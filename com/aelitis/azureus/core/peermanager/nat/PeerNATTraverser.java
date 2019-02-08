/*     */ package com.aelitis.azureus.core.peermanager.nat;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.nat.NATTraversal;
/*     */ import com.aelitis.azureus.core.nat.NATTraversalHandler;
/*     */ import com.aelitis.azureus.core.nat.NATTraversalObserver;
/*     */ import com.aelitis.azureus.core.nat.NATTraverser;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Average;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class PeerNATTraverser
/*     */   implements NATTraversalHandler
/*     */ {
/*  49 */   private static final LogIDs LOGID = LogIDs.PEER;
/*     */   private static final int OUTCOME_SUCCESS = 0;
/*     */   private static final int OUTCOME_FAILED_NO_REND = 1;
/*     */   private static final int OUTCOME_FAILED_OTHER = 2;
/*     */   private static PeerNATTraverser singleton;
/*     */   private static int MAX_ACTIVE_REQUESTS;
/*     */   private static final int TIMER_PERIOD = 10000;
/*     */   private static final int USAGE_PERIOD = 10000;
/*     */   private static final int USAGE_DURATION_SECS = 60;
/*     */   
/*     */   public static void initialise(AzureusCore core)
/*     */   {
/*  61 */     singleton = new PeerNATTraverser(core);
/*     */   }
/*     */   
/*     */ 
/*     */   public static PeerNATTraverser getSingleton()
/*     */   {
/*  67 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*  73 */     COConfigurationManager.addAndFireParameterListener("peer.nat.traversal.request.conc.max", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*  81 */         PeerNATTraverser.access$002(COConfigurationManager.getIntParameter(name));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  89 */   private static final int MAX_USAGE_PER_MIN = MAX_ACTIVE_REQUESTS * 5 * 1000;
/*     */   
/*     */   private static final int STATS_TICK_COUNT = 12;
/*     */   
/*     */   final NATTraverser nat_traverser;
/*     */   
/*  95 */   final Map initiators = new HashMap();
/*  96 */   final LinkedList pending_requests = new LinkedList();
/*  97 */   final List active_requests = new ArrayList();
/*     */   
/*  99 */   final Average usage_average = Average.getInstance(10000, 60);
/*     */   
/* 101 */   private int attempted_count = 0;
/* 102 */   private int success_count = 0;
/* 103 */   private int failed_no_rendezvous = 0;
/* 104 */   private int failed_negative_bloom = 0;
/*     */   
/* 106 */   private BloomFilter negative_result_bloom = BloomFilterFactory.createAddOnly(BLOOM_SIZE);
/*     */   
/* 108 */   private static final int BLOOM_SIZE = MAX_ACTIVE_REQUESTS * 1024;
/*     */   
/*     */   private static final int BLOOM_REBUILD_PERIOD = 300000;
/*     */   
/*     */   private static final int BLOOM_REBUILD_TICKS = 30;
/*     */   
/*     */   private PeerNATTraverser(AzureusCore core)
/*     */   {
/* 116 */     this.nat_traverser = core.getNATTraverser();
/*     */     
/* 118 */     this.nat_traverser.registerHandler(this);
/*     */     
/* 120 */     SimpleTimer.addPeriodicEvent("PeerNAT:stats", 10000L, new TimerEventPerformer()
/*     */     {
/*     */       private int ticks;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/* 131 */         this.ticks += 1;
/*     */         
/* 133 */         List to_run = null;
/*     */         
/* 135 */         synchronized (PeerNATTraverser.this.initiators)
/*     */         {
/* 137 */           if (this.ticks % 30 == 0)
/*     */           {
/* 139 */             int size = PeerNATTraverser.this.negative_result_bloom.getEntryCount();
/*     */             
/* 141 */             if (Logger.isEnabled())
/*     */             {
/* 143 */               if (size > 0)
/*     */               {
/* 145 */                 Logger.log(new LogEvent(PeerNATTraverser.LOGID, "PeerNATTraverser: negative bloom size = " + size));
/*     */               }
/*     */             }
/*     */             
/* 149 */             PeerNATTraverser.this.negative_result_bloom = BloomFilterFactory.createAddOnly(PeerNATTraverser.BLOOM_SIZE);
/*     */           }
/*     */           
/* 152 */           if (this.ticks % 12 == 0)
/*     */           {
/* 154 */             String msg = "NAT traversal stats: active=" + PeerNATTraverser.this.active_requests.size() + ",pending=" + PeerNATTraverser.this.pending_requests.size() + ",attempted=" + PeerNATTraverser.this.attempted_count + ",no rendezvous=" + PeerNATTraverser.this.failed_no_rendezvous + ",negative bloom=" + PeerNATTraverser.this.failed_negative_bloom + ",successful=" + PeerNATTraverser.this.success_count;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 162 */             if (Logger.isEnabled()) {
/* 163 */               Logger.log(new LogEvent(PeerNATTraverser.LOGID, msg));
/*     */             }
/*     */           }
/*     */           
/* 167 */           int used = 0;
/*     */           
/* 169 */           for (int i = 0; i < PeerNATTraverser.this.active_requests.size(); i++)
/*     */           {
/* 171 */             used = (int)(used + ((PeerNATTraverser.PeerNATTraversal)PeerNATTraverser.this.active_requests.get(i)).getTimeUsed());
/*     */           }
/*     */           
/* 174 */           PeerNATTraverser.this.usage_average.addValue(used);
/*     */           
/* 176 */           int usage = (int)PeerNATTraverser.this.usage_average.getAverage();
/*     */           
/* 178 */           if (usage > PeerNATTraverser.MAX_USAGE_PER_MIN)
/*     */           {
/* 180 */             return;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 187 */           while ((PeerNATTraverser.this.pending_requests.size() != 0) && (PeerNATTraverser.this.active_requests.size() < PeerNATTraverser.MAX_ACTIVE_REQUESTS))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 195 */             PeerNATTraverser.PeerNATTraversal traversal = (PeerNATTraverser.PeerNATTraversal)PeerNATTraverser.this.pending_requests.removeFirst();
/*     */             
/* 197 */             PeerNATTraverser.this.active_requests.add(traversal);
/*     */             
/* 199 */             if (to_run == null)
/*     */             {
/* 201 */               to_run = new ArrayList();
/*     */             }
/*     */             
/* 204 */             to_run.add(traversal);
/*     */             
/* 206 */             PeerNATTraverser.access$408(PeerNATTraverser.this);
/*     */           }
/*     */         }
/*     */         
/* 210 */         if (to_run != null)
/*     */         {
/* 212 */           for (int i = 0; i < to_run.size(); i++)
/*     */           {
/* 214 */             PeerNATTraverser.PeerNATTraversal traversal = (PeerNATTraverser.PeerNATTraversal)to_run.get(i);
/*     */             
/* 216 */             boolean bad = false;
/*     */             
/* 218 */             synchronized (PeerNATTraverser.this.initiators)
/*     */             {
/* 220 */               if (PeerNATTraverser.this.negative_result_bloom.contains(traversal.getTarget().toString().getBytes()))
/*     */               {
/* 222 */                 bad = true;
/*     */                 
/* 224 */                 PeerNATTraverser.access$608(PeerNATTraverser.this);
/*     */               }
/*     */             }
/*     */             
/* 228 */             if (bad)
/*     */             {
/* 230 */               PeerNATTraverser.this.removeRequest(traversal, 2);
/*     */               
/* 232 */               traversal.getAdapter().failed();
/*     */             }
/*     */             else {
/* 235 */               traversal.run();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 246 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 252 */     return "Peer Traversal";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void register(PeerNATInitiator initiator)
/*     */   {
/* 259 */     synchronized (this.initiators)
/*     */     {
/* 261 */       if (this.initiators.put(initiator, new LinkedList()) != null)
/*     */       {
/* 263 */         Debug.out("initiator already present");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void unregister(PeerNATInitiator initiator)
/*     */   {
/*     */     List to_cancel;
/*     */     
/* 274 */     synchronized (this.initiators)
/*     */     {
/* 276 */       LinkedList requests = (LinkedList)this.initiators.remove(initiator);
/*     */       
/* 278 */       if (requests == null)
/*     */       {
/* 280 */         Debug.out("initiator not present");
/*     */         
/* 282 */         return;
/*     */       }
/*     */       
/*     */ 
/* 286 */       to_cancel = requests;
/*     */     }
/*     */     
/*     */ 
/* 290 */     Iterator it = to_cancel.iterator();
/*     */     
/* 292 */     while (it.hasNext())
/*     */     {
/* 294 */       PeerNATTraversal traversal = (PeerNATTraversal)it.next();
/*     */       
/* 296 */       traversal.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void create(PeerNATInitiator initiator, InetSocketAddress target, PeerNATTraversalAdapter adapter)
/*     */   {
/* 306 */     boolean bad = false;
/*     */     
/* 308 */     synchronized (this.initiators)
/*     */     {
/* 310 */       if (this.negative_result_bloom.contains(target.toString().getBytes()))
/*     */       {
/* 312 */         bad = true;
/*     */         
/* 314 */         this.failed_negative_bloom += 1;
/*     */       }
/*     */       else
/*     */       {
/* 318 */         LinkedList requests = (LinkedList)this.initiators.get(initiator);
/*     */         
/* 320 */         if (requests == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 325 */           bad = true;
/*     */         }
/*     */         else
/*     */         {
/* 329 */           PeerNATTraversal traversal = new PeerNATTraversal(initiator, target, adapter);
/*     */           
/* 331 */           requests.addLast(traversal);
/*     */           
/* 333 */           this.pending_requests.addLast(traversal);
/*     */           
/* 335 */           if (Logger.isEnabled()) {
/* 336 */             Logger.log(new LogEvent(LOGID, "created NAT traversal for " + initiator.getDisplayName() + "/" + target));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 345 */     if (bad)
/*     */     {
/* 347 */       adapter.failed();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List getTraversals(PeerNATInitiator initiator)
/*     */   {
/* 355 */     List result = new ArrayList();
/*     */     
/* 357 */     synchronized (this.initiators)
/*     */     {
/* 359 */       LinkedList requests = (LinkedList)this.initiators.get(initiator);
/*     */       
/* 361 */       if (requests != null)
/*     */       {
/* 363 */         Iterator it = requests.iterator();
/*     */         
/* 365 */         while (it.hasNext())
/*     */         {
/* 367 */           PeerNATTraversal x = (PeerNATTraversal)it.next();
/*     */           
/* 369 */           result.add(x.getTarget());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 374 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeRequest(PeerNATTraversal request, int outcome)
/*     */   {
/* 382 */     synchronized (this.initiators)
/*     */     {
/* 384 */       LinkedList requests = (LinkedList)this.initiators.get(request.getInitiator());
/*     */       
/* 386 */       if (requests != null)
/*     */       {
/* 388 */         requests.remove(request);
/*     */       }
/*     */       
/* 391 */       this.pending_requests.remove(request);
/*     */       
/* 393 */       if (this.active_requests.remove(request))
/*     */       {
/* 395 */         this.usage_average.addValue(request.getTimeUsed());
/*     */         
/* 397 */         if (outcome == 0)
/*     */         {
/* 399 */           this.success_count += 1;
/*     */         }
/*     */         else
/*     */         {
/* 403 */           InetSocketAddress target = request.getTarget();
/*     */           
/* 405 */           this.negative_result_bloom.add(target.toString().getBytes());
/*     */           
/* 407 */           if (outcome == 1)
/*     */           {
/* 409 */             this.failed_no_rendezvous += 1;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map process(InetSocketAddress originator, Map data)
/*     */   {
/* 423 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected class PeerNATTraversal
/*     */     implements NATTraversalObserver
/*     */   {
/*     */     private final PeerNATInitiator initiator;
/*     */     
/*     */     private final InetSocketAddress target;
/*     */     
/*     */     private final PeerNATTraversalAdapter adapter;
/*     */     
/*     */     private NATTraversal traversal;
/*     */     
/*     */     private boolean cancelled;
/*     */     
/*     */     private long time;
/*     */     
/*     */ 
/*     */     protected PeerNATTraversal(PeerNATInitiator _initiator, InetSocketAddress _target, PeerNATTraversalAdapter _adapter)
/*     */     {
/* 445 */       this.initiator = _initiator;
/* 446 */       this.target = _target;
/* 447 */       this.adapter = _adapter;
/*     */     }
/*     */     
/*     */ 
/*     */     protected PeerNATInitiator getInitiator()
/*     */     {
/* 453 */       return this.initiator;
/*     */     }
/*     */     
/*     */ 
/*     */     protected InetSocketAddress getTarget()
/*     */     {
/* 459 */       return this.target;
/*     */     }
/*     */     
/*     */ 
/*     */     protected PeerNATTraversalAdapter getAdapter()
/*     */     {
/* 465 */       return this.adapter;
/*     */     }
/*     */     
/*     */ 
/*     */     protected long getTimeUsed()
/*     */     {
/* 471 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 473 */       long elapsed = now - this.time;
/*     */       
/* 475 */       this.time = now;
/*     */       
/* 477 */       if (elapsed < 0L)
/*     */       {
/* 479 */         elapsed = 0L;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 484 */         elapsed = Math.min(elapsed, 10000L);
/*     */       }
/*     */       
/* 487 */       return elapsed;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void run()
/*     */     {
/* 493 */       synchronized (this)
/*     */       {
/* 495 */         if (!this.cancelled)
/*     */         {
/* 497 */           this.time = SystemTime.getCurrentTime();
/*     */           
/* 499 */           this.traversal = PeerNATTraverser.this.nat_traverser.attemptTraversal(PeerNATTraverser.this, this.target, null, false, this);
/*     */         }
/*     */       }
/*     */     }
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
/*     */     public void succeeded(InetSocketAddress rendezvous, InetSocketAddress target, Map reply)
/*     */     {
/* 516 */       PeerNATTraverser.this.removeRequest(this, 0);
/*     */       
/* 518 */       if (Logger.isEnabled()) {
/* 519 */         Logger.log(new LogEvent(PeerNATTraverser.LOGID, "NAT traversal for " + this.initiator.getDisplayName() + "/" + target + " succeeded"));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 525 */       this.adapter.success(target);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void failed(int reason)
/*     */     {
/* 533 */       PeerNATTraverser.this.removeRequest(this, reason == 1 ? 1 : 2);
/*     */       
/* 535 */       this.adapter.failed();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void failed(Throwable cause)
/*     */     {
/* 542 */       PeerNATTraverser.this.removeRequest(this, 2);
/*     */       
/* 544 */       this.adapter.failed();
/*     */     }
/*     */     
/*     */ 
/*     */     public void disabled()
/*     */     {
/* 550 */       PeerNATTraverser.this.removeRequest(this, 2);
/*     */       
/* 552 */       this.adapter.failed();
/*     */     }
/*     */     
/*     */ 
/*     */     protected void cancel()
/*     */     {
/*     */       NATTraversal active_traversal;
/*     */       
/* 560 */       synchronized (this)
/*     */       {
/* 562 */         this.cancelled = true;
/*     */         
/* 564 */         active_traversal = this.traversal;
/*     */       }
/*     */       
/* 567 */       if (active_traversal == null)
/*     */       {
/* 569 */         PeerNATTraverser.this.removeRequest(this, 2);
/*     */       }
/*     */       else
/*     */       {
/* 573 */         active_traversal.cancel();
/*     */       }
/*     */       
/* 576 */       this.adapter.failed();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/nat/PeerNATTraverser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */