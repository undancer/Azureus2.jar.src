package org.gudy.azureus2.plugins.utils.resourcedownloader;

import java.io.File;
import java.net.Proxy;
import java.net.URL;

public abstract interface ResourceDownloaderFactory
{
  public abstract ResourceDownloader create(File paramFile);
  
  public abstract ResourceDownloader create(URL paramURL);
  
  public abstract ResourceDownloader createWithAutoPluginProxy(URL paramURL);
  
  public abstract ResourceDownloader create(URL paramURL, boolean paramBoolean);
  
  public abstract ResourceDownloader create(URL paramURL, Proxy paramProxy);
  
  public abstract ResourceDownloader create(URL paramURL, String paramString1, String paramString2);
  
  public abstract ResourceDownloader create(ResourceDownloaderDelayedFactory paramResourceDownloaderDelayedFactory);
  
  public abstract ResourceDownloader getRetryDownloader(ResourceDownloader paramResourceDownloader, int paramInt);
  
  public abstract ResourceDownloader getTimeoutDownloader(ResourceDownloader paramResourceDownloader, int paramInt);
  
  public abstract ResourceDownloader getAlternateDownloader(ResourceDownloader[] paramArrayOfResourceDownloader);
  
  public abstract ResourceDownloader getAlternateDownloader(ResourceDownloader[] paramArrayOfResourceDownloader, int paramInt);
  
  public abstract ResourceDownloader getRandomDownloader(ResourceDownloader[] paramArrayOfResourceDownloader);
  
  public abstract ResourceDownloader getRandomDownloader(ResourceDownloader[] paramArrayOfResourceDownloader, int paramInt);
  
  public abstract ResourceDownloader getMetaRefreshDownloader(ResourceDownloader paramResourceDownloader);
  
  public abstract ResourceDownloader getTorrentDownloader(ResourceDownloader paramResourceDownloader, boolean paramBoolean);
  
  public abstract ResourceDownloader getTorrentDownloader(ResourceDownloader paramResourceDownloader, boolean paramBoolean, File paramFile);
  
  public abstract ResourceDownloader getSuffixBasedDownloader(ResourceDownloader paramResourceDownloader);
  
  public abstract ResourceDownloader create(URL paramURL, String paramString);
  
  public abstract ResourceDownloader create(URL paramURL, String paramString, Proxy paramProxy);
  
  public abstract ResourceDownloader create(URL paramURL, byte[] paramArrayOfByte);
  
  public abstract ResourceDownloader[] getSourceforgeDownloaders(String paramString1, String paramString2);
  
  public abstract ResourceDownloader getSourceforgeDownloader(String paramString1, String paramString2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */