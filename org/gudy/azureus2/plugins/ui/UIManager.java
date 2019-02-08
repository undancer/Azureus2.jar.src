package org.gudy.azureus2.plugins.ui;

import java.io.File;
import java.net.URL;
import java.util.Map;
import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.ui.menus.MenuManager;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
import org.gudy.azureus2.plugins.ui.model.PluginConfigModel;
import org.gudy.azureus2.plugins.ui.tables.TableManager;

public abstract interface UIManager
{
  public static final String MB_PARAM_REMEMBER_ID = "remember-id";
  public static final String MB_PARAM_REMEMBER_BY_DEF = "remember-by-def";
  public static final String MB_PARAM_REMEMBER_RES = "remember-res";
  public static final String MB_PARAM_AUTO_CLOSE_MS = "auto-close-ms";
  
  public abstract BasicPluginConfigModel createBasicPluginConfigModel(String paramString);
  
  public abstract BasicPluginConfigModel createBasicPluginConfigModel(String paramString1, String paramString2);
  
  public abstract PluginConfigModel[] getPluginConfigModels();
  
  public abstract BasicPluginViewModel createBasicPluginViewModel(String paramString);
  
  public abstract BasicPluginViewModel createLoggingViewModel(LoggerChannel paramLoggerChannel, boolean paramBoolean);
  
  public abstract void copyToClipBoard(String paramString)
    throws UIException;
  
  public abstract TableManager getTableManager();
  
  public abstract void showTextMessage(String paramString1, String paramString2, String paramString3);
  
  public abstract long showMessageBox(String paramString1, String paramString2, long paramLong);
  
  public abstract long showMessageBox(String paramString1, String paramString2, long paramLong, Object[] paramArrayOfObject);
  
  public abstract long showMessageBox(String paramString1, String paramString2, long paramLong, Map<String, Object> paramMap);
  
  public abstract void openURL(URL paramURL)
    throws UIException;
  
  public abstract void openTorrent(Torrent paramTorrent);
  
  public abstract boolean showConfigSection(String paramString);
  
  public abstract MenuManager getMenuManager();
  
  public abstract void attachUI(UIInstanceFactory paramUIInstanceFactory)
    throws UIException;
  
  public abstract void detachUI(UIInstanceFactory paramUIInstanceFactory)
    throws UIException;
  
  public abstract void addUIListener(UIManagerListener paramUIManagerListener);
  
  public abstract void removeUIListener(UIManagerListener paramUIManagerListener);
  
  public abstract void addUIEventListener(UIManagerEventListener paramUIManagerEventListener);
  
  public abstract void removeUIEventListener(UIManagerEventListener paramUIManagerEventListener);
  
  public abstract boolean hasUIInstances();
  
  public abstract UIInstance[] getUIInstances();
  
  public abstract UIInputReceiver getInputReceiver();
  
  public abstract UIMessage createMessage();
  
  public abstract void openFile(File paramFile);
  
  public abstract void showFile(File paramFile);
  
  public abstract void addDataSourceListener(UIDataSourceListener paramUIDataSourceListener, boolean paramBoolean);
  
  public abstract void removeDataSourceListener(UIDataSourceListener paramUIDataSourceListener);
  
  public abstract Object getDataSource();
  
  public abstract void setEverythingHidden(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */