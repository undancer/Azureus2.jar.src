/*    */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*    */ 
/*    */ import com.aelitis.azureus.core.subs.Subscription;
/*    */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*    */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*    */ public class ColumnSubscriptionLastChecked
/*    */   extends ColumnDateSizer
/*    */ {
/* 37 */   public static String COLUMN_ID = "last-checked";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 40 */     info.addCategories(new String[] { "essential", "time" });
/*    */     
/*    */ 
/*    */ 
/* 44 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ColumnSubscriptionLastChecked(String sTableID)
/*    */   {
/* 51 */     super(Subscription.class, COLUMN_ID, TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*    */     
/* 53 */     setRefreshInterval(-2);
/* 54 */     setMinWidth(100);
/*    */     
/* 56 */     setMultiline(false);
/*    */     
/* 58 */     setShowTime(true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell, long timestamp)
/*    */   {
/* 66 */     timestamp = 0L;
/*    */     
/* 68 */     Subscription sub = (Subscription)cell.getDataSource();
/*    */     
/* 70 */     if (sub != null)
/*    */     {
/* 72 */       timestamp = sub.getHistory().getLastScanTime();
/*    */     }
/*    */     
/* 75 */     if ((!cell.setSortValue(timestamp)) && (cell.isValid())) {
/* 76 */       return;
/*    */     }
/*    */     
/* 79 */     if (!cell.isShown()) {
/* 80 */       return;
/*    */     }
/*    */     
/* 83 */     if (sub.isSearchTemplate())
/*    */     {
/* 85 */       cell.setText("");
/*    */     }
/* 87 */     else if (timestamp <= 0L)
/*    */     {
/* 89 */       cell.setText("--");
/*    */     }
/*    */     else
/*    */     {
/* 93 */       super.refresh(cell, timestamp);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionLastChecked.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */