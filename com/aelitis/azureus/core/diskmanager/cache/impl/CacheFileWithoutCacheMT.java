/*     */ package com.aelitis.azureus.core.diskmanager.cache.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFile;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheFileWithoutCacheMT
/*     */   implements CacheFile
/*     */ {
/*     */   private static final int MAX_CLONES = 20;
/*     */   private static int num_clones;
/*     */   private static int max_clone_depth;
/*     */   private final CacheFileManagerImpl manager;
/*     */   private final FMFile base_file;
/*     */   private FMFile[] files;
/*     */   private int[] files_use_count;
/*     */   private final TOTorrentFile torrent_file;
/*     */   private boolean moving;
/*     */   private long bytes_written;
/*     */   private long bytes_read;
/*     */   
/*     */   protected CacheFileWithoutCacheMT(CacheFileManagerImpl _manager, FMFile _file, TOTorrentFile _torrent_file)
/*     */   {
/*  58 */     this.manager = _manager;
/*  59 */     this.base_file = _file;
/*  60 */     this.torrent_file = _torrent_file;
/*     */     
/*  62 */     this.files = new FMFile[] { this.base_file };
/*     */     
/*  64 */     this.files_use_count = new int[] { 0 };
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentFile getTorrentFile()
/*     */   {
/*  70 */     return this.torrent_file;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean exists()
/*     */   {
/*  76 */     return this.base_file.exists();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void moveFile(File new_file)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/*  86 */       synchronized (this)
/*     */       {
/*  88 */         this.moving = true;
/*     */       }
/*     */       
/*     */       for (;;)
/*     */       {
/*  93 */         synchronized (this)
/*     */         {
/*  95 */           boolean surviving = false;
/*     */           
/*  97 */           int i = 1; if (i < this.files_use_count.length)
/*     */           {
/*  99 */             if (this.files_use_count[i] > 0)
/*     */             {
/* 101 */               surviving = true;
/*     */             }
/*     */             else
/*     */             {
/*  97 */               i++; continue;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 107 */           if (!surviving)
/*     */           {
/* 109 */             int i = 1; if (i < this.files_use_count.length)
/*     */             {
/* 111 */               FMFile file = this.files[i];
/*     */               
/* 113 */               if (file.isClone())
/*     */               {
/*     */ 
/*     */ 
/* 117 */                 synchronized (CacheFileWithoutCacheMT.class)
/*     */                 {
/* 119 */                   num_clones -= 1;
/*     */                 }
/*     */               }
/*     */               
/* 123 */               file.close();i++; continue;
/*     */             }
/*     */             
/* 126 */             this.files = new FMFile[] { this.base_file };
/*     */             
/* 128 */             this.files_use_count = new int[] { this.files_use_count[0] };
/*     */             
/* 130 */             this.base_file.moveFile(new_file);
/*     */             
/* 132 */             break;
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 137 */           System.out.println("CacheFileWithoutCacheMT: waiting for clones to die");
/*     */           
/* 139 */           Thread.sleep(250L);
/*     */ 
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 148 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally
/*     */     {
/* 152 */       synchronized (this)
/*     */       {
/* 154 */         this.moving = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void renameFile(String new_file)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 166 */       synchronized (this)
/*     */       {
/* 168 */         this.moving = true;
/*     */       }
/*     */       
/*     */       for (;;)
/*     */       {
/* 173 */         synchronized (this)
/*     */         {
/* 175 */           boolean surviving = false;
/*     */           
/* 177 */           int i = 1; if (i < this.files_use_count.length)
/*     */           {
/* 179 */             if (this.files_use_count[i] > 0)
/*     */             {
/* 181 */               surviving = true;
/*     */             }
/*     */             else
/*     */             {
/* 177 */               i++; continue;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 187 */           if (!surviving)
/*     */           {
/* 189 */             int i = 1; if (i < this.files_use_count.length)
/*     */             {
/* 191 */               FMFile file = this.files[i];
/*     */               
/* 193 */               if (file.isClone())
/*     */               {
/*     */ 
/*     */ 
/* 197 */                 synchronized (CacheFileWithoutCacheMT.class)
/*     */                 {
/* 199 */                   num_clones -= 1;
/*     */                 }
/*     */               }
/*     */               
/* 203 */               file.close();i++; continue;
/*     */             }
/*     */             
/* 206 */             this.files = new FMFile[] { this.base_file };
/*     */             
/* 208 */             this.files_use_count = new int[] { this.files_use_count[0] };
/*     */             
/* 210 */             this.base_file.renameFile(new_file);
/*     */             
/* 212 */             break;
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 217 */           System.out.println("CacheFileWithoutCacheMT: waiting for clones to die");
/*     */           
/* 219 */           Thread.sleep(250L);
/*     */ 
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 228 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally
/*     */     {
/* 232 */       synchronized (this)
/*     */       {
/* 234 */         this.moving = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAccessMode(int mode)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 248 */       synchronized (this)
/*     */       {
/* 250 */         for (int i = 0; i < this.files.length; i++)
/*     */         {
/* 252 */           this.files[i].setAccessMode(mode == 1 ? 1 : 2);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (FMFileManagerException e) {
/* 257 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 264 */     return this.base_file.getAccessMode() == 1 ? 1 : 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStorageType(int type)
/*     */     throws CacheFileManagerException
/*     */   {
/* 273 */     throw new CacheFileManagerException(this, "Not Implemented");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStorageType()
/*     */   {
/* 279 */     return CacheFileManagerImpl.convertFileToCacheType(this.base_file.getStorageType());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getLength()
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 289 */       return this.base_file.exists() ? this.base_file.getLength() : 0L;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 293 */       this.manager.rethrow(this, e);
/*     */     }
/* 295 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long compareLength(long compare_to)
/*     */     throws CacheFileManagerException
/*     */   {
/* 305 */     return getLength() - compare_to;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLength(long length)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 315 */       this.base_file.setLength(length);
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 319 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieceComplete(int piece_number, DirectByteBuffer piece_data)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 331 */       this.base_file.setPieceComplete(piece_number, piece_data);
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 335 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected FMFile getFile()
/*     */     throws CacheFileManagerException
/*     */   {
/* 344 */     synchronized (this)
/*     */     {
/* 346 */       if (this.moving)
/*     */       {
/* 348 */         this.files_use_count[0] += 1;
/*     */         
/* 350 */         return this.files[0];
/*     */       }
/*     */       
/* 353 */       int min_index = -1;
/* 354 */       int min = Integer.MAX_VALUE;
/*     */       
/* 356 */       for (int i = 0; i < this.files_use_count.length; i++)
/*     */       {
/* 358 */         int count = this.files_use_count[i];
/*     */         
/* 360 */         if (count < min)
/*     */         {
/* 362 */           min = count;
/* 363 */           min_index = i;
/*     */         }
/*     */       }
/*     */       
/* 367 */       if ((min == 0) || (this.files_use_count.length == 20))
/*     */       {
/* 369 */         this.files_use_count[min_index] += 1;
/*     */         
/* 371 */         return this.files[min_index];
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 377 */         FMFile clone = this.base_file.createClone();
/*     */         
/*     */ 
/*     */ 
/* 381 */         int old_num = this.files.length;
/* 382 */         int new_num = old_num + 1;
/*     */         
/* 384 */         synchronized (CacheFileWithoutCacheMT.class)
/*     */         {
/* 386 */           num_clones += 1;
/*     */           
/* 388 */           if ((num_clones % 100 != 0) || (
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 393 */             (new_num == 20) || (new_num > max_clone_depth)))
/*     */           {
/* 395 */             max_clone_depth = new_num;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 401 */         FMFile[] new_files = new FMFile[new_num];
/* 402 */         int[] new_files_use_count = new int[new_num];
/*     */         
/* 404 */         System.arraycopy(this.files, 0, new_files, 0, old_num);
/* 405 */         System.arraycopy(this.files_use_count, 0, new_files_use_count, 0, old_num);
/*     */         
/* 407 */         new_files[old_num] = clone;
/* 408 */         new_files_use_count[old_num] = 1;
/*     */         
/* 410 */         this.files = new_files;
/* 411 */         this.files_use_count = new_files_use_count;
/*     */         
/* 413 */         return clone;
/*     */       }
/*     */       catch (FMFileManagerException e)
/*     */       {
/* 417 */         this.manager.rethrow(this, e);
/*     */         
/* 419 */         return null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void releaseFile(FMFile file)
/*     */   {
/* 428 */     synchronized (this)
/*     */     {
/* 430 */       for (int i = 0; i < this.files_use_count.length; i++)
/*     */       {
/* 432 */         if (this.files[i] == file)
/*     */         {
/* 434 */           int count = this.files_use_count[i];
/*     */           
/* 436 */           if (count > 0)
/*     */           {
/* 438 */             count--;
/*     */           }
/*     */           
/* 441 */           this.files_use_count[i] = count;
/*     */           
/* 443 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(DirectByteBuffer[] buffers, long position, short policy)
/*     */     throws CacheFileManagerException
/*     */   {
/* 457 */     int read_length = 0;
/*     */     
/* 459 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 461 */       read_length += buffers[i].remaining((byte)3);
/*     */     }
/*     */     
/* 464 */     FMFile file = null;
/*     */     try
/*     */     {
/* 467 */       file = getFile();
/*     */       
/* 469 */       file.read(buffers, position);
/*     */       
/* 471 */       this.manager.fileBytesRead(read_length);
/*     */       
/* 473 */       this.bytes_read += read_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 477 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally
/*     */     {
/* 481 */       releaseFile(file);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(DirectByteBuffer buffer, long position, short policy)
/*     */     throws CacheFileManagerException
/*     */   {
/* 493 */     int read_length = buffer.remaining((byte)3);
/*     */     
/* 495 */     FMFile file = null;
/*     */     try
/*     */     {
/* 498 */       file = getFile();
/*     */       
/* 500 */       file.read(buffer, position);
/*     */       
/* 502 */       this.manager.fileBytesRead(read_length);
/*     */       
/* 504 */       this.bytes_read += read_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 508 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally
/*     */     {
/* 512 */       releaseFile(file);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(DirectByteBuffer buffer, long position)
/*     */     throws CacheFileManagerException
/*     */   {
/* 523 */     int write_length = buffer.remaining((byte)3);
/*     */     try
/*     */     {
/* 526 */       this.base_file.write(buffer, position);
/*     */       
/* 528 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 530 */       this.bytes_written += write_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 534 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(DirectByteBuffer[] buffers, long position)
/*     */     throws CacheFileManagerException
/*     */   {
/* 545 */     int write_length = 0;
/*     */     
/* 547 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 549 */       write_length += buffers[i].remaining((byte)3);
/*     */     }
/*     */     try
/*     */     {
/* 553 */       this.base_file.write(buffers, position);
/*     */       
/* 555 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 557 */       this.bytes_written += write_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 561 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeAndHandoverBuffer(DirectByteBuffer buffer, long position)
/*     */     throws CacheFileManagerException
/*     */   {
/* 572 */     int write_length = buffer.remaining((byte)3);
/*     */     
/* 574 */     boolean write_ok = false;
/*     */     try
/*     */     {
/* 577 */       this.base_file.write(buffer, position);
/*     */       
/* 579 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 581 */       this.bytes_written += write_length;
/*     */       
/* 583 */       write_ok = true;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 587 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally
/*     */     {
/* 591 */       if (write_ok)
/*     */       {
/* 593 */         buffer.returnToPool();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeAndHandoverBuffers(DirectByteBuffer[] buffers, long position)
/*     */     throws CacheFileManagerException
/*     */   {
/* 605 */     int write_length = 0;
/*     */     
/* 607 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 609 */       write_length += buffers[i].remaining((byte)3);
/*     */     }
/*     */     
/* 612 */     boolean write_ok = false;
/*     */     try
/*     */     {
/* 615 */       this.base_file.write(buffers, position);
/*     */       
/* 617 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 619 */       this.bytes_written += write_length;
/*     */       
/* 621 */       write_ok = true;
/*     */     }
/*     */     catch (FMFileManagerException e) {
/*     */       int i;
/* 625 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally {
/*     */       int i;
/* 629 */       if (write_ok)
/*     */       {
/* 631 */         for (int i = 0; i < buffers.length; i++)
/*     */         {
/* 633 */           buffers[i].returnToPool();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void flushCache()
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 645 */       this.base_file.flush();
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 649 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearCache()
/*     */     throws CacheFileManagerException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 666 */       synchronized (this)
/*     */       {
/* 668 */         for (int i = 0; i < this.files.length; i++)
/*     */         {
/* 670 */           FMFile file = this.files[i];
/*     */           
/* 672 */           if (file.isClone())
/*     */           {
/*     */ 
/*     */ 
/* 676 */             synchronized (CacheFileWithoutCacheMT.class)
/*     */             {
/* 678 */               num_clones -= 1;
/*     */             }
/*     */           }
/*     */           
/* 682 */           file.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (FMFileManagerException e) {
/* 687 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 694 */     return this.base_file.isOpen();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSessionBytesRead()
/*     */   {
/* 700 */     return this.bytes_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSessionBytesWritten()
/*     */   {
/* 706 */     return this.bytes_written;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void delete()
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 716 */       this.base_file.delete();
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 720 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/impl/CacheFileWithoutCacheMT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */