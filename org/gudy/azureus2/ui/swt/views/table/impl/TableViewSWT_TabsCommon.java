/*     */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener2;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.TabbedMdiInterface;
/*     */ import com.aelitis.azureus.ui.swt.mdi.TabbedMdiMaximizeListener;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Sash;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance.UISWTViewEventListenerWrapper;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TableViewSWT_TabsCommon
/*     */   implements SelectedContentListener
/*     */ {
/*     */   UISWTView parentView;
/*     */   TableViewSWT<?> tv;
/*     */   public Composite tableComposite;
/*     */   private TableView<?> tvOverride;
/*     */   private Sash sash;
/*     */   private TabbedMdiInterface tabbedMDI;
/*     */   private Composite cTabsHolder;
/*     */   private FormData fdHeightChanger;
/*     */   private MenuItem menuItemShowTabs;
/*     */   private ISelectedContent[] selectedContent;
/*     */   
/*     */   public TableViewSWT_TabsCommon(UISWTView parentView, TableViewSWT<?> tv)
/*     */   {
/*  94 */     this.parentView = parentView;
/*  95 */     this.tv = tv;
/*     */   }
/*     */   
/*     */   public void triggerTabViewsDataSourceChanged(TableView<?> tv) {
/*  99 */     if ((this.tabbedMDI == null) || (this.tabbedMDI.isDisposed())) {
/* 100 */       return;
/*     */     }
/* 102 */     MdiEntry[] entries = this.tabbedMDI.getEntries();
/* 103 */     if ((entries == null) || (entries.length == 0)) {
/* 104 */       return;
/*     */     }
/*     */     
/* 107 */     Object[] ds = tv.getSelectedDataSources(true);
/*     */     
/* 109 */     for (MdiEntry entry : entries) {
/* 110 */       if ((entry instanceof MdiEntrySWT)) {
/* 111 */         triggerTabViewDataSourceChanged((MdiEntrySWT)entry, tv, ds);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTvOverride(TableView<?> tvOverride) {
/* 117 */     this.tvOverride = tvOverride;
/* 118 */     this.selectedContent = SelectedContentManager.getCurrentlySelectedContent();
/*     */   }
/*     */   
/*     */   public void triggerTabViewDataSourceChanged(MdiEntrySWT view, TableView<?> tv, Object[] dataSourcesCore)
/*     */   {
/* 123 */     if (this.tvOverride != null) {
/* 124 */       tv = this.tvOverride;
/*     */     }
/* 126 */     if (view == null) {
/* 127 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 133 */     if (dataSourcesCore == null) {
/* 134 */       dataSourcesCore = tv.getSelectedDataSources(true);
/*     */     }
/* 136 */     if (this.tabbedMDI != null) {
/* 137 */       this.tabbedMDI.setMaximizeVisible((dataSourcesCore != null) && (dataSourcesCore.length == 1));
/*     */     }
/* 139 */     view.triggerEvent(1, dataSourcesCore.length == 0 ? tv.getParentDataSource() : dataSourcesCore);
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 145 */     SelectedContentManager.removeCurrentlySelectedContentListener(this);
/*     */     
/* 147 */     if (this.menuItemShowTabs != null) {
/* 148 */       this.menuItemShowTabs.remove();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 167 */     writer.println("# of SubViews: " + (this.tabbedMDI == null ? "null" : Integer.valueOf(this.tabbedMDI.getEntriesCount())));
/*     */   }
/*     */   
/*     */   public void localeChanged()
/*     */   {
/* 172 */     if (this.tabbedMDI == null) {
/* 173 */       return;
/*     */     }
/* 175 */     MdiEntry[] entries = this.tabbedMDI.getEntries();
/* 176 */     if ((entries == null) || (entries.length == 0)) {
/* 177 */       return;
/*     */     }
/* 179 */     for (MdiEntry entry : entries) {
/* 180 */       if ((entry instanceof MdiEntrySWT)) {
/* 181 */         ((MdiEntrySWT)entry).triggerEvent(6, null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public MdiEntrySWT getActiveSubView() {
/* 187 */     if ((!this.tv.isTabViewsEnabled()) || (this.tabbedMDI == null) || (this.tabbedMDI.isDisposed()) || (this.tabbedMDI.getMinimized()))
/*     */     {
/* 189 */       return null;
/*     */     }
/*     */     
/* 192 */     return this.tabbedMDI.getCurrentEntrySWT();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private MdiEntry addTabView(UISWTInstance.UISWTViewEventListenerWrapper listener, String afterID)
/*     */   {
/* 199 */     UISWTViewCore view = null;
/* 200 */     MdiEntrySWT entry = (MdiEntrySWT)this.tabbedMDI.createEntryFromEventListener(this.tv.getTableID(), listener, listener.getViewID(), true, null, afterID);
/*     */     
/* 202 */     if ((entry instanceof UISWTViewCore)) {
/* 203 */       view = entry;
/*     */     }
/*     */     else {
/* 206 */       return entry;
/*     */     }
/*     */     try
/*     */     {
/* 210 */       if (this.parentView != null) {
/* 211 */         view.setParentView(this.parentView);
/*     */       }
/*     */       
/* 214 */       triggerTabViewDataSourceChanged(entry, this.tv, null);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 218 */       Debug.out(e);
/*     */     }
/*     */     
/* 221 */     return entry;
/*     */   }
/*     */   
/*     */   private void removeTabView(String id)
/*     */   {
/* 226 */     boolean exists = this.tabbedMDI.entryExists(id);
/* 227 */     if (!exists) {
/* 228 */       return;
/*     */     }
/* 230 */     MdiEntry entry = this.tabbedMDI.getEntry(id);
/*     */     
/*     */ 
/*     */ 
/* 234 */     this.tabbedMDI.removeItem(entry);
/*     */   }
/*     */   
/*     */   public Composite createSashForm(Composite composite) {
/* 238 */     if (!this.tv.isTabViewsEnabled()) {
/* 239 */       this.tableComposite = this.tv.createMainPanel(composite);
/* 240 */       return this.tableComposite;
/*     */     }
/*     */     
/* 243 */     SelectedContentManager.addCurrentlySelectedContentListener(this);
/*     */     
/* 245 */     ConfigurationManager configMan = ConfigurationManager.getInstance();
/*     */     
/* 247 */     int iNumViews = 0;
/*     */     
/* 249 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 250 */     if (uiFunctions != null) {
/* 251 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 253 */       if (pluginUI != null) {
/* 254 */         iNumViews += pluginUI.getViewListeners(this.tv.getTableID()).length;
/*     */       }
/*     */     }
/*     */     
/* 258 */     if (iNumViews == 0) {
/* 259 */       this.tableComposite = this.tv.createMainPanel(composite);
/* 260 */       return this.tableComposite;
/*     */     }
/*     */     
/* 263 */     final String props_prefix = this.tv.getTableID() + "." + this.tv.getPropertiesPrefix();
/*     */     
/*     */ 
/*     */ 
/* 267 */     final Composite form = new Composite(composite, 0);
/* 268 */     FormLayout flayout = new FormLayout();
/* 269 */     flayout.marginHeight = 0;
/* 270 */     flayout.marginWidth = 0;
/* 271 */     form.setLayout(flayout);
/*     */     
/* 273 */     GridData gridData = new GridData(1808);
/* 274 */     form.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 280 */     this.cTabsHolder = new Composite(form, 0);
/* 281 */     this.tabbedMDI = uiFunctions.createTabbedMDI(this.cTabsHolder, props_prefix);
/* 282 */     this.tabbedMDI.setMaximizeVisible(true);
/* 283 */     this.tabbedMDI.setMinimizeVisible(true);
/*     */     
/* 285 */     this.tabbedMDI.setTabbedMdiMaximizeListener(new TabbedMdiMaximizeListener() {
/*     */       public void maximizePressed() {
/* 287 */         TableView tvToUse = TableViewSWT_TabsCommon.this.tvOverride == null ? TableViewSWT_TabsCommon.this.tv : TableViewSWT_TabsCommon.this.tvOverride;
/* 288 */         Object[] ds = tvToUse.getSelectedDataSources(true);
/*     */         
/* 290 */         if ((ds.length == 1) && ((ds[0] instanceof DownloadManager)))
/*     */         {
/* 292 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */           
/* 294 */           if (uiFunctions != null)
/*     */           {
/* 296 */             uiFunctions.getMDI().showEntryByID("DMDetails", ds);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 303 */     });
/* 304 */     int SASH_WIDTH = 5;
/*     */     
/* 306 */     this.sash = Utils.createSash(form, 5);
/*     */     
/* 308 */     this.tableComposite = this.tv.createMainPanel(form);
/* 309 */     Composite cFixLayout = this.tableComposite;
/* 310 */     while ((cFixLayout != null) && (cFixLayout.getParent() != form)) {
/* 311 */       cFixLayout = cFixLayout.getParent();
/*     */     }
/* 313 */     if (cFixLayout == null) {
/* 314 */       cFixLayout = this.tableComposite;
/*     */     }
/* 316 */     GridLayout layout = new GridLayout();
/* 317 */     layout.numColumns = 1;
/* 318 */     layout.horizontalSpacing = 0;
/* 319 */     layout.verticalSpacing = 0;
/* 320 */     layout.marginHeight = 0;
/* 321 */     layout.marginWidth = 0;
/* 322 */     cFixLayout.setLayout(layout);
/*     */     
/*     */ 
/* 325 */     FormData formData = new FormData();
/* 326 */     formData.left = new FormAttachment(0, 0);
/* 327 */     formData.right = new FormAttachment(100, 0);
/* 328 */     formData.bottom = new FormAttachment(100, 0);
/* 329 */     int iSplitAt = configMan.getIntParameter(props_prefix + ".SplitAt", 3000);
/*     */     
/*     */ 
/* 332 */     if (iSplitAt < 100) {
/* 333 */       iSplitAt *= 100;
/*     */     }
/*     */     
/*     */ 
/* 337 */     double pct = iSplitAt / 10000.0D;
/* 338 */     if (pct < 0.03D) {
/* 339 */       pct = 0.03D;
/* 340 */     } else if (pct > 0.97D) {
/* 341 */       pct = 0.97D;
/*     */     }
/*     */     
/*     */ 
/* 345 */     this.sash.setData("PCT", new Double(pct));
/* 346 */     this.cTabsHolder.setLayout(new FormLayout());
/* 347 */     this.fdHeightChanger = formData;
/* 348 */     this.cTabsHolder.setLayoutData(formData);
/*     */     
/*     */ 
/* 351 */     formData = new FormData();
/* 352 */     formData.left = new FormAttachment(0, 0);
/* 353 */     formData.right = new FormAttachment(100, 0);
/* 354 */     formData.bottom = new FormAttachment(this.cTabsHolder);
/* 355 */     formData.height = 5;
/* 356 */     this.sash.setLayoutData(formData);
/*     */     
/*     */ 
/* 359 */     formData = new FormData();
/* 360 */     formData.left = new FormAttachment(0, 0);
/* 361 */     formData.right = new FormAttachment(100, 0);
/* 362 */     formData.top = new FormAttachment(0, 0);
/* 363 */     formData.bottom = new FormAttachment(this.sash);
/* 364 */     cFixLayout.setLayoutData(formData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 369 */     this.sash.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 371 */         boolean FASTDRAG = true;
/*     */         
/* 373 */         if (e.detail == 1) {
/* 374 */           return;
/*     */         }
/*     */         
/* 377 */         Rectangle area = form.getClientArea();
/*     */         
/* 379 */         int height = area.height - e.y - e.height;
/*     */         
/* 381 */         if (!Constants.isWindows) {
/* 382 */           height -= 5;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 388 */         if (area.height - height < 100) {
/* 389 */           height = area.height - 100;
/*     */         }
/*     */         
/* 392 */         if (height < 0) {
/* 393 */           height = 0;
/*     */         }
/*     */         
/* 396 */         TableViewSWT_TabsCommon.this.fdHeightChanger.height = height;
/*     */         
/* 398 */         Double l = new Double(height / area.height);
/* 399 */         TableViewSWT_TabsCommon.this.sash.setData("PCT", l);
/* 400 */         if (e.detail != 1) {
/* 401 */           ConfigurationManager configMan = ConfigurationManager.getInstance();
/* 402 */           configMan.setParameter(props_prefix + ".SplitAt", (int)(l.doubleValue() * 10000.0D));
/*     */         }
/*     */         
/* 405 */         form.layout();
/*     */         
/* 407 */         TableViewSWT_TabsCommon.this.cTabsHolder.redraw();
/*     */       }
/*     */       
/* 410 */     });
/* 411 */     buildFolder(form, props_prefix);
/*     */     
/* 413 */     return form;
/*     */   }
/*     */   
/*     */   private void buildFolder(final Composite form, String props_prefix) {
/* 417 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 418 */     UIManager uim = pi.getUIManager();
/* 419 */     MenuManager menuManager = uim.getMenuManager();
/*     */     
/*     */ 
/* 422 */     this.menuItemShowTabs = menuManager.addMenuItem(props_prefix + "._end_", "ConfigView.section.style.ShowTabsInTorrentView");
/*     */     
/* 424 */     this.menuItemShowTabs.setStyle(2);
/* 425 */     this.menuItemShowTabs.addFillListener(new MenuItemFillListener() {
/*     */       public void menuWillBeShown(MenuItem menu, Object data) {
/* 427 */         menu.setData(Boolean.valueOf(COConfigurationManager.getBooleanParameter("Library.ShowTabsInTorrentView")));
/*     */       }
/*     */       
/* 430 */     });
/* 431 */     this.menuItemShowTabs.addListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 433 */         COConfigurationManager.setParameter("Library.ShowTabsInTorrentView", ((Boolean)menu.getData()).booleanValue());
/*     */       }
/*     */       
/*     */ 
/* 437 */     });
/* 438 */     this.cTabsHolder.addListener(11, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 440 */         if (TableViewSWT_TabsCommon.this.tabbedMDI.getMinimized()) {
/* 441 */           TableViewSWT_TabsCommon.this.fdHeightChanger.height = TableViewSWT_TabsCommon.this.tabbedMDI.getFolderHeight();
/* 442 */           TableViewSWT_TabsCommon.this.cTabsHolder.getParent().layout();
/* 443 */           return;
/*     */         }
/*     */         
/* 446 */         Double l = (Double)TableViewSWT_TabsCommon.this.sash.getData("PCT");
/* 447 */         if (l != null) {
/* 448 */           TableViewSWT_TabsCommon.this.fdHeightChanger.height = ((int)(form.getBounds().height * l.doubleValue()));
/* 449 */           TableViewSWT_TabsCommon.this.cTabsHolder.getParent().layout();
/*     */         }
/*     */         
/*     */       }
/* 453 */     });
/* 454 */     String[] restricted_to = this.tv.getTabViewsRestrictedTo();
/*     */     
/* 456 */     Set<String> rt_set = new HashSet();
/*     */     
/* 458 */     if (restricted_to != null)
/*     */     {
/* 460 */       rt_set.addAll(Arrays.asList(restricted_to));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 465 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 466 */     if (uiFunctions != null)
/*     */     {
/* 468 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 470 */       if (pluginUI != null)
/*     */       {
/* 472 */         UISWTInstance.UISWTViewEventListenerWrapper[] pluginViews = pluginUI.getViewListeners(this.tv.getTableID());
/*     */         
/* 474 */         if (pluginViews != null) {
/* 475 */           for (final UISWTInstance.UISWTViewEventListenerWrapper l : pluginViews) {
/* 476 */             if (l != null)
/*     */             {
/*     */               try
/*     */               {
/* 480 */                 String view_id = l.getViewID();
/*     */                 
/* 482 */                 if ((restricted_to == null) || (rt_set.contains(view_id)))
/*     */                 {
/*     */ 
/*     */ 
/* 486 */                   this.tabbedMDI.registerEntry(view_id, new MdiEntryCreationListener2()
/*     */                   {
/*     */                     public MdiEntry createMDiEntry(MultipleDocumentInterface mdi, String id, Object datasource, Map<?, ?> params) {
/* 489 */                       return TableViewSWT_TabsCommon.this.addTabView(l, null);
/*     */                     }
/*     */                     
/* 492 */                   });
/* 493 */                   this.tabbedMDI.loadEntryByID(view_id, false);
/*     */                 }
/*     */               }
/*     */               catch (Exception e) {}
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 503 */     if (!this.tabbedMDI.getMinimized()) {
/* 504 */       MdiEntry[] entries = this.tabbedMDI.getEntries();
/* 505 */       if (entries.length > 0) {
/* 506 */         this.tabbedMDI.showEntry(entries[0]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void swt_refresh()
/*     */   {
/* 514 */     if ((this.tv.isTabViewsEnabled()) && (this.tabbedMDI != null) && (!this.tabbedMDI.isDisposed()) && (!this.tabbedMDI.getMinimized()))
/*     */     {
/*     */ 
/* 517 */       MdiEntry entry = this.tabbedMDI.getCurrentEntry();
/* 518 */       if (entry != null) {
/* 519 */         entry.updateUI();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void currentlySelectedContentChanged(ISelectedContent[] currentContent, String viewID)
/*     */   {
/* 527 */     TableView tvToUse = this.tvOverride == null ? this.tv : this.tvOverride;
/* 528 */     if ((viewID != null) && (viewID.equals(tvToUse.getTableID()))) {
/* 529 */       this.selectedContent = currentContent;
/*     */     }
/* 531 */     if ((currentContent.length == 0) && (this.tv.isVisible()) && (this.selectedContent != null) && (this.selectedContent.length != 0))
/*     */     {
/* 533 */       SelectedContentManager.changeCurrentlySelectedContent(tvToUse.getTableID(), this.selectedContent, tvToUse);
/*     */     }
/*     */   }
/*     */   
/*     */   public void obfusticatedImage(Image image)
/*     */   {
/* 539 */     if ((this.tabbedMDI instanceof ObfusticateImage)) {
/* 540 */       ObfusticateImage o = (ObfusticateImage)this.tabbedMDI;
/* 541 */       image = o.obfusticatedImage(image);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/TableViewSWT_TabsCommon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */