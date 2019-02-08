/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.ui.common.table.TableView;
/*    */ import com.aelitis.azureus.ui.common.table.TableViewCreator;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*    */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
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
/*    */ public class TagAddedToDateItem
/*    */   extends ColumnDateSizer
/*    */ {
/* 37 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "tag_added_to";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 42 */     info.addCategories(new String[] { "time", "content" });
/* 43 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public TagAddedToDateItem(String sTableID) {
/* 47 */     super(DATASOURCE_TYPE, "tag_added_to", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*    */     
/* 49 */     setMultiline(false);
/*    */     
/* 51 */     setRefreshInterval(-3);
/*    */     
/* 53 */     setShowTime(true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public TagAddedToDateItem(String tableID, boolean v)
/*    */   {
/* 61 */     this(tableID);
/* 62 */     setVisible(v);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell, long timestamp) {
/* 66 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 67 */     TableView<?> tv = cell.getTableRow().getView();
/* 68 */     TableViewCreator tvc = tv == null ? null : cell.getTableRow().getView().getTableViewCreator();
/* 69 */     if ((dm != null) && ((tvc instanceof MyTorrentsView))) {
/* 70 */       MyTorrentsView mtv = (MyTorrentsView)tvc;
/* 71 */       Tag[] tags = mtv.getCurrentTags();
/* 72 */       if ((tags != null) && (tags.length == 1)) {
/* 73 */         long time = tags[0].getTaggableAddedTime(dm);
/* 74 */         super.refresh(cell, time);
/* 75 */         return;
/*    */       }
/*    */     }
/* 78 */     super.refresh(cell, -1L);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TagAddedToDateItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */