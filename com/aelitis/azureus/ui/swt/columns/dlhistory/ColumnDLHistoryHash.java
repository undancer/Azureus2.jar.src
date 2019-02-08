/*    */ package com.aelitis.azureus.ui.swt.columns.dlhistory;
/*    */ 
/*    */ import org.gudy.azureus2.core3.history.DownloadHistory;
/*    */ import org.gudy.azureus2.core3.util.ByteFormatter;
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
/*    */ public class ColumnDLHistoryHash
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "hash";
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
/*    */   public ColumnDLHistoryHash(TableColumn column)
/*    */   {
/* 44 */     column.setWidth(200);
/* 45 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 52 */     DownloadHistory dl = (DownloadHistory)cell.getDataSource();
/*    */     
/* 54 */     byte[] hash = null;
/*    */     
/* 56 */     if (dl != null)
/*    */     {
/* 58 */       hash = dl.getTorrentHash();
/*    */     }
/*    */     
/* 61 */     String str = hash == null ? "" : ByteFormatter.encodeString(hash);
/*    */     
/* 63 */     if ((!cell.setSortValue(str)) && (cell.isValid()))
/*    */     {
/* 65 */       return;
/*    */     }
/*    */     
/* 68 */     if (!cell.isShown())
/*    */     {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     cell.setText(str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/dlhistory/ColumnDLHistoryHash.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */