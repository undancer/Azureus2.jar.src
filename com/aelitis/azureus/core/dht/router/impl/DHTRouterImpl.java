/*      */ package com.aelitis.azureus.core.dht.router.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouterAdapter;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouterContact;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouterContactAttachment;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouterObserver;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouterStats;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Debug;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTRouterImpl
/*      */   implements DHTRouter
/*      */ {
/*      */   private static final int SMALLEST_SUBTREE_MAX_EXCESS = 10240;
/*      */   private boolean is_bootstrap_proxy;
/*      */   private int K;
/*      */   private int B;
/*      */   private int max_rep_per_node;
/*      */   private DHTLogger logger;
/*      */   private int smallest_subtree_max;
/*      */   private DHTRouterAdapter adapter;
/*      */   private DHTRouterContactImpl local_contact;
/*      */   private byte[] router_node_id;
/*      */   private DHTRouterNodeImpl root;
/*      */   private DHTRouterNodeImpl smallest_subtree;
/*      */   private int consecutive_dead;
/*   84 */   private static long random_seed = ;
/*      */   
/*      */   private Random random;
/*   87 */   private List<DHTRouterContactImpl> outstanding_pings = new ArrayList();
/*   88 */   private List<DHTRouterContactImpl> outstanding_adds = new ArrayList();
/*      */   
/*   90 */   private final DHTRouterStatsImpl stats = new DHTRouterStatsImpl(this);
/*      */   
/*   92 */   private final AEMonitor this_mon = new AEMonitor("DHTRouter");
/*      */   
/*   94 */   private static final AEMonitor class_mon = new AEMonitor("DHTRouter:class");
/*      */   
/*   96 */   private final CopyOnWriteList<DHTRouterObserver> observers = new CopyOnWriteList();
/*      */   
/*      */   private boolean sleeping;
/*      */   
/*      */   private boolean suspended;
/*  101 */   private final BloomFilter recent_contact_bloom = BloomFilterFactory.createRotating(BloomFilterFactory.createAddOnly(10240), 2);
/*      */   
/*      */ 
/*      */ 
/*      */   private TimerEventPeriodic timer_event;
/*      */   
/*      */ 
/*      */ 
/*      */   private volatile int seed_in_ticks;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int TICK_PERIOD = 10000;
/*      */   
/*      */ 
/*      */   private static final int SEED_DELAY_PERIOD = 60000;
/*      */   
/*      */ 
/*      */   private static final int SEED_DELAY_TICKS = 6;
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTRouterImpl(int _K, int _B, int _max_rep_per_node, byte[] _router_node_id, DHTRouterContactAttachment _attachment, DHTLogger _logger)
/*      */   {
/*      */     try
/*      */     {
/*  127 */       class_mon.enter();
/*      */       
/*  129 */       this.random = new Random(random_seed++);
/*      */     }
/*      */     finally
/*      */     {
/*  133 */       class_mon.exit();
/*      */     }
/*      */     
/*  136 */     this.is_bootstrap_proxy = COConfigurationManager.getBooleanParameter("dht.bootstrap.is.proxy", false);
/*      */     
/*  138 */     this.K = _K;
/*  139 */     this.B = _B;
/*  140 */     this.max_rep_per_node = _max_rep_per_node;
/*  141 */     this.logger = _logger;
/*      */     
/*      */ 
/*  144 */     this.smallest_subtree_max = 1;
/*      */     
/*  146 */     for (int i = 0; i < this.B; i++)
/*      */     {
/*  148 */       this.smallest_subtree_max *= 2;
/*      */     }
/*      */     
/*  151 */     this.smallest_subtree_max += 10240;
/*      */     
/*  153 */     this.router_node_id = _router_node_id;
/*      */     
/*  155 */     Object buckets = new ArrayList();
/*      */     
/*  157 */     this.local_contact = new DHTRouterContactImpl(this.router_node_id, _attachment, true);
/*      */     
/*  159 */     ((List)buckets).add(this.local_contact);
/*      */     
/*  161 */     this.root = new DHTRouterNodeImpl(this, 0, true, (List)buckets);
/*      */     
/*  163 */     this.timer_event = SimpleTimer.addPeriodicEvent("DHTRouter:pinger", 10000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  172 */         if (DHTRouterImpl.this.suspended)
/*      */         {
/*  174 */           return;
/*      */         }
/*      */         
/*  177 */         DHTRouterImpl.this.pingeroonies();
/*      */         
/*  179 */         if (DHTRouterImpl.this.seed_in_ticks > 0)
/*      */         {
/*  181 */           DHTRouterImpl.access$110(DHTRouterImpl.this);
/*      */           
/*  183 */           if (DHTRouterImpl.this.seed_in_ticks == 0)
/*      */           {
/*  185 */             DHTRouterImpl.this.seedSupport();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void notifyAdded(DHTRouterContact contact) {
/*  193 */     for (Iterator<DHTRouterObserver> i = this.observers.iterator(); i.hasNext();) {
/*  194 */       DHTRouterObserver rto = (DHTRouterObserver)i.next();
/*      */       try {
/*  196 */         rto.added(contact);
/*      */       } catch (Throwable e) {
/*  198 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyRemoved(DHTRouterContact contact) {
/*  204 */     for (Iterator<DHTRouterObserver> i = this.observers.iterator(); i.hasNext();) {
/*  205 */       DHTRouterObserver rto = (DHTRouterObserver)i.next();
/*      */       try {
/*  207 */         rto.removed(contact);
/*      */       } catch (Throwable e) {
/*  209 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyLocationChanged(DHTRouterContact contact) {
/*  215 */     for (Iterator<DHTRouterObserver> i = this.observers.iterator(); i.hasNext();) {
/*  216 */       DHTRouterObserver rto = (DHTRouterObserver)i.next();
/*      */       try {
/*  218 */         rto.locationChanged(contact);
/*      */       } catch (Throwable e) {
/*  220 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyNowAlive(DHTRouterContact contact) {
/*  226 */     for (Iterator<DHTRouterObserver> i = this.observers.iterator(); i.hasNext();) {
/*  227 */       DHTRouterObserver rto = (DHTRouterObserver)i.next();
/*      */       try {
/*  229 */         rto.nowAlive(contact);
/*      */       } catch (Throwable e) {
/*  231 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyNowFailing(DHTRouterContact contact) {
/*  237 */     for (Iterator<DHTRouterObserver> i = this.observers.iterator(); i.hasNext();) {
/*  238 */       DHTRouterObserver rto = (DHTRouterObserver)i.next();
/*      */       try {
/*  240 */         rto.nowFailing(contact);
/*      */       } catch (Throwable e) {
/*  242 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyDead() {
/*  248 */     for (Iterator<DHTRouterObserver> i = this.observers.iterator(); i.hasNext();) {
/*  249 */       DHTRouterObserver rto = (DHTRouterObserver)i.next();
/*      */       try {
/*  251 */         rto.destroyed(this);
/*      */       } catch (Throwable e) {
/*  253 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean addObserver(DHTRouterObserver rto) {
/*  259 */     if ((rto != null) && (!this.observers.contains(rto))) {
/*  260 */       this.observers.add(rto);
/*  261 */       return true;
/*      */     }
/*  263 */     return false;
/*      */   }
/*      */   
/*      */   public boolean containsObserver(DHTRouterObserver rto) {
/*  267 */     return (rto != null) && (this.observers.contains(rto));
/*      */   }
/*      */   
/*      */   public boolean removeObserver(DHTRouterObserver rto) {
/*  271 */     return (rto != null) && (this.observers.remove(rto));
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTRouterStats getStats()
/*      */   {
/*  277 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getK()
/*      */   {
/*  283 */     return this.K;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] getID()
/*      */   {
/*  290 */     return this.router_node_id;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isID(byte[] id)
/*      */   {
/*  297 */     return Arrays.equals(id, this.router_node_id);
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTRouterContact getLocalContact()
/*      */   {
/*  303 */     return this.local_contact;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAdapter(DHTRouterAdapter _adapter)
/*      */   {
/*  310 */     this.adapter = _adapter;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSleeping(boolean _sleeping)
/*      */   {
/*  317 */     this.sleeping = _sleeping;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSuspended(boolean _suspended)
/*      */   {
/*  324 */     this.suspended = _suspended;
/*      */     
/*  326 */     if (!this.suspended)
/*      */     {
/*  328 */       this.seed_in_ticks = 1;
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
/*      */   public void contactKnown(byte[] node_id, DHTRouterContactAttachment attachment, boolean force)
/*      */   {
/*  341 */     if (SystemTime.getMonotonousTime() - this.recent_contact_bloom.getStartTimeMono() > 600000L)
/*      */     {
/*  343 */       this.recent_contact_bloom.clear();
/*      */     }
/*      */     
/*  346 */     if (this.recent_contact_bloom.contains(node_id))
/*      */     {
/*  348 */       if (!force)
/*      */       {
/*  350 */         return;
/*      */       }
/*      */     }
/*      */     
/*  354 */     this.recent_contact_bloom.add(node_id);
/*      */     
/*  356 */     addContact(node_id, attachment, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void contactAlive(byte[] node_id, DHTRouterContactAttachment attachment)
/*      */   {
/*  364 */     addContact(node_id, attachment, true);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public DHTRouterContact contactDead(byte[] node_id, boolean force)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 548	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:suspended	Z
/*      */     //   4: ifeq +5 -> 9
/*      */     //   7: aconst_null
/*      */     //   8: areturn
/*      */     //   9: aload_0
/*      */     //   10: getfield 549	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:router_node_id	[B
/*      */     //   13: aload_1
/*      */     //   14: invokestatic 627	java/util/Arrays:equals	([B[B)Z
/*      */     //   17: ifeq +13 -> 30
/*      */     //   20: ldc 7
/*      */     //   22: invokestatic 640	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */     //   25: aload_0
/*      */     //   26: getfield 552	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:local_contact	Lcom/aelitis/azureus/core/dht/router/impl/DHTRouterContactImpl;
/*      */     //   29: areturn
/*      */     //   30: aload_0
/*      */     //   31: getfield 562	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   34: invokevirtual 637	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   37: aload_0
/*      */     //   38: dup
/*      */     //   39: getfield 541	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:consecutive_dead	I
/*      */     //   42: iconst_1
/*      */     //   43: iadd
/*      */     //   44: putfield 541	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:consecutive_dead	I
/*      */     //   47: aload_0
/*      */     //   48: aload_1
/*      */     //   49: invokevirtual 585	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:findContactSupport	([B)[Ljava/lang/Object;
/*      */     //   52: astore_3
/*      */     //   53: aload_3
/*      */     //   54: iconst_0
/*      */     //   55: aaload
/*      */     //   56: checkcast 325	com/aelitis/azureus/core/dht/router/impl/DHTRouterNodeImpl
/*      */     //   59: astore 4
/*      */     //   61: aload_3
/*      */     //   62: iconst_1
/*      */     //   63: aaload
/*      */     //   64: checkcast 321	com/aelitis/azureus/core/dht/router/impl/DHTRouterContactImpl
/*      */     //   67: astore 5
/*      */     //   69: aload 5
/*      */     //   71: ifnull +25 -> 96
/*      */     //   74: aload_0
/*      */     //   75: getfield 541	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:consecutive_dead	I
/*      */     //   78: bipush 100
/*      */     //   80: if_icmplt +7 -> 87
/*      */     //   83: iload_2
/*      */     //   84: ifeq +12 -> 96
/*      */     //   87: aload_0
/*      */     //   88: aload 4
/*      */     //   90: aload 5
/*      */     //   92: iload_2
/*      */     //   93: invokespecial 589	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:contactDeadSupport	(Lcom/aelitis/azureus/core/dht/router/impl/DHTRouterNodeImpl;Lcom/aelitis/azureus/core/dht/router/impl/DHTRouterContactImpl;Z)V
/*      */     //   96: aload 5
/*      */     //   98: astore 6
/*      */     //   100: aload_0
/*      */     //   101: getfield 562	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   104: invokevirtual 638	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   107: aload_0
/*      */     //   108: invokevirtual 577	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:dispatchPings	()V
/*      */     //   111: aload_0
/*      */     //   112: invokevirtual 576	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:dispatchNodeAdds	()V
/*      */     //   115: aload 6
/*      */     //   117: areturn
/*      */     //   118: astore 7
/*      */     //   120: aload_0
/*      */     //   121: getfield 562	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   124: invokevirtual 638	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   127: aload 7
/*      */     //   129: athrow
/*      */     //   130: astore 8
/*      */     //   132: aload_0
/*      */     //   133: invokevirtual 577	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:dispatchPings	()V
/*      */     //   136: aload_0
/*      */     //   137: invokevirtual 576	com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl:dispatchNodeAdds	()V
/*      */     //   140: aload 8
/*      */     //   142: athrow
/*      */     // Line number table:
/*      */     //   Java source line #382	-> byte code offset #0
/*      */     //   Java source line #384	-> byte code offset #7
/*      */     //   Java source line #387	-> byte code offset #9
/*      */     //   Java source line #393	-> byte code offset #20
/*      */     //   Java source line #395	-> byte code offset #25
/*      */     //   Java source line #400	-> byte code offset #30
/*      */     //   Java source line #402	-> byte code offset #37
/*      */     //   Java source line #411	-> byte code offset #47
/*      */     //   Java source line #413	-> byte code offset #53
/*      */     //   Java source line #414	-> byte code offset #61
/*      */     //   Java source line #416	-> byte code offset #69
/*      */     //   Java source line #421	-> byte code offset #74
/*      */     //   Java source line #423	-> byte code offset #87
/*      */     //   Java source line #427	-> byte code offset #96
/*      */     //   Java source line #431	-> byte code offset #100
/*      */     //   Java source line #435	-> byte code offset #107
/*      */     //   Java source line #437	-> byte code offset #111
/*      */     //   Java source line #431	-> byte code offset #118
/*      */     //   Java source line #435	-> byte code offset #130
/*      */     //   Java source line #437	-> byte code offset #136
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	143	0	this	DHTRouterImpl
/*      */     //   0	143	1	node_id	byte[]
/*      */     //   0	143	2	force	boolean
/*      */     //   52	10	3	res	Object[]
/*      */     //   59	30	4	node	DHTRouterNodeImpl
/*      */     //   67	30	5	contact	DHTRouterContactImpl
/*      */     //   98	18	6	localDHTRouterContactImpl1	DHTRouterContactImpl
/*      */     //   118	10	7	localObject1	Object
/*      */     //   130	11	8	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   30	100	118	finally
/*      */     //   118	120	118	finally
/*      */     //   30	107	130	finally
/*      */     //   118	132	130	finally
/*      */   }
/*      */   
/*      */   private void contactDeadSupport(DHTRouterNodeImpl node, DHTRouterContactImpl contact, boolean force)
/*      */   {
/*  450 */     if (this.is_bootstrap_proxy)
/*      */     {
/*  452 */       List<DHTRouterContactImpl> replacements = node.getReplacements();
/*      */       
/*  454 */       if ((replacements == null) || (replacements.size() == 0))
/*      */       {
/*  456 */         return;
/*      */       }
/*      */     }
/*      */     
/*  460 */     node.dead(contact, force);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void contactRemoved(byte[] node_id) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addContact(byte[] node_id, DHTRouterContactAttachment attachment, boolean known_to_be_alive)
/*      */   {
/*  476 */     if (attachment.isSleeping())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  481 */       if (Arrays.equals(this.router_node_id, node_id))
/*      */       {
/*  483 */         return;
/*      */       }
/*      */       try
/*      */       {
/*  487 */         this.this_mon.enter();
/*      */         
/*  489 */         Object[] res = findContactSupport(node_id);
/*      */         
/*  491 */         DHTRouterNodeImpl node = (DHTRouterNodeImpl)res[0];
/*  492 */         DHTRouterContactImpl contact = (DHTRouterContactImpl)res[1];
/*      */         
/*  494 */         if (contact != null)
/*      */         {
/*  496 */           contactDeadSupport(node, contact, true);
/*      */         }
/*      */       }
/*      */       finally {
/*  500 */         this.this_mon.exit();
/*      */       }
/*      */       
/*  503 */       return;
/*      */     }
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/*  509 */         this.this_mon.enter();
/*      */         
/*  511 */         if (known_to_be_alive)
/*      */         {
/*  513 */           this.consecutive_dead = 0;
/*      */         }
/*      */         
/*  516 */         addContactSupport(node_id, attachment, known_to_be_alive);
/*      */       }
/*      */       finally
/*      */       {
/*  520 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */     finally {
/*  524 */       dispatchPings();
/*      */       
/*  526 */       dispatchNodeAdds();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DHTRouterContact addContactSupport(byte[] node_id, DHTRouterContactAttachment attachment, boolean known_to_be_alive)
/*      */   {
/*  536 */     if (Arrays.equals(this.router_node_id, node_id))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  541 */       return this.local_contact;
/*      */     }
/*      */     
/*  544 */     DHTRouterNodeImpl current_node = this.root;
/*      */     
/*  546 */     boolean part_of_smallest_subtree = false;
/*      */     
/*  548 */     for (int i = 0; i < node_id.length; i++)
/*      */     {
/*  550 */       byte b = node_id[i];
/*      */       
/*  552 */       int j = 7;
/*      */       
/*  554 */       while (j >= 0)
/*      */       {
/*  556 */         if (current_node == this.smallest_subtree)
/*      */         {
/*  558 */           part_of_smallest_subtree = true;
/*      */         }
/*      */         
/*  561 */         boolean bit = (b >> j & 0x1) == 1;
/*      */         
/*      */         DHTRouterNodeImpl next_node;
/*      */         DHTRouterNodeImpl next_node;
/*  565 */         if (bit)
/*      */         {
/*  567 */           next_node = current_node.getLeft();
/*      */         }
/*      */         else
/*      */         {
/*  571 */           next_node = current_node.getRight();
/*      */         }
/*      */         
/*  574 */         if (next_node == null)
/*      */         {
/*  576 */           DHTRouterContact existing_contact = current_node.updateExistingNode(node_id, attachment, known_to_be_alive);
/*      */           
/*  578 */           if (existing_contact != null)
/*      */           {
/*  580 */             return existing_contact;
/*      */           }
/*      */           
/*  583 */           List buckets = current_node.getBuckets();
/*      */           
/*  585 */           int buckets_size = buckets.size();
/*      */           
/*  587 */           if ((this.sleeping) && (buckets_size >= this.K / 4) && (!current_node.containsRouterNodeID()))
/*      */           {
/*      */ 
/*      */ 
/*  591 */             DHTRouterContactImpl new_contact = new DHTRouterContactImpl(node_id, attachment, known_to_be_alive);
/*      */             
/*  593 */             return current_node.addReplacement(new_contact, 1);
/*      */           }
/*  595 */           if (buckets_size == this.K)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  602 */             boolean contains_router_node_id = current_node.containsRouterNodeID();
/*  603 */             int depth = current_node.getDepth();
/*      */             
/*  605 */             boolean too_deep_to_split = depth % this.B == 0;
/*      */             
/*      */ 
/*  608 */             if ((contains_router_node_id) || (!too_deep_to_split) || (part_of_smallest_subtree))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  628 */               if ((part_of_smallest_subtree) && (too_deep_to_split) && (!contains_router_node_id) && (getContactCount(this.smallest_subtree) > this.smallest_subtree_max))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  633 */                 Debug.out("DHTRouter: smallest subtree max size violation");
/*      */                 
/*  635 */                 return null;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  640 */               List left_buckets = new ArrayList();
/*  641 */               List right_buckets = new ArrayList();
/*      */               
/*  643 */               for (int k = 0; k < buckets.size(); k++)
/*      */               {
/*  645 */                 DHTRouterContactImpl contact = (DHTRouterContactImpl)buckets.get(k);
/*      */                 
/*  647 */                 byte[] bucket_id = contact.getID();
/*      */                 
/*  649 */                 if ((bucket_id[(depth / 8)] >> 7 - depth % 8 & 0x1) == 0)
/*      */                 {
/*  651 */                   right_buckets.add(contact);
/*      */                 }
/*      */                 else
/*      */                 {
/*  655 */                   left_buckets.add(contact);
/*      */                 }
/*      */               }
/*      */               
/*  659 */               boolean right_contains_rid = false;
/*  660 */               boolean left_contains_rid = false;
/*      */               
/*  662 */               if (contains_router_node_id)
/*      */               {
/*  664 */                 right_contains_rid = (this.router_node_id[(depth / 8)] >> 7 - depth % 8 & 0x1) == 0;
/*      */                 
/*      */ 
/*  667 */                 left_contains_rid = !right_contains_rid;
/*      */               }
/*      */               
/*  670 */               DHTRouterNodeImpl new_left = new DHTRouterNodeImpl(this, depth + 1, left_contains_rid, left_buckets);
/*  671 */               DHTRouterNodeImpl new_right = new DHTRouterNodeImpl(this, depth + 1, right_contains_rid, right_buckets);
/*      */               
/*  673 */               current_node.split(new_left, new_right);
/*      */               
/*  675 */               if (right_contains_rid)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  680 */                 this.smallest_subtree = new_left;
/*      */               }
/*  682 */               else if (left_contains_rid)
/*      */               {
/*      */ 
/*      */ 
/*  686 */                 this.smallest_subtree = new_right;
/*      */ 
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  695 */               DHTRouterContactImpl new_contact = new DHTRouterContactImpl(node_id, attachment, known_to_be_alive);
/*      */               
/*  697 */               return current_node.addReplacement(new_contact, this.sleeping ? 1 : this.max_rep_per_node);
/*      */             }
/*      */             
/*      */           }
/*      */           else
/*      */           {
/*  703 */             DHTRouterContactImpl new_contact = new DHTRouterContactImpl(node_id, attachment, known_to_be_alive);
/*      */             
/*  705 */             current_node.addNode(new_contact);
/*      */             
/*  707 */             return new_contact;
/*      */           }
/*      */         }
/*      */         else {
/*  711 */           current_node = next_node;
/*      */           
/*  713 */           j--;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  718 */     Debug.out("DHTRouter inconsistency");
/*      */     
/*  720 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List findClosestContacts(byte[] node_id, int num_to_return, boolean live_only)
/*      */   {
/*      */     try
/*      */     {
/*  732 */       this.this_mon.enter();
/*      */       
/*  734 */       List res = new ArrayList();
/*      */       
/*  736 */       findClosestContacts(node_id, num_to_return, 0, this.root, live_only, res);
/*      */       
/*  738 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/*  742 */       this.this_mon.exit();
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
/*      */   protected void findClosestContacts(byte[] node_id, int num_to_return, int depth, DHTRouterNodeImpl current_node, boolean live_only, List res)
/*      */   {
/*  755 */     List buckets = current_node.getBuckets();
/*      */     
/*  757 */     if (buckets != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  762 */       for (int i = 0; i < buckets.size(); i++)
/*      */       {
/*  764 */         DHTRouterContactImpl contact = (DHTRouterContactImpl)buckets.get(i);
/*      */         
/*      */ 
/*      */ 
/*  768 */         if ((!live_only) || (!contact.isFailing()))
/*      */         {
/*  770 */           res.add(contact);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  775 */       boolean bit = (node_id[(depth / 8)] >> 7 - depth % 8 & 0x1) == 1;
/*      */       
/*      */       DHTRouterNodeImpl worse_node;
/*      */       DHTRouterNodeImpl best_node;
/*      */       DHTRouterNodeImpl worse_node;
/*  780 */       if (bit)
/*      */       {
/*  782 */         DHTRouterNodeImpl best_node = current_node.getLeft();
/*      */         
/*  784 */         worse_node = current_node.getRight();
/*      */       }
/*      */       else {
/*  787 */         best_node = current_node.getRight();
/*      */         
/*  789 */         worse_node = current_node.getLeft();
/*      */       }
/*      */       
/*  792 */       findClosestContacts(node_id, num_to_return, depth + 1, best_node, live_only, res);
/*      */       
/*  794 */       if (res.size() < num_to_return)
/*      */       {
/*  796 */         findClosestContacts(node_id, num_to_return, depth + 1, worse_node, live_only, res);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTRouterContact findContact(byte[] node_id)
/*      */   {
/*  805 */     Object[] res = findContactSupport(node_id);
/*      */     
/*  807 */     return (DHTRouterContact)res[1];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTRouterNodeImpl findNode(byte[] node_id)
/*      */   {
/*  814 */     Object[] res = findContactSupport(node_id);
/*      */     
/*  816 */     return (DHTRouterNodeImpl)res[0];
/*      */   }
/*      */   
/*      */ 
/*      */   protected Object[] findContactSupport(byte[] node_id)
/*      */   {
/*      */     try
/*      */     {
/*  824 */       this.this_mon.enter();
/*      */       
/*  826 */       DHTRouterNodeImpl current_node = this.root;
/*      */       boolean bit;
/*  828 */       for (int i = 0; i < node_id.length; i++)
/*      */       {
/*  830 */         if (current_node.getBuckets() != null) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*  835 */         byte b = node_id[i];
/*      */         
/*  837 */         int j = 7;
/*      */         
/*  839 */         while (j >= 0)
/*      */         {
/*  841 */           bit = (b >> j & 0x1) == 1;
/*      */           
/*  843 */           if (current_node.getBuckets() != null) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*  848 */           if (bit)
/*      */           {
/*  850 */             current_node = current_node.getLeft();
/*      */           }
/*      */           else
/*      */           {
/*  854 */             current_node = current_node.getRight();
/*      */           }
/*      */           
/*  857 */           j--;
/*      */         }
/*      */       }
/*      */       
/*  861 */       List buckets = current_node.getBuckets();
/*      */       
/*  863 */       for (int k = 0; k < buckets.size(); k++)
/*      */       {
/*  865 */         DHTRouterContactImpl contact = (DHTRouterContactImpl)buckets.get(k);
/*      */         
/*  867 */         if (Arrays.equals(node_id, contact.getID()))
/*      */         {
/*  869 */           return new Object[] { current_node, contact };
/*      */         }
/*      */       }
/*      */       
/*  873 */       return new Object[] { current_node, null };
/*      */     }
/*      */     finally
/*      */     {
/*  877 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getNodeCount()
/*      */   {
/*  884 */     return getNodeCount(this.root);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long getNodeCount(DHTRouterNodeImpl node)
/*      */   {
/*  891 */     if (node.getBuckets() != null)
/*      */     {
/*  893 */       return 1L;
/*      */     }
/*      */     
/*      */ 
/*  897 */     return 1L + getNodeCount(node.getLeft()) + getNodeCount(node.getRight());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long getContactCount()
/*      */   {
/*  904 */     return getContactCount(this.root);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long getContactCount(DHTRouterNodeImpl node)
/*      */   {
/*  911 */     if (node.getBuckets() != null)
/*      */     {
/*  913 */       return node.getBuckets().size();
/*      */     }
/*      */     
/*      */ 
/*  917 */     return getContactCount(node.getLeft()) + getContactCount(node.getRight());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List findBestContacts(int max)
/*      */   {
/*  925 */     Set set = new TreeSet(new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(Object o1, Object o2)
/*      */       {
/*      */ 
/*      */ 
/*  934 */         DHTRouterContactImpl c1 = (DHTRouterContactImpl)o1;
/*  935 */         DHTRouterContactImpl c2 = (DHTRouterContactImpl)o2;
/*      */         
/*  937 */         return (int)(c2.getTimeAlive() - c1.getTimeAlive());
/*      */       }
/*      */     });
/*      */     
/*      */     try
/*      */     {
/*  943 */       this.this_mon.enter();
/*      */       
/*  945 */       findAllContacts(set, this.root);
/*      */     }
/*      */     finally
/*      */     {
/*  949 */       this.this_mon.exit();
/*      */     }
/*      */     
/*  952 */     Object result = new ArrayList(max);
/*      */     
/*  954 */     Iterator it = set.iterator();
/*      */     
/*  956 */     while ((it.hasNext()) && ((max <= 0) || (((List)result).size() < max)))
/*      */     {
/*  958 */       ((List)result).add(it.next());
/*      */     }
/*      */     
/*  961 */     return (List)result;
/*      */   }
/*      */   
/*      */   public List getAllContacts()
/*      */   {
/*      */     try
/*      */     {
/*  968 */       this.this_mon.enter();
/*      */       
/*  970 */       List l = new ArrayList();
/*      */       
/*  972 */       findAllContacts(l, this.root);
/*      */       
/*  974 */       return l;
/*      */     }
/*      */     finally
/*      */     {
/*  978 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void findAllContacts(Set set, DHTRouterNodeImpl node)
/*      */   {
/*  987 */     List buckets = node.getBuckets();
/*      */     
/*  989 */     if (buckets == null)
/*      */     {
/*  991 */       findAllContacts(set, node.getLeft());
/*      */       
/*  993 */       findAllContacts(set, node.getRight());
/*      */     }
/*      */     else {
/*  996 */       for (int i = 0; i < buckets.size(); i++)
/*      */       {
/*  998 */         DHTRouterContactImpl contact = (DHTRouterContactImpl)buckets.get(i);
/*      */         
/* 1000 */         set.add(contact);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void findAllContacts(List list, DHTRouterNodeImpl node)
/*      */   {
/* 1010 */     List buckets = node.getBuckets();
/*      */     
/* 1012 */     if (buckets == null)
/*      */     {
/* 1014 */       findAllContacts(list, node.getLeft());
/*      */       
/* 1016 */       findAllContacts(list, node.getRight());
/*      */     }
/*      */     else {
/* 1019 */       for (int i = 0; i < buckets.size(); i++)
/*      */       {
/* 1021 */         DHTRouterContactImpl contact = (DHTRouterContactImpl)buckets.get(i);
/*      */         
/* 1023 */         list.add(contact);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void seed()
/*      */   {
/* 1033 */     this.seed_in_ticks = 6;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void seedSupport()
/*      */   {
/* 1042 */     byte[] path = new byte[this.router_node_id.length];
/*      */     
/* 1044 */     List ids = new ArrayList();
/*      */     try
/*      */     {
/* 1047 */       this.this_mon.enter();
/*      */       
/* 1049 */       refreshNodes(ids, this.root, path, true, 120000L);
/*      */     }
/*      */     finally
/*      */     {
/* 1053 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1056 */     for (int i = 0; i < ids.size(); i++)
/*      */     {
/* 1058 */       requestLookup((byte[])ids.get(i), "Seeding DHT");
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
/*      */   protected void refreshNodes(List nodes_to_refresh, DHTRouterNodeImpl node, byte[] path, boolean seeding, long max_permitted_idle)
/*      */   {
/* 1072 */     if ((seeding) && (node == this.smallest_subtree))
/*      */     {
/* 1074 */       return;
/*      */     }
/*      */     
/* 1077 */     if (max_permitted_idle != 0L)
/*      */     {
/* 1079 */       if (node.getTimeSinceLastLookup() <= max_permitted_idle)
/*      */       {
/* 1081 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1085 */     if (node.getBuckets() != null)
/*      */     {
/*      */ 
/*      */ 
/* 1089 */       if ((seeding) && (node.containsRouterNodeID()))
/*      */       {
/* 1091 */         return;
/*      */       }
/*      */       
/* 1094 */       refreshNode(nodes_to_refresh, node, path);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1100 */     if (node.getBuckets() == null)
/*      */     {
/* 1102 */       int depth = node.getDepth();
/*      */       
/* 1104 */       byte mask = (byte)(1 << 7 - depth % 8);
/*      */       
/* 1106 */       path[(depth / 8)] = ((byte)(path[(depth / 8)] | mask));
/*      */       
/* 1108 */       refreshNodes(nodes_to_refresh, node.getLeft(), path, seeding, max_permitted_idle);
/*      */       
/* 1110 */       path[(depth / 8)] = ((byte)(path[(depth / 8)] & (mask ^ 0xFFFFFFFF)));
/*      */       
/* 1112 */       refreshNodes(nodes_to_refresh, node.getRight(), path, seeding, max_permitted_idle);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void refreshNode(List nodes_to_refresh, DHTRouterNodeImpl node, byte[] path)
/*      */   {
/* 1124 */     byte[] id = new byte[this.router_node_id.length];
/*      */     
/* 1126 */     this.random.nextBytes(id);
/*      */     
/* 1128 */     int depth = node.getDepth();
/*      */     
/* 1130 */     for (int i = 0; i < depth; i++)
/*      */     {
/* 1132 */       byte mask = (byte)(1 << 7 - i % 8);
/*      */       
/* 1134 */       boolean bit = (path[(i / 8)] >> 7 - i % 8 & 0x1) == 1;
/*      */       
/* 1136 */       if (bit)
/*      */       {
/* 1138 */         id[(i / 8)] = ((byte)(id[(i / 8)] | mask));
/*      */       }
/*      */       else
/*      */       {
/* 1142 */         id[(i / 8)] = ((byte)(id[(i / 8)] & (mask ^ 0xFFFFFFFF)));
/*      */       }
/*      */     }
/*      */     
/* 1146 */     nodes_to_refresh.add(id);
/*      */   }
/*      */   
/*      */ 
/*      */   protected DHTRouterNodeImpl getSmallestSubtree()
/*      */   {
/* 1152 */     return this.smallest_subtree;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void recordLookup(byte[] node_id)
/*      */   {
/* 1159 */     findNode(node_id).setLastLookupTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void refreshIdleLeaves(long idle_max)
/*      */   {
/* 1169 */     byte[] path = new byte[this.router_node_id.length];
/*      */     
/* 1171 */     List ids = new ArrayList();
/*      */     try
/*      */     {
/* 1174 */       this.this_mon.enter();
/*      */       
/* 1176 */       refreshNodes(ids, this.root, path, false, idle_max);
/*      */     }
/*      */     finally
/*      */     {
/* 1180 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1183 */     for (int i = 0; i < ids.size(); i++)
/*      */     {
/* 1185 */       requestLookup((byte[])ids.get(i), "Idle leaf refresh");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean requestPing(byte[] node_id)
/*      */   {
/* 1193 */     Object[] res = findContactSupport(node_id);
/*      */     
/* 1195 */     DHTRouterContactImpl contact = (DHTRouterContactImpl)res[1];
/*      */     
/* 1197 */     if (contact != null)
/*      */     {
/* 1199 */       this.adapter.requestPing(contact);
/*      */       
/* 1201 */       return true;
/*      */     }
/*      */     
/* 1204 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void requestPing(DHTRouterContactImpl contact)
/*      */   {
/* 1211 */     if (this.suspended)
/*      */     {
/* 1213 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1218 */     DHTLog.log("DHTRouter: requestPing:" + DHTLog.getString(contact.getID()));
/*      */     
/* 1220 */     if (contact == this.local_contact)
/*      */     {
/* 1222 */       Debug.out("pinging local contact");
/*      */     }
/*      */     try
/*      */     {
/* 1226 */       this.this_mon.enter();
/*      */       
/* 1228 */       if (!this.outstanding_pings.contains(contact))
/*      */       {
/* 1230 */         this.outstanding_pings.add(contact);
/*      */       }
/*      */     }
/*      */     finally {
/* 1234 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void dispatchPings()
/*      */   {
/* 1241 */     if (this.outstanding_pings.size() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*      */     List pings;
/*      */     
/*      */     try
/*      */     {
/* 1249 */       this.this_mon.enter();
/*      */       
/* 1251 */       pings = this.outstanding_pings;
/*      */       
/* 1253 */       this.outstanding_pings = new ArrayList();
/*      */     }
/*      */     finally
/*      */     {
/* 1257 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1260 */     if (this.suspended)
/*      */     {
/* 1262 */       return;
/*      */     }
/*      */     
/* 1265 */     for (int i = 0; i < pings.size(); i++)
/*      */     {
/* 1267 */       this.adapter.requestPing((DHTRouterContactImpl)pings.get(i));
/*      */     }
/*      */   }
/*      */   
/*      */   protected void pingeroonies()
/*      */   {
/*      */     try
/*      */     {
/* 1275 */       this.this_mon.enter();
/*      */       
/* 1277 */       DHTRouterNodeImpl node = this.root;
/*      */       
/* 1279 */       LinkedList stack = new LinkedList();
/*      */       
/*      */       for (;;)
/*      */       {
/* 1283 */         List buckets = node.getBuckets();
/*      */         
/* 1285 */         if (buckets == null)
/*      */         {
/* 1287 */           if (this.random.nextBoolean())
/*      */           {
/* 1289 */             stack.add(node.getRight());
/*      */             
/* 1291 */             node = node.getLeft();
/*      */           }
/*      */           else
/*      */           {
/* 1295 */             stack.add(node.getLeft());
/*      */             
/* 1297 */             node = node.getRight();
/*      */           }
/*      */         }
/*      */         else {
/* 1301 */           int max_fails = 0;
/* 1302 */           DHTRouterContactImpl max_fails_contact = null;
/*      */           
/* 1304 */           for (int i = 0; i < buckets.size(); i++)
/*      */           {
/* 1306 */             DHTRouterContactImpl contact = (DHTRouterContactImpl)buckets.get(i);
/*      */             
/* 1308 */             if (!contact.getPingOutstanding())
/*      */             {
/* 1310 */               int fails = contact.getFailCount();
/*      */               
/* 1312 */               if (fails > max_fails)
/*      */               {
/* 1314 */                 max_fails = fails;
/* 1315 */                 max_fails_contact = contact;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1320 */           if (max_fails_contact != null)
/*      */           {
/* 1322 */             requestPing(max_fails_contact); return;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1327 */           if (stack.size() == 0) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 1332 */           node = (DHTRouterNodeImpl)stack.removeLast();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1337 */       this.this_mon.exit();
/*      */       
/* 1339 */       dispatchPings();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void requestNodeAdd(DHTRouterContactImpl contact)
/*      */   {
/* 1347 */     if (this.suspended)
/*      */     {
/* 1349 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1354 */     DHTLog.log("DHTRouter: requestNodeAdd:" + DHTLog.getString(contact.getID()));
/*      */     
/* 1356 */     if (contact == this.local_contact)
/*      */     {
/* 1358 */       Debug.out("adding local contact");
/*      */     }
/*      */     try
/*      */     {
/* 1362 */       this.this_mon.enter();
/*      */       
/* 1364 */       if (!this.outstanding_adds.contains(contact))
/*      */       {
/* 1366 */         this.outstanding_adds.add(contact);
/*      */       }
/*      */     }
/*      */     finally {
/* 1370 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void dispatchNodeAdds()
/*      */   {
/* 1377 */     if (this.outstanding_adds.size() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*      */     List adds;
/*      */     
/*      */     try
/*      */     {
/* 1385 */       this.this_mon.enter();
/*      */       
/* 1387 */       adds = this.outstanding_adds;
/*      */       
/* 1389 */       this.outstanding_adds = new ArrayList();
/*      */     }
/*      */     finally
/*      */     {
/* 1393 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1396 */     if (this.suspended)
/*      */     {
/* 1398 */       return;
/*      */     }
/*      */     
/* 1401 */     for (int i = 0; i < adds.size(); i++)
/*      */     {
/* 1403 */       this.adapter.requestAdd((DHTRouterContactImpl)adds.get(i));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] refreshRandom()
/*      */   {
/* 1410 */     byte[] id = new byte[this.router_node_id.length];
/*      */     
/* 1412 */     this.random.nextBytes(id);
/*      */     
/* 1414 */     requestLookup(id, "Random Refresh");
/*      */     
/* 1416 */     return id;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void requestLookup(byte[] id, String description)
/*      */   {
/* 1424 */     DHTLog.log("DHTRouter: requestLookup:" + DHTLog.getString(id));
/*      */     
/* 1426 */     this.adapter.requestLookup(id, description);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void getStatsSupport(long[] stats_array, DHTRouterNodeImpl node)
/*      */   {
/* 1434 */     stats_array[0] += 1L;
/*      */     
/* 1436 */     List buckets = node.getBuckets();
/*      */     
/* 1438 */     if (buckets == null)
/*      */     {
/* 1440 */       getStatsSupport(stats_array, node.getLeft());
/*      */       
/* 1442 */       getStatsSupport(stats_array, node.getRight());
/*      */     }
/*      */     else
/*      */     {
/* 1446 */       stats_array[1] += 1L;
/*      */       
/* 1448 */       stats_array[2] += buckets.size();
/*      */       
/* 1450 */       for (int i = 0; i < buckets.size(); i++)
/*      */       {
/* 1452 */         DHTRouterContactImpl contact = (DHTRouterContactImpl)buckets.get(i);
/*      */         
/* 1454 */         if (contact.getFirstFailTime() > 0L)
/*      */         {
/* 1456 */           stats_array[6] += 1L;
/*      */         }
/* 1458 */         else if (contact.hasBeenAlive())
/*      */         {
/* 1460 */           stats_array[4] += 1L;
/*      */         }
/*      */         else
/*      */         {
/* 1464 */           stats_array[5] += 1L;
/*      */         }
/*      */       }
/*      */       
/* 1468 */       List rep = node.getReplacements();
/*      */       
/* 1470 */       if (rep != null)
/*      */       {
/* 1472 */         stats_array[3] += rep.size();
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
/*      */   protected long[] getStatsSupport()
/*      */   {
/*      */     try
/*      */     {
/* 1490 */       this.this_mon.enter();
/*      */       
/* 1492 */       long[] res = new long[7];
/*      */       
/* 1494 */       getStatsSupport(res, this.root);
/*      */       
/* 1496 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 1500 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1508 */     this.logger.log(str);
/*      */   }
/*      */   
/*      */   public void print()
/*      */   {
/*      */     try
/*      */     {
/* 1515 */       this.this_mon.enter();
/*      */       
/* 1517 */       log("DHT: " + DHTLog.getString2(this.router_node_id) + ", node count=" + getNodeCount() + ", contacts=" + getContactCount());
/*      */       
/* 1519 */       this.root.print("", "");
/*      */     }
/*      */     finally
/*      */     {
/* 1523 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 1530 */     this.timer_event.cancel();
/*      */     
/* 1532 */     notifyDead();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/impl/DHTRouterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */