package com.aelitis.azureus.core.diskmanager.file;

import java.io.File;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface FMFile
{
  public static final int FT_LINEAR = 1;
  public static final int FT_COMPACT = 2;
  public static final int FT_PIECE_REORDER = 3;
  public static final int FT_PIECE_REORDER_COMPACT = 4;
  public static final int FM_READ = 1;
  public static final int FM_WRITE = 2;
  
  public abstract String getName();
  
  public abstract boolean exists();
  
  public abstract FMFileOwner getOwner();
  
  public abstract void moveFile(File paramFile)
    throws FMFileManagerException;
  
  public abstract void renameFile(String paramString)
    throws FMFileManagerException;
  
  public abstract void setAccessMode(int paramInt)
    throws FMFileManagerException;
  
  public abstract int getAccessMode();
  
  public abstract void setStorageType(int paramInt)
    throws FMFileManagerException;
  
  public abstract int getStorageType();
  
  public abstract void ensureOpen(String paramString)
    throws FMFileManagerException;
  
  public abstract long getLength()
    throws FMFileManagerException;
  
  public abstract void setLength(long paramLong)
    throws FMFileManagerException;
  
  public abstract void setPieceComplete(int paramInt, DirectByteBuffer paramDirectByteBuffer)
    throws FMFileManagerException;
  
  public abstract void read(DirectByteBuffer paramDirectByteBuffer, long paramLong)
    throws FMFileManagerException;
  
  public abstract void read(DirectByteBuffer[] paramArrayOfDirectByteBuffer, long paramLong)
    throws FMFileManagerException;
  
  public abstract void write(DirectByteBuffer paramDirectByteBuffer, long paramLong)
    throws FMFileManagerException;
  
  public abstract void write(DirectByteBuffer[] paramArrayOfDirectByteBuffer, long paramLong)
    throws FMFileManagerException;
  
  public abstract void flush()
    throws FMFileManagerException;
  
  public abstract void close()
    throws FMFileManagerException;
  
  public abstract boolean isOpen();
  
  public abstract void delete()
    throws FMFileManagerException;
  
  public abstract FMFile createClone()
    throws FMFileManagerException;
  
  public abstract boolean isClone();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/FMFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */