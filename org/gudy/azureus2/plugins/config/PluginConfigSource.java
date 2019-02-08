package org.gudy.azureus2.plugins.config;

import java.io.File;

public abstract interface PluginConfigSource
{
  public abstract void initialize();
  
  public abstract void setConfigFilename(String paramString);
  
  public abstract File getConfigFile();
  
  public abstract void save(boolean paramBoolean);
  
  public abstract void forceSettingsMigration();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/config/PluginConfigSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */