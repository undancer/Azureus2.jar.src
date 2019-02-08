/*      */ package com.aelitis.azureus.ui.swt.subscriptions;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionResultFilter;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultCategory;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultExisting;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultHash;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultName;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultRatings;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultSeedsPeers;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultSize;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectToggle;
/*      */ import com.aelitis.azureus.ui.swt.utils.SearchSubsUtils;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.ModifyEvent;
/*      */ import org.eclipse.swt.events.ModifyListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.layout.RowData;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.Spinner;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ 
/*      */ public class SBC_SubscriptionResultsView extends SkinView implements UIUpdatable, org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener, TableViewFilterCheck<SBC_SubscriptionResult>, com.aelitis.azureus.core.subs.SubscriptionListener
/*      */ {
/*      */   public static final String TABLE_SR = "SubscriptionResults";
/*   76 */   private static boolean columnsAdded = false;
/*      */   
/*      */   private TableViewSWT<SBC_SubscriptionResult> tv_subs_results;
/*      */   
/*      */   private MdiEntry mdi_entry;
/*      */   
/*      */   private Composite table_parent;
/*      */   
/*      */   private Text txtFilter;
/*      */   
/*   86 */   private final Object filter_lock = new Object();
/*      */   
/*      */   private int minSize;
/*      */   
/*      */   private int maxSize;
/*   91 */   private String[] with_keywords = new String[0];
/*   92 */   private String[] without_keywords = new String[0];
/*      */   
/*   94 */   private FrequencyLimitedDispatcher refilter_dispatcher = new FrequencyLimitedDispatcher(new org.gudy.azureus2.core3.util.AERunnable()
/*      */   {
/*      */ 
/*      */ 
/*      */     public void runSupport()
/*      */     {
/*      */ 
/*  101 */       SBC_SubscriptionResultsView.this.refilter();
/*      */     }
/*   94 */   }, 250);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Subscription ds;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  107 */   private List<SBC_SubscriptionResult> last_selected_content = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*      */   {
/*  118 */     com.aelitis.azureus.core.AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void azureusCoreRunning(AzureusCore core)
/*      */       {
/*      */ 
/*  125 */         SBC_SubscriptionResultsView.this.initColumns(core);
/*      */       }
/*      */       
/*  128 */     });
/*  129 */     String mdi_key = "Subscription_" + org.gudy.azureus2.core3.util.ByteFormatter.encodeString(this.ds.getPublicKey());
/*      */     
/*  131 */     MultipleDocumentInterfaceSWT mdi = com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*      */     
/*  133 */     if ((mdi != null) && (this.ds != null))
/*      */     {
/*  135 */       this.mdi_entry = mdi.getEntry(mdi_key);
/*      */       
/*  137 */       if (this.mdi_entry != null)
/*      */       {
/*  139 */         this.mdi_entry.addToolbarEnabler(this);
/*      */       }
/*      */     }
/*      */     
/*  143 */     if (this.ds != null)
/*      */     {
/*  145 */       SWTSkinObjectText title = (SWTSkinObjectText)getSkinObject("title");
/*      */       
/*  147 */       if (title != null)
/*      */       {
/*  149 */         title.setText(MessageText.getString("subs.results.view.title", new String[] { this.ds.getName() }));
/*      */         
/*  151 */         Control control = title.getControl();
/*      */         
/*  153 */         final Menu menu = new Menu(control);
/*      */         
/*  155 */         control.setMenu(menu);
/*      */         
/*  157 */         final String menu_key = SubscriptionMDIEntry.setupMenus(this.ds, null);
/*      */         
/*  159 */         menu.addMenuListener(new MenuListener()
/*      */         {
/*      */ 
/*      */           public void menuShown(MenuEvent e)
/*      */           {
/*  164 */             for (org.eclipse.swt.widgets.MenuItem mi : menu.getItems()) {
/*  165 */               mi.dispose();
/*      */             }
/*      */             
/*  168 */             org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray(menu_key);
/*      */             
/*  170 */             org.gudy.azureus2.ui.swt.MenuBuildUtils.addPluginMenuItems(menu_items, menu, true, true, new org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl(new Object[] { SBC_SubscriptionResultsView.this.ds }));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void menuHidden(MenuEvent e) {}
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  183 */     SWTSkinObjectTextbox soFilterBox = (SWTSkinObjectTextbox)getSkinObject("filterbox");
/*  184 */     if (soFilterBox != null) {
/*  185 */       this.txtFilter = soFilterBox.getTextControl();
/*      */     }
/*      */     
/*  188 */     final SWTSkinObject soFilterArea = getSkinObject("filterarea");
/*  189 */     if (soFilterArea != null)
/*      */     {
/*  191 */       SWTSkinObjectToggle soFilterButton = (SWTSkinObjectToggle)getSkinObject("filter-button");
/*  192 */       if (soFilterButton != null) {
/*  193 */         boolean toggled = COConfigurationManager.getBooleanParameter("Subscription View Filter Options Expanded", false);
/*      */         
/*  195 */         if (toggled)
/*      */         {
/*  197 */           soFilterButton.setToggled(true);
/*      */           
/*  199 */           soFilterArea.setVisible(true);
/*      */         }
/*      */         
/*  202 */         soFilterButton.addSelectionListener(new com.aelitis.azureus.ui.swt.skin.SWTSkinToggleListener()
/*      */         {
/*      */           public void toggleChanged(SWTSkinObjectToggle so, boolean toggled) {
/*  205 */             COConfigurationManager.setParameter("Subscription View Filter Options Expanded", toggled);
/*      */             
/*  207 */             soFilterArea.setVisible(toggled);
/*  208 */             Utils.relayout(soFilterArea.getControl().getParent());
/*      */           }
/*      */         });
/*      */       }
/*  212 */       Composite parent = (Composite)soFilterArea.getControl();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  217 */       int sepHeight = 20;
/*      */       
/*  219 */       Composite cFilters = new Composite(parent, 0);
/*  220 */       org.eclipse.swt.layout.FormData fd = Utils.getFilledFormData();
/*  221 */       cFilters.setLayoutData(fd);
/*      */       
/*  223 */       GridLayout layout = new GridLayout(1, true);
/*  224 */       layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  225 */       layout.marginWidth = (layout.marginHeight = 0);
/*      */       
/*  227 */       cFilters.setLayout(layout);
/*      */       
/*  229 */       SubscriptionResultFilter filters = null;
/*      */       
/*  231 */       Runnable pFilterUpdater = null;
/*      */       
/*  233 */       if (this.ds.isUpdateable()) {
/*      */         try
/*      */         {
/*  236 */           filters = this.ds.getFilters();
/*      */           
/*  238 */           Composite pFilters = new Composite(cFilters, 0);
/*  239 */           pFilters.setLayoutData(new GridData(768));
/*      */           
/*  241 */           layout = new GridLayout(1, false);
/*  242 */           layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  243 */           layout.marginWidth = (layout.marginHeight = 0);
/*      */           
/*  245 */           pFilters.setLayout(layout);
/*      */           
/*  247 */           final Label pflabel = new Label(pFilters, 0);
/*  248 */           pflabel.setLayoutData(new GridData(768));
/*      */           
/*  250 */           final SubscriptionResultFilter f_filters = filters;
/*      */           
/*      */ 
/*  253 */           pFilterUpdater = new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*  258 */               long kInB = DisplayFormatters.getKinB();
/*  259 */               long mInB = kInB * kInB;
/*      */               
/*  261 */               long min_size = f_filters.getMinSze() / mInB;
/*  262 */               long max_size = f_filters.getMaxSize() / mInB;
/*      */               
/*      */ 
/*  265 */               pflabel.setText(MessageText.getString("subs.persistent.filters", new String[] { SBC_SubscriptionResultsView.this.getString(f_filters.getWithWords()), SBC_SubscriptionResultsView.this.getString(f_filters.getWithoutWords()), String.valueOf(min_size < 0L ? 0L : min_size), String.valueOf(max_size < 0L ? 0L : max_size) }));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  276 */           };
/*  277 */           pFilterUpdater.run();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  281 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  285 */       Composite vFilters = new Composite(cFilters, 0);
/*  286 */       vFilters.setLayoutData(new GridData(768));
/*      */       
/*      */ 
/*  289 */       RowLayout rowLayout = new RowLayout(256);
/*  290 */       rowLayout.spacing = 5;
/*  291 */       rowLayout.marginBottom = (rowLayout.marginTop = rowLayout.marginLeft = rowLayout.marginRight = 0);
/*  292 */       rowLayout.center = true;
/*  293 */       vFilters.setLayout(rowLayout);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  298 */       ImageLoader imageLoader = ImageLoader.getInstance();
/*      */       
/*  300 */       for (int i = 0; i < 2; i++)
/*      */       {
/*  302 */         final boolean with = i == 0;
/*      */         
/*  304 */         if (!with)
/*      */         {
/*  306 */           Label label = new Label(vFilters, 514);
/*  307 */           label.setLayoutData(new RowData(-1, sepHeight));
/*      */         }
/*      */         
/*  310 */         Composite cWithKW = new Composite(vFilters, 0);
/*  311 */         layout = new GridLayout(2, false);
/*  312 */         layout.marginWidth = 0;
/*  313 */         layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  314 */         cWithKW.setLayout(layout);
/*      */         
/*      */ 
/*  317 */         Label lblWithKWImg = new Label(cWithKW, 0);
/*  318 */         lblWithKWImg.setImage(imageLoader.getImage(with ? "icon_filter_plus" : "icon_filter_minus"));
/*      */         
/*  320 */         final Text textWithKW = new Text(cWithKW, 2048);
/*  321 */         textWithKW.setMessage(MessageText.getString(with ? "SubscriptionResults.filter.with.words" : "SubscriptionResults.filter.without.words"));
/*  322 */         GridData gd = new GridData();
/*  323 */         gd.widthHint = Utils.adjustPXForDPI(100);
/*  324 */         textWithKW.setLayoutData(gd);
/*  325 */         textWithKW.addModifyListener(new ModifyListener()
/*      */         {
/*      */           public void modifyText(ModifyEvent e)
/*      */           {
/*  329 */             String text = textWithKW.getText().toLowerCase(Locale.US);
/*  330 */             String[] bits = text.split("\\s+");
/*      */             
/*  332 */             Set<String> temp = new java.util.HashSet();
/*      */             
/*  334 */             for (String bit : bits)
/*      */             {
/*  336 */               bit = bit.trim();
/*  337 */               if (bit.length() > 0) {
/*  338 */                 temp.add(bit);
/*      */               }
/*      */             }
/*      */             
/*  342 */             String[] words = (String[])temp.toArray(new String[temp.size()]);
/*  343 */             synchronized (SBC_SubscriptionResultsView.this.filter_lock) {
/*  344 */               if (with) {
/*  345 */                 SBC_SubscriptionResultsView.this.with_keywords = words;
/*      */               } else {
/*  347 */                 SBC_SubscriptionResultsView.this.without_keywords = words;
/*      */               }
/*      */             }
/*  350 */             SBC_SubscriptionResultsView.this.refilter_dispatcher.dispatch();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  358 */       Label label = new Label(vFilters, 514);
/*  359 */       label.setLayoutData(new RowData(-1, sepHeight));
/*      */       
/*  361 */       Composite cMinSize = new Composite(vFilters, 0);
/*  362 */       layout = new GridLayout(2, false);
/*  363 */       layout.marginWidth = 0;
/*  364 */       layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  365 */       cMinSize.setLayout(layout);
/*  366 */       Label lblMinSize = new Label(cMinSize, 0);
/*  367 */       lblMinSize.setText(MessageText.getString("SubscriptionResults.filter.min_size"));
/*  368 */       Spinner spinMinSize = new Spinner(cMinSize, 2048);
/*  369 */       spinMinSize.setMinimum(0);
/*  370 */       spinMinSize.setMaximum(104857600);
/*  371 */       spinMinSize.setSelection(this.minSize);
/*  372 */       spinMinSize.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  374 */           SBC_SubscriptionResultsView.this.minSize = ((Spinner)event.widget).getSelection();
/*  375 */           SBC_SubscriptionResultsView.this.refilter_dispatcher.dispatch();
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*  380 */       });
/*  381 */       label = new Label(vFilters, 514);
/*  382 */       label.setLayoutData(new RowData(-1, sepHeight));
/*      */       
/*  384 */       Composite cMaxSize = new Composite(vFilters, 0);
/*  385 */       layout = new GridLayout(2, false);
/*  386 */       layout.marginWidth = 0;
/*  387 */       layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  388 */       cMaxSize.setLayout(layout);
/*  389 */       Label lblMaxSize = new Label(cMaxSize, 0);
/*  390 */       lblMaxSize.setText(MessageText.getString("SubscriptionResults.filter.max_size"));
/*  391 */       Spinner spinMaxSize = new Spinner(cMaxSize, 2048);
/*  392 */       spinMaxSize.setMinimum(0);
/*  393 */       spinMaxSize.setMaximum(104857600);
/*  394 */       spinMaxSize.setSelection(this.maxSize);
/*  395 */       spinMaxSize.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  397 */           SBC_SubscriptionResultsView.this.maxSize = ((Spinner)event.widget).getSelection();
/*  398 */           SBC_SubscriptionResultsView.this.refilter_dispatcher.dispatch();
/*      */         }
/*      */       });
/*      */       
/*  402 */       if (filters != null)
/*      */       {
/*  404 */         label = new Label(vFilters, 514);
/*  405 */         label.setLayoutData(new RowData(-1, sepHeight));
/*      */         
/*  407 */         final SubscriptionResultFilter f_filters = filters;
/*  408 */         final Runnable f_pFilterUpdater = pFilterUpdater;
/*      */         
/*  410 */         Button save = new Button(vFilters, 8);
/*  411 */         save.setText(MessageText.getString("ConfigView.button.save"));
/*  412 */         save.addListener(13, new Listener()
/*      */         {
/*      */           public void handleEvent(Event event)
/*      */           {
/*      */             try {
/*  417 */               long kInB = DisplayFormatters.getKinB();
/*  418 */               long mInB = kInB * kInB;
/*      */               
/*  420 */               f_filters.update(SBC_SubscriptionResultsView.this.with_keywords, SBC_SubscriptionResultsView.this.without_keywords, SBC_SubscriptionResultsView.this.minSize * mInB, SBC_SubscriptionResultsView.this.maxSize * mInB);
/*      */               
/*      */ 
/*      */ 
/*  424 */               f_pFilterUpdater.run();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  428 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*  434 */       parent.layout(true);
/*      */     }
/*      */     
/*  437 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getString(String[] strs)
/*      */   {
/*  444 */     String result = "";
/*      */     
/*  446 */     if (strs != null)
/*      */     {
/*  448 */       for (String str : strs)
/*      */       {
/*  450 */         result = result + (result == "" ? "" : ", ") + str;
/*      */       }
/*      */     }
/*      */     
/*  454 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean isOurContent(SBC_SubscriptionResult result)
/*      */   {
/*  461 */     long size = result.getSize();
/*      */     
/*  463 */     long kInB = DisplayFormatters.getKinB();
/*  464 */     long mInB = kInB * kInB;
/*      */     
/*  466 */     boolean size_ok = ((size == -1L) || (size >= mInB * this.minSize)) && ((size == -1L) || (this.maxSize == 0) || (size <= mInB * this.maxSize));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  471 */     if (!size_ok)
/*      */     {
/*  473 */       return false;
/*      */     }
/*      */     
/*  476 */     if ((this.with_keywords.length > 0) || (this.without_keywords.length > 0))
/*      */     {
/*  478 */       synchronized (this.filter_lock)
/*      */       {
/*  480 */         String name = result.getName().toLowerCase(Locale.US);
/*      */         
/*  482 */         for (int i = 0; i < this.with_keywords.length; i++)
/*      */         {
/*  484 */           if (!name.contains(this.with_keywords[i]))
/*      */           {
/*  486 */             return false;
/*      */           }
/*      */         }
/*      */         
/*  490 */         for (int i = 0; i < this.without_keywords.length; i++)
/*      */         {
/*  492 */           if (name.contains(this.without_keywords[i]))
/*      */           {
/*  494 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  500 */     return true;
/*      */   }
/*      */   
/*      */   protected void refilter()
/*      */   {
/*  505 */     if (this.tv_subs_results != null) {
/*  506 */       this.tv_subs_results.refilter();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initColumns(AzureusCore core)
/*      */   {
/*  515 */     synchronized (SBC_SubscriptionResultsView.class)
/*      */     {
/*  517 */       if (columnsAdded)
/*      */       {
/*  519 */         return;
/*      */       }
/*      */       
/*  522 */       columnsAdded = true;
/*      */     }
/*      */     
/*  525 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*      */     
/*  527 */     tableManager.registerColumn(SBC_SubscriptionResult.class, "new", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  533 */         new com.aelitis.azureus.ui.swt.columns.subscriptions.ColumnSubResultNew(column);
/*      */       }
/*      */       
/*  536 */     });
/*  537 */     tableManager.registerColumn(SBC_SubscriptionResult.class, "type", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  543 */         new com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultType(column);
/*      */       }
/*      */       
/*  546 */     });
/*  547 */     tableManager.registerColumn(SBC_SubscriptionResult.class, ColumnSearchSubResultName.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  553 */         new ColumnSearchSubResultName(column);
/*      */       }
/*      */       
/*  556 */     });
/*  557 */     tableManager.registerColumn(SBC_SubscriptionResult.class, "actions", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  563 */         new com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultActions(column);
/*      */       }
/*      */       
/*  566 */     });
/*  567 */     tableManager.registerColumn(SBC_SubscriptionResult.class, "size", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  573 */         new ColumnSearchSubResultSize(column);
/*      */       }
/*      */       
/*  576 */     });
/*  577 */     tableManager.registerColumn(SBC_SubscriptionResult.class, ColumnSearchSubResultSeedsPeers.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  583 */         new ColumnSearchSubResultSeedsPeers(column);
/*      */       }
/*      */       
/*  586 */     });
/*  587 */     tableManager.registerColumn(SBC_SubscriptionResult.class, ColumnSearchSubResultRatings.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  593 */         new ColumnSearchSubResultRatings(column);
/*      */       }
/*      */       
/*  596 */     });
/*  597 */     tableManager.registerColumn(SBC_SubscriptionResult.class, "age", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  603 */         new com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultAge(column);
/*      */       }
/*      */       
/*  606 */     });
/*  607 */     tableManager.registerColumn(SBC_SubscriptionResult.class, "rank", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  613 */         new com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultRank(column);
/*      */       }
/*      */       
/*  616 */     });
/*  617 */     tableManager.registerColumn(SBC_SubscriptionResult.class, ColumnSearchSubResultCategory.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  623 */         new ColumnSearchSubResultCategory(column);
/*      */       }
/*      */       
/*  626 */     });
/*  627 */     tableManager.registerColumn(SBC_SubscriptionResult.class, ColumnSearchSubResultHash.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  633 */         new ColumnSearchSubResultHash(column);
/*      */       }
/*      */       
/*  636 */     });
/*  637 */     tableManager.registerColumn(SBC_SubscriptionResult.class, ColumnSearchSubResultExisting.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  643 */         new ColumnSearchSubResultExisting(column);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params)
/*      */   {
/*  652 */     synchronized (this)
/*      */     {
/*  654 */       Subscription new_ds = null;
/*      */       
/*  656 */       if ((params instanceof Subscription))
/*      */       {
/*  658 */         new_ds = (Subscription)params;
/*      */       }
/*  660 */       else if ((params instanceof Object[]))
/*      */       {
/*  662 */         Object[] objs = (Object[])params;
/*      */         
/*  664 */         if ((objs.length == 1) && ((objs[0] instanceof Subscription)))
/*      */         {
/*  666 */           new_ds = (Subscription)objs[0];
/*      */         }
/*      */       }
/*      */       
/*  670 */       if (this.ds != null)
/*      */       {
/*  672 */         this.ds.removeListener(this);
/*      */       }
/*      */       
/*  675 */       this.ds = new_ds;
/*      */       
/*  677 */       if (new_ds != null)
/*      */       {
/*  679 */         this.ds.addListener(this);
/*      */       }
/*      */     }
/*      */     
/*  683 */     return super.dataSourceChanged(skinObject, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void subscriptionChanged(Subscription subs, int reason)
/*      */   {
/*  691 */     if (reason == 2)
/*      */     {
/*  693 */       reconcileResults(subs);
/*      */       
/*  695 */       this.tv_subs_results.runForAllRows(new com.aelitis.azureus.ui.common.table.TableGroupRowRunner()
/*      */       {
/*      */         public void run(TableRowCore row)
/*      */         {
/*  699 */           row.invalidate(true);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void reconcileResults(Subscription subs)
/*      */   {
/*  709 */     synchronized (this)
/*      */     {
/*  711 */       if ((subs != this.ds) || (this.ds == null) || (subs == null) || (this.tv_subs_results == null))
/*      */       {
/*  713 */         return;
/*      */       }
/*      */       
/*  716 */       this.tv_subs_results.processDataSourceQueueSync();
/*      */       
/*  718 */       List<SBC_SubscriptionResult> existing_results = this.tv_subs_results.getDataSources(true);
/*      */       
/*  720 */       Map<String, SBC_SubscriptionResult> existing_map = new java.util.HashMap();
/*      */       
/*  722 */       for (SBC_SubscriptionResult result : existing_results)
/*      */       {
/*  724 */         existing_map.put(result.getID(), result);
/*      */       }
/*      */       
/*  727 */       SubscriptionResult[] current_results = this.ds.getResults(false);
/*      */       
/*  729 */       List<SBC_SubscriptionResult> new_results = new ArrayList(current_results.length);
/*      */       
/*  731 */       for (SubscriptionResult result : current_results)
/*      */       {
/*  733 */         SBC_SubscriptionResult existing = (SBC_SubscriptionResult)existing_map.remove(result.getID());
/*      */         
/*  735 */         if (existing == null)
/*      */         {
/*  737 */           new_results.add(new SBC_SubscriptionResult(this.ds, result));
/*      */         }
/*      */         else
/*      */         {
/*  741 */           existing.updateFrom(result);
/*      */         }
/*      */       }
/*      */       
/*  745 */       if (new_results.size() > 0)
/*      */       {
/*  747 */         this.tv_subs_results.addDataSources(new_results.toArray(new SBC_SubscriptionResult[new_results.size()]));
/*      */       }
/*      */       
/*  750 */       if (existing_map.size() > 0)
/*      */       {
/*  752 */         Collection<SBC_SubscriptionResult> to_remove = existing_map.values();
/*      */         
/*  754 */         this.tv_subs_results.removeDataSources(to_remove.toArray(new SBC_SubscriptionResult[to_remove.size()]));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void subscriptionDownloaded(Subscription subs, boolean auto) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void showView()
/*      */   {
/*  770 */     SWTSkinObject so_list = getSkinObject("subs-results-list");
/*      */     
/*  772 */     if (so_list != null)
/*      */     {
/*  774 */       so_list.setVisible(true);
/*      */       
/*  776 */       initTable((Composite)so_list.getControl());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void hideView()
/*      */   {
/*  783 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*      */   {
/*  793 */     super.skinObjectShown(skinObject, params);
/*      */     
/*  795 */     showView();
/*      */     
/*  797 */     synchronized (this)
/*      */     {
/*  799 */       if (this.ds != null)
/*      */       {
/*  801 */         this.ds.addListener(this);
/*      */       }
/*      */     }
/*      */     
/*  805 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*      */   {
/*  813 */     hideView();
/*      */     
/*  815 */     synchronized (this)
/*      */     {
/*  817 */       if (this.ds != null)
/*      */       {
/*  819 */         this.ds.removeListener(this);
/*      */       }
/*      */     }
/*      */     
/*  823 */     return super.skinObjectHidden(skinObject, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*      */   {
/*  831 */     synchronized (this)
/*      */     {
/*  833 */       if (this.tv_subs_results != null)
/*      */       {
/*  835 */         this.tv_subs_results.delete();
/*      */         
/*  837 */         this.tv_subs_results = null;
/*      */       }
/*      */       
/*  840 */       if (this.ds != null)
/*      */       {
/*  842 */         this.ds.removeListener(this);
/*      */       }
/*      */     }
/*      */     
/*  846 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*      */     
/*      */ 
/*      */ 
/*  850 */     return super.skinObjectDestroyed(skinObject, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void initTable(Composite control)
/*      */   {
/*  857 */     this.tv_subs_results = org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory.createTableViewSWT(SBC_SubscriptionResult.class, "SubscriptionResults", "SubscriptionResults", new TableColumnCore[0], "age", 268500994);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  865 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*      */     
/*  867 */     tableManager.setDefaultColumnNames("SubscriptionResults", new String[] { "new", "type", ColumnSearchSubResultName.COLUMN_ID, "actions", "size", ColumnSearchSubResultSeedsPeers.COLUMN_ID, ColumnSearchSubResultRatings.COLUMN_ID, "age", "rank", ColumnSearchSubResultCategory.COLUMN_ID });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  881 */     tableManager.setDefaultSortColumnName("SubscriptionResults", "age");
/*      */     
/*  883 */     TableColumnCore tcc = tableManager.getTableColumnCore("SubscriptionResults", "age");
/*      */     
/*  885 */     if (tcc != null)
/*      */     {
/*  887 */       tcc.setDefaultSortAscending(true);
/*      */     }
/*      */     
/*  890 */     if (this.txtFilter != null) {
/*  891 */       this.tv_subs_results.enableFilterCheck(this.txtFilter, this);
/*      */     }
/*      */     
/*  894 */     this.tv_subs_results.setRowDefaultHeight(COConfigurationManager.getIntParameter("Search Subs Row Height"));
/*      */     
/*  896 */     SWTSkinObject soSizeSlider = getSkinObject("table-size-slider");
/*  897 */     if ((soSizeSlider instanceof SWTSkinObjectContainer)) {
/*  898 */       SWTSkinObjectContainer so = (SWTSkinObjectContainer)soSizeSlider;
/*  899 */       if (!this.tv_subs_results.enableSizeSlider(so.getComposite(), 16, 100)) {
/*  900 */         so.setVisible(false);
/*      */       }
/*      */     }
/*      */     
/*  904 */     this.table_parent = new Composite(control, 0);
/*  905 */     this.table_parent.setLayoutData(Utils.getFilledFormData());
/*  906 */     GridLayout layout = new GridLayout();
/*  907 */     layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/*  908 */     this.table_parent.setLayout(layout);
/*      */     
/*  910 */     this.tv_subs_results.addSelectionListener(new com.aelitis.azureus.ui.common.table.TableSelectionListener()
/*      */     {
/*      */ 
/*      */       public void selected(TableRowCore[] _rows)
/*      */       {
/*      */ 
/*  916 */         updateSelectedContent();
/*      */       }
/*      */       
/*      */ 
/*      */       public void mouseExit(TableRowCore row) {}
/*      */       
/*      */       public void mouseEnter(TableRowCore row) {}
/*      */       
/*      */       public void focusChanged(TableRowCore focus)
/*      */       {
/*  926 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  927 */         if (uiFunctions != null) {
/*  928 */           uiFunctions.refreshIconBar();
/*      */         }
/*      */       }
/*      */       
/*      */       public void deselected(TableRowCore[] rows) {
/*  933 */         updateSelectedContent();
/*      */       }
/*      */       
/*      */       public void defaultSelected(TableRowCore[] rows, int stateMask) {
/*  937 */         if (rows.length == 1)
/*      */         {
/*  939 */           SBC_SubscriptionResult rc = (SBC_SubscriptionResult)rows[0].getDataSource();
/*      */           
/*  941 */           com.aelitis.azureus.ui.swt.search.SBC_SearchResultsView.downloadAction(rc);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       private void updateSelectedContent()
/*      */       {
/*  948 */         TableRowCore[] rows = SBC_SubscriptionResultsView.this.tv_subs_results.getSelectedRows();
/*      */         
/*  950 */         ArrayList<ISelectedContent> valid = new ArrayList();
/*      */         
/*  952 */         SBC_SubscriptionResultsView.this.last_selected_content.clear();
/*      */         
/*  954 */         for (int i = 0; i < rows.length; i++)
/*      */         {
/*  956 */           SBC_SubscriptionResult rc = (SBC_SubscriptionResult)rows[i].getDataSource();
/*      */           
/*  958 */           SBC_SubscriptionResultsView.this.last_selected_content.add(rc);
/*      */           
/*  960 */           byte[] hash = rc.getHash();
/*      */           
/*  962 */           if ((hash != null) && (hash.length > 0))
/*      */           {
/*  964 */             SelectedContent sc = new SelectedContent(org.gudy.azureus2.core3.util.Base32.encode(hash), rc.getName());
/*      */             
/*  966 */             sc.setDownloadInfo(new com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo(SBC_SubscriptionResultsView.this.getDownloadURI(rc)));
/*      */             
/*  968 */             valid.add(sc);
/*      */           }
/*      */         }
/*      */         
/*  972 */         ISelectedContent[] sels = (ISelectedContent[])valid.toArray(new ISelectedContent[valid.size()]);
/*      */         
/*  974 */         SelectedContentManager.changeCurrentlySelectedContent("IconBarEnabler", sels, SBC_SubscriptionResultsView.this.tv_subs_results);
/*      */         
/*      */ 
/*  977 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*      */         
/*  979 */         if (uiFunctions != null)
/*      */         {
/*  981 */           uiFunctions.refreshIconBar(); } } }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  986 */     this.tv_subs_results.addLifeCycleListener(new com.aelitis.azureus.ui.common.table.TableLifeCycleListener()
/*      */     {
/*      */ 
/*      */       public void tableViewInitialized()
/*      */       {
/*      */ 
/*  992 */         SBC_SubscriptionResultsView.this.reconcileResults(SBC_SubscriptionResultsView.this.ds);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void tableViewDestroyed() {}
/* 1001 */     });
/* 1002 */     this.tv_subs_results.addMenuFillListener(new org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener()
/*      */     {
/*      */ 
/*      */       public void fillMenu(String sColumnName, Menu menu)
/*      */       {
/*      */ 
/* 1008 */         Object[] _related_content = SBC_SubscriptionResultsView.this.tv_subs_results.getSelectedDataSources().toArray();
/*      */         
/* 1010 */         final SBC_SubscriptionResult[] results = new SBC_SubscriptionResult[_related_content.length];
/*      */         
/* 1012 */         System.arraycopy(_related_content, 0, results, 0, results.length);
/*      */         
/* 1014 */         org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1015 */         item.setText(MessageText.getString("label.copy.url.to.clip"));
/* 1016 */         item.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1019 */             StringBuffer buffer = new StringBuffer(1024);
/*      */             
/* 1021 */             for (SBC_SubscriptionResult result : results)
/*      */             {
/* 1023 */               if (buffer.length() > 0) {
/* 1024 */                 buffer.append("\r\n");
/*      */               }
/*      */               
/* 1027 */               buffer.append(SBC_SubscriptionResultsView.this.getDownloadURI(result));
/*      */             }
/* 1029 */             org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipBoard(buffer.toString());
/*      */           }
/*      */           
/* 1032 */         });
/* 1033 */         item.setEnabled(results.length > 0);
/*      */         
/* 1035 */         SearchSubsUtils.addMenu(results, menu);
/*      */         
/* 1037 */         new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */         
/* 1039 */         if (results.length == 1)
/*      */         {
/* 1041 */           if (SearchSubsUtils.addMenu(results[0], menu))
/*      */           {
/* 1043 */             new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */           }
/*      */         }
/*      */         
/* 1047 */         org.eclipse.swt.widgets.MenuItem remove_item = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1049 */         remove_item.setText(MessageText.getString("azbuddy.ui.menu.remove"));
/*      */         
/* 1051 */         Utils.setMenuItemImage(remove_item, "delete");
/*      */         
/* 1053 */         remove_item.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1055 */             SBC_SubscriptionResultsView.this.userDelete(results);
/*      */           }
/*      */           
/*      */ 
/* 1059 */         });
/* 1060 */         remove_item.setEnabled(results.length > 0);
/*      */         
/* 1062 */         new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/* 1071 */     });
/* 1072 */     this.tv_subs_results.addKeyListener(new org.eclipse.swt.events.KeyListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void keyPressed(KeyEvent e)
/*      */       {
/*      */ 
/* 1079 */         if ((e.stateMask == 0) && (e.keyCode == 127))
/*      */         {
/*      */           Object[] selected;
/*      */           
/* 1083 */           synchronized (this) {
/*      */             Object[] selected;
/* 1085 */             if (SBC_SubscriptionResultsView.this.tv_subs_results == null)
/*      */             {
/* 1087 */               selected = new Object[0];
/*      */             }
/*      */             else
/*      */             {
/* 1091 */               selected = SBC_SubscriptionResultsView.this.tv_subs_results.getSelectedDataSources().toArray();
/*      */             }
/*      */           }
/*      */           
/* 1095 */           SBC_SubscriptionResult[] content = new SBC_SubscriptionResult[selected.length];
/*      */           
/* 1097 */           for (int i = 0; i < content.length; i++)
/*      */           {
/* 1099 */             content[i] = ((SBC_SubscriptionResult)selected[i]);
/*      */           }
/*      */           
/* 1102 */           SBC_SubscriptionResultsView.this.userDelete(content);
/*      */           
/* 1104 */           e.doit = false;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void keyReleased(KeyEvent arg0) {}
/* 1135 */     });
/* 1136 */     this.tv_subs_results.initialize(this.table_parent);
/*      */     
/* 1138 */     control.layout(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void userDelete(SBC_SubscriptionResult[] results)
/*      */   {
/* 1145 */     TableRowCore focusedRow = this.tv_subs_results.getFocusedRow();
/*      */     
/* 1147 */     TableRowCore focusRow = null;
/*      */     
/* 1149 */     if (focusedRow != null) {
/* 1150 */       int i = this.tv_subs_results.indexOf(focusedRow);
/* 1151 */       int size = this.tv_subs_results.size(false);
/* 1152 */       if (i < size - 1) {
/* 1153 */         focusRow = this.tv_subs_results.getRow(i + 1);
/* 1154 */       } else if (i > 0) {
/* 1155 */         focusRow = this.tv_subs_results.getRow(i - 1);
/*      */       }
/*      */     }
/*      */     
/* 1159 */     for (SBC_SubscriptionResult result : results)
/*      */     {
/* 1161 */       result.delete();
/*      */     }
/*      */     
/* 1164 */     if (focusRow != null)
/*      */     {
/* 1166 */       this.tv_subs_results.setSelectedRows(new TableRowCore[] { focusRow });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUpdateUIName()
/*      */   {
/* 1173 */     return "SubscriptionResultsView";
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateUI()
/*      */   {
/* 1179 */     if (this.tv_subs_results != null)
/*      */     {
/* 1181 */       this.tv_subs_results.refreshTable(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean filterCheck(SBC_SubscriptionResult ds, String filter, boolean regex)
/*      */   {
/* 1191 */     if (!isOurContent(ds))
/*      */     {
/* 1193 */       return false;
/*      */     }
/*      */     
/* 1196 */     return SearchSubsUtils.filterCheck(ds, filter, regex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void filterSet(String filter) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*      */   {
/* 1208 */     if ((this.tv_subs_results == null) || (!this.tv_subs_results.isVisible()))
/*      */     {
/* 1210 */       return false;
/*      */     }
/*      */     
/* 1213 */     if (item.getID().equals("remove"))
/*      */     {
/* 1215 */       Object[] _related_content = this.tv_subs_results.getSelectedDataSources().toArray();
/*      */       
/* 1217 */       if (_related_content.length > 0)
/*      */       {
/* 1219 */         SBC_SubscriptionResult[] related_content = new SBC_SubscriptionResult[_related_content.length];
/*      */         
/* 1221 */         System.arraycopy(_related_content, 0, related_content, 0, related_content.length);
/*      */         
/* 1223 */         userDelete(related_content);
/*      */         
/* 1225 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1229 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void refreshToolBarItems(Map<String, Long> list)
/*      */   {
/* 1236 */     if ((this.tv_subs_results == null) || (!this.tv_subs_results.isVisible()))
/*      */     {
/* 1238 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1243 */     ISelectedContent[] content = SelectedContentManager.getCurrentlySelectedContent();
/*      */     
/* 1245 */     for (ISelectedContent c : content)
/*      */     {
/* 1247 */       if (c.getDownloadManager() != null)
/*      */       {
/* 1249 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1253 */     list.put("remove", Long.valueOf(this.tv_subs_results.getSelectedDataSources().size() > 0 ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getDownloadURI(SBC_SubscriptionResult result)
/*      */   {
/* 1260 */     String torrent_url = result.getTorrentLink();
/*      */     
/* 1262 */     if ((torrent_url != null) && (torrent_url.length() > 0))
/*      */     {
/* 1264 */       return torrent_url;
/*      */     }
/*      */     
/* 1267 */     String uri = org.gudy.azureus2.core3.util.UrlUtils.getMagnetURI(result.getHash(), result.getName(), this.ds.getHistory().getDownloadNetworks());
/*      */     
/* 1269 */     return uri;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SBC_SubscriptionResultsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */