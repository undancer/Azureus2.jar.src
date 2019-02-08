package org.gudy.azureus2.plugins.disk;

import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface DiskManagerReadRequestListener
{
  public abstract void complete(DiskManagerReadRequest paramDiskManagerReadRequest, PooledByteBuffer paramPooledByteBuffer);
  
  public abstract void failed(DiskManagerReadRequest paramDiskManagerReadRequest, DiskManagerException paramDiskManagerException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerReadRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */