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
/*    */ public class RequestedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public RequestedItem()
/*    */   {
/* 37 */     super("Requested", 3, -1, 20, "Pieces");
/* 38 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 42 */     info.addCategories(new String[] { "swarm" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 49 */     boolean value = false;
/* 50 */     PEPiece pePiece = (PEPiece)cell.getDataSource();
/* 51 */     if (pePiece != null)
/*    */     {
/* 53 */       value = pePiece.isRequested();
/*    */     }
/* 55 */     if ((!cell.setSortValue(value ? 1L : 0L)) && (cell.isValid()))
/* 56 */       return;
/* 57 */     cell.setText(value ? "*" : "");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/RequestedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */