package org.gudy.azureus2.plugins.disk;

import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface DiskManagerEvent
{
  public static final int EVENT_TYPE_SUCCESS = 1;
  public static final int EVENT_TYPE_FAILED = 2;
  public static final int EVENT_TYPE_BLOCKED = 3;
  
  public abstract int getType();
  
  public abstract long getOffset();
  
  public abstract int getLength();
  
  public abstract PooledByteBuffer getBuffer();
  
  public abstract Throwable getFailure();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/disk/DiskManagerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */