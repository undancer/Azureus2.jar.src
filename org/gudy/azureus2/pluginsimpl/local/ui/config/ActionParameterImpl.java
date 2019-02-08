/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
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
/*    */ public class ActionParameterImpl
/*    */   extends ParameterImpl
/*    */   implements ActionParameter
/*    */ {
/*    */   private String action_resource;
/* 36 */   private int style = 1;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ActionParameterImpl(PluginConfigImpl config, String label_resource_name, String action_resource_name)
/*    */   {
/* 44 */     super(config, label_resource_name, label_resource_name);
/*    */     
/* 46 */     this.action_resource = action_resource_name;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getActionResource()
/*    */   {
/* 52 */     return this.action_resource;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setStyle(int _style)
/*    */   {
/* 59 */     this.style = _style;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getStyle()
/*    */   {
/* 65 */     return this.style;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ActionParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */