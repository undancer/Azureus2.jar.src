/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.tracker;
/*    */ 
/*    */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*    */ public class LastUpdateItem
/*    */   extends ColumnDateSizer
/*    */ {
/* 33 */   public static final Class DATASOURCE_TYPE = TrackerPeerSource.class;
/*    */   public static final String COLUMN_ID = "last_update";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 38 */     info.addCategories(new String[] { "time", "tracker" });
/* 39 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public LastUpdateItem(String sTableID)
/*    */   {
/* 46 */     super(DATASOURCE_TYPE, "last_update", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*    */     
/* 48 */     setMultiline(false);
/*    */     
/* 50 */     setRefreshInterval(-1);
/*    */     
/* 52 */     setShowTime(true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public LastUpdateItem(String tableID, boolean v)
/*    */   {
/* 61 */     this(tableID);
/*    */     
/* 63 */     setVisible(v);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell, long timestamp) {
/* 67 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*    */     
/* 69 */     timestamp = ps == null ? 0L : ps.getLastUpdate();
/*    */     
/* 71 */     super.refresh(cell, timestamp * 1000L);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/LastUpdateItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */