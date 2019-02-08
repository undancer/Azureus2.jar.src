package org.gudy.azureus2.plugins.config;

public abstract interface ConfigParameter
{
  public abstract void addConfigParameterListener(ConfigParameterListener paramConfigParameterListener);
  
  public abstract void removeConfigParameterListener(ConfigParameterListener paramConfigParameterListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/config/ConfigParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */