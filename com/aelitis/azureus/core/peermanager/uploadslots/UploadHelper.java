package com.aelitis.azureus.core.peermanager.uploadslots;

import java.util.ArrayList;
import org.gudy.azureus2.core3.peer.PEPeer;

public abstract interface UploadHelper
{
  public static final int PRIORITY_DISABLED = 0;
  public static final int PRIORITY_LOWEST = 1;
  public static final int PRIORITY_LOW = 2;
  public static final int PRIORITY_NORMAL = 4;
  public static final int PRIORITY_HIGH = 8;
  public static final int PRIORITY_HIGHEST = 16;
  
  public abstract int getPriority();
  
  public abstract ArrayList<PEPeer> getAllPeers();
  
  public abstract boolean isSeeding();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/uploadslots/UploadHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */