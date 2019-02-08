/*    */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.simpleparser;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentFactory;
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
/*    */ public class SimpleXMLParserDocumentFactoryImpl
/*    */   implements SimpleXMLParserDocumentFactory
/*    */ {
/*    */   public SimpleXMLParserDocument create(File file)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 43 */     return new SimpleXMLParserDocumentImpl(file);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public SimpleXMLParserDocument create(InputStream is)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 52 */     return new SimpleXMLParserDocumentImpl(null, is);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SimpleXMLParserDocument create(URL source_url, InputStream is)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 62 */     return new SimpleXMLParserDocumentImpl(source_url, is);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public SimpleXMLParserDocument create(String data)
/*    */     throws SimpleXMLParserDocumentException
/*    */   {
/* 71 */     return new SimpleXMLParserDocumentImpl(data);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/simpleparser/SimpleXMLParserDocumentFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */