/*    */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
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
/*    */ public class ColumnSearchSubResultRank
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "rank";
/*    */   
/*    */   public ColumnSearchSubResultRank(TableColumn column)
/*    */   {
/* 34 */     column.initialize(3, -2, 60);
/* 35 */     column.addListeners(this);
/* 36 */     column.setRefreshInterval(-3);
/* 37 */     column.setType(3);
/*    */     
/* 39 */     if ((column instanceof TableColumnCore)) {
/* 40 */       ((TableColumnCore)column).setUseCoreDataSource(true);
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 45 */     SearchSubsResultBase rc = (SearchSubsResultBase)cell.getDataSource();
/* 46 */     if (rc == null) {
/* 47 */       return;
/*    */     }
/*    */     
/* 50 */     long rank = rc.getRank();
/*    */     
/* 52 */     if ((rank >= 0L) && (cell.setSortValue(rank)))
/*    */     {
/* 54 */       cell.setText(String.valueOf(rank));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultRank.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */