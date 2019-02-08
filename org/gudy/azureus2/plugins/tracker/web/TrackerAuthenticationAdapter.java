/*    */ package org.gudy.azureus2.plugins.tracker.web;
/*    */ 
/*    */ import java.net.URL;
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
/*    */ public class TrackerAuthenticationAdapter
/*    */   implements TrackerAuthenticationListener
/*    */ {
/*    */   public boolean authenticate(URL resource, String user, String password)
/*    */   {
/* 39 */     return false;
/*    */   }
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
/*    */   public boolean authenticate(String headers, URL resource, String user, String password)
/*    */   {
/* 58 */     return authenticate(resource, user, password);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] authenticate(URL resource, String user)
/*    */   {
/* 66 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/web/TrackerAuthenticationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */