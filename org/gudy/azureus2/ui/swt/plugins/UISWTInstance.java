package org.gudy.azureus2.ui.swt.plugins;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
import org.gudy.azureus2.ui.common.UIInstanceBase;

public abstract interface UISWTInstance
  extends UIInstanceBase
{
  public static final String VIEW_MAIN = "Main";
  public static final String VIEW_MYTORRENTS = "MyTorrents";
  public static final String VIEW_TORRENT_DETAILS = "TorrentDetailsView";
  public static final String VIEW_TORRENT_PEERS = "Peers";
  public static final String VIEW_TORRENT_PIECES = "Pieces";
  public static final String VIEW_TORRENT_FILES = "Files";
  public static final String VIEW_TOPBAR = "TopBar";
  public static final String VIEW_STATISTICS = "StatsView";
  public static final String VIEW_CONFIG = "ConfigView";
  public static final String VIEW_SIDEBAR_AREA = "SideBarArea";
  
  public abstract Display getDisplay();
  
  public abstract Image loadImage(String paramString);
  
  public abstract UISWTGraphic createGraphic(Image paramImage);
  
  public abstract void addView(String paramString1, String paramString2, UISWTViewEventListener paramUISWTViewEventListener);
  
  public abstract void addView(String paramString1, String paramString2, Class<? extends UISWTViewEventListener> paramClass, Object paramObject);
  
  public abstract boolean openView(String paramString1, String paramString2, Object paramObject);
  
  public abstract boolean openView(String paramString1, String paramString2, Object paramObject, boolean paramBoolean);
  
  public abstract void openMainView(String paramString, UISWTViewEventListener paramUISWTViewEventListener, Object paramObject);
  
  public abstract void openMainView(String paramString, UISWTViewEventListener paramUISWTViewEventListener, Object paramObject, boolean paramBoolean);
  
  public abstract void removeViews(String paramString1, String paramString2);
  
  public abstract UISWTView[] getOpenViews(String paramString);
  
  public abstract UISWTViewEventListenerWrapper[] getViewListeners(String paramString);
  
  public abstract void showDownloadBar(Download paramDownload, boolean paramBoolean);
  
  public abstract void showTransfersBar(boolean paramBoolean);
  
  public abstract UISWTStatusEntry createStatusEntry();
  
  public abstract boolean openView(BasicPluginViewModel paramBasicPluginViewModel);
  
  public abstract void openConfig(BasicPluginConfigModel paramBasicPluginConfigModel);
  
  public abstract Shell createShell(int paramInt);
  
  public static abstract interface UISWTViewEventListenerWrapper
    extends UISWTViewEventListener
  {
    public abstract String getViewID();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/plugins/UISWTInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */