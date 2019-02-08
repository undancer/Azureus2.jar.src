package com.aelitis.azureus.plugins.extseed.util;

import com.aelitis.azureus.plugins.extseed.ExternalSeedException;

public abstract interface ExternalSeedHTTPDownloader
{
  public abstract void download(int paramInt, ExternalSeedHTTPDownloaderListener paramExternalSeedHTTPDownloaderListener, boolean paramBoolean)
    throws ExternalSeedException;
  
  public abstract void downloadRange(long paramLong, int paramInt, ExternalSeedHTTPDownloaderListener paramExternalSeedHTTPDownloaderListener, boolean paramBoolean)
    throws ExternalSeedException;
  
  public abstract void downloadSocket(int paramInt, ExternalSeedHTTPDownloaderListener paramExternalSeedHTTPDownloaderListener, boolean paramBoolean)
    throws ExternalSeedException;
  
  public abstract int getLastResponse();
  
  public abstract int getLast503RetrySecs();
  
  public abstract void deactivate();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/util/ExternalSeedHTTPDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */