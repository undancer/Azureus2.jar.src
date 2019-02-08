/*      */ package com.aelitis.azureus.core.dht.transport.util;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportProgressListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportTransferHandler;
/*      */ import java.security.SecureRandom;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTTransferHandler
/*      */ {
/*      */   private static final int TRANSFER_QUEUE_MAX = 128;
/*      */   private static final long MAX_TRANSFER_QUEUE_BYTES = 8388608L;
/*      */   private static final long WRITE_XFER_RESEND_DELAY_BASE = 12500L;
/*      */   private static final long READ_XFER_REREQUEST_DELAY_BASE = 5000L;
/*      */   private static final long WRITE_REPLY_TIMEOUT_BASE = 60000L;
/*      */   private final long WRITE_XFER_RESEND_DELAY;
/*      */   private final long READ_XFER_REREQUEST_DELAY;
/*      */   private final long WRITE_REPLY_TIMEOUT;
/*      */   private static final boolean XFER_TRACE = false;
/*   70 */   private final Map<HashWrapper, transferHandlerInterceptor> transfer_handlers = new HashMap();
/*      */   
/*   72 */   private final Map<Long, transferQueue> read_transfers = new HashMap();
/*   73 */   private final Map<Long, transferQueue> write_transfers = new HashMap();
/*      */   
/*      */   private long last_xferq_log;
/*      */   
/*      */   private int active_write_queue_processor_count;
/*      */   
/*      */   private long total_bytes_on_transfer_queues;
/*   80 */   final Map<HashWrapper, Object> call_transfers = new HashMap();
/*      */   
/*      */   private final Adapter adapter;
/*      */   
/*      */   private final int max_data;
/*      */   private final DHTLogger logger;
/*   86 */   final AEMonitor this_mon = new AEMonitor("DHTTransferHandler");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTTransferHandler(Adapter _adapter, int _max_data, DHTLogger _logger)
/*      */   {
/*   94 */     this(_adapter, _max_data, 2.0F, _logger);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTTransferHandler(Adapter _adapter, int _max_data, float _latency_indicator, DHTLogger _logger)
/*      */   {
/*  104 */     this.adapter = _adapter;
/*  105 */     this.max_data = _max_data;
/*  106 */     this.logger = _logger;
/*      */     
/*  108 */     this.WRITE_XFER_RESEND_DELAY = ((_latency_indicator * 12500.0F));
/*  109 */     this.READ_XFER_REREQUEST_DELAY = ((_latency_indicator * 5000.0F));
/*  110 */     this.WRITE_REPLY_TIMEOUT = ((_latency_indicator * 60000.0F));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler)
/*      */   {
/*  119 */     registerTransferHandler(handler_key, handler, null);
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
/*      */   public void registerTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler, Map<String, Object> options)
/*      */   {
/*  132 */     synchronized (this.transfer_handlers)
/*      */     {
/*  134 */       transferHandlerInterceptor existing = (transferHandlerInterceptor)this.transfer_handlers.put(new HashWrapper(handler_key), new transferHandlerInterceptor(handler, options));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  140 */       if (existing != null)
/*      */       {
/*  142 */         Debug.out("Duplicate transfer handler: existing=" + existing.getName() + ", new=" + handler.getName());
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
/*      */   public void unregisterTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler)
/*      */   {
/*  156 */     synchronized (this.transfer_handlers)
/*      */     {
/*  158 */       this.transfer_handlers.remove(new HashWrapper(handler_key));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int handleTransferRequest(DHTTransportContact target, long connection_id, byte[] transfer_key, byte[] request_key, byte[] data, int start, int length, boolean write_request, boolean first_packet_only)
/*      */     throws DHTTransportException
/*      */   {
/*      */     transferHandlerInterceptor handler;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  178 */     synchronized (this.transfer_handlers)
/*      */     {
/*  180 */       handler = (transferHandlerInterceptor)this.transfer_handlers.get(new HashWrapper(transfer_key));
/*      */     }
/*      */     
/*  183 */     if (handler == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  191 */       return -1;
/*      */     }
/*      */     
/*  194 */     if (data == null)
/*      */     {
/*  196 */       data = handler.handleRead(target, request_key);
/*      */     }
/*      */     
/*  199 */     if (data == null)
/*      */     {
/*  201 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  207 */     if (data.length == 0)
/*      */     {
/*  209 */       if (write_request)
/*      */       {
/*  211 */         sendWriteRequest(connection_id, target, transfer_key, request_key, data, 0, 0, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  222 */         sendReadReply(connection_id, target, transfer_key, request_key, data, 0, 0, 0);
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*  234 */       if (start < 0)
/*      */       {
/*  236 */         start = 0;
/*      */       }
/*  238 */       else if (start >= data.length)
/*      */       {
/*  240 */         log("dataRequest: invalid start position");
/*      */         
/*  242 */         return data.length;
/*      */       }
/*      */       
/*  245 */       if (length <= 0)
/*      */       {
/*  247 */         length = data.length;
/*      */       }
/*  249 */       else if (start + length > data.length)
/*      */       {
/*  251 */         log("dataRequest: invalid length");
/*      */         
/*  253 */         return data.length;
/*      */       }
/*      */       
/*  256 */       int end = start + length;
/*      */       
/*  258 */       while (start < end)
/*      */       {
/*  260 */         int chunk = end - start;
/*      */         
/*  262 */         if (chunk > this.max_data)
/*      */         {
/*  264 */           chunk = this.max_data;
/*      */         }
/*      */         
/*  267 */         if (write_request)
/*      */         {
/*  269 */           sendWriteRequest(connection_id, target, transfer_key, request_key, data, start, chunk, data.length);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  279 */           if (first_packet_only) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  285 */           sendReadReply(connection_id, target, transfer_key, request_key, data, start, chunk, data.length);
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
/*  296 */         start += chunk;
/*      */       }
/*      */     }
/*      */     
/*  300 */     return data.length;
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
/*      */   public void receivePacket(final DHTTransportContact originator, final Packet req)
/*      */   {
/*  322 */     byte packet_type = req.getPacketType();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  328 */     if (packet_type == 1)
/*      */     {
/*  330 */       transferQueue queue = lookupTransferQueue(this.read_transfers, req.getConnectionId());
/*      */       
/*      */ 
/*      */ 
/*  334 */       if (queue != null)
/*      */       {
/*  336 */         queue.add(req);
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*  345 */     else if (packet_type == 3)
/*      */     {
/*  347 */       transferQueue queue = lookupTransferQueue(this.write_transfers, req.getConnectionId());
/*      */       
/*      */ 
/*      */ 
/*  351 */       if (queue != null)
/*      */       {
/*  353 */         queue.add(req);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  363 */       byte[] transfer_key = req.getTransferKey();
/*      */       
/*  365 */       if (packet_type == 0)
/*      */       {
/*      */         try {
/*  368 */           handleTransferRequest(originator, req.getConnectionId(), transfer_key, req.getRequestKey(), null, req.getStartPosition(), req.getLength(), false, false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (DHTTransportException e)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  380 */           log(e);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  387 */         transferQueue old_queue = lookupTransferQueue(this.read_transfers, req.getConnectionId());
/*      */         
/*  389 */         if (old_queue != null)
/*      */         {
/*  391 */           old_queue.add(req);
/*      */         }
/*      */         else
/*      */         {
/*      */           final transferHandlerInterceptor handler;
/*      */           
/*  397 */           synchronized (this.transfer_handlers)
/*      */           {
/*  399 */             handler = (transferHandlerInterceptor)this.transfer_handlers.get(new HashWrapper(transfer_key));
/*      */           }
/*      */           
/*  402 */           if (handler != null)
/*      */           {
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  411 */               int req_total_len = req.getTotalLength();
/*      */               
/*  413 */               if ((handler.getBooleanOption("disable_call_acks", false)) && (req_total_len == req.getLength()))
/*      */               {
/*  415 */                 byte[] write_data = req.getData();
/*      */                 
/*  417 */                 if (write_data.length != req_total_len)
/*      */                 {
/*  419 */                   byte[] temp = new byte[req_total_len];
/*      */                   
/*  421 */                   System.arraycopy(write_data, 0, temp, 0, req_total_len);
/*      */                   
/*  423 */                   write_data = temp;
/*      */                 }
/*      */                 
/*  426 */                 final byte[] reply_data = handler.handleWrite(originator, req.getConnectionId(), req.getRequestKey(), write_data);
/*      */                 
/*  428 */                 if (reply_data != null)
/*      */                 {
/*  430 */                   if (reply_data.length <= this.max_data)
/*      */                   {
/*  432 */                     long write_connection_id = this.adapter.getConnectionID();
/*      */                     
/*  434 */                     sendWriteRequest(write_connection_id, originator, transfer_key, req.getRequestKey(), reply_data, 0, reply_data.length, reply_data.length);
/*      */ 
/*      */ 
/*      */                   }
/*      */                   else
/*      */                   {
/*      */ 
/*      */ 
/*      */                     try
/*      */                     {
/*      */ 
/*      */ 
/*  446 */                       this.this_mon.enter();
/*      */                       
/*  448 */                       if (this.active_write_queue_processor_count >= 128)
/*      */                       {
/*  450 */                         throw new DHTTransportException("Active write queue process thread limit exceeded");
/*      */                       }
/*      */                       
/*  453 */                       this.active_write_queue_processor_count += 1;
/*      */ 
/*      */ 
/*      */                     }
/*      */                     finally
/*      */                     {
/*      */ 
/*  460 */                       this.this_mon.exit();
/*      */                     }
/*      */                     
/*  463 */                     new AEThread2("DHTTransportUDP:writeQueueProcessor", true)
/*      */                     {
/*      */ 
/*      */                       public void run()
/*      */                       {
/*      */                         try
/*      */                         {
/*  470 */                           DHTTransferHandler.this.writeTransfer((DHTTransferHandler.DHTTransportProgressListenerTRACE)null, originator, req.getTransferKey(), req.getRequestKey(), reply_data, DHTTransferHandler.this.WRITE_REPLY_TIMEOUT);
/*      */ 
/*      */ 
/*      */ 
/*      */                         }
/*      */                         catch (DHTTransportException e)
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/*  480 */                           DHTTransferHandler.this.log("Failed to process transfer queue: " + Debug.getNestedExceptionMessage(e));
/*      */                         }
/*      */                         finally
/*      */                         {
/*      */                           try {
/*  485 */                             DHTTransferHandler.this.this_mon.enter();
/*      */                             
/*  487 */                             DHTTransferHandler.access$210(DHTTransferHandler.this);
/*      */ 
/*      */ 
/*      */                           }
/*      */                           finally
/*      */                           {
/*      */ 
/*  494 */                             DHTTransferHandler.this.this_mon.exit();
/*      */                           }
/*      */                           
/*      */                         }
/*      */                         
/*      */                       }
/*      */                       
/*  501 */                     }.start();
/*  502 */                     sendWriteReply(req.getConnectionId(), originator, req.getTransferKey(), req.getRequestKey(), req.getStartPosition(), req.getLength());
/*      */ 
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*  513 */                 final transferQueue new_queue = new transferQueue(this.read_transfers, req.getConnectionId());
/*      */                 
/*      */ 
/*      */ 
/*  517 */                 new_queue.add(req);
/*      */                 
/*      */ 
/*      */                 try
/*      */                 {
/*  522 */                   this.this_mon.enter();
/*      */                   
/*  524 */                   if (this.active_write_queue_processor_count >= 128)
/*      */                   {
/*  526 */                     new_queue.destroy();
/*      */                     
/*  528 */                     throw new DHTTransportException("Active write queue process thread limit exceeded");
/*      */                   }
/*      */                   
/*  531 */                   this.active_write_queue_processor_count += 1;
/*      */ 
/*      */ 
/*      */                 }
/*      */                 finally
/*      */                 {
/*      */ 
/*  538 */                   this.this_mon.exit();
/*      */                 }
/*      */                 
/*  541 */                 new AEThread2("DHTTransportUDP:writeQueueProcessor", true)
/*      */                 {
/*      */                   public void run()
/*      */                   {
/*      */                     try
/*      */                     {
/*  547 */                       byte[] write_data = DHTTransferHandler.this.runTransferQueue(new_queue, (DHTTransferHandler.DHTTransportProgressListenerTRACE)null, originator, req.getTransferKey(), req.getRequestKey(), 60000L, false);
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  557 */                       if (write_data != null)
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*  562 */                         if ((req.getStartPosition() != 0) || (req.getLength() != req.getTotalLength()))
/*      */                         {
/*      */ 
/*  565 */                           DHTTransferHandler.this.sendWriteReply(req.getConnectionId(), originator, req.getTransferKey(), req.getRequestKey(), 0, req.getTotalLength());
/*      */                         }
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  574 */                         byte[] reply_data = handler.handleWrite(originator, req.getConnectionId(), req.getRequestKey(), write_data);
/*      */                         
/*  576 */                         if (reply_data != null)
/*      */                         {
/*  578 */                           DHTTransferHandler.this.writeTransfer((DHTTransferHandler.DHTTransportProgressListenerTRACE)null, originator, req.getTransferKey(), req.getRequestKey(), reply_data, DHTTransferHandler.this.WRITE_REPLY_TIMEOUT);
/*      */ 
/*      */                         }
/*      */                         
/*      */ 
/*      */                       }
/*      */                       
/*      */ 
/*      */ 
/*      */                     }
/*      */                     catch (DHTTransportException e)
/*      */                     {
/*      */ 
/*  591 */                       DHTTransferHandler.this.log("Failed to process transfer queue: " + Debug.getNestedExceptionMessage(e));
/*      */                     }
/*      */                     finally
/*      */                     {
/*      */                       try {
/*  596 */                         DHTTransferHandler.this.this_mon.enter();
/*      */                         
/*  598 */                         DHTTransferHandler.access$210(DHTTransferHandler.this);
/*      */ 
/*      */ 
/*      */                       }
/*      */                       finally
/*      */                       {
/*      */ 
/*  605 */                         DHTTransferHandler.this.this_mon.exit();
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/*  612 */                 }.start();
/*  613 */                 sendWriteReply(req.getConnectionId(), originator, req.getTransferKey(), req.getRequestKey(), req.getStartPosition(), req.getLength());
/*      */ 
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */             }
/*      */             catch (DHTTransportException e)
/*      */             {
/*      */ 
/*  623 */               long now = SystemTime.getMonotonousTime();
/*      */               
/*  625 */               if ((this.last_xferq_log == 0L) || (now - this.last_xferq_log > 300000L))
/*      */               {
/*  627 */                 this.last_xferq_log = now;
/*      */                 
/*  629 */                 log("Failed to create transfer queue");
/*      */                 
/*  631 */                 log(e);
/*      */               }
/*      */             }
/*      */           }
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
/*      */   public byte[] readTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] key, long timeout)
/*      */     throws DHTTransportException
/*      */   {
/*  650 */     long connection_id = this.adapter.getConnectionID();
/*      */     
/*  652 */     transferQueue transfer_queue = new transferQueue(this.read_transfers, connection_id);
/*      */     
/*  654 */     return runTransferQueue(transfer_queue, listener, target, handler_key, key, timeout, true);
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
/*      */   protected byte[] runTransferQueue(transferQueue transfer_queue, DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] key, long timeout, boolean read_transfer)
/*      */     throws DHTTransportException
/*      */   {
/*  669 */     SortedSet<Packet> packets = new TreeSet(new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(DHTTransferHandler.Packet p1, DHTTransferHandler.Packet p2)
/*      */       {
/*      */ 
/*      */ 
/*  678 */         return p1.getStartPosition() - p2.getStartPosition();
/*      */       }
/*      */       
/*  681 */     });
/*  682 */     int entire_request_count = 0;
/*      */     
/*  684 */     int transfer_size = -1;
/*  685 */     int transferred = 0;
/*      */     
/*  687 */     String target_name = DHTLog.getString2(target.getID());
/*      */     try
/*      */     {
/*  690 */       long start = SystemTime.getCurrentTime();
/*      */       
/*  692 */       if (read_transfer)
/*      */       {
/*  694 */         if (listener != null) {
/*  695 */           listener.reportActivity(getMessageText("request_all", new String[] { target_name }));
/*      */         }
/*      */         
/*  698 */         entire_request_count++;
/*      */         
/*  700 */         sendReadRequest(transfer_queue.getConnectionID(), target, handler_key, key, 0, 0);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  706 */         entire_request_count++;
/*      */       }
/*      */       Packet reply;
/*  709 */       while (SystemTime.getCurrentTime() - start <= timeout)
/*      */       {
/*  711 */         reply = transfer_queue.receive(this.READ_XFER_REREQUEST_DELAY);
/*      */         Iterator<Packet> it;
/*  713 */         if (reply != null)
/*      */         {
/*  715 */           if ((listener != null) && (transfer_size == -1))
/*      */           {
/*  717 */             transfer_size = reply.getTotalLength();
/*      */             
/*  719 */             listener.reportSize(transfer_size);
/*      */           }
/*      */           
/*  722 */           it = packets.iterator();
/*      */           
/*  724 */           boolean duplicate = false;
/*      */           
/*  726 */           while (it.hasNext())
/*      */           {
/*  728 */             Packet p = (Packet)it.next();
/*      */             
/*      */ 
/*      */ 
/*  732 */             if ((p.getStartPosition() < reply.getStartPosition() + reply.getLength()) && (p.getStartPosition() + p.getLength() > reply.getStartPosition()))
/*      */             {
/*      */ 
/*  735 */               duplicate = true;
/*      */               
/*  737 */               break;
/*      */             }
/*      */           }
/*      */           
/*  741 */           if (!duplicate)
/*      */           {
/*  743 */             if (listener != null) {
/*  744 */               listener.reportActivity(getMessageText("received_bit", new String[] { String.valueOf(reply.getStartPosition()), String.valueOf(reply.getStartPosition() + reply.getLength()), target_name }));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  752 */             transferred += reply.getLength();
/*      */             
/*  754 */             if (listener != null) {
/*  755 */               listener.reportCompleteness(transfer_size == 0 ? 100 : 100 * transferred / transfer_size);
/*      */             }
/*      */             
/*  758 */             packets.add(reply);
/*      */             
/*      */ 
/*      */ 
/*  762 */             it = packets.iterator();
/*      */             
/*  764 */             int pos = 0;
/*  765 */             int actual_end = -1;
/*      */             
/*  767 */             while (it.hasNext())
/*      */             {
/*  769 */               Packet p = (Packet)it.next();
/*      */               
/*  771 */               if (actual_end == -1)
/*      */               {
/*  773 */                 actual_end = p.getTotalLength();
/*      */               }
/*      */               
/*  776 */               if (p.getStartPosition() != pos) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  783 */               pos += p.getLength();
/*      */               
/*  785 */               if (pos == actual_end)
/*      */               {
/*      */ 
/*      */ 
/*  789 */                 if (listener != null) {
/*  790 */                   listener.reportActivity(getMessageText("complete", new String[0]));
/*      */                 }
/*      */                 
/*  793 */                 byte[] result = new byte[actual_end];
/*      */                 
/*  795 */                 it = packets.iterator();
/*      */                 
/*  797 */                 pos = 0;
/*      */                 
/*  799 */                 while (it.hasNext())
/*      */                 {
/*  801 */                   p = (Packet)it.next();
/*      */                   
/*  803 */                   System.arraycopy(p.getData(), 0, result, pos, p.getLength());
/*      */                   
/*  805 */                   pos += p.getLength();
/*      */                 }
/*      */                 
/*  808 */                 return result;
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*  816 */         else if (packets.size() == 0)
/*      */         {
/*  818 */           if (entire_request_count == 2)
/*      */           {
/*  820 */             if (listener != null) {
/*  821 */               listener.reportActivity(getMessageText("timeout", new String[] { target_name }));
/*      */             }
/*      */             
/*  824 */             return null;
/*      */           }
/*      */           
/*  827 */           entire_request_count++;
/*      */           
/*  829 */           if (listener != null) {
/*  830 */             listener.reportActivity(getMessageText("rerequest_all", new String[] { target_name }));
/*      */           }
/*      */           
/*  833 */           sendReadRequest(transfer_queue.getConnectionID(), target, handler_key, key, 0, 0);
/*      */         }
/*      */         else
/*      */         {
/*  837 */           Iterator<Packet> it = packets.iterator();
/*      */           
/*  839 */           int pos = 0;
/*  840 */           int actual_end = -1;
/*      */           
/*  842 */           while (it.hasNext())
/*      */           {
/*  844 */             Packet p = (Packet)it.next();
/*      */             
/*  846 */             if (actual_end == -1)
/*      */             {
/*  848 */               actual_end = p.getTotalLength();
/*      */             }
/*      */             
/*  851 */             if (p.getStartPosition() != pos)
/*      */             {
/*  853 */               if (listener != null) {
/*  854 */                 listener.reportActivity(getMessageText("rerequest_bit", new String[] { String.valueOf(pos), String.valueOf(p.getStartPosition()), target_name }));
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  862 */               sendReadRequest(transfer_queue.getConnectionID(), target, handler_key, key, pos, p.getStartPosition() - pos);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  872 */             pos = p.getStartPosition() + p.getLength();
/*      */           }
/*      */           
/*  875 */           if (pos != actual_end)
/*      */           {
/*  877 */             if (listener != null) {
/*  878 */               listener.reportActivity(getMessageText("rerequest_bit", new String[] { String.valueOf(pos), String.valueOf(actual_end), target_name }));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  886 */             sendReadRequest(transfer_queue.getConnectionID(), target, handler_key, key, pos, actual_end - pos);
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
/*  898 */       if (listener != null) {
/*  899 */         if (packets.size() == 0)
/*      */         {
/*  901 */           listener.reportActivity(getMessageText("timeout", new String[] { target_name }));
/*      */         }
/*      */         else
/*      */         {
/*  905 */           listener.reportActivity(getMessageText("timeout_some", new String[] { String.valueOf(packets.size()), target_name }));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  913 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*  917 */       transfer_queue.destroy();
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
/*      */   public void writeTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] key, byte[] data, long timeout)
/*      */     throws DHTTransportException
/*      */   {
/*  932 */     long connection_id = this.adapter.getConnectionID();
/*      */     
/*  934 */     writeTransfer(listener, target, connection_id, handler_key, key, data, timeout);
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
/*      */   private void writeTransfer(DHTTransportProgressListener listener, DHTTransportContact target, long connection_id, byte[] handler_key, byte[] key, byte[] data, long timeout)
/*      */     throws DHTTransportException
/*      */   {
/*  949 */     transferQueue transfer_queue = null;
/*      */     try
/*      */     {
/*  952 */       transfer_queue = new transferQueue(this.write_transfers, connection_id);
/*      */       
/*  954 */       boolean ok = false;
/*  955 */       boolean reply_received = false;
/*      */       
/*  957 */       int loop = 0;
/*  958 */       int total_length = data.length;
/*      */       
/*  960 */       long start = SystemTime.getCurrentTime();
/*      */       
/*  962 */       long last_packet_time = 0L;
/*      */       
/*      */       for (;;)
/*      */       {
/*  966 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  968 */         if (now < start)
/*      */         {
/*  970 */           start = now;
/*      */           
/*  972 */           last_packet_time = 0L;
/*      */         }
/*      */         else
/*      */         {
/*  976 */           if (now - start > timeout) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  982 */         long time_since_last_packet = now - last_packet_time;
/*      */         
/*  984 */         if (time_since_last_packet >= this.WRITE_XFER_RESEND_DELAY)
/*      */         {
/*  986 */           if (listener != null) {
/*  987 */             listener.reportActivity(getMessageText(loop == 0 ? "sending" : "resending", new String[0]));
/*      */           }
/*      */           
/*  990 */           loop++;
/*      */           
/*  992 */           total_length = handleTransferRequest(target, connection_id, handler_key, key, data, -1, -1, true, reply_received);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1002 */           last_packet_time = now;
/* 1003 */           time_since_last_packet = 0L;
/*      */         }
/*      */         
/* 1006 */         Packet packet = transfer_queue.receive(this.WRITE_XFER_RESEND_DELAY - time_since_last_packet);
/*      */         
/* 1008 */         if (packet != null)
/*      */         {
/* 1010 */           last_packet_time = now;
/*      */           
/* 1012 */           reply_received = true;
/*      */           
/* 1014 */           if ((packet.getStartPosition() == 0) && (packet.getLength() == total_length))
/*      */           {
/* 1016 */             ok = true;
/*      */             
/* 1018 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1023 */       if (ok)
/*      */       {
/* 1025 */         if (listener != null) {
/* 1026 */           listener.reportCompleteness(100);
/*      */           
/* 1028 */           listener.reportActivity(getMessageText("send_complete", new String[0]));
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1033 */         if (listener != null) {
/* 1034 */           listener.reportActivity(getMessageText("send_timeout", new String[0]));
/*      */         }
/*      */         
/* 1037 */         throw new DHTTransportException("Timeout");
/*      */       }
/*      */     }
/*      */     finally {
/* 1041 */       if (transfer_queue != null)
/*      */       {
/* 1043 */         transfer_queue.destroy();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] writeReadTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] transfer_key, byte[] data, long timeout)
/*      */     throws DHTTransportException
/*      */   {
/*      */     transferHandlerInterceptor handler;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1060 */     synchronized (this.transfer_handlers)
/*      */     {
/* 1062 */       handler = (transferHandlerInterceptor)this.transfer_handlers.get(new HashWrapper(transfer_key));
/*      */     }
/*      */     
/* 1065 */     if (handler == null)
/*      */     {
/* 1067 */       return null;
/*      */     }
/*      */     
/* 1070 */     boolean no_acks = (handler.getBooleanOption("disable_call_acks", false)) && (data.length <= this.max_data);
/*      */     
/* 1072 */     long connection_id = this.adapter.getConnectionID();
/*      */     
/* 1074 */     byte[] call_key = new byte[20];
/*      */     
/* 1076 */     RandomUtils.SECURE_RANDOM.nextBytes(call_key);
/*      */     
/* 1078 */     AESemaphore call_sem = new AESemaphore("DHTTransportUDP:calSem");
/*      */     
/* 1080 */     HashWrapper wrapped_key = new HashWrapper(call_key);
/*      */     try
/*      */     {
/* 1083 */       this.this_mon.enter();
/*      */       
/* 1085 */       this.call_transfers.put(wrapped_key, new Object[] { call_sem, Long.valueOf(connection_id) });
/*      */     }
/*      */     finally
/*      */     {
/* 1089 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1092 */     boolean removed = false;
/*      */     try {
/*      */       long start;
/* 1095 */       if (no_acks)
/*      */       {
/* 1097 */         int retry_count = 0;
/*      */         
/*      */         for (;;)
/*      */         {
/* 1101 */           start = SystemTime.getMonotonousTime();
/*      */           
/* 1103 */           sendWriteRequest(connection_id, target, transfer_key, call_key, data, 0, data.length, data.length);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1113 */           long timeout_to_use = Math.min(timeout, this.WRITE_XFER_RESEND_DELAY);
/*      */           
/* 1115 */           if (call_sem.reserve(timeout_to_use))
/*      */           {
/*      */             try {
/* 1118 */               this.this_mon.enter();
/*      */               
/* 1120 */               Object res = this.call_transfers.remove(wrapped_key);
/*      */               
/* 1122 */               removed = true;
/*      */               
/* 1124 */               if ((res instanceof byte[]))
/*      */               {
/* 1126 */                 byte[] arrayOfByte1 = (byte[])res;
/*      */                 
/*      */ 
/*      */ 
/* 1130 */                 this.this_mon.exit();
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1177 */                 if (!removed) {
/*      */                   try
/*      */                   {
/* 1180 */                     this.this_mon.enter();
/*      */                     
/* 1182 */                     this.call_transfers.remove(wrapped_key);
/*      */                   }
/*      */                   finally {}
/*      */                 }
/* 1186 */                 return arrayOfByte1;
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/* 1130 */               this.this_mon.exit();
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */           }
/* 1137 */           else if (retry_count <= 0)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1142 */             retry_count++;
/*      */             
/* 1144 */             timeout -= SystemTime.getMonotonousTime() - start;
/*      */             
/* 1146 */             if (timeout < 1000L) {
/*      */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1154 */         writeTransfer(listener, target, connection_id, transfer_key, call_key, data, timeout);
/*      */         
/* 1156 */         if (call_sem.reserve(timeout)) {
/*      */           try
/*      */           {
/* 1159 */             this.this_mon.enter();
/*      */             
/* 1161 */             Object res = this.call_transfers.remove(wrapped_key);
/*      */             
/* 1163 */             removed = true;
/*      */             
/* 1165 */             if ((res instanceof byte[]))
/*      */             {
/* 1167 */               start = (byte[])res;
/*      */               
/*      */ 
/*      */ 
/* 1171 */               this.this_mon.exit();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1177 */               if (!removed) {
/*      */                 try
/*      */                 {
/* 1180 */                   this.this_mon.enter();
/*      */                   
/* 1182 */                   this.call_transfers.remove(wrapped_key);
/*      */                 }
/*      */                 finally {}
/*      */               }
/* 1186 */               return start;
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 1171 */             this.this_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1177 */       if (!removed) {
/*      */         try
/*      */         {
/* 1180 */           this.this_mon.enter();
/*      */           
/* 1182 */           this.call_transfers.remove(wrapped_key);
/*      */         }
/*      */         finally
/*      */         {
/* 1186 */           this.this_mon.exit();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1191 */     throw new DHTTransportException("timeout");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected transferQueue lookupTransferQueue(Map<Long, transferQueue> transfers, long id)
/*      */   {
/*      */     try
/*      */     {
/* 1200 */       this.this_mon.enter();
/*      */       
/* 1202 */       return (transferQueue)transfers.get(new Long(id));
/*      */     }
/*      */     finally
/*      */     {
/* 1206 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getMessageText(String resource, String... params)
/*      */   {
/* 1215 */     return MessageText.getString("DHTTransport.report." + resource, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final class DHTTransportProgressListenerTRACE
/*      */     implements DHTTransportProgressListener
/*      */   {
/*      */     private String prefix;
/*      */     
/*      */ 
/*      */ 
/*      */     public DHTTransportProgressListenerTRACE(String prefix)
/*      */     {
/* 1229 */       this.prefix = prefix;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void reportSize(long size) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void reportActivity(String str) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void reportCompleteness(int percent) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected class transferQueue
/*      */   {
/*      */     private final Map<Long, transferQueue> transfers;
/*      */     
/*      */ 
/*      */ 
/*      */     private final long connection_id;
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean destroyed;
/*      */     
/*      */ 
/*      */ 
/* 1268 */     private final List<DHTTransferHandler.Packet> packets = new ArrayList();
/*      */     
/* 1270 */     private final AESemaphore packets_sem = new AESemaphore("DHTUDPTransport:transferQueue");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected transferQueue(long _transfers)
/*      */       throws DHTTransportException
/*      */     {
/* 1279 */       this.transfers = _transfers;
/* 1280 */       this.connection_id = _connection_id;
/*      */       try
/*      */       {
/* 1283 */         DHTTransferHandler.this.this_mon.enter();
/*      */         
/* 1285 */         if (this.transfers.size() > 128)
/*      */         {
/* 1287 */           Debug.out("Transfer queue count limit exceeded");
/*      */           
/* 1289 */           throw new DHTTransportException("Transfer queue limit exceeded");
/*      */         }
/*      */         
/* 1292 */         Long l_id = new Long(this.connection_id);
/*      */         
/* 1294 */         transferQueue existing = (transferQueue)this.transfers.get(l_id);
/*      */         
/* 1296 */         if (existing != null)
/*      */         {
/* 1298 */           existing.destroy();
/*      */         }
/*      */         
/* 1301 */         this.transfers.put(l_id, this);
/*      */       }
/*      */       finally
/*      */       {
/* 1305 */         DHTTransferHandler.this.this_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getConnectionID()
/*      */     {
/* 1312 */       return this.connection_id;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void add(DHTTransferHandler.Packet packet)
/*      */     {
/*      */       try
/*      */       {
/* 1320 */         DHTTransferHandler.this.this_mon.enter();
/*      */         
/* 1322 */         if (this.destroyed) {
/*      */           return;
/*      */         }
/*      */         
/*      */ 
/* 1327 */         if (DHTTransferHandler.this.total_bytes_on_transfer_queues > 8388608L)
/*      */         {
/* 1329 */           Debug.out("Transfer queue byte limit exceeded"); return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1336 */         int length = packet.getLength();
/*      */         
/* 1338 */         DHTTransferHandler.access$314(DHTTransferHandler.this, length);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1344 */         this.packets.add(packet);
/*      */       }
/*      */       finally
/*      */       {
/* 1348 */         DHTTransferHandler.this.this_mon.exit();
/*      */       }
/*      */       
/* 1351 */       this.packets_sem.release();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected DHTTransferHandler.Packet receive(long timeout)
/*      */     {
/* 1358 */       if (this.packets_sem.reserve(timeout)) {
/*      */         try
/*      */         {
/* 1361 */           DHTTransferHandler.this.this_mon.enter();
/*      */           
/* 1363 */           if (this.destroyed)
/*      */           {
/* 1365 */             return null;
/*      */           }
/*      */           
/* 1368 */           DHTTransferHandler.Packet packet = (DHTTransferHandler.Packet)this.packets.remove(0);
/*      */           
/* 1370 */           int length = packet.getLength();
/*      */           
/* 1372 */           DHTTransferHandler.access$322(DHTTransferHandler.this, length);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1378 */           return packet;
/*      */         }
/*      */         finally
/*      */         {
/* 1382 */           DHTTransferHandler.this.this_mon.exit();
/*      */         }
/*      */       }
/*      */       
/* 1386 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void destroy()
/*      */     {
/*      */       try
/*      */       {
/* 1394 */         DHTTransferHandler.this.this_mon.enter();
/*      */         
/* 1396 */         this.destroyed = true;
/*      */         
/* 1398 */         this.transfers.remove(new Long(this.connection_id));
/*      */         
/* 1400 */         for (int i = 0; i < this.packets.size(); i++)
/*      */         {
/* 1402 */           DHTTransferHandler.Packet packet = (DHTTransferHandler.Packet)this.packets.get(i);
/*      */           
/* 1404 */           int length = packet.getLength();
/*      */           
/* 1406 */           DHTTransferHandler.access$322(DHTTransferHandler.this, length);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1413 */         this.packets.clear();
/*      */         
/* 1415 */         this.packets_sem.releaseForever();
/*      */       }
/*      */       finally
/*      */       {
/* 1419 */         DHTTransferHandler.this.this_mon.exit();
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
/*      */   public void sendReadRequest(long connection_id, DHTTransportContact contact, byte[] transfer_key, byte[] key, int start_pos, int len)
/*      */   {
/* 1437 */     this.adapter.sendRequest(contact, new Packet(connection_id, (byte)0, transfer_key, key, new byte[0], start_pos, len, 0));
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
/*      */   public void sendReadReply(long connection_id, DHTTransportContact contact, byte[] transfer_key, byte[] key, byte[] data, int start_position, int length, int total_length)
/*      */   {
/* 1465 */     this.adapter.sendRequest(contact, new Packet(connection_id, (byte)1, transfer_key, key, data, start_position, length, total_length));
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
/*      */   public void sendWriteRequest(long connection_id, DHTTransportContact contact, byte[] transfer_key, byte[] key, byte[] data, int start_position, int length, int total_length)
/*      */   {
/* 1493 */     this.adapter.sendRequest(contact, new Packet(connection_id, (byte)2, transfer_key, key, data, start_position, length, total_length));
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
/*      */   public void sendWriteReply(long connection_id, DHTTransportContact contact, byte[] transfer_key, byte[] key, int start_position, int length)
/*      */   {
/* 1519 */     this.adapter.sendRequest(contact, new Packet(connection_id, (byte)3, transfer_key, key, new byte[0], start_position, length, 0));
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
/*      */   private void log(String str)
/*      */   {
/* 1540 */     this.logger.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void log(Throwable e)
/*      */   {
/* 1551 */     this.logger.log(e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected class transferHandlerInterceptor
/*      */     implements DHTTransportTransferHandler
/*      */   {
/*      */     private final DHTTransportTransferHandler handler;
/*      */     
/*      */     private final Map<String, Object> options;
/*      */     
/*      */ 
/*      */     protected transferHandlerInterceptor(Map<String, Object> _handler)
/*      */     {
/* 1566 */       this.handler = _handler;
/* 1567 */       this.options = _options;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 1573 */       return this.handler.getName();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean getBooleanOption(String name, boolean def)
/*      */     {
/* 1581 */       if (this.options == null)
/*      */       {
/* 1583 */         return def;
/*      */       }
/*      */       
/* 1586 */       Boolean b = (Boolean)this.options.get(name);
/*      */       
/* 1588 */       return b == null ? def : b.booleanValue();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public byte[] handleRead(DHTTransportContact originator, byte[] key)
/*      */     {
/* 1596 */       return this.handler.handleRead(originator, key);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public byte[] handleWrite(DHTTransportContact originator, byte[] key, byte[] value)
/*      */     {
/* 1605 */       return handleWrite(originator, 0L, key, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public byte[] handleWrite(DHTTransportContact originator, long connection_id, byte[] key, byte[] value)
/*      */     {
/* 1615 */       HashWrapper key_wrapper = new HashWrapper(key);
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1620 */         DHTTransferHandler.this.this_mon.enter();
/*      */         
/* 1622 */         Object _obj = DHTTransferHandler.this.call_transfers.get(key_wrapper);
/*      */         
/* 1624 */         if ((_obj instanceof Object[]))
/*      */         {
/* 1626 */           Object[] obj = (Object[])_obj;
/*      */           
/*      */ 
/*      */ 
/* 1630 */           if (((Long)obj[1]).longValue() != connection_id)
/*      */           {
/* 1632 */             AESemaphore sem = (AESemaphore)obj[0];
/*      */             
/* 1634 */             DHTTransferHandler.this.call_transfers.put(key_wrapper, value);
/*      */             
/* 1636 */             sem.release();
/*      */             
/* 1638 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 1643 */         DHTTransferHandler.this.this_mon.exit();
/*      */       }
/*      */       
/* 1646 */       return this.handler.handleWrite(originator, key, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static class Packet
/*      */   {
/*      */     public static final byte PT_READ_REQUEST = 0;
/*      */     
/*      */     public static final byte PT_READ_REPLY = 1;
/*      */     
/*      */     public static final byte PT_WRITE_REQUEST = 2;
/*      */     
/*      */     public static final byte PT_WRITE_REPLY = 3;
/*      */     
/*      */     private final long connection_id;
/*      */     
/*      */     private final byte packet_type;
/*      */     
/*      */     private final byte[] transfer_key;
/*      */     
/*      */     private final byte[] key;
/*      */     
/*      */     private final byte[] data;
/*      */     
/*      */     private final int start_position;
/*      */     
/*      */     private final int length;
/*      */     
/*      */     private final int total_length;
/*      */     private int flags;
/*      */     
/*      */     public Packet(long _connection_id, byte _packet_type, byte[] _transfer_key, byte[] _key, byte[] _data, int _start_position, int _length, int _total_length)
/*      */     {
/* 1680 */       this.connection_id = _connection_id;
/* 1681 */       this.packet_type = _packet_type;
/* 1682 */       this.transfer_key = _transfer_key;
/* 1683 */       this.key = _key;
/* 1684 */       this.data = _data;
/* 1685 */       this.start_position = _start_position;
/* 1686 */       this.length = _length;
/* 1687 */       this.total_length = _total_length;
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
/*      */ 
/*      */ 
/*      */     public Packet(long _connection_id, byte _packet_type, byte[] _transfer_key, byte[] _key, byte[] _data, int _start_position, int _length, int _total_length, int _flags)
/*      */     {
/* 1702 */       this.connection_id = _connection_id;
/* 1703 */       this.packet_type = _packet_type;
/* 1704 */       this.transfer_key = _transfer_key;
/* 1705 */       this.key = _key;
/* 1706 */       this.data = _data;
/* 1707 */       this.start_position = _start_position;
/* 1708 */       this.length = _length;
/* 1709 */       this.total_length = _total_length;
/* 1710 */       this.flags = _flags;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getConnectionId()
/*      */     {
/* 1716 */       return this.connection_id;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte getPacketType()
/*      */     {
/* 1722 */       return this.packet_type;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getTransferKey()
/*      */     {
/* 1728 */       return this.transfer_key;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getRequestKey()
/*      */     {
/* 1734 */       return this.key;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getData()
/*      */     {
/* 1740 */       return this.data;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getStartPosition()
/*      */     {
/* 1746 */       return this.start_position;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getLength()
/*      */     {
/* 1752 */       return this.length;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getTotalLength()
/*      */     {
/* 1758 */       return this.total_length;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getFlags()
/*      */     {
/* 1764 */       return this.flags;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getString()
/*      */     {
/* 1770 */       return "ty=" + this.packet_type + ",tk=" + DHTLog.getString2(this.transfer_key) + ",rk=" + DHTLog.getString2(this.key) + ",data=" + this.data.length + ",st=" + this.start_position + ",len=" + this.length + ",tot=" + this.total_length;
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface Adapter
/*      */   {
/*      */     public abstract long getConnectionID();
/*      */     
/*      */     public abstract void sendRequest(DHTTransportContact paramDHTTransportContact, DHTTransferHandler.Packet paramPacket);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/util/DHTTransferHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */