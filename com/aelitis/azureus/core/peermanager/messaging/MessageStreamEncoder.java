package com.aelitis.azureus.core.peermanager.messaging;

import com.aelitis.azureus.core.networkmanager.RawMessage;

public abstract interface MessageStreamEncoder
{
  public abstract RawMessage[] encodeMessage(Message paramMessage);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/MessageStreamEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */