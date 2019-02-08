package com.aelitis.azureus.core.networkmanager.admin;

import java.net.InetAddress;

public abstract interface NetworkAdminNATDevice
{
  public abstract String getName();
  
  public abstract InetAddress getAddress();
  
  public abstract int getPort();
  
  public abstract InetAddress getExternalAddress();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminNATDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */