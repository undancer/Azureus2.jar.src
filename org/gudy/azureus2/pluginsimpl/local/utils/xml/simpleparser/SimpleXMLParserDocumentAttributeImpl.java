/*    */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.simpleparser;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentAttribute;
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
/*    */ public class SimpleXMLParserDocumentAttributeImpl
/*    */   implements SimpleXMLParserDocumentAttribute
/*    */ {
/*    */   protected String name;
/*    */   protected String value;
/*    */   
/*    */   protected SimpleXMLParserDocumentAttributeImpl(String _name, String _value)
/*    */   {
/* 39 */     this.name = _name;
/* 40 */     this.value = _value;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 46 */     return this.name;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 52 */     return this.value;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/simpleparser/SimpleXMLParserDocumentAttributeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */