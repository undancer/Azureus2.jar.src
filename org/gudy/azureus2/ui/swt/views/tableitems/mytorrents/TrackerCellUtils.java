/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.eclipse.swt.graphics.Color;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*    */ import org.gudy.azureus2.core3.tracker.client.impl.bt.TRTrackerBTScraperResponseImpl;
/*    */ import org.gudy.azureus2.core3.tracker.client.impl.bt.TrackerStatus;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*    */ public class TrackerCellUtils
/*    */ {
/* 43 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static void updateColor(TableCell cell, DownloadManager dm, boolean show_errors) {
/* 46 */     if ((dm == null) || (cell == null)) {
/* 47 */       return;
/*    */     }
/* 49 */     if ((show_errors) && 
/* 50 */       (dm.isTrackerError())) {
/* 51 */       cell.setForegroundToErrorColor();
/* 52 */       return;
/*    */     }
/*    */     
/* 55 */     TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
/* 56 */     if (((response instanceof TRTrackerBTScraperResponseImpl)) && (response.getStatus() == 2)) {
/* 57 */       boolean bMultiHashScrapes = ((TRTrackerBTScraperResponseImpl)response).getTrackerStatus().getSupportsMultipeHashScrapes();
/* 58 */       Color color = bMultiHashScrapes ? null : Colors.grey;
/* 59 */       cell.setForeground(Utils.colorToIntArray(color));
/*    */     } else {
/* 61 */       cell.setForeground(Utils.colorToIntArray(null));
/*    */     }
/*    */   }
/*    */   
/*    */   public static String getTooltipText(TableCell cell, DownloadManager dm, boolean show_errors) {
/* 66 */     if ((dm == null) || (cell == null)) {
/* 67 */       return null;
/*    */     }
/* 69 */     if ((show_errors) && 
/* 70 */       (dm.isTrackerError())) {
/* 71 */       return null;
/*    */     }
/*    */     
/* 74 */     String sToolTip = null;
/* 75 */     TRTrackerScraperResponse response = dm.getTrackerScrapeResponse();
/* 76 */     if (((response instanceof TRTrackerBTScraperResponseImpl)) && (response.getStatus() == 2)) {
/* 77 */       String sPrefix = ((TRTrackerBTScraperResponseImpl)response).getTrackerStatus().getSupportsMultipeHashScrapes() ? "" : "No";
/*    */       
/* 79 */       sToolTip = MessageText.getString("Tracker.tooltip." + sPrefix + "MultiSupport");
/*    */     }
/*    */     
/* 82 */     return sToolTip;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TrackerCellUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */