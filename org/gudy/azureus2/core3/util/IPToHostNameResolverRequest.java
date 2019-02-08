/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IPToHostNameResolverRequest
/*    */ {
/*    */   protected final String ip;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected IPToHostNameResolverListener listener;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected IPToHostNameResolverRequest(String _ip, IPToHostNameResolverListener _listener)
/*    */   {
/* 34 */     this.ip = _ip;
/* 35 */     this.listener = _listener;
/*    */   }
/*    */   
/*    */ 
/*    */   public void cancel()
/*    */   {
/* 41 */     this.listener = null;
/*    */   }
/*    */   
/*    */ 
/*    */   protected String getIP()
/*    */   {
/* 47 */     return this.ip;
/*    */   }
/*    */   
/*    */ 
/*    */   protected IPToHostNameResolverListener getListener()
/*    */   {
/* 53 */     return this.listener;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/IPToHostNameResolverRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */