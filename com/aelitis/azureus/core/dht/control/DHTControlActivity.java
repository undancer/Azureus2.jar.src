package com.aelitis.azureus.core.dht.control;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import java.util.List;

public abstract interface DHTControlActivity
{
  public static final int AT_INTERNAL_GET = 1;
  public static final int AT_EXTERNAL_GET = 2;
  public static final int AT_INTERNAL_PUT = 3;
  public static final int AT_EXTERNAL_PUT = 4;
  
  public abstract byte[] getTarget();
  
  public abstract String getDescription();
  
  public abstract int getType();
  
  public abstract boolean isQueued();
  
  public abstract ActivityState getCurrentState();
  
  public abstract String getString();
  
  public static abstract interface ActivityNode
  {
    public abstract DHTTransportContact getContact();
    
    public abstract List<ActivityNode> getChildren();
  }
  
  public static abstract interface ActivityState
  {
    public abstract DHTControlActivity.ActivityNode getRootNode();
    
    public abstract int getDepth();
    
    public abstract String getResult();
    
    public abstract String getString();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/DHTControlActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */