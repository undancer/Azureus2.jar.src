package org.gudy.azureus2.plugins.utils;

import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
import org.gudy.azureus2.plugins.download.DownloadStats;

public abstract interface ShortCuts
{
  public abstract Download getDownload(byte[] paramArrayOfByte)
    throws DownloadException;
  
  public abstract DownloadStats getDownloadStats(byte[] paramArrayOfByte)
    throws DownloadException;
  
  public abstract void restartDownload(byte[] paramArrayOfByte)
    throws DownloadException;
  
  public abstract void stopDownload(byte[] paramArrayOfByte)
    throws DownloadException;
  
  public abstract void removeDownload(byte[] paramArrayOfByte)
    throws DownloadException, DownloadRemovalVetoException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/ShortCuts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */