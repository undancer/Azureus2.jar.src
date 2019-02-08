/*    */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*    */ 
/*    */ import com.aelitis.azureus.core.subs.Subscription;
/*    */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*    */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*    */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
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
/*    */ public class ColumnSubscriptionMaxResults
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 33 */   public static String COLUMN_ID = "max-results";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 36 */     info.addCategories(new String[] { "settings" });
/*    */     
/*    */ 
/* 39 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public ColumnSubscriptionMaxResults(String sTableID)
/*    */   {
/* 44 */     super(COLUMN_ID, -1, 100, sTableID);
/* 45 */     setRefreshInterval(-2);
/* 46 */     setMinWidth(100);
/* 47 */     setMaxWidth(100);
/* 48 */     setAlignment(2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 52 */     int maxResults = 0;
/* 53 */     Subscription sub = (Subscription)cell.getDataSource();
/* 54 */     if (sub != null) {
/* 55 */       maxResults = sub.getHistory().getMaxNonDeletedResults();
/*    */     }
/*    */     
/* 58 */     if (maxResults < 0)
/*    */     {
/* 60 */       maxResults = SubscriptionManagerFactory.getSingleton().getMaxNonDeletedResults();
/*    */     }
/*    */     
/* 63 */     boolean is_st = sub.isSearchTemplate();
/*    */     
/* 65 */     if ((!cell.setSortValue(is_st ? -1L : maxResults)) && (cell.isValid())) {
/* 66 */       return;
/*    */     }
/*    */     
/* 69 */     if (!cell.isShown()) {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     if (is_st)
/*    */     {
/* 75 */       cell.setText("");
/*    */ 
/*    */     }
/* 78 */     else if (maxResults == 0)
/*    */     {
/* 80 */       cell.setText(MessageText.getString("ConfigView.unlimited"));
/*    */     }
/*    */     else
/*    */     {
/* 84 */       cell.setText("" + maxResults);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionMaxResults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */