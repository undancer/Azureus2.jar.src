package org.gudy.azureus2.core3.disk;

import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface DiskManagerReadRequestListener
{
  public abstract void readCompleted(DiskManagerReadRequest paramDiskManagerReadRequest, DirectByteBuffer paramDirectByteBuffer);
  
  public abstract void readFailed(DiskManagerReadRequest paramDiskManagerReadRequest, Throwable paramThrowable);
  
  public abstract int getPriority();
  
  public abstract void requestExecuted(long paramLong);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerReadRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */