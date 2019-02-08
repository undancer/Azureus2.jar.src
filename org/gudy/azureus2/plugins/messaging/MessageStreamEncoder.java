package org.gudy.azureus2.plugins.messaging;

import org.gudy.azureus2.plugins.network.RawMessage;

public abstract interface MessageStreamEncoder
{
  public abstract RawMessage encodeMessage(Message paramMessage);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/MessageStreamEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */