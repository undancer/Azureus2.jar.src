/*    */ package com.aelitis.azureus.ui.swt.columns.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*    */ import org.eclipse.swt.graphics.Color;
/*    */ import org.eclipse.swt.graphics.GC;
/*    */ import org.eclipse.swt.graphics.Rectangle;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
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
/*    */ public class ColumnTagColor
/*    */   implements TableCellRefreshListener, TableCellSWTPaintListener, TableColumnExtraInfoListener
/*    */ {
/* 34 */   public static String COLUMN_ID = "tag.color";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 37 */     info.addCategories(new String[] { "essential" });
/*    */     
/*    */ 
/* 40 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public ColumnTagColor(TableColumn column)
/*    */   {
/* 45 */     column.setWidth(30);
/* 46 */     column.addListeners(this);
/*    */     
/* 48 */     if ((column instanceof TableColumnCore)) {
/* 49 */       ((TableColumnCore)column).addCellOtherListener("SWTPaint", this);
/*    */     }
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 54 */     Tag tag = (Tag)cell.getDataSource();
/* 55 */     if (tag == null) {
/* 56 */       return;
/*    */     }
/*    */     
/* 59 */     int[] color = tag.getColor();
/*    */     
/* 61 */     if ((color == null) || (color.length < 3)) {
/* 62 */       return;
/*    */     }
/*    */     
/* 65 */     int sortVal = color[0] + color[1] << 8 + color[2] << 16;
/*    */     
/* 67 */     if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/* 68 */       return;
/*    */     }
/*    */     
/* 71 */     if (!cell.isShown()) {
/* 72 */       return;
/*    */     }
/*    */     
/* 75 */     cell.setForeground(color);
/*    */   }
/*    */   
/*    */   public void cellPaint(GC gc, TableCellSWT cell)
/*    */   {
/* 80 */     Rectangle bounds = cell.getBounds();
/* 81 */     Color foregroundSWT = cell.getForegroundSWT();
/* 82 */     if (foregroundSWT != null) {
/* 83 */       gc.setBackground(foregroundSWT);
/* 84 */       bounds.x += 1;
/* 85 */       bounds.y += 1;
/* 86 */       bounds.width -= 1;
/* 87 */       bounds.height -= 1;
/* 88 */       gc.fillRectangle(bounds);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagColor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */