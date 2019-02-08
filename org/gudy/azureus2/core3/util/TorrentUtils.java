/*      */ package org.gudy.azureus2.core3.util;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.DNSUtils;
/*      */ import com.aelitis.azureus.core.util.DNSUtils.DNSUtilsIntf;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFactory;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*      */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogRelation;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentListener;
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
/*      */ public class TorrentUtils
/*      */ {
/*      */   public static final long MAX_TORRENT_FILE_SIZE = 67108864L;
/*      */   private static final String NO_VALID_URL_URL = "http://no.valid.urls.defined/announce";
/*      */   public static final int TORRENT_FLAG_LOW_NOISE = 1;
/*      */   public static final int TORRENT_FLAG_METADATA_TORRENT = 2;
/*      */   private static final String TORRENT_AZ_PROP_DHT_BACKUP_ENABLE = "dht_backup_enable";
/*      */   private static final String TORRENT_AZ_PROP_DHT_BACKUP_REQUESTED = "dht_backup_requested";
/*      */   private static final String TORRENT_AZ_PROP_TORRENT_FLAGS = "torrent_flags";
/*      */   private static final String TORRENT_AZ_PROP_PLUGINS = "plugins";
/*      */   public static final String TORRENT_AZ_PROP_OBTAINED_FROM = "obtained_from";
/*      */   private static final String TORRENT_AZ_PROP_NETWORK_CACHE = "network_cache";
/*      */   private static final String TORRENT_AZ_PROP_TAG_CACHE = "tag_cache";
/*      */   private static final String TORRENT_AZ_PROP_PEER_CACHE = "peer_cache";
/*      */   private static final String TORRENT_AZ_PROP_PEER_CACHE_VALID = "peer_cache_valid";
/*      */   public static final String TORRENT_AZ_PROP_INITIAL_LINKAGE = "initial_linkage";
/*      */   public static final String TORRENT_AZ_PROP_INITIAL_LINKAGE2 = "initial_linkage2";
/*      */   private static final String MEM_ONLY_TORRENT_PATH = "?/\\!:mem_only:!\\/?";
/*      */   private static final long PC_MARKER;
/*      */   private static final List<byte[]> created_torrents;
/*      */   private static final Set<HashWrapper> created_torrents_set;
/*      */   private static final ThreadLocal<Map<String, Object>> tls;
/*      */   private static volatile Set<String> ignore_files_set;
/*      */   private static volatile Set<String> skip_extensions_set;
/*      */   private static boolean bSaveTorrentBackup;
/*      */   private static final CopyOnWriteList<torrentAttributeListener> torrent_attribute_listeners;
/*      */   static final CopyOnWriteList<TorrentAnnounceURLChangeListener> torrent_url_changed_listeners;
/*      */   private static final AsyncDispatcher dispatcher;
/*      */   private static boolean DNS_HANDLING_ENABLE;
/*      */   private static final boolean TRACE_DNS = false;
/*      */   private static final int DNS_HISTORY_TIMEOUT = 14400000;
/*      */   private static final Map<String, DNSTXTEntry> dns_mapping;
/*      */   private static volatile int dns_mapping_seq_count;
/*      */   private static final ThreadPool dns_threads;
/*      */   static final DNSUtils.DNSUtilsIntf dns_utils;
/*      */   static final AtomicLong torrent_delete_level;
/*      */   static long torrent_delete_time;
/*      */   private static final int PIECE_HASH_TIMEOUT = 180000;
/*      */   static final Map torrent_delegates;
/*      */   
/*      */   public static TOTorrent readFromFile(File file, boolean create_delegate)
/*      */     throws TOTorrentException
/*      */   {
/*  227 */     return readFromFile(file, create_delegate, false);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ExtendedTorrent readDelegateFromFile(File file, boolean force_initial_discard)
/*      */     throws TOTorrentException
/*      */   {
/*  249 */     return (ExtendedTorrent)readFromFile(file, true, force_initial_discard);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static TOTorrent readFromFile(File file, boolean create_delegate, boolean force_initial_discard)
/*      */     throws TOTorrentException
/*      */   {
/*      */     try
/*      */     {
/*  263 */       torrent = TOTorrentFactory.deserialiseFromBEncodedFile(file);
/*      */       
/*      */ 
/*      */ 
/*  267 */       if (bSaveTorrentBackup)
/*      */       {
/*  269 */         File torrent_file_bak = new File(file.getParent(), file.getName() + ".bak");
/*      */         
/*  271 */         if (!torrent_file_bak.exists()) {
/*      */           try
/*      */           {
/*  274 */             torrent.serialiseToBEncodedFile(torrent_file_bak);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  278 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*      */       TOTorrent torrent;
/*      */       
/*  287 */       File torrentBackup = new File(file.getParent(), file.getName() + ".bak");
/*      */       
/*  289 */       if (torrentBackup.exists())
/*      */       {
/*  291 */         torrent = TOTorrentFactory.deserialiseFromBEncodedFile(torrentBackup);
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  298 */         throw e;
/*      */       }
/*      */     }
/*      */     TOTorrent torrent;
/*  302 */     torrent.setAdditionalStringProperty("torrent filename", file.toString());
/*      */     
/*  304 */     if (create_delegate)
/*      */     {
/*  306 */       torrentDelegate res = new torrentDelegate(torrent, file);
/*      */       
/*  308 */       if (force_initial_discard)
/*      */       {
/*  310 */         res.discardPieces(SystemTime.getCurrentTime(), true);
/*      */       }
/*      */       
/*  313 */       return res;
/*      */     }
/*      */     
/*      */ 
/*  317 */     return torrent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static TOTorrent readFromBEncodedInputStream(InputStream is)
/*      */     throws TOTorrentException
/*      */   {
/*  327 */     TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedInputStream(is);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  332 */     torrent.removeAdditionalProperties();
/*      */     
/*  334 */     return torrent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static TOTorrent cloneTorrent(TOTorrent torrent)
/*      */     throws TOTorrentException
/*      */   {
/*  343 */     return TOTorrentFactory.deserialiseFromMap(torrent.serialiseToMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setMemoryOnly(TOTorrent torrent, boolean mem_only)
/*      */   {
/*  351 */     if (mem_only)
/*      */     {
/*  353 */       torrent.setAdditionalStringProperty("torrent filename", "?/\\!:mem_only:!\\/?");
/*      */     }
/*      */     else
/*      */     {
/*  357 */       String s = torrent.getAdditionalStringProperty("torrent filename");
/*      */       
/*  359 */       if ((s != null) && (s.equals("?/\\!:mem_only:!\\/?")))
/*      */       {
/*  361 */         torrent.removeAdditionalProperty("torrent filename");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeToFile(TOTorrent torrent)
/*      */     throws TOTorrentException
/*      */   {
/*  372 */     writeToFile(torrent, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeToFile(TOTorrent torrent, boolean force_backup)
/*      */     throws TOTorrentException
/*      */   {
/*      */     try
/*      */     {
/*  383 */       torrent.getMonitor().enter();
/*      */       
/*  385 */       String str = torrent.getAdditionalStringProperty("torrent filename");
/*      */       
/*  387 */       if (str == null)
/*      */       {
/*  389 */         throw new TOTorrentException("TorrentUtils::writeToFile: no 'torrent filename' attribute defined", 1);
/*      */       }
/*      */       
/*  392 */       if (str.equals("?/\\!:mem_only:!\\/?")) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  400 */       File torrent_file_tmp = new File(str + "._az");
/*      */       
/*  402 */       torrent.serialiseToBEncodedFile(torrent_file_tmp);
/*      */       
/*      */ 
/*      */ 
/*  406 */       File torrent_file = new File(str);
/*      */       
/*  408 */       if (((force_backup) || (COConfigurationManager.getBooleanParameter("Save Torrent Backup"))) && (torrent_file.exists()))
/*      */       {
/*      */ 
/*  411 */         File torrent_file_bak = new File(str + ".bak");
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  417 */           torrent_file_bak.delete();
/*      */           
/*  419 */           torrent_file.renameTo(torrent_file_bak);
/*      */         }
/*      */         catch (SecurityException e)
/*      */         {
/*  423 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  429 */       if (torrent_file.exists())
/*      */       {
/*  431 */         torrent_file.delete();
/*      */       }
/*      */       
/*  434 */       torrent_file_tmp.renameTo(torrent_file);
/*      */     }
/*      */     finally
/*      */     {
/*  438 */       torrent.getMonitor().exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeToFile(TOTorrent torrent, File file)
/*      */     throws TOTorrentException
/*      */   {
/*  449 */     writeToFile(torrent, file, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void writeToFile(TOTorrent torrent, File file, boolean force_backup)
/*      */     throws TOTorrentException
/*      */   {
/*  460 */     torrent.setAdditionalStringProperty("torrent filename", file.toString());
/*      */     
/*  462 */     writeToFile(torrent, force_backup);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getTorrentFileName(TOTorrent torrent)
/*      */     throws TOTorrentException
/*      */   {
/*  471 */     String str = torrent.getAdditionalStringProperty("torrent filename");
/*      */     
/*  473 */     if (str == null)
/*      */     {
/*  475 */       throw new TOTorrentException("TorrentUtils::getTorrentFileName: no 'torrent filename' attribute defined", 1);
/*      */     }
/*      */     
/*  478 */     if (str.equals("?/\\!:mem_only:!\\/?"))
/*      */     {
/*  480 */       return null;
/*      */     }
/*      */     
/*  483 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void copyToFile(TOTorrent torrent, File file)
/*      */     throws TOTorrentException
/*      */   {
/*  493 */     torrent.serialiseToBEncodedFile(file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void delete(TOTorrent torrent)
/*      */     throws TOTorrentException
/*      */   {
/*      */     try
/*      */     {
/*  503 */       torrent.getMonitor().enter();
/*      */       
/*  505 */       String str = torrent.getAdditionalStringProperty("torrent filename");
/*      */       
/*  507 */       if (str == null)
/*      */       {
/*  509 */         throw new TOTorrentException("TorrentUtils::delete: no 'torrent filename' attribute defined", 1);
/*      */       }
/*      */       
/*  512 */       if (str.equals("?/\\!:mem_only:!\\/?")) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  517 */       File file = new File(str);
/*      */       
/*  519 */       if (!file.delete())
/*      */       {
/*  521 */         if (file.exists())
/*      */         {
/*  523 */           throw new TOTorrentException("TorrentUtils::delete: failed to delete '" + str + "'", 5);
/*      */         }
/*      */       }
/*      */       
/*  527 */       new File(str + ".bak").delete();
/*      */     }
/*      */     finally
/*      */     {
/*  531 */       torrent.getMonitor().exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void delete(File torrent_file, boolean force_no_recycle)
/*      */   {
/*  540 */     if (!FileUtil.deleteWithRecycle(torrent_file, force_no_recycle))
/*      */     {
/*  542 */       if (torrent_file.exists())
/*      */       {
/*  544 */         Debug.out("TorrentUtils::delete: failed to delete '" + torrent_file + "'");
/*      */       }
/*      */     }
/*      */     
/*  548 */     new File(torrent_file.toString() + ".bak").delete();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean move(File from_torrent, File to_torrent)
/*      */   {
/*  556 */     if (!FileUtil.renameFile(from_torrent, to_torrent))
/*      */     {
/*  558 */       return false;
/*      */     }
/*      */     
/*  561 */     if (new File(from_torrent.toString() + ".bak").exists())
/*      */     {
/*  563 */       FileUtil.renameFile(new File(from_torrent.toString() + ".bak"), new File(to_torrent.toString() + ".bak"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  568 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String exceptionToText(TOTorrentException e)
/*      */   {
/*  577 */     int reason = e.getReason();
/*      */     String errorDetail;
/*  579 */     String errorDetail; if (reason == 1)
/*      */     {
/*  581 */       errorDetail = MessageText.getString("DownloadManager.error.filenotfound");
/*      */     } else { String errorDetail;
/*  583 */       if (reason == 2)
/*      */       {
/*  585 */         errorDetail = MessageText.getString("DownloadManager.error.fileempty");
/*      */       } else { String errorDetail;
/*  587 */         if (reason == 3)
/*      */         {
/*  589 */           errorDetail = MessageText.getString("DownloadManager.error.filetoobig");
/*      */         } else { String errorDetail;
/*  591 */           if (reason == 6)
/*      */           {
/*  593 */             errorDetail = MessageText.getString("DownloadManager.error.filewithouttorrentinfo");
/*      */           } else { String errorDetail;
/*  595 */             if (reason == 7)
/*      */             {
/*  597 */               errorDetail = MessageText.getString("DownloadManager.error.unsupportedencoding");
/*      */             } else { String errorDetail;
/*  599 */               if (reason == 4)
/*      */               {
/*  601 */                 errorDetail = MessageText.getString("DownloadManager.error.ioerror");
/*      */               } else { String errorDetail;
/*  603 */                 if (reason == 8)
/*      */                 {
/*  605 */                   errorDetail = MessageText.getString("DownloadManager.error.sha1");
/*      */                 } else { String errorDetail;
/*  607 */                   if (reason == 9)
/*      */                   {
/*  609 */                     errorDetail = MessageText.getString("DownloadManager.error.operationcancancelled");
/*      */                   }
/*      */                   else
/*      */                   {
/*  613 */                     errorDetail = Debug.getNestedExceptionMessage(e); }
/*      */                 }
/*      */               } } } } } }
/*  616 */     String msg = Debug.getNestedExceptionMessage(e);
/*      */     
/*  618 */     if (!errorDetail.contains(msg))
/*      */     {
/*  620 */       errorDetail = errorDetail + " (" + msg + ")";
/*      */     }
/*      */     
/*  623 */     return errorDetail;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Set<String> getUniqueTrackerHosts(TOTorrent torrent)
/*      */   {
/*  630 */     Set<String> hosts = new HashSet();
/*      */     
/*  632 */     if (torrent != null)
/*      */     {
/*  634 */       URL announce_url = torrent.getAnnounceURL();
/*      */       
/*  636 */       if (announce_url != null)
/*      */       {
/*  638 */         String host = announce_url.getHost();
/*      */         
/*  640 */         if (host != null)
/*      */         {
/*  642 */           hosts.add(host.toLowerCase(Locale.US));
/*      */         }
/*      */       }
/*      */       
/*  646 */       TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*      */       
/*  648 */       TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */       
/*  650 */       for (TOTorrentAnnounceURLSet set : sets)
/*      */       {
/*  652 */         URL[] urls = set.getAnnounceURLs();
/*      */         
/*  654 */         for (URL u : urls)
/*      */         {
/*  656 */           String host = u.getHost();
/*      */           
/*  658 */           if (host != null)
/*      */           {
/*  660 */             hosts.add(host.toLowerCase(Locale.US));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  666 */     return hosts;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String announceGroupsToText(TOTorrent torrent)
/*      */   {
/*  673 */     URL announce_url = torrent.getAnnounceURL();
/*      */     
/*  675 */     String announce_url_str = announce_url == null ? "" : announce_url.toString().trim();
/*      */     
/*  677 */     TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*      */     
/*  679 */     TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */     
/*  681 */     if (sets.length == 0)
/*      */     {
/*  683 */       return announce_url_str;
/*      */     }
/*      */     
/*      */ 
/*  687 */     StringBuilder sb = new StringBuilder(1024);
/*      */     
/*  689 */     boolean announce_found = false;
/*      */     
/*  691 */     for (int i = 0; i < sets.length; i++)
/*      */     {
/*  693 */       TOTorrentAnnounceURLSet set = sets[i];
/*      */       
/*  695 */       URL[] urls = set.getAnnounceURLs();
/*      */       
/*  697 */       if (urls.length > 0)
/*      */       {
/*  699 */         for (int j = 0; j < urls.length; j++)
/*      */         {
/*  701 */           String str = urls[j].toString().trim();
/*      */           
/*  703 */           if (str.equals(announce_url_str))
/*      */           {
/*  705 */             announce_found = true;
/*      */           }
/*      */           
/*  708 */           sb.append(str);
/*  709 */           sb.append("\r\n");
/*      */         }
/*      */         
/*  712 */         sb.append("\r\n");
/*      */       }
/*      */     }
/*      */     
/*  716 */     String result = sb.toString().trim();
/*      */     
/*  718 */     if (!announce_found)
/*      */     {
/*  720 */       if (announce_url_str.length() > 0)
/*      */       {
/*  722 */         if (result.length() == 0)
/*      */         {
/*  724 */           result = announce_url_str;
/*      */         }
/*      */         else
/*      */         {
/*  728 */           result = "\r\n\r\n" + announce_url_str;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  733 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String announceGroupsToText(List<List<String>> group)
/*      */   {
/*  741 */     StringBuilder sb = new StringBuilder(1024);
/*      */     
/*  743 */     for (List<String> urls : group)
/*      */     {
/*  745 */       if (sb.length() > 0)
/*      */       {
/*  747 */         sb.append("\r\n");
/*      */       }
/*      */       
/*  750 */       for (String str : urls)
/*      */       {
/*  752 */         sb.append(str);
/*  753 */         sb.append("\r\n");
/*      */       }
/*      */     }
/*      */     
/*  757 */     return sb.toString().trim();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<List<String>> announceTextToGroups(String text)
/*      */   {
/*  764 */     List<List<String>> groups = new ArrayList();
/*      */     
/*  766 */     String[] lines = text.split("\n");
/*      */     
/*  768 */     List<String> current_group = new ArrayList();
/*      */     
/*  770 */     Set<String> hits = new HashSet();
/*      */     
/*  772 */     for (String line : lines)
/*      */     {
/*  774 */       line = line.trim();
/*      */       
/*  776 */       if (line.length() == 0)
/*      */       {
/*  778 */         if (current_group.size() > 0)
/*      */         {
/*  780 */           groups.add(current_group);
/*      */           
/*  782 */           current_group = new ArrayList();
/*      */         }
/*      */       } else {
/*  785 */         String lc_line = line.toLowerCase();
/*      */         
/*  787 */         if (!hits.contains(lc_line))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  792 */           hits.add(lc_line);
/*      */           
/*  794 */           current_group.add(line);
/*      */         }
/*      */       }
/*      */     }
/*  798 */     if (current_group.size() > 0)
/*      */     {
/*  800 */       groups.add(current_group);
/*      */     }
/*      */     
/*  803 */     return groups;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<List<String>> announceGroupsToList(TOTorrent torrent)
/*      */   {
/*  810 */     List<List<String>> groups = new ArrayList();
/*      */     
/*  812 */     TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*      */     
/*  814 */     TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */     
/*  816 */     if (sets.length == 0)
/*      */     {
/*  818 */       List<String> s = new ArrayList();
/*      */       
/*  820 */       s.add(UrlUtils.getCanonicalString(torrent.getAnnounceURL()));
/*      */       
/*  822 */       groups.add(s);
/*      */     }
/*      */     else
/*      */     {
/*  826 */       Set<String> all_urls = new HashSet();
/*      */       
/*  828 */       for (int i = 0; i < sets.length; i++)
/*      */       {
/*  830 */         List<String> s = new ArrayList();
/*      */         
/*  832 */         TOTorrentAnnounceURLSet set = sets[i];
/*      */         
/*  834 */         URL[] urls = set.getAnnounceURLs();
/*      */         
/*  836 */         for (int j = 0; j < urls.length; j++)
/*      */         {
/*  838 */           String u = UrlUtils.getCanonicalString(urls[j]);
/*      */           
/*  840 */           s.add(u);
/*      */           
/*  842 */           all_urls.add(u);
/*      */         }
/*      */         
/*  845 */         if (s.size() > 0)
/*      */         {
/*  847 */           groups.add(s);
/*      */         }
/*      */       }
/*      */       
/*  851 */       String a = UrlUtils.getCanonicalString(torrent.getAnnounceURL());
/*      */       
/*  853 */       if (!all_urls.contains(a))
/*      */       {
/*  855 */         List<String> s = new ArrayList();
/*      */         
/*  857 */         s.add(a);
/*      */         
/*  859 */         groups.add(0, s);
/*      */       }
/*      */     }
/*      */     
/*  863 */     return groups;
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
/*      */   public static TOTorrentAnnounceURLSet[] listToAnnounceSets(List<List<String>> groups, TOTorrent torrent)
/*      */   {
/*  878 */     List<TOTorrentAnnounceURLSet> sets = new ArrayList();
/*      */     
/*  880 */     for (List<String> group : groups)
/*      */     {
/*  882 */       List<URL> urls = new ArrayList(group.size());
/*      */       
/*  884 */       for (String s : group) {
/*      */         try
/*      */         {
/*  887 */           urls.add(new URL(s));
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*  893 */       if (urls.size() > 0)
/*      */       {
/*  895 */         sets.add(torrent.getAnnounceURLGroup().createAnnounceURLSet((URL[])urls.toArray(new URL[urls.size()])));
/*      */       }
/*      */     }
/*      */     
/*  899 */     return (TOTorrentAnnounceURLSet[])sets.toArray(new TOTorrentAnnounceURLSet[sets.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void listToAnnounceGroups(List<List<String>> groups, TOTorrent torrent)
/*      */   {
/*      */     try
/*      */     {
/*  913 */       TOTorrentAnnounceURLGroup tg = torrent.getAnnounceURLGroup();
/*      */       
/*  915 */       if (groups.size() == 1)
/*      */       {
/*  917 */         List set = (List)groups.get(0);
/*      */         
/*  919 */         if (set.size() == 1)
/*      */         {
/*  921 */           torrent.setAnnounceURL(new URL((String)set.get(0)));
/*      */           
/*  923 */           tg.setAnnounceURLSets(new TOTorrentAnnounceURLSet[0]);
/*      */           
/*  925 */           return;
/*      */         }
/*      */       }
/*      */       
/*  929 */       String announce_url = torrent.getAnnounceURL().toExternalForm();
/*      */       
/*  931 */       URL first_url = null;
/*      */       
/*  933 */       Vector g = new Vector();
/*      */       
/*  935 */       for (int i = 0; i < groups.size(); i++)
/*      */       {
/*  937 */         List<String> set = (List)groups.get(i);
/*      */         
/*  939 */         URL[] urls = new URL[set.size()];
/*      */         
/*  941 */         for (int j = 0; j < set.size(); j++)
/*      */         {
/*  943 */           String url_str = (String)set.get(j);
/*      */           
/*  945 */           if ((announce_url != null) && (url_str.equals(announce_url)))
/*      */           {
/*  947 */             announce_url = null;
/*      */           }
/*      */           
/*  950 */           urls[j] = new URL((String)set.get(j));
/*      */           
/*  952 */           if (first_url == null)
/*      */           {
/*  954 */             first_url = urls[j];
/*      */           }
/*      */         }
/*      */         
/*  958 */         if (urls.length > 0)
/*      */         {
/*  960 */           g.add(tg.createAnnounceURLSet(urls));
/*      */         }
/*      */       }
/*      */       
/*  964 */       TOTorrentAnnounceURLSet[] sets = new TOTorrentAnnounceURLSet[g.size()];
/*      */       
/*  966 */       if (sets.length == 0)
/*      */       {
/*      */ 
/*      */ 
/*  970 */         torrent.setAnnounceURL(new URL("http://no.valid.urls.defined/announce"));
/*      */ 
/*      */ 
/*      */       }
/*  974 */       else if ((announce_url != null) && (first_url != null))
/*      */       {
/*  976 */         torrent.setAnnounceURL(first_url);
/*      */       }
/*      */       
/*      */ 
/*  980 */       g.copyInto(sets);
/*      */       
/*  982 */       tg.setAnnounceURLSets(sets);
/*      */     }
/*      */     catch (MalformedURLException e)
/*      */     {
/*  986 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void announceGroupsInsertFirst(TOTorrent torrent, String first_url)
/*      */   {
/*      */     try
/*      */     {
/*  997 */       announceGroupsInsertFirst(torrent, new URL(first_url));
/*      */     }
/*      */     catch (MalformedURLException e)
/*      */     {
/* 1001 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void announceGroupsInsertFirst(TOTorrent torrent, URL first_url)
/*      */   {
/* 1010 */     announceGroupsInsertFirst(torrent, new URL[] { first_url });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void announceGroupsInsertFirst(TOTorrent torrent, URL[] first_urls)
/*      */   {
/* 1018 */     TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*      */     
/* 1020 */     TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */     
/* 1022 */     TOTorrentAnnounceURLSet set1 = group.createAnnounceURLSet(first_urls);
/*      */     
/*      */ 
/* 1025 */     if (sets.length > 0)
/*      */     {
/* 1027 */       TOTorrentAnnounceURLSet[] new_sets = new TOTorrentAnnounceURLSet[sets.length + 1];
/*      */       
/* 1029 */       new_sets[0] = set1;
/*      */       
/* 1031 */       System.arraycopy(sets, 0, new_sets, 1, sets.length);
/*      */       
/* 1033 */       group.setAnnounceURLSets(new_sets);
/*      */     }
/*      */     else
/*      */     {
/* 1037 */       TOTorrentAnnounceURLSet set2 = group.createAnnounceURLSet(new URL[] { torrent.getAnnounceURL() });
/*      */       
/* 1039 */       group.setAnnounceURLSets(new TOTorrentAnnounceURLSet[] { set1, set2 });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void announceGroupsInsertLast(TOTorrent torrent, URL[] first_urls)
/*      */   {
/* 1049 */     TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*      */     
/* 1051 */     TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */     
/* 1053 */     TOTorrentAnnounceURLSet set1 = group.createAnnounceURLSet(first_urls);
/*      */     
/*      */ 
/* 1056 */     if (sets.length > 0)
/*      */     {
/* 1058 */       TOTorrentAnnounceURLSet[] new_sets = new TOTorrentAnnounceURLSet[sets.length + 1];
/*      */       
/* 1060 */       new_sets[sets.length] = set1;
/*      */       
/* 1062 */       System.arraycopy(sets, 0, new_sets, 0, sets.length);
/*      */       
/* 1064 */       group.setAnnounceURLSets(new_sets);
/*      */     }
/*      */     else
/*      */     {
/* 1068 */       TOTorrentAnnounceURLSet set2 = group.createAnnounceURLSet(new URL[] { torrent.getAnnounceURL() });
/*      */       
/* 1070 */       group.setAnnounceURLSets(new TOTorrentAnnounceURLSet[] { set2, set1 });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void announceGroupsSetFirst(TOTorrent torrent, String first_url)
/*      */   {
/* 1080 */     List groups = announceGroupsToList(torrent);
/*      */     
/* 1082 */     boolean found = false;
/*      */     
/*      */ 
/* 1085 */     for (int i = 0; i < groups.size(); i++)
/*      */     {
/* 1087 */       List set = (List)groups.get(i);
/*      */       
/* 1089 */       for (int j = 0; j < set.size(); j++)
/*      */       {
/* 1091 */         if (first_url.equals(set.get(j)))
/*      */         {
/* 1093 */           set.remove(j);
/*      */           
/* 1095 */           set.add(0, first_url);
/*      */           
/* 1097 */           groups.remove(set);
/*      */           
/* 1099 */           groups.add(0, set);
/*      */           
/* 1101 */           found = true;
/*      */           
/*      */           break label119;
/*      */         }
/*      */       }
/*      */     }
/*      */     label119:
/* 1108 */     if (!found)
/*      */     {
/* 1110 */       System.out.println("TorrentUtils::announceGroupsSetFirst - failed to find '" + first_url + "'");
/*      */     }
/*      */     
/* 1113 */     listToAnnounceGroups(groups, torrent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean announceGroupsContainsURL(TOTorrent torrent, String url)
/*      */   {
/* 1121 */     List groups = announceGroupsToList(torrent);
/*      */     
/* 1123 */     for (int i = 0; i < groups.size(); i++)
/*      */     {
/* 1125 */       List set = (List)groups.get(i);
/*      */       
/* 1127 */       for (int j = 0; j < set.size(); j++)
/*      */       {
/* 1129 */         if (url.equals(set.get(j)))
/*      */         {
/* 1131 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1136 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean canMergeAnnounceURLs(TOTorrent new_torrent, TOTorrent dest_torrent)
/*      */   {
/*      */     try
/*      */     {
/* 1145 */       List<List<String>> new_groups = announceGroupsToList(new_torrent);
/* 1146 */       List<List<String>> dest_groups = announceGroupsToList(dest_torrent);
/*      */       
/* 1148 */       all_dest = new HashSet();
/*      */       
/* 1150 */       for (List<String> l : dest_groups)
/*      */       {
/* 1152 */         all_dest.addAll(l);
/*      */       }
/*      */       
/* 1155 */       for (List<String> l : new_groups)
/*      */       {
/* 1157 */         for (String u : l)
/*      */         {
/* 1159 */           List<URL> mods = applyAllDNSMods(new URL(u));
/*      */           
/* 1161 */           if (mods != null)
/*      */           {
/* 1163 */             for (URL m : mods)
/*      */             {
/* 1165 */               if (!all_dest.contains(UrlUtils.getCanonicalString(m)))
/*      */               {
/* 1167 */                 return true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/*      */       Set<String> all_dest;
/* 1175 */       Debug.out(e);
/*      */     }
/*      */     
/* 1178 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean mergeAnnounceURLs(TOTorrent new_torrent, TOTorrent dest_torrent)
/*      */   {
/* 1186 */     if ((new_torrent == null) || (dest_torrent == null))
/*      */     {
/* 1188 */       return false;
/*      */     }
/*      */     
/* 1191 */     List new_groups = announceGroupsToList(new_torrent);
/* 1192 */     List dest_groups = announceGroupsToList(dest_torrent);
/*      */     
/* 1194 */     List groups_to_add = new ArrayList();
/*      */     
/* 1196 */     for (int i = 0; i < new_groups.size(); i++)
/*      */     {
/* 1198 */       List new_set = (List)new_groups.get(i);
/*      */       
/* 1200 */       boolean match = false;
/*      */       
/* 1202 */       for (int j = 0; j < dest_groups.size(); j++)
/*      */       {
/* 1204 */         List dest_set = (List)dest_groups.get(j);
/*      */         
/* 1206 */         boolean same = new_set.size() == dest_set.size();
/*      */         
/* 1208 */         if (same)
/*      */         {
/* 1210 */           for (int k = 0; k < new_set.size(); k++)
/*      */           {
/* 1212 */             String new_url = (String)new_set.get(k);
/*      */             
/* 1214 */             if (!dest_set.contains(new_url))
/*      */             {
/* 1216 */               same = false;
/*      */               
/* 1218 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1223 */         if (same)
/*      */         {
/* 1225 */           match = true;
/*      */           
/* 1227 */           break;
/*      */         }
/*      */       }
/*      */       
/* 1231 */       if (!match)
/*      */       {
/* 1233 */         groups_to_add.add(new_set);
/*      */       }
/*      */     }
/*      */     
/* 1237 */     if (groups_to_add.size() == 0)
/*      */     {
/* 1239 */       return false;
/*      */     }
/*      */     
/* 1242 */     for (int i = 0; i < groups_to_add.size(); i++)
/*      */     {
/* 1244 */       dest_groups.add(i, groups_to_add.get(i));
/*      */     }
/*      */     
/* 1247 */     listToAnnounceGroups(dest_groups, dest_torrent);
/*      */     
/* 1249 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<List<String>> mergeAnnounceURLs(List<List<String>> base_urls, List<List<String>> merge_urls)
/*      */   {
/* 1257 */     base_urls = getClone(base_urls);
/* 1258 */     if (merge_urls == null) {
/* 1259 */       return base_urls;
/*      */     }
/* 1261 */     Set<String> mergesSet = new HashSet();
/* 1262 */     mergesSet.add("http://no.valid.urls.defined/announce");
/* 1263 */     for (List<String> l : merge_urls) {
/* 1264 */       mergesSet.addAll(l);
/*      */     }
/* 1266 */     Iterator<List<String>> it1 = base_urls.iterator();
/* 1267 */     while (it1.hasNext()) {
/* 1268 */       List<String> l = (List)it1.next();
/* 1269 */       Iterator<String> it2 = l.iterator();
/* 1270 */       while (it2.hasNext()) {
/* 1271 */         if (mergesSet.contains(it2.next())) {
/* 1272 */           it2.remove();
/*      */         }
/*      */       }
/* 1275 */       if (l.isEmpty()) {
/* 1276 */         it1.remove();
/*      */       }
/*      */     }
/*      */     
/* 1280 */     for (List<String> l : merge_urls) {
/* 1281 */       if (!l.isEmpty()) {
/* 1282 */         base_urls.add(l);
/*      */       }
/*      */     }
/*      */     
/* 1286 */     return base_urls;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<List<String>> removeAnnounceURLs(List<List<String>> base_urls, List<List<String>> remove_urls, boolean use_prefix_match)
/*      */   {
/* 1295 */     base_urls = getClone(base_urls);
/* 1296 */     if (remove_urls == null) {
/* 1297 */       return base_urls;
/*      */     }
/* 1299 */     Set<String> removeSet = new HashSet();
/* 1300 */     removeSet.add("http://no.valid.urls.defined/announce");
/* 1301 */     for (List<String> l : remove_urls) {
/* 1302 */       for (String s : l) {
/* 1303 */         removeSet.add(s.toLowerCase(Locale.US));
/*      */       }
/*      */     }
/* 1306 */     Iterator<List<String>> it1 = base_urls.iterator();
/* 1307 */     while (it1.hasNext()) {
/* 1308 */       List<String> l = (List)it1.next();
/* 1309 */       Iterator<String> it2 = l.iterator();
/* 1310 */       while (it2.hasNext()) {
/* 1311 */         String url = (String)it2.next();
/* 1312 */         if (url.equals("http://no.valid.urls.defined/announce")) {
/* 1313 */           it2.remove();
/*      */         } else {
/* 1315 */           url = url.toLowerCase(Locale.US);
/*      */           
/* 1317 */           if (use_prefix_match)
/*      */           {
/* 1319 */             for (String s : removeSet)
/*      */             {
/* 1321 */               if (url.startsWith(s))
/*      */               {
/* 1323 */                 it2.remove();
/* 1324 */                 break;
/*      */               }
/*      */               
/*      */             }
/* 1328 */           } else if (removeSet.contains(url))
/*      */           {
/* 1330 */             it2.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1335 */       if (l.isEmpty()) {
/* 1336 */         it1.remove();
/*      */       }
/*      */     }
/*      */     
/* 1340 */     return base_urls;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<List<String>> removeAnnounceURLs2(List<List<String>> base_urls, List<String> remove_urls, boolean use_prefix_match)
/*      */   {
/* 1349 */     if (remove_urls == null)
/*      */     {
/* 1351 */       return getClone(base_urls);
/*      */     }
/*      */     
/* 1354 */     List<List<String>> temp = new ArrayList(1);
/*      */     
/* 1356 */     temp.add(remove_urls);
/*      */     
/* 1358 */     return removeAnnounceURLs(base_urls, temp, use_prefix_match);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<List<String>> getClone(List<List<String>> lls)
/*      */   {
/* 1365 */     if (lls == null) {
/* 1366 */       return lls;
/*      */     }
/* 1368 */     List<List<String>> result = new ArrayList(lls.size());
/* 1369 */     for (List<String> l : lls) {
/* 1370 */       result.add(new ArrayList(l));
/*      */     }
/* 1372 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean replaceAnnounceURL(TOTorrent torrent, URL old_url, URL new_url)
/*      */   {
/* 1381 */     boolean found = false;
/*      */     
/* 1383 */     String old_str = old_url.toString();
/* 1384 */     String new_str = new_url.toString();
/*      */     
/* 1386 */     List l = announceGroupsToList(torrent);
/*      */     
/* 1388 */     for (int i = 0; i < l.size(); i++)
/*      */     {
/* 1390 */       List set = (List)l.get(i);
/*      */       
/* 1392 */       for (int j = 0; j < set.size(); j++)
/*      */       {
/* 1394 */         if (((String)set.get(j)).equals(old_str))
/*      */         {
/* 1396 */           found = true;
/*      */           
/* 1398 */           set.set(j, new_str);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1403 */     if (found)
/*      */     {
/* 1405 */       listToAnnounceGroups(l, torrent);
/*      */     }
/*      */     
/* 1408 */     if (torrent.getAnnounceURL().toString().equals(old_str))
/*      */     {
/* 1410 */       torrent.setAnnounceURL(new_url);
/*      */       
/* 1412 */       found = true;
/*      */     }
/*      */     
/* 1415 */     if (found) {
/*      */       try
/*      */       {
/* 1418 */         writeToFile(torrent);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1422 */         Debug.printStackTrace(e);
/*      */         
/* 1424 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1428 */     return found;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setResumeDataCompletelyValid(DownloadManagerState download_manager_state)
/*      */   {
/* 1435 */     DiskManagerFactory.setResumeDataCompletelyValid(download_manager_state);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getLocalisedName(TOTorrent torrent)
/*      */   {
/* 1442 */     if (torrent == null) {
/* 1443 */       return "";
/*      */     }
/*      */     try {
/* 1446 */       String utf8Name = torrent.getUTF8Name();
/* 1447 */       if (utf8Name != null) {
/* 1448 */         return utf8Name;
/*      */       }
/*      */       
/* 1451 */       LocaleUtilDecoder decoder = LocaleTorrentUtil.getTorrentEncodingIfAvailable(torrent);
/*      */       
/* 1453 */       if (decoder == null)
/*      */       {
/* 1455 */         return new String(torrent.getName(), "UTF8");
/*      */       }
/*      */       
/* 1458 */       return decoder.decodeString(torrent.getName());
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1462 */       Debug.printStackTrace(e);
/*      */     }
/* 1464 */     return new String(torrent.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setTLSTorrentHash(HashWrapper hash)
/*      */   {
/* 1472 */     ((Map)tls.get()).put("hash", hash);
/*      */   }
/*      */   
/*      */ 
/*      */   public static HashWrapper getTLSTorrentHash()
/*      */   {
/* 1478 */     return (HashWrapper)((Map)tls.get()).get("hash");
/*      */   }
/*      */   
/*      */ 
/*      */   public static TOTorrent getTLSTorrent()
/*      */   {
/* 1484 */     HashWrapper hash = (HashWrapper)((Map)tls.get()).get("hash");
/*      */     
/* 1486 */     if (hash != null) {
/*      */       try
/*      */       {
/* 1489 */         AzureusCore core = AzureusCoreFactory.getSingleton();
/*      */         
/* 1491 */         DownloadManager dm = core.getGlobalManager().getDownloadManager(hash);
/*      */         
/* 1493 */         if (dm != null)
/*      */         {
/* 1495 */           return dm.getTorrent();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1499 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1503 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setTLSDescription(String desc)
/*      */   {
/* 1510 */     ((Map)tls.get()).put("desc", desc);
/*      */   }
/*      */   
/*      */ 
/*      */   public static String getTLSDescription()
/*      */   {
/* 1516 */     return (String)((Map)tls.get()).get("desc");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Object getTLS()
/*      */   {
/* 1527 */     return new HashMap((Map)tls.get());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setTLS(Object obj)
/*      */   {
/* 1534 */     Map<String, Object> m = (Map)obj;
/*      */     
/* 1536 */     Map<String, Object> tls_map = (Map)tls.get();
/*      */     
/* 1538 */     tls_map.clear();
/*      */     
/* 1540 */     tls_map.putAll(m);
/*      */   }
/*      */   
/*      */   public static URL getDecentralisedEmptyURL()
/*      */   {
/*      */     try
/*      */     {
/* 1547 */       return new URL("dht://");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1551 */       Debug.printStackTrace(e);
/*      */     }
/* 1553 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static URL getDecentralisedURL(byte[] hash)
/*      */   {
/*      */     try
/*      */     {
/* 1562 */       return new URL("dht://" + ByteFormatter.encodeString(hash) + ".dht/announce");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1566 */       Debug.out(e);
/*      */     }
/* 1568 */     return getDecentralisedEmptyURL();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static URL getDecentralisedURL(TOTorrent torrent)
/*      */   {
/*      */     try
/*      */     {
/* 1577 */       return new URL("dht://" + ByteFormatter.encodeString(torrent.getHash()) + ".dht/announce");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1581 */       Debug.out(e);
/*      */     }
/* 1583 */     return getDecentralisedEmptyURL();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setDecentralised(TOTorrent torrent)
/*      */   {
/* 1591 */     torrent.setAnnounceURL(getDecentralisedURL(torrent));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean isDecentralised(TOTorrent torrent)
/*      */   {
/* 1598 */     if (torrent == null)
/*      */     {
/* 1600 */       return false;
/*      */     }
/*      */     
/* 1603 */     return torrent.isDecentralised();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isDecentralised(URL url)
/*      */   {
/* 1611 */     if (url == null)
/*      */     {
/* 1613 */       return false;
/*      */     }
/*      */     
/* 1616 */     return url.getProtocol().equalsIgnoreCase("dht");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean isDecentralised(String host)
/*      */   {
/* 1623 */     if (host == null)
/*      */     {
/* 1625 */       return false;
/*      */     }
/*      */     
/* 1628 */     return host.endsWith(".dht");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static Map getAzureusProperties(TOTorrent torrent)
/*      */   {
/* 1635 */     Map m = torrent.getAdditionalMapProperty("azureus_properties");
/*      */     
/* 1637 */     if (m == null)
/*      */     {
/* 1639 */       m = new HashMap();
/*      */       
/* 1641 */       torrent.setAdditionalMapProperty("azureus_properties", m);
/*      */     }
/*      */     
/* 1644 */     return m;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static Map getAzureusPrivateProperties(TOTorrent torrent)
/*      */   {
/* 1651 */     Map m = torrent.getAdditionalMapProperty("azureus_private_properties");
/*      */     
/* 1653 */     if (m == null)
/*      */     {
/* 1655 */       m = new HashMap();
/*      */       
/* 1657 */       torrent.setAdditionalMapProperty("azureus_private_properties", m);
/*      */     }
/*      */     
/* 1660 */     return m;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getContentMapString(TOTorrent torrent, String key)
/*      */   {
/* 1668 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 1670 */     Object content = m.get("Content");
/*      */     
/* 1672 */     if (!(content instanceof Map))
/*      */     {
/* 1674 */       return null;
/*      */     }
/*      */     
/* 1677 */     Map mapContent = (Map)content;
/*      */     
/* 1679 */     Object obj = mapContent.get(key);
/*      */     
/* 1681 */     if ((obj instanceof String))
/*      */     {
/* 1683 */       return (String)obj;
/*      */     }
/* 1685 */     if ((obj instanceof byte[])) {
/*      */       try
/*      */       {
/* 1688 */         return new String((byte[])obj, "UTF8");
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/* 1692 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */     
/* 1696 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean isFeaturedContent(TOTorrent torrent)
/*      */   {
/* 1703 */     String content_type = getContentMapString(torrent, "Content Type");
/*      */     
/* 1705 */     return (content_type != null) && (content_type.equalsIgnoreCase("featured"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setObtainedFrom(File file, String str)
/*      */   {
/*      */     try
/*      */     {
/* 1714 */       TOTorrent torrent = readFromFile(file, false, false);
/*      */       
/* 1716 */       setObtainedFrom(torrent, str);
/*      */       
/* 1718 */       writeToFile(torrent);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (TOTorrentException e) {}catch (Throwable e)
/*      */     {
/*      */ 
/* 1725 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setObtainedFrom(TOTorrent torrent, String str)
/*      */   {
/* 1734 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     try
/*      */     {
/* 1737 */       str = str.trim();
/*      */       
/* 1739 */       if ((str == null) || (str.length() == 0))
/*      */       {
/* 1741 */         m.remove("obtained_from");
/*      */       }
/*      */       else
/*      */       {
/* 1745 */         m.put("obtained_from", str.getBytes("UTF-8"));
/*      */       }
/*      */       
/* 1748 */       fireAttributeListener(torrent, "obtained_from", str);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1752 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getObtainedFrom(TOTorrent torrent)
/*      */   {
/* 1760 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     
/* 1762 */     byte[] from = (byte[])m.get("obtained_from");
/*      */     
/* 1764 */     if (from != null) {
/*      */       try
/*      */       {
/* 1767 */         return new String(from, "UTF-8");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1771 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1775 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setNetworkCache(TOTorrent torrent, List<String> networks)
/*      */   {
/* 1783 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     try
/*      */     {
/* 1786 */       m.put("network_cache", networks);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1790 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<String> getNetworkCache(TOTorrent torrent)
/*      */   {
/* 1798 */     List<String> result = new ArrayList();
/*      */     
/* 1800 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     try
/*      */     {
/* 1803 */       List l = (List)m.get("network_cache");
/*      */       
/* 1805 */       if (l != null)
/*      */       {
/* 1807 */         for (Object o : l)
/*      */         {
/* 1809 */           if ((o instanceof String))
/*      */           {
/* 1811 */             result.add((String)o);
/*      */           }
/* 1813 */           else if ((o instanceof byte[]))
/*      */           {
/* 1815 */             String s = new String((byte[])o, "UTF-8");
/*      */             
/* 1817 */             for (String x : AENetworkClassifier.AT_NETWORKS)
/*      */             {
/* 1819 */               if (s.equals(x))
/*      */               {
/* 1821 */                 result.add(x);
/*      */                 
/* 1823 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1831 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1834 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setTagCache(TOTorrent torrent, List<String> networks)
/*      */   {
/* 1842 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     try
/*      */     {
/* 1845 */       m.put("tag_cache", networks);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1849 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<String> getTagCache(TOTorrent torrent)
/*      */   {
/* 1857 */     List<String> result = new ArrayList();
/*      */     
/* 1859 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     try
/*      */     {
/* 1862 */       List l = (List)m.get("tag_cache");
/*      */       
/* 1864 */       if (l != null)
/*      */       {
/* 1866 */         for (Object o : l)
/*      */         {
/* 1868 */           if ((o instanceof String))
/*      */           {
/* 1870 */             result.add((String)o);
/*      */           }
/* 1872 */           else if ((o instanceof byte[]))
/*      */           {
/* 1874 */             String s = new String((byte[])o, "UTF-8");
/*      */             
/* 1876 */             result.add(s);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1882 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1885 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setPeerCache(TOTorrent torrent, Map pc)
/*      */   {
/* 1893 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     try
/*      */     {
/* 1896 */       m.put("peer_cache", pc);
/*      */       
/* 1898 */       setPeerCacheValid(torrent);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1902 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setPeerCacheValid(TOTorrent torrent)
/*      */   {
/* 1910 */     Map m = getAzureusPrivateProperties(torrent);
/*      */     try
/*      */     {
/* 1913 */       m.put("peer_cache_valid", new Long(PC_MARKER));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1917 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static Map getPeerCache(TOTorrent torrent)
/*      */   {
/*      */     try
/*      */     {
/* 1926 */       Map m = getAzureusPrivateProperties(torrent);
/*      */       
/* 1928 */       Long value = (Long)m.get("peer_cache_valid");
/*      */       
/* 1930 */       if ((value != null) && (value.longValue() == PC_MARKER))
/*      */       {
/* 1932 */         return (Map)m.get("peer_cache");
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1939 */       Debug.out(e);
/*      */     }
/*      */     
/* 1942 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setFlag(TOTorrent torrent, int flag, boolean value)
/*      */   {
/* 1951 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 1953 */     Long flags = (Long)m.get("torrent_flags");
/*      */     
/* 1955 */     if (flags == null)
/*      */     {
/* 1957 */       flags = new Long(0L);
/*      */     }
/*      */     
/* 1960 */     m.put("torrent_flags", new Long(flags.intValue() | flag));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean getFlag(TOTorrent torrent, int flag)
/*      */   {
/* 1968 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 1970 */     Long flags = (Long)m.get("torrent_flags");
/*      */     
/* 1972 */     if (flags == null)
/*      */     {
/* 1974 */       return false;
/*      */     }
/*      */     
/* 1977 */     return (flags.intValue() & flag) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Map<Integer, File> getInitialLinkage(TOTorrent torrent)
/*      */   {
/* 1984 */     Map<Integer, File> result = new HashMap();
/*      */     try
/*      */     {
/* 1987 */       Map pp = torrent.getAdditionalMapProperty("azureus_private_properties");
/*      */       
/* 1989 */       if (pp != null)
/*      */       {
/*      */ 
/*      */ 
/* 1993 */         byte[] g_data = (byte[])pp.get("initial_linkage2");
/*      */         Map<String, Object> _links;
/* 1995 */         Map<String, Object> _links; if (g_data == null)
/*      */         {
/* 1997 */           _links = (Map)pp.get("initial_linkage");
/*      */         }
/*      */         else
/*      */         {
/* 2001 */           _links = BDecoder.decode(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(g_data))));
/*      */         }
/*      */         
/* 2004 */         if (_links != null)
/*      */         {
/* 2006 */           Map<String, String> links = BDecoder.decodeStrings(_links);
/*      */           
/* 2008 */           for (Map.Entry<String, String> entry : links.entrySet())
/*      */           {
/* 2010 */             int file_index = Integer.parseInt((String)entry.getKey());
/* 2011 */             String file = (String)entry.getValue();
/*      */             
/* 2013 */             result.put(Integer.valueOf(file_index), new File(file));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2019 */       Debug.out("Failed to read linkage map", e);
/*      */     }
/*      */     
/* 2022 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setPluginStringProperty(TOTorrent torrent, String name, String value)
/*      */   {
/* 2031 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2033 */     Object obj = m.get("plugins");
/*      */     
/*      */     Map p;
/*      */     Map p;
/* 2037 */     if ((obj instanceof Map))
/*      */     {
/* 2039 */       p = (Map)obj;
/*      */     }
/*      */     else
/*      */     {
/* 2043 */       p = new HashMap();
/*      */       
/* 2045 */       m.put("plugins", p);
/*      */     }
/*      */     
/* 2048 */     if (value == null)
/*      */     {
/* 2050 */       p.remove(name);
/*      */     }
/*      */     else
/*      */     {
/* 2054 */       p.put(name, value.getBytes());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getPluginStringProperty(TOTorrent torrent, String name)
/*      */   {
/* 2063 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2065 */     Object obj = m.get("plugins");
/*      */     
/* 2067 */     if ((obj instanceof Map))
/*      */     {
/* 2069 */       Map p = (Map)obj;
/*      */       
/* 2071 */       obj = p.get(name);
/*      */       
/* 2073 */       if ((obj instanceof byte[]))
/*      */       {
/* 2075 */         return new String((byte[])obj);
/*      */       }
/*      */     }
/*      */     
/* 2079 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setPluginMapProperty(TOTorrent torrent, String name, Map value)
/*      */   {
/* 2088 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2090 */     Object obj = m.get("plugins");
/*      */     
/*      */     Map p;
/*      */     Map p;
/* 2094 */     if ((obj instanceof Map))
/*      */     {
/* 2096 */       p = (Map)obj;
/*      */     }
/*      */     else
/*      */     {
/* 2100 */       p = new HashMap();
/*      */       
/* 2102 */       m.put("plugins", p);
/*      */     }
/*      */     
/* 2105 */     if (value == null)
/*      */     {
/* 2107 */       p.remove(name);
/*      */     }
/*      */     else
/*      */     {
/* 2111 */       p.put(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map getPluginMapProperty(TOTorrent torrent, String name)
/*      */   {
/* 2120 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2122 */     Object obj = m.get("plugins");
/*      */     
/* 2124 */     if ((obj instanceof Map))
/*      */     {
/* 2126 */       Map p = (Map)obj;
/*      */       
/* 2128 */       obj = p.get(name);
/*      */       
/* 2130 */       if ((obj instanceof Map))
/*      */       {
/* 2132 */         return (Map)obj;
/*      */       }
/*      */     }
/*      */     
/* 2136 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setDHTBackupEnabled(TOTorrent torrent, boolean enabled)
/*      */   {
/* 2144 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2146 */     m.put("dht_backup_enable", new Long(enabled ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean getDHTBackupEnabled(TOTorrent torrent)
/*      */   {
/* 2155 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2157 */     Object obj = m.get("dht_backup_enable");
/*      */     
/* 2159 */     if ((obj instanceof Long))
/*      */     {
/* 2161 */       return ((Long)obj).longValue() == 1L;
/*      */     }
/*      */     
/* 2164 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isDHTBackupRequested(TOTorrent torrent)
/*      */   {
/* 2173 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2175 */     Object obj = m.get("dht_backup_requested");
/*      */     
/* 2177 */     if ((obj instanceof Long))
/*      */     {
/* 2179 */       return ((Long)obj).longValue() == 1L;
/*      */     }
/*      */     
/* 2182 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setDHTBackupRequested(TOTorrent torrent, boolean requested)
/*      */   {
/* 2190 */     Map m = getAzureusProperties(torrent);
/*      */     
/* 2192 */     m.put("dht_backup_requested", new Long(requested ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isReallyPrivate(TOTorrent torrent)
/*      */   {
/* 2200 */     if (torrent == null)
/*      */     {
/* 2202 */       return false;
/*      */     }
/*      */     
/* 2205 */     URL url = torrent.getAnnounceURL();
/*      */     
/* 2207 */     if (url == null)
/*      */     {
/* 2209 */       TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*      */       
/* 2211 */       if ((sets != null) && (sets.length > 0))
/*      */       {
/* 2213 */         URL[] urls = sets[0].getAnnounceURLs();
/*      */         
/* 2215 */         if (urls.length > 0)
/*      */         {
/* 2217 */           url = urls[0];
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2222 */     if (url != null)
/*      */     {
/* 2224 */       if (UrlUtils.containsPasskey(url))
/*      */       {
/* 2226 */         return torrent.getPrivate();
/*      */       }
/*      */     }
/*      */     
/* 2230 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean getPrivate(TOTorrent torrent)
/*      */   {
/* 2237 */     if (torrent == null)
/*      */     {
/* 2239 */       return false;
/*      */     }
/*      */     
/* 2242 */     return torrent.getPrivate();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setPrivate(TOTorrent torrent, boolean _private)
/*      */   {
/* 2250 */     if (torrent == null)
/*      */     {
/* 2252 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2256 */       torrent.setPrivate(_private);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2260 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Set<String> getSkipExtensionsSet()
/*      */   {
/* 2270 */     return getSkipExtensionsSetSupport(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static synchronized Set<String> getSkipExtensionsSetSupport(boolean force)
/*      */   {
/* 2277 */     if ((skip_extensions_set == null) || (force))
/*      */     {
/* 2279 */       Set<String> new_skip_set = new HashSet();
/*      */       
/* 2281 */       String skip_list = COConfigurationManager.getStringParameter("File.Torrent.AutoSkipExtensions");
/*      */       
/* 2283 */       skip_list = skip_list.replace(',', ';');
/*      */       
/* 2285 */       if (skip_extensions_set == null)
/*      */       {
/*      */ 
/*      */ 
/* 2289 */         COConfigurationManager.addParameterListener("File.Torrent.AutoSkipExtensions", new ParameterListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void parameterChanged(String parameterName)
/*      */           {
/*      */ 
/*      */ 
/* 2297 */             TorrentUtils.getSkipExtensionsSetSupport(true);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 2302 */       int pos = 0;
/*      */       
/*      */       for (;;)
/*      */       {
/* 2306 */         int p1 = skip_list.indexOf(";", pos);
/*      */         
/*      */         String bit;
/*      */         String bit;
/* 2310 */         if (p1 == -1)
/*      */         {
/* 2312 */           bit = skip_list.substring(pos);
/*      */         }
/*      */         else
/*      */         {
/* 2316 */           bit = skip_list.substring(pos, p1);
/*      */           
/* 2318 */           pos = p1 + 1;
/*      */         }
/*      */         
/* 2321 */         String ext = bit.trim().toLowerCase();
/*      */         
/* 2323 */         if (ext.startsWith("."))
/*      */         {
/* 2325 */           ext = ext.substring(1);
/*      */         }
/*      */         
/* 2328 */         if (ext.length() > 0)
/*      */         {
/* 2330 */           new_skip_set.add(ext);
/*      */         }
/*      */         
/* 2333 */         if (p1 == -1) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2339 */       skip_extensions_set = new_skip_set;
/*      */     }
/*      */     
/* 2342 */     return skip_extensions_set;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Set<String> getIgnoreSet()
/*      */   {
/* 2351 */     return getIgnoreSetSupport(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static synchronized Set<String> getIgnoreSetSupport(boolean force)
/*      */   {
/* 2358 */     if ((ignore_files_set == null) || (force))
/*      */     {
/* 2360 */       Set<String> new_ignore_set = new HashSet();
/*      */       
/* 2362 */       String ignore_list = COConfigurationManager.getStringParameter("File.Torrent.IgnoreFiles", ".DS_Store;Thumbs.db;desktop.ini");
/*      */       
/* 2364 */       if (ignore_files_set == null)
/*      */       {
/*      */ 
/*      */ 
/* 2368 */         COConfigurationManager.addParameterListener("File.Torrent.IgnoreFiles", new ParameterListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void parameterChanged(String parameterName)
/*      */           {
/*      */ 
/*      */ 
/* 2376 */             TorrentUtils.getIgnoreSetSupport(true);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 2381 */       int pos = 0;
/*      */       
/*      */       for (;;)
/*      */       {
/* 2385 */         int p1 = ignore_list.indexOf(";", pos);
/*      */         
/*      */         String bit;
/*      */         String bit;
/* 2389 */         if (p1 == -1)
/*      */         {
/* 2391 */           bit = ignore_list.substring(pos);
/*      */         }
/*      */         else
/*      */         {
/* 2395 */           bit = ignore_list.substring(pos, p1);
/*      */           
/* 2397 */           pos = p1 + 1;
/*      */         }
/*      */         
/* 2400 */         new_ignore_set.add(bit.trim().toLowerCase());
/*      */         
/* 2402 */         if (p1 == -1) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2408 */       ignore_files_set = new_ignore_set;
/*      */     }
/*      */     
/* 2411 */     return ignore_files_set;
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*   63 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void generate(IndentWriter writer)
/*      */       {
/*      */ 
/*   70 */         writer.println("DNS TXT Records");
/*      */         try
/*      */         {
/*   73 */           writer.indent();
/*      */           
/*   75 */           Set<String> names = COConfigurationManager.getDefinedParameters();
/*      */           
/*   77 */           prefix = "dns.txts.cache.";
/*      */           
/*   79 */           for (String name : names)
/*      */           {
/*   81 */             if (name.startsWith(prefix)) {
/*      */               try
/*      */               {
/*   84 */                 String tracker = new String(Base32.decode(name.substring(prefix.length())), "UTF-8");
/*      */                 
/*   86 */                 String str = "";
/*      */                 
/*   88 */                 List<byte[]> txts = COConfigurationManager.getListParameter(name, null);
/*      */                 
/*   90 */                 if (txts != null)
/*      */                 {
/*   92 */                   for (byte[] txt : txts)
/*      */                   {
/*   94 */                     str = str + (str.length() == 0 ? "" : ", ") + new String(txt, "UTF-8");
/*      */                   }
/*      */                 }
/*      */                 
/*   98 */                 writer.println(tracker + " -> [" + str + "]");
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  102 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         } finally {
/*      */           String prefix;
/*  108 */           writer.exdent();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  131 */     });
/*  132 */     PC_MARKER = RandomUtils.nextLong();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  137 */     tls = new ThreadLocal()
/*      */     {
/*      */ 
/*      */       public Map<String, Object> initialValue()
/*      */       {
/*      */ 
/*  143 */         return new HashMap();
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  151 */     };
/*  152 */     torrent_attribute_listeners = new CopyOnWriteList();
/*  153 */     torrent_url_changed_listeners = new CopyOnWriteList();
/*      */     
/*  155 */     dispatcher = new AsyncDispatcher();
/*      */     
/*  157 */     DNS_HANDLING_ENABLE = true;
/*      */     
/*      */ 
/*      */ 
/*  161 */     dns_mapping = new HashMap();
/*      */     
/*  163 */     dns_threads = new ThreadPool("DNS:lookups", 16, true);
/*      */     
/*      */ 
/*  166 */     SimpleTimer.addPeriodicEvent("TU:dnstimer", 7200000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  175 */         if (TorrentUtils.DNS_HANDLING_ENABLE)
/*      */         {
/*  177 */           TorrentUtils.access$100();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  182 */     });
/*  183 */     dns_utils = DNSUtils.getSingleton();
/*      */     
/*      */ 
/*  186 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Save Torrent Backup", "Tracker DNS Records Enable", "Enable.Proxy" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String _name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  196 */         TorrentUtils.access$202(COConfigurationManager.getBooleanParameter("Save Torrent Backup"));
/*      */         
/*  198 */         boolean enable_proxy = COConfigurationManager.getBooleanParameter("Enable.Proxy");
/*      */         
/*  200 */         TorrentUtils.access$002((TorrentUtils.dns_utils != null) && (COConfigurationManager.getBooleanParameter("Tracker DNS Records Enable")) && (!enable_proxy));
/*      */       }
/*      */       
/*  203 */     });
/*  204 */     created_torrents = COConfigurationManager.getListParameter("my.created.torrents", new ArrayList());
/*      */     
/*  206 */     created_torrents_set = new HashSet();
/*      */     
/*  208 */     Iterator it = created_torrents.iterator();
/*      */     
/*  210 */     while (it.hasNext())
/*      */     {
/*  212 */       created_torrents_set.add(new HashWrapper((byte[])it.next()));
/*      */     }
/*      */     
/*      */ 
/*  216 */     torrent_delete_level = new AtomicLong();
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
/* 2421 */     torrent_delegates = new WeakHashMap();
/*      */     
/*      */ 
/* 2424 */     SimpleTimer.addPeriodicEvent("TorrentUtils:pieceDiscard", 90000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/* 2433 */         long now = SystemTime.getCurrentTime();
/*      */         
/* 2435 */         synchronized (TorrentUtils.torrent_delegates)
/*      */         {
/* 2437 */           Iterator it = TorrentUtils.torrent_delegates.keySet().iterator();
/*      */           
/* 2439 */           while (it.hasNext())
/*      */           {
/* 2441 */             ((TorrentUtils.torrentDelegate)it.next()).discardPieces(now, false);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/* 2448 */   static final HashSet torrentFluffKeyset = new HashSet(2);
/* 2449 */   static final Map fluffThombstone = new HashMap(1);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void registerMapFluff(String[] fluff)
/*      */   {
/* 2459 */     synchronized (TorrentUtils.class)
/*      */     {
/* 2461 */       Collections.addAll(torrentFluffKeyset, fluff);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class torrentDelegate
/*      */     extends LogRelation
/*      */     implements TorrentUtils.ExtendedTorrent
/*      */   {
/*      */     private final TOTorrent delegate;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private final File file;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean fluff_dirty;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2489 */     private long last_pieces_read_time = SystemTime.getCurrentTime();
/*      */     
/*      */     private URL url_mod_last_pre;
/*      */     
/*      */     private URL url_mod_last_post;
/*      */     
/*      */     private int url_mod_last_seq;
/*      */     
/*      */     private List<URL> urlg_mod_last_pre;
/*      */     
/*      */     private TOTorrentAnnounceURLGroup urlg_mod_last_post;
/*      */     private int urlg_mod_last_seq;
/*      */     
/*      */     protected torrentDelegate(TOTorrent _delegate, File _file)
/*      */     {
/* 2504 */       this.delegate = _delegate;
/* 2505 */       this.file = _file;
/*      */       
/* 2507 */       synchronized (TorrentUtils.torrent_delegates)
/*      */       {
/* 2509 */         TorrentUtils.torrent_delegates.put(this, null);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setDiscardFluff(boolean discard)
/*      */     {
/* 2517 */       if ((discard) && (!TorrentUtils.torrentFluffKeyset.isEmpty()))
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 2522 */           getMonitor().enter();
/*      */           
/*      */ 
/*      */           try
/*      */           {
/* 2527 */             if (this.fluff_dirty)
/*      */             {
/* 2529 */               boolean[] restored = restoreState(true, true);
/*      */               
/* 2531 */               this.delegate.serialiseToBEncodedFile(this.file);
/*      */               
/* 2533 */               this.fluff_dirty = false;
/*      */               
/* 2535 */               if (restored[0] != 0)
/*      */               {
/* 2537 */                 discardPieces(SystemTime.getCurrentTime(), true);
/*      */               }
/*      */             }
/*      */             
/* 2541 */             for (it = TorrentUtils.torrentFluffKeyset.iterator(); it.hasNext();)
/*      */             {
/* 2543 */               this.delegate.setAdditionalMapProperty((String)it.next(), TorrentUtils.fluffThombstone);
/*      */             }
/*      */           } catch (Throwable e) {
/*      */             Iterator it;
/* 2547 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         finally {
/* 2551 */           getMonitor().exit();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getName()
/*      */     {
/* 2559 */       return this.delegate.getName();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isSimpleTorrent()
/*      */     {
/* 2565 */       return this.delegate.isSimpleTorrent();
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getComment()
/*      */     {
/* 2571 */       return this.delegate.getComment();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setComment(String comment)
/*      */     {
/* 2578 */       this.delegate.setComment(comment);
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCreationDate()
/*      */     {
/* 2584 */       return this.delegate.getCreationDate();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setCreationDate(long date)
/*      */     {
/* 2591 */       this.delegate.setCreationDate(date);
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getCreatedBy()
/*      */     {
/* 2597 */       return this.delegate.getCreatedBy();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setCreatedBy(byte[] cb)
/*      */     {
/* 2604 */       this.delegate.setCreatedBy(cb);
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isCreated()
/*      */     {
/* 2610 */       return this.delegate.isCreated();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isDecentralised()
/*      */     {
/* 2616 */       URL url = getAnnounceURLSupport();
/*      */       
/* 2618 */       return TorrentUtils.isDecentralised(url);
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getAnnounceURL()
/*      */     {
/* 2624 */       URL url = getAnnounceURLSupport();
/*      */       
/* 2626 */       int seq = TorrentUtils.dns_mapping_seq_count;
/*      */       
/* 2628 */       if ((url == this.url_mod_last_pre) && (this.url_mod_last_post != null) && (seq == this.url_mod_last_seq))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2634 */         return this.url_mod_last_post;
/*      */       }
/*      */       
/* 2637 */       this.url_mod_last_post = TorrentUtils.applyDNSMods(url);
/* 2638 */       this.url_mod_last_pre = url;
/* 2639 */       this.url_mod_last_seq = seq;
/*      */       
/* 2641 */       return this.url_mod_last_post;
/*      */     }
/*      */     
/*      */ 
/*      */     public TOTorrentAnnounceURLGroup getAnnounceURLGroup()
/*      */     {
/* 2647 */       TOTorrentAnnounceURLGroup group = getAnnounceURLGroupSupport();
/*      */       
/* 2649 */       int seq = TorrentUtils.dns_mapping_seq_count;
/*      */       
/* 2651 */       if ((seq == this.urlg_mod_last_seq) && (this.urlg_mod_last_pre != null) && (this.urlg_mod_last_post != null))
/*      */       {
/* 2653 */         boolean match = (!(this.urlg_mod_last_post instanceof TorrentUtils.URLGroup)) || (!((TorrentUtils.URLGroup)this.urlg_mod_last_post).hasBeenModified());
/*      */         
/* 2655 */         if (match)
/*      */         {
/* 2657 */           TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */           
/* 2659 */           Iterator<URL> it = this.urlg_mod_last_pre.iterator();
/*      */           
/*      */ 
/* 2662 */           for (int i = 0; i < sets.length; i++)
/*      */           {
/* 2664 */             URL[] urls = sets[i].getAnnounceURLs();
/*      */             
/* 2666 */             for (int j = 0; j < urls.length; j++)
/*      */             {
/* 2668 */               if (!it.hasNext())
/*      */               {
/* 2670 */                 match = false;
/*      */                 
/*      */                 break label164;
/*      */               }
/*      */               
/* 2675 */               if (it.next() != urls[j])
/*      */               {
/* 2677 */                 match = false;
/*      */                 
/* 2679 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           label164:
/* 2684 */           if (it.hasNext())
/*      */           {
/* 2686 */             match = false;
/*      */           }
/*      */         }
/*      */         
/* 2690 */         if (match)
/*      */         {
/*      */ 
/*      */ 
/* 2694 */           return this.urlg_mod_last_post;
/*      */         }
/*      */       }
/*      */       
/* 2698 */       List<URL> url_list = new ArrayList();
/*      */       
/* 2700 */       TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */       
/* 2702 */       for (int i = 0; i < sets.length; i++)
/*      */       {
/* 2704 */         URL[] urls = sets[i].getAnnounceURLs();
/*      */         
/* 2706 */         Collections.addAll(url_list, urls);
/*      */       }
/*      */       
/* 2709 */       this.urlg_mod_last_post = TorrentUtils.applyDNSMods(getAnnounceURL(), group);
/* 2710 */       this.urlg_mod_last_pre = url_list;
/* 2711 */       this.urlg_mod_last_seq = seq;
/*      */       
/* 2713 */       return this.urlg_mod_last_post;
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getAnnounceURLSupport()
/*      */     {
/* 2719 */       return this.delegate.getAnnounceURL();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean setAnnounceURL(URL url)
/*      */     {
/* 2726 */       return this.delegate.setAnnounceURL(url);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public TOTorrentAnnounceURLGroup getAnnounceURLGroupSupport()
/*      */     {
/* 2733 */       return this.delegate.getAnnounceURLGroup();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void discardPieces(long now, boolean force)
/*      */     {
/* 2743 */       if ((now < this.last_pieces_read_time) && (!force))
/*      */       {
/* 2745 */         this.last_pieces_read_time = now;
/*      */       }
/*      */       else {
/*      */         try
/*      */         {
/* 2750 */           if (((now - this.last_pieces_read_time > 180000L) || (force)) && (this.delegate.getPieces() != null))
/*      */           {
/*      */             try
/*      */             {
/* 2754 */               getMonitor().enter();
/*      */               
/*      */ 
/*      */ 
/* 2758 */               this.delegate.setPieces((byte[][])null);
/*      */             }
/*      */             finally {
/* 2761 */               getMonitor().exit();
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 2766 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[][] getPieces()
/*      */       throws TOTorrentException
/*      */     {
/* 2776 */       byte[][] res = this.delegate.getPieces();
/*      */       
/* 2778 */       this.last_pieces_read_time = SystemTime.getCurrentTime();
/*      */       
/* 2780 */       if (res == null)
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 2785 */           getMonitor().enter();
/*      */           
/* 2787 */           restoreState(true, false);
/*      */           
/* 2789 */           res = this.delegate.getPieces();
/*      */         }
/*      */         finally
/*      */         {
/* 2793 */           getMonitor().exit();
/*      */         }
/*      */       }
/*      */       
/* 2797 */       return res;
/*      */     }
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
/*      */     protected boolean[] restoreState(boolean do_pieces, boolean do_fluff)
/*      */       throws TOTorrentException
/*      */     {
/* 2814 */       boolean had_pieces = this.delegate.getPieces() != null;
/*      */       
/* 2816 */       boolean had_fluff = true;
/*      */       
/* 2818 */       for (Iterator it = TorrentUtils.torrentFluffKeyset.iterator(); it.hasNext();)
/*      */       {
/* 2820 */         had_fluff &= this.delegate.getAdditionalMapProperty((String)it.next()) != TorrentUtils.fluffThombstone;
/*      */       }
/*      */       
/* 2823 */       if (had_pieces)
/*      */       {
/* 2825 */         do_pieces = false;
/*      */       }
/*      */       
/* 2828 */       if (had_fluff)
/*      */       {
/* 2830 */         do_fluff = false; }
/*      */       TOTorrent temp;
/*      */       Iterator it;
/* 2833 */       if ((do_pieces) || (do_fluff))
/*      */       {
/* 2835 */         temp = TorrentUtils.readFromFile(this.file, false);
/*      */         
/* 2837 */         if (do_pieces)
/*      */         {
/* 2839 */           byte[][] res = temp.getPieces();
/*      */           
/* 2841 */           this.delegate.setPieces(res);
/*      */         }
/*      */         
/* 2844 */         if (do_fluff)
/*      */         {
/* 2846 */           for (it = TorrentUtils.torrentFluffKeyset.iterator(); it.hasNext();)
/*      */           {
/* 2848 */             String fluffKey = (String)it.next();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2853 */             if (this.delegate.getAdditionalMapProperty(fluffKey) == TorrentUtils.fluffThombstone)
/*      */             {
/* 2855 */               this.delegate.setAdditionalMapProperty(fluffKey, temp.getAdditionalMapProperty(fluffKey));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2861 */       return new boolean[] { do_pieces, do_fluff };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public byte[][] peekPieces()
/*      */       throws TOTorrentException
/*      */     {
/* 2874 */       return this.delegate.getPieces();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setPieces(byte[][] pieces)
/*      */       throws TOTorrentException
/*      */     {
/* 2883 */       throw new TOTorrentException("Unsupported Operation", 5);
/*      */     }
/*      */     
/*      */ 
/*      */     public long getPieceLength()
/*      */     {
/* 2889 */       return this.delegate.getPieceLength();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getNumberOfPieces()
/*      */     {
/* 2895 */       return this.delegate.getNumberOfPieces();
/*      */     }
/*      */     
/*      */ 
/*      */     public long getSize()
/*      */     {
/* 2901 */       return this.delegate.getSize();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getFileCount()
/*      */     {
/* 2907 */       return this.delegate.getFileCount();
/*      */     }
/*      */     
/*      */ 
/*      */     public TOTorrentFile[] getFiles()
/*      */     {
/* 2913 */       return this.delegate.getFiles();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getHash()
/*      */       throws TOTorrentException
/*      */     {
/* 2921 */       return this.delegate.getHash();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public HashWrapper getHashWrapper()
/*      */       throws TOTorrentException
/*      */     {
/* 2929 */       return this.delegate.getHashWrapper();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setHashOverride(byte[] hash)
/*      */       throws TOTorrentException
/*      */     {
/* 2938 */       throw new TOTorrentException("Not supported", 8);
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getPrivate()
/*      */     {
/* 2944 */       return this.delegate.getPrivate();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setPrivate(boolean _private)
/*      */       throws TOTorrentException
/*      */     {
/* 2955 */       throw new TOTorrentException("Can't amend private attribute", 5);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean hasSameHashAs(TOTorrent other)
/*      */     {
/* 2962 */       return this.delegate.hasSameHashAs(other);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalStringProperty(String name, String value)
/*      */     {
/* 2970 */       this.delegate.setAdditionalStringProperty(name, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getAdditionalStringProperty(String name)
/*      */     {
/* 2977 */       return this.delegate.getAdditionalStringProperty(name);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalByteArrayProperty(String name, byte[] value)
/*      */     {
/* 2985 */       this.delegate.setAdditionalByteArrayProperty(name, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getAdditionalByteArrayProperty(String name)
/*      */     {
/* 2992 */       return this.delegate.getAdditionalByteArrayProperty(name);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalLongProperty(String name, Long value)
/*      */     {
/* 3000 */       this.delegate.setAdditionalLongProperty(name, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Long getAdditionalLongProperty(String name)
/*      */     {
/* 3007 */       return this.delegate.getAdditionalLongProperty(name);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalListProperty(String name, List value)
/*      */     {
/* 3016 */       this.delegate.setAdditionalListProperty(name, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public List getAdditionalListProperty(String name)
/*      */     {
/* 3023 */       return this.delegate.getAdditionalListProperty(name);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalMapProperty(String name, Map value)
/*      */     {
/* 3031 */       if (TorrentUtils.torrentFluffKeyset.contains(name))
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 3036 */           getMonitor().enter();
/*      */           
/* 3038 */           this.delegate.setAdditionalMapProperty(name, value);
/*      */           
/* 3040 */           this.fluff_dirty = true;
/*      */         }
/*      */         finally
/*      */         {
/* 3044 */           getMonitor().exit();
/*      */         }
/*      */         
/*      */       } else {
/* 3048 */         this.delegate.setAdditionalMapProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Map getAdditionalMapProperty(String name)
/*      */     {
/* 3056 */       if (TorrentUtils.torrentFluffKeyset.contains(name)) {
/*      */         try
/*      */         {
/* 3059 */           getMonitor().enter();
/*      */           
/* 3061 */           Map result = this.delegate.getAdditionalMapProperty(name);
/*      */           
/* 3063 */           if (result == TorrentUtils.fluffThombstone) {
/*      */             try
/*      */             {
/* 3066 */               restoreState(false, true);
/*      */               
/* 3068 */               Map res = this.delegate.getAdditionalMapProperty(name);
/*      */               
/*      */ 
/*      */ 
/* 3072 */               return res;
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3076 */               Debug.out("Property '" + name + " lost due to torrent read error", e);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 3081 */           getMonitor().exit();
/*      */         }
/*      */       }
/*      */       
/* 3085 */       return this.delegate.getAdditionalMapProperty(name);
/*      */     }
/*      */     
/*      */     public Object getAdditionalProperty(String name) {
/* 3089 */       if (TorrentUtils.torrentFluffKeyset.contains(name))
/*      */       {
/*      */         try
/*      */         {
/* 3093 */           getMonitor().enter();
/*      */           
/* 3095 */           Object result = this.delegate.getAdditionalProperty(name);
/* 3096 */           if (result == TorrentUtils.fluffThombstone)
/*      */           {
/*      */             try
/*      */             {
/* 3100 */               restoreState(false, true);
/* 3101 */               Object res = this.delegate.getAdditionalProperty(name);
/*      */               
/*      */ 
/* 3104 */               return res;
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3108 */               Debug.out("Property '" + name + " lost due to torrent read error", e);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 3113 */           getMonitor().exit();
/*      */         }
/*      */       }
/*      */       
/* 3117 */       return this.delegate.getAdditionalProperty(name);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalProperty(String name, Object value)
/*      */     {
/* 3125 */       if (TorrentUtils.torrentFluffKeyset.contains(name))
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 3130 */           getMonitor().enter();
/*      */           
/* 3132 */           this.delegate.setAdditionalProperty(name, value);
/*      */           
/* 3134 */           this.fluff_dirty = true;
/*      */         }
/*      */         finally
/*      */         {
/* 3138 */           getMonitor().exit();
/*      */         }
/*      */         
/*      */       } else {
/* 3142 */         this.delegate.setAdditionalProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeAdditionalProperty(String name)
/*      */     {
/* 3150 */       if (this.delegate.getAdditionalProperty(name) == null) {
/* 3151 */         return;
/*      */       }
/* 3153 */       if (TorrentUtils.torrentFluffKeyset.contains(name))
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 3158 */           getMonitor().enter();
/*      */           
/* 3160 */           this.delegate.removeAdditionalProperty(name);
/*      */           
/* 3162 */           this.fluff_dirty = true;
/*      */         }
/*      */         finally
/*      */         {
/* 3166 */           getMonitor().exit();
/*      */         }
/*      */         
/*      */       } else {
/* 3170 */         this.delegate.removeAdditionalProperty(name);
/*      */       }
/*      */     }
/*      */     
/*      */     public void removeAdditionalProperties()
/*      */     {
/*      */       try
/*      */       {
/* 3178 */         getMonitor().enter();
/*      */         
/* 3180 */         this.delegate.removeAdditionalProperties();
/*      */         
/* 3182 */         this.fluff_dirty = true;
/*      */       }
/*      */       finally
/*      */       {
/* 3186 */         getMonitor().exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void serialiseToBEncodedFile(File target_file)
/*      */       throws TOTorrentException
/*      */     {
/*      */       try
/*      */       {
/* 3199 */         getMonitor().enter();
/*      */         
/* 3201 */         boolean[] restored = restoreState(true, true);
/*      */         
/* 3203 */         this.delegate.serialiseToBEncodedFile(target_file);
/*      */         
/* 3205 */         if (target_file.equals(this.file))
/*      */         {
/* 3207 */           this.fluff_dirty = false;
/*      */         }
/*      */         
/* 3210 */         if (restored[0] != 0)
/*      */         {
/* 3212 */           discardPieces(SystemTime.getCurrentTime(), true);
/*      */         }
/*      */         
/* 3215 */         if (restored[1] != 0)
/*      */         {
/* 3217 */           for (it = TorrentUtils.torrentFluffKeyset.iterator(); it.hasNext();)
/*      */           {
/* 3219 */             this.delegate.setAdditionalMapProperty((String)it.next(), TorrentUtils.fluffThombstone);
/*      */           }
/*      */         }
/*      */       } finally {
/*      */         Iterator it;
/* 3224 */         getMonitor().exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Map serialiseToMap()
/*      */       throws TOTorrentException
/*      */     {
/*      */       try
/*      */       {
/* 3237 */         getMonitor().enter();
/*      */         
/* 3239 */         boolean[] restored = restoreState(true, true);
/*      */         
/* 3241 */         Map result = this.delegate.serialiseToMap();
/*      */         
/* 3243 */         if (restored[0] != 0)
/*      */         {
/* 3245 */           discardPieces(SystemTime.getCurrentTime(), true);
/*      */         }
/*      */         Iterator it;
/* 3248 */         if (restored[1] != 0)
/*      */         {
/* 3250 */           for (it = TorrentUtils.torrentFluffKeyset.iterator(); it.hasNext();)
/*      */           {
/* 3252 */             this.delegate.setAdditionalMapProperty((String)it.next(), TorrentUtils.fluffThombstone);
/*      */           }
/*      */         }
/*      */         
/* 3256 */         return result;
/*      */       }
/*      */       finally
/*      */       {
/* 3260 */         getMonitor().exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void serialiseToXMLFile(File target_file)
/*      */       throws TOTorrentException
/*      */     {
/*      */       try
/*      */       {
/* 3274 */         getMonitor().enter();
/*      */         
/* 3276 */         boolean[] restored = restoreState(true, true);
/*      */         
/* 3278 */         this.delegate.serialiseToXMLFile(target_file);
/*      */         
/* 3280 */         if (restored[0] != 0)
/*      */         {
/* 3282 */           discardPieces(SystemTime.getCurrentTime(), true);
/*      */         }
/*      */         
/* 3285 */         if (restored[1] != 0)
/*      */         {
/* 3287 */           for (it = TorrentUtils.torrentFluffKeyset.iterator(); it.hasNext();)
/*      */           {
/* 3289 */             this.delegate.setAdditionalMapProperty((String)it.next(), TorrentUtils.fluffThombstone);
/*      */           }
/*      */         }
/*      */       } finally {
/*      */         Iterator it;
/* 3294 */         getMonitor().exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void addListener(TOTorrentListener l)
/*      */     {
/* 3302 */       this.delegate.addListener(l);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(TOTorrentListener l)
/*      */     {
/* 3309 */       this.delegate.removeListener(l);
/*      */     }
/*      */     
/*      */ 
/*      */     public AEMonitor getMonitor()
/*      */     {
/* 3315 */       return this.delegate.getMonitor();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void print()
/*      */     {
/* 3322 */       this.delegate.print();
/*      */     }
/*      */     
/*      */     public String getRelationText() {
/* 3326 */       if ((this.delegate instanceof LogRelation))
/* 3327 */         return ((LogRelation)this.delegate).getRelationText();
/* 3328 */       return this.delegate.toString();
/*      */     }
/*      */     
/*      */     public Object[] getQueryableInterfaces() {
/* 3332 */       if ((this.delegate instanceof LogRelation))
/* 3333 */         return ((LogRelation)this.delegate).getQueryableInterfaces();
/* 3334 */       return super.getQueryableInterfaces();
/*      */     }
/*      */     
/*      */     public String getUTF8Name() {
/* 3338 */       return this.delegate.getUTF8Name();
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
/*      */   public static File copyTorrentFileToSaveDir(File f, boolean persistent)
/*      */     throws IOException
/*      */   {
/* 3356 */     boolean saveTorrents = (persistent) && (COConfigurationManager.getBooleanParameter("Save Torrent Files"));
/*      */     File torrentDir;
/* 3358 */     File torrentDir; if (saveTorrents) {
/* 3359 */       torrentDir = new File(COConfigurationManager.getDirectoryParameter("General_sDefaultTorrent_Directory"));
/*      */     }
/*      */     else {
/* 3362 */       torrentDir = new File(f.getParent());
/*      */     }
/*      */     
/*      */ 
/* 3366 */     boolean moveWhenDone = COConfigurationManager.getBooleanParameter("Move Completed When Done");
/* 3367 */     String completedDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
/*      */     
/* 3369 */     if ((moveWhenDone) && (completedDir.length() > 0)) {
/* 3370 */       File cFile = new File(completedDir, f.getName());
/* 3371 */       if (cFile.exists())
/*      */       {
/* 3373 */         torrentDir = new File(completedDir);
/*      */       }
/*      */     }
/*      */     
/* 3377 */     FileUtil.mkdirs(torrentDir);
/*      */     
/* 3379 */     File fDest = new File(torrentDir, f.getName().replaceAll("%20", "."));
/* 3380 */     if (fDest.equals(f)) {
/* 3381 */       return f;
/*      */     }
/*      */     
/* 3384 */     while (fDest.exists()) {
/* 3385 */       fDest = new File(torrentDir, "_" + fDest.getName());
/*      */     }
/*      */     
/* 3388 */     fDest.createNewFile();
/*      */     
/* 3390 */     if (!FileUtil.copyFile(f, fDest)) {
/* 3391 */       throw new IOException("File copy failed");
/*      */     }
/*      */     
/* 3394 */     if (shouldDeleteTorrentFileAfterAdd(f, persistent))
/*      */     {
/* 3396 */       f.delete();
/*      */     }
/*      */     
/* 3399 */     return fDest;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean shouldDeleteTorrentFileAfterAdd(File f, boolean persistent)
/*      */   {
/* 3407 */     if (!persistent)
/*      */     {
/* 3409 */       return false;
/*      */     }
/*      */     
/* 3412 */     boolean delTorrents = COConfigurationManager.getBooleanParameter("Delete Original Torrent Files");
/*      */     
/* 3414 */     if (!delTorrents)
/*      */     {
/* 3416 */       return false;
/*      */     }
/*      */     
/* 3419 */     boolean saveTorrents = COConfigurationManager.getBooleanParameter("Save Torrent Files");
/*      */     
/* 3421 */     if (saveTorrents) {
/*      */       try
/*      */       {
/* 3424 */         File torrentDir = new File(COConfigurationManager.getDirectoryParameter("General_sDefaultTorrent_Directory"));
/*      */         
/* 3426 */         if ((torrentDir.isDirectory()) && (torrentDir.equals(f.getParentFile())))
/*      */         {
/* 3428 */           return false;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 3432 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 3436 */     File active_dir = FileUtil.getUserFile("active");
/*      */     
/* 3438 */     return !active_dir.equals(f.getParentFile());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DownloadManager getDownloadManager(HashWrapper hash)
/*      */   {
/*      */     try
/*      */     {
/* 3449 */       return AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManager(hash);
/*      */     }
/*      */     catch (Exception e) {}
/* 3452 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void recursiveEmptyDirDelete(File f)
/*      */   {
/* 3464 */     recursiveEmptyDirDelete(f, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void recursiveEmptyDirDelete(File f, boolean log_warnings)
/*      */   {
/* 3475 */     Set ignore_map = getIgnoreSet();
/*      */     
/* 3477 */     FileUtil.recursiveEmptyDirDelete(f, ignore_map, log_warnings);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String nicePrintTorrentHash(TOTorrent torrent)
/*      */   {
/* 3487 */     return nicePrintTorrentHash(torrent, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String nicePrintTorrentHash(TOTorrent torrent, boolean tight)
/*      */   {
/*      */     byte[] hash;
/*      */     
/*      */ 
/*      */     byte[] hash;
/*      */     
/*      */ 
/* 3501 */     if (torrent == null)
/*      */     {
/* 3503 */       hash = new byte[20];
/*      */     } else {
/*      */       try {
/* 3506 */         hash = torrent.getHash();
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/* 3510 */         Debug.printStackTrace(e);
/*      */         
/* 3512 */         hash = new byte[20];
/*      */       }
/*      */     }
/*      */     
/* 3516 */     return ByteFormatter.nicePrint(hash, tight);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isTorrentFile(String filename)
/*      */     throws FileNotFoundException, IOException
/*      */   {
/* 3529 */     File check = new File(filename);
/* 3530 */     if (!check.exists())
/* 3531 */       throw new FileNotFoundException("File " + filename + " not found.");
/* 3532 */     if (!check.canRead())
/* 3533 */       throw new IOException("File " + filename + " cannot be read.");
/* 3534 */     if (check.isDirectory())
/* 3535 */       throw new FileIsADirectoryException("File " + filename + " is a directory.");
/*      */     try {
/* 3537 */       TOTorrentFactory.deserialiseFromBEncodedFile(check);
/* 3538 */       return true;
/*      */     } catch (Throwable e) {}
/* 3540 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void addCreatedTorrent(TOTorrent torrent)
/*      */   {
/* 3548 */     synchronized (created_torrents)
/*      */     {
/*      */       try {
/* 3551 */         byte[] hash = torrent.getHash();
/*      */         
/* 3553 */         HashWrapper hw = new HashWrapper(hash);
/*      */         
/* 3555 */         boolean dirty = false;
/*      */         
/* 3557 */         long check = COConfigurationManager.getLongParameter("my.created.torrents.check", 0L);
/*      */         
/* 3559 */         COConfigurationManager.setParameter("my.created.torrents.check", check + 1L);
/*      */         
/* 3561 */         if (check % 200L == 0L) {
/*      */           try
/*      */           {
/* 3564 */             List<DownloadManager> dms = AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManagers();
/*      */             
/* 3566 */             Set<HashWrapper> actual_hashes = new HashSet();
/*      */             
/* 3568 */             for (DownloadManager dm : dms)
/*      */             {
/* 3570 */               TOTorrent t = dm.getTorrent();
/*      */               
/* 3572 */               if (t != null) {
/*      */                 try
/*      */                 {
/* 3575 */                   actual_hashes.add(t.getHashWrapper());
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 3583 */             Iterator<byte[]> it = created_torrents.iterator();
/*      */             
/* 3585 */             int deleted = 0;
/*      */             
/* 3587 */             while (it.hasNext())
/*      */             {
/* 3589 */               HashWrapper existing_hw = new HashWrapper((byte[])it.next());
/*      */               
/* 3591 */               if ((!actual_hashes.contains(existing_hw)) && (!existing_hw.equals(hw)))
/*      */               {
/* 3593 */                 it.remove();
/*      */                 
/* 3595 */                 created_torrents_set.remove(existing_hw);
/*      */                 
/* 3597 */                 deleted++;
/*      */                 
/* 3599 */                 dirty = true;
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3608 */         if (created_torrents.size() == 0)
/*      */         {
/* 3610 */           COConfigurationManager.setParameter("my.created.torrents", created_torrents);
/*      */         }
/*      */         
/* 3613 */         if (!created_torrents_set.contains(hw))
/*      */         {
/* 3615 */           created_torrents.add(hash);
/*      */           
/* 3617 */           created_torrents_set.add(hw);
/*      */           
/* 3619 */           dirty = true;
/*      */         }
/*      */         
/* 3622 */         if (dirty)
/*      */         {
/* 3624 */           COConfigurationManager.setDirty();
/*      */         }
/*      */       }
/*      */       catch (TOTorrentException e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void removeCreatedTorrent(TOTorrent torrent)
/*      */   {
/* 3636 */     synchronized (created_torrents)
/*      */     {
/*      */       try {
/* 3639 */         HashWrapper hw = torrent.getHashWrapper();
/*      */         
/* 3641 */         byte[] hash = hw.getBytes();
/*      */         
/*      */ 
/*      */ 
/* 3645 */         Iterator<byte[]> it = created_torrents.iterator();
/*      */         
/* 3647 */         while (it.hasNext())
/*      */         {
/* 3649 */           byte[] h = (byte[])it.next();
/*      */           
/* 3651 */           if (Arrays.equals(hash, h))
/*      */           {
/* 3653 */             it.remove();
/*      */           }
/*      */         }
/*      */         
/* 3657 */         COConfigurationManager.setDirty();
/*      */         
/* 3659 */         created_torrents_set.remove(hw);
/*      */       }
/*      */       catch (TOTorrentException e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isCreatedTorrent(TOTorrent torrent)
/*      */   {
/* 3671 */     synchronized (created_torrents)
/*      */     {
/*      */       try {
/* 3674 */         HashWrapper hw = torrent.getHashWrapper();
/*      */         
/* 3676 */         boolean res = created_torrents_set.contains(hw);
/*      */         
/*      */ 
/*      */ 
/* 3680 */         if (!res)
/*      */         {
/* 3682 */           res = torrent.isCreated();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3687 */         return res;
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/* 3691 */         Debug.printStackTrace(e);
/*      */         
/* 3693 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static TOTorrent download(URL url)
/*      */     throws IOException
/*      */   {
/* 3705 */     return download(url, 0L);
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
/*      */   private static void fireAttributeListener(TOTorrent torrent, String attribute, Object value)
/*      */   {
/* 3770 */     Iterator it = torrent_attribute_listeners.iterator();
/*      */     
/* 3772 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 3775 */         ((torrentAttributeListener)it.next()).attributeSet(torrent, attribute, value);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3779 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void addTorrentAttributeListener(torrentAttributeListener listener)
/*      */   {
/* 3788 */     torrent_attribute_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void removeTorrentAttributeListener(torrentAttributeListener listener)
/*      */   {
/* 3795 */     torrent_attribute_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void addTorrentURLChangeListener(TorrentAnnounceURLChangeListener listener)
/*      */   {
/* 3802 */     torrent_url_changed_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void removeTorrentURLChangeListener(TorrentAnnounceURLChangeListener listener)
/*      */   {
/* 3809 */     torrent_url_changed_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/* 3814 */   private static final Pattern txt_pattern = Pattern.compile("(UDP|TCP):([0-9]+)");
/*      */   
/*      */ 
/*      */ 
/*      */   private static DNSTXTEntry getDNSTXTEntry(URL url)
/*      */   {
/* 3820 */     if (isDecentralised(url))
/*      */     {
/* 3822 */       return null;
/*      */     }
/*      */     
/* 3825 */     String host = url.getHost();
/*      */     
/* 3827 */     String tracker_network = AENetworkClassifier.categoriseAddress(host);
/*      */     
/* 3829 */     if (tracker_network != "Public")
/*      */     {
/* 3831 */       return null;
/*      */     }
/*      */     
/* 3834 */     return getDNSTXTEntry(host, false, null);
/*      */   }
/*      */   
/*      */ 
/*      */   private static void checkDNSTimeouts()
/*      */   {
/* 3840 */     final List<String> hosts = new ArrayList();
/*      */     
/* 3842 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 3844 */     synchronized (dns_mapping)
/*      */     {
/* 3846 */       for (Map.Entry<String, DNSTXTEntry> entry : dns_mapping.entrySet())
/*      */       {
/* 3848 */         DNSTXTEntry txt_entry = (DNSTXTEntry)entry.getValue();
/*      */         
/* 3850 */         if (now - txt_entry.getCreateTime() > 14400000L)
/*      */         {
/* 3852 */           hosts.add(entry.getKey());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3857 */     if (hosts.size() > 0)
/*      */     {
/* 3859 */       new AEThread2("DNS:updates")
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/* 3864 */           for (String host : hosts)
/*      */           {
/* 3866 */             TorrentUtils.getDNSTXTEntry(host, true, null);
/*      */           }
/*      */         }
/*      */       }.start();
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
/*      */   private static DNSTXTEntry getDNSTXTEntry(String host, boolean force_update, final List<String> already_got_records)
/*      */   {
/* 3886 */     boolean is_new = false;
/*      */     DNSTXTEntry txt_entry;
/* 3888 */     DNSTXTEntry old_txt_entry; synchronized (dns_mapping)
/*      */     {
/* 3890 */       old_txt_entry = txt_entry = (DNSTXTEntry)dns_mapping.get(host);
/*      */       
/* 3892 */       if ((txt_entry != null) && (SystemTime.getMonotonousTime() - txt_entry.getCreateTime() > 14400000L))
/*      */       {
/* 3894 */         force_update = true;
/*      */       }
/*      */       
/* 3897 */       if ((force_update) || (txt_entry == null))
/*      */       {
/* 3899 */         txt_entry = new DNSTXTEntry(null);
/*      */         
/* 3901 */         dns_mapping.put(host, txt_entry);
/*      */         
/* 3903 */         is_new = true;
/*      */       }
/*      */     }
/*      */     
/* 3907 */     if (is_new)
/*      */     {
/* 3909 */       String _config_key = "";
/*      */       try
/*      */       {
/* 3912 */         _config_key = "dns.txts.cache." + Base32.encode(host.getBytes("UTF-8"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3916 */         Debug.out(e);
/*      */       }
/*      */       
/* 3919 */       final String config_key = _config_key;
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*      */         List<String> txts;
/*      */         
/*      */         List<String> txts;
/*      */         
/* 3928 */         if (already_got_records != null)
/*      */         {
/* 3930 */           txts = already_got_records;
/*      */         }
/*      */         else
/*      */         {
/* 3934 */           final AESemaphore lookup_sem = new AESemaphore("DU:ls");
/*      */           
/* 3936 */           final Object[] result = { null, null };
/*      */           
/* 3938 */           final DNSTXTEntry f_txt_entry = txt_entry;
/*      */           
/* 3940 */           dns_threads.run(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */               try
/*      */               {
/* 3947 */                 List<String> txts = TorrentUtils.dns_utils.getTXTRecords(this.val$host);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3953 */                 synchronized (result)
/*      */                 {
/* 3955 */                   if (result[0] == null)
/*      */                   {
/* 3957 */                     result[1] = txts; return;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 try
/*      */                 {
/* 3966 */                   List txts_cache = new ArrayList();
/*      */                   
/* 3968 */                   for (String str : txts)
/*      */                   {
/* 3970 */                     txts_cache.add(str.getBytes("UTF-8"));
/*      */                   }
/*      */                   
/* 3973 */                   List old_txts_cache = COConfigurationManager.getListParameter(config_key, null);
/*      */                   
/* 3975 */                   boolean same = false;
/*      */                   
/* 3977 */                   if (old_txts_cache != null)
/*      */                   {
/* 3979 */                     same = old_txts_cache.size() == txts_cache.size();
/*      */                     
/* 3981 */                     if (same)
/*      */                     {
/* 3983 */                       for (int i = 0; i < old_txts_cache.size(); i++)
/*      */                       {
/* 3985 */                         if (!Arrays.equals((byte[])old_txts_cache.get(i), (byte[])txts_cache.get(i)))
/*      */                         {
/* 3987 */                           same = false;
/*      */                           
/* 3989 */                           break;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/* 3995 */                   if (!same)
/*      */                   {
/* 3997 */                     COConfigurationManager.setParameter(config_key, txts_cache);
/*      */                     
/* 3999 */                     TorrentUtils.DNSTXTEntry.access$1100(f_txt_entry).reserve();
/*      */                     
/* 4001 */                     if (already_got_records == null)
/*      */                     {
/* 4003 */                       TorrentUtils.getDNSTXTEntry(this.val$host, true, txts);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/* 4008 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */               finally
/*      */               {
/* 4013 */                 lookup_sem.release();
/*      */               }
/*      */               
/*      */             }
/* 4017 */           });
/* 4018 */           List txts_cache = COConfigurationManager.getListParameter(config_key, null);
/*      */           
/*      */ 
/*      */ 
/* 4022 */           if ((old_txt_entry != null) || (txts_cache == null) || (force_update))
/*      */           {
/* 4024 */             lookup_sem.reserve(2500L);
/*      */           }
/*      */           
/* 4027 */           synchronized (result)
/*      */           {
/* 4029 */             result[0] = "";
/*      */             
/* 4031 */             txts = (List)result[1];
/*      */           }
/*      */           try
/*      */           {
/* 4035 */             if (txts == null)
/*      */             {
/* 4037 */               txts = new ArrayList();
/*      */               
/* 4039 */               if (txts_cache != null)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4046 */                 for (Object o : txts_cache)
/*      */                 {
/* 4048 */                   txts.add(new String((byte[])o, "UTF-8"));
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 4057 */               txts_cache = new ArrayList();
/*      */               
/* 4059 */               for (String str : txts)
/*      */               {
/* 4061 */                 txts_cache.add(str.getBytes("UTF-8"));
/*      */               }
/*      */               
/* 4064 */               COConfigurationManager.setParameter(config_key, txts_cache);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 4068 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/* 4072 */         boolean found_bt = false;
/*      */         
/* 4074 */         for (String txt : txts)
/*      */         {
/* 4076 */           if (txt.startsWith("BITTORRENT"))
/*      */           {
/* 4078 */             found_bt = true;
/*      */             
/* 4080 */             Matcher matcher = txt_pattern.matcher(txt.substring(10));
/*      */             
/* 4082 */             while (matcher.find())
/*      */             {
/* 4084 */               boolean is_tcp = matcher.group(1).startsWith("T");
/* 4085 */               Integer port = Integer.valueOf(Integer.parseInt(matcher.group(2)));
/*      */               
/* 4087 */               txt_entry.addPort(is_tcp, port.intValue());
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 4092 */         txt_entry.setHasRecords(found_bt);
/*      */         
/* 4094 */         if (old_txt_entry == null)
/*      */         {
/* 4096 */           dns_mapping_seq_count += 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/* 4103 */         else if (!old_txt_entry.sameAs(txt_entry))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4109 */           dns_mapping_seq_count += 1;
/*      */           
/* 4111 */           dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 4117 */               for (TorrentUtils.TorrentAnnounceURLChangeListener l : TorrentUtils.torrent_url_changed_listeners) {
/*      */                 try
/*      */                 {
/* 4120 */                   l.changed();
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 4124 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 4133 */         txt_entry.getSemaphore().releaseForever();
/*      */       }
/*      */     }
/*      */     
/* 4137 */     txt_entry.getSemaphore().reserve();
/*      */     
/* 4139 */     return txt_entry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static URL applyDNSMods(URL url)
/*      */   {
/* 4146 */     if (DNS_HANDLING_ENABLE)
/*      */     {
/* 4148 */       DNSTXTEntry txt_entry = getDNSTXTEntry(url);
/*      */       
/* 4150 */       if ((txt_entry != null) && (txt_entry.hasRecords()))
/*      */       {
/* 4152 */         boolean url_is_tcp = url.getProtocol().toLowerCase().startsWith("http");
/* 4153 */         int url_port = url.getPort();
/*      */         
/* 4155 */         if (url_port == -1)
/*      */         {
/* 4157 */           url_port = url.getDefaultPort();
/*      */         }
/*      */         
/* 4160 */         List<DNSTXTPortInfo> ports = txt_entry.getPorts();
/*      */         
/* 4162 */         if (ports.size() == 0)
/*      */         {
/* 4164 */           return UrlUtils.setHost(url, url.getHost() + ".disabled_by_tracker");
/*      */         }
/*      */         
/*      */ 
/* 4168 */         DNSTXTPortInfo first_port = (DNSTXTPortInfo)ports.get(0);
/*      */         
/* 4170 */         if (url_port != first_port.getPort())
/*      */         {
/* 4172 */           url = UrlUtils.setPort(url, first_port.getPort());
/*      */         }
/*      */         
/* 4175 */         if (url_is_tcp == first_port.isTCP())
/*      */         {
/* 4177 */           return url;
/*      */         }
/*      */         
/*      */ 
/* 4181 */         return UrlUtils.setProtocol(url, first_port.isTCP() ? "http" : "udp");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4186 */       return url;
/*      */     }
/*      */     
/*      */ 
/* 4190 */     return url;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static List<URL> applyAllDNSMods(URL url)
/*      */   {
/* 4198 */     if (DNS_HANDLING_ENABLE)
/*      */     {
/* 4200 */       DNSTXTEntry txt_entry = getDNSTXTEntry(url);
/*      */       
/* 4202 */       if ((txt_entry != null) && (txt_entry.hasRecords()))
/*      */       {
/* 4204 */         boolean url_is_tcp = url.getProtocol().toLowerCase().startsWith("http");
/* 4205 */         int url_port = url.getPort();
/*      */         
/* 4207 */         if (url_port == -1)
/*      */         {
/* 4209 */           url_port = url.getDefaultPort();
/*      */         }
/*      */         
/* 4212 */         List<DNSTXTPortInfo> ports = txt_entry.getPorts();
/*      */         
/* 4214 */         if (ports.size() == 0)
/*      */         {
/* 4216 */           return null;
/*      */         }
/*      */         
/*      */ 
/* 4220 */         List<URL> result = new ArrayList();
/*      */         
/* 4222 */         for (DNSTXTPortInfo port : ports)
/*      */         {
/* 4224 */           URL mod_url = url;
/*      */           
/* 4226 */           if (url_port != port.getPort())
/*      */           {
/* 4228 */             mod_url = UrlUtils.setPort(mod_url, port.getPort());
/*      */           }
/*      */           
/* 4231 */           if (url_is_tcp != port.isTCP())
/*      */           {
/* 4233 */             mod_url = UrlUtils.setProtocol(mod_url, port.isTCP() ? "http" : "udp");
/*      */           }
/*      */           
/* 4236 */           result.add(mod_url);
/*      */         }
/*      */         
/* 4239 */         return result;
/*      */       }
/*      */       
/*      */ 
/* 4243 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 4247 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static TOTorrentAnnounceURLGroup applyDNSMods(URL announce_url, TOTorrentAnnounceURLGroup group)
/*      */   {
/* 4256 */     if (DNS_HANDLING_ENABLE)
/*      */     {
/* 4258 */       Map<String, Object[]> dns_maps = new HashMap();
/*      */       
/* 4260 */       DNSTXTEntry announce_txt_entry = getDNSTXTEntry(announce_url);
/*      */       
/* 4262 */       if ((announce_txt_entry != null) && (announce_txt_entry.hasRecords()))
/*      */       {
/* 4264 */         dns_maps.put(announce_url.getHost(), new Object[] { announce_url, announce_txt_entry });
/*      */       }
/*      */       
/* 4267 */       TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */       
/* 4269 */       List<TOTorrentAnnounceURLSet> mod_sets = new ArrayList();
/*      */       
/* 4271 */       for (TOTorrentAnnounceURLSet set : sets)
/*      */       {
/* 4273 */         URL[] urls = set.getAnnounceURLs();
/*      */         
/* 4275 */         List<URL> mod_urls = new ArrayList();
/*      */         
/* 4277 */         for (URL url : urls)
/*      */         {
/* 4279 */           DNSTXTEntry txt_entry = getDNSTXTEntry(url);
/*      */           
/* 4281 */           if ((txt_entry == null) || (!txt_entry.hasRecords()))
/*      */           {
/* 4283 */             mod_urls.add(url);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 4289 */             dns_maps.put(url.getHost(), new Object[] { url, txt_entry });
/*      */           }
/*      */         }
/*      */         
/* 4293 */         if (mod_urls.size() != urls.length)
/*      */         {
/* 4295 */           if (mod_urls.size() > 0)
/*      */           {
/* 4297 */             mod_sets.add(group.createAnnounceURLSet((URL[])mod_urls.toArray(new URL[mod_urls.size()])));
/*      */           }
/*      */         }
/*      */         else {
/* 4301 */           mod_sets.add(set);
/*      */         }
/*      */       }
/*      */       
/* 4305 */       if (dns_maps.size() > 0)
/*      */       {
/* 4307 */         for (Map.Entry<String, Object[]> entry : dns_maps.entrySet())
/*      */         {
/* 4309 */           Object[] stuff = (Object[])entry.getValue();
/*      */           
/* 4311 */           URL url = (URL)stuff[0];
/* 4312 */           DNSTXTEntry dns = (DNSTXTEntry)stuff[1];
/*      */           
/* 4314 */           List<DNSTXTPortInfo> ports = dns.getPorts();
/*      */           
/* 4316 */           if (ports.size() > 0)
/*      */           {
/* 4318 */             List<URL> urls = new ArrayList();
/*      */             
/* 4320 */             for (DNSTXTPortInfo port : ports)
/*      */             {
/* 4322 */               int url_port = url.getPort();
/* 4323 */               boolean url_is_tcp = url.getProtocol().toLowerCase().startsWith("http");
/*      */               
/* 4325 */               if (url_port != port.getPort())
/*      */               {
/* 4327 */                 url = UrlUtils.setPort(url, port.getPort());
/*      */               }
/*      */               
/* 4330 */               if (url_is_tcp != port.isTCP())
/*      */               {
/* 4332 */                 url = UrlUtils.setProtocol(url, port.isTCP() ? "http" : "udp");
/*      */               }
/*      */               
/* 4335 */               urls.add(url);
/*      */             }
/*      */             
/* 4338 */             if (urls.size() > 0)
/*      */             {
/* 4340 */               mod_sets.add(group.createAnnounceURLSet((URL[])urls.toArray(new URL[urls.size()])));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 4345 */         return new URLGroup(group, mod_sets, null);
/*      */       }
/*      */       
/*      */ 
/* 4349 */       return group;
/*      */     }
/*      */     
/*      */ 
/* 4353 */     return group;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class URLGroup
/*      */     implements TOTorrentAnnounceURLGroup
/*      */   {
/*      */     private final TOTorrentAnnounceURLGroup delegate;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private TOTorrentAnnounceURLSet[] sets;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean modified;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private URLGroup(TOTorrentAnnounceURLGroup _delegate, List<TOTorrentAnnounceURLSet> mod_sets)
/*      */     {
/* 4388 */       this.delegate = _delegate;
/*      */       
/* 4390 */       this.sets = ((TOTorrentAnnounceURLSet[])mod_sets.toArray(new TOTorrentAnnounceURLSet[mod_sets.size()]));
/*      */     }
/*      */     
/*      */ 
/*      */     public TOTorrentAnnounceURLSet[] getAnnounceURLSets()
/*      */     {
/* 4396 */       return this.sets;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setAnnounceURLSets(TOTorrentAnnounceURLSet[] _sets)
/*      */     {
/* 4403 */       this.modified = true;
/*      */       
/* 4405 */       this.sets = _sets;
/*      */       
/* 4407 */       this.delegate.setAnnounceURLSets(_sets);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public TOTorrentAnnounceURLSet createAnnounceURLSet(URL[] urls)
/*      */     {
/* 4414 */       return this.delegate.createAnnounceURLSet(urls);
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean hasBeenModified()
/*      */     {
/* 4420 */       return this.modified;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class DNSTXTEntry
/*      */   {
/* 4427 */     private final long create_time = SystemTime.getMonotonousTime();
/*      */     
/* 4429 */     private final AESemaphore sem = new AESemaphore("DNSTXTEntry");
/*      */     
/*      */     private boolean has_records;
/* 4432 */     private final List<TorrentUtils.DNSTXTPortInfo> ports = new ArrayList();
/*      */     
/*      */ 
/*      */     private long getCreateTime()
/*      */     {
/* 4437 */       return this.create_time;
/*      */     }
/*      */     
/*      */ 
/*      */     private AESemaphore getSemaphore()
/*      */     {
/* 4443 */       return this.sem;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setHasRecords(boolean has)
/*      */     {
/* 4450 */       this.has_records = has;
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean hasRecords()
/*      */     {
/* 4456 */       return this.has_records;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addPort(boolean is_tcp, int port)
/*      */     {
/* 4464 */       this.ports.add(new TorrentUtils.DNSTXTPortInfo(is_tcp, port, null));
/*      */     }
/*      */     
/*      */ 
/*      */     private List<TorrentUtils.DNSTXTPortInfo> getPorts()
/*      */     {
/* 4470 */       return this.ports;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean sameAs(DNSTXTEntry other)
/*      */     {
/* 4477 */       if (this.has_records != other.has_records)
/*      */       {
/* 4479 */         return false;
/*      */       }
/*      */       
/* 4482 */       if (this.ports.size() != other.ports.size())
/*      */       {
/* 4484 */         return false;
/*      */       }
/*      */       
/* 4487 */       for (int i = 0; i < this.ports.size(); i++)
/*      */       {
/* 4489 */         if (!TorrentUtils.DNSTXTPortInfo.access$2100((TorrentUtils.DNSTXTPortInfo)this.ports.get(i), (TorrentUtils.DNSTXTPortInfo)other.ports.get(i)))
/*      */         {
/* 4491 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 4495 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getString()
/*      */     {
/* 4501 */       if (this.has_records)
/*      */       {
/* 4503 */         if (this.ports.size() == 0)
/*      */         {
/* 4505 */           return "Deny all";
/*      */         }
/*      */         
/*      */ 
/* 4509 */         String res = "";
/*      */         
/* 4511 */         for (TorrentUtils.DNSTXTPortInfo port : this.ports)
/*      */         {
/* 4513 */           res = res + (res.length() == 0 ? "" : ", ") + TorrentUtils.DNSTXTPortInfo.access$2200(port);
/*      */         }
/*      */         
/* 4516 */         return "Permit " + res;
/*      */       }
/*      */       
/*      */ 
/* 4520 */       return "No records";
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class DNSTXTPortInfo
/*      */   {
/*      */     private final boolean is_tcp;
/*      */     
/*      */ 
/*      */     private final int port;
/*      */     
/*      */ 
/*      */     private DNSTXTPortInfo(boolean _is_tcp, int _port)
/*      */     {
/* 4536 */       this.is_tcp = _is_tcp;
/* 4537 */       this.port = _port;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean sameAs(DNSTXTPortInfo other)
/*      */     {
/* 4544 */       return (this.is_tcp == other.is_tcp) && (this.port == other.port);
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean isTCP()
/*      */     {
/* 4550 */       return this.is_tcp;
/*      */     }
/*      */     
/*      */ 
/*      */     private int getPort()
/*      */     {
/* 4556 */       return this.port;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getString()
/*      */     {
/* 4562 */       return (this.is_tcp ? "TCP" : "UDP ") + this.port;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void startTorrentDelete()
/*      */   {
/* 4569 */     long val = torrent_delete_level.incrementAndGet();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void endTorrentDelete()
/*      */   {
/* 4577 */     long val = torrent_delete_level.decrementAndGet();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void runTorrentDelete(Runnable target)
/*      */   {
/*      */     try
/*      */     {
/* 4587 */       startTorrentDelete();
/*      */       
/* 4589 */       target.run();
/*      */     }
/*      */     finally
/*      */     {
/* 4593 */       endTorrentDelete();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isTorrentDeleting()
/*      */   {
/* 4600 */     return torrent_delete_level.get() > 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public static void setTorrentDeleted()
/*      */   {
/* 4606 */     synchronized (TorrentUtils.class)
/*      */     {
/* 4608 */       torrent_delete_time = SystemTime.getMonotonousTime();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static long getMillisecondsSinceLastTorrentDelete()
/*      */   {
/* 4615 */     synchronized (TorrentUtils.class)
/*      */     {
/* 4617 */       if (torrent_delete_time == 0L)
/*      */       {
/* 4619 */         return Long.MAX_VALUE;
/*      */       }
/* 4621 */       return SystemTime.getMonotonousTime() - torrent_delete_time;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 4631 */       URL url = new URL("http://inferno.demonoid.com:3413/announce");
/*      */       
/* 4633 */       System.out.println(applyDNSMods(url));
/*      */       
/* 4635 */       Thread.sleep(1000000L);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4639 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public static TOTorrent download(URL url, long timeout)
/*      */     throws IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aconst_null
/*      */     //   1: astore_3
/*      */     //   2: aload_0
/*      */     //   3: invokevirtual 1522	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   6: invokestatic 1574	org/gudy/azureus2/core3/util/AENetworkClassifier:categoriseAddress	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   9: ldc_w 794
/*      */     //   12: if_acmpeq +11 -> 23
/*      */     //   15: ldc_w 810
/*      */     //   18: aload_0
/*      */     //   19: invokestatic 1456	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/lang/String;Ljava/net/URL;)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*      */     //   22: astore_3
/*      */     //   23: aload_3
/*      */     //   24: ifnonnull +19 -> 43
/*      */     //   27: new 911	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFactoryImpl
/*      */     //   30: dup
/*      */     //   31: invokespecial 1671	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFactoryImpl:<init>	()V
/*      */     //   34: aload_0
/*      */     //   35: invokevirtual 1672	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFactoryImpl:create	(Ljava/net/URL;)Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;
/*      */     //   38: astore 4
/*      */     //   40: goto +41 -> 81
/*      */     //   43: new 911	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFactoryImpl
/*      */     //   46: dup
/*      */     //   47: invokespecial 1671	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFactoryImpl:<init>	()V
/*      */     //   50: aload_3
/*      */     //   51: invokeinterface 1677 1 0
/*      */     //   56: aload_3
/*      */     //   57: invokeinterface 1676 1 0
/*      */     //   62: invokevirtual 1673	org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFactoryImpl:create	(Ljava/net/URL;Ljava/net/Proxy;)Lorg/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader;
/*      */     //   65: astore 4
/*      */     //   67: aload 4
/*      */     //   69: ldc_w 801
/*      */     //   72: aload_0
/*      */     //   73: invokevirtual 1522	java/net/URL:getHost	()Ljava/lang/String;
/*      */     //   76: invokeinterface 1737 3 0
/*      */     //   81: lload_1
/*      */     //   82: lconst_0
/*      */     //   83: lcmp
/*      */     //   84: ifle +31 -> 115
/*      */     //   87: aload 4
/*      */     //   89: ldc_w 800
/*      */     //   92: lload_1
/*      */     //   93: invokestatic 1489	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */     //   96: invokeinterface 1737 3 0
/*      */     //   101: aload 4
/*      */     //   103: ldc_w 802
/*      */     //   106: lload_1
/*      */     //   107: invokestatic 1489	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */     //   110: invokeinterface 1737 3 0
/*      */     //   115: aload 4
/*      */     //   117: invokeinterface 1736 1 0
/*      */     //   122: ldc_w 757
/*      */     //   125: invokestatic 1594	org/gudy/azureus2/core3/util/FileUtil:readInputStreamAsByteArray	(Ljava/io/InputStream;I)[B
/*      */     //   128: astore 5
/*      */     //   130: aload 5
/*      */     //   132: invokestatic 1567	org/gudy/azureus2/core3/torrent/TOTorrentFactory:deserialiseFromBEncodedByteArray	([B)Lorg/gudy/azureus2/core3/torrent/TOTorrent;
/*      */     //   135: astore 6
/*      */     //   137: aload_3
/*      */     //   138: ifnull +10 -> 148
/*      */     //   141: aload_3
/*      */     //   142: iconst_1
/*      */     //   143: invokeinterface 1675 2 0
/*      */     //   148: aload 6
/*      */     //   150: areturn
/*      */     //   151: astore 7
/*      */     //   153: aload_3
/*      */     //   154: ifnull +10 -> 164
/*      */     //   157: aload_3
/*      */     //   158: iconst_1
/*      */     //   159: invokeinterface 1675 2 0
/*      */     //   164: aload 7
/*      */     //   166: athrow
/*      */     //   167: astore_3
/*      */     //   168: aload_3
/*      */     //   169: athrow
/*      */     //   170: astore_3
/*      */     //   171: new 828	java/io/IOException
/*      */     //   174: dup
/*      */     //   175: aload_3
/*      */     //   176: invokestatic 1589	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   179: invokespecial 1479	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   182: athrow
/*      */     // Line number table:
/*      */     //   Java source line #3716	-> byte code offset #0
/*      */     //   Java source line #3719	-> byte code offset #2
/*      */     //   Java source line #3721	-> byte code offset #15
/*      */     //   Java source line #3726	-> byte code offset #23
/*      */     //   Java source line #3728	-> byte code offset #27
/*      */     //   Java source line #3732	-> byte code offset #43
/*      */     //   Java source line #3734	-> byte code offset #67
/*      */     //   Java source line #3737	-> byte code offset #81
/*      */     //   Java source line #3739	-> byte code offset #87
/*      */     //   Java source line #3740	-> byte code offset #101
/*      */     //   Java source line #3743	-> byte code offset #115
/*      */     //   Java source line #3745	-> byte code offset #130
/*      */     //   Java source line #3749	-> byte code offset #137
/*      */     //   Java source line #3751	-> byte code offset #141
/*      */     //   Java source line #3749	-> byte code offset #151
/*      */     //   Java source line #3751	-> byte code offset #157
/*      */     //   Java source line #3754	-> byte code offset #167
/*      */     //   Java source line #3756	-> byte code offset #168
/*      */     //   Java source line #3758	-> byte code offset #170
/*      */     //   Java source line #3760	-> byte code offset #171
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	183	0	url	URL
/*      */     //   0	183	1	timeout	long
/*      */     //   1	157	3	plugin_proxy	com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy
/*      */     //   167	2	3	e	IOException
/*      */     //   170	6	3	e	Throwable
/*      */     //   38	3	4	rd	org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader
/*      */     //   65	51	4	rd	org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader
/*      */     //   128	3	5	bytes	byte[]
/*      */     //   151	14	7	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   2	137	151	finally
/*      */     //   151	153	151	finally
/*      */     //   0	148	167	java/io/IOException
/*      */     //   151	167	167	java/io/IOException
/*      */     //   0	148	170	java/lang/Throwable
/*      */     //   151	167	170	java/lang/Throwable
/*      */   }
/*      */   
/*      */   public static abstract interface ExtendedTorrent
/*      */     extends TOTorrent
/*      */   {
/*      */     public abstract byte[][] peekPieces()
/*      */       throws TOTorrentException;
/*      */     
/*      */     public abstract void setDiscardFluff(boolean paramBoolean);
/*      */   }
/*      */   
/*      */   public static abstract interface TorrentAnnounceURLChangeListener
/*      */   {
/*      */     public abstract void changed();
/*      */   }
/*      */   
/*      */   public static abstract interface torrentAttributeListener
/*      */   {
/*      */     public abstract void attributeSet(TOTorrent paramTOTorrent, String paramString, Object paramObject);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/TorrentUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */