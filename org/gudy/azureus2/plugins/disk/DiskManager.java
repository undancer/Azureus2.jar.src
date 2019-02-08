package org.gudy.azureus2.plugins.disk;

import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface DiskManager
{
  public static final int BLOCK_SIZE = 16384;
  
  public abstract DiskManagerReadRequest read(int paramInt1, int paramInt2, int paramInt3, DiskManagerReadRequestListener paramDiskManagerReadRequestListener)
    throws DiskManagerException;
  
  public abstract DiskManagerWriteRequest write(int paramInt1, int paramInt2, PooledByteBuffer paramPooledByteBuffer, DiskManagerWriteRequestListener paramDiskManagerWriteRequestListener)
    throws DiskManagerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */