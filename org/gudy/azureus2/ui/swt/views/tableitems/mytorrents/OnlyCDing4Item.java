/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*    */ import org.gudy.azureus2.core3.util.TimeFormatter;
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
/*    */ 
/*    */ 
/*    */ public class OnlyCDing4Item
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 44 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "OnlyCDing4";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 49 */     info.addCategories(new String[] { "sharing" });
/* 50 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public OnlyCDing4Item(String sTableID)
/*    */   {
/* 55 */     super(DATASOURCE_TYPE, "OnlyCDing4", 2, 70, sTableID);
/* 56 */     setRefreshInterval(-2);
/* 57 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 61 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 62 */     long value = dm == null ? 0L : dm.getStats().getSecondsOnlySeeding();
/*    */     
/* 64 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 65 */       return;
/*    */     }
/* 67 */     cell.setText(TimeFormatter.format(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/OnlyCDing4Item.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */