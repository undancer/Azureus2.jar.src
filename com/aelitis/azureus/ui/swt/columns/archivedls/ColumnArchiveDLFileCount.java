/*    */ package com.aelitis.azureus.ui.swt.columns.archivedls;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.download.DownloadStub;
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
/*    */ public class ColumnArchiveDLFileCount
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 26 */   public static String COLUMN_ID = "filecount";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 32 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/*    */ 
/* 36 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnArchiveDLFileCount(TableColumn column)
/*    */   {
/* 43 */     column.setWidth(70);
/* 44 */     column.setAlignment(2);
/* 45 */     column.setMinWidthAuto(true);
/*    */     
/* 47 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 54 */     DownloadStub dl = (DownloadStub)cell.getDataSource();
/*    */     
/* 56 */     long count = 0L;
/*    */     
/* 58 */     if (dl != null)
/*    */     {
/* 60 */       count = dl.getStubFiles().length;
/*    */     }
/*    */     
/*    */ 
/* 64 */     if (((cell.setSortValue(count)) || (!cell.isValid())) || 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 69 */       (!cell.isShown()))
/*    */     {
/* 71 */       return;
/*    */     }
/*    */     
/* 74 */     cell.setText(String.valueOf(count));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/archivedls/ColumnArchiveDLFileCount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */