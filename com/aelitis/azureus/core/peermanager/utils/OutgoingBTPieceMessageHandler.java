/*     */ package com.aelitis.azureus.core.peermanager.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTPiece;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerAdapter;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ public class OutgoingBTPieceMessageHandler
/*     */ {
/*     */   private final PEPeer peer;
/*     */   private final OutgoingMessageQueue outgoing_message_queue;
/*     */   private byte piece_version;
/*  47 */   private final LinkedList<DiskManagerReadRequest> requests = new LinkedList();
/*  48 */   private final ArrayList<DiskManagerReadRequest> loading_messages = new ArrayList();
/*  49 */   private final HashMap<BTPiece, DiskManagerReadRequest> queued_messages = new HashMap();
/*     */   
/*  51 */   private final AEMonitor lock_mon = new AEMonitor("OutgoingBTPieceMessageHandler:lock");
/*  52 */   private boolean destroyed = false;
/*  53 */   private int request_read_ahead = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final OutgoingBTPieceMessageHandlerAdapter adapter;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OutgoingBTPieceMessageHandler(PEPeer _peer, OutgoingMessageQueue _outgoing_message_q, OutgoingBTPieceMessageHandlerAdapter _adapter, byte _piece_version)
/*     */   {
/*  71 */     this.peer = _peer;
/*  72 */     this.outgoing_message_queue = _outgoing_message_q;
/*  73 */     this.adapter = _adapter;
/*  74 */     this.piece_version = _piece_version;
/*     */     
/*  76 */     this.outgoing_message_queue.registerQueueListener(this.sent_message_listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPieceVersion(byte version)
/*     */   {
/*  83 */     this.piece_version = version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  88 */   private final DiskManagerReadRequestListener read_req_listener = new DiskManagerReadRequestListener() {
/*     */     public void readCompleted(DiskManagerReadRequest request, DirectByteBuffer data) {
/*     */       try {
/*  91 */         OutgoingBTPieceMessageHandler.this.lock_mon.enter();
/*     */         
/*  93 */         if ((!OutgoingBTPieceMessageHandler.this.loading_messages.contains(request)) || (OutgoingBTPieceMessageHandler.this.destroyed)) {
/*  94 */           data.returnToPool(); return;
/*     */         }
/*     */         
/*  97 */         OutgoingBTPieceMessageHandler.this.loading_messages.remove(request);
/*     */         
/*  99 */         BTPiece msg = new BTPiece(request.getPieceNumber(), request.getOffset(), data, OutgoingBTPieceMessageHandler.this.piece_version);
/* 100 */         OutgoingBTPieceMessageHandler.this.queued_messages.put(msg, request);
/*     */         
/* 102 */         OutgoingBTPieceMessageHandler.this.outgoing_message_queue.addMessage(msg, true);
/*     */       }
/*     */       finally {
/* 105 */         OutgoingBTPieceMessageHandler.this.lock_mon.exit();
/*     */       }
/*     */       
/* 108 */       OutgoingBTPieceMessageHandler.this.outgoing_message_queue.doListenerNotifications();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void readFailed(DiskManagerReadRequest request, Throwable cause)
/*     */     {
/*     */       try
/*     */       {
/* 117 */         OutgoingBTPieceMessageHandler.this.lock_mon.enter();
/*     */         
/* 119 */         if ((!OutgoingBTPieceMessageHandler.this.loading_messages.contains(request)) || (OutgoingBTPieceMessageHandler.this.destroyed)) {
/*     */           return;
/*     */         }
/* 122 */         OutgoingBTPieceMessageHandler.this.loading_messages.remove(request);
/*     */       } finally {
/* 124 */         OutgoingBTPieceMessageHandler.this.lock_mon.exit();
/*     */       }
/*     */       
/* 127 */       OutgoingBTPieceMessageHandler.this.peer.sendRejectRequest(request);
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPriority()
/*     */     {
/* 133 */       return -1;
/*     */     }
/*     */     
/*     */     public void requestExecuted(long bytes)
/*     */     {
/* 138 */       OutgoingBTPieceMessageHandler.this.adapter.diskRequestCompleted(bytes);
/*     */     }
/*     */   };
/*     */   
/*     */ 
/* 143 */   private final OutgoingMessageQueue.MessageQueueListener sent_message_listener = new OutgoingMessageQueue.MessageQueueListener() {
/* 144 */     public boolean messageAdded(Message message) { return true; }
/*     */     
/*     */     public void messageSent(Message message) {
/* 147 */       if (message.getID().equals("BT_PIECE")) {
/*     */         try {
/* 149 */           OutgoingBTPieceMessageHandler.this.lock_mon.enter();
/*     */           
/*     */ 
/*     */ 
/* 153 */           OutgoingBTPieceMessageHandler.this.queued_messages.remove(message);
/*     */         }
/*     */         finally {
/* 156 */           OutgoingBTPieceMessageHandler.this.lock_mon.exit();
/*     */         }
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
/* 170 */         OutgoingBTPieceMessageHandler.this.doReadAheadLoads();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void messageQueued(Message message) {}
/*     */     
/*     */ 
/*     */     public void messageRemoved(Message message) {}
/*     */     
/*     */     public void protocolBytesSent(int byte_count) {}
/*     */     
/*     */     public void dataBytesSent(int byte_count) {}
/*     */     
/*     */     public void flush() {}
/*     */   };
/*     */   
/*     */   public boolean addPieceRequest(int piece_number, int piece_offset, int length)
/*     */   {
/* 189 */     if (this.destroyed) { return false;
/*     */     }
/* 191 */     DiskManagerReadRequest dmr = this.peer.getManager().getDiskManager().createReadRequest(piece_number, piece_offset, length);
/*     */     try
/*     */     {
/* 194 */       this.lock_mon.enter();
/*     */       
/* 196 */       this.requests.addLast(dmr);
/*     */     }
/*     */     finally {
/* 199 */       this.lock_mon.exit();
/*     */     }
/*     */     
/* 202 */     doReadAheadLoads();
/*     */     
/* 204 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removePieceRequest(int piece_number, int piece_offset, int length)
/*     */   {
/* 215 */     if (this.destroyed) { return;
/*     */     }
/* 217 */     DiskManagerReadRequest dmr = this.peer.getManager().getDiskManager().createReadRequest(piece_number, piece_offset, length);
/*     */     
/* 219 */     boolean inform_rejected = false;
/*     */     try
/*     */     {
/* 222 */       this.lock_mon.enter();
/*     */       
/* 224 */       if (this.requests.contains(dmr)) {
/* 225 */         this.requests.remove(dmr);
/* 226 */         inform_rejected = true; return;
/*     */       }
/*     */       
/*     */ 
/* 230 */       if (this.loading_messages.contains(dmr)) {
/* 231 */         this.loading_messages.remove(dmr);
/* 232 */         inform_rejected = true; return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 237 */       for (i = this.queued_messages.entrySet().iterator(); i.hasNext();) {
/* 238 */         Map.Entry entry = (Map.Entry)i.next();
/* 239 */         if (entry.getValue().equals(dmr)) {
/* 240 */           BTPiece msg = (BTPiece)entry.getKey();
/* 241 */           if (!this.outgoing_message_queue.removeMessage(msg, true)) break;
/* 242 */           inform_rejected = true;
/* 243 */           i.remove(); break;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       Iterator i;
/* 250 */       this.lock_mon.exit();
/*     */       
/* 252 */       if (inform_rejected)
/*     */       {
/* 254 */         this.peer.sendRejectRequest(dmr);
/*     */       }
/*     */     }
/*     */     
/* 258 */     this.outgoing_message_queue.doListenerNotifications();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeAllPieceRequests()
/*     */   {
/* 267 */     if (this.destroyed) { return;
/*     */     }
/* 269 */     List<DiskManagerReadRequest> removed = new ArrayList();
/*     */     try
/*     */     {
/* 272 */       this.lock_mon.enter();
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
/* 297 */       for (Iterator<BTPiece> i = this.queued_messages.keySet().iterator(); i.hasNext();) {
/* 298 */         BTPiece msg = (BTPiece)i.next();
/* 299 */         if (this.outgoing_message_queue.removeMessage(msg, true)) {
/* 300 */           removed.add(this.queued_messages.get(msg));
/*     */         }
/*     */       }
/*     */       
/* 304 */       this.queued_messages.clear();
/*     */       
/* 306 */       removed.addAll(this.requests);
/*     */       
/* 308 */       this.requests.clear();
/*     */       
/* 310 */       removed.addAll(this.loading_messages);
/*     */       
/* 312 */       this.loading_messages.clear();
/*     */     }
/*     */     finally {
/* 315 */       this.lock_mon.exit();
/*     */     }
/*     */     
/* 318 */     for (DiskManagerReadRequest request : removed)
/*     */     {
/* 320 */       this.peer.sendRejectRequest(request);
/*     */     }
/*     */     
/* 323 */     this.outgoing_message_queue.doListenerNotifications();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRequestReadAhead(int num_to_read_ahead)
/*     */   {
/* 329 */     this.request_read_ahead = num_to_read_ahead;
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/*     */     try
/*     */     {
/* 336 */       this.lock_mon.enter();
/*     */       
/* 338 */       removeAllPieceRequests();
/*     */       
/* 340 */       this.queued_messages.clear();
/*     */       
/* 342 */       this.destroyed = true;
/*     */       
/* 344 */       this.outgoing_message_queue.cancelQueueListener(this.sent_message_listener);
/*     */     }
/*     */     finally {
/* 347 */       this.lock_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private void doReadAheadLoads()
/*     */   {
/* 353 */     List to_submit = null;
/*     */     try {
/* 355 */       this.lock_mon.enter();
/*     */       
/* 357 */       while ((this.loading_messages.size() + this.queued_messages.size() < this.request_read_ahead) && (!this.requests.isEmpty()) && (!this.destroyed)) {
/* 358 */         DiskManagerReadRequest dmr = (DiskManagerReadRequest)this.requests.removeFirst();
/* 359 */         this.loading_messages.add(dmr);
/* 360 */         if (to_submit == null) to_submit = new ArrayList();
/* 361 */         to_submit.add(dmr);
/*     */       }
/*     */     } finally {
/* 364 */       this.lock_mon.exit();
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
/* 375 */     if (to_submit != null) {
/* 376 */       for (int i = 0; i < to_submit.size(); i++) {
/* 377 */         this.peer.getManager().getAdapter().enqueueReadRequest(this.peer, (DiskManagerReadRequest)to_submit.get(i), this.read_req_listener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] getRequestedPieceNumbers()
/*     */   {
/* 388 */     if (this.destroyed) { return new int[0];
/*     */     }
/*     */     
/* 391 */     int iLastNumber = -1;
/* 392 */     int pos = 0;
/*     */     int[] pieceNumbers;
/*     */     try
/*     */     {
/* 396 */       this.lock_mon.enter();
/*     */       
/*     */ 
/* 399 */       pieceNumbers = new int[this.queued_messages.size() + this.loading_messages.size() + this.requests.size()];
/*     */       
/* 401 */       for (Iterator iter = this.queued_messages.keySet().iterator(); iter.hasNext();) {
/* 402 */         BTPiece msg = (BTPiece)iter.next();
/* 403 */         if (iLastNumber != msg.getPieceNumber()) {
/* 404 */           iLastNumber = msg.getPieceNumber();
/* 405 */           pieceNumbers[(pos++)] = iLastNumber;
/*     */         }
/*     */       }
/*     */       
/* 409 */       for (Iterator iter = this.loading_messages.iterator(); iter.hasNext();) {
/* 410 */         DiskManagerReadRequest dmr = (DiskManagerReadRequest)iter.next();
/* 411 */         if (iLastNumber != dmr.getPieceNumber()) {
/* 412 */           iLastNumber = dmr.getPieceNumber();
/* 413 */           pieceNumbers[(pos++)] = iLastNumber;
/*     */         }
/*     */       }
/*     */       
/* 417 */       for (iter = this.requests.iterator(); iter.hasNext();) {
/* 418 */         DiskManagerReadRequest dmr = (DiskManagerReadRequest)iter.next();
/* 419 */         if (iLastNumber != dmr.getPieceNumber()) {
/* 420 */           iLastNumber = dmr.getPieceNumber();
/* 421 */           pieceNumbers[(pos++)] = iLastNumber;
/*     */         }
/*     */       }
/*     */     } finally {
/*     */       Iterator iter;
/* 426 */       this.lock_mon.exit();
/*     */     }
/*     */     
/* 429 */     int[] trimmed = new int[pos];
/* 430 */     System.arraycopy(pieceNumbers, 0, trimmed, 0, pos);
/*     */     
/* 432 */     return trimmed;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRequestCount()
/*     */   {
/* 438 */     return this.queued_messages.size() + this.loading_messages.size() + this.requests.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStalledPendingLoad()
/*     */   {
/* 444 */     return (this.queued_messages.size() == 0) && (this.loading_messages.size() > 0);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/OutgoingBTPieceMessageHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */