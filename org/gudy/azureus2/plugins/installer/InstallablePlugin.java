package org.gudy.azureus2.plugins.installer;

import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;

public abstract interface InstallablePlugin
{
  public abstract String getId();
  
  public abstract String getVersion();
  
  public abstract String getName();
  
  public abstract String getDescription();
  
  public abstract String getRelativeURLBase();
  
  public abstract PluginInterface getAlreadyInstalledPlugin();
  
  public abstract boolean isAlreadyInstalled();
  
  public abstract void install(boolean paramBoolean)
    throws PluginException;
  
  public abstract void install(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws PluginException;
  
  public abstract void uninstall()
    throws PluginException;
  
  public abstract PluginInstaller getInstaller();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/installer/InstallablePlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */