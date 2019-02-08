package com.aelitis.azureus.core.networkmanager.admin.impl;

import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
import java.net.InetAddress;

public abstract interface NetworkAdminProtocolTester
{
  public abstract InetAddress testOutbound(InetAddress paramInetAddress, int paramInt)
    throws NetworkAdminException;
  
  public abstract InetAddress testInbound(InetAddress paramInetAddress, int paramInt)
    throws NetworkAdminException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminProtocolTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */