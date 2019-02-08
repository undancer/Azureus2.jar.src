package com.aelitis.azureus.core.devices;

public abstract interface TranscodeProviderAdapter
{
  public abstract void updateProgress(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void streamStats(long paramLong1, long paramLong2);
  
  public abstract void failed(TranscodeException paramTranscodeException);
  
  public abstract void complete();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeProviderAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */