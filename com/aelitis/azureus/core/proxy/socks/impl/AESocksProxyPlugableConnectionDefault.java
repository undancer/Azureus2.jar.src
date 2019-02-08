/*     */ package com.aelitis.azureus.core.proxy.socks.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyConnection;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxy;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyAddress;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyConnection;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyPlugableConnection;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
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
/*     */ public class AESocksProxyPlugableConnectionDefault
/*     */   implements AESocksProxyPlugableConnection
/*     */ {
/*     */   protected final AESocksProxyConnection socks_connection;
/*     */   protected final AEProxyConnection connection;
/*     */   protected final SocketChannel source_channel;
/*     */   protected SocketChannel target_channel;
/*     */   protected proxyStateRelayData relay_data_state;
/*     */   
/*     */   public AESocksProxyPlugableConnectionDefault(AESocksProxyConnection _socks_connection)
/*     */   {
/*  61 */     this.socks_connection = _socks_connection;
/*  62 */     this.connection = this.socks_connection.getConnection();
/*     */     
/*  64 */     this.source_channel = this.connection.getSourceChannel();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  70 */     if (this.target_channel != null)
/*     */     {
/*  72 */       return this.target_channel.socket().getInetAddress() + ":" + this.target_channel.socket().getPort();
/*     */     }
/*     */     
/*  75 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/*  81 */     return this.target_channel.socket().getInetAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLocalPort()
/*     */   {
/*  87 */     return this.target_channel.socket().getPort();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connect(AESocksProxyAddress _address)
/*     */     throws IOException
/*     */   {
/*  96 */     InetAddress address = _address.getAddress();
/*     */     
/*  98 */     if (address == null)
/*     */     {
/* 100 */       if (this.socks_connection.areDNSLookupsEnabled()) {
/*     */         try
/*     */         {
/* 103 */           address = HostNameToIPResolver.syncResolve(_address.getUnresolvedAddress());
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/* 109 */       if (address == null)
/*     */       {
/* 111 */         throw new IOException("DNS lookup of '" + _address.getUnresolvedAddress() + "' fails");
/*     */       }
/*     */     }
/*     */     
/* 115 */     new proxyStateRelayConnect(new InetSocketAddress(address, _address.getPort()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void relayData()
/*     */     throws IOException
/*     */   {
/* 123 */     new proxyStateRelayData();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 129 */     if (this.target_channel != null) {
/*     */       try
/*     */       {
/* 132 */         this.connection.cancelReadSelect(this.target_channel);
/* 133 */         this.connection.cancelWriteSelect(this.target_channel);
/*     */         
/* 135 */         this.target_channel.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 139 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 143 */     if (this.relay_data_state != null)
/*     */     {
/* 145 */       this.relay_data_state.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected class proxyStateRelayConnect
/*     */     extends AESocksProxyState
/*     */   {
/*     */     protected final InetSocketAddress address;
/*     */     
/*     */ 
/*     */ 
/*     */     protected proxyStateRelayConnect(InetSocketAddress _address)
/*     */       throws IOException
/*     */     {
/* 162 */       super();
/*     */       
/* 164 */       this.address = _address;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 169 */       AESocksProxyPlugableConnectionDefault.this.connection.cancelReadSelect(AESocksProxyPlugableConnectionDefault.this.source_channel);
/*     */       
/* 171 */       AESocksProxyPlugableConnectionDefault.this.connection.setConnectState(this);
/*     */       
/* 173 */       AESocksProxyPlugableConnectionDefault.this.target_channel = SocketChannel.open();
/*     */       
/* 175 */       InetAddress bindIP = NetworkAdmin.getSingleton().getMultiHomedOutgoingRoundRobinBindAddress(this.address.getAddress());
/*     */       
/* 177 */       if (bindIP != null) {
/*     */         try
/*     */         {
/* 180 */           AESocksProxyPlugableConnectionDefault.this.target_channel.socket().bind(new InetSocketAddress(bindIP, 0));
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 184 */           if (!bindIP.isAnyLocalAddress())
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 194 */             if ((!e.getMessage().contains("not supported")) || (!this.address.isUnresolved()))
/*     */             {
/* 196 */               throw e;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 202 */       AESocksProxyPlugableConnectionDefault.this.target_channel.configureBlocking(false);
/*     */       
/* 204 */       AESocksProxyPlugableConnectionDefault.this.target_channel.connect(this.address);
/*     */       
/* 206 */       AESocksProxyPlugableConnectionDefault.this.connection.requestConnectSelect(AESocksProxyPlugableConnectionDefault.this.target_channel);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected boolean connectSupport(SocketChannel sc)
/*     */       throws IOException
/*     */     {
/* 215 */       if (!sc.finishConnect())
/*     */       {
/* 217 */         throw new IOException("finishConnect returned false");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 222 */       AESocksProxy proxy = AESocksProxyPlugableConnectionDefault.this.socks_connection.getProxy();
/*     */       
/* 224 */       if (proxy.getNextSOCKSProxyHost() != null) {}
/*     */       
/*     */ 
/*     */ 
/* 228 */       AESocksProxyPlugableConnectionDefault.this.socks_connection.connected();
/*     */       
/* 230 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected class proxyStateRelayData
/*     */     extends AESocksProxyState
/*     */   {
/*     */     protected DirectByteBuffer source_buffer;
/*     */     
/*     */     protected DirectByteBuffer target_buffer;
/* 241 */     protected long outward_bytes = 0L;
/* 242 */     protected long inward_bytes = 0L;
/*     */     
/*     */ 
/*     */ 
/*     */     protected proxyStateRelayData()
/*     */       throws IOException
/*     */     {
/* 249 */       super();
/*     */       
/* 251 */       this.source_buffer = DirectByteBufferPool.getBuffer((byte)11, 1024);
/* 252 */       this.target_buffer = DirectByteBufferPool.getBuffer((byte)11, 1024);
/*     */       
/* 254 */       AESocksProxyPlugableConnectionDefault.this.relay_data_state = this;
/*     */       
/* 256 */       if (AESocksProxyPlugableConnectionDefault.this.connection.isClosed())
/*     */       {
/* 258 */         destroy();
/*     */         
/* 260 */         throw new IOException("connection closed");
/*     */       }
/*     */       
/* 263 */       AESocksProxyPlugableConnectionDefault.this.connection.setReadState(this);
/*     */       
/* 265 */       AESocksProxyPlugableConnectionDefault.this.connection.setWriteState(this);
/*     */       
/* 267 */       AESocksProxyPlugableConnectionDefault.this.connection.requestReadSelect(AESocksProxyPlugableConnectionDefault.this.source_channel);
/*     */       
/* 269 */       AESocksProxyPlugableConnectionDefault.this.connection.requestReadSelect(AESocksProxyPlugableConnectionDefault.this.target_channel);
/*     */       
/* 271 */       AESocksProxyPlugableConnectionDefault.this.connection.setConnected();
/*     */     }
/*     */     
/*     */ 
/*     */     protected void destroy()
/*     */     {
/* 277 */       if (this.source_buffer != null)
/*     */       {
/* 279 */         this.source_buffer.returnToPool();
/*     */         
/* 281 */         this.source_buffer = null;
/*     */       }
/*     */       
/* 284 */       if (this.target_buffer != null)
/*     */       {
/* 286 */         this.target_buffer.returnToPool();
/*     */         
/* 288 */         this.target_buffer = null;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected boolean readSupport(SocketChannel sc)
/*     */       throws IOException
/*     */     {
/* 298 */       AESocksProxyPlugableConnectionDefault.this.connection.setTimeStamp();
/*     */       
/* 300 */       SocketChannel chan1 = sc;
/* 301 */       SocketChannel chan2 = sc == AESocksProxyPlugableConnectionDefault.this.source_channel ? AESocksProxyPlugableConnectionDefault.this.target_channel : AESocksProxyPlugableConnectionDefault.this.source_channel;
/*     */       
/* 303 */       DirectByteBuffer read_buffer = sc == AESocksProxyPlugableConnectionDefault.this.source_channel ? this.source_buffer : this.target_buffer;
/*     */       
/* 305 */       int len = read_buffer.read((byte)10, chan1);
/*     */       
/* 307 */       if (len == -1)
/*     */       {
/*     */ 
/*     */ 
/* 311 */         AESocksProxyPlugableConnectionDefault.this.connection.close();
/*     */ 
/*     */ 
/*     */       }
/* 315 */       else if (read_buffer.position((byte)10) > 0)
/*     */       {
/* 317 */         read_buffer.flip((byte)10);
/*     */         
/* 319 */         int written = read_buffer.write((byte)10, chan2);
/*     */         
/* 321 */         if (chan1 == AESocksProxyPlugableConnectionDefault.this.source_channel)
/*     */         {
/* 323 */           this.outward_bytes += written;
/*     */         }
/*     */         else
/*     */         {
/* 327 */           this.inward_bytes += written;
/*     */         }
/*     */         
/* 330 */         if (read_buffer.hasRemaining((byte)10))
/*     */         {
/* 332 */           AESocksProxyPlugableConnectionDefault.this.connection.cancelReadSelect(chan1);
/*     */           
/* 334 */           AESocksProxyPlugableConnectionDefault.this.connection.requestWriteSelect(chan2);
/*     */         }
/*     */         else
/*     */         {
/* 338 */           read_buffer.position((byte)10, 0);
/*     */           
/* 340 */           read_buffer.limit((byte)10, read_buffer.capacity((byte)10));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 345 */       return len > 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected boolean writeSupport(SocketChannel sc)
/*     */       throws IOException
/*     */     {
/* 357 */       SocketChannel chan1 = sc;
/* 358 */       SocketChannel chan2 = sc == AESocksProxyPlugableConnectionDefault.this.source_channel ? AESocksProxyPlugableConnectionDefault.this.target_channel : AESocksProxyPlugableConnectionDefault.this.source_channel;
/*     */       
/* 360 */       DirectByteBuffer read_buffer = sc == AESocksProxyPlugableConnectionDefault.this.source_channel ? this.target_buffer : this.source_buffer;
/*     */       
/* 362 */       int written = read_buffer.write((byte)10, chan1);
/*     */       
/* 364 */       if (chan1 == AESocksProxyPlugableConnectionDefault.this.target_channel)
/*     */       {
/* 366 */         this.outward_bytes += written;
/*     */       }
/*     */       else
/*     */       {
/* 370 */         this.inward_bytes += written;
/*     */       }
/*     */       
/* 373 */       if (read_buffer.hasRemaining((byte)10))
/*     */       {
/* 375 */         AESocksProxyPlugableConnectionDefault.this.connection.requestWriteSelect(chan1);
/*     */       }
/*     */       else
/*     */       {
/* 379 */         read_buffer.position((byte)10, 0);
/*     */         
/* 381 */         read_buffer.limit((byte)10, read_buffer.capacity((byte)10));
/*     */         
/* 383 */         AESocksProxyPlugableConnectionDefault.this.connection.requestReadSelect(chan2);
/*     */       }
/*     */       
/* 386 */       return written > 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getStateName()
/*     */     {
/* 392 */       String state = getClass().getName();
/*     */       
/* 394 */       int pos = state.indexOf("$");
/*     */       
/* 396 */       state = state.substring(pos + 1);
/*     */       
/* 398 */       return state + " [out=" + this.outward_bytes + ",in=" + this.inward_bytes + "] " + this.source_buffer + " / " + this.target_buffer;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/impl/AESocksProxyPlugableConnectionDefault.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */