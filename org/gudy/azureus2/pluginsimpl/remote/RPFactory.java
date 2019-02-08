/*    */ package org.gudy.azureus2.pluginsimpl.remote;
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
/*    */ 
/*    */ 
/*    */ public class RPFactory
/*    */ {
/*    */   public static RPPluginInterface getPlugin(RPRequestDispatcher dispatcher)
/*    */     throws RPException
/*    */   {
/* 38 */     RPRequest request = new RPRequest(null, "getSingleton", null);
/*    */     
/* 40 */     RPReply reply = dispatcher.dispatch(request);
/*    */     
/* 42 */     RPPluginInterface pi = (RPPluginInterface)reply.getResponse();
/*    */     
/* 44 */     pi._setRemote(dispatcher);
/*    */     
/* 46 */     return pi;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */