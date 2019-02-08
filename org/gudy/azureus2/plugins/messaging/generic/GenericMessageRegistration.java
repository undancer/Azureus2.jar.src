package org.gudy.azureus2.plugins.messaging.generic;

import java.net.InetSocketAddress;
import org.gudy.azureus2.plugins.messaging.MessageException;

public abstract interface GenericMessageRegistration
{
  public abstract GenericMessageEndpoint createEndpoint(InetSocketAddress paramInetSocketAddress);
  
  public abstract GenericMessageConnection createConnection(GenericMessageEndpoint paramGenericMessageEndpoint)
    throws MessageException;
  
  public abstract void cancel();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/generic/GenericMessageRegistration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */