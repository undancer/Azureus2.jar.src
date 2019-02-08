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
/*    */ public class LeftItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public LeftItem()
/*    */   {
/* 37 */     super("left", 2, -2, 50, "MyTracker");
/* 38 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 42 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 43 */     long value = 0L;
/* 44 */     if (item != null) {
/* 45 */       Long longObject = (Long)item.getData("GUI_Left");
/* 46 */       if (longObject != null) {
/* 47 */         value = longObject.longValue();
/*    */       }
/*    */     }
/* 50 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 51 */       return;
/*    */     }
/*    */     
/* 54 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/LeftItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */