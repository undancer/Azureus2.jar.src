package com.aelitis.azureus.plugins.net.buddy.tracker;

public abstract interface BuddyPluginTrackerListener
{
  public abstract void enabledStateChanged(BuddyPluginTracker paramBuddyPluginTracker, boolean paramBoolean);
  
  public abstract void networkStatusChanged(BuddyPluginTracker paramBuddyPluginTracker, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/tracker/BuddyPluginTrackerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */