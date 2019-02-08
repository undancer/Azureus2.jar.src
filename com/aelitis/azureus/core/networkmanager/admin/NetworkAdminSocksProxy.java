package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminSocksProxy
{
  public abstract String getName();
  
  public abstract String getHost();
  
  public abstract String getPort();
  
  public abstract String getUser();
  
  public abstract String[] getVersionsSupported()
    throws NetworkAdminException;
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminSocksProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */