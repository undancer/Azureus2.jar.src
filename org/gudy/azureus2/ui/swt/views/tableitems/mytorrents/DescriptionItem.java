/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import com.aelitis.azureus.core.util.PlatformTorrentUtils;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
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
/*    */ public class DescriptionItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener, TableCellMouseListener, TableCellAddedListener
/*    */ {
/* 43 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "description";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 48 */     info.addCategories(new String[] { "content" });
/*    */   }
/*    */   
/*    */   public DescriptionItem(String sTableID)
/*    */   {
/* 53 */     super(DATASOURCE_TYPE, "description", 1, 150, sTableID);
/* 54 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void cellAdded(TableCell cell) {
/* 58 */     if ((cell instanceof TableCellSWT)) {
/* 59 */       ((TableCellSWT)cell).setCursorID(21);
/*    */     }
/*    */   }
/*    */   
/*    */   public void cellMouseTrigger(TableCellMouseEvent event) {
/* 64 */     DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/* 65 */     if (dm == null) { return;
/*    */     }
/* 67 */     if (event.eventType != 1) { return;
/*    */     }
/*    */     
/* 70 */     if (event.button != 1) return;
/* 71 */     event.skipCoreFunctionality = true;
/*    */     
/* 73 */     TorrentUtil.promptUserForDescription(new DownloadManager[] { dm });
/* 74 */     refresh(event.cell);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 78 */     if (cell.isDisposed()) { return;
/*    */     }
/*    */     
/* 81 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 82 */     String desc = "";
/* 83 */     if (dm != null) {
/*    */       try {
/* 85 */         desc = PlatformTorrentUtils.getContentDescription(dm.getTorrent());
/*    */         
/* 87 */         if (desc == null) {
/* 88 */           desc = "";
/*    */         }
/*    */       }
/*    */       catch (Throwable e) {}
/*    */     }
/* 93 */     cell.setText(desc);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DescriptionItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */