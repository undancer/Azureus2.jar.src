/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*    */ public class FileWriteSpeedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public FileWriteSpeedItem()
/*    */   {
/* 34 */     super("writerate", 2, -1, 60, "Files");
/*    */     
/* 36 */     setRefreshInterval(-2);
/*    */     
/* 38 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 45 */     info.addCategories(new String[] { "bytes" });
/*    */     
/*    */ 
/*    */ 
/* 49 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 56 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*    */     
/* 58 */     int speed = 0;
/*    */     
/* 60 */     if (fileInfo != null)
/*    */     {
/* 62 */       speed = fileInfo.getWriteBytesPerSecond();
/*    */     }
/*    */     
/* 65 */     if ((!cell.setSortValue(speed)) && (cell.isValid()))
/*    */     {
/* 67 */       return;
/*    */     }
/*    */     
/* 70 */     cell.setText(speed == 0 ? "" : DisplayFormatters.formatByteCountToKiBEtcPerSec(speed));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/FileWriteSpeedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */