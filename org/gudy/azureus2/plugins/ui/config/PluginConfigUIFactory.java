package org.gudy.azureus2.plugins.ui.config;

public abstract interface PluginConfigUIFactory
{
  public abstract EnablerParameter createBooleanParameter(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract Parameter createIntParameter(String paramString1, String paramString2, int paramInt);
  
  public abstract Parameter createIntParameter(String paramString1, String paramString2, int paramInt, int[] paramArrayOfInt, String[] paramArrayOfString);
  
  public abstract Parameter createStringParameter(String paramString1, String paramString2, String paramString3);
  
  public abstract Parameter createStringParameter(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString1, String[] paramArrayOfString2);
  
  public abstract Parameter createFileParameter(String paramString1, String paramString2, String paramString3);
  
  public abstract Parameter createDirectoryParameter(String paramString1, String paramString2, String paramString3);
  
  public abstract Parameter createColorParameter(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/PluginConfigUIFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */