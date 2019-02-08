package com.aelitis.azureus.plugins.dht;

public abstract interface DHTPluginTransferHandler
{
  public abstract String getName();
  
  public abstract byte[] handleRead(DHTPluginContact paramDHTPluginContact, byte[] paramArrayOfByte);
  
  public abstract byte[] handleWrite(DHTPluginContact paramDHTPluginContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPluginTransferHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */