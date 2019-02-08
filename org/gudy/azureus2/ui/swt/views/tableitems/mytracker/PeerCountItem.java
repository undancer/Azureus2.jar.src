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
/*    */ public class PeerCountItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PeerCountItem()
/*    */   {
/* 36 */     super("peers", 2, -2, 60, "MyTracker");
/* 37 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 41 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 42 */     long value = 0L;
/* 43 */     if (item != null) {
/* 44 */       Long longObject = (Long)item.getData("GUI_PeerCount");
/* 45 */       if (longObject != null) {
/* 46 */         value = longObject.longValue();
/*    */       }
/*    */     }
/* 49 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 50 */       return;
/*    */     }
/*    */     
/* 53 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/PeerCountItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */