/*     */ package org.gudy.azureus2.ui.console;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIException;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstanceFactory;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerEvent;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerEventListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIMessage;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;
/*     */ import org.gudy.azureus2.ui.common.IUserInterface;
/*     */ import org.gudy.azureus2.ui.common.UIConst;
/*     */ import org.gudy.azureus2.ui.common.UIInstanceBase;
/*     */ import org.gudy.azureus2.ui.common.UITemplateHeadless;
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
/*     */ public class UI
/*     */   extends UITemplateHeadless
/*     */   implements IUserInterface, UIInstanceFactory, UIInstanceBase, UIManagerEventListener
/*     */ {
/*  43 */   private ConsoleInput console = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(boolean first, boolean others)
/*     */   {
/*  50 */     super.init(first, others);
/*  51 */     System.setProperty("java.awt.headless", "true");
/*     */   }
/*     */   
/*     */   public String[] processArgs(String[] args) {
/*  55 */     return args;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUIType()
/*     */   {
/*  61 */     return 2;
/*     */   }
/*     */   
/*     */   public void startUI() {
/*  65 */     super.startUI();
/*     */     
/*  67 */     boolean created_console = false;
/*     */     
/*  69 */     if ((!isStarted()) || (this.console == null) || (!this.console.isAlive()))
/*     */     {
/*  71 */       System.out.println();
/*     */       
/*  73 */       PrintStream this_out = System.out;
/*     */       
/*     */ 
/*  76 */       if (!"on".equals(System.getProperty("azureus.console.noisy")))
/*     */       {
/*     */ 
/*  79 */         PrintStream ps = new PrintStream(new OutputStream() { public void write(int c) {}
/*     */           
/*     */           public void write(byte[] b, int i1, int i2) {}
/*  82 */         });
/*  83 */         System.setOut(ps);
/*  84 */         System.setErr(ps);
/*  85 */         org.gudy.azureus2.core3.logging.Logger.allowLoggingToStdErr(false);
/*     */       }
/*     */       
/*  88 */       this.console = new ConsoleInput("Main", UIConst.getAzureusCore(), System.in, this_out, Boolean.TRUE);
/*  89 */       this.console.printwelcome();
/*  90 */       this.console.printconsolehelp();
/*  91 */       created_console = true;
/*     */     }
/*     */     
/*  94 */     PluginInterface pi = UIConst.getAzureusCore().getPluginManager().getDefaultPluginInterface();
/*  95 */     UIManager ui_manager = pi.getUIManager();
/*     */     
/*  97 */     ui_manager.addUIEventListener(this);
/*     */     try
/*     */     {
/* 100 */       ui_manager.attachUI(this);
/*     */     } catch (UIException e) {
/* 102 */       e.printStackTrace();
/*     */     }
/* 104 */     TorrentDownloaderFactory.initManager(UIConst.getGlobalManager(), true, true, COConfigurationManager.getStringParameter("Default save path"));
/*     */     
/* 106 */     if ((created_console) && (System.getProperty("azureus.console.multiuser") != null)) {
/* 107 */       UserManager manager = UserManager.getInstance(pi);
/* 108 */       this.console.registerCommand(new UserCommand(manager));
/*     */     }
/*     */   }
/*     */   
/*     */   public void openRemoteTorrent(String url)
/*     */   {
/* 114 */     if (this.console != null) {
/* 115 */       this.console.downloadRemoteTorrent(url);
/* 116 */       return;
/*     */     }
/* 118 */     if (this.console != null) {
/* 119 */       this.console.out.println("Downloading torrent from url: " + url);
/*     */     }
/* 121 */     TorrentDownloaderFactory.downloadManaged(url);
/*     */   }
/*     */   
/*     */   public void openTorrent(String fileName)
/*     */   {
/* 126 */     String uc_filename = fileName.toUpperCase(Locale.US);
/*     */     
/* 128 */     boolean is_remote = (uc_filename.startsWith("HTTP://")) || (uc_filename.startsWith("HTTPS://")) || (uc_filename.startsWith("MAGNET:"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 133 */     if (this.console != null)
/*     */     {
/*     */ 
/*     */ 
/* 137 */       if (is_remote)
/*     */       {
/* 139 */         this.console.out.println("Downloading torrent from url: " + fileName);
/*     */         
/* 141 */         this.console.downloadRemoteTorrent(fileName);
/*     */       }
/*     */       else {
/* 144 */         this.console.downloadTorrent(fileName);
/*     */       }
/* 146 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 151 */     if (is_remote) {
/* 152 */       if (this.console != null) {
/* 153 */         this.console.out.println("Downloading torrent from url: " + fileName);
/*     */       }
/* 155 */       TorrentDownloaderFactory.downloadManaged(fileName);
/* 156 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 160 */       if (!TorrentUtils.isTorrentFile(fileName)) {
/* 161 */         org.apache.log4j.Logger.getLogger("azureus2.ui.console").error(fileName + " doesn't seem to be a torrent file. Not added.");
/* 162 */         return;
/*     */       }
/*     */     } catch (Exception e) {
/* 165 */       org.apache.log4j.Logger.getLogger("azureus2.ui.console").error("Something is wrong with " + fileName + ". Not added. (Reason: " + e.getMessage() + ")");
/* 166 */       return;
/*     */     }
/* 168 */     if (UIConst.getGlobalManager() != null) {
/*     */       try {
/* 170 */         String downloadDir = COConfigurationManager.getDirectoryParameter("Default save path");
/* 171 */         if (this.console != null) {
/* 172 */           this.console.out.println("Adding torrent: " + fileName + " and saving to " + downloadDir);
/*     */         }
/* 174 */         UIConst.getGlobalManager().addDownloadManager(fileName, downloadDir);
/*     */       } catch (Exception e) {
/* 176 */         org.apache.log4j.Logger.getLogger("azureus2.ui.console").error("The torrent " + fileName + " could not be added.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UIInstance getInstance(PluginInterface plugin_interface)
/*     */   {
/* 185 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void detach()
/*     */     throws UIException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UIManagerEvent event)
/*     */   {
/* 199 */     Object data = event.getData();
/*     */     
/* 201 */     switch (event.getType())
/*     */     {
/*     */ 
/*     */     case 1: 
/* 205 */       String[] bits = (String[])data;
/*     */       
/* 207 */       for (int i = 0; i < bits.length; i++)
/*     */       {
/* 209 */         this.console.out.println(bits[i]);
/*     */       }
/*     */       
/* 212 */       break;
/*     */     
/*     */ 
/*     */     case 2: 
/* 216 */       openTorrent(((File)data).toString());
/*     */       
/* 218 */       break;
/*     */     
/*     */ 
/*     */     case 3: 
/* 222 */       openRemoteTorrent(((URL)((Object[])(Object[])data)[0]).toExternalForm());
/*     */       
/* 224 */       break;
/*     */     
/*     */ 
/*     */     case 4: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 5: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 6: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 7: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 8: 
/*     */       break;
/*     */     
/*     */ 
/*     */ 
/*     */     case 9: 
/*     */       break;
/*     */     
/*     */ 
/*     */ 
/*     */     case 10: 
/* 254 */       return false;
/*     */     
/*     */ 
/*     */     case 11: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 12: 
/*     */       break;
/*     */     
/*     */ 
/*     */ 
/*     */     case 13: 
/* 267 */       event.setResult(Boolean.FALSE);
/*     */       
/* 269 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 279 */     return true;
/*     */   }
/*     */   
/*     */   public int promptUser(String title, String text, String[] options, int defaultOption)
/*     */   {
/* 284 */     this.console.out.println("Prompt: " + title);
/* 285 */     this.console.out.println(text);
/*     */     
/* 287 */     String sOptions = "Options: ";
/* 288 */     for (int i = 0; i < options.length; i++) {
/* 289 */       if (i != 0) {
/* 290 */         sOptions = sOptions + ", ";
/*     */       }
/* 292 */       sOptions = sOptions + "[" + i + "]" + options[i];
/*     */     }
/*     */     
/* 295 */     this.console.out.println(sOptions);
/*     */     
/* 297 */     this.console.out.println("WARNING: Option [" + defaultOption + "] automatically selected. " + "Console UI devs need to implement this function!");
/*     */     
/*     */ 
/*     */ 
/* 301 */     return defaultOption;
/*     */   }
/*     */   
/*     */   public boolean openView(BasicPluginViewModel model)
/*     */   {
/* 306 */     return false;
/*     */   }
/*     */   
/*     */   public UIInputReceiver getInputReceiver()
/*     */   {
/* 311 */     return null;
/*     */   }
/*     */   
/*     */   public UIMessage createMessage()
/*     */   {
/* 316 */     return null;
/*     */   }
/*     */   
/*     */   public UIToolBarManager getToolBarManager() {
/* 320 */     return null;
/*     */   }
/*     */   
/*     */   public void unload(PluginInterface pi) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/UI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */