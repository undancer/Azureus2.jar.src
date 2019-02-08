package org.gudy.azureus2.plugins;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.gudy.azureus2.plugins.config.ConfigParameter;
import org.gudy.azureus2.plugins.config.PluginConfigSource;

public abstract interface PluginConfig
{
  public static final String CORE_PARAM_INT_MAX_UPLOAD_SPEED_KBYTES_PER_SEC = "Max Upload Speed KBs";
  public static final String CORE_PARAM_INT_MAX_UPLOAD_SPEED_SEEDING_KBYTES_PER_SEC = "Max Upload Speed When Only Seeding KBs";
  public static final String CORE_PARAM_INT_MAX_DOWNLOAD_SPEED_KBYTES_PER_SEC = "Max Download Speed KBs";
  public static final String CORE_PARAM_INT_MAX_CONNECTIONS_PER_TORRENT = "Max Connections Per Torrent";
  public static final String CORE_PARAM_INT_MAX_CONNECTIONS_GLOBAL = "Max Connections Global";
  public static final String CORE_PARAM_INT_MAX_DOWNLOADS = "Max Downloads";
  public static final String CORE_PARAM_INT_MAX_ACTIVE = "Max Active Torrents";
  public static final String CORE_PARAM_INT_MAX_ACTIVE_SEEDING = "Max Active Torrents When Only Seeding";
  public static final String CORE_PARAM_INT_MAX_UPLOADS = "Max Uploads";
  public static final String CORE_PARAM_INT_MAX_UPLOADS_SEEDING = "Max Uploads Seeding";
  public static final String CORE_PARAM_BOOLEAN_AUTO_SPEED_ON = "Auto Upload Speed Enabled";
  public static final String CORE_PARAM_BOOLEAN_AUTO_SPEED_SEEDING_ON = "Auto Upload Speed Seeding Enabled";
  public static final String CORE_PARAM_BOOLEAN_MAX_UPLOAD_SPEED_SEEDING = "Max Upload Speed When Only Seeding Enabled";
  public static final String CORE_PARAM_BOOLEAN_MAX_ACTIVE_SEEDING = "Max Active Torrents When Only Seeding Enabled";
  public static final String CORE_PARAM_BOOLEAN_SOCKS_PROXY_NO_INWARD_CONNECTION = "SOCKS Proxy No Inward Connection";
  public static final String CORE_PARAM_BOOLEAN_NEW_SEEDS_START_AT_TOP = "Newly Seeding Torrents Get First Priority";
  public static final String CORE_PARAM_STRING_LOCAL_BIND_IP = "CORE_PARAM_STRING_LOCAL_BIND_IP";
  public static final String CORE_PARAM_BOOLEAN_FRIENDLY_HASH_CHECKING = "CORE_PARAM_BOOLEAN_FRIENDLY_HASH_CHECKING";
  public static final String GUI_PARAM_INT_SWT_REFRESH_IN_MS = "GUI_PARAM_INT_SWT_REFRESH_IN_MS";
  public static final String CORE_PARAM_BOOLEAN_NEW_TORRENTS_START_AS_STOPPED = "CORE_PARAM_BOOLEAN_NEW_TORRENTS_START_AS_STOPPED";
  public static final String CORE_PARAM_INT_INCOMING_TCP_PORT = "Incoming TCP Port";
  public static final String CORE_PARAM_INT_INCOMING_UDP_PORT = "Incoming UDP Port";
  public static final String CORE_PARAM_STRING_DEFAULT_SAVE_PATH = "Default save path";
  
  /**
   * @deprecated
   */
  public abstract boolean getBooleanParameter(String paramString);
  
  /**
   * @deprecated
   */
  public abstract boolean getBooleanParameter(String paramString, boolean paramBoolean);
  
  /**
   * @deprecated
   */
  public abstract byte[] getByteParameter(String paramString);
  
  /**
   * @deprecated
   */
  public abstract byte[] getByteParameter(String paramString, byte[] paramArrayOfByte);
  
  /**
   * @deprecated
   */
  public abstract float getFloatParameter(String paramString);
  
  /**
   * @deprecated
   */
  public abstract float getFloatParameter(String paramString, float paramFloat);
  
  /**
   * @deprecated
   */
  public abstract int getIntParameter(String paramString);
  
  /**
   * @deprecated
   */
  public abstract int getIntParameter(String paramString, int paramInt);
  
  /**
   * @deprecated
   */
  public abstract long getLongParameter(String paramString);
  
  /**
   * @deprecated
   */
  public abstract long getLongParameter(String paramString, long paramLong);
  
  /**
   * @deprecated
   */
  public abstract String getStringParameter(String paramString);
  
  /**
   * @deprecated
   */
  public abstract String getStringParameter(String paramString1, String paramString2);
  
  /**
   * @deprecated
   */
  public abstract void setBooleanParameter(String paramString, boolean paramBoolean);
  
  /**
   * @deprecated
   */
  public abstract void setByteParameter(String paramString, byte[] paramArrayOfByte);
  
  /**
   * @deprecated
   */
  public abstract void setFloatParameter(String paramString, float paramFloat);
  
  /**
   * @deprecated
   */
  public abstract void setIntParameter(String paramString, int paramInt);
  
  /**
   * @deprecated
   */
  public abstract void setLongParameter(String paramString, long paramLong);
  
  /**
   * @deprecated
   */
  public abstract void setStringParameter(String paramString1, String paramString2);
  
  public abstract boolean getCoreBooleanParameter(String paramString);
  
  public abstract boolean getCoreBooleanParameter(String paramString, boolean paramBoolean);
  
  public abstract byte[] getCoreByteParameter(String paramString);
  
  public abstract byte[] getCoreByteParameter(String paramString, byte[] paramArrayOfByte);
  
  public abstract int[] getCoreColorParameter(String paramString);
  
  public abstract int[] getCoreColorParameter(String paramString, int[] paramArrayOfInt);
  
  public abstract float getCoreFloatParameter(String paramString);
  
  public abstract float getCoreFloatParameter(String paramString, float paramFloat);
  
  public abstract int getCoreIntParameter(String paramString);
  
  public abstract int getCoreIntParameter(String paramString, int paramInt);
  
  public abstract long getCoreLongParameter(String paramString);
  
  public abstract long getCoreLongParameter(String paramString, long paramLong);
  
  public abstract String getCoreStringParameter(String paramString);
  
  public abstract String getCoreStringParameter(String paramString1, String paramString2);
  
  public abstract void setCoreBooleanParameter(String paramString, boolean paramBoolean);
  
  public abstract void setCoreByteParameter(String paramString, byte[] paramArrayOfByte);
  
  public abstract void setCoreColorParameter(String paramString, int[] paramArrayOfInt);
  
  public abstract void setCoreColorParameter(String paramString, int[] paramArrayOfInt, boolean paramBoolean);
  
  public abstract void setCoreFloatParameter(String paramString, float paramFloat);
  
  public abstract void setCoreIntParameter(String paramString, int paramInt);
  
  public abstract void setCoreLongParameter(String paramString, long paramLong);
  
  public abstract void setCoreStringParameter(String paramString1, String paramString2);
  
  public abstract boolean getPluginBooleanParameter(String paramString);
  
  public abstract boolean getPluginBooleanParameter(String paramString, boolean paramBoolean);
  
  public abstract byte[] getPluginByteParameter(String paramString);
  
  public abstract byte[] getPluginByteParameter(String paramString, byte[] paramArrayOfByte);
  
  public abstract int[] getPluginColorParameter(String paramString);
  
  public abstract int[] getPluginColorParameter(String paramString, int[] paramArrayOfInt);
  
  public abstract float getPluginFloatParameter(String paramString);
  
  public abstract float getPluginFloatParameter(String paramString, float paramFloat);
  
  public abstract int getPluginIntParameter(String paramString);
  
  public abstract int getPluginIntParameter(String paramString, int paramInt);
  
  public abstract List getPluginListParameter(String paramString, List paramList);
  
  public abstract long getPluginLongParameter(String paramString);
  
  public abstract long getPluginLongParameter(String paramString, long paramLong);
  
  public abstract Map getPluginMapParameter(String paramString, Map paramMap);
  
  public abstract String getPluginStringParameter(String paramString);
  
  public abstract String getPluginStringParameter(String paramString1, String paramString2);
  
  public abstract String[] getPluginStringListParameter(String paramString);
  
  public abstract void setPluginParameter(String paramString, boolean paramBoolean);
  
  public abstract void setPluginParameter(String paramString, byte[] paramArrayOfByte);
  
  public abstract void setPluginParameter(String paramString, float paramFloat);
  
  public abstract void setPluginParameter(String paramString, int paramInt);
  
  public abstract void setPluginParameter(String paramString, int paramInt, boolean paramBoolean);
  
  public abstract void setPluginParameter(String paramString, long paramLong);
  
  public abstract void setPluginParameter(String paramString1, String paramString2);
  
  public abstract void setPluginStringListParameter(String paramString, String[] paramArrayOfString);
  
  public abstract void setPluginColorParameter(String paramString, int[] paramArrayOfInt);
  
  public abstract void setPluginColorParameter(String paramString, int[] paramArrayOfInt, boolean paramBoolean);
  
  public abstract void setPluginListParameter(String paramString, List paramList);
  
  public abstract void setPluginMapParameter(String paramString, Map paramMap);
  
  public abstract boolean getUnsafeBooleanParameter(String paramString);
  
  public abstract boolean getUnsafeBooleanParameter(String paramString, boolean paramBoolean);
  
  public abstract byte[] getUnsafeByteParameter(String paramString);
  
  public abstract byte[] getUnsafeByteParameter(String paramString, byte[] paramArrayOfByte);
  
  public abstract int[] getUnsafeColorParameter(String paramString);
  
  public abstract int[] getUnsafeColorParameter(String paramString, int[] paramArrayOfInt);
  
  public abstract float getUnsafeFloatParameter(String paramString);
  
  public abstract float getUnsafeFloatParameter(String paramString, float paramFloat);
  
  public abstract int getUnsafeIntParameter(String paramString);
  
  public abstract int getUnsafeIntParameter(String paramString, int paramInt);
  
  public abstract long getUnsafeLongParameter(String paramString);
  
  public abstract long getUnsafeLongParameter(String paramString, long paramLong);
  
  public abstract String getUnsafeStringParameter(String paramString);
  
  public abstract String getUnsafeStringParameter(String paramString1, String paramString2);
  
  public abstract void setUnsafeBooleanParameter(String paramString, boolean paramBoolean);
  
  public abstract void setUnsafeByteParameter(String paramString, byte[] paramArrayOfByte);
  
  public abstract void setUnsafeColorParameter(String paramString, int[] paramArrayOfInt);
  
  public abstract void setUnsafeColorParameter(String paramString, int[] paramArrayOfInt, boolean paramBoolean);
  
  public abstract void setUnsafeFloatParameter(String paramString, float paramFloat);
  
  public abstract void setUnsafeIntParameter(String paramString, int paramInt);
  
  public abstract void setUnsafeLongParameter(String paramString, long paramLong);
  
  public abstract void setUnsafeStringParameter(String paramString1, String paramString2);
  
  public abstract boolean removePluginParameter(String paramString);
  
  public abstract boolean removePluginColorParameter(String paramString);
  
  public abstract String getPluginConfigKeyPrefix();
  
  public abstract ConfigParameter getParameter(String paramString);
  
  public abstract ConfigParameter getPluginParameter(String paramString);
  
  public abstract boolean isNewInstall();
  
  public abstract Map getUnsafeParameterList();
  
  public abstract void save()
    throws PluginException;
  
  public abstract File getPluginUserFile(String paramString);
  
  public abstract boolean hasParameter(String paramString);
  
  public abstract boolean hasPluginParameter(String paramString);
  
  public abstract void addListener(PluginConfigListener paramPluginConfigListener);
  
  public abstract void setPluginConfigKeyPrefix(String paramString);
  
  public abstract PluginConfigSource enableExternalConfigSource();
  
  public abstract PluginConfigSource getPluginConfigSource();
  
  public abstract void setPluginConfigSource(PluginConfigSource paramPluginConfigSource);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */