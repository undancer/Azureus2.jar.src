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
/*    */ public class ColorParameter
/*    */   extends ParameterImpl
/*    */ {
/*    */   private int defaultRed;
/*    */   private int defaultGreen;
/*    */   private int defaultBlue;
/*    */   
/*    */   public ColorParameter(PluginConfigImpl config, String key, String label, int red, int green, int blue)
/*    */   {
/* 40 */     super(config, key, label);
/* 41 */     this.defaultRed = red;
/* 42 */     this.defaultGreen = green;
/* 43 */     this.defaultBlue = blue;
/*    */     
/* 45 */     config.notifyRGBParamExists(getKey());
/* 46 */     COConfigurationManager.setIntDefault(getKey() + ".red", getDefaultRed());
/* 47 */     COConfigurationManager.setIntDefault(getKey() + ".green", getDefaultGreen());
/* 48 */     COConfigurationManager.setIntDefault(getKey() + ".blue", getDefaultBlue());
/*    */   }
/*    */   
/*    */   public int getDefaultRed()
/*    */   {
/* 53 */     return this.defaultRed;
/*    */   }
/*    */   
/*    */   public int getDefaultGreen()
/*    */   {
/* 58 */     return this.defaultGreen;
/*    */   }
/*    */   
/*    */   public int getDefaultBlue()
/*    */   {
/* 63 */     return this.defaultBlue;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ColorParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */