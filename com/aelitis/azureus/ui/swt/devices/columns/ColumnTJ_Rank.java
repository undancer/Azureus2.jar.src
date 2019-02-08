/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*    */ import com.aelitis.azureus.core.devices.TranscodeJob;
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
/*    */ public class ColumnTJ_Rank
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "trancode_qpos";
/*    */   
/*    */   public ColumnTJ_Rank(TableColumn column)
/*    */   {
/* 39 */     column.initialize(2, -2, 25);
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
/* 58 */     TranscodeJob job = tf.getJob();
/*    */     
/*    */     long value;
/* 61 */     if (job == null) {
/*    */       try {
/* 63 */         value = 2147483647L + tf.getCreationDateMillis() + 1L;
/*    */       } catch (Throwable t) {
/* 65 */         long value = 2147483648L;
/*    */       }
/*    */     } else {
/* 68 */       value = job.getIndex();
/*    */     }
/* 70 */     if ((cell.setSortValue(value)) || (!cell.isValid())) {
/* 71 */       if (value > 2147483647L) {
/* 72 */         cell.setText("");
/*    */       } else {
/* 74 */         cell.setText("" + value);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnTJ_Rank.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */