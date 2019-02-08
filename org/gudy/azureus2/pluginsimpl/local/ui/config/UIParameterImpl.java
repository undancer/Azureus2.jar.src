/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.config.UIParameter;
/*    */ import org.gudy.azureus2.plugins.ui.config.UIParameterContext;
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
/*    */ public class UIParameterImpl
/*    */   extends ParameterImpl
/*    */   implements UIParameter
/*    */ {
/*    */   private UIParameterContext context;
/*    */   
/*    */   public UIParameterImpl(PluginConfigImpl _config, UIParameterContext _context, String _key, String _label)
/*    */   {
/* 33 */     super(_config, _key, _label);
/* 34 */     this.context = _context;
/*    */   }
/*    */   
/*    */   public UIParameterContext getContext() {
/* 38 */     return this.context;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/UIParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */