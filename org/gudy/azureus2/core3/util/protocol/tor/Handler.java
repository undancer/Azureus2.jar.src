/*    */ package org.gudy.azureus2.core3.util.protocol.tor;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.Proxy;
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
/*    */ 
/*    */ 
/*    */ public class Handler
/*    */   extends URLStreamHandler
/*    */ {
/*    */   public URLConnection openConnection(URL u)
/*    */     throws IOException
/*    */   {
/* 44 */     throw new IOException("tor: URIs can't be used directly");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public URLConnection openConnection(URL u, Proxy proxy)
/*    */     throws IOException
/*    */   {
/* 54 */     throw new IOException("tor: URIs can't be used directly");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/tor/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */