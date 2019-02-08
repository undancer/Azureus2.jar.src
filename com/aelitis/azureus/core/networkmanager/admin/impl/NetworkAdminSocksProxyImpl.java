/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSocksProxy;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.ProtocolEndpointTCP;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.ProxyLoginHandler;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.ProxyLoginHandler.ProxyListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPConnectionManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPConnectionManager.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportHelperFilterFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportImpl;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ public class NetworkAdminSocksProxyImpl
/*     */   implements NetworkAdminSocksProxy
/*     */ {
/*  47 */   private final String TARGET_HOST = "version.vuze.com";
/*  48 */   private final int TARGET_PORT = 80;
/*     */   
/*     */ 
/*     */   private final String host;
/*     */   
/*     */ 
/*     */   private final String port;
/*     */   
/*     */   final String user;
/*     */   
/*     */   final String password;
/*     */   
/*     */ 
/*     */   protected NetworkAdminSocksProxyImpl(String _host, String _port, String _user, String _password)
/*     */   {
/*  63 */     this.host = _host;
/*  64 */     this.port = _port;
/*  65 */     this.user = _user;
/*  66 */     this.password = _password;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isConfigured()
/*     */   {
/*  72 */     return this.host.length() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  78 */     return this.host + ":" + this.port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHost()
/*     */   {
/*  84 */     return this.host;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPort()
/*     */   {
/*  90 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUser()
/*     */   {
/*  96 */     return this.user;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getVersionsSupported()
/*     */     throws NetworkAdminException
/*     */   {
/* 104 */     NetworkAdminException failure = null;
/*     */     
/* 106 */     List versions = new ArrayList();
/*     */     try
/*     */     {
/* 109 */       testVersion("V4");
/*     */       
/* 111 */       versions.add("4");
/*     */     }
/*     */     catch (NetworkAdminException e)
/*     */     {
/* 115 */       failure = e;
/*     */     }
/*     */     try
/*     */     {
/* 119 */       testVersion("V4a");
/*     */       
/* 121 */       versions.add("4a");
/*     */     }
/*     */     catch (NetworkAdminException e)
/*     */     {
/* 125 */       failure = e;
/*     */     }
/*     */     try
/*     */     {
/* 129 */       testVersion("V5");
/*     */       
/* 131 */       versions.add("5");
/*     */     }
/*     */     catch (NetworkAdminException e)
/*     */     {
/* 135 */       failure = e;
/*     */     }
/*     */     
/* 138 */     if (versions.size() > 0)
/*     */     {
/* 140 */       return (String[])versions.toArray(new String[versions.size()]);
/*     */     }
/*     */     
/*     */ 
/* 144 */     throw failure;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 150 */     String res = getName();
/*     */     
/* 152 */     if (this.user.length() > 0)
/*     */     {
/* 154 */       res = res + " [auth=" + this.user + "]";
/*     */     }
/*     */     
/* 157 */     res = res + ", versions=";
/*     */     try
/*     */     {
/* 160 */       String[] versions = getVersionsSupported();
/*     */       
/* 162 */       for (int j = 0; j < versions.length; j++)
/*     */       {
/* 164 */         res = res + (j == 0 ? "" : ",") + versions[j];
/*     */       }
/*     */     }
/*     */     catch (NetworkAdminException e)
/*     */     {
/* 169 */       res = res + "unknown (" + e.getLocalizedMessage() + ")";
/*     */     }
/*     */     
/* 172 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void testVersion(final String version)
/*     */     throws NetworkAdminException
/*     */   {
/* 181 */     int RES_CONNECT_FAILED = 0;
/* 182 */     int RES_SOCKS_FAILED = 1;
/* 183 */     int RES_OK = 3;
/*     */     
/* 185 */     final AESemaphore sem = new AESemaphore("NetworkAdminSocksProxy:test");
/*     */     
/* 187 */     final int[] result = { 0 };
/*     */     
/* 189 */     final NetworkAdminException[] error = { null };
/*     */     try
/*     */     {
/* 192 */       InetSocketAddress socks_address = new InetSocketAddress(InetAddress.getByName(this.host), Integer.parseInt(this.port));
/*     */       
/* 194 */       final InetSocketAddress target_address = new InetSocketAddress("version.vuze.com", 80);
/*     */       
/* 196 */       TCPConnectionManager.ConnectListener connect_listener = new TCPConnectionManager.ConnectListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public int connectAttemptStarted(int default_connect_timeout)
/*     */         {
/*     */ 
/* 203 */           return default_connect_timeout;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void connectSuccess(SocketChannel channel)
/*     */         {
/* 210 */           final TCPTransportImpl transport = new TCPTransportImpl((ProtocolEndpointTCP)ProtocolEndpointFactory.createEndpoint(1, target_address), false, false, (byte[][])null);
/*     */           
/*     */ 
/*     */ 
/* 214 */           transport.setFilter(TCPTransportHelperFilterFactory.createTransparentFilter(channel));
/*     */           
/* 216 */           new ProxyLoginHandler(transport, target_address, new ProxyLoginHandler.ProxyListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void connectSuccess()
/*     */             {
/*     */ 
/*     */ 
/* 224 */               transport.close("Done");
/*     */               
/* 226 */               NetworkAdminSocksProxyImpl.1.this.val$result[0] = 3;
/*     */               
/* 228 */               NetworkAdminSocksProxyImpl.1.this.val$sem.release();
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             public void connectFailure(Throwable failure_msg)
/*     */             {
/* 235 */               transport.close("Proxy login failed");
/*     */               
/* 237 */               NetworkAdminSocksProxyImpl.1.this.val$result[0] = 1;
/* 238 */               NetworkAdminSocksProxyImpl.1.this.val$error[0] = new NetworkAdminException("Proxy connect failed", failure_msg);
/*     */               
/* 240 */               NetworkAdminSocksProxyImpl.1.this.val$sem.release(); } }, version, NetworkAdminSocksProxyImpl.this.user, NetworkAdminSocksProxyImpl.this.password);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void connectFailure(Throwable failure_msg)
/*     */         {
/* 252 */           result[0] = 0;
/* 253 */           error[0] = new NetworkAdminException("Connect failed", failure_msg);
/*     */           
/* 255 */           sem.release();
/*     */         }
/*     */         
/* 258 */       };
/* 259 */       TCPNetworkManager.getSingleton().getConnectDisconnectManager().requestNewConnection(socks_address, connect_listener, 3);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 264 */       result[0] = 0;
/* 265 */       error[0] = new NetworkAdminException("Connect failed", e);
/*     */       
/* 267 */       sem.release();
/*     */     }
/*     */     
/* 270 */     if (!sem.reserve(10000L))
/*     */     {
/* 272 */       result[0] = 0;
/* 273 */       error[0] = new NetworkAdminException("Connect timeout");
/*     */     }
/*     */     
/* 276 */     if (result[0] != 3)
/*     */     {
/* 278 */       throw error[0];
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminSocksProxyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */