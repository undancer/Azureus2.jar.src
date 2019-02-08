package com.aelitis.azureus.core.peermanager.messaging.azureus;

import com.aelitis.azureus.core.peermanager.messaging.Message;
import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;

public abstract interface AZStylePeerExchange
  extends Message
{
  public abstract PeerItem[] getAddedPeers();
  
  public abstract PeerItem[] getDroppedPeers();
  
  public abstract int getMaxAllowedPeersPerVolley(boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZStylePeerExchange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */