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
/*    */ public class ColumnSearchSubResultRatings
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 29 */   public static String COLUMN_ID = "ratings";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 33 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 36 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnSearchSubResultRatings(TableColumn column)
/*    */   {
/* 41 */     column.initialize(3, -2, 80);
/* 42 */     column.setRefreshInterval(-3);
/* 43 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 47 */     SearchSubsResultBase result = (SearchSubsResultBase)cell.getDataSource();
/*    */     
/* 49 */     long sort = result.getVotesCommentsSortValue();
/*    */     
/* 51 */     if ((!cell.setSortValue(sort)) && (cell.isValid()))
/*    */     {
/* 53 */       return;
/*    */     }
/*    */     
/* 56 */     if (!cell.isShown()) {
/* 57 */       return;
/*    */     }
/*    */     
/* 60 */     String str = result.getVotesComments();
/*    */     
/* 62 */     cell.setText(str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultRatings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */