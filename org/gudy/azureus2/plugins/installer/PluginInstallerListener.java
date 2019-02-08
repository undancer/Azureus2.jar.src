package org.gudy.azureus2.plugins.installer;

import org.gudy.azureus2.plugins.PluginException;

public abstract interface PluginInstallerListener
{
  public abstract boolean installRequest(String paramString, InstallablePlugin paramInstallablePlugin)
    throws PluginException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/installer/PluginInstallerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */