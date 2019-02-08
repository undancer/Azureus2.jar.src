/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*    */ public class TimeUntilCompleteItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public TimeUntilCompleteItem(String table_id)
/*    */   {
/* 42 */     super("timetocomplete", 2, -1, 65, table_id);
/* 43 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 47 */     info.addCategories(new String[] { "time", "content" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 54 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 55 */     long value = peer == null ? Long.MAX_VALUE : peer.getStats().getEstimatedSecondsToCompletion();
/*    */     
/* 57 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 58 */       return;
/*    */     }
/* 60 */     if (value > 604800L)
/*    */     {
/* 62 */       value = Long.MAX_VALUE;
/*    */     }
/*    */     
/* 65 */     cell.setText(DisplayFormatters.formatETA(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/TimeUntilCompleteItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */