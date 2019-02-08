/*     */ package com.aelitis.azureus.ui.swt.search;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.config.PlatformConfigMessenger;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginHTTPProxy;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
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
/*     */ public class SearchResultsTabAreaBrowser
/*     */   implements SearchResultsTabAreaBase
/*     */ {
/*     */   private static boolean search_proxy_init_done;
/*     */   private static AEProxyFactory.PluginHTTPProxy search_proxy;
/*     */   private static boolean search_proxy_set;
/*  82 */   private static AESemaphore search_proxy_sem = new AESemaphore("sps");
/*     */   
/*  84 */   private static List<SearchResultsTabAreaBrowser> pending = new ArrayList();
/*     */   private final SearchResultsTabArea parent;
/*     */   private SWTSkinObjectBrowser browserSkinObject;
/*     */   private SearchResultsTabArea.SearchQuery sq;
/*     */   
/*  89 */   private static void initProxy() { synchronized (SearchResultsTabArea.class)
/*     */     {
/*  91 */       if (search_proxy_init_done)
/*     */       {
/*  93 */         return;
/*     */       }
/*     */       
/*  96 */       search_proxy_init_done = true;
/*     */     }
/*     */     
/*  99 */     new AEThread2("ST_test")
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 106 */           String test_url = PlatformConfigMessenger.getWebSearchUrl().replaceAll("%s", "derp");
/*     */           try
/*     */           {
/* 109 */             URL url = new URL(test_url);
/*     */             
/* 111 */             url = UrlUtils.setProtocol(url, "https");
/*     */             
/* 113 */             url = UrlUtils.setPort(url, 443);
/*     */             
/* 115 */             boolean use_proxy = !COConfigurationManager.getStringParameter("browser.internal.proxy.id", "none").equals("none");
/*     */             
/* 117 */             if (!use_proxy)
/*     */             {
/* 119 */               Boolean looks_ok = AEProxyFactory.testPluginHTTPProxy(url, true);
/*     */               
/* 121 */               use_proxy = (looks_ok != null) && (!looks_ok.booleanValue());
/*     */             }
/*     */             
/* 124 */             if (use_proxy)
/*     */             {
/* 126 */               SearchResultsTabAreaBrowser.access$002(AEProxyFactory.getPluginHTTPProxy("search", url, true));
/*     */               
/* 128 */               if (SearchResultsTabAreaBrowser.search_proxy != null)
/*     */               {
/* 130 */                 UrlFilter.getInstance().addUrlWhitelist("https?://" + ((InetSocketAddress)SearchResultsTabAreaBrowser.search_proxy.getProxy().address()).getAddress().getHostAddress() + ":?[0-9]*/.*"); }
/*     */             }
/*     */           } catch (Throwable e) {}
/*     */         } finally { List<SearchResultsTabAreaBrowser> to_redo;
/*     */           Iterator i$;
/*     */           SearchResultsTabAreaBrowser area;
/*     */           SearchResultsTabArea.SearchQuery sq;
/* 137 */           List<SearchResultsTabAreaBrowser> to_redo = null;
/*     */           
/* 139 */           synchronized (SearchResultsTabArea.class)
/*     */           {
/* 141 */             SearchResultsTabAreaBrowser.access$102(true);
/*     */             
/* 143 */             to_redo = new ArrayList(SearchResultsTabAreaBrowser.pending);
/*     */             
/* 145 */             SearchResultsTabAreaBrowser.pending.clear();
/*     */           }
/*     */           
/* 148 */           SearchResultsTabAreaBrowser.search_proxy_sem.releaseForever();
/*     */           
/* 150 */           for (SearchResultsTabAreaBrowser area : to_redo) {
/*     */             try
/*     */             {
/*     */               try {
/* 154 */                 area.browserSkinObject.setAutoReloadPending(false, SearchResultsTabAreaBrowser.search_proxy == null);
/*     */               }
/*     */               catch (Throwable e) {}
/*     */               
/*     */ 
/* 159 */               if (SearchResultsTabAreaBrowser.search_proxy != null)
/*     */               {
/* 161 */                 SearchResultsTabArea.SearchQuery sq = area.sq;
/*     */                 
/* 163 */                 if (sq != null)
/*     */                 {
/* 165 */                   area.anotherSearch(sq);
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */   static {
/* 177 */     COConfigurationManager.addParameterListener("browser.internal.proxy.id", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/* 185 */         synchronized (SearchResultsTabArea.class)
/*     */         {
/* 187 */           if (!SearchResultsTabAreaBrowser.search_proxy_init_done)
/*     */           {
/* 189 */             return;
/*     */           }
/*     */           
/* 192 */           SearchResultsTabAreaBrowser.access$602(false);
/*     */           
/* 194 */           SearchResultsTabAreaBrowser.access$102(false);
/*     */           
/* 196 */           if (SearchResultsTabAreaBrowser.search_proxy != null)
/*     */           {
/* 198 */             SearchResultsTabAreaBrowser.search_proxy.destroy();
/*     */             
/* 200 */             SearchResultsTabAreaBrowser.access$002(null);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static AEProxyFactory.PluginHTTPProxy getSearchProxy(SearchResultsTabAreaBrowser area)
/*     */   {
/* 211 */     initProxy();
/*     */     
/* 213 */     boolean force_proxy = !COConfigurationManager.getStringParameter("browser.internal.proxy.id", "none").equals("none");
/*     */     
/* 215 */     search_proxy_sem.reserve(force_proxy ? 60000L : 2500L);
/*     */     
/* 217 */     synchronized (SearchResultsTabArea.class)
/*     */     {
/* 219 */       if (search_proxy_set)
/*     */       {
/* 221 */         return search_proxy;
/*     */       }
/*     */       
/*     */ 
/* 225 */       pending.add(area);
/*     */       try
/*     */       {
/* 228 */         area.browserSkinObject.setAutoReloadPending(true, false);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 233 */       return null;
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
/*     */   protected SearchResultsTabAreaBrowser(SearchResultsTabArea _parent)
/*     */   {
/* 248 */     this.parent = _parent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void init(SWTSkinObjectBrowser _browserSkinObject)
/*     */   {
/* 255 */     this.browserSkinObject = _browserSkinObject;
/*     */     
/* 257 */     this.browserSkinObject.addListener(new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params)
/*     */       {
/* 261 */         if (eventType == 0) {
/* 262 */           SearchResultsTabAreaBrowser.this.browserSkinObject.removeListener(this);
/*     */           
/* 264 */           SearchResultsTabAreaBrowser.this.createBrowseArea(SearchResultsTabAreaBrowser.this.browserSkinObject);
/*     */         }
/* 266 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void createBrowseArea(SWTSkinObjectBrowser browserSkinObject)
/*     */   {
/* 275 */     this.browserSkinObject = browserSkinObject;
/*     */     
/*     */ 
/* 278 */     browserSkinObject.addListener(new BrowserContext.loadingListener() {
/*     */       public void browserLoadingChanged(boolean loading, String url) {
/* 280 */         SearchResultsTabAreaBrowser.this.parent.setBusy(loading);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void anotherSearch(SearchResultsTabArea.SearchQuery sq)
/*     */   {
/* 290 */     this.sq = sq;
/*     */     
/* 292 */     String url = PlatformConfigMessenger.getWebSearchUrl().replaceAll("%s", UrlUtils.encode(sq.term));
/*     */     
/* 294 */     AEProxyFactory.PluginHTTPProxy proxy = getSearchProxy(this);
/*     */     
/* 296 */     if (proxy != null)
/*     */     {
/* 298 */       url = proxy.proxifyURL(url);
/*     */     }
/*     */     
/* 301 */     if (Utils.isThisThreadSWT()) {
/*     */       try {
/* 303 */         this.browserSkinObject.getBrowser().setText("");
/* 304 */         final BrowserWrapper browser = this.browserSkinObject.getBrowser();
/* 305 */         final boolean[] done = { false };
/* 306 */         browser.addLocationListener(new LocationListener()
/*     */         {
/*     */           public void changing(LocationEvent event) {}
/*     */           
/*     */           public void changed(LocationEvent event) {
/* 311 */             done[0] = true;
/* 312 */             browser.removeLocationListener(this);
/*     */           }
/* 314 */         });
/* 315 */         this.browserSkinObject.getBrowser().setUrl("about:blank");
/* 316 */         this.browserSkinObject.getBrowser().refresh();
/* 317 */         this.browserSkinObject.getBrowser().update();
/* 318 */         Display display = Utils.getDisplay();
/* 319 */         if (display != null) {
/* 320 */           long until = SystemTime.getCurrentTime() + 300L;
/* 321 */           while ((done[0] == 0) && (until > SystemTime.getCurrentTime())) {
/* 322 */             if (!display.readAndDispatch()) {
/* 323 */               display.sleep();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {}
/*     */     }
/*     */     
/*     */ 
/* 332 */     this.browserSkinObject.setURL(url);
/*     */   }
/*     */   
/*     */   public int getResultCount()
/*     */   {
/* 337 */     return 0;
/*     */   }
/*     */   
/*     */   public void showView() {}
/*     */   
/*     */   public void refreshView() {}
/*     */   
/*     */   public void hideView() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SearchResultsTabAreaBrowser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */