/*     */ package com.aelitis.azureus.core.metasearch.impl.web.rss;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*     */ import com.aelitis.azureus.core.metasearch.SearchException;
/*     */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*     */ import com.aelitis.azureus.core.metasearch.impl.EngineImpl;
/*     */ import com.aelitis.azureus.core.metasearch.impl.MetaSearchImpl;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.FieldMapping;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine.pageDetails;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine.pageDetailsVerifier;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebResult;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSChannel;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSFeed;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSItem;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentAttribute;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
/*     */ import org.json.simple.JSONObject;
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
/*     */ public class RSSEngine
/*     */   extends WebEngine
/*     */ {
/*  59 */   private Pattern seed_leecher_pat = Pattern.compile("([0-9]+)\\s+(seed|leecher)s", 2);
/*  60 */   private Pattern size_pat = Pattern.compile("([0-9\\.]+)\\s+(B|KB|KiB|MB|MiB|GB|GiB|TB|TiB)", 2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EngineImpl importFromBEncodedMap(MetaSearchImpl meta_search, Map map)
/*     */     throws IOException
/*     */   {
/*  69 */     return new RSSEngine(meta_search, map);
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
/*     */   public static Engine importFromJSONString(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, JSONObject map)
/*     */     throws IOException
/*     */   {
/*  83 */     return new RSSEngine(meta_search, id, last_updated, rank_bias, name, map);
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
/*     */   public RSSEngine(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, String searchURLFormat, boolean needs_auth, String auth_method, String login_url, String[] required_cookies)
/*     */   {
/* 101 */     super(meta_search, 4, id, last_updated, rank_bias, name, searchURLFormat, "GMT", false, "EEE, d MMM yyyy HH:mm:ss Z", new FieldMapping[0], needs_auth, auth_method, login_url, required_cookies);
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
/*     */   protected RSSEngine(MetaSearchImpl meta_search, Map map)
/*     */     throws IOException
/*     */   {
/* 125 */     super(meta_search, map);
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
/*     */   protected RSSEngine(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, JSONObject map)
/*     */     throws IOException
/*     */   {
/* 141 */     super(meta_search, 1, id, last_updated, rank_bias, name, map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap()
/*     */     throws IOException
/*     */   {
/* 150 */     return exportToBencodedMap(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap(boolean generic)
/*     */     throws IOException
/*     */   {
/* 159 */     Map res = new HashMap();
/*     */     
/* 161 */     super.exportToBencodedMap(res, generic);
/*     */     
/* 163 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean supportsField(int field_id)
/*     */   {
/* 172 */     switch (field_id)
/*     */     {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 102: 
/*     */     case 103: 
/*     */     case 105: 
/* 181 */       return true;
/*     */     }
/*     */     
/*     */     
/* 185 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAutoDownloadSupported()
/*     */   {
/* 194 */     return (int)getLocalLong("auto_dl_supported", 0L);
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
/*     */   protected Result[] searchSupport(SearchParameter[] searchParameters, Map searchContext, int desired_max_matches, int absolute_max_matches, String headers, ResultListener listener)
/*     */     throws SearchException
/*     */   {
/* 208 */     debugStart();
/*     */     
/* 210 */     boolean only_if_mod = !searchContext.containsKey("force_full");
/*     */     
/* 212 */     WebEngine.pageDetails page_details = super.getWebPageContent(searchParameters, searchContext, headers, only_if_mod, new WebEngine.pageDetailsVerifier()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void verify(WebEngine.pageDetails details)
/*     */         throws SearchException
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/* 227 */           String page = details.getContent();
/*     */           
/* 229 */           if ((page != null) && (page.length() > 0))
/*     */           {
/* 231 */             ByteArrayInputStream bais = new ByteArrayInputStream(page.getBytes("UTF-8"));
/*     */             
/* 233 */             RSSFeed rssFeed = StaticUtilities.getRSSFeed(details.getInitialURL(), bais);
/*     */             
/* 235 */             details.setVerifiedState(rssFeed);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 239 */           RSSEngine.this.debugLog("failed: " + Debug.getNestedExceptionMessageAndStack(e));
/*     */           
/* 241 */           if ((e instanceof SearchException))
/*     */           {
/* 243 */             throw ((SearchException)e);
/*     */           }
/*     */           
/* 246 */           throw new SearchException("RSS matching failed", e);
/*     */         }
/*     */         
/*     */       }
/* 250 */     });
/* 251 */     String page = page_details.getContent();
/*     */     
/* 253 */     if (listener != null)
/*     */     {
/* 255 */       listener.contentReceived(this, page);
/*     */     }
/*     */     
/* 258 */     if ((page == null) || (page.length() == 0))
/*     */     {
/* 260 */       return new Result[0];
/*     */     }
/*     */     try
/*     */     {
/* 264 */       RSSFeed rssFeed = (RSSFeed)page_details.getVerifiedState();
/*     */       
/* 266 */       RSSChannel[] channels = rssFeed.getChannels();
/*     */       
/* 268 */       List results = new ArrayList();
/*     */       
/* 270 */       for (int i = 0; i < channels.length; i++)
/*     */       {
/* 272 */         RSSChannel channel = channels[i];
/*     */         
/* 274 */         SimpleXMLParserDocumentNode[] channel_kids = channel.getNode().getChildren();
/*     */         
/* 276 */         int auto_dl_state = 1;
/*     */         
/* 278 */         for (int j = 0; j < channel_kids.length; j++)
/*     */         {
/* 280 */           SimpleXMLParserDocumentNode child = channel_kids[j];
/*     */           
/* 282 */           String lc_full_child_name = child.getFullName().toLowerCase();
/*     */           
/* 284 */           if (lc_full_child_name.equals("vuze:auto_dl_enabled"))
/*     */           {
/* 286 */             if (!child.getValue().equalsIgnoreCase("true"))
/*     */             {
/* 288 */               auto_dl_state = 2;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 293 */         setLocalLong("auto_dl_supported", auto_dl_state);
/*     */         
/* 295 */         RSSItem[] items = channel.getItems();
/*     */         
/* 297 */         for (int j = 0; j < items.length; j++)
/*     */         {
/* 299 */           RSSItem item = items[j];
/*     */           
/* 301 */           WebResult result = new WebResult(this, getRootPage(), getBasePage(), getDateParser(), "");
/*     */           
/* 303 */           result.setPublishedDate(item.getPublicationDate());
/*     */           
/* 305 */           result.setNameFromHTML(item.getTitle());
/*     */           
/* 307 */           URL cdp_link = item.getLink();
/*     */           
/* 309 */           boolean cdp_set = false;
/*     */           
/* 311 */           if (cdp_link != null)
/*     */           {
/* 313 */             String link_url = cdp_link.toExternalForm();
/*     */             
/* 315 */             String lc_url = link_url.toLowerCase(Locale.US);
/*     */             
/* 317 */             if ((lc_url.startsWith("http")) || (lc_url.startsWith("tor:http")))
/*     */             {
/* 319 */               result.setCDPLink(link_url);
/*     */               
/* 321 */               cdp_set = true;
/*     */             }
/*     */           }
/*     */           
/* 325 */           String uid = item.getUID();
/*     */           
/* 327 */           if (uid != null)
/*     */           {
/* 329 */             result.setUID(uid);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 335 */             if (!cdp_set) {
/*     */               try
/*     */               {
/* 338 */                 String test_url = new URL(uid).toExternalForm();
/*     */                 
/* 340 */                 if (test_url.toLowerCase().startsWith("http"))
/*     */                 {
/* 342 */                   result.setCDPLink(test_url);
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */           
/* 349 */           boolean got_seeds_peers = false;
/*     */           
/* 351 */           int item_seeds = -1;
/* 352 */           int item_peers = -1;
/* 353 */           String item_hash = null;
/* 354 */           String item_magnet = null;
/*     */           
/* 356 */           String desc_size = null;
/*     */           
/* 358 */           SimpleXMLParserDocumentNode node = item.getNode();
/*     */           
/* 360 */           if (node != null)
/*     */           {
/* 362 */             SimpleXMLParserDocumentNode[] children = node.getChildren();
/*     */             
/* 364 */             boolean vuze_feed = false;
/*     */             
/* 366 */             for (int k = 0; k < children.length; k++)
/*     */             {
/* 368 */               SimpleXMLParserDocumentNode child = children[k];
/*     */               
/* 370 */               String lc_full_child_name = child.getFullName().toLowerCase();
/*     */               
/* 372 */               if (lc_full_child_name.startsWith("vuze:"))
/*     */               {
/* 374 */                 vuze_feed = true;
/*     */                 
/* 376 */                 break;
/*     */               }
/*     */             }
/*     */             
/* 380 */             for (int k = 0; k < children.length; k++)
/*     */             {
/* 382 */               SimpleXMLParserDocumentNode child = children[k];
/*     */               
/* 384 */               String lc_child_name = child.getName().toLowerCase();
/* 385 */               String lc_full_child_name = child.getFullName().toLowerCase();
/*     */               
/* 387 */               String value = child.getValue().trim();
/*     */               
/* 389 */               if (lc_child_name.equals("enclosure"))
/*     */               {
/* 391 */                 SimpleXMLParserDocumentAttribute typeAtt = child.getAttribute("type");
/*     */                 
/* 393 */                 if ((typeAtt != null) && (typeAtt.getValue().equalsIgnoreCase("application/x-bittorrent")))
/*     */                 {
/* 395 */                   SimpleXMLParserDocumentAttribute urlAtt = child.getAttribute("url");
/*     */                   
/* 397 */                   if (urlAtt != null)
/*     */                   {
/* 399 */                     result.setTorrentLink(urlAtt.getValue());
/*     */                   }
/*     */                   
/* 402 */                   SimpleXMLParserDocumentAttribute lengthAtt = child.getAttribute("length");
/*     */                   
/* 404 */                   if (lengthAtt != null)
/*     */                   {
/* 406 */                     result.setSizeFromHTML(lengthAtt.getValue());
/*     */                   }
/*     */                 }
/* 409 */               } else if (lc_child_name.equals("category"))
/*     */               {
/* 411 */                 result.setCategoryFromHTML(value);
/*     */               }
/* 413 */               else if (lc_child_name.equals("comments"))
/*     */               {
/* 415 */                 result.setCommentsFromHTML(value);
/*     */               }
/* 417 */               else if ((lc_child_name.equals("link")) || (lc_child_name.equals("guid")))
/*     */               {
/* 419 */                 String lc_value = value.toLowerCase();
/*     */                 try
/*     */                 {
/* 422 */                   URL url = new URL(value);
/*     */                   
/* 424 */                   if ((lc_value.endsWith(".torrent")) || (lc_value.startsWith("magnet:")) || (lc_value.startsWith("bc:")) || (lc_value.startsWith("bctp:")) || (lc_value.startsWith("dht:")))
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 431 */                     result.setTorrentLink(value);
/*     */                   }
/* 433 */                   else if ((lc_child_name.equals("link")) && (!vuze_feed))
/*     */                   {
/* 435 */                     long test = getLocalLong("link_is_torrent", 0L);
/*     */                     
/* 437 */                     if (test == 1L)
/*     */                     {
/* 439 */                       result.setTorrentLink(value);
/*     */                     }
/* 441 */                     else if ((test == 0L) || (SystemTime.getCurrentTime() - test > 60000L))
/*     */                     {
/* 443 */                       if (linkIsToTorrent(url))
/*     */                       {
/* 445 */                         result.setTorrentLink(value);
/*     */                         
/* 447 */                         setLocalLong("link_is_torrent", 1L);
/*     */                       }
/*     */                       else
/*     */                       {
/* 451 */                         setLocalLong("link_is_torrent", SystemTime.getCurrentTime());
/*     */                       }
/*     */                       
/*     */                     }
/*     */                     
/*     */                   }
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 460 */                   SimpleXMLParserDocumentAttribute typeAtt = child.getAttribute("type");
/*     */                   
/* 462 */                   if ((typeAtt != null) && (typeAtt.getValue().equalsIgnoreCase("application/x-bittorrent")))
/*     */                   {
/* 464 */                     SimpleXMLParserDocumentAttribute hrefAtt = child.getAttribute("href");
/*     */                     
/* 466 */                     if (hrefAtt != null)
/*     */                     {
/* 468 */                       String href = hrefAtt.getValue().trim();
/*     */                       
/*     */                       try
/*     */                       {
/* 472 */                         result.setTorrentLink(new URL(href).toExternalForm());
/*     */ 
/*     */                       }
/*     */                       catch (Throwable f) {}
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/* 480 */               else if ((lc_child_name.equals("content")) && (rssFeed.isAtomFeed()))
/*     */               {
/* 482 */                 SimpleXMLParserDocumentAttribute srcAtt = child.getAttribute("src");
/*     */                 
/* 484 */                 String src = srcAtt == null ? null : srcAtt.getValue();
/*     */                 
/* 486 */                 if (src != null)
/*     */                 {
/* 488 */                   boolean is_dl_link = false;
/*     */                   
/* 490 */                   SimpleXMLParserDocumentAttribute typeAtt = child.getAttribute("type");
/*     */                   
/* 492 */                   if ((typeAtt != null) && (typeAtt.getValue().equalsIgnoreCase("application/x-bittorrent")))
/*     */                   {
/* 494 */                     is_dl_link = true;
/*     */                   }
/*     */                   
/* 497 */                   if (!is_dl_link)
/*     */                   {
/* 499 */                     is_dl_link = src.toLowerCase().contains(".torrent");
/*     */                   }
/*     */                   
/* 502 */                   if (is_dl_link) {
/*     */                     try
/*     */                     {
/* 505 */                       new URL(src);
/*     */                       
/* 507 */                       result.setTorrentLink(src);
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/*     */                 }
/*     */               }
/* 513 */               else if (lc_full_child_name.equals("vuze:size"))
/*     */               {
/* 515 */                 result.setSizeFromHTML(value);
/*     */               }
/* 517 */               else if (lc_full_child_name.equals("vuze:seeds"))
/*     */               {
/* 519 */                 got_seeds_peers = true;
/*     */                 
/* 521 */                 result.setNbSeedsFromHTML(value);
/*     */               }
/* 523 */               else if (lc_full_child_name.equals("vuze:superseeds"))
/*     */               {
/* 525 */                 got_seeds_peers = true;
/*     */                 
/* 527 */                 result.setNbSuperSeedsFromHTML(value);
/*     */               }
/* 529 */               else if (lc_full_child_name.equals("vuze:peers"))
/*     */               {
/* 531 */                 got_seeds_peers = true;
/*     */                 
/* 533 */                 result.setNbPeersFromHTML(value);
/*     */               }
/* 535 */               else if (lc_full_child_name.equals("vuze:rank"))
/*     */               {
/* 537 */                 result.setRankFromHTML(value);
/*     */               }
/* 539 */               else if (lc_full_child_name.equals("vuze:contenttype"))
/*     */               {
/* 541 */                 String type = value.toLowerCase();
/*     */                 
/* 543 */                 if (type.startsWith("video"))
/*     */                 {
/* 545 */                   type = "video";
/*     */                 }
/* 547 */                 else if (type.startsWith("audio"))
/*     */                 {
/* 549 */                   type = "audio";
/*     */                 }
/* 551 */                 else if (type.startsWith("games"))
/*     */                 {
/* 553 */                   type = "game";
/*     */                 }
/*     */                 
/* 556 */                 result.setContentType(type);
/*     */               }
/* 558 */               else if (lc_full_child_name.equals("vuze:downloadurl"))
/*     */               {
/* 560 */                 result.setTorrentLink(value);
/*     */               }
/* 562 */               else if (lc_full_child_name.equals("vuze:playurl"))
/*     */               {
/* 564 */                 result.setPlayLink(value);
/*     */               }
/* 566 */               else if (lc_full_child_name.equals("vuze:drmkey"))
/*     */               {
/* 568 */                 result.setDrmKey(value);
/*     */               }
/* 570 */               else if (lc_full_child_name.equals("vuze:assethash"))
/*     */               {
/* 572 */                 result.setHash(value);
/*     */               }
/* 574 */               else if ((lc_child_name.equals("seeds")) || (lc_child_name.equals("seeders")))
/*     */               {
/*     */                 try {
/* 577 */                   item_seeds = Integer.parseInt(value);
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/* 582 */               else if ((lc_child_name.equals("peers")) || (lc_child_name.equals("leechers")))
/*     */               {
/*     */                 try {
/* 585 */                   item_peers = Integer.parseInt(value);
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/* 590 */               else if ((lc_child_name.equals("infohash")) || (lc_child_name.equals("info_hash")))
/*     */               {
/* 592 */                 item_hash = value;
/*     */               }
/* 594 */               else if (lc_child_name.equals("magneturi"))
/*     */               {
/* 596 */                 item_magnet = value;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 601 */           if (!got_seeds_peers)
/*     */           {
/* 603 */             if ((item_peers >= 0) && (item_seeds >= 0))
/*     */             {
/* 605 */               result.setNbSeedsFromHTML(String.valueOf(item_seeds));
/* 606 */               result.setNbPeersFromHTML(String.valueOf(item_peers));
/*     */               
/* 608 */               got_seeds_peers = true;
/*     */             }
/*     */           }
/*     */           
/* 612 */           if (!got_seeds_peers) {
/*     */             try
/*     */             {
/* 615 */               SimpleXMLParserDocumentNode desc_node = node.getChild("description");
/*     */               
/* 617 */               if (desc_node != null)
/*     */               {
/* 619 */                 String desc = desc_node.getValue().trim();
/*     */                 
/*     */ 
/*     */ 
/* 623 */                 desc = desc.replaceAll("\\(s\\)", "s");
/*     */                 
/* 625 */                 desc = desc.replaceAll("seeders", "seeds");
/*     */                 
/* 627 */                 Matcher m = this.seed_leecher_pat.matcher(desc);
/*     */                 
/* 629 */                 while (m.find())
/*     */                 {
/* 631 */                   String num = m.group(1);
/*     */                   
/* 633 */                   String type = m.group(2);
/*     */                   
/* 635 */                   if (type.toLowerCase().charAt(0) == 's')
/*     */                   {
/* 637 */                     result.setNbSeedsFromHTML(num);
/*     */                   }
/*     */                   else
/*     */                   {
/* 641 */                     result.setNbPeersFromHTML(num);
/*     */                   }
/*     */                 }
/*     */                 
/* 645 */                 m = this.size_pat.matcher(desc);
/*     */                 
/* 647 */                 if (m.find())
/*     */                 {
/* 649 */                   desc_size = m.group(1) + " " + m.group(2);
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 660 */             SimpleXMLParserDocumentNode torrent_node = node.getChild("torrent");
/*     */             
/* 662 */             if (torrent_node != null)
/*     */             {
/* 664 */               if (result.getSize() <= 0L)
/*     */               {
/* 666 */                 SimpleXMLParserDocumentNode n = torrent_node.getChild("contentLength");
/*     */                 
/* 668 */                 if (n != null) {
/*     */                   try
/*     */                   {
/* 671 */                     long l = Long.parseLong(n.getValue().trim());
/*     */                     
/* 673 */                     result.setSizeFromHTML(l + " B");
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 681 */               String dlink = result.getDownloadLink();
/*     */               
/* 683 */               if ((dlink == null) || (dlink.length() == 0))
/*     */               {
/* 685 */                 SimpleXMLParserDocumentNode n = torrent_node.getChild("magnetURI");
/*     */                 
/* 687 */                 if (n != null)
/*     */                 {
/* 689 */                   dlink = n.getValue().trim();
/*     */                   
/* 691 */                   result.setTorrentLink(dlink);
/*     */                 }
/*     */               }
/*     */               
/* 695 */               String hash = result.getHash();
/*     */               
/* 697 */               if ((hash == null) || (hash.length() == 0))
/*     */               {
/* 699 */                 SimpleXMLParserDocumentNode n = torrent_node.getChild("infoHash");
/*     */                 
/* 701 */                 if (n != null)
/*     */                 {
/* 703 */                   String h = n.getValue().trim();
/*     */                   
/* 705 */                   result.setHash(h);
/*     */                   
/* 707 */                   if ((dlink == null) || (dlink.length() == 0))
/*     */                   {
/* 709 */                     String uri = UrlUtils.normaliseMagnetURI(h);
/*     */                     
/* 711 */                     if (uri != null)
/*     */                     {
/* 713 */                       result.setTorrentLink(uri);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 719 */               SimpleXMLParserDocumentNode trackers_node = torrent_node.getChild("trackers");
/*     */               
/* 721 */               if ((trackers_node != null) && (!got_seeds_peers))
/*     */               {
/* 723 */                 SimpleXMLParserDocumentNode[] groups = trackers_node.getChildren();
/*     */                 
/* 725 */                 int max_total = -1;
/*     */                 
/* 727 */                 int best_seeds = 0;
/* 728 */                 int best_leechers = 0;
/*     */                 
/* 730 */                 for (SimpleXMLParserDocumentNode group : groups)
/*     */                 {
/* 732 */                   SimpleXMLParserDocumentNode[] g_kids = group.getChildren();
/*     */                   
/* 734 */                   for (SimpleXMLParserDocumentNode t : g_kids)
/*     */                   {
/* 736 */                     if (t.getName().equalsIgnoreCase("tracker"))
/*     */                     {
/* 738 */                       SimpleXMLParserDocumentAttribute a_seeds = t.getAttribute("seeds");
/* 739 */                       SimpleXMLParserDocumentAttribute a_leechers = t.getAttribute("peers");
/*     */                       
/* 741 */                       int seeds = a_seeds == null ? -1 : Integer.parseInt(a_seeds.getValue().trim());
/* 742 */                       int leechers = a_leechers == null ? -1 : Integer.parseInt(a_leechers.getValue().trim());
/*     */                       
/* 744 */                       int total = seeds + leechers;
/*     */                       
/* 746 */                       if (total > max_total)
/*     */                       {
/* 748 */                         max_total = total;
/*     */                         
/* 750 */                         best_seeds = seeds;
/* 751 */                         best_leechers = leechers;
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 
/* 757 */                 if (max_total >= 0)
/*     */                 {
/* 759 */                   result.setNbSeedsFromHTML(String.valueOf(Math.max(0, best_seeds)));
/* 760 */                   result.setNbPeersFromHTML(String.valueOf(Math.max(0, best_leechers)));
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 766 */             e.printStackTrace();
/*     */           }
/*     */           
/* 769 */           if ((item_hash != null) && (result.getHash() == null))
/*     */           {
/* 771 */             result.setHash(item_hash);
/*     */           }
/*     */           
/* 774 */           if (item_magnet != null)
/*     */           {
/* 776 */             String existing = result.getTorrentLinkRaw();
/*     */             
/* 778 */             if ((existing == null) || (existing.length() == 0))
/*     */             {
/* 780 */               result.setTorrentLink(item_magnet);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 786 */           String dlink = result.getDownloadLink();
/*     */           
/* 788 */           if ((dlink == null) || (dlink.length() == 0))
/*     */           {
/* 790 */             String name = result.getName();
/*     */             
/* 792 */             if (name != null)
/*     */             {
/* 794 */               String magnet = UrlUtils.parseTextForMagnets(name);
/*     */               
/* 796 */               if (magnet != null)
/*     */               {
/* 798 */                 result.setTorrentLink(magnet);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 803 */           dlink = result.getDownloadLink();
/*     */           
/* 805 */           if ((dlink == null) || (dlink.length() == 0))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 810 */             result.setTorrentLink(result.getCDPLink());
/*     */           }
/*     */           
/* 813 */           if (result.getSize() <= 0L)
/*     */           {
/* 815 */             if (desc_size != null)
/*     */             {
/* 817 */               result.setSizeFromHTML(desc_size);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 822 */           if (result.getHash() == null)
/*     */           {
/* 824 */             if (dlink != null)
/*     */             {
/* 826 */               String mag = UrlUtils.parseTextForMagnets(dlink);
/*     */               
/* 828 */               if (mag == null)
/*     */               {
/* 830 */                 String tlink = result.getTorrentLinkRaw();
/*     */                 
/* 832 */                 if (tlink != null)
/*     */                 {
/* 834 */                   mag = UrlUtils.parseTextForMagnets(tlink);
/*     */                 }
/*     */               }
/*     */               
/* 838 */               if (mag != null)
/*     */               {
/* 840 */                 byte[] hash = UrlUtils.getHashFromMagnetURI(mag);
/*     */                 
/* 842 */                 if (hash != null)
/*     */                 {
/* 844 */                   result.setHash(ByteFormatter.encodeString(hash));
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 849 */           results.add(result);
/*     */           
/* 851 */           if ((absolute_max_matches >= 0) && (results.size() == absolute_max_matches)) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 858 */       Result[] res = (Result[])results.toArray(new Result[results.size()]);
/*     */       
/* 860 */       debugLog("success: found " + res.length + " results");
/*     */       
/* 862 */       return res;
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 867 */       debugLog("failed: " + Debug.getNestedExceptionMessageAndStack(e));
/*     */       
/* 869 */       if ((e instanceof SearchException))
/*     */       {
/* 871 */         throw ((SearchException)e);
/*     */       }
/*     */       
/* 874 */       throw new SearchException("RSS matching failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean linkIsToTorrent(URL url)
/*     */   {
/*     */     try
/*     */     {
/* 883 */       HttpURLConnection con = (HttpURLConnection)url.openConnection();
/*     */       
/* 885 */       con.setRequestMethod("HEAD");
/*     */       
/* 887 */       con.setConnectTimeout(10000);
/*     */       
/* 889 */       con.setReadTimeout(10000);
/*     */       
/* 891 */       String content_type = con.getContentType();
/*     */       
/* 893 */       if (content_type != null)
/*     */       {
/* 895 */         log("Testing link " + url + " to see if torrent link -> content type=" + content_type);
/*     */         
/* 897 */         if (content_type.equalsIgnoreCase("application/x-bittorrent"))
/*     */         {
/* 899 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 903 */       return false;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 907 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/web/rss/RSSEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */