/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagType;
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
/*    */ public class ColumnTagType
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 28 */   public static String COLUMN_ID = "tag.type";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 31 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 34 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagType(TableColumn column)
/*    */   {
/* 39 */     column.setWidth(120);
/* 40 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 44 */     Tag tag = (Tag)cell.getDataSource();
/* 45 */     TagType sortVal = null;
/* 46 */     if (tag != null) {
/* 47 */       sortVal = tag.getTagType();
/*    */     }
/*    */     
/*    */ 
/* 51 */     if ((!cell.setSortValue(sortVal == null ? 0L : sortVal.getTagType())) && (cell.isValid())) {
/* 52 */       return;
/*    */     }
/*    */     
/* 55 */     if (!cell.isShown()) {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     cell.setText(sortVal == null ? "" : sortVal.getTagTypeName(true));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */