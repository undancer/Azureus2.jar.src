package com.aelitis.azureus.core.custom;

import java.io.File;
import java.io.InputStream;

public abstract interface Customization
{
  public static final String RT_META_SEARCH_TEMPLATES = "metasearch";
  public static final String RT_SUBSCRIPTIONS = "subs";
  public static final String RT_CNETWORKS = "cnetworks";
  
  public abstract String getName();
  
  public abstract String getVersion();
  
  public abstract Object getProperty(String paramString);
  
  public abstract boolean isActive();
  
  public abstract void setActive(boolean paramBoolean);
  
  public abstract InputStream getResource(String paramString);
  
  public abstract InputStream[] getResources(String paramString);
  
  public abstract void exportToVuzeFile(File paramFile)
    throws CustomizationException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/custom/Customization.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */