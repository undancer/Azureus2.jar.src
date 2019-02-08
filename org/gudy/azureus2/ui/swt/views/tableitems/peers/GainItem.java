/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerStats;
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
/*    */ public class GainItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "gain";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 40 */     info.addCategories(new String[] { "bytes", "sharing" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public GainItem(String table_id)
/*    */   {
/* 48 */     super("gain", 2, -1, 70, table_id);
/* 49 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 53 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 54 */     long value = peer == null ? 0L : peer.getStats().getTotalDataBytesReceived() - peer.getStats().getTotalDataBytesSent();
/*    */     
/* 56 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 57 */       return;
/*    */     }
/* 59 */     cell.setText((value >= 0L ? "" : "-") + DisplayFormatters.formatByteCountToKiBEtc(Math.abs(value)));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/GainItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */