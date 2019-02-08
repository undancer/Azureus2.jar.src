package com.aelitis.azureus.core.peer.cache;

import java.net.InetAddress;
import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface CacheDiscoverer
{
  public abstract CachePeer[] lookup(TOTorrent paramTOTorrent);
  
  public abstract CachePeer lookup(byte[] paramArrayOfByte, InetAddress paramInetAddress, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peer/cache/CacheDiscoverer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */