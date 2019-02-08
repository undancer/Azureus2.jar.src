/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
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
/*    */ public class ColumnTagCount
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "tag.count";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 30 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 33 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagCount(TableColumn column)
/*    */   {
/* 38 */     column.setWidth(70);
/* 39 */     column.setRefreshInterval(-2);
/* 40 */     column.setAlignment(2);
/* 41 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 45 */     Tag tag = (Tag)cell.getDataSource();
/* 46 */     int sortVal = 0;
/* 47 */     if (tag != null) {
/* 48 */       sortVal = tag.getTaggedCount();
/*    */     }
/*    */     
/* 51 */     if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/* 52 */       return;
/*    */     }
/*    */     
/* 55 */     if (!cell.isShown()) {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     cell.setText("" + sortVal);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagCount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */