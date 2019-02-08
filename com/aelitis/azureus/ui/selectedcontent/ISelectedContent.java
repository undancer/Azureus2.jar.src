package com.aelitis.azureus.ui.selectedcontent;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface ISelectedContent
{
  public abstract String getHash();
  
  public abstract void setHash(String paramString);
  
  public abstract DownloadManager getDownloadManager();
  
  public abstract int getFileIndex();
  
  public abstract void setDownloadManager(DownloadManager paramDownloadManager);
  
  public abstract TOTorrent getTorrent();
  
  public abstract void setTorrent(TOTorrent paramTOTorrent);
  
  public abstract String getDisplayName();
  
  public abstract void setDisplayName(String paramString);
  
  public abstract DownloadUrlInfo getDownloadInfo();
  
  public abstract void setDownloadInfo(DownloadUrlInfo paramDownloadUrlInfo);
  
  public abstract boolean sameAs(ISelectedContent paramISelectedContent);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/selectedcontent/ISelectedContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */