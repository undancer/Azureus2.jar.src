/*     */ package com.aelitis.azureus.core.messenger.config;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessage;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessengerListener;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
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
/*     */ public class PlatformConfigMessenger
/*     */ {
/*     */   public static final String LISTENER_ID = "config";
/*     */   private static final String OP_LOG_PLUGIN = "log-plugin";
/*  47 */   private static boolean sendStats = true;
/*     */   
/*  49 */   private static boolean platformLoginComplete = false;
/*     */   
/*  51 */   private static String webSearchUrl = "https://www.google.com/search?q=%s";
/*     */   
/*  53 */   protected static List platformLoginCompleteListeners = Collections.EMPTY_LIST;
/*     */   
/*  55 */   private static CopyOnWriteList<String> externalLinks = new CopyOnWriteList();
/*     */   
/*     */   public static void login(long maxDelayMS) {
/*  58 */     PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*     */     
/*  60 */     Object[] params = { "version", "5.7.6.0", "locale", MessageText.getCurrentLocale().toString(), "vid", COConfigurationManager.getStringParameter("ID") };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  68 */     PlatformMessage message = new PlatformMessage("AZMSG", "config", "login", params, maxDelayMS);
/*     */     
/*     */ 
/*  71 */     PlatformMessengerListener listener = new PlatformMessengerListener()
/*     */     {
/*     */       public void replyReceived(PlatformMessage message, String replyType, Map reply)
/*     */       {
/*  75 */         if (reply == null) {
/*  76 */           return;
/*     */         }
/*     */         
/*  79 */         boolean allowMulti = MapUtils.getMapBoolean(reply, "allow-multi-rpc", PlatformMessenger.getAllowMulti());
/*     */         
/*  81 */         PlatformMessenger.setAllowMulti(allowMulti);
/*     */         try
/*     */         {
/*  84 */           List listURLs = (List)MapUtils.getMapObject(reply, "url-whitelist", null, List.class);
/*     */           
/*  86 */           if (listURLs != null) {
/*  87 */             for (int i = 0; i < listURLs.size(); i++) {
/*  88 */               String string = (String)listURLs.get(i);
/*  89 */               UrlFilter.getInstance().addUrlWhitelist(string);
/*     */             }
/*     */           }
/*     */         } catch (Exception e) {
/*  93 */           Debug.out(e);
/*     */         }
/*     */         try
/*     */         {
/*  97 */           List listURLs = (List)MapUtils.getMapObject(reply, "url-blacklist", null, List.class);
/*     */           
/*  99 */           if (listURLs != null) {
/* 100 */             for (int i = 0; i < listURLs.size(); i++) {
/* 101 */               String string = (String)listURLs.get(i);
/* 102 */               UrlFilter.getInstance().addUrlBlacklist(string);
/*     */             }
/*     */           }
/*     */         } catch (Exception e) {
/* 106 */           Debug.out(e);
/*     */         }
/*     */         
/*     */         try
/*     */         {
/* 111 */           List list = MapUtils.getMapList(reply, "external-links", Collections.EMPTY_LIST);
/* 112 */           PlatformConfigMessenger.externalLinks.addAll(list);
/*     */         } catch (Exception e) {
/* 114 */           Debug.out(e);
/*     */         }
/*     */         try
/*     */         {
/* 118 */           PlatformConfigMessenger.access$102(MapUtils.getMapBoolean(reply, "send-stats", false));
/*     */         }
/*     */         catch (Exception e) {}
/*     */         
/* 122 */         PlatformConfigMessenger.access$202(MapUtils.getMapString(reply, "web-search-url", PlatformConfigMessenger.webSearchUrl));
/*     */         
/*     */ 
/* 125 */         PlatformConfigMessenger.access$302(true);
/* 126 */         Object[] listeners = PlatformConfigMessenger.platformLoginCompleteListeners.toArray();
/* 127 */         PlatformConfigMessenger.platformLoginCompleteListeners = Collections.EMPTY_LIST;
/* 128 */         for (int i = 0; i < listeners.length; i++) {
/*     */           try {
/* 130 */             PlatformConfigMessenger.PlatformLoginCompleteListener l = (PlatformConfigMessenger.PlatformLoginCompleteListener)listeners[i];
/* 131 */             l.platformLoginComplete();
/*     */           } catch (Exception e) {
/* 133 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void messageSent(PlatformMessage message) {}
/* 142 */     };
/* 143 */     PlatformMessenger.pushMessageNow(message, listener);
/*     */   }
/*     */   
/*     */   public static void logPlugin(String event, String pluginID) {
/* 147 */     boolean send_info = COConfigurationManager.getBooleanParameter("Send Version Info");
/* 148 */     if (send_info) {}
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
/*     */   public static void sendUsageStats(Map stats, long timestamp, String version, PlatformMessengerListener l)
/*     */   {
/* 168 */     if (!sendStats) {
/* 169 */       return;
/*     */     }
/*     */     try {
/* 172 */       PlatformMessage message = new PlatformMessage("AZMSG", "config", "send-usage-stats2", new Object[] { "stats", stats, "version", version, "timestamp", new Long(timestamp), "ago-ms", new Long(SystemTime.getCurrentTime() - timestamp) }, 5000L);
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
/* 184 */       PlatformMessenger.queueMessage(message, l);
/*     */     } catch (Exception e) {
/* 186 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void sendVersionServerMap(Map mapVerServer) {
/* 191 */     boolean send_info = COConfigurationManager.getBooleanParameter("Send Version Info");
/* 192 */     if (send_info) {}
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
/*     */   public static boolean allowSendStats()
/*     */   {
/* 215 */     return sendStats;
/*     */   }
/*     */   
/*     */   public static String getWebSearchUrl() {
/* 219 */     return webSearchUrl;
/*     */   }
/*     */   
/*     */   public static void addPlatformLoginCompleteListener(PlatformLoginCompleteListener l)
/*     */   {
/*     */     try
/*     */     {
/* 226 */       if (l == null) {
/* 227 */         return;
/*     */       }
/* 229 */       if (platformLoginComplete) {
/* 230 */         l.platformLoginComplete();
/* 231 */         return;
/*     */       }
/* 233 */       if (platformLoginCompleteListeners == Collections.EMPTY_LIST) {
/* 234 */         platformLoginCompleteListeners = new ArrayList(1);
/*     */       }
/* 236 */       platformLoginCompleteListeners.add(l);
/*     */     } catch (Exception e) {
/* 238 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean areLinksExternal(String url)
/*     */   {
/* 247 */     for (String regex : externalLinks) {
/*     */       try {
/* 249 */         if (Pattern.compile(regex).matcher(url).find()) {
/* 250 */           return true;
/*     */         }
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/* 255 */     return false;
/*     */   }
/*     */   
/*     */   public static void addLinkExternal(String link) {
/* 259 */     if (externalLinks.contains(link)) {
/* 260 */       return;
/*     */     }
/* 262 */     externalLinks.add(link);
/*     */   }
/*     */   
/*     */   public static abstract interface GetBrowseSectionsReplyListener
/*     */   {
/*     */     public abstract void messageSent();
/*     */     
/*     */     public abstract void replyReceived(Map[] paramArrayOfMap);
/*     */   }
/*     */   
/*     */   public static abstract interface PlatformLoginCompleteListener
/*     */   {
/*     */     public abstract void platformLoginComplete();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/config/PlatformConfigMessenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */