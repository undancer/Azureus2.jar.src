package org.gudy.azureus2.core3.tracker.server.impl;

import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
import org.gudy.azureus2.core3.util.HashWrapper;

public abstract interface TRTrackerServerSimplePeer
{
  public abstract byte[] getIPAsRead();
  
  public abstract byte[] getIPAddressBytes();
  
  public abstract HashWrapper getPeerId();
  
  public abstract int getTCPPort();
  
  public abstract int getUDPPort();
  
  public abstract int getHTTPPort();
  
  public abstract boolean isSeed();
  
  public abstract boolean isBiased();
  
  public abstract byte getCryptoLevel();
  
  public abstract byte getAZVer();
  
  public abstract int getUpSpeed();
  
  public abstract DHTNetworkPosition getNetworkPosition();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerSimplePeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */