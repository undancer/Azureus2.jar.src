/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
/*     */ import com.aelitis.azureus.core.nat.NATTraversalObserver;
/*     */ import com.aelitis.azureus.core.nat.NATTraverser;
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageException;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnection;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnectionListener;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
/*     */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
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
/*     */ public class GenericMessageConnectionImpl
/*     */   implements GenericMessageConnection
/*     */ {
/*     */   private static final boolean TRACE = false;
/*     */   private static final boolean TEST_TUNNEL = false;
/*     */   private MessageManagerImpl message_manager;
/*     */   private String msg_id;
/*     */   private String msg_desc;
/*     */   private GenericMessageEndpointImpl endpoint;
/*     */   private int stream_crypto;
/*     */   byte[][] shared_secrets;
/*     */   private boolean incoming;
/*     */   private volatile GenericMessageConnectionAdapter delegate;
/*     */   private volatile boolean closing;
/*     */   private volatile boolean closed;
/*     */   private volatile boolean connecting;
/*  73 */   private List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */   private int connect_method_count;
/*     */   
/*     */   private List inbound_rls;
/*     */   
/*     */   private List outbound_rls;
/*     */   
/*     */ 
/*     */   protected GenericMessageConnectionImpl(MessageManagerImpl _message_manager, GenericMessageConnectionAdapter _delegate)
/*     */   {
/*  85 */     this.message_manager = _message_manager;
/*  86 */     this.delegate = _delegate;
/*     */     
/*  88 */     this.incoming = true;
/*     */     
/*  90 */     this.connect_method_count = 1;
/*     */     
/*  92 */     this.delegate.setOwner(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GenericMessageConnectionImpl(MessageManagerImpl _message_manager, String _msg_id, String _msg_desc, GenericMessageEndpointImpl _endpoint, int _stream_crypto, byte[][] _shared_secrets)
/*     */   {
/* 104 */     this.message_manager = _message_manager;
/* 105 */     this.msg_id = _msg_id;
/* 106 */     this.msg_desc = _msg_desc;
/* 107 */     this.endpoint = _endpoint;
/* 108 */     this.stream_crypto = _stream_crypto;
/* 109 */     this.shared_secrets = _shared_secrets;
/*     */     
/* 111 */     this.connect_method_count = this.endpoint.getConnectionEndpoint().getProtocols().length;
/*     */     
/* 113 */     this.incoming = false;
/*     */   }
/*     */   
/*     */ 
/*     */   public GenericMessageEndpoint getEndpoint()
/*     */   {
/* 119 */     return this.endpoint == null ? this.delegate.getEndpoint() : this.endpoint;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumMessageSize()
/*     */   {
/* 125 */     return this.delegate == null ? 32768 : this.delegate.getMaximumMessageSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 131 */     if (this.delegate == null)
/*     */     {
/* 133 */       return "";
/*     */     }
/*     */     
/*     */ 
/* 137 */     return this.delegate.getType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getTransportType()
/*     */   {
/* 144 */     if (this.delegate == null)
/*     */     {
/* 146 */       return 0;
/*     */     }
/*     */     
/* 149 */     return this.delegate.getTransportType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addInboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 158 */     synchronized (this)
/*     */     {
/* 160 */       if (this.delegate != null)
/*     */       {
/* 162 */         this.delegate.addInboundRateLimiter(limiter);
/*     */       }
/*     */       else
/*     */       {
/* 166 */         if (this.inbound_rls == null)
/*     */         {
/* 168 */           this.inbound_rls = new ArrayList();
/*     */         }
/*     */         
/* 171 */         this.inbound_rls.add(limiter);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeInboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 180 */     synchronized (this)
/*     */     {
/* 182 */       if (this.delegate != null)
/*     */       {
/* 184 */         this.delegate.removeInboundRateLimiter(limiter);
/*     */ 
/*     */ 
/*     */       }
/* 188 */       else if (this.inbound_rls != null)
/*     */       {
/* 190 */         this.inbound_rls.remove(limiter);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addOutboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 200 */     synchronized (this)
/*     */     {
/* 202 */       if (this.delegate != null)
/*     */       {
/* 204 */         this.delegate.addOutboundRateLimiter(limiter);
/*     */       }
/*     */       else
/*     */       {
/* 208 */         if (this.outbound_rls == null)
/*     */         {
/* 210 */           this.outbound_rls = new ArrayList();
/*     */         }
/*     */         
/* 213 */         this.outbound_rls.add(limiter);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeOutboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 222 */     synchronized (this)
/*     */     {
/* 224 */       if (this.delegate != null)
/*     */       {
/* 226 */         this.delegate.removeOutboundRateLimiter(limiter);
/*     */ 
/*     */ 
/*     */       }
/* 230 */       else if (this.outbound_rls != null)
/*     */       {
/* 232 */         this.outbound_rls.remove(limiter);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isIncoming()
/*     */   {
/* 241 */     return this.incoming;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectMethodCount()
/*     */   {
/* 247 */     return this.connect_method_count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void connect()
/*     */     throws MessageException
/*     */   {
/* 255 */     connect(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setDelegate(GenericMessageConnectionAdapter _delegate)
/*     */   {
/* 262 */     synchronized (this)
/*     */     {
/* 264 */       this.delegate = _delegate;
/*     */       
/* 266 */       if (this.inbound_rls != null)
/*     */       {
/* 268 */         for (int i = 0; i < this.inbound_rls.size(); i++)
/*     */         {
/* 270 */           this.delegate.addInboundRateLimiter((RateLimiter)this.inbound_rls.get(i));
/*     */         }
/*     */         
/* 273 */         this.inbound_rls = null;
/*     */       }
/*     */       
/* 276 */       if (this.outbound_rls != null)
/*     */       {
/* 278 */         for (int i = 0; i < this.outbound_rls.size(); i++)
/*     */         {
/* 280 */           this.delegate.addOutboundRateLimiter((RateLimiter)this.outbound_rls.get(i));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 285 */         this.outbound_rls = null;
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
/*     */ 
/*     */   public void connect(ByteBuffer initial_data)
/*     */     throws MessageException
/*     */   {
/* 302 */     if (this.incoming)
/*     */     {
/* 304 */       throw new MessageException("Already connected");
/*     */     }
/*     */     
/* 307 */     if (this.connecting)
/*     */     {
/* 309 */       throw new MessageException("Connect already performed");
/*     */     }
/*     */     
/* 312 */     this.connecting = true;
/*     */     
/* 314 */     if (this.closed)
/*     */     {
/* 316 */       throw new MessageException("Connection has been closed");
/*     */     }
/*     */     
/* 319 */     InetSocketAddress tcp_ep = this.endpoint.getTCP();
/*     */     
/* 321 */     if (tcp_ep != null)
/*     */     {
/* 323 */       connectTCP(initial_data, tcp_ep);
/*     */     }
/*     */     else
/*     */     {
/* 327 */       InetSocketAddress udp_ep = this.endpoint.getUDP();
/*     */       
/* 329 */       if (udp_ep != null)
/*     */       {
/* 331 */         connectUDP(initial_data, udp_ep, false);
/*     */       }
/*     */       else
/*     */       {
/* 335 */         throw new MessageException("No protocols availabld");
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
/*     */   protected void connectTCP(final ByteBuffer initial_data, InetSocketAddress tcp_ep)
/*     */   {
/* 349 */     GenericMessageEndpointImpl gen_tcp = new GenericMessageEndpointImpl(this.endpoint.getNotionalAddress());
/*     */     
/* 351 */     gen_tcp.addTCP(tcp_ep);
/*     */     
/* 353 */     final GenericMessageConnectionDirect tcp_delegate = new GenericMessageConnectionDirect(this.msg_id, this.msg_desc, gen_tcp, this.stream_crypto, this.shared_secrets);
/*     */     
/* 355 */     tcp_delegate.setOwner(this);
/*     */     
/* 357 */     tcp_delegate.connect(initial_data, new GenericMessageConnectionAdapter.ConnectionListener()
/*     */     {
/*     */       private boolean connected;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void connectSuccess()
/*     */       {
/* 366 */         this.connected = true;
/*     */         
/* 368 */         GenericMessageConnectionImpl.this.setDelegate(tcp_delegate);
/*     */         
/* 370 */         if (GenericMessageConnectionImpl.this.closed)
/*     */         {
/*     */           try {
/* 373 */             GenericMessageConnectionImpl.this.delegate.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/* 378 */           GenericMessageConnectionImpl.this.reportFailed(new MessageException("Connection has been closed"));
/*     */         }
/*     */         else
/*     */         {
/* 382 */           GenericMessageConnectionImpl.this.reportConnected();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectFailure(Throwable failure_msg)
/*     */       {
/* 390 */         InetSocketAddress udp_ep = GenericMessageConnectionImpl.this.endpoint.getUDP();
/*     */         
/* 392 */         if ((udp_ep != null) && (!this.connected))
/*     */         {
/* 394 */           initial_data.rewind();
/*     */           
/* 396 */           GenericMessageConnectionImpl.this.connectUDP(initial_data, udp_ep, false);
/*     */         }
/*     */         else
/*     */         {
/* 400 */           GenericMessageConnectionImpl.this.reportFailed(failure_msg);
/*     */         }
/*     */       }
/*     */     });
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
/*     */   protected void connectUDP(final ByteBuffer initial_data, final InetSocketAddress udp_ep, boolean nat_traversal)
/*     */   {
/* 416 */     final GenericMessageEndpointImpl gen_udp = new GenericMessageEndpointImpl(this.endpoint.getNotionalAddress());
/*     */     
/* 418 */     gen_udp.addUDP(udp_ep);
/*     */     
/* 420 */     final GenericMessageConnectionAdapter udp_delegate = new GenericMessageConnectionDirect(this.msg_id, this.msg_desc, gen_udp, this.stream_crypto, this.shared_secrets);
/*     */     
/* 422 */     udp_delegate.setOwner(this);
/*     */     
/* 424 */     if (nat_traversal)
/*     */     {
/* 426 */       NATTraverser nat_traverser = this.message_manager.getNATTraverser();
/*     */       
/* 428 */       Map request = new HashMap();
/*     */       
/* 430 */       nat_traverser.attemptTraversal(this.message_manager, udp_ep, request, false, new NATTraversalObserver()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void succeeded(final InetSocketAddress rendezvous, final InetSocketAddress target, Map reply)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 443 */           if (GenericMessageConnectionImpl.this.closed)
/*     */           {
/* 445 */             GenericMessageConnectionImpl.this.reportFailed(new MessageException("Connection has been closed"));
/*     */           }
/*     */           else
/*     */           {
/* 449 */             GenericMessageConnectionImpl.access$308(GenericMessageConnectionImpl.this);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 459 */             udp_delegate.connect(initial_data, new GenericMessageConnectionAdapter.ConnectionListener()
/*     */             {
/*     */               private boolean connected;
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public void connectSuccess()
/*     */               {
/* 468 */                 this.connected = true;
/*     */                 
/* 470 */                 GenericMessageConnectionImpl.this.setDelegate(GenericMessageConnectionImpl.2.this.val$udp_delegate);
/*     */                 
/* 472 */                 if (GenericMessageConnectionImpl.this.closed)
/*     */                 {
/*     */                   try {
/* 475 */                     GenericMessageConnectionImpl.this.delegate.close();
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                   
/*     */ 
/* 480 */                   GenericMessageConnectionImpl.this.reportFailed(new MessageException("Connection has been closed"));
/*     */                 }
/*     */                 else
/*     */                 {
/* 484 */                   GenericMessageConnectionImpl.this.reportConnected();
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public void connectFailure(Throwable failure_msg)
/*     */               {
/* 492 */                 if (this.connected)
/*     */                 {
/* 494 */                   GenericMessageConnectionImpl.this.reportFailed(failure_msg);
/*     */                 }
/*     */                 else
/*     */                 {
/* 498 */                   GenericMessageConnectionImpl.2.this.val$initial_data.rewind();
/*     */                   
/* 500 */                   GenericMessageConnectionImpl.this.connectTunnel(GenericMessageConnectionImpl.2.this.val$initial_data, GenericMessageConnectionImpl.2.this.val$gen_udp, rendezvous, target);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void failed(int failure_type)
/*     */         {
/* 512 */           GenericMessageConnectionImpl.this.reportFailed(new MessageException("UDP connection attempt failed - NAT traversal failed (" + NATTraversalObserver.FT_STRINGS[failure_type] + ")"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void failed(Throwable cause)
/*     */         {
/* 519 */           GenericMessageConnectionImpl.this.reportFailed(cause);
/*     */         }
/*     */         
/*     */ 
/*     */         public void disabled()
/*     */         {
/* 525 */           GenericMessageConnectionImpl.this.reportFailed(new MessageException("UDP connection attempt failed as DDB is disabled"));
/*     */         }
/*     */       });
/*     */     }
/*     */     else {
/* 530 */       udp_delegate.connect(initial_data, new GenericMessageConnectionAdapter.ConnectionListener()
/*     */       {
/*     */         private boolean connected;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void connectSuccess()
/*     */         {
/* 539 */           this.connected = true;
/*     */           
/* 541 */           GenericMessageConnectionImpl.this.setDelegate(udp_delegate);
/*     */           
/* 543 */           if (GenericMessageConnectionImpl.this.closed)
/*     */           {
/*     */             try {
/* 546 */               GenericMessageConnectionImpl.this.delegate.close();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */             
/*     */ 
/* 551 */             GenericMessageConnectionImpl.this.reportFailed(new MessageException("Connection has been closed"));
/*     */           }
/*     */           else
/*     */           {
/* 555 */             GenericMessageConnectionImpl.this.reportConnected();
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void connectFailure(Throwable failure_msg)
/*     */         {
/* 563 */           if (this.connected)
/*     */           {
/* 565 */             GenericMessageConnectionImpl.this.reportFailed(failure_msg);
/*     */           }
/*     */           else
/*     */           {
/* 569 */             initial_data.rewind();
/*     */             
/* 571 */             GenericMessageConnectionImpl.this.connectUDP(initial_data, udp_ep, true);
/*     */           }
/*     */         }
/*     */       });
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
/*     */ 
/*     */   protected void connectTunnel(ByteBuffer initial_data, GenericMessageEndpoint ep, InetSocketAddress rendezvous, InetSocketAddress target)
/*     */   {
/* 589 */     final GenericMessageConnectionIndirect tunnel_delegate = new GenericMessageConnectionIndirect(this.message_manager, this.msg_id, this.msg_desc, ep, rendezvous, target);
/*     */     
/*     */ 
/* 592 */     tunnel_delegate.setOwner(this);
/*     */     
/* 594 */     tunnel_delegate.connect(initial_data, new GenericMessageConnectionAdapter.ConnectionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void connectSuccess()
/*     */       {
/*     */ 
/* 601 */         GenericMessageConnectionImpl.this.setDelegate(tunnel_delegate);
/*     */         
/* 603 */         if (GenericMessageConnectionImpl.this.closed)
/*     */         {
/*     */           try {
/* 606 */             GenericMessageConnectionImpl.this.delegate.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/*     */ 
/* 612 */           GenericMessageConnectionImpl.this.reportFailed(new MessageException("Connection has been closed"));
/*     */         }
/*     */         else
/*     */         {
/* 616 */           GenericMessageConnectionImpl.this.reportConnected();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectFailure(Throwable failure_msg)
/*     */       {
/* 624 */         GenericMessageConnectionImpl.this.reportFailed(failure_msg);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void accepted()
/*     */   {
/* 637 */     this.delegate.accepted();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void send(PooledByteBuffer message)
/*     */     throws MessageException
/*     */   {
/* 646 */     int size = ((PooledByteBufferImpl)message).getBuffer().remaining((byte)1);
/*     */     
/* 648 */     if (size > getMaximumMessageSize())
/*     */     {
/* 650 */       throw new MessageException("Message is too large: supplied is " + size + ", maximum is " + getMaximumMessageSize());
/*     */     }
/*     */     
/* 653 */     this.delegate.send(message);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void receive(GenericMessage message)
/*     */   {
/* 660 */     boolean handled = false;
/*     */     
/* 662 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 664 */       PooledByteBuffer buffer = new PooledByteBufferImpl(message.getPayload());
/*     */       try
/*     */       {
/* 667 */         ((GenericMessageConnectionListener)this.listeners.get(i)).receive(this, buffer);
/*     */         
/* 669 */         handled = true;
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/* 673 */         buffer.returnToPool();
/*     */         
/* 675 */         if (!(f instanceof MessageException))
/*     */         {
/* 677 */           Debug.printStackTrace(f);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 682 */     if ((!handled) && (!this.closed) && (!this.closing))
/*     */     {
/* 684 */       Debug.out("GenericMessage: incoming message not handled");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void closing()
/*     */   {
/* 691 */     this.closing = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws MessageException
/*     */   {
/* 699 */     this.closed = true;
/*     */     
/* 701 */     if (this.delegate != null)
/*     */     {
/* 703 */       this.delegate.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void reportConnected()
/*     */   {
/* 710 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 713 */         ((GenericMessageConnectionListener)this.listeners.get(i)).connected(this);
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/* 717 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void reportFailed(Throwable e)
/*     */   {
/* 726 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 729 */         ((GenericMessageConnectionListener)this.listeners.get(i)).failed(this, e);
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/* 733 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(GenericMessageConnectionListener listener)
/*     */   {
/* 742 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(GenericMessageConnectionListener listener)
/*     */   {
/* 749 */     this.listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessageConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */