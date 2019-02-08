package com.aelitis.azureus.core.instancemanager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.regex.PatternSyntaxException;

public abstract interface AZInstanceManager
{
  public static final int AT_TCP = 1;
  public static final int AT_UDP = 2;
  public static final int AT_UDP_NON_DATA = 3;
  
  public abstract void initialize();
  
  public abstract boolean isInitialized();
  
  public abstract AZInstance getMyInstance();
  
  public abstract int getOtherInstanceCount(boolean paramBoolean);
  
  public abstract AZInstance[] getOtherInstances();
  
  public abstract void updateNow();
  
  public abstract AZInstanceTracked[] track(byte[] paramArrayOfByte, AZInstanceTracked.TrackTarget paramTrackTarget);
  
  public abstract InetSocketAddress getLANAddress(InetSocketAddress paramInetSocketAddress, int paramInt);
  
  public abstract InetSocketAddress getExternalAddress(InetSocketAddress paramInetSocketAddress, int paramInt);
  
  public abstract boolean isLANAddress(InetAddress paramInetAddress);
  
  public abstract boolean isExternalAddress(InetAddress paramInetAddress);
  
  public abstract boolean addLANSubnet(String paramString)
    throws PatternSyntaxException;
  
  public abstract void addLANAddress(InetAddress paramInetAddress);
  
  public abstract void removeLANAddress(InetAddress paramInetAddress);
  
  public abstract boolean getIncludeWellKnownLANs();
  
  public abstract void setIncludeWellKnownLANs(boolean paramBoolean);
  
  public abstract long getClockSkew();
  
  public abstract boolean addInstance(InetAddress paramInetAddress);
  
  public abstract void addListener(AZInstanceManagerListener paramAZInstanceManagerListener);
  
  public abstract void removeListener(AZInstanceManagerListener paramAZInstanceManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/AZInstanceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */