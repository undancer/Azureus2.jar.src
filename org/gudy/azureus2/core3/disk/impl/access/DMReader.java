package org.gudy.azureus2.core3.disk.impl.access;

import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface DMReader
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract DirectByteBuffer readBlock(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract DiskManagerReadRequest createReadRequest(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void readBlock(DiskManagerReadRequest paramDiskManagerReadRequest, DiskManagerReadRequestListener paramDiskManagerReadRequestListener);
  
  public abstract boolean hasOutstandingReadRequestForPiece(int paramInt);
  
  public abstract long[] getStats();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/DMReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */