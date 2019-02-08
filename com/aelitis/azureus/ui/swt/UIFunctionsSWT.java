package com.aelitis.azureus.ui.swt;

import com.aelitis.azureus.ui.UIFunctions;
import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
import com.aelitis.azureus.ui.swt.mdi.TabbedMdiInterface;
import java.util.Map;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;
import org.gudy.azureus2.ui.swt.mainwindow.IMainMenu;
import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar;
import org.gudy.azureus2.ui.swt.mainwindow.IMainWindow;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
import org.gudy.azureus2.ui.swt.plugins.UISWTView;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;

public abstract interface UIFunctionsSWT
  extends UIFunctions
{
  public abstract Shell getMainShell();
  
  public abstract void addPluginView(String paramString, UISWTViewEventListener paramUISWTViewEventListener);
  
  public abstract void closeDownloadBars();
  
  public abstract boolean isGlobalTransferBarShown();
  
  public abstract void showGlobalTransferBar();
  
  public abstract void closeGlobalTransferBar();
  
  public abstract UISWTView[] getPluginViews();
  
  public abstract void openPluginView(String paramString1, String paramString2, UISWTViewEventListener paramUISWTViewEventListener, Object paramObject, boolean paramBoolean);
  
  public abstract void removePluginView(String paramString);
  
  public abstract void closePluginView(UISWTViewCore paramUISWTViewCore);
  
  public abstract void closePluginViews(String paramString);
  
  public abstract UISWTInstance getUISWTInstance();
  
  public abstract void refreshTorrentMenu();
  
  public abstract IMainStatusBar getMainStatusBar();
  
  public abstract IMainMenu createMainMenu(Shell paramShell);
  
  public abstract IMainWindow getMainWindow();
  
  public abstract void closeAllDetails();
  
  public abstract boolean hasDetailViews();
  
  public abstract Shell showCoreWaitDlg();
  
  public abstract MultipleDocumentInterfaceSWT getMDISWT();
  
  public abstract void promptForSearch();
  
  public abstract UIToolBarManager getToolBarManager();
  
  public abstract void setHideAll(boolean paramBoolean);
  
  public abstract void openTorrentWindow();
  
  public abstract void openTorrentOpenOptions(Shell paramShell, String paramString, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void openTorrentOpenOptions(Shell paramShell, String paramString, String[] paramArrayOfString, Map<String, Object> paramMap);
  
  public abstract TabbedMdiInterface createTabbedMDI(Composite paramComposite, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/UIFunctionsSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */