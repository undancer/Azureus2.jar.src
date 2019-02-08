/*      */ package com.aelitis.azureus.core.dht.db.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHTStorageAdapter;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageKey;
/*      */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTDBMapping
/*      */ {
/*      */   private static final boolean TRACE_ADDS = false;
/*      */   private final DHTDBImpl db;
/*      */   private final HashWrapper key;
/*      */   private final ShortHash short_key;
/*      */   private DHTStorageKey adapter_key;
/*      */   private Map<HashWrapper, DHTDBValueImpl> direct_originator_map_may_be_null;
/*   56 */   final Map<HashWrapper, DHTDBValueImpl> indirect_originator_value_map = createLinkedMap();
/*      */   
/*      */   private int hits;
/*      */   
/*      */   private int direct_data_size;
/*      */   
/*      */   private int indirect_data_size;
/*      */   private int local_size;
/*   64 */   private byte diversification_state = 1;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int IP_COUNT_BLOOM_SIZE_INCREASE_CHUNK = 50;
/*      */   
/*      */ 
/*      */ 
/*      */   private Object ip_count_bloom_filter;
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTDBMapping(DHTDBImpl _db, HashWrapper _key, boolean _local)
/*      */   {
/*   78 */     this.db = _db;
/*   79 */     this.key = _key;
/*      */     
/*   81 */     this.short_key = new ShortHash(this.key.getBytes());
/*      */     try
/*      */     {
/*   84 */       if (this.db.getAdapter() != null)
/*      */       {
/*   86 */         this.adapter_key = this.db.getAdapter().keyCreated(this.key, _local);
/*      */         
/*   88 */         if (this.adapter_key != null)
/*      */         {
/*   90 */           this.diversification_state = this.adapter_key.getDiversificationType();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*   95 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map<HashWrapper, DHTDBValueImpl> createLinkedMap()
/*      */   {
/*  102 */     return new LinkedHashMap(1, 0.75F, true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected HashWrapper getKey()
/*      */   {
/*  108 */     return this.key;
/*      */   }
/*      */   
/*      */ 
/*      */   protected ShortHash getShortKey()
/*      */   {
/*  114 */     return this.short_key;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateLocalContact(DHTTransportContact contact)
/*      */   {
/*  124 */     if (this.direct_originator_map_may_be_null == null)
/*      */     {
/*  126 */       return;
/*      */     }
/*      */     
/*  129 */     List<DHTDBValueImpl> changed = new ArrayList();
/*      */     
/*  131 */     Iterator<DHTDBValueImpl> it = this.direct_originator_map_may_be_null.values().iterator();
/*      */     
/*  133 */     while (it.hasNext())
/*      */     {
/*  135 */       DHTDBValueImpl value = (DHTDBValueImpl)it.next();
/*      */       
/*  137 */       if (value.isLocal())
/*      */       {
/*  139 */         value.setOriginatorAndSender(contact);
/*      */         
/*  141 */         changed.add(value);
/*      */         
/*  143 */         this.direct_data_size -= value.getValue().length;
/*      */         
/*  145 */         this.local_size -= value.getValue().length;
/*      */         
/*  147 */         it.remove();
/*      */         
/*  149 */         informDeleted(value);
/*      */       }
/*      */     }
/*      */     
/*  153 */     for (int i = 0; i < changed.size(); i++)
/*      */     {
/*  155 */       add((DHTDBValueImpl)changed.get(i));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void add(DHTDBValueImpl new_value)
/*      */   {
/*  211 */     DHTTransportContact originator = new_value.getOriginator();
/*  212 */     DHTTransportContact sender = new_value.getSender();
/*      */     
/*  214 */     HashWrapper originator_id = new HashWrapper(originator.getID());
/*      */     
/*  216 */     boolean direct = Arrays.equals(originator.getID(), sender.getID());
/*      */     
/*  218 */     if (direct)
/*      */     {
/*      */ 
/*      */ 
/*  222 */       addDirectValue(originator_id, new_value);
/*      */       
/*      */ 
/*      */ 
/*  226 */       Iterator<Map.Entry<HashWrapper, DHTDBValueImpl>> it = this.indirect_originator_value_map.entrySet().iterator();
/*      */       
/*  228 */       List<HashWrapper> to_remove = new ArrayList();
/*      */       
/*  230 */       while (it.hasNext())
/*      */       {
/*  232 */         Map.Entry<HashWrapper, DHTDBValueImpl> entry = (Map.Entry)it.next();
/*      */         
/*  234 */         HashWrapper existing_key = (HashWrapper)entry.getKey();
/*      */         
/*  236 */         DHTDBValueImpl existing_value = (DHTDBValueImpl)entry.getValue();
/*      */         
/*  238 */         if (Arrays.equals(existing_value.getOriginator().getID(), originator.getID()))
/*      */         {
/*  240 */           to_remove.add(existing_key);
/*      */         }
/*      */       }
/*      */       
/*  244 */       for (int i = 0; i < to_remove.size(); i++)
/*      */       {
/*  246 */         removeIndirectValue((HashWrapper)to_remove.get(i));
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  253 */       if ((this.direct_originator_map_may_be_null != null) && (this.direct_originator_map_may_be_null.get(originator_id) != null))
/*      */       {
/*      */ 
/*  256 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  261 */       HashWrapper originator_value_id = getOriginatorValueID(new_value);
/*      */       
/*  263 */       DHTDBValueImpl existing_value = (DHTDBValueImpl)this.indirect_originator_value_map.get(originator_value_id);
/*      */       
/*  265 */       if (existing_value != null)
/*      */       {
/*  267 */         addIndirectValue(originator_value_id, new_value);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  275 */       else if (this.diversification_state == 1)
/*      */       {
/*  277 */         addIndirectValue(originator_value_id, new_value);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private HashWrapper getOriginatorValueID(DHTDBValueImpl value)
/*      */   {
/*  287 */     DHTTransportContact originator = value.getOriginator();
/*      */     
/*  289 */     byte[] originator_id = originator.getID();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  297 */     return new HashWrapper(originator_id);
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
/*      */   protected void addHit()
/*      */   {
/*  316 */     this.hits += 1;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getHits()
/*      */   {
/*  322 */     return this.hits;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getIndirectSize()
/*      */   {
/*  328 */     return this.indirect_data_size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getDirectSize()
/*      */   {
/*  336 */     return this.direct_data_size - this.local_size;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getLocalSize()
/*      */   {
/*  342 */     return this.local_size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DHTDBValueImpl[] get(DHTTransportContact by_who, int max, short flags)
/*      */   {
/*  351 */     if ((flags & 0x8) != 0)
/*      */     {
/*  353 */       if (this.adapter_key != null) {
/*      */         try
/*      */         {
/*  356 */           ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
/*      */           
/*  358 */           DataOutputStream dos = new DataOutputStream(baos);
/*      */           
/*  360 */           this.adapter_key.serialiseStats(dos);
/*      */           
/*  362 */           dos.close();
/*      */           
/*  364 */           return new DHTDBValueImpl[] { new DHTDBValueImpl(SystemTime.getCurrentTime(), baos.toByteArray(), 0, this.db.getLocalContact(), this.db.getLocalContact(), true, 8, 0, -1) };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  379 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*  383 */       return new DHTDBValueImpl[0];
/*      */     }
/*      */     
/*  386 */     List<DHTDBValueImpl> res = new ArrayList();
/*      */     
/*  388 */     Set<HashWrapper> duplicate_check = new HashSet();
/*      */     
/*  390 */     Map<HashWrapper, DHTDBValueImpl>[] maps = { this.direct_originator_map_may_be_null, this.indirect_originator_value_map };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  395 */     for (int i = 0; i < maps.length; i++)
/*      */     {
/*  397 */       Map<HashWrapper, DHTDBValueImpl> map = maps[i];
/*      */       
/*  399 */       if (map != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  404 */         List<HashWrapper> keys_used = new ArrayList();
/*      */         
/*  406 */         Iterator<Map.Entry<HashWrapper, DHTDBValueImpl>> it = map.entrySet().iterator();
/*      */         
/*  408 */         while ((it.hasNext()) && ((max == 0) || (res.size() < max)))
/*      */         {
/*  410 */           Map.Entry<HashWrapper, DHTDBValueImpl> entry = (Map.Entry)it.next();
/*      */           
/*  412 */           HashWrapper entry_key = (HashWrapper)entry.getKey();
/*      */           
/*  414 */           DHTDBValueImpl entry_value = (DHTDBValueImpl)entry.getValue();
/*      */           
/*  416 */           HashWrapper x = new HashWrapper(entry_value.getValue());
/*      */           
/*  418 */           if (!duplicate_check.contains(x))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  423 */             duplicate_check.add(x);
/*      */             
/*      */ 
/*      */ 
/*  427 */             if (entry_value.getValue().length > 0)
/*      */             {
/*  429 */               res.add(entry_value);
/*      */               
/*  431 */               keys_used.add(entry_key);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  437 */         for (int j = 0; j < keys_used.size(); j++)
/*      */         {
/*  439 */           map.get(keys_used.get(j));
/*      */         }
/*      */       }
/*      */     }
/*  443 */     informRead(by_who);
/*      */     
/*  445 */     DHTDBValueImpl[] v = new DHTDBValueImpl[res.size()];
/*      */     
/*  447 */     res.toArray(v);
/*      */     
/*  449 */     return v;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DHTDBValueImpl get(DHTTransportContact originator)
/*      */   {
/*  458 */     if (this.direct_originator_map_may_be_null == null)
/*      */     {
/*  460 */       return null;
/*      */     }
/*      */     
/*  463 */     HashWrapper originator_id = new HashWrapper(originator.getID());
/*      */     
/*  465 */     DHTDBValueImpl res = (DHTDBValueImpl)this.direct_originator_map_may_be_null.get(originator_id);
/*      */     
/*  467 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTDBValueImpl getAnyValue(DHTTransportContact originator)
/*      */   {
/*  474 */     DHTDBValueImpl res = null;
/*      */     try
/*      */     {
/*  477 */       Map<HashWrapper, DHTDBValueImpl> map = this.direct_originator_map_may_be_null;
/*      */       
/*  479 */       if (map != null)
/*      */       {
/*  481 */         HashWrapper originator_id = new HashWrapper(originator.getID());
/*      */         
/*  483 */         res = (DHTDBValueImpl)map.get(originator_id);
/*      */       }
/*      */       
/*  486 */       if (res == null)
/*      */       {
/*  488 */         Iterator<DHTDBValueImpl> it = this.indirect_originator_value_map.values().iterator();
/*      */         
/*  490 */         if (it.hasNext())
/*      */         {
/*  492 */           res = (DHTDBValueImpl)it.next();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*  499 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected List<DHTDBValueImpl> getAllValues(DHTTransportContact originator)
/*      */   {
/*  506 */     List<DHTDBValueImpl> res = new ArrayList();
/*      */     
/*  508 */     Set<HashWrapper> duplicate_check = new HashSet();
/*      */     
/*  510 */     Map<HashWrapper, DHTDBValueImpl>[] maps = { this.direct_originator_map_may_be_null, this.indirect_originator_value_map };
/*      */     
/*  512 */     for (int i = 0; i < maps.length; i++)
/*      */     {
/*  514 */       Map<HashWrapper, DHTDBValueImpl> map = maps[i];
/*      */       
/*  516 */       if (map != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  521 */         Iterator<Map.Entry<HashWrapper, DHTDBValueImpl>> it = map.entrySet().iterator();
/*      */         
/*  523 */         while (it.hasNext())
/*      */         {
/*  525 */           Map.Entry<HashWrapper, DHTDBValueImpl> entry = (Map.Entry)it.next();
/*      */           
/*  527 */           DHTDBValueImpl entry_value = (DHTDBValueImpl)entry.getValue();
/*      */           
/*  529 */           HashWrapper x = new HashWrapper(entry_value.getValue());
/*      */           
/*  531 */           if (!duplicate_check.contains(x))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  536 */             duplicate_check.add(x);
/*      */             
/*      */ 
/*      */ 
/*  540 */             if (entry_value.getValue().length > 0)
/*      */             {
/*  542 */               res.add(entry_value); }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  547 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DHTDBValueImpl remove(DHTTransportContact originator)
/*      */   {
/*  556 */     HashWrapper originator_id = new HashWrapper(originator.getID());
/*      */     
/*  558 */     DHTDBValueImpl res = removeDirectValue(originator_id);
/*      */     
/*  560 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int getValueCount()
/*      */   {
/*  567 */     if (this.direct_originator_map_may_be_null == null)
/*      */     {
/*  569 */       return this.indirect_originator_value_map.size();
/*      */     }
/*      */     
/*  572 */     return this.direct_originator_map_may_be_null.size() + this.indirect_originator_value_map.size();
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getDirectValueCount()
/*      */   {
/*  578 */     if (this.direct_originator_map_may_be_null == null)
/*      */     {
/*  580 */       return 0;
/*      */     }
/*      */     
/*  583 */     return this.direct_originator_map_may_be_null.size();
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getIndirectValueCount()
/*      */   {
/*  589 */     return this.indirect_originator_value_map.size();
/*      */   }
/*      */   
/*      */ 
/*      */   protected Iterator<DHTDBValueImpl> getValues()
/*      */   {
/*  595 */     return new valueIterator(true, true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected Iterator<DHTDBValueImpl> getDirectValues()
/*      */   {
/*  601 */     return new valueIterator(true, false);
/*      */   }
/*      */   
/*      */ 
/*      */   protected Iterator<DHTDBValueImpl> getIndirectValues()
/*      */   {
/*  607 */     return new valueIterator(false, true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected byte getDiversificationType()
/*      */   {
/*  613 */     return this.diversification_state;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDirectValue(HashWrapper value_key, DHTDBValueImpl value)
/*      */   {
/*  621 */     if (this.direct_originator_map_may_be_null == null)
/*      */     {
/*  623 */       this.direct_originator_map_may_be_null = createLinkedMap();
/*      */     }
/*      */     
/*  626 */     DHTDBValueImpl old = (DHTDBValueImpl)this.direct_originator_map_may_be_null.put(value_key, value);
/*      */     
/*  628 */     if (old != null)
/*      */     {
/*  630 */       int old_version = old.getVersion();
/*  631 */       int new_version = value.getVersion();
/*      */       
/*  633 */       if ((old_version != -1) && (new_version != -1) && (old_version >= new_version))
/*      */       {
/*  635 */         if (old_version == new_version)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  641 */           old.reset();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  656 */         this.direct_originator_map_may_be_null.put(value_key, old);
/*      */         
/*  658 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  665 */       this.direct_data_size -= old.getValue().length;
/*      */       
/*  667 */       if (old.isLocal())
/*      */       {
/*  669 */         this.local_size -= old.getValue().length;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  678 */     this.direct_data_size += value.getValue().length;
/*      */     
/*  680 */     if (value.isLocal())
/*      */     {
/*  682 */       this.local_size += value.getValue().length;
/*      */     }
/*      */     
/*  685 */     if (old == null)
/*      */     {
/*  687 */       informAdded(value);
/*      */     }
/*      */     else
/*      */     {
/*  691 */       informUpdated(old, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTDBValueImpl removeDirectValue(HashWrapper value_key)
/*      */   {
/*  699 */     if (this.direct_originator_map_may_be_null == null)
/*      */     {
/*  701 */       return null;
/*      */     }
/*      */     
/*  704 */     DHTDBValueImpl old = (DHTDBValueImpl)this.direct_originator_map_may_be_null.remove(value_key);
/*      */     
/*  706 */     if (old != null)
/*      */     {
/*  708 */       this.direct_data_size -= old.getValue().length;
/*      */       
/*  710 */       if (old.isLocal())
/*      */       {
/*  712 */         this.local_size -= old.getValue().length;
/*      */       }
/*      */       
/*  715 */       informDeleted(old);
/*      */     }
/*      */     
/*  718 */     return old;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addIndirectValue(HashWrapper value_key, DHTDBValueImpl value)
/*      */   {
/*  726 */     DHTDBValueImpl old = (DHTDBValueImpl)this.indirect_originator_value_map.put(value_key, value);
/*      */     
/*  728 */     if (old != null)
/*      */     {
/*      */ 
/*      */ 
/*  732 */       int old_version = old.getVersion();
/*  733 */       int new_version = value.getVersion();
/*      */       
/*  735 */       if ((old_version != -1) && (new_version != -1) && (old_version >= new_version))
/*      */       {
/*  737 */         if (old_version == new_version)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  743 */           old.reset();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  755 */         this.indirect_originator_value_map.put(value_key, old);
/*      */         
/*  757 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  764 */       if ((old_version == -1) || (new_version == -1))
/*      */       {
/*  766 */         if (old.getCreationTime() > value.getCreationTime() + 30000L)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  774 */           this.indirect_originator_value_map.put(value_key, old);
/*      */           
/*  776 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  784 */       this.indirect_data_size -= old.getValue().length;
/*      */       
/*  786 */       if (old.isLocal())
/*      */       {
/*  788 */         this.local_size -= old.getValue().length;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  796 */     this.indirect_data_size += value.getValue().length;
/*      */     
/*  798 */     if (value.isLocal())
/*      */     {
/*  800 */       this.local_size += value.getValue().length;
/*      */     }
/*      */     
/*  803 */     if (old == null)
/*      */     {
/*  805 */       informAdded(value);
/*      */     }
/*      */     else
/*      */     {
/*  809 */       informUpdated(old, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTDBValueImpl removeIndirectValue(HashWrapper value_key)
/*      */   {
/*  817 */     DHTDBValueImpl old = (DHTDBValueImpl)this.indirect_originator_value_map.remove(value_key);
/*      */     
/*  819 */     if (old != null)
/*      */     {
/*  821 */       this.indirect_data_size -= old.getValue().length;
/*      */       
/*  823 */       if (old.isLocal())
/*      */       {
/*  825 */         this.local_size -= old.getValue().length;
/*      */       }
/*      */       
/*  828 */       informDeleted(old);
/*      */     }
/*      */     
/*  831 */     return old;
/*      */   }
/*      */   
/*      */   protected void destroy()
/*      */   {
/*      */     try
/*      */     {
/*  838 */       if (this.adapter_key != null)
/*      */       {
/*  840 */         Iterator<DHTDBValueImpl> it = getValues();
/*      */         
/*  842 */         while (it.hasNext())
/*      */         {
/*  844 */           it.next();
/*      */           
/*  846 */           it.remove();
/*      */         }
/*      */         
/*  849 */         this.db.getAdapter().keyDeleted(this.adapter_key);
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  854 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void informDeleted(DHTDBValueImpl value)
/*      */   {
/*  862 */     boolean direct = (!value.isLocal()) && (Arrays.equals(value.getOriginator().getID(), value.getSender().getID()));
/*      */     
/*      */ 
/*      */ 
/*  866 */     if (direct)
/*      */     {
/*  868 */       removeFromBloom(value);
/*      */     }
/*      */     try
/*      */     {
/*  872 */       if (this.adapter_key != null)
/*      */       {
/*  874 */         this.db.getAdapter().valueDeleted(this.adapter_key, value);
/*      */         
/*  876 */         this.diversification_state = this.adapter_key.getDiversificationType();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  880 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void informAdded(DHTDBValueImpl value)
/*      */   {
/*  888 */     boolean direct = (!value.isLocal()) && (Arrays.equals(value.getOriginator().getID(), value.getSender().getID()));
/*      */     
/*      */ 
/*      */ 
/*  892 */     if (direct)
/*      */     {
/*  894 */       addToBloom(value);
/*      */     }
/*      */     try
/*      */     {
/*  898 */       if (this.adapter_key != null)
/*      */       {
/*  900 */         this.db.getAdapter().valueAdded(this.adapter_key, value);
/*      */         
/*  902 */         this.diversification_state = this.adapter_key.getDiversificationType();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  906 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void informUpdated(DHTDBValueImpl old_value, DHTDBValueImpl new_value)
/*      */   {
/*  915 */     boolean old_direct = (!old_value.isLocal()) && (Arrays.equals(old_value.getOriginator().getID(), old_value.getSender().getID()));
/*      */     
/*      */ 
/*      */ 
/*  919 */     boolean new_direct = (!new_value.isLocal()) && (Arrays.equals(new_value.getOriginator().getID(), new_value.getSender().getID()));
/*      */     
/*      */ 
/*      */ 
/*  923 */     if ((new_direct) && (!old_direct))
/*      */     {
/*  925 */       addToBloom(new_value);
/*      */     }
/*      */     try
/*      */     {
/*  929 */       if (this.adapter_key != null)
/*      */       {
/*  931 */         this.db.getAdapter().valueUpdated(this.adapter_key, old_value, new_value);
/*      */         
/*  933 */         this.diversification_state = this.adapter_key.getDiversificationType();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  937 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void informRead(DHTTransportContact contact)
/*      */   {
/*      */     try
/*      */     {
/*  946 */       if ((this.adapter_key != null) && (contact != null))
/*      */       {
/*  948 */         this.db.getAdapter().keyRead(this.adapter_key, contact);
/*      */         
/*  950 */         this.diversification_state = this.adapter_key.getDiversificationType();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  954 */       Debug.printStackTrace(e);
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
/*      */   protected void addToBloom(DHTDBValueImpl value)
/*      */   {
/*  967 */     DHTTransportContact originator = value.getOriginator();
/*      */     
/*  969 */     byte[] bloom_key = originator.getBloomKey();
/*      */     
/*      */ 
/*      */ 
/*  973 */     if (this.ip_count_bloom_filter == null)
/*      */     {
/*  975 */       this.ip_count_bloom_filter = bloom_key; return;
/*      */     }
/*      */     
/*      */ 
/*      */     BloomFilter filter;
/*      */     
/*      */ 
/*  982 */     if ((this.ip_count_bloom_filter instanceof byte[]))
/*      */     {
/*  984 */       byte[] existing_address = (byte[])this.ip_count_bloom_filter;
/*      */       BloomFilter filter;
/*  986 */       this.ip_count_bloom_filter = (filter = BloomFilterFactory.createAddRemove4Bit(50));
/*      */       
/*  988 */       filter.add(existing_address);
/*      */     }
/*      */     else
/*      */     {
/*  992 */       filter = (BloomFilter)this.ip_count_bloom_filter;
/*      */     }
/*      */     
/*  995 */     int hit_count = filter.add(bloom_key);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1004 */     if (filter.getSize() / filter.getEntryCount() < 10)
/*      */     {
/* 1006 */       rebuildIPBloomFilter(true);
/*      */     }
/*      */     
/* 1009 */     if (hit_count >= 15)
/*      */     {
/* 1011 */       this.db.banContact(originator, "local flood on '" + DHTLog.getFullString(this.key.getBytes()) + "'");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeFromBloom(DHTDBValueImpl value)
/*      */   {
/* 1019 */     DHTTransportContact originator = value.getOriginator();
/*      */     
/* 1021 */     if (this.ip_count_bloom_filter == null)
/*      */     {
/* 1023 */       return;
/*      */     }
/*      */     
/* 1026 */     byte[] bloom_key = originator.getBloomKey();
/*      */     
/* 1028 */     if ((this.ip_count_bloom_filter instanceof byte[]))
/*      */     {
/* 1030 */       byte[] existing_address = (byte[])this.ip_count_bloom_filter;
/*      */       
/* 1032 */       if (Arrays.equals(bloom_key, existing_address))
/*      */       {
/* 1034 */         this.ip_count_bloom_filter = null;
/*      */       }
/*      */       
/* 1037 */       return;
/*      */     }
/*      */     
/* 1040 */     BloomFilter filter = (BloomFilter)this.ip_count_bloom_filter;
/*      */     
/* 1042 */     int hit_count = filter.remove(bloom_key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void rebuildIPBloomFilter(boolean increase_size)
/*      */   {
/*      */     int old_size;
/*      */     
/*      */ 
/*      */ 
/*      */     int old_size;
/*      */     
/*      */ 
/*      */ 
/* 1058 */     if ((this.ip_count_bloom_filter instanceof BloomFilter))
/*      */     {
/* 1060 */       old_size = ((BloomFilter)this.ip_count_bloom_filter).getSize();
/*      */     }
/*      */     else
/*      */     {
/* 1064 */       old_size = 50; }
/*      */     BloomFilter new_filter;
/*      */     BloomFilter new_filter;
/* 1067 */     if (increase_size)
/*      */     {
/* 1069 */       new_filter = BloomFilterFactory.createAddRemove4Bit(old_size + 50);
/*      */     }
/*      */     else
/*      */     {
/* 1073 */       new_filter = BloomFilterFactory.createAddRemove4Bit(old_size);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1080 */       Iterator<DHTDBValueImpl> it = getDirectValues();
/*      */       
/* 1082 */       int max_hits = 0;
/*      */       
/* 1084 */       while (it.hasNext())
/*      */       {
/* 1086 */         DHTDBValueImpl val = (DHTDBValueImpl)it.next();
/*      */         
/* 1088 */         if (!val.isLocal())
/*      */         {
/*      */ 
/*      */ 
/* 1092 */           int hits = new_filter.add(val.getOriginator().getBloomKey());
/*      */           
/* 1094 */           if (hits > max_hits)
/*      */           {
/* 1096 */             max_hits = hits;
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/* 1108 */       this.ip_count_bloom_filter = new_filter;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void print()
/*      */   {
/*      */     int entries;
/*      */     int entries;
/* 1117 */     if (this.ip_count_bloom_filter == null)
/*      */     {
/* 1119 */       entries = 0;
/*      */     } else { int entries;
/* 1121 */       if ((this.ip_count_bloom_filter instanceof byte[]))
/*      */       {
/* 1123 */         entries = 1;
/*      */       }
/*      */       else
/*      */       {
/* 1127 */         entries = ((BloomFilter)this.ip_count_bloom_filter).getEntryCount();
/*      */       }
/*      */     }
/* 1130 */     System.out.println(ByteFormatter.encodeString(this.key.getBytes()) + ": " + "dir=" + (this.direct_originator_map_may_be_null == null ? 0 : this.direct_originator_map_may_be_null.size()) + "," + "indir=" + this.indirect_originator_value_map.size() + "," + "bloom=" + entries);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1136 */     System.out.println("    indirect");
/*      */     
/* 1138 */     Iterator<DHTDBValueImpl> it = getIndirectValues();
/*      */     
/* 1140 */     while (it.hasNext())
/*      */     {
/* 1142 */       DHTDBValueImpl val = (DHTDBValueImpl)it.next();
/*      */       
/* 1144 */       System.out.println("        " + val.getOriginator().getString() + ": " + new String(val.getValue()));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class valueIterator
/*      */     implements Iterator<DHTDBValueImpl>
/*      */   {
/* 1152 */     private final List<Map<HashWrapper, DHTDBValueImpl>> maps = new ArrayList(2);
/*      */     
/* 1154 */     private int map_index = 0;
/*      */     
/*      */     private Map<HashWrapper, DHTDBValueImpl> map;
/*      */     
/*      */     private Iterator<DHTDBValueImpl> it;
/*      */     
/*      */     private DHTDBValueImpl value;
/*      */     
/*      */ 
/*      */     protected valueIterator(boolean direct, boolean indirect)
/*      */     {
/* 1165 */       if ((direct) && (DHTDBMapping.this.direct_originator_map_may_be_null != null)) {
/* 1166 */         this.maps.add(DHTDBMapping.this.direct_originator_map_may_be_null);
/*      */       }
/*      */       
/* 1169 */       if (indirect) {
/* 1170 */         this.maps.add(DHTDBMapping.this.indirect_originator_value_map);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean hasNext()
/*      */     {
/* 1177 */       if ((this.it != null) && (this.it.hasNext()))
/*      */       {
/* 1179 */         return true;
/*      */       }
/*      */       
/* 1182 */       while (this.map_index < this.maps.size())
/*      */       {
/* 1184 */         this.map = ((Map)this.maps.get(this.map_index++));
/*      */         
/* 1186 */         this.it = this.map.values().iterator();
/*      */         
/* 1188 */         if (this.it.hasNext())
/*      */         {
/* 1190 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 1194 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public DHTDBValueImpl next()
/*      */     {
/* 1200 */       if (hasNext())
/*      */       {
/* 1202 */         this.value = ((DHTDBValueImpl)this.it.next());
/*      */         
/* 1204 */         return this.value;
/*      */       }
/*      */       
/* 1207 */       throw new NoSuchElementException();
/*      */     }
/*      */     
/*      */ 
/*      */     public void remove()
/*      */     {
/* 1213 */       if (this.it == null)
/*      */       {
/* 1215 */         throw new IllegalStateException();
/*      */       }
/*      */       
/* 1218 */       if (this.value != null)
/*      */       {
/* 1220 */         if (this.value.isLocal())
/*      */         {
/* 1222 */           DHTDBMapping.access$120(DHTDBMapping.this, this.value.getValue().length);
/*      */         }
/*      */         
/* 1225 */         if (this.map == DHTDBMapping.this.indirect_originator_value_map)
/*      */         {
/* 1227 */           DHTDBMapping.access$220(DHTDBMapping.this, this.value.getValue().length);
/*      */         }
/*      */         else
/*      */         {
/* 1231 */           DHTDBMapping.access$320(DHTDBMapping.this, this.value.getValue().length);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1236 */         this.it.remove();
/*      */         
/* 1238 */         DHTDBMapping.this.informDeleted(this.value);
/*      */         
/* 1240 */         this.value = null;
/*      */       }
/*      */       else
/*      */       {
/* 1244 */         throw new IllegalStateException();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static class ShortHash
/*      */   {
/*      */     private final byte[] bytes;
/*      */     
/*      */     private final int hash_code;
/*      */     
/*      */ 
/*      */     protected ShortHash(byte[] _bytes)
/*      */     {
/* 1259 */       this.bytes = _bytes;
/*      */       
/* 1261 */       int hc = 0;
/*      */       
/* 1263 */       for (int i = 0; i < 6; i++)
/*      */       {
/* 1265 */         hc = 31 * hc + this.bytes[i];
/*      */       }
/*      */       
/* 1268 */       this.hash_code = hc;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public final boolean equals(Object o)
/*      */     {
/* 1275 */       if (!(o instanceof ShortHash))
/*      */       {
/* 1277 */         return false;
/*      */       }
/*      */       
/* 1280 */       ShortHash other = (ShortHash)o;
/*      */       
/* 1282 */       byte[] other_hash = other.bytes;
/*      */       
/* 1284 */       for (int i = 0; i < 6; i++)
/*      */       {
/* 1286 */         if (this.bytes[i] != other_hash[i])
/*      */         {
/* 1288 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 1292 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1298 */       return this.hash_code;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/db/impl/DHTDBMapping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */