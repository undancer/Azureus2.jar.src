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
/*    */ public class ColumnSubscriptionError
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 31 */   public static String COLUMN_ID = "last-error";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 35 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 38 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public ColumnSubscriptionError(String sTableID)
/*    */   {
/* 43 */     super(COLUMN_ID, -2, 300, sTableID);
/* 44 */     setRefreshInterval(-2);
/* 45 */     setAlignment(1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 49 */     String error = "";
/* 50 */     Subscription sub = (Subscription)cell.getDataSource();
/* 51 */     if (sub != null) {
/* 52 */       error = sub.getHistory().getLastError();
/*    */     }
/*    */     
/* 55 */     if ((!cell.setSortValue(error)) && (cell.isValid())) {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     if (!cell.isShown()) {
/* 60 */       return;
/*    */     }
/*    */     
/* 63 */     cell.setText(error);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */