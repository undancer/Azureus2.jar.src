/*     */ package org.gudy.azureus2.ui.console;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import com.aelitis.azureus.util.InitialisationFunctions;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.ConsoleAppender;
/*     */ import org.apache.log4j.PatternLayout;
/*     */ import org.apache.log4j.varia.DenyAllFilter;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.impl.TorrentDownloaderManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallerListener;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.ui.common.UIConst;
/*     */ import org.gudy.azureus2.ui.console.commands.AddFind;
/*     */ import org.gudy.azureus2.ui.console.commands.Archive;
/*     */ import org.gudy.azureus2.ui.console.commands.Create;
/*     */ import org.gudy.azureus2.ui.console.commands.Hack;
/*     */ import org.gudy.azureus2.ui.console.commands.IConsoleCommand;
/*     */ import org.gudy.azureus2.ui.console.commands.Move;
/*     */ import org.gudy.azureus2.ui.console.commands.Priority;
/*     */ import org.gudy.azureus2.ui.console.commands.Share;
/*     */ import org.gudy.azureus2.ui.console.commands.Show;
/*     */ import org.gudy.azureus2.ui.console.commands.Tags;
/*     */ import org.gudy.azureus2.ui.console.commands.TorrentCheck;
/*     */ import org.gudy.azureus2.ui.console.commands.TorrentForceStart;
/*     */ import org.gudy.azureus2.ui.console.commands.TorrentHost;
/*     */ import org.gudy.azureus2.ui.console.commands.TorrentLog;
/*     */ import org.gudy.azureus2.ui.console.commands.TorrentPublish;
/*     */ import org.gudy.azureus2.ui.console.commands.TorrentQueue;
/*     */ import org.gudy.azureus2.ui.console.commands.TorrentStart;
/*     */ import org.gudy.azureus2.ui.console.commands.XML;
/*     */ import org.gudy.azureus2.ui.console.util.TextWrap;
/*     */ import org.gudy.azureus2.update.CorePatchChecker;
/*     */ import org.gudy.azureus2.update.UpdaterUpdateChecker;
/*     */ 
/*     */ public class ConsoleInput extends Thread
/*     */ {
/*     */   private static final String ALIASES_CONFIG_FILE = "console.aliases.properties";
/*     */   public final AzureusCore azureus_core;
/*     */   public volatile PrintStream out;
/*  79 */   public final List torrents = new ArrayList();
/*  80 */   public File[] adds = null;
/*     */   
/*     */   private final CommandReader br;
/*     */   
/*     */   private final boolean controlling;
/*     */   private boolean running;
/*  86 */   private final Vector oldcommand = new Vector();
/*     */   
/*  88 */   private static final List pluginCommands = new ArrayList();
/*  89 */   public final Properties aliases = new Properties();
/*  90 */   private final Map commands = new LinkedHashMap();
/*  91 */   private final List helpItems = new ArrayList();
/*     */   
/*     */ 
/*     */   private final UserProfile userProfile;
/*     */   
/*     */ 
/*     */ 
/*     */   public static void registerPluginCommand(Class clazz)
/*     */   {
/* 100 */     if (!IConsoleCommand.class.isAssignableFrom(clazz))
/*     */     {
/* 102 */       throw new IllegalArgumentException("Class must implement IConsoleCommand");
/*     */     }
/* 104 */     pluginCommands.add(clazz);
/*     */   }
/*     */   
/*     */   public static void unregisterPluginCommand(Class clazz) {
/* 108 */     pluginCommands.remove(clazz);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConsoleInput(String con, AzureusCore _azureus_core, Reader _in, PrintStream _out, Boolean _controlling)
/*     */   {
/* 120 */     this(con, _azureus_core, _in, _out, _controlling, UserProfile.DEFAULT_USER_PROFILE);
/*     */   }
/*     */   
/*     */   public ConsoleInput(String con, AzureusCore _azureus_core, Reader _in, PrintStream _out, Boolean _controlling, UserProfile profile)
/*     */   {
/* 125 */     super("Console Input: " + con);
/* 126 */     this.out = _out;
/* 127 */     this.azureus_core = _azureus_core;
/* 128 */     this.userProfile = profile;
/* 129 */     this.controlling = _controlling.booleanValue();
/* 130 */     this.br = new CommandReader(_in);
/*     */     
/*     */ 
/* 133 */     System.out.println("ConsoleInput: initializing...");
/* 134 */     initialise();
/* 135 */     System.out.println("ConsoleInput: initialized OK");
/*     */     
/* 137 */     System.out.println("ConsoleInput: starting...");
/* 138 */     start();
/* 139 */     System.out.println("ConsoleInput: started OK");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConsoleInput(AzureusCore _azureus_core, PrintStream _out)
/*     */   {
/* 151 */     super("");
/* 152 */     this.out = _out;
/* 153 */     this.azureus_core = _azureus_core;
/* 154 */     this.userProfile = UserProfile.DEFAULT_USER_PROFILE;
/* 155 */     this.controlling = false;
/* 156 */     this.br = new CommandReader(new InputStreamReader(new ByteArrayInputStream(new byte[0])));
/*     */     
/* 158 */     if (org.apache.log4j.Logger.getRootLogger().getAppender("ConsoleAppender") == null)
/*     */     {
/* 160 */       Appender app = new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n"));
/* 161 */       app.setName("ConsoleAppender");
/* 162 */       app.addFilter(new DenyAllFilter());
/* 163 */       org.apache.log4j.Logger.getRootLogger().addAppender(app);
/*     */     }
/*     */     
/* 166 */     initialise();
/*     */   }
/*     */   
/*     */   protected void initialise() {
/* 170 */     registerAlertHandler();
/* 171 */     registerCommands();
/* 172 */     registerPluginCommands();
/*     */     
/* 174 */     if (this.azureus_core != null)
/*     */     {
/* 176 */       if (this.controlling)
/*     */       {
/*     */ 
/* 179 */         this.azureus_core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*     */           {
/*     */ 
/*     */ 
/* 187 */             if ((component instanceof GlobalManager))
/*     */             {
/* 189 */               InitialisationFunctions.earlyInitialisation(core);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void started(AzureusCore core)
/*     */           {
/* 197 */             ConsoleInput.this.registerUpdateChecker();
/*     */             
/* 199 */             InitialisationFunctions.lateInitialisation(core);
/*     */           }
/*     */         });
/*     */       }
/*     */       try
/*     */       {
/* 205 */         loadAliases();
/*     */       } catch (IOException e) {
/* 207 */         this.out.println("Error while loading aliases: " + e.getMessage());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 212 */     this.oldcommand.add("sh");
/* 213 */     this.oldcommand.add("t");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadTorrent(String filename, String outputDir)
/*     */   {
/* 224 */     DownloadManager manager = this.azureus_core.getGlobalManager().addDownloadManager(filename, outputDir);
/* 225 */     manager.getDownloadState().setAttribute("user", getUserProfile().getUsername());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadRemoteTorrent(String url, final String outputDir)
/*     */   {
/* 236 */     TorrentDownloader downloader = org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory.create(new TorrentDownloaderCallBackInterface()
/*     */     {
/*     */ 
/*     */       public void TorrentDownloaderEvent(int state, TorrentDownloader inf)
/*     */       {
/*     */ 
/* 242 */         if (state == 3) {
/* 243 */           ConsoleInput.this.out.println("Torrent file download complete. Starting torrent");
/* 244 */           TorrentDownloaderManager.getInstance().remove(inf);
/* 245 */           ConsoleInput.this.downloadTorrent(inf.getFile().getAbsolutePath(), outputDir);
/*     */         } else {
/* 247 */           if (state == 4)
/*     */           {
/* 249 */             ConsoleInput.this.out.println("Torrent file download failed: " + inf.getError());
/*     */           }
/*     */           
/* 252 */           TorrentDownloaderManager.getInstance().TorrentDownloaderEvent(state, inf); } } }, url, null, null, true);
/*     */     
/*     */ 
/*     */ 
/* 256 */     TorrentDownloaderManager.getInstance().add(downloader);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadTorrent(String fileName)
/*     */   {
/* 265 */     downloadTorrent(fileName, getDefaultSaveDirectory());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadRemoteTorrent(String url)
/*     */   {
/* 273 */     downloadRemoteTorrent(url, getDefaultSaveDirectory());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void registerPluginCommands()
/*     */   {
/* 281 */     for (Iterator iter = pluginCommands.iterator(); iter.hasNext();) {
/* 282 */       Class clazz = (Class)iter.next();
/*     */       try {
/* 284 */         IConsoleCommand command = (IConsoleCommand)clazz.newInstance();
/* 285 */         registerCommand(command);
/*     */       }
/*     */       catch (InstantiationException e) {
/* 288 */         this.out.println("Error while registering plugin command: " + clazz.getName() + ":" + e.getMessage());
/*     */       } catch (IllegalAccessException e) {
/* 290 */         this.out.println("Error while registering plugin command: " + clazz.getName() + ":" + e.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void registerAlertHandler()
/*     */   {
/* 298 */     org.gudy.azureus2.core3.logging.Logger.addListener(new org.gudy.azureus2.core3.logging.ILogAlertListener() {
/* 299 */       private java.util.Set history = java.util.Collections.synchronizedSet(new java.util.HashSet());
/*     */       
/*     */       public void alertRaised(LogAlert alert) {
/* 302 */         if (!alert.repeatable) {
/* 303 */           if (this.history.contains(alert.text))
/*     */           {
/* 305 */             return;
/*     */           }
/*     */           
/* 308 */           this.history.add(alert.text);
/*     */         }
/* 310 */         ConsoleInput.this.out.println(alert.text);
/* 311 */         if (alert.err != null) {
/* 312 */           alert.err.printStackTrace(ConsoleInput.this.out);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void registerCommands()
/*     */   {
/* 321 */     registerCommand(new XML());
/* 322 */     registerCommand(new Hack());
/* 323 */     registerCommand(new AddFind());
/* 324 */     registerCommand(new Create());
/* 325 */     registerCommand(new TorrentCheck());
/* 326 */     registerCommand(new TorrentQueue());
/* 327 */     registerCommand(new org.gudy.azureus2.ui.console.commands.TorrentRemove());
/* 328 */     registerCommand(new org.gudy.azureus2.ui.console.commands.TorrentArchive());
/* 329 */     registerCommand(new TorrentStart());
/* 330 */     registerCommand(new org.gudy.azureus2.ui.console.commands.TorrentStop());
/* 331 */     registerCommand(new TorrentHost());
/* 332 */     registerCommand(new TorrentPublish());
/* 333 */     registerCommand(new TorrentForceStart());
/* 334 */     registerCommand(new TorrentLog());
/* 335 */     registerCommand(new org.gudy.azureus2.ui.console.commands.Log());
/* 336 */     registerCommand(new Move());
/* 337 */     registerCommand(new org.gudy.azureus2.ui.console.commands.RunState());
/* 338 */     registerCommand(new Share());
/* 339 */     registerCommand(new org.gudy.azureus2.ui.console.commands.Set());
/* 340 */     registerCommand(new Show());
/* 341 */     registerCommand(new CommandUI());
/* 342 */     registerCommand(new CommandLogout());
/* 343 */     registerCommand(new CommandQuit());
/* 344 */     registerCommand(new CommandHelp());
/* 345 */     registerCommand(new org.gudy.azureus2.ui.console.commands.Alias());
/* 346 */     registerCommand(new Priority());
/* 347 */     registerCommand(new org.gudy.azureus2.ui.console.commands.Plugin());
/* 348 */     registerCommand(new org.gudy.azureus2.ui.console.commands.Pairing());
/* 349 */     registerCommand(new Archive());
/*     */     try
/*     */     {
/* 352 */       registerCommand(new org.gudy.azureus2.ui.console.commands.Subscriptions());
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 356 */     registerCommand(new Tags());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void registerCommand(IConsoleCommand command)
/*     */   {
/* 365 */     for (Iterator iter = command.getCommandNames().iterator(); iter.hasNext();) {
/* 366 */       String cmdName = (String)iter.next();
/* 367 */       this.commands.put(cmdName, command);
/*     */     }
/* 369 */     this.helpItems.add(command);
/*     */   }
/*     */   
/*     */   protected void unregisterCommand(IConsoleCommand command)
/*     */   {
/* 374 */     for (Iterator iter = command.getCommandNames().iterator(); iter.hasNext();) {
/* 375 */       String cmdName = (String)iter.next();
/* 376 */       if (command.equals(this.commands.get(cmdName)))
/* 377 */         this.commands.remove(cmdName);
/*     */     }
/* 379 */     this.helpItems.remove(command);
/*     */   }
/*     */   
/*     */   protected void unregisterCommand(String commandName) {
/* 383 */     IConsoleCommand cmd = (IConsoleCommand)this.commands.get(commandName);
/* 384 */     if (cmd == null) {
/* 385 */       return;
/*     */     }
/*     */     
/* 388 */     int numCommands = 0;
/* 389 */     for (Iterator iter = this.commands.entrySet().iterator(); iter.hasNext();) {
/* 390 */       Map.Entry entry = (Map.Entry)iter.next();
/* 391 */       if (cmd.equals(entry.getValue()))
/* 392 */         numCommands++;
/*     */     }
/* 394 */     if (numCommands == 1) {
/* 395 */       unregisterCommand(cmd);
/*     */     } else
/* 397 */       this.commands.remove(commandName);
/*     */   }
/*     */   
/*     */   public ConsoleInput(String con, AzureusCore _azureus_core, InputStream _in, PrintStream _out, Boolean _controlling) {
/* 401 */     this(con, _azureus_core, new InputStreamReader(_in), _out, _controlling);
/*     */   }
/*     */   
/*     */   private static void quit(boolean finish) {
/* 405 */     if (finish) {
/* 406 */       UIConst.shutdown();
/*     */     }
/*     */   }
/*     */   
/*     */   private class CommandHelp extends IConsoleCommand
/*     */   {
/*     */     public CommandHelp() {
/* 413 */       super("?");
/*     */     }
/*     */     
/* 416 */     public String getCommandDescriptions() { return "help [torrents]\t\t\t?\tShow this help. 'torrents' shows info about the show torrents display."; }
/*     */     
/*     */     public void execute(String commandName, ConsoleInput ci, List args)
/*     */     {
/* 420 */       if (args.isEmpty()) {
/* 421 */         ConsoleInput.this.printconsolehelp(ci.out);
/*     */       } else {
/* 423 */         String subcommand = (String)args.get(0);
/* 424 */         IConsoleCommand cmd = (IConsoleCommand)ConsoleInput.this.commands.get(subcommand);
/* 425 */         if (cmd != null)
/*     */         {
/* 427 */           List newargs = new ArrayList(args);
/* 428 */           newargs.remove(0);
/* 429 */           cmd.printHelp(ci.out, newargs);
/*     */ 
/*     */         }
/* 432 */         else if ((subcommand.equalsIgnoreCase("torrents")) || (subcommand.equalsIgnoreCase("t"))) {
/* 433 */           ci.out.println("> -----");
/* 434 */           ci.out.println("# [state] PercentDone Name (Filesize) ETA\r\n\tDownSpeed / UpSpeed\tDownloaded/Uploaded\tConnectedSeeds(total) / ConnectedPeers(total)");
/* 435 */           ci.out.println();
/* 436 */           ci.out.println("States:");
/* 437 */           ci.out.println(" > Downloading");
/* 438 */           ci.out.println(" * Seeding");
/* 439 */           ci.out.println(" ! Stopped");
/* 440 */           ci.out.println(" . Waiting (for allocation/checking)");
/* 441 */           ci.out.println(" : Ready");
/* 442 */           ci.out.println(" - Queued");
/* 443 */           ci.out.println(" A Allocating");
/* 444 */           ci.out.println(" C Checking");
/* 445 */           ci.out.println(" E Error");
/* 446 */           ci.out.println(" I Initializing");
/* 447 */           ci.out.println(" ? Unknown");
/* 448 */           ci.out.println("> -----");
/*     */         } else {
/* 450 */           ConsoleInput.this.printconsolehelp(ci.out);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void printwelcome()
/*     */   {
/* 458 */     this.out.println("Running " + Constants.APP_NAME + " " + "5.7.6.0" + "...");
/* 459 */     this.out.println("Using configuration settings from:");
/* 460 */     this.out.println("  " + SystemProperties.getUserPath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 465 */   public void printconsolehelp() { printconsolehelp(this.out); }
/*     */   
/*     */   private void printconsolehelp(PrintStream os) {
/* 468 */     os.println("> -----");
/* 469 */     os.println("Available console commands (use help <command> for more details):");
/* 470 */     os.println();
/*     */     
/* 472 */     ArrayList cmd_lines = new ArrayList();
/* 473 */     Iterator itr = this.helpItems.iterator();
/* 474 */     while (itr.hasNext()) {
/* 475 */       StringBuilder line_so_far = new StringBuilder("[");
/* 476 */       IConsoleCommand cmd = (IConsoleCommand)itr.next();
/* 477 */       String short_name = cmd.getShortCommandName();
/* 478 */       if (short_name != null) {
/* 479 */         line_so_far.append(short_name);
/*     */       }
/* 481 */       line_so_far.append("] ");
/* 482 */       line_so_far.append(cmd.getCommandName());
/* 483 */       cmd_lines.add(line_so_far.toString());
/*     */     }
/*     */     
/* 486 */     TextWrap.printList(cmd_lines.iterator(), os, "   ");
/* 487 */     os.println("> -----");
/*     */   }
/*     */   
/*     */   private static class CommandQuit extends IConsoleCommand
/*     */   {
/*     */     public CommandQuit()
/*     */     {
/* 494 */       super();
/*     */     }
/*     */     
/* 497 */     public String getCommandDescriptions() { return "quit\t\t\t\t\tShutdown Azureus"; }
/*     */     
/*     */     public void execute(String commandName, ConsoleInput ci, List args) {
/* 500 */       if (ci.controlling) {
/* 501 */         ci.running = false;
/* 502 */         ci.out.print("Exiting.....");
/* 503 */         ConsoleInput.quit(true);
/* 504 */         ci.out.println("OK");
/*     */ 
/*     */       }
/* 507 */       else if ((args.isEmpty()) || (!args.get(0).toString().equalsIgnoreCase("IAMSURE"))) {
/* 508 */         ci.out.println("> The 'quit' command exits azureus. Since this is a non-controlling shell thats probably not what you wanted. Use 'logout' to quit it or 'quit iamsure' to really exit azureus.");
/*     */       }
/*     */       else {
/* 511 */         ci.out.print("Exiting.....");
/* 512 */         ConsoleInput.quit(true);
/* 513 */         ci.out.println("OK");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CommandLogout
/*     */     extends IConsoleCommand
/*     */   {
/*     */     public CommandLogout()
/*     */     {
/* 523 */       super();
/*     */     }
/*     */     
/* 526 */     public String getCommandDescriptions() { return "logout\t\t\t\t\tLog out of the CLI"; }
/*     */     
/*     */     public void execute(String commandName, ConsoleInput ci, List args) {
/*     */       try {
/* 530 */         if (!ci.controlling)
/*     */         {
/*     */ 
/*     */ 
/* 534 */           if (ci.out != System.out)
/*     */           {
/* 536 */             ci.out.println("Logged out");
/*     */             
/* 538 */             ci.out.close();
/*     */           }
/*     */           
/* 541 */           ci.br.close();
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (IOException ignored) {}finally
/*     */       {
/* 548 */         ci.running = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CommandUI extends IConsoleCommand
/*     */   {
/*     */     public CommandUI()
/*     */     {
/* 557 */       super("u");
/*     */     }
/*     */     
/* 560 */     public String getCommandDescriptions() { return "ui <interface>\t\t\tu\tStart additional user interface."; }
/*     */     
/*     */     public void execute(String commandName, ConsoleInput ci, List args) {
/* 563 */       if (!args.isEmpty()) {
/* 564 */         UIConst.startUI(args.get(0).toString(), null);
/*     */       } else {
/* 566 */         ci.out.println("> Missing subcommand for 'ui'\r\n> ui syntax: ui <interface>");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean invokeCommand(String command, List cargs) {
/* 572 */     if (command.startsWith("\\")) {
/* 573 */       command = command.substring(1);
/* 574 */     } else if (this.aliases.containsKey(command))
/*     */     {
/* 576 */       List list = this.br.parseCommandLine(this.aliases.getProperty(command));
/* 577 */       String newCommand = list.remove(0).toString().toLowerCase();
/* 578 */       list.addAll(cargs);
/* 579 */       return invokeCommand(newCommand, list);
/*     */     }
/* 581 */     if (this.commands.containsKey(command)) {
/* 582 */       IConsoleCommand cmd = (IConsoleCommand)this.commands.get(command);
/*     */       try {
/* 584 */         if (cargs == null)
/* 585 */           cargs = new ArrayList();
/* 586 */         cmd.execute(command, this, cargs);
/* 587 */         return true;
/*     */       }
/*     */       catch (Exception e) {
/* 590 */         this.out.println("> Invoking Command '" + command + "' failed. Exception: " + Debug.getNestedExceptionMessage(e));
/* 591 */         return false;
/*     */       }
/*     */     }
/* 594 */     return false;
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/* 599 */     this.running = true;
/* 600 */     for (;;) { if (this.running) {
/*     */         List<String> comargs;
/* 602 */         try { String line = this.br.readLine();
/* 603 */           comargs = this.br.parseCommandLine(line);
/*     */         } catch (Exception e) {
/* 605 */           this.out.println("Stopping console input reader because of exception: " + e.getMessage());
/* 606 */           this.running = false;
/* 607 */           return;
/*     */         }
/* 609 */         if (!comargs.isEmpty())
/*     */         {
/* 611 */           int argNum = comargs.size();
/*     */           
/* 613 */           File outputFile = null;
/* 614 */           boolean outputFileAppend = false;
/*     */           
/* 616 */           if (argNum >= 3)
/*     */           {
/* 618 */             String temp = (String)comargs.get(argNum - 2);
/*     */             
/* 620 */             if ((temp.equals(">")) || (temp.equals(">>")))
/*     */             {
/* 622 */               File file = new File((String)comargs.get(argNum - 1));
/*     */               
/* 624 */               if (!file.getParentFile().canWrite())
/*     */               {
/* 626 */                 this.out.println("> Invalid output file '" + file + "'");
/*     */                 
/* 628 */                 continue;
/*     */               }
/*     */               
/* 631 */               outputFile = file;
/* 632 */               outputFileAppend = temp.equals(">>");
/*     */               
/* 634 */               comargs = comargs.subList(0, argNum - 2);
/*     */             }
/*     */           }
/*     */           
/* 638 */           String command = ((String)comargs.get(0)).toLowerCase();
/* 639 */           if (".".equals(command))
/*     */           {
/* 641 */             if (this.oldcommand.size() > 0) {
/* 642 */               comargs.clear();
/* 643 */               comargs.addAll(this.oldcommand);
/* 644 */               command = ((String)comargs.get(0)).toLowerCase();
/*     */             } else {
/* 646 */               this.out.println("No old command. Remove commands are not repeated to prevent errors");
/*     */             }
/*     */           }
/* 649 */           this.oldcommand.clear();
/* 650 */           this.oldcommand.addAll(comargs);
/* 651 */           comargs.remove(0);
/*     */           
/* 653 */           PrintStream base_os = null;
/*     */           try
/*     */           {
/* 656 */             if (outputFile != null)
/*     */             {
/* 658 */               PrintStream temp = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile, outputFileAppend), 128));
/*     */               
/* 660 */               base_os = this.out;
/* 661 */               this.out = temp;
/*     */             }
/*     */             
/* 664 */             if (!invokeCommand(command, comargs)) {
/* 665 */               this.out.println("> Command '" + command + "' unknown (or . used without prior command)");
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 673 */             if (base_os != null) {
/*     */               try
/*     */               {
/* 676 */                 PrintStream temp = this.out;
/*     */                 
/* 678 */                 this.out = base_os;
/*     */                 
/* 680 */                 temp.close();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 684 */                 this.out.println("Exception occurred when closing output file");
/* 685 */                 e.printStackTrace(this.out);
/*     */               }
/*     */             }
/*     */             PrintStream temp;
/*     */             PrintStream temp;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 669 */             this.out.println("Exception occurred when executing command: '" + command + "'");
/* 670 */             e.printStackTrace(this.out);
/*     */           }
/*     */           finally {
/* 673 */             if (base_os != null) {
/*     */               try
/*     */               {
/* 676 */                 temp = this.out;
/*     */                 
/* 678 */                 this.out = base_os;
/*     */                 
/* 680 */                 temp.close();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 684 */                 this.out.println("Exception occurred when closing output file");
/* 685 */                 e.printStackTrace(this.out);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private File getAliasesFile() {
/* 695 */     PluginInterface pi = this.azureus_core.getPluginManager().getDefaultPluginInterface();
/* 696 */     String azureusUserDir = pi.getUtilities().getAzureusUserDir();
/* 697 */     return new File(azureusUserDir, "console.aliases.properties");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void loadAliases()
/*     */     throws IOException
/*     */   {
/* 705 */     File aliasesFile = getAliasesFile();
/* 706 */     this.out.println("Attempting to load aliases from: " + aliasesFile.getCanonicalPath());
/* 707 */     if (aliasesFile.exists())
/*     */     {
/* 709 */       FileInputStream fr = new FileInputStream(aliasesFile);
/* 710 */       this.aliases.clear();
/*     */       try {
/* 712 */         this.aliases.load(fr);
/*     */       } finally {
/* 714 */         fr.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void saveAliases()
/*     */   {
/* 723 */     File aliasesFile = getAliasesFile();
/*     */     try {
/* 725 */       this.out.println("Saving aliases to: " + aliasesFile.getCanonicalPath());
/* 726 */       FileOutputStream fo = new FileOutputStream(aliasesFile);
/*     */       try {
/* 728 */         this.aliases.store(fo, "This aliases file was automatically written by Azureus");
/*     */       } finally {
/* 730 */         fo.close();
/*     */       }
/*     */     } catch (IOException e) {
/* 733 */       this.out.println("> Error saving aliases to " + aliasesFile.getPath() + ":" + e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UserProfile getUserProfile()
/*     */   {
/* 741 */     return this.userProfile;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDefaultSaveDirectory()
/*     */   {
/*     */     try
/*     */     {
/* 750 */       String saveDir = getUserProfile().getDefaultSaveDirectory();
/* 751 */       if (saveDir == null)
/*     */       {
/* 753 */         saveDir = COConfigurationManager.getDirectoryParameter("Default save path");
/* 754 */         if ((saveDir != null) && (saveDir.length() != 0)) {} }
/* 755 */       return ".";
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 760 */       e.printStackTrace(); }
/* 761 */     return ".";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void registerUpdateChecker()
/*     */   {
/* 769 */     boolean check_at_start = COConfigurationManager.getBooleanParameter("update.start", true);
/*     */     
/* 771 */     if (!check_at_start)
/*     */     {
/* 773 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 778 */     PluginManager pm = this.azureus_core.getPluginManager();
/*     */     
/* 780 */     pm.getPluginInstaller().addListener(new PluginInstallerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean installRequest(String reason, InstallablePlugin plugin)
/*     */         throws org.gudy.azureus2.plugins.PluginException
/*     */       {
/*     */ 
/*     */ 
/* 790 */         ConsoleInput.this.out.println("Plugin installation request for '" + plugin.getName() + "' - " + reason);
/*     */         
/* 792 */         String desc = plugin.getDescription();
/*     */         
/* 794 */         String[] bits = desc.split("\n");
/*     */         
/* 796 */         for (int i = 0; i < bits.length; i++)
/*     */         {
/* 798 */           ConsoleInput.this.out.println("\t" + bits[i]);
/*     */         }
/*     */         
/* 801 */         return true;
/*     */       }
/*     */       
/* 804 */     });
/* 805 */     PluginInterface pi = pm.getPluginInterfaceByClass(CorePatchChecker.class);
/*     */     
/* 807 */     if (pi != null)
/*     */     {
/* 809 */       pi.getPluginState().setDisabled(true);
/*     */     }
/*     */     
/* 812 */     pi = pm.getPluginInterfaceByClass(UpdaterUpdateChecker.class);
/*     */     
/* 814 */     if (pi != null)
/*     */     {
/* 816 */       pi.getPluginState().setDisabled(true);
/*     */     }
/*     */     
/*     */ 
/* 820 */     UpdateManager update_manager = this.azureus_core.getPluginManager().getDefaultPluginInterface().getUpdateManager();
/*     */     
/* 822 */     final UpdateCheckInstance checker = update_manager.createUpdateCheckInstance();
/*     */     
/* 824 */     checker.addListener(new UpdateCheckInstanceListener()
/*     */     {
/*     */       public void cancelled(UpdateCheckInstance instance) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void complete(UpdateCheckInstance instance)
/*     */       {
/* 838 */         int num_updates = 0;
/*     */         
/* 840 */         Update[] updates = instance.getUpdates();
/*     */         
/* 842 */         for (int i = 0; i < updates.length; i++)
/*     */         {
/* 844 */           Update update = updates[i];
/*     */           
/* 846 */           num_updates++;
/*     */           
/* 848 */           ConsoleInput.this.out.println("Update available for '" + update.getName() + "', new version = " + update.getNewVersion());
/*     */           
/* 850 */           String[] descs = update.getDescription();
/*     */           
/* 852 */           for (int j = 0; j < descs.length; j++)
/*     */           {
/* 854 */             ConsoleInput.this.out.println("\t" + descs[j]);
/*     */           }
/*     */           
/* 857 */           if (update.isMandatory())
/*     */           {
/* 859 */             ConsoleInput.this.out.println("**** This is a mandatory update, other updates can not proceed until this is performed ****");
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 865 */         checker.cancel();
/*     */         
/* 867 */         if (num_updates > 0)
/*     */         {
/* 869 */           ConsoleInput.this.out.println("Apply these updates with the 'plugin update' command");
/*     */         }
/*     */         
/*     */       }
/* 873 */     });
/* 874 */     checker.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AzureusCore getCore()
/*     */   {
/* 881 */     return this.azureus_core;
/*     */   }
/*     */   
/*     */ 
/*     */   public GlobalManager getGlobalManager()
/*     */   {
/* 887 */     return this.azureus_core.getGlobalManager();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/ConsoleInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */