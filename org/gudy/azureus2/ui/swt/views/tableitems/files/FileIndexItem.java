/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileIndexItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public FileIndexItem()
/*    */   {
/* 36 */     super("torrentfileindex", 2, -1, 40, "Files");
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
/* 48 */     int index = fileInfo == null ? -1 : fileInfo.getIndex();
/*    */     
/* 50 */     if (cell.setSortValue(index)) {
/* 51 */       cell.setText(index == -1 ? "" : String.valueOf(index));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/FileIndexItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */