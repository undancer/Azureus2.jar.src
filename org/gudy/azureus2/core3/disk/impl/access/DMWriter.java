package org.gudy.azureus2.core3.disk.impl.access;

import org.gudy.azureus2.core3.disk.DiskManagerException;
import org.gudy.azureus2.core3.disk.DiskManagerWriteRequest;
import org.gudy.azureus2.core3.disk.DiskManagerWriteRequestListener;
import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface DMWriter
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract boolean zeroFile(DiskManagerFileInfoImpl paramDiskManagerFileInfoImpl, long paramLong)
    throws DiskManagerException;
  
  public abstract DiskManagerWriteRequest createWriteRequest(int paramInt1, int paramInt2, DirectByteBuffer paramDirectByteBuffer, Object paramObject);
  
  public abstract void writeBlock(DiskManagerWriteRequest paramDiskManagerWriteRequest, DiskManagerWriteRequestListener paramDiskManagerWriteRequestListener);
  
  public abstract boolean hasOutstandingWriteRequestForPiece(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/DMWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */