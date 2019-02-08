package com.aelitis.azureus.core.networkmanager.admin;

import java.net.InetAddress;

public abstract interface NetworkAdminProtocol
{
  public static final int PT_HTTP = 1;
  public static final int PT_TCP = 2;
  public static final int PT_UDP = 3;
  
  public abstract int getType();
  
  public abstract int getPort();
  
  public abstract String getTypeString();
  
  public abstract InetAddress test(NetworkAdminNetworkInterfaceAddress paramNetworkAdminNetworkInterfaceAddress)
    throws NetworkAdminException;
  
  public abstract InetAddress test(NetworkAdminNetworkInterfaceAddress paramNetworkAdminNetworkInterfaceAddress, NetworkAdminProgressListener paramNetworkAdminProgressListener)
    throws NetworkAdminException;
  
  public abstract InetAddress test(NetworkAdminNetworkInterfaceAddress paramNetworkAdminNetworkInterfaceAddress, boolean paramBoolean, NetworkAdminProgressListener paramNetworkAdminProgressListener)
    throws NetworkAdminException;
  
  public abstract String getName();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminProtocol.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */