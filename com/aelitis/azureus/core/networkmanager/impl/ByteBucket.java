package com.aelitis.azureus.core.networkmanager.impl;

public abstract interface ByteBucket
{
  public abstract int getRate();
  
  public abstract void setRate(int paramInt);
  
  public abstract int getAvailableByteCount();
  
  public abstract void setBytesUsed(int paramInt);
  
  public abstract void setFrozen(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ByteBucket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */