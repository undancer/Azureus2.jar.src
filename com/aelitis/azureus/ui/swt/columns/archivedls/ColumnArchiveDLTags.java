/*    */ package com.aelitis.azureus.ui.swt.columns.archivedls;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubEx;
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
/*    */ public class ColumnArchiveDLTags
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 26 */   public static String COLUMN_ID = "tags";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 32 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/*    */ 
/* 36 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnArchiveDLTags(TableColumn column)
/*    */   {
/* 43 */     column.setPosition(-1);
/* 44 */     column.setWidth(100);
/* 45 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 52 */     DownloadStub.DownloadStubEx dl = (DownloadStub.DownloadStubEx)cell.getDataSource();
/*    */     
/* 54 */     String text = "";
/*    */     
/* 56 */     if (dl != null)
/*    */     {
/* 58 */       String[] tags = dl.getManualTags();
/*    */       
/* 60 */       if (tags != null)
/*    */       {
/* 62 */         for (String t : tags)
/*    */         {
/* 64 */           text = text + (text.length() == 0 ? "" : ", ") + t;
/*    */         }
/*    */       }
/*    */     }
/*    */     
/* 69 */     if ((!cell.setSortValue(text)) && (cell.isValid()))
/*    */     {
/* 71 */       return;
/*    */     }
/*    */     
/* 74 */     if (!cell.isShown())
/*    */     {
/* 76 */       return;
/*    */     }
/*    */     
/* 79 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/archivedls/ColumnArchiveDLTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */