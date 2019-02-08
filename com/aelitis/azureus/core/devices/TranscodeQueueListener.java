package com.aelitis.azureus.core.devices;

public abstract interface TranscodeQueueListener
{
  public abstract void jobAdded(TranscodeJob paramTranscodeJob);
  
  public abstract void jobChanged(TranscodeJob paramTranscodeJob);
  
  public abstract void jobRemoved(TranscodeJob paramTranscodeJob);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeQueueListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */