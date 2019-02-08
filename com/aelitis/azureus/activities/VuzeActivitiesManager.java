/*     */ package com.aelitis.azureus.activities;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkPropertyChangeListener;
/*     */ import com.aelitis.azureus.core.messenger.config.PlatformVuzeActivitiesMessenger;
/*     */ import com.aelitis.azureus.core.messenger.config.PlatformVuzeActivitiesMessenger.GetEntriesReplyListener;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class VuzeActivitiesManager
/*     */ {
/*     */   public static final long MAX_LIFE_MS = 63072000000L;
/*     */   private static final long DEFAULT_PLATFORM_REFRESH = 86400000L;
/*     */   private static final String SAVE_FILENAME = "VuzeActivities.config";
/*  50 */   private static ArrayList<VuzeActivitiesListener> listeners = new ArrayList();
/*     */   
/*  52 */   private static ArrayList<VuzeActivitiesLoadedListener> listenersLoaded = new ArrayList();
/*  53 */   private static final Object listenersLoadedLock = new Object();
/*     */   
/*  55 */   private static CopyOnWriteList<VuzeActivitiesEntry> allEntries = new CopyOnWriteList();
/*     */   
/*  57 */   private static AEMonitor allEntries_mon = new AEMonitor("VuzeActivityMan");
/*     */   
/*  59 */   private static List<VuzeActivitiesEntry> removedEntries = new ArrayList();
/*     */   
/*     */ 
/*     */   private static PlatformVuzeActivitiesMessenger.GetEntriesReplyListener replyListener;
/*     */   
/*     */   private static AEDiagnosticsLogger diag_logger;
/*     */   
/*  66 */   private static Map<String, Long> lastNewsAt = new HashMap();
/*     */   
/*  68 */   private static boolean skipAutoSave = true;
/*     */   
/*  70 */   private static AEMonitor config_mon = new AEMonitor("ConfigMon");
/*     */   
/*  72 */   private static boolean saveEventsOnClose = false;
/*     */   
/*     */   static {
/*  75 */     if (System.getProperty("debug.vuzenews", "0").equals("1")) {
/*  76 */       diag_logger = AEDiagnostics.getLogger("v3.vuzenews");
/*  77 */       diag_logger.log("\n\nVuze News Logging Starts");
/*     */     } else {
/*  79 */       diag_logger = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void initialize(final AzureusCore core) {
/*  84 */     new AEThread2("lazy init", true) {
/*     */       public void run() {
/*  86 */         VuzeActivitiesManager._initialize(core);
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */   private static void _initialize(AzureusCore core) {
/*  92 */     if (diag_logger != null) {
/*  93 */       diag_logger.log("Initialize Called");
/*     */     }
/*     */     
/*  96 */     core.addLifecycleListener(new AzureusCoreLifecycleAdapter() {
/*     */       public void stopping(AzureusCore core) {
/*  98 */         if (VuzeActivitiesManager.saveEventsOnClose) {
/*  99 */           VuzeActivitiesManager.access$200();
/*     */         }
/*     */         
/*     */       }
/* 103 */     });
/* 104 */     loadEvents();
/*     */     
/* 106 */     ContentNetworkManager cnm = ContentNetworkManagerFactory.getSingleton();
/* 107 */     if (cnm != null) {
/* 108 */       ContentNetwork[] contentNetworks = cnm.getContentNetworks();
/* 109 */       cnm.addListener(new ContentNetworkListener()
/*     */       {
/*     */         public void networkRemoved(ContentNetwork network) {}
/*     */         
/*     */ 
/*     */         public void networkChanged(ContentNetwork network) {}
/*     */         
/*     */         public void networkAdded(ContentNetwork cn)
/*     */         {
/* 118 */           VuzeActivitiesManager.setupContentNetwork(cn);
/*     */         }
/*     */         
/*     */ 
/*     */         public void networkAddFailed(long network_id, Throwable error) {}
/*     */       });
/*     */       
/* 125 */       for (ContentNetwork cn : contentNetworks) {
/* 126 */         setupContentNetwork(cn);
/*     */       }
/*     */     }
/*     */     
/* 130 */     replyListener = new PlatformVuzeActivitiesMessenger.GetEntriesReplyListener()
/*     */     {
/*     */       public void gotVuzeNewsEntries(VuzeActivitiesEntry[] entries, long refreshInMS) {
/* 133 */         if (VuzeActivitiesManager.diag_logger != null) {
/* 134 */           VuzeActivitiesManager.diag_logger.log("Received Reply from platform with " + entries.length + " entries.  Refresh in " + refreshInMS);
/*     */         }
/*     */         
/*     */ 
/* 138 */         VuzeActivitiesManager.addEntries(entries);
/*     */         
/* 140 */         if (refreshInMS <= 0L) {
/* 141 */           refreshInMS = 86400000L;
/*     */         }
/*     */         
/* 144 */         SimpleTimer.addEvent("GetVuzeNews", SystemTime.getOffsetTime(refreshInMS), new TimerEventPerformer()
/*     */         {
/*     */           public void perform(TimerEvent event) {
/* 147 */             VuzeActivitiesManager.pullActivitiesNow(5000L, "timer", false);
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 152 */     };
/* 153 */     pullActivitiesNow(5000L, "initial", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setupContentNetwork(ContentNetwork cn)
/*     */   {
/* 162 */     cn.addPersistentPropertyChangeListener(new ContentNetworkPropertyChangeListener()
/*     */     {
/*     */       public void propertyChanged(String name) {
/* 165 */         if (!"active".equals(name)) {
/* 166 */           return;
/*     */         }
/* 168 */         Object oIsActive = this.val$cn.getPersistentProperty("active");
/* 169 */         boolean isActive = (oIsActive instanceof Boolean) ? ((Boolean)oIsActive).booleanValue() : false;
/*     */         
/* 171 */         if (isActive) {
/* 172 */           VuzeActivitiesManager.pullActivitiesNow(2000L, "CN:PropChange", false);
/*     */         }
/*     */       }
/*     */     });
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
/*     */ 
/*     */ 
/*     */   public static void pullActivitiesNow(long delay, String reason, boolean alwaysPull)
/*     */   {
/* 226 */     ContentNetwork cn = ConstantsVuze.getDefaultContentNetwork();
/* 227 */     if (cn == null) {
/* 228 */       return;
/*     */     }
/*     */     
/* 231 */     String id = "" + cn.getID();
/* 232 */     Long oLastPullTime = (Long)lastNewsAt.get(id);
/* 233 */     long lastPullTime = oLastPullTime != null ? oLastPullTime.longValue() : 0L;
/* 234 */     long now = SystemTime.getCurrentTime();
/* 235 */     long diff = now - lastPullTime;
/* 236 */     if ((!alwaysPull) && (diff < 5000L)) {
/* 237 */       return;
/*     */     }
/* 239 */     if (diff > 63072000000L) {
/* 240 */       diff = 63072000000L;
/*     */     }
/* 242 */     PlatformVuzeActivitiesMessenger.getEntries(diff, delay, reason, replyListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void clearLastPullTimes()
/*     */   {
/* 250 */     lastNewsAt = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void resetRemovedEntries()
/*     */   {
/* 261 */     removedEntries.clear();
/* 262 */     saveEvents();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void saveEvents()
/*     */   {
/* 271 */     saveEventsOnClose = true;
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
/*     */   private static void loadEvents()
/*     */   {
/* 284 */     skipAutoSave = true;
/*     */     try
/*     */     {
/* 287 */       Map<?, ?> map = FileUtil.readResilientConfigFile("VuzeActivities.config");
/*     */       Iterator i$;
/*     */       VuzeActivitiesLoadedListener l;
/* 290 */       if ((map != null) && (map.size() > 0) && (MapUtils.getMapLong(map, "version", 0L) < 2L))
/*     */       {
/* 292 */         clearLastPullTimes();
/* 293 */         skipAutoSave = false;
/* 294 */         saveEventsNow();
/*     */       }
/*     */       else
/*     */       {
/* 298 */         long cutoffTime = getCutoffTime();
/*     */         try
/*     */         {
/* 301 */           lastNewsAt = MapUtils.getMapMap(map, "LastChecks", new HashMap());
/*     */         } catch (Exception e) {
/* 303 */           Debug.out(e);
/*     */         }
/*     */         
/*     */ 
/* 307 */         if (lastNewsAt.size() == 0) {
/* 308 */           long lastVuzeNewsAt = MapUtils.getMapLong(map, "LastCheck", 0L);
/* 309 */           if (lastVuzeNewsAt > 0L) {
/* 310 */             if (lastVuzeNewsAt < cutoffTime) {
/* 311 */               lastVuzeNewsAt = cutoffTime;
/*     */             }
/* 313 */             lastNewsAt.put("1", new Long(lastVuzeNewsAt));
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 320 */         List newRemovedEntries = (List)MapUtils.getMapObject(map, "removed-entries", null, List.class);
/*     */         Iterator iter;
/* 322 */         if (newRemovedEntries != null) {
/* 323 */           for (iter = newRemovedEntries.iterator(); iter.hasNext();) {
/* 324 */             Object value = iter.next();
/* 325 */             if ((value instanceof Map))
/*     */             {
/*     */ 
/* 328 */               VuzeActivitiesEntry entry = createEntryFromMap((Map)value, true);
/*     */               
/* 330 */               if ((entry != null) && (entry.getTimestamp() > cutoffTime)) {
/* 331 */                 removedEntries.add(entry);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 336 */         Object value = map.get("entries");
/* 337 */         if (!(value instanceof List)) { Iterator i$;
/*     */           VuzeActivitiesLoadedListener l;
/*     */           return;
/*     */         }
/* 341 */         List entries = (List)value;
/* 342 */         List<VuzeActivitiesEntry> entriesToAdd = new ArrayList(entries.size());
/* 343 */         for (Iterator iter = entries.iterator(); iter.hasNext();) {
/* 344 */           value = iter.next();
/* 345 */           if ((value instanceof Map))
/*     */           {
/*     */ 
/*     */ 
/* 349 */             VuzeActivitiesEntry entry = createEntryFromMap((Map)value, true);
/*     */             
/* 351 */             if ((entry != null) && 
/* 352 */               (entry.getTimestamp() > cutoffTime)) {
/* 353 */               entriesToAdd.add(entry);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 358 */         int num = entriesToAdd.size();
/* 359 */         if (num > 0)
/* 360 */           addEntries((VuzeActivitiesEntry[])entriesToAdd.toArray(new VuzeActivitiesEntry[num]));
/*     */       } } finally { Iterator i$;
/*     */       VuzeActivitiesLoadedListener l;
/* 363 */       skipAutoSave = false;
/*     */       
/* 365 */       synchronized (listenersLoadedLock) {
/* 366 */         if (listenersLoaded != null) {
/* 367 */           for (VuzeActivitiesLoadedListener l : listenersLoaded) {
/*     */             try {
/* 369 */               l.vuzeActivitiesLoaded();
/*     */             } catch (Exception e) {
/* 371 */               Debug.out(e);
/*     */             }
/*     */           }
/* 374 */           listenersLoaded = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void saveEventsNow()
/*     */   {
/* 382 */     if (skipAutoSave) {
/* 383 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 387 */       config_mon.enter();
/*     */       
/* 389 */       Map<String, Object> mapSave = new HashMap();
/* 390 */       mapSave.put("LastChecks", lastNewsAt);
/* 391 */       mapSave.put("version", new Long(2L));
/*     */       
/* 393 */       List<Object> entriesList = new ArrayList();
/*     */       
/* 395 */       List<VuzeActivitiesEntry> allEntries = getAllEntries();
/* 396 */       for (VuzeActivitiesEntry entry : allEntries) {
/* 397 */         if (entry != null)
/*     */         {
/*     */ 
/*     */ 
/* 401 */           boolean isHeader = "Header".equals(entry.getTypeID());
/* 402 */           if (!isHeader)
/* 403 */             entriesList.add(entry.toMap());
/*     */         }
/*     */       }
/* 406 */       mapSave.put("entries", entriesList);
/*     */       
/* 408 */       List<Object> removedEntriesList = new ArrayList();
/* 409 */       for (Iterator<VuzeActivitiesEntry> iter = removedEntries.iterator(); iter.hasNext();) {
/* 410 */         VuzeActivitiesEntry entry = (VuzeActivitiesEntry)iter.next();
/* 411 */         removedEntriesList.add(entry.toDeletedMap());
/*     */       }
/* 413 */       mapSave.put("removed-entries", removedEntriesList);
/*     */       
/* 415 */       FileUtil.writeResilientConfigFile("VuzeActivities.config", mapSave);
/*     */     }
/*     */     catch (Throwable t) {
/* 418 */       Debug.out(t);
/*     */     } finally {
/* 420 */       config_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public static long getCutoffTime() {
/* 425 */     return SystemTime.getOffsetTime(-63072000000L);
/*     */   }
/*     */   
/*     */   public static void addListener(VuzeActivitiesListener l) {
/* 429 */     listeners.add(l);
/*     */   }
/*     */   
/*     */   public static void removeListener(VuzeActivitiesListener l) {
/* 433 */     listeners.remove(l);
/*     */   }
/*     */   
/*     */   public static void addListener(VuzeActivitiesLoadedListener l) {
/* 437 */     synchronized (listenersLoadedLock) {
/* 438 */       if (listenersLoaded != null) {
/* 439 */         listenersLoaded.add(l);
/*     */       } else {
/*     */         try {
/* 442 */           l.vuzeActivitiesLoaded();
/*     */         } catch (Exception e) {
/* 444 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void removeListener(VuzeActivitiesLoadedListener l) {
/* 451 */     synchronized (listenersLoadedLock) {
/* 452 */       if (listenersLoaded != null) {
/* 453 */         listenersLoaded.remove(l);
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
/*     */   public static VuzeActivitiesEntry[] addEntries(VuzeActivitiesEntry[] entries)
/*     */   {
/* 466 */     long cutoffTime = getCutoffTime();
/*     */     
/* 468 */     ArrayList<VuzeActivitiesEntry> newEntries = new ArrayList(entries.length);
/* 469 */     ArrayList<VuzeActivitiesEntry> existingEntries = new ArrayList(0);
/*     */     try
/*     */     {
/* 472 */       allEntries_mon.enter();
/*     */       
/* 474 */       for (int i = 0; i < entries.length; i++) {
/* 475 */         VuzeActivitiesEntry entry = entries[i];
/* 476 */         boolean isHeader = "Header".equals(entry.getTypeID());
/* 477 */         if (((entry.getTimestamp() >= cutoffTime) || (isHeader)) && (!removedEntries.contains(entry)))
/*     */         {
/*     */ 
/* 480 */           VuzeActivitiesEntry existing_entry = (VuzeActivitiesEntry)allEntries.get(entry);
/* 481 */           if (existing_entry != null) {
/* 482 */             existingEntries.add(existing_entry);
/* 483 */             if (existing_entry.getTimestamp() < entry.getTimestamp()) {
/* 484 */               existing_entry.updateFrom(entry);
/*     */             }
/*     */           } else {
/* 487 */             newEntries.add(entry);
/* 488 */             allEntries.add(entry);
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally {
/* 493 */       allEntries_mon.exit();
/*     */     }
/*     */     
/* 496 */     VuzeActivitiesEntry[] newEntriesArray = (VuzeActivitiesEntry[])newEntries.toArray(new VuzeActivitiesEntry[newEntries.size()]);
/*     */     
/* 498 */     if (newEntriesArray.length > 0) {
/* 499 */       saveEventsNow();
/*     */       
/* 501 */       Object[] listenersArray = listeners.toArray();
/* 502 */       for (int i = 0; i < listenersArray.length; i++) {
/* 503 */         VuzeActivitiesListener l = (VuzeActivitiesListener)listenersArray[i];
/* 504 */         l.vuzeNewsEntriesAdded(newEntriesArray);
/*     */       }
/*     */     }
/*     */     Iterator<VuzeActivitiesEntry> iter;
/* 508 */     if (existingEntries.size() > 0) {
/* 509 */       if (newEntriesArray.length == 0) {
/* 510 */         saveEvents();
/*     */       }
/*     */       
/* 513 */       for (iter = existingEntries.iterator(); iter.hasNext();) {
/* 514 */         VuzeActivitiesEntry entry = (VuzeActivitiesEntry)iter.next();
/* 515 */         triggerEntryChanged(entry);
/*     */       }
/*     */     }
/*     */     
/* 519 */     return newEntriesArray;
/*     */   }
/*     */   
/*     */   public static void removeEntries(VuzeActivitiesEntry[] entries) {
/* 523 */     removeEntries(entries, false);
/*     */   }
/*     */   
/*     */   public static void removeEntries(VuzeActivitiesEntry[] entries, boolean allowReAdd) {
/* 527 */     long cutoffTime = getCutoffTime();
/*     */     try
/*     */     {
/* 530 */       allEntries_mon.enter();
/*     */       
/* 532 */       for (int i = 0; i < entries.length; i++) {
/* 533 */         VuzeActivitiesEntry entry = entries[i];
/* 534 */         if (entry != null)
/*     */         {
/*     */ 
/* 537 */           allEntries.remove(entry);
/* 538 */           boolean isHeader = "Header".equals(entry.getTypeID());
/* 539 */           if ((!allowReAdd) && (entry.getTimestamp() > cutoffTime) && (!isHeader) && 
/* 540 */             (!entry.allowReAdd()))
/*     */           {
/* 542 */             removedEntries.add(entry);
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally {
/* 547 */       allEntries_mon.exit();
/*     */     }
/*     */     
/* 550 */     Object[] listenersArray = listeners.toArray();
/* 551 */     for (int i = 0; i < listenersArray.length; i++) {
/* 552 */       VuzeActivitiesListener l = (VuzeActivitiesListener)listenersArray[i];
/* 553 */       l.vuzeNewsEntriesRemoved(entries);
/*     */     }
/* 555 */     saveEventsNow();
/*     */   }
/*     */   
/*     */   public static VuzeActivitiesEntry getEntryByID(String id) {
/*     */     try {
/* 560 */       allEntries_mon.enter();
/*     */       
/* 562 */       for (iter = allEntries.iterator(); iter.hasNext();) {
/* 563 */         VuzeActivitiesEntry entry = (VuzeActivitiesEntry)iter.next();
/* 564 */         if (entry != null)
/*     */         {
/*     */ 
/* 567 */           String entryID = entry.getID();
/* 568 */           if ((entryID != null) && (entryID.equals(id)))
/* 569 */             return entry;
/*     */         }
/*     */       }
/*     */     } finally { Iterator<VuzeActivitiesEntry> iter;
/* 573 */       allEntries_mon.exit();
/*     */     }
/*     */     
/* 576 */     return null;
/*     */   }
/*     */   
/*     */   public static boolean isEntryIdRemoved(String id) {
/* 580 */     for (VuzeActivitiesEntry entry : removedEntries) {
/* 581 */       if (entry.getID().equals(id)) {
/* 582 */         return true;
/*     */       }
/*     */     }
/* 585 */     return false;
/*     */   }
/*     */   
/*     */   public static List<VuzeActivitiesEntry> getAllEntries() {
/* 589 */     return allEntries.getList();
/*     */   }
/*     */   
/*     */ 
/*     */   public static Object[] getMostRecentUnseen()
/*     */   {
/* 595 */     VuzeActivitiesEntry newest = null;
/* 596 */     long newest_time = 0L;
/*     */     
/* 598 */     int num_unseen = 0;
/*     */     
/* 600 */     for (VuzeActivitiesEntry entry : allEntries)
/*     */     {
/* 602 */       if (!entry.getViewed())
/*     */       {
/* 604 */         num_unseen++;
/*     */         
/* 606 */         long t = entry.getTimestamp();
/*     */         
/* 608 */         if (t > newest_time)
/*     */         {
/* 610 */           newest = entry;
/* 611 */           newest_time = t;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 616 */     return new Object[] { newest, Integer.valueOf(num_unseen) };
/*     */   }
/*     */   
/*     */   public static int getNumEntries() {
/* 620 */     return allEntries.size();
/*     */   }
/*     */   
/*     */   public static void log(String s) {
/* 624 */     if (diag_logger != null) {
/* 625 */       diag_logger.log(s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void triggerEntryChanged(VuzeActivitiesEntry entry)
/*     */   {
/* 635 */     Object[] listenersArray = listeners.toArray();
/* 636 */     for (int i = 0; i < listenersArray.length; i++) {
/* 637 */       VuzeActivitiesListener l = (VuzeActivitiesListener)listenersArray[i];
/* 638 */       l.vuzeNewsEntryChanged(entry);
/*     */     }
/* 640 */     saveEvents();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static VuzeActivitiesEntry createEntryFromMap(Map<?, ?> map, boolean internalMap)
/*     */   {
/* 652 */     VuzeActivitiesEntry entry = new VuzeActivitiesEntry();
/* 653 */     if (internalMap) {
/* 654 */       entry.loadFromInternalMap(map);
/*     */     } else {
/* 656 */       entry.loadFromExternalMap(map);
/*     */     }
/* 658 */     return entry;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/activities/VuzeActivitiesManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */