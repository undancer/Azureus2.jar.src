package com.aelitis.azureus.core.devices;

public abstract interface TranscodeQueueActionListener
{
  public static final int ACT_REMOVE = 1;
  
  public abstract void jobWillBeActioned(TranscodeJob paramTranscodeJob, int paramInt)
    throws TranscodeActionVetoException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeQueueActionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */