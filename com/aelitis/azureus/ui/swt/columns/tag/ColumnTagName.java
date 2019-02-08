/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
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
/*    */ public class ColumnTagName
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 27 */   public static String COLUMN_ID = "tag.name";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 30 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 33 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagName(TableColumn column)
/*    */   {
/* 38 */     column.setWidth(160);
/* 39 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 43 */     Tag tag = (Tag)cell.getDataSource();
/* 44 */     String tagName = null;
/* 45 */     if (tag != null) {
/* 46 */       tagName = tag.getTagName(true);
/*    */     }
/*    */     
/* 49 */     if (tagName == null) {
/* 50 */       tagName = "";
/*    */     }
/*    */     
/* 53 */     String desc = tag == null ? null : tag.getDescription();
/*    */     
/* 55 */     if (desc != null)
/*    */     {
/* 57 */       cell.setToolTip(desc);
/*    */     }
/*    */     
/* 60 */     if ((!cell.setSortValue(tagName)) && (cell.isValid())) {
/* 61 */       return;
/*    */     }
/*    */     
/* 64 */     if (!cell.isShown()) {
/* 65 */       return;
/*    */     }
/*    */     
/* 68 */     cell.setText(tagName);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */