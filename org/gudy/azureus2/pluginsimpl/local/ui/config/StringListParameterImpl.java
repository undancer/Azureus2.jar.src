/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.plugins.ui.config.StringListParameter;
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
/*    */ public class StringListParameterImpl
/*    */   extends ParameterImpl
/*    */   implements StringListParameter
/*    */ {
/*    */   private String defaultValue;
/*    */   private String[] values;
/*    */   private String[] labels;
/*    */   
/*    */   public StringListParameterImpl(PluginConfigImpl config, String key, String label, String defaultValue, String[] values, String[] labels)
/*    */   {
/* 44 */     super(config, key, label);
/* 45 */     this.defaultValue = defaultValue;
/* 46 */     this.values = values;
/* 47 */     this.labels = labels;
/* 48 */     config.notifyParamExists(getKey());
/* 49 */     COConfigurationManager.setStringDefault(getKey(), defaultValue);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getDefaultValue()
/*    */   {
/* 55 */     return this.defaultValue;
/*    */   }
/*    */   
/*    */   public String[] getValues()
/*    */   {
/* 60 */     return this.values;
/*    */   }
/*    */   
/*    */   public String[] getLabels()
/*    */   {
/* 65 */     return this.labels;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setLabels(String[] _labels)
/*    */   {
/* 72 */     this.labels = _labels;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 78 */     return this.config.getUnsafeStringParameter(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setValue(String s)
/*    */   {
/* 85 */     this.config.setUnsafeStringParameter(getKey(), s);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/StringListParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */