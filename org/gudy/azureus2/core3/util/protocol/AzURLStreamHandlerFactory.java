/*    */ package org.gudy.azureus2.core3.util.protocol;
/*    */ 
/*    */ import java.net.URLStreamHandler;
/*    */ import java.net.URLStreamHandlerFactory;
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
/*    */ public class AzURLStreamHandlerFactory
/*    */   implements URLStreamHandlerFactory
/*    */ {
/* 25 */   private static final String packageName = AzURLStreamHandlerFactory.class.getPackage().getName();
/*    */   
/*    */   public URLStreamHandler createURLStreamHandler(String protocol)
/*    */   {
/* 29 */     if ((protocol.equals("file")) || (protocol.equals("jar")))
/* 30 */       return null;
/* 31 */     String clsName = packageName + "." + protocol + ".Handler";
/*    */     try
/*    */     {
/* 34 */       Class cls = Class.forName(clsName);
/* 35 */       return (URLStreamHandler)cls.newInstance();
/*    */     }
/*    */     catch (Throwable e) {}
/*    */     
/*    */ 
/* 40 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/AzURLStreamHandlerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */