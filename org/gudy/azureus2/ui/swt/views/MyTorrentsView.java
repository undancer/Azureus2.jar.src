/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.TagTypeListener.TagEvent;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableCountChangeListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableExpansionChangeListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck.TableViewFilterCheckEx;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableViewImpl;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*      */ import java.io.File;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.SWT;
/*      */ import org.eclipse.swt.dnd.DragSource;
/*      */ import org.eclipse.swt.dnd.DragSourceAdapter;
/*      */ import org.eclipse.swt.dnd.DragSourceEvent;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.dnd.DropTargetAdapter;
/*      */ import org.eclipse.swt.dnd.DropTargetEvent;
/*      */ import org.eclipse.swt.dnd.DropTargetListener;
/*      */ import org.eclipse.swt.dnd.TextTransfer;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.layout.RowData;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.eclipse.swt.widgets.Widget;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerEvent;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerEventListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*      */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.TorrentMenuFancy;
/*      */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*      */ import org.gudy.azureus2.ui.swt.URLTransfer;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.CompositeMinSize;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*      */ import org.gudy.azureus2.ui.swt.views.piece.PieceInfoView;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_TabsCommon;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*      */ import org.gudy.azureus2.ui.swt.views.table.painted.TableRowPainted;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
/*      */ 
/*      */ public class MyTorrentsView extends TableViewTab<DownloadManager> implements org.gudy.azureus2.core3.global.GlobalManagerListener, ParameterListener, DownloadManagerListener, com.aelitis.azureus.core.tag.TagTypeListener, com.aelitis.azureus.core.tag.TagListener, org.eclipse.swt.events.KeyListener, TableLifeCycleListener, org.gudy.azureus2.ui.swt.views.table.TableViewSWTPanelCreator, TableSelectionListener, org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener, com.aelitis.azureus.ui.common.table.TableRefreshListener, TableViewFilterCheck.TableViewFilterCheckEx<DownloadManager>, org.gudy.azureus2.plugins.ui.tables.TableRowRefreshListener, TableCountChangeListener, TableExpansionChangeListener, UIPluginViewToolBarListener
/*      */ {
/*  136 */   private static final LogIDs LOGID = LogIDs.GUI;
/*      */   public static volatile Set<String> preferred_tracker_names;
/*      */   public static volatile boolean eta_absolute;
/*      */   public static volatile boolean progress_eta_absolute;
/*      */   private AzureusCore azureus_core;
/*      */   private GlobalManager globalManager;
/*      */   
/*  143 */   static { COConfigurationManager.addAndFireParameterListeners(new String[] { "mtv.trackername.pref.hosts", "mtv.eta.show_absolute", "mtv.progress_eta.show_absolute" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  155 */         String prefs = COConfigurationManager.getStringParameter("mtv.trackername.pref.hosts", null);
/*      */         
/*  157 */         Set<String> new_vals = new HashSet();
/*      */         
/*  159 */         if (prefs != null)
/*      */         {
/*  161 */           String[] bits = prefs.split(";");
/*      */           
/*  163 */           for (String s : bits)
/*      */           {
/*  165 */             s = s.trim();
/*      */             
/*  167 */             if (s.length() > 0)
/*      */             {
/*  169 */               new_vals.add(s);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  174 */         MyTorrentsView.preferred_tracker_names = new_vals;
/*      */         
/*  176 */         MyTorrentsView.eta_absolute = COConfigurationManager.getBooleanParameter("mtv.eta.show_absolute", false);
/*  177 */         MyTorrentsView.progress_eta_absolute = COConfigurationManager.getBooleanParameter("mtv.progress_eta.show_absolute", false);
/*      */       }
/*      */     }); }
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
/*  190 */   private GlobalManagerEventListener gm_event_listener = new GlobalManagerEventListener()
/*      */   {
/*      */ 
/*      */ 
/*      */     public void eventOccurred(GlobalManagerEvent event)
/*      */     {
/*      */ 
/*  197 */       if (event.getEventType() == 1)
/*      */       {
/*  199 */         DownloadManager dm = event.getDownload();
/*      */         
/*  201 */         if (MyTorrentsView.this.isOurDownloadManager(dm))
/*      */         {
/*  203 */           TableRowCore row = MyTorrentsView.this.tv.getRow(dm);
/*      */           
/*  205 */           if (row != null)
/*      */           {
/*  207 */             TableRowCore[] existing = MyTorrentsView.this.tv.getSelectedRows();
/*      */             
/*  209 */             if (existing != null)
/*      */             {
/*  211 */               for (TableRowCore e : existing)
/*      */               {
/*  213 */                 if (e != row)
/*      */                 {
/*  215 */                   e.setSelected(false);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  220 */             if (!row.isSelected())
/*      */             {
/*  222 */               row.setSelected(true);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   };
/*      */   
/*      */   private boolean supportsTabs;
/*      */   
/*      */   private Composite cTablePanel;
/*  233 */   private Font fontButton = null;
/*      */   protected Composite cCategoriesAndTags;
/*  235 */   private DragSource dragSource = null;
/*  236 */   private DropTarget dropTarget = null;
/*  237 */   protected Text txtFilter = null;
/*  238 */   private Menu tableHeaderMenu = null;
/*      */   
/*      */   private TimerEventPeriodic txtFilterUpdateEvent;
/*      */   
/*      */   private Tag[] currentTags;
/*      */   
/*      */   private List<Tag> allTags;
/*      */   
/*  246 */   private int drag_drop_line_start = -1;
/*  247 */   private TableRowCore[] drag_drop_rows = null;
/*      */   
/*      */   private boolean bDNDalwaysIncomplete;
/*      */   
/*      */   private TableViewSWT<DownloadManager> tv;
/*      */   
/*      */   private Composite cTableParentPanel;
/*      */   
/*      */   protected boolean viewActive;
/*      */   private TableSelectionListener defaultSelectedListener;
/*      */   private Composite filterParent;
/*      */   protected boolean neverShowCatOrTagButtons;
/*  259 */   private boolean rebuildListOnFocusGain = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Menu oldMenu;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isCompletedOnly;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Class<?> forDataSourceType;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private SelectionListener buttonSelectionListener;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Listener buttonHoverListener;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private DropTargetListener buttonDropTargetListener;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isEmptyListOnNullDS;
/*      */   
/*      */ 
/*      */ 
/*      */   private FrequencyLimitedDispatcher refresh_limiter;
/*      */   
/*      */ 
/*      */ 
/*      */   private Set<Tag> pending_tag_changes;
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean currentTagsAny;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TableViewSWT<DownloadManager> initYourTableView()
/*      */   {
/*  313 */     return this.tv;
/*      */   }
/*      */   
/*      */   public void tableViewTabInitComplete()
/*      */   {
/*  318 */     if (COConfigurationManager.getBooleanParameter("Library.showFancyMenu", true)) {
/*  319 */       Composite tableComposite = this.tv.getComposite();
/*  320 */       this.oldMenu = tableComposite.getMenu();
/*  321 */       Menu menu = new Menu(tableComposite);
/*  322 */       tableComposite.setMenu(menu);
/*  323 */       menu.addMenuListener(new MenuListener()
/*      */       {
/*      */         public void menuShown(MenuEvent e) {
/*  326 */           if (!MyTorrentsView.this.showMyOwnMenu(e)) {
/*  327 */             MyTorrentsView.this.oldMenu.setVisible(true);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public void menuHidden(MenuEvent e) {}
/*      */       });
/*      */     }
/*  335 */     super.tableViewTabInitComplete();
/*      */   }
/*      */   
/*      */   protected boolean showMyOwnMenu(MenuEvent e) {
/*  339 */     Display d = e.widget.getDisplay();
/*  340 */     if (d == null) {
/*  341 */       return false;
/*      */     }
/*  343 */     Object[] dataSources = this.tv.getSelectedDataSources(true);
/*  344 */     DownloadManager[] dms = getSelectedDownloads();
/*      */     
/*  346 */     boolean hasSelection = dms.length > 0;
/*      */     
/*  348 */     if (!hasSelection) {
/*  349 */       return false;
/*      */     }
/*  351 */     Point pt = e.display.getCursorLocation();
/*  352 */     pt = this.tv.getTableComposite().toControl(pt.x, pt.y);
/*  353 */     TableColumnCore column = this.tv.getTableColumnByOffset(pt.x);
/*      */     
/*  355 */     boolean isSeedingView = (Download.class.equals(this.forDataSourceType)) || (DownloadTypeComplete.class.equals(this.forDataSourceType));
/*  356 */     new TorrentMenuFancy(this.tv, isSeedingView, getComposite().getShell(), dms, this.tv.getTableID()).showMenu(column, this.oldMenu);
/*      */     
/*  358 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public void init(AzureusCore _azureus_core, String tableID, Class<?> forDataSourceType, TableColumnCore[] basicItems)
/*      */   {
/*  364 */     this.forDataSourceType = forDataSourceType;
/*  365 */     this.isCompletedOnly = forDataSourceType.equals(DownloadTypeComplete.class);
/*      */     
/*  367 */     this.tv = createTableView(forDataSourceType, tableID, basicItems);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  372 */     if (getRowDefaultHeight() > 0) {
/*  373 */       this.tv.setRowDefaultHeightPX(getRowDefaultHeight());
/*      */     } else {
/*  375 */       this.tv.setRowDefaultHeightEM(1.0F);
/*      */     }
/*      */     
/*  378 */     this.azureus_core = _azureus_core;
/*  379 */     this.globalManager = this.azureus_core.getGlobalManager();
/*      */     
/*      */ 
/*  382 */     if (this.currentTags == null) {
/*  383 */       this.currentTags = new Tag[] { CategoryManager.getCategory(1) };
/*      */     }
/*      */     
/*      */ 
/*  387 */     this.tv.addLifeCycleListener(this);
/*  388 */     this.tv.setMainPanelCreator(this);
/*  389 */     this.tv.addSelectionListener(this, false);
/*  390 */     this.tv.addMenuFillListener(this);
/*  391 */     this.tv.addRefreshListener(this, false);
/*  392 */     if (this.tv.canHaveSubItems()) {
/*  393 */       this.tv.addRefreshListener(this);
/*  394 */       this.tv.addCountChangeListener(this);
/*  395 */       this.tv.addExpansionChangeListener(this);
/*      */     }
/*      */     
/*  398 */     this.tv.addTableDataSourceChangedListener(new com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener() {
/*      */       public void tableDataSourceChanged(Object newDataSource) {
/*  400 */         if ((newDataSource instanceof Tag[])) {
/*  401 */           MyTorrentsView.this.neverShowCatOrTagButtons = true;
/*  402 */           MyTorrentsView.this.setCurrentTags((Tag[])newDataSource);
/*  403 */           return;
/*      */         }
/*      */         
/*  406 */         if ((newDataSource instanceof Object[])) {
/*  407 */           Object[] datasources = (Object[])newDataSource;
/*  408 */           Object firstDS = datasources.length > 0 ? datasources[0] : null;
/*  409 */           if ((firstDS instanceof Tag)) {
/*  410 */             Tag[] tags = new Tag[datasources.length];
/*  411 */             System.arraycopy(datasources, 0, tags, 0, datasources.length);
/*  412 */             MyTorrentsView.this.setCurrentTags(tags);
/*  413 */             return;
/*      */           }
/*      */         }
/*      */         
/*  417 */         if ((newDataSource instanceof Tag)) {
/*  418 */           MyTorrentsView.this.neverShowCatOrTagButtons = true;
/*  419 */           MyTorrentsView.this.setCurrentTags(new Tag[] { (Tag)newDataSource });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  424 */         if ((newDataSource == null) && (MyTorrentsView.this.isEmptyListOnNullDS))
/*  425 */           MyTorrentsView.this.setCurrentTags(new Tag[0]); } }, true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  430 */     if (this.txtFilter != null) {
/*  431 */       this.filterParent = this.txtFilter.getParent();
/*  432 */       if (Constants.isWindows)
/*      */       {
/*  434 */         this.filterParent = this.filterParent.getParent();
/*      */       }
/*      */       
/*  437 */       Menu menuFilterHeader = getHeaderMenu(this.txtFilter);
/*  438 */       this.filterParent.setMenu(menuFilterHeader);
/*  439 */       Control[] children = this.filterParent.getChildren();
/*  440 */       for (Control control : children) {
/*  441 */         if (control != this.txtFilter) {
/*  442 */           control.setMenu(menuFilterHeader);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void tableViewInitialized()
/*      */   {
/*  450 */     this.tv.addKeyListener(this);
/*      */     
/*  452 */     createTabs();
/*      */     
/*  454 */     if (this.txtFilter == null) {
/*  455 */       this.tv.enableFilterCheck(null, this);
/*      */     }
/*      */     
/*  458 */     createDragDrop();
/*      */     
/*  460 */     Utils.getOffOfSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  463 */         COConfigurationManager.addAndFireParameterListeners(new String[] { "DND Always In Incomplete", "User Mode", "Library.ShowCatButtons", "Library.ShowTagButtons", "Library.ShowTagButtons.CompOnly" }, MyTorrentsView.this);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  470 */         if (MyTorrentsView.this.currentTags != null) {
/*  471 */           for (Tag tag : MyTorrentsView.this.currentTags) {
/*  472 */             tag.addTagListener(MyTorrentsView.this, false);
/*      */           }
/*      */         }
/*  475 */         TagManager tagManager = TagManagerFactory.getTagManager();
/*  476 */         TagType ttManual = tagManager.getTagType(3);
/*  477 */         TagType ttCat = tagManager.getTagType(1);
/*  478 */         ttManual.addTagTypeListener(MyTorrentsView.this, false);
/*  479 */         ttCat.addTagTypeListener(MyTorrentsView.this, false);
/*      */         
/*  481 */         MyTorrentsView.this.globalManager.addListener(MyTorrentsView.this, false);
/*  482 */         MyTorrentsView.this.globalManager.addEventListener(MyTorrentsView.this.gm_event_listener);
/*  483 */         DownloadManager[] dms = (DownloadManager[])MyTorrentsView.this.globalManager.getDownloadManagers().toArray(new DownloadManager[0]);
/*  484 */         for (int i = 0; i < dms.length; i++) {
/*  485 */           DownloadManager dm = dms[i];
/*  486 */           dm.addListener(MyTorrentsView.this);
/*  487 */           if (!MyTorrentsView.this.isOurDownloadManager(dm)) {
/*  488 */             dms[i] = null;
/*      */           }
/*      */         }
/*  491 */         MyTorrentsView.this.tv.addDataSources(dms);
/*  492 */         MyTorrentsView.this.tv.processDataSourceQueue();
/*      */       }
/*      */       
/*  495 */     });
/*  496 */     this.cTablePanel.layout();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Menu getHeaderMenu(Control control)
/*      */   {
/*  503 */     if (this.tableHeaderMenu != null)
/*      */     {
/*  505 */       return this.tableHeaderMenu;
/*      */     }
/*      */     
/*  508 */     this.tableHeaderMenu = new Menu(control.getShell(), 8);
/*      */     
/*      */ 
/*      */ 
/*  512 */     final MenuItem menuItemShowUptime = new MenuItem(this.tableHeaderMenu, 32);
/*  513 */     Messages.setLanguageText(menuItemShowUptime, "ConfigView.label.showuptime");
/*      */     
/*  515 */     menuItemShowUptime.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  517 */         COConfigurationManager.setParameter("MyTorrentsView.showuptime", menuItemShowUptime.getSelection());
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  523 */     });
/*  524 */     final MenuItem menuItemShowRates = new MenuItem(this.tableHeaderMenu, 32);
/*  525 */     Messages.setLanguageText(menuItemShowRates, "label.show.selected.rates");
/*      */     
/*  527 */     menuItemShowRates.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  529 */         COConfigurationManager.setParameter("MyTorrentsView.showrates", menuItemShowRates.getSelection());
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  534 */     });
/*  535 */     final MenuItem menuItemShowCatBut = new MenuItem(this.tableHeaderMenu, 32);
/*  536 */     Messages.setLanguageText(menuItemShowCatBut, "ConfigView.label.show.cat.but");
/*      */     
/*  538 */     menuItemShowCatBut.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  540 */         COConfigurationManager.setParameter("Library.ShowCatButtons", menuItemShowCatBut.getSelection());
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  547 */     });
/*  548 */     final MenuItem menuItemShowTagBut = new MenuItem(this.tableHeaderMenu, 32);
/*  549 */     Messages.setLanguageText(menuItemShowTagBut, "ConfigView.label.show.tag.but");
/*      */     
/*  551 */     menuItemShowTagBut.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  553 */         COConfigurationManager.setParameter("Library.ShowTagButtons", menuItemShowTagBut.getSelection());
/*      */       }
/*      */       
/*      */ 
/*  557 */     });
/*  558 */     new MenuItem(this.tableHeaderMenu, 2);
/*      */     
/*      */ 
/*      */ 
/*  562 */     String rr = MessageText.getString("ConfigView.section.security.restart.title");
/*      */     
/*  564 */     final MenuItem menuEnableSimple = new MenuItem(this.tableHeaderMenu, 32);
/*      */     
/*  566 */     menuEnableSimple.setText(MessageText.getString("ConfigView.section.style.EnableSimpleView") + " (" + rr + ")");
/*      */     
/*  568 */     menuEnableSimple.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  570 */         COConfigurationManager.setParameter("Library.EnableSimpleView", menuEnableSimple.getSelection());
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  577 */     });
/*  578 */     this.tableHeaderMenu.addMenuListener(new MenuListener() {
/*      */       public void menuShown(MenuEvent e) {
/*  580 */         menuItemShowUptime.setSelection(COConfigurationManager.getBooleanParameter("MyTorrentsView.showuptime"));
/*  581 */         menuItemShowRates.setSelection(COConfigurationManager.getBooleanParameter("MyTorrentsView.showrates"));
/*  582 */         menuItemShowCatBut.setSelection(COConfigurationManager.getBooleanParameter("Library.ShowCatButtons"));
/*  583 */         menuItemShowTagBut.setSelection(COConfigurationManager.getBooleanParameter("Library.ShowTagButtons"));
/*      */         
/*  585 */         menuItemShowCatBut.setEnabled(!MyTorrentsView.this.neverShowCatOrTagButtons);
/*  586 */         menuItemShowTagBut.setEnabled(!MyTorrentsView.this.neverShowCatOrTagButtons);
/*      */         
/*  588 */         menuEnableSimple.setSelection(COConfigurationManager.getBooleanParameter("Library.EnableSimpleView"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void menuHidden(MenuEvent e) {}
/*  595 */     });
/*  596 */     return this.tableHeaderMenu;
/*      */   }
/*      */   
/*      */   public void tableViewDestroyed()
/*      */   {
/*  601 */     this.tv.removeKeyListener(this);
/*      */     
/*  603 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*      */         try {
/*  606 */           Utils.disposeSWTObjects(new Object[] { MyTorrentsView.this.dragSource, MyTorrentsView.this.dropTarget, MyTorrentsView.this.fontButton, MyTorrentsView.this.tableHeaderMenu });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  612 */           MyTorrentsView.this.dragSource = null;
/*  613 */           MyTorrentsView.this.dropTarget = null;
/*  614 */           MyTorrentsView.this.fontButton = null;
/*  615 */           MyTorrentsView.this.tableHeaderMenu = null;
/*      */         }
/*      */         catch (Exception e) {
/*  618 */           org.gudy.azureus2.core3.util.Debug.out(e);
/*      */         }
/*      */       }
/*  621 */     });
/*  622 */     Object[] dms = this.globalManager.getDownloadManagers().toArray();
/*  623 */     for (int i = 0; i < dms.length; i++) {
/*  624 */       DownloadManager dm = (DownloadManager)dms[i];
/*  625 */       dm.removeListener(this);
/*      */     }
/*      */     
/*  628 */     if (this.currentTags != null) {
/*  629 */       for (Tag tag : this.currentTags) {
/*  630 */         tag.removeTagListener(this);
/*      */       }
/*      */     }
/*  633 */     TagManager tagManager = TagManagerFactory.getTagManager();
/*  634 */     TagType ttManual = tagManager.getTagType(3);
/*  635 */     TagType ttCat = tagManager.getTagType(1);
/*  636 */     ttManual.removeTagTypeListener(this);
/*  637 */     ttCat.removeTagTypeListener(this);
/*      */     
/*  639 */     this.globalManager.removeListener(this);
/*  640 */     this.globalManager.removeEventListener(this.gm_event_listener);
/*  641 */     COConfigurationManager.removeParameterListener("DND Always In Incomplete", this);
/*  642 */     COConfigurationManager.removeParameterListener("Library.ShowCatButtons", this);
/*  643 */     COConfigurationManager.removeParameterListener("Library.ShowTagButtons", this);
/*  644 */     COConfigurationManager.removeParameterListener("Library.ShowTagButtons.CompOnly", this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Composite createTableViewPanel(Composite composite)
/*      */   {
/*  652 */     this.cTableParentPanel = new Composite(composite, 0);
/*  653 */     GridLayout layout = new GridLayout();
/*  654 */     layout.horizontalSpacing = 0;
/*  655 */     layout.verticalSpacing = 0;
/*  656 */     layout.marginHeight = 0;
/*  657 */     layout.marginWidth = 0;
/*  658 */     this.cTableParentPanel.setLayout(layout);
/*  659 */     if ((composite.getLayout() instanceof GridLayout)) {
/*  660 */       this.cTableParentPanel.setLayoutData(new GridData(1808));
/*      */     }
/*      */     
/*  663 */     this.cTablePanel = new Composite(this.cTableParentPanel, 0);
/*      */     
/*  665 */     this.cTablePanel.addListener(26, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  667 */         MyTorrentsView.this.viewActive = true;
/*  668 */         MyTorrentsView.this.updateSelectedContent();
/*      */       }
/*      */       
/*  671 */     });
/*  672 */     this.cTablePanel.addListener(27, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  674 */         MyTorrentsView.this.viewActive = false;
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  680 */     });
/*  681 */     GridData gridData = new GridData(1808);
/*  682 */     this.cTablePanel.setLayoutData(gridData);
/*      */     
/*  684 */     layout = new GridLayout(1, false);
/*  685 */     layout.marginHeight = 0;
/*  686 */     layout.marginWidth = 0;
/*  687 */     layout.verticalSpacing = 0;
/*  688 */     layout.horizontalSpacing = 0;
/*  689 */     this.cTablePanel.setLayout(layout);
/*      */     
/*  691 */     this.cTablePanel.layout();
/*  692 */     return this.cTablePanel;
/*      */   }
/*      */   
/*      */   private void createTabs() {
/*  696 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  698 */         MyTorrentsView.this.swt_createTabs();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void swt_createTabs()
/*      */   {
/*  705 */     boolean catButtonsDisabled = this.neverShowCatOrTagButtons;
/*  706 */     if (!catButtonsDisabled) {
/*  707 */       catButtonsDisabled = !COConfigurationManager.getBooleanParameter("Library.ShowCatButtons");
/*      */     }
/*      */     
/*  710 */     List<Tag> tags_to_show = new ArrayList();
/*      */     
/*  712 */     boolean tagButtonsDisabled = this.neverShowCatOrTagButtons;
/*  713 */     if (!tagButtonsDisabled) {
/*  714 */       tagButtonsDisabled = !COConfigurationManager.getBooleanParameter("Library.ShowTagButtons");
/*      */       
/*  716 */       if ((!tagButtonsDisabled) && 
/*  717 */         (!this.isCompletedOnly)) {
/*  718 */         tagButtonsDisabled = COConfigurationManager.getBooleanParameter("Library.ShowTagButtons.CompOnly");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  723 */     if (!tagButtonsDisabled) {
/*  724 */       ArrayList<Tag> tagsManual = new ArrayList(TagManagerFactory.getTagManager().getTagType(3).getTags());
/*      */       
/*      */ 
/*  727 */       for (Tag tag : tagsManual) {
/*  728 */         if (tag.isVisible()) {
/*  729 */           tags_to_show.add(tag);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  734 */     if (!catButtonsDisabled) {
/*  735 */       ArrayList<Tag> tagsCat = new ArrayList(TagManagerFactory.getTagManager().getTagType(1).getTags());
/*      */       
/*      */ 
/*  738 */       for (Tag tag : tagsCat) {
/*  739 */         if (tag.isVisible()) {
/*  740 */           tags_to_show.add(tag);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  746 */     tags_to_show = TagUIUtils.sortTags(tags_to_show);
/*      */     
/*  748 */     buildHeaderArea();
/*  749 */     if ((this.cCategoriesAndTags != null) && (!this.cCategoriesAndTags.isDisposed())) {
/*  750 */       Utils.disposeComposite(this.cCategoriesAndTags, false);
/*      */     }
/*      */     
/*  753 */     if (tags_to_show.size() > 0) {
/*  754 */       buildCatAndTag(tags_to_show);
/*  755 */     } else if ((this.cTableParentPanel != null) && (!this.cTableParentPanel.isDisposed())) {
/*  756 */       this.cTableParentPanel.layout();
/*      */     }
/*      */   }
/*      */   
/*      */   private void buildHeaderArea() {
/*  761 */     if (this.cCategoriesAndTags == null) {
/*  762 */       this.cCategoriesAndTags = new CompositeMinSize(this.cTableParentPanel, 0);
/*  763 */       ((CompositeMinSize)this.cCategoriesAndTags).setMinSize(new Point(-1, 24));
/*  764 */       GridData gridData = new GridData(131072, 16777216, true, false);
/*  765 */       this.cCategoriesAndTags.setLayoutData(gridData);
/*  766 */       this.cCategoriesAndTags.moveAbove(null);
/*      */       
/*  768 */       if (this.filterParent != null)
/*      */       {
/*  770 */         org.eclipse.swt.graphics.Color background = this.filterParent.getBackground();
/*  771 */         if (background != null) {
/*  772 */           this.cCategoriesAndTags.setBackground(background);
/*  773 */           this.cTableParentPanel.setBackground(background);
/*      */         }
/*      */       }
/*      */       
/*  777 */       this.cCategoriesAndTags.setBackgroundMode(2);
/*  778 */     } else if (this.cCategoriesAndTags.isDisposed())
/*      */     {
/*      */       return;
/*      */     }
/*      */     RowLayout rowLayout;
/*      */     RowLayout rowLayout;
/*  784 */     if ((this.cCategoriesAndTags.getLayout() instanceof RowLayout)) {
/*  785 */       rowLayout = (RowLayout)this.cCategoriesAndTags.getLayout();
/*      */     } else {
/*  787 */       rowLayout = new RowLayout();
/*  788 */       this.cCategoriesAndTags.setLayout(rowLayout);
/*      */     }
/*  790 */     rowLayout.marginTop = 0;
/*  791 */     rowLayout.marginBottom = 0;
/*  792 */     rowLayout.marginLeft = Utils.adjustPXForDPI(3);
/*  793 */     rowLayout.marginRight = Utils.adjustPXForDPI(3);
/*  794 */     rowLayout.spacing = 0;
/*  795 */     rowLayout.wrap = true;
/*      */     
/*      */ 
/*  798 */     Menu menu = getHeaderMenu(this.cTableParentPanel);
/*  799 */     this.cTableParentPanel.setMenu(menu);
/*      */     
/*  801 */     if (Constants.isOSX)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  808 */       this.cTableParentPanel.addListener(35, new Listener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void handleEvent(Event event)
/*      */         {
/*      */ 
/*      */ 
/*  816 */           Display display = MyTorrentsView.this.cTableParentPanel.getDisplay();
/*      */           
/*  818 */           Point pp_rel = display.map(null, MyTorrentsView.this.cTableParentPanel, event.x, event.y);
/*      */           
/*  820 */           Control hit = Utils.findChild(MyTorrentsView.this.cTableParentPanel, pp_rel.x, pp_rel.y);
/*      */           
/*  822 */           event.doit = (hit == MyTorrentsView.this.cTableParentPanel);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  827 */     this.tv.enableFilterCheck(this.txtFilter, this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void buildCatAndTag(List<Tag> tags)
/*      */   {
/*  838 */     if ((tags.size() == 0) || (this.cCategoriesAndTags.isDisposed())) {
/*  839 */       return;
/*      */     }
/*      */     
/*  842 */     int iFontPixelsHeight = Utils.adjustPXForDPI(10);
/*  843 */     int iFontPointHeight = iFontPixelsHeight * 72 / Utils.getDPIRaw(this.cCategoriesAndTags.getDisplay()).y;
/*      */     
/*  845 */     Label spacer = null;
/*      */     
/*  847 */     int max_rd_height = 0;
/*      */     
/*  849 */     this.allTags = tags;
/*      */     
/*  851 */     if (this.buttonSelectionListener == null) {
/*  852 */       this.buttonSelectionListener = new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  854 */           boolean add = e.stateMask == SWT.MOD1;
/*      */           
/*  856 */           Button curButton = (Button)e.widget;
/*  857 */           boolean isEnabled = curButton.getSelection();
/*      */           
/*  859 */           Tag tag = (Tag)curButton.getData("Tag");
/*      */           
/*  861 */           if (!isEnabled) {
/*  862 */             MyTorrentsView.this.removeTagFromCurrent(tag);
/*      */           }
/*  864 */           else if (add) {
/*  865 */             Category catAll = CategoryManager.getCategory(1);
/*      */             
/*  867 */             if (tag.equals(catAll)) {
/*  868 */               MyTorrentsView.this.setCurrentTags(new Tag[] { catAll });
/*      */             } else {
/*  870 */               Tag[] newTags = new Tag[MyTorrentsView.this.currentTags.length + 1];
/*  871 */               System.arraycopy(MyTorrentsView.this.currentTags, 0, newTags, 0, MyTorrentsView.this.currentTags.length);
/*  872 */               newTags[MyTorrentsView.this.currentTags.length] = tag;
/*      */               
/*  874 */               newTags = (Tag[])MyTorrentsView.this.removeFromArray(newTags, catAll);
/*  875 */               MyTorrentsView.this.setCurrentTags(newTags);
/*      */             }
/*      */           } else {
/*  878 */             MyTorrentsView.this.setCurrentTags(new Tag[] { (Tag)curButton.getData("Tag") });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  884 */           Control[] controls = curButton.getParent().getChildren();
/*  885 */           for (int i = 0; i < controls.length; i++) {
/*  886 */             if ((controls[i] instanceof Button))
/*      */             {
/*      */ 
/*  889 */               Button b = (Button)controls[i];
/*  890 */               Tag btag = (Tag)b.getData("Tag");
/*  891 */               b.setSelection(MyTorrentsView.this.isCurrent(btag));
/*      */             }
/*      */           }
/*      */         }
/*  895 */       };
/*  896 */       this.buttonHoverListener = new Listener() {
/*      */         public void handleEvent(Event event) {
/*  898 */           Button curButton = (Button)event.widget;
/*  899 */           Tag tag = (Tag)curButton.getData("Tag");
/*      */           
/*  901 */           if (!(tag instanceof Category)) {
/*  902 */             curButton.setToolTipText(TagUIUtils.getTagTooltip(tag, true));
/*  903 */             return;
/*      */           }
/*      */           
/*  906 */           Category category = (Category)tag;
/*      */           
/*  908 */           List<DownloadManager> dms = category.getDownloadManagers(MyTorrentsView.this.globalManager.getDownloadManagers());
/*      */           
/*      */ 
/*  911 */           long ttlActive = 0L;
/*  912 */           long ttlSize = 0L;
/*  913 */           long ttlRSpeed = 0L;
/*  914 */           long ttlSSpeed = 0L;
/*  915 */           int count = 0;
/*  916 */           for (DownloadManager dm : dms)
/*      */           {
/*  918 */             if (category.hasTaggable(dm))
/*      */             {
/*      */ 
/*      */ 
/*  922 */               count++;
/*  923 */               if ((dm.getState() == 50) || (dm.getState() == 60))
/*      */               {
/*  925 */                 ttlActive += 1L;
/*      */               }
/*  927 */               DownloadManagerStats stats = dm.getStats();
/*  928 */               ttlSize += stats.getSizeExcludingDND();
/*  929 */               ttlRSpeed += stats.getDataReceiveRate();
/*  930 */               ttlSSpeed += stats.getDataSendRate();
/*      */             }
/*      */           }
/*  933 */           String up_details = "";
/*  934 */           String down_details = "";
/*      */           
/*  936 */           if (category.getType() != 1)
/*      */           {
/*  938 */             String up_str = MessageText.getString("GeneralView.label.maxuploadspeed");
/*      */             
/*  940 */             String down_str = MessageText.getString("GeneralView.label.maxdownloadspeed");
/*      */             
/*  942 */             String unlimited_str = MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited");
/*      */             
/*      */ 
/*  945 */             int up_speed = category.getUploadSpeed();
/*  946 */             int down_speed = category.getDownloadSpeed();
/*      */             
/*  948 */             up_details = up_str + ": " + (up_speed == 0 ? unlimited_str : DisplayFormatters.formatByteCountToKiBEtc(up_speed));
/*      */             
/*  950 */             down_details = down_str + ": " + (down_speed == 0 ? unlimited_str : DisplayFormatters.formatByteCountToKiBEtc(down_speed));
/*      */           }
/*      */           
/*      */ 
/*  954 */           if (count == 0) {
/*  955 */             curButton.setToolTipText(down_details + "\n" + up_details + "\nTotal: 0");
/*      */             
/*  957 */             return;
/*      */           }
/*      */           
/*  960 */           curButton.setToolTipText((up_details.length() == 0 ? "" : new StringBuilder().append(down_details).append("\n").append(up_details).append("\n").toString()) + "Total: " + count + "\n" + "Downloading/Seeding: " + ttlActive + "\n" + "\n" + "Total Speed: " + DisplayFormatters.formatByteCountToKiBEtcPerSec(ttlRSpeed) + " / " + DisplayFormatters.formatByteCountToKiBEtcPerSec(ttlSSpeed) + "\n" + "Average Speed: " + DisplayFormatters.formatByteCountToKiBEtcPerSec(ttlRSpeed / (ttlActive == 0L ? 1L : ttlActive)) + " / " + DisplayFormatters.formatByteCountToKiBEtcPerSec(ttlSSpeed / (ttlActive == 0L ? 1L : ttlActive)) + "\n" + "Size: " + DisplayFormatters.formatByteCountToKiBEtc(ttlSize));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  978 */       };
/*  979 */       this.buttonDropTargetListener = new DropTargetAdapter()
/*      */       {
/*      */         public void dragOver(DropTargetEvent e) {
/*  982 */           if (MyTorrentsView.this.drag_drop_line_start >= 0) {
/*  983 */             boolean doAdd = false;
/*      */             
/*  985 */             Control curButton = ((DropTarget)e.widget).getControl();
/*  986 */             Tag tag = (Tag)curButton.getData("Tag");
/*  987 */             Object[] ds = MyTorrentsView.this.tv.getSelectedDataSources().toArray();
/*  988 */             if (tag != null) {
/*  989 */               for (Object obj : ds)
/*      */               {
/*  991 */                 if ((obj instanceof DownloadManager))
/*      */                 {
/*  993 */                   DownloadManager dm = (DownloadManager)obj;
/*      */                   
/*  995 */                   if (!tag.hasTaggable(dm)) {
/*  996 */                     doAdd = true;
/*  997 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1003 */             e.detail = (doAdd ? 1 : 2);
/*      */           }
/*      */           else {
/* 1006 */             e.detail = 0;
/*      */           }
/*      */         }
/*      */         
/*      */         public void drop(DropTargetEvent e) {
/* 1011 */           e.detail = 0;
/*      */           
/* 1013 */           if (MyTorrentsView.this.drag_drop_line_start >= 0) {
/* 1014 */             MyTorrentsView.this.drag_drop_line_start = -1;
/* 1015 */             MyTorrentsView.this.drag_drop_rows = null;
/*      */             
/* 1017 */             Object[] ds = MyTorrentsView.this.tv.getSelectedDataSources().toArray();
/*      */             
/* 1019 */             Control curButton = ((DropTarget)e.widget).getControl();
/*      */             
/* 1021 */             Tag tag = (Tag)curButton.getData("Tag");
/*      */             
/* 1023 */             if ((tag instanceof Category)) {
/* 1024 */               TorrentUtil.assignToCategory(ds, (Category)tag);
/* 1025 */               return;
/*      */             }
/*      */             
/* 1028 */             boolean doAdd = false;
/* 1029 */             for (Object obj : ds)
/*      */             {
/* 1031 */               if ((obj instanceof DownloadManager))
/*      */               {
/* 1033 */                 DownloadManager dm = (DownloadManager)obj;
/*      */                 
/* 1035 */                 if (!tag.hasTaggable(dm)) {
/* 1036 */                   doAdd = true;
/* 1037 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1042 */             for (Object obj : ds)
/*      */             {
/* 1044 */               if ((obj instanceof DownloadManager))
/*      */               {
/* 1046 */                 DownloadManager dm = (DownloadManager)obj;
/*      */                 
/* 1048 */                 if (doAdd) {
/* 1049 */                   tag.addTaggable(dm);
/*      */                 } else {
/* 1051 */                   tag.removeTaggable(dm);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       };
/*      */     }
/*      */     
/* 1060 */     for (Tag tag : tags) {
/* 1061 */       boolean isCat = tag instanceof Category;
/*      */       
/* 1063 */       Button button = new Button(this.cCategoriesAndTags, 2);
/*      */       
/* 1065 */       if (isCat) {
/* 1066 */         if (spacer == null) {
/* 1067 */           spacer = new Label(this.cCategoriesAndTags, 0);
/* 1068 */           RowData rd = new RowData();
/* 1069 */           rd.width = 8;
/* 1070 */           spacer.setLayoutData(rd);
/* 1071 */           spacer.moveAbove(null);
/*      */         }
/* 1073 */         button.moveAbove(spacer);
/*      */       }
/*      */       
/* 1076 */       button.addKeyListener(this);
/* 1077 */       if (this.fontButton == null) {
/* 1078 */         Font f = button.getFont();
/* 1079 */         FontData fd = f.getFontData()[0];
/* 1080 */         fd.setHeight(iFontPointHeight);
/* 1081 */         this.fontButton = new Font(this.cCategoriesAndTags.getDisplay(), fd);
/*      */       }
/* 1083 */       button.setText("|");
/* 1084 */       button.setFont(this.fontButton);
/* 1085 */       button.pack(true);
/* 1086 */       if (button.computeSize(100, -1).y > 0) {
/* 1087 */         RowData rd = new RowData();
/* 1088 */         int rd_height = button.computeSize(100, -1).y - 2 + button.getBorderWidth() * 2;
/* 1089 */         rd.height = rd_height;
/* 1090 */         max_rd_height = Math.max(max_rd_height, rd_height);
/* 1091 */         button.setLayoutData(rd);
/*      */       }
/*      */       
/* 1094 */       String tag_name = tag.getTagName(true);
/*      */       
/* 1096 */       button.setText(tag_name);
/*      */       
/* 1098 */       button.setData("Tag", tag);
/* 1099 */       if (isCurrent(tag)) {
/* 1100 */         button.setSelection(true);
/*      */       }
/*      */       
/* 1103 */       button.addSelectionListener(this.buttonSelectionListener);
/*      */       
/*      */ 
/* 1106 */       button.addListener(32, this.buttonHoverListener);
/*      */       
/* 1108 */       final DropTarget tabDropTarget = new DropTarget(button, 23);
/*      */       
/* 1110 */       Transfer[] types = { TextTransfer.getInstance() };
/*      */       
/*      */ 
/* 1113 */       tabDropTarget.setTransfer(types);
/* 1114 */       tabDropTarget.addDropListener(this.buttonDropTargetListener);
/*      */       
/* 1116 */       button.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
/*      */         public void widgetDisposed(DisposeEvent e) {
/* 1118 */           if (!tabDropTarget.isDisposed()) {
/* 1119 */             tabDropTarget.dispose();
/*      */           }
/*      */           
/*      */         }
/* 1123 */       });
/* 1124 */       Menu menu = new Menu(button);
/*      */       
/* 1126 */       button.setMenu(menu);
/*      */       
/* 1128 */       if (isCat) {
/* 1129 */         org.gudy.azureus2.ui.swt.views.utils.CategoryUIUtils.setupCategoryMenu(menu, (Category)tag);
/*      */       } else {
/* 1131 */         TagUIUtils.createSideBarMenuItems(menu, tag);
/*      */       }
/*      */     }
/*      */     
/* 1135 */     if (max_rd_height > 0) {
/* 1136 */       RowLayout layout = (RowLayout)this.cCategoriesAndTags.getLayout();
/* 1137 */       int top_margin = (24 - max_rd_height + 1) / 2;
/* 1138 */       if (top_margin > 0) {
/* 1139 */         layout.marginTop = top_margin;
/*      */       }
/*      */     }
/*      */     
/* 1143 */     this.cCategoriesAndTags.getParent().layout(true, true);
/*      */   }
/*      */   
/*      */   public boolean isOurDownloadManager(DownloadManager dm) {
/* 1147 */     if (!isInTags(dm, this.currentTags)) {
/* 1148 */       return false;
/*      */     }
/*      */     
/* 1151 */     if (Download.class.equals(this.forDataSourceType)) {
/* 1152 */       return true;
/*      */     }
/*      */     
/* 1155 */     boolean bCompleted = dm.isDownloadComplete(this.bDNDalwaysIncomplete);
/* 1156 */     boolean bOurs = ((bCompleted) && (this.isCompletedOnly)) || ((!bCompleted) && (!this.isCompletedOnly));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1164 */     return bOurs;
/*      */   }
/*      */   
/*      */   public boolean filterCheck(DownloadManager dm, String sLastSearch, boolean bRegexSearch) {
/* 1168 */     if (dm == null) {
/* 1169 */       return false;
/*      */     }
/*      */     boolean bOurs;
/* 1172 */     if (sLastSearch.length() > 0) {
/*      */       try {
/* 1174 */         String comment = dm.getDownloadState().getUserComment();
/* 1175 */         if (comment == null) {
/* 1176 */           comment = "";
/*      */         }
/*      */         
/* 1179 */         String[][] name_mapping = { { "", dm.getDisplayName() }, { "t:", "" }, { "st:", "" + dm.getState() }, { "c:", comment }, { "f:", "" } };
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
/* 1202 */         Object o_name = name_mapping[0][1];
/*      */         
/* 1204 */         String tmpSearch = sLastSearch;
/*      */         
/* 1206 */         for (int i = 1; i < name_mapping.length; i++)
/*      */         {
/* 1208 */           if (tmpSearch.startsWith(name_mapping[i][0]))
/*      */           {
/* 1210 */             tmpSearch = tmpSearch.substring(name_mapping[i][0].length());
/*      */             
/* 1212 */             if (i == 1)
/*      */             {
/* 1214 */               List<String> names = new ArrayList();
/*      */               
/* 1216 */               o_name = names;
/*      */               
/* 1218 */               TOTorrent t = dm.getTorrent();
/*      */               
/* 1220 */               if (t != null)
/*      */               {
/* 1222 */                 names.add(t.getAnnounceURL().getHost());
/*      */                 
/* 1224 */                 TOTorrentAnnounceURLSet[] sets = t.getAnnounceURLGroup().getAnnounceURLSets();
/*      */                 
/* 1226 */                 for (TOTorrentAnnounceURLSet set : sets)
/*      */                 {
/* 1228 */                   URL[] urls = set.getAnnounceURLs();
/*      */                   
/* 1230 */                   for (URL u : urls)
/*      */                   {
/* 1232 */                     names.add(u.getHost());
/*      */                   }
/*      */                 }
/*      */                 try
/*      */                 {
/* 1237 */                   byte[] hash = t.getHash();
/*      */                   
/* 1239 */                   names.add(ByteFormatter.encodeString(hash));
/* 1240 */                   names.add(org.gudy.azureus2.core3.util.Base32.encode(hash));
/*      */ 
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/* 1246 */             else if (i == 4)
/*      */             {
/* 1248 */               List<String> names = new ArrayList();
/*      */               
/* 1250 */               o_name = names;
/*      */               
/* 1252 */               DiskManagerFileInfoSet file_set = dm.getDiskManagerFileInfoSet();
/*      */               
/* 1254 */               DiskManagerFileInfo[] files = file_set.getFiles();
/*      */               
/* 1256 */               for (DiskManagerFileInfo f : files)
/*      */               {
/* 1258 */                 File file = f.getFile(true);
/*      */                 
/* 1260 */                 String name = tmpSearch.contains(File.separator) ? file.getAbsolutePath() : file.getName();
/*      */                 
/* 1262 */                 names.add(name);
/*      */               }
/*      */             }
/*      */             else {
/* 1266 */               o_name = name_mapping[i][1];
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1271 */         String s = "\\Q" + tmpSearch.replaceAll("[|;]", "\\\\E|\\\\Q") + "\\E";
/*      */         
/*      */ 
/* 1274 */         match_result = true;
/*      */         
/* 1276 */         if ((bRegexSearch) && (s.startsWith("!"))) {
/* 1277 */           s = s.substring(1);
/*      */           
/* 1279 */           match_result = false;
/*      */         }
/*      */         
/* 1282 */         pattern = com.aelitis.azureus.core.util.RegExUtil.getCachedPattern("tv:search", s, 2);
/*      */         boolean bOurs;
/* 1284 */         if ((o_name instanceof String))
/*      */         {
/* 1286 */           bOurs = pattern.matcher((String)o_name).find() == match_result;
/*      */         }
/*      */         else {
/* 1289 */           List<String> names = (List)o_name;
/*      */           
/*      */ 
/*      */ 
/* 1293 */           bOurs = !match_result;
/*      */           
/* 1295 */           for (String name : names) {
/* 1296 */             if (pattern.matcher(name).find()) {
/* 1297 */               bOurs = match_result;
/* 1298 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       } catch (Exception e) {
/*      */         boolean match_result;
/*      */         Pattern pattern;
/* 1305 */         boolean bOurs = true;
/*      */       }
/*      */       
/*      */     } else {
/* 1309 */       bOurs = true;
/*      */     }
/*      */     
/* 1312 */     return bOurs;
/*      */   }
/*      */   
/*      */   public void filterSet(final String filter)
/*      */   {
/* 1317 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1320 */         if (MyTorrentsView.this.txtFilter != null) {
/* 1321 */           Object x = MyTorrentsView.this.filterParent.getData("ViewUtils:ViewTitleExtraInfo");
/*      */           
/* 1323 */           if ((x instanceof ViewUtils.ViewTitleExtraInfo))
/*      */           {
/* 1325 */             boolean enabled = filter.length() > 0;
/*      */             
/* 1327 */             if (enabled)
/*      */             {
/* 1329 */               if (MyTorrentsView.this.txtFilterUpdateEvent == null)
/*      */               {
/* 1331 */                 MyTorrentsView.this.txtFilterUpdateEvent = SimpleTimer.addPeriodicEvent("MTV:updater", 1000L, new TimerEventPerformer()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void perform(TimerEvent event)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 1341 */                     Utils.execSWTThread(new AERunnable()
/*      */                     {
/*      */ 
/*      */                       public void runSupport()
/*      */                       {
/*      */ 
/* 1347 */                         if (MyTorrentsView.this.txtFilterUpdateEvent != null)
/*      */                         {
/* 1349 */                           if (MyTorrentsView.this.tv.isDisposed())
/*      */                           {
/* 1351 */                             MyTorrentsView.this.txtFilterUpdateEvent.cancel();
/*      */                             
/* 1353 */                             MyTorrentsView.this.txtFilterUpdateEvent = null;
/*      */                           }
/*      */                           else
/*      */                           {
/* 1357 */                             MyTorrentsView.this.viewChanged(MyTorrentsView.this.tv);
/*      */                           }
/*      */                           
/*      */                         }
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/* 1367 */             else if (MyTorrentsView.this.txtFilterUpdateEvent != null)
/*      */             {
/* 1369 */               MyTorrentsView.this.txtFilterUpdateEvent.cancel();
/*      */               
/* 1371 */               MyTorrentsView.this.txtFilterUpdateEvent = null;
/*      */             }
/*      */             
/*      */ 
/* 1375 */             ((ViewUtils.ViewTitleExtraInfo)x).setEnabled(MyTorrentsView.this.tv.getComposite(), enabled);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void viewChanged(final TableView<DownloadManager> view)
/*      */   {
/* 1386 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1388 */         MyTorrentsView.this.swt_viewChanged(view);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void swt_viewChanged(TableView<DownloadManager> view)
/*      */   {
/* 1395 */     if ((this.filterParent != null) && (!this.filterParent.isDisposed())) {
/* 1396 */       Object x = this.filterParent.getData("ViewUtils:ViewTitleExtraInfo");
/*      */       
/* 1398 */       if ((x instanceof ViewUtils.ViewTitleExtraInfo))
/*      */       {
/* 1400 */         TableRowCore[] rows = view.getRows();
/*      */         
/* 1402 */         int active = 0;
/*      */         
/* 1404 */         for (TableRowCore row : rows)
/*      */         {
/* 1406 */           DownloadManager dm = (DownloadManager)row.getDataSource(true);
/*      */           
/* 1408 */           int state = dm.getState();
/*      */           
/* 1410 */           if ((state == 50) || (state == 60))
/*      */           {
/* 1412 */             active++;
/*      */           }
/*      */         }
/*      */         
/* 1416 */         ((ViewUtils.ViewTitleExtraInfo)x).update(this.tv.getComposite(), rows.length, active);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void selected(TableRowCore[] rows)
/*      */   {
/* 1423 */     updateSelectedContent();
/* 1424 */     refreshTorrentMenu();
/*      */   }
/*      */   
/*      */   public void deselected(TableRowCore[] rows)
/*      */   {
/* 1429 */     updateSelectedContent();
/* 1430 */     refreshTorrentMenu();
/*      */   }
/*      */   
/*      */   public void focusChanged(TableRowCore focus)
/*      */   {
/* 1435 */     updateSelectedContent();
/* 1436 */     refreshTorrentMenu();
/*      */   }
/*      */   
/*      */   public MyTorrentsView(boolean supportsTabs)
/*      */   {
/*  276 */     super("MyTorrentsView");
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
/* 1447 */     this.refresh_limiter = new FrequencyLimitedDispatcher(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1450 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 1452 */             MyTorrentsView.this.updateSelectedContent();
/*      */           }
/*      */         });
/*      */       }
/* 1450 */     }, 250);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1459 */     this.refresh_limiter.setSingleThreaded();
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
/* 2302 */     this.pending_tag_changes = new HashSet();
/*      */     
/* 2304 */     this.currentTagsAny = true;this.supportsTabs = supportsTabs;
/*      */   }
/*      */   
/*      */   public MyTorrentsView(String propertiesPrefix, boolean supportsTabs)
/*      */   {
/*  281 */     super(propertiesPrefix);
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
/* 1447 */     this.refresh_limiter = new FrequencyLimitedDispatcher(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1450 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 1452 */             MyTorrentsView.this.updateSelectedContent();
/*      */           }
/*      */         });
/*      */       }
/* 1450 */     }, 250);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1459 */     this.refresh_limiter.setSingleThreaded();
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
/* 2302 */     this.pending_tag_changes = new HashSet();
/*      */     
/* 2304 */     this.currentTagsAny = true;this.supportsTabs = supportsTabs;
/*      */   }
/*      */   
/*      */   public MyTorrentsView(AzureusCore _azureus_core, String tableID, boolean isSeedingView, TableColumnCore[] basicItems, Text txtFilter, Composite cCatsAndTags, boolean supportsTabs)
/*      */   {
/*  303 */     super("MyTorrentsView");
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
/* 1447 */     this.refresh_limiter = new FrequencyLimitedDispatcher(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1450 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 1452 */             MyTorrentsView.this.updateSelectedContent();
/*      */           }
/*      */         });
/*      */       }
/* 1450 */     }, 250);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1459 */     this.refresh_limiter.setSingleThreaded();
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
/* 2302 */     this.pending_tag_changes = new HashSet();
/*      */     
/* 2304 */     this.currentTagsAny = true;this.txtFilter = txtFilter;this.cCategoriesAndTags = cCatsAndTags;this.supportsTabs = supportsTabs;init(_azureus_core, tableID, isSeedingView ? DownloadTypeComplete.class : org.gudy.azureus2.plugins.download.DownloadTypeIncomplete.class, basicItems);
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
/*      */   private void updateSelectedContentRateLimited()
/*      */   {
/* 1467 */     this.refresh_limiter.dispatch();
/*      */   }
/*      */   
/*      */   public void updateSelectedContent() {
/* 1471 */     updateSelectedContent(false);
/*      */   }
/*      */   
/*      */   public void updateSelectedContent(boolean force) {
/* 1475 */     if ((this.cTablePanel == null) || (this.cTablePanel.isDisposed())) {
/* 1476 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1482 */     if ((!isTableFocus()) && 
/* 1483 */       (!force)) {
/* 1484 */       return;
/*      */     }
/*      */     
/* 1487 */     Object[] dataSources = this.tv.getSelectedDataSources(true);
/* 1488 */     List<SelectedContent> listSelected = new ArrayList(dataSources.length);
/* 1489 */     for (Object ds : dataSources) {
/* 1490 */       if ((ds instanceof DownloadManager)) {
/* 1491 */         listSelected.add(new SelectedContent((DownloadManager)ds));
/* 1492 */       } else if ((ds instanceof DiskManagerFileInfo)) {
/* 1493 */         DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 1494 */         listSelected.add(new SelectedContent(fileInfo.getDownloadManager(), fileInfo.getIndex()));
/*      */       }
/*      */     }
/* 1497 */     SelectedContent[] content = (SelectedContent[])listSelected.toArray(new SelectedContent[0]);
/* 1498 */     SelectedContentManager.changeCurrentlySelectedContent(this.tv.getTableID(), content, this.tv);
/*      */   }
/*      */   
/*      */   private void refreshTorrentMenu() {
/* 1502 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1503 */     if ((uiFunctions != null) && ((uiFunctions instanceof UIFunctionsSWT))) {
/* 1504 */       ((UIFunctionsSWT)uiFunctions).refreshTorrentMenu();
/*      */     }
/*      */   }
/*      */   
/*      */   public DownloadManager[] getSelectedDownloads() {
/* 1509 */     Object[] data_sources = this.tv.getSelectedDataSources().toArray();
/* 1510 */     List<DownloadManager> list = new ArrayList();
/* 1511 */     for (Object ds : data_sources) {
/* 1512 */       if ((ds instanceof DownloadManager)) {
/* 1513 */         list.add((DownloadManager)ds);
/*      */       }
/*      */     }
/* 1516 */     return (DownloadManager[])list.toArray(new DownloadManager[0]);
/*      */   }
/*      */   
/*      */   public void defaultSelected(TableRowCore[] rows, int keyMask)
/*      */   {
/* 1521 */     if (this.defaultSelectedListener != null) {
/* 1522 */       this.defaultSelectedListener.defaultSelected(rows, keyMask);
/* 1523 */       return;
/*      */     }
/* 1525 */     showSelectedDetails();
/*      */   }
/*      */   
/*      */   private void showSelectedDetails() {
/* 1529 */     Object[] dm_sources = this.tv.getSelectedDataSources().toArray();
/* 1530 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1531 */     for (int i = 0; i < dm_sources.length; i++) {
/* 1532 */       if ((dm_sources[i] instanceof DownloadManager))
/*      */       {
/*      */ 
/* 1535 */         if (uiFunctions != null) {
/* 1536 */           uiFunctions.getMDI().showEntryByID("DMDetails", dm_sources[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void overrideDefaultSelected(TableSelectionListener defaultSelectedListener)
/*      */   {
/* 1544 */     this.defaultSelectedListener = defaultSelectedListener;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addThisColumnSubMenu(String sColumnName, Menu menuThisColumn)
/*      */   {
/* 1553 */     if (sColumnName.equals("health")) {
/* 1554 */       MenuItem item = new MenuItem(menuThisColumn, 8);
/* 1555 */       Messages.setLanguageText(item, "MyTorrentsView.menu.health");
/* 1556 */       Utils.setMenuItemImage(item, "st_explain");
/* 1557 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/* 1559 */           org.gudy.azureus2.ui.swt.help.HealthHelpWindow.show(Display.getDefault());
/*      */         }
/*      */       });
/*      */     }
/* 1563 */     else if (sColumnName.equals("trackername")) {
/* 1564 */       MenuItem item = new MenuItem(menuThisColumn, 8);
/* 1565 */       Messages.setLanguageText(item, "MyTorrentsView.menu.trackername.editprefs");
/* 1566 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/* 1568 */           SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("trackername.prefs.title", "trackername.prefs.message");
/*      */           
/* 1570 */           entryWindow.setPreenteredText(COConfigurationManager.getStringParameter("mtv.trackername.pref.hosts", ""), true);
/* 1571 */           entryWindow.selectPreenteredText(false);
/* 1572 */           entryWindow.prompt();
/* 1573 */           if (entryWindow.hasSubmittedInput()) {
/* 1574 */             String text = entryWindow.getSubmittedInput();
/*      */             
/* 1576 */             COConfigurationManager.setParameter("mtv.trackername.pref.hosts", text.trim());
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/* 1581 */     else if ((sColumnName.equals("eta")) || (sColumnName.equals("smootheta"))) {
/* 1582 */       final MenuItem item = new MenuItem(menuThisColumn, 32);
/* 1583 */       Messages.setLanguageText(item, "MyTorrentsView.menu.eta.abs");
/* 1584 */       item.setSelection(eta_absolute);
/*      */       
/* 1586 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/* 1588 */           MyTorrentsView.eta_absolute = item.getSelection();
/* 1589 */           MyTorrentsView.this.tv.columnInvalidate("eta");
/* 1590 */           MyTorrentsView.this.tv.refreshTable(false);
/* 1591 */           COConfigurationManager.setParameter("mtv.eta.show_absolute", MyTorrentsView.eta_absolute);
/*      */         }
/*      */       });
/* 1594 */     } else if (sColumnName.equals("ProgressETA")) {
/* 1595 */       final MenuItem item = new MenuItem(menuThisColumn, 32);
/* 1596 */       Messages.setLanguageText(item, "MyTorrentsView.menu.eta.abs");
/* 1597 */       item.setSelection(progress_eta_absolute);
/*      */       
/* 1599 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/* 1601 */           MyTorrentsView.progress_eta_absolute = item.getSelection();
/* 1602 */           MyTorrentsView.this.tv.columnInvalidate("ProgressETA");
/* 1603 */           MyTorrentsView.this.tv.refreshTable(false);
/* 1604 */           COConfigurationManager.setParameter("mtv.progress_eta.show_absolute", MyTorrentsView.progress_eta_absolute);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public void fillMenu(String sColumnName, Menu menu)
/*      */   {
/* 1612 */     Object[] dataSources = this.tv.getSelectedDataSources(true);
/* 1613 */     DownloadManager[] dms = getSelectedDownloads();
/*      */     
/* 1615 */     if ((dms.length == 0) && (dataSources.length > 0)) {
/* 1616 */       List<DiskManagerFileInfo> listFileInfos = new ArrayList();
/* 1617 */       DownloadManager firstFileDM = null;
/* 1618 */       for (Object ds : dataSources) {
/* 1619 */         if ((ds instanceof DiskManagerFileInfo)) {
/* 1620 */           DiskManagerFileInfo info = (DiskManagerFileInfo)ds;
/*      */           
/* 1622 */           if ((firstFileDM != null) && (!firstFileDM.equals(info.getDownloadManager()))) {
/*      */             break;
/*      */           }
/* 1625 */           firstFileDM = info.getDownloadManager();
/* 1626 */           listFileInfos.add(info);
/*      */         }
/*      */       }
/* 1629 */       if (listFileInfos.size() > 0) {
/* 1630 */         FilesViewMenuUtil.fillMenu(this.tv, menu, new DownloadManager[] { firstFileDM }, new DiskManagerFileInfo[][] { (DiskManagerFileInfo[])listFileInfos.toArray(new DiskManagerFileInfo[0]) });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1635 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1639 */     boolean hasSelection = dms.length > 0;
/*      */     
/* 1641 */     if (hasSelection) {
/* 1642 */       boolean isSeedingView = (Download.class.equals(this.forDataSourceType)) || (DownloadTypeComplete.class.equals(this.forDataSourceType));
/* 1643 */       TorrentUtil.fillTorrentMenu(menu, dms, this.azureus_core, this.cTablePanel, true, isSeedingView ? 2 : 1, this.tv);
/*      */       
/*      */ 
/*      */ 
/* 1647 */       new MenuItem(menu, 2);
/*      */     }
/*      */   }
/*      */   
/*      */   private void createDragDrop()
/*      */   {
/*      */     try {
/* 1654 */       Transfer[] types = { TextTransfer.getInstance() };
/*      */       
/* 1656 */       if ((this.dragSource != null) && (!this.dragSource.isDisposed())) {
/* 1657 */         this.dragSource.dispose();
/*      */       }
/*      */       
/* 1660 */       if ((this.dropTarget != null) && (!this.dropTarget.isDisposed())) {
/* 1661 */         this.dropTarget.dispose();
/*      */       }
/*      */       
/* 1664 */       this.dragSource = this.tv.createDragSource(3);
/* 1665 */       if (this.dragSource != null) {
/* 1666 */         this.dragSource.setTransfer(types);
/* 1667 */         this.dragSource.addDragListener(new DragSourceAdapter() {
/*      */           private String eventData;
/*      */           
/*      */           public void dragStart(DragSourceEvent event) {
/* 1671 */             TableRowCore[] rows = MyTorrentsView.this.tv.getSelectedRows();
/* 1672 */             if (rows.length != 0) {
/* 1673 */               event.doit = true;
/*      */               
/* 1675 */               MyTorrentsView.this.drag_drop_line_start = rows[0].getIndex();
/* 1676 */               MyTorrentsView.this.drag_drop_rows = rows;
/*      */             } else {
/* 1678 */               event.doit = false;
/* 1679 */               MyTorrentsView.this.drag_drop_line_start = -1;
/* 1680 */               MyTorrentsView.this.drag_drop_rows = null;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1685 */             boolean onlyDMs = true;
/* 1686 */             StringBuilder sb = new StringBuilder();
/* 1687 */             Object[] selectedDataSources = MyTorrentsView.this.tv.getSelectedDataSources(true);
/* 1688 */             for (Object ds : selectedDataSources) {
/* 1689 */               if ((ds instanceof DownloadManager)) {
/* 1690 */                 DownloadManager dm = (DownloadManager)ds;
/* 1691 */                 TOTorrent torrent = dm.getTorrent();
/* 1692 */                 if (torrent != null) {
/*      */                   try {
/* 1694 */                     sb.append(torrent.getHashWrapper().toBase32String());
/* 1695 */                     sb.append('\n');
/*      */                   }
/*      */                   catch (TOTorrentException e) {}
/*      */                 }
/* 1699 */               } else if ((ds instanceof DiskManagerFileInfo)) {
/* 1700 */                 DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 1701 */                 DownloadManager dm = fileInfo.getDownloadManager();
/* 1702 */                 TOTorrent torrent = dm.getTorrent();
/* 1703 */                 if (torrent != null) {
/*      */                   try {
/* 1705 */                     sb.append(torrent.getHashWrapper().toBase32String());
/* 1706 */                     sb.append(';');
/* 1707 */                     sb.append(fileInfo.getIndex());
/* 1708 */                     sb.append('\n');
/* 1709 */                     onlyDMs = false;
/*      */                   }
/*      */                   catch (TOTorrentException e) {}
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1716 */             this.eventData = ((onlyDMs ? "DownloadManager\n" : "DiskManagerFileInfo\n") + sb.toString());
/*      */           }
/*      */           
/*      */           public void dragSetData(DragSourceEvent event)
/*      */           {
/* 1721 */             event.data = this.eventData;
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1726 */       this.dropTarget = this.tv.createDropTarget(31);
/*      */       
/* 1728 */       if (this.dropTarget != null) {
/* 1729 */         this.dropTarget.setTransfer(new Transfer[] { org.eclipse.swt.dnd.HTMLTransfer.getInstance(), URLTransfer.getInstance(), org.eclipse.swt.dnd.FileTransfer.getInstance(), TextTransfer.getInstance() });
/*      */         
/*      */ 
/*      */ 
/* 1733 */         this.dropTarget.addDropListener(new DropTargetAdapter() {
/* 1734 */           Point enterPoint = null;
/*      */           
/* 1736 */           public void dropAccept(DropTargetEvent event) { event.currentDataType = URLTransfer.pickBestType(event.dataTypes, event.currentDataType); }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void dragEnter(DropTargetEvent event)
/*      */           {
/* 1743 */             if (MyTorrentsView.this.drag_drop_line_start < 0) {
/* 1744 */               if (event.detail != 1) {
/* 1745 */                 if ((event.operations & 0x4) > 0) {
/* 1746 */                   event.detail = 4;
/* 1747 */                 } else if ((event.operations & 0x1) > 0)
/* 1748 */                   event.detail = 1;
/*      */               }
/* 1750 */             } else if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
/*      */             {
/* 1752 */               event.detail = (MyTorrentsView.this.tv.getTableRowWithCursor() == null ? 0 : 2);
/* 1753 */               event.feedback = 8;
/* 1754 */               this.enterPoint = new Point(event.x, event.y);
/*      */             }
/*      */           }
/*      */           
/*      */           public void dragLeave(DropTargetEvent event)
/*      */           {
/* 1760 */             super.dragLeave(event);
/*      */             
/* 1762 */             MyTorrentsView.this.tv.getComposite().redraw();
/*      */           }
/*      */           
/*      */           public void dragOver(DropTargetEvent event) {
/* 1766 */             if (MyTorrentsView.this.drag_drop_line_start >= 0) {
/* 1767 */               if ((MyTorrentsView.this.drag_drop_rows.length > 0) && (!(MyTorrentsView.this.drag_drop_rows[0].getDataSource(true) instanceof DownloadManager)))
/*      */               {
/* 1769 */                 event.detail = 0;
/* 1770 */                 return;
/*      */               }
/* 1772 */               TableRowCore row = MyTorrentsView.this.tv.getTableRowWithCursor();
/* 1773 */               if ((row instanceof TableRowPainted)) {
/* 1774 */                 boolean dragging_down = row.getIndex() > MyTorrentsView.this.drag_drop_line_start;
/* 1775 */                 Rectangle bounds = ((TableRowPainted)row).getBounds();
/* 1776 */                 MyTorrentsView.this.tv.getComposite().redraw();
/* 1777 */                 MyTorrentsView.this.tv.getComposite().update();
/* 1778 */                 GC gc = new GC(MyTorrentsView.this.tv.getComposite());
/* 1779 */                 gc.setLineWidth(2);
/* 1780 */                 int y_pos = bounds.y;
/* 1781 */                 if (dragging_down) {
/* 1782 */                   y_pos += bounds.height;
/*      */                 }
/* 1784 */                 gc.drawLine(bounds.x, y_pos, bounds.x + bounds.width, y_pos);
/* 1785 */                 gc.dispose();
/*      */               }
/* 1787 */               event.detail = (row == null ? 0 : 2);
/* 1788 */               event.feedback = (0x8 | ((this.enterPoint != null) && (this.enterPoint.y > event.y) ? 2 : 4));
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */           public void drop(DropTargetEvent event)
/*      */           {
/* 1795 */             if (!(event.data instanceof String)) {
/* 1796 */               TorrentOpener.openDroppedTorrents(event, true);
/* 1797 */               return;
/*      */             }
/* 1799 */             String data = (String)event.data;
/* 1800 */             if (data.startsWith("DiskManagerFileInfo\n")) {
/* 1801 */               return;
/*      */             }
/* 1803 */             if (!data.startsWith("DownloadManager\n")) {
/* 1804 */               TorrentOpener.openDroppedTorrents(event, true);
/* 1805 */               return;
/*      */             }
/*      */             
/* 1808 */             event.detail = 0;
/*      */             
/* 1810 */             if (MyTorrentsView.this.drag_drop_line_start >= 0) {
/* 1811 */               event.detail = 0;
/* 1812 */               TableRowCore row = MyTorrentsView.this.tv.getRow(event);
/* 1813 */               if (row == null)
/* 1814 */                 return;
/* 1815 */               if (row.getParentRowCore() != null) {
/* 1816 */                 row = row.getParentRowCore();
/*      */               }
/* 1818 */               int drag_drop_line_end = row.getIndex();
/* 1819 */               if (drag_drop_line_end != MyTorrentsView.this.drag_drop_line_start) {
/* 1820 */                 DownloadManager dm = (DownloadManager)row.getDataSource(true);
/* 1821 */                 MyTorrentsView.this.moveRowsTo(MyTorrentsView.this.drag_drop_rows, dm.getPosition());
/* 1822 */                 event.detail = 2;
/*      */               }
/* 1824 */               MyTorrentsView.this.drag_drop_line_start = -1;
/* 1825 */               MyTorrentsView.this.drag_drop_rows = null;
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     catch (Throwable t) {
/* 1832 */       Logger.log(new org.gudy.azureus2.core3.logging.LogEvent(LOGID, "failed to init drag-n-drop", t));
/*      */     }
/*      */   }
/*      */   
/*      */   private void moveRowsTo(TableRowCore[] rows, int iNewPos) {
/* 1837 */     if ((rows == null) || (rows.length == 0)) {
/* 1838 */       return;
/*      */     }
/*      */     
/* 1841 */     TableColumnCore sortColumn = this.tv.getSortColumn();
/* 1842 */     boolean isSortAscending = sortColumn == null ? true : sortColumn.isSortAscending();
/*      */     
/*      */ 
/* 1845 */     for (int i = 0; i < rows.length; i++) {
/* 1846 */       TableRowCore row = rows[i];
/* 1847 */       Object ds = row.getDataSource(true);
/* 1848 */       if ((ds instanceof DownloadManager))
/*      */       {
/*      */ 
/* 1851 */         DownloadManager dm = (DownloadManager)ds;
/* 1852 */         int iOldPos = dm.getPosition();
/* 1853 */         this.globalManager.moveTo(dm, iNewPos);
/* 1854 */         if (isSortAscending) {
/* 1855 */           if (iOldPos > iNewPos) {
/* 1856 */             iNewPos++;
/*      */           }
/* 1858 */         } else if (iOldPos < iNewPos) {
/* 1859 */           iNewPos--;
/*      */         }
/*      */       }
/*      */     }
/* 1863 */     boolean bForceSort = sortColumn == null ? false : sortColumn.getName().equals("#");
/* 1864 */     this.tv.columnInvalidate("#");
/* 1865 */     this.tv.refreshTable(bForceSort);
/*      */   }
/*      */   
/*      */   public void tableRefresh()
/*      */   {
/* 1870 */     if (this.tv.isDisposed()) {
/* 1871 */       return;
/*      */     }
/* 1873 */     refreshTorrentMenu();
/*      */   }
/*      */   
/*      */ 
/*      */   public void keyPressed(KeyEvent e)
/*      */   {
/* 1879 */     this.viewActive = true;
/* 1880 */     int key = e.character;
/* 1881 */     if ((key <= 26) && (key > 0)) {
/* 1882 */       key += 96;
/*      */     }
/* 1884 */     if (e.stateMask == 393216)
/*      */     {
/* 1886 */       if (key == 115) {
/* 1887 */         ManagerUtils.asyncStopAll();
/* 1888 */         e.doit = false;
/* 1889 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1894 */       if (e.keyCode == 16777218) {
/* 1895 */         moveSelectedTorrents(10);
/* 1896 */         e.doit = false;
/* 1897 */         return;
/*      */       }
/*      */       
/* 1900 */       if (e.keyCode == 16777217) {
/* 1901 */         moveSelectedTorrents(-10);
/* 1902 */         e.doit = false;
/* 1903 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1907 */     if (e.stateMask == SWT.MOD1) {
/* 1908 */       switch (key) {
/*      */       case 97: 
/* 1910 */         if (e.widget != this.txtFilter) {
/* 1911 */           this.tv.selectAll();
/* 1912 */           e.doit = false;
/*      */         }
/*      */         break;
/*      */       case 99: 
/* 1916 */         if (e.widget != this.txtFilter) {
/* 1917 */           this.tv.clipboardSelected();
/* 1918 */           e.doit = false;
/*      */         }
/*      */         break;
/*      */       case 105: 
/* 1922 */         showSelectedDetails();
/* 1923 */         e.doit = false;
/*      */       }
/*      */       
/*      */       
/* 1927 */       if (!e.doit) {
/* 1928 */         return;
/*      */       }
/*      */     }
/* 1931 */     if (e.stateMask == 262144) {
/* 1932 */       switch (e.keyCode) {
/*      */       case 16777217: 
/* 1934 */         moveSelectedTorrentsUp();
/* 1935 */         e.doit = false;
/* 1936 */         break;
/*      */       case 16777218: 
/* 1938 */         moveSelectedTorrentsDown();
/* 1939 */         e.doit = false;
/* 1940 */         break;
/*      */       case 16777223: 
/* 1942 */         moveSelectedTorrentsTop();
/* 1943 */         e.doit = false;
/* 1944 */         break;
/*      */       case 16777224: 
/* 1946 */         moveSelectedTorrentsEnd();
/* 1947 */         e.doit = false;
/*      */       }
/*      */       
/* 1950 */       if (!e.doit) {
/* 1951 */         return;
/*      */       }
/* 1953 */       switch (key) {
/*      */       case 114: 
/* 1955 */         TorrentUtil.resumeTorrents(this.tv.getSelectedDataSources().toArray());
/* 1956 */         e.doit = false;
/* 1957 */         break;
/*      */       case 115: 
/* 1959 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 1961 */             TorrentUtil.stopDataSources(MyTorrentsView.this.tv.getSelectedDataSources().toArray());
/*      */           }
/* 1963 */         });
/* 1964 */         e.doit = false;
/*      */       }
/*      */       
/*      */       
/* 1968 */       if (!e.doit) {
/* 1969 */         return;
/*      */       }
/*      */     }
/* 1972 */     if ((e.keyCode == 16777227) && ((e.stateMask & SWT.MODIFIER_MASK) == 0)) {
/* 1973 */       FilesViewMenuUtil.rename(this.tv, this.tv.getSelectedDataSources(true), true, false);
/* 1974 */       e.doit = false;
/* 1975 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1980 */     if ((e.stateMask == 0) && (e.keyCode == 127) && (e.widget != this.txtFilter)) {
/* 1981 */       Utils.getOffOfSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/* 1983 */           TorrentUtil.removeDataSources(MyTorrentsView.this.tv.getSelectedDataSources().toArray());
/*      */         }
/* 1985 */       });
/* 1986 */       e.doit = false;
/* 1987 */       return;
/*      */     }
/*      */     
/* 1990 */     if ((e.keyCode != 8) && (
/* 1991 */       ((e.stateMask & 0xFFFDFFFF) != 0) || (e.character < ' '))) {}
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
/*      */   private void moveSelectedTorrentsDown()
/*      */   {
/* 2006 */     DownloadManager[] dms = getSelectedDownloads();
/* 2007 */     Arrays.sort(dms, new Comparator() {
/*      */       public int compare(DownloadManager a, DownloadManager b) {
/* 2009 */         return a.getPosition() - b.getPosition();
/*      */       }
/*      */     });
/* 2012 */     for (int i = dms.length - 1; i >= 0; i--) {
/* 2013 */       DownloadManager dm = dms[i];
/* 2014 */       if (dm.getGlobalManager().isMoveableDown(dm)) {
/* 2015 */         dm.getGlobalManager().moveDown(dm);
/*      */       }
/*      */     }
/*      */     
/* 2019 */     boolean bForceSort = this.tv.getSortColumn().getName().equals("#");
/* 2020 */     this.tv.columnInvalidate("#");
/* 2021 */     this.tv.refreshTable(bForceSort);
/*      */   }
/*      */   
/*      */   private void moveSelectedTorrentsUp()
/*      */   {
/* 2026 */     DownloadManager[] dms = getSelectedDownloads();
/* 2027 */     Arrays.sort(dms, new Comparator() {
/*      */       public int compare(DownloadManager a, DownloadManager b) {
/* 2029 */         return a.getPosition() - b.getPosition();
/*      */       }
/*      */     });
/* 2032 */     for (int i = 0; i < dms.length; i++) {
/* 2033 */       DownloadManager dm = dms[i];
/* 2034 */       if (dm.getGlobalManager().isMoveableUp(dm)) {
/* 2035 */         dm.getGlobalManager().moveUp(dm);
/*      */       }
/*      */     }
/*      */     
/* 2039 */     boolean bForceSort = this.tv.getSortColumn().getName().equals("#");
/* 2040 */     this.tv.columnInvalidate("#");
/* 2041 */     this.tv.refreshTable(bForceSort);
/*      */   }
/*      */   
/*      */   private void moveSelectedTorrents(int by)
/*      */   {
/* 2046 */     DownloadManager[] dms = getSelectedDownloads();
/* 2047 */     if (dms.length <= 0) {
/* 2048 */       return;
/*      */     }
/* 2050 */     int[] newPositions = new int[dms.length];
/*      */     
/* 2052 */     if (by < 0) {
/* 2053 */       Arrays.sort(dms, new Comparator() {
/*      */         public int compare(DownloadManager a, DownloadManager b) {
/* 2055 */           return a.getPosition() - b.getPosition();
/*      */         }
/*      */       });
/*      */     } else {
/* 2059 */       Arrays.sort(dms, new Comparator() {
/*      */         public int compare(DownloadManager a, DownloadManager b) {
/* 2061 */           return b.getPosition() - a.getPosition();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 2066 */     for (int i = 0; i < dms.length; i++) {
/* 2067 */       DownloadManager dm = dms[i];
/* 2068 */       boolean complete = dm.isDownloadComplete(false);
/* 2069 */       int count = this.globalManager.downloadManagerCount(complete);
/* 2070 */       int pos = dm.getPosition() + by;
/* 2071 */       if (pos < i + 1) {
/* 2072 */         pos = i + 1;
/* 2073 */       } else if (pos > count - i) {
/* 2074 */         pos = count - i;
/*      */       }
/* 2076 */       newPositions[i] = pos;
/*      */     }
/*      */     
/* 2079 */     for (int i = 0; i < dms.length; i++) {
/* 2080 */       DownloadManager dm = dms[i];
/* 2081 */       this.globalManager.moveTo(dm, newPositions[i]);
/*      */     }
/*      */     
/* 2084 */     boolean bForceSort = this.tv.getSortColumn().getName().equals("#");
/* 2085 */     this.tv.columnInvalidate("#");
/* 2086 */     this.tv.refreshTable(bForceSort);
/*      */   }
/*      */   
/*      */   private void moveSelectedTorrentsTop() {
/* 2090 */     moveSelectedTorrentsTopOrEnd(true);
/*      */   }
/*      */   
/*      */   private void moveSelectedTorrentsEnd() {
/* 2094 */     moveSelectedTorrentsTopOrEnd(false);
/*      */   }
/*      */   
/*      */   private void moveSelectedTorrentsTopOrEnd(boolean moveToTop) {
/* 2098 */     DownloadManager[] dms = getSelectedDownloads();
/* 2099 */     if (dms.length == 0) {
/* 2100 */       return;
/*      */     }
/* 2102 */     if (moveToTop) {
/* 2103 */       this.globalManager.moveTop(dms);
/*      */     } else {
/* 2105 */       this.globalManager.moveEnd(dms);
/*      */     }
/* 2107 */     boolean bForceSort = this.tv.getSortColumn().getName().equals("#");
/* 2108 */     if (bForceSort) {
/* 2109 */       this.tv.columnInvalidate("#");
/* 2110 */       this.tv.refreshTable(bForceSort);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void parameterChanged(String parameterName)
/*      */   {
/* 2119 */     if ((parameterName == null) || (parameterName.equals("DND Always In Incomplete"))) {
/* 2120 */       this.bDNDalwaysIncomplete = COConfigurationManager.getBooleanParameter("DND Always In Incomplete");
/*      */     }
/*      */     
/* 2123 */     if ((parameterName != null) && ((parameterName.equals("Library.ShowCatButtons")) || (parameterName.equals("Library.ShowTagButtons")) || (parameterName.equals("Library.ShowTagButtons.CompOnly"))))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2128 */       createTabs();
/*      */     }
/*      */   }
/*      */   
/*      */   private MdiEntrySWT getActiveView() {
/* 2133 */     TableViewSWT_TabsCommon tabsCommon = this.tv.getTabsCommon();
/* 2134 */     if (tabsCommon != null) {
/* 2135 */       return tabsCommon.getActiveSubView();
/*      */     }
/* 2137 */     return null;
/*      */   }
/*      */   
/*      */   public void refreshToolBarItems(Map<String, Long> list) {
/* 2141 */     com.aelitis.azureus.ui.selectedcontent.ISelectedContent[] datasource = SelectedContentManager.getCurrentlySelectedContent();
/*      */     
/* 2143 */     if (!isTableFocus()) {
/* 2144 */       UISWTViewCore active_view = getActiveView();
/* 2145 */       if (active_view != null) {
/* 2146 */         UIPluginViewToolBarListener l = active_view.getToolBarListener();
/* 2147 */         if (l != null) {
/* 2148 */           Map<String, Long> activeViewList = new java.util.HashMap();
/* 2149 */           l.refreshToolBarItems(activeViewList);
/* 2150 */           if (activeViewList.size() > 0) {
/* 2151 */             list.putAll(activeViewList);
/* 2152 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/* 2160 */     boolean isTableSelected = false;
/* 2161 */     if ((this.tv instanceof TableViewImpl)) {
/* 2162 */       isTableSelected = ((TableViewImpl)this.tv).isTableSelected();
/*      */     }
/* 2164 */     if (!isTableSelected) {
/* 2165 */       UISWTViewCore active_view = getActiveView();
/* 2166 */       if (active_view != null) {
/* 2167 */         UIPluginViewToolBarListener l = active_view.getToolBarListener();
/* 2168 */         if ((l != null) && (l.toolBarItemActivated(item, activationType, datasource))) {
/* 2169 */           return true;
/*      */         }
/*      */       }
/* 2172 */       return false;
/*      */     }
/*      */     
/* 2175 */     String itemKey = item.getID();
/* 2176 */     if (activationType == 1L) {
/* 2177 */       if (itemKey.equals("up")) {
/* 2178 */         moveSelectedTorrentsTop();
/* 2179 */         return true;
/*      */       }
/* 2181 */       if (itemKey.equals("down")) {
/* 2182 */         moveSelectedTorrentsEnd();
/* 2183 */         return true;
/*      */       }
/* 2185 */       return false;
/*      */     }
/*      */     
/* 2188 */     if (activationType != 0L) {
/* 2189 */       return false;
/*      */     }
/* 2191 */     if (itemKey.equals("top")) {
/* 2192 */       moveSelectedTorrentsTop();
/* 2193 */       return true;
/*      */     }
/* 2195 */     if (itemKey.equals("bottom")) {
/* 2196 */       moveSelectedTorrentsEnd();
/* 2197 */       return true;
/*      */     }
/* 2199 */     if (itemKey.equals("up")) {
/* 2200 */       moveSelectedTorrentsUp();
/* 2201 */       return true;
/*      */     }
/* 2203 */     if (itemKey.equals("down")) {
/* 2204 */       moveSelectedTorrentsDown();
/* 2205 */       return true;
/*      */     }
/* 2207 */     if (itemKey.equals("run")) {
/* 2208 */       TorrentUtil.runDataSources(this.tv.getSelectedDataSources().toArray());
/* 2209 */       return true;
/*      */     }
/* 2211 */     if (itemKey.equals("start")) {
/* 2212 */       TorrentUtil.queueDataSources(this.tv.getSelectedDataSources().toArray(), true);
/* 2213 */       return true;
/*      */     }
/* 2215 */     if (itemKey.equals("stop")) {
/* 2216 */       TorrentUtil.stopDataSources(this.tv.getSelectedDataSources().toArray());
/* 2217 */       return true;
/*      */     }
/* 2219 */     if (itemKey.equals("startstop")) {
/* 2220 */       TorrentUtil.stopOrStartDataSources(this.tv.getSelectedDataSources().toArray());
/* 2221 */       return true;
/*      */     }
/* 2223 */     if (itemKey.equals("remove")) {
/* 2224 */       TorrentUtil.removeDataSources(this.tv.getSelectedDataSources().toArray());
/* 2225 */       return true;
/*      */     }
/* 2227 */     return false;
/*      */   }
/*      */   
/*      */   public void stateChanged(DownloadManager manager, int state)
/*      */   {
/* 2232 */     final TableRowCore row = this.tv.getRow(manager);
/* 2233 */     if (row != null) {
/* 2234 */       Utils.getOffOfSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/* 2236 */           row.refresh(true);
/* 2237 */           if (row.isSelected()) {
/* 2238 */             MyTorrentsView.this.updateSelectedContentRateLimited();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public void positionChanged(DownloadManager download, int oldPosition, int newPosition)
/*      */   {
/* 2247 */     if (isOurDownloadManager(download)) {
/* 2248 */       Utils.execSWTThreadLater(0, new AERunnable() {
/*      */         public void runSupport() {
/* 2250 */           MyTorrentsView.this.updateSelectedContent();
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void completionChanged(DownloadManager manager, boolean bCompleted)
/*      */   {
/* 2265 */     if (isOurDownloadManager(manager)) {
/* 2266 */       this.tv.addDataSource(manager);
/*      */     }
/*      */     else {
/* 2269 */       this.tv.removeDataSource(manager);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tagEventOccurred(TagTypeListener.TagEvent event)
/*      */   {
/* 2280 */     int type = event.getEventType();
/* 2281 */     Tag tag = event.getTag();
/* 2282 */     if (type == 0) {
/* 2283 */       tagAdded(tag);
/* 2284 */     } else if (type == 1) {
/* 2285 */       tagChanged(tag);
/* 2286 */     } else if (type == 2) {
/* 2287 */       tagRemoved(tag);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tagAdded(Tag tag)
/*      */   {
/* 2299 */     createTabs();
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
/*      */   public void tagChanged(Tag tag)
/*      */   {
/* 2312 */     synchronized (this.pending_tag_changes)
/*      */     {
/* 2314 */       this.pending_tag_changes.add(tag);
/*      */     }
/*      */     
/* 2317 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/* 2321 */         if (MyTorrentsView.this.allTags != null)
/*      */         {
/* 2323 */           boolean create_tabs = false;
/*      */           
/* 2325 */           synchronized (MyTorrentsView.this.pending_tag_changes)
/*      */           {
/* 2327 */             for (Tag t : MyTorrentsView.this.pending_tag_changes)
/*      */             {
/* 2329 */               boolean should_be_visible = t.isVisible();
/* 2330 */               boolean is_visible = MyTorrentsView.this.allTags.contains(t);
/*      */               
/* 2332 */               if (should_be_visible != is_visible)
/*      */               {
/* 2334 */                 create_tabs = true;
/*      */                 
/* 2336 */                 break;
/*      */               }
/*      */             }
/*      */             
/* 2340 */             MyTorrentsView.this.pending_tag_changes.clear();
/*      */           }
/*      */           
/* 2343 */           if (create_tabs)
/*      */           {
/* 2345 */             MyTorrentsView.this.createTabs();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void tagRemoved(Tag tag)
/*      */   {
/* 2356 */     if (this.currentTags == null) {
/* 2357 */       return;
/*      */     }
/*      */     
/* 2360 */     removeTagFromCurrent(tag);
/* 2361 */     createTabs();
/*      */   }
/*      */   
/*      */   private void removeTagFromCurrent(Tag tag)
/*      */   {
/* 2366 */     boolean found = false;
/* 2367 */     for (int i = 0; i < this.currentTags.length; i++) {
/* 2368 */       Tag curTag = this.currentTags[i];
/* 2369 */       if (curTag.equals(tag)) { Tag[] tags;
/*      */         Tag[] tags;
/* 2371 */         if (this.currentTags.length == 1) {
/* 2372 */           tags = new Tag[] { CategoryManager.getCategory(1) };
/*      */         }
/*      */         else
/*      */         {
/* 2376 */           tags = new Tag[this.currentTags.length - 1];
/* 2377 */           if (i > 0) {
/* 2378 */             System.arraycopy(this.currentTags, 0, tags, 0, i);
/*      */           }
/* 2380 */           if (tags.length - i > 0) {
/* 2381 */             System.arraycopy(this.currentTags, i + 1, tags, 0, tags.length - i);
/*      */           }
/*      */         }
/*      */         
/* 2385 */         setCurrentTags(tags);
/* 2386 */         found = true;
/* 2387 */         break;
/*      */       }
/*      */     }
/*      */     
/* 2391 */     if (!found)
/*      */     {
/*      */ 
/* 2394 */       setCurrentTags(this.currentTags);
/*      */     }
/*      */   }
/*      */   
/*      */   private Object[] removeFromArray(Object[] array, Object o) {
/* 2399 */     for (int i = 0; i < array.length; i++) {
/* 2400 */       Object cur = array[i];
/* 2401 */       if (cur.equals(o)) {
/* 2402 */         Tag[] newArray = new Tag[array.length - 1];
/* 2403 */         if (i > 0) {
/* 2404 */           System.arraycopy(array, 0, newArray, 0, i);
/*      */         }
/* 2406 */         if (newArray.length - i > 0) {
/* 2407 */           System.arraycopy(array, i + 1, newArray, 0, newArray.length - i);
/*      */         }
/*      */         
/* 2410 */         return newArray;
/*      */       }
/*      */     }
/*      */     
/* 2414 */     return array;
/*      */   }
/*      */   
/*      */ 
/*      */   public Tag[] getCurrentTags()
/*      */   {
/* 2420 */     return this.currentTags;
/*      */   }
/*      */   
/*      */   protected void setCurrentTags(Tag[] tags) {
/* 2424 */     if (this.currentTags != null) {
/* 2425 */       for (Tag tag : this.currentTags) {
/* 2426 */         tag.removeTagListener(this);
/*      */       }
/*      */     }
/*      */     
/* 2430 */     this.currentTags = tags;
/* 2431 */     if (this.currentTags != null) {
/* 2432 */       Set<Tag> to_remove = null;
/* 2433 */       for (Tag tag : this.currentTags) {
/* 2434 */         if (tag.getTaggableTypes() != 2)
/*      */         {
/*      */ 
/* 2437 */           if (to_remove == null) {
/* 2438 */             to_remove = new HashSet();
/*      */           }
/* 2440 */           to_remove.add(tag);
/*      */         } else {
/* 2442 */           tag.addTagListener(this, false);
/*      */         }
/*      */       }
/* 2445 */       if (to_remove != null) {
/* 2446 */         Tag[] updated_tags = new Tag[this.currentTags.length - to_remove.size()];
/*      */         
/* 2448 */         int pos = 0;
/* 2449 */         for (Tag tag : this.currentTags) {
/* 2450 */           if (!to_remove.contains(tag)) {
/* 2451 */             updated_tags[(pos++)] = tag;
/*      */           }
/*      */         }
/* 2454 */         this.currentTags = updated_tags;
/*      */       }
/*      */     }
/*      */     
/* 2458 */     this.tv.processDataSourceQueue();
/* 2459 */     Object[] managers = this.globalManager.getDownloadManagers().toArray();
/* 2460 */     List<DownloadManager> listRemoves = new ArrayList();
/* 2461 */     List<DownloadManager> listAdds = new ArrayList();
/*      */     
/* 2463 */     for (int i = 0; i < managers.length; i++) {
/* 2464 */       DownloadManager dm = (DownloadManager)managers[i];
/*      */       
/* 2466 */       boolean bHave = this.tv.isUnfilteredDataSourceAdded(dm);
/* 2467 */       if (!isOurDownloadManager(dm)) {
/* 2468 */         if (bHave) {
/* 2469 */           listRemoves.add(dm);
/*      */         }
/*      */       }
/* 2472 */       else if (!bHave) {
/* 2473 */         listAdds.add(dm);
/*      */       }
/*      */     }
/*      */     
/* 2477 */     this.tv.removeDataSources(listRemoves.toArray(new DownloadManager[0]));
/* 2478 */     this.tv.addDataSources(listAdds.toArray(new DownloadManager[0]));
/*      */     
/* 2480 */     this.tv.processDataSourceQueue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isInTags(DownloadManager manager, Tag[] tags)
/*      */   {
/* 2489 */     if (tags == null) {
/* 2490 */       return true;
/*      */     }
/*      */     
/* 2493 */     if (this.currentTagsAny) {
/* 2494 */       for (Tag tag : tags) {
/* 2495 */         if (tag.hasTaggable(manager)) {
/* 2496 */           return true;
/*      */         }
/*      */       }
/* 2499 */       return false;
/*      */     }
/* 2501 */     for (Tag tag : tags) {
/* 2502 */       if (!tag.hasTaggable(manager)) {
/* 2503 */         return false;
/*      */       }
/*      */     }
/* 2506 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isInCurrentTag(DownloadManager manager)
/*      */   {
/* 2514 */     return isInTags(manager, this.currentTags);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void taggableAdded(Tag tag, Taggable tagged)
/*      */   {
/* 2522 */     DownloadManager manager = (DownloadManager)tagged;
/*      */     
/* 2524 */     if (isOurDownloadManager(manager))
/*      */     {
/* 2526 */       this.tv.addDataSource(manager);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void taggableSync(Tag tag)
/*      */   {
/* 2536 */     List<DownloadManager> dataSources = this.tv.getDataSources();
/*      */     
/* 2538 */     for (DownloadManager dm : dataSources)
/*      */     {
/* 2540 */       if (!isOurDownloadManager(dm))
/*      */       {
/* 2542 */         this.tv.removeDataSource(dm);
/*      */       }
/*      */     }
/*      */     
/* 2546 */     for (Taggable t : tag.getTagged())
/*      */     {
/* 2548 */       DownloadManager manager = (DownloadManager)t;
/*      */       
/* 2550 */       if ((isOurDownloadManager(manager)) && (!this.tv.dataSourceExists(manager)))
/*      */       {
/* 2552 */         this.tv.addDataSource(manager);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void taggableRemoved(Tag tag, Taggable tagged)
/*      */   {
/* 2562 */     DownloadManager manager = (DownloadManager)tagged;
/*      */     
/* 2564 */     this.tv.removeDataSource(manager);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void downloadManagerAdded(DownloadManager dm)
/*      */   {
/* 2572 */     dm.addListener(this);
/* 2573 */     if (isOurDownloadManager(dm)) {
/* 2574 */       this.tv.addDataSource(dm);
/*      */     }
/*      */   }
/*      */   
/*      */   public void downloadManagerRemoved(DownloadManager dm)
/*      */   {
/* 2580 */     dm.removeListener(this);
/* 2581 */     org.gudy.azureus2.ui.swt.minibar.DownloadBar.close(dm);
/* 2582 */     this.tv.removeDataSource(dm);
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
/*      */   public void updateLanguage()
/*      */   {
/* 2595 */     super.updateLanguage();
/* 2596 */     getComposite().layout(true, true);
/*      */   }
/*      */   
/*      */   public boolean isTableFocus() {
/* 2600 */     return this.viewActive;
/*      */   }
/*      */   
/*      */   public Image obfusticatedImage(Image image)
/*      */   {
/* 2605 */     return this.tv.obfusticatedImage(image);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2615 */   private static boolean registeredCoreSubViews = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TableViewSWT<DownloadManager> createTableView(Class<?> forDataSourceType, String tableID, TableColumnCore[] basicItems)
/*      */   {
/* 2623 */     int tableExtraStyle = COConfigurationManager.getIntParameter("MyTorrentsView.table.style");
/* 2624 */     TableViewSWT<DownloadManager> table = org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory.createTableViewSWT(forDataSourceType, tableID, getPropertiesPrefix(), basicItems, "#", tableExtraStyle | 0x2 | 0x10000 | 0x10000000 | 0x40);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2631 */     boolean enable_tab_views = (!Utils.isAZ2UI()) && (this.supportsTabs) && (COConfigurationManager.getBooleanParameter("Library.ShowTabsInTorrentView"));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2636 */     List<String> restrictTo = new ArrayList();
/* 2637 */     restrictTo.addAll(Arrays.asList(new String[] { "GeneralView", "TrackerView", "PeersView", PeersGraphicView.MSGID_PREFIX, "PiecesView", "DownloadActivityView", "PieceInfoView", "FilesView", "TaggingView", "PrivacyView" }));
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
/* 2651 */     restrictTo.add("azbuddy.ui.menu.chat");
/* 2652 */     PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/* 2653 */     PluginInterface pi = pm.getPluginInterfaceByID("aercm", true);
/*      */     
/* 2655 */     if (pi != null) {
/* 2656 */       String pluginInfo = pi.getPluginconfig().getPluginStringParameter("plugin.info", "");
/*      */       
/* 2658 */       if (pluginInfo.equals("e")) {
/* 2659 */         restrictTo.add("rcm.subview.torrentdetails.name");
/*      */       }
/*      */     }
/*      */     
/* 2663 */     if (Logger.isEnabled())
/*      */     {
/* 2665 */       restrictTo.add("ConsoleView");
/*      */     }
/*      */     
/* 2668 */     table.setEnableTabViews(enable_tab_views, false, (String[])restrictTo.toArray(new String[0]));
/*      */     
/*      */ 
/* 2671 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 2672 */     if (uiFunctions != null) {
/* 2673 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*      */       
/* 2675 */       registerPluginViews(pluginUI);
/*      */     }
/*      */     
/* 2678 */     return table;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void registerPluginViews(UISWTInstance pluginUI)
/*      */   {
/* 2685 */     if ((pluginUI != null) && (!registeredCoreSubViews))
/*      */     {
/* 2687 */       String[] views_with_tabs = { "MyLibrary.big", "MyTorrents", "MyTorrents.big", "MySeeders" };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2694 */       boolean hasTags = false;
/*      */       try
/*      */       {
/* 2697 */         hasTags = TagManagerFactory.getTagManager().getTagType(3).getTags().size() > 0;
/*      */       }
/*      */       catch (Throwable t) {}
/*      */       
/* 2701 */       for (String id : views_with_tabs)
/*      */       {
/* 2703 */         pluginUI.addView(id, "GeneralView", GeneralView.class, null);
/* 2704 */         pluginUI.addView(id, "TrackerView", TrackerView.class, null);
/* 2705 */         pluginUI.addView(id, "PeersView", PeersView.class, null);
/* 2706 */         pluginUI.addView(id, PeersGraphicView.MSGID_PREFIX, PeersGraphicView.class, null);
/* 2707 */         pluginUI.addView(id, "PiecesView", PiecesView.class, null);
/* 2708 */         pluginUI.addView(id, "PieceInfoView", PieceInfoView.class, null);
/* 2709 */         pluginUI.addView(id, "DownloadActivityView", DownloadActivityView.class, null);
/* 2710 */         pluginUI.addView(id, "FilesView", FilesView.class, null);
/* 2711 */         pluginUI.addView(id, "TorrentInfoView", TorrentInfoView.class, null);
/* 2712 */         pluginUI.addView(id, "TorrentOptionsView", TorrentOptionsView.class, null);
/* 2713 */         if (hasTags) {
/* 2714 */           pluginUI.addView(id, "TaggingView", TaggingView.class, null);
/*      */         }
/* 2716 */         pluginUI.addView(id, "PrivacyView", PrivacyView.class, null);
/*      */         
/* 2718 */         if (Logger.isEnabled()) {
/* 2719 */           pluginUI.addView(id, "ConsoleView", LoggerView.class, null);
/*      */         }
/*      */       }
/*      */       
/* 2723 */       registeredCoreSubViews = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getRowDefaultHeight()
/*      */   {
/* 2732 */     return -1;
/*      */   }
/*      */   
/*      */   public void rowRefresh(TableRow row)
/*      */   {
/* 2737 */     if (!(row instanceof TableRowCore)) {
/* 2738 */       return;
/*      */     }
/*      */     
/* 2741 */     TableRowCore rowCore = (TableRowCore)row;
/* 2742 */     Object ds = rowCore.getDataSource(true);
/* 2743 */     if (!(ds instanceof DownloadManager)) {
/* 2744 */       return;
/*      */     }
/*      */     
/* 2747 */     DownloadManager dm = (DownloadManager)ds;
/* 2748 */     if ((rowCore.getSubItemCount() == 0) && (dm.getTorrent() != null) && (!dm.getTorrent().isSimpleTorrent()) && (rowCore.isVisible()) && (dm.getNumFileInfos() > 0))
/*      */     {
/*      */ 
/* 2751 */       DiskManagerFileInfoSet fileInfos = dm.getDiskManagerFileInfoSet();
/* 2752 */       if (fileInfos != null) {
/* 2753 */         DiskManagerFileInfo[] files = fileInfos.getFiles();
/* 2754 */         boolean copied = false;
/* 2755 */         int pos = 0;
/* 2756 */         for (int i = 0; i < files.length; i++) {
/* 2757 */           DiskManagerFileInfo fileInfo = files[i];
/* 2758 */           if ((!fileInfo.isSkipped()) || ((fileInfo.getStorageType() != 2) && (fileInfo.getStorageType() != 4)))
/*      */           {
/*      */ 
/*      */ 
/* 2762 */             if (pos != i) {
/* 2763 */               if (!copied)
/*      */               {
/*      */ 
/* 2766 */                 DiskManagerFileInfo[] oldFiles = files;
/* 2767 */                 files = new DiskManagerFileInfo[files.length];
/* 2768 */                 System.arraycopy(oldFiles, 0, files, 0, files.length);
/*      */                 
/* 2770 */                 copied = true;
/*      */               }
/*      */               
/* 2773 */               files[pos] = files[i];
/*      */             }
/* 2775 */             pos++;
/*      */           } }
/* 2777 */         if (pos != files.length) {
/* 2778 */           DiskManagerFileInfo[] oldFiles = files;
/* 2779 */           files = new DiskManagerFileInfo[pos];
/* 2780 */           System.arraycopy(oldFiles, 0, files, 0, pos);
/*      */         }
/* 2782 */         rowCore.setSubItems(files);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event) {
/* 2788 */     boolean b = super.eventOccurred(event);
/* 2789 */     if (event.getType() == 3) {
/* 2790 */       if (this.rebuildListOnFocusGain) {
/* 2791 */         List<?> dms = this.globalManager.getDownloadManagers();
/* 2792 */         List<DownloadManager> listAdds = new ArrayList();
/* 2793 */         List<DownloadManager> listRemoves = new ArrayList();
/* 2794 */         for (Iterator<?> iter = dms.iterator(); iter.hasNext();) {
/* 2795 */           DownloadManager dm = (DownloadManager)iter.next();
/*      */           
/* 2797 */           if (!isOurDownloadManager(dm)) {
/* 2798 */             listRemoves.add(dm);
/*      */           } else {
/* 2800 */             listAdds.add(dm);
/*      */           }
/*      */         }
/* 2803 */         this.tv.removeDataSources(listRemoves.toArray(new DownloadManager[0]));
/* 2804 */         this.tv.addDataSources(listAdds.toArray(new DownloadManager[0]));
/*      */       }
/* 2806 */       updateSelectedContent(true);
/* 2807 */     } else if (event.getType() != 4) {}
/*      */     
/* 2809 */     return b;
/*      */   }
/*      */   
/*      */   public void setRebuildListOnFocusGain(boolean rebuildListOnFocusGain) {
/* 2813 */     this.rebuildListOnFocusGain = rebuildListOnFocusGain;
/*      */   }
/*      */   
/*      */   public void rowAdded(TableRowCore row) {
/* 2817 */     if (row.getParentRowCore() == null) {
/* 2818 */       DownloadManager dm = (DownloadManager)row.getDataSource(true);
/* 2819 */       if (dm.getDownloadState().getBooleanAttribute("file.expand")) {
/* 2820 */         row.setExpanded(true);
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
/*      */ 
/*      */ 
/*      */   public void rowExpanded(TableRowCore row)
/*      */   {
/* 2835 */     if (row.getParentRowCore() == null)
/*      */     {
/* 2837 */       DownloadManager dm = (DownloadManager)row.getDataSource(true);
/*      */       
/* 2839 */       dm.getDownloadState().setBooleanAttribute("file.expand", true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void rowCollapsed(TableRowCore row)
/*      */   {
/* 2847 */     if (row.getParentRowCore() == null)
/*      */     {
/* 2849 */       DownloadManager dm = (DownloadManager)row.getDataSource(true);
/*      */       
/* 2851 */       dm.getDownloadState().setBooleanAttribute("file.expand", false);
/*      */     }
/*      */   }
/*      */   
/*      */   protected Class<?> getForDataSourceType() {
/* 2856 */     return this.forDataSourceType;
/*      */   }
/*      */   
/*      */   private boolean isCurrent(Tag tag) {
/* 2860 */     if (this.currentTags != null) {
/* 2861 */       for (Tag curTag : this.currentTags) {
/* 2862 */         if (tag.equals(curTag)) {
/* 2863 */           return true;
/*      */         }
/*      */       }
/*      */     }
/* 2867 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isCurrentTagsAny() {
/* 2871 */     return this.currentTagsAny;
/*      */   }
/*      */   
/*      */   public void setCurrentTagsAny(boolean currentTagsAny) {
/* 2875 */     if (this.currentTagsAny == currentTagsAny) {
/* 2876 */       return;
/*      */     }
/* 2878 */     this.currentTagsAny = currentTagsAny;
/* 2879 */     setCurrentTags(this.currentTags);
/*      */   }
/*      */   
/*      */   public void mouseEnter(TableRowCore row) {}
/*      */   
/*      */   public void mouseExit(TableRowCore row) {}
/*      */   
/*      */   public void keyReleased(KeyEvent e) {}
/*      */   
/*      */   public void filePriorityChanged(DownloadManager download, DiskManagerFileInfo file) {}
/*      */   
/*      */   public void downloadComplete(DownloadManager manager) {}
/*      */   
/*      */   public void tagTypeChanged(TagType tag_type) {}
/*      */   
/*      */   public void destroyInitiated() {}
/*      */   
/*      */   public void destroyed() {}
/*      */   
/*      */   public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*      */   
/*      */   public void rowRemoved(TableRowCore row) {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/MyTorrentsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */