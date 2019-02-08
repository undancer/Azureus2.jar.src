package com.aelitis.azureus.plugins.net.buddy;

public class BuddyPluginAdapter
  implements BuddyPluginListener
{
  public void initialised(boolean available) {}
  
  public void buddyAdded(BuddyPluginBuddy buddy) {}
  
  public void buddyRemoved(BuddyPluginBuddy buddy) {}
  
  public void buddyChanged(BuddyPluginBuddy buddy) {}
  
  public void messageLogged(String str, boolean is_error) {}
  
  public void enabledStateChanged(boolean enabled) {}
  
  public void updated() {}
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */