package com.aelitis.azureus.core.devices;

public abstract interface TranscodeManager
{
  public abstract TranscodeProvider[] getProviders();
  
  public abstract TranscodeQueue getQueue();
  
  public abstract void addListener(TranscodeManagerListener paramTranscodeManagerListener);
  
  public abstract void removeListener(TranscodeManagerListener paramTranscodeManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */