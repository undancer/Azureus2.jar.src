/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*    */ public class FileExtensionItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public FileExtensionItem()
/*    */   {
/* 35 */     super("fileext", 1, -1, 50, "Files");
/* 36 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 40 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 43 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 47 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/* 48 */     cell.setText(determineFileExt(fileInfo));
/*    */   }
/*    */   
/*    */   private static String determineFileExt(DiskManagerFileInfo fileInfo) {
/* 52 */     String name = fileInfo == null ? "" : fileInfo.getFile(true).getName();
/*    */     
/* 54 */     DownloadManager dm = fileInfo == null ? null : fileInfo.getDownloadManager();
/*    */     
/* 56 */     String incomp_suffix = dm == null ? null : dm.getDownloadState().getAttribute("incompfilesuffix");
/*    */     
/* 58 */     if ((incomp_suffix != null) && (name.endsWith(incomp_suffix)))
/*    */     {
/* 60 */       name = name.substring(0, name.length() - incomp_suffix.length());
/*    */     }
/*    */     
/* 63 */     int dot_position = name.lastIndexOf(".");
/* 64 */     if (dot_position == -1) { return "";
/*    */     }
/*    */     
/* 67 */     return name.substring(dot_position + 1);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/FileExtensionItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */