package org.gudy.azureus2.ui.swt.mainwindow;

import org.eclipse.swt.graphics.Rectangle;

public abstract interface IMainWindow
{
  public static final int WINDOW_ELEMENT_MENU = 1;
  public static final int WINDOW_ELEMENT_TOOLBAR = 2;
  public static final int WINDOW_ELEMENT_STATUSBAR = 3;
  public static final int WINDOW_ELEMENT_TOPBAR = 4;
  public static final int WINDOW_CLIENT_AREA = 6;
  public static final int WINDOW_CONTENT_DISPLAY_AREA = 7;
  
  public abstract boolean isVisible(int paramInt);
  
  public abstract void setVisible(int paramInt, boolean paramBoolean);
  
  public abstract Rectangle getMetrics(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/IMainWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */