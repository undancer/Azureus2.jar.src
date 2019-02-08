package com.aelitis.azureus.core.instancemanager;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public abstract interface AZInstance
{
  public abstract String getID();
  
  public abstract String getApplicationID();
  
  public abstract InetAddress getInternalAddress();
  
  public abstract List getInternalAddresses();
  
  public abstract InetAddress getExternalAddress();
  
  public abstract int getTCPListenPort();
  
  public abstract int getUDPListenPort();
  
  public abstract int getUDPNonDataListenPort();
  
  public abstract Map<String, Object> getProperties();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/AZInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */