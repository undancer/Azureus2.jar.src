/*    */ package org.gudy.azureus2.ui.swt.views.clientstats;
/*    */ 
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
/*    */ public class ColumnCS_Name
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "name";
/*    */   
/*    */   public ColumnCS_Name(TableColumn column)
/*    */   {
/* 32 */     column.initialize(1, -2, 215);
/* 33 */     column.addListeners(this);
/* 34 */     column.setType(3);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 38 */     ClientStatsDataSource ds = (ClientStatsDataSource)cell.getDataSource();
/* 39 */     if (ds == null) {
/* 40 */       return;
/*    */     }
/* 42 */     cell.setText(ds.client);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ColumnCS_Name.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */