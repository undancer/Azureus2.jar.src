/*    */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.core3.util.SystemTime;
/*    */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
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
/*    */ public class ColumnSearchSubResultAge
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "age";
/*    */   
/*    */   public ColumnSearchSubResultAge(TableColumn column)
/*    */   {
/* 37 */     column.initialize(3, -2, 50);
/* 38 */     column.addListeners(this);
/* 39 */     column.setRefreshInterval(-1);
/* 40 */     column.setType(3);
/*    */     
/* 42 */     if ((column instanceof TableColumnCore)) {
/* 43 */       ((TableColumnCore)column).setUseCoreDataSource(true);
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 48 */     SearchSubsResultBase rc = (SearchSubsResultBase)cell.getDataSource();
/* 49 */     if (rc == null) {
/* 50 */       return;
/*    */     }
/*    */     
/* 53 */     long time = rc.getTime();
/*    */     
/* 55 */     long age_secs = (SystemTime.getCurrentTime() - time) / 1000L;
/*    */     
/* 57 */     if (cell.setSortValue(age_secs))
/*    */     {
/* 59 */       if (time <= 0L) {
/* 60 */         cell.setText("--");
/*    */       } else {
/* 62 */         cell.setToolTip(time <= 0L ? "--" : DisplayFormatters.formatCustomDateOnly(time));
/* 63 */         cell.setText(age_secs < 0L ? "--" : TimeFormatter.format3(age_secs));
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultAge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */