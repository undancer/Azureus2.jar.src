/*    */ package org.gudy.azureus2.ui.swt.views.clientstats;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
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
/*    */ public class ColumnCS_ReceivedPer
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "received.per";
/*    */   
/*    */   public ColumnCS_ReceivedPer(TableColumn column)
/*    */   {
/* 33 */     column.initialize(2, -2, 80);
/* 34 */     column.addListeners(this);
/* 35 */     column.setType(3);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 39 */     ClientStatsDataSource ds = (ClientStatsDataSource)cell.getDataSource();
/* 40 */     if ((ds == null) || (ds.count == 0)) {
/* 41 */       return;
/*    */     }
/* 43 */     long val = ds.bytesReceived / ds.count;
/* 44 */     if ((cell.setSortValue(val)) || (!cell.isValid())) {
/* 45 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtc(val));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ColumnCS_ReceivedPer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */