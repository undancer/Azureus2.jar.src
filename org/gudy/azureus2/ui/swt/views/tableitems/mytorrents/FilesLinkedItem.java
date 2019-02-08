/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import com.aelitis.azureus.core.util.LinkFileMap;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
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
/*    */ public class FilesLinkedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 38 */   public static final Class<Download> DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "fileslinked";
/*    */   
/*    */   public FilesLinkedItem(String sTableID)
/*    */   {
/* 43 */     super(DATASOURCE_TYPE, "fileslinked", 3, 50, sTableID);
/*    */     
/* 45 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 49 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 52 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 56 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 58 */     int link_count = 0;
/*    */     
/* 60 */     if (dm != null) {
/* 61 */       link_count = dm.getDownloadState().getFileLinks().size();
/*    */     }
/*    */     
/* 64 */     if ((!cell.setSortValue(link_count)) && (cell.isValid()))
/*    */     {
/* 66 */       return;
/*    */     }
/*    */     
/* 69 */     cell.setText(link_count == 0 ? "" : String.valueOf(link_count));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/FilesLinkedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */