package com.aelitis.azureus.core.dht.speed;

import java.net.InetSocketAddress;

public abstract interface DHTSpeedTesterContact
{
  public abstract InetSocketAddress getAddress();
  
  public abstract int getPingPeriod();
  
  public abstract void setPingPeriod(int paramInt);
  
  public abstract void destroy();
  
  public abstract void addListener(DHTSpeedTesterContactListener paramDHTSpeedTesterContactListener);
  
  public abstract void removeListener(DHTSpeedTesterContactListener paramDHTSpeedTesterContactListener);
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/speed/DHTSpeedTesterContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */