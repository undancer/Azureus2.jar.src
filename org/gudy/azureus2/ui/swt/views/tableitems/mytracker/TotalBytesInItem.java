/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytracker;
/*    */ 
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class TotalBytesInItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public TotalBytesInItem()
/*    */   {
/* 37 */     super("bytesin", 2, -2, 50, "MyTracker");
/* 38 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 42 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 43 */     long value = item == null ? 0L : item.getTotalBytesIn();
/*    */     
/* 45 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 46 */       return;
/*    */     }
/*    */     
/* 49 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/TotalBytesInItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */