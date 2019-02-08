/*      */ package com.aelitis.azureus.core.subs.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.lws.LightWeightSeed;
/*      */ import com.aelitis.azureus.core.lws.LightWeightSeedAdapter;
/*      */ import com.aelitis.azureus.core.lws.LightWeightSeedManager;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*      */ import com.aelitis.azureus.core.security.CryptoECCUtils;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionListener;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionPopularityListener;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionResultFilter;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.net.URL;
/*      */ import java.security.KeyPair;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.bouncycastle.util.encoders.Base64;
/*      */ import org.json.simple.JSONObject;
/*      */ 
/*      */ public class SubscriptionImpl implements Subscription
/*      */ {
/*      */   private static final int MAX_ASSOCIATIONS;
/*      */   private static final int MIN_RECENT_ASSOC_TO_RETAIN = 16;
/*      */   private SubscriptionManagerImpl manager;
/*      */   private byte[] public_key;
/*      */   private byte[] private_key;
/*      */   private String name;
/*      */   private String name_ex;
/*      */   private int version;
/*      */   private int az_version;
/*      */   private boolean is_public;
/*      */   private boolean is_anonymous;
/*      */   private Map singleton_details;
/*      */   private byte[] hash;
/*      */   private byte[] sig;
/*      */   private int sig_data_size;
/*      */   private int add_type;
/*      */   private long add_time;
/*      */   private boolean is_subscribed;
/*      */   private int highest_prompted_version;
/*      */   private byte[] short_id;
/*      */   private String id;
/*      */   
/*      */   static
/*      */   {
/*   79 */     int max_assoc = 256;
/*      */     try
/*      */     {
/*   82 */       max_assoc = Integer.parseInt(System.getProperty("azureus.subs.max.associations", "" + max_assoc));
/*      */     }
/*      */     catch (Throwable e) {
/*   85 */       Debug.out(e);
/*      */     }
/*      */     
/*   88 */     MAX_ASSOCIATIONS = max_assoc;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static byte[] intToBytes(int version)
/*      */   {
/*  100 */     return new byte[] { (byte)(version >> 24), (byte)(version >> 16), (byte)(version >> 8), (byte)version };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static int bytesToInt(byte[] bytes)
/*      */   {
/*  107 */     return bytes[0] << 24 & 0xFF000000 | bytes[1] << 16 & 0xFF0000 | bytes[2] << 8 & 0xFF00 | bytes[3] & 0xFF;
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
/*  141 */   private List<association> associations = new ArrayList();
/*      */   
/*      */   private int fixed_random;
/*      */   
/*  145 */   private long popularity = -1L;
/*      */   
/*  147 */   private long last_auto_upgrade_check = -1L;
/*      */   
/*      */   private boolean published;
/*      */   
/*      */   private boolean server_published;
/*      */   
/*      */   private boolean server_publication_outstanding;
/*      */   
/*      */   private boolean singleton_sp_attempted;
/*      */   
/*      */   private String local_name;
/*      */   
/*      */   private LightWeightSeed lws;
/*      */   private int lws_skip_check;
/*      */   private boolean destroyed;
/*      */   private Map history_map;
/*      */   private Map schedule_map;
/*  164 */   private Map user_data = new LightHashMap();
/*      */   
/*      */   private final SubscriptionHistoryImpl history;
/*      */   
/*      */   private String referer;
/*      */   
/*  170 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*      */   
/*      */   private Map verify_cache_details;
/*      */   
/*      */   private boolean verify_cache_result;
/*      */   private String creator_ref;
/*      */   private String category;
/*  177 */   private long tag_id = -1L;
/*      */   
/*      */ 
/*      */   private String parent;
/*      */   
/*      */ 
/*      */   protected static String getSkeletonJSON(Engine engine, int check_interval_mins)
/*      */   {
/*  185 */     JSONObject map = new JSONObject();
/*      */     
/*  187 */     map.put("engine_id", new Long(engine.getId()));
/*      */     
/*  189 */     map.put("search_term", "");
/*      */     
/*  191 */     map.put("filters", new HashMap());
/*      */     
/*  193 */     map.put("options", new HashMap());
/*      */     
/*  195 */     Map schedule = new HashMap();
/*      */     
/*  197 */     schedule.put("interval", new Long(check_interval_mins));
/*      */     
/*  199 */     List days = new ArrayList();
/*      */     
/*  201 */     for (int i = 1; i <= 7; i++)
/*      */     {
/*  203 */       days.add(String.valueOf(i));
/*      */     }
/*      */     
/*  206 */     schedule.put("days", days);
/*      */     
/*  208 */     map.put("schedule", schedule);
/*      */     
/*  210 */     embedEngines(map, engine);
/*      */     
/*  212 */     return JSONUtils.encodeToJSON(map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static String getSkeletonJSON(Engine engine, String term, String networks, int check_interval_mins)
/*      */   {
/*  222 */     JSONObject map = new JSONObject();
/*      */     
/*  224 */     map.put("engine_id", new Long(engine.getId()));
/*      */     
/*  226 */     map.put("search_term", term);
/*      */     
/*  228 */     if (networks != null)
/*      */     {
/*  230 */       map.put("networks", networks);
/*      */     }
/*      */     
/*  233 */     map.put("filters", new HashMap());
/*      */     
/*  235 */     map.put("options", new HashMap());
/*      */     
/*  237 */     Map schedule = new HashMap();
/*      */     
/*  239 */     schedule.put("interval", new Long(check_interval_mins));
/*      */     
/*  241 */     List days = new ArrayList();
/*      */     
/*  243 */     for (int i = 1; i <= 7; i++)
/*      */     {
/*  245 */       days.add(String.valueOf(i));
/*      */     }
/*      */     
/*  248 */     schedule.put("days", days);
/*      */     
/*  250 */     map.put("schedule", schedule);
/*      */     
/*  252 */     embedEngines(map, engine);
/*      */     
/*  254 */     return JSONUtils.encodeToJSON(map);
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
/*      */   protected SubscriptionImpl(SubscriptionManagerImpl _manager, String _name, boolean _public, boolean _anonymous, Map _singleton_details, String _json_content, int _add_type)
/*      */     throws SubscriptionException
/*      */   {
/*  272 */     this.manager = _manager;
/*      */     
/*  274 */     this.history_map = new HashMap();
/*      */     
/*  276 */     this.history = new SubscriptionHistoryImpl(this.manager, this);
/*      */     
/*  278 */     this.name = _name;
/*  279 */     this.is_public = _public;
/*  280 */     this.is_anonymous = _anonymous;
/*  281 */     this.singleton_details = _singleton_details;
/*      */     
/*  283 */     this.version = 1;
/*  284 */     this.az_version = 1;
/*      */     
/*  286 */     this.add_type = _add_type;
/*  287 */     this.add_time = SystemTime.getCurrentTime();
/*      */     
/*  289 */     this.is_subscribed = true;
/*      */     try
/*      */     {
/*  292 */       KeyPair kp = CryptoECCUtils.createKeys();
/*      */       
/*  294 */       this.public_key = CryptoECCUtils.keyToRawdata(kp.getPublic());
/*  295 */       this.private_key = CryptoECCUtils.keyToRawdata(kp.getPrivate());
/*      */       
/*      */ 
/*  298 */       this.fixed_random = RandomUtils.nextInt();
/*      */       
/*  300 */       init();
/*      */       
/*  302 */       String json_content = embedEngines(_json_content);
/*      */       
/*  304 */       SubscriptionBodyImpl body = new SubscriptionBodyImpl(this.manager, this.name, this.is_public, this.is_anonymous, json_content, this.public_key, this.version, this.az_version, this.singleton_details);
/*      */       
/*  306 */       syncToBody(body);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  310 */       throw new SubscriptionException("Failed to create subscription", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl(SubscriptionManagerImpl _manager, Map map)
/*      */     throws IOException
/*      */   {
/*  323 */     this.manager = _manager;
/*      */     
/*  325 */     fromMap(map);
/*      */     
/*  327 */     this.history = new SubscriptionHistoryImpl(this.manager, this);
/*      */     
/*  329 */     init();
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
/*      */   protected SubscriptionImpl(SubscriptionManagerImpl _manager, SubscriptionBodyImpl _body, int _add_type, boolean _is_subscribed)
/*      */     throws SubscriptionException
/*      */   {
/*  343 */     this.manager = _manager;
/*      */     
/*  345 */     this.history_map = new HashMap();
/*      */     
/*  347 */     this.history = new SubscriptionHistoryImpl(this.manager, this);
/*      */     
/*  349 */     syncFromBody(_body);
/*      */     
/*  351 */     this.add_type = _add_type;
/*  352 */     this.add_time = SystemTime.getCurrentTime();
/*      */     
/*  354 */     this.is_subscribed = _is_subscribed;
/*      */     
/*  356 */     this.fixed_random = RandomUtils.nextInt();
/*      */     
/*  358 */     init();
/*      */     
/*  360 */     syncToBody(_body);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void syncFromBody(SubscriptionBodyImpl body)
/*      */     throws SubscriptionException
/*      */   {
/*  369 */     this.public_key = body.getPublicKey();
/*  370 */     this.version = body.getVersion();
/*  371 */     this.az_version = body.getAZVersion();
/*      */     
/*  373 */     this.name = body.getName();
/*  374 */     this.is_public = body.isPublic();
/*  375 */     this.is_anonymous = body.isAnonymous();
/*  376 */     this.singleton_details = body.getSingletonDetails();
/*      */     
/*  378 */     if (this.az_version > 1)
/*      */     {
/*  380 */       throw new SubscriptionException(MessageText.getString("subscription.version.bad", new String[] { this.name }));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void syncToBody(SubscriptionBodyImpl body)
/*      */     throws SubscriptionException
/*      */   {
/*  392 */     body.writeVuzeFile(this);
/*      */     
/*  394 */     this.hash = body.getHash();
/*  395 */     this.sig = body.getSig();
/*  396 */     this.sig_data_size = body.getSigDataSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Map toMap()
/*      */     throws IOException
/*      */   {
/*  404 */     synchronized (this)
/*      */     {
/*  406 */       Map map = new HashMap();
/*      */       
/*  408 */       map.put("name", this.name.getBytes("UTF-8"));
/*      */       
/*  410 */       map.put("public_key", this.public_key);
/*      */       
/*  412 */       map.put("version", new Long(this.version));
/*      */       
/*  414 */       map.put("az_version", new Long(this.az_version));
/*      */       
/*  416 */       map.put("is_public", new Long(this.is_public ? 1L : 0L));
/*      */       
/*  418 */       map.put("is_anonymous", new Long(this.is_anonymous ? 1L : 0L));
/*      */       
/*  420 */       if (this.singleton_details != null)
/*      */       {
/*  422 */         map.put("sin_details", this.singleton_details);
/*  423 */         map.put("spa", new Long(this.singleton_sp_attempted ? 1L : 0L));
/*      */       }
/*      */       
/*  426 */       if (this.local_name != null)
/*      */       {
/*  428 */         map.put("local_name", this.local_name);
/*      */       }
/*      */       
/*      */ 
/*  432 */       map.put("hash", this.hash);
/*  433 */       map.put("sig", this.sig);
/*  434 */       map.put("sig_data_size", new Long(this.sig_data_size));
/*      */       
/*      */ 
/*      */ 
/*  438 */       if (this.private_key != null)
/*      */       {
/*  440 */         map.put("private_key", this.private_key);
/*      */       }
/*      */       
/*  443 */       map.put("add_type", new Long(this.add_type));
/*  444 */       map.put("add_time", new Long(this.add_time));
/*      */       
/*  446 */       map.put("subscribed", new Long(this.is_subscribed ? 1L : 0L));
/*      */       
/*  448 */       map.put("pop", new Long(this.popularity));
/*      */       
/*  450 */       map.put("rand", new Long(this.fixed_random));
/*      */       
/*  452 */       map.put("hupv", new Long(this.highest_prompted_version));
/*      */       
/*  454 */       map.put("sp", new Long(this.server_published ? 1L : 0L));
/*  455 */       map.put("spo", new Long(this.server_publication_outstanding ? 1L : 0L));
/*      */       
/*  457 */       if (this.associations.size() > 0)
/*      */       {
/*  459 */         List l_assoc = new ArrayList();
/*      */         
/*  461 */         map.put("assoc", l_assoc);
/*      */         
/*  463 */         for (int i = 0; i < this.associations.size(); i++)
/*      */         {
/*  465 */           association assoc = (association)this.associations.get(i);
/*      */           
/*  467 */           Map m = new HashMap();
/*      */           
/*  469 */           l_assoc.add(m);
/*      */           
/*  471 */           m.put("h", assoc.getHash());
/*  472 */           m.put("w", new Long(assoc.getWhen()));
/*      */         }
/*      */       }
/*      */       
/*  476 */       map.put("history", this.history_map);
/*      */       
/*  478 */       if (this.creator_ref != null)
/*      */       {
/*  480 */         map.put("cref", this.creator_ref.getBytes("UTF-8"));
/*      */       }
/*      */       
/*  483 */       if (this.category != null)
/*      */       {
/*  485 */         map.put("cat", this.category.getBytes("UTF-8"));
/*      */       }
/*      */       
/*  488 */       if (this.tag_id != -1L)
/*      */       {
/*  490 */         map.put("tag", Long.valueOf(this.tag_id));
/*      */       }
/*      */       
/*  493 */       if (this.parent != null)
/*      */       {
/*  495 */         map.put("par", this.parent.getBytes("UTF-8"));
/*      */       }
/*      */       
/*  498 */       return map;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fromMap(Map map)
/*      */     throws IOException
/*      */   {
/*  508 */     this.name = new String((byte[])map.get("name"), "UTF-8");
/*  509 */     this.public_key = ((byte[])map.get("public_key"));
/*  510 */     this.private_key = ((byte[])map.get("private_key"));
/*  511 */     this.version = ((Long)map.get("version")).intValue();
/*  512 */     this.az_version = ((int)ImportExportUtils.importLong(map, "az_version", 1L));
/*  513 */     this.is_public = (((Long)map.get("is_public")).intValue() == 1);
/*  514 */     Long anon = (Long)map.get("is_anonymous");
/*  515 */     this.is_anonymous = ((anon != null) && (anon.longValue() == 1L));
/*  516 */     this.singleton_details = ((Map)map.get("sin_details"));
/*      */     
/*  518 */     this.hash = ((byte[])map.get("hash"));
/*  519 */     this.sig = ((byte[])map.get("sig"));
/*  520 */     this.sig_data_size = ((Long)map.get("sig_data_size")).intValue();
/*      */     
/*  522 */     this.fixed_random = ((Long)map.get("rand")).intValue();
/*      */     
/*  524 */     this.add_type = ((Long)map.get("add_type")).intValue();
/*  525 */     this.add_time = ((Long)map.get("add_time")).longValue();
/*      */     
/*  527 */     this.is_subscribed = (((Long)map.get("subscribed")).intValue() == 1);
/*      */     
/*  529 */     this.popularity = ((Long)map.get("pop")).longValue();
/*      */     
/*  531 */     this.highest_prompted_version = ((Long)map.get("hupv")).intValue();
/*      */     
/*  533 */     this.server_published = (((Long)map.get("sp")).intValue() == 1);
/*  534 */     this.server_publication_outstanding = (((Long)map.get("spo")).intValue() == 1);
/*      */     
/*  536 */     Long l_spa = (Long)map.get("spa");
/*      */     
/*  538 */     if (l_spa != null) {
/*  539 */       this.singleton_sp_attempted = (l_spa.longValue() == 1L);
/*      */     }
/*      */     
/*  542 */     byte[] b_local_name = (byte[])map.get("local_name");
/*      */     
/*  544 */     if (b_local_name != null)
/*      */     {
/*  546 */       this.local_name = new String(b_local_name, "UTF-8");
/*      */     }
/*      */     
/*  549 */     List l_assoc = (List)map.get("assoc");
/*      */     
/*  551 */     if (l_assoc != null)
/*      */     {
/*  553 */       for (int i = 0; i < l_assoc.size(); i++)
/*      */       {
/*  555 */         Map m = (Map)l_assoc.get(i);
/*      */         
/*  557 */         byte[] hash = (byte[])m.get("h");
/*  558 */         long when = ((Long)m.get("w")).longValue();
/*      */         
/*  560 */         this.associations.add(new association(hash, when));
/*      */       }
/*      */     }
/*      */     
/*  564 */     this.history_map = ((Map)map.get("history"));
/*      */     
/*  566 */     if (this.history_map == null)
/*      */     {
/*  568 */       this.history_map = new HashMap();
/*      */     }
/*      */     
/*  571 */     byte[] b_cref = (byte[])map.get("cref");
/*      */     
/*  573 */     if (b_cref != null)
/*      */     {
/*  575 */       this.creator_ref = new String(b_cref, "UTF-8");
/*      */     }
/*      */     
/*  578 */     byte[] b_cat = (byte[])map.get("cat");
/*      */     
/*  580 */     if (b_cat != null)
/*      */     {
/*  582 */       this.category = new String(b_cat, "UTF-8");
/*      */     }
/*      */     
/*  585 */     Long l_tag_id = (Long)map.get("tag");
/*      */     
/*  587 */     if (l_tag_id != null)
/*      */     {
/*  589 */       this.tag_id = l_tag_id.longValue();
/*      */     }
/*      */     
/*  592 */     byte[] b_parent = (byte[])map.get("par");
/*      */     
/*  594 */     if (b_parent != null)
/*      */     {
/*  596 */       this.parent = new String(b_parent, "UTF-8");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map getScheduleConfig()
/*      */   {
/*  603 */     if (this.schedule_map == null) {
/*      */       try
/*      */       {
/*  606 */         Map map = JSONUtils.decodeJSON(getJSON());
/*      */         
/*  608 */         this.schedule_map = ((Map)map.get("schedule"));
/*      */         
/*  610 */         if (this.schedule_map == null)
/*      */         {
/*  612 */           this.schedule_map = new HashMap();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  616 */         log("Failed to load schedule", e);
/*      */         
/*  618 */         this.schedule_map = new HashMap();
/*      */       }
/*      */     }
/*      */     
/*  622 */     return this.schedule_map;
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map getHistoryConfig()
/*      */   {
/*  628 */     return this.history_map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateHistoryConfig(Map _history_map, int reason)
/*      */   {
/*  636 */     this.history_map = _history_map;
/*      */     
/*  638 */     fireChanged(reason);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void upgrade(SubscriptionBodyImpl body)
/*      */     throws SubscriptionException
/*      */   {
/*  649 */     syncFromBody(body);
/*      */     
/*      */ 
/*      */ 
/*  653 */     syncToBody(body);
/*      */     
/*  655 */     fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void init()
/*      */   {
/*  661 */     this.short_id = SubscriptionBodyImpl.deriveShortID(this.public_key, this.singleton_details);
/*  662 */     this.id = null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSingleton()
/*      */   {
/*  668 */     return this.singleton_details != null;
/*      */   }
/*      */   
/*      */   public boolean isShareable()
/*      */   {
/*      */     try
/*      */     {
/*  675 */       return (getEngine().isShareable()) && (!isSingleton());
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  679 */       Debug.printStackTrace(e);
/*      */     }
/*  681 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isSearchTemplate()
/*      */   {
/*  688 */     return getName(false).startsWith("Search Template:");
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map getSingletonDetails()
/*      */   {
/*  694 */     return this.singleton_details;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getSingletonPublishAttempted()
/*      */   {
/*  700 */     return this.singleton_sp_attempted;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setSingletonPublishAttempted()
/*      */   {
/*  706 */     if (!this.singleton_sp_attempted)
/*      */     {
/*  708 */       this.singleton_sp_attempted = true;
/*      */       
/*  710 */       this.manager.configDirty(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  717 */     return getName(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getName(boolean use_local)
/*      */   {
/*  724 */     return this.local_name == null ? this.name : this.local_name;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getURI()
/*      */   {
/*  730 */     String str = "sub:?name=" + UrlUtils.encode(getName()) + "&id=" + Base32.encode(getShortID()) + "&v=" + getVersion();
/*      */     
/*  732 */     if (this.is_anonymous)
/*      */     {
/*  734 */       str = str + "&a=1";
/*      */     }
/*      */     
/*  737 */     return "azplug:?id=subscription&arg=" + UrlUtils.encode(str);
/*      */   }
/*      */   
/*      */ 
/*      */   public void requestAttention()
/*      */   {
/*  743 */     this.manager.selectSubscription(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLocalName(String str)
/*      */   {
/*  750 */     this.local_name = str;
/*      */     
/*  752 */     this.manager.configDirty(this);
/*      */     
/*  754 */     fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setName(String _name)
/*      */     throws SubscriptionException
/*      */   {
/*  763 */     if (!this.name.equals(_name))
/*      */     {
/*  765 */       boolean ok = false;
/*      */       
/*  767 */       String old_name = this.name;
/*  768 */       int old_version = this.version;
/*      */       try
/*      */       {
/*  771 */         this.name = _name;
/*      */         
/*  773 */         this.version += 1;
/*      */         
/*  775 */         SubscriptionBodyImpl body = new SubscriptionBodyImpl(this.manager, this);
/*      */         
/*  777 */         syncToBody(body);
/*      */         
/*  779 */         versionUpdated(body, false);
/*      */         
/*  781 */         ok = true;
/*      */       }
/*      */       finally
/*      */       {
/*  785 */         if (!ok)
/*      */         {
/*  787 */           this.name = old_name;
/*  788 */           this.version = old_version;
/*      */         }
/*      */       }
/*      */       
/*  792 */       fireChanged(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNameEx()
/*      */   {
/*  799 */     if (this.name_ex == null) {
/*      */       try
/*      */       {
/*  802 */         Map map = JSONUtils.decodeJSON(getJSON());
/*      */         
/*  804 */         String search_term = (String)map.get("search_term");
/*  805 */         Map filters = (Map)map.get("filters");
/*      */         
/*  807 */         Engine engine = this.manager.getEngine(this, map, true);
/*      */         
/*  809 */         String engine_name = engine.getNameEx();
/*      */         
/*  811 */         if (this.name.startsWith(engine_name))
/*      */         {
/*  813 */           this.name_ex = this.name;
/*      */         }
/*  815 */         else if (engine_name.startsWith(this.name))
/*      */         {
/*  817 */           this.name_ex = engine_name;
/*      */         }
/*      */         else
/*      */         {
/*  821 */           this.name_ex = (this.name + ": " + engine.getNameEx());
/*      */         }
/*      */         
/*  824 */         if ((search_term != null) && (search_term.length() > 0))
/*      */         {
/*  826 */           this.name_ex = (this.name_ex + ", query=" + search_term);
/*      */         }
/*      */         
/*  829 */         if ((filters != null) && (filters.size() > 0))
/*      */         {
/*  831 */           this.name_ex = (this.name_ex + ", filters=" + new SubscriptionResultFilterImpl(this, filters).getString());
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  836 */         this.name_ex = (this.name + ": " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */     
/*  840 */     return this.name_ex;
/*      */   }
/*      */   
/*      */   public String getQueryKey()
/*      */   {
/*      */     try
/*      */     {
/*  847 */       Map map = JSONUtils.decodeJSON(getJSON());
/*      */       
/*  849 */       String search_term = (String)map.get("search_term");
/*  850 */       Map filters = (Map)map.get("filters");
/*      */       
/*  852 */       Engine engine = this.manager.getEngine(this, map, true);
/*      */       
/*  854 */       String name = engine.getNameEx();
/*      */       
/*  856 */       if ((search_term != null) && (search_term.length() > 0))
/*      */       {
/*  858 */         name = name + ", query=" + search_term;
/*      */       }
/*      */       
/*  861 */       if ((filters != null) && (filters.size() > 0)) {}
/*      */       
/*  863 */       return name + ", filters=" + new SubscriptionResultFilterImpl(this, filters).getString();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  870 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getAddTime()
/*      */   {
/*  877 */     return this.add_time;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAddType()
/*      */   {
/*  883 */     return this.add_type;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPublic()
/*      */   {
/*  889 */     return this.is_public;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAnonymous()
/*      */   {
/*  895 */     return this.is_anonymous;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPublic(boolean _is_public)
/*      */     throws SubscriptionException
/*      */   {
/*  904 */     if (this.is_public != _is_public)
/*      */     {
/*  906 */       boolean ok = false;
/*      */       
/*  908 */       boolean old_public = this.is_public;
/*  909 */       int old_version = this.version;
/*      */       try
/*      */       {
/*  912 */         this.is_public = _is_public;
/*      */         
/*  914 */         this.version += 1;
/*      */         
/*  916 */         SubscriptionBodyImpl body = new SubscriptionBodyImpl(this.manager, this);
/*      */         
/*  918 */         syncToBody(body);
/*      */         
/*  920 */         versionUpdated(body, false);
/*      */         
/*  922 */         ok = true;
/*      */       }
/*      */       finally
/*      */       {
/*  926 */         if (!ok)
/*      */         {
/*  928 */           this.version = old_version;
/*  929 */           this.is_public = old_public;
/*      */         }
/*      */       }
/*      */       
/*  933 */       fireChanged(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getServerPublicationOutstanding()
/*      */   {
/*  940 */     return this.server_publication_outstanding;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setServerPublicationOutstanding()
/*      */   {
/*  946 */     if (!this.server_publication_outstanding)
/*      */     {
/*  948 */       this.server_publication_outstanding = true;
/*      */       
/*  950 */       fireChanged(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setServerPublished()
/*      */   {
/*  957 */     if ((this.server_publication_outstanding) || (!this.server_published))
/*      */     {
/*  959 */       this.server_published = true;
/*  960 */       this.server_publication_outstanding = false;
/*      */       
/*  962 */       fireChanged(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getServerPublished()
/*      */   {
/*  969 */     return this.server_published;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getJSON()
/*      */     throws SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/*  978 */       SubscriptionBodyImpl body = new SubscriptionBodyImpl(this.manager, this);
/*      */       
/*  980 */       return body.getJSON();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  984 */       this.history.setFatalError(Debug.getNestedExceptionMessage(e));
/*      */       
/*  986 */       if ((e instanceof SubscriptionException))
/*      */       {
/*  988 */         throw ((SubscriptionException)e);
/*      */       }
/*      */       
/*  991 */       throw new SubscriptionException("Failed to read subscription", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean setJSON(String _json)
/*      */     throws SubscriptionException
/*      */   {
/* 1001 */     String json = embedEngines(_json);
/*      */     
/* 1003 */     SubscriptionBodyImpl body = new SubscriptionBodyImpl(this.manager, this);
/*      */     
/* 1005 */     String old_json = body.getJSON();
/*      */     
/* 1007 */     if (!json.equals(old_json))
/*      */     {
/* 1009 */       boolean ok = false;
/*      */       
/* 1011 */       int old_version = this.version;
/*      */       try
/*      */       {
/* 1014 */         this.version += 1;
/*      */         
/* 1016 */         body.setJSON(json);
/*      */         
/* 1018 */         syncToBody(body);
/*      */         
/* 1020 */         versionUpdated(body, true);
/*      */         
/* 1022 */         this.referer = null;
/*      */         
/* 1024 */         ok = true;
/*      */       }
/*      */       finally
/*      */       {
/* 1028 */         if (!ok)
/*      */         {
/* 1030 */           this.version = old_version;
/*      */         }
/*      */       }
/*      */       
/* 1034 */       fireChanged(1);
/*      */       
/* 1036 */       return true;
/*      */     }
/*      */     
/* 1039 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String embedEngines(String json_in)
/*      */   {
/* 1048 */     Map map = JSONUtils.decodeJSON(json_in);
/*      */     
/* 1050 */     long engine_id = ((Long)map.get("engine_id")).longValue();
/*      */     
/* 1052 */     String json_out = json_in;
/*      */     
/* 1054 */     if ((engine_id >= 2147483647L) || (engine_id < 0L))
/*      */     {
/* 1056 */       Engine engine = MetaSearchManagerFactory.getSingleton().getMetaSearch().getEngine(engine_id);
/*      */       
/* 1058 */       if (engine == null)
/*      */       {
/* 1060 */         log("Private search template with id '" + engine_id + "' not found!!!!");
/*      */       }
/*      */       else {
/*      */         try
/*      */         {
/* 1065 */           embedEngines(map, engine);
/*      */           
/* 1067 */           json_out = JSONUtils.encodeToJSON(map);
/*      */           
/*      */ 
/* 1070 */           log("Embedded private search template '" + engine.getName() + "'");
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1074 */           log("Failed to embed private search template", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1079 */     return json_out;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void embedEngines(Map map, Engine engine)
/*      */   {
/* 1087 */     Map engines = new HashMap();
/*      */     
/* 1089 */     map.put("engines", engines);
/*      */     
/* 1091 */     Map engine_map = new HashMap();
/*      */     
/*      */     try
/*      */     {
/* 1095 */       String engine_str = new String(Base64.encode(BEncoder.encode(engine.exportToBencodedMap())), "UTF-8");
/*      */       
/* 1097 */       engine_map.put("content", engine_str);
/*      */       
/* 1099 */       engines.put(String.valueOf(engine.getId()), engine_map);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1103 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Engine extractEngine(Map json_map, long id)
/*      */   {
/* 1112 */     Map engines = (Map)json_map.get("engines");
/*      */     
/* 1114 */     if (engines != null)
/*      */     {
/* 1116 */       Map engine_map = (Map)engines.get(String.valueOf(id));
/*      */       
/* 1118 */       if (engine_map != null)
/*      */       {
/* 1120 */         String engine_str = (String)engine_map.get("content");
/*      */         
/*      */         try
/*      */         {
/* 1124 */           Map map = org.gudy.azureus2.core3.util.BDecoder.decode(Base64.decode(engine_str.getBytes("UTF-8")));
/*      */           
/* 1126 */           return MetaSearchManagerFactory.getSingleton().getMetaSearch().importFromBEncodedMap(map);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1130 */           log("failed to import engine", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1135 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Subscription cloneWithNewEngine(Engine engine)
/*      */     throws SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/* 1145 */       String json = getJSON();
/*      */       
/* 1147 */       Map map = JSONUtils.decodeJSON(json);
/*      */       
/* 1149 */       long id = ((Long)map.get("engine_id")).longValue();
/*      */       
/* 1151 */       if (id == engine.getId())
/*      */       {
/* 1153 */         embedEngines(map, engine);
/*      */         
/* 1155 */         SubscriptionImpl subs = new SubscriptionImpl(this.manager, getName(), engine.isPublic(), isAnonymous(), null, JSONUtils.encodeToJSON(map), 1);
/*      */         
/* 1157 */         subs = this.manager.addSubscription(subs);
/*      */         
/* 1159 */         setLocalName(getName(false) + " (old)");
/*      */         
/* 1161 */         return subs;
/*      */       }
/*      */       
/*      */ 
/* 1165 */       throw new SubscriptionException("Engine mismatch");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1169 */       throw new SubscriptionException("Failed to export engine", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Engine getEngine()
/*      */     throws SubscriptionException
/*      */   {
/* 1178 */     return getEngine(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Engine getEngine(boolean local_only)
/*      */     throws SubscriptionException
/*      */   {
/* 1187 */     Map map = JSONUtils.decodeJSON(getJSON());
/*      */     
/* 1189 */     return this.manager.getEngine(this, map, local_only);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void engineUpdated(Engine engine)
/*      */   {
/*      */     try
/*      */     {
/* 1197 */       String json = getJSON();
/*      */       
/* 1199 */       Map map = JSONUtils.decodeJSON(json);
/*      */       
/* 1201 */       long id = ((Long)map.get("engine_id")).longValue();
/*      */       
/* 1203 */       if (id == engine.getId())
/*      */       {
/* 1205 */         if (setJSON(json))
/*      */         {
/* 1207 */           log("Engine has been updated, saved");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1212 */       log("Engine update failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean setDetails(String _name, boolean _is_public, String _json)
/*      */     throws SubscriptionException
/*      */   {
/* 1224 */     _json = embedEngines(_json);
/*      */     
/* 1226 */     SubscriptionBodyImpl body = new SubscriptionBodyImpl(this.manager, this);
/*      */     
/* 1228 */     String old_json = body.getJSON();
/*      */     
/* 1230 */     boolean json_changed = !_json.equals(old_json);
/*      */     
/* 1232 */     if ((!_name.equals(this.name)) || (_is_public != this.is_public) || (json_changed))
/*      */     {
/*      */ 
/*      */ 
/* 1236 */       boolean ok = false;
/*      */       
/* 1238 */       String old_name = this.name;
/* 1239 */       boolean old_public = this.is_public;
/* 1240 */       int old_version = this.version;
/*      */       try
/*      */       {
/* 1243 */         this.is_public = _is_public;
/* 1244 */         this.name = _name;
/*      */         
/* 1246 */         body.setJSON(_json);
/*      */         
/* 1248 */         this.version += 1;
/*      */         
/* 1250 */         syncToBody(body);
/*      */         
/* 1252 */         versionUpdated(body, json_changed);
/*      */         
/* 1254 */         ok = true;
/*      */       }
/*      */       finally
/*      */       {
/* 1258 */         if (!ok)
/*      */         {
/* 1260 */           this.version = old_version;
/* 1261 */           this.is_public = old_public;
/* 1262 */           this.name = old_name;
/*      */         }
/*      */       }
/*      */       
/* 1266 */       fireChanged(1);
/*      */       
/* 1268 */       return true;
/*      */     }
/*      */     
/* 1271 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void versionUpdated(SubscriptionBodyImpl body, boolean json_changed)
/*      */   {
/* 1279 */     if (json_changed) {
/*      */       try
/*      */       {
/* 1282 */         Map map = JSONUtils.decodeJSON(body.getJSON());
/*      */         
/* 1284 */         this.schedule_map = ((Map)map.get("schedule"));
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/* 1290 */     this.name_ex = null;
/*      */     
/* 1292 */     if (this.is_public)
/*      */     {
/* 1294 */       this.manager.updatePublicSubscription(this);
/*      */       
/* 1296 */       setPublished(false);
/*      */       
/* 1298 */       synchronized (this)
/*      */       {
/* 1300 */         for (int i = 0; i < this.associations.size(); i++)
/*      */         {
/* 1302 */           ((association)this.associations.get(i)).setPublished(false);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getPublicKey()
/*      */   {
/* 1311 */     return this.public_key;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getShortID()
/*      */   {
/* 1317 */     return this.short_id;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getID()
/*      */   {
/* 1323 */     if (this.id == null) {
/* 1324 */       this.id = Base32.encode(getShortID());
/*      */     }
/* 1326 */     return this.id;
/*      */   }
/*      */   
/*      */ 
/*      */   protected byte[] getPrivateKey()
/*      */   {
/* 1332 */     return this.private_key;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getFixedRandom()
/*      */   {
/* 1338 */     return this.fixed_random;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getVersion()
/*      */   {
/* 1344 */     return this.version;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAZVersion()
/*      */   {
/* 1350 */     return this.az_version;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setHighestUserPromptedVersion(int v)
/*      */   {
/* 1357 */     if (v < this.version)
/*      */     {
/* 1359 */       v = this.version;
/*      */     }
/*      */     
/* 1362 */     if (this.highest_prompted_version != v)
/*      */     {
/* 1364 */       this.highest_prompted_version = v;
/*      */       
/* 1366 */       fireChanged(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getHighestUserPromptedVersion()
/*      */   {
/* 1373 */     return this.highest_prompted_version;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getHighestVersion()
/*      */   {
/* 1379 */     return Math.max(this.version, this.highest_prompted_version);
/*      */   }
/*      */   
/*      */ 
/*      */   public void resetHighestVersion()
/*      */   {
/* 1385 */     if (this.highest_prompted_version > 0)
/*      */     {
/* 1387 */       this.highest_prompted_version = 0;
/*      */       
/* 1389 */       fireChanged(1);
/*      */       
/* 1391 */       this.manager.checkUpgrade(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isMine()
/*      */   {
/* 1398 */     if (this.private_key == null)
/*      */     {
/* 1400 */       return false;
/*      */     }
/*      */     
/* 1403 */     if ((isSingleton()) && (this.add_type != 1))
/*      */     {
/* 1405 */       return false;
/*      */     }
/*      */     
/* 1408 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isUpdateable()
/*      */   {
/* 1414 */     return this.private_key != null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSubscribed()
/*      */   {
/* 1420 */     return this.is_subscribed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSubscribed(boolean s)
/*      */   {
/* 1427 */     if (this.is_subscribed != s)
/*      */     {
/* 1429 */       this.is_subscribed = s;
/*      */       
/* 1431 */       if (this.is_subscribed)
/*      */       {
/* 1433 */         this.manager.setSelected(this);
/*      */       }
/*      */       else
/*      */       {
/* 1437 */         reset();
/*      */       }
/*      */       
/* 1440 */       fireChanged(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAutoDownloadSupported()
/*      */   {
/* 1447 */     return this.history.isAutoDownloadSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void getPopularity(final SubscriptionPopularityListener listener)
/*      */     throws SubscriptionException
/*      */   {
/* 1456 */     new AEThread2("subs:popwait", true)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/* 1462 */           SubscriptionImpl.this.manager.getPopularity(SubscriptionImpl.this, new SubscriptionPopularityListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void gotPopularity(long pop)
/*      */             {
/*      */ 
/*      */ 
/* 1470 */               if (pop != SubscriptionImpl.this.popularity)
/*      */               {
/* 1472 */                 SubscriptionImpl.this.popularity = pop;
/*      */                 
/* 1474 */                 SubscriptionImpl.this.fireChanged(1);
/*      */               }
/*      */               
/* 1477 */               SubscriptionImpl.1.this.val$listener.gotPopularity(SubscriptionImpl.this.popularity);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void failed(SubscriptionException e)
/*      */             {
/* 1484 */               if (SubscriptionImpl.this.popularity == -1L)
/*      */               {
/* 1486 */                 SubscriptionImpl.1.this.val$listener.failed(new SubscriptionException("Failed to read popularity", e));
/*      */               }
/*      */               else
/*      */               {
/* 1490 */                 SubscriptionImpl.1.this.val$listener.gotPopularity(SubscriptionImpl.this.popularity);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1497 */           if (SubscriptionImpl.this.popularity == -1L)
/*      */           {
/* 1499 */             listener.failed(new SubscriptionException("Failed to read popularity", e));
/*      */           }
/*      */           else
/*      */           {
/* 1503 */             listener.gotPopularity(SubscriptionImpl.this.popularity);
/*      */           }
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCachedPopularity()
/*      */   {
/* 1513 */     return this.popularity;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setCachedPopularity(long pop)
/*      */   {
/* 1520 */     if (pop != this.popularity)
/*      */     {
/* 1522 */       this.popularity = pop;
/*      */       
/* 1524 */       fireChanged(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getReferer()
/*      */   {
/* 1531 */     if (this.referer == null)
/*      */     {
/*      */       try {
/* 1534 */         Map map = JSONUtils.decodeJSON(getJSON());
/*      */         
/* 1536 */         Engine engine = this.manager.getEngine(this, map, false);
/*      */         
/* 1538 */         if (engine != null)
/*      */         {
/* 1540 */           this.referer = engine.getReferer();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1544 */         log("Failed to get referer", e);
/*      */       }
/*      */       
/* 1547 */       if (this.referer == null)
/*      */       {
/* 1549 */         this.referer = "";
/*      */       }
/*      */     }
/*      */     
/* 1553 */     return this.referer;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPublish()
/*      */   {
/* 1559 */     synchronized (this)
/*      */     {
/* 1561 */       if (this.destroyed)
/*      */       {
/* 1563 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1568 */       if (isSingleton())
/*      */       {
/* 1570 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1575 */       if (!isSubscribed())
/*      */       {
/* 1577 */         return;
/*      */       }
/*      */       
/* 1580 */       if (this.popularity > 100L)
/*      */       {
/*      */ 
/*      */ 
/* 1584 */         if (this.lws_skip_check == 2)
/*      */         {
/* 1586 */           return;
/*      */         }
/* 1588 */         if (this.lws_skip_check == 0)
/*      */         {
/* 1590 */           if (RandomUtils.nextInt((int)((this.popularity + 99L) / 100L)) == 0)
/*      */           {
/* 1592 */             this.lws_skip_check = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1596 */             this.lws_skip_check = 2;
/*      */             
/* 1598 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1603 */       if (this.hash != null)
/*      */       {
/* 1605 */         boolean create = false;
/*      */         
/* 1607 */         if (this.lws == null)
/*      */         {
/* 1609 */           create = true;
/*      */ 
/*      */ 
/*      */         }
/* 1613 */         else if (!Arrays.equals(this.lws.getHash().getBytes(), this.hash))
/*      */         {
/* 1615 */           this.lws.remove();
/*      */           
/* 1617 */           create = true;
/*      */         }
/*      */         
/*      */ 
/* 1621 */         if (create) {
/*      */           try
/*      */           {
/* 1624 */             File original_data_location = this.manager.getVuzeFile(this);
/*      */             
/* 1626 */             if (original_data_location.exists())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1631 */               File versioned_data_location = new File(original_data_location.getParent(), original_data_location.getName() + "." + getVersion());
/*      */               
/* 1633 */               if (!versioned_data_location.exists())
/*      */               {
/* 1635 */                 if (!FileUtil.copyFile(original_data_location, versioned_data_location))
/*      */                 {
/* 1637 */                   throw new Exception("Failed to copy file to '" + versioned_data_location + "'");
/*      */                 }
/*      */               }
/*      */               
/* 1641 */               this.lws = LightWeightSeedManager.getSingleton().add(getName(), new HashWrapper(this.hash), TorrentUtils.getDecentralisedEmptyURL(), versioned_data_location, isAnonymous() ? "I2P" : "Public", new LightWeightSeedAdapter()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public TOTorrent getTorrent(byte[] hash, URL announce_url, File data_location)
/*      */                   throws Exception
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1657 */                   SubscriptionImpl.this.log("Generating light-weight torrent: hash=" + ByteFormatter.encodeString(hash));
/*      */                   
/* 1659 */                   TOTorrentCreator creator = org.gudy.azureus2.core3.torrent.TOTorrentFactory.createFromFileOrDirWithFixedPieceLength(data_location, announce_url, 262144L);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1665 */                   TOTorrent t = creator.create();
/*      */                   
/* 1667 */                   t.setHashOverride(hash);
/*      */                   
/* 1669 */                   return t;
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1676 */             log("Failed to create light-weight-seed", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected synchronized boolean canAutoUpgradeCheck()
/*      */   {
/* 1686 */     if (isSingleton())
/*      */     {
/* 1688 */       return false;
/*      */     }
/*      */     
/* 1691 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 1693 */     if ((this.last_auto_upgrade_check == -1L) || (now - this.last_auto_upgrade_check > 14400000L))
/*      */     {
/* 1695 */       this.last_auto_upgrade_check = now;
/*      */       
/* 1697 */       return true;
/*      */     }
/*      */     
/* 1700 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addAssociation(byte[] hash)
/*      */   {
/* 1707 */     if (hash.length != 20)
/*      */     {
/* 1709 */       Debug.out("Invalid hash: " + ByteFormatter.encodeString(hash));
/*      */       
/* 1711 */       return;
/*      */     }
/*      */     
/* 1714 */     addAssociationSupport(hash, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean addAssociationSupport(byte[] hash, boolean internal)
/*      */   {
/* 1722 */     synchronized (this)
/*      */     {
/* 1724 */       for (int i = 0; i < this.associations.size(); i++)
/*      */       {
/* 1726 */         association assoc = (association)this.associations.get(i);
/*      */         
/* 1728 */         if (Arrays.equals(assoc.getHash(), hash))
/*      */         {
/* 1730 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 1734 */       this.associations.add(new association(hash, SystemTime.getCurrentTime()));
/*      */       
/* 1736 */       if ((MAX_ASSOCIATIONS > 0) && (this.associations.size() > MAX_ASSOCIATIONS))
/*      */       {
/* 1738 */         this.associations.remove(RandomUtils.nextInt(MAX_ASSOCIATIONS - 16));
/*      */       }
/*      */     }
/*      */     
/* 1742 */     if (!internal)
/*      */     {
/* 1744 */       fireChanged(1);
/*      */       
/* 1746 */       this.manager.associationAdded(this, hash);
/*      */     }
/*      */     
/* 1749 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasAssociation(byte[] hash)
/*      */   {
/* 1756 */     synchronized (this)
/*      */     {
/* 1758 */       for (int i = 0; i < this.associations.size(); i++)
/*      */       {
/* 1760 */         association assoc = (association)this.associations.get(i);
/*      */         
/* 1762 */         if (Arrays.equals(assoc.getHash(), hash))
/*      */         {
/* 1764 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1769 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addPotentialAssociation(String result_id, String key)
/*      */   {
/* 1777 */     this.manager.addPotentialAssociation(this, result_id, key);
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
/*      */   protected association getAssociationForPublish()
/*      */   {
/* 1792 */     synchronized (this)
/*      */     {
/* 1794 */       int num_assoc = this.associations.size();
/*      */       
/*      */ 
/*      */ 
/* 1798 */       for (int i = num_assoc - 1; i >= Math.max(0, num_assoc - 16); i--)
/*      */       {
/* 1800 */         association assoc = (association)this.associations.get(i);
/*      */         
/* 1802 */         if (!assoc.getPublished())
/*      */         {
/* 1804 */           assoc.setPublished(true);
/*      */           
/* 1806 */           return assoc;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1812 */       int rem = this.associations.size() - 16;
/*      */       
/* 1814 */       if (rem > 0)
/*      */       {
/* 1816 */         List<association> l = new ArrayList(this.associations.subList(0, rem));
/*      */         
/* 1818 */         Collections.shuffle(l);
/*      */         
/* 1820 */         for (int i = 0; i < l.size(); i++)
/*      */         {
/* 1822 */           association assoc = (association)l.get(i);
/*      */           
/* 1824 */           if (!assoc.getPublished())
/*      */           {
/* 1826 */             assoc.setPublished(true);
/*      */             
/* 1828 */             return assoc;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1834 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getAssociationsRemainingForPublish()
/*      */   {
/* 1840 */     synchronized (this)
/*      */     {
/* 1842 */       int result = 0;
/*      */       
/* 1844 */       for (association a : this.associations)
/*      */       {
/* 1846 */         if (!a.getPublished())
/*      */         {
/* 1848 */           result++;
/*      */         }
/*      */       }
/*      */       
/* 1852 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getPublished()
/*      */   {
/* 1859 */     return this.published;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setPublished(boolean b)
/*      */   {
/* 1866 */     this.published = b;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getVerifiedPublicationVersion(Map details)
/*      */   {
/* 1876 */     if (isSingleton())
/*      */     {
/* 1878 */       return getVersion();
/*      */     }
/*      */     
/* 1881 */     if (!verifyPublicationDetails(details))
/*      */     {
/* 1883 */       return -1;
/*      */     }
/*      */     
/* 1886 */     return getPublicationVersion(details);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static int getPublicationVersion(Map details)
/*      */   {
/* 1893 */     return ((Long)details.get("v")).intValue();
/*      */   }
/*      */   
/*      */ 
/*      */   protected byte[] getPublicationHash()
/*      */   {
/* 1899 */     return this.hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static byte[] getPublicationHash(Map details)
/*      */   {
/* 1906 */     return (byte[])details.get("h");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static int getPublicationSize(Map details)
/*      */   {
/* 1913 */     return ((Long)details.get("z")).intValue();
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map getPublicationDetails()
/*      */   {
/* 1919 */     Map result = new HashMap();
/*      */     
/* 1921 */     result.put("v", new Long(this.version));
/*      */     
/* 1923 */     if (this.singleton_details == null)
/*      */     {
/* 1925 */       result.put("h", this.hash);
/* 1926 */       result.put("z", new Long(this.sig_data_size));
/* 1927 */       result.put("s", this.sig);
/*      */     }
/*      */     else
/*      */     {
/* 1931 */       result.put("x", this.singleton_details);
/*      */     }
/*      */     
/* 1934 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean verifyPublicationDetails(Map details)
/*      */   {
/* 1941 */     synchronized (this)
/*      */     {
/* 1943 */       if (BEncoder.mapsAreIdentical(this.verify_cache_details, details))
/*      */       {
/* 1945 */         return this.verify_cache_result;
/*      */       }
/*      */     }
/*      */     
/* 1949 */     byte[] hash = (byte[])details.get("h");
/* 1950 */     int version = ((Long)details.get("v")).intValue();
/* 1951 */     int size = ((Long)details.get("z")).intValue();
/* 1952 */     byte[] sig = (byte[])details.get("s");
/*      */     
/* 1954 */     boolean result = SubscriptionBodyImpl.verify(this.public_key, hash, version, size, sig);
/*      */     
/* 1956 */     synchronized (this)
/*      */     {
/* 1958 */       this.verify_cache_details = details;
/* 1959 */       this.verify_cache_result = result;
/*      */     }
/*      */     
/* 1962 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCreatorRef(String ref)
/*      */   {
/* 1969 */     this.creator_ref = ref;
/*      */     
/* 1971 */     fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getCreatorRef()
/*      */   {
/* 1977 */     return this.creator_ref;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCategory(String _category)
/*      */   {
/* 1984 */     if ((_category == null) && (this.category == null))
/*      */     {
/* 1986 */       return;
/*      */     }
/*      */     
/* 1989 */     if ((_category != null) && (this.category != null) && (_category.equals(this.category)))
/*      */     {
/* 1991 */       return;
/*      */     }
/*      */     
/* 1994 */     this.manager.setCategoryOnExisting(this, this.category, _category);
/*      */     
/* 1996 */     this.category = _category;
/*      */     
/* 1998 */     fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getCategory()
/*      */   {
/* 2004 */     return this.category;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTagID(long _tag_id)
/*      */   {
/* 2012 */     if (_tag_id == this.tag_id)
/*      */     {
/* 2014 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2020 */     this.tag_id = _tag_id;
/*      */     
/* 2022 */     fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTagID()
/*      */   {
/* 2028 */     return this.tag_id;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getParent()
/*      */   {
/* 2034 */     return this.parent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setParent(String _parent)
/*      */   {
/* 2041 */     if ((_parent == null) && (this.parent == null))
/*      */     {
/* 2043 */       return;
/*      */     }
/*      */     
/* 2046 */     if ((_parent != null) && (this.parent != null) && (_parent.equals(this.parent)))
/*      */     {
/* 2048 */       return;
/*      */     }
/*      */     
/* 2051 */     this.parent = _parent;
/*      */     
/* 2053 */     fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireChanged(int reason)
/*      */   {
/* 2060 */     this.manager.configDirty(this);
/*      */     
/* 2062 */     Iterator it = this.listeners.iterator();
/*      */     
/* 2064 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 2067 */         ((SubscriptionListener)it.next()).subscriptionChanged(this, reason);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2071 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fireDownloaded(boolean was_auto)
/*      */   {
/* 2081 */     Iterator it = this.listeners.iterator();
/*      */     
/* 2083 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 2086 */         ((SubscriptionListener)it.next()).subscriptionDownloaded(this, was_auto);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2090 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(SubscriptionListener l)
/*      */   {
/* 2099 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(SubscriptionListener l)
/*      */   {
/* 2106 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */   public SubscriptionHistory getHistory()
/*      */   {
/* 2112 */     return this.history;
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.azureus.core.subs.SubscriptionManager getManager()
/*      */   {
/* 2118 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public VuzeFile getVuzeFile()
/*      */     throws SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/* 2127 */       return VuzeFileHandler.getSingleton().loadVuzeFile(this.manager.getVuzeFile(this).getAbsolutePath());
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2131 */       throw new SubscriptionException("Failed to get Vuze file", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public VuzeFile getSearchTemplateVuzeFile()
/*      */   {
/* 2138 */     if (!isSearchTemplate())
/*      */     {
/* 2140 */       return null;
/*      */     }
/*      */     
/* 2143 */     Object[] details = this.manager.getSearchTemplateVuzeFile(this);
/*      */     
/* 2145 */     if (details != null)
/*      */     {
/* 2147 */       return (VuzeFile)details[0];
/*      */     }
/*      */     
/* 2150 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSearchTemplateImportable()
/*      */   {
/* 2156 */     return this.manager.isSearchTemplateImportable(this);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*      */     LightWeightSeed l;
/*      */     
/* 2164 */     synchronized (this)
/*      */     {
/* 2166 */       this.destroyed = true;
/*      */       
/* 2168 */       l = this.lws;
/*      */     }
/*      */     
/* 2171 */     if (l != null)
/*      */     {
/* 2173 */       l.remove();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/* 2180 */     getHistory().reset();
/*      */     try
/*      */     {
/* 2183 */       getEngine().reset();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2187 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void remove()
/*      */   {
/* 2194 */     destroy();
/*      */     
/* 2196 */     this.manager.removeSubscription(this);
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
/*      */   public SubscriptionResult[] getResults(boolean include_deleted)
/*      */   {
/* 2212 */     return getHistory().getResults(include_deleted);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SubscriptionResultFilter getFilters()
/*      */     throws SubscriptionException
/*      */   {
/* 2221 */     Map map = JSONUtils.decodeJSON(getJSON());
/*      */     
/* 2223 */     Map filters = (Map)map.get("filters");
/*      */     
/* 2225 */     return new SubscriptionResultFilterImpl(this, filters);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUserData(Object key, Object data)
/*      */   {
/* 2233 */     synchronized (this.user_data)
/*      */     {
/* 2235 */       if (data == null)
/*      */       {
/* 2237 */         this.user_data.remove(key);
/*      */       }
/*      */       else
/*      */       {
/* 2241 */         this.user_data.put(key, data);
/*      */       }
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
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 2260 */     this.manager.log(getString() + ": " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 2268 */     this.manager.log(getString() + ": " + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 2274 */     return "name=" + this.name + ",sid=" + ByteFormatter.encodeString(this.short_id) + ",ver=" + this.version + ",pub=" + this.is_public + ",anon=" + this.is_anonymous + ",mine=" + isMine() + ",sub=" + this.is_subscribed + (this.is_subscribed ? ",hist={" + this.history.getString() + "}" : "") + ",pop=" + this.popularity + (this.server_publication_outstanding ? ",spo=true" : "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void generate(IndentWriter writer)
/*      */   {
/*      */     String engine_str;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 2294 */       engine_str = "" + getEngine().getId();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2298 */       engine_str = Debug.getNestedExceptionMessage(e);
/*      */     }
/*      */     
/* 2301 */     writer.println(getString() + ": engine=" + engine_str);
/*      */     try
/*      */     {
/* 2304 */       writer.indent();
/*      */       
/* 2306 */       synchronized (this)
/*      */       {
/* 2308 */         for (int i = 0; i < this.associations.size(); i++)
/*      */         {
/* 2310 */           ((association)this.associations.get(i)).generate(writer);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2315 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getAssociationCount()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 1011	com/aelitis/azureus/core/subs/impl/SubscriptionImpl:associations	Ljava/util/List;
/*      */     //   8: invokeinterface 1197 1 0
/*      */     //   13: aload_1
/*      */     //   14: monitorexit
/*      */     //   15: ireturn
/*      */     //   16: astore_2
/*      */     //   17: aload_1
/*      */     //   18: monitorexit
/*      */     //   19: aload_2
/*      */     //   20: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1783	-> byte code offset #0
/*      */     //   Java source line #1785	-> byte code offset #4
/*      */     //   Java source line #1786	-> byte code offset #16
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	21	0	this	SubscriptionImpl
/*      */     //   2	16	1	Ljava/lang/Object;	Object
/*      */     //   16	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	15	16	finally
/*      */     //   16	19	16	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected boolean isRemoved()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 985	com/aelitis/azureus/core/subs/impl/SubscriptionImpl:destroyed	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2202	-> byte code offset #0
/*      */     //   Java source line #2204	-> byte code offset #4
/*      */     //   Java source line #2205	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	SubscriptionImpl
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public Object getUserData(Object key)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 1015	com/aelitis/azureus/core/subs/impl/SubscriptionImpl:user_data	Ljava/util/Map;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 1015	com/aelitis/azureus/core/subs/impl/SubscriptionImpl:user_data	Ljava/util/Map;
/*      */     //   11: aload_1
/*      */     //   12: invokeinterface 1204 2 0
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: areturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2250	-> byte code offset #0
/*      */     //   Java source line #2252	-> byte code offset #7
/*      */     //   Java source line #2253	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	SubscriptionImpl
/*      */     //   0	25	1	key	Object
/*      */     //   5	17	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   protected static class association
/*      */   {
/*      */     private byte[] hash;
/*      */     private long when;
/*      */     private boolean published;
/*      */     
/*      */     protected association(byte[] _hash, long _when)
/*      */     {
/* 2331 */       this.hash = _hash;
/* 2332 */       this.when = _when;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte[] getHash()
/*      */     {
/* 2338 */       return this.hash;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getWhen()
/*      */     {
/* 2344 */       return this.when;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean getPublished()
/*      */     {
/* 2350 */       return this.published;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setPublished(boolean b)
/*      */     {
/* 2357 */       this.published = b;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 2363 */       return ByteFormatter.encodeString(this.hash) + ", pub=" + this.published;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void generate(IndentWriter writer)
/*      */     {
/* 2370 */       writer.println(getString());
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */