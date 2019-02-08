package com.aelitis.azureus.core.messenger;

import java.util.Map;

public abstract interface PlatformMessengerListener
{
  public abstract void messageSent(PlatformMessage paramPlatformMessage);
  
  public abstract void replyReceived(PlatformMessage paramPlatformMessage, String paramString, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/PlatformMessengerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */