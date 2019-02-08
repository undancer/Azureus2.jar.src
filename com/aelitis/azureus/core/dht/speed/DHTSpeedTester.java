package com.aelitis.azureus.core.dht.speed;

public abstract interface DHTSpeedTester
{
  public abstract int getContactNumber();
  
  public abstract void setContactNumber(int paramInt);
  
  public abstract void destroy();
  
  public abstract void addListener(DHTSpeedTesterListener paramDHTSpeedTesterListener);
  
  public abstract void removeListener(DHTSpeedTesterListener paramDHTSpeedTesterListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/speed/DHTSpeedTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */