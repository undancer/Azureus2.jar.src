/*    */ package org.gudy.azureus2.ui.swt.config.plugins;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.BooleanParameterImpl;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ColorParameter;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.DirectoryParameterImpl;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.FileParameter;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.IntParameterImpl;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.IntsParameter;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringListParameterImpl;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.StringParameterImpl;
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
/*    */ 
/*    */ public class PluginParameter
/*    */ {
/*    */   public PluginParameterImpl implementation;
/*    */   
/*    */   public PluginParameter(Composite pluginGroup, Parameter parameter)
/*    */   {
/* 46 */     if ((parameter instanceof StringParameterImpl)) {
/* 47 */       this.implementation = new PluginStringParameter(pluginGroup, (StringParameterImpl)parameter);
/* 48 */     } else if ((parameter instanceof IntParameterImpl)) {
/* 49 */       this.implementation = new PluginIntParameter(pluginGroup, (IntParameterImpl)parameter);
/* 50 */     } else if ((parameter instanceof BooleanParameterImpl)) {
/* 51 */       this.implementation = new PluginBooleanParameter(pluginGroup, (BooleanParameterImpl)parameter);
/* 52 */     } else if ((parameter instanceof FileParameter)) {
/* 53 */       this.implementation = new PluginFileParameter(pluginGroup, (FileParameter)parameter);
/* 54 */     } else if ((parameter instanceof DirectoryParameterImpl)) {
/* 55 */       this.implementation = new PluginDirectoryParameter(pluginGroup, (DirectoryParameterImpl)parameter);
/* 56 */     } else if ((parameter instanceof IntsParameter)) {
/* 57 */       this.implementation = new PluginIntsParameter(pluginGroup, (IntsParameter)parameter);
/* 58 */     } else if ((parameter instanceof StringListParameterImpl)) {
/* 59 */       this.implementation = new PluginStringsParameter(pluginGroup, (StringListParameterImpl)parameter);
/* 60 */     } else if ((parameter instanceof ColorParameter)) {
/* 61 */       this.implementation = new PluginColorParameter(pluginGroup, (ColorParameter)parameter);
/*    */     }
/*    */   }
/*    */   
/*    */   public Control[] getControls() {
/* 66 */     return this.implementation.getControls();
/*    */   }
/*    */   
/*    */   public void setAdditionalActionPerfomer(IAdditionalActionPerformer performer) {
/* 70 */     if ((this.implementation instanceof PluginBooleanParameter)) {
/* 71 */       ((PluginBooleanParameter)this.implementation).setAdditionalActionPerfomer(performer);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/plugins/PluginParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */