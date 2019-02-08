package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminRoutesListener
{
  public abstract boolean foundNode(NetworkAdminNetworkInterfaceAddress paramNetworkAdminNetworkInterfaceAddress, NetworkAdminNode[] paramArrayOfNetworkAdminNode, int paramInt1, int paramInt2);
  
  public abstract boolean timeout(NetworkAdminNetworkInterfaceAddress paramNetworkAdminNetworkInterfaceAddress, NetworkAdminNode[] paramArrayOfNetworkAdminNode, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminRoutesListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */