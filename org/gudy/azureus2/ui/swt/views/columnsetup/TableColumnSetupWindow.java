/*      */ package org.gudy.azureus2.ui.swt.views.columnsetup;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.RegExUtil;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableCountChangeListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableSelectionAdapter;
/*      */ import com.aelitis.azureus.ui.common.table.TableStructureModificationListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.dnd.DragSource;
/*      */ import org.eclipse.swt.dnd.DragSourceEvent;
/*      */ import org.eclipse.swt.dnd.DragSourceListener;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.dnd.DropTargetAdapter;
/*      */ import org.eclipse.swt.dnd.DropTargetEvent;
/*      */ import org.eclipse.swt.dnd.DropTargetListener;
/*      */ import org.eclipse.swt.dnd.TextTransfer;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.KeyListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.events.TraverseEvent;
/*      */ import org.eclipse.swt.events.TraverseListener;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Combo;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.ExpandBar;
/*      */ import org.eclipse.swt.widgets.ExpandItem;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BubbleTextBox;
/*      */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*      */ 
/*      */ public class TableColumnSetupWindow implements UIUpdatable
/*      */ {
/*      */   private static final String TABLEID_AVAIL = "ColumnSetupAvail";
/*      */   private static final String TABLEID_CHOSEN = "ColumnSetupChosen";
/*      */   private static final boolean CAT_BUTTONS = true;
/*      */   private Shell shell;
/*      */   private TableViewSWT<TableColumn> tvAvail;
/*      */   private final String forTableID;
/*      */   private final Class<?> forDataSourceType;
/*      */   private Composite cTableAvail;
/*      */   private Composite cCategories;
/*      */   private TableViewSWT tvChosen;
/*      */   private Composite cTableChosen;
/*      */   private TableColumnCore[] columnsChosen;
/*      */   private final TableRow sampleRow;
/*      */   private DragSourceListener dragSourceListener;
/*      */   private final TableStructureModificationListener<?> listener;
/*      */   private TableColumnCore[] columnsOriginalOrder;
/*   97 */   protected boolean apply = false;
/*      */   
/*   99 */   private Button[] radProficiency = new Button[3];
/*      */   
/*  101 */   private Map<TableColumnCore, Boolean> mapNewVisibility = new HashMap();
/*      */   
/*      */   private ArrayList<TableColumnCore> listColumnsNoCat;
/*      */   
/*      */   private ArrayList<String> listCats;
/*      */   
/*      */   private Combo comboFilter;
/*      */   
/*      */   private Group cPickArea;
/*      */   
/*      */   protected boolean doReset;
/*      */   
/*      */   public TableColumnSetupWindow(Class<?> forDataSourceType, String _tableID, TableRow sampleRow, TableStructureModificationListener<?> _listener)
/*      */   {
/*  115 */     this.sampleRow = sampleRow;
/*  116 */     this.listener = _listener;
/*      */     
/*  118 */     this.forDataSourceType = forDataSourceType;
/*  119 */     this.forTableID = _tableID;
/*      */     
/*  121 */     this.dragSourceListener = new DragSourceListener() {
/*      */       private TableColumnCore tableColumn;
/*      */       
/*      */       public void dragStart(DragSourceEvent event) {
/*  125 */         event.doit = true;
/*      */         
/*  127 */         if (!(event.widget instanceof DragSource)) {
/*  128 */           event.doit = false;
/*  129 */           return;
/*      */         }
/*      */         
/*  132 */         TableView<?> tv = (TableView)((DragSource)event.widget).getData("tv");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  143 */         TableRowCore row = tv.getFocusedRow();
/*  144 */         if (row == null) {
/*  145 */           event.doit = false;
/*  146 */           return;
/*      */         }
/*      */         
/*  149 */         this.tableColumn = ((TableColumnCore)row.getDataSource());
/*      */         
/*      */ 
/*  152 */         if ((event.image != null) && (!Constants.isLinux)) {
/*      */           try {
/*  154 */             GC gc = new GC(event.image);
/*      */             try {
/*  156 */               Rectangle bounds = event.image.getBounds();
/*  157 */               gc.fillRectangle(bounds);
/*  158 */               String title = MessageText.getString(this.tableColumn.getTitleLanguageKey(), this.tableColumn.getName());
/*      */               
/*  160 */               String s = title + " Column will be placed at the location you drop it, shifting other columns down";
/*      */               
/*  162 */               GCStringPrinter sp = new GCStringPrinter(gc, s, bounds, false, false, 16777280);
/*      */               
/*  164 */               sp.calculateMetrics();
/*  165 */               if (sp.isCutoff()) {
/*  166 */                 GCStringPrinter.printString(gc, title, bounds, false, false, 16777280);
/*      */               }
/*      */               else {
/*  169 */                 sp.printString();
/*      */               }
/*      */             } finally {
/*  172 */               gc.dispose();
/*      */             }
/*      */           }
/*      */           catch (Throwable t) {}
/*      */         }
/*      */       }
/*      */       
/*      */       public void dragSetData(DragSourceEvent event)
/*      */       {
/*  181 */         if (!(event.widget instanceof DragSource)) {
/*  182 */           return;
/*      */         }
/*      */         
/*  185 */         TableView<?> tv = (TableView)((DragSource)event.widget).getData("tv");
/*  186 */         event.data = ("" + (tv == TableColumnSetupWindow.this.tvChosen ? "c" : "a"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void dragFinished(DragSourceEvent event) {}
/*  192 */     };
/*  193 */     String tableName = MessageText.getString(_tableID + "View.header", (String)null);
/*      */     
/*  195 */     if (tableName == null) {
/*  196 */       tableName = MessageText.getString(_tableID + "View.title.full", (String)null);
/*      */       
/*  198 */       if (tableName == null) {
/*  199 */         tableName = _tableID;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  204 */     this.shell = org.gudy.azureus2.ui.swt.components.shell.ShellFactory.createShell(Utils.findAnyShell(), 1264);
/*  205 */     Utils.setShellIcon(this.shell);
/*  206 */     FormLayout formLayout = new FormLayout();
/*  207 */     this.shell.setText(MessageText.getString("ColumnSetup.title", new String[] { tableName }));
/*      */     
/*      */ 
/*  210 */     this.shell.setLayout(formLayout);
/*  211 */     this.shell.setSize(780, 550);
/*      */     
/*  213 */     this.shell.addTraverseListener(new TraverseListener() {
/*      */       public void keyTraversed(TraverseEvent e) {
/*  215 */         if (e.detail == 2) {
/*  216 */           TableColumnSetupWindow.this.shell.dispose();
/*      */         }
/*      */       }
/*  219 */     });
/*  220 */     this.shell.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  222 */         TableColumnSetupWindow.this.close();
/*      */       }
/*      */       
/*  225 */     });
/*  226 */     Label topInfo = new Label(this.shell, 64);
/*  227 */     Messages.setLanguageText(topInfo, "ColumnSetup.explain");
/*      */     
/*  229 */     FormData fd = Utils.getFilledFormData();
/*  230 */     fd.left.offset = 5;
/*  231 */     fd.top.offset = 5;
/*  232 */     fd.bottom = null;
/*  233 */     Utils.setLayoutData(topInfo, fd);
/*      */     
/*  235 */     Button btnOk = new Button(this.shell, 8);
/*  236 */     Messages.setLanguageText(btnOk, "Button.ok");
/*  237 */     btnOk.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  239 */         TableColumnSetupWindow.this.apply = true;
/*  240 */         TableColumnSetupWindow.this.shell.dispose();
/*      */       }
/*      */       
/*  243 */     });
/*  244 */     this.cPickArea = new Group(this.shell, 0);
/*  245 */     this.cPickArea.setLayout(new FormLayout());
/*      */     
/*      */ 
/*  248 */     final ExpandBar expandFilters = new ExpandBar(this.cPickArea, 0);
/*  249 */     expandFilters.setSpacing(1);
/*      */     
/*  251 */     final Composite cFilterArea = new Composite(expandFilters, 0);
/*  252 */     cFilterArea.setLayout(new FormLayout());
/*      */     
/*  254 */     final TableColumnManager tcm = TableColumnManager.getInstance();
/*      */     
/*  256 */     Group cResultArea = new Group(this.shell, 0);
/*  257 */     Messages.setLanguageText(cResultArea, "ColumnSetup.chosencolumns");
/*  258 */     cResultArea.setLayout(new FormLayout());
/*      */     
/*  260 */     Composite cResultButtonArea = new Composite(cResultArea, 0);
/*  261 */     cResultButtonArea.setLayout(new FormLayout());
/*      */     
/*  263 */     this.tvAvail = createTVAvail();
/*      */     
/*  265 */     this.cTableAvail = new Composite(this.cPickArea, 524288);
/*  266 */     GridLayout gridLayout = new GridLayout();
/*  267 */     gridLayout.marginWidth = (gridLayout.marginHeight = 0);
/*  268 */     this.cTableAvail.setLayout(gridLayout);
/*      */     
/*  270 */     BubbleTextBox bubbleTextBox = new BubbleTextBox(this.cTableAvail, 2948);
/*  271 */     bubbleTextBox.getTextWidget().setMessage(MessageText.getString("column.setup.search"));
/*  272 */     GridData gd = new GridData(131072, 16777216, true, false);
/*  273 */     bubbleTextBox.getParent().setLayoutData(gd);
/*      */     
/*  275 */     this.tvAvail.enableFilterCheck(bubbleTextBox.getTextWidget(), new TableViewFilterCheck()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean filterCheck(TableColumn ds, String filter, boolean regex)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  286 */         TableColumnCore core = (TableColumnCore)ds;
/*      */         
/*  288 */         String raw_key = core.getTitleLanguageKey(false);
/*  289 */         String current_key = core.getTitleLanguageKey(true);
/*      */         
/*  291 */         String name1 = MessageText.getString(raw_key, core.getName());
/*  292 */         String name2 = null;
/*      */         
/*  294 */         if (!raw_key.equals(current_key)) {
/*  295 */           String rename = MessageText.getString(current_key, "");
/*  296 */           if (rename.length() > 0) {
/*  297 */             name2 = rename;
/*      */           }
/*      */         }
/*  300 */         String[] names = { name1, name2, MessageText.getString(core.getTitleLanguageKey() + ".info") };
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  306 */         for (String name : names)
/*      */         {
/*  308 */           if (name != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  313 */             String s = "\\Q" + filter.replaceAll("[|;]", "\\\\E|\\\\Q") + "\\E";
/*      */             
/*  315 */             boolean match_result = true;
/*      */             
/*  317 */             if ((regex) && (s.startsWith("!")))
/*      */             {
/*  319 */               s = s.substring(1);
/*      */               
/*  321 */               match_result = false;
/*      */             }
/*      */             
/*  324 */             Pattern pattern = RegExUtil.getCachedPattern("tcs:search", s, 2);
/*      */             
/*  326 */             if (pattern.matcher(name).find() == match_result)
/*      */             {
/*  328 */               return true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  333 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void filterSet(String filter) {}
/*  340 */     });
/*  341 */     this.tvAvail.initialize(this.cTableAvail);
/*      */     
/*  343 */     TableColumnCore[] datasources = tcm.getAllTableColumnCoreAsArray(forDataSourceType, this.forTableID);
/*      */     
/*      */ 
/*  346 */     this.listColumnsNoCat = new ArrayList(Arrays.asList(datasources));
/*      */     
/*  348 */     this.listCats = new ArrayList();
/*  349 */     for (int i = 0; i < datasources.length; i++) {
/*  350 */       TableColumnCore column = datasources[i];
/*  351 */       TableColumnInfo info = tcm.getColumnInfo(forDataSourceType, this.forTableID, column.getName());
/*      */       
/*  353 */       if (info != null) {
/*  354 */         String[] categories = info.getCategories();
/*  355 */         if ((categories != null) && (categories.length > 0)) {
/*  356 */           for (int j = 0; j < categories.length; j++) {
/*  357 */             String cat = categories[j];
/*  358 */             if (!this.listCats.contains(cat)) {
/*  359 */               this.listCats.add(cat);
/*      */             }
/*      */           }
/*  362 */           this.listColumnsNoCat.remove(column);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  367 */     Listener radListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  369 */         TableColumnSetupWindow.this.fillAvail();
/*      */       }
/*      */       
/*      */ 
/*  373 */     };
/*  374 */     Composite cProficiency = new Composite(cFilterArea, 0);
/*  375 */     cProficiency.setBackgroundMode(2);
/*  376 */     cProficiency.setLayout(new FormLayout());
/*      */     
/*  378 */     Label lblProficiency = new Label(cProficiency, 0);
/*  379 */     Messages.setLanguageText(lblProficiency, "ColumnSetup.proficiency");
/*      */     
/*  381 */     this.radProficiency[0] = new Button(cProficiency, 16);
/*  382 */     Messages.setLanguageText(this.radProficiency[0], "ConfigView.section.mode.beginner");
/*  383 */     fd = new FormData();
/*  384 */     fd.left = new FormAttachment(lblProficiency, 5);
/*  385 */     this.radProficiency[0].setLayoutData(fd);
/*  386 */     this.radProficiency[0].addListener(13, radListener);
/*      */     
/*  388 */     this.radProficiency[1] = new Button(cProficiency, 16);
/*  389 */     Messages.setLanguageText(this.radProficiency[1], "ConfigView.section.mode.intermediate");
/*  390 */     fd = new FormData();
/*  391 */     fd.left = new FormAttachment(this.radProficiency[0], 5);
/*  392 */     this.radProficiency[1].setLayoutData(fd);
/*  393 */     this.radProficiency[1].addListener(13, radListener);
/*      */     
/*  395 */     this.radProficiency[2] = new Button(cProficiency, 16);
/*  396 */     Messages.setLanguageText(this.radProficiency[2], "ConfigView.section.mode.advanced");
/*  397 */     fd = new FormData();
/*  398 */     fd.left = new FormAttachment(this.radProficiency[1], 5);
/*  399 */     this.radProficiency[2].setLayoutData(fd);
/*  400 */     this.radProficiency[2].addListener(13, radListener);
/*      */     
/*  402 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  403 */     if (userMode < 0) {
/*  404 */       userMode = 0;
/*  405 */     } else if (userMode >= this.radProficiency.length) {
/*  406 */       userMode = this.radProficiency.length - 1;
/*      */     }
/*  408 */     this.radProficiency[userMode].setSelection(true);
/*      */     
/*      */ 
/*      */ 
/*  412 */     Listener buttonListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  414 */         Control[] children = TableColumnSetupWindow.this.cCategories.getChildren();
/*  415 */         for (int i = 0; i < children.length; i++) {
/*  416 */           Control child = children[i];
/*  417 */           if ((child != event.widget) && ((child instanceof Button))) {
/*  418 */             Button btn = (Button)child;
/*  419 */             btn.setSelection(false);
/*      */           }
/*      */         }
/*      */         
/*  423 */         TableColumnSetupWindow.this.fillAvail();
/*      */       }
/*      */       
/*  426 */     };
/*  427 */     Label lblCat = new Label(cFilterArea, 0);
/*  428 */     Messages.setLanguageText(lblCat, "ColumnSetup.categories");
/*      */     
/*      */ 
/*  431 */     this.cCategories = new Composite(cFilterArea, 0);
/*  432 */     Utils.setLayout(this.cCategories, new org.eclipse.swt.layout.RowLayout());
/*      */     
/*  434 */     Button button = new Button(this.cCategories, 2);
/*  435 */     Messages.setLanguageText(button, "Categories.all");
/*  436 */     button.addListener(13, buttonListener);
/*  437 */     button.setSelection(true);
/*      */     
/*  439 */     for (String cat : this.listCats) {
/*  440 */       button = new Button(this.cCategories, 2);
/*  441 */       button.setData("cat", cat);
/*  442 */       if (MessageText.keyExists("ColumnCategory." + cat)) {
/*  443 */         button.setText(MessageText.getString("ColumnCategory." + cat));
/*      */       } else {
/*  445 */         button.setText(cat);
/*      */       }
/*  447 */       button.addListener(13, buttonListener);
/*      */     }
/*      */     
/*  450 */     if (this.listColumnsNoCat.size() > 0) {
/*  451 */       button = new Button(this.cCategories, 2);
/*  452 */       if (MessageText.keyExists("ColumnCategory.uncat")) {
/*  453 */         button.setText(MessageText.getString("ColumnCategory.uncat"));
/*      */       } else {
/*  455 */         button.setText("?");
/*      */       }
/*  457 */       button.setText("?");
/*  458 */       button.setData("cat", "uncat");
/*  459 */       button.addListener(13, buttonListener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  472 */     final ExpandItem expandItemFilters = new ExpandItem(expandFilters, 0);
/*  473 */     expandItemFilters.setText(MessageText.getString("ColumnSetup.filters"));
/*  474 */     expandItemFilters.setControl(cFilterArea);
/*  475 */     expandFilters.addListener(11, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  477 */         expandItemFilters.setHeight(cFilterArea.computeSize(expandFilters.getSize().x, -1).y + 3);
/*      */       }
/*      */       
/*      */ 
/*  481 */     });
/*  482 */     expandFilters.addListener(17, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  484 */         Utils.execSWTThreadLater(Constants.isLinux ? 250 : 0, new AERunnable() {
/*      */           public void runSupport() {
/*  486 */             TableColumnSetupWindow.this.shell.layout(true, true);
/*      */           }
/*      */         });
/*      */       }
/*  490 */     });
/*  491 */     expandFilters.addListener(18, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  493 */         Utils.execSWTThreadLater(Constants.isLinux ? 250 : 0, new AERunnable() {
/*      */           public void runSupport() {
/*  495 */             TableColumnSetupWindow.this.shell.layout(true, true);
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*  505 */     });
/*  506 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*      */     
/*  508 */     Button btnLeft = new Button(cResultButtonArea, 8);
/*  509 */     imageLoader.setButtonImage(btnLeft, "alignleft");
/*  510 */     btnLeft.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  512 */         TableColumnSetupWindow.this.alignChosen(1);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  518 */     });
/*  519 */     Button btnCentre = new Button(cResultButtonArea, 8);
/*  520 */     imageLoader.setButtonImage(btnCentre, "aligncentre");
/*  521 */     btnCentre.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  523 */         TableColumnSetupWindow.this.alignChosen(3);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  529 */     });
/*  530 */     Button btnRight = new Button(cResultButtonArea, 8);
/*  531 */     imageLoader.setButtonImage(btnRight, "alignright");
/*  532 */     btnRight.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  534 */         TableColumnSetupWindow.this.alignChosen(2);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  540 */     });
/*  541 */     Button btnUp = new Button(cResultButtonArea, 8);
/*  542 */     imageLoader.setButtonImage(btnUp, "up");
/*  543 */     btnUp.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  545 */         TableColumnSetupWindow.this.moveChosenUp();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  551 */     });
/*  552 */     Button btnDown = new Button(cResultButtonArea, 8);
/*  553 */     imageLoader.setButtonImage(btnDown, "down");
/*  554 */     btnDown.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  556 */         TableColumnSetupWindow.this.moveChosenDown();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  562 */     });
/*  563 */     Button btnDel = new Button(cResultButtonArea, 8);
/*  564 */     imageLoader.setButtonImage(btnDel, "delete2");
/*  565 */     btnDel.addSelectionListener(new SelectionListener() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  567 */         TableColumnSetupWindow.this.removeSelectedChosen();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  573 */     });
/*  574 */     this.tvChosen = createTVChosen();
/*      */     
/*  576 */     this.cTableChosen = new Composite(cResultArea, 0);
/*  577 */     gridLayout = new GridLayout();
/*  578 */     gridLayout.marginWidth = (gridLayout.marginHeight = 0);
/*  579 */     this.cTableChosen.setLayout(gridLayout);
/*      */     
/*  581 */     this.tvChosen.initialize(this.cTableChosen);
/*      */     
/*  583 */     this.columnsChosen = tcm.getAllTableColumnCoreAsArray(forDataSourceType, this.forTableID);
/*      */     
/*  585 */     Arrays.sort(this.columnsChosen, TableColumnManager.getTableColumnOrderComparator());
/*      */     
/*  587 */     this.columnsOriginalOrder = new TableColumnCore[this.columnsChosen.length];
/*  588 */     System.arraycopy(this.columnsChosen, 0, this.columnsOriginalOrder, 0, this.columnsChosen.length);
/*      */     
/*  590 */     int pos = 0;
/*  591 */     for (int i = 0; i < this.columnsChosen.length; i++) {
/*  592 */       boolean visible = this.columnsChosen[i].isVisible();
/*  593 */       this.mapNewVisibility.put(this.columnsChosen[i], Boolean.valueOf(visible));
/*  594 */       if (visible) {
/*  595 */         this.columnsChosen[i].setPositionNoShift(pos++);
/*  596 */         this.tvChosen.addDataSource(this.columnsChosen[i]);
/*      */       }
/*      */     }
/*  599 */     this.tvChosen.processDataSourceQueue();
/*      */     
/*      */ 
/*  602 */     Button btnReset = null;
/*  603 */     String[] defaultColumnNames = tcm.getDefaultColumnNames(this.forTableID);
/*  604 */     if (defaultColumnNames != null) {
/*  605 */       btnReset = new Button(cResultButtonArea, 8);
/*  606 */       Messages.setLanguageText(btnReset, "Button.reset");
/*  607 */       btnReset.addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  609 */           String[] defaultColumnNames = tcm.getDefaultColumnNames(TableColumnSetupWindow.this.forTableID);
/*  610 */           if (defaultColumnNames != null) {
/*  611 */             List<TableColumnCore> defaultColumns = new ArrayList();
/*  612 */             for (String name : defaultColumnNames) {
/*  613 */               TableColumnCore column = tcm.getTableColumnCore(TableColumnSetupWindow.this.forTableID, name);
/*  614 */               if (column != null) {
/*  615 */                 defaultColumns.add(column);
/*      */               }
/*      */             }
/*  618 */             if (defaultColumns.size() > 0) {
/*  619 */               for (TableColumnCore tc : TableColumnSetupWindow.this.mapNewVisibility.keySet()) {
/*  620 */                 TableColumnSetupWindow.this.mapNewVisibility.put(tc, Boolean.FALSE);
/*      */               }
/*  622 */               TableColumnSetupWindow.this.tvChosen.removeAllTableRows();
/*  623 */               TableColumnSetupWindow.this.columnsChosen = ((TableColumnCore[])defaultColumns.toArray(new TableColumnCore[0]));
/*  624 */               for (int i = 0; i < TableColumnSetupWindow.this.columnsChosen.length; i++) {
/*  625 */                 TableColumnSetupWindow.this.mapNewVisibility.put(TableColumnSetupWindow.this.columnsChosen[i], Boolean.TRUE);
/*  626 */                 TableColumnSetupWindow.this.columnsChosen[i].setPositionNoShift(i);
/*  627 */                 TableColumnSetupWindow.this.tvChosen.addDataSource(TableColumnSetupWindow.this.columnsChosen[i]);
/*      */               }
/*  629 */               TableColumnSetupWindow.this.doReset = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  636 */     final Button btnCancel = new Button(this.shell, 8);
/*  637 */     Messages.setLanguageText(btnCancel, "Button.cancel");
/*  638 */     btnCancel.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  640 */         TableColumnSetupWindow.this.shell.dispose();
/*      */       }
/*      */       
/*  643 */     });
/*  644 */     Button btnApply = new Button(cResultButtonArea, 8);
/*  645 */     Messages.setLanguageText(btnApply, "Button.apply");
/*  646 */     btnApply.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  648 */         TableColumnSetupWindow.this.apply();
/*  649 */         btnCancel.setEnabled(false);
/*      */       }
/*      */       
/*  652 */     });
/*  653 */     fd = new FormData();
/*  654 */     fd.left = new FormAttachment(0, 5);
/*  655 */     fd.right = new FormAttachment(100, -5);
/*      */     
/*      */ 
/*      */ 
/*  659 */     fd = new FormData();
/*  660 */     fd.top = new FormAttachment(topInfo, 5);
/*  661 */     fd.right = new FormAttachment(100, -3);
/*  662 */     fd.bottom = new FormAttachment(btnOk, -5);
/*  663 */     fd.width = 210;
/*  664 */     Utils.setLayoutData(cResultArea, fd);
/*      */     
/*  666 */     fd = new FormData();
/*  667 */     fd.top = new FormAttachment(0, 3);
/*  668 */     fd.left = new FormAttachment(0, 3);
/*  669 */     fd.right = new FormAttachment(100, -3);
/*  670 */     fd.bottom = new FormAttachment(cResultButtonArea, -3);
/*  671 */     Utils.setLayoutData(this.cTableChosen, fd);
/*      */     
/*  673 */     fd = new FormData();
/*  674 */     fd.bottom = new FormAttachment(100, 0);
/*  675 */     fd.left = new FormAttachment(this.cTableChosen, 0, 16777216);
/*  676 */     Utils.setLayoutData(cResultButtonArea, fd);
/*      */     
/*      */ 
/*      */ 
/*  680 */     fd = new FormData();
/*  681 */     fd.top = new FormAttachment(0, 3);
/*  682 */     fd.left = new FormAttachment(0, 3);
/*  683 */     Utils.setLayoutData(btnLeft, fd);
/*      */     
/*  685 */     fd = new FormData();
/*  686 */     fd.left = new FormAttachment(btnLeft, 3);
/*  687 */     fd.top = new FormAttachment(btnLeft, 0, 128);
/*  688 */     fd.bottom = new FormAttachment(btnLeft, 0, 1024);
/*  689 */     Utils.setLayoutData(btnCentre, fd);
/*      */     
/*  691 */     fd = new FormData();
/*  692 */     fd.left = new FormAttachment(btnCentre, 3);
/*  693 */     fd.top = new FormAttachment(btnLeft, 0, 128);
/*  694 */     fd.bottom = new FormAttachment(btnLeft, 0, 1024);
/*  695 */     Utils.setLayoutData(btnRight, fd);
/*      */     
/*      */ 
/*      */ 
/*  699 */     fd = new FormData();
/*  700 */     fd.left = new FormAttachment(0, 3);
/*  701 */     fd.top = new FormAttachment(btnLeft, 2);
/*  702 */     Utils.setLayoutData(btnUp, fd);
/*      */     
/*  704 */     fd = new FormData();
/*  705 */     fd.left = new FormAttachment(btnUp, 3);
/*  706 */     fd.top = new FormAttachment(btnUp, 0, 128);
/*  707 */     Utils.setLayoutData(btnDown, fd);
/*      */     
/*  709 */     fd = new FormData();
/*  710 */     fd.left = new FormAttachment(btnDown, 3);
/*  711 */     fd.top = new FormAttachment(btnUp, 0, 128);
/*  712 */     Utils.setLayoutData(btnDel, fd);
/*      */     
/*  714 */     if (btnReset != null)
/*      */     {
/*  716 */       fd = new FormData();
/*  717 */       fd.right = new FormAttachment(btnApply, -3);
/*  718 */       fd.bottom = new FormAttachment(btnApply, 0, 1024);
/*  719 */       Utils.setLayoutData(btnReset, fd);
/*      */     }
/*      */     
/*  722 */     fd = new FormData();
/*  723 */     fd.right = new FormAttachment(100, -3);
/*  724 */     fd.top = new FormAttachment(btnUp, 3, 1024);
/*      */     
/*  726 */     Utils.setLayoutData(btnApply, fd);
/*      */     
/*  728 */     fd = new FormData();
/*  729 */     fd.right = new FormAttachment(100, -8);
/*  730 */     fd.bottom = new FormAttachment(100, -3);
/*      */     
/*  732 */     Utils.setLayoutData(btnCancel, fd);
/*      */     
/*  734 */     fd = new FormData();
/*  735 */     fd.right = new FormAttachment(btnCancel, -3);
/*  736 */     fd.bottom = new FormAttachment(btnCancel, 0, 1024);
/*      */     
/*  738 */     Utils.setLayoutData(btnOk, fd);
/*      */     
/*      */ 
/*      */ 
/*  742 */     fd = new FormData();
/*  743 */     fd.top = new FormAttachment(topInfo, 5);
/*  744 */     fd.left = new FormAttachment(0, 3);
/*  745 */     fd.right = new FormAttachment(cResultArea, -3);
/*  746 */     fd.bottom = new FormAttachment(100, -3);
/*  747 */     Utils.setLayoutData(this.cPickArea, fd);
/*      */     
/*  749 */     fd = new FormData();
/*  750 */     fd.bottom = new FormAttachment(100, 0);
/*  751 */     fd.left = new FormAttachment(0, 0);
/*  752 */     fd.right = new FormAttachment(100, 0);
/*  753 */     Utils.setLayoutData(expandFilters, fd);
/*      */     
/*      */ 
/*      */ 
/*  757 */     fd = new FormData();
/*  758 */     fd.bottom = new FormAttachment(this.cCategories, 0, 16777216);
/*  759 */     fd.left = new FormAttachment(0, 5);
/*  760 */     Utils.setLayoutData(lblCat, fd);
/*      */     
/*  762 */     fd = new FormData();
/*      */     
/*  764 */     fd.bottom = new FormAttachment(this.radProficiency[0], 0, 16777216);
/*  765 */     fd.left = new FormAttachment(0, 0);
/*  766 */     Utils.setLayoutData(lblProficiency, fd);
/*      */     
/*  768 */     fd = new FormData();
/*  769 */     fd.top = new FormAttachment(cProficiency, 5);
/*  770 */     fd.left = new FormAttachment(lblCat, 5);
/*  771 */     fd.right = new FormAttachment(100, 0);
/*  772 */     Utils.setLayoutData(this.cCategories, fd);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  785 */     fd = new FormData();
/*  786 */     fd.top = new FormAttachment(0, 5);
/*  787 */     fd.left = new FormAttachment(0, 5);
/*  788 */     Utils.setLayoutData(cProficiency, fd);
/*      */     
/*  790 */     fd = new FormData();
/*  791 */     fd.top = new FormAttachment(0, 3);
/*  792 */     fd.left = new FormAttachment(0, 3);
/*  793 */     fd.right = new FormAttachment(100, -3);
/*  794 */     fd.bottom = new FormAttachment(expandFilters, -3);
/*  795 */     Utils.setLayoutData(this.cTableAvail, fd);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  801 */     this.shell.setTabList(new Control[] { this.cPickArea, cResultArea, btnOk, btnCancel });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  808 */     this.cPickArea.setTabList(new Control[] { this.cTableAvail });
/*      */     
/*      */ 
/*      */ 
/*  812 */     fillAvail();
/*      */     
/*  814 */     UIUpdaterSWT.getInstance().addUpdater(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fillAvail()
/*      */   {
/*  823 */     String selectedCat = null;
/*      */     
/*  825 */     Control[] children = this.cCategories.getChildren();
/*  826 */     for (int i = 0; i < children.length; i++) {
/*  827 */       Control child = children[i];
/*  828 */       if ((child instanceof Button)) {
/*  829 */         Button btn = (Button)child;
/*  830 */         if (btn.getSelection()) {
/*  831 */           selectedCat = (String)btn.getData("cat");
/*  832 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  840 */     if ((selectedCat != null) && (selectedCat.equals("all"))) {
/*  841 */       selectedCat = null;
/*      */     }
/*      */     
/*      */ 
/*  845 */     byte selectedProf = 0;
/*  846 */     for (byte i = 0; i < this.radProficiency.length; i = (byte)(i + 1)) {
/*  847 */       Button btn = this.radProficiency[i];
/*  848 */       if (btn.getSelection()) {
/*  849 */         selectedProf = i;
/*  850 */         break;
/*      */       }
/*      */     }
/*      */     
/*      */     String s;
/*      */     String s;
/*  856 */     if (selectedCat != null) {
/*  857 */       s = MessageText.getString("ColumnSetup.availcolumns.filteredby", new String[] { this.radProficiency[selectedProf].getText(), selectedCat });
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  862 */       s = MessageText.getString("ColumnSetup.availcolumns", new String[] { this.radProficiency[selectedProf].getText() });
/*      */     }
/*      */     
/*      */ 
/*  866 */     this.cPickArea.setText(s);
/*      */     
/*  868 */     this.tvAvail.removeAllTableRows();
/*      */     
/*  870 */     TableColumnManager tcm = TableColumnManager.getInstance();
/*  871 */     TableColumnCore[] datasources = tcm.getAllTableColumnCoreAsArray(this.forDataSourceType, this.forTableID);
/*      */     
/*      */ 
/*  874 */     if (selectedCat == "uncat") {
/*  875 */       datasources = (TableColumnCore[])this.listColumnsNoCat.toArray(new TableColumnCore[this.listColumnsNoCat.size()]);
/*      */     }
/*  877 */     for (int i = 0; i < datasources.length; i++) {
/*  878 */       TableColumnCore column = datasources[i];
/*  879 */       TableColumnInfo info = tcm.getColumnInfo(this.forDataSourceType, this.forTableID, column.getName());
/*      */       
/*  881 */       String[] cats = info == null ? null : info.getCategories();
/*  882 */       if (cats == null) {
/*  883 */         if ((selectedCat == null) || (selectedCat.equals("uncat"))) {
/*  884 */           this.tvAvail.addDataSource(column);
/*      */         }
/*      */       } else {
/*  887 */         for (int j = 0; j < cats.length; j++) {
/*  888 */           String cat = cats[j];
/*  889 */           if (((selectedCat == null) || (selectedCat.equalsIgnoreCase(cat))) && (info.getProficiency() <= selectedProf))
/*      */           {
/*  891 */             this.tvAvail.addDataSource(column);
/*  892 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  897 */     this.tvAvail.processDataSourceQueue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void removeSelectedChosen()
/*      */   {
/*  906 */     Object[] datasources = this.tvChosen.getSelectedDataSources().toArray();
/*  907 */     for (int i = 0; i < datasources.length; i++) {
/*  908 */       TableColumnCore column = (TableColumnCore)datasources[i];
/*  909 */       this.mapNewVisibility.put(column, Boolean.FALSE);
/*      */     }
/*  911 */     this.tvChosen.removeDataSources(datasources);
/*  912 */     this.tvChosen.processDataSourceQueue();
/*  913 */     for (int i = 0; i < datasources.length; i++) {
/*  914 */       TableRowSWT row = (TableRowSWT)this.tvAvail.getRow((TableColumn)datasources[i]);
/*  915 */       if (row != null) {
/*  916 */         row.redraw();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void moveChosenDown()
/*      */   {
/*  927 */     TableRowCore[] selectedRows = this.tvChosen.getSelectedRows();
/*  928 */     TableRowCore[] rows = this.tvChosen.getRows();
/*  929 */     for (int i = selectedRows.length - 1; i >= 0; i--) {
/*  930 */       TableRowCore row = selectedRows[i];
/*  931 */       TableColumnCore column = (TableColumnCore)row.getDataSource();
/*  932 */       if (column != null) {
/*  933 */         int oldColumnPos = column.getPosition();
/*  934 */         int oldRowPos = row.getIndex();
/*  935 */         if (oldRowPos < rows.length - 1) {
/*  936 */           TableRowCore displacedRow = rows[(oldRowPos + 1)];
/*  937 */           ((TableColumnCore)displacedRow.getDataSource()).setPositionNoShift(oldColumnPos);
/*  938 */           rows[(oldRowPos + 1)] = rows[oldRowPos];
/*  939 */           rows[oldRowPos] = displacedRow;
/*  940 */           column.setPositionNoShift(oldColumnPos + 1);
/*      */         }
/*      */       }
/*      */     }
/*  944 */     this.tvChosen.tableInvalidate();
/*  945 */     this.tvChosen.refreshTable(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void moveChosenUp()
/*      */   {
/*  954 */     TableRowCore[] selectedRows = this.tvChosen.getSelectedRows();
/*  955 */     TableRowCore[] rows = this.tvChosen.getRows();
/*  956 */     for (int i = 0; i < selectedRows.length; i++) {
/*  957 */       TableRowCore row = selectedRows[i];
/*  958 */       TableColumnCore column = (TableColumnCore)row.getDataSource();
/*  959 */       if (column != null) {
/*  960 */         int oldColumnPos = column.getPosition();
/*  961 */         int oldRowPos = row.getIndex();
/*  962 */         if (oldRowPos > 0) {
/*  963 */           TableRowCore displacedRow = rows[(oldRowPos - 1)];
/*  964 */           ((TableColumnCore)displacedRow.getDataSource()).setPositionNoShift(oldColumnPos);
/*  965 */           rows[(oldRowPos - 1)] = rows[oldRowPos];
/*  966 */           rows[oldRowPos] = displacedRow;
/*  967 */           column.setPositionNoShift(oldColumnPos - 1);
/*      */           
/*  969 */           column.setAlignment(3);
/*      */         }
/*      */       }
/*      */     }
/*  973 */     this.tvChosen.tableInvalidate();
/*  974 */     this.tvChosen.refreshTable(true);
/*      */   }
/*      */   
/*      */   protected void alignChosen(int align) {
/*  978 */     TableRowCore[] selectedRows = this.tvChosen.getSelectedRows();
/*  979 */     for (int i = 0; i < selectedRows.length; i++) {
/*  980 */       TableRowCore row = selectedRows[i];
/*  981 */       TableColumnCore column = (TableColumnCore)row.getDataSource();
/*  982 */       if (column != null) {
/*  983 */         column.setAlignment(align);
/*      */       }
/*      */     }
/*  986 */     this.tvChosen.tableInvalidate();
/*  987 */     this.tvChosen.refreshTable(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void apply()
/*      */   {
/*  997 */     TableColumnManager tcm = TableColumnManager.getInstance();
/*  998 */     if (this.doReset) {
/*  999 */       TableColumnCore[] allTableColumns = tcm.getAllTableColumnCoreAsArray(this.forDataSourceType, this.forTableID);
/*      */       
/* 1001 */       if (allTableColumns != null) {
/* 1002 */         for (TableColumnCore column : allTableColumns) {
/* 1003 */           if (column != null) {
/* 1004 */             column.reset();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1010 */     for (TableColumnCore tc : this.mapNewVisibility.keySet()) {
/* 1011 */       boolean visible = ((Boolean)this.mapNewVisibility.get(tc)).booleanValue();
/* 1012 */       tc.setVisible(visible);
/*      */     }
/*      */     
/* 1015 */     tcm.saveTableColumns(this.forDataSourceType, this.forTableID);
/* 1016 */     this.listener.tableStructureChanged(true, this.forDataSourceType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private TableViewSWT<?> createTVChosen()
/*      */   {
/* 1025 */     TableColumnManager tcm = TableColumnManager.getInstance();
/* 1026 */     TableColumnCore[] columnTVChosen = tcm.getAllTableColumnCoreAsArray(TableColumn.class, "ColumnSetupChosen");
/*      */     
/* 1028 */     for (int i = 0; i < columnTVChosen.length; i++) {
/* 1029 */       TableColumnCore column = columnTVChosen[i];
/* 1030 */       if (column.getName().equals("TableColumnChosenColumn")) {
/* 1031 */         column.setVisible(true);
/* 1032 */         column.setWidth(175);
/* 1033 */         column.setSortAscending(true);
/*      */       } else {
/* 1035 */         column.setVisible(false);
/*      */       }
/*      */     }
/*      */     
/* 1039 */     final TableViewSWT<?> tvChosen = TableViewFactory.createTableViewSWT(TableColumn.class, "ColumnSetupChosen", "ColumnSetupChosen", columnTVChosen, "TableColumnChosenColumn", 268500994);
/*      */     
/*      */ 
/*      */ 
/* 1043 */     this.tvAvail.setParentDataSource(this);
/* 1044 */     tvChosen.setMenuEnabled(false);
/* 1045 */     tvChosen.setHeaderVisible(false);
/*      */     
/*      */ 
/* 1048 */     tvChosen.addLifeCycleListener(new TableLifeCycleListener() {
/*      */       private DragSource dragSource;
/*      */       private DropTarget dropTarget;
/*      */       
/*      */       public void tableViewInitialized() {
/* 1053 */         this.dragSource = tvChosen.createDragSource(7);
/*      */         
/* 1055 */         this.dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
/*      */         
/*      */ 
/* 1058 */         this.dragSource.setData("tv", tvChosen);
/* 1059 */         this.dragSource.addDragListener(TableColumnSetupWindow.this.dragSourceListener);
/*      */         
/* 1061 */         this.dropTarget = tvChosen.createDropTarget(31);
/*      */         
/*      */ 
/* 1064 */         this.dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
/*      */         
/*      */ 
/* 1067 */         this.dropTarget.addDropListener(new DropTargetListener()
/*      */         {
/*      */           public void dropAccept(DropTargetEvent event) {}
/*      */           
/*      */ 
/*      */ 
/*      */           public void drop(DropTargetEvent event)
/*      */           {
/* 1075 */             String id = (String)event.data;
/* 1076 */             TableRowCore destRow = TableColumnSetupWindow.20.this.val$tvChosen.getRow(event);
/*      */             
/* 1078 */             TableView<?> tv = id.equals("c") ? TableColumnSetupWindow.20.this.val$tvChosen : TableColumnSetupWindow.this.tvAvail;
/*      */             
/* 1080 */             Object[] dataSources = tv.getSelectedDataSources().toArray();
/* 1081 */             for (int i = 0; i < dataSources.length; i++) {
/* 1082 */               TableColumnCore column = (TableColumnCore)dataSources[i];
/* 1083 */               if (column != null) {
/* 1084 */                 TableColumnSetupWindow.this.chooseColumn(column, destRow, true);
/* 1085 */                 TableRowCore row = TableColumnSetupWindow.this.tvAvail.getRow(column);
/* 1086 */                 if (row != null) {
/* 1087 */                   row.redraw();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void dragOver(DropTargetEvent event) {}
/*      */           
/*      */ 
/*      */ 
/*      */           public void dragOperationChanged(DropTargetEvent event) {}
/*      */           
/*      */ 
/*      */ 
/*      */           public void dragLeave(DropTargetEvent event) {}
/*      */           
/*      */ 
/*      */ 
/*      */           public void dragEnter(DropTargetEvent event) {}
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void tableViewDestroyed()
/*      */       {
/* 1116 */         if ((this.dragSource != null) && (!this.dragSource.isDisposed())) {
/* 1117 */           this.dragSource.dispose();
/*      */         }
/* 1119 */         if ((this.dropTarget != null) && (!this.dropTarget.isDisposed())) {
/* 1120 */           this.dropTarget.dispose();
/*      */         }
/*      */         
/*      */       }
/* 1124 */     });
/* 1125 */     tvChosen.addKeyListener(new KeyListener()
/*      */     {
/*      */       public void keyReleased(KeyEvent e) {}
/*      */       
/*      */       public void keyPressed(KeyEvent e) {
/* 1130 */         if ((e.stateMask == 0) && ((e.keyCode == 16777219) || (e.keyCode == 127)))
/*      */         {
/* 1132 */           TableColumnSetupWindow.this.removeSelectedChosen();
/* 1133 */           e.doit = false;
/*      */         }
/*      */         
/* 1136 */         if (e.stateMask == 262144) {
/* 1137 */           if (e.keyCode == 16777217) {
/* 1138 */             TableColumnSetupWindow.this.moveChosenUp();
/* 1139 */             e.doit = false;
/* 1140 */           } else if (e.keyCode == 16777218) {
/* 1141 */             TableColumnSetupWindow.this.moveChosenDown();
/* 1142 */             e.doit = false;
/*      */           }
/*      */         }
/*      */       }
/* 1146 */     });
/* 1147 */     return tvChosen;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private TableViewSWT<TableColumn> createTVAvail()
/*      */   {
/* 1156 */     TableColumnManager tcm = TableColumnManager.getInstance();
/* 1157 */     Map<String, TableColumnCore> mapColumns = tcm.getTableColumnsAsMap(TableColumn.class, "ColumnSetupAvail");
/*      */     
/*      */ 
/* 1160 */     int[] widths = { 405, 105 };
/* 1161 */     TableColumnCore[] columns; if (this.sampleRow == null) {
/* 1162 */       TableColumnCore[] columns = { (TableColumnCore)mapColumns.get("TableColumnNameInfo") };
/*      */       
/*      */ 
/* 1165 */       widths = new int[] { 510 };
/*      */     } else {
/* 1167 */       columns = new TableColumnCore[] { (TableColumnCore)mapColumns.get("TableColumnNameInfo"), (TableColumnCore)mapColumns.get("TableColumnSample") };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1172 */     for (int i = 0; i < columns.length; i++) {
/* 1173 */       TableColumnCore column = columns[i];
/* 1174 */       if (column != null) {
/* 1175 */         column.setVisible(true);
/* 1176 */         column.setPositionNoShift(i);
/* 1177 */         column.setWidth(widths[i]);
/*      */       }
/*      */     }
/*      */     
/* 1181 */     final TableViewSWT<TableColumn> tvAvail = TableViewFactory.createTableViewSWT(TableColumn.class, "ColumnSetupAvail", "ColumnSetupAvail", columns, "TableColumnNameInfo", 268500996);
/*      */     
/*      */ 
/*      */ 
/* 1185 */     tvAvail.setParentDataSource(this);
/* 1186 */     tvAvail.setMenuEnabled(false);
/*      */     
/* 1188 */     tvAvail.setRowDefaultHeightEM(5.0F);
/*      */     
/* 1190 */     tvAvail.addLifeCycleListener(new TableLifeCycleListener() {
/*      */       private DragSource dragSource;
/*      */       private DropTarget dropTarget;
/*      */       
/*      */       public void tableViewInitialized() {
/* 1195 */         this.dragSource = tvAvail.createDragSource(7);
/*      */         
/* 1197 */         this.dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
/*      */         
/*      */ 
/* 1200 */         this.dragSource.setData("tv", tvAvail);
/* 1201 */         this.dragSource.addDragListener(TableColumnSetupWindow.this.dragSourceListener);
/*      */         
/*      */ 
/* 1204 */         this.dropTarget = tvAvail.createDropTarget(31);
/*      */         
/*      */ 
/* 1207 */         this.dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
/*      */         
/*      */ 
/* 1210 */         this.dropTarget.addDropListener(new DropTargetAdapter() {
/*      */           public void drop(DropTargetEvent event) {
/* 1212 */             String id = (String)event.data;
/*      */             
/* 1214 */             if (!id.equals("c")) {
/* 1215 */               return;
/*      */             }
/*      */             
/* 1218 */             TableColumnSetupWindow.this.removeSelectedChosen();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */       public void tableViewDestroyed()
/*      */       {
/* 1225 */         if ((this.dragSource != null) && (!this.dragSource.isDisposed())) {
/* 1226 */           this.dragSource.dispose();
/*      */         }
/* 1228 */         if ((this.dropTarget != null) && (!this.dropTarget.isDisposed())) {
/* 1229 */           this.dropTarget.dispose();
/*      */         }
/*      */         
/*      */       }
/* 1233 */     });
/* 1234 */     tvAvail.addSelectionListener(new TableSelectionAdapter() {
/*      */       public void defaultSelected(TableRowCore[] rows, int stateMask) {
/* 1236 */         for (int i = 0; i < rows.length; i++) {
/* 1237 */           TableRowCore row = rows[i];
/* 1238 */           TableColumnCore column = (TableColumnCore)row.getDataSource();
/* 1239 */           TableColumnSetupWindow.this.chooseColumn(column, null, false); } } }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1244 */     tvAvail.addKeyListener(new KeyListener()
/*      */     {
/*      */       public void keyReleased(KeyEvent e) {}
/*      */       
/*      */       public void keyPressed(KeyEvent e) {
/* 1249 */         if (e.stateMask == 0) {
/* 1250 */           if (e.keyCode == 16777220) {
/* 1251 */             TableRowCore[] selectedRows = tvAvail.getSelectedRows();
/* 1252 */             for (int i = 0; i < selectedRows.length; i++) {
/* 1253 */               TableRowCore row = selectedRows[i];
/* 1254 */               TableColumnCore column = (TableColumnCore)row.getDataSource();
/* 1255 */               TableColumnSetupWindow.this.chooseColumn(column, null, false);
/* 1256 */               TableColumnSetupWindow.this.tvChosen.processDataSourceQueue();
/* 1257 */               row.redraw();
/*      */             }
/* 1259 */             e.doit = false;
/* 1260 */           } else if (e.keyCode == 16777219) {
/* 1261 */             TableRowCore[] selectedRows = tvAvail.getSelectedRows();
/* 1262 */             for (int i = 0; i < selectedRows.length; i++) {
/* 1263 */               TableRowCore row = selectedRows[i];
/* 1264 */               TableColumnCore column = (TableColumnCore)row.getDataSource();
/* 1265 */               TableColumnSetupWindow.this.mapNewVisibility.put(column, Boolean.FALSE);
/* 1266 */               TableColumnSetupWindow.this.tvChosen.removeDataSource(column);
/* 1267 */               TableColumnSetupWindow.this.tvChosen.processDataSourceQueue();
/* 1268 */               row.redraw();
/*      */             }
/* 1270 */             e.doit = false;
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 1275 */     });
/* 1276 */     return tvAvail;
/*      */   }
/*      */   
/*      */   public void open() {
/* 1280 */     this.shell.open();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUpdateUIName()
/*      */   {
/* 1286 */     return null;
/*      */   }
/*      */   
/*      */   public void updateUI()
/*      */   {
/* 1291 */     if (this.shell.isDisposed()) {
/* 1292 */       UIUpdaterSWT.getInstance().removeUpdater(this);
/* 1293 */       return;
/*      */     }
/* 1295 */     if ((this.tvAvail != null) && (!this.tvAvail.isDisposed())) {
/* 1296 */       this.tvAvail.refreshTable(false);
/*      */     }
/* 1298 */     if ((this.tvChosen != null) && (!this.tvChosen.isDisposed())) {
/* 1299 */       this.tvChosen.refreshTable(false);
/*      */     }
/*      */   }
/*      */   
/*      */   public TableRow getSampleRow() {
/* 1304 */     return this.sampleRow;
/*      */   }
/*      */   
/*      */   public void chooseColumn(TableColumnCore column) {
/* 1308 */     chooseColumn(column, null, false);
/* 1309 */     TableRowCore row = this.tvAvail.getRow(column);
/* 1310 */     if (row != null) {
/* 1311 */       row.redraw();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isColumnAdded(TableColumnCore column) {
/* 1316 */     if (this.tvChosen == null) {
/* 1317 */       return false;
/*      */     }
/* 1319 */     TableRowCore row = this.tvChosen.getRow(column);
/* 1320 */     return row != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void chooseColumn(final TableColumnCore column, TableRowCore placeAboveRow, boolean ignoreExisting)
/*      */   {
/* 1330 */     TableRowCore row = this.tvChosen.getRow(column);
/*      */     
/* 1332 */     if ((row == null) || (ignoreExisting)) {
/* 1333 */       int newPosition = 0;
/*      */       
/* 1335 */       row = (placeAboveRow == null) && (!ignoreExisting) ? this.tvChosen.getFocusedRow() : placeAboveRow;
/*      */       
/* 1337 */       if ((row == null) || (row.getDataSource() == null)) {
/* 1338 */         if (this.columnsChosen.length > 0) {
/* 1339 */           newPosition = this.columnsChosen.length;
/*      */         }
/*      */       } else {
/* 1342 */         newPosition = ((TableColumn)row.getDataSource()).getPosition();
/*      */       }
/*      */       
/* 1345 */       int oldPosition = column.getPosition();
/* 1346 */       final boolean shiftDir = (oldPosition > newPosition) || (!((Boolean)this.mapNewVisibility.get(column)).booleanValue());
/*      */       
/* 1348 */       column.setPositionNoShift(newPosition);
/* 1349 */       this.mapNewVisibility.put(column, Boolean.TRUE);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1356 */       for (int i = 0; i < 10; i++) {
/*      */         try {
/* 1358 */           Arrays.sort(this.columnsChosen, new Comparator() {
/*      */             public int compare(Object arg0, Object arg1) {
/* 1360 */               if (((arg1 instanceof TableColumn)) && ((arg0 instanceof TableColumn))) {
/* 1361 */                 int iPositionA = ((TableColumn)arg0).getPosition();
/* 1362 */                 if (iPositionA < 0)
/* 1363 */                   iPositionA = 65535 + iPositionA;
/* 1364 */                 int iPositionB = ((TableColumn)arg1).getPosition();
/* 1365 */                 if (iPositionB < 0) {
/* 1366 */                   iPositionB = 65535 + iPositionB;
/*      */                 }
/* 1368 */                 int i = iPositionA - iPositionB;
/* 1369 */                 if (i == 0) {
/* 1370 */                   if (column == arg0) {
/* 1371 */                     return shiftDir ? -1 : 1;
/*      */                   }
/* 1373 */                   return shiftDir ? 1 : -1;
/*      */                 }
/* 1375 */                 return i;
/*      */               }
/* 1377 */               return 0;
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1387 */       int pos = 0;
/* 1388 */       for (int i = 0; i < this.columnsChosen.length; i++) {
/* 1389 */         if (((Boolean)this.mapNewVisibility.get(this.columnsChosen[i])).booleanValue()) {
/* 1390 */           this.columnsChosen[i].setPositionNoShift(pos++);
/*      */         }
/*      */       }
/*      */       
/* 1394 */       TableRowCore existingRow = this.tvChosen.getRow(column);
/* 1395 */       if (existingRow == null) {
/* 1396 */         this.tvChosen.addDataSource(column);
/* 1397 */         this.tvChosen.processDataSourceQueue();
/* 1398 */         this.tvChosen.addCountChangeListener(new TableCountChangeListener()
/*      */         {
/*      */           public void rowRemoved(TableRowCore row) {}
/*      */           
/*      */           public void rowAdded(final TableRowCore row)
/*      */           {
/* 1404 */             Utils.execSWTThreadLater(500, new AERunnable() {
/*      */               public void runSupport() {
/* 1406 */                 TableColumnSetupWindow.this.tvChosen.setSelectedRows(new TableRowCore[] { row });
/* 1407 */                 TableColumnSetupWindow.this.tvChosen.showRow(row);
/*      */               }
/* 1409 */             });
/* 1410 */             TableColumnSetupWindow.this.tvChosen.removeCountChangeListener(this);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1415 */       Arrays.sort(this.columnsChosen, TableColumnManager.getTableColumnOrderComparator());
/*      */       
/*      */ 
/* 1418 */       this.tvChosen.tableInvalidate();
/* 1419 */       this.tvChosen.refreshTable(true);
/*      */     }
/*      */     else {
/* 1422 */       row.setSelected(true);
/*      */     }
/*      */   }
/*      */   
/*      */   private void close() {
/* 1427 */     if (this.apply) {
/* 1428 */       apply();
/*      */     } else {
/* 1430 */       for (int i = 0; i < this.columnsOriginalOrder.length; i++) {
/* 1431 */         TableColumnCore column = this.columnsOriginalOrder[i];
/* 1432 */         if (column != null) {
/* 1433 */           column.setPositionNoShift(i);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/columnsetup/TableColumnSetupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */