/*     */ package com.aelitis.azureus.core.subs;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
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
/*     */ public class SubscriptionUtils
/*     */ {
/*     */   public static SubscriptionDownloadDetails[] getAllCachedDownloadDetails(AzureusCore core)
/*     */   {
/*  39 */     List<DownloadManager> dms = core.getGlobalManager().getDownloadManagers();
/*     */     
/*  41 */     List<SubscriptionDownloadDetails> result = new ArrayList();
/*     */     
/*  43 */     SubscriptionManager sub_man = SubscriptionManagerFactory.getSingleton();
/*     */     
/*  45 */     for (int i = 0; i < dms.size(); i++)
/*     */     {
/*  47 */       DownloadManager dm = (DownloadManager)dms.get(i);
/*     */       
/*  49 */       TOTorrent torrent = dm.getTorrent();
/*     */       
/*  51 */       if (torrent != null) {
/*     */         try
/*     */         {
/*  54 */           Subscription[] subs = sub_man.getKnownSubscriptions(torrent.getHash());
/*     */           
/*  56 */           if ((subs != null) && (subs.length > 0))
/*     */           {
/*  58 */             if (sub_man.hideSearchTemplates())
/*     */             {
/*  60 */               List<Subscription> filtered = new ArrayList();
/*     */               
/*  62 */               for (Subscription s : subs)
/*     */               {
/*  64 */                 if (!s.isSearchTemplate())
/*     */                 {
/*  66 */                   filtered.add(s);
/*     */                 }
/*     */               }
/*     */               
/*  70 */               if (filtered.size() > 0)
/*     */               {
/*  72 */                 result.add(new SubscriptionDownloadDetails(dm, (Subscription[])filtered.toArray(new Subscription[filtered.size()])));
/*     */               }
/*     */             }
/*     */             else {
/*  76 */               result.add(new SubscriptionDownloadDetails(dm, subs));
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/*  84 */     return (SubscriptionDownloadDetails[])result.toArray(new SubscriptionDownloadDetails[result.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getSubscriptionChatKey(Subscription subs)
/*     */   {
/*     */     try
/*     */     {
/*  92 */       String key = null;
/*     */       
/*  94 */       Engine engine = subs.getEngine();
/*     */       
/*  96 */       if ((engine instanceof WebEngine))
/*     */       {
/*  98 */         WebEngine web_engine = (WebEngine)subs.getEngine();
/*     */         
/* 100 */         key = web_engine.getSearchUrl(true);
/*     */       }
/*     */       else
/*     */       {
/* 104 */         key = subs.getQueryKey();
/*     */       }
/*     */       
/* 107 */       if (key != null) {}
/*     */       
/* 109 */       return "Subscription: " + key;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 116 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void peekChatAsync(String net, String key, Runnable done)
/*     */   {
/*     */     try
/*     */     {
/* 129 */       Class<?> utils = SubscriptionUtils.class.getClassLoader().loadClass("com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils");
/*     */       
/* 131 */       if (utils != null)
/*     */       {
/* 133 */         utils.getMethod("peekChatAsync", new Class[] { String.class, String.class, Runnable.class }).invoke(null, new Object[] { net, key, done });
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class SubscriptionDownloadDetails
/*     */   {
/*     */     private DownloadManager download;
/*     */     
/*     */ 
/*     */     private Subscription[] subscriptions;
/*     */     
/*     */ 
/*     */     protected SubscriptionDownloadDetails(DownloadManager dm, Subscription[] subs)
/*     */     {
/* 151 */       this.download = dm;
/* 152 */       this.subscriptions = subs;
/*     */     }
/*     */     
/*     */ 
/*     */     public DownloadManager getDownload()
/*     */     {
/* 158 */       return this.download;
/*     */     }
/*     */     
/*     */ 
/*     */     public Subscription[] getSubscriptions()
/*     */     {
/* 164 */       return this.subscriptions;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */