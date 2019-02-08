/*    */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*    */ 
/*    */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
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
/*    */ 
/*    */ 
/*    */ public class ColumnSearchSubResultCategory
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 30 */   public static String COLUMN_ID = "category";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 34 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 37 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnSearchSubResultCategory(TableColumn column)
/*    */   {
/* 42 */     column.initialize(3, -2, 100);
/* 43 */     column.setRefreshInterval(-3);
/* 44 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 48 */     SearchSubsResultBase result = (SearchSubsResultBase)cell.getDataSource();
/*    */     
/* 50 */     String str = result.getCategory();
/*    */     
/* 52 */     if ((!cell.setSortValue(str)) && (cell.isValid()))
/*    */     {
/* 54 */       return;
/*    */     }
/*    */     
/* 57 */     if (!cell.isShown()) {
/* 58 */       return;
/*    */     }
/*    */     
/* 61 */     cell.setText(str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */