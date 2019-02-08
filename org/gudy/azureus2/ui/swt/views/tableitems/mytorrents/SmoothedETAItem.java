/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*    */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
/*    */ import org.gudy.azureus2.ui.swt.views.ViewUtils;
/*    */ import org.gudy.azureus2.ui.swt.views.ViewUtils.CustomDateFormat;
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
/*    */ public class SmoothedETAItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 45 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*    */   
/*    */   public static final String COLUMN_ID = "smootheta";
/*    */   private ViewUtils.CustomDateFormat cdf;
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 52 */     info.addCategories(new String[] { "essential" });
/* 53 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public SmoothedETAItem(String sTableID)
/*    */   {
/* 58 */     super(DATASOURCE_TYPE, "smootheta", 2, 60, sTableID);
/* 59 */     setRefreshInterval(-2);
/* 60 */     addDataSourceType(DiskManagerFileInfo.class);
/* 61 */     this.cdf = ViewUtils.addCustomDateFormat(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 65 */     Object ds = cell.getDataSource();
/*    */     
/* 67 */     if ((ds instanceof DiskManagerFileInfo)) {
/* 68 */       DiskManagerFileInfo file = (DiskManagerFileInfo)cell.getDataSource();
/* 69 */       long value = file.getETA();
/*    */       
/* 71 */       if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 72 */         return;
/*    */       }
/*    */       
/* 75 */       cell.setText(ViewUtils.formatETA(value, MyTorrentsView.eta_absolute, this.cdf.getDateFormat()));
/*    */     } else {
/* 77 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 78 */       long value = dm == null ? 0L : dm.getStats().getSmoothedETA();
/*    */       
/* 80 */       if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 81 */         return;
/*    */       }
/*    */       
/* 84 */       cell.setText(ViewUtils.formatETA(value, MyTorrentsView.eta_absolute, this.cdf.getDateFormat()));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void postConfigLoad()
/*    */   {
/* 91 */     super.postConfigLoad();
/*    */     
/* 93 */     this.cdf.update();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SmoothedETAItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */