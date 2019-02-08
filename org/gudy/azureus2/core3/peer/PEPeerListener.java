package org.gudy.azureus2.core3.peer;

import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;

public abstract interface PEPeerListener
{
  public abstract void stateChanged(PEPeer paramPEPeer, int paramInt);
  
  public abstract void sentBadChunk(PEPeer paramPEPeer, int paramInt1, int paramInt2);
  
  public abstract void addAvailability(PEPeer paramPEPeer, BitFlags paramBitFlags);
  
  public abstract void removeAvailability(PEPeer paramPEPeer, BitFlags paramBitFlags);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */