/*     */ package com.aelitis.azureus.ui.swt.browser;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext.torrentURLHandler;
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContextImpl;
/*     */ import com.aelitis.azureus.core.messenger.browser.listeners.BrowserMessageListener;
/*     */ import com.aelitis.azureus.core.messenger.config.PlatformConfigMessenger;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.ui.swt.browser.msg.MessageDispatcherSWT;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.browser.CloseWindowListener;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.browser.OpenWindowListener;
/*     */ import org.eclipse.swt.browser.ProgressEvent;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.browser.TitleEvent;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.browser.WindowEvent;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BrowserContext
/*     */   extends ClientMessageContextImpl
/*     */   implements DisposeListener
/*     */ {
/*     */   private static final String CONTEXT_KEY = "BrowserContext";
/*     */   private static final String KEY_ENABLE_MENU = "browser.menu.enable";
/*     */   private BrowserWrapper browser;
/*     */   private Display display;
/*  78 */   private boolean pageLoading = false;
/*     */   
/*  80 */   private long pageLoadingStart = 0L;
/*     */   
/*  82 */   private long pageLoadingEnd = 0L;
/*     */   
/*  84 */   private String lastValidURL = null;
/*     */   
/*     */   private final boolean forceVisibleAfterLoad;
/*     */   
/*     */   private TimerEventPeriodic checkURLEvent;
/*     */   
/*     */   private Control widgetWaitIndicator;
/*     */   
/*     */   private MessageDispatcherSWT messageDispatcherSWT;
/*     */   
/*  94 */   protected boolean wiggleBrowser = Utils.isCarbon;
/*     */   
/*     */   private ClientMessageContext.torrentURLHandler torrentURLHandler;
/*     */   
/*  98 */   private List loadingListeners = Collections.EMPTY_LIST;
/*     */   
/*     */   private long pageLoadTime;
/*     */   
/* 102 */   private long contentNetworkID = 1L;
/*     */   
/* 104 */   private AEMonitor mon_listJS = new AEMonitor("listJS");
/*     */   
/* 106 */   private List<String> listJS = new ArrayList(1);
/*     */   
/* 108 */   private boolean allowPopups = true;
/*     */   
/* 110 */   private volatile boolean autoReloadPending = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String[] lastRetryData;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BrowserContext(String _id, BrowserWrapper _browser, Control _widgetWaitingIndicator, boolean _forceVisibleAfterLoad)
/*     */   {
/* 126 */     super(_id, null);
/*     */     
/* 128 */     this.browser = _browser;
/* 129 */     this.forceVisibleAfterLoad = _forceVisibleAfterLoad;
/* 130 */     this.widgetWaitIndicator = _widgetWaitingIndicator;
/*     */     
/*     */ 
/*     */ 
/* 134 */     this.messageDispatcherSWT = new MessageDispatcherSWT(this);
/*     */     
/* 136 */     setMessageDispatcher(this.messageDispatcherSWT);
/*     */     
/* 138 */     final TimerEventPerformer showBrowersPerformer = new TimerEventPerformer() {
/*     */       public void perform(TimerEvent event) {
/* 140 */         if ((BrowserContext.this.browser != null) && (!BrowserContext.this.browser.isDisposed())) {
/* 141 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/* 143 */               if ((BrowserContext.this.forceVisibleAfterLoad) && (BrowserContext.this.browser != null) && (!BrowserContext.this.browser.isDisposed()) && (!BrowserContext.this.browser.isVisible()))
/*     */               {
/* 145 */                 BrowserContext.this.browser.setVisible(true);
/*     */               }
/*     */               
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/* 152 */     };
/* 153 */     final TimerEventPerformer hideIndicatorPerformer = new TimerEventPerformer() {
/*     */       public void perform(TimerEvent event) {
/* 155 */         BrowserContext.this.setPageLoading(false, BrowserContext.this.browser.getUrl());
/* 156 */         if ((BrowserContext.this.widgetWaitIndicator != null) && (!BrowserContext.this.widgetWaitIndicator.isDisposed())) {
/* 157 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/* 159 */               if ((BrowserContext.this.widgetWaitIndicator != null) && (!BrowserContext.this.widgetWaitIndicator.isDisposed()))
/*     */               {
/* 161 */                 BrowserContext.this.widgetWaitIndicator.setVisible(false);
/*     */               }
/*     */               
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/* 168 */     };
/* 169 */     final TimerEventPerformer checkURLEventPerformer = new TimerEventPerformer() {
/*     */       public void perform(TimerEvent event) {
/* 171 */         if ((BrowserContext.this.browser != null) && (!BrowserContext.this.browser.isDisposed())) {
/* 172 */           Utils.execSWTThreadLater(0, new AERunnable() {
/*     */             public void runSupport() {
/* 174 */               if ((BrowserContext.this.browser != null) && (!BrowserContext.this.browser.isDisposed())) {
/* 175 */                 BrowserContext.this.browser.execute("try { tuxLocString = document.location.toString();if (tuxLocString.indexOf('res://') == 0) {  document.title = 'err: ' + tuxLocString;} else {  tuxTitleString = document.title.toString();  if (tuxTitleString.indexOf('408 ') == 0 || tuxTitleString.indexOf('503 ') == 0 || tuxTitleString.indexOf('500 ') == 0)   { document.title = 'err: ' + tuxTitleString; } }} catch (e) { }");
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 191 */     if (this.forceVisibleAfterLoad)
/*     */     {
/* 193 */       this.browser.setVisible(false);
/*     */     }
/*     */     
/* 196 */     setPageLoading(false, this.browser.getUrl());
/*     */     
/* 198 */     if ((this.widgetWaitIndicator != null) && (!this.widgetWaitIndicator.isDisposed())) {
/* 199 */       this.widgetWaitIndicator.setVisible(false);
/*     */     }
/*     */     
/* 202 */     this.browser.addTitleListener(new TitleListener()
/*     */     {
/*     */ 
/*     */       public void changed(TitleEvent event)
/*     */       {
/*     */ 
/* 208 */         if ((BrowserContext.this.browser == null) || (BrowserContext.this.browser.isDisposed()) || (BrowserContext.this.browser.getShell().isDisposed())) {
/* 209 */           return;
/*     */         }
/*     */         
/* 212 */         if (!BrowserContext.this.browser.isVisible()) {
/* 213 */           SimpleTimer.addEvent("Show Browser", System.currentTimeMillis() + 700L, showBrowersPerformer);
/*     */         }
/*     */         
/* 216 */         if (event.title.startsWith("err: ")) {
/* 217 */           BrowserContext.this.fillWithRetry(event.title, "err in title");
/*     */         }
/*     */         
/*     */       }
/* 221 */     });
/* 222 */     this.browser.addProgressListener(new ProgressListener()
/*     */     {
/*     */       public void changed(ProgressEvent event) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void completed(ProgressEvent event)
/*     */       {
/* 232 */         if ((BrowserContext.this.browser == null) || (BrowserContext.this.browser.isDisposed()) || (BrowserContext.this.browser.getShell().isDisposed())) {
/* 233 */           return;
/*     */         }
/*     */         
/* 236 */         checkURLEventPerformer.perform(null);
/* 237 */         if ((BrowserContext.this.forceVisibleAfterLoad) && (!BrowserContext.this.browser.isVisible())) {
/* 238 */           BrowserContext.this.browser.setVisible(true);
/*     */         }
/*     */         
/* 241 */         BrowserContext.this.browser.execute("try { if (azureusClientWelcome) { azureusClientWelcome('" + ConstantsVuze.AZID + "'," + "{ 'azv':'" + "5.7.6.0" + "', 'browser-id':'" + BrowserContext.this.getID() + "' }" + ");} } catch (e) { }");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 248 */         if ((Constants.isCVSVersion()) || (System.getProperty("debug.https", null) != null))
/*     */         {
/* 250 */           if (BrowserContext.this.browser.getUrl().indexOf("https") == 0) {
/* 251 */             BrowserContext.this.browser.execute("try { o = document.getElementsByTagName('body'); if (o) o[0].style.borderTop = '2px dotted #3b3b3b'; } catch (e) {}");
/*     */           }
/*     */         }
/*     */         
/* 255 */         if (BrowserContext.this.wiggleBrowser) {
/* 256 */           Shell shell = BrowserContext.this.browser.getShell();
/* 257 */           Point size = shell.getSize();
/* 258 */           size.x -= 1;
/* 259 */           size.y -= 1;
/* 260 */           shell.setSize(size);
/* 261 */           size.x += 1;
/* 262 */           size.y += 1;
/* 263 */           shell.setSize(size);
/*     */         }
/*     */         
/*     */       }
/* 267 */     });
/* 268 */     this.checkURLEvent = SimpleTimer.addPeriodicEvent("checkURL", 10000L, checkURLEventPerformer);
/*     */     
/*     */ 
/*     */ 
/* 272 */     this.browser.addOpenWindowListener(new OpenWindowListener() {
/*     */       public void open(WindowEvent event) {
/* 274 */         if ((BrowserContext.this.browser == null) || (BrowserContext.this.browser.isDisposed()) || (BrowserContext.this.browser.getShell().isDisposed())) {
/* 275 */           return;
/*     */         }
/* 277 */         event.required = true;
/*     */         
/* 279 */         if (BrowserContext.this.browser.getUrl().contains("js.debug=1")) {
/* 280 */           final Shell shell = ShellFactory.createMainShell(1264);
/* 281 */           shell.setLayout(new FillLayout());
/* 282 */           shell.setSize(920, 500);
/* 283 */           BrowserWrapper subBrowser = BrowserWrapper.createBrowser(shell, Utils.getInitialBrowserStyle(0));
/*     */           
/* 285 */           subBrowser.addCloseWindowListener(new CloseWindowListener() {
/*     */             public void close(WindowEvent event) {
/* 287 */               shell.dispose();
/*     */             }
/* 289 */           });
/* 290 */           shell.open();
/* 291 */           subBrowser.setBrowser(event);
/*     */         }
/*     */         else
/*     */         {
/* 295 */           final BrowserWrapper subBrowser = BrowserWrapper.createBrowser(BrowserContext.this.browser.getControl(), Utils.getInitialBrowserStyle(0));
/*     */           
/* 297 */           subBrowser.addLocationListener(new LocationListener()
/*     */           {
/*     */             public void changed(LocationEvent arg0) {}
/*     */             
/*     */             public void changing(LocationEvent event)
/*     */             {
/* 303 */               event.doit = false;
/* 304 */               boolean doLinkExternally = PlatformConfigMessenger.areLinksExternal(BrowserContext.this.browser.getUrl());
/* 305 */               if (doLinkExternally) {
/* 306 */                 Utils.launch(event.location);
/* 307 */               } else if ((BrowserContext.this.allowPopups()) && (!UrlFilter.getInstance().urlIsBlocked(event.location)) && ((event.location.startsWith("http://")) || (event.location.startsWith("https://"))))
/*     */               {
/*     */ 
/* 310 */                 BrowserContext.this.debug("open sub browser: " + event.location);
/* 311 */                 Utils.launch(event.location);
/*     */               } else {
/* 313 */                 BrowserContext.this.debug("blocked open sub browser: " + event.location);
/*     */               }
/*     */               
/*     */ 
/* 317 */               Utils.execSWTThreadLater(1000, new AERunnable() {
/*     */                 public void runSupport() {
/* 319 */                   BrowserContext.6.2.this.val$subBrowser.dispose();
/*     */                 }
/*     */               });
/*     */             }
/* 323 */           });
/* 324 */           subBrowser.setBrowser(event);
/*     */         }
/*     */         
/*     */       }
/* 328 */     });
/* 329 */     final BrowserWrapper bw = this.browser;
/*     */     
/* 331 */     this.browser.addLocationListener(new LocationListener() {
/*     */       private TimerEvent timerevent;
/*     */       
/*     */       public void changed(LocationEvent event) {
/* 335 */         if ((BrowserContext.this.browser.isDisposed()) || (BrowserContext.this.browser.getShell().isDisposed())) {
/* 336 */           return;
/*     */         }
/* 338 */         BrowserContext.this.debug("browser.changed " + event.location);
/* 339 */         if (this.timerevent != null) {
/* 340 */           this.timerevent.cancel();
/*     */         }
/* 342 */         checkURLEventPerformer.perform(null);
/* 343 */         BrowserContext.this.setPageLoading(false, event.top ? event.location : null);
/* 344 */         if ((BrowserContext.this.widgetWaitIndicator != null) && (!BrowserContext.this.widgetWaitIndicator.isDisposed())) {
/* 345 */           BrowserContext.this.widgetWaitIndicator.setVisible(false);
/*     */         }
/*     */         
/*     */ 
/* 349 */         if (!event.top) {
/* 350 */           return;
/*     */         }
/* 352 */         String location = event.location.toLowerCase();
/* 353 */         boolean isWebURL = (location.startsWith("http://")) || (location.startsWith("https://"));
/*     */         
/* 355 */         if ((!isWebURL) && 
/* 356 */           (event.location.startsWith("res://"))) {
/* 357 */           BrowserContext.this.fillWithRetry(event.location, "top changed");
/* 358 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 363 */         if (UrlFilter.getInstance().isWhitelisted(event.location)) {
/* 364 */           BrowserContext.this.lastValidURL = event.location;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void changing(LocationEvent event)
/*     */       {
/* 372 */         BrowserContext.this.debug("browser.changing " + event.location + " from " + BrowserContext.this.browser.getUrl() + ";" + event.top);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 377 */         if ((BrowserContext.this.browser.isDisposed()) || (BrowserContext.this.browser.getShell().isDisposed())) {
/* 378 */           return;
/*     */         }
/*     */         
/* 381 */         String event_location = event.location;
/*     */         
/*     */ 
/*     */ 
/* 385 */         if ((event_location.startsWith("javascript")) && (event_location.indexOf("back()") > 0))
/*     */         {
/* 387 */           if (BrowserContext.this.browser.isBackEnabled()) {
/* 388 */             BrowserContext.this.browser.back();
/* 389 */           } else if (BrowserContext.this.lastValidURL != null) {
/* 390 */             BrowserContext.this.fillWithRetry(event_location, "back");
/*     */           }
/* 392 */           return;
/*     */         }
/*     */         
/*     */ 
/* 396 */         String lowerLocation = event_location.toLowerCase();
/* 397 */         boolean isOurURI = (lowerLocation.startsWith("magnet:")) || (lowerLocation.startsWith("vuze:")) || (lowerLocation.startsWith("bc:")) || (lowerLocation.startsWith("bctp:")) || (lowerLocation.startsWith("dht:"));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 403 */         if (isOurURI) {
/* 404 */           event.doit = false;
/* 405 */           TorrentOpener.openTorrent(event_location);
/* 406 */           return;
/*     */         }
/*     */         
/* 409 */         boolean isWebURL = (lowerLocation.startsWith("http://")) || (lowerLocation.startsWith("https://"));
/*     */         
/* 411 */         if (!isWebURL)
/*     */         {
/* 413 */           return;
/*     */         }
/*     */         
/* 416 */         boolean blocked = UrlFilter.getInstance().urlIsBlocked(event_location);
/*     */         
/* 418 */         if (!BrowserContext.this.allowPopups()) {
/* 419 */           if (blocked) {
/* 420 */             return;
/*     */           }
/*     */           
/* 423 */           String curURL = BrowserContext.this.browser.getUrl().toLowerCase();
/*     */           
/* 425 */           boolean isPageLoadingOrRecent = (BrowserContext.this.isPageLoading()) || ((BrowserContext.this.pageLoadingEnd > 0L) && (BrowserContext.this.pageLoadingEnd + 500L > SystemTime.getCurrentTime())) || (event_location.contains(".admonkey."));
/*     */           
/*     */ 
/*     */ 
/* 429 */           boolean wasSearch = (curURL.startsWith("http://www.google.com/#q")) || (curURL.startsWith("http://www.google.com/search")) || (PlatformConfigMessenger.areLinksExternal(curURL));
/*     */           
/*     */ 
/*     */ 
/* 433 */           boolean isSearch = (event_location.startsWith("http://www.google.com/#q")) || (event_location.startsWith("http://www.google.com/search")) || (PlatformConfigMessenger.areLinksExternal(event_location));
/*     */           
/*     */ 
/*     */ 
/* 437 */           if ((wasSearch) && (!isSearch) && (!curURL.equalsIgnoreCase(event_location)) && (!event_location.equals("about:blank")) && (!isPageLoadingOrRecent))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 442 */             event.doit = false;
/* 443 */             String[] contentTypes = BrowserContext.this.getContentTypes(event_location, bw.getUrl());
/*     */             
/* 445 */             boolean isTorrent = false;
/* 446 */             for (String s : contentTypes)
/*     */             {
/* 448 */               if (s != null)
/*     */               {
/* 450 */                 if (s.contains("torrent"))
/*     */                 {
/* 452 */                   isTorrent = true;
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 457 */             if ((!isTorrent) || (!BrowserContext.this.openTorrent(bw, event))) {
/* 458 */               Utils.launch(event.location);
/*     */             }
/* 460 */             return;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 465 */         if (blocked) {
/* 466 */           event.doit = false;
/* 467 */           new MessageBoxShell(32, "URL blocked", "Tried to open " + event_location + " but it's blocked").open(null);
/*     */           
/* 469 */           BrowserContext.this.browser.back();
/*     */         } else {
/* 471 */           if (UrlFilter.getInstance().isWhitelisted(event_location)) {
/* 472 */             BrowserContext.this.lastValidURL = event_location;
/*     */           }
/* 474 */           BrowserContext.this.setPageLoading(true, event.location);
/* 475 */           if (event.top) {
/* 476 */             if ((BrowserContext.this.widgetWaitIndicator != null) && (!BrowserContext.this.widgetWaitIndicator.isDisposed())) {
/* 477 */               BrowserContext.this.widgetWaitIndicator.setVisible(true);
/*     */             }
/*     */             
/*     */ 
/* 481 */             this.timerevent = SimpleTimer.addEvent("Hide Indicator", System.currentTimeMillis() + 20000L, hideIndicatorPerformer);
/*     */           }
/*     */           else {
/* 484 */             boolean isTorrent = false;
/* 485 */             boolean isVuzeFile = false;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 491 */             if ((event_location.endsWith(".torrent")) || (event_location.endsWith("?torrent"))) {
/* 492 */               isTorrent = true;
/*     */             }
/*     */             else
/*     */             {
/* 496 */               boolean can_rpc = UrlFilter.getInstance().urlCanRPC(event_location);
/*     */               
/* 498 */               boolean test_for_torrent = (!can_rpc) && (!event_location.contains(".htm"));
/*     */               
/* 500 */               boolean test_for_vuze = (can_rpc) && ((event_location.endsWith(".xml")) || (event_location.endsWith(".vuze")));
/*     */               
/* 502 */               if ((test_for_torrent) || (test_for_vuze))
/*     */               {
/* 504 */                 String[] contentTypes = BrowserContext.this.getContentTypes(event_location, bw.getUrl());
/*     */                 
/* 506 */                 for (String s : contentTypes)
/*     */                 {
/* 508 */                   if (s != null)
/*     */                   {
/* 510 */                     if ((test_for_torrent) && (s.contains("torrent")))
/*     */                     {
/* 512 */                       isTorrent = true;
/*     */                     }
/*     */                     
/* 515 */                     if ((test_for_vuze) && (s.contains("vuze")))
/*     */                     {
/* 517 */                       isVuzeFile = true;
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 527 */             if (isTorrent)
/*     */             {
/* 529 */               BrowserContext.this.openTorrent(bw, event);
/*     */             }
/* 531 */             else if (isVuzeFile)
/*     */             {
/* 533 */               event.doit = false;
/* 534 */               BrowserContext.this.setPageLoading(false, event.location);
/*     */               try
/*     */               {
/* 537 */                 String referer_str = null;
/*     */                 try
/*     */                 {
/* 540 */                   referer_str = new URL(bw.getUrl()).toExternalForm();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */                 
/*     */ 
/* 545 */                 Map headers = UrlUtils.getBrowserHeaders(referer_str);
/*     */                 
/* 547 */                 String cookies = (String)bw.getData("current-cookies");
/*     */                 
/* 549 */                 if (cookies != null)
/*     */                 {
/* 551 */                   headers.put("Cookie", cookies);
/*     */                 }
/*     */                 
/* 554 */                 ResourceDownloader rd = StaticUtilities.getResourceDownloaderFactory().create(new URL(event_location));
/*     */                 
/* 556 */                 VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*     */                 
/* 558 */                 VuzeFile vf = vfh.loadVuzeFile(rd.download());
/*     */                 
/* 560 */                 if (vf == null)
/*     */                 {
/* 562 */                   event.doit = true;
/* 563 */                   BrowserContext.this.setPageLoading(true, event.location);
/*     */                 }
/*     */                 else
/*     */                 {
/* 567 */                   vfh.handleFiles(new VuzeFile[] { vf }, 0);
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 571 */                 e.printStackTrace();
/*     */               }
/*     */               
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 578 */     });
/* 579 */     this.browser.setData("BrowserContext", this);
/* 580 */     this.browser.addDisposeListener(this);
/*     */     
/*     */ 
/* 583 */     final boolean enableMenu = System.getProperty("browser.menu.enable", "0").equals("1");
/*     */     
/* 585 */     this.browser.addListener(35, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 588 */         event.doit = enableMenu;
/*     */       }
/*     */       
/* 591 */     });
/* 592 */     this.messageDispatcherSWT.registerBrowser(this.browser);
/* 593 */     this.display = this.browser.getDisplay();
/*     */   }
/*     */   
/*     */   protected boolean openTorrent(BrowserWrapper browser, LocationEvent event) {
/* 597 */     event.doit = false;
/* 598 */     setPageLoading(false, event.location);
/*     */     try
/*     */     {
/* 601 */       String referer_str = null;
/*     */       try
/*     */       {
/* 604 */         referer_str = new URL(browser.getUrl()).toExternalForm();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 609 */       final Map headers = UrlUtils.getBrowserHeaders(referer_str);
/*     */       
/*     */ 
/* 612 */       String cookies = (String)browser.getData("current-cookies");
/*     */       
/* 614 */       if (cookies != null)
/*     */       {
/* 616 */         headers.put("Cookie", cookies);
/*     */       }
/*     */       
/* 619 */       final String url = event.location;
/*     */       
/* 621 */       if (this.torrentURLHandler != null) {
/*     */         try
/*     */         {
/* 624 */           this.torrentURLHandler.handleTorrentURL(url);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 628 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 632 */       Utils.getOffOfSWTThread(new AERunnable()
/*     */       {
/*     */         public void runSupport() {
/*     */           try {
/* 636 */             PluginInitializer.getDefaultInterface().getDownloadManager().addDownload(new URL(url), headers);
/*     */           }
/*     */           catch (Exception e) {
/* 639 */             Debug.out(e);
/*     */           }
/*     */           
/*     */         }
/* 643 */       });
/* 644 */       return true;
/*     */     } catch (Throwable e) {
/* 646 */       Debug.out(e); }
/* 647 */     return false;
/*     */   }
/*     */   
/*     */   protected String[] getContentTypes(String event_location, String _referer)
/*     */   {
/*     */     try
/*     */     {
/* 654 */       URL url = new URL(event_location);
/* 655 */       URLConnection conn = url.openConnection();
/*     */       
/*     */ 
/*     */ 
/* 659 */       ((HttpURLConnection)conn).setRequestMethod("HEAD");
/*     */       
/* 661 */       String referer_str = null;
/*     */       try
/*     */       {
/* 664 */         URL referer = new URL(_referer);
/*     */         
/* 666 */         if (referer != null)
/*     */         {
/* 668 */           referer_str = referer.toExternalForm();
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/*     */ 
/* 675 */       UrlUtils.setBrowserHeaders(conn, referer_str);
/*     */       
/* 677 */       UrlUtils.connectWithTimeouts(conn, 1500L, 5000L);
/*     */       
/* 679 */       String contentType = conn.getContentType();
/* 680 */       String contentDisposition = conn.getHeaderField("Content-Disposition");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 685 */       String server = conn.getHeaderField("Server");
/* 686 */       if (("application/x-bittorrent".equals(contentType)) && (":3".equals(server))) {
/* 687 */         Thread.sleep(6000L);
/*     */       }
/*     */       
/* 690 */       return new String[] { contentType, contentDisposition };
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 697 */     return new String[0];
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
/*     */   protected void setPageLoading(boolean b, String url)
/*     */   {
/* 710 */     if ((b) && (this.pageLoading)) {
/* 711 */       return;
/*     */     }
/* 713 */     this.mon_listJS.enter();
/*     */     try {
/* 715 */       this.pageLoading = b;
/* 716 */       if (this.pageLoading) {
/* 717 */         this.pageLoadingStart = SystemTime.getCurrentTime();
/* 718 */         this.pageLoadTime = -1L;
/* 719 */       } else if ((this.pageLoadingStart > 0L) && (url != null)) {
/* 720 */         this.pageLoadingEnd = SystemTime.getCurrentTime();
/* 721 */         this.pageLoadTime = (this.pageLoadingEnd - this.pageLoadingStart);
/* 722 */         executeInBrowser("clientSetLoadTime(" + this.pageLoadTime + ");");
/*     */         
/* 724 */         this.pageLoadingStart = 0L;
/*     */       }
/* 726 */       if ((!this.pageLoading) && (this.listJS.size() > 0)) {
/* 727 */         debug(this.listJS.size() + " javascripts queued.  Executing now..");
/* 728 */         for (String js : this.listJS) {
/* 729 */           executeInBrowser(js);
/*     */         }
/* 731 */         this.listJS.clear();
/*     */       }
/*     */     } finally {
/* 734 */       this.mon_listJS.exit();
/*     */     }
/*     */     
/* 737 */     Object[] listeners = this.loadingListeners.toArray();
/* 738 */     for (int i = 0; i < listeners.length; i++) {
/* 739 */       loadingListener l = (loadingListener)listeners[i];
/* 740 */       l.browserLoadingChanged(b, url);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTorrentURLHandler(ClientMessageContext.torrentURLHandler handler)
/*     */   {
/* 748 */     this.torrentURLHandler = handler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAutoReloadPending(final boolean is_pending, final boolean aborted)
/*     */   {
/* 756 */     this.autoReloadPending = is_pending;
/*     */     
/* 758 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 764 */         if (!is_pending)
/*     */         {
/* 766 */           if (aborted)
/*     */           {
/* 768 */             if (BrowserContext.this.lastRetryData != null)
/*     */             {
/* 770 */               if (!BrowserContext.this.browser.isDisposed())
/*     */               {
/* 772 */                 BrowserContext.this.fillWithRetry(BrowserContext.this.lastRetryData[0], BrowserContext.this.lastRetryData[1]);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void fillWithRetry(String s, String s2)
/*     */   {
/* 783 */     Color bg = this.browser.getDisplay().getSystemColor(22);
/* 784 */     Color fg = this.browser.getDisplay().getSystemColor(21);
/*     */     
/* 786 */     if (this.autoReloadPending)
/*     */     {
/* 788 */       this.lastRetryData = new String[] { s, s2 };
/*     */       
/* 790 */       this.browser.setText("<html><body style='overflow:auto; font-family: verdana; font-size: 10pt' bgcolor=#" + Utils.toColorHexString(bg) + " text=#" + Utils.toColorHexString(fg) + ">" + "<br>Please wait while Vuze attempts to load the page (this can take a moment or two initially) ...<br>" + "</body></html>");
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 797 */       this.browser.setText("<html><body style='overflow:auto; font-family: verdana; font-size: 10pt' bgcolor=#" + Utils.toColorHexString(bg) + " text=#" + Utils.toColorHexString(fg) + ">" + "<br>Sorry, there was a problem loading this page.<br> " + "Please check if your internet connection is working and click <a href='" + this.lastValidURL + "' style=\"color: rgb(100, 155, 255); \">retry</a> to continue." + "<div style='word-wrap: break-word'><font size=1 color=#" + Utils.toColorHexString(bg) + ">" + s + "<br><br>" + s2 + "</font></div>" + "</body></html>");
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
/*     */   private void deregisterBrowser()
/*     */   {
/* 815 */     if (this.browser == null) {
/* 816 */       throw new IllegalStateException("Context " + getID() + " doesn't have a registered browser");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 822 */     if (!this.browser.isDisposed()) {
/* 823 */       this.browser.setData("BrowserContext", null);
/* 824 */       this.browser.removeDisposeListener(this);
/* 825 */       this.messageDispatcherSWT.deregisterBrowser(this.browser);
/*     */     }
/* 827 */     this.browser = null;
/*     */     
/* 829 */     if ((this.checkURLEvent != null) && (!this.checkURLEvent.isCancelled())) {
/* 830 */       this.checkURLEvent.cancel();
/* 831 */       this.checkURLEvent = null;
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
/*     */   public void addMessageListener(BrowserMessageListener listener)
/*     */   {
/* 855 */     this.messageDispatcherSWT.addListener(listener);
/*     */   }
/*     */   
/*     */   public Object getBrowserData(String key) {
/* 859 */     return this.browser.getData(key);
/*     */   }
/*     */   
/*     */   public void setBrowserData(String key, Object value) {
/* 863 */     this.browser.setData(key, value);
/*     */   }
/*     */   
/*     */   public boolean sendBrowserMessage(String key, String op) {
/* 867 */     return sendBrowserMessage(key, op, (Map)null);
/*     */   }
/*     */   
/*     */   public boolean sendBrowserMessage(String key, String op, Map params) {
/* 871 */     StringBuilder msg = new StringBuilder();
/* 872 */     msg.append("az.msg.dispatch('").append(key).append("', '").append(op).append("'");
/*     */     
/* 874 */     if (params != null) {
/* 875 */       msg.append(", ").append(JSONUtils.encodeToJSON(params));
/*     */     }
/* 877 */     msg.append(")");
/*     */     
/* 879 */     return executeInBrowser(msg.toString());
/*     */   }
/*     */   
/*     */   public boolean sendBrowserMessage(String key, String op, Collection params) {
/* 883 */     StringBuilder msg = new StringBuilder();
/* 884 */     msg.append("az.msg.dispatch('").append(key).append("', '").append(op).append("'");
/*     */     
/* 886 */     if (params != null) {
/* 887 */       msg.append(", ").append(JSONUtils.encodeToJSON(params));
/*     */     }
/* 889 */     msg.append(")");
/*     */     
/* 891 */     return executeInBrowser(msg.toString());
/*     */   }
/*     */   
/*     */   protected boolean maySend(String key, String op, Map params) {
/* 895 */     return !this.pageLoading;
/*     */   }
/*     */   
/*     */   public boolean executeInBrowser(final String javascript) {
/* 899 */     this.mon_listJS.enter();
/*     */     try {
/* 901 */       if (!mayExecute(javascript)) {
/* 902 */         this.listJS.add(javascript);
/* 903 */         return false;
/*     */       }
/*     */     } finally {
/* 906 */       this.mon_listJS.exit();
/*     */     }
/*     */     
/* 909 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 910 */       debug("CANNOT: browser.execute( " + getShortJavascript(javascript) + " )");
/* 911 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 915 */     final String reallyExecute = "try { " + javascript + " } catch ( e ) { }";
/* 916 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 918 */         if ((BrowserContext.this.browser == null) || (BrowserContext.this.browser.isDisposed())) {
/* 919 */           BrowserContext.this.debug("CANNOT: browser.execute( " + BrowserContext.this.getShortJavascript(javascript) + " )");
/*     */         }
/* 921 */         else if (!BrowserContext.this.browser.execute(reallyExecute)) {
/* 922 */           BrowserContext.this.debug("FAILED: browser.execute( " + BrowserContext.this.getShortJavascript(javascript) + " )");
/*     */         }
/*     */         else {
/* 925 */           BrowserContext.this.debug("SUCCESS: browser.execute( " + BrowserContext.this.getShortJavascript(javascript) + " )");
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 930 */     });
/* 931 */     return true;
/*     */   }
/*     */   
/*     */   protected boolean mayExecute(String javascript) {
/* 935 */     return !this.pageLoading;
/*     */   }
/*     */   
/*     */   public void widgetDisposed(DisposeEvent event) {
/* 939 */     if (event.widget == this.browser.getControl()) {
/* 940 */       deregisterBrowser();
/*     */     }
/*     */   }
/*     */   
/*     */   private String getShortJavascript(String javascript) {
/* 945 */     if (javascript.length() < 515) {
/* 946 */       return javascript;
/*     */     }
/* 948 */     StringBuilder result = new StringBuilder();
/* 949 */     result.append(javascript.substring(0, 256));
/* 950 */     result.append("...");
/* 951 */     result.append(javascript.substring(javascript.length() - 256));
/* 952 */     return result.toString();
/*     */   }
/*     */   
/*     */   public void setWiggleBrowser(boolean wiggleBrowser) {
/* 956 */     this.wiggleBrowser = wiggleBrowser;
/*     */   }
/*     */   
/*     */   public boolean isPageLoading() {
/* 960 */     return this.pageLoading;
/*     */   }
/*     */   
/*     */   public void addListener(loadingListener l)
/*     */   {
/* 965 */     if (this.loadingListeners == Collections.EMPTY_LIST) {
/* 966 */       this.loadingListeners = new ArrayList(1);
/*     */     }
/* 968 */     this.loadingListeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getContentNetworkID()
/*     */   {
/* 976 */     return this.contentNetworkID;
/*     */   }
/*     */   
/*     */   public void setContentNetworkID(long contentNetworkID) {
/* 980 */     this.contentNetworkID = contentNetworkID;
/*     */   }
/*     */   
/*     */   public void setAllowPopups(boolean allowPopups) {
/* 984 */     this.allowPopups = allowPopups;
/*     */   }
/*     */   
/*     */   public boolean allowPopups() {
/* 988 */     return this.allowPopups;
/*     */   }
/*     */   
/*     */   public static abstract interface loadingListener
/*     */   {
/*     */     public abstract void browserLoadingChanged(boolean paramBoolean, String paramString);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/BrowserContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */