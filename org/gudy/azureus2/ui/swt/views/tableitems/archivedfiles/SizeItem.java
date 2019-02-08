/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.archivedfiles;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*    */ 
/*    */ 
/*    */ public class SizeItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellLightRefreshListener
/*    */ {
/*    */   public SizeItem(String tableID)
/*    */   {
/* 38 */     super("size", 2, -2, 70, tableID);
/*    */     
/* 40 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 47 */     info.addCategories(new String[] { "bytes" });
/*    */     
/*    */ 
/*    */ 
/* 51 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell, boolean sortOnlyRefresh)
/*    */   {
/* 59 */     DownloadStub.DownloadStubFile fileInfo = (DownloadStub.DownloadStubFile)cell.getDataSource();
/*    */     
/*    */     long size;
/*    */     long size;
/* 63 */     if (fileInfo == null)
/*    */     {
/* 65 */       size = 0L;
/*    */     }
/*    */     else
/*    */     {
/* 69 */       size = fileInfo.getLength();
/*    */     }
/*    */     
/* 72 */     if ((!cell.setSortValue(size)) && (cell.isValid()))
/*    */     {
/* 74 */       return;
/*    */     }
/*    */     
/* 77 */     if (size < 0L)
/*    */     {
/*    */ 
/*    */ 
/* 81 */       cell.setText("(" + DisplayFormatters.formatByteCountToKiBEtc(-size) + ")");
/*    */     }
/*    */     else
/*    */     {
/* 85 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtc(size));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 93 */     refresh(cell, false);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/archivedfiles/SizeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */