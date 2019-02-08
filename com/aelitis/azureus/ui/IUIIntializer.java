package com.aelitis.azureus.ui;

public abstract interface IUIIntializer
{
  public abstract void stopIt(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void run();
  
  public abstract void addListener(InitializerListener paramInitializerListener);
  
  public abstract void removeListener(InitializerListener paramInitializerListener);
  
  public abstract void increaseProgress();
  
  public abstract void abortProgress();
  
  public abstract void reportCurrentTask(String paramString);
  
  public abstract void reportPercent(int paramInt);
  
  public abstract void initializationComplete();
  
  public abstract void runInSWTThread();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/IUIIntializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */