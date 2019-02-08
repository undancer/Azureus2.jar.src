/*    */ package org.gudy.azureus2.ui.swt.views.columnsetup;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColumnTC_Info
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "TableColumnInfo";
/*    */   
/*    */   public ColumnTC_Info(String tableID)
/*    */   {
/* 47 */     super("TableColumnInfo", tableID);
/* 48 */     initialize(5, -1, 150, -3);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 53 */     TableColumnCore column = (TableColumnCore)cell.getDataSource();
/* 54 */     String key = column.getTitleLanguageKey();
/* 55 */     if ((!cell.setSortValue(key)) && (cell.isValid())) {
/* 56 */       return;
/*    */     }
/* 58 */     cell.setText(MessageText.getString(key + ".info", ""));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/columnsetup/ColumnTC_Info.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */