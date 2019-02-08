/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector.SelectListener;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public class VirtualNonBlockingServerChannelSelector
/*     */   implements VirtualServerChannelSelector
/*     */ {
/*  40 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*  42 */   private final List server_channels = new ArrayList();
/*     */   
/*     */   private final InetAddress bind_address;
/*     */   
/*     */   private final int start_port;
/*     */   private final int num_ports;
/*     */   private final int receive_buffer_size;
/*     */   private final VirtualServerChannelSelector.SelectListener listener;
/*  50 */   protected final AEMonitor this_mon = new AEMonitor("VirtualNonBlockingServerChannelSelector");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private long last_accept_time;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public VirtualNonBlockingServerChannelSelector(InetSocketAddress bind_address, int so_rcvbuf_size, VirtualServerChannelSelector.SelectListener listener)
/*     */   {
/*  68 */     this(bind_address.getAddress(), bind_address.getPort(), 1, so_rcvbuf_size, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public VirtualNonBlockingServerChannelSelector(InetAddress _bind_address, int _start_port, int _num_ports, int _so_rcvbuf_size, VirtualServerChannelSelector.SelectListener _listener)
/*     */   {
/*  79 */     this.bind_address = _bind_address;
/*  80 */     this.start_port = _start_port;
/*  81 */     this.num_ports = _num_ports;
/*  82 */     this.receive_buffer_size = _so_rcvbuf_size;
/*  83 */     this.listener = _listener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/*     */     try
/*     */     {
/*  92 */       this.this_mon.enter();
/*     */       
/*  94 */       if (!isRunning()) {
/*  95 */         for (int i = this.start_port; i < this.start_port + this.num_ports; i++) {
/*     */           try
/*     */           {
/*  98 */             final ServerSocketChannel server_channel = ServerSocketChannel.open();
/*     */             
/* 100 */             this.server_channels.add(server_channel);
/*     */             
/* 102 */             server_channel.socket().setReuseAddress(true);
/*     */             
/* 104 */             if (this.receive_buffer_size > 0) { server_channel.socket().setReceiveBufferSize(this.receive_buffer_size);
/*     */             }
/* 106 */             server_channel.socket().bind(new InetSocketAddress(this.bind_address, i), 1024);
/*     */             
/* 108 */             if (Logger.isEnabled()) { Logger.log(new LogEvent(LOGID, "TCP incoming server socket " + this.bind_address));
/*     */             }
/* 110 */             server_channel.configureBlocking(false);
/*     */             
/* 112 */             VirtualAcceptSelector.getSingleton().register(server_channel, new VirtualAcceptSelector.AcceptListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void newConnectionAccepted(SocketChannel channel)
/*     */               {
/*     */ 
/*     */ 
/* 120 */                 VirtualNonBlockingServerChannelSelector.this.last_accept_time = SystemTime.getCurrentTime();
/*     */                 
/* 122 */                 VirtualNonBlockingServerChannelSelector.this.listener.newConnectionAccepted(server_channel, channel);
/*     */               }
/*     */             });
/*     */           } catch (Throwable t) {
/* 126 */             Debug.out(t);
/* 127 */             Logger.log(new LogAlert(false, "ERROR, unable to bind TCP incoming server socket to " + i, t));
/*     */           }
/*     */         }
/*     */         
/* 131 */         this.last_accept_time = SystemTime.getCurrentTime();
/*     */       }
/*     */     }
/*     */     finally {
/* 135 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */     try
/*     */     {
/* 145 */       this.this_mon.enter();
/*     */       
/* 147 */       for (int i = 0; i < this.server_channels.size(); i++) {
/*     */         try {
/* 149 */           ServerSocketChannel server_channel = (ServerSocketChannel)this.server_channels.get(i);
/*     */           
/* 151 */           VirtualAcceptSelector.getSingleton().cancel(server_channel);
/*     */           
/* 153 */           server_channel.close();
/*     */         }
/*     */         catch (Throwable t) {
/* 156 */           Debug.out(t);
/*     */         }
/*     */       }
/* 159 */       this.server_channels.clear();
/*     */     }
/*     */     finally {
/* 162 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/* 172 */     if (this.server_channels.size() == 0) {
/* 173 */       return false;
/*     */     }
/*     */     
/* 176 */     ServerSocketChannel server_channel = (ServerSocketChannel)this.server_channels.get(0);
/*     */     
/* 178 */     if ((server_channel != null) && (server_channel.isOpen())) return true;
/* 179 */     return false;
/*     */   }
/*     */   
/*     */   public InetAddress getBoundToAddress()
/*     */   {
/* 184 */     if (this.server_channels.size() == 0) {
/* 185 */       return null;
/*     */     }
/* 187 */     ServerSocketChannel server_channel = (ServerSocketChannel)this.server_channels.get(0);
/*     */     
/* 189 */     return server_channel.socket().getInetAddress();
/*     */   }
/*     */   
/*     */   public int getPort() {
/* 193 */     if (this.server_channels.size() == 0) {
/* 194 */       return -1;
/*     */     }
/* 196 */     ServerSocketChannel server_channel = (ServerSocketChannel)this.server_channels.get(0);
/*     */     
/* 198 */     return server_channel.socket().getLocalPort();
/*     */   }
/*     */   
/*     */   public long getTimeOfLastAccept() {
/* 202 */     return this.last_accept_time;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/VirtualNonBlockingServerChannelSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */