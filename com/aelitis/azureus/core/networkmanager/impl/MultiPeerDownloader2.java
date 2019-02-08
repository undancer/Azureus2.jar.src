/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*     */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportBase;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class MultiPeerDownloader2
/*     */   implements RateControlledEntity
/*     */ {
/*     */   private static final int MOVE_TO_IDLE_TIME = 500;
/*  41 */   private static final Object ADD_ACTION = new Object();
/*  42 */   private static final Object REMOVE_ACTION = new Object();
/*     */   
/*  44 */   private volatile ArrayList connections_cow = new ArrayList();
/*  45 */   private final AEMonitor connections_mon = new AEMonitor("MultiPeerDownloader");
/*     */   
/*     */   private final RateHandler main_handler;
/*     */   
/*     */   private List pending_actions;
/*  50 */   private final connectionList active_connections = new connectionList();
/*  51 */   private final connectionList idle_connections = new connectionList();
/*     */   
/*     */ 
/*     */ 
/*     */   private long last_idle_check;
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile EventWaiter waiter;
/*     */   
/*     */ 
/*     */ 
/*     */   public MultiPeerDownloader2(RateHandler _main_handler)
/*     */   {
/*  65 */     this.main_handler = _main_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   public RateHandler getRateHandler()
/*     */   {
/*  71 */     return this.main_handler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPeerConnection(NetworkConnectionBase connection)
/*     */   {
/*  79 */     EventWaiter waiter_to_kick = null;
/*     */     try {
/*  81 */       this.connections_mon.enter();
/*     */       
/*  83 */       int cow_size = this.connections_cow.size();
/*  84 */       if (cow_size == 0) {
/*  85 */         waiter_to_kick = this.waiter;
/*  86 */         if (waiter_to_kick != null) {
/*  87 */           this.waiter = null;
/*     */         }
/*     */       }
/*  90 */       ArrayList conn_new = new ArrayList(cow_size + 1);
/*  91 */       conn_new.addAll(this.connections_cow);
/*  92 */       conn_new.add(connection);
/*  93 */       this.connections_cow = conn_new;
/*     */       
/*  95 */       if (this.pending_actions == null)
/*     */       {
/*  97 */         this.pending_actions = new ArrayList();
/*     */       }
/*     */       
/* 100 */       this.pending_actions.add(new Object[] { ADD_ACTION, connection });
/*     */     } finally {
/* 102 */       this.connections_mon.exit();
/*     */     }
/*     */     
/* 105 */     if (waiter_to_kick != null)
/*     */     {
/* 107 */       waiter_to_kick.eventOccurred();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean removePeerConnection(NetworkConnectionBase connection)
/*     */   {
/*     */     try
/*     */     {
/* 119 */       this.connections_mon.enter();
/*     */       
/* 121 */       ArrayList conn_new = new ArrayList(this.connections_cow);
/* 122 */       boolean removed = conn_new.remove(connection);
/* 123 */       boolean bool1; if (!removed) return false;
/* 124 */       this.connections_cow = conn_new;
/*     */       
/* 126 */       if (this.pending_actions == null)
/*     */       {
/* 128 */         this.pending_actions = new ArrayList();
/*     */       }
/*     */       
/* 131 */       this.pending_actions.add(new Object[] { REMOVE_ACTION, connection });
/* 132 */       return true;
/*     */     } finally {
/* 134 */       this.connections_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canProcess(EventWaiter waiter)
/*     */   {
/* 145 */     int[] allowed = this.main_handler.getCurrentNumBytesAllowed();
/*     */     
/* 147 */     if (allowed[0] < 1)
/*     */     {
/* 149 */       return false;
/*     */     }
/*     */     
/* 152 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesReadyToWrite()
/*     */   {
/* 158 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectionCount(EventWaiter _waiter)
/*     */   {
/* 164 */     int result = this.connections_cow.size();
/*     */     
/* 166 */     if (result == 0)
/*     */     {
/* 168 */       this.waiter = _waiter;
/*     */     }
/*     */     
/* 171 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getReadyConnectionCount(EventWaiter waiter)
/*     */   {
/* 178 */     int res = 0;
/*     */     
/* 180 */     for (Iterator it = this.connections_cow.iterator(); it.hasNext();)
/*     */     {
/* 182 */       NetworkConnectionBase connection = (NetworkConnectionBase)it.next();
/*     */       
/* 184 */       if (connection.getTransportBase().isReadyForRead(waiter) == 0L)
/*     */       {
/* 186 */         res++;
/*     */       }
/*     */     }
/*     */     
/* 190 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int doProcessing(EventWaiter waiter, int max_bytes)
/*     */   {
/* 202 */     int[] bytes_allowed = this.main_handler.getCurrentNumBytesAllowed();
/*     */     
/* 204 */     int num_bytes_allowed = bytes_allowed[0];
/*     */     
/* 206 */     boolean protocol_is_free = bytes_allowed[1] > 0;
/*     */     
/* 208 */     if (num_bytes_allowed < 1)
/*     */     {
/* 210 */       return 0;
/*     */     }
/*     */     
/* 213 */     if ((max_bytes > 0) && (max_bytes < num_bytes_allowed))
/*     */     {
/* 215 */       num_bytes_allowed = max_bytes;
/*     */     }
/*     */     
/* 218 */     if (this.pending_actions != null) {
/*     */       try
/*     */       {
/* 221 */         this.connections_mon.enter();
/*     */         
/* 223 */         for (int i = 0; i < this.pending_actions.size(); i++)
/*     */         {
/* 225 */           Object[] entry = (Object[])this.pending_actions.get(i);
/*     */           
/* 227 */           NetworkConnectionBase connection = (NetworkConnectionBase)entry[1];
/*     */           
/* 229 */           if (entry[0] == ADD_ACTION)
/*     */           {
/* 231 */             this.active_connections.add(connection);
/*     */           }
/*     */           else
/*     */           {
/* 235 */             this.active_connections.remove(connection);
/*     */             
/* 237 */             this.idle_connections.remove(connection);
/*     */           }
/*     */         }
/*     */         
/* 241 */         this.pending_actions = null;
/*     */       }
/*     */       finally
/*     */       {
/* 245 */         this.connections_mon.exit();
/*     */       }
/*     */     }
/*     */     
/* 249 */     long now = SystemTime.getSteppedMonotonousTime();
/*     */     
/* 251 */     if (now - this.last_idle_check > 500L)
/*     */     {
/* 253 */       this.last_idle_check = now;
/*     */       
/*     */ 
/*     */ 
/* 257 */       connectionEntry entry = this.idle_connections.head();
/*     */       
/* 259 */       while (entry != null)
/*     */       {
/* 261 */         NetworkConnectionBase connection = entry.connection;
/*     */         
/* 263 */         connectionEntry next = entry.next;
/*     */         
/* 265 */         if (connection.getTransportBase().isReadyForRead(waiter) == 0L)
/*     */         {
/*     */ 
/*     */ 
/* 269 */           this.idle_connections.remove(entry);
/*     */           
/* 271 */           this.active_connections.addToStart(entry);
/*     */         }
/*     */         
/* 274 */         entry = next;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 280 */     int num_bytes_remaining = num_bytes_allowed;
/*     */     
/* 282 */     int data_bytes_read = 0;
/* 283 */     int protocol_bytes_read = 0;
/*     */     
/* 285 */     connectionEntry entry = this.active_connections.head();
/*     */     
/* 287 */     int num_entries = this.active_connections.size();
/*     */     
/* 289 */     for (int i = 0; (i < num_entries) && (entry != null) && (num_bytes_remaining > 0); i++)
/*     */     {
/* 291 */       NetworkConnectionBase connection = entry.connection;
/*     */       
/* 293 */       connectionEntry next = entry.next;
/*     */       
/* 295 */       long ready = connection.getTransportBase().isReadyForRead(waiter);
/*     */       
/*     */ 
/*     */ 
/* 299 */       if (ready == 0L)
/*     */       {
/* 301 */         int mss = connection.getMssSize();
/*     */         
/* 303 */         int allowed = num_bytes_remaining > mss ? mss : num_bytes_remaining;
/*     */         
/* 305 */         int bytes_read = 0;
/*     */         try
/*     */         {
/* 308 */           int[] read = connection.getIncomingMessageQueue().receiveFromTransport(allowed, protocol_is_free);
/*     */           
/* 310 */           data_bytes_read += read[0];
/* 311 */           protocol_bytes_read += read[1];
/*     */           
/* 313 */           bytes_read = read[0] + read[1];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 334 */           if (!(e instanceof IOException))
/*     */           {
/*     */ 
/*     */ 
/* 338 */             if (!Debug.getNestedExceptionMessage(e).contains("Incorrect mix"))
/*     */             {
/* 340 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */           
/* 344 */           connection.notifyOfException(e);
/*     */         }
/*     */         
/* 347 */         num_bytes_remaining -= bytes_read;
/*     */         
/*     */ 
/*     */ 
/* 351 */         this.active_connections.moveToEnd(entry);
/*     */       }
/* 353 */       else if (ready > 500L)
/*     */       {
/*     */ 
/*     */ 
/* 357 */         this.active_connections.remove(entry);
/*     */         
/* 359 */         this.idle_connections.addToEnd(entry);
/*     */       }
/*     */       
/* 362 */       entry = next;
/*     */     }
/*     */     
/* 365 */     int total_bytes_read = num_bytes_allowed - num_bytes_remaining;
/*     */     
/* 367 */     if (total_bytes_read > 0)
/*     */     {
/* 369 */       this.main_handler.bytesProcessed(data_bytes_read, protocol_bytes_read);
/*     */       
/* 371 */       return total_bytes_read;
/*     */     }
/*     */     
/* 374 */     return 0;
/*     */   }
/*     */   
/*     */ 
/* 378 */   public int getPriority() { return 1; }
/*     */   
/* 380 */   public boolean getPriorityBoost() { return false; }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 385 */     StringBuilder str = new StringBuilder();
/*     */     
/* 387 */     str.append("MPD (").append(this.connections_cow.size()).append("/").append(this.active_connections.size()).append("/").append(this.idle_connections.size()).append(": ");
/*     */     
/*     */ 
/* 390 */     int num = 0;
/*     */     
/* 392 */     for (Iterator it = this.connections_cow.iterator(); it.hasNext();)
/*     */     {
/* 394 */       NetworkConnectionBase connection = (NetworkConnectionBase)it.next();
/*     */       
/* 396 */       if (num++ > 0)
/*     */       {
/* 398 */         str.append(",");
/*     */       }
/*     */       
/* 401 */       str.append(connection.getString());
/*     */     }
/*     */     
/* 404 */     return str.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class connectionList
/*     */   {
/*     */     private int size;
/*     */     
/*     */     private MultiPeerDownloader2.connectionEntry head;
/*     */     
/*     */     private MultiPeerDownloader2.connectionEntry tail;
/*     */     
/*     */ 
/*     */     protected MultiPeerDownloader2.connectionEntry add(NetworkConnectionBase connection)
/*     */     {
/* 419 */       MultiPeerDownloader2.connectionEntry entry = new MultiPeerDownloader2.connectionEntry(connection);
/*     */       
/* 421 */       if (this.head == null)
/*     */       {
/* 423 */         this.head = (this.tail = entry);
/*     */       }
/*     */       else
/*     */       {
/* 427 */         this.tail.next = entry;
/* 428 */         entry.prev = this.tail;
/*     */         
/* 430 */         this.tail = entry;
/*     */       }
/*     */       
/* 433 */       this.size += 1;
/*     */       
/* 435 */       return entry;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void addToEnd(MultiPeerDownloader2.connectionEntry entry)
/*     */     {
/* 442 */       entry.next = null;
/* 443 */       entry.prev = this.tail;
/*     */       
/* 445 */       if (this.tail == null)
/*     */       {
/* 447 */         this.head = (this.tail = entry);
/*     */       }
/*     */       else
/*     */       {
/* 451 */         this.tail.next = entry;
/* 452 */         this.tail = entry;
/*     */       }
/*     */       
/* 455 */       this.size += 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void addToStart(MultiPeerDownloader2.connectionEntry entry)
/*     */     {
/* 462 */       entry.next = this.head;
/* 463 */       entry.prev = null;
/*     */       
/* 465 */       if (this.head == null)
/*     */       {
/* 467 */         this.head = (this.tail = entry);
/*     */       }
/*     */       else
/*     */       {
/* 471 */         this.head.prev = entry;
/* 472 */         this.head = entry;
/*     */       }
/*     */       
/* 475 */       this.size += 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void moveToEnd(MultiPeerDownloader2.connectionEntry entry)
/*     */     {
/* 482 */       if (entry != this.tail)
/*     */       {
/* 484 */         MultiPeerDownloader2.connectionEntry prev = entry.prev;
/* 485 */         MultiPeerDownloader2.connectionEntry next = entry.next;
/*     */         
/* 487 */         if (prev == null)
/*     */         {
/* 489 */           this.head = next;
/*     */         }
/*     */         else
/*     */         {
/* 493 */           prev.next = next;
/*     */         }
/*     */         
/* 496 */         next.prev = prev;
/*     */         
/* 498 */         entry.prev = this.tail;
/* 499 */         entry.next = null;
/*     */         
/* 501 */         this.tail.next = entry;
/* 502 */         this.tail = entry;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected MultiPeerDownloader2.connectionEntry remove(NetworkConnectionBase connection)
/*     */     {
/* 510 */       MultiPeerDownloader2.connectionEntry entry = this.head;
/*     */       
/* 512 */       while (entry != null)
/*     */       {
/* 514 */         if (entry.connection == connection)
/*     */         {
/* 516 */           remove(entry);
/*     */           
/* 518 */           return entry;
/*     */         }
/*     */         
/*     */ 
/* 522 */         entry = entry.next;
/*     */       }
/*     */       
/*     */ 
/* 526 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void remove(MultiPeerDownloader2.connectionEntry entry)
/*     */     {
/* 533 */       MultiPeerDownloader2.connectionEntry prev = entry.prev;
/* 534 */       MultiPeerDownloader2.connectionEntry next = entry.next;
/*     */       
/* 536 */       if (prev == null)
/*     */       {
/* 538 */         this.head = next;
/*     */       }
/*     */       else
/*     */       {
/* 542 */         prev.next = next;
/*     */       }
/*     */       
/* 545 */       if (next == null)
/*     */       {
/* 547 */         this.tail = prev;
/*     */       }
/*     */       else
/*     */       {
/* 551 */         next.prev = prev;
/*     */       }
/*     */       
/* 554 */       this.size -= 1;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int size()
/*     */     {
/* 560 */       return this.size;
/*     */     }
/*     */     
/*     */ 
/*     */     protected MultiPeerDownloader2.connectionEntry head()
/*     */     {
/* 566 */       return this.head;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class connectionEntry
/*     */   {
/*     */     private connectionEntry next;
/*     */     
/*     */     private connectionEntry prev;
/*     */     
/*     */     final NetworkConnectionBase connection;
/*     */     
/*     */ 
/*     */     protected connectionEntry(NetworkConnectionBase _connection)
/*     */     {
/* 582 */       this.connection = _connection;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/MultiPeerDownloader2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */