/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ 
/*    */ public class DoneItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public DoneItem()
/*    */   {
/* 38 */     super("done", 2, -2, 70, "Files");
/* 39 */     setRefreshInterval(-2);
/* 40 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 44 */     info.addCategories(new String[] { "bytes", "progress" });
/*    */     
/*    */ 
/*    */ 
/* 48 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 52 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/* 53 */     long value = fileInfo == null ? 0L : fileInfo.getDownloaded();
/*    */     
/* 55 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 56 */       return;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 61 */     cell.setText(value < 0L ? "" : DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/DoneItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */