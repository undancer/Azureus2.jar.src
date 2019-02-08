/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginHTTPProxy;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.ConfigListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.DisplayListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.TorrentListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.VuzeListener;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import org.eclipse.swt.SWTError;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class SWTSkinObjectBrowser
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*     */   private boolean generic_proxy_init_done;
/*     */   private AEProxyFactory.PluginHTTPProxy generic_proxy;
/*     */   private boolean generic_proxy_set;
/*  57 */   private AESemaphore generic_proxy_sem = new AESemaphore("sps");
/*     */   
/*     */   private BrowserWrapper browser;
/*     */   private Composite cParent;
/*     */   private Composite cArea;
/*     */   private String sStartURL;
/*     */   private BrowserContext context;
/*     */   
/*     */   private void initProxy(final String target_url, final String proxy_reason)
/*     */   {
/*  67 */     synchronized (SWTSkinObjectBrowser.class)
/*     */     {
/*  69 */       if (this.generic_proxy_init_done)
/*     */       {
/*  71 */         return;
/*     */       }
/*     */       
/*  74 */       this.generic_proxy_init_done = true;
/*     */     }
/*     */     
/*  77 */     new AEThread2("GB_test")
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/*     */           try {
/*  84 */             URL url = new URL(target_url);
/*     */             
/*  86 */             url = UrlUtils.setProtocol(url, "https");
/*     */             
/*  88 */             url = UrlUtils.setPort(url, 443);
/*     */             
/*  90 */             boolean use_proxy = !COConfigurationManager.getStringParameter("browser.internal.proxy.id", "none").equals("none");
/*     */             
/*  92 */             if (!use_proxy)
/*     */             {
/*  94 */               Boolean looks_ok = AEProxyFactory.testPluginHTTPProxy(url, true);
/*     */               
/*  96 */               use_proxy = (looks_ok != null) && (!looks_ok.booleanValue());
/*     */             }
/*     */             
/*  99 */             if (use_proxy)
/*     */             {
/* 101 */               SWTSkinObjectBrowser.this.generic_proxy = AEProxyFactory.getPluginHTTPProxy(proxy_reason, url, true);
/*     */               
/* 103 */               if (SWTSkinObjectBrowser.this.generic_proxy != null)
/*     */               {
/* 105 */                 UrlFilter.getInstance().addUrlWhitelist("https?://" + ((InetSocketAddress)SWTSkinObjectBrowser.this.generic_proxy.getProxy().address()).getAddress().getHostAddress() + ":?[0-9]*/.*");
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */         finally {
/* 112 */           synchronized (SWTSkinObjectBrowser.class)
/*     */           {
/* 114 */             SWTSkinObjectBrowser.this.generic_proxy_set = true;
/*     */             
/* 116 */             SWTSkinObjectBrowser.this.generic_proxy_sem.releaseForever();
/*     */             
/* 118 */             if (SWTSkinObjectBrowser.this.isDisposed())
/*     */             {
/* 120 */               if (SWTSkinObjectBrowser.this.generic_proxy != null)
/*     */               {
/* 122 */                 SWTSkinObjectBrowser.this.generic_proxy.destroy();
/*     */                 
/* 124 */                 SWTSkinObjectBrowser.this.generic_proxy = null;
/*     */               }
/*     */               
/* 127 */               return;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 132 */           SWTSkinObjectBrowser.this.setAutoReloadPending(false, SWTSkinObjectBrowser.this.generic_proxy == null);
/*     */           
/* 134 */           if (SWTSkinObjectBrowser.this.generic_proxy != null)
/*     */           {
/* 136 */             SWTSkinObjectBrowser.this.updateBrowserProxy(SWTSkinObjectBrowser.this.generic_proxy);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String urlToUse;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean forceVisibleAfterLoad;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean use_generic_proxy;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String proxy_reason;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean autoReloadPending;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private AEProxyFactory.PluginHTTPProxy getGenericProxy(String target_url, String reason)
/*     */   {
/* 179 */     initProxy(target_url, reason);
/*     */     
/* 181 */     boolean force_proxy = !COConfigurationManager.getStringParameter("browser.internal.proxy.id", "none").equals("none");
/*     */     
/* 183 */     this.generic_proxy_sem.reserve(force_proxy ? 60000L : 2500L);
/*     */     
/* 185 */     synchronized (SWTSkinObjectBrowser.class)
/*     */     {
/* 187 */       if (this.generic_proxy_set)
/*     */       {
/* 189 */         return this.generic_proxy;
/*     */       }
/*     */       
/*     */ 
/* 193 */       setAutoReloadPending(true, false);
/*     */       
/* 195 */       return null;
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
/* 219 */   private static boolean doneTheUglySWTFocusHack = false;
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
/*     */   public SWTSkinObjectBrowser(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parent)
/*     */   {
/* 234 */     super(skin, properties, sID, sConfigID, "browser", parent);COConfigurationManager.addParameterListener("browser.internal.proxy.id", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/* 152 */         synchronized (SWTSkinObjectBrowser.class)
/*     */         {
/* 154 */           if (!SWTSkinObjectBrowser.this.generic_proxy_init_done)
/*     */           {
/* 156 */             return;
/*     */           }
/*     */           
/* 159 */           SWTSkinObjectBrowser.this.generic_proxy_init_done = false;
/*     */           
/* 161 */           SWTSkinObjectBrowser.this.generic_proxy_set = false;
/*     */           
/* 163 */           if (SWTSkinObjectBrowser.this.generic_proxy != null)
/*     */           {
/* 165 */             SWTSkinObjectBrowser.this.generic_proxy.destroy();
/*     */             
/* 167 */             SWTSkinObjectBrowser.this.generic_proxy = null;
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
/*     */       }
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
/* 214 */     }
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
/* 234 */       );this.use_generic_proxy = false;this.proxy_reason = null;this.autoReloadPending = false;
/*     */     
/* 236 */     this.cParent = (parent == null ? skin.getShell() : (Composite)parent.getControl());
/*     */     
/*     */ 
/* 239 */     this.cArea = this.cParent;
/* 240 */     this.cArea = new Canvas(this.cParent, 262144);
/* 241 */     this.cArea.setLayout(new FormLayout());
/*     */     
/* 243 */     setControl(this.cArea);
/*     */     
/* 245 */     if (this.cParent.isVisible()) {
/* 246 */       init();
/*     */     } else {
/* 248 */       addListener(new SWTSkinObjectListener()
/*     */       {
/*     */         public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params) {
/* 251 */           if (eventType == 0) {
/* 252 */             SWTSkinObjectBrowser.this.removeListener(this);
/* 253 */             SWTSkinObjectBrowser.this.init();
/*     */           }
/* 255 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void init() {
/* 262 */     if ((this.browser != null) && (!this.browser.isDisposed())) {
/* 263 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 267 */       this.browser = BrowserWrapper.createBrowser(this.cArea, Utils.getInitialBrowserStyle(0));
/*     */       
/* 269 */       this.browser.setLayoutData(Utils.getFilledFormData());
/* 270 */       this.browser.getParent().layout(true);
/*     */     } catch (SWTError e) {
/* 272 */       System.err.println("Browser: " + e.toString());
/* 273 */       return;
/*     */     }
/*     */     
/* 276 */     Control widgetIndicator = null;
/* 277 */     String sIndicatorWidgetID = this.properties.getStringValue(this.sConfigID + ".indicator");
/*     */     
/* 279 */     if (sIndicatorWidgetID != null) {
/* 280 */       SWTSkinObject skinObjectIndicator = this.skin.getSkinObjectByID(sIndicatorWidgetID);
/* 281 */       if (skinObjectIndicator != null) {
/* 282 */         widgetIndicator = skinObjectIndicator.getControl();
/*     */       }
/*     */     }
/*     */     
/* 286 */     String browserID = this.properties.getStringValue(this.sConfigID + ".view");
/* 287 */     if (browserID == null) {
/* 288 */       browserID = this.sID;
/*     */     }
/*     */     
/* 291 */     this.forceVisibleAfterLoad = this.properties.getBooleanValue(this.sConfigID + ".forceVisibleAfterLoad", true);
/* 292 */     this.context = new BrowserContext(browserID, this.browser, widgetIndicator, this.forceVisibleAfterLoad);
/*     */     
/* 294 */     if (this.autoReloadPending) {
/* 295 */       this.context.setAutoReloadPending(this.autoReloadPending, false);
/*     */     }
/* 297 */     boolean noListeners = this.properties.getBooleanValue(this.sConfigID + ".browser.nolisteners", false);
/*     */     
/* 299 */     if (!noListeners) {
/* 300 */       this.context.addMessageListener(new TorrentListener());
/* 301 */       this.context.addMessageListener(new VuzeListener());
/* 302 */       this.context.addMessageListener(new DisplayListener(this.browser));
/* 303 */       this.context.addMessageListener(new ConfigListener(this.browser));
/*     */     }
/*     */     
/* 306 */     boolean popouts = this.properties.getBooleanValue(this.sConfigID + ".browser.allowPopouts", true);
/* 307 */     this.context.setAllowPopups(popouts);
/*     */     
/* 309 */     this.context.addListener(new BrowserContext.loadingListener() {
/*     */       public void browserLoadingChanged(boolean loading, String url) {
/* 311 */         if ((loading) && (SWTSkinObjectBrowser.this.browser.isVisible()))
/*     */         {
/* 313 */           if (UrlFilter.getInstance().urlCanRPC(url)) {
/* 314 */             SelectedContentManager.clearCurrentlySelectedContent();
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/* 320 */     });
/* 321 */     String url = this.sStartURL != null ? this.sStartURL : this.urlToUse != null ? this.urlToUse : this.properties.getStringValue(this.sConfigID + ".url", (String)null);
/*     */     
/* 323 */     if (url != null) {
/* 324 */       setURL(url);
/*     */     }
/*     */   }
/*     */   
/*     */   public BrowserWrapper getBrowser() {
/* 329 */     if (this.browser == null) {
/* 330 */       init();
/*     */     }
/* 332 */     return this.browser;
/*     */   }
/*     */   
/*     */   public void setURL(final String url) {
/* 336 */     this.urlToUse = url;
/* 337 */     if (this.browser == null) {
/* 338 */       return;
/*     */     }
/* 340 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 342 */         if (SWTSkinObjectBrowser.this.browser.isDisposed()) {
/* 343 */           return;
/*     */         }
/* 345 */         if (url == null) {
/* 346 */           SWTSkinObjectBrowser.this.browser.setText("");
/*     */         } else {
/* 348 */           String urlToUse = url;
/* 349 */           if (UrlFilter.getInstance().urlCanRPC(url)) {
/* 350 */             ContentNetwork contentNetwork = ContentNetworkManagerFactory.getSingleton().getContentNetwork(SWTSkinObjectBrowser.this.context.getContentNetworkID());
/*     */             
/* 352 */             if (contentNetwork != null) {
/* 353 */               urlToUse = contentNetwork.appendURLSuffix(urlToUse, false, true);
/*     */             }
/*     */           }
/*     */           
/* 357 */           if (SWTSkinObjectBrowser.this.browser != null) {
/* 358 */             SWTSkinObjectBrowser.this.setBrowserURL(urlToUse);
/* 359 */             if (SWTSkinObjectBrowser.this.browser.isVisible()) {
/* 360 */               SWTSkinObjectBrowser.this.browser.setFocus();
/*     */             }
/*     */           }
/*     */         }
/* 364 */         if (SWTSkinObjectBrowser.this.sStartURL == null) {
/* 365 */           SWTSkinObjectBrowser.this.sStartURL = url;
/* 366 */           if (SWTSkinObjectBrowser.this.browser != null) {
/* 367 */             SWTSkinObjectBrowser.this.browser.setData("StartURL", url);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void restart()
/*     */   {
/* 376 */     if (null != this.sStartURL)
/*     */     {
/* 378 */       String sRand = "rand=" + SystemTime.getCurrentTime();
/* 379 */       String startURLUnique; String startURLUnique; if (this.sStartURL.indexOf("rand=") > 0) {
/* 380 */         startURLUnique = this.sStartURL.replaceAll("rand=[0-9.]+", sRand); } else { String startURLUnique;
/* 381 */         if (this.sStartURL.indexOf('?') > 0) {
/* 382 */           startURLUnique = this.sStartURL + "&" + sRand;
/*     */         } else
/* 384 */           startURLUnique = this.sStartURL + "?" + sRand;
/*     */       }
/* 386 */       System.out.println(startURLUnique);
/* 387 */       setURL(startURLUnique);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void layout()
/*     */   {
/* 395 */     this.cParent.layout();
/*     */   }
/*     */   
/*     */   public BrowserContext getContext() {
/* 399 */     return this.context;
/*     */   }
/*     */   
/*     */   public String getStartURL() {
/* 403 */     return this.sStartURL;
/*     */   }
/*     */   
/*     */   public void setStartURL(String url) {
/* 407 */     this.sStartURL = url;
/* 408 */     if (null != this.browser) {
/* 409 */       if (this.urlToUse == null) {
/* 410 */         setBrowserURL(url);
/*     */       }
/* 412 */       this.browser.setData("StartURL", url);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setBrowserURL(String url)
/*     */   {
/* 420 */     if (this.use_generic_proxy)
/*     */     {
/* 422 */       this.browser.setData("CurrentURL", url);
/*     */       
/* 424 */       AEProxyFactory.PluginHTTPProxy proxy = getGenericProxy(url, this.proxy_reason);
/*     */       
/* 426 */       if (proxy != null)
/*     */       {
/* 428 */         url = proxy.proxifyURL(url);
/*     */         
/* 430 */         this.browser.setData("StartURL", url);
/*     */       }
/*     */     }
/*     */     
/* 434 */     this.browser.setUrl(url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void updateBrowserProxy(final AEProxyFactory.PluginHTTPProxy proxy)
/*     */   {
/* 441 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 447 */         if ((SWTSkinObjectBrowser.this.browser != null) && (!SWTSkinObjectBrowser.this.browser.isDisposed()))
/*     */         {
/* 449 */           String url = (String)SWTSkinObjectBrowser.this.browser.getData("CurrentURL");
/*     */           
/* 451 */           if (url != null)
/*     */           {
/* 453 */             url = proxy.proxifyURL(url);
/*     */             
/* 455 */             SWTSkinObjectBrowser.this.browser.setData("StartURL", url);
/*     */             
/* 457 */             SWTSkinObjectBrowser.this.browser.setUrl(url);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void enablePluginProxy(String reason)
/*     */   {
/* 468 */     this.use_generic_proxy = true;
/* 469 */     this.proxy_reason = reason;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAutoReloadPending(boolean is_pending, boolean aborted)
/*     */   {
/* 477 */     this.autoReloadPending = is_pending;
/* 478 */     BrowserContext bc = this.context;
/* 479 */     if (bc != null) {
/* 480 */       bc.setAutoReloadPending(is_pending, aborted);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isPageLoading() {
/* 485 */     return this.context == null ? false : this.context.isPageLoading();
/*     */   }
/*     */   
/*     */   public boolean setIsVisible(final boolean visible, boolean walkup)
/*     */   {
/* 490 */     boolean changed = super.setIsVisible(visible, walkup);
/*     */     
/* 492 */     if (changed)
/*     */     {
/* 494 */       Utils.execSWTThreadLater(0, new AERunnable() {
/*     */         public void runSupport() {
/* 496 */           if ((!SWTSkinObjectBrowser.this.isDisposed()) && (SWTSkinObjectBrowser.this.context != null)) {
/* 497 */             SWTSkinObjectBrowser.this.context.sendBrowserMessage("browser", visible ? "shown" : "hidden");
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 502 */     return changed;
/*     */   }
/*     */   
/*     */   public void addListener(BrowserContext.loadingListener l) {
/* 506 */     if (this.context != null) {
/* 507 */       this.context.addListener(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh() {
/* 512 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 514 */         if ((SWTSkinObjectBrowser.this.browser != null) && (!SWTSkinObjectBrowser.this.browser.isDisposed())) {
/* 515 */           SWTSkinObjectBrowser.this.browser.refresh();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void dispose()
/*     */   {
/* 523 */     if (this.generic_proxy != null) {
/* 524 */       this.generic_proxy.destroy();
/* 525 */       this.generic_proxy = null;
/*     */     }
/* 527 */     super.dispose();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectBrowser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */