package com.aelitis.net.upnp.services;

import com.aelitis.net.upnp.UPnPException;

public abstract interface UPnPOfflineDownloader
  extends UPnPSpecificService
{
  public abstract long getFreeSpace(String paramString)
    throws UPnPException;
  
  public abstract void activate(String paramString)
    throws UPnPException;
  
  public abstract String addDownload(String paramString1, String paramString2, String paramString3)
    throws UPnPException;
  
  public abstract String addDownloadChunked(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2)
    throws UPnPException;
  
  public abstract String[] updateDownload(String paramString1, String paramString2, String paramString3)
    throws UPnPException;
  
  public abstract String[] setDownloads(String paramString1, String paramString2)
    throws UPnPException;
  
  public abstract String removeDownload(String paramString1, String paramString2)
    throws UPnPException;
  
  public abstract String[] startDownload(String paramString1, String paramString2)
    throws UPnPException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/services/UPnPOfflineDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */