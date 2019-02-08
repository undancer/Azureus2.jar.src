/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
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
/*    */ public class LatencyItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "latency";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 38 */     info.addCategories(new String[] { "time" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public LatencyItem(String table_id)
/*    */   {
/* 45 */     super("latency", 2, -1, 70, table_id);
/* 46 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 50 */     PEPeerTransport peer = (PEPeerTransport)cell.getDataSource();
/*    */     
/* 52 */     long value = peer == null ? 0L : peer.getLatency();
/*    */     
/* 54 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 55 */       return;
/*    */     }
/*    */     
/* 58 */     cell.setText(value == 0L ? "" : TimeFormatter.format100ths(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/LatencyItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */