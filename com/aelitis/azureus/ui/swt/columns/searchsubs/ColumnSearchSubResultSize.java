/*    */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
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
/*    */ public class ColumnSearchSubResultSize
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "size";
/*    */   
/*    */   public ColumnSearchSubResultSize(TableColumn column)
/*    */   {
/* 35 */     column.initialize(2, -2, 80);
/* 36 */     column.addListeners(this);
/* 37 */     column.setRefreshInterval(-3);
/* 38 */     column.setType(3);
/*    */     
/* 40 */     if ((column instanceof TableColumnCore)) {
/* 41 */       ((TableColumnCore)column).setUseCoreDataSource(true);
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 46 */     SearchSubsResultBase rc = (SearchSubsResultBase)cell.getDataSource();
/* 47 */     if (rc == null) {
/* 48 */       return;
/*    */     }
/*    */     
/* 51 */     long size = rc.getSize();
/*    */     
/* 53 */     if ((size > 0L) && (cell.setSortValue(size)))
/*    */     {
/* 55 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtc(size));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultSize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */