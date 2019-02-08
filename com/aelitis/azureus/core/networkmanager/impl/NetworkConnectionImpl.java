/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionAttempt;
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection.ConnectionListener;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportBase;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
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
/*     */ public class NetworkConnectionImpl
/*     */   extends NetworkConnectionHelper
/*     */   implements NetworkConnection
/*     */ {
/*     */   private final ConnectionEndpoint connection_endpoint;
/*     */   private final boolean is_incoming;
/*     */   private boolean connect_with_crypto;
/*     */   private boolean allow_fallback;
/*     */   private byte[][] shared_secrets;
/*     */   private NetworkConnection.ConnectionListener connection_listener;
/*     */   private boolean is_connected;
/*  55 */   private byte is_lan_local = 0;
/*     */   
/*     */ 
/*     */   private final OutgoingMessageQueueImpl outgoing_message_queue;
/*     */   
/*     */ 
/*     */   private final IncomingMessageQueueImpl incoming_message_queue;
/*     */   
/*     */ 
/*     */   private Transport transport;
/*     */   
/*     */ 
/*     */   private volatile ConnectionAttempt connection_attempt;
/*     */   
/*     */ 
/*     */   private volatile boolean closed;
/*     */   
/*     */ 
/*     */   private Map<Object, Object> user_data;
/*     */   
/*     */ 
/*     */ 
/*     */   public NetworkConnectionImpl(ConnectionEndpoint _target, MessageStreamEncoder encoder, MessageStreamDecoder decoder, boolean _connect_with_crypto, boolean _allow_fallback, byte[][] _shared_secrets)
/*     */   {
/*  79 */     this.connection_endpoint = _target;
/*  80 */     this.is_incoming = false;
/*  81 */     this.connect_with_crypto = _connect_with_crypto;
/*  82 */     this.allow_fallback = _allow_fallback;
/*  83 */     this.shared_secrets = _shared_secrets;
/*     */     
/*     */ 
/*  86 */     this.is_connected = false;
/*  87 */     this.outgoing_message_queue = new OutgoingMessageQueueImpl(encoder);
/*  88 */     this.incoming_message_queue = new IncomingMessageQueueImpl(decoder, this);
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
/*     */   public NetworkConnectionImpl(Transport _transport, MessageStreamEncoder encoder, MessageStreamDecoder decoder)
/*     */   {
/* 101 */     this.transport = _transport;
/* 102 */     this.connection_endpoint = this.transport.getTransportEndpoint().getProtocolEndpoint().getConnectionEndpoint();
/* 103 */     this.is_incoming = true;
/* 104 */     this.is_connected = true;
/* 105 */     this.outgoing_message_queue = new OutgoingMessageQueueImpl(encoder);
/* 106 */     this.outgoing_message_queue.setTransport(this.transport);
/* 107 */     this.incoming_message_queue = new IncomingMessageQueueImpl(decoder, this);
/*     */     
/* 109 */     this.transport.bindConnection(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ConnectionEndpoint getEndpoint()
/*     */   {
/* 116 */     return this.connection_endpoint;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIncoming()
/*     */   {
/* 122 */     return this.is_incoming;
/*     */   }
/*     */   
/*     */   public void connect(int priority, NetworkConnection.ConnectionListener listener) {
/* 126 */     connect(null, priority, listener);
/*     */   }
/*     */   
/*     */   public void connect(ByteBuffer initial_outbound_data, int priority, NetworkConnection.ConnectionListener listener) {
/* 130 */     this.connection_listener = listener;
/*     */     
/* 132 */     if (this.is_connected)
/*     */     {
/* 134 */       this.connection_listener.connectStarted(-1);
/*     */       
/* 136 */       this.connection_listener.connectSuccess(initial_outbound_data);
/*     */       
/* 138 */       return;
/*     */     }
/*     */     
/* 141 */     if (this.connection_attempt != null)
/*     */     {
/* 143 */       Debug.out("Connection attempt already active");
/*     */       
/* 145 */       listener.connectFailure(new Throwable("Connection attempt already active"));
/*     */       
/* 147 */       return;
/*     */     }
/*     */     
/* 150 */     this.connection_attempt = this.connection_endpoint.connectOutbound(this.connect_with_crypto, this.allow_fallback, this.shared_secrets, initial_outbound_data, priority, new Transport.ConnectListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public int connectAttemptStarted(int default_connect_timeout)
/*     */       {
/*     */ 
/*     */ 
/* 159 */         return NetworkConnectionImpl.this.connection_listener.connectStarted(default_connect_timeout);
/*     */       }
/*     */       
/*     */       public void connectSuccess(Transport _transport, ByteBuffer remaining_initial_data) {
/* 163 */         NetworkConnectionImpl.this.is_connected = true;
/* 164 */         NetworkConnectionImpl.this.transport = _transport;
/* 165 */         NetworkConnectionImpl.this.outgoing_message_queue.setTransport(NetworkConnectionImpl.this.transport);
/* 166 */         NetworkConnectionImpl.this.transport.bindConnection(NetworkConnectionImpl.this);
/* 167 */         NetworkConnectionImpl.this.connection_listener.connectSuccess(remaining_initial_data);
/* 168 */         NetworkConnectionImpl.this.connection_attempt = null;
/*     */       }
/*     */       
/*     */       public void connectFailure(Throwable failure_msg) {
/* 172 */         NetworkConnectionImpl.this.is_connected = false;
/* 173 */         NetworkConnectionImpl.this.connection_listener.connectFailure(failure_msg);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public Object getConnectionProperty(String property_name)
/*     */       {
/* 180 */         return NetworkConnectionImpl.this.connection_listener.getConnectionProperty(property_name);
/*     */       }
/*     */     });
/*     */     
/* 184 */     if (this.closed)
/*     */     {
/* 186 */       ConnectionAttempt ca = this.connection_attempt;
/*     */       
/* 188 */       if (ca != null)
/*     */       {
/* 190 */         ca.abandon();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Transport detachTransport()
/*     */   {
/* 198 */     Transport t = this.transport;
/*     */     
/* 200 */     if (t != null)
/*     */     {
/* 202 */       t.unbindConnection(this);
/*     */     }
/*     */     
/* 205 */     this.transport = new bogusTransport(this.transport);
/*     */     
/* 207 */     close("detached transport");
/*     */     
/* 209 */     return t;
/*     */   }
/*     */   
/*     */   public void close(String reason) {
/* 213 */     NetworkManager.getSingleton().stopTransferProcessing(this);
/* 214 */     this.closed = true;
/* 215 */     if (this.connection_attempt != null) {
/* 216 */       this.connection_attempt.abandon();
/*     */     }
/* 218 */     if (this.transport != null) {
/* 219 */       this.transport.close("Tidy close" + ((reason == null) || (reason.length() == 0) ? "" : new StringBuilder().append(": ").append(reason).toString()));
/*     */     }
/* 221 */     this.incoming_message_queue.destroy();
/* 222 */     this.outgoing_message_queue.destroy();
/* 223 */     this.is_connected = false;
/*     */   }
/*     */   
/*     */   public void notifyOfException(Throwable error)
/*     */   {
/* 228 */     if (this.connection_listener != null) {
/* 229 */       this.connection_listener.exceptionThrown(error);
/*     */     }
/*     */     else {
/* 232 */       Debug.out("notifyOfException():: connection_listener == null for exception: " + error.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 237 */   public OutgoingMessageQueue getOutgoingMessageQueue() { return this.outgoing_message_queue; }
/*     */   
/* 239 */   public IncomingMessageQueue getIncomingMessageQueue() { return this.incoming_message_queue; }
/*     */   
/*     */ 
/*     */ 
/*     */   public void startMessageProcessing()
/*     */   {
/* 245 */     NetworkManager.getSingleton().startTransferProcessing(this);
/*     */   }
/*     */   
/*     */   public void enableEnhancedMessageProcessing(boolean enable, int partition_id)
/*     */   {
/* 250 */     if (enable) {
/* 251 */       NetworkManager.getSingleton().upgradeTransferProcessing(this, partition_id);
/*     */     } else {
/* 253 */       NetworkManager.getSingleton().downgradeTransferProcessing(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 258 */   public Transport getTransport() { return this.transport; }
/*     */   
/* 260 */   public TransportBase getTransportBase() { return this.transport; }
/*     */   
/*     */ 
/*     */   public int getMssSize()
/*     */   {
/* 265 */     if (this.transport == null)
/*     */     {
/* 267 */       return NetworkManager.getMinMssSize();
/*     */     }
/*     */     
/*     */ 
/* 271 */     return this.transport.getMssSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object setUserData(Object key, Object value)
/*     */   {
/* 281 */     synchronized (this) {
/* 282 */       if (this.user_data == null) {
/* 283 */         this.user_data = new LightHashMap();
/*     */       }
/*     */       
/* 286 */       return this.user_data.put(key, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getUserData(Object key)
/*     */   {
/* 294 */     synchronized (this) {
/* 295 */       if (this.user_data == null) {
/* 296 */         return null;
/*     */       }
/*     */       
/* 299 */       return this.user_data.get(key);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 304 */     return this.transport == null ? this.connection_endpoint.getDescription() : this.transport.getDescription();
/*     */   }
/*     */   
/*     */   public boolean isConnected()
/*     */   {
/* 309 */     return this.is_connected;
/*     */   }
/*     */   
/*     */   public boolean isLANLocal()
/*     */   {
/* 314 */     if (this.is_lan_local == 0)
/*     */     {
/* 316 */       this.is_lan_local = AddressUtils.isLANLocalAddress(this.connection_endpoint.getNotionalAddress());
/*     */     }
/* 318 */     return this.is_lan_local == 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 324 */     return "tran=" + (this.transport == null ? "null" : new StringBuilder().append(this.transport.getDescription()).append(",w_ready=").append(this.transport.isReadyForWrite(null)).append(",r_ready=").append(this.transport.isReadyForRead(null)).toString()) + ",in=" + this.incoming_message_queue.getPercentDoneOfCurrentMessage() + ",out=" + (this.outgoing_message_queue == null ? 0 : this.outgoing_message_queue.getTotalSize()) + ",owner=" + (this.connection_listener == null ? "null" : this.connection_listener.getDescription());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class bogusTransport
/*     */     implements Transport
/*     */   {
/*     */     private final Transport transport;
/*     */     
/*     */ 
/*     */ 
/*     */     protected bogusTransport(Transport _transport)
/*     */     {
/* 338 */       this.transport = _transport;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isReadyForWrite(EventWaiter waiter)
/*     */     {
/* 345 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long isReadyForRead(EventWaiter waiter)
/*     */     {
/* 352 */       return Long.MAX_VALUE;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isTCP()
/*     */     {
/* 358 */       return this.transport.isTCP();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isSOCKS()
/*     */     {
/* 364 */       return this.transport.isSOCKS();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getDescription()
/*     */     {
/* 370 */       return this.transport.getDescription();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getMssSize()
/*     */     {
/* 376 */       return this.transport.getMssSize();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setAlreadyRead(ByteBuffer bytes_already_read)
/*     */     {
/* 383 */       Debug.out("Bogus Transport Operation");
/*     */     }
/*     */     
/*     */ 
/*     */     public TransportEndpoint getTransportEndpoint()
/*     */     {
/* 389 */       return this.transport.getTransportEndpoint();
/*     */     }
/*     */     
/*     */ 
/*     */     public TransportStartpoint getTransportStartpoint()
/*     */     {
/* 395 */       return this.transport.getTransportStartpoint();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isEncrypted()
/*     */     {
/* 401 */       return this.transport.isEncrypted();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getEncryption(boolean verbose)
/*     */     {
/* 407 */       return this.transport.getEncryption(verbose);
/*     */     }
/*     */     
/* 410 */     public String getProtocol() { return this.transport.getProtocol(); }
/*     */     
/*     */ 
/*     */     public void setReadyForRead()
/*     */     {
/* 415 */       Debug.out("Bogus Transport Operation");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long write(ByteBuffer[] buffers, int array_offset, int length)
/*     */       throws IOException
/*     */     {
/* 426 */       Debug.out("Bogus Transport Operation");
/*     */       
/* 428 */       throw new IOException("Bogus transport!");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public long read(ByteBuffer[] buffers, int array_offset, int length)
/*     */       throws IOException
/*     */     {
/* 437 */       Debug.out("Bogus Transport Operation");
/*     */       
/* 439 */       throw new IOException("Bogus transport!");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setTransportMode(int mode)
/*     */     {
/* 446 */       Debug.out("Bogus Transport Operation");
/*     */     }
/*     */     
/*     */ 
/*     */     public int getTransportMode()
/*     */     {
/* 452 */       return this.transport.getTransportMode();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void connectOutbound(ByteBuffer initial_data, Transport.ConnectListener listener, int priority)
/*     */     {
/* 461 */       Debug.out("Bogus Transport Operation");
/*     */       
/* 463 */       listener.connectFailure(new Throwable("Bogus Transport"));
/*     */     }
/*     */     
/*     */ 
/*     */     public void connectedInbound()
/*     */     {
/* 469 */       Debug.out("Bogus Transport Operation");
/*     */     }
/*     */     
/*     */     public void close(String reason) {}
/*     */     
/*     */     public void bindConnection(NetworkConnection connection) {}
/*     */     
/*     */     public void unbindConnection(NetworkConnection connection) {}
/*     */     
/*     */     public void setTrace(boolean on) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/NetworkConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */