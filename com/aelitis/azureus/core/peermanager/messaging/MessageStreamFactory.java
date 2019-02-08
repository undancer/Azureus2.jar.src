package com.aelitis.azureus.core.peermanager.messaging;

public abstract interface MessageStreamFactory
{
  public abstract MessageStreamEncoder createEncoder();
  
  public abstract MessageStreamDecoder createDecoder();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/MessageStreamFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */