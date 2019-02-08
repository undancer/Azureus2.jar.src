/*    */ package org.gudy.azureus2.ui.swt.config.plugins;
/*    */ 
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringParameterImpl;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.config.StringParameter;
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
/*    */ public class PluginStringParameter
/*    */   implements PluginParameterImpl
/*    */ {
/*    */   Control[] controls;
/*    */   
/*    */   public PluginStringParameter(Composite pluginGroup, StringParameterImpl parameter)
/*    */   {
/* 41 */     this.controls = new Control[2];
/*    */     
/* 43 */     this.controls[0] = new Label(pluginGroup, 0);
/* 44 */     Messages.setLanguageText(this.controls[0], parameter.getLabelKey());
/*    */     
/* 46 */     StringParameter sp = new StringParameter(pluginGroup, parameter.getKey(), parameter.getDefaultValue());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 51 */     this.controls[1] = sp.getControl();
/* 52 */     GridData gridData = new GridData(768);
/* 53 */     this.controls[1].setLayoutData(gridData);
/* 54 */     new Label(pluginGroup, 0);
/*    */   }
/*    */   
/*    */   public Control[] getControls() {
/* 58 */     return this.controls;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/plugins/PluginStringParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */