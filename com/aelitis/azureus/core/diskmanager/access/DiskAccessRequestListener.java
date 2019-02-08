package com.aelitis.azureus.core.diskmanager.access;

public abstract interface DiskAccessRequestListener
{
  public abstract void requestComplete(DiskAccessRequest paramDiskAccessRequest);
  
  public abstract void requestCancelled(DiskAccessRequest paramDiskAccessRequest);
  
  public abstract void requestFailed(DiskAccessRequest paramDiskAccessRequest, Throwable paramThrowable);
  
  public abstract int getPriority();
  
  public abstract void requestExecuted(long paramLong);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/access/DiskAccessRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */