/*     */ package org.gudy.azureus2.ui.swt.views.columnsetup;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellVisibilityListener;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.FakeTableCell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColumnTC_Sample
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener
/*     */ {
/*     */   public static final String COLUMN_ID = "TableColumnSample";
/*     */   
/*     */   public ColumnTC_Sample(String tableID)
/*     */   {
/*  51 */     super("TableColumnSample", tableID);
/*  52 */     setPosition(-1);
/*  53 */     setRefreshInterval(-2);
/*  54 */     setWidth(120);
/*     */   }
/*     */   
/*     */   public void cellAdded(final TableCell cell)
/*     */   {
/*  59 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/*  61 */         if (cell.isDisposed()) {
/*  62 */           return;
/*     */         }
/*  64 */         TableColumnCore column = (TableColumnCore)cell.getDataSource();
/*  65 */         TableViewSWT<?> tv = (TableViewSWT)((TableCellCore)cell).getTableRowCore().getView();
/*  66 */         TableColumnSetupWindow tvs = (TableColumnSetupWindow)tv.getParentDataSource();
/*  67 */         TableRowCore sampleRow = (TableRowCore)tvs.getSampleRow();
/*     */         
/*  69 */         cell.addListeners(new ColumnTC_Sample.Cell(cell, column, tv.getTableComposite(), sampleRow));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static class Cell
/*     */     implements TableCellRefreshListener, TableCellSWTPaintListener, TableCellVisibilityListener, TableCellDisposeListener
/*     */   {
/*     */     private final TableColumnCore column;
/*     */     private FakeTableCell sampleCell;
/*     */     
/*     */     public Cell(TableCell parentCell, TableColumnCore column, Composite c, TableRowCore sampleRow)
/*     */     {
/*  82 */       this.column = column;
/*  83 */       if (sampleRow == null) {
/*  84 */         return;
/*     */       }
/*  86 */       Object ds = sampleRow.getDataSource(true);
/*  87 */       Object pds = sampleRow.getDataSource(false);
/*  88 */       if (column.handlesDataSourceType(pds.getClass())) {
/*  89 */         this.sampleCell = new FakeTableCell(column, ds);
/*     */         
/*  91 */         Rectangle bounds = ((TableCellSWT)parentCell).getBounds();
/*  92 */         this.sampleCell.setControl(c, bounds, false);
/*     */       }
/*     */     }
/*     */     
/*     */     public void dispose(TableCell cell) {
/*  97 */       this.sampleCell = null;
/*     */     }
/*     */     
/*     */ 
/*     */     public void cellPaint(GC gc, TableCellSWT cell)
/*     */     {
/* 103 */       FakeTableCell sampleCell = this.sampleCell;
/*     */       
/* 105 */       if (sampleCell == null) {
/* 106 */         return;
/*     */       }
/* 108 */       Rectangle bounds = cell.getBounds();
/* 109 */       sampleCell.setCellArea(bounds);
/*     */       try {
/* 111 */         sampleCell.refresh();
/* 112 */         sampleCell.doPaint(gc);
/*     */       } catch (Throwable e) {
/* 114 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void cellVisibilityChanged(TableCell cell, int visibility)
/*     */     {
/* 121 */       FakeTableCell sampleCell = this.sampleCell;
/*     */       
/* 123 */       if (sampleCell == null) {
/* 124 */         return;
/*     */       }
/*     */       try {
/* 127 */         this.column.invokeCellVisibilityListeners(sampleCell, visibility);
/*     */       } catch (Throwable e) {
/* 129 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void refresh(TableCell cell)
/*     */     {
/* 136 */       FakeTableCell sampleCell = this.sampleCell;
/*     */       
/* 138 */       if (sampleCell == null) {
/* 139 */         return;
/*     */       }
/* 141 */       if (!cell.isShown()) {
/* 142 */         return;
/*     */       }
/* 144 */       sampleCell.refresh(true, true, true);
/* 145 */       cell.setSortValue(sampleCell.getSortValue());
/* 146 */       cell.invalidate();
/* 147 */       if ((cell instanceof TableCellSWT)) {
/* 148 */         ((TableCellSWT)cell).redraw();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/columnsetup/ColumnTC_Sample.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */