package org.gudy.azureus2.plugins.utils;

public abstract interface UTTimer
{
  public abstract UTTimerEvent addEvent(long paramLong, UTTimerEventPerformer paramUTTimerEventPerformer);
  
  public abstract UTTimerEvent addPeriodicEvent(long paramLong, UTTimerEventPerformer paramUTTimerEventPerformer);
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/UTTimer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */