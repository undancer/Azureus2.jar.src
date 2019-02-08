package com.aelitis.azureus.core.speedmanager.impl.v2;

import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;

public abstract interface PSMonitorListener
{
  public abstract void notifyUpload(SpeedManagerLimitEstimate paramSpeedManagerLimitEstimate);
  
  public abstract void notifyDownload(SpeedManagerLimitEstimate paramSpeedManagerLimitEstimate);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/PSMonitorListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */