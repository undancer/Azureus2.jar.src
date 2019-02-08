/*    */ package org.gudy.azureus2.ui.swt.config.plugins;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.BooleanParameterImpl;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*    */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
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
/*    */ public class PluginBooleanParameter
/*    */   implements PluginParameterImpl
/*    */ {
/*    */   Control[] controls;
/*    */   BooleanParameter booleanParameter;
/*    */   
/*    */   public PluginBooleanParameter(Composite pluginGroup, BooleanParameterImpl parameter)
/*    */   {
/* 42 */     this.controls = new Control[2];
/*    */     
/* 44 */     this.controls[0] = new Label(pluginGroup, 0);
/* 45 */     Messages.setLanguageText(this.controls[0], parameter.getLabelKey());
/*    */     
/* 47 */     this.booleanParameter = new BooleanParameter(pluginGroup, parameter.getKey(), parameter.getDefaultValue());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 52 */     this.controls[1] = this.booleanParameter.getControl();
/* 53 */     new Label(pluginGroup, 0);
/*    */   }
/*    */   
/*    */   public Control[] getControls() {
/* 57 */     return this.controls;
/*    */   }
/*    */   
/*    */   public void setAdditionalActionPerfomer(IAdditionalActionPerformer performer) {
/* 61 */     this.booleanParameter.setAdditionalActionPerformer(performer);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/plugins/PluginBooleanParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */