package com.aelitis.azureus.core.diskmanager.access;

import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface DiskAccessController
{
  public abstract DiskAccessRequest queueReadRequest(CacheFile paramCacheFile, long paramLong, DirectByteBuffer paramDirectByteBuffer, short paramShort, DiskAccessRequestListener paramDiskAccessRequestListener);
  
  public abstract DiskAccessRequest queueWriteRequest(CacheFile paramCacheFile, long paramLong, DirectByteBuffer paramDirectByteBuffer, boolean paramBoolean, DiskAccessRequestListener paramDiskAccessRequestListener);
  
  public abstract DiskAccessControllerStats getStats();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/access/DiskAccessController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */