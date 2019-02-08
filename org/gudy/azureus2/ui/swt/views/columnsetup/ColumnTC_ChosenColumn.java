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
/*    */ public class ColumnTC_ChosenColumn
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "TableColumnChosenColumn";
/*    */   
/*    */   public ColumnTC_ChosenColumn(String tableID)
/*    */   {
/* 46 */     super("TableColumnChosenColumn", tableID);
/* 47 */     initialize(5, -1, 175, -3);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 52 */     TableColumnCore column = (TableColumnCore)cell.getDataSource();
/* 53 */     int colPos = column.getPosition();
/*    */     
/* 55 */     if ((!cell.setSortValue(colPos)) && (cell.isValid())) {
/* 56 */       return;
/*    */     }
/* 58 */     String key = column.getTitleLanguageKey();
/* 59 */     String s = MessageText.getString(key, column.getName());
/*    */     
/* 61 */     cell.setText(s);
/* 62 */     String info = MessageText.getString(key + ".info", "");
/* 63 */     cell.setToolTip(info);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/columnsetup/ColumnTC_ChosenColumn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */