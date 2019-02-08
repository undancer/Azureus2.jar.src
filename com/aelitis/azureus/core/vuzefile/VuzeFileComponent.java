package com.aelitis.azureus.core.vuzefile;

import java.util.Map;

public abstract interface VuzeFileComponent
{
  public static final int COMP_TYPE_NONE = 0;
  public static final int COMP_TYPE_METASEARCH_TEMPLATE = 1;
  public static final int COMP_TYPE_V3_NAVIGATION = 2;
  public static final int COMP_TYPE_V3_CONDITION_CHECK = 4;
  public static final int COMP_TYPE_PLUGIN = 8;
  public static final int COMP_TYPE_SUBSCRIPTION = 16;
  public static final int COMP_TYPE_SUBSCRIPTION_SINGLETON = 32;
  public static final int COMP_TYPE_CUSTOMIZATION = 64;
  public static final int COMP_TYPE_CONTENT_NETWORK = 128;
  public static final int COMP_TYPE_METASEARCH_OPERATION = 256;
  public static final int COMP_TYPE_DEVICE = 512;
  public static final int COMP_TYPE_CONFIG_SETTINGS = 1024;
  public static final int COMP_TYPE_ADD_TORRENT = 2048;
  
  public abstract int getType();
  
  public abstract String getTypeName();
  
  public abstract Map getContent();
  
  public abstract void setProcessed();
  
  public abstract boolean isProcessed();
  
  public abstract void setData(Object paramObject1, Object paramObject2);
  
  public abstract Object getData(Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/vuzefile/VuzeFileComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */