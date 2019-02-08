/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.pieces;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPiece;
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
/*    */ public class AvailabilityItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public AvailabilityItem()
/*    */   {
/* 37 */     super("availability", 2, -2, 80, "Pieces");
/* 38 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 42 */     info.addCategories(new String[] { "swarm" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 48 */     PEPiece piece = (PEPiece)cell.getDataSource();
/* 49 */     long value = piece == null ? 0L : piece.getAvailability();
/*    */     
/* 51 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 52 */       return;
/*    */     }
/*    */     
/* 55 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/AvailabilityItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */