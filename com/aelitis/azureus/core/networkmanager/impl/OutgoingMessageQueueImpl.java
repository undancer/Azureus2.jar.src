/*      */ package com.aelitis.azureus.core.networkmanager.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*      */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*      */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*      */ import com.aelitis.azureus.core.networkmanager.Transport;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*      */ import java.io.IOException;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class OutgoingMessageQueueImpl
/*      */   implements OutgoingMessageQueue
/*      */ {
/*   45 */   private final LinkedList<RawMessage> queue = new LinkedList();
/*   46 */   private final AEMonitor queue_mon = new AEMonitor("OutgoingMessageQueue:queue");
/*      */   
/*   48 */   private final ArrayList delayed_notifications = new ArrayList();
/*   49 */   private final AEMonitor delayed_notifications_mon = new AEMonitor("OutgoingMessageQueue:DN");
/*      */   
/*   51 */   private volatile ArrayList listeners = new ArrayList();
/*   52 */   private final AEMonitor listeners_mon = new AEMonitor("OutgoingMessageQueue:L");
/*      */   
/*   54 */   private int total_size = 0;
/*   55 */   private int total_data_size = 0;
/*   56 */   private boolean priority_boost = false;
/*   57 */   private RawMessage urgent_message = null;
/*   58 */   private boolean destroyed = false;
/*      */   
/*      */   private MessageStreamEncoder stream_encoder;
/*      */   
/*      */   private Transport transport;
/*   63 */   private int percent_complete = -1;
/*      */   
/*      */   private static final boolean TRACE_HISTORY = false;
/*      */   private static final int MAX_HISTORY_TRACES = 30;
/*   67 */   private final LinkedList<RawMessage> prev_sent = new LinkedList();
/*      */   
/*      */ 
/*      */   private boolean trace;
/*      */   
/*      */ 
/*      */ 
/*      */   public OutgoingMessageQueueImpl(MessageStreamEncoder stream_encoder)
/*      */   {
/*   76 */     this.stream_encoder = stream_encoder;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTransport(Transport _transport)
/*      */   {
/*   83 */     this.transport = _transport;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMssSize()
/*      */   {
/*   89 */     return this.transport == null ? NetworkManager.getMinMssSize() : this.transport.getMssSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEncoder(MessageStreamEncoder stream_encoder)
/*      */   {
/*   97 */     this.stream_encoder = stream_encoder;
/*      */   }
/*      */   
/*      */ 
/*      */   public MessageStreamEncoder getEncoder()
/*      */   {
/*  103 */     return this.stream_encoder;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPercentDoneOfCurrentMessage()
/*      */   {
/*  111 */     return this.percent_complete;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void destroy()
/*      */   {
/*  119 */     this.destroyed = true;
/*      */     try {
/*  121 */       this.queue_mon.enter();
/*      */       
/*  123 */       while (!this.queue.isEmpty()) {
/*  124 */         ((RawMessage)this.queue.remove(0)).destroy();
/*      */       }
/*      */     } finally {
/*  127 */       this.queue_mon.exit();
/*      */     }
/*  129 */     this.total_size = 0;
/*  130 */     this.total_data_size = 0;
/*  131 */     this.prev_sent.clear();
/*  132 */     this.listeners = new ArrayList();
/*  133 */     this.percent_complete = -1;
/*  134 */     this.urgent_message = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getTotalSize()
/*      */   {
/*  142 */     return this.total_size;
/*      */   }
/*      */   
/*      */   public int getDataQueuedBytes()
/*      */   {
/*  147 */     return this.total_data_size;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getProtocolQueuedBytes()
/*      */   {
/*  153 */     return this.total_size - this.total_data_size;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getPriorityBoost()
/*      */   {
/*  159 */     return this.priority_boost;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPriorityBoost(boolean boost)
/*      */   {
/*  166 */     this.priority_boost = boost;
/*      */   }
/*      */   
/*      */   public boolean isBlocked()
/*      */   {
/*  171 */     if (this.transport == null)
/*      */     {
/*  173 */       return false;
/*      */     }
/*      */     
/*  176 */     return !this.transport.isReadyForWrite(null);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasUrgentMessage()
/*      */   {
/*  182 */     return this.urgent_message != null;
/*      */   }
/*      */   
/*      */   public Message peekFirstMessage()
/*      */   {
/*      */     try
/*      */     {
/*  189 */       this.queue_mon.enter();
/*      */       
/*  191 */       return (Message)this.queue.peek();
/*      */     }
/*      */     finally
/*      */     {
/*  195 */       this.queue_mon.exit();
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
/*      */   public void addMessage(Message message, boolean manual_listener_notify)
/*      */   {
/*  211 */     boolean allowed = true;
/*  212 */     ArrayList list_ref = this.listeners;
/*      */     
/*  214 */     for (int i = 0; i < list_ref.size(); i++) {
/*  215 */       OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)list_ref.get(i);
/*  216 */       allowed = (allowed) && (listener.messageAdded(message));
/*      */     }
/*      */     
/*  219 */     if (!allowed)
/*      */     {
/*      */ 
/*  222 */       return;
/*      */     }
/*      */     
/*      */ 
/*  226 */     RawMessage[] rmesgs = this.stream_encoder.encodeMessage(message);
/*      */     
/*  228 */     if (this.destroyed) {
/*  229 */       for (int i = 0; i < rmesgs.length; i++) {
/*  230 */         rmesgs[i].destroy();
/*      */       }
/*  232 */       return;
/*      */     }
/*      */     
/*  235 */     for (int i = 0; i < rmesgs.length; i++)
/*      */     {
/*  237 */       RawMessage rmesg = rmesgs[i];
/*      */       
/*  239 */       removeMessagesOfType(rmesg.messagesToRemove(), manual_listener_notify);
/*      */       try
/*      */       {
/*  242 */         this.queue_mon.enter();
/*      */         
/*  244 */         int pos = 0;
/*  245 */         for (Iterator<RawMessage> it = this.queue.iterator(); it.hasNext();) {
/*  246 */           RawMessage msg = (RawMessage)it.next();
/*  247 */           if ((rmesg.getPriority() > msg.getPriority()) && (msg.getRawData()[0].position((byte)5) == 0)) {
/*      */             break;
/*      */           }
/*      */           
/*  251 */           pos++;
/*      */         }
/*  253 */         if (rmesg.isNoDelay()) {
/*  254 */           this.urgent_message = rmesg;
/*      */         }
/*  256 */         this.queue.add(pos, rmesg);
/*      */         
/*  258 */         DirectByteBuffer[] payload = rmesg.getRawData();
/*  259 */         int remaining = 0;
/*  260 */         for (int j = 0; j < payload.length; j++) {
/*  261 */           remaining += payload[j].remaining((byte)5);
/*      */         }
/*  263 */         this.total_size += remaining;
/*  264 */         if (rmesg.getType() == 1) {
/*  265 */           this.total_data_size += remaining;
/*      */         }
/*      */       } finally {
/*  268 */         this.queue_mon.exit();
/*      */       }
/*      */       
/*  271 */       if (manual_listener_notify) {
/*  272 */         NotificationItem item = new NotificationItem(0, null);
/*  273 */         item.message = rmesg;
/*      */         try {
/*  275 */           this.delayed_notifications_mon.enter();
/*      */           
/*  277 */           this.delayed_notifications.add(item);
/*      */         }
/*      */         finally {
/*  280 */           this.delayed_notifications_mon.exit();
/*      */         }
/*      */       }
/*      */       else {
/*  284 */         ArrayList listeners_ref = this.listeners;
/*      */         
/*  286 */         for (int j = 0; j < listeners_ref.size(); j++) {
/*  287 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(j);
/*  288 */           listener.messageQueued(rmesg.getBaseMessage());
/*      */         }
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
/*      */   public void removeMessagesOfType(Message[] message_types, boolean manual_listener_notify)
/*      */   {
/*  307 */     if (message_types == null) { return;
/*      */     }
/*  309 */     ArrayList<RawMessage> messages_removed = null;
/*      */     try
/*      */     {
/*  312 */       this.queue_mon.enter();
/*      */       
/*  314 */       for (Iterator<RawMessage> i = this.queue.iterator(); i.hasNext();) {
/*  315 */         RawMessage msg = (RawMessage)i.next();
/*      */         
/*  317 */         for (int t = 0; t < message_types.length; t++) {
/*  318 */           boolean same_type = message_types[t].getID().equals(msg.getID());
/*      */           
/*  320 */           if ((same_type) && (msg.getRawData()[0].position((byte)5) == 0)) {
/*  321 */             if (msg == this.urgent_message) { this.urgent_message = null;
/*      */             }
/*  323 */             DirectByteBuffer[] payload = msg.getRawData();
/*  324 */             int remaining = 0;
/*  325 */             for (int x = 0; x < payload.length; x++) {
/*  326 */               remaining += payload[x].remaining((byte)5);
/*      */             }
/*  328 */             this.total_size -= remaining;
/*  329 */             if (msg.getType() == 1) {
/*  330 */               this.total_data_size -= remaining;
/*      */             }
/*  332 */             if (manual_listener_notify) {
/*  333 */               NotificationItem item = new NotificationItem(1, null);
/*  334 */               item.message = msg;
/*      */               try {
/*  336 */                 this.delayed_notifications_mon.enter();
/*      */                 
/*  338 */                 this.delayed_notifications.add(item);
/*      */               }
/*      */               finally {
/*  341 */                 this.delayed_notifications_mon.exit();
/*      */               }
/*      */             }
/*      */             else {
/*  345 */               if (messages_removed == null) {
/*  346 */                 messages_removed = new ArrayList();
/*      */               }
/*  348 */               messages_removed.add(msg);
/*      */             }
/*  350 */             i.remove();
/*  351 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  356 */       if (this.queue.isEmpty()) {
/*  357 */         this.percent_complete = -1;
/*      */       }
/*      */     } finally {
/*  360 */       this.queue_mon.exit();
/*      */     }
/*      */     
/*  363 */     if ((!manual_listener_notify) && (messages_removed != null))
/*      */     {
/*  365 */       ArrayList listeners_ref = this.listeners;
/*      */       
/*  367 */       for (int x = 0; x < messages_removed.size(); x++) {
/*  368 */         RawMessage msg = (RawMessage)messages_removed.get(x);
/*      */         
/*  370 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*  371 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*  372 */           listener.messageRemoved(msg.getBaseMessage());
/*      */         }
/*  374 */         msg.destroy();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean removeMessage(Message message, boolean manual_listener_notify)
/*      */   {
/*  398 */     RawMessage msg_removed = null;
/*      */     try
/*      */     {
/*  401 */       this.queue_mon.enter();
/*      */       
/*  403 */       for (Iterator<RawMessage> it = this.queue.iterator(); it.hasNext();) {
/*  404 */         RawMessage raw = (RawMessage)it.next();
/*      */         
/*  406 */         if (message.equals(raw.getBaseMessage())) {
/*  407 */           if (raw.getRawData()[0].position((byte)5) != 0) break;
/*  408 */           if (raw == this.urgent_message) { this.urgent_message = null;
/*      */           }
/*  410 */           DirectByteBuffer[] payload = raw.getRawData();
/*  411 */           int remaining = 0;
/*  412 */           for (int x = 0; x < payload.length; x++) {
/*  413 */             remaining += payload[x].remaining((byte)5);
/*      */           }
/*  415 */           this.total_size -= remaining;
/*  416 */           if (raw.getType() == 1) {
/*  417 */             this.total_data_size -= remaining;
/*      */           }
/*  419 */           this.queue.remove(raw);
/*  420 */           msg_removed = raw;
/*  421 */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  427 */       if (this.queue.isEmpty()) {
/*  428 */         this.percent_complete = -1;
/*      */       }
/*      */     } finally {
/*  431 */       this.queue_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*  435 */     if (msg_removed != null) {
/*  436 */       if (manual_listener_notify) {
/*  437 */         NotificationItem item = new NotificationItem(1, null);
/*  438 */         item.message = msg_removed;
/*      */         try {
/*  440 */           this.delayed_notifications_mon.enter();
/*      */           
/*  442 */           this.delayed_notifications.add(item);
/*      */         }
/*      */         finally {
/*  445 */           this.delayed_notifications_mon.exit();
/*      */         }
/*      */       }
/*      */       else {
/*  449 */         ArrayList listeners_ref = this.listeners;
/*      */         
/*  451 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*  452 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*  453 */           listener.messageRemoved(msg_removed.getBaseMessage());
/*      */         }
/*  455 */         msg_removed.destroy();
/*      */       }
/*  457 */       return true;
/*      */     }
/*      */     
/*  460 */     return false;
/*      */   }
/*      */   
/*      */ 
/*  464 */   private WeakReference rawBufferCache = new WeakReference(null);
/*  465 */   private WeakReference origPositionsCache = new WeakReference(null);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] deliverToTransport(int max_bytes, boolean protocol_is_free, boolean manual_listener_notify)
/*      */     throws IOException
/*      */   {
/*  481 */     if (max_bytes < 1) {
/*  482 */       if (!protocol_is_free) {
/*  483 */         Debug.out("max_bytes < 1: " + max_bytes);
/*      */         
/*  485 */         return new int[2];
/*      */       }
/*      */       
/*  488 */       max_bytes = 0;
/*      */     }
/*      */     
/*  491 */     if (this.transport == null) {
/*  492 */       throw new IOException("not ready to deliver data");
/*      */     }
/*  494 */     int data_written = 0;
/*  495 */     int protocol_written = 0;
/*      */     
/*  497 */     ArrayList<RawMessage> messages_sent = null;
/*      */     
/*      */     try
/*      */     {
/*  501 */       this.queue_mon.enter();
/*      */       
/*  503 */       if (!this.queue.isEmpty())
/*      */       {
/*  505 */         int buffer_limit = 64;
/*      */         
/*  507 */         ByteBuffer[] raw_buffers = (ByteBuffer[])this.rawBufferCache.get();
/*  508 */         if (raw_buffers == null)
/*      */         {
/*  510 */           raw_buffers = new ByteBuffer[buffer_limit];
/*  511 */           this.rawBufferCache = new WeakReference(raw_buffers);
/*      */         }
/*      */         else {
/*  514 */           Arrays.fill(raw_buffers, null);
/*      */         }
/*      */         
/*      */ 
/*  518 */         int[] orig_positions = (int[])this.origPositionsCache.get();
/*  519 */         if (orig_positions == null)
/*      */         {
/*  521 */           orig_positions = new int[buffer_limit];
/*  522 */           this.origPositionsCache = new WeakReference(orig_positions);
/*      */         }
/*      */         else {
/*  525 */           Arrays.fill(orig_positions, 0);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  530 */         int buffer_count = 0;
/*      */         
/*  532 */         int total_sofar_excluding_free = 0;
/*  533 */         int total_to_write = 0;
/*      */         
/*      */ 
/*  536 */         for (Iterator<RawMessage> i = this.queue.iterator(); i.hasNext();)
/*      */         {
/*  538 */           RawMessage message = (RawMessage)i.next();
/*      */           
/*  540 */           boolean msg_is_free = (message.getType() == 0) && (protocol_is_free);
/*      */           
/*  542 */           DirectByteBuffer[] payloads = message.getRawData();
/*      */           
/*  544 */           for (int x = 0; x < payloads.length; x++)
/*      */           {
/*  546 */             ByteBuffer buff = payloads[x].getBuffer((byte)5);
/*      */             
/*  548 */             raw_buffers[buffer_count] = buff;
/*      */             
/*  550 */             orig_positions[buffer_count] = buff.position();
/*      */             
/*  552 */             buffer_count++;
/*      */             
/*  554 */             int rem = buff.remaining();
/*      */             
/*  556 */             total_to_write += rem;
/*      */             
/*  558 */             if (!msg_is_free)
/*      */             {
/*  560 */               total_sofar_excluding_free += rem;
/*      */               
/*  562 */               if (total_sofar_excluding_free >= max_bytes) {
/*      */                 break label397;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  568 */             if (buffer_count == buffer_limit)
/*      */             {
/*  570 */               int new_buffer_limit = buffer_limit * 2;
/*      */               
/*  572 */               ByteBuffer[] new_raw_buffers = new ByteBuffer[new_buffer_limit];
/*  573 */               int[] new_orig_positions = new int[new_buffer_limit];
/*      */               
/*  575 */               System.arraycopy(raw_buffers, 0, new_raw_buffers, 0, buffer_limit);
/*  576 */               System.arraycopy(orig_positions, 0, new_orig_positions, 0, buffer_limit);
/*      */               
/*  578 */               raw_buffers = new_raw_buffers;
/*  579 */               orig_positions = new_orig_positions;
/*      */               
/*  581 */               buffer_limit = new_buffer_limit;
/*      */             }
/*      */           }
/*      */         }
/*      */         label397:
/*  586 */         ByteBuffer last_buff = raw_buffers[(buffer_count - 1)];
/*      */         
/*  588 */         int orig_last_limit = last_buff.limit();
/*      */         int reduce_by;
/*  590 */         if (total_sofar_excluding_free > max_bytes)
/*      */         {
/*  592 */           reduce_by = total_sofar_excluding_free - max_bytes;
/*      */           
/*  594 */           last_buff.limit(orig_last_limit - reduce_by);
/*      */           
/*  596 */           total_to_write -= reduce_by;
/*      */         }
/*      */         
/*  599 */         if (total_to_write <= 0)
/*      */         {
/*  601 */           last_buff.limit(orig_last_limit);
/*      */           
/*  603 */           return new int[2];
/*      */         }
/*      */         
/*  606 */         this.transport.write(raw_buffers, 0, buffer_count);
/*      */         
/*  608 */         last_buff.limit(orig_last_limit);
/*      */         
/*  610 */         int pos = 0;
/*  611 */         boolean stop = false;
/*      */         
/*  613 */         while ((!this.queue.isEmpty()) && (!stop)) {
/*  614 */           RawMessage msg = (RawMessage)this.queue.get(0);
/*  615 */           DirectByteBuffer[] payloads = msg.getRawData();
/*      */           
/*  617 */           for (int x = 0; x < payloads.length; x++) {
/*  618 */             ByteBuffer bb = payloads[x].getBuffer((byte)5);
/*      */             
/*  620 */             int bytes_written = bb.limit() - bb.remaining() - orig_positions[pos];
/*  621 */             this.total_size -= bytes_written;
/*      */             
/*  623 */             if (msg.getType() == 1) {
/*  624 */               this.total_data_size -= bytes_written;
/*      */             }
/*      */             
/*  627 */             if ((x > 0) && (msg.getType() == 1)) {
/*  628 */               data_written += bytes_written;
/*      */             } else {
/*  630 */               protocol_written += bytes_written;
/*      */             }
/*      */             
/*  633 */             if (bb.hasRemaining()) {
/*  634 */               stop = true;
/*      */               
/*      */ 
/*  637 */               int message_size = 0;
/*  638 */               int written = 0;
/*      */               
/*  640 */               for (int i = 0; i < payloads.length; i++) {
/*  641 */                 ByteBuffer buff = payloads[i].getBuffer((byte)5);
/*      */                 
/*  643 */                 message_size += buff.limit();
/*      */                 
/*  645 */                 if (i < x) {
/*  646 */                   written += buff.limit();
/*      */                 }
/*  648 */                 else if (i == x) {
/*  649 */                   written += buff.position();
/*      */                 }
/*      */               }
/*      */               
/*  653 */               this.percent_complete = (written * 100 / message_size);
/*      */               
/*  655 */               break;
/*      */             }
/*  657 */             if (x == payloads.length - 1) {
/*  658 */               if (msg == this.urgent_message) { this.urgent_message = null;
/*      */               }
/*  660 */               this.queue.remove(0);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  669 */               this.percent_complete = -1;
/*      */               
/*  671 */               if (manual_listener_notify) {
/*  672 */                 NotificationItem item = new NotificationItem(2, null);
/*  673 */                 item.message = msg;
/*  674 */                 try { this.delayed_notifications_mon.enter();
/*  675 */                   this.delayed_notifications.add(item);
/*  676 */                 } finally { this.delayed_notifications_mon.exit();
/*      */                 }
/*      */               } else {
/*  679 */                 if (messages_sent == null) {
/*  680 */                   messages_sent = new ArrayList();
/*      */                 }
/*  682 */                 messages_sent.add(msg);
/*      */               }
/*      */             }
/*      */             
/*  686 */             pos++;
/*  687 */             if (pos >= buffer_count) {
/*  688 */               stop = true;
/*  689 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/*  695 */       this.queue_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  701 */     if ((data_written + protocol_written > 0) || (messages_sent != null))
/*      */     {
/*  703 */       if (this.trace) {
/*  704 */         TimeFormatter.milliTrace("omq:deliver: " + (data_written + protocol_written) + ", q=" + this.queue.size() + "/" + this.total_size);
/*      */       }
/*      */       
/*  707 */       if (manual_listener_notify)
/*      */       {
/*  709 */         if (data_written > 0) {
/*  710 */           NotificationItem item = new NotificationItem(3, null);
/*  711 */           item.byte_count = data_written;
/*      */           try {
/*  713 */             this.delayed_notifications_mon.enter();
/*      */             
/*  715 */             this.delayed_notifications.add(item);
/*      */           }
/*      */           finally {
/*  718 */             this.delayed_notifications_mon.exit();
/*      */           }
/*      */         }
/*      */         
/*  722 */         if (protocol_written > 0) {
/*  723 */           NotificationItem item = new NotificationItem(4, null);
/*  724 */           item.byte_count = protocol_written;
/*      */           try {
/*  726 */             this.delayed_notifications_mon.enter();
/*      */             
/*  728 */             this.delayed_notifications.add(item);
/*      */           }
/*      */           finally {
/*  731 */             this.delayed_notifications_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  736 */         ArrayList listeners_ref = this.listeners;
/*      */         
/*  738 */         int num_listeners = listeners_ref.size();
/*  739 */         for (int i = 0; i < num_listeners; i++) {
/*  740 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*      */           
/*  742 */           if (data_written > 0) listener.dataBytesSent(data_written);
/*  743 */           if (protocol_written > 0) { listener.protocolBytesSent(protocol_written);
/*      */           }
/*  745 */           if (messages_sent != null)
/*      */           {
/*  747 */             for (int x = 0; x < messages_sent.size(); x++) {
/*  748 */               RawMessage msg = (RawMessage)messages_sent.get(x);
/*      */               
/*  750 */               listener.messageSent(msg.getBaseMessage());
/*      */               
/*  752 */               if (i == num_listeners - 1) {
/*  753 */                 msg.destroy();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  760 */     else if (this.trace) {
/*  761 */       TimeFormatter.milliTrace("omq:deliver: 0, q=" + this.queue.size() + "/" + this.total_size);
/*      */     }
/*      */     
/*      */ 
/*  765 */     return new int[] { data_written, protocol_written };
/*      */   }
/*      */   
/*      */   public void flush()
/*      */   {
/*      */     try
/*      */     {
/*  772 */       this.queue_mon.enter();
/*      */       
/*  774 */       if (this.queue.isEmpty()) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  779 */       for (int i = 0; i < this.queue.size(); i++)
/*      */       {
/*  781 */         RawMessage msg = (RawMessage)this.queue.get(i);
/*      */         
/*  783 */         msg.setNoDelay();
/*      */         
/*  785 */         if (i == 0)
/*      */         {
/*  787 */           this.urgent_message = msg;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  792 */       this.queue_mon.exit();
/*      */     }
/*      */     
/*  795 */     ArrayList list_ref = this.listeners;
/*      */     
/*  797 */     for (int i = 0; i < list_ref.size(); i++) {
/*  798 */       OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)list_ref.get(i);
/*  799 */       listener.flush();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isDestroyed()
/*      */   {
/*  805 */     return this.destroyed;
/*      */   }
/*      */   
/*      */ 
/*      */   public void doListenerNotifications()
/*      */   {
/*      */     ArrayList notifications_copy;
/*      */     try
/*      */     {
/*  814 */       this.delayed_notifications_mon.enter();
/*      */       
/*  816 */       if (this.delayed_notifications.size() == 0) return;
/*  817 */       notifications_copy = new ArrayList(this.delayed_notifications);
/*  818 */       this.delayed_notifications.clear();
/*      */     }
/*      */     finally {
/*  821 */       this.delayed_notifications_mon.exit();
/*      */     }
/*      */     
/*  824 */     ArrayList listeners_ref = this.listeners;
/*      */     
/*  826 */     for (int j = 0; j < notifications_copy.size(); j++) {
/*  827 */       NotificationItem item = (NotificationItem)notifications_copy.get(j);
/*      */       
/*  829 */       switch (item.type) {
/*      */       case 0: 
/*  831 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*  832 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*  833 */           listener.messageQueued(item.message.getBaseMessage());
/*      */         }
/*  835 */         break;
/*      */       
/*      */       case 1: 
/*  838 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*  839 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*  840 */           listener.messageRemoved(item.message.getBaseMessage());
/*      */         }
/*  842 */         item.message.destroy();
/*  843 */         break;
/*      */       
/*      */       case 2: 
/*  846 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*  847 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*  848 */           listener.messageSent(item.message.getBaseMessage());
/*      */         }
/*  850 */         item.message.destroy();
/*  851 */         break;
/*      */       
/*      */       case 4: 
/*  854 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*  855 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*  856 */           listener.protocolBytesSent(item.byte_count);
/*      */         }
/*  858 */         break;
/*      */       
/*      */       case 3: 
/*  861 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*  862 */           OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*  863 */           listener.dataBytesSent(item.byte_count);
/*      */         }
/*  865 */         break;
/*      */       
/*      */       default: 
/*  868 */         Debug.out("NotificationItem.type unknown :" + item.type);
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTrace(boolean on)
/*      */   {
/*  878 */     this.trace = on;
/*      */     
/*  880 */     this.transport.setTrace(on);
/*      */   }
/*      */   
/*      */   public String getQueueTrace() {
/*  884 */     StringBuilder trace = new StringBuilder();
/*      */     
/*  886 */     trace.append("**** OUTGOING QUEUE TRACE ****\n");
/*      */     try
/*      */     {
/*  889 */       this.queue_mon.enter();
/*      */       
/*      */ 
/*  892 */       int i = 0;
/*      */       
/*  894 */       for (Iterator<RawMessage> it = this.prev_sent.iterator(); it.hasNext();) {
/*  895 */         RawMessage raw = (RawMessage)it.next();
/*  896 */         trace.append("[#h").append(i).append("]: ").append(raw.getID()).append(" [").append(raw.getDescription()).append("]").append("\n");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  902 */         i++;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  907 */       position = this.queue.size() - 1;
/*      */       
/*  909 */       for (it = this.queue.iterator(); it.hasNext();) {
/*  910 */         RawMessage raw = (RawMessage)it.next();
/*      */         
/*  912 */         int pos = raw.getRawData()[0].position((byte)5);
/*  913 */         int length = raw.getRawData()[0].limit((byte)5);
/*      */         
/*  915 */         trace.append("[#").append(position).append(" ").append(pos).append(":").append(length).append("]: ").append(raw.getID()).append(" [").append(raw.getDescription()).append("]").append("\n");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  928 */         position--;
/*      */       }
/*      */     } finally { int position;
/*      */       Iterator<RawMessage> it;
/*  932 */       this.queue_mon.exit();
/*      */     }
/*      */     
/*  935 */     return trace.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerQueueListener(OutgoingMessageQueue.MessageQueueListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  944 */       this.listeners_mon.enter();
/*      */       
/*  946 */       ArrayList new_list = new ArrayList(this.listeners.size() + 1);
/*  947 */       new_list.addAll(this.listeners);
/*  948 */       new_list.add(listener);
/*  949 */       this.listeners = new_list;
/*      */     } finally {
/*  951 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void cancelQueueListener(OutgoingMessageQueue.MessageQueueListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  960 */       this.listeners_mon.enter();
/*      */       
/*  962 */       ArrayList new_list = new ArrayList(this.listeners);
/*  963 */       new_list.remove(listener);
/*  964 */       this.listeners = new_list;
/*      */     } finally {
/*  966 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void notifyOfExternallySentMessage(Message message)
/*      */   {
/*  976 */     ArrayList listeners_ref = this.listeners;
/*      */     
/*  978 */     DirectByteBuffer[] buffs = message.getData();
/*  979 */     int size = 0;
/*  980 */     for (int i = 0; i < buffs.length; i++) {
/*  981 */       size += buffs[i].remaining((byte)5);
/*      */     }
/*      */     
/*  984 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*  985 */       OutgoingMessageQueue.MessageQueueListener listener = (OutgoingMessageQueue.MessageQueueListener)listeners_ref.get(i);
/*      */       
/*  987 */       listener.messageSent(message);
/*      */       
/*  989 */       if (message.getType() == 1) {
/*  990 */         listener.dataBytesSent(size);
/*      */       }
/*      */       else {
/*  993 */         listener.protocolBytesSent(size);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class NotificationItem
/*      */   {
/*      */     private static final int MESSAGE_ADDED = 0;
/*      */     
/*      */     private static final int MESSAGE_REMOVED = 1;
/*      */     
/*      */     private static final int MESSAGE_SENT = 2;
/*      */     
/*      */     private static final int DATA_BYTES_SENT = 3;
/*      */     
/*      */     private static final int PROTOCOL_BYTES_SENT = 4;
/*      */     
/*      */     private final int type;
/*      */     private RawMessage message;
/* 1013 */     private int byte_count = 0;
/*      */     
/* 1015 */     private NotificationItem(int notification_type) { this.type = notification_type; }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/OutgoingMessageQueueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */