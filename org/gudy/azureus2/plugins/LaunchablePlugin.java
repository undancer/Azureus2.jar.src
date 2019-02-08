package org.gudy.azureus2.plugins;

public abstract interface LaunchablePlugin
  extends Plugin
{
  public abstract void setDefaults(String[] paramArrayOfString);
  
  public abstract boolean process();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/LaunchablePlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */