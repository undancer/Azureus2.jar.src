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
/*    */ public class RPInternalProcessException
/*    */   extends RPException
/*    */ {
/*    */   public RPInternalProcessException(Throwable t)
/*    */   {
/* 26 */     super(t);
/*    */   }
/*    */   
/*    */   public String getRPType() {
/* 30 */     return "internal-error";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/rpexceptions/RPInternalProcessException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */