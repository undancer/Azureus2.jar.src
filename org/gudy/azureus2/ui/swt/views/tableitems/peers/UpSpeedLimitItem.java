/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
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
/*    */ public class UpSpeedLimitItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "maxupspeed";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 44 */     info.addCategories(new String[] { "settings" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public UpSpeedLimitItem(String table_id)
/*    */   {
/* 51 */     super("maxupspeed", 2, -1, 35, table_id);
/* 52 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 56 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 57 */     long value = peer == null ? 0L : peer.getUploadRateLimitBytesPerSecond();
/* 58 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 59 */       return;
/*    */     }
/* 61 */     if (value == -1L) {
/* 62 */       cell.setText(MessageText.getString("MyTorrents.items.UpSpeedLimit.disabled"));
/* 63 */     } else if (value == 0L) {
/* 64 */       cell.setText("∞");
/*    */     } else {
/* 66 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/UpSpeedLimitItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */