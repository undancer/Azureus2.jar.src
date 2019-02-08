/*     */ package com.aelitis.azureus.core.cnetwork.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.crypto.VuzeCryptoManager;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*     */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*     */ import com.aelitis.azureus.util.ImportExportUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureDetails;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContentNetworkVuzeGeneric
/*     */   extends ContentNetworkImpl
/*     */ {
/*     */   private static String URL_SUFFIX;
/*     */   
/*     */   static
/*     */   {
/*  48 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Send Version Info", "locale" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  56 */         boolean send_info = COConfigurationManager.getBooleanParameter("Send Version Info");
/*     */         
/*  58 */         ContentNetworkVuzeGeneric.access$002("azid=" + (send_info ? Base32.encode(VuzeCryptoManager.getSingleton().getPlatformAZID()) : "anonymous") + "&azv=" + "5.7.6.0" + "&locale=" + MessageText.getCurrentLocale().toString() + "&os.name=" + UrlUtils.encode(System.getProperty("os.name")) + "&vzemb=1");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */         String suffix = System.getProperty("url.suffix", null);
/*  65 */         if (suffix != null) {
/*  66 */           ContentNetworkVuzeGeneric.access$084("&" + suffix);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*  72 */   private static final String RPC_ADDRESS = System.getProperty("platform_rpc", "https://vrpc.vuze.com/vzrpc/rpc.php");
/*     */   
/*     */ 
/*  75 */   private Map<Integer, String> service_map = new HashMap();
/*     */   
/*     */ 
/*     */   private Set<Integer> service_exclusions;
/*     */   
/*     */ 
/*     */   private String SITE_HOST;
/*     */   
/*     */ 
/*     */   private String URL_PREFIX;
/*     */   
/*     */ 
/*     */   private String URL_EXT_PREFIX;
/*     */   
/*     */ 
/*     */   private String URL_ICON;
/*     */   
/*     */ 
/*     */   private String URL_RELAY_RPC;
/*     */   
/*     */ 
/*     */   private String URL_AUTHORIZED_RPC;
/*     */   
/*     */ 
/*     */   private String URL_FAQ;
/*     */   
/*     */   private String URL_BLOG;
/*     */   
/*     */   private String URL_FORUMS;
/*     */   
/*     */   private String URL_WIKI;
/*     */   
/*     */ 
/*     */   public ContentNetworkVuzeGeneric(ContentNetworkManagerImpl _manager, long _content_network, long _version, String _name, Map<String, Object> _pprop_defaults, Set<Integer> _service_exclusions, String _site_host, String _url_prefix, String _url_icon, String _url_relay_rpc, String _url_authorised_rpc, String _url_faq, String _url_blog, String _url_forums, String _url_wiki, String _url_ext_prefix)
/*     */   {
/* 110 */     super(_manager, 1L, _content_network, _version, _name, _pprop_defaults);
/*     */     
/* 112 */     this.SITE_HOST = _site_host;
/* 113 */     this.URL_PREFIX = _url_prefix;
/* 114 */     this.URL_ICON = _url_icon;
/* 115 */     this.URL_RELAY_RPC = _url_relay_rpc;
/* 116 */     this.URL_AUTHORIZED_RPC = _url_authorised_rpc;
/* 117 */     this.URL_FAQ = _url_faq;
/* 118 */     this.URL_BLOG = _url_blog;
/* 119 */     this.URL_FORUMS = _url_forums;
/* 120 */     this.URL_WIKI = _url_wiki;
/* 121 */     this.URL_EXT_PREFIX = _url_ext_prefix;
/*     */     
/* 123 */     this.service_exclusions = _service_exclusions;
/*     */     
/* 125 */     init();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ContentNetworkVuzeGeneric(ContentNetworkManagerImpl _manager, Map<String, Object> _map)
/*     */     throws IOException
/*     */   {
/* 135 */     super(_manager);
/*     */     
/* 137 */     importFromBEncodedMap(_map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void importFromBEncodedMap(Map<String, Object> map)
/*     */     throws IOException
/*     */   {
/* 146 */     super.importFromBEncodedMap(map);
/*     */     
/* 148 */     this.SITE_HOST = ImportExportUtils.importString(map, "vg_site");
/* 149 */     this.URL_PREFIX = ImportExportUtils.importString(map, "vg_prefix");
/* 150 */     this.URL_EXT_PREFIX = ImportExportUtils.importString(map, "vg_ext_prefix");
/* 151 */     if (this.URL_EXT_PREFIX == null) {
/* 152 */       this.URL_EXT_PREFIX = this.URL_PREFIX;
/*     */     }
/* 154 */     this.URL_ICON = ImportExportUtils.importString(map, "vg_icon");
/* 155 */     this.URL_RELAY_RPC = ImportExportUtils.importString(map, "vg_relay_rpc");
/* 156 */     this.URL_AUTHORIZED_RPC = ImportExportUtils.importString(map, "vg_auth_rpc");
/* 157 */     this.URL_FAQ = ImportExportUtils.importString(map, "vg_faq");
/* 158 */     this.URL_BLOG = ImportExportUtils.importString(map, "vg_blog");
/* 159 */     this.URL_FORUMS = ImportExportUtils.importString(map, "vg_forums");
/* 160 */     this.URL_WIKI = ImportExportUtils.importString(map, "vg_wiki");
/*     */     
/* 162 */     List<Long> sex = (List)map.get("vg_sex");
/*     */     
/* 164 */     if (sex != null)
/*     */     {
/* 166 */       this.service_exclusions = new HashSet();
/*     */       
/* 168 */       for (Long l : sex)
/*     */       {
/* 170 */         this.service_exclusions.add(Integer.valueOf(l.intValue()));
/*     */       }
/*     */     }
/*     */     
/* 174 */     init();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void exportToBEncodedMap(Map map)
/*     */     throws IOException
/*     */   {
/* 183 */     super.exportToBEncodedMap(map);
/*     */     
/* 185 */     ImportExportUtils.exportString(map, "vg_site", this.SITE_HOST);
/* 186 */     ImportExportUtils.exportString(map, "vg_prefix", this.URL_PREFIX);
/* 187 */     ImportExportUtils.exportString(map, "vg_ext_prefix", this.URL_EXT_PREFIX);
/* 188 */     ImportExportUtils.exportString(map, "vg_icon", this.URL_ICON);
/* 189 */     ImportExportUtils.exportString(map, "vg_relay_rpc", this.URL_RELAY_RPC);
/* 190 */     ImportExportUtils.exportString(map, "vg_auth_rpc", this.URL_AUTHORIZED_RPC);
/* 191 */     ImportExportUtils.exportString(map, "vg_faq", this.URL_FAQ);
/* 192 */     ImportExportUtils.exportString(map, "vg_blog", this.URL_BLOG);
/* 193 */     ImportExportUtils.exportString(map, "vg_forums", this.URL_FORUMS);
/* 194 */     ImportExportUtils.exportString(map, "vg_wiki", this.URL_WIKI);
/*     */     
/* 196 */     if (this.service_exclusions != null)
/*     */     {
/* 198 */       List<Long> sex = new ArrayList();
/*     */       
/* 200 */       for (Integer i : this.service_exclusions)
/*     */       {
/* 202 */         sex.add(Long.valueOf(i.longValue()));
/*     */       }
/*     */       
/* 205 */       map.put("vg_sex", sex);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void init()
/*     */   {
/* 212 */     this.service_map.clear();
/*     */     
/* 214 */     addService(2, this.URL_PREFIX + "xsearch/index.php?q=");
/* 215 */     addService(3, RPC_ADDRESS);
/* 216 */     addService(6, this.URL_PREFIX + "browse.start?");
/* 217 */     addService(7, this.URL_PREFIX + "publish.start?");
/* 218 */     addService(8, this.URL_PREFIX + "welcome.start?");
/* 219 */     addService(35, this.URL_PREFIX + "about.start?");
/* 220 */     addService(9, this.URL_PREFIX + "publishnew.start?");
/* 221 */     addService(10, this.URL_PREFIX + "publishinfo.start");
/* 222 */     addService(11, this.URL_PREFIX + "details/");
/* 223 */     addService(12, this.URL_PREFIX + "comment/");
/* 224 */     addService(13, this.URL_PREFIX + "profile/");
/* 225 */     addService(14, this.URL_PREFIX + "download/");
/* 226 */     addService(15, this.URL_PREFIX);
/* 227 */     addService(16, this.URL_EXT_PREFIX + "support/");
/* 228 */     addService(22, this.URL_PREFIX + "login.start?");
/* 229 */     addService(23, this.URL_PREFIX + "logout.start?");
/* 230 */     addService(24, this.URL_PREFIX + "register.start?");
/* 231 */     addService(25, this.URL_PREFIX + "profile.start?");
/* 232 */     addService(26, this.URL_PREFIX + "account.start?");
/* 233 */     addService(27, this.URL_PREFIX);
/* 234 */     addService(37, this.URL_EXT_PREFIX);
/* 235 */     addService(28, this.URL_PREFIX + "user/AddFriend.html?");
/* 236 */     addService(29, this.URL_PREFIX + "xsearch/index.php?");
/*     */     
/* 238 */     addService(31, this.URL_PREFIX + "ip.start?");
/* 239 */     addService(30, this.URL_ICON);
/*     */     
/* 241 */     addService(32, this.URL_PREFIX + "emp/load/");
/* 242 */     addService(33, this.URL_PREFIX + "emp/recommend/");
/* 243 */     addService(34, this.URL_PREFIX + "sidebar.close");
/* 244 */     addService(36, "http://pixel.quantserve.com/pixel/p-64Ix1G_SXwOa-.gif");
/*     */     
/* 246 */     if (this.URL_RELAY_RPC != null)
/*     */     {
/* 248 */       addService(4, this.URL_RELAY_RPC);
/*     */     }
/*     */     
/* 251 */     if (this.URL_AUTHORIZED_RPC != null)
/*     */     {
/* 253 */       addService(5, this.URL_AUTHORIZED_RPC);
/*     */     }
/*     */     
/* 256 */     if (this.URL_FAQ != null)
/*     */     {
/* 258 */       addService(17, this.URL_FAQ);
/* 259 */       addService(18, this.URL_FAQ);
/*     */     }
/*     */     
/* 262 */     if (this.URL_BLOG != null)
/*     */     {
/* 264 */       addService(19, this.URL_BLOG);
/*     */     }
/*     */     
/* 267 */     if (this.URL_FORUMS != null)
/*     */     {
/* 269 */       addService(20, this.URL_FORUMS);
/*     */     }
/*     */     
/* 272 */     if (this.URL_WIKI != null)
/*     */     {
/* 274 */       addService(21, this.URL_WIKI);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addService(int type, String url_str)
/*     */   {
/* 283 */     this.service_map.put(Integer.valueOf(type), url_str);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Set<Integer> getServiceExclusions()
/*     */   {
/* 289 */     return this.service_exclusions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getProperty(int property)
/*     */   {
/* 296 */     if (property == 1)
/*     */     {
/* 298 */       return this.SITE_HOST;
/*     */     }
/* 300 */     if (property == 2)
/*     */     {
/* 302 */       return Boolean.valueOf(getID() != 1L);
/*     */     }
/* 304 */     if (property == 3)
/*     */     {
/* 306 */       return String.valueOf(getID());
/*     */     }
/*     */     
/*     */ 
/* 310 */     debug("Unknown property");
/*     */     
/* 312 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isServiceSupported(int service_type)
/*     */   {
/* 320 */     if ((this.service_exclusions != null) && (this.service_exclusions.contains(Integer.valueOf(service_type))))
/*     */     {
/* 322 */       return false;
/*     */     }
/*     */     
/* 325 */     return this.service_map.get(Integer.valueOf(service_type)) != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getServiceURL(int service_type)
/*     */   {
/* 332 */     return getServiceURL(service_type, new Object[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServiceURL(int service_type, Object[] params)
/*     */   {
/* 341 */     if ((this.service_exclusions != null) && (this.service_exclusions.contains(Integer.valueOf(service_type))))
/*     */     {
/* 343 */       debug("Service type '" + service_type + "' is excluded");
/*     */       
/* 345 */       return null;
/*     */     }
/*     */     
/* 348 */     String base = (String)this.service_map.get(Integer.valueOf(service_type));
/*     */     
/* 350 */     if (base == null)
/*     */     {
/* 352 */       debug("Unknown service type '" + service_type + "'");
/*     */       
/* 354 */       return null;
/*     */     }
/*     */     
/* 357 */     switch (service_type)
/*     */     {
/*     */ 
/*     */     case 2: 
/* 361 */       String query = (String)params[0];
/* 362 */       boolean to_subscribe = ((Boolean)params[1]).booleanValue();
/*     */       
/* 364 */       String url_str = base + UrlUtils.encode(query) + "&" + URL_SUFFIX + "&rand=" + SystemTime.getCurrentTime();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 370 */       if (to_subscribe)
/*     */       {
/* 372 */         url_str = url_str + "&createSubscription=1";
/*     */       }
/*     */       
/* 375 */       String extension_key = getExtensionKey();
/*     */       
/* 377 */       if (extension_key != null)
/*     */       {
/* 379 */         url_str = url_str + "&extension_key=" + UrlUtils.encode(extension_key);
/*     */       }
/*     */       
/* 382 */       url_str = url_str + "&fud=" + UrlUtils.encode(MetaSearchManagerFactory.getSingleton().getMetaSearch().getFUD());
/*     */       
/* 384 */       return url_str;
/*     */     
/*     */ 
/*     */     case 11: 
/* 388 */       String hash = (String)params[0];
/* 389 */       String client_ref = (String)params[1];
/*     */       
/* 391 */       String url_str = base + hash + ".html?" + URL_SUFFIX;
/*     */       
/* 393 */       if (client_ref != null)
/*     */       {
/* 395 */         url_str = url_str + "&client_ref=" + UrlUtils.encode(client_ref);
/*     */       }
/*     */       
/* 398 */       return url_str;
/*     */     
/*     */ 
/*     */     case 32: 
/*     */     case 33: 
/* 403 */       String hash = (String)params[0];
/*     */       
/* 405 */       String url_str = base + hash + "?" + URL_SUFFIX;
/*     */       
/* 407 */       return url_str;
/*     */     
/*     */ 
/*     */     case 12: 
/* 411 */       String hash = (String)params[0];
/*     */       
/* 413 */       return base + hash + ".html?" + URL_SUFFIX + "&rnd=" + Math.random();
/*     */     
/*     */ 
/*     */     case 13: 
/* 417 */       String login_id = (String)params[0];
/* 418 */       String client_ref = (String)params[1];
/*     */       
/* 420 */       return base + UrlUtils.encode(login_id) + "?" + URL_SUFFIX + "&client_ref=" + UrlUtils.encode(client_ref);
/*     */     
/*     */ 
/*     */     case 14: 
/* 424 */       String hash = (String)params[0];
/* 425 */       String client_ref = (String)params[1];
/*     */       
/* 427 */       String url_str = base + hash + ".torrent";
/*     */       
/* 429 */       if (client_ref != null)
/*     */       {
/* 431 */         url_str = url_str + "?referal=" + UrlUtils.encode(client_ref);
/*     */       }
/*     */       
/* 434 */       url_str = appendURLSuffix(url_str, false, true);
/*     */       
/* 436 */       return url_str;
/*     */     
/*     */ 
/*     */     case 18: 
/* 440 */       String topic = (String)params[0];
/*     */       
/* 442 */       return base + topic;
/*     */     
/*     */ 
/*     */     case 22: 
/* 446 */       String message = (String)params[0];
/*     */       
/* 448 */       if ((message == null) || (message.length() == 0))
/*     */       {
/* 450 */         base = base + URL_SUFFIX;
/*     */       }
/*     */       else
/*     */       {
/* 454 */         base = base + "msg=" + UrlUtils.encode(message);
/*     */         
/* 456 */         base = base + "&" + URL_SUFFIX;
/*     */       }
/*     */       
/* 459 */       return base;
/*     */     
/*     */ 
/*     */     case 25: 
/*     */     case 26: 
/* 464 */       base = base + URL_SUFFIX + "&rand=" + SystemTime.getCurrentTime();
/*     */       
/* 466 */       return base;
/*     */     
/*     */ 
/*     */     case 27: 
/* 470 */       String relative_url = (String)params[0];
/* 471 */       boolean append_suffix = ((Boolean)params[1]).booleanValue();
/*     */       
/* 473 */       base = base + (relative_url.startsWith("/") ? relative_url.substring(1) : relative_url);
/*     */       
/* 475 */       if (append_suffix)
/*     */       {
/* 477 */         base = appendURLSuffix(base, false, true);
/*     */       }
/*     */       
/* 480 */       return base;
/*     */     
/*     */ 
/*     */     case 37: 
/* 484 */       String relative_url = (String)params[0];
/* 485 */       boolean append_suffix = ((Boolean)params[1]).booleanValue();
/*     */       
/* 487 */       base = base + (relative_url.startsWith("/") ? relative_url.substring(1) : relative_url);
/*     */       
/* 489 */       if (append_suffix)
/*     */       {
/* 491 */         base = appendURLSuffix(base, false, true);
/*     */       }
/*     */       
/* 494 */       base = base.replaceAll("&vzemb=1", "");
/*     */       
/* 496 */       return base;
/*     */     
/*     */ 
/*     */     case 28: 
/* 500 */       String colour = (String)params[0];
/*     */       
/* 502 */       base = base + "ts=" + Math.random() + "&bg_color=" + colour + "&" + URL_SUFFIX;
/*     */       
/* 504 */       return base;
/*     */     
/*     */ 
/*     */     case 29: 
/* 508 */       String subs_id = (String)params[0];
/*     */       
/* 510 */       base = base + "subscription=" + subs_id + "&" + URL_SUFFIX;
/*     */       
/* 512 */       return base;
/*     */     
/*     */ 
/*     */     case 31: 
/* 516 */       String sourceRef = params.length > 0 ? (String)params[0] : null;
/*     */       
/* 518 */       if (sourceRef != null)
/*     */       {
/* 520 */         base = base + "sourceref=" + UrlUtils.encode(sourceRef) + "&" + URL_SUFFIX;
/*     */       }
/*     */       
/* 523 */       return base;
/*     */     
/*     */     case 8: 
/* 526 */       String installID = COConfigurationManager.getStringParameter("install.id", "null");
/* 527 */       if (installID.length() == 0) {
/* 528 */         installID = "blank";
/*     */       }
/* 530 */       base = base + "iid=" + UrlUtils.encode(installID) + "&" + URL_SUFFIX;
/* 531 */       return base;
/*     */     
/*     */ 
/*     */ 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 23: 
/*     */     case 24: 
/* 539 */       return base + URL_SUFFIX;
/*     */     
/*     */ 
/*     */     case 35: 
/* 543 */       return base + "azv=" + "5.7.6.0" + "&locale=" + MessageText.getCurrentLocale().toString();
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 548 */     return appendURLSuffix(base, false, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String appendURLSuffix(String url_in, boolean for_post, boolean include_azid)
/*     */   {
/* 559 */     if (url_in.contains("vzemb="))
/*     */     {
/*     */ 
/*     */ 
/* 563 */       return url_in;
/*     */     }
/*     */     
/*     */ 
/* 567 */     String suffix = URL_SUFFIX;
/*     */     
/* 569 */     if (!include_azid)
/*     */     {
/* 571 */       suffix = suffix.replaceAll("azid=.*?&", "");
/*     */     }
/*     */     
/* 574 */     if (for_post)
/*     */     {
/* 576 */       if (url_in.length() == 0)
/*     */       {
/* 578 */         return suffix;
/*     */       }
/*     */       
/*     */ 
/* 582 */       return url_in + "&" + suffix;
/*     */     }
/*     */     
/*     */ 
/* 586 */     if (url_in.contains("?"))
/*     */     {
/* 588 */       return url_in + "&" + suffix;
/*     */     }
/*     */     
/*     */ 
/* 592 */     return url_in + "?" + suffix;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getExtensionKey()
/*     */   {
/* 600 */     FeatureManager fm = PluginInitializer.getDefaultInterface().getUtilities().getFeatureManager();
/*     */     
/* 602 */     FeatureManager.FeatureDetails[] fds = fm.getFeatureDetails("core");
/*     */     
/* 604 */     for (FeatureManager.FeatureDetails fd : fds)
/*     */     {
/* 606 */       if (!fd.hasExpired())
/*     */       {
/* 608 */         String finger_print = (String)fd.getProperty("Fingerprint");
/*     */         
/* 610 */         if (finger_print != null)
/*     */         {
/* 612 */           return fd.getLicence().getShortID() + "-" + finger_print;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 617 */     fds = fm.getFeatureDetails("no_ads");
/*     */     
/* 619 */     for (FeatureManager.FeatureDetails fd : fds)
/*     */     {
/* 621 */       if (!fd.hasExpired())
/*     */       {
/* 623 */         String finger_print = (String)fd.getProperty("Fingerprint");
/*     */         
/* 625 */         if (finger_print != null)
/*     */         {
/* 627 */           return fd.getLicence().getShortID() + "-" + finger_print;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 632 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/impl/ContentNetworkVuzeGeneric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */