/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.util.StringInterner;
/*    */ import org.gudy.azureus2.plugins.download.Download;
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
/*    */ 
/*    */ 
/*    */ public class FilesDoneItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 40 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "filesdone";
/*    */   
/*    */   public FilesDoneItem(String sTableID)
/*    */   {
/* 45 */     super(DATASOURCE_TYPE, "filesdone", 3, 50, sTableID);
/* 46 */     setRefreshInterval(5);
/* 47 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 51 */     info.addCategories(new String[] { "content", "progress" });
/*    */     
/*    */ 
/*    */ 
/* 55 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 59 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 61 */     String text = "";
/*    */     
/* 63 */     if (dm != null) {
/* 64 */       int complete = 0;
/* 65 */       int skipped = 0;
/* 66 */       int skipped_complete = 0;
/*    */       
/* 68 */       DiskManagerFileInfo[] files = dm.getDiskManagerFileInfo();
/*    */       
/* 70 */       int total = files.length;
/*    */       
/* 72 */       for (int i = 0; i < files.length; i++) {
/* 73 */         DiskManagerFileInfo file = files[i];
/*    */         
/* 75 */         if (file.getLength() == file.getDownloaded()) {
/* 76 */           complete++;
/* 77 */           if (file.isSkipped()) {
/* 78 */             skipped++;
/* 79 */             skipped_complete++;
/*    */           }
/* 81 */         } else if (file.isSkipped()) {
/* 82 */           skipped++;
/*    */         }
/*    */       }
/*    */       
/* 86 */       if (skipped == 0) {
/* 87 */         text = StringInterner.intern(complete + "/" + total);
/*    */       } else {
/* 89 */         text = complete - skipped_complete + "(" + complete + ")/" + (total - skipped) + "(" + total + ")";
/*    */       }
/*    */     }
/*    */     
/* 93 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/FilesDoneItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */