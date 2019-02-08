/*     */ package org.gudy.azureus2.pluginsimpl.local.network;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilterStreamCipher;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportImpl;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPTransport;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPTransportHelper;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageStreamDecoder;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageStreamEncoder;
/*     */ import org.gudy.azureus2.plugins.network.Connection;
/*     */ import org.gudy.azureus2.plugins.network.ConnectionManager;
/*     */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*     */ import org.gudy.azureus2.plugins.network.TransportException;
/*     */ import org.gudy.azureus2.plugins.network.TransportFilter;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageStreamDecoderAdapter;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageStreamEncoderAdapter;
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
/*     */ public class ConnectionManagerImpl
/*     */   implements ConnectionManager
/*     */ {
/*     */   private static ConnectionManagerImpl instance;
/*     */   private AzureusCore azureus_core;
/*     */   
/*     */   public static synchronized ConnectionManagerImpl getSingleton(AzureusCore core)
/*     */   {
/*  67 */     if (instance == null)
/*     */     {
/*  69 */       instance = new ConnectionManagerImpl(core);
/*     */     }
/*     */     
/*  72 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ConnectionManagerImpl(AzureusCore _core)
/*     */   {
/*  79 */     this.azureus_core = _core;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Connection createConnection(InetSocketAddress remote_address, MessageStreamEncoder encoder, MessageStreamDecoder decoder)
/*     */   {
/*  89 */     ConnectionEndpoint connection_endpoint = new ConnectionEndpoint(remote_address);
/*     */     
/*  91 */     connection_endpoint.addProtocol(ProtocolEndpointFactory.createEndpoint(1, remote_address));
/*     */     
/*  93 */     NetworkConnection core_conn = NetworkManager.getSingleton().createConnection(connection_endpoint, new MessageStreamEncoderAdapter(encoder), new MessageStreamDecoderAdapter(decoder), false, false, (byte[][])null);
/*     */     
/*     */ 
/*  96 */     return new ConnectionImpl(core_conn, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNATStatus()
/*     */   {
/* 102 */     return this.azureus_core.getGlobalManager().getNATStatus();
/*     */   }
/*     */   
/*     */   public org.gudy.azureus2.plugins.network.TransportCipher createTransportCipher(String algorithm, int mode, SecretKeySpec key_spec, AlgorithmParameterSpec params) throws TransportException {
/*     */     try {
/* 107 */       com.aelitis.azureus.core.networkmanager.impl.TransportCipher cipher = new com.aelitis.azureus.core.networkmanager.impl.TransportCipher(algorithm, mode, key_spec, params);
/* 108 */       return new TransportCipherImpl(cipher);
/*     */     }
/*     */     catch (Exception e) {
/* 111 */       throw new TransportException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public TransportFilter createTransportFilter(Connection connection, org.gudy.azureus2.plugins.network.TransportCipher read_cipher, org.gudy.azureus2.plugins.network.TransportCipher write_cipher) throws TransportException {
/* 116 */     org.gudy.azureus2.plugins.network.Transport transport = connection.getTransport();
/* 117 */     if (transport == null)
/* 118 */       throw new TransportException("no transport available");
/*     */     com.aelitis.azureus.core.networkmanager.Transport core_transport;
/*     */     try {
/* 121 */       core_transport = ((TransportImpl)transport).coreTransport();
/* 122 */     } catch (IOException e) { throw new TransportException(e);
/*     */     }
/*     */     
/*     */     TransportHelper helper;
/* 126 */     if ((core_transport instanceof TCPTransportImpl)) {
/* 127 */       TransportHelperFilter hfilter = ((TCPTransportImpl)core_transport).getFilter();
/* 128 */       TransportHelper helper; if (hfilter != null) { helper = hfilter.getHelper();
/*     */       } else {
/* 130 */         helper = new TCPTransportHelper(((TCPTransportImpl)core_transport).getSocketChannel());
/*     */       }
/* 132 */     } else if ((core_transport instanceof UDPTransport)) {
/* 133 */       TransportHelperFilter hfilter = ((UDPTransport)core_transport).getFilter();
/* 134 */       TransportHelper helper; if (hfilter != null) { helper = hfilter.getHelper();
/*     */       } else {
/* 136 */         TransportHelper helper = ((UDPTransport)core_transport).getFilter().getHelper();
/* 137 */         InetSocketAddress addr = core_transport.getTransportEndpoint().getProtocolEndpoint().getConnectionEndpoint().getNotionalAddress();
/* 138 */         if (!connection.isIncoming()) {
/* 139 */           try { helper = new UDPTransportHelper(UDPNetworkManager.getSingleton().getConnectionManager(), addr, (UDPTransport)core_transport);
/* 140 */           } catch (IOException ioe) { throw new TransportException(ioe);
/*     */ 
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/*     */ 
/* 152 */           throw new TransportException("udp incoming transport type not supported - " + core_transport);
/*     */         }
/*     */       }
/*     */     } else {
/* 156 */       throw new TransportException("transport type not supported - " + core_transport);
/*     */     }
/*     */     TransportHelper helper;
/* 159 */     TransportHelperFilterStreamCipher core_filter = new TransportHelperFilterStreamCipher(helper, ((TransportCipherImpl)read_cipher).cipher, ((TransportCipherImpl)write_cipher).cipher);
/* 160 */     return new TransportFilterImpl(core_filter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RateLimiter createRateLimiter(String name, int bps)
/*     */   {
/* 168 */     return new PluginRateLimiter(name, bps, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class PluginRateLimiter
/*     */     implements RateLimiter
/*     */   {
/*     */     private String name;
/*     */     
/*     */ 
/*     */     private int rate;
/*     */     
/*     */     private long total;
/*     */     
/*     */ 
/*     */     private PluginRateLimiter(String _name, int _bps)
/*     */     {
/* 186 */       this.name = _name;
/* 187 */       this.rate = _bps;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getName()
/*     */     {
/* 193 */       return this.name;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getRateLimitBytesPerSecond()
/*     */     {
/* 199 */       return this.rate;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setRateLimitBytesPerSecond(int bytes_per_second)
/*     */     {
/* 206 */       this.rate = bytes_per_second;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getRateLimitTotalByteCount()
/*     */     {
/* 212 */       return this.total;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void updateBytesUsed(int used)
/*     */     {
/* 219 */       this.total += used;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/network/ConnectionManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */