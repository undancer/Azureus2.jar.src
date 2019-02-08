package com.aelitis.azureus.plugins.net.buddy;

import java.util.Map;

public abstract interface BuddyPluginAZ2ChatListener
{
  public abstract void messageReceived(BuddyPluginAZ2.chatParticipant paramchatParticipant, Map paramMap);
  
  public abstract void participantAdded(BuddyPluginAZ2.chatParticipant paramchatParticipant);
  
  public abstract void participantChanged(BuddyPluginAZ2.chatParticipant paramchatParticipant);
  
  public abstract void participantRemoved(BuddyPluginAZ2.chatParticipant paramchatParticipant);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginAZ2ChatListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */