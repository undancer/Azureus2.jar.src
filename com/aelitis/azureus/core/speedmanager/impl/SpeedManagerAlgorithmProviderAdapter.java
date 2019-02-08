package com.aelitis.azureus.core.speedmanager.impl;

import com.aelitis.azureus.core.speedmanager.SpeedManager;
import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;

public abstract interface SpeedManagerAlgorithmProviderAdapter
{
  public abstract SpeedManager getSpeedManager();
  
  public abstract int getCurrentProtocolUploadSpeed();
  
  public abstract int getCurrentDataUploadSpeed();
  
  public abstract int getCurrentUploadLimit();
  
  public abstract void setCurrentUploadLimit(int paramInt);
  
  public abstract int getCurrentProtocolDownloadSpeed();
  
  public abstract int getCurrentDataDownloadSpeed();
  
  public abstract int getCurrentDownloadLimit();
  
  public abstract void setCurrentDownloadLimit(int paramInt);
  
  public abstract SpeedManagerPingMapper getPingMapper();
  
  public abstract SpeedManagerPingMapper createTransientPingMapper();
  
  public abstract void log(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/SpeedManagerAlgorithmProviderAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */