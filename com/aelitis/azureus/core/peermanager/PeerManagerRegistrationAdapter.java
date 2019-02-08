package com.aelitis.azureus.core.peermanager;

import com.aelitis.azureus.core.networkmanager.NetworkConnection;
import java.net.InetSocketAddress;

public abstract interface PeerManagerRegistrationAdapter
{
  public abstract byte[][] getSecrets();
  
  public abstract boolean manualRoute(NetworkConnection paramNetworkConnection);
  
  public abstract boolean isPeerSourceEnabled(String paramString);
  
  public abstract boolean activateRequest(InetSocketAddress paramInetSocketAddress);
  
  public abstract void deactivateRequest(InetSocketAddress paramInetSocketAddress);
  
  public abstract String getDescription();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/PeerManagerRegistrationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */