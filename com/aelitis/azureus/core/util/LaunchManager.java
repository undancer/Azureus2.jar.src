/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
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
/*     */ public class LaunchManager
/*     */ {
/*  30 */   private static final LaunchManager singleton = new LaunchManager();
/*     */   final CopyOnWriteList<LaunchController> controllers;
/*     */   
/*     */   public static LaunchManager getManager()
/*     */   {
/*  35 */     return singleton;
/*     */   }
/*     */   
/*  38 */   public LaunchManager() { this.controllers = new CopyOnWriteList(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void launchRequest(final LaunchTarget target, final LaunchAction action)
/*     */   {
/*  45 */     new AEThread2("LaunchManager:request")
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*  50 */         for (LaunchManager.LaunchController c : LaunchManager.this.controllers) {
/*     */           try
/*     */           {
/*  53 */             c.handleRequest(target);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*  57 */             action.actionDenied(e);
/*     */             
/*  59 */             return;
/*     */           }
/*     */         }
/*     */         
/*  63 */         action.actionAllowed();
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public LaunchTarget createTarget(DownloadManager dm)
/*     */   {
/*  72 */     return new LaunchTarget(dm, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public LaunchTarget createTarget(DiskManagerFileInfo fi)
/*     */   {
/*  79 */     return new LaunchTarget(fi, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addController(LaunchController controller)
/*     */   {
/*  86 */     this.controllers.add(controller);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  93 */   public void removeController(LaunchController controller) { this.controllers.remove(controller); }
/*     */   
/*     */   public static abstract interface LaunchAction { public abstract void actionAllowed();
/*     */     
/*     */     public abstract void actionDenied(Throwable paramThrowable);
/*     */   }
/*     */   
/*     */   public static abstract interface LaunchController { public abstract void handleRequest(LaunchManager.LaunchTarget paramLaunchTarget) throws Throwable;
/*     */   }
/*     */   
/*     */   public static class LaunchTarget { private final DownloadManager dm;
/*     */     private DiskManagerFileInfo file_info;
/*     */     
/* 106 */     private LaunchTarget(DownloadManager _dm) { this.dm = _dm; }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private LaunchTarget(DiskManagerFileInfo _file_info)
/*     */     {
/* 113 */       this.file_info = _file_info;
/* 114 */       this.dm = this.file_info.getDownloadManager();
/*     */     }
/*     */     
/*     */ 
/*     */     public DownloadManager getDownload()
/*     */     {
/* 120 */       return this.dm;
/*     */     }
/*     */     
/*     */ 
/*     */     public DiskManagerFileInfo getFile()
/*     */     {
/* 126 */       return this.file_info;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/LaunchManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */