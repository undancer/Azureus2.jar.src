package com.aelitis.azureus.core.devices;

public abstract interface TranscodeProviderJob
{
  public abstract void pause();
  
  public abstract void resume();
  
  public abstract void cancel();
  
  public abstract void setMaxBytesPerSecond(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeProviderJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */