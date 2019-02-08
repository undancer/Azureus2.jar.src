/*     */ package com.aelitis.azureus.core.speedmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTester;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContact;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContactListener;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterListener;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TestPingSourceImpl
/*     */   implements DHTSpeedTester
/*     */ {
/*     */   private final SpeedManagerAlgorithmProviderAdapter adapter;
/*     */   private volatile int contact_num;
/*  44 */   private final List listeners = new ArrayList();
/*     */   
/*  46 */   final CopyOnWriteList sources = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */   private int period;
/*     */   
/*     */ 
/*     */   protected TestPingSourceImpl(SpeedManagerAlgorithmProviderAdapter _adapter)
/*     */   {
/*  54 */     this.adapter = _adapter;
/*     */     
/*  56 */     SimpleTimer.addPeriodicEvent("TestPingSourceImpl", 1000L, new TimerEventPerformer()
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
/*  67 */         this.ticks += 1;
/*     */         
/*     */         List sources_to_update;
/*     */         
/*  71 */         synchronized (TestPingSourceImpl.this.sources)
/*     */         {
/*  73 */           while (TestPingSourceImpl.this.sources.size() < TestPingSourceImpl.this.contact_num)
/*     */           {
/*  75 */             TestPingSourceImpl.this.addContact(new TestPingSourceImpl.testSource(TestPingSourceImpl.this));
/*     */           }
/*     */           
/*  78 */           sources_to_update = TestPingSourceImpl.this.sources.getList();
/*     */         }
/*     */         
/*  81 */         if (TestPingSourceImpl.this.period > 0)
/*     */         {
/*  83 */           if (this.ticks % TestPingSourceImpl.this.period == 0)
/*     */           {
/*  85 */             TestPingSourceImpl.testSource[] contacts = new TestPingSourceImpl.testSource[sources_to_update.size()];
/*     */             
/*  87 */             sources_to_update.toArray(contacts);
/*     */             
/*  89 */             TestPingSourceImpl.this.update(contacts);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected SpeedManagerAlgorithmProviderAdapter getAdapter()
/*     */   {
/*  99 */     return this.adapter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void update(testSource[] contacts)
/*     */   {
/* 106 */     int[] round_trip_times = new int[contacts.length];
/*     */     
/* 108 */     updateSources(contacts);
/*     */     
/* 110 */     for (int i = 0; i < round_trip_times.length; i++)
/*     */     {
/* 112 */       round_trip_times[i] = contacts[i].getRTT();
/*     */     }
/*     */     
/* 115 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 117 */       ((DHTSpeedTesterListener)this.listeners.get(i)).resultGroup(contacts, round_trip_times);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void updateSources(testSource[] paramArrayOftestSource);
/*     */   
/*     */ 
/*     */   public int getContactNumber()
/*     */   {
/* 128 */     return this.contact_num;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setContactNumber(int number)
/*     */   {
/* 135 */     this.contact_num = number;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addContact(testSource contact)
/*     */   {
/* 142 */     synchronized (this.sources)
/*     */     {
/* 144 */       this.sources.add(contact);
/*     */     }
/*     */     
/* 147 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 149 */       ((DHTSpeedTesterListener)this.listeners.get(i)).contactAdded(contact);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeContact(testSource contact)
/*     */   {
/* 157 */     synchronized (this.sources)
/*     */     {
/* 159 */       this.sources.remove(contact);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 167 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 169 */       ((DHTSpeedTesterListener)this.listeners.get(i)).destroyed();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(DHTSpeedTesterListener listener)
/*     */   {
/* 177 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(DHTSpeedTesterListener listener)
/*     */   {
/* 184 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   protected class testSource
/*     */     implements DHTSpeedTesterContact
/*     */   {
/* 191 */     private final InetSocketAddress address = new InetSocketAddress(1);
/*     */     
/* 193 */     private final List listeners = new ArrayList();
/*     */     private int rtt;
/*     */     
/*     */     protected testSource() {}
/*     */     
/*     */     public InetSocketAddress getAddress()
/*     */     {
/* 200 */       return this.address;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getString()
/*     */     {
/* 206 */       return "test source";
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPingPeriod()
/*     */     {
/* 212 */       return TestPingSourceImpl.this.period;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setPingPeriod(int period_secs)
/*     */     {
/* 219 */       TestPingSourceImpl.this.period = period_secs;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int getRTT()
/*     */     {
/* 225 */       return this.rtt;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setRTT(int _rtt)
/*     */     {
/* 232 */       this.rtt = _rtt;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void failed()
/*     */     {
/* 238 */       for (int i = 0; i < this.listeners.size(); i++)
/*     */       {
/* 240 */         ((DHTSpeedTesterContactListener)this.listeners.get(i)).contactDied(this);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void destroy()
/*     */     {
/* 247 */       TestPingSourceImpl.this.removeContact(this);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void addListener(DHTSpeedTesterContactListener listener)
/*     */     {
/* 254 */       this.listeners.add(listener);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void removeListener(DHTSpeedTesterContactListener listener)
/*     */     {
/* 261 */       this.listeners.remove(listener);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/TestPingSourceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */