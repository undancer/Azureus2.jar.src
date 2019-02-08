package com.aelitis.azureus.core.dht.nat;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import java.net.InetSocketAddress;
import java.util.Map;

public abstract interface DHTNATPuncher
{
  public abstract void start();
  
  public abstract void setSuspended(boolean paramBoolean);
  
  public abstract void destroy();
  
  public abstract boolean active();
  
  public abstract void forceActive(boolean paramBoolean);
  
  public abstract boolean operational();
  
  public abstract DHTTransportContact getLocalContact();
  
  public abstract DHTTransportContact getRendezvous();
  
  public abstract DHTNATPuncher getSecondaryPuncher();
  
  public abstract Map punch(String paramString, DHTTransportContact paramDHTTransportContact, DHTTransportContact[] paramArrayOfDHTTransportContact, Map paramMap);
  
  public abstract Map punch(String paramString, InetSocketAddress[] paramArrayOfInetSocketAddress, DHTTransportContact[] paramArrayOfDHTTransportContact, Map paramMap);
  
  public abstract void setRendezvous(DHTTransportContact paramDHTTransportContact1, DHTTransportContact paramDHTTransportContact2);
  
  public abstract Map sendMessage(InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2, Map paramMap);
  
  public abstract String getStats();
  
  public abstract void addListener(DHTNATPuncherListener paramDHTNATPuncherListener);
  
  public abstract void removeListener(DHTNATPuncherListener paramDHTNATPuncherListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/nat/DHTNATPuncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */