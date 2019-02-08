/*    */ package org.gudy.azureus2.core3.xml.simpleparser;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*    */ import org.gudy.azureus2.pluginsimpl.local.utils.xml.simpleparser.SimpleXMLParserDocumentImpl;
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
/*    */ public class SimpleXMLParserDocumentFactory
/*    */ {
/*    */   public static SimpleXMLParserDocument create(File file)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 41 */     return new SimpleXMLParserDocumentImpl(file);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public static SimpleXMLParserDocument create(InputStream is)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 56 */     return new SimpleXMLParserDocumentImpl(null, is);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static SimpleXMLParserDocument create(URL source_url, InputStream is)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 66 */     return new SimpleXMLParserDocumentImpl(source_url, is);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static SimpleXMLParserDocument create(String data)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 75 */     return new SimpleXMLParserDocumentImpl(data);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/xml/simpleparser/SimpleXMLParserDocumentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */