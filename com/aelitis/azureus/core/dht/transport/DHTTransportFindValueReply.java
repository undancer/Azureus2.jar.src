package com.aelitis.azureus.core.dht.transport;

public abstract interface DHTTransportFindValueReply
{
  public abstract byte getDiversificationType();
  
  public abstract boolean hit();
  
  public abstract DHTTransportValue[] getValues();
  
  public abstract DHTTransportContact[] getContacts();
  
  public abstract boolean blocked();
  
  public abstract byte[] getBlockedKey();
  
  public abstract byte[] getBlockedSignature();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportFindValueReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */