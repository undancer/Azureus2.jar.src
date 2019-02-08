package com.aelitis.azureus.core.diskmanager.access;

import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface DiskAccessRequest
{
  public abstract CacheFile getFile();
  
  public abstract long getOffset();
  
  public abstract int getSize();
  
  public abstract DirectByteBuffer getBuffer();
  
  public abstract int getPriority();
  
  public abstract void cancel();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/access/DiskAccessRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */