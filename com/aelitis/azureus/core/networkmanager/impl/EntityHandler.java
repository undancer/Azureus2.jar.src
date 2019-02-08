/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*     */ import java.util.HashMap;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class EntityHandler
/*     */ {
/*  38 */   private final HashMap upgraded_connections = new HashMap();
/*  39 */   private final AEMonitor lock = new AEMonitor("EntityHandler");
/*     */   private final MultiPeerUploader global_uploader;
/*     */   private final MultiPeerDownloader2 global_downloader;
/*  42 */   private boolean global_registered = false;
/*     */   
/*     */ 
/*     */ 
/*     */   private final int handler_type;
/*     */   
/*     */ 
/*     */ 
/*     */   public EntityHandler(int type, RateHandler rate_handler)
/*     */   {
/*  52 */     this.handler_type = type;
/*  53 */     if (this.handler_type == 0) {
/*  54 */       this.global_uploader = new MultiPeerUploader(rate_handler);
/*  55 */       this.global_downloader = null;
/*     */     }
/*     */     else {
/*  58 */       this.global_downloader = new MultiPeerDownloader2(rate_handler);
/*  59 */       this.global_uploader = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerPeerConnection(NetworkConnectionBase connection)
/*     */   {
/*     */     try
/*     */     {
/*  70 */       this.lock.enter();
/*  71 */       if (!this.global_registered) {
/*  72 */         if (this.handler_type == 0) {
/*  73 */           NetworkManager.getSingleton().addWriteEntity(this.global_uploader, -1);
/*     */         }
/*     */         else {
/*  76 */           NetworkManager.getSingleton().addReadEntity(this.global_downloader, -1);
/*     */         }
/*     */         
/*  79 */         this.global_registered = true;
/*     */       }
/*     */     } finally {
/*  82 */       this.lock.exit();
/*     */     }
/*  84 */     if (this.handler_type == 0) {
/*  85 */       this.global_uploader.addPeerConnection(connection);
/*     */     }
/*     */     else {
/*  88 */       this.global_downloader.addPeerConnection(connection);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cancelPeerConnection(NetworkConnectionBase connection)
/*     */   {
/*  98 */     if (this.handler_type == 0) {
/*  99 */       if (!this.global_uploader.removePeerConnection(connection)) {
/* 100 */         SinglePeerUploader upload_entity = (SinglePeerUploader)this.upgraded_connections.remove(connection);
/* 101 */         if (upload_entity != null) {
/* 102 */           NetworkManager.getSingleton().removeWriteEntity(upload_entity);
/*     */         }
/*     */         
/*     */       }
/*     */     }
/* 107 */     else if (!this.global_downloader.removePeerConnection(connection)) {
/* 108 */       SinglePeerDownloader download_entity = (SinglePeerDownloader)this.upgraded_connections.remove(connection);
/* 109 */       if (download_entity != null) {
/* 110 */         NetworkManager.getSingleton().removeReadEntity(download_entity);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void upgradePeerConnection(NetworkConnectionBase connection, RateHandler handler, int partition_id)
/*     */   {
/*     */     try
/*     */     {
/* 124 */       this.lock.enter();
/* 125 */       if (this.handler_type == 0) {
/* 126 */         SinglePeerUploader upload_entity = new SinglePeerUploader(connection, handler);
/* 127 */         if (!this.global_uploader.removePeerConnection(connection)) {
/* 128 */           Debug.out("upgradePeerConnection:: upload entity not found/removed !");
/*     */         }
/* 130 */         NetworkManager.getSingleton().addWriteEntity(upload_entity, partition_id);
/* 131 */         this.upgraded_connections.put(connection, upload_entity);
/*     */       }
/*     */       else {
/* 134 */         SinglePeerDownloader download_entity = new SinglePeerDownloader(connection, handler);
/* 135 */         if (!this.global_downloader.removePeerConnection(connection)) {
/* 136 */           Debug.out("upgradePeerConnection:: download entity not found/removed !");
/*     */         }
/* 138 */         NetworkManager.getSingleton().addReadEntity(download_entity, partition_id);
/* 139 */         this.upgraded_connections.put(connection, download_entity);
/*     */       }
/*     */     } finally {
/* 142 */       this.lock.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void downgradePeerConnection(NetworkConnectionBase connection)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       this.lock.enter();
/* 152 */       if (this.handler_type == 0) {
/* 153 */         SinglePeerUploader upload_entity = (SinglePeerUploader)this.upgraded_connections.remove(connection);
/* 154 */         if (upload_entity != null) {
/* 155 */           NetworkManager.getSingleton().removeWriteEntity(upload_entity);
/*     */         }
/*     */         else {
/* 158 */           Debug.out("upload_entity == null");
/*     */         }
/* 160 */         this.global_uploader.addPeerConnection(connection);
/*     */       }
/*     */       else {
/* 163 */         SinglePeerDownloader download_entity = (SinglePeerDownloader)this.upgraded_connections.remove(connection);
/* 164 */         if (download_entity != null) {
/* 165 */           NetworkManager.getSingleton().removeReadEntity(download_entity);
/*     */         }
/*     */         else {
/* 168 */           Debug.out("download_entity == null");
/*     */         }
/* 170 */         this.global_downloader.addPeerConnection(connection);
/*     */       }
/*     */     } finally {
/* 173 */       this.lock.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public RateHandler getRateHandler(NetworkConnectionBase connection)
/*     */   {
/*     */     try
/*     */     {
/* 181 */       this.lock.enter();
/*     */       RateHandler localRateHandler;
/* 183 */       if (this.handler_type == 0)
/*     */       {
/* 185 */         SinglePeerUploader upload_entity = (SinglePeerUploader)this.upgraded_connections.get(connection);
/*     */         
/* 187 */         if (upload_entity != null)
/*     */         {
/* 189 */           return upload_entity.getRateHandler();
/*     */         }
/*     */         
/* 192 */         return this.global_uploader.getRateHandler();
/*     */       }
/*     */       
/*     */ 
/* 196 */       SinglePeerDownloader download_entity = (SinglePeerDownloader)this.upgraded_connections.get(connection);
/*     */       
/* 198 */       if (download_entity != null)
/*     */       {
/* 200 */         return download_entity.getRateHandler();
/*     */       }
/*     */       
/* 203 */       return this.global_downloader.getRateHandler();
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 208 */       this.lock.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/EntityHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */