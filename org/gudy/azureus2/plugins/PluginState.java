package org.gudy.azureus2.plugins;

public abstract interface PluginState
{
  public abstract boolean isLoadedAtStartup();
  
  public abstract void setLoadedAtStartup(boolean paramBoolean);
  
  public abstract boolean hasFailed();
  
  public abstract boolean isDisabled();
  
  public abstract void setDisabled(boolean paramBoolean);
  
  public abstract boolean isBuiltIn();
  
  public abstract boolean isMandatory();
  
  public abstract boolean isOperational();
  
  public abstract boolean isInitialisationComplete();
  
  public abstract void uninstall()
    throws PluginException;
  
  public abstract boolean isShared();
  
  public abstract boolean isUnloadable();
  
  public abstract boolean isUnloaded();
  
  public abstract void unload()
    throws PluginException;
  
  public abstract void reload()
    throws PluginException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */