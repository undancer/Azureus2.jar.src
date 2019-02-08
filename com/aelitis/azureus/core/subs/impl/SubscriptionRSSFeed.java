/*     */ package com.aelitis.azureus.core.subs.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin;
/*     */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin.Provider;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.plugins.utils.subscriptions.Subscription;
/*     */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionManager;
/*     */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionResult;
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
/*     */ public class SubscriptionRSSFeed
/*     */   implements RSSGeneratorPlugin.Provider
/*     */ {
/*     */   private static final String PROVIDER = "subscriptions";
/*     */   private SubscriptionManagerImpl manager;
/*     */   private PluginInterface plugin_interface;
/*     */   private RSSGeneratorPlugin generator;
/*     */   
/*     */   protected SubscriptionRSSFeed(SubscriptionManagerImpl _manager, PluginInterface _plugin_interface)
/*     */   {
/*  64 */     this.manager = _manager;
/*  65 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  67 */     this.generator = RSSGeneratorPlugin.getSingleton();
/*     */     
/*  69 */     if (this.generator != null)
/*     */     {
/*  71 */       RSSGeneratorPlugin.registerProvider("subscriptions", this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  78 */     return this.manager.isRSSPublishEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeedURL()
/*     */   {
/*  84 */     return this.generator.getURL() + "subscriptions";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*     */     throws IOException
/*     */   {
/*  94 */     InetSocketAddress local_address = request.getLocalAddress();
/*     */     
/*  96 */     if (local_address == null)
/*     */     {
/*  98 */       return false;
/*     */     }
/*     */     
/* 101 */     URL url = request.getAbsoluteURL();
/*     */     
/* 103 */     String path = url.getPath();
/*     */     
/* 105 */     path = path.substring("subscriptions".length() + 1);
/*     */     try
/*     */     {
/* 108 */       SubscriptionManager sman = this.plugin_interface.getUtilities().getSubscriptionManager();
/*     */       
/* 110 */       Subscription[] subs = sman.getSubscriptions();
/*     */       
/* 112 */       OutputStream os = response.getOutputStream();
/*     */       
/* 114 */       PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
/*     */       
/* 116 */       if (path.length() <= 1)
/*     */       {
/* 118 */         response.setContentType("text/html; charset=UTF-8");
/*     */         
/* 120 */         pw.println("<HTML><HEAD><TITLE>Vuze Subscription Feeds</TITLE></HEAD><BODY>");
/*     */         
/* 122 */         for (Subscription s : subs)
/*     */         {
/* 124 */           if (!s.isSearchTemplate())
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 129 */             String name = s.getName();
/*     */             
/* 131 */             pw.println("<LI><A href=\"subscriptions/" + s.getID() + "\">" + name + "</A></LI>");
/*     */           }
/*     */         }
/* 134 */         pw.println("</BODY></HTML>");
/*     */       }
/*     */       else
/*     */       {
/* 138 */         String id = path.substring(1);
/*     */         
/* 140 */         Subscription subscription = null;
/*     */         
/* 142 */         for (Subscription s : subs)
/*     */         {
/* 144 */           if (s.getID().equals(id))
/*     */           {
/* 146 */             subscription = s;
/*     */             
/* 148 */             break;
/*     */           }
/*     */         }
/*     */         
/* 152 */         if (subscription == null)
/*     */         {
/* 154 */           response.setReplyStatus(404);
/*     */           
/* 156 */           return true;
/*     */         }
/*     */         
/* 159 */         URL feed_url = url;
/*     */         
/*     */ 
/*     */ 
/* 163 */         String host = (String)request.getHeaders().get("host");
/*     */         
/* 165 */         if (host != null)
/*     */         {
/* 167 */           int pos = host.indexOf(':');
/*     */           
/* 169 */           if (pos != -1)
/*     */           {
/* 171 */             host = host.substring(0, pos);
/*     */           }
/*     */           
/* 174 */           feed_url = UrlUtils.setHost(url, host);
/*     */         }
/*     */         
/* 177 */         response.setContentType("application/xml");
/*     */         
/* 179 */         pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
/*     */         
/* 181 */         pw.println("<rss version=\"2.0\" xmlns:vuze=\"http://www.vuze.com\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\">");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 188 */         pw.println("<channel>");
/*     */         
/* 190 */         String channel_title = "Vuze Subscription: " + escape(subscription.getName());
/*     */         
/* 192 */         pw.println("<title>" + channel_title + "</title>");
/* 193 */         pw.println("<link>http://vuze.com</link>");
/* 194 */         pw.println("<atom:link href=\"" + escape(feed_url.toExternalForm()) + "\" rel=\"self\" type=\"application/rss+xml\" />");
/*     */         
/* 196 */         pw.println("<description>Vuze RSS Feed for subscription " + escape(subscription.getName()) + "</description>");
/*     */         
/* 198 */         pw.println("<itunes:image href=\"http://www.vuze.com/img/vuze_icon_128.png\"/>");
/* 199 */         pw.println("<image><url>http://www.vuze.com/img/vuze_icon_128.png</url><title>" + channel_title + "</title><link>http://vuze.com</link></image>");
/*     */         
/*     */ 
/* 202 */         SubscriptionResult[] results = subscription.getResults();
/*     */         
/*     */ 
/* 205 */         String feed_date_key = "subscriptions.feed_date." + subscription.getID();
/*     */         
/* 207 */         long feed_date = COConfigurationManager.getLongParameter(feed_date_key);
/*     */         
/* 209 */         boolean new_date = false;
/*     */         
/* 211 */         for (SubscriptionResult result : results)
/*     */         {
/* 213 */           Date date = (Date)result.getProperty(2);
/*     */           
/* 215 */           long millis = date.getTime();
/*     */           
/* 217 */           if (millis > feed_date)
/*     */           {
/* 219 */             feed_date = millis;
/*     */             
/* 221 */             new_date = true;
/*     */           }
/*     */         }
/*     */         
/* 225 */         if (new_date)
/*     */         {
/* 227 */           COConfigurationManager.setParameter(feed_date_key, feed_date);
/*     */         }
/*     */         
/* 230 */         pw.println("<pubDate>" + TimeFormatter.getHTTPDate(feed_date) + "</pubDate>");
/*     */         
/*     */ 
/* 233 */         for (SubscriptionResult result : results) {
/*     */           try
/*     */           {
/* 236 */             pw.println("<item>");
/*     */             
/* 238 */             String name = (String)result.getProperty(1);
/*     */             
/* 240 */             pw.println("<title>" + escape(name) + "</title>");
/*     */             
/* 242 */             Date date = (Date)result.getProperty(2);
/*     */             
/* 244 */             if (date != null)
/*     */             {
/* 246 */               pw.println("<pubDate>" + TimeFormatter.getHTTPDate(date.getTime()) + "</pubDate>");
/*     */             }
/*     */             
/* 249 */             String uid = (String)result.getProperty(20);
/*     */             
/* 251 */             if (uid != null)
/*     */             {
/* 253 */               pw.println("<guid isPermaLink=\"false\">" + escape(uid) + "</guid>");
/*     */             }
/*     */             
/* 256 */             String link = (String)result.getProperty(12);
/* 257 */             Long size = (Long)result.getProperty(3);
/*     */             
/* 259 */             if (link != null)
/*     */             {
/* 261 */               pw.println("<link>" + escape(link) + "</link>");
/*     */               
/*     */ 
/* 264 */               if (size != null)
/*     */               {
/* 266 */                 pw.println("<media:content fileSize=\"" + size + "\" url=\"" + escape(link) + "\"/>");
/*     */               }
/*     */             }
/*     */             
/* 270 */             if (size != null)
/*     */             {
/* 272 */               pw.println("<vuze:size>" + size + "</vuze:size>");
/*     */             }
/*     */             
/* 275 */             Long seeds = (Long)result.getProperty(5);
/*     */             
/* 277 */             if (seeds != null)
/*     */             {
/* 279 */               pw.println("<vuze:seeds>" + seeds + "</vuze:seeds>");
/*     */             }
/*     */             
/* 282 */             Long peers = (Long)result.getProperty(4);
/*     */             
/* 284 */             if (peers != null)
/*     */             {
/* 286 */               pw.println("<vuze:peers>" + peers + "</vuze:peers>");
/*     */             }
/*     */             
/* 289 */             Long rank = (Long)result.getProperty(17);
/*     */             
/* 291 */             if (rank != null)
/*     */             {
/* 293 */               pw.println("<vuze:rank>" + rank + "</vuze:rank>");
/*     */             }
/*     */             
/*     */ 
/* 297 */             pw.println("</item>");
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 301 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */         
/* 305 */         pw.println("</channel>");
/*     */         
/* 307 */         pw.println("</rss>");
/*     */       }
/*     */       
/* 310 */       pw.flush();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 314 */       Debug.out(e);
/*     */       
/* 316 */       throw new IOException(Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */     
/* 319 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String escape(String str)
/*     */   {
/* 326 */     return XUXmlWriter.escapeXML(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String escapeMultiline(String str)
/*     */   {
/* 333 */     return XUXmlWriter.escapeXML(str.replaceAll("[\r\n]+", "<BR>"));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionRSSFeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */