package com.aelitis.azureus.core.networkmanager.impl;

import com.aelitis.azureus.core.networkmanager.EventWaiter;
import com.aelitis.azureus.core.networkmanager.RateHandler;

public abstract interface RateControlledEntity
{
  public static final int PRIORITY_NORMAL = 0;
  public static final int PRIORITY_HIGH = 1;
  
  public abstract boolean canProcess(EventWaiter paramEventWaiter);
  
  public abstract int doProcessing(EventWaiter paramEventWaiter, int paramInt);
  
  public abstract int getPriority();
  
  public abstract boolean getPriorityBoost();
  
  public abstract long getBytesReadyToWrite();
  
  public abstract int getConnectionCount(EventWaiter paramEventWaiter);
  
  public abstract int getReadyConnectionCount(EventWaiter paramEventWaiter);
  
  public abstract RateHandler getRateHandler();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/RateControlledEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */