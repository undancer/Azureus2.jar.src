/*    */ package com.aelitis.azureus.ui.swt.devices.columns;
/*    */ 
/*    */ import com.aelitis.azureus.core.devices.TranscodeFile;
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
/*    */ public class ColumnTJ_Category
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/*    */   public static final String COLUMN_ID = "category";
/*    */   
/*    */   public ColumnTJ_Category(TableColumn column)
/*    */   {
/* 38 */     column.initialize(1, -1, 80);
/* 39 */     column.addListeners(this);
/* 40 */     column.setRefreshInterval(-2);
/* 41 */     column.setType(3);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 45 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 48 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 53 */     TranscodeFile tf = (TranscodeFile)cell.getDataSource();
/* 54 */     if (tf == null) {
/* 55 */       return;
/*    */     }
/*    */     
/* 58 */     String[] cats = tf.getCategories();
/*    */     
/* 60 */     String str = "";
/*    */     
/* 62 */     for (String cat : cats)
/*    */     {
/* 64 */       str = str + (str.length() == 0 ? "" : ",") + cat;
/*    */     }
/*    */     
/* 67 */     cell.setText(str);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnTJ_Category.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */