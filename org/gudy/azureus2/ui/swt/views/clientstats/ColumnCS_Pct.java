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
/*    */ 
/*    */ public class ColumnCS_Pct
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "percent";
/*    */   
/*    */   public ColumnCS_Pct(TableColumn column)
/*    */   {
/* 34 */     column.initialize(2, -2, 50);
/* 35 */     column.addListeners(this);
/* 36 */     column.setType(3);
/* 37 */     column.setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 41 */     ClientStatsDataSource ds = (ClientStatsDataSource)cell.getDataSource();
/* 42 */     if (ds == null) {
/* 43 */       return;
/*    */     }
/* 45 */     float val = ds.count * 1000.0F / (float)ds.overall.count;
/* 46 */     if ((cell.setSortValue(val)) || (!cell.isValid())) {
/* 47 */       cell.setText(DisplayFormatters.formatPercentFromThousands((int)val));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ColumnCS_Pct.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */