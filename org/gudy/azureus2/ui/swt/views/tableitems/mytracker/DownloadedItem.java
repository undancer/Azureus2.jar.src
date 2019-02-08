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
/*    */ 
/*    */ public class DownloadedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public DownloadedItem()
/*    */   {
/* 38 */     super("downloaded", 2, -2, 70, "MyTracker");
/* 39 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 43 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 44 */     long value = 0L;
/* 45 */     if (item != null) {
/* 46 */       Long longObject = (Long)item.getData("GUI_Downloaded");
/* 47 */       if (longObject != null) {
/* 48 */         value = longObject.longValue();
/*    */       }
/*    */     }
/* 51 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 52 */       return;
/*    */     }
/*    */     
/* 55 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/DownloadedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */