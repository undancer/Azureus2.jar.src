/*    */ package org.gudy.azureus2.core3.html;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import org.gudy.azureus2.core3.html.impl.HTMLPageImpl;
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
/*    */ public class HTMLPageFactory
/*    */ {
/*    */   public static HTMLPage loadPage(InputStream is)
/*    */     throws HTMLException
/*    */   {
/* 39 */     return loadPage(is, true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static HTMLPage loadPage(InputStream is, String charset)
/*    */     throws HTMLException
/*    */   {
/* 49 */     return loadPage(is, charset, true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static HTMLPage loadPage(InputStream is, boolean close_file)
/*    */     throws HTMLException
/*    */   {
/* 59 */     return loadPage(is, null, close_file);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static HTMLPage loadPage(InputStream is, String charset, boolean close_file)
/*    */     throws HTMLException
/*    */   {
/* 70 */     return new HTMLPageImpl(is, charset, close_file);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/html/HTMLPageFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */