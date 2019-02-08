package org.gudy.azureus2.core3.disk.impl.access;

import org.gudy.azureus2.core3.disk.DiskManagerCheckRequest;
import org.gudy.azureus2.core3.disk.DiskManagerCheckRequestListener;

public abstract interface DMChecker
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract DiskManagerCheckRequest createCheckRequest(int paramInt, Object paramObject);
  
  public abstract void enqueueCompleteRecheckRequest(DiskManagerCheckRequest paramDiskManagerCheckRequest, DiskManagerCheckRequestListener paramDiskManagerCheckRequestListener);
  
  public abstract void enqueueCheckRequest(DiskManagerCheckRequest paramDiskManagerCheckRequest, DiskManagerCheckRequestListener paramDiskManagerCheckRequestListener);
  
  public abstract boolean hasOutstandingCheckRequestForPiece(int paramInt);
  
  public abstract int getCompleteRecheckStatus();
  
  public abstract void setCheckingEnabled(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/DMChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */