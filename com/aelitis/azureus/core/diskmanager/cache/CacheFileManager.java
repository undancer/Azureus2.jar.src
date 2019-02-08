package com.aelitis.azureus.core.diskmanager.cache;

import com.aelitis.azureus.core.util.LinkFileMap;
import java.io.File;
import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface CacheFileManager
{
  public abstract CacheFile createFile(CacheFileOwner paramCacheFileOwner, File paramFile, int paramInt)
    throws CacheFileManagerException;
  
  public abstract CacheFileManagerStats getStats();
  
  public abstract void setFileLinks(TOTorrent paramTOTorrent, LinkFileMap paramLinkFileMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/CacheFileManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */