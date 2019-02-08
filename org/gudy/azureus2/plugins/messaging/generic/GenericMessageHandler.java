package org.gudy.azureus2.plugins.messaging.generic;

import org.gudy.azureus2.plugins.messaging.MessageException;

public abstract interface GenericMessageHandler
{
  public abstract boolean accept(GenericMessageConnection paramGenericMessageConnection)
    throws MessageException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/generic/GenericMessageHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */