/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportBase;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ public class MultiPeerUploader
/*     */   implements RateControlledEntity
/*     */ {
/*     */   private static final int FLUSH_CHECK_LOOP_TIME = 500;
/*     */   private static final int FLUSH_WAIT_TIME = 3000;
/*  42 */   private long last_flush_check_time = 0L;
/*     */   
/*     */   private final RateHandler rate_handler;
/*     */   
/*  46 */   private boolean destroyed = false;
/*     */   
/*  48 */   private final HashMap waiting_connections = new HashMap();
/*  49 */   private final LinkedList ready_connections = new LinkedList();
/*  50 */   private final AEMonitor lists_lock = new AEMonitor("PacketFillingMultiPeerUploader:lists_lock");
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile EventWaiter waiter;
/*     */   
/*     */ 
/*     */ 
/*     */   public MultiPeerUploader(RateHandler rate_handler)
/*     */   {
/*  60 */     this.rate_handler = rate_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   public RateHandler getRateHandler()
/*     */   {
/*  66 */     return this.rate_handler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void flushCheck()
/*     */   {
/*  73 */     long diff = SystemTime.getCurrentTime() - this.last_flush_check_time;
/*     */     
/*  75 */     if ((!this.destroyed) && ((diff > 500L) || (diff < 0L))) {
/*  76 */       try { this.lists_lock.enter();
/*  77 */         current_time = SystemTime.getCurrentTime();
/*     */         
/*  79 */         for (i = this.waiting_connections.entrySet().iterator(); i.hasNext();) {
/*  80 */           Map.Entry entry = (Map.Entry)i.next();
/*  81 */           PeerData peer_data = (PeerData)entry.getValue();
/*     */           
/*  83 */           long wait_time = current_time - peer_data.last_message_added_time;
/*     */           
/*  85 */           if ((wait_time > 3000L) || (wait_time < 0L))
/*     */           {
/*  87 */             NetworkConnectionBase conn = (NetworkConnectionBase)entry.getKey();
/*     */             
/*  89 */             if (conn.getOutgoingMessageQueue().getTotalSize() > 0) {
/*  90 */               conn.getOutgoingMessageQueue().cancelQueueListener(peer_data.queue_listener);
/*  91 */               i.remove();
/*  92 */               addToReadyList(conn);
/*     */             }
/*     */             else
/*     */             {
/*  96 */               peer_data.last_message_added_time = current_time;
/*     */             }
/*     */           }
/*     */         } } finally { long current_time;
/*     */         Iterator i;
/* 101 */         this.lists_lock.exit();
/*     */       }
/* 103 */       this.last_flush_check_time = SystemTime.getCurrentTime();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 114 */     this.destroyed = true;
/*     */     try
/*     */     {
/* 117 */       this.lists_lock.enter();
/*     */       
/*     */ 
/* 120 */       for (Iterator i = this.waiting_connections.entrySet().iterator(); i.hasNext();) {
/* 121 */         Map.Entry entry = (Map.Entry)i.next();
/* 122 */         NetworkConnectionBase conn = (NetworkConnectionBase)entry.getKey();
/* 123 */         PeerData data = (PeerData)entry.getValue();
/* 124 */         conn.getOutgoingMessageQueue().cancelQueueListener(data.queue_listener);
/*     */       }
/* 126 */       this.waiting_connections.clear();
/*     */       
/*     */ 
/* 129 */       this.ready_connections.clear();
/*     */     }
/*     */     finally {
/* 132 */       this.lists_lock.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPeerConnection(NetworkConnectionBase peer_connection)
/*     */   {
/* 144 */     int mss_size = peer_connection.getMssSize();
/* 145 */     boolean has_urgent_data = peer_connection.getOutgoingMessageQueue().hasUrgentMessage();
/* 146 */     int num_bytes_ready = peer_connection.getOutgoingMessageQueue().getTotalSize();
/*     */     
/* 148 */     if ((num_bytes_ready >= mss_size) || (has_urgent_data)) {
/* 149 */       addToReadyList(peer_connection);
/*     */     }
/*     */     else {
/* 152 */       addToWaitingList(peer_connection);
/*     */     }
/*     */     
/* 155 */     EventWaiter waiter_to_kick = this.waiter;
/*     */     
/* 157 */     if (waiter_to_kick != null)
/*     */     {
/* 159 */       this.waiter = null;
/*     */       
/* 161 */       waiter_to_kick.eventOccurred();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean removePeerConnection(NetworkConnectionBase peer_connection)
/*     */   {
/*     */     try
/*     */     {
/* 173 */       this.lists_lock.enter();
/*     */       
/*     */ 
/* 176 */       PeerData peer_data = (PeerData)this.waiting_connections.remove(peer_connection);
/* 177 */       boolean bool; if (peer_data != null) {
/* 178 */         peer_connection.getOutgoingMessageQueue().cancelQueueListener(peer_data.queue_listener);
/* 179 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 183 */       if (this.ready_connections.remove(peer_connection)) {
/* 184 */         return true;
/*     */       }
/*     */       
/* 187 */       return false;
/*     */     }
/*     */     finally {
/* 190 */       this.lists_lock.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addToWaitingList(final NetworkConnectionBase conn)
/*     */   {
/* 199 */     final PeerData peer_data = new PeerData(null);
/*     */     
/* 201 */     OutgoingMessageQueue.MessageQueueListener listener = new OutgoingMessageQueue.MessageQueueListener() {
/* 202 */       public boolean messageAdded(Message message) { return true; }
/*     */       
/*     */       public void messageQueued(Message message) {
/*     */         try {
/* 206 */           MultiPeerUploader.this.lists_lock.enter();
/*     */           
/* 208 */           if (MultiPeerUploader.this.waiting_connections.get(conn) == null) {
/*     */             return;
/*     */           }
/*     */           
/* 212 */           int mss_size = conn.getMssSize();
/* 213 */           boolean has_urgent_data = conn.getOutgoingMessageQueue().hasUrgentMessage();
/* 214 */           int num_bytes_ready = conn.getOutgoingMessageQueue().getTotalSize();
/*     */           
/* 216 */           if ((num_bytes_ready >= mss_size) || (has_urgent_data)) {
/* 217 */             MultiPeerUploader.this.waiting_connections.remove(conn);
/* 218 */             conn.getOutgoingMessageQueue().cancelQueueListener(this);
/* 219 */             MultiPeerUploader.this.addToReadyList(conn);
/*     */ 
/*     */           }
/* 222 */           else if (!MultiPeerUploader.PeerData.access$600(peer_data))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 228 */             MultiPeerUploader.PeerData.access$602(peer_data, true);
/* 229 */             MultiPeerUploader.PeerData.access$002(peer_data, SystemTime.getCurrentTime());
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 234 */           MultiPeerUploader.this.lists_lock.exit();
/*     */         }
/*     */       }
/*     */       
/*     */       public void flush()
/*     */       {
/*     */         try
/*     */         {
/* 242 */           MultiPeerUploader.this.lists_lock.enter();
/*     */           
/* 244 */           if (MultiPeerUploader.this.waiting_connections.remove(conn) != null)
/*     */           {
/* 246 */             conn.getOutgoingMessageQueue().cancelQueueListener(this);
/*     */             
/* 248 */             MultiPeerUploader.this.addToReadyList(conn);
/*     */           }
/*     */         }
/*     */         finally {
/* 252 */           MultiPeerUploader.this.lists_lock.exit();
/*     */         } }
/*     */       
/*     */       public void messageRemoved(Message message) {}
/*     */       
/*     */       public void messageSent(Message message) {}
/*     */       
/*     */       public void protocolBytesSent(int byte_count) {}
/*     */       
/* 261 */       public void dataBytesSent(int byte_count) {} };
/* 262 */     peer_data.queue_listener = listener;
/* 263 */     peer_data.last_message_added_time = SystemTime.getCurrentTime();
/* 264 */     peer_data.bumped = false;
/*     */     try
/*     */     {
/* 267 */       this.lists_lock.enter();
/*     */       
/* 269 */       this.waiting_connections.put(conn, peer_data);
/* 270 */       conn.getOutgoingMessageQueue().registerQueueListener(listener);
/*     */     }
/*     */     finally {
/* 273 */       this.lists_lock.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void addToReadyList(NetworkConnectionBase conn)
/*     */   {
/*     */     try
/*     */     {
/* 282 */       this.lists_lock.enter();
/*     */       
/* 284 */       this.ready_connections.addLast(conn);
/*     */     }
/*     */     finally {
/* 287 */       this.lists_lock.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private int write(EventWaiter waiter, int num_bytes_to_write, boolean protocol_is_free)
/*     */   {
/* 294 */     if (num_bytes_to_write < 1)
/*     */     {
/* 296 */       if (!protocol_is_free)
/*     */       {
/* 298 */         Debug.out("num_bytes_to_write < 1");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 303 */       return 0;
/*     */     }
/*     */     
/* 306 */     HashMap connections_to_notify_of_exception = new HashMap();
/* 307 */     ArrayList manual_notifications = new ArrayList();
/*     */     
/* 309 */     int num_bytes_remaining = num_bytes_to_write;
/*     */     
/* 311 */     int data_bytes_written = 0;
/* 312 */     int protocol_bytes_written = 0;
/*     */     try
/*     */     {
/* 315 */       this.lists_lock.enter();
/*     */       
/* 317 */       int num_unusable_connections = 0;
/*     */       
/* 319 */       while ((num_bytes_remaining > 0) && (num_unusable_connections < this.ready_connections.size())) {
/* 320 */         NetworkConnectionBase conn = (NetworkConnectionBase)this.ready_connections.removeFirst();
/*     */         
/* 322 */         if (!conn.getTransportBase().isReadyForWrite(waiter)) {
/* 323 */           this.ready_connections.addLast(conn);
/* 324 */           num_unusable_connections++;
/*     */         }
/*     */         else
/*     */         {
/* 328 */           int total_size = conn.getOutgoingMessageQueue().getTotalSize();
/*     */           
/* 330 */           if (total_size < 1) {
/* 331 */             addToWaitingList(conn);
/*     */           }
/*     */           else
/*     */           {
/* 335 */             int mss_size = conn.getMssSize();
/* 336 */             int num_bytes_allowed = num_bytes_remaining > mss_size ? mss_size : num_bytes_remaining;
/* 337 */             int num_bytes_available = total_size > mss_size ? mss_size : total_size;
/*     */             
/* 339 */             if (num_bytes_allowed >= num_bytes_available) {
/* 340 */               int written = 0;
/*     */               try {
/* 342 */                 int[] _written = conn.getOutgoingMessageQueue().deliverToTransport(num_bytes_available, protocol_is_free, true);
/*     */                 
/* 344 */                 data_bytes_written += _written[0];
/* 345 */                 protocol_bytes_written += _written[1];
/*     */                 
/* 347 */                 written = _written[0] + _written[1];
/*     */                 
/* 349 */                 if (written > 0) {
/* 350 */                   manual_notifications.add(conn);
/*     */                 }
/*     */                 
/* 353 */                 boolean has_urgent_data = conn.getOutgoingMessageQueue().hasUrgentMessage();
/* 354 */                 int remaining = conn.getOutgoingMessageQueue().getTotalSize();
/*     */                 
/* 356 */                 if ((remaining >= mss_size) || (has_urgent_data)) {
/* 357 */                   this.ready_connections.addLast(conn);
/* 358 */                   num_unusable_connections = 0;
/*     */                 }
/*     */                 else
/*     */                 {
/* 362 */                   addToWaitingList(conn);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 384 */                 if (!(e instanceof IOException))
/*     */                 {
/* 386 */                   Debug.printStackTrace(e);
/*     */                 }
/*     */                 
/* 389 */                 connections_to_notify_of_exception.put(conn, e);
/* 390 */                 addToWaitingList(conn);
/*     */               }
/*     */               
/* 393 */               num_bytes_remaining -= written;
/*     */             }
/*     */             else {
/* 396 */               this.ready_connections.addLast(conn);
/* 397 */               num_unusable_connections++;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 404 */       this.lists_lock.exit();
/*     */     }
/*     */     
/*     */ 
/* 408 */     for (int i = 0; i < manual_notifications.size(); i++) {
/* 409 */       NetworkConnectionBase conn = (NetworkConnectionBase)manual_notifications.get(i);
/* 410 */       conn.getOutgoingMessageQueue().doListenerNotifications();
/*     */     }
/*     */     
/*     */ 
/* 414 */     for (Iterator i = connections_to_notify_of_exception.entrySet().iterator(); i.hasNext();) {
/* 415 */       Map.Entry entry = (Map.Entry)i.next();
/* 416 */       NetworkConnectionBase conn = (NetworkConnectionBase)entry.getKey();
/* 417 */       Throwable exception = (Throwable)entry.getValue();
/* 418 */       conn.notifyOfException(exception);
/*     */     }
/*     */     
/* 421 */     int num_bytes_written = num_bytes_to_write - num_bytes_remaining;
/* 422 */     if (num_bytes_written > 0) {
/* 423 */       this.rate_handler.bytesProcessed(data_bytes_written, protocol_bytes_written);
/*     */     }
/*     */     
/* 426 */     return num_bytes_written;
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
/*     */   public long getBytesReadyToWrite()
/*     */   {
/* 453 */     long total = 0L;
/*     */     try
/*     */     {
/* 456 */       this.lists_lock.enter();
/*     */       
/* 458 */       for (Iterator i = this.waiting_connections.keySet().iterator(); i.hasNext();)
/*     */       {
/* 460 */         NetworkConnectionBase conn = (NetworkConnectionBase)i.next();
/*     */         
/* 462 */         total += conn.getOutgoingMessageQueue().getTotalSize();
/*     */       }
/*     */       
/* 465 */       for (i = this.ready_connections.iterator(); i.hasNext();)
/*     */       {
/* 467 */         NetworkConnectionBase conn = (NetworkConnectionBase)i.next();
/*     */         
/* 469 */         total += conn.getOutgoingMessageQueue().getTotalSize();
/*     */       }
/*     */     } finally {
/*     */       Iterator i;
/* 473 */       this.lists_lock.exit();
/*     */     }
/*     */     
/* 476 */     return total;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectionCount(EventWaiter _waiter)
/*     */   {
/* 482 */     int res = this.waiting_connections.size() + this.ready_connections.size();
/*     */     
/* 484 */     if (res == 0)
/*     */     {
/* 486 */       this.waiter = _waiter;
/*     */     }
/*     */     
/* 489 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getReadyConnectionCount(EventWaiter waiter)
/*     */   {
/* 496 */     int total = 0;
/*     */     try
/*     */     {
/* 499 */       this.lists_lock.enter();
/*     */       
/* 501 */       for (Iterator i = this.waiting_connections.keySet().iterator(); i.hasNext();)
/*     */       {
/* 503 */         NetworkConnectionBase conn = (NetworkConnectionBase)i.next();
/*     */         
/* 505 */         if (conn.getTransportBase().isReadyForWrite(waiter))
/*     */         {
/* 507 */           total++;
/*     */         }
/*     */       }
/*     */       
/* 511 */       for (i = this.ready_connections.iterator(); i.hasNext();)
/*     */       {
/* 513 */         NetworkConnectionBase conn = (NetworkConnectionBase)i.next();
/*     */         
/* 515 */         if (conn.getTransportBase().isReadyForWrite(waiter))
/*     */         {
/* 517 */           total++;
/*     */         }
/*     */       }
/*     */     } finally {
/*     */       Iterator i;
/* 522 */       this.lists_lock.exit();
/*     */     }
/*     */     
/* 525 */     return total;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canProcess(EventWaiter waiter)
/*     */   {
/* 531 */     flushCheck();
/*     */     
/* 533 */     if (this.ready_connections.isEmpty()) { return false;
/*     */     }
/* 535 */     int[] allowed = this.rate_handler.getCurrentNumBytesAllowed();
/*     */     
/* 537 */     if ((allowed[0] < 1) && (allowed[1] == 0)) return false;
/* 538 */     return true;
/*     */   }
/*     */   
/*     */   public int doProcessing(EventWaiter waiter, int max_bytes)
/*     */   {
/* 543 */     int[] allowed = this.rate_handler.getCurrentNumBytesAllowed();
/*     */     
/* 545 */     int num_bytes_allowed = allowed[0];
/* 546 */     boolean protocol_is_free = allowed[1] > 0;
/*     */     
/* 548 */     if (num_bytes_allowed < 1) { return 0;
/*     */     }
/* 550 */     if ((max_bytes > 0) && (max_bytes < num_bytes_allowed)) {
/* 551 */       num_bytes_allowed = max_bytes;
/*     */     }
/*     */     
/* 554 */     return write(waiter, num_bytes_allowed, protocol_is_free);
/*     */   }
/*     */   
/*     */   public int getPriority() {
/* 558 */     return 1;
/*     */   }
/*     */   
/* 561 */   public boolean getPriorityBoost() { return false; }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 566 */     StringBuilder str = new StringBuilder();
/*     */     
/* 568 */     str.append("MPU (").append(this.waiting_connections.size()).append("/").append(this.ready_connections.size()).append("): ");
/*     */     
/* 570 */     str.append("can_process=").append(canProcess(null)).append(",bytes_allowed=").append(this.rate_handler.getCurrentNumBytesAllowed()).append(", waiting=");
/*     */     
/*     */     try
/*     */     {
/* 574 */       this.lists_lock.enter();
/*     */       
/* 576 */       num = 0;
/*     */       
/* 578 */       for (Iterator i = this.waiting_connections.keySet().iterator(); i.hasNext();)
/*     */       {
/* 580 */         NetworkConnectionBase conn = (NetworkConnectionBase)i.next();
/*     */         
/* 582 */         if (num++ > 0) {
/* 583 */           str.append(",");
/*     */         }
/*     */         
/* 586 */         str.append(conn.getString());
/*     */       }
/*     */       
/* 589 */       str.append(": ready=");
/*     */       
/* 591 */       num = 0;
/*     */       
/* 593 */       for (i = this.ready_connections.iterator(); i.hasNext();)
/*     */       {
/* 595 */         NetworkConnectionBase conn = (NetworkConnectionBase)i.next();
/*     */         
/* 597 */         if (num++ > 0) {
/* 598 */           str.append(",");
/*     */         }
/*     */         
/* 601 */         str.append(conn.getString());
/*     */       }
/*     */     } finally { int num;
/*     */       Iterator i;
/* 605 */       this.lists_lock.exit();
/*     */     }
/*     */     
/* 608 */     return str.toString();
/*     */   }
/*     */   
/*     */   private static class PeerData
/*     */   {
/*     */     private OutgoingMessageQueue.MessageQueueListener queue_listener;
/*     */     private long last_message_added_time;
/*     */     private boolean bumped;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/MultiPeerUploader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */