/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportImpl;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
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
/*     */ public class UDPTransport
/*     */   extends TransportImpl
/*     */ {
/*  36 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private final ProtocolEndpointUDP endpoint;
/*     */   
/*     */   private byte[][] shared_secrets;
/*  41 */   private int transport_mode = 0;
/*     */   
/*     */ 
/*     */   private volatile boolean closed;
/*     */   
/*     */ 
/*     */ 
/*     */   protected UDPTransport(ProtocolEndpointUDP _endpoint, byte[][] _shared_secrets)
/*     */   {
/*  50 */     this.endpoint = _endpoint;
/*  51 */     this.shared_secrets = _shared_secrets;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UDPTransport(ProtocolEndpointUDP _endpoint, TransportHelperFilter _filter)
/*     */   {
/*  59 */     this.endpoint = _endpoint;
/*     */     
/*  61 */     setFilter(_filter);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTCP()
/*     */   {
/*  67 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSOCKS()
/*     */   {
/*  73 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getProtocol()
/*     */   {
/*  79 */     return "UDP";
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportEndpoint getTransportEndpoint()
/*     */   {
/*  85 */     return new TransportEndpointUDP(this.endpoint);
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportStartpoint getTransportStartpoint()
/*     */   {
/*  91 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMssSize()
/*     */   {
/*  97 */     return UDPNetworkManager.getUdpMssSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 103 */     return this.endpoint.getAddress().toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTransportMode(int mode)
/*     */   {
/* 110 */     this.transport_mode = mode;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTransportMode()
/*     */   {
/* 116 */     return this.transport_mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connectOutbound(ByteBuffer initial_data, Transport.ConnectListener listener, int priority)
/*     */   {
/* 125 */     if (!UDPNetworkManager.UDP_OUTGOING_ENABLED)
/*     */     {
/* 127 */       listener.connectFailure(new Throwable("Outbound UDP connections disabled"));
/*     */       
/* 129 */       return;
/*     */     }
/*     */     
/* 132 */     if (this.closed)
/*     */     {
/* 134 */       listener.connectFailure(new Throwable("Connection already closed"));
/*     */       
/* 136 */       return;
/*     */     }
/*     */     
/* 139 */     if (getFilter() != null)
/*     */     {
/* 141 */       listener.connectFailure(new Throwable("Already connected"));
/*     */       
/* 143 */       return;
/*     */     }
/*     */     
/* 146 */     if (COConfigurationManager.getBooleanParameter("Proxy.Data.Enable"))
/*     */     {
/* 148 */       listener.connectFailure(new Throwable("UDP proxy connection not supported"));
/*     */       
/* 150 */       return;
/*     */     }
/*     */     
/* 153 */     UDPConnectionManager con_man = UDPNetworkManager.getSingleton().getConnectionManager();
/*     */     
/* 155 */     con_man.connectOutbound(this, this.endpoint.getAddress(), this.shared_secrets, initial_data, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close(String reason)
/*     */   {
/* 162 */     this.closed = true;
/*     */     
/* 164 */     readyForRead(false);
/* 165 */     readyForWrite(false);
/*     */     
/* 167 */     TransportHelperFilter filter = getFilter();
/*     */     
/* 169 */     if (filter != null)
/*     */     {
/* 171 */       filter.getHelper().close(reason);
/*     */       
/* 173 */       setFilter(null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 180 */     return this.closed;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPTransport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */