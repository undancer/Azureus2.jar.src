package org.gudy.azureus2.plugins.ui.config;

public abstract interface StringListParameter
  extends Parameter
{
  public abstract void setValue(String paramString);
  
  public abstract String getValue();
  
  public abstract void setLabels(String[] paramArrayOfString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/StringListParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */