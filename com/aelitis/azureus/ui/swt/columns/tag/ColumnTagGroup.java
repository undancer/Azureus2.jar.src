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
/*    */ public class ColumnTagGroup
/*    */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*    */ {
/* 38 */   public static String COLUMN_ID = "tag.group";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 41 */     info.addCategories(new String[] { "content" });
/*    */     
/*    */ 
/* 44 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagGroup(TableColumn column)
/*    */   {
/* 49 */     column.setWidth(70);
/* 50 */     column.setRefreshInterval(-2);
/* 51 */     column.setPosition(-1);
/* 52 */     column.addListeners(this);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 56 */     Tag tag = (Tag)cell.getDataSource();
/*    */     
/* 58 */     String text = null;
/*    */     
/* 60 */     if (tag != null)
/*    */     {
/* 62 */       text = tag.getGroup();
/*    */     }
/*    */     
/* 65 */     if (text == null)
/*    */     {
/* 67 */       text = "";
/*    */     }
/*    */     
/* 70 */     if ((!cell.setSortValue(text)) && (cell.isValid()))
/*    */     {
/* 72 */       return;
/*    */     }
/*    */     
/* 75 */     if (!cell.isShown())
/*    */     {
/* 77 */       return;
/*    */     }
/*    */     
/* 80 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */