package com.aelitis.azureus.core.networkmanager.admin;

public abstract interface NetworkAdminSpeedTesterResult
{
  public abstract NetworkAdminSpeedTester getTest();
  
  public abstract long getTestTime();
  
  public abstract int getDownloadSpeed();
  
  public abstract int getUploadSpeed();
  
  public abstract boolean hadError();
  
  public abstract String getLastError();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/NetworkAdminSpeedTesterResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */