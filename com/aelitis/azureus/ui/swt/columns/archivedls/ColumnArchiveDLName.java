/*    */ package com.aelitis.azureus.ui.swt.columns.archivedls;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*    */ import org.gudy.azureus2.plugins.download.DownloadStub;
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
/*    */ public class ColumnArchiveDLName
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener, ObfusticateCellText
/*    */ {
/* 28 */   public static String COLUMN_ID = "name";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 34 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/*    */ 
/* 38 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnArchiveDLName(TableColumn column)
/*    */   {
/* 45 */     column.setWidth(400);
/* 46 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 53 */     DownloadStub dl = (DownloadStub)cell.getDataSource();
/*    */     
/* 55 */     String name = null;
/*    */     
/* 57 */     if (dl != null)
/*    */     {
/* 59 */       name = dl.getName();
/*    */     }
/*    */     
/* 62 */     if (name == null)
/*    */     {
/* 64 */       name = "";
/*    */     }
/*    */     
/* 67 */     if ((!cell.setSortValue(name)) && (cell.isValid()))
/*    */     {
/* 69 */       return;
/*    */     }
/*    */     
/* 72 */     if (!cell.isShown())
/*    */     {
/* 74 */       return;
/*    */     }
/*    */     
/* 77 */     cell.setText(name);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getObfusticatedText(TableCell cell)
/*    */   {
/* 84 */     DownloadStub dl = (DownloadStub)cell.getDataSource();
/*    */     
/* 86 */     if (dl == null)
/*    */     {
/* 88 */       return "";
/*    */     }
/*    */     
/* 91 */     return ByteFormatter.encodeString(dl.getTorrentHash());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/archivedls/ColumnArchiveDLName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */