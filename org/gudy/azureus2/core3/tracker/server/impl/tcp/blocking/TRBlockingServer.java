/*     */ package org.gudy.azureus2.core3.tracker.server.impl.tcp.blocking;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import javax.net.ssl.SSLServerSocket;
/*     */ import javax.net.ssl.SSLServerSocketFactory;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerTCP;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class TRBlockingServer
/*     */   extends TRTrackerServerTCP
/*     */ {
/*  49 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   
/*     */ 
/*     */ 
/*     */   private InetAddress current_bind_ip;
/*     */   
/*     */ 
/*     */ 
/*     */   private ServerSocket server_socket;
/*     */   
/*     */ 
/*     */   private volatile boolean closed;
/*     */   
/*     */ 
/*     */ 
/*     */   public TRBlockingServer(String _name, int _port, InetAddress _bind_ip, boolean _ssl, boolean _apply_ip_filter, boolean _start_up_ready)
/*     */     throws TRTrackerServerException
/*     */   {
/*  67 */     super(_name, _port, _ssl, _apply_ip_filter, _start_up_ready);
/*     */     
/*  69 */     boolean ok = false;
/*     */     try
/*     */     {
/*  72 */       InetAddress bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */       
/*  74 */       String tr_bind_ip = COConfigurationManager.getStringParameter("Bind IP for Tracker", "");
/*     */       
/*  76 */       if (tr_bind_ip.length() >= 7)
/*     */       {
/*     */         try
/*     */         {
/*  80 */           bind_ip = InetAddress.getByName(tr_bind_ip);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  84 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/*  88 */       if (_ssl)
/*     */       {
/*  90 */         if (_port == 0)
/*     */         {
/*  92 */           throw new TRTrackerServerException("port of 0 not currently supported for SSL");
/*     */         }
/*     */         try
/*     */         {
/*  96 */           SSLServerSocketFactory factory = SESecurityManager.getSSLServerSocketFactory();
/*     */           
/*  98 */           if (factory == null)
/*     */           {
/* 100 */             throw new TRTrackerServerException("TRTrackerServer: failed to get SSL factory");
/*     */           }
/*     */           
/*     */           SSLServerSocket ssl_server_socket;
/*     */           SSLServerSocket ssl_server_socket;
/* 105 */           if (_bind_ip != null)
/*     */           {
/* 107 */             this.current_bind_ip = _bind_ip;
/*     */             
/* 109 */             ssl_server_socket = (SSLServerSocket)factory.createServerSocket(getPort(), 128, _bind_ip);
/*     */           } else { SSLServerSocket ssl_server_socket;
/* 111 */             if (bind_ip == null)
/*     */             {
/* 113 */               ssl_server_socket = (SSLServerSocket)factory.createServerSocket(getPort(), 128);
/*     */             }
/*     */             else
/*     */             {
/* 117 */               this.current_bind_ip = bind_ip;
/*     */               
/* 119 */               ssl_server_socket = (SSLServerSocket)factory.createServerSocket(getPort(), 128, bind_ip);
/*     */             }
/*     */           }
/* 122 */           String[] cipherSuites = ssl_server_socket.getSupportedCipherSuites();
/*     */           
/* 124 */           ssl_server_socket.setEnabledCipherSuites(cipherSuites);
/*     */           
/* 126 */           ssl_server_socket.setNeedClientAuth(false);
/*     */           
/* 128 */           ssl_server_socket.setReuseAddress(true);
/*     */           
/* 130 */           this.server_socket = ssl_server_socket;
/*     */           
/* 132 */           Thread accept_thread = new AEThread("TRTrackerServer:accept.loop(ssl)")
/*     */           {
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */ 
/* 138 */               TRBlockingServer.this.acceptLoop(TRBlockingServer.this.server_socket);
/*     */             }
/*     */             
/* 141 */           };
/* 142 */           accept_thread.setDaemon(true);
/*     */           
/* 144 */           accept_thread.start();
/*     */           
/* 146 */           Logger.log(new LogEvent(LOGID, "TRTrackerServer: SSL listener established on port " + getPort()));
/*     */           
/*     */ 
/*     */ 
/* 150 */           ok = true;
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 155 */           Logger.logTextResource(new LogAlert(false, 3, "Tracker.alert.listenfail"), new String[] { "" + getPort() });
/*     */           
/*     */ 
/*     */ 
/* 159 */           Logger.log(new LogEvent(LOGID, "TRTrackerServer: SSL listener failed on port " + getPort(), e));
/*     */           
/*     */ 
/* 162 */           if ((e instanceof TRTrackerServerException))
/*     */           {
/* 164 */             throw ((TRTrackerServerException)e);
/*     */           }
/*     */           
/*     */ 
/* 168 */           throw new TRTrackerServerException("TRTrackerServer: accept fails: " + e.toString());
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */         try
/*     */         {
/* 177 */           int port = getPort();
/*     */           ServerSocket ss;
/* 179 */           ServerSocket ss; if (_bind_ip != null)
/*     */           {
/* 181 */             this.current_bind_ip = _bind_ip;
/*     */             
/* 183 */             ss = new ServerSocket(port, 1024, _bind_ip);
/*     */           } else { ServerSocket ss;
/* 185 */             if (bind_ip == null)
/*     */             {
/* 187 */               ss = new ServerSocket(port, 1024);
/*     */             }
/*     */             else
/*     */             {
/* 191 */               this.current_bind_ip = bind_ip;
/*     */               
/* 193 */               ss = new ServerSocket(port, 1024, bind_ip);
/*     */             }
/*     */           }
/* 196 */           if (port == 0)
/*     */           {
/* 198 */             setPort(ss.getLocalPort());
/*     */           }
/*     */           
/* 201 */           ss.setReuseAddress(true);
/*     */           
/* 203 */           this.server_socket = ss;
/*     */           
/* 205 */           Thread accept_thread = new AEThread("TRTrackerServer:accept.loop")
/*     */           {
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */ 
/* 211 */               TRBlockingServer.this.acceptLoop(TRBlockingServer.this.server_socket);
/*     */             }
/*     */             
/* 214 */           };
/* 215 */           accept_thread.setDaemon(true);
/*     */           
/* 217 */           accept_thread.start();
/*     */           
/* 219 */           Logger.log(new LogEvent(LOGID, "TRTrackerServer: listener established on port " + getPort()));
/*     */           
/*     */ 
/* 222 */           ok = true;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 226 */           Logger.logTextResource(new LogAlert(false, 3, "Tracker.alert.listenfail"), new String[] { "" + getPort() });
/*     */           
/*     */ 
/*     */ 
/* 230 */           throw new TRTrackerServerException("TRTrackerServer: accept fails", e);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 235 */       if (!ok)
/*     */       {
/* 237 */         destroySupport();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getBindIP()
/*     */   {
/* 245 */     return this.current_bind_ip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void acceptLoop(ServerSocket ss)
/*     */   {
/* 252 */     long successfull_accepts = 0L;
/* 253 */     long failed_accepts = 0L;
/*     */     
/* 255 */     while (!this.closed) {
/*     */       try
/*     */       {
/* 258 */         Socket socket = ss.accept();
/*     */         
/* 260 */         successfull_accepts += 1L;
/*     */         
/* 262 */         String ip = socket.getInetAddress().getHostAddress();
/*     */         
/* 264 */         if ((!isIPFilterEnabled()) || (!this.ip_filter.isInRange(ip, "Tracker", null)))
/*     */         {
/* 266 */           runProcessor(new TRBlockingServerProcessor(this, socket));
/*     */         }
/*     */         else
/*     */         {
/* 270 */           socket.close();
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 275 */         if (!this.closed)
/*     */         {
/* 277 */           failed_accepts += 1L;
/*     */           
/* 279 */           Logger.log(new LogEvent(LOGID, "TRTrackerServer: listener failed on port " + getPort(), e));
/*     */           
/*     */ 
/* 282 */           if ((failed_accepts > 100L) && (successfull_accepts == 0L))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 287 */             Logger.logTextResource(new LogAlert(false, 3, "Network.alert.acceptfail"), new String[] { "" + getPort(), "TCP" });
/*     */             
/*     */ 
/*     */ 
/* 291 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void closeSupport()
/*     */   {
/* 301 */     this.closed = true;
/*     */     try
/*     */     {
/* 304 */       this.server_socket.close();
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/* 310 */     destroySupport();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/tcp/blocking/TRBlockingServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */