/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.Map;
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
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Sash;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.ui.swt.DelayedListenerMultiCombiner;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_TabsCommon;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
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
/*     */ public class MyTorrentsSuperView
/*     */   implements UISWTViewCoreEventListener, AEDiagnosticsEvidenceGenerator, UIPluginViewToolBarListener
/*     */ {
/*  68 */   private static int SASH_WIDTH = 5;
/*     */   
/*     */ 
/*     */   private MyTorrentsView torrentview;
/*     */   
/*     */ 
/*     */   private MyTorrentsView seedingview;
/*     */   
/*     */ 
/*     */   private Composite form;
/*     */   
/*     */ 
/*     */   private MyTorrentsView lastSelectedView;
/*     */   
/*     */ 
/*     */   private Composite child1;
/*     */   
/*     */   private Composite child2;
/*     */   
/*     */   private final Text txtFilter;
/*     */   
/*     */   private final Composite cCats;
/*     */   
/*     */   private Object ds;
/*     */   
/*     */   private UISWTView swtView;
/*     */   
/*     */   private MyTorrentsView viewWhenDeactivated;
/*     */   
/*     */ 
/*     */   public MyTorrentsSuperView(Text txtFilter, Composite cCats)
/*     */   {
/* 100 */     this.txtFilter = txtFilter;
/* 101 */     this.cCats = cCats;
/* 102 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 104 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 106 */             TableColumnManager tcManager = TableColumnManager.getInstance();
/* 107 */             tcManager.addColumns(MyTorrentsSuperView.this.getCompleteColumns());
/* 108 */             tcManager.addColumns(MyTorrentsSuperView.this.getIncompleteColumns());
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public Composite getComposite()
/*     */   {
/* 117 */     return this.form;
/*     */   }
/*     */   
/*     */   public void initialize(final Composite parent) {
/* 121 */     if (this.form != null) {
/* 122 */       return;
/*     */     }
/*     */     
/* 125 */     this.form = new Composite(parent, 0);
/* 126 */     FormLayout flayout = new FormLayout();
/* 127 */     flayout.marginHeight = 0;
/* 128 */     flayout.marginWidth = 0;
/* 129 */     this.form.setLayout(flayout);
/*     */     
/* 131 */     GridData gridData = new GridData(1808);
/* 132 */     this.form.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 138 */     this.child1 = new Composite(this.form, 0);
/* 139 */     GridLayout layout = new GridLayout();
/* 140 */     layout.numColumns = 1;
/* 141 */     layout.horizontalSpacing = 0;
/* 142 */     layout.verticalSpacing = 0;
/* 143 */     layout.marginHeight = 0;
/* 144 */     layout.marginWidth = 0;
/* 145 */     this.child1.setLayout(layout);
/*     */     
/* 147 */     final Sash sash = Utils.createSash(this.form, SASH_WIDTH);
/*     */     
/* 149 */     this.child2 = new Composite(this.form, 0);
/* 150 */     layout = new GridLayout();
/* 151 */     layout.numColumns = 1;
/* 152 */     layout.horizontalSpacing = 0;
/* 153 */     layout.verticalSpacing = 0;
/* 154 */     layout.marginHeight = 0;
/* 155 */     layout.marginWidth = 0;
/* 156 */     this.child2.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 161 */     int weight = (int)COConfigurationManager.getFloatParameter("MyTorrents.SplitAt");
/* 162 */     if (weight > 10000) {
/* 163 */       weight = 10000;
/* 164 */     } else if (weight < 100) {
/* 165 */       weight *= 100;
/*     */     }
/*     */     
/* 168 */     if (weight < 500) {
/* 169 */       weight = 500;
/* 170 */     } else if (weight > 9000) {
/* 171 */       weight = 9000;
/*     */     }
/* 173 */     double pct = weight / 10000.0F;
/* 174 */     sash.setData("PCT", new Double(pct));
/*     */     
/*     */ 
/* 177 */     FormData formData = new FormData();
/* 178 */     formData.left = new FormAttachment(0, 0);
/* 179 */     formData.right = new FormAttachment(100, 0);
/* 180 */     formData.top = new FormAttachment(0, 0);
/* 181 */     formData.bottom = new FormAttachment((int)(pct * 100.0D), 0);
/* 182 */     this.child1.setLayoutData(formData);
/* 183 */     final FormData child1Data = formData;
/*     */     
/*     */ 
/* 186 */     formData = new FormData();
/* 187 */     formData.left = new FormAttachment(0, 0);
/* 188 */     formData.right = new FormAttachment(100, 0);
/* 189 */     formData.top = new FormAttachment(this.child1);
/* 190 */     formData.height = SASH_WIDTH;
/* 191 */     sash.setLayoutData(formData);
/*     */     
/*     */ 
/* 194 */     formData = new FormData();
/* 195 */     formData.left = new FormAttachment(0, 0);
/* 196 */     formData.right = new FormAttachment(100, 0);
/* 197 */     formData.bottom = new FormAttachment(100, 0);
/* 198 */     formData.top = new FormAttachment(sash);
/*     */     
/* 200 */     this.child2.setLayoutData(formData);
/*     */     
/*     */ 
/*     */ 
/* 204 */     sash.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 206 */         boolean FASTDRAG = true;
/*     */         
/* 208 */         if (e.detail == 1) {
/* 209 */           return;
/*     */         }
/* 211 */         child1Data.height = (e.y + e.height - MyTorrentsSuperView.SASH_WIDTH);
/* 212 */         MyTorrentsSuperView.this.form.layout();
/*     */         
/* 214 */         Double l = new Double(MyTorrentsSuperView.this.child1.getBounds().height / MyTorrentsSuperView.this.form.getBounds().height);
/*     */         
/* 216 */         sash.setData("PCT", l);
/* 217 */         if (e.detail != 1) {
/* 218 */           int i = (int)(l.doubleValue() * 10000.0D);
/* 219 */           COConfigurationManager.setParameter("MyTorrents.SplitAt", i);
/*     */         }
/*     */         
/*     */       }
/* 223 */     });
/* 224 */     this.form.addListener(11, new DelayedListenerMultiCombiner() {
/*     */       public void handleDelayedEvent(Event e) {
/* 226 */         if (sash.isDisposed()) {
/* 227 */           return;
/*     */         }
/* 229 */         Double l = (Double)sash.getData("PCT");
/* 230 */         if (l == null) {
/* 231 */           return;
/*     */         }
/* 233 */         int newHeight = (int)(MyTorrentsSuperView.this.form.getBounds().height * l.doubleValue());
/* 234 */         if ((child1Data.height != newHeight) || (child1Data.bottom != null)) {
/* 235 */           child1Data.bottom = null;
/* 236 */           child1Data.height = newHeight;
/* 237 */           MyTorrentsSuperView.this.form.layout();
/*     */         }
/*     */         
/*     */       }
/* 241 */     });
/* 242 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(final AzureusCore core) {
/* 244 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 246 */             MyTorrentsSuperView.this.initializeWithCore(core, MyTorrentsSuperView.4.this.val$parent);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void initializeWithCore(AzureusCore core, Composite parent)
/*     */   {
/* 256 */     this.torrentview = createTorrentView(core, "MyTorrents", false, getIncompleteColumns(), this.child1);
/*     */     
/*     */ 
/*     */ 
/* 260 */     this.seedingview = createTorrentView(core, "MySeeders", true, getCompleteColumns(), this.child2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 265 */     this.torrentview.getComposite().addListener(15, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 267 */         MyTorrentsSuperView.this.seedingview.getTableView().getTabsCommon().setTvOverride(MyTorrentsSuperView.this.torrentview.getTableView());
/*     */       }
/*     */       
/* 270 */     });
/* 271 */     this.seedingview.getComposite().addListener(15, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 273 */         MyTorrentsSuperView.this.seedingview.getTableView().getTabsCommon().setTvOverride(null);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 279 */     });
/* 280 */     SelectedContentManager.addCurrentlySelectedContentListener(new SelectedContentListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void currentlySelectedContentChanged(ISelectedContent[] currentContent, String viewId)
/*     */       {
/*     */ 
/*     */ 
/* 288 */         if ((MyTorrentsSuperView.this.form.isDisposed()) || (MyTorrentsSuperView.this.torrentview == null) || (MyTorrentsSuperView.this.seedingview == null))
/*     */         {
/* 290 */           SelectedContentManager.removeCurrentlySelectedContentListener(this);
/*     */         }
/*     */         else
/*     */         {
/* 294 */           TableView<?> selected_tv = SelectedContentManager.getCurrentlySelectedTableView();
/*     */           
/* 296 */           TableViewSWT<?> incomp_tv = MyTorrentsSuperView.this.torrentview.getTableView();
/* 297 */           TableViewSWT<?> comp_tv = MyTorrentsSuperView.this.seedingview.getTableView();
/*     */           
/* 299 */           if ((incomp_tv != null) && (comp_tv != null) && ((selected_tv == incomp_tv) || (selected_tv == comp_tv)))
/*     */           {
/* 301 */             TableViewSWT_TabsCommon tabs = comp_tv.getTabsCommon();
/*     */             
/* 303 */             if (tabs != null)
/*     */             {
/* 305 */               tabs.triggerTabViewsDataSourceChanged(selected_tv);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 311 */     });
/* 312 */     initializeDone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initializeDone() {}
/*     */   
/*     */ 
/*     */   public void updateLanguage()
/*     */   {
/* 322 */     if ((getComposite() == null) || (getComposite().isDisposed())) {
/* 323 */       return;
/*     */     }
/* 325 */     if (this.seedingview != null) {
/* 326 */       this.seedingview.updateLanguage();
/*     */     }
/* 328 */     if (this.torrentview != null) {
/* 329 */       this.torrentview.updateLanguage();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getFullTitle() {
/* 334 */     return MessageText.getString("MyTorrentsView.mytorrents");
/*     */   }
/*     */   
/*     */   private MyTorrentsView getCurrentView()
/*     */   {
/*     */     try
/*     */     {
/* 341 */       if ((this.torrentview != null) && (this.torrentview.isTableFocus())) {
/* 342 */         this.lastSelectedView = this.torrentview;
/* 343 */       } else if ((this.seedingview != null) && (this.seedingview.isTableFocus())) {
/* 344 */         this.lastSelectedView = this.seedingview;
/*     */       }
/*     */     }
/*     */     catch (Exception ignore) {}
/* 348 */     return this.lastSelectedView;
/*     */   }
/*     */   
/*     */   private UIPluginViewToolBarListener getActiveToolbarListener() {
/* 352 */     MyTorrentsView[] viewsToCheck = { getCurrentView(), this.torrentview, this.seedingview };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 357 */     for (int i = 0; i < viewsToCheck.length; i++) {
/* 358 */       MyTorrentsView view = viewsToCheck[i];
/* 359 */       if (view != null) {
/* 360 */         MdiEntrySWT activeSubView = view.getTableView().getTabsCommon().getActiveSubView();
/* 361 */         if (activeSubView != null) {
/* 362 */           UIPluginViewToolBarListener toolBarListener = activeSubView.getToolBarListener();
/* 363 */           if (toolBarListener != null) {
/* 364 */             return toolBarListener;
/*     */           }
/*     */         }
/* 367 */         if ((i == 0) && (view.isTableFocus())) {
/* 368 */           return view;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 373 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 380 */     UIPluginViewToolBarListener currentView = getActiveToolbarListener();
/* 381 */     if (currentView != null) {
/* 382 */       currentView.refreshToolBarItems(list);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 390 */     UIPluginViewToolBarListener currentView = getActiveToolbarListener();
/* 391 */     if ((currentView != null) && 
/* 392 */       (currentView.toolBarItemActivated(item, activationType, datasource))) {
/* 393 */       return true;
/*     */     }
/*     */     
/* 396 */     MyTorrentsView currentView2 = getCurrentView();
/* 397 */     if ((currentView2 != currentView) && (currentView2 != null) && 
/* 398 */       (currentView2.toolBarItemActivated(item, activationType, datasource))) {
/* 399 */       return true;
/*     */     }
/*     */     
/* 402 */     return false;
/*     */   }
/*     */   
/*     */   public DownloadManager[] getSelectedDownloads() {
/* 406 */     MyTorrentsView currentView = getCurrentView();
/* 407 */     if (currentView == null) return null;
/* 408 */     return currentView.getSelectedDownloads();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/*     */     try
/*     */     {
/* 417 */       writer.indent();
/*     */       
/* 419 */       writer.println("Downloading");
/*     */       
/* 421 */       writer.indent();
/*     */       
/* 423 */       this.torrentview.generate(writer);
/*     */     }
/*     */     finally
/*     */     {
/* 427 */       writer.exdent();
/*     */       
/* 429 */       writer.exdent();
/*     */     }
/*     */     try
/*     */     {
/* 433 */       writer.indent();
/*     */       
/* 435 */       writer.println("Seeding");
/*     */       
/* 437 */       writer.indent();
/*     */       
/* 439 */       this.seedingview.generate(writer);
/*     */     }
/*     */     finally
/*     */     {
/* 443 */       writer.exdent();
/*     */       
/* 445 */       writer.exdent();
/*     */     }
/*     */   }
/*     */   
/*     */   private Image obfusticatedImage(Image image) {
/* 450 */     if (this.torrentview != null) {
/* 451 */       this.torrentview.obfusticatedImage(image);
/*     */     }
/* 453 */     if (this.seedingview != null) {
/* 454 */       this.seedingview.obfusticatedImage(image);
/*     */     }
/* 456 */     return image;
/*     */   }
/*     */   
/*     */   public Menu getPrivateMenu() {
/* 460 */     return null;
/*     */   }
/*     */   
/*     */   public void viewActivated()
/*     */   {
/*     */     
/* 466 */     if (this.viewWhenDeactivated != null) {
/* 467 */       this.viewWhenDeactivated.getComposite().setFocus();
/* 468 */       this.viewWhenDeactivated.updateSelectedContent(true);
/*     */     } else {
/* 470 */       MyTorrentsView currentView = getCurrentView();
/* 471 */       if (currentView != null) {
/* 472 */         currentView.updateSelectedContent();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void viewDeactivated() {
/* 478 */     this.viewWhenDeactivated = getCurrentView();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TableColumnCore[] getIncompleteColumns()
/*     */   {
/* 502 */     return TableColumnCreator.createIncompleteDM("MyTorrents");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TableColumnCore[] getCompleteColumns()
/*     */   {
/* 511 */     return TableColumnCreator.createCompleteDM("MySeeders");
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
/*     */ 
/*     */ 
/*     */ 
/*     */   protected MyTorrentsView createTorrentView(AzureusCore _azureus_core, String tableID, boolean isSeedingView, TableColumnCore[] columns, Composite c)
/*     */   {
/* 532 */     MyTorrentsView view = new MyTorrentsView(_azureus_core, tableID, isSeedingView, columns, this.txtFilter, this.cCats, isSeedingView);
/*     */     
/*     */     try
/*     */     {
/* 536 */       UISWTViewImpl swtView = new UISWTViewImpl(tableID, "Main", false);
/* 537 */       swtView.setDatasource(this.ds);
/* 538 */       swtView.setEventListener(view, true);
/* 539 */       swtView.setDelayInitializeToFirstActivate(false);
/*     */       
/* 541 */       swtView.initialize(c);
/*     */     } catch (Exception e) {
/* 543 */       Debug.out(e);
/*     */     }
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
/* 558 */     c.layout();
/* 559 */     return view;
/*     */   }
/*     */   
/*     */   public MyTorrentsView getTorrentview()
/*     */   {
/* 564 */     return this.torrentview;
/*     */   }
/*     */   
/*     */   public MyTorrentsView getSeedingview()
/*     */   {
/* 569 */     return this.seedingview;
/*     */   }
/*     */   
/*     */   public void dataSourceChanged(Object newDataSource) {
/* 573 */     this.ds = newDataSource;
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 577 */     switch (event.getType()) {
/*     */     case 0: 
/* 579 */       this.swtView = ((UISWTView)event.getData());
/* 580 */       this.swtView.setToolBarListener(this);
/* 581 */       this.swtView.setTitle(getFullTitle());
/* 582 */       break;
/*     */     
/*     */     case 7: 
/*     */       break;
/*     */     
/*     */     case 2: 
/* 588 */       initialize((Composite)event.getData());
/* 589 */       return true;
/*     */     
/*     */     case 6: 
/* 592 */       this.swtView.setTitle(getFullTitle());
/* 593 */       Messages.updateLanguageForControl(getComposite());
/* 594 */       break;
/*     */     
/*     */     case 1: 
/* 597 */       dataSourceChanged(event.getData());
/* 598 */       break;
/*     */     
/*     */     case 5: 
/*     */       break;
/*     */     
/*     */     case 9: 
/* 604 */       Object data = event.getData();
/* 605 */       if ((data instanceof Map)) {
/* 606 */         obfusticatedImage((Image)MapUtils.getMapObject((Map)data, "image", null, Image.class));
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/* 612 */     if (this.seedingview != null) {
/*     */       try {
/* 614 */         this.seedingview.getSWTView().triggerEvent(event.getType(), event.getData());
/*     */       } catch (Exception e) {
/* 616 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 620 */     if (this.torrentview != null) {
/*     */       try {
/* 622 */         this.torrentview.getSWTView().triggerEvent(event.getType(), event.getData());
/*     */       } catch (Exception e) {
/* 624 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 631 */     switch (event.getType()) {
/*     */     case 3: 
/* 633 */       viewActivated();
/* 634 */       break;
/*     */     
/*     */     case 4: 
/* 637 */       viewDeactivated();
/*     */     }
/*     */     
/*     */     
/* 641 */     return true;
/*     */   }
/*     */   
/*     */   public UISWTView getSWTView() {
/* 645 */     return this.swtView;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/MyTorrentsSuperView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */