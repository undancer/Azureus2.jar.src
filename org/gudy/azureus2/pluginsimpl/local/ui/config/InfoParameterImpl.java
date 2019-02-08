/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.config.InfoParameter;
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
/*    */ public class InfoParameterImpl
/*    */   extends ParameterImpl
/*    */   implements InfoParameter
/*    */ {
/*    */   public InfoParameterImpl(PluginConfigImpl config, String key, String label, String value)
/*    */   {
/* 43 */     super(config, key, label);
/*    */     
/* 45 */     setValue(value);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 51 */     return this.config.getUnsafeStringParameter(getKey(), "");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setValue(String s)
/*    */   {
/* 58 */     this.config.setUnsafeStringParameter(getKey(), s);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/InfoParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */