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
/*    */ public class FirstPieceItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public FirstPieceItem()
/*    */   {
/* 36 */     super("firstpiece", 2, -2, 75, "Files");
/* 37 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 41 */     info.addCategories(new String[] { "protocol" });
/*    */     
/*    */ 
/* 44 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 48 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*    */     long sort_value;
/*    */     long sort_value;
/* 51 */     if (fileInfo == null) {
/* 52 */       sort_value = 0L;
/*    */     } else {
/* 54 */       sort_value = fileInfo.getFirstPieceNumber();
/*    */       
/* 56 */       if (sort_value >= 0L)
/*    */       {
/* 58 */         sort_value = (sort_value << 32) + fileInfo.getIndex();
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 63 */     if ((!cell.setSortValue(sort_value)) && (cell.isValid())) {
/* 64 */       return;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 69 */     cell.setText("" + fileInfo.getFirstPieceNumber());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/FirstPieceItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */