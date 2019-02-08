package com.aelitis.azureus.core.instancemanager;

import com.aelitis.azureus.plugins.dht.DHTPlugin;
import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
import java.net.InetAddress;

public abstract interface AZInstanceManagerAdapter
{
  public abstract String getID();
  
  public abstract int[] getPorts();
  
  public abstract DHTPlugin getDHTPlugin();
  
  public abstract UPnPPlugin getUPnPPlugin();
  
  public abstract InetAddress getPublicAddress();
  
  public abstract VCPublicAddress getVCPublicAddress();
  
  public abstract AZInstanceTracked.TrackTarget track(byte[] paramArrayOfByte);
  
  public abstract void addListener(StateListener paramStateListener);
  
  public static abstract interface StateListener
  {
    public abstract void started();
    
    public abstract void stopped();
  }
  
  public static abstract interface VCPublicAddress
  {
    public abstract String getAddress();
    
    public abstract long getCacheTime();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/AZInstanceManagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */