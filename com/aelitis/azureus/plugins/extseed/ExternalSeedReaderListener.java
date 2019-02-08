package com.aelitis.azureus.plugins.extseed;

import org.gudy.azureus2.plugins.peers.PeerReadRequest;
import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface ExternalSeedReaderListener
{
  public abstract void requestComplete(PeerReadRequest paramPeerReadRequest, PooledByteBuffer paramPooledByteBuffer);
  
  public abstract void requestCancelled(PeerReadRequest paramPeerReadRequest);
  
  public abstract void requestFailed(PeerReadRequest paramPeerReadRequest);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/ExternalSeedReaderListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */