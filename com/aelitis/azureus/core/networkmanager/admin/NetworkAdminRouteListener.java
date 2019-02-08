package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminRouteListener
{
  public abstract boolean foundNode(NetworkAdminNode paramNetworkAdminNode, int paramInt1, int paramInt2);
  
  public abstract boolean timeout(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminRouteListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */