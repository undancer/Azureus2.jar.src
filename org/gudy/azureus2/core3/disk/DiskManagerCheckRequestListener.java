package org.gudy.azureus2.core3.disk;

public abstract interface DiskManagerCheckRequestListener
{
  public abstract void checkCompleted(DiskManagerCheckRequest paramDiskManagerCheckRequest, boolean paramBoolean);
  
  public abstract void checkCancelled(DiskManagerCheckRequest paramDiskManagerCheckRequest);
  
  public abstract void checkFailed(DiskManagerCheckRequest paramDiskManagerCheckRequest, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerCheckRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */