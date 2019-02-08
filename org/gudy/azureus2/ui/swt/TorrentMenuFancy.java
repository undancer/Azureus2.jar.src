/*      */ package org.gudy.azureus2.ui.swt;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedLimitHandler;
/*      */ import com.aelitis.azureus.core.util.HTTPUtils;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap;
/*      */ import com.aelitis.azureus.core.util.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.MenuDetectEvent;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.events.ShellEvent;
/*      */ import org.eclipse.swt.events.ShellListener;
/*      */ import org.eclipse.swt.events.TraverseEvent;
/*      */ import org.eclipse.swt.events.TraverseListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FillLayout;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.layout.RowData;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.Monitor;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Widget;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.ui.Graphic;
/*      */ import org.gudy.azureus2.plugins.ui.GraphicURI;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuBuilder;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.menus.MenuItemImpl;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.SelectableSpeedMenu;
/*      */ import org.gudy.azureus2.ui.swt.minibar.DownloadBar;
/*      */ import org.gudy.azureus2.ui.swt.minibar.MiniBarManager;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*      */ import org.gudy.azureus2.ui.swt.sharing.ShareUtils;
/*      */ import org.gudy.azureus2.ui.swt.shells.AdvRenameWindow;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableSelectedRowsListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.utils.TableContextMenuManager;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*      */ 
/*      */ public class TorrentMenuFancy
/*      */ {
/*      */   private static final String HEADER_CONTROL = "Control";
/*      */   private static final String HEADER_SOCIAL = "Social";
/*      */   private static final String HEADER_ORGANIZE = "Organize";
/*      */   private static final String HEADER_MENU = "Other";
/*      */   private static final String HEADER_MSG_PREFIX = "FancyMenu.Header.";
/*      */   private static final String HEADER_CONTENT = "Content";
/*      */   protected static final boolean DEBUG_MENU = false;
/*      */   private static final int SHELL_MARGIN = 1;
/*      */   
/*      */   private static class HeaderInfo
/*      */   {
/*      */     private Runnable runnable;
/*      */     private Composite composite;
/*      */     private String id;
/*      */     
/*      */     public HeaderInfo(String id, Runnable runnable, Composite composite)
/*      */     {
/*  112 */       this.id = id;
/*  113 */       this.runnable = runnable;
/*  114 */       this.composite = composite;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class FancyRowInfo
/*      */   {
/*      */     private Listener listener;
/*      */     
/*      */     private Label lblText;
/*      */     
/*      */     private Label lblRight;
/*      */     
/*      */     private Label lblIcon;
/*      */     
/*      */     private Label lblCheck;
/*      */     
/*      */     private Composite cRow;
/*      */     
/*      */     private boolean keepMenu;
/*      */     private boolean isSelected;
/*      */     private boolean hasSubMenu;
/*      */     
/*      */     public void setEnabled(boolean enabled)
/*      */     {
/*  139 */       this.cRow.setEnabled(enabled);
/*      */     }
/*      */     
/*      */     public Label getRightLabel() {
/*  143 */       if (this.lblRight == null) {
/*  144 */         this.lblRight = new Label(this.cRow, 0);
/*  145 */         GridData gd = new GridData();
/*  146 */         gd.horizontalIndent = 10;
/*  147 */         Utils.setLayoutData(this.lblRight, gd);
/*  148 */         this.lblRight.setEnabled(false);
/*      */       }
/*  150 */       return this.lblRight;
/*      */     }
/*      */     
/*      */     public Listener getListener() {
/*  154 */       return this.listener;
/*      */     }
/*      */     
/*      */     public void setListener(Listener listener) {
/*  158 */       this.listener = listener;
/*      */     }
/*      */     
/*      */     public Label getText() {
/*  162 */       return this.lblText;
/*      */     }
/*      */     
/*      */     public void setText(Label lblText) {
/*  166 */       this.lblText = lblText;
/*      */     }
/*      */     
/*      */     public void setRightLabel(Label lblRight) {
/*  170 */       this.lblRight = lblRight;
/*      */     }
/*      */     
/*      */     public void setRightLabelText(String s) {
/*  174 */       getRightLabel().setText(s);
/*      */     }
/*      */     
/*      */     public Label getIconLabel() {
/*  178 */       return this.lblIcon;
/*      */     }
/*      */     
/*      */     public void setIconLabel(Label lblIcon) {
/*  182 */       this.lblIcon = lblIcon;
/*      */     }
/*      */     
/*      */     public Composite getRow() {
/*  186 */       return this.cRow;
/*      */     }
/*      */     
/*      */     public void setRow(Composite cRow) {
/*  190 */       this.cRow = cRow;
/*      */     }
/*      */     
/*      */     public boolean keepMenu() {
/*  194 */       return this.keepMenu;
/*      */     }
/*      */     
/*      */     public void setKeepMenu(boolean keepMenu) {
/*  198 */       this.keepMenu = keepMenu;
/*      */     }
/*      */     
/*      */     public void setSelection(boolean isSelected) {
/*  202 */       this.isSelected = isSelected;
/*  203 */       ImageLoader.getInstance().setLabelImage(this.lblCheck, isSelected ? "check_yes" : "check_no");
/*      */     }
/*      */     
/*      */     public boolean isSelected()
/*      */     {
/*  208 */       return this.isSelected;
/*      */     }
/*      */     
/*      */     public void setCheckLabel(Label lblCheck) {
/*  212 */       this.lblCheck = lblCheck;
/*      */     }
/*      */     
/*      */     public boolean hasSubMenu() {
/*  216 */       return this.hasSubMenu;
/*      */     }
/*      */     
/*      */ 
/*  220 */     public void setHasSubMenu(boolean hasSubMenu) { this.hasSubMenu = hasSubMenu; } }
/*      */   
/*      */   private static abstract interface FancyMenuRowInfoListener { public abstract void buildMenu(Menu paramMenu); }
/*      */   
/*  224 */   private static class FancyMenuRowInfo extends TorrentMenuFancy.FancyRowInfo { private FancyMenuRowInfo() { super(); }
/*      */     
/*      */ 
/*      */ 
/*      */     public Menu getMenu()
/*      */     {
/*  230 */       return this.menu;
/*      */     }
/*      */     
/*      */     public void setMenu(Menu menu) {
/*  234 */       this.menu = menu;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private Menu menu;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  247 */   private List<FancyRowInfo> listRowInfos = new ArrayList();
/*      */   
/*  249 */   private List<HeaderInfo> listHeaders = new ArrayList();
/*      */   
/*      */   private Composite topArea;
/*      */   
/*      */   private Composite detailArea;
/*      */   
/*      */   private Listener headerListener;
/*      */   
/*      */   private TableViewSWT<?> tv;
/*      */   
/*      */   private boolean isSeedingView;
/*      */   
/*      */   private Shell parentShell;
/*      */   
/*      */   private DownloadManager[] dms;
/*      */   
/*      */   private String tableID;
/*      */   
/*      */   private boolean hasSelection;
/*      */   
/*  269 */   private Map<String, String[]> mapMovedPluginMenus = new HashMap();
/*      */   
/*  271 */   private Map<String, Integer> mapMovedPluginMenuUserMode = new HashMap();
/*      */   
/*  273 */   private List<String> listMovedPluginIDs = new ArrayList();
/*      */   
/*      */   private Shell shell;
/*      */   
/*      */   private Listener listenerForTrigger;
/*      */   
/*      */   private Listener listenerRow;
/*      */   
/*      */   private PaintListener listenerRowPaint;
/*      */   
/*      */   private TableColumnCore column;
/*      */   
/*      */   private HeaderInfo activatedHeader;
/*      */   
/*      */   private Menu currentMenu;
/*      */   
/*      */   private FancyRowInfo currentRowInfo;
/*      */   
/*      */   private Point originalShellLocation;
/*      */   
/*      */   private boolean subMenuVisible;
/*      */   
/*      */   private PaintListener paintListenerArrow;
/*      */   
/*      */ 
/*      */   public TorrentMenuFancy(TableViewSWT<?> tv, boolean isSeedingView, Shell parentShell, DownloadManager[] dms, String tableID)
/*      */   {
/*  300 */     this.tv = tv;
/*  301 */     this.isSeedingView = isSeedingView;
/*  302 */     this.parentShell = parentShell;
/*  303 */     this.dms = dms;
/*  304 */     this.tableID = tableID;
/*  305 */     this.hasSelection = (dms.length > 0);
/*      */     
/*  307 */     String[] ids_control = { "azpeerinjector.contextmenu.inject", "tablemenu.main.item", "StartStopRules.menu.viewDebug", "MyTorrentsView.menu.rename.displayed" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  313 */     this.mapMovedPluginMenuUserMode.put("tablemenu.main.item", Integer.valueOf(2));
/*  314 */     this.mapMovedPluginMenuUserMode.put("azpeerinjector.contextmenu.inject", Integer.valueOf(2));
/*      */     
/*  316 */     this.mapMovedPluginMenus.put("Control", ids_control);
/*  317 */     this.listMovedPluginIDs.addAll(Arrays.asList(ids_control));
/*      */     
/*  319 */     String[] ids_social = { "azsubs.contextmenu.lookupassoc", "rcm.contextmenu.lookupassoc", "rcm.contextmenu.lookupsize", "MagnetPlugin.contextmenu.exporturi", "azbuddy.contextmenu", "RatingPlugin.contextmenu.manageRating", "label.chat" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  328 */     this.mapMovedPluginMenus.put("Social", ids_social);
/*  329 */     this.listMovedPluginIDs.addAll(Arrays.asList(ids_social));
/*      */     
/*  331 */     String[] ids_content = { "upnpmediaserver.contextmenu", "devices.contextmenu.xcode", "antivirus.ui.contextmenu.scan", "vuzexcode.transcode", "burn.menu.addtodvd" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  338 */     this.mapMovedPluginMenus.put("Content", ids_content);
/*  339 */     this.listMovedPluginIDs.addAll(Arrays.asList(ids_content));
/*      */     
/*  341 */     this.listenerForTrigger = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  343 */         TorrentMenuFancy.FancyRowInfo rowInfo = TorrentMenuFancy.this.findRowInfo(event.widget);
/*  344 */         if (rowInfo != null) {
/*  345 */           if (!rowInfo.keepMenu()) {
/*  346 */             TorrentMenuFancy.this.shell.dispose();
/*      */           }
/*      */           
/*  349 */           if (rowInfo.getListener() != null) {
/*  350 */             rowInfo.getListener().handleEvent(event);
/*      */           }
/*      */         } else {
/*  353 */           TorrentMenuFancy.this.shell.dispose();
/*      */         }
/*      */         
/*      */       }
/*  357 */     };
/*  358 */     this.paintListenerArrow = new PaintListener() {
/*      */       public void paintControl(PaintEvent e) {
/*  360 */         Control c = (Control)e.widget;
/*  361 */         Point size = c.getSize();
/*  362 */         int arrowSize = Utils.adjustPXForDPI(8);
/*  363 */         int xStart = size.x - arrowSize;
/*  364 */         int yStart = size.y - (size.y + arrowSize) / 2;
/*  365 */         e.gc.setBackground(e.display.getSystemColor(21));
/*  366 */         e.gc.setAntialias(1);
/*  367 */         e.gc.fillPolygon(new int[] { xStart, yStart, xStart + arrowSize, yStart + arrowSize / 2, xStart, yStart + arrowSize });
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  377 */     };
/*  378 */     this.listenerRow = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  380 */         Composite parent = TorrentMenuFancy.this.detailArea;
/*  381 */         Rectangle bounds = parent.getBounds();
/*  382 */         if (event.type == 7) {
/*  383 */           TorrentMenuFancy.this.currentRowInfo = null;
/*  384 */           parent.redraw(0, 0, bounds.width, bounds.height, true);
/*  385 */         } else if (event.type == 6) {
/*  386 */           TorrentMenuFancy.FancyRowInfo rowInfo = TorrentMenuFancy.this.findRowInfo(event.widget);
/*  387 */           TorrentMenuFancy.this.currentRowInfo = rowInfo;
/*  388 */           parent.redraw(0, 0, bounds.width, bounds.height, true);
/*      */         }
/*      */         
/*      */       }
/*  392 */     };
/*  393 */     this.listenerRowPaint = new PaintListener() {
/*      */       public void paintControl(PaintEvent e) {
/*  395 */         TorrentMenuFancy.FancyRowInfo rowInfo = TorrentMenuFancy.this.findRowInfo(e.widget);
/*  396 */         if (rowInfo == null) {
/*  397 */           return;
/*      */         }
/*      */         
/*  400 */         boolean isSelected = TorrentMenuFancy.this.currentRowInfo == rowInfo;
/*      */         
/*  402 */         if (!isSelected) {
/*  403 */           for (Control control : ((Composite)e.widget).getChildren()) {
/*  404 */             control.setBackground(null);
/*  405 */             control.setForeground(null);
/*      */           }
/*      */           
/*  408 */           return;
/*      */         }
/*  410 */         Rectangle bounds = ((Control)e.widget).getBounds();
/*      */         
/*  412 */         Color bg = e.display.getSystemColor(25);
/*  413 */         int arc = bounds.height / 3;
/*  414 */         e.gc.setBackground(bg);
/*  415 */         e.gc.setForeground(e.display.getSystemColor(17));
/*  416 */         e.gc.setAntialias(1);
/*      */         
/*  418 */         e.gc.fillRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, arc, arc);
/*      */         
/*  420 */         e.gc.setAlpha(100);
/*  421 */         e.gc.drawRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, arc, arc);
/*      */         
/*      */ 
/*  424 */         Color fg = e.display.getSystemColor(24);
/*  425 */         for (Control control : ((Composite)e.widget).getChildren()) {
/*  426 */           control.setBackground(bg);
/*  427 */           control.setForeground(fg);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  432 */     };
/*  433 */     Collections.sort(this.listMovedPluginIDs);
/*      */   }
/*      */   
/*      */   public void showMenu(TableColumnCore acolumn, final Menu fallbackMenu) {
/*  437 */     this.column = acolumn;
/*  438 */     Display d = this.parentShell.getDisplay();
/*      */     
/*      */ 
/*  441 */     this.shell = new Shell(this.parentShell, 536870920)
/*      */     {
/*      */       protected void checkSubclass() {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void dispose()
/*      */       {
/*  449 */         super.dispose();
/*      */       }
/*      */       
/*      */ 
/*  453 */     };
/*  454 */     RowLayout shellLayout = new RowLayout(512);
/*  455 */     shellLayout.fill = true;
/*  456 */     shellLayout.marginBottom = (shellLayout.marginLeft = shellLayout.marginRight = shellLayout.marginTop = 0);
/*  457 */     shellLayout.marginWidth = (shellLayout.marginHeight = 1);
/*      */     
/*  459 */     this.shell.setLayout(shellLayout);
/*  460 */     this.shell.setBackgroundMode(2);
/*      */     
/*  462 */     this.topArea = new Composite(this.shell, 536870912);
/*  463 */     this.detailArea = new Composite(this.shell, 536870912);
/*      */     
/*  465 */     this.topArea.setBackground(d.getSystemColor(25));
/*  466 */     this.topArea.setForeground(d.getSystemColor(24));
/*      */     
/*  468 */     FormData fd = Utils.getFilledFormData();
/*  469 */     fd.bottom = null;
/*  470 */     RowLayout topLayout = new RowLayout(256);
/*  471 */     topLayout.spacing = 0;
/*  472 */     topLayout.pack = true;
/*  473 */     topLayout.marginBottom = (topLayout.marginTop = topLayout.marginLeft = topLayout.marginRight = 0);
/*  474 */     this.topArea.setLayout(topLayout);
/*      */     
/*      */ 
/*  477 */     fd = Utils.getFilledFormData();
/*  478 */     fd.top = new FormAttachment(this.topArea, 0, 1024);
/*  479 */     FormLayout layoutDetailsArea = new FormLayout();
/*  480 */     layoutDetailsArea.marginWidth = 2;
/*  481 */     layoutDetailsArea.marginBottom = 2;
/*  482 */     this.detailArea.setLayout(layoutDetailsArea);
/*      */     
/*  484 */     this.headerListener = new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  487 */         Control control = (Control)e.widget;
/*  488 */         if (e.type == 9) {
/*  489 */           Rectangle bounds = control.getBounds();
/*  490 */           int y = bounds.height - 2;
/*  491 */           e.gc.drawLine(0, y, bounds.width, y);
/*  492 */         } else if ((e.type == 6) || (e.type == 47)) {
/*  493 */           Object data = e.widget.getData("ID");
/*      */           
/*  495 */           if ((data instanceof TorrentMenuFancy.HeaderInfo)) {
/*  496 */             TorrentMenuFancy.HeaderInfo header = (TorrentMenuFancy.HeaderInfo)data;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  501 */             TorrentMenuFancy.this.activateHeader(header);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  506 */     };
/*  507 */     HeaderInfo firstHeader = addHeader("Control", "FancyMenu.Header.Control", new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  510 */         TorrentMenuFancy.this.buildTorrentCustomMenu_Control(TorrentMenuFancy.this.detailArea, TorrentMenuFancy.this.dms);
/*      */       }
/*  512 */     });
/*  513 */     addHeader("Content", "FancyMenu.Header.Content", new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  516 */         TorrentMenuFancy.this.buildTorrentCustomMenu_Content(TorrentMenuFancy.this.detailArea, TorrentMenuFancy.this.dms);
/*      */       }
/*  518 */     });
/*  519 */     addHeader("Organize", "FancyMenu.Header.Organize", new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  522 */         TorrentMenuFancy.this.buildTorrentCustomMenu_Organize(TorrentMenuFancy.this.detailArea, TorrentMenuFancy.this.dms);
/*      */       }
/*  524 */     });
/*  525 */     addHeader("Social", "FancyMenu.Header.Social", new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  528 */         TorrentMenuFancy.this.buildTorrentCustomMenu_Social(TorrentMenuFancy.this.detailArea);
/*      */       }
/*      */       
/*      */ 
/*  532 */     });
/*  533 */     final List<org.gudy.azureus2.plugins.ui.menus.MenuItem> listOtherItems = new ArrayList();
/*      */     
/*  535 */     TableContextMenuItem[] items = TableContextMenuManager.getInstance().getAllAsArray(this.tableID);
/*      */     
/*      */ 
/*  538 */     for (TableContextMenuItem item : items) {
/*  539 */       if (Collections.binarySearch(this.listMovedPluginIDs, item.getResourceKey()) < 0)
/*      */       {
/*      */ 
/*  542 */         listOtherItems.add(item);
/*      */       }
/*      */     }
/*      */     
/*  546 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray("download_context");
/*      */     
/*  548 */     for (org.gudy.azureus2.plugins.ui.menus.MenuItem item : menu_items) {
/*  549 */       if (Collections.binarySearch(this.listMovedPluginIDs, item.getResourceKey()) < 0)
/*      */       {
/*      */ 
/*  552 */         listOtherItems.add(item);
/*      */       }
/*      */     }
/*      */     
/*  556 */     if (this.column != null) {
/*  557 */       TableContextMenuItem[] columnItems = this.column.getContextMenuItems(2);
/*  558 */       for (TableContextMenuItem item : columnItems) {
/*  559 */         if (Collections.binarySearch(this.listMovedPluginIDs, item.getResourceKey()) < 0)
/*      */         {
/*      */ 
/*  562 */           listOtherItems.add(item);
/*      */         }
/*      */       }
/*      */     }
/*  566 */     if (listOtherItems.size() > 0) {
/*  567 */       addHeader("Other", "FancyMenu.Header.Other", new AERunnable() {
/*      */         public void runSupport() {
/*  569 */           TorrentMenuFancy.this.buildTorrentCustomMenu_Other(TorrentMenuFancy.this.detailArea, listOtherItems);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  574 */     this.originalShellLocation = d.getCursorLocation();
/*  575 */     this.originalShellLocation.x -= 5;
/*  576 */     this.originalShellLocation.y -= 16;
/*      */     
/*  578 */     this.shell.setLocation(this.originalShellLocation);
/*      */     
/*  580 */     this.shell.addPaintListener(new PaintListener()
/*      */     {
/*      */       public void paintControl(PaintEvent e) {
/*  583 */         e.gc.setForeground(e.display.getSystemColor(23));
/*  584 */         Rectangle clientArea = TorrentMenuFancy.this.shell.getClientArea();
/*  585 */         e.gc.drawRectangle(0, 0, clientArea.width - 1, clientArea.height - 1);
/*      */       }
/*      */       
/*  588 */     });
/*  589 */     this.shell.addKeyListener(new org.eclipse.swt.events.KeyListener()
/*      */     {
/*      */       public void keyReleased(KeyEvent e) {}
/*      */       
/*      */       public void keyPressed(KeyEvent e)
/*      */       {
/*  595 */         if (e.keyCode == 16777218) {
/*  596 */           if (TorrentMenuFancy.this.currentRowInfo == null) {
/*  597 */             TorrentMenuFancy.this.currentRowInfo = ((TorrentMenuFancy.FancyRowInfo)TorrentMenuFancy.this.listRowInfos.get(0));
/*      */           } else {
/*  599 */             boolean next = false;
/*  600 */             for (TorrentMenuFancy.FancyRowInfo rowInfo : TorrentMenuFancy.this.listRowInfos) {
/*  601 */               if (next) {
/*  602 */                 TorrentMenuFancy.this.currentRowInfo = rowInfo;
/*  603 */                 next = false;
/*  604 */                 break;
/*      */               }
/*  606 */               if (rowInfo == TorrentMenuFancy.this.currentRowInfo) {
/*  607 */                 next = true;
/*      */               }
/*      */             }
/*  610 */             if (next) {
/*  611 */               TorrentMenuFancy.this.currentRowInfo = ((TorrentMenuFancy.FancyRowInfo)TorrentMenuFancy.this.listRowInfos.get(0));
/*      */             }
/*      */           }
/*  614 */           Rectangle bounds = TorrentMenuFancy.this.detailArea.getBounds();
/*  615 */           TorrentMenuFancy.this.detailArea.redraw(0, 0, bounds.width, bounds.height, true);
/*  616 */         } else if (e.keyCode == 16777217) { TorrentMenuFancy.FancyRowInfo previous;
/*  617 */           if (TorrentMenuFancy.this.currentRowInfo == null) {
/*  618 */             TorrentMenuFancy.this.currentRowInfo = ((TorrentMenuFancy.FancyRowInfo)TorrentMenuFancy.this.listRowInfos.get(TorrentMenuFancy.this.listRowInfos.size() - 1));
/*      */           } else {
/*  620 */             previous = (TorrentMenuFancy.FancyRowInfo)TorrentMenuFancy.this.listRowInfos.get(TorrentMenuFancy.this.listRowInfos.size() - 1);
/*  621 */             for (TorrentMenuFancy.FancyRowInfo rowInfo : TorrentMenuFancy.this.listRowInfos) {
/*  622 */               if (rowInfo == TorrentMenuFancy.this.currentRowInfo) {
/*  623 */                 TorrentMenuFancy.this.currentRowInfo = previous;
/*  624 */                 break;
/*      */               }
/*  626 */               previous = rowInfo;
/*      */             }
/*      */           }
/*  629 */           Rectangle bounds = TorrentMenuFancy.this.detailArea.getBounds();
/*  630 */           TorrentMenuFancy.this.detailArea.redraw(0, 0, bounds.width, bounds.height, true); } else { TorrentMenuFancy.HeaderInfo previous;
/*  631 */           if (e.keyCode == 16777219) {
/*  632 */             previous = (TorrentMenuFancy.HeaderInfo)TorrentMenuFancy.this.listHeaders.get(TorrentMenuFancy.this.listHeaders.size() - 1);
/*  633 */             for (TorrentMenuFancy.HeaderInfo header : TorrentMenuFancy.this.listHeaders) {
/*  634 */               if (header == TorrentMenuFancy.this.activatedHeader) {
/*  635 */                 TorrentMenuFancy.this.activateHeader(previous);
/*  636 */                 break;
/*      */               }
/*  638 */               previous = header;
/*      */             }
/*  640 */           } else if (e.keyCode == 16777220) {
/*  641 */             if ((TorrentMenuFancy.this.currentRowInfo != null) && (TorrentMenuFancy.this.currentRowInfo.hasSubMenu())) {
/*  642 */               Event event = new Event();
/*  643 */               event.display = e.display;
/*  644 */               event.widget = TorrentMenuFancy.FancyRowInfo.access$900(TorrentMenuFancy.this.currentRowInfo);
/*  645 */               TorrentMenuFancy.this.listenerForTrigger.handleEvent(event);
/*      */             } else {
/*  647 */               boolean next = false;
/*  648 */               for (TorrentMenuFancy.HeaderInfo header : TorrentMenuFancy.this.listHeaders) {
/*  649 */                 if (next) {
/*  650 */                   TorrentMenuFancy.this.activateHeader(header);
/*  651 */                   next = false;
/*  652 */                   break;
/*      */                 }
/*  654 */                 if (header == TorrentMenuFancy.this.activatedHeader) {
/*  655 */                   next = true;
/*      */                 }
/*      */               }
/*  658 */               if (next) {
/*  659 */                 TorrentMenuFancy.this.activateHeader((TorrentMenuFancy.HeaderInfo)TorrentMenuFancy.this.listHeaders.get(0));
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  666 */     if (fallbackMenu != null)
/*      */     {
/*  668 */       firstHeader.composite.addMenuDetectListener(new org.eclipse.swt.events.MenuDetectListener()
/*      */       {
/*      */         public void menuDetected(MenuDetectEvent e)
/*      */         {
/*  672 */           TorrentMenuFancy.this.shell.dispose();
/*  673 */           fallbackMenu.setVisible(true);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  678 */     this.shell.addTraverseListener(new TraverseListener() {
/*      */       public void keyTraversed(TraverseEvent e) {
/*  680 */         if (e.detail == 2)
/*      */         {
/*      */ 
/*      */ 
/*  684 */           TorrentMenuFancy.this.shell.dispose();
/*  685 */         } else if ((e.detail == 4) && 
/*  686 */           (TorrentMenuFancy.this.currentRowInfo != null)) {
/*  687 */           Event event = new Event();
/*  688 */           event.display = e.display;
/*  689 */           event.widget = TorrentMenuFancy.FancyRowInfo.access$900(TorrentMenuFancy.this.currentRowInfo);
/*  690 */           TorrentMenuFancy.this.listenerForTrigger.handleEvent(event);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  695 */     });
/*  696 */     this.shell.addShellListener(new ShellListener()
/*      */     {
/*      */       public void shellIconified(ShellEvent e) {}
/*      */       
/*      */ 
/*      */       public void shellDeiconified(ShellEvent e) {}
/*      */       
/*      */       public void shellDeactivated(ShellEvent e)
/*      */       {
/*  705 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*      */           public void runSupport() {
/*  707 */             if (TorrentMenuFancy.this.subMenuVisible) {
/*  708 */               return;
/*      */             }
/*  710 */             if (TorrentMenuFancy.this.shell.isDisposed()) {
/*  711 */               return;
/*      */             }
/*  713 */             Shell[] shells = TorrentMenuFancy.this.shell.getShells();
/*  714 */             if ((shells != null) && (shells.length > 0)) {
/*  715 */               for (Shell aShell : shells) {
/*  716 */                 if (!aShell.isDisposed()) {
/*  717 */                   return;
/*      */                 }
/*      */               }
/*      */             }
/*  721 */             TorrentMenuFancy.this.shell.dispose();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void shellClosed(ShellEvent e) {}
/*      */       
/*      */ 
/*      */       public void shellActivated(ShellEvent e) {}
/*  732 */     });
/*  733 */     activateHeader(firstHeader);
/*      */     
/*  735 */     this.shell.open();
/*      */   }
/*      */   
/*      */   protected void activateHeader(HeaderInfo header) {
/*  739 */     if ((header == null) || (this.activatedHeader == header)) {
/*  740 */       return;
/*      */     }
/*      */     
/*  743 */     if ((this.currentMenu != null) && (!this.currentMenu.isDisposed())) {
/*  744 */       this.currentMenu.setVisible(false);
/*      */     }
/*  746 */     Display d = header.composite.getDisplay();
/*  747 */     header.composite.setBackground(d.getSystemColor(22));
/*  748 */     header.composite.setForeground(d.getSystemColor(21));
/*      */     
/*  750 */     Utils.disposeSWTObjects(this.detailArea.getChildren());
/*  751 */     this.listRowInfos.clear();
/*  752 */     this.currentRowInfo = null;
/*      */     
/*  754 */     if (header.runnable != null) {
/*  755 */       header.runnable.run();
/*      */     }
/*      */     
/*  758 */     String[] ids = (String[])this.mapMovedPluginMenus.get(header.id);
/*  759 */     if (ids != null) {
/*  760 */       addTableItemsWithID(this.detailArea, this.tableID, ids);
/*  761 */       addMenuItemsWithID(this.detailArea, "download_context", ids);
/*  762 */       if (this.column != null) {
/*  763 */         TableContextMenuItem[] columnItems = this.column.getContextMenuItems(2);
/*  764 */         addItemsArray(this.detailArea, columnItems, ids);
/*      */       }
/*      */     }
/*      */     
/*  768 */     Control lastControl = null;
/*  769 */     for (Control child : this.detailArea.getChildren()) {
/*  770 */       FormData fd = new FormData();
/*  771 */       if (lastControl == null) {
/*  772 */         fd.top = new FormAttachment(0);
/*      */       } else {
/*  774 */         fd.top = new FormAttachment(lastControl);
/*      */       }
/*  776 */       fd.left = new FormAttachment(0, 0);
/*  777 */       fd.right = new FormAttachment(100, 0);
/*  778 */       Utils.setLayoutData(child, fd);
/*  779 */       lastControl = child;
/*      */     }
/*      */     
/*  782 */     this.shell.setLocation(this.shell.getLocation().x, this.originalShellLocation.y);
/*  783 */     this.detailArea.moveBelow(null);
/*  784 */     this.shell.pack(true);
/*  785 */     this.detailArea.layout(true, true);
/*      */     
/*  787 */     Point shellSize = this.shell.getSize();
/*  788 */     Point ptBottomRight = this.shell.toDisplay(shellSize);
/*  789 */     Rectangle monitorArea = this.shell.getMonitor().getClientArea();
/*  790 */     if (ptBottomRight.x > monitorArea.x + monitorArea.width) {
/*  791 */       this.shell.setLocation(monitorArea.x + monitorArea.width - shellSize.x, this.shell.getLocation().y);
/*      */     }
/*      */     
/*      */ 
/*  795 */     if (ptBottomRight.y > monitorArea.y + monitorArea.height)
/*      */     {
/*  797 */       if (this.shell.getChildren()[0] != this.detailArea) {
/*  798 */         this.shell.setLocation(this.shell.getLocation().x, this.originalShellLocation.y - this.detailArea.getSize().y - 3);
/*      */         
/*  800 */         this.detailArea.moveAbove(null);
/*  801 */         lastControl = null;
/*  802 */         Control[] children = this.detailArea.getChildren();
/*  803 */         for (int i = 0; i < children.length; i++) {
/*  804 */           Control child = children[(children.length - i - 1)];
/*  805 */           FormData fd = new FormData();
/*  806 */           if (lastControl == null) {
/*  807 */             fd.top = new FormAttachment(0);
/*      */           } else {
/*  809 */             fd.top = new FormAttachment(lastControl);
/*      */           }
/*  811 */           fd.left = new FormAttachment(0, 0);
/*  812 */           fd.right = new FormAttachment(100, 0);
/*  813 */           Utils.setLayoutData(child, fd);
/*  814 */           lastControl = child;
/*      */         }
/*  816 */         this.shell.layout(true, true);
/*      */       }
/*      */     }
/*      */     
/*  820 */     if (this.activatedHeader != null) {
/*  821 */       this.activatedHeader.composite.setBackground(d.getSystemColor(25));
/*  822 */       this.activatedHeader.composite.setForeground(d.getSystemColor(24));
/*      */     }
/*      */     
/*  825 */     this.activatedHeader = header;
/*      */   }
/*      */   
/*      */   public void buildTorrentCustomMenu_Control(Composite cParent, final DownloadManager[] dms)
/*      */   {
/*  830 */     final int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/*  832 */     boolean start = false;
/*  833 */     boolean stop = false;
/*  834 */     boolean recheck = false;
/*  835 */     boolean barsOpened = true;
/*  836 */     boolean bChangeDir = this.hasSelection;
/*      */     
/*  838 */     for (int i = 0; i < dms.length; i++) {
/*  839 */       DownloadManager dm = dms[i];
/*      */       
/*  841 */       if ((barsOpened) && (!DownloadBar.getManager().isOpen(dm))) {
/*  842 */         barsOpened = false;
/*      */       }
/*  844 */       stop = (stop) || (ManagerUtils.isStopable(dm));
/*      */       
/*  846 */       start = (start) || (ManagerUtils.isStartable(dm));
/*      */       
/*  848 */       recheck = (recheck) || (dm.canForceRecheck());
/*      */       
/*  850 */       boolean stopped = ManagerUtils.isStopped(dm);
/*      */       
/*  852 */       int state = dm.getState();
/*  853 */       bChangeDir &= ((state == 100) || (state == 70) || (state == 75));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  862 */       if ((bChangeDir) && (dms.length == 1)) {
/*  863 */         bChangeDir = dm.isDataAlreadyAllocated();
/*  864 */         if ((bChangeDir) && (state == 100))
/*      */         {
/*  866 */           bChangeDir = !dm.filesExist(true);
/*      */         } else {
/*  868 */           DiskManagerFileInfo[] files = dm.getDiskManagerFileInfoSet().getFiles();
/*  869 */           bChangeDir = false;
/*  870 */           for (DiskManagerFileInfo info : files)
/*  871 */             if (!info.isSkipped())
/*      */             {
/*      */ 
/*  874 */               bChangeDir = !info.getFile(true).exists();
/*  875 */               break;
/*      */             }
/*      */         }
/*      */       }
/*      */     }
/*  880 */     Composite cQuickCommands = new Composite(cParent, 0);
/*      */     
/*  882 */     RowLayout rowLayout = new RowLayout(256);
/*  883 */     rowLayout.justify = true;
/*  884 */     rowLayout.marginLeft = 0;
/*  885 */     rowLayout.marginRight = 0;
/*  886 */     cQuickCommands.setLayout(rowLayout);
/*  887 */     GridData gd = new GridData();
/*  888 */     gd.grabExcessHorizontalSpace = true;
/*  889 */     gd.horizontalAlignment = 4;
/*  890 */     Utils.setLayoutData(cQuickCommands, gd);
/*      */     
/*      */ 
/*  893 */     createActionButton(dms, cQuickCommands, "MyTorrentsView.menu.queue", "start", start, new ListenerGetOffSWT()
/*      */     {
/*      */       void handleEventOffSWT(Event event) {
/*  896 */         TorrentUtil.queueDataSources(dms, true);
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*  901 */     if (userMode > 0) {
/*  902 */       boolean forceStart = false;
/*  903 */       boolean forceStartEnabled = false;
/*      */       
/*  905 */       for (int i = 0; i < dms.length; i++) {
/*  906 */         DownloadManager dm = dms[i];
/*      */         
/*  908 */         forceStartEnabled = (forceStartEnabled) || (ManagerUtils.isForceStartable(dm));
/*      */         
/*      */ 
/*  911 */         forceStart = (forceStart) || (dm.isForceStart());
/*      */       }
/*      */       
/*  914 */       final boolean newForceStart = !forceStart;
/*      */       
/*  916 */       createActionButton(dms, cQuickCommands, "MyTorrentsView.menu.forceStart", "forcestart", forceStartEnabled, forceStart ? 2048 : 8, new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager dm)
/*      */         {
/*  920 */           if (ManagerUtils.isForceStartable(dm)) {
/*  921 */             dm.setForceStart(newForceStart);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  928 */     if (userMode > 0) {
/*  929 */       createActionButton(dms, cQuickCommands, "v3.MainWindow.button.pause", "pause", stop, new ListenerGetOffSWT()
/*      */       {
/*      */         public void handleEventOffSWT(Event event) {
/*  932 */           TorrentUtil.pauseDataSources(dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  938 */     createActionButton(dms, cQuickCommands, "MyTorrentsView.menu.stop", "stop", stop, new ListenerGetOffSWT()
/*      */     {
/*      */       public void handleEventOffSWT(Event event) {
/*  941 */         TorrentUtil.stopDataSources(dms);
/*      */       }
/*      */       
/*      */ 
/*  945 */     });
/*  946 */     createActionButton(dms, cQuickCommands, "MyTorrentsView.menu.recheck", "recheck", recheck, new ListenerDMTask(dms)
/*      */     {
/*      */       public void run(DownloadManager dm) {
/*  949 */         if (dm.canForceRecheck()) {
/*  950 */           dm.forceRecheck();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  955 */     });
/*  956 */     createActionButton(dms, cQuickCommands, "menu.delete.options", "delete", this.hasSelection, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/*  959 */         TorrentUtil.removeDownloads(dms, null, true);
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*      */ 
/*  965 */     if (bChangeDir) {
/*  966 */       createRow(cParent, "MyTorrentsView.menu.changeDirectory", null, new Listener()
/*      */       {
/*      */         public void handleEvent(Event e) {
/*  969 */           TorrentUtil.changeDirSelectedTorrents(dms, TorrentMenuFancy.this.parentShell);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  975 */     if (this.hasSelection) {
/*  976 */       createRow(cParent, "MyTorrentsView.menu.showdetails", "details", new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager dm) {
/*  979 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  980 */           if (uiFunctions != null) {
/*  981 */             uiFunctions.getMDI().showEntryByID("DMDetails", dm);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  989 */     if (this.hasSelection) {
/*  990 */       FancyRowInfo row = createRow(cParent, "MyTorrentsView.menu.showdownloadbar", "downloadBar", new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager dm)
/*      */         {
/*  994 */           if (DownloadBar.getManager().isOpen(dm)) {
/*  995 */             DownloadBar.close(dm);
/*      */           } else {
/*  997 */             DownloadBar.open(dm, TorrentMenuFancy.this.parentShell);
/*      */           }
/*      */         }
/* 1000 */       });
/* 1001 */       row.setSelection(barsOpened);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1006 */     if (this.hasSelection) {
/* 1007 */       FancyRowInfo rowSpeedDL = createRow(cParent, "MyTorrentsView.menu.downSpeedLimit", "image.torrentspeed.down", false, new Listener()
/*      */       {
/*      */         public void handleEvent(Event e)
/*      */         {
/* 1011 */           Event event = new Event();
/* 1012 */           event.type = 4;
/* 1013 */           event.widget = e.widget;
/* 1014 */           event.stateMask = e.stateMask;
/* 1015 */           event.button = e.button;
/* 1016 */           e.display.post(event);
/*      */           
/* 1018 */           AzureusCore core = AzureusCoreFactory.getSingleton();
/* 1019 */           SelectableSpeedMenu.invokeSlider((Control)event.widget, core, dms, false, TorrentMenuFancy.this.shell);
/*      */           
/* 1021 */           if (e.display.getActiveShell() != TorrentMenuFancy.this.shell) {
/* 1022 */             if (!TorrentMenuFancy.this.shell.isDisposed()) {
/* 1023 */               TorrentMenuFancy.this.shell.dispose();
/*      */             }
/* 1025 */             return;
/*      */           }
/* 1027 */           TorrentMenuFancy.FancyRowInfo rowInfo = TorrentMenuFancy.this.findRowInfo(event.widget);
/* 1028 */           if (rowInfo != null) {
/* 1029 */             TorrentMenuFancy.this.updateRowSpeed(rowInfo, false);
/*      */           }
/*      */           
/*      */         }
/* 1033 */       });
/* 1034 */       rowSpeedDL.keepMenu = true;
/*      */       
/* 1036 */       updateRowSpeed(rowSpeedDL, false);
/*      */     }
/*      */     
/* 1039 */     if (this.hasSelection) {
/* 1040 */       FancyRowInfo rowSpeedUL = createRow(cParent, "MyTorrentsView.menu.upSpeedLimit", "image.torrentspeed.up", false, new Listener()
/*      */       {
/*      */         public void handleEvent(Event e)
/*      */         {
/* 1044 */           Event event = new Event();
/* 1045 */           event.type = 4;
/* 1046 */           event.widget = e.widget;
/* 1047 */           event.stateMask = e.stateMask;
/* 1048 */           event.button = e.button;
/* 1049 */           e.display.post(event);
/*      */           
/* 1051 */           AzureusCore core = AzureusCoreFactory.getSingleton();
/* 1052 */           SelectableSpeedMenu.invokeSlider((Control)e.widget, core, dms, true, TorrentMenuFancy.this.shell);
/*      */           
/* 1054 */           if (e.display.getActiveShell() != TorrentMenuFancy.this.shell) {
/* 1055 */             if (!TorrentMenuFancy.this.shell.isDisposed()) {
/* 1056 */               TorrentMenuFancy.this.shell.dispose();
/*      */             }
/* 1058 */             return;
/*      */           }
/* 1060 */           TorrentMenuFancy.FancyRowInfo rowInfo = TorrentMenuFancy.this.findRowInfo(event.widget);
/* 1061 */           if (rowInfo != null) {
/* 1062 */             TorrentMenuFancy.this.updateRowSpeed(rowInfo, true);
/*      */           }
/*      */         }
/* 1065 */       });
/* 1066 */       rowSpeedUL.keepMenu = true;
/*      */       
/* 1068 */       updateRowSpeed(rowSpeedUL, true);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1073 */     if ((this.hasSelection) && (userMode > 0)) {
/* 1074 */       createMenuRow(cParent, "MyTorrentsView.menu.tracker", null, new FancyMenuRowInfoListener()
/*      */       {
/*      */         public void buildMenu(Menu menu) {
/* 1077 */           boolean changeUrl = TorrentMenuFancy.this.hasSelection;
/* 1078 */           boolean manualUpdate = true;
/* 1079 */           boolean allStopped = true;
/*      */           
/* 1081 */           int userMode = COConfigurationManager.getIntParameter("User Mode");
/* 1082 */           boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*      */           
/* 1084 */           for (DownloadManager dm : dms) {
/* 1085 */             boolean stopped = ManagerUtils.isStopped(dm);
/*      */             
/* 1087 */             allStopped &= stopped;
/*      */             
/* 1089 */             if (userMode < 2) {
/* 1090 */               TRTrackerAnnouncer trackerClient = dm.getTrackerClient();
/*      */               
/* 1092 */               if (trackerClient != null) {
/* 1093 */                 boolean update_state = SystemTime.getCurrentTime() / 1000L - trackerClient.getLastUpdateTime() >= 60L;
/*      */                 
/* 1095 */                 manualUpdate &= update_state;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1101 */           TorrentUtil.addTrackerTorrentMenu(menu, dms, changeUrl, manualUpdate, allStopped, use_open_containing_folder);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1108 */     if (this.hasSelection) {
/* 1109 */       AzureusCore azureus_core = AzureusCoreFactory.getSingleton();
/*      */       
/* 1111 */       SpeedLimitHandler slh = SpeedLimitHandler.getSingleton(azureus_core);
/*      */       
/* 1113 */       if (slh.hasAnyProfiles())
/*      */       {
/* 1115 */         createMenuRow(cParent, "MainWindow.menu.speed_limits", null, new FancyMenuRowInfoListener()
/*      */         {
/*      */           public void buildMenu(Menu menu) {
/* 1118 */             TorrentUtil.addSpeedLimitsMenu(dms, menu);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/* 1124 */     if ((userMode > 0) && (this.hasSelection))
/*      */     {
/* 1126 */       boolean can_pause = false;
/*      */       
/* 1128 */       for (int i = 0; i < dms.length; i++) {
/* 1129 */         DownloadManager dm = dms[i];
/* 1130 */         if (ManagerUtils.isPauseable(dm)) {
/* 1131 */           can_pause = true;
/* 1132 */           break;
/*      */         }
/*      */       }
/*      */       
/* 1136 */       if (can_pause)
/*      */       {
/* 1138 */         createRow(this.detailArea, "MainWindow.menu.transfers.pausetransfersfor", null, new Listener()
/*      */         {
/*      */           public void handleEvent(Event event) {
/* 1141 */             TorrentUtil.pauseDownloadsFor(dms);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1150 */     if ((userMode > 0) && (dms.length > 1)) {
/* 1151 */       createRow(cParent, "label.options.and.info", null, new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager[] dms) {
/* 1154 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1155 */           if (uiFunctions != null) {
/* 1156 */             uiFunctions.getMDI().showEntryByID("TorrentOptionsView", dms);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1167 */     if (userMode > 0) {
/* 1168 */       createMenuRow(cParent, "MyTorrentsView.menu.peersource", null, new FancyMenuRowInfoListener()
/*      */       {
/*      */         public void buildMenu(Menu menu) {
/* 1171 */           TorrentUtil.addPeerSourceSubMenu(dms, menu);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 1177 */     if ((userMode > 0) && (IpFilterManagerFactory.getSingleton().getIPFilter().isEnabled()))
/*      */     {
/*      */ 
/* 1180 */       boolean allEnabled = true;
/* 1181 */       boolean allDisabled = true;
/*      */       
/* 1183 */       for (int j = 0; j < dms.length; j++) {
/* 1184 */         DownloadManager dm = dms[j];
/*      */         
/* 1186 */         boolean filterDisabled = dm.getDownloadState().getFlag(256L);
/*      */         
/*      */ 
/* 1189 */         if (filterDisabled) {
/* 1190 */           allEnabled = false;
/*      */         } else {
/* 1192 */           allDisabled = false;
/*      */         }
/*      */       }
/*      */       
/*      */       boolean bChecked;
/*      */       boolean bChecked;
/* 1198 */       if (allEnabled) {
/* 1199 */         bChecked = true; } else { boolean bChecked;
/* 1200 */         if (allDisabled) {
/* 1201 */           bChecked = false;
/*      */         } else {
/* 1203 */           bChecked = false;
/*      */         }
/*      */       }
/* 1206 */       final boolean newDisable = bChecked;
/*      */       
/* 1208 */       FancyRowInfo row = createRow(cParent, "MyTorrentsView.menu.ipf_enable", null, new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager dm) {
/* 1211 */           dm.getDownloadState().setFlag(256L, newDisable);
/*      */         }
/*      */         
/*      */ 
/* 1215 */       });
/* 1216 */       row.setSelection(bChecked);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1222 */     if (userMode > 1) {
/* 1223 */       createMenuRow(cParent, "MyTorrentsView.menu.networks", null, new FancyMenuRowInfoListener()
/*      */       {
/*      */         public void buildMenu(Menu menu) {
/* 1226 */           TorrentUtil.addNetworksSubMenu(dms, menu);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 1232 */     if (userMode > 0) {
/* 1233 */       createMenuRow(cParent, "MyTorrentsView.menu.advancedmenu", null, new FancyMenuRowInfoListener()
/*      */       {
/*      */         public void buildMenu(Menu menu)
/*      */         {
/* 1237 */           boolean allStopped = true;
/* 1238 */           boolean allScanSelected = true;
/* 1239 */           boolean allScanNotSelected = true;
/* 1240 */           boolean fileMove = true;
/* 1241 */           boolean allResumeIncomplete = true;
/* 1242 */           boolean hasClearableLinks = false;
/* 1243 */           boolean hasRevertableFiles = false;
/*      */           
/* 1245 */           for (DownloadManager dm : dms) {
/* 1246 */             boolean stopped = ManagerUtils.isStopped(dm);
/*      */             
/* 1248 */             allStopped &= stopped;
/*      */             
/* 1250 */             fileMove = (fileMove) && (dm.canMoveDataFiles());
/*      */             
/* 1252 */             boolean scan = dm.getDownloadState().getFlag(2L);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1257 */             boolean incomplete = !dm.isDownloadComplete(true);
/*      */             
/* 1259 */             allScanSelected = (incomplete) && (allScanSelected) && (scan);
/* 1260 */             allScanNotSelected = (incomplete) && (allScanNotSelected) && (!scan);
/*      */             
/* 1262 */             DownloadManagerState dms = dm.getDownloadState();
/*      */             
/* 1264 */             if (dms.isResumeDataComplete()) {
/* 1265 */               allResumeIncomplete = false;
/*      */             }
/* 1267 */             if ((stopped) && (!hasClearableLinks) && 
/* 1268 */               (dm.getDiskManagerFileInfoSet().nbFiles() > 1) && 
/* 1269 */               (dms.getFileLinks().hasLinks())) {
/* 1270 */               hasClearableLinks = true;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1275 */             if (dm.getDownloadState().getFileLinks().size() > 0)
/*      */             {
/* 1277 */               hasRevertableFiles = true;
/*      */             }
/*      */           }
/*      */           
/* 1281 */           boolean fileRescan = (allScanSelected) || (allScanNotSelected);
/*      */           
/* 1283 */           org.eclipse.swt.widgets.MenuItem itemFileMoveTorrent = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1284 */           Messages.setLanguageText(itemFileMoveTorrent, "MyTorrentsView.menu.movetorrent");
/*      */           
/* 1286 */           itemFileMoveTorrent.addListener(13, new ListenerDMTask(dms)
/*      */           {
/*      */             public void run(DownloadManager[] dms) {
/* 1289 */               TorrentUtil.moveTorrentFile(TorrentMenuFancy.this.parentShell, dms);
/*      */             }
/* 1291 */           });
/* 1292 */           itemFileMoveTorrent.setEnabled(fileMove);
/*      */           
/* 1294 */           final org.eclipse.swt.widgets.MenuItem itemFileRescan = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/* 1295 */           Messages.setLanguageText(itemFileRescan, "MyTorrentsView.menu.rescanfile");
/*      */           
/* 1297 */           itemFileRescan.addListener(13, new ListenerDMTask(dms)
/*      */           {
/*      */             public void run(DownloadManager dm) {
/* 1300 */               dm.getDownloadState().setFlag(2L, itemFileRescan.getSelection());
/*      */             }
/*      */             
/*      */ 
/* 1304 */           });
/* 1305 */           itemFileRescan.setSelection(allScanSelected);
/* 1306 */           itemFileRescan.setEnabled(fileRescan);
/*      */           
/*      */ 
/*      */ 
/* 1310 */           org.eclipse.swt.widgets.MenuItem itemRevertFiles = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1311 */           Messages.setLanguageText(itemRevertFiles, "MyTorrentsView.menu.revertfiles");
/* 1312 */           itemRevertFiles.addListener(13, new ListenerDMTask(dms)
/*      */           {
/*      */             public void run(DownloadManager[] dms) {
/* 1315 */               org.gudy.azureus2.ui.swt.views.FilesViewMenuUtil.revertFiles(TorrentMenuFancy.this.tv, dms);
/*      */             }
/*      */             
/* 1318 */           });
/* 1319 */           itemRevertFiles.setEnabled(hasRevertableFiles);
/*      */           
/*      */ 
/*      */ 
/* 1323 */           org.eclipse.swt.widgets.MenuItem itemClearLinks = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1324 */           Messages.setLanguageText(itemClearLinks, "FilesView.menu.clear.links");
/* 1325 */           itemClearLinks.addListener(13, new ListenerDMTask(dms)
/*      */           {
/*      */             public void run(DownloadManager dm) {
/* 1328 */               if ((ManagerUtils.isStopped(dm)) && (dm.getDownloadState().getFileLinks().hasLinks()))
/*      */               {
/*      */ 
/* 1331 */                 DiskManagerFileInfoSet fis = dm.getDiskManagerFileInfoSet();
/*      */                 
/* 1333 */                 if (fis.nbFiles() > 1)
/*      */                 {
/* 1335 */                   DiskManagerFileInfo[] files = fis.getFiles();
/*      */                   
/* 1337 */                   for (DiskManagerFileInfo file_info : files)
/*      */                   {
/* 1339 */                     File file_link = file_info.getFile(true);
/* 1340 */                     File file_nolink = file_info.getFile(false);
/*      */                     
/* 1342 */                     if (!file_nolink.getAbsolutePath().equals(file_link.getAbsolutePath()))
/*      */                     {
/* 1344 */                       file_info.setLink(null);
/*      */                     }
/*      */                     
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 1351 */           });
/* 1352 */           itemClearLinks.setEnabled(hasClearableLinks);
/*      */           
/*      */ 
/* 1355 */           org.eclipse.swt.widgets.MenuItem itemFileClearAlloc = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1356 */           Messages.setLanguageText(itemFileClearAlloc, "MyTorrentsView.menu.clear_alloc_data");
/*      */           
/* 1358 */           itemFileClearAlloc.addListener(13, new ListenerDMTask(dms)
/*      */           {
/*      */             public void run(DownloadManager dm) {
/* 1361 */               dm.setDataAlreadyAllocated(false);
/*      */             }
/*      */             
/* 1364 */           });
/* 1365 */           itemFileClearAlloc.setEnabled(allStopped);
/*      */           
/*      */ 
/*      */ 
/* 1369 */           org.eclipse.swt.widgets.MenuItem itemFileClearResume = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1370 */           Messages.setLanguageText(itemFileClearResume, "MyTorrentsView.menu.clear_resume_data");
/*      */           
/* 1372 */           itemFileClearResume.addListener(13, new ListenerDMTask(dms)
/*      */           {
/*      */             public void run(DownloadManager dm) {
/* 1375 */               dm.getDownloadState().clearResumeData();
/*      */             }
/* 1377 */           });
/* 1378 */           itemFileClearResume.setEnabled(allStopped);
/*      */           
/*      */ 
/*      */ 
/* 1382 */           org.eclipse.swt.widgets.MenuItem itemFileSetResumeComplete = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1383 */           Messages.setLanguageText(itemFileSetResumeComplete, "MyTorrentsView.menu.set.resume.complete");
/*      */           
/* 1385 */           itemFileSetResumeComplete.addListener(13, new ListenerDMTask(dms) {
/*      */             public void run(DownloadManager dm) {
/* 1387 */               org.gudy.azureus2.core3.util.TorrentUtils.setResumeDataCompletelyValid(dm.getDownloadState());
/*      */             }
/* 1389 */           });
/* 1390 */           itemFileSetResumeComplete.setEnabled((allStopped) && (allResumeIncomplete));
/*      */           
/*      */ 
/*      */ 
/* 1394 */           if ((userMode > 1) && (TorrentMenuFancy.this.isSeedingView))
/*      */           {
/* 1396 */             boolean canSetSuperSeed = false;
/* 1397 */             boolean superSeedAllYes = true;
/* 1398 */             boolean superSeedAllNo = true;
/* 1399 */             for (DownloadManager dm : dms) {
/* 1400 */               PEPeerManager pm = dm.getPeerManager();
/*      */               
/* 1402 */               if (pm != null)
/*      */               {
/* 1404 */                 if (pm.canToggleSuperSeedMode())
/*      */                 {
/* 1406 */                   canSetSuperSeed = true;
/*      */                 }
/*      */                 
/* 1409 */                 if (pm.isSuperSeedMode())
/*      */                 {
/* 1411 */                   superSeedAllYes = false;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1415 */                   superSeedAllNo = false;
/*      */                 }
/*      */               } else {
/* 1418 */                 superSeedAllYes = false;
/* 1419 */                 superSeedAllNo = false;
/*      */               }
/*      */             }
/*      */             
/* 1423 */             org.eclipse.swt.widgets.MenuItem itemSuperSeed = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/*      */             
/* 1425 */             Messages.setLanguageText(itemSuperSeed, "ManagerItem.superseeding");
/*      */             
/*      */ 
/* 1428 */             boolean enabled = (canSetSuperSeed) && ((superSeedAllNo) || (superSeedAllYes));
/*      */             
/*      */ 
/* 1431 */             itemSuperSeed.setEnabled(enabled);
/*      */             
/* 1433 */             final boolean selected = superSeedAllNo;
/*      */             
/* 1435 */             if (enabled)
/*      */             {
/* 1437 */               itemSuperSeed.setSelection(selected);
/*      */               
/* 1439 */               itemSuperSeed.addListener(13, new ListenerDMTask(dms)
/*      */               {
/*      */                 public void run(DownloadManager dm) {
/* 1442 */                   PEPeerManager pm = dm.getPeerManager();
/*      */                   
/* 1444 */                   if (pm != null)
/*      */                   {
/* 1446 */                     if ((pm.isSuperSeedMode() == selected) && (pm.canToggleSuperSeedMode()))
/*      */                     {
/*      */ 
/* 1449 */                       pm.setSuperSeedMode(!selected);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   private void updateRowSpeed(FancyRowInfo row, boolean isUpload)
/*      */   {
/* 1463 */     int dlRate = isUpload ? this.dms[0].getStats().getUploadRateLimitBytesPerSecond() : this.dms[0].getStats().getDownloadRateLimitBytesPerSecond();
/*      */     
/*      */ 
/* 1466 */     for (DownloadManager dm : this.dms) {
/* 1467 */       int dlRate2 = isUpload ? dm.getStats().getUploadRateLimitBytesPerSecond() : dm.getStats().getDownloadRateLimitBytesPerSecond();
/*      */       
/* 1469 */       if (dlRate != dlRate2) {
/* 1470 */         dlRate = -2;
/* 1471 */         break;
/*      */       }
/*      */     }
/* 1474 */     if (dlRate != -2) { String currentSpeed;
/*      */       String currentSpeed;
/* 1476 */       if (dlRate == 0) {
/* 1477 */         currentSpeed = MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited"); } else { String currentSpeed;
/* 1478 */         if (dlRate < 0) {
/* 1479 */           currentSpeed = MessageText.getString("MyTorrentsView.menu.setSpeed.disabled");
/*      */         } else
/* 1481 */           currentSpeed = org.gudy.azureus2.core3.util.DisplayFormatters.formatByteCountToKiBEtcPerSec(dlRate);
/*      */       }
/* 1483 */       row.setRightLabelText(currentSpeed);
/* 1484 */       row.cRow.layout();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private FancyMenuRowInfo createMenuRow(Composite cParent, String keyTitle, String keyImage, final FancyMenuRowInfoListener listener)
/*      */   {
/* 1491 */     Listener showSWTMenuListener = new Listener() {
/* 1492 */       int lastX = 0;
/*      */       
/* 1494 */       int lastY = 0;
/*      */       
/*      */       public void handleEvent(final Event event) {
/* 1497 */         if ((event.type == 32) && (this.lastX == event.x) && (this.lastY == event.y))
/*      */         {
/* 1499 */           return;
/*      */         }
/* 1501 */         this.lastX = event.x;
/* 1502 */         this.lastY = event.y;
/*      */         
/*      */ 
/*      */ 
/* 1506 */         TorrentMenuFancy.FancyRowInfo findRowInfo = TorrentMenuFancy.this.findRowInfo(event.widget);
/* 1507 */         if (!(findRowInfo instanceof TorrentMenuFancy.FancyMenuRowInfo)) {
/* 1508 */           return;
/*      */         }
/*      */         
/* 1511 */         TorrentMenuFancy.FancyMenuRowInfo rowInfo = (TorrentMenuFancy.FancyMenuRowInfo)findRowInfo;
/* 1512 */         TorrentMenuFancy.this.currentMenu = rowInfo.getMenu();
/* 1513 */         if ((TorrentMenuFancy.this.currentMenu != null) && (!TorrentMenuFancy.this.currentMenu.isDisposed())) {
/* 1514 */           return;
/*      */         }
/*      */         
/* 1517 */         TorrentMenuFancy.this.currentMenu = new Menu(TorrentMenuFancy.this.parentShell, 8);
/* 1518 */         rowInfo.setMenu(TorrentMenuFancy.this.currentMenu);
/*      */         
/* 1520 */         TorrentMenuFancy.this.currentMenu.addMenuListener(new MenuListener()
/*      */         {
/*      */           public void menuShown(MenuEvent arg0) {
/* 1523 */             TorrentMenuFancy.this.subMenuVisible = true;
/*      */           }
/*      */           
/*      */           public void menuHidden(final MenuEvent arg0) {
/* 1527 */             TorrentMenuFancy.this.subMenuVisible = false;
/* 1528 */             TorrentMenuFancy.this.currentMenu = null;
/* 1529 */             Utils.execSWTThreadLater(0, new Runnable()
/*      */             {
/*      */               public void run() {
/* 1532 */                 arg0.widget.dispose();
/*      */               }
/*      */             });
/*      */           }
/* 1536 */         });
/* 1537 */         listener.buildMenu(TorrentMenuFancy.this.currentMenu);
/*      */         
/* 1539 */         Composite rowComposite = rowInfo.getRow();
/*      */         
/* 1541 */         if (rowComposite != null) {
/* 1542 */           Point size = rowComposite.getSize();
/* 1543 */           Point menuLocation = rowComposite.toDisplay(size.x - 3, -3);
/* 1544 */           TorrentMenuFancy.this.currentMenu.setLocation(menuLocation);
/*      */         }
/* 1546 */         if (TorrentMenuFancy.this.currentMenu.getItemCount() > 0) {
/* 1547 */           TorrentMenuFancy.this.currentMenu.setVisible(true);
/*      */           
/* 1549 */           TorrentMenuFancy.this.addMenuItemListener(TorrentMenuFancy.this.currentMenu, TorrentMenuFancy.this.listenerForTrigger);
/*      */           
/* 1551 */           final TorrentMenuFancy.FancyMenuRowInfo currentRow = rowInfo;
/* 1552 */           final Point currentMousePos = event.display.getCursorLocation();
/*      */           
/* 1554 */           Utils.execSWTThreadLater(300, new Runnable() {
/*      */             public void run() {
/* 1556 */               Point cursorLocation = event.display.getCursorLocation();
/* 1557 */               if (currentMousePos.equals(cursorLocation)) {
/* 1558 */                 Utils.execSWTThreadLater(300, this);
/* 1559 */                 return;
/*      */               }
/*      */               
/* 1562 */               Control control = Utils.getCursorControl();
/*      */               
/* 1564 */               if (control != null) {
/* 1565 */                 Object data = control.getData("ID");
/* 1566 */                 if ((data instanceof TorrentMenuFancy.HeaderInfo)) {
/* 1567 */                   TorrentMenuFancy.HeaderInfo header = (TorrentMenuFancy.HeaderInfo)data;
/* 1568 */                   TorrentMenuFancy.this.activateHeader(header);
/*      */                 }
/*      */               }
/*      */               
/* 1572 */               Menu submenu = currentRow.getMenu();
/* 1573 */               if ((submenu == null) || (submenu.isDisposed()) || (!submenu.isVisible()))
/*      */               {
/* 1575 */                 return;
/*      */               }
/* 1577 */               TorrentMenuFancy.FancyRowInfo rowInfo = TorrentMenuFancy.this.findRowInfo(control);
/* 1578 */               if ((rowInfo != null) && (rowInfo != currentRow)) {
/* 1579 */                 submenu.setVisible(false);
/* 1580 */                 return;
/*      */               }
/* 1582 */               Utils.execSWTThreadLater(300, this);
/*      */             }
/*      */           });
/*      */         } else {
/* 1586 */           TorrentMenuFancy.this.currentMenu.dispose();
/* 1587 */           TorrentMenuFancy.this.currentMenu = null;
/*      */         }
/*      */         
/*      */       }
/* 1591 */     };
/* 1592 */     FancyMenuRowInfo row = new FancyMenuRowInfo(null);
/* 1593 */     createRow(cParent, keyTitle, keyImage, true, showSWTMenuListener, row);
/* 1594 */     row.setHasSubMenu(true);
/*      */     
/* 1596 */     Composite cRow = row.getRow();
/* 1597 */     Utils.addListenerAndChildren(cRow, 32, showSWTMenuListener);
/*      */     
/* 1599 */     row.setKeepMenu(true);
/*      */     
/* 1601 */     Label rightLabel = row.getRightLabel();
/* 1602 */     GridData gd = new GridData(12, -1);
/* 1603 */     Utils.setLayoutData(rightLabel, gd);
/* 1604 */     row.getRightLabel().addPaintListener(this.paintListenerArrow);
/*      */     
/* 1606 */     return row;
/*      */   }
/*      */   
/*      */   protected void addMenuItemListener(Menu menu, Listener l) {
/* 1610 */     for (org.eclipse.swt.widgets.MenuItem item : menu.getItems()) {
/* 1611 */       if (item.getStyle() == 64) {
/* 1612 */         addMenuItemListener(item.getMenu(), l);
/*      */       } else {
/* 1614 */         item.addListener(13, l);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private FancyRowInfo createRow(Composite cParent, String keyTitle, String keyImage, Listener triggerListener)
/*      */   {
/* 1621 */     return createRow(cParent, keyTitle, keyImage, true, triggerListener, new FancyRowInfo(null));
/*      */   }
/*      */   
/*      */ 
/*      */   private FancyRowInfo createRow(Composite cParent, String keyTitle, String keyImage, boolean triggerOnUp, Listener triggerListener)
/*      */   {
/* 1627 */     return createRow(cParent, keyTitle, keyImage, triggerOnUp, triggerListener, new FancyRowInfo(null));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private FancyRowInfo createRow(Composite cParent, String keyTitle, String keyImage, boolean triggerOnUp, Listener triggerListener, FancyRowInfo rowInfo)
/*      */   {
/* 1635 */     Composite cRow = new Composite(cParent, 0);
/*      */     
/*      */ 
/* 1638 */     cRow.setData("ID", rowInfo);
/* 1639 */     GridLayout gridLayout = new GridLayout(4, false);
/* 1640 */     gridLayout.marginWidth = Utils.adjustPXForDPI(1);
/* 1641 */     gridLayout.marginHeight = Utils.adjustPXForDPI(3);
/* 1642 */     gridLayout.marginRight = Utils.adjustPXForDPI(4);
/* 1643 */     gridLayout.horizontalSpacing = 0;
/* 1644 */     gridLayout.verticalSpacing = 0;
/* 1645 */     cRow.setLayout(gridLayout);
/*      */     
/*      */ 
/*      */ 
/* 1649 */     Label lblIcon = new Label(cRow, 16777216);
/* 1650 */     GridData gridData = new GridData();
/* 1651 */     gridData.widthHint = 20;
/* 1652 */     Utils.setLayoutData(lblIcon, gridData);
/* 1653 */     if (keyImage != null) {
/* 1654 */       ImageLoader.getInstance().setLabelImage(lblIcon, keyImage);
/*      */     }
/*      */     
/* 1657 */     Label item = new Label(cRow, 0);
/* 1658 */     gridData = new GridData();
/* 1659 */     gridData.grabExcessHorizontalSpace = true;
/* 1660 */     gridData.horizontalIndent = 2;
/* 1661 */     Utils.setLayoutData(item, gridData);
/* 1662 */     Messages.setLanguageText(item, keyTitle);
/*      */     
/* 1664 */     Label lblCheck = new Label(cRow, 16777216);
/* 1665 */     gridData = new GridData();
/* 1666 */     gridData.widthHint = 13;
/* 1667 */     Utils.setLayoutData(lblCheck, gridData);
/*      */     
/* 1669 */     if (triggerListener != null) {
/* 1670 */       Utils.addListenerAndChildren(cRow, triggerOnUp ? 4 : 3, this.listenerForTrigger);
/*      */     }
/*      */     
/*      */ 
/* 1674 */     Utils.addListenerAndChildren(cRow, 6, this.listenerRow);
/* 1675 */     Utils.addListenerAndChildren(cRow, 7, this.listenerRow);
/*      */     
/* 1677 */     cRow.addPaintListener(this.listenerRowPaint);
/*      */     
/* 1679 */     rowInfo.setListener(triggerListener);
/* 1680 */     rowInfo.setRow(cRow);
/* 1681 */     rowInfo.setIconLabel(lblIcon);
/* 1682 */     rowInfo.setText(item);
/* 1683 */     rowInfo.setRightLabel(null);
/* 1684 */     rowInfo.setCheckLabel(lblCheck);
/*      */     
/* 1686 */     this.listRowInfos.add(rowInfo);
/* 1687 */     return rowInfo;
/*      */   }
/*      */   
/*      */   private FancyRowInfo findRowInfo(Widget widget) {
/* 1691 */     Object findData = findData(widget, "ID");
/* 1692 */     if ((findData instanceof FancyRowInfo)) {
/* 1693 */       return (FancyRowInfo)findData;
/*      */     }
/*      */     
/* 1696 */     return null;
/*      */   }
/*      */   
/*      */   protected Object findData(Widget widget, String id) {
/* 1700 */     if ((widget == null) || (widget.isDisposed())) {
/* 1701 */       return null;
/*      */     }
/* 1703 */     Object o = widget.getData(id);
/* 1704 */     if (o != null) {
/* 1705 */       return o;
/*      */     }
/* 1707 */     if ((widget instanceof Control)) {
/* 1708 */       Control control = ((Control)widget).getParent();
/* 1709 */       while (control != null) {
/* 1710 */         o = control.getData(id);
/* 1711 */         if (o != null) {
/* 1712 */           return o;
/*      */         }
/* 1714 */         control = control.getParent();
/*      */       }
/*      */     }
/* 1717 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private Control createActionButton(DownloadManager[] dms, Composite cParent, String keyToolTip, String keyImage, boolean enable, Listener listener)
/*      */   {
/* 1723 */     return createActionButton(dms, cParent, keyToolTip, keyImage, enable, 2048, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Control createActionButton(DownloadManager[] dms, Composite cParent, String keyToolTip, final String keyImage, boolean enable, int style, final Listener listener)
/*      */   {
/* 1730 */     Canvas item = new Canvas(cParent, 537133056);
/*      */     
/*      */ 
/* 1733 */     Listener l = new Listener() {
/*      */       private boolean inWidget;
/*      */       
/*      */       public void handleEvent(Event e) {
/* 1737 */         Control c = (Control)e.widget;
/* 1738 */         if (e.type == 9) {
/* 1739 */           Point size = c.getSize();
/* 1740 */           if (this.inWidget) {
/* 1741 */             e.gc.setBackground(e.display.getSystemColor(20));
/*      */           } else {
/* 1743 */             e.gc.setBackground(e.display.getSystemColor(19));
/*      */           }
/* 1745 */           e.gc.setAdvanced(true);
/* 1746 */           e.gc.setAntialias(1);
/* 1747 */           e.gc.fillRoundRectangle(0, 0, size.x - 1, size.y - 1, 6, 6);
/* 1748 */           e.gc.setForeground(e.display.getSystemColor(17));
/* 1749 */           e.gc.drawRoundRectangle(0, 0, size.x - 1, size.y - 1, 6, 6);
/* 1750 */           e.gc.setForeground(e.display.getSystemColor(20));
/* 1751 */           e.gc.drawRoundRectangle(1, 1, size.x - 3, size.y - 3, 6, 6);
/*      */           
/* 1753 */           Image image = ImageLoader.getInstance().getImage(keyImage + "-disabled");
/*      */           
/* 1755 */           Rectangle bounds = image.getBounds();
/* 1756 */           int x = size.x / 2 - bounds.width / 2;
/* 1757 */           int y = size.y / 2 - bounds.height / 2;
/*      */           
/* 1759 */           e.gc.drawImage(image, x, y);
/* 1760 */         } else if (e.type == 6) {
/* 1761 */           this.inWidget = true;
/* 1762 */           c.redraw();
/* 1763 */         } else if (e.type == 7) {
/* 1764 */           this.inWidget = false;
/* 1765 */           c.redraw();
/*      */         }
/*      */         
/*      */       }
/* 1769 */     };
/* 1770 */     item.addListener(6, l);
/* 1771 */     item.addListener(7, l);
/* 1772 */     item.addListener(9, l);
/*      */     
/* 1774 */     Messages.setLanguageTooltip(item, keyToolTip);
/* 1775 */     item.addListener(4, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1777 */         listener.handleEvent(event);
/* 1778 */         TorrentMenuFancy.this.shell.dispose();
/*      */       }
/* 1780 */     });
/* 1781 */     item.setEnabled(enable);
/*      */     
/* 1783 */     RowData rowData = new RowData(30, 21);
/* 1784 */     Utils.setLayoutData(item, rowData);
/*      */     
/* 1786 */     return item;
/*      */   }
/*      */   
/*      */ 
/*      */   public void buildTorrentCustomMenu_Organize(final Composite detailArea, final DownloadManager[] dms)
/*      */   {
/* 1792 */     if (!this.hasSelection) {
/* 1793 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1798 */     createMenuRow(detailArea, "label.tags", "image.sidebar.tag-overview", new FancyMenuRowInfoListener()
/*      */     {
/*      */       public void buildMenu(Menu menu) {
/* 1801 */         org.gudy.azureus2.ui.swt.views.utils.TagUIUtils.addLibraryViewTagsSubMenu(dms, menu, detailArea);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 1806 */     });
/* 1807 */     createMenuRow(detailArea, "MyTorrentsView.menu.setCategory", "image.sidebar.library", new FancyMenuRowInfoListener()
/*      */     {
/*      */       public void buildMenu(Menu menu) {
/* 1810 */         TorrentUtil.addCategorySubMenu(dms, menu, detailArea);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 1815 */     });
/* 1816 */     final List<Download> ar_dms = new ArrayList();
/*      */     
/* 1818 */     for (DownloadManager dm : dms)
/*      */     {
/* 1820 */       Download stub = org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils.wrap(dm);
/*      */       
/* 1822 */       if (stub.canStubbify())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1827 */         ar_dms.add(stub);
/*      */       }
/*      */     }
/* 1830 */     if (ar_dms.size() > 0)
/*      */     {
/* 1832 */       createRow(detailArea, "MyTorrentsView.menu.archive", "image.sidebar.archive", new Listener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void handleEvent(Event event)
/*      */         {
/*      */ 
/* 1839 */           ManagerUtils.moveToArchive(ar_dms, null);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1847 */     createRow(detailArea, "MyTorrentsView.menu.rename", null, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1849 */         for (DownloadManager dm : dms) {
/* 1850 */           AdvRenameWindow window = new AdvRenameWindow();
/* 1851 */           window.open(dm);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 1857 */     });
/* 1858 */     createRow(detailArea, "MyTorrentsView.menu.reposition.manual", null, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 1861 */         TorrentUtil.repositionManual(TorrentMenuFancy.this.tv, dms, TorrentMenuFancy.this.parentShell, TorrentMenuFancy.this.isSeedingView);
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*      */ 
/* 1867 */     if (this.tv.getSWTFilter() != null) {
/* 1868 */       createRow(detailArea, "MyTorrentsView.menu.filter", null, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 1870 */           TorrentMenuFancy.this.tv.openFilterDialog();
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void buildTorrentCustomMenu_Social(Composite detailArea)
/*      */   {
/* 1879 */     boolean isTrackerOn = org.gudy.azureus2.core3.tracker.util.TRTrackerUtils.isTrackerEnabled();
/* 1880 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/* 1882 */     if (this.hasSelection) {
/* 1883 */       createMenuRow(detailArea, "ConfigView.section.interface.alerts", null, new FancyMenuRowInfoListener()
/*      */       {
/*      */         public void buildMenu(Menu menu) {
/* 1886 */           org.gudy.azureus2.ui.swt.mainwindow.MenuFactory.addAlertsMenu(menu, false, TorrentMenuFancy.this.dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1891 */     if ((userMode > 0) && (isTrackerOn) && (this.hasSelection))
/*      */     {
/* 1893 */       createRow(detailArea, "MyTorrentsView.menu.host", "host", new Listener() {
/*      */         public void handleEvent(Event event) {
/* 1895 */           TorrentUtil.hostTorrents(TorrentMenuFancy.this.dms);
/*      */         }
/*      */         
/*      */ 
/* 1899 */       });
/* 1900 */       createRow(detailArea, "MyTorrentsView.menu.publish", "publish", new Listener()
/*      */       {
/*      */         public void handleEvent(Event event) {
/* 1903 */           TorrentUtil.publishTorrents(TorrentMenuFancy.this.dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1908 */     if (userMode > 0)
/*      */     {
/* 1910 */       if (this.dms.length == 1) {
/* 1911 */         String title = MessageText.getString("MyTorrentsView.menu.exportmenu") + ": " + MessageText.getString("MyTorrentsView.menu.export");
/*      */         
/* 1913 */         FancyRowInfo row = createRow(detailArea, null, null, new ListenerDMTask(this.dms)
/*      */         {
/*      */           public void run(DownloadManager dm) {
/* 1916 */             if (dm != null) {
/* 1917 */               new org.gudy.azureus2.ui.swt.exporttorrent.wizard.ExportTorrentWizard(TorrentMenuFancy.this.parentShell.getDisplay(), dm);
/*      */             }
/*      */           }
/* 1920 */         });
/* 1921 */         row.getText().setText(title);
/*      */       }
/*      */       
/*      */ 
/* 1925 */       String title = MessageText.getString("MyTorrentsView.menu.exportmenu") + ": " + MessageText.getString("MyTorrentsView.menu.exporttorrent");
/*      */       
/* 1927 */       FancyRowInfo row = createRow(detailArea, null, null, new ListenerDMTask(this.dms)
/*      */       {
/*      */         public void run(DownloadManager[] dms) {
/* 1930 */           TorrentUtil.exportTorrent(dms, TorrentMenuFancy.this.parentShell);
/*      */         }
/* 1932 */       });
/* 1933 */       row.getText().setText(title);
/*      */       
/*      */ 
/* 1936 */       createRow(detailArea, "MyTorrentsView.menu.exporthttpseeds", null, new ListenerDMTask(this.dms)
/*      */       {
/*      */         public void run(DownloadManager[] dms) {
/* 1939 */           TorrentUtil.exportHTTPSeeds(dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 1945 */     if (this.isSeedingView) {
/* 1946 */       createRow(detailArea, "MyTorrentsView.menu.create_personal_share", null, new ListenerDMTask(this.dms, false)
/*      */       {
/*      */         public void run(DownloadManager dm) {
/* 1949 */           File file = dm.getSaveLocation();
/*      */           
/* 1951 */           Map<String, String> properties = new HashMap();
/*      */           
/* 1953 */           properties.put("personal", "true");
/*      */           
/* 1955 */           if (file.isFile())
/*      */           {
/* 1957 */             ShareUtils.shareFile(file.getAbsolutePath(), properties);
/*      */           }
/* 1959 */           else if (file.isDirectory())
/*      */           {
/* 1961 */             ShareUtils.shareDir(file.getAbsolutePath(), properties);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTableItemsWithID(Composite detailArea, String menuID, String[] ids)
/*      */   {
/* 1972 */     TableContextMenuItem[] items = TableContextMenuManager.getInstance().getAllAsArray(menuID);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1978 */     addItemsArray(detailArea, items, ids);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addMenuItemsWithID(Composite detailArea, String menuID, String[] ids)
/*      */   {
/* 1985 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] items = MenuItemManager.getInstance().getAllAsArray(menuID);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1991 */     addItemsArray(detailArea, items, ids);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addItemsArray(Composite detailArea, org.gudy.azureus2.plugins.ui.menus.MenuItem[] items, String[] onlyIDs)
/*      */   {
/* 1997 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/* 1999 */     for (int i = 0; i < onlyIDs.length; i++) {
/* 2000 */       String id = onlyIDs[i];
/*      */       
/*      */ 
/*      */ 
/* 2004 */       Integer requiredUserMode = (Integer)this.mapMovedPluginMenuUserMode.get(id);
/* 2005 */       if ((requiredUserMode == null) || (userMode >= requiredUserMode.intValue()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2013 */         for (org.gudy.azureus2.plugins.ui.menus.MenuItem item : items) {
/* 2014 */           String key = item.getResourceKey();
/* 2015 */           if (id.equals(key))
/*      */           {
/*      */ 
/*      */ 
/* 2019 */             addPluginItem(detailArea, item);
/*      */             
/*      */ 
/*      */ 
/* 2023 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void buildTorrentCustomMenu_Other(Composite detailArea, List<org.gudy.azureus2.plugins.ui.menus.MenuItem> items) {
/* 2031 */     for (org.gudy.azureus2.plugins.ui.menus.MenuItem item : items)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2038 */       addPluginItem(detailArea, item);
/*      */     }
/*      */   }
/*      */   
/*      */   private Object[] getTarget(org.gudy.azureus2.plugins.ui.menus.MenuItem item)
/*      */   {
/* 2044 */     if ("table".equals(item.getMenuID())) {
/* 2045 */       return this.tv.getSelectedRows();
/*      */     }
/* 2047 */     Object[] dataSources = this.tv.getSelectedDataSources(false);
/* 2048 */     Download[] downloads = new Download[dataSources.length];
/* 2049 */     System.arraycopy(dataSources, 0, downloads, 0, dataSources.length);
/* 2050 */     return downloads;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void addPluginItem(Composite detailArea, final org.gudy.azureus2.plugins.ui.menus.MenuItem item)
/*      */   {
/* 2057 */     MenuItemImpl menuImpl = (MenuItemImpl)item;
/* 2058 */     menuImpl.invokeMenuWillBeShownListeners(getTarget(item));
/*      */     
/* 2060 */     if (!item.isVisible())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2065 */       return;
/*      */     }
/*      */     
/* 2068 */     Graphic graphic = item.getGraphic();
/*      */     
/*      */ 
/*      */     FancyRowInfo row;
/*      */     
/*      */     FancyRowInfo row;
/*      */     
/* 2075 */     if (item.getStyle() == 5)
/*      */     {
/* 2077 */       row = createMenuRow(detailArea, item.getResourceKey(), null, new FancyMenuRowInfoListener()
/*      */       {
/*      */         public void buildMenu(Menu menu) {
/* 2080 */           if (TorrentMenuFancy.this.dms.length != 0) {
/* 2081 */             MenuBuilder submenuBuilder = ((MenuItemImpl)item).getSubmenuBuilder();
/* 2082 */             if (submenuBuilder != null) {
/*      */               try {
/* 2084 */                 item.removeAllChildItems();
/* 2085 */                 submenuBuilder.buildSubmenu(item, TorrentMenuFancy.this.getTarget(item));
/*      */               } catch (Throwable t) {
/* 2087 */                 Debug.out(t);
/*      */               }
/*      */             }
/*      */             
/* 2091 */             MenuBuildUtils.addPluginMenuItems(item.getItems(), menu, false, true, new MenuBuildUtils.PluginMenuController()
/*      */             {
/*      */ 
/*      */               public Listener makeSelectionListener(final org.gudy.azureus2.plugins.ui.menus.MenuItem plugin_menu_item)
/*      */               {
/* 2096 */                 new TableSelectedRowsListener(TorrentMenuFancy.this.tv, false) {
/*      */                   public boolean run(TableRowCore[] rows) {
/* 2098 */                     if (rows.length != 0) {
/* 2099 */                       ((MenuItemImpl)plugin_menu_item).invokeListenersMulti(TorrentMenuFancy.this.getTarget(TorrentMenuFancy.52.this.val$item));
/*      */                     }
/* 2101 */                     return true;
/*      */                   }
/*      */                 };
/*      */               }
/*      */               
/*      */               public void notifyFillListeners(org.gudy.azureus2.plugins.ui.menus.MenuItem menu_item)
/*      */               {
/* 2108 */                 ((MenuItemImpl)menu_item).invokeMenuWillBeShownListeners(TorrentMenuFancy.this.getTarget(TorrentMenuFancy.52.this.val$item));
/*      */               }
/*      */               
/*      */ 
/*      */               public void buildSubmenu(org.gudy.azureus2.plugins.ui.menus.MenuItem parent)
/*      */               {
/* 2114 */                 MenuBuilder submenuBuilder = ((MenuItemImpl)parent).getSubmenuBuilder();
/* 2115 */                 if (submenuBuilder != null) {
/*      */                   try {
/* 2117 */                     parent.removeAllChildItems();
/* 2118 */                     submenuBuilder.buildSubmenu(parent, TorrentMenuFancy.this.getTarget(TorrentMenuFancy.52.this.val$item));
/*      */                   } catch (Throwable t) {
/* 2120 */                     Debug.out(t);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       });
/*      */     } else {
/* 2130 */       row = createRow(detailArea, item.getResourceKey(), null, new TableSelectedRowsListener(this.tv, false)
/*      */       {
/*      */         public boolean run(TableRowCore[] rows)
/*      */         {
/* 2134 */           if (rows.length != 0) {
/* 2135 */             ((MenuItemImpl)item).invokeListenersMulti(TorrentMenuFancy.this.getTarget(item));
/*      */           }
/* 2137 */           return true;
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 2143 */     row.setEnabled(item.isEnabled());
/* 2144 */     if ((graphic instanceof UISWTGraphic)) {
/* 2145 */       row.getIconLabel().setImage(((UISWTGraphic)graphic).getImage());
/* 2146 */     } else if ((graphic instanceof GraphicURI)) {
/* 2147 */       ImageLoader.getInstance().setLabelImage(row.getIconLabel(), ((GraphicURI)graphic).getURI().toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void buildTorrentCustomMenu_Content(Composite detailArea, final DownloadManager[] dms)
/*      */   {
/* 2156 */     if (this.hasSelection) {
/* 2157 */       createRow(detailArea, "MyTorrentsView.menu.open", "run", new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager[] dms) {
/* 2160 */           TorrentUtil.runDataSources(dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 2166 */     if (this.hasSelection) {
/* 2167 */       final boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/* 2168 */       createRow(detailArea, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"), null, new ListenerDMTask(dms, false)
/*      */       {
/*      */         public void run(DownloadManager dm)
/*      */         {
/* 2172 */           ManagerUtils.open(dm, use_open_containing_folder);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2179 */     if (this.hasSelection) {
/* 2180 */       createMenuRow(detailArea, "MyTorrentsView.menu.browse", null, new FancyMenuRowInfoListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void buildMenu(Menu menuBrowse)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2190 */           org.eclipse.swt.widgets.MenuItem itemBrowsePublic = new org.eclipse.swt.widgets.MenuItem(menuBrowse, 8);
/* 2191 */           itemBrowsePublic.setText(MessageText.getString("label.public") + "...");
/* 2192 */           itemBrowsePublic.addListener(13, new ListenerDMTask(dms, false)
/*      */           {
/*      */             public void run(DownloadManager dm)
/*      */             {
/* 2196 */               ManagerUtils.browse(dm, false, true);
/*      */             }
/*      */             
/* 2199 */           });
/* 2200 */           org.eclipse.swt.widgets.MenuItem itemBrowseAnon = new org.eclipse.swt.widgets.MenuItem(menuBrowse, 8);
/* 2201 */           itemBrowseAnon.setText(MessageText.getString("label.anon") + "...");
/* 2202 */           itemBrowseAnon.addListener(13, new ListenerDMTask(dms, false)
/*      */           {
/*      */             public void run(DownloadManager dm)
/*      */             {
/* 2206 */               ManagerUtils.browse(dm, true, true);
/*      */             }
/*      */             
/* 2209 */           });
/* 2210 */           new org.eclipse.swt.widgets.MenuItem(menuBrowse, 2);
/*      */           
/* 2212 */           org.eclipse.swt.widgets.MenuItem itemBrowseURL = new org.eclipse.swt.widgets.MenuItem(menuBrowse, 8);
/* 2213 */           Messages.setLanguageText(itemBrowseURL, "label.copy.url.to.clip");
/* 2214 */           itemBrowseURL.addListener(13, new Listener() {
/*      */             public void handleEvent(Event event) {
/* 2216 */               Utils.getOffOfSWTThread(new AERunnable()
/*      */               {
/*      */                 public void runSupport()
/*      */                 {
/* 2220 */                   String url = ManagerUtils.browse(TorrentMenuFancy.56.this.val$dms[0], true, false);
/* 2221 */                   if (url != null) {
/* 2222 */                     org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipBoard(url);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/* 2227 */           });
/* 2228 */           itemBrowseURL.setEnabled(dms.length == 1);
/*      */           
/* 2230 */           new org.eclipse.swt.widgets.MenuItem(menuBrowse, 2);
/*      */           
/* 2232 */           final org.eclipse.swt.widgets.MenuItem itemBrowseDir = new org.eclipse.swt.widgets.MenuItem(menuBrowse, 32);
/* 2233 */           Messages.setLanguageText(itemBrowseDir, "library.launch.web.in.browser.dir.list");
/* 2234 */           itemBrowseDir.setSelection(COConfigurationManager.getBooleanParameter("Library.LaunchWebsiteInBrowserDirList"));
/* 2235 */           itemBrowseDir.addListener(13, new Listener() {
/*      */             public void handleEvent(Event event) {
/* 2237 */               COConfigurationManager.setParameter("Library.LaunchWebsiteInBrowserDirList", itemBrowseDir.getSelection());
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2246 */     createRow(detailArea, "MyTorrentsView.menu.torrent.set.thumb", null, new ListenerDMTask(dms)
/*      */     {
/*      */       public void run(DownloadManager[] dms) {
/* 2249 */         FileDialog fDialog = new FileDialog(TorrentMenuFancy.this.parentShell, 4098);
/*      */         
/* 2251 */         fDialog.setText(MessageText.getString("MainWindow.dialog.choose.thumb"));
/* 2252 */         String path = fDialog.open();
/* 2253 */         if (path == null) {
/* 2254 */           return;
/*      */         }
/* 2256 */         File file = new File(path);
/*      */         try
/*      */         {
/* 2259 */           byte[] thumbnail = FileUtil.readFileAsByteArray(file);
/*      */           
/* 2261 */           String name = file.getName();
/*      */           
/* 2263 */           int pos = name.lastIndexOf(".");
/*      */           
/*      */           String ext;
/*      */           String ext;
/* 2267 */           if (pos != -1)
/*      */           {
/* 2269 */             ext = name.substring(pos + 1);
/*      */           }
/*      */           else
/*      */           {
/* 2273 */             ext = "";
/*      */           }
/*      */           
/* 2276 */           String type = HTTPUtils.guessContentTypeFromFileType(ext);
/*      */           
/* 2278 */           for (DownloadManager dm : dms) {
/*      */             try
/*      */             {
/* 2281 */               TOTorrent torrent = dm.getTorrent();
/*      */               
/* 2283 */               PlatformTorrentUtils.setContentThumbnail(torrent, thumbnail, type);
/*      */ 
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2291 */           Debug.out(e);
/*      */         }
/*      */         
/*      */       }
/* 2295 */     });
/* 2296 */     boolean fileMove = true;
/* 2297 */     boolean locateFiles = false;
/*      */     
/* 2299 */     for (int i = 0; i < dms.length; i++) {
/* 2300 */       DownloadManager dm = dms[i];
/* 2301 */       if (!dm.canMoveDataFiles()) {
/* 2302 */         fileMove = false;
/*      */       }
/* 2304 */       if (!dm.isDownloadComplete(false)) {
/* 2305 */         locateFiles = true;
/*      */       }
/*      */     }
/* 2308 */     if (fileMove) {
/* 2309 */       createRow(detailArea, "MyTorrentsView.menu.movedata", null, new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager[] dms) {
/* 2312 */           TorrentUtil.moveDataFiles(TorrentMenuFancy.this.parentShell, dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 2317 */     createRow(detailArea, "MyTorrentsView.menu.checkfilesexist", null, new ListenerDMTask(dms)
/*      */     {
/*      */       public void run(DownloadManager dm) {
/* 2320 */         dm.filesExist(true);
/*      */       }
/*      */     });
/*      */     
/* 2324 */     if (locateFiles) {
/* 2325 */       createRow(detailArea, "MyTorrentsView.menu.locatefiles", null, new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager[] dms) {
/* 2328 */           ManagerUtils.locateFiles(dms, TorrentMenuFancy.this.parentShell);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 2333 */     if ((dms.length == 1) && (ManagerUtils.canFindMoreLikeThis())) {
/* 2334 */       createRow(detailArea, "MyTorrentsView.menu.findmorelikethis", null, new ListenerDMTask(dms)
/*      */       {
/*      */         public void run(DownloadManager[] dms) {
/* 2337 */           ManagerUtils.findMoreLikeThis(dms[0], TorrentMenuFancy.this.parentShell);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 2342 */     createRow(detailArea, "MyTorrentsView.menu.thisColumn.toClipboard", null, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 2345 */         String sToClipboard = "";
/* 2346 */         if (TorrentMenuFancy.this.column == null) {
/* 2347 */           return;
/*      */         }
/* 2349 */         String columnName = TorrentMenuFancy.this.column.getName();
/* 2350 */         if (columnName == null) {
/* 2351 */           return;
/*      */         }
/* 2353 */         TableRowCore[] rows = TorrentMenuFancy.this.tv.getSelectedRows();
/* 2354 */         for (TableRowCore row : rows) {
/* 2355 */           if (row != rows[0]) {
/* 2356 */             sToClipboard = sToClipboard + "\n";
/*      */           }
/* 2358 */           TableCellCore cell = row.getTableCellCore(columnName);
/* 2359 */           if (cell != null) {
/* 2360 */             sToClipboard = sToClipboard + cell.getClipboardText();
/*      */           }
/*      */         }
/* 2363 */         if (sToClipboard.length() == 0) {
/* 2364 */           return;
/*      */         }
/* 2366 */         new Clipboard(Display.getDefault()).setContents(new Object[] { sToClipboard }, new org.eclipse.swt.dnd.Transfer[] { org.eclipse.swt.dnd.TextTransfer.getInstance() });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private HeaderInfo addHeader(String id, String title, AERunnable runnable)
/*      */   {
/* 2376 */     Composite composite = new Composite(this.topArea, 0);
/*      */     
/* 2378 */     HeaderInfo headerInfo = new HeaderInfo(id, runnable, composite);
/*      */     
/* 2380 */     composite.setBackgroundMode(2);
/* 2381 */     FillLayout fillLayout = new FillLayout();
/* 2382 */     fillLayout.marginWidth = 6;
/* 2383 */     fillLayout.marginHeight = 2;
/* 2384 */     composite.setLayout(fillLayout);
/* 2385 */     Display d = composite.getDisplay();
/* 2386 */     composite.setBackground(d.getSystemColor(25));
/* 2387 */     composite.setForeground(d.getSystemColor(24));
/*      */     
/* 2389 */     Label control = new Label(composite, 0);
/* 2390 */     Messages.setLanguageText(control, title);
/* 2391 */     control.setData("ID", headerInfo);
/*      */     
/* 2393 */     control.addListener(6, this.headerListener);
/* 2394 */     control.addListener(47, this.headerListener);
/* 2395 */     control.addListener(7, this.headerListener);
/* 2396 */     control.addListener(9, this.headerListener);
/*      */     
/* 2398 */     this.listHeaders.add(headerInfo);
/* 2399 */     return headerInfo;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/TorrentMenuFancy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */