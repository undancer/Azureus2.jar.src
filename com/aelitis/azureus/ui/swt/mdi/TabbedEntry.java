/*     */ package com.aelitis.azureus.ui.swt.mdi;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.CTabFolder;
/*     */ import org.eclipse.swt.custom.CTabItem;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Item;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateTab;
/*     */ import org.gudy.azureus2.ui.swt.debug.UIDebugGenerator;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventCancelledException;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
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
/*     */ public class TabbedEntry
/*     */   extends BaseMdiEntry
/*     */   implements DisposeListener
/*     */ {
/*     */   private static final String SO_ID_ENTRY_WRAPPER = "mdi.content.item";
/*     */   private CTabItem swtItem;
/*     */   private SWTSkin skin;
/*     */   private boolean showonSWTItemSet;
/*     */   private boolean buildonSWTItemSet;
/*  74 */   private static long uniqueNumber = 0L;
/*     */   
/*     */   public TabbedEntry(TabbedMDI mdi, SWTSkin skin, String id, String parentViewID) {
/*  77 */     super(mdi, id, parentViewID);
/*  78 */     this.skin = skin;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canBuildStandAlone()
/*     */   {
/*  84 */     String skinRef = getSkinRef();
/*     */     
/*  86 */     if (skinRef != null)
/*     */     {
/*  88 */       return true;
/*     */     }
/*     */     
/*     */ 
/*  92 */     UISWTViewEventListener event_listener = getEventListener();
/*     */     
/*  94 */     if (((event_listener instanceof UISWTViewCoreEventListenerEx)) && (((UISWTViewCoreEventListenerEx)event_listener).isCloneable()))
/*     */     {
/*  96 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 100 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SWTSkinObjectContainer buildStandAlone(SWTSkinObjectContainer soParent)
/*     */   {
/* 107 */     Control control = null;
/*     */     
/*     */ 
/*     */ 
/* 111 */     Composite parent = soParent.getComposite();
/*     */     
/* 113 */     String skinRef = getSkinRef();
/*     */     
/* 115 */     if (skinRef != null)
/*     */     {
/* 117 */       Shell shell = parent.getShell();
/* 118 */       Cursor cursor = shell.getCursor();
/*     */       try {
/* 120 */         shell.setCursor(shell.getDisplay().getSystemCursor(1));
/*     */         
/*     */ 
/*     */ 
/* 124 */         SWTSkinObjectContainer soContents = (SWTSkinObjectContainer)this.skin.createSkinObject("MdiContents." + uniqueNumber++, "mdi.content.item", soParent, null);
/*     */         
/*     */ 
/*     */ 
/* 128 */         SWTSkinObject skinObject = this.skin.createSkinObject(this.id, skinRef, soContents, getDatasourceCore());
/*     */         
/*     */ 
/* 131 */         control = skinObject.getControl();
/* 132 */         control.setLayoutData(Utils.getFilledFormData());
/* 133 */         control.getParent().layout(true, true);
/*     */         
/* 135 */         soContents.setVisible(true);
/*     */         
/* 137 */         return soContents;
/*     */       }
/*     */       finally {
/* 140 */         shell.setCursor(cursor);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 145 */     UISWTViewEventListener event_listener = getEventListener();
/*     */     
/* 147 */     if (((event_listener instanceof UISWTViewCoreEventListenerEx)) && (((UISWTViewCoreEventListenerEx)event_listener).isCloneable()))
/*     */     {
/* 149 */       final UISWTViewImpl view = new UISWTViewImpl(getParentID(), this.id, true);
/*     */       try
/*     */       {
/* 152 */         view.setEventListener(((UISWTViewCoreEventListenerEx)event_listener).getClone(), false);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 156 */         Debug.out(e);
/*     */       }
/*     */       
/* 159 */       view.setDatasource(this.datasource);
/*     */       try
/*     */       {
/* 162 */         SWTSkinObjectContainer soContents = (SWTSkinObjectContainer)this.skin.createSkinObject("MdiIView." + uniqueNumber++, "mdi.content.item", soParent);
/*     */         
/*     */ 
/*     */ 
/* 166 */         parent.setBackgroundMode(0);
/*     */         
/* 168 */         final Composite viewComposite = soContents.getComposite();
/* 169 */         boolean doGridLayout = true;
/* 170 */         if (getControlType() == 257) {
/* 171 */           doGridLayout = false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 177 */         if (doGridLayout) {
/* 178 */           GridLayout gridLayout = new GridLayout();
/* 179 */           gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = gridLayout.marginHeight = gridLayout.marginWidth = 0);
/* 180 */           viewComposite.setLayout(gridLayout);
/* 181 */           viewComposite.setLayoutData(Utils.getFilledFormData());
/*     */         }
/*     */         
/* 184 */         view.setPluginSkinObject(soContents);
/* 185 */         view.initialize(viewComposite);
/*     */         
/*     */ 
/*     */ 
/* 189 */         Composite iviewComposite = view.getComposite();
/* 190 */         control = iviewComposite;
/*     */         
/*     */ 
/*     */ 
/* 194 */         if (doGridLayout) {
/* 195 */           Object existingLayoutData = iviewComposite.getLayoutData();
/* 196 */           Object existingParentLayoutData = iviewComposite.getParent().getLayoutData();
/* 197 */           if ((existingLayoutData == null) || ((!(existingLayoutData instanceof GridData)) && ((existingParentLayoutData instanceof GridLayout))))
/*     */           {
/*     */ 
/* 200 */             GridData gridData = new GridData(1808);
/* 201 */             iviewComposite.setLayoutData(gridData);
/*     */           }
/*     */         }
/*     */         
/* 205 */         parent.layout(true, true);
/*     */         
/* 207 */         final UIUpdater updater = UIFunctionsManager.getUIFunctions().getUIUpdater();
/*     */         
/* 209 */         updater.addUpdater(new UIUpdatable()
/*     */         {
/*     */           public void updateUI()
/*     */           {
/* 213 */             if (viewComposite.isDisposed()) {
/* 214 */               updater.removeUpdater(this);
/*     */             } else {
/* 216 */               view.triggerEvent(5, null);
/*     */             }
/*     */           }
/*     */           
/*     */           public String getUpdateUIName() {
/* 221 */             return "popout";
/*     */           }
/*     */           
/* 224 */         });
/* 225 */         soContents.setVisible(true);
/*     */         
/* 227 */         view.triggerEvent(3, null);
/*     */         
/* 229 */         return soContents;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 233 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 238 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void build()
/*     */   {
/* 247 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 249 */         TabbedEntry.this.swt_build();
/* 250 */         TabbedEntry.this.build();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean swt_build() {
/* 256 */     if (this.swtItem == null) {
/* 257 */       this.buildonSWTItemSet = true;
/* 258 */       return true;
/*     */     }
/* 260 */     this.buildonSWTItemSet = false;
/*     */     
/* 262 */     Control control = this.swtItem.getControl();
/* 263 */     if ((control == null) || (control.isDisposed())) {
/* 264 */       Composite parent = this.swtItem.getParent();
/* 265 */       SWTSkinObject soParent = (SWTSkinObject)parent.getData("SkinObject");
/*     */       
/* 267 */       String skinRef = getSkinRef();
/* 268 */       if (skinRef != null) {
/* 269 */         Shell shell = parent.getShell();
/* 270 */         Cursor cursor = shell.getCursor();
/*     */         try {
/* 272 */           shell.setCursor(shell.getDisplay().getSystemCursor(1));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 280 */           SWTSkinObject skinObject = this.skin.createSkinObject(this.id, skinRef, soParent, getDatasourceCore());
/*     */           
/*     */ 
/* 283 */           control = skinObject.getControl();
/* 284 */           control.setLayoutData(Utils.getFilledFormData());
/* 285 */           control.getParent().layout(true);
/*     */           
/*     */ 
/*     */ 
/* 289 */           CTabItem oldSelection = this.swtItem.getParent().getSelection();
/* 290 */           this.swtItem.getParent().setSelection(this.swtItem);
/* 291 */           this.swtItem.setControl(control);
/* 292 */           if (oldSelection != null) {
/* 293 */             this.swtItem.getParent().setSelection(oldSelection);
/*     */           }
/* 295 */           setPluginSkinObject(skinObject);
/* 296 */           setSkinObjectMaster(skinObject);
/*     */           
/*     */ 
/* 299 */           initialize((Composite)control);
/*     */         } finally {
/* 301 */           shell.setCursor(cursor);
/*     */         }
/*     */       }
/*     */       else {
/*     */         try {
/* 306 */           SWTSkinObjectContainer soContents = (SWTSkinObjectContainer)this.skin.createSkinObject("MdiIView." + uniqueNumber++, "mdi.content.item", soParent);
/*     */           
/*     */ 
/*     */ 
/* 310 */           parent.setBackgroundMode(0);
/*     */           
/* 312 */           Composite viewComposite = soContents.getComposite();
/*     */           
/*     */ 
/*     */ 
/* 316 */           boolean doGridLayout = true;
/* 317 */           if (getControlType() == 257) {
/* 318 */             doGridLayout = false;
/*     */           }
/* 320 */           if (doGridLayout) {
/* 321 */             GridLayout gridLayout = new GridLayout();
/* 322 */             gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = gridLayout.marginHeight = gridLayout.marginWidth = 0);
/* 323 */             viewComposite.setLayout(gridLayout);
/* 324 */             viewComposite.setLayoutData(Utils.getFilledFormData());
/*     */           }
/*     */           
/* 327 */           setPluginSkinObject(soContents);
/*     */           
/* 329 */           initialize(viewComposite);
/*     */           
/* 331 */           Composite iviewComposite = getComposite();
/* 332 */           control = iviewComposite;
/* 333 */           if (doGridLayout) {
/* 334 */             Object existingLayoutData = iviewComposite.getLayoutData();
/* 335 */             Object existingParentLayoutData = iviewComposite.getParent().getLayoutData();
/* 336 */             if ((existingLayoutData == null) || ((!(existingLayoutData instanceof GridData)) && ((existingParentLayoutData instanceof GridLayout))))
/*     */             {
/*     */ 
/* 339 */               GridData gridData = new GridData(1808);
/* 340 */               iviewComposite.setLayoutData(gridData);
/*     */             }
/*     */           }
/*     */           
/* 344 */           CTabItem oldSelection = this.swtItem.getParent().getSelection();
/* 345 */           this.swtItem.getParent().setSelection(this.swtItem);
/* 346 */           this.swtItem.setControl(viewComposite);
/* 347 */           if (oldSelection != null) {
/* 348 */             this.swtItem.getParent().setSelection(oldSelection);
/*     */           }
/* 350 */           setSkinObjectMaster(soContents);
/*     */         } catch (Exception e) {
/* 352 */           Debug.out("Error creating sidebar content area for " + this.id, e);
/*     */           try {
/* 354 */             setEventListener(null, false);
/*     */           }
/*     */           catch (UISWTViewEventCancelledException e1) {}
/* 357 */           close(true);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 362 */       if ((control != null) && (!control.isDisposed())) {
/* 363 */         control.setData("BaseMDIEntry", this);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 373 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 377 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/* 388 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*     */       public void runSupport() {
/* 390 */         TabbedEntry.this.swt_show();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void swt_show() {
/* 396 */     if (this.swtItem == null) {
/* 397 */       this.showonSWTItemSet = true;
/* 398 */       return;
/*     */     }
/* 400 */     this.showonSWTItemSet = false;
/* 401 */     if (!swt_build()) {
/* 402 */       return;
/*     */     }
/*     */     
/* 405 */     triggerOpenListeners();
/*     */     
/*     */ 
/* 408 */     if (this.swtItem.getParent().getSelection() != this.swtItem) {
/* 409 */       this.swtItem.getParent().setSelection(this.swtItem);
/*     */     }
/*     */     
/* 412 */     super.show();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MdiEntryVitalityImage addVitalityImage(String imageID)
/*     */   {
/* 419 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isCloseable()
/*     */   {
/* 424 */     return ((TabbedMDI)getMDI()).isMainMDI ? true : super.isCloseable();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCloseable(boolean closeable)
/*     */   {
/* 433 */     if (((TabbedMDI)getMDI()).isMainMDI) {
/* 434 */       closeable = true;
/*     */     }
/* 436 */     super.setCloseable(closeable);
/* 437 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 440 */         if ((TabbedEntry.this.swtItem == null) || (TabbedEntry.this.swtItem.isDisposed())) {
/* 441 */           return;
/*     */         }
/* 443 */         TabbedEntry.this.swtItem.setShowClose(TabbedEntry.this.isCloseable());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setSwtItem(CTabItem swtItem) {
/* 449 */     this.swtItem = swtItem;
/* 450 */     if (swtItem == null) {
/* 451 */       setDisposed(true);
/* 452 */       return;
/*     */     }
/* 454 */     setDisposed(false);
/*     */     
/* 456 */     swtItem.addDisposeListener(this);
/* 457 */     String title = getTitle();
/* 458 */     if (title != null) {
/* 459 */       swtItem.setText(escapeAccelerators(title));
/*     */     }
/*     */     
/* 462 */     updateLeftImage();
/*     */     
/* 464 */     swtItem.setShowClose(isCloseable());
/*     */     
/* 466 */     if (this.buildonSWTItemSet) {
/* 467 */       build();
/*     */     }
/* 469 */     if (this.showonSWTItemSet) {
/* 470 */       show();
/*     */     }
/*     */   }
/*     */   
/*     */   public Item getSwtItem() {
/* 475 */     return this.swtItem;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/* 482 */     super.setTitle(title);
/*     */     
/* 484 */     if (this.swtItem != null) {
/* 485 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 487 */           if ((TabbedEntry.this.swtItem == null) || (TabbedEntry.this.swtItem.isDisposed())) {
/* 488 */             return;
/*     */           }
/* 490 */           TabbedEntry.this.swtItem.setText(TabbedEntry.this.escapeAccelerators(TabbedEntry.this.getTitle()));
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MdiEntryVitalityImage[] getVitalityImages()
/*     */   {
/* 500 */     return new MdiEntryVitalityImage[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean close(boolean forceClose)
/*     */   {
/* 508 */     if (!super.close(forceClose)) {
/* 509 */       return false;
/*     */     }
/*     */     
/* 512 */     Utils.execSWTThread(new Runnable() {
/*     */       public void run() {
/* 514 */         if ((TabbedEntry.this.swtItem != null) && (!TabbedEntry.this.swtItem.isDisposed()))
/*     */         {
/* 516 */           TabbedEntry.this.swtItem.dispose();
/* 517 */           TabbedEntry.this.swtItem = null;
/*     */         }
/*     */       }
/* 520 */     });
/* 521 */     return true;
/*     */   }
/*     */   
/*     */   public void redraw() {
/* 525 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 528 */         if ((TabbedEntry.this.swtItem == null) || (TabbedEntry.this.swtItem.isDisposed())) {
/* 529 */           return;
/*     */         }
/*     */         
/* 532 */         TabbedEntry.this.swtItem.getParent().notifyListeners(11, new Event());
/*     */         
/* 534 */         TabbedEntry.this.swtItem.getParent().redraw();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setImageLeftID(String id)
/*     */   {
/* 541 */     super.setImageLeftID(id);
/* 542 */     updateLeftImage();
/*     */   }
/*     */   
/*     */   public void setImageLeft(Image imageLeft)
/*     */   {
/* 547 */     super.setImageLeft(imageLeft);
/* 548 */     updateLeftImage();
/*     */   }
/*     */   
/*     */   private void updateLeftImage() {
/* 552 */     if (this.swtItem == null) {
/* 553 */       return;
/*     */     }
/* 555 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 557 */         if ((TabbedEntry.this.swtItem == null) || (TabbedEntry.this.swtItem.isDisposed())) {
/* 558 */           return;
/*     */         }
/* 560 */         Image image = TabbedEntry.this.getImageLeft(null);
/* 561 */         TabbedEntry.this.swtItem.setImage(image);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void widgetDisposed(DisposeEvent e) {
/* 567 */     setSwtItem(null);
/*     */     
/* 569 */     triggerCloseListeners(!SWTThread.getInstance().isTerminated());
/*     */     try
/*     */     {
/* 572 */       setEventListener(null, false);
/*     */     }
/*     */     catch (UISWTViewEventCancelledException e1) {}
/*     */     
/* 576 */     SWTSkinObject so = getSkinObject();
/* 577 */     if (so != null) {
/* 578 */       setSkinObjectMaster(null);
/* 579 */       so.getSkin().removeSkinObject(so);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 585 */     Utils.execSWTThreadLater(0, new AERunnable()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 592 */         if (SWTThread.getInstance().isTerminated()) {
/* 593 */           return;
/*     */         }
/*     */         
/* 596 */         TabbedEntry.this.mdi.removeItem(TabbedEntry.this);
/* 597 */         TabbedEntry.this.mdi.removeEntryAutoOpen(TabbedEntry.this.id);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private String escapeAccelerators(String str) {
/* 603 */     if (str == null) {
/* 604 */       return str;
/*     */     }
/*     */     
/* 607 */     return str.replaceAll("&", "&&");
/*     */   }
/*     */   
/*     */   public void expandTo() {}
/*     */   
/*     */   public void viewTitleInfoRefresh(ViewTitleInfo titleInfoToRefresh)
/*     */   {
/* 614 */     super.viewTitleInfoRefresh(titleInfoToRefresh);
/*     */     
/* 616 */     if ((titleInfoToRefresh == null) || (this.viewTitleInfo != titleInfoToRefresh)) {
/* 617 */       return;
/*     */     }
/* 619 */     if (isDisposed()) {
/* 620 */       return;
/*     */     }
/*     */     
/* 623 */     String newText = (String)this.viewTitleInfo.getTitleInfoProperty(5);
/* 624 */     if (newText != null) {
/* 625 */       setTitle(newText);
/*     */     } else {
/* 627 */       String titleID = getTitleID();
/* 628 */       if (titleID != null) {
/* 629 */         setTitleID(titleID);
/*     */       }
/*     */     }
/* 632 */     redraw();
/*     */   }
/*     */   
/*     */   public boolean isSelectable()
/*     */   {
/* 637 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSelectable(boolean selectable) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(MdiSWTMenuHackListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(MdiSWTMenuHackListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void setParentID(String id) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String getParentID()
/*     */   {
/* 661 */     return null;
/*     */   }
/*     */   
/*     */   public Image obfusticatedImage(Image image)
/*     */   {
/* 666 */     Rectangle bounds = this.swtItem == null ? null : this.swtItem.getBounds();
/* 667 */     if (bounds != null)
/*     */     {
/* 669 */       boolean isActive = this.swtItem.getParent().getSelection() == this.swtItem;
/* 670 */       boolean isHeaderVisible = this.swtItem.isShowing();
/*     */       
/* 672 */       Point location = Utils.getLocationRelativeToShell(this.swtItem.getParent());
/*     */       
/* 674 */       bounds.x += location.x;
/* 675 */       bounds.y += location.y;
/*     */       
/* 677 */       Map<String, Object> map = new HashMap();
/* 678 */       map.put("image", image);
/* 679 */       map.put("obfuscateTitle", Boolean.valueOf(false));
/* 680 */       if (isActive) {
/* 681 */         triggerEvent(9, map);
/*     */         
/* 683 */         if ((this.viewTitleInfo instanceof ObfusticateImage)) {
/* 684 */           ((ObfusticateImage)this.viewTitleInfo).obfusticatedImage(image);
/*     */         }
/*     */       }
/*     */       
/* 688 */       if (isHeaderVisible) {
/* 689 */         if ((this.viewTitleInfo instanceof ObfusticateTab)) {
/* 690 */           String header = ((ObfusticateTab)this.viewTitleInfo).getObfusticatedHeader();
/* 691 */           if (header != null) {
/* 692 */             UIDebugGenerator.obfusticateArea(image, bounds, header);
/*     */           }
/*     */         }
/*     */         
/* 696 */         if (MapUtils.getMapBoolean(map, "obfuscateTitle", false)) {
/* 697 */           UIDebugGenerator.obfusticateArea(image, bounds);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 702 */     return image;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/mdi/TabbedEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */