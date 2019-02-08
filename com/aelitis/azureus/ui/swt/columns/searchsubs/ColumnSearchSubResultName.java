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
/*    */ public class ColumnSearchSubResultName
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 29 */   public static String COLUMN_ID = "name";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 33 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 36 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnSearchSubResultName(TableColumn column)
/*    */   {
/* 41 */     column.initialize(1, -2, 400);
/* 42 */     column.setRefreshInterval(-3);
/* 43 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 47 */     SearchSubsResultBase result = (SearchSubsResultBase)cell.getDataSource();
/*    */     
/* 49 */     String str = result.getName();
/*    */     
/* 51 */     if ((!cell.setSortValue(str)) && (cell.isValid()))
/*    */     {
/* 53 */       return;
/*    */     }
/*    */     
/* 56 */     if (!cell.isShown()) {
/* 57 */       return;
/*    */     }
/*    */     
/* 60 */     cell.setText(str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */