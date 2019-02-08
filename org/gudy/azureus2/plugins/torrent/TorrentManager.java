package org.gudy.azureus2.plugins.torrent;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public abstract interface TorrentManager
{
  public static final int PRESERVE_NONE = 0;
  public static final int PRESERVE_ENCODING = 1;
  public static final int PRESERVE_ALL = -1;
  
  public abstract TorrentDownloader getURLDownloader(URL paramURL)
    throws TorrentException;
  
  public abstract TorrentDownloader getURLDownloader(URL paramURL, String paramString1, String paramString2)
    throws TorrentException;
  
  public abstract Torrent createFromBEncodedFile(File paramFile)
    throws TorrentException;
  
  public abstract Torrent createFromBEncodedFile(File paramFile, boolean paramBoolean)
    throws TorrentException;
  
  public abstract Torrent createFromBEncodedInputStream(InputStream paramInputStream)
    throws TorrentException;
  
  public abstract Torrent createFromBEncodedData(byte[] paramArrayOfByte)
    throws TorrentException;
  
  public abstract Torrent createFromBEncodedFile(File paramFile, int paramInt)
    throws TorrentException;
  
  public abstract Torrent createFromBEncodedInputStream(InputStream paramInputStream, int paramInt)
    throws TorrentException;
  
  public abstract Torrent createFromBEncodedData(byte[] paramArrayOfByte, int paramInt)
    throws TorrentException;
  
  public abstract Torrent createFromDataFile(File paramFile, URL paramURL)
    throws TorrentException;
  
  public abstract Torrent createFromDataFile(File paramFile, URL paramURL, boolean paramBoolean)
    throws TorrentException;
  
  public abstract TorrentCreator createFromDataFileEx(File paramFile, URL paramURL, boolean paramBoolean)
    throws TorrentException;
  
  public abstract TorrentAttribute[] getDefinedAttributes();
  
  public abstract TorrentAttribute getAttribute(String paramString);
  
  public abstract TorrentAttribute getPluginAttribute(String paramString);
  
  public abstract void addListener(TorrentManagerListener paramTorrentManagerListener);
  
  public abstract void removeListener(TorrentManagerListener paramTorrentManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */