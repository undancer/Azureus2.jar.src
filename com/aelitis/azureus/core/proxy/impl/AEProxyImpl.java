/*     */ package com.aelitis.azureus.core.proxy.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import com.aelitis.azureus.core.proxy.AEProxy;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyException;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyHandler;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
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
/*     */ public class AEProxyImpl
/*     */   implements AEProxy, VirtualChannelSelector.VirtualSelectorListener
/*     */ {
/*  48 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private static final int DEBUG_PERIOD = 60000;
/*     */   
/*     */   private long last_debug;
/*     */   
/*     */   private int port;
/*     */   private final long connect_timeout;
/*     */   private final long read_timeout;
/*     */   private final AEProxyHandler proxy_handler;
/*     */   private ServerSocketChannel ssc;
/*     */   final VirtualChannelSelector read_selector;
/*     */   final VirtualChannelSelector connect_selector;
/*     */   final VirtualChannelSelector write_selector;
/*  62 */   private final List<AEProxyConnectionImpl> processors = new ArrayList();
/*     */   
/*  64 */   private final HashMap write_select_regs = new HashMap();
/*     */   
/*     */   private boolean allow_external_access;
/*     */   
/*  68 */   private final AEMonitor this_mon = new AEMonitor("AEProxyImpl");
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile boolean destroyed;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEProxyImpl(int _port, long _connect_timeout, long _read_timeout, AEProxyHandler _proxy_handler)
/*     */     throws AEProxyException
/*     */   {
/*  80 */     this.port = _port;
/*  81 */     this.connect_timeout = _connect_timeout;
/*  82 */     this.read_timeout = _read_timeout;
/*  83 */     this.proxy_handler = _proxy_handler;
/*     */     
/*  85 */     String name = "Proxy:" + this.port;
/*     */     
/*  87 */     this.read_selector = new VirtualChannelSelector(name, 1, false);
/*  88 */     this.connect_selector = new VirtualChannelSelector(name, 8, true);
/*  89 */     this.write_selector = new VirtualChannelSelector(name, 4, true);
/*     */     
/*     */     try
/*     */     {
/*  93 */       this.ssc = ServerSocketChannel.open();
/*     */       
/*  95 */       ServerSocket ss = this.ssc.socket();
/*     */       
/*  97 */       ss.setReuseAddress(true);
/*     */       
/*  99 */       ss.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), this.port), 128);
/*     */       
/* 101 */       if (this.port == 0)
/*     */       {
/* 103 */         this.port = ss.getLocalPort();
/*     */       }
/*     */       
/* 106 */       new AEThread2("AEProxy:connect.loop")
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 111 */           AEProxyImpl.this.selectLoop(AEProxyImpl.this.connect_selector);
/*     */         }
/*     */         
/*     */ 
/* 115 */       }.start();
/* 116 */       new AEThread2("AEProxy:read.loop")
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 121 */           AEProxyImpl.this.selectLoop(AEProxyImpl.this.read_selector);
/*     */         }
/*     */         
/* 124 */       }.start();
/* 125 */       new AEThread2("AEProxy:write.loop")
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 130 */           AEProxyImpl.this.selectLoop(AEProxyImpl.this.write_selector);
/*     */         }
/*     */         
/*     */ 
/* 134 */       }.start();
/* 135 */       new AEThread2("AEProxy:accept.loop")
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 140 */           AEProxyImpl.this.acceptLoop(AEProxyImpl.this.ssc);
/*     */         }
/*     */       }.start();
/*     */       
/* 144 */       if (Logger.isEnabled()) {
/* 145 */         Logger.log(new LogEvent(LOGID, "AEProxy: listener established on port " + this.port));
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 150 */       Logger.logTextResource(new LogAlert(false, 3, "Tracker.alert.listenfail"), new String[] { "" + this.port });
/*     */       
/*     */ 
/*     */ 
/* 154 */       if (Logger.isEnabled()) {
/* 155 */         Logger.log(new LogEvent(LOGID, "AEProxy: listener failed on port " + this.port, e));
/*     */       }
/*     */       
/* 158 */       throw new AEProxyException("AEProxy: accept fails: " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAllowExternalConnections(boolean permit)
/*     */   {
/* 166 */     this.allow_external_access = permit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void acceptLoop(ServerSocketChannel ssc)
/*     */   {
/* 173 */     long successfull_accepts = 0L;
/* 174 */     long failed_accepts = 0L;
/*     */     
/* 176 */     while (!this.destroyed) {
/*     */       try
/*     */       {
/* 179 */         SocketChannel socket_channel = ssc.accept();
/*     */         
/* 181 */         successfull_accepts += 1L;
/*     */         
/* 183 */         if ((!this.allow_external_access) && (!socket_channel.socket().getInetAddress().isLoopbackAddress()))
/*     */         {
/* 185 */           if (Logger.isEnabled()) {
/* 186 */             Logger.log(new LogEvent(LOGID, 1, "AEProxy: incoming connection from '" + socket_channel.socket().getInetAddress() + "' - closed as not local"));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 191 */           socket_channel.close();
/*     */         }
/*     */         else
/*     */         {
/*     */           try {
/* 196 */             socket_channel.configureBlocking(false);
/*     */             
/* 198 */             socket_channel.socket().setTcpNoDelay(true);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 202 */             socket_channel.close();
/*     */             
/* 204 */             throw e;
/*     */           }
/*     */           
/* 207 */           AEProxyConnectionImpl processor = new AEProxyConnectionImpl(this, socket_channel, this.proxy_handler);
/*     */           
/* 209 */           if (!processor.isClosed())
/*     */           {
/* 211 */             boolean added = false;
/*     */             try
/*     */             {
/* 214 */               this.this_mon.enter();
/*     */               
/* 216 */               if (!this.destroyed)
/*     */               {
/* 218 */                 added = true;
/*     */                 
/* 220 */                 this.processors.add(processor);
/*     */                 
/* 222 */                 if (Logger.isEnabled()) {
/* 223 */                   Logger.log(new LogEvent(LOGID, "AEProxy: active processors = " + this.processors.size()));
/*     */                 }
/*     */               }
/*     */             }
/*     */             finally {
/* 228 */               this.this_mon.exit();
/*     */             }
/*     */             
/* 231 */             if (!added)
/*     */             {
/* 233 */               processor.close();
/*     */             }
/*     */             else
/*     */             {
/* 237 */               this.read_selector.register(socket_channel, this, processor);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 243 */         if (!this.destroyed)
/*     */         {
/* 245 */           failed_accepts += 1L;
/*     */           
/* 247 */           if (Logger.isEnabled()) {
/* 248 */             Logger.log(new LogEvent(LOGID, "AEProxy: listener failed on port " + this.port, e));
/*     */           }
/*     */           
/* 251 */           if ((failed_accepts > 100L) && (successfull_accepts == 0L))
/*     */           {
/*     */ 
/*     */ 
/* 255 */             Logger.logTextResource(new LogAlert(false, 3, "Network.alert.acceptfail"), new String[] { "" + this.port, "TCP" });
/*     */             
/*     */ 
/*     */ 
/* 259 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void close(AEProxyConnectionImpl processor)
/*     */   {
/*     */     try
/*     */     {
/* 271 */       this.this_mon.enter();
/*     */       
/* 273 */       this.processors.remove(processor);
/*     */     }
/*     */     finally
/*     */     {
/* 277 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void selectLoop(VirtualChannelSelector selector)
/*     */   {
/* 285 */     long last_time = 0L;
/*     */     
/* 287 */     while (!this.destroyed) {
/*     */       try
/*     */       {
/* 290 */         selector.select(100L);
/*     */         
/*     */ 
/*     */ 
/* 294 */         if (selector == this.read_selector)
/*     */         {
/* 296 */           long now = SystemTime.getCurrentTime();
/*     */           
/* 298 */           if (now < last_time)
/*     */           {
/* 300 */             last_time = now;
/*     */           }
/* 302 */           else if (now - last_time >= 5000L)
/*     */           {
/* 304 */             last_time = now;
/*     */             
/* 306 */             checkTimeouts();
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 311 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void checkTimeouts()
/*     */   {
/* 319 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 321 */     if (now - this.last_debug > 60000L)
/*     */     {
/* 323 */       this.last_debug = now;
/*     */       try
/*     */       {
/* 326 */         this.this_mon.enter();
/*     */         
/* 328 */         Iterator it = this.processors.iterator();
/*     */         
/* 330 */         while (it.hasNext())
/*     */         {
/* 332 */           AEProxyConnectionImpl processor = (AEProxyConnectionImpl)it.next();
/*     */           
/* 334 */           if (Logger.isEnabled()) {
/* 335 */             Logger.log(new LogEvent(LOGID, "AEProxy: active processor: " + processor.getStateString()));
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 340 */         this.this_mon.exit();
/*     */       }
/*     */     }
/*     */     
/* 344 */     if ((this.connect_timeout <= 0L) && (this.read_timeout <= 0L))
/*     */     {
/* 346 */       return;
/*     */     }
/*     */     
/* 349 */     List closes = new ArrayList();
/*     */     try
/*     */     {
/* 352 */       this.this_mon.enter();
/*     */       
/* 354 */       Iterator it = this.processors.iterator();
/*     */       
/* 356 */       while (it.hasNext())
/*     */       {
/* 358 */         AEProxyConnectionImpl processor = (AEProxyConnectionImpl)it.next();
/*     */         
/* 360 */         long diff = now - processor.getTimeStamp();
/*     */         
/* 362 */         if ((this.connect_timeout > 0L) && (diff >= this.connect_timeout) && (!processor.isConnected()))
/*     */         {
/*     */ 
/*     */ 
/* 366 */           closes.add(processor);
/*     */         }
/* 368 */         else if ((this.read_timeout > 0L) && (diff >= this.read_timeout) && (processor.isConnected()))
/*     */         {
/*     */ 
/*     */ 
/* 372 */           closes.add(processor);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 377 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 380 */     for (int i = 0; i < closes.size(); i++)
/*     */     {
/* 382 */       ((AEProxyConnectionImpl)closes.get(i)).failed(new SocketTimeoutException("timeout"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void requestWriteSelect(AEProxyConnectionImpl processor, SocketChannel sc)
/*     */   {
/* 392 */     if (this.write_select_regs.containsKey(sc)) {
/* 393 */       this.write_selector.resumeSelects(sc);
/*     */     }
/*     */     else {
/* 396 */       this.write_select_regs.put(sc, null);
/* 397 */       this.write_selector.register(sc, this, processor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void cancelWriteSelect(SocketChannel sc)
/*     */   {
/* 405 */     this.write_select_regs.remove(sc);
/* 406 */     this.write_selector.cancel(sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void requestReadSelect(AEProxyConnectionImpl processor, SocketChannel sc)
/*     */   {
/* 414 */     this.read_selector.register(sc, this, processor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void cancelReadSelect(SocketChannel sc)
/*     */   {
/* 421 */     this.read_selector.cancel(sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void requestConnectSelect(AEProxyConnectionImpl processor, SocketChannel sc)
/*     */   {
/* 429 */     this.connect_selector.register(sc, this, processor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void cancelConnectSelect(SocketChannel sc)
/*     */   {
/* 436 */     this.connect_selector.cancel(sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */   {
/* 445 */     AEProxyConnectionImpl processor = (AEProxyConnectionImpl)attachment;
/*     */     
/* 447 */     if (selector == this.read_selector)
/*     */     {
/* 449 */       return processor.read(sc);
/*     */     }
/* 451 */     if (selector == this.write_selector)
/*     */     {
/* 453 */       return processor.write(sc);
/*     */     }
/*     */     
/*     */ 
/* 457 */     return processor.connect(sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */   {
/* 468 */     AEProxyConnectionImpl processor = (AEProxyConnectionImpl)attachment;
/*     */     
/* 470 */     processor.failed(msg);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 476 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */     List<AEProxyConnectionImpl> to_close;
/*     */     try
/*     */     {
/* 485 */       this.this_mon.enter();
/*     */       
/* 487 */       this.destroyed = true;
/*     */       
/* 489 */       to_close = new ArrayList(this.processors);
/*     */     }
/*     */     finally
/*     */     {
/* 493 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 496 */     for (AEProxyConnectionImpl con : to_close) {
/*     */       try
/*     */       {
/* 499 */         con.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 505 */     if (this.ssc != null) {
/*     */       try
/*     */       {
/* 508 */         this.ssc.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 514 */     this.connect_selector.destroy();
/* 515 */     this.read_selector.destroy();
/* 516 */     this.write_selector.destroy();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/impl/AEProxyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */