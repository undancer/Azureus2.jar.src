package org.gudy.azureus2.core3.disk;

import org.gudy.azureus2.plugins.peers.PeerReadRequest;

public abstract interface DiskManagerReadRequest
  extends PeerReadRequest, DiskManagerRequest
{
  public abstract int getPieceNumber();
  
  public abstract int getOffset();
  
  public abstract int getLength();
  
  public abstract long getTimeCreated(long paramLong);
  
  public abstract void setTimeSent(long paramLong);
  
  public abstract long getTimeSent();
  
  public abstract void setFlush(boolean paramBoolean);
  
  public abstract boolean getFlush();
  
  public abstract void setUseCache(boolean paramBoolean);
  
  public abstract boolean getUseCache();
  
  public abstract void setLatencyTest();
  
  public abstract boolean isLatencyTest();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerReadRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */