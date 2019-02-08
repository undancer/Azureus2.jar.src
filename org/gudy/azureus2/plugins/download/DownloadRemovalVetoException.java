/*    */ package org.gudy.azureus2.plugins.download;
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
/*    */ public class DownloadRemovalVetoException
/*    */   extends Exception
/*    */ {
/*    */   private boolean silent;
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
/*    */   public DownloadRemovalVetoException(String str, boolean silent)
/*    */   {
/* 41 */     super(str);
/* 42 */     this.silent = silent;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DownloadRemovalVetoException(String str)
/*    */   {
/* 49 */     this(str, false);
/*    */   }
/*    */   
/*    */   public boolean isSilent() {
/* 53 */     return this.silent;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadRemovalVetoException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */