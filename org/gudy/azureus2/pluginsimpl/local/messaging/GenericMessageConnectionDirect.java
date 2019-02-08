/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection.ConnectionListener;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageException;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
/*     */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
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
/*     */ public class GenericMessageConnectionDirect
/*     */   implements GenericMessageConnectionAdapter
/*     */ {
/*     */   public static final int MAX_MESSAGE_SIZE = 262144;
/*     */   private GenericMessageConnectionImpl owner;
/*     */   private String msg_id;
/*     */   private String msg_desc;
/*     */   private int stream_crypto;
/*     */   private byte[][] shared_secrets;
/*     */   private GenericMessageEndpointImpl endpoint;
/*     */   private NetworkConnection connection;
/*     */   private volatile boolean connected;
/*     */   private boolean processing;
/*     */   private volatile boolean closed;
/*     */   private List<LimitedRateGroup> inbound_rls;
/*     */   private List<LimitedRateGroup> outbound_rls;
/*     */   
/*     */   protected static GenericMessageConnectionDirect receive(GenericMessageEndpointImpl endpoint, String msg_id, String msg_desc, int stream_crypto, byte[][] shared_secrets)
/*     */   {
/*  60 */     GenericMessageConnectionDirect direct_connection = new GenericMessageConnectionDirect(msg_id, msg_desc, endpoint, stream_crypto, shared_secrets);
/*     */     
/*  62 */     return direct_connection;
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
/*     */ 
/*     */   protected GenericMessageConnectionDirect(String _msg_id, String _msg_desc, GenericMessageEndpointImpl _endpoint, int _stream_crypto, byte[][] _shared_secrets)
/*     */   {
/*  90 */     this.msg_id = _msg_id;
/*  91 */     this.msg_desc = _msg_desc;
/*  92 */     this.endpoint = _endpoint;
/*  93 */     this.stream_crypto = _stream_crypto;
/*  94 */     this.shared_secrets = _shared_secrets;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOwner(GenericMessageConnectionImpl _owner)
/*     */   {
/* 101 */     this.owner = _owner;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumMessageSize()
/*     */   {
/* 107 */     return 262144;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 113 */     if (this.connection == null)
/*     */     {
/* 115 */       return "";
/*     */     }
/*     */     
/*     */ 
/* 119 */     Transport transport = this.connection.getTransport();
/*     */     
/* 121 */     if (transport == null)
/*     */     {
/* 123 */       return "";
/*     */     }
/*     */     
/* 126 */     return transport.getEncryption(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getTransportType()
/*     */   {
/* 133 */     if (this.connection == null)
/*     */     {
/* 135 */       return 0;
/*     */     }
/*     */     
/*     */ 
/* 139 */     Transport t = this.connection.getTransport();
/*     */     
/* 141 */     if (t == null)
/*     */     {
/* 143 */       return 0;
/*     */     }
/*     */     
/*     */ 
/* 147 */     if (t.isTCP())
/*     */     {
/* 149 */       return 0;
/*     */     }
/*     */     
/*     */ 
/* 153 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addInboundRateLimiter(RateLimiter _limiter)
/*     */   {
/* 163 */     LimitedRateGroup limiter = UtilitiesImpl.wrapLimiter(_limiter, false);
/*     */     
/* 165 */     synchronized (this)
/*     */     {
/* 167 */       if (this.processing)
/*     */       {
/* 169 */         this.connection.addRateLimiter(limiter, false);
/*     */       }
/*     */       else
/*     */       {
/* 173 */         if (this.inbound_rls == null)
/*     */         {
/* 175 */           this.inbound_rls = new ArrayList();
/*     */         }
/*     */         
/* 178 */         this.inbound_rls.add(limiter);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeInboundRateLimiter(RateLimiter _limiter)
/*     */   {
/* 187 */     LimitedRateGroup limiter = UtilitiesImpl.wrapLimiter(_limiter, false);
/*     */     
/* 189 */     synchronized (this)
/*     */     {
/* 191 */       if (this.processing)
/*     */       {
/* 193 */         this.connection.removeRateLimiter(limiter, false);
/*     */ 
/*     */ 
/*     */       }
/* 197 */       else if (this.inbound_rls != null)
/*     */       {
/* 199 */         this.inbound_rls.remove(limiter);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addOutboundRateLimiter(RateLimiter _limiter)
/*     */   {
/* 209 */     LimitedRateGroup limiter = UtilitiesImpl.wrapLimiter(_limiter, false);
/*     */     
/* 211 */     synchronized (this)
/*     */     {
/* 213 */       if (this.processing)
/*     */       {
/* 215 */         this.connection.addRateLimiter(limiter, true);
/*     */       }
/*     */       else
/*     */       {
/* 219 */         if (this.outbound_rls == null)
/*     */         {
/* 221 */           this.outbound_rls = new ArrayList();
/*     */         }
/*     */         
/* 224 */         this.outbound_rls.add(limiter);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeOutboundRateLimiter(RateLimiter _limiter)
/*     */   {
/* 233 */     LimitedRateGroup limiter = UtilitiesImpl.wrapLimiter(_limiter, false);
/*     */     
/* 235 */     synchronized (this)
/*     */     {
/* 237 */       if (this.processing)
/*     */       {
/* 239 */         this.connection.removeRateLimiter(limiter, true);
/*     */ 
/*     */ 
/*     */       }
/* 243 */       else if (this.outbound_rls != null)
/*     */       {
/* 245 */         this.outbound_rls.remove(limiter);
/*     */       }
/*     */     }
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
/*     */   protected void connect(NetworkConnection _connection)
/*     */   {
/* 260 */     this.connection = _connection;
/*     */     
/* 262 */     this.connection.connect(3, new NetworkConnection.ConnectionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public int connectStarted(int default_connect_timeout)
/*     */       {
/*     */ 
/*     */ 
/* 270 */         return default_connect_timeout;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectSuccess(ByteBuffer remaining_initial_data)
/*     */       {
/* 277 */         GenericMessageConnectionDirect.this.connected = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectFailure(Throwable failure_msg)
/*     */       {
/* 284 */         GenericMessageConnectionDirect.this.owner.reportFailed(failure_msg);
/*     */         
/* 286 */         GenericMessageConnectionDirect.this.connection.close(failure_msg == null ? null : Debug.getNestedExceptionMessage(failure_msg));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void exceptionThrown(Throwable error)
/*     */       {
/* 293 */         GenericMessageConnectionDirect.this.owner.reportFailed(error);
/*     */         
/* 295 */         GenericMessageConnectionDirect.this.connection.close(error == null ? null : Debug.getNestedExceptionMessage(error));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public Object getConnectionProperty(String property_name)
/*     */       {
/* 302 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public String getDescription()
/*     */       {
/* 310 */         return "generic connection: " + GenericMessageConnectionDirect.this.endpoint.getNotionalAddress();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void accepted()
/*     */   {
/* 318 */     startProcessing();
/*     */   }
/*     */   
/*     */ 
/*     */   public GenericMessageEndpoint getEndpoint()
/*     */   {
/* 324 */     return this.endpoint;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connect(ByteBuffer upper_initial_data, final GenericMessageConnectionAdapter.ConnectionListener listener)
/*     */   {
/* 332 */     if (this.connected)
/*     */     {
/* 334 */       return;
/*     */     }
/*     */     
/* 337 */     ConnectionEndpoint cep = this.endpoint.getConnectionEndpoint();
/*     */     
/* 339 */     cep = cep.getLANAdjustedEndpoint();
/*     */     
/* 341 */     this.connection = NetworkManager.getSingleton().createConnection(cep, new GenericMessageEncoder(), new GenericMessageDecoder(this.msg_id, this.msg_desc), this.stream_crypto != 1, this.stream_crypto != 3, this.shared_secrets);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 350 */     ByteBuffer initial_data = ByteBuffer.wrap(this.msg_id.getBytes());
/*     */     
/* 352 */     if (upper_initial_data != null)
/*     */     {
/* 354 */       GenericMessage gm = new GenericMessage(this.msg_id, this.msg_desc, new DirectByteBuffer(upper_initial_data), false);
/*     */       
/* 356 */       DirectByteBuffer[] payload = new GenericMessageEncoder().encodeMessage(gm)[0].getRawData();
/*     */       
/* 358 */       int size = initial_data.remaining();
/*     */       
/* 360 */       for (int i = 0; i < payload.length; i++)
/*     */       {
/* 362 */         size += payload[i].remaining((byte)11);
/*     */       }
/*     */       
/* 365 */       ByteBuffer temp = ByteBuffer.allocate(size);
/*     */       
/* 367 */       temp.put(initial_data);
/*     */       
/* 369 */       for (int i = 0; i < payload.length; i++)
/*     */       {
/* 371 */         temp.put(payload[i].getBuffer((byte)11));
/*     */       }
/*     */       
/* 374 */       temp.rewind();
/*     */       
/* 376 */       initial_data = temp;
/*     */     }
/*     */     
/* 379 */     this.connection.connect(initial_data, 3, new NetworkConnection.ConnectionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public int connectStarted(int default_connect_timeout)
/*     */       {
/*     */ 
/*     */ 
/* 388 */         return default_connect_timeout;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectSuccess(ByteBuffer remaining_initial_data)
/*     */       {
/* 395 */         GenericMessageConnectionDirect.this.connected = true;
/*     */         
/*     */         try
/*     */         {
/* 399 */           if ((remaining_initial_data != null) && (remaining_initial_data.remaining() > 0))
/*     */           {
/*     */ 
/*     */ 
/* 403 */             GenericMessageConnectionDirect.this.connection.getOutgoingMessageQueue().addMessage(new GenericMessage(GenericMessageConnectionDirect.this.msg_id, GenericMessageConnectionDirect.this.msg_desc, new DirectByteBuffer(remaining_initial_data), true), false);
/*     */           }
/*     */           
/*     */ 
/* 407 */           listener.connectSuccess();
/*     */           
/* 409 */           GenericMessageConnectionDirect.this.startProcessing();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 413 */           connectFailure(e);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectFailure(Throwable failure_msg)
/*     */       {
/* 421 */         listener.connectFailure(failure_msg);
/*     */         
/* 423 */         GenericMessageConnectionDirect.this.connection.close(failure_msg == null ? null : Debug.getNestedExceptionMessage(failure_msg));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void exceptionThrown(Throwable error)
/*     */       {
/* 430 */         listener.connectFailure(error);
/*     */         
/* 432 */         GenericMessageConnectionDirect.this.connection.close(error == null ? null : Debug.getNestedExceptionMessage(error));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public Object getConnectionProperty(String property_name)
/*     */       {
/* 439 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public String getDescription()
/*     */       {
/* 445 */         return "generic connection";
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void startProcessing()
/*     */   {
/* 453 */     this.connection.getIncomingMessageQueue().registerQueueListener(new IncomingMessageQueue.MessageQueueListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean messageReceived(Message _message)
/*     */       {
/*     */ 
/* 460 */         GenericMessage message = (GenericMessage)_message;
/*     */         
/* 462 */         GenericMessageConnectionDirect.this.owner.receive(message);
/*     */         
/* 464 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void protocolBytesReceived(int byte_count) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void dataBytesReceived(int byte_count) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean isPriority()
/*     */       {
/* 482 */         return false;
/*     */       }
/*     */       
/* 485 */     });
/* 486 */     this.connection.getOutgoingMessageQueue().registerQueueListener(new OutgoingMessageQueue.MessageQueueListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean messageAdded(Message message)
/*     */       {
/*     */ 
/*     */ 
/* 495 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void messageQueued(Message message) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void messageRemoved(Message message) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void messageSent(Message message) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void protocolBytesSent(int byte_count) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void dataBytesSent(int byte_count) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void flush() {}
/* 534 */     });
/* 535 */     this.connection.startMessageProcessing();
/*     */     
/* 537 */     this.connection.enableEnhancedMessageProcessing(true, -1);
/*     */     
/* 539 */     synchronized (this)
/*     */     {
/* 541 */       if (this.inbound_rls != null)
/*     */       {
/* 543 */         for (int i = 0; i < this.inbound_rls.size(); i++)
/*     */         {
/* 545 */           this.connection.addRateLimiter((LimitedRateGroup)this.inbound_rls.get(i), false);
/*     */         }
/*     */         
/* 548 */         this.inbound_rls = null;
/*     */       }
/*     */       
/* 551 */       if (this.outbound_rls != null)
/*     */       {
/* 553 */         for (int i = 0; i < this.outbound_rls.size(); i++)
/*     */         {
/* 555 */           this.connection.addRateLimiter((LimitedRateGroup)this.outbound_rls.get(i), true);
/*     */         }
/*     */         
/* 558 */         this.outbound_rls = null;
/*     */       }
/*     */       
/* 561 */       this.processing = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void send(PooledByteBuffer data)
/*     */     throws MessageException
/*     */   {
/* 571 */     if (!this.connected)
/*     */     {
/* 573 */       throw new MessageException("not connected");
/*     */     }
/*     */     
/* 576 */     PooledByteBufferImpl impl = (PooledByteBufferImpl)data;
/*     */     try
/*     */     {
/* 579 */       this.connection.getOutgoingMessageQueue().addMessage(new GenericMessage(this.msg_id, this.msg_desc, impl.getBuffer(), false), false);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 584 */       throw new MessageException("send failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws MessageException
/*     */   {
/* 593 */     if (!this.connected)
/*     */     {
/* 595 */       throw new MessageException("not connected");
/*     */     }
/*     */     
/* 598 */     if (!this.closed)
/*     */     {
/* 600 */       this.closed = true;
/*     */       
/* 602 */       this.connection.close(null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessageConnectionDirect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */