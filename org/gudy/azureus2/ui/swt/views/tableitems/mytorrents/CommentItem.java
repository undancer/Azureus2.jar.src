/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.TorrentUtil;
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
/*    */ public class CommentItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener, TableCellMouseListener, ObfusticateCellText
/*    */ {
/* 43 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "comment";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 48 */     info.addCategories(new String[] { "content" });
/*    */   }
/*    */   
/*    */   public CommentItem(String sTableID)
/*    */   {
/* 53 */     super(DATASOURCE_TYPE, "comment", 1, 300, sTableID);
/* 54 */     setRefreshInterval(-2);
/* 55 */     setType(1);
/* 56 */     setObfustication(true);
/* 57 */     setMinWidth(50);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 61 */     String comment = null;
/* 62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 63 */     comment = dm.getDownloadState().getUserComment();
/* 64 */     if (comment != null) {
/* 65 */       comment = comment.replace('\r', ' ').replace('\n', ' ');
/*    */     }
/* 67 */     cell.setText(comment == null ? "" : comment);
/*    */   }
/*    */   
/*    */   public void cellMouseTrigger(TableCellMouseEvent event) {
/* 71 */     DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/* 72 */     if (dm == null) { return;
/*    */     }
/* 74 */     event.skipCoreFunctionality = true;
/* 75 */     if (event.eventType != 2) return;
/* 76 */     TorrentUtil.promptUserForComment(new DownloadManager[] { dm });
/*    */   }
/*    */   
/*    */   public String getObfusticatedText(TableCell cell) {
/* 80 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 81 */     return Integer.toHexString(dm.hashCode());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/CommentItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */