package org.gudy.azureus2.ui.swt.plugins;

import org.eclipse.swt.graphics.Image;
import org.gudy.azureus2.plugins.ui.menus.MenuContext;

public abstract interface UISWTStatusEntry
{
  public static final int IMAGE_LED_GREY = 0;
  public static final int IMAGE_LED_RED = 1;
  public static final int IMAGE_LED_YELLOW = 2;
  public static final int IMAGE_LED_GREEN = 3;
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract void setText(String paramString);
  
  public abstract void setTooltipText(String paramString);
  
  public abstract void setListener(UISWTStatusEntryListener paramUISWTStatusEntryListener);
  
  public abstract void setImageEnabled(boolean paramBoolean);
  
  public abstract void setImage(int paramInt);
  
  public abstract void setImage(Image paramImage);
  
  public abstract void destroy();
  
  public abstract MenuContext getMenuContext();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/plugins/UISWTStatusEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */