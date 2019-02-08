/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.download.Download;
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
/*    */ public class UpSpeedLimitItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 42 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "maxupspeed";
/*    */   
/*    */ 
/*    */   public UpSpeedLimitItem(String sTableID)
/*    */   {
/* 49 */     super(DATASOURCE_TYPE, "maxupspeed", 2, 35, sTableID);
/* 50 */     setRefreshInterval(-2);
/* 51 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 55 */     info.addCategories(new String[] { "sharing", "settings" });
/*    */     
/*    */ 
/*    */ 
/* 59 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 63 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 64 */     long value = dm == null ? 0L : dm.getEffectiveUploadRateLimitBytesPerSecond();
/* 65 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 66 */       return;
/*    */     }
/* 68 */     if (value == -1L) {
/* 69 */       cell.setText(MessageText.getString("MyTorrents.items.UpSpeedLimit.disabled"));
/* 70 */     } else if (value == 0L) {
/* 71 */       cell.setText("∞");
/*    */     } else {
/* 73 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/UpSpeedLimitItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */