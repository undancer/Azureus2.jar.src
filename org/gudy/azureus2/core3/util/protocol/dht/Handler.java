/*    */ package org.gudy.azureus2.core3.util.protocol.dht;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLStreamHandler;
/*    */ import org.gudy.azureus2.core3.util.Base32;
/*    */ import org.gudy.azureus2.core3.util.ByteFormatter;
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
/*    */ 
/*    */ public class Handler
/*    */   extends URLStreamHandler
/*    */ {
/*    */   public URLConnection openConnection(URL u)
/*    */   {
/*    */     URL magnet_url;
/*    */     try
/*    */     {
/* 50 */       String str = u.toString();
/*    */       
/* 52 */       str = str.substring(6);
/*    */       
/* 54 */       int param_pos = str.indexOf('/');
/*    */       
/* 56 */       String hash = param_pos == -1 ? str : str.substring(0, param_pos);
/*    */       
/* 58 */       hash = hash.trim();
/*    */       
/* 60 */       int dot_pos = hash.indexOf('.');
/*    */       
/* 62 */       if (dot_pos != -1)
/*    */       {
/* 64 */         hash = hash.substring(0, dot_pos).trim();
/*    */       }
/*    */       
/* 67 */       if (hash.length() == 40)
/*    */       {
/* 69 */         hash = Base32.encode(ByteFormatter.decodeString(hash));
/*    */       }
/*    */       
/* 72 */       magnet_url = new URL("magnet:?xt=urn:btih:" + hash + "/" + (param_pos == -1 ? "" : str.substring(param_pos + 1)));
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 76 */       Debug.out("Failed to transform dht url '" + u + "'", e);
/*    */       
/* 78 */       return null;
/*    */     }
/*    */     
/*    */ 
/*    */     try
/*    */     {
/* 84 */       return magnet_url.openConnection();
/*    */     }
/*    */     catch (MalformedURLException e)
/*    */     {
/* 88 */       Debug.printStackTrace(e);
/*    */       
/* 90 */       return null;
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 94 */       Debug.printStackTrace(e);
/*    */     }
/* 96 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/dht/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */