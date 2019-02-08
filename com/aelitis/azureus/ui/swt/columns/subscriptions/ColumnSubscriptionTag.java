/*    */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*    */ 
/*    */ import com.aelitis.azureus.core.subs.Subscription;
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagManager;
/*    */ import com.aelitis.azureus.core.tag.TagManagerFactory;
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
/*    */ public class ColumnSubscriptionTag
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 39 */   private static TagManager tag_manager = ;
/*    */   
/* 41 */   public static String COLUMN_ID = "tag.name";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 44 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 47 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public ColumnSubscriptionTag(String sTableID)
/*    */   {
/* 52 */     super(COLUMN_ID, -2, 100, sTableID);
/* 53 */     setRefreshInterval(-2);
/* 54 */     setMinWidth(100);
/* 55 */     setMaxWidth(100);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 59 */     Subscription sub = (Subscription)cell.getDataSource();
/*    */     
/* 61 */     Tag tag = tag_manager.lookupTagByUID(sub.getTagID());
/*    */     
/* 63 */     String tag_str = tag == null ? "" : tag.getTagName(true);
/*    */     
/* 65 */     if ((!cell.setSortValue(tag_str)) && (cell.isValid())) {
/* 66 */       return;
/*    */     }
/*    */     
/* 69 */     if (!cell.isShown()) {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     cell.setText(tag_str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionTag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */