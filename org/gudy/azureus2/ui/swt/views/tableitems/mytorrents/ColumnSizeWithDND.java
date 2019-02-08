/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
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
/*    */ public class ColumnSizeWithDND
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 35 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "sizewithdnd";
/*    */   
/*    */   public ColumnSizeWithDND(String sTableID)
/*    */   {
/* 41 */     super(DATASOURCE_TYPE, "sizewithdnd", 2, 70, sTableID);
/* 42 */     setRefreshInterval(-2);
/* 43 */     setMinWidthAuto(true);
/*    */     
/* 45 */     setPosition(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 49 */     info.addCategories(new String[] { "sharing", "bytes" });
/*    */     
/*    */ 
/*    */ 
/* 53 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 57 */     long value = 0L;
/* 58 */     Object ds = cell.getDataSource();
/* 59 */     if ((ds instanceof DownloadManager)) {
/* 60 */       DownloadManager dm = (DownloadManager)ds;
/* 61 */       value = dm.getSize();
/* 62 */     } else if ((ds instanceof DiskManagerFileInfo)) {
/* 63 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 64 */       value = fileInfo.getLength();
/*    */     }
/*    */     
/* 67 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 68 */       return;
/*    */     }
/* 70 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/ColumnSizeWithDND.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */