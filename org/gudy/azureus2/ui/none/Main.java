/*     */ package org.gudy.azureus2.ui.none;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginManagerDefaults;
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
/*     */ 
/*     */ 
/*     */ public class Main
/*     */ {
/*     */   public static void main(String[] args)
/*     */   {
/*  40 */     System.setProperty("az.factory.internat.bundle", "org.gudy.azureus2.ui.none.internat.MessagesBundle");
/*     */     
/*  42 */     COConfigurationManager.initialise();
/*     */     
/*  44 */     if (System.getProperty("azureus.low.resource.mode", "false").equals("true"))
/*     */     {
/*  46 */       System.out.println("Low resource mode enabled");
/*     */       
/*  48 */       COConfigurationManager.setParameter("Start In Low Resource Mode", true);
/*  49 */       COConfigurationManager.setParameter("DHT.protocol.version.min", 51);
/*     */       
/*  51 */       COConfigurationManager.setParameter("Auto Upload Speed Enabled", false);
/*  52 */       COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", false);
/*     */       
/*  54 */       COConfigurationManager.setParameter("dht.net.cvs_v4.enable", false);
/*  55 */       COConfigurationManager.setParameter("dht.net.main_v6.enable", false);
/*     */       
/*  57 */       COConfigurationManager.setParameter("network.tcp.read.select.time", 500);
/*  58 */       COConfigurationManager.setParameter("network.tcp.read.select.min.time", 500);
/*  59 */       COConfigurationManager.setParameter("network.tcp.write.select.time", 500);
/*  60 */       COConfigurationManager.setParameter("network.tcp.write.select.min.time", 500);
/*  61 */       COConfigurationManager.setParameter("network.tcp.connect.select.time", 500);
/*  62 */       COConfigurationManager.setParameter("network.tcp.connect.select.min.time", 500);
/*     */       
/*  64 */       COConfigurationManager.setParameter("network.udp.poll.time", 100);
/*     */       
/*  66 */       COConfigurationManager.setParameter("network.utp.poll.time", 100);
/*     */       
/*     */ 
/*  69 */       COConfigurationManager.setParameter("network.control.read.idle.time", 100);
/*  70 */       COConfigurationManager.setParameter("network.control.write.idle.time", 100);
/*     */       
/*  72 */       COConfigurationManager.setParameter("diskmanager.perf.cache.enable", true);
/*  73 */       COConfigurationManager.setParameter("diskmanager.perf.cache.size", 4);
/*  74 */       COConfigurationManager.setParameter("diskmanager.perf.cache.enable.read", false);
/*     */       
/*  76 */       COConfigurationManager.setParameter("peermanager.schedule.time", 500);
/*     */       
/*  78 */       PluginManagerDefaults defaults = PluginManager.getDefaults();
/*     */       
/*  80 */       defaults.setDefaultPluginEnabled("Buddy", false);
/*  81 */       defaults.setDefaultPluginEnabled("Share Hoster", false);
/*  82 */       defaults.setDefaultPluginEnabled("RSS", false);
/*  83 */       defaults.setDefaultPluginEnabled("Network Status", false);
/*     */     }
/*     */     
/*     */ 
/*  87 */     String download_dir = System.getProperty("azureus.folder.download", "");
/*     */     
/*  89 */     if (download_dir.length() > 0)
/*     */     {
/*  91 */       File dir = new File(download_dir);
/*     */       
/*  93 */       dir.mkdirs();
/*     */       
/*  95 */       System.out.println("Download directory set to '" + dir + "'");
/*     */       
/*  97 */       COConfigurationManager.setParameter("Default save path", dir.getAbsolutePath());
/*     */     }
/*     */     
/* 100 */     String torrent_dir = System.getProperty("azureus.folder.torrent", "");
/*     */     
/* 102 */     if (torrent_dir.length() > 0)
/*     */     {
/* 104 */       File dir = new File(torrent_dir);
/*     */       
/* 106 */       dir.mkdirs();
/*     */       
/* 108 */       System.out.println("Torrent directory set to '" + dir + "'");
/*     */       
/* 110 */       COConfigurationManager.setParameter("Save Torrent Files", true);
/*     */       
/* 112 */       COConfigurationManager.setParameter("General_sDefaultTorrent_Directory", dir.getAbsolutePath());
/*     */     }
/*     */     
/* 115 */     AzureusCore core = AzureusCoreFactory.create();
/*     */     
/* 117 */     core.start();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/none/Main.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */