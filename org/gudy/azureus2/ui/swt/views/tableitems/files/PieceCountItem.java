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
/*    */ public class PieceCountItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PieceCountItem()
/*    */   {
/* 36 */     super("numberofpieces", 2, -2, 75, "Files");
/* 37 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 41 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 44 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 48 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/* 49 */     long value = fileInfo == null ? 0L : fileInfo.getNbPieces();
/*    */     
/* 51 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 52 */       return;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 57 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/PieceCountItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */