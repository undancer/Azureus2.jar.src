package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminSpeedTestScheduledTest
{
  public abstract NetworkAdminSpeedTester getTester();
  
  public abstract long getMaxUpBytePerSec();
  
  public abstract long getMaxDownBytePerSec();
  
  public abstract boolean start();
  
  public abstract void abort();
  
  public abstract void addListener(NetworkAdminSpeedTestScheduledTestListener paramNetworkAdminSpeedTestScheduledTestListener);
  
  public abstract void removeListener(NetworkAdminSpeedTestScheduledTestListener paramNetworkAdminSpeedTestScheduledTestListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminSpeedTestScheduledTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */