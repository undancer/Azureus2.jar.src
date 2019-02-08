package com.aelitis.azureus.core.diskmanager.file.impl;

import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
import java.io.RandomAccessFile;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface FMFileAccess
{
  public abstract void aboutToOpen()
    throws FMFileManagerException;
  
  public abstract long getLength(RandomAccessFile paramRandomAccessFile)
    throws FMFileManagerException;
  
  public abstract void setLength(RandomAccessFile paramRandomAccessFile, long paramLong)
    throws FMFileManagerException;
  
  public abstract void read(RandomAccessFile paramRandomAccessFile, DirectByteBuffer[] paramArrayOfDirectByteBuffer, long paramLong)
    throws FMFileManagerException;
  
  public abstract void write(RandomAccessFile paramRandomAccessFile, DirectByteBuffer[] paramArrayOfDirectByteBuffer, long paramLong)
    throws FMFileManagerException;
  
  public abstract void flush()
    throws FMFileManagerException;
  
  public abstract boolean isPieceCompleteProcessingNeeded(int paramInt);
  
  public abstract void setPieceComplete(RandomAccessFile paramRandomAccessFile, int paramInt, DirectByteBuffer paramDirectByteBuffer)
    throws FMFileManagerException;
  
  public abstract FMFileImpl getFile();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/impl/FMFileAccess.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */