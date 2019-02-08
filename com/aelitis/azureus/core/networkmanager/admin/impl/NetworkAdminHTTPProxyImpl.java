/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminHTTPProxy;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminHTTPProxy.Details;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.ProtocolEndpointTCP;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPConnectionManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPConnectionManager.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportHelperFilterFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportImpl;
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkAdminHTTPProxyImpl
/*     */   implements NetworkAdminHTTPProxy
/*     */ {
/*     */   private static final String NL = "\r\n";
/*  53 */   private final String TARGET_HOST = "version.vuze.com";
/*  54 */   private final int TARGET_PORT = 80;
/*     */   
/*     */   private final String http_host;
/*     */   
/*     */   private final String http_port;
/*     */   
/*     */   private final String https_host;
/*     */   
/*     */   private final String https_port;
/*     */   private final String user;
/*     */   private final String password;
/*     */   private final String[] non_proxy_hosts;
/*     */   
/*     */   protected NetworkAdminHTTPProxyImpl()
/*     */   {
/*  69 */     this.http_host = System.getProperty("http.proxyHost", "").trim();
/*  70 */     this.http_port = System.getProperty("http.proxyPort", "").trim();
/*  71 */     this.https_host = System.getProperty("https.proxyHost", "").trim();
/*  72 */     this.https_port = System.getProperty("https.proxyPort", "").trim();
/*     */     
/*  74 */     this.user = System.getProperty("http.proxyUser", "").trim();
/*  75 */     this.password = System.getProperty("http.proxyPassword", "").trim();
/*     */     
/*  77 */     String nph = System.getProperty("http.nonProxyHosts", "").trim();
/*     */     
/*  79 */     StringTokenizer tok = new StringTokenizer(nph, "|");
/*     */     
/*  81 */     this.non_proxy_hosts = new String[tok.countTokens()];
/*     */     
/*  83 */     int pos = 0;
/*     */     
/*  85 */     while (tok.hasMoreTokens())
/*     */     {
/*  87 */       this.non_proxy_hosts[(pos++)] = tok.nextToken();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  94 */     String res = "";
/*     */     
/*  96 */     if (this.http_host.length() > 0)
/*     */     {
/*  98 */       res = "http=" + this.http_host + ":" + this.http_port;
/*     */     }
/*     */     
/* 101 */     if (this.https_host.length() > 0)
/*     */     {
/* 103 */       res = res + (res.length() == 0 ? "" : ", ") + "https=" + this.https_host + ":" + this.https_port;
/*     */     }
/*     */     
/* 106 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isConfigured()
/*     */   {
/* 112 */     return (this.http_host.length() > 0) || (this.https_host.length() > 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHTTPHost()
/*     */   {
/* 118 */     return this.http_host;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHTTPPort()
/*     */   {
/* 124 */     return this.http_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHTTPSHost()
/*     */   {
/* 130 */     return this.https_host;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHTTPSPort()
/*     */   {
/* 136 */     return this.https_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 142 */     return this.user;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getNonProxyHosts()
/*     */   {
/* 148 */     return this.non_proxy_hosts;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 154 */     String res = getName();
/*     */     
/* 156 */     if (this.user.length() > 0)
/*     */     {
/* 158 */       res = res + " [auth=" + this.user + "]";
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 163 */       NetworkAdminHTTPProxy.Details details = getDetails();
/*     */       
/* 165 */       res = res + " server=" + details.getServerName();
/* 166 */       res = res + ", response=" + details.getResponse();
/* 167 */       res = res + ", auth=" + details.getAuthenticationType();
/*     */     }
/*     */     catch (NetworkAdminException e)
/*     */     {
/* 171 */       res = res + " failed to query proxy - " + e.getLocalizedMessage();
/*     */     }
/*     */     
/* 174 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public NetworkAdminHTTPProxy.Details getDetails()
/*     */     throws NetworkAdminException
/*     */   {
/* 182 */     int RES_CONNECT_FAILED = 0;
/* 183 */     int RES_PROXY_FAILED = 1;
/* 184 */     int RES_OK = 3;
/*     */     
/* 186 */     final AESemaphore sem = new AESemaphore("NetworkAdminSocksProxy:test");
/*     */     
/* 188 */     final int[] result = { 0 };
/*     */     
/* 190 */     final NetworkAdminException[] error = { null };
/* 191 */     final ProxyDetails[] details = { null };
/*     */     try
/*     */     {
/* 194 */       InetSocketAddress socks_address = new InetSocketAddress(InetAddress.getByName(this.http_host), Integer.parseInt(this.http_port));
/*     */       
/*     */ 
/* 197 */       final InetSocketAddress target_address = new InetSocketAddress("version.vuze.com", 80);
/*     */       
/* 199 */       TCPConnectionManager.ConnectListener connect_listener = new TCPConnectionManager.ConnectListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public int connectAttemptStarted(int default_connect_timeout)
/*     */         {
/*     */ 
/* 206 */           return default_connect_timeout;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void connectSuccess(SocketChannel channel)
/*     */         {
/* 213 */           TCPTransportImpl transport = new TCPTransportImpl((ProtocolEndpointTCP)ProtocolEndpointFactory.createEndpoint(1, target_address), false, false, (byte[][])null);
/*     */           
/*     */ 
/*     */ 
/* 217 */           transport.setFilter(TCPTransportHelperFilterFactory.createTransparentFilter(channel));
/*     */           
/* 219 */           final long start_time = SystemTime.getCurrentTime();
/*     */           try
/*     */           {
/* 222 */             String get_str = VersionCheckClient.getSingleton().getHTTPGetString(true, false);
/*     */             
/* 224 */             ByteBuffer request = ByteBuffer.wrap(get_str.getBytes());
/*     */             
/* 226 */             while (request.hasRemaining())
/*     */             {
/* 228 */               if (transport.write(new ByteBuffer[] { request }, 0, 1) < 1L)
/*     */               {
/* 230 */                 if (SystemTime.getCurrentTime() - start_time > 30000L)
/*     */                 {
/* 232 */                   String error = "proxy handshake message send timed out after 30sec";
/*     */                   
/* 234 */                   Debug.out(error);
/*     */                   
/* 236 */                   throw new IOException(error);
/*     */                 }
/*     */                 try
/*     */                 {
/* 240 */                   Thread.sleep(50L);
/*     */                 }
/*     */                 catch (Throwable t)
/*     */                 {
/* 244 */                   t.printStackTrace();
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 249 */             TCPNetworkManager.getSingleton().getReadSelector().register(transport.getSocketChannel(), new VirtualChannelSelector.VirtualSelectorListener()
/*     */             {
/*     */ 
/*     */ 
/* 253 */               private final byte[] reply_buffer = new byte[' '];
/*     */               
/* 255 */               private final ByteBuffer reply = ByteBuffer.wrap(this.reply_buffer);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */               {
/*     */                 try
/*     */                 {
/* 264 */                   if (SystemTime.getCurrentTime() - start_time > 30000L)
/*     */                   {
/* 266 */                     throw new Exception("Timeout");
/*     */                   }
/*     */                   
/* 269 */                   long len = this.val$transport.read(new ByteBuffer[] { this.reply }, 0, 1);
/*     */                   
/* 271 */                   if (len <= 0L)
/*     */                   {
/* 273 */                     return false;
/*     */                   }
/*     */                   
/* 276 */                   String str = new String(this.reply_buffer, 0, this.reply.position());
/*     */                   
/* 278 */                   if (str.contains("\r\n\r\n"))
/*     */                   {
/* 280 */                     System.out.println(str);
/*     */                     
/* 282 */                     String server_name = "unknown";
/* 283 */                     String auth = "none";
/* 284 */                     String response = "unknown";
/*     */                     
/* 286 */                     StringTokenizer tok = new StringTokenizer(str, "\n");
/*     */                     
/* 288 */                     int line_num = 0;
/*     */                     
/* 290 */                     while (tok.hasMoreTokens())
/*     */                     {
/* 292 */                       String token = tok.nextToken().trim();
/*     */                       
/* 294 */                       if (token.length() != 0)
/*     */                       {
/*     */ 
/*     */ 
/*     */ 
/* 299 */                         line_num++;
/*     */                         
/* 301 */                         if (line_num == 1)
/*     */                         {
/* 303 */                           int pos = token.indexOf(' ');
/*     */                           
/* 305 */                           if (pos != -1)
/*     */                           {
/* 307 */                             response = token.substring(pos + 1).trim();
/*     */                           }
/*     */                         }
/*     */                         else {
/* 311 */                           int pos = token.indexOf(':');
/*     */                           
/* 313 */                           if (pos != -1)
/*     */                           {
/* 315 */                             String lhs = token.substring(0, pos).trim().toLowerCase(MessageText.LOCALE_ENGLISH);
/* 316 */                             String rhs = token.substring(pos + 1).trim();
/*     */                             
/* 318 */                             if (lhs.equals("server"))
/*     */                             {
/* 320 */                               if (!response.startsWith("200"))
/*     */                               {
/* 322 */                                 server_name = rhs;
/*     */                               }
/* 324 */                             } else if (lhs.equals("via"))
/*     */                             {
/* 326 */                               server_name = rhs;
/*     */                               
/* 328 */                               int p = server_name.indexOf(' ');
/*     */                               
/* 330 */                               if (p != -1)
/*     */                               {
/* 332 */                                 server_name = server_name.substring(p + 1).trim();
/*     */                               }
/*     */                             }
/* 335 */                             else if (lhs.equals("proxy-authenticate"))
/*     */                             {
/* 337 */                               auth = rhs;
/*     */                             }
/*     */                           }
/*     */                         }
/*     */                       }
/*     */                     }
/* 343 */                     NetworkAdminHTTPProxyImpl.1.this.val$details[0] = new NetworkAdminHTTPProxyImpl.ProxyDetails(server_name, response, auth);
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 349 */                     this.val$transport.close("Done");
/*     */                     
/* 351 */                     NetworkAdminHTTPProxyImpl.1.this.val$result[0] = 3;
/*     */                     
/* 353 */                     NetworkAdminHTTPProxyImpl.1.this.val$sem.release();
/*     */                   }
/*     */                   else
/*     */                   {
/* 357 */                     TCPNetworkManager.getSingleton().getReadSelector().resumeSelects(this.val$transport.getSocketChannel());
/*     */                   }
/*     */                   
/* 360 */                   return true;
/*     */                 }
/*     */                 catch (Throwable t) {}
/*     */                 
/*     */ 
/*     */ 
/* 366 */                 return false;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */               {
/* 377 */                 NetworkAdminHTTPProxyImpl.1.this.val$result[0] = 1;
/* 378 */                 NetworkAdminHTTPProxyImpl.1.this.val$error[0] = new NetworkAdminException("Proxy error", msg);
/*     */                 
/* 380 */                 this.val$transport.close("Proxy error");
/*     */                 
/* 382 */                 NetworkAdminHTTPProxyImpl.1.this.val$sem.release(); } }, null);
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */ 
/* 389 */             result[0] = 1;
/* 390 */             error[0] = new NetworkAdminException("Proxy connect failed", t);
/*     */             
/* 392 */             sem.release();
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */         public void connectFailure(Throwable failure_msg)
/*     */         {
/* 399 */           result[0] = 0;
/* 400 */           error[0] = new NetworkAdminException("Connect failed", failure_msg);
/*     */           
/* 402 */           sem.release();
/*     */         }
/*     */         
/* 405 */       };
/* 406 */       TCPNetworkManager.getSingleton().getConnectDisconnectManager().requestNewConnection(socks_address, connect_listener, 3);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 411 */       result[0] = 0;
/* 412 */       error[0] = new NetworkAdminException("Connect failed", e);
/*     */       
/* 414 */       sem.release();
/*     */     }
/*     */     
/* 417 */     if (!sem.reserve(10000L))
/*     */     {
/* 419 */       result[0] = 0;
/* 420 */       error[0] = new NetworkAdminException("Connect timeout");
/*     */     }
/*     */     
/* 423 */     if (result[0] == 3)
/*     */     {
/* 425 */       return details[0];
/*     */     }
/*     */     
/* 428 */     throw error[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class ProxyDetails
/*     */     implements NetworkAdminHTTPProxy.Details
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final String response;
/*     */     
/*     */     private final String auth_type;
/*     */     
/*     */ 
/*     */     protected ProxyDetails(String _name, String _response, String _auth_type)
/*     */     {
/* 445 */       this.name = _name;
/* 446 */       this.response = _response;
/* 447 */       this.auth_type = _auth_type;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getServerName()
/*     */     {
/* 453 */       return this.name;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getResponse()
/*     */     {
/* 459 */       return this.response;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getAuthenticationType()
/*     */     {
/* 465 */       return this.auth_type;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminHTTPProxyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */