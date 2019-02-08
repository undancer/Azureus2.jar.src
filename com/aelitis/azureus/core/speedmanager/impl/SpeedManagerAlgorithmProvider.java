package com.aelitis.azureus.core.speedmanager.impl;

import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;

public abstract interface SpeedManagerAlgorithmProvider
{
  public static final int UPDATE_PERIOD_MILLIS = 1000;
  
  public abstract void reset();
  
  public abstract void updateStats();
  
  public abstract void pingSourceFound(SpeedManagerPingSource paramSpeedManagerPingSource, boolean paramBoolean);
  
  public abstract void pingSourceFailed(SpeedManagerPingSource paramSpeedManagerPingSource);
  
  public abstract void calculate(SpeedManagerPingSource[] paramArrayOfSpeedManagerPingSource);
  
  public abstract int getIdlePingMillis();
  
  public abstract int getCurrentPingMillis();
  
  public abstract int getMaxPingMillis();
  
  public abstract int getCurrentChokeSpeed();
  
  public abstract int getMaxUploadSpeed();
  
  public abstract boolean getAdjustsDownloadLimits();
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/SpeedManagerAlgorithmProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */