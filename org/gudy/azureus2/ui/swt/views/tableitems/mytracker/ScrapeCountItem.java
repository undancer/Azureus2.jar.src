/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytracker;
/*    */ 
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*    */ public class ScrapeCountItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public ScrapeCountItem()
/*    */   {
/* 36 */     super("scrapes", 2, -2, 60, "MyTracker");
/* 37 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 41 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 42 */     long value = item == null ? 0L : item.getScrapeCount();
/*    */     
/* 44 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 45 */       return;
/*    */     }
/*    */     
/* 48 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/ScrapeCountItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */