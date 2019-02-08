/*     */ package org.gudy.azureus2.ui.swt.update;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
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
/*     */ public class UpdateAutoDownloader
/*     */   implements ResourceDownloaderListener
/*     */ {
/*     */   private final Update[] updates;
/*     */   private ArrayList downloaders;
/*     */   private Iterator iterDownloaders;
/*     */   private final cbCompletion completionCallback;
/*     */   
/*     */   public UpdateAutoDownloader(Update[] updates, cbCompletion completionCallback)
/*     */   {
/*  57 */     this.updates = updates;
/*  58 */     this.completionCallback = completionCallback;
/*  59 */     this.downloaders = new ArrayList();
/*     */     
/*  61 */     start();
/*     */   }
/*     */   
/*     */   private void start() {
/*  65 */     for (int i = 0; i < this.updates.length; i++) {
/*  66 */       Update update = this.updates[i];
/*  67 */       ResourceDownloader[] rds = update.getDownloaders();
/*  68 */       Collections.addAll(this.downloaders, rds);
/*     */     }
/*     */     
/*  71 */     this.iterDownloaders = this.downloaders.iterator();
/*  72 */     nextUpdate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean nextUpdate()
/*     */   {
/*  81 */     if (this.iterDownloaders.hasNext()) {
/*  82 */       ResourceDownloader downloader = (ResourceDownloader)this.iterDownloaders.next();
/*  83 */       downloader.addListener(this);
/*  84 */       downloader.asyncDownload();
/*  85 */       return true;
/*     */     }
/*  87 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void allDownloadsComplete()
/*     */   {
/*  96 */     boolean bRequiresRestart = false;
/*  97 */     boolean bHadMandatoryUpdates = false;
/*     */     
/*  99 */     for (int i = 0; i < this.updates.length; i++) {
/* 100 */       Update update = this.updates[i];
/*     */       
/* 102 */       if (update.getDownloaders().length > 0) {
/* 103 */         if (update.getRestartRequired() != 1) {
/* 104 */           bRequiresRestart = true;
/*     */         }
/* 106 */         if (update.isMandatory()) {
/* 107 */           bHadMandatoryUpdates = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 112 */     this.completionCallback.allUpdatesComplete(bRequiresRestart, bHadMandatoryUpdates);
/*     */   }
/*     */   
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 117 */     downloader.removeListener(this);
/* 118 */     if (!nextUpdate())
/*     */     {
/* 120 */       AEThread thread = new AEThread("AllDownloadsComplete", true) {
/*     */         public void runSupport() {
/* 122 */           UpdateAutoDownloader.this.allDownloadsComplete();
/*     */         }
/* 124 */       };
/* 125 */       thread.start();
/*     */     }
/* 127 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 133 */     downloader.removeListener(this);
/* 134 */     this.iterDownloaders.remove();
/* 135 */     nextUpdate();
/*     */   }
/*     */   
/*     */   public void reportActivity(ResourceDownloader downloader, String activity) {}
/*     */   
/*     */   public void reportAmountComplete(ResourceDownloader downloader, long amount) {}
/*     */   
/*     */   public void reportPercentComplete(ResourceDownloader downloader, int percentage) {}
/*     */   
/*     */   public static abstract interface cbCompletion
/*     */   {
/*     */     public abstract void allUpdatesComplete(boolean paramBoolean1, boolean paramBoolean2);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/update/UpdateAutoDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */