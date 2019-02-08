package com.aelitis.azureus.plugins.dht;

public abstract interface DHTPluginKeyStats
{
  public abstract int getEntryCount();
  
  public abstract int getSize();
  
  public abstract int getReadsPerMinute();
  
  public abstract byte getDiversification();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPluginKeyStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */