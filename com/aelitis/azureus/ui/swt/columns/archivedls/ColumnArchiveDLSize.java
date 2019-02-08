/*    */ package com.aelitis.azureus.ui.swt.columns.archivedls;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
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
/*    */ public class ColumnArchiveDLSize
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 29 */   public static String COLUMN_ID = "size";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 35 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/*    */ 
/* 39 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnArchiveDLSize(TableColumn column)
/*    */   {
/* 46 */     column.setWidth(70);
/* 47 */     column.setAlignment(2);
/* 48 */     column.setMinWidthAuto(true);
/*    */     
/* 50 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 57 */     DownloadStub dl = (DownloadStub)cell.getDataSource();
/*    */     
/* 59 */     long size = 0L;
/*    */     
/* 61 */     if (dl != null)
/*    */     {
/* 63 */       size = dl.getTorrentSize();
/*    */     }
/*    */     
/*    */ 
/* 67 */     if (((cell.setSortValue(size)) || (!cell.isValid())) || 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 72 */       (!cell.isShown()))
/*    */     {
/* 74 */       return;
/*    */     }
/*    */     
/* 77 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(size));
/*    */     
/* 79 */     if ((Utils.getUserMode() > 0) && ((cell instanceof TableCellSWT))) {
/* 80 */       if (size >= 1073741824L) {
/* 81 */         ((TableCellSWT)cell).setTextAlpha(456);
/* 82 */       } else if (size < 1048576L) {
/* 83 */         ((TableCellSWT)cell).setTextAlpha(180);
/*    */       } else {
/* 85 */         ((TableCellSWT)cell).setTextAlpha(255);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/archivedls/ColumnArchiveDLSize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */