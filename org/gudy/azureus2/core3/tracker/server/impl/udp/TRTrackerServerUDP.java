/*     */ package org.gudy.azureus2.core3.tracker.server.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerRequestListener;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerImpl;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
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
/*     */ public class TRTrackerServerUDP
/*     */   extends TRTrackerServerImpl
/*     */ {
/*  45 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   
/*     */ 
/*     */   private static final int THREAD_POOL_SIZE = 10;
/*     */   
/*     */ 
/*     */   private final ThreadPool thread_pool;
/*     */   
/*     */   private final int port;
/*     */   
/*     */   private InetAddress current_bind_ip;
/*     */   
/*     */   private DatagramSocket dg_socket;
/*     */   
/*     */   private volatile boolean closed;
/*     */   
/*     */ 
/*     */   public TRTrackerServerUDP(String _name, int _port, boolean _start_up_ready)
/*     */   {
/*  64 */     super(_name, _start_up_ready);
/*     */     
/*  66 */     this.port = _port;
/*     */     
/*  68 */     this.thread_pool = new ThreadPool("TrackerServer:UDP:" + this.port, 10);
/*     */     try
/*     */     {
/*  71 */       InetAddress bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */       
/*     */       DatagramSocket socket;
/*     */       
/*     */       InetSocketAddress address;
/*     */       DatagramSocket socket;
/*  77 */       if (bind_ip == null)
/*     */       {
/*  79 */         InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), this.port);
/*     */         
/*  81 */         socket = new DatagramSocket(this.port);
/*     */       }
/*     */       else
/*     */       {
/*  85 */         this.current_bind_ip = bind_ip;
/*     */         
/*  87 */         address = new InetSocketAddress(bind_ip, this.port);
/*     */         
/*  89 */         socket = new DatagramSocket(address);
/*     */       }
/*     */       
/*  92 */       socket.setReuseAddress(true);
/*     */       
/*  94 */       this.dg_socket = socket;
/*     */       
/*  96 */       final InetSocketAddress f_address = address;
/*     */       
/*  98 */       Thread recv_thread = new AEThread("TRTrackerServerUDP:recv.loop")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 104 */           TRTrackerServerUDP.this.recvLoop(TRTrackerServerUDP.this.dg_socket, f_address);
/*     */         }
/*     */         
/* 107 */       };
/* 108 */       recv_thread.setDaemon(true);
/*     */       
/* 110 */       recv_thread.start();
/*     */       
/* 112 */       Logger.log(new LogEvent(LOGID, "TRTrackerServerUDP: recv established on port " + this.port));
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 117 */       Logger.log(new LogEvent(LOGID, "TRTrackerServerUDP: DatagramSocket bind failed on port " + this.port, e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InetAddress getBindIP()
/*     */   {
/* 125 */     return this.current_bind_ip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void recvLoop(DatagramSocket socket, InetSocketAddress address)
/*     */   {
/* 133 */     long successful_accepts = 0L;
/* 134 */     long failed_accepts = 0L;
/*     */     
/* 136 */     while (!this.closed) {
/*     */       try
/*     */       {
/* 139 */         byte[] buf = new byte[' '];
/*     */         
/* 141 */         DatagramPacket packet = new DatagramPacket(buf, buf.length, address);
/*     */         
/* 143 */         socket.receive(packet);
/*     */         
/* 145 */         successful_accepts += 1L;
/*     */         
/* 147 */         failed_accepts = 0L;
/*     */         
/* 149 */         String ip = packet.getAddress().getHostAddress();
/*     */         
/* 151 */         if (!this.ip_filter.isInRange(ip, "Tracker", null))
/*     */         {
/* 153 */           this.thread_pool.run(new TRTrackerServerProcessorUDP(this, socket, packet));
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 158 */         if (!this.closed)
/*     */         {
/* 160 */           failed_accepts += 1L;
/*     */           
/* 162 */           Logger.log(new LogEvent(LOGID, "TRTrackerServer: receive failed on port " + this.port, e));
/*     */           
/*     */ 
/* 165 */           if (((failed_accepts > 100L) && (successful_accepts == 0L)) || (failed_accepts > 1000L))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 170 */             Logger.logTextResource(new LogAlert(false, 3, "Network.alert.acceptfail"), new String[] { "" + this.port, "UDP" });
/*     */             
/*     */ 
/*     */ 
/* 174 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 184 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 190 */     return COConfigurationManager.getStringParameter("Tracker IP", "");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSSL()
/*     */   {
/* 196 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRequestListener(TRTrackerServerRequestListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRequestListener(TRTrackerServerRequestListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void closeSupport()
/*     */   {
/* 214 */     this.closed = true;
/*     */     try
/*     */     {
/* 217 */       this.dg_socket.close();
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/* 223 */     destroySupport();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/udp/TRTrackerServerUDP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */