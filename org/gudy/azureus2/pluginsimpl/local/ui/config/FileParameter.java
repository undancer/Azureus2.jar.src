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
/*    */ public class FileParameter
/*    */   extends ParameterImpl
/*    */   implements org.gudy.azureus2.plugins.ui.config.FileParameter
/*    */ {
/*    */   private String defaultValue;
/*    */   private String[] file_extensions;
/*    */   
/*    */   public FileParameter(PluginConfigImpl config, String key, String label, String defaultValue)
/*    */   {
/* 40 */     this(config, key, label, defaultValue, null);
/*    */   }
/*    */   
/*    */   public FileParameter(PluginConfigImpl config, String key, String label, String defaultValue, String[] file_extensions) {
/* 44 */     super(config, key, label);
/*    */     
/* 46 */     this.defaultValue = defaultValue;
/* 47 */     this.file_extensions = file_extensions;
/* 48 */     config.notifyParamExists(getKey());
/* 49 */     COConfigurationManager.setStringDefault(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getDefaultValue()
/*    */   {
/* 57 */     return this.defaultValue;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 61 */     return this.config.getUnsafeStringParameter(getKey(), getDefaultValue());
/*    */   }
/*    */   
/*    */   public String[] getFileExtensions() {
/* 65 */     return this.file_extensions;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/FileParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */