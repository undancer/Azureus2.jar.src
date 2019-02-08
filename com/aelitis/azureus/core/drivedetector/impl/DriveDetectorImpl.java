/*     */ package com.aelitis.azureus.core.drivedetector.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectedInfo;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectedListener;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetector;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectorFactory;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor2;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
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
/*     */ public class DriveDetectorImpl
/*     */   implements DriveDetector, AEDiagnosticsEvidenceGenerator
/*     */ {
/*  36 */   final AEMonitor2 mon_driveDetector = new AEMonitor2("driveDetector");
/*     */   
/*  38 */   final CopyOnWriteList<DriveDetectedListener> listListeners = new CopyOnWriteList(1);
/*     */   
/*  40 */   final Map<File, Map> mapDrives = new HashMap(1);
/*     */   
/*  42 */   private final AsyncDispatcher dispatcher = new AsyncDispatcher("DriveDetector");
/*     */   
/*     */   public DriveDetectorImpl() {
/*  45 */     AEDiagnostics.addEvidenceGenerator(this);
/*     */   }
/*     */   
/*     */   public DriveDetectedInfo[] getDetectedDriveInfo() {
/*  49 */     this.mon_driveDetector.enter();
/*     */     try {
/*  51 */       int i = 0;
/*  52 */       DriveDetectedInfo[] ddi = new DriveDetectedInfo[this.mapDrives.size()];
/*  53 */       for (File key : this.mapDrives.keySet()) {
/*  54 */         ddi[(i++)] = new DriveDetectedInfoImpl(key, (Map)this.mapDrives.get(key));
/*     */       }
/*  56 */       return ddi;
/*     */     } finally {
/*  58 */       this.mon_driveDetector.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(DriveDetectedListener l) {
/*  63 */     this.mon_driveDetector.enter();
/*     */     try {
/*  65 */       if (!this.listListeners.contains(l)) {
/*  66 */         this.listListeners.add(l);
/*     */       } else {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*  73 */       for (File key : this.mapDrives.keySet()) {
/*     */         try {
/*  75 */           l.driveDetected(new DriveDetectedInfoImpl(key, (Map)this.mapDrives.get(key)));
/*     */         } catch (Throwable e) {
/*  77 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     } finally {
/*  81 */       this.mon_driveDetector.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(DriveDetectedListener l) {
/*  86 */     this.listListeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void driveDetected(final File _location, final Map info)
/*     */   {
/*  94 */     this.dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 100 */         File location = DriveDetectorImpl.this.normaliseFile(_location);
/* 101 */         DriveDetectorImpl.this.mon_driveDetector.enter();
/*     */         try {
/* 103 */           if (!DriveDetectorImpl.this.mapDrives.containsKey(location)) {
/* 104 */             info.put("File", location);
/* 105 */             DriveDetectorImpl.this.mapDrives.put(location, info);
/*     */           }
/*     */           else {
/*     */             return;
/*     */           }
/*     */         }
/*     */         finally {
/* 112 */           DriveDetectorImpl.this.mon_driveDetector.exit();
/*     */         }
/*     */         
/* 115 */         for (DriveDetectedListener l : DriveDetectorImpl.this.listListeners) {
/*     */           try {
/* 117 */             l.driveDetected(new DriveDetectedInfoImpl(location, info));
/*     */           } catch (Throwable e) {
/* 119 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void driveRemoved(final File _location) {
/* 127 */     this.dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 133 */         File location = DriveDetectorImpl.this.normaliseFile(_location);
/*     */         
/* 135 */         DriveDetectorImpl.this.mon_driveDetector.enter();
/*     */         Map map;
/* 137 */         try { map = (Map)DriveDetectorImpl.this.mapDrives.remove(location);
/* 138 */           if (map == null) {
/*     */             return;
/*     */           }
/*     */         }
/*     */         finally {
/* 143 */           DriveDetectorImpl.this.mon_driveDetector.exit();
/*     */         }
/*     */         
/* 146 */         for (DriveDetectedListener l : DriveDetectorImpl.this.listListeners) {
/*     */           try {
/* 148 */             l.driveRemoved(new DriveDetectedInfoImpl(location, map));
/*     */           } catch (Throwable e) {
/* 150 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private File normaliseFile(File f)
/*     */   {
/*     */     try
/*     */     {
/* 162 */       return f.getCanonicalFile();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 166 */       Debug.out(e);
/*     */     }
/* 168 */     return f;
/*     */   }
/*     */   
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 174 */     synchronized (this.mapDrives) {
/* 175 */       writer.println("DriveDetector: " + this.mapDrives.size() + " drives");
/* 176 */       for (File file : this.mapDrives.keySet()) {
/*     */         try {
/* 178 */           writer.indent();
/* 179 */           writer.println(file.getPath());
/*     */           Map driveInfo;
/*     */           Iterator iter;
/* 182 */           try { writer.indent();
/*     */             
/* 184 */             driveInfo = (Map)this.mapDrives.get(file);
/* 185 */             for (iter = driveInfo.keySet().iterator(); iter.hasNext();) {
/* 186 */               Object key = iter.next();
/* 187 */               Object val = driveInfo.get(key);
/* 188 */               writer.println(key + ": " + val);
/*     */             }
/*     */           }
/*     */           finally {}
/*     */         }
/*     */         catch (Throwable e) {
/* 194 */           Debug.out(e);
/*     */         } finally {
/* 196 */           writer.exdent();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 203 */     DriveDetectedInfo[] infos = DriveDetectorFactory.getDeviceDetector().getDetectedDriveInfo();
/* 204 */     Map<String, Object> infoMap; for (DriveDetectedInfo info : infos) {
/* 205 */       System.out.println(info.getLocation());
/*     */       
/* 207 */       infoMap = info.getInfoMap();
/* 208 */       for (String key : infoMap.keySet()) {
/* 209 */         Object val = infoMap.get(key);
/* 210 */         System.out.println("\t" + key + ": " + val);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/drivedetector/impl/DriveDetectorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */