/*     */ package org.gudy.azureus2.core3.download;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.impl.DownloadManagerStateImpl;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
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
/*     */ public class DownloadManagerStateFactory
/*     */ {
/*     */   public static int MAX_FILES_FOR_INCOMPLETE_AND_DND_LINKAGE;
/*     */   
/*     */   static
/*     */   {
/*  44 */     COConfigurationManager.addAndFireParameterListener("Max File Links Supported", new ParameterListener()
/*     */     {
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*  50 */         DownloadManagerStateFactory.MAX_FILES_FOR_INCOMPLETE_AND_DND_LINKAGE = COConfigurationManager.getIntParameter(name);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DownloadManagerState getDownloadState(TOTorrent torrent)
/*     */     throws TOTorrentException
/*     */   {
/*  61 */     return DownloadManagerStateImpl.getDownloadState(torrent);
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
/*     */   public static void importDownloadState(File source_dir, byte[] download_hash)
/*     */     throws DownloadManagerException
/*     */   {
/*  90 */     DownloadManagerStateImpl.importDownloadState(source_dir, download_hash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void deleteDownloadState(byte[] download_hash)
/*     */     throws DownloadManagerException
/*     */   {
/*  99 */     DownloadManagerStateImpl.deleteDownloadState(download_hash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void deleteDownloadState(File source_dir, byte[] download_hash)
/*     */     throws DownloadManagerException
/*     */   {
/* 109 */     DownloadManagerStateImpl.deleteDownloadState(source_dir, download_hash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addGlobalListener(DownloadManagerStateAttributeListener l, String attribute, int event_type)
/*     */   {
/* 116 */     DownloadManagerStateImpl.addGlobalListener(l, attribute, event_type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeGlobalListener(DownloadManagerStateAttributeListener l, String attribute, int event_type)
/*     */   {
/* 123 */     DownloadManagerStateImpl.removeGlobalListener(l, attribute, event_type);
/*     */   }
/*     */   
/*     */   public static void loadGlobalStateCache() {}
/*     */   
/*     */   public static void saveGlobalStateCache() {}
/*     */   
/*     */   public static void discardGlobalStateCache() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerStateFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */