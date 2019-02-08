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
/*    */ 
/*    */ 
/*    */ public class ResourceDownloaderException
/*    */   extends Exception
/*    */ {
/*    */   public ResourceDownloaderException(String str)
/*    */   {
/* 34 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ResourceDownloaderException(Throwable cause)
/*    */   {
/* 41 */     super(cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ResourceDownloaderException(ResourceDownloader rd, String str)
/*    */   {
/* 49 */     super(rd.getName() + ": " + str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ResourceDownloaderException(ResourceDownloader rd, String str, Throwable cause)
/*    */   {
/* 58 */     super(rd.getName() + ": " + str, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */