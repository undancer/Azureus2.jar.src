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
/*    */ 
/*    */ 
/*    */ public class BuddyPluginPasswordException
/*    */   extends BuddyPluginException
/*    */ {
/*    */   private boolean was_incorrecte;
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
/*    */   public BuddyPluginPasswordException(boolean _was_incorrecte, String str, Throwable cause)
/*    */   {
/* 37 */     super(str, cause);
/*    */     
/* 39 */     this.was_incorrecte = _was_incorrecte;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean wasIncorrect()
/*    */   {
/* 45 */     return this.was_incorrecte;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginPasswordException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */