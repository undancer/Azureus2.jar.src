package com.aelitis.azureus.ui.swt.shells.main;

import com.aelitis.azureus.core.AzureusCore;
import org.eclipse.swt.widgets.Shell;
import org.gudy.azureus2.ui.swt.mainwindow.IMainMenu;
import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar;
import org.gudy.azureus2.ui.swt.mainwindow.IMainWindow;
import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTInstanceImpl;

public abstract interface MainWindow
  extends IMainWindow
{
  public abstract void init(AzureusCore paramAzureusCore);
  
  public abstract Shell getShell();
  
  public abstract IMainMenu getMainMenu();
  
  public abstract IMainStatusBar getMainStatusBar();
  
  public abstract boolean isReady();
  
  public abstract void setVisible(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract UISWTInstanceImpl getUISWTInstanceImpl();
  
  public abstract void setSelectedLanguageItem();
  
  public abstract void setHideAll(boolean paramBoolean);
  
  public abstract boolean dispose(boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/MainWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */