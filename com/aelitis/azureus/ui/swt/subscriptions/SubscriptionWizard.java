/*      */ package com.aelitis.azureus.ui.swt.subscriptions;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionUtils;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionUtils.SubscriptionDownloadDetails;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*      */ import java.net.URL;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.custom.StackLayout;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FillLayout;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Link;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.TabFolder;
/*      */ import org.eclipse.swt.widgets.TabItem;
/*      */ import org.eclipse.swt.widgets.Table;
/*      */ import org.eclipse.swt.widgets.TableItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SubscriptionWizard
/*      */ {
/*      */   private static final int MODE_OPT_IN = 1;
/*      */   private static final int MODE_SUBSCRIBE = 2;
/*      */   private static final int MODE_CREATE_SEARCH = 3;
/*      */   private static final int MODE_CREATE_RSS = 4;
/*      */   private static final int RANK_COLUMN_WIDTH = 85;
/*      */   private static final String TABLE_SUB_WIZ = "SubscriptionWizard";
/*  105 */   private final String TITLE_OPT_IN = MessageText.getString("Wizard.Subscription.optin.title");
/*  106 */   private final String TITLE_SUBSCRIBE = MessageText.getString("Wizard.Subscription.subscribe.title");
/*  107 */   private final String TITLE_CREATE = MessageText.getString("Wizard.Subscription.create.title");
/*      */   
/*      */   Display display;
/*      */   
/*      */   Shell shell;
/*      */   
/*      */   Image rankingBars;
/*      */   
/*      */   Color rankingBorderColor;
/*      */   
/*      */   Label title;
/*      */   
/*      */   Button cancelButton;
/*      */   
/*      */   Button searchButton;
/*      */   
/*      */   Button saveButton;
/*      */   
/*      */   Button yesButton;
/*      */   
/*      */   Button addButton;
/*      */   
/*      */   Button availableButton;
/*      */   
/*      */   Button createButton;
/*      */   Font boldFont;
/*      */   Font titleFont;
/*      */   Font subTitleFont;
/*      */   Font textInputFont;
/*      */   Composite main;
/*      */   StackLayout mainLayout;
/*      */   Composite optinComposite;
/*      */   Composite createComposite;
/*      */   TabFolder createTabFolder;
/*      */   TabItem createRSSTabItem;
/*      */   TabItem createSearchTabItem;
/*      */   Composite availableSubscriptionComposite;
/*      */   Table libraryTable;
/*      */   Listener rssSaveListener;
/*      */   Listener searchListener;
/*      */   Text searchInput;
/*      */   Text feedUrl;
/*      */   Text subsName;
/*      */   Button anonCheck;
/*      */   String subs_name_default;
/*      */   SubscriptionUtils.SubscriptionDownloadDetails[] availableSubscriptions;
/*      */   Subscription[] subscriptions;
/*      */   DownloadManager download;
/*      */   URL rss_feed_url;
/*  156 */   boolean anon_default = false;
/*      */   
/*      */   private ImageLoader imageLoader;
/*      */   private TableViewSWT<Subscription> tvSubscriptions;
/*  160 */   private static boolean columnsAdded = false;
/*      */   
/*      */   public SubscriptionWizard() {
/*  163 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SubscriptionWizard(URL url, Map<String, Object> options)
/*      */   {
/*  171 */     this.rss_feed_url = url;
/*      */     
/*  173 */     Boolean anon = (Boolean)options.get("_anonymous_");
/*      */     
/*  175 */     this.anon_default = ((anon != null) && (anon.booleanValue()));
/*      */     
/*  177 */     this.subs_name_default = ((String)options.get("t"));
/*      */     
/*  179 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */   public SubscriptionWizard(DownloadManager _download)
/*      */   {
/*  185 */     this.download = _download;
/*      */     
/*  187 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void init()
/*      */   {
/*  193 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  195 */         SubscriptionWizard.this.init(core);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void init(AzureusCore core) {
/*  201 */     this.imageLoader = ImageLoader.getInstance();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  216 */     this.availableSubscriptions = SubscriptionUtils.getAllCachedDownloadDetails(core);
/*  217 */     Arrays.sort(this.availableSubscriptions, new Comparator() {
/*      */       public int compare(SubscriptionUtils.SubscriptionDownloadDetails o1, SubscriptionUtils.SubscriptionDownloadDetails o2) {
/*  219 */         if ((o1 == null) || (o2 == null)) return 0;
/*  220 */         return o1.getDownload().getDisplayName().compareTo(o2.getDownload().getDisplayName());
/*      */       }
/*      */       
/*      */ 
/*  224 */     });
/*  225 */     this.shell = ShellFactory.createMainShell(112);
/*  226 */     this.shell.setSize(650, 400);
/*  227 */     Utils.centreWindow(this.shell);
/*      */     
/*  229 */     this.shell.setMinimumSize(550, 400);
/*      */     
/*  231 */     this.display = this.shell.getDisplay();
/*      */     
/*  233 */     Utils.setShellIcon(this.shell);
/*      */     
/*  235 */     this.rankingBars = this.imageLoader.getImage("ranking_bars");
/*  236 */     this.rankingBorderColor = new Color(this.display, 200, 200, 200);
/*      */     
/*  238 */     createFonts();
/*      */     
/*  240 */     this.shell.setText(MessageText.getString("Wizard.Subscription.title"));
/*      */     
/*  242 */     this.shell.addListener(12, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  244 */         SubscriptionWizard.this.imageLoader.releaseImage("ranking_bars");
/*  245 */         SubscriptionWizard.this.imageLoader.releaseImage("wizard_header_bg");
/*  246 */         SubscriptionWizard.this.imageLoader.releaseImage("icon_rss");
/*      */         
/*  248 */         if ((SubscriptionWizard.this.titleFont != null) && (!SubscriptionWizard.this.titleFont.isDisposed())) {
/*  249 */           SubscriptionWizard.this.titleFont.dispose();
/*      */         }
/*      */         
/*  252 */         if ((SubscriptionWizard.this.textInputFont != null) && (!SubscriptionWizard.this.textInputFont.isDisposed())) {
/*  253 */           SubscriptionWizard.this.textInputFont.dispose();
/*      */         }
/*      */         
/*  256 */         if ((SubscriptionWizard.this.boldFont != null) && (!SubscriptionWizard.this.boldFont.isDisposed())) {
/*  257 */           SubscriptionWizard.this.boldFont.dispose();
/*      */         }
/*      */         
/*  260 */         if ((SubscriptionWizard.this.subTitleFont != null) && (!SubscriptionWizard.this.subTitleFont.isDisposed())) {
/*  261 */           SubscriptionWizard.this.subTitleFont.dispose();
/*      */         }
/*      */         
/*  264 */         if ((SubscriptionWizard.this.rankingBorderColor != null) && (!SubscriptionWizard.this.rankingBorderColor.isDisposed())) {
/*  265 */           SubscriptionWizard.this.rankingBorderColor.dispose();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  270 */     });
/*  271 */     Composite header = new Composite(this.shell, 0);
/*  272 */     header.setBackgroundMode(1);
/*  273 */     header.setBackgroundImage(this.imageLoader.getImage("wizard_header_bg"));
/*  274 */     Label topSeparator = new Label(this.shell, 258);
/*  275 */     this.main = new Composite(this.shell, 0);
/*  276 */     Label bottomSeparator = new Label(this.shell, 258);
/*  277 */     Composite footer = new Composite(this.shell, 0);
/*      */     
/*  279 */     FormLayout layout = new FormLayout();
/*  280 */     this.shell.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*  284 */     FormData data = new FormData();
/*  285 */     data.top = new FormAttachment(0, 0);
/*  286 */     data.left = new FormAttachment(0, 0);
/*  287 */     data.right = new FormAttachment(100, 0);
/*      */     
/*  289 */     header.setLayoutData(data);
/*      */     
/*  291 */     data = new FormData();
/*  292 */     data.top = new FormAttachment(header, 0);
/*  293 */     data.left = new FormAttachment(0, 0);
/*  294 */     data.right = new FormAttachment(100, 0);
/*  295 */     topSeparator.setLayoutData(data);
/*      */     
/*  297 */     data = new FormData();
/*  298 */     data.top = new FormAttachment(topSeparator, 0);
/*  299 */     data.left = new FormAttachment(0, 0);
/*  300 */     data.right = new FormAttachment(100, 0);
/*  301 */     data.bottom = new FormAttachment(bottomSeparator, 0);
/*  302 */     this.main.setLayoutData(data);
/*      */     
/*  304 */     data = new FormData();
/*  305 */     data.left = new FormAttachment(0, 0);
/*  306 */     data.right = new FormAttachment(100, 0);
/*  307 */     data.bottom = new FormAttachment(footer, 0);
/*  308 */     bottomSeparator.setLayoutData(data);
/*      */     
/*  310 */     data = new FormData();
/*  311 */     data.bottom = new FormAttachment(100, 0);
/*  312 */     data.left = new FormAttachment(0, 0);
/*  313 */     data.right = new FormAttachment(100, 0);
/*      */     
/*  315 */     footer.setLayoutData(data);
/*      */     
/*  317 */     populateHeader(header);
/*  318 */     populateFooter(footer);
/*      */     
/*  320 */     this.mainLayout = new StackLayout();
/*  321 */     this.main.setLayout(this.mainLayout);
/*      */     
/*  323 */     this.optinComposite = createOptInComposite(this.main);
/*  324 */     this.createComposite = createCreateComposite(this.main);
/*  325 */     this.availableSubscriptionComposite = createAvailableSubscriptionComposite(this.main);
/*      */     
/*      */ 
/*  328 */     setDefaultAvailableMode();
/*      */     
/*  330 */     this.shell.layout();
/*  331 */     this.shell.open();
/*      */     
/*  333 */     setInitialViews();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setInitialViews()
/*      */   {
/*  339 */     if (this.availableSubscriptions != null)
/*      */     {
/*  341 */       for (int i = 0; i < this.availableSubscriptions.length; i++)
/*      */       {
/*  343 */         SubscriptionUtils.SubscriptionDownloadDetails details = this.availableSubscriptions[i];
/*      */         
/*  345 */         if (details.getDownload() == this.download)
/*      */         {
/*  347 */           final int f_i = i;
/*      */           
/*  349 */           Utils.execSWTThread(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/*  355 */               SubscriptionWizard.this.libraryTable.setTopIndex(f_i);
/*      */             }
/*      */           });
/*      */         }
/*      */       } }
/*      */   }
/*      */   
/*      */   private void populateHeader(Composite header) {
/*  363 */     header.setBackground(this.display.getSystemColor(1));
/*  364 */     this.title = new Label(header, 64);
/*      */     
/*  366 */     this.title.setFont(this.titleFont);
/*      */     
/*  368 */     FillLayout layout = new FillLayout();
/*  369 */     layout.marginHeight = 10;
/*  370 */     layout.marginWidth = 10;
/*  371 */     header.setLayout(layout);
/*      */   }
/*      */   
/*      */   private Composite createOptInComposite(Composite parent)
/*      */   {
/*  376 */     Composite composite = new Composite(parent, 0);
/*  377 */     composite.setBackgroundMode(2);
/*      */     
/*  379 */     Label description = new Label(composite, 64);
/*  380 */     description.setFont(this.boldFont);
/*  381 */     description.setText(MessageText.getString("Wizard.Subscription.optin.description"));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  399 */     FormLayout layout = new FormLayout();
/*  400 */     composite.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*  404 */     FormData data = new FormData();
/*  405 */     data.top = new FormAttachment(0, 40);
/*  406 */     data.left = new FormAttachment(0, 50);
/*  407 */     data.right = new FormAttachment(100, -50);
/*  408 */     description.setLayoutData(data);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  440 */     return composite;
/*      */   }
/*      */   
/*      */   private Composite createCreateComposite(Composite parent) {
/*  444 */     Composite composite = new Composite(parent, 0);
/*      */     
/*  446 */     FillLayout layout = new FillLayout();
/*  447 */     layout.marginHeight = 8;
/*  448 */     layout.marginWidth = 8;
/*      */     
/*  450 */     composite.setLayout(layout);
/*      */     
/*  452 */     this.createTabFolder = new TabFolder(composite, 0);
/*  453 */     this.createTabFolder.setFont(this.subTitleFont);
/*      */     
/*  455 */     this.createSearchTabItem = new TabItem(this.createTabFolder, 0);
/*  456 */     this.createSearchTabItem.setText(MessageText.getString("Wizard.Subscription.create.search"));
/*  457 */     this.createSearchTabItem.setControl(createCreateSearchComposite(this.createTabFolder));
/*      */     
/*  459 */     this.createRSSTabItem = new TabItem(this.createTabFolder, 0);
/*  460 */     this.createRSSTabItem.setText("  " + MessageText.getString("Wizard.Subscription.create.rss"));
/*  461 */     this.createRSSTabItem.setControl(createCreateRSSComposite(this.createTabFolder));
/*      */     
/*  463 */     this.createTabFolder.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  465 */         TabItem[] selectedItems = SubscriptionWizard.this.createTabFolder.getSelection();
/*  466 */         if (selectedItems.length != 1) {
/*  467 */           return;
/*      */         }
/*  469 */         TabItem selectedItem = selectedItems[0];
/*  470 */         if (selectedItem == SubscriptionWizard.this.createRSSTabItem) {
/*  471 */           SubscriptionWizard.this.setMode(4);
/*      */         } else {
/*  473 */           SubscriptionWizard.this.setMode(3);
/*      */         }
/*      */         
/*      */       }
/*  477 */     });
/*  478 */     return composite;
/*      */   }
/*      */   
/*      */   private Composite createCreateRSSComposite(Composite parent) {
/*  482 */     Composite composite = new Composite(parent, 0);
/*      */     
/*  484 */     Label subTitle1 = new Label(composite, 64);
/*  485 */     subTitle1.setFont(this.subTitleFont);
/*  486 */     subTitle1.setText(MessageText.getString("Wizard.Subscription.rss.subtitle1"));
/*      */     
/*  488 */     Composite cSearchInput = new Composite(composite, 0);
/*  489 */     cSearchInput.setLayout(new FormLayout());
/*  490 */     this.imageLoader.setBackgroundImage(cSearchInput, "search_bg");
/*  491 */     Rectangle imageBounds = cSearchInput.getBackgroundImage().getBounds();
/*      */     
/*  493 */     this.feedUrl = new Text(cSearchInput, 4);
/*  494 */     this.feedUrl.setFont(this.textInputFont);
/*  495 */     this.feedUrl.setText("http://");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  507 */     this.feedUrl.addListener(14, this.rssSaveListener);
/*      */     
/*  509 */     this.feedUrl.addListener(24, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  511 */         boolean valid_url = false;
/*      */         try {
/*  513 */           URL url = new URL(SubscriptionWizard.this.feedUrl.getText());
/*  514 */           String protocol = url.getProtocol().toLowerCase();
/*  515 */           valid_url = (protocol.equals("azplug")) || (protocol.equals("file")) || (url.getHost().trim().length() > 0);
/*      */         }
/*      */         catch (Exception e) {}
/*  518 */         SubscriptionWizard.this.saveButton.setEnabled(valid_url);
/*      */       }
/*      */       
/*  521 */     });
/*  522 */     Label subTitle2 = new Label(composite, 64);
/*      */     
/*  524 */     subTitle2.setText(MessageText.getString("Wizard.Subscription.rss.subtitle2"));
/*      */     
/*  526 */     Label rssBullet = new Label(composite, 0);
/*  527 */     this.imageLoader.setLabelImage(rssBullet, "rss");
/*      */     
/*  529 */     Label subsNameText = new Label(composite, 64);
/*  530 */     subsNameText.setText(MessageText.getString("TableColumn.header.name"));
/*      */     
/*  532 */     this.subsName = new Text(composite, 2048);
/*  533 */     this.subsName.setFont(this.textInputFont);
/*  534 */     if (this.subs_name_default != null) {
/*  535 */       this.subsName.setText(this.subs_name_default);
/*      */     }
/*  537 */     this.anonCheck = new Button(composite, 32);
/*  538 */     Label anonMsg = new Label(composite, 64);
/*  539 */     anonMsg.setText(MessageText.getString("label.anon"));
/*      */     
/*  541 */     this.anonCheck.setSelection(this.anon_default);
/*      */     
/*  543 */     Label subTitle3 = new Label(composite, 64);
/*  544 */     subTitle3.setFont(this.subTitleFont);
/*  545 */     subTitle3.setText(MessageText.getString("Wizard.Subscription.rss.subtitle3"));
/*      */     
/*  547 */     FormLayout layout = new FormLayout();
/*  548 */     layout.marginWidth = 50;
/*  549 */     layout.marginTop = 25;
/*  550 */     composite.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*  554 */     FormData data = new FormData();
/*  555 */     data.top = new FormAttachment(0);
/*  556 */     data.left = new FormAttachment(0);
/*  557 */     data.right = new FormAttachment(100);
/*  558 */     subTitle1.setLayoutData(data);
/*      */     
/*  560 */     data = new FormData();
/*  561 */     data.top = new FormAttachment(subTitle1, 5);
/*  562 */     data.left = new FormAttachment(50, -imageBounds.width / 2);
/*  563 */     data.width = imageBounds.width;
/*  564 */     data.height = imageBounds.height;
/*  565 */     cSearchInput.setLayoutData(data);
/*      */     
/*      */ 
/*      */ 
/*  569 */     data = new FormData();
/*  570 */     data.top = new FormAttachment(0, 7);
/*  571 */     data.left = new FormAttachment(0, 45);
/*  572 */     data.right = new FormAttachment(100, -8);
/*  573 */     this.feedUrl.setLayoutData(data);
/*      */     
/*      */ 
/*      */ 
/*  577 */     data = new FormData();
/*  578 */     data.top = new FormAttachment(cSearchInput, 15);
/*  579 */     data.left = new FormAttachment(0);
/*  580 */     rssBullet.setLayoutData(data);
/*      */     
/*  582 */     data = new FormData();
/*  583 */     data.top = new FormAttachment(rssBullet, -3, 128);
/*  584 */     data.left = new FormAttachment(rssBullet, 5);
/*  585 */     data.right = new FormAttachment(100);
/*  586 */     subTitle2.setLayoutData(data);
/*      */     
/*      */ 
/*      */ 
/*  590 */     data = new FormData();
/*  591 */     data.top = new FormAttachment(rssBullet, 20);
/*  592 */     data.left = new FormAttachment(subTitle2, 0, 16384);
/*  593 */     subsNameText.setLayoutData(data);
/*      */     
/*  595 */     data = new FormData();
/*  596 */     data.bottom = new FormAttachment(subsNameText, 0, 1024);
/*  597 */     data.left = new FormAttachment(subsNameText, 5, 131072);
/*  598 */     data.right = new FormAttachment(50);
/*  599 */     this.subsName.setLayoutData(data);
/*      */     
/*  601 */     data = new FormData();
/*  602 */     data.bottom = new FormAttachment(subsNameText, 0, 1024);
/*  603 */     data.left = new FormAttachment(this.subsName, 5, 131072);
/*  604 */     this.anonCheck.setLayoutData(data);
/*      */     
/*  606 */     data = new FormData();
/*  607 */     data.bottom = new FormAttachment(subsNameText, 0, 1024);
/*  608 */     data.left = new FormAttachment(this.anonCheck, 5, 131072);
/*  609 */     data.right = new FormAttachment(100);
/*  610 */     anonMsg.setLayoutData(data);
/*      */     
/*      */ 
/*      */ 
/*  614 */     data = new FormData();
/*  615 */     data.top = new FormAttachment(this.subsName, 20);
/*  616 */     data.left = new FormAttachment(0);
/*  617 */     data.right = new FormAttachment(100);
/*  618 */     subTitle3.setLayoutData(data);
/*      */     
/*      */ 
/*  621 */     return composite;
/*      */   }
/*      */   
/*      */   private Composite createCreateSearchComposite(Composite parent) {
/*  625 */     Composite composite = new Composite(parent, 0);
/*      */     
/*  627 */     Label subTitle1 = new Label(composite, 64);
/*  628 */     subTitle1.setFont(this.subTitleFont);
/*  629 */     subTitle1.setText(MessageText.getString("Wizard.Subscription.search.subtitle1"));
/*      */     
/*  631 */     Composite cSearchInput = new Composite(composite, 0);
/*  632 */     cSearchInput.setLayout(new FormLayout());
/*  633 */     this.imageLoader.setBackgroundImage(cSearchInput, "search_bg");
/*  634 */     Rectangle imageBounds = cSearchInput.getBackgroundImage().getBounds();
/*      */     
/*  636 */     this.searchInput = new Text(cSearchInput, 4);
/*  637 */     this.searchInput.setFont(this.textInputFont);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  650 */     this.searchInput.addListener(14, this.searchListener);
/*      */     
/*  652 */     Label subTitle2 = new Label(composite, 64);
/*  653 */     subTitle2.setFont(this.subTitleFont);
/*  654 */     subTitle2.setText(MessageText.getString("Wizard.Subscription.search.subtitle2"));
/*      */     
/*  656 */     Label checkBullet1 = new Label(composite, 0);
/*  657 */     this.imageLoader.setLabelImage(checkBullet1, "icon_check");
/*  658 */     Label checkBullet2 = new Label(composite, 0);
/*  659 */     this.imageLoader.setLabelImage(checkBullet2, "icon_check");
/*      */     
/*  661 */     Label description1 = new Label(composite, 0);
/*  662 */     description1.setText(MessageText.getString("Wizard.Subscription.search.subtitle2.sub1"));
/*  663 */     Label description2 = new Label(composite, 0);
/*  664 */     description2.setText(MessageText.getString("Wizard.Subscription.search.subtitle2.sub2"));
/*      */     
/*  666 */     Label subTitle3 = new Label(composite, 64);
/*  667 */     subTitle3.setFont(this.subTitleFont);
/*  668 */     subTitle3.setText(MessageText.getString("Wizard.Subscription.search.subtitle3"));
/*      */     
/*  670 */     FormLayout layout = new FormLayout();
/*  671 */     layout.marginLeft = 50;
/*  672 */     layout.marginRight = 50;
/*  673 */     layout.marginTop = 25;
/*      */     
/*  675 */     composite.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*  679 */     FormData data = new FormData();
/*  680 */     data.top = new FormAttachment(0);
/*  681 */     data.left = new FormAttachment(0);
/*  682 */     data.right = new FormAttachment(100);
/*  683 */     subTitle1.setLayoutData(data);
/*      */     
/*  685 */     data = new FormData();
/*  686 */     data.top = new FormAttachment(subTitle1, 5);
/*  687 */     data.left = new FormAttachment(50, -imageBounds.width / 2);
/*  688 */     data.width = imageBounds.width;
/*  689 */     data.height = imageBounds.height;
/*  690 */     cSearchInput.setLayoutData(data);
/*      */     
/*  692 */     data = new FormData();
/*  693 */     data.top = new FormAttachment(0, 7);
/*  694 */     data.left = new FormAttachment(0, 45);
/*  695 */     data.right = new FormAttachment(100, -8);
/*  696 */     this.searchInput.setLayoutData(data);
/*      */     
/*  698 */     data = new FormData();
/*  699 */     data.top = new FormAttachment(cSearchInput, 15);
/*  700 */     data.left = new FormAttachment(0);
/*  701 */     data.right = new FormAttachment(100);
/*  702 */     subTitle2.setLayoutData(data);
/*      */     
/*  704 */     data = new FormData();
/*  705 */     data.top = new FormAttachment(subTitle2, 5);
/*  706 */     data.left = new FormAttachment(0);
/*  707 */     checkBullet1.setLayoutData(data);
/*      */     
/*  709 */     data = new FormData();
/*  710 */     data.top = new FormAttachment(checkBullet1, 5);
/*  711 */     data.left = new FormAttachment(0);
/*  712 */     checkBullet2.setLayoutData(data);
/*      */     
/*  714 */     data = new FormData();
/*  715 */     data.top = new FormAttachment(checkBullet1, 0, 128);
/*  716 */     data.left = new FormAttachment(checkBullet1, 5);
/*  717 */     description1.setLayoutData(data);
/*      */     
/*  719 */     data = new FormData();
/*  720 */     data.top = new FormAttachment(checkBullet2, 0, 128);
/*  721 */     data.left = new FormAttachment(checkBullet2, 5);
/*  722 */     description2.setLayoutData(data);
/*      */     
/*  724 */     data = new FormData();
/*  725 */     data.top = new FormAttachment(checkBullet2, 15);
/*  726 */     data.left = new FormAttachment(0);
/*  727 */     data.right = new FormAttachment(100);
/*  728 */     subTitle3.setLayoutData(data);
/*      */     
/*  730 */     return composite;
/*      */   }
/*      */   
/*      */   private Composite createAvailableSubscriptionComposite(Composite parent) {
/*  734 */     Composite composite = new Composite(parent, 0);
/*      */     
/*  736 */     Label hsep1 = new Label(composite, 258);
/*  737 */     Label hsep2 = new Label(composite, 258);
/*      */     
/*  739 */     Label vsep = new Label(composite, 514);
/*      */     
/*  741 */     Label subtitle1 = new Label(composite, 0);
/*  742 */     Label subtitle2 = new Label(composite, 0);
/*  743 */     subtitle1.setFont(this.subTitleFont);
/*  744 */     subtitle2.setFont(this.subTitleFont);
/*  745 */     subtitle1.setText(MessageText.getString("Wizard.Subscription.subscribe.library"));
/*  746 */     subtitle2.setText(MessageText.getString("Wizard.Subscription.subscribe.subscriptions"));
/*      */     
/*  748 */     this.libraryTable = new Table(composite, 268501508);
/*      */     
/*  750 */     org.eclipse.swt.widgets.TableColumn torrentColumn = new org.eclipse.swt.widgets.TableColumn(this.libraryTable, 0);
/*  751 */     torrentColumn.setWidth(Utils.adjustPXForDPI(50));
/*      */     
/*  753 */     Composite compEmpty = new Composite(composite, 0);
/*  754 */     compEmpty.setBackground(this.display.getSystemColor(1));
/*  755 */     compEmpty.setBackgroundMode(1);
/*  756 */     FillLayout fl = new FillLayout();
/*  757 */     fl.marginHeight = 15;
/*  758 */     fl.marginWidth = 15;
/*  759 */     compEmpty.setLayout(fl);
/*  760 */     compEmpty.setVisible(false);
/*      */     
/*  762 */     Link labelEmpty = new Link(compEmpty, 64);
/*  763 */     labelEmpty.setText(MessageText.getString("Wizard.Subscription.subscribe.library.empty"));
/*  764 */     labelEmpty.setFont(this.subTitleFont);
/*  765 */     labelEmpty.setForeground(ColorCache.getColor(composite.getDisplay(), "#6D6F6E"));
/*      */     
/*  767 */     labelEmpty.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  769 */         if ((event.text != null) && ((event.text.startsWith("http://")) || (event.text.startsWith("https://")))) {
/*  770 */           Utils.launch(event.text);
/*      */         }
/*      */         
/*      */       }
/*  774 */     });
/*  775 */     initColumns();
/*      */     
/*  777 */     Composite cTV = new Composite(composite, 0);
/*  778 */     cTV.setLayoutData(Utils.getFilledFormData());
/*  779 */     GridLayout layout = new GridLayout();
/*  780 */     layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/*  781 */     cTV.setLayout(layout);
/*      */     
/*  783 */     this.tvSubscriptions = TableViewFactory.createTableViewSWT(Subscription.class, "SubscriptionWizard", "SubscriptionWizard", new TableColumnCore[0], "SubWizRank", 268501508);
/*      */     
/*      */ 
/*  786 */     this.tvSubscriptions.setMenuEnabled(false);
/*  787 */     this.tvSubscriptions.setHeaderVisible(false);
/*  788 */     this.tvSubscriptions.setRowDefaultHeightEM(1.4F);
/*      */     
/*  790 */     this.tvSubscriptions.initialize(cTV);
/*      */     
/*  792 */     this.tvSubscriptions.getComposite().addListener(11, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  794 */         org.gudy.azureus2.plugins.ui.tables.TableColumn tcName = SubscriptionWizard.this.tvSubscriptions.getTableColumn("SubWizName");
/*  795 */         org.gudy.azureus2.plugins.ui.tables.TableColumn tcRank = SubscriptionWizard.this.tvSubscriptions.getTableColumn("SubWizRank");
/*  796 */         Rectangle clientArea = ((Composite)event.widget).getClientArea();
/*  797 */         tcName.setWidthPX(clientArea.width - tcRank.getWidth() - 1);
/*      */       }
/*  799 */     });
/*  800 */     this.tvSubscriptions.addSelectionListener(new TableSelectionListener()
/*      */     {
/*      */       public void selected(TableRowCore[] row) {
/*  803 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  805 */             if (SubscriptionWizard.this.tvSubscriptions.getSelectedRowsSize() == 0) {
/*  806 */               SubscriptionWizard.this.addButton.setEnabled(false);
/*      */             } else {
/*  808 */               SubscriptionWizard.this.addButton.setEnabled(true);
/*  809 */               TableRowCore[] rows = SubscriptionWizard.this.tvSubscriptions.getSelectedRows();
/*  810 */               Subscription subscription = (Subscription)rows[0].getDataSource();
/*  811 */               if (subscription.isSubscribed()) {
/*  812 */                 SubscriptionWizard.this.addButton.setEnabled(false);
/*      */               } else {
/*  814 */                 SubscriptionWizard.this.addButton.setEnabled(true);
/*      */               }
/*  816 */               SubscriptionWizard.this.addButton.setData("subscription", subscription);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */       public void mouseExit(TableRowCore row) {}
/*      */       
/*      */ 
/*      */       public void mouseEnter(TableRowCore row) {}
/*      */       
/*      */       public void focusChanged(TableRowCore focus) {}
/*      */       
/*      */       public void deselected(TableRowCore[] rows)
/*      */       {
/*  832 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  834 */             if (SubscriptionWizard.this.tvSubscriptions.getSelectedRowsSize() == 0) {
/*  835 */               SubscriptionWizard.this.addButton.setEnabled(false);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */       public void defaultSelected(TableRowCore[] rows, int stateMask) {}
/*  832 */     }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  846 */     UIUpdaterSWT.getInstance().addUpdater(new UIUpdatable()
/*      */     {
/*      */       public void updateUI() {
/*  849 */         if (SubscriptionWizard.this.tvSubscriptions != null) {
/*  850 */           SubscriptionWizard.this.tvSubscriptions.refreshTable(false);
/*      */         }
/*      */       }
/*      */       
/*      */       public String getUpdateUIName() {
/*  855 */         return "SubWiz";
/*      */       }
/*      */       
/*  858 */     });
/*  859 */     Listener resizeListener = new Listener()
/*      */     {
/*      */       int last_width;
/*      */       
/*      */       public void handleEvent(Event event) {
/*  864 */         Table table = (Table)event.widget;
/*  865 */         Rectangle rect = table.getClientArea();
/*  866 */         int width = rect.width - 3;
/*      */         
/*  868 */         if (width == this.last_width) {
/*  869 */           return;
/*      */         }
/*      */         
/*  872 */         this.last_width = width;
/*  873 */         int nbColumns = table.getColumnCount();
/*      */         
/*  875 */         if (nbColumns == 1) {
/*  876 */           table.getColumns()[0].setWidth(width);
/*      */         }
/*      */         
/*  879 */         ((Table)event.widget).update();
/*      */       }
/*      */       
/*      */ 
/*  883 */     };
/*  884 */     this.libraryTable.addListener(11, resizeListener);
/*      */     
/*  886 */     final Listener selectionListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  888 */         TableItem item = (TableItem)event.item;
/*  889 */         SubscriptionWizard.this.subscriptions = ((Subscription[])item.getData("subscriptions"));
/*      */         
/*  891 */         SubscriptionWizard.this.tvSubscriptions.removeDataSources(SubscriptionWizard.this.tvSubscriptions.getDataSources().toArray(new Subscription[0]));
/*  892 */         if (SubscriptionWizard.this.subscriptions != null) {
/*  893 */           SubscriptionWizard.this.tvSubscriptions.addDataSources(SubscriptionWizard.this.subscriptions);
/*      */         }
/*  895 */         SubscriptionWizard.this.tvSubscriptions.processDataSourceQueueSync();
/*      */         
/*  897 */         SubscriptionWizard.this.addButton.setEnabled(false);
/*  898 */         SubscriptionWizard.this.addButton.setData("subscription", null);
/*  899 */         SubscriptionWizard.this.tvSubscriptions.setSelectedRows(new TableRowCore[0]);
/*  900 */         if ((SubscriptionWizard.this.subscriptions != null) && (SubscriptionWizard.this.subscriptions.length > 0)) {
/*  901 */           TableRowCore row = SubscriptionWizard.this.tvSubscriptions.getRow(SubscriptionWizard.this.subscriptions[0]);
/*  902 */           if (row != null) {
/*  903 */             row.setSelected(true);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  908 */     };
/*  909 */     this.libraryTable.addListener(13, selectionListener);
/*      */     
/*  911 */     if (this.availableSubscriptions != null) {
/*  912 */       this.libraryTable.addListener(36, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  914 */           TableItem item = (TableItem)event.item;
/*  915 */           int index = SubscriptionWizard.this.libraryTable.indexOf(item);
/*      */           
/*  917 */           SubscriptionUtils.SubscriptionDownloadDetails subInfo = SubscriptionWizard.this.availableSubscriptions[index];
/*  918 */           item.setText(subInfo.getDownload().getDisplayName());
/*  919 */           item.setData("subscriptions", subInfo.getSubscriptions());
/*  920 */           boolean isSubscribed = false;
/*  921 */           Subscription[] subs = subInfo.getSubscriptions();
/*  922 */           for (int i = 0; i < subs.length; i++) {
/*  923 */             if (subs[i].isSubscribed()) isSubscribed = true;
/*      */           }
/*  925 */           if (isSubscribed) {
/*  926 */             item.setForeground(SubscriptionWizard.this.display.getSystemColor(15));
/*      */           }
/*      */           
/*  929 */           if (subInfo.getDownload() == SubscriptionWizard.this.download) {
/*  930 */             SubscriptionWizard.this.libraryTable.setSelection(item);
/*  931 */             selectionListener.handleEvent(event);
/*      */           }
/*  933 */           if ((index == 0) && (SubscriptionWizard.this.download == null)) {
/*  934 */             SubscriptionWizard.this.libraryTable.setSelection(item);
/*  935 */             selectionListener.handleEvent(event);
/*      */           }
/*  937 */           if (SubscriptionWizard.this.libraryTable.getSelectionIndex() == index)
/*      */           {
/*      */ 
/*  940 */             selectionListener.handleEvent(event);
/*      */           }
/*      */           
/*      */         }
/*  944 */       });
/*  945 */       this.libraryTable.setItemCount(this.availableSubscriptions.length);
/*  946 */       if (this.availableSubscriptions.length == 0) {
/*  947 */         this.libraryTable.setVisible(false);
/*  948 */         compEmpty.setVisible(true);
/*      */       }
/*      */     }
/*      */     else {
/*  952 */       this.libraryTable.addListener(36, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  954 */           TableItem item = (TableItem)event.item;
/*  955 */           int index = SubscriptionWizard.this.libraryTable.indexOf(item);
/*  956 */           item.setText("test " + index);
/*      */         }
/*      */         
/*  959 */       });
/*  960 */       this.libraryTable.setItemCount(20);
/*      */     }
/*      */     
/*  963 */     this.addButton.setEnabled(false);
/*  964 */     this.addButton.setData("subscription", null);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  969 */     this.libraryTable.addListener(41, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  971 */         event.height = 20;
/*      */       }
/*      */       
/*  974 */     });
/*  975 */     FormLayout formLayout = new FormLayout();
/*  976 */     composite.setLayout(formLayout);
/*      */     
/*      */ 
/*      */ 
/*  980 */     FormData data = new FormData();
/*  981 */     data.top = new FormAttachment(0, 0);
/*  982 */     data.left = new FormAttachment(40, 0);
/*  983 */     data.bottom = new FormAttachment(100, 0);
/*  984 */     vsep.setLayoutData(data);
/*      */     
/*  986 */     data = new FormData();
/*  987 */     data.top = new FormAttachment(0, 5);
/*  988 */     data.right = new FormAttachment(vsep, 0);
/*  989 */     data.left = new FormAttachment(0, 5);
/*  990 */     subtitle1.setLayoutData(data);
/*      */     
/*  992 */     data = new FormData();
/*  993 */     data.top = new FormAttachment(0, 5);
/*  994 */     data.left = new FormAttachment(vsep, 5);
/*  995 */     data.right = new FormAttachment(100, 0);
/*  996 */     subtitle2.setLayoutData(data);
/*      */     
/*  998 */     data = new FormData();
/*  999 */     data.top = new FormAttachment(subtitle1, 5);
/* 1000 */     data.right = new FormAttachment(vsep, 0);
/* 1001 */     data.left = new FormAttachment(0, 0);
/* 1002 */     hsep1.setLayoutData(data);
/*      */     
/* 1004 */     data = new FormData();
/* 1005 */     data.top = new FormAttachment(subtitle2, 5);
/* 1006 */     data.left = new FormAttachment(vsep, -1);
/* 1007 */     data.right = new FormAttachment(100, 0);
/* 1008 */     hsep2.setLayoutData(data);
/*      */     
/* 1010 */     data = new FormData();
/* 1011 */     data.top = new FormAttachment(hsep1, 0);
/* 1012 */     data.right = new FormAttachment(vsep, 0);
/* 1013 */     data.left = new FormAttachment(0, 0);
/* 1014 */     data.bottom = new FormAttachment(100, 0);
/*      */     
/* 1016 */     if ((this.availableSubscriptions != null) && (this.availableSubscriptions.length > 0)) {
/* 1017 */       this.libraryTable.setLayoutData(data);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1022 */       this.libraryTable.dispose();
/* 1023 */       this.cancelButton.setFocus();
/* 1024 */       this.shell.setDefaultButton(this.cancelButton);
/* 1025 */       compEmpty.setLayoutData(data);
/*      */     }
/*      */     
/* 1028 */     data = new FormData();
/* 1029 */     data.top = new FormAttachment(hsep2, 0);
/* 1030 */     data.left = new FormAttachment(vsep, 0);
/* 1031 */     data.right = new FormAttachment(100, 0);
/* 1032 */     data.bottom = new FormAttachment(100, 0);
/* 1033 */     cTV.setLayoutData(data);
/*      */     
/*      */ 
/* 1036 */     return composite;
/*      */   }
/*      */   
/*      */   private static void initColumns() {
/* 1040 */     if (columnsAdded) {
/* 1041 */       return;
/*      */     }
/* 1043 */     columnsAdded = true;
/* 1044 */     UIManager uiManager = PluginInitializer.getDefaultInterface().getUIManager();
/* 1045 */     TableManager tableManager = uiManager.getTableManager();
/* 1046 */     tableManager.registerColumn(Subscription.class, "SubWizName", new TableColumnCreationListener()
/*      */     {
/*      */       private Image rssIcon;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void tableColumnCreated(org.gudy.azureus2.plugins.ui.tables.TableColumn column)
/*      */       {
/* 1055 */         column.setVisible(column.getTableID().equals("SubscriptionWizard"));
/* 1056 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 1057 */         this.rssIcon = imageLoader.getImage("icon_rss");
/*      */         
/* 1059 */         column.addCellAddedListener(new TableCellAddedListener() {
/*      */           public void cellAdded(TableCell cell) {
/* 1061 */             Subscription sub = (Subscription)cell.getDataSource();
/* 1062 */             if (sub.isSubscribed()) {
/* 1063 */               cell.setForeground(160, 160, 160);
/*      */             }
/* 1065 */             cell.setText(sub.getName());
/* 1066 */             ((TableCellSWT)cell).setIcon(SubscriptionWizard.16.this.rssIcon);
/* 1067 */             cell.setToolTip(sub.getNameEx());
/*      */           }
/*      */         });
/*      */       }
/* 1071 */     });
/* 1072 */     tableManager.registerColumn(Subscription.class, "SubWizRank", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(org.gudy.azureus2.plugins.ui.tables.TableColumn column)
/*      */       {
/* 1076 */         column.setWidthLimits(85, 85);
/* 1077 */         column.setVisible(column.getTableID().equals("SubscriptionWizard"));
/* 1078 */         column.addCellRefreshListener(new TableCellRefreshListener() {
/*      */           public void refresh(TableCell cell) {
/* 1080 */             Subscription sub = (Subscription)cell.getDataSource();
/* 1081 */             cell.setSortValue(sub.getCachedPopularity());
/*      */           }
/*      */         });
/* 1084 */         if ((column instanceof TableColumnCore)) {
/* 1085 */           TableColumnCore columnCore = (TableColumnCore)column;
/* 1086 */           columnCore.setSortAscending(false);
/* 1087 */           columnCore.addCellOtherListener("SWTPaint", new TableCellSWTPaintListener()
/*      */           {
/*      */             public void cellPaint(GC gc, TableCellSWT cell) {
/* 1090 */               Subscription sub = (Subscription)cell.getDataSource();
/*      */               
/* 1092 */               Rectangle bounds = cell.getBounds();
/* 1093 */               bounds.width -= 5;
/* 1094 */               bounds.height -= 7;
/* 1095 */               bounds.x += 2;
/* 1096 */               bounds.y += 3;
/* 1097 */               gc.setBackground(ColorCache.getColor(gc.getDevice(), 255, 255, 255));
/*      */               
/* 1099 */               gc.fillRectangle(bounds);
/* 1100 */               gc.setForeground(ColorCache.getColor(gc.getDevice(), 200, 200, 200));
/*      */               
/* 1102 */               gc.drawRectangle(bounds);
/* 1103 */               bounds.width -= 2;
/* 1104 */               bounds.height -= 2;
/* 1105 */               bounds.x += 1;
/* 1106 */               bounds.y += 1;
/*      */               
/* 1108 */               long popularity = sub.getCachedPopularity();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 1113 */               int rank = 80 * (int)popularity / 1000;
/* 1114 */               if (rank > 80)
/* 1115 */                 rank = 80;
/* 1116 */               if (rank < 5) {
/* 1117 */                 rank = 5;
/*      */               }
/* 1119 */               Rectangle clipping = gc.getClipping();
/*      */               
/* 1121 */               bounds.width = rank;
/* 1122 */               bounds.height -= 1;
/* 1123 */               bounds.x += 1;
/* 1124 */               bounds.y += 1;
/* 1125 */               Utils.setClipping(gc, bounds);
/*      */               
/* 1127 */               ImageLoader imageLoader = ImageLoader.getInstance();
/* 1128 */               Image rankingBars = imageLoader.getImage("ranking_bars");
/* 1129 */               gc.drawImage(rankingBars, bounds.x, bounds.y);
/* 1130 */               imageLoader.releaseImage("ranking_bars");
/*      */               
/* 1132 */               Utils.setClipping(gc, clipping);
/*      */             }
/*      */             
/*      */ 
/*      */           });
/*      */         }
/*      */       }
/* 1139 */     });
/* 1140 */     TableColumnManager tcm = TableColumnManager.getInstance();
/* 1141 */     tcm.setDefaultColumnNames("SubscriptionWizard", new String[] { "SubWizName", "SubWizRank" });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void createFonts()
/*      */   {
/* 1150 */     FontData[] fDatas = this.shell.getFont().getFontData();
/*      */     
/* 1152 */     for (int i = 0; i < fDatas.length; i++) {
/* 1153 */       fDatas[i].setStyle(1);
/*      */     }
/* 1155 */     this.boldFont = new Font(this.display, fDatas);
/*      */     
/*      */ 
/* 1158 */     for (int i = 0; i < fDatas.length; i++) {
/* 1159 */       if (Constants.isOSX) {
/* 1160 */         fDatas[i].setHeight(12);
/*      */       } else {
/* 1162 */         fDatas[i].setHeight(10);
/*      */       }
/*      */     }
/* 1165 */     this.subTitleFont = new Font(this.display, fDatas);
/*      */     
/* 1167 */     for (int i = 0; i < fDatas.length; i++) {
/* 1168 */       if (Constants.isOSX) {
/* 1169 */         fDatas[i].setHeight(17);
/*      */       } else {
/* 1171 */         fDatas[i].setHeight(14);
/*      */       }
/*      */     }
/* 1174 */     this.titleFont = new Font(this.display, fDatas);
/*      */     
/*      */ 
/* 1177 */     this.textInputFont = FontUtils.getFontWithHeight(this.shell.getFont(), null, Utils.isGTK3 ? 12 : 14);
/*      */   }
/*      */   
/*      */ 
/*      */   private void populateFooter(Composite footer)
/*      */   {
/* 1183 */     this.yesButton = new Button(footer, 8);
/* 1184 */     this.yesButton.setText(MessageText.getString("Button.yes"));
/* 1185 */     this.yesButton.setFont(this.boldFont);
/*      */     
/* 1187 */     this.addButton = new Button(footer, 8);
/* 1188 */     this.addButton.setText(MessageText.getString("Button.add"));
/* 1189 */     this.addButton.setFont(this.boldFont);
/*      */     
/* 1191 */     this.saveButton = new Button(footer, 8);
/* 1192 */     this.saveButton.setText(MessageText.getString("Button.save"));
/* 1193 */     this.saveButton.setEnabled(false);
/* 1194 */     this.saveButton.setFont(this.boldFont);
/*      */     
/* 1196 */     this.searchButton = new Button(footer, 8);
/* 1197 */     this.searchButton.setText(MessageText.getString("Button.search"));
/* 1198 */     this.searchButton.setFont(this.boldFont);
/*      */     
/* 1200 */     this.cancelButton = new Button(footer, 8);
/*      */     
/*      */ 
/* 1203 */     this.createButton = new Button(footer, 8);
/* 1204 */     this.createButton.setText(MessageText.getString("Button.createNewSubscription"));
/*      */     
/* 1206 */     this.availableButton = new Button(footer, 8);
/* 1207 */     this.availableButton.setText(MessageText.getString("Button.availableSubscriptions"));
/*      */     
/* 1209 */     FormLayout layout = new FormLayout();
/* 1210 */     layout.marginHeight = 5;
/* 1211 */     layout.marginWidth = 5;
/* 1212 */     layout.spacing = 5;
/*      */     
/* 1214 */     footer.setLayout(layout);
/*      */     
/*      */ 
/* 1217 */     FormData data = new FormData();
/* 1218 */     data.right = new FormAttachment(100);
/* 1219 */     data.width = 100;
/*      */     
/* 1221 */     this.yesButton.setLayoutData(data);
/* 1222 */     this.addButton.setLayoutData(data);
/* 1223 */     this.searchButton.setLayoutData(data);
/*      */     
/* 1225 */     data = new FormData();
/* 1226 */     data.right = new FormAttachment(100);
/* 1227 */     data.width = 100;
/* 1228 */     this.saveButton.setLayoutData(data);
/*      */     
/* 1230 */     data = new FormData();
/* 1231 */     data.right = new FormAttachment(this.saveButton);
/* 1232 */     data.width = 100;
/* 1233 */     this.cancelButton.setLayoutData(data);
/*      */     
/* 1235 */     data = new FormData();
/* 1236 */     data.left = new FormAttachment(0);
/* 1237 */     data.width = 175;
/* 1238 */     this.createButton.setLayoutData(data);
/* 1239 */     this.availableButton.setLayoutData(data);
/*      */     
/*      */ 
/* 1242 */     this.yesButton.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1244 */         COConfigurationManager.setParameter("subscriptions.opted_in", true);
/* 1245 */         COConfigurationManager.save();
/* 1246 */         SubscriptionWizard.this.setMode(2);
/*      */       }
/*      */       
/* 1249 */     });
/* 1250 */     this.createButton.addListener(13, new Listener() {
/*      */       public void handleEvent(Event arg0) {
/* 1252 */         SubscriptionWizard.this.setMode(3);
/*      */       }
/*      */       
/* 1255 */     });
/* 1256 */     this.availableButton.addListener(13, new Listener() {
/*      */       public void handleEvent(Event arg0) {
/* 1258 */         SubscriptionWizard.this.setDefaultAvailableMode();
/*      */       }
/*      */       
/* 1261 */     });
/* 1262 */     this.cancelButton.addListener(13, new Listener() {
/*      */       public void handleEvent(Event arg0) {
/* 1264 */         SubscriptionWizard.this.shell.close();
/*      */       }
/*      */       
/*      */ 
/* 1268 */     });
/* 1269 */     this.rssSaveListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*      */         try {
/* 1272 */           String url_str = SubscriptionWizard.this.feedUrl.getText();
/* 1273 */           URL url = new URL(url_str);
/*      */           
/* 1275 */           Map user_data = new HashMap();
/*      */           
/* 1277 */           user_data.put(SubscriptionManagerUI.SUB_EDIT_MODE_KEY, Boolean.TRUE);
/*      */           
/* 1279 */           boolean anonymous = SubscriptionWizard.this.anonCheck.getSelection();
/*      */           
/* 1281 */           String subs_name = SubscriptionWizard.this.subsName.getText().trim();
/*      */           
/* 1283 */           if (subs_name.length() == 0)
/*      */           {
/* 1285 */             subs_name = url_str;
/*      */           }
/*      */           
/* 1288 */           Subscription subRSS = SubscriptionManagerFactory.getSingleton().createRSS(subs_name, url, 120, anonymous, user_data);
/*      */           
/* 1290 */           if (anonymous)
/*      */           {
/* 1292 */             subRSS.getHistory().setDownloadNetworks(new String[] { "I2P" });
/*      */           }
/*      */           
/* 1295 */           SubscriptionWizard.this.shell.close();
/*      */           
/* 1297 */           String key = "Subscription_" + ByteFormatter.encodeString(subRSS.getPublicKey());
/*      */           
/* 1299 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1300 */           mdi.showEntryByID(key);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1304 */           Utils.reportError(e);
/*      */         }
/*      */         
/*      */       }
/* 1308 */     };
/* 1309 */     this.saveButton.addListener(13, this.rssSaveListener);
/*      */     
/* 1311 */     this.addButton.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1313 */         Subscription subscription = (Subscription)SubscriptionWizard.this.addButton.getData("subscription");
/* 1314 */         if (subscription != null) {
/* 1315 */           subscription.setSubscribed(true);
/* 1316 */           subscription.requestAttention();
/* 1317 */           SubscriptionWizard.this.shell.close();
/*      */         }
/*      */         
/*      */       }
/* 1321 */     });
/* 1322 */     this.searchListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1324 */         UIFunctionsManager.getUIFunctions().doSearch(SubscriptionWizard.this.searchInput.getText(), true);
/* 1325 */         SubscriptionWizard.this.shell.close();
/*      */       }
/*      */       
/* 1328 */     };
/* 1329 */     this.searchButton.addListener(13, this.searchListener);
/*      */   }
/*      */   
/*      */   private void setDefaultAvailableMode()
/*      */   {
/* 1334 */     boolean opted_in = COConfigurationManager.getBooleanParameter("subscriptions.opted_in");
/* 1335 */     if (!opted_in) {
/* 1336 */       setMode(1);
/*      */     } else {
/* 1338 */       setMode(2);
/*      */     }
/*      */   }
/*      */   
/*      */   private void setMode(int mode) {
/* 1343 */     this.addButton.setVisible(false);
/* 1344 */     this.searchButton.setVisible(false);
/* 1345 */     this.saveButton.setVisible(false);
/* 1346 */     this.yesButton.setVisible(false);
/* 1347 */     this.createButton.setVisible(false);
/* 1348 */     this.availableButton.setVisible(false);
/* 1349 */     this.cancelButton.setText(MessageText.getString("Button.cancel"));
/*      */     
/* 1351 */     String titleText = this.TITLE_OPT_IN;
/*      */     
/* 1353 */     if ((mode != 1) && 
/* 1354 */       (this.rss_feed_url != null)) {
/* 1355 */       mode = 4;
/* 1356 */       this.feedUrl.setText(this.rss_feed_url.toExternalForm());
/* 1357 */       this.rss_feed_url = null;
/*      */     }
/*      */     
/* 1360 */     switch (mode) {
/*      */     case 2: 
/* 1362 */       this.mainLayout.topControl = this.availableSubscriptionComposite;
/* 1363 */       titleText = this.TITLE_SUBSCRIBE;
/* 1364 */       this.createButton.setVisible(true);
/* 1365 */       this.addButton.setVisible(true);
/* 1366 */       this.shell.setDefaultButton(this.addButton);
/* 1367 */       break;
/*      */     
/*      */     case 4: 
/* 1370 */       this.mainLayout.topControl = this.createComposite;
/* 1371 */       this.createTabFolder.setSelection(this.createRSSTabItem);
/* 1372 */       titleText = this.TITLE_CREATE;
/* 1373 */       this.availableButton.setVisible(true);
/* 1374 */       this.saveButton.setVisible(true);
/* 1375 */       this.shell.setDefaultButton(this.saveButton);
/* 1376 */       break;
/*      */     
/*      */     case 3: 
/* 1379 */       this.mainLayout.topControl = this.createComposite;
/* 1380 */       this.createTabFolder.setSelection(this.createSearchTabItem);
/* 1381 */       titleText = this.TITLE_CREATE;
/* 1382 */       this.availableButton.setVisible(true);
/* 1383 */       this.searchButton.setVisible(true);
/* 1384 */       this.shell.setDefaultButton(this.searchButton);
/* 1385 */       break;
/*      */     
/*      */     case 1: 
/*      */     default: 
/* 1389 */       this.mainLayout.topControl = this.optinComposite;
/* 1390 */       this.cancelButton.setText(MessageText.getString("Button.no"));
/* 1391 */       this.createButton.setVisible(true);
/* 1392 */       this.yesButton.setVisible(true);
/* 1393 */       this.shell.setDefaultButton(this.yesButton);
/*      */     }
/*      */     
/*      */     
/* 1397 */     this.main.layout(true, true);
/*      */     
/* 1399 */     this.title.setText(titleText);
/*      */   }
/*      */   
/*      */   public static void main(String[] args) {
/* 1403 */     SubscriptionWizard sw = new SubscriptionWizard();
/*      */     
/* 1405 */     while (!sw.shell.isDisposed()) {
/* 1406 */       if (!sw.display.readAndDispatch()) {
/* 1407 */         sw.display.sleep();
/*      */       }
/*      */     }
/*      */     
/* 1411 */     sw.display.dispose();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */