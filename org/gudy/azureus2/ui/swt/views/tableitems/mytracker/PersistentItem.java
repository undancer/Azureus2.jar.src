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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PersistentItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PersistentItem()
/*    */   {
/* 42 */     super("persistent", 2, -1, 60, "MyTracker");
/*    */     
/* 44 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 50 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/*    */     
/* 52 */     String status_text = "";
/*    */     
/* 54 */     if (item != null)
/*    */     {
/* 56 */       if ((!cell.setSortValue(item.isPassive() ? 1L : 0L)) && (cell.isValid())) {
/* 57 */         return;
/*    */       }
/*    */       
/* 60 */       if (item.isPersistent()) {
/* 61 */         status_text = MessageText.getString("Button.yes").replaceAll("&", "");
/*    */       } else {
/* 63 */         status_text = MessageText.getString("Button.no").replaceAll("&", "");
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 68 */     cell.setText(status_text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/PersistentItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */