/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointHandler;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
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
/*     */ public class ProtocolEndpointTCP
/*     */   implements ProtocolEndpoint
/*     */ {
/*     */   private ConnectionEndpoint ce;
/*     */   private final InetSocketAddress address;
/*     */   
/*     */   public static void register()
/*     */   {
/*  42 */     ProtocolEndpointFactory.registerHandler(new ProtocolEndpointHandler()
/*     */     {
/*     */ 
/*     */       public int getType()
/*     */       {
/*     */ 
/*  48 */         return 1;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public ProtocolEndpoint create(InetSocketAddress address)
/*     */       {
/*  55 */         return new ProtocolEndpointTCP(address, null);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public ProtocolEndpoint create(ConnectionEndpoint connection_endpoint, InetSocketAddress address)
/*     */       {
/*  63 */         return new ProtocolEndpointTCP(connection_endpoint, address, null);
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
/*     */   private ProtocolEndpointTCP(ConnectionEndpoint _ce, InetSocketAddress _address)
/*     */   {
/*  76 */     this.ce = _ce;
/*  77 */     this.address = _address;
/*     */     
/*  79 */     this.ce.addProtocol(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ProtocolEndpointTCP(InetSocketAddress _address)
/*     */   {
/*  86 */     this.ce = new ConnectionEndpoint(_address);
/*  87 */     this.address = _address;
/*     */     
/*  89 */     this.ce.addProtocol(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setConnectionEndpoint(ConnectionEndpoint _ce)
/*     */   {
/*  96 */     this.ce = _ce;
/*     */     
/*  98 */     this.ce.addProtocol(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 104 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public ConnectionEndpoint getConnectionEndpoint()
/*     */   {
/* 110 */     return this.ce;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/* 116 */     return this.address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InetSocketAddress getAdjustedAddress(boolean to_lan)
/*     */   {
/* 123 */     return AddressUtils.adjustTCPAddress(this.address, to_lan);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Transport connectOutbound(boolean connect_with_crypto, boolean allow_fallback, byte[][] shared_secrets, ByteBuffer initial_data, int priority, Transport.ConnectListener listener)
/*     */   {
/* 135 */     TCPTransportImpl t = new TCPTransportImpl(this, connect_with_crypto, allow_fallback, shared_secrets);
/*     */     
/* 137 */     t.connectOutbound(initial_data, listener, priority);
/*     */     
/* 139 */     return t;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Transport connectLightWeight(SocketChannel sc)
/*     */   {
/* 146 */     return new LightweightTCPTransport(this, TCPTransportHelperFilterFactory.createTransparentFilter(sc));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 152 */     return this.address.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/ProtocolEndpointTCP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */