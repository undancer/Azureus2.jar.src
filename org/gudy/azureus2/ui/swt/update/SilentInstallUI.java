/*     */ package org.gudy.azureus2.ui.swt.update;
/*     */ 
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
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
/*     */ public class SilentInstallUI
/*     */ {
/*     */   private UpdateMonitor monitor;
/*     */   private UpdateCheckInstance instance;
/*     */   private boolean cancelled;
/*     */   
/*     */   protected SilentInstallUI(UpdateMonitor _monitor, UpdateCheckInstance _instance)
/*     */   {
/*  44 */     this.monitor = _monitor;
/*  45 */     this.instance = _instance;
/*     */     try
/*     */     {
/*  48 */       this.monitor.addDecisionHandler(_instance);
/*     */       
/*  50 */       new AEThread2("SilentInstallerUI", true)
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/*  56 */             Update[] updates = SilentInstallUI.this.instance.getUpdates();
/*     */             
/*  58 */             for (Update update : updates)
/*     */             {
/*  60 */               ResourceDownloader[] downloaders = update.getDownloaders();
/*     */               
/*  62 */               for (ResourceDownloader downloader : downloaders)
/*     */               {
/*  64 */                 synchronized (SilentInstallUI.this)
/*     */                 {
/*  66 */                   if (SilentInstallUI.this.cancelled)
/*     */                   {
/*  68 */                     return;
/*     */                   }
/*     */                 }
/*     */                 
/*  72 */                 downloader.download();
/*     */               }
/*     */             }
/*     */             
/*  76 */             boolean restart_required = false;
/*     */             
/*  78 */             for (int i = 0; i < updates.length; i++)
/*     */             {
/*  80 */               if (updates[i].getRestartRequired() == 2)
/*     */               {
/*  82 */                 restart_required = true;
/*     */               }
/*     */             }
/*     */             
/*  86 */             if (restart_required)
/*     */             {
/*  88 */               SilentInstallUI.this.monitor.handleRestart();
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/*  92 */             Debug.out("Install failed", e);
/*     */             
/*  94 */             SilentInstallUI.this.instance.cancel();
/*     */           }
/*     */         }
/*     */       }.start();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 101 */       Debug.out(e);
/*     */       
/* 103 */       this.instance.cancel();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/update/SilentInstallUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */