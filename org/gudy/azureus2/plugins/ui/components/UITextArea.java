package org.gudy.azureus2.plugins.ui.components;

public abstract interface UITextArea
  extends UIComponent
{
  public static final int DEFAULT_MAX_SIZE = 60000;
  
  public abstract void setText(String paramString);
  
  public abstract void appendText(String paramString);
  
  public abstract String getText();
  
  public abstract void setMaximumSize(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/components/UITextArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */