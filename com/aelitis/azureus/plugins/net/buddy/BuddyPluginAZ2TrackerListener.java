package com.aelitis.azureus.plugins.net.buddy;

import java.util.Map;

public abstract interface BuddyPluginAZ2TrackerListener
{
  public abstract Map<String, Object> messageReceived(BuddyPluginBuddy paramBuddyPluginBuddy, Map<String, Object> paramMap);
  
  public abstract void messageFailed(BuddyPluginBuddy paramBuddyPluginBuddy, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginAZ2TrackerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */