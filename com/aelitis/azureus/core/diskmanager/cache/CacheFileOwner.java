package com.aelitis.azureus.core.diskmanager.cache;

import java.io.File;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;

public abstract interface CacheFileOwner
{
  public static final int CACHE_MODE_NORMAL = 1;
  public static final int CACHE_MODE_NO_CACHE = 2;
  public static final int CACHE_MODE_EXPERIMENTAL = 3;
  
  public abstract String getCacheFileOwnerName();
  
  public abstract TOTorrentFile getCacheFileTorrentFile();
  
  public abstract File getCacheFileControlFileDir();
  
  public abstract int getCacheMode();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/CacheFileOwner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */