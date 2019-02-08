/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*    */ 
/*    */ public class StorageTypeItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public StorageTypeItem()
/*    */   {
/* 38 */     super("storagetype", 1, -1, 70, "Files");
/* 39 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 43 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 46 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 50 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*    */     String tmp;
/* 52 */     String tmp; if (fileInfo == null) {
/* 53 */       tmp = "";
/*    */     } else {
/* 55 */       int st = fileInfo.getStorageType();
/* 56 */       String tmp; if (st == 1) {
/* 57 */         tmp = MessageText.getString("FileItem.storage.linear"); } else { String tmp;
/* 58 */         if (st == 2) {
/* 59 */           tmp = MessageText.getString("FileItem.storage.compact");
/*    */         } else
/* 61 */           tmp = MessageText.getString("FileItem.storage.reorder");
/*    */       }
/*    */     }
/* 64 */     cell.setText(tmp);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/StorageTypeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */