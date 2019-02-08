/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*    */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
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
/*    */ public class TrackerStatusItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellAddedListener, TableCellToolTipListener
/*    */ {
/* 40 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "tracker";
/*    */   
/*    */   public TrackerStatusItem(String sTableID)
/*    */   {
/* 45 */     super(DATASOURCE_TYPE, "tracker", 1, 90, sTableID);
/* 46 */     setRefreshInterval(15);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 50 */     info.addCategories(new String[] { "tracker" });
/*    */     
/*    */ 
/* 53 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void cellAdded(TableCell cell) {
/* 57 */     new Cell(cell);
/*    */   }
/*    */   
/*    */   private static class Cell extends AbstractTrackerCell {
/*    */     public Cell(TableCell cell) {
/* 62 */       super();
/*    */     }
/*    */     
/*    */     public void refresh(TableCell cell) {
/* 66 */       super.refresh(cell);
/*    */       
/* 68 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 69 */       String status = dm == null ? "" : dm.getTrackerStatus();
/*    */       
/*    */ 
/*    */ 
/*    */ 
/* 74 */       int nl_pos = status.indexOf('\n');
/* 75 */       if (nl_pos >= 0) {
/* 76 */         status = status.substring(0, nl_pos);
/*    */       }
/* 78 */       if ((cell.setText(status)) || (!cell.isValid())) {
/* 79 */         TrackerCellUtils.updateColor(cell, dm, true);
/*    */       }
/*    */     }
/*    */     
/*    */     public void scrapeResult(TRTrackerScraperResponse response) {
/* 84 */       checkScrapeResult(response);
/*    */     }
/*    */     
/*    */     public void announceResult(TRTrackerAnnouncerResponse response) {
/* 88 */       this.cell.invalidate();
/*    */     }
/*    */   }
/*    */   
/*    */   public void cellHover(TableCell cell) {
/* 93 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 94 */     cell.setToolTip(TrackerCellUtils.getTooltipText(cell, dm, true));
/*    */   }
/*    */   
/*    */   public void cellHoverComplete(TableCell cell) {
/* 98 */     cell.setToolTip(null);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TrackerStatusItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */