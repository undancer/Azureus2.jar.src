package com.aelitis.azureus.plugins.dht;

public abstract interface DHTPluginOperationListener
{
  public abstract void starts(byte[] paramArrayOfByte);
  
  public abstract boolean diversified();
  
  public abstract void valueRead(DHTPluginContact paramDHTPluginContact, DHTPluginValue paramDHTPluginValue);
  
  public abstract void valueWritten(DHTPluginContact paramDHTPluginContact, DHTPluginValue paramDHTPluginValue);
  
  public abstract void complete(byte[] paramArrayOfByte, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPluginOperationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */