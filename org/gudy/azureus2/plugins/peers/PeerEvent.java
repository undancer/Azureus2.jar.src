package org.gudy.azureus2.plugins.peers;

public abstract interface PeerEvent
{
  public static final int ET_STATE_CHANGED = 1;
  public static final int ET_BAD_CHUNK = 2;
  public static final int ET_ADD_AVAILABILITY = 3;
  public static final int ET_REMOVE_AVAILABILITY = 4;
  
  public abstract int getType();
  
  public abstract Object getData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */