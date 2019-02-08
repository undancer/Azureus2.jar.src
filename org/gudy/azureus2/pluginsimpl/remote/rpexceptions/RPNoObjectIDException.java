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
/*    */ public class RPNoObjectIDException
/*    */   extends RPException
/*    */ {
/*    */   public RPNoObjectIDException()
/*    */   {
/* 26 */     super("Object identifier missing from request");
/*    */   }
/*    */   
/*    */   public String getRPType() {
/* 30 */     return "no-object-id-given";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/rpexceptions/RPNoObjectIDException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */