/*    */ package org.gudy.azureus2.core3.xml.util;
/*    */ 
/*    */ import java.io.PrintWriter;
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
/*    */ public class XMLEscapeWriter
/*    */   extends PrintWriter
/*    */ {
/* 29 */   private boolean enabled = true;
/*    */   
/*    */ 
/*    */ 
/*    */   public XMLEscapeWriter(PrintWriter pw)
/*    */   {
/* 35 */     super(pw);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void print(String str)
/*    */   {
/* 42 */     if (this.enabled)
/*    */     {
/* 44 */       super.print(XUXmlWriter.escapeXML(str));
/*    */     }
/*    */     else
/*    */     {
/* 48 */       super.print(str);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setEnabled(boolean b)
/*    */   {
/* 56 */     this.enabled = b;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/xml/util/XMLEscapeWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */