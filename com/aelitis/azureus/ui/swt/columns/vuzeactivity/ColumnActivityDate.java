/*    */ package com.aelitis.azureus.ui.swt.columns.vuzeactivity;
/*    */ 
/*    */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*    */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*    */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*    */ public class ColumnActivityDate
/*    */   extends ColumnDateSizer
/*    */   implements TableCellAddedListener
/*    */ {
/*    */   public static final String COLUMN_ID = "activityDate";
/*    */   
/*    */   public ColumnActivityDate(String tableID)
/*    */   {
/* 48 */     super(null, "activityDate", TableColumnCreator.DATE_COLUMN_WIDTH, tableID);
/*    */     
/* 50 */     setMultiline(false);
/*    */   }
/*    */   
/*    */   public void cellAdded(TableCell cell)
/*    */   {
/* 55 */     if ((cell instanceof TableCellSWT)) {
/* 56 */       ((TableCellSWT)cell).setTextAlpha(120);
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell, long timestamp)
/*    */   {
/* 62 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/* 63 */     timestamp = entry.getTimestamp();
/*    */     
/* 65 */     super.refresh(cell, timestamp);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/vuzeactivity/ColumnActivityDate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */