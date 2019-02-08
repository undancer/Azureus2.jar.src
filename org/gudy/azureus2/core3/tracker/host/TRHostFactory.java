/*    */ package org.gudy.azureus2.core3.tracker.host;
/*    */ 
/*    */ import org.gudy.azureus2.core3.tracker.host.impl.TRHostImpl;
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
/*    */ 
/*    */ 
/*    */ public class TRHostFactory
/*    */ {
/*    */   public static TRHost getSingleton()
/*    */   {
/* 37 */     return TRHostImpl.create();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */