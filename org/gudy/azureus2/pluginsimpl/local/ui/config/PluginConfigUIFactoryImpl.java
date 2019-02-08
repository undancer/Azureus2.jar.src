/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.ui.config.EnablerParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginConfigImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PluginConfigUIFactoryImpl
/*     */   implements PluginConfigUIFactory
/*     */ {
/*     */   String pluginKey;
/*     */   PluginConfigImpl config;
/*     */   
/*     */   public PluginConfigUIFactoryImpl(PluginConfigImpl _config, String _pluginKey)
/*     */   {
/*  46 */     this.config = _config;
/*  47 */     this.pluginKey = _pluginKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Parameter createIntParameter(String key, String label, int defaultValue, int[] values, String[] labels)
/*     */   {
/*  57 */     return new IntsParameter(this.config, this.pluginKey + "." + key, label, defaultValue, values, labels);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public EnablerParameter createBooleanParameter(String key, String label, boolean defaultValue)
/*     */   {
/*  65 */     return new BooleanParameterImpl(this.config, this.pluginKey + "." + key, label, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Parameter createIntParameter(String key, String label, int defaultValue)
/*     */   {
/*  73 */     return new IntParameterImpl(this.config, this.pluginKey + "." + key, label, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Parameter createStringParameter(String key, String label, String defaultValue)
/*     */   {
/*  82 */     return new StringParameterImpl(this.config, this.pluginKey + "." + key, label, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Parameter createStringParameter(String key, String label, String defaultValue, String[] values, String[] labels)
/*     */   {
/*  91 */     return new StringListParameterImpl(this.config, this.pluginKey + "." + key, label, defaultValue, values, labels);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Parameter createFileParameter(String key, String label, String defaultValue)
/*     */   {
/*  99 */     return new FileParameter(this.config, this.pluginKey + "." + key, label, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Parameter createDirectoryParameter(String key, String label, String defaultValue)
/*     */   {
/* 106 */     return new DirectoryParameterImpl(this.config, this.pluginKey + "." + key, label, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Parameter createColorParameter(String key, String label, int defaultValueRed, int defaultValueGreen, int defaultValueBlue)
/*     */   {
/* 116 */     return new ColorParameter(this.config, this.pluginKey + "." + key, label, defaultValueRed, defaultValueGreen, defaultValueBlue);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/PluginConfigUIFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */