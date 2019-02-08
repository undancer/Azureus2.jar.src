/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin;
/*     */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin.Provider;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.core3.xml.util.XMLEscapeWriter;
/*     */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class DeviceManagerRSSFeed
/*     */   implements RSSGeneratorPlugin.Provider
/*     */ {
/*     */   private static final String PROVIDER = "devices";
/*     */   private DeviceManagerImpl manager;
/*     */   private RSSGeneratorPlugin generator;
/*     */   
/*     */   protected DeviceManagerRSSFeed(DeviceManagerImpl _manager)
/*     */   {
/*  69 */     this.manager = _manager;
/*  70 */     this.generator = RSSGeneratorPlugin.getSingleton();
/*     */     
/*  72 */     if (this.generator != null)
/*     */     {
/*  74 */       RSSGeneratorPlugin.registerProvider("devices", this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  81 */     return this.manager.isRSSPublishEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeedURL()
/*     */   {
/*  87 */     return this.generator.getURL() + "devices";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*     */     throws IOException
/*     */   {
/*  97 */     InetSocketAddress local_address = request.getLocalAddress();
/*     */     
/*  99 */     if (local_address == null)
/*     */     {
/* 101 */       return false;
/*     */     }
/*     */     
/* 104 */     URL url = request.getAbsoluteURL();
/*     */     
/* 106 */     String path = url.getPath();
/*     */     
/* 108 */     path = path.substring("devices".length() + 1);
/*     */     
/* 110 */     DeviceImpl[] devices = this.manager.getDevices();
/*     */     
/* 112 */     OutputStream os = response.getOutputStream();
/*     */     
/* 114 */     XMLEscapeWriter pw = new XMLEscapeWriter(new PrintWriter(new OutputStreamWriter(os, "UTF-8")));
/*     */     
/* 116 */     pw.setEnabled(false);
/*     */     
/* 118 */     boolean hide_generic = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.hidegeneric", true);
/*     */     
/* 120 */     boolean show_only_tagged = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.showonlytagged", false);
/*     */     
/* 122 */     if (path.length() <= 1)
/*     */     {
/* 124 */       response.setContentType("text/html; charset=UTF-8");
/*     */       
/* 126 */       pw.println("<HTML><HEAD><TITLE>Vuze Device Feeds</TITLE></HEAD><BODY>");
/*     */       
/* 128 */       for (DeviceImpl d : devices)
/*     */       {
/* 130 */         if ((d.getType() == 3) && (!d.isHidden()) && (d.isRSSPublishEnabled()) && ((!hide_generic) || (!d.isNonSimple())) && ((!show_only_tagged) || (d.isTagged())))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 139 */           String name = d.getName();
/*     */           
/* 141 */           String device_url = "devices/" + URLEncoder.encode(name, "UTF-8");
/*     */           
/* 143 */           pw.println("<LI><A href=\"" + device_url + "\">" + name + "</A>&nbsp;&nbsp;-&nbsp;&nbsp;<font size=\"-1\"><a href=\"" + device_url + "?format=html\">html</a></font></LI>");
/*     */         }
/*     */       }
/* 146 */       pw.println("</BODY></HTML>");
/*     */     }
/*     */     else
/*     */     {
/* 150 */       String device_name = URLDecoder.decode(path.substring(1), "UTF-8");
/*     */       
/* 152 */       DeviceImpl device = null;
/*     */       
/* 154 */       for (DeviceImpl d : devices)
/*     */       {
/* 156 */         if ((d.getName().equals(device_name)) && (d.isRSSPublishEnabled()))
/*     */         {
/* 158 */           device = d;
/*     */           
/* 160 */           break;
/*     */         }
/*     */       }
/*     */       
/* 164 */       if (device == null)
/*     */       {
/* 166 */         response.setReplyStatus(404);
/*     */         
/* 168 */         return true;
/*     */       }
/*     */       
/* 171 */       TranscodeFileImpl[] _files = device.getFiles();
/*     */       
/* 173 */       List<TranscodeFileImpl> files = new ArrayList(_files.length);
/*     */       
/* 175 */       files.addAll(Arrays.asList(_files));
/*     */       
/* 177 */       Collections.sort(files, new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public int compare(TranscodeFileImpl f1, TranscodeFileImpl f2)
/*     */         {
/*     */ 
/*     */ 
/* 186 */           long added1 = f1.getCreationDateMillis() / 1000L;
/* 187 */           long added2 = f2.getCreationDateMillis() / 1000L;
/*     */           
/* 189 */           return (int)(added2 - added1);
/*     */         }
/*     */         
/* 192 */       });
/* 193 */       URL feed_url = url;
/*     */       
/*     */ 
/*     */ 
/* 197 */       String host = (String)request.getHeaders().get("host");
/*     */       
/* 199 */       if (host != null)
/*     */       {
/* 201 */         int pos = host.indexOf(':');
/*     */         
/* 203 */         if (pos != -1)
/*     */         {
/* 205 */           host = host.substring(0, pos);
/*     */         }
/*     */         
/* 208 */         feed_url = UrlUtils.setHost(url, host);
/*     */       }
/*     */       
/* 211 */       if ((device instanceof DeviceMediaRendererImpl))
/*     */       {
/* 213 */         ((DeviceMediaRendererImpl)device).browseReceived();
/*     */       }
/*     */       
/* 216 */       String channel_title = "Vuze Device: " + escape(device.getName());
/*     */       
/* 218 */       boolean html = request.getURL().contains("format=html");
/*     */       
/* 220 */       if (html)
/*     */       {
/* 222 */         response.setContentType("text/html; charset=UTF-8");
/*     */         
/* 224 */         pw.println("<HTML><HEAD><TITLE>" + channel_title + "</TITLE></HEAD><BODY>");
/*     */         
/*     */ 
/* 227 */         for (TranscodeFileImpl file : files)
/*     */         {
/* 229 */           if ((file.isComplete()) || 
/*     */           
/* 231 */             (file.isTemplate()))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 237 */             URL stream_url = file.getStreamURL(feed_url.getHost());
/*     */             
/* 239 */             if (stream_url != null)
/*     */             {
/* 241 */               String url_ext = stream_url.toExternalForm();
/*     */               
/* 243 */               pw.println("<p>");
/*     */               
/* 245 */               pw.println("<a href=\"" + url_ext + "\">" + escape(file.getName()) + "</a>");
/*     */               
/* 247 */               url_ext = url_ext + (url_ext.indexOf('?') == -1 ? "?" : "&");
/*     */               
/* 249 */               url_ext = url_ext + "action=download";
/*     */               
/* 251 */               pw.println("&nbsp;&nbsp;-&nbsp;&nbsp;<font size=\"-1\"><a href=\"" + url_ext + "\">save</a></font>");
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 256 */         pw.println("</BODY></HTML>");
/*     */       }
/*     */       else {
/* 259 */         boolean debug = request.getURL().contains("format=debug");
/*     */         
/* 261 */         if (debug)
/*     */         {
/* 263 */           response.setContentType("text/html; charset=UTF-8");
/*     */           
/* 265 */           pw.println("<HTML><HEAD><TITLE>" + channel_title + "</TITLE></HEAD><BODY>");
/*     */           
/* 267 */           pw.println("<pre>");
/*     */           
/* 269 */           pw.setEnabled(true);
/*     */         }
/*     */         else
/*     */         {
/* 273 */           response.setContentType("application/xml");
/*     */         }
/*     */         try
/*     */         {
/* 277 */           pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
/*     */           
/* 279 */           pw.println("<rss version=\"2.0\" xmlns:vuze=\"http://www.vuze.com\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\">");
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 286 */           pw.println("<channel>");
/*     */           
/* 288 */           pw.println("<title>" + channel_title + "</title>");
/* 289 */           pw.println("<link>http://vuze.com</link>");
/* 290 */           pw.println("<atom:link href=\"" + feed_url.toExternalForm() + "\" rel=\"self\" type=\"application/rss+xml\" />");
/*     */           
/* 292 */           pw.println("<description>Vuze RSS Feed for device " + escape(device.getName()) + "</description>");
/*     */           
/* 294 */           pw.println("<itunes:image href=\"http://www.vuze.com/img/vuze_icon_128.png\"/>");
/* 295 */           pw.println("<image><url>http://www.vuze.com/img/vuze_icon_128.png</url><title>" + channel_title + "</title><link>http://vuze.com</link></image>");
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 300 */           String feed_date_key = "devices.feed_date." + device.getID();
/*     */           
/* 302 */           long feed_date = COConfigurationManager.getLongParameter(feed_date_key);
/*     */           
/* 304 */           boolean new_date = false;
/*     */           
/* 306 */           for (TranscodeFileImpl file : files)
/*     */           {
/* 308 */             long file_date = file.getCreationDateMillis();
/*     */             
/* 310 */             if (file_date > feed_date)
/*     */             {
/* 312 */               new_date = true;
/*     */               
/* 314 */               feed_date = file_date;
/*     */             }
/*     */           }
/*     */           
/* 318 */           if (new_date)
/*     */           {
/* 320 */             COConfigurationManager.setParameter(feed_date_key, feed_date);
/*     */           }
/*     */           
/* 323 */           pw.println("<pubDate>" + TimeFormatter.getHTTPDate(feed_date) + "</pubDate>");
/*     */           
/* 325 */           for (TranscodeFileImpl file : files)
/*     */           {
/* 327 */             if ((file.isComplete()) || 
/*     */             
/* 329 */               (file.isTemplate()))
/*     */             {
/*     */ 
/*     */               try
/*     */               {
/*     */ 
/*     */ 
/* 336 */                 pw.println("<item>");
/*     */                 
/* 338 */                 pw.println("<title>" + escape(file.getName()) + "</title>");
/*     */                 
/* 340 */                 pw.println("<pubDate>" + TimeFormatter.getHTTPDate(file.getCreationDateMillis()) + "</pubDate>");
/*     */                 
/* 342 */                 pw.println("<guid isPermaLink=\"false\">" + escape(file.getKey()) + "</guid>");
/*     */                 
/* 344 */                 String[] categories = file.getCategories();
/*     */                 
/* 346 */                 for (String category : categories)
/*     */                 {
/* 348 */                   pw.println("<category>" + escape(category) + "</category>");
/*     */                 }
/*     */                 
/* 351 */                 String[] tags = file.getTags(true);
/*     */                 
/* 353 */                 for (String tag : tags)
/*     */                 {
/* 355 */                   pw.println("<tag>" + escape(tag) + "</tag>");
/*     */                 }
/*     */                 
/* 358 */                 String mediaContent = "";
/*     */                 
/* 360 */                 URL stream_url = file.getStreamURL(feed_url.getHost());
/*     */                 
/* 362 */                 if (stream_url != null)
/*     */                 {
/* 364 */                   String url_ext = escape(stream_url.toExternalForm());
/*     */                   
/* 366 */                   long fileSize = file.getTargetFile().getLength();
/*     */                   
/* 368 */                   pw.println("<link>" + url_ext + "</link>");
/*     */                   
/* 370 */                   mediaContent = "<media:content medium=\"video\" fileSize=\"" + fileSize + "\" url=\"" + url_ext + "\"";
/*     */                   
/*     */ 
/* 373 */                   String mime_type = file.getMimeType();
/*     */                   
/* 375 */                   if (mime_type != null)
/*     */                   {
/* 377 */                     mediaContent = mediaContent + " type=\"" + mime_type + "\"";
/*     */                   }
/*     */                   
/* 380 */                   pw.println("<enclosure url=\"" + url_ext + "\" length=\"" + fileSize + (mime_type == null ? "" : new StringBuilder().append("\" type=\"").append(mime_type).toString()) + "\"></enclosure>");
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 386 */                 String thumb_url = null;
/* 387 */                 String author = null;
/* 388 */                 String description = null;
/*     */                 try
/*     */                 {
/* 391 */                   Torrent torrent = file.getSourceFile().getDownload().getTorrent();
/*     */                   
/* 393 */                   TOTorrent toTorrent = PluginCoreUtils.unwrap(torrent);
/*     */                   
/* 395 */                   long duration_secs = PlatformTorrentUtils.getContentVideoRunningTime(toTorrent);
/*     */                   
/* 397 */                   if ((mediaContent.length() > 0) && (duration_secs > 0L))
/*     */                   {
/* 399 */                     mediaContent = mediaContent + " duration=\"" + duration_secs + "\"";
/*     */                   }
/*     */                   
/* 402 */                   thumb_url = PlatformTorrentUtils.getContentThumbnailUrl(toTorrent);
/*     */                   
/* 404 */                   author = PlatformTorrentUtils.getContentAuthor(toTorrent);
/*     */                   
/* 406 */                   description = PlatformTorrentUtils.getContentDescription(toTorrent);
/*     */                   
/* 408 */                   if (description != null)
/*     */                   {
/* 410 */                     description = escapeMultiline(description);
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
/* 423 */                     pw.println("<description>" + description + "</description>");
/*     */                   }
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 431 */                 if (mediaContent.length() > 0)
/*     */                 {
/* 433 */                   pw.println(mediaContent += "></media:content>");
/*     */                 }
/*     */                 
/* 436 */                 pw.println("<media:title>" + escape(file.getName()) + "</media:title>");
/*     */                 
/* 438 */                 if (description != null)
/*     */                 {
/* 440 */                   pw.println("<media:description>" + description + "</media:description>");
/*     */                 }
/*     */                 
/* 443 */                 if (thumb_url != null)
/*     */                 {
/* 445 */                   pw.println("<media:thumbnail url=\"" + thumb_url + "\"/>");
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 450 */                 if (thumb_url != null)
/*     */                 {
/* 452 */                   pw.println("<itunes:image href=\"" + thumb_url + "\"/>");
/*     */                 }
/*     */                 
/* 455 */                 if (author != null)
/*     */                 {
/* 457 */                   pw.println("<itunes:author>" + escape(author) + "</itunees:author>");
/*     */                 }
/*     */                 
/* 460 */                 pw.println("<itunes:summary>" + escape(file.getName()) + "</itunes:summary>");
/* 461 */                 pw.println("<itunes:duration>" + TimeFormatter.formatColon(file.getDurationMillis() / 1000L) + "</itunes:duration>");
/*     */                 
/* 463 */                 pw.println("</item>");
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 467 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */           }
/* 471 */           pw.println("</channel>");
/*     */           
/* 473 */           pw.println("</rss>");
/*     */         }
/*     */         finally
/*     */         {
/* 477 */           if (debug)
/*     */           {
/* 479 */             pw.setEnabled(false);
/*     */             
/* 481 */             pw.println("</pre>");
/*     */             
/* 483 */             pw.println("</BODY></HTML>");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 489 */     pw.flush();
/*     */     
/* 491 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String escape(String str)
/*     */   {
/* 498 */     return XUXmlWriter.escapeXML(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String escapeMultiline(String str)
/*     */   {
/* 505 */     return XUXmlWriter.escapeXML(str.replaceAll("[\r\n]+", "<BR>"));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceManagerRSSFeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */