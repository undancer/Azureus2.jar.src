package org.gudy.azureus2.plugins.disk;

public abstract interface DiskManagerWriteRequestListener
{
  public abstract void complete(DiskManagerWriteRequest paramDiskManagerWriteRequest);
  
  public abstract void failed(DiskManagerWriteRequest paramDiskManagerWriteRequest, DiskManagerException paramDiskManagerException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerWriteRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */