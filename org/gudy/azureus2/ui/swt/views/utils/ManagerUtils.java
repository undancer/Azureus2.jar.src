/*      */ package org.gudy.azureus2.ui.swt.views.utils;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*      */ import com.aelitis.azureus.core.util.HTTPUtils;
/*      */ import com.aelitis.azureus.core.util.LaunchManager;
/*      */ import com.aelitis.azureus.core.util.LaunchManager.LaunchAction;
/*      */ import com.aelitis.azureus.core.util.LaunchManager.LaunchTarget;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.DirectoryDialog;
/*      */ import org.eclipse.swt.widgets.MessageBox;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerDownloadRemovalVetoException;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostException;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.ConcurrentHasher;
/*      */ import org.gudy.azureus2.core3.util.ConcurrentHasherRequest;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.PluginException;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerChannel;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerEvent;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerListener;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerRequest;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*      */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*      */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.webplugin.WebPlugin;
/*      */ 
/*      */ public class ManagerUtils
/*      */ {
/*      */   private static RunDownloadManager run;
/*      */   
/*      */   public static void setRunRunnable(RunDownloadManager run)
/*      */   {
/*  117 */     run = run;
/*      */   }
/*      */   
/*      */   public static void run(DownloadManager dm) {
/*  121 */     if (dm != null) {
/*  122 */       LaunchManager launch_manager = LaunchManager.getManager();
/*      */       
/*  124 */       LaunchManager.LaunchTarget target = launch_manager.createTarget(dm);
/*      */       
/*  126 */       launch_manager.launchRequest(target, new LaunchManager.LaunchAction()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void actionAllowed()
/*      */         {
/*      */ 
/*  133 */           AZ3Functions.provider prov = com.aelitis.azureus.core.util.AZ3Functions.getProvider();
/*      */           
/*  135 */           if (prov != null)
/*      */           {
/*  137 */             prov.setOpened(this.val$dm, true);
/*      */           }
/*      */           
/*  140 */           Utils.execSWTThread(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/*  146 */               if (ManagerUtils.run != null) {
/*  147 */                 ManagerUtils.run.run(ManagerUtils.1.this.val$dm);
/*      */               } else {
/*  149 */                 Utils.launch(ManagerUtils.1.this.val$dm.getSaveLocation().toString());
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void actionDenied(Throwable reason)
/*      */         {
/*  159 */           Debug.out("Launch request denied", reason);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void open(DownloadManager dm)
/*      */   {
/*  169 */     open(dm, false);
/*      */   }
/*      */   
/*      */   public static void open(DownloadManager dm, final boolean open_containing_folder_mode) {
/*  173 */     if (dm != null)
/*      */     {
/*  175 */       LaunchManager launch_manager = LaunchManager.getManager();
/*      */       
/*  177 */       LaunchManager.LaunchTarget target = launch_manager.createTarget(dm);
/*      */       
/*  179 */       launch_manager.launchRequest(target, new LaunchManager.LaunchAction()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void actionAllowed()
/*      */         {
/*      */ 
/*  186 */           Utils.execSWTThread(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/*  192 */               ManagerUtils.open(ManagerUtils.2.this.val$dm.getSaveLocation(), ManagerUtils.2.this.val$open_containing_folder_mode);
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void actionDenied(Throwable reason)
/*      */         {
/*  201 */           Debug.out("Launch request denied", reason);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void open(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file, final boolean open_containing_folder_mode)
/*      */   {
/*  212 */     if (file != null)
/*      */     {
/*  214 */       LaunchManager launch_manager = LaunchManager.getManager();
/*      */       
/*  216 */       LaunchManager.LaunchTarget target = launch_manager.createTarget(file);
/*      */       
/*  218 */       launch_manager.launchRequest(target, new LaunchManager.LaunchAction()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void actionAllowed()
/*      */         {
/*      */ 
/*  225 */           Utils.execSWTThread(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/*  231 */               File this_file = ManagerUtils.3.this.val$file.getFile(true);
/*      */               
/*  233 */               File parent_file = ManagerUtils.3.this.val$open_containing_folder_mode ? this_file.getParentFile() : null;
/*      */               
/*  235 */               ManagerUtils.open(parent_file == null ? this_file : parent_file);
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void actionDenied(Throwable reason)
/*      */         {
/*  244 */           Debug.out("Launch request denied", reason);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public static void open(File f, boolean open_containing_folder_mode)
/*      */   {
/*  252 */     if (open_containing_folder_mode) {
/*  253 */       Utils.launch(f.getParent());
/*      */     }
/*      */     else {
/*  256 */       open(f);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void open(File f) {
/*  261 */     while ((f != null) && (!f.exists())) {
/*  262 */       f = f.getParentFile();
/*      */     }
/*  264 */     if (f == null) {
/*  265 */       return;
/*      */     }
/*  267 */     PlatformManager mgr = PlatformManagerFactory.getPlatformManager();
/*      */     
/*  269 */     if (mgr.hasCapability(org.gudy.azureus2.platform.PlatformManagerCapabilities.ShowFileInBrowser)) {
/*      */       try {
/*  271 */         PlatformManagerFactory.getPlatformManager().showFile(f.toString());
/*  272 */         return;
/*      */       } catch (PlatformManagerException e) {
/*  274 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  278 */     if (f.isDirectory()) {
/*  279 */       Utils.launch(f.toString());
/*      */     } else {
/*  281 */       Utils.launch(f.getParent().toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static boolean getBrowseAnon(DownloadManager dm)
/*      */   {
/*  289 */     boolean anon = COConfigurationManager.getBooleanParameter("Library.LaunchWebsiteInBrowserAnon");
/*      */     
/*  291 */     if (!anon)
/*      */     {
/*  293 */       boolean found_pub = false;
/*      */       
/*  295 */       String[] nets = dm.getDownloadState().getNetworks();
/*      */       
/*  297 */       for (String net : nets)
/*      */       {
/*  299 */         if (net == "Public")
/*      */         {
/*  301 */           found_pub = true;
/*      */           
/*  303 */           break;
/*      */         }
/*      */       }
/*      */       
/*  307 */       if ((nets.length > 0) && (!found_pub))
/*      */       {
/*  309 */         anon = true;
/*      */       }
/*      */     }
/*      */     
/*  313 */     return anon;
/*      */   }
/*      */   
/*      */ 
/*      */   private static org.gudy.azureus2.core3.disk.DiskManagerFileInfo getBrowseHomePage(DownloadManager dm)
/*      */   {
/*      */     try
/*      */     {
/*  321 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files = dm.getDiskManagerFileInfoSet().getFiles();
/*      */       
/*  323 */       for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : files)
/*      */       {
/*  325 */         if (file.getTorrentFile().getPathComponents().length == 1)
/*      */         {
/*  327 */           String name = file.getTorrentFile().getRelativePath().toLowerCase(Locale.US);
/*      */           
/*  329 */           if ((name.equals("index.html")) || (name.equals("index.htm")))
/*      */           {
/*  331 */             return file;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  337 */       Debug.out(e);
/*      */     }
/*      */     
/*  340 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean browseWebsite(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/*      */     try
/*      */     {
/*  348 */       String name = file.getTorrentFile().getRelativePath().toLowerCase(Locale.US);
/*      */       
/*  350 */       if ((name.equals("index.html")) || (name.equals("index.htm")))
/*      */       {
/*  352 */         browse(file);
/*      */         
/*  354 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  358 */       Debug.out(e);
/*      */     }
/*      */     
/*  361 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean browseWebsite(DownloadManager dm)
/*      */   {
/*  368 */     org.gudy.azureus2.core3.disk.DiskManagerFileInfo file = getBrowseHomePage(dm);
/*      */     
/*  370 */     if (file != null)
/*      */     {
/*  372 */       browse(file);
/*      */       
/*  374 */       return true;
/*      */     }
/*      */     
/*  377 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String browse(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/*  384 */     boolean anon = getBrowseAnon(file.getDownloadManager());
/*      */     
/*  386 */     return browse(file, anon, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String browse(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file, boolean anon, boolean launch)
/*      */   {
/*  395 */     return browse(file.getDownloadManager(), file, anon, launch);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String browse(DownloadManager dm)
/*      */   {
/*  402 */     boolean anon = getBrowseAnon(dm);
/*      */     
/*  404 */     return browse(dm, null, anon, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String browse(DownloadManager dm, boolean anon, boolean launch)
/*      */   {
/*  413 */     return browse(dm, null, anon, launch);
/*      */   }
/*      */   
/*      */ 
/*  417 */   private static Map<DownloadManager, WebPlugin> browse_plugins = new java.util.IdentityHashMap();
/*      */   
/*      */   /* Error */
/*      */   public static String browse(final DownloadManager dm, org.gudy.azureus2.core3.disk.DiskManagerFileInfo _file, final boolean anon, final boolean launch)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 461	java/util/Properties
/*      */     //   3: dup
/*      */     //   4: invokespecial 882	java/util/Properties:<init>	()V
/*      */     //   7: astore 4
/*      */     //   9: aload_0
/*      */     //   10: invokeinterface 989 1 0
/*      */     //   15: astore 5
/*      */     //   17: aload 5
/*      */     //   19: invokevirtual 851	java/io/File:isFile	()Z
/*      */     //   22: ifeq +16 -> 38
/*      */     //   25: aload 5
/*      */     //   27: invokevirtual 852	java/io/File:getParentFile	()Ljava/io/File;
/*      */     //   30: invokevirtual 854	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*      */     //   33: astore 6
/*      */     //   35: goto +10 -> 45
/*      */     //   38: aload 5
/*      */     //   40: invokevirtual 854	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*      */     //   43: astore 6
/*      */     //   45: ldc 22
/*      */     //   47: invokestatic 892	org/gudy/azureus2/core3/config/COConfigurationManager:getBooleanParameter	(Ljava/lang/String;)Z
/*      */     //   50: istore 8
/*      */     //   52: iload 8
/*      */     //   54: ifne +12 -> 66
/*      */     //   57: aload_1
/*      */     //   58: ifnonnull +8 -> 66
/*      */     //   61: aload_0
/*      */     //   62: invokestatic 943	org/gudy/azureus2/ui/swt/views/utils/ManagerUtils:getBrowseHomePage	(Lorg/gudy/azureus2/core3/download/DownloadManager;)Lorg/gudy/azureus2/core3/disk/DiskManagerFileInfo;
/*      */     //   65: astore_1
/*      */     //   66: aload_1
/*      */     //   67: astore 9
/*      */     //   69: aload 9
/*      */     //   71: ifnonnull +10 -> 81
/*      */     //   74: ldc 1
/*      */     //   76: astore 7
/*      */     //   78: goto +136 -> 214
/*      */     //   81: aload 9
/*      */     //   83: invokeinterface 983 1 0
/*      */     //   88: invokeinterface 999 1 0
/*      */     //   93: astore 10
/*      */     //   95: aload 10
/*      */     //   97: getstatic 836	java/io/File:separatorChar	C
/*      */     //   100: bipush 47
/*      */     //   102: invokevirtual 866	java/lang/String:replace	(CC)Ljava/lang/String;
/*      */     //   105: ldc 6
/*      */     //   107: invokevirtual 868	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
/*      */     //   110: astore 11
/*      */     //   112: ldc 1
/*      */     //   114: astore 12
/*      */     //   116: iload 8
/*      */     //   118: ifeq +11 -> 129
/*      */     //   121: aload 11
/*      */     //   123: arraylength
/*      */     //   124: iconst_1
/*      */     //   125: isub
/*      */     //   126: goto +6 -> 132
/*      */     //   129: aload 11
/*      */     //   131: arraylength
/*      */     //   132: istore 13
/*      */     //   134: iconst_0
/*      */     //   135: istore 14
/*      */     //   137: iload 14
/*      */     //   139: iload 13
/*      */     //   141: if_icmpge +69 -> 210
/*      */     //   144: aload 11
/*      */     //   146: iload 14
/*      */     //   148: aaload
/*      */     //   149: astore 15
/*      */     //   151: aload 15
/*      */     //   153: invokevirtual 863	java/lang/String:length	()I
/*      */     //   156: ifne +6 -> 162
/*      */     //   159: goto +45 -> 204
/*      */     //   162: new 450	java/lang/StringBuilder
/*      */     //   165: dup
/*      */     //   166: invokespecial 870	java/lang/StringBuilder:<init>	()V
/*      */     //   169: aload 12
/*      */     //   171: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   174: aload 12
/*      */     //   176: ldc 1
/*      */     //   178: if_acmpne +8 -> 186
/*      */     //   181: ldc 1
/*      */     //   183: goto +5 -> 188
/*      */     //   186: ldc 6
/*      */     //   188: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   191: aload 15
/*      */     //   193: invokestatic 906	org/gudy/azureus2/core3/util/UrlUtils:encode	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   196: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   199: invokevirtual 871	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   202: astore 12
/*      */     //   204: iinc 14 1
/*      */     //   207: goto -70 -> 137
/*      */     //   210: aload 12
/*      */     //   212: astore 7
/*      */     //   214: getstatic 840	org/gudy/azureus2/ui/swt/views/utils/ManagerUtils:browse_plugins	Ljava/util/Map;
/*      */     //   217: dup
/*      */     //   218: astore 10
/*      */     //   220: monitorenter
/*      */     //   221: getstatic 840	org/gudy/azureus2/ui/swt/views/utils/ManagerUtils:browse_plugins	Ljava/util/Map;
/*      */     //   224: aload_0
/*      */     //   225: invokeinterface 978 2 0
/*      */     //   230: checkcast 527	org/gudy/azureus2/ui/webplugin/WebPlugin
/*      */     //   233: astore 11
/*      */     //   235: aload 11
/*      */     //   237: ifnonnull +345 -> 582
/*      */     //   240: aload 4
/*      */     //   242: ldc 27
/*      */     //   244: iconst_0
/*      */     //   245: invokestatic 860	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   248: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   251: pop
/*      */     //   252: aload 4
/*      */     //   254: ldc 12
/*      */     //   256: ldc 7
/*      */     //   258: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   261: pop
/*      */     //   262: aload 4
/*      */     //   264: ldc 20
/*      */     //   266: ldc 1
/*      */     //   268: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   271: pop
/*      */     //   272: aload 4
/*      */     //   274: ldc 29
/*      */     //   276: aload 6
/*      */     //   278: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   281: pop
/*      */     //   282: aload 4
/*      */     //   284: ldc 10
/*      */     //   286: ldc 36
/*      */     //   288: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   291: pop
/*      */     //   292: aload 4
/*      */     //   294: ldc 14
/*      */     //   296: iconst_1
/*      */     //   297: invokestatic 859	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
/*      */     //   300: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   303: pop
/*      */     //   304: aload 4
/*      */     //   306: ldc 13
/*      */     //   308: iconst_1
/*      */     //   309: invokestatic 859	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
/*      */     //   312: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   315: pop
/*      */     //   316: aload 4
/*      */     //   318: ldc 16
/*      */     //   320: iconst_0
/*      */     //   321: invokestatic 859	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
/*      */     //   324: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   327: pop
/*      */     //   328: aload 4
/*      */     //   330: ldc 18
/*      */     //   332: iconst_0
/*      */     //   333: invokestatic 859	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
/*      */     //   336: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   339: pop
/*      */     //   340: aload 4
/*      */     //   342: ldc 15
/*      */     //   344: iconst_0
/*      */     //   345: invokestatic 859	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
/*      */     //   348: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   351: pop
/*      */     //   352: aload 4
/*      */     //   354: ldc 17
/*      */     //   356: iconst_0
/*      */     //   357: invokestatic 859	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
/*      */     //   360: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   363: pop
/*      */     //   364: new 450	java/lang/StringBuilder
/*      */     //   367: dup
/*      */     //   368: invokespecial 870	java/lang/StringBuilder:<init>	()V
/*      */     //   371: ldc 44
/*      */     //   373: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   376: aload_0
/*      */     //   377: invokeinterface 991 1 0
/*      */     //   382: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   385: invokevirtual 871	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   388: astore 12
/*      */     //   390: new 450	java/lang/StringBuilder
/*      */     //   393: dup
/*      */     //   394: invokespecial 870	java/lang/StringBuilder:<init>	()V
/*      */     //   397: ldc 30
/*      */     //   399: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   402: aload_0
/*      */     //   403: invokeinterface 990 1 0
/*      */     //   408: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   411: invokevirtual 871	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   414: astore 13
/*      */     //   416: new 461	java/util/Properties
/*      */     //   419: dup
/*      */     //   420: invokespecial 882	java/util/Properties:<init>	()V
/*      */     //   423: astore 14
/*      */     //   425: aload 14
/*      */     //   427: new 450	java/lang/StringBuilder
/*      */     //   430: dup
/*      */     //   431: invokespecial 870	java/lang/StringBuilder:<init>	()V
/*      */     //   434: ldc 39
/*      */     //   436: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   439: aload 12
/*      */     //   441: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   444: invokevirtual 871	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   447: aload 13
/*      */     //   449: invokevirtual 883	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   452: pop
/*      */     //   453: invokestatic 912	org/gudy/azureus2/pluginsimpl/local/PluginInitializer:getDefaultInterface	()Lorg/gudy/azureus2/plugins/PluginInterface;
/*      */     //   456: invokeinterface 1006 1 0
/*      */     //   461: invokeinterface 1011 1 0
/*      */     //   466: aload 14
/*      */     //   468: invokeinterface 1010 2 0
/*      */     //   473: new 478	org/gudy/azureus2/core3/util/AESemaphore
/*      */     //   476: dup
/*      */     //   477: ldc 43
/*      */     //   479: invokespecial 897	org/gudy/azureus2/core3/util/AESemaphore:<init>	(Ljava/lang/String;)V
/*      */     //   482: astore 15
/*      */     //   484: iconst_1
/*      */     //   485: anewarray 449	java/lang/String
/*      */     //   488: dup
/*      */     //   489: iconst_0
/*      */     //   490: aconst_null
/*      */     //   491: aastore
/*      */     //   492: astore 16
/*      */     //   494: new 518	org/gudy/azureus2/ui/swt/views/utils/ManagerUtils$4
/*      */     //   497: dup
/*      */     //   498: aload 4
/*      */     //   500: aload_0
/*      */     //   501: aload 13
/*      */     //   503: aload 7
/*      */     //   505: iload_3
/*      */     //   506: iload_2
/*      */     //   507: aload 16
/*      */     //   509: aload 15
/*      */     //   511: aload 9
/*      */     //   513: invokespecial 963	org/gudy/azureus2/ui/swt/views/utils/ManagerUtils$4:<init>	(Ljava/util/Properties;Lorg/gudy/azureus2/core3/download/DownloadManager;Ljava/lang/String;Ljava/lang/String;ZZ[Ljava/lang/String;Lorg/gudy/azureus2/core3/util/AESemaphore;Lorg/gudy/azureus2/core3/disk/DiskManagerFileInfo;)V
/*      */     //   516: astore 11
/*      */     //   518: aload 11
/*      */     //   520: aload 12
/*      */     //   522: aload 12
/*      */     //   524: invokestatic 910	org/gudy/azureus2/plugins/PluginManager:registerPlugin	(Lorg/gudy/azureus2/plugins/Plugin;Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   527: getstatic 840	org/gudy/azureus2/ui/swt/views/utils/ManagerUtils:browse_plugins	Ljava/util/Map;
/*      */     //   530: aload_0
/*      */     //   531: aload 11
/*      */     //   533: invokeinterface 979 3 0
/*      */     //   538: pop
/*      */     //   539: iload_3
/*      */     //   540: ifeq +8 -> 548
/*      */     //   543: aconst_null
/*      */     //   544: aload 10
/*      */     //   546: monitorexit
/*      */     //   547: areturn
/*      */     //   548: aload 15
/*      */     //   550: ldc2_w 428
/*      */     //   553: invokevirtual 896	org/gudy/azureus2/core3/util/AESemaphore:reserve	(J)Z
/*      */     //   556: pop
/*      */     //   557: aload 16
/*      */     //   559: dup
/*      */     //   560: astore 17
/*      */     //   562: monitorenter
/*      */     //   563: aload 16
/*      */     //   565: iconst_0
/*      */     //   566: aaload
/*      */     //   567: aload 17
/*      */     //   569: monitorexit
/*      */     //   570: aload 10
/*      */     //   572: monitorexit
/*      */     //   573: areturn
/*      */     //   574: astore 18
/*      */     //   576: aload 17
/*      */     //   578: monitorexit
/*      */     //   579: aload 18
/*      */     //   581: athrow
/*      */     //   582: aload 11
/*      */     //   584: invokevirtual 971	org/gudy/azureus2/ui/webplugin/WebPlugin:getProtocol	()Ljava/lang/String;
/*      */     //   587: astore 12
/*      */     //   589: aload 11
/*      */     //   591: invokevirtual 972	org/gudy/azureus2/ui/webplugin/WebPlugin:getServerBindIP	()Ljava/net/InetAddress;
/*      */     //   594: astore 13
/*      */     //   596: aload 13
/*      */     //   598: invokevirtual 875	java/net/InetAddress:isAnyLocalAddress	()Z
/*      */     //   601: ifeq +10 -> 611
/*      */     //   604: ldc 7
/*      */     //   606: astore 14
/*      */     //   608: goto +10 -> 618
/*      */     //   611: aload 13
/*      */     //   613: invokevirtual 876	java/net/InetAddress:getHostAddress	()Ljava/lang/String;
/*      */     //   616: astore 14
/*      */     //   618: new 450	java/lang/StringBuilder
/*      */     //   621: dup
/*      */     //   622: invokespecial 870	java/lang/StringBuilder:<init>	()V
/*      */     //   625: aload 12
/*      */     //   627: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   630: ldc 9
/*      */     //   632: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   635: aload 14
/*      */     //   637: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   640: ldc 8
/*      */     //   642: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   645: aload 11
/*      */     //   647: invokevirtual 970	org/gudy/azureus2/ui/webplugin/WebPlugin:getServerPort	()I
/*      */     //   650: invokevirtual 872	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   653: ldc 6
/*      */     //   655: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   658: aload 7
/*      */     //   660: invokevirtual 873	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   663: invokevirtual 871	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   666: astore 15
/*      */     //   668: iload_3
/*      */     //   669: ifeq +16 -> 685
/*      */     //   672: aload 15
/*      */     //   674: iconst_0
/*      */     //   675: iconst_1
/*      */     //   676: iload_2
/*      */     //   677: invokestatic 925	org/gudy/azureus2/ui/swt/Utils:launch	(Ljava/lang/String;ZZZ)V
/*      */     //   680: aconst_null
/*      */     //   681: aload 10
/*      */     //   683: monitorexit
/*      */     //   684: areturn
/*      */     //   685: aload 15
/*      */     //   687: aload 10
/*      */     //   689: monitorexit
/*      */     //   690: areturn
/*      */     //   691: astore 19
/*      */     //   693: aload 10
/*      */     //   695: monitorexit
/*      */     //   696: aload 19
/*      */     //   698: athrow
/*      */     // Line number table:
/*      */     //   Java source line #426	-> byte code offset #0
/*      */     //   Java source line #428	-> byte code offset #9
/*      */     //   Java source line #432	-> byte code offset #17
/*      */     //   Java source line #434	-> byte code offset #25
/*      */     //   Java source line #438	-> byte code offset #38
/*      */     //   Java source line #443	-> byte code offset #45
/*      */     //   Java source line #445	-> byte code offset #52
/*      */     //   Java source line #447	-> byte code offset #57
/*      */     //   Java source line #449	-> byte code offset #61
/*      */     //   Java source line #453	-> byte code offset #66
/*      */     //   Java source line #455	-> byte code offset #69
/*      */     //   Java source line #460	-> byte code offset #74
/*      */     //   Java source line #464	-> byte code offset #81
/*      */     //   Java source line #466	-> byte code offset #95
/*      */     //   Java source line #468	-> byte code offset #112
/*      */     //   Java source line #470	-> byte code offset #116
/*      */     //   Java source line #472	-> byte code offset #134
/*      */     //   Java source line #474	-> byte code offset #144
/*      */     //   Java source line #476	-> byte code offset #151
/*      */     //   Java source line #478	-> byte code offset #159
/*      */     //   Java source line #481	-> byte code offset #162
/*      */     //   Java source line #472	-> byte code offset #204
/*      */     //   Java source line #484	-> byte code offset #210
/*      */     //   Java source line #487	-> byte code offset #214
/*      */     //   Java source line #489	-> byte code offset #221
/*      */     //   Java source line #491	-> byte code offset #235
/*      */     //   Java source line #493	-> byte code offset #240
/*      */     //   Java source line #494	-> byte code offset #252
/*      */     //   Java source line #495	-> byte code offset #262
/*      */     //   Java source line #496	-> byte code offset #272
/*      */     //   Java source line #497	-> byte code offset #282
/*      */     //   Java source line #498	-> byte code offset #292
/*      */     //   Java source line #500	-> byte code offset #304
/*      */     //   Java source line #501	-> byte code offset #316
/*      */     //   Java source line #502	-> byte code offset #328
/*      */     //   Java source line #503	-> byte code offset #340
/*      */     //   Java source line #504	-> byte code offset #352
/*      */     //   Java source line #506	-> byte code offset #364
/*      */     //   Java source line #507	-> byte code offset #390
/*      */     //   Java source line #509	-> byte code offset #416
/*      */     //   Java source line #511	-> byte code offset #425
/*      */     //   Java source line #513	-> byte code offset #453
/*      */     //   Java source line #515	-> byte code offset #473
/*      */     //   Java source line #516	-> byte code offset #484
/*      */     //   Java source line #518	-> byte code offset #494
/*      */     //   Java source line #1551	-> byte code offset #518
/*      */     //   Java source line #1556	-> byte code offset #527
/*      */     //   Java source line #1558	-> byte code offset #539
/*      */     //   Java source line #1560	-> byte code offset #543
/*      */     //   Java source line #1564	-> byte code offset #548
/*      */     //   Java source line #1566	-> byte code offset #557
/*      */     //   Java source line #1568	-> byte code offset #563
/*      */     //   Java source line #1569	-> byte code offset #574
/*      */     //   Java source line #1573	-> byte code offset #582
/*      */     //   Java source line #1575	-> byte code offset #589
/*      */     //   Java source line #1579	-> byte code offset #596
/*      */     //   Java source line #1581	-> byte code offset #604
/*      */     //   Java source line #1585	-> byte code offset #611
/*      */     //   Java source line #1588	-> byte code offset #618
/*      */     //   Java source line #1590	-> byte code offset #668
/*      */     //   Java source line #1592	-> byte code offset #672
/*      */     //   Java source line #1594	-> byte code offset #680
/*      */     //   Java source line #1598	-> byte code offset #685
/*      */     //   Java source line #1601	-> byte code offset #691
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	699	0	dm	DownloadManager
/*      */     //   0	699	1	_file	org.gudy.azureus2.core3.disk.DiskManagerFileInfo
/*      */     //   0	699	2	anon	boolean
/*      */     //   0	699	3	launch	boolean
/*      */     //   7	492	4	props	Properties
/*      */     //   15	24	5	save_location	File
/*      */     //   33	3	6	root_dir	String
/*      */     //   43	234	6	root_dir	String
/*      */     //   76	3	7	url_suffix	String
/*      */     //   212	447	7	url_suffix	String
/*      */     //   50	67	8	always_browse	boolean
/*      */     //   67	445	9	file	org.gudy.azureus2.core3.disk.DiskManagerFileInfo
/*      */     //   93	3	10	relative_path	String
/*      */     //   218	476	10	Ljava/lang/Object;	Object
/*      */     //   110	35	11	bits	String[]
/*      */     //   233	413	11	plugin	WebPlugin
/*      */     //   114	97	12	_url_suffix	String
/*      */     //   388	135	12	plugin_id	String
/*      */     //   587	39	12	protocol	String
/*      */     //   132	8	13	bits_to_use	int
/*      */     //   414	88	13	plugin_name	String
/*      */     //   594	18	13	bind_ip	InetAddress
/*      */     //   135	70	14	i	int
/*      */     //   423	44	14	messages	Properties
/*      */     //   606	3	14	host	String
/*      */     //   616	20	14	host	String
/*      */     //   149	43	15	bit	String
/*      */     //   482	67	15	waiter	AESemaphore
/*      */     //   666	20	15	url	String
/*      */     //   492	72	16	url_holder	String[]
/*      */     //   560	17	17	Ljava/lang/Object;	Object
/*      */     //   574	6	18	localObject1	Object
/*      */     //   691	6	19	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   563	570	574	finally
/*      */     //   574	579	574	finally
/*      */     //   221	547	691	finally
/*      */     //   548	573	691	finally
/*      */     //   574	684	691	finally
/*      */     //   685	690	691	finally
/*      */     //   691	696	691	finally
/*      */   }
/*      */   
/*      */   public static URL getMediaServerContentURL(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/* 1608 */     PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/*      */     
/* 1610 */     PluginInterface pi = pm.getPluginInterfaceByID("azupnpav", false);
/*      */     
/* 1612 */     if (pi == null)
/*      */     {
/* 1614 */       return null;
/*      */     }
/*      */     
/* 1617 */     if (!pi.getPluginState().isOperational())
/*      */     {
/* 1619 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 1623 */       Object url = pi.getIPC().invoke("getContentURL", new Object[] { PluginCoreUtils.wrap(file) });
/*      */       
/* 1625 */       if ((url instanceof String))
/*      */       {
/* 1627 */         String s_url = (String)url;
/*      */         
/* 1629 */         if (s_url.length() > 0)
/*      */         {
/* 1631 */           return new URL(s_url);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1636 */       e.printStackTrace();
/*      */     }
/*      */     
/* 1639 */     return null;
/*      */   }
/*      */   
/*      */   public static class ArchiveCallback { public void success(DownloadStub source, DownloadStub target) {}
/*      */     
/*      */     public void failed(DownloadStub original, Throwable error) {}
/*      */     
/*      */     public void completed() {}
/*      */   }
/*      */   
/*      */   public static abstract interface RunDownloadManager { public abstract void run(DownloadManager paramDownloadManager); }
/*      */   
/* 1651 */   private static class UnloadableWebPlugin extends WebPlugin implements org.gudy.azureus2.plugins.UnloadablePlugin { private UnloadableWebPlugin(Properties props) { super(); }
/*      */     
/*      */ 
/*      */ 
/*      */     public void unload()
/*      */       throws PluginException
/*      */     {
/* 1658 */       super.unloadPlugin();
/*      */     }
/*      */   }
/*      */   
/*      */   public static boolean isStartable(DownloadManager dm) {
/* 1663 */     if (dm == null)
/* 1664 */       return false;
/* 1665 */     int state = dm.getState();
/* 1666 */     if (state != 70) {
/* 1667 */       return false;
/*      */     }
/* 1669 */     return true;
/*      */   }
/*      */   
/*      */   public static boolean isStopable(DownloadManager dm) {
/* 1673 */     if (dm == null)
/* 1674 */       return false;
/* 1675 */     int state = dm.getState();
/* 1676 */     if ((state == 70) || (state == 65))
/*      */     {
/* 1678 */       return false;
/*      */     }
/* 1680 */     return true;
/*      */   }
/*      */   
/*      */   public static boolean isPauseable(DownloadManager dm) {
/* 1684 */     if (dm == null)
/* 1685 */       return false;
/* 1686 */     int state = dm.getState();
/* 1687 */     if ((state == 70) || (state == 65) || (state == 100))
/*      */     {
/*      */ 
/* 1690 */       return false;
/*      */     }
/* 1692 */     return true;
/*      */   }
/*      */   
/*      */   public static boolean isStopped(DownloadManager dm) {
/* 1696 */     if (dm == null)
/* 1697 */       return false;
/* 1698 */     int state = dm.getState();
/* 1699 */     if ((state == 70) || (state == 100))
/*      */     {
/* 1701 */       return true;
/*      */     }
/* 1703 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean isForceStartable(DownloadManager dm)
/*      */   {
/* 1710 */     if (dm == null) {
/* 1711 */       return false;
/*      */     }
/*      */     
/* 1714 */     int state = dm.getState();
/*      */     
/* 1716 */     if ((state != 70) && (state != 75) && (state != 60) && (state != 50))
/*      */     {
/*      */ 
/* 1719 */       return false;
/*      */     }
/*      */     
/* 1722 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void host(AzureusCore azureus_core, DownloadManager dm)
/*      */   {
/* 1735 */     if (dm == null) {
/* 1736 */       return;
/*      */     }
/*      */     
/* 1739 */     TOTorrent torrent = dm.getTorrent();
/* 1740 */     if (torrent == null) {
/* 1741 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1745 */       azureus_core.getTrackerHost().hostTorrent(torrent, true, false);
/*      */     } catch (TRHostException e) {
/* 1747 */       MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("MyTorrentsView.menu.host.error.title"), MessageText.getString("MyTorrentsView.menu.host.error.message").concat("\n").concat(e.toString()));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1752 */       mb.open(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void publish(AzureusCore azureus_core, DownloadManager dm)
/*      */   {
/* 1766 */     if (dm == null) {
/* 1767 */       return;
/*      */     }
/*      */     
/* 1770 */     TOTorrent torrent = dm.getTorrent();
/* 1771 */     if (torrent == null) {
/* 1772 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1776 */       azureus_core.getTrackerHost().publishTorrent(torrent);
/*      */     } catch (TRHostException e) {
/* 1778 */       MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("MyTorrentsView.menu.host.error.title"), MessageText.getString("MyTorrentsView.menu.host.error.message").concat("\n").concat(e.toString()));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1783 */       mb.open(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void start(DownloadManager dm)
/*      */   {
/* 1792 */     if ((dm != null) && (dm.getState() == 70))
/*      */     {
/* 1794 */       dm.setStateWaiting();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void queue(DownloadManager dm, Composite panelNotUsed)
/*      */   {
/* 1803 */     if ((dm != null) && 
/* 1804 */       (dm.getState() == 70))
/*      */     {
/* 1806 */       dm.setStateQueued();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void pause(DownloadManager dm, Shell shell)
/*      */   {
/* 1822 */     if (dm == null) {
/* 1823 */       return;
/*      */     }
/*      */     
/* 1826 */     int state = dm.getState();
/*      */     
/* 1828 */     if ((state == 70) || (state == 65))
/*      */     {
/* 1830 */       return;
/*      */     }
/*      */     
/* 1833 */     asyncPause(dm);
/*      */   }
/*      */   
/*      */   public static void stop(DownloadManager dm, Shell shell) {
/* 1837 */     stop(dm, shell, 70);
/*      */   }
/*      */   
/*      */   public static void stop(DownloadManager dm, final Shell shell, final int stateAfterStopped)
/*      */   {
/* 1842 */     if (dm == null) {
/* 1843 */       return;
/*      */     }
/*      */     
/* 1846 */     int state = dm.getState();
/*      */     
/* 1848 */     if ((state == 70) || (state == 65) || (state == stateAfterStopped))
/*      */     {
/*      */ 
/* 1851 */       return;
/*      */     }
/*      */     
/* 1854 */     boolean stopme = true;
/* 1855 */     if (state == 60)
/*      */     {
/* 1857 */       if ((dm.getStats().getShareRatio() >= 0) && (dm.getStats().getShareRatio() < 1000) && (COConfigurationManager.getBooleanParameter("Alert on close", false)))
/*      */       {
/*      */ 
/* 1860 */         if (!Utils.isThisThreadSWT()) {
/* 1861 */           Utils.execSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/* 1863 */               ManagerUtils.stop(this.val$dm, shell, stateAfterStopped);
/*      */             }
/* 1865 */           });
/* 1866 */           return;
/*      */         }
/* 1868 */         Shell aShell = shell == null ? Utils.findAnyShell() : shell;
/* 1869 */         MessageBox mb = new MessageBox(aShell, 200);
/*      */         
/* 1871 */         mb.setText(MessageText.getString("seedmore.title"));
/* 1872 */         mb.setMessage(MessageText.getString("seedmore.shareratio") + dm.getStats().getShareRatio() / 10 + "%.\n" + MessageText.getString("seedmore.uploadmore"));
/*      */         
/*      */ 
/* 1875 */         int action = mb.open();
/* 1876 */         stopme = action == 64;
/*      */       }
/*      */     }
/*      */     
/* 1880 */     if (stopme) {
/* 1881 */       asyncStop(dm, stateAfterStopped);
/*      */     }
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public static void remove(DownloadManager dm, Shell unused_shell, boolean bDeleteTorrent, boolean bDeleteData)
/*      */   {
/* 1890 */     remove(dm, unused_shell, bDeleteTorrent, bDeleteData, null);
/*      */   }
/*      */   
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public static void remove(DownloadManager dm, Shell unused_shell, boolean bDeleteTorrent, boolean bDeleteData, AERunnable deleteFailed)
/*      */   {
/* 1899 */     org.gudy.azureus2.ui.swt.TorrentUtil.removeDownloads(new DownloadManager[] { dm }, null);
/* 1900 */     Debug.out("ManagerUtils.remove is Deprecated.  Use TorrentUtil.removeDownloads");
/*      */   }
/*      */   
/* 1903 */   private static AsyncDispatcher async = new AsyncDispatcher(2000);
/*      */   
/*      */ 
/*      */ 
/*      */   public static void asyncStopDelete(final DownloadManager dm, int stateAfterStopped, final boolean bDeleteTorrent, boolean bDeleteData, final AERunnable deleteFailed)
/*      */   {
/* 1909 */     TorrentUtils.startTorrentDelete();
/*      */     
/* 1911 */     final boolean[] endDone = { false };
/*      */     try
/*      */     {
/* 1914 */       async.dispatch(new AERunnable()
/*      */       {
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/* 1920 */             boolean reallyDeleteData = (this.val$bDeleteData) && (!dm.getDownloadState().getFlag(64L));
/*      */             
/*      */ 
/*      */ 
/* 1924 */             dm.getGlobalManager().removeDownloadManager(dm, bDeleteTorrent, reallyDeleteData);
/*      */           }
/*      */           catch (GlobalManagerDownloadRemovalVetoException f) {
/*      */             Tracker tracker;
/*      */             TOTorrent torrent;
/*      */             byte[] target_hash;
/*      */             ShareResource[] arr$;
/*      */             int i$;
/*      */             try {
/* 1933 */               PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface();
/*      */               
/* 1935 */               org.gudy.azureus2.plugins.sharing.ShareManager sm = pi.getShareManager();
/*      */               
/* 1937 */               tracker = pi.getTracker();
/*      */               
/* 1939 */               ShareResource[] shares = sm.getShares();
/*      */               
/* 1941 */               torrent = dm.getTorrent();
/*      */               
/* 1943 */               target_hash = torrent.getHash();
/*      */               
/* 1945 */               arr$ = shares;int len$ = arr$.length; for (i$ = 0; i$ < len$;) { ShareResource share = arr$[i$];
/*      */                 
/* 1947 */                 int type = share.getType();
/*      */                 
/*      */                 byte[] hash;
/*      */                 byte[] hash;
/* 1951 */                 if (type == 2)
/*      */                 {
/* 1953 */                   hash = ((ShareResourceDir)share).getItem().getTorrent().getHash();
/*      */                 } else { byte[] hash;
/* 1955 */                   if (type == 1)
/*      */                   {
/* 1957 */                     hash = ((org.gudy.azureus2.plugins.sharing.ShareResourceFile)share).getItem().getTorrent().getHash();
/*      */                   }
/*      */                   else
/*      */                   {
/* 1961 */                     hash = null;
/*      */                   }
/*      */                 }
/* 1964 */                 if (hash != null)
/*      */                 {
/* 1966 */                   if (Arrays.equals(target_hash, hash))
/*      */                   {
/*      */                     try {
/* 1969 */                       dm.stopIt(70, false, false);
/*      */                     }
/*      */                     catch (Throwable e) {}
/*      */                     
/*      */ 
/*      */                     try
/*      */                     {
/* 1976 */                       TrackerTorrent tracker_torrent = tracker.getTorrent(PluginCoreUtils.wrap(torrent));
/*      */                       
/* 1978 */                       if (tracker_torrent != null)
/*      */                       {
/* 1980 */                         tracker_torrent.stop();
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {}
/*      */                     
/* 1985 */                     share.delete(); return;
/*      */                   }
/*      */                 }
/* 1945 */                 i$++;
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1996 */             if (!f.isSilent()) {
/* 1997 */               UIFunctionsManager.getUIFunctions().forceNotify(1, MessageText.getString("globalmanager.download.remove.veto"), f.getMessage(), null, null, -1);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2005 */             if (deleteFailed != null) {
/* 2006 */               deleteFailed.runSupport();
/*      */             }
/*      */           } catch (Exception ex) {
/* 2009 */             Debug.printStackTrace(ex);
/* 2010 */             if (deleteFailed != null) {
/* 2011 */               deleteFailed.runSupport();
/*      */             }
/*      */           }
/*      */           finally {
/* 2015 */             synchronized (endDone)
/*      */             {
/* 2017 */               if (endDone[0] == 0)
/*      */               {
/* 2019 */                 TorrentUtils.endTorrentDelete();
/*      */                 
/* 2021 */                 endDone[0] = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2030 */       synchronized (endDone)
/*      */       {
/* 2032 */         if (endDone[0] == 0)
/*      */         {
/* 2034 */           TorrentUtils.endTorrentDelete();
/*      */           
/* 2036 */           endDone[0] = true;
/*      */         }
/*      */       }
/*      */       
/* 2040 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void asyncStop(DownloadManager dm, final int stateAfterStopped)
/*      */   {
/* 2049 */     async.dispatch(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/* 2053 */         this.val$dm.stopIt(stateAfterStopped, false, false);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void asyncPause(DownloadManager dm)
/*      */   {
/* 2062 */     async.dispatch(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/* 2066 */         this.val$dm.pause();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void asyncStartAll() {
/* 2072 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*      */     {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/* 2075 */         core.getGlobalManager().startAllDownloads();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void asyncStopAll() {
/* 2081 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*      */     {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/* 2084 */         core.getGlobalManager().stopAllDownloads();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void asyncPause() {
/* 2090 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*      */     {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/* 2093 */         core.getGlobalManager().pauseDownloads();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void asyncPauseForPeriod(int seconds) {
/* 2099 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*      */     {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/* 2102 */         core.getGlobalManager().pauseDownloadsForPeriod(this.val$seconds);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/* 2107 */   public static void asyncResume() { CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*      */     {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/* 2110 */         core.getGlobalManager().resumeDownloads();
/*      */       }
/*      */     }); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void asyncPauseForPeriod(final List<DownloadManager> dms, int seconds)
/*      */   {
/* 2120 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*      */     {
/*      */ 
/*      */       public void azureusCoreRunning(AzureusCore core)
/*      */       {
/* 2125 */         final List<DownloadManager> paused = new ArrayList();
/*      */         
/* 2127 */         final DownloadManagerListener listener = new org.gudy.azureus2.core3.download.impl.DownloadManagerAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void stateChanged(DownloadManager manager, int state)
/*      */           {
/*      */ 
/*      */ 
/* 2135 */             synchronized (paused)
/*      */             {
/* 2137 */               if (!paused.remove(manager))
/*      */               {
/* 2139 */                 return;
/*      */               }
/*      */             }
/*      */             
/* 2143 */             manager.removeListener(this);
/*      */           }
/*      */           
/* 2146 */         };
/* 2147 */         long target_time = SystemTime.getOffsetTime(this.val$seconds * 1000);
/*      */         
/* 2149 */         for (DownloadManager dm : dms)
/*      */         {
/* 2151 */           if (ManagerUtils.isPauseable(dm))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2156 */             if (dm.pause(target_time))
/*      */             {
/* 2158 */               synchronized (paused)
/*      */               {
/* 2160 */                 paused.add(dm);
/*      */               }
/*      */               
/* 2163 */               dm.addListener(listener, false);
/*      */             }
/*      */           }
/*      */         }
/* 2167 */         if (paused.size() > 0)
/*      */         {
/* 2169 */           SimpleTimer.addEvent("ManagerUtils.resumer", target_time, new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */ 
/* 2178 */               List<DownloadManager> to_resume = new ArrayList();
/*      */               
/* 2180 */               synchronized (paused)
/*      */               {
/* 2182 */                 to_resume.addAll(paused);
/*      */                 
/* 2184 */                 paused.clear();
/*      */               }
/*      */               
/* 2187 */               for (DownloadManager dm : to_resume)
/*      */               {
/* 2189 */                 dm.removeListener(listener);
/*      */                 try
/*      */                 {
/* 2192 */                   dm.resume();
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 2196 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void moveToArchive(List<Download> downloads, ArchiveCallback _run_when_complete)
/*      */   {
/* 2235 */     final ArchiveCallback run_when_complete = _run_when_complete == null ? new ArchiveCallback() : _run_when_complete;
/*      */     
/* 2237 */     Utils.getOffOfSWTThread(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/* 2245 */           String title = MessageText.getString("archive.info.title");
/* 2246 */           String text = MessageText.getString("archive.info.text");
/*      */           
/* 2248 */           MessageBoxShell prompter = new MessageBoxShell(title, text, new String[] { MessageText.getString("Button.ok") }, 0);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2254 */           String remember_id = "managerutils.archive.info";
/*      */           
/* 2256 */           prompter.setRemember(remember_id, true, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2261 */           prompter.setAutoCloseInMS(0);
/*      */           
/* 2263 */           prompter.open(null);
/*      */           
/* 2265 */           prompter.waitUntilClosed();
/*      */           
/* 2267 */           for (Download dm : this.val$downloads) {
/*      */             try
/*      */             {
/* 2270 */               DownloadStub stub = dm.stubbify();
/*      */               
/* 2272 */               run_when_complete.success(dm, stub);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2276 */               run_when_complete.failed(dm, e);
/*      */               
/* 2278 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 2283 */           run_when_complete.completed();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void restoreFromArchive(List<DownloadStub> downloads, final boolean start, ArchiveCallback _run_when_complete)
/*      */   {
/* 2295 */     final ArchiveCallback run_when_complete = _run_when_complete == null ? new ArchiveCallback() : _run_when_complete;
/*      */     
/* 2297 */     Utils.getOffOfSWTThread(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/* 2305 */           TagManager tag_manager = null;
/* 2306 */           Tag tag_restored = null;
/*      */           try
/*      */           {
/* 2309 */             tag_manager = com.aelitis.azureus.core.tag.TagManagerFactory.getTagManager();
/*      */             
/* 2311 */             TagType tt = tag_manager.getTagType(3);
/*      */             
/* 2313 */             String tag_name = MessageText.getString("label.restored");
/*      */             
/* 2315 */             tag_restored = tt.getTag(tag_name, true);
/*      */             
/* 2317 */             if (tag_restored == null)
/*      */             {
/* 2319 */               tag_restored = tt.createTag(tag_name, true);
/*      */               
/* 2321 */               tag_restored.setPublic(false);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 2325 */             Debug.out(e);
/*      */           }
/*      */           try
/*      */           {
/* 2329 */             if (tag_manager != null)
/*      */             {
/* 2331 */               tag_manager.setProcessingEnabled(false);
/*      */             }
/*      */             
/* 2334 */             for (DownloadStub dm : this.val$downloads) {
/*      */               try
/*      */               {
/* 2337 */                 Download dl = dm.destubbify();
/*      */                 
/* 2339 */                 if (dl != null)
/*      */                 {
/* 2341 */                   run_when_complete.success(dm, dl);
/*      */                   
/* 2343 */                   if (tag_restored != null)
/*      */                   {
/* 2345 */                     tag_restored.addTaggable(PluginCoreUtils.unwrap(dl));
/*      */                   }
/*      */                   
/* 2348 */                   if (start)
/*      */                   {
/* 2350 */                     ManagerUtils.start(PluginCoreUtils.unwrap(dl));
/*      */                   }
/*      */                 }
/*      */                 else {
/* 2354 */                   run_when_complete.failed(dm, new Exception("Unknown error"));
/*      */                 }
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 2359 */                 run_when_complete.failed(dm, e);
/*      */                 
/* 2361 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/* 2366 */             if (tag_manager != null)
/*      */             {
/* 2368 */               tag_manager.setProcessingEnabled(true);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 2374 */           run_when_complete.completed();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static DownloadManager[] cleanUp(DownloadManager[] dms)
/*      */   {
/* 2384 */     List<DownloadManager> result = new ArrayList();
/*      */     
/* 2386 */     if (dms != null)
/*      */     {
/* 2388 */       for (DownloadManager dm : dms)
/*      */       {
/* 2390 */         if ((dm != null) && (!dm.isDestroyed()))
/*      */         {
/* 2392 */           result.add(dm);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2397 */     return (DownloadManager[])result.toArray(new DownloadManager[result.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void locateFiles(DownloadManager[] dms, Shell shell)
/*      */   {
/* 2405 */     locateFiles(dms, (org.gudy.azureus2.core3.disk.DiskManagerFileInfo[][])null, shell);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void locateFiles(final DownloadManager[] dms, final org.gudy.azureus2.core3.disk.DiskManagerFileInfo[][] dm_files, Shell shell)
/*      */   {
/* 2414 */     DirectoryDialog dd = new DirectoryDialog(shell);
/*      */     
/* 2416 */     dd.setFilterPath(TorrentOpener.getFilterPathData());
/*      */     
/* 2418 */     dd.setText(MessageText.getString("MyTorrentsView.menu.locatefiles.dialog"));
/*      */     
/* 2420 */     String path = dd.open();
/*      */     
/* 2422 */     if (path != null)
/*      */     {
/* 2424 */       TorrentOpener.setFilterPathData(path);
/*      */       
/* 2426 */       final File dir = new File(path);
/*      */       
/* 2428 */       final TextViewerWindow viewer = new TextViewerWindow(MessageText.getString("locatefiles.view.title"), null, "", true, true);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2433 */       viewer.setEditable(false);
/*      */       
/* 2435 */       viewer.setOKEnabled(true);
/*      */       
/* 2437 */       new AEThread2("FileLocator")
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/* 2442 */           int MAX_LINKS = org.gudy.azureus2.core3.download.DownloadManagerStateFactory.MAX_FILES_FOR_INCOMPLETE_AND_DND_LINKAGE;
/* 2443 */           String LINK_LIMIT_MSG = "Link limit of " + MAX_LINKS + " exceeded. See Tools->Options->Files to increase this";
/*      */           try
/*      */           {
/* 2446 */             Map<Long, Set<File>> file_map = new HashMap();
/*      */             
/* 2448 */             final boolean[] quit = { false };
/*      */             
/* 2450 */             viewer.addListener(new org.gudy.azureus2.ui.swt.TextViewerWindow.TextViewerWindowListener()
/*      */             {
/*      */               public void closed()
/*      */               {
/* 2454 */                 synchronized (quit) {
/* 2455 */                   quit[0] = true;
/*      */                 }
/*      */                 
/*      */               }
/* 2459 */             });
/* 2460 */             ManagerUtils.logLine(viewer, new SimpleDateFormat().format(new Date()) + ": Enumerating files in " + dir);
/*      */             
/* 2462 */             long bfm_start = SystemTime.getMonotonousTime();
/*      */             
/* 2464 */             long[] last_log = { bfm_start };
/*      */             
/* 2466 */             int file_count = ManagerUtils.buildFileMap(viewer, dir, file_map, last_log, quit);
/*      */             
/* 2468 */             ManagerUtils.logLine(viewer, (bfm_start == last_log[0] ? "" : "\r\n") + "Found " + file_count + " files with " + file_map.size() + " distinct sizes");
/*      */             
/* 2470 */             Set<String> all_dm_incomplete_files = null;
/*      */             
/* 2472 */             ConcurrentHasher hasher = ConcurrentHasher.getSingleton();
/*      */             
/* 2474 */             int downloads_modified = 0;
/*      */             
/* 2476 */             for (int i = 0; i < dms.length; i++)
/*      */             {
/* 2478 */               DownloadManager dm = dms[i];
/*      */               
/* 2480 */               synchronized (quit) {
/* 2481 */                 if (quit[0] != 0) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */               
/* 2486 */               if (dm.isPersistent())
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2491 */                 TOTorrent torrent = dm.getTorrent();
/*      */                 
/* 2493 */                 if (torrent != null)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 2498 */                   org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] selected_files = dm_files == null ? null : dm_files[i];
/*      */                   
/*      */                   Set<Integer> selected_file_indexes;
/*      */                   Set<Integer> selected_file_indexes;
/* 2502 */                   if (selected_files == null)
/*      */                   {
/* 2504 */                     selected_file_indexes = null;
/*      */                   }
/*      */                   else
/*      */                   {
/* 2508 */                     selected_file_indexes = new HashSet();
/*      */                     
/* 2510 */                     for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo f : selected_files)
/*      */                     {
/* 2512 */                       selected_file_indexes.add(Integer.valueOf(f.getIndex()));
/*      */                     }
/*      */                   }
/*      */                   
/* 2516 */                   TOTorrentFile[] to_files = torrent.getFiles();
/*      */                   
/* 2518 */                   long piece_size = torrent.getPieceLength();
/*      */                   
/* 2520 */                   byte[][] pieces = torrent.getPieces();
/*      */                   
/* 2522 */                   ManagerUtils.logLine(viewer, "Processing '" + dm.getDisplayName() + "', piece size=" + DisplayFormatters.formatByteCountToKiBEtc(piece_size));
/*      */                   
/* 2524 */                   int dm_state = dm.getState();
/*      */                   
/* 2526 */                   if ((dm_state != 70) && (dm_state != 100))
/*      */                   {
/* 2528 */                     ManagerUtils.logLine(viewer, "    Download must be stopped");
/*      */ 
/*      */                   }
/*      */                   else
/*      */                   {
/* 2533 */                     org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files = dm.getDiskManagerFileInfoSet().getFiles();
/*      */                     
/* 2535 */                     Set<String> dm_files = null;
/*      */                     
/* 2537 */                     Map<org.gudy.azureus2.core3.disk.DiskManagerFileInfo, File> links_established = new HashMap();
/*      */                     
/* 2539 */                     Map<org.gudy.azureus2.core3.disk.DiskManagerFileInfo, Set<String>> unmatched_files = new java.util.TreeMap(new Comparator()
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*      */                       public int compare(org.gudy.azureus2.core3.disk.DiskManagerFileInfo o1, org.gudy.azureus2.core3.disk.DiskManagerFileInfo o2)
/*      */                       {
/*      */ 
/*      */ 
/* 2548 */                         long diff = o2.getLength() - o1.getLength();
/*      */                         
/* 2550 */                         if (diff < 0L)
/* 2551 */                           return -1;
/* 2552 */                         if (diff > 0L) {
/* 2553 */                           return 1;
/*      */                         }
/* 2555 */                         return 0;
/*      */                       }
/*      */                       
/*      */ 
/* 2559 */                     });
/* 2560 */                     int no_candidates = 0;
/* 2561 */                     int already_complete = 0;
/*      */                     
/* 2563 */                     int link_count = 0;
/*      */                     
/*      */                     try
/*      */                     {
/*      */                       label1798:
/* 2568 */                       for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : files)
/*      */                       {
/* 2570 */                         synchronized (quit) {
/* 2571 */                           if (quit[0] != 0) {
/*      */                             break;
/*      */                           }
/*      */                         }
/*      */                         
/* 2576 */                         if ((selected_file_indexes == null) || 
/*      */                         
/* 2578 */                           (selected_file_indexes.contains(Integer.valueOf(file.getIndex()))))
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2584 */                           long file_length = file.getLength();
/*      */                           
/* 2586 */                           if (file.getDownloaded() == file_length)
/*      */                           {
/* 2588 */                             already_complete++;
/*      */ 
/*      */                           }
/*      */                           else
/*      */                           {
/* 2593 */                             Set<File> candidates = (Set)file_map.get(Long.valueOf(file_length));
/*      */                             
/* 2595 */                             if (candidates != null)
/*      */                             {
/* 2597 */                               if (candidates.size() > 0)
/*      */                               {
/*      */ 
/*      */ 
/* 2601 */                                 if (all_dm_incomplete_files == null)
/*      */                                 {
/* 2603 */                                   all_dm_incomplete_files = new HashSet();
/*      */                                   
/* 2605 */                                   List<DownloadManager> all_dms = AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManagers();
/*      */                                   
/* 2607 */                                   for (DownloadManager x : all_dms)
/*      */                                   {
/* 2609 */                                     if (!x.isDownloadComplete(false))
/*      */                                     {
/* 2611 */                                       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] fs = x.getDiskManagerFileInfoSet().getFiles();
/*      */                                       
/* 2613 */                                       for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo f : fs)
/*      */                                       {
/* 2615 */                                         if ((f.isSkipped()) || (f.getDownloaded() != f.getLength()))
/*      */                                         {
/*      */ 
/* 2618 */                                           all_dm_incomplete_files.add(f.getFile(true).getAbsolutePath());
/*      */                                         }
/*      */                                       }
/*      */                                     }
/*      */                                   }
/*      */                                 }
/*      */                                 
/* 2625 */                                 Iterator<File> it = candidates.iterator();
/*      */                                 
/* 2627 */                                 while (it.hasNext())
/*      */                                 {
/* 2629 */                                   File f = (File)it.next();
/*      */                                   
/* 2631 */                                   if (all_dm_incomplete_files.contains(f.getAbsolutePath()))
/*      */                                   {
/* 2633 */                                     it.remove();
/*      */                                   }
/*      */                                 }
/*      */                               }
/*      */                               
/* 2638 */                               if (candidates.size() > 0)
/*      */                               {
/*      */ 
/*      */ 
/* 2642 */                                 candidates = new HashSet(candidates);
/*      */                                 
/*      */ 
/*      */ 
/* 2646 */                                 if (dm_files == null)
/*      */                                 {
/* 2648 */                                   dm_files = new HashSet();
/*      */                                   
/* 2650 */                                   for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo f : files)
/*      */                                   {
/* 2652 */                                     dm_files.add(f.getFile(true).getAbsolutePath());
/*      */                                   }
/*      */                                 }
/*      */                                 
/* 2656 */                                 Iterator<File> it = candidates.iterator();
/*      */                                 
/* 2658 */                                 while (it.hasNext())
/*      */                                 {
/* 2660 */                                   File f = (File)it.next();
/*      */                                   
/* 2662 */                                   if (dm_files.contains(f.getAbsolutePath()))
/*      */                                   {
/* 2664 */                                     it.remove();
/*      */                                   }
/*      */                                 }
/*      */                               }
/*      */                               
/* 2669 */                               if (candidates.size() > 0)
/*      */                               {
/* 2671 */                                 boolean matched = false;
/*      */                                 
/* 2673 */                                 Set<String> failed_candidates = new HashSet();
/*      */                                 
/* 2675 */                                 TOTorrentFile to_file = file.getTorrentFile();
/*      */                                 
/* 2677 */                                 long offset = 0L;
/*      */                                 
/* 2679 */                                 for (TOTorrentFile tf : to_files)
/*      */                                 {
/* 2681 */                                   if (tf == to_file) {
/*      */                                     break;
/*      */                                   }
/*      */                                   
/*      */ 
/* 2686 */                                   offset += tf.getLength();
/*      */                                 }
/*      */                                 
/* 2689 */                                 int to_piece_number = to_file.getFirstPieceNumber();
/*      */                                 
/* 2691 */                                 long to_file_offset = offset % piece_size;
/*      */                                 
/* 2693 */                                 if (to_file_offset != 0L)
/*      */                                 {
/* 2695 */                                   to_file_offset = piece_size - to_file_offset;
/*      */                                   
/* 2697 */                                   to_piece_number++;
/*      */                                 }
/*      */                                 
/* 2700 */                                 long to_stop_at = file_length - piece_size;
/*      */                                 byte[] buffer;
/* 2702 */                                 if (to_file_offset < to_stop_at)
/*      */                                 {
/* 2704 */                                   ManagerUtils.logLine(viewer, "    " + candidates.size() + " candidate(s) for " + to_file.getRelativePath() + " (size=" + DisplayFormatters.formatByteCountToKiBEtc(to_file.getLength()) + ")");
/*      */                                   
/* 2706 */                                   buffer = new byte[(int)piece_size];
/*      */                                   
/* 2708 */                                   for (File candidate : candidates)
/*      */                                   {
/* 2710 */                                     synchronized (quit) {
/* 2711 */                                       if (quit[0] != 0) {
/*      */                                         break;
/*      */                                       }
/*      */                                     }
/*      */                                     
/* 2716 */                                     ManagerUtils.log(viewer, "        Testing " + candidate);
/*      */                                     
/* 2718 */                                     RandomAccessFile raf = null;
/*      */                                     
/* 2720 */                                     boolean error = false;
/* 2721 */                                     boolean hash_failed = false;
/*      */                                     
/* 2723 */                                     long last_ok_log = SystemTime.getMonotonousTime();
/*      */                                     try
/*      */                                     {
/* 2726 */                                       raf = new RandomAccessFile(candidate, "r");
/*      */                                       
/* 2728 */                                       long file_offset = to_file_offset;
/* 2729 */                                       int piece_number = to_piece_number;
/*      */                                       
/* 2731 */                                       while (file_offset < to_stop_at)
/*      */                                       {
/* 2733 */                                         synchronized (quit) {
/* 2734 */                                           if (quit[0] != 0) {
/*      */                                             break;
/*      */                                           }
/*      */                                         }
/*      */                                         
/* 2739 */                                         raf.seek(file_offset);
/*      */                                         
/* 2741 */                                         raf.read(buffer);
/*      */                                         
/* 2743 */                                         ConcurrentHasherRequest req = hasher.addRequest(java.nio.ByteBuffer.wrap(buffer));
/*      */                                         
/* 2745 */                                         byte[] hash = req.getResult();
/*      */                                         
/* 2747 */                                         boolean match = Arrays.equals(pieces[piece_number], hash);
/*      */                                         
/* 2749 */                                         if (match)
/*      */                                         {
/* 2751 */                                           long now = SystemTime.getMonotonousTime();
/*      */                                           
/* 2753 */                                           if (now - last_ok_log >= 250L)
/*      */                                           {
/* 2755 */                                             last_ok_log = now;
/*      */                                             
/* 2757 */                                             ManagerUtils.log(viewer, ".");
/*      */                                           }
/*      */                                           
/* 2760 */                                           file_offset += piece_size;
/* 2761 */                                           piece_number++;
/*      */                                         }
/*      */                                         else
/*      */                                         {
/* 2765 */                                           hash_failed = true;
/*      */                                           
/* 2767 */                                           failed_candidates.add(candidate.getAbsolutePath());
/*      */                                           
/* 2769 */                                           ManagerUtils.logLine(viewer, "X");
/*      */                                           
/* 2771 */                                           break;
/*      */                                         }
/*      */                                       }
/*      */                                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2783 */                                       if (raf != null) {
/*      */                                         try
/*      */                                         {
/* 2786 */                                           raf.close();
/*      */                                         }
/*      */                                         catch (Throwable e) {}
/*      */                                       }
/*      */                                       
/*      */ 
/*      */ 
/*      */ 
/* 2794 */                                       if (error) {
/*      */                                         break label1798;
/*      */                                       }
/*      */                                     }
/*      */                                     catch (Throwable e)
/*      */                                     {
/* 2777 */                                       ManagerUtils.logLine(viewer, "X");
/*      */                                       
/* 2779 */                                       error = true;
/*      */                                     }
/*      */                                     finally
/*      */                                     {
/* 2783 */                                       if (raf != null) {
/*      */                                         try
/*      */                                         {
/* 2786 */                                           raf.close();
/*      */                                         }
/*      */                                         catch (Throwable e) {}
/*      */                                       }
/*      */                                     }
/*      */                                     
/*      */ 
/*      */ 
/* 2794 */                                     if (!hash_failed)
/*      */                                     {
/* 2796 */                                       ManagerUtils.logLine(viewer, " Matched");
/*      */                                       try
/*      */                                       {
/* 2799 */                                         dm.setUserData("set_link_dont_delete_existing", Boolean.valueOf(true));
/*      */                                         
/* 2801 */                                         if (file.setLink(candidate))
/*      */                                         {
/* 2803 */                                           ManagerUtils.logLine(viewer, "        Link successful");
/*      */                                           
/* 2805 */                                           links_established.put(file, candidate);
/*      */                                           
/* 2807 */                                           link_count++;
/*      */                                           
/* 2809 */                                           matched = true;
/*      */                                           
/* 2811 */                                           if (link_count > MAX_LINKS)
/*      */                                           {
/* 2813 */                                             ManagerUtils.logLine(viewer, "    " + LINK_LIMIT_MSG);
/*      */                                             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2823 */                                             dm.setUserData("set_link_dont_delete_existing", null);
/*      */                                             break label1836;
/*      */                                           }
/*      */                                         }
/*      */                                         else
/*      */                                         {
/* 2819 */                                           ManagerUtils.logLine(viewer, "        Link failed");
/*      */                                         }
/*      */                                       }
/*      */                                       finally {
/* 2823 */                                         dm.setUserData("set_link_dont_delete_existing", null);
/*      */                                       }
/*      */                                       
/* 2826 */                                       break;
/*      */                                     }
/*      */                                   }
/*      */                                 }
/*      */                                 
/*      */ 
/* 2832 */                                 if (!matched)
/*      */                                 {
/* 2834 */                                   unmatched_files.put(file, failed_candidates);
/*      */                                 }
/*      */                               }
/*      */                               else {
/* 2838 */                                 no_candidates++;
/*      */                               }
/*      */                             }
/*      */                             else {
/* 2842 */                               no_candidates++;
/*      */                             }
/*      */                           } } }
/*      */                       label1836:
/* 2846 */                       ManagerUtils.logLine(viewer, "    Matched=" + links_established.size() + ", complete=" + already_complete + ", no candidates=" + no_candidates + ", remaining=" + unmatched_files.size() + " (total=" + files.length + ")");
/*      */                       
/* 2848 */                       if ((links_established.size() > 0) && (unmatched_files.size() > 0))
/*      */                       {
/* 2850 */                         ManagerUtils.logLine(viewer, "    Looking for other potential name-based matches");
/*      */                         
/* 2852 */                         File overall_root = null;
/*      */                         
/* 2854 */                         for (Map.Entry<org.gudy.azureus2.core3.disk.DiskManagerFileInfo, File> entry : links_established.entrySet())
/*      */                         {
/* 2856 */                           org.gudy.azureus2.core3.disk.DiskManagerFileInfo dm_file = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)entry.getKey();
/* 2857 */                           File root = (File)entry.getValue();
/*      */                           
/* 2859 */                           String rel = dm_file.getTorrentFile().getRelativePath();
/*      */                           
/* 2861 */                           int pos = 0;
/*      */                           
/* 2863 */                           while (root != null)
/*      */                           {
/* 2865 */                             root = root.getParentFile();
/*      */                             
/* 2867 */                             pos = rel.indexOf(File.separatorChar, pos);
/*      */                             
/* 2869 */                             if (pos < 0)
/*      */                               break;
/* 2871 */                             pos += 1;
/*      */                           }
/*      */                           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2879 */                           if (root == null)
/*      */                           {
/* 2881 */                             ManagerUtils.logLine(viewer, "        No usable root folder found");
/*      */                             
/* 2883 */                             break;
/*      */                           }
/*      */                           
/* 2886 */                           if (overall_root == null)
/*      */                           {
/* 2888 */                             overall_root = root;
/*      */ 
/*      */ 
/*      */                           }
/* 2892 */                           else if (!overall_root.equals(root))
/*      */                           {
/* 2894 */                             overall_root = null;
/*      */                             
/* 2896 */                             ManagerUtils.logLine(viewer, "        Inconsistent root folder found");
/*      */                             
/* 2898 */                             break;
/*      */                           }
/*      */                         }
/*      */                         
/*      */ 
/* 2903 */                         if (overall_root != null)
/*      */                         {
/* 2905 */                           ManagerUtils.logLine(viewer, "        Root folder is " + overall_root.getAbsolutePath());
/*      */                           
/* 2907 */                           int links_ok = 0;
/*      */                           
/* 2909 */                           for (Map.Entry<org.gudy.azureus2.core3.disk.DiskManagerFileInfo, Set<String>> entry : unmatched_files.entrySet())
/*      */                           {
/* 2911 */                             synchronized (quit) {
/* 2912 */                               if (quit[0] != 0) {
/*      */                                 break;
/*      */                               }
/*      */                             }
/*      */                             
/* 2917 */                             org.gudy.azureus2.core3.disk.DiskManagerFileInfo file = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)entry.getKey();
/*      */                             
/* 2919 */                             if ((selected_file_indexes == null) || 
/*      */                             
/* 2921 */                               (selected_file_indexes.contains(Integer.valueOf(file.getIndex()))))
/*      */                             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2927 */                               File expected_file = new File(overall_root, file.getTorrentFile().getRelativePath());
/*      */                               
/* 2929 */                               if ((expected_file.exists()) && (expected_file.length() == file.getLength()))
/*      */                               {
/* 2931 */                                 if (!((Set)entry.getValue()).contains(expected_file.getAbsolutePath())) {
/*      */                                   try
/*      */                                   {
/* 2934 */                                     dm.setUserData("set_link_dont_delete_existing", Boolean.valueOf(true));
/*      */                                     
/* 2936 */                                     if (file.setLink(expected_file))
/*      */                                     {
/* 2938 */                                       links_ok++;
/*      */                                       
/* 2940 */                                       link_count++;
/*      */                                       
/* 2942 */                                       if (link_count > MAX_LINKS)
/*      */                                       {
/* 2944 */                                         ManagerUtils.logLine(viewer, "        " + LINK_LIMIT_MSG);
/*      */                                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2951 */                                         dm.setUserData("set_link_dont_delete_existing", null); break; } } } finally { dm.setUserData("set_link_dont_delete_existing", null);
/*      */                                   }
/*      */                                 }
/*      */                               }
/*      */                             }
/*      */                           }
/* 2957 */                           ManagerUtils.logLine(viewer, "        Linked " + links_ok + " of " + unmatched_files.size());
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     finally {
/* 2962 */                       if (link_count > 0)
/*      */                       {
/* 2964 */                         dm.forceRecheck();
/*      */                         
/* 2966 */                         downloads_modified++;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 } } }
/* 2971 */             ManagerUtils.logLine(viewer, new SimpleDateFormat().format(new Date()) + ": Complete, downloads updated=" + downloads_modified);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2975 */             ManagerUtils.log(viewer, "\r\n" + new SimpleDateFormat().format(new Date()) + ": Failed: " + Debug.getNestedExceptionMessage(e) + "\r\n");
/*      */           }
/*      */           
/*      */         }
/* 2979 */       }.start();
/* 2980 */       viewer.goModal();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void logLine(TextViewerWindow viewer, String str)
/*      */   {
/* 2989 */     log(viewer, str + "\r\n");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void log(TextViewerWindow viewer, final String str)
/*      */   {
/* 2997 */     Utils.execSWTThread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 3003 */         if (!this.val$viewer.isDisposed())
/*      */         {
/* 3005 */           this.val$viewer.append(str);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int buildFileMap(TextViewerWindow viewer, File dir, Map<Long, Set<File>> map, long[] last_log, boolean[] quit)
/*      */   {
/* 3019 */     File[] files = dir.listFiles();
/*      */     
/* 3021 */     int total_files = 0;
/*      */     
/* 3023 */     if (files != null)
/*      */     {
/* 3025 */       for (File f : files)
/*      */       {
/* 3027 */         if (quit[0] != 0)
/*      */         {
/* 3029 */           return total_files;
/*      */         }
/*      */         
/* 3032 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 3034 */         if (now - last_log[0] > 250L)
/*      */         {
/* 3036 */           log(viewer, ".");
/*      */           
/* 3038 */           last_log[0] = now;
/*      */         }
/*      */         
/* 3041 */         if (f.isDirectory())
/*      */         {
/* 3043 */           total_files += buildFileMap(viewer, f, map, last_log, quit);
/*      */         }
/*      */         else
/*      */         {
/* 3047 */           long size = f.length();
/*      */           
/* 3049 */           if (size > 0L)
/*      */           {
/* 3051 */             total_files++;
/*      */             
/* 3053 */             Set<File> list = (Set)map.get(Long.valueOf(size));
/*      */             
/* 3055 */             if (list == null)
/*      */             {
/* 3057 */               list = new HashSet();
/*      */               
/* 3059 */               map.put(Long.valueOf(size), list);
/*      */             }
/*      */             
/* 3062 */             list.add(f);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3068 */     return total_files;
/*      */   }
/*      */   
/*      */   public static boolean canFindMoreLikeThis()
/*      */   {
/*      */     try
/*      */     {
/* 3075 */       PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("aercm");
/*      */       
/* 3077 */       if ((pi != null) && (pi.getPluginState().isOperational())) if (pi.getIPC().canInvoke("lookupByExpression", new Object[] { "", new String[0], new HashMap() }))
/*      */         {
/*      */ 
/*      */ 
/* 3081 */           return true;
/*      */         }
/*      */     }
/*      */     catch (Throwable e) {
/* 3085 */       Debug.out(e);
/*      */     }
/*      */     
/* 3088 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void findMoreLikeThis(DownloadManager dm, Shell shell)
/*      */   {
/* 3096 */     findMoreLikeThis(dm, null, shell);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void findMoreLikeThis(org.gudy.azureus2.core3.disk.DiskManagerFileInfo file, Shell shell)
/*      */   {
/* 3104 */     findMoreLikeThis(file.getDownloadManager(), file, shell);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void findMoreLikeThis(DownloadManager dm, org.gudy.azureus2.core3.disk.DiskManagerFileInfo file, Shell shell)
/*      */   {
/* 3113 */     String expression = file == null ? dm.getDisplayName() : file.getFile(true).getName();
/*      */     
/* 3115 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("find.more.like.title", "find.more.like.msg");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3120 */     entryWindow.setPreenteredText(expression, false);
/*      */     
/* 3122 */     entryWindow.selectPreenteredText(true);
/*      */     
/* 3124 */     entryWindow.prompt(new org.gudy.azureus2.plugins.ui.UIInputReceiverListener() {
/*      */       public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/* 3126 */         if (!entryWindow.hasSubmittedInput()) {
/* 3127 */           return;
/*      */         }
/*      */         
/* 3130 */         String expression = entryWindow.getSubmittedInput();
/*      */         
/* 3132 */         if ((expression != null) && (expression.trim().length() > 0))
/*      */         {
/* 3134 */           expression = expression.trim();
/*      */           try
/*      */           {
/* 3137 */             PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("aercm");
/*      */             
/* 3139 */             if ((pi != null) && (pi.getPluginState().isOperational())) { if (pi.getIPC().canInvoke("lookupByExpression", new Object[] { "", new String[0], new HashMap() }))
/*      */               {
/*      */ 
/*      */ 
/* 3143 */                 Map<String, Object> options = new HashMap();
/*      */                 
/* 3145 */                 options.put("Subscription", Boolean.valueOf(true));
/* 3146 */                 options.put("Name", MessageText.getString("label.more") + ": " + expression);
/*      */                 
/* 3148 */                 pi.getIPC().invoke("lookupByExpression", new Object[] { expression, this.val$dm.getDownloadState().getNetworks(), options });
/*      */ 
/*      */               }
/*      */               
/*      */             }
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 3157 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/utils/ManagerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */