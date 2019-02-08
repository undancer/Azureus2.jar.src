package com.aelitis.azureus.core.diskmanager.cache;

import java.io.File;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface CacheFile
{
  public static final int CT_LINEAR = 1;
  public static final int CT_COMPACT = 2;
  public static final int CT_PIECE_REORDER = 3;
  public static final int CT_PIECE_REORDER_COMPACT = 4;
  public static final int CF_READ = 1;
  public static final int CF_WRITE = 2;
  public static final short CP_NONE = 0;
  public static final short CP_READ_CACHE = 1;
  public static final short CP_FLUSH = 2;
  
  public abstract TOTorrentFile getTorrentFile();
  
  public abstract boolean exists();
  
  public abstract void moveFile(File paramFile)
    throws CacheFileManagerException;
  
  public abstract void renameFile(String paramString)
    throws CacheFileManagerException;
  
  public abstract void setAccessMode(int paramInt)
    throws CacheFileManagerException;
  
  public abstract int getAccessMode();
  
  public abstract void setStorageType(int paramInt)
    throws CacheFileManagerException;
  
  public abstract int getStorageType();
  
  public abstract long getLength()
    throws CacheFileManagerException;
  
  public abstract long compareLength(long paramLong)
    throws CacheFileManagerException;
  
  public abstract void setLength(long paramLong)
    throws CacheFileManagerException;
  
  public abstract void setPieceComplete(int paramInt, DirectByteBuffer paramDirectByteBuffer)
    throws CacheFileManagerException;
  
  public abstract void read(DirectByteBuffer paramDirectByteBuffer, long paramLong, short paramShort)
    throws CacheFileManagerException;
  
  public abstract void read(DirectByteBuffer[] paramArrayOfDirectByteBuffer, long paramLong, short paramShort)
    throws CacheFileManagerException;
  
  public abstract void write(DirectByteBuffer paramDirectByteBuffer, long paramLong)
    throws CacheFileManagerException;
  
  public abstract void write(DirectByteBuffer[] paramArrayOfDirectByteBuffer, long paramLong)
    throws CacheFileManagerException;
  
  public abstract void writeAndHandoverBuffer(DirectByteBuffer paramDirectByteBuffer, long paramLong)
    throws CacheFileManagerException;
  
  public abstract void writeAndHandoverBuffers(DirectByteBuffer[] paramArrayOfDirectByteBuffer, long paramLong)
    throws CacheFileManagerException;
  
  public abstract void flushCache()
    throws CacheFileManagerException;
  
  public abstract void clearCache()
    throws CacheFileManagerException;
  
  public abstract void close()
    throws CacheFileManagerException;
  
  public abstract boolean isOpen();
  
  public abstract long getSessionBytesRead();
  
  public abstract long getSessionBytesWritten();
  
  public abstract void delete()
    throws CacheFileManagerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/CacheFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */