package com.aelitis.azureus.ui.skin;

import java.util.ResourceBundle;

public abstract interface SkinProperties
{
  public abstract void addProperty(String paramString1, String paramString2);
  
  public abstract int getIntValue(String paramString, int paramInt);
  
  public abstract String getStringValue(String paramString);
  
  public abstract String getStringValue(String paramString1, String paramString2);
  
  public abstract String[] getStringArray(String paramString);
  
  public abstract String getStringValue(String paramString, String[] paramArrayOfString);
  
  public abstract String getStringValue(String paramString1, String[] paramArrayOfString, String paramString2);
  
  public abstract String[] getStringArray(String paramString, String[] paramArrayOfString);
  
  public abstract int[] getColorValue(String paramString);
  
  public abstract boolean getBooleanValue(String paramString, boolean paramBoolean);
  
  public abstract void clearCache();
  
  public abstract boolean hasKey(String paramString);
  
  public abstract String getReferenceID(String paramString);
  
  public abstract void addResourceBundle(ResourceBundle paramResourceBundle, String paramString);
  
  public abstract void addResourceBundle(ResourceBundle paramResourceBundle, String paramString, ClassLoader paramClassLoader);
  
  public abstract ClassLoader getClassLoader();
  
  public abstract int getEmHeightPX();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/skin/SkinProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */