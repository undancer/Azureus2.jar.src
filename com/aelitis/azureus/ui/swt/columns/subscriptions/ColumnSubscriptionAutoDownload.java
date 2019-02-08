/*    */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*    */ 
/*    */ import com.aelitis.azureus.core.subs.Subscription;
/*    */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
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
/*    */ 
/*    */ 
/*    */ public class ColumnSubscriptionAutoDownload
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 38 */   public static String COLUMN_ID = "auto-download";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 41 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 44 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnSubscriptionAutoDownload(String sTableID)
/*    */   {
/* 49 */     super(COLUMN_ID, 3, -2, 100, sTableID);
/* 50 */     setRefreshInterval(-2);
/* 51 */     setMinWidth(100);
/* 52 */     setMaxWidth(100);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 56 */     boolean autoDownload = false;
/* 57 */     Subscription sub = (Subscription)cell.getDataSource();
/* 58 */     if (sub != null) {
/* 59 */       SubscriptionHistory history = sub.getHistory();
/* 60 */       if (history != null) {
/* 61 */         autoDownload = history.isAutoDownload();
/*    */       }
/*    */     }
/*    */     
/* 65 */     if ((!cell.setSortValue(autoDownload ? 1L : 0L)) && (cell.isValid())) {
/* 66 */       return;
/*    */     }
/*    */     
/* 69 */     if (!cell.isShown()) {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     if (sub.isAutoDownloadSupported()) {
/* 74 */       cell.setText(DisplayFormatters.getYesNo(autoDownload));
/*    */     } else {
/* 76 */       if ((!cell.setSortValue(-1L)) && (cell.isValid())) {
/* 77 */         return;
/*    */       }
/* 79 */       cell.setText("");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionAutoDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */