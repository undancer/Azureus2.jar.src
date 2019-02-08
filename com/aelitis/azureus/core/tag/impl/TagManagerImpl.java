/*      */ package com.aelitis.azureus.core.tag.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin;
/*      */ import com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin.Provider;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagDownload;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureFileLocation;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureListener;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRSSFeed;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagManagerListener;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.tag.TaggableLifecycleHandler;
/*      */ import com.aelitis.azureus.core.tag.TaggableLifecycleListener;
/*      */ import com.aelitis.azureus.core.tag.TaggableResolver;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.IdentityHashSet;
/*      */ import com.aelitis.azureus.core.util.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.util.MapUtils;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.core3.xml.util.XMLEscapeWriter;
/*      */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*      */ import org.gudy.azureus2.plugins.utils.ScriptProvider;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.pluginsimpl.PluginUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ public class TagManagerImpl implements TagManager, DownloadCompletionListener, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final String CONFIG_FILE = "tag.config";
/*      */   private static final int CU_TAG_CREATE = 1;
/*      */   private static final int CU_TAG_CHANGE = 2;
/*      */   private static final int CU_TAG_CONTENTS = 3;
/*      */   private static final int CU_TAG_REMOVE = 4;
/*  100 */   private static final boolean enabled = COConfigurationManager.getBooleanParameter("tagmanager.enable", true);
/*      */   
/*      */   private static TagManagerImpl singleton;
/*      */   
/*      */ 
/*      */   public static synchronized TagManagerImpl getSingleton()
/*      */   {
/*  107 */     if (singleton == null)
/*      */     {
/*  109 */       singleton = new TagManagerImpl();
/*      */       
/*  111 */       singleton.init();
/*      */     }
/*      */     
/*  114 */     return singleton;
/*      */   }
/*      */   
/*  117 */   final CopyOnWriteList<TagTypeBase> tag_types = new CopyOnWriteList();
/*      */   
/*  119 */   private final Map<Integer, TagType> tag_type_map = new HashMap();
/*      */   
/*      */   private static final String RSS_PROVIDER = "tags";
/*      */   
/*  123 */   final Set<TagBase> rss_tags = new HashSet();
/*      */   
/*  125 */   final Set<org.gudy.azureus2.core3.download.DownloadManager> active_copy_on_complete = new IdentityHashSet();
/*      */   
/*  127 */   private final RSSGeneratorPlugin.Provider rss_generator = new RSSGeneratorPlugin.Provider()
/*      */   {
/*      */ 
/*      */     public boolean isEnabled()
/*      */     {
/*      */ 
/*  133 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*      */       throws IOException
/*      */     {
/*  143 */       URL url = request.getAbsoluteURL();
/*      */       
/*  145 */       String path = url.getPath();
/*      */       
/*  147 */       String query = url.getQuery();
/*      */       
/*  149 */       if (query != null)
/*      */       {
/*  151 */         path = path + "?" + query;
/*      */       }
/*      */       
/*  154 */       int pos = path.indexOf('?');
/*      */       
/*  156 */       if (pos != -1)
/*      */       {
/*  158 */         String args = path.substring(pos + 1);
/*      */         
/*  160 */         path = path.substring(0, pos);
/*      */         
/*  162 */         if (path.endsWith("GetTorrent"))
/*      */         {
/*  164 */           String[] bits = args.split("&");
/*      */           
/*  166 */           for (String bit : bits)
/*      */           {
/*  168 */             String[] temp = bit.split("=");
/*      */             
/*  170 */             if (temp.length == 2)
/*      */             {
/*  172 */               if (temp[0].equals("hash")) {
/*      */                 try
/*      */                 {
/*  175 */                   Download download = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getDownloadManager().getDownload(Base32.decode(temp[1]));
/*      */                   
/*  177 */                   Torrent torrent = download.getTorrent();
/*      */                   
/*  179 */                   torrent = torrent.getClone();
/*      */                   
/*  181 */                   torrent = torrent.removeAdditionalProperties();
/*      */                   
/*  183 */                   response.getOutputStream().write(torrent.writeToBEncodedData());
/*      */                   
/*  185 */                   response.setContentType("application/x-bittorrent");
/*      */                   
/*  187 */                   return true;
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  196 */           response.setReplyStatus(404);
/*      */           
/*  198 */           return true;
/*      */         }
/*  200 */         if (path.endsWith("GetThumbnail"))
/*      */         {
/*  202 */           String[] bits = args.split("&");
/*      */           
/*  204 */           for (String bit : bits)
/*      */           {
/*  206 */             String[] temp = bit.split("=");
/*      */             
/*  208 */             if (temp.length == 2)
/*      */             {
/*  210 */               if (temp[0].equals("hash")) {
/*      */                 try
/*      */                 {
/*  213 */                   Download download = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getDownloadManager().getDownload(Base32.decode(temp[1]));
/*      */                   
/*  215 */                   org.gudy.azureus2.core3.download.DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*      */                   
/*  217 */                   TOTorrent torrent = core_download.getTorrent();
/*      */                   
/*  219 */                   byte[] thumb = PlatformTorrentUtils.getContentThumbnail(torrent);
/*      */                   
/*  221 */                   if (thumb != null)
/*      */                   {
/*  223 */                     response.getOutputStream().write(thumb);
/*      */                     
/*  225 */                     String thumb_type = PlatformTorrentUtils.getContentThumbnailType(torrent);
/*      */                     
/*  227 */                     if ((thumb_type == null) || (thumb_type.length() == 0))
/*      */                     {
/*  229 */                       thumb_type = "image/jpeg";
/*      */                     }
/*      */                     
/*  232 */                     response.setContentType(thumb_type);
/*      */                     
/*  234 */                     return true;
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  243 */           response.setReplyStatus(404);
/*      */           
/*  245 */           return true;
/*      */         }
/*      */       }
/*      */       
/*  249 */       path = path.substring("tags".length() + 1);
/*      */       
/*  251 */       XMLEscapeWriter pw = new XMLEscapeWriter(new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8")));
/*      */       
/*  253 */       pw.setEnabled(false);
/*      */       
/*  255 */       if (path.length() <= 1)
/*      */       {
/*  257 */         response.setContentType("text/html; charset=UTF-8");
/*      */         
/*  259 */         pw.println("<HTML><HEAD><TITLE>Vuze Tag Feeds</TITLE></HEAD><BODY>");
/*      */         
/*  261 */         Map<String, String> lines = new TreeMap();
/*      */         
/*      */         List<TagBase> tags;
/*      */         
/*  265 */         synchronized (TagManagerImpl.this.rss_tags)
/*      */         {
/*  267 */           tags = new ArrayList(TagManagerImpl.this.rss_tags);
/*      */         }
/*      */         
/*  270 */         for (TagBase t : tags)
/*      */         {
/*  272 */           if ((t instanceof TagDownload))
/*      */           {
/*  274 */             if (((TagFeatureRSSFeed)t).isTagRSSFeedEnabled())
/*      */             {
/*  276 */               String name = t.getTagName(true);
/*      */               
/*  278 */               String tag_url = "tags/" + t.getTagType().getTagType() + "-" + t.getTagID();
/*      */               
/*  280 */               lines.put(name, "<LI><A href=\"" + tag_url + "\">" + name + "</A>&nbsp;&nbsp;-&nbsp;&nbsp;<font size=\"-1\"><a href=\"" + tag_url + "?format=html\">html</a></font></LI>");
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  285 */         for (String line : lines.values())
/*      */         {
/*  287 */           pw.println(line);
/*      */         }
/*      */         
/*  290 */         pw.println("</BODY></HTML>");
/*      */       }
/*      */       else
/*      */       {
/*  294 */         String tag_id = path.substring(1);
/*      */         
/*  296 */         String[] bits = tag_id.split("-");
/*      */         
/*  298 */         int tt_id = Integer.parseInt(bits[0]);
/*  299 */         int t_id = Integer.parseInt(bits[1]);
/*      */         
/*  301 */         TagDownload tag = null;
/*      */         
/*  303 */         synchronized (TagManagerImpl.this.rss_tags)
/*      */         {
/*  305 */           for (TagBase t : TagManagerImpl.this.rss_tags)
/*      */           {
/*  307 */             if ((t.getTagType().getTagType() == tt_id) && (t.getTagID() == t_id))
/*      */             {
/*  309 */               if ((t instanceof TagDownload))
/*      */               {
/*  311 */                 tag = (TagDownload)t;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  317 */         if (tag == null)
/*      */         {
/*  319 */           response.setReplyStatus(404);
/*      */           
/*  321 */           return true;
/*      */         }
/*      */         
/*  324 */         boolean enable_low_noise = RSSGeneratorPlugin.getSingleton().isLowNoiseEnabled();
/*      */         
/*      */ 
/*  327 */         Set<org.gudy.azureus2.core3.download.DownloadManager> dms = tag.getTaggedDownloads();
/*      */         
/*  329 */         List<Download> downloads = new ArrayList(dms.size());
/*      */         
/*  331 */         long dl_marker = 0L;
/*      */         
/*  333 */         for (org.gudy.azureus2.core3.download.DownloadManager dm : dms)
/*      */         {
/*  335 */           TOTorrent torrent = dm.getTorrent();
/*      */           
/*  337 */           if (torrent != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  342 */             DownloadManagerState state = dm.getDownloadState();
/*      */             
/*  344 */             if ((!state.getFlag(512L)) && (
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  349 */               (enable_low_noise) || 
/*      */               
/*  351 */               (!state.getFlag(16L))))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  357 */               if (!TorrentUtils.isReallyPrivate(torrent))
/*      */               {
/*  359 */                 dl_marker += dm.getDownloadState().getLongParameter("stats.download.added.time");
/*      */                 
/*  361 */                 downloads.add(PluginCoreUtils.wrap(dm));
/*      */               } }
/*      */           }
/*      */         }
/*  365 */         if (url.toExternalForm().contains("format=html"))
/*      */         {
/*  367 */           String host = (String)request.getHeaders().get("host");
/*      */           
/*  369 */           if (host != null)
/*      */           {
/*  371 */             int c_pos = host.indexOf(':');
/*      */             
/*  373 */             if (c_pos != -1)
/*      */             {
/*  375 */               host = host.substring(0, c_pos);
/*      */             }
/*      */           }
/*      */           else {
/*  379 */             host = "127.0.0.1";
/*      */           }
/*      */           
/*  382 */           response.setContentType("text/html; charset=UTF-8");
/*      */           
/*  384 */           pw.println("<HTML><HEAD><TITLE>Tag: " + escape(tag.getTagName(true)) + "</TITLE></HEAD><BODY>");
/*      */           
/*  386 */           PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/*      */           
/*  388 */           PluginInterface pi = pm.getPluginInterfaceByID("azupnpav", true);
/*      */           
/*  390 */           if (pi == null)
/*      */           {
/*  392 */             pw.println("UPnP Media Server plugin not found");
/*      */           }
/*      */           else
/*      */           {
/*  396 */             for (int i = 0; i < downloads.size(); i++)
/*      */             {
/*  398 */               Download download = (Download)downloads.get(i);
/*      */               
/*  400 */               org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[] files = download.getDiskManagerFileInfo();
/*      */               
/*  402 */               for (org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file : files)
/*      */               {
/*  404 */                 File target_file = file.getFile(true);
/*      */                 
/*  406 */                 if (target_file.exists())
/*      */                 {
/*      */ 
/*      */                   try
/*      */                   {
/*      */ 
/*  412 */                     URL stream_url = new URL((String)pi.getIPC().invoke("getContentURL", new Object[] { file }));
/*      */                     
/*  414 */                     if (stream_url != null)
/*      */                     {
/*  416 */                       stream_url = UrlUtils.setHost(stream_url, host);
/*      */                       
/*  418 */                       String url_ext = stream_url.toExternalForm();
/*      */                       
/*  420 */                       pw.println("<p>");
/*      */                       
/*  422 */                       pw.println("<a href=\"" + url_ext + "\">" + escape(target_file.getName()) + "</a>");
/*      */                       
/*  424 */                       url_ext = url_ext + (url_ext.indexOf('?') == -1 ? "?" : "&");
/*      */                       
/*  426 */                       url_ext = url_ext + "action=download";
/*      */                       
/*  428 */                       pw.println("&nbsp;&nbsp;-&nbsp;&nbsp;<font size=\"-1\"><a href=\"" + url_ext + "\">save</a></font>");
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e) {
/*  432 */                     e.printStackTrace();
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*  438 */           pw.println("</BODY></HTML>");
/*      */         }
/*      */         else
/*      */         {
/*  442 */           String config_key = "tag.rss.config." + tt_id + "." + t_id;
/*      */           
/*  444 */           long old_marker = COConfigurationManager.getLongParameter(config_key + ".marker", 0L);
/*      */           
/*  446 */           long last_modified = COConfigurationManager.getLongParameter(config_key + ".last_mod", 0L);
/*      */           
/*  448 */           long now = SystemTime.getCurrentTime();
/*      */           
/*  450 */           if (old_marker == dl_marker)
/*      */           {
/*  452 */             if (last_modified == 0L)
/*      */             {
/*  454 */               last_modified = now;
/*      */             }
/*      */           }
/*      */           else {
/*  458 */             COConfigurationManager.setParameter(config_key + ".marker", dl_marker);
/*      */             
/*  460 */             last_modified = now;
/*      */           }
/*      */           
/*  463 */           if (last_modified == now)
/*      */           {
/*  465 */             COConfigurationManager.setParameter(config_key + ".last_mod", last_modified);
/*      */           }
/*      */           
/*  468 */           response.setContentType("application/xml; charset=UTF-8");
/*      */           
/*  470 */           pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
/*      */           
/*  472 */           pw.println("<rss version=\"2.0\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:vuze=\"http://www.vuze.com\">");
/*      */           
/*  474 */           pw.println("<channel>");
/*      */           
/*  476 */           pw.println("<title>" + escape(tag.getTagName(true)) + "</title>");
/*      */           
/*  478 */           Collections.sort(downloads, new Comparator()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public int compare(Download d1, Download d2)
/*      */             {
/*      */ 
/*      */ 
/*  487 */               long added1 = TagManagerImpl.1.this.getAddedTime(d1) / 1000L;
/*  488 */               long added2 = TagManagerImpl.1.this.getAddedTime(d2) / 1000L;
/*      */               
/*  490 */               return (int)(added2 - added1);
/*      */             }
/*      */             
/*      */ 
/*  494 */           });
/*  495 */           pw.println("<pubDate>" + TimeFormatter.getHTTPDate(last_modified) + "</pubDate>");
/*      */           
/*  497 */           for (int i = 0; i < downloads.size(); i++)
/*      */           {
/*  499 */             Download download = (Download)downloads.get(i);
/*      */             
/*  501 */             org.gudy.azureus2.core3.download.DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*      */             
/*  503 */             Torrent torrent = download.getTorrent();
/*  504 */             TOTorrent to_torrent = core_download.getTorrent();
/*      */             
/*  506 */             byte[] hash = torrent.getHash();
/*      */             
/*  508 */             String hash_str = Base32.encode(hash);
/*      */             
/*  510 */             pw.println("<item>");
/*      */             
/*  512 */             pw.println("<title>" + escape(download.getName()) + "</title>");
/*      */             
/*  514 */             String desc = PlatformTorrentUtils.getContentDescription(to_torrent);
/*      */             
/*  516 */             if ((desc != null) && (desc.length() > 0))
/*      */             {
/*  518 */               desc = desc.replaceAll("\r\n", "<br>");
/*  519 */               desc = desc.replaceAll("\n", "<br>");
/*  520 */               desc = desc.replaceAll("\t", "    ");
/*      */               
/*  522 */               pw.println("<description>" + escape(desc) + "</description>");
/*      */             }
/*      */             
/*  525 */             pw.println("<guid>" + hash_str + "</guid>");
/*      */             
/*  527 */             String magnet_uri = UrlUtils.getMagnetURI(download);
/*      */             
/*  529 */             String obtained_from = TorrentUtils.getObtainedFrom(core_download.getTorrent());
/*      */             
/*  531 */             String[] dl_nets = core_download.getDownloadState().getNetworks();
/*      */             
/*  533 */             boolean added_fl = false;
/*      */             
/*  535 */             if (obtained_from != null) {
/*      */               try
/*      */               {
/*  538 */                 URL ou = new URL(obtained_from);
/*      */                 
/*  540 */                 if (ou.getProtocol().toLowerCase(Locale.US).startsWith("http"))
/*      */                 {
/*  542 */                   String host = ou.getHost();
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*  547 */                   String net = AENetworkClassifier.categoriseAddress(host);
/*      */                   
/*  549 */                   boolean net_ok = false;
/*      */                   
/*  551 */                   if ((dl_nets == null) || (dl_nets.length == 0))
/*      */                   {
/*  553 */                     net_ok = true;
/*      */                   }
/*      */                   else
/*      */                   {
/*  557 */                     for (String dl_net : dl_nets)
/*      */                     {
/*  559 */                       if (dl_net == net)
/*      */                       {
/*  561 */                         net_ok = true;
/*      */                         
/*  563 */                         break;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/*  568 */                   if (net_ok)
/*      */                   {
/*  570 */                     magnet_uri = magnet_uri + "&fl=" + UrlUtils.encode(ou.toExternalForm());
/*      */                     
/*  572 */                     added_fl = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  585 */             if (!added_fl)
/*      */             {
/*  587 */               String host = (String)request.getHeaders().get("host");
/*      */               
/*  589 */               if (host != null)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  594 */                 String local_fl = url.getProtocol() + "://" + host + "/" + "tags" + "/GetTorrent?hash=" + Base32.encode(torrent.getHash());
/*      */                 
/*  596 */                 magnet_uri = magnet_uri + "&fl=" + UrlUtils.encode(local_fl);
/*      */               }
/*      */             }
/*      */             
/*  600 */             magnet_uri = escape(magnet_uri);
/*      */             
/*  602 */             pw.println("<link>" + magnet_uri + "</link>");
/*      */             
/*  604 */             long added = core_download.getDownloadState().getLongParameter("stats.download.added.time");
/*      */             
/*  606 */             pw.println("<pubDate>" + TimeFormatter.getHTTPDate(added) + "</pubDate>");
/*      */             
/*  608 */             pw.println("<vuze:size>" + torrent.getSize() + "</vuze:size>");
/*  609 */             pw.println("<vuze:assethash>" + hash_str + "</vuze:assethash>");
/*      */             
/*  611 */             pw.println("<vuze:downloadurl>" + magnet_uri + "</vuze:downloadurl>");
/*      */             
/*  613 */             DownloadScrapeResult scrape = download.getLastScrapeResult();
/*      */             
/*  615 */             if ((scrape != null) && (scrape.getResponseType() == 1))
/*      */             {
/*  617 */               pw.println("<vuze:seeds>" + scrape.getSeedCount() + "</vuze:seeds>");
/*  618 */               pw.println("<vuze:peers>" + scrape.getNonSeedCount() + "</vuze:peers>");
/*      */             }
/*      */             
/*  621 */             byte[] thumb = PlatformTorrentUtils.getContentThumbnail(to_torrent);
/*      */             
/*  623 */             if (thumb != null)
/*      */             {
/*  625 */               String host = (String)request.getHeaders().get("host");
/*      */               
/*  627 */               if (host != null)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  632 */                 String thumb_url = url.getProtocol() + "://" + host + "/" + "tags" + "/GetThumbnail?hash=" + Base32.encode(torrent.getHash());
/*      */                 
/*  634 */                 pw.println("<media:thumbnail url=\"" + thumb_url + "\"/>");
/*      */               }
/*      */             }
/*      */             
/*  638 */             pw.println("</item>");
/*      */           }
/*      */           
/*  641 */           pw.println("</channel>");
/*      */           
/*  643 */           pw.println("</rss>");
/*      */         }
/*      */       }
/*      */       
/*  647 */       pw.flush();
/*      */       
/*  649 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected long getAddedTime(Download download)
/*      */     {
/*  656 */       org.gudy.azureus2.core3.download.DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*      */       
/*  658 */       return core_download.getDownloadState().getLongParameter("stats.download.added.time");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected String escape(String str)
/*      */     {
/*  665 */       return XUXmlWriter.escapeXML(str);
/*      */     }
/*      */   };
/*      */   
/*  669 */   final AsyncDispatcher async_dispatcher = new AsyncDispatcher(5000);
/*      */   
/*  671 */   private final FrequencyLimitedDispatcher dirty_dispatcher = new FrequencyLimitedDispatcher(new AERunnable()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void runSupport()
/*      */     {
/*      */ 
/*      */ 
/*  680 */       new AEThread2("tag:fld")
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*  688 */             Thread.sleep(1000L);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/*      */ 
/*      */ 
/*  694 */           TagManagerImpl.this.writeConfig();
/*      */         }
/*      */       }.start();
/*      */     }
/*  671 */   }, 30000);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map config;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private WeakReference<Map> config_ref;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean config_dirty;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  707 */   private final List<Object[]> config_change_queue = new ArrayList();
/*      */   
/*      */ 
/*  710 */   private final CopyOnWriteList<TagManagerListener> listeners = new CopyOnWriteList();
/*      */   
/*  712 */   private final CopyOnWriteList<Object[]> feature_listeners = new CopyOnWriteList();
/*      */   
/*  714 */   private final Map<Long, LifecycleHandlerImpl> lifecycle_handlers = new HashMap();
/*      */   
/*      */   private TagPropertyUntaggedHandler untagged_handler;
/*      */   
/*      */   private TagPropertyConstraintHandler constraint_handler;
/*      */   
/*      */   private boolean js_plugin_install_tried;
/*      */   
/*      */ 
/*      */   private TagManagerImpl()
/*      */   {
/*  725 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  731 */     return enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   private void init()
/*      */   {
/*  737 */     if (!enabled)
/*      */     {
/*  739 */       return;
/*      */     }
/*      */     
/*  742 */     AzureusCore azureus_core = AzureusCoreFactory.getSingleton();
/*      */     
/*  744 */     final TagPropertyTrackerHandler auto_tracker = new TagPropertyTrackerHandler(azureus_core, this);
/*      */     
/*  746 */     this.untagged_handler = new TagPropertyUntaggedHandler(azureus_core, this);
/*      */     
/*  748 */     new TagPropertyTrackerTemplateHandler(azureus_core, this);
/*      */     
/*  750 */     this.constraint_handler = new TagPropertyConstraintHandler(azureus_core, this);
/*      */     
/*  752 */     azureus_core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void started(AzureusCore core)
/*      */       {
/*      */ 
/*  759 */         core.getPluginManager().getDefaultPluginInterface().getDownloadManager().getGlobalDownloadEventNotifier().addCompletionListener(TagManagerImpl.this);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*      */       {
/*  767 */         if ((component instanceof GlobalManager))
/*      */         {
/*  769 */           GlobalManager global_manager = (GlobalManager)component;
/*      */           
/*  771 */           global_manager.addDownloadManagerInitialisationAdapter(new DownloadManagerInitialisationAdapter()
/*      */           {
/*      */ 
/*      */             public int getActions()
/*      */             {
/*      */ 
/*  777 */               return 1;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void initialised(org.gudy.azureus2.core3.download.DownloadManager manager, boolean for_seeding)
/*      */             {
/*  785 */               org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files = manager.getDiskManagerFileInfoSet().getFiles();
/*      */               
/*  787 */               for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : files)
/*      */               {
/*  789 */                 if (file.getTorrentFile().getPathComponents().length == 1)
/*      */                 {
/*  791 */                   String name = file.getTorrentFile().getRelativePath().toLowerCase(Locale.US);
/*      */                   
/*  793 */                   if ((name.equals("index.html")) || (name.equals("index.htm")))
/*      */                   {
/*  795 */                     TagType tt = TagManagerFactory.getTagManager().getTagType(3);
/*      */                     
/*  797 */                     String tag_name = "Websites";
/*      */                     
/*  799 */                     Tag tag = tt.getTag(tag_name, true);
/*      */                     try
/*      */                     {
/*  802 */                       if (tag == null)
/*      */                       {
/*  804 */                         tag = tt.createTag(tag_name, true);
/*      */                       }
/*      */                       
/*  807 */                       if (!tag.hasTaggable(manager))
/*      */                       {
/*  809 */                         tag.addTaggable(manager);
/*      */                         
/*  811 */                         tag.setDescription(MessageText.getString("tag.website.desc"));
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {
/*  815 */                       Debug.out(e);
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*  824 */           });
/*  825 */           global_manager.addDownloadManagerInitialisationAdapter(new DownloadManagerInitialisationAdapter()
/*      */           {
/*      */ 
/*      */             public int getActions()
/*      */             {
/*      */ 
/*  831 */               return 2;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void initialised(org.gudy.azureus2.core3.download.DownloadManager manager, boolean for_seeding)
/*      */             {
/*  839 */               if (for_seeding)
/*      */               {
/*  841 */                 return;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  847 */               List<Tag> auto_tags = TagManagerImpl.3.this.val$auto_tracker.getTagsForDownload(manager);
/*      */               
/*  849 */               Set<Tag> tags = new HashSet(TagManagerImpl.this.getTagsForTaggable(3, manager));
/*      */               
/*  851 */               tags.addAll(auto_tags);
/*      */               
/*  853 */               if (tags.size() == 0)
/*      */               {
/*      */ 
/*      */ 
/*  857 */                 tags.addAll(TagManagerImpl.this.untagged_handler.getUntaggedTags());
/*      */               }
/*      */               
/*  860 */               if (tags.size() > 0)
/*      */               {
/*  862 */                 List<Tag> sl_tags = new ArrayList();
/*      */                 
/*  864 */                 for (Tag tag : tags)
/*      */                 {
/*  866 */                   TagFeatureFileLocation fl = (TagFeatureFileLocation)tag;
/*      */                   
/*  868 */                   if (fl.supportsTagInitialSaveFolder())
/*      */                   {
/*  870 */                     File save_loc = fl.getTagInitialSaveFolder();
/*      */                     
/*  872 */                     if (save_loc != null)
/*      */                     {
/*  874 */                       sl_tags.add(tag);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  879 */                 if (sl_tags.size() > 0)
/*      */                 {
/*  881 */                   if (sl_tags.size() > 1)
/*      */                   {
/*  883 */                     Collections.sort(sl_tags, new Comparator()
/*      */                     {
/*      */ 
/*      */ 
/*      */                       public int compare(Tag o1, Tag o2)
/*      */                       {
/*      */ 
/*      */ 
/*  891 */                         return o1.getTagID() - o2.getTagID();
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                   
/*  896 */                   TagFeatureFileLocation tag = (TagFeatureFileLocation)sl_tags.get(0);
/*      */                   
/*  898 */                   long options = tag.getTagInitialSaveOptions();
/*      */                   
/*  900 */                   boolean set_data = (options & 1L) != 0L;
/*  901 */                   boolean set_torrent = (options & 0x2) != 0L;
/*      */                   
/*  903 */                   File new_loc = tag.getTagInitialSaveFolder();
/*      */                   
/*  905 */                   if (set_data)
/*      */                   {
/*  907 */                     File old_loc = manager.getSaveLocation();
/*      */                     
/*  909 */                     if (!new_loc.equals(old_loc))
/*      */                     {
/*  911 */                       manager.setTorrentSaveDir(new_loc.getAbsolutePath());
/*      */                     }
/*      */                   }
/*      */                   
/*  915 */                   if (set_torrent)
/*      */                   {
/*  917 */                     File old_torrent_file = new File(manager.getTorrentFileName());
/*      */                     
/*  919 */                     if (old_torrent_file.exists()) {
/*      */                       try
/*      */                       {
/*  922 */                         manager.setTorrentFile(new_loc, old_torrent_file.getName());
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/*  926 */                         Debug.out(e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void stopped(AzureusCore core)
/*      */       {
/*  941 */         TagManagerImpl.this.destroy();
/*      */       }
/*      */       
/*  944 */     });
/*  945 */     SimpleTimer.addPeriodicEvent("TM:Sync", 30000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  954 */         for (TagType tt : TagManagerImpl.this.tag_types)
/*      */         {
/*  956 */           ((TagTypeBase)tt).sync();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setProcessingEnabled(boolean enabled)
/*      */   {
/*  966 */     if (this.constraint_handler != null)
/*      */     {
/*  968 */       this.constraint_handler.setProcessingEnabled(enabled);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void onCompletion(Download d)
/*      */   {
/*  976 */     final org.gudy.azureus2.core3.download.DownloadManager manager = PluginCoreUtils.unwrap(d);
/*      */     
/*  978 */     List<Tag> tags = getTagsForTaggable(manager);
/*      */     
/*  980 */     List<Tag> cc_tags = new ArrayList();
/*      */     
/*  982 */     for (Tag tag : tags)
/*      */     {
/*  984 */       if (tag.getTagType().hasTagTypeFeature(16L))
/*      */       {
/*  986 */         TagFeatureFileLocation fl = (TagFeatureFileLocation)tag;
/*      */         
/*  988 */         if (fl.supportsTagCopyOnComplete())
/*      */         {
/*  990 */           File save_loc = fl.getTagCopyOnCompleteFolder();
/*      */           
/*  992 */           if (save_loc != null)
/*      */           {
/*  994 */             cc_tags.add(tag);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1000 */     if (cc_tags.size() > 0)
/*      */     {
/* 1002 */       if (cc_tags.size() > 1)
/*      */       {
/* 1004 */         Collections.sort(cc_tags, new Comparator()
/*      */         {
/*      */ 
/*      */ 
/*      */           public int compare(Tag o1, Tag o2)
/*      */           {
/*      */ 
/*      */ 
/* 1012 */             return o1.getTagID() - o2.getTagID();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1017 */       TagFeatureFileLocation fl = (TagFeatureFileLocation)cc_tags.get(0);
/*      */       
/* 1019 */       final File new_loc = fl.getTagCopyOnCompleteFolder();
/*      */       
/* 1021 */       long options = fl.getTagCopyOnCompleteOptions();
/*      */       
/* 1023 */       boolean copy_data = (options & 1L) != 0L;
/* 1024 */       boolean copy_torrent = (options & 0x2) != 0L;
/*      */       
/* 1026 */       if (copy_data)
/*      */       {
/* 1028 */         File old_loc = manager.getSaveLocation();
/*      */         
/* 1030 */         if (!new_loc.equals(old_loc))
/*      */         {
/*      */           boolean do_it;
/*      */           
/* 1034 */           synchronized (this.active_copy_on_complete) {
/*      */             boolean do_it;
/* 1036 */             if (this.active_copy_on_complete.contains(manager))
/*      */             {
/* 1038 */               do_it = false;
/*      */             }
/*      */             else
/*      */             {
/* 1042 */               this.active_copy_on_complete.add(manager);
/*      */               
/* 1044 */               do_it = true;
/*      */             }
/*      */           }
/*      */           
/* 1048 */           if (do_it)
/*      */           {
/* 1050 */             new AEThread2("tm:copy")
/*      */             {
/*      */               public void run()
/*      */               {
/*      */                 try
/*      */                 {
/* 1056 */                   long stopped_and_incomplete_start = 0L;
/* 1057 */                   long looks_good_start = 0L;
/*      */                   
/*      */                   for (;;)
/*      */                   {
/* 1061 */                     if (manager.isDestroyed())
/*      */                     {
/* 1063 */                       throw new Exception("Download has been removed");
/*      */                     }
/*      */                     
/* 1066 */                     DiskManager dm = manager.getDiskManager();
/*      */                     
/* 1068 */                     if (dm == null)
/*      */                     {
/* 1070 */                       looks_good_start = 0L;
/*      */                       
/* 1072 */                       if (manager.getAssumedComplete())
/*      */                         break;
/* 1074 */                       long now = SystemTime.getMonotonousTime();
/*      */                       
/* 1076 */                       if (stopped_and_incomplete_start == 0L)
/*      */                       {
/* 1078 */                         stopped_and_incomplete_start = now;
/*      */                       }
/* 1080 */                       else if (now - stopped_and_incomplete_start > 30000L)
/*      */                       {
/* 1082 */                         throw new Exception("Download is stopped and incomplete");
/*      */                       }
/*      */                       
/*      */ 
/*      */                     }
/*      */                     else
/*      */                     {
/*      */ 
/* 1090 */                       stopped_and_incomplete_start = 0L;
/*      */                       
/* 1092 */                       if (manager.getAssumedComplete())
/*      */                       {
/* 1094 */                         if ((dm.getMoveProgress() == -1) && (dm.getCompleteRecheckStatus() == -1))
/*      */                         {
/* 1096 */                           long now = SystemTime.getMonotonousTime();
/*      */                           
/* 1098 */                           if (looks_good_start == 0L)
/*      */                           {
/* 1100 */                             looks_good_start = now;
/*      */                           } else {
/* 1102 */                             if (now - looks_good_start > 5000L) {
/*      */                               break;
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                       else {
/* 1109 */                         looks_good_start = 0L;
/*      */                       }
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/* 1115 */                     Thread.sleep(1000L);
/*      */                   }
/*      */                   
/* 1118 */                   manager.copyDataFiles(new_loc);
/*      */                   
/* 1120 */                   Logger.logTextResource(new LogAlert(manager, true, 0, "alert.copy.on.comp.done"), new String[] { manager.getDisplayName(), new_loc.toString() });
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 1130 */                   Logger.logTextResource(new LogAlert(manager, true, 3, "alert.copy.on.comp.fail"), new String[] { manager.getDisplayName(), new_loc.toString(), Debug.getNestedExceptionMessage(e) });
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/*      */                 finally
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 1140 */                   synchronized (TagManagerImpl.this.active_copy_on_complete)
/*      */                   {
/* 1142 */                     TagManagerImpl.this.active_copy_on_complete.remove(manager);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }.start();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1152 */       if (copy_torrent)
/*      */       {
/* 1154 */         File old_file = new File(manager.getTorrentFileName());
/*      */         
/* 1156 */         if (old_file.exists())
/*      */         {
/* 1158 */           File new_file = new File(new_loc, old_file.getName());
/*      */           
/* 1160 */           FileUtil.copyFile(old_file, new_file);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object evalScript(Tag tag, String script, org.gudy.azureus2.core3.download.DownloadManager dm, String intent_key)
/*      */   {
/* 1173 */     String script_type = "";
/*      */     
/* 1175 */     if ((script.length() >= 10) && (script.substring(0, 10).toLowerCase(Locale.US).startsWith("javascript")))
/*      */     {
/* 1177 */       int p1 = script.indexOf('(');
/*      */       
/* 1179 */       int p2 = script.lastIndexOf(')');
/*      */       
/* 1181 */       if ((p1 != -1) && (p2 != -1))
/*      */       {
/* 1183 */         script = script.substring(p1 + 1, p2).trim();
/*      */         
/* 1185 */         if ((script.startsWith("\"")) && (script.endsWith("\"")))
/*      */         {
/* 1187 */           script = script.substring(1, script.length() - 1);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1192 */         script = script.replaceAll("\\\\\"", "\"");
/*      */         
/* 1194 */         script_type = "javascript";
/*      */       }
/*      */     }
/*      */     
/* 1198 */     if (script_type == "")
/*      */     {
/* 1200 */       Debug.out("Unrecognised script type: " + script);
/*      */       
/* 1202 */       return null;
/*      */     }
/*      */     
/* 1205 */     boolean provider_found = false;
/*      */     
/* 1207 */     List<ScriptProvider> providers = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getUtilities().getScriptProviders();
/*      */     
/* 1209 */     for (ScriptProvider p : providers)
/*      */     {
/* 1211 */       if (p.getScriptType() == script_type)
/*      */       {
/* 1213 */         provider_found = true;
/*      */         
/* 1215 */         Download plugin_dm = PluginCoreUtils.wrap(dm);
/*      */         
/* 1217 */         if (plugin_dm == null)
/*      */         {
/* 1219 */           return null;
/*      */         }
/*      */         
/* 1222 */         Map<String, Object> bindings = new HashMap();
/*      */         
/*      */ 
/* 1225 */         String dm_name = dm.getDisplayName();
/*      */         
/* 1227 */         if (dm_name.length() > 32)
/*      */         {
/* 1229 */           dm_name = dm_name.substring(0, 29) + "...";
/*      */         }
/*      */         
/* 1232 */         String intent = intent_key + "(\"" + tag.getTagName() + "\",\"" + dm_name + "\")";
/*      */         
/* 1234 */         bindings.put("intent", intent);
/*      */         
/* 1236 */         bindings.put("download", plugin_dm);
/*      */         
/* 1238 */         bindings.put("tag", tag);
/*      */         try
/*      */         {
/* 1241 */           return p.eval(script, bindings);
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*      */ 
/* 1247 */           Debug.out(e);
/*      */           
/* 1249 */           return null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1254 */     if (!provider_found)
/*      */     {
/* 1256 */       if (!this.js_plugin_install_tried)
/*      */       {
/* 1258 */         this.js_plugin_install_tried = true;
/*      */         
/* 1260 */         PluginUtils.installJavaScriptPlugin();
/*      */       }
/*      */     }
/*      */     
/* 1264 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void resolverInitialized(TaggableResolver resolver)
/*      */   {
/* 1271 */     TagTypeDownloadManual ttdm = new TagTypeDownloadManual(resolver);
/*      */     
/* 1273 */     List<Tag> tags = new ArrayList();
/*      */     
/* 1275 */     synchronized (this)
/*      */     {
/* 1277 */       Map config = getConfig();
/*      */       
/* 1279 */       Map<String, Object> tt = (Map)config.get(String.valueOf(ttdm.getTagType()));
/*      */       
/* 1281 */       if (tt != null)
/*      */       {
/* 1283 */         for (Map.Entry<String, Object> entry : tt.entrySet())
/*      */         {
/* 1285 */           String key = (String)entry.getKey();
/*      */           try
/*      */           {
/* 1288 */             if (Character.isDigit(key.charAt(0)))
/*      */             {
/* 1290 */               int tag_id = Integer.parseInt(key);
/* 1291 */               Map m = (Map)entry.getValue();
/*      */               
/* 1293 */               tags.add(ttdm.createTag(tag_id, m));
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1297 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1303 */     for (Tag tag : tags)
/*      */     {
/* 1305 */       ttdm.addTag(tag);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void removeTaggable(TaggableResolver resolver, Taggable taggable)
/*      */   {
/* 1314 */     for (TagType tt : this.tag_types)
/*      */     {
/* 1316 */       TagTypeBase ttb = (TagTypeBase)tt;
/*      */       
/* 1318 */       ttb.removeTaggable(resolver, taggable);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTagType(TagTypeBase tag_type)
/*      */   {
/* 1326 */     if (!enabled)
/*      */     {
/* 1328 */       Debug.out("Not enabled");
/*      */       
/* 1330 */       return;
/*      */     }
/*      */     
/* 1333 */     synchronized (this.tag_type_map)
/*      */     {
/* 1335 */       if (this.tag_type_map.put(Integer.valueOf(tag_type.getTagType()), tag_type) != null)
/*      */       {
/* 1337 */         Debug.out("Duplicate tag type!");
/*      */       }
/*      */     }
/*      */     
/* 1341 */     this.tag_types.add(tag_type);
/*      */     
/* 1343 */     for (TagManagerListener l : this.listeners) {
/*      */       try
/*      */       {
/* 1346 */         l.tagTypeAdded(this, tag_type);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/* 1350 */         Debug.out(t);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public TagType getTagType(int tag_type)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 855	com/aelitis/azureus/core/tag/impl/TagManagerImpl:tag_type_map	Ljava/util/Map;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 855	com/aelitis/azureus/core/tag/impl/TagManagerImpl:tag_type_map	Ljava/util/Map;
/*      */     //   11: iload_1
/*      */     //   12: invokestatic 932	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   15: invokeinterface 1014 2 0
/*      */     //   20: checkcast 433	com/aelitis/azureus/core/tag/TagType
/*      */     //   23: aload_2
/*      */     //   24: monitorexit
/*      */     //   25: areturn
/*      */     //   26: astore_3
/*      */     //   27: aload_2
/*      */     //   28: monitorexit
/*      */     //   29: aload_3
/*      */     //   30: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1359	-> byte code offset #0
/*      */     //   Java source line #1361	-> byte code offset #7
/*      */     //   Java source line #1362	-> byte code offset #26
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	31	0	this	TagManagerImpl
/*      */     //   0	31	1	tag_type	int
/*      */     //   5	23	2	Ljava/lang/Object;	Object
/*      */     //   26	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	25	26	finally
/*      */     //   26	29	26	finally
/*      */   }
/*      */   
/*      */   protected void removeTagType(TagTypeBase tag_type)
/*      */   {
/* 1369 */     synchronized (this.tag_type_map)
/*      */     {
/* 1371 */       this.tag_type_map.remove(Integer.valueOf(tag_type.getTagType()));
/*      */     }
/*      */     
/* 1374 */     this.tag_types.remove(tag_type);
/*      */     
/* 1376 */     for (TagManagerListener l : this.listeners) {
/*      */       try
/*      */       {
/* 1379 */         l.tagTypeRemoved(this, tag_type);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/* 1383 */         Debug.out(t);
/*      */       }
/*      */     }
/*      */     
/* 1387 */     removeConfig(tag_type);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<TagType> getTagTypes()
/*      */   {
/* 1393 */     return (List)this.tag_types.getList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void taggableAdded(TagType tag_type, Tag tag, Taggable tagged)
/*      */   {
/* 1405 */     int tt = tag_type.getTagType();
/*      */     try
/*      */     {
/* 1408 */       if ((tt == 3) && ((tagged instanceof org.gudy.azureus2.core3.download.DownloadManager)))
/*      */       {
/* 1410 */         TagFeatureFileLocation fl = (TagFeatureFileLocation)tag;
/*      */         
/* 1412 */         if (fl.supportsTagInitialSaveFolder())
/*      */         {
/* 1414 */           File save_loc = fl.getTagInitialSaveFolder();
/*      */           
/* 1416 */           if (save_loc != null)
/*      */           {
/* 1418 */             org.gudy.azureus2.core3.download.DownloadManager dm = (org.gudy.azureus2.core3.download.DownloadManager)tagged;
/*      */             
/* 1420 */             if (dm.getState() == 70)
/*      */             {
/* 1422 */               TOTorrent torrent = dm.getTorrent();
/*      */               
/* 1424 */               if (torrent != null)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1430 */                 if (dm.getGlobalManager().getDownloadManager(torrent.getHashWrapper()) != null)
/*      */                 {
/* 1432 */                   long options = fl.getTagInitialSaveOptions();
/*      */                   
/* 1434 */                   boolean set_data = (options & 1L) != 0L;
/* 1435 */                   boolean set_torrent = (options & 0x2) != 0L;
/*      */                   
/* 1437 */                   if (set_data)
/*      */                   {
/* 1439 */                     File existing_save_loc = dm.getSaveLocation();
/*      */                     
/* 1441 */                     if ((!existing_save_loc.equals(save_loc)) && (!existing_save_loc.exists()))
/*      */                     {
/* 1443 */                       dm.setTorrentSaveDir(save_loc.getAbsolutePath());
/*      */                     }
/*      */                   }
/*      */                   
/* 1447 */                   if (set_torrent)
/*      */                   {
/* 1449 */                     File old_torrent_file = new File(dm.getTorrentFileName());
/*      */                     
/* 1451 */                     if (old_torrent_file.exists()) {
/*      */                       try
/*      */                       {
/* 1454 */                         dm.setTorrentFile(save_loc, old_torrent_file.getName());
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/* 1458 */                         Debug.out(e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1470 */       Debug.out(e);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1476 */     if (tt == 3)
/*      */     {
/* 1478 */       synchronized (this.lifecycle_handlers)
/*      */       {
/* 1480 */         long type = tagged.getTaggableType();
/*      */         
/* 1482 */         LifecycleHandlerImpl handler = (LifecycleHandlerImpl)this.lifecycle_handlers.get(Long.valueOf(type));
/*      */         
/* 1484 */         if (handler == null)
/*      */         {
/* 1486 */           handler = new LifecycleHandlerImpl(null);
/*      */           
/* 1488 */           this.lifecycle_handlers.put(Long.valueOf(type), handler);
/*      */         }
/*      */         
/* 1491 */         handler.taggableTagged(tag_type, tag, tagged);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void taggableRemoved(TagType tag_type, Tag tag, Taggable tagged)
/*      */   {
/* 1502 */     int tt = tag_type.getTagType();
/*      */     
/*      */ 
/*      */ 
/* 1506 */     if (tt == 3)
/*      */     {
/* 1508 */       synchronized (this.lifecycle_handlers)
/*      */       {
/* 1510 */         long type = tagged.getTaggableType();
/*      */         
/* 1512 */         LifecycleHandlerImpl handler = (LifecycleHandlerImpl)this.lifecycle_handlers.get(Long.valueOf(type));
/*      */         
/* 1514 */         if (handler == null)
/*      */         {
/* 1516 */           handler = new LifecycleHandlerImpl(null);
/*      */           
/* 1518 */           this.lifecycle_handlers.put(Long.valueOf(type), handler);
/*      */         }
/*      */         
/* 1521 */         handler.taggableUntagged(tag_type, tag, tagged);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<Tag> getTagsForTaggable(Taggable taggable)
/*      */   {
/* 1530 */     Set<Tag> result = new HashSet();
/*      */     
/* 1532 */     for (TagType tt : this.tag_types)
/*      */     {
/* 1534 */       result.addAll(tt.getTagsForTaggable(taggable));
/*      */     }
/*      */     
/* 1537 */     return new ArrayList(result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<Tag> getTagsForTaggable(int tts, Taggable taggable)
/*      */   {
/* 1545 */     Set<Tag> result = new HashSet();
/*      */     
/* 1547 */     for (TagType tt : this.tag_types)
/*      */     {
/* 1549 */       if (tt.getTagType() == tts)
/*      */       {
/* 1551 */         result.addAll(tt.getTagsForTaggable(taggable));
/*      */       }
/*      */     }
/*      */     
/* 1555 */     return new ArrayList(result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Tag lookupTagByUID(long tag_uid)
/*      */   {
/* 1562 */     int tag_type_id = (int)(tag_uid >> 32 & 0xFFFFFFFF);
/*      */     
/*      */     TagType tt;
/*      */     
/* 1566 */     synchronized (this.tag_type_map)
/*      */     {
/* 1568 */       tt = (TagType)this.tag_type_map.get(Integer.valueOf(tag_type_id));
/*      */     }
/*      */     
/* 1571 */     if (tt != null)
/*      */     {
/* 1573 */       int tag_id = (int)(tag_uid & 0xFFFFFFFF);
/*      */       
/* 1575 */       return tt.getTag(tag_id);
/*      */     }
/*      */     
/* 1578 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TaggableLifecycleHandler registerTaggableResolver(TaggableResolver resolver)
/*      */   {
/* 1585 */     if (!enabled)
/*      */     {
/* 1587 */       new TaggableLifecycleHandler()
/*      */       {
/*      */         public void initialized(List<Taggable> initial_taggables) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void taggableCreated(Taggable taggable) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void taggableDestroyed(Taggable taggable) {}
/*      */       };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1612 */     long type = resolver.getResolverTaggableType();
/*      */     LifecycleHandlerImpl handler;
/* 1614 */     synchronized (this.lifecycle_handlers)
/*      */     {
/* 1616 */       handler = (LifecycleHandlerImpl)this.lifecycle_handlers.get(Long.valueOf(type));
/*      */       
/* 1618 */       if (handler == null)
/*      */       {
/* 1620 */         handler = new LifecycleHandlerImpl(null);
/*      */         
/* 1622 */         this.lifecycle_handlers.put(Long.valueOf(type), handler);
/*      */       }
/*      */       
/* 1625 */       handler.setResolver(resolver);
/*      */     }
/*      */     
/* 1628 */     return handler;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagPublicDefault(boolean pub)
/*      */   {
/* 1635 */     COConfigurationManager.setParameter("tag.manager.pub.default", pub);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getTagPublicDefault()
/*      */   {
/* 1641 */     return COConfigurationManager.getBooleanParameter("tag.manager.pub.default", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkRSSFeeds(TagBase tag, boolean enable)
/*      */   {
/* 1649 */     synchronized (this.rss_tags)
/*      */     {
/* 1651 */       if (enable)
/*      */       {
/* 1653 */         if (this.rss_tags.contains(tag))
/*      */         {
/* 1655 */           return;
/*      */         }
/*      */         
/* 1658 */         this.rss_tags.add(tag);
/*      */         
/* 1660 */         if (this.rss_tags.size() > 1)
/*      */         {
/* 1662 */           return;
/*      */         }
/*      */         
/*      */ 
/* 1666 */         RSSGeneratorPlugin.registerProvider("tags", this.rss_generator);
/*      */       }
/*      */       else
/*      */       {
/* 1670 */         this.rss_tags.remove(tag);
/*      */         
/* 1672 */         if (this.rss_tags.size() == 0)
/*      */         {
/* 1674 */           RSSGeneratorPlugin.unregisterProvider("tags");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addTagManagerListener(TagManagerListener listener, boolean fire_for_existing)
/*      */   {
/* 1685 */     this.listeners.add(listener);
/*      */     
/* 1687 */     if (fire_for_existing)
/*      */     {
/* 1689 */       for (TagType tt : this.tag_types)
/*      */       {
/* 1691 */         listener.tagTypeAdded(this, tt);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeTagManagerListener(TagManagerListener listener)
/*      */   {
/* 1700 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addTagFeatureListener(int features, TagFeatureListener listener)
/*      */   {
/* 1708 */     this.feature_listeners.add(new Object[] { Integer.valueOf(features), listener });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeTagFeatureListener(TagFeatureListener listener)
/*      */   {
/* 1715 */     for (Object[] entry : this.feature_listeners)
/*      */     {
/* 1717 */       if (entry[1] == listener)
/*      */       {
/* 1719 */         this.feature_listeners.remove(entry);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void featureChanged(Tag tag, int feature)
/*      */   {
/* 1729 */     for (Object[] entry : this.feature_listeners)
/*      */     {
/* 1731 */       if ((((Integer)entry[0]).intValue() & feature) != 0) {
/*      */         try
/*      */         {
/* 1734 */           ((TagFeatureListener)entry[1]).tagFeatureChanged(tag, feature);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1738 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addTaggableLifecycleListener(long taggable_type, TaggableLifecycleListener listener)
/*      */   {
/* 1749 */     synchronized (this.lifecycle_handlers)
/*      */     {
/* 1751 */       LifecycleHandlerImpl handler = (LifecycleHandlerImpl)this.lifecycle_handlers.get(Long.valueOf(taggable_type));
/*      */       
/* 1753 */       if (handler == null)
/*      */       {
/* 1755 */         handler = new LifecycleHandlerImpl(null);
/*      */         
/* 1757 */         this.lifecycle_handlers.put(Long.valueOf(taggable_type), handler);
/*      */       }
/*      */       
/* 1760 */       handler.addListener(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeTaggableLifecycleListener(long taggable_type, TaggableLifecycleListener listener)
/*      */   {
/* 1769 */     synchronized (this.lifecycle_handlers)
/*      */     {
/* 1771 */       LifecycleHandlerImpl handler = (LifecycleHandlerImpl)this.lifecycle_handlers.get(Long.valueOf(taggable_type));
/*      */       
/* 1773 */       if (handler != null)
/*      */       {
/* 1775 */         handler.removeListener(listener);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void tagCreated(TagWithState tag)
/*      */   {
/* 1784 */     addConfigUpdate(1, tag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void tagChanged(TagWithState tag)
/*      */   {
/* 1791 */     addConfigUpdate(2, tag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void tagRemoved(TagWithState tag)
/*      */   {
/* 1799 */     addConfigUpdate(4, tag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void tagContentsChanged(TagWithState tag)
/*      */   {
/* 1806 */     addConfigUpdate(3, tag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addConfigUpdate(int type, TagWithState tag)
/*      */   {
/* 1814 */     if (!tag.getTagType().isTagTypePersistent())
/*      */     {
/* 1816 */       return;
/*      */     }
/*      */     
/* 1819 */     if ((tag.isRemoved()) && (type != 4))
/*      */     {
/* 1821 */       return;
/*      */     }
/*      */     
/* 1824 */     synchronized (this)
/*      */     {
/* 1826 */       this.config_change_queue.add(new Object[] { Integer.valueOf(type), tag });
/*      */     }
/*      */     
/* 1829 */     setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void applyConfigUpdates(Map config)
/*      */   {
/* 1836 */     Map<TagWithState, Integer> updates = new HashMap();
/*      */     
/* 1838 */     for (Object[] update : this.config_change_queue)
/*      */     {
/* 1840 */       int type = ((Integer)update[0]).intValue();
/* 1841 */       TagWithState tag = (TagWithState)update[1];
/*      */       
/* 1843 */       if (tag.isRemoved())
/*      */       {
/* 1845 */         type = 4;
/*      */       }
/*      */       
/* 1848 */       Integer existing = (Integer)updates.get(tag);
/*      */       
/* 1850 */       if (existing == null)
/*      */       {
/* 1852 */         updates.put(tag, Integer.valueOf(type));
/*      */ 
/*      */ 
/*      */       }
/* 1856 */       else if (existing.intValue() != 4)
/*      */       {
/* 1858 */         if (type > existing.intValue())
/*      */         {
/* 1860 */           updates.put(tag, Integer.valueOf(type));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1865 */     for (Map.Entry<TagWithState, Integer> entry : updates.entrySet())
/*      */     {
/* 1867 */       TagWithState tag = (TagWithState)entry.getKey();
/* 1868 */       int type = ((Integer)entry.getValue()).intValue();
/*      */       
/* 1870 */       TagType tag_type = tag.getTagType();
/*      */       
/* 1872 */       String tt_key = String.valueOf(tag_type.getTagType());
/*      */       
/* 1874 */       Map tt = (Map)config.get(tt_key);
/*      */       
/* 1876 */       if (tt == null)
/*      */       {
/* 1878 */         if (type != 4)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1883 */           tt = new HashMap();
/*      */           
/* 1885 */           config.put(tt_key, tt);
/*      */         }
/*      */       } else {
/* 1888 */         String t_key = String.valueOf(tag.getTagID());
/*      */         
/* 1890 */         if (type == 4)
/*      */         {
/* 1892 */           tt.remove(t_key);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1897 */           Map t = (Map)tt.get(t_key);
/*      */           
/* 1899 */           if (t == null)
/*      */           {
/* 1901 */             t = new HashMap();
/*      */             
/* 1903 */             tt.put(t_key, t);
/*      */           }
/*      */           
/* 1906 */           tag.exportDetails(t, type == 3);
/*      */         }
/*      */       } }
/* 1909 */     this.config_change_queue.clear();
/*      */   }
/*      */   
/*      */ 
/*      */   private void destroy()
/*      */   {
/* 1915 */     for (TagType tt : this.tag_types)
/*      */     {
/* 1917 */       ((TagTypeBase)tt).closing();
/*      */     }
/*      */     
/* 1920 */     writeConfig();
/*      */   }
/*      */   
/*      */ 
/*      */   private void setDirty()
/*      */   {
/* 1926 */     synchronized (this)
/*      */     {
/* 1928 */       if (!this.config_dirty)
/*      */       {
/* 1930 */         this.config_dirty = true;
/*      */         
/* 1932 */         this.dirty_dispatcher.dispatch();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private Map readConfig()
/*      */   {
/* 1940 */     if (!enabled)
/*      */     {
/* 1942 */       Debug.out("TagManager is disabled");
/*      */       
/* 1944 */       return new HashMap();
/*      */     }
/*      */     
/*      */     Map map;
/*      */     Map map;
/* 1949 */     if (FileUtil.resilientConfigFileExists("tag.config"))
/*      */     {
/* 1951 */       map = FileUtil.readResilientConfigFile("tag.config");
/*      */     }
/*      */     else
/*      */     {
/* 1955 */       map = new HashMap();
/*      */     }
/*      */     
/* 1958 */     return map;
/*      */   }
/*      */   
/*      */ 
/*      */   private Map getConfig()
/*      */   {
/* 1964 */     synchronized (this)
/*      */     {
/* 1966 */       if (this.config != null)
/*      */       {
/* 1968 */         return this.config;
/*      */       }
/*      */       
/* 1971 */       if (this.config_ref != null)
/*      */       {
/* 1973 */         this.config = ((Map)this.config_ref.get());
/*      */         
/* 1975 */         if (this.config != null)
/*      */         {
/* 1977 */           return this.config;
/*      */         }
/*      */       }
/*      */       
/* 1981 */       this.config = readConfig();
/*      */       
/* 1983 */       return this.config;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void writeConfig()
/*      */   {
/* 1990 */     if (!enabled)
/*      */     {
/* 1992 */       Debug.out("TagManager is disabled");
/*      */     }
/*      */     
/* 1995 */     synchronized (this)
/*      */     {
/* 1997 */       if (!this.config_dirty)
/*      */       {
/* 1999 */         return;
/*      */       }
/*      */       
/* 2002 */       this.config_dirty = false;
/*      */       
/* 2004 */       if (this.config_change_queue.size() > 0)
/*      */       {
/* 2006 */         applyConfigUpdates(getConfig());
/*      */       }
/*      */       
/* 2009 */       if (this.config != null)
/*      */       {
/* 2011 */         FileUtil.writeResilientConfigFile("tag.config", this.config);
/*      */         
/* 2013 */         this.config_ref = new WeakReference(this.config);
/*      */         
/* 2015 */         this.config = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map getConf(TagTypeBase tag_type, TagBase tag, boolean create)
/*      */   {
/* 2026 */     Map m = getConfig();
/*      */     
/* 2028 */     String tt_key = String.valueOf(tag_type.getTagType());
/*      */     
/* 2030 */     Map tt = (Map)m.get(tt_key);
/*      */     
/* 2032 */     if (tt == null)
/*      */     {
/* 2034 */       if (create)
/*      */       {
/* 2036 */         tt = new HashMap();
/*      */         
/* 2038 */         m.put(tt_key, tt);
/*      */       }
/*      */       else
/*      */       {
/* 2042 */         return null;
/*      */       }
/*      */     }
/*      */     
/* 2046 */     String t_key = String.valueOf(tag.getTagID());
/*      */     
/* 2048 */     Map t = (Map)tt.get(t_key);
/*      */     
/* 2050 */     if (t == null)
/*      */     {
/* 2052 */       if (create)
/*      */       {
/* 2054 */         t = new HashMap();
/*      */         
/* 2056 */         tt.put(t_key, t);
/*      */       }
/*      */       else
/*      */       {
/* 2060 */         return null;
/*      */       }
/*      */     }
/*      */     
/* 2064 */     Map conf = (Map)t.get("c");
/*      */     
/* 2066 */     if ((conf == null) && (create))
/*      */     {
/* 2068 */       conf = new HashMap();
/*      */       
/* 2070 */       t.put("c", conf);
/*      */     }
/*      */     
/* 2073 */     return conf;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Boolean readBooleanAttribute(TagTypeBase tag_type, TagBase tag, String attr, Boolean def)
/*      */   {
/* 2083 */     Long result = readLongAttribute(tag_type, tag, attr, def == null ? null : Long.valueOf(def.booleanValue() ? 1L : 0L));
/*      */     
/* 2085 */     if (result == null)
/*      */     {
/* 2087 */       return null;
/*      */     }
/*      */     
/* 2090 */     return Boolean.valueOf(result.longValue() == 1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean writeBooleanAttribute(TagTypeBase tag_type, TagBase tag, String attr, Boolean value)
/*      */   {
/* 2100 */     return writeLongAttribute(tag_type, tag, attr, value == null ? null : Long.valueOf(value.booleanValue() ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Long readLongAttribute(TagTypeBase tag_type, TagBase tag, String attr, Long def)
/*      */   {
/*      */     try
/*      */     {
/* 2111 */       synchronized (this)
/*      */       {
/* 2113 */         Map conf = getConf(tag_type, tag, false);
/*      */         
/* 2115 */         if (conf == null)
/*      */         {
/* 2117 */           return def;
/*      */         }
/*      */         
/* 2120 */         Long value = (Long)conf.get(attr);
/*      */         
/* 2122 */         if (value == null)
/*      */         {
/* 2124 */           return def;
/*      */         }
/*      */         
/* 2127 */         return value;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2133 */       return def;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2131 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean writeLongAttribute(TagTypeBase tag_type, TagBase tag, String attr, Long value)
/*      */   {
/*      */     try
/*      */     {
/* 2145 */       synchronized (this)
/*      */       {
/* 2147 */         Map conf = getConf(tag_type, tag, true);
/*      */         
/* 2149 */         if (value == null)
/*      */         {
/* 2151 */           if (conf.containsKey(attr))
/*      */           {
/* 2153 */             conf.remove(attr);
/*      */             
/* 2155 */             setDirty();
/*      */             
/* 2157 */             return true;
/*      */           }
/*      */           
/*      */ 
/* 2161 */           return false;
/*      */         }
/*      */         
/*      */ 
/* 2165 */         long old = MapUtils.getMapLong(conf, attr, 0L);
/*      */         
/* 2167 */         if ((old == value.longValue()) && (conf.containsKey(attr)))
/*      */         {
/* 2169 */           return false;
/*      */         }
/*      */         
/* 2172 */         conf.put(attr, value);
/*      */         
/* 2174 */         setDirty();
/*      */         
/* 2176 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2183 */       return false;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2181 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String readStringAttribute(TagTypeBase tag_type, TagBase tag, String attr, String def)
/*      */   {
/*      */     try
/*      */     {
/* 2195 */       synchronized (this)
/*      */       {
/* 2197 */         Map conf = getConf(tag_type, tag, false);
/*      */         
/* 2199 */         if (conf == null)
/*      */         {
/* 2201 */           return def;
/*      */         }
/*      */         
/* 2204 */         return MapUtils.getMapString(conf, attr, def);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2210 */       return def;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2208 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeStringAttribute(TagTypeBase tag_type, TagBase tag, String attr, String value)
/*      */   {
/*      */     try
/*      */     {
/* 2222 */       synchronized (this)
/*      */       {
/* 2224 */         Map conf = getConf(tag_type, tag, true);
/*      */         
/* 2226 */         String old = MapUtils.getMapString(conf, attr, null);
/*      */         
/* 2228 */         if (old == value)
/*      */         {
/* 2230 */           return;
/*      */         }
/* 2232 */         if ((old != null) && (value != null) && (old.equals(value)))
/*      */         {
/* 2234 */           return;
/*      */         }
/*      */         
/* 2237 */         MapUtils.setMapString(conf, attr, value);
/*      */         
/* 2239 */         setDirty();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2243 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String[] readStringListAttribute(TagTypeBase tag_type, TagBase tag, String attr, String[] def)
/*      */   {
/*      */     try
/*      */     {
/* 2255 */       synchronized (this)
/*      */       {
/* 2257 */         Map conf = getConf(tag_type, tag, false);
/*      */         
/* 2259 */         if (conf == null)
/*      */         {
/* 2261 */           return def;
/*      */         }
/*      */         
/* 2264 */         List<String> vals = BDecoder.decodeStrings((List)conf.get(attr));
/*      */         
/* 2266 */         if (vals == null)
/*      */         {
/* 2268 */           return def;
/*      */         }
/*      */         
/* 2271 */         return (String[])vals.toArray(new String[vals.size()]);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2277 */       return def;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2275 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean writeStringListAttribute(TagTypeBase tag_type, TagBase tag, String attr, String[] value)
/*      */   {
/*      */     try
/*      */     {
/* 2289 */       synchronized (this)
/*      */       {
/* 2291 */         Map conf = getConf(tag_type, tag, true);
/*      */         
/* 2293 */         List<String> old = BDecoder.decodeStrings((List)conf.get(attr));
/*      */         
/* 2295 */         if ((old == null) && (value == null))
/*      */         {
/* 2297 */           return false;
/*      */         }
/* 2299 */         if ((old != null) && (value != null))
/*      */         {
/* 2301 */           if (value.length == old.size())
/*      */           {
/* 2303 */             boolean diff = false;
/*      */             
/* 2305 */             for (int i = 0; i < value.length; i++)
/*      */             {
/* 2307 */               String old_value = (String)old.get(i);
/*      */               
/* 2309 */               if ((old_value == null) || (!((String)old.get(i)).equals(value[i])))
/*      */               {
/* 2311 */                 diff = true;
/*      */                 
/* 2313 */                 break;
/*      */               }
/*      */             }
/*      */             
/* 2317 */             if (!diff)
/*      */             {
/* 2319 */               return false;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2324 */         if (value == null)
/*      */         {
/* 2326 */           conf.remove(attr);
/*      */         }
/*      */         else {
/* 2329 */           conf.put(attr, Arrays.asList(value));
/*      */         }
/*      */         
/* 2332 */         setDirty();
/*      */         
/* 2334 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2340 */       return false;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2338 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void removeConfig(TagType tag_type)
/*      */   {
/* 2348 */     synchronized (this)
/*      */     {
/* 2350 */       Map m = getConfig();
/*      */       
/* 2352 */       String tt_key = String.valueOf(tag_type.getTagType());
/*      */       
/* 2354 */       Map tt = (Map)m.remove(tt_key);
/*      */       
/* 2356 */       if (tt != null)
/*      */       {
/* 2358 */         setDirty();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeConfig(Tag tag)
/*      */   {
/* 2367 */     TagType tag_type = tag.getTagType();
/*      */     
/* 2369 */     synchronized (this)
/*      */     {
/* 2371 */       Map m = getConfig();
/*      */       
/* 2373 */       String tt_key = String.valueOf(tag_type.getTagType());
/*      */       
/* 2375 */       Map tt = (Map)m.get(tt_key);
/*      */       
/* 2377 */       if (tt == null)
/*      */       {
/* 2379 */         return;
/*      */       }
/*      */       
/* 2382 */       String t_key = String.valueOf(tag.getTagID());
/*      */       
/* 2384 */       Map t = (Map)tt.remove(t_key);
/*      */       
/* 2386 */       if (t != null)
/*      */       {
/* 2388 */         setDirty();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class LifecycleHandlerImpl
/*      */     implements TaggableLifecycleHandler
/*      */   {
/*      */     private TaggableResolver resolver;
/*      */     
/*      */     private boolean initialised;
/* 2400 */     private final CopyOnWriteList<TaggableLifecycleListener> listeners = new CopyOnWriteList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private LifecycleHandlerImpl() {}
/*      */     
/*      */ 
/*      */ 
/*      */     private void setResolver(TaggableResolver _resolver)
/*      */     {
/* 2411 */       this.resolver = _resolver;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void addListener(final TaggableLifecycleListener listener)
/*      */     {
/* 2418 */       synchronized (this)
/*      */       {
/* 2420 */         this.listeners.add(listener);
/*      */         
/* 2422 */         if (this.initialised)
/*      */         {
/* 2424 */           final List<Taggable> taggables = this.resolver.getResolvedTaggables();
/*      */           
/* 2426 */           if (taggables.size() > 0)
/*      */           {
/* 2428 */             TagManagerImpl.this.async_dispatcher.dispatch(new AERunnable()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/* 2435 */                 listener.initialised(taggables);
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void removeListener(TaggableLifecycleListener listener)
/*      */     {
/* 2447 */       synchronized (this)
/*      */       {
/* 2449 */         this.listeners.remove(listener);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void initialized(final List<Taggable> initial_taggables)
/*      */     {
/* 2457 */       TagManagerImpl.this.resolverInitialized(this.resolver);
/*      */       
/* 2459 */       synchronized (this)
/*      */       {
/* 2461 */         this.initialised = true;
/*      */         
/* 2463 */         if (this.listeners.size() > 0)
/*      */         {
/* 2465 */           final List<TaggableLifecycleListener> listeners_ref = this.listeners.getList();
/*      */           
/* 2467 */           TagManagerImpl.this.async_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 2474 */               for (TaggableLifecycleListener listener : listeners_ref)
/*      */               {
/* 2476 */                 listener.initialised(initial_taggables);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void taggableCreated(final Taggable t)
/*      */     {
/* 2488 */       synchronized (this)
/*      */       {
/* 2490 */         if (this.initialised)
/*      */         {
/* 2492 */           final List<TaggableLifecycleListener> listeners_ref = this.listeners.getList();
/*      */           
/* 2494 */           TagManagerImpl.this.async_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 2501 */               for (TaggableLifecycleListener listener : listeners_ref) {
/*      */                 try
/*      */                 {
/* 2504 */                   listener.taggableCreated(t);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 2508 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void taggableDestroyed(final Taggable t)
/*      */     {
/* 2521 */       TagManagerImpl.this.removeTaggable(this.resolver, t);
/*      */       
/* 2523 */       synchronized (this)
/*      */       {
/* 2525 */         if (this.initialised)
/*      */         {
/* 2527 */           final List<TaggableLifecycleListener> listeners_ref = this.listeners.getList();
/*      */           
/* 2529 */           TagManagerImpl.this.async_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 2536 */               for (TaggableLifecycleListener listener : listeners_ref) {
/*      */                 try
/*      */                 {
/* 2539 */                   listener.taggableDestroyed(t);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 2543 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void taggableTagged(final TagType tag_type, final Tag tag, final Taggable taggable)
/*      */     {
/* 2558 */       synchronized (this)
/*      */       {
/* 2560 */         if (this.initialised)
/*      */         {
/* 2562 */           final List<TaggableLifecycleListener> listeners_ref = this.listeners.getList();
/*      */           
/* 2564 */           TagManagerImpl.this.async_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 2571 */               for (TaggableLifecycleListener listener : listeners_ref) {
/*      */                 try
/*      */                 {
/* 2574 */                   listener.taggableTagged(tag_type, tag, taggable);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 2578 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void taggableUntagged(final TagType tag_type, final Tag tag, final Taggable taggable)
/*      */     {
/* 2593 */       synchronized (this)
/*      */       {
/* 2595 */         if (this.initialised)
/*      */         {
/* 2597 */           final List<TaggableLifecycleListener> listeners_ref = this.listeners.getList();
/*      */           
/* 2599 */           TagManagerImpl.this.async_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 2606 */               for (TaggableLifecycleListener listener : listeners_ref) {
/*      */                 try
/*      */                 {
/* 2609 */                   listener.taggableUntagged(tag_type, tag, taggable);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 2613 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2627 */     writer.println("Tag Manager");
/*      */     try
/*      */     {
/* 2630 */       writer.indent();
/*      */       
/* 2632 */       for (TagTypeBase tag_type : this.tag_types)
/*      */       {
/* 2634 */         tag_type.generate(writer);
/*      */       }
/*      */     }
/*      */     finally {
/* 2638 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer, TagTypeBase tag_type) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer, TagTypeBase tag_type, TagBase tag)
/*      */   {
/* 2655 */     synchronized (this)
/*      */     {
/* 2657 */       Map conf = getConf(tag_type, tag, false);
/*      */       
/* 2659 */       if (conf != null)
/*      */       {
/* 2661 */         conf = BDecoder.decodeStrings(BEncoder.cloneMap(conf));
/*      */         
/* 2663 */         writer.println(BEncoder.encodeToJSON(conf));
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */