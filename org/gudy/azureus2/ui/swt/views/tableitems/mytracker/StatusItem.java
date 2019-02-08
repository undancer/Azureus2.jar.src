/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytracker;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*    */ public class StatusItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public StatusItem()
/*    */   {
/* 37 */     super("status", -2, 60, "MyTracker");
/* 38 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 43 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/*    */     
/* 45 */     String status_text = "";
/*    */     
/* 47 */     if (item != null) {
/* 48 */       int status = item.getStatus();
/*    */       
/* 50 */       if ((!cell.setSortValue(status)) && (cell.isValid())) {
/* 51 */         return;
/*    */       }
/*    */       
/* 54 */       if (status == 2) {
/* 55 */         status_text = MessageText.getString("MyTrackerView.status.started");
/*    */       }
/* 57 */       else if (status == 1) {
/* 58 */         status_text = MessageText.getString("MyTrackerView.status.stopped");
/*    */       }
/* 60 */       else if (status == 0) {
/* 61 */         status_text = MessageText.getString("MyTrackerView.status.failed");
/*    */       }
/* 63 */       else if (status == 3) {
/* 64 */         status_text = MessageText.getString("MyTrackerView.status.published");
/*    */       }
/*    */     }
/*    */     
/* 68 */     cell.setText(status_text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/StatusItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */