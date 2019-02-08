/*    */ package org.gudy.azureus2.ui.swt.config.plugins;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
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
/*    */ public class PluginColorParameter
/*    */   implements PluginParameterImpl
/*    */ {
/*    */   Control[] controls;
/*    */   
/*    */   public PluginColorParameter(Composite pluginGroup, org.gudy.azureus2.pluginsimpl.local.ui.config.ColorParameter parameter)
/*    */   {
/* 40 */     this.controls = new Control[2];
/*    */     
/* 42 */     this.controls[0] = new Label(pluginGroup, 0);
/* 43 */     Messages.setLanguageText(this.controls[0], parameter.getLabelKey());
/*    */     
/* 45 */     org.gudy.azureus2.ui.swt.config.ColorParameter cp = new org.gudy.azureus2.ui.swt.config.ColorParameter(pluginGroup, parameter.getKey(), parameter.getDefaultRed(), parameter.getDefaultGreen(), parameter.getDefaultBlue());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 52 */     this.controls[1] = cp.getControl();
/* 53 */     new Label(pluginGroup, 0);
/*    */   }
/*    */   
/*    */   public Control[] getControls() {
/* 57 */     return this.controls;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/plugins/PluginColorParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */