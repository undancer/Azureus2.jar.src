/*     */ package com.aelitis.azureus.core.lws;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.plugins.tracker.dht.DHTTrackerPlugin;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseTTTorrent;
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
/*     */ public class LightWeightSeedManager
/*     */ {
/*  57 */   private static final LightWeightSeedManager singleton = new LightWeightSeedManager();
/*     */   
/*     */ 
/*     */   public static LightWeightSeedManager getSingleton()
/*     */   {
/*  62 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*  66 */   private final Map lws_map = new HashMap();
/*     */   
/*     */   private boolean started;
/*  69 */   final Set<LWSDownload> dht_add_queue = new HashSet();
/*     */   
/*     */   private boolean borked;
/*     */   
/*     */   private DHTTrackerPlugin public_dht_tracker_plugin;
/*     */   
/*     */   private IPCInterface anon_dht_tracker_plugin;
/*     */   
/*     */   private DDBaseTTTorrent tttorrent;
/*     */   private TimerEventPeriodic timer;
/*  79 */   final AESemaphore init_sem = new AESemaphore("LWSM");
/*     */   
/*     */ 
/*     */ 
/*     */   protected LightWeightSeedManager()
/*     */   {
/*  85 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  87 */         LightWeightSeedManager.this.startUp();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void startUp()
/*     */   {
/*  95 */     synchronized (this)
/*     */     {
/*  97 */       if (this.started)
/*     */       {
/*  99 */         return;
/*     */       }
/*     */       
/* 102 */       this.started = true;
/*     */     }
/*     */     
/* 105 */     boolean release_now = true;
/*     */     try
/*     */     {
/* 108 */       final PluginManager plugin_manager = AzureusCoreFactory.getSingleton().getPluginManager();
/*     */       
/* 110 */       PluginInterface pi = plugin_manager.getPluginInterfaceByClass(DHTTrackerPlugin.class);
/*     */       
/* 112 */       if (pi != null)
/*     */       {
/* 114 */         final DHTTrackerPlugin plugin = (DHTTrackerPlugin)pi.getPlugin();
/*     */         
/* 116 */         new AEThread2("LWS:waitForPlug", true)
/*     */         {
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/* 122 */               plugin.waitUntilInitialised();
/*     */               
/* 124 */               if (plugin.isRunning())
/*     */               {
/* 126 */                 LightWeightSeedManager.this.tttorrent = DDBaseImpl.getSingleton(AzureusCoreFactory.getSingleton()).getTTTorrent();
/*     */               }
/*     */               
/*     */ 
/*     */               try
/*     */               {
/* 132 */                 PluginInterface anon_pi = plugin_manager.getPluginInterfaceByID("azneti2phelper");
/*     */                 
/* 134 */                 if (anon_pi != null)
/*     */                 {
/* 136 */                   LightWeightSeedManager.this.anon_dht_tracker_plugin = anon_pi.getIPC();
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 140 */                 Debug.out(e);
/*     */               }
/*     */               Set<LWSDownload> to_add;
/* 143 */               synchronized (this)
/*     */               {
/* 145 */                 LightWeightSeedManager.this.public_dht_tracker_plugin = plugin;
/*     */                 
/* 147 */                 to_add = new HashSet(LightWeightSeedManager.this.dht_add_queue);
/*     */                 
/* 149 */                 LightWeightSeedManager.this.dht_add_queue.clear();
/*     */               }
/*     */               
/* 152 */               Iterator<LWSDownload> it = to_add.iterator();
/*     */               
/* 154 */               while (it.hasNext())
/*     */               {
/* 156 */                 LightWeightSeedManager.this.addDownload((LWSDownload)it.next());
/*     */               }
/*     */             }
/*     */             finally {
/* 160 */               LightWeightSeedManager.this.init_sem.releaseForever();
/*     */             }
/*     */             
/*     */           }
/* 164 */         }.start();
/* 165 */         release_now = false;
/*     */       }
/*     */       else
/*     */       {
/* 169 */         synchronized (this.dht_add_queue)
/*     */         {
/* 171 */           this.borked = true;
/*     */           
/* 173 */           this.dht_add_queue.clear();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 178 */       if (release_now)
/*     */       {
/* 180 */         this.init_sem.releaseForever();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LightWeightSeed add(String name, HashWrapper hash, URL url, File data_location, String network, LightWeightSeedAdapter adapter)
/*     */     throws Exception
/*     */   {
/* 196 */     if (!TorrentUtils.isDecentralised(url))
/*     */     {
/* 198 */       throw new Exception("Only decentralised torrents supported");
/*     */     }
/*     */     
/*     */     LightWeightSeed lws;
/*     */     
/* 203 */     synchronized (this)
/*     */     {
/* 205 */       if (this.lws_map.containsKey(hash))
/*     */       {
/* 207 */         throw new Exception("Seed for hash '" + ByteFormatter.encodeString(hash.getBytes()) + "' already added");
/*     */       }
/*     */       
/* 210 */       lws = new LightWeightSeed(this, name, hash, url, data_location, network, adapter);
/*     */       
/* 212 */       this.lws_map.put(hash, lws);
/*     */       
/* 214 */       if (this.timer == null)
/*     */       {
/* 216 */         this.timer = SimpleTimer.addPeriodicEvent("LWSManager:timer", 60000L, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */ 
/*     */ 
/* 225 */             LightWeightSeedManager.this.processTimer();
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 230 */       log("Added LWS: " + name + ", " + UrlUtils.getMagnetURI(hash.getBytes()));
/*     */     }
/*     */     
/* 233 */     lws.start();
/*     */     
/* 235 */     return lws;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public LightWeightSeed get(HashWrapper hw)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 304	com/aelitis/azureus/core/lws/LightWeightSeedManager:lws_map	Ljava/util/Map;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 362 2 0
/*     */     //   14: checkcast 147	com/aelitis/azureus/core/lws/LightWeightSeed
/*     */     //   17: aload_2
/*     */     //   18: monitorexit
/*     */     //   19: areturn
/*     */     //   20: astore_3
/*     */     //   21: aload_2
/*     */     //   22: monitorexit
/*     */     //   23: aload_3
/*     */     //   24: athrow
/*     */     // Line number table:
/*     */     //   Java source line #242	-> byte code offset #0
/*     */     //   Java source line #244	-> byte code offset #4
/*     */     //   Java source line #245	-> byte code offset #20
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	25	0	this	LightWeightSeedManager
/*     */     //   0	25	1	hw	HashWrapper
/*     */     //   2	20	2	Ljava/lang/Object;	Object
/*     */     //   20	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	19	20	finally
/*     */     //   20	23	20	finally
/*     */   }
/*     */   
/*     */   protected void processTimer()
/*     */   {
/*     */     List to_process;
/* 253 */     synchronized (this)
/*     */     {
/* 255 */       to_process = new ArrayList(this.lws_map.values());
/*     */     }
/*     */     
/* 258 */     for (int i = 0; i < to_process.size(); i++) {
/*     */       try
/*     */       {
/* 261 */         ((LightWeightSeed)to_process.get(i)).checkDeactivation();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 265 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void remove(LightWeightSeed lws)
/*     */   {
/* 274 */     lws.stop();
/*     */     
/* 276 */     synchronized (this)
/*     */     {
/* 278 */       this.lws_map.remove(lws.getHash());
/*     */       
/* 280 */       if (this.lws_map.size() == 0)
/*     */       {
/* 282 */         if (this.timer != null)
/*     */         {
/* 284 */           this.timer.cancel();
/*     */           
/* 286 */           this.timer = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 291 */     log("Added LWS: " + lws.getName() + ", " + UrlUtils.getMagnetURI(lws.getHash().getBytes()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addToDHTTracker(LWSDownload download)
/*     */   {
/* 298 */     synchronized (this.dht_add_queue)
/*     */     {
/* 300 */       if (this.borked)
/*     */       {
/* 302 */         return;
/*     */       }
/*     */       
/* 305 */       if (this.public_dht_tracker_plugin == null)
/*     */       {
/* 307 */         this.dht_add_queue.add(download);
/*     */         
/* 309 */         return;
/*     */       }
/*     */     }
/*     */     
/* 313 */     this.init_sem.reserve();
/*     */     
/* 315 */     addDownload(download);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeFromDHTTracker(LWSDownload download)
/*     */   {
/* 322 */     synchronized (this.dht_add_queue)
/*     */     {
/* 324 */       if (this.borked)
/*     */       {
/* 326 */         return;
/*     */       }
/*     */       
/* 329 */       if (this.public_dht_tracker_plugin == null)
/*     */       {
/* 331 */         this.dht_add_queue.remove(download);
/*     */         
/* 333 */         return;
/*     */       }
/*     */     }
/*     */     
/* 337 */     this.init_sem.reserve();
/*     */     
/* 339 */     removeDownload(download);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addDownload(LWSDownload download)
/*     */   {
/* 346 */     if (download.getLWS().getNetwork() == "Public")
/*     */     {
/* 348 */       this.public_dht_tracker_plugin.addDownload(download);
/*     */ 
/*     */ 
/*     */     }
/* 352 */     else if (this.anon_dht_tracker_plugin != null) {
/*     */       try
/*     */       {
/* 355 */         this.anon_dht_tracker_plugin.invoke("addDownloadToTracker", new Object[] { download, new HashMap() });
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/*     */ 
/* 363 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 367 */     if (this.tttorrent != null)
/*     */     {
/* 369 */       this.tttorrent.addDownload(download);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeDownload(LWSDownload download)
/*     */   {
/* 377 */     if (download.getLWS().getNetwork() == "Public")
/*     */     {
/* 379 */       this.public_dht_tracker_plugin.removeDownload(download);
/*     */ 
/*     */ 
/*     */     }
/* 383 */     else if (this.anon_dht_tracker_plugin != null) {
/*     */       try
/*     */       {
/* 386 */         this.anon_dht_tracker_plugin.invoke("removeDownloadFromTracker", new Object[] { download, new HashMap() });
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/*     */ 
/* 394 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 399 */     if (this.tttorrent != null)
/*     */     {
/* 401 */       this.tttorrent.removeDownload(download);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 409 */     Logger.log(new LogEvent(LogIDs.CORE, str));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str, Throwable e)
/*     */   {
/* 417 */     Logger.log(new LogEvent(LogIDs.CORE, str, e));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LightWeightSeedManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */