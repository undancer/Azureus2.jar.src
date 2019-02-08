package com.aelitis.azureus.core.networkmanager;

public abstract interface RateHandler
{
  public abstract int[] getCurrentNumBytesAllowed();
  
  public abstract void bytesProcessed(int paramInt1, int paramInt2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/RateHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */