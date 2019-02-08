/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector.SelectListener;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
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
/*     */ public class VirtualBlockingServerChannelSelector
/*     */   implements VirtualServerChannelSelector
/*     */ {
/*  38 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*  39 */   private ServerSocketChannel server_channel = null;
/*     */   
/*     */   private final InetSocketAddress bind_address;
/*     */   private final int receive_buffer_size;
/*     */   private final VirtualServerChannelSelector.SelectListener listener;
/*  44 */   protected final AEMonitor this_mon = new AEMonitor("VirtualServerChannelSelector");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long last_accept_time;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public VirtualBlockingServerChannelSelector(InetSocketAddress _bind_address, int so_rcvbuf_size, VirtualServerChannelSelector.SelectListener listener)
/*     */   {
/*  56 */     this.bind_address = _bind_address;
/*  57 */     this.receive_buffer_size = so_rcvbuf_size;
/*  58 */     this.listener = listener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/*     */     try
/*     */     {
/*  68 */       this.this_mon.enter();
/*     */       
/*  70 */       if (!isRunning()) {
/*     */         try {
/*  72 */           this.server_channel = ServerSocketChannel.open();
/*     */           
/*  74 */           this.server_channel.socket().setReuseAddress(true);
/*  75 */           if (this.receive_buffer_size > 0) { this.server_channel.socket().setReceiveBufferSize(this.receive_buffer_size);
/*     */           }
/*  77 */           this.server_channel.socket().bind(this.bind_address, 1024);
/*     */           
/*  79 */           if (Logger.isEnabled()) { Logger.log(new LogEvent(LOGID, "TCP incoming server socket " + this.bind_address));
/*     */           }
/*  81 */           AEThread accept_thread = new AEThread("VServerSelector:port" + this.bind_address.getPort()) {
/*     */             public void runSupport() {
/*  83 */               VirtualBlockingServerChannelSelector.this.accept_loop();
/*     */             }
/*  85 */           };
/*  86 */           accept_thread.setDaemon(true);
/*  87 */           accept_thread.start();
/*     */         }
/*     */         catch (Throwable t) {
/*  90 */           Debug.out(t);
/*  91 */           Logger.log(new LogAlert(false, "ERROR, unable to bind TCP incoming server socket to " + this.bind_address.getPort(), t));
/*     */         }
/*     */         
/*  94 */         this.last_accept_time = SystemTime.getCurrentTime();
/*     */       }
/*     */     }
/*     */     finally {
/*  98 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */     try
/*     */     {
/* 108 */       this.this_mon.enter();
/*     */       
/* 110 */       if (this.server_channel != null) {
/*     */         try {
/* 112 */           this.server_channel.close();
/* 113 */           this.server_channel = null;
/*     */         } catch (Throwable t) {
/* 115 */           Debug.out(t);
/*     */         }
/*     */       }
/*     */     } finally {
/* 119 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void accept_loop()
/*     */   {
/* 125 */     while (isRunning()) {
/*     */       try {
/* 127 */         SocketChannel client_channel = this.server_channel.accept();
/* 128 */         this.last_accept_time = SystemTime.getCurrentTime();
/*     */         try {
/* 130 */           client_channel.configureBlocking(false);
/*     */         } catch (IOException e) {
/* 132 */           client_channel.close();
/* 133 */           throw e;
/*     */         }
/* 135 */         this.listener.newConnectionAccepted(this.server_channel, client_channel);
/*     */ 
/*     */       }
/*     */       catch (AsynchronousCloseException e) {}catch (Throwable t)
/*     */       {
/*     */ 
/* 141 */         Debug.out(t);
/* 142 */         try { Thread.sleep(500L); } catch (Exception e) { e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/* 154 */     if ((this.server_channel != null) && (this.server_channel.isOpen())) return true;
/* 155 */     return false;
/*     */   }
/*     */   
/*     */   public InetAddress getBoundToAddress()
/*     */   {
/* 160 */     if (this.server_channel != null) {
/* 161 */       return this.server_channel.socket().getInetAddress();
/*     */     }
/* 163 */     return null;
/*     */   }
/*     */   
/*     */   public int getPort()
/*     */   {
/* 168 */     if (this.server_channel != null) {
/* 169 */       return this.server_channel.socket().getLocalPort();
/*     */     }
/* 171 */     return -1;
/*     */   }
/*     */   
/*     */   public long getTimeOfLastAccept() {
/* 175 */     return this.last_accept_time;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/VirtualBlockingServerChannelSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */