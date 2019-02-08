package org.gudy.azureus2.plugins.download;

import java.net.URL;
import java.util.Map;

public abstract interface DownloadAnnounceResult
{
  public static final int RT_SUCCESS = 1;
  public static final int RT_ERROR = 2;
  
  public abstract Download getDownload();
  
  public abstract int getResponseType();
  
  public abstract int getReportedPeerCount();
  
  public abstract int getSeedCount();
  
  public abstract int getNonSeedCount();
  
  public abstract String getError();
  
  public abstract URL getURL();
  
  public abstract DownloadAnnounceResultPeer[] getPeers();
  
  public abstract long getTimeToWait();
  
  public abstract Map getExtensions();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadAnnounceResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */