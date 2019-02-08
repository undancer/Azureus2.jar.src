/*    */ package org.gudy.azureus2.plugins;
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
/*    */ public class PluginException
/*    */   extends Exception
/*    */ {
/*    */   public PluginException(String str)
/*    */   {
/* 39 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public PluginException(String str, Throwable e)
/*    */   {
/* 47 */     super(str, e);
/*    */   }
/*    */   
/*    */   public PluginException(Throwable e) {
/* 51 */     super(e);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */