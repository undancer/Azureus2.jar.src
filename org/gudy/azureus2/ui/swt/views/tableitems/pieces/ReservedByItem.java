/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.pieces;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPiece;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*    */ public class ReservedByItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public ReservedByItem()
/*    */   {
/* 36 */     super("reservedby", 2, -1, 80, "Pieces");
/* 37 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 41 */     PEPiece piece = (PEPiece)cell.getDataSource();
/* 42 */     String reservedBy = piece == null ? null : piece.getReservedBy();
/* 43 */     String value = reservedBy == null ? "---" : reservedBy;
/* 44 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 45 */       return;
/*    */     }
/*    */     
/* 48 */     cell.setText(value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/ReservedByItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */