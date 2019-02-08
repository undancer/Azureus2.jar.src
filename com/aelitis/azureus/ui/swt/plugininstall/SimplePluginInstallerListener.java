package com.aelitis.azureus.ui.swt.plugininstall;

public abstract interface SimplePluginInstallerListener
{
  public abstract void finished();
  
  public abstract void progress(int paramInt);
  
  public abstract void failed(Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/plugininstall/SimplePluginInstallerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */