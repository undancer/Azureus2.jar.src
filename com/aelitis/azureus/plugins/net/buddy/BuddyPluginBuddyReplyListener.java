package com.aelitis.azureus.plugins.net.buddy;

import java.util.Map;

public abstract interface BuddyPluginBuddyReplyListener
{
  public abstract void replyReceived(BuddyPluginBuddy paramBuddyPluginBuddy, Map paramMap);
  
  public abstract void sendFailed(BuddyPluginBuddy paramBuddyPluginBuddy, BuddyPluginException paramBuddyPluginException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddyReplyListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */