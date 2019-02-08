/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.DeviceOfflineDownload;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColumnOD_Remaining
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "od_remaining";
/*    */   
/*    */   public ColumnOD_Remaining(TableColumn column)
/*    */   {
/* 40 */     column.initialize(3, -2, 80);
/* 41 */     column.addListeners(this);
/* 42 */     column.setRefreshInterval(-1);
/* 43 */     column.setType(3);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 47 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 50 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 54 */     DeviceOfflineDownload od = (DeviceOfflineDownload)cell.getDataSource();
/* 55 */     if (od == null) {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     long remaining = od.getRemaining();
/*    */     
/* 61 */     cell.setText(remaining == 0L ? "" : DisplayFormatters.formatByteCountToKiBEtc(remaining));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnOD_Remaining.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */