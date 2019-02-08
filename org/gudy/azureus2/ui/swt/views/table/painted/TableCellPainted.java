/*     */ package org.gudy.azureus2.ui.swt.views.table.painted;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableCellSWTBase;
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
/*     */ public class TableCellPainted
/*     */   extends TableCellSWTBase
/*     */ {
/*     */   private static final boolean DEBUG_CELLPAINT = false;
/*     */   private Rectangle bounds;
/*  47 */   private String text = "";
/*     */   
/*     */   private int marginWidth;
/*     */   
/*     */   private int marginHeight;
/*     */   
/*     */   private boolean redrawScheduled;
/*     */   
/*     */   private Color colorFG;
/*     */   
/*     */ 
/*     */   public TableCellPainted(TableRowSWT row, TableColumnCore column, int pos)
/*     */   {
/*  60 */     super(row, column);
/*  61 */     constructionCompleter();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void constructionCompleter()
/*     */   {
/*  67 */     constructionComplete();
/*     */     
/*  69 */     this.tableColumn.invokeCellAddedListeners(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getDataSource()
/*     */   {
/*  81 */     TableRowCore row = this.tableRow;
/*  82 */     TableColumnCore col = this.tableColumn;
/*     */     
/*  84 */     if ((row == null) || (col == null)) {
/*  85 */       return null;
/*     */     }
/*  87 */     return row.getDataSource(col.getUseCoreDataSource());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TableColumn getTableColumn()
/*     */   {
/*  94 */     return this.tableColumn;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TableRow getTableRow()
/*     */   {
/* 101 */     return this.tableRow;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getTableID()
/*     */   {
/* 108 */     return this.tableRow == null ? null : this.tableRow.getTableID();
/*     */   }
/*     */   
/*     */   public static boolean stringEquals(String s0, String s1)
/*     */   {
/* 113 */     boolean s0Null = s0 == null;
/* 114 */     boolean s1Null = s1 == null;
/* 115 */     if ((s0Null) || (s1Null)) {
/* 116 */       return s0Null == s1Null;
/*     */     }
/* 118 */     return s0.equals(s1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getText()
/*     */   {
/* 125 */     if ((hasFlag(2)) && ((this.sortValue instanceof String))) {
/* 126 */       return (String)this.sortValue;
/*     */     }
/*     */     
/* 129 */     return this.text;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Comparable<?> getSortValue()
/*     */   {
/* 136 */     Comparable<?> value = super.getSortValue();
/* 137 */     return value == null ? "" : value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isShown()
/*     */   {
/* 144 */     return (!isDisposed()) && (this.tableRow != null) && (this.tableRow.getView().isColumnVisible(this.tableColumn));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxLines()
/*     */   {
/* 152 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getWidth()
/*     */   {
/* 159 */     if (isDisposed()) {
/* 160 */       return -1;
/*     */     }
/* 162 */     return this.tableColumn.getWidth() - 2 - getMarginWidth() * 2;
/*     */   }
/*     */   
/*     */   public int getWidthRaw() {
/* 166 */     if (isDisposed()) {
/* 167 */       return -1;
/*     */     }
/* 169 */     return this.tableColumn.getWidth() - 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getHeight()
/*     */   {
/* 176 */     if (this.bounds == null) {
/* 177 */       if (this.tableRow == null) {
/* 178 */         return 20;
/*     */       }
/* 180 */       return this.tableRow.getView().getRowDefaultHeight();
/*     */     }
/* 182 */     return this.bounds.height - getMarginHeight() * 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMarginHeight()
/*     */   {
/* 189 */     return this.marginHeight;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMarginHeight(int height)
/*     */   {
/* 196 */     this.marginHeight = height;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMarginWidth()
/*     */   {
/* 203 */     return this.marginWidth;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMarginWidth(int width)
/*     */   {
/* 210 */     this.marginWidth = width;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Graphic getBackgroundGraphic()
/*     */   {
/* 218 */     return new UISWTGraphicImpl(getBackgroundImage());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void locationChanged() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean setCursorID(int cursorID)
/*     */   {
/* 232 */     if (!super.setCursorID(cursorID)) {
/* 233 */       return false;
/*     */     }
/* 235 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 237 */         if ((TableCellPainted.this.isDisposed()) || (TableCellPainted.this.tableRow == null)) {
/* 238 */           return;
/*     */         }
/* 240 */         if (TableCellPainted.this.isMouseOver()) {
/* 241 */           TableViewSWT<?> view = (TableViewSWT)TableCellPainted.this.tableRow.getView();
/* 242 */           if (view != null) {
/* 243 */             Composite composite = view.getComposite();
/* 244 */             if ((composite != null) && (!composite.isDisposed())) {
/* 245 */               composite.setCursor(composite.getDisplay().getSystemCursor(TableCellPainted.this.getCursorID()));
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 251 */     });
/* 252 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void redraw()
/*     */   {
/* 260 */     if ((this.tableRow == null) || (!this.tableRow.isVisible()) || (this.redrawScheduled)) {
/* 261 */       return;
/*     */     }
/* 263 */     this.redrawScheduled = true;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 269 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 272 */         if (TableCellPainted.this.isDisposed()) {
/* 273 */           return;
/*     */         }
/* 275 */         TableCellPainted.this.redrawScheduled = false;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 281 */         if ((TableCellPainted.this.bounds != null) && (TableCellPainted.this.tableRow != null)) {
/* 282 */           TableViewPainted view = (TableViewPainted)TableCellPainted.this.tableRow.getView();
/* 283 */           if (view != null) {
/* 284 */             view.swt_updateCanvasImage(TableCellPainted.this.bounds, false);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean setForeground(Color color)
/*     */   {
/* 293 */     if (isInvisibleAndCanRefresh()) {
/* 294 */       return false;
/*     */     }
/*     */     
/* 297 */     if ((color == this.colorFG) || ((color != null) && (color.equals(this.colorFG))) || ((this.colorFG != null) && (this.colorFG.equals(color))))
/*     */     {
/* 299 */       return false;
/*     */     }
/*     */     
/* 302 */     this.colorFG = color;
/* 303 */     setFlag(64);
/*     */     
/* 305 */     return true;
/*     */   }
/*     */   
/*     */   public Point getSize() {
/* 309 */     if (this.bounds == null) {
/* 310 */       return new Point(0, 0);
/*     */     }
/* 312 */     return new Point(this.bounds.width - this.marginWidth * 2, this.bounds.height - this.marginHeight * 2);
/*     */   }
/*     */   
/*     */   public Rectangle getBounds()
/*     */   {
/* 317 */     if (this.bounds == null) {
/* 318 */       return new Rectangle(0, 0, 0, 0);
/*     */     }
/* 320 */     return new Rectangle(this.bounds.x + this.marginWidth, this.bounds.y + this.marginHeight, this.bounds.width - this.marginWidth * 2, this.bounds.height - this.marginHeight * 2);
/*     */   }
/*     */   
/*     */   public Rectangle getBoundsRaw()
/*     */   {
/* 325 */     if (this.bounds == null) {
/* 326 */       return null;
/*     */     }
/* 328 */     return new Rectangle(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height);
/*     */   }
/*     */   
/*     */   public Rectangle getBoundsOnDisplay() {
/* 332 */     if ((isDisposed()) || (this.tableRow == null)) {
/* 333 */       return null;
/*     */     }
/* 335 */     Rectangle bounds = getBoundsRaw();
/* 336 */     if (bounds == null) {
/* 337 */       return null;
/*     */     }
/* 339 */     TableViewPainted tv = (TableViewPainted)this.tableRow.getView();
/* 340 */     if (tv == null) {
/* 341 */       return null;
/*     */     }
/* 343 */     Composite c = tv.getTableComposite();
/* 344 */     if ((c == null) || (c.isDisposed())) {
/* 345 */       return null;
/*     */     }
/* 347 */     Point pt = c.toDisplay(bounds.x, bounds.y);
/* 348 */     bounds.x = pt.x;
/* 349 */     bounds.y = pt.y;
/* 350 */     bounds.height = getHeight();
/* 351 */     bounds.width = getWidthRaw();
/* 352 */     return bounds;
/*     */   }
/*     */   
/*     */   public Image getBackgroundImage() {
/* 356 */     if ((this.bounds == null) || (this.bounds.isEmpty())) {
/* 357 */       return null;
/*     */     }
/*     */     
/* 360 */     Image image = new Image(Display.getDefault(), this.bounds.width - this.marginWidth * 2, this.bounds.height - this.marginHeight * 2);
/*     */     
/*     */ 
/* 363 */     GC gc = new GC(image);
/* 364 */     gc.setForeground(getBackgroundSWT());
/* 365 */     gc.setBackground(getBackgroundSWT());
/* 366 */     gc.fillRectangle(0, 0, this.bounds.width, this.bounds.height);
/* 367 */     gc.dispose();
/*     */     
/* 369 */     return image;
/*     */   }
/*     */   
/*     */   public Color getForegroundSWT() {
/* 373 */     return this.colorFG;
/*     */   }
/*     */   
/*     */   public Color getBackgroundSWT() {
/* 377 */     return null;
/*     */   }
/*     */   
/*     */   public void setBoundsRaw(Rectangle bounds) {
/* 381 */     this.bounds = bounds;
/*     */   }
/*     */   
/*     */   public boolean uiSetText(String text)
/*     */   {
/* 386 */     boolean bChanged = !stringEquals(this.text, text);
/* 387 */     if (bChanged) {
/* 388 */       this.text = text;
/*     */     }
/* 390 */     return bChanged;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/painted/TableCellPainted.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */