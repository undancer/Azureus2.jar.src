/*    */ package org.gudy.azureus2.pluginsimpl.remote.rpexceptions;
/*    */ 
/*    */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
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
/*    */ public class RPMethodAccessDeniedException
/*    */   extends RPException
/*    */ {
/*    */   public RPMethodAccessDeniedException()
/*    */   {
/* 26 */     super("Access Denied");
/*    */   }
/*    */   
/*    */   public String getRPType() {
/* 30 */     return "method-access-denied";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/rpexceptions/RPMethodAccessDeniedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */