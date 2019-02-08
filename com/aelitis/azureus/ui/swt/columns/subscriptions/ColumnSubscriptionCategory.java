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
/*    */ public class ColumnSubscriptionCategory
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 36 */   public static String COLUMN_ID = "category";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 39 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 42 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public ColumnSubscriptionCategory(String sTableID)
/*    */   {
/* 47 */     super(COLUMN_ID, -2, 100, sTableID);
/* 48 */     setRefreshInterval(-2);
/* 49 */     setMinWidth(100);
/* 50 */     setMaxWidth(100);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 55 */     Subscription sub = (Subscription)cell.getDataSource();
/* 56 */     String category = null;
/* 57 */     if (sub != null) {
/* 58 */       category = sub.getCategory();
/*    */     }
/*    */     
/* 61 */     if (category == null) {
/* 62 */       category = "";
/*    */     }
/*    */     
/* 65 */     if ((!cell.setSortValue(category)) && (cell.isValid())) {
/* 66 */       return;
/*    */     }
/*    */     
/* 69 */     if (!cell.isShown()) {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     cell.setText(category);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */