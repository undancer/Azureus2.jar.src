/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.custom.SashForm;
/*      */ import org.eclipse.swt.custom.ScrolledComposite;
/*      */ import org.eclipse.swt.custom.StackLayout;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Combo;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.ScrollBar;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.eclipse.swt.widgets.Tree;
/*      */ import org.eclipse.swt.widgets.TreeItem;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Timer;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.config.ConfigSection;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionPlugins;
/*      */ 
/*      */ public class ConfigView implements org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener
/*      */ {
/*      */   public static final String VIEW_ID = "ConfigView";
/*   56 */   private static final org.gudy.azureus2.core3.logging.LogIDs LOGID = org.gudy.azureus2.core3.logging.LogIDs.GUI;
/*      */   
/*      */   public static final String sSectionPrefix = "ConfigView.section.";
/*   59 */   Map<TreeItem, ConfigSection> sections = new java.util.HashMap();
/*      */   
/*   61 */   java.util.List<ConfigSection> sectionsCreated = new ArrayList(1);
/*      */   
/*      */   Composite cConfig;
/*      */   Composite cConfigSection;
/*      */   StackLayout layoutConfigSection;
/*      */   Label lHeader;
/*      */   Label usermodeHint;
/*      */   Font headerFont;
/*      */   Font filterFoundFont;
/*      */   Tree tree;
/*      */   ArrayList<ConfigSection> pluginSections;
/*      */   private Timer filterDelayTimer;
/*   73 */   private String filterText = "";
/*      */   
/*      */   private Label lblX;
/*      */   
/*      */   private Listener scResizeListener;
/*      */   
/*      */   private Image imgSmallX;
/*      */   
/*      */   private Image imgSmallXGray;
/*      */   
/*      */   private String startSection;
/*      */   private UISWTView swtView;
/*      */   
/*      */   private void initialize(final Composite composite)
/*      */   {
/*   88 */     this.cConfig = new Composite(composite, 0);
/*      */     
/*   90 */     GridLayout configLayout = new GridLayout();
/*   91 */     configLayout.marginHeight = 0;
/*   92 */     configLayout.marginWidth = 0;
/*   93 */     this.cConfig.setLayout(configLayout);
/*   94 */     GridData gridData = new GridData(1808);
/*   95 */     Utils.setLayoutData(this.cConfig, gridData);
/*      */     
/*   97 */     final Label label = new Label(this.cConfig, 16777216);
/*   98 */     Messages.setLanguageText(label, "view.waiting.core");
/*   99 */     gridData = new GridData(1808);
/*  100 */     Utils.setLayoutData(label, gridData);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  105 */     com.aelitis.azureus.core.AzureusCoreFactory.addCoreRunningListener(new com.aelitis.azureus.core.AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(com.aelitis.azureus.core.AzureusCore core) {
/*  107 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  109 */             ConfigView.this._initialize(ConfigView.1.this.val$composite);
/*  110 */             ConfigView.1.this.val$label.dispose();
/*  111 */             ConfigView.1.this.val$composite.layout(true, true);
/*      */           }
/*      */         });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _initialize(Composite composite)
/*      */   {
/*      */     try
/*      */     {
/*  140 */       Display d = composite.getDisplay();
/*      */       
/*      */ 
/*  143 */       SashForm form = new SashForm(this.cConfig, 256);
/*  144 */       GridData gridData = new GridData(1808);
/*  145 */       Utils.setLayoutData(form, gridData);
/*      */       
/*  147 */       Composite cLeftSide = new Composite(form, 2048);
/*  148 */       gridData = new GridData(1808);
/*  149 */       Utils.setLayoutData(cLeftSide, gridData);
/*      */       
/*  151 */       FormLayout layout = new FormLayout();
/*  152 */       cLeftSide.setLayout(layout);
/*      */       
/*  154 */       Composite cFilterArea = new Composite(cLeftSide, 0);
/*  155 */       cFilterArea.setLayout(new FormLayout());
/*      */       
/*  157 */       final Text txtFilter = new Text(cFilterArea, 2048);
/*  158 */       final String sFilterText = MessageText.getString("ConfigView.filter");
/*  159 */       txtFilter.setText(sFilterText);
/*  160 */       txtFilter.selectAll();
/*  161 */       txtFilter.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
/*      */         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
/*  163 */           ConfigView.this.filterTree(txtFilter.getText());
/*      */         }
/*  165 */       });
/*  166 */       txtFilter.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
/*      */         public void mouseDown(MouseEvent e) {
/*  168 */           if (txtFilter.getText().equals(sFilterText)) {
/*  169 */             txtFilter.selectAll();
/*      */           }
/*      */         }
/*  172 */       });
/*  173 */       txtFilter.setFocus();
/*      */       
/*  175 */       ImageLoader imageLoader = ImageLoader.getInstance();
/*  176 */       this.imgSmallXGray = imageLoader.getImage("smallx-gray");
/*  177 */       this.imgSmallX = imageLoader.getImage("smallx");
/*      */       
/*  179 */       this.lblX = new Label(cFilterArea, 64);
/*  180 */       Messages.setLanguageTooltip(this.lblX, "MyTorrentsView.clearFilter.tooltip");
/*  181 */       this.lblX.setImage(this.imgSmallXGray);
/*  182 */       this.lblX.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
/*      */         public void mouseUp(MouseEvent e) {
/*  184 */           txtFilter.setText("");
/*      */         }
/*      */         
/*  187 */       });
/*  188 */       this.lblX.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
/*      */         public void widgetDisposed(DisposeEvent arg0) {
/*  190 */           ImageLoader imageLoader = ImageLoader.getInstance();
/*  191 */           imageLoader.releaseImage("smallx-gray");
/*  192 */           imageLoader.releaseImage("smallx");
/*      */         }
/*      */         
/*  195 */       });
/*  196 */       Label lblSearch = new Label(cFilterArea, 0);
/*  197 */       imageLoader.setLabelImage(lblSearch, "search");
/*      */       
/*  199 */       this.tree = new Tree(cLeftSide, 0);
/*  200 */       FontData[] fontData = this.tree.getFont().getFontData();
/*  201 */       fontData[0].setStyle(1);
/*  202 */       this.filterFoundFont = new Font(d, fontData);
/*      */       
/*      */ 
/*      */ 
/*  206 */       FormData formData = new FormData();
/*  207 */       formData.bottom = new FormAttachment(100, -5);
/*  208 */       formData.left = new FormAttachment(0, 0);
/*  209 */       formData.right = new FormAttachment(100, 0);
/*  210 */       Utils.setLayoutData(cFilterArea, formData);
/*      */       
/*  212 */       formData = new FormData();
/*  213 */       formData.top = new FormAttachment(0, 5);
/*  214 */       formData.left = new FormAttachment(0, 5);
/*  215 */       Utils.setLayoutData(lblSearch, formData);
/*      */       
/*  217 */       formData = new FormData();
/*  218 */       formData.top = new FormAttachment(0, 5);
/*  219 */       formData.left = new FormAttachment(lblSearch, 5);
/*  220 */       formData.right = new FormAttachment(this.lblX, -3);
/*  221 */       Utils.setLayoutData(txtFilter, formData);
/*      */       
/*  223 */       formData = new FormData();
/*  224 */       formData.top = new FormAttachment(0, 5);
/*  225 */       formData.right = new FormAttachment(100, -5);
/*  226 */       Utils.setLayoutData(this.lblX, formData);
/*      */       
/*  228 */       formData = new FormData();
/*  229 */       formData.top = new FormAttachment(0, 0);
/*  230 */       formData.left = new FormAttachment(0, 0);
/*  231 */       formData.right = new FormAttachment(100, 0);
/*  232 */       formData.bottom = new FormAttachment(cFilterArea, -1);
/*  233 */       Utils.setLayoutData(this.tree, formData);
/*      */       
/*  235 */       Composite cRightSide = new Composite(form, 0);
/*  236 */       GridLayout configLayout = new GridLayout();
/*  237 */       configLayout.marginHeight = 3;
/*  238 */       configLayout.marginWidth = 0;
/*  239 */       cRightSide.setLayout(configLayout);
/*      */       
/*      */ 
/*      */ 
/*  243 */       Composite cHeader = new Composite(cRightSide, 2048);
/*      */       
/*  245 */       configLayout = new GridLayout();
/*  246 */       configLayout.marginHeight = 3;
/*  247 */       configLayout.marginWidth = 0;
/*  248 */       configLayout.numColumns = 2;
/*  249 */       configLayout.marginRight = 5;
/*  250 */       cHeader.setLayout(configLayout);
/*  251 */       gridData = new GridData(772);
/*  252 */       Utils.setLayoutData(cHeader, gridData);
/*      */       
/*  254 */       cHeader.setBackground(d.getSystemColor(26));
/*  255 */       cHeader.setForeground(d.getSystemColor(27));
/*      */       
/*  257 */       this.lHeader = new Label(cHeader, 0);
/*  258 */       this.lHeader.setBackground(d.getSystemColor(26));
/*  259 */       this.lHeader.setForeground(d.getSystemColor(27));
/*  260 */       fontData = this.lHeader.getFont().getFontData();
/*  261 */       fontData[0].setStyle(1);
/*  262 */       int fontHeight = (int)(fontData[0].getHeight() * 1.2D);
/*  263 */       fontData[0].setHeight(fontHeight);
/*  264 */       this.headerFont = new Font(d, fontData);
/*  265 */       this.lHeader.setFont(this.headerFont);
/*  266 */       gridData = new GridData(36);
/*  267 */       Utils.setLayoutData(this.lHeader, gridData);
/*      */       
/*      */ 
/*  270 */       this.usermodeHint = new Label(cHeader, 0);
/*  271 */       this.usermodeHint.setBackground(d.getSystemColor(26));
/*  272 */       this.usermodeHint.setForeground(d.getSystemColor(27));
/*  273 */       gridData = new GridData(644);
/*  274 */       Utils.setLayoutData(this.usermodeHint, gridData);
/*      */       
/*  276 */       org.eclipse.swt.widgets.Menu headerMenu = new org.eclipse.swt.widgets.Menu(cHeader.getShell(), 8);
/*      */       
/*  278 */       MenuItem menuShortCut = new MenuItem(headerMenu, 8);
/*  279 */       Messages.setLanguageText(menuShortCut, "label.set.shortcut");
/*      */       
/*  281 */       menuShortCut.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  284 */           final TreeItem tree_item = (TreeItem)ConfigView.this.lHeader.getData("TreeItem");
/*      */           
/*  286 */           if (tree_item != null)
/*      */           {
/*  288 */             final String id = (String)tree_item.getData("ID");
/*      */             
/*  290 */             if (id != null)
/*      */             {
/*  292 */               SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("config.dialog.shortcut.title", "config.dialog.shortcut.text");
/*      */               
/*      */ 
/*  295 */               entryWindow.setPreenteredText(COConfigurationManager.getStringParameter("config.section.shortcut.key." + id, ""), false);
/*  296 */               entryWindow.setTextLimit(1);
/*  297 */               entryWindow.prompt(new org.gudy.azureus2.plugins.ui.UIInputReceiverListener() {
/*      */                 public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/*  299 */                   if (!entryWindow.hasSubmittedInput()) {
/*  300 */                     return;
/*      */                   }
/*  302 */                   String sReturn = entryWindow.getSubmittedInput();
/*  303 */                   if (sReturn != null)
/*      */                   {
/*  305 */                     sReturn = sReturn.trim();
/*      */                     
/*  307 */                     if (sReturn.length() > 1)
/*      */                     {
/*  309 */                       sReturn = sReturn.substring(0, 1);
/*      */                     }
/*      */                     
/*  312 */                     COConfigurationManager.setParameter("config.section.shortcut.key." + id, sReturn);
/*      */                     
/*  314 */                     ConfigView.this.updateHeader(tree_item);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*  322 */       });
/*  323 */       cHeader.setMenu(headerMenu);
/*  324 */       this.lHeader.setMenu(headerMenu);
/*  325 */       this.usermodeHint.setMenu(headerMenu);
/*      */       
/*      */ 
/*  328 */       this.cConfigSection = new Composite(cRightSide, 0);
/*  329 */       this.layoutConfigSection = new StackLayout();
/*  330 */       this.cConfigSection.setLayout(this.layoutConfigSection);
/*  331 */       gridData = new GridData(1808);
/*  332 */       gridData.horizontalIndent = 2;
/*  333 */       Utils.setLayoutData(this.cConfigSection, gridData);
/*      */       
/*  335 */       form.setWeights(new int[] { 20, 80 });
/*      */       
/*  337 */       this.tree.addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  339 */           Tree tree = (Tree)e.getSource();
/*      */           
/*      */ 
/*      */ 
/*  343 */           if (tree.getSelection().length > 0) {
/*  344 */             ConfigView.this.showSection(tree.getSelection()[0], false);
/*      */           }
/*      */         }
/*  347 */       });
/*  348 */       this.tree.addListener(14, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  350 */           TreeItem item = (TreeItem)e.item;
/*  351 */           if (item != null)
/*  352 */             item.setExpanded(!item.getExpanded());
/*      */         }
/*      */       });
/*      */     } catch (Exception e) {
/*  356 */       Logger.log(new LogEvent(LOGID, "Error initializing ConfigView", e));
/*      */     }
/*      */     
/*  359 */     this.scResizeListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  361 */         ConfigView.this.setupSC((ScrolledComposite)event.widget);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  372 */     };
/*  373 */     this.pluginSections = org.gudy.azureus2.pluginsimpl.local.ui.config.ConfigSectionRepository.getInstance().getList();
/*      */     
/*  375 */     ConfigSection[] internalSections = { new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionMode(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionStartShutdown(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionBackupRestore(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionConnection(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionConnectionProxy(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionConnectionAdvanced(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionConnectionEncryption(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionConnectionDNS(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTransfer(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTransferAutoSpeedSelect(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTransferAutoSpeed(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTransferAutoSpeedBeta(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTransferLAN(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionFile(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionFileMove(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionFileTorrents(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionFileTorrentsDecoding(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionFilePerformance(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterface(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfaceLanguage(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfaceStart(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfaceDisplay(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfaceTables(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfaceColor(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfaceAlerts(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfacePassword(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionInterfaceLegacy(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionIPFilter(), new ConfigSectionPlugins(this), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionStats(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTracker(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTrackerClient(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionTrackerServer(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionSecurity(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionSharing(), new org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionLogging() };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  414 */     this.pluginSections.addAll(0, java.util.Arrays.asList(internalSections));
/*      */     
/*  416 */     for (int i = 0; i < this.pluginSections.size(); i++)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  421 */       boolean plugin_section = i >= internalSections.length;
/*      */       
/*  423 */       ConfigSection section = (ConfigSection)this.pluginSections.get(i);
/*      */       
/*  425 */       if ((section instanceof UISWTConfigSection)) {
/*      */         String name;
/*      */         try {
/*  428 */           name = section.configSectionGetName();
/*      */         } catch (Exception e) {
/*  430 */           Logger.log(new LogEvent(LOGID, "A ConfigSection plugin caused an error while trying to call its configSectionGetName function", e));
/*      */           
/*      */ 
/*  433 */           name = "Bad Plugin";
/*      */         }
/*      */         
/*  436 */         String section_key = name;
/*      */         
/*  438 */         if (plugin_section)
/*      */         {
/*      */ 
/*      */ 
/*  442 */           if (!MessageText.keyExists(section_key))
/*      */           {
/*  444 */             section_key = "ConfigView.section." + name;
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/*  449 */           section_key = "ConfigView.section." + name;
/*      */         }
/*      */         
/*  452 */         String section_name = MessageText.getString(section_key);
/*      */         
/*      */         try
/*      */         {
/*  456 */           String location = section.configSectionGetParentSection();
/*      */           TreeItem treeItem;
/*  458 */           TreeItem treeItem; if ((location.length() == 0) || (location.equalsIgnoreCase("root")))
/*      */           {
/*      */ 
/*  461 */             treeItem = new TreeItem(this.tree, 0);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  466 */             TreeItem treeItemFound = findTreeItem(this.tree, location);
/*  467 */             TreeItem treeItem; if (treeItemFound != null) { TreeItem treeItem;
/*  468 */               if (location.equalsIgnoreCase("plugins"))
/*      */               {
/*  470 */                 int position = findInsertPointFor(section_name, treeItemFound);
/*  471 */                 TreeItem treeItem; if (position == -1) {
/*  472 */                   treeItem = new TreeItem(treeItemFound, 0);
/*      */                 }
/*      */                 else {
/*  475 */                   treeItem = new TreeItem(treeItemFound, 0, position);
/*      */                 }
/*      */               }
/*      */               else {
/*  479 */                 treeItem = new TreeItem(treeItemFound, 0);
/*      */               }
/*      */             } else {
/*  482 */               treeItem = new TreeItem(this.tree, 0);
/*      */             }
/*      */           }
/*      */           
/*  486 */           ScrolledComposite sc = new ScrolledComposite(this.cConfigSection, 768);
/*  487 */           sc.setExpandHorizontal(true);
/*  488 */           sc.setExpandVertical(true);
/*  489 */           Utils.setLayoutData(sc, new GridData(1808));
/*  490 */           sc.getVerticalBar().setIncrement(16);
/*  491 */           sc.addListener(11, this.scResizeListener);
/*      */           
/*  493 */           if (i == 0) {
/*  494 */             Composite c = ((UISWTConfigSection)section).configSectionCreate(sc);
/*  495 */             this.sectionsCreated.add(section);
/*  496 */             sc.setContent(c);
/*      */           }
/*      */           
/*  499 */           Messages.setLanguageText(treeItem, section_key);
/*  500 */           treeItem.setData("Panel", sc);
/*  501 */           treeItem.setData("ID", name);
/*  502 */           treeItem.setData("ConfigSectionSWT", section);
/*      */           
/*  504 */           this.sections.put(treeItem, section);
/*      */           
/*      */ 
/*      */ 
/*  508 */           if ((section instanceof ConfigSectionPlugins))
/*  509 */             ((ConfigSectionPlugins)section).initPluginSubSections();
/*      */         } catch (Exception e) {
/*  511 */           Logger.log(new LogEvent(LOGID, "ConfigSection plugin '" + name + "' caused an error", e));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  517 */     final Display d = composite.getDisplay();
/*      */     
/*  519 */     final Listener shortcut_listener = new Listener()
/*      */     {
/*      */       public void handleEvent(Event e)
/*      */       {
/*      */         char key;
/*      */         
/*      */ 
/*  526 */         if (((e.stateMask & (org.eclipse.swt.SWT.MOD1 | 0x40000)) != 0) || (e.keyCode == 4194304))
/*      */         {
/*  528 */           key = e.character;
/*      */           
/*  530 */           if ((key <= '\032') && (key > 0)) {
/*  531 */             key = (char)(key + '`');
/*      */           }
/*      */           
/*  534 */           if ((e.stateMask & 0x20000) != 0) {
/*  535 */             key = Character.toUpperCase(key);
/*      */           }
/*  537 */           if (!Character.isISOControl(key))
/*      */           {
/*  539 */             for (TreeItem ti : ConfigView.this.sections.keySet())
/*      */             {
/*  541 */               String id = (String)ti.getData("ID");
/*      */               
/*  543 */               if (id != null)
/*      */               {
/*  545 */                 String shortcut = COConfigurationManager.getStringParameter("config.section.shortcut.key." + id, "");
/*      */                 
/*  547 */                 if (shortcut.equals(String.valueOf(key)))
/*      */                 {
/*      */ 
/*      */ 
/*  551 */                   ConfigView.this.selectSection(id, true);
/*      */                   
/*  553 */                   e.doit = false;
/*      */                   
/*  555 */                   break;
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  563 */     };
/*  564 */     d.addFilter(1, shortcut_listener);
/*      */     
/*  566 */     this.cConfigSection.addDisposeListener(new org.eclipse.swt.events.DisposeListener()
/*      */     {
/*      */       public void widgetDisposed(DisposeEvent e)
/*      */       {
/*  570 */         d.removeFilter(1, shortcut_listener);
/*      */       }
/*      */     });
/*      */     
/*  574 */     if ((composite instanceof Shell)) {
/*  575 */       initApplyCloseButton();
/*      */     } else {
/*  577 */       initSaveButton();
/*      */     }
/*      */     
/*  580 */     if ((this.startSection != null) && 
/*  581 */       (selectSection(this.startSection, false))) {
/*  582 */       return;
/*      */     }
/*      */     
/*      */ 
/*  586 */     TreeItem selection = getLatestSelection();
/*      */     
/*  588 */     TreeItem[] items = { selection };
/*      */     
/*  590 */     this.tree.setSelection(items);
/*      */     
/*      */ 
/*      */ 
/*  594 */     showSection(selection, false);
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
/*      */   private void setupSC(ScrolledComposite sc)
/*      */   {
/*  621 */     Composite c = (Composite)sc.getContent();
/*  622 */     if (c != null) {
/*  623 */       Point size1 = c.computeSize(sc.getClientArea().width, -1);
/*  624 */       Point size = c.computeSize(-1, size1.y);
/*  625 */       sc.setMinSize(size);
/*      */     }
/*  627 */     sc.getVerticalBar().setPageIncrement(sc.getSize().y);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void filterTree(String text)
/*      */   {
/*  635 */     this.filterText = text;
/*  636 */     if (this.filterDelayTimer != null) {
/*  637 */       this.filterDelayTimer.destroy();
/*      */     }
/*      */     
/*  640 */     if ((this.lblX != null) && (!this.lblX.isDisposed())) {
/*  641 */       Image img = this.filterText.length() > 0 ? this.imgSmallX : this.imgSmallXGray;
/*      */       
/*  643 */       this.lblX.setImage(img);
/*      */     }
/*      */     
/*      */ 
/*  647 */     this.filterDelayTimer = new Timer("Filter");
/*  648 */     this.filterDelayTimer.addEvent(org.gudy.azureus2.core3.util.SystemTime.getCurrentTime() + 300L, new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */     {
/*      */       public void perform(org.gudy.azureus2.core3.util.TimerEvent event) {
/*  651 */         ConfigView.this.filterDelayTimer.destroy();
/*  652 */         ConfigView.this.filterDelayTimer = null;
/*      */         
/*  654 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  656 */             if (ConfigView.this.filterDelayTimer != null) {
/*  657 */               return;
/*      */             }
/*  659 */             if (ConfigView.this.tree.isDisposed()) {
/*  660 */               return;
/*      */             }
/*      */             
/*  663 */             Shell shell = ConfigView.this.tree.getShell();
/*  664 */             if (shell != null) {
/*  665 */               shell.setCursor(shell.getDisplay().getSystemCursor(1));
/*      */             }
/*      */             try
/*      */             {
/*  669 */               ArrayList<TreeItem> foundItems = new ArrayList();
/*  670 */               TreeItem[] items = ConfigView.this.tree.getItems();
/*      */               try {
/*  672 */                 ConfigView.this.tree.setRedraw(false);
/*  673 */                 for (int i = 0; i < items.length; i++) {
/*  674 */                   items[i].setExpanded(false);
/*      */                 }
/*      */                 
/*  677 */                 ConfigView.this.filterTree(items, ConfigView.this.filterText, foundItems);
/*      */               } finally {
/*  679 */                 ConfigView.this.tree.setRedraw(true);
/*      */               }
/*      */             } finally { TreeItem[] selection;
/*  682 */               if (shell != null) {
/*  683 */                 shell.setCursor(null);
/*      */               }
/*  685 */               TreeItem[] selection = ConfigView.this.tree.getSelection();
/*  686 */               if ((selection != null) && (selection.length > 0)) {
/*  687 */                 ConfigView.this.showSection(selection[0], false);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void filterTree(TreeItem[] items, String text, ArrayList<TreeItem> foundItems)
/*      */   {
/*  698 */     text = text.toLowerCase();
/*  699 */     for (int i = 0; i < items.length; i++) {
/*  700 */       ensureSectionBuilt(items[i], false);
/*  701 */       ScrolledComposite composite = (ScrolledComposite)items[i].getData("Panel");
/*      */       
/*  703 */       if ((text.length() > 0) && ((items[i].getText().toLowerCase().contains(text)) || (compositeHasText(composite, text))))
/*      */       {
/*      */ 
/*  706 */         foundItems.add(items[i]);
/*      */         
/*  708 */         ensureExpandedTo(items[i]);
/*  709 */         items[i].setFont(this.filterFoundFont);
/*      */       } else {
/*  711 */         items[i].setFont(null);
/*      */       }
/*  713 */       filterTree(items[i].getItems(), text, foundItems);
/*      */     }
/*      */   }
/*      */   
/*      */   private void ensureExpandedTo(TreeItem item) {
/*  718 */     TreeItem itemParent = item.getParentItem();
/*  719 */     if (itemParent != null) {
/*  720 */       itemParent.setExpanded(true);
/*  721 */       ensureExpandedTo(itemParent);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean compositeHasText(Composite composite, String text)
/*      */   {
/*  731 */     Control[] children = composite.getChildren();
/*      */     
/*  733 */     for (int i = 0; i < children.length; i++) {
/*  734 */       Control child = children[i];
/*  735 */       if ((child instanceof Label)) {
/*  736 */         if (((Label)child).getText().toLowerCase().contains(text)) {
/*  737 */           return true;
/*      */         }
/*  739 */       } else if ((child instanceof Group)) {
/*  740 */         if (((Group)child).getText().toLowerCase().contains(text)) {
/*  741 */           return true;
/*      */         }
/*  743 */       } else if ((child instanceof Button)) {
/*  744 */         if (((Button)child).getText().toLowerCase().contains(text)) {
/*  745 */           return true;
/*      */         }
/*  747 */       } else if ((child instanceof org.eclipse.swt.widgets.List)) {
/*  748 */         String[] items = ((org.eclipse.swt.widgets.List)child).getItems();
/*  749 */         for (String item : items) {
/*  750 */           if (item.toLowerCase().contains(text)) {
/*  751 */             return true;
/*      */           }
/*      */         }
/*  754 */       } else if ((child instanceof Combo)) {
/*  755 */         String[] items = ((Combo)child).getItems();
/*  756 */         for (String item : items) {
/*  757 */           if (item.toLowerCase().contains(text)) {
/*  758 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  763 */       if (((child instanceof Composite)) && 
/*  764 */         (compositeHasText((Composite)child, text))) {
/*  765 */         return true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  770 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void saveLatestSelection(TreeItem item)
/*      */   {
/*  777 */     String path = "";
/*      */     
/*  779 */     while (item != null)
/*      */     {
/*  781 */       path = item.getText() + (path.length() == 0 ? "" : new StringBuilder().append("$").append(path).toString());
/*      */       
/*  783 */       item = item.getParentItem();
/*      */     }
/*      */     
/*  786 */     COConfigurationManager.setParameter("ConfigView.section.last.selection", path);
/*      */   }
/*      */   
/*      */ 
/*      */   private TreeItem getLatestSelection()
/*      */   {
/*  792 */     String path = COConfigurationManager.getStringParameter("ConfigView.section.last.selection", "");
/*      */     
/*  794 */     String[] bits = path.split("\\$");
/*      */     
/*  796 */     TreeItem[] items = this.tree.getItems();
/*      */     
/*  798 */     TreeItem current = null;
/*      */     
/*  800 */     boolean located = false;
/*      */     
/*  802 */     for (int i = 0; i < bits.length; i++)
/*      */     {
/*  804 */       String bit = bits[i];
/*      */       
/*  806 */       boolean found = false;
/*      */       
/*  808 */       for (int j = 0; j < items.length; j++)
/*      */       {
/*  810 */         if (items[j].getText().equals(bit))
/*      */         {
/*  812 */           current = items[j];
/*      */           
/*  814 */           items = current.getItems();
/*      */           
/*  816 */           found = true;
/*      */           
/*  818 */           if (i != bits.length - 1)
/*      */             break;
/*  820 */           located = true; break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  827 */       if (!found) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  833 */     TreeItem result = located ? current : this.tree.getItems()[0];
/*      */     
/*  835 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void showSection(TreeItem section, boolean focus)
/*      */   {
/*  843 */     saveLatestSelection(section);
/*      */     
/*  845 */     ScrolledComposite item = (ScrolledComposite)section.getData("Panel");
/*      */     
/*  847 */     if (item != null)
/*      */     {
/*  849 */       ensureSectionBuilt(section, true);
/*      */       
/*  851 */       this.layoutConfigSection.topControl = item;
/*      */       
/*  853 */       setupSC(item);
/*      */       
/*  855 */       if ((this.filterText != null) && (this.filterText.length() > 0)) {
/*  856 */         hilightText(item, this.filterText);
/*  857 */         item.layout(true, true);
/*      */       }
/*      */       
/*  860 */       this.cConfigSection.layout();
/*      */       
/*  862 */       updateHeader(section);
/*      */       
/*  864 */       if (focus) {
/*  865 */         this.layoutConfigSection.topControl.traverse(16);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void hilightText(Composite c, String text) {
/*  871 */     Control[] children = c.getChildren();
/*  872 */     for (Control child : children) {
/*  873 */       if ((child instanceof Composite)) {
/*  874 */         hilightText((Composite)child, text);
/*      */       }
/*      */       
/*  877 */       if ((child instanceof Label)) {
/*  878 */         if (((Label)child).getText().toLowerCase().contains(text)) {
/*  879 */           hilightControl(child);
/*      */         }
/*  881 */       } else if ((child instanceof Group)) {
/*  882 */         if (((Group)child).getText().toLowerCase().contains(text)) {
/*  883 */           hilightControl(child);
/*      */         }
/*  885 */       } else if ((child instanceof Button)) {
/*  886 */         if (((Button)child).getText().toLowerCase().contains(text)) {
/*  887 */           hilightControl(child);
/*      */         }
/*  889 */       } else if ((child instanceof org.eclipse.swt.widgets.List)) {
/*  890 */         String[] items = ((org.eclipse.swt.widgets.List)child).getItems();
/*  891 */         for (String item : items) {
/*  892 */           if (item.toLowerCase().contains(text)) {
/*  893 */             hilightControl(child);
/*  894 */             break;
/*      */           }
/*      */         }
/*  897 */       } else if ((child instanceof Combo)) {
/*  898 */         String[] items = ((Combo)child).getItems();
/*  899 */         for (String item : items) {
/*  900 */           if (item.toLowerCase().contains(text)) {
/*  901 */             hilightControl(child);
/*  902 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void hilightControl(Control child)
/*      */   {
/*  916 */     child.setFont(this.headerFont);
/*  917 */     child.setBackground(child.getDisplay().getSystemColor(29));
/*  918 */     child.setForeground(child.getDisplay().getSystemColor(28));
/*      */   }
/*      */   
/*      */   private void ensureSectionBuilt(TreeItem treeSection, boolean recreateIfAlreadyThere) {
/*  922 */     ScrolledComposite item = (ScrolledComposite)treeSection.getData("Panel");
/*      */     
/*  924 */     if (item != null)
/*      */     {
/*  926 */       ConfigSection configSection = (ConfigSection)treeSection.getData("ConfigSectionSWT");
/*      */       
/*  928 */       if (configSection != null)
/*      */       {
/*  930 */         Control previous = item.getContent();
/*  931 */         if ((previous instanceof Composite)) {
/*  932 */           if (!recreateIfAlreadyThere) {
/*  933 */             return;
/*      */           }
/*  935 */           configSection.configSectionDelete();
/*  936 */           this.sectionsCreated.remove(configSection);
/*  937 */           Utils.disposeComposite((Composite)previous, true);
/*      */         }
/*      */         
/*  940 */         Composite c = ((UISWTConfigSection)configSection).configSectionCreate(item);
/*      */         
/*  942 */         this.sectionsCreated.add(configSection);
/*      */         
/*  944 */         item.setContent(c);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void updateHeader(TreeItem section) {
/*  950 */     if (section == null) {
/*  951 */       return;
/*      */     }
/*  953 */     this.lHeader.setData("TreeItem", section);
/*      */     
/*  955 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  956 */     int maxUsermode = 0;
/*      */     try
/*      */     {
/*  959 */       ConfigSection sect = (ConfigSection)this.sections.get(section);
/*  960 */       if ((sect instanceof UISWTConfigSection))
/*      */       {
/*  962 */         maxUsermode = ((UISWTConfigSection)sect).maxUserMode();
/*      */       }
/*      */     }
/*      */     catch (Error e) {}
/*      */     
/*      */ 
/*      */ 
/*  969 */     String id = (String)section.getData("ID");
/*      */     
/*  971 */     String shortcut = COConfigurationManager.getStringParameter("config.section.shortcut.key." + id, null);
/*      */     
/*      */     String sc_text;
/*      */     String sc_text;
/*  975 */     if ((shortcut != null) && (shortcut.length() > 0))
/*      */     {
/*  977 */       sc_text = "      (Ctrl+" + shortcut.charAt(0) + ")";
/*      */     }
/*      */     else {
/*  980 */       sc_text = "";
/*      */     }
/*      */     
/*  983 */     if (userMode < maxUsermode) {
/*  984 */       Messages.setLanguageText(this.usermodeHint, "ConfigView.higher.mode.available");
/*      */     } else {
/*  986 */       this.usermodeHint.setText("");
/*      */     }
/*  988 */     String sHeader = section.getText();
/*      */     
/*  990 */     section = section.getParentItem();
/*  991 */     while (section != null)
/*      */     {
/*  993 */       sHeader = section.getText() + " : " + sHeader;
/*  994 */       section = section.getParentItem();
/*      */     }
/*  996 */     this.lHeader.setText(" " + sHeader.replaceAll("&", "&&") + sc_text);
/*  997 */     this.lHeader.getParent().layout(true, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Composite createConfigSection(TreeItem treeItemParent, String sNameID, int position, boolean bPrefix)
/*      */   {
/* 1005 */     ScrolledComposite sc = new ScrolledComposite(this.cConfigSection, 768);
/* 1006 */     sc.setExpandHorizontal(true);
/* 1007 */     sc.setExpandVertical(true);
/* 1008 */     Utils.setLayoutData(sc, new GridData(1808));
/* 1009 */     sc.getVerticalBar().setIncrement(16);
/* 1010 */     sc.addListener(11, this.scResizeListener);
/*      */     
/* 1012 */     Composite cConfigSection = new Composite(sc, 0);
/*      */     
/* 1014 */     String section_key = (bPrefix ? "ConfigView.section." : "") + sNameID;
/*      */     
/* 1016 */     if (position == -2) {
/* 1017 */       position = findInsertPointFor(MessageText.getString(section_key), treeItemParent == null ? this.tree : treeItemParent);
/*      */     }
/*      */     TreeItem treeItem;
/*      */     TreeItem treeItem;
/* 1021 */     if (treeItemParent == null) { TreeItem treeItem;
/* 1022 */       if (position >= 0) {
/* 1023 */         treeItem = new TreeItem(this.tree, 0, position);
/*      */       } else
/* 1025 */         treeItem = new TreeItem(this.tree, 0);
/*      */     } else { TreeItem treeItem;
/* 1027 */       if (position >= 0) {
/* 1028 */         treeItem = new TreeItem(treeItemParent, 0, position);
/*      */       } else
/* 1030 */         treeItem = new TreeItem(treeItemParent, 0);
/*      */     }
/* 1032 */     Messages.setLanguageText(treeItem, section_key);
/* 1033 */     treeItem.setData("Panel", sc);
/* 1034 */     treeItem.setData("ID", sNameID);
/*      */     
/* 1036 */     sc.setContent(cConfigSection);
/* 1037 */     return cConfigSection;
/*      */   }
/*      */   
/* 1040 */   private static java.util.Comparator<Object> insert_point_comparator = new java.util.Comparator()
/*      */   {
/*      */     private String asString(Object o) {
/* 1043 */       if ((o instanceof String)) {
/* 1044 */         return (String)o;
/*      */       }
/* 1046 */       if ((o instanceof TreeItem)) {
/* 1047 */         return ((TreeItem)o).getText();
/*      */       }
/*      */       
/* 1050 */       throw new ClassCastException("object is not String or TreeItem: " + o.getClass().getName());
/*      */     }
/*      */     
/*      */     public int compare(Object o1, Object o2)
/*      */     {
/* 1055 */       int result = String.CASE_INSENSITIVE_ORDER.compare(asString(o1), asString(o2));
/* 1056 */       return result;
/*      */     }
/*      */   };
/*      */   
/*      */   private static int findInsertPointFor(String name, Object structure) {
/* 1061 */     TreeItem[] children = null;
/* 1062 */     if ((structure instanceof Tree)) {
/* 1063 */       children = ((Tree)structure).getItems();
/*      */     }
/*      */     else {
/* 1066 */       children = ((TreeItem)structure).getItems();
/*      */     }
/* 1068 */     if (children.length == 0) return -1;
/* 1069 */     int result = java.util.Arrays.binarySearch(children, name, insert_point_comparator);
/* 1070 */     if (result > 0) return result;
/* 1071 */     result = -(result + 1);
/* 1072 */     if (result == children.length) {
/* 1073 */       result = -1;
/*      */     }
/* 1075 */     return result;
/*      */   }
/*      */   
/*      */   public TreeItem findTreeItem(String ID) {
/* 1079 */     return findTreeItem((Tree)null, ID);
/*      */   }
/*      */   
/*      */   private TreeItem findTreeItem(Tree tree, String ID) {
/* 1083 */     if (tree == null) {
/* 1084 */       tree = this.tree;
/*      */     }
/* 1086 */     if (tree == null) {
/* 1087 */       return null;
/*      */     }
/* 1089 */     TreeItem[] items = tree.getItems();
/* 1090 */     for (int i = 0; i < items.length; i++) {
/* 1091 */       String itemID = (String)items[i].getData("ID");
/* 1092 */       if ((itemID != null) && (itemID.equalsIgnoreCase(ID))) {
/* 1093 */         return items[i];
/*      */       }
/* 1095 */       TreeItem itemFound = findTreeItem(items[i], ID);
/* 1096 */       if (itemFound != null)
/* 1097 */         return itemFound;
/*      */     }
/* 1099 */     return null;
/*      */   }
/*      */   
/*      */   private TreeItem findTreeItem(TreeItem item, String ID) {
/* 1103 */     TreeItem[] subItems = item.getItems();
/* 1104 */     for (int i = 0; i < subItems.length; i++) {
/* 1105 */       String itemID = (String)subItems[i].getData("ID");
/* 1106 */       if ((itemID != null) && (itemID.equalsIgnoreCase(ID))) {
/* 1107 */         return subItems[i];
/*      */       }
/*      */       
/* 1110 */       TreeItem itemFound = findTreeItem(subItems[i], ID);
/* 1111 */       if (itemFound != null)
/* 1112 */         return itemFound;
/*      */     }
/* 1114 */     return null;
/*      */   }
/*      */   
/*      */   private void initSaveButton()
/*      */   {
/* 1119 */     final Button save = new Button(this.cConfig, 8);
/* 1120 */     Messages.setLanguageText(save, "ConfigView.button.save");
/* 1121 */     GridData gridData = new GridData(32);
/* 1122 */     gridData.horizontalSpan = 2;
/* 1123 */     gridData.widthHint = 80;
/* 1124 */     Utils.setLayoutData(save, gridData);
/*      */     
/* 1126 */     save.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent event) {
/* 1129 */         save.setFocus();
/* 1130 */         ConfigView.this.save();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void initApplyCloseButton() {
/* 1136 */     Composite cButtons = new Composite(this.cConfig, 0);
/* 1137 */     GridLayout gridLayout = new GridLayout();
/* 1138 */     gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = gridLayout.marginHeight = gridLayout.marginWidth = 0);
/* 1139 */     gridLayout.numColumns = 2;
/* 1140 */     cButtons.setLayout(gridLayout);
/* 1141 */     Utils.setLayoutData(cButtons, new GridData(128));
/*      */     
/*      */ 
/* 1144 */     final Button apply = new Button(cButtons, 8);
/* 1145 */     Messages.setLanguageText(apply, "Button.apply");
/* 1146 */     GridData gridData = new GridData(32);
/* 1147 */     gridData.widthHint = 80;
/* 1148 */     Utils.setLayoutData(apply, gridData);
/*      */     
/* 1150 */     apply.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent event) {
/* 1153 */         apply.setFocus();
/* 1154 */         ConfigView.this.save();
/*      */       }
/*      */       
/* 1157 */     });
/* 1158 */     Button close = new Button(cButtons, 8);
/* 1159 */     Messages.setLanguageText(close, "Button.close");
/* 1160 */     gridData = new GridData(32);
/* 1161 */     gridData.widthHint = 80;
/* 1162 */     Utils.setLayoutData(close, gridData);
/*      */     
/* 1164 */     close.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent event) {
/* 1167 */         apply.setFocus();
/* 1168 */         ConfigView.this.save();
/* 1169 */         apply.getShell().dispose();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private Composite getComposite() {
/* 1175 */     return this.cConfig;
/*      */   }
/*      */   
/*      */   private void updateLanguage() {
/* 1179 */     updateHeader(this.tree.getSelection()[0]);
/* 1180 */     if (this.swtView != null) {
/* 1181 */       this.swtView.setTitle(getFullTitle());
/*      */     }
/*      */   }
/*      */   
/*      */   private void delete()
/*      */   {
/* 1187 */     for (ConfigSection section : this.sectionsCreated) {
/*      */       try {
/* 1189 */         section.configSectionDelete();
/*      */       } catch (Exception e) {
/* 1191 */         org.gudy.azureus2.core3.util.Debug.out("Error while deleting config section", e);
/*      */       }
/*      */     }
/* 1194 */     this.sectionsCreated.clear();
/* 1195 */     if (this.pluginSections != null) {
/* 1196 */       this.pluginSections.clear();
/*      */     }
/* 1198 */     if ((this.tree != null) && 
/* 1199 */       (!this.tree.isDisposed())) {
/* 1200 */       TreeItem[] items = this.tree.getItems();
/* 1201 */       for (int i = 0; i < items.length; i++) {
/* 1202 */         Composite c = (Composite)items[i].getData("Panel");
/* 1203 */         Utils.disposeComposite(c);
/* 1204 */         items[i].setData("Panel", null);
/*      */         
/* 1206 */         items[i].setData("ConfigSectionSWT", null);
/*      */       }
/*      */     }
/*      */     
/* 1210 */     Utils.disposeComposite(this.cConfig);
/*      */     
/* 1212 */     Utils.disposeSWTObjects(new Object[] { this.headerFont, this.filterFoundFont });
/* 1213 */     this.headerFont = null;
/* 1214 */     this.filterFoundFont = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getFullTitle()
/*      */   {
/* 1221 */     return MessageText.getString(MessageText.resolveLocalizationKey("ConfigView.title.full"));
/*      */   }
/*      */   
/*      */   public boolean selectSection(String id, boolean focus) {
/* 1225 */     TreeItem ti = findTreeItem(id);
/* 1226 */     if (ti == null)
/* 1227 */       return false;
/* 1228 */     this.tree.setSelection(new TreeItem[] { ti });
/* 1229 */     showSection(ti, focus);
/* 1230 */     return true;
/*      */   }
/*      */   
/*      */   public void save() {
/* 1234 */     COConfigurationManager.setParameter("updated", 1);
/* 1235 */     COConfigurationManager.save();
/*      */     
/* 1237 */     if (null != this.pluginSections) {
/* 1238 */       for (int i = 0; i < this.pluginSections.size(); i++) {
/* 1239 */         ((ConfigSection)this.pluginSections.get(i)).configSectionSave();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void dataSourceChanged(Object newDataSource)
/*      */   {
/* 1246 */     if ((newDataSource instanceof String)) {
/* 1247 */       String id = (String)newDataSource;
/* 1248 */       this.startSection = id;
/* 1249 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/* 1251 */           ConfigView.this.selectSection(ConfigView.this.startSection, false);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event) {
/* 1258 */     switch (event.getType()) {
/*      */     case 0: 
/* 1260 */       this.swtView = ((UISWTView)event.getData());
/* 1261 */       this.swtView.setTitle(getFullTitle());
/* 1262 */       break;
/*      */     
/*      */     case 7: 
/* 1265 */       delete();
/* 1266 */       break;
/*      */     
/*      */     case 2: 
/* 1269 */       initialize((Composite)event.getData());
/* 1270 */       break;
/*      */     
/*      */     case 6: 
/* 1273 */       Messages.updateLanguageForControl(getComposite());
/* 1274 */       updateLanguage();
/* 1275 */       break;
/*      */     
/*      */     case 1: 
/* 1278 */       dataSourceChanged(event.getData());
/* 1279 */       break;
/*      */     case 3: 
/*      */       break;
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1288 */     return true;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/ConfigView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */