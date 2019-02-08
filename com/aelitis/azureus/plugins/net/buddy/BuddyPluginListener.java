package com.aelitis.azureus.plugins.net.buddy;

public abstract interface BuddyPluginListener
{
  public abstract void initialised(boolean paramBoolean);
  
  public abstract void buddyAdded(BuddyPluginBuddy paramBuddyPluginBuddy);
  
  public abstract void buddyRemoved(BuddyPluginBuddy paramBuddyPluginBuddy);
  
  public abstract void buddyChanged(BuddyPluginBuddy paramBuddyPluginBuddy);
  
  public abstract void messageLogged(String paramString, boolean paramBoolean);
  
  public abstract void enabledStateChanged(boolean paramBoolean);
  
  public abstract void updated();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */