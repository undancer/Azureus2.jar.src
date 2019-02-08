/*    */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*    */ 
/*    */ import com.aelitis.azureus.core.subs.Subscription;
/*    */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
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
/*    */ public class ColumnSubscriptionNbNewResults
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 36 */   public static String COLUMN_ID = "nb-new-results";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 39 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 42 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnSubscriptionNbNewResults(String sTableID)
/*    */   {
/* 47 */     super(COLUMN_ID, -2, 100, sTableID);
/* 48 */     setRefreshInterval(-2);
/* 49 */     setMinWidth(100);
/* 50 */     setMaxWidth(100);
/* 51 */     setAlignment(2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 55 */     int nbResults = 0;
/* 56 */     Subscription sub = (Subscription)cell.getDataSource();
/* 57 */     if (sub != null) {
/* 58 */       if (sub.isSearchTemplate()) {
/* 59 */         nbResults = -1;
/*    */       } else {
/* 61 */         nbResults = sub.getHistory().getNumUnread();
/*    */       }
/*    */     }
/*    */     
/* 65 */     if ((!cell.setSortValue(nbResults)) && (cell.isValid())) {
/* 66 */       return;
/*    */     }
/*    */     
/* 69 */     if (!cell.isShown()) {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     if (sub.isSearchTemplate()) {
/* 74 */       cell.setText("");
/*    */     } else {
/* 76 */       cell.setText("" + nbResults);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionNbNewResults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */