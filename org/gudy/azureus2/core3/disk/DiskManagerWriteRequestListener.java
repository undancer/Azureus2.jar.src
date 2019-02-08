package org.gudy.azureus2.core3.disk;

public abstract interface DiskManagerWriteRequestListener
{
  public abstract void writeCompleted(DiskManagerWriteRequest paramDiskManagerWriteRequest);
  
  public abstract void writeFailed(DiskManagerWriteRequest paramDiskManagerWriteRequest, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerWriteRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */