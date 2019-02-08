/*    */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.download.DownloadException;
/*    */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*    */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*    */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*    */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShortCutsImpl
/*    */   implements ShortCuts
/*    */ {
/*    */   protected PluginInterface pi;
/*    */   
/*    */   public ShortCutsImpl(PluginInterface _pi)
/*    */   {
/* 44 */     this.pi = _pi;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DownloadStats getDownloadStats(byte[] hash)
/*    */     throws DownloadException
/*    */   {
/* 53 */     return getDownload(hash).getStats();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void restartDownload(byte[] hash)
/*    */     throws DownloadException
/*    */   {
/* 62 */     getDownload(hash).restart();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void stopDownload(byte[] hash)
/*    */     throws DownloadException
/*    */   {
/* 71 */     getDownload(hash).stop();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void removeDownload(byte[] hash)
/*    */     throws DownloadException, DownloadRemovalVetoException
/*    */   {
/* 80 */     getDownload(hash).remove();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public Download getDownload(byte[] hash)
/*    */     throws DownloadException
/*    */   {
/* 89 */     Download dl = ((DownloadManagerImpl)this.pi.getDownloadManager()).getDownload(hash);
/*    */     
/* 91 */     if (dl == null)
/*    */     {
/* 93 */       throw new DownloadException("Torrent not found");
/*    */     }
/*    */     
/* 96 */     return dl;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/ShortCutsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */