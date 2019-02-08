package org.gudy.azureus2.plugins.peers;

/**
 * @deprecated
 */
public abstract interface PeerListener
{
  public abstract void stateChanged(int paramInt);
  
  public abstract void sentBadChunk(int paramInt1, int paramInt2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */