/*      */ package org.gudy.azureus2.ui.swt.views.table.painted;
/*      */ 
/*      */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*      */ import java.io.PrintStream;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableCellSWTBase;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableRowSWTBase;
/*      */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnSWTUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TableRowPainted
/*      */   extends TableRowSWTBase
/*      */ {
/*      */   private static final boolean DEBUG_SUBS = false;
/*   52 */   private Point drawOffset = new Point(0, 0);
/*      */   
/*      */   private int numSubItems;
/*      */   
/*      */   private Object[] subDataSources;
/*      */   
/*      */   private TableRowPainted[] subRows;
/*      */   
/*      */   private Object subRows_sync;
/*      */   
/*      */   private int subRowsHeight;
/*      */   
/*      */   private TableCellCore cellSort;
/*      */   
/*   66 */   public static final Color[] alternatingColors = { null, Colors.colorAltRow };
/*      */   
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*   72 */     Colors.getInstance().addColorsChangedListener(new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*   76 */         TableRowPainted.alternatingColors[1] = Colors.colorAltRow;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*   81 */   private int height = 0;
/*      */   
/*   83 */   private boolean initializing = true;
/*      */   
/*   85 */   private Color colorFG = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableRowPainted(TableRowCore parentRow, TableViewPainted tv, Object dataSource, boolean triggerHeightChange)
/*      */   {
/*   93 */     super(tv.getSyncObject(), parentRow, tv, dataSource);
/*   94 */     this.subRows_sync = tv.getSyncObject();
/*      */     
/*   96 */     TableColumnCore sortColumn = tv.getSortColumn();
/*   97 */     if ((sortColumn != null) && ((parentRow == null) || (sortColumn.handlesDataSourceType(getDataSource(false).getClass()))))
/*      */     {
/*      */ 
/*  100 */       this.cellSort = new TableCellPainted(this, sortColumn, sortColumn.getPosition());
/*      */     }
/*      */     
/*      */ 
/*  104 */     if (this.height == 0) {
/*  105 */       setHeight(tv.getRowDefaultHeight(), false);
/*      */     }
/*  107 */     this.initializing = false;
/*  108 */     if (triggerHeightChange) {
/*  109 */       heightChanged(0, this.height);
/*      */     }
/*      */   }
/*      */   
/*      */   private void buildCells()
/*      */   {
/*  115 */     TableColumnCore[] visibleColumns = getView().getVisibleColumns();
/*  116 */     if (visibleColumns == null) {
/*  117 */       return;
/*      */     }
/*  119 */     synchronized (this.lock) {
/*  120 */       this.mTableCells = new LinkedHashMap(visibleColumns.length, 1.0F);
/*      */       
/*      */ 
/*  123 */       TableColumn currentSortColumn = null;
/*  124 */       if (this.cellSort != null) {
/*  125 */         currentSortColumn = this.cellSort.getTableColumn();
/*      */       }
/*  127 */       TableRowCore parentRow = getParentRowCore();
/*      */       
/*  129 */       for (int i = 0; i < visibleColumns.length; i++) {
/*  130 */         if (visibleColumns[i] != null)
/*      */         {
/*      */ 
/*      */ 
/*  134 */           if ((parentRow != null) && (!visibleColumns[i].handlesDataSourceType(getDataSource(false).getClass())))
/*      */           {
/*  136 */             this.mTableCells.put(visibleColumns[i].getName(), null);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  141 */             TableCellCore cell = (currentSortColumn != null) && (visibleColumns[i].equals(currentSortColumn)) ? this.cellSort : new TableCellPainted(this, visibleColumns[i], i);
/*      */             
/*      */ 
/*  144 */             this.mTableCells.put(visibleColumns[i].getName(), cell);
/*      */           } }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void destroyCells() {
/*  151 */     synchronized (this.lock) {
/*  152 */       if (this.mTableCells != null) {
/*  153 */         for (TableCellCore cell : this.mTableCells.values()) {
/*  154 */           if ((cell != null) && (cell != this.cellSort) && 
/*  155 */             (!cell.isDisposed())) {
/*  156 */             cell.dispose();
/*      */           }
/*      */         }
/*      */         
/*  160 */         this.mTableCells = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public TableViewPainted getViewPainted() {
/*  166 */     return (TableViewPainted)getView();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void swt_paintGC(GC gc, Rectangle drawBounds, int rowStartX, int rowStartY, int pos, boolean isTableSelected, boolean isTableEnabled)
/*      */   {
/*  178 */     if ((isRowDisposed()) || (gc == null) || (gc.isDisposed()) || (drawBounds == null)) {
/*  179 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  186 */     TableColumnCore[] visibleColumns = getView().getVisibleColumns();
/*  187 */     if ((visibleColumns == null) || (visibleColumns.length == 0)) {
/*  188 */       return;
/*      */     }
/*      */     
/*  191 */     boolean isSelected = isSelected();
/*  192 */     boolean isSelectedNotFocused = (isSelected) && (!isTableSelected);
/*      */     
/*  194 */     Color origBG = gc.getBackground();
/*  195 */     Color origFG = gc.getForeground();
/*      */     
/*  197 */     Color fg = getForeground();
/*  198 */     Color shadowColor = null;
/*      */     
/*      */     Color altColor;
/*      */     Color bg;
/*  202 */     if (isTableEnabled) {
/*  203 */       Color altColor = alternatingColors[0];
/*  204 */       if (altColor == null) {
/*  205 */         altColor = gc.getDevice().getSystemColor(25);
/*      */       }
/*  207 */       if (isSelected)
/*      */       {
/*  209 */         Color color = gc.getDevice().getSystemColor(26);
/*  210 */         gc.setBackground(color);
/*      */       } else {
/*  212 */         gc.setBackground(altColor);
/*      */       }
/*      */       
/*  215 */       Color bg = getBackground();
/*  216 */       if (bg == null) {
/*  217 */         bg = gc.getBackground();
/*      */       } else {
/*  219 */         gc.setBackground(bg);
/*      */       }
/*      */       
/*  222 */       if (isSelected) {
/*  223 */         shadowColor = fg;
/*  224 */         fg = gc.getDevice().getSystemColor(27);
/*      */       }
/*  226 */       else if (fg == null) {
/*  227 */         fg = gc.getDevice().getSystemColor(24);
/*      */       }
/*      */     }
/*      */     else {
/*  231 */       Device device = gc.getDevice();
/*  232 */       altColor = device.getSystemColor(22);
/*  233 */       Color bg; if (isSelected) {
/*  234 */         bg = device.getSystemColor(19);
/*      */       } else {
/*  236 */         bg = altColor;
/*      */       }
/*  238 */       gc.setBackground(bg);
/*      */       
/*  240 */       fg = device.getSystemColor(18);
/*      */     }
/*  242 */     gc.setForeground(fg);
/*      */     
/*  244 */     int rowAlpha = getAlpha();
/*  245 */     Font font = gc.getFont();
/*  246 */     Rectangle clipping = gc.getClipping();
/*      */     
/*  248 */     int x = rowStartX;
/*      */     
/*  250 */     synchronized (this.lock) {
/*  251 */       if (this.mTableCells == null)
/*      */       {
/*  253 */         setShown(true, true);
/*      */       }
/*  255 */       if (this.mTableCells != null)
/*  256 */         for (TableColumn tc : visibleColumns) {
/*  257 */           TableCellCore cell = (TableCellCore)this.mTableCells.get(tc.getName());
/*  258 */           int w = tc.getWidth();
/*  259 */           if ((!(cell instanceof TableCellPainted)) || (cell.isDisposed())) {
/*  260 */             gc.fillRectangle(x, rowStartY, w, getHeight());
/*  261 */             x += w;
/*      */           }
/*      */           else {
/*  264 */             TableCellPainted cellSWT = (TableCellPainted)cell;
/*  265 */             Rectangle r = new Rectangle(x, rowStartY, w, getHeight());
/*  266 */             cellSWT.setBoundsRaw(r);
/*  267 */             if (drawBounds.intersects(r))
/*      */             {
/*  269 */               gc.setAlpha(255);
/*  270 */               if (isSelectedNotFocused) {
/*  271 */                 gc.setBackground(altColor);
/*  272 */                 gc.fillRectangle(r);
/*  273 */                 gc.setAlpha(100);
/*  274 */                 gc.setBackground(bg);
/*  275 */                 gc.fillRectangle(r);
/*      */               } else {
/*  277 */                 gc.setBackground(bg);
/*  278 */                 gc.fillRectangle(r);
/*  279 */                 if (isSelected) {
/*  280 */                   gc.setAlpha(80);
/*  281 */                   gc.setForeground(altColor);
/*  282 */                   gc.fillGradientRectangle(r.x, r.y, r.width, r.height, true);
/*  283 */                   gc.setForeground(fg);
/*      */                 }
/*      */               }
/*  286 */               gc.setAlpha(rowAlpha);
/*  287 */               if (swt_paintCell(gc, cellSWT.getBounds(), cellSWT, shadowColor))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  295 */                 gc.setBackground(bg);
/*  296 */                 gc.setForeground(fg);
/*  297 */                 gc.setFont(font);
/*  298 */                 Utils.setClipping(gc, clipping);
/*      */               }
/*  300 */               if (DEBUG_ROW_PAINT) {
/*  301 */                 ((TableCellSWTBase)cell).debug("painted " + (cell.getVisuallyChangedSinceRefresh() ? "VC" : "!P") + " @ " + r);
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*  306 */             else if (DEBUG_ROW_PAINT) {
/*  307 */               ((TableCellSWTBase)cell).debug("Skip paintItem; no intersects; r=" + r + ";dB=" + drawBounds + " from " + Debug.getCompressedStackTrace(4));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  316 */             x += w;
/*      */           }
/*      */         }
/*  319 */       int w = drawBounds.width - x;
/*  320 */       if (w > 0) {
/*  321 */         Rectangle r = new Rectangle(x, rowStartY, w, getHeight());
/*  322 */         if (clipping.intersects(r)) {
/*  323 */           gc.setAlpha(255);
/*  324 */           if (isSelectedNotFocused) {
/*  325 */             gc.setBackground(altColor);
/*  326 */             gc.fillRectangle(r);
/*  327 */             gc.setAlpha(100);
/*  328 */             gc.setBackground(bg);
/*  329 */             gc.fillRectangle(r);
/*      */           } else {
/*  331 */             gc.fillRectangle(r);
/*  332 */             if (isSelected) {
/*  333 */               gc.setAlpha(80);
/*  334 */               gc.setForeground(altColor);
/*  335 */               gc.fillGradientRectangle(r.x, r.y, r.width, r.height, true);
/*  336 */               gc.setForeground(fg);
/*      */             }
/*      */           }
/*  339 */           gc.setAlpha(rowAlpha);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  349 */     if (isFocused()) {
/*  350 */       gc.setAlpha(40);
/*  351 */       gc.setForeground(origFG);
/*  352 */       gc.setLineDash(new int[] { 1, 2 });
/*  353 */       gc.drawRectangle(rowStartX, rowStartY, getViewPainted().getClientArea().width - 1, getHeight() - 1);
/*      */       
/*  355 */       gc.setLineStyle(1);
/*      */     }
/*      */     
/*  358 */     gc.setAlpha(255);
/*  359 */     gc.setBackground(origBG);
/*  360 */     gc.setForeground(origFG);
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean swt_paintCell(GC gc, Rectangle cellBounds, TableCellSWTBase cell, Color shadowColor)
/*      */   {
/*  366 */     if (cellBounds == null) {
/*  367 */       return false;
/*      */     }
/*      */     
/*  370 */     boolean gcChanged = false;
/*      */     try
/*      */     {
/*  373 */       gc.setTextAntialias(-1);
/*      */       
/*  375 */       TableViewSWT<?> view = (TableViewSWT)getView();
/*      */       
/*  377 */       TableColumnCore column = (TableColumnCore)cell.getTableColumn();
/*  378 */       view.invokePaintListeners(gc, this, column, cellBounds);
/*      */       
/*  380 */       int fontStyle = getFontStyle();
/*  381 */       Font oldFont = null;
/*  382 */       if (fontStyle == 1) {
/*  383 */         oldFont = gc.getFont();
/*  384 */         gc.setFont(FontUtils.getAnyFontBold(gc));
/*  385 */         gcChanged = true;
/*      */       }
/*      */       
/*  388 */       if (!cell.isUpToDate())
/*      */       {
/*  390 */         cell.refresh(true, true);
/*      */       }
/*      */       
/*      */ 
/*  394 */       String text = cell.getText();
/*      */       
/*  396 */       Color fg = cell.getForegroundSWT();
/*  397 */       if (fg != null) {
/*  398 */         gcChanged = true;
/*  399 */         if (isSelected()) {
/*  400 */           shadowColor = fg;
/*      */         } else {
/*  402 */           gc.setForeground(fg);
/*      */         }
/*      */       }
/*  405 */       Color bg = cell.getBackgroundSWT();
/*  406 */       if (bg != null) {
/*  407 */         gcChanged = true;
/*  408 */         gc.setBackground(bg);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  414 */       if (cell.needsPainting()) {
/*  415 */         Image graphicSWT = cell.getGraphicSWT();
/*  416 */         if ((graphicSWT != null) && (!graphicSWT.isDisposed())) {
/*  417 */           Rectangle imageBounds = graphicSWT.getBounds();
/*  418 */           Rectangle graphicBounds = new Rectangle(cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height);
/*      */           
/*  420 */           if (cell.getFillCell()) {
/*  421 */             if (!graphicBounds.isEmpty()) {
/*  422 */               gc.setAdvanced(true);
/*      */               
/*  424 */               gc.drawImage(graphicSWT, 0, 0, imageBounds.width, imageBounds.height, graphicBounds.x, graphicBounds.y, graphicBounds.width, graphicBounds.height);
/*      */             }
/*      */             
/*      */           }
/*      */           else
/*      */           {
/*  430 */             if (imageBounds.width < graphicBounds.width) {
/*  431 */               int alignment = column.getAlignment();
/*  432 */               if ((alignment & 0x3) > 0) {
/*  433 */                 graphicBounds.x += (graphicBounds.width - imageBounds.width) / 2;
/*  434 */               } else if ((alignment & 0x2) > 0) {
/*  435 */                 graphicBounds.x = (graphicBounds.x + graphicBounds.width - imageBounds.width);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  440 */             if (imageBounds.height < graphicBounds.height) {
/*  441 */               graphicBounds.y += (graphicBounds.height - imageBounds.height) / 2;
/*      */             }
/*      */             
/*  444 */             gc.drawImage(graphicSWT, graphicBounds.x, graphicBounds.y);
/*      */           }
/*      */         }
/*      */         
/*  448 */         cell.doPaint(gc);
/*  449 */         gcChanged = true;
/*      */       }
/*  451 */       if (text.length() > 0) {
/*  452 */         int ofsx = 0;
/*  453 */         Image image = cell.getIcon();
/*  454 */         Rectangle imageBounds = null;
/*  455 */         if ((image != null) && (!image.isDisposed())) {
/*  456 */           imageBounds = image.getBounds();
/*  457 */           int ofs = imageBounds.width;
/*  458 */           ofsx += ofs;
/*  459 */           cellBounds.x += ofs;
/*  460 */           cellBounds.width -= ofs;
/*      */         }
/*      */         
/*  463 */         int style = TableColumnSWTUtils.convertColumnAlignmentToSWT(column.getAlignment());
/*  464 */         if (cellBounds.height > 20) {
/*  465 */           style |= 0x40;
/*      */         }
/*  467 */         int textOpacity = cell.getTextAlpha();
/*      */         
/*      */ 
/*  470 */         if (textOpacity < 255)
/*      */         {
/*  472 */           gc.setAlpha(textOpacity);
/*  473 */           gcChanged = true;
/*  474 */         } else if (textOpacity > 255) {
/*  475 */           gc.setFont(FontUtils.getAnyFontBold(gc));
/*      */           
/*      */ 
/*  478 */           gcChanged = true;
/*      */         }
/*      */         
/*  481 */         ofsx += 6;
/*  482 */         cellBounds.x += 3;
/*  483 */         cellBounds.width -= 6;
/*  484 */         cellBounds.y += 2;
/*  485 */         cellBounds.height -= 4;
/*  486 */         if (!cellBounds.isEmpty()) {
/*  487 */           GCStringPrinter sp = new GCStringPrinter(gc, text, cellBounds, true, cellBounds.height > 20, style);
/*      */           
/*      */           boolean fit;
/*      */           boolean fit;
/*  491 */           if (shadowColor != null) {
/*  492 */             Color oldFG = gc.getForeground();
/*  493 */             gc.setForeground(shadowColor);
/*      */             
/*  495 */             cellBounds.x += 1;
/*  496 */             cellBounds.y += 1;
/*  497 */             int alpha = gc.getAlpha();
/*  498 */             gc.setAlpha(64);
/*  499 */             sp.printString(gc, cellBounds, style);
/*  500 */             gc.setAlpha(alpha);
/*  501 */             gc.setForeground(oldFG);
/*      */             
/*  503 */             cellBounds.x -= 1;
/*  504 */             cellBounds.y -= 1;
/*  505 */             fit = sp.printString2(gc, cellBounds, style);
/*      */           } else {
/*  507 */             fit = sp.printString();
/*      */           }
/*      */           
/*  510 */           if (fit)
/*      */           {
/*  512 */             cell.setDefaultToolTip(null);
/*      */           }
/*      */           else {
/*  515 */             cell.setDefaultToolTip(text);
/*      */           }
/*      */           
/*  518 */           Point size = sp.getCalculatedSize();
/*  519 */           size.x += ofsx;
/*      */           
/*  521 */           TableColumn tableColumn = cell.getTableColumn();
/*  522 */           if ((tableColumn != null) && (tableColumn.getPreferredWidth() < size.x)) {
/*  523 */             tableColumn.setPreferredWidth(size.x);
/*      */           }
/*      */           
/*  526 */           if (imageBounds != null) {
/*  527 */             int drawToY = cellBounds.y + cellBounds.height / 2 - imageBounds.height / 2;
/*      */             
/*      */ 
/*  530 */             boolean hack_adv = (Constants.isWindows8OrHigher) && (gc.getAdvanced());
/*      */             
/*  532 */             if (hack_adv)
/*      */             {
/*  534 */               gc.setAdvanced(false);
/*      */             }
/*  536 */             if ((style & 0x20000) != 0) {
/*  537 */               int drawToX = cellBounds.x + cellBounds.width - size.x;
/*  538 */               gc.drawImage(image, drawToX, drawToY);
/*      */             }
/*  540 */             else if (imageBounds.height > cellBounds.height) {
/*  541 */               float pct = cellBounds.height / imageBounds.height;
/*  542 */               gc.drawImage(image, 0, 0, imageBounds.width, imageBounds.height, cellBounds.x - imageBounds.width - 3, cellBounds.y, (int)(imageBounds.width * pct), (int)(imageBounds.height * pct));
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*  547 */               gc.drawImage(image, cellBounds.x - imageBounds.width - 3, drawToY);
/*      */             }
/*      */             
/*  550 */             if (hack_adv) {
/*  551 */               gc.setAdvanced(true);
/*      */             }
/*      */           }
/*      */         } else {
/*  555 */           cell.setDefaultToolTip(null);
/*      */         }
/*      */       }
/*  558 */       cell.clearVisuallyChangedSinceRefresh();
/*      */       
/*  560 */       if (oldFont != null) {
/*  561 */         gc.setFont(oldFont);
/*      */       }
/*      */     } catch (Exception e) {
/*  564 */       e.printStackTrace();
/*      */     }
/*      */     
/*  567 */     return gcChanged;
/*      */   }
/*      */   
/*      */   private Font getRandomFont() {
/*  571 */     FontData[] fontList = Display.getDefault().getFontList(null, Math.random() > 0.5D);
/*  572 */     FontData fontData = fontList[((int)(Math.random() * fontList.length))];
/*  573 */     fontData.setStyle((int)(Math.random() * 4.0D));
/*  574 */     fontData.height = ((float)(Math.random() * 50.0D));
/*  575 */     return new Font(Display.getDefault(), fontData);
/*      */   }
/*      */   
/*      */   public List<TableCellCore> refresh(boolean bDoGraphics, boolean bVisible)
/*      */   {
/*  580 */     final List<TableCellCore> invalidCells = super.refresh(bDoGraphics, bVisible);
/*      */     
/*      */ 
/*  583 */     if (invalidCells.size() > 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  588 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  590 */           Composite composite = TableRowPainted.this.getViewPainted().getComposite();
/*  591 */           if ((composite == null) || (composite.isDisposed()) || (!TableRowPainted.this.isVisible())) {
/*      */             return;
/*      */           }
/*      */           boolean allCells;
/*  595 */           synchronized (TableRowPainted.this.lock) {
/*  596 */             allCells = (TableRowPainted.this.mTableCells != null) && (invalidCells.size() == TableRowPainted.this.mTableCells.size());
/*      */           }
/*      */           Rectangle drawBounds;
/*  599 */           if (allCells) {
/*  600 */             TableRowPainted.this.getViewPainted().swt_updateCanvasImage(TableRowPainted.this.getDrawBounds(), false);
/*      */           } else {
/*  602 */             drawBounds = TableRowPainted.this.getDrawBounds();
/*  603 */             for (Object o : invalidCells) {
/*  604 */               if ((o instanceof TableCellPainted)) {
/*  605 */                 TableCellPainted cell = (TableCellPainted)o;
/*  606 */                 Rectangle bounds = cell.getBoundsRaw();
/*  607 */                 if (bounds != null)
/*      */                 {
/*      */ 
/*  610 */                   bounds.y = drawBounds.y;
/*  611 */                   bounds.height = drawBounds.height;
/*  612 */                   cell.setBoundsRaw(bounds);
/*  613 */                   TableRowPainted.this.getViewPainted().swt_updateCanvasImage(bounds, false);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  623 */     return invalidCells;
/*      */   }
/*      */   
/*      */   public void redraw(boolean doChildren) {
/*  627 */     redraw(doChildren, false);
/*      */   }
/*      */   
/*      */   public void redraw(boolean doChildren, boolean immediateRedraw) {
/*  631 */     if (isRowDisposed()) {
/*  632 */       return;
/*      */     }
/*  634 */     getViewPainted().redrawRow(this, immediateRedraw);
/*      */     
/*  636 */     if (!doChildren) {
/*  637 */       return;
/*      */     }
/*  639 */     synchronized (this.subRows_sync) {
/*  640 */       if (this.subRows != null) {
/*  641 */         for (TableRowPainted subrow : this.subRows) {
/*  642 */           subrow.redraw();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void debug(String s) {
/*  649 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("table");
/*  650 */     String prefix = SystemTime.getCurrentTime() + ":" + getTableID() + ": r" + getIndex();
/*      */     
/*  652 */     if (getParentRowCore() != null) {
/*  653 */       prefix = prefix + "of" + getParentRowCore().getIndex();
/*      */     }
/*  655 */     prefix = prefix + ": ";
/*  656 */     diag_logger.log(prefix + s);
/*      */     
/*  658 */     System.out.println(prefix + s);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Rectangle getBounds()
/*      */   {
/*  668 */     return new Rectangle(0, this.drawOffset.y, 9990, getHeight());
/*      */   }
/*      */   
/*      */   public Rectangle getDrawBounds() {
/*  672 */     TableViewPainted view = (TableViewPainted)getView();
/*  673 */     Rectangle clientArea = view.getClientArea();
/*  674 */     int offsetX = TableViewPainted.DIRECT_DRAW ? -clientArea.x : 0;
/*  675 */     Rectangle bounds = new Rectangle(offsetX, this.drawOffset.y - clientArea.y, 9990, getHeight());
/*      */     
/*  677 */     return bounds;
/*      */   }
/*      */   
/*      */   public int getFullHeight() {
/*  681 */     int h = getHeight();
/*  682 */     if ((this.numSubItems > 0) && (isExpanded())) {
/*  683 */       h += this.subRowsHeight;
/*      */     }
/*  685 */     return h;
/*      */   }
/*      */   
/*      */   public Point getDrawOffset() {
/*  689 */     return this.drawOffset;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void heightChanged(int oldHeight, int newHeight)
/*      */   {
/*  696 */     getViewPainted().rowHeightChanged(this, oldHeight, newHeight);
/*  697 */     TableRowCore row = getParentRowCore();
/*  698 */     if ((row instanceof TableRowPainted)) {
/*  699 */       ((TableRowPainted)row).subRowHeightChanged(this, oldHeight, newHeight);
/*      */     }
/*      */   }
/*      */   
/*      */   public void subRowHeightChanged(TableRowCore row, int oldHeight, int newHeight) {
/*  704 */     this.subRowsHeight += newHeight - oldHeight;
/*      */   }
/*      */   
/*      */   public boolean setDrawOffset(Point drawOffset) {
/*  708 */     if (drawOffset.equals(this.drawOffset)) {
/*  709 */       return false;
/*      */     }
/*  711 */     this.drawOffset = drawOffset;
/*      */     
/*  713 */     return true;
/*      */   }
/*      */   
/*      */   public void setWidgetSelected(boolean selected)
/*      */   {
/*  718 */     redraw(false, true);
/*      */   }
/*      */   
/*      */   public boolean setShown(boolean b, boolean force)
/*      */   {
/*  723 */     if ((b == this.wasShown) && (!force)) {
/*  724 */       return false;
/*      */     }
/*      */     
/*  727 */     synchronized (this.lock) {
/*  728 */       if ((b) && (this.mTableCells == null)) {
/*  729 */         buildCells();
/*      */       }
/*      */     }
/*      */     
/*  733 */     boolean ret = super.setShown(b, force);
/*      */     
/*  735 */     if (b) {
/*  736 */       invalidate();
/*  737 */       redraw(false, false);
/*      */     }
/*      */     
/*  740 */     synchronized (this.lock) {
/*  741 */       if ((!b) && (this.mTableCells != null)) {
/*  742 */         destroyCells();
/*      */       }
/*      */     }
/*      */     
/*  746 */     return ret;
/*      */   }
/*      */   
/*      */   public void delete()
/*      */   {
/*  751 */     super.delete();
/*      */     
/*  753 */     synchronized (this.lock) {
/*  754 */       if ((this.cellSort != null) && (!this.cellSort.isDisposed()))
/*      */       {
/*  756 */         this.cellSort.dispose();
/*      */         
/*  758 */         this.cellSort = null;
/*      */       }
/*      */     }
/*      */     
/*  762 */     deleteExistingSubRows();
/*      */   }
/*      */   
/*      */   private void deleteExistingSubRows() {
/*  766 */     synchronized (this.subRows_sync) {
/*  767 */       if (this.subRows != null) {
/*  768 */         for (TableRowPainted subrow : this.subRows) {
/*  769 */           subrow.delete();
/*      */         }
/*      */       }
/*  772 */       this.subRows = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSubItemCount(int length) {
/*  777 */     this.numSubItems = length;
/*  778 */     if ((isExpanded()) && (this.subDataSources.length == length))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  783 */       deleteExistingSubRows();
/*  784 */       TableRowPainted[] newSubRows = new TableRowPainted[length];
/*  785 */       TableViewPainted tv = getViewPainted();
/*  786 */       int h = 0;
/*  787 */       for (int i = 0; i < newSubRows.length; i++) {
/*  788 */         newSubRows[i] = new TableRowPainted(this, tv, this.subDataSources[i], false);
/*  789 */         newSubRows[i].setTableItem(i, false);
/*  790 */         h += newSubRows[i].getHeight();
/*      */       }
/*      */       
/*  793 */       int oldHeight = getFullHeight();
/*  794 */       this.subRowsHeight = h;
/*  795 */       getViewPainted().rowHeightChanged(this, oldHeight, getFullHeight());
/*  796 */       getViewPainted().triggerListenerRowAdded(newSubRows);
/*      */       
/*  798 */       this.subRows = newSubRows;
/*      */     }
/*      */   }
/*      */   
/*      */   public int getSubItemCount() {
/*  803 */     return this.numSubItems;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableRowCore linkSubItem(int indexOf)
/*      */   {
/*  811 */     return null;
/*      */   }
/*      */   
/*      */   public void setSubItems(Object[] datasources) {
/*  815 */     deleteExistingSubRows();
/*  816 */     synchronized (this.subRows_sync) {
/*  817 */       this.subDataSources = datasources;
/*  818 */       this.subRowsHeight = 0;
/*  819 */       setSubItemCount(datasources.length);
/*      */     }
/*      */   }
/*      */   
/*      */   public TableRowCore[] getSubRowsWithNull() {
/*  824 */     synchronized (this.subRows_sync) {
/*  825 */       return this.subRows == null ? new TableRowCore[0] : this.subRows;
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeSubRow(Object datasource) {
/*  830 */     synchronized (this.subRows_sync)
/*      */     {
/*  832 */       for (int i = 0; i < this.subDataSources.length; i++) {
/*  833 */         Object ds = this.subDataSources[i];
/*  834 */         if (ds == datasource) {
/*  835 */           TableRowPainted rowToDel = this.subRows[i];
/*  836 */           TableRowPainted[] newSubRows = new TableRowPainted[this.subRows.length - 1];
/*  837 */           System.arraycopy(this.subRows, 0, newSubRows, 0, i);
/*  838 */           System.arraycopy(this.subRows, i + 1, newSubRows, i, this.subRows.length - i - 1);
/*      */           
/*  840 */           this.subRows = newSubRows;
/*      */           
/*  842 */           Object[] newDatasources = new Object[this.subRows.length];
/*  843 */           System.arraycopy(this.subDataSources, 0, newDatasources, 0, i);
/*  844 */           System.arraycopy(this.subDataSources, i + 1, newDatasources, i, this.subDataSources.length - i - 1);
/*      */           
/*  846 */           this.subDataSources = newDatasources;
/*      */           
/*  848 */           rowToDel.delete();
/*      */           
/*  850 */           setSubItemCount(this.subRows.length);
/*      */           
/*  852 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setExpanded(boolean b)
/*      */   {
/*  860 */     if (canExpand()) {
/*  861 */       int oldHeight = getFullHeight();
/*  862 */       super.setExpanded(b);
/*  863 */       synchronized (this.subRows_sync) {
/*  864 */         TableRowPainted[] newSubRows = null;
/*  865 */         if ((b) && ((this.subRows == null) || (this.subRows.length != this.numSubItems)) && (this.subDataSources != null) && (this.subDataSources.length == this.numSubItems))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  871 */           deleteExistingSubRows();
/*  872 */           newSubRows = new TableRowPainted[this.numSubItems];
/*  873 */           TableViewPainted tv = getViewPainted();
/*  874 */           int h = 0;
/*  875 */           for (int i = 0; i < newSubRows.length; i++) {
/*  876 */             newSubRows[i] = new TableRowPainted(this, tv, this.subDataSources[i], false);
/*      */             
/*  878 */             newSubRows[i].setTableItem(i, false);
/*  879 */             h += newSubRows[i].getHeight();
/*      */           }
/*      */           
/*  882 */           this.subRowsHeight = h;
/*      */           
/*  884 */           this.subRows = newSubRows;
/*      */         }
/*      */         
/*  887 */         getViewPainted().rowHeightChanged(this, oldHeight, getFullHeight());
/*      */         
/*  889 */         if (newSubRows != null) {
/*  890 */           getViewPainted().triggerListenerRowAdded(newSubRows);
/*      */         }
/*      */       }
/*      */       
/*  894 */       if (isVisible()) {
/*  895 */         getViewPainted().visibleRowsChanged();
/*  896 */         getViewPainted().redrawTable();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public TableRowCore getSubRow(int pos) {
/*  902 */     synchronized (this.subRows_sync) {
/*  903 */       if (this.subRows == null) {
/*  904 */         return null;
/*      */       }
/*  906 */       if ((pos >= 0) && (pos < this.subRows.length)) {
/*  907 */         return this.subRows[pos];
/*      */       }
/*  909 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean setForeground(Color color)
/*      */   {
/*  918 */     if (isRowDisposed()) {
/*  919 */       return false;
/*      */     }
/*  921 */     if ((color == this.colorFG) || ((color != null) && (color.equals(this.colorFG))) || ((this.colorFG != null) && (this.colorFG.equals(color))))
/*      */     {
/*  923 */       return false;
/*      */     }
/*      */     
/*  926 */     this.colorFG = color;
/*      */     
/*  928 */     Utils.getOffOfSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  930 */         TableRowPainted.this.redraw(false, false);
/*      */       }
/*      */       
/*  933 */     });
/*  934 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean setIconSize(Point pt)
/*      */   {
/*  940 */     return false;
/*      */   }
/*      */   
/*      */   public Color getForeground()
/*      */   {
/*  945 */     return this.colorFG;
/*      */   }
/*      */   
/*      */   public Color getBackground()
/*      */   {
/*  950 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getHeight()
/*      */   {
/*  962 */     return this.height == 0 ? getView().getRowDefaultHeight() : this.height;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean setHeight(int newHeight)
/*      */   {
/*  969 */     TableRowCore parentRowCore = getParentRowCore();
/*  970 */     boolean trigger = (parentRowCore == null) || (parentRowCore.isExpanded());
/*      */     
/*  972 */     return setHeight(newHeight, trigger);
/*      */   }
/*      */   
/*      */   public boolean setHeight(int newHeight, boolean trigger) {
/*  976 */     if (this.height == newHeight) {
/*  977 */       return false;
/*      */     }
/*  979 */     int oldHeight = this.height;
/*  980 */     this.height = newHeight;
/*  981 */     if ((trigger) && (!this.initializing)) {
/*  982 */       heightChanged(oldHeight, newHeight);
/*      */     }
/*      */     
/*  985 */     return true;
/*      */   }
/*      */   
/*      */   public TableCellCore getTableCellCore(String name)
/*      */   {
/*  990 */     if (isRowDisposed()) {
/*  991 */       return null;
/*      */     }
/*  993 */     synchronized (this.lock) {
/*  994 */       if (this.mTableCells == null) {
/*  995 */         if ((this.cellSort != null) && (!this.cellSort.isDisposed()) && (this.cellSort.getTableColumn().getName().equals(name)))
/*      */         {
/*  997 */           return this.cellSort;
/*      */         }
/*  999 */         return null;
/*      */       }
/*      */       
/* 1002 */       return (TableCellCore)this.mTableCells.get(name);
/*      */     }
/*      */   }
/*      */   
/*      */   public TableCellSWT getTableCellSWT(String name)
/*      */   {
/* 1008 */     TableCellCore cell = getTableCellCore(name);
/* 1009 */     return (cell instanceof TableCellSWT) ? (TableCellSWT)cell : null;
/*      */   }
/*      */   
/*      */   public TableCell getTableCell(String field)
/*      */   {
/* 1014 */     return getTableCellCore(field);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSortColumn(String columnID)
/*      */   {
/* 1024 */     synchronized (this.lock)
/*      */     {
/* 1026 */       if (this.mTableCells == null) {
/* 1027 */         if ((this.cellSort != null) && (!this.cellSort.isDisposed())) {
/* 1028 */           if (this.cellSort.getTableColumn().getName().equals(columnID)) {
/* 1029 */             return;
/*      */           }
/* 1031 */           this.cellSort.dispose();
/* 1032 */           this.cellSort = null;
/*      */         }
/* 1034 */         TableColumnCore sortColumn = (TableColumnCore)getView().getTableColumn(columnID);
/*      */         
/* 1036 */         if ((getParentRowCore() == null) || (sortColumn.handlesDataSourceType(getDataSource(false).getClass())))
/*      */         {
/* 1038 */           this.cellSort = new TableCellPainted(this, sortColumn, sortColumn.getPosition());
/*      */         }
/*      */         else {
/* 1041 */           this.cellSort = null;
/*      */         }
/*      */       } else {
/* 1044 */         this.cellSort = ((TableCellCore)this.mTableCells.get(columnID));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setBackgroundImage(Image image) {}
/*      */   
/*      */   /* Error */
/*      */   public TableCellCore getSortColumnCell(String hint)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 713	org/gudy/azureus2/ui/swt/views/table/painted/TableRowPainted:lock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 712	org/gudy/azureus2/ui/swt/views/table/painted/TableRowPainted:cellSort	Lcom/aelitis/azureus/ui/common/table/TableCellCore;
/*      */     //   11: aload_2
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_3
/*      */     //   15: aload_2
/*      */     //   16: monitorexit
/*      */     //   17: aload_3
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1018	-> byte code offset #0
/*      */     //   Java source line #1019	-> byte code offset #7
/*      */     //   Java source line #1020	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	TableRowPainted
/*      */     //   0	19	1	hint	String
/*      */     //   5	11	2	Ljava/lang/Object;	Object
/*      */     //   14	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/painted/TableRowPainted.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */