/*    */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*    */ 
/*    */ import com.aelitis.azureus.core.subs.Subscription;
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
/*    */ public class ColumnSubscriptionSubscribers
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 36 */   public static String COLUMN_ID = "nb-subscribers";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 39 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 42 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public ColumnSubscriptionSubscribers(String sTableID)
/*    */   {
/* 47 */     super(COLUMN_ID, -1, 100, sTableID);
/* 48 */     setRefreshInterval(-2);
/* 49 */     setMinWidth(100);
/* 50 */     setMaxWidth(100);
/* 51 */     setAlignment(2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 55 */     int nbSubsribers = 0;
/* 56 */     Subscription sub = (Subscription)cell.getDataSource();
/* 57 */     if (sub != null) {
/* 58 */       nbSubsribers = (int)sub.getCachedPopularity();
/*    */     }
/*    */     
/* 61 */     if ((!cell.setSortValue(nbSubsribers)) && (cell.isValid())) {
/* 62 */       return;
/*    */     }
/*    */     
/* 65 */     if (!cell.isShown()) {
/* 66 */       return;
/*    */     }
/*    */     
/* 69 */     if (nbSubsribers < 0) {
/* 70 */       cell.setText("");
/*    */     } else {
/* 72 */       cell.setText("" + nbSubsribers);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionSubscribers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */