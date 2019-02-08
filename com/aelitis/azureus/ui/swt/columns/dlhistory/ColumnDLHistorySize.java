/*    */ package com.aelitis.azureus.ui.swt.columns.dlhistory;
/*    */ 
/*    */ import org.gudy.azureus2.core3.history.DownloadHistory;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*    */ public class ColumnDLHistorySize
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "size";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 33 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/*    */ 
/* 37 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnDLHistorySize(TableColumn column)
/*    */   {
/* 44 */     column.setWidth(80);
/* 45 */     column.setAlignment(2);
/* 46 */     column.setRefreshInterval(-3);
/* 47 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 54 */     DownloadHistory dl = (DownloadHistory)cell.getDataSource();
/*    */     
/* 56 */     long size = 0L;
/*    */     
/* 58 */     if (dl != null)
/*    */     {
/* 60 */       size = dl.getSize();
/*    */     }
/*    */     
/* 63 */     if ((!cell.setSortValue(size)) && (cell.isValid()))
/*    */     {
/* 65 */       return;
/*    */     }
/*    */     
/* 68 */     if (!cell.isShown())
/*    */     {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     cell.setText(size <= 0L ? "" : DisplayFormatters.formatByteCountToKiBEtc(size));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/dlhistory/ColumnDLHistorySize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */