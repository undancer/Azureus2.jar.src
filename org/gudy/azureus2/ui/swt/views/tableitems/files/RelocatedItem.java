/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
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
/*    */ public class RelocatedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public RelocatedItem()
/*    */   {
/* 33 */     super("relocated", 3, -1, 70, "Files");
/* 34 */     setRefreshInterval(-2);
/* 35 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 39 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 42 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 46 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*    */     
/*    */     boolean relocated;
/*    */     boolean relocated;
/* 50 */     if (fileInfo == null)
/*    */     {
/* 52 */       relocated = false;
/*    */     }
/*    */     else
/*    */     {
/* 56 */       File source = fileInfo.getFile(false);
/*    */       
/* 58 */       File target = fileInfo.getDownloadManager().getDownloadState().getFileLink(fileInfo.getIndex(), source);
/*    */       boolean relocated;
/* 60 */       if (target == null)
/*    */       {
/* 62 */         relocated = false;
/*    */       }
/*    */       else {
/*    */         boolean relocated;
/* 66 */         if (target == source)
/*    */         {
/* 68 */           relocated = false;
/*    */         }
/*    */         else
/*    */         {
/* 72 */           relocated = !target.equals(source);
/*    */         }
/*    */       }
/*    */     }
/*    */     
/* 77 */     if ((!cell.setSortValue(relocated ? 1L : 0L)) && (cell.isValid()))
/*    */     {
/* 79 */       return;
/*    */     }
/*    */     
/* 82 */     String text = relocated ? "*" : "";
/*    */     
/* 84 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/RelocatedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */