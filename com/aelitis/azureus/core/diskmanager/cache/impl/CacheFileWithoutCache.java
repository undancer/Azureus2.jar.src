/*     */ package com.aelitis.azureus.core.diskmanager.cache.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFile;
/*     */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*     */ import java.io.File;
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
/*     */ 
/*     */ 
/*     */ public class CacheFileWithoutCache
/*     */   implements CacheFile
/*     */ {
/*     */   protected final CacheFileManagerImpl manager;
/*     */   protected final FMFile file;
/*     */   protected final TOTorrentFile torrent_file;
/*     */   private long bytes_written;
/*     */   private long bytes_read;
/*     */   
/*     */   protected CacheFileWithoutCache(CacheFileManagerImpl _manager, FMFile _file, TOTorrentFile _torrent_file)
/*     */   {
/*  53 */     this.manager = _manager;
/*  54 */     this.file = _file;
/*  55 */     this.torrent_file = _torrent_file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TOTorrentFile getTorrentFile()
/*     */   {
/*  62 */     return this.torrent_file;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean exists()
/*     */   {
/*  68 */     return this.file.exists();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void moveFile(File new_file)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/*  78 */       this.file.moveFile(new_file);
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/*  82 */       this.manager.rethrow(this, e);
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
/*  93 */       this.file.renameFile(new_file);
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/*  97 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAccessMode(int mode)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 109 */       this.file.setAccessMode(mode == 1 ? 1 : 2);
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 113 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 120 */     return this.file.getAccessMode() == 1 ? 1 : 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStorageType(int type)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 131 */       this.file.setStorageType(CacheFileManagerImpl.convertCacheToFileType(type));
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 135 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStorageType()
/*     */   {
/* 142 */     return CacheFileManagerImpl.convertFileToCacheType(this.file.getStorageType());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getLength()
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 152 */       return this.file.exists() ? this.file.getLength() : 0L;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 156 */       this.manager.rethrow(this, e);
/*     */     }
/* 158 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long compareLength(long compare_to)
/*     */     throws CacheFileManagerException
/*     */   {
/* 168 */     return getLength() - compare_to;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLength(long length)
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 179 */       this.file.setLength(length);
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 183 */       this.manager.rethrow(this, e);
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
/* 195 */       this.file.setPieceComplete(piece_number, piece_data);
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 199 */       this.manager.rethrow(this, e);
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
/* 211 */     int read_length = 0;
/*     */     
/* 213 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 215 */       read_length += buffers[i].remaining((byte)3);
/*     */     }
/*     */     try
/*     */     {
/* 219 */       this.file.read(buffers, position);
/*     */       
/* 221 */       this.manager.fileBytesRead(read_length);
/*     */       
/* 223 */       this.bytes_read += read_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 227 */       this.manager.rethrow(this, e);
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
/* 239 */     int read_length = buffer.remaining((byte)3);
/*     */     try
/*     */     {
/* 242 */       this.file.read(buffer, position);
/*     */       
/* 244 */       this.manager.fileBytesRead(read_length);
/*     */       
/* 246 */       this.bytes_read += read_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 250 */       this.manager.rethrow(this, e);
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
/* 261 */     int write_length = buffer.remaining((byte)3);
/*     */     try
/*     */     {
/* 264 */       this.file.write(buffer, position);
/*     */       
/* 266 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 268 */       this.bytes_written += write_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 272 */       this.manager.rethrow(this, e);
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
/* 283 */     int write_length = 0;
/*     */     
/* 285 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 287 */       write_length += buffers[i].remaining((byte)3);
/*     */     }
/*     */     try
/*     */     {
/* 291 */       this.file.write(buffers, position);
/*     */       
/* 293 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 295 */       this.bytes_written += write_length;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 299 */       this.manager.rethrow(this, e);
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
/* 310 */     int write_length = buffer.remaining((byte)3);
/*     */     
/* 312 */     boolean write_ok = false;
/*     */     try
/*     */     {
/* 315 */       this.file.write(buffer, position);
/*     */       
/* 317 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 319 */       this.bytes_written += write_length;
/*     */       
/* 321 */       write_ok = true;
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 325 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally
/*     */     {
/* 329 */       if (write_ok)
/*     */       {
/* 331 */         buffer.returnToPool();
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
/* 343 */     int write_length = 0;
/*     */     
/* 345 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 347 */       write_length += buffers[i].remaining((byte)3);
/*     */     }
/*     */     
/* 350 */     boolean write_ok = false;
/*     */     try
/*     */     {
/* 353 */       this.file.write(buffers, position);
/*     */       
/* 355 */       this.manager.fileBytesWritten(write_length);
/*     */       
/* 357 */       this.bytes_written += write_length;
/*     */       
/* 359 */       write_ok = true;
/*     */     }
/*     */     catch (FMFileManagerException e) {
/*     */       int i;
/* 363 */       this.manager.rethrow(this, e);
/*     */     }
/*     */     finally {
/*     */       int i;
/* 367 */       if (write_ok)
/*     */       {
/* 369 */         for (int i = 0; i < buffers.length; i++)
/*     */         {
/* 371 */           buffers[i].returnToPool();
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
/* 383 */       this.file.flush();
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 387 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
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
/* 405 */       this.file.close();
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 409 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 416 */     return this.file.isOpen();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSessionBytesRead()
/*     */   {
/* 422 */     return this.bytes_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSessionBytesWritten()
/*     */   {
/* 428 */     return this.bytes_written;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void delete()
/*     */     throws CacheFileManagerException
/*     */   {
/*     */     try
/*     */     {
/* 438 */       this.file.delete();
/*     */     }
/*     */     catch (FMFileManagerException e)
/*     */     {
/* 442 */       this.manager.rethrow(this, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/impl/CacheFileWithoutCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */