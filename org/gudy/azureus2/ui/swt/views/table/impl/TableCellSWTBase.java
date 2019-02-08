/*      */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*      */ 
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnSortObject;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import java.io.PrintStream;
/*      */ import java.text.Collator;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.RGB;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.ui.Graphic;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellClipboardListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellVisibilityListener;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ 
/*      */ public abstract class TableCellSWTBase implements org.gudy.azureus2.ui.swt.views.table.TableCellSWT
/*      */ {
/*   50 */   private static final LogIDs LOGID = LogIDs.GUI;
/*      */   
/*   52 */   private static AEMonitor this_mon = new AEMonitor("TableCell");
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int FLAG_VALID = 1;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int FLAG_SORTVALUEISTEXT = 2;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int FLAG_TOOLTIPISAUTO = 4;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int FLAG_UPTODATE = 8;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int FLAG_DISPOSED = 16;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int FLAG_MUSTREFRESH = 32;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final int FLAG_VISUALLY_CHANGED_SINCE_REFRESH = 64;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final boolean DEBUGONLYZERO = false;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final boolean DEBUG_FLAGS = false;
/*      */   
/*      */ 
/*      */ 
/*      */   private int flags;
/*      */   
/*      */ 
/*      */ 
/*      */   protected TableRowCore tableRow;
/*      */   
/*      */ 
/*      */ 
/*      */   protected TableColumnCore tableColumn;
/*      */   
/*      */ 
/*      */ 
/*      */   private byte tooltipErrLoopCount;
/*      */   
/*      */ 
/*      */ 
/*  108 */   public boolean bDebug = false;
/*      */   
/*      */   protected ArrayList<TableCellRefreshListener> refreshListeners;
/*      */   
/*      */   private ArrayList<TableCellDisposeListener> disposeListeners;
/*      */   
/*      */   private ArrayList<TableCellToolTipListener> tooltipListeners;
/*      */   
/*      */   private ArrayList<TableCellMouseListener> cellMouseListeners;
/*      */   
/*      */   private ArrayList<TableCellMouseMoveListener> cellMouseMoveListeners;
/*      */   
/*      */   private ArrayList<TableCellVisibilityListener> cellVisibilityListeners;
/*      */   
/*      */   protected ArrayList<TableCellSWTPaintListener> cellSWTPaintListeners;
/*      */   
/*      */   private ArrayList<TableCellClipboardListener> cellClipboardListeners;
/*      */   
/*      */   protected Comparable sortValue;
/*      */   
/*  128 */   private byte restartRefresh = 0;
/*      */   
/*  130 */   private boolean bInRefreshAsync = false;
/*      */   
/*      */ 
/*      */   private byte refreshErrLoopCount;
/*      */   
/*      */   private byte loopFactor;
/*      */   
/*  137 */   protected static int MAX_REFRESHES = 10;
/*      */   
/*  139 */   private static int MAX_REFRESHES_WITHIN_MS = 100;
/*      */   
/*  141 */   private boolean bInRefresh = false;
/*      */   
/*      */ 
/*      */   private long lastRefresh;
/*      */   
/*      */   protected int numFastRefreshes;
/*      */   
/*      */   private Object oToolTip;
/*      */   
/*      */   private Object defaultToolTip;
/*      */   
/*  152 */   private int textAlpha = 255;
/*      */   
/*  154 */   private boolean doFillCell = false;
/*      */   
/*  156 */   private int iCursorID = 0;
/*      */   
/*      */   private boolean mouseOver;
/*      */   
/*      */   private Image icon;
/*      */   
/*  162 */   private Graphic graphic = null;
/*      */   
/*      */   public TableCellSWTBase(TableRowCore row, TableColumnCore column) {
/*  165 */     this.flags = 2;
/*  166 */     this.tableRow = row;
/*  167 */     this.tableColumn = column;
/*  168 */     this.tooltipErrLoopCount = 0;
/*  169 */     this.refreshErrLoopCount = 0;
/*  170 */     this.loopFactor = 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void constructionComplete()
/*      */   {
/*  178 */     if ((this.tableColumn != null) && (this.tableColumn.getType() == 2)) {
/*  179 */       setMarginHeight(1);
/*  180 */       setMarginWidth(1);
/*      */     }
/*      */   }
/*      */   
/*      */   protected abstract void constructionCompleter();
/*      */   
/*      */   public void addRefreshListener(TableCellRefreshListener listener)
/*      */   {
/*      */     try {
/*  189 */       this_mon.enter();
/*      */       
/*  191 */       if (this.refreshListeners == null) {
/*  192 */         this.refreshListeners = new ArrayList(1);
/*      */       }
/*  194 */       if (this.bDebug) {
/*  195 */         debug("addRefreshListener; count=" + this.refreshListeners.size());
/*      */       }
/*  197 */       this.refreshListeners.add(listener);
/*      */     }
/*      */     finally {
/*  200 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeRefreshListener(TableCellRefreshListener listener) {
/*      */     try {
/*  206 */       this_mon.enter();
/*      */       
/*  208 */       if (this.refreshListeners == null) {
/*      */         return;
/*      */       }
/*  211 */       this.refreshListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  214 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addDisposeListener(TableCellDisposeListener listener) {
/*      */     try {
/*  220 */       this_mon.enter();
/*      */       
/*  222 */       if (this.disposeListeners == null) {
/*  223 */         this.disposeListeners = new ArrayList(1);
/*      */       }
/*  225 */       this.disposeListeners.add(listener);
/*      */     }
/*      */     finally {
/*  228 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeDisposeListener(TableCellDisposeListener listener) {
/*      */     try {
/*  234 */       this_mon.enter();
/*      */       
/*  236 */       if (this.disposeListeners == null) {
/*      */         return;
/*      */       }
/*  239 */       this.disposeListeners.remove(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  243 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addToolTipListener(TableCellToolTipListener listener) {
/*      */     try {
/*  249 */       this_mon.enter();
/*      */       
/*  251 */       if (this.tooltipListeners == null) {
/*  252 */         this.tooltipListeners = new ArrayList(1);
/*      */       }
/*  254 */       this.tooltipListeners.add(listener);
/*      */     }
/*      */     finally {
/*  257 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeToolTipListener(TableCellToolTipListener listener) {
/*      */     try {
/*  263 */       this_mon.enter();
/*      */       
/*  265 */       if (this.tooltipListeners == null) {
/*      */         return;
/*      */       }
/*  268 */       this.tooltipListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  271 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addMouseListener(TableCellMouseListener listener) {
/*      */     try {
/*  277 */       this_mon.enter();
/*      */       
/*  279 */       if (this.cellMouseListeners == null) {
/*  280 */         this.cellMouseListeners = new ArrayList(1);
/*      */       }
/*  282 */       this.cellMouseListeners.add(listener);
/*      */     }
/*      */     finally {
/*  285 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeMouseListener(TableCellMouseListener listener) {
/*      */     try {
/*  291 */       this_mon.enter();
/*      */       
/*  293 */       if (this.cellMouseListeners == null) {
/*      */         return;
/*      */       }
/*  296 */       this.cellMouseListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  299 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addMouseMoveListener(TableCellMouseMoveListener listener) {
/*      */     try {
/*  305 */       this_mon.enter();
/*      */       
/*  307 */       if (this.cellMouseMoveListeners == null) {
/*  308 */         this.cellMouseMoveListeners = new ArrayList(1);
/*      */       }
/*  310 */       this.cellMouseMoveListeners.add(listener);
/*      */     }
/*      */     finally {
/*  313 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeMouseMoveListener(TableCellMouseMoveListener listener) {
/*      */     try {
/*  319 */       this_mon.enter();
/*      */       
/*  321 */       if (this.cellMouseMoveListeners == null) {
/*      */         return;
/*      */       }
/*  324 */       this.cellMouseMoveListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  327 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addVisibilityListener(TableCellVisibilityListener listener) {
/*      */     try {
/*  333 */       this_mon.enter();
/*      */       
/*  335 */       if (this.cellVisibilityListeners == null) {
/*  336 */         this.cellVisibilityListeners = new ArrayList(1);
/*      */       }
/*  338 */       this.cellVisibilityListeners.add(listener);
/*      */     }
/*      */     finally {
/*  341 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeVisibilityListener(TableCellVisibilityListener listener) {
/*      */     try {
/*  347 */       this_mon.enter();
/*      */       
/*  349 */       if (this.cellVisibilityListeners == null) {
/*      */         return;
/*      */       }
/*  352 */       this.cellVisibilityListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  355 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addSWTPaintListener(TableCellSWTPaintListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  366 */       this_mon.enter();
/*      */       
/*  368 */       if (this.cellSWTPaintListeners == null) {
/*  369 */         this.cellSWTPaintListeners = new ArrayList(1);
/*      */       }
/*  371 */       this.cellSWTPaintListeners.add(listener);
/*      */     }
/*      */     finally {
/*  374 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeSWTPaintListeners(GC gc) {
/*  379 */     if (this.tableColumn != null) {
/*  380 */       Object[] swtPaintListeners = this.tableColumn.getCellOtherListeners("SWTPaint");
/*  381 */       if (swtPaintListeners != null) {
/*  382 */         for (int i = 0; i < swtPaintListeners.length; i++) {
/*      */           try {
/*  384 */             TableCellSWTPaintListener l = (TableCellSWTPaintListener)swtPaintListeners[i];
/*      */             
/*  386 */             l.cellPaint(gc, this);
/*      */           }
/*      */           catch (Throwable e) {
/*  389 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  395 */     if (this.cellSWTPaintListeners == null) {
/*  396 */       return;
/*      */     }
/*      */     
/*  399 */     for (int i = 0; i < this.cellSWTPaintListeners.size(); i++) {
/*      */       try {
/*  401 */         TableCellSWTPaintListener l = (TableCellSWTPaintListener)this.cellSWTPaintListeners.get(i);
/*      */         
/*  403 */         l.cellPaint(gc, this);
/*      */       }
/*      */       catch (Throwable e) {
/*  406 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void addCellClipboardListener(TableCellClipboardListener listener) {
/*      */     try {
/*  413 */       this_mon.enter();
/*      */       
/*  415 */       if (this.cellClipboardListeners == null) {
/*  416 */         this.cellClipboardListeners = new ArrayList(1);
/*      */       }
/*  418 */       this.cellClipboardListeners.add(listener);
/*      */     }
/*      */     finally {
/*  421 */       this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public String getClipboardText() {
/*  426 */     if (isDisposed()) {
/*  427 */       return "";
/*      */     }
/*  429 */     String text = this.tableColumn.getClipboardText(this);
/*  430 */     if (text != null) {
/*  431 */       return text;
/*      */     }
/*      */     try
/*      */     {
/*  435 */       this_mon.enter();
/*      */       
/*  437 */       if (this.cellClipboardListeners != null) {
/*  438 */         for (TableCellClipboardListener l : this.cellClipboardListeners) {
/*      */           try {
/*  440 */             text = l.getClipboardText(this);
/*      */           } catch (Exception e) {
/*  442 */             Debug.out(e);
/*      */           }
/*  444 */           if (text != null) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/*  450 */       this_mon.exit();
/*      */     }
/*  452 */     if (text == null) {
/*  453 */       text = getText();
/*      */     }
/*  455 */     return text;
/*      */   }
/*      */   
/*      */   public void addListeners(Object listenerObject) {
/*  459 */     if ((listenerObject instanceof TableCellDisposeListener)) {
/*  460 */       addDisposeListener((TableCellDisposeListener)listenerObject);
/*      */     }
/*      */     
/*  463 */     if ((listenerObject instanceof TableCellRefreshListener)) {
/*  464 */       addRefreshListener((TableCellRefreshListener)listenerObject);
/*      */     }
/*  466 */     if ((listenerObject instanceof TableCellToolTipListener)) {
/*  467 */       addToolTipListener((TableCellToolTipListener)listenerObject);
/*      */     }
/*  469 */     if ((listenerObject instanceof TableCellMouseMoveListener)) {
/*  470 */       addMouseMoveListener((TableCellMouseMoveListener)listenerObject);
/*      */     }
/*      */     
/*  473 */     if ((listenerObject instanceof TableCellMouseListener)) {
/*  474 */       addMouseListener((TableCellMouseListener)listenerObject);
/*      */     }
/*      */     
/*  477 */     if ((listenerObject instanceof TableCellVisibilityListener)) {
/*  478 */       addVisibilityListener((TableCellVisibilityListener)listenerObject);
/*      */     }
/*  480 */     if ((listenerObject instanceof TableCellSWTPaintListener)) {
/*  481 */       addSWTPaintListener((TableCellSWTPaintListener)listenerObject);
/*      */     }
/*  483 */     if ((listenerObject instanceof TableCellClipboardListener))
/*  484 */       addCellClipboardListener((TableCellClipboardListener)listenerObject);
/*      */   }
/*      */   
/*      */   public void invokeToolTipListeners(int type) {
/*  488 */     if (this.tableColumn == null) {
/*  489 */       return;
/*      */     }
/*  491 */     this.tableColumn.invokeCellToolTipListeners(this, type);
/*      */     
/*  493 */     if ((this.tooltipListeners == null) || (this.tooltipErrLoopCount > 2)) {
/*  494 */       return;
/*      */     }
/*  496 */     int iErrCount = this.tableColumn.getConsecutiveErrCount();
/*  497 */     if (iErrCount > 10) {
/*  498 */       return;
/*      */     }
/*      */     try {
/*  501 */       if (type == 0) {
/*  502 */         for (int i = 0; i < this.tooltipListeners.size(); i++)
/*  503 */           ((TableCellToolTipListener)this.tooltipListeners.get(i)).cellHover(this);
/*      */       } else {
/*  505 */         for (int i = 0; i < this.tooltipListeners.size(); i++)
/*  506 */           ((TableCellToolTipListener)this.tooltipListeners.get(i)).cellHoverComplete(this);
/*      */       }
/*  508 */       this.tooltipErrLoopCount = 0;
/*      */     } catch (Throwable e) {
/*  510 */       this.tooltipErrLoopCount = ((byte)(this.tooltipErrLoopCount + 1));
/*  511 */       this.tableColumn.setConsecutiveErrCount(++iErrCount);
/*  512 */       pluginError(e);
/*  513 */       if (this.tooltipErrLoopCount > 2) {
/*  514 */         Logger.log(new LogEvent(LOGID, 3, "TableCell's tooltip will not be refreshed anymore this session."));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeMouseListeners(TableCellMouseEvent event) {
/*  520 */     ArrayList listeners = event.eventType == 3 ? this.cellMouseMoveListeners : this.cellMouseListeners;
/*      */     
/*  522 */     if (listeners == null) {
/*  523 */       return;
/*      */     }
/*  525 */     if ((event.cell != null) && (event.row == null)) {
/*  526 */       event.row = event.cell.getTableRow();
/*      */     }
/*      */     
/*  529 */     for (int i = 0; i < listeners.size(); i++) {
/*      */       try {
/*  531 */         TableCellMouseListener l = (TableCellMouseListener)listeners.get(i);
/*      */         
/*  533 */         l.cellMouseTrigger(event);
/*      */       }
/*      */       catch (Throwable e) {
/*  536 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeVisibilityListeners(int visibility, boolean invokeColumnListeners)
/*      */   {
/*  543 */     TableColumnCore tc = this.tableColumn;
/*      */     
/*  545 */     if ((invokeColumnListeners) && (tc != null)) {
/*  546 */       tc.invokeCellVisibilityListeners(this, visibility);
/*      */     }
/*      */     
/*  549 */     if (this.cellVisibilityListeners == null) {
/*  550 */       return;
/*      */     }
/*  552 */     for (int i = 0; i < this.cellVisibilityListeners.size(); i++) {
/*      */       try {
/*  554 */         TableCellVisibilityListener l = (TableCellVisibilityListener)this.cellVisibilityListeners.get(i);
/*      */         
/*  556 */         l.cellVisibilityChanged(this, visibility);
/*      */       }
/*      */       catch (Throwable e) {
/*  559 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void dispose() {
/*  565 */     if (isDisposed())
/*      */     {
/*      */ 
/*  568 */       Debug.out("Double disposal!");
/*  569 */       return;
/*      */     }
/*  571 */     setFlag(16);
/*      */     
/*  573 */     TableColumnCore tc = this.tableColumn;
/*      */     
/*  575 */     if (tc != null) {
/*  576 */       tc.invokeCellDisposeListeners(this);
/*      */     }
/*      */     
/*  579 */     if (this.disposeListeners != null) {
/*      */       try {
/*  581 */         for (Iterator iter = this.disposeListeners.iterator(); iter.hasNext();) {
/*  582 */           TableCellDisposeListener listener = (TableCellDisposeListener)iter.next();
/*  583 */           listener.dispose(this);
/*      */         }
/*  585 */         this.disposeListeners = null;
/*      */       } catch (Throwable e) {
/*  587 */         pluginError(e);
/*      */       }
/*      */     }
/*      */     
/*  591 */     this.refreshListeners = null;
/*  592 */     this.tableColumn = null;
/*  593 */     this.tableRow = null;
/*  594 */     this.sortValue = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void debug(final String s)
/*      */   {
/*  601 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  603 */         if (TableCellSWTBase.this.tableRow == null) {
/*  604 */           System.out.println(SystemTime.getCurrentTime() + ": c" + (TableCellSWTBase.this.tableColumn == null ? null : Integer.valueOf(TableCellSWTBase.this.tableColumn.getPosition())) + ";F=" + TableCellSWTBase.this.flagToText(TableCellSWTBase.this.flags, false) + ";" + s);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  609 */           System.out.println(SystemTime.getCurrentTime() + ": r" + TableCellSWTBase.this.tableRow.getIndex() + "c" + (TableCellSWTBase.this.tableColumn == null ? null : Integer.valueOf(TableCellSWTBase.this.tableColumn.getPosition())) + ";r.v?" + (TableCellSWTBase.this.tableRow.isVisible() ? "Y" : "N") + "F=" + TableCellSWTBase.this.flagToText(TableCellSWTBase.this.flags, false) + ";" + s); } } }, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void pluginError(Throwable e)
/*      */   {
/*  620 */     if (this.tableColumn != null) {
/*  621 */       String sTitleLanguageKey = this.tableColumn.getTitleLanguageKey();
/*      */       
/*  623 */       String sPosition = this.tableColumn.getPosition() + " (" + MessageText.getString(sTitleLanguageKey) + ")";
/*      */       
/*  625 */       Logger.log(new LogEvent(LOGID, "Table Cell Plugin for Column #" + sPosition + " generated an exception ", e));
/*      */     }
/*      */     else {
/*  628 */       Logger.log(new LogEvent(LOGID, "Table Cell Plugin generated an exception ", e));
/*      */     }
/*      */   }
/*      */   
/*      */   protected void pluginError(String s) {
/*  633 */     String sPosition = "r" + this.tableRow.getIndex() + "c" + this.tableColumn.getPosition();
/*      */     
/*  635 */     Logger.log(new LogEvent(LOGID, 3, "Table Cell Plugin for Column #" + sPosition + ":" + s + "\n  " + Debug.getStackTrace(true, true)));
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean refresh()
/*      */   {
/*  641 */     return refresh(true);
/*      */   }
/*      */   
/*      */   public boolean refresh(boolean bDoGraphics) {
/*      */     boolean isRowShown;
/*      */     boolean isRowShown;
/*  647 */     if (this.tableRow != null) {
/*  648 */       TableView view = this.tableRow.getView();
/*  649 */       isRowShown = view.isRowVisible(this.tableRow);
/*      */     } else {
/*  651 */       isRowShown = true;
/*      */     }
/*  653 */     boolean isCellShown = (isRowShown) && (isShown());
/*  654 */     return refresh(bDoGraphics, isRowShown, isCellShown);
/*      */   }
/*      */   
/*      */   public boolean refresh(boolean bDoGraphics, boolean bRowVisible)
/*      */   {
/*  659 */     boolean isCellShown = (bRowVisible) && (isShown());
/*  660 */     return refresh(bDoGraphics, bRowVisible, isCellShown);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean refresh(boolean bDoGraphics, boolean bRowVisible, boolean bCellVisible)
/*      */   {
/*  669 */     TableColumnCore tc = this.tableColumn;
/*      */     
/*  671 */     if (tc == null) {
/*  672 */       return false;
/*      */     }
/*  674 */     boolean ret = getVisuallyChangedSinceRefresh();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  679 */     int iErrCount = 0;
/*  680 */     if (this.refreshErrLoopCount > 2) {
/*  681 */       return ret;
/*      */     }
/*      */     
/*  684 */     iErrCount = tc.getConsecutiveErrCount();
/*  685 */     if (iErrCount > 10) {
/*  686 */       this.refreshErrLoopCount = 3;
/*  687 */       return ret;
/*      */     }
/*      */     
/*  690 */     if (this.bInRefresh)
/*      */     {
/*      */ 
/*      */ 
/*  694 */       if (this.bDebug)
/*  695 */         debug("Calling Refresh from Refresh :) Skipping.");
/*  696 */       return ret;
/*      */     }
/*      */     try {
/*  699 */       this.bInRefresh = true;
/*  700 */       if (ret) {
/*  701 */         long now = SystemTime.getCurrentTime();
/*  702 */         if (now - this.lastRefresh < MAX_REFRESHES_WITHIN_MS) {
/*  703 */           this.numFastRefreshes += 1;
/*  704 */           if (this.numFastRefreshes >= MAX_REFRESHES) {
/*  705 */             if (this.numFastRefreshes % MAX_REFRESHES == 0) {
/*  706 */               pluginError("this plugin is crazy. tried to refresh " + this.numFastRefreshes + " times in " + (now - this.lastRefresh) + "ms");
/*      */             }
/*      */             
/*      */ 
/*  710 */             return ret;
/*      */           }
/*      */         } else {
/*  713 */           this.numFastRefreshes = 0;
/*  714 */           this.lastRefresh = now;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  719 */       if ((bCellVisible) && (!isUpToDate())) {
/*  720 */         if (this.bDebug)
/*  721 */           debug("Setting Invalid because visible & not up to date");
/*  722 */         clearFlag(1);
/*  723 */         setFlag(8);
/*  724 */       } else if ((!bCellVisible) && (isUpToDate())) {
/*  725 */         if (this.bDebug) {
/*  726 */           debug("Setting not up to date because cell not visible " + Debug.getCompressedStackTrace());
/*      */         }
/*  728 */         clearFlag(8);
/*      */       }
/*      */       
/*  731 */       if (this.bDebug) {
/*  732 */         debug("Cell Valid?" + hasFlag(1) + "; Visible?" + this.tableRow.isVisible() + "/" + isShown());
/*      */       }
/*      */       
/*  735 */       int iInterval = tc.getRefreshInterval();
/*  736 */       if ((iInterval == -3) && (!hasFlag(33)) && (hasFlag(2)) && (this.sortValue != null) && (tc.getType() == 3))
/*      */       {
/*      */ 
/*      */ 
/*  740 */         if (bCellVisible) {
/*  741 */           if (this.bDebug)
/*  742 */             debug("fast refresh: setText");
/*  743 */           ret = setText((String)this.sortValue);
/*  744 */           setFlag(1);
/*      */         }
/*  746 */       } else if ((iInterval == -2) || ((iInterval == -1) && (bDoGraphics)) || ((iInterval > 0) && (this.loopFactor % iInterval == 0)) || (!hasFlag(1)) || (hasFlag(32)))
/*      */       {
/*      */ 
/*      */ 
/*  750 */         boolean bWasValid = isValid();
/*      */         
/*  752 */         ret = hasFlag(32);
/*  753 */         if (ret) {
/*  754 */           clearFlag(32);
/*      */         }
/*      */         
/*  757 */         if (this.bDebug) {
/*  758 */           debug("invoke refresh; wasValid? " + bWasValid);
/*      */         }
/*      */         
/*  761 */         long lTimeStart = Constants.isCVSVersion() ? SystemTime.getMonotonousTime() : 0L;
/*      */         
/*  763 */         tc.invokeCellRefreshListeners(this, !bCellVisible);
/*  764 */         if (this.refreshListeners != null) {
/*  765 */           for (TableCellRefreshListener l : this.refreshListeners) {
/*  766 */             if ((l instanceof TableCellLightRefreshListener)) {
/*  767 */               ((TableCellLightRefreshListener)l).refresh(this, !bCellVisible);
/*      */             } else {
/*  769 */               l.refresh(this);
/*      */             }
/*      */           }
/*      */         }
/*  773 */         if (Constants.isCVSVersion()) {
/*  774 */           long lTimeEnd = SystemTime.getMonotonousTime();
/*  775 */           tc.addRefreshTime(lTimeEnd - lTimeStart);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  780 */         if ((!bWasValid) && (!hasFlag(32))) {
/*  781 */           setFlag(1);
/*      */         }
/*      */       }
/*  784 */       else if ((bCellVisible) && (this.bDebug)) {
/*  785 */         debug("Skipped refresh; Interval=" + iInterval);
/*      */       }
/*      */       
/*  788 */       this.loopFactor = ((byte)(this.loopFactor + 1));
/*  789 */       this.refreshErrLoopCount = 0;
/*  790 */       if (iErrCount > 0) {
/*  791 */         tc.setConsecutiveErrCount(0);
/*      */       }
/*      */       
/*  794 */       ret |= getVisuallyChangedSinceRefresh();
/*  795 */       if (this.bDebug) {
/*  796 */         debug("refresh done; visual change? " + ret + ";" + Debug.getCompressedStackTrace());
/*      */       }
/*      */     } catch (Throwable e) {
/*  799 */       this.refreshErrLoopCount = ((byte)(this.refreshErrLoopCount + 1));
/*  800 */       if (tc != null) {
/*  801 */         tc.setConsecutiveErrCount(++iErrCount);
/*      */       }
/*  803 */       pluginError(e);
/*  804 */       if (this.refreshErrLoopCount > 2) {
/*  805 */         Logger.log(new LogEvent(LOGID, 3, "TableCell will not be refreshed anymore this session."));
/*      */       }
/*      */     } finally {
/*  808 */       this.bInRefresh = false;
/*      */     }
/*      */     
/*  811 */     return ret;
/*      */   }
/*      */   
/*      */   public boolean setSortValue(Comparable valueToSort) {
/*  815 */     if (this.tableColumn == null) {
/*  816 */       return false;
/*      */     }
/*  818 */     if (!this.tableColumn.isSortValueLive())
/*      */     {
/*  820 */       if ((!(valueToSort instanceof Number)) && (!(valueToSort instanceof String)) && (!(valueToSort instanceof TableColumnSortObject)))
/*      */       {
/*  822 */         this.tableColumn.setSortValueLive(true);
/*      */       }
/*      */     }
/*  825 */     return _setSortValue(valueToSort);
/*      */   }
/*      */   
/*      */   private boolean _setSortValue(Comparable valueToSort) {
/*  829 */     if (isDisposed()) {
/*  830 */       return false;
/*      */     }
/*      */     
/*  833 */     if (this.sortValue == valueToSort) {
/*  834 */       return false;
/*      */     }
/*  836 */     if (hasFlag(2)) {
/*  837 */       clearFlag(2);
/*  838 */       if ((this.sortValue instanceof String))
/*      */       {
/*      */ 
/*  841 */         setText((String)this.sortValue);
/*      */       }
/*      */     }
/*  844 */     if (((valueToSort instanceof String)) && ((this.sortValue instanceof String)) && (this.sortValue.equals(valueToSort)))
/*      */     {
/*  846 */       return false;
/*      */     }
/*      */     
/*  849 */     if (((valueToSort instanceof Number)) && ((this.sortValue instanceof Number)) && (this.sortValue.equals(valueToSort)))
/*      */     {
/*  851 */       return false;
/*      */     }
/*      */     
/*  854 */     if (this.bDebug) {
/*  855 */       debug("Setting SortValue to " + (valueToSort == null ? "null" : valueToSort.getClass().getName()));
/*      */     }
/*      */     
/*  858 */     this.tableColumn.setLastSortValueChange(SystemTime.getCurrentTime());
/*  859 */     this.sortValue = valueToSort;
/*      */     
/*      */ 
/*      */ 
/*  863 */     if ((this.cellSWTPaintListeners != null) || (this.tableColumn.hasCellOtherListeners("SWTPaint")))
/*      */     {
/*  865 */       setFlag(64);
/*      */     }
/*      */     
/*      */ 
/*  869 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setSortValue(long valueToSort) {
/*  873 */     if (isDisposed()) {
/*  874 */       return false;
/*      */     }
/*      */     
/*  877 */     if (((this.sortValue instanceof Long)) && (((Long)this.sortValue).longValue() == valueToSort))
/*      */     {
/*  879 */       return false;
/*      */     }
/*  881 */     return _setSortValue(Long.valueOf(valueToSort));
/*      */   }
/*      */   
/*      */   public boolean setSortValue(float valueToSort) {
/*  885 */     if (isDisposed()) {
/*  886 */       return false;
/*      */     }
/*      */     
/*  889 */     if (((this.sortValue instanceof Float)) && (((Float)this.sortValue).floatValue() == valueToSort))
/*      */     {
/*  891 */       return false;
/*      */     }
/*  893 */     return _setSortValue(new Float(valueToSort));
/*      */   }
/*      */   
/*      */   public Comparable getSortValue() {
/*  897 */     return this.sortValue;
/*      */   }
/*      */   
/*      */   public boolean isValid()
/*      */   {
/*  902 */     return (this.flags & 0x1) != 0;
/*      */   }
/*      */   
/*      */   public boolean isDisposed()
/*      */   {
/*  907 */     return (this.flags & 0x10) != 0;
/*      */   }
/*      */   
/*      */   public boolean hasFlag(int flag) {
/*  911 */     return (this.flags & flag) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFlag(int flag)
/*      */   {
/*  919 */     this.flags |= flag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clearFlag(int flag)
/*      */   {
/*  927 */     this.flags &= (flag ^ 0xFFFFFFFF);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void invalidate()
/*      */   {
/*  935 */     if (isDisposed()) {
/*  936 */       return;
/*      */     }
/*      */     
/*  939 */     invalidate(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void invalidate(boolean bMustRefresh)
/*      */   {
/*  949 */     if ((this.flags & 0x41) == 64) {
/*  950 */       if (bMustRefresh) {
/*  951 */         if ((this.flags & 0x20) == 0) {}
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  958 */         return;
/*      */       }
/*      */     }
/*  961 */     clearFlag(1);
/*      */     
/*  963 */     if (this.bDebug) {
/*  964 */       debug("Invalidate Cell;" + bMustRefresh);
/*      */     }
/*  966 */     if (bMustRefresh) {
/*  967 */       setFlag(96);
/*      */     } else {
/*  969 */       setFlag(64);
/*      */     }
/*      */   }
/*      */   
/*      */   public void refreshAsync()
/*      */   {
/*  975 */     if (this.bInRefreshAsync)
/*      */     {
/*  977 */       if (this.restartRefresh < Byte.MAX_VALUE) {
/*  978 */         this.restartRefresh = ((byte)(this.restartRefresh + 1));
/*      */       }
/*  980 */       return;
/*      */     }
/*  982 */     this.bInRefreshAsync = true;
/*      */     
/*  984 */     AERunnable runnable = new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  987 */         TableCellSWTBase.this.restartRefresh = 0;
/*  988 */         TableCellSWTBase.this.refresh(true);
/*  989 */         TableCellSWTBase.this.bInRefreshAsync = false;
/*      */         
/*  991 */         if (TableCellSWTBase.this.restartRefresh > 0) {
/*  992 */           TableCellSWTBase.this.refreshAsync();
/*      */         }
/*      */       }
/*  995 */     };
/*  996 */     Utils.execSWTThreadLater(25, runnable);
/*      */   }
/*      */   
/*      */   public void setUpToDate(boolean upToDate) {
/* 1000 */     if (this.bDebug)
/* 1001 */       debug("set up to date to " + upToDate);
/* 1002 */     if (upToDate) {
/* 1003 */       setFlag(8);
/*      */     } else {
/* 1005 */       clearFlag(8);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isUpToDate() {
/* 1010 */     return hasFlag(8);
/*      */   }
/*      */   
/*      */   public boolean getVisuallyChangedSinceRefresh() {
/* 1014 */     return hasFlag(64);
/*      */   }
/*      */   
/*      */   public void clearVisuallyChangedSinceRefresh() {
/* 1018 */     clearFlag(64);
/*      */   }
/*      */   
/*      */ 
/*      */   public int compareTo(Object o)
/*      */   {
/*      */     try
/*      */     {
/* 1026 */       Comparable ourSortValue = getSortValue();
/* 1027 */       Comparable otherSortValue = ((TableCellSWTBase)o).getSortValue();
/* 1028 */       if (((ourSortValue instanceof String)) && ((otherSortValue instanceof String)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1033 */         Collator collator = Collator.getInstance(Locale.getDefault());
/* 1034 */         return collator.compare(ourSortValue, otherSortValue);
/*      */       }
/*      */       try {
/* 1037 */         return ourSortValue.compareTo(otherSortValue);
/*      */       }
/*      */       catch (ClassCastException e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1048 */       return 0;
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1045 */       System.out.println("Could not compare cells");
/* 1046 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean needsPainting()
/*      */   {
/* 1052 */     if (isDisposed()) {
/* 1053 */       return false;
/*      */     }
/*      */     
/* 1056 */     if ((this.cellSWTPaintListeners != null) || (this.tableColumn.hasCellOtherListeners("SWTPaint")))
/*      */     {
/* 1058 */       return true;
/*      */     }
/*      */     
/* 1061 */     return getGraphic() != null;
/*      */   }
/*      */   
/*      */   public boolean setText(String text) {
/* 1065 */     if (isDisposed()) {
/* 1066 */       return false;
/*      */     }
/*      */     
/* 1069 */     if (text == null)
/* 1070 */       text = "";
/* 1071 */     boolean bChanged = false;
/*      */     
/* 1073 */     if ((hasFlag(2)) && (!text.equals(this.sortValue))) {
/* 1074 */       bChanged = true;
/* 1075 */       this.sortValue = text;
/* 1076 */       this.tableColumn.setLastSortValueChange(SystemTime.getCurrentTime());
/* 1077 */       if (this.bDebug) {
/* 1078 */         debug("Setting SortValue to text;");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1089 */     if ((uiSetText(text)) && (!hasFlag(2))) {
/* 1090 */       bChanged = true;
/*      */     }
/* 1092 */     if (this.bDebug) {
/* 1093 */       debug("setText (" + bChanged + ") : " + text);
/*      */     }
/*      */     
/* 1096 */     if (bChanged) {
/* 1097 */       setFlag(64);
/*      */     }
/*      */     
/* 1100 */     boolean do_auto = this.tableColumn == null ? false : this.tableColumn.doesAutoTooltip();
/*      */     
/*      */ 
/*      */ 
/* 1104 */     if (!do_auto) {
/* 1105 */       if (hasFlag(4)) {
/* 1106 */         this.oToolTip = null;
/* 1107 */         clearFlag(4);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1112 */       this.oToolTip = text;
/* 1113 */       setFlag(4);
/*      */     }
/*      */     
/* 1116 */     return bChanged;
/*      */   }
/*      */   
/*      */   public void setToolTip(Object tooltip) {
/* 1120 */     this.oToolTip = tooltip;
/*      */     
/* 1122 */     if (tooltip == null) {
/* 1123 */       setFlag(4);
/*      */     } else {
/* 1125 */       clearFlag(4);
/*      */     }
/*      */   }
/*      */   
/*      */   public Object getToolTip() {
/* 1130 */     return this.oToolTip;
/*      */   }
/*      */   
/*      */   public Object getDefaultToolTip() {
/* 1134 */     return this.defaultToolTip;
/*      */   }
/*      */   
/*      */   public void setDefaultToolTip(Object tt) {
/* 1138 */     this.defaultToolTip = tt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract boolean uiSetText(String paramString);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void doPaint(GC gc)
/*      */   {
/* 1154 */     if (this.bDebug) {
/* 1155 */       debug("doPaint up2date:" + hasFlag(8) + ";v:" + hasFlag(1) + ";rl=" + this.refreshListeners);
/*      */     }
/*      */     
/* 1158 */     invokeSWTPaintListeners(gc);
/*      */   }
/*      */   
/*      */   public int getTextAlpha() {
/* 1162 */     return this.textAlpha;
/*      */   }
/*      */   
/*      */   public void setTextAlpha(int textOpacity) {
/* 1166 */     this.textAlpha = textOpacity;
/*      */   }
/*      */   
/*      */ 
/*      */   public TableRowSWT getTableRowSWT()
/*      */   {
/* 1172 */     if ((this.tableRow instanceof TableRowSWT)) {
/* 1173 */       return (TableRowSWT)this.tableRow;
/*      */     }
/* 1175 */     return null;
/*      */   }
/*      */   
/*      */   public TableRowCore getTableRowCore() {
/* 1179 */     return this.tableRow;
/*      */   }
/*      */   
/*      */   private String flagToText(int flag, boolean onlySet) {
/* 1183 */     StringBuilder sb = new StringBuilder();
/* 1184 */     sb.append(onlySet ? ' ' : (flag & 0x10) > 0 ? 'D' : 'd');
/* 1185 */     sb.append(onlySet ? ' ' : (flag & 0x20) > 0 ? 'M' : 'm');
/* 1186 */     sb.append(onlySet ? ' ' : (flag & 0x2) > 0 ? 'S' : 's');
/* 1187 */     sb.append(onlySet ? ' ' : (flag & 0x4) > 0 ? 'T' : 't');
/* 1188 */     sb.append(onlySet ? ' ' : (flag & 0x8) > 0 ? 'U' : 'u');
/* 1189 */     sb.append(onlySet ? ' ' : (flag & 0x1) > 0 ? 'V' : 'v');
/* 1190 */     sb.append(onlySet ? Character.valueOf(' ') : (flag & 0x40) > 0 ? "VC" : "vc");
/* 1191 */     return sb.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public abstract int getWidthRaw();
/*      */   
/*      */ 
/*      */   public void setFillCell(boolean doFillCell)
/*      */   {
/* 1201 */     this.doFillCell = doFillCell;
/*      */   }
/*      */   
/*      */   public boolean getFillCell() {
/* 1205 */     return this.doFillCell;
/*      */   }
/*      */   
/*      */   public TableColumnCore getTableColumnCore()
/*      */   {
/* 1210 */     return this.tableColumn;
/*      */   }
/*      */   
/*      */   public boolean setCursorID(int cursorID) {
/* 1214 */     if (this.iCursorID == cursorID) {
/* 1215 */       return false;
/*      */     }
/* 1217 */     this.iCursorID = cursorID;
/* 1218 */     return true;
/*      */   }
/*      */   
/*      */   public int getCursorID()
/*      */   {
/* 1223 */     return this.iCursorID;
/*      */   }
/*      */   
/*      */   public void setMouseOver(boolean b) {
/* 1227 */     this.mouseOver = b;
/*      */   }
/*      */   
/*      */   public boolean isMouseOver() {
/* 1231 */     if ((this.tableRow != null) && (!this.tableRow.isVisible()))
/*      */     {
/* 1233 */       return false;
/*      */     }
/* 1235 */     return this.mouseOver;
/*      */   }
/*      */   
/*      */   public boolean setIcon(Image img) {
/* 1239 */     if (isInvisibleAndCanRefresh()) {
/* 1240 */       return false;
/*      */     }
/* 1242 */     this.icon = img;
/*      */     
/* 1244 */     this.graphic = null;
/* 1245 */     setFlag(64);
/* 1246 */     return true;
/*      */   }
/*      */   
/*      */   public Image getIcon() {
/* 1250 */     return this.icon;
/*      */   }
/*      */   
/*      */   public boolean setGraphic(Image img)
/*      */   {
/* 1255 */     return setGraphic(new UISWTGraphicImpl(img));
/*      */   }
/*      */   
/*      */   public boolean setGraphic(Graphic img)
/*      */   {
/* 1260 */     if ((img != null) && (isDisposed())) {
/* 1261 */       return false;
/*      */     }
/*      */     
/* 1264 */     if ((this.tableColumn == null) || (this.tableColumn.getType() != 2))
/*      */     {
/* 1266 */       return false;
/*      */     }
/*      */     
/* 1269 */     if ((img == this.graphic) && (this.numFastRefreshes >= MAX_REFRESHES)) {
/* 1270 */       pluginError("TableCellImpl::setGraphic to same Graphic object. Forcing refresh.");
/*      */     }
/*      */     
/*      */ 
/* 1274 */     boolean changed = (img == this.graphic) || ((img != null) && (!img.equals(this.graphic))) || ((this.graphic != null) && (!this.graphic.equals(img)));
/*      */     
/* 1276 */     this.graphic = img;
/*      */     
/* 1278 */     if (changed) {
/* 1279 */       setFlag(64);
/* 1280 */       redraw();
/*      */     }
/*      */     
/* 1283 */     return changed;
/*      */   }
/*      */   
/*      */   public Graphic getGraphic() {
/* 1287 */     return this.graphic;
/*      */   }
/*      */   
/*      */   public Image getGraphicSWT() {
/* 1291 */     return (this.graphic instanceof UISWTGraphic) ? ((UISWTGraphic)this.graphic).getImage() : null;
/*      */   }
/*      */   
/*      */   public boolean isInvisibleAndCanRefresh()
/*      */   {
/* 1296 */     return (!isDisposed()) && (!isShown()) && ((this.refreshListeners != null) || (this.tableColumn.hasCellRefreshListener()));
/*      */   }
/*      */   
/*      */   public int[] getBackground()
/*      */   {
/* 1301 */     Color color = getBackgroundSWT();
/*      */     
/* 1303 */     if (color == null) {
/* 1304 */       return null;
/*      */     }
/*      */     
/* 1307 */     return new int[] { color.getRed(), color.getGreen(), color.getBlue() };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] getForeground()
/*      */   {
/* 1316 */     Color color = getForegroundSWT();
/*      */     
/* 1318 */     if (color == null) {
/* 1319 */       return new int[3];
/*      */     }
/*      */     
/* 1322 */     return new int[] { color.getRed(), color.getGreen(), color.getBlue() };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean setForeground(int red, int green, int blue)
/*      */   {
/* 1331 */     if (isInvisibleAndCanRefresh()) {
/* 1332 */       return false;
/*      */     }
/*      */     
/* 1335 */     if ((red < 0) || (green < 0) || (blue < 0)) {
/* 1336 */       return setForeground((Color)null);
/*      */     }
/* 1338 */     return setForeground(new RGB(red, green, blue));
/*      */   }
/*      */   
/*      */   private boolean setForeground(final RGB rgb) {
/* 1342 */     Color colorFG = getForegroundSWT();
/* 1343 */     boolean changed = (colorFG == null) || (colorFG.isDisposed()) || (!colorFG.getRGB().equals(rgb));
/*      */     
/* 1345 */     if (changed) {
/* 1346 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/* 1348 */           TableCellSWTBase.this.setForeground(ColorCache.getColor(Display.getCurrent(), rgb));
/*      */         }
/*      */       });
/*      */     }
/* 1352 */     return changed;
/*      */   }
/*      */   
/*      */   public boolean setForeground(int[] rgb)
/*      */   {
/* 1357 */     if ((rgb == null) || (rgb.length < 3)) {
/* 1358 */       return setForeground((Color)null);
/*      */     }
/* 1360 */     return setForeground(rgb[0], rgb[1], rgb[2]);
/*      */   }
/*      */   
/*      */   public boolean setForegroundToErrorColor() {
/* 1364 */     return setForeground(Colors.colorError);
/*      */   }
/*      */   
/*      */   public int[] getMouseOffset() {
/* 1368 */     Point ofs = ((TableViewSWT)this.tableRow.getView()).getTableCellMouseOffset(this);
/* 1369 */     return new int[] { ofs.x, ofs == null ? null : ofs.y };
/*      */   }
/*      */   
/*      */   public String getObfusticatedText() {
/* 1373 */     if (isDisposed()) {
/* 1374 */       return null;
/*      */     }
/* 1376 */     if (this.tableColumn.isObfusticated()) {
/* 1377 */       if ((this.tableColumn instanceof ObfusticateCellText)) {
/* 1378 */         return ((ObfusticateCellText)this.tableColumn).getObfusticatedText(this);
/*      */       }
/*      */       
/* 1381 */       return "";
/*      */     }
/* 1383 */     return null;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/TableCellSWTBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */