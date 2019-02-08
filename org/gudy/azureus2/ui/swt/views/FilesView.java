/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.AZ3Functions;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*      */ import com.aelitis.azureus.core.util.RegExUtil;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck.TableViewFilterCheckEx;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.dnd.DragSource;
/*      */ import org.eclipse.swt.dnd.DragSourceAdapter;
/*      */ import org.eclipse.swt.dnd.DragSourceEvent;
/*      */ import org.eclipse.swt.dnd.FileTransfer;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.KeyListener;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Layout;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BubbleTextBox;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventImpl;
/*      */ import org.gudy.azureus2.ui.swt.views.file.FileInfoView;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.DoneItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.FileCRC32Item;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.FileETAItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.FileExtensionItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.FileIndexItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.FileReadSpeedItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.FirstPieceItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.NameItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.PercentItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.ProgressGraphItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.RelocatedItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.RemainingPiecesItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.SizeItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.StorageTypeItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.files.TorrentRelativePathItem;
/*      */ import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.AlertsItem;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*      */ 
/*      */ public class FilesView extends TableViewTab<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> implements TableDataSourceChangedListener, TableSelectionListener, org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener, com.aelitis.azureus.ui.common.table.TableRefreshListener, DownloadManagerStateAttributeListener, org.gudy.azureus2.core3.download.DownloadManagerListener, TableLifeCycleListener, TableViewFilterCheck.TableViewFilterCheckEx<org.gudy.azureus2.core3.disk.DiskManagerFileInfo>, KeyListener, ParameterListener, UISWTViewCoreEventListenerEx
/*      */ {
/*   93 */   private static boolean registeredCoreSubViews = false;
/*   94 */   boolean refreshing = false;
/*   95 */   private DragSource dragSource = null;
/*      */   
/*   97 */   private static final TableColumnCore[] basicItems = { new NameItem(), new org.gudy.azureus2.ui.swt.views.tableitems.files.PathItem(), new org.gudy.azureus2.ui.swt.views.tableitems.files.PathNameItem(), new SizeItem(), new DoneItem(), new PercentItem(), new FirstPieceItem(), new org.gudy.azureus2.ui.swt.views.tableitems.files.PieceCountItem(), new RemainingPiecesItem(), new ProgressGraphItem(), new org.gudy.azureus2.ui.swt.views.tableitems.files.ModeItem(), new org.gudy.azureus2.ui.swt.views.tableitems.files.PriorityItem(), new StorageTypeItem(), new FileExtensionItem(), new FileIndexItem(), new TorrentRelativePathItem(), new FileCRC32Item(), new org.gudy.azureus2.ui.swt.views.tableitems.files.FileMD5Item(), new org.gudy.azureus2.ui.swt.views.tableitems.files.FileSHA1Item(), new org.gudy.azureus2.ui.swt.views.tableitems.files.FileAvailabilityItem(), new AlertsItem("Files"), new FileReadSpeedItem(), new org.gudy.azureus2.ui.swt.views.tableitems.files.FileWriteSpeedItem(), new FileETAItem(), new RelocatedItem() };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final String MSGID_PREFIX = "FilesView";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  133 */   private List<DownloadManager> managers = new ArrayList();
/*      */   
/*  135 */   private boolean enable_tabs = true;
/*      */   public static boolean show_full_path;
/*      */   public boolean hide_dnd_files;
/*      */   private volatile long selection_size;
/*      */   private volatile long selection_done;
/*      */   private MenuItem path_item;
/*      */   private TableViewSWT<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> tv;
/*      */   private final boolean allowTabViews;
/*      */   private Composite cTop;
/*      */   private BubbleTextBox bubbleTextBox;
/*      */   private Button btnShowDND;
/*      */   private Label lblHeader;
/*      */   
/*      */   static
/*      */   {
/*  126 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*      */     
/*  128 */     tcManager.setDefaultColumnNames("Files", basicItems);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  144 */     COConfigurationManager.addAndFireParameterListener("FilesView.show.full.path", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  152 */         FilesView.show_full_path = COConfigurationManager.getBooleanParameter("FilesView.show.full.path");
/*      */       }
/*      */     });
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
/*  166 */   private boolean disableTableWhenEmpty = true;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public FilesView()
/*      */   {
/*  173 */     super("FilesView");
/*  174 */     this.allowTabViews = true;
/*      */   }
/*      */   
/*      */   public FilesView(boolean allowTabViews) {
/*  178 */     super("FilesView");
/*  179 */     this.allowTabViews = allowTabViews;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCloneable()
/*      */   {
/*  185 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public UISWTViewCoreEventListener getClone()
/*      */   {
/*  191 */     return new FilesView(this.allowTabViews);
/*      */   }
/*      */   
/*      */   public TableViewSWT<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> initYourTableView() {
/*  195 */     this.tv = TableViewFactory.createTableViewSWT(org.gudy.azureus2.plugins.disk.DiskManagerFileInfo.class, "Files", getPropertiesPrefix(), basicItems, "firstpiece", 268500994);
/*      */     
/*      */ 
/*      */ 
/*  199 */     if (this.allowTabViews) {
/*  200 */       this.tv.setEnableTabViews(this.enable_tabs, true, null);
/*      */     }
/*      */     
/*  203 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  204 */     if (uiFunctions != null) {
/*  205 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*      */       
/*  207 */       if ((pluginUI != null) && (!registeredCoreSubViews))
/*      */       {
/*      */         DownloadManager manager;
/*      */         DownloadManager manager;
/*  211 */         if (this.managers.size() == 1)
/*      */         {
/*  213 */           manager = (DownloadManager)this.managers.get(0);
/*      */         }
/*      */         else
/*      */         {
/*  217 */           manager = null;
/*      */         }
/*      */         
/*  220 */         pluginUI.addView("Files", "FileInfoView", FileInfoView.class, manager);
/*      */         
/*      */ 
/*  223 */         registeredCoreSubViews = true;
/*      */       }
/*      */     }
/*      */     
/*  227 */     this.tv.addTableDataSourceChangedListener(this, true);
/*  228 */     this.tv.addRefreshListener(this, true);
/*  229 */     this.tv.addSelectionListener(this, false);
/*  230 */     this.tv.addMenuFillListener(this);
/*  231 */     this.tv.addLifeCycleListener(this);
/*  232 */     this.tv.addKeyListener(this);
/*      */     
/*  234 */     return this.tv;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Composite initComposite(Composite composite)
/*      */   {
/*  242 */     Composite parent = new Composite(composite, 0);
/*  243 */     GridLayout layout = new GridLayout();
/*  244 */     layout.marginHeight = (layout.marginWidth = 0);
/*  245 */     parent.setLayout(layout);
/*      */     
/*  247 */     Layout compositeLayout = composite.getLayout();
/*  248 */     if ((compositeLayout instanceof GridLayout)) {
/*  249 */       parent.setLayoutData(new GridData(4, 4, true, true));
/*  250 */     } else if ((compositeLayout instanceof FormLayout)) {
/*  251 */       parent.setLayoutData(Utils.getFilledFormData());
/*      */     }
/*      */     
/*  254 */     this.cTop = new Composite(parent, 0);
/*      */     
/*  256 */     this.cTop.setLayoutData(new GridData(4, 1, true, false));
/*  257 */     this.cTop.setLayout(new FormLayout());
/*      */     
/*  259 */     this.btnShowDND = new Button(this.cTop, 32);
/*  260 */     Messages.setLanguageText(this.btnShowDND, "FilesView.hide.dnd");
/*  261 */     this.btnShowDND.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  263 */         COConfigurationManager.setParameter("FilesView.hide.dnd", !FilesView.this.hide_dnd_files);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  269 */     });
/*  270 */     this.hide_dnd_files = COConfigurationManager.getBooleanParameter("FilesView.hide.dnd");
/*      */     
/*  272 */     this.btnShowDND.setSelection(this.hide_dnd_files);
/*      */     
/*  274 */     this.lblHeader = new Label(this.cTop, 16777216);
/*      */     
/*  276 */     this.bubbleTextBox = new BubbleTextBox(this.cTop, 2948);
/*  277 */     this.bubbleTextBox.getTextWidget().setMessage(MessageText.getString("TorrentDetailsView.filter"));
/*      */     
/*  279 */     FormData fd = Utils.getFilledFormData();
/*  280 */     fd.left = null;
/*  281 */     this.bubbleTextBox.getParent().setLayoutData(fd);
/*      */     
/*  283 */     fd = new FormData();
/*  284 */     fd.top = new FormAttachment(this.bubbleTextBox.getParent(), 10, 16777216);
/*  285 */     fd.left = new FormAttachment(0, 0);
/*  286 */     this.btnShowDND.setLayoutData(fd);
/*      */     
/*  288 */     fd = new FormData();
/*  289 */     fd.top = new FormAttachment(this.bubbleTextBox.getParent(), 10, 16777216);
/*  290 */     fd.left = new FormAttachment(this.btnShowDND, 10);
/*  291 */     fd.right = new FormAttachment(this.bubbleTextBox.getParent(), -10);
/*  292 */     this.lblHeader.setLayoutData(fd);
/*      */     
/*  294 */     this.tv.enableFilterCheck(this.bubbleTextBox.getTextWidget(), this);
/*      */     
/*  296 */     Composite tableParent = new Composite(parent, 0);
/*      */     
/*  298 */     tableParent.setLayoutData(new GridData(4, 4, true, true));
/*  299 */     GridLayout gridLayout = new GridLayout();
/*  300 */     gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = 0);
/*  301 */     gridLayout.marginHeight = (gridLayout.marginWidth = 0);
/*  302 */     tableParent.setLayout(gridLayout);
/*      */     
/*  304 */     parent.setTabList(new org.eclipse.swt.widgets.Control[] { tableParent, this.cTop });
/*      */     
/*  306 */     return tableParent;
/*      */   }
/*      */   
/*      */ 
/*      */   public void tableDataSourceChanged(Object newDataSource)
/*      */   {
/*  312 */     List<DownloadManager> newManagers = ViewUtils.getDownloadManagersFromDataSource(newDataSource);
/*      */     
/*  314 */     if (newManagers.size() == this.managers.size()) {
/*  315 */       boolean diff = false;
/*  316 */       for (DownloadManager manager : this.managers) {
/*  317 */         if (!newManagers.contains(manager)) {
/*  318 */           diff = true;
/*  319 */           break;
/*      */         }
/*      */       }
/*  322 */       if (!diff) {
/*  323 */         if (this.disableTableWhenEmpty) {
/*  324 */           this.tv.setEnabled(this.managers.size() > 0);
/*      */         }
/*  326 */         return;
/*      */       }
/*      */     }
/*      */     
/*  330 */     for (DownloadManager manager : this.managers) {
/*  331 */       manager.getDownloadState().removeListener(this, "filelinks2", 1);
/*      */       
/*      */ 
/*  334 */       manager.removeListener(this);
/*      */     }
/*      */     
/*  337 */     this.managers = newManagers;
/*      */     
/*  339 */     for (DownloadManager manager : this.managers) {
/*  340 */       manager.getDownloadState().addListener(this, "filelinks2", 1);
/*      */       
/*      */ 
/*      */ 
/*  344 */       manager.addListener(this);
/*      */     }
/*      */     
/*  347 */     if (!this.tv.isDisposed()) {
/*  348 */       this.tv.removeAllTableRows();
/*  349 */       if (this.disableTableWhenEmpty) {
/*  350 */         this.tv.setEnabled(this.managers.size() > 0);
/*      */       }
/*  352 */       updateHeader();
/*      */     }
/*      */   }
/*      */   
/*      */   public void deselected(TableRowCore[] rows)
/*      */   {
/*  358 */     updateSelectedContent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void selected(TableRowCore[] rows)
/*      */   {
/*  367 */     updateSelectedContent();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void filePriorityChanged(DownloadManager download, org.gudy.azureus2.core3.disk.DiskManagerFileInfo file)
/*      */   {
/*  395 */     if (this.hide_dnd_files)
/*      */     {
/*  397 */       this.tv.refilter();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean filterCheck(org.gudy.azureus2.core3.disk.DiskManagerFileInfo ds, String filter, boolean regex)
/*      */   {
/*  405 */     if ((this.hide_dnd_files) && (ds.isSkipped()))
/*      */     {
/*  407 */       return false;
/*      */     }
/*      */     
/*  410 */     if ((filter == null) || (filter.length() == 0))
/*      */     {
/*  412 */       return true;
/*      */     }
/*      */     
/*  415 */     if (this.tv.getFilterControl() == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  420 */       return true;
/*      */     }
/*      */     try
/*      */     {
/*  424 */       File file = ds.getFile(true);
/*      */       
/*  426 */       String name = filter.contains(File.separator) ? file.getAbsolutePath() : file.getName();
/*      */       
/*  428 */       String s = "\\Q" + filter.replaceAll("[|;]", "\\\\E|\\\\Q") + "\\E";
/*      */       
/*  430 */       boolean match_result = true;
/*      */       
/*  432 */       if ((regex) && (s.startsWith("!")))
/*      */       {
/*  434 */         s = s.substring(1);
/*      */         
/*  436 */         match_result = false;
/*      */       }
/*      */       
/*  439 */       Pattern pattern = RegExUtil.getCachedPattern("fv:search", s, 2);
/*      */       
/*  441 */       return pattern.matcher(name).find() == match_result;
/*      */     }
/*      */     catch (Exception e) {}
/*      */     
/*  445 */     return true;
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
/*      */   public void viewChanged(TableView<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> view)
/*      */   {
/*  458 */     updateHeader();
/*      */   }
/*      */   
/*      */   public void updateSelectedContent() {
/*  462 */     long total_size = 0L;
/*  463 */     long total_done = 0L;
/*      */     
/*  465 */     Object[] dataSources = this.tv.getSelectedDataSources(true);
/*  466 */     List<SelectedContent> listSelected = new ArrayList(dataSources.length);
/*      */     
/*  468 */     for (Object ds : dataSources) {
/*  469 */       if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/*  470 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds;
/*  471 */         listSelected.add(new SelectedContent(fileInfo.getDownloadManager(), fileInfo.getIndex()));
/*      */         
/*  473 */         if (!fileInfo.isSkipped())
/*      */         {
/*  475 */           total_size += fileInfo.getLength();
/*  476 */           total_done += fileInfo.getDownloaded();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  483 */     this.selection_size = total_size;
/*  484 */     this.selection_done = total_done;
/*      */     
/*  486 */     updateHeader();
/*      */     
/*  488 */     SelectedContent[] sc = (SelectedContent[])listSelected.toArray(new SelectedContent[0]);
/*  489 */     SelectedContentManager.changeCurrentlySelectedContent(this.tv.getTableID(), sc, this.tv);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void defaultSelected(TableRowCore[] rows, int stateMask)
/*      */   {
/*  496 */     org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.tv.getFirstSelectedDataSource();
/*      */     
/*  498 */     if (fileInfo == null)
/*      */     {
/*  500 */       return;
/*      */     }
/*      */     
/*  503 */     boolean webInBrowser = COConfigurationManager.getBooleanParameter("Library.LaunchWebsiteInBrowser");
/*      */     
/*  505 */     if (webInBrowser)
/*      */     {
/*  507 */       if (ManagerUtils.browseWebsite(fileInfo))
/*      */       {
/*  509 */         return;
/*      */       }
/*      */     }
/*      */     
/*  513 */     String mode = COConfigurationManager.getStringParameter("list.dm.dblclick");
/*      */     
/*  515 */     if (mode.equals("2"))
/*      */     {
/*  517 */       boolean openMode = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*      */       
/*  519 */       ManagerUtils.open(fileInfo, openMode);
/*      */     }
/*  521 */     else if ((mode.equals("3")) || (mode.equals("4")))
/*      */     {
/*      */ 
/*  524 */       if (fileInfo.getAccessMode() == 1)
/*      */       {
/*  526 */         if ((mode.equals("4")) && (fileInfo.getDownloaded() == fileInfo.getLength()) && (Utils.isQuickViewSupported(fileInfo)))
/*      */         {
/*      */ 
/*      */ 
/*  530 */           Utils.setQuickViewActive(fileInfo, true);
/*      */         }
/*      */         else
/*      */         {
/*  534 */           Utils.launch(fileInfo);
/*      */         }
/*      */       }
/*  537 */     } else if (mode.equals("5"))
/*      */     {
/*  539 */       ManagerUtils.browse(fileInfo);
/*      */     }
/*      */     else
/*      */     {
/*  543 */       AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*      */       
/*  545 */       if (az3 != null)
/*      */       {
/*  547 */         DownloadManager dm = fileInfo.getDownloadManager();
/*      */         
/*  549 */         if ((az3.canPlay(dm, fileInfo.getIndex())) || ((stateMask & 0x40000) != 0))
/*      */         {
/*  551 */           az3.play(dm, fileInfo.getIndex());
/*      */           
/*  553 */           return;
/*      */         }
/*      */       }
/*      */       
/*  557 */       if (fileInfo.getAccessMode() == 1)
/*      */       {
/*  559 */         Utils.launch(fileInfo);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void fillMenu(String sColumnName, Menu menu)
/*      */   {
/*  567 */     if (this.managers.size() != 0)
/*      */     {
/*  569 */       if (this.managers.size() == 1)
/*      */       {
/*  571 */         Object[] data_sources = this.tv.getSelectedDataSources().toArray();
/*      */         
/*  573 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files = new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[data_sources.length];
/*      */         
/*  575 */         for (int i = 0; i < data_sources.length; i++) {
/*  576 */           files[i] = ((org.gudy.azureus2.core3.disk.DiskManagerFileInfo)data_sources[i]);
/*      */         }
/*      */         
/*  579 */         FilesViewMenuUtil.fillMenu(this.tv, menu, new DownloadManager[] { (DownloadManager)this.managers.get(0) }, new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[][] { files });
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  586 */         Object[] data_sources = this.tv.getSelectedDataSources().toArray();
/*      */         
/*  588 */         Map<DownloadManager, List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo>> map = new java.util.IdentityHashMap();
/*      */         
/*  590 */         List<DownloadManager> dms = new ArrayList();
/*      */         
/*  592 */         for (Object ds : data_sources)
/*      */         {
/*  594 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo file = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds;
/*      */           
/*  596 */           DownloadManager dm = file.getDownloadManager();
/*      */           
/*  598 */           List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> list = (List)map.get(dm);
/*      */           
/*  600 */           if (list == null)
/*      */           {
/*  602 */             list = new ArrayList(dm.getDiskManagerFileInfoSet().nbFiles());
/*      */             
/*  604 */             map.put(dm, list);
/*      */             
/*  606 */             dms.add(dm);
/*      */           }
/*      */           
/*  609 */           list.add(file);
/*      */         }
/*      */         
/*  612 */         DownloadManager[] manager_list = (DownloadManager[])dms.toArray(new DownloadManager[dms.size()]);
/*      */         
/*  614 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo[][] files_list = new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[manager_list.length][];
/*      */         
/*  616 */         for (int i = 0; i < manager_list.length; i++)
/*      */         {
/*  618 */           List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> list = (List)map.get(manager_list[i]);
/*      */           
/*  620 */           files_list[i] = ((org.gudy.azureus2.core3.disk.DiskManagerFileInfo[])list.toArray(new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[list.size()]));
/*      */         }
/*      */         
/*  623 */         FilesViewMenuUtil.fillMenu(this.tv, menu, manager_list, files_list);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*  629 */   private boolean force_refresh = false;
/*      */   
/*  631 */   public void tableRefresh() { if (this.refreshing) {
/*  632 */       return;
/*      */     }
/*      */     try {
/*  635 */       this.refreshing = true;
/*  636 */       if (this.tv.isDisposed()) {
/*      */         return;
/*      */       }
/*  639 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files = getFileInfo();
/*      */       
/*  641 */       if ((files != null) && ((this.force_refresh) || (!doAllExist(files)))) {
/*  642 */         this.force_refresh = false;
/*      */         
/*  644 */         List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> datasources = this.tv.getDataSources();
/*  645 */         if (datasources.size() == files.length)
/*      */         {
/*      */ 
/*  648 */           ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> toAdd = new ArrayList(Arrays.asList(files));
/*  649 */           ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> toRemove = new ArrayList();
/*  650 */           for (int i = 0; i < datasources.size(); i++)
/*      */           {
/*  652 */             org.gudy.azureus2.core3.disk.DiskManagerFileInfo info = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)datasources.get(i);
/*      */             
/*  654 */             if (files[info.getIndex()] == info) {
/*  655 */               toAdd.set(info.getIndex(), null);
/*      */             } else
/*  657 */               toRemove.add(info);
/*      */           }
/*  659 */           this.tv.removeDataSources(toRemove.toArray(new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[toRemove.size()]));
/*  660 */           this.tv.addDataSources(toAdd.toArray(new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[toAdd.size()]));
/*  661 */           this.tv.tableInvalidate();
/*      */         }
/*      */         else {
/*  664 */           this.tv.removeAllTableRows();
/*      */           
/*  666 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] filesCopy = new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[files.length];
/*  667 */           System.arraycopy(files, 0, filesCopy, 0, files.length);
/*      */           
/*  669 */           this.tv.addDataSources(filesCopy);
/*      */         }
/*      */         
/*  672 */         this.tv.processDataSourceQueue();
/*      */       }
/*      */     } finally {
/*  675 */       this.refreshing = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean doAllExist(org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files)
/*      */   {
/*  686 */     for (int i = 0; i < files.length; i++) {
/*  687 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileinfo = files[i];
/*      */       
/*  689 */       if (this.tv.isFiltered(fileinfo))
/*      */       {
/*      */ 
/*      */ 
/*  693 */         TableRowCore row = this.tv.getRow(fileinfo);
/*  694 */         if (row == null) {
/*  695 */           return false;
/*      */         }
/*      */         
/*  698 */         if (row.getDataSource(true) != fileinfo) {
/*  699 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*  703 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addThisColumnSubMenu(String sColumnName, Menu menuThisColumn)
/*      */   {
/*  710 */     if (sColumnName.equals("path")) {
/*  711 */       this.path_item = new MenuItem(menuThisColumn, 32);
/*      */       
/*  713 */       this.path_item.setSelection(show_full_path);
/*      */       
/*  715 */       Messages.setLanguageText(this.path_item, "FilesView.fullpath");
/*      */       
/*  717 */       this.path_item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  719 */           FilesView.show_full_path = FilesView.this.path_item.getSelection();
/*  720 */           FilesView.this.tv.columnInvalidate("path");
/*  721 */           FilesView.this.tv.refreshTable(false);
/*  722 */           COConfigurationManager.setParameter("FilesView.show.full.path", FilesView.show_full_path);
/*      */         }
/*      */       });
/*      */     }
/*  726 */     else if (sColumnName.equals("file_eta")) {
/*  727 */       final MenuItem item = new MenuItem(menuThisColumn, 32);
/*  728 */       Messages.setLanguageText(item, "MyTorrentsView.menu.eta.abs");
/*  729 */       item.setSelection(MyTorrentsView.eta_absolute);
/*      */       
/*  731 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  733 */           MyTorrentsView.eta_absolute = item.getSelection();
/*  734 */           FilesView.this.tv.columnInvalidate("eta");
/*  735 */           FilesView.this.tv.refreshTable(false);
/*  736 */           COConfigurationManager.setParameter("mtv.eta.show_absolute", MyTorrentsView.eta_absolute);
/*      */         }
/*      */       });
/*  739 */     } else if (sColumnName.equals("priority")) {
/*  740 */       final MenuItem item = new MenuItem(menuThisColumn, 32);
/*  741 */       Messages.setLanguageText(item, "FilesView.hide.dnd");
/*  742 */       item.setSelection(this.hide_dnd_files);
/*      */       
/*  744 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  746 */           FilesView.this.hide_dnd_files = item.getSelection();
/*  747 */           COConfigurationManager.setParameter("FilesView.hide.dnd", FilesView.this.hide_dnd_files);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] getFileInfo()
/*      */   {
/*  757 */     if (this.managers.size() == 0)
/*      */     {
/*  759 */       return null;
/*      */     }
/*  761 */     if (this.managers.size() == 1)
/*      */     {
/*  763 */       return ((DownloadManager)this.managers.get(0)).getDiskManagerFileInfoSet().getFiles();
/*      */     }
/*      */     
/*      */ 
/*  767 */     List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> temp = new ArrayList();
/*      */     
/*  769 */     for (DownloadManager dm : this.managers)
/*      */     {
/*  771 */       temp.addAll(Arrays.asList(dm.getDiskManagerFileInfoSet().getFiles()));
/*      */     }
/*      */     
/*  774 */     return (org.gudy.azureus2.core3.disk.DiskManagerFileInfo[])temp.toArray(new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[temp.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void attributeEventOccurred(DownloadManager dm, String attribute_name, int event_type)
/*      */   {
/*  781 */     Object oIsChangingLinks = dm.getUserData("is_changing_links");
/*  782 */     if (((oIsChangingLinks instanceof Boolean)) && (((Boolean)oIsChangingLinks).booleanValue())) {
/*  783 */       return;
/*      */     }
/*  785 */     this.force_refresh = true;
/*      */   }
/*      */   
/*      */   public void tableViewInitialized() {
/*  789 */     createDragDrop();
/*      */     
/*  791 */     this.hide_dnd_files = COConfigurationManager.getBooleanParameter("FilesView.hide.dnd");
/*  792 */     COConfigurationManager.addParameterListener("FilesView.hide.dnd", this);
/*      */   }
/*      */   
/*      */   public void tableViewTabInitComplete() {
/*  796 */     updateSelectedContent();
/*  797 */     super.tableViewTabInitComplete();
/*      */   }
/*      */   
/*      */   public void tableViewDestroyed() {
/*  801 */     COConfigurationManager.removeParameterListener("FilesView.hide.dnd", this);
/*  802 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*      */         try {
/*  805 */           Utils.disposeSWTObjects(new Object[] { FilesView.this.dragSource });
/*      */           
/*      */ 
/*  808 */           FilesView.this.dragSource = null;
/*      */         } catch (Exception e) {
/*  810 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  815 */     for (DownloadManager manager : this.managers) {
/*  816 */       manager.getDownloadState().removeListener(this, "filelinks2", 1);
/*      */       
/*  818 */       manager.removeListener(this);
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
/*      */   private void createDragDrop()
/*      */   {
/*      */     try
/*      */     {
/*  834 */       Transfer[] types = { org.eclipse.swt.dnd.TextTransfer.getInstance(), FileTransfer.getInstance() };
/*      */       
/*  836 */       if ((this.dragSource != null) && (!this.dragSource.isDisposed())) {
/*  837 */         this.dragSource.dispose();
/*      */       }
/*      */       
/*  840 */       this.dragSource = this.tv.createDragSource(3);
/*  841 */       if (this.dragSource != null) {
/*  842 */         this.dragSource.setTransfer(types);
/*  843 */         this.dragSource.addDragListener(new DragSourceAdapter() {
/*      */           private String eventData1;
/*      */           private String[] eventData2;
/*      */           
/*      */           public void dragStart(DragSourceEvent event) {
/*  848 */             TableRowCore[] rows = FilesView.this.tv.getSelectedRows();
/*      */             
/*  850 */             if ((rows.length != 0) && (FilesView.this.managers.size() > 0)) {
/*  851 */               event.doit = true;
/*      */             } else {
/*  853 */               event.doit = false;
/*  854 */               return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  859 */             Object[] selectedDownloads = FilesView.this.tv.getSelectedDataSources().toArray();
/*  860 */             this.eventData2 = new String[selectedDownloads.length];
/*  861 */             this.eventData1 = "DiskManagerFileInfo\n";
/*      */             
/*  863 */             for (int i = 0; i < selectedDownloads.length; i++) {
/*  864 */               org.gudy.azureus2.core3.disk.DiskManagerFileInfo fi = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)selectedDownloads[i];
/*      */               try
/*      */               {
/*  867 */                 TOTorrent torrent = fi.getDownloadManager().getTorrent();
/*  868 */                 if (torrent != null) {
/*  869 */                   this.eventData1 = (this.eventData1 + torrent.getHashWrapper().toBase32String() + ";" + fi.getIndex() + "\n");
/*      */                 }
/*      */               }
/*      */               catch (Exception e) {}
/*      */               try
/*      */               {
/*  875 */                 this.eventData2[i] = fi.getFile(true).getAbsolutePath();
/*      */               }
/*      */               catch (Exception e) {}
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */           public void dragSetData(DragSourceEvent event)
/*      */           {
/*  884 */             if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
/*  885 */               event.data = this.eventData2;
/*      */             } else {
/*  887 */               event.data = this.eventData1;
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     } catch (Throwable t) {
/*  893 */       Logger.log(new LogEvent(LogIDs.GUI, "failed to init drag-n-drop", t));
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event) {
/*  898 */     if ((event.getType() == 0) && 
/*  899 */       ((event instanceof UISWTViewEventImpl)))
/*      */     {
/*  901 */       String parent = ((UISWTViewEventImpl)event).getParentID();
/*      */       
/*  903 */       this.enable_tabs = ((parent != null) && (parent.equals("TorrentDetailsView")));
/*      */     }
/*      */     
/*  906 */     boolean b = super.eventOccurred(event);
/*  907 */     if (event.getType() == 3) {
/*  908 */       updateSelectedContent();
/*  909 */     } else if (event.getType() == 4) {
/*  910 */       SelectedContentManager.clearCurrentlySelectedContent();
/*      */     }
/*  912 */     return b;
/*      */   }
/*      */   
/*      */   public void keyPressed(KeyEvent e)
/*      */   {
/*  917 */     if ((e.keyCode == 16777227) && ((e.stateMask & org.eclipse.swt.SWT.MODIFIER_MASK) == 0)) {
/*  918 */       FilesViewMenuUtil.rename(this.tv, this.tv.getSelectedDataSources(true), true, false);
/*  919 */       e.doit = false;
/*  920 */       return;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void parameterChanged(String parameterName)
/*      */   {
/*  932 */     if ("FilesView.hide.dnd".equals(parameterName)) {
/*  933 */       this.hide_dnd_files = COConfigurationManager.getBooleanParameter(parameterName);
/*  934 */       if ((this.btnShowDND != null) && (!this.btnShowDND.isDisposed())) {
/*  935 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  937 */             if ((FilesView.this.btnShowDND != null) && (!FilesView.this.btnShowDND.isDisposed())) {
/*  938 */               FilesView.this.btnShowDND.setSelection(FilesView.this.hide_dnd_files);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*  943 */       if ((this.tv == null) || (this.tv.isDisposed())) {
/*  944 */         return;
/*      */       }
/*  946 */       this.tv.refilter();
/*      */     }
/*      */   }
/*      */   
/*      */   private void updateHeader() {
/*  951 */     if (this.managers.size() == 0) {
/*  952 */       if (this.lblHeader != null) {
/*  953 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  955 */             if ((FilesView.this.lblHeader == null) || (FilesView.this.lblHeader.isDisposed())) {
/*  956 */               return;
/*      */             }
/*  958 */             FilesView.this.lblHeader.setText("");
/*      */           }
/*      */         });
/*      */       }
/*  962 */       return;
/*      */     }
/*  964 */     int total = 0;
/*      */     
/*  966 */     for (DownloadManager manager : this.managers) {
/*  967 */       total += manager.getNumFileInfos();
/*      */     }
/*      */     
/*  970 */     int numInList = this.tv.getRowCount();
/*      */     
/*      */ 
/*      */ 
/*  974 */     String s = MessageText.getString("library.unopened.header" + (total > 1 ? ".p" : ""), new String[] { String.valueOf(total) });
/*      */     
/*      */ 
/*      */ 
/*  978 */     if (total != numInList) {
/*  979 */       s = MessageText.getString("v3.MainWindow.xofx", new String[] { String.valueOf(numInList), s });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  985 */     s = s + getSelectionText();
/*      */     
/*  987 */     final String sHeader = s;
/*      */     
/*  989 */     if (this.lblHeader != null) {
/*  990 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  992 */           if ((FilesView.this.lblHeader == null) || (FilesView.this.lblHeader.isDisposed())) {
/*  993 */             return;
/*      */           }
/*  995 */           FilesView.this.lblHeader.setText(sHeader);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private String getSelectionText()
/*      */   {
/* 1004 */     int selection_count = this.tv.getSelectedRowsSize();
/* 1005 */     if (selection_count == 0) {
/* 1006 */       return "";
/*      */     }
/*      */     
/* 1009 */     String str = ", " + MessageText.getString("label.num_selected", new String[] { String.valueOf(selection_count) });
/*      */     
/*      */ 
/*      */ 
/* 1013 */     if (this.selection_size > 0L)
/*      */     {
/* 1015 */       if (this.selection_size == this.selection_done)
/*      */       {
/* 1017 */         str = str + " (" + DisplayFormatters.formatByteCountToKiBEtc(this.selection_size) + ")";
/*      */       } else {
/* 1019 */         str = str + " (" + DisplayFormatters.formatByteCountToKiBEtc(this.selection_done) + "/" + DisplayFormatters.formatByteCountToKiBEtc(this.selection_size) + ")";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1024 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDisableWhenEmpty(boolean b)
/*      */   {
/* 1031 */     this.disableTableWhenEmpty = b;
/*      */   }
/*      */   
/*      */   public void focusChanged(TableRowCore focus) {}
/*      */   
/*      */   public void stateChanged(DownloadManager manager, int state) {}
/*      */   
/*      */   public void downloadComplete(DownloadManager manager) {}
/*      */   
/*      */   public void completionChanged(DownloadManager manager, boolean bCompleted) {}
/*      */   
/*      */   public void positionChanged(DownloadManager download, int oldPosition, int newPosition) {}
/*      */   
/*      */   public void filterSet(String filter) {}
/*      */   
/*      */   public void mouseEnter(TableRowCore row) {}
/*      */   
/*      */   public void mouseExit(TableRowCore row) {}
/*      */   
/*      */   public void keyReleased(KeyEvent e) {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/FilesView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */