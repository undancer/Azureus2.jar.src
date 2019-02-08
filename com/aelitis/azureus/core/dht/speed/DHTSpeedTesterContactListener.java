package com.aelitis.azureus.core.dht.speed;

public abstract interface DHTSpeedTesterContactListener
{
  public abstract void ping(DHTSpeedTesterContact paramDHTSpeedTesterContact, int paramInt);
  
  public abstract void pingFailed(DHTSpeedTesterContact paramDHTSpeedTesterContact);
  
  public abstract void contactDied(DHTSpeedTesterContact paramDHTSpeedTesterContact);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/speed/DHTSpeedTesterContactListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */