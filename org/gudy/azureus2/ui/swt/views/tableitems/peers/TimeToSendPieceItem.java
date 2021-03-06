/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.util.TimeFormatter;
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
/*    */ public class TimeToSendPieceItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public TimeToSendPieceItem(String table_id)
/*    */   {
/* 42 */     super("timetosend", 2, -1, 70, table_id);
/* 43 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 47 */     info.addCategories(new String[] { "time" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 53 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 54 */     long value = peer == null ? 0L : peer.getUploadHint();
/*    */     
/* 56 */     Comparable sortValue = cell.getSortValue();
/* 57 */     long oldValue = 0L;
/* 58 */     if ((sortValue instanceof Number)) {
/* 59 */       oldValue = ((Number)sortValue).longValue();
/*    */     }
/*    */     
/* 62 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 63 */       return;
/*    */     }
/* 65 */     String text = TimeFormatter.format(value / 1000L);
/* 66 */     if (oldValue > 0L) {
/* 67 */       text = text + ", " + TimeFormatter.format(oldValue / 1000L);
/*    */     }
/* 69 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/TimeToSendPieceItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */