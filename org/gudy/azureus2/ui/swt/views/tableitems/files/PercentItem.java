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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PercentItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PercentItem()
/*    */   {
/* 37 */     super("%", 2, -2, 60, "Files");
/* 38 */     setRefreshInterval(-2);
/* 39 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 43 */     info.addCategories(new String[] { "progress" });
/*    */     
/*    */ 
/* 46 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 51 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*    */     
/* 53 */     long percent = 0L;
/*    */     
/* 55 */     if (fileInfo != null) {
/* 56 */       long bytesDownloaded = fileInfo.getDownloaded();
/*    */       
/* 58 */       if (bytesDownloaded < 0L)
/*    */       {
/* 60 */         percent = -1L;
/*    */       }
/*    */       else {
/* 63 */         long length = fileInfo.getLength();
/*    */         
/* 65 */         if (length != 0L)
/*    */         {
/* 67 */           percent = 1000L * bytesDownloaded / length;
/*    */         }
/*    */         else
/*    */         {
/* 71 */           percent = 1000L;
/*    */         }
/*    */       }
/*    */     }
/*    */     else
/*    */     {
/* 77 */       percent = -1L;
/*    */     }
/*    */     
/* 80 */     if ((!cell.setSortValue(percent)) && (cell.isValid()))
/*    */     {
/* 82 */       return;
/*    */     }
/*    */     
/* 85 */     cell.setText(percent < 0L ? "" : DisplayFormatters.formatPercentFromThousands((int)percent));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/PercentItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */