/*    */ package org.gudy.azureus2.core3.util.protocol.bc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLStreamHandler;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*    */     URL magnet_url;
/*    */     try
/*    */     {
/* 46 */       String str = UrlUtils.parseTextForMagnets(u.toExternalForm());
/*    */       
/* 48 */       if (str == null)
/*    */       {
/* 50 */         Debug.out("Failed to transform bc url '" + u + "'");
/*    */         
/* 52 */         return null;
/*    */       }
/*    */       
/* 55 */       magnet_url = new URL(str);
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 59 */       Debug.out("Failed to transform bc url '" + u + "'", e);
/*    */       
/* 61 */       return null;
/*    */     }
/*    */     
/*    */ 
/*    */     try
/*    */     {
/* 67 */       return magnet_url.openConnection();
/*    */     }
/*    */     catch (MalformedURLException e)
/*    */     {
/* 71 */       Debug.printStackTrace(e);
/*    */       
/* 73 */       return null;
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 77 */       Debug.printStackTrace(e);
/*    */     }
/* 79 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/bc/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */