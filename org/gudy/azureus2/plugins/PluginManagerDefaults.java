/*    */ package org.gudy.azureus2.plugins;
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface PluginManagerDefaults
/*    */ {
/*    */   public static final String PID_START_STOP_RULES = "Start/Stop Rules";
/*    */   
/*    */ 
/*    */   public static final String PID_REMOVE_RULES = "Torrent Removal Rules";
/*    */   
/*    */ 
/*    */   public static final String PID_SHARE_HOSTER = "Share Hoster";
/*    */   
/*    */ 
/*    */   public static final String PID_DEFAULT_TRACKER_WEB = "Default Tracker Web";
/*    */   
/*    */ 
/*    */   public static final String PID_PLUGIN_UPDATE_CHECKER = "Plugin Update Checker";
/*    */   
/*    */ 
/*    */   public static final String PID_CORE_UPDATE_CHECKER = "Core Update Checker";
/*    */   
/*    */ 
/*    */   public static final String PID_CORE_PATCH_CHECKER = "Core Patch Checker";
/*    */   
/*    */ 
/*    */   public static final String PID_PLATFORM_CHECKER = "Platform Checker";
/*    */   
/*    */ 
/*    */   public static final String PID_UPNP = "UPnP";
/*    */   
/*    */ 
/*    */   public static final String PID_DHT = "DHT";
/*    */   
/*    */ 
/*    */   public static final String PID_DHT_TRACKER = "DHT Tracker";
/*    */   
/*    */ 
/*    */   public static final String PID_MAGNET = "Magnet URI Handler";
/*    */   
/*    */   public static final String PID_EXTERNAL_SEED = "External Seed";
/*    */   
/*    */   public static final String PID_LOCAL_TRACKER = "Local Tracker";
/*    */   
/*    */   public static final String PID_TRACKER_PEER_AUTH = "Tracker Peer Auth";
/*    */   
/*    */   public static final String PID_NET_STATUS = "Network Status";
/*    */   
/*    */   public static final String PID_BUDDY = "Buddy";
/*    */   
/*    */   public static final String PID_RSS = "RSS";
/*    */   
/* 54 */   public static final String[] PLUGIN_IDS = { "Start/Stop Rules", "Torrent Removal Rules", "Share Hoster", "Default Tracker Web", "Core Update Checker", "Core Patch Checker", "Platform Checker", "UPnP", "DHT", "DHT Tracker", "Magnet URI Handler", "External Seed", "Local Tracker", "Tracker Peer Auth", "Network Status", "Buddy", "RSS" };
/*    */   
/*    */   public abstract String[] getDefaultPlugins();
/*    */   
/*    */   public abstract void setDefaultPluginEnabled(String paramString, boolean paramBoolean);
/*    */   
/*    */   public abstract boolean isDefaultPluginEnabled(String paramString);
/*    */   
/*    */   public abstract void setApplicationName(String paramString);
/*    */   
/*    */   public abstract String getApplicationName();
/*    */   
/*    */   public abstract void setApplicationIdentifier(String paramString);
/*    */   
/*    */   public abstract String getApplicationIdentifier();
/*    */   
/*    */   public abstract void setApplicationEntryPoint(String paramString);
/*    */   
/*    */   public abstract String getApplicationEntryPoint();
/*    */   
/*    */   public abstract void setSingleInstanceHandler(int paramInt, PluginManagerArgumentHandler paramPluginManagerArgumentHandler);
/*    */   
/*    */   public abstract boolean setSingleInstanceHandlerAndProcess(int paramInt, PluginManagerArgumentHandler paramPluginManagerArgumentHandler, String[] paramArrayOfString);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginManagerDefaults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */