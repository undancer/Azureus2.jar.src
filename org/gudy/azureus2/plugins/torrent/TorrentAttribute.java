package org.gudy.azureus2.plugins.torrent;

public abstract interface TorrentAttribute
{
  public static final String TA_CATEGORY = "Category";
  public static final String TA_NETWORKS = "Networks";
  public static final String TA_PEER_SOURCES = "PeerSources";
  public static final String TA_TRACKER_CLIENT_EXTENSIONS = "TrackerClientExtensions";
  public static final String TA_SHARE_PROPERTIES = "ShareProperties";
  public static final String TA_CONTENT_MAP = "ContentMap";
  public static final String TA_DISPLAY_NAME = "DisplayName";
  public static final String TA_USER_COMMENT = "UserComment";
  public static final String TA_RELATIVE_SAVE_PATH = "RelativePath";
  
  public abstract String getName();
  
  public abstract String[] getDefinedValues();
  
  public abstract void addDefinedValue(String paramString);
  
  public abstract void removeDefinedValue(String paramString);
  
  public abstract void addTorrentAttributeListener(TorrentAttributeListener paramTorrentAttributeListener);
  
  public abstract void removeTorrentAttributeListener(TorrentAttributeListener paramTorrentAttributeListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentAttribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */