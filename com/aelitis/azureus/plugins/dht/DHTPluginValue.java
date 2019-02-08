package com.aelitis.azureus.plugins.dht;

public abstract interface DHTPluginValue
{
  public abstract byte[] getValue();
  
  public abstract long getCreationTime();
  
  public abstract long getVersion();
  
  public abstract boolean isLocal();
  
  public abstract int getFlags();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPluginValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */