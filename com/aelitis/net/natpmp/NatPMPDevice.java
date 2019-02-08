package com.aelitis.net.natpmp;

import java.net.InetAddress;
import java.net.NetworkInterface;

public abstract interface NatPMPDevice
{
  public abstract boolean connect()
    throws Exception;
  
  public abstract int addPortMapping(boolean paramBoolean, int paramInt1, int paramInt2)
    throws Exception;
  
  public abstract void deletePortMapping(boolean paramBoolean, int paramInt1, int paramInt2)
    throws Exception;
  
  public abstract InetAddress getLocalAddress();
  
  public abstract NetworkInterface getNetworkInterface();
  
  public abstract int getEpoch();
  
  public abstract String getExternalIPAddress();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/NatPMPDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */