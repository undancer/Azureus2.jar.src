/*     */ package com.aelitis.azureus.ui.swt.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext.torrentURLHandler;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginHTTPProxy;
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionListener;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.CookiesListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.OpenCloseSearchDetailsListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.ConfigListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.DisplayListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.ExternalLoginCookieListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.MetaSearchListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.TorrentListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.VuzeListener;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.browser.ProgressEvent;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AERunnableObject;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubscriptionViewInternalBrowser
/*     */   implements SubscriptionsViewBase, OpenCloseSearchDetailsListener, UIPluginViewToolBarListener
/*     */ {
/*     */   private static boolean subscription_proxy_init_done;
/*     */   private static AEProxyFactory.PluginHTTPProxy subscription_proxy;
/*     */   private static boolean subscription_proxy_set;
/*  74 */   private static AESemaphore subscription_proxy_sem = new AESemaphore("sps");
/*     */   
/*  76 */   private static List<SubscriptionViewInternalBrowser> pending = new ArrayList();
/*     */   private Subscription subs;
/*     */   private Composite parent_composite;
/*     */   private Composite composite;
/*     */   private BrowserWrapper mainBrowser;
/*     */   
/*     */   private static void initProxy()
/*     */   {
/*  84 */     synchronized (SubscriptionViewInternalBrowser.class)
/*     */     {
/*  86 */       if (subscription_proxy_init_done)
/*     */       {
/*  88 */         return;
/*     */       }
/*     */       
/*  91 */       subscription_proxy_init_done = true;
/*     */     }
/*     */     
/*  94 */     new AEThread2("ST_test")
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 100 */           String test_url = ConstantsVuze.getDefaultContentNetwork().getSubscriptionURL("derp");
/*     */           try
/*     */           {
/* 103 */             URL url = new URL(test_url);
/*     */             
/* 105 */             url = UrlUtils.setProtocol(url, "https");
/*     */             
/* 107 */             url = UrlUtils.setPort(url, 443);
/*     */             
/* 109 */             boolean use_proxy = !COConfigurationManager.getStringParameter("browser.internal.proxy.id", "none").equals("none");
/*     */             
/* 111 */             if (!use_proxy)
/*     */             {
/* 113 */               Boolean looks_ok = AEProxyFactory.testPluginHTTPProxy(url, true);
/*     */               
/* 115 */               use_proxy = (looks_ok != null) && (!looks_ok.booleanValue());
/*     */             }
/*     */             
/* 118 */             if (use_proxy)
/*     */             {
/* 120 */               SubscriptionViewInternalBrowser.access$002(AEProxyFactory.getPluginHTTPProxy("subscriptions", url, true));
/*     */               
/* 122 */               if (SubscriptionViewInternalBrowser.subscription_proxy != null)
/*     */               {
/* 124 */                 UrlFilter.getInstance().addUrlWhitelist("https?://" + ((InetSocketAddress)SubscriptionViewInternalBrowser.subscription_proxy.getProxy().address()).getAddress().getHostAddress() + ":?[0-9]*/.*"); }
/*     */             }
/*     */           } catch (Throwable e) {}
/*     */         } finally {
/*     */           List<SubscriptionViewInternalBrowser> to_redo;
/*     */           Iterator i$;
/*     */           SubscriptionViewInternalBrowser view;
/* 131 */           List<SubscriptionViewInternalBrowser> to_redo = null;
/*     */           
/* 133 */           synchronized (SubscriptionViewInternalBrowser.class)
/*     */           {
/* 135 */             SubscriptionViewInternalBrowser.access$102(true);
/*     */             
/* 137 */             to_redo = new ArrayList(SubscriptionViewInternalBrowser.pending);
/*     */             
/* 139 */             SubscriptionViewInternalBrowser.pending.clear();
/*     */           }
/*     */           
/* 142 */           SubscriptionViewInternalBrowser.subscription_proxy_sem.releaseForever();
/*     */           
/* 144 */           for (SubscriptionViewInternalBrowser view : to_redo)
/*     */           {
/*     */             try {
/* 147 */               view.mainBrowserContext.setAutoReloadPending(false, SubscriptionViewInternalBrowser.subscription_proxy == null);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */             
/*     */ 
/* 152 */             if (SubscriptionViewInternalBrowser.subscription_proxy != null) {
/*     */               try
/*     */               {
/* 155 */                 view.updateBrowserProxy(SubscriptionViewInternalBrowser.subscription_proxy);
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */   static
/*     */   {
/* 168 */     COConfigurationManager.addParameterListener("browser.internal.proxy.id", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/* 176 */         synchronized (SubscriptionViewInternalBrowser.class)
/*     */         {
/* 178 */           if (!SubscriptionViewInternalBrowser.subscription_proxy_init_done)
/*     */           {
/* 180 */             return;
/*     */           }
/*     */           
/* 183 */           SubscriptionViewInternalBrowser.access$602(false);
/*     */           
/* 185 */           SubscriptionViewInternalBrowser.access$102(false);
/*     */           
/* 187 */           if (SubscriptionViewInternalBrowser.subscription_proxy != null)
/*     */           {
/* 189 */             SubscriptionViewInternalBrowser.subscription_proxy.destroy();
/*     */             
/* 191 */             SubscriptionViewInternalBrowser.access$002(null);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static AEProxyFactory.PluginHTTPProxy getSubscriptionProxy(SubscriptionViewInternalBrowser view)
/*     */   {
/* 202 */     initProxy();
/*     */     
/* 204 */     boolean force_proxy = !COConfigurationManager.getStringParameter("browser.internal.proxy.id", "none").equals("none");
/*     */     
/* 206 */     subscription_proxy_sem.reserve(force_proxy ? 60000L : 2500L);
/*     */     
/* 208 */     synchronized (SubscriptionViewInternalBrowser.class)
/*     */     {
/* 210 */       if (subscription_proxy_set)
/*     */       {
/* 212 */         return subscription_proxy;
/*     */       }
/*     */       
/*     */ 
/* 216 */       pending.add(view);
/*     */       try
/*     */       {
/* 219 */         view.mainBrowserContext.setAutoReloadPending(true, false);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 224 */       return null;
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
/*     */   public void refreshView()
/*     */   {
/* 254 */     if (this.subs == null) {
/* 255 */       return;
/*     */     }
/* 257 */     String key = "Subscription_" + ByteFormatter.encodeString(this.subs.getPublicKey());
/* 258 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/* 259 */     if (mdi != null) {
/* 260 */       MdiEntrySWT entry = mdi.getEntrySWT(key);
/* 261 */       if (entry != null) {
/* 262 */         UISWTViewEventListener eventListener = entry.getEventListener();
/* 263 */         if ((eventListener instanceof SubscriptionViewInternalBrowser)) {
/* 264 */           SubscriptionViewInternalBrowser subsView = (SubscriptionViewInternalBrowser)eventListener;
/* 265 */           subsView.updateBrowser(false);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initialize(Composite _parent_composite)
/*     */   {
/* 276 */     this.parent_composite = _parent_composite;
/*     */     
/* 278 */     this.composite = new Composite(this.parent_composite, 0);
/*     */     
/* 280 */     this.composite.setLayout(new FormLayout());
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
/* 320 */     this.subs.addListener(new SubscriptionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void subscriptionChanged(Subscription subs, int reason)
/*     */       {
/*     */ 
/*     */ 
/* 328 */         Utils.execSWTThread(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 334 */             SubscriptionViewInternalBrowser.this.updateInfo();
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void subscriptionDownloaded(Subscription subs, boolean auto)
/*     */       {
/* 344 */         if (auto)
/*     */         {
/* 346 */           SubscriptionViewInternalBrowser.this.updateBrowser(true);
/*     */         }
/*     */         
/*     */       }
/* 350 */     });
/* 351 */     updateInfo();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void createBrowsers()
/*     */   {
/* 358 */     if ((this.mainBrowser != null) && (!this.mainBrowser.isDisposed())) {
/* 359 */       return;
/*     */     }
/*     */     try {
/* 362 */       final BrowserWrapper bw = this.mainBrowser = BrowserWrapper.createBrowser(this.composite, Utils.getInitialBrowserStyle(0));
/* 363 */       this.mainBrowser.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/* 365 */           bw.setUrl("about:blank");
/* 366 */           bw.setVisible(false);
/*     */         }
/* 368 */       });
/* 369 */       this.mainBrowserContext = new BrowserContext("browser-window" + Math.random(), this.mainBrowser, null, true);
/*     */       
/*     */ 
/* 372 */       this.mainBrowserContext.addListener(new BrowserContext.loadingListener() {
/*     */         public void browserLoadingChanged(boolean loading, String url) {
/* 374 */           if (SubscriptionViewInternalBrowser.this.mdiInfo.spinnerImage != null) {
/* 375 */             SubscriptionViewInternalBrowser.this.mdiInfo.spinnerImage.setVisible(loading);
/*     */           }
/*     */           
/*     */         }
/* 379 */       });
/* 380 */       this.mainBrowserContext.addMessageListener(new TorrentListener());
/* 381 */       this.mainBrowserContext.addMessageListener(new VuzeListener());
/* 382 */       this.mainBrowserContext.addMessageListener(new DisplayListener(this.mainBrowser));
/* 383 */       this.mainBrowserContext.addMessageListener(new ConfigListener(this.mainBrowser));
/* 384 */       this.mainBrowserContext.addMessageListener(new MetaSearchListener(this));
/*     */       
/*     */ 
/* 387 */       ContentNetwork contentNetwork = ContentNetworkManagerFactory.getSingleton().getContentNetwork(this.mainBrowserContext.getContentNetworkID());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 393 */       String url = contentNetwork.getSubscriptionURL(this.subs.getID());
/*     */       
/* 395 */       Boolean edit_mode = (Boolean)this.subs.getUserData(SubscriptionManagerUI.SUB_EDIT_MODE_KEY);
/*     */       
/* 397 */       if (edit_mode != null)
/*     */       {
/* 399 */         if (edit_mode.booleanValue())
/*     */         {
/* 401 */           url = url + "&editMode=1";
/*     */         }
/*     */         
/* 404 */         this.subs.setUserData(SubscriptionManagerUI.SUB_EDIT_MODE_KEY, null);
/*     */       }
/*     */       
/* 407 */       this.mainBrowser.setData("StartURL", url);
/*     */       
/* 409 */       AEProxyFactory.PluginHTTPProxy proxy = getSubscriptionProxy(this);
/*     */       
/* 411 */       if (proxy != null)
/*     */       {
/* 413 */         url = proxy.proxifyURL(url);
/*     */         
/* 415 */         this.mainBrowser.setData("StartURL", url);
/*     */       }
/*     */       
/* 418 */       this.mainBrowser.setUrl(url);
/*     */       
/* 420 */       FormData data = new FormData();
/* 421 */       data.left = new FormAttachment(0, 0);
/* 422 */       data.right = new FormAttachment(100, 0);
/* 423 */       data.top = new FormAttachment(this.composite, 0);
/* 424 */       data.bottom = new FormAttachment(100, 0);
/* 425 */       this.mainBrowser.setLayoutData(data);
/*     */       
/* 427 */       final BrowserWrapper db = this.detailsBrowser = BrowserWrapper.createBrowser(this.composite, Utils.getInitialBrowserStyle(0));
/* 428 */       this.detailsBrowser.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/* 430 */           db.setUrl("about:blank");
/* 431 */           db.setVisible(false);
/*     */         }
/* 433 */       });
/* 434 */       BrowserContext detailsContext = new BrowserContext("browser-window" + Math.random(), this.detailsBrowser, null, false);
/*     */       
/* 436 */       detailsContext.addListener(new BrowserContext.loadingListener() {
/*     */         public void browserLoadingChanged(boolean loading, String url) {
/* 438 */           if (SubscriptionViewInternalBrowser.this.mdiInfo.spinnerImage != null) {
/* 439 */             SubscriptionViewInternalBrowser.this.mdiInfo.spinnerImage.setVisible(loading);
/*     */           }
/*     */           
/*     */         }
/* 443 */       });
/* 444 */       ClientMessageContext.torrentURLHandler url_handler = new ClientMessageContext.torrentURLHandler()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void handleTorrentURL(final String url)
/*     */         {
/*     */ 
/* 451 */           Utils.execSWTThreadWithObject("SMUI", new AERunnableObject()
/*     */           {
/*     */ 
/*     */ 
/*     */             public Object runSupport()
/*     */             {
/*     */ 
/* 458 */               String subscriptionId = (String)SubscriptionViewInternalBrowser.this.detailsBrowser.getData("subscription_id");
/* 459 */               String subscriptionResultId = (String)SubscriptionViewInternalBrowser.this.detailsBrowser.getData("subscription_result_id");
/*     */               
/* 461 */               if ((subscriptionId != null) && (subscriptionResultId != null))
/*     */               {
/* 463 */                 Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(subscriptionId);
/*     */                 
/* 465 */                 if (subs != null)
/*     */                 {
/* 467 */                   subs.addPotentialAssociation(subscriptionResultId, url);
/*     */                 }
/*     */               }
/*     */               
/* 471 */               return null; } }, 10000L);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 477 */       };
/* 478 */       detailsContext.setTorrentURLHandler(url_handler);
/*     */       
/* 480 */       TorrentListener torrent_listener = new TorrentListener();
/*     */       
/* 482 */       torrent_listener.setTorrentURLHandler(url_handler);
/*     */       
/* 484 */       detailsContext.addMessageListener(torrent_listener);
/* 485 */       detailsContext.addMessageListener(new VuzeListener());
/* 486 */       detailsContext.addMessageListener(new DisplayListener(this.detailsBrowser));
/* 487 */       detailsContext.addMessageListener(new ConfigListener(this.detailsBrowser));
/* 488 */       url = "about:blank";
/* 489 */       this.detailsBrowser.setUrl(url);
/* 490 */       this.detailsBrowser.setData("StartURL", url);
/*     */       
/* 492 */       ExternalLoginCookieListener cookieListener = new ExternalLoginCookieListener(new CookiesListener() {
/*     */         public void cookiesFound(String cookies) {
/* 494 */           if (SubscriptionViewInternalBrowser.this.detailsBrowser != null)
/* 495 */             SubscriptionViewInternalBrowser.this.detailsBrowser.setData("current-cookies", cookies); } }, this.detailsBrowser);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 500 */       cookieListener.hook();
/*     */       
/* 502 */       data = new FormData();
/* 503 */       data.left = new FormAttachment(0, 0);
/* 504 */       data.right = new FormAttachment(100, 0);
/* 505 */       data.top = new FormAttachment(this.mainBrowser.getControl(), 0);
/* 506 */       data.bottom = new FormAttachment(100, 0);
/* 507 */       this.detailsBrowser.setLayoutData(data);
/*     */       
/* 509 */       this.mainBrowser.setVisible(true);
/* 510 */       this.detailsBrowser.setVisible(false);
/*     */       
/* 512 */       this.mainBrowser.getParent().layout(true, true);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 517 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 525 */     list.put("share", Long.valueOf(1L));
/* 526 */     list.put("remove", Long.valueOf(1L));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 534 */     if (item.getID().equals("remove")) {
/* 535 */       this.mdiInfo.removeWithConfirm();
/*     */     }
/* 537 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroyBrowsers()
/*     */   {
/* 543 */     if (this.mainBrowser != null)
/*     */     {
/* 545 */       this.mainBrowser.dispose();
/*     */       
/* 547 */       this.mainBrowser = null;
/*     */     }
/*     */     
/* 550 */     if (this.detailsBrowser != null)
/*     */     {
/* 552 */       this.detailsBrowser.dispose();
/*     */       
/* 554 */       this.detailsBrowser = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void closeSearchResults(Map params) {
/* 559 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 562 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setVisible(false);
/*     */         
/* 564 */         FormData gd = (FormData)SubscriptionViewInternalBrowser.this.mainBrowser.getLayoutData();
/* 565 */         gd.bottom = new FormAttachment(100, 0);
/* 566 */         SubscriptionViewInternalBrowser.this.mainBrowser.setLayoutData(gd);
/*     */         
/* 568 */         SubscriptionViewInternalBrowser.this.mainBrowser.getParent().layout(true);
/* 569 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setUrl("about:blank");
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void openSearchResults(final Map params)
/*     */   {
/* 576 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 579 */         String url = MapUtils.getMapString(params, "url", "http://google.com/search?q=" + Math.random());
/*     */         
/* 581 */         if (UrlFilter.getInstance().urlCanRPC(url)) {
/* 582 */           url = ConstantsVuze.getDefaultContentNetwork().appendURLSuffix(url, false, true);
/*     */         }
/*     */         
/*     */ 
/* 586 */         String listenerAdded = (String)SubscriptionViewInternalBrowser.this.detailsBrowser.getData("g.nt.la");
/* 587 */         if (listenerAdded == null) {
/* 588 */           final BrowserWrapper browser = SubscriptionViewInternalBrowser.this.detailsBrowser;
/* 589 */           SubscriptionViewInternalBrowser.this.detailsBrowser.setData("g.nt.la", "");
/* 590 */           SubscriptionViewInternalBrowser.this.detailsBrowser.addProgressListener(new ProgressListener() {
/*     */             public void changed(ProgressEvent event) {}
/*     */             
/*     */             public void completed(ProgressEvent event) {
/* 594 */               String execAfterLoad = (String)browser.getData("execAfterLoad");
/*     */               
/* 596 */               browser.setData("execAfterLoad", null);
/* 597 */               boolean result; if ((execAfterLoad != null) && (!execAfterLoad.equals("")))
/*     */               {
/*     */ 
/* 600 */                 result = browser.execute(execAfterLoad);
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 610 */         String execAfterLoad = MapUtils.getMapString(params, "execAfterLoad", null);
/*     */         
/* 612 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setData("execAfterLoad", execAfterLoad);
/*     */         
/*     */ 
/* 615 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setData("subscription_id", MapUtils.getMapString(params, "subs_id", null));
/* 616 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setData("subscription_result_id", MapUtils.getMapString(params, "subs_rid", null));
/*     */         
/* 618 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setUrl(url);
/* 619 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setData("StartURL", url);
/* 620 */         SubscriptionViewInternalBrowser.this.detailsBrowser.setVisible(true);
/*     */         
/* 622 */         FormData data = (FormData)SubscriptionViewInternalBrowser.this.mainBrowser.getLayoutData();
/* 623 */         data.bottom = null;
/* 624 */         data.height = MapUtils.getMapInt(params, "top-height", 120);
/*     */         
/*     */ 
/* 627 */         SubscriptionViewInternalBrowser.this.mainBrowser.getParent().layout(true, true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateBrowserProxy(final AEProxyFactory.PluginHTTPProxy proxy)
/*     */   {
/* 637 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 643 */         if ((SubscriptionViewInternalBrowser.this.mainBrowser != null) && (!SubscriptionViewInternalBrowser.this.mainBrowser.isDisposed()) && (SubscriptionViewInternalBrowser.this.mainBrowser.isVisible()))
/*     */         {
/* 645 */           String url = (String)SubscriptionViewInternalBrowser.this.mainBrowser.getData("StartURL");
/*     */           
/* 647 */           if (url != null)
/*     */           {
/* 649 */             url = proxy.proxifyURL(url);
/*     */             
/* 651 */             SubscriptionViewInternalBrowser.this.mainBrowser.setData("StartURL", url);
/*     */             
/* 653 */             SubscriptionViewInternalBrowser.this.mainBrowser.setUrl(url);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateBrowser(final boolean is_auto)
/*     */   {
/* 664 */     if ((this.mainBrowser != null) && (!this.mainBrowser.isDisposed()))
/*     */     {
/* 666 */       Utils.execSWTThread(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 672 */           if ((SubscriptionViewInternalBrowser.this.mainBrowser != null) && (!SubscriptionViewInternalBrowser.this.mainBrowser.isDisposed()) && (SubscriptionViewInternalBrowser.this.mainBrowser.isVisible()))
/*     */           {
/* 674 */             String url = (String)SubscriptionViewInternalBrowser.this.mainBrowser.getData("StartURL");
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 679 */             if ((is_auto) && (url.endsWith("&editMode=1")))
/*     */             {
/* 681 */               url = url.substring(0, url.lastIndexOf("&editMode=1"));
/*     */               
/* 683 */               SubscriptionViewInternalBrowser.this.mainBrowser.setData("StartURL", url);
/*     */             }
/*     */             
/* 686 */             SubscriptionViewInternalBrowser.this.mainBrowser.setUrl(url);
/*     */           }
/*     */         }
/*     */       });
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
/*     */   private BrowserContext mainBrowserContext;
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
/*     */   private BrowserWrapper detailsBrowser;
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
/*     */   private SubscriptionMDIEntry mdiInfo;
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
/*     */   private UISWTView swtView;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 748 */     return this.composite;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getFullTitle()
/*     */   {
/* 754 */     if (this.subs == null) {
/* 755 */       return "";
/*     */     }
/* 757 */     return this.subs.getName();
/*     */   }
/*     */   
/*     */   public void resizeMainBrowser() {
/* 761 */     if (this.mainBrowser != null)
/*     */     {
/* 763 */       Utils.execSWTThreadLater(0, new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 769 */           if ((SubscriptionViewInternalBrowser.this.mainBrowser != null) && (!SubscriptionViewInternalBrowser.this.mainBrowser.isDisposed()) && (SubscriptionViewInternalBrowser.this.mainBrowser.isVisible()))
/*     */           {
/* 771 */             FormData data = (FormData)SubscriptionViewInternalBrowser.this.mainBrowser.getLayoutData();
/* 772 */             data.bottom = new FormAttachment(100, -1);
/* 773 */             SubscriptionViewInternalBrowser.this.mainBrowser.getParent().layout(true);
/* 774 */             Utils.execSWTThreadLater(0, new Runnable()
/*     */             {
/*     */               public void run() {
/* 777 */                 if ((SubscriptionViewInternalBrowser.this.mainBrowser != null) && (!SubscriptionViewInternalBrowser.this.mainBrowser.isDisposed()) && (SubscriptionViewInternalBrowser.this.mainBrowser.isVisible()))
/*     */                 {
/* 779 */                   FormData data = (FormData)SubscriptionViewInternalBrowser.this.mainBrowser.getLayoutData();
/* 780 */                   data.bottom = new FormAttachment(100, 0);
/* 781 */                   SubscriptionViewInternalBrowser.this.mainBrowser.getParent().layout(true);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */       });
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
/*     */   private void viewActivated()
/*     */   {
/* 800 */     if ((this.subs != null) && (this.mdiInfo == null)) {
/* 801 */       this.mdiInfo = ((SubscriptionMDIEntry)this.subs.getUserData(SubscriptionManagerUI.SUB_ENTRYINFO_KEY));
/*     */     }
/* 803 */     createBrowsers();
/*     */   }
/*     */   
/*     */   private void viewDeactivated() {
/* 807 */     if ((this.mdiInfo != null) && (this.mdiInfo.spinnerImage != null)) {
/* 808 */       this.mdiInfo.spinnerImage.setVisible(false);
/*     */     }
/* 810 */     destroyBrowsers();
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 814 */     switch (event.getType()) {
/*     */     case 0: 
/* 816 */       this.swtView = ((UISWTView)event.getData());
/* 817 */       this.swtView.setTitle(getFullTitle());
/* 818 */       break;
/*     */     
/*     */     case 7: 
/*     */       break;
/*     */     
/*     */     case 2: 
/* 824 */       initialize((Composite)event.getData());
/* 825 */       break;
/*     */     
/*     */     case 6: 
/* 828 */       Messages.updateLanguageForControl(getComposite());
/* 829 */       this.swtView.setTitle(getFullTitle());
/* 830 */       break;
/*     */     
/*     */     case 1: 
/* 833 */       dataSourceChanged(event.getData());
/* 834 */       break;
/*     */     
/*     */     case 3: 
/* 837 */       viewActivated();
/* 838 */       break;
/*     */     
/*     */     case 4: 
/* 841 */       viewDeactivated();
/* 842 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 848 */     return true;
/*     */   }
/*     */   
/*     */   private void dataSourceChanged(Object data)
/*     */   {
/* 853 */     if ((data instanceof Subscription)) {
/* 854 */       this.subs = ((Subscription)data);
/* 855 */       this.mdiInfo = ((SubscriptionMDIEntry)this.subs.getUserData(SubscriptionManagerUI.SUB_ENTRYINFO_KEY));
/*     */     }
/* 857 */     if ((this.subs != null) && (this.swtView != null)) {
/* 858 */       this.swtView.setTitle(getFullTitle());
/*     */     }
/*     */   }
/*     */   
/*     */   protected void updateInfo() {}
/*     */   
/*     */   public void resizeSecondaryBrowser() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionViewInternalBrowser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */