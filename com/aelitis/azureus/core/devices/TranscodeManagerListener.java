package com.aelitis.azureus.core.devices;

public abstract interface TranscodeManagerListener
{
  public abstract void providerAdded(TranscodeProvider paramTranscodeProvider);
  
  public abstract void providerUpdated(TranscodeProvider paramTranscodeProvider);
  
  public abstract void providerRemoved(TranscodeProvider paramTranscodeProvider);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */