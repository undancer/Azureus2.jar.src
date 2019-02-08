/*     */ package org.gudy.azureus2.ui.common;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.ui.common.util.LegacyHashtable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExternalUIConst
/*     */ {
/*  34 */   public static Hashtable parameterlegacy = null;
/*  35 */   private static boolean defaultsNotRegistered = true;
/*     */   
/*     */   static {
/*  38 */     parameterlegacy = new LegacyHashtable();
/*     */     
/*     */ 
/*     */ 
/*  42 */     parameterlegacy.put("max_up", "Max Upload Speed KBs");
/*  43 */     parameterlegacy.put("max_down", "Max Download Speed KBs");
/*     */     
/*  45 */     parameterlegacy.put("General_sDefaultSave_Directory", "Default save path");
/*  46 */     parameterlegacy.put("Core_sOverrideIP", "Override Ip");
/*     */     
/*  48 */     parameterlegacy.put("Core_iTCPListenPort", "TCP.Listen.Port");
/*     */     
/*     */ 
/*  51 */     parameterlegacy.put("Core_iMaxActiveTorrents", "max active torrents");
/*  52 */     parameterlegacy.put("Core_iMaxDownloads", "max downloads");
/*     */     
/*  54 */     parameterlegacy.put("Core_iMaxUploads", "Max Uploads");
/*  55 */     parameterlegacy.put("Core_iMaxUploadSpeed", "Max Upload Speed KBs");
/*  56 */     parameterlegacy.put("Core_bUseResume", "Use Resume");
/*  57 */     parameterlegacy.put("Core_iSaveResumeInterval", "Save Resume Interval");
/*  58 */     parameterlegacy.put("Core_bIncrementalAllocate", "Enable incremental file creation");
/*  59 */     parameterlegacy.put("Core_bCheckPiecesOnCompletion", "Check Pieces on Completion");
/*  60 */     parameterlegacy.put("Core_fSeedingShareStop", "Stop Ratio");
/*  61 */     parameterlegacy.put("StartStopManager_bIgnoreRatioPeers", "Stop Peers Ratio");
/*  62 */     parameterlegacy.put("Core_iSeedingRatioStart", "Start Peers Ratio");
/*  63 */     parameterlegacy.put("Core_bDisconnectSeed", "Disconnect Seed");
/*  64 */     parameterlegacy.put("Core_bSwitchPriority", "Switch Priority");
/*  65 */     parameterlegacy.put("Core_bSlowConnect", "Slow Connect");
/*  66 */     parameterlegacy.put("Core_sPriorityExtensions", "priorityExtensions");
/*  67 */     parameterlegacy.put("Core_bPriorityExtensionsIgnoreCase", "priorityExtensionsIgnoreCase");
/*  68 */     parameterlegacy.put("Core_bIpFilterEnabled", "Ip Filter Enabled");
/*  69 */     parameterlegacy.put("Core_bIpFilterAllow", "Ip Filter Allow");
/*  70 */     parameterlegacy.put("Core_bAllowSameIPPeers", "Allow Same IP Peers");
/*  71 */     parameterlegacy.put("Core_bUseSuperSeeding", "Use Super Seeding");
/*  72 */     parameterlegacy.put("Core_iMaxPeerConnectionsPerTorrent", "Max.Peer.Connections.Per.Torrent");
/*  73 */     parameterlegacy.put("Core_iMaxPeerConnectionsTotal", "Max.Peer.Connections.Total");
/*  74 */     parameterlegacy.put("SWT_bUseCustomTab", "useCustomTab");
/*  75 */     parameterlegacy.put("SWT_iGUIRefresh", "GUI Refresh");
/*  76 */     parameterlegacy.put("SWT_iGraphicsUpdate", "Graphics Update");
/*  77 */     parameterlegacy.put("SWT_iReOrderDelay", "ReOrder Delay");
/*  78 */     parameterlegacy.put("SWT_bSendVersionInfo", "Send Version Info");
/*  79 */     parameterlegacy.put("SWT_bShowDownloadBasket", "Show Download Basket");
/*  80 */     parameterlegacy.put("SWT_bAlwaysRefreshMyTorrents", "config.style.refreshMT");
/*  81 */     parameterlegacy.put("SWT_bOpenDetails", "Open Details");
/*  82 */     parameterlegacy.put("SWT_bProgressBarColorOverride", "Colors.progressBar.override");
/*  83 */     parameterlegacy.put("Plugin_sConfig_Directory", "Plugin.config.directory");
/*  84 */     parameterlegacy.put("Plugin_bConfigEnable", "Plugin.config.enable");
/*  85 */     parameterlegacy.put("Plugin_iConfigIntlist", "Plugin.config.intlist");
/*  86 */     parameterlegacy.put("Plugin_sConfigLogfile", "Plugin.config.logfile");
/*  87 */     parameterlegacy.put("Plugin_sConfigNick", "Plugin.config.nick");
/*  88 */     parameterlegacy.put("Plugin_iConfigPortBlue", "Plugin.config.port.blue");
/*  89 */     parameterlegacy.put("Plugin_iConfigPortGreen", "Plugin.config.port.green");
/*  90 */     parameterlegacy.put("Plugin_iConfigPortRed", "Plugin.config.port.red");
/*  91 */     parameterlegacy.put("Plugin_iConfigPort", "Plugin.config.port");
/*  92 */     parameterlegacy.put("Plugin_sConfigStringlist", "Plugin.config.stringlist");
/*  93 */     parameterlegacy.put("Logger_bEnable", "Logging Enable");
/*  94 */     parameterlegacy.put("Logger_sDir_Directory", "Logging Dir");
/*  95 */     parameterlegacy.put("Logger_iMaxSize", "Logging Max Size");
/*     */     
/*  97 */     parameterlegacy.put("Tracker_Password_Enable", "Tracker Password Enable Web");
/*  98 */     parameterlegacy.put("Tracker_UserName", "Tracker Username");
/*  99 */     parameterlegacy.put("Tracker_Password", "Tracker Password");
/*     */     
/* 101 */     int[] logComponents = { 0, 1, 2, 4 };
/* 102 */     for (int i = 0; i < logComponents.length; i++)
/* 103 */       for (int j = 0; j <= 3; j++)
/* 104 */         parameterlegacy.put("Logger_bLog" + logComponents[i] + "-" + j, "bLog" + logComponents[i] + "-" + j);
/*     */   }
/*     */   
/*     */   public static void registerDefaults() {
/* 108 */     HashMap def = new HashMap();
/* 109 */     if (defaultsNotRegistered) {
/* 110 */       defaultsNotRegistered = false;
/*     */       
/*     */ 
/* 113 */       def.put("Server_sName", "Azureus2 WebInterface");
/*     */       
/* 115 */       def.put("Server_sBindIP", "");
/*     */       
/* 117 */       def.put("Server_iPort", new Long(8088L));
/*     */       
/* 119 */       def.put("Server_iTimeout", new Long(10L));
/*     */       
/* 121 */       def.put("Server_sTemplate_Directory", SystemProperties.getUserPath() + "template");
/*     */       
/* 123 */       def.put("Server_iMaxHTTPConnections", new Long(5L));
/*     */       
/* 125 */       def.put("Server_iRefresh", new Long(20L));
/*     */       
/* 127 */       def.put("Server_sAllowStatic", "127.0.0.1");
/*     */       
/* 129 */       def.put("Server_sAllowDynamic", "");
/*     */       
/* 131 */       def.put("Server_iRecheckDynamic", new Long(30L));
/*     */       
/* 133 */       def.put("Server_bNoJavaScript", new Long(0L));
/*     */       
/*     */ 
/*     */ 
/* 137 */       def.put("Server_sAccessHost", "torrent");
/*     */       
/* 139 */       def.put("Server_bProxyEnableCookies", new Long(1L));
/*     */       
/* 141 */       def.put("Server_bProxyBlockURLs", new Long(0L));
/*     */       
/* 143 */       def.put("Server_bProxyFilterHTTP", new Long(0L));
/*     */       
/* 145 */       def.put("Server_sProxyUserAgent", "Mozilla/4.0 (compatible; MSIE 4.0; WindowsNT 5.0)");
/*     */       
/* 147 */       def.put("Server_bUseDownstreamProxy", new Long(0L));
/*     */       
/* 149 */       def.put("Server_sDownstreamProxyHost", "127.0.0.1");
/*     */       
/* 151 */       def.put("Server_iDownstreamProxyPort", new Long(0L));
/*     */       
/* 153 */       def.put("Server_bProxyGrabTorrents", new Long(1L));
/*     */       
/* 155 */       def.put("Server_sProxySuccessRedirect", "torrents");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 170 */       def.put("Server_bLogFile", new Long(0L));
/*     */       
/* 172 */       def.put("Server_sLogFile", SystemProperties.getUserPath() + "webinterface.log");
/*     */       
/* 174 */       def.put("Server_iLogLevelWebinterface", new Long(20000L));
/*     */       
/* 176 */       def.put("Server_iLogLevelCore", new Long(20000L));
/*     */       
/* 178 */       def.put("Server_iLogCount", new Long(200L));
/* 179 */       COConfigurationManager.registerExternalDefaults(def);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/ExternalUIConst.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */