/*     */ package com.aelitis.azureus.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationChecker;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationParameterNotFoundException;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
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
/*     */ public class UIConfigDefaultsSWTv3
/*     */ {
/*     */   public static void initialize(AzureusCore core)
/*     */   {
/*  38 */     ConfigurationManager config = ConfigurationManager.getInstance();
/*     */     
/*  40 */     if ("az2".equalsIgnoreCase(config.getStringParameter("ui", "az3"))) {
/*  41 */       return;
/*     */     }
/*     */     
/*  44 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  45 */     boolean startAdvanced = userMode > 1;
/*     */     
/*  47 */     boolean configNeedsSave = false;
/*     */     
/*  49 */     if (System.getProperty("FORCE_PROGRESSIVE", "").length() > 0) {
/*  50 */       config.setParameter("Prioritize First Piece", true);
/*  51 */       configNeedsSave = true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  56 */     String sFirstVersion = config.getStringParameter("First Recorded Version");
/*     */     
/*  58 */     ConfigurationDefaults defaults = ConfigurationDefaults.getInstance();
/*     */     
/*  60 */     defaults.addParameter("ui", "az3");
/*     */     
/*     */ 
/*     */ 
/*  64 */     if (Constants.compareVersions(sFirstVersion, "2.5.0.0") == 0) {
/*  65 */       String sDefSavePath = config.getStringParameter("Default save path");
/*     */       
/*  67 */       System.out.println(sDefSavePath);
/*  68 */       String sDefPath = null;
/*     */       try {
/*  70 */         sDefPath = defaults.getStringParameter("Default save path");
/*     */       } catch (ConfigurationParameterNotFoundException e) {
/*  72 */         e.printStackTrace();
/*     */       }
/*  74 */       if (sDefPath != null) {
/*  75 */         File fNewPath = new File(sDefPath);
/*     */         
/*  77 */         if ((sDefSavePath != null) && (fNewPath.equals(new File(sDefSavePath)))) {
/*  78 */           sFirstVersion = "3.0.0.5";
/*  79 */           config.setParameter("First Recorded Version", sFirstVersion);
/*  80 */           configNeedsSave = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  86 */     boolean immediateSwitch = config.getBooleanParameter("az3.switch.immediate", false);
/*     */     
/*  88 */     if ((Constants.compareVersions(sFirstVersion, "3.0.0.0") >= 0) || (immediateSwitch))
/*     */     {
/*     */ 
/*  91 */       if ((!config.isNewInstall()) && (Constants.compareVersions(sFirstVersion, "3.0.0.4") < 0))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  96 */         String userPath = SystemProperties.getUserPath();
/*  97 */         File fOldPath = new File(userPath, "data");
/*  98 */         String sDefSavePath = config.getStringParameter("Default save path");
/*     */         
/* 100 */         String sDefPath = "";
/*     */         try {
/* 102 */           sDefPath = defaults.getStringParameter("Default save path");
/*     */         }
/*     */         catch (ConfigurationParameterNotFoundException e) {}
/* 105 */         File fNewPath = new File(sDefPath);
/*     */         
/* 107 */         if ((sDefSavePath != null) && (fNewPath.equals(new File(sDefSavePath)))) {
/* 108 */           sFirstVersion = "3.0.0.5";
/* 109 */           config.setParameter("First Recorded Version", sFirstVersion);
/* 110 */           configNeedsSave = true;
/* 111 */         } else { if ((sDefSavePath == null) || (!fOldPath.equals(new File(sDefSavePath))))
/*     */           {
/* 113 */             sFirstVersion = "2.5.0.0";
/* 114 */             config.setParameter("First Recorded Version", sFirstVersion);
/* 115 */             config.save();
/* 116 */             return;
/*     */           }
/*     */           
/*     */ 
/* 120 */           config.removeParameter("Default save path");
/*     */         }
/*     */       }
/*     */       
/* 124 */       defaults.addParameter("Auto Upload Speed Enabled", true);
/* 125 */       defaults.addParameter("ui.addtorrent.openoptions", startAdvanced ? "always" : "many");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */       defaults.addParameter("Popup Download Finished", false);
/* 134 */       defaults.addParameter("Popup Download Added", false);
/*     */       
/* 136 */       defaults.addParameter("Status Area Show SR", false);
/* 137 */       defaults.addParameter("Status Area Show NAT", false);
/* 138 */       defaults.addParameter("Status Area Show IPF", false);
/* 139 */       defaults.addParameter("Status Area Show RIP", true);
/*     */       
/* 141 */       defaults.addParameter("Message Popup Autoclose in Seconds", 10);
/*     */       
/* 143 */       defaults.addParameter("window.maximized", true);
/*     */       
/* 145 */       defaults.addParameter("update.autodownload", true);
/*     */       
/*     */ 
/*     */ 
/* 149 */       defaults.addParameter("auto_remove_inactive_items", false);
/*     */       
/* 151 */       defaults.addParameter("show_torrents_menu", false);
/*     */     }
/*     */     
/*     */ 
/* 155 */     defaults.addParameter("v3.topbar.show.frog", false);
/* 156 */     config.removeParameter("v3.home-tab.starttab");
/* 157 */     defaults.addParameter("MyTorrentsView.table.style", 0);
/* 158 */     defaults.addParameter("v3.Show Welcome", true);
/*     */     
/* 160 */     defaults.addParameter("Library.viewmode", startAdvanced ? 1 : 0);
/* 161 */     defaults.addParameter("LibraryDL.viewmode", startAdvanced ? 1 : 0);
/* 162 */     defaults.addParameter("LibraryUnopened.viewmode", startAdvanced ? 1 : 0);
/* 163 */     defaults.addParameter("LibraryCD.viewmode", startAdvanced ? 1 : 0);
/* 164 */     defaults.addParameter("Library.EnableSimpleView", 1);
/* 165 */     defaults.addParameter("Library.CatInSideBar", startAdvanced ? 1 : 0);
/* 166 */     defaults.addParameter("Library.TagInSideBar", 1);
/* 167 */     defaults.addParameter("Library.ShowTabsInTorrentView", 1);
/* 168 */     defaults.addParameter("list.dm.dblclick", "0");
/*     */     
/*     */ 
/* 171 */     defaults.addParameter("vista.adminquit", false);
/* 172 */     defaults.addParameter("Start Minimized", false);
/* 173 */     defaults.addParameter("Password enabled", false);
/* 174 */     defaults.addParameter("ToolBar.showText", true);
/* 175 */     defaults.addParameter("burninfo.shownonce", false);
/*     */     
/* 177 */     defaults.addParameter("Table.extendedErase", !Constants.isWindowsXP);
/* 178 */     defaults.addParameter("Table.useTree", true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 183 */     core.addLifecycleListener(new AzureusCoreLifecycleAdapter() {
/*     */       public void started(AzureusCore core) {
/* 185 */         this.val$defaults.addParameter("Plugin.DHT.dht.warn.user", false);
/* 186 */         this.val$defaults.addParameter("Plugin.UPnP.upnp.alertothermappings", false);
/* 187 */         this.val$defaults.addParameter("Plugin.UPnP.upnp.alertdeviceproblems", false);
/*     */       }
/*     */     });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 195 */     if ((!COConfigurationManager.hasParameter("v3.StartTab", true)) || ((ConfigurationChecker.isNewVersion()) && (Constants.compareVersions(Constants.getBaseVersion(), "4.2.1.0") == 0)))
/*     */     {
/*     */ 
/*     */ 
/* 199 */       Map<?, ?> map = FileUtil.readResilientConfigFile("tables.config");
/* 200 */       if ((map != null) && (map.size() > 0)) {
/* 201 */         Object[] keys = map.keySet().toArray();
/* 202 */         boolean removedSome = false;
/* 203 */         for (int i = 0; i < keys.length; i++) {
/* 204 */           if ((keys[i] instanceof String)) {
/* 205 */             String sKey = (String)keys[i];
/* 206 */             if ((sKey.endsWith(".big")) || (sKey.startsWith("Table.library-")) || (sKey.startsWith("Table.Media")) || (sKey.startsWith("Table.activity.table")) || (sKey.equals("Table.Activity.big")) || (sKey.equals("Table.Activity_SB")))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 211 */               map.remove(sKey);
/* 212 */               removedSome = true;
/*     */             }
/*     */           }
/*     */         }
/* 216 */         if (removedSome) {
/* 217 */           FileUtil.writeResilientConfigFile("tables.config", map);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 223 */     if (!config.getBooleanParameter("IconBar.enabled")) {
/* 224 */       config.setParameter("IconBar.enabled", true);
/*     */     }
/*     */     
/*     */ 
/* 228 */     if (configNeedsSave) {
/* 229 */       config.save();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/UIConfigDefaultsSWTv3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */