/*      */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*      */ 
/*      */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableStructureEventDispatcher;
/*      */ import com.aelitis.azureus.ui.common.table.TableStructureModificationListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.KeyListener;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuBuilder;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseEvent;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseListener;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.menus.MenuItemImpl;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.views.columnsetup.TableColumnSetupWindow;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWTPaintListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableSelectedRowsListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTFilter;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.utils.TableContextMenuManager;
/*      */ 
/*      */ public class TableViewSWT_Common implements org.eclipse.swt.events.MouseListener, org.eclipse.swt.events.MouseMoveListener, org.eclipse.swt.events.SelectionListener, KeyListener
/*      */ {
/*      */   TableViewSWT<?> tv;
/*   56 */   private long lCancelSelectionTriggeredOn = -1L;
/*   57 */   private long lastSelectionTriggeredOn = -1L;
/*      */   
/*      */   private static final int ASYOUTYPE_MODE_FIND = 0;
/*      */   
/*      */   private static final int ASYOUTYPE_MODE_FILTER = 1;
/*      */   private static final int ASYOUTYPE_MODE = 1;
/*      */   private static final int ASYOUTYPE_UPDATEDELAY = 300;
/*   64 */   private static final org.eclipse.swt.graphics.Color COLOR_FILTER_REGEX = org.gudy.azureus2.ui.swt.mainwindow.Colors.fadedYellow;
/*      */   
/*      */   private static Font FONT_NO_REGEX;
/*      */   private static Font FONT_REGEX;
/*      */   private static Font FONT_REGEX_ERROR;
/*   69 */   private List<TableViewSWTMenuFillListener> listenersMenuFill = new ArrayList(1);
/*      */   
/*      */ 
/*   72 */   private List<KeyListener> listenersKey = new ArrayList(1);
/*      */   
/*      */   private ArrayList<TableRowMouseListener> rowMouseListeners;
/*      */   
/*   76 */   private static AEMonitor mon_RowMouseListener = new AEMonitor("rml");
/*      */   
/*   78 */   private static AEMonitor mon_RowPaintListener = new AEMonitor("rpl");
/*      */   
/*   80 */   public int xAdj = 0;
/*   81 */   public int yAdj = 0;
/*      */   
/*      */   private ArrayList<TableRowSWTPaintListener> rowPaintListeners;
/*      */   
/*      */   public TableViewSWT_Common(TableViewSWT<?> tv)
/*      */   {
/*   87 */     this.tv = tv;
/*      */   }
/*      */   
/*   90 */   long lastMouseDblClkEventTime = 0L;
/*      */   
/*   92 */   public void mouseDoubleClick(MouseEvent e) { long time = e.time & 0xFFFFFFFF;
/*   93 */     long diff = time - this.lastMouseDblClkEventTime;
/*      */     
/*      */ 
/*   96 */     if ((diff <= e.display.getDoubleClickTime()) && (diff >= 0L)) {
/*   97 */       return;
/*      */     }
/*   99 */     this.lastMouseDblClkEventTime = time;
/*      */     
/*  101 */     TableColumnCore tc = this.tv.getTableColumnByOffset(e.x);
/*  102 */     TableCellCore cell = this.tv.getTableCell(e.x, e.y);
/*  103 */     if ((cell != null) && (tc != null)) {
/*  104 */       TableCellMouseEvent event = createMouseEvent(cell, e, 2, false);
/*      */       
/*  106 */       if (event != null) {
/*  107 */         tc.invokeCellMouseListeners(event);
/*  108 */         cell.invokeMouseListeners(event);
/*  109 */         if (event.skipCoreFunctionality) {
/*  110 */           this.lCancelSelectionTriggeredOn = System.currentTimeMillis();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*  116 */   long lastMouseUpEventTime = 0L;
/*  117 */   Point lastMouseUpPos = new Point(0, 0);
/*  118 */   boolean mouseDown = false;
/*  119 */   TableRowSWT mouseDownOnRow = null;
/*      */   TableRowCore lastClickRow;
/*      */   
/*      */   public void mouseUp(MouseEvent e) {
/*  123 */     if (!this.mouseDown) {
/*  124 */       return;
/*      */     }
/*  126 */     this.mouseDown = false;
/*      */     
/*  128 */     TableColumnCore tc = this.tv.getTableColumnByOffset(e.x);
/*  129 */     TableCellCore cell = this.tv.getTableCell(e.x, e.y);
/*      */     
/*  131 */     mouseUp(this.mouseDownOnRow, cell, e.button, e.stateMask);
/*      */     
/*  133 */     if (e.button == 1) {
/*  134 */       long time = e.time & 0xFFFFFFFF;
/*  135 */       long diff = time - this.lastMouseUpEventTime;
/*  136 */       if ((diff <= e.display.getDoubleClickTime()) && (diff >= 0L) && (this.lastMouseUpPos.x == e.x) && (this.lastMouseUpPos.y == e.y))
/*      */       {
/*      */ 
/*      */ 
/*  140 */         runDefaultAction(e.stateMask);
/*  141 */         return;
/*      */       }
/*  143 */       this.lastMouseUpEventTime = time;
/*  144 */       this.lastMouseUpPos = new Point(e.x, e.y);
/*      */     }
/*      */     
/*  147 */     if ((cell != null) && (tc != null)) {
/*  148 */       TableCellMouseEvent event = createMouseEvent(cell, e, 1, false);
/*      */       
/*  150 */       if (event != null) {
/*  151 */         tc.invokeCellMouseListeners(event);
/*  152 */         cell.invokeMouseListeners(event);
/*  153 */         if (event.skipCoreFunctionality) {
/*  154 */           this.lCancelSelectionTriggeredOn = System.currentTimeMillis();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void mouseDown(MouseEvent e)
/*      */   {
/*  163 */     this.mouseDown = true;
/*      */     
/*      */ 
/*      */ 
/*  167 */     TableRowSWT row = this.mouseDownOnRow = this.tv.getTableRow(e.x, e.y, false);
/*  168 */     TableCellCore cell = this.tv.getTableCell(e.x, e.y);
/*  169 */     TableColumnCore tc = cell == null ? null : cell.getTableColumnCore();
/*      */     
/*  171 */     mouseDown(row, cell, e.button, e.stateMask);
/*      */     
/*  173 */     if (row == null) {
/*  174 */       this.tv.setSelectedRows(new TableRowCore[0]);
/*      */     }
/*      */     
/*  177 */     this.tv.editCell(null, -1);
/*      */     
/*  179 */     if ((cell != null) && (tc != null)) {
/*  180 */       TableCellMouseEvent event = createMouseEvent(cell, e, 0, false);
/*      */       
/*  182 */       if (event != null) {
/*  183 */         tc.invokeCellMouseListeners(event);
/*  184 */         cell.invokeMouseListeners(event);
/*  185 */         this.tv.invokeRowMouseListener(event);
/*  186 */         if (event.skipCoreFunctionality) {
/*  187 */           this.lCancelSelectionTriggeredOn = System.currentTimeMillis();
/*      */         }
/*      */       }
/*  190 */       if ((tc.hasInplaceEditorListener()) && (e.button == 1) && (this.lastClickRow == cell.getTableRowCore()))
/*      */       {
/*  192 */         this.tv.editCell(this.tv.getTableColumnByOffset(e.x), cell.getTableRowCore().getIndex());
/*      */       }
/*  194 */       if (e.button == 1) {
/*  195 */         this.lastClickRow = cell.getTableRowCore();
/*      */       }
/*  197 */     } else if (row != null) {
/*  198 */       TableRowMouseEvent event = createMouseEvent(row, e, 0, false);
/*      */       
/*  200 */       if (event != null) {
/*  201 */         this.tv.invokeRowMouseListener(event);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void mouseDown(TableRowSWT row, TableCellCore cell, int button, int stateMask) {}
/*      */   
/*      */ 
/*      */   public void mouseUp(TableRowCore row, TableCellCore cell, int button, int stateMask) {}
/*      */   
/*      */ 
/*      */   private TableCellMouseEvent createMouseEvent(TableCellCore cell, MouseEvent e, int type, boolean allowOOB)
/*      */   {
/*  216 */     TableCellMouseEvent event = new TableCellMouseEvent();
/*  217 */     event.cell = cell;
/*  218 */     if (cell != null) {
/*  219 */       event.row = cell.getTableRow();
/*      */     }
/*  221 */     event.eventType = type;
/*  222 */     event.button = e.button;
/*      */     
/*  224 */     event.keyboardState = e.stateMask;
/*  225 */     event.skipCoreFunctionality = false;
/*  226 */     if ((cell instanceof TableCellSWT)) {
/*  227 */       Rectangle r = ((TableCellSWT)cell).getBounds();
/*  228 */       if (r == null) {
/*  229 */         return event;
/*      */       }
/*  231 */       event.x = (e.x - r.x - this.xAdj);
/*  232 */       if ((!allowOOB) && (event.x < 0)) {
/*  233 */         return null;
/*      */       }
/*  235 */       event.y = (e.y - r.y - this.yAdj);
/*  236 */       if ((!allowOOB) && (event.y < 0)) {
/*  237 */         return null;
/*      */       }
/*      */     }
/*      */     
/*  241 */     return event;
/*      */   }
/*      */   
/*      */   private TableRowMouseEvent createMouseEvent(TableRowSWT row, MouseEvent e, int type, boolean allowOOB)
/*      */   {
/*  246 */     TableCellMouseEvent event = new TableCellMouseEvent();
/*  247 */     event.row = row;
/*  248 */     event.eventType = type;
/*  249 */     event.button = e.button;
/*      */     
/*  251 */     event.keyboardState = e.stateMask;
/*  252 */     event.skipCoreFunctionality = false;
/*  253 */     if (row != null) {
/*  254 */       Rectangle r = row.getBounds();
/*  255 */       event.x = (e.x - r.x - this.xAdj);
/*  256 */       if ((!allowOOB) && (event.x < 0)) {
/*  257 */         return null;
/*      */       }
/*  259 */       event.y = (e.y - r.y - this.yAdj);
/*  260 */       if ((!allowOOB) && (event.y < 0)) {
/*  261 */         return null;
/*      */       }
/*      */     }
/*      */     
/*  265 */     return event;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  270 */   TableCellCore lastCell = null;
/*      */   
/*  272 */   int lastCursorID = 0;
/*      */   
/*      */   public void mouseMove(MouseEvent e) {
/*  275 */     this.lCancelSelectionTriggeredOn = -1L;
/*  276 */     if (this.tv.isDragging()) {
/*  277 */       return;
/*      */     }
/*      */     try {
/*  280 */       TableCellCore cell = this.tv.getTableCell(e.x, e.y);
/*      */       
/*  282 */       if (cell != this.lastCell) {
/*  283 */         if ((this.lastCell != null) && (!this.lastCell.isDisposed())) {
/*  284 */           TableCellMouseEvent event = createMouseEvent(this.lastCell, e, 5, true);
/*      */           
/*  286 */           if (event != null) {
/*  287 */             ((TableCellSWT)this.lastCell).setMouseOver(false);
/*  288 */             TableColumnCore tc = (TableColumnCore)this.lastCell.getTableColumn();
/*  289 */             if (tc != null) {
/*  290 */               tc.invokeCellMouseListeners(event);
/*      */             }
/*  292 */             this.lastCell.invokeMouseListeners(event);
/*      */           }
/*      */         }
/*      */         
/*  296 */         if ((cell != null) && (!cell.isDisposed())) {
/*  297 */           TableCellMouseEvent event = createMouseEvent(cell, e, 4, false);
/*      */           
/*  299 */           if (event != null) {
/*  300 */             ((TableCellSWT)cell).setMouseOver(true);
/*  301 */             TableColumnCore tc = (TableColumnCore)cell.getTableColumn();
/*  302 */             if (tc != null) {
/*  303 */               tc.invokeCellMouseListeners(event);
/*      */             }
/*  305 */             cell.invokeMouseListeners(event);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  310 */       int iCursorID = 0;
/*  311 */       if (cell == null) {
/*  312 */         this.lastCell = null;
/*  313 */       } else if (cell == this.lastCell) {
/*  314 */         iCursorID = this.lastCursorID;
/*      */       } else {
/*  316 */         iCursorID = cell.getCursorID();
/*  317 */         this.lastCell = cell;
/*      */       }
/*      */       
/*  320 */       if (iCursorID < 0) {
/*  321 */         iCursorID = 0;
/*      */       }
/*      */       
/*  324 */       if (iCursorID != this.lastCursorID) {
/*  325 */         this.lastCursorID = iCursorID;
/*      */         
/*  327 */         if (iCursorID >= 0) {
/*  328 */           this.tv.getComposite().setCursor(this.tv.getComposite().getDisplay().getSystemCursor(iCursorID));
/*      */         } else {
/*  330 */           this.tv.getComposite().setCursor(null);
/*      */         }
/*      */       }
/*      */       
/*  334 */       if (cell != null) {
/*  335 */         TableCellMouseEvent event = createMouseEvent(cell, e, 3, false);
/*      */         
/*  337 */         if (event != null) {
/*  338 */           TableColumnCore tc = (TableColumnCore)cell.getTableColumn();
/*  339 */           if (tc.hasCellMouseMoveListener()) {
/*  340 */             ((TableColumnCore)cell.getTableColumn()).invokeCellMouseListeners(event);
/*      */           }
/*  342 */           cell.invokeMouseListeners(event);
/*      */           
/*      */ 
/*      */ 
/*  346 */           iCursorID = cell.getCursorID();
/*  347 */           if (iCursorID != this.lastCursorID) {
/*  348 */             this.lastCursorID = iCursorID;
/*      */             
/*  350 */             if (iCursorID >= 0) {
/*  351 */               this.tv.getComposite().setCursor(this.tv.getComposite().getDisplay().getSystemCursor(iCursorID));
/*      */             } else {
/*  353 */               this.tv.getComposite().setCursor(null);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Exception ex) {
/*  359 */       Debug.out(ex);
/*      */     }
/*      */   }
/*      */   
/*      */   public void widgetSelected(SelectionEvent e) {}
/*      */   
/*      */   public void widgetDefaultSelected(SelectionEvent e)
/*      */   {
/*  367 */     if ((this.lCancelSelectionTriggeredOn > 0L) && (System.currentTimeMillis() - this.lCancelSelectionTriggeredOn < 200L))
/*      */     {
/*  369 */       e.doit = false;
/*      */     } else {
/*  371 */       runDefaultAction(e.stateMask);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void keyPressed(KeyEvent event)
/*      */   {
/*  379 */     TableViewSWTFilter<?> filter = this.tv.getSWTFilter();
/*  380 */     if ((event.widget != null) && (filter != null) && (event.widget == filter.widget) && (
/*  381 */       (event.character == '') || (event.character == '\b'))) {
/*  382 */       handleSearchKeyPress(event);
/*  383 */       return;
/*      */     }
/*      */     
/*      */ 
/*  387 */     KeyListener[] listeners = this.tv.getKeyListeners();
/*  388 */     for (KeyListener l : listeners) {
/*  389 */       l.keyPressed(event);
/*  390 */       if (!event.doit) {
/*  391 */         this.lCancelSelectionTriggeredOn = SystemTime.getCurrentTime();
/*  392 */         return;
/*      */       }
/*      */     }
/*      */     
/*  396 */     if (event.keyCode == 16777230) {
/*  397 */       if ((event.stateMask & 0x20000) != 0) {
/*  398 */         this.tv.runForSelectedRows(new com.aelitis.azureus.ui.common.table.TableGroupRowRunner() {
/*      */           public void run(TableRowCore row) {
/*  400 */             row.invalidate();
/*  401 */             row.refresh(true);
/*      */           }
/*      */         });
/*  404 */       } else if ((event.stateMask & 0x40000) != 0) {
/*  405 */         this.tv.runForAllRows(new com.aelitis.azureus.ui.common.table.TableGroupRowRunner() {
/*      */           public void run(TableRowCore row) {
/*  407 */             row.invalidate();
/*  408 */             row.refresh(true);
/*      */           }
/*      */         });
/*      */       } else {
/*  412 */         this.tv.sortColumn(true);
/*      */       }
/*  414 */       event.doit = false;
/*  415 */       return;
/*      */     }
/*      */     
/*  418 */     int key = event.character;
/*  419 */     if ((key <= 26) && (key > 0)) {
/*  420 */       key += 96;
/*      */     }
/*      */     
/*  423 */     if (event.stateMask == org.eclipse.swt.SWT.MOD1) {
/*  424 */       switch (key) {
/*      */       case 97: 
/*  426 */         if ((filter == null) || (event.widget != filter.widget)) {
/*  427 */           if (!this.tv.isSingleSelection()) {
/*  428 */             this.tv.selectAll();
/*  429 */             event.doit = false;
/*      */           }
/*      */         } else {
/*  432 */           filter.widget.selectAll();
/*  433 */           event.doit = false;
/*      */         }
/*      */         
/*  436 */         break;
/*      */       
/*      */       case 43: 
/*  439 */         if (org.gudy.azureus2.core3.util.Constants.isUnix) {
/*  440 */           this.tv.expandColumns();
/*  441 */           event.doit = false;
/*      */         }
/*      */         
/*      */         break;
/*      */       case 102: 
/*  446 */         this.tv.openFilterDialog();
/*  447 */         event.doit = false;
/*  448 */         break;
/*      */       case 120: 
/*  450 */         if ((filter != null) && (event.widget == filter.widget)) {
/*  451 */           filter.regex = (!filter.regex);
/*  452 */           filter.widget.setBackground(filter.regex ? COLOR_FILTER_REGEX : null);
/*  453 */           validateFilterRegex();
/*  454 */           this.tv.refilter(); return;
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       case 103: 
/*  460 */         System.out.println("force sort");
/*  461 */         this.tv.resetLastSortedOn();
/*  462 */         this.tv.sortColumn(true);
/*      */       }
/*      */       
/*      */     }
/*      */     
/*      */ 
/*  468 */     if ((event.stateMask == 0) && 
/*  469 */       (filter != null) && (filter.widget == event.widget)) {
/*  470 */       if (event.keyCode == 16777218) {
/*  471 */         this.tv.setFocus();
/*  472 */         event.doit = false;
/*  473 */       } else if (event.character == '\r') {
/*  474 */         this.tv.refilter();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  479 */     if (!event.doit) {
/*  480 */       return;
/*      */     }
/*      */     
/*  483 */     handleSearchKeyPress(event);
/*      */   }
/*      */   
/*      */   private void handleSearchKeyPress(KeyEvent e) {
/*  487 */     TableViewSWTFilter<?> filter = this.tv.getSWTFilter();
/*  488 */     if ((filter == null) || (e.widget == filter.widget)) {
/*  489 */       return;
/*      */     }
/*      */     
/*  492 */     String newText = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  500 */     if (e.keyCode == 8) {
/*  501 */       if (e.stateMask == 262144) {
/*  502 */         newText = "";
/*  503 */       } else if (filter.nextText.length() > 0) {
/*  504 */         newText = filter.nextText.substring(0, filter.nextText.length() - 1);
/*      */       }
/*  506 */     } else if (((e.stateMask & 0xFFFDFFFF) == 0) && (e.character > ' ') && (e.character != '')) {
/*  507 */       newText = filter.nextText + String.valueOf(e.character);
/*      */     }
/*      */     
/*  510 */     if (newText == null) {
/*  511 */       return;
/*      */     }
/*      */     
/*      */ 
/*  515 */     if ((filter != null) && (filter.widget != null) && (!filter.widget.isDisposed())) {
/*  516 */       filter.widget.setFocus();
/*      */     }
/*  518 */     this.tv.setFilterText(newText);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  558 */     e.doit = false;
/*      */   }
/*      */   
/*      */ 
/*      */   private void validateFilterRegex()
/*      */   {
/*  564 */     TableViewSWTFilter<?> filter = this.tv.getSWTFilter();
/*  565 */     if (filter.regex) {
/*  566 */       if (FONT_NO_REGEX == null) {
/*  567 */         FONT_NO_REGEX = filter.widget.getFont();
/*  568 */         FontData[] fd = FONT_NO_REGEX.getFontData();
/*  569 */         for (int i = 0; i < fd.length; i++) {
/*  570 */           fd[i].setStyle(1);
/*      */         }
/*  572 */         FONT_REGEX = new Font(filter.widget.getDisplay(), fd);
/*  573 */         for (int i = 0; i < fd.length; i++) {
/*  574 */           fd[i].setStyle(2);
/*      */         }
/*  576 */         FONT_REGEX_ERROR = new Font(filter.widget.getDisplay(), fd);
/*      */       }
/*      */       try {
/*  579 */         java.util.regex.Pattern.compile(filter.nextText, 2);
/*  580 */         filter.widget.setBackground(COLOR_FILTER_REGEX);
/*  581 */         filter.widget.setFont(FONT_REGEX);
/*      */         
/*  583 */         Messages.setLanguageTooltip(filter.widget, "MyTorrentsView.filter.tooltip");
/*      */       }
/*      */       catch (Exception e) {
/*  586 */         filter.widget.setBackground(org.gudy.azureus2.ui.swt.mainwindow.Colors.colorErrorBG);
/*  587 */         filter.widget.setToolTipText(e.getMessage());
/*  588 */         filter.widget.setFont(FONT_REGEX_ERROR);
/*      */       }
/*      */     } else {
/*  591 */       filter.widget.setBackground(null);
/*  592 */       Messages.setLanguageTooltip(filter.widget, "MyTorrentsView.filter.tooltip");
/*      */       
/*  594 */       if (FONT_NO_REGEX != null) {
/*  595 */         filter.widget.setFont(FONT_NO_REGEX);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setFilterText(String s) {
/*  601 */     TableViewSWTFilter<?> filter = this.tv.getSWTFilter();
/*  602 */     if (filter == null) {
/*  603 */       return;
/*      */     }
/*  605 */     filter.nextText = s;
/*  606 */     if ((filter != null) && (filter.widget != null) && (!filter.widget.isDisposed())) {
/*  607 */       if (!filter.nextText.equals(filter.widget.getText())) {
/*  608 */         filter.widget.setText(filter.nextText);
/*  609 */         filter.widget.setSelection(filter.nextText.length());
/*      */       }
/*      */       
/*  612 */       validateFilterRegex();
/*      */     }
/*      */     
/*  615 */     if (filter.eventUpdate != null) {
/*  616 */       filter.eventUpdate.cancel();
/*      */     }
/*  618 */     filter.eventUpdate = org.gudy.azureus2.core3.util.SimpleTimer.addEvent("SearchUpdate", SystemTime.getOffsetTime(300L), new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */     {
/*      */       public void perform(TimerEvent event)
/*      */       {
/*  622 */         TableViewSWTFilter<?> filter = TableViewSWT_Common.this.tv.getSWTFilter();
/*      */         
/*  624 */         if (filter == null) {
/*  625 */           return;
/*      */         }
/*  627 */         if ((filter.eventUpdate == null) || (filter.eventUpdate.isCancelled())) {
/*  628 */           filter.eventUpdate = null;
/*  629 */           return;
/*      */         }
/*  631 */         filter.eventUpdate = null;
/*  632 */         if ((filter.nextText != null) && (!filter.nextText.equals(filter.text))) {
/*  633 */           filter.text = filter.nextText;
/*  634 */           filter.checker.filterSet(filter.text);
/*  635 */           TableViewSWT_Common.this.tv.refilter();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void runDefaultAction(int stateMask)
/*      */   {
/*  643 */     if ((this.lastSelectionTriggeredOn > 0L) && (System.currentTimeMillis() - this.lastSelectionTriggeredOn < 200L))
/*      */     {
/*  645 */       return;
/*      */     }
/*      */     
/*      */ 
/*  649 */     if (System.currentTimeMillis() - this.lCancelSelectionTriggeredOn > 200L) {
/*  650 */       this.lastSelectionTriggeredOn = System.currentTimeMillis();
/*  651 */       TableRowCore[] selectedRows = this.tv.getSelectedRows();
/*  652 */       this.tv.triggerDefaultSelectedListeners(selectedRows, stateMask);
/*      */     }
/*      */   }
/*      */   
/*      */   public void keyReleased(KeyEvent event) {
/*  657 */     KeyListener[] listeners = this.tv.getKeyListeners();
/*  658 */     for (KeyListener l : listeners) {
/*  659 */       l.keyReleased(event);
/*  660 */       if (!event.doit) {
/*  661 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addKeyListener(KeyListener listener)
/*      */   {
/*  670 */     if (this.listenersKey.contains(listener)) {
/*  671 */       return;
/*      */     }
/*      */     
/*  674 */     this.listenersKey.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeKeyListener(KeyListener listener)
/*      */   {
/*  681 */     this.listenersKey.remove(listener);
/*      */   }
/*      */   
/*      */   public KeyListener[] getKeyListeners() {
/*  685 */     return (KeyListener[])this.listenersKey.toArray(new KeyListener[0]);
/*      */   }
/*      */   
/*      */   public void addRowMouseListener(TableRowMouseListener listener) {
/*      */     try {
/*  690 */       mon_RowMouseListener.enter();
/*      */       
/*  692 */       if (this.rowMouseListeners == null) {
/*  693 */         this.rowMouseListeners = new ArrayList(1);
/*      */       }
/*  695 */       this.rowMouseListeners.add(listener);
/*      */     }
/*      */     finally {
/*  698 */       mon_RowMouseListener.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeRowMouseListener(TableRowMouseListener listener) {
/*      */     try {
/*  704 */       mon_RowMouseListener.enter();
/*      */       
/*  706 */       if (this.rowMouseListeners == null) {
/*      */         return;
/*      */       }
/*  709 */       this.rowMouseListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  712 */       mon_RowMouseListener.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void invokeRowMouseListener(TableRowMouseEvent event)
/*      */   {
/*  720 */     if (this.rowMouseListeners == null) {
/*  721 */       return;
/*      */     }
/*  723 */     ArrayList<TableRowMouseListener> listeners = new ArrayList(this.rowMouseListeners);
/*      */     
/*      */ 
/*  726 */     for (int i = 0; i < listeners.size(); i++) {
/*      */       try {
/*  728 */         TableRowMouseListener l = (TableRowMouseListener)listeners.get(i);
/*      */         
/*  730 */         l.rowMouseTrigger(event);
/*      */       }
/*      */       catch (Throwable e) {
/*  733 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addRowPaintListener(TableRowSWTPaintListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  743 */       mon_RowPaintListener.enter();
/*      */       
/*  745 */       if (this.rowPaintListeners == null) {
/*  746 */         this.rowPaintListeners = new ArrayList(1);
/*      */       }
/*  748 */       this.rowPaintListeners.add(listener);
/*      */     }
/*      */     finally {
/*  751 */       mon_RowPaintListener.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeRowPaintListener(TableRowSWTPaintListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  760 */       mon_RowPaintListener.enter();
/*      */       
/*  762 */       if (this.rowPaintListeners == null) {
/*      */         return;
/*      */       }
/*  765 */       this.rowPaintListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  768 */       mon_RowPaintListener.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void invokePaintListeners(GC gc, TableRowCore row, TableColumnCore column, Rectangle cellArea)
/*      */   {
/*  775 */     if (this.rowPaintListeners == null) {
/*  776 */       return;
/*      */     }
/*  778 */     ArrayList<TableRowSWTPaintListener> listeners = new ArrayList(this.rowPaintListeners);
/*      */     
/*      */ 
/*  781 */     for (int i = 0; i < listeners.size(); i++) {
/*      */       try {
/*  783 */         TableRowSWTPaintListener l = (TableRowSWTPaintListener)listeners.get(i);
/*      */         
/*  785 */         l.rowPaint(gc, row, column, cellArea);
/*      */       }
/*      */       catch (Throwable e) {
/*  788 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void fillMenu(Menu menu, final TableColumnCore column)
/*      */   {
/*  801 */     String columnName = column == null ? null : column.getName();
/*      */     
/*  803 */     Object[] listeners = this.listenersMenuFill.toArray();
/*  804 */     for (int i = 0; i < listeners.length; i++) {
/*  805 */       TableViewSWTMenuFillListener l = (TableViewSWTMenuFillListener)listeners[i];
/*  806 */       l.fillMenu(columnName, menu);
/*      */     }
/*      */     
/*  809 */     boolean hasLevel1 = false;
/*  810 */     boolean hasLevel2 = false;
/*      */     
/*  812 */     TableRowCore[] selectedRows = this.tv.getSelectedRows();
/*  813 */     for (TableRowCore row : selectedRows) {
/*  814 */       if (row.getParentRowCore() != null) {
/*  815 */         hasLevel2 = true;
/*      */       } else {
/*  817 */         hasLevel1 = true;
/*      */       }
/*      */     }
/*      */     
/*  821 */     String tableID = this.tv.getTableID();
/*  822 */     String sMenuID = hasLevel1 ? tableID : "Files";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  827 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = null;
/*  828 */     boolean isDownloadContext; boolean isDownloadContext; if ((org.gudy.azureus2.plugins.download.Download.class.isAssignableFrom(this.tv.getDataSourceType())) && (!hasLevel2)) {
/*  829 */       menu_items = MenuItemManager.getInstance().getAllAsArray("download_context");
/*      */       
/*  831 */       isDownloadContext = true;
/*      */     } else {
/*  833 */       menu_items = MenuItemManager.getInstance().getAllAsArray((String)null);
/*  834 */       isDownloadContext = false;
/*      */     }
/*      */     
/*  837 */     if (columnName == null) {
/*  838 */       org.eclipse.swt.widgets.MenuItem itemChangeTable = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  839 */       Messages.setLanguageText(itemChangeTable, "MyTorrentsView.menu.editTableColumns");
/*      */       
/*  841 */       org.gudy.azureus2.ui.swt.Utils.setMenuItemImage(itemChangeTable, "columns");
/*      */       
/*  843 */       itemChangeTable.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  845 */           TableViewSWT_Common.this.showColumnEditor();
/*      */         }
/*      */       });
/*      */     }
/*      */     else
/*      */     {
/*  851 */       org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  852 */       Messages.setLanguageText(item, "MyTorrentsView.menu.thisColumn.toClipboard");
/*  853 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  855 */           String sToClipboard = "";
/*  856 */           if (column == null) {
/*  857 */             return;
/*      */           }
/*  859 */           String columnName = column.getName();
/*  860 */           if (columnName == null) {
/*  861 */             return;
/*      */           }
/*  863 */           TableRowCore[] rows = TableViewSWT_Common.this.tv.getSelectedRows();
/*  864 */           for (TableRowCore row : rows) {
/*  865 */             if (row != rows[0]) {
/*  866 */               sToClipboard = sToClipboard + "\n";
/*      */             }
/*  868 */             TableCellCore cell = row.getTableCellCore(columnName);
/*  869 */             if (cell != null) {
/*  870 */               sToClipboard = sToClipboard + cell.getClipboardText();
/*      */             }
/*      */           }
/*  873 */           if (sToClipboard.length() == 0) {
/*  874 */             return;
/*      */           }
/*  876 */           new Clipboard(Display.getDefault()).setContents(new Object[] { sToClipboard }, new org.eclipse.swt.dnd.Transfer[] { org.eclipse.swt.dnd.TextTransfer.getInstance() });
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  887 */     boolean enable_items = selectedRows.length > 0;
/*      */     
/*  889 */     TableContextMenuItem[] items = TableContextMenuManager.getInstance().getAllAsArray(sMenuID);
/*      */     
/*      */ 
/*  892 */     if ((items.length > 0) || (menu_items.length > 0)) {
/*  893 */       new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */       
/*      */ 
/*  896 */       if (menu_items != null)
/*      */       {
/*      */         Object[] target;
/*  899 */         if (isDownloadContext) {
/*  900 */           Object[] dataSources = this.tv.getSelectedDataSources(false);
/*  901 */           Object[] target = new org.gudy.azureus2.plugins.download.Download[dataSources.length];
/*  902 */           System.arraycopy(dataSources, 0, target, 0, target.length);
/*      */         } else {
/*  904 */           target = selectedRows;
/*      */         }
/*  906 */         MenuBuildUtils.addPluginMenuItems(menu_items, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(target));
/*      */       }
/*      */       
/*      */ 
/*  910 */       if (items.length > 0) {
/*  911 */         MenuBuildUtils.addPluginMenuItems(items, menu, true, enable_items, new org.gudy.azureus2.ui.swt.MenuBuildUtils.PluginMenuController()
/*      */         {
/*      */           public Listener makeSelectionListener(final org.gudy.azureus2.plugins.ui.menus.MenuItem plugin_menu_item)
/*      */           {
/*  915 */             new TableSelectedRowsListener(TableViewSWT_Common.this.tv, false) {
/*      */               public boolean run(TableRowCore[] rows) {
/*  917 */                 if (rows.length != 0) {
/*  918 */                   ((MenuItemImpl)plugin_menu_item).invokeListenersMulti(rows);
/*      */                 }
/*  920 */                 return true;
/*      */               }
/*      */             };
/*      */           }
/*      */           
/*      */           public void notifyFillListeners(org.gudy.azureus2.plugins.ui.menus.MenuItem menu_item)
/*      */           {
/*  927 */             ((MenuItemImpl)menu_item).invokeMenuWillBeShownListeners(TableViewSWT_Common.this.tv.getSelectedRows());
/*      */           }
/*      */           
/*      */ 
/*      */           public void buildSubmenu(org.gudy.azureus2.plugins.ui.menus.MenuItem parent)
/*      */           {
/*  933 */             MenuBuilder submenuBuilder = ((MenuItemImpl)parent).getSubmenuBuilder();
/*  934 */             if (submenuBuilder != null) {
/*      */               try {
/*  936 */                 parent.removeAllChildItems();
/*  937 */                 submenuBuilder.buildSubmenu(parent, TableViewSWT_Common.this.tv.getSelectedRows());
/*      */               } catch (Throwable t) {
/*  939 */                 Debug.out(t);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*  947 */     if (hasLevel1)
/*      */     {
/*  949 */       if (column != null) {
/*  950 */         TableContextMenuItem[] columnItems = column.getContextMenuItems(2);
/*  951 */         if (columnItems.length > 0) {
/*  952 */           new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */           
/*  954 */           MenuBuildUtils.addPluginMenuItems(columnItems, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(this.tv.getSelectedDataSources(true)));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  965 */       if (this.tv.getSWTFilter() != null) {
/*  966 */         org.eclipse.swt.widgets.MenuItem itemFilter = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  967 */         Messages.setLanguageText(itemFilter, "MyTorrentsView.menu.filter");
/*  968 */         itemFilter.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/*  970 */             TableViewSWT_Common.this.tv.openFilterDialog();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void showColumnEditor() {
/*  978 */     TableRowCore focusedRow = this.tv.getFocusedRow();
/*  979 */     if ((focusedRow == null) || (focusedRow.isRowDisposed())) {
/*  980 */       focusedRow = this.tv.getRow(0);
/*      */     }
/*  982 */     String tableID = this.tv.getTableID();
/*  983 */     new TableColumnSetupWindow(this.tv.getDataSourceType(), tableID, focusedRow, TableStructureEventDispatcher.getInstance(tableID)).open();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void fillColumnMenu(final Menu menu, final TableColumnCore column, boolean isBlankArea)
/*      */   {
/*  995 */     String tableID = this.tv.getTableID();
/*  996 */     int hiddenColumnCount = 0;
/*      */     
/*  998 */     if (!isBlankArea) {
/*  999 */       TableColumnManager tcm = TableColumnManager.getInstance();
/* 1000 */       TableColumnCore[] allTableColumns = tcm.getAllTableColumnCoreAsArray(this.tv.getDataSourceType(), tableID);
/*      */       
/*      */ 
/* 1003 */       java.util.Arrays.sort(allTableColumns, TableColumnManager.getTableColumnOrderComparator());
/*      */       
/*      */ 
/* 1006 */       for (final TableColumnCore tc : allTableColumns) {
/* 1007 */         boolean visible = tc.isVisible();
/* 1008 */         if (!visible) {
/* 1009 */           org.gudy.azureus2.plugins.ui.tables.TableColumnInfo columnInfo = tcm.getColumnInfo(this.tv.getDataSourceType(), tableID, tc.getName());
/*      */           
/* 1011 */           if (columnInfo.getProficiency() != 0) {
/* 1012 */             hiddenColumnCount++;
/* 1013 */             continue;
/*      */           }
/*      */         }
/* 1016 */         org.eclipse.swt.widgets.MenuItem menuItem = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/* 1017 */         Messages.setLanguageText(menuItem, tc.getTitleLanguageKey());
/* 1018 */         if (visible) {
/* 1019 */           menuItem.setSelection(true);
/*      */         }
/* 1021 */         menuItem.addListener(13, new Listener() {
/*      */           public void handleEvent(Event e) {
/* 1023 */             tc.setVisible(!tc.isVisible());
/* 1024 */             TableColumnManager tcm = TableColumnManager.getInstance();
/* 1025 */             String tableID = TableViewSWT_Common.this.tv.getTableID();
/* 1026 */             tcm.saveTableColumns(TableViewSWT_Common.this.tv.getDataSourceType(), tableID);
/* 1027 */             if ((TableViewSWT_Common.this.tv instanceof TableStructureModificationListener)) {
/* 1028 */               ((TableStructureModificationListener)TableViewSWT_Common.this.tv).tableStructureChanged(true, null);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1036 */     if (hiddenColumnCount > 0)
/*      */     {
/* 1038 */       org.eclipse.swt.widgets.MenuItem itemMoreHidden = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1039 */       Messages.setLanguageText(itemMoreHidden, "MyTorrentsView.menu.moreColHidden", new String[] { String.valueOf(hiddenColumnCount) });
/*      */       
/*      */ 
/* 1042 */       itemMoreHidden.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/* 1044 */           TableViewSWT_Common.this.showColumnEditor();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1049 */     if (menu.getItemCount() > 0) {
/* 1050 */       new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */     }
/*      */     
/* 1053 */     if (column != null) {
/* 1054 */       org.eclipse.swt.widgets.MenuItem renameColumn = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1055 */       Messages.setLanguageText(renameColumn, "MyTorrentsView.menu.renameColumn");
/*      */       
/*      */ 
/* 1058 */       renameColumn.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/* 1060 */           SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("ColumnRenameWindow.title", "ColumnRenameWindow.message");
/*      */           
/*      */ 
/* 1063 */           String existing_name = column.getNameOverride();
/* 1064 */           if (existing_name == null) {
/* 1065 */             existing_name = "";
/*      */           }
/* 1067 */           entryWindow.setPreenteredText(existing_name, false);
/* 1068 */           entryWindow.selectPreenteredText(true);
/*      */           
/* 1070 */           entryWindow.prompt();
/*      */           
/* 1072 */           if (entryWindow.hasSubmittedInput())
/*      */           {
/* 1074 */             String name = entryWindow.getSubmittedInput().trim();
/*      */             
/* 1076 */             if (name.length() == 0) {
/* 1077 */               name = null;
/*      */             }
/* 1079 */             column.setNameOverride(name);
/* 1080 */             TableColumnManager tcm = TableColumnManager.getInstance();
/* 1081 */             String tableID = TableViewSWT_Common.this.tv.getTableID();
/* 1082 */             tcm.saveTableColumns(TableViewSWT_Common.this.tv.getDataSourceType(), tableID);
/* 1083 */             TableStructureEventDispatcher.getInstance(tableID).tableStructureChanged(true, null);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/* 1088 */     org.eclipse.swt.widgets.MenuItem itemResetColumns = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1089 */     Messages.setLanguageText(itemResetColumns, "table.columns.reset");
/* 1090 */     itemResetColumns.addListener(13, new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1092 */         String tableID = TableViewSWT_Common.this.tv.getTableID();
/* 1093 */         TableColumnManager tcm = TableColumnManager.getInstance();
/* 1094 */         tcm.resetColumns(TableViewSWT_Common.this.tv.getDataSourceType(), tableID);
/*      */       }
/*      */       
/* 1097 */     });
/* 1098 */     org.eclipse.swt.widgets.MenuItem itemChangeTable = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1099 */     Messages.setLanguageText(itemChangeTable, "MyTorrentsView.menu.editTableColumns");
/*      */     
/* 1101 */     org.gudy.azureus2.ui.swt.Utils.setMenuItemImage(itemChangeTable, "columns");
/*      */     
/* 1103 */     itemChangeTable.addListener(13, new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1105 */         TableViewSWT_Common.this.showColumnEditor();
/*      */       }
/*      */       
/* 1108 */     });
/* 1109 */     menu.setData("column", column);
/*      */     
/* 1111 */     if (column == null) {
/* 1112 */       return;
/*      */     }
/*      */     
/* 1115 */     String sColumnName = column.getName();
/* 1116 */     if (sColumnName != null) {
/* 1117 */       Object[] listeners = this.listenersMenuFill.toArray();
/* 1118 */       for (int i = 0; i < listeners.length; i++) {
/* 1119 */         TableViewSWTMenuFillListener l = (TableViewSWTMenuFillListener)listeners[i];
/* 1120 */         l.addThisColumnSubMenu(sColumnName, menu);
/*      */       }
/*      */     }
/*      */     
/* 1124 */     final org.eclipse.swt.widgets.MenuItem at_item = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/* 1125 */     Messages.setLanguageText(at_item, "MyTorrentsView.menu.thisColumn.autoTooltip");
/*      */     
/* 1127 */     at_item.addListener(13, new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1129 */         TableColumnCore tcc = (TableColumnCore)menu.getData("column");
/* 1130 */         tcc.setAutoTooltip(at_item.getSelection());
/* 1131 */         tcc.invalidateCells();
/*      */       }
/* 1133 */     });
/* 1134 */     at_item.setSelection(column.doesAutoTooltip());
/*      */     
/*      */ 
/*      */ 
/* 1138 */     TableContextMenuItem[] items = column.getContextMenuItems(1);
/* 1139 */     if (items.length > 0) {
/* 1140 */       new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */       
/* 1142 */       MenuBuildUtils.addPluginMenuItems(items, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(this.tv.getSelectedDataSources(true)));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addMenuFillListener(TableViewSWTMenuFillListener l)
/*      */   {
/* 1150 */     this.listenersMenuFill.add(l);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/TableViewSWT_Common.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */