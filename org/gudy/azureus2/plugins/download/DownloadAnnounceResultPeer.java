package org.gudy.azureus2.plugins.download;

public abstract interface DownloadAnnounceResultPeer
{
  public static final short PROTOCOL_NORMAL = 1;
  public static final short PROTOCOL_CRYPT = 2;
  public static final String PEERSOURCE_BT_TRACKER = "Tracker";
  public static final String PEERSOURCE_DHT = "DHT";
  public static final String PEERSOURCE_PEX = "PeerExchange";
  public static final String PEERSOURCE_PLUGIN = "Plugin";
  public static final String PEERSOURCE_INCOMING = "Incoming";
  
  public abstract String getSource();
  
  public abstract int getPort();
  
  public abstract int getUDPPort();
  
  public abstract String getAddress();
  
  public abstract byte[] getPeerID();
  
  public abstract short getProtocol();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadAnnounceResultPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */