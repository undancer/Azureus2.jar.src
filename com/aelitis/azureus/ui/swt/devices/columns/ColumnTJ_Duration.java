/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*    */ import org.gudy.azureus2.core3.util.TimeFormatter;
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
/*    */ public class ColumnTJ_Duration
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "duration";
/*    */   
/*    */   public ColumnTJ_Duration(TableColumn column)
/*    */   {
/* 39 */     column.initialize(2, -2, 85);
/* 40 */     column.addListeners(this);
/* 41 */     column.setRefreshInterval(-1);
/* 42 */     column.setType(3);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 46 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 49 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 54 */     TranscodeFile tf = (TranscodeFile)cell.getDataSource();
/* 55 */     if (tf == null) {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     long duration = tf.getDurationMillis();
/*    */     
/* 61 */     cell.setText(duration == 0L ? "" : TimeFormatter.format(duration / 1000L));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnTJ_Duration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */