/*     */ package com.aelitis.azureus.plugins.startstoprules.always;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginListener;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginManagerDefaults;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.utils.Monitor;
/*     */ import org.gudy.azureus2.plugins.utils.Semaphore;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
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
/*     */ public class RunEverythingPlugin
/*     */   implements Plugin, DownloadManagerListener, DownloadListener, DownloadTrackerListener
/*     */ {
/*     */   private PluginInterface plugin_interface;
/*     */   private LoggerChannel logger;
/*     */   private Map downloads;
/*     */   private Monitor downloads_mon;
/*     */   private Semaphore work_sem;
/*     */   private volatile boolean closing;
/*     */   
/*     */   public static void load(PluginInterface _plugin_interface)
/*     */   {
/*  64 */     PluginManagerDefaults defaults = PluginManager.getDefaults();
/*     */     
/*  66 */     defaults.setDefaultPluginEnabled("Start/Stop Rules", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface _pi)
/*     */   {
/*  73 */     this.plugin_interface = _pi;
/*     */     
/*  75 */     this.logger = this.plugin_interface.getLogger().getChannel("RunEverythingSeedingRules");
/*     */     
/*  77 */     this.plugin_interface.addListener(new PluginListener()
/*     */     {
/*     */       public void initializationComplete() {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void closedownInitiated()
/*     */       {
/*  88 */         RunEverythingPlugin.this.closing = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void closedownComplete() {}
/*  97 */     });
/*  98 */     this.downloads = new HashMap();
/*     */     
/* 100 */     this.downloads_mon = this.plugin_interface.getUtilities().getMonitor();
/*     */     
/* 102 */     this.work_sem = this.plugin_interface.getUtilities().getSemaphore();
/*     */     
/* 104 */     this.plugin_interface.getDownloadManager().addListener(this);
/*     */     
/* 106 */     this.plugin_interface.getUtilities().createTimer("DownloadRules", true).addPeriodicEvent(10000L, new UTTimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void perform(UTTimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/* 114 */         RunEverythingPlugin.this.checkRules();
/*     */       }
/*     */       
/* 117 */     });
/* 118 */     this.plugin_interface.getUtilities().createThread("DownloadRules", new Runnable()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 125 */         RunEverythingPlugin.this.processLoop();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void downloadAdded(Download download)
/*     */   {
/* 134 */     log("added: " + download.getName() + ", state = " + Download.ST_NAMES[download.getState()]);
/*     */     
/* 136 */     downloadData dd = new downloadData(download);
/*     */     try
/*     */     {
/* 139 */       this.downloads_mon.enter();
/*     */       
/* 141 */       this.downloads.put(download, dd);
/*     */     }
/*     */     finally
/*     */     {
/* 145 */       this.downloads_mon.exit();
/*     */     }
/*     */     
/* 148 */     download.addListener(this);
/*     */     
/* 150 */     checkRules();
/*     */   }
/*     */   
/*     */ 
/*     */   public void downloadRemoved(Download download)
/*     */   {
/*     */     try
/*     */     {
/* 158 */       this.downloads_mon.enter();
/*     */       
/* 160 */       this.downloads.remove(download);
/*     */     }
/*     */     finally
/*     */     {
/* 164 */       this.downloads_mon.exit();
/*     */     }
/*     */     
/* 167 */     download.removeListener(this);
/*     */     
/* 169 */     checkRules();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void scrapeResult(DownloadScrapeResult result)
/*     */   {
/* 176 */     checkRules();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void announceResult(DownloadAnnounceResult result)
/*     */   {
/* 183 */     checkRules();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stateChanged(Download download, int old_state, int new_state)
/*     */   {
/* 192 */     log("Rules: state change for " + download.getName() + ": " + Download.ST_NAMES[old_state] + "->" + Download.ST_NAMES[new_state]);
/*     */     
/* 194 */     checkRules();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void positionChanged(Download download, int oldPosition, int newPosition)
/*     */   {
/* 203 */     checkRules();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void checkRules()
/*     */   {
/* 209 */     this.work_sem.release();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLoop()
/*     */   {
/* 215 */     while (!this.closing)
/*     */     {
/* 217 */       this.work_sem.reserve();
/*     */       
/* 219 */       while (this.work_sem.reserveIfAvailable()) {}
/*     */       
/*     */       try
/*     */       {
/* 223 */         processSupport();
/*     */         
/* 225 */         Thread.sleep(250L);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 229 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processSupport()
/*     */   {
/* 237 */     if (this.closing)
/*     */     {
/* 239 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 243 */       this.downloads_mon.enter();
/*     */       
/* 245 */       List dls = new ArrayList(this.downloads.values());
/*     */       
/*     */ 
/*     */ 
/* 249 */       Iterator it = dls.iterator();
/*     */       
/* 251 */       while (it.hasNext())
/*     */       {
/* 253 */         downloadData dd = (downloadData)it.next();
/*     */         
/* 255 */         if (dd.ignore())
/*     */         {
/* 257 */           it.remove();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 263 */       it = dls.iterator();
/*     */       
/* 265 */       while (it.hasNext())
/*     */       {
/* 267 */         downloadData dd = (downloadData)it.next();
/*     */         
/* 269 */         if (dd.getState() == 1)
/*     */         {
/* 271 */           it.remove();
/*     */           try
/*     */           {
/* 274 */             log("initialising " + dd.getName());
/*     */             
/* 276 */             dd.getDownload().initialize();
/*     */           }
/*     */           catch (DownloadException e)
/*     */           {
/* 280 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 287 */       it = dls.iterator();
/*     */       
/* 289 */       while (it.hasNext())
/*     */       {
/* 291 */         downloadData dd = (downloadData)it.next();
/*     */         
/* 293 */         if (dd.getState() == 3)
/*     */         {
/* 295 */           it.remove();
/*     */           try
/*     */           {
/* 298 */             log("starting " + dd.getName());
/*     */             
/* 300 */             dd.getDownload().start();
/*     */           }
/*     */           catch (DownloadException e)
/*     */           {
/* 304 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 311 */       it = dls.iterator();
/*     */       
/* 313 */       while (it.hasNext())
/*     */       {
/* 315 */         downloadData dd = (downloadData)it.next();
/*     */         
/* 317 */         if ((dd.getState() == 9) && (!dd.isComplete())) {
/*     */           try
/*     */           {
/* 320 */             it.remove();
/*     */             
/* 322 */             log("restarting download " + dd.getName());
/*     */             
/* 324 */             dd.getDownload().restart();
/*     */           }
/*     */           catch (DownloadException e)
/*     */           {
/* 328 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 335 */       it = dls.iterator();
/*     */       
/* 337 */       while (it.hasNext())
/*     */       {
/* 339 */         downloadData dd = (downloadData)it.next();
/*     */         
/* 341 */         if ((dd.isComplete()) && (dd.getState() == 9)) {
/*     */           try
/*     */           {
/* 344 */             it.remove();
/*     */             
/* 346 */             log("restarting seed " + dd.getName());
/*     */             
/* 348 */             dd.getDownload().restart();
/*     */           }
/*     */           catch (DownloadException e)
/*     */           {
/* 352 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 358 */       this.downloads_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 366 */     this.logger.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class downloadData
/*     */   {
/*     */     private Download download;
/*     */     
/*     */ 
/*     */     protected downloadData(Download _download)
/*     */     {
/* 378 */       this.download = _download;
/*     */     }
/*     */     
/*     */ 
/*     */     protected Download getDownload()
/*     */     {
/* 384 */       return this.download;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int getState()
/*     */     {
/* 390 */       return this.download.getState();
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getName()
/*     */     {
/* 396 */       return this.download.getName();
/*     */     }
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
/*     */     protected boolean isComplete()
/*     */     {
/* 436 */       return this.download.isComplete();
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean ignore()
/*     */     {
/* 442 */       int state = this.download.getState();
/*     */       
/* 444 */       return (state == 8) || (state == 7) || (state == 6);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/always/RunEverythingPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */