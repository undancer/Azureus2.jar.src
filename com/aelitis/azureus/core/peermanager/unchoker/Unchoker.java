package com.aelitis.azureus.core.peermanager.unchoker;

import java.util.ArrayList;
import org.gudy.azureus2.core3.peer.PEPeer;

public abstract interface Unchoker
{
  public abstract boolean isSeedingUnchoker();
  
  public abstract ArrayList<PEPeer> getImmediateUnchokes(int paramInt, ArrayList<PEPeer> paramArrayList);
  
  public abstract void calculateUnchokes(int paramInt, ArrayList<PEPeer> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  public abstract ArrayList<PEPeer> getChokes();
  
  public abstract ArrayList<PEPeer> getUnchokes();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/unchoker/Unchoker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */