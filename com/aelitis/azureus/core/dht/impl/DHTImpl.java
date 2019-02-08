/*     */ package com.aelitis.azureus.core.dht.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.DHTListener;
/*     */ import com.aelitis.azureus.core.dht.DHTLogger;
/*     */ import com.aelitis.azureus.core.dht.DHTOperationListener;
/*     */ import com.aelitis.azureus.core.dht.DHTStorageAdapter;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlAdapter;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlFactory;
/*     */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*     */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*     */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherAdapter;
/*     */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherFactory;
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTester;
/*     */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterFactory;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler.RunStateChangeListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class DHTImpl
/*     */   implements DHT, AERunStateHandler.RunStateChangeListener
/*     */ {
/*     */   final DHTStorageAdapter storage_adapter;
/*     */   private DHTNATPuncherAdapter nat_adapter;
/*     */   private final DHTControl control;
/*     */   private DHTNATPuncher nat_puncher;
/*     */   private DHTSpeedTester speed_tester;
/*     */   private final Properties properties;
/*     */   private final DHTLogger logger;
/*  63 */   private final CopyOnWriteList<DHTListener> listeners = new CopyOnWriteList();
/*     */   
/*  65 */   private boolean runstate_startup = true;
/*  66 */   private boolean sleeping = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTImpl(DHTTransport _transport, Properties _properties, DHTStorageAdapter _storage_adapter, DHTNATPuncherAdapter _nat_adapter, DHTLogger _logger)
/*     */   {
/*  76 */     this.properties = _properties;
/*  77 */     this.storage_adapter = _storage_adapter;
/*  78 */     this.nat_adapter = _nat_adapter;
/*  79 */     this.logger = _logger;
/*     */     
/*  81 */     DHTNetworkPositionManager.initialise(this.storage_adapter);
/*     */     
/*  83 */     DHTLog.setLogger(this.logger);
/*     */     
/*  85 */     int K = getProp("EntriesPerNode", 20);
/*  86 */     int B = getProp("NodeSplitFactor", 4);
/*  87 */     int max_r = getProp("ReplacementsPerNode", 5);
/*  88 */     int s_conc = getProp("SearchConcurrency", 5);
/*  89 */     int l_conc = getProp("LookupConcurrency", 10);
/*  90 */     int o_rep = getProp("OriginalRepublishInterval", 28800000);
/*  91 */     int c_rep = getProp("CacheRepublishInterval", 1800000);
/*  92 */     int c_n = getProp("CacheClosestN", 1);
/*  93 */     boolean e_c = getProp("EncodeKeys", 1) == 1;
/*  94 */     boolean r_p = getProp("EnableRandomLookup", 1) == 1;
/*     */     
/*  96 */     this.control = DHTControlFactory.create(new DHTControlAdapter()
/*     */     {
/*     */ 
/*     */       public DHTStorageAdapter getStorageAdapter()
/*     */       {
/*     */ 
/* 102 */         return DHTImpl.this.storage_adapter;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean isDiversified(byte[] key)
/*     */       {
/* 109 */         if (DHTImpl.this.storage_adapter == null)
/*     */         {
/* 111 */           return false;
/*     */         }
/*     */         
/* 114 */         return DHTImpl.this.storage_adapter.isDiversified(key);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public byte[][] diversify(String description, DHTTransportContact cause, boolean put_operation, boolean existing, byte[] key, byte type, boolean exhaustive, int max_depth)
/*     */       {
/*     */         boolean valid;
/*     */         
/*     */ 
/*     */ 
/*     */         boolean valid;
/*     */         
/*     */ 
/*     */ 
/* 130 */         if (existing)
/*     */         {
/* 132 */           valid = (type == 2) || (type == 3) || (type == 1);
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 137 */           valid = (type == 2) || (type == 3);
/*     */         }
/*     */         
/*     */ 
/* 141 */         if ((DHTImpl.this.storage_adapter != null) && (valid))
/*     */         {
/* 143 */           if (existing)
/*     */           {
/* 145 */             return DHTImpl.this.storage_adapter.getExistingDiversification(key, put_operation, exhaustive, max_depth);
/*     */           }
/*     */           
/*     */ 
/* 149 */           return DHTImpl.this.storage_adapter.createNewDiversification(description, cause, key, put_operation, type, exhaustive, max_depth);
/*     */         }
/*     */         
/*     */ 
/* 153 */         if (!valid)
/*     */         {
/* 155 */           Debug.out("Invalid diversification received: type = " + type);
/*     */         }
/*     */         
/* 158 */         if (existing)
/*     */         {
/* 160 */           return new byte[][] { key };
/*     */         }
/*     */         
/*     */ 
/* 164 */         return new byte[0][]; } }, _transport, K, B, max_r, s_conc, l_conc, o_rep, c_rep, c_n, e_c, r_p, this.logger);
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
/* 175 */     if (this.nat_adapter != null)
/*     */     {
/* 177 */       this.nat_puncher = DHTNATPuncherFactory.create(this.nat_adapter, this);
/*     */     }
/*     */     
/* 180 */     AERunStateHandler.addListener(this, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTImpl(DHTTransport _transport, DHTRouter _router, DHTDB _database, Properties _properties, DHTStorageAdapter _storage_adapter, DHTLogger _logger)
/*     */   {
/* 192 */     this.properties = _properties;
/* 193 */     this.storage_adapter = _storage_adapter;
/* 194 */     this.logger = _logger;
/*     */     
/* 196 */     DHTNetworkPositionManager.initialise(this.storage_adapter);
/*     */     
/* 198 */     DHTLog.setLogger(this.logger);
/*     */     
/* 200 */     int K = getProp("EntriesPerNode", 20);
/* 201 */     int B = getProp("NodeSplitFactor", 4);
/* 202 */     int max_r = getProp("ReplacementsPerNode", 5);
/* 203 */     int s_conc = getProp("SearchConcurrency", 5);
/* 204 */     int l_conc = getProp("LookupConcurrency", 10);
/* 205 */     int o_rep = getProp("OriginalRepublishInterval", 28800000);
/* 206 */     int c_rep = getProp("CacheRepublishInterval", 1800000);
/* 207 */     int c_n = getProp("CacheClosestN", 1);
/* 208 */     boolean e_c = getProp("EncodeKeys", 1) == 1;
/* 209 */     boolean r_p = getProp("EnableRandomLookup", 1) == 1;
/*     */     
/* 211 */     this.control = DHTControlFactory.create(new DHTControlAdapter()
/*     */     {
/*     */ 
/*     */       public DHTStorageAdapter getStorageAdapter()
/*     */       {
/*     */ 
/* 217 */         return DHTImpl.this.storage_adapter;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean isDiversified(byte[] key)
/*     */       {
/* 224 */         if (DHTImpl.this.storage_adapter == null)
/*     */         {
/* 226 */           return false;
/*     */         }
/*     */         
/* 229 */         return DHTImpl.this.storage_adapter.isDiversified(key);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public byte[][] diversify(String description, DHTTransportContact cause, boolean put_operation, boolean existing, byte[] key, byte type, boolean exhaustive, int max_depth)
/*     */       {
/*     */         boolean valid;
/*     */         
/*     */ 
/*     */ 
/*     */         boolean valid;
/*     */         
/*     */ 
/*     */ 
/* 245 */         if (existing)
/*     */         {
/* 247 */           valid = (type == 2) || (type == 3) || (type == 1);
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 252 */           valid = (type == 2) || (type == 3);
/*     */         }
/*     */         
/*     */ 
/* 256 */         if ((DHTImpl.this.storage_adapter != null) && (valid))
/*     */         {
/* 258 */           if (existing)
/*     */           {
/* 260 */             return DHTImpl.this.storage_adapter.getExistingDiversification(key, put_operation, exhaustive, max_depth);
/*     */           }
/*     */           
/*     */ 
/* 264 */           return DHTImpl.this.storage_adapter.createNewDiversification(description, cause, key, put_operation, type, exhaustive, max_depth);
/*     */         }
/*     */         
/*     */ 
/* 268 */         if (!valid)
/*     */         {
/* 270 */           Debug.out("Invalid diversification received: type = " + type);
/*     */         }
/*     */         
/* 273 */         if (existing)
/*     */         {
/* 275 */           return new byte[][] { key };
/*     */         }
/*     */         
/*     */ 
/* 279 */         return new byte[0][]; } }, _transport, _router, _database, K, B, max_r, s_conc, l_conc, o_rep, c_rep, c_n, e_c, r_p, this.logger);
/*     */   }
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
/*     */   public void runStateChanged(long run_state)
/*     */   {
/*     */     try
/*     */     {
/* 298 */       boolean is_sleeping = AERunStateHandler.isDHTSleeping();
/*     */       
/* 300 */       if (this.sleeping != is_sleeping)
/*     */       {
/* 302 */         this.sleeping = is_sleeping;
/*     */         
/* 304 */         if (!this.runstate_startup)
/*     */         {
/* 306 */           System.out.println("DHT sleeping=" + this.sleeping);
/*     */         }
/*     */       }
/*     */       
/* 310 */       this.control.setSleeping(this.sleeping);
/*     */       
/* 312 */       DHTSpeedTester old_tester = null;
/* 313 */       new_tester = null;
/*     */       
/* 315 */       synchronized (this)
/*     */       {
/* 317 */         if (this.sleeping)
/*     */         {
/* 319 */           if (this.speed_tester != null)
/*     */           {
/* 321 */             old_tester = this.speed_tester;
/*     */             
/* 323 */             this.speed_tester = null;
/*     */           }
/*     */         }
/*     */         else {
/* 327 */           new_tester = this.speed_tester = DHTSpeedTesterFactory.create(this);
/*     */         }
/*     */       }
/*     */       
/* 331 */       if (old_tester != null)
/*     */       {
/* 333 */         if (!this.runstate_startup)
/*     */         {
/* 335 */           System.out.println("    destroying speed tester");
/*     */         }
/*     */         
/* 338 */         old_tester.destroy();
/*     */       }
/*     */       
/* 341 */       if (new_tester != null)
/*     */       {
/* 343 */         if (!this.runstate_startup)
/*     */         {
/* 345 */           System.out.println("    creating speed tester");
/*     */         }
/*     */         
/* 348 */         for (DHTListener l : this.listeners) {
/*     */           try
/*     */           {
/* 351 */             l.speedTesterAvailable(new_tester);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 355 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally {
/*     */       DHTSpeedTester new_tester;
/* 361 */       this.runstate_startup = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSleeping()
/*     */   {
/* 368 */     return this.sleeping;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSuspended(boolean susp)
/*     */   {
/* 375 */     if (susp)
/*     */     {
/* 377 */       if (this.nat_puncher != null)
/*     */       {
/* 379 */         this.nat_puncher.setSuspended(true);
/*     */       }
/*     */       
/* 382 */       this.control.setSuspended(true);
/*     */     }
/*     */     else
/*     */     {
/* 386 */       this.control.setSuspended(false);
/*     */       
/* 388 */       if (this.nat_puncher != null)
/*     */       {
/* 390 */         this.nat_puncher.setSuspended(false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getProp(String name, int def)
/*     */   {
/* 400 */     Integer x = (Integer)this.properties.get(name);
/*     */     
/* 402 */     if (x == null)
/*     */     {
/* 404 */       this.properties.put(name, new Integer(def));
/*     */       
/* 406 */       return def;
/*     */     }
/*     */     
/* 409 */     return x.intValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getIntProperty(String name)
/*     */   {
/* 416 */     return ((Integer)this.properties.get(name)).intValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDiversified(byte[] key)
/*     */   {
/* 423 */     return this.control.isDiversified(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(byte[] key, String description, byte[] value, short flags, DHTOperationListener listener)
/*     */   {
/* 434 */     this.control.put(key, description, value, flags, (byte)0, (byte)-1, true, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(byte[] key, String description, byte[] value, short flags, boolean high_priority, DHTOperationListener listener)
/*     */   {
/* 446 */     this.control.put(key, description, value, flags, (byte)0, (byte)-1, high_priority, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(byte[] key, String description, byte[] value, short flags, byte life_hours, boolean high_priority, DHTOperationListener listener)
/*     */   {
/* 459 */     this.control.put(key, description, value, flags, life_hours, (byte)-1, high_priority, listener);
/*     */   }
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
/*     */   public void put(byte[] key, String description, byte[] value, short flags, byte life_hours, byte replication_control, boolean high_priority, DHTOperationListener listener)
/*     */   {
/* 473 */     this.control.put(key, description, value, flags, life_hours, replication_control, high_priority, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTTransportValue getLocalValue(byte[] key)
/*     */   {
/* 480 */     return this.control.getLocalValue(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<DHTTransportValue> getStoredValues(byte[] key)
/*     */   {
/* 487 */     return this.control.getStoredValues(key);
/*     */   }
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
/*     */   public void get(byte[] key, String description, short flags, int max_values, long timeout, boolean exhaustive, boolean high_priority, DHTOperationListener listener)
/*     */   {
/* 501 */     this.control.get(key, description, flags, max_values, timeout, exhaustive, high_priority, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] remove(byte[] key, String description, DHTOperationListener listener)
/*     */   {
/* 510 */     return this.control.remove(key, description, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] remove(DHTTransportContact[] contacts, byte[] key, String description, DHTOperationListener listener)
/*     */   {
/* 520 */     return this.control.remove(contacts, key, description, listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransport getTransport()
/*     */   {
/* 526 */     return this.control.getTransport();
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTRouter getRouter()
/*     */   {
/* 532 */     return this.control.getRouter();
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTControl getControl()
/*     */   {
/* 538 */     return this.control;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTDB getDataBase()
/*     */   {
/* 544 */     return this.control.getDataBase();
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTNATPuncher getNATPuncher()
/*     */   {
/* 550 */     return this.nat_puncher;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTSpeedTester getSpeedTester()
/*     */   {
/* 556 */     return this.speed_tester;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTStorageAdapter getStorageAdapter()
/*     */   {
/* 562 */     return this.storage_adapter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void integrate(boolean full_wait)
/*     */   {
/* 569 */     this.control.seed(full_wait);
/*     */     
/* 571 */     if (this.nat_puncher != null)
/*     */     {
/* 573 */       this.nat_puncher.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 580 */     if (this.nat_puncher != null)
/*     */     {
/* 582 */       this.nat_puncher.destroy();
/*     */     }
/*     */     
/* 585 */     DHTNetworkPositionManager.destroy(this.storage_adapter);
/*     */     
/* 587 */     AERunStateHandler.removeListener(this);
/*     */     
/* 589 */     if (this.control != null)
/*     */     {
/* 591 */       this.control.destroy();
/*     */     }
/*     */     
/* 594 */     if (this.speed_tester != null)
/*     */     {
/* 596 */       this.speed_tester.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void exportState(DataOutputStream os, int max)
/*     */     throws IOException
/*     */   {
/* 607 */     this.control.exportState(os, max);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void importState(DataInputStream is)
/*     */     throws IOException
/*     */   {
/* 616 */     this.control.importState(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLogging(boolean on)
/*     */   {
/* 623 */     DHTLog.setLogging(on);
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTLogger getLogger()
/*     */   {
/* 629 */     return this.logger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void print(boolean full)
/*     */   {
/* 636 */     this.control.print(full);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(DHTListener listener)
/*     */   {
/* 643 */     this.listeners.add(listener);
/*     */     
/* 645 */     DHTSpeedTester st = this.speed_tester;
/*     */     
/* 647 */     if (st != null)
/*     */     {
/* 649 */       listener.speedTesterAvailable(st);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(DHTListener listener)
/*     */   {
/* 657 */     this.listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/impl/DHTImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */