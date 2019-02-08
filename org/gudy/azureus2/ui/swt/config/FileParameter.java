/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.FileDialog;
/*    */ import org.eclipse.swt.widgets.Shell;
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
/*    */ 
/*    */ 
/*    */ public class FileParameter
/*    */   extends DirectoryParameter
/*    */ {
/*    */   protected String[] extension_list;
/*    */   
/*    */   public FileParameter(Composite pluginGroup, String name, String defaultValue, String[] extension_list)
/*    */   {
/* 41 */     super(pluginGroup, name, defaultValue);
/* 42 */     this.extension_list = extension_list;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected String getBrowseImageResource()
/*    */   {
/* 49 */     return "openFolderButton";
/*    */   }
/*    */   
/*    */   protected String openDialog(Shell shell, String old_value) {
/* 53 */     FileDialog dialog = new FileDialog(shell, 65536);
/* 54 */     dialog.setFilterPath(old_value);
/* 55 */     if (this.extension_list != null) {
/* 56 */       dialog.setFilterExtensions(this.extension_list);
/*    */     }
/* 58 */     return dialog.open();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/FileParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */