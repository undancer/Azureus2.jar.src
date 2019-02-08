package org.gudy.azureus2.ui.swt.plugins;

import org.gudy.azureus2.plugins.ui.UIInputReceiver;

public abstract interface UISWTInputReceiver
  extends UIInputReceiver
{
  public abstract void setWidthHint(int paramInt);
  
  public abstract void setLineHeight(int paramInt);
  
  public abstract void selectPreenteredText(boolean paramBoolean);
  
  public abstract void setSelectableItems(String[] paramArrayOfString, int paramInt, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/plugins/UISWTInputReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */