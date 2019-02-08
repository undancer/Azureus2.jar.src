package com.aelitis.azureus.plugins.dht;

import java.net.InetSocketAddress;
import java.util.Map;

public abstract interface DHTPluginContact
{
  public abstract byte[] getID();
  
  public abstract String getName();
  
  public abstract InetSocketAddress getAddress();
  
  public abstract byte getProtocolVersion();
  
  public abstract int getNetwork();
  
  public abstract Map<String, Object> exportToMap();
  
  public abstract boolean isAlive(long paramLong);
  
  public abstract void isAlive(long paramLong, DHTPluginOperationListener paramDHTPluginOperationListener);
  
  public abstract boolean isOrHasBeenLocal();
  
  public abstract Map openTunnel();
  
  public abstract byte[] read(DHTPluginProgressListener paramDHTPluginProgressListener, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long paramLong);
  
  public abstract void write(DHTPluginProgressListener paramDHTPluginProgressListener, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long paramLong);
  
  public abstract byte[] call(DHTPluginProgressListener paramDHTPluginProgressListener, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long paramLong);
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPluginContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */