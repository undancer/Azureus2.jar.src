/*     */ package com.aelitis.azureus.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTInstanceImpl;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventListenerHolder;
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
/*     */ public class TopBarView
/*     */   extends SkinView
/*     */ {
/*  59 */   private static final Object view_name_key = new Object();
/*     */   
/*  61 */   private java.util.List<UISWTViewCore> topbarViews = new ArrayList();
/*     */   
/*     */   private UISWTViewCore activeTopBar;
/*     */   
/*     */   private SWTSkin skin;
/*     */   
/*     */   private org.eclipse.swt.widgets.List listPlugins;
/*     */   
/*     */   private Composite cPluginArea;
/*     */   
/*  71 */   private static boolean registeredCoreSubViews = false;
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/*  75 */     this.skin = skinObject.getSkin();
/*     */     
/*  77 */     this.skin.addListener("topbar-plugins", new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params) {
/*  80 */         if (eventType == 0) {
/*  81 */           TopBarView.this.skin.removeListener("topbar-plugins", this);
/*     */           
/*  83 */           AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */             public void azureusCoreRunning(AzureusCore core) {
/*  85 */               Utils.execSWTThreadLater(0, new AERunnable() {
/*     */                 public void runSupport() {
/*  87 */                   TopBarView.this.buildTopBarViews();
/*     */                 }
/*     */               });
/*     */             }
/*     */           });
/*     */         }
/*  93 */         return null;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  98 */     });
/*  99 */     this.skin.getSkinObject("topbar-area-plugin").addListener(new SWTSkinObjectListener()
/*     */     {
/*     */ 
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params)
/*     */       {
/* 104 */         if (eventType == 0)
/*     */         {
/* 106 */           if (TopBarView.this.activeTopBar != null)
/*     */           {
/* 108 */             TopBarView.this.activeTopBar.triggerEvent(3, null);
/*     */           }
/* 110 */         } else if (eventType == 1)
/*     */         {
/* 112 */           if (TopBarView.this.activeTopBar != null)
/*     */           {
/* 114 */             TopBarView.this.activeTopBar.triggerEvent(4, null);
/*     */           }
/*     */         }
/*     */         
/* 118 */         return null;
/*     */       }
/*     */       
/* 121 */     });
/* 122 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void buildTopBarViews()
/*     */   {
/* 132 */     SWTSkinObject skinObject = this.skin.getSkinObject("topbar-plugins");
/* 133 */     if (skinObject == null) {
/* 134 */       return;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 139 */       this.cPluginArea = ((Composite)skinObject.getControl());
/*     */       
/* 141 */       final UIUpdatable updatable = new UIUpdatable() {
/*     */         public void updateUI() {
/* 143 */           Object[] views = TopBarView.this.topbarViews.toArray();
/* 144 */           for (int i = 0; i < views.length; i++) {
/*     */             try {
/* 146 */               UISWTViewCore view = (UISWTViewCore)views[i];
/* 147 */               if (view.getComposite().isVisible()) {
/* 148 */                 view.triggerEvent(5, null);
/*     */               }
/*     */             } catch (Exception e) {
/* 151 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         public String getUpdateUIName() {
/* 157 */           return "TopBar";
/*     */         }
/*     */       };
/*     */       try {
/* 161 */         UIFunctionsManager.getUIFunctions().getUIUpdater().addUpdater(updatable);
/*     */       } catch (Exception e) {
/* 163 */         Debug.out(e);
/*     */       }
/*     */       
/* 166 */       skinObject.getControl().addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/*     */           try {
/* 169 */             UIFunctionsManager.getUIFunctions().getUIUpdater().removeUpdater(updatable);
/*     */           }
/*     */           catch (Exception ex) {
/* 172 */             Debug.out(ex);
/*     */           }
/* 174 */           Object[] views = TopBarView.this.topbarViews.toArray();
/* 175 */           TopBarView.this.topbarViews.clear();
/* 176 */           for (int i = 0; i < views.length; i++) {
/* 177 */             UISWTViewCore view = (UISWTViewCore)views[i];
/* 178 */             if (view != null) {
/* 179 */               view.triggerEvent(7, null);
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
/*     */             }
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
/*     */           }
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
/*     */         }
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
/* 260 */       });
/* 261 */       SWTSkinObject soList = this.skin.getSkinObject("topbar-plugin-list");
/* 262 */       if (soList != null) {
/* 263 */         Composite cList = (Composite)soList.getControl();
/* 264 */         this.listPlugins = new org.eclipse.swt.widgets.List(cList, 512);
/* 265 */         this.listPlugins.setLayoutData(Utils.getFilledFormData());
/* 266 */         this.listPlugins.setBackground(cList.getBackground());
/* 267 */         this.listPlugins.setForeground(cList.getForeground());
/* 268 */         this.listPlugins.addSelectionListener(new SelectionListener() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 270 */             String[] selection = TopBarView.this.listPlugins.getSelection();
/*     */             String name;
/* 272 */             if (selection.length > 0)
/*     */             {
/* 274 */               name = selection[0];
/*     */               
/* 276 */               for (UISWTViewCore view : TopBarView.this.topbarViews)
/*     */               {
/* 278 */                 if (TopBarView.this.getViewName(view).equals(name))
/*     */                 {
/* 280 */                   TopBarView.this.activateTopBar(view);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void widgetDefaultSelected(SelectionEvent e) {}
/* 289 */         });
/* 290 */         Messages.setLanguageTooltip(this.listPlugins, "label.right.click.for.options");
/*     */         
/* 292 */         final Menu menu = new Menu(this.listPlugins);
/*     */         
/* 294 */         this.listPlugins.setMenu(menu);
/*     */         
/* 296 */         menu.addMenuListener(new MenuListener()
/*     */         {
/*     */ 
/*     */           public void menuShown(MenuEvent e)
/*     */           {
/* 301 */             for (MenuItem mi : menu.getItems()) {
/* 302 */               mi.dispose();
/*     */             }
/*     */             
/* 305 */             for (final UISWTViewCore view : TopBarView.this.topbarViews)
/*     */             {
/* 307 */               final String name = TopBarView.this.getViewName(view);
/*     */               
/* 309 */               final MenuItem mi = new MenuItem(menu, 32);
/*     */               
/* 311 */               mi.setText(name);
/*     */               
/* 313 */               boolean enabled = TopBarView.this.isEnabled(view);
/*     */               
/* 315 */               mi.setSelection(enabled);
/*     */               
/* 317 */               mi.addSelectionListener(new SelectionAdapter()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void widgetSelected(SelectionEvent e)
/*     */                 {
/*     */ 
/* 324 */                   boolean enabled = mi.getSelection();
/*     */                   
/* 326 */                   TopBarView.this.setEnabled(view, enabled);
/*     */                   
/* 328 */                   if (enabled)
/*     */                   {
/* 330 */                     TopBarView.this.activateTopBar(view);
/*     */                   }
/*     */                   else
/*     */                   {
/* 334 */                     TopBarView.this.listPlugins.remove(name);
/*     */                     
/* 336 */                     TopBarView.this.activateTopBar(null);
/*     */                   }
/*     */                   
/* 339 */                   Utils.relayout(TopBarView.this.cPluginArea);
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void menuHidden(MenuEvent e) {}
/*     */         });
/*     */       }
/*     */       
/*     */ 
/* 352 */       skinObject = this.skin.getSkinObject("pluginbar");
/* 353 */       if (skinObject != null) {
/* 354 */         Listener l = new Listener() {
/* 355 */           private int mouseDownAt = 0;
/*     */           
/*     */           public void handleEvent(Event event) {
/* 358 */             Composite c = (Composite)event.widget;
/* 359 */             if (event.type == 3) {
/* 360 */               Rectangle clientArea = c.getClientArea();
/* 361 */               if (event.y > clientArea.height - 10) {
/* 362 */                 this.mouseDownAt = event.y;
/*     */               }
/* 364 */             } else if ((event.type == 4) && (this.mouseDownAt > 0)) {
/* 365 */               int diff = event.y - this.mouseDownAt;
/* 366 */               this.mouseDownAt = 0;
/* 367 */               FormData formData = (FormData)c.getLayoutData();
/* 368 */               formData.height += diff;
/* 369 */               if (formData.height < 50) {
/* 370 */                 formData.height = 50;
/*     */               } else {
/* 372 */                 Rectangle clientArea = c.getShell().getClientArea();
/* 373 */                 int max = clientArea.height - 350;
/* 374 */                 if (formData.height > max) {
/* 375 */                   formData.height = max;
/*     */                 }
/*     */               }
/* 378 */               COConfigurationManager.setParameter("v3.topbar.height", formData.height);
/*     */               
/* 380 */               Utils.relayout(c);
/* 381 */             } else if (event.type == 5) {
/* 382 */               Rectangle clientArea = c.getClientArea();
/* 383 */               boolean draggable = event.y > clientArea.height - 10;
/* 384 */               c.setCursor(draggable ? c.getDisplay().getSystemCursor(7) : null);
/*     */             }
/* 386 */             else if (event.type == 7) {
/* 387 */               c.setCursor(null);
/*     */             }
/*     */           }
/* 390 */         };
/* 391 */         Control control = skinObject.getControl();
/* 392 */         control.addListener(3, l);
/* 393 */         control.addListener(4, l);
/* 394 */         control.addListener(5, l);
/* 395 */         control.addListener(7, l);
/*     */         
/* 397 */         skinObject.addListener(new SWTSkinObjectListener()
/*     */         {
/*     */           public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params) {
/* 400 */             if (eventType == 0) {
/* 401 */               int h = COConfigurationManager.getIntParameter("v3.topbar.height");
/* 402 */               Control control = skinObject.getControl();
/* 403 */               FormData formData = (FormData)control.getLayoutData();
/* 404 */               formData.height = h;
/* 405 */               control.setLayoutData(formData);
/* 406 */               Utils.relayout(control);
/*     */             }
/* 408 */             return null;
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 413 */       UISWTInstanceImpl uiSWTinstance = (UISWTInstanceImpl)UIFunctionsManagerSWT.getUIFunctionsSWT().getUISWTInstance();
/*     */       
/* 415 */       if ((uiSWTinstance != null) && (!registeredCoreSubViews)) {
/* 416 */         uiSWTinstance.addView("TopBar", "ViewDownSpeedGraph", new ViewDownSpeedGraph());
/*     */         
/* 418 */         uiSWTinstance.addView("TopBar", "ViewUpSpeedGraph", new ViewUpSpeedGraph());
/*     */         
/* 420 */         uiSWTinstance.addView("TopBar", "ViewQuickConfig", new ViewQuickConfig());
/*     */         
/* 422 */         uiSWTinstance.addView("TopBar", "ViewQuickNetInfo", new ViewQuickNetInfo());
/*     */         
/* 424 */         uiSWTinstance.addView("TopBar", "ViewQuickNotifications", new ViewQuickNotifications());
/*     */         
/*     */ 
/*     */ 
/* 428 */         registeredCoreSubViews = true;
/*     */       }
/*     */       
/* 431 */       if (uiSWTinstance != null)
/*     */       {
/* 433 */         UISWTViewEventListenerHolder[] pluginViews = uiSWTinstance.getViewListeners("TopBar");
/*     */         
/* 435 */         for (UISWTViewEventListenerHolder l : pluginViews)
/*     */         {
/* 437 */           if (l != null) {
/*     */             try
/*     */             {
/* 440 */               UISWTViewImpl view = new UISWTViewImpl(l.getViewID(), "TopBar", false);
/*     */               
/* 442 */               view.setEventListener(l, true);
/*     */               
/* 444 */               addTopBarView(view, this.cPluginArea);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 448 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 454 */       String active_view_id = COConfigurationManager.getStringParameter("topbar.active.view.id", "");
/*     */       
/* 456 */       boolean activated = false;
/* 457 */       UISWTViewCore first_enabled = null;
/*     */       
/* 459 */       for (UISWTViewCore view : this.topbarViews)
/*     */       {
/* 461 */         if (isEnabled(view))
/*     */         {
/* 463 */           if (first_enabled == null)
/*     */           {
/* 465 */             first_enabled = view;
/*     */           }
/*     */           
/* 468 */           if (active_view_id.equals(view.getViewID()))
/*     */           {
/* 470 */             activateTopBar(view);
/*     */             
/* 472 */             activated = true;
/*     */             
/* 474 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 479 */       if ((!activated) && (first_enabled != null))
/*     */       {
/* 481 */         activateTopBar(first_enabled);
/*     */         
/* 483 */         activated = true;
/*     */       }
/*     */       
/* 486 */       if ((!activated) && (this.topbarViews.size() > 0))
/*     */       {
/* 488 */         UISWTViewCore view = (UISWTViewCore)this.topbarViews.get(0);
/*     */         
/* 490 */         setEnabled(view, true);
/*     */         
/* 492 */         activateTopBar(view);
/*     */       }
/*     */       
/* 495 */       if (skinObject != null)
/*     */       {
/* 497 */         skinObject.getControl().getParent().layout(true);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 501 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isEnabled(UISWTViewCore view)
/*     */   {
/* 509 */     return COConfigurationManager.getBooleanParameter("topbar.view." + view.getViewID() + ".enabled", true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean setEnabled(UISWTViewCore view, boolean enabled)
/*     */   {
/* 517 */     return COConfigurationManager.setParameter("topbar.view." + view.getViewID() + ".enabled", enabled);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getViewName(UISWTViewCore view)
/*     */   {
/* 526 */     String s = (String)view.getUserData(view_name_key);
/*     */     
/* 528 */     if (s != null)
/*     */     {
/* 530 */       return s;
/*     */     }
/*     */     
/* 533 */     s = view.getFullTitle();
/*     */     
/* 535 */     if (MessageText.keyExists(s))
/*     */     {
/* 537 */       s = MessageText.getString(s);
/*     */     }
/*     */     
/* 540 */     view.setUserData(view_name_key, s);
/*     */     
/* 542 */     return s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void activateTopBar(UISWTViewCore view)
/*     */   {
/* 549 */     if (view == null)
/*     */     {
/*     */ 
/*     */ 
/* 553 */       if (this.activeTopBar != null)
/*     */       {
/* 555 */         if (!isEnabled(this.activeTopBar))
/*     */         {
/* 557 */           Composite c = this.activeTopBar.getComposite();
/*     */           
/* 559 */           while (c.getParent() != this.cPluginArea)
/*     */           {
/* 561 */             c = c.getParent();
/*     */           }
/*     */           
/* 564 */           c.setVisible(false);
/*     */           
/* 566 */           this.activeTopBar = null;
/*     */           
/* 568 */           for (UISWTViewCore v : this.topbarViews)
/*     */           {
/* 570 */             if (isEnabled(v))
/*     */             {
/*     */ 
/*     */ 
/* 574 */               view = v;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 580 */       if (view == null)
/*     */       {
/*     */ 
/*     */ 
/* 584 */         return;
/*     */       }
/*     */     }
/*     */     
/* 588 */     if (!isEnabled(view))
/*     */     {
/* 590 */       Debug.out("Attempt to activate disabled view");
/*     */       
/* 592 */       return;
/*     */     }
/*     */     
/* 595 */     if (view == this.activeTopBar)
/*     */     {
/* 597 */       return;
/*     */     }
/*     */     
/* 600 */     if (this.activeTopBar != null)
/*     */     {
/* 602 */       Composite c = this.activeTopBar.getComposite();
/*     */       
/* 604 */       while (c.getParent() != this.cPluginArea)
/*     */       {
/* 606 */         c = c.getParent();
/*     */       }
/*     */       
/* 609 */       c.setVisible(false);
/*     */     }
/*     */     
/* 612 */     this.activeTopBar = view;
/*     */     
/* 614 */     COConfigurationManager.setParameter("topbar.active.view.id", view.getViewID());
/*     */     
/* 616 */     if (this.listPlugins != null)
/*     */     {
/* 618 */       String name = getViewName(view);
/*     */       
/* 620 */       int index = this.listPlugins.indexOf(name);
/*     */       
/* 622 */       if (index == -1)
/*     */       {
/* 624 */         this.listPlugins.add(name);
/*     */         
/* 626 */         index = this.listPlugins.indexOf(name);
/*     */       }
/*     */       
/* 629 */       this.listPlugins.setSelection(new String[0]);
/*     */     }
/*     */     
/*     */ 
/* 633 */     Composite c = this.activeTopBar.getComposite();
/*     */     
/* 635 */     while (c.getParent() != this.cPluginArea)
/*     */     {
/* 637 */       c = c.getParent();
/*     */     }
/*     */     
/* 640 */     c.setVisible(true);
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
/* 651 */     Utils.relayout(this.cPluginArea);
/*     */     
/* 653 */     this.activeTopBar.triggerEvent(3, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addTopBarView(UISWTViewCore view, Composite composite)
/*     */   {
/* 662 */     Composite parent = new Composite(composite, 0);
/* 663 */     parent.setLayoutData(Utils.getFilledFormData());
/* 664 */     parent.setLayout(new FormLayout());
/*     */     
/* 666 */     view.initialize(parent);
/* 667 */     parent.setVisible(false);
/*     */     
/* 669 */     Control[] children = parent.getChildren();
/* 670 */     for (int i = 0; i < children.length; i++) {
/* 671 */       Control control = children[i];
/* 672 */       Object ld = control.getLayoutData();
/* 673 */       boolean useGridLayout = (ld != null) && ((ld instanceof GridData));
/* 674 */       if (useGridLayout) {
/* 675 */         GridLayout gridLayout = new GridLayout();
/* 676 */         gridLayout.horizontalSpacing = 0;
/* 677 */         gridLayout.marginHeight = 0;
/* 678 */         gridLayout.marginWidth = 0;
/* 679 */         gridLayout.verticalSpacing = 0;
/* 680 */         parent.setLayout(gridLayout);
/* 681 */         break; }
/* 682 */       if (ld == null) {
/* 683 */         control.setLayoutData(Utils.getFilledFormData());
/*     */       }
/*     */     }
/*     */     
/* 687 */     this.topbarViews.add(view);
/*     */     
/* 689 */     if (this.listPlugins != null)
/*     */     {
/* 691 */       if (isEnabled(view))
/*     */       {
/* 693 */         this.listPlugins.add(getViewName(view));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/TopBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */