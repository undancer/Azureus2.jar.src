package com.aelitis.azureus.core.networkmanager.admin;

import java.net.InetAddress;

public abstract interface NetworkAdminNode
{
  public abstract InetAddress getAddress();
  
  public abstract boolean isLocalAddress();
  
  public abstract int getDistance();
  
  public abstract int getRTT();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */