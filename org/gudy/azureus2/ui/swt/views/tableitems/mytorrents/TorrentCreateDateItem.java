/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
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
/*    */ 
/*    */ public class TorrentCreateDateItem
/*    */   extends ColumnDateSizer
/*    */ {
/* 33 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "torrent_created";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 38 */     info.addCategories(new String[] { "time", "content" });
/* 39 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public TorrentCreateDateItem(String sTableID) {
/* 43 */     super(DATASOURCE_TYPE, "torrent_created", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*    */     
/* 45 */     setMultiline(false);
/*    */     
/* 47 */     setRefreshInterval(-3);
/*    */     
/* 49 */     setShowTime(false);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public TorrentCreateDateItem(String tableID, boolean v)
/*    */   {
/* 57 */     this(tableID);
/* 58 */     setVisible(v);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell, long timestamp) {
/* 62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 63 */     timestamp = dm == null ? 0L : dm.getTorrentCreationDate();
/* 64 */     super.refresh(cell, timestamp * 1000L);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TorrentCreateDateItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */