/*    */ package org.gudy.azureus2.core3.util.protocol.i2p;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLStreamHandler;
/*    */ import org.gudy.azureus2.core3.util.Debug;
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
/*    */   {
/* 44 */     String str = u.toString();
/*    */     
/* 46 */     str = "http" + str.substring(3);
/*    */     try
/*    */     {
/* 49 */       return new URL(str).openConnection();
/*    */     }
/*    */     catch (MalformedURLException e)
/*    */     {
/* 53 */       Debug.printStackTrace(e);
/*    */       
/* 55 */       return null;
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 59 */       Debug.printStackTrace(e);
/*    */     }
/* 61 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/i2p/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */