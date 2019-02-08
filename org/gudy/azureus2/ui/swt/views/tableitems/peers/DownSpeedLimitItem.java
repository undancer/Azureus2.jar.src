/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DownSpeedLimitItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "maxdownspeed";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 45 */     info.addCategories(new String[] { "bytes", "settings" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DownSpeedLimitItem(String table_id)
/*    */   {
/* 53 */     super("maxdownspeed", 2, -1, 35, table_id);
/* 54 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 58 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 59 */     long value = peer == null ? 0L : peer.getStats().getDownloadRateLimitBytesPerSecond();
/* 60 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 61 */       return;
/*    */     }
/* 63 */     if (value == -1L) {
/* 64 */       cell.setText(MessageText.getString("MyTorrents.items.DownSpeedLimit.disabled"));
/* 65 */     } else if (value == 0L) {
/* 66 */       cell.setText("∞");
/*    */     } else {
/* 68 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/DownSpeedLimitItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */