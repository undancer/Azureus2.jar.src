/*     */ package com.aelitis.azureus.core.dht.speed.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.DHTLogger;
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTester;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContact;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContactListener;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterListener;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandlerAdapter;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
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
/*     */ public class DHTSpeedTesterImpl
/*     */   implements DHTSpeedTester
/*     */ {
/*     */   private static final long PING_TIMEOUT = 5000L;
/*     */   private final PluginInterface plugin_interface;
/*     */   private final DHT dht;
/*     */   private int contact_num;
/*     */   private BloomFilter tried_bloom;
/*  55 */   private final LinkedList pending_contacts = new LinkedList();
/*  56 */   private final List active_pings = new ArrayList();
/*     */   
/*  58 */   private final List<DHTSpeedTesterListener> new_listeners = new ArrayList();
/*  59 */   private final CopyOnWriteList<DHTSpeedTesterListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTSpeedTesterImpl(DHT _dht)
/*     */   {
/*  65 */     this.dht = _dht;
/*     */     
/*  67 */     this.plugin_interface = this.dht.getLogger().getPluginInterface();
/*     */     
/*  69 */     UTTimer timer = this.plugin_interface.getUtilities().createTimer("DHTSpeedTester:finder", true);
/*     */     
/*     */ 
/*  72 */     timer.addPeriodicEvent(5000L, new UTTimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void perform(UTTimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/*  80 */         DHTSpeedTesterImpl.this.findContacts();
/*     */       }
/*     */       
/*  83 */     });
/*  84 */     timer.addPeriodicEvent(1000L, new UTTimerEventPerformer()
/*     */     {
/*     */       int tick_count;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(UTTimerEvent event)
/*     */       {
/*     */         try
/*     */         {
/*  95 */           DHTSpeedTesterImpl.this.pingContacts(this.tick_count);
/*     */         }
/*     */         finally
/*     */         {
/*  99 */           this.tick_count += 1;
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public int getContactNumber()
/*     */   {
/* 108 */     return this.contact_num;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setContactNumber(int number)
/*     */   {
/* 115 */     this.contact_num = number;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void findContacts()
/*     */   {
/* 121 */     DHTTransportContact[] reachables = this.dht.getTransport().getReachableContacts();
/*     */     
/* 123 */     for (int i = 0; i < reachables.length; i++)
/*     */     {
/* 125 */       DHTTransportContact contact = reachables[i];
/*     */       
/* 127 */       byte[] address = contact.getAddress().getAddress().getAddress();
/*     */       
/* 129 */       if ((this.tried_bloom == null) || (this.tried_bloom.getEntryCount() > 500))
/*     */       {
/* 131 */         this.tried_bloom = BloomFilterFactory.createAddOnly(4096);
/*     */       }
/*     */       
/* 134 */       if (!this.tried_bloom.contains(address))
/*     */       {
/* 136 */         this.tried_bloom.add(address);
/*     */         
/* 138 */         synchronized (this.pending_contacts)
/*     */         {
/* 140 */           potentialPing ping = new potentialPing(contact, DHTNetworkPositionManager.estimateRTT(contact.getNetworkPositions(), this.dht.getTransport().getLocalContact().getNetworkPositions()));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 145 */           this.pending_contacts.add(0, ping);
/*     */           
/* 147 */           if (this.pending_contacts.size() > 60)
/*     */           {
/* 149 */             this.pending_contacts.removeLast();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void pingContacts(int tick_count)
/*     */   {
/* 160 */     List copy = null;
/*     */     
/* 162 */     synchronized (this.new_listeners)
/*     */     {
/* 164 */       if (this.new_listeners.size() > 0)
/*     */       {
/* 166 */         copy = new ArrayList(this.new_listeners);
/*     */         
/* 168 */         this.new_listeners.clear();
/*     */       }
/*     */     }
/*     */     
/* 172 */     if (copy != null)
/*     */     {
/* 174 */       for (int i = 0; i < copy.size(); i++)
/*     */       {
/* 176 */         DHTSpeedTesterListener listener = (DHTSpeedTesterListener)copy.get(i);
/*     */         
/* 178 */         this.listeners.add(listener);
/*     */         
/* 180 */         for (int j = 0; j < this.active_pings.size(); j++)
/*     */         {
/* 182 */           activePing ping = (activePing)this.active_pings.get(j);
/*     */           
/* 184 */           if (ping.isInformedAlive())
/*     */           {
/*     */             try
/*     */             {
/* 188 */               listener.contactAdded(ping);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 192 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 199 */     Iterator pit = this.active_pings.iterator();
/*     */     
/* 201 */     pingInstanceSet ping_set = new pingInstanceSet(true);
/*     */     
/* 203 */     while (pit.hasNext())
/*     */     {
/* 205 */       activePing ping = (activePing)pit.next();
/*     */       
/* 207 */       if (ping.update(ping_set, tick_count))
/*     */       {
/* 209 */         if (!ping.isInformedAlive())
/*     */         {
/* 211 */           ping.setInformedAlive();
/*     */           
/* 213 */           Iterator it = this.listeners.iterator();
/*     */           
/* 215 */           while (it.hasNext()) {
/*     */             try
/*     */             {
/* 218 */               ((DHTSpeedTesterListener)it.next()).contactAdded(ping);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 222 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 228 */       if (ping.isDead())
/*     */       {
/* 230 */         pit.remove();
/*     */         
/* 232 */         ping.informDead();
/*     */       }
/*     */     }
/*     */     
/* 236 */     ping_set.setFull();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 241 */     int num_active = this.active_pings.size();
/*     */     
/* 243 */     if (num_active < this.contact_num)
/*     */     {
/* 245 */       Set pc = new TreeSet(new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */         public int compare(Object o1, Object o2)
/*     */         {
/*     */ 
/*     */ 
/* 253 */           DHTSpeedTesterImpl.potentialPing p1 = (DHTSpeedTesterImpl.potentialPing)o1;
/* 254 */           DHTSpeedTesterImpl.potentialPing p2 = (DHTSpeedTesterImpl.potentialPing)o2;
/*     */           
/* 256 */           return p1.getRTT() - p2.getRTT();
/*     */         }
/*     */       });
/*     */       
/* 260 */       synchronized (this.pending_contacts)
/*     */       {
/* 262 */         pc.addAll(this.pending_contacts);
/*     */       }
/*     */       
/* 265 */       Iterator it = pc.iterator();
/*     */       
/* 267 */       if (pc.size() >= 3)
/*     */       {
/*     */ 
/*     */ 
/* 271 */         Object pps = new ArrayList();
/*     */         
/* 273 */         for (int i = 0; i < 3; i++)
/*     */         {
/* 275 */           potentialPing pp = (potentialPing)it.next();
/*     */           
/* 277 */           ((List)pps).add(pp);
/*     */           
/* 279 */           it.remove();
/*     */           
/* 281 */           synchronized (this.pending_contacts)
/*     */           {
/* 283 */             this.pending_contacts.remove(pp);
/*     */           }
/*     */         }
/*     */         
/* 287 */         this.active_pings.add(new activePing((List)pps));
/*     */       }
/* 289 */     } else if (num_active > this.contact_num)
/*     */     {
/* 291 */       for (int i = 0; i < num_active - this.contact_num; i++)
/*     */       {
/* 293 */         ((activePing)this.active_pings.get(i)).destroy();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void informResults(DHTSpeedTesterContact[] contacts, int[] rtts)
/*     */   {
/* 303 */     Iterator it = this.listeners.iterator();
/*     */     
/* 305 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 308 */         ((DHTSpeedTesterListener)it.next()).resultGroup(contacts, rtts);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 312 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 320 */     synchronized (this.new_listeners)
/*     */     {
/* 322 */       for (DHTSpeedTesterListener l : this.new_listeners) {
/*     */         try
/*     */         {
/* 325 */           l.destroyed();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 329 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */       
/* 333 */       this.new_listeners.clear();
/*     */     }
/*     */     
/* 336 */     for (DHTSpeedTesterListener l : this.listeners) {
/*     */       try
/*     */       {
/* 339 */         l.destroyed();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 343 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(DHTSpeedTesterListener listener)
/*     */   {
/* 352 */     synchronized (this.new_listeners)
/*     */     {
/* 354 */       this.new_listeners.add(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(DHTSpeedTesterListener listener)
/*     */   {
/* 362 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class potentialPing
/*     */   {
/*     */     private final DHTTransportContact contact;
/*     */     
/*     */     private final int rtt;
/*     */     
/*     */ 
/*     */     protected potentialPing(DHTTransportContact _contact, float _rtt)
/*     */     {
/* 376 */       this.contact = _contact;
/* 377 */       this.rtt = ((int)(Float.isNaN(_rtt) ? 1000.0D : _rtt));
/*     */     }
/*     */     
/*     */ 
/*     */     protected DHTTransportContact getContact()
/*     */     {
/* 383 */       return this.contact;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int getRTT()
/*     */     {
/* 389 */       return this.rtt;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected class activePing
/*     */     implements DHTSpeedTesterContact
/*     */   {
/*     */     private boolean running;
/*     */     
/*     */     private boolean dead;
/*     */     private boolean informed_alive;
/*     */     private int outstanding;
/* 402 */     private int best_ping = Integer.MAX_VALUE;
/*     */     
/*     */     private DHTTransportContact best_pingee;
/*     */     private int consec_fails;
/*     */     private int total_ok;
/*     */     private int total_fails;
/* 408 */     private int period = 5;
/* 409 */     final CopyOnWriteList listeners = new CopyOnWriteList();
/*     */     
/*     */ 
/*     */ 
/*     */     protected activePing(List candidates)
/*     */     {
/* 415 */       String str = "";
/*     */       
/* 417 */       DHTSpeedTesterImpl.pingInstanceSet ping_set = new DHTSpeedTesterImpl.pingInstanceSet(DHTSpeedTesterImpl.this, false);
/*     */       
/* 419 */       synchronized (this)
/*     */       {
/* 421 */         for (int i = 0; i < candidates.size(); i++)
/*     */         {
/* 423 */           DHTSpeedTesterImpl.potentialPing pp = (DHTSpeedTesterImpl.potentialPing)candidates.get(i);
/*     */           
/* 425 */           str = str + (i == 0 ? "" : ",") + pp.getContact().getString() + "/" + pp.getRTT();
/*     */           
/* 427 */           ping(ping_set, pp.getContact());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected boolean update(DHTSpeedTesterImpl.pingInstanceSet ping_set, int tick_count)
/*     */     {
/* 437 */       synchronized (this)
/*     */       {
/* 439 */         if ((this.dead) || (!this.running) || (this.outstanding > 0))
/*     */         {
/* 441 */           return false;
/*     */         }
/*     */         
/* 444 */         if (this.best_pingee == null)
/*     */         {
/* 446 */           this.dead = true;
/*     */           
/* 448 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 452 */       if (tick_count % this.period == 0)
/*     */       {
/* 454 */         ping(ping_set, this.best_pingee);
/*     */       }
/*     */       
/* 457 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void ping(DHTSpeedTesterImpl.pingInstanceSet ping_set, DHTTransportContact contact)
/*     */     {
/* 465 */       final DHTSpeedTesterImpl.pingInstance pi = new DHTSpeedTesterImpl.pingInstance(ping_set);
/*     */       
/* 467 */       synchronized (this)
/*     */       {
/* 469 */         this.outstanding += 1;
/*     */       }
/*     */       try
/*     */       {
/* 473 */         contact.sendImmediatePing(new DHTTransportReplyHandlerAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void pingReply(DHTTransportContact contact)
/*     */           {
/*     */ 
/* 480 */             int rtt = getElapsed();
/*     */             
/* 482 */             if (rtt < 0)
/*     */             {
/* 484 */               Debug.out("Invalid RTT: " + rtt);
/*     */             }
/*     */             try
/*     */             {
/* 488 */               synchronized (DHTSpeedTesterImpl.activePing.this)
/*     */               {
/* 490 */                 DHTSpeedTesterImpl.activePing.access$010(DHTSpeedTesterImpl.activePing.this);
/*     */                 
/* 492 */                 if (!DHTSpeedTesterImpl.activePing.this.running)
/*     */                 {
/* 494 */                   if (rtt < DHTSpeedTesterImpl.activePing.this.best_ping)
/*     */                   {
/* 496 */                     DHTSpeedTesterImpl.activePing.this.best_pingee = contact;
/* 497 */                     DHTSpeedTesterImpl.activePing.this.best_ping = rtt;
/*     */                   }
/*     */                   
/* 500 */                   if (DHTSpeedTesterImpl.activePing.this.outstanding == 0)
/*     */                   {
/* 502 */                     DHTSpeedTesterImpl.activePing.this.running = true;
/*     */                   }
/*     */                 }
/*     */                 else {
/* 506 */                   DHTSpeedTesterImpl.activePing.access$408(DHTSpeedTesterImpl.activePing.this);
/*     */                   
/* 508 */                   DHTSpeedTesterImpl.activePing.this.consec_fails = 0;
/*     */                 }
/*     */               }
/*     */               
/* 512 */               Iterator it = DHTSpeedTesterImpl.activePing.this.listeners.iterator();
/*     */               
/* 514 */               while (it.hasNext()) {
/*     */                 try
/*     */                 {
/* 517 */                   ((DHTSpeedTesterContactListener)it.next()).ping(DHTSpeedTesterImpl.activePing.this, getElapsed());
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 521 */                   Debug.printStackTrace(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             finally {
/* 526 */               pi.setResult(DHTSpeedTesterImpl.activePing.this, rtt);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           public void failed(DHTTransportContact contact, Throwable error)
/*     */           {
/*     */             try
/*     */             {
/* 537 */               synchronized (DHTSpeedTesterImpl.activePing.this)
/*     */               {
/* 539 */                 DHTSpeedTesterImpl.activePing.access$010(DHTSpeedTesterImpl.activePing.this);
/*     */                 
/* 541 */                 if (!DHTSpeedTesterImpl.activePing.this.running)
/*     */                 {
/* 543 */                   if (DHTSpeedTesterImpl.activePing.this.outstanding == 0)
/*     */                   {
/* 545 */                     DHTSpeedTesterImpl.activePing.this.running = true;
/*     */                   }
/*     */                 }
/*     */                 else {
/* 549 */                   DHTSpeedTesterImpl.activePing.access$508(DHTSpeedTesterImpl.activePing.this);
/* 550 */                   DHTSpeedTesterImpl.activePing.access$608(DHTSpeedTesterImpl.activePing.this);
/*     */                   
/* 552 */                   if (DHTSpeedTesterImpl.activePing.this.consec_fails == 3)
/*     */                   {
/* 554 */                     DHTSpeedTesterImpl.activePing.this.dead = true;
/*     */                   }
/* 556 */                   else if ((DHTSpeedTesterImpl.activePing.this.total_ok > 10) && (DHTSpeedTesterImpl.activePing.this.total_fails > 0) && (DHTSpeedTesterImpl.activePing.this.total_ok / DHTSpeedTesterImpl.activePing.this.total_fails < 1))
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/* 561 */                     DHTSpeedTesterImpl.activePing.this.dead = true;
/*     */                   }
/* 563 */                   else if (DHTSpeedTesterImpl.activePing.this.total_ok > 100)
/*     */                   {
/* 565 */                     DHTSpeedTesterImpl.activePing.this.total_ok = 0;
/* 566 */                     DHTSpeedTesterImpl.activePing.this.total_fails = 0;
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 571 */               if (!DHTSpeedTesterImpl.activePing.this.dead)
/*     */               {
/* 573 */                 Iterator it = DHTSpeedTesterImpl.activePing.this.listeners.iterator();
/*     */                 
/* 575 */                 while (it.hasNext()) {
/*     */                   try
/*     */                   {
/* 578 */                     ((DHTSpeedTesterContactListener)it.next()).pingFailed(DHTSpeedTesterImpl.activePing.this);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 582 */                     Debug.printStackTrace(e);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             finally
/*     */             {
/* 589 */               pi.setResult(DHTSpeedTesterImpl.activePing.this, -1); } } }, 5000L);
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 598 */           pi.setResult(this, -1);
/*     */         }
/*     */         finally
/*     */         {
/* 602 */           synchronized (this)
/*     */           {
/* 604 */             this.dead = true;
/*     */             
/* 606 */             this.outstanding -= 1;
/*     */           }
/*     */         }
/*     */         
/* 610 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void destroy()
/*     */     {
/* 617 */       this.dead = true;
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean isDead()
/*     */     {
/* 623 */       return this.dead;
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean isInformedAlive()
/*     */     {
/* 629 */       return this.informed_alive;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void setInformedAlive()
/*     */     {
/* 635 */       this.informed_alive = true;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void informDead()
/*     */     {
/* 641 */       if (this.informed_alive)
/*     */       {
/* 643 */         Iterator it = this.listeners.iterator();
/*     */         
/* 645 */         while (it.hasNext()) {
/*     */           try
/*     */           {
/* 648 */             ((DHTSpeedTesterContactListener)it.next()).contactDied(this);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 652 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public DHTTransportContact getContact()
/*     */     {
/* 661 */       return this.best_pingee;
/*     */     }
/*     */     
/*     */ 
/*     */     public InetSocketAddress getAddress()
/*     */     {
/* 667 */       return getContact().getAddress();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getString()
/*     */     {
/* 673 */       return getContact().getString();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPingPeriod()
/*     */     {
/* 679 */       return this.period;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setPingPeriod(int _period)
/*     */     {
/* 686 */       this.period = _period;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void addListener(DHTSpeedTesterContactListener listener)
/*     */     {
/* 693 */       this.listeners.add(listener);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void removeListener(DHTSpeedTesterContactListener listener)
/*     */     {
/* 700 */       this.listeners.remove(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class pingInstance
/*     */   {
/*     */     private DHTSpeedTesterImpl.activePing contact;
/*     */     
/*     */     private final DHTSpeedTesterImpl.pingInstanceSet set;
/*     */     
/*     */     private int result;
/*     */     
/*     */     protected pingInstance(DHTSpeedTesterImpl.pingInstanceSet _set)
/*     */     {
/* 715 */       this.set = _set;
/*     */       
/* 717 */       this.set.add(this);
/*     */     }
/*     */     
/*     */ 
/*     */     protected DHTSpeedTesterImpl.activePing getContact()
/*     */     {
/* 723 */       return this.contact;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int getResult()
/*     */     {
/* 729 */       return this.result;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void setResult(DHTSpeedTesterImpl.activePing _contact, int _result)
/*     */     {
/* 737 */       this.contact = _contact;
/* 738 */       this.result = _result;
/*     */       
/* 740 */       this.set.complete(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected class pingInstanceSet
/*     */   {
/*     */     private final boolean active;
/*     */     
/*     */     private int instances;
/*     */     private boolean full;
/* 751 */     final List results = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */     protected pingInstanceSet(boolean _active)
/*     */     {
/* 757 */       this.active = _active;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void add(DHTSpeedTesterImpl.pingInstance instance)
/*     */     {
/* 764 */       synchronized (this)
/*     */       {
/* 766 */         this.instances += 1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected void setFull()
/*     */     {
/* 773 */       synchronized (this)
/*     */       {
/* 775 */         this.full = true;
/*     */         
/* 777 */         if (this.results.size() == this.instances)
/*     */         {
/* 779 */           sendResult();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void complete(DHTSpeedTesterImpl.pingInstance instance)
/*     */     {
/* 788 */       synchronized (this)
/*     */       {
/* 790 */         this.results.add(instance);
/*     */         
/* 792 */         if ((this.results.size() == this.instances) && (this.full))
/*     */         {
/* 794 */           sendResult();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected void sendResult()
/*     */     {
/* 802 */       if ((this.active) && (this.results.size() > 0))
/*     */       {
/* 804 */         DHTSpeedTesterContact[] contacts = new DHTSpeedTesterContact[this.results.size()];
/* 805 */         int[] rtts = new int[contacts.length];
/*     */         
/* 807 */         for (int i = 0; i < contacts.length; i++)
/*     */         {
/* 809 */           DHTSpeedTesterImpl.pingInstance pi = (DHTSpeedTesterImpl.pingInstance)this.results.get(i);
/*     */           
/* 811 */           contacts[i] = pi.getContact();
/* 812 */           rtts[i] = pi.getResult();
/*     */         }
/*     */         
/* 815 */         DHTSpeedTesterImpl.this.informResults(contacts, rtts);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/speed/impl/DHTSpeedTesterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */