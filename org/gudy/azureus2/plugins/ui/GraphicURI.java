/*    */ package org.gudy.azureus2.plugins.ui;
/*    */ 
/*    */ import java.net.URI;
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
/*    */ public class GraphicURI
/*    */   implements Graphic
/*    */ {
/*    */   private URI uri;
/*    */   
/*    */   public GraphicURI(URI uri)
/*    */   {
/* 34 */     this.uri = uri;
/*    */   }
/*    */   
/*    */   public URI getURI() {
/* 38 */     return this.uri;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/GraphicURI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */