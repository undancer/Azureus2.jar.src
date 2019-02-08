/*    */ package com.aelitis.azureus.ui.swt.columns.dlhistory;
/*    */ 
/*    */ import org.gudy.azureus2.core3.history.DownloadHistory;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
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
/*    */ public class ColumnDLHistorySaveLocation
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener, ObfusticateCellText
/*    */ {
/* 28 */   public static String COLUMN_ID = "savepath";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 34 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/*    */ 
/* 38 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnDLHistorySaveLocation(TableColumn column)
/*    */   {
/* 45 */     column.setWidth(600);
/* 46 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 53 */     DownloadHistory dl = (DownloadHistory)cell.getDataSource();
/*    */     
/* 55 */     String sl = null;
/*    */     
/* 57 */     if (dl != null)
/*    */     {
/* 59 */       sl = dl.getSaveLocation();
/*    */     }
/*    */     
/* 62 */     if (sl == null)
/*    */     {
/* 64 */       sl = "";
/*    */     }
/*    */     
/* 67 */     if ((!cell.setSortValue(sl)) && (cell.isValid()))
/*    */     {
/* 69 */       return;
/*    */     }
/*    */     
/* 72 */     if (!cell.isShown())
/*    */     {
/* 74 */       return;
/*    */     }
/*    */     
/* 77 */     cell.setText(sl);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getObfusticatedText(TableCell cell)
/*    */   {
/* 84 */     DownloadHistory dl = (DownloadHistory)cell.getDataSource();
/*    */     
/* 86 */     if (dl == null)
/*    */     {
/* 88 */       return "";
/*    */     }
/*    */     
/* 91 */     return Debug.secretFileName(dl.getSaveLocation());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/dlhistory/ColumnDLHistorySaveLocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */