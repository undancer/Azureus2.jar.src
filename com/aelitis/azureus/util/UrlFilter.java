/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class UrlFilter
/*     */ {
/*  40 */   private static UrlFilter instance = null;
/*     */   
/*     */ 
/*     */ 
/*  44 */   private String default_site_host = (String)ConstantsVuze.getDefaultContentNetwork().getProperty(1);
/*     */   
/*  46 */   private String DEFAULT_RPC_WHITELIST = "https?://" + this.default_site_host.replaceAll("\\.", "\\\\.") + ":?[0-9]*/" + ".*";
/*     */   
/*     */ 
/*  49 */   private CopyOnWriteList<String> listUrlBlacklist = new CopyOnWriteList();
/*     */   
/*  51 */   private CopyOnWriteList<String> listUrlWhitelist = new CopyOnWriteList();
/*     */   
/*  53 */   private AEMonitor mon = new AEMonitor("UrlFilter");
/*     */   
/*     */   public static UrlFilter getInstance() {
/*  56 */     synchronized (UrlFilter.class) {
/*  57 */       if (instance == null) {
/*  58 */         instance = new UrlFilter();
/*     */       }
/*  60 */       return instance;
/*     */     }
/*     */   }
/*     */   
/*     */   public UrlFilter() {
/*  65 */     addUrlWhitelist(this.DEFAULT_RPC_WHITELIST);
/*  66 */     addUrlWhitelist("https?://([^.]+.?)?vuze.com:?[0-9]*/.*");
/*  67 */     addUrlWhitelist("https?://192\\.168\\.0\\.*:?[0-9]*/.*");
/*  68 */     addUrlWhitelist("https?://localhost:?[0-9]*/.*");
/*     */     
/*  70 */     addUrlWhitelist("https?://plusone\\.google\\.com/.*");
/*  71 */     addUrlWhitelist("https?://clients[0-9]\\.google\\.com/.*");
/*     */     
/*  73 */     ContentNetworkManager cmn = ContentNetworkManagerFactory.getSingleton();
/*  74 */     ContentNetwork[] contentNetworks = cmn.getContentNetworks();
/*  75 */     cmn.addListener(new ContentNetworkListener()
/*     */     {
/*     */       public void networkRemoved(ContentNetwork network) {}
/*     */       
/*     */       public void networkChanged(ContentNetwork network) {}
/*     */       
/*     */       public void networkAdded(ContentNetwork network)
/*     */       {
/*  83 */         UrlFilter.this.addNetworkFilters(network);
/*     */       }
/*     */       
/*     */ 
/*     */       public void networkAddFailed(long network_id, Throwable error) {}
/*     */     });
/*     */     
/*  90 */     for (ContentNetwork cn : contentNetworks) {
/*  91 */       addNetworkFilters(cn);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addNetworkFilters(ContentNetwork network)
/*     */   {
/* 103 */     if (network == null) {
/* 104 */       return;
/*     */     }
/*     */     
/* 107 */     int[] whitelist_services = { 15, 5, 4, 3 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */     for (int service : whitelist_services)
/*     */     {
/* 116 */       if (network.isServiceSupported(service))
/*     */       {
/* 118 */         String serviceUrl = network.getServiceURL(service);
/*     */         
/* 120 */         if (!isWhitelisted(serviceUrl))
/*     */         {
/* 122 */           addUrlWhitelist(serviceUrl + ".*");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void addUrlWhitelist(String string) {
/* 129 */     addUrlWhitelistSupport(string);
/*     */     
/* 131 */     if (string.contains("://localhost"))
/*     */     {
/* 133 */       addUrlWhitelistSupport(string.replace("://localhost", "://127.0.0.1"));
/*     */     }
/*     */   }
/*     */   
/*     */   private void addUrlWhitelistSupport(String string) {
/* 138 */     this.mon.enter();
/*     */     try {
/* 140 */       if (!this.listUrlWhitelist.contains(string)) {
/* 141 */         PlatformMessenger.debug("add whitelist of " + string);
/* 142 */         this.listUrlWhitelist.add(string);
/*     */       } else {
/* 144 */         PlatformMessenger.debug("whitelist already exists: " + string);
/*     */       }
/*     */     } finally {
/* 147 */       this.mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addUrlBlacklist(String string) {
/* 152 */     this.mon.enter();
/*     */     try {
/* 154 */       if (!this.listUrlBlacklist.contains(string)) {
/* 155 */         PlatformMessenger.debug("add blacklist of " + string);
/* 156 */         this.listUrlBlacklist.add(string);
/*     */       }
/*     */     } finally {
/* 159 */       this.mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] getUrlWhitelist()
/*     */   {
/* 165 */     return (String[])this.listUrlWhitelist.toArray(new String[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isWhitelisted(String url)
/*     */   {
/* 172 */     Iterator<String> it = this.listUrlWhitelist.iterator();
/*     */     
/* 174 */     while (it.hasNext()) {
/* 175 */       if (url.matches((String)it.next())) {
/* 176 */         return true;
/*     */       }
/*     */     }
/* 179 */     return false;
/*     */   }
/*     */   
/*     */   public boolean urlCanRPC(String url) {
/* 183 */     return urlCanRPC(url, false);
/*     */   }
/*     */   
/*     */   public boolean urlCanRPC(String url, boolean showDebug) {
/* 187 */     if (url == null) {
/* 188 */       Debug.out("URL null and should be blocked");
/* 189 */       return false;
/*     */     }
/*     */     
/* 192 */     if ((Constants.isCVSVersion()) && (url.startsWith("file://"))) {
/* 193 */       return true;
/*     */     }
/*     */     
/* 196 */     if (isWhitelisted(url))
/*     */     {
/* 198 */       return true;
/*     */     }
/*     */     
/* 201 */     if (showDebug) {
/* 202 */       Debug.out("urlCanRPC: URL '" + url + "' " + " does not match one of the " + this.listUrlWhitelist.size() + " whitelist entries");
/*     */     }
/*     */     
/* 205 */     return false;
/*     */   }
/*     */   
/*     */   public boolean urlIsBlocked(String url) {
/* 209 */     if (url == null) {
/* 210 */       Debug.out("URL null and should be blocked");
/* 211 */       return true;
/*     */     }
/*     */     
/* 214 */     for (Iterator<String> iter = this.listUrlBlacklist.iterator(); iter.hasNext();) {
/* 215 */       String blackListed = (String)iter.next();
/* 216 */       if (url.matches(blackListed)) {
/* 217 */         Debug.out("URL '" + url + "' " + " is blocked by " + blackListed);
/* 218 */         return true;
/*     */       }
/*     */     }
/* 221 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/UrlFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */