/*    */ package org.gudy.azureus2.pluginsimpl.local;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.PluginException;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.UnloadablePlugin;
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
/*    */ public class FailedPlugin
/*    */   implements UnloadablePlugin
/*    */ {
/*    */   protected String plugin_name;
/*    */   protected String plugin_dir;
/*    */   protected PluginInterfaceImpl plugin_interface;
/*    */   
/*    */   public FailedPlugin()
/*    */   {
/* 37 */     this.plugin_name = null;
/* 38 */     this.plugin_dir = null;
/*    */   }
/*    */   
/*    */   public FailedPlugin(String _name, String _target_dir) {
/* 42 */     this.plugin_name = _name;
/* 43 */     this.plugin_dir = _target_dir;
/*    */   }
/*    */   
/*    */   public void initialize(PluginInterface pi) throws PluginException {
/* 47 */     this.plugin_interface = ((PluginInterfaceImpl)pi);
/*    */     
/* 49 */     this.plugin_interface.setPluginVersion("0.0");
/*    */     
/* 51 */     if (this.plugin_name == null) {
/* 52 */       this.plugin_interface.setPluginName(this.plugin_interface.getPluginID());
/*    */     } else {
/* 54 */       this.plugin_interface.setPluginName(this.plugin_name);
/*    */     }
/* 56 */     if (this.plugin_dir != null) {
/* 57 */       this.plugin_interface.setPluginDirectoryName(this.plugin_dir);
/*    */     }
/*    */   }
/*    */   
/*    */   public void unload() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/FailedPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */