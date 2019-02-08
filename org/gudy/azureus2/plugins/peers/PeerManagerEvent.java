package org.gudy.azureus2.plugins.peers;

public abstract interface PeerManagerEvent
{
  public static final int ET_PEER_ADDED = 1;
  public static final int ET_PEER_REMOVED = 2;
  public static final int ET_PEER_DISCOVERED = 3;
  public static final int ET_PEER_SENT_BAD_DATA = 4;
  public static final int ET_PIECE_ACTIVATED = 5;
  public static final int ET_PIECE_DEACTIVATED = 6;
  public static final int ET_PIECE_COMPLETION_CHANGED = 7;
  
  public abstract PeerManager getPeerManager();
  
  public abstract int getType();
  
  public abstract Peer getPeer();
  
  public abstract PeerDescriptor getPeerDescriptor();
  
  public abstract Object getData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerManagerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */