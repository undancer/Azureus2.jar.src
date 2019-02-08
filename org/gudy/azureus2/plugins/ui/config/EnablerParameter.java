package org.gudy.azureus2.plugins.ui.config;

public abstract interface EnablerParameter
  extends Parameter
{
  public abstract void addEnabledOnSelection(Parameter paramParameter);
  
  public abstract void addDisabledOnSelection(Parameter paramParameter);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/EnablerParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */