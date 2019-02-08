/*     */ package com.aelitis.azureus.core.clientmessageservice.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.OutgoingMessageQueueImpl;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.ProtocolEndpointTCP;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportImpl;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TransportEndpointTCP;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageEncoder;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public class ClientConnection
/*     */ {
/*     */   private Transport parent_transport;
/*     */   private final Transport light_transport;
/*     */   private final OutgoingMessageQueue out_queue;
/*     */   private final AZMessageDecoder decoder;
/*  49 */   private static final AZMessageEncoder encoder = new AZMessageEncoder(0);
/*     */   
/*     */   private long last_activity_time;
/*  52 */   private final AEMonitor msg_mon = new AEMonitor("ClientConnection");
/*  53 */   private final ArrayList sending_msgs = new ArrayList();
/*     */   
/*     */   private Map user_data;
/*     */   
/*     */   private boolean close_pending;
/*     */   private boolean closed;
/*     */   private boolean last_write_made_progress;
/*  60 */   private String debug_string = "<>";
/*     */   
/*     */ 
/*     */   private Throwable closing_reason;
/*     */   
/*     */ 
/*     */ 
/*     */   public ClientConnection(SocketChannel channel)
/*     */   {
/*  69 */     this.decoder = new AZMessageDecoder();
/*     */     
/*  71 */     InetSocketAddress remote = null;
/*     */     
/*  73 */     ProtocolEndpointTCP pe = (ProtocolEndpointTCP)ProtocolEndpointFactory.createEndpoint(1, remote);
/*     */     
/*     */ 
/*  76 */     this.light_transport = pe.connectLightWeight(channel);
/*     */     
/*  78 */     this.out_queue = new OutgoingMessageQueueImpl(encoder);
/*  79 */     this.out_queue.setTransport(this.light_transport);
/*  80 */     this.last_activity_time = System.currentTimeMillis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ClientConnection(TCPTransportImpl transport)
/*     */   {
/*  89 */     this(transport.getSocketChannel());
/*  90 */     this.parent_transport = transport;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message[] readMessages()
/*     */     throws IOException
/*     */   {
/* 101 */     int bytes_read = this.decoder.performStreamDecode(this.light_transport, 1048576);
/* 102 */     if (bytes_read > 0) { this.last_activity_time = System.currentTimeMillis();
/*     */     }
/* 104 */     return this.decoder.removeDecodedMessages();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getLastReadMadeProgress()
/*     */   {
/* 110 */     return this.decoder.getLastReadMadeProgress();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 116 */   public boolean getLastWriteMadeProgress() { return this.last_write_made_progress; }
/*     */   
/*     */   public void sendMessage(final ClientMessage client_msg, final Message msg) {
/*     */     try {
/* 120 */       this.msg_mon.enter();
/* 121 */       this.sending_msgs.add(client_msg);
/*     */     } finally {
/* 123 */       this.msg_mon.exit();
/*     */     }
/* 125 */     this.out_queue.registerQueueListener(new OutgoingMessageQueue.MessageQueueListener() {
/* 126 */       public boolean messageAdded(Message message) { return true; }
/*     */       
/*     */       public void messageQueued(Message message) {}
/*     */       public void messageRemoved(Message message) {}
/*     */       public void protocolBytesSent(int byte_count) {}
/*     */       public void dataBytesSent(int byte_count) {}
/*     */       public void flush() {}
/* 133 */       public void messageSent(Message message) { if (message.equals(msg)) {
/* 134 */           try { ClientConnection.this.msg_mon.enter();
/* 135 */             ClientConnection.this.sending_msgs.remove(client_msg);
/*     */           } finally {
/* 137 */             ClientConnection.this.msg_mon.exit();
/*     */           }
/* 139 */           client_msg.reportComplete();
/*     */         }
/*     */         
/*     */       }
/* 143 */     });
/* 144 */     this.out_queue.addMessage(msg, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean writeMessages()
/*     */     throws IOException
/*     */   {
/* 155 */     int[] written = this.out_queue.deliverToTransport(1048576, false, false);
/* 156 */     int bytes_written = written[0] + written[1];
/* 157 */     if (bytes_written > 0) { this.last_activity_time = System.currentTimeMillis();
/*     */     }
/* 159 */     this.last_write_made_progress = (bytes_written > 0);
/*     */     
/* 161 */     return this.out_queue.getTotalSize() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close(Throwable reason)
/*     */   {
/* 171 */     ClientMessage[] messages = null;
/*     */     try {
/* 173 */       this.msg_mon.enter();
/* 174 */       if (this.closed) {
/*     */         return;
/*     */       }
/* 177 */       this.closed = true;
/* 178 */       if (!this.sending_msgs.isEmpty()) {
/* 179 */         messages = (ClientMessage[])this.sending_msgs.toArray(new ClientMessage[this.sending_msgs.size()]);
/*     */       }
/*     */     } finally {
/* 182 */       this.msg_mon.exit();
/*     */     }
/* 184 */     if (messages != null) {
/* 185 */       if (reason == null) {
/* 186 */         reason = new Exception("Connection closed");
/*     */       }
/* 188 */       for (int i = 0; i < messages.length; i++) {
/* 189 */         ClientMessage msg = messages[i];
/* 190 */         msg.reportFailed(reason);
/*     */       }
/*     */     }
/*     */     
/* 194 */     this.decoder.destroy();
/* 195 */     this.out_queue.destroy();
/*     */     
/* 197 */     String x = "Tidy close" + (reason == null ? "" : new StringBuilder().append(": ").append(Debug.getNestedExceptionMessage(reason)).toString());
/* 198 */     if (this.parent_transport != null) {
/* 199 */       this.parent_transport.close(x);
/*     */     }
/*     */     else {
/* 202 */       this.light_transport.close(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void closePending()
/*     */   {
/* 214 */     this.last_activity_time = System.currentTimeMillis();
/* 215 */     this.close_pending = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isClosePending()
/*     */   {
/* 221 */     return this.close_pending;
/*     */   }
/*     */   
/* 224 */   public SocketChannel getSocketChannel() { return ((TransportEndpointTCP)this.light_transport.getTransportEndpoint()).getSocketChannel(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLastActivityTime()
/*     */   {
/* 231 */     return this.last_activity_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public void resetLastActivityTime()
/*     */   {
/* 237 */     this.last_activity_time = System.currentTimeMillis();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setClosingReason(Throwable r)
/*     */   {
/* 243 */     this.closing_reason = r;
/*     */   }
/*     */   
/*     */ 
/*     */   public Throwable getClosingReason()
/*     */   {
/* 249 */     return this.closing_reason;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getUserData(Object key)
/*     */   {
/* 256 */     Map m = this.user_data;
/*     */     
/* 258 */     if (m == null)
/*     */     {
/* 260 */       return null;
/*     */     }
/*     */     
/* 263 */     return m.get(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUserData(Object key, Object data)
/*     */   {
/*     */     try
/*     */     {
/* 272 */       this.msg_mon.enter();
/*     */       
/*     */ 
/*     */ 
/* 276 */       Map m = this.user_data == null ? new HashMap() : new HashMap(this.user_data);
/*     */       
/* 278 */       m.put(key, data);
/*     */       
/* 280 */       this.user_data = m;
/*     */     }
/*     */     finally {
/* 283 */       this.msg_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 288 */   public void setDebugString(String debug) { this.debug_string = debug; }
/*     */   
/* 290 */   public String getDebugString() { return this.debug_string; }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaximumMessageSize(int max_bytes)
/*     */   {
/* 296 */     if (this.decoder != null) {
/* 297 */       this.decoder.setMaximumMessageSize(max_bytes);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/impl/ClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */