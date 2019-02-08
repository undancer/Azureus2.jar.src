package com.aelitis.azureus.ui.common.updater;

public abstract interface UIUpdater
{
  public abstract void addUpdater(UIUpdatable paramUIUpdatable);
  
  public abstract boolean isAdded(UIUpdatable paramUIUpdatable);
  
  public abstract void removeUpdater(UIUpdatable paramUIUpdatable);
  
  public abstract void stopIt();
  
  public abstract void start();
  
  public abstract void addListener(UIUpdaterListener paramUIUpdaterListener);
  
  public abstract void removeListener(UIUpdaterListener paramUIUpdaterListener);
  
  public static abstract interface UIUpdaterListener
  {
    public abstract void updateComplete(int paramInt);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/updater/UIUpdater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */