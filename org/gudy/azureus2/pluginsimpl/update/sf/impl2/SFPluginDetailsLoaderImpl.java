/*     */ package org.gudy.azureus2.pluginsimpl.update.sf.impl2;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsException;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoader;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoaderListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SFPluginDetailsLoaderImpl
/*     */   implements SFPluginDetailsLoader, ResourceDownloaderListener
/*     */ {
/*  61 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   private static final String site_prefix_default = "http://plugins.vuze.com/";
/*     */   private static String site_prefix;
/*     */   private static String base_url_params;
/*     */   private static String page_url;
/*     */   private static SFPluginDetailsLoaderImpl singleton;
/*     */   
/*     */   static {
/*  69 */     try { Map data = VersionCheckClient.getSingleton().getVersionCheckInfo("pu");
/*     */       
/*  71 */       byte[] b_sp = (byte[])data.get("plugin_update_url");
/*     */       
/*  73 */       if (b_sp == null)
/*     */       {
/*  75 */         site_prefix = "http://plugins.vuze.com/";
/*     */       }
/*     */       else
/*     */       {
/*  79 */         site_prefix = new String(b_sp);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/*  83 */       site_prefix = "http://plugins.vuze.com/";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */     base_url_params = "version=5.7.6.0&app=" + SystemProperties.getApplicationName();
/*     */     try
/*     */     {
/*  93 */       base_url_params = base_url_params + "&os=" + URLEncoder.encode(System.getProperty("os.name"), "UTF-8");
/*     */       
/*  95 */       base_url_params = base_url_params + "&osv=" + URLEncoder.encode(System.getProperty("os.version"), "UTF-8");
/*     */       
/*  97 */       base_url_params = base_url_params + "&arch=" + URLEncoder.encode(System.getProperty("os.arch"), "UTF-8");
/*     */       
/*  99 */       base_url_params = base_url_params + "&ui=" + URLEncoder.encode(COConfigurationManager.getStringParameter("ui"), "UTF-8");
/*     */       
/* 101 */       base_url_params = base_url_params + "&java=" + URLEncoder.encode(Constants.JAVA_VERSION, "UTF-8");
/*     */       
/* 103 */       if (Constants.API_LEVEL > 0)
/*     */       {
/* 105 */         base_url_params = base_url_params + "&api_level=" + Constants.API_LEVEL;
/*     */       }
/*     */       try
/*     */       {
/* 109 */         Class c = Class.forName("org.eclipse.swt.SWT");
/*     */         
/* 111 */         String swt_platform = (String)c.getMethod("getPlatform", new Class[0]).invoke(null, new Object[0]);
/*     */         
/* 113 */         base_url_params = base_url_params + "&swt_platform=" + swt_platform;
/*     */         
/* 115 */         Integer swt_version = (Integer)c.getMethod("getVersion", new Class[0]).invoke(null, new Object[0]);
/*     */         
/* 117 */         base_url_params = base_url_params + "&swt_version=" + swt_version;
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 123 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/*     */ 
/* 127 */     page_url = site_prefix + "update/pluginlist3.php?type=&" + base_url_params;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 132 */       PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*     */       
/* 134 */       if (pm.hasCapability(PlatformManagerCapabilities.GetVersion))
/*     */       {
/* 136 */         page_url = page_url + "&pmv=" + pm.getVersion();
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 145 */   private static AEMonitor class_mon = new AEMonitor("SFPluginDetailsLoader:class");
/*     */   private static final int RELOAD_MIN_TIME = 3600000;
/*     */   protected boolean plugin_ids_loaded;
/*     */   protected long plugin_ids_loaded_at;
/*     */   protected List plugin_ids;
/*     */   protected Map plugin_map;
/*     */   
/*     */   public static SFPluginDetailsLoader getSingleton() {
/* 153 */     try { class_mon.enter();
/*     */       
/* 155 */       if (singleton == null)
/*     */       {
/* 157 */         singleton = new SFPluginDetailsLoaderImpl();
/*     */       }
/*     */       
/* 160 */       return singleton;
/*     */     }
/*     */     finally {
/* 163 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 173 */   protected List listeners = new ArrayList();
/*     */   
/* 175 */   protected ResourceDownloaderFactory rd_factory = ResourceDownloaderFactoryImpl.getSingleton();
/*     */   
/* 177 */   protected AEMonitor this_mon = new AEMonitor("SFPluginDetailsLoader");
/*     */   
/*     */ 
/*     */   protected SFPluginDetailsLoaderImpl()
/*     */   {
/* 182 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getRelativeURLBase()
/*     */   {
/* 188 */     return site_prefix;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void loadPluginList()
/*     */     throws SFPluginDetailsException
/*     */   {
/*     */     try
/*     */     {
/* 197 */       String page_url_to_use = addEPIDS(page_url);
/*     */       
/* 199 */       URL original_url = new URL(page_url_to_use);
/*     */       
/* 201 */       URL url = original_url;
/* 202 */       Proxy proxy = null;
/* 203 */       AEProxyFactory.PluginProxy plugin_proxy = null;
/*     */       
/* 205 */       boolean tried_proxy = false;
/* 206 */       boolean ok = false;
/*     */       
/* 208 */       if (COConfigurationManager.getBooleanParameter("update.anonymous"))
/*     */       {
/* 210 */         tried_proxy = true;
/*     */         
/* 212 */         plugin_proxy = AEProxyFactory.getPluginProxy("loading plugin details", url);
/*     */         
/* 214 */         if (plugin_proxy == null)
/*     */         {
/* 216 */           throw new SFPluginDetailsException("Proxy not available");
/*     */         }
/*     */         
/*     */ 
/* 220 */         url = plugin_proxy.getURL();
/* 221 */         proxy = plugin_proxy.getProxy();
/*     */       }
/*     */       try
/*     */       {
/*     */         for (;;)
/*     */         {
/*     */           try
/*     */           {
/* 229 */             ResourceDownloader dl = this.rd_factory.create(url, proxy);
/*     */             
/* 231 */             if (plugin_proxy != null)
/*     */             {
/* 233 */               dl.setProperty("URL_HOST", plugin_proxy.getURLHostRewrite());
/*     */             }
/*     */             
/* 236 */             dl = this.rd_factory.getRetryDownloader(dl, 5);
/*     */             
/* 238 */             dl.addListener(this);
/*     */             
/* 240 */             Properties details = new Properties();
/*     */             
/* 242 */             InputStream is = dl.download();
/*     */             
/* 244 */             details.load(is);
/*     */             
/* 246 */             is.close();
/*     */             
/* 248 */             Iterator it = details.keySet().iterator();
/*     */             
/* 250 */             if (it.hasNext())
/*     */             {
/* 252 */               String plugin_id = (String)it.next();
/*     */               
/* 254 */               String data = (String)details.get(plugin_id);
/*     */               
/* 256 */               int pos = 0;
/*     */               
/* 258 */               List bits = new ArrayList();
/*     */               
/* 260 */               if (pos < data.length())
/*     */               {
/* 262 */                 int p1 = data.indexOf(';', pos);
/*     */                 
/* 264 */                 if (p1 == -1)
/*     */                 {
/* 266 */                   bits.add(data.substring(pos).trim());
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/* 271 */                   bits.add(data.substring(pos, p1).trim());
/*     */                   
/* 273 */                   pos = p1 + 1;
/*     */                   
/* 275 */                   continue;
/*     */                 } }
/* 277 */               if (bits.size() < 3) {
/* 278 */                 Logger.log(new LogEvent(LOGID, 3, "SF loadPluginList failed for plugin '" + plugin_id + "'.  Details array is " + bits.size() + " (3 min)"));
/*     */               }
/*     */               else
/*     */               {
/* 282 */                 String version = (String)bits.get(0);
/* 283 */                 String cvs_version = (String)bits.get(1);
/* 284 */                 String name = (String)bits.get(2);
/* 285 */                 String category = "";
/*     */                 
/* 287 */                 if (bits.size() > 3) {
/* 288 */                   category = (String)bits.get(3);
/*     */                 }
/*     */                 
/* 291 */                 this.plugin_ids.add(plugin_id);
/*     */                 
/* 293 */                 this.plugin_map.put(plugin_id.toLowerCase(MessageText.LOCALE_ENGLISH), new SFPluginDetailsImpl(this, plugin_id, version, cvs_version, name, category));
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 298 */               ok = true;
/*     */             }
/*     */             
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 304 */             if (!tried_proxy)
/*     */             {
/* 306 */               tried_proxy = true;
/*     */               
/* 308 */               plugin_proxy = AEProxyFactory.getPluginProxy("loading plugin details", url);
/*     */               
/* 310 */               if (plugin_proxy == null)
/*     */               {
/* 312 */                 throw e;
/*     */               }
/*     */               
/*     */ 
/* 316 */               url = plugin_proxy.getURL();
/* 317 */               proxy = plugin_proxy.getProxy();
/*     */             }
/*     */             else
/*     */             {
/* 321 */               throw e;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 327 */         if (plugin_proxy != null)
/*     */         {
/* 329 */           plugin_proxy.setOK(ok);
/*     */         }
/*     */       }
/*     */       
/* 333 */       this.plugin_ids_loaded = true;
/*     */       
/* 335 */       this.plugin_ids_loaded_at = SystemTime.getCurrentTime();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 339 */       Debug.printStackTrace(e);
/*     */       
/* 341 */       throw new SFPluginDetailsException("Plugin list load failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private String addEPIDS(String str)
/*     */   {
/*     */     try
/*     */     {
/* 350 */       String pids = "";
/*     */       
/* 352 */       PluginInterface[] pis = PluginInitializer.getDefaultInterface().getPluginManager().getPluginInterfaces();
/*     */       
/* 354 */       for (PluginInterface pi : pis)
/*     */       {
/* 356 */         PluginState ps = pi.getPluginState();
/*     */         
/* 358 */         if ((!ps.isBuiltIn()) && (!ps.isDisabled()))
/*     */         {
/* 360 */           String version = pi.getPluginVersion();
/*     */           
/* 362 */           if ((version != null) && (Constants.compareVersions(version, "0") > 0))
/*     */           {
/* 364 */             String pid = pi.getPluginID();
/*     */             
/* 366 */             if ((pid != null) && (pid.length() > 0))
/*     */             {
/* 368 */               pids = pids + pid + ":";
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 374 */       str = str + "&epids=" + UrlUtils.encode(pids);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 378 */       Debug.out(e);
/*     */     }
/*     */     
/* 381 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void loadPluginDetails(SFPluginDetailsImpl details)
/*     */     throws SFPluginDetailsException
/*     */   {
/*     */     try
/*     */     {
/* 391 */       String page_url_to_use = site_prefix + "update/pluginlist3.php?plugin=" + UrlUtils.encode(details.getId()) + "&" + base_url_params;
/*     */       
/*     */ 
/* 394 */       page_url_to_use = addEPIDS(page_url_to_use);
/*     */       try
/*     */       {
/* 397 */         PluginInterface defPI = PluginInitializer.getDefaultInterface();
/* 398 */         PluginInterface pi = defPI == null ? null : defPI.getPluginManager().getPluginInterfaceByID(details.getId(), false);
/*     */         
/* 400 */         if (pi != null)
/*     */         {
/* 402 */           String existing_version = pi.getPluginVersion();
/*     */           
/* 404 */           if (existing_version != null)
/*     */           {
/* 406 */             page_url_to_use = page_url_to_use + "&ver_" + details.getId() + "=" + UrlUtils.encode(existing_version);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 411 */         Debug.out(e);
/*     */       }
/*     */       
/* 414 */       URL original_url = new URL(page_url_to_use);
/*     */       
/* 416 */       URL url = original_url;
/* 417 */       Proxy proxy = null;
/* 418 */       AEProxyFactory.PluginProxy plugin_proxy = null;
/*     */       
/* 420 */       boolean tried_proxy = false;
/* 421 */       boolean ok = false;
/*     */       
/* 423 */       if (COConfigurationManager.getBooleanParameter("update.anonymous"))
/*     */       {
/* 425 */         tried_proxy = true;
/*     */         
/* 427 */         plugin_proxy = AEProxyFactory.getPluginProxy("loading plugin details", url);
/*     */         
/* 429 */         if (plugin_proxy == null)
/*     */         {
/* 431 */           throw new SFPluginDetailsException("Proxy not available");
/*     */         }
/*     */         
/*     */ 
/* 435 */         url = plugin_proxy.getURL();
/* 436 */         proxy = plugin_proxy.getProxy();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 444 */         ResourceDownloader p_dl = this.rd_factory.create(url, proxy);
/*     */         
/* 446 */         if (proxy != null)
/*     */         {
/* 448 */           p_dl.setProperty("URL_HOST", original_url.getHost());
/*     */         }
/*     */         
/* 451 */         p_dl = this.rd_factory.getRetryDownloader(p_dl, 5);
/*     */         
/* 453 */         p_dl.addListener(this);
/*     */         
/* 455 */         InputStream is = p_dl.download();
/*     */         try
/*     */         {
/* 458 */           if (!processPluginStream(details, is))
/*     */           {
/* 460 */             throw new SFPluginDetailsException("Plugin details load fails for '" + details.getId() + "': data not found");
/*     */           }
/*     */           
/* 463 */           ok = true;
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/*     */ 
/* 469 */           is.close();
/*     */         }
/*     */       } catch (Throwable e) {
/*     */         for (;;) {
/* 473 */           if (!tried_proxy)
/*     */           {
/* 475 */             tried_proxy = true;
/*     */             
/* 477 */             plugin_proxy = AEProxyFactory.getPluginProxy("loading plugin details", url);
/*     */             
/* 479 */             if (plugin_proxy == null)
/*     */             {
/* 481 */               throw e;
/*     */             }
/*     */             
/*     */ 
/* 485 */             url = plugin_proxy.getURL();
/* 486 */             proxy = plugin_proxy.getProxy();
/*     */           }
/*     */           else
/*     */           {
/* 490 */             throw e;
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 496 */         if (plugin_proxy != null)
/*     */         {
/* 498 */           plugin_proxy.setOK(ok);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 503 */       Debug.printStackTrace(e);
/*     */       
/* 505 */       throw new SFPluginDetailsException("Plugin details load fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean processPluginStream(SFPluginDetailsImpl details, InputStream is)
/*     */   {
/* 511 */     Properties properties = new Properties();
/*     */     try {
/* 513 */       properties.load(is);
/*     */       
/* 515 */       String pid = details.getId();
/*     */       
/* 517 */       String download_url = properties.getProperty(pid + ".dl_link", "");
/* 518 */       download_url = site_prefix + download_url;
/*     */       
/* 520 */       String author = properties.getProperty(pid + ".author", "");
/* 521 */       String desc = properties.getProperty(pid + ".description", "");
/* 522 */       String cvs_download_url = properties.getProperty(pid + ".dl_link_cvs", null);
/* 523 */       cvs_download_url = site_prefix + cvs_download_url;
/*     */       
/* 525 */       String comment = properties.getProperty(pid + ".comment", "");
/*     */       
/* 527 */       String info_url = properties.getProperty(pid + ".info_url", null);
/*     */       
/* 529 */       details.setDetails(download_url, author, cvs_download_url, desc, comment, info_url);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 536 */       return true;
/*     */     } catch (IOException e) {
/* 538 */       Debug.out(e);
/*     */     }
/* 540 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getPluginIDs()
/*     */     throws SFPluginDetailsException
/*     */   {
/*     */     try
/*     */     {
/* 549 */       this.this_mon.enter();
/*     */       
/* 551 */       if (!this.plugin_ids_loaded)
/*     */       {
/* 553 */         loadPluginList();
/*     */       }
/*     */       
/* 556 */       String[] res = new String[this.plugin_ids.size()];
/*     */       
/* 558 */       this.plugin_ids.toArray(res);
/*     */       
/* 560 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 564 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SFPluginDetails getPluginDetails(String name)
/*     */     throws SFPluginDetailsException
/*     */   {
/*     */     try
/*     */     {
/* 575 */       this.this_mon.enter();
/*     */       
/*     */ 
/*     */ 
/* 579 */       getPluginIDs();
/*     */       
/* 581 */       SFPluginDetails details = (SFPluginDetails)this.plugin_map.get(name.toLowerCase(MessageText.LOCALE_ENGLISH));
/*     */       
/* 583 */       if (details == null)
/*     */       {
/* 585 */         throw new SFPluginDetailsException("Plugin '" + name + "' not found");
/*     */       }
/*     */       
/* 588 */       return details;
/*     */     }
/*     */     finally {
/* 591 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SFPluginDetails[] getPluginDetails()
/*     */     throws SFPluginDetailsException
/*     */   {
/* 600 */     String[] plugin_ids = getPluginIDs();
/*     */     
/* 602 */     SFPluginDetails[] res = new SFPluginDetails[plugin_ids.length];
/*     */     
/* 604 */     for (int i = 0; i < plugin_ids.length; i++)
/*     */     {
/* 606 */       res[i] = getPluginDetails(plugin_ids[i]);
/*     */     }
/*     */     
/* 609 */     return res;
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
/*     */   public void reportActivity(ResourceDownloader downloader, String activity)
/*     */   {
/* 631 */     informListeners(activity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 639 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 647 */     informListeners("Error: " + e.getMessage());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void informListeners(String log)
/*     */   {
/* 654 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 656 */       ((SFPluginDetailsLoaderListener)this.listeners.get(i)).log(log);
/*     */     }
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/*     */     try
/*     */     {
/* 664 */       this.this_mon.enter();
/*     */       
/* 666 */       long now = SystemTime.getCurrentTime();
/*     */       
/*     */ 
/*     */ 
/* 670 */       if (now < this.plugin_ids_loaded_at)
/*     */       {
/* 672 */         this.plugin_ids_loaded_at = 0L;
/*     */       }
/*     */       
/* 675 */       if (now - this.plugin_ids_loaded_at > 3600000L)
/*     */       {
/* 677 */         if (Logger.isEnabled()) {
/* 678 */           Logger.log(new LogEvent(LOGID, "SFPluginDetailsLoader: resetting values"));
/*     */         }
/*     */         
/* 681 */         this.plugin_ids_loaded = false;
/*     */         
/* 683 */         this.plugin_ids = new ArrayList();
/* 684 */         this.plugin_map = new HashMap();
/*     */ 
/*     */       }
/* 687 */       else if (Logger.isEnabled()) {
/* 688 */         Logger.log(new LogEvent(LOGID, 1, "SFPluginDetailsLoader: not resetting, cache still valid"));
/*     */       }
/*     */       
/*     */     }
/*     */     finally
/*     */     {
/* 694 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(SFPluginDetailsLoaderListener l)
/*     */   {
/* 702 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(SFPluginDetailsLoaderListener l)
/*     */   {
/* 709 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */   public void reportPercentComplete(ResourceDownloader downloader, int percentage) {}
/*     */   
/*     */   public void reportAmountComplete(ResourceDownloader downloader, long amount) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/update/sf/impl2/SFPluginDetailsLoaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */