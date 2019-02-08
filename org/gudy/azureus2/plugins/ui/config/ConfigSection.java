package org.gudy.azureus2.plugins.ui.config;

public abstract interface ConfigSection
{
  public static final String SECTION_ROOT = "root";
  public static final String SECTION_PLUGINS = "plugins";
  public static final String SECTION_TRACKER = "tracker";
  public static final String SECTION_FILES = "files";
  public static final String SECTION_INTERFACE = "style";
  public static final String SECTION_CONNECTION = "server";
  public static final String SECTION_TRANSFER = "transfer";
  
  public abstract String configSectionGetParentSection();
  
  public abstract String configSectionGetName();
  
  public abstract void configSectionSave();
  
  public abstract void configSectionDelete();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/ConfigSection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */