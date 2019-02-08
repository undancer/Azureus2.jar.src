/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public class ETAItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 46 */   public static final Class<?> DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*    */   
/*    */   public static final String COLUMN_ID = "eta";
/*    */   private ViewUtils.CustomDateFormat cdf;
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 53 */     info.addCategories(new String[] { "essential" });
/* 54 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ETAItem(String sTableID)
/*    */   {
/* 59 */     super(DATASOURCE_TYPE, "eta", 2, 60, sTableID);
/* 60 */     setRefreshInterval(-2);
/*    */     
/* 62 */     this.cdf = ViewUtils.addCustomDateFormat(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 66 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 67 */     long value = dm == null ? 0L : dm.getStats().getETA();
/* 68 */     Long sortVal = value < 0L ? null : Long.valueOf(value);
/*    */     
/* 70 */     if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/* 71 */       return;
/*    */     }
/*    */     
/* 74 */     cell.setText(ViewUtils.formatETA(value, MyTorrentsView.eta_absolute, this.cdf.getDateFormat()));
/*    */   }
/*    */   
/*    */ 
/*    */   public void postConfigLoad()
/*    */   {
/* 80 */     super.postConfigLoad();
/*    */     
/* 82 */     this.cdf.update();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/ETAItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */