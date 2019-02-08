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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BadNATCountItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public BadNATCountItem()
/*    */   {
/* 42 */     super("badnat", 2, -2, 60, "MyTracker");
/* 43 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 49 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 50 */     long value = 0L;
/* 51 */     if (item != null) {
/* 52 */       Long longObject = (Long)item.getData("GUI_BadNATCount");
/* 53 */       if (longObject != null) {
/* 54 */         value = longObject.longValue();
/*    */       }
/*    */     }
/* 57 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 58 */       return;
/*    */     }
/*    */     
/* 61 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/BadNATCountItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */