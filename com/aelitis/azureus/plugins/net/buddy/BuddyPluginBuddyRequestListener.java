package com.aelitis.azureus.plugins.net.buddy;

import java.util.Map;

public abstract interface BuddyPluginBuddyRequestListener
{
  public abstract Map requestReceived(BuddyPluginBuddy paramBuddyPluginBuddy, int paramInt, Map paramMap)
    throws BuddyPluginException;
  
  public abstract void pendingMessages(BuddyPluginBuddy[] paramArrayOfBuddyPluginBuddy);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddyRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */