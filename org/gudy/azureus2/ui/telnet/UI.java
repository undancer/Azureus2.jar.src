/*     */ package org.gudy.azureus2.ui.telnet;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.ui.common.IUserInterface;
/*     */ import org.gudy.azureus2.ui.common.UIConst;
/*     */ import org.gudy.azureus2.ui.common.UITemplateHeadless;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ import org.gudy.azureus2.ui.console.UserProfile;
/*     */ import org.gudy.azureus2.ui.console.multiuser.MultiUserConsoleInput;
/*     */ import org.gudy.azureus2.ui.console.multiuser.UserManager;
/*     */ import org.gudy.azureus2.ui.console.multiuser.commands.UserCommand;
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
/*     */ public class UI
/*     */   extends UITemplateHeadless
/*     */   implements IUserInterface
/*     */ {
/*     */   private UserManager userManager;
/*     */   
/*     */   public String[] processArgs(String[] args)
/*     */   {
/*  58 */     return args;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startUI()
/*     */   {
/*  67 */     if (!isStarted()) {
/*     */       try
/*     */       {
/*  70 */         int telnetPort = COConfigurationManager.getIntParameter("Telnet_iPort", 57006);
/*  71 */         String allowedHostsStr = COConfigurationManager.getStringParameter("Telnet_sAllowedHosts", "127.0.0.1,titan");
/*  72 */         StringTokenizer st = new StringTokenizer(allowedHostsStr, ",");
/*  73 */         Set allowedHosts = new HashSet();
/*  74 */         while (st.hasMoreTokens())
/*  75 */           allowedHosts.add(st.nextToken().toLowerCase());
/*  76 */         int maxLoginAttempts = COConfigurationManager.getIntParameter("Telnet_iMaxLoginAttempts", 3);
/*  77 */         this.userManager = initUserManager();
/*  78 */         Thread thread = new Thread(new SocketServer(this, telnetPort, allowedHosts, this.userManager, maxLoginAttempts), "Telnet Socket Server Thread");
/*  79 */         thread.setDaemon(true);
/*  80 */         thread.start();
/*     */       } catch (IOException e) {
/*  82 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  85 */     super.startUI();
/*     */     
/*  87 */     TorrentDownloaderFactory.initManager(UIConst.getGlobalManager(), true, true, COConfigurationManager.getStringParameter("Default save path"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private UserManager initUserManager()
/*     */   {
/*  95 */     if (System.getProperty("azureus.console.multiuser") != null) {
/*  96 */       return UserManager.getInstance(UIConst.getAzureusCore().getPluginManager().getDefaultPluginInterface());
/*     */     }
/*  98 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void openTorrent(String fileName)
/*     */   {
/* 105 */     if (fileName.toUpperCase().startsWith("HTTP://")) {
/* 106 */       System.out.println("Downloading torrent from url: " + fileName);
/* 107 */       TorrentDownloaderFactory.downloadManaged(fileName);
/* 108 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 112 */       if (!TorrentUtils.isTorrentFile(fileName)) {
/* 113 */         Logger.getLogger("azureus2.ui.telnet").error(fileName + " doesn't seem to be a torrent file. Not added.");
/* 114 */         return;
/*     */       }
/*     */     } catch (Exception e) {
/* 117 */       Logger.getLogger("azureus2.ui.telnet").error("Something is wrong with " + fileName + ". Not added. (Reason: " + e.getMessage() + ")");
/* 118 */       return;
/*     */     }
/* 120 */     if (UIConst.getGlobalManager() != null) {
/*     */       try {
/* 122 */         UIConst.getGlobalManager().addDownloadManager(fileName, COConfigurationManager.getDirectoryParameter("Default save path"));
/*     */       } catch (Exception e) {
/* 124 */         Logger.getLogger("azureus2.ui.telnet").error("The torrent " + fileName + " could not be added.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void createNewConsoleInput(String consoleName, InputStream inputStream, PrintStream outputStream, UserProfile profile)
/*     */   {
/*     */     ConsoleInput console;
/*     */     
/*     */ 
/*     */     ConsoleInput console;
/*     */     
/*     */ 
/* 140 */     if (this.userManager != null)
/*     */     {
/* 142 */       MultiUserConsoleInput muc = new MultiUserConsoleInput(consoleName, UIConst.getAzureusCore(), new InputStreamReader(inputStream), outputStream, Boolean.FALSE, profile);
/* 143 */       muc.registerCommand(new UserCommand(this.userManager));
/* 144 */       console = muc;
/*     */     }
/*     */     else
/*     */     {
/* 148 */       console = new ConsoleInput(consoleName, UIConst.getAzureusCore(), new InputStreamReader(inputStream), outputStream, Boolean.FALSE, profile);
/*     */       
/* 150 */       System.out.println("TelnetUI: console input instantiated");
/*     */     }
/* 152 */     console.printwelcome();
/* 153 */     console.printconsolehelp();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/telnet/UI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */