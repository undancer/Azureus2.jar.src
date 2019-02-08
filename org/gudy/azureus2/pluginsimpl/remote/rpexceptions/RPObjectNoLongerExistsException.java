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
/*    */ public class RPObjectNoLongerExistsException
/*    */   extends RPException
/*    */ {
/*    */   public RPObjectNoLongerExistsException()
/*    */   {
/* 26 */     super("Object no longer exists");
/*    */   }
/*    */   
/*    */   public String getRPType() {
/* 30 */     return "object-no-longer-exists";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/rpexceptions/RPObjectNoLongerExistsException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */