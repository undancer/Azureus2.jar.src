package com.aelitis.azureus.core.peermanager;

import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;

public abstract interface PeerManagerRegistration
{
  public abstract TOTorrentFile getLink(String paramString);
  
  public abstract void removeLink(String paramString);
  
  public abstract void addLink(String paramString, TOTorrentFile paramTOTorrentFile)
    throws Exception;
  
  public abstract void activate(PEPeerControl paramPEPeerControl);
  
  public abstract void deactivate();
  
  public abstract void unregister();
  
  public abstract String getDescription();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/PeerManagerRegistration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */