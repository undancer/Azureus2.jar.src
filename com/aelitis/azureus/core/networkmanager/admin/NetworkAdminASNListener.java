package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminASNListener
{
  public abstract void success(NetworkAdminASN paramNetworkAdminASN);
  
  public abstract void failed(NetworkAdminException paramNetworkAdminException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminASNListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */