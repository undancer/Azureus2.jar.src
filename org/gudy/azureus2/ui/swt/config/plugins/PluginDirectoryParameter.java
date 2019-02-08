/*    */ package org.gudy.azureus2.ui.swt.config.plugins;
/*    */ 
/*    */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.widgets.Button;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.DirectoryDialog;
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.pluginsimpl.local.ui.config.DirectoryParameterImpl;
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
/*    */ public class PluginDirectoryParameter
/*    */   implements PluginParameterImpl
/*    */ {
/*    */   Control[] controls;
/*    */   
/*    */   public PluginDirectoryParameter(final Composite pluginGroup, DirectoryParameterImpl parameter)
/*    */   {
/* 44 */     this.controls = new Control[3];
/*    */     
/* 46 */     this.controls[0] = new Label(pluginGroup, 0);
/* 47 */     Messages.setLanguageText(this.controls[0], parameter.getLabelKey());
/*    */     
/* 49 */     final StringParameter sp = new StringParameter(pluginGroup, parameter.getKey(), parameter.getDefaultValue());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 54 */     this.controls[1] = sp.getControl();
/* 55 */     GridData gridData = new GridData(768);
/* 56 */     this.controls[1].setLayoutData(gridData);
/*    */     
/* 58 */     Button browse = new Button(pluginGroup, 8);
/* 59 */     ImageLoader.getInstance().setButtonImage(browse, "openFolderButton");
/* 60 */     browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*    */     
/* 62 */     browse.addListener(13, new Listener() {
/*    */       public void handleEvent(Event event) {
/* 64 */         DirectoryDialog dialog = new DirectoryDialog(pluginGroup.getShell(), 65536);
/* 65 */         dialog.setFilterPath(sp.getValue());
/* 66 */         String path = dialog.open();
/* 67 */         if (path != null) {
/* 68 */           sp.setValue(path);
/*    */         }
/*    */       }
/* 71 */     });
/* 72 */     this.controls[2] = browse;
/*    */   }
/*    */   
/*    */   public Control[] getControls() {
/* 76 */     return this.controls;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/plugins/PluginDirectoryParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */