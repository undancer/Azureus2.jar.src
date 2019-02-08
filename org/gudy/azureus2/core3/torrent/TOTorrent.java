package org.gudy.azureus2.core3.torrent;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.HashWrapper;

public abstract interface TOTorrent
{
  public static final String DEFAULT_IGNORE_FILES = ".DS_Store;Thumbs.db;desktop.ini";
  public static final String AZUREUS_PROPERTIES = "azureus_properties";
  public static final String AZUREUS_PRIVATE_PROPERTIES = "azureus_private_properties";
  public static final String ENCODING_ACTUALLY_UTF8_KEYS = "utf8 keys";
  
  public abstract byte[] getName();
  
  public abstract boolean isSimpleTorrent();
  
  public abstract byte[] getComment();
  
  public abstract void setComment(String paramString);
  
  public abstract long getCreationDate();
  
  public abstract void setCreationDate(long paramLong);
  
  public abstract byte[] getCreatedBy();
  
  public abstract void setCreatedBy(byte[] paramArrayOfByte);
  
  public abstract boolean isCreated();
  
  public abstract URL getAnnounceURL();
  
  public abstract boolean setAnnounceURL(URL paramURL);
  
  public abstract TOTorrentAnnounceURLGroup getAnnounceURLGroup();
  
  public abstract boolean isDecentralised();
  
  public abstract byte[][] getPieces()
    throws TOTorrentException;
  
  public abstract void setPieces(byte[][] paramArrayOfByte)
    throws TOTorrentException;
  
  public abstract long getPieceLength();
  
  public abstract int getNumberOfPieces();
  
  public abstract long getSize();
  
  public abstract int getFileCount();
  
  public abstract TOTorrentFile[] getFiles();
  
  public abstract byte[] getHash()
    throws TOTorrentException;
  
  public abstract HashWrapper getHashWrapper()
    throws TOTorrentException;
  
  public abstract void setHashOverride(byte[] paramArrayOfByte)
    throws TOTorrentException;
  
  public abstract boolean hasSameHashAs(TOTorrent paramTOTorrent);
  
  public abstract boolean getPrivate();
  
  public abstract void setPrivate(boolean paramBoolean)
    throws TOTorrentException;
  
  public abstract void setAdditionalStringProperty(String paramString1, String paramString2);
  
  public abstract String getAdditionalStringProperty(String paramString);
  
  public abstract void setAdditionalByteArrayProperty(String paramString, byte[] paramArrayOfByte);
  
  public abstract byte[] getAdditionalByteArrayProperty(String paramString);
  
  public abstract void setAdditionalLongProperty(String paramString, Long paramLong);
  
  public abstract Long getAdditionalLongProperty(String paramString);
  
  public abstract void setAdditionalListProperty(String paramString, List paramList);
  
  public abstract List getAdditionalListProperty(String paramString);
  
  public abstract void setAdditionalMapProperty(String paramString, Map paramMap);
  
  public abstract Map getAdditionalMapProperty(String paramString);
  
  public abstract Object getAdditionalProperty(String paramString);
  
  public abstract void setAdditionalProperty(String paramString, Object paramObject);
  
  public abstract void removeAdditionalProperty(String paramString);
  
  public abstract void removeAdditionalProperties();
  
  public abstract void serialiseToBEncodedFile(File paramFile)
    throws TOTorrentException;
  
  public abstract Map serialiseToMap()
    throws TOTorrentException;
  
  public abstract void serialiseToXMLFile(File paramFile)
    throws TOTorrentException;
  
  public abstract void addListener(TOTorrentListener paramTOTorrentListener);
  
  public abstract void removeListener(TOTorrentListener paramTOTorrentListener);
  
  public abstract AEMonitor getMonitor();
  
  public abstract void print();
  
  public abstract String getUTF8Name();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/TOTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */