package com.aelitis.azureus.core.diskmanager.file;

import java.io.File;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;

public abstract interface FMFileOwner
{
  public abstract String getName();
  
  public abstract TOTorrentFile getTorrentFile();
  
  public abstract File getControlFileDir();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/FMFileOwner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */