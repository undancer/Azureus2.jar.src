/*     */ package com.aelitis.azureus.ui.swt.columns;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
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
/*     */ public abstract class ColumnCheckBox
/*     */   implements TableCellRefreshListener, TableColumnExtraInfoListener, TableCellMouseListener
/*     */ {
/*  50 */   private static final UISWTGraphic tick_icon = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("check_yes"));
/*  51 */   private static final UISWTGraphic tick_ro_icon = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("check_ro_yes"));
/*  52 */   private static final UISWTGraphic cross_icon = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("check_no"));
/*     */   
/*     */ 
/*     */   private boolean read_only;
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  61 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*     */ 
/*  65 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnCheckBox(TableColumn column, int width, boolean read_only)
/*     */   {
/*  74 */     this.read_only = read_only;
/*     */     
/*  76 */     column.setWidth(width);
/*  77 */     column.setType(2);
/*  78 */     column.addListeners(this);
/*     */     
/*  80 */     if (read_only) {
/*  81 */       column.removeCellMouseListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ColumnCheckBox(TableColumn column)
/*     */   {
/*  89 */     this(column, 40, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnCheckBox(TableColumn column, int width)
/*     */   {
/*  97 */     this(column, width, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract Boolean getCheckBoxState(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void setCheckBoxState(Object paramObject, boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 113 */     if (event.eventType == 1)
/*     */     {
/* 115 */       TableCell cell = event.cell;
/*     */       
/* 117 */       int event_x = event.x;
/* 118 */       int event_y = event.y;
/* 119 */       int cell_width = cell.getWidth();
/* 120 */       int cell_height = cell.getHeight();
/*     */       
/* 122 */       Rectangle icon_bounds = tick_icon.getImage().getBounds();
/*     */       
/* 124 */       int x_pad = (cell_width - icon_bounds.width) / 2;
/* 125 */       int y_pad = (cell_height - icon_bounds.height) / 2;
/*     */       
/* 127 */       if ((event_x >= x_pad) && (event_x <= cell_width - x_pad) && (event_y >= y_pad) && (event_y <= cell_height - y_pad))
/*     */       {
/*     */ 
/* 130 */         Object datasource = cell.getDataSource();
/*     */         
/* 132 */         Boolean state = getCheckBoxState(datasource);
/*     */         
/* 134 */         if (state != null)
/*     */         {
/* 136 */           setCheckBoxState(datasource, !state.booleanValue());
/*     */           
/* 138 */           cell.invalidate();
/*     */           
/* 140 */           if ((cell instanceof TableCellCore))
/*     */           {
/* 142 */             ((TableCellCore)cell).refresh(true);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 153 */     Object dataSource = cell.getDataSource();
/* 154 */     Boolean state = getCheckBoxState(dataSource);
/*     */     
/* 156 */     long sortVal = 0L;
/* 157 */     UISWTGraphic icon = null;
/*     */     
/* 159 */     if (state != null)
/*     */     {
/* 161 */       if (state.booleanValue())
/*     */       {
/* 163 */         sortVal = 2L;
/* 164 */         icon = this.read_only ? tick_ro_icon : tick_icon;
/*     */       }
/*     */       else
/*     */       {
/* 168 */         sortVal = 1L;
/* 169 */         icon = this.read_only ? null : cross_icon;
/*     */       }
/*     */     }
/*     */     
/* 173 */     sortVal = adjustSortVal(dataSource, sortVal);
/*     */     
/* 175 */     if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/* 176 */       return;
/*     */     }
/*     */     
/* 179 */     if (!cell.isShown()) {
/* 180 */       return;
/*     */     }
/*     */     
/* 183 */     if (cell.getGraphic() != icon)
/*     */     {
/* 185 */       cell.setGraphic(icon);
/*     */     }
/*     */   }
/*     */   
/*     */   public long adjustSortVal(Object ds, long sortVal) {
/* 190 */     return sortVal;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/ColumnCheckBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */