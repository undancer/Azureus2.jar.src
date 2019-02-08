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
/*    */ 
/*    */ public class CompletedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public CompletedItem(String tableID)
/*    */   {
/* 40 */     super("completed", 3, -2, 75, tableID);
/*    */     
/* 42 */     setRefreshInterval(-1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 49 */     info.addCategories(new String[] { "essential" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 58 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*    */     
/* 60 */     int value = ps == null ? -1 : ps.getCompletedCount();
/*    */     
/* 62 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/*    */     {
/* 64 */       return;
/*    */     }
/*    */     
/* 67 */     cell.setText(value < 0 ? "" : String.valueOf(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/CompletedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */