/*     */ package org.gudy.azureus2.pluginsimpl.local.peers;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportBase;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
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
/*     */ public class PeerForeignNetworkConnection
/*     */   extends NetworkConnectionHelper
/*     */ {
/*     */   private final PeerForeignDelegate delegate;
/*     */   private final Peer peer;
/*  49 */   private OutgoingMessageQueue outgoing_message_queue = new omq();
/*  50 */   private IncomingMessageQueue incoming_message_queue = new imq();
/*     */   
/*  52 */   private TransportBase transport_base = new tp();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PeerForeignNetworkConnection(PeerForeignDelegate _delegate, Peer _peer)
/*     */   {
/*  59 */     this.delegate = _delegate;
/*  60 */     this.peer = _peer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectionEndpoint getEndpoint()
/*     */   {
/*  68 */     return new ConnectionEndpoint(new InetSocketAddress(this.peer.getIp(), this.peer.getPort()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void notifyOfException(Throwable error)
/*     */   {
/*  75 */     Debug.printStackTrace(error);
/*     */   }
/*     */   
/*     */ 
/*     */   public OutgoingMessageQueue getOutgoingMessageQueue()
/*     */   {
/*  81 */     return this.outgoing_message_queue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IncomingMessageQueue getIncomingMessageQueue()
/*     */   {
/*  88 */     return this.incoming_message_queue;
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportBase getTransportBase()
/*     */   {
/*  94 */     return this.transport_base;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMssSize()
/*     */   {
/* 100 */     return NetworkManager.getMinMssSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIncoming()
/*     */   {
/* 106 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLANLocal()
/*     */   {
/* 112 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 118 */     String peer_str = this.peer.getClass().getName();
/*     */     
/* 120 */     int pos = peer_str.lastIndexOf('.');
/*     */     
/* 122 */     if (pos != -1)
/*     */     {
/* 124 */       peer_str = peer_str.substring(pos + 1);
/*     */     }
/*     */     
/* 127 */     peer_str = peer_str + " " + this.peer.getIp() + ":" + this.peer.getPort();
/*     */     
/* 129 */     return "peer=" + peer_str + ",in=" + this.incoming_message_queue.getPercentDoneOfCurrentMessage() + ",out=" + this.outgoing_message_queue.getTotalSize();
/*     */   }
/*     */   
/*     */ 
/*     */   protected class tp
/*     */     implements TransportBase
/*     */   {
/* 136 */     private long last_ready_for_read = SystemTime.getSteppedMonotonousTime();
/*     */     
/*     */     protected tp() {}
/*     */     
/*     */     public boolean isReadyForWrite(EventWaiter waiter)
/*     */     {
/* 142 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long isReadyForRead(EventWaiter waiter)
/*     */     {
/* 149 */       long now = SystemTime.getSteppedMonotonousTime();
/*     */       
/* 151 */       if (PeerForeignNetworkConnection.this.peer.isTransferAvailable())
/*     */       {
/* 153 */         this.last_ready_for_read = now;
/*     */         
/* 155 */         return 0L;
/*     */       }
/*     */       
/* 158 */       long diff = now - this.last_ready_for_read + 1L;
/*     */       
/* 160 */       return diff;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isTCP()
/*     */     {
/* 168 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getDescription()
/*     */     {
/* 174 */       return "Peer transport delegate";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class imq
/*     */     implements IncomingMessageQueue
/*     */   {
/*     */     protected imq() {}
/*     */     
/*     */ 
/*     */     public void setDecoder(MessageStreamDecoder new_stream_decoder) {}
/*     */     
/*     */ 
/*     */     public MessageStreamDecoder getDecoder()
/*     */     {
/* 191 */       throw new RuntimeException("Not imp");
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPercentDoneOfCurrentMessage()
/*     */     {
/* 197 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public int[] receiveFromTransport(int max_bytes, boolean protocol_is_free)
/*     */       throws IOException
/*     */     {
/* 204 */       return new int[] { PeerForeignNetworkConnection.this.peer.readBytes(PeerForeignNetworkConnection.this.delegate.isDownloadDisabled() ? 0 : max_bytes), 0 };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void notifyOfExternallyReceivedMessage(Message message) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void resumeQueueProcessing() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void registerQueueListener(IncomingMessageQueue.MessageQueueListener listener) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void cancelQueueListener(IncomingMessageQueue.MessageQueueListener listener) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void destroy() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected class omq
/*     */     implements OutgoingMessageQueue
/*     */   {
/*     */     protected omq() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setTransport(Transport _transport) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public int getMssSize()
/*     */     {
/* 250 */       return PeerForeignNetworkConnection.this.getMssSize();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setEncoder(MessageStreamEncoder stream_encoder) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public MessageStreamEncoder getEncoder()
/*     */     {
/* 262 */       throw new RuntimeException("Not imp");
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPercentDoneOfCurrentMessage()
/*     */     {
/* 268 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void destroy() {}
/*     */     
/*     */ 
/*     */ 
/*     */     public void flush() {}
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isDestroyed()
/*     */     {
/* 283 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getTotalSize()
/*     */     {
/* 289 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getDataQueuedBytes()
/*     */     {
/* 295 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getProtocolQueuedBytes()
/*     */     {
/* 301 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean getPriorityBoost()
/*     */     {
/* 307 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setPriorityBoost(boolean boost) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isBlocked()
/*     */     {
/* 319 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean hasUrgentMessage()
/*     */     {
/* 325 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public Message peekFirstMessage()
/*     */     {
/* 331 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void addMessage(Message message, boolean manual_listener_notify)
/*     */     {
/* 339 */       throw new RuntimeException("Not imp");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void removeMessagesOfType(Message[] message_types, boolean manual_listener_notify)
/*     */     {
/* 347 */       throw new RuntimeException("Not imp");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean removeMessage(Message message, boolean manual_listener_notify)
/*     */     {
/* 355 */       throw new RuntimeException("Not imp");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int[] deliverToTransport(int max_bytes, boolean protocol_is_free, boolean manual_listener_notify)
/*     */       throws IOException
/*     */     {
/* 366 */       throw new RuntimeException("Not imp");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void doListenerNotifications() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setTrace(boolean on) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public String getQueueTrace()
/*     */     {
/* 383 */       return "";
/*     */     }
/*     */     
/*     */     public void registerQueueListener(OutgoingMessageQueue.MessageQueueListener listener) {}
/*     */     
/*     */     public void cancelQueueListener(OutgoingMessageQueue.MessageQueueListener listener) {}
/*     */     
/*     */     public void notifyOfExternallySentMessage(Message message) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/peers/PeerForeignNetworkConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */