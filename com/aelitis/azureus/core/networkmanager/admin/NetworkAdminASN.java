package com.aelitis.azureus.core.networkmanager.admin;

import java.net.InetAddress;

public abstract interface NetworkAdminASN
{
  public abstract String getAS();
  
  public abstract String getASName();
  
  public abstract String getBGPPrefix();
  
  public abstract InetAddress getBGPStartAddress();
  
  public abstract InetAddress getBGPEndAddress();
  
  public abstract boolean matchesCIDR(InetAddress paramInetAddress);
  
  public abstract boolean sameAs(NetworkAdminASN paramNetworkAdminASN);
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminASN.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */