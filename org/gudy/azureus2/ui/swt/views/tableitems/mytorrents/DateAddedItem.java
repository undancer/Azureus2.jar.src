/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*    */ import java.io.File;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*    */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*    */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*    */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*    */ public class DateAddedItem
/*    */   extends ColumnDateSizer
/*    */ {
/* 41 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "date_added";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 46 */     info.addCategories(new String[] { "time", "content" });
/* 47 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public DateAddedItem(String sTableID) {
/* 51 */     super(DATASOURCE_TYPE, "date_added", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*    */     
/* 53 */     setMultiline(false);
/*    */     
/*    */ 
/* 56 */     TableContextMenuItem menuReset = addContextMenuItem("TableColumn.menu.date_added.reset");
/* 57 */     menuReset.addListener(new MenuItemListener() {
/*    */       public void selected(MenuItem menu, Object target) {
/* 59 */         if ((target instanceof TableRowCore)) {
/* 60 */           TableRowCore row = (TableRowCore)target;
/* 61 */           Object dataSource = row.getDataSource(true);
/* 62 */           if ((dataSource instanceof DownloadManager)) {
/* 63 */             DownloadManager dm = (DownloadManager)dataSource;
/*    */             
/* 65 */             DownloadManagerState state = dm.getDownloadState();
/*    */             try
/*    */             {
/* 68 */               long add_time = new File(dm.getTorrentFileName()).lastModified();
/*    */               
/* 70 */               if (add_time >= 0L) {
/* 71 */                 state.setLongParameter("stats.download.added.time", add_time);
/*    */               }
/*    */             }
/*    */             catch (Throwable e) {}
/*    */           }
/*    */           
/*    */ 
/* 78 */           row.getTableCell("date_added").invalidate();
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DateAddedItem(String tableID, boolean v)
/*    */   {
/* 89 */     this(tableID);
/* 90 */     setVisible(v);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell, long timestamp) {
/* 94 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 95 */     timestamp = dm == null ? 0L : dm.getDownloadState().getLongParameter("stats.download.added.time");
/*    */     
/* 97 */     super.refresh(cell, timestamp);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DateAddedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */