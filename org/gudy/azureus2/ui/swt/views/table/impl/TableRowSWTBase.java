/*     */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TableRowSWTBase
/*     */   implements TableRowSWT
/*     */ {
/*  45 */   public static boolean DEBUG_ROW_PAINT = false;
/*     */   
/*  47 */   private static final boolean expand_enabled = COConfigurationManager.getBooleanParameter("Table.useTree");
/*     */   
/*     */ 
/*     */   protected Object lock;
/*     */   
/*     */   private final TableViewSWT tv;
/*     */   
/*     */   private final TableRowCore parentRow;
/*     */   
/*     */   private final Object coreDataSource;
/*     */   
/*  58 */   private int lastIndex = -1;
/*     */   
/*     */   protected Map<String, TableCellCore> mTableCells;
/*     */   
/*     */   private boolean bDisposed;
/*     */   
/*     */   private Object pluginDataSource;
/*     */   
/*  66 */   protected boolean wasShown = false;
/*     */   
/*     */   private boolean bSetNotUpToDateLastRefresh;
/*     */   
/*     */   private ArrayList<TableRowMouseListener> mouseListeners;
/*     */   
/*     */   private Map<String, Object> dataList;
/*     */   
/*  74 */   private int alpha = 255;
/*     */   
/*     */   private int fontStyle;
/*     */   
/*     */   private boolean expanded;
/*     */   
/*     */ 
/*     */   public TableRowSWTBase(Object lock, TableRowCore parentRow, TableViewSWT tv, Object dataSource)
/*     */   {
/*  83 */     this.lock = lock;
/*  84 */     this.parentRow = parentRow;
/*  85 */     this.tv = tv;
/*  86 */     this.coreDataSource = dataSource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  93 */   public void invalidate() { invalidate(false); }
/*     */   
/*     */   public void invalidate(boolean mustRefersh) {
/*  96 */     synchronized (this.lock) {
/*  97 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/*  98 */         return;
/*     */       }
/*     */       
/* 101 */       for (TableCellCore cell : this.mTableCells.values()) {
/* 102 */         if (cell != null) {
/* 103 */           cell.invalidate(mustRefersh);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean doesAnyCellHaveFlag(int flag) {
/* 110 */     synchronized (this.lock) {
/* 111 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 112 */         return false;
/*     */       }
/*     */       
/* 115 */       for (TableCellCore cell : this.mTableCells.values()) {
/* 116 */         if (((cell instanceof TableCellSWTBase)) && (((TableCellSWTBase)cell).hasFlag(flag)))
/*     */         {
/* 118 */           return true;
/*     */         }
/*     */       }
/* 121 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCellFlag(int flag)
/*     */   {
/* 127 */     synchronized (this.lock) {
/* 128 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 129 */         return;
/*     */       }
/*     */       
/* 132 */       for (TableCellCore cell : this.mTableCells.values()) {
/* 133 */         if (cell != null) {
/* 134 */           ((TableCellSWTBase)cell).setFlag(flag);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearCellFlag(int flag, boolean subRows) {
/* 141 */     synchronized (this.lock) {
/* 142 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 143 */         return;
/*     */       }
/*     */       
/* 146 */       for (TableCellCore cell : this.mTableCells.values()) {
/* 147 */         if (cell != null) {
/* 148 */           ((TableCellSWTBase)cell).clearFlag(flag);
/*     */         }
/*     */       }
/* 151 */       if (subRows) {
/* 152 */         TableRowCore[] subRowsWithNull = getSubRowsWithNull();
/* 153 */         for (TableRowCore row : subRowsWithNull) {
/* 154 */           ((TableRowSWTBase)row).clearCellFlag(flag, false);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void delete()
/*     */   {
/* 164 */     synchronized (this.lock)
/*     */     {
/* 166 */       if (this.bDisposed) {
/* 167 */         return;
/*     */       }
/*     */       
/* 170 */       if (this.mTableCells != null) {
/* 171 */         for (TableCellCore cell : this.mTableCells.values()) {
/*     */           try {
/* 173 */             if ((cell != null) && (!cell.isDisposed())) {
/* 174 */               cell.dispose();
/*     */             }
/*     */           } catch (Exception e) {
/* 177 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 182 */       setHeight(0);
/*     */       
/* 184 */       this.bDisposed = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List refresh(boolean bDoGraphics)
/*     */   {
/* 192 */     if (this.bDisposed) {
/* 193 */       return Collections.EMPTY_LIST;
/*     */     }
/*     */     
/* 196 */     boolean bVisible = isVisible();
/*     */     
/* 198 */     return refresh(bDoGraphics, bVisible);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void locationChanged(int iStartColumn)
/*     */   {
/* 205 */     if ((this.bDisposed) || (!isVisible())) {
/* 206 */       return;
/*     */     }
/* 208 */     synchronized (this.lock) {
/* 209 */       if (this.mTableCells == null) {
/* 210 */         return;
/*     */       }
/*     */       
/* 213 */       for (TableCellCore cell : this.mTableCells.values()) {
/* 214 */         if ((cell != null) && (cell.getTableColumn().getPosition() > iStartColumn)) {
/* 215 */           cell.locationChanged();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
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
/*     */   public Object getDataSource(boolean bCoreObject)
/*     */   {
/* 232 */     if (bCoreObject) {
/* 233 */       return this.coreDataSource;
/*     */     }
/*     */     
/* 236 */     if (this.pluginDataSource != null) {
/* 237 */       return this.pluginDataSource;
/*     */     }
/*     */     
/* 240 */     this.pluginDataSource = PluginCoreUtils.convert(this.coreDataSource, bCoreObject);
/*     */     
/* 242 */     return this.pluginDataSource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 249 */     if (this.bDisposed) {
/* 250 */       return -1;
/*     */     }
/*     */     
/* 253 */     if (this.lastIndex >= 0) {
/* 254 */       if (this.parentRow != null) {
/* 255 */         return this.lastIndex;
/*     */       }
/* 257 */       TableRowCore row = this.tv.getRowQuick(this.lastIndex);
/* 258 */       if (row == this) {
/* 259 */         return this.lastIndex;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 265 */     return this.tv.indexOf(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TableCellCore getTableCellCore(String name)
/*     */   {
/* 272 */     synchronized (this.lock) {
/* 273 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 274 */         return null;
/*     */       }
/*     */       
/* 277 */       return (TableCellCore)this.mTableCells.get(name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isVisible()
/*     */   {
/* 285 */     return this.tv.isRowVisible(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setTableItem(int newIndex)
/*     */   {
/* 292 */     return setTableItem(newIndex, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setTableItem(int newIndex, boolean isVisible)
/*     */   {
/* 299 */     if (this.bDisposed) {
/* 300 */       System.out.println("XXX setTI: bDisposed from " + Debug.getCompressedStackTrace());
/*     */       
/* 302 */       return false;
/*     */     }
/* 304 */     boolean changedIndex = this.lastIndex != newIndex;
/* 305 */     if (changedIndex)
/*     */     {
/* 307 */       this.lastIndex = newIndex;
/*     */     }
/*     */     
/* 310 */     return changedIndex;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSelected(boolean selected)
/*     */   {
/* 317 */     TableView tableView = getView();
/* 318 */     if ((tableView instanceof TableViewSWT)) {
/* 319 */       ((TableViewSWT)tableView).setRowSelected(this, selected, true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isRowDisposed()
/*     */   {
/* 327 */     return this.bDisposed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUpToDate(boolean upToDate)
/*     */   {
/* 334 */     synchronized (this.lock) {
/* 335 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 336 */         return;
/*     */       }
/*     */       
/* 339 */       for (TableCellCore cell : this.mTableCells.values()) {
/* 340 */         if (cell != null) {
/* 341 */           cell.setUpToDate(upToDate);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<TableCellCore> refresh(boolean bDoGraphics, boolean bVisible)
/*     */   {
/* 353 */     List<TableCellCore> list = Collections.EMPTY_LIST;
/*     */     
/* 355 */     if (this.bDisposed) {
/* 356 */       return list;
/*     */     }
/*     */     
/* 359 */     if (!bVisible) {
/* 360 */       if (!this.bSetNotUpToDateLastRefresh) {
/* 361 */         setUpToDate(false);
/* 362 */         this.bSetNotUpToDateLastRefresh = true;
/*     */       }
/* 364 */       return list;
/*     */     }
/*     */     
/* 367 */     this.bSetNotUpToDateLastRefresh = false;
/*     */     
/*     */ 
/*     */ 
/* 371 */     this.tv.invokeRefreshListeners(this);
/*     */     
/*     */ 
/* 374 */     Collection<TableCellCore> lTableCells = null;
/* 375 */     synchronized (this.lock) {
/* 376 */       if (this.mTableCells != null) {
/* 377 */         lTableCells = new ArrayList(this.mTableCells.values());
/*     */       }
/*     */     }
/*     */     
/* 381 */     if (lTableCells != null) {
/* 382 */       for (TableCellCore cell : lTableCells) {
/* 383 */         if ((cell != null) && (!cell.isDisposed()))
/*     */         {
/*     */ 
/* 386 */           TableColumn column = cell.getTableColumn();
/*     */           
/* 388 */           if ((column == this.tv.getSortColumn()) || (this.tv.isColumnVisible(column)))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 393 */             boolean cellVisible = (bVisible) && (cell.isShown());
/* 394 */             boolean changed = cell.refresh(bDoGraphics, bVisible, cellVisible);
/* 395 */             if (changed) {
/* 396 */               if (list == Collections.EMPTY_LIST) {
/* 397 */                 list = new ArrayList(lTableCells.size());
/*     */               }
/* 399 */               list.add(cell);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 406 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TableView getView()
/*     */   {
/* 413 */     return this.tv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addMouseListener(TableRowMouseListener listener)
/*     */   {
/* 420 */     synchronized (this.lock)
/*     */     {
/* 422 */       if (this.mouseListeners == null) {
/* 423 */         this.mouseListeners = new ArrayList(1);
/*     */       }
/*     */       
/* 426 */       this.mouseListeners.add(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeMouseListener(TableRowMouseListener listener)
/*     */   {
/* 435 */     synchronized (this.lock)
/*     */     {
/* 437 */       if (this.mouseListeners == null) {
/* 438 */         return;
/*     */       }
/*     */       
/* 441 */       this.mouseListeners.remove(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void invokeMouseListeners(TableRowMouseEvent event)
/*     */   {
/*     */     ArrayList<TableRowMouseListener> listeners;
/*     */     
/*     */ 
/* 452 */     synchronized (this.lock) { ArrayList<TableRowMouseListener> listeners;
/* 453 */       if (this.mouseListeners == null) {
/* 454 */         listeners = null;
/*     */       } else {
/* 456 */         listeners = new ArrayList(this.mouseListeners);
/*     */       }
/*     */     }
/*     */     
/* 460 */     if (listeners == null) {
/* 461 */       return;
/*     */     }
/*     */     
/* 464 */     for (int i = 0; i < listeners.size(); i++) {
/*     */       try {
/* 466 */         TableRowMouseListener l = (TableRowMouseListener)listeners.get(i);
/*     */         
/* 468 */         l.rowMouseTrigger(event);
/*     */       }
/*     */       catch (Throwable e) {
/* 471 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isMouseOver()
/*     */   {
/* 480 */     return this.tv.getTableRowWithCursor() == this;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canExpand()
/*     */   {
/* 486 */     return expand_enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isExpanded()
/*     */   {
/* 493 */     return this.expanded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExpanded(boolean b)
/*     */   {
/* 500 */     if (canExpand())
/*     */     {
/* 502 */       if (this.expanded != b)
/*     */       {
/* 504 */         this.expanded = b;
/*     */         
/* 506 */         this.tv.invokeExpansionChangeListeners(this, b);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TableRowCore getParentRowCore()
/*     */   {
/* 515 */     return this.parentRow;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInPaintItem()
/*     */   {
/* 522 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getDataSource()
/*     */   {
/* 529 */     return getDataSource(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getTableID()
/*     */   {
/* 536 */     return this.tv.getTableID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean setForeground(Color paramColor);
/*     */   
/*     */ 
/*     */ 
/*     */   public void setForeground(int red, int green, int blue)
/*     */   {
/* 548 */     setForeground2(red, green, blue);
/*     */   }
/*     */   
/*     */   public boolean setForeground2(int red, int green, int blue) {
/* 552 */     if ((red < 0) || (green < 0) || (blue < 0)) {
/* 553 */       return setForeground((Color)null);
/*     */     }
/* 555 */     return setForeground(new RGB(red, green, blue));
/*     */   }
/*     */   
/*     */   private boolean setForeground(final RGB rgb) {
/* 559 */     Color colorFG = getForeground();
/* 560 */     boolean changed = (colorFG == null) || (colorFG.isDisposed()) || (!colorFG.getRGB().equals(rgb));
/*     */     
/* 562 */     if (changed) {
/* 563 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 565 */           TableRowSWTBase.this.setForeground(ColorCache.getColor(Display.getCurrent(), rgb));
/*     */         }
/*     */       });
/*     */     }
/* 569 */     return changed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setForeground(int[] rgb)
/*     */   {
/* 576 */     setForeground2(rgb);
/*     */   }
/*     */   
/*     */   public boolean setForeground2(int[] rgb) {
/* 580 */     if ((rgb == null) || (rgb.length < 3)) {
/* 581 */       return setForeground((Color)null);
/*     */     }
/* 583 */     return setForeground2(rgb[0], rgb[1], rgb[2]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setForegroundToErrorColor()
/*     */   {
/* 590 */     setForeground(Colors.colorError);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 597 */     synchronized (this.lock) {
/* 598 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 599 */         return true;
/*     */       }
/*     */       
/* 602 */       boolean valid = true;
/* 603 */       for (TableCell cell : this.mTableCells.values()) {
/* 604 */         if ((cell != null) && (cell.isValid())) {
/* 605 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 609 */       return valid;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TableCell getTableCell(String field)
/*     */   {
/* 617 */     synchronized (this.lock) {
/* 618 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 619 */         return null;
/*     */       }
/*     */       
/* 622 */       return (TableCell)this.mTableCells.get(field);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSelected()
/*     */   {
/* 630 */     return getView().isSelected(this);
/*     */   }
/*     */   
/*     */   public boolean isFocused() {
/* 634 */     return getView().getFocusedRow() == this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getData(String id)
/*     */   {
/* 641 */     synchronized (this) {
/* 642 */       return this.dataList == null ? null : this.dataList.get(id);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setData(String id, Object data)
/*     */   {
/* 650 */     synchronized (this) {
/* 651 */       if (this.dataList == null) {
/* 652 */         this.dataList = new HashMap(1);
/*     */       }
/* 654 */       if (data == null) {
/* 655 */         this.dataList.remove(id);
/*     */       } else {
/* 657 */         this.dataList.put(id, data);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean setIconSize(Point paramPoint);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Color getForeground();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Color getBackground();
/*     */   
/*     */ 
/*     */ 
/*     */   public TableCellSWT getTableCellSWT(String name)
/*     */   {
/* 681 */     synchronized (this.lock) {
/* 682 */       if ((this.bDisposed) || (this.mTableCells == null)) {
/* 683 */         return null;
/*     */       }
/*     */       
/* 686 */       TableCellCore cell = (TableCellCore)this.mTableCells.get(name);
/* 687 */       if ((cell instanceof TableCellSWT)) {
/* 688 */         return (TableCellSWT)cell;
/*     */       }
/* 690 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Rectangle getBounds();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void setBackgroundImage(Image paramImage);
/*     */   
/*     */ 
/*     */ 
/*     */   public int getFontStyle()
/*     */   {
/* 708 */     return this.fontStyle;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setFontStyle(int style)
/*     */   {
/* 715 */     if (this.fontStyle == style) {
/* 716 */       return false;
/*     */     }
/*     */     
/* 719 */     this.fontStyle = style;
/* 720 */     invalidate();
/*     */     
/* 722 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getAlpha()
/*     */   {
/* 729 */     return this.alpha;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setAlpha(int alpha)
/*     */   {
/* 736 */     if (alpha == this.alpha) {
/* 737 */       return false;
/*     */     }
/* 739 */     this.alpha = alpha;
/* 740 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract void setWidgetSelected(boolean paramBoolean);
/*     */   
/*     */ 
/*     */   public boolean isShown()
/*     */   {
/* 749 */     return this.wasShown;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setShown(boolean b, boolean force)
/*     */   {
/* 756 */     if (this.bDisposed) {
/* 757 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 761 */     if ((b == this.wasShown) && (!force)) {
/* 762 */       return false;
/*     */     }
/* 764 */     this.wasShown = b;
/*     */     
/* 766 */     Collection<TableCellCore> lTableCells = null;
/* 767 */     synchronized (this.lock) {
/* 768 */       if (this.mTableCells != null) {
/* 769 */         lTableCells = new ArrayList(this.mTableCells.values());
/*     */       }
/*     */     }
/*     */     
/* 773 */     if (lTableCells != null) {
/* 774 */       for (TableCellCore cell : lTableCells) {
/* 775 */         if (cell != null) {
/* 776 */           cell.invokeVisibilityListeners(b ? 0 : 1, true);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 783 */     return true;
/*     */   }
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
/*     */   public void redraw()
/*     */   {
/* 798 */     redraw(false);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/TableRowSWTBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */