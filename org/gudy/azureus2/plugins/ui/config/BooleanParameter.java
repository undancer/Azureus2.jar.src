package org.gudy.azureus2.plugins.ui.config;

public abstract interface BooleanParameter
  extends EnablerParameter
{
  public abstract boolean getValue();
  
  public abstract void setValue(boolean paramBoolean);
  
  public abstract void setDefaultValue(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/BooleanParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */