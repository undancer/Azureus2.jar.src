package com.aelitis.azureus.core.diskmanager.cache;

import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface CacheFileManagerStats
{
  public abstract long getSize();
  
  public abstract long getUsedSize();
  
  public abstract long getBytesWrittenToCache();
  
  public abstract long getBytesWrittenToFile();
  
  public abstract long getBytesReadFromCache();
  
  public abstract long getBytesReadFromFile();
  
  public abstract long getAverageBytesWrittenToCache();
  
  public abstract long getAverageBytesWrittenToFile();
  
  public abstract long getAverageBytesReadFromCache();
  
  public abstract long getAverageBytesReadFromFile();
  
  public abstract long getCacheReadCount();
  
  public abstract long getCacheWriteCount();
  
  public abstract long getFileReadCount();
  
  public abstract long getFileWriteCount();
  
  public abstract boolean[] getBytesInCache(TOTorrent paramTOTorrent, long[] paramArrayOfLong1, long[] paramArrayOfLong2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/CacheFileManagerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */