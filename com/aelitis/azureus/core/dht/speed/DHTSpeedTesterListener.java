package com.aelitis.azureus.core.dht.speed;

public abstract interface DHTSpeedTesterListener
{
  public abstract void contactAdded(DHTSpeedTesterContact paramDHTSpeedTesterContact);
  
  public abstract void resultGroup(DHTSpeedTesterContact[] paramArrayOfDHTSpeedTesterContact, int[] paramArrayOfInt);
  
  public abstract void destroyed();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/speed/DHTSpeedTesterListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */