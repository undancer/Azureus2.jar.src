/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
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
/*    */ 
/*    */ 
/*    */ public class ColumnDoneWithDND
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 45 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "donewithdnd";
/*    */   
/*    */   public ColumnDoneWithDND(String sTableID)
/*    */   {
/* 51 */     super(DATASOURCE_TYPE, "donewithdnd", 2, 55, sTableID);
/* 52 */     addDataSourceType(DiskManagerFileInfo.class);
/* 53 */     setRefreshInterval(-2);
/* 54 */     setPosition(-1);
/* 55 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 59 */     info.addCategories(new String[] { "progress" });
/* 60 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 65 */     Object ds = cell.getDataSource();
/* 66 */     int value; if ((ds instanceof DownloadManager))
/*    */     {
/* 68 */       DownloadManager dm = (DownloadManager)ds;
/* 69 */       DownloadManagerStats stats = dm.getStats();
/* 70 */       value = stats.getDownloadCompleted(true); } else { int value;
/* 71 */       if ((ds instanceof DiskManagerFileInfo)) {
/* 72 */         DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 73 */         long length = fileInfo.getLength();
/* 74 */         int value; if (length == 0L) {
/* 75 */           value = 1000;
/*    */         } else
/* 77 */           value = (int)(fileInfo.getDownloaded() * 1000L / length);
/*    */       } else { return;
/*    */       }
/*    */     }
/*    */     int value;
/* 82 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/* 83 */       return;
/* 84 */     cell.setText(DisplayFormatters.formatPercentFromThousands(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/ColumnDoneWithDND.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */