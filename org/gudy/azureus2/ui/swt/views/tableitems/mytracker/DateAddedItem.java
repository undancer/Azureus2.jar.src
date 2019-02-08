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
/*    */ public class DateAddedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public DateAddedItem()
/*    */   {
/* 38 */     super("date_added", 2, -2, 60, "MyTracker");
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 43 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/*    */     
/* 45 */     String date_text = "";
/*    */     
/* 47 */     if (item != null)
/*    */     {
/* 49 */       long date = item.getDateAdded();
/*    */       
/* 51 */       date_text = DisplayFormatters.formatDate(date);
/*    */     }
/*    */     
/* 54 */     cell.setText(date_text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/DateAddedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */