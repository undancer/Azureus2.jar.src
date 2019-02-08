/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
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
/*    */ public class IntParameterImpl
/*    */   extends ParameterImpl
/*    */   implements IntParameter
/*    */ {
/*    */   private int defaultValue;
/*    */   private boolean limited;
/*    */   private int min_value;
/*    */   private int max_value;
/*    */   
/*    */   public IntParameterImpl(PluginConfigImpl config, String key, String label, int defaultValue)
/*    */   {
/* 43 */     super(config, key, label);
/* 44 */     config.notifyParamExists(getKey());
/* 45 */     COConfigurationManager.setIntDefault(getKey(), defaultValue);
/*    */     
/* 47 */     this.defaultValue = defaultValue;
/* 48 */     this.limited = false;
/*    */   }
/*    */   
/*    */   public IntParameterImpl(PluginConfigImpl config, String key, String label, int defaultValue, int min_value, int max_value)
/*    */   {
/* 53 */     this(config, key, label, defaultValue);
/* 54 */     this.min_value = min_value;
/* 55 */     this.max_value = max_value;
/* 56 */     this.limited = true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getDefaultValue()
/*    */   {
/* 65 */     return this.defaultValue;
/*    */   }
/*    */   
/*    */   public int getValue()
/*    */   {
/* 70 */     return this.config.getUnsafeIntParameter(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setValue(int b)
/*    */   {
/* 77 */     this.config.setUnsafeIntParameter(getKey(), b);
/*    */   }
/*    */   
/* 80 */   public boolean isLimited() { return this.limited; }
/* 81 */   public int getMinValue() { return this.min_value; }
/* 82 */   public int getMaxValue() { return this.max_value; }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/IntParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */