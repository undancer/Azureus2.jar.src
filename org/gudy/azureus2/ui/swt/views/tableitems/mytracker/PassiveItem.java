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
/*    */ public class PassiveItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PassiveItem()
/*    */   {
/* 38 */     super("passive", 2, -2, 60, "MyTracker");
/* 39 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 44 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/*    */     
/* 46 */     String status_text = "";
/*    */     
/* 48 */     if (item != null)
/*    */     {
/* 50 */       if ((!cell.setSortValue(item.isPassive() ? 1L : 0L)) && (cell.isValid())) {
/* 51 */         return;
/*    */       }
/*    */       
/* 54 */       if (item.isPassive()) {
/* 55 */         status_text = MessageText.getString("Button.yes").replaceAll("&", "");
/*    */       } else {
/* 57 */         status_text = MessageText.getString("Button.no").replaceAll("&", "");
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 62 */     cell.setText(status_text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/PassiveItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */