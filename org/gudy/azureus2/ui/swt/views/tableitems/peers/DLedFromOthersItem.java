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
/*    */ 
/*    */ public class DLedFromOthersItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "DLedFromOthers";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 41 */     info.addCategories(new String[] { "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DLedFromOthersItem(String table_id)
/*    */   {
/* 48 */     super("DLedFromOthers", 2, -1, 70, table_id);
/* 49 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 53 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 54 */     long value = peer == null ? 0L : peer.getStats().getTotalBytesDownloadedByPeer() - peer.getStats().getTotalDataBytesSent();
/*    */     
/* 56 */     if (value < 0L) { value = 0L;
/*    */     }
/* 58 */     if (peer != null) {
/* 59 */       Long prev_value = (Long)peer.getData("DLedFromOther_prev");
/*    */       
/* 61 */       if (prev_value != null) {
/* 62 */         if (value < prev_value.longValue()) {
/* 63 */           value = prev_value.longValue();
/*    */         }
/* 65 */         else if (value > prev_value.longValue()) {
/* 66 */           peer.setData("DLedFromOther_prev", new Long(value));
/*    */         }
/*    */       }
/*    */       else {
/* 70 */         peer.setData("DLedFromOther_prev", new Long(value));
/*    */       }
/*    */     }
/*    */     
/* 74 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 75 */       return;
/*    */     }
/* 77 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/DLedFromOthersItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */