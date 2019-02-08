/*    */ package org.gudy.azureus2.ui.swt.config.plugins;
/*    */ 
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.IntParameterImpl;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.config.IntParameter;
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
/*    */ public class PluginIntParameter
/*    */   implements PluginParameterImpl
/*    */ {
/*    */   Control[] controls;
/*    */   
/*    */   public PluginIntParameter(Composite pluginGroup, IntParameterImpl parameter)
/*    */   {
/* 42 */     this.controls = new Control[2];
/*    */     
/* 44 */     this.controls[0] = new Label(pluginGroup, 0);
/* 45 */     Messages.setLanguageText(this.controls[0], parameter.getLabelKey());
/*    */     
/* 47 */     IntParameter ip = new IntParameter(pluginGroup, parameter.getKey(), parameter.getDefaultValue());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 52 */     this.controls[1] = ip.getControl();
/* 53 */     GridData gridData = new GridData();
/* 54 */     gridData.widthHint = 100;
/* 55 */     Utils.setLayoutData(this.controls[1], gridData);
/* 56 */     new Label(pluginGroup, 0);
/*    */   }
/*    */   
/*    */   public Control[] getControls() {
/* 60 */     return this.controls;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/plugins/PluginIntParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */