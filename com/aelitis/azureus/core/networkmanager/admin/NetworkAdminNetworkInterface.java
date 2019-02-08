package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminNetworkInterface
{
  public abstract String getDisplayName();
  
  public abstract String getName();
  
  public abstract NetworkAdminNetworkInterfaceAddress[] getAddresses();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminNetworkInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */