package com.aelitis.azureus.core.networkmanager.admin;

import java.net.InetAddress;

public abstract interface NetworkAdminNetworkInterfaceAddress
{
  public abstract NetworkAdminNetworkInterface getInterface();
  
  public abstract InetAddress getAddress();
  
  public abstract boolean isLoopback();
  
  public abstract NetworkAdminNode[] getRoute(InetAddress paramInetAddress, int paramInt, NetworkAdminRouteListener paramNetworkAdminRouteListener)
    throws NetworkAdminException;
  
  public abstract NetworkAdminNode pingTarget(InetAddress paramInetAddress, int paramInt, NetworkAdminRouteListener paramNetworkAdminRouteListener)
    throws NetworkAdminException;
  
  public abstract InetAddress testProtocol(NetworkAdminProtocol paramNetworkAdminProtocol)
    throws NetworkAdminException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminNetworkInterfaceAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */