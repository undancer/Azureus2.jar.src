/*    */ package com.aelitis.azureus.plugins.extseed;
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
/*    */ public class ExternalSeedException
/*    */   extends Exception
/*    */ {
/* 26 */   private boolean permanent = false;
/*    */   
/*    */ 
/*    */ 
/*    */   public ExternalSeedException(String str)
/*    */   {
/* 32 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ExternalSeedException(String str, Throwable e)
/*    */   {
/* 40 */     super(str, e);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setPermanentFailure(boolean b)
/*    */   {
/* 47 */     this.permanent = b;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isPermanentFailure()
/*    */   {
/* 53 */     return this.permanent;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/ExternalSeedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */