/*    */ package org.gudy.azureus2.core3.util.protocol.magnet;
/*    */ 
/*    */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Handler
/*    */   extends URLStreamHandler
/*    */ {
/*    */   public URLConnection openConnection(URL u)
/*    */   {
/* 49 */     new MagnetConnection2(u, new MagnetConnection2.MagnetHandler()
/*    */     {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       public void process(URL magnet, OutputStream os)
/*    */         throws IOException
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 62 */         String get = "/download/" + magnet.toString().substring(7) + " HTTP/1.0\r\n\r\n";
/*    */         
/* 64 */         MagnetURIHandler.getSingleton().process(get, new ByteArrayInputStream(new byte[0]), os);
/*    */       }
/*    */     });
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/magnet/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */