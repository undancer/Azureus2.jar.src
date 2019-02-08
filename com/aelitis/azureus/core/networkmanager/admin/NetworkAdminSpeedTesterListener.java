package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminSpeedTesterListener
{
  public abstract void complete(NetworkAdminSpeedTester paramNetworkAdminSpeedTester, NetworkAdminSpeedTesterResult paramNetworkAdminSpeedTesterResult);
  
  public abstract void stage(NetworkAdminSpeedTester paramNetworkAdminSpeedTester, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminSpeedTesterListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */