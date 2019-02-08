package org.gudy.azureus2.plugins.ui;

import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;

public abstract interface UIInstance
{
  public static final int UIT_SWT = 1;
  public static final int UIT_CONSOLE = 2;
  
  public abstract int getUIType();
  
  public abstract boolean openView(BasicPluginViewModel paramBasicPluginViewModel);
  
  public abstract int promptUser(String paramString1, String paramString2, String[] paramArrayOfString, int paramInt);
  
  public abstract UIInputReceiver getInputReceiver();
  
  public abstract UIMessage createMessage();
  
  public abstract UIToolBarManager getToolBarManager();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */