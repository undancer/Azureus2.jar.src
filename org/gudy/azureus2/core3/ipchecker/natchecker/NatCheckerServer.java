/*     */ package org.gudy.azureus2.core3.ipchecker.natchecker;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager.ByteMatcher;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager.RoutingListener;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.http.HTTPNetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamFactory;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageEncoder;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class NatCheckerServer
/*     */   extends AEThread
/*     */ {
/*  48 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private static final String incoming_handshake = "NATCHECK_HANDSHAKE";
/*     */   
/*     */   private final InetAddress bind_ip;
/*     */   
/*     */   private boolean bind_ip_set;
/*     */   private final String check;
/*     */   private final boolean http_test;
/*     */   private ServerSocket server;
/*  58 */   private volatile boolean bContinue = true;
/*     */   
/*     */ 
/*     */ 
/*     */   private final boolean use_incoming_router;
/*     */   
/*     */ 
/*     */   private NetworkManager.ByteMatcher matcher;
/*     */   
/*     */ 
/*     */ 
/*     */   public NatCheckerServer(InetAddress _bind_ip, int _port, String _check, boolean _http_test)
/*     */     throws Exception
/*     */   {
/*  72 */     super("Nat Checker Server");
/*     */     
/*  74 */     this.bind_ip = _bind_ip;
/*  75 */     this.check = _check;
/*  76 */     this.http_test = _http_test;
/*     */     
/*  78 */     if (this.http_test)
/*     */     {
/*  80 */       HTTPNetworkManager net_man = HTTPNetworkManager.getSingleton();
/*     */       
/*  82 */       if (net_man.isHTTPListenerEnabled())
/*     */       {
/*  84 */         this.use_incoming_router = (_port == net_man.getHTTPListeningPortNumber());
/*     */       }
/*     */       else
/*     */       {
/*  88 */         this.use_incoming_router = false;
/*     */       }
/*     */       
/*  91 */       if (this.use_incoming_router)
/*     */       {
/*  93 */         if (!net_man.isEffectiveBindAddress(this.bind_ip))
/*     */         {
/*  95 */           net_man.setExplicitBindAddress(this.bind_ip);
/*     */           
/*  97 */           this.bind_ip_set = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 102 */       TCPNetworkManager net_man = TCPNetworkManager.getSingleton();
/*     */       
/* 104 */       if (net_man.isTCPListenerEnabled())
/*     */       {
/* 106 */         this.use_incoming_router = (_port == net_man.getTCPListeningPortNumber());
/*     */       }
/*     */       else
/*     */       {
/* 110 */         this.use_incoming_router = false;
/*     */       }
/*     */       
/* 113 */       if (this.use_incoming_router)
/*     */       {
/* 115 */         if (!net_man.isEffectiveBindAddress(this.bind_ip))
/*     */         {
/* 117 */           net_man.setExplicitBindAddress(this.bind_ip);
/*     */           
/* 119 */           this.bind_ip_set = true;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 125 */         this.matcher = new NetworkManager.ByteMatcher() {
/* 126 */           public int matchThisSizeOrBigger() { return maxSize(); }
/* 127 */           public int maxSize() { return "NATCHECK_HANDSHAKE".getBytes().length; }
/* 128 */           public int minSize() { return maxSize(); }
/*     */           
/*     */           public Object matches(TransportHelper transport, ByteBuffer to_compare, int port) {
/* 131 */             int old_limit = to_compare.limit();
/* 132 */             to_compare.limit(to_compare.position() + maxSize());
/* 133 */             boolean matches = to_compare.equals(ByteBuffer.wrap("NATCHECK_HANDSHAKE".getBytes()));
/* 134 */             to_compare.limit(old_limit);
/* 135 */             return matches ? "" : null; }
/*     */           
/* 137 */           public Object minMatches(TransportHelper transport, ByteBuffer to_compare, int port) { return matches(transport, to_compare, port); }
/* 138 */           public byte[][] getSharedSecrets() { return null; }
/* 139 */           public int getSpecificPort() { return -1;
/*     */           }
/*     */ 
/* 142 */         };
/* 143 */         NetworkManager.getSingleton().requestIncomingConnectionRouting(this.matcher, new NetworkManager.RoutingListener()
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
/* 196 */           new MessageStreamFactory
/*     */           {
/*     */ 
/*     */ 
/*     */             public void connectionRouted(NetworkConnection connection, Object routing_data)
/*     */             {
/*     */ 
/*     */ 
/* 151 */               if (Logger.isEnabled()) {
/* 152 */                 Logger.log(new LogEvent(NatCheckerServer.LOGID, "Incoming connection from [" + connection + "] successfully routed to NAT CHECKER"));
/*     */               }
/*     */               try
/*     */               {
/* 156 */                 ByteBuffer msg = NatCheckerServer.this.getMessage();
/*     */                 
/* 158 */                 Transport transport = connection.getTransport();
/*     */                 
/* 160 */                 long start = SystemTime.getCurrentTime();
/*     */                 
/* 162 */                 while (msg.hasRemaining())
/*     */                 {
/* 164 */                   transport.write(new ByteBuffer[] { msg }, 0, 1);
/*     */                   
/* 166 */                   if (msg.hasRemaining())
/*     */                   {
/* 168 */                     long now = SystemTime.getCurrentTime();
/*     */                     
/* 170 */                     if (now < start)
/*     */                     {
/* 172 */                       start = now;
/*     */ 
/*     */ 
/*     */                     }
/* 176 */                     else if (now - start > 30000L)
/*     */                     {
/* 178 */                       throw new Exception("Timeout");
/*     */                     }
/*     */                     
/*     */ 
/* 182 */                     Thread.sleep(50L);
/*     */                   }
/*     */                 }
/*     */               }
/*     */               catch (Throwable t) {
/* 187 */                 Debug.out("Nat check write failed", t);
/*     */               }
/*     */               
/* 190 */               connection.close(null);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 196 */             public boolean autoCryptoFallback() { return true; } }, new MessageStreamFactory()
/*     */           {
/*     */ 
/*     */             public MessageStreamEncoder createEncoder() {
/* 200 */               return new AZMessageEncoder(0); }
/* 201 */             public MessageStreamDecoder createDecoder() { return new AZMessageDecoder(); }
/*     */           });
/*     */       }
/*     */       
/* 205 */       if (Logger.isEnabled()) {
/* 206 */         Logger.log(new LogEvent(LOGID, "NAT tester using central routing for server socket"));
/*     */       }
/*     */     }
/*     */     
/* 210 */     if (!this.use_incoming_router)
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 216 */         this.server = new ServerSocket();
/* 217 */         this.server.setReuseAddress(true);
/*     */         
/*     */         InetSocketAddress address;
/*     */         InetSocketAddress address;
/* 221 */         if (this.bind_ip != null)
/*     */         {
/* 223 */           address = new InetSocketAddress(this.bind_ip, _port);
/*     */         }
/*     */         else
/*     */         {
/* 227 */           address = new InetSocketAddress(_port);
/*     */         }
/*     */         
/* 230 */         this.server.bind(address);
/*     */         
/* 232 */         if (Logger.isEnabled()) { Logger.log(new LogEvent(LOGID, "NAT tester server socket bound to " + address));
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 237 */         Logger.log(new LogEvent(LOGID, "NAT tester failed to setup listener socket", e));
/*     */         
/* 239 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ByteBuffer getMessage()
/*     */     throws IOException
/*     */   {
/* 249 */     Map map = new HashMap();
/*     */     
/* 251 */     map.put("check", this.check);
/*     */     
/* 253 */     byte[] map_bytes = BEncoder.encode(map);
/*     */     
/* 255 */     ByteBuffer msg = ByteBuffer.allocate(4 + map_bytes.length);
/*     */     
/* 257 */     msg.putInt(map_bytes.length);
/* 258 */     msg.put(map_bytes);
/*     */     
/* 260 */     msg.flip();
/*     */     
/* 262 */     return msg;
/*     */   }
/*     */   
/*     */   public void runSupport() {
/* 266 */     for (;;) { if (this.bContinue)
/*     */         try {
/* 268 */           if (this.use_incoming_router)
/*     */           {
/* 270 */             Thread.sleep(20L);
/*     */           }
/*     */           else
/*     */           {
/* 274 */             Socket sck = this.server.accept();
/*     */             try
/*     */             {
/* 277 */               sck.getOutputStream().write(getMessage().array());
/*     */               
/* 279 */               sck.close();
/*     */               
/* 281 */               sck = null;
/*     */               
/* 283 */               if (sck != null) {
/*     */                 try {
/* 285 */                   sck.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */             finally
/*     */             {
/* 283 */               if (sck != null) {
/*     */                 try {
/* 285 */                   sck.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e) {
/* 293 */           this.bContinue = false;
/*     */         }
/*     */     }
/*     */   }
/*     */   
/*     */   public void stopIt() {
/* 299 */     this.bContinue = false;
/*     */     
/* 301 */     if (this.use_incoming_router)
/*     */     {
/* 303 */       if (this.http_test)
/*     */       {
/* 305 */         if (this.bind_ip_set)
/*     */         {
/* 307 */           HTTPNetworkManager.getSingleton().clearExplicitBindAddress();
/*     */         }
/*     */       }
/*     */       else {
/* 311 */         NetworkManager.getSingleton().cancelIncomingConnectionRouting(this.matcher);
/*     */         
/* 313 */         if (this.bind_ip_set)
/*     */         {
/* 315 */           TCPNetworkManager.getSingleton().clearExplicitBindAddress();
/*     */         }
/*     */       }
/*     */     }
/* 319 */     else if (this.server != null) {
/*     */       try {
/* 321 */         this.server.close();
/*     */       } catch (Throwable t) {
/* 323 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/natchecker/NatCheckerServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */