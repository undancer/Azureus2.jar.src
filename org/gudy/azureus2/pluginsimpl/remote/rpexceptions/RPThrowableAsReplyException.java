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
/*    */ public class RPThrowableAsReplyException
/*    */   extends RPException
/*    */ {
/*    */   public RPThrowableAsReplyException(Throwable t)
/*    */   {
/* 26 */     super(t);
/*    */   }
/*    */   
/*    */   public String getRPType() {
/* 30 */     return "error-as-reply";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/rpexceptions/RPThrowableAsReplyException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */