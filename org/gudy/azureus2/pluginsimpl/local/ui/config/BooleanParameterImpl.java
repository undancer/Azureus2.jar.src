/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
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
/*    */ 
/*    */ 
/*    */ public class BooleanParameterImpl
/*    */   extends ParameterImpl
/*    */   implements BooleanParameter
/*    */ {
/*    */   private boolean default_value;
/*    */   
/*    */   public BooleanParameterImpl(PluginConfigImpl config, String key, String label, boolean defaultValue)
/*    */   {
/* 48 */     super(config, key, label);
/* 49 */     this.default_value = defaultValue;
/* 50 */     config.notifyParamExists(getKey());
/* 51 */     COConfigurationManager.setBooleanDefault(getKey(), defaultValue);
/*    */   }
/*    */   
/*    */   public boolean getDefaultValue() {
/* 55 */     return this.default_value;
/*    */   }
/*    */   
/*    */   public boolean getValue() {
/* 59 */     return this.config.getUnsafeBooleanParameter(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */   public void setValue(boolean b) {
/* 63 */     this.config.setUnsafeBooleanParameter(getKey(), b);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setDefaultValue(boolean defaultValue)
/*    */   {
/* 70 */     this.default_value = defaultValue;
/*    */     
/* 72 */     COConfigurationManager.setBooleanDefault(getKey(), defaultValue);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/BooleanParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */