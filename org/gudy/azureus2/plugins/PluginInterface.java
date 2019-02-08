package org.gudy.azureus2.plugins;

import java.util.Properties;
import org.gudy.azureus2.plugins.clientid.ClientIDManager;
import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTManager;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.ipc.IPCInterface;
import org.gudy.azureus2.plugins.ipfilter.IPFilter;
import org.gudy.azureus2.plugins.logging.Logger;
import org.gudy.azureus2.plugins.messaging.MessageManager;
import org.gudy.azureus2.plugins.network.ConnectionManager;
import org.gudy.azureus2.plugins.platform.PlatformManager;
import org.gudy.azureus2.plugins.sharing.ShareException;
import org.gudy.azureus2.plugins.sharing.ShareManager;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.tracker.Tracker;
import org.gudy.azureus2.plugins.ui.UIManager;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.Parameter;
import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
import org.gudy.azureus2.plugins.update.UpdateManager;
import org.gudy.azureus2.plugins.utils.ShortCuts;
import org.gudy.azureus2.plugins.utils.Utilities;

public abstract interface PluginInterface
{
  public abstract String getAzureusName();
  
  public abstract String getApplicationName();
  
  public abstract String getAzureusVersion();
  
  /**
   * @deprecated
   */
  public abstract void addConfigUIParameters(Parameter[] paramArrayOfParameter, String paramString);
  
  /**
   * @deprecated
   */
  public abstract void addConfigSection(ConfigSection paramConfigSection);
  
  public abstract void removeConfigSection(ConfigSection paramConfigSection);
  
  public abstract ConfigSection[] getConfigSections();
  
  public abstract Tracker getTracker();
  
  public abstract Logger getLogger();
  
  public abstract IPFilter getIPFilter();
  
  public abstract DownloadManager getDownloadManager();
  
  public abstract ShareManager getShareManager()
    throws ShareException;
  
  public abstract TorrentManager getTorrentManager();
  
  public abstract Utilities getUtilities();
  
  public abstract ShortCuts getShortCuts();
  
  public abstract UIManager getUIManager();
  
  public abstract UpdateManager getUpdateManager();
  
  /**
   * @deprecated
   */
  public abstract void openTorrentFile(String paramString);
  
  /**
   * @deprecated
   */
  public abstract void openTorrentURL(String paramString);
  
  public abstract Properties getPluginProperties();
  
  public abstract String getPluginDirectoryName();
  
  public abstract String getPerUserPluginDirectoryName();
  
  public abstract String getPluginName();
  
  public abstract String getPluginVersion();
  
  public abstract String getPluginID();
  
  /**
   * @deprecated
   */
  public abstract boolean isMandatory();
  
  /**
   * @deprecated
   */
  public abstract boolean isBuiltIn();
  
  public abstract PluginConfig getPluginconfig();
  
  /**
   * @deprecated
   */
  public abstract PluginConfigUIFactory getPluginConfigUIFactory();
  
  public abstract ClassLoader getPluginClassLoader();
  
  public abstract PluginInterface getLocalPluginInterface(Class paramClass, String paramString)
    throws PluginException;
  
  public abstract IPCInterface getIPC();
  
  public abstract Plugin getPlugin();
  
  /**
   * @deprecated
   */
  public abstract boolean isOperational();
  
  /**
   * @deprecated
   */
  public abstract boolean isDisabled();
  
  /**
   * @deprecated
   */
  public abstract void setDisabled(boolean paramBoolean);
  
  /**
   * @deprecated
   */
  public abstract boolean isUnloadable();
  
  /**
   * @deprecated
   */
  public abstract boolean isShared();
  
  /**
   * @deprecated
   */
  public abstract void unload()
    throws PluginException;
  
  /**
   * @deprecated
   */
  public abstract void reload()
    throws PluginException;
  
  /**
   * @deprecated
   */
  public abstract void uninstall()
    throws PluginException;
  
  public abstract boolean isInitialisationThread();
  
  public abstract PluginManager getPluginManager();
  
  public abstract ClientIDManager getClientIDManager();
  
  public abstract ConnectionManager getConnectionManager();
  
  public abstract MessageManager getMessageManager();
  
  public abstract DistributedDatabase getDistributedDatabase();
  
  public abstract PlatformManager getPlatformManager();
  
  public abstract void addListener(PluginListener paramPluginListener);
  
  public abstract void removeListener(PluginListener paramPluginListener);
  
  public abstract void firePluginEvent(PluginEvent paramPluginEvent);
  
  public abstract void addEventListener(PluginEventListener paramPluginEventListener);
  
  public abstract void removeEventListener(PluginEventListener paramPluginEventListener);
  
  public abstract MainlineDHTManager getMainlineDHTManager();
  
  public abstract PluginState getPluginState();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */