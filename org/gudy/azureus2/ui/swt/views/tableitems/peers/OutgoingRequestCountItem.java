/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OutgoingRequestCountItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "outgoingreqcount";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 46 */     info.addCategories(new String[] { "protocol" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public OutgoingRequestCountItem(String table_id)
/*    */   {
/* 53 */     super("outgoingreqcount", 2, -1, 60, table_id);
/* 54 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 58 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 59 */     long value = peer == null ? 0L : peer.getOutgoingRequestCount();
/*    */     
/* 61 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 62 */       return;
/*    */     }
/* 64 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/OutgoingRequestCountItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */