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
/*    */ public class DoneItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 45 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "done";
/*    */   
/*    */   public DoneItem(String sTableID)
/*    */   {
/* 51 */     super(DATASOURCE_TYPE, "done", 2, 55, sTableID);
/* 52 */     addDataSourceType(DiskManagerFileInfo.class);
/* 53 */     setRefreshInterval(-2);
/* 54 */     if (sTableID.equals("MyTorrents")) {
/* 55 */       setPosition(-2);
/*    */     } else
/* 57 */       setPosition(-1);
/* 58 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 62 */     info.addCategories(new String[] { "progress" });
/* 63 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 68 */     Object ds = cell.getDataSource();
/* 69 */     int value; if ((ds instanceof DownloadManager))
/*    */     {
/* 71 */       DownloadManager dm = (DownloadManager)ds;
/* 72 */       DownloadManagerStats stats = dm.getStats();
/* 73 */       value = stats.getPercentDoneExcludingDND(); } else { int value;
/* 74 */       if ((ds instanceof DiskManagerFileInfo)) {
/* 75 */         DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 76 */         long length = fileInfo.getLength();
/* 77 */         int value; if (length == 0L) {
/* 78 */           value = 1000;
/*    */         } else
/* 80 */           value = (int)(fileInfo.getDownloaded() * 1000L / length);
/*    */       } else { return;
/*    */       }
/*    */     }
/*    */     int value;
/* 85 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/* 86 */       return;
/* 87 */     cell.setText(DisplayFormatters.formatPercentFromThousands(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DoneItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */