/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.plugins.ui.config.DirectoryParameter;
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
/*    */ 
/*    */ public class DirectoryParameterImpl
/*    */   extends ParameterImpl
/*    */   implements DirectoryParameter
/*    */ {
/*    */   private String defaultValue;
/*    */   
/*    */   public DirectoryParameterImpl(PluginConfigImpl config, String key, String label, String defaultValue)
/*    */   {
/* 49 */     super(config, key, label);
/*    */     
/* 51 */     this.defaultValue = defaultValue;
/*    */     
/* 53 */     config.notifyParamExists(getKey());
/* 54 */     COConfigurationManager.setStringDefault(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getDefaultValue()
/*    */   {
/* 61 */     return this.defaultValue;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 67 */     return this.config.getUnsafeStringParameter(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setValue(String str)
/*    */   {
/* 74 */     this.config.setUnsafeStringParameter(getKey(), str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/DirectoryParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */