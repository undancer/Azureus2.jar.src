/*     */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileChannel.MapMode;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FMFileAccessLinear
/*     */   implements FMFileAccess
/*     */ {
/*     */   private static final int WRITE_RETRY_LIMIT = 10;
/*     */   private static final int WRITE_RETRY_DELAY = 100;
/*     */   private static final int READ_RETRY_LIMIT = 10;
/*     */   private static final int READ_RETRY_DELAY = 100;
/*     */   private static final boolean DEBUG = true;
/*     */   private static final boolean DEBUG_VERBOSE = false;
/*  52 */   private static final boolean USE_MMAP = System.getProperty("azureus.io.usemmap", "false") == "true";
/*     */   
/*     */ 
/*     */   private final FMFileImpl owner;
/*     */   
/*     */ 
/*     */   protected FMFileAccessLinear(FMFileImpl _owner)
/*     */   {
/*  60 */     this.owner = _owner;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void aboutToOpen()
/*     */     throws FMFileManagerException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public long getLength(RandomAccessFile raf)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/*  77 */       AEThread2.setDebug(this.owner);
/*     */       
/*  79 */       return raf.length();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  83 */       throw new FMFileManagerException("getLength fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLength(RandomAccessFile raf, long length)
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/*  95 */       AEThread2.setDebug(this.owner);
/*     */       try
/*     */       {
/*  98 */         raf.setLength(length);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 102 */         if (Constants.isAndroid)
/*     */         {
/*     */ 
/*     */ 
/* 106 */           if (!Debug.getNestedExceptionMessage(e).toUpperCase(Locale.US).contains("EINVAL"))
/*     */           {
/*     */ 
/*     */ 
/* 110 */             throw e;
/*     */           }
/*     */           
/* 113 */           long required = length - raf.length();
/*     */           
/* 115 */           if (required > 0L)
/*     */           {
/* 117 */             if (FileUtil.getUsableSpaceSupported())
/*     */             {
/* 119 */               long usable = FileUtil.getUsableSpace(this.owner.getLinkedFile().getParentFile());
/*     */               
/*     */ 
/*     */ 
/* 123 */               if ((usable >= 0L) && (usable < required))
/*     */               {
/*     */ 
/*     */ 
/* 127 */                 throw e;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 132 */           if (required > 0L)
/*     */           {
/* 134 */             long old_pos = raf.getFilePointer();
/*     */             try
/*     */             {
/* 137 */               raf.seek(length - 1L);
/*     */               
/* 139 */               raf.write(0);
/*     */             }
/*     */             catch (IOException f)
/*     */             {
/* 143 */               throw e;
/*     */             }
/*     */             finally
/*     */             {
/*     */               try {
/* 148 */                 raf.seek(old_pos);
/*     */               }
/*     */               catch (Throwable f) {}
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */         throw e;
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 165 */       throw new FMFileManagerException("setLength fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPieceCompleteProcessingNeeded(int piece_number)
/*     */   {
/* 173 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieceComplete(RandomAccessFile raf, int piece_number, DirectByteBuffer piece_data)
/*     */     throws FMFileManagerException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(RandomAccessFile raf, DirectByteBuffer buffer, long offset)
/*     */     throws FMFileManagerException
/*     */   {
/* 194 */     if (raf == null)
/*     */     {
/* 196 */       throw new FMFileManagerException("read: raf is null");
/*     */     }
/*     */     
/* 199 */     FileChannel fc = raf.getChannel();
/*     */     
/* 201 */     if (!fc.isOpen())
/*     */     {
/* 203 */       Debug.out("FileChannel is closed: " + this.owner.getName());
/*     */       
/* 205 */       throw new FMFileManagerException("read - file is closed");
/*     */     }
/*     */     
/* 208 */     AEThread2.setDebug(this.owner);
/*     */     try
/*     */     {
/* 211 */       if (USE_MMAP)
/*     */       {
/* 213 */         long remainingInFile = fc.size() - offset;
/* 214 */         long remainingInTargetBuffer = buffer.remaining((byte)4);
/* 215 */         MappedByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, offset, Math.min(remainingInFile, remainingInTargetBuffer));
/* 216 */         buffer.put((byte)4, buf);
/*     */       } else {
/* 218 */         fc.position(offset);
/* 219 */         while ((fc.position() < fc.size()) && (buffer.hasRemaining((byte)4))) {
/* 220 */           buffer.read((byte)4, fc);
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 227 */       Debug.printStackTrace(e);
/*     */       
/* 229 */       throw new FMFileManagerException("read fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(RandomAccessFile raf, DirectByteBuffer[] buffers, long offset)
/*     */     throws FMFileManagerException
/*     */   {
/* 241 */     if (raf == null)
/*     */     {
/* 243 */       throw new FMFileManagerException("read: raf is null");
/*     */     }
/*     */     
/* 246 */     FileChannel fc = raf.getChannel();
/*     */     
/* 248 */     if (!fc.isOpen())
/*     */     {
/* 250 */       Debug.out("FileChannel is closed: " + this.owner.getName());
/*     */       
/* 252 */       throw new FMFileManagerException("read - file is closed");
/*     */     }
/*     */     
/* 255 */     AEThread2.setDebug(this.owner);
/*     */     
/* 257 */     int[] original_positions = new int[buffers.length];
/*     */     
/* 259 */     long read_start = SystemTime.getHighPrecisionCounter();
/*     */     try
/*     */     {
/* 262 */       if (USE_MMAP)
/*     */       {
/*     */ 
/* 265 */         long size = 0L;
/* 266 */         for (int i = 0; i < buffers.length; i++)
/*     */         {
/* 268 */           size += buffers[i].remaining((byte)4);
/* 269 */           original_positions[i] = buffers[i].position(4);
/*     */         }
/*     */         
/* 272 */         size = Math.min(size, fc.size() - offset);
/* 273 */         MappedByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, offset, size);
/* 274 */         for (DirectByteBuffer b : buffers)
/*     */         {
/* 276 */           buf.limit(buf.position() + b.remaining((byte)4));
/* 277 */           b.put((byte)4, buf);
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 283 */         fc.position(offset);
/* 284 */         ByteBuffer[] bbs = new ByteBuffer[buffers.length];
/*     */         
/* 286 */         ByteBuffer last_bb = null;
/* 287 */         for (int i = 0; i < bbs.length; i++) {
/* 288 */           ByteBuffer bb = bbs[i] = buffers[i].getBuffer((byte)4);
/* 289 */           int pos = original_positions[i] = bb.position();
/* 290 */           if (pos != bb.limit()) {
/* 291 */             last_bb = bbs[i];
/*     */           }
/*     */         }
/*     */         
/* 295 */         if (last_bb != null) {
/* 296 */           int loop = 0;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 307 */           if (Constants.isAndroid)
/*     */           {
/* 309 */             int bbs_index = 0;
/*     */             
/* 311 */             while ((fc.position() < fc.size()) && (last_bb.hasRemaining()))
/*     */             {
/* 313 */               ByteBuffer current_bb = bbs[bbs_index];
/*     */               
/* 315 */               if (!current_bb.hasRemaining())
/*     */               {
/* 317 */                 bbs_index++;
/*     */               }
/*     */               else
/*     */               {
/* 321 */                 long read = fc.read(current_bb);
/*     */                 
/* 323 */                 if (read > 0L)
/*     */                 {
/* 325 */                   loop = 0;
/*     */                 }
/*     */                 else
/*     */                 {
/* 329 */                   loop++;
/*     */                   
/* 331 */                   if (loop == 10) {
/* 332 */                     Debug.out("FMFile::read: zero length read - abandoning");
/* 333 */                     throw new FMFileManagerException("read fails: retry limit exceeded");
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */                   try
/*     */                   {
/* 340 */                     Thread.sleep(100 * loop);
/*     */                   } catch (InterruptedException e) {
/* 342 */                     throw new FMFileManagerException("read fails: interrupted");
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/* 349 */             while ((fc.position() < fc.size()) && (last_bb.hasRemaining())) {
/* 350 */               long read = fc.read(bbs);
/* 351 */               if (read > 0L) {
/* 352 */                 loop = 0;
/*     */               } else {
/* 354 */                 loop++;
/* 355 */                 if (loop == 10) {
/* 356 */                   Debug.out("FMFile::read: zero length read - abandoning");
/* 357 */                   throw new FMFileManagerException("read fails: retry limit exceeded");
/*     */                 }
/*     */                 
/*     */ 
/*     */                 try
/*     */                 {
/* 363 */                   Thread.sleep(100 * loop);
/*     */                 } catch (InterruptedException e) {
/* 365 */                   throw new FMFileManagerException("read fails: interrupted");
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/*     */       try {
/*     */         long elapsed_millis;
/* 376 */         Debug.out("Read failed: " + this.owner.getString() + ": raf open=" + raf.getChannel().isOpen() + ", len=" + raf.length() + ",off=" + offset);
/*     */       }
/*     */       catch (IOException f) {}
/*     */       
/*     */ 
/* 381 */       Debug.printStackTrace(e);
/*     */       
/* 383 */       if (original_positions != null) {
/*     */         try
/*     */         {
/* 386 */           for (int i = 0; i < original_positions.length; i++)
/*     */           {
/* 388 */             buffers[i].position((byte)4, original_positions[i]);
/*     */           }
/*     */         }
/*     */         catch (Throwable e2)
/*     */         {
/* 393 */           Debug.out(e2);
/*     */         }
/*     */       }
/*     */       
/* 397 */       throw new FMFileManagerException("read fails", e);
/*     */     }
/*     */     finally
/*     */     {
/* 401 */       long elapsed_millis = (SystemTime.getHighPrecisionCounter() - read_start) / 1000000L;
/*     */       
/* 403 */       if (elapsed_millis > 10000L)
/*     */       {
/* 405 */         System.out.println("read took " + elapsed_millis + " for " + this.owner.getString());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(RandomAccessFile raf, DirectByteBuffer[] buffers, long position)
/*     */     throws FMFileManagerException
/*     */   {
/* 418 */     if (raf == null) {
/* 419 */       throw new FMFileManagerException("write fails: raf is null");
/*     */     }
/*     */     
/* 422 */     FileChannel fc = raf.getChannel();
/*     */     
/* 424 */     if (!fc.isOpen())
/*     */     {
/* 426 */       Debug.out("FileChannel is closed: " + this.owner.getName());
/*     */       
/* 428 */       throw new FMFileManagerException("read - file is closed");
/*     */     }
/*     */     
/* 431 */     AEThread2.setDebug(this.owner);
/*     */     
/* 433 */     int[] original_positions = new int[buffers.length];
/*     */     
/*     */     try
/*     */     {
/* 437 */       if (USE_MMAP) {
/* 438 */         long size = 0L;
/* 439 */         for (int i = 0; i < buffers.length; i++)
/*     */         {
/* 441 */           size += buffers[i].remaining((byte)4);
/* 442 */           original_positions[i] = buffers[i].position(4);
/*     */         }
/*     */         
/* 445 */         if (position + size > fc.size())
/*     */         {
/* 447 */           fc.position(position + size - 1L);
/* 448 */           fc.write(ByteBuffer.allocate(1));
/* 449 */           fc.force(true);
/*     */         }
/*     */         
/* 452 */         MappedByteBuffer buf = fc.map(FileChannel.MapMode.READ_WRITE, position, size);
/* 453 */         for (DirectByteBuffer b : buffers)
/* 454 */           buf.put(b.getBuffer((byte)4));
/* 455 */         buf.force();
/*     */       } else {
/* 457 */         long expected_write = 0L;
/* 458 */         long actual_write = 0L;
/* 459 */         boolean partial_write = false;
/*     */         
/*     */ 
/* 462 */         for (int i = 0; i < buffers.length; i++) {
/* 463 */           expected_write += buffers[i].limit((byte)4) - buffers[i].position((byte)4);
/*     */         }
/*     */         
/*     */ 
/* 467 */         fc.position(position);
/* 468 */         ByteBuffer[] bbs = new ByteBuffer[buffers.length];
/*     */         
/*     */ 
/* 471 */         ByteBuffer last_bb = null;
/* 472 */         for (int i = 0; i < bbs.length; i++) {
/* 473 */           ByteBuffer bb = bbs[i] = buffers[i].getBuffer((byte)4);
/* 474 */           int pos = original_positions[i] = bb.position();
/* 475 */           if (pos != bb.limit()) {
/* 476 */             last_bb = bbs[i];
/*     */           }
/*     */         }
/*     */         
/* 480 */         if (last_bb != null) {
/* 481 */           int loop = 0;
/*     */           
/* 483 */           while (last_bb.position() != last_bb.limit()) {
/* 484 */             long written = fc.write(bbs);
/* 485 */             actual_write += written;
/*     */             
/* 487 */             if (written > 0L) {
/* 488 */               loop = 0;
/*     */               
/* 490 */               if (last_bb.position() != last_bb.limit()) {
/* 491 */                 partial_write = true;
/*     */               }
/*     */               
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 499 */               loop++;
/* 500 */               if (loop == 10) {
/* 501 */                 Debug.out("FMFile::write: zero length write - abandoning");
/* 502 */                 throw new FMFileManagerException("write fails: retry limit exceeded");
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               try
/*     */               {
/* 509 */                 Thread.sleep(100 * loop);
/*     */               } catch (InterruptedException e) {
/* 511 */                 throw new FMFileManagerException("write fails: interrupted");
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 518 */         if (expected_write != actual_write) {
/* 519 */           Debug.out("FMFile::write: **** partial write **** failed: expected = " + expected_write + ", actual = " + actual_write);
/* 520 */           throw new FMFileManagerException("write fails: expected write/actual write mismatch");
/*     */         }
/* 522 */         if (!partial_write) {}
/*     */       }
/*     */       
/*     */       int i;
/*     */       
/*     */       return;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 531 */       if (original_positions != null) {
/*     */         try
/*     */         {
/* 534 */           for (i = 0; i < original_positions.length; i++)
/*     */           {
/* 536 */             buffers[i].position((byte)4, original_positions[i]);
/*     */           }
/*     */         }
/*     */         catch (Throwable e2)
/*     */         {
/* 541 */           Debug.out(e2);
/*     */         }
/*     */       }
/*     */       
/* 545 */       throw new FMFileManagerException("write fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void flush()
/*     */     throws FMFileManagerException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public FMFileImpl getFile()
/*     */   {
/* 560 */     return this.owner;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 566 */     return "linear";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileAccessLinear.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */