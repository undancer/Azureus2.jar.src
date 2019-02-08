/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.config.HyperlinkParameter;
/*    */ import org.gudy.azureus2.pluginsimpl.local.PluginConfigImpl;
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
/*    */ public class HyperlinkParameterImpl
/*    */   extends LabelParameterImpl
/*    */   implements HyperlinkParameter
/*    */ {
/*    */   private String hyperlink;
/*    */   
/*    */   public HyperlinkParameterImpl(PluginConfigImpl config, String key, String label, String hyperlink)
/*    */   {
/* 33 */     super(config, key, label);
/* 34 */     this.hyperlink = hyperlink;
/*    */   }
/*    */   
/*    */   public String getHyperlink() {
/* 38 */     return this.hyperlink;
/*    */   }
/*    */   
/*    */   public void setHyperlink(String url_location) {
/* 42 */     this.hyperlink = url_location;
/*    */     
/* 44 */     fireParameterChanged();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/HyperlinkParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */