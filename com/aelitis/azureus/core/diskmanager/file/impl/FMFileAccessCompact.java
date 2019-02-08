/*     */ package com.aelitis.azureus.core.diskmanager.file.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import java.io.File;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FMFileAccessCompact
/*     */   implements FMFileAccess
/*     */ {
/*     */   private static final byte SS = 4;
/*     */   private final TOTorrentFile torrent_file;
/*     */   private int piece_size;
/*     */   private final File controlFileDir;
/*     */   private final String controlFileName;
/*     */   private final FMFileAccess delegate;
/*     */   private volatile long current_length;
/*     */   private static final long version = 0L;
/*     */   private volatile boolean write_required;
/*     */   private long first_piece_start;
/*     */   private long first_piece_length;
/*     */   private long last_piece_start;
/*     */   private long last_piece_length;
/*     */   
/*     */   protected FMFileAccessCompact(TOTorrentFile _torrent_file, File _controlFileDir, String _controlFileName, FMFileAccess _delegate)
/*     */     throws FMFileManagerException
/*     */   {
/*  66 */     this.torrent_file = _torrent_file;
/*  67 */     this.controlFileDir = _controlFileDir;
/*  68 */     this.controlFileName = _controlFileName;
/*  69 */     this.delegate = _delegate;
/*     */     try
/*     */     {
/*  72 */       this.piece_size = ((int)this.torrent_file.getTorrent().getPieceLength());
/*     */       
/*  74 */       TOTorrent torrent = this.torrent_file.getTorrent();
/*     */       
/*  76 */       long file_length = this.torrent_file.getLength();
/*     */       
/*  78 */       long file_offset_in_torrent = 0L;
/*     */       
/*  80 */       for (int i = 0; i < torrent.getFiles().length; i++)
/*     */       {
/*  82 */         TOTorrentFile f = torrent.getFiles()[i];
/*     */         
/*  84 */         if (f == this.torrent_file) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*  89 */         file_offset_in_torrent += f.getLength();
/*     */       }
/*     */       
/*  92 */       int piece_offset = this.piece_size - (int)(file_offset_in_torrent % this.piece_size);
/*     */       
/*  94 */       if (piece_offset == this.piece_size)
/*     */       {
/*  96 */         piece_offset = 0;
/*     */       }
/*     */       
/*  99 */       this.first_piece_length = piece_offset;
/* 100 */       this.first_piece_start = 0L;
/*     */       
/* 102 */       if (this.first_piece_length >= file_length)
/*     */       {
/*     */ 
/*     */ 
/* 106 */         this.first_piece_length = file_length;
/* 107 */         this.last_piece_start = file_length;
/* 108 */         this.last_piece_length = 0L;
/*     */       }
/*     */       else
/*     */       {
/* 112 */         this.last_piece_length = ((file_length - piece_offset) % this.piece_size);
/* 113 */         this.last_piece_start = (file_length - this.last_piece_length);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */       if (!new File(this.controlFileDir, this.controlFileName).exists())
/*     */       {
/* 125 */         if ((!this.controlFileDir.isDirectory()) && (!FileUtil.mkdirs(this.controlFileDir))) {
/* 126 */           throw new FMFileManagerException("Directory creation failed: " + this.controlFileDir);
/*     */         }
/*     */         
/*     */       }
/*     */       else {
/* 131 */         readState();
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 135 */       throw new FMFileManagerException("Compact file init fail", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getFirstPieceStart()
/*     */   {
/* 142 */     return this.first_piece_start;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getFirstPieceLength()
/*     */   {
/* 148 */     return this.first_piece_length;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLastPieceStart()
/*     */   {
/* 154 */     return this.last_piece_start;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLastPieceLength()
/*     */   {
/* 160 */     return this.last_piece_length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void aboutToOpen()
/*     */     throws FMFileManagerException
/*     */   {
/* 168 */     this.delegate.aboutToOpen();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLength(RandomAccessFile raf)
/*     */     throws FMFileManagerException
/*     */   {
/* 177 */     return this.current_length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLength(RandomAccessFile raf, long length)
/*     */     throws FMFileManagerException
/*     */   {
/* 187 */     if (length != this.current_length)
/*     */     {
/* 189 */       this.current_length = length;
/*     */       
/* 191 */       this.write_required = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void read(RandomAccessFile raf, DirectByteBuffer buffer, long position)
/*     */     throws FMFileManagerException
/*     */   {
/* 203 */     int original_limit = buffer.limit((byte)4);
/*     */     try
/*     */     {
/* 206 */       int len = original_limit - buffer.position((byte)4);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 212 */       if (position < this.first_piece_start + this.first_piece_length)
/*     */       {
/* 214 */         int available = (int)(this.first_piece_start + this.first_piece_length - position);
/*     */         
/* 216 */         if (available >= len)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */           this.delegate.read(raf, new DirectByteBuffer[] { buffer }, position);
/*     */           
/* 224 */           position += len;
/* 225 */           len = 0;
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 232 */           buffer.limit((byte)4, buffer.position((byte)4) + available);
/*     */           
/* 234 */           this.delegate.read(raf, new DirectByteBuffer[] { buffer }, position);
/*     */           
/* 236 */           buffer.limit((byte)4, original_limit);
/*     */           
/* 238 */           position += available;
/* 239 */           len -= available;
/*     */         }
/*     */       }
/*     */       
/* 243 */       if (len == 0) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 251 */       long space = this.last_piece_start - position;
/*     */       
/* 253 */       if (space > 0L)
/*     */       {
/* 255 */         if (space >= len)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 261 */           buffer.position((byte)4, original_limit);
/*     */           
/* 263 */           position += len;
/* 264 */           len = 0;
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 271 */           buffer.position((byte)4, buffer.position((byte)4) + (int)space);
/*     */           
/* 273 */           position += space;
/* 274 */           len = (int)(len - space);
/*     */         }
/*     */       }
/*     */       
/* 278 */       if (len == 0) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 287 */       this.delegate.read(raf, new DirectByteBuffer[] { buffer }, position - this.last_piece_start + this.first_piece_length);
/*     */     }
/*     */     finally
/*     */     {
/* 291 */       buffer.limit((byte)4, original_limit);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(RandomAccessFile raf, DirectByteBuffer[] buffers, long position)
/*     */     throws FMFileManagerException
/*     */   {
/* 303 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 305 */       DirectByteBuffer buffer = buffers[i];
/*     */       
/* 307 */       int len = buffers[i].limit((byte)4) - buffers[i].position((byte)4);
/*     */       
/* 309 */       read(raf, buffer, position);
/*     */       
/* 311 */       int rem = buffers[i].remaining((byte)4);
/*     */       
/* 313 */       position += len - rem;
/*     */       
/* 315 */       if (rem > 0) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 321 */     if (position > this.current_length)
/*     */     {
/* 323 */       setLength(raf, position);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void write(RandomAccessFile raf, DirectByteBuffer buffer, long position)
/*     */     throws FMFileManagerException
/*     */   {
/* 335 */     int original_limit = buffer.limit((byte)4);
/*     */     try
/*     */     {
/* 338 */       int len = original_limit - buffer.position((byte)4);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 344 */       if (position < this.first_piece_start + this.first_piece_length)
/*     */       {
/* 346 */         int available = (int)(this.first_piece_start + this.first_piece_length - position);
/*     */         
/* 348 */         if (available >= len)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 354 */           this.delegate.write(raf, new DirectByteBuffer[] { buffer }, position);
/*     */           
/* 356 */           position += len;
/* 357 */           len = 0;
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 364 */           buffer.limit((byte)4, buffer.position((byte)4) + available);
/*     */           
/* 366 */           this.delegate.write(raf, new DirectByteBuffer[] { buffer }, position);
/*     */           
/* 368 */           buffer.limit((byte)4, original_limit);
/*     */           
/* 370 */           position += available;
/* 371 */           len -= available;
/*     */         }
/*     */       }
/*     */       
/* 375 */       if (len == 0) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 383 */       long space = this.last_piece_start - position;
/*     */       
/* 385 */       if (space > 0L)
/*     */       {
/* 387 */         if (space >= len)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 393 */           buffer.position((byte)4, original_limit);
/*     */           
/* 395 */           position += len;
/* 396 */           len = 0;
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 403 */           buffer.position((byte)4, buffer.position((byte)4) + (int)space);
/*     */           
/* 405 */           position += space;
/* 406 */           len = (int)(len - space);
/*     */         }
/*     */       }
/*     */       
/* 410 */       if (len == 0) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 419 */       this.delegate.write(raf, new DirectByteBuffer[] { buffer }, position - this.last_piece_start + this.first_piece_length);
/*     */     }
/*     */     finally
/*     */     {
/* 423 */       buffer.limit((byte)4, original_limit);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(RandomAccessFile raf, DirectByteBuffer[] buffers, long position)
/*     */     throws FMFileManagerException
/*     */   {
/* 436 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 438 */       DirectByteBuffer buffer = buffers[i];
/*     */       
/* 440 */       int len = buffers[i].limit((byte)4) - buffers[i].position((byte)4);
/*     */       
/* 442 */       write(raf, buffer, position);
/*     */       
/* 444 */       position += len;
/*     */     }
/*     */     
/* 447 */     if (position > this.current_length)
/*     */     {
/* 449 */       setLength(raf, position);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void flush()
/*     */     throws FMFileManagerException
/*     */   {
/* 458 */     writeState();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPieceCompleteProcessingNeeded(int piece_number)
/*     */   {
/* 465 */     return false;
/*     */   }
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
/*     */   protected void readState()
/*     */     throws FMFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 484 */       Map data = FileUtil.readResilientFile(this.controlFileDir, this.controlFileName, false);
/*     */       
/*     */ 
/* 487 */       if ((data != null) && (data.size() > 0))
/*     */       {
/* 489 */         Long version = (Long)data.get("version");
/*     */         
/* 491 */         Long length = (Long)data.get("length");
/*     */         
/* 493 */         this.current_length = length.longValue();
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 497 */       throw new FMFileManagerException("Failed to read control file state", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeState()
/*     */     throws FMFileManagerException
/*     */   {
/* 506 */     boolean write = this.write_required;
/*     */     
/* 508 */     if (write)
/*     */     {
/* 510 */       this.write_required = false;
/*     */       try
/*     */       {
/* 513 */         Map data = new HashMap();
/*     */         
/* 515 */         data.put("version", new Long(0L));
/*     */         
/* 517 */         data.put("length", new Long(this.current_length));
/*     */         
/* 519 */         FileUtil.writeResilientFile(this.controlFileDir, this.controlFileName, data, false);
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 524 */         throw new FMFileManagerException("Failed to write control file state", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public FMFileImpl getFile()
/*     */   {
/* 532 */     return this.delegate.getFile();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 538 */     return "compact";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileAccessCompact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */