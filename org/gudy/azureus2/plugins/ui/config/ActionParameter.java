package org.gudy.azureus2.plugins.ui.config;

public abstract interface ActionParameter
  extends Parameter
{
  public static final int STYLE_BUTTON = 1;
  public static final int STYLE_LINK = 2;
  
  public abstract void setStyle(int paramInt);
  
  public abstract int getStyle();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/ActionParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */