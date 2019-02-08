/*      */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*      */ 
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.MenuAdapter;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.MouseListener;
/*      */ import org.eclipse.swt.events.MouseMoveListener;
/*      */ import org.eclipse.swt.events.MouseTrackListener;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPiece;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.ui.Graphic;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellClipboardListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseMoveListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellVisibilityListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseListener;
/*      */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.peers.PeerManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.tracker.TrackerTorrentImpl;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
/*      */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
/*      */ 
/*      */ public class FakeTableCell implements org.gudy.azureus2.ui.swt.views.table.TableCellSWT, PaintListener, MouseListener, MouseMoveListener, MouseTrackListener
/*      */ {
/*   71 */   private AEMonitor this_mon = new AEMonitor("FakeTableCell");
/*      */   
/*      */   private ArrayList refreshListeners;
/*      */   
/*      */   private ArrayList disposeListeners;
/*      */   
/*      */   private ArrayList tooltipListeners;
/*      */   
/*      */   private ArrayList cellMouseListeners;
/*      */   
/*      */   private ArrayList cellMouseMoveListeners;
/*      */   
/*      */   private ArrayList cellVisibilityListeners;
/*      */   
/*      */   private ArrayList<TableCellClipboardListener> cellClipboardListeners;
/*      */   
/*      */   private Image image;
/*      */   
/*      */   private Rectangle imageBounds;
/*      */   
/*      */   private int marginHeight;
/*      */   
/*      */   private int orientation;
/*      */   
/*      */   private int marginWidth;
/*      */   
/*      */   private Comparable sortValue;
/*      */   
/*      */   private Object coreDataSource;
/*      */   
/*      */   private Composite composite;
/*      */   
/*      */   private final TableColumnCore tableColumn;
/*      */   
/*      */   private Graphic graphic;
/*      */   
/*      */   private String text;
/*      */   
/*      */   private Object pluginDataSource;
/*      */   
/*      */   private Object tooltip;
/*      */   
/*      */   private Object default_tooltip;
/*      */   
/*      */   private Rectangle cellArea;
/*      */   
/*      */   private boolean hadMore;
/*  118 */   private boolean wrapText = true;
/*      */   
/*      */   private ArrayList cellSWTPaintListeners;
/*      */   
/*      */   private boolean valid;
/*      */   
/*  124 */   private TableRow fakeRow = null;
/*      */   
/*      */ 
/*      */ 
/*      */   public FakeTableCell(TableColumn column, Object ds)
/*      */   {
/*  130 */     this.valid = false;
/*  131 */     this.coreDataSource = ds;
/*  132 */     this.tableColumn = ((TableColumnCore)column);
/*  133 */     setOrientationViaColumn();
/*  134 */     this.tableColumn.invokeCellAddedListeners(this);
/*      */   }
/*      */   
/*      */   public FakeTableCell(TableColumnCore column, Object ds) {
/*  138 */     this.valid = false;
/*  139 */     this.coreDataSource = ds;
/*  140 */     this.tableColumn = column;
/*  141 */     setOrientationViaColumn();
/*  142 */     this.tableColumn.invokeCellAddedListeners(this);
/*      */   }
/*      */   
/*      */   public void addRefreshListener(TableCellRefreshListener listener) {
/*      */     try {
/*  147 */       this.this_mon.enter();
/*      */       
/*  149 */       if (this.refreshListeners == null) {
/*  150 */         this.refreshListeners = new ArrayList(1);
/*      */       }
/*  152 */       this.refreshListeners.add(listener);
/*      */     }
/*      */     finally {
/*  155 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeRefreshListener(TableCellRefreshListener listener) {
/*      */     try {
/*  161 */       this.this_mon.enter();
/*      */       
/*  163 */       if (this.refreshListeners == null) {
/*      */         return;
/*      */       }
/*  166 */       this.refreshListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  169 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addDisposeListener(TableCellDisposeListener listener) {
/*      */     try {
/*  175 */       this.this_mon.enter();
/*      */       
/*  177 */       if (this.disposeListeners == null) {
/*  178 */         this.disposeListeners = new ArrayList(1);
/*      */       }
/*  180 */       this.disposeListeners.add(listener);
/*      */     }
/*      */     finally {
/*  183 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeDisposeListener(TableCellDisposeListener listener) {
/*      */     try {
/*  189 */       this.this_mon.enter();
/*      */       
/*  191 */       if (this.disposeListeners == null) {
/*      */         return;
/*      */       }
/*  194 */       this.disposeListeners.remove(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  198 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addToolTipListener(TableCellToolTipListener listener) {
/*      */     try {
/*  204 */       this.this_mon.enter();
/*      */       
/*  206 */       if (this.tooltipListeners == null) {
/*  207 */         this.tooltipListeners = new ArrayList(1);
/*      */       }
/*  209 */       this.tooltipListeners.add(listener);
/*      */     }
/*      */     finally {
/*  212 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeToolTipListener(TableCellToolTipListener listener) {
/*      */     try {
/*  218 */       this.this_mon.enter();
/*      */       
/*  220 */       if (this.tooltipListeners == null) {
/*      */         return;
/*      */       }
/*  223 */       this.tooltipListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  226 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addMouseListener(TableCellMouseListener listener) {
/*      */     try {
/*  232 */       this.this_mon.enter();
/*      */       
/*  234 */       if (this.cellMouseListeners == null) {
/*  235 */         this.cellMouseListeners = new ArrayList(1);
/*      */       }
/*  237 */       this.cellMouseListeners.add(listener);
/*      */     }
/*      */     finally {
/*  240 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeMouseListener(TableCellMouseListener listener) {
/*      */     try {
/*  246 */       this.this_mon.enter();
/*      */       
/*  248 */       if (this.cellMouseListeners == null) {
/*      */         return;
/*      */       }
/*  251 */       this.cellMouseListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  254 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addMouseMoveListener(TableCellMouseMoveListener listener) {
/*      */     try {
/*  260 */       this.this_mon.enter();
/*      */       
/*  262 */       if (this.cellMouseMoveListeners == null) {
/*  263 */         this.cellMouseMoveListeners = new ArrayList(1);
/*      */       }
/*  265 */       this.cellMouseMoveListeners.add(listener);
/*      */     }
/*      */     finally {
/*  268 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeMouseMoveListener(TableCellMouseMoveListener listener) {
/*      */     try {
/*  274 */       this.this_mon.enter();
/*      */       
/*  276 */       if (this.cellMouseMoveListeners == null) {
/*      */         return;
/*      */       }
/*  279 */       this.cellMouseMoveListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  282 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addVisibilityListener(TableCellVisibilityListener listener) {
/*      */     try {
/*  288 */       this.this_mon.enter();
/*      */       
/*  290 */       if (this.cellVisibilityListeners == null) {
/*  291 */         this.cellVisibilityListeners = new ArrayList(1);
/*      */       }
/*  293 */       this.cellVisibilityListeners.add(listener);
/*      */     }
/*      */     finally {
/*  296 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeVisibilityListener(TableCellVisibilityListener listener) {
/*      */     try {
/*  302 */       this.this_mon.enter();
/*      */       
/*  304 */       if (this.cellVisibilityListeners == null) {
/*      */         return;
/*      */       }
/*  307 */       this.cellVisibilityListeners.remove(listener);
/*      */     }
/*      */     finally {
/*  310 */       this.this_mon.exit();
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
/*  321 */       this.this_mon.enter();
/*      */       
/*  323 */       if (this.cellSWTPaintListeners == null) {
/*  324 */         this.cellSWTPaintListeners = new ArrayList(1);
/*      */       }
/*  326 */       this.cellSWTPaintListeners.add(listener);
/*      */     }
/*      */     finally {
/*  329 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeSWTPaintListeners(GC gc) {
/*  334 */     if (getBounds().isEmpty()) {
/*  335 */       return;
/*      */     }
/*  337 */     if (this.tableColumn != null) {
/*  338 */       Object[] swtPaintListeners = this.tableColumn.getCellOtherListeners("SWTPaint");
/*  339 */       if (swtPaintListeners != null) {
/*  340 */         for (int i = 0; i < swtPaintListeners.length; i++) {
/*      */           try {
/*  342 */             TableCellSWTPaintListener l = (TableCellSWTPaintListener)swtPaintListeners[i];
/*      */             
/*  344 */             l.cellPaint(gc, this);
/*      */           }
/*      */           catch (Throwable e) {
/*  347 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  353 */     if (this.cellSWTPaintListeners == null) {
/*  354 */       return;
/*      */     }
/*      */     
/*      */ 
/*  358 */     for (int i = 0; i < this.cellSWTPaintListeners.size(); i++) {
/*      */       try {
/*  360 */         TableCellSWTPaintListener l = (TableCellSWTPaintListener)this.cellSWTPaintListeners.get(i);
/*      */         
/*  362 */         l.cellPaint(gc, this);
/*      */       }
/*      */       catch (Throwable e) {
/*  365 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void addCellClipboardListener(TableCellClipboardListener listener) {
/*      */     try {
/*  372 */       this.this_mon.enter();
/*      */       
/*  374 */       if (this.cellClipboardListeners == null) {
/*  375 */         this.cellClipboardListeners = new ArrayList(1);
/*      */       }
/*  377 */       this.cellClipboardListeners.add(listener);
/*      */     }
/*      */     finally {
/*  380 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public String getClipboardText() {
/*  385 */     String text = null;
/*      */     try {
/*  387 */       this.this_mon.enter();
/*      */       
/*  389 */       if (this.cellClipboardListeners != null) {
/*  390 */         for (TableCellClipboardListener l : this.cellClipboardListeners) {
/*      */           try {
/*  392 */             text = l.getClipboardText(this);
/*      */           } catch (Exception e) {
/*  394 */             Debug.out(e);
/*      */           }
/*  396 */           if (text != null) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/*  402 */       this.this_mon.exit();
/*      */     }
/*  404 */     if (text == null) {
/*  405 */       text = getText();
/*      */     }
/*  407 */     return text;
/*      */   }
/*      */   
/*      */   public void addListeners(Object listenerObject)
/*      */   {
/*  412 */     if ((listenerObject instanceof TableCellDisposeListener)) {
/*  413 */       addDisposeListener((TableCellDisposeListener)listenerObject);
/*      */     }
/*  415 */     if ((listenerObject instanceof TableCellRefreshListener)) {
/*  416 */       addRefreshListener((TableCellRefreshListener)listenerObject);
/*      */     }
/*  418 */     if ((listenerObject instanceof TableCellToolTipListener)) {
/*  419 */       addToolTipListener((TableCellToolTipListener)listenerObject);
/*      */     }
/*  421 */     if ((listenerObject instanceof TableCellMouseMoveListener)) {
/*  422 */       addMouseMoveListener((TableCellMouseMoveListener)listenerObject);
/*      */     }
/*      */     
/*  425 */     if ((listenerObject instanceof TableCellMouseListener)) {
/*  426 */       addMouseListener((TableCellMouseListener)listenerObject);
/*      */     }
/*      */     
/*  429 */     if ((listenerObject instanceof TableCellVisibilityListener)) {
/*  430 */       addVisibilityListener((TableCellVisibilityListener)listenerObject);
/*      */     }
/*  432 */     if ((listenerObject instanceof TableCellSWTPaintListener)) {
/*  433 */       addSWTPaintListener((TableCellSWTPaintListener)listenerObject);
/*      */     }
/*      */     
/*  436 */     if ((listenerObject instanceof TableCellClipboardListener)) {
/*  437 */       addCellClipboardListener((TableCellClipboardListener)listenerObject);
/*      */     }
/*      */   }
/*      */   
/*      */   public void invokeMouseListeners(TableCellMouseEvent event) {
/*  442 */     if ((event.cell != null) && (event.row == null)) {
/*  443 */       event.row = event.cell.getTableRow();
/*      */     }
/*      */     try
/*      */     {
/*  447 */       this.tableColumn.invokeCellMouseListeners(event);
/*      */     } catch (Throwable e) {
/*  449 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  452 */     ArrayList listeners = event.eventType == 3 ? this.cellMouseMoveListeners : this.cellMouseListeners;
/*      */     
/*      */ 
/*  455 */     if (listeners == null) {
/*  456 */       return;
/*      */     }
/*      */     
/*  459 */     for (int i = 0; i < listeners.size(); i++) {
/*      */       try {
/*  461 */         TableCellMouseListener l = (TableCellMouseListener)listeners.get(i);
/*      */         
/*  463 */         l.cellMouseTrigger(event);
/*      */       }
/*      */       catch (Throwable e) {
/*  466 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public Object getDataSource()
/*      */   {
/*  473 */     boolean bCoreObject = (this.tableColumn != null) && (this.tableColumn.getUseCoreDataSource());
/*  474 */     if (bCoreObject) {
/*  475 */       return this.coreDataSource;
/*      */     }
/*      */     
/*  478 */     if (this.pluginDataSource != null) {
/*  479 */       return this.pluginDataSource;
/*      */     }
/*      */     
/*  482 */     if ((this.coreDataSource instanceof DownloadManager)) {
/*  483 */       DownloadManager dm = (DownloadManager)this.coreDataSource;
/*  484 */       if (dm != null) {
/*      */         try {
/*  486 */           this.pluginDataSource = DownloadManagerImpl.getDownloadStatic(dm);
/*      */         }
/*      */         catch (DownloadException e) {}
/*      */       }
/*      */     }
/*  491 */     if ((this.coreDataSource instanceof PEPeer)) {
/*  492 */       PEPeer peer = (PEPeer)this.coreDataSource;
/*  493 */       if (peer != null) {
/*  494 */         this.pluginDataSource = PeerManagerImpl.getPeerForPEPeer(peer);
/*      */       }
/*      */     }
/*      */     
/*  498 */     if ((this.coreDataSource instanceof PEPiece))
/*      */     {
/*  500 */       PEPiece piece = (PEPiece)this.coreDataSource;
/*  501 */       if (piece != null) {
/*  502 */         this.pluginDataSource = null;
/*      */       }
/*      */     }
/*      */     
/*  506 */     if ((this.coreDataSource instanceof DiskManagerFileInfo)) {
/*  507 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)this.coreDataSource;
/*  508 */       if (fileInfo != null) {
/*      */         try {
/*  510 */           this.pluginDataSource = new org.gudy.azureus2.pluginsimpl.local.disk.DiskManagerFileInfoImpl(DownloadManagerImpl.getDownloadStatic(fileInfo.getDownloadManager()), fileInfo);
/*      */         }
/*      */         catch (DownloadException e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  518 */     if ((this.coreDataSource instanceof TRHostTorrent)) {
/*  519 */       TRHostTorrent item = (TRHostTorrent)this.coreDataSource;
/*  520 */       if (item != null) {
/*  521 */         this.pluginDataSource = new TrackerTorrentImpl(item);
/*      */       }
/*      */     }
/*      */     
/*  525 */     if (this.pluginDataSource == null)
/*      */     {
/*  527 */       this.pluginDataSource = this.coreDataSource;
/*      */     }
/*      */     
/*  530 */     return this.pluginDataSource;
/*      */   }
/*      */   
/*      */   public int[] getForeground()
/*      */   {
/*  535 */     if ((this.composite == null) || (this.composite.isDisposed())) {
/*  536 */       return null;
/*      */     }
/*  538 */     Color fg = this.composite.getForeground();
/*  539 */     return new int[] { fg.getRed(), fg.getGreen(), fg.getBlue() };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] getBackground()
/*      */   {
/*  550 */     return new int[] { 0, 0, 0 };
/*      */   }
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
/*      */   public Graphic getBackgroundGraphic()
/*      */   {
/*  570 */     if ((this.composite == null) || (this.composite.isDisposed())) {
/*  571 */       return null;
/*      */     }
/*      */     try
/*      */     {
/*  575 */       Rectangle bounds = this.composite.getBounds();
/*      */       
/*  577 */       if (bounds.isEmpty()) {
/*  578 */         return null;
/*      */       }
/*      */       
/*  581 */       Image imgCap = new Image(this.composite.getDisplay(), bounds.width, bounds.height);
/*      */       
/*      */ 
/*      */ 
/*  585 */       Control bgControl = Utils.findBackgroundImageControl(this.composite);
/*  586 */       Image imgBG = this.composite.getBackgroundImage();
/*      */       
/*  588 */       GC gc = new GC(imgCap);
/*      */       try {
/*  590 */         if (imgBG == null) {
/*  591 */           gc.setBackground(this.composite.getBackground());
/*  592 */           gc.fillRectangle(0, 0, bounds.width, bounds.height);
/*      */         }
/*      */         
/*  595 */         if (imgBG != null) {
/*  596 */           Point controlPos = new Point(0, 0);
/*  597 */           if ((bgControl instanceof Composite)) {
/*  598 */             Rectangle compArea = ((Composite)bgControl).getClientArea();
/*  599 */             controlPos.x = compArea.x;
/*  600 */             controlPos.y = compArea.y;
/*      */           }
/*  602 */           Point absControlLoc = bgControl.toDisplay(controlPos.x, controlPos.y);
/*      */           
/*  604 */           Rectangle shellClientArea = this.composite.getShell().getClientArea();
/*  605 */           Point absShellLoc = this.composite.getParent().toDisplay(shellClientArea.x, shellClientArea.y);
/*      */           
/*      */ 
/*  608 */           Point ofs = new Point(absControlLoc.x - absShellLoc.x, absControlLoc.y - absShellLoc.y);
/*      */           
/*  610 */           Rectangle imgBGBounds = imgBG.getBounds();
/*  611 */           ofs.x %= imgBGBounds.width;
/*  612 */           ofs.y %= imgBGBounds.height;
/*      */           
/*  614 */           gc.drawImage(imgBG, ofs.x, ofs.y);
/*      */         }
/*      */       } finally {
/*  617 */         gc.dispose();
/*      */       }
/*      */       
/*  620 */       return new UISWTGraphicImpl(imgCap);
/*      */     } catch (Exception e) {
/*  622 */       Debug.out(e);
/*      */     }
/*  624 */     return null;
/*      */   }
/*      */   
/*      */   public Graphic getGraphic()
/*      */   {
/*  629 */     return this.graphic;
/*      */   }
/*      */   
/*      */   public int getHeight()
/*      */   {
/*  634 */     if ((this.composite != null) && (!this.composite.isDisposed())) {
/*  635 */       if (this.cellArea != null) {
/*  636 */         return this.cellArea.height;
/*      */       }
/*  638 */       return this.composite.getSize().y;
/*      */     }
/*  640 */     return 0;
/*      */   }
/*      */   
/*      */   public int getMaxLines()
/*      */   {
/*  645 */     return -1;
/*      */   }
/*      */   
/*      */   public Comparable getSortValue()
/*      */   {
/*  650 */     if (this.sortValue == null) {
/*  651 */       return "";
/*      */     }
/*  653 */     return this.sortValue;
/*      */   }
/*      */   
/*      */   public TableColumn getTableColumn()
/*      */   {
/*  658 */     return this.tableColumn;
/*      */   }
/*      */   
/*      */   public String getTableID()
/*      */   {
/*  663 */     return this.tableColumn == null ? null : this.tableColumn.getTableID();
/*      */   }
/*      */   
/*      */   public TableRow getTableRow()
/*      */   {
/*  668 */     if (this.fakeRow == null) {
/*  669 */       this.fakeRow = new TableRow() {
/*  670 */         Map<String, Object> data = new LightHashMap(1);
/*      */         
/*      */ 
/*      */         public void setForegroundToErrorColor() {}
/*      */         
/*      */ 
/*      */         public void setForeground(int[] rgb) {}
/*      */         
/*      */ 
/*      */         public void setForeground(int red, int green, int blue) {}
/*      */         
/*      */         public void removeMouseListener(TableRowMouseListener listener) {}
/*      */         
/*      */         public int getIndex()
/*      */         {
/*  685 */           return 1;
/*      */         }
/*      */         
/*      */         public boolean isValid() {
/*  689 */           return FakeTableCell.this.isValid();
/*      */         }
/*      */         
/*      */         public boolean isSelected() {
/*  693 */           return false;
/*      */         }
/*      */         
/*      */         public String getTableID() {
/*  697 */           return FakeTableCell.this.getTableID();
/*      */         }
/*      */         
/*      */ 
/*      */         public TableView<?> getView()
/*      */         {
/*  703 */           return null;
/*      */         }
/*      */         
/*  706 */         public TableCell getTableCell(String columnName) { return null; }
/*      */         
/*      */         public Object getDataSource()
/*      */         {
/*  710 */           return FakeTableCell.this.getDataSource();
/*      */         }
/*      */         
/*      */         public void addMouseListener(TableRowMouseListener listener) {}
/*      */         
/*      */         /* Error */
/*      */         public Object getData(String id)
/*      */         {
/*      */           // Byte code:
/*      */           //   0: aload_0
/*      */           //   1: getfield 82	org/gudy/azureus2/ui/swt/views/table/impl/FakeTableCell$1:data	Ljava/util/Map;
/*      */           //   4: dup
/*      */           //   5: astore_2
/*      */           //   6: monitorenter
/*      */           //   7: aload_0
/*      */           //   8: getfield 82	org/gudy/azureus2/ui/swt/views/table/impl/FakeTableCell$1:data	Ljava/util/Map;
/*      */           //   11: aload_1
/*      */           //   12: invokeinterface 89 2 0
/*      */           //   17: aload_2
/*      */           //   18: monitorexit
/*      */           //   19: areturn
/*      */           //   20: astore_3
/*      */           //   21: aload_2
/*      */           //   22: monitorexit
/*      */           //   23: aload_3
/*      */           //   24: athrow
/*      */           // Line number table:
/*      */           //   Java source line #717	-> byte code offset #0
/*      */           //   Java source line #718	-> byte code offset #7
/*      */           //   Java source line #719	-> byte code offset #20
/*      */           // Local variable table:
/*      */           //   start	length	slot	name	signature
/*      */           //   0	25	0	this	1
/*      */           //   0	25	1	id	String
/*      */           //   5	17	2	Ljava/lang/Object;	Object
/*      */           //   20	4	3	localObject1	Object
/*      */           // Exception table:
/*      */           //   from	to	target	type
/*      */           //   7	19	20	finally
/*      */           //   20	23	20	finally
/*      */         }
/*      */         
/*      */         public void setData(String id, Object val)
/*      */         {
/*  723 */           synchronized (this.data) {
/*  724 */             this.data.put(id, val);
/*      */           }
/*      */         }
/*      */       };
/*      */     }
/*  729 */     return this.fakeRow;
/*      */   }
/*      */   
/*      */   public String getText()
/*      */   {
/*  734 */     return this.text;
/*      */   }
/*      */   
/*      */   public Object getToolTip()
/*      */   {
/*  739 */     if ((this.tooltip == null) && (this.hadMore)) {
/*  740 */       return this.text;
/*      */     }
/*  742 */     return this.tooltip;
/*      */   }
/*      */   
/*      */   public int getWidth()
/*      */   {
/*  747 */     if (!isDisposed()) {
/*  748 */       if (this.cellArea != null) {
/*  749 */         return this.cellArea.width - 2;
/*      */       }
/*  751 */       return this.composite.getSize().x;
/*      */     }
/*  753 */     return 0;
/*      */   }
/*      */   
/*      */   public void invalidate()
/*      */   {
/*  758 */     this.valid = false;
/*      */   }
/*      */   
/*      */   public boolean isDisposed()
/*      */   {
/*  763 */     return (this.composite == null) || (this.composite.isDisposed());
/*      */   }
/*      */   
/*      */   public boolean isShown()
/*      */   {
/*  768 */     return true;
/*      */   }
/*      */   
/*      */   public boolean isValid()
/*      */   {
/*  773 */     return this.valid;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFillCell(boolean fillCell) {}
/*      */   
/*      */ 
/*      */   public void setWrapText(boolean wrap)
/*      */   {
/*  783 */     this.wrapText = wrap;
/*      */   }
/*      */   
/*      */   public boolean setForeground(int red, int green, int blue)
/*      */   {
/*  788 */     if (isDisposed()) {
/*  789 */       return false;
/*      */     }
/*  791 */     if ((red < 0) || (green < 0) || (blue < 0)) {
/*  792 */       this.composite.setForeground(null);
/*      */     } else {
/*  794 */       this.composite.setForeground(ColorCache.getColor(this.composite.getDisplay(), red, green, blue));
/*      */     }
/*      */     
/*  797 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setForeground(int[] rgb)
/*      */   {
/*  802 */     if ((rgb == null) || (rgb.length < 3)) {
/*  803 */       return setForeground(-1, -1, -1);
/*      */     }
/*  805 */     return setForeground(rgb[0], rgb[1], rgb[2]);
/*      */   }
/*      */   
/*      */   public boolean setForegroundToErrorColor()
/*      */   {
/*  810 */     if (isDisposed()) {
/*  811 */       return false;
/*      */     }
/*  813 */     this.composite.setForeground(Colors.colorError);
/*  814 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setGraphic(Graphic img)
/*      */   {
/*  819 */     Image imgSWT = null;
/*  820 */     if ((img instanceof UISWTGraphic)) {
/*  821 */       imgSWT = ((UISWTGraphic)img).getImage();
/*      */     }
/*      */     
/*  824 */     if ((imgSWT != null) && (imgSWT.isDisposed())) {
/*  825 */       return false;
/*      */     }
/*      */     
/*  828 */     if (this.image == imgSWT) {
/*  829 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  834 */     this.image = imgSWT;
/*  835 */     if (this.image != null) {
/*  836 */       this.imageBounds = this.image.getBounds();
/*      */     }
/*      */     
/*  839 */     if ((this.composite != null) && (!this.composite.isDisposed())) {
/*  840 */       redraw();
/*      */     }
/*      */     
/*  843 */     this.graphic = img;
/*  844 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMarginHeight(int height) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMarginWidth(int width) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean setSortValue(Comparable valueToSort)
/*      */   {
/*  861 */     return _setSortValue(valueToSort);
/*      */   }
/*      */   
/*      */   public boolean setSortValue(float valueToSort)
/*      */   {
/*  866 */     return _setSortValue(Float.valueOf(valueToSort));
/*      */   }
/*      */   
/*      */   public boolean setText(String text)
/*      */   {
/*  871 */     if ((text != null) && (text.equals(this.text))) {
/*  872 */       return false;
/*      */     }
/*  874 */     this.text = text;
/*  875 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  877 */         if (!FakeTableCell.this.isDisposed()) {
/*  878 */           FakeTableCell.this.composite.redraw();
/*      */         }
/*      */       }
/*  881 */     });
/*  882 */     return true;
/*      */   }
/*      */   
/*      */   public void setToolTip(Object tooltip)
/*      */   {
/*  887 */     this.tooltip = tooltip;
/*  888 */     updateTooltip();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDefaultToolTip(Object o)
/*      */   {
/*  895 */     this.default_tooltip = o;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getDefaultToolTip()
/*      */   {
/*  901 */     return this.default_tooltip;
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateTooltip()
/*      */   {
/*  907 */     if (!isDisposed()) {
/*  908 */       Object target = this.tooltip == null ? this.default_tooltip : this.tooltip;
/*      */       
/*  910 */       this.composite.setToolTipText(target == null ? null : target.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean _setSortValue(Comparable valueToSort) {
/*  915 */     if (this.sortValue == valueToSort) {
/*  916 */       return false;
/*      */     }
/*  918 */     if (((valueToSort instanceof String)) && ((this.sortValue instanceof String)) && (this.sortValue.equals(valueToSort)))
/*      */     {
/*  920 */       return false;
/*      */     }
/*      */     
/*  923 */     if (((valueToSort instanceof Number)) && ((this.sortValue instanceof Number)) && (this.sortValue.equals(valueToSort)))
/*      */     {
/*  925 */       return false;
/*      */     }
/*      */     
/*  928 */     this.sortValue = valueToSort;
/*      */     
/*  930 */     return true;
/*      */   }
/*      */   
/*      */   public boolean setSortValue(long valueToSort) {
/*  934 */     if (((this.sortValue instanceof Long)) && (((Long)this.sortValue).longValue() == valueToSort))
/*      */     {
/*  936 */       return false;
/*      */     }
/*  938 */     return _setSortValue(new Long(valueToSort));
/*      */   }
/*      */   
/*      */   public void doPaint(GC gc, Rectangle bounds) {
/*  942 */     if (isDisposed()) {
/*  943 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  952 */     if (bounds == null) {
/*  953 */       return;
/*      */     }
/*      */     
/*  956 */     if (!bounds.intersects(gc.getClipping())) {
/*  957 */       return;
/*      */     }
/*      */     
/*      */ 
/*  961 */     if ((this.image != null) && (!this.image.isDisposed())) {
/*  962 */       Point size = new Point(bounds.width, bounds.height);
/*      */       
/*      */ 
/*      */ 
/*  966 */       int y = this.marginHeight;
/*  967 */       y += (size.y - this.imageBounds.height) / 2;
/*      */       int x;
/*  969 */       if (this.orientation == 16777216) {
/*  970 */         int x = this.marginWidth;
/*  971 */         x += (size.x - this.marginWidth * 2 - this.imageBounds.width) / 2; } else { int x;
/*  972 */         if (this.orientation == 131072) {
/*  973 */           x = bounds.width - this.marginWidth - this.imageBounds.width;
/*      */         } else {
/*  975 */           x = this.marginWidth;
/*      */         }
/*      */       }
/*  978 */       int width = Math.min(bounds.width - x - this.marginWidth, this.imageBounds.width);
/*  979 */       int height = Math.min(bounds.height - y - this.marginHeight, this.imageBounds.height);
/*      */       
/*      */ 
/*  982 */       if ((width >= 0) && (height >= 0)) {
/*  983 */         gc.drawImage(this.image, 0, 0, width, height, bounds.x + x, bounds.y + y, width, height);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  988 */     if ((this.text != null) && (this.text.length() > 0)) {
/*  989 */       GCStringPrinter sp = new GCStringPrinter(gc, this.text, bounds, true, false, this.wrapText ? this.orientation | 0x40 : this.orientation);
/*      */       
/*  991 */       sp.printString();
/*  992 */       this.hadMore = sp.isCutoff();
/*      */     }
/*      */     
/*  995 */     invokeSWTPaintListeners(gc);
/*      */   }
/*      */   
/*      */   public boolean refresh()
/*      */   {
/* 1000 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1002 */         boolean wasValid = FakeTableCell.this.valid;
/*      */         try {
/* 1004 */           FakeTableCell.this.tableColumn.invokeCellRefreshListeners(FakeTableCell.this, false);
/*      */         }
/*      */         catch (Throwable e) {}
/* 1007 */         if (FakeTableCell.this.refreshListeners != null) {
/* 1008 */           for (int i = 0; i < FakeTableCell.this.refreshListeners.size(); i++) {
/* 1009 */             ((TableCellRefreshListener)FakeTableCell.this.refreshListeners.get(i)).refresh(FakeTableCell.this);
/*      */           }
/*      */         }
/* 1012 */         if (!wasValid) {
/* 1013 */           FakeTableCell.this.valid = true;
/*      */         }
/*      */       }
/* 1016 */     });
/* 1017 */     return true;
/*      */   }
/*      */   
/*      */   public void setDataSource(Object _coreDataSource) {
/* 1021 */     this.coreDataSource = _coreDataSource;
/* 1022 */     if ((_coreDataSource != null) && (!isDisposed())) {
/* 1023 */       invokeVisibilityListeners(0, true);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setControl(Composite composite)
/*      */   {
/* 1029 */     setControl(composite, null, true);
/*      */   }
/*      */   
/*      */   public void setControl(Composite composite, Rectangle cellArea, boolean addListeners) {
/* 1033 */     if (composite == null) {
/* 1034 */       dispose();
/* 1035 */       this.composite = null;
/* 1036 */       return;
/*      */     }
/*      */     
/* 1039 */     this.composite = composite;
/* 1040 */     this.cellArea = cellArea;
/*      */     
/* 1042 */     if (addListeners) {
/* 1043 */       composite.addPaintListener(this);
/* 1044 */       composite.addMouseListener(this);
/* 1045 */       composite.addMouseMoveListener(this);
/* 1046 */       composite.addMouseTrackListener(this);
/*      */     }
/*      */     
/* 1049 */     setForeground(-1, -1, -1);
/* 1050 */     setText(null);
/* 1051 */     setToolTip(null);
/*      */     
/* 1053 */     composite.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 1055 */         FakeTableCell.this.dispose();
/*      */       }
/*      */     });
/* 1058 */     if ((this.coreDataSource != null) && (!isDisposed())) {
/* 1059 */       invokeVisibilityListeners(0, true);
/*      */     }
/*      */   }
/*      */   
/*      */   public void paintControl(PaintEvent e)
/*      */   {
/* 1065 */     doPaint(e.gc, this.cellArea == null ? this.composite.getClientArea() : this.cellArea);
/*      */   }
/*      */   
/*      */   public void mouseUp(MouseEvent e) {
/* 1069 */     invokeMouseListeners(buildMouseEvent(e, 1));
/*      */   }
/*      */   
/*      */   public void mouseDown(MouseEvent e) {
/*      */     try {
/* 1074 */       if ((this.composite == null) || (this.composite.getMenu() != null) || ((this.cellMouseListeners != null) && (this.cellMouseListeners.size() > 0)) || (this.text == null) || (this.text.length() == 0)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1081 */       if ((e.button != 3) && ((e.button != 1) || (e.stateMask != 262144))) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 1086 */       Menu menu = new Menu(this.composite.getShell(), 8);
/*      */       
/* 1088 */       MenuItem item = new MenuItem(menu, 0);
/*      */       
/* 1090 */       item.setText(MessageText.getString("ConfigView.copy.to.clipboard.tooltip"));
/*      */       
/* 1092 */       item.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void widgetSelected(SelectionEvent arg0)
/*      */         {
/*      */ 
/* 1099 */           if ((!FakeTableCell.this.composite.isDisposed()) && (FakeTableCell.this.text != null) && (FakeTableCell.this.text.length() > 0))
/*      */           {
/* 1101 */             new Clipboard(FakeTableCell.this.composite.getDisplay()).setContents(new Object[] { FakeTableCell.this.text }, new Transfer[] { org.eclipse.swt.dnd.TextTransfer.getInstance() });
/*      */           }
/*      */           
/*      */         }
/* 1105 */       });
/* 1106 */       this.composite.setMenu(menu);
/*      */       
/* 1108 */       menu.addMenuListener(new MenuAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void menuHidden(MenuEvent arg0)
/*      */         {
/*      */ 
/* 1115 */           if (!FakeTableCell.this.composite.isDisposed())
/*      */           {
/* 1117 */             FakeTableCell.this.composite.setMenu(null);
/*      */           }
/*      */           
/*      */         }
/* 1121 */       });
/* 1122 */       menu.setVisible(true);
/*      */     }
/*      */     finally
/*      */     {
/* 1126 */       invokeMouseListeners(buildMouseEvent(e, 0));
/*      */     }
/*      */   }
/*      */   
/*      */   public void mouseDoubleClick(MouseEvent e) {
/* 1131 */     invokeMouseListeners(buildMouseEvent(e, 2));
/*      */   }
/*      */   
/*      */   public void mouseMove(MouseEvent e)
/*      */   {
/* 1136 */     invokeMouseListeners(buildMouseEvent(e, 3));
/*      */   }
/*      */   
/*      */   public void mouseHover(MouseEvent e) {
/* 1140 */     invokeToolTipListeners(0);
/*      */   }
/*      */   
/*      */   public void mouseExit(MouseEvent e) {
/* 1144 */     invokeMouseListeners(buildMouseEvent(e, 5));
/*      */   }
/*      */   
/*      */   public void mouseEnter(MouseEvent e) {
/* 1148 */     invokeMouseListeners(buildMouseEvent(e, 4));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TableCellMouseEvent buildMouseEvent(MouseEvent e, int eventType)
/*      */   {
/* 1159 */     if (isDisposed()) {
/* 1160 */       return null;
/*      */     }
/* 1162 */     TableCellMouseEvent event = new TableCellMouseEvent();
/* 1163 */     event.cell = this;
/* 1164 */     event.button = e.button;
/* 1165 */     event.keyboardState = e.stateMask;
/* 1166 */     event.eventType = eventType;
/*      */     
/* 1168 */     Rectangle r = this.composite.getBounds();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1175 */     if (this.cellArea != null) {
/* 1176 */       r = new Rectangle(r.x + this.cellArea.x, r.y + this.cellArea.y, this.cellArea.width, this.cellArea.height);
/*      */     }
/*      */     
/*      */ 
/* 1180 */     event.x = (e.x - r.x);
/* 1181 */     event.y = (e.y - r.y);
/*      */     
/* 1183 */     return event;
/*      */   }
/*      */   
/*      */   private void setOrientationViaColumn() {
/* 1187 */     this.orientation = org.gudy.azureus2.ui.swt.views.table.utils.TableColumnSWTUtils.convertColumnAlignmentToSWT(this.tableColumn.getAlignment());
/*      */   }
/*      */   
/*      */   public void dispose()
/*      */   {
/* 1192 */     if ((this.composite != null) && (!this.composite.isDisposed())) {
/* 1193 */       this.composite.removePaintListener(this);
/* 1194 */       this.composite.removeMouseListener(this);
/* 1195 */       this.composite.removeMouseMoveListener(this);
/* 1196 */       this.composite.removeMouseTrackListener(this);
/*      */     }
/*      */     
/* 1199 */     if (this.disposeListeners != null) {
/* 1200 */       for (Iterator iter = this.disposeListeners.iterator(); iter.hasNext();) {
/* 1201 */         TableCellDisposeListener listener = (TableCellDisposeListener)iter.next();
/*      */         try {
/* 1203 */           listener.dispose(this);
/*      */         } catch (Throwable e) {
/* 1205 */           Debug.out(e);
/*      */         }
/*      */       }
/* 1208 */       this.disposeListeners = null;
/*      */     }
/* 1210 */     this.tableColumn.invokeCellDisposeListeners(this);
/* 1211 */     this.tableColumn.invalidateCells();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCursorID()
/*      */   {
/* 1217 */     return 0;
/*      */   }
/*      */   
/*      */   public String getObfusticatedText()
/*      */   {
/* 1222 */     return this.text;
/*      */   }
/*      */   
/*      */   public TableRowCore getTableRowCore()
/*      */   {
/* 1227 */     return null;
/*      */   }
/*      */   
/*      */   public boolean getVisuallyChangedSinceRefresh()
/*      */   {
/* 1232 */     return true;
/*      */   }
/*      */   
/*      */   public void invalidate(boolean mustRefresh)
/*      */   {
/* 1237 */     this.valid = false;
/*      */   }
/*      */   
/*      */   public void invokeToolTipListeners(int type)
/*      */   {
/* 1242 */     if (this.tableColumn == null) {
/* 1243 */       return;
/*      */     }
/* 1245 */     this.tableColumn.invokeCellToolTipListeners(this, type);
/*      */     
/* 1247 */     if (this.tooltipListeners == null) {
/* 1248 */       return;
/*      */     }
/*      */     try {
/* 1251 */       if (type == 0) {
/* 1252 */         for (int i = 0; i < this.tooltipListeners.size(); i++)
/* 1253 */           ((TableCellToolTipListener)this.tooltipListeners.get(i)).cellHover(this);
/*      */       } else {
/* 1255 */         for (int i = 0; i < this.tooltipListeners.size(); i++)
/* 1256 */           ((TableCellToolTipListener)this.tooltipListeners.get(i)).cellHoverComplete(this);
/*      */       }
/*      */     } catch (Throwable e) {
/* 1259 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void invokeVisibilityListeners(int visibility, boolean invokeColumnListeners)
/*      */   {
/* 1266 */     if (invokeColumnListeners) {
/* 1267 */       this.tableColumn.invokeCellVisibilityListeners(this, visibility);
/*      */     }
/*      */     
/* 1270 */     if (this.cellVisibilityListeners == null) {
/* 1271 */       return;
/*      */     }
/* 1273 */     for (int i = 0; i < this.cellVisibilityListeners.size(); i++) {
/*      */       try {
/* 1275 */         TableCellVisibilityListener l = (TableCellVisibilityListener)this.cellVisibilityListeners.get(i);
/*      */         
/* 1277 */         l.cellVisibilityChanged(this, visibility);
/*      */       }
/*      */       catch (Throwable e) {
/* 1280 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isMouseOver()
/*      */   {
/* 1287 */     if (isDisposed()) {
/* 1288 */       return false;
/*      */     }
/* 1290 */     Rectangle r = this.composite.getBounds();
/* 1291 */     if (this.cellArea != null) {
/* 1292 */       r = new Rectangle(r.x + this.cellArea.x, r.y + this.cellArea.y, this.cellArea.width, this.cellArea.height);
/*      */     }
/*      */     
/* 1295 */     Point ptStart = this.composite.toDisplay(r.x, r.y);
/* 1296 */     r.x = ptStart.x;
/* 1297 */     r.y = ptStart.y;
/* 1298 */     Point ptCursor = this.composite.getDisplay().getCursorLocation();
/* 1299 */     return r.contains(ptCursor);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setMouseOver(boolean b) {}
/*      */   
/*      */ 
/*      */   public boolean isUpToDate()
/*      */   {
/* 1308 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void locationChanged() {}
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean needsPainting()
/*      */   {
/* 1319 */     return true;
/*      */   }
/*      */   
/*      */   public boolean refresh(boolean doGraphics)
/*      */   {
/* 1324 */     return refresh();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean refresh(boolean doGraphics, boolean rowVisible, boolean cellVisible)
/*      */   {
/* 1330 */     return refresh();
/*      */   }
/*      */   
/*      */   public boolean refresh(boolean doGraphics, boolean rowVisible)
/*      */   {
/* 1335 */     return refresh();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean setCursorID(int cursorID)
/*      */   {
/* 1341 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUpToDate(boolean upToDate) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public int compareTo(Object arg0)
/*      */   {
/* 1353 */     return 0;
/*      */   }
/*      */   
/*      */   public void setOrentation(int o) {
/* 1357 */     this.orientation = o;
/*      */   }
/*      */   
/*      */   public Rectangle getCellArea() {
/* 1361 */     return this.cellArea;
/*      */   }
/*      */   
/*      */   public void setCellArea(Rectangle cellArea)
/*      */   {
/* 1366 */     this.cellArea = cellArea;
/*      */   }
/*      */   
/*      */   public int[] getMouseOffset()
/*      */   {
/* 1371 */     if (isDisposed()) {
/* 1372 */       return null;
/*      */     }
/* 1374 */     Rectangle r = this.composite.getBounds();
/* 1375 */     if (this.cellArea != null) {
/* 1376 */       r = new Rectangle(r.x + this.cellArea.x, r.y + this.cellArea.y, this.cellArea.width, this.cellArea.height);
/*      */     }
/*      */     
/* 1379 */     Point ptStart = this.composite.toDisplay(r.x, r.y);
/* 1380 */     r.x = ptStart.x;
/* 1381 */     r.y = ptStart.y;
/* 1382 */     Point ptCursor = this.composite.getDisplay().getCursorLocation();
/* 1383 */     if (!r.contains(ptCursor)) {
/* 1384 */       return null;
/*      */     }
/* 1386 */     return new int[] { ptCursor.x - r.x, ptCursor.y - r.y };
/*      */   }
/*      */   
/*      */   public int getMarginHeight()
/*      */   {
/* 1391 */     return this.marginHeight;
/*      */   }
/*      */   
/*      */   public int getMarginWidth()
/*      */   {
/* 1396 */     return this.marginWidth;
/*      */   }
/*      */   
/*      */   public void refreshAsync()
/*      */   {
/* 1401 */     refresh();
/*      */   }
/*      */   
/*      */   public void redraw()
/*      */   {
/* 1406 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1408 */         if (!FakeTableCell.this.isDisposed()) {
/* 1409 */           FakeTableCell.this.composite.redraw();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void doPaint(GC gc)
/*      */   {
/* 1417 */     doPaint(gc, this.cellArea == null ? this.composite.getClientArea() : this.cellArea);
/*      */   }
/*      */   
/*      */ 
/*      */   public Image getBackgroundImage()
/*      */   {
/* 1423 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Color getBackgroundSWT()
/*      */   {
/* 1429 */     return this.composite.getBackground();
/*      */   }
/*      */   
/*      */   public Rectangle getBounds()
/*      */   {
/* 1434 */     return this.cellArea == null ? this.composite.getClientArea() : new Rectangle(this.cellArea.x, this.cellArea.y, this.cellArea.width, this.cellArea.height);
/*      */   }
/*      */   
/*      */ 
/*      */   public Color getForegroundSWT()
/*      */   {
/* 1440 */     return this.composite.getForeground();
/*      */   }
/*      */   
/*      */ 
/*      */   public Image getGraphicSWT()
/*      */   {
/* 1446 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Image getIcon()
/*      */   {
/* 1452 */     return null;
/*      */   }
/*      */   
/*      */   public Point getSize()
/*      */   {
/* 1457 */     Rectangle bounds = getBounds();
/* 1458 */     if (bounds == null) {
/* 1459 */       return null;
/*      */     }
/* 1461 */     return new Point(bounds.width, bounds.height);
/*      */   }
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.ui.swt.views.table.TableRowSWT getTableRowSWT()
/*      */   {
/* 1467 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTextAlpha()
/*      */   {
/* 1473 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean setForeground(Color color)
/*      */   {
/* 1479 */     return false;
/*      */   }
/*      */   
/*      */   public boolean setGraphic(Image img)
/*      */   {
/* 1484 */     this.graphic = null;
/*      */     
/* 1486 */     this.image = img;
/* 1487 */     if (this.image != null) {
/* 1488 */       this.imageBounds = this.image.getBounds();
/*      */     }
/*      */     
/* 1491 */     if ((this.composite != null) && (!this.composite.isDisposed())) {
/* 1492 */       redraw();
/*      */     }
/*      */     
/* 1495 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean setIcon(Image img)
/*      */   {
/* 1501 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTextAlpha(int textOpacity) {}
/*      */   
/*      */ 
/*      */   public Rectangle getBoundsOnDisplay()
/*      */   {
/* 1511 */     Rectangle bounds = getBounds();
/* 1512 */     Point pt = this.composite.toDisplay(bounds.x, bounds.y);
/* 1513 */     bounds.x = pt.x;
/* 1514 */     bounds.y = pt.y;
/* 1515 */     return bounds;
/*      */   }
/*      */   
/*      */   public TableColumnCore getTableColumnCore() {
/* 1519 */     return this.tableColumn;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/FakeTableCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */