package com.aelitis.azureus.plugins.net.buddy;

public abstract interface BuddyPluginAZ2Listener
{
  public abstract void chatCreated(BuddyPluginAZ2.chatInstance paramchatInstance);
  
  public abstract void chatDestroyed(BuddyPluginAZ2.chatInstance paramchatInstance);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginAZ2Listener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */