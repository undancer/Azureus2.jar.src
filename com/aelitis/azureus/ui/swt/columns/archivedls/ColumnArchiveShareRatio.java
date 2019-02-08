/*    */ package com.aelitis.azureus.ui.swt.columns.archivedls;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ 
/*    */ 
/*    */ public class ColumnArchiveShareRatio
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 29 */   public static String COLUMN_ID = "shareRatio";
/*    */   
/*    */ 
/*    */ 
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 35 */     info.addCategories(new String[] { "progress" });
/*    */     
/*    */ 
/*    */ 
/* 39 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnArchiveShareRatio(TableColumn column)
/*    */   {
/* 46 */     column.setWidth(70);
/* 47 */     column.setAlignment(2);
/* 48 */     column.setMinWidthAuto(true);
/*    */     
/* 50 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 57 */     DownloadStub.DownloadStubEx dl = (DownloadStub.DownloadStubEx)cell.getDataSource();
/*    */     
/*    */ 
/*    */ 
/* 61 */     int sr = -1;
/*    */     String sr_str;
/* 63 */     String sr_str; if (dl != null)
/*    */     {
/* 65 */       sr = dl.getShareRatio();
/*    */       String sr_str;
/* 67 */       if (sr < 0)
/*    */       {
/* 69 */         sr_str = "";
/*    */       } else { String sr_str;
/* 71 */         if (sr == Integer.MAX_VALUE)
/*    */         {
/* 73 */           sr_str = "∞";
/*    */         }
/*    */         else
/*    */         {
/* 77 */           sr_str = DisplayFormatters.formatDecimal(sr / 1000.0D, 3);
/*    */         }
/*    */       }
/*    */     } else {
/* 81 */       sr_str = "";
/*    */     }
/*    */     
/* 84 */     if ((!cell.setSortValue(sr)) && (cell.isValid()))
/*    */     {
/* 86 */       return;
/*    */     }
/*    */     
/* 89 */     cell.setText(sr_str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/archivedls/ColumnArchiveShareRatio.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */