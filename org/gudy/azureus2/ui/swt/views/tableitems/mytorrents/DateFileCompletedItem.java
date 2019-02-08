/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*    */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*    */ public class DateFileCompletedItem
/*    */   extends ColumnDateSizer
/*    */ {
/* 33 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "DateFileCompleted";
/*    */   
/*    */   public DateFileCompletedItem(String sTableID)
/*    */   {
/* 38 */     super(DATASOURCE_TYPE, "DateFileCompleted", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/* 39 */     setRefreshInterval(-2);
/* 40 */     setMultiline(false);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 44 */     info.addCategories(new String[] { "time", "content" });
/*    */     
/*    */ 
/*    */ 
/* 48 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DateFileCompletedItem(String tableID, boolean v)
/*    */   {
/* 56 */     this(tableID);
/* 57 */     setVisible(v);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell, long timestamp) {
/* 61 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 62 */     long value = 0L;
/* 63 */     if (dm == null) {
/* 64 */       return;
/*    */     }
/*    */     
/* 67 */     value = dm.getDownloadState().getLongParameter("stats.download.file.completed.time");
/*    */     
/* 69 */     if (value <= 0L)
/*    */     {
/*    */ 
/*    */ 
/* 73 */       value = dm.getDownloadState().getLongParameter("stats.download.completed.time");
/*    */     }
/*    */     
/* 76 */     super.refresh(cell, value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DateFileCompletedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */