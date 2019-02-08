/*    */ package org.gudy.azureus2.core3.util.protocol.udp;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLStreamHandler;
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
/*    */ public class Handler
/*    */   extends URLStreamHandler
/*    */ {
/*    */   public URLConnection openConnection(URL u)
/*    */   {
/* 39 */     return new UDPURLConnection(u);
/*    */   }
/*    */   
/*    */ 
/*    */   public int getDefaultPort()
/*    */   {
/* 45 */     return 80;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/udp/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */