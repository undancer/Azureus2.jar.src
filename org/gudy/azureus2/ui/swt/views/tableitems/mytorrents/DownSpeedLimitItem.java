/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
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
/*    */ public class DownSpeedLimitItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 43 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*    */   
/*    */   public static final String COLUMN_ID = "maxdownspeed";
/*    */   
/*    */   public DownSpeedLimitItem(String sTableID)
/*    */   {
/* 49 */     super(DATASOURCE_TYPE, "maxdownspeed", 2, 35, sTableID);
/* 50 */     setRefreshInterval(-2);
/* 51 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 55 */     info.addCategories(new String[] { "settings" });
/*    */     
/*    */ 
/* 58 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 63 */     long value = dm == null ? 0L : dm.getStats().getDownloadRateLimitBytesPerSecond();
/* 64 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 65 */       return;
/*    */     }
/* 67 */     if (value == -1L) {
/* 68 */       cell.setText(MessageText.getString("MyTorrents.items.DownSpeedLimit.disabled"));
/* 69 */     } else if (value == 0L) {
/* 70 */       cell.setText("∞");
/*    */     } else {
/* 72 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DownSpeedLimitItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */