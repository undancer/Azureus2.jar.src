/*    */ package org.gudy.azureus2.plugins.utils.resourcedownloader;
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
/*    */ public class ResourceDownloaderCancelledException
/*    */   extends ResourceDownloaderException
/*    */ {
/*    */   public ResourceDownloaderCancelledException(ResourceDownloader rd)
/*    */   {
/* 32 */     super(rd, "Download cancelled");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderCancelledException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */