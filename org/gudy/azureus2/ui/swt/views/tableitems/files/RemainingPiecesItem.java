/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManager;
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
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
/*    */ public class RemainingPiecesItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public RemainingPiecesItem()
/*    */   {
/* 38 */     super("remaining", 2, -2, 60, "Files");
/* 39 */     setRefreshInterval(-2);
/* 40 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 44 */     info.addCategories(new String[] { "progress" });
/*    */     
/*    */ 
/* 47 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 51 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*    */     
/*    */ 
/*    */ 
/* 55 */     DiskManager dm = fileInfo == null ? null : fileInfo.getDiskManager();
/*    */     
/* 57 */     int remaining = 0;
/*    */     
/* 59 */     if ((fileInfo != null) && (dm != null)) {
/* 60 */       int start = fileInfo.getFirstPieceNumber();
/* 61 */       int end = start + fileInfo.getNbPieces();
/* 62 */       DiskManagerPiece[] pieces = dm.getPieces();
/* 63 */       for (int i = start; i < end; i++) {
/* 64 */         if (!pieces[i].isDone()) remaining++;
/*    */       }
/*    */     }
/*    */     else {
/* 68 */       remaining = -1;
/*    */     }
/*    */     
/* 71 */     if ((!cell.setSortValue(remaining)) && (cell.isValid())) {
/* 72 */       return;
/*    */     }
/*    */     
/* 75 */     cell.setText("" + (remaining < 0 ? "" : new StringBuilder().append("").append(remaining).toString()));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/RemainingPiecesItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */