package org.gudy.azureus2.plugins.ui.components;

public abstract interface UIComponent
{
  public static final String PT_ENABLED = "enabled";
  public static final String PT_VALUE = "value";
  public static final String PT_VISIBLE = "visible";
  public static final String PT_WIDTH_HINT = "whint";
  public static final String PT_HEIGHT_HINT = "hhint";
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean getEnabled();
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract boolean getVisible();
  
  public abstract void setProperty(String paramString, Object paramObject);
  
  public abstract Object getProperty(String paramString);
  
  public abstract void addPropertyChangeListener(UIPropertyChangeListener paramUIPropertyChangeListener);
  
  public abstract void removePropertyChangeListener(UIPropertyChangeListener paramUIPropertyChangeListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/components/UIComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */