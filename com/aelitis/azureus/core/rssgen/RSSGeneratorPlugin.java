/*     */ package com.aelitis.azureus.core.rssgen;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.TreeMap;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageGenerator;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.HyperlinkParameter;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.ui.webplugin.WebPlugin;
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
/*     */ public class RSSGeneratorPlugin
/*     */   extends WebPlugin
/*     */ {
/*     */   public static final String PLUGIN_NAME = "Local RSS etc.";
/*     */   public static final int DEFAULT_PORT = 6905;
/*     */   public static final String DEFAULT_ACCESS = "all";
/*     */   private static volatile RSSGeneratorPlugin singleton;
/*     */   private static boolean loaded;
/*  58 */   private static final Properties defaults = new Properties();
/*     */   
/*     */ 
/*     */ 
/*     */   public static void load(PluginInterface plugin_interface)
/*     */   {
/*  64 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  65 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Local RSS etc.");
/*     */     
/*  67 */     synchronized (RSSGeneratorPlugin.class)
/*     */     {
/*  69 */       if (loaded)
/*     */       {
/*  71 */         return;
/*     */       }
/*     */       
/*  74 */       loaded = true;
/*     */     }
/*     */     
/*  77 */     File root_dir = new File(SystemProperties.getUserPath() + "rss");
/*     */     
/*  79 */     if (!root_dir.exists())
/*     */     {
/*  81 */       root_dir.mkdir();
/*     */     }
/*     */     
/*     */     String rss_access;
/*     */     Integer rss_port;
/*     */     String rss_access;
/*  87 */     if (COConfigurationManager.getBooleanParameter("rss.internal.migrated", false))
/*     */     {
/*  89 */       Integer rss_port = Integer.valueOf(COConfigurationManager.getIntParameter("rss.internal.config.port", 6905));
/*  90 */       rss_access = COConfigurationManager.getStringParameter("rss.internal.config.access", "all");
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*  96 */       int port = COConfigurationManager.getIntParameter("Plugin.default.device.rss.port", 6905);
/*     */       
/*  98 */       rss_port = Integer.valueOf(port);
/*     */       
/* 100 */       if (port != 6905)
/*     */       {
/* 102 */         COConfigurationManager.setParameter("rss.internal.config.port", port);
/*     */       }
/*     */       
/* 105 */       boolean local = COConfigurationManager.getBooleanParameter("Plugin.default.device.rss.localonly", true);
/*     */       
/* 107 */       rss_access = local ? "local" : "all";
/*     */       
/* 109 */       if (!rss_access.equals("all"))
/*     */       {
/* 111 */         COConfigurationManager.setParameter("rss.internal.config.access", rss_access);
/*     */       }
/*     */       
/* 114 */       COConfigurationManager.setParameter("rss.internal.migrated", true);
/*     */     }
/*     */     
/* 117 */     defaults.put("Enable", Boolean.valueOf(COConfigurationManager.getBooleanParameter("Plugin.default.device.rss.enable", false)));
/*     */     
/* 119 */     defaults.put("Disablable", Boolean.TRUE);
/* 120 */     defaults.put("Port", rss_port);
/* 121 */     defaults.put("Access", rss_access);
/* 122 */     defaults.put("Root Dir", root_dir.getAbsolutePath());
/* 123 */     defaults.put("DefaultEnableKeepAlive", Boolean.TRUE);
/* 124 */     defaults.put("DefaultHideResourceConfig", Boolean.TRUE);
/* 125 */     defaults.put("PairingSID", "rss");
/*     */     
/* 127 */     defaults.put("DefaultConfigModelParams", new String[] { "root", "rss" });
/*     */   }
/*     */   
/*     */ 
/*     */   public static RSSGeneratorPlugin getSingleton()
/*     */   {
/* 133 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/* 137 */   private static final Map<String, Provider> providers = new TreeMap();
/*     */   
/*     */   private HyperlinkParameter test_param;
/*     */   
/*     */   private BooleanParameter enable_low_noise;
/*     */   
/*     */   public RSSGeneratorPlugin()
/*     */   {
/* 145 */     super(defaults);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLowNoiseEnabled()
/*     */   {
/* 151 */     return this.enable_low_noise.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getURL()
/*     */   {
/* 157 */     InetAddress bind_ip = getServerBindIP();
/*     */     
/*     */     String ip;
/*     */     String ip;
/* 161 */     if (bind_ip.isAnyLocalAddress())
/*     */     {
/* 163 */       ip = "127.0.0.1";
/*     */     }
/*     */     else
/*     */     {
/* 167 */       ip = bind_ip.getHostAddress();
/*     */     }
/*     */     
/* 170 */     return getProtocol() + "://" + ip + ":" + getPort() + "/";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setupServer()
/*     */   {
/* 177 */     super.setupServer();
/*     */     
/* 179 */     if (this.test_param != null)
/*     */     {
/* 181 */       this.test_param.setEnabled(isPluginEnabled());
/*     */       
/* 183 */       this.test_param.setHyperlink(getURL());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerProvider(String name, Provider provider)
/*     */   {
/* 192 */     synchronized (providers)
/*     */     {
/* 194 */       providers.put(name, provider);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void unregisterProvider(String name)
/*     */   {
/* 202 */     synchronized (providers)
/*     */     {
/* 204 */       providers.remove(name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface pi)
/*     */     throws PluginException
/*     */   {
/* 214 */     singleton = this;
/*     */     
/* 216 */     pi.getPluginProperties().setProperty("plugin.name", "Local RSS etc.");
/*     */     
/* 218 */     super.initialize(pi);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void initStage(int num)
/*     */   {
/* 226 */     if (num == 1)
/*     */     {
/* 228 */       BasicPluginConfigModel config = getConfigModel();
/*     */       
/* 230 */       this.test_param = config.addHyperlinkParameter2("rss.internal.test.url", "");
/*     */       
/* 232 */       this.enable_low_noise = config.addBooleanParameter2("rss.internal.enable.low.noise", "rss.internal.enable.low.noise", true);
/*     */       
/* 234 */       this.test_param.setEnabled(isPluginEnabled());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean generateSupport(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*     */     throws IOException
/*     */   {
/* 245 */     String url = request.getURL();
/*     */     
/* 247 */     if (url.startsWith("/"))
/*     */     {
/* 249 */       url = url.substring(1);
/*     */     }
/*     */     
/* 252 */     if (url.equals("favicon.ico")) {
/*     */       try
/*     */       {
/* 255 */         InputStream stream = getClass().getClassLoader().getResourceAsStream("org/gudy/azureus2/ui/icons/favicon.ico");
/*     */         
/* 257 */         response.useStream("image/x-icon", stream);
/*     */         
/* 259 */         return true;
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 265 */     if ((url.length() == 0) || (url.charAt(0) == '?'))
/*     */     {
/* 267 */       response.setContentType("text/html; charset=UTF-8");
/*     */       
/* 269 */       PrintWriter pw = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
/*     */       
/* 271 */       pw.println("<HTML><HEAD><TITLE>Vuze Feeds etc.</TITLE></HEAD><BODY>");
/*     */       
/* 273 */       synchronized (providers)
/*     */       {
/* 275 */         for (Map.Entry<String, Provider> entry : providers.entrySet())
/*     */         {
/* 277 */           Provider provider = (Provider)entry.getValue();
/*     */           
/* 279 */           if (provider.isEnabled())
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 284 */             String name = (String)entry.getKey();
/*     */             
/* 286 */             pw.println("<LI><A href=\"" + URLEncoder.encode(name, "UTF-8") + "\">" + name + "</A></LI>");
/*     */           }
/*     */         }
/*     */       }
/* 290 */       pw.println("</BODY></HTML>");
/*     */       
/* 292 */       pw.flush();
/*     */       
/* 294 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 298 */     int pos = url.indexOf('/');
/*     */     
/* 300 */     if (pos != -1)
/*     */     {
/* 302 */       url = url.substring(0, pos);
/*     */     }
/*     */     
/*     */     Provider provider;
/*     */     
/* 307 */     synchronized (providers)
/*     */     {
/* 309 */       provider = (Provider)providers.get(url);
/*     */     }
/*     */     
/* 312 */     if ((provider != null) && (provider.isEnabled()))
/*     */     {
/* 314 */       if (provider.generate(request, response))
/*     */       {
/* 316 */         return true;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 321 */     response.setReplyStatus(404);
/*     */     
/* 323 */     return true;
/*     */   }
/*     */   
/*     */   public static abstract interface Provider
/*     */     extends TrackerWebPageGenerator
/*     */   {
/*     */     public abstract boolean isEnabled();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/rssgen/RSSGeneratorPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */