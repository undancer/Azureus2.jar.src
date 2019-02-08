/*      */ package com.aelitis.azureus.core.metasearch.impl.web;
/*      */ 
/*      */ import com.aelitis.azureus.core.metasearch.SearchException;
/*      */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*      */ import com.aelitis.azureus.core.metasearch.impl.DateParser;
/*      */ import com.aelitis.azureus.core.metasearch.impl.DateParserRegex;
/*      */ import com.aelitis.azureus.core.metasearch.impl.EngineImpl;
/*      */ import com.aelitis.azureus.core.metasearch.impl.MetaSearchImpl;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import com.aelitis.azureus.util.UrlFilter;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.json.simple.JSONArray;
/*      */ import org.json.simple.JSONObject;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class WebEngine
/*      */   extends EngineImpl
/*      */ {
/*      */   public static final String AM_TRANSPARENT = "transparent";
/*      */   public static final String AM_PROXY = "proxy";
/*      */   private static final boolean NEEDS_AUTH_DEFAULT = false;
/*      */   private static final boolean AUTOMATIC_DATE_PARSER_DEFAULT = true;
/*   73 */   private static final Pattern baseTagPattern = Pattern.compile("(?i)<base.*?href=\"([^\"]+)\".*?>");
/*   74 */   private static final Pattern rootURLPattern = Pattern.compile("((?:tor:)?https?://[^/]+)");
/*   75 */   private static final Pattern baseURLPattern = Pattern.compile("((?:tor:)?https?://.*/)");
/*      */   
/*      */ 
/*      */   private String searchURLFormat;
/*      */   
/*      */ 
/*      */   private String timeZone;
/*      */   
/*      */ 
/*      */   private boolean automaticDateParser;
/*      */   
/*      */ 
/*      */   private String userDateFormat;
/*      */   
/*      */ 
/*      */   private String downloadLinkCSS;
/*      */   
/*      */ 
/*      */   private FieldMapping[] mappings;
/*      */   
/*      */ 
/*      */   private String rootPage;
/*      */   
/*      */ 
/*      */   private String basePage;
/*      */   
/*      */ 
/*      */   private DateParser dateParser;
/*      */   
/*      */   private boolean needsAuth;
/*      */   
/*      */   private String authMethod;
/*      */   
/*      */   private String loginPageUrl;
/*      */   
/*      */   private String iconUrl;
/*      */   
/*      */   private String[] requiredCookies;
/*      */   
/*      */   private String fullCookies;
/*      */   
/*      */   private String local_cookies;
/*      */   
/*      */ 
/*      */   public WebEngine(MetaSearchImpl meta_search, int type, long id, long last_updated, float rank_bias, String name, String searchURLFormat, String timeZone, boolean automaticDateParser, String userDateFormat, FieldMapping[] mappings, boolean needs_auth, String auth_method, String login_url, String[] required_cookies)
/*      */   {
/*  121 */     super(meta_search, type, id, last_updated, rank_bias, name);
/*      */     
/*  123 */     this.searchURLFormat = searchURLFormat;
/*  124 */     this.timeZone = timeZone;
/*  125 */     this.automaticDateParser = automaticDateParser;
/*  126 */     this.userDateFormat = userDateFormat;
/*  127 */     this.mappings = mappings;
/*  128 */     this.needsAuth = needs_auth;
/*  129 */     this.authMethod = auth_method;
/*  130 */     this.loginPageUrl = login_url;
/*  131 */     this.requiredCookies = required_cookies;
/*      */     
/*  133 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected WebEngine(MetaSearchImpl meta_search, Map map)
/*      */     throws IOException
/*      */   {
/*  145 */     super(meta_search, map);
/*      */     
/*  147 */     this.searchURLFormat = ImportExportUtils.importString(map, "web.search_url_format");
/*  148 */     this.timeZone = ImportExportUtils.importString(map, "web.time_zone");
/*  149 */     this.userDateFormat = ImportExportUtils.importString(map, "web.date_format");
/*  150 */     this.downloadLinkCSS = ImportExportUtils.importString(map, "web.dl_link_css");
/*      */     
/*  152 */     this.needsAuth = ImportExportUtils.importBoolean(map, "web.needs_auth", false);
/*  153 */     this.authMethod = ImportExportUtils.importString(map, "web.auth_method", "transparent");
/*  154 */     this.loginPageUrl = ImportExportUtils.importString(map, "web.login_page");
/*  155 */     this.requiredCookies = ImportExportUtils.importStringArray(map, "web.required_cookies");
/*  156 */     this.fullCookies = ImportExportUtils.importString(map, "web.full_cookies");
/*      */     
/*  158 */     this.automaticDateParser = ImportExportUtils.importBoolean(map, "web.auto_date", true);
/*  159 */     this.iconUrl = ImportExportUtils.importString(map, "web.icon_url");
/*      */     
/*  161 */     List maps = (List)map.get("web.maps");
/*      */     
/*  163 */     this.mappings = new FieldMapping[maps.size()];
/*      */     
/*  165 */     for (int i = 0; i < this.mappings.length; i++)
/*      */     {
/*  167 */       Map m = (Map)maps.get(i);
/*      */       
/*  169 */       this.mappings[i] = new FieldMapping(ImportExportUtils.importString(m, "name"), ((Long)m.get("field")).intValue());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  175 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void exportToBencodedMap(Map map, boolean generic)
/*      */     throws IOException
/*      */   {
/*  185 */     super.exportToBencodedMap(map, generic);
/*      */     
/*  187 */     if (generic)
/*      */     {
/*  189 */       if (this.searchURLFormat != null) {
/*  190 */         ImportExportUtils.exportString(map, "web.search_url_format", this.searchURLFormat);
/*      */       }
/*  192 */       if (this.timeZone != null) {
/*  193 */         ImportExportUtils.exportString(map, "web.time_zone", this.timeZone);
/*      */       }
/*  195 */       if (this.userDateFormat != null) {
/*  196 */         ImportExportUtils.exportString(map, "web.date_format", this.userDateFormat);
/*      */       }
/*  198 */       if (this.downloadLinkCSS != null) {
/*  199 */         ImportExportUtils.exportString(map, "web.dl_link_css", this.downloadLinkCSS);
/*      */       }
/*      */       
/*  202 */       if (this.needsAuth) {
/*  203 */         ImportExportUtils.exportBoolean(map, "web.needs_auth", this.needsAuth);
/*      */       }
/*  205 */       if ((this.authMethod != null) && (!this.authMethod.equals("transparent"))) {
/*  206 */         ImportExportUtils.exportString(map, "web.auth_method", this.authMethod);
/*      */       }
/*  208 */       if (this.loginPageUrl != null) {
/*  209 */         ImportExportUtils.exportString(map, "web.login_page", this.loginPageUrl);
/*      */       }
/*  211 */       if (this.iconUrl != null) {
/*  212 */         ImportExportUtils.exportString(map, "web.icon_url", this.iconUrl);
/*      */       }
/*  214 */       if ((this.requiredCookies != null) && (this.requiredCookies.length > 0)) {
/*  215 */         ImportExportUtils.exportStringArray(map, "web.required_cookies", this.requiredCookies);
/*      */       }
/*  217 */       if (this.automaticDateParser != true) {
/*  218 */         ImportExportUtils.exportBoolean(map, "web.auto_date", this.automaticDateParser);
/*      */       }
/*      */     }
/*      */     else {
/*  222 */       ImportExportUtils.exportString(map, "web.search_url_format", this.searchURLFormat);
/*  223 */       ImportExportUtils.exportString(map, "web.time_zone", this.timeZone);
/*  224 */       ImportExportUtils.exportString(map, "web.date_format", this.userDateFormat);
/*  225 */       ImportExportUtils.exportString(map, "web.dl_link_css", this.downloadLinkCSS);
/*      */       
/*  227 */       ImportExportUtils.exportBoolean(map, "web.needs_auth", this.needsAuth);
/*  228 */       ImportExportUtils.exportString(map, "web.auth_method", this.authMethod);
/*  229 */       ImportExportUtils.exportString(map, "web.login_page", this.loginPageUrl);
/*  230 */       ImportExportUtils.exportString(map, "web.icon_url", this.iconUrl);
/*  231 */       ImportExportUtils.exportStringArray(map, "web.required_cookies", this.requiredCookies);
/*  232 */       ImportExportUtils.exportJSONString(map, "web.full_cookies", this.fullCookies);
/*      */       
/*  234 */       ImportExportUtils.exportBoolean(map, "web.auto_date", this.automaticDateParser);
/*      */     }
/*      */     
/*  237 */     List maps = new ArrayList();
/*      */     
/*  239 */     map.put("web.maps", maps);
/*      */     
/*  241 */     for (int i = 0; i < this.mappings.length; i++)
/*      */     {
/*  243 */       FieldMapping fm = this.mappings[i];
/*      */       
/*  245 */       Map m = new HashMap();
/*      */       
/*  247 */       ImportExportUtils.exportString(m, "name", fm.getName());
/*  248 */       m.put("field", new Long(fm.getField()));
/*      */       
/*  250 */       maps.add(m);
/*      */     }
/*      */   }
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
/*      */   protected WebEngine(MetaSearchImpl meta_search, int type, long id, long last_updated, float rank_bias, String name, JSONObject map)
/*      */     throws IOException
/*      */   {
/*  268 */     super(meta_search, type, id, last_updated, rank_bias, name, map);
/*      */     
/*  270 */     this.searchURLFormat = ImportExportUtils.importURL(map, "searchURL");
/*  271 */     this.timeZone = ImportExportUtils.importString(map, "timezone");
/*  272 */     this.userDateFormat = ImportExportUtils.importString(map, "time_format");
/*  273 */     this.downloadLinkCSS = ImportExportUtils.importURL(map, "download_link");
/*      */     
/*      */ 
/*  276 */     this.needsAuth = ImportExportUtils.importBoolean(map, "needs_auth", false);
/*  277 */     this.authMethod = ImportExportUtils.importString(map, "auth_method", "transparent");
/*  278 */     this.loginPageUrl = ImportExportUtils.importURL(map, "login_page");
/*  279 */     this.iconUrl = ImportExportUtils.importURL(map, "icon_url");
/*      */     
/*  281 */     this.requiredCookies = ImportExportUtils.importStringArray(map, "required_cookies");
/*      */     
/*  283 */     this.fullCookies = ImportExportUtils.importString(map, "full_cookies");
/*      */     
/*  285 */     this.automaticDateParser = ((this.userDateFormat == null) || (this.userDateFormat.trim().length() == 0));
/*      */     
/*  287 */     List maps = (List)map.get("column_map");
/*      */     
/*  289 */     List conv_maps = new ArrayList();
/*      */     
/*  291 */     for (int i = 0; i < maps.size(); i++)
/*      */     {
/*  293 */       Map m = (Map)maps.get(i);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  298 */       if (m != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  305 */         Map test = (Map)m.get("mapping");
/*      */         
/*  307 */         if (test != null)
/*      */         {
/*  309 */           m = test;
/*      */         }
/*      */         
/*  312 */         String vuze_field = ImportExportUtils.importString(m, "vuze_field").toUpperCase();
/*      */         
/*  314 */         String field_name = ImportExportUtils.importString(m, "group_nb");
/*      */         
/*  316 */         if (field_name == null)
/*      */         {
/*  318 */           field_name = ImportExportUtils.importString(m, "field_name");
/*      */         }
/*      */         
/*  321 */         if ((vuze_field == null) || (field_name == null))
/*      */         {
/*  323 */           log("Missing field mapping name/value in '" + m + "'");
/*      */         }
/*      */         
/*  326 */         int field_id = vuzeFieldToID(vuze_field);
/*      */         
/*  328 */         if (field_id == -1)
/*      */         {
/*  330 */           log("Unrecognised field mapping '" + vuze_field + "'");
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  335 */           conv_maps.add(new FieldMapping(field_name, field_id)); }
/*      */       }
/*      */     }
/*  338 */     this.mappings = ((FieldMapping[])conv_maps.toArray(new FieldMapping[conv_maps.size()]));
/*      */     
/*  340 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void exportToJSONObject(JSONObject res)
/*      */     throws IOException
/*      */   {
/*  349 */     super.exportToJSONObject(res);
/*      */     
/*  351 */     ImportExportUtils.exportJSONURL(res, "searchURL", this.searchURLFormat);
/*      */     
/*  353 */     ImportExportUtils.exportJSONString(res, "timezone", this.timeZone);
/*      */     
/*  355 */     if (this.downloadLinkCSS != null)
/*      */     {
/*  357 */       ImportExportUtils.exportJSONURL(res, "download_link", this.downloadLinkCSS);
/*      */     }
/*      */     
/*  360 */     ImportExportUtils.exportJSONBoolean(res, "needs_auth", this.needsAuth);
/*  361 */     ImportExportUtils.exportJSONString(res, "auth_method", this.authMethod);
/*  362 */     ImportExportUtils.exportJSONURL(res, "login_page", this.loginPageUrl);
/*  363 */     ImportExportUtils.exportJSONURL(res, "icon_url", this.iconUrl);
/*  364 */     ImportExportUtils.exportJSONStringArray(res, "required_cookies", this.requiredCookies);
/*  365 */     ImportExportUtils.exportJSONString(res, "full_cookies", this.fullCookies);
/*      */     
/*  367 */     if (!this.automaticDateParser)
/*      */     {
/*  369 */       ImportExportUtils.exportJSONString(res, "time_format", this.userDateFormat);
/*      */     }
/*      */     
/*  372 */     JSONArray maps = new JSONArray();
/*      */     
/*  374 */     res.put("column_map", maps);
/*      */     
/*  376 */     for (int i = 0; i < this.mappings.length; i++)
/*      */     {
/*  378 */       FieldMapping fm = this.mappings[i];
/*      */       
/*  380 */       int field_id = fm.getField();
/*      */       
/*  382 */       String field_value = vuzeIDToField(field_id);
/*      */       
/*  384 */       if (field_value == null)
/*      */       {
/*  386 */         log("JSON export: unknown field id " + field_id);
/*      */       }
/*      */       else
/*      */       {
/*  390 */         JSONObject entry = new JSONObject();
/*      */         
/*  392 */         maps.add(entry);
/*      */         
/*  394 */         entry.put("vuze_field", field_value);
/*      */         
/*  396 */         if (getType() == 2)
/*      */         {
/*  398 */           entry.put("field_name", fm.getName());
/*      */         }
/*      */         else
/*      */         {
/*  402 */           entry.put("group_nb", fm.getName());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void init()
/*      */   {
/*      */     try
/*      */     {
/*  412 */       Matcher m = rootURLPattern.matcher(this.searchURLFormat);
/*  413 */       if (m.find()) {
/*  414 */         this.rootPage = m.group(1);
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/*  418 */       this.rootPage = null;
/*      */     }
/*      */     try
/*      */     {
/*  422 */       Matcher m = baseURLPattern.matcher(this.searchURLFormat);
/*  423 */       if (m.find()) {
/*  424 */         this.basePage = m.group(1);
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/*  428 */       this.basePage = null;
/*      */     }
/*      */     
/*  431 */     this.dateParser = new DateParserRegex(this.timeZone, this.automaticDateParser, this.userDateFormat);
/*      */     
/*  433 */     this.local_cookies = getLocalString("cookies");
/*      */     
/*      */ 
/*      */ 
/*  437 */     this.authMethod = this.authMethod.intern();
/*      */     
/*      */ 
/*      */ 
/*  441 */     int cook_pos = this.searchURLFormat.indexOf(":COOKIE:");
/*      */     
/*  443 */     if (cook_pos != -1)
/*      */     {
/*  445 */       String explicit_cookie = this.searchURLFormat.substring(cook_pos + 8);
/*      */       
/*  447 */       setNeedsAuth(true);
/*      */       
/*  449 */       setCookies(explicit_cookie);
/*      */       
/*  451 */       setRequiredCookies(CookieParser.getCookiesNames(explicit_cookie));
/*      */       
/*  453 */       this.searchURLFormat = this.searchURLFormat.substring(0, cook_pos);
/*      */       
/*  455 */       setPublic(false);
/*      */       
/*  457 */       String name = getName();
/*      */       
/*  459 */       int n_pos = name.indexOf(":COOKIE:");
/*      */       
/*  461 */       if (n_pos != -1)
/*      */       {
/*  463 */         setName(name.substring(0, n_pos));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNameEx()
/*      */   {
/*  471 */     String url = getRootPage();
/*      */     
/*  473 */     if ((url == null) || (url.length() == 0))
/*      */     {
/*  475 */       url = this.searchURLFormat;
/*      */     }
/*      */     
/*  478 */     String name = getName();
/*      */     
/*  480 */     if (!name.contains(url))
/*      */     {
/*  482 */       return name + " (" + url + ")";
/*      */     }
/*      */     
/*      */ 
/*  486 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getReferer()
/*      */   {
/*  494 */     return getRootPage();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsContext(String context_key)
/*      */   {
/*      */     try
/*      */     {
/*  502 */       URL url = new URL(this.searchURLFormat);
/*      */       
/*  504 */       String host = url.getHost();
/*      */       
/*  506 */       if (Constants.isAzureusDomain(host))
/*      */       {
/*  508 */         return true;
/*      */       }
/*      */       
/*  511 */       if (UrlFilter.getInstance().isWhitelisted(this.searchURLFormat))
/*      */       {
/*  513 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  519 */       InetAddress iad = AddressUtils.getByName(host);
/*      */       
/*  521 */       if ((iad.isLoopbackAddress()) || (iad.isLinkLocalAddress()) || (iad.isSiteLocalAddress()))
/*      */       {
/*      */ 
/*      */ 
/*  525 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  530 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isShareable()
/*      */   {
/*      */     try
/*      */     {
/*  537 */       return !UrlUtils.containsPasskey(new URL(this.searchURLFormat));
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  541 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAnonymous()
/*      */   {
/*      */     try
/*      */     {
/*  549 */       return AENetworkClassifier.categoriseAddress(new URL(this.searchURLFormat).getHost()) != "Public";
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  553 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected pageDetails getWebPageContent(SearchParameter[] searchParameters, Map<String, String> searchContext, String headers, boolean only_if_modified)
/*      */     throws SearchException
/*      */   {
/*  566 */     return getWebPageContent(searchParameters, searchContext, headers, only_if_modified, null);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected pageDetails getWebPageContent(SearchParameter[] searchParameters, Map<String, String> searchContext, String headers, boolean only_if_modified, pageDetailsVerifier verifier)
/*      */     throws SearchException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 935	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:searchURLFormat	Ljava/lang/String;
/*      */     //   4: astore 6
/*      */     //   6: aload 6
/*      */     //   8: getstatic 942	java/util/Locale:US	Ljava/util/Locale;
/*      */     //   11: invokevirtual 1040	java/lang/String:toLowerCase	(Ljava/util/Locale;)Ljava/lang/String;
/*      */     //   14: astore 7
/*      */     //   16: aload 7
/*      */     //   18: ldc 63
/*      */     //   20: invokevirtual 1035	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*      */     //   23: istore 8
/*      */     //   25: iconst_0
/*      */     //   26: istore 9
/*      */     //   28: iload 8
/*      */     //   30: ifne +23 -> 53
/*      */     //   33: aload 6
/*      */     //   35: invokestatic 943	com/aelitis/azureus/core/metasearch/Result:adjustLink	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   38: astore 10
/*      */     //   40: aload 10
/*      */     //   42: ldc 63
/*      */     //   44: invokevirtual 1035	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*      */     //   47: ifeq +6 -> 53
/*      */     //   50: iconst_1
/*      */     //   51: istore 9
/*      */     //   53: iload 8
/*      */     //   55: ifne +8 -> 63
/*      */     //   58: iload 9
/*      */     //   60: ifeq +304 -> 364
/*      */     //   63: new 602	java/util/HashMap
/*      */     //   66: dup
/*      */     //   67: invokespecial 1062	java/util/HashMap:<init>	()V
/*      */     //   70: astore_2
/*      */     //   71: iload 8
/*      */     //   73: ifeq +12 -> 85
/*      */     //   76: aload 6
/*      */     //   78: iconst_4
/*      */     //   79: invokevirtual 1030	java/lang/String:substring	(I)Ljava/lang/String;
/*      */     //   82: goto +5 -> 87
/*      */     //   85: aload 6
/*      */     //   87: astore 10
/*      */     //   89: new 598	java/net/URL
/*      */     //   92: dup
/*      */     //   93: aload 10
/*      */     //   95: invokespecial 1057	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   98: astore 11
/*      */     //   100: goto +15 -> 115
/*      */     //   103: astore 12
/*      */     //   105: new 564	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   108: dup
/*      */     //   109: aload 12
/*      */     //   111: invokespecial 945	com/aelitis/azureus/core/metasearch/SearchException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   114: athrow
/*      */     //   115: new 602	java/util/HashMap
/*      */     //   118: dup
/*      */     //   119: invokespecial 1062	java/util/HashMap:<init>	()V
/*      */     //   122: astore 12
/*      */     //   124: aload 12
/*      */     //   126: ldc 56
/*      */     //   128: iconst_1
/*      */     //   129: anewarray 592	java/lang/String
/*      */     //   132: dup
/*      */     //   133: iconst_0
/*      */     //   134: ldc 21
/*      */     //   136: aastore
/*      */     //   137: invokeinterface 1094 3 0
/*      */     //   142: pop
/*      */     //   143: new 593	java/lang/StringBuilder
/*      */     //   146: dup
/*      */     //   147: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   150: ldc 35
/*      */     //   152: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   155: aload 10
/*      */     //   157: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   160: ldc 5
/*      */     //   162: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   165: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   168: aload 11
/*      */     //   170: aload 12
/*      */     //   172: iconst_1
/*      */     //   173: invokestatic 991	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/lang/String;Ljava/net/URL;Ljava/util/Map;Z)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*      */     //   176: astore 13
/*      */     //   178: aload 13
/*      */     //   180: ifnonnull +36 -> 216
/*      */     //   183: new 564	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   186: dup
/*      */     //   187: new 593	java/lang/StringBuilder
/*      */     //   190: dup
/*      */     //   191: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   194: ldc 17
/*      */     //   196: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   199: aload 10
/*      */     //   201: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   204: ldc 5
/*      */     //   206: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   209: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   212: invokespecial 944	com/aelitis/azureus/core/metasearch/SearchException:<init>	(Ljava/lang/String;)V
/*      */     //   215: athrow
/*      */     //   216: aload 13
/*      */     //   218: invokeinterface 1085 1 0
/*      */     //   223: astore 14
/*      */     //   225: aload 13
/*      */     //   227: invokeinterface 1084 1 0
/*      */     //   232: astore 15
/*      */     //   234: iconst_0
/*      */     //   235: istore 16
/*      */     //   237: new 593	java/lang/StringBuilder
/*      */     //   240: dup
/*      */     //   241: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   244: aload 11
/*      */     //   246: invokevirtual 1054	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   249: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   252: aload 11
/*      */     //   254: invokevirtual 1052	java/net/URL:getPort	()I
/*      */     //   257: iconst_m1
/*      */     //   258: if_icmpne +8 -> 266
/*      */     //   261: ldc 1
/*      */     //   263: goto +26 -> 289
/*      */     //   266: new 593	java/lang/StringBuilder
/*      */     //   269: dup
/*      */     //   270: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   273: ldc 8
/*      */     //   275: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   278: aload 11
/*      */     //   280: invokevirtual 1052	java/net/URL:getPort	()I
/*      */     //   283: invokevirtual 1045	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   286: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   289: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   292: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   295: astore 17
/*      */     //   297: aload_0
/*      */     //   298: aload 15
/*      */     //   300: aload 17
/*      */     //   302: aload 14
/*      */     //   304: invokevirtual 1056	java/net/URL:toExternalForm	()Ljava/lang/String;
/*      */     //   307: aload_1
/*      */     //   308: aload_2
/*      */     //   309: aload_3
/*      */     //   310: iload 4
/*      */     //   312: invokespecial 988	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:getWebPageContentSupport	(Ljava/net/Proxy;Ljava/lang/String;Ljava/lang/String;[Lcom/aelitis/azureus/core/metasearch/SearchParameter;Ljava/util/Map;Ljava/lang/String;Z)Lcom/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails;
/*      */     //   315: astore 18
/*      */     //   317: aload 5
/*      */     //   319: ifnull +12 -> 331
/*      */     //   322: aload 5
/*      */     //   324: aload 18
/*      */     //   326: invokeinterface 1082 2 0
/*      */     //   331: iconst_1
/*      */     //   332: istore 16
/*      */     //   334: aload 18
/*      */     //   336: astore 19
/*      */     //   338: aload 13
/*      */     //   340: iload 16
/*      */     //   342: invokeinterface 1083 2 0
/*      */     //   347: aload 19
/*      */     //   349: areturn
/*      */     //   350: astore 20
/*      */     //   352: aload 13
/*      */     //   354: iload 16
/*      */     //   356: invokeinterface 1083 2 0
/*      */     //   361: aload 20
/*      */     //   363: athrow
/*      */     //   364: new 598	java/net/URL
/*      */     //   367: dup
/*      */     //   368: aload 6
/*      */     //   370: invokespecial 1057	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   373: astore 10
/*      */     //   375: aload 10
/*      */     //   377: invokevirtual 1054	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   380: invokestatic 1067	org/gudy/azureus2/core3/util/AENetworkClassifier:categoriseAddress	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   383: ldc 19
/*      */     //   385: if_acmpeq +11 -> 396
/*      */     //   388: new 602	java/util/HashMap
/*      */     //   391: dup
/*      */     //   392: invokespecial 1062	java/util/HashMap:<init>	()V
/*      */     //   395: astore_2
/*      */     //   396: goto +5 -> 401
/*      */     //   399: astore 10
/*      */     //   401: aload_0
/*      */     //   402: aconst_null
/*      */     //   403: aconst_null
/*      */     //   404: aload 6
/*      */     //   406: aload_1
/*      */     //   407: aload_2
/*      */     //   408: aload_3
/*      */     //   409: iload 4
/*      */     //   411: invokespecial 988	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:getWebPageContentSupport	(Ljava/net/Proxy;Ljava/lang/String;Ljava/lang/String;[Lcom/aelitis/azureus/core/metasearch/SearchParameter;Ljava/util/Map;Ljava/lang/String;Z)Lcom/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails;
/*      */     //   414: astore 10
/*      */     //   416: aload 5
/*      */     //   418: ifnull +12 -> 430
/*      */     //   421: aload 5
/*      */     //   423: aload 10
/*      */     //   425: invokeinterface 1082 2 0
/*      */     //   430: aload 10
/*      */     //   432: areturn
/*      */     //   433: astore 10
/*      */     //   435: new 598	java/net/URL
/*      */     //   438: dup
/*      */     //   439: aload 6
/*      */     //   441: invokespecial 1057	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   444: astore 11
/*      */     //   446: ldc 47
/*      */     //   448: aload 11
/*      */     //   450: invokestatic 990	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/lang/String;Ljava/net/URL;)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*      */     //   453: astore 12
/*      */     //   455: aload 12
/*      */     //   457: ifnonnull +6 -> 463
/*      */     //   460: aload 10
/*      */     //   462: athrow
/*      */     //   463: aload 12
/*      */     //   465: invokeinterface 1085 1 0
/*      */     //   470: astore 13
/*      */     //   472: aload 12
/*      */     //   474: invokeinterface 1084 1 0
/*      */     //   479: astore 14
/*      */     //   481: iconst_0
/*      */     //   482: istore 15
/*      */     //   484: new 593	java/lang/StringBuilder
/*      */     //   487: dup
/*      */     //   488: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   491: aload 11
/*      */     //   493: invokevirtual 1054	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   496: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   499: aload 11
/*      */     //   501: invokevirtual 1052	java/net/URL:getPort	()I
/*      */     //   504: iconst_m1
/*      */     //   505: if_icmpne +8 -> 513
/*      */     //   508: ldc 1
/*      */     //   510: goto +26 -> 536
/*      */     //   513: new 593	java/lang/StringBuilder
/*      */     //   516: dup
/*      */     //   517: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   520: ldc 8
/*      */     //   522: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   525: aload 11
/*      */     //   527: invokevirtual 1052	java/net/URL:getPort	()I
/*      */     //   530: invokevirtual 1045	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   533: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   536: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   539: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   542: astore 16
/*      */     //   544: aload_0
/*      */     //   545: aload 14
/*      */     //   547: aload 16
/*      */     //   549: aload 13
/*      */     //   551: invokevirtual 1056	java/net/URL:toExternalForm	()Ljava/lang/String;
/*      */     //   554: aload_1
/*      */     //   555: aload_2
/*      */     //   556: aload_3
/*      */     //   557: iload 4
/*      */     //   559: invokespecial 988	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:getWebPageContentSupport	(Ljava/net/Proxy;Ljava/lang/String;Ljava/lang/String;[Lcom/aelitis/azureus/core/metasearch/SearchParameter;Ljava/util/Map;Ljava/lang/String;Z)Lcom/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails;
/*      */     //   562: astore 17
/*      */     //   564: aload 5
/*      */     //   566: ifnull +12 -> 578
/*      */     //   569: aload 5
/*      */     //   571: aload 17
/*      */     //   573: invokeinterface 1082 2 0
/*      */     //   578: iconst_1
/*      */     //   579: istore 15
/*      */     //   581: aload 17
/*      */     //   583: astore 18
/*      */     //   585: aload 12
/*      */     //   587: iload 15
/*      */     //   589: invokeinterface 1083 2 0
/*      */     //   594: aload 18
/*      */     //   596: areturn
/*      */     //   597: astore 21
/*      */     //   599: aload 12
/*      */     //   601: iload 15
/*      */     //   603: invokeinterface 1083 2 0
/*      */     //   608: aload 21
/*      */     //   610: athrow
/*      */     //   611: astore 11
/*      */     //   613: aload 10
/*      */     //   615: athrow
/*      */     // Line number table:
/*      */     //   Java source line #579	-> byte code offset #0
/*      */     //   Java source line #581	-> byte code offset #6
/*      */     //   Java source line #583	-> byte code offset #16
/*      */     //   Java source line #585	-> byte code offset #25
/*      */     //   Java source line #587	-> byte code offset #28
/*      */     //   Java source line #589	-> byte code offset #33
/*      */     //   Java source line #591	-> byte code offset #40
/*      */     //   Java source line #593	-> byte code offset #50
/*      */     //   Java source line #597	-> byte code offset #53
/*      */     //   Java source line #601	-> byte code offset #63
/*      */     //   Java source line #603	-> byte code offset #71
/*      */     //   Java source line #609	-> byte code offset #89
/*      */     //   Java source line #614	-> byte code offset #100
/*      */     //   Java source line #611	-> byte code offset #103
/*      */     //   Java source line #613	-> byte code offset #105
/*      */     //   Java source line #616	-> byte code offset #115
/*      */     //   Java source line #618	-> byte code offset #124
/*      */     //   Java source line #620	-> byte code offset #143
/*      */     //   Java source line #627	-> byte code offset #178
/*      */     //   Java source line #629	-> byte code offset #183
/*      */     //   Java source line #632	-> byte code offset #216
/*      */     //   Java source line #633	-> byte code offset #225
/*      */     //   Java source line #635	-> byte code offset #234
/*      */     //   Java source line #638	-> byte code offset #237
/*      */     //   Java source line #640	-> byte code offset #297
/*      */     //   Java source line #659	-> byte code offset #317
/*      */     //   Java source line #661	-> byte code offset #322
/*      */     //   Java source line #664	-> byte code offset #331
/*      */     //   Java source line #666	-> byte code offset #334
/*      */     //   Java source line #670	-> byte code offset #338
/*      */     //   Java source line #676	-> byte code offset #364
/*      */     //   Java source line #678	-> byte code offset #375
/*      */     //   Java source line #682	-> byte code offset #388
/*      */     //   Java source line #685	-> byte code offset #396
/*      */     //   Java source line #684	-> byte code offset #399
/*      */     //   Java source line #687	-> byte code offset #401
/*      */     //   Java source line #689	-> byte code offset #416
/*      */     //   Java source line #691	-> byte code offset #421
/*      */     //   Java source line #694	-> byte code offset #430
/*      */     //   Java source line #696	-> byte code offset #433
/*      */     //   Java source line #699	-> byte code offset #435
/*      */     //   Java source line #701	-> byte code offset #446
/*      */     //   Java source line #703	-> byte code offset #455
/*      */     //   Java source line #705	-> byte code offset #460
/*      */     //   Java source line #709	-> byte code offset #463
/*      */     //   Java source line #710	-> byte code offset #472
/*      */     //   Java source line #712	-> byte code offset #481
/*      */     //   Java source line #715	-> byte code offset #484
/*      */     //   Java source line #717	-> byte code offset #544
/*      */     //   Java source line #719	-> byte code offset #564
/*      */     //   Java source line #721	-> byte code offset #569
/*      */     //   Java source line #724	-> byte code offset #578
/*      */     //   Java source line #726	-> byte code offset #581
/*      */     //   Java source line #730	-> byte code offset #585
/*      */     //   Java source line #733	-> byte code offset #611
/*      */     //   Java source line #735	-> byte code offset #613
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	616	0	this	WebEngine
/*      */     //   0	616	1	searchParameters	SearchParameter[]
/*      */     //   0	616	2	searchContext	Map<String, String>
/*      */     //   0	616	3	headers	String
/*      */     //   0	616	4	only_if_modified	boolean
/*      */     //   0	616	5	verifier	pageDetailsVerifier
/*      */     //   4	436	6	searchURL	String
/*      */     //   14	3	7	lc_url	String
/*      */     //   23	49	8	explicit_tor	boolean
/*      */     //   26	33	9	user_tor	boolean
/*      */     //   38	3	10	test	String
/*      */     //   87	113	10	target_resource	String
/*      */     //   373	3	10	url	URL
/*      */     //   399	3	10	e	Throwable
/*      */     //   414	17	10	details	pageDetails
/*      */     //   433	181	10	e	SearchException
/*      */     //   98	181	11	location	URL
/*      */     //   444	82	11	original_url	URL
/*      */     //   611	3	11	f	Throwable
/*      */     //   103	7	12	e	java.net.MalformedURLException
/*      */     //   122	49	12	options	Map<String, Object>
/*      */     //   453	147	12	plugin_proxy	com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy
/*      */     //   176	177	13	plugin_proxy	com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy
/*      */     //   470	80	13	url	URL
/*      */     //   223	80	14	url	URL
/*      */     //   479	67	14	proxy	java.net.Proxy
/*      */     //   232	67	15	proxy	java.net.Proxy
/*      */     //   482	120	15	ok	boolean
/*      */     //   235	120	16	ok	boolean
/*      */     //   542	6	16	proxy_host	String
/*      */     //   295	6	17	proxy_host	String
/*      */     //   562	20	17	details	pageDetails
/*      */     //   315	280	18	details	pageDetails
/*      */     //   336	12	19	localpageDetails1	pageDetails
/*      */     //   350	12	20	localObject1	Object
/*      */     //   597	12	21	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   89	100	103	java/net/MalformedURLException
/*      */     //   237	338	350	finally
/*      */     //   350	352	350	finally
/*      */     //   364	396	399	java/lang/Throwable
/*      */     //   364	432	433	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   484	585	597	finally
/*      */     //   597	599	597	finally
/*      */     //   435	594	611	java/lang/Throwable
/*      */     //   597	611	611	java/lang/Throwable
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private pageDetails getWebPageContentSupport(java.net.Proxy proxy, String proxy_host, String searchURL, SearchParameter[] searchParameters, Map<String, String> searchContext, String headers, boolean only_if_modified)
/*      */     throws SearchException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 593	java/lang/StringBuilder
/*      */     //   3: dup
/*      */     //   4: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   7: ldc 20
/*      */     //   9: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   12: aload_0
/*      */     //   13: invokevirtual 970	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:getName	()Ljava/lang/String;
/*      */     //   16: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   19: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   22: invokestatic 1073	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   25: aload_0
/*      */     //   26: invokevirtual 966	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:requiresLogin	()Z
/*      */     //   29: ifeq +13 -> 42
/*      */     //   32: new 565	com/aelitis/azureus/core/metasearch/SearchLoginException
/*      */     //   35: dup
/*      */     //   36: ldc 51
/*      */     //   38: invokespecial 947	com/aelitis/azureus/core/metasearch/SearchLoginException:<init>	(Ljava/lang/String;)V
/*      */     //   41: athrow
/*      */     //   42: aload_3
/*      */     //   43: invokevirtual 1027	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */     //   46: ldc 65
/*      */     //   48: invokevirtual 1035	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*      */     //   51: istore 8
/*      */     //   53: iload 8
/*      */     //   55: ifne +255 -> 310
/*      */     //   58: aload 4
/*      */     //   60: arraylength
/*      */     //   61: anewarray 592	java/lang/String
/*      */     //   64: astore 9
/*      */     //   66: aload 4
/*      */     //   68: arraylength
/*      */     //   69: anewarray 592	java/lang/String
/*      */     //   72: astore 10
/*      */     //   74: iconst_0
/*      */     //   75: istore 11
/*      */     //   77: iload 11
/*      */     //   79: aload 4
/*      */     //   81: arraylength
/*      */     //   82: if_icmpge +59 -> 141
/*      */     //   85: aload 4
/*      */     //   87: iload 11
/*      */     //   89: aaload
/*      */     //   90: astore 12
/*      */     //   92: aload 9
/*      */     //   94: iload 11
/*      */     //   96: new 593	java/lang/StringBuilder
/*      */     //   99: dup
/*      */     //   100: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   103: ldc 3
/*      */     //   105: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   108: aload 12
/*      */     //   110: invokevirtual 948	com/aelitis/azureus/core/metasearch/SearchParameter:getMatchPattern	()Ljava/lang/String;
/*      */     //   113: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   116: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   119: aastore
/*      */     //   120: aload 10
/*      */     //   122: iload 11
/*      */     //   124: aload 12
/*      */     //   126: invokevirtual 949	com/aelitis/azureus/core/metasearch/SearchParameter:getValue	()Ljava/lang/String;
/*      */     //   129: ldc 33
/*      */     //   131: invokestatic 1059	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   134: aastore
/*      */     //   135: iinc 11 1
/*      */     //   138: goto -61 -> 77
/*      */     //   141: aload_3
/*      */     //   142: aload 9
/*      */     //   144: aload 10
/*      */     //   146: invokestatic 992	com/aelitis/azureus/core/util/GeneralUtils:replaceAll	(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
/*      */     //   149: astore_3
/*      */     //   150: aload 5
/*      */     //   152: invokeinterface 1092 1 0
/*      */     //   157: invokeinterface 1097 1 0
/*      */     //   162: astore 11
/*      */     //   164: aload 11
/*      */     //   166: invokeinterface 1086 1 0
/*      */     //   171: ifeq +139 -> 310
/*      */     //   174: aload 11
/*      */     //   176: invokeinterface 1087 1 0
/*      */     //   181: checkcast 607	java/util/Map$Entry
/*      */     //   184: astore 12
/*      */     //   186: aload 12
/*      */     //   188: invokeinterface 1095 1 0
/*      */     //   193: checkcast 592	java/lang/String
/*      */     //   196: astore 13
/*      */     //   198: aload_0
/*      */     //   199: aload 13
/*      */     //   201: invokevirtual 979	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:supportsContext	(Ljava/lang/String;)Z
/*      */     //   204: ifeq +103 -> 307
/*      */     //   207: aload_3
/*      */     //   208: bipush 63
/*      */     //   210: invokevirtual 1023	java/lang/String:indexOf	(I)I
/*      */     //   213: iconst_m1
/*      */     //   214: if_icmpne +26 -> 240
/*      */     //   217: new 593	java/lang/StringBuilder
/*      */     //   220: dup
/*      */     //   221: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   224: aload_3
/*      */     //   225: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   228: ldc 14
/*      */     //   230: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   233: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   236: astore_3
/*      */     //   237: goto +23 -> 260
/*      */     //   240: new 593	java/lang/StringBuilder
/*      */     //   243: dup
/*      */     //   244: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   247: aload_3
/*      */     //   248: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   251: ldc 4
/*      */     //   253: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   256: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   259: astore_3
/*      */     //   260: aload 12
/*      */     //   262: invokeinterface 1096 1 0
/*      */     //   267: checkcast 592	java/lang/String
/*      */     //   270: astore 14
/*      */     //   272: new 593	java/lang/StringBuilder
/*      */     //   275: dup
/*      */     //   276: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   279: aload_3
/*      */     //   280: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   283: aload 13
/*      */     //   285: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   288: ldc 13
/*      */     //   290: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   293: aload 14
/*      */     //   295: ldc 33
/*      */     //   297: invokestatic 1059	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   300: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   303: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   306: astore_3
/*      */     //   307: goto -143 -> 164
/*      */     //   310: invokestatic 1076	org/gudy/azureus2/plugins/utils/StaticUtilities:getResourceDownloaderFactory	()Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderFactory;
/*      */     //   313: astore 9
/*      */     //   315: aload_3
/*      */     //   316: ldc 38
/*      */     //   318: invokevirtual 1032	java/lang/String:indexOf	(Ljava/lang/String;)I
/*      */     //   321: istore 12
/*      */     //   323: iload 12
/*      */     //   325: ifle +165 -> 490
/*      */     //   328: aload_3
/*      */     //   329: iload 12
/*      */     //   331: bipush 9
/*      */     //   333: iadd
/*      */     //   334: invokevirtual 1030	java/lang/String:substring	(I)Ljava/lang/String;
/*      */     //   337: astore 13
/*      */     //   339: aload_3
/*      */     //   340: iconst_0
/*      */     //   341: iload 12
/*      */     //   343: iconst_1
/*      */     //   344: isub
/*      */     //   345: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   348: astore_3
/*      */     //   349: aload_0
/*      */     //   350: new 593	java/lang/StringBuilder
/*      */     //   353: dup
/*      */     //   354: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   357: ldc 60
/*      */     //   359: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   362: aload_3
/*      */     //   363: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   366: ldc 7
/*      */     //   368: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   371: aload 13
/*      */     //   373: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   376: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   379: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   382: new 598	java/net/URL
/*      */     //   385: dup
/*      */     //   386: aload_3
/*      */     //   387: invokespecial 1057	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   390: astore 10
/*      */     //   392: aload 13
/*      */     //   394: bipush 58
/*      */     //   396: invokevirtual 1023	java/lang/String:indexOf	(I)I
/*      */     //   399: istore 14
/*      */     //   401: aload 13
/*      */     //   403: iconst_0
/*      */     //   404: iload 14
/*      */     //   406: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   409: astore 15
/*      */     //   411: aload 15
/*      */     //   413: ldc 57
/*      */     //   415: invokevirtual 1025	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   418: ifne +13 -> 431
/*      */     //   421: new 564	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   424: dup
/*      */     //   425: ldc 18
/*      */     //   427: invokespecial 944	com/aelitis/azureus/core/metasearch/SearchException:<init>	(Ljava/lang/String;)V
/*      */     //   430: athrow
/*      */     //   431: aload 13
/*      */     //   433: iload 14
/*      */     //   435: iconst_1
/*      */     //   436: iadd
/*      */     //   437: invokevirtual 1030	java/lang/String:substring	(I)Ljava/lang/String;
/*      */     //   440: astore 13
/*      */     //   442: aload_1
/*      */     //   443: ifnonnull +19 -> 462
/*      */     //   446: aload 9
/*      */     //   448: aload 10
/*      */     //   450: aload 13
/*      */     //   452: invokeinterface 1103 3 0
/*      */     //   457: astore 11
/*      */     //   459: goto +17 -> 476
/*      */     //   462: aload 9
/*      */     //   464: aload 10
/*      */     //   466: aload 13
/*      */     //   468: aload_1
/*      */     //   469: invokeinterface 1105 4 0
/*      */     //   474: astore 11
/*      */     //   476: aload 11
/*      */     //   478: ldc 23
/*      */     //   480: ldc 36
/*      */     //   482: invokeinterface 1100 3 0
/*      */     //   487: goto +66 -> 553
/*      */     //   490: aload_0
/*      */     //   491: new 593	java/lang/StringBuilder
/*      */     //   494: dup
/*      */     //   495: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   498: ldc 60
/*      */     //   500: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   503: aload_3
/*      */     //   504: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   507: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   510: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   513: new 598	java/net/URL
/*      */     //   516: dup
/*      */     //   517: aload_3
/*      */     //   518: invokespecial 1057	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   521: astore 10
/*      */     //   523: aload_1
/*      */     //   524: ifnonnull +17 -> 541
/*      */     //   527: aload 9
/*      */     //   529: aload 10
/*      */     //   531: invokeinterface 1101 2 0
/*      */     //   536: astore 11
/*      */     //   538: goto +15 -> 553
/*      */     //   541: aload 9
/*      */     //   543: aload 10
/*      */     //   545: aload_1
/*      */     //   546: invokeinterface 1104 3 0
/*      */     //   551: astore 11
/*      */     //   553: aload_2
/*      */     //   554: ifnull +13 -> 567
/*      */     //   557: aload 11
/*      */     //   559: ldc 26
/*      */     //   561: aload_2
/*      */     //   562: invokeinterface 1100 3 0
/*      */     //   567: aload_0
/*      */     //   568: aload 11
/*      */     //   570: aload 6
/*      */     //   572: invokevirtual 985	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:setHeaders	(Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;Ljava/lang/String;)V
/*      */     //   575: aload_0
/*      */     //   576: getfield 924	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:needsAuth	Z
/*      */     //   579: ifeq +26 -> 605
/*      */     //   582: aload_0
/*      */     //   583: getfield 932	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:local_cookies	Ljava/lang/String;
/*      */     //   586: ifnull +19 -> 605
/*      */     //   589: aload 11
/*      */     //   591: ldc 24
/*      */     //   593: aload_0
/*      */     //   594: getfield 932	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:local_cookies	Ljava/lang/String;
/*      */     //   597: invokeinterface 1100 3 0
/*      */     //   602: goto +33 -> 635
/*      */     //   605: aload_0
/*      */     //   606: getfield 930	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:fullCookies	Ljava/lang/String;
/*      */     //   609: ifnull +26 -> 635
/*      */     //   612: aload_0
/*      */     //   613: getfield 930	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:fullCookies	Ljava/lang/String;
/*      */     //   616: invokevirtual 1022	java/lang/String:length	()I
/*      */     //   619: ifle +16 -> 635
/*      */     //   622: aload 11
/*      */     //   624: ldc 24
/*      */     //   626: aload_0
/*      */     //   627: getfield 930	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:fullCookies	Ljava/lang/String;
/*      */     //   630: invokeinterface 1100 3 0
/*      */     //   635: iload 7
/*      */     //   637: ifeq +51 -> 688
/*      */     //   640: aload_0
/*      */     //   641: ldc 50
/*      */     //   643: invokevirtual 983	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:getLocalString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   646: astore 13
/*      */     //   648: aload_0
/*      */     //   649: ldc 42
/*      */     //   651: invokevirtual 983	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:getLocalString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   654: astore 14
/*      */     //   656: aload 13
/*      */     //   658: ifnull +14 -> 672
/*      */     //   661: aload 11
/*      */     //   663: ldc 28
/*      */     //   665: aload 13
/*      */     //   667: invokeinterface 1100 3 0
/*      */     //   672: aload 14
/*      */     //   674: ifnull +14 -> 688
/*      */     //   677: aload 11
/*      */     //   679: ldc 29
/*      */     //   681: aload 14
/*      */     //   683: invokeinterface 1100 3 0
/*      */     //   688: aconst_null
/*      */     //   689: astore 13
/*      */     //   691: ldc 33
/*      */     //   693: astore 14
/*      */     //   695: aconst_null
/*      */     //   696: astore 15
/*      */     //   698: aload 10
/*      */     //   700: invokevirtual 1055	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */     //   703: ldc 45
/*      */     //   705: invokevirtual 1034	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   708: ifeq +85 -> 793
/*      */     //   711: aload 10
/*      */     //   713: invokevirtual 1056	java/net/URL:toExternalForm	()Ljava/lang/String;
/*      */     //   716: astore 16
/*      */     //   718: aload 10
/*      */     //   720: invokevirtual 1053	java/net/URL:getAuthority	()Ljava/lang/String;
/*      */     //   723: ifnull +14 -> 737
/*      */     //   726: aload 16
/*      */     //   728: ldc 10
/*      */     //   730: ldc 9
/*      */     //   732: invokevirtual 1042	java/lang/String:replaceFirst	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   735: astore 16
/*      */     //   737: aload 16
/*      */     //   739: bipush 63
/*      */     //   741: invokevirtual 1023	java/lang/String:indexOf	(I)I
/*      */     //   744: istore 17
/*      */     //   746: iload 17
/*      */     //   748: iconst_m1
/*      */     //   749: if_icmpeq +13 -> 762
/*      */     //   752: aload 16
/*      */     //   754: iconst_0
/*      */     //   755: iload 17
/*      */     //   757: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   760: astore 16
/*      */     //   762: new 584	java/io/FileInputStream
/*      */     //   765: dup
/*      */     //   766: new 583	java/io/File
/*      */     //   769: dup
/*      */     //   770: new 598	java/net/URL
/*      */     //   773: dup
/*      */     //   774: aload 16
/*      */     //   776: invokespecial 1057	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   779: invokevirtual 1058	java/net/URL:toURI	()Ljava/net/URI;
/*      */     //   782: invokespecial 1013	java/io/File:<init>	(Ljava/net/URI;)V
/*      */     //   785: invokespecial 1014	java/io/FileInputStream:<init>	(Ljava/io/File;)V
/*      */     //   788: astore 13
/*      */     //   790: goto +616 -> 1406
/*      */     //   793: aload_1
/*      */     //   794: ifnonnull +33 -> 827
/*      */     //   797: aload 11
/*      */     //   799: ldc 22
/*      */     //   801: sipush 10000
/*      */     //   804: invokestatic 1017	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   807: invokeinterface 1100 3 0
/*      */     //   812: aload 11
/*      */     //   814: ldc 31
/*      */     //   816: sipush 10000
/*      */     //   819: invokestatic 1017	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   822: invokeinterface 1100 3 0
/*      */     //   827: aload 9
/*      */     //   829: aload 11
/*      */     //   831: invokeinterface 1102 2 0
/*      */     //   836: astore 15
/*      */     //   838: aload 15
/*      */     //   840: invokeinterface 1098 1 0
/*      */     //   845: astore 13
/*      */     //   847: goto +71 -> 918
/*      */     //   850: astore 16
/*      */     //   852: aload 15
/*      */     //   854: ldc 27
/*      */     //   856: invokeinterface 1099 2 0
/*      */     //   861: checkcast 589	java/lang/Long
/*      */     //   864: astore 17
/*      */     //   866: aload 17
/*      */     //   868: ifnull +47 -> 915
/*      */     //   871: aload 17
/*      */     //   873: invokevirtual 1019	java/lang/Long:longValue	()J
/*      */     //   876: ldc2_w 527
/*      */     //   879: lcmp
/*      */     //   880: ifne +35 -> 915
/*      */     //   883: new 573	com/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails
/*      */     //   886: dup
/*      */     //   887: aload 10
/*      */     //   889: aload 10
/*      */     //   891: ldc 1
/*      */     //   893: invokespecial 989	com/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails:<init>	(Ljava/net/URL;Ljava/net/URL;Ljava/lang/String;)V
/*      */     //   896: astore 18
/*      */     //   898: aload 13
/*      */     //   900: ifnull +8 -> 908
/*      */     //   903: aload 13
/*      */     //   905: invokevirtual 1015	java/io/InputStream:close	()V
/*      */     //   908: aconst_null
/*      */     //   909: invokestatic 1073	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   912: aload 18
/*      */     //   914: areturn
/*      */     //   915: aload 16
/*      */     //   917: athrow
/*      */     //   918: aload_0
/*      */     //   919: getfield 924	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:needsAuth	Z
/*      */     //   922: ifeq +119 -> 1041
/*      */     //   925: aload 15
/*      */     //   927: ldc 32
/*      */     //   929: invokeinterface 1099 2 0
/*      */     //   934: checkcast 604	java/util/List
/*      */     //   937: astore 16
/*      */     //   939: new 601	java/util/ArrayList
/*      */     //   942: dup
/*      */     //   943: invokespecial 1061	java/util/ArrayList:<init>	()V
/*      */     //   946: astore 17
/*      */     //   948: aload 16
/*      */     //   950: ifnull +91 -> 1041
/*      */     //   953: iconst_0
/*      */     //   954: istore 18
/*      */     //   956: iload 18
/*      */     //   958: aload 16
/*      */     //   960: invokeinterface 1088 1 0
/*      */     //   965: if_icmpge +76 -> 1041
/*      */     //   968: aload 16
/*      */     //   970: iload 18
/*      */     //   972: invokeinterface 1089 2 0
/*      */     //   977: checkcast 592	java/lang/String
/*      */     //   980: ldc 12
/*      */     //   982: invokevirtual 1039	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
/*      */     //   985: astore 19
/*      */     //   987: iconst_0
/*      */     //   988: istore 20
/*      */     //   990: iload 20
/*      */     //   992: aload 19
/*      */     //   994: arraylength
/*      */     //   995: if_icmpge +40 -> 1035
/*      */     //   998: aload 19
/*      */     //   1000: iload 20
/*      */     //   1002: aaload
/*      */     //   1003: invokevirtual 1029	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   1006: astore 21
/*      */     //   1008: aload 21
/*      */     //   1010: bipush 61
/*      */     //   1012: invokevirtual 1023	java/lang/String:indexOf	(I)I
/*      */     //   1015: iconst_m1
/*      */     //   1016: if_icmpeq +13 -> 1029
/*      */     //   1019: aload 17
/*      */     //   1021: aload 21
/*      */     //   1023: invokeinterface 1090 2 0
/*      */     //   1028: pop
/*      */     //   1029: iinc 20 1
/*      */     //   1032: goto -42 -> 990
/*      */     //   1035: iinc 18 1
/*      */     //   1038: goto -82 -> 956
/*      */     //   1041: iload 7
/*      */     //   1043: ifeq +59 -> 1102
/*      */     //   1046: aload_0
/*      */     //   1047: aload 15
/*      */     //   1049: ldc 30
/*      */     //   1051: invokeinterface 1099 2 0
/*      */     //   1056: invokevirtual 982	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:extractProperty	(Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   1059: astore 16
/*      */     //   1061: aload_0
/*      */     //   1062: aload 15
/*      */     //   1064: ldc 25
/*      */     //   1066: invokeinterface 1099 2 0
/*      */     //   1071: invokevirtual 982	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:extractProperty	(Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   1074: astore 17
/*      */     //   1076: aload 16
/*      */     //   1078: ifnull +11 -> 1089
/*      */     //   1081: aload_0
/*      */     //   1082: ldc 50
/*      */     //   1084: aload 16
/*      */     //   1086: invokevirtual 984	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:setLocalString	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   1089: aload 17
/*      */     //   1091: ifnull +11 -> 1102
/*      */     //   1094: aload_0
/*      */     //   1095: ldc 42
/*      */     //   1097: aload 17
/*      */     //   1099: invokevirtual 984	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:setLocalString	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   1102: aload 15
/*      */     //   1104: ldc 23
/*      */     //   1106: invokeinterface 1099 2 0
/*      */     //   1111: checkcast 604	java/util/List
/*      */     //   1114: astore 16
/*      */     //   1116: aload 16
/*      */     //   1118: ifnull +288 -> 1406
/*      */     //   1121: aload 16
/*      */     //   1123: invokeinterface 1088 1 0
/*      */     //   1128: ifle +278 -> 1406
/*      */     //   1131: aload 16
/*      */     //   1133: iconst_0
/*      */     //   1134: invokeinterface 1089 2 0
/*      */     //   1139: checkcast 592	java/lang/String
/*      */     //   1142: astore 17
/*      */     //   1144: aload 17
/*      */     //   1146: invokevirtual 1027	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */     //   1149: ldc_w 550
/*      */     //   1152: invokevirtual 1032	java/lang/String:indexOf	(Ljava/lang/String;)I
/*      */     //   1155: istore 18
/*      */     //   1157: iload 18
/*      */     //   1159: iconst_m1
/*      */     //   1160: if_icmpeq +246 -> 1406
/*      */     //   1163: aload 17
/*      */     //   1165: iload 18
/*      */     //   1167: iconst_1
/*      */     //   1168: iadd
/*      */     //   1169: invokevirtual 1030	java/lang/String:substring	(I)Ljava/lang/String;
/*      */     //   1172: astore 17
/*      */     //   1174: aload 17
/*      */     //   1176: bipush 61
/*      */     //   1178: invokevirtual 1023	java/lang/String:indexOf	(I)I
/*      */     //   1181: istore 18
/*      */     //   1183: iload 18
/*      */     //   1185: iconst_m1
/*      */     //   1186: if_icmpeq +220 -> 1406
/*      */     //   1189: aload 17
/*      */     //   1191: iload 18
/*      */     //   1193: iconst_1
/*      */     //   1194: iadd
/*      */     //   1195: invokevirtual 1030	java/lang/String:substring	(I)Ljava/lang/String;
/*      */     //   1198: invokevirtual 1029	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   1201: astore 17
/*      */     //   1203: aload 17
/*      */     //   1205: bipush 59
/*      */     //   1207: invokevirtual 1023	java/lang/String:indexOf	(I)I
/*      */     //   1210: istore 18
/*      */     //   1212: iload 18
/*      */     //   1214: iconst_m1
/*      */     //   1215: if_icmpeq +16 -> 1231
/*      */     //   1218: aload 17
/*      */     //   1220: iconst_0
/*      */     //   1221: iload 18
/*      */     //   1223: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   1226: invokevirtual 1029	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   1229: astore 17
/*      */     //   1231: aload 17
/*      */     //   1233: ldc_w 530
/*      */     //   1236: invokevirtual 1035	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*      */     //   1239: ifeq +14 -> 1253
/*      */     //   1242: aload 17
/*      */     //   1244: iconst_1
/*      */     //   1245: invokevirtual 1030	java/lang/String:substring	(I)Ljava/lang/String;
/*      */     //   1248: invokevirtual 1029	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   1251: astore 17
/*      */     //   1253: aload 17
/*      */     //   1255: ldc_w 530
/*      */     //   1258: invokevirtual 1033	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   1261: ifeq +21 -> 1282
/*      */     //   1264: aload 17
/*      */     //   1266: iconst_0
/*      */     //   1267: aload 17
/*      */     //   1269: invokevirtual 1022	java/lang/String:length	()I
/*      */     //   1272: iconst_1
/*      */     //   1273: isub
/*      */     //   1274: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   1277: invokevirtual 1029	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   1280: astore 17
/*      */     //   1282: aload 17
/*      */     //   1284: invokestatic 1060	java/nio/charset/Charset:isSupported	(Ljava/lang/String;)Z
/*      */     //   1287: ifeq +32 -> 1319
/*      */     //   1290: aload_0
/*      */     //   1291: new 593	java/lang/StringBuilder
/*      */     //   1294: dup
/*      */     //   1295: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   1298: ldc_w 553
/*      */     //   1301: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1304: aload 17
/*      */     //   1306: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1309: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1312: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   1315: aload 17
/*      */     //   1317: astore 14
/*      */     //   1319: goto +87 -> 1406
/*      */     //   1322: astore 19
/*      */     //   1324: aload 17
/*      */     //   1326: invokevirtual 1028	java/lang/String:toUpperCase	()Ljava/lang/String;
/*      */     //   1329: astore 17
/*      */     //   1331: aload 17
/*      */     //   1333: invokestatic 1060	java/nio/charset/Charset:isSupported	(Ljava/lang/String;)Z
/*      */     //   1336: ifeq +32 -> 1368
/*      */     //   1339: aload_0
/*      */     //   1340: new 593	java/lang/StringBuilder
/*      */     //   1343: dup
/*      */     //   1344: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   1347: ldc_w 553
/*      */     //   1350: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1353: aload 17
/*      */     //   1355: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1358: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1361: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   1364: aload 17
/*      */     //   1366: astore 14
/*      */     //   1368: goto +38 -> 1406
/*      */     //   1371: astore 20
/*      */     //   1373: aload_0
/*      */     //   1374: new 593	java/lang/StringBuilder
/*      */     //   1377: dup
/*      */     //   1378: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   1381: ldc_w 542
/*      */     //   1384: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1387: aload 17
/*      */     //   1389: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1392: ldc_w 532
/*      */     //   1395: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1398: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1401: aload 20
/*      */     //   1403: invokevirtual 986	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:log	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1406: new 582	java/io/ByteArrayOutputStream
/*      */     //   1409: dup
/*      */     //   1410: sipush 8192
/*      */     //   1413: invokespecial 1011	java/io/ByteArrayOutputStream:<init>	(I)V
/*      */     //   1416: astore 16
/*      */     //   1418: sipush 8192
/*      */     //   1421: newarray <illegal type>
/*      */     //   1423: astore 17
/*      */     //   1425: aload 13
/*      */     //   1427: aload 17
/*      */     //   1429: invokevirtual 1016	java/io/InputStream:read	([B)I
/*      */     //   1432: istore 18
/*      */     //   1434: iload 18
/*      */     //   1436: ifgt +6 -> 1442
/*      */     //   1439: goto +16 -> 1455
/*      */     //   1442: aload 16
/*      */     //   1444: aload 17
/*      */     //   1446: iconst_0
/*      */     //   1447: iload 18
/*      */     //   1449: invokevirtual 1012	java/io/ByteArrayOutputStream:write	([BII)V
/*      */     //   1452: goto -27 -> 1425
/*      */     //   1455: aload 16
/*      */     //   1457: invokevirtual 1010	java/io/ByteArrayOutputStream:toByteArray	()[B
/*      */     //   1460: astore 18
/*      */     //   1462: iload 8
/*      */     //   1464: ifeq +73 -> 1537
/*      */     //   1467: invokestatic 995	com/aelitis/azureus/core/vuzefile/VuzeFileHandler:getSingleton	()Lcom/aelitis/azureus/core/vuzefile/VuzeFileHandler;
/*      */     //   1470: astore 19
/*      */     //   1472: aload 19
/*      */     //   1474: aload 18
/*      */     //   1476: invokevirtual 993	com/aelitis/azureus/core/vuzefile/VuzeFileHandler:loadVuzeFile	([B)Lcom/aelitis/azureus/core/vuzefile/VuzeFile;
/*      */     //   1479: astore 20
/*      */     //   1481: aload 19
/*      */     //   1483: iconst_1
/*      */     //   1484: anewarray 578	com/aelitis/azureus/core/vuzefile/VuzeFile
/*      */     //   1487: dup
/*      */     //   1488: iconst_0
/*      */     //   1489: aload 20
/*      */     //   1491: aastore
/*      */     //   1492: iconst_0
/*      */     //   1493: invokevirtual 994	com/aelitis/azureus/core/vuzefile/VuzeFileHandler:handleFiles	([Lcom/aelitis/azureus/core/vuzefile/VuzeFile;I)V
/*      */     //   1496: goto +10 -> 1506
/*      */     //   1499: astore 19
/*      */     //   1501: aload 19
/*      */     //   1503: invokestatic 1071	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   1506: new 573	com/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails
/*      */     //   1509: dup
/*      */     //   1510: aload 10
/*      */     //   1512: aload 10
/*      */     //   1514: aconst_null
/*      */     //   1515: invokespecial 989	com/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails:<init>	(Ljava/net/URL;Ljava/net/URL;Ljava/lang/String;)V
/*      */     //   1518: astore 19
/*      */     //   1520: aload 13
/*      */     //   1522: ifnull +8 -> 1530
/*      */     //   1525: aload 13
/*      */     //   1527: invokevirtual 1015	java/io/InputStream:close	()V
/*      */     //   1530: aconst_null
/*      */     //   1531: invokestatic 1073	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   1534: aload 19
/*      */     //   1536: areturn
/*      */     //   1537: aconst_null
/*      */     //   1538: astore 19
/*      */     //   1540: new 592	java/lang/String
/*      */     //   1543: dup
/*      */     //   1544: aload 18
/*      */     //   1546: iconst_0
/*      */     //   1547: aload 18
/*      */     //   1549: arraylength
/*      */     //   1550: sipush 2048
/*      */     //   1553: invokestatic 1021	java/lang/Math:min	(II)I
/*      */     //   1556: aload 14
/*      */     //   1558: invokespecial 1037	java/lang/String:<init>	([BIILjava/lang/String;)V
/*      */     //   1561: astore 20
/*      */     //   1563: aload 20
/*      */     //   1565: invokevirtual 1027	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */     //   1568: astore 21
/*      */     //   1570: aload 21
/*      */     //   1572: ldc_w 539
/*      */     //   1575: invokevirtual 1032	java/lang/String:indexOf	(Ljava/lang/String;)I
/*      */     //   1578: istore 22
/*      */     //   1580: iload 22
/*      */     //   1582: iconst_m1
/*      */     //   1583: if_icmpeq +276 -> 1859
/*      */     //   1586: aload 21
/*      */     //   1588: ldc_w 541
/*      */     //   1591: invokevirtual 1032	java/lang/String:indexOf	(Ljava/lang/String;)I
/*      */     //   1594: istore 23
/*      */     //   1596: iload 23
/*      */     //   1598: iconst_m1
/*      */     //   1599: if_icmpeq +260 -> 1859
/*      */     //   1602: aload 21
/*      */     //   1604: ldc_w 554
/*      */     //   1607: iload 22
/*      */     //   1609: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1612: istore 24
/*      */     //   1614: iload 24
/*      */     //   1616: iconst_m1
/*      */     //   1617: if_icmpeq +15 -> 1632
/*      */     //   1620: aload 21
/*      */     //   1622: ldc_w 530
/*      */     //   1625: iload 24
/*      */     //   1627: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1630: istore 24
/*      */     //   1632: iload 24
/*      */     //   1634: iload 22
/*      */     //   1636: if_icmple +223 -> 1859
/*      */     //   1639: iload 24
/*      */     //   1641: iload 23
/*      */     //   1643: if_icmpge +216 -> 1859
/*      */     //   1646: iinc 24 1
/*      */     //   1649: aload 21
/*      */     //   1651: ldc_w 530
/*      */     //   1654: iload 24
/*      */     //   1656: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1659: istore 25
/*      */     //   1661: iload 25
/*      */     //   1663: iload 24
/*      */     //   1665: if_icmple +194 -> 1859
/*      */     //   1668: iload 25
/*      */     //   1670: iload 23
/*      */     //   1672: if_icmpge +187 -> 1859
/*      */     //   1675: aload 20
/*      */     //   1677: iload 24
/*      */     //   1679: iload 25
/*      */     //   1681: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   1684: invokevirtual 1029	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   1687: astore 26
/*      */     //   1689: aload 26
/*      */     //   1691: invokestatic 1060	java/nio/charset/Charset:isSupported	(Ljava/lang/String;)Z
/*      */     //   1694: ifeq +127 -> 1821
/*      */     //   1697: aload_0
/*      */     //   1698: new 593	java/lang/StringBuilder
/*      */     //   1701: dup
/*      */     //   1702: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   1705: ldc_w 552
/*      */     //   1708: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1711: aload 26
/*      */     //   1713: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1716: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1719: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   1722: aload 26
/*      */     //   1724: astore 14
/*      */     //   1726: iload 23
/*      */     //   1728: istore 27
/*      */     //   1730: bipush 64
/*      */     //   1732: istore 28
/*      */     //   1734: aload 18
/*      */     //   1736: iload 27
/*      */     //   1738: baload
/*      */     //   1739: bipush 63
/*      */     //   1741: if_icmpeq +17 -> 1758
/*      */     //   1744: iload 28
/*      */     //   1746: iinc 28 -1
/*      */     //   1749: ifle +9 -> 1758
/*      */     //   1752: iinc 27 1
/*      */     //   1755: goto -21 -> 1734
/*      */     //   1758: new 593	java/lang/StringBuilder
/*      */     //   1761: dup
/*      */     //   1762: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   1765: aload 20
/*      */     //   1767: iconst_0
/*      */     //   1768: iload 24
/*      */     //   1770: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   1773: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1776: ldc_w 558
/*      */     //   1779: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1782: aload 20
/*      */     //   1784: iload 25
/*      */     //   1786: iload 23
/*      */     //   1788: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   1791: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1794: new 592	java/lang/String
/*      */     //   1797: dup
/*      */     //   1798: aload 18
/*      */     //   1800: iload 27
/*      */     //   1802: aload 18
/*      */     //   1804: arraylength
/*      */     //   1805: iload 27
/*      */     //   1807: isub
/*      */     //   1808: aload 14
/*      */     //   1810: invokespecial 1037	java/lang/String:<init>	([BIILjava/lang/String;)V
/*      */     //   1813: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1816: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1819: astore 19
/*      */     //   1821: goto +38 -> 1859
/*      */     //   1824: astore 27
/*      */     //   1826: aload_0
/*      */     //   1827: new 593	java/lang/StringBuilder
/*      */     //   1830: dup
/*      */     //   1831: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   1834: ldc_w 542
/*      */     //   1837: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1840: aload 26
/*      */     //   1842: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1845: ldc_w 532
/*      */     //   1848: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1851: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1854: aload 27
/*      */     //   1856: invokevirtual 986	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:log	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1859: aload 19
/*      */     //   1861: ifnonnull +327 -> 2188
/*      */     //   1864: iconst_0
/*      */     //   1865: istore 22
/*      */     //   1867: aload 21
/*      */     //   1869: ldc_w 555
/*      */     //   1872: iload 22
/*      */     //   1874: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1877: istore 23
/*      */     //   1879: iload 23
/*      */     //   1881: iconst_m1
/*      */     //   1882: if_icmpeq +306 -> 2188
/*      */     //   1885: aload 21
/*      */     //   1887: ldc_w 540
/*      */     //   1890: iload 23
/*      */     //   1892: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1895: istore 24
/*      */     //   1897: iload 24
/*      */     //   1899: iconst_m1
/*      */     //   1900: if_icmpeq +288 -> 2188
/*      */     //   1903: aload 21
/*      */     //   1905: ldc_w 550
/*      */     //   1908: iload 23
/*      */     //   1910: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1913: istore 25
/*      */     //   1915: iload 25
/*      */     //   1917: iconst_m1
/*      */     //   1918: if_icmpeq +263 -> 2181
/*      */     //   1921: iload 25
/*      */     //   1923: iload 24
/*      */     //   1925: if_icmpge +256 -> 2181
/*      */     //   1928: aload 21
/*      */     //   1930: ldc 13
/*      */     //   1932: iload 25
/*      */     //   1934: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1937: istore 25
/*      */     //   1939: iload 25
/*      */     //   1941: iconst_m1
/*      */     //   1942: if_icmpeq +239 -> 2181
/*      */     //   1945: iinc 25 1
/*      */     //   1948: aload 21
/*      */     //   1950: ldc_w 530
/*      */     //   1953: iload 25
/*      */     //   1955: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1958: istore 26
/*      */     //   1960: iload 26
/*      */     //   1962: iconst_m1
/*      */     //   1963: if_icmpeq +218 -> 2181
/*      */     //   1966: aload 21
/*      */     //   1968: ldc 12
/*      */     //   1970: iload 25
/*      */     //   1972: invokevirtual 1036	java/lang/String:indexOf	(Ljava/lang/String;I)I
/*      */     //   1975: istore 27
/*      */     //   1977: iload 27
/*      */     //   1979: iconst_m1
/*      */     //   1980: if_icmpeq +14 -> 1994
/*      */     //   1983: iload 27
/*      */     //   1985: iload 26
/*      */     //   1987: if_icmpge +7 -> 1994
/*      */     //   1990: iload 27
/*      */     //   1992: istore 26
/*      */     //   1994: aload 20
/*      */     //   1996: iload 25
/*      */     //   1998: iload 26
/*      */     //   2000: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   2003: invokevirtual 1029	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   2006: astore 28
/*      */     //   2008: aload 28
/*      */     //   2010: invokestatic 1060	java/nio/charset/Charset:isSupported	(Ljava/lang/String;)Z
/*      */     //   2013: ifeq +127 -> 2140
/*      */     //   2016: aload_0
/*      */     //   2017: new 593	java/lang/StringBuilder
/*      */     //   2020: dup
/*      */     //   2021: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   2024: ldc_w 551
/*      */     //   2027: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2030: aload 28
/*      */     //   2032: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2035: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2038: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   2041: aload 28
/*      */     //   2043: astore 14
/*      */     //   2045: iload 24
/*      */     //   2047: istore 29
/*      */     //   2049: bipush 64
/*      */     //   2051: istore 30
/*      */     //   2053: aload 18
/*      */     //   2055: iload 29
/*      */     //   2057: baload
/*      */     //   2058: bipush 63
/*      */     //   2060: if_icmpeq +17 -> 2077
/*      */     //   2063: iload 30
/*      */     //   2065: iinc 30 -1
/*      */     //   2068: ifle +9 -> 2077
/*      */     //   2071: iinc 29 1
/*      */     //   2074: goto -21 -> 2053
/*      */     //   2077: new 593	java/lang/StringBuilder
/*      */     //   2080: dup
/*      */     //   2081: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   2084: aload 20
/*      */     //   2086: iconst_0
/*      */     //   2087: iload 25
/*      */     //   2089: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   2092: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2095: ldc_w 558
/*      */     //   2098: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2101: aload 20
/*      */     //   2103: iload 26
/*      */     //   2105: iload 24
/*      */     //   2107: invokevirtual 1031	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   2110: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2113: new 592	java/lang/String
/*      */     //   2116: dup
/*      */     //   2117: aload 18
/*      */     //   2119: iload 29
/*      */     //   2121: aload 18
/*      */     //   2123: arraylength
/*      */     //   2124: iload 29
/*      */     //   2126: isub
/*      */     //   2127: aload 14
/*      */     //   2129: invokespecial 1037	java/lang/String:<init>	([BIILjava/lang/String;)V
/*      */     //   2132: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2135: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2138: astore 19
/*      */     //   2140: goto +48 -> 2188
/*      */     //   2143: astore 29
/*      */     //   2145: aload_0
/*      */     //   2146: new 593	java/lang/StringBuilder
/*      */     //   2149: dup
/*      */     //   2150: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   2153: ldc_w 542
/*      */     //   2156: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2159: aload 28
/*      */     //   2161: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2164: ldc_w 532
/*      */     //   2167: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2170: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2173: aload 29
/*      */     //   2175: invokevirtual 986	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:log	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   2178: goto +10 -> 2188
/*      */     //   2181: iload 24
/*      */     //   2183: istore 22
/*      */     //   2185: goto -318 -> 1867
/*      */     //   2188: aload 19
/*      */     //   2190: ifnonnull +16 -> 2206
/*      */     //   2193: new 592	java/lang/String
/*      */     //   2196: dup
/*      */     //   2197: aload 18
/*      */     //   2199: aload 14
/*      */     //   2201: invokespecial 1038	java/lang/String:<init>	([BLjava/lang/String;)V
/*      */     //   2204: astore 19
/*      */     //   2206: aload_0
/*      */     //   2207: ldc_w 556
/*      */     //   2210: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   2213: aload_0
/*      */     //   2214: aload 19
/*      */     //   2216: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   2219: getstatic 939	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:baseTagPattern	Ljava/util/regex/Pattern;
/*      */     //   2222: aload 19
/*      */     //   2224: invokevirtual 1065	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
/*      */     //   2227: astore 22
/*      */     //   2229: aload 22
/*      */     //   2231: invokevirtual 1063	java/util/regex/Matcher:find	()Z
/*      */     //   2234: ifeq +40 -> 2274
/*      */     //   2237: aload_0
/*      */     //   2238: aload 22
/*      */     //   2240: iconst_1
/*      */     //   2241: invokevirtual 1064	java/util/regex/Matcher:group	(I)Ljava/lang/String;
/*      */     //   2244: putfield 928	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:basePage	Ljava/lang/String;
/*      */     //   2247: aload_0
/*      */     //   2248: new 593	java/lang/StringBuilder
/*      */     //   2251: dup
/*      */     //   2252: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   2255: ldc_w 549
/*      */     //   2258: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2261: aload_0
/*      */     //   2262: getfield 928	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:basePage	Ljava/lang/String;
/*      */     //   2265: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2268: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2271: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   2274: goto +5 -> 2279
/*      */     //   2277: astore 22
/*      */     //   2279: aload 10
/*      */     //   2281: astore 22
/*      */     //   2283: aload 15
/*      */     //   2285: ifnull +27 -> 2312
/*      */     //   2288: aload 15
/*      */     //   2290: ldc_w 547
/*      */     //   2293: invokeinterface 1099 2 0
/*      */     //   2298: checkcast 598	java/net/URL
/*      */     //   2301: astore 23
/*      */     //   2303: aload 23
/*      */     //   2305: ifnull +7 -> 2312
/*      */     //   2308: aload 23
/*      */     //   2310: astore 22
/*      */     //   2312: new 573	com/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails
/*      */     //   2315: dup
/*      */     //   2316: aload 10
/*      */     //   2318: aload 22
/*      */     //   2320: aload 19
/*      */     //   2322: invokespecial 989	com/aelitis/azureus/core/metasearch/impl/web/WebEngine$pageDetails:<init>	(Ljava/net/URL;Ljava/net/URL;Ljava/lang/String;)V
/*      */     //   2325: astore 23
/*      */     //   2327: aload 13
/*      */     //   2329: ifnull +8 -> 2337
/*      */     //   2332: aload 13
/*      */     //   2334: invokevirtual 1015	java/io/InputStream:close	()V
/*      */     //   2337: aconst_null
/*      */     //   2338: invokestatic 1073	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   2341: aload 23
/*      */     //   2343: areturn
/*      */     //   2344: astore 31
/*      */     //   2346: aload 13
/*      */     //   2348: ifnull +8 -> 2356
/*      */     //   2351: aload 13
/*      */     //   2353: invokevirtual 1015	java/io/InputStream:close	()V
/*      */     //   2356: aload 31
/*      */     //   2358: athrow
/*      */     //   2359: astore 8
/*      */     //   2361: aload 8
/*      */     //   2363: athrow
/*      */     //   2364: astore 8
/*      */     //   2366: aload_0
/*      */     //   2367: new 593	java/lang/StringBuilder
/*      */     //   2370: dup
/*      */     //   2371: invokespecial 1043	java/lang/StringBuilder:<init>	()V
/*      */     //   2374: ldc_w 544
/*      */     //   2377: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2380: aload 8
/*      */     //   2382: invokestatic 1072	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessageAndStack	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   2385: invokevirtual 1048	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2388: invokevirtual 1044	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2391: invokevirtual 975	com/aelitis/azureus/core/metasearch/impl/web/WebEngine:debugLog	(Ljava/lang/String;)V
/*      */     //   2394: new 564	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   2397: dup
/*      */     //   2398: ldc_w 543
/*      */     //   2401: aload 8
/*      */     //   2403: invokespecial 946	com/aelitis/azureus/core/metasearch/SearchException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   2406: athrow
/*      */     //   2407: astore 32
/*      */     //   2409: aconst_null
/*      */     //   2410: invokestatic 1073	org/gudy/azureus2/core3/util/TorrentUtils:setTLSDescription	(Ljava/lang/String;)V
/*      */     //   2413: aload 32
/*      */     //   2415: athrow
/*      */     // Line number table:
/*      */     //   Java source line #754	-> byte code offset #0
/*      */     //   Java source line #756	-> byte code offset #25
/*      */     //   Java source line #758	-> byte code offset #32
/*      */     //   Java source line #761	-> byte code offset #42
/*      */     //   Java source line #763	-> byte code offset #53
/*      */     //   Java source line #765	-> byte code offset #58
/*      */     //   Java source line #766	-> byte code offset #66
/*      */     //   Java source line #768	-> byte code offset #74
/*      */     //   Java source line #770	-> byte code offset #85
/*      */     //   Java source line #772	-> byte code offset #92
/*      */     //   Java source line #773	-> byte code offset #120
/*      */     //   Java source line #768	-> byte code offset #135
/*      */     //   Java source line #776	-> byte code offset #141
/*      */     //   Java source line #778	-> byte code offset #150
/*      */     //   Java source line #780	-> byte code offset #164
/*      */     //   Java source line #782	-> byte code offset #174
/*      */     //   Java source line #784	-> byte code offset #186
/*      */     //   Java source line #786	-> byte code offset #198
/*      */     //   Java source line #788	-> byte code offset #207
/*      */     //   Java source line #790	-> byte code offset #217
/*      */     //   Java source line #794	-> byte code offset #240
/*      */     //   Java source line #797	-> byte code offset #260
/*      */     //   Java source line #799	-> byte code offset #272
/*      */     //   Java source line #801	-> byte code offset #307
/*      */     //   Java source line #811	-> byte code offset #310
/*      */     //   Java source line #817	-> byte code offset #315
/*      */     //   Java source line #819	-> byte code offset #323
/*      */     //   Java source line #821	-> byte code offset #328
/*      */     //   Java source line #823	-> byte code offset #339
/*      */     //   Java source line #825	-> byte code offset #349
/*      */     //   Java source line #827	-> byte code offset #382
/*      */     //   Java source line #829	-> byte code offset #392
/*      */     //   Java source line #831	-> byte code offset #401
/*      */     //   Java source line #833	-> byte code offset #411
/*      */     //   Java source line #835	-> byte code offset #421
/*      */     //   Java source line #838	-> byte code offset #431
/*      */     //   Java source line #842	-> byte code offset #442
/*      */     //   Java source line #844	-> byte code offset #446
/*      */     //   Java source line #848	-> byte code offset #462
/*      */     //   Java source line #851	-> byte code offset #476
/*      */     //   Java source line #853	-> byte code offset #487
/*      */     //   Java source line #855	-> byte code offset #490
/*      */     //   Java source line #857	-> byte code offset #513
/*      */     //   Java source line #859	-> byte code offset #523
/*      */     //   Java source line #861	-> byte code offset #527
/*      */     //   Java source line #865	-> byte code offset #541
/*      */     //   Java source line #869	-> byte code offset #553
/*      */     //   Java source line #871	-> byte code offset #557
/*      */     //   Java source line #874	-> byte code offset #567
/*      */     //   Java source line #876	-> byte code offset #575
/*      */     //   Java source line #878	-> byte code offset #589
/*      */     //   Java source line #879	-> byte code offset #605
/*      */     //   Java source line #880	-> byte code offset #622
/*      */     //   Java source line #884	-> byte code offset #635
/*      */     //   Java source line #886	-> byte code offset #640
/*      */     //   Java source line #887	-> byte code offset #648
/*      */     //   Java source line #889	-> byte code offset #656
/*      */     //   Java source line #891	-> byte code offset #661
/*      */     //   Java source line #894	-> byte code offset #672
/*      */     //   Java source line #896	-> byte code offset #677
/*      */     //   Java source line #900	-> byte code offset #688
/*      */     //   Java source line #903	-> byte code offset #691
/*      */     //   Java source line #905	-> byte code offset #695
/*      */     //   Java source line #907	-> byte code offset #698
/*      */     //   Java source line #911	-> byte code offset #711
/*      */     //   Java source line #913	-> byte code offset #718
/*      */     //   Java source line #915	-> byte code offset #726
/*      */     //   Java source line #918	-> byte code offset #737
/*      */     //   Java source line #920	-> byte code offset #746
/*      */     //   Java source line #922	-> byte code offset #752
/*      */     //   Java source line #925	-> byte code offset #762
/*      */     //   Java source line #927	-> byte code offset #790
/*      */     //   Java source line #929	-> byte code offset #793
/*      */     //   Java source line #931	-> byte code offset #797
/*      */     //   Java source line #933	-> byte code offset #812
/*      */     //   Java source line #936	-> byte code offset #827
/*      */     //   Java source line #940	-> byte code offset #838
/*      */     //   Java source line #956	-> byte code offset #847
/*      */     //   Java source line #942	-> byte code offset #850
/*      */     //   Java source line #944	-> byte code offset #852
/*      */     //   Java source line #946	-> byte code offset #866
/*      */     //   Java source line #950	-> byte code offset #883
/*      */     //   Java source line #1298	-> byte code offset #898
/*      */     //   Java source line #1300	-> byte code offset #903
/*      */     //   Java source line #1318	-> byte code offset #908
/*      */     //   Java source line #954	-> byte code offset #915
/*      */     //   Java source line #959	-> byte code offset #918
/*      */     //   Java source line #961	-> byte code offset #925
/*      */     //   Java source line #963	-> byte code offset #939
/*      */     //   Java source line #965	-> byte code offset #948
/*      */     //   Java source line #967	-> byte code offset #953
/*      */     //   Java source line #969	-> byte code offset #968
/*      */     //   Java source line #971	-> byte code offset #987
/*      */     //   Java source line #973	-> byte code offset #998
/*      */     //   Java source line #975	-> byte code offset #1008
/*      */     //   Java source line #977	-> byte code offset #1019
/*      */     //   Java source line #971	-> byte code offset #1029
/*      */     //   Java source line #967	-> byte code offset #1035
/*      */     //   Java source line #995	-> byte code offset #1041
/*      */     //   Java source line #997	-> byte code offset #1046
/*      */     //   Java source line #998	-> byte code offset #1061
/*      */     //   Java source line #1000	-> byte code offset #1076
/*      */     //   Java source line #1002	-> byte code offset #1081
/*      */     //   Java source line #1005	-> byte code offset #1089
/*      */     //   Java source line #1007	-> byte code offset #1094
/*      */     //   Java source line #1011	-> byte code offset #1102
/*      */     //   Java source line #1013	-> byte code offset #1116
/*      */     //   Java source line #1015	-> byte code offset #1131
/*      */     //   Java source line #1017	-> byte code offset #1144
/*      */     //   Java source line #1019	-> byte code offset #1157
/*      */     //   Java source line #1021	-> byte code offset #1163
/*      */     //   Java source line #1023	-> byte code offset #1174
/*      */     //   Java source line #1025	-> byte code offset #1183
/*      */     //   Java source line #1027	-> byte code offset #1189
/*      */     //   Java source line #1029	-> byte code offset #1203
/*      */     //   Java source line #1031	-> byte code offset #1212
/*      */     //   Java source line #1033	-> byte code offset #1218
/*      */     //   Java source line #1036	-> byte code offset #1231
/*      */     //   Java source line #1038	-> byte code offset #1242
/*      */     //   Java source line #1041	-> byte code offset #1253
/*      */     //   Java source line #1043	-> byte code offset #1264
/*      */     //   Java source line #1047	-> byte code offset #1282
/*      */     //   Java source line #1049	-> byte code offset #1290
/*      */     //   Java source line #1051	-> byte code offset #1315
/*      */     //   Java source line #1070	-> byte code offset #1319
/*      */     //   Java source line #1053	-> byte code offset #1322
/*      */     //   Java source line #1058	-> byte code offset #1324
/*      */     //   Java source line #1060	-> byte code offset #1331
/*      */     //   Java source line #1062	-> byte code offset #1339
/*      */     //   Java source line #1064	-> byte code offset #1364
/*      */     //   Java source line #1069	-> byte code offset #1368
/*      */     //   Java source line #1066	-> byte code offset #1371
/*      */     //   Java source line #1068	-> byte code offset #1373
/*      */     //   Java source line #1076	-> byte code offset #1406
/*      */     //   Java source line #1078	-> byte code offset #1418
/*      */     //   Java source line #1082	-> byte code offset #1425
/*      */     //   Java source line #1084	-> byte code offset #1434
/*      */     //   Java source line #1086	-> byte code offset #1439
/*      */     //   Java source line #1089	-> byte code offset #1442
/*      */     //   Java source line #1090	-> byte code offset #1452
/*      */     //   Java source line #1092	-> byte code offset #1455
/*      */     //   Java source line #1094	-> byte code offset #1462
/*      */     //   Java source line #1097	-> byte code offset #1467
/*      */     //   Java source line #1099	-> byte code offset #1472
/*      */     //   Java source line #1101	-> byte code offset #1481
/*      */     //   Java source line #1106	-> byte code offset #1496
/*      */     //   Java source line #1103	-> byte code offset #1499
/*      */     //   Java source line #1105	-> byte code offset #1501
/*      */     //   Java source line #1108	-> byte code offset #1506
/*      */     //   Java source line #1298	-> byte code offset #1520
/*      */     //   Java source line #1300	-> byte code offset #1525
/*      */     //   Java source line #1318	-> byte code offset #1530
/*      */     //   Java source line #1111	-> byte code offset #1537
/*      */     //   Java source line #1113	-> byte code offset #1540
/*      */     //   Java source line #1115	-> byte code offset #1563
/*      */     //   Java source line #1122	-> byte code offset #1570
/*      */     //   Java source line #1124	-> byte code offset #1580
/*      */     //   Java source line #1126	-> byte code offset #1586
/*      */     //   Java source line #1128	-> byte code offset #1596
/*      */     //   Java source line #1130	-> byte code offset #1602
/*      */     //   Java source line #1132	-> byte code offset #1614
/*      */     //   Java source line #1134	-> byte code offset #1620
/*      */     //   Java source line #1137	-> byte code offset #1632
/*      */     //   Java source line #1139	-> byte code offset #1646
/*      */     //   Java source line #1141	-> byte code offset #1649
/*      */     //   Java source line #1143	-> byte code offset #1661
/*      */     //   Java source line #1145	-> byte code offset #1675
/*      */     //   Java source line #1148	-> byte code offset #1689
/*      */     //   Java source line #1150	-> byte code offset #1697
/*      */     //   Java source line #1152	-> byte code offset #1722
/*      */     //   Java source line #1156	-> byte code offset #1726
/*      */     //   Java source line #1158	-> byte code offset #1730
/*      */     //   Java source line #1160	-> byte code offset #1734
/*      */     //   Java source line #1162	-> byte code offset #1752
/*      */     //   Java source line #1165	-> byte code offset #1758
/*      */     //   Java source line #1170	-> byte code offset #1821
/*      */     //   Java source line #1167	-> byte code offset #1824
/*      */     //   Java source line #1169	-> byte code offset #1826
/*      */     //   Java source line #1177	-> byte code offset #1859
/*      */     //   Java source line #1183	-> byte code offset #1864
/*      */     //   Java source line #1187	-> byte code offset #1867
/*      */     //   Java source line #1189	-> byte code offset #1879
/*      */     //   Java source line #1191	-> byte code offset #1885
/*      */     //   Java source line #1193	-> byte code offset #1897
/*      */     //   Java source line #1195	-> byte code offset #1903
/*      */     //   Java source line #1197	-> byte code offset #1915
/*      */     //   Java source line #1199	-> byte code offset #1928
/*      */     //   Java source line #1201	-> byte code offset #1939
/*      */     //   Java source line #1203	-> byte code offset #1945
/*      */     //   Java source line #1205	-> byte code offset #1948
/*      */     //   Java source line #1207	-> byte code offset #1960
/*      */     //   Java source line #1209	-> byte code offset #1966
/*      */     //   Java source line #1211	-> byte code offset #1977
/*      */     //   Java source line #1213	-> byte code offset #1990
/*      */     //   Java source line #1216	-> byte code offset #1994
/*      */     //   Java source line #1219	-> byte code offset #2008
/*      */     //   Java source line #1221	-> byte code offset #2016
/*      */     //   Java source line #1223	-> byte code offset #2041
/*      */     //   Java source line #1227	-> byte code offset #2045
/*      */     //   Java source line #1229	-> byte code offset #2049
/*      */     //   Java source line #1231	-> byte code offset #2053
/*      */     //   Java source line #1233	-> byte code offset #2071
/*      */     //   Java source line #1236	-> byte code offset #2077
/*      */     //   Java source line #1241	-> byte code offset #2140
/*      */     //   Java source line #1238	-> byte code offset #2143
/*      */     //   Java source line #1240	-> byte code offset #2145
/*      */     //   Java source line #1243	-> byte code offset #2178
/*      */     //   Java source line #1248	-> byte code offset #2181
/*      */     //   Java source line #1258	-> byte code offset #2185
/*      */     //   Java source line #1261	-> byte code offset #2188
/*      */     //   Java source line #1263	-> byte code offset #2193
/*      */     //   Java source line #1266	-> byte code offset #2206
/*      */     //   Java source line #1267	-> byte code offset #2213
/*      */     //   Java source line #1272	-> byte code offset #2219
/*      */     //   Java source line #1273	-> byte code offset #2229
/*      */     //   Java source line #1274	-> byte code offset #2237
/*      */     //   Java source line #1276	-> byte code offset #2247
/*      */     //   Java source line #1280	-> byte code offset #2274
/*      */     //   Java source line #1278	-> byte code offset #2277
/*      */     //   Java source line #1282	-> byte code offset #2279
/*      */     //   Java source line #1284	-> byte code offset #2283
/*      */     //   Java source line #1286	-> byte code offset #2288
/*      */     //   Java source line #1288	-> byte code offset #2303
/*      */     //   Java source line #1290	-> byte code offset #2308
/*      */     //   Java source line #1294	-> byte code offset #2312
/*      */     //   Java source line #1298	-> byte code offset #2327
/*      */     //   Java source line #1300	-> byte code offset #2332
/*      */     //   Java source line #1318	-> byte code offset #2337
/*      */     //   Java source line #1298	-> byte code offset #2344
/*      */     //   Java source line #1300	-> byte code offset #2351
/*      */     //   Java source line #1304	-> byte code offset #2359
/*      */     //   Java source line #1306	-> byte code offset #2361
/*      */     //   Java source line #1308	-> byte code offset #2364
/*      */     //   Java source line #1312	-> byte code offset #2366
/*      */     //   Java source line #1314	-> byte code offset #2394
/*      */     //   Java source line #1318	-> byte code offset #2407
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	2416	0	this	WebEngine
/*      */     //   0	2416	1	proxy	java.net.Proxy
/*      */     //   0	2416	2	proxy_host	String
/*      */     //   0	2416	3	searchURL	String
/*      */     //   0	2416	4	searchParameters	SearchParameter[]
/*      */     //   0	2416	5	searchContext	Map<String, String>
/*      */     //   0	2416	6	headers	String
/*      */     //   0	2416	7	only_if_modified	boolean
/*      */     //   51	1412	8	vuze_file	boolean
/*      */     //   2359	3	8	e	SearchException
/*      */     //   2364	38	8	e	Throwable
/*      */     //   64	79	9	from_strs	String[]
/*      */     //   313	515	9	rdf	org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory
/*      */     //   72	73	10	to_strs	String[]
/*      */     //   390	75	10	initial_url	URL
/*      */     //   521	1796	10	initial_url	URL
/*      */     //   75	61	11	i	int
/*      */     //   162	13	11	it	java.util.Iterator<java.util.Map.Entry<String, String>>
/*      */     //   457	3	11	initial_url_rd	ResourceDownloader
/*      */     //   474	3	11	initial_url_rd	ResourceDownloader
/*      */     //   536	3	11	initial_url_rd	ResourceDownloader
/*      */     //   551	279	11	initial_url_rd	ResourceDownloader
/*      */     //   90	35	12	parameter	SearchParameter
/*      */     //   184	77	12	entry	java.util.Map.Entry<String, String>
/*      */     //   321	21	12	post_pos	int
/*      */     //   196	88	13	key	String
/*      */     //   337	130	13	post_params	String
/*      */     //   646	20	13	last_modified	String
/*      */     //   689	1663	13	is	java.io.InputStream
/*      */     //   270	24	14	value	String
/*      */     //   399	35	14	sep	int
/*      */     //   654	28	14	etag	String
/*      */     //   693	1507	14	content_charset	String
/*      */     //   409	3	15	type	String
/*      */     //   696	1593	15	mr_rd	ResourceDownloader
/*      */     //   716	59	16	str	String
/*      */     //   850	66	16	e	org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException
/*      */     //   937	32	16	cookies_list	List
/*      */     //   1059	26	16	last_modified	String
/*      */     //   1114	18	16	cts	List
/*      */     //   1416	40	16	baos	java.io.ByteArrayOutputStream
/*      */     //   744	12	17	pos	int
/*      */     //   864	8	17	response	Long
/*      */     //   946	74	17	cookies_set	List
/*      */     //   1074	24	17	etag	String
/*      */     //   1142	246	17	content_type	String
/*      */     //   1423	22	17	buffer	byte[]
/*      */     //   896	17	18	localpageDetails	pageDetails
/*      */     //   954	82	18	i	int
/*      */     //   1155	67	18	pos	int
/*      */     //   1432	16	18	len	int
/*      */     //   1460	738	18	data	byte[]
/*      */     //   985	14	19	cookies	String[]
/*      */     //   1322	3	19	e	Throwable
/*      */     //   1470	12	19	vfh	com.aelitis.azureus.core.vuzefile.VuzeFileHandler
/*      */     //   1499	36	19	e	Throwable
/*      */     //   1538	783	19	page	String
/*      */     //   988	42	20	j	int
/*      */     //   1371	31	20	f	Throwable
/*      */     //   1479	11	20	vf	com.aelitis.azureus.core.vuzefile.VuzeFile
/*      */     //   1561	541	20	content	String
/*      */     //   1006	16	21	cookie	String
/*      */     //   1568	399	21	lc_content	String
/*      */     //   1578	57	22	pos1	int
/*      */     //   1865	319	22	pos	int
/*      */     //   2227	12	22	m	Matcher
/*      */     //   2277	3	22	e	Exception
/*      */     //   2281	38	22	final_url	URL
/*      */     //   1594	193	23	pos2	int
/*      */     //   1877	32	23	pos1	int
/*      */     //   2301	41	23	x	URL
/*      */     //   1612	157	24	pos3	int
/*      */     //   1895	287	24	pos2	int
/*      */     //   1659	126	25	pos4	int
/*      */     //   1913	175	25	pos3	int
/*      */     //   1687	154	26	encoding	String
/*      */     //   1958	146	26	pos4	int
/*      */     //   1728	78	27	data_start	int
/*      */     //   1824	31	27	e	Throwable
/*      */     //   1975	16	27	pos5	int
/*      */     //   1732	13	28	max_skip	int
/*      */     //   2006	154	28	encoding	String
/*      */     //   2047	78	29	data_start	int
/*      */     //   2143	31	29	e	Throwable
/*      */     //   2051	13	30	max_skip	int
/*      */     //   2344	13	31	localObject1	Object
/*      */     //   2407	7	32	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   838	847	850	org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderException
/*      */     //   1282	1319	1322	java/lang/Throwable
/*      */     //   1324	1368	1371	java/lang/Throwable
/*      */     //   1467	1496	1499	java/lang/Throwable
/*      */     //   1689	1821	1824	java/lang/Throwable
/*      */     //   2008	2140	2143	java/lang/Throwable
/*      */     //   2219	2274	2277	java/lang/Exception
/*      */     //   691	898	2344	finally
/*      */     //   915	1520	2344	finally
/*      */     //   1537	2327	2344	finally
/*      */     //   2344	2346	2344	finally
/*      */     //   0	908	2359	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   915	1530	2359	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   1537	2337	2359	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   2344	2359	2359	com/aelitis/azureus/core/metasearch/SearchException
/*      */     //   0	908	2364	java/lang/Throwable
/*      */     //   915	1530	2364	java/lang/Throwable
/*      */     //   1537	2337	2364	java/lang/Throwable
/*      */     //   2344	2359	2364	java/lang/Throwable
/*      */     //   0	908	2407	finally
/*      */     //   915	1530	2407	finally
/*      */     //   1537	2337	2407	finally
/*      */     //   2344	2409	2407	finally
/*      */   }
/*      */   
/*      */   protected String extractProperty(Object o)
/*      */   {
/* 1326 */     if ((o instanceof String))
/*      */     {
/* 1328 */       return (String)o;
/*      */     }
/* 1330 */     if ((o instanceof List))
/*      */     {
/* 1332 */       List l = (List)o;
/*      */       
/* 1334 */       if (l.size() > 0)
/*      */       {
/* 1336 */         if (l.size() > 1)
/*      */         {
/* 1338 */           Debug.out("Property has multiple values!");
/*      */         }
/*      */         
/* 1341 */         Object x = l.get(0);
/*      */         
/* 1343 */         if ((x instanceof String))
/*      */         {
/* 1345 */           return (String)x;
/*      */         }
/*      */         
/*      */ 
/* 1349 */         Debug.out("Property value isn't a String:" + x);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1354 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setHeaders(ResourceDownloader rd, String encoded_headers)
/*      */   {
/* 1362 */     UrlUtils.setBrowserHeaders(rd, encoded_headers, this.rootPage);
/*      */   }
/*      */   
/*      */   public String getIcon() {
/* 1366 */     if (this.iconUrl != null) {
/* 1367 */       return this.iconUrl;
/*      */     }
/* 1369 */     if (this.rootPage != null) {
/* 1370 */       return this.rootPage + "/favicon.ico";
/*      */     }
/* 1372 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected FieldMapping[] getMappings()
/*      */   {
/* 1378 */     return this.mappings;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsField(int field_id)
/*      */   {
/* 1385 */     for (int i = 0; i < this.mappings.length; i++)
/*      */     {
/* 1387 */       if (this.mappings[i].getField() == field_id)
/*      */       {
/* 1389 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1393 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getRootPage()
/*      */   {
/* 1399 */     return this.rootPage;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getBasePage()
/*      */   {
/* 1405 */     return this.basePage;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DateParser getDateParser()
/*      */   {
/* 1411 */     return this.dateParser;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDownloadLinkCSS()
/*      */   {
/* 1417 */     if (this.downloadLinkCSS == null)
/*      */     {
/* 1419 */       return "";
/*      */     }
/*      */     
/* 1422 */     return this.downloadLinkCSS;
/*      */   }
/*      */   
/*      */   public boolean requiresLogin() {
/* 1426 */     return (this.needsAuth) && (!CookieParser.cookiesContain(this.requiredCookies, this.local_cookies));
/*      */   }
/*      */   
/*      */   public void setCookies(String cookies) {
/* 1430 */     this.local_cookies = cookies;
/*      */     
/* 1432 */     setLocalString("cookies", cookies);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getSearchUrl(boolean raw)
/*      */   {
/* 1439 */     if (raw)
/*      */     {
/* 1441 */       return this.searchURLFormat;
/*      */     }
/*      */     
/* 1444 */     return getSearchUrl();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getSearchUrl()
/*      */   {
/* 1451 */     return this.searchURLFormat.replaceAll("%s", "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSearchUrl(String str)
/*      */   {
/* 1458 */     this.searchURLFormat = str;
/*      */     
/* 1460 */     init();
/*      */     
/* 1462 */     getMetaSearch().configDirty();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getLoginPageUrl()
/*      */   {
/* 1468 */     return this.searchURLFormat.replaceAll("%s", "");
/*      */   }
/*      */   
/*      */   public void setLoginPageUrl(String loginPageUrl) {
/* 1472 */     this.loginPageUrl = loginPageUrl;
/*      */   }
/*      */   
/*      */   public String[] getRequiredCookies() {
/* 1476 */     return this.requiredCookies;
/*      */   }
/*      */   
/*      */   public void setRequiredCookies(String[] requiredCookies) {
/* 1480 */     this.requiredCookies = requiredCookies;
/*      */   }
/*      */   
/*      */   public boolean isNeedsAuth() {
/* 1484 */     return this.needsAuth;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAuthenticated()
/*      */   {
/* 1490 */     return isNeedsAuth();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setNeedsAuth(boolean b)
/*      */   {
/* 1497 */     this.needsAuth = b;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getAuthMethod()
/*      */   {
/* 1503 */     return this.authMethod;
/*      */   }
/*      */   
/*      */   public String getCookies() {
/* 1507 */     return this.local_cookies;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 1513 */     return getString(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getString(boolean full)
/*      */   {
/* 1520 */     return super.getString() + (full ? ", url=" + this.searchURLFormat : "") + ", auth=" + isNeedsAuth() + (isNeedsAuth() ? " [cookies=" + this.local_cookies + "]" : "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class pageDetails
/*      */   {
/*      */     private URL initial_url;
/*      */     
/*      */ 
/*      */     private URL final_url;
/*      */     
/*      */ 
/*      */     private String content;
/*      */     
/*      */ 
/*      */     private Object verified_state;
/*      */     
/*      */ 
/*      */     protected pageDetails(URL _initial_url, URL _final_url, String _content)
/*      */     {
/* 1541 */       this.initial_url = _initial_url;
/* 1542 */       this.final_url = _final_url;
/* 1543 */       this.content = _content;
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getInitialURL()
/*      */     {
/* 1549 */       return this.initial_url;
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getFinalURL()
/*      */     {
/* 1555 */       return this.final_url;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getContent()
/*      */     {
/* 1561 */       return this.content;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setContent(String _content)
/*      */     {
/* 1568 */       this.content = _content;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setVerifiedState(Object state)
/*      */     {
/* 1575 */       this.verified_state = state;
/*      */     }
/*      */     
/*      */ 
/*      */     public Object getVerifiedState()
/*      */     {
/* 1581 */       return this.verified_state;
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface pageDetailsVerifier
/*      */   {
/*      */     public abstract void verify(WebEngine.pageDetails parampageDetails)
/*      */       throws SearchException;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/web/WebEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */