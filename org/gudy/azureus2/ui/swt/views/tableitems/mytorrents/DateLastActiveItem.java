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
/*    */ public class DateLastActiveItem
/*    */   extends ColumnDateSizer
/*    */ {
/* 33 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "DateTorrentLastActive";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 38 */     info.addCategories(new String[] { "time", "content" });
/* 39 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public DateLastActiveItem(String sTableID) {
/* 43 */     super(DATASOURCE_TYPE, "DateTorrentLastActive", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*    */     
/* 45 */     setMultiline(false);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell, long timestamp) {
/* 49 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 50 */     if (dm == null) {
/* 51 */       timestamp = 0L;
/*    */     } else {
/* 53 */       timestamp = dm.getDownloadState().getLongParameter("stats.download.last.active.time");
/* 54 */       if (timestamp == 0L) {
/* 55 */         timestamp = dm.getDownloadState().getLongParameter("stats.download.completed.time");
/*    */       }
/* 57 */       if (timestamp == 0L) {
/* 58 */         timestamp = dm.getDownloadState().getLongParameter("stats.download.added.time");
/*    */       }
/*    */     }
/* 61 */     super.refresh(cell, timestamp);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DateLastActiveItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */