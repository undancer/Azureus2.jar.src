/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
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
/*    */ public class TorrentPathItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener, ObfusticateCellText
/*    */ {
/* 41 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "torrentpath";
/*    */   
/*    */   public TorrentPathItem(String sTableID)
/*    */   {
/* 47 */     super(DATASOURCE_TYPE, "torrentpath", 1, 150, sTableID);
/* 48 */     setObfustication(true);
/* 49 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 53 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 56 */     info.setProficiency((byte)2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 60 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 62 */     cell.setText(dm == null ? "" : dm.getTorrentFileName());
/*    */   }
/*    */   
/*    */   public String getObfusticatedText(TableCell cell) {
/* 66 */     return Debug.secretFileName(cell.getText());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TorrentPathItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */