/*    */ package org.gudy.azureus2.plugins.utils.resourcedownloader;
/*    */ 
/*    */ import java.io.InputStream;
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
/*    */ public class ResourceDownloaderAdapter
/*    */   implements ResourceDownloaderListener
/*    */ {
/*    */   public void reportPercentComplete(ResourceDownloader downloader, int percentage) {}
/*    */   
/*    */   public void reportAmountComplete(ResourceDownloader downloader, long amount) {}
/*    */   
/*    */   public void reportActivity(ResourceDownloader downloader, String activity) {}
/*    */   
/*    */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*    */   {
/* 59 */     return true;
/*    */   }
/*    */   
/*    */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */