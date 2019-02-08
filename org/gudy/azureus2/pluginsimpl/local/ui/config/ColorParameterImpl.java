/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.plugins.ui.config.ColorParameter;
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
/*    */ public class ColorParameterImpl
/*    */   extends ParameterImpl
/*    */   implements ColorParameter
/*    */ {
/*    */   private int r;
/*    */   private int g;
/*    */   private int b;
/*    */   private final int orig_r;
/*    */   private final int orig_g;
/*    */   private final int orig_b;
/*    */   
/*    */   public ColorParameterImpl(PluginConfigImpl config, String key, String label, int _r, int _g, int _b)
/*    */   {
/* 39 */     super(config, key, label);
/*    */     
/* 41 */     config.notifyRGBParamExists(getKey());
/* 42 */     COConfigurationManager.setIntDefault(getKey() + ".red", this.r);
/* 43 */     COConfigurationManager.setIntDefault(getKey() + ".green", this.g);
/* 44 */     COConfigurationManager.setIntDefault(getKey() + ".blue", this.b);
/* 45 */     COConfigurationManager.setBooleanDefault(getKey() + ".override", false);
/*    */     
/* 47 */     this.orig_r = (this.r = _r);
/* 48 */     this.orig_g = (this.g = _g);
/* 49 */     this.orig_b = (this.b = _b);
/*    */   }
/*    */   
/* 52 */   public int getRedValue() { return this.r; }
/* 53 */   public int getGreenValue() { return this.g; }
/* 54 */   public int getBlueValue() { return this.b; }
/*    */   
/*    */   public void reloadParamDataFromConfig(boolean override) {
/* 57 */     int[] rgb = this.config.getUnsafeColorParameter(getKey());
/* 58 */     this.r = rgb[0];
/* 59 */     this.g = rgb[1];
/* 60 */     this.b = rgb[2];
/* 61 */     this.config.setUnsafeBooleanParameter(getKey() + ".override", override);
/*    */   }
/*    */   
/*    */   public void setRGBValue(int r, int g, int b) {
/* 65 */     this.r = r;this.g = g;this.b = b;
/* 66 */     this.config.setUnsafeColorParameter(getKey(), new int[] { r, g, b }, true);
/*    */   }
/*    */   
/*    */   public void resetToDefault() {
/* 70 */     this.config.setUnsafeColorParameter(getKey(), new int[] { this.orig_r, this.orig_g, this.orig_b }, false);
/*    */   }
/*    */   
/*    */   public boolean isOverridden() {
/* 74 */     return this.config.getUnsafeBooleanParameter(getKey() + ".override");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ColorParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */