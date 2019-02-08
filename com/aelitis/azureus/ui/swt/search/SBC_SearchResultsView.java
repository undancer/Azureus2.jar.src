/*      */ package com.aelitis.azureus.ui.swt.search;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*      */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchListener;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*      */ import com.aelitis.azureus.core.metasearch.Result;
/*      */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteSet;
/*      */ import com.aelitis.azureus.core.util.GeneralUtils;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*      */ import com.aelitis.azureus.ui.common.RememberedDecisionsManager;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*      */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.swt.columns.search.ColumnSearchResultSite;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultActions;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultAge;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultCategory;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultExisting;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultHash;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultName;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultRank;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultRatings;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultSeedsPeers;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultSize;
/*      */ import com.aelitis.azureus.ui.swt.columns.searchsubs.ColumnSearchSubResultType;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader.ImageDownloaderListener;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinCheckboxListener;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectCheckbox;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectToggle;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinToggleListener;
/*      */ import com.aelitis.azureus.ui.swt.subscriptions.SBC_SubscriptionResult;
/*      */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
/*      */ import com.aelitis.azureus.ui.swt.utils.SearchSubsUtils;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBox;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBoxListener;
/*      */ import com.aelitis.azureus.util.ConstantsVuze;
/*      */ import com.aelitis.azureus.util.UrlFilter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import org.eclipse.swt.events.ModifyEvent;
/*      */ import org.eclipse.swt.events.ModifyListener;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.layout.RowData;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Spinner;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*      */ 
/*      */ public class SBC_SearchResultsView implements SearchResultsTabAreaBase, TableViewFilterCheck<SBC_SearchResult>, MetaSearchListener
/*      */ {
/*      */   public static final String TABLE_SR = "SearchResults";
/*  129 */   private static boolean columnsAdded = false;
/*      */   
/*      */   private static Image[] vitality_images;
/*      */   
/*      */   private static Image ok_image;
/*      */   
/*      */   private static Image fail_image;
/*      */   
/*      */   private static Image auth_image;
/*      */   
/*      */   private SearchResultsTabArea parent;
/*      */   
/*      */   private TableViewSWT<SBC_SearchResult> tv_subs_results;
/*      */   
/*      */   private Composite table_parent;
/*      */   private Text txtFilter;
/*  145 */   private final Object filter_lock = new Object();
/*      */   
/*      */   private Spinner spinMinSize;
/*      */   
/*      */   private Spinner spinMaxSize;
/*      */   
/*      */   private Text textWithKW;
/*      */   private Text textWithoutKW;
/*      */   private int minSize;
/*      */   private int maxSize;
/*  155 */   private String[] with_keywords = new String[0];
/*  156 */   private String[] without_keywords = new String[0];
/*      */   
/*  158 */   private FrequencyLimitedDispatcher refilter_dispatcher = new FrequencyLimitedDispatcher(new AERunnable()
/*      */   {
/*      */ 
/*      */ 
/*      */     public void runSupport()
/*      */     {
/*      */ 
/*  165 */       SBC_SearchResultsView.this.refilter();
/*      */     }
/*  158 */   }, 250);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  169 */   private final CopyOnWriteSet<String> deselected_engines = new CopyOnWriteSet(false);
/*      */   
/*      */   private Composite engine_area;
/*      */   
/*  173 */   private List<SBC_SearchResult> last_selected_content = new ArrayList();
/*      */   
/*  175 */   private Object search_lock = new Object();
/*      */   
/*      */   private SearchInstance current_search;
/*      */   
/*      */ 
/*      */   protected SBC_SearchResultsView(SearchResultsTabArea _parent)
/*      */   {
/*  182 */     this.parent = _parent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private SWTSkinObject getSkinObject(String viewID)
/*      */   {
/*  189 */     return this.parent.getSkinObject(viewID);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*      */   {
/*  196 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void azureusCoreRunning(AzureusCore core)
/*      */       {
/*      */ 
/*  203 */         SBC_SearchResultsView.this.initColumns(core);
/*      */       }
/*      */       
/*  206 */     });
/*  207 */     SWTSkinObjectTextbox soFilterBox = (SWTSkinObjectTextbox)getSkinObject("filterbox");
/*  208 */     if (soFilterBox != null) {
/*  209 */       this.txtFilter = soFilterBox.getTextControl();
/*      */     }
/*      */     
/*  212 */     if (vitality_images == null)
/*      */     {
/*  214 */       ImageLoader loader = ImageLoader.getInstance();
/*      */       
/*  216 */       vitality_images = loader.getImages("image.sidebar.vitality.dots");
/*      */       
/*  218 */       ok_image = loader.getImage("tick_mark");
/*  219 */       fail_image = loader.getImage("progress_cancel");
/*  220 */       auth_image = loader.getImage("image.sidebar.vitality.auth");
/*      */     }
/*      */     
/*  223 */     final SWTSkinObject soFilterArea = getSkinObject("filterarea");
/*  224 */     if (soFilterArea != null)
/*      */     {
/*  226 */       SWTSkinObjectToggle soFilterButton = (SWTSkinObjectToggle)getSkinObject("filter-button");
/*  227 */       if (soFilterButton != null)
/*      */       {
/*  229 */         boolean toggled = COConfigurationManager.getBooleanParameter("Search View Filter Options Expanded", false);
/*      */         
/*  231 */         if (toggled)
/*      */         {
/*  233 */           soFilterButton.setToggled(true);
/*      */           
/*  235 */           soFilterArea.setVisible(true);
/*      */         }
/*      */         
/*  238 */         soFilterButton.addSelectionListener(new SWTSkinToggleListener()
/*      */         {
/*      */           public void toggleChanged(SWTSkinObjectToggle so, boolean toggled) {
/*  241 */             COConfigurationManager.setParameter("Search View Filter Options Expanded", toggled);
/*      */             
/*  243 */             soFilterArea.setVisible(toggled);
/*  244 */             Utils.relayout(soFilterArea.getControl().getParent());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*  249 */       Composite parent = (Composite)soFilterArea.getControl();
/*      */       
/*  251 */       Composite filter_area = new Composite(parent, 0);
/*  252 */       FormData fd = Utils.getFilledFormData();
/*  253 */       filter_area.setLayoutData(fd);
/*      */       
/*  255 */       GridLayout layout = new GridLayout();
/*  256 */       layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  257 */       filter_area.setLayout(layout);
/*      */       
/*      */ 
/*  260 */       int sepHeight = 20;
/*      */       
/*  262 */       Composite cRow = new Composite(filter_area, 0);
/*  263 */       cRow.setLayoutData(new GridData(768));
/*      */       
/*  265 */       RowLayout rowLayout = new RowLayout(256);
/*  266 */       rowLayout.spacing = 5;
/*  267 */       rowLayout.marginBottom = (rowLayout.marginTop = rowLayout.marginLeft = rowLayout.marginRight = 0);
/*  268 */       rowLayout.center = true;
/*  269 */       cRow.setLayout(rowLayout);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  274 */       ImageLoader imageLoader = ImageLoader.getInstance();
/*      */       
/*  276 */       for (int i = 0; i < 2; i++)
/*      */       {
/*  278 */         final boolean with = i == 0;
/*      */         
/*  280 */         if (!with)
/*      */         {
/*  282 */           Label label = new Label(cRow, 514);
/*  283 */           label.setLayoutData(new RowData(-1, sepHeight));
/*      */         }
/*      */         
/*  286 */         Composite cWithKW = new Composite(cRow, 0);
/*  287 */         layout = new GridLayout(2, false);
/*  288 */         layout.marginWidth = 0;
/*  289 */         layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  290 */         cWithKW.setLayout(layout);
/*      */         
/*      */ 
/*  293 */         Label lblWithKWImg = new Label(cWithKW, 0);
/*  294 */         lblWithKWImg.setImage(imageLoader.getImage(with ? "icon_filter_plus" : "icon_filter_minus"));
/*      */         
/*  296 */         final Text textWidget = new Text(cWithKW, 2048);
/*  297 */         if (with) {
/*  298 */           this.textWithKW = textWidget;
/*      */         } else {
/*  300 */           this.textWithoutKW = textWidget;
/*      */         }
/*  302 */         textWidget.setMessage(MessageText.getString(with ? "SubscriptionResults.filter.with.words" : "SubscriptionResults.filter.without.words"));
/*  303 */         GridData gd = new GridData();
/*  304 */         gd.widthHint = Utils.adjustPXForDPI(100);
/*  305 */         textWidget.setLayoutData(gd);
/*  306 */         textWidget.addModifyListener(new ModifyListener()
/*      */         {
/*      */           public void modifyText(ModifyEvent e)
/*      */           {
/*  310 */             String text = textWidget.getText().toLowerCase(Locale.US);
/*  311 */             String[] bits = text.split("\\s+");
/*      */             
/*  313 */             Set<String> temp = new HashSet();
/*      */             
/*  315 */             for (String bit : bits)
/*      */             {
/*  317 */               bit = bit.trim();
/*  318 */               if (bit.length() > 0) {
/*  319 */                 temp.add(bit);
/*      */               }
/*      */             }
/*      */             
/*  323 */             String[] words = (String[])temp.toArray(new String[temp.size()]);
/*  324 */             synchronized (SBC_SearchResultsView.this.filter_lock) {
/*  325 */               if (with) {
/*  326 */                 SBC_SearchResultsView.this.with_keywords = words;
/*      */               } else {
/*  328 */                 SBC_SearchResultsView.this.without_keywords = words;
/*      */               }
/*      */             }
/*  331 */             SBC_SearchResultsView.this.refilter_dispatcher.dispatch();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  339 */       Label label = new Label(cRow, 514);
/*  340 */       label.setLayoutData(new RowData(-1, sepHeight));
/*      */       
/*  342 */       Composite cMinSize = new Composite(cRow, 0);
/*  343 */       layout = new GridLayout(2, false);
/*  344 */       layout.marginWidth = 0;
/*  345 */       layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  346 */       cMinSize.setLayout(layout);
/*  347 */       Label lblMinSize = new Label(cMinSize, 0);
/*  348 */       lblMinSize.setText(MessageText.getString("SubscriptionResults.filter.min_size"));
/*  349 */       this.spinMinSize = new Spinner(cMinSize, 2048);
/*  350 */       this.spinMinSize.setMinimum(0);
/*  351 */       this.spinMinSize.setMaximum(104857600);
/*  352 */       this.spinMinSize.setSelection(this.minSize);
/*  353 */       this.spinMinSize.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  355 */           SBC_SearchResultsView.this.minSize = ((Spinner)event.widget).getSelection();
/*  356 */           SBC_SearchResultsView.this.refilter();
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*  361 */       });
/*  362 */       label = new Label(cRow, 514);
/*  363 */       label.setLayoutData(new RowData(-1, sepHeight));
/*      */       
/*  365 */       Composite cMaxSize = new Composite(cRow, 0);
/*  366 */       layout = new GridLayout(2, false);
/*  367 */       layout.marginWidth = 0;
/*  368 */       layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 0);
/*  369 */       cMaxSize.setLayout(layout);
/*  370 */       Label lblMaxSize = new Label(cMaxSize, 0);
/*  371 */       lblMaxSize.setText(MessageText.getString("SubscriptionResults.filter.max_size"));
/*  372 */       this.spinMaxSize = new Spinner(cMaxSize, 2048);
/*  373 */       this.spinMaxSize.setMinimum(0);
/*  374 */       this.spinMaxSize.setMaximum(104857600);
/*  375 */       this.spinMaxSize.setSelection(this.maxSize);
/*  376 */       this.spinMaxSize.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  378 */           SBC_SearchResultsView.this.maxSize = ((Spinner)event.widget).getSelection();
/*  379 */           SBC_SearchResultsView.this.refilter();
/*      */         }
/*      */         
/*  382 */       });
/*  383 */       this.engine_area = new Composite(filter_area, 0);
/*  384 */       this.engine_area.setLayoutData(new GridData(768));
/*      */       
/*  386 */       buildEngineArea(null);
/*      */       
/*  388 */       parent.layout(true);
/*      */     }
/*      */     
/*  391 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void buildEngineArea(final SearchInstance search)
/*      */   {
/*  398 */     if (this.engine_area.isDisposed())
/*      */     {
/*  400 */       return;
/*      */     }
/*      */     
/*  403 */     final Engine[] engines = search == null ? new Engine[0] : search.getEngines();
/*      */     
/*  405 */     Utils.disposeComposite(this.engine_area, false);
/*      */     
/*  407 */     Arrays.sort(engines, new Comparator()
/*      */     {
/*      */ 
/*      */       public int compare(Engine o1, Engine o2)
/*      */       {
/*  412 */         return o1.getName().compareTo(o2.getName());
/*      */       }
/*      */       
/*  415 */     });
/*  416 */     RowLayout rowLayout = new RowLayout(256);
/*  417 */     rowLayout.spacing = 3;
/*  418 */     rowLayout.marginBottom = (rowLayout.marginTop = rowLayout.marginLeft = rowLayout.marginRight = 0);
/*  419 */     rowLayout.pack = false;
/*  420 */     this.engine_area.setLayout(rowLayout);
/*      */     
/*  422 */     final Composite label_comp = new Composite(this.engine_area, 0);
/*      */     
/*  424 */     GridLayout layout = new GridLayout();
/*  425 */     layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 1);
/*  426 */     label_comp.setLayout(layout);
/*      */     
/*  428 */     Label label = new Label(label_comp, 0);
/*  429 */     Messages.setLanguageText(label, "label.show.results.from");
/*  430 */     GridData grid_data = new GridData(16384, 16777216, true, true);
/*      */     
/*  432 */     label.setLayoutData(grid_data);
/*      */     
/*  434 */     final List<Button> buttons = new ArrayList();
/*  435 */     final List<Label> result_counts = new ArrayList();
/*  436 */     final List<ImageLabel> indicators = new ArrayList();
/*      */     
/*  438 */     label.addMouseListener(new MouseAdapter()
/*      */     {
/*      */ 
/*      */       public void mouseDown(MouseEvent e)
/*      */       {
/*  443 */         SBC_SearchResultsView.this.deselected_engines.clear();
/*      */         
/*  445 */         for (Button b : buttons)
/*      */         {
/*  447 */           b.setSelection(true);
/*      */         }
/*      */         
/*  450 */         SBC_SearchResultsView.this.refilter();
/*      */       }
/*      */     });
/*      */     
/*  454 */     for (final Engine engine : engines)
/*      */     {
/*  456 */       final Composite engine_comp = new Composite(this.engine_area, 0);
/*      */       
/*  458 */       layout = new GridLayout(3, false);
/*  459 */       layout.marginBottom = (layout.marginTop = layout.marginLeft = layout.marginRight = 1);
/*  460 */       engine_comp.setLayout(layout);
/*      */       
/*  462 */       engine_comp.addPaintListener(new PaintListener()
/*      */       {
/*      */         public void paintControl(PaintEvent e)
/*      */         {
/*  466 */           GC gc = e.gc;
/*  467 */           gc.setForeground(org.gudy.azureus2.ui.swt.mainwindow.Colors.grey);
/*      */           
/*  469 */           Point size = engine_comp.getSize();
/*      */           
/*  471 */           gc.drawRectangle(new Rectangle(0, 0, size.x - 1, size.y - 1));
/*      */         }
/*      */         
/*  474 */       });
/*  475 */       final Button button = new Button(engine_comp, 32);
/*      */       
/*  477 */       button.setData(engine);
/*      */       
/*  479 */       buttons.add(button);
/*      */       
/*  481 */       button.setText(engine.getName());
/*      */       
/*  483 */       button.setSelection(!this.deselected_engines.contains(engine.getUID()));
/*      */       
/*  485 */       Image image = getIcon(engine, new ImageLoadListener()
/*      */       {
/*      */ 
/*      */         public void imageLoaded(Image image)
/*      */         {
/*      */ 
/*  491 */           button.setImage(image);
/*      */         }
/*      */       });
/*      */       
/*  495 */       if (image != null)
/*      */       {
/*  497 */         button.setImage(image);
/*      */       }
/*      */       
/*  500 */       button.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*  505 */           String id = engine.getUID();
/*      */           
/*  507 */           if (button.getSelection())
/*      */           {
/*  509 */             SBC_SearchResultsView.this.deselected_engines.remove(id);
/*      */           }
/*      */           else
/*      */           {
/*  513 */             SBC_SearchResultsView.this.deselected_engines.add(id);
/*      */           }
/*      */           
/*  516 */           SBC_SearchResultsView.this.refilter();
/*      */         }
/*      */         
/*  519 */       });
/*  520 */       Menu menu = new Menu(button);
/*      */       
/*  522 */       button.setMenu(menu);
/*      */       
/*  524 */       MenuItem mi = new MenuItem(menu, 8);
/*      */       
/*  526 */       mi.setText(MessageText.getString("label.this.site.only"));
/*      */       
/*  528 */       mi.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*  533 */           SBC_SearchResultsView.this.deselected_engines.clear();
/*      */           
/*  535 */           button.setSelection(true);
/*      */           
/*  537 */           for (Button b : buttons)
/*      */           {
/*  539 */             if (b != button)
/*      */             {
/*  541 */               b.setSelection(false);
/*      */               
/*  543 */               SBC_SearchResultsView.this.deselected_engines.add(((Engine)b.getData()).getUID());
/*      */             }
/*      */           }
/*      */           
/*  547 */           SBC_SearchResultsView.this.refilter();
/*      */         }
/*      */         
/*  550 */       });
/*  551 */       MenuItem miCreateSubscription = new MenuItem(menu, 8);
/*  552 */       Messages.setLanguageText(miCreateSubscription, "menu.search.create.subscription");
/*  553 */       miCreateSubscription.addSelectionListener(new SelectionListener()
/*      */       {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  556 */           Map filterMap = SBC_SearchResultsView.this.buildFilterMap();
/*  557 */           SearchUtils.showCreateSubscriptionDialog(engine.getId(), SBC_SearchResultsView.SearchInstance.access$900(SBC_SearchResultsView.this.current_search).term, filterMap);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void widgetDefaultSelected(SelectionEvent e) {}
/*  564 */       });
/*  565 */       SearchUtils.addMenus(menu, engine, true);
/*      */       
/*  567 */       Label results = new Label(engine_comp, 0);
/*      */       
/*  569 */       GC temp = new GC(results);
/*  570 */       Point size = temp.textExtent("(888)");
/*  571 */       temp.dispose();
/*      */       
/*  573 */       GridData gd = new GridData();
/*      */       
/*  575 */       gd.widthHint = Utils.adjustPXForDPI(size.x);
/*      */       
/*  577 */       results.setLayoutData(gd);
/*      */       
/*  579 */       result_counts.add(results);
/*      */       
/*  581 */       ImageLabel indicator = new ImageLabel(engine_comp, vitality_images[0]);
/*      */       
/*  583 */       indicators.add(indicator);
/*      */       
/*  585 */       indicator.addMouseListener(new MouseAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void mouseDown(MouseEvent e)
/*      */         {
/*      */ 
/*      */ 
/*  593 */           SBC_SearchResultsView.this.deselected_engines.clear();
/*      */           
/*  595 */           boolean only_me_selected = button.getSelection();
/*      */           
/*  597 */           if (only_me_selected)
/*      */           {
/*  599 */             for (Button b : buttons)
/*      */             {
/*  601 */               if (b != button)
/*      */               {
/*  603 */                 if (b.getSelection())
/*      */                 {
/*  605 */                   only_me_selected = false;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  611 */           if (only_me_selected)
/*      */           {
/*  613 */             button.setSelection(false);
/*      */             
/*  615 */             SBC_SearchResultsView.this.deselected_engines.add(engine.getUID());
/*      */             
/*  617 */             for (Button b : buttons)
/*      */             {
/*  619 */               if (b != button)
/*      */               {
/*  621 */                 b.setSelection(true);
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/*  626 */             button.setSelection(true);
/*      */             
/*  628 */             for (Button b : buttons)
/*      */             {
/*  630 */               if (b != button)
/*      */               {
/*  632 */                 b.setSelection(false);
/*      */                 
/*  634 */                 SBC_SearchResultsView.this.deselected_engines.add(((Engine)b.getData()).getUID());
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  639 */           SBC_SearchResultsView.this.refilter();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  644 */     Composite cAddEdit = new Composite(this.engine_area, 0);
/*  645 */     cAddEdit.setLayout(new GridLayout());
/*  646 */     Button btnAddEdit = new Button(cAddEdit, 8);
/*  647 */     btnAddEdit.setLayoutData(new GridData(16777216, 0, true, true));
/*  648 */     Messages.setLanguageText(btnAddEdit, "button.add.edit.search.templates");
/*  649 */     btnAddEdit.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  651 */         UIFunctions functions = UIFunctionsManager.getUIFunctions();
/*  652 */         if (functions != null) {
/*  653 */           functions.viewURL("/xsearch/addedit.php", null, "");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  660 */     });
/*  661 */     Composite cCreateTemplate = new Composite(this.engine_area, 0);
/*  662 */     cCreateTemplate.setLayout(new GridLayout());
/*  663 */     Button btnCreateTemplate = new Button(cCreateTemplate, 8);
/*  664 */     btnCreateTemplate.setLayoutData(new GridData(16777216, 0, true, true));
/*  665 */     Messages.setLanguageText(btnCreateTemplate, "menu.search.create.subscription");
/*  666 */     btnCreateTemplate.addSelectionListener(new SelectionListener()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  669 */         Map filterMap = SBC_SearchResultsView.this.buildFilterMap();
/*  670 */         SearchUtils.showCreateSubscriptionDialog(-1L, SBC_SearchResultsView.SearchInstance.access$900(SBC_SearchResultsView.this.current_search).term, SBC_SearchResultsView.this.buildFilterMap());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*      */     });
/*      */     
/*      */ 
/*  680 */     if (engines.length > 0)
/*      */     {
/*  682 */       new AEThread2("updater")
/*      */       {
/*      */         int ticks;
/*  685 */         int image_index = 0;
/*      */         
/*  687 */         volatile boolean running = true;
/*      */         
/*      */ 
/*      */ 
/*      */         public void run()
/*      */         {
/*  693 */           while (this.running)
/*      */           {
/*  695 */             if (label_comp.isDisposed())
/*      */             {
/*  697 */               return;
/*      */             }
/*      */             try
/*      */             {
/*  701 */               Thread.sleep(100L);
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/*      */ 
/*  706 */             Utils.execSWTThread(new Runnable()
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*      */ 
/*  712 */                 if (SBC_SearchResultsView.17.this.val$label_comp.isDisposed())
/*      */                 {
/*  714 */                   return;
/*      */                 }
/*      */                 
/*  717 */                 SBC_SearchResultsView.17.this.ticks += 1;
/*      */                 
/*  719 */                 SBC_SearchResultsView.17.this.image_index += 1;
/*      */                 
/*  721 */                 if (SBC_SearchResultsView.17.this.image_index == SBC_SearchResultsView.vitality_images.length)
/*      */                 {
/*  723 */                   SBC_SearchResultsView.17.this.image_index = 0;
/*      */                 }
/*      */                 
/*  726 */                 boolean do_results = SBC_SearchResultsView.17.this.ticks % 5 == 0;
/*      */                 
/*  728 */                 boolean all_done = do_results;
/*      */                 
/*  730 */                 for (int i = 0; i < SBC_SearchResultsView.17.this.val$engines.length; i++)
/*      */                 {
/*  732 */                   Object[] status = SBC_SearchResultsView.17.this.val$search.getEngineStatus(SBC_SearchResultsView.17.this.val$engines[i]);
/*      */                   
/*  734 */                   int state = ((Integer)status[0]).intValue();
/*      */                   
/*  736 */                   SBC_SearchResultsView.ImageLabel indicator = (SBC_SearchResultsView.ImageLabel)SBC_SearchResultsView.17.this.val$indicators.get(i);
/*      */                   
/*  738 */                   if (state == 0)
/*      */                   {
/*  740 */                     SBC_SearchResultsView.ImageLabel.access$1100(indicator, SBC_SearchResultsView.vitality_images[SBC_SearchResultsView.17.this.image_index]);
/*      */                   }
/*  742 */                   else if (state == 1)
/*      */                   {
/*  744 */                     SBC_SearchResultsView.ImageLabel.access$1100(indicator, SBC_SearchResultsView.ok_image);
/*      */                   }
/*  746 */                   else if (state == 2)
/*      */                   {
/*  748 */                     SBC_SearchResultsView.ImageLabel.access$1100(indicator, SBC_SearchResultsView.fail_image);
/*      */                     
/*  750 */                     String msg = (String)status[2];
/*      */                     
/*  752 */                     if (msg != null)
/*      */                     {
/*  754 */                       indicator.setToolTipText(msg);
/*      */                     }
/*      */                   }
/*      */                   else {
/*  758 */                     SBC_SearchResultsView.ImageLabel.access$1100(indicator, SBC_SearchResultsView.auth_image);
/*      */                   }
/*      */                   
/*  761 */                   if (do_results)
/*      */                   {
/*  763 */                     if (state == 0)
/*      */                     {
/*  765 */                       all_done = false;
/*      */                     }
/*      */                     
/*  768 */                     String str = "(" + status[1] + ")";
/*      */                     
/*  770 */                     Label rc = (Label)SBC_SearchResultsView.17.this.val$result_counts.get(i);
/*      */                     
/*  772 */                     if (!str.equals(rc.getText()))
/*      */                     {
/*  774 */                       rc.setText(str);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  779 */                 if (all_done)
/*      */                 {
/*  781 */                   SBC_SearchResultsView.17.this.running = false;
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       }.start();
/*      */     }
/*  789 */     this.engine_area.layout(true);
/*      */   }
/*      */   
/*      */   protected Map buildFilterMap() {
/*  793 */     Map<String, Object> mapFilter = new HashMap();
/*  794 */     if ((this.without_keywords != null) && (this.without_keywords.length > 0)) {
/*  795 */       mapFilter.put("text_filter_out", GeneralUtils.stringJoin(Arrays.asList(this.without_keywords), " "));
/*      */     }
/*  797 */     if ((this.with_keywords != null) && (this.with_keywords.length > 0)) {
/*  798 */       mapFilter.put("text_filter", GeneralUtils.stringJoin(Arrays.asList(this.with_keywords), " "));
/*      */     }
/*  800 */     if (this.maxSize > 0) {
/*  801 */       mapFilter.put("max_size", Long.valueOf(this.maxSize * 1024 * 1024L));
/*      */     }
/*  803 */     if (this.minSize > 0) {
/*  804 */       mapFilter.put("min_size", Long.valueOf(this.minSize * 1024 * 1024L));
/*      */     }
/*      */     
/*  807 */     return mapFilter;
/*      */   }
/*      */   
/*      */ 
/*      */   private void resetFilters()
/*      */   {
/*  813 */     synchronized (this.filter_lock)
/*      */     {
/*  815 */       this.minSize = 0;
/*  816 */       this.maxSize = 0;
/*      */       
/*  818 */       this.with_keywords = new String[0];
/*  819 */       this.without_keywords = new String[0];
/*      */       
/*  821 */       this.deselected_engines.clear();
/*      */     }
/*      */     
/*  824 */     Utils.execSWTThread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*  830 */         if ((SBC_SearchResultsView.this.spinMinSize != null) && (!SBC_SearchResultsView.this.spinMinSize.isDisposed())) {
/*  831 */           SBC_SearchResultsView.this.spinMinSize.setSelection(0);
/*      */         }
/*  833 */         if ((SBC_SearchResultsView.this.spinMaxSize != null) && (!SBC_SearchResultsView.this.spinMaxSize.isDisposed())) {
/*  834 */           SBC_SearchResultsView.this.spinMaxSize.setSelection(0);
/*      */         }
/*  836 */         if ((SBC_SearchResultsView.this.textWithKW != null) && (!SBC_SearchResultsView.this.textWithKW.isDisposed())) {
/*  837 */           SBC_SearchResultsView.this.textWithKW.setText("");
/*      */         }
/*  839 */         if ((SBC_SearchResultsView.this.textWithoutKW != null) && (!SBC_SearchResultsView.this.textWithoutKW.isDisposed())) {
/*  840 */           SBC_SearchResultsView.this.textWithoutKW.setText("");
/*      */         }
/*      */         
/*  843 */         if (SBC_SearchResultsView.this.tv_subs_results != null) {
/*  844 */           SBC_SearchResultsView.this.tv_subs_results.setFilterText("");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setSearchEngines(final SearchInstance si)
/*      */   {
/*  854 */     Utils.execSWTThread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*  860 */         SBC_SearchResultsView.this.buildEngineArea(si);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean isOurContent(SBC_SearchResult result)
/*      */   {
/*  869 */     long size = result.getSize();
/*      */     
/*  871 */     long kInB = DisplayFormatters.getKinB();
/*  872 */     long mInB = kInB * kInB;
/*      */     
/*  874 */     boolean size_ok = ((size == -1L) || (size >= mInB * this.minSize)) && ((size == -1L) || (this.maxSize == 0) || (size <= mInB * this.maxSize));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  879 */     if (!size_ok)
/*      */     {
/*  881 */       return false;
/*      */     }
/*      */     
/*  884 */     if ((this.with_keywords.length > 0) || (this.without_keywords.length > 0))
/*      */     {
/*  886 */       synchronized (this.filter_lock)
/*      */       {
/*  888 */         String name = result.getName().toLowerCase(Locale.US);
/*      */         
/*  890 */         for (int i = 0; i < this.with_keywords.length; i++)
/*      */         {
/*  892 */           if (!name.contains(this.with_keywords[i]))
/*      */           {
/*  894 */             return false;
/*      */           }
/*      */         }
/*      */         
/*  898 */         for (int i = 0; i < this.without_keywords.length; i++)
/*      */         {
/*  900 */           if (name.contains(this.without_keywords[i]))
/*      */           {
/*  902 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  908 */     String engine_id = result.getEngine().getUID();
/*      */     
/*  910 */     if (this.deselected_engines.contains(engine_id))
/*      */     {
/*  912 */       return false;
/*      */     }
/*      */     
/*  915 */     return true;
/*      */   }
/*      */   
/*      */   protected void refilter()
/*      */   {
/*  920 */     if (this.tv_subs_results != null) {
/*  921 */       this.tv_subs_results.refilter();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initColumns(AzureusCore core)
/*      */   {
/*  930 */     synchronized (SBC_SearchResultsView.class)
/*      */     {
/*  932 */       if (columnsAdded)
/*      */       {
/*  934 */         return;
/*      */       }
/*      */       
/*  937 */       columnsAdded = true;
/*      */     }
/*      */     
/*  940 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*      */     
/*  942 */     tableManager.registerColumn(SBC_SearchResult.class, "type", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  948 */         new ColumnSearchSubResultType(column);
/*      */       }
/*  950 */     });
/*  951 */     tableManager.registerColumn(SBC_SearchResult.class, ColumnSearchSubResultName.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  957 */         new ColumnSearchSubResultName(column);
/*      */       }
/*      */       
/*  960 */     });
/*  961 */     tableManager.registerColumn(SBC_SearchResult.class, "actions", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  967 */         new ColumnSearchSubResultActions(column);
/*      */       }
/*      */       
/*  970 */     });
/*  971 */     tableManager.registerColumn(SBC_SearchResult.class, "size", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  977 */         new ColumnSearchSubResultSize(column);
/*      */       }
/*      */       
/*  980 */     });
/*  981 */     tableManager.registerColumn(SBC_SearchResult.class, ColumnSearchSubResultSeedsPeers.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  987 */         new ColumnSearchSubResultSeedsPeers(column);
/*      */       }
/*      */       
/*  990 */     });
/*  991 */     tableManager.registerColumn(SBC_SearchResult.class, ColumnSearchSubResultRatings.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/*  997 */         new ColumnSearchSubResultRatings(column);
/*      */       }
/*      */       
/* 1000 */     });
/* 1001 */     tableManager.registerColumn(SBC_SearchResult.class, "age", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/* 1007 */         new ColumnSearchSubResultAge(column);
/*      */       }
/*      */       
/* 1010 */     });
/* 1011 */     tableManager.registerColumn(SBC_SearchResult.class, "rank", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/* 1017 */         new ColumnSearchSubResultRank(column);
/*      */       }
/*      */       
/* 1020 */     });
/* 1021 */     tableManager.registerColumn(SBC_SearchResult.class, ColumnSearchSubResultCategory.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/* 1027 */         new ColumnSearchSubResultCategory(column);
/*      */       }
/*      */       
/* 1030 */     });
/* 1031 */     tableManager.registerColumn(SBC_SearchResult.class, "site", new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/* 1037 */         new ColumnSearchResultSite(column);
/*      */       }
/*      */       
/* 1040 */     });
/* 1041 */     tableManager.registerColumn(SBC_SearchResult.class, ColumnSearchSubResultHash.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/* 1047 */         new ColumnSearchSubResultHash(column);
/*      */       }
/*      */       
/* 1050 */     });
/* 1051 */     tableManager.registerColumn(SBC_SearchResult.class, ColumnSearchSubResultExisting.COLUMN_ID, new TableColumnCreationListener()
/*      */     {
/*      */ 
/*      */       public void tableColumnCreated(TableColumn column)
/*      */       {
/*      */ 
/* 1057 */         new ColumnSearchSubResultExisting(column);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void showView()
/*      */   {
/* 1065 */     SWTSkinObject so_list = getSkinObject("search-results-list");
/*      */     
/* 1067 */     if (so_list != null)
/*      */     {
/* 1069 */       MetaSearchManagerFactory.getSingleton().getMetaSearch().addListener(this);
/*      */       
/* 1071 */       so_list.setVisible(true);
/*      */       
/* 1073 */       initTable((Composite)so_list.getControl());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void refreshView()
/*      */   {
/* 1080 */     if (this.tv_subs_results != null)
/*      */     {
/* 1082 */       this.tv_subs_results.refreshTable(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void hideView()
/*      */   {
/* 1089 */     synchronized (this.search_lock)
/*      */     {
/* 1091 */       if (this.current_search != null)
/*      */       {
/* 1093 */         this.current_search.cancel();
/*      */         
/* 1095 */         this.current_search = null;
/*      */       }
/*      */     }
/*      */     
/* 1099 */     MetaSearchManagerFactory.getSingleton().getMetaSearch().removeListener(this);
/*      */     
/* 1101 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void engineAdded(Engine engine)
/*      */   {
/* 1110 */     if (engine.isActive())
/*      */     {
/* 1112 */       autoSearchAgain();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void engineUpdated(Engine engine) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void engineRemoved(Engine engine)
/*      */   {
/* 1126 */     SearchInstance si = this.current_search;
/*      */     
/* 1128 */     if (si != null)
/*      */     {
/* 1130 */       if (si.getEngineIndex(engine) >= 0)
/*      */       {
/* 1132 */         autoSearchAgain();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void engineStateChanged(Engine engine)
/*      */   {
/* 1141 */     SearchInstance si = this.current_search;
/*      */     
/* 1143 */     if (si != null)
/*      */     {
/* 1145 */       if (si.getEngineIndex(engine) >= 0)
/*      */       {
/* 1147 */         autoSearchAgain();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void initTable(Composite control)
/*      */   {
/* 1156 */     this.tv_subs_results = TableViewFactory.createTableViewSWT(SBC_SearchResult.class, "SearchResults", "SearchResults", new TableColumnCore[0], ColumnSearchSubResultName.COLUMN_ID, 268500994);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1164 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*      */     
/* 1166 */     tableManager.setDefaultColumnNames("SearchResults", new String[] { "type", ColumnSearchSubResultName.COLUMN_ID, "actions", "size", ColumnSearchSubResultSeedsPeers.COLUMN_ID, ColumnSearchSubResultRatings.COLUMN_ID, "age", "rank", ColumnSearchSubResultCategory.COLUMN_ID, "site" });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1180 */     tableManager.setDefaultSortColumnName("SearchResults", "rank");
/*      */     
/*      */ 
/* 1183 */     if (this.txtFilter != null) {
/* 1184 */       this.tv_subs_results.enableFilterCheck(this.txtFilter, this);
/*      */     }
/*      */     
/* 1187 */     this.tv_subs_results.setRowDefaultHeight(COConfigurationManager.getIntParameter("Search Subs Row Height"));
/*      */     
/* 1189 */     SWTSkinObject soSizeSlider = getSkinObject("table-size-slider");
/* 1190 */     if ((soSizeSlider instanceof SWTSkinObjectContainer)) {
/* 1191 */       SWTSkinObjectContainer so = (SWTSkinObjectContainer)soSizeSlider;
/* 1192 */       if (!this.tv_subs_results.enableSizeSlider(so.getComposite(), 16, 100)) {
/* 1193 */         so.setVisible(false);
/*      */       }
/*      */     }
/*      */     
/* 1197 */     this.table_parent = new Composite(control, 0);
/* 1198 */     this.table_parent.setLayoutData(Utils.getFilledFormData());
/* 1199 */     GridLayout layout = new GridLayout();
/* 1200 */     layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/* 1201 */     this.table_parent.setLayout(layout);
/*      */     
/* 1203 */     this.tv_subs_results.addSelectionListener(new TableSelectionListener()
/*      */     {
/*      */ 
/*      */       public void selected(TableRowCore[] _rows)
/*      */       {
/*      */ 
/* 1209 */         updateSelectedContent();
/*      */       }
/*      */       
/*      */ 
/*      */       public void mouseExit(TableRowCore row) {}
/*      */       
/*      */       public void mouseEnter(TableRowCore row) {}
/*      */       
/*      */       public void focusChanged(TableRowCore focus)
/*      */       {
/* 1219 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1220 */         if (uiFunctions != null) {
/* 1221 */           uiFunctions.refreshIconBar();
/*      */         }
/*      */       }
/*      */       
/*      */       public void deselected(TableRowCore[] rows) {
/* 1226 */         updateSelectedContent();
/*      */       }
/*      */       
/*      */       public void defaultSelected(TableRowCore[] rows, int stateMask) {
/* 1230 */         if (rows.length == 1)
/*      */         {
/* 1232 */           SBC_SearchResult rc = (SBC_SearchResult)rows[0].getDataSource();
/*      */           
/* 1234 */           SBC_SearchResultsView.downloadAction(rc);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       private void updateSelectedContent()
/*      */       {
/* 1241 */         TableRowCore[] rows = SBC_SearchResultsView.this.tv_subs_results.getSelectedRows();
/*      */         
/* 1243 */         ArrayList<ISelectedContent> valid = new ArrayList();
/*      */         
/* 1245 */         SBC_SearchResultsView.this.last_selected_content.clear();
/*      */         
/* 1247 */         for (int i = 0; i < rows.length; i++)
/*      */         {
/* 1249 */           SBC_SearchResult rc = (SBC_SearchResult)rows[i].getDataSource();
/*      */           
/* 1251 */           SBC_SearchResultsView.this.last_selected_content.add(rc);
/*      */           
/* 1253 */           byte[] hash = rc.getHash();
/*      */           
/* 1255 */           if ((hash != null) && (hash.length > 0))
/*      */           {
/* 1257 */             SelectedContent sc = new SelectedContent(Base32.encode(hash), rc.getName());
/*      */             
/* 1259 */             sc.setDownloadInfo(new DownloadUrlInfo(SBC_SearchResultsView.this.getDownloadURI(rc)));
/*      */             
/* 1261 */             valid.add(sc);
/*      */           }
/*      */         }
/*      */         
/* 1265 */         ISelectedContent[] sels = (ISelectedContent[])valid.toArray(new ISelectedContent[valid.size()]);
/*      */         
/* 1267 */         com.aelitis.azureus.ui.selectedcontent.SelectedContentManager.changeCurrentlySelectedContent("IconBarEnabler", sels, SBC_SearchResultsView.this.tv_subs_results);
/*      */         
/*      */ 
/* 1270 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*      */         
/* 1272 */         if (uiFunctions != null)
/*      */         {
/* 1274 */           uiFunctions.refreshIconBar(); } } }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1279 */     this.tv_subs_results.addLifeCycleListener(new TableLifeCycleListener()
/*      */     {
/*      */       public void tableViewInitialized() {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void tableViewDestroyed() {}
/* 1293 */     });
/* 1294 */     this.tv_subs_results.addMenuFillListener(new TableViewSWTMenuFillListener()
/*      */     {
/*      */ 
/*      */       public void fillMenu(String sColumnName, Menu menu)
/*      */       {
/*      */ 
/* 1300 */         Object[] _related_content = SBC_SearchResultsView.this.tv_subs_results.getSelectedDataSources().toArray();
/*      */         
/* 1302 */         final SBC_SearchResult[] results = new SBC_SearchResult[_related_content.length];
/*      */         
/* 1304 */         System.arraycopy(_related_content, 0, results, 0, results.length);
/*      */         
/* 1306 */         MenuItem item = new MenuItem(menu, 8);
/* 1307 */         item.setText(MessageText.getString("label.copy.url.to.clip"));
/* 1308 */         item.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1311 */             StringBuffer buffer = new StringBuffer(1024);
/*      */             
/* 1313 */             for (SBC_SearchResult result : results)
/*      */             {
/* 1315 */               if (buffer.length() > 0) {
/* 1316 */                 buffer.append("\r\n");
/*      */               }
/*      */               
/* 1319 */               buffer.append(SBC_SearchResultsView.this.getDownloadURI(result));
/*      */             }
/* 1321 */             org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipBoard(buffer.toString());
/*      */           }
/*      */           
/* 1324 */         });
/* 1325 */         item.setEnabled(results.length > 0);
/*      */         
/* 1327 */         SearchSubsUtils.addMenu(results, menu);
/*      */         
/* 1329 */         new MenuItem(menu, 2);
/*      */         
/* 1331 */         if (results.length == 1)
/*      */         {
/* 1333 */           if (SearchSubsUtils.addMenu(results[0], menu))
/*      */           {
/* 1335 */             new MenuItem(menu, 2);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/* 1346 */     });
/* 1347 */     this.tv_subs_results.initialize(this.table_parent);
/*      */     
/* 1349 */     control.layout(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void invalidate(SBC_SearchResult result)
/*      */   {
/* 1356 */     TableRowCore row = this.tv_subs_results.getRow(result);
/*      */     
/* 1358 */     if (row != null)
/*      */     {
/* 1360 */       row.invalidate(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean filterCheck(SBC_SearchResult ds, String filter, boolean regex)
/*      */   {
/* 1370 */     if (!isOurContent(ds))
/*      */     {
/* 1372 */       return false;
/*      */     }
/*      */     
/* 1375 */     return SearchSubsUtils.filterCheck(ds, filter, regex);
/*      */   }
/*      */   
/*      */ 
/*      */   public void filterSet(String filter) {}
/*      */   
/*      */ 
/*      */   private void autoSearchAgain()
/*      */   {
/* 1384 */     SearchInstance si = this.current_search;
/*      */     
/* 1386 */     if (si != null)
/*      */     {
/* 1388 */       anotherSearch(si.getSearchQuery());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void anotherSearch(SearchResultsTabArea.SearchQuery sq)
/*      */   {
/* 1396 */     synchronized (this.search_lock)
/*      */     {
/* 1398 */       if (this.current_search != null)
/*      */       {
/* 1400 */         this.current_search.cancel();
/*      */       }
/*      */       
/* 1403 */       resetFilters();
/*      */       
/* 1405 */       this.current_search = new SearchInstance(sq, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getDownloadURI(SBC_SearchResult result)
/*      */   {
/* 1413 */     String torrent_url = result.getTorrentLink();
/*      */     
/* 1415 */     if ((torrent_url != null) && (torrent_url.length() > 0))
/*      */     {
/* 1417 */       return torrent_url;
/*      */     }
/*      */     
/* 1420 */     String uri = UrlUtils.getMagnetURI(result.getHash(), result.getName(), new String[] { "Public" });
/*      */     
/* 1422 */     return uri;
/*      */   }
/*      */   
/* 1425 */   private static ImageLoader image_loader = new ImageLoader(null, null);
/*      */   
/* 1427 */   private static Map<String, Object[]> image_map = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */   public Image getIcon(SBC_SearchResult result)
/*      */   {
/* 1433 */     return getIcon(result.getEngine(), result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Image getIcon(Engine engine, ImageLoadListener result)
/*      */   {
/* 1441 */     String icon = engine.getIcon();
/*      */     
/* 1443 */     Image img = null;
/*      */     
/* 1445 */     if (icon != null)
/*      */     {
/* 1447 */       Object[] x = (Object[])image_map.get(icon);
/*      */       
/* 1449 */       if (x == null)
/*      */       {
/* 1451 */         Set<ImageLoadListener> waiters = new HashSet();
/*      */         
/* 1453 */         final Object[] f_x = { null, waiters, Long.valueOf(SystemTime.getMonotonousTime()) };
/*      */         
/* 1455 */         waiters.add(result);
/*      */         
/* 1457 */         image_map.put(icon, f_x);
/*      */         
/* 1459 */         image_loader.getUrlImage(icon, new Point(0, Utils.adjustPXForDPI(16)), new ImageLoader.ImageDownloaderListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void imageDownloaded(Image image, boolean returnedImmediately)
/*      */           {
/*      */ 
/* 1466 */             f_x[0] = image;
/*      */             
/* 1468 */             Set<SBC_SearchResultsView.ImageLoadListener> set = (Set)f_x[1];
/*      */             
/* 1470 */             for (SBC_SearchResultsView.ImageLoadListener result : set)
/*      */             {
/* 1472 */               result.imageLoaded(image);
/*      */             }
/*      */             
/* 1475 */             f_x[1] = null;
/*      */           }
/*      */           
/* 1478 */         });
/* 1479 */         img = (Image)f_x[0];
/*      */ 
/*      */ 
/*      */       }
/* 1483 */       else if ((x[1] instanceof Set))
/*      */       {
/* 1485 */         ((Set)x[1]).add(result);
/*      */       }
/*      */       else
/*      */       {
/* 1489 */         img = (Image)x[0];
/*      */         
/* 1491 */         if (img == null)
/*      */         {
/* 1493 */           if (SystemTime.getMonotonousTime() - ((Long)x[2]).longValue() > 120000L)
/*      */           {
/* 1495 */             image_map.remove(icon);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1502 */     return img;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResultCount()
/*      */   {
/* 1508 */     SearchInstance ci = this.current_search;
/*      */     
/* 1510 */     if (ci == null)
/*      */     {
/* 1512 */       return -1;
/*      */     }
/*      */     
/* 1515 */     return this.current_search.getResultCount();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private class SearchInstance
/*      */     implements com.aelitis.azureus.core.metasearch.ResultListener
/*      */   {
/*      */     private final SearchResultsTabArea.SearchQuery sq;
/*      */     
/*      */ 
/*      */     private final Engine[] engines;
/*      */     
/*      */ 
/*      */     private final Object[][] engine_status;
/*      */     
/*      */ 
/*      */     private boolean _cancelled;
/*      */     
/*      */ 
/* 1536 */     private Set<Engine> pending = new HashSet();
/*      */     
/* 1538 */     private AtomicInteger result_count = new AtomicInteger();
/*      */     
/*      */ 
/*      */ 
/*      */     private SearchInstance(SearchResultsTabArea.SearchQuery _sq)
/*      */     {
/* 1544 */       this.sq = _sq;
/*      */       
/* 1546 */       SBC_SearchResultsView.this.tv_subs_results.removeAllTableRows();
/*      */       
/* 1548 */       SWTSkinObjectText title = (SWTSkinObjectText)SBC_SearchResultsView.this.parent.getSkinObject("title");
/*      */       
/* 1550 */       if (title != null)
/*      */       {
/* 1552 */         title.setText(MessageText.getString("search.results.view.title", new String[] { this.sq.term }));
/*      */       }
/*      */       
/* 1555 */       MetaSearchManager metaSearchManager = MetaSearchManagerFactory.getSingleton();
/*      */       
/* 1557 */       List<SearchParameter> sps = new ArrayList();
/*      */       
/* 1559 */       sps.add(new SearchParameter("s", this.sq.term));
/*      */       
/* 1561 */       SearchParameter[] parameters = (SearchParameter[])sps.toArray(new SearchParameter[sps.size()]);
/*      */       
/* 1563 */       Map<String, String> context = new HashMap();
/*      */       
/* 1565 */       context.put("force_full", "true");
/*      */       
/* 1567 */       context.put("batch_millis", "250");
/*      */       
/* 1569 */       context.put("remove_dup_hash", "true");
/*      */       
/* 1571 */       String headers = null;
/*      */       
/* 1573 */       SBC_SearchResultsView.this.parent.setBusy(true);
/*      */       
/* 1575 */       synchronized (this.pending)
/*      */       {
/* 1577 */         this.engines = metaSearchManager.getMetaSearch().search(this, parameters, headers, context, 500);
/*      */         
/* 1579 */         this.engine_status = new Object[this.engines.length][];
/*      */         
/* 1581 */         for (int i = 0; i < this.engine_status.length; i++)
/*      */         {
/* 1583 */           this.engine_status[i] = { Integer.valueOf(0), Integer.valueOf(0), null };
/*      */         }
/*      */         
/* 1586 */         SBC_SearchResultsView.this.setSearchEngines(this);
/*      */         
/* 1588 */         if (this.engines.length == 0)
/*      */         {
/* 1590 */           SBC_SearchResultsView.this.parent.setBusy(false);
/*      */         }
/*      */         else
/*      */         {
/* 1594 */           this.pending.addAll(Arrays.asList(this.engines));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected SearchResultsTabArea.SearchQuery getSearchQuery()
/*      */     {
/* 1602 */       return this.sq;
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     protected Engine[] getEngines()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 261	com/aelitis/azureus/ui/swt/search/SBC_SearchResultsView$SearchInstance:pending	Ljava/util/Set;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: getfield 257	com/aelitis/azureus/ui/swt/search/SBC_SearchResultsView$SearchInstance:engines	[Lcom/aelitis/azureus/core/metasearch/Engine;
/*      */       //   11: aload_1
/*      */       //   12: monitorexit
/*      */       //   13: areturn
/*      */       //   14: astore_2
/*      */       //   15: aload_1
/*      */       //   16: monitorexit
/*      */       //   17: aload_2
/*      */       //   18: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1608	-> byte code offset #0
/*      */       //   Java source line #1610	-> byte code offset #7
/*      */       //   Java source line #1611	-> byte code offset #14
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	19	0	this	SearchInstance
/*      */       //   5	11	1	Ljava/lang/Object;	Object
/*      */       //   14	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	13	14	finally
/*      */       //   14	17	14	finally
/*      */     }
/*      */     
/*      */     protected int getEngineIndex(Engine e)
/*      */     {
/* 1618 */       synchronized (this.pending)
/*      */       {
/* 1620 */         for (int i = 0; i < this.engines.length; i++) {
/* 1621 */           if (this.engines[i] == e) {
/* 1622 */             return i;
/*      */           }
/*      */         }
/* 1625 */         return -1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected Object[] getEngineStatus(Engine engine)
/*      */     {
/* 1633 */       int i = getEngineIndex(engine);
/*      */       
/* 1635 */       if (i >= 0)
/*      */       {
/* 1637 */         return this.engine_status[i];
/*      */       }
/*      */       
/*      */ 
/* 1641 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void cancel()
/*      */     {
/* 1648 */       this._cancelled = true;
/*      */       
/* 1650 */       SBC_SearchResultsView.this.parent.setBusy(false);
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     private boolean isCancelled()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 258	com/aelitis/azureus/ui/swt/search/SBC_SearchResultsView$SearchInstance:this$0	Lcom/aelitis/azureus/ui/swt/search/SBC_SearchResultsView;
/*      */       //   4: invokestatic 269	com/aelitis/azureus/ui/swt/search/SBC_SearchResultsView:access$2500	(Lcom/aelitis/azureus/ui/swt/search/SBC_SearchResultsView;)Ljava/lang/Object;
/*      */       //   7: dup
/*      */       //   8: astore_1
/*      */       //   9: monitorenter
/*      */       //   10: aload_0
/*      */       //   11: getfield 256	com/aelitis/azureus/ui/swt/search/SBC_SearchResultsView$SearchInstance:_cancelled	Z
/*      */       //   14: aload_1
/*      */       //   15: monitorexit
/*      */       //   16: ireturn
/*      */       //   17: astore_2
/*      */       //   18: aload_1
/*      */       //   19: monitorexit
/*      */       //   20: aload_2
/*      */       //   21: athrow
/*      */       // Line number table:
/*      */       //   Java source line #1656	-> byte code offset #0
/*      */       //   Java source line #1658	-> byte code offset #10
/*      */       //   Java source line #1659	-> byte code offset #17
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	22	0	this	SearchInstance
/*      */       //   8	11	1	Ljava/lang/Object;	Object
/*      */       //   17	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   10	16	17	finally
/*      */       //   17	20	17	finally
/*      */     }
/*      */     
/*      */     public void contentReceived(Engine engine, String content) {}
/*      */     
/*      */     public void matchFound(Engine engine, String[] fields) {}
/*      */     
/*      */     public void engineFailed(Engine engine, Throwable e)
/*      */     {
/* 1681 */       if (isCancelled())
/*      */       {
/* 1683 */         return;
/*      */       }
/*      */       
/* 1686 */       engineDone(engine, 2, Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void engineRequiresLogin(Engine engine, Throwable e)
/*      */     {
/* 1694 */       if (isCancelled())
/*      */       {
/* 1696 */         return;
/*      */       }
/*      */       
/* 1699 */       engineDone(engine, 3, null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void resultsComplete(Engine engine)
/*      */     {
/* 1706 */       if (isCancelled())
/*      */       {
/* 1708 */         return;
/*      */       }
/*      */       
/* 1711 */       engineDone(engine, 1, null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void engineDone(Engine engine, int state, String msg)
/*      */     {
/* 1720 */       int i = getEngineIndex(engine);
/*      */       
/* 1722 */       if (i >= 0)
/*      */       {
/* 1724 */         this.engine_status[i][0] = Integer.valueOf(state);
/* 1725 */         this.engine_status[i][2] = msg;
/*      */       }
/*      */       
/* 1728 */       synchronized (this.pending)
/*      */       {
/* 1730 */         this.pending.remove(engine);
/*      */         
/* 1732 */         if (this.pending.isEmpty())
/*      */         {
/* 1734 */           SBC_SearchResultsView.this.parent.setBusy(false);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void resultsReceived(Engine engine, Result[] results)
/*      */     {
/* 1744 */       synchronized (SBC_SearchResultsView.this.search_lock)
/*      */       {
/* 1746 */         if (isCancelled())
/*      */         {
/* 1748 */           return;
/*      */         }
/*      */         
/* 1751 */         int index = getEngineIndex(engine);
/*      */         
/* 1753 */         if (index >= 0)
/*      */         {
/* 1755 */           int count = ((Integer)this.engine_status[index][1]).intValue();
/*      */           
/* 1757 */           this.engine_status[index][1] = Integer.valueOf(count + results.length);
/*      */         }
/*      */         
/* 1760 */         SBC_SearchResult[] data_sources = new SBC_SearchResult[results.length];
/*      */         
/* 1762 */         for (int i = 0; i < results.length; i++)
/*      */         {
/* 1764 */           data_sources[i] = new SBC_SearchResult(SBC_SearchResultsView.this, engine, results[i]);
/*      */         }
/*      */         
/* 1767 */         SBC_SearchResultsView.this.tv_subs_results.addDataSources(data_sources);
/*      */         
/* 1769 */         SBC_SearchResultsView.this.tv_subs_results.processDataSourceQueueSync();
/*      */         
/* 1771 */         this.result_count.addAndGet(results.length);
/*      */         
/* 1773 */         SBC_SearchResultsView.this.parent.resultsFound();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getResultCount()
/*      */     {
/* 1780 */       return this.result_count.get();
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface ImageLoadListener
/*      */   {
/*      */     public abstract void imageLoaded(Image paramImage);
/*      */   }
/*      */   
/*      */   static class ImageLabel extends Canvas implements PaintListener
/*      */   {
/*      */     private Image image;
/*      */     
/*      */     public ImageLabel(Composite parent, Image _image)
/*      */     {
/* 1795 */       super(536870912);
/*      */       
/* 1797 */       this.image = _image;
/*      */       
/* 1799 */       addPaintListener(this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void paintControl(PaintEvent e)
/*      */     {
/* 1806 */       if (!this.image.isDisposed())
/*      */       {
/* 1808 */         Point size = getSize();
/*      */         
/* 1810 */         Rectangle rect = this.image.getBounds();
/*      */         
/* 1812 */         int x_offset = Math.max(0, (size.x - rect.width) / 2);
/* 1813 */         int y_offset = Math.max(0, (size.y - rect.height) / 2);
/*      */         
/* 1815 */         e.gc.drawImage(this.image, x_offset, y_offset);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Point computeSize(int wHint, int hHint, boolean changed)
/*      */     {
/* 1826 */       if (this.image.isDisposed()) {
/* 1827 */         return new Point(0, 0);
/*      */       }
/*      */       
/* 1830 */       Rectangle rect = this.image.getBounds();
/*      */       
/* 1832 */       return new Point(rect.width, rect.height);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setImage(Image _image)
/*      */     {
/* 1839 */       if (_image == this.image)
/*      */       {
/* 1841 */         return;
/*      */       }
/*      */       
/* 1844 */       this.image = _image;
/*      */       
/* 1846 */       redraw();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void downloadAction(SearchSubsResultBase entry)
/*      */   {
/* 1854 */     String link = entry.getTorrentLink();
/*      */     
/* 1856 */     if (link.startsWith("chat:"))
/*      */     {
/* 1858 */       Utils.launch(link);
/*      */       
/* 1860 */       return;
/*      */     }
/*      */     
/* 1863 */     showDownloadFTUX(entry, new UserPrompterResultListener()
/*      */     {
/*      */ 
/*      */       public void prompterClosed(int result)
/*      */       {
/*      */ 
/* 1869 */         if (result == 0) {
/* 1870 */           String referer_str = null;
/*      */           
/* 1872 */           String torrentUrl = this.val$entry.getTorrentLink();
/*      */           
/* 1874 */           if (UrlFilter.getInstance().isWhitelisted(torrentUrl))
/*      */           {
/* 1876 */             ContentNetwork cn = ContentNetworkManagerFactory.getSingleton().getContentNetworkForURL(torrentUrl);
/*      */             
/* 1878 */             if (cn == null)
/*      */             {
/* 1880 */               cn = ConstantsVuze.getDefaultContentNetwork();
/*      */             }
/*      */             
/* 1883 */             torrentUrl = cn.appendURLSuffix(torrentUrl, false, true);
/*      */           }
/*      */           try
/*      */           {
/* 1887 */             Map headers = UrlUtils.getBrowserHeaders(referer_str);
/*      */             
/* 1889 */             if ((this.val$entry instanceof SBC_SubscriptionResult))
/*      */             {
/* 1891 */               SBC_SubscriptionResult sub_entry = (SBC_SubscriptionResult)this.val$entry;
/*      */               
/* 1893 */               Subscription subs = sub_entry.getSubscription();
/*      */               try
/*      */               {
/* 1896 */                 Engine engine = subs.getEngine();
/*      */                 
/* 1898 */                 if ((engine != null) && ((engine instanceof WebEngine)))
/*      */                 {
/* 1900 */                   WebEngine webEngine = (WebEngine)engine;
/*      */                   
/* 1902 */                   if (webEngine.isNeedsAuth())
/*      */                   {
/* 1904 */                     headers.put("Cookie", webEngine.getCookies());
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {
/* 1909 */                 Debug.out(e);
/*      */               }
/*      */               
/* 1912 */               subs.addPotentialAssociation(sub_entry.getID(), torrentUrl);
/*      */             }
/*      */             else
/*      */             {
/* 1916 */               SBC_SearchResult search_entry = (SBC_SearchResult)this.val$entry;
/*      */               
/* 1918 */               Engine engine = search_entry.getEngine();
/*      */               
/* 1920 */               if (engine != null)
/*      */               {
/* 1922 */                 engine.addPotentialAssociation(torrentUrl);
/*      */                 
/* 1924 */                 if ((engine instanceof WebEngine))
/*      */                 {
/* 1926 */                   WebEngine webEngine = (WebEngine)engine;
/*      */                   
/* 1928 */                   if (webEngine.isNeedsAuth())
/*      */                   {
/* 1930 */                     headers.put("Cookie", webEngine.getCookies());
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1936 */             byte[] torrent_hash = this.val$entry.getHash();
/*      */             
/* 1938 */             if (torrent_hash != null)
/*      */             {
/* 1940 */               if ((torrent_hash != null) && (!torrentUrl.toLowerCase().startsWith("magnet")))
/*      */               {
/* 1942 */                 String title = this.val$entry.getName();
/*      */                 
/* 1944 */                 String magnet = UrlUtils.getMagnetURI(torrent_hash, title, null);
/*      */                 
/* 1946 */                 headers.put("X-Alternative-URI-1", magnet);
/*      */               }
/*      */             }
/*      */             
/* 1950 */             PluginInitializer.getDefaultInterface().getDownloadManager().addDownload(new java.net.URL(torrentUrl), headers);
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/* 1956 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void showDownloadFTUX(SearchSubsResultBase entry, final UserPrompterResultListener listener)
/*      */   {
/* 1968 */     if ((entry instanceof SBC_SubscriptionResult))
/*      */     {
/* 1970 */       listener.prompterClosed(0);
/*      */       
/* 1972 */       return;
/*      */     }
/*      */     
/* 1975 */     if (RememberedDecisionsManager.getRememberedDecision("searchsubs.dl.ftux") == 1)
/*      */     {
/* 1977 */       listener.prompterClosed(0);
/*      */       
/* 1979 */       return;
/*      */     }
/*      */     
/* 1982 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("searchsubs.dl.ftux.title"), null, new String[] { MessageText.getString("Button.ok"), MessageText.getString("Button.cancel") }, 0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1987 */     box.setSubTitle(MessageText.getString("searchsubs.dl.ftux.heading"));
/*      */     
/* 1989 */     boolean[] check_state = { true };
/*      */     
/* 1991 */     box.setListener(new VuzeMessageBoxListener() {
/*      */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 1993 */         SWTSkin skin = soExtra.getSkin();
/* 1994 */         SBC_SearchResultsView.addResourceBundle(skin, "com/aelitis/azureus/ui/swt/columns/searchsubs/", "skin3_dl_ftux");
/*      */         
/*      */ 
/* 1997 */         String id = "searchsubs.dlftux.shell";
/* 1998 */         skin.createSkinObject(id, id, soExtra);
/*      */         
/* 2000 */         SWTSkinObjectCheckbox cb = (SWTSkinObjectCheckbox)skin.getSkinObject("agree-checkbox");
/* 2001 */         cb.setChecked(true);
/* 2002 */         cb.addSelectionListener(new SWTSkinCheckboxListener() {
/*      */           public void checkboxChanged(SWTSkinObjectCheckbox so, boolean checked) {
/* 2004 */             SBC_SearchResultsView.37.this.val$check_state[0] = checked;
/*      */           }
/*      */           
/*      */         });
/*      */       }
/* 2009 */     });
/* 2010 */     box.open(new UserPrompterResultListener()
/*      */     {
/*      */ 
/*      */       public void prompterClosed(int result)
/*      */       {
/* 2015 */         if ((result == 0) && (this.val$check_state[0] != 0))
/*      */         {
/* 2017 */           RememberedDecisionsManager.setRemembered("searchsubs.dl.ftux", 1);
/*      */         }
/*      */         
/* 2020 */         listener.prompterClosed(result);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static void addResourceBundle(SWTSkin skin, String path, String name)
/*      */   {
/* 2027 */     String sFile = path + name;
/* 2028 */     ClassLoader loader = ColumnSearchSubResultActions.class.getClassLoader();
/* 2029 */     SWTSkinProperties skinProperties = skin.getSkinProperties();
/*      */     try {
/* 2031 */       ResourceBundle subBundle = ResourceBundle.getBundle(sFile, Locale.getDefault(), loader);
/*      */       
/* 2033 */       skinProperties.addResourceBundle(subBundle, path, loader);
/*      */     } catch (MissingResourceException mre) {
/* 2035 */       Debug.out(mre);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SBC_SearchResultsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */