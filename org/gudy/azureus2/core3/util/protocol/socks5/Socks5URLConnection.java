/*    */ package org.gudy.azureus2.core3.util.protocol.socks5;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
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
/*    */ class Socks5URLConnection
/*    */   extends URLConnection
/*    */ {
/*    */   Socks5URLConnection(URL u)
/*    */   {
/* 42 */     super(u);
/*    */   }
/*    */   
/*    */ 
/*    */   public void connect()
/*    */     throws IOException
/*    */   {
/* 49 */     throw new IOException("Not Implemented");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/socks5/Socks5URLConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */