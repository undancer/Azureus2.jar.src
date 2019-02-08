package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminSpeedTestScheduler
{
  public static final int TEST_TYPE_BT = 1;
  
  public abstract void initialise();
  
  public abstract NetworkAdminSpeedTestScheduledTest getCurrentTest();
  
  public abstract NetworkAdminSpeedTestScheduledTest scheduleTest(int paramInt)
    throws NetworkAdminException;
  
  public abstract NetworkAdminSpeedTesterResult getLastResult(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminSpeedTestScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */