package com.aelitis.azureus.core.speedmanager.impl.v2;

import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;

public abstract interface SMConfigurationAdapter
{
  public abstract SpeedManagerLimitEstimate getUploadLimit();
  
  public abstract SpeedManagerLimitEstimate getDownloadLimit();
  
  public abstract void setUploadLimit(SpeedManagerLimitEstimate paramSpeedManagerLimitEstimate);
  
  public abstract void setDownloadLimit(SpeedManagerLimitEstimate paramSpeedManagerLimitEstimate);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SMConfigurationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */