package com.aelitis.azureus.plugins.net.buddy;

import java.util.Map;

public abstract interface BuddyPluginBuddyMessageListener
{
  public abstract void messageQueued(BuddyPluginBuddyMessage paramBuddyPluginBuddyMessage);
  
  public abstract boolean deliverySucceeded(BuddyPluginBuddyMessage paramBuddyPluginBuddyMessage, Map paramMap);
  
  public abstract void deliveryFailed(BuddyPluginBuddyMessage paramBuddyPluginBuddyMessage, BuddyPluginException paramBuddyPluginException);
  
  public abstract void messageDeleted(BuddyPluginBuddyMessage paramBuddyPluginBuddyMessage);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddyMessageListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */