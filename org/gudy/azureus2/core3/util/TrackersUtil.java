/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class TrackersUtil
/*     */ {
/*     */   private List<String> trackers;
/*     */   private Map<String, List<List<String>>> multiTrackers;
/*     */   private Map<String, Map> webseeds;
/*     */   private static TrackersUtil instance;
/*  46 */   private static final AEMonitor class_mon = new AEMonitor("TrackersUtil:class");
/*     */   
/*     */ 
/*     */   private TrackersUtil()
/*     */   {
/*  51 */     this.trackers = new ArrayList();
/*  52 */     this.multiTrackers = new HashMap();
/*  53 */     this.webseeds = new HashMap();
/*  54 */     loadList();
/*     */   }
/*     */   
/*     */   public static TrackersUtil getInstance()
/*     */   {
/*     */     try {
/*  60 */       class_mon.enter();
/*     */       
/*  62 */       if (instance == null)
/*  63 */         instance = new TrackersUtil();
/*  64 */       return instance;
/*     */     }
/*     */     finally
/*     */     {
/*  68 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<String> getTrackersList() {
/*  73 */     if (this.trackers != null) {
/*  74 */       return new ArrayList(this.trackers);
/*     */     }
/*  76 */     return null;
/*     */   }
/*     */   
/*     */   public void addTracker(String trackerAnnounceUrl) {
/*  80 */     if (this.trackers.contains(trackerAnnounceUrl))
/*  81 */       return;
/*  82 */     this.trackers.add(0, trackerAnnounceUrl);
/*  83 */     saveList();
/*     */   }
/*     */   
/*     */   public void addMultiTracker(String configName, List<List<String>> groups) {
/*  87 */     this.multiTrackers.put(configName, groups);
/*  88 */     saveList();
/*     */   }
/*     */   
/*     */   public void removeMultiTracker(String configName) {
/*  92 */     this.multiTrackers.remove(configName);
/*  93 */     saveList();
/*     */   }
/*     */   
/*     */ 
/*  97 */   public Map<String, List<List<String>>> getMultiTrackers() { return new HashMap(this.multiTrackers); }
/*     */   
/*     */   public void addWebSeed(String configName, Map ws) {
/* 100 */     this.webseeds.put(configName, ws);
/* 101 */     saveList();
/*     */   }
/*     */   
/*     */   public void removeWebSeed(String configName) {
/* 105 */     this.webseeds.remove(configName);
/* 106 */     saveList();
/*     */   }
/*     */   
/*     */   public Map<String, Map> getWebSeeds() {
/* 110 */     return new HashMap(this.webseeds);
/*     */   }
/*     */   
/*     */   public void clearAllTrackers(boolean save) {
/* 114 */     this.trackers = new ArrayList();
/* 115 */     this.multiTrackers = new HashMap();
/* 116 */     this.webseeds = new HashMap();
/* 117 */     if (save) saveList();
/*     */   }
/*     */   
/*     */   private void loadList() {
/* 121 */     File fTrackers = FileUtil.getUserFile("trackers.config");
/* 122 */     if ((fTrackers.exists()) && (fTrackers.isFile())) {
/* 123 */       FileInputStream fin = null;
/* 124 */       BufferedInputStream bin = null;
/*     */       try {
/* 126 */         fin = new FileInputStream(fTrackers);
/* 127 */         bin = new BufferedInputStream(fin, 8192);
/* 128 */         Map map = BDecoder.decode(bin);
/* 129 */         List list = (List)map.get("trackers");
/* 130 */         if (list != null) {
/* 131 */           Iterator iter = list.iterator();
/* 132 */           while (iter.hasNext()) {
/* 133 */             String tracker = new String((byte[])iter.next());
/* 134 */             this.trackers.add(tracker);
/*     */           }
/*     */         }
/* 137 */         Map mapMT = (Map)map.get("multi-trackers");
/* 138 */         if (mapMT != null) {
/* 139 */           Iterator iter = mapMT.keySet().iterator();
/* 140 */           while (iter.hasNext()) {
/* 141 */             String configName = (String)iter.next();
/* 142 */             List groups = (List)mapMT.get(configName);
/* 143 */             List resGroups = new ArrayList(groups.size());
/* 144 */             Iterator iterGroups = groups.iterator();
/* 145 */             while (iterGroups.hasNext()) {
/* 146 */               List theseTrackers = (List)iterGroups.next();
/* 147 */               List resTrackers = new ArrayList(theseTrackers.size());
/* 148 */               Iterator iterTrackers = theseTrackers.iterator();
/* 149 */               while (iterTrackers.hasNext()) {
/* 150 */                 String tracker = new String((byte[])iterTrackers.next());
/* 151 */                 resTrackers.add(tracker);
/*     */               }
/* 153 */               resGroups.add(resTrackers);
/*     */             }
/* 155 */             this.multiTrackers.put(configName, resGroups);
/*     */           }
/*     */         }
/* 158 */         this.webseeds = ((Map)map.get("webseeds"));
/*     */         
/* 160 */         if (this.webseeds == null) {
/* 161 */           this.webseeds = new HashMap();
/*     */         } else {
/* 163 */           BDecoder.decodeStrings(this.webseeds);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 167 */         Debug.printStackTrace(e);
/*     */       }
/*     */       finally
/*     */       {
/* 171 */         if (bin != null) {
/*     */           try {
/* 173 */             bin.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/* 177 */         if (fin != null) {
/*     */           try {
/* 179 */             fin.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void saveList() {
/* 188 */     Map map = new HashMap();
/* 189 */     map.put("trackers", this.trackers);
/* 190 */     map.put("multi-trackers", this.multiTrackers);
/* 191 */     map.put("webseeds", this.webseeds);
/* 192 */     FileOutputStream fos = null;
/*     */     try
/*     */     {
/* 195 */       File fTrackers = FileUtil.getUserFile("trackers.config");
/* 196 */       fos = new FileOutputStream(fTrackers);
/* 197 */       fos.write(BEncoder.encode(map));
/* 198 */       fos.close(); return;
/*     */     } catch (Exception e) {
/* 200 */       Debug.printStackTrace(e);
/*     */     } finally {
/* 202 */       if (fos != null) {
/*     */         try {
/* 204 */           fos.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/TrackersUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */