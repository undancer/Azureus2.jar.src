/*      */ package com.aelitis.azureus.core.dht.db.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.DHTOperationAdapter;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageAdapter;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageBlock;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageKey;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageKeyStats;
/*      */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*      */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*      */ import com.aelitis.azureus.core.dht.db.DHTDBLookupResult;
/*      */ import com.aelitis.azureus.core.dht.db.DHTDBStats;
/*      */ import com.aelitis.azureus.core.dht.db.DHTDBValue;
/*      */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportQueryStoreReply;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandlerAdapter;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*      */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTDBImpl
/*      */   implements DHTDB, DHTDBStats
/*      */ {
/*      */   private static final int MAX_VALUE_LIFETIME = 259200000;
/*      */   private final int original_republish_interval;
/*      */   public static final int ORIGINAL_REPUBLISH_INTERVAL_GRACE = 3600000;
/*      */   private static final boolean ENABLE_PRECIOUS_STUFF = false;
/*      */   private static final int PRECIOUS_CHECK_INTERVAL = 7200000;
/*      */   private final int cache_republish_interval;
/*      */   private static final long MIN_CACHE_EXPIRY_CHECK_INTERVAL = 60000L;
/*      */   private long last_cache_expiry_check;
/*      */   private static final long IP_BLOOM_FILTER_REBUILD_PERIOD = 900000L;
/*      */   private static final int IP_COUNT_BLOOM_SIZE_INCREASE_CHUNK = 1000;
/*   93 */   private BloomFilter ip_count_bloom_filter = BloomFilterFactory.createAddRemove8Bit(1000);
/*      */   
/*      */   private static final int VALUE_VERSION_CHUNK = 128;
/*      */   
/*      */   private int next_value_version;
/*      */   
/*      */   private int next_value_version_left;
/*      */   
/*      */   protected static final int QUERY_STORE_REQUEST_ENTRY_SIZE = 6;
/*      */   protected static final int QUERY_STORE_REPLY_ENTRY_SIZE = 2;
/*  103 */   final Map<HashWrapper, DHTDBMapping> stored_values = new HashMap();
/*  104 */   private final Map<DHTDBMapping.ShortHash, DHTDBMapping> stored_values_prefix_map = new HashMap();
/*      */   
/*      */   private DHTControl control;
/*      */   
/*      */   private final DHTStorageAdapter adapter;
/*      */   
/*      */   private DHTRouter router;
/*      */   
/*      */   private DHTTransportContact local_contact;
/*      */   
/*      */   final DHTLogger logger;
/*      */   
/*      */   private static final long MAX_TOTAL_SIZE = 4194304L;
/*      */   private int total_size;
/*      */   private int total_values;
/*      */   private int total_keys;
/*      */   private int total_local_keys;
/*      */   private boolean force_original_republish;
/*  122 */   private final IpFilter ip_filter = IpFilterManagerFactory.getSingleton().getIPFilter();
/*      */   
/*  124 */   final AEMonitor this_mon = new AEMonitor("DHTDB");
/*      */   
/*      */   private static final boolean DEBUG_SURVEY = false;
/*      */   
/*      */   private static final boolean SURVEY_ONLY_RF_KEYS = true;
/*      */   
/*      */   private static final int SURVEY_PERIOD = 900000;
/*      */   
/*      */   private static final int SURVEY_STATE_INACT_TIMEOUT = 3600000;
/*      */   
/*      */   private static final int SURVEY_STATE_MAX_LIFE_TIMEOUT = 12600000;
/*      */   
/*      */   private static final int SURVEY_STATE_MAX_LIFE_RAND = 3600000;
/*      */   
/*      */   private static final int MAX_SURVEY_SIZE = 100;
/*      */   private static final int MAX_SURVEY_STATE_SIZE = 150;
/*      */   private final boolean survey_enabled;
/*      */   private volatile boolean survey_in_progress;
/*  142 */   private final Map<HashWrapper, Long> survey_mapping_times = new HashMap();
/*      */   
/*  144 */   private final Map<HashWrapper, SurveyContactState> survey_state = new LinkedHashMap(150, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<HashWrapper, DHTDBImpl.SurveyContactState> eldest)
/*      */     {
/*      */ 
/*  151 */       return size() > 150;
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */   private TimerEventPeriodic precious_timer;
/*      */   
/*      */   private TimerEventPeriodic original_republish_timer;
/*      */   
/*      */   private TimerEventPeriodic cache_republish_timer;
/*      */   
/*      */   private final TimerEventPeriodic bloom_timer;
/*      */   
/*      */   private TimerEventPeriodic survey_timer;
/*      */   
/*      */   private boolean sleeping;
/*      */   
/*      */   private boolean suspended;
/*      */   
/*      */   private volatile boolean destroyed;
/*      */   
/*      */ 
/*      */   public DHTDBImpl(DHTStorageAdapter _adapter, int _original_republish_interval, int _cache_republish_interval, byte _protocol_version, DHTLogger _logger)
/*      */   {
/*  175 */     this.adapter = (_adapter == null ? null : new adapterFacade(_adapter));
/*  176 */     this.original_republish_interval = _original_republish_interval;
/*  177 */     this.cache_republish_interval = _cache_republish_interval;
/*  178 */     this.logger = _logger;
/*      */     
/*  180 */     this.survey_enabled = ((_protocol_version >= 26) && ((this.adapter == null) || (this.adapter.getNetwork() == 1) || (FeatureAvailability.isDHTRepV2Enabled())));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  203 */     if (this.original_republish_interval > 0)
/*      */     {
/*  205 */       this.original_republish_timer = SimpleTimer.addPeriodicEvent("DHTDB:op", this.original_republish_interval, true, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  215 */           DHTDBImpl.this.logger.log("Republish of original mappings starts");
/*      */           
/*  217 */           long start = SystemTime.getCurrentTime();
/*      */           
/*  219 */           int stats = DHTDBImpl.this.republishOriginalMappings();
/*      */           
/*  221 */           long end = SystemTime.getCurrentTime();
/*      */           
/*  223 */           DHTDBImpl.this.logger.log("Republish of original mappings completed in " + (end - start) + ": " + "values = " + stats);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  230 */     if (this.cache_republish_interval > 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  235 */       this.cache_republish_timer = SimpleTimer.addPeriodicEvent("DHTDB:cp", this.cache_republish_interval + 10000 - RandomUtils.nextInt(20000), true, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  245 */           DHTDBImpl.this.logger.log("Republish of cached mappings starts");
/*      */           
/*  247 */           long start = SystemTime.getCurrentTime();
/*      */           
/*  249 */           int[] stats = DHTDBImpl.this.republishCachedMappings();
/*      */           
/*  251 */           long end = SystemTime.getCurrentTime();
/*      */           
/*  253 */           DHTDBImpl.this.logger.log("Republish of cached mappings completed in " + (end - start) + ": " + "values = " + stats[0] + ", keys = " + stats[1] + ", ops = " + stats[2]);
/*      */           
/*      */ 
/*  256 */           if (DHTDBImpl.this.force_original_republish)
/*      */           {
/*  258 */             DHTDBImpl.this.force_original_republish = false;
/*      */             
/*  260 */             DHTDBImpl.this.logger.log("Force republish of original mappings due to router change starts");
/*      */             
/*  262 */             start = SystemTime.getCurrentTime();
/*      */             
/*  264 */             int stats2 = DHTDBImpl.this.republishOriginalMappings();
/*      */             
/*  266 */             end = SystemTime.getCurrentTime();
/*      */             
/*  268 */             DHTDBImpl.this.logger.log("Force republish of original mappings due to router change completed in " + (end - start) + ": " + "values = " + stats2);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  276 */     this.bloom_timer = SimpleTimer.addPeriodicEvent("DHTDB:bloom", 900000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/*  286 */           DHTDBImpl.this.this_mon.enter();
/*      */           
/*  288 */           DHTDBImpl.this.rebuildIPBloomFilter(false);
/*      */         }
/*      */         finally
/*      */         {
/*  292 */           DHTDBImpl.this.this_mon.exit();
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  297 */     if (this.survey_enabled)
/*      */     {
/*  299 */       this.survey_timer = SimpleTimer.addPeriodicEvent("DHTDB:survey", 900000L, true, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  309 */           DHTDBImpl.this.survey();
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setControl(DHTControl _control)
/*      */   {
/*  320 */     this.control = _control;
/*      */     
/*      */ 
/*      */ 
/*  324 */     this.force_original_republish = (this.router != null);
/*      */     
/*  326 */     this.router = this.control.getRouter();
/*  327 */     this.local_contact = this.control.getTransport().getLocalContact();
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  332 */       this.this_mon.enter();
/*      */       
/*  334 */       this.survey_state.clear();
/*      */       
/*  336 */       Iterator<DHTDBMapping> it = this.stored_values.values().iterator();
/*      */       
/*  338 */       while (it.hasNext())
/*      */       {
/*  340 */         DHTDBMapping mapping = (DHTDBMapping)it.next();
/*      */         
/*  342 */         mapping.updateLocalContact(this.local_contact);
/*      */       }
/*      */     }
/*      */     finally {
/*  346 */       this.this_mon.exit();
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
/*      */   public DHTDBValue store(HashWrapper key, byte[] value, short flags, byte life_hours, byte replication_control)
/*      */   {
/*  361 */     if ((flags & 0x100) == 0)
/*      */     {
/*  363 */       if ((flags & 0x200) != 0)
/*      */       {
/*  365 */         Debug.out("Obfuscated puts without 'put-and-forget' are not supported as original-republishing of them is not implemented");
/*      */       }
/*      */       
/*  368 */       if (life_hours > 0)
/*      */       {
/*  370 */         if (life_hours * 60 * 60 * 1000 < this.original_republish_interval)
/*      */         {
/*  372 */           Debug.out("Don't put persistent values with a lifetime less than republish period - lifetime over-ridden");
/*      */           
/*  374 */           life_hours = 0;
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/*  379 */         this.this_mon.enter();
/*      */         
/*  381 */         this.total_local_keys += 1;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  386 */         DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(key);
/*      */         
/*  388 */         if (mapping == null)
/*      */         {
/*  390 */           mapping = new DHTDBMapping(this, key, true);
/*      */           
/*  392 */           this.stored_values.put(key, mapping);
/*      */           
/*  394 */           addToPrefixMap(mapping);
/*      */         }
/*      */         
/*  397 */         DHTDBValueImpl res = new DHTDBValueImpl(SystemTime.getCurrentTime(), value, getNextValueVersion(), this.local_contact, this.local_contact, true, flags, life_hours, replication_control);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  409 */         mapping.add(res);
/*      */         
/*  411 */         return res;
/*      */       }
/*      */       finally
/*      */       {
/*  415 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*  419 */     DHTDBValueImpl res = new DHTDBValueImpl(SystemTime.getCurrentTime(), value, getNextValueVersion(), this.local_contact, this.local_contact, true, flags, life_hours, replication_control);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  431 */     return res;
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
/*      */   public byte store(DHTTransportContact sender, HashWrapper key, DHTTransportValue[] values)
/*      */   {
/*  456 */     if (this.total_size + this.total_values * 4 > 4194304L)
/*      */     {
/*  458 */       DHTLog.log("Not storing " + DHTLog.getString2(key.getHash()) + " as maximum storage limit exceeded");
/*      */       
/*  460 */       return 3;
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  466 */       this.this_mon.enter();
/*      */       
/*  468 */       if ((this.sleeping) || (this.suspended))
/*      */       {
/*  470 */         return 1;
/*      */       }
/*      */       
/*  473 */       checkCacheExpiration(false);
/*      */       
/*  475 */       DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(key);
/*      */       
/*  477 */       if (mapping == null)
/*      */       {
/*  479 */         mapping = new DHTDBMapping(this, key, false);
/*      */         
/*  481 */         this.stored_values.put(key, mapping);
/*      */         
/*  483 */         addToPrefixMap(mapping);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  489 */       for (int i = 0; i < values.length; i++)
/*      */       {
/*  491 */         DHTTransportValue value = values[i];
/*      */         
/*  493 */         DHTDBValueImpl mapping_value = new DHTDBValueImpl(sender, value, false);
/*      */         
/*  495 */         mapping.add(mapping_value);
/*      */       }
/*      */       
/*  498 */       return mapping.getDiversificationType();
/*      */     }
/*      */     finally
/*      */     {
/*  502 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTDBLookupResult get(DHTTransportContact reader, HashWrapper key, int max_values, short flags, boolean external_request)
/*      */   {
/*      */     try
/*      */     {
/*  515 */       this.this_mon.enter();
/*      */       
/*  517 */       checkCacheExpiration(false);
/*      */       
/*  519 */       final DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(key);
/*      */       
/*  521 */       if (mapping == null)
/*      */       {
/*  523 */         return null;
/*      */       }
/*      */       
/*  526 */       if (external_request)
/*      */       {
/*  528 */         mapping.addHit();
/*      */       }
/*      */       
/*  531 */       final Object values = mapping.get(reader, max_values, flags);
/*      */       
/*  533 */       new DHTDBLookupResult()
/*      */       {
/*      */ 
/*      */         public DHTDBValue[] getValues()
/*      */         {
/*      */ 
/*  539 */           return values;
/*      */         }
/*      */         
/*      */ 
/*      */         public byte getDiversificationType()
/*      */         {
/*  545 */           return mapping.getDiversificationType();
/*      */         }
/*      */       };
/*      */     }
/*      */     finally
/*      */     {
/*  551 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTDBValue get(HashWrapper key)
/*      */   {
/*      */     try
/*      */     {
/*  562 */       this.this_mon.enter();
/*      */       
/*  564 */       DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(key);
/*      */       DHTDBValueImpl localDHTDBValueImpl;
/*  566 */       if (mapping != null)
/*      */       {
/*  568 */         return mapping.get(this.local_contact);
/*      */       }
/*      */       
/*  571 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*  575 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTDBValue getAnyValue(HashWrapper key)
/*      */   {
/*      */     try
/*      */     {
/*  584 */       this.this_mon.enter();
/*      */       
/*  586 */       DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(key);
/*      */       DHTDBValueImpl localDHTDBValueImpl;
/*  588 */       if (mapping != null)
/*      */       {
/*  590 */         return mapping.getAnyValue(this.local_contact);
/*      */       }
/*      */       
/*  593 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*  597 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public List<DHTDBValue> getAllValues(HashWrapper key)
/*      */   {
/*      */     try
/*      */     {
/*  606 */       this.this_mon.enter();
/*      */       
/*  608 */       DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(key);
/*      */       
/*  610 */       List<DHTDBValue> result = new ArrayList();
/*      */       
/*  612 */       if (mapping != null)
/*      */       {
/*  614 */         result.addAll(mapping.getAllValues(this.local_contact));
/*      */       }
/*      */       
/*  617 */       return result;
/*      */     }
/*      */     finally
/*      */     {
/*  621 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasKey(HashWrapper key)
/*      */   {
/*      */     try
/*      */     {
/*  630 */       this.this_mon.enter();
/*      */       
/*  632 */       return this.stored_values.containsKey(key);
/*      */     }
/*      */     finally
/*      */     {
/*  636 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTDBValue remove(DHTTransportContact originator, HashWrapper key)
/*      */   {
/*      */     try
/*      */     {
/*  648 */       this.this_mon.enter();
/*      */       
/*  650 */       DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(key);
/*      */       DHTDBValueImpl res;
/*  652 */       if (mapping != null)
/*      */       {
/*  654 */         res = mapping.remove(originator);
/*      */         DHTDBValue localDHTDBValue;
/*  656 */         if (res != null)
/*      */         {
/*  658 */           this.total_local_keys -= 1;
/*      */           
/*  660 */           if (!mapping.getValues().hasNext())
/*      */           {
/*  662 */             this.stored_values.remove(key);
/*      */             
/*  664 */             removeFromPrefixMap(mapping);
/*      */             
/*  666 */             mapping.destroy();
/*      */           }
/*      */           
/*  669 */           return res.getValueForDeletion(getNextValueVersion());
/*      */         }
/*      */         
/*  672 */         return null;
/*      */       }
/*      */       
/*  675 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*  679 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTStorageBlock keyBlockRequest(DHTTransportContact direct_sender, byte[] request, byte[] signature)
/*      */   {
/*  689 */     if (this.adapter == null)
/*      */     {
/*  691 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  697 */     if (direct_sender != null)
/*      */     {
/*  699 */       byte[] key = this.adapter.getKeyForKeyBlock(request);
/*      */       
/*  701 */       List<DHTTransportContact> closest_contacts = this.control.getClosestKContactsList(key, true);
/*      */       
/*  703 */       boolean process_it = false;
/*      */       
/*  705 */       for (int i = 0; i < closest_contacts.size(); i++)
/*      */       {
/*  707 */         if (this.router.isID(((DHTTransportContact)closest_contacts.get(i)).getID()))
/*      */         {
/*  709 */           process_it = true;
/*      */           
/*  711 */           break;
/*      */         }
/*      */       }
/*      */       
/*  715 */       if (!process_it)
/*      */       {
/*  717 */         DHTLog.log("Not processing key block for  " + DHTLog.getString2(key) + " as key too far away");
/*      */         
/*  719 */         return null;
/*      */       }
/*      */       
/*  722 */       if (!this.control.verifyContact(direct_sender, true))
/*      */       {
/*  724 */         DHTLog.log("Not processing key block for  " + DHTLog.getString2(key) + " as verification failed");
/*      */         
/*  726 */         return null;
/*      */       }
/*      */     }
/*      */     
/*  730 */     return this.adapter.keyBlockRequest(direct_sender, request, signature);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTStorageBlock getKeyBlockDetails(byte[] key)
/*      */   {
/*  737 */     if (this.adapter == null)
/*      */     {
/*  739 */       return null;
/*      */     }
/*      */     
/*  742 */     return this.adapter.getKeyBlockDetails(key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isKeyBlocked(byte[] key)
/*      */   {
/*  749 */     return getKeyBlockDetails(key) != null;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTStorageBlock[] getDirectKeyBlocks()
/*      */   {
/*  755 */     if (this.adapter == null)
/*      */     {
/*  757 */       return new DHTStorageBlock[0];
/*      */     }
/*      */     
/*  760 */     return this.adapter.getDirectKeyBlocks();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  766 */     return this.total_keys == 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getKeyCount()
/*      */   {
/*  772 */     return this.total_keys;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getLocalKeyCount()
/*      */   {
/*  778 */     return this.total_local_keys;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getValueCount()
/*      */   {
/*  784 */     return this.total_values;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSize()
/*      */   {
/*  790 */     return this.total_size;
/*      */   }
/*      */   
/*      */   public int[] getValueDetails()
/*      */   {
/*      */     try
/*      */     {
/*  797 */       this.this_mon.enter();
/*      */       
/*  799 */       int[] res = new int[6];
/*      */       
/*  801 */       Iterator<DHTDBMapping> it = this.stored_values.values().iterator();
/*      */       DHTDBMapping mapping;
/*  803 */       while (it.hasNext())
/*      */       {
/*  805 */         mapping = (DHTDBMapping)it.next();
/*      */         
/*  807 */         res[0] += mapping.getValueCount();
/*  808 */         res[1] += mapping.getLocalSize();
/*  809 */         res[2] += mapping.getDirectSize();
/*  810 */         res[3] += mapping.getIndirectSize();
/*      */         
/*  812 */         int dt = mapping.getDiversificationType();
/*      */         
/*  814 */         if (dt == 2)
/*      */         {
/*  816 */           res[4] += 1;
/*      */         }
/*  818 */         else if (dt == 3)
/*      */         {
/*  820 */           res[5] += 1;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  837 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/*  841 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getKeyBlockCount()
/*      */   {
/*  848 */     if (this.adapter == null)
/*      */     {
/*  850 */       return 0;
/*      */     }
/*      */     
/*  853 */     return this.adapter.getDirectKeyBlocks().length;
/*      */   }
/*      */   
/*      */   public Iterator<HashWrapper> getKeys()
/*      */   {
/*      */     try
/*      */     {
/*  860 */       this.this_mon.enter();
/*      */       
/*  862 */       return new ArrayList(this.stored_values.keySet()).iterator();
/*      */     }
/*      */     finally
/*      */     {
/*  866 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int republishOriginalMappings()
/*      */   {
/*  873 */     if (this.suspended)
/*      */     {
/*  875 */       this.logger.log("Original republish skipped as suspended");
/*      */       
/*  877 */       return 0;
/*      */     }
/*      */     
/*  880 */     int values_published = 0;
/*      */     
/*  882 */     Map<HashWrapper, List<DHTDBValueImpl>> republish = new HashMap();
/*      */     try
/*      */     {
/*  885 */       this.this_mon.enter();
/*      */       
/*  887 */       Iterator<Map.Entry<HashWrapper, DHTDBMapping>> it = this.stored_values.entrySet().iterator();
/*      */       
/*  889 */       while (it.hasNext())
/*      */       {
/*  891 */         Map.Entry<HashWrapper, DHTDBMapping> entry = (Map.Entry)it.next();
/*      */         
/*  893 */         HashWrapper key = (HashWrapper)entry.getKey();
/*      */         
/*  895 */         DHTDBMapping mapping = (DHTDBMapping)entry.getValue();
/*      */         
/*  897 */         Iterator<DHTDBValueImpl> it2 = mapping.getValues();
/*      */         
/*  899 */         List<DHTDBValueImpl> values = new ArrayList();
/*      */         
/*  901 */         while (it2.hasNext())
/*      */         {
/*  903 */           DHTDBValueImpl value = (DHTDBValueImpl)it2.next();
/*      */           
/*  905 */           if ((value != null) && (value.isLocal()))
/*      */           {
/*      */ 
/*      */ 
/*  909 */             value.setCreationTime();
/*      */             
/*  911 */             values.add(value);
/*      */           }
/*      */         }
/*      */         
/*  915 */         if (values.size() > 0)
/*      */         {
/*  917 */           republish.put(key, values);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  923 */       this.this_mon.exit();
/*      */     }
/*      */     
/*  926 */     Iterator<Map.Entry<HashWrapper, List<DHTDBValueImpl>>> it = republish.entrySet().iterator();
/*      */     
/*  928 */     int key_tot = republish.size();
/*  929 */     int key_num = 0;
/*      */     
/*  931 */     while (it.hasNext())
/*      */     {
/*  933 */       key_num++;
/*      */       
/*  935 */       Map.Entry<HashWrapper, List<DHTDBValueImpl>> entry = (Map.Entry)it.next();
/*      */       
/*  937 */       HashWrapper key = (HashWrapper)entry.getKey();
/*      */       
/*  939 */       List<DHTDBValueImpl> values = (List)entry.getValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  944 */       for (int i = 0; i < values.size(); i++)
/*      */       {
/*  946 */         values_published++;
/*      */         
/*  948 */         this.control.putEncodedKey(key.getHash(), "Republish orig: " + key_num + " of " + key_tot, (DHTTransportValue)values.get(i), 0L, true);
/*      */       }
/*      */     }
/*      */     
/*  952 */     return values_published;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int[] republishCachedMappings()
/*      */   {
/*  958 */     if (this.suspended)
/*      */     {
/*  960 */       this.logger.log("Cache republish skipped as suspended");
/*      */       
/*  962 */       return new int[] { 0, 0, 0 };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  968 */     this.router.refreshIdleLeaves(this.cache_republish_interval);
/*      */     
/*  970 */     final Map<HashWrapper, List<DHTDBValueImpl>> republish = new HashMap();
/*      */     
/*  972 */     List<DHTDBMapping> republish_via_survey = new ArrayList();
/*      */     
/*  974 */     long now = System.currentTimeMillis();
/*      */     try
/*      */     {
/*  977 */       this.this_mon.enter();
/*      */       
/*  979 */       checkCacheExpiration(true);
/*      */       
/*  981 */       Iterator<Map.Entry<HashWrapper, DHTDBMapping>> it = this.stored_values.entrySet().iterator();
/*      */       
/*  983 */       while (it.hasNext())
/*      */       {
/*  985 */         Map.Entry<HashWrapper, DHTDBMapping> entry = (Map.Entry)it.next();
/*      */         
/*  987 */         HashWrapper key = (HashWrapper)entry.getKey();
/*      */         
/*  989 */         DHTDBMapping mapping = (DHTDBMapping)entry.getValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  996 */         if (mapping.getDiversificationType() == 1)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1001 */           Iterator<DHTDBValueImpl> it2 = mapping.getValues();
/*      */           
/* 1003 */           boolean all_rf_values = it2.hasNext();
/*      */           
/* 1005 */           List<DHTDBValueImpl> values = new ArrayList();
/*      */           
/* 1007 */           while (it2.hasNext())
/*      */           {
/* 1009 */             DHTDBValueImpl value = (DHTDBValueImpl)it2.next();
/*      */             
/* 1011 */             if (value.isLocal())
/*      */             {
/* 1013 */               all_rf_values = false;
/*      */             }
/*      */             else
/*      */             {
/* 1017 */               if (value.getReplicationFactor() == -1)
/*      */               {
/* 1019 */                 all_rf_values = false;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1026 */               if (now < value.getStoreTime())
/*      */               {
/*      */ 
/*      */ 
/* 1030 */                 value.setStoreTime(now);
/*      */               }
/* 1032 */               else if (now - value.getStoreTime() > this.cache_republish_interval)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1038 */                 values.add(value);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1043 */           if (all_rf_values)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1048 */             values.clear();
/*      */             
/* 1050 */             republish_via_survey.add(mapping);
/*      */           }
/*      */           
/* 1053 */           if (values.size() > 0)
/*      */           {
/* 1055 */             republish.put(key, values);
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 1060 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1063 */     if (republish_via_survey.size() > 0)
/*      */     {
/*      */ 
/*      */ 
/* 1067 */       List<HashWrapper> stop_caching = new ArrayList();
/*      */       
/* 1069 */       for (DHTDBMapping mapping : republish_via_survey)
/*      */       {
/* 1071 */         HashWrapper key = mapping.getKey();
/*      */         
/* 1073 */         byte[] lookup_id = key.getHash();
/*      */         
/* 1075 */         List<DHTTransportContact> contacts = this.control.getClosestKContactsList(lookup_id, false);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1080 */         boolean keep_caching = false;
/*      */         
/* 1082 */         for (int j = 0; j < contacts.size(); j++)
/*      */         {
/* 1084 */           if (this.router.isID(((DHTTransportContact)contacts.get(j)).getID()))
/*      */           {
/* 1086 */             keep_caching = true;
/*      */             
/* 1088 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1092 */         if (!keep_caching)
/*      */         {
/* 1094 */           DHTLog.log("Dropping cache entry for " + DHTLog.getString(lookup_id) + " as now too far away");
/*      */           
/* 1096 */           stop_caching.add(key);
/*      */         }
/*      */       }
/*      */       
/* 1100 */       if (stop_caching.size() > 0) {
/*      */         try
/*      */         {
/* 1103 */           this.this_mon.enter();
/*      */           
/* 1105 */           for (int i = 0; i < stop_caching.size(); i++)
/*      */           {
/* 1107 */             DHTDBMapping mapping = (DHTDBMapping)this.stored_values.remove(stop_caching.get(i));
/*      */             
/* 1109 */             if (mapping != null)
/*      */             {
/* 1111 */               removeFromPrefixMap(mapping);
/*      */               
/* 1113 */               mapping.destroy();
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 1118 */           this.this_mon.exit();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1123 */     final int[] values_published = { 0 };
/* 1124 */     final int[] keys_published = { 0 };
/* 1125 */     final int[] republish_ops = { 0 };
/*      */     
/* 1127 */     final HashSet<DHTTransportContact> anti_spoof_done = new HashSet();
/*      */     
/* 1129 */     if (republish.size() > 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1141 */       Iterator<Map.Entry<HashWrapper, List<DHTDBValueImpl>>> it1 = republish.entrySet().iterator();
/*      */       
/* 1143 */       List<HashWrapper> stop_caching = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/* 1147 */       Map<HashWrapper, Object[]> contact_map = new HashMap();
/*      */       
/* 1149 */       while (it1.hasNext())
/*      */       {
/* 1151 */         Map.Entry<HashWrapper, List<DHTDBValueImpl>> entry = (Map.Entry)it1.next();
/*      */         
/* 1153 */         HashWrapper key = (HashWrapper)entry.getKey();
/*      */         
/* 1155 */         byte[] lookup_id = key.getHash();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1162 */         List<DHTTransportContact> contacts = this.control.getClosestKContactsList(lookup_id, false);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1167 */         boolean keep_caching = false;
/*      */         
/* 1169 */         for (int j = 0; j < contacts.size(); j++)
/*      */         {
/* 1171 */           if (this.router.isID(((DHTTransportContact)contacts.get(j)).getID()))
/*      */           {
/* 1173 */             keep_caching = true;
/*      */             
/* 1175 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1179 */         if (!keep_caching)
/*      */         {
/* 1181 */           DHTLog.log("Dropping cache entry for " + DHTLog.getString(lookup_id) + " as now too far away");
/*      */           
/* 1183 */           stop_caching.add(key);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1189 */         for (int j = 0; j < contacts.size(); j++)
/*      */         {
/* 1191 */           DHTTransportContact contact = (DHTTransportContact)contacts.get(j);
/*      */           
/* 1193 */           if (!this.router.isID(contact.getID()))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1198 */             Object[] data = (Object[])contact_map.get(new HashWrapper(contact.getID()));
/*      */             
/* 1200 */             if (data == null)
/*      */             {
/* 1202 */               data = new Object[] { contact, new ArrayList() };
/*      */               
/* 1204 */               contact_map.put(new HashWrapper(contact.getID()), data);
/*      */             }
/*      */             
/* 1207 */             ((List)data[1]).add(key);
/*      */           }
/*      */         }
/*      */       }
/* 1211 */       Iterator<Object[]> it2 = contact_map.values().iterator();
/*      */       
/* 1213 */       final int con_tot = contact_map.size();
/* 1214 */       int con_num = 0;
/*      */       
/* 1216 */       while (it2.hasNext())
/*      */       {
/* 1218 */         con_num++;
/*      */         
/* 1220 */         final int f_con_num = con_num;
/*      */         
/* 1222 */         final Object[] data = (Object[])it2.next();
/*      */         
/* 1224 */         final DHTTransportContact contact = (DHTTransportContact)data[0];
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1229 */         final AESemaphore sem = new AESemaphore("DHTDB:cacheForward");
/*      */         
/* 1231 */         contact.sendFindNode(new DHTTransportReplyHandlerAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void findNodeReply(DHTTransportContact _contact, DHTTransportContact[] _contacts)
/*      */           {
/*      */ 
/*      */ 
/* 1239 */             anti_spoof_done.add(_contact);
/*      */             
/*      */ 
/*      */             try
/*      */             {
/* 1244 */               List<HashWrapper> keys = (List)data[1];
/*      */               
/* 1246 */               byte[][] store_keys = new byte[keys.size()][];
/* 1247 */               DHTTransportValue[][] store_values = new DHTTransportValue[store_keys.length][];
/*      */               
/* 1249 */               keys_published[0] += store_keys.length;
/*      */               
/* 1251 */               for (int i = 0; i < store_keys.length; i++)
/*      */               {
/* 1253 */                 HashWrapper wrapper = (HashWrapper)keys.get(i);
/*      */                 
/* 1255 */                 store_keys[i] = wrapper.getHash();
/*      */                 
/* 1257 */                 List<DHTDBValueImpl> values = (List)republish.get(wrapper);
/*      */                 
/* 1259 */                 store_values[i] = new DHTTransportValue[values.size()];
/*      */                 
/* 1261 */                 values_published[0] += store_values[i].length;
/*      */                 
/* 1263 */                 for (int j = 0; j < values.size(); j++)
/*      */                 {
/* 1265 */                   DHTDBValueImpl value = (DHTDBValueImpl)values.get(j);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/* 1270 */                   store_values[i][j] = value.getValueForRelay(DHTDBImpl.this.local_contact);
/*      */                 }
/*      */               }
/*      */               
/* 1274 */               List<DHTTransportContact> contacts = new ArrayList();
/*      */               
/* 1276 */               contacts.add(contact);
/*      */               
/* 1278 */               republish_ops[0] += 1;
/*      */               
/* 1280 */               DHTDBImpl.this.control.putDirectEncodedKeys(store_keys, "Republish cache: " + f_con_num + " of " + con_tot, store_values, contacts);
/*      */ 
/*      */ 
/*      */             }
/*      */             finally
/*      */             {
/*      */ 
/* 1287 */               sem.release();
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void failed(DHTTransportContact _contact, Throwable _error)
/*      */           {
/*      */             try
/*      */             {
/* 1299 */               DHTLog.log("cacheForward: pre-store findNode failed " + DHTLog.getString(_contact) + " -> failed: " + _error.getMessage());
/*      */               
/* 1301 */               DHTDBImpl.this.router.contactDead(_contact.getID(), false);
/*      */             }
/*      */             finally
/*      */             {
/* 1305 */               sem.release(); } } }, contact.getProtocolVersion() >= 8 ? new byte[0] : new byte[20], (short)1024);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1312 */         sem.reserve();
/*      */       }
/*      */       try
/*      */       {
/* 1316 */         this.this_mon.enter();
/*      */         
/* 1318 */         for (int i = 0; i < stop_caching.size(); i++)
/*      */         {
/* 1320 */           DHTDBMapping mapping = (DHTDBMapping)this.stored_values.remove(stop_caching.get(i));
/*      */           
/* 1322 */           if (mapping != null)
/*      */           {
/* 1324 */             removeFromPrefixMap(mapping);
/*      */             
/* 1326 */             mapping.destroy();
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 1331 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */     
/* 1335 */     DHTStorageBlock[] direct_key_blocks = getDirectKeyBlocks();
/*      */     
/* 1337 */     if (direct_key_blocks.length > 0)
/*      */     {
/* 1339 */       for (int i = 0; i < direct_key_blocks.length; i++)
/*      */       {
/* 1341 */         final DHTStorageBlock key_block = direct_key_blocks[i];
/*      */         
/* 1343 */         List contacts = this.control.getClosestKContactsList(key_block.getKey(), false);
/*      */         
/* 1345 */         boolean forward_it = false;
/*      */         
/*      */ 
/*      */ 
/* 1349 */         for (int j = 0; j < contacts.size(); j++)
/*      */         {
/* 1351 */           DHTTransportContact contact = (DHTTransportContact)contacts.get(j);
/*      */           
/* 1353 */           if (this.router.isID(contact.getID()))
/*      */           {
/* 1355 */             forward_it = true;
/*      */             
/* 1357 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1361 */         for (int j = 0; (forward_it) && (j < contacts.size()); j++)
/*      */         {
/* 1363 */           final DHTTransportContact contact = (DHTTransportContact)contacts.get(j);
/*      */           
/* 1365 */           if (!key_block.hasBeenSentTo(contact))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1370 */             if (!this.router.isID(contact.getID()))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1375 */               if (contact.getProtocolVersion() >= 14)
/*      */               {
/* 1377 */                 final Runnable task = new Runnable()
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/* 1383 */                     contact.sendKeyBlock(new DHTTransportReplyHandlerAdapter()
/*      */                     {
/*      */ 
/*      */ 
/*      */                       public void keyBlockReply(DHTTransportContact _contact)
/*      */                       {
/*      */ 
/* 1390 */                         DHTLog.log("key block forward ok " + DHTLog.getString(_contact));
/*      */                         
/* 1392 */                         DHTDBImpl.9.this.val$key_block.sentTo(_contact);
/*      */                       }
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1400 */                       public void failed(DHTTransportContact _contact, Throwable _error) { DHTLog.log("key block forward failed " + DHTLog.getString(_contact) + " -> failed: " + _error.getMessage()); } }, key_block.getRequest(), key_block.getCertificate());
/*      */                   }
/*      */                 };
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1408 */                 if (anti_spoof_done.contains(contact))
/*      */                 {
/* 1410 */                   task.run();
/*      */                 }
/*      */                 else
/*      */                 {
/* 1414 */                   contact.sendFindNode(new DHTTransportReplyHandlerAdapter()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void findNodeReply(DHTTransportContact contact, DHTTransportContact[] contacts)
/*      */                     {
/*      */ 
/*      */ 
/* 1422 */                       task.run();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void failed(DHTTransportContact _contact, Throwable _error)
/*      */                     {
/* 1431 */                       DHTLog.log("pre-kb findNode failed " + DHTLog.getString(_contact) + " -> failed: " + _error.getMessage());
/*      */                       
/* 1433 */                       DHTDBImpl.this.router.contactDead(_contact.getID(), false); } }, contact.getProtocolVersion() >= 8 ? new byte[0] : new byte[20], (short)1024);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1444 */     return new int[] { values_published[0], keys_published[0], republish_ops[0] };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkCacheExpiration(boolean force)
/*      */   {
/* 1451 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1453 */     if (!force)
/*      */     {
/* 1455 */       long elapsed = now - this.last_cache_expiry_check;
/*      */       
/* 1457 */       if ((elapsed > 0L) && (elapsed < 60000L))
/*      */       {
/* 1459 */         return;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1464 */       this.this_mon.enter();
/*      */       
/* 1466 */       this.last_cache_expiry_check = now;
/*      */       
/* 1468 */       Iterator<DHTDBMapping> it = this.stored_values.values().iterator();
/*      */       
/* 1470 */       while (it.hasNext())
/*      */       {
/* 1472 */         DHTDBMapping mapping = (DHTDBMapping)it.next();
/*      */         
/* 1474 */         if (mapping.getValueCount() == 0)
/*      */         {
/* 1476 */           it.remove();
/*      */           
/* 1478 */           removeFromPrefixMap(mapping);
/*      */           
/* 1480 */           mapping.destroy();
/*      */         }
/*      */         else
/*      */         {
/* 1484 */           Iterator<DHTDBValueImpl> it2 = mapping.getValues();
/*      */           
/* 1486 */           while (it2.hasNext())
/*      */           {
/* 1488 */             DHTDBValueImpl value = (DHTDBValueImpl)it2.next();
/*      */             
/* 1490 */             if (!value.isLocal())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1496 */               int life_hours = value.getLifeTimeHours();
/*      */               
/*      */               int max_age;
/*      */               int max_age;
/* 1500 */               if (life_hours < 1)
/*      */               {
/* 1502 */                 max_age = this.original_republish_interval;
/*      */               }
/*      */               else
/*      */               {
/* 1506 */                 max_age = life_hours * 60 * 60 * 1000;
/*      */                 
/* 1508 */                 if (max_age > 259200000)
/*      */                 {
/* 1510 */                   max_age = 259200000;
/*      */                 }
/*      */               }
/*      */               
/*      */               int grace;
/*      */               int grace;
/* 1516 */               if ((value.getFlags() & 0x100) != 0)
/*      */               {
/* 1518 */                 grace = 0;
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/* 1524 */                 grace = Math.min(3600000, max_age / 4);
/*      */               }
/*      */               
/* 1527 */               if (now > value.getCreationTime() + max_age + grace)
/*      */               {
/* 1529 */                 DHTLog.log("removing cache entry (" + value.getString() + ")");
/*      */                 
/* 1531 */                 it2.remove();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1539 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addToPrefixMap(DHTDBMapping mapping)
/*      */   {
/* 1547 */     DHTDBMapping.ShortHash key = mapping.getShortKey();
/*      */     
/* 1549 */     DHTDBMapping existing = (DHTDBMapping)this.stored_values_prefix_map.get(key);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1554 */     if (existing != null)
/*      */     {
/* 1556 */       byte[] existing_full = existing.getKey().getBytes();
/* 1557 */       byte[] new_full = mapping.getKey().getBytes();
/*      */       
/* 1559 */       if (this.control.computeAndCompareDistances(existing_full, new_full, this.local_contact.getID()) < 0)
/*      */       {
/* 1561 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1565 */     this.stored_values_prefix_map.put(key, mapping);
/*      */     
/* 1567 */     if (this.stored_values_prefix_map.size() > this.stored_values.size())
/*      */     {
/* 1569 */       Debug.out("inconsistent");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeFromPrefixMap(DHTDBMapping mapping)
/*      */   {
/* 1577 */     DHTDBMapping.ShortHash key = mapping.getShortKey();
/*      */     
/* 1579 */     DHTDBMapping existing = (DHTDBMapping)this.stored_values_prefix_map.get(key);
/*      */     
/* 1581 */     if (existing == mapping)
/*      */     {
/* 1583 */       this.stored_values_prefix_map.remove(key);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPreciousStuff()
/*      */   {
/* 1590 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1592 */     Map<HashWrapper, List<DHTDBValueImpl>> republish = new HashMap();
/*      */     
/*      */     try
/*      */     {
/* 1596 */       this.this_mon.enter();
/*      */       
/* 1598 */       Iterator<Map.Entry<HashWrapper, DHTDBMapping>> it = this.stored_values.entrySet().iterator();
/*      */       
/* 1600 */       while (it.hasNext())
/*      */       {
/* 1602 */         Map.Entry<HashWrapper, DHTDBMapping> entry = (Map.Entry)it.next();
/*      */         
/* 1604 */         HashWrapper key = (HashWrapper)entry.getKey();
/*      */         
/* 1606 */         DHTDBMapping mapping = (DHTDBMapping)entry.getValue();
/*      */         
/* 1608 */         Iterator<DHTDBValueImpl> it2 = mapping.getValues();
/*      */         
/* 1610 */         List<DHTDBValueImpl> values = new ArrayList();
/*      */         
/* 1612 */         while (it2.hasNext())
/*      */         {
/* 1614 */           DHTDBValueImpl value = (DHTDBValueImpl)it2.next();
/*      */           
/* 1616 */           if (value.isLocal())
/*      */           {
/* 1618 */             if ((value.getFlags() & 0x20) != 0)
/*      */             {
/* 1620 */               if (now - value.getCreationTime() > 7200000L)
/*      */               {
/* 1622 */                 value.setCreationTime();
/*      */                 
/* 1624 */                 values.add(value);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1630 */         if (values.size() > 0)
/*      */         {
/* 1632 */           republish.put(key, values);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1638 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1641 */     Iterator<Map.Entry<HashWrapper, List<DHTDBValueImpl>>> it = republish.entrySet().iterator();
/*      */     
/* 1643 */     while (it.hasNext())
/*      */     {
/* 1645 */       Map.Entry<HashWrapper, List<DHTDBValueImpl>> entry = (Map.Entry)it.next();
/*      */       
/* 1647 */       HashWrapper key = (HashWrapper)entry.getKey();
/*      */       
/* 1649 */       List<DHTDBValueImpl> values = (List)entry.getValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1654 */       for (int i = 0; i < values.size(); i++)
/*      */       {
/* 1656 */         this.control.putEncodedKey(key.getHash(), "Precious republish", (DHTTransportValue)values.get(i), 0L, true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected DHTTransportContact getLocalContact()
/*      */   {
/* 1664 */     return this.local_contact;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DHTStorageAdapter getAdapter()
/*      */   {
/* 1670 */     return this.adapter;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1677 */     this.logger.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTDBStats getStats()
/*      */   {
/* 1683 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void survey()
/*      */   {
/* 1689 */     if (this.survey_in_progress)
/*      */     {
/* 1691 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1698 */     checkCacheExpiration(false);
/*      */     
/* 1700 */     final byte[] my_id = this.router.getID();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1706 */     final ByteArrayHashMap<DHTTransportContact> id_map = new ByteArrayHashMap();
/*      */     
/* 1708 */     List<DHTTransportContact> all_contacts = this.control.getClosestContactsList(my_id, this.router.getK() * 3, true);
/*      */     
/* 1710 */     for (DHTTransportContact contact : all_contacts)
/*      */     {
/* 1712 */       id_map.put(contact.getID(), contact);
/*      */     }
/*      */     
/* 1715 */     byte[] max_key = my_id;
/* 1716 */     byte[] max_dist = null;
/*      */     
/* 1718 */     final List<HashWrapper> applicable_keys = new ArrayList();
/*      */     try
/*      */     {
/* 1721 */       this.this_mon.enter();
/*      */       
/* 1723 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 1725 */       Iterator<SurveyContactState> s_it = this.survey_state.values().iterator();
/*      */       
/* 1727 */       while (s_it.hasNext())
/*      */       {
/* 1729 */         if (((SurveyContactState)s_it.next()).timeout(now))
/*      */         {
/* 1731 */           s_it.remove();
/*      */         }
/*      */       }
/*      */       
/* 1735 */       Iterator<DHTDBMapping> it = this.stored_values.values().iterator();
/*      */       
/* 1737 */       Set<HashWrapper> existing_times = new HashSet(this.survey_mapping_times.keySet());
/*      */       
/* 1739 */       while (it.hasNext())
/*      */       {
/* 1741 */         DHTDBMapping mapping = (DHTDBMapping)it.next();
/*      */         
/* 1743 */         HashWrapper hw = mapping.getKey();
/*      */         
/* 1745 */         if (existing_times.size() > 0)
/*      */         {
/* 1747 */           existing_times.remove(hw);
/*      */         }
/*      */         
/* 1750 */         if (applyRF(mapping))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1755 */           applicable_keys.add(hw);
/*      */           
/* 1757 */           byte[] key = hw.getBytes();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1768 */           byte[] distance = this.control.computeDistance(my_id, key);
/*      */           
/* 1770 */           if ((max_dist == null) || (this.control.compareDistances(distance, max_dist) > 0))
/*      */           {
/* 1772 */             max_dist = distance;
/* 1773 */             max_key = key;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1779 */       for (HashWrapper hw : existing_times)
/*      */       {
/* 1781 */         this.survey_mapping_times.remove(hw);
/*      */       }
/*      */       
/* 1784 */       this.logger.log("Survey starts: state size=" + this.survey_state.size() + ", all keys=" + this.stored_values.size() + ", applicable keys=" + applicable_keys.size());
/*      */     }
/*      */     finally
/*      */     {
/* 1788 */       this.this_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1795 */     if (max_key == my_id)
/*      */     {
/* 1797 */       this.logger.log("Survey complete - no applicable values");
/*      */       
/* 1799 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1804 */     byte[] obscured_key = this.control.getObfuscatedKey(max_key);
/*      */     
/* 1806 */     final int[] requery_count = { 0 };
/*      */     
/* 1808 */     final boolean[] processing = { false };
/*      */     try
/*      */     {
/* 1811 */       this.survey_in_progress = true;
/*      */       
/* 1813 */       this.control.lookupEncoded(obscured_key, "Neighbourhood survey: basic", 0L, true, new DHTOperationAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1820 */         private final List<DHTTransportContact> contacts = new ArrayList();
/*      */         
/*      */ 
/*      */         private boolean survey_complete;
/*      */         
/*      */ 
/*      */ 
/*      */         public void found(DHTTransportContact contact, boolean is_closest)
/*      */         {
/* 1829 */           if (is_closest)
/*      */           {
/* 1831 */             synchronized (this.contacts)
/*      */             {
/* 1833 */               if (!this.survey_complete)
/*      */               {
/* 1835 */                 this.contacts.add(contact);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void complete(boolean timeout)
/*      */         {
/* 1845 */           boolean requeried = false;
/*      */           try
/*      */           {
/* 1848 */             int hits = 0;
/* 1849 */             int misses = 0;
/*      */             
/*      */ 
/*      */ 
/* 1853 */             byte[] min_dist = null;
/* 1854 */             byte[] min_id = null;
/*      */             
/* 1856 */             synchronized (this.contacts)
/*      */             {
/* 1858 */               for (DHTTransportContact c : this.contacts)
/*      */               {
/* 1860 */                 byte[] id = c.getID();
/*      */                 
/* 1862 */                 if (id_map.containsKey(id))
/*      */                 {
/* 1864 */                   hits++;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1868 */                   misses++;
/*      */                   
/* 1870 */                   if (id_map.size() >= 100)
/*      */                   {
/* 1872 */                     DHTDBImpl.this.log("Max survery size exceeded");
/*      */                     
/* 1874 */                     break;
/*      */                   }
/*      */                   
/* 1877 */                   id_map.put(id, c);
/*      */                   
/* 1879 */                   byte[] distance = DHTDBImpl.this.control.computeDistance(my_id, id);
/*      */                   
/* 1881 */                   if ((min_dist == null) || (DHTDBImpl.this.control.compareDistances(distance, min_dist) < 0))
/*      */                   {
/* 1883 */                     min_dist = distance;
/* 1884 */                     min_id = id;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 1889 */               this.contacts.clear();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1894 */             if ((misses > 0) && (misses * 100 / (hits + misses) >= 25) && (id_map.size() < 100))
/*      */             {
/* 1896 */               int tmp235_234 = 0; int[] tmp235_231 = requery_count; int tmp237_236 = tmp235_231[tmp235_234];tmp235_231[tmp235_234] = (tmp237_236 + 1); if (tmp237_236 < 5)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1904 */                 DHTDBImpl.this.control.lookupEncoded(min_id, "Neighbourhood survey: level=" + requery_count[0], 0L, true, this);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1911 */                 requeried = true;
/*      */ 
/*      */ 
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*      */ 
/*      */ 
/* 1927 */             if (!requeried)
/*      */             {
/* 1929 */               synchronized (this.contacts)
/*      */               {
/* 1931 */                 this.survey_complete = true;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1938 */               DHTDBImpl.this.processSurvey(my_id, applicable_keys, id_map);
/*      */               
/* 1940 */               processing[0] = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1948 */       if (processing[0] == 0)
/*      */       {
/* 1950 */         this.logger.log("Survey complete - no applicable nodes");
/*      */         
/* 1952 */         this.survey_in_progress = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void processSurvey(byte[] survey_my_id, List<HashWrapper> applicable_keys, ByteArrayHashMap<DHTTransportContact> survey)
/*      */   {
/* 1963 */     boolean went_async = false;
/*      */     try
/*      */     {
/* 1966 */       byte[][] node_ids = new byte[survey.size()][];
/*      */       
/* 1968 */       int pos = 0;
/*      */       
/* 1970 */       for (byte[] id : survey.keys())
/*      */       {
/* 1972 */         node_ids[(pos++)] = id;
/*      */       }
/*      */       
/* 1975 */       ByteArrayHashMap<List<DHTDBMapping>> value_map = new ByteArrayHashMap();
/*      */       
/* 1977 */       Map<DHTTransportContact, ByteArrayHashMap<List<DHTDBMapping>>> request_map = new HashMap();
/*      */       
/* 1979 */       Map<DHTDBMapping, List<DHTTransportContact>> mapping_to_node_map = new HashMap();
/*      */       
/* 1981 */       int max_nodes = Math.min(node_ids.length, this.router.getK());
/*      */       try
/*      */       {
/* 1984 */         this.this_mon.enter();
/*      */         
/* 1986 */         Iterator<HashWrapper> it = applicable_keys.iterator();
/*      */         
/* 1988 */         int value_count = 0;
/*      */         
/* 1990 */         while (it.hasNext())
/*      */         {
/* 1992 */           DHTDBMapping mapping = (DHTDBMapping)this.stored_values.get(it.next());
/*      */           
/* 1994 */           if (mapping != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1999 */             value_count++;
/*      */             
/* 2001 */             final byte[] key = mapping.getKey().getBytes();
/*      */             
/*      */ 
/*      */ 
/* 2005 */             Arrays.sort(node_ids, new Comparator()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public int compare(byte[] o1, byte[] o2)
/*      */               {
/*      */ 
/*      */ 
/* 2014 */                 return DHTDBImpl.this.control.computeAndCompareDistances(o1, o2, key);
/*      */               }
/*      */               
/* 2017 */             });
/* 2018 */             boolean found_myself = false;
/*      */             
/* 2020 */             for (int i = 0; i < max_nodes; i++)
/*      */             {
/* 2022 */               byte[] id = node_ids[i];
/*      */               
/* 2024 */               if (Arrays.equals(survey_my_id, id))
/*      */               {
/* 2026 */                 found_myself = true;
/*      */                 
/* 2028 */                 break;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2034 */             if (found_myself)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2043 */               List<DHTTransportContact> node_list = new ArrayList(max_nodes);
/*      */               
/* 2045 */               mapping_to_node_map.put(mapping, node_list);
/*      */               
/* 2047 */               for (int i = 0; i < max_nodes; i++)
/*      */               {
/* 2049 */                 byte[] id = node_ids[i];
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2055 */                 if (!Arrays.equals(survey_my_id, id))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 2060 */                   List<DHTDBMapping> list = (List)value_map.get(id);
/*      */                   
/* 2062 */                   if (list == null)
/*      */                   {
/* 2064 */                     list = new ArrayList();
/*      */                     
/* 2066 */                     value_map.put(id, list);
/*      */                   }
/*      */                   
/* 2069 */                   list.add(mapping);
/*      */                   
/* 2071 */                   node_list.add(survey.get(id));
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2081 */         for (byte[] id : node_ids)
/*      */         {
/* 2083 */           int MAX_PREFIX_TEST = 3;
/*      */           
/* 2085 */           List<DHTDBMapping> all_entries = (List)value_map.remove(id);
/*      */           
/* 2087 */           ByteArrayHashMap<List<DHTDBMapping>> prefix_map = new ByteArrayHashMap();
/*      */           
/* 2089 */           if (all_entries != null)
/*      */           {
/* 2091 */             prefix_map.put(new byte[0], all_entries);
/*      */             
/* 2093 */             for (int i = 0; i < 3; i++)
/*      */             {
/* 2095 */               List<byte[]> prefixes = prefix_map.keys();
/*      */               
/* 2097 */               for (byte[] prefix : prefixes)
/*      */               {
/* 2099 */                 if (prefix.length == i)
/*      */                 {
/* 2101 */                   List<DHTDBMapping> list = (List)prefix_map.get(prefix);
/*      */                   
/* 2103 */                   if (list.size() >= 2)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 2108 */                     ByteArrayHashMap<List<DHTDBMapping>> temp_map = new ByteArrayHashMap();
/*      */                     
/* 2110 */                     for (DHTDBMapping mapping : list)
/*      */                     {
/* 2112 */                       byte[] key = mapping.getKey().getBytes();
/*      */                       
/* 2114 */                       byte[] sub_prefix = new byte[i + 1];
/*      */                       
/* 2116 */                       System.arraycopy(key, 0, sub_prefix, 0, i + 1);
/*      */                       
/* 2118 */                       List<DHTDBMapping> entries = (List)temp_map.get(sub_prefix);
/*      */                       
/* 2120 */                       if (entries == null)
/*      */                       {
/* 2122 */                         entries = new ArrayList();
/*      */                         
/* 2124 */                         temp_map.put(sub_prefix, entries);
/*      */                       }
/*      */                       
/* 2127 */                       entries.add(mapping);
/*      */                     }
/*      */                     
/* 2130 */                     List<DHTDBMapping> new_list = new ArrayList(list.size());
/*      */                     
/* 2132 */                     List<byte[]> temp_keys = temp_map.keys();
/*      */                     
/* 2134 */                     for (byte[] k : temp_keys)
/*      */                     {
/* 2136 */                       List<DHTDBMapping> entries = (List)temp_map.get(k);
/*      */                       
/* 2138 */                       int num = entries.size();
/*      */                       
/*      */ 
/*      */ 
/* 2142 */                       int outer_cost = num * (6 - i);
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/* 2147 */                       int inner_cost = i + 4 + num * (6 - i - 1);
/*      */                       
/* 2149 */                       if (inner_cost < outer_cost)
/*      */                       {
/* 2151 */                         prefix_map.put(k, entries);
/*      */                       }
/*      */                       else
/*      */                       {
/* 2155 */                         new_list.addAll(entries);
/*      */                       }
/*      */                     }
/*      */                     
/* 2159 */                     if (new_list.size() == 0)
/*      */                     {
/* 2161 */                       prefix_map.remove(prefix);
/*      */                     }
/*      */                     else
/*      */                     {
/* 2165 */                       prefix_map.put(prefix, new_list);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 2171 */             String str = "";
/*      */             
/* 2173 */             int encoded_size = 1;
/*      */             
/* 2175 */             List<byte[]> prefixes = prefix_map.keys();
/*      */             
/* 2177 */             for (byte[] prefix : prefixes)
/*      */             {
/* 2179 */               encoded_size += 3 + prefix.length;
/*      */               
/* 2181 */               List<DHTDBMapping> entries = (List)prefix_map.get(prefix);
/*      */               
/* 2183 */               encoded_size += (6 - prefix.length) * entries.size();
/*      */               
/* 2185 */               str = str + (str.length() == 0 ? "" : ", ") + ByteFormatter.encodeString(prefix) + "->" + entries.size();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2192 */             if (prefixes.size() > 0)
/*      */             {
/* 2194 */               request_map.put(survey.get(id), prefix_map);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 2200 */         this.this_mon.exit();
/*      */       }
/*      */       
/* 2203 */       LinkedList<Map.Entry<DHTTransportContact, ByteArrayHashMap<List<DHTDBMapping>>>> to_do = new LinkedList(request_map.entrySet());
/*      */       
/* 2205 */       Map<DHTTransportContact, Object[]> replies = new HashMap();
/*      */       
/* 2207 */       for (int i = 0; i < Math.min(3, to_do.size()); i++)
/*      */       {
/* 2209 */         went_async = true;
/*      */         
/* 2211 */         doQuery(survey_my_id, request_map.size(), mapping_to_node_map, to_do, replies, null, null, null);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2216 */       if (!went_async)
/*      */       {
/* 2218 */         this.logger.log("Survey complete - no applicable queries");
/*      */         
/* 2220 */         this.survey_in_progress = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean applyRF(DHTDBMapping mapping)
/*      */   {
/* 2229 */     if (mapping.getDiversificationType() != 1)
/*      */     {
/* 2231 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2236 */     Iterator<DHTDBValueImpl> it2 = mapping.getValues();
/*      */     
/* 2238 */     if (!it2.hasNext())
/*      */     {
/* 2240 */       return false;
/*      */     }
/*      */     
/* 2243 */     int min_period = Integer.MAX_VALUE;
/*      */     
/* 2245 */     long min_create = Long.MAX_VALUE;
/*      */     
/* 2247 */     while (it2.hasNext())
/*      */     {
/* 2249 */       DHTDBValueImpl value = (DHTDBValueImpl)it2.next();
/*      */       
/* 2251 */       byte rep_fact = value.getReplicationFactor();
/*      */       
/* 2253 */       if ((rep_fact == -1) || (rep_fact == 0))
/*      */       {
/* 2255 */         return false;
/*      */       }
/*      */       
/* 2258 */       int hours = value.getReplicationFrequencyHours() & 0xFF;
/*      */       
/* 2260 */       if (hours < min_period)
/*      */       {
/* 2262 */         min_period = hours;
/*      */       }
/*      */       
/* 2265 */       min_create = Math.min(min_create, value.getCreationTime());
/*      */     }
/*      */     
/* 2268 */     if (min_period > 0)
/*      */     {
/* 2270 */       HashWrapper hw = mapping.getKey();
/*      */       
/* 2272 */       Long next_time = (Long)this.survey_mapping_times.get(hw);
/*      */       
/* 2274 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 2276 */       if ((next_time != null) && (next_time.longValue() > now))
/*      */       {
/* 2278 */         return false;
/*      */       }
/*      */       
/* 2281 */       long period = min_period * 60 * 60 * 1000;
/*      */       
/* 2283 */       long offset_time = (SystemTime.getCurrentTime() - min_create) % period;
/*      */       
/* 2285 */       long rand = RandomUtils.nextInt(1800000) - 900000;
/*      */       
/* 2287 */       long new_next_time = now - offset_time + period + rand;
/*      */       
/* 2289 */       if (new_next_time < now + 1800000L)
/*      */       {
/* 2291 */         new_next_time += period;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2298 */       this.survey_mapping_times.put(hw, Long.valueOf(new_next_time));
/*      */       
/* 2300 */       if (next_time == null)
/*      */       {
/* 2302 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2307 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void doQuery(final byte[] survey_my_id, final int total, final Map<DHTDBMapping, List<DHTTransportContact>> mapping_to_node_map, final LinkedList<Map.Entry<DHTTransportContact, ByteArrayHashMap<List<DHTDBMapping>>>> to_do, final Map<DHTTransportContact, Object[]> replies, DHTTransportContact done_contact, List<DHTDBMapping> done_mappings, List<byte[]> done_reply)
/*      */   {
/*      */     Map.Entry<DHTTransportContact, ByteArrayHashMap<List<DHTDBMapping>>> entry;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2323 */     synchronized (to_do)
/*      */     {
/* 2325 */       if (done_contact != null)
/*      */       {
/* 2327 */         replies.put(done_contact, new Object[] { done_mappings, done_reply });
/*      */       }
/*      */       
/* 2330 */       if (to_do.size() == 0)
/*      */       {
/* 2332 */         if (replies.size() == total)
/*      */         {
/* 2334 */           queriesComplete(survey_my_id, mapping_to_node_map, replies);
/*      */         }
/*      */         
/* 2337 */         return;
/*      */       }
/*      */       
/* 2340 */       entry = (Map.Entry)to_do.removeFirst();
/*      */     }
/*      */     
/* 2343 */     DHTTransportContact contact = (DHTTransportContact)entry.getKey();
/*      */     
/* 2345 */     boolean handled = false;
/*      */     try
/*      */     {
/* 2348 */       if (contact.getProtocolVersion() >= 26)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2354 */         final List<DHTDBMapping> mapping_list = new ArrayList();
/*      */         
/* 2356 */         ByteArrayHashMap<List<DHTDBMapping>> map = (ByteArrayHashMap)entry.getValue();
/*      */         
/* 2358 */         List<byte[]> prefixes = map.keys();
/*      */         
/* 2360 */         List<Object[]> encoded = new ArrayList(prefixes.size());
/*      */         try
/*      */         {
/* 2363 */           this.this_mon.enter();
/*      */           
/* 2365 */           SurveyContactState contact_state = (SurveyContactState)this.survey_state.get(new HashWrapper(contact.getID()));
/*      */           
/* 2367 */           for (byte[] prefix : prefixes)
/*      */           {
/* 2369 */             prefix_len = prefix.length;
/* 2370 */             suffix_len = 6 - prefix_len;
/*      */             
/* 2372 */             List<DHTDBMapping> mappings = (List)map.get(prefix);
/*      */             
/* 2374 */             l = new ArrayList(mappings.size());
/*      */             
/* 2376 */             encoded.add(new Object[] { prefix, l });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2384 */             for (DHTDBMapping m : mappings)
/*      */             {
/* 2386 */               if ((contact_state == null) || 
/*      */               
/* 2388 */                 (!contact_state.testMapping(m)))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2398 */                 mapping_list.add(m);
/*      */                 
/* 2400 */                 byte[] k = m.getKey().getBytes();
/*      */                 
/* 2402 */                 byte[] suffix = new byte[suffix_len];
/*      */                 
/* 2404 */                 System.arraycopy(k, prefix_len, suffix, 0, suffix_len);
/*      */                 
/* 2406 */                 l.add(suffix); } } }
/*      */           int prefix_len;
/*      */           int suffix_len;
/*      */           List<byte[]> l;
/* 2410 */           if (Arrays.equals(contact.getID(), survey_my_id))
/*      */           {
/* 2412 */             Debug.out("inconsistent - we shouldn't query ourselves!");
/*      */           }
/*      */           
/* 2415 */           contact.sendQueryStore(new DHTTransportReplyHandlerAdapter()
/*      */           {
/*      */             /* Error */
/*      */             public void queryStoreReply(DHTTransportContact contact, List<byte[]> response)
/*      */             {
/*      */               // Byte code:
/*      */               //   0: aload_0
/*      */               //   1: getfield 51	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:this$0	Lcom/aelitis/azureus/core/dht/db/impl/DHTDBImpl;
/*      */               //   4: aload_0
/*      */               //   5: getfield 50	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$survey_my_id	[B
/*      */               //   8: aload_0
/*      */               //   9: getfield 49	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$total	I
/*      */               //   12: aload_0
/*      */               //   13: getfield 54	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_to_node_map	Ljava/util/Map;
/*      */               //   16: aload_0
/*      */               //   17: getfield 52	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$to_do	Ljava/util/LinkedList;
/*      */               //   20: aload_0
/*      */               //   21: getfield 55	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$replies	Ljava/util/Map;
/*      */               //   24: aload_1
/*      */               //   25: aload_0
/*      */               //   26: getfield 53	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_list	Ljava/util/List;
/*      */               //   29: aload_2
/*      */               //   30: invokevirtual 56	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl:doQuery	([BILjava/util/Map;Ljava/util/LinkedList;Ljava/util/Map;Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;Ljava/util/List;Ljava/util/List;)V
/*      */               //   33: goto +39 -> 72
/*      */               //   36: astore_3
/*      */               //   37: aload_0
/*      */               //   38: getfield 51	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:this$0	Lcom/aelitis/azureus/core/dht/db/impl/DHTDBImpl;
/*      */               //   41: aload_0
/*      */               //   42: getfield 50	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$survey_my_id	[B
/*      */               //   45: aload_0
/*      */               //   46: getfield 49	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$total	I
/*      */               //   49: aload_0
/*      */               //   50: getfield 54	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_to_node_map	Ljava/util/Map;
/*      */               //   53: aload_0
/*      */               //   54: getfield 52	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$to_do	Ljava/util/LinkedList;
/*      */               //   57: aload_0
/*      */               //   58: getfield 55	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$replies	Ljava/util/Map;
/*      */               //   61: aload_1
/*      */               //   62: aload_0
/*      */               //   63: getfield 53	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_list	Ljava/util/List;
/*      */               //   66: aload_2
/*      */               //   67: invokevirtual 56	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl:doQuery	([BILjava/util/Map;Ljava/util/LinkedList;Ljava/util/Map;Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;Ljava/util/List;Ljava/util/List;)V
/*      */               //   70: aload_3
/*      */               //   71: athrow
/*      */               //   72: return
/*      */               // Line number table:
/*      */               //   Java source line #2434	-> byte code offset #0
/*      */               //   Java source line #2435	-> byte code offset #33
/*      */               //   Java source line #2434	-> byte code offset #36
/*      */               //   Java source line #2436	-> byte code offset #72
/*      */               // Local variable table:
/*      */               //   start	length	slot	name	signature
/*      */               //   0	73	0	this	13
/*      */               //   0	73	1	contact	DHTTransportContact
/*      */               //   0	73	2	response	List<byte[]>
/*      */               //   36	35	3	localObject	Object
/*      */               // Exception table:
/*      */               //   from	to	target	type
/*      */               //   36	37	36	finally
/*      */             }
/*      */             
/*      */             /* Error */
/*      */             public void failed(DHTTransportContact contact, Throwable error)
/*      */             {
/*      */               // Byte code:
/*      */               //   0: aload_0
/*      */               //   1: getfield 51	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:this$0	Lcom/aelitis/azureus/core/dht/db/impl/DHTDBImpl;
/*      */               //   4: aload_0
/*      */               //   5: getfield 50	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$survey_my_id	[B
/*      */               //   8: aload_0
/*      */               //   9: getfield 49	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$total	I
/*      */               //   12: aload_0
/*      */               //   13: getfield 54	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_to_node_map	Ljava/util/Map;
/*      */               //   16: aload_0
/*      */               //   17: getfield 52	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$to_do	Ljava/util/LinkedList;
/*      */               //   20: aload_0
/*      */               //   21: getfield 55	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$replies	Ljava/util/Map;
/*      */               //   24: aload_1
/*      */               //   25: aload_0
/*      */               //   26: getfield 53	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_list	Ljava/util/List;
/*      */               //   29: aconst_null
/*      */               //   30: invokevirtual 56	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl:doQuery	([BILjava/util/Map;Ljava/util/LinkedList;Ljava/util/Map;Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;Ljava/util/List;Ljava/util/List;)V
/*      */               //   33: goto +39 -> 72
/*      */               //   36: astore_3
/*      */               //   37: aload_0
/*      */               //   38: getfield 51	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:this$0	Lcom/aelitis/azureus/core/dht/db/impl/DHTDBImpl;
/*      */               //   41: aload_0
/*      */               //   42: getfield 50	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$survey_my_id	[B
/*      */               //   45: aload_0
/*      */               //   46: getfield 49	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$total	I
/*      */               //   49: aload_0
/*      */               //   50: getfield 54	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_to_node_map	Ljava/util/Map;
/*      */               //   53: aload_0
/*      */               //   54: getfield 52	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$to_do	Ljava/util/LinkedList;
/*      */               //   57: aload_0
/*      */               //   58: getfield 55	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$replies	Ljava/util/Map;
/*      */               //   61: aload_1
/*      */               //   62: aload_0
/*      */               //   63: getfield 53	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl$13:val$mapping_list	Ljava/util/List;
/*      */               //   66: aconst_null
/*      */               //   67: invokevirtual 56	com/aelitis/azureus/core/dht/db/impl/DHTDBImpl:doQuery	([BILjava/util/Map;Ljava/util/LinkedList;Ljava/util/Map;Lcom/aelitis/azureus/core/dht/transport/DHTTransportContact;Ljava/util/List;Ljava/util/List;)V
/*      */               //   70: aload_3
/*      */               //   71: athrow
/*      */               //   72: return
/*      */               // Line number table:
/*      */               //   Java source line #2449	-> byte code offset #0
/*      */               //   Java source line #2450	-> byte code offset #33
/*      */               //   Java source line #2449	-> byte code offset #36
/*      */               //   Java source line #2451	-> byte code offset #72
/*      */               // Local variable table:
/*      */               //   start	length	slot	name	signature
/*      */               //   0	73	0	this	13
/*      */               //   0	73	1	contact	DHTTransportContact
/*      */               //   0	73	2	error	Throwable
/*      */               //   36	35	3	localObject	Object
/*      */               // Exception table:
/*      */               //   from	to	target	type
/*      */               //   36	37	36	finally
/*      */             }
/* 2415 */           }, 6, encoded);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2454 */           handled = true;
/*      */         }
/*      */         finally
/*      */         {
/* 2458 */           this.this_mon.exit();
/*      */         }
/*      */       }
/*      */     } finally {
/*      */       List<DHTDBMapping> mapping_list;
/*      */       ByteArrayHashMap<List<DHTDBMapping>> map;
/*      */       List<byte[]> prefixes;
/*      */       Iterator i$;
/*      */       byte[] prefix;
/* 2467 */       if (!handled)
/*      */       {
/* 2469 */         List<DHTDBMapping> mapping_list = new ArrayList();
/*      */         
/* 2471 */         ByteArrayHashMap<List<DHTDBMapping>> map = (ByteArrayHashMap)entry.getValue();
/*      */         
/* 2473 */         List<byte[]> prefixes = map.keys();
/*      */         
/* 2475 */         for (byte[] prefix : prefixes)
/*      */         {
/* 2477 */           mapping_list.addAll((Collection)map.get(prefix));
/*      */         }
/*      */         
/* 2480 */         doQuery(survey_my_id, total, mapping_to_node_map, to_do, replies, contact, mapping_list, null);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void queriesComplete(byte[] survey_my_id, Map<DHTDBMapping, List<DHTTransportContact>> mapping_to_node_map, Map<DHTTransportContact, Object[]> replies)
/*      */   {
/* 2491 */     Map<SurveyContactState, List<DHTDBMapping>> store_ops = new HashMap();
/*      */     try
/*      */     {
/* 2494 */       this.this_mon.enter();
/*      */       
/* 2496 */       if (!Arrays.equals(survey_my_id, this.router.getID()))
/*      */       {
/* 2498 */         this.logger.log("Survey abandoned - router changed"); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2507 */       totals = new HashMap();
/*      */       
/* 2509 */       for (Map.Entry<DHTTransportContact, Object[]> entry : replies.entrySet())
/*      */       {
/* 2511 */         DHTTransportContact contact = (DHTTransportContact)entry.getKey();
/*      */         
/* 2513 */         HashWrapper hw = new HashWrapper(contact.getID());
/*      */         
/* 2515 */         SurveyContactState contact_state = (SurveyContactState)this.survey_state.get(hw);
/*      */         
/* 2517 */         if (contact_state != null)
/*      */         {
/* 2519 */           contact_state.updateContactDetails(contact);
/*      */         }
/*      */         else
/*      */         {
/* 2523 */           contact_state = new SurveyContactState(contact);
/*      */           
/* 2525 */           this.survey_state.put(hw, contact_state);
/*      */         }
/*      */         
/* 2528 */         contact_state.updateUseTime();
/*      */         
/* 2530 */         Object[] temp = (Object[])entry.getValue();
/*      */         
/* 2532 */         List<DHTDBMapping> mappings = (List)temp[0];
/* 2533 */         List<byte[]> reply = (List)temp[1];
/*      */         
/* 2535 */         if (reply == null)
/*      */         {
/* 2537 */           contact_state.contactFailed();
/*      */         }
/*      */         else
/*      */         {
/* 2541 */           contact_state.contactOK();
/*      */           
/* 2543 */           if (mappings.size() != reply.size())
/*      */           {
/* 2545 */             Debug.out("Inconsistent: mappings=" + mappings.size() + ", reply=" + reply.size());
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 2550 */             Iterator<DHTDBMapping> it1 = mappings.iterator();
/* 2551 */             Iterator<byte[]> it2 = reply.iterator();
/*      */             
/* 2553 */             while (it1.hasNext())
/*      */             {
/* 2555 */               DHTDBMapping mapping = (DHTDBMapping)it1.next();
/* 2556 */               byte[] rep = (byte[])it2.next();
/*      */               
/* 2558 */               if (rep == null)
/*      */               {
/* 2560 */                 contact_state.removeMapping(mapping);
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/* 2566 */                 DHTDBMapping mapping_to_check = (DHTDBMapping)this.stored_values_prefix_map.get(mapping.getShortKey());
/*      */                 
/* 2568 */                 if (mapping_to_check != null)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2574 */                   byte[] k = mapping_to_check.getKey().getBytes();
/*      */                   
/* 2576 */                   int rep_len = rep.length;
/*      */                   
/* 2578 */                   if ((rep_len < 2) || (rep_len >= k.length))
/*      */                   {
/* 2580 */                     Debug.out("Invalid rep_len: " + rep_len);
/*      */ 
/*      */                   }
/*      */                   else
/*      */                   {
/* 2585 */                     boolean match = true;
/*      */                     
/* 2587 */                     int offset = k.length - rep_len;
/*      */                     
/* 2589 */                     for (int i = 0; i < rep_len; i++)
/*      */                     {
/* 2591 */                       if (rep[i] != k[(i + offset)])
/*      */                       {
/* 2593 */                         match = false;
/*      */                         
/* 2595 */                         break;
/*      */                       }
/*      */                     }
/*      */                     
/* 2599 */                     if (match)
/*      */                     {
/* 2601 */                       contact_state.addMapping(mapping);
/*      */                     }
/*      */                     else
/*      */                     {
/* 2605 */                       contact_state.removeMapping(mapping);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 2611 */             Set<DHTDBMapping> contact_mappings = contact_state.getMappings();
/*      */             
/* 2613 */             for (DHTDBMapping m : contact_mappings)
/*      */             {
/* 2615 */               int[] t = (int[])totals.get(m);
/*      */               
/* 2617 */               if (t == null)
/*      */               {
/* 2619 */                 t = new int[] { 2 };
/*      */                 
/* 2621 */                 totals.put(m, t);
/*      */               }
/*      */               else
/*      */               {
/* 2625 */                 t[0] += 1;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2631 */       for (Map.Entry<DHTDBMapping, List<DHTTransportContact>> entry : mapping_to_node_map.entrySet())
/*      */       {
/* 2633 */         DHTDBMapping mapping = (DHTDBMapping)entry.getKey();
/* 2634 */         List<DHTTransportContact> contacts = (List)entry.getValue();
/*      */         
/* 2636 */         int[] t = (int[])totals.get(mapping);
/*      */         
/*      */         int copies;
/*      */         int copies;
/* 2640 */         if (t == null)
/*      */         {
/* 2642 */           copies = 1;
/*      */         }
/*      */         else
/*      */         {
/* 2646 */           copies = t[0];
/*      */         }
/*      */         
/* 2649 */         Iterator<DHTDBValueImpl> values = mapping.getValues();
/*      */         
/* 2651 */         if (values.hasNext())
/*      */         {
/* 2653 */           int max_replication_factor = -1;
/*      */           
/* 2655 */           while (values.hasNext())
/*      */           {
/* 2657 */             DHTDBValueImpl value = (DHTDBValueImpl)values.next();
/*      */             
/* 2659 */             int rf = value.getReplicationFactor();
/*      */             
/* 2661 */             if (rf > max_replication_factor)
/*      */             {
/* 2663 */               max_replication_factor = rf;
/*      */             }
/*      */           }
/*      */           
/* 2667 */           if (max_replication_factor != 0)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2672 */             if (max_replication_factor > this.router.getK())
/*      */             {
/* 2674 */               max_replication_factor = this.router.getK();
/*      */             }
/*      */             
/* 2677 */             if (copies < max_replication_factor)
/*      */             {
/* 2679 */               int required = max_replication_factor - copies;
/*      */               
/* 2681 */               List<SurveyContactState> potential_targets = new ArrayList();
/*      */               
/* 2683 */               List<byte[]> addresses = new ArrayList(contacts.size());
/*      */               
/* 2685 */               for (DHTTransportContact c : contacts)
/*      */               {
/* 2687 */                 if (c.getProtocolVersion() >= 26)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 2692 */                   addresses.add(AddressUtils.getAddressBytes(c.getAddress()));
/*      */                   
/* 2694 */                   SurveyContactState contact_state = (SurveyContactState)this.survey_state.get(new HashWrapper(c.getID()));
/*      */                   
/* 2696 */                   if ((contact_state != null) && (!contact_state.testMapping(mapping)))
/*      */                   {
/* 2698 */                     potential_targets.add(contact_state);
/*      */                   }
/*      */                 }
/*      */               }
/* 2702 */               Set<HashWrapper> bad_addresses = new HashSet();
/*      */               
/* 2704 */               for (Iterator i$ = addresses.iterator(); i$.hasNext();) { a1 = (byte[])i$.next();
/*      */                 
/* 2706 */                 for (byte[] a2 : addresses)
/*      */                 {
/*      */ 
/*      */ 
/* 2710 */                   if ((a1 != a2) && (a1.length == a2.length) && (a1.length == 4))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2717 */                     if ((a1[0] == a2[0]) && (a1[1] == a2[1]))
/*      */                     {
/* 2719 */                       log("/16 match on " + ByteFormatter.encodeString(a1) + "/" + ByteFormatter.encodeString(a2));
/*      */                       
/* 2721 */                       bad_addresses.add(new HashWrapper(a1));
/* 2722 */                       bad_addresses.add(new HashWrapper(a2));
/*      */                     } }
/*      */                 }
/*      */               }
/*      */               byte[] a1;
/* 2727 */               final byte[] key = mapping.getKey().getBytes();
/*      */               
/* 2729 */               Collections.sort(potential_targets, new Comparator()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public int compare(DHTDBImpl.SurveyContactState o1, DHTDBImpl.SurveyContactState o2)
/*      */                 {
/*      */ 
/*      */ 
/* 2738 */                   boolean o1_bad = o1.getConsecFails() >= 2;
/* 2739 */                   boolean o2_bad = o2.getConsecFails() >= 2;
/*      */                   
/* 2741 */                   if (o1_bad == o2_bad)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2763 */                     return DHTDBImpl.this.control.computeAndCompareDistances(o1.getContact().getID(), o2.getContact().getID(), key);
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2771 */                   if (o1_bad)
/*      */                   {
/* 2773 */                     return 1;
/*      */                   }
/*      */                   
/*      */ 
/* 2777 */                   return -1;
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/* 2782 */               });
/* 2783 */               int avail = Math.min(required, potential_targets.size());
/*      */               
/* 2785 */               for (int i = 0; i < avail; i++)
/*      */               {
/* 2787 */                 SurveyContactState target = (SurveyContactState)potential_targets.get(i);
/*      */                 
/* 2789 */                 if ((bad_addresses.size() > 0) && (bad_addresses.contains(new HashWrapper(AddressUtils.getAddressBytes(target.getContact().getAddress())))))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2795 */                   target.addMapping(mapping);
/*      */                 }
/*      */                 else
/*      */                 {
/* 2799 */                   List<DHTDBMapping> m = (List)store_ops.get(target);
/*      */                   
/* 2801 */                   if (m == null)
/*      */                   {
/* 2803 */                     m = new ArrayList();
/*      */                     
/* 2805 */                     store_ops.put(target, m);
/*      */                   }
/*      */                   
/* 2808 */                   m.add(mapping);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally { Map<DHTDBMapping, int[]> totals;
/* 2816 */       this.this_mon.exit();
/*      */       
/* 2818 */       this.survey_in_progress = false;
/*      */     }
/*      */     
/* 2821 */     this.logger.log("Survey complete - " + store_ops.size() + " store ops");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2827 */     for (Map.Entry<SurveyContactState, List<DHTDBMapping>> store_op : store_ops.entrySet())
/*      */     {
/* 2829 */       final SurveyContactState contact = (SurveyContactState)store_op.getKey();
/* 2830 */       final List<DHTDBMapping> keys = (List)store_op.getValue();
/*      */       
/* 2832 */       final byte[][] store_keys = new byte[keys.size()][];
/* 2833 */       final DHTTransportValue[][] store_values = new DHTTransportValue[store_keys.length][];
/*      */       
/* 2835 */       for (int i = 0; i < store_keys.length; i++)
/*      */       {
/* 2837 */         DHTDBMapping mapping = (DHTDBMapping)keys.get(i);
/*      */         
/* 2839 */         store_keys[i] = mapping.getKey().getBytes();
/*      */         
/* 2841 */         List<DHTTransportValue> v = new ArrayList();
/*      */         
/* 2843 */         Iterator<DHTDBValueImpl> it = mapping.getValues();
/*      */         
/* 2845 */         while (it.hasNext())
/*      */         {
/* 2847 */           DHTDBValueImpl value = (DHTDBValueImpl)it.next();
/*      */           
/* 2849 */           if (!value.isLocal())
/*      */           {
/* 2851 */             v.add(value.getValueForRelay(this.local_contact));
/*      */           }
/*      */         }
/*      */         
/* 2855 */         store_values[i] = ((DHTTransportValue[])v.toArray(new DHTTransportValue[v.size()]));
/*      */       }
/*      */       
/* 2858 */       final DHTTransportContact d_contact = contact.getContact();
/*      */       
/* 2860 */       final Runnable store_exec = new Runnable()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2870 */           DHTDBImpl.this.control.putDirectEncodedKeys(store_keys, "Replication forward", store_values, d_contact, new DHTOperationAdapter()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void complete(boolean timeout)
/*      */             {
/*      */ 
/*      */ 
/*      */               try
/*      */               {
/*      */ 
/*      */ 
/* 2882 */                 DHTDBImpl.this.this_mon.enter();
/*      */                 
/* 2884 */                 if (timeout)
/*      */                 {
/* 2886 */                   DHTDBImpl.15.this.val$contact.contactFailed();
/*      */                 }
/*      */                 else
/*      */                 {
/* 2890 */                   DHTDBImpl.15.this.val$contact.contactOK();
/*      */                   
/* 2892 */                   for (DHTDBMapping m : DHTDBImpl.15.this.val$keys)
/*      */                   {
/* 2894 */                     DHTDBImpl.15.this.val$contact.addMapping(m);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               finally {
/* 2899 */                 DHTDBImpl.this.this_mon.exit();
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       };
/*      */       
/* 2906 */       if (d_contact.getRandomIDType() != 1)
/*      */       {
/* 2908 */         Debug.out("derp");
/*      */       }
/*      */       
/* 2911 */       if (d_contact.getRandomID() == 0)
/*      */       {
/* 2913 */         d_contact.sendFindNode(new DHTTransportReplyHandlerAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void findNodeReply(DHTTransportContact _contact, DHTTransportContact[] _contacts)
/*      */           {
/*      */ 
/*      */ 
/* 2921 */             store_exec.run();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void failed(DHTTransportContact _contact, Throwable _error)
/*      */           {
/*      */             try
/*      */             {
/* 2930 */               DHTDBImpl.this.this_mon.enter();
/*      */               
/* 2932 */               contact.contactFailed();
/*      */             }
/*      */             finally
/*      */             {
/* 2936 */               DHTDBImpl.this.this_mon.exit(); } } }, d_contact.getProtocolVersion() >= 8 ? new byte[0] : new byte[20], (short)1024);
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/* 2944 */         store_exec.run();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void sleep()
/*      */   {
/* 2952 */     Iterator<Map.Entry<HashWrapper, DHTDBMapping>> it = this.stored_values.entrySet().iterator();
/*      */     
/* 2954 */     while (it.hasNext())
/*      */     {
/* 2956 */       Map.Entry<HashWrapper, DHTDBMapping> entry = (Map.Entry)it.next();
/*      */       
/* 2958 */       HashWrapper key = (HashWrapper)entry.getKey();
/*      */       
/* 2960 */       DHTDBMapping mapping = (DHTDBMapping)entry.getValue();
/*      */       
/* 2962 */       Iterator<DHTDBValueImpl> it2 = mapping.getValues();
/*      */       
/* 2964 */       boolean all_remote = it2.hasNext();
/*      */       
/* 2966 */       while (it2.hasNext())
/*      */       {
/* 2968 */         DHTDBValueImpl value = (DHTDBValueImpl)it2.next();
/*      */         
/* 2970 */         if (value.isLocal())
/*      */         {
/* 2972 */           all_remote = false;
/*      */           
/* 2974 */           break;
/*      */         }
/*      */       }
/*      */       
/* 2978 */       if (all_remote)
/*      */       {
/* 2980 */         it.remove();
/*      */         
/* 2982 */         removeFromPrefixMap(mapping);
/*      */         
/* 2984 */         mapping.destroy();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setSleeping(boolean asleep)
/*      */   {
/*      */     try
/*      */     {
/* 2994 */       this.this_mon.enter();
/*      */       
/* 2996 */       this.sleeping = asleep;
/*      */       
/* 2998 */       if (asleep)
/*      */       {
/* 3000 */         sleep();
/*      */       }
/*      */     }
/*      */     finally {
/* 3004 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setSuspended(boolean susp)
/*      */   {
/*      */     boolean waking_up;
/*      */     
/*      */     try
/*      */     {
/* 3015 */       this.this_mon.enter();
/*      */       
/* 3017 */       waking_up = (this.suspended) && (!susp);
/*      */       
/* 3019 */       this.suspended = susp;
/*      */       
/* 3021 */       if (susp)
/*      */       {
/* 3023 */         sleep();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3028 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 3031 */     if (waking_up)
/*      */     {
/* 3033 */       new AEThread2("DHTB:resume")
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 3041 */             Thread.sleep(15000L);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/*      */ 
/* 3046 */           DHTDBImpl.this.logger.log("Force republish of original mappings due to resume from suspend");
/*      */           
/* 3048 */           long start = SystemTime.getMonotonousTime();
/*      */           
/* 3050 */           int stats = DHTDBImpl.this.republishOriginalMappings();
/*      */           
/* 3052 */           long end = SystemTime.getMonotonousTime();
/*      */           
/* 3054 */           DHTDBImpl.this.logger.log("Force republish of original mappings due to resume from suspend completed in " + (end - start) + ": " + "values = " + stats);
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
/*      */   public DHTTransportQueryStoreReply queryStore(DHTTransportContact originating_contact, int header_len, List<Object[]> keys)
/*      */   {
/* 3067 */     final List<byte[]> reply = new ArrayList();
/*      */     try
/*      */     {
/* 3070 */       this.this_mon.enter();
/*      */       
/* 3072 */       SurveyContactState existing_state = (SurveyContactState)this.survey_state.get(new HashWrapper(originating_contact.getID()));
/*      */       
/* 3074 */       if (existing_state != null)
/*      */       {
/* 3076 */         existing_state.updateContactDetails(originating_contact);
/*      */       }
/*      */       
/* 3079 */       for (Object[] entry : keys)
/*      */       {
/* 3081 */         byte[] prefix = (byte[])entry[0];
/* 3082 */         List<byte[]> suffixes = (List)entry[1];
/*      */         
/* 3084 */         header = new byte[header_len];
/*      */         
/* 3086 */         prefix_len = prefix.length;
/* 3087 */         suffix_len = header_len - prefix_len;
/*      */         
/* 3089 */         System.arraycopy(prefix, 0, header, 0, prefix_len);
/*      */         
/* 3091 */         for (byte[] suffix : suffixes)
/*      */         {
/* 3093 */           System.arraycopy(suffix, 0, header, prefix_len, suffix_len);
/*      */           
/* 3095 */           DHTDBMapping mapping = (DHTDBMapping)this.stored_values_prefix_map.get(new DHTDBMapping.ShortHash(header));
/*      */           
/* 3097 */           if (mapping == null)
/*      */           {
/* 3099 */             reply.add(null);
/*      */           }
/*      */           else
/*      */           {
/* 3103 */             if (existing_state != null)
/*      */             {
/* 3105 */               existing_state.addMapping(mapping);
/*      */             }
/*      */             
/* 3108 */             byte[] k = mapping.getKey().getBytes();
/*      */             
/* 3110 */             byte[] r = new byte[2];
/*      */             
/* 3112 */             System.arraycopy(k, k.length - 2, r, 0, 2);
/*      */             
/* 3114 */             reply.add(r);
/*      */           } } }
/*      */       byte[] header;
/*      */       int prefix_len;
/*      */       int suffix_len;
/* 3119 */       new DHTTransportQueryStoreReply()
/*      */       {
/*      */ 
/*      */         public int getHeaderSize()
/*      */         {
/*      */ 
/* 3125 */           return 2;
/*      */         }
/*      */         
/*      */ 
/*      */         public List<byte[]> getEntries()
/*      */         {
/* 3131 */           return reply;
/*      */         }
/*      */       };
/*      */     }
/*      */     finally
/*      */     {
/* 3137 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void print(boolean full)
/*      */   {
/* 3145 */     Map<Integer, Object[]> count = new TreeMap();
/*      */     try
/*      */     {
/* 3148 */       this.this_mon.enter();
/*      */       
/* 3150 */       this.logger.log("Stored keys = " + this.stored_values.size() + ", values = " + getValueDetails()[0]);
/*      */       
/* 3152 */       if (!full) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 3157 */       Iterator<Map.Entry<HashWrapper, DHTDBMapping>> it1 = this.stored_values.entrySet().iterator();
/*      */       
/*      */ 
/*      */ 
/* 3161 */       while (it1.hasNext())
/*      */       {
/* 3163 */         Map.Entry<HashWrapper, DHTDBMapping> entry = (Map.Entry)it1.next();
/*      */         
/* 3165 */         HashWrapper value_key = (HashWrapper)entry.getKey();
/*      */         
/* 3167 */         DHTDBMapping mapping = (DHTDBMapping)entry.getValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3175 */         DHTDBValue[] values = mapping.get(null, 0, (short)0);
/*      */         
/* 3177 */         for (int i = 0; i < values.length; i++)
/*      */         {
/* 3179 */           DHTDBValue value = values[i];
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3193 */           Integer key = new Integer(value.isLocal() ? 0 : 1);
/*      */           
/* 3195 */           Object[] data = (Object[])count.get(key);
/*      */           
/* 3197 */           if (data == null)
/*      */           {
/* 3199 */             data = new Object[2];
/*      */             
/* 3201 */             data[0] = new Integer(1);
/*      */             
/* 3203 */             data[1] = "";
/*      */             
/* 3205 */             count.put(key, data);
/*      */           }
/*      */           else
/*      */           {
/* 3209 */             data[0] = new Integer(((Integer)data[0]).intValue() + 1);
/*      */           }
/*      */           
/* 3212 */           String s = (String)data[1];
/*      */           
/* 3214 */           s = s + (s.length() == 0 ? "" : ", ") + "key=" + DHTLog.getString2(value_key.getHash()) + ",val=" + value.getString();
/*      */           
/* 3216 */           data[1] = s;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3238 */       Iterator<Integer> it2 = count.keySet().iterator();
/*      */       
/* 3240 */       while (it2.hasNext())
/*      */       {
/* 3242 */         Integer k = (Integer)it2.next();
/*      */         
/* 3244 */         Object[] data = (Object[])count.get(k);
/*      */         
/* 3246 */         this.logger.log("    " + k + " -> " + data[0] + " entries");
/*      */       }
/*      */       
/* 3249 */       Iterator<Map.Entry<HashWrapper, DHTDBMapping>> it3 = this.stored_values.entrySet().iterator();
/*      */       
/* 3251 */       StringBuilder sb = new StringBuilder(1024);
/*      */       
/* 3253 */       int str_entries = 0;
/*      */       
/* 3255 */       while (it3.hasNext())
/*      */       {
/* 3257 */         Map.Entry<HashWrapper, DHTDBMapping> entry = (Map.Entry)it3.next();
/*      */         
/* 3259 */         HashWrapper value_key = (HashWrapper)entry.getKey();
/*      */         
/* 3261 */         DHTDBMapping mapping = (DHTDBMapping)entry.getValue();
/*      */         
/* 3263 */         if (str_entries == 16)
/*      */         {
/* 3265 */           this.logger.log(sb.toString());
/*      */           
/* 3267 */           sb = new StringBuilder(1024);
/*      */           
/* 3269 */           sb.append("    ");
/*      */           
/* 3271 */           str_entries = 0;
/*      */         }
/*      */         
/* 3274 */         str_entries++;
/*      */         
/* 3276 */         if (str_entries > 1) {
/* 3277 */           sb.append(", ");
/*      */         }
/* 3279 */         sb.append(DHTLog.getString2(value_key.getHash()));
/* 3280 */         sb.append(" -> ");
/* 3281 */         sb.append(mapping.getValueCount());
/* 3282 */         sb.append("/");
/* 3283 */         sb.append(mapping.getHits());
/* 3284 */         sb.append("[");
/* 3285 */         sb.append(mapping.getLocalSize());
/* 3286 */         sb.append(",");
/* 3287 */         sb.append(mapping.getDirectSize());
/* 3288 */         sb.append(",");
/* 3289 */         sb.append(mapping.getIndirectSize());
/* 3290 */         sb.append("]");
/*      */       }
/*      */       
/* 3293 */       if (str_entries > 0)
/*      */       {
/* 3295 */         this.logger.log(sb.toString());
/*      */       }
/*      */     }
/*      */     finally {
/* 3299 */       this.this_mon.exit();
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
/*      */   protected void banContact(final DHTTransportContact contact, String reason)
/*      */   {
/* 3314 */     final boolean ban_ip = (this.control.getTransport().getNetwork() != 1) && (!this.control.getTransport().isIPV6());
/*      */     
/*      */ 
/*      */ 
/* 3318 */     new AEThread2("DHTDBImpl:delayed flood delete", true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 3327 */           DHTDBImpl.this.this_mon.enter();
/*      */           
/* 3329 */           Iterator<DHTDBMapping> it = DHTDBImpl.this.stored_values.values().iterator();
/*      */           
/* 3331 */           boolean overall_deleted = false;
/*      */           
/* 3333 */           HashWrapper value_id = new HashWrapper(contact.getID());
/*      */           
/* 3335 */           while (it.hasNext())
/*      */           {
/* 3337 */             DHTDBMapping mapping = (DHTDBMapping)it.next();
/*      */             
/* 3339 */             boolean deleted = false;
/*      */             
/* 3341 */             if (mapping.removeDirectValue(value_id) != null)
/*      */             {
/* 3343 */               deleted = true;
/*      */             }
/*      */             
/* 3346 */             if (mapping.removeIndirectValue(value_id) != null)
/*      */             {
/* 3348 */               deleted = true;
/*      */             }
/*      */             
/*      */ 
/* 3352 */             if ((deleted) && (!ban_ip))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 3357 */               mapping.rebuildIPBloomFilter(false);
/*      */               
/* 3359 */               overall_deleted = true;
/*      */             }
/*      */           }
/*      */           
/* 3363 */           if ((overall_deleted) && (!ban_ip))
/*      */           {
/* 3365 */             DHTDBImpl.this.rebuildIPBloomFilter(false);
/*      */           }
/*      */         }
/*      */         finally {
/* 3369 */           DHTDBImpl.this.this_mon.exit();
/*      */         }
/*      */       }
/*      */     }.start();
/*      */     
/*      */ 
/* 3375 */     if (ban_ip)
/*      */     {
/* 3377 */       this.logger.log("Banning " + contact.getString() + " due to store flooding (" + reason + ")");
/*      */       
/* 3379 */       this.ip_filter.ban(AddressUtils.getHostAddress(contact.getAddress()), "DHT: Sender stored excessive entries at this node (" + reason + ")", false);
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
/*      */ 
/*      */   protected void incrementValueAdds(DHTTransportContact contact)
/*      */   {
/* 3398 */     byte[] bloom_key = contact.getBloomKey();
/*      */     
/* 3400 */     int hit_count = this.ip_count_bloom_filter.add(bloom_key);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3409 */     if (this.ip_count_bloom_filter.getSize() / this.ip_count_bloom_filter.getEntryCount() < 10)
/*      */     {
/* 3411 */       rebuildIPBloomFilter(true);
/*      */     }
/*      */     
/* 3414 */     if (hit_count > 64)
/*      */     {
/*      */ 
/*      */ 
/* 3418 */       banContact(contact, "global flood");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decrementValueAdds(DHTTransportContact contact)
/*      */   {
/* 3426 */     byte[] bloom_key = contact.getBloomKey();
/*      */     
/* 3428 */     int hit_count = this.ip_count_bloom_filter.remove(bloom_key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void rebuildIPBloomFilter(boolean increase_size)
/*      */   {
/*      */     BloomFilter new_filter;
/*      */     
/*      */ 
/*      */     BloomFilter new_filter;
/*      */     
/*      */ 
/* 3442 */     if (increase_size)
/*      */     {
/* 3444 */       new_filter = BloomFilterFactory.createAddRemove8Bit(this.ip_count_bloom_filter.getSize() + 1000);
/*      */     }
/*      */     else
/*      */     {
/* 3448 */       new_filter = BloomFilterFactory.createAddRemove8Bit(this.ip_count_bloom_filter.getSize());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 3457 */       Iterator<DHTDBMapping> it = this.stored_values.values().iterator();
/*      */       
/* 3459 */       int max_hits = 0;
/*      */       
/* 3461 */       while (it.hasNext())
/*      */       {
/* 3463 */         DHTDBMapping mapping = (DHTDBMapping)it.next();
/*      */         
/* 3465 */         mapping.rebuildIPBloomFilter(false);
/*      */         
/* 3467 */         Iterator<DHTDBValueImpl> it2 = mapping.getDirectValues();
/*      */         
/* 3469 */         while (it2.hasNext())
/*      */         {
/* 3471 */           DHTDBValueImpl val = (DHTDBValueImpl)it2.next();
/*      */           
/* 3473 */           if (!val.isLocal())
/*      */           {
/*      */ 
/*      */ 
/* 3477 */             byte[] bloom_key = val.getOriginator().getBloomKey();
/*      */             
/* 3479 */             int hits = new_filter.add(bloom_key);
/*      */             
/* 3481 */             if (hits > max_hits)
/*      */             {
/* 3483 */               max_hits = hits;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3538 */       this.logger.log("Rebuilt global IP bloom filter, size=" + new_filter.getSize() + ", entries=" + new_filter.getEntryCount() + ", max hits=" + max_hits);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3553 */       this.ip_count_bloom_filter = new_filter;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void reportSizes(String op) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getNextValueVersion()
/*      */   {
/*      */     try
/*      */     {
/* 3633 */       this.this_mon.enter();
/*      */       
/* 3635 */       if (this.next_value_version_left == 0)
/*      */       {
/* 3637 */         this.next_value_version_left = 128;
/*      */         
/* 3639 */         if (this.adapter != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3645 */           this.next_value_version = this.adapter.getNextValueVersions(128);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3651 */       this.next_value_version_left -= 1;
/*      */       
/* 3653 */       int res = this.next_value_version++;
/*      */       
/*      */ 
/*      */ 
/* 3657 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 3661 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 3668 */     this.destroyed = true;
/*      */     
/* 3670 */     if (this.precious_timer != null)
/*      */     {
/* 3672 */       this.precious_timer.cancel();
/*      */     }
/* 3674 */     if (this.original_republish_timer != null)
/*      */     {
/* 3676 */       this.original_republish_timer.cancel();
/*      */     }
/* 3678 */     if (this.cache_republish_timer != null)
/*      */     {
/* 3680 */       this.cache_republish_timer.cancel();
/*      */     }
/* 3682 */     if (this.bloom_timer != null)
/*      */     {
/* 3684 */       this.bloom_timer.cancel();
/*      */     }
/* 3686 */     if (this.survey_timer != null)
/*      */     {
/* 3688 */       this.survey_timer.cancel();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected class adapterFacade
/*      */     implements DHTStorageAdapter
/*      */   {
/*      */     private final DHTStorageAdapter delegate;
/*      */     
/*      */ 
/*      */     protected adapterFacade(DHTStorageAdapter _delegate)
/*      */     {
/* 3702 */       this.delegate = _delegate;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getNetwork()
/*      */     {
/* 3708 */       return this.delegate.getNetwork();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DHTStorageKey keyCreated(HashWrapper key, boolean local)
/*      */     {
/* 3718 */       DHTDBImpl.this.reportSizes("keyAdded");
/*      */       
/* 3720 */       DHTDBImpl.access$408(DHTDBImpl.this);
/*      */       
/* 3722 */       return this.delegate.keyCreated(key, local);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void keyDeleted(DHTStorageKey adapter_key)
/*      */     {
/* 3729 */       DHTDBImpl.access$410(DHTDBImpl.this);
/*      */       
/* 3731 */       this.delegate.keyDeleted(adapter_key);
/*      */       
/* 3733 */       DHTDBImpl.this.reportSizes("keyDeleted");
/*      */     }
/*      */     
/*      */ 
/*      */     public int getKeyCount()
/*      */     {
/* 3739 */       return this.delegate.getKeyCount();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void keyRead(DHTStorageKey adapter_key, DHTTransportContact contact)
/*      */     {
/* 3747 */       DHTDBImpl.this.reportSizes("keyRead");
/*      */       
/* 3749 */       this.delegate.keyRead(adapter_key, contact);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public DHTStorageKeyStats deserialiseStats(DataInputStream is)
/*      */       throws IOException
/*      */     {
/* 3758 */       return this.delegate.deserialiseStats(is);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void valueAdded(DHTStorageKey key, DHTTransportValue value)
/*      */     {
/* 3766 */       DHTDBImpl.access$508(DHTDBImpl.this);
/* 3767 */       DHTDBImpl.access$612(DHTDBImpl.this, value.getValue().length);
/*      */       
/* 3769 */       DHTDBImpl.this.reportSizes("valueAdded");
/*      */       
/* 3771 */       if (!value.isLocal())
/*      */       {
/* 3773 */         DHTDBValueImpl val = (DHTDBValueImpl)value;
/*      */         
/* 3775 */         boolean direct = Arrays.equals(value.getOriginator().getID(), val.getSender().getID());
/*      */         
/* 3777 */         if (direct)
/*      */         {
/* 3779 */           DHTDBImpl.this.incrementValueAdds(value.getOriginator());
/*      */         }
/*      */       }
/*      */       
/* 3783 */       this.delegate.valueAdded(key, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void valueUpdated(DHTStorageKey key, DHTTransportValue old_value, DHTTransportValue new_value)
/*      */     {
/* 3792 */       DHTDBImpl.access$612(DHTDBImpl.this, new_value.getValue().length - old_value.getValue().length);
/*      */       
/* 3794 */       DHTDBImpl.this.reportSizes("valueUpdated");
/*      */       
/* 3796 */       this.delegate.valueUpdated(key, old_value, new_value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void valueDeleted(DHTStorageKey key, DHTTransportValue value)
/*      */     {
/* 3804 */       DHTDBImpl.access$510(DHTDBImpl.this);
/* 3805 */       DHTDBImpl.access$620(DHTDBImpl.this, value.getValue().length);
/*      */       
/* 3807 */       DHTDBImpl.this.reportSizes("valueDeleted");
/*      */       
/* 3809 */       if (!value.isLocal())
/*      */       {
/* 3811 */         DHTDBValueImpl val = (DHTDBValueImpl)value;
/*      */         
/* 3813 */         boolean direct = Arrays.equals(value.getOriginator().getID(), val.getSender().getID());
/*      */         
/* 3815 */         if (direct)
/*      */         {
/* 3817 */           DHTDBImpl.this.decrementValueAdds(value.getOriginator());
/*      */         }
/*      */       }
/*      */       
/* 3821 */       this.delegate.valueDeleted(key, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean isDiversified(byte[] key)
/*      */     {
/* 3830 */       return this.delegate.isDiversified(key);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public byte[][] getExistingDiversification(byte[] key, boolean put_operation, boolean exhaustive_get, int max_depth)
/*      */     {
/* 3840 */       return this.delegate.getExistingDiversification(key, put_operation, exhaustive_get, max_depth);
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
/*      */     public byte[][] createNewDiversification(String description, DHTTransportContact cause, byte[] key, boolean put_operation, byte diversification_type, boolean exhaustive_get, int max_depth)
/*      */     {
/* 3853 */       return this.delegate.createNewDiversification(description, cause, key, put_operation, diversification_type, exhaustive_get, max_depth);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int getNextValueVersions(int num)
/*      */     {
/* 3860 */       return this.delegate.getNextValueVersions(num);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DHTStorageBlock keyBlockRequest(DHTTransportContact direct_sender, byte[] request, byte[] signature)
/*      */     {
/* 3869 */       return this.delegate.keyBlockRequest(direct_sender, request, signature);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public DHTStorageBlock getKeyBlockDetails(byte[] key)
/*      */     {
/* 3876 */       return this.delegate.getKeyBlockDetails(key);
/*      */     }
/*      */     
/*      */ 
/*      */     public DHTStorageBlock[] getDirectKeyBlocks()
/*      */     {
/* 3882 */       return this.delegate.getDirectKeyBlocks();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getKeyForKeyBlock(byte[] request)
/*      */     {
/* 3889 */       return this.delegate.getKeyForKeyBlock(request);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setStorageForKey(String key, byte[] data)
/*      */     {
/* 3897 */       this.delegate.setStorageForKey(key, data);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getStorageForKey(String key)
/*      */     {
/* 3904 */       return this.delegate.getStorageForKey(key);
/*      */     }
/*      */     
/*      */ 
/*      */     public int getRemoteFreqDivCount()
/*      */     {
/* 3910 */       return this.delegate.getRemoteFreqDivCount();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getRemoteSizeDivCount()
/*      */     {
/* 3916 */       return this.delegate.getRemoteSizeDivCount();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class SurveyContactState
/*      */   {
/*      */     private DHTTransportContact contact;
/*      */     
/* 3925 */     private final long creation_time = SystemTime.getMonotonousTime();
/* 3926 */     private final long timeout = this.creation_time + 12600000L + RandomUtils.nextInt(3600000);
/*      */     
/* 3928 */     private long last_used = this.creation_time;
/*      */     
/* 3930 */     private final Set<DHTDBMapping> mappings = new HashSet();
/*      */     
/*      */ 
/*      */     private int consec_fails;
/*      */     
/*      */ 
/*      */     protected SurveyContactState(DHTTransportContact c)
/*      */     {
/* 3938 */       this.contact = c;
/*      */       
/* 3940 */       log("new");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean timeout(long now)
/*      */     {
/* 3947 */       return (now - this.last_used > 3600000L) || (now > this.timeout);
/*      */     }
/*      */     
/*      */ 
/*      */     protected DHTTransportContact getContact()
/*      */     {
/* 3953 */       return this.contact;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getCreationTime()
/*      */     {
/* 3959 */       return this.creation_time;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void updateContactDetails(DHTTransportContact c)
/*      */     {
/* 3966 */       if (c.getInstanceID() != this.contact.getInstanceID())
/*      */       {
/* 3968 */         log("instance id changed");
/*      */         
/* 3970 */         this.mappings.clear();
/*      */       }
/*      */       
/* 3973 */       this.contact = c;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void updateUseTime()
/*      */     {
/* 3979 */       this.last_used = SystemTime.getMonotonousTime();
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getLastUseTime()
/*      */     {
/* 3985 */       return this.last_used;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void contactOK()
/*      */     {
/* 3991 */       log("contact ok");
/*      */       
/* 3993 */       this.consec_fails = 0;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void contactFailed()
/*      */     {
/* 3999 */       this.consec_fails += 1;
/*      */       
/* 4001 */       log("failed, consec=" + this.consec_fails);
/*      */       
/* 4003 */       if (this.consec_fails >= 2)
/*      */       {
/* 4005 */         this.mappings.clear();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getConsecFails()
/*      */     {
/* 4012 */       return this.consec_fails;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean testMapping(DHTDBMapping mapping)
/*      */     {
/* 4019 */       return this.mappings.contains(mapping);
/*      */     }
/*      */     
/*      */ 
/*      */     protected Set<DHTDBMapping> getMappings()
/*      */     {
/* 4025 */       return this.mappings;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void addMapping(DHTDBMapping mapping)
/*      */     {
/* 4032 */       if (this.mappings.add(mapping))
/*      */       {
/* 4034 */         log("add mapping");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void removeMapping(DHTDBMapping mapping)
/*      */     {
/* 4042 */       if (this.mappings.remove(mapping))
/*      */       {
/* 4044 */         log("remove mapping");
/*      */       }
/*      */     }
/*      */     
/*      */     protected void log(String str) {}
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/db/impl/DHTDBImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */