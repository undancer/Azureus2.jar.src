/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.tracker;
/*    */ 
/*    */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
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
/*    */ public class PeersItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PeersItem(String tableID)
/*    */   {
/* 39 */     super("peers", 3, -2, 75, tableID);
/*    */     
/* 41 */     setRefreshInterval(-1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 48 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 51 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 58 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*    */     
/* 60 */     int value = ps == null ? -1 : ps.getPeers();
/*    */     
/* 62 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/*    */     {
/* 64 */       return;
/*    */     }
/*    */     
/* 67 */     cell.setText(value < 0 ? "" : String.valueOf(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/PeersItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */