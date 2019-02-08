/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
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
/*    */ public class StringParameterImpl
/*    */   extends ParameterImpl
/*    */   implements StringParameter
/*    */ {
/*    */   private String defaultValue;
/*    */   private int line_count;
/*    */   
/*    */   public StringParameterImpl(PluginConfigImpl config, String key, String label, String defaultValue)
/*    */   {
/* 36 */     super(config, key, label);
/* 37 */     config.notifyParamExists(getKey());
/* 38 */     COConfigurationManager.setStringDefault(getKey(), defaultValue);
/* 39 */     this.defaultValue = defaultValue;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getDefaultValue()
/*    */   {
/* 47 */     return this.defaultValue;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 53 */     return this.config.getUnsafeStringParameter(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setValue(String s)
/*    */   {
/* 60 */     this.config.setUnsafeStringParameter(getKey(), s);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setMultiLine(int visible_line_count)
/*    */   {
/* 67 */     this.line_count = visible_line_count;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getMultiLine()
/*    */   {
/* 73 */     return this.line_count;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/StringParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */