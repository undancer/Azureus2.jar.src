package org.gudy.azureus2.plugins.messaging;

import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.messaging.generic.GenericMessageHandler;
import org.gudy.azureus2.plugins.messaging.generic.GenericMessageRegistration;

public abstract interface MessageManager
{
  public static final int STREAM_ENCRYPTION_NONE = 1;
  public static final int STREAM_ENCRYPTION_RC4_PREFERRED = 2;
  public static final int STREAM_ENCRYPTION_RC4_REQUIRED = 3;
  
  public abstract void registerMessageType(Message paramMessage)
    throws MessageException;
  
  public abstract void deregisterMessageType(Message paramMessage);
  
  public abstract void locateCompatiblePeers(PluginInterface paramPluginInterface, Message paramMessage, MessageManagerListener paramMessageManagerListener);
  
  public abstract void cancelCompatiblePeersLocation(MessageManagerListener paramMessageManagerListener);
  
  public abstract GenericMessageRegistration registerGenericMessageType(String paramString1, String paramString2, int paramInt, GenericMessageHandler paramGenericMessageHandler)
    throws MessageException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/MessageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */