/*    */ package com.aelitis.azureus.plugins.net.buddy;
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
/*    */ public class BuddyPluginTimeoutException
/*    */   extends BuddyPluginException
/*    */ {
/*    */   private boolean was_active;
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
/*    */   protected BuddyPluginTimeoutException(String str, boolean active)
/*    */   {
/* 34 */     super(str);
/*    */     
/* 36 */     this.was_active = active;
/*    */   }
/*    */   
/*    */ 
/*    */   protected boolean wasActive()
/*    */   {
/* 42 */     return this.was_active;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginTimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */