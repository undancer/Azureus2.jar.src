/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Panel;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.awt.SWT_AWT;
/*     */ import org.eclipse.swt.custom.CTabFolder;
/*     */ import org.eclipse.swt.custom.CTabItem;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.TabFolder;
/*     */ import org.eclipse.swt.widgets.TabItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIRuntimeException;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarEnablerBase;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*     */ import org.gudy.azureus2.ui.swt.plugins.PluginUISWTSkinObject;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ import org.gudy.azureus2.ui.swt.views.IViewAlwaysInitialize;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UISWTViewImpl
/*     */   implements UISWTViewCore, UIPluginViewToolBarListener
/*     */ {
/*     */   public static final String CFG_PREFIX = "Views.plugins.";
/*  78 */   private boolean delayInitializeToFirstActivate = true;
/*     */   
/*     */ 
/*     */   private static final boolean DEBUG_TRIGGERS = false;
/*     */   
/*     */ 
/*     */   protected PluginUISWTSkinObject skinObject;
/*     */   
/*     */ 
/*     */   private Object initialDatasource;
/*     */   
/*     */   private UISWTView parentView;
/*     */   
/*     */   protected Object datasource;
/*     */   
/*  93 */   private boolean useCoreDataSource = false;
/*     */   
/*     */   private UISWTViewEventListener eventListener;
/*     */   
/*     */   protected Composite composite;
/*     */   
/*     */   protected final String id;
/*     */   
/*     */   private String title;
/*     */   
/*     */   private String titleID;
/*     */   
/*     */   private String setTitle;
/*     */   
/*     */   private String setTitleID;
/*     */   
/* 109 */   private int iControlType = 0;
/*     */   
/* 111 */   private Boolean hasFocus = null;
/*     */   
/*     */   private Map<Object, Object> user_data;
/*     */   
/* 115 */   private boolean haveSentInitialize = false;
/*     */   
/* 117 */   private boolean created = false;
/*     */   
/*     */   private String parentViewID;
/*     */   
/*     */   private boolean destroyOnDeactivate;
/*     */   
/*     */   private Composite masterComposite;
/*     */   
/* 125 */   private Set<UIPluginViewToolBarListener> setToolBarEnablers = new HashSet(1);
/*     */   
/*     */   public UISWTViewImpl(String id, String parentViewID, boolean destroyOnDeactivate) {
/* 128 */     this.id = id;
/* 129 */     this.parentViewID = parentViewID;
/* 130 */     this.destroyOnDeactivate = destroyOnDeactivate;
/* 131 */     this.titleID = ("Views.plugins." + this.id + ".title");
/* 132 */     if ((!MessageText.keyExists(this.titleID)) && (MessageText.keyExists(this.id))) {
/* 133 */       this.titleID = id;
/* 134 */     } else if (id.contains(" ")) {
/* 135 */       this.titleID = ("!" + id + "!");
/*     */     }
/*     */   }
/*     */   
/*     */   public void setEventListener(UISWTViewEventListener _eventListener, boolean doCreate)
/*     */     throws UISWTViewEventCancelledException
/*     */   {
/* 142 */     this.eventListener = _eventListener;
/*     */     
/* 144 */     if (this.eventListener == null) {
/* 145 */       return;
/*     */     }
/*     */     
/* 148 */     if ((_eventListener instanceof UISWTViewEventListenerHolder)) {
/* 149 */       UISWTViewEventListenerHolder h = (UISWTViewEventListenerHolder)_eventListener;
/* 150 */       UISWTViewEventListener delegatedEventListener = h.getDelegatedEventListener(this);
/*     */       
/* 152 */       if (delegatedEventListener != null) {
/* 153 */         this.eventListener = delegatedEventListener;
/*     */       }
/*     */     }
/*     */     
/* 157 */     if ((this.eventListener instanceof IViewAlwaysInitialize)) {
/* 158 */       this.delayInitializeToFirstActivate = false;
/*     */     }
/*     */     
/* 161 */     if ((this.eventListener instanceof UISWTViewCoreEventListener)) {
/* 162 */       setUseCoreDataSource(true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 168 */     if ((doCreate) && (!triggerBooleanEvent(0, this))) {
/* 169 */       throw new UISWTViewEventCancelledException();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UISWTViewEventListener getEventListener()
/*     */   {
/* 178 */     return this.eventListener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getInitialDataSource()
/*     */   {
/* 185 */     return this.initialDatasource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDatasource(Object datasource)
/*     */   {
/* 192 */     if (this.initialDatasource == null) {
/* 193 */       this.initialDatasource = datasource;
/*     */     }
/* 195 */     triggerEvent(1, datasource);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getDataSource()
/*     */   {
/* 205 */     return PluginCoreUtils.convert(this.datasource, useCoreDataSource());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setParentView(UISWTView parentView)
/*     */   {
/* 212 */     this.parentView = parentView;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UISWTView getParentView()
/*     */   {
/* 219 */     return this.parentView;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getViewID()
/*     */   {
/* 227 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void closeView()
/*     */   {
/*     */     try
/*     */     {
/* 241 */       Composite c = getComposite();
/*     */       
/* 243 */       if ((c != null) && (!c.isDisposed()))
/*     */       {
/* 245 */         Composite parent = c.getParent();
/*     */         
/* 247 */         triggerEvent(7, null);
/*     */         
/* 249 */         if ((parent instanceof CTabFolder))
/*     */         {
/* 251 */           for (CTabItem item : ((CTabFolder)parent).getItems())
/*     */           {
/* 253 */             if (item.getControl() == c)
/*     */             {
/* 255 */               item.dispose();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (Throwable e) {
/* 261 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setControlType(int iControlType)
/*     */   {
/* 270 */     if ((iControlType == 1) || (iControlType == 0) || (iControlType == 257))
/*     */     {
/* 272 */       this.iControlType = iControlType;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getControlType()
/*     */   {
/* 280 */     return this.iControlType;
/*     */   }
/*     */   
/*     */ 
/*     */   public void triggerEvent(int eventType, Object data)
/*     */   {
/*     */     try
/*     */     {
/* 288 */       triggerBooleanEvent(eventType, data);
/*     */     }
/*     */     catch (Exception e) {
/* 291 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static String padRight(String s, int n) {
/* 296 */     return String.format("%1$-" + n + "s", new Object[] { s });
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
/*     */   private boolean triggerBooleanEvent(int eventType, Object data)
/*     */   {
/* 316 */     if (eventType == 6)
/*     */     {
/*     */ 
/*     */ 
/* 320 */       this.titleID = ("Views.plugins." + this.id + ".title");
/* 321 */       if ((!MessageText.keyExists(this.titleID)) && (MessageText.keyExists(this.id))) {
/* 322 */         this.titleID = this.id;
/* 323 */       } else if (this.id.contains(" ")) {
/* 324 */         this.titleID = ("!" + this.id + "!");
/*     */       }
/* 326 */       this.title = null;
/*     */       
/*     */ 
/*     */ 
/* 330 */       if (this.setTitleID != null) {
/* 331 */         setTitleID(this.setTitleID);
/*     */       }
/* 333 */       if (this.setTitle != null) {
/* 334 */         setTitle(this.setTitle);
/*     */       }
/*     */       
/* 337 */       refreshTitle();
/* 338 */       Messages.updateLanguageForControl(getComposite());
/*     */     }
/*     */     
/* 341 */     if ((this.eventListener == null) && (eventType != 1))
/*     */     {
/*     */ 
/* 344 */       return false;
/*     */     }
/*     */     
/* 347 */     if (eventType == 2) {
/* 348 */       if (this.haveSentInitialize)
/*     */       {
/*     */ 
/*     */ 
/* 352 */         return false;
/*     */       }
/* 354 */       if (!this.created)
/*     */       {
/* 356 */         triggerBooleanEvent(0, this);
/* 357 */       } else if (this.datasource != null) {
/* 358 */         triggerBooleanEvent(1, this.datasource);
/*     */       }
/* 360 */       this.haveSentInitialize = true;
/*     */     }
/*     */     
/* 363 */     if (eventType == 0) {
/* 364 */       this.created = true;
/*     */     }
/*     */     
/* 367 */     if ((this.delayInitializeToFirstActivate) && (eventType == 3) && (!this.haveSentInitialize))
/*     */     {
/*     */ 
/* 370 */       swt_triggerInitialize();
/*     */     }
/*     */     
/* 373 */     if ((eventType == 3) && (this.hasFocus != null) && (this.hasFocus.booleanValue()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 378 */       return true;
/*     */     }
/* 380 */     if ((eventType == 4) && (this.hasFocus != null) && (!this.hasFocus.booleanValue()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 385 */       return true;
/*     */     }
/*     */     
/* 388 */     if (eventType == 1) {
/* 389 */       Object newDataSource = PluginCoreUtils.convert(data, true);
/* 390 */       if (this.datasource == newDataSource)
/*     */       {
/*     */ 
/*     */ 
/* 394 */         return true;
/*     */       }
/* 396 */       if (((newDataSource instanceof Object[])) && ((this.datasource instanceof Object[])) && 
/* 397 */         (Arrays.equals((Object[])newDataSource, (Object[])this.datasource)))
/*     */       {
/*     */ 
/*     */ 
/* 401 */         return true;
/*     */       }
/*     */       
/* 404 */       this.datasource = newDataSource;
/* 405 */       data = PluginCoreUtils.convert(this.datasource, this.useCoreDataSource);
/* 406 */       if (this.initialDatasource == null) {
/* 407 */         this.initialDatasource = this.datasource;
/*     */       }
/* 409 */       if (this.eventListener == null) {
/* 410 */         return true;
/*     */       }
/*     */       
/*     */     }
/* 414 */     else if ((eventType == 9) && ((this.eventListener instanceof ObfusticateImage)))
/*     */     {
/* 416 */       if ((data instanceof Map)) {
/* 417 */         ((ObfusticateImage)this.eventListener).obfusticatedImage((Image)MapUtils.getMapObject((Map)data, "image", null, Image.class));
/*     */       }
/*     */       
/*     */     }
/* 421 */     else if (eventType == 3) {
/* 422 */       this.hasFocus = Boolean.valueOf(true);
/* 423 */       if (!this.haveSentInitialize) {
/* 424 */         swt_triggerInitialize();
/*     */       }
/* 426 */     } else if (eventType == 4) {
/* 427 */       this.hasFocus = Boolean.valueOf(false);
/* 428 */       if (isDestroyOnDeactivate()) {
/* 429 */         triggerEvent(7, null);
/*     */       }
/* 431 */     } else if (eventType == 7) {
/* 432 */       if ((this.hasFocus != null) && (this.hasFocus.booleanValue())) {
/* 433 */         triggerEvent(4, null);
/*     */       }
/*     */       
/* 436 */       if ((!this.created) && (!this.haveSentInitialize) && (getComposite() == null)) {
/* 437 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 441 */     boolean result = false;
/*     */     try {
/* 443 */       result = this.eventListener.eventOccurred(new UISWTViewEventImpl(this.parentViewID, this, eventType, data));
/*     */     }
/*     */     catch (Throwable t) {
/* 446 */       Debug.out("ViewID=" + this.id + "; EventID=" + org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent.DEBUG_TYPES[eventType] + "; data=" + data, t);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 452 */     if (eventType == 7) {
/* 453 */       if ((this.masterComposite != null) && (!this.masterComposite.isDisposed())) {
/* 454 */         Composite parent = this.masterComposite.getParent();
/* 455 */         Utils.disposeComposite(this.masterComposite);
/* 456 */         Utils.relayoutUp(parent);
/*     */       }
/* 458 */       this.masterComposite = null;
/* 459 */       this.composite = null;
/* 460 */       this.haveSentInitialize = false;
/* 461 */       this.hasFocus = Boolean.valueOf(false);
/* 462 */       this.created = false;
/* 463 */       this.initialDatasource = this.datasource;
/* 464 */       this.datasource = null;
/* 465 */     } else if (eventType == 0) {
/* 466 */       if ((this.eventListener instanceof UISWTViewEventListenerHolder)) {
/* 467 */         UISWTViewEventListenerHolder h = (UISWTViewEventListenerHolder)this.eventListener;
/* 468 */         UISWTViewEventListener delegatedEventListener = h.getDelegatedEventListener(this);
/*     */         
/* 470 */         if (delegatedEventListener != null) {
/*     */           try {
/* 472 */             setEventListener(delegatedEventListener, false);
/*     */           }
/*     */           catch (UISWTViewEventCancelledException e) {}
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 481 */       triggerEventRaw(1, PluginCoreUtils.convert(this.datasource, this.useCoreDataSource));
/*     */     }
/*     */     
/* 484 */     return result;
/*     */   }
/*     */   
/*     */   protected boolean triggerEventRaw(int eventType, Object data) {
/* 488 */     if (this.eventListener == null) {
/* 489 */       System.err.println("null eventListener for " + org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent.DEBUG_TYPES[eventType] + " " + Debug.getCompressedStackTrace());
/*     */       
/* 491 */       return eventType == 8;
/*     */     }
/*     */     try {
/* 494 */       return this.eventListener.eventOccurred(new UISWTViewEventImpl(this.parentViewID, this, eventType, data));
/*     */     }
/*     */     catch (Throwable t) {
/* 497 */       throw new UIRuntimeException("UISWTView.triggerEvent:: ViewID=" + this.id + "; EventID=" + eventType + "; data=" + data, t);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/* 506 */     if (title == null) {
/* 507 */       return;
/*     */     }
/* 509 */     this.setTitle = title;
/*     */     
/* 511 */     if ((title.startsWith("{")) && (title.endsWith("}")) && (title.length() > 2)) {
/* 512 */       setTitleID(title.substring(1, title.length() - 1));
/* 513 */       return;
/*     */     }
/* 515 */     if (title.equals(this.title)) {
/* 516 */       return;
/*     */     }
/* 518 */     if ((title.contains(".")) && (MessageText.keyExists(title))) {
/* 519 */       setTitleID(title);
/* 520 */       return;
/*     */     }
/*     */     
/* 523 */     this.title = title;
/* 524 */     this.titleID = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTitleID(String titleID)
/*     */   {
/* 531 */     if ((titleID != null) && ((MessageText.keyExists(titleID)) || (titleID.startsWith("!"))))
/*     */     {
/*     */ 
/* 534 */       this.setTitleID = titleID;
/*     */       
/* 536 */       this.titleID = titleID;
/* 537 */       this.title = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void refreshTitle() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/* 550 */     if ((this.eventListener instanceof UISWTViewEventListenerHolder)) {
/* 551 */       return ((UISWTViewEventListenerHolder)this.eventListener).getPluginInterface();
/*     */     }
/*     */     
/* 554 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Composite getComposite()
/*     */   {
/* 561 */     return this.composite;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTitleID()
/*     */   {
/* 569 */     if (this.title == null)
/*     */     {
/*     */ 
/* 572 */       if (MessageText.keyExists(this.id)) {
/* 573 */         return this.id;
/*     */       }
/* 575 */       String id = "Views.plugins." + this.id + ".title";
/* 576 */       if (MessageText.keyExists(id)) {
/* 577 */         return id;
/*     */       }
/* 579 */       return "!" + id + "!";
/*     */     }
/* 581 */     return "!" + this.title + "!";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getFullTitle()
/*     */   {
/* 588 */     if (this.titleID != null) {
/* 589 */       return MessageText.getString(this.titleID);
/*     */     }
/* 591 */     return this.title;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(Composite parent)
/*     */   {
/* 598 */     this.masterComposite = parent;
/* 599 */     if (this.iControlType == 0)
/*     */     {
/* 601 */       Layout parentLayout = parent.getLayout();
/* 602 */       if ((parentLayout instanceof FormLayout)) {
/* 603 */         this.composite = parent;
/*     */       } else {
/* 605 */         this.composite = new Composite(parent, 0);
/* 606 */         GridLayout layout = new GridLayout(1, false);
/* 607 */         layout.marginHeight = 0;
/* 608 */         layout.marginWidth = 0;
/* 609 */         this.composite.setLayout(layout);
/* 610 */         GridData gridData = new GridData(1808);
/* 611 */         this.composite.setLayoutData(gridData);
/*     */       }
/*     */       
/* 614 */       Listener showListener = new Listener() {
/*     */         public void handleEvent(Event event) {
/* 616 */           if ((UISWTViewImpl.this.composite == null) || (UISWTViewImpl.this.composite.isDisposed())) {
/* 617 */             return;
/*     */           }
/* 619 */           Composite parent = UISWTViewImpl.this.composite.getParent();
/* 620 */           if ((parent instanceof CTabFolder)) {
/* 621 */             CTabFolder tabFolder = (CTabFolder)parent;
/* 622 */             Control selectedControl = tabFolder.getSelection().getControl();
/* 623 */             if (selectedControl != UISWTViewImpl.this.composite) {
/* 624 */               return;
/*     */             }
/* 626 */           } else if ((parent instanceof TabFolder)) {
/* 627 */             TabFolder tabFolder = (TabFolder)parent;
/* 628 */             TabItem[] selectedControl = tabFolder.getSelection();
/* 629 */             if ((selectedControl != null) && (selectedControl.length == 1) && (selectedControl[0].getControl() != UISWTViewImpl.this.composite))
/*     */             {
/* 631 */               return;
/*     */             }
/*     */           }
/*     */           
/* 635 */           Utils.execSWTThreadLater(0, new AERunnable()
/*     */           {
/*     */             public void runSupport() {
/* 638 */               UISWTViewImpl.this.triggerEvent(3, null);
/*     */             }
/*     */             
/*     */           });
/*     */         }
/* 643 */       };
/* 644 */       this.composite.addListener(22, showListener);
/* 645 */       if (parent != this.composite) {
/* 646 */         parent.addListener(22, showListener);
/*     */       }
/* 648 */       if (this.composite.isVisible()) {
/* 649 */         boolean focusGained = true;
/* 650 */         if (((parent instanceof CTabFolder)) || ((parent instanceof TabFolder)))
/*     */         {
/*     */ 
/* 653 */           focusGained = false;
/*     */         }
/* 655 */         if (focusGained) {
/* 656 */           triggerEvent(3, null);
/*     */         }
/*     */       }
/* 659 */       if (this.delayInitializeToFirstActivate) {
/* 660 */         return;
/*     */       }
/* 662 */       swt_triggerInitialize();
/* 663 */     } else if (this.iControlType == 1) {
/* 664 */       this.composite = new Composite(parent, 16777216);
/* 665 */       FillLayout layout = new FillLayout();
/* 666 */       layout.marginHeight = 0;
/* 667 */       layout.marginWidth = 0;
/* 668 */       this.composite.setLayout(layout);
/* 669 */       GridData gridData = new GridData(1808);
/* 670 */       this.composite.setLayoutData(gridData);
/*     */       
/* 672 */       Frame f = SWT_AWT.new_Frame(this.composite);
/*     */       
/* 674 */       Panel pan = new Panel();
/*     */       
/* 676 */       f.add(pan);
/*     */       
/* 678 */       triggerEvent(2, pan);
/* 679 */     } else if (this.iControlType == 257) {
/* 680 */       triggerEvent(2, getPluginSkinObject());
/*     */     }
/*     */   }
/*     */   
/*     */   private void swt_triggerInitialize() {
/* 685 */     if (this.haveSentInitialize) {
/* 686 */       return;
/*     */     }
/*     */     
/* 689 */     if (!this.created) {
/* 690 */       triggerBooleanEvent(0, this);
/*     */     }
/*     */     
/* 693 */     if (this.composite != null) {
/* 694 */       this.composite.setRedraw(false);
/* 695 */       this.composite.setLayoutDeferred(true);
/* 696 */       triggerEvent(2, this.composite);
/*     */       
/* 698 */       if ((this.composite.getLayout() instanceof GridLayout))
/*     */       {
/* 700 */         Control[] children = this.composite.getChildren();
/* 701 */         for (int i = 0; i < children.length; i++) {
/* 702 */           Control control = children[i];
/* 703 */           Object layoutData = control.getLayoutData();
/* 704 */           if ((layoutData == null) || (!(layoutData instanceof GridData))) {
/* 705 */             if (layoutData != null) {
/* 706 */               Logger.log(new LogEvent(LogIDs.PLUGIN, 1, "Plugin View '" + this.id + "' tried to setLayoutData of " + control + " to a " + layoutData.getClass().getName()));
/*     */             }
/*     */             
/*     */ 
/*     */             GridData gridData;
/*     */             
/*     */             GridData gridData;
/*     */             
/* 714 */             if (children.length == 1) {
/* 715 */               gridData = new GridData(4, 4, true, true);
/*     */             } else {
/* 717 */               gridData = new GridData();
/*     */             }
/*     */             
/* 720 */             control.setLayoutData(gridData);
/*     */           }
/*     */         }
/*     */       }
/* 724 */       this.composite.layout();
/* 725 */       this.composite.setLayoutDeferred(false);
/* 726 */       this.composite.setRedraw(true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean useCoreDataSource()
/*     */   {
/* 734 */     return this.useCoreDataSource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUseCoreDataSource(boolean useCoreDataSource)
/*     */   {
/* 741 */     if (this.useCoreDataSource == useCoreDataSource) {
/* 742 */       return;
/*     */     }
/*     */     
/* 745 */     this.useCoreDataSource = useCoreDataSource;
/* 746 */     if (this.datasource != null) {
/* 747 */       setDatasource(this.datasource);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PluginUISWTSkinObject getPluginSkinObject()
/*     */   {
/* 755 */     return this.skinObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPluginSkinObject(PluginUISWTSkinObject so)
/*     */   {
/* 763 */     this.skinObject = so;
/*     */   }
/*     */   
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 768 */     UIToolBarEnablerBase[] toolbarEnablers = getToolbarEnablers();
/* 769 */     for (UIToolBarEnablerBase tbEnablerBase : toolbarEnablers) {
/* 770 */       if ((tbEnablerBase instanceof UIPluginViewToolBarListener)) {
/* 771 */         UIPluginViewToolBarListener tbEnabler = (UIPluginViewToolBarListener)tbEnablerBase;
/* 772 */         if (tbEnabler.toolBarItemActivated(item, activationType, datasource)) {
/* 773 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 777 */     return false;
/*     */   }
/*     */   
/*     */   public void refreshToolBarItems(Map<String, Long> list) {
/* 781 */     UIToolBarEnablerBase[] toolbarEnablers = getToolbarEnablers();
/* 782 */     for (UIToolBarEnablerBase tbEnablerBase : toolbarEnablers) {
/* 783 */       if ((tbEnablerBase instanceof UIPluginViewToolBarListener)) {
/* 784 */         UIPluginViewToolBarListener tbEnabler = (UIPluginViewToolBarListener)tbEnablerBase;
/* 785 */         tbEnabler.refreshToolBarItems(list);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setToolBarListener(UIPluginViewToolBarListener l)
/*     */   {
/* 794 */     addToolbarEnabler(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UIPluginViewToolBarListener getToolBarListener()
/*     */   {
/* 801 */     return this.setToolBarEnablers.size() == 0 ? null : (UIPluginViewToolBarListener)this.setToolBarEnablers.iterator().next();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UIToolBarEnablerBase[] getToolbarEnablers()
/*     */   {
/* 809 */     return (UIToolBarEnablerBase[])this.setToolBarEnablers.toArray(new UIToolBarEnablerBase[0]);
/*     */   }
/*     */   
/*     */   public boolean hasToolbarEnableers() {
/* 813 */     return this.setToolBarEnablers.size() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addToolbarEnabler(UIToolBarEnablerBase enabler)
/*     */   {
/* 820 */     if (this.setToolBarEnablers.contains(enabler)) {
/* 821 */       return;
/*     */     }
/* 823 */     this.setToolBarEnablers.add((UIPluginViewToolBarListener)enabler);
/* 824 */     setToolbarVisibility(this.setToolBarEnablers.size() > 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeToolbarEnabler(UIToolBarEnablerBase enabler)
/*     */   {
/* 831 */     this.setToolBarEnablers.remove(enabler);
/* 832 */     setToolbarVisibility(this.setToolBarEnablers.size() > 0);
/*     */   }
/*     */   
/*     */   protected void setToolbarVisibility(boolean visible) {}
/*     */   
/*     */   public void setUserData(Object key, Object data)
/*     */   {
/* 839 */     synchronized (this)
/*     */     {
/* 841 */       if (this.user_data == null)
/*     */       {
/* 843 */         this.user_data = new LightHashMap();
/*     */       }
/*     */       
/* 846 */       if (data == null)
/*     */       {
/* 848 */         this.user_data.remove(key);
/*     */         
/* 850 */         if (this.user_data.isEmpty())
/*     */         {
/* 852 */           this.user_data = null;
/*     */         }
/*     */       }
/*     */       else {
/* 856 */         this.user_data.put(key, data);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getUserData(Object key)
/*     */   {
/* 865 */     synchronized (this)
/*     */     {
/* 867 */       if (this.user_data == null)
/*     */       {
/* 869 */         return null;
/*     */       }
/*     */       
/* 872 */       return this.user_data.get(key);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDestroyOnDeactivate(boolean b)
/*     */   {
/* 880 */     this.destroyOnDeactivate = b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDestroyOnDeactivate()
/*     */   {
/* 887 */     return this.destroyOnDeactivate;
/*     */   }
/*     */   
/*     */   public boolean isDelayInitializeToFirstActivate() {
/* 891 */     return this.delayInitializeToFirstActivate;
/*     */   }
/*     */   
/*     */   public void setDelayInitializeToFirstActivate(boolean delayInitializeToFirstActivate)
/*     */   {
/* 896 */     this.delayInitializeToFirstActivate = delayInitializeToFirstActivate;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UISWTViewImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */