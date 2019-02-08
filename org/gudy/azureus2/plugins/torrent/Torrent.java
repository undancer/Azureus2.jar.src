package org.gudy.azureus2.plugins.torrent;

import java.io.File;
import java.net.URL;
import java.util.Map;

public abstract interface Torrent
{
  public abstract String getName();
  
  public abstract URL getAnnounceURL();
  
  public abstract void setAnnounceURL(URL paramURL);
  
  public abstract TorrentAnnounceURLList getAnnounceURLList();
  
  public abstract byte[] getHash();
  
  public abstract long getSize();
  
  public abstract String getComment();
  
  public abstract void setComment(String paramString);
  
  public abstract long getCreationDate();
  
  public abstract String getCreatedBy();
  
  public abstract long getPieceSize();
  
  public abstract long getPieceCount();
  
  public abstract byte[][] getPieces();
  
  public abstract TorrentFile[] getFiles();
  
  public abstract String getEncoding();
  
  public abstract void setEncoding(String paramString)
    throws TorrentEncodingException;
  
  public abstract void setDefaultEncoding()
    throws TorrentEncodingException;
  
  public abstract Object getAdditionalProperty(String paramString);
  
  public abstract Torrent removeAdditionalProperties();
  
  public abstract void setPluginStringProperty(String paramString1, String paramString2);
  
  public abstract String getPluginStringProperty(String paramString);
  
  public abstract void setMapProperty(String paramString, Map paramMap);
  
  public abstract Map getMapProperty(String paramString);
  
  public abstract boolean isDecentralised();
  
  public abstract boolean isDecentralisedBackupEnabled();
  
  public abstract void setDecentralisedBackupRequested(boolean paramBoolean);
  
  public abstract boolean isDecentralisedBackupRequested();
  
  public abstract boolean isPrivate();
  
  public abstract void setPrivate(boolean paramBoolean);
  
  public abstract boolean wasCreatedByUs();
  
  public abstract URL getMagnetURI()
    throws TorrentException;
  
  public abstract Map writeToMap()
    throws TorrentException;
  
  public abstract void writeToFile(File paramFile)
    throws TorrentException;
  
  public abstract byte[] writeToBEncodedData()
    throws TorrentException;
  
  public abstract void save()
    throws TorrentException;
  
  public abstract void setComplete(File paramFile)
    throws TorrentException;
  
  public abstract boolean isComplete();
  
  public abstract boolean isSimpleTorrent();
  
  public abstract Torrent getClone()
    throws TorrentException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/Torrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */