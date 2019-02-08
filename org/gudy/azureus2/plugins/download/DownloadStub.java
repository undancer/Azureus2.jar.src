package org.gudy.azureus2.plugins.download;

import java.io.File;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

public abstract interface DownloadStub
{
  public abstract boolean isStub();
  
  public abstract Download destubbify()
    throws DownloadException;
  
  public abstract String getName();
  
  public abstract byte[] getTorrentHash();
  
  public abstract long getTorrentSize();
  
  public abstract Torrent getTorrent();
  
  public abstract String getSavePath();
  
  public abstract DownloadStubFile[] getStubFiles();
  
  public abstract long getLongAttribute(TorrentAttribute paramTorrentAttribute);
  
  public abstract void setLongAttribute(TorrentAttribute paramTorrentAttribute, long paramLong);
  
  public abstract void remove()
    throws DownloadException, DownloadRemovalVetoException;
  
  public static abstract interface DownloadStubEx
    extends DownloadStub
  {
    public abstract long getCreationDate();
    
    public abstract String[] getManualTags();
    
    public abstract int getShareRatio();
    
    public abstract void remove(boolean paramBoolean1, boolean paramBoolean2)
      throws DownloadException, DownloadRemovalVetoException;
  }
  
  public static abstract interface DownloadStubFile
  {
    public abstract File getFile();
    
    public abstract long getLength();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */