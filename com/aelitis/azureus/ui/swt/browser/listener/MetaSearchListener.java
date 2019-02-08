/*      */ package com.aelitis.azureus.ui.swt.browser.listener;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*      */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*      */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*      */ import com.aelitis.azureus.core.custom.CustomizationManager;
/*      */ import com.aelitis.azureus.core.custom.CustomizationManagerFactory;
/*      */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*      */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
/*      */ import com.aelitis.azureus.core.messenger.browser.listeners.AbstractBrowserMessageListener;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformConfigMessenger;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*      */ import com.aelitis.azureus.core.metasearch.Result;
/*      */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*      */ import com.aelitis.azureus.core.metasearch.SearchException;
/*      */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.CookieParser;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionDownloadListener;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionScheduler;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.ui.swt.browser.OpenCloseSearchDetailsListener;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.TorrentListViewsUtils;
/*      */ import com.aelitis.azureus.util.ConstantsVuze;
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import com.aelitis.azureus.util.MapUtils;
/*      */ import com.aelitis.azureus.util.UrlFilter;
/*      */ import java.io.File;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils.torrentAttributeListener;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.json.simple.JSONArray;
/*      */ import org.json.simple.JSONObject;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MetaSearchListener
/*      */   extends AbstractBrowserMessageListener
/*      */ {
/*      */   public static final String LISTENER_ID = "metasearch";
/*      */   public static final String OP_SEARCH = "search";
/*      */   public static final String OP_GET_ENGINES = "get-engines";
/*      */   public static final String OP_GET_ALL_ENGINES = "get-all-engines";
/*      */   public static final String OP_ENGINE_PREFERRED = "engine-preferred";
/*      */   public static final String OP_CHANGE_ENGINE_SELECTION = "change-engine-selection";
/*      */   public static final String OP_SET_SELECTED_ENGINES = "set-selected-engines";
/*      */   public static final String OP_GET_AUTO_MODE = "get-auto-mode";
/*      */   public static final String OP_SAVE_TEMPLATE = "save-template";
/*      */   public static final String OP_LOAD_TEMPLATE = "load-template";
/*      */   public static final String OP_DELETE_TEMPLATE = "delete-template";
/*      */   public static final String OP_TEST_TEMPLATE = "test-template";
/*      */   public static final String OP_EXPORT_TEMPLATE = "export-template";
/*      */   public static final String OP_IMPORT_TEMPLATE = "import-template";
/*      */   public static final String OP_OPEN_SEARCH_RESULTS = "open-search-results";
/*      */   public static final String OP_CLOSE_SEARCH_RESULTS = "close-search-results";
/*      */   public static final String OP_LOAD_TORRENT = "load-torrent";
/*      */   public static final String OP_HAS_LOAD_TORRENT = "has-load-torrent";
/*      */   public static final String OP_ENGINE_LOGIN = "engine-login";
/*      */   public static final String OP_GET_LOGIN_COOKIES = "get-login-cookies";
/*      */   public static final String OP_CREATE_SUBSCRIPTION = "create-subscription";
/*      */   public static final String OP_READ_SUBSCRIPTION = "read-subscription";
/*      */   public static final String OP_UPDATE_SUBSCRIPTION = "update-subscription";
/*      */   public static final String OP_READ_SUBSCRIPTION_RESULTS = "read-subscription-results";
/*      */   public static final String OP_DELETE_SUBSCRIPTION_RESULTS = "delete-subscription-results";
/*      */   public static final String OP_MARK_SUBSCRIPTION_RESULTS = "mark-subscription-results";
/*      */   public static final String OP_DOWNLOAD_SUBSCRIPTION = "download-subscription";
/*      */   public static final String OP_SUBSCRIPTION_SET_AUTODL = "subscription-set-auto-download";
/*      */   public static final String OP_IS_CUSTOMISED = "is-customized";
/*      */   public static final String OP_ADD_EXTERNAL_LINKS = "add-external-links";
/*  113 */   private static final Set active_subs_auth = new HashSet();
/*      */   
/*  115 */   private static final Set pending_play_now_urls = new HashSet();
/*      */   private final OpenCloseSearchDetailsListener openCloseSearchDetailsListener;
/*      */   
/*  118 */   static { TorrentUtils.addTorrentAttributeListener(new TorrentUtils.torrentAttributeListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void attributeSet(TOTorrent torrent, String attribute, Object value)
/*      */       {
/*      */ 
/*      */ 
/*  127 */         if (attribute == "obtained_from") {
/*      */           try
/*      */           {
/*  130 */             String torrent_url = (String)value;
/*      */             
/*  132 */             boolean hook_dm = false;
/*      */             
/*  134 */             synchronized (MetaSearchListener.pending_play_now_urls)
/*      */             {
/*  136 */               if (MetaSearchListener.pending_play_now_urls.remove(torrent_url))
/*      */               {
/*      */ 
/*      */ 
/*  140 */                 hook_dm = true;
/*      */               }
/*      */             }
/*      */             
/*  144 */             if (!hook_dm) {
/*  145 */               return;
/*      */             }
/*      */             
/*  148 */             if (!AzureusCoreFactory.isCoreRunning())
/*      */             {
/*  150 */               Debug.out("Core wasn't available for pending play now");
/*      */             }
/*      */             else
/*      */             {
/*  154 */               final HashWrapper hash = torrent.getHashWrapper();
/*      */               
/*  156 */               AzureusCore core = AzureusCoreFactory.getSingleton();
/*      */               
/*  158 */               final GlobalManager gm = core.getGlobalManager();
/*      */               
/*  160 */               org.gudy.azureus2.core3.download.DownloadManager existing = gm.getDownloadManager(hash);
/*      */               
/*  162 */               if (existing != null)
/*      */               {
/*  164 */                 MetaSearchListener.playOrStream(existing);
/*      */                 
/*  166 */                 return;
/*      */               }
/*      */               
/*  169 */               GlobalManagerListener l = new GlobalManagerAdapter()
/*      */               {
/*      */ 
/*  172 */                 private final long listener_add_time = SystemTime.getMonotonousTime();
/*      */                 
/*      */ 
/*      */                 public void downloadManagerAdded(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */                 {
/*      */                   try
/*      */                   {
/*  179 */                     TOTorrent t = dm.getTorrent();
/*      */                     
/*  181 */                     if (t.getHashWrapper().equals(hash))
/*      */                     {
/*  183 */                       gm.removeListener(this);
/*      */                       
/*  185 */                       MetaSearchListener.playOrStream(dm);
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/*  190 */                     Debug.out(e);
/*      */                   }
/*      */                   finally
/*      */                   {
/*  194 */                     if (SystemTime.getMonotonousTime() - this.listener_add_time > 300000L)
/*      */                     {
/*  196 */                       gm.removeListener(this);
/*      */                     }
/*      */                     
/*      */                   }
/*      */                 }
/*  201 */               };
/*  202 */               gm.addListener(l, false);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*  206 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void playOrStream(final org.gudy.azureus2.core3.download.DownloadManager download)
/*      */   {
/*  217 */     new AEThread2("MSL:POS", true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*  222 */         TorrentListViewsUtils.playOrStream(download, -1);
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MetaSearchListener(OpenCloseSearchDetailsListener openCloseSearchDetailsListener)
/*      */   {
/*  234 */     super("metasearch");
/*      */     
/*  236 */     this.openCloseSearchDetailsListener = openCloseSearchDetailsListener;
/*      */   }
/*      */   
/*      */   public void handleMessage(BrowserMessage message)
/*      */   {
/*  241 */     String opid = message.getOperationId();
/*      */     
/*  243 */     MetaSearchManager metaSearchManager = MetaSearchManagerFactory.getSingleton();
/*      */     
/*  245 */     metaSearchManager.log("BrowserListener: received " + message);
/*      */     
/*  247 */     if ("search".equals(opid))
/*      */     {
/*  249 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*      */ 
/*  252 */       search(decodedMap, null);
/*      */     }
/*  254 */     else if ("engine-preferred".equals(opid))
/*      */     {
/*  256 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  258 */       long engine_id = ((Long)decodedMap.get("engine_id")).longValue();
/*      */       
/*  260 */       Engine engine = getEngineFromId(engine_id);
/*      */       
/*  262 */       if (engine != null)
/*      */       {
/*  264 */         metaSearchManager.getMetaSearch().enginePreferred(engine);
/*      */       }
/*      */     }
/*  267 */     else if ("engine-login".equals(opid))
/*      */     {
/*  269 */       final Map decodedMap = message.getDecodedMap();
/*      */       
/*  271 */       long engine_id = ((Long)decodedMap.get("engine_id")).longValue();
/*      */       
/*  273 */       Long sid = (Long)decodedMap.get("sid");
/*      */       
/*  275 */       final Engine engine = getEngineFromId(engine_id);
/*      */       
/*  277 */       if ((engine != null) && ((engine instanceof WebEngine)))
/*      */       {
/*  279 */         final WebEngine webEngine = (WebEngine)engine;
/*      */         
/*  281 */         Utils.execSWTThread(new Runnable() {
/*      */           public void run() {
/*  283 */             new ExternalLoginWindow(new ExternalLoginListener()
/*      */             {
/*      */               private String previous_cookies;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               private boolean search_done;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void canceled(ExternalLoginWindow window) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void cookiesFound(ExternalLoginWindow window, String cookies)
/*      */               {
/*  313 */                 if (handleCookies(cookies, false))
/*      */                 {
/*  315 */                   window.close();
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void done(ExternalLoginWindow window, String cookies)
/*      */               {
/*  324 */                 handleCookies(cookies, true);
/*      */               }
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
/*      */ 
/*      */ 
/*      */ 
/*      */               private boolean handleCookies(String cookies, boolean force_if_ready)
/*      */               {
/*  343 */                 if (this.search_done)
/*      */                 {
/*  345 */                   return false;
/*      */                 }
/*      */                 
/*  348 */                 String[] required = MetaSearchListener.3.this.val$webEngine.getRequiredCookies();
/*      */                 
/*  350 */                 boolean skip_search = (required.length == 0) && (!force_if_ready);
/*      */                 
/*  352 */                 if (CookieParser.cookiesContain(required, cookies))
/*      */                 {
/*  354 */                   MetaSearchListener.3.this.val$webEngine.setCookies(cookies);
/*      */                   
/*  356 */                   if ((this.previous_cookies == null) || (!this.previous_cookies.equals(cookies)))
/*      */                   {
/*  358 */                     this.previous_cookies = cookies;
/*      */                     
/*  360 */                     if (!skip_search)
/*      */                     {
/*      */ 
/*      */ 
/*  364 */                       this.search_done = true;
/*      */                       
/*  366 */                       MetaSearchListener.this.search(MetaSearchListener.3.this.val$decodedMap, MetaSearchListener.3.this.val$webEngine);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  371 */                 return this.search_done; } }, webEngine.getName(), webEngine.getLoginPageUrl(), false, webEngine.getAuthMethod(), engine.isMine());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  402 */         Map params = new HashMap();
/*      */         
/*  404 */         if (sid != null) {
/*  405 */           params.put("sid", sid);
/*      */         }
/*  407 */         params.put("error", "engine not found or not a web engine");
/*      */         
/*  409 */         sendBrowserMessage("metasearch", "engineFailed", params);
/*      */       }
/*  411 */     } else if ("get-login-cookies".equals(opid))
/*      */     {
/*  413 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  415 */       final String url = ((String)decodedMap.get("url")).replaceAll("%s", "");
/*      */       
/*  417 */       Utils.execSWTThread(new Runnable() {
/*      */         public void run() {
/*  419 */           new ExternalLoginWindow(new ExternalLoginListener()
/*      */           {
/*      */ 
/*      */             public void canceled(ExternalLoginWindow window) {
/*  423 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "setCookiesFailed", new HashMap()); }
/*      */             
/*      */             public void cookiesFound(ExternalLoginWindow window, String cookies) {}
/*      */             
/*  427 */             public void done(ExternalLoginWindow window, String cookies) { String[] cookieNames = CookieParser.getCookiesNames(cookies);
/*  428 */               Map params = new HashMap();
/*  429 */               params.put("cookieNames", cookieNames);
/*  430 */               params.put("currentCookie", cookies);
/*  431 */               params.put("cookieMethod", window.proxyCaptureModeRequired() ? "proxy" : "transparent");
/*  432 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "setCookies", params); } }, url, url, true, "proxy", true);
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */       });
/*      */ 
/*      */ 
/*      */     }
/*  444 */     else if ("get-engines".equals(opid)) {
/*  445 */       String subscriptionId = null;
/*      */       try
/*      */       {
/*  448 */         Map decodedMap = message.getDecodedMap();
/*      */         
/*  450 */         subscriptionId = (String)decodedMap.get("subs_id");
/*      */       }
/*      */       catch (Exception e) {}
/*      */       
/*      */ 
/*  455 */       Engine[] engines = null;
/*      */       
/*  457 */       if (subscriptionId != null)
/*      */       {
/*  459 */         engines = new Engine[0];
/*      */         
/*  461 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(subscriptionId);
/*      */         
/*  463 */         if (subs != null) {
/*      */           try
/*      */           {
/*  466 */             Engine engine = subs.getEngine();
/*      */             
/*  468 */             if (engine != null)
/*      */             {
/*  470 */               engines = new Engine[] { engine };
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*  474 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  479 */       if (engines == null)
/*      */       {
/*  481 */         engines = metaSearchManager.getMetaSearch().getEngines(true, true);
/*      */       }
/*      */       
/*  484 */       List params = new ArrayList();
/*  485 */       for (int i = 0; i < engines.length; i++) {
/*  486 */         Engine engine = engines[i];
/*      */         
/*  488 */         if (((engine.isActive()) && (engine.getSource() != 0)) || 
/*      */         
/*      */ 
/*      */ 
/*  492 */           (subscriptionId != null))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  497 */           Map engineMap = new HashMap();
/*  498 */           engineMap.put("id", new Long(engine.getId()));
/*  499 */           engineMap.put("name", engine.getName());
/*  500 */           engineMap.put("favicon", engine.getIcon());
/*  501 */           engineMap.put("dl_link_css", engine.getDownloadLinkCSS());
/*  502 */           engineMap.put("selected", Engine.SEL_STATE_STRINGS[engine.getSelectionState()]);
/*  503 */           engineMap.put("type", Engine.ENGINE_SOURCE_STRS[engine.getSource()]);
/*  504 */           engineMap.put("shareable", Boolean.valueOf(engine.isShareable()));
/*  505 */           params.add(engineMap);
/*      */         } }
/*  507 */       sendBrowserMessage("metasearch", "enginesUsed", params);
/*      */     }
/*  509 */     else if ("get-all-engines".equals(opid))
/*      */     {
/*  511 */       Engine[] engines = metaSearchManager.getMetaSearch().getEngines(false, true);
/*  512 */       List params = new ArrayList();
/*  513 */       for (int i = 0; i < engines.length; i++) {
/*  514 */         Engine engine = engines[i];
/*      */         
/*  516 */         if (engine.getSource() != 0)
/*      */         {
/*      */ 
/*      */ 
/*  520 */           Map engineMap = new HashMap();
/*  521 */           engineMap.put("id", new Long(engine.getId()));
/*  522 */           engineMap.put("name", engine.getName());
/*  523 */           engineMap.put("favicon", engine.getIcon());
/*  524 */           engineMap.put("dl_link_css", engine.getDownloadLinkCSS());
/*  525 */           engineMap.put("selected", Engine.SEL_STATE_STRINGS[engine.getSelectionState()]);
/*  526 */           engineMap.put("type", Engine.ENGINE_SOURCE_STRS[engine.getSource()]);
/*  527 */           engineMap.put("shareable", Boolean.valueOf(engine.isShareable()));
/*  528 */           params.add(engineMap);
/*      */         } }
/*  530 */       sendBrowserMessage("metasearch", "engineList", params);
/*      */     }
/*  532 */     else if ("set-selected-engines".equals(opid))
/*      */     {
/*  534 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  536 */       List template_ids = (List)decodedMap.get("template_ids");
/*      */       
/*  538 */       long[] ids = new long[template_ids.size()];
/*      */       
/*  540 */       for (int i = 0; i < ids.length; i++)
/*      */       {
/*  542 */         Map m = (Map)template_ids.get(i);
/*      */         
/*  544 */         ids[i] = ((Long)m.get("id")).longValue();
/*      */       }
/*      */       
/*  547 */       boolean auto = ((Boolean)decodedMap.get("auto")).booleanValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  554 */       Boolean is_default = (Boolean)decodedMap.get("set_default");
/*      */       
/*  556 */       boolean skip = false;
/*      */       
/*  558 */       if ((is_default != null) && (is_default.booleanValue()))
/*      */       {
/*  560 */         if (CustomizationManagerFactory.getSingleton().getActiveCustomization() != null)
/*      */         {
/*  562 */           skip = true;
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/*  567 */         if (!skip)
/*      */         {
/*  569 */           metaSearchManager.setSelectedEngines(ids, auto);
/*      */         }
/*      */         
/*  572 */         Map params = new HashMap();
/*  573 */         sendBrowserMessage("metasearch", "setSelectedCompleted", params);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  577 */         Debug.out(e);
/*      */         
/*  579 */         Map params = new HashMap();
/*  580 */         params.put("error", Debug.getNestedExceptionMessage(e));
/*      */         
/*  582 */         sendBrowserMessage("metasearch", "setSelectedFailed", params);
/*      */       }
/*  584 */     } else if ("change-engine-selection".equals(opid))
/*      */     {
/*  586 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  588 */       MetaSearch ms = metaSearchManager.getMetaSearch();
/*      */       
/*  590 */       Engine[] engines = ms.getEngines(false, true);
/*      */       
/*  592 */       Set selected = new HashSet();
/*      */       
/*  594 */       for (int i = 0; i < engines.length; i++)
/*      */       {
/*  596 */         Engine e = engines[i];
/*      */         
/*  598 */         if (e.getSelectionState() == 2)
/*      */         {
/*  600 */           selected.add(new Long(e.getId()));
/*      */         }
/*      */       }
/*      */       
/*  604 */       List l_engines = (List)decodedMap.get("engines");
/*      */       
/*  606 */       for (int i = 0; i < l_engines.size(); i++)
/*      */       {
/*  608 */         Map map = (Map)l_engines.get(i);
/*      */         
/*  610 */         long id = ((Long)map.get("id")).longValue();
/*      */         
/*  612 */         String str = (String)map.get("selected");
/*      */         
/*  614 */         if (str.equalsIgnoreCase(Engine.SEL_STATE_STRINGS[2]))
/*      */         {
/*  616 */           selected.add(new Long(id));
/*      */         }
/*  618 */         else if (str.equalsIgnoreCase(Engine.SEL_STATE_STRINGS[0]))
/*      */         {
/*  620 */           selected.remove(new Long(id));
/*      */         }
/*      */       }
/*      */       
/*  624 */       long[] ids = new long[selected.size()];
/*      */       
/*  626 */       Iterator it = selected.iterator();
/*      */       
/*  628 */       int pos = 0;
/*      */       
/*  630 */       while (it.hasNext())
/*      */       {
/*  632 */         long id = ((Long)it.next()).longValue();
/*      */         
/*  634 */         ids[(pos++)] = id;
/*      */       }
/*      */       try
/*      */       {
/*  638 */         metaSearchManager.setSelectedEngines(ids, metaSearchManager.isAutoMode());
/*      */         
/*  640 */         Map params = new HashMap();
/*  641 */         sendBrowserMessage("metasearch", "changeEngineSelectionCompleted", params);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  645 */         Debug.out(e);
/*      */         
/*  647 */         Map params = new HashMap();
/*  648 */         params.put("error", Debug.getNestedExceptionMessage(e));
/*      */         
/*  650 */         sendBrowserMessage("metasearch", "changeEngineSelectionFailed", params);
/*      */       }
/*  652 */     } else if ("get-auto-mode".equals(opid))
/*      */     {
/*  654 */       boolean mode = metaSearchManager.isAutoMode();
/*      */       
/*  656 */       Map params = new HashMap();
/*      */       
/*  658 */       params.put("auto", Boolean.valueOf(mode));
/*      */       
/*  660 */       boolean custom = CustomizationManagerFactory.getSingleton().getActiveCustomization() != null;
/*      */       
/*  662 */       params.put("is_custom", Boolean.valueOf(custom));
/*      */       
/*  664 */       sendBrowserMessage("metasearch", "getAutoModeResult", params);
/*      */     }
/*  666 */     else if ("save-template".equals(opid))
/*      */     {
/*  668 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  670 */       String type_str = (String)decodedMap.get("type");
/*      */       
/*  672 */       String name = (String)decodedMap.get("name");
/*      */       
/*  674 */       Long l_id = (Long)decodedMap.get("id");
/*      */       
/*  676 */       long id = l_id == null ? -1L : l_id.longValue();
/*      */       
/*  678 */       String json = (String)decodedMap.get("value");
/*      */       
/*  680 */       String cookies = (String)decodedMap.get("current_cookie");
/*      */       
/*      */       try
/*      */       {
/*  684 */         Engine engine = metaSearchManager.addEngine(id, type_str.equals("json") ? 2 : 1, name, json);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  691 */         engine.setMine(true);
/*      */         
/*  693 */         if ((cookies != null) && ((engine instanceof WebEngine))) {
/*  694 */           WebEngine we = (WebEngine)engine;
/*  695 */           we.setCookies(cookies);
/*      */         }
/*      */         
/*  698 */         Map params = new HashMap();
/*  699 */         params.put("id", new Long(engine.getId()));
/*      */         
/*  701 */         sendBrowserMessage("metasearch", "saveTemplateCompleted", params);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  705 */         Debug.out(e);
/*      */         
/*  707 */         Map params = new HashMap();
/*  708 */         params.put("id", new Long(id));
/*  709 */         params.put("error", Debug.getNestedExceptionMessage(e));
/*      */         
/*  711 */         sendBrowserMessage("metasearch", "saveTemplateFailed", params);
/*      */       }
/*  713 */     } else if ("load-template".equals(opid))
/*      */     {
/*  715 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  717 */       long id = ((Long)decodedMap.get("id")).longValue();
/*      */       
/*  719 */       Engine engine = metaSearchManager.getMetaSearch().getEngine(id);
/*      */       
/*  721 */       if (engine == null)
/*      */       {
/*  723 */         Map params = new HashMap();
/*  724 */         params.put("id", new Long(id));
/*  725 */         params.put("error", "Template not found");
/*      */         
/*  727 */         sendBrowserMessage("metasearch", "loadTemplateFailed", params);
/*      */       }
/*      */       else
/*      */       {
/*      */         try {
/*  732 */           Map params = new HashMap();
/*  733 */           params.put("id", new Long(engine.getId()));
/*  734 */           params.put("name", engine.getName());
/*  735 */           int type = engine.getType();
/*  736 */           params.put("type", type < Engine.ENGINE_TYPE_STRS.length ? Engine.ENGINE_TYPE_STRS[type] : Integer.valueOf(type));
/*  737 */           params.put("value", JSONObject.escape(engine.exportToJSONString()));
/*  738 */           params.put("shareable", Boolean.valueOf(engine.isShareable()));
/*      */           
/*  740 */           params.put("uid", engine.getUID());
/*      */           
/*  742 */           params.put("supports_direct_download", Boolean.valueOf((engine.supportsField(102)) || (engine.supportsField(105))));
/*      */           
/*      */ 
/*      */ 
/*  746 */           params.put("auto_dl_supported", Boolean.valueOf(engine.getAutoDownloadSupported() == 1));
/*      */           
/*  748 */           sendBrowserMessage("metasearch", "loadTemplateCompleted", params);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  752 */           Debug.out(e);
/*      */           
/*  754 */           Map params = new HashMap();
/*  755 */           params.put("id", new Long(id));
/*  756 */           params.put("error", Debug.getNestedExceptionMessage(e));
/*      */           
/*  758 */           sendBrowserMessage("metasearch", "loadTemplateFailed", params);
/*      */         }
/*      */       }
/*  761 */     } else if ("delete-template".equals(opid))
/*      */     {
/*  763 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  765 */       long id = ((Long)decodedMap.get("id")).longValue();
/*      */       
/*  767 */       Engine engine = metaSearchManager.getMetaSearch().getEngine(id);
/*      */       
/*  769 */       if (engine == null)
/*      */       {
/*  771 */         Map params = new HashMap();
/*  772 */         params.put("id", new Long(id));
/*  773 */         params.put("error", "Template not found");
/*      */         
/*  775 */         sendBrowserMessage("metasearch", "deleteTemplateFailed", params);
/*      */       }
/*  777 */       else if (engine.getSource() != 2)
/*      */       {
/*  779 */         Map params = new HashMap();
/*  780 */         params.put("id", new Long(id));
/*  781 */         params.put("error", "Template is not local");
/*      */         
/*  783 */         sendBrowserMessage("metasearch", "deleteTemplateFailed", params);
/*      */       }
/*      */       else
/*      */       {
/*  787 */         engine.delete();
/*      */         
/*  789 */         Map params = new HashMap();
/*  790 */         params.put("id", new Long(id));
/*  791 */         sendBrowserMessage("metasearch", "deleteTemplateCompleted", params);
/*      */       }
/*  793 */     } else if ("test-template".equals(opid))
/*      */     {
/*  795 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  797 */       final long id = ((Long)decodedMap.get("id")).longValue();
/*  798 */       long match_count = ((Long)decodedMap.get("max_matches")).longValue();
/*      */       
/*  800 */       String searchText = (String)decodedMap.get("searchText");
/*  801 */       String headers = (String)decodedMap.get("headers");
/*      */       
/*  803 */       Long sid = (Long)decodedMap.get("sid");
/*      */       
/*  805 */       Engine engine = metaSearchManager.getMetaSearch().getEngine(id);
/*      */       
/*  807 */       if (engine == null)
/*      */       {
/*  809 */         Map params = new HashMap();
/*  810 */         params.put("id", new Long(id));
/*  811 */         params.put("error", "Template not found");
/*  812 */         if (sid != null) { params.put("sid", sid);
/*      */         }
/*  814 */         sendBrowserMessage("metasearch", "testTemplateFailed", params);
/*      */       }
/*      */       else
/*      */       {
/*  818 */         SearchParameter parameter = new SearchParameter("s", searchText);
/*  819 */         SearchParameter[] parameters = { parameter };
/*      */         
/*      */         try
/*      */         {
/*  823 */           engine.search(parameters, new HashMap(), (int)match_count, (int)match_count, headers, new ResultListener()
/*      */           {
/*      */             private String content;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  832 */             private List matches = new ArrayList();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void contentReceived(Engine engine, String _content)
/*      */             {
/*  839 */               this.content = _content;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void matchFound(Engine engine, String[] fields)
/*      */             {
/*  847 */               this.matches.add(fields);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void resultsReceived(Engine engine, Result[] results) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void resultsComplete(Engine engine)
/*      */             {
/*  861 */               Map params = new HashMap();
/*  862 */               params.put("id", new Long(id));
/*  863 */               if (this.val$sid != null) params.put("sid", this.val$sid);
/*  864 */               params.put("content", JSONObject.escape(this.content));
/*      */               
/*  866 */               JSONArray l_matches = new JSONArray();
/*      */               
/*  868 */               params.put("matches", l_matches);
/*      */               
/*  870 */               for (int i = 0; i < this.matches.size(); i++)
/*      */               {
/*  872 */                 String[] match = (String[])this.matches.get(i);
/*      */                 
/*  874 */                 JSONArray l_match = new JSONArray();
/*      */                 
/*  876 */                 l_matches.add(l_match);
/*      */                 
/*  878 */                 Collections.addAll(l_match, match);
/*      */               }
/*      */               
/*  881 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "testTemplateCompleted", params);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void engineFailed(Engine engine, Throwable e)
/*      */             {
/*  890 */               Debug.out(e);
/*      */               
/*  892 */               Map params = new HashMap();
/*  893 */               params.put("id", new Long(id));
/*  894 */               params.put("error", Debug.getNestedExceptionMessage(e));
/*  895 */               if (this.val$sid != null) { params.put("sid", this.val$sid);
/*      */               }
/*  897 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "testTemplateFailed", params);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void engineRequiresLogin(Engine engine, Throwable e)
/*      */             {
/*  905 */               Map params = new HashMap();
/*  906 */               params.put("id", new Long(id));
/*  907 */               params.put("error", Debug.getNestedExceptionMessage(e));
/*  908 */               if (this.val$sid != null) { params.put("sid", this.val$sid);
/*      */               }
/*  910 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "testTemplateRequiresLogin", params);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (SearchException e) {}
/*      */       }
/*      */     }
/*  917 */     else if ("export-template".equals(opid))
/*      */     {
/*  919 */       Map decodedMap = message.getDecodedMap();
/*      */       
/*  921 */       final long id = ((Long)decodedMap.get("id")).longValue();
/*      */       
/*  923 */       final Engine engine = metaSearchManager.getMetaSearch().getEngine(id);
/*      */       
/*  925 */       if (engine == null)
/*      */       {
/*  927 */         Map params = new HashMap();
/*  928 */         params.put("error", "template '" + id + "' not found");
/*      */         
/*  930 */         sendBrowserMessage("metasearch", "exportTemplateFailed", params);
/*      */       }
/*      */       else {
/*  933 */         final Shell shell = Utils.findAnyShell();
/*      */         
/*  935 */         shell.getDisplay().asyncExec(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  941 */             FileDialog dialog = new FileDialog(shell, 139264);
/*      */             
/*      */ 
/*  944 */             dialog.setFilterPath(TorrentOpener.getFilterPathData());
/*      */             
/*  946 */             dialog.setText(MessageText.getString("metasearch.export.select.template.file"));
/*      */             
/*  948 */             dialog.setFilterExtensions(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  953 */             dialog.setFilterNames(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  959 */             String path = TorrentOpener.setFilterPathData(dialog.open());
/*      */             
/*  961 */             if (path != null)
/*      */             {
/*  963 */               String lc = path.toLowerCase();
/*      */               
/*  965 */               if ((!lc.endsWith(".vuze")) && (!lc.endsWith(".vuz")))
/*      */               {
/*  967 */                 path = path + ".vuze";
/*      */               }
/*      */               try
/*      */               {
/*  971 */                 engine.exportToVuzeFile(new File(path));
/*      */                 
/*  973 */                 Map params = new HashMap();
/*  974 */                 params.put("id", new Long(id));
/*  975 */                 MetaSearchListener.this.sendBrowserMessage("metasearch", "exportTemplateCompleted", params);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  979 */                 Debug.out(e);
/*      */                 
/*  981 */                 Map params = new HashMap();
/*  982 */                 params.put("id", new Long(id));
/*  983 */                 params.put("error", "save failed: " + Debug.getNestedExceptionMessage(e));
/*      */                 
/*  985 */                 MetaSearchListener.this.sendBrowserMessage("metasearch", "exportTemplateFailed", params);
/*      */               }
/*      */             }
/*      */             else {
/*  989 */               Map params = new HashMap();
/*  990 */               params.put("id", new Long(id));
/*  991 */               params.put("error", "operation cancelled");
/*      */               
/*  993 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "exportTemplateFailed", params);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*  998 */     } else if ("import-template".equals(opid))
/*      */     {
/* 1000 */       final Shell shell = Utils.findAnyShell();
/*      */       
/* 1002 */       shell.getDisplay().asyncExec(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 1008 */           FileDialog dialog = new FileDialog(shell, 135168);
/*      */           
/*      */ 
/* 1011 */           dialog.setFilterPath(TorrentOpener.getFilterPathData());
/*      */           
/* 1013 */           dialog.setText(MessageText.getString("metasearch.import.select.template.file"));
/*      */           
/* 1015 */           dialog.setFilterExtensions(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1020 */           dialog.setFilterNames(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1026 */           String path = TorrentOpener.setFilterPathData(dialog.open());
/*      */           
/* 1028 */           if (path != null)
/*      */           {
/* 1030 */             VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*      */             
/* 1032 */             VuzeFile vf = vfh.loadAndHandleVuzeFile(path, 1);
/*      */             
/* 1034 */             if (vf == null)
/*      */             {
/* 1036 */               Map params = new HashMap();
/* 1037 */               params.put("error", "invalid .vuze file");
/*      */               
/* 1039 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "importTemplateFailed", params);
/*      */             }
/*      */             else
/*      */             {
/* 1043 */               VuzeFileComponent[] comps = vf.getComponents();
/*      */               
/* 1045 */               for (int i = 0; i < comps.length; i++)
/*      */               {
/* 1047 */                 VuzeFileComponent comp = comps[i];
/*      */                 
/* 1049 */                 if (comp.getType() == 1)
/*      */                 {
/* 1051 */                   Engine engine = (Engine)comp.getData(Engine.VUZE_FILE_COMPONENT_ENGINE_KEY);
/*      */                   
/* 1053 */                   if (engine != null)
/*      */                   {
/* 1055 */                     Map params = new HashMap();
/* 1056 */                     params.put("id", new Long(engine.getId()));
/* 1057 */                     MetaSearchListener.this.sendBrowserMessage("metasearch", "importTemplateCompleted", params);
/*      */                     
/* 1059 */                     return;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 1064 */               Map params = new HashMap();
/* 1065 */               params.put("error", "invalid search template file");
/*      */               
/* 1067 */               MetaSearchListener.this.sendBrowserMessage("metasearch", "importTemplateFailed", params);
/*      */             }
/*      */           }
/*      */           else {
/* 1071 */             Map params = new HashMap();
/*      */             
/* 1073 */             params.put("error", "operation cancelled");
/*      */             
/* 1075 */             MetaSearchListener.this.sendBrowserMessage("metasearch", "importTemplateFailed", params);
/*      */           }
/*      */         }
/*      */       });
/* 1079 */     } else if ("open-search-results".equals(opid))
/*      */     {
/* 1081 */       Map decodedMap = message.getDecodedMap();
/* 1082 */       this.openCloseSearchDetailsListener.openSearchResults(decodedMap);
/* 1083 */     } else if ("close-search-results".equals(opid))
/*      */     {
/* 1085 */       Map decodedMap = message.getDecodedMap();
/* 1086 */       this.openCloseSearchDetailsListener.closeSearchResults(decodedMap);
/* 1087 */     } else if ("load-torrent".equals(opid)) {
/* 1088 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1090 */       String torrentUrl = (String)decodedMap.get("torrent_url");
/* 1091 */       String referer_str = (String)decodedMap.get("referer_url");
/*      */       
/* 1093 */       if (UrlFilter.getInstance().isWhitelisted(torrentUrl))
/*      */       {
/* 1095 */         ContentNetwork cn = ContentNetworkManagerFactory.getSingleton().getContentNetworkForURL(torrentUrl);
/*      */         
/* 1097 */         if (cn == null)
/*      */         {
/* 1099 */           cn = ConstantsVuze.getDefaultContentNetwork();
/*      */         }
/*      */         
/* 1102 */         torrentUrl = cn.appendURLSuffix(torrentUrl, false, true);
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1107 */         Map headers = UrlUtils.getBrowserHeaders(referer_str);
/*      */         
/* 1109 */         String subscriptionId = (String)decodedMap.get("subs_id");
/* 1110 */         String subscriptionResultId = (String)decodedMap.get("subs_rid");
/*      */         
/* 1112 */         if (subscriptionId != null)
/*      */         {
/* 1114 */           Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(subscriptionId);
/*      */           
/* 1116 */           if (subs != null)
/*      */           {
/*      */             try {
/* 1119 */               Engine engine = subs.getEngine();
/*      */               
/* 1121 */               if ((engine != null) && ((engine instanceof WebEngine)))
/*      */               {
/* 1123 */                 WebEngine webEngine = (WebEngine)engine;
/*      */                 
/* 1125 */                 if (webEngine.isNeedsAuth())
/*      */                 {
/* 1127 */                   headers.put("Cookie", webEngine.getCookies());
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 1132 */               Debug.out(e);
/*      */             }
/*      */             
/* 1135 */             if (subscriptionResultId != null)
/*      */             {
/* 1137 */               subs.addPotentialAssociation(subscriptionResultId, torrentUrl);
/*      */             }
/*      */           }
/*      */         } else {
/*      */           try {
/* 1142 */             long engineID = ((Long)decodedMap.get("engine_id")).longValue();
/*      */             
/* 1144 */             Engine engine = metaSearchManager.getMetaSearch().getEngine(engineID);
/*      */             
/* 1146 */             if (engine != null)
/*      */             {
/* 1148 */               engine.addPotentialAssociation(torrentUrl);
/*      */             }
/*      */             
/* 1151 */             if ((engine != null) && ((engine instanceof WebEngine)))
/*      */             {
/* 1153 */               WebEngine webEngine = (WebEngine)engine;
/*      */               
/* 1155 */               if (webEngine.isNeedsAuth())
/*      */               {
/* 1157 */                 headers.put("Cookie", webEngine.getCookies());
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1162 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/* 1167 */           String hash = (String)decodedMap.get("hash");
/*      */           
/* 1169 */           if (hash != null)
/*      */           {
/* 1171 */             if (!torrentUrl.toLowerCase(Locale.US).startsWith("magnet:"))
/*      */             {
/* 1173 */               headers.put("X-Alternative-URI-1", UrlUtils.getMagnetURI(Base32.decode(hash)));
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/* 1179 */         Boolean play_now = (Boolean)decodedMap.get("play-now");
/*      */         
/* 1181 */         if ((play_now != null) && (play_now.booleanValue()))
/*      */         {
/* 1183 */           synchronized (MetaSearchListener.class)
/*      */           {
/* 1185 */             pending_play_now_urls.add(torrentUrl);
/*      */           }
/*      */         }
/*      */         
/* 1189 */         PluginInitializer.getDefaultInterface().getDownloadManager().addDownload(new URL(torrentUrl), headers);
/*      */         
/*      */ 
/*      */ 
/* 1193 */         Map params = new HashMap();
/* 1194 */         params.put("torrent_url", torrentUrl);
/* 1195 */         params.put("referer_url", referer_str);
/* 1196 */         sendBrowserMessage("metasearch", "loadTorrentCompleted", params);
/*      */       }
/*      */       catch (Exception e) {
/* 1199 */         Map params = new HashMap();
/* 1200 */         params.put("torrent_url", torrentUrl);
/* 1201 */         params.put("referer_url", referer_str);
/* 1202 */         params.put("error", e.getMessage());
/* 1203 */         sendBrowserMessage("metasearch", "loadTorrentFailed", params);
/*      */       }
/*      */     }
/* 1206 */     else if ("has-load-torrent".equals(opid))
/*      */     {
/* 1208 */       Map params = new HashMap();
/* 1209 */       params.put("result", "1");
/* 1210 */       sendBrowserMessage("metasearch", "hasLoadTorrent", params);
/*      */     }
/* 1212 */     else if ("create-subscription".equals(opid))
/*      */     {
/* 1214 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1216 */       Long tid = (Long)decodedMap.get("tid");
/*      */       
/* 1218 */       String name = (String)decodedMap.get("name");
/* 1219 */       Boolean isPublic = (Boolean)decodedMap.get("is_public");
/* 1220 */       Map options = (Map)decodedMap.get("options");
/*      */       
/* 1222 */       Boolean isEnabled = (Boolean)options.get("is_enabled");
/* 1223 */       Boolean autoDownload = (Boolean)options.get("auto_dl");
/*      */       
/* 1225 */       Map result = new HashMap();
/*      */       
/* 1227 */       if (tid != null) result.put("tid", tid);
/*      */       try
/*      */       {
/* 1230 */         JSONObject payload = new JSONObject();
/*      */         
/*      */ 
/*      */ 
/* 1234 */         payload.put("engine_id", decodedMap.get("engine_id"));
/* 1235 */         payload.put("search_term", decodedMap.get("search_term"));
/* 1236 */         payload.put("filters", decodedMap.get("filters"));
/* 1237 */         payload.put("schedule", decodedMap.get("schedule"));
/* 1238 */         payload.put("options", decodedMap.get("options"));
/*      */         
/* 1240 */         Subscription subs = SubscriptionManagerFactory.getSingleton().create(name, isPublic.booleanValue(), payload.toString());
/*      */         
/* 1242 */         subs.getHistory().setDetails(isEnabled == null ? true : isEnabled.booleanValue(), autoDownload == null ? false : autoDownload.booleanValue());
/*      */         
/*      */ 
/*      */ 
/* 1246 */         result.put("id", subs.getID());
/*      */         
/* 1248 */         subs.requestAttention();
/*      */         
/* 1250 */         sendBrowserMessage("metasearch", "createSubscriptionCompleted", result);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1254 */         Debug.out(e);
/*      */         
/* 1256 */         result.put("error", "create failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1258 */         sendBrowserMessage("metasearch", "createSubscriptionFailed", result);
/*      */       }
/* 1260 */     } else if ("read-subscription".equals(opid))
/*      */     {
/* 1262 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1264 */       Long tid = (Long)decodedMap.get("tid");
/*      */       
/* 1266 */       String sid = (String)decodedMap.get("id");
/*      */       
/* 1268 */       Map result = new HashMap();
/*      */       
/* 1270 */       if (tid != null) result.put("tid", tid);
/*      */       try
/*      */       {
/* 1273 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(sid);
/*      */         
/* 1275 */         if (subs == null)
/*      */         {
/* 1277 */           result.put("error", "Subscription not found");
/*      */           
/* 1279 */           sendBrowserMessage("metasearch", "readSubscriptionFailed", result);
/*      */         }
/*      */         else
/*      */         {
/* 1283 */           boolean shareable = subs.isShareable();
/*      */           
/*      */ 
/*      */ 
/* 1287 */           result.put("id", subs.getID());
/* 1288 */           result.put("name", subs.getName());
/* 1289 */           result.put("is_public", Boolean.valueOf((shareable) && (subs.isPublic())));
/* 1290 */           result.put("is_author", Boolean.valueOf(subs.isMine()));
/* 1291 */           result.put("is_shareable", Boolean.valueOf(shareable));
/* 1292 */           result.put("auto_dl_supported", Boolean.valueOf(subs.isAutoDownloadSupported()));
/*      */           
/* 1294 */           SubscriptionHistory history = subs.getHistory();
/*      */           
/* 1296 */           Map options = new HashMap();
/*      */           
/* 1298 */           result.put("options", options);
/*      */           
/* 1300 */           options.put("is_enabled", Boolean.valueOf(history.isEnabled()));
/* 1301 */           options.put("auto_dl", Boolean.valueOf(history.isAutoDownload()));
/*      */           
/* 1303 */           Map info = new HashMap();
/*      */           
/* 1305 */           result.put("info", info);
/*      */           
/* 1307 */           info.put("last_scan", new Long(history.getLastScanTime()));
/* 1308 */           info.put("last_new", new Long(history.getLastNewResultTime()));
/* 1309 */           info.put("num_unread", new Long(history.getNumUnread()));
/* 1310 */           info.put("num_read", new Long(history.getNumRead()));
/*      */           
/* 1312 */           String json = subs.getJSON();
/*      */           
/* 1314 */           Map map = JSONUtils.decodeJSON(json);
/*      */           
/* 1316 */           result.put("engine_id", map.get("engine_id"));
/* 1317 */           result.put("search_term", map.get("search_term"));
/* 1318 */           result.put("filters", map.get("filters"));
/* 1319 */           result.put("schedule", map.get("schedule"));
/*      */           
/* 1321 */           sendBrowserMessage("metasearch", "readSubscriptionCompleted", result);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1325 */         Debug.out(e);
/*      */         
/* 1327 */         result.put("error", "read failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1329 */         sendBrowserMessage("metasearch", "readSubscriptionFailed", result);
/*      */       }
/* 1331 */     } else if ("update-subscription".equals(opid))
/*      */     {
/* 1333 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1335 */       Long tid = (Long)decodedMap.get("tid");
/*      */       
/* 1337 */       String name = (String)decodedMap.get("name");
/* 1338 */       Boolean isPublic = (Boolean)decodedMap.get("is_public");
/* 1339 */       String sid = (String)decodedMap.get("id");
/*      */       
/* 1341 */       Map options = (Map)decodedMap.get("options");
/*      */       
/* 1343 */       Boolean isEnabled = (Boolean)options.get("is_enabled");
/* 1344 */       Boolean autoDownload = (Boolean)options.get("auto_dl");
/*      */       
/* 1346 */       Map result = new HashMap();
/*      */       
/* 1348 */       if (tid != null) result.put("tid", tid);
/*      */       try
/*      */       {
/* 1351 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(sid);
/*      */         
/* 1353 */         if (subs == null)
/*      */         {
/* 1355 */           result.put("error", "Subscription not found");
/*      */           
/* 1357 */           sendBrowserMessage("metasearch", "updateSubscriptionFailed", result);
/*      */         }
/*      */         else
/*      */         {
/* 1361 */           JSONObject payload = new JSONObject();
/*      */           
/*      */ 
/*      */ 
/* 1365 */           payload.put("engine_id", decodedMap.get("engine_id"));
/* 1366 */           payload.put("search_term", decodedMap.get("search_term"));
/* 1367 */           payload.put("filters", decodedMap.get("filters"));
/* 1368 */           payload.put("schedule", decodedMap.get("schedule"));
/* 1369 */           payload.put("options", decodedMap.get("options"));
/*      */           
/* 1371 */           boolean changed = subs.setDetails(name, isPublic.booleanValue(), payload.toString());
/*      */           
/* 1373 */           subs.getHistory().setDetails(isEnabled == null ? true : isEnabled.booleanValue(), autoDownload == null ? false : autoDownload.booleanValue());
/*      */           
/*      */ 
/*      */ 
/* 1377 */           if (changed)
/*      */           {
/* 1379 */             subs.reset();
/*      */             try
/*      */             {
/* 1382 */               subs.getManager().getScheduler().downloadAsync(subs, true);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1386 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */           
/* 1390 */           result.put("id", subs.getID());
/*      */           
/* 1392 */           sendBrowserMessage("metasearch", "updateSubscriptionCompleted", result);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1396 */         Debug.out(e);
/*      */         
/* 1398 */         result.put("error", "update failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1400 */         sendBrowserMessage("metasearch", "updateSubscriptionFailed", result);
/*      */       }
/*      */     }
/* 1403 */     else if ("subscription-set-auto-download".equals(opid))
/*      */     {
/* 1405 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1407 */       String sid = (String)decodedMap.get("id");
/*      */       
/* 1409 */       Long tid = (Long)decodedMap.get("tid");
/*      */       
/* 1411 */       Boolean autoDownload = (Boolean)decodedMap.get("auto_dl");
/*      */       
/* 1413 */       Map result = new HashMap();
/*      */       
/* 1415 */       if (tid != null) result.put("tid", tid);
/*      */       try
/*      */       {
/* 1418 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(sid);
/*      */         
/* 1420 */         if (subs == null)
/*      */         {
/* 1422 */           result.put("error", "Subscription not found");
/*      */           
/* 1424 */           sendBrowserMessage("metasearch", "setSubscriptionAutoDownloadFailed", result);
/*      */         }
/*      */         else
/*      */         {
/* 1428 */           subs.getHistory().setAutoDownload(autoDownload.booleanValue());
/*      */           
/* 1430 */           sendBrowserMessage("metasearch", "setSubscriptionAutoDownloadCompleted", result);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1434 */         Debug.out(e);
/*      */         
/* 1436 */         result.put("error", "update failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1438 */         sendBrowserMessage("metasearch", "setSubscriptionAutoDownloadFailed", result);
/*      */       }
/*      */     }
/* 1441 */     else if ("read-subscription-results".equals(opid))
/*      */     {
/* 1443 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1445 */       Long tid = (Long)decodedMap.get("tid");
/*      */       
/* 1447 */       String sid = (String)decodedMap.get("id");
/*      */       
/* 1449 */       final Map result = new HashMap();
/*      */       
/* 1451 */       if (tid != null) result.put("tid", tid);
/*      */       try
/*      */       {
/* 1454 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(sid);
/*      */         
/* 1456 */         if (subs == null)
/*      */         {
/* 1458 */           result.put("error", "Subscription not found");
/*      */           
/* 1460 */           sendBrowserMessage("metasearch", "readSubscriptionResultsFailed", result);
/*      */         }
/*      */         else
/*      */         {
/* 1464 */           result.put("id", subs.getID());
/*      */           
/* 1466 */           if (!handleSubscriptionAuth(subs, result))
/*      */           {
/* 1468 */             if (subs.getHistory().getLastScanTime() == 0L)
/*      */             {
/* 1470 */               subs.getManager().getScheduler().download(subs, false, new SubscriptionDownloadListener()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void complete(Subscription subs)
/*      */                 {
/*      */ 
/*      */ 
/* 1479 */                   if (!MetaSearchListener.this.handleSubscriptionAuth(subs, result))
/*      */                   {
/* 1481 */                     MetaSearchListener.this.encodeResults(subs, result);
/*      */                     
/* 1483 */                     MetaSearchListener.this.sendBrowserMessage("metasearch", "readSubscriptionResultsCompleted", result);
/*      */                     
/* 1485 */                     MetaSearchListener.this.openCloseSearchDetailsListener.resizeMainBrowser();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void failed(Subscription subs, SubscriptionException error)
/*      */                 {
/* 1495 */                   Debug.out(error);
/*      */                   
/* 1497 */                   result.put("error", "read failed: " + Debug.getNestedExceptionMessage(error));
/*      */                   
/* 1499 */                   MetaSearchListener.this.sendBrowserMessage("metasearch", "readSubscriptionResultsFailed", result);
/*      */                 }
/*      */               });
/*      */             }
/*      */             else
/*      */             {
/* 1505 */               encodeResults(subs, result);
/*      */               
/* 1507 */               sendBrowserMessage("metasearch", "readSubscriptionResultsCompleted", result);
/*      */               
/* 1509 */               this.openCloseSearchDetailsListener.resizeMainBrowser();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1515 */         Debug.out(e);
/*      */         
/* 1517 */         result.put("error", "read failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1519 */         sendBrowserMessage("metasearch", "readSubscriptionFailed", result);
/*      */       }
/* 1521 */     } else if ("delete-subscription-results".equals(opid))
/*      */     {
/* 1523 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1525 */       String sid = (String)decodedMap.get("id");
/*      */       
/* 1527 */       List rids = (List)decodedMap.get("rids");
/*      */       try
/*      */       {
/* 1530 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(sid);
/*      */         
/* 1532 */         if (subs == null)
/*      */         {
/* 1534 */           Map params = new HashMap();
/*      */           
/* 1536 */           params.put("error", "Subscription not found");
/*      */           
/* 1538 */           sendBrowserMessage("metasearch", "deleteSubscriptionResultsFailed", params);
/*      */         }
/*      */         else
/*      */         {
/* 1542 */           String[] rids_a = (String[])rids.toArray(new String[rids.size()]);
/*      */           
/* 1544 */           subs.getHistory().deleteResults(rids_a);
/*      */           
/* 1546 */           Map result = new HashMap();
/*      */           
/* 1548 */           result.put("rids", rids);
/*      */           
/* 1550 */           sendBrowserMessage("metasearch", "deleteSubscriptionResultsCompleted", result);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1554 */         Debug.out(e);
/*      */         
/* 1556 */         Map params = new HashMap();
/*      */         
/* 1558 */         params.put("error", "delete failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1560 */         sendBrowserMessage("metasearch", "deleteSubscriptionResultsFailed", params);
/*      */       }
/* 1562 */     } else if ("mark-subscription-results".equals(opid))
/*      */     {
/* 1564 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1566 */       String sid = (String)decodedMap.get("id");
/*      */       
/* 1568 */       List rids = (List)decodedMap.get("rids");
/* 1569 */       List reads = (List)decodedMap.get("reads");
/*      */       
/* 1571 */       Map result = new HashMap();
/*      */       try
/*      */       {
/* 1574 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(sid);
/*      */         
/* 1576 */         if (subs == null)
/*      */         {
/* 1578 */           result.put("error", "Subscription not found");
/*      */           
/* 1580 */           sendBrowserMessage("metasearch", "markSubscriptionResultsFailed", result);
/*      */         }
/*      */         else
/*      */         {
/* 1584 */           String[] rids_a = (String[])rids.toArray(new String[rids.size()]);
/*      */           
/* 1586 */           boolean[] reads_a = new boolean[reads.size()];
/*      */           
/* 1588 */           for (int i = 0; i < reads.size(); i++)
/*      */           {
/* 1590 */             reads_a[i] = ((Boolean)reads.get(i)).booleanValue();
/*      */           }
/*      */           
/* 1593 */           subs.getHistory().markResults(rids_a, reads_a);
/*      */           
/* 1595 */           result.put("rids", rids);
/* 1596 */           result.put("reads", reads);
/*      */           
/* 1598 */           sendBrowserMessage("metasearch", "markSubscriptionResultsCompleted", result);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1602 */         Debug.out(e);
/*      */         
/* 1604 */         result.put("error", "mark failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1606 */         sendBrowserMessage("metasearch", "markSubscriptionResultsFailed", result);
/*      */       }
/* 1608 */     } else if ("download-subscription".equals(opid))
/*      */     {
/* 1610 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1612 */       Long tid = (Long)decodedMap.get("tid");
/*      */       
/* 1614 */       String sid = (String)decodedMap.get("id");
/*      */       
/* 1616 */       final Map result = new HashMap();
/*      */       
/* 1618 */       if (tid != null) result.put("tid", tid);
/*      */       try
/*      */       {
/* 1621 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(sid);
/*      */         
/* 1623 */         if (subs == null)
/*      */         {
/* 1625 */           result.put("error", "Subscription not found");
/*      */           
/* 1627 */           sendBrowserMessage("metasearch", "downloadSubscriptionFailed", result);
/*      */         }
/*      */         else
/*      */         {
/* 1631 */           result.put("id", subs.getID());
/*      */           
/* 1633 */           if (!handleSubscriptionAuth(subs, result))
/*      */           {
/* 1635 */             subs.getManager().getScheduler().download(subs, false, new SubscriptionDownloadListener()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public void complete(Subscription subs)
/*      */               {
/*      */ 
/*      */ 
/* 1644 */                 if (!MetaSearchListener.this.handleSubscriptionAuth(subs, result))
/*      */                 {
/* 1646 */                   MetaSearchListener.this.encodeResults(subs, result);
/*      */                   
/* 1648 */                   MetaSearchListener.this.sendBrowserMessage("metasearch", "downloadSubscriptionCompleted", result);
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void failed(Subscription subs, SubscriptionException error)
/*      */               {
/* 1657 */                 Debug.out(error);
/*      */                 
/* 1659 */                 result.put("error", "read failed: " + Debug.getNestedExceptionMessage(error));
/*      */                 
/* 1661 */                 MetaSearchListener.this.sendBrowserMessage("metasearch", "downloadSubscriptionFailed", result);
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1669 */         Debug.out(e);
/*      */         
/* 1671 */         result.put("error", "read failed: " + Debug.getNestedExceptionMessage(e));
/*      */         
/* 1673 */         sendBrowserMessage("metasearch", "downloadSubscriptionFailed", result);
/*      */       }
/* 1675 */     } else if ("is-customized".equals(opid))
/*      */     {
/* 1677 */       boolean custom = CustomizationManagerFactory.getSingleton().getActiveCustomization() != null;
/*      */       
/* 1679 */       Map params = new HashMap();
/*      */       
/* 1681 */       params.put("is_custom", Boolean.valueOf(custom));
/*      */       
/* 1683 */       sendBrowserMessage("metasearch", "isCustomizedResult", params);
/* 1684 */     } else if ("add-external-links".equals(opid)) {
/* 1685 */       Map decodedMap = message.getDecodedMap();
/*      */       
/* 1687 */       List list = MapUtils.getMapList(decodedMap, "external-links", Collections.EMPTY_LIST);
/* 1688 */       for (Object o : list) {
/* 1689 */         if ((o instanceof String)) {
/* 1690 */           String link = (String)o;
/* 1691 */           PlatformConfigMessenger.addLinkExternal(link);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean handleSubscriptionAuth(final Subscription subs, final Map result)
/*      */   {
/* 1702 */     if (subs.getHistory().isAuthFail()) {
/*      */       try
/*      */       {
/* 1705 */         Engine engine = subs.getEngine();
/*      */         
/* 1707 */         if ((engine instanceof WebEngine))
/*      */         {
/* 1709 */           final WebEngine webEngine = (WebEngine)engine;
/*      */           
/* 1711 */           synchronized (active_subs_auth)
/*      */           {
/* 1713 */             if (active_subs_auth.contains(subs))
/*      */             {
/* 1715 */               return false;
/*      */             }
/*      */             
/* 1718 */             active_subs_auth.add(subs);
/*      */           }
/*      */           
/* 1721 */           Utils.execSWTThread(new Runnable() {
/*      */             public void run() {
/* 1723 */               new ExternalLoginWindow(new ExternalLoginListener()
/*      */               {
/*      */                 private String previous_cookies;
/*      */                 
/*      */ 
/*      */                 private boolean result_sent;
/*      */                 
/*      */ 
/*      */                 public void canceled(ExternalLoginWindow window)
/*      */                 {
/*      */                   try
/*      */                   {
/* 1735 */                     MetaSearchListener.this.encodeResults(MetaSearchListener.10.this.val$subs, MetaSearchListener.10.this.val$result);
/*      */                     
/* 1737 */                     MetaSearchListener.this.sendBrowserMessage("metasearch", "readSubscriptionResultsCompleted", MetaSearchListener.10.this.val$result);
/*      */                   }
/*      */                   finally
/*      */                   {
/* 1741 */                     completed();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void cookiesFound(ExternalLoginWindow window, String cookies)
/*      */                 {
/* 1750 */                   if (handleCookies(cookies, false))
/*      */                   {
/* 1752 */                     window.close();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */                 public void done(ExternalLoginWindow window, String cookies)
/*      */                 {
/*      */                   try
/*      */                   {
/* 1762 */                     if (!handleCookies(cookies, true))
/*      */                     {
/* 1764 */                       MetaSearchListener.this.encodeResults(MetaSearchListener.10.this.val$subs, MetaSearchListener.10.this.val$result);
/*      */                       
/* 1766 */                       MetaSearchListener.this.sendBrowserMessage("metasearch", "readSubscriptionResultsCompleted", MetaSearchListener.10.this.val$result);
/*      */                     }
/*      */                   }
/*      */                   finally {
/* 1770 */                     completed();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */                 private void completed()
/*      */                 {
/* 1777 */                   synchronized (MetaSearchListener.active_subs_auth)
/*      */                   {
/* 1779 */                     MetaSearchListener.active_subs_auth.remove(MetaSearchListener.10.this.val$subs);
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 private boolean handleCookies(String cookies, boolean force_if_ready)
/*      */                 {
/* 1788 */                   if (this.result_sent)
/*      */                   {
/* 1790 */                     return false;
/*      */                   }
/*      */                   
/* 1793 */                   String[] required = MetaSearchListener.10.this.val$webEngine.getRequiredCookies();
/*      */                   
/* 1795 */                   boolean skip = (required.length == 0) && (!force_if_ready);
/*      */                   
/* 1797 */                   if (CookieParser.cookiesContain(required, cookies))
/*      */                   {
/* 1799 */                     MetaSearchListener.10.this.val$webEngine.setCookies(cookies);
/*      */                     
/* 1801 */                     if ((this.previous_cookies == null) || (!this.previous_cookies.equals(cookies)))
/*      */                     {
/* 1803 */                       this.previous_cookies = cookies;
/*      */                       
/* 1805 */                       if (!skip)
/*      */                       {
/*      */ 
/*      */ 
/* 1809 */                         this.result_sent = true;
/*      */                         try
/*      */                         {
/* 1812 */                           MetaSearchListener.10.this.val$subs.getManager().getScheduler().download(MetaSearchListener.10.this.val$subs, false, new SubscriptionDownloadListener()
/*      */                           {
/*      */ 
/*      */ 
/*      */ 
/*      */                             public void complete(Subscription subs)
/*      */                             {
/*      */ 
/*      */ 
/* 1821 */                               MetaSearchListener.10.this.val$result.put("id", subs.getID());
/*      */                               
/* 1823 */                               MetaSearchListener.this.encodeResults(subs, MetaSearchListener.10.this.val$result);
/*      */                               
/* 1825 */                               MetaSearchListener.this.sendBrowserMessage("metasearch", "readSubscriptionResultsCompleted", MetaSearchListener.10.this.val$result);
/*      */                             }
/*      */                             
/*      */ 
/*      */ 
/*      */ 
/*      */                             public void failed(Subscription subs, SubscriptionException error)
/*      */                             {
/* 1833 */                               Debug.out(error);
/*      */                               
/* 1835 */                               MetaSearchListener.10.this.val$result.put("error", "read failed: " + Debug.getNestedExceptionMessage(error));
/*      */                               
/* 1837 */                               MetaSearchListener.this.sendBrowserMessage("metasearch", "readSubscriptionResultsFailed", MetaSearchListener.10.this.val$result);
/*      */                             }
/*      */                           });
/*      */                         }
/*      */                         catch (Throwable error) {
/* 1842 */                           Debug.out(error);
/*      */                           
/* 1844 */                           MetaSearchListener.10.this.val$result.put("error", "read failed: " + Debug.getNestedExceptionMessage(error));
/*      */                           
/* 1846 */                           MetaSearchListener.this.sendBrowserMessage("metasearch", "readSubscriptionResultsFailed", MetaSearchListener.10.this.val$result);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/* 1852 */                   return this.result_sent; } }, webEngine.getName(), webEngine.getLoginPageUrl(), false, webEngine.getAuthMethod(), subs.isMine());
/*      */ 
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1862 */           });
/* 1863 */           return true;
/*      */         }
/*      */         
/*      */ 
/* 1867 */         return false;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1871 */         Debug.printStackTrace(e);
/*      */         
/* 1873 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1877 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void search(Map decodedMap, Engine target)
/*      */   {
/* 1886 */     String searchText = (String)decodedMap.get("searchText");
/*      */     
/* 1888 */     String headers = (String)decodedMap.get("headers");
/*      */     
/* 1890 */     final Long sid = (Long)decodedMap.get("sid");
/*      */     
/* 1892 */     Boolean mature = (Boolean)decodedMap.get("mature");
/*      */     
/* 1894 */     Long l_max_per_engine = (Long)decodedMap.get("maxResultsPerEngine");
/*      */     
/* 1896 */     int max_per_engine = l_max_per_engine == null ? 100 : l_max_per_engine.intValue();
/*      */     
/* 1898 */     if (max_per_engine < 1)
/*      */     {
/* 1900 */       max_per_engine = 1;
/*      */     }
/*      */     
/* 1903 */     if (target == null)
/*      */     {
/*      */ 
/*      */ 
/* 1907 */       String subscriptionId = (String)decodedMap.get("subs_id");
/*      */       
/* 1909 */       if (subscriptionId != null)
/*      */       {
/* 1911 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(subscriptionId);
/*      */         
/* 1913 */         if (subs != null) {
/*      */           try
/*      */           {
/* 1916 */             Engine engine = subs.getEngine();
/*      */             
/* 1918 */             if (engine != null)
/*      */             {
/* 1920 */               target = engine;
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1924 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1930 */     ResultListener listener = new ResultListener()
/*      */     {
/*      */       public void contentReceived(Engine engine, String content) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void matchFound(Engine engine, String[] fields) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineFailed(Engine engine, Throwable e)
/*      */       {
/* 1948 */         Debug.out(e);
/*      */         
/* 1950 */         Map params = getParams(engine);
/*      */         
/* 1952 */         params.put("error", Debug.getNestedExceptionMessage(e));
/*      */         
/* 1954 */         MetaSearchListener.this.sendBrowserMessage("metasearch", "engineFailed", params);
/*      */       }
/*      */       
/*      */       public void engineRequiresLogin(Engine engine, Throwable e) {
/* 1958 */         Map params = getParams(engine);
/*      */         
/* 1960 */         params.put("error", Debug.getNestedExceptionMessage(e));
/*      */         
/* 1962 */         MetaSearchListener.this.sendBrowserMessage("metasearch", "engineRequiresLogin", params);
/*      */       }
/*      */       
/*      */       public void resultsComplete(Engine engine)
/*      */       {
/* 1967 */         MetaSearchListener.this.sendBrowserMessage("metasearch", "engineCompleted", getParams(engine));
/*      */       }
/*      */       
/*      */       public void resultsReceived(Engine engine, Result[] results) {
/* 1971 */         Map params = getParams(engine);
/* 1972 */         List resultsList = new ArrayList(results.length);
/* 1973 */         for (int i = 0; i < results.length; i++) {
/* 1974 */           Result result = results[i];
/* 1975 */           resultsList.add(result.toJSONMap());
/*      */         }
/* 1977 */         params.put("results", resultsList);
/* 1978 */         MetaSearchListener.this.sendBrowserMessage("metasearch", "resultsReceived", params);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       protected Map getParams(Engine engine)
/*      */       {
/* 1985 */         Map params = new HashMap();
/* 1986 */         params.put("id", new Long(engine.getId()));
/* 1987 */         params.put("name", engine.getName());
/* 1988 */         params.put("favicon", engine.getIcon());
/* 1989 */         params.put("dl_link_css", engine.getDownloadLinkCSS());
/* 1990 */         params.put("shareable", Boolean.valueOf(engine.isShareable()));
/*      */         
/* 1992 */         if (sid != null) {
/* 1993 */           params.put("sid", sid);
/*      */         }
/* 1995 */         return params;
/*      */       }
/*      */       
/* 1998 */     };
/* 1999 */     List sps = new ArrayList();
/*      */     
/* 2001 */     sps.add(new SearchParameter("s", searchText));
/*      */     
/* 2003 */     if (mature != null)
/*      */     {
/* 2005 */       sps.add(new SearchParameter("m", mature.toString()));
/*      */     }
/*      */     
/* 2008 */     SearchParameter[] parameters = (SearchParameter[])sps.toArray(new SearchParameter[sps.size()]);
/*      */     
/* 2010 */     MetaSearchManager metaSearchManager = MetaSearchManagerFactory.getSingleton();
/*      */     
/* 2012 */     Map<String, String> context = new HashMap();
/*      */     
/* 2014 */     context.put("force_full", "true");
/*      */     
/* 2016 */     context.put("batch_millis", "1000");
/*      */     
/* 2018 */     context.put("remove_dup_hash", "true");
/*      */     
/* 2020 */     if (target == null)
/*      */     {
/* 2022 */       metaSearchManager.getMetaSearch().search(listener, parameters, headers, context, max_per_engine);
/*      */     }
/*      */     else
/*      */     {
/* 2026 */       metaSearchManager.getMetaSearch().search(new Engine[] { target }, listener, parameters, headers, context, max_per_engine);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void encodeResults(Subscription subs, Map result)
/*      */   {
/* 2036 */     JSONArray results_list = new JSONArray();
/*      */     
/* 2038 */     SubscriptionResult[] results = subs.getHistory().getResults(false);
/*      */     
/* 2040 */     for (int i = 0; i < results.length; i++)
/*      */     {
/* 2042 */       SubscriptionResult r = results[i];
/*      */       
/* 2044 */       results_list.add(r.toJSONMap());
/*      */     }
/*      */     
/* 2047 */     result.put("results", results_list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Engine getEngineFromId(long id)
/*      */   {
/* 2056 */     MetaSearchManager metaSearchManager = MetaSearchManagerFactory.getSingleton();
/*      */     
/* 2058 */     Engine[] engines = metaSearchManager.getMetaSearch().getEngines(false, true);
/* 2059 */     for (int i = 0; i < engines.length; i++) {
/* 2060 */       Engine engine = engines[i];
/* 2061 */       if (engine.getId() == id) {
/* 2062 */         return engine;
/*      */       }
/*      */     }
/* 2065 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean sendBrowserMessage(String key, String op, Map params)
/*      */   {
/* 2074 */     MetaSearchManagerFactory.getSingleton().log("BrowserListener: sent " + op + ": " + params);
/*      */     
/* 2076 */     return this.context.sendBrowserMessage(key, op, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean sendBrowserMessage(String key, String op, Collection params)
/*      */   {
/* 2085 */     MetaSearchManagerFactory.getSingleton().log("BrowserListener: sent " + op + ": " + params);
/*      */     
/* 2087 */     return this.context.sendBrowserMessage(key, op, params);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/MetaSearchListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */