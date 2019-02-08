package com.aelitis.azureus.core.devices;

public abstract interface TranscodeTargetListener
{
  public static final int CT_PROPERTY = 1;
  
  public abstract void fileAdded(TranscodeFile paramTranscodeFile);
  
  public abstract void fileChanged(TranscodeFile paramTranscodeFile, int paramInt, Object paramObject);
  
  public abstract void fileRemoved(TranscodeFile paramTranscodeFile);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeTargetListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */