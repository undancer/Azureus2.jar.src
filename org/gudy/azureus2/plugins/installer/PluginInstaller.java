package org.gudy.azureus2.plugins.installer;

import java.io.File;
import java.util.Map;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.update.UpdateCheckInstance;

public abstract interface PluginInstaller
{
  public abstract StandardPlugin[] getStandardPlugins()
    throws PluginException;
  
  public abstract StandardPlugin getStandardPlugin(String paramString)
    throws PluginException;
  
  public abstract void requestInstall(String paramString, InstallablePlugin paramInstallablePlugin)
    throws PluginException;
  
  public abstract void install(InstallablePlugin[] paramArrayOfInstallablePlugin, boolean paramBoolean)
    throws PluginException;
  
  public abstract UpdateCheckInstance install(InstallablePlugin[] paramArrayOfInstallablePlugin, boolean paramBoolean, Map<Integer, Object> paramMap, PluginInstallationListener paramPluginInstallationListener)
    throws PluginException;
  
  public abstract FilePluginInstaller installFromFile(File paramFile)
    throws PluginException;
  
  public abstract void uninstall(PluginInterface paramPluginInterface)
    throws PluginException;
  
  public abstract void uninstall(PluginInterface[] paramArrayOfPluginInterface)
    throws PluginException;
  
  public abstract void uninstall(PluginInterface[] paramArrayOfPluginInterface, PluginInstallationListener paramPluginInstallationListener)
    throws PluginException;
  
  public abstract UpdateCheckInstance uninstall(PluginInterface[] paramArrayOfPluginInterface, PluginInstallationListener paramPluginInstallationListener, Map<Integer, Object> paramMap)
    throws PluginException;
  
  public abstract void addListener(PluginInstallerListener paramPluginInstallerListener);
  
  public abstract void removeListener(PluginInstallerListener paramPluginInstallerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/installer/PluginInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */