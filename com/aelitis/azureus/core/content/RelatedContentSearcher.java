/*      */ package com.aelitis.azureus.core.content;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*      */ import com.aelitis.azureus.core.util.RegExUtil;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface.DHTInterface;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferHandler;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferType;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseValue;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchException;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchInstance;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchObserver;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchResult;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class RelatedContentSearcher
/*      */   implements DistributedDatabaseTransferHandler
/*      */ {
/*   93 */   private static final boolean SEARCH_CVS_ONLY_DEFAULT = System.getProperty("azureus.rcm.search.cvs.only", "0").equals("1");
/*      */   
/*      */   private static final boolean TRACE_SEARCH = false;
/*      */   
/*      */   private static final int SEARCH_MIN_SEEDS_DEFAULT = -1;
/*      */   
/*      */   private static final int SEARCH_MIN_LEECHERS_DEFAULT = -1;
/*      */   
/*      */   private static final int SEARCH_POP_MIN_SEEDS_DEFAULT = 100;
/*      */   
/*      */   private static final int SEARCH_POP_MIN_LEECHERS_DEFAULT = 25;
/*      */   
/*      */   private static final int MAX_REMOTE_SEARCH_RESULTS = 30;
/*      */   private static final int MAX_REMOTE_SEARCH_CONTACTS = 50;
/*      */   private static final int MAX_REMOTE_SEARCH_MILLIS = 25000;
/*      */   private static final int REDUCED_REMOTE_SEARCH_MILLIS = 10000;
/*      */   private static final int MAX_LOCAL_POPULAR_RESULTS = 50;
/*      */   private static final int HARVEST_MAX_BLOOMS = 50;
/*      */   private static final int HARVEST_MAX_FAILS_HISTORY = 128;
/*      */   private static final int HARVEST_BLOOM_UPDATE_MILLIS = 900000;
/*      */   private static final int HARVEST_BLOOM_DISCARD_MILLIS = 3600000;
/*      */   private static final int HARVEST_BLOOM_OP_RESET_MILLIS = 300000;
/*      */   private static final int HARVEST_BLOOM_OP_RESET_TICKS = 10;
/*      */   private static final int HARVEST_BLOOM_SE_RESET_MILLIS = 60000;
/*      */   private static final int HARVEST_BLOOM_SE_RESET_TICKS = 2;
/*      */   private static final int KEY_BLOOM_LOAD_FACTOR = 8;
/*      */   private static final int KEY_BLOOM_MIN_BITS = 1000;
/*      */   private static final int KEY_BLOOM_MAX_BITS = 50000;
/*      */   private static final int KEY_BLOOM_MAX_ENTRIES = 6250;
/*      */   private volatile BloomFilter key_bloom_with_local;
/*      */   private volatile BloomFilter key_bloom_without_local;
/*  124 */   private volatile long last_key_bloom_update = -1L;
/*      */   
/*  126 */   private Set<String> ignore_words = new HashSet();
/*      */   private ByteArrayHashMap<ForeignBloom> harvested_blooms;
/*      */   
/*  129 */   protected RelatedContentSearcher(RelatedContentManager _manager, DistributedDatabaseTransferType _transfer_type, DHTPluginInterface _dht_plugin, boolean _defer_ddb_check) { String ignore = "a, in, of, at, the, and, or, if, to, an, for, with";
/*      */     
/*  131 */     String[] lame_entries = ignore.toLowerCase(Locale.US).split(",");
/*      */     
/*  133 */     for (String entry : lame_entries)
/*      */     {
/*  135 */       entry = entry.trim();
/*      */       
/*  137 */       if (entry.length() > 0)
/*      */       {
/*  139 */         this.ignore_words.add(entry);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  146 */     this.harvested_blooms = new ByteArrayHashMap();
/*  147 */     this.harvested_fails = new ByteArrayHashMap();
/*      */     
/*  149 */     this.harvest_op_requester_bloom = BloomFilterFactory.createAddOnly(2048);
/*  150 */     this.harvest_se_requester_bloom = BloomFilterFactory.createAddRemove4Bit(512);
/*      */     
/*  152 */     this.harvest_dispatcher = new AsyncDispatcher();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  167 */     this.manager = _manager;
/*  168 */     this.transfer_type = _transfer_type;
/*  169 */     this.dht_plugin = _dht_plugin;
/*      */     
/*  171 */     if (!_defer_ddb_check)
/*      */     {
/*  173 */       checkDDB();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected DHTPluginInterface getDHTPlugin()
/*      */   {
/*  180 */     return this.dht_plugin;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void timerTick(boolean enabled, int tick_count)
/*      */   {
/*  188 */     checkDDB();
/*      */     
/*  190 */     if (enabled)
/*      */     {
/*  192 */       harvestBlooms();
/*      */       
/*  194 */       if (tick_count % 2 == 0)
/*      */       {
/*  196 */         this.harvest_se_requester_bloom = this.harvest_se_requester_bloom.getReplica();
/*      */       }
/*      */       
/*  199 */       if (tick_count % 10 == 0)
/*      */       {
/*  201 */         this.harvest_op_requester_bloom = this.harvest_op_requester_bloom.getReplica();
/*      */       }
/*      */     }
/*      */     
/*  205 */     checkKeyBloom();
/*      */     
/*  207 */     testKeyBloom();
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkDDB()
/*      */   {
/*  213 */     if (this.ddb == null) {
/*      */       try
/*      */       {
/*  216 */         List<DistributedDatabase> ddbs = DDBaseImpl.getDDBs(new String[] { this.dht_plugin.getNetwork() });
/*      */         
/*  218 */         if (ddbs.size() > 0)
/*      */         {
/*  220 */           this.ddb = ((DistributedDatabase)ddbs.get(0));
/*      */           
/*  222 */           this.ddb.addTransferHandler(this.transfer_type, this);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SearchInstance searchRCM(Map<String, Object> search_parameters, SearchObserver _observer)
/*      */     throws SearchException
/*      */   {
/*  238 */     final String term = fixupTerm((String)search_parameters.get("s"));
/*      */     
/*  240 */     boolean is_popular = isPopularity(term);
/*      */     
/*  242 */     final int min_seeds = ImportExportUtils.importInt(search_parameters, "z", is_popular ? 100 : -1);
/*  243 */     final int min_leechers = ImportExportUtils.importInt(search_parameters, "l", is_popular ? 25 : -1);
/*      */     
/*  245 */     final MySearchObserver observer = new MySearchObserver(_observer, min_seeds, min_leechers, null);
/*      */     
/*  247 */     final SearchInstance si = new SearchInstance()
/*      */     {
/*      */ 
/*      */       public void cancel()
/*      */       {
/*      */ 
/*  253 */         Debug.out("Cancelled");
/*      */       }
/*      */     };
/*      */     
/*  257 */     if (term == null)
/*      */     {
/*  259 */       observer.complete();
/*      */     }
/*      */     else
/*      */     {
/*  263 */       new AEThread2("RCM:search", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*  268 */           boolean search_cvs_only = RelatedContentSearcher.SEARCH_CVS_ONLY_DEFAULT;
/*      */           
/*  270 */           final Set<String> hashes_sync_me = new HashSet();
/*      */           
/*      */           try
/*      */           {
/*  274 */             List<RelatedContent> matches = RelatedContentSearcher.this.matchContent(term, min_seeds, min_leechers, true, search_cvs_only);
/*      */             
/*  276 */             for (final RelatedContent c : matches)
/*      */             {
/*  278 */               final byte[] hash = c.getHash();
/*      */               
/*  280 */               if (hash != null)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  285 */                 hashes_sync_me.add(Base32.encode(hash));
/*      */                 
/*  287 */                 SearchResult result = new SearchResult()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public Object getProperty(int property_name)
/*      */                   {
/*      */ 
/*  294 */                     if (property_name == 22)
/*      */                     {
/*  296 */                       return new Long(c.getVersion());
/*      */                     }
/*  298 */                     if (property_name == 1)
/*      */                     {
/*  300 */                       return c.getTitle();
/*      */                     }
/*  302 */                     if (property_name == 3)
/*      */                     {
/*  304 */                       return Long.valueOf(c.getSize());
/*      */                     }
/*  306 */                     if (property_name == 21)
/*      */                     {
/*  308 */                       return hash;
/*      */                     }
/*  310 */                     if (property_name == 17)
/*      */                     {
/*      */ 
/*      */ 
/*  314 */                       return new Long(c.getRank() / 4);
/*      */                     }
/*  316 */                     if (property_name == 5)
/*      */                     {
/*  318 */                       return new Long(c.getSeeds());
/*      */                     }
/*  320 */                     if (property_name == 4)
/*      */                     {
/*  322 */                       return new Long(c.getLeechers());
/*      */                     }
/*  324 */                     if (property_name == 6)
/*      */                     {
/*  326 */                       if (c.getContentNetwork() != -1L)
/*      */                       {
/*  328 */                         return new Long(1L);
/*      */                       }
/*      */                       
/*      */ 
/*  332 */                       return new Long(0L);
/*      */                     }
/*  334 */                     if (property_name == 2)
/*      */                     {
/*  336 */                       long date = c.getPublishDate();
/*      */                       
/*  338 */                       if (date <= 0L)
/*      */                       {
/*  340 */                         return null;
/*      */                       }
/*      */                       
/*  343 */                       return new Date(date);
/*      */                     }
/*  345 */                     if ((property_name == 23) || (property_name == 12) || (property_name == 16))
/*      */                     {
/*      */ 
/*      */ 
/*  349 */                       byte[] hash = c.getHash();
/*      */                       
/*  351 */                       if (hash != null)
/*      */                       {
/*  353 */                         return UrlUtils.getMagnetURI(hash, c.getTitle(), c.getNetworks());
/*      */                       }
/*  355 */                     } else if (property_name == 7)
/*      */                     {
/*  357 */                       String[] tags = c.getTags();
/*      */                       
/*  359 */                       if (tags != null)
/*      */                       {
/*  361 */                         for (String tag : tags)
/*      */                         {
/*  363 */                           if (!tag.startsWith("_"))
/*      */                           {
/*  365 */                             return tag;
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     } else {
/*  370 */                       if (property_name == 50000)
/*      */                       {
/*  372 */                         return Long.valueOf(c.getContentNetwork());
/*      */                       }
/*  374 */                       if (property_name == 50001)
/*      */                       {
/*  376 */                         return c.getTrackerKeys();
/*      */                       }
/*  378 */                       if (property_name == 50002)
/*      */                       {
/*  380 */                         return c.getWebSeedKeys();
/*      */                       }
/*  382 */                       if (property_name == 50003)
/*      */                       {
/*  384 */                         return c.getTags();
/*      */                       }
/*  386 */                       if (property_name == 50004)
/*      */                       {
/*  388 */                         return c.getNetworks();
/*      */                       }
/*      */                     }
/*  391 */                     return null;
/*      */                   }
/*      */                   
/*  394 */                 };
/*  395 */                 observer.resultReceived(si, result); } } } finally { try { final List<DistributedDatabaseContact> initial_hinted_contacts;
/*      */               final Set<DistributedDatabaseContact> extra_hinted_contacts;
/*      */               final LinkedList<DistributedDatabaseContact> contacts_to_search;
/*      */               final Map<InetSocketAddress, DistributedDatabaseContact> contact_map;
/*      */               Iterator i$;
/*  400 */               DistributedDatabaseContact c; DHTPluginInterface.DHTInterface[] dhts; boolean public_dht; DHTPluginInterface.DHTInterface[] arr$; int len$; int i$; DHTPluginInterface.DHTInterface dht; int network; DHTPluginContact[] contacts; DHTPluginContact[] arr$; int len$; int i$; DHTPluginContact dc; InetSocketAddress address; DistributedDatabaseContact c; DHTPluginInterface.DHTInterface[] arr$; int len$; int i$; DHTPluginInterface.DHTInterface dht; int network; DHTPluginContact[] contacts; DHTPluginContact[] arr$; int len$; int i$; DHTPluginContact dc; InetSocketAddress address; DistributedDatabaseContact c; int desired_pos; Iterator i$; DistributedDatabaseContact dc; long start; long max; final AESemaphore sem; int sent; final int[] done; final DistributedDatabaseContact contact_to_search; int i; long remaining; List<DistributedDatabaseContact> initial_hinted_contacts = RelatedContentSearcher.this.searchForeignBlooms(term);
/*  401 */               Set<DistributedDatabaseContact> extra_hinted_contacts = new HashSet();
/*      */               
/*  403 */               Collections.shuffle(initial_hinted_contacts);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  408 */               LinkedList<DistributedDatabaseContact> contacts_to_search = new LinkedList();
/*      */               
/*  410 */               Map<InetSocketAddress, DistributedDatabaseContact> contact_map = new HashMap();
/*      */               
/*  412 */               for (DistributedDatabaseContact c : initial_hinted_contacts)
/*      */               {
/*      */ 
/*      */ 
/*  416 */                 contact_map.put(c.getAddress(), c);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  421 */               if (RelatedContentSearcher.this.ddb != null)
/*      */               {
/*  423 */                 DHTPluginInterface.DHTInterface[] dhts = RelatedContentSearcher.this.dht_plugin.getDHTInterfaces();
/*      */                 
/*  425 */                 boolean public_dht = RelatedContentSearcher.this.dht_plugin.getNetwork() == "Public";
/*      */                 
/*  427 */                 for (DHTPluginInterface.DHTInterface dht : dhts)
/*      */                 {
/*  429 */                   if (!dht.isIPV6())
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*  434 */                     int network = dht.getNetwork();
/*      */                     
/*  436 */                     if ((public_dht) && (search_cvs_only) && (network != 1))
/*      */                     {
/*  438 */                       RelatedContentSearcher.logSearch("Search: ignoring main DHT");
/*      */ 
/*      */                     }
/*      */                     else
/*      */                     {
/*  443 */                       DHTPluginContact[] contacts = dht.getReachableContacts();
/*      */                       
/*  445 */                       Collections.shuffle(Arrays.asList(contacts));
/*      */                       
/*  447 */                       for (DHTPluginContact dc : contacts)
/*      */                       {
/*  449 */                         InetSocketAddress address = dc.getAddress();
/*      */                         
/*  451 */                         if (!contact_map.containsKey(address)) {
/*      */                           try
/*      */                           {
/*  454 */                             DistributedDatabaseContact c = RelatedContentSearcher.this.importContact(dc, network);
/*      */                             
/*  456 */                             contact_map.put(address, c);
/*      */                             
/*  458 */                             contacts_to_search.add(c);
/*      */                           }
/*      */                           catch (Throwable e) {}
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  467 */                 if (contact_map.size() < 50)
/*      */                 {
/*      */ 
/*      */ 
/*  471 */                   for (DHTPluginInterface.DHTInterface dht : dhts)
/*      */                   {
/*  473 */                     if (!dht.isIPV6())
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*  478 */                       int network = dht.getNetwork();
/*      */                       
/*  480 */                       if ((public_dht) && (search_cvs_only) && (network != 1))
/*      */                       {
/*  482 */                         RelatedContentSearcher.logSearch("Search: ignoring main DHT");
/*      */ 
/*      */                       }
/*      */                       else
/*      */                       {
/*  487 */                         DHTPluginContact[] contacts = dht.getRecentContacts();
/*      */                         
/*  489 */                         for (DHTPluginContact dc : contacts)
/*      */                         {
/*  491 */                           InetSocketAddress address = dc.getAddress();
/*      */                           
/*  493 */                           if (!contact_map.containsKey(address)) {
/*      */                             try
/*      */                             {
/*  496 */                               DistributedDatabaseContact c = RelatedContentSearcher.this.importContact(dc, network);
/*      */                               
/*  498 */                               contact_map.put(address, c);
/*      */                               
/*  500 */                               contacts_to_search.add(c);
/*      */                               
/*  502 */                               if (contact_map.size() >= 50) {
/*      */                                 break;
/*      */                               }
/*      */                             }
/*      */                             catch (Throwable e) {}
/*      */                           }
/*      */                         }
/*      */                         
/*      */ 
/*      */ 
/*  512 */                         if (contact_map.size() >= 50) {
/*      */                           break;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  522 */               int desired_pos = 0;
/*      */               
/*  524 */               for (DistributedDatabaseContact dc : initial_hinted_contacts)
/*      */               {
/*  526 */                 if (desired_pos < contacts_to_search.size())
/*      */                 {
/*  528 */                   contacts_to_search.add(desired_pos, dc);
/*      */                   
/*  530 */                   desired_pos += 2;
/*      */                 }
/*      */                 else
/*      */                 {
/*  534 */                   contacts_to_search.addLast(dc);
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  539 */               long start = SystemTime.getMonotonousTime();
/*  540 */               long max = 25000L;
/*      */               
/*  542 */               AESemaphore sem = new AESemaphore("RCM:rems");
/*      */               
/*  544 */               int sent = 0;
/*      */               
/*  546 */               int[] done = { 0 };
/*      */               
/*  548 */               RelatedContentSearcher.logSearch("Search starts: contacts=" + contacts_to_search.size() + ", hinted=" + initial_hinted_contacts.size());
/*      */               
/*      */ 
/*      */ 
/*      */               for (;;)
/*      */               {
/*  554 */                 if ((RelatedContentSearcher.MySearchObserver.access$800(observer) >= 200) || (SystemTime.getMonotonousTime() - start >= max))
/*      */                 {
/*      */ 
/*  557 */                   RelatedContentSearcher.logSearch("Hard limit exceeded"); return;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*  562 */                 if (sent >= 50)
/*      */                 {
/*  564 */                   RelatedContentSearcher.logSearch("Max contacts searched");
/*      */                   
/*  566 */                   break;
/*      */                 }
/*      */                 
/*      */                 DistributedDatabaseContact contact_to_search;
/*      */                 
/*  571 */                 synchronized (contacts_to_search)
/*      */                 {
/*  573 */                   if (contacts_to_search.isEmpty())
/*      */                   {
/*  575 */                     RelatedContentSearcher.logSearch("Contacts exhausted");
/*      */                     
/*  577 */                     break;
/*      */                   }
/*      */                   
/*      */ 
/*  581 */                   contact_to_search = (DistributedDatabaseContact)contacts_to_search.removeFirst();
/*      */                 }
/*      */                 
/*      */ 
/*  585 */                 new AEThread2("RCM:rems", true)
/*      */                 {
/*      */                   public void run()
/*      */                   {
/*      */                     try
/*      */                     {
/*  591 */                       RelatedContentSearcher.logSearch("Searching " + contact_to_search.getAddress());
/*      */                       
/*  593 */                       List<DistributedDatabaseContact> extra_contacts = RelatedContentSearcher.this.sendRemoteSearch(RelatedContentSearcher.2.this.val$si, hashes_sync_me, contact_to_search, RelatedContentSearcher.2.this.val$term, RelatedContentSearcher.2.this.val$min_seeds, RelatedContentSearcher.2.this.val$min_leechers, RelatedContentSearcher.2.this.val$observer);
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  600 */                       if (extra_contacts == null)
/*      */                       {
/*  602 */                         RelatedContentSearcher.logSearch("    " + contact_to_search.getAddress() + " failed");
/*      */                         
/*  604 */                         RelatedContentSearcher.this.foreignBloomFailed(contact_to_search);
/*      */                       }
/*      */                       else
/*      */                       {
/*      */                         String type;
/*      */                         String type;
/*  610 */                         if (initial_hinted_contacts.contains(contact_to_search)) {
/*  611 */                           type = "i"; } else { String type;
/*  612 */                           if (extra_hinted_contacts.contains(contact_to_search)) {
/*  613 */                             type = "e";
/*      */                           } else
/*  615 */                             type = "n";
/*      */                         }
/*  617 */                         RelatedContentSearcher.logSearch("    " + contact_to_search.getAddress() + " OK " + type + " - additional=" + extra_contacts.size());
/*      */                         
/*      */                         int insert_point;
/*      */                         
/*  621 */                         synchronized (contacts_to_search)
/*      */                         {
/*  623 */                           insert_point = 0;
/*      */                           
/*  625 */                           if (type.equals("i"))
/*      */                           {
/*  627 */                             for (int i = 0; i < contacts_to_search.size(); i++)
/*      */                             {
/*  629 */                               if (extra_hinted_contacts.contains(contacts_to_search.get(i)))
/*      */                               {
/*  631 */                                 insert_point = i + 1;
/*      */                               }
/*      */                             }
/*      */                           }
/*      */                           
/*  636 */                           for (DistributedDatabaseContact c : extra_contacts)
/*      */                           {
/*  638 */                             InetSocketAddress address = c.getAddress();
/*      */                             
/*  640 */                             if (!contact_map.containsKey(address))
/*      */                             {
/*  642 */                               RelatedContentSearcher.logSearch("        additional target: " + address);
/*      */                               
/*  644 */                               extra_hinted_contacts.add(c);
/*      */                               
/*  646 */                               contact_map.put(address, c);
/*      */                               
/*  648 */                               contacts_to_search.add(insert_point, c);
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     finally {
/*  655 */                       synchronized (done)
/*      */                       {
/*  657 */                         done[0] += 1;
/*      */                       }
/*      */                       
/*  660 */                       sem.release();
/*      */                     }
/*      */                     
/*      */                   }
/*  664 */                 }.start();
/*  665 */                 sent++;
/*      */                 
/*  667 */                 synchronized (done)
/*      */                 {
/*  669 */                   if (done[0] >= 25)
/*      */                   {
/*  671 */                     RelatedContentSearcher.logSearch("Switching to reduced time limit (1)");
/*      */                     
/*      */ 
/*      */ 
/*  675 */                     start = SystemTime.getMonotonousTime();
/*  676 */                     max = 10000L;
/*      */                     
/*  678 */                     break;
/*      */                   }
/*      */                 }
/*      */                 
/*  682 */                 if (sent > 10)
/*      */                 {
/*      */                   try
/*      */                   {
/*      */ 
/*  687 */                     Thread.sleep(250L);
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  694 */               RelatedContentSearcher.logSearch("Request dispatch complete: sent=" + sent + ", done=" + done[0]);
/*      */               
/*  696 */               for (int i = 0; i < sent; i++)
/*      */               {
/*  698 */                 if (done[0] > sent * 9 / 10)
/*      */                 {
/*  700 */                   RelatedContentSearcher.logSearch("9/10ths replied (" + done[0] + "/" + sent + "), done");
/*      */                   
/*  702 */                   break;
/*      */                 }
/*      */                 
/*  705 */                 long remaining = start + max - SystemTime.getMonotonousTime();
/*      */                 
/*  707 */                 if ((remaining > 10000L) && (done[0] >= 25))
/*      */                 {
/*      */ 
/*  710 */                   RelatedContentSearcher.logSearch("Switching to reduced time limit (2)");
/*      */                   
/*      */ 
/*      */ 
/*  714 */                   start = SystemTime.getMonotonousTime();
/*  715 */                   max = 10000L;
/*      */                 }
/*      */                 
/*  718 */                 if (remaining > 0L)
/*      */                 {
/*  720 */                   sem.reserve(250L);
/*      */                 }
/*      */                 else
/*      */                 {
/*  724 */                   RelatedContentSearcher.logSearch("Time exhausted");
/*      */                   
/*  726 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/*  731 */               RelatedContentSearcher.logSearch("Search complete");
/*      */               
/*  733 */               observer.complete();
/*      */             }
/*      */           }
/*      */         }
/*      */       }.start();
/*      */     }
/*      */     
/*  740 */     return si;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String fixupTerm(String term)
/*      */   {
/*  747 */     if (term == null)
/*      */     {
/*  749 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  755 */     if (term.contains("|"))
/*      */     {
/*  757 */       while (term.contains(" |"))
/*      */       {
/*  759 */         term = term.replaceAll(" \\|", "|");
/*      */       }
/*      */       
/*  762 */       while (term.contains("| "))
/*      */       {
/*  764 */         term = term.replaceAll("\\| ", "|");
/*      */       }
/*      */     }
/*      */     
/*  768 */     term = transformTerm(term);
/*      */     
/*  770 */     return term;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String escapeTag(String tag)
/*      */   {
/*  777 */     if (tag.contains(" "))
/*      */     {
/*  779 */       tag = tag.replaceAll(" ", "+");
/*      */     }
/*      */     
/*  782 */     return tag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String unescapeTag(String tag)
/*      */   {
/*  789 */     if (tag.contains("+"))
/*      */     {
/*  791 */       tag = tag.replaceAll("\\+", " ");
/*      */     }
/*      */     
/*  794 */     return tag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String transformTerm(String term)
/*      */   {
/*  803 */     Pattern p = Pattern.compile("\"([^\"]+)\"");
/*      */     
/*  805 */     Matcher m = p.matcher(term);
/*      */     
/*  807 */     boolean result = m.find();
/*      */     
/*  809 */     if (result)
/*      */     {
/*  811 */       StringBuffer sb = new StringBuffer();
/*      */       
/*  813 */       while (result)
/*      */       {
/*  815 */         String str = m.group(1);
/*      */         
/*  817 */         if (str.contains(" "))
/*      */         {
/*  819 */           str = str.replaceAll("\\s+", " ");
/*      */           
/*  821 */           str = "(" + str.replaceAll(" ", ".*?") + ")";
/*      */         }
/*      */         
/*  824 */         m.appendReplacement(sb, Matcher.quoteReplacement(str));
/*      */         
/*  826 */         result = m.find();
/*      */       }
/*      */       
/*  829 */       m.appendTail(sb);
/*      */       
/*  831 */       term = sb.toString();
/*      */     }
/*      */     
/*  834 */     return term;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private List<RelatedContent> matchContent(String term, int min_seeds, int min_leechers, boolean is_local, boolean search_cvs_only)
/*      */   {
/*  845 */     final boolean is_popularity = isPopularity(term);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  852 */     String[] bits = Constants.PAT_SPLIT_SPACE.split(term.toLowerCase());
/*      */     
/*  854 */     int[] bit_types = new int[bits.length];
/*  855 */     Pattern[] bit_patterns = new Pattern[bits.length];
/*      */     
/*  857 */     for (int i = 0; i < bits.length; i++)
/*      */     {
/*  859 */       String bit = bits[i] = bits[i].trim();
/*      */       
/*  861 */       if (bit.length() > 0)
/*      */       {
/*  863 */         char c = bit.charAt(0);
/*      */         
/*  865 */         if (c == '+')
/*      */         {
/*  867 */           bit_types[i] = 1;
/*      */           
/*  869 */           bit = bits[i] = bit.substring(1);
/*      */         }
/*  871 */         else if (c == '-')
/*      */         {
/*  873 */           bit_types[i] = 2;
/*      */           
/*  875 */           bit = bits[i] = bit.substring(1);
/*      */         }
/*      */         
/*  878 */         if ((bit.startsWith("(")) && (bit.endsWith(")")))
/*      */         {
/*  880 */           bit = bit.substring(1, bit.length() - 1);
/*      */           try
/*      */           {
/*  883 */             if (!RegExUtil.mightBeEvil(bit))
/*      */             {
/*  885 */               bit_patterns[i] = Pattern.compile(bit, 2);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*  889 */         } else if (bit.contains("|"))
/*      */         {
/*  891 */           if (!bit.contains("tag:")) {
/*      */             try
/*      */             {
/*  894 */               if (!RegExUtil.mightBeEvil(bit))
/*      */               {
/*  896 */                 bit_patterns[i] = Pattern.compile(bit, 2);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  905 */     Map<String, RelatedContent> result = new HashMap();
/*      */     
/*  907 */     Iterator<RelatedContentManager.DownloadInfo> it1 = getDHTInfos(search_cvs_only).iterator();
/*      */     
/*      */     Iterator<RelatedContentManager.DownloadInfo> it2;
/*      */     
/*  911 */     synchronized (this.manager.rcm_lock)
/*      */     {
/*  913 */       it2 = new ArrayList(RelatedContentManager.transient_info_cache.values()).iterator();
/*      */     }
/*      */     
/*  916 */     Iterator<RelatedContentManager.DownloadInfo> it3 = this.manager.getRelatedContentAsList().iterator();
/*      */     
/*  918 */     for (Iterator<RelatedContentManager.DownloadInfo> it : new Iterator[] { it1, it2, it3 })
/*      */     {
/*  920 */       while (it.hasNext())
/*      */       {
/*  922 */         RelatedContentManager.DownloadInfo c = (RelatedContentManager.DownloadInfo)it.next();
/*      */         
/*  924 */         if ((c.getSeeds() >= min_seeds) && (c.getLeechers() >= min_leechers))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  929 */           String title = c.getTitle();
/*  930 */           String lc_title = c.getTitle().toLowerCase();
/*      */           
/*  932 */           boolean match = true;
/*  933 */           boolean at_least_one = false;
/*      */           
/*  935 */           byte[] hash = c.getHash();
/*      */           
/*  937 */           if ((term.startsWith("hash:")) && (hash != null) && (term.substring(5).equals(Base32.encode(hash))))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  943 */             at_least_one = true;
/*      */           }
/*  945 */           else if ((title.equalsIgnoreCase(term)) && (term.trim().length() > 0))
/*      */           {
/*      */ 
/*      */ 
/*  949 */             at_least_one = true;
/*      */           }
/*      */           else
/*      */           {
/*  953 */             for (int i = 0; i < bits.length; i++)
/*      */             {
/*  955 */               String bit = bits[i];
/*      */               
/*  957 */               if (bit.length() > 0)
/*      */               {
/*      */                 boolean hit;
/*      */                 
/*  961 */                 if (bit_patterns[i] == null)
/*      */                 {
/*  963 */                   String[] sub_bits = bit.split("\\|");
/*      */                   
/*  965 */                   boolean hit = false;
/*      */                   
/*  967 */                   for (String sub_bit : sub_bits)
/*      */                   {
/*  969 */                     if (sub_bit.startsWith("tag:"))
/*      */                     {
/*  971 */                       String[] tags = c.getTags();
/*      */                       
/*  973 */                       hit = false;
/*      */                       
/*  975 */                       if ((tags != null) && (tags.length > 0))
/*      */                       {
/*  977 */                         String target_tag = sub_bit.substring(4).toLowerCase(Locale.US);
/*      */                         
/*  979 */                         target_tag = unescapeTag(target_tag);
/*      */                         
/*  981 */                         target_tag = this.manager.truncateTag(target_tag);
/*      */                         
/*  983 */                         for (String t : tags)
/*      */                         {
/*  985 */                           if (t.startsWith(target_tag))
/*      */                           {
/*  987 */                             hit = true;
/*      */                             
/*  989 */                             break;
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     else {
/*  995 */                       hit = lc_title.contains(sub_bit);
/*      */                     }
/*      */                     
/*  998 */                     if (hit) {
/*      */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/* 1005 */                   hit = bit_patterns[i].matcher(lc_title).find();
/*      */                 }
/*      */                 
/* 1008 */                 int type = bit_types[i];
/*      */                 
/* 1010 */                 if (hit)
/*      */                 {
/* 1012 */                   if (type == 2)
/*      */                   {
/* 1014 */                     match = false;
/*      */                     
/* 1016 */                     break;
/*      */                   }
/*      */                   
/*      */ 
/* 1020 */                   at_least_one = true;
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/* 1025 */                 else if (type == 2)
/*      */                 {
/* 1027 */                   at_least_one = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1031 */                   match = false;
/*      */                   
/* 1033 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1040 */           if ((match) && (at_least_one))
/*      */           {
/*      */             String key;
/*      */             String key;
/* 1044 */             if (hash != null)
/*      */             {
/* 1046 */               key = Base32.encode(hash);
/*      */             }
/*      */             else
/*      */             {
/* 1050 */               key = this.manager.getPrivateInfoKey(c);
/*      */             }
/*      */             
/* 1053 */             result.put(key, c);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1058 */     Object list = new ArrayList(result.values());
/*      */     
/* 1060 */     int max = is_local ? Integer.MAX_VALUE : is_popularity ? 50 : 30;
/*      */     
/* 1062 */     if (((List)list).size() > max)
/*      */     {
/* 1064 */       Collections.sort((List)list, new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(RelatedContent o1, RelatedContent o2)
/*      */         {
/*      */ 
/*      */ 
/* 1073 */           if (is_popularity)
/*      */           {
/* 1075 */             int v1 = o1.getVersion();
/* 1076 */             int v2 = o2.getVersion();
/*      */             
/* 1078 */             if (v1 == v2)
/*      */             {
/* 1080 */               long sl1 = o1.getSeeds() + o1.getLeechers();
/* 1081 */               long sl2 = o2.getSeeds() + o2.getLeechers();
/*      */               
/* 1083 */               long diff = sl2 - sl1;
/*      */               
/* 1085 */               if (diff < 0L)
/* 1086 */                 return -1;
/* 1087 */               if (diff > 0L) {
/* 1088 */                 return 1;
/*      */               }
/* 1090 */               return 0;
/*      */             }
/*      */             
/*      */ 
/* 1094 */             return v2 - v1;
/*      */           }
/*      */           
/*      */ 
/* 1098 */           return o2.getRank() - o1.getRank();
/*      */         }
/*      */         
/*      */ 
/* 1102 */       });
/* 1103 */       list = ((List)list).subList(0, max);
/*      */     }
/*      */     
/* 1106 */     return (List<RelatedContent>)list;
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
/*      */   protected List<DistributedDatabaseContact> sendRemoteSearch(SearchInstance si, Set<String> hashes_sync_me, final DistributedDatabaseContact contact, String term, int min_seeds, int min_leechers, final SearchObserver _observer)
/*      */   {
/* 1119 */     SearchObserver observer = new SearchObserver()
/*      */     {
/*      */       public void resultReceived(SearchInstance search, SearchResult result)
/*      */       {
/* 1123 */         RelatedContentSearcher.logSearch("    result from " + contact.getName());
/* 1124 */         _observer.resultReceived(search, result);
/*      */       }
/*      */       
/*      */       public Object getProperty(int property) {
/* 1128 */         return _observer.getProperty(property);
/*      */       }
/*      */       
/*      */       public void complete() {
/* 1132 */         _observer.complete();
/*      */       }
/*      */       
/*      */       public void cancelled() {
/* 1136 */         _observer.cancelled();
/*      */       }
/*      */     };
/*      */     try
/*      */     {
/* 1141 */       Boolean supports_duplicates = (Boolean)observer.getProperty(2);
/*      */       
/* 1143 */       Map<String, Object> request = new HashMap();
/*      */       
/* 1145 */       request.put("t", term);
/*      */       
/* 1147 */       if (SEARCH_CVS_ONLY_DEFAULT)
/*      */       {
/* 1149 */         request.put("n", "c");
/*      */       }
/*      */       
/* 1152 */       if (min_seeds > 0) {
/* 1153 */         request.put("s", Long.valueOf(min_seeds));
/*      */       }
/*      */       
/* 1156 */       if (min_leechers > 0) {
/* 1157 */         request.put("l", Long.valueOf(min_leechers));
/*      */       }
/*      */       
/* 1160 */       DistributedDatabaseKey key = this.ddb.createKey(BEncoder.encode(request));
/*      */       
/* 1162 */       DistributedDatabaseValue value = contact.read(null, this.transfer_type, key, contact.getAddress().isUnresolved() ? 20000L : 10000L);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1169 */       if (value == null)
/*      */       {
/* 1171 */         return null;
/*      */       }
/*      */       
/* 1174 */       Map<String, Object> reply = BDecoder.decode((byte[])value.getValue(byte[].class));
/*      */       
/* 1176 */       List<Map<String, Object>> list = (List)reply.get("l");
/*      */       
/* 1178 */       if (list != null)
/*      */       {
/* 1180 */         for (final Map<String, Object> map : list)
/*      */         {
/* 1182 */           final String title = ImportExportUtils.importString(map, "n");
/*      */           
/* 1184 */           final byte[] hash = (byte[])map.get("h");
/*      */           
/* 1186 */           if (hash != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1191 */             String hash_str = Base32.encode(hash);
/*      */             
/* 1193 */             synchronized (hashes_sync_me)
/*      */             {
/* 1195 */               if (hashes_sync_me.contains(hash_str))
/*      */               {
/* 1197 */                 if ((supports_duplicates != null) && (supports_duplicates.booleanValue())) {}
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/* 1204 */                 hashes_sync_me.add(hash_str);
/*      */               }
/*      */             }
/*      */             
/* 1208 */             long version = ImportExportUtils.importLong(map, "v", 0L);
/*      */             
/* 1210 */             SearchResult result = new SearchResult()
/*      */             {
/*      */ 
/*      */               public Object getProperty(int property_name)
/*      */               {
/*      */ 
/*      */                 try
/*      */                 {
/* 1218 */                   if (property_name == 22)
/*      */                   {
/* 1220 */                     return Long.valueOf(ImportExportUtils.importLong(map, "v", 0L));
/*      */                   }
/* 1222 */                   if (property_name == 1)
/*      */                   {
/* 1224 */                     return title;
/*      */                   }
/* 1226 */                   if (property_name == 3)
/*      */                   {
/* 1228 */                     return Long.valueOf(ImportExportUtils.importLong(map, "s"));
/*      */                   }
/* 1230 */                   if (property_name == 21)
/*      */                   {
/* 1232 */                     return hash;
/*      */                   }
/* 1234 */                   if (property_name == 17)
/*      */                   {
/* 1236 */                     return Long.valueOf(ImportExportUtils.importLong(map, "r") / 4L);
/*      */                   }
/* 1238 */                   if (property_name == 6)
/*      */                   {
/* 1240 */                     long cnet = ImportExportUtils.importLong(map, "c", -1L);
/*      */                     
/* 1242 */                     if (cnet == -1L)
/*      */                     {
/* 1244 */                       return Long.valueOf(0L);
/*      */                     }
/*      */                     
/*      */ 
/* 1248 */                     return Long.valueOf(1L);
/*      */                   }
/* 1250 */                   if (property_name == 5)
/*      */                   {
/* 1252 */                     return Long.valueOf(ImportExportUtils.importLong(map, "z"));
/*      */                   }
/* 1254 */                   if (property_name == 4)
/*      */                   {
/* 1256 */                     return Long.valueOf(ImportExportUtils.importLong(map, "l"));
/*      */                   }
/* 1258 */                   if (property_name == 2)
/*      */                   {
/* 1260 */                     long date = ImportExportUtils.importLong(map, "p", 0L) * 60L * 60L * 1000L;
/*      */                     
/* 1262 */                     if (date <= 0L)
/*      */                     {
/* 1264 */                       return null;
/*      */                     }
/*      */                     
/* 1267 */                     return new Date(date);
/*      */                   }
/* 1269 */                   if ((property_name == 23) || (property_name == 12) || (property_name == 16))
/*      */                   {
/*      */ 
/*      */ 
/* 1273 */                     byte[] hash = (byte[])map.get("h");
/*      */                     
/* 1275 */                     if (hash != null)
/*      */                     {
/* 1277 */                       return UrlUtils.getMagnetURI(hash, title, RelatedContentManager.convertNetworks((byte)(int)ImportExportUtils.importLong(map, "o", 1L)));
/*      */                     }
/*      */                   }
/* 1280 */                   else if (property_name == 7)
/*      */                   {
/* 1282 */                     String[] tags = RelatedContentSearcher.this.manager.decodeTags((byte[])map.get("g"));
/*      */                     
/* 1284 */                     if (tags != null)
/*      */                     {
/* 1286 */                       for (String tag : tags)
/*      */                       {
/* 1288 */                         if (!tag.startsWith("_"))
/*      */                         {
/* 1290 */                           return tag; }
/*      */                       }
/*      */                     }
/*      */                   } else {
/* 1294 */                     if (property_name == 50000)
/*      */                     {
/* 1296 */                       long cnet = ImportExportUtils.importLong(map, "c", -1L);
/*      */                       
/* 1298 */                       return Long.valueOf(cnet);
/*      */                     }
/* 1300 */                     if (property_name == 50001)
/*      */                     {
/* 1302 */                       return map.get("k");
/*      */                     }
/* 1304 */                     if (property_name == 50002)
/*      */                     {
/* 1306 */                       return map.get("w");
/*      */                     }
/* 1308 */                     if (property_name == 50003)
/*      */                     {
/* 1310 */                       return RelatedContentSearcher.this.manager.decodeTags((byte[])map.get("g"));
/*      */                     }
/* 1312 */                     if (property_name == 50004)
/*      */                     {
/* 1314 */                       return RelatedContentManager.convertNetworks((byte)(int)ImportExportUtils.importLong(map, "o", 1L));
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */                 
/* 1320 */                 return null;
/*      */               }
/*      */               
/* 1323 */             };
/* 1324 */             observer.resultReceived(si, result);
/*      */           }
/*      */         }
/*      */       }
/* 1328 */       list = (List)reply.get("c");
/*      */       
/* 1330 */       List<DistributedDatabaseContact> contacts = new ArrayList();
/*      */       
/* 1332 */       if (list != null)
/*      */       {
/* 1334 */         for (Map<String, Object> m : list) {
/*      */           try
/*      */           {
/* 1337 */             Map<String, Object> map = (Map)m.get("m");
/*      */             
/* 1339 */             if (map != null)
/*      */             {
/* 1341 */               DistributedDatabaseContact ddb_contact = this.ddb.importContact(map);
/*      */               
/* 1343 */               contacts.add(ddb_contact);
/*      */             }
/*      */             else
/*      */             {
/* 1347 */               String host = ImportExportUtils.importString(m, "a");
/*      */               
/* 1349 */               int port = ImportExportUtils.importInt(m, "p");
/*      */               
/* 1351 */               DistributedDatabaseContact ddb_contact = this.ddb.importContact(new InetSocketAddress(InetAddress.getByName(host), port), DHTTransportUDP.PROTOCOL_VERSION_MIN, contact.getDHT());
/*      */               
/*      */ 
/* 1354 */               contacts.add(ddb_contact);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1363 */       return contacts;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1367 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected BloomFilter sendRemoteFetch(DistributedDatabaseContact contact)
/*      */   {
/*      */     try
/*      */     {
/* 1376 */       Map<String, Object> request = new HashMap();
/*      */       
/* 1378 */       request.put("x", "f");
/*      */       
/* 1380 */       DistributedDatabaseKey key = this.ddb.createKey(BEncoder.encode(request));
/*      */       
/* 1382 */       DistributedDatabaseValue value = contact.read(null, this.transfer_type, key, contact.getAddress().isUnresolved() ? 15000L : 5000L);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1391 */       if (value != null)
/*      */       {
/* 1393 */         Map<String, Object> reply = BDecoder.decode((byte[])value.getValue(byte[].class));
/*      */         
/* 1395 */         Map<String, Object> m = (Map)reply.get("f");
/*      */         
/* 1397 */         if (m != null)
/*      */         {
/* 1399 */           return BloomFilterFactory.deserialiseFromMap(m);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1405 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected BloomFilter sendRemoteUpdate(ForeignBloom f_bloom)
/*      */   {
/*      */     try
/*      */     {
/* 1413 */       Map<String, Object> request = new HashMap();
/*      */       
/* 1415 */       request.put("x", "u");
/* 1416 */       request.put("s", new Long(f_bloom.getFilter().getEntryCount()));
/*      */       
/* 1418 */       DistributedDatabaseKey key = this.ddb.createKey(BEncoder.encode(request));
/*      */       
/* 1420 */       DistributedDatabaseContact contact = f_bloom.getContact();
/*      */       
/* 1422 */       DistributedDatabaseValue value = contact.read(null, this.transfer_type, key, contact.getAddress().isUnresolved() ? 15000L : 5000L);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1431 */       if (value != null)
/*      */       {
/* 1433 */         Map<String, Object> reply = BDecoder.decode((byte[])value.getValue(byte[].class));
/*      */         
/* 1435 */         Map<String, Object> m = (Map)reply.get("f");
/*      */         
/* 1437 */         if (m != null)
/*      */         {
/* 1439 */           logSearch("Bloom for " + f_bloom.getContact().getAddress() + " updated");
/*      */           
/* 1441 */           return BloomFilterFactory.deserialiseFromMap(m);
/*      */         }
/*      */         
/*      */ 
/* 1445 */         if (reply.containsKey("s"))
/*      */         {
/* 1447 */           logSearch("Bloom for " + f_bloom.getContact().getAddress() + " same size");
/*      */         }
/*      */         else
/*      */         {
/* 1451 */           logSearch("Bloom for " + f_bloom.getContact().getAddress() + " update not supported yet");
/*      */         }
/*      */         
/* 1454 */         return f_bloom.getFilter();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/* 1460 */     logSearch("Bloom for " + f_bloom.getContact().getAddress() + " update failed");
/*      */     
/* 1462 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map<String, Object> receiveRemoteRequest(DistributedDatabaseContact originator, Map<String, Object> request)
/*      */   {
/* 1470 */     Map<String, Object> response = new HashMap();
/*      */     try
/*      */     {
/* 1473 */       boolean originator_is_neighbour = false;
/*      */       
/* 1475 */       DHTPluginInterface.DHTInterface[] dhts = this.dht_plugin.getDHTInterfaces();
/*      */       
/* 1477 */       byte[] originator_id = originator.getID();
/*      */       
/* 1479 */       byte[] originator_bytes = AddressUtils.getAddressBytes(originator.getAddress());
/*      */       
/* 1481 */       for (DHTPluginInterface.DHTInterface d : dhts)
/*      */       {
/* 1483 */         List<DHTPluginContact> contacts = d.getClosestContacts(d.getID(), true);
/*      */         
/* 1485 */         for (DHTPluginContact c : contacts)
/*      */         {
/* 1487 */           if (Arrays.equals(c.getID(), originator_id))
/*      */           {
/* 1489 */             originator_is_neighbour = true;
/*      */             
/* 1491 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1495 */         if (originator_is_neighbour) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1501 */       String req_type = ImportExportUtils.importString(request, "x");
/*      */       
/* 1503 */       if (req_type != null)
/*      */       {
/* 1505 */         boolean dup = this.harvest_op_requester_bloom.contains(originator_bytes);
/*      */         
/* 1507 */         logSearch("Received remote request: " + BDecoder.decodeStrings(request) + " from " + originator.getAddress() + "/" + originator.getDHT() + ", dup=" + dup + ", bs=" + this.harvest_op_requester_bloom.getEntryCount());
/*      */         
/* 1509 */         if (!dup)
/*      */         {
/* 1511 */           this.harvest_op_requester_bloom.add(originator_bytes);
/*      */           
/* 1513 */           if (req_type.equals("f"))
/*      */           {
/* 1515 */             BloomFilter filter = getKeyBloom(!originator_is_neighbour);
/*      */             
/* 1517 */             if (filter != null)
/*      */             {
/* 1519 */               response.put("f", filter.serialiseToMap());
/*      */             }
/* 1521 */           } else if (req_type.equals("u"))
/*      */           {
/* 1523 */             BloomFilter filter = getKeyBloom(!originator_is_neighbour);
/*      */             
/* 1525 */             if (filter != null)
/*      */             {
/* 1527 */               int existing_size = ImportExportUtils.importInt(request, "s", 0);
/*      */               
/* 1529 */               if (existing_size != filter.getEntryCount())
/*      */               {
/* 1531 */                 response.put("f", filter.serialiseToMap());
/*      */               }
/*      */               else
/*      */               {
/* 1535 */                 response.put("s", new Long(existing_size));
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1543 */         int hits = this.harvest_se_requester_bloom.count(originator_bytes);
/*      */         
/* 1545 */         String term = ImportExportUtils.importString(request, "t");
/*      */         
/* 1547 */         term = fixupTerm(term);
/*      */         
/* 1549 */         String network = ImportExportUtils.importString(request, "n", "");
/*      */         
/* 1551 */         boolean search_cvs_only = network.equals("c");
/*      */         
/* 1553 */         int min_seeds = ImportExportUtils.importInt(request, "s", -1);
/* 1554 */         int min_leechers = ImportExportUtils.importInt(request, "l", -1);
/*      */         
/*      */ 
/*      */ 
/* 1558 */         logSearch("Received remote search: '" + term + "' from " + originator.getAddress() + ", hits=" + hits + ", bs=" + this.harvest_se_requester_bloom.getEntryCount());
/*      */         
/* 1560 */         if (hits < 10)
/*      */         {
/* 1562 */           this.harvest_se_requester_bloom.add(originator_bytes);
/*      */           
/* 1564 */           if (term != null)
/*      */           {
/* 1566 */             List<RelatedContent> matches = matchContent(term, min_seeds, min_leechers, false, search_cvs_only);
/*      */             
/* 1568 */             List<Map<String, Object>> l_list = new ArrayList();
/*      */             
/* 1570 */             for (int i = 0; i < matches.size(); i++)
/*      */             {
/* 1572 */               RelatedContent c = (RelatedContent)matches.get(i);
/*      */               
/* 1574 */               Map<String, Object> map = new HashMap();
/*      */               
/* 1576 */               l_list.add(map);
/*      */               
/* 1578 */               ImportExportUtils.exportLong(map, "v", c.getVersion());
/* 1579 */               ImportExportUtils.exportString(map, "n", c.getTitle());
/* 1580 */               ImportExportUtils.exportLong(map, "s", c.getSize());
/* 1581 */               ImportExportUtils.exportLong(map, "r", c.getRank());
/* 1582 */               ImportExportUtils.exportLong(map, "d", c.getLastSeenSecs());
/* 1583 */               ImportExportUtils.exportLong(map, "p", c.getPublishDate() / 3600000L);
/* 1584 */               ImportExportUtils.exportLong(map, "l", c.getLeechers());
/* 1585 */               ImportExportUtils.exportLong(map, "z", c.getSeeds());
/* 1586 */               ImportExportUtils.exportLong(map, "c", c.getContentNetwork());
/*      */               
/* 1588 */               byte[] hash = c.getHash();
/*      */               
/* 1590 */               if (hash != null)
/*      */               {
/* 1592 */                 map.put("h", hash);
/*      */               }
/*      */               
/* 1595 */               byte[] tracker_keys = c.getTrackerKeys();
/*      */               
/* 1597 */               if (tracker_keys != null) {
/* 1598 */                 map.put("k", tracker_keys);
/*      */               }
/*      */               
/* 1601 */               byte[] ws_keys = c.getWebSeedKeys();
/*      */               
/* 1603 */               if (ws_keys != null) {
/* 1604 */                 map.put("w", ws_keys);
/*      */               }
/*      */               
/* 1607 */               String[] tags = c.getTags();
/*      */               
/* 1609 */               if (tags != null) {
/* 1610 */                 map.put("g", this.manager.encodeTags(tags));
/*      */               }
/*      */               
/* 1613 */               byte nets = c.getNetworksInternal();
/*      */               
/* 1615 */               if ((nets != 0) && (nets != 1))
/*      */               {
/*      */ 
/* 1618 */                 map.put("o", new Long(nets & 0xFF));
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1624 */             response.put("l", l_list);
/*      */             
/* 1626 */             List<DistributedDatabaseContact> bloom_hits = searchForeignBlooms(term);
/*      */             
/* 1628 */             if (bloom_hits.size() > 0)
/*      */             {
/* 1630 */               List<Map> c_list = new ArrayList();
/*      */               
/* 1632 */               for (DistributedDatabaseContact c : bloom_hits)
/*      */               {
/* 1634 */                 Map m = new HashMap();
/*      */                 
/* 1636 */                 c_list.add(m);
/*      */                 
/* 1638 */                 InetSocketAddress address = c.getAddress();
/*      */                 
/* 1640 */                 if (address.isUnresolved())
/*      */                 {
/* 1642 */                   m.put("m", c.exportToMap());
/*      */                 }
/*      */                 else {
/* 1645 */                   m.put("a", address.getAddress().getHostAddress());
/*      */                   
/* 1647 */                   m.put("p", new Long(address.getPort()));
/*      */                 }
/*      */               }
/*      */               
/* 1651 */               response.put("c", c_list);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1659 */     return response;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseValue read(DistributedDatabaseContact contact, DistributedDatabaseTransferType type, DistributedDatabaseKey ddb_key)
/*      */     throws DistributedDatabaseException
/*      */   {
/* 1670 */     Object o_key = ddb_key.getKey();
/*      */     try
/*      */     {
/* 1673 */       byte[] key = (byte[])o_key;
/*      */       
/*      */ 
/*      */ 
/* 1677 */       Map<String, Object> request = BDecoder.decode(key);
/*      */       
/* 1679 */       Map<String, Object> result = receiveRemoteRequest(contact, request);
/*      */       
/* 1681 */       return this.ddb.createValue(BEncoder.encode(result));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1685 */       Debug.out(e);
/*      */     }
/* 1687 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DistributedDatabaseValue write(DistributedDatabaseContact contact, DistributedDatabaseTransferType type, DistributedDatabaseKey key, DistributedDatabaseValue value)
/*      */     throws DistributedDatabaseException
/*      */   {
/* 1700 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkKeyBloom()
/*      */   {
/* 1706 */     if ((this.last_key_bloom_update == -1L) || (SystemTime.getMonotonousTime() - this.last_key_bloom_update > 600000L))
/*      */     {
/* 1708 */       synchronized (this.manager.rcm_lock)
/*      */       {
/* 1710 */         updateKeyBloom(this.manager.loadRelatedContent());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private BloomFilter getKeyBloom(boolean include_dht_local)
/*      */   {
/* 1719 */     if (this.key_bloom_with_local == null)
/*      */     {
/* 1721 */       synchronized (this.manager.rcm_lock)
/*      */       {
/* 1723 */         updateKeyBloom(this.manager.loadRelatedContent());
/*      */       }
/*      */     }
/*      */     
/* 1727 */     if (include_dht_local)
/*      */     {
/* 1729 */       return this.key_bloom_with_local;
/*      */     }
/*      */     
/*      */ 
/* 1733 */     return this.key_bloom_without_local;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private List<String> getDHTWords(RelatedContentManager.DownloadInfo info)
/*      */   {
/* 1741 */     String title = info.getTitle();
/*      */     
/* 1743 */     title = title.toLowerCase(Locale.US);
/*      */     
/* 1745 */     char[] chars = title.toCharArray();
/*      */     
/* 1747 */     for (int i = 0; i < chars.length; i++)
/*      */     {
/* 1749 */       if (!Character.isLetterOrDigit(chars[i]))
/*      */       {
/* 1751 */         chars[i] = ' ';
/*      */       }
/*      */     }
/*      */     
/* 1755 */     String[] words = new String(chars).split(" ");
/*      */     
/* 1757 */     List<String> result = new ArrayList(words.length);
/*      */     
/* 1759 */     for (String word : words)
/*      */     {
/* 1761 */       if ((word.length() > 0) && (!this.ignore_words.contains(word)))
/*      */       {
/* 1763 */         result.add(word);
/*      */       }
/*      */     }
/*      */     
/* 1767 */     String[] tags = info.getTags();
/*      */     
/* 1769 */     if (tags != null)
/*      */     {
/* 1771 */       for (String tag : tags)
/*      */       {
/* 1773 */         tag = escapeTag(tag);
/*      */         
/*      */ 
/*      */ 
/* 1777 */         for (int i = 1; i <= tag.length(); i++)
/*      */         {
/* 1779 */           result.add("tag:" + tag.substring(0, i));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1784 */     byte[] hash = info.getHash();
/*      */     
/* 1786 */     if (hash != null)
/*      */     {
/*      */ 
/*      */ 
/* 1790 */       result.add("hash:" + Base32.encode(hash));
/*      */     }
/*      */     
/* 1793 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateKeyBloom(RelatedContentManager.ContentCache cc)
/*      */   {
/* 1800 */     synchronized (this.manager.rcm_lock)
/*      */     {
/* 1802 */       Set<String> dht_only_words = new HashSet();
/* 1803 */       Set<String> non_dht_words = new HashSet();
/*      */       
/* 1805 */       List<RelatedContentManager.DownloadInfo> dht_infos = getDHTInfos(SEARCH_CVS_ONLY_DEFAULT);
/*      */       
/* 1807 */       Iterator<RelatedContentManager.DownloadInfo> it_dht = dht_infos.iterator();
/*      */       
/* 1809 */       Iterator<RelatedContentManager.DownloadInfo> it_transient = RelatedContentManager.transient_info_cache.values().iterator();
/*      */       
/* 1811 */       Iterator<RelatedContentManager.DownloadInfo> it_rc = cc.related_content.values().iterator();
/*      */       
/* 1813 */       for (Iterator<RelatedContentManager.DownloadInfo> it : new Iterator[] { it_transient, it_rc, it_dht })
/*      */       {
/* 1815 */         while (it.hasNext())
/*      */         {
/* 1817 */           RelatedContentManager.DownloadInfo di = (RelatedContentManager.DownloadInfo)it.next();
/*      */           
/* 1819 */           List<String> words = getDHTWords(di);
/*      */           
/* 1821 */           for (String word : words)
/*      */           {
/*      */ 
/*      */ 
/* 1825 */             if (it == it_dht)
/*      */             {
/* 1827 */               if (!non_dht_words.contains(word))
/*      */               {
/* 1829 */                 dht_only_words.add(word);
/*      */               }
/*      */             }
/*      */             else {
/* 1833 */               non_dht_words.add(word);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1839 */       int all_desired_bits = (dht_only_words.size() + non_dht_words.size()) * 8;
/*      */       
/* 1841 */       all_desired_bits = Math.max(all_desired_bits, 1000);
/* 1842 */       all_desired_bits = Math.min(all_desired_bits, 50000);
/*      */       
/* 1844 */       BloomFilter all_bloom = BloomFilterFactory.createAddOnly(all_desired_bits);
/*      */       
/* 1846 */       int non_dht_desired_bits = non_dht_words.size() * 8;
/*      */       
/* 1848 */       non_dht_desired_bits = Math.max(non_dht_desired_bits, 1000);
/* 1849 */       non_dht_desired_bits = Math.min(non_dht_desired_bits, 50000);
/*      */       
/* 1851 */       BloomFilter non_dht_bloom = BloomFilterFactory.createAddOnly(non_dht_desired_bits);
/*      */       
/* 1853 */       List<String> non_dht_words_rand = new ArrayList(non_dht_words);
/*      */       
/* 1855 */       Collections.shuffle(non_dht_words_rand);
/*      */       
/* 1857 */       for (String word : non_dht_words_rand) {
/*      */         try
/*      */         {
/* 1860 */           byte[] bytes = word.getBytes("UTF8");
/*      */           
/* 1862 */           all_bloom.add(bytes);
/*      */           
/* 1864 */           if (all_bloom.getEntryCount() >= 6250) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 1869 */           if (non_dht_bloom.getEntryCount() < 6250)
/*      */           {
/* 1871 */             non_dht_bloom.add(bytes);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/* 1877 */       List<String> dht_only_words_rand = new ArrayList(dht_only_words);
/*      */       
/* 1879 */       Collections.shuffle(dht_only_words_rand);
/*      */       
/* 1881 */       for (String word : dht_only_words_rand) {
/*      */         try
/*      */         {
/* 1884 */           byte[] bytes = word.getBytes("UTF8");
/*      */           
/* 1886 */           all_bloom.add(bytes);
/*      */           
/* 1888 */           if (all_bloom.getEntryCount() >= 6250) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/* 1896 */       logSearch("blooms=" + all_bloom.getSize() + "/" + all_bloom.getEntryCount() + ", " + non_dht_bloom.getSize() + "/" + non_dht_bloom.getEntryCount() + ": rcm=" + cc.related_content.size() + ", trans=" + RelatedContentManager.transient_info_cache.size() + ", dht=" + dht_infos.size());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1902 */       this.key_bloom_with_local = all_bloom;
/* 1903 */       this.key_bloom_without_local = non_dht_bloom;
/*      */       
/* 1905 */       this.last_key_bloom_update = SystemTime.getMonotonousTime();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private List<RelatedContentManager.DownloadInfo> getDHTInfos(boolean search_cvs_only)
/*      */   {
/*      */     List<DHTPluginValue> vals;
/*      */     
/*      */     List<DHTPluginValue> vals;
/* 1915 */     if (search_cvs_only) {
/*      */       List<DHTPluginValue> vals;
/* 1917 */       if ((this.dht_plugin instanceof DHTPlugin))
/*      */       {
/* 1919 */         vals = ((DHTPlugin)this.dht_plugin).getValues(1, false);
/*      */       }
/*      */       else
/*      */       {
/* 1923 */         vals = this.dht_plugin.getValues();
/*      */       }
/*      */     }
/*      */     else {
/* 1927 */       vals = this.dht_plugin.getValues();
/*      */     }
/*      */     
/* 1930 */     Set<String> unique_keys = new HashSet();
/*      */     
/* 1932 */     List<RelatedContentManager.DownloadInfo> dht_infos = new ArrayList();
/*      */     
/* 1934 */     for (DHTPluginValue val : vals)
/*      */     {
/* 1936 */       if (!val.isLocal())
/*      */       {
/* 1938 */         byte[] bytes = val.getValue();
/*      */         
/* 1940 */         String test = new String(bytes);
/*      */         
/* 1942 */         if ((test.startsWith("d1:d")) && (test.endsWith("ee")) && (test.contains("1:h20:"))) {
/*      */           try
/*      */           {
/* 1945 */             Map map = BDecoder.decode(bytes);
/*      */             
/* 1947 */             RelatedContentManager.DownloadInfo info = this.manager.decodeInfo(map, null, 1, false, unique_keys);
/*      */             
/* 1949 */             if (info != null)
/*      */             {
/* 1951 */               dht_infos.add(info);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1960 */     return dht_infos;
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
/*      */   private ByteArrayHashMap<String> harvested_fails;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile BloomFilter harvest_op_requester_bloom;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile BloomFilter harvest_se_requester_bloom;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final AsyncDispatcher harvest_dispatcher;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final RelatedContentManager manager;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final DistributedDatabaseTransferType transfer_type;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final DHTPluginInterface dht_plugin;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DistributedDatabase ddb;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void testKeyBloom() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void harvestBlooms()
/*      */   {
/* 2070 */     this.harvest_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 2076 */         if (RelatedContentSearcher.this.harvest_dispatcher.getQueueSize() > 0)
/*      */         {
/* 2078 */           return;
/*      */         }
/*      */         
/* 2081 */         RelatedContentSearcher.ForeignBloom oldest = null;
/*      */         
/* 2083 */         synchronized (RelatedContentSearcher.this.harvested_blooms)
/*      */         {
/* 2085 */           for (RelatedContentSearcher.ForeignBloom bloom : RelatedContentSearcher.this.harvested_blooms.values())
/*      */           {
/* 2087 */             if ((oldest == null) || (bloom.getLastUpdateTime() < oldest.getLastUpdateTime()))
/*      */             {
/*      */ 
/* 2090 */               oldest = bloom;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2095 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 2097 */         if (oldest != null)
/*      */         {
/* 2099 */           if (now - oldest.getLastUpdateTime() > 900000L)
/*      */           {
/* 2101 */             DistributedDatabaseContact ddb_contact = oldest.getContact();
/*      */             
/* 2103 */             if ((now - oldest.getCreateTime() > 3600000L) && (RelatedContentSearcher.this.harvested_blooms.size() >= 25))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2110 */               RelatedContentSearcher.logSearch("Harvest: discarding " + ddb_contact.getAddress());
/*      */               
/* 2112 */               synchronized (RelatedContentSearcher.this.harvested_blooms)
/*      */               {
/* 2114 */                 RelatedContentSearcher.this.harvested_blooms.remove(ddb_contact.getID());
/*      */               }
/*      */             }
/*      */             else {
/* 2118 */               BloomFilter updated_filter = RelatedContentSearcher.this.sendRemoteUpdate(oldest);
/*      */               
/* 2120 */               if (updated_filter == null)
/*      */               {
/* 2122 */                 synchronized (RelatedContentSearcher.this.harvested_blooms)
/*      */                 {
/* 2124 */                   RelatedContentSearcher.this.harvested_blooms.remove(ddb_contact.getID());
/*      */                   
/* 2126 */                   RelatedContentSearcher.this.harvested_fails.put(ddb_contact.getID(), "");
/*      */                 }
/*      */                 
/*      */               } else {
/* 2130 */                 oldest.updateFilter(updated_filter);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2136 */         if (RelatedContentSearcher.this.harvested_blooms.size() < 50) {
/*      */           try
/*      */           {
/* 2139 */             int fail_count = 0;
/*      */             
/* 2141 */             DHTPluginInterface.DHTInterface[] dhts = RelatedContentSearcher.this.dht_plugin.getDHTInterfaces();
/*      */             
/*      */ 
/* 2144 */             for (DHTPluginInterface.DHTInterface dht : dhts)
/*      */             {
/* 2146 */               if (!dht.isIPV6())
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2151 */                 int network = dht.getNetwork();
/*      */                 
/* 2153 */                 if ((RelatedContentSearcher.SEARCH_CVS_ONLY_DEFAULT) && (network != 1))
/*      */                 {
/* 2155 */                   RelatedContentSearcher.logSearch("Harvest: ignoring main DHT");
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/* 2160 */                   DHTPluginContact[] contacts = dht.getReachableContacts();
/*      */                   
/* 2162 */                   byte[] dht_id = dht.getID();
/*      */                   
/* 2164 */                   for (DHTPluginContact contact : contacts)
/*      */                   {
/* 2166 */                     byte[] contact_id = contact.getID();
/*      */                     
/* 2168 */                     if (!Arrays.equals(dht_id, contact_id))
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2175 */                       DistributedDatabaseContact ddb_contact = RelatedContentSearcher.this.importContact(contact, network);
/*      */                       
/* 2177 */                       synchronized (RelatedContentSearcher.this.harvested_blooms)
/*      */                       {
/* 2179 */                         if (RelatedContentSearcher.this.harvested_fails.containsKey(contact_id)) {
/*      */                           continue;
/*      */                         }
/*      */                         
/*      */ 
/* 2184 */                         if (RelatedContentSearcher.this.harvested_blooms.containsKey(contact_id)) {
/*      */                           continue;
/*      */                         }
/*      */                       }
/*      */                       
/*      */ 
/* 2190 */                       BloomFilter filter = RelatedContentSearcher.this.sendRemoteFetch(ddb_contact);
/*      */                       
/* 2192 */                       RelatedContentSearcher.logSearch("harvest: " + contact.getString() + " -> " + (filter == null ? "null" : filter.getString()));
/*      */                       
/* 2194 */                       if (filter != null)
/*      */                       {
/* 2196 */                         synchronized (RelatedContentSearcher.this.harvested_blooms)
/*      */                         {
/* 2198 */                           RelatedContentSearcher.this.harvested_blooms.put(contact_id, new RelatedContentSearcher.ForeignBloom(ddb_contact, filter, null));
/*      */                         }
/*      */                         
/*      */ 
/*      */                       }
/*      */                       else
/*      */                       {
/* 2205 */                         synchronized (RelatedContentSearcher.this.harvested_blooms)
/*      */                         {
/* 2207 */                           RelatedContentSearcher.this.harvested_fails.put(contact_id, "");
/*      */                         }
/*      */                         
/* 2210 */                         fail_count++;
/*      */                         
/* 2212 */                         if (fail_count > 5) {}
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 2221 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */         
/* 2225 */         synchronized (RelatedContentSearcher.this.harvested_blooms)
/*      */         {
/* 2227 */           if (RelatedContentSearcher.this.harvested_fails.size() > 128)
/*      */           {
/* 2229 */             RelatedContentSearcher.this.harvested_fails.clear();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DistributedDatabaseContact importContact(DHTPluginContact contact, int network)
/*      */     throws DistributedDatabaseException
/*      */   {
/* 2243 */     InetSocketAddress address = contact.getAddress();
/*      */     
/* 2245 */     if (address.isUnresolved())
/*      */     {
/* 2247 */       return this.ddb.importContact(contact.exportToMap());
/*      */     }
/*      */     
/*      */ 
/* 2251 */     return this.ddb.importContact(address, DHTTransportUDP.PROTOCOL_VERSION_MIN, network == 1 ? 2 : 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void foreignBloomFailed(DistributedDatabaseContact contact)
/*      */   {
/* 2259 */     byte[] contact_id = contact.getID();
/*      */     
/* 2261 */     synchronized (this.harvested_blooms)
/*      */     {
/* 2263 */       if (this.harvested_blooms.remove(contact_id) != null)
/*      */       {
/* 2265 */         this.harvested_fails.put(contact_id, "");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean isPopularity(String term)
/*      */   {
/* 2274 */     return term.equals("(.)");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private List<DistributedDatabaseContact> searchForeignBlooms(String term)
/*      */   {
/* 2281 */     boolean is_popularity = isPopularity(term);
/*      */     
/* 2283 */     List<DistributedDatabaseContact> result = new ArrayList();
/*      */     try
/*      */     {
/* 2286 */       String[] bits = Constants.PAT_SPLIT_SPACE.split(term.toLowerCase());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2291 */       int[] bit_types = new int[bits.length];
/* 2292 */       byte[][] bit_bytes = new byte[bit_types.length][];
/* 2293 */       byte[][][] extras = new byte[bit_types.length][][];
/*      */       
/* 2295 */       for (int i = 0; i < bits.length; i++)
/*      */       {
/* 2297 */         String bit = bits[i].trim();
/*      */         
/* 2299 */         if (bit.length() > 0)
/*      */         {
/* 2301 */           char c = bit.charAt(0);
/*      */           
/* 2303 */           if (c == '+')
/*      */           {
/* 2305 */             bit_types[i] = 1;
/*      */             
/* 2307 */             bit = bit.substring(1);
/*      */           }
/* 2309 */           else if (c == '-')
/*      */           {
/* 2311 */             bit_types[i] = 2;
/*      */             
/* 2313 */             bit = bit.substring(1);
/*      */           }
/*      */           
/* 2316 */           if ((bit.startsWith("(")) && (bit.endsWith(")")))
/*      */           {
/* 2318 */             bit_types[i] = 3;
/*      */           }
/* 2320 */           else if (bit.contains("|"))
/*      */           {
/* 2322 */             String[] parts = bit.split("\\|");
/*      */             
/* 2324 */             List<String> p = new ArrayList();
/*      */             
/* 2326 */             for (String part : parts)
/*      */             {
/* 2328 */               part = part.trim();
/*      */               
/* 2330 */               if (part.length() > 0)
/*      */               {
/* 2332 */                 p.add(part);
/*      */               }
/*      */             }
/*      */             
/* 2336 */             if (p.size() == 0)
/*      */             {
/* 2338 */               bit_types[i] = 3;
/*      */             }
/*      */             else
/*      */             {
/* 2342 */               bit_types[i] = 4;
/*      */               
/* 2344 */               extras[i] = new byte[p.size()][];
/*      */               
/* 2346 */               for (int j = 0; j < p.size(); j++)
/*      */               {
/* 2348 */                 extras[i][j] = ((String)p.get(j)).getBytes("UTF8");
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 2353 */           bit_bytes[i] = bit.getBytes("UTF8");
/*      */         }
/*      */       }
/*      */       
/* 2357 */       synchronized (this.harvested_blooms)
/*      */       {
/* 2359 */         for (ForeignBloom fb : this.harvested_blooms.values())
/*      */         {
/* 2361 */           if (is_popularity)
/*      */           {
/* 2363 */             result.add(fb.getContact());
/*      */           }
/*      */           else
/*      */           {
/* 2367 */             BloomFilter filter = fb.getFilter();
/*      */             
/* 2369 */             boolean failed = false;
/* 2370 */             int matches = 0;
/*      */             
/* 2372 */             for (int i = 0; i < bit_bytes.length; i++)
/*      */             {
/* 2374 */               byte[] bit = bit_bytes[i];
/*      */               
/* 2376 */               if ((bit != null) && (bit.length != 0))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2381 */                 int type = bit_types[i];
/*      */                 
/* 2383 */                 if (type != 3)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 2388 */                   if ((type == 0) || (type == 1))
/*      */                   {
/* 2390 */                     if (filter.contains(bit))
/*      */                     {
/* 2392 */                       matches++;
/*      */                     }
/*      */                     else
/*      */                     {
/* 2396 */                       failed = true;
/*      */                       
/* 2398 */                       break;
/*      */                     }
/* 2400 */                   } else if (type == 2)
/*      */                   {
/* 2402 */                     if (!filter.contains(bit))
/*      */                     {
/* 2404 */                       matches++;
/*      */                     }
/*      */                     else
/*      */                     {
/* 2408 */                       failed = true;
/*      */                       
/* 2410 */                       break;
/*      */                     }
/* 2412 */                   } else if (type == 4)
/*      */                   {
/* 2414 */                     byte[][] parts = extras[i];
/*      */                     
/* 2416 */                     int old_matches = matches;
/*      */                     
/* 2418 */                     for (byte[] p : parts)
/*      */                     {
/* 2420 */                       if (filter.contains(p))
/*      */                       {
/* 2422 */                         matches++;
/*      */                         
/* 2424 */                         break;
/*      */                       }
/*      */                     }
/*      */                     
/* 2428 */                     if (matches == old_matches)
/*      */                     {
/* 2430 */                       failed = true;
/*      */                       
/* 2432 */                       break;
/*      */                     }
/*      */                   } }
/*      */               }
/*      */             }
/* 2437 */             if ((matches > 0) && (!failed))
/*      */             {
/* 2439 */               result.add(fb.getContact());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {
/* 2446 */       Debug.out(e);
/*      */     }
/*      */     
/* 2449 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void logSearch(String str) {}
/*      */   
/*      */ 
/*      */ 
/*      */   private static class MySearchObserver
/*      */     implements SearchObserver
/*      */   {
/*      */     private final SearchObserver observer;
/*      */     
/*      */ 
/*      */     private final int min_seeds;
/*      */     
/*      */ 
/*      */     private final int min_leechers;
/*      */     
/*      */ 
/* 2470 */     private final AtomicInteger num_results = new AtomicInteger();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private MySearchObserver(SearchObserver _observer, int _min_seeds, int _min_leechers)
/*      */     {
/* 2478 */       this.observer = _observer;
/* 2479 */       this.min_seeds = _min_seeds;
/* 2480 */       this.min_leechers = _min_leechers;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void resultReceived(SearchInstance search, SearchResult result)
/*      */     {
/* 2488 */       if (this.min_seeds > 0)
/*      */       {
/* 2490 */         Number seeds = (Number)result.getProperty(5);
/*      */         
/* 2492 */         if ((seeds != null) && (seeds.intValue() < this.min_seeds))
/*      */         {
/* 2494 */           return;
/*      */         }
/*      */       }
/*      */       
/* 2498 */       if (this.min_leechers > 0)
/*      */       {
/* 2500 */         Number leechers = (Number)result.getProperty(4);
/*      */         
/* 2502 */         if ((leechers != null) && (leechers.intValue() < this.min_leechers))
/*      */         {
/* 2504 */           return;
/*      */         }
/*      */       }
/*      */       
/* 2508 */       this.observer.resultReceived(search, result);
/*      */       
/* 2510 */       RelatedContentSearcher.logSearch("results=" + this.num_results.incrementAndGet());
/*      */     }
/*      */     
/*      */ 
/*      */     private int getResultCount()
/*      */     {
/* 2516 */       return this.num_results.get();
/*      */     }
/*      */     
/*      */ 
/*      */     public void complete()
/*      */     {
/* 2522 */       this.observer.complete();
/*      */     }
/*      */     
/*      */ 
/*      */     public void cancelled()
/*      */     {
/* 2528 */       this.observer.cancelled();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object getProperty(int property)
/*      */     {
/* 2535 */       return this.observer.getProperty(property);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class ForeignBloom
/*      */   {
/*      */     private DistributedDatabaseContact contact;
/*      */     
/*      */     private BloomFilter filter;
/*      */     
/*      */     private long created;
/*      */     
/*      */     private long last_update;
/*      */     
/*      */ 
/*      */     private ForeignBloom(DistributedDatabaseContact _contact, BloomFilter _filter)
/*      */     {
/* 2553 */       this.contact = _contact;
/* 2554 */       this.filter = _filter;
/*      */       
/* 2556 */       this.created = SystemTime.getMonotonousTime();
/*      */       
/* 2558 */       this.last_update = this.created;
/*      */     }
/*      */     
/*      */ 
/*      */     public DistributedDatabaseContact getContact()
/*      */     {
/* 2564 */       return this.contact;
/*      */     }
/*      */     
/*      */ 
/*      */     public BloomFilter getFilter()
/*      */     {
/* 2570 */       return this.filter;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCreateTime()
/*      */     {
/* 2576 */       return this.created;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getLastUpdateTime()
/*      */     {
/* 2582 */       return this.last_update;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void updateFilter(BloomFilter f)
/*      */     {
/* 2589 */       this.filter = f;
/* 2590 */       this.last_update = SystemTime.getMonotonousTime();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/RelatedContentSearcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */