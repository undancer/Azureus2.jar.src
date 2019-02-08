/*    */ package org.gudy.azureus2.ui.swt.config.plugins;
/*    */ 
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringListParameterImpl;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
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
/*    */ public class PluginStringsParameter
/*    */   implements PluginParameterImpl
/*    */ {
/*    */   Control[] controls;
/*    */   
/*    */   public PluginStringsParameter(Composite pluginGroup, StringListParameterImpl parameter)
/*    */   {
/* 42 */     this.controls = new Control[2];
/*    */     
/* 44 */     this.controls[0] = new Label(pluginGroup, 0);
/* 45 */     Messages.setLanguageText(this.controls[0], parameter.getLabelKey());
/*    */     
/* 47 */     StringListParameter slp = new StringListParameter(pluginGroup, parameter.getKey(), parameter.getDefaultValue(), parameter.getLabels(), parameter.getValues());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 54 */     this.controls[1] = slp.getControl();
/* 55 */     GridData gridData = new GridData();
/* 56 */     gridData.widthHint = 100;
/* 57 */     Utils.setLayoutData(this.controls[1], gridData);
/* 58 */     new Label(pluginGroup, 0);
/*    */   }
/*    */   
/*    */   public Control[] getControls() {
/* 62 */     return this.controls;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/plugins/PluginStringsParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */