package org.gudy.azureus2.plugins;

public abstract interface PluginEvent
{
  public static final int PEV_CONFIGURATION_WIZARD_STARTS = 1;
  public static final int PEV_CONFIGURATION_WIZARD_COMPLETES = 2;
  public static final int PEV_INITIALISATION_PROGRESS_TASK = 3;
  public static final int PEV_INITIALISATION_PROGRESS_PERCENT = 4;
  public static final int PEV_INITIAL_SHARING_COMPLETE = 5;
  public static final int PEV_INITIALISATION_UI_COMPLETES = 6;
  public static final int PEV_ALL_PLUGINS_INITIALISED = 7;
  public static final int PEV_PLUGIN_OPERATIONAL = 8;
  public static final int PEV_PLUGIN_NOT_OPERATIONAL = 9;
  public static final int PEV_PLUGIN_INSTALLED = 10;
  public static final int PEV_PLUGIN_UPDATED = 11;
  public static final int PEV_PLUGIN_UNINSTALLED = 12;
  public static final int PEV_FIRST_USER_EVENT = 1024;
  
  public abstract int getType();
  
  public abstract Object getValue();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */