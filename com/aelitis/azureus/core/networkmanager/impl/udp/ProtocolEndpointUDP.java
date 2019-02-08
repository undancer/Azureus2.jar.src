/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointHandler;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class ProtocolEndpointUDP
/*     */   implements ProtocolEndpoint
/*     */ {
/*     */   private ConnectionEndpoint ce;
/*     */   private final InetSocketAddress address;
/*     */   
/*     */   public static void register()
/*     */   {
/*  41 */     ProtocolEndpointFactory.registerHandler(new ProtocolEndpointHandler()
/*     */     {
/*     */ 
/*     */       public int getType()
/*     */       {
/*     */ 
/*  47 */         return 2;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public ProtocolEndpoint create(InetSocketAddress address)
/*     */       {
/*  54 */         return new ProtocolEndpointUDP(address, null);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public ProtocolEndpoint create(ConnectionEndpoint connection_endpoint, InetSocketAddress address)
/*     */       {
/*  62 */         return new ProtocolEndpointUDP(connection_endpoint, address, null);
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
/*     */   private ProtocolEndpointUDP(ConnectionEndpoint _ce, InetSocketAddress _address)
/*     */   {
/*  75 */     this.ce = _ce;
/*  76 */     this.address = _address;
/*     */     
/*  78 */     this.ce.addProtocol(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ProtocolEndpointUDP(InetSocketAddress _address)
/*     */   {
/*  85 */     this.ce = new ConnectionEndpoint(_address);
/*  86 */     this.address = _address;
/*     */     
/*  88 */     this.ce.addProtocol(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setConnectionEndpoint(ConnectionEndpoint _ce)
/*     */   {
/*  95 */     this.ce = _ce;
/*     */     
/*  97 */     this.ce.addProtocol(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 103 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/* 109 */     return this.address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InetSocketAddress getAdjustedAddress(boolean to_lan)
/*     */   {
/* 116 */     return AddressUtils.adjustUDPAddress(this.address, to_lan);
/*     */   }
/*     */   
/*     */ 
/*     */   public ConnectionEndpoint getConnectionEndpoint()
/*     */   {
/* 122 */     return this.ce;
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
/* 134 */     UDPTransport t = new UDPTransport(this, shared_secrets);
/*     */     
/* 136 */     t.connectOutbound(initial_data, listener, priority);
/*     */     
/* 138 */     return t;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 144 */     return this.address.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/ProtocolEndpointUDP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */