/*    */ package com.aelitis.azureus.ui.swt.columns.dlhistory;
/*    */ 
/*    */ import org.gudy.azureus2.core3.history.DownloadHistory;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*    */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*    */ public class ColumnDLHistoryAddDate
/*    */   implements TableColumnExtraInfoListener, TableCellRefreshListener
/*    */ {
/* 35 */   public static String COLUMN_ID = "added";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 38 */     info.addCategories(new String[] { "time" });
/*    */     
/*    */ 
/* 41 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnDLHistoryAddDate(TableColumn column)
/*    */   {
/* 48 */     column.setWidth(TableColumnCreator.DATE_COLUMN_WIDTH);
/* 49 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 56 */     TableColumn tc = cell.getTableColumn();
/*    */     
/* 58 */     if ((tc instanceof ColumnDateSizer))
/*    */     {
/* 60 */       DownloadHistory dl = (DownloadHistory)cell.getDataSource();
/*    */       
/* 62 */       ((ColumnDateSizer)tc).refresh(cell, dl.getAddTime());
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/dlhistory/ColumnDLHistoryAddDate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */