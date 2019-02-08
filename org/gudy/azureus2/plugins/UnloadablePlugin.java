package org.gudy.azureus2.plugins;

public abstract interface UnloadablePlugin
  extends Plugin
{
  public abstract void unload()
    throws PluginException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/UnloadablePlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */