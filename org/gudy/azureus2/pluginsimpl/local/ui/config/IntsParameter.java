/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public class IntsParameter
/*    */   extends ParameterImpl
/*    */ {
/*    */   private int defaultValue;
/*    */   private int[] values;
/*    */   private String[] labels;
/*    */   
/*    */   public IntsParameter(PluginConfigImpl config, String key, String label, int defaultValue, int[] values, String[] labels)
/*    */   {
/* 48 */     super(config, key, label);
/* 49 */     this.defaultValue = defaultValue;
/* 50 */     this.values = values;
/* 51 */     this.labels = labels;
/*    */     
/* 53 */     COConfigurationManager.setIntDefault(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */ 
/*    */   public int getDefaultValue()
/*    */   {
/* 59 */     return this.defaultValue;
/*    */   }
/*    */   
/*    */   public int[] getValues()
/*    */   {
/* 64 */     return this.values;
/*    */   }
/*    */   
/*    */   public String[] getLabels()
/*    */   {
/* 69 */     return this.labels;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/IntsParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */