/*     */ package org.gudy.azureus2.core3.tracker.server.impl.tcp.nonblocking;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector.SelectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelectorFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerTCP;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TRNonBlockingServer
/*     */   extends TRTrackerServerTCP
/*     */   implements VirtualServerChannelSelector.SelectListener
/*     */ {
/*  54 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   private static final int TIMEOUT_CHECK_INTERVAL = 10000;
/*     */   private static final int CLOSE_DELAY = 5000;
/*     */   private static int SELECT_LOOP_TIME;
/*     */   private TRNonBlockingServerProcessorFactory processor_factory;
/*     */   private final VirtualChannelSelector read_selector;
/*     */   private final VirtualChannelSelector write_selector;
/*     */   
/*  62 */   static { COConfigurationManager.addAndFireParameterListeners(new String[] { "network.tracker.tcp.select.time" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  72 */         TRNonBlockingServer.access$002(COConfigurationManager.getIntParameter("network.tracker.tcp.select.time", 100));
/*     */       }
/*     */     }); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  82 */   private List connections_to_close = new ArrayList();
/*     */   
/*  84 */   private List processors = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   private InetAddress current_bind_ip;
/*     */   
/*     */ 
/*     */   private long total_timeouts;
/*     */   
/*     */ 
/*     */   private long total_connections;
/*     */   
/*     */ 
/*  97 */   public static final int MAX_CONCURRENT_CONNECTIONS = COConfigurationManager.getIntParameter("Tracker TCP NonBlocking Conc Max");
/*     */   
/*  99 */   private final AEMonitor this_mon = new AEMonitor("TRNonBlockingServer");
/*     */   
/*     */   private VirtualServerChannelSelector accept_server;
/*     */   
/* 103 */   private boolean immediate_close = COConfigurationManager.getBooleanParameter("Tracker TCP NonBlocking Immediate Close");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private volatile boolean closed;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRNonBlockingServer(String _name, int _port, InetAddress _bind_ip, boolean _apply_ip_filter, TRNonBlockingServerProcessorFactory _processor_factory)
/*     */     throws TRTrackerServerException
/*     */   {
/* 117 */     this(_name, _port, _bind_ip, _apply_ip_filter, true, _processor_factory);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRNonBlockingServer(String _name, int _port, InetAddress _bind_ip, boolean _apply_ip_filter, boolean _start_up_ready, TRNonBlockingServerProcessorFactory _processor_factory)
/*     */     throws TRTrackerServerException
/*     */   {
/* 131 */     super(_name, _port, false, _apply_ip_filter, _start_up_ready);
/*     */     
/* 133 */     this.processor_factory = _processor_factory;
/*     */     
/* 135 */     this.read_selector = new VirtualChannelSelector(_name + ":" + _port, 1, false);
/* 136 */     this.write_selector = new VirtualChannelSelector(_name + ":" + _port, 4, true);
/*     */     
/* 138 */     boolean ok = false;
/*     */     try
/*     */     {
/*     */       InetSocketAddress address;
/*     */       InetSocketAddress address;
/* 143 */       if (_bind_ip == null)
/*     */       {
/* 145 */         _bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */         InetSocketAddress address;
/* 147 */         if (_bind_ip == null)
/*     */         {
/* 149 */           address = new InetSocketAddress(_port);
/*     */         }
/*     */         else
/*     */         {
/* 153 */           this.current_bind_ip = _bind_ip;
/*     */           
/* 155 */           address = new InetSocketAddress(_bind_ip, _port);
/*     */         }
/*     */       }
/*     */       else {
/* 159 */         this.current_bind_ip = _bind_ip;
/*     */         
/* 161 */         address = new InetSocketAddress(_bind_ip, _port);
/*     */       }
/*     */       
/* 164 */       this.accept_server = VirtualServerChannelSelectorFactory.createBlocking(address, 0, this);
/*     */       
/* 166 */       this.accept_server.start();
/*     */       
/* 168 */       if (_port == 0)
/*     */       {
/* 170 */         setPort(this.accept_server.getPort());
/*     */       }
/*     */       
/* 173 */       AEThread read_thread = new AEThread("TRTrackerServer:readSelector")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 179 */           TRNonBlockingServer.this.selectLoop(TRNonBlockingServer.this.read_selector);
/*     */         }
/*     */         
/* 182 */       };
/* 183 */       read_thread.setDaemon(true);
/*     */       
/* 185 */       read_thread.start();
/*     */       
/* 187 */       AEThread write_thread = new AEThread("TRTrackerServer:writeSelector")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 193 */           TRNonBlockingServer.this.selectLoop(TRNonBlockingServer.this.write_selector);
/*     */         }
/*     */         
/* 196 */       };
/* 197 */       write_thread.setDaemon(true);
/*     */       
/* 199 */       write_thread.start();
/*     */       
/* 201 */       AEThread close_thread = new AEThread("TRTrackerServer:closeScheduler")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 207 */           TRNonBlockingServer.this.closeLoop();
/*     */         }
/*     */         
/* 210 */       };
/* 211 */       close_thread.setDaemon(true);
/*     */       
/* 213 */       close_thread.start();
/*     */       
/* 215 */       Logger.log(new LogEvent(LOGID, "TRTrackerServer: Non-blocking listener established on port " + getPort()));
/*     */       
/*     */ 
/*     */ 
/* 219 */       ok = true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 223 */       Logger.logTextResource(new LogAlert(false, 3, "Tracker.alert.listenfail"), new String[] { "" + getPort() });
/*     */       
/*     */ 
/*     */ 
/* 227 */       throw new TRTrackerServerException("TRTrackerServer: accept fails", e);
/*     */     }
/*     */     finally
/*     */     {
/* 231 */       if (!ok)
/*     */       {
/* 233 */         destroySupport();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getBindIP()
/*     */   {
/* 241 */     return this.current_bind_ip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setImmediateClose(boolean immediate)
/*     */   {
/* 248 */     this.immediate_close = immediate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void selectLoop(VirtualChannelSelector selector)
/*     */   {
/* 255 */     long last_time = 0L;
/*     */     
/* 257 */     while (!this.closed) {
/*     */       try
/*     */       {
/* 260 */         selector.select(SELECT_LOOP_TIME);
/*     */         
/*     */ 
/*     */ 
/* 264 */         if (selector == this.read_selector)
/*     */         {
/* 266 */           long now = SystemTime.getCurrentTime();
/*     */           
/* 268 */           if (now < last_time)
/*     */           {
/* 270 */             last_time = now;
/*     */           }
/* 272 */           else if (now - last_time >= 10000L)
/*     */           {
/* 274 */             last_time = now;
/*     */             
/* 276 */             checkTimeouts(now);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 281 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void newConnectionAccepted(ServerSocketChannel server, SocketChannel channel)
/*     */   {
/* 291 */     final TRNonBlockingServerProcessor processor = this.processor_factory.create(this, channel);
/*     */     
/*     */     int num_processors;
/*     */     try
/*     */     {
/* 296 */       this.this_mon.enter();
/*     */       
/* 298 */       this.total_connections += 1L;
/*     */       
/* 300 */       this.processors.add(processor);
/*     */       
/* 302 */       num_processors = this.processors.size();
/*     */     }
/*     */     finally
/*     */     {
/* 306 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 309 */     if ((MAX_CONCURRENT_CONNECTIONS != 0) && (num_processors > MAX_CONCURRENT_CONNECTIONS))
/*     */     {
/*     */ 
/* 312 */       removeAndCloseConnection(processor);
/*     */     }
/* 314 */     else if ((isIPFilterEnabled()) && (this.ip_filter.isInRange(channel.socket().getInetAddress().getHostAddress(), "Tracker", null)))
/*     */     {
/*     */ 
/* 317 */       removeAndCloseConnection(processor);
/*     */     }
/*     */     else
/*     */     {
/* 321 */       Object read_listener = new VirtualChannelSelector.VirtualSelectorListener()
/*     */       {
/*     */         private boolean selector_registered;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */         {
/*     */           try
/*     */           {
/* 333 */             int read_result = processor.processRead();
/*     */             
/* 335 */             if (read_result == 0)
/*     */             {
/* 337 */               if (this.selector_registered)
/*     */               {
/* 339 */                 TRNonBlockingServer.this.read_selector.pauseSelects(sc);
/*     */               }
/*     */             }
/* 342 */             else if (read_result < 0)
/*     */             {
/* 344 */               TRNonBlockingServer.this.removeAndCloseConnection(processor);
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/* 349 */             else if (!this.selector_registered)
/*     */             {
/* 351 */               this.selector_registered = true;
/*     */               
/* 353 */               TRNonBlockingServer.this.read_selector.register(sc, this, null);
/*     */             }
/*     */             else
/*     */             {
/* 357 */               TRNonBlockingServer.this.read_selector.resumeSelects(sc);
/*     */             }
/*     */             
/*     */ 
/* 361 */             return read_result != 2;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 365 */             Debug.printStackTrace(e);
/*     */             
/* 367 */             TRNonBlockingServer.this.removeAndCloseConnection(processor);
/*     */           }
/* 369 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */         {
/* 380 */           TRNonBlockingServer.this.removeAndCloseConnection(processor);
/*     */         }
/*     */         
/* 383 */       };
/* 384 */       processor.setReadListener((VirtualChannelSelector.VirtualSelectorListener)read_listener);
/*     */       
/* 386 */       ((VirtualChannelSelector.VirtualSelectorListener)read_listener).selectSuccess(this.read_selector, channel, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void readyToWrite(final TRNonBlockingServerProcessor processor)
/*     */   {
/* 394 */     VirtualChannelSelector.VirtualSelectorListener write_listener = processor.getWriteListener();
/*     */     
/* 396 */     if (write_listener == null)
/*     */     {
/* 398 */       write_listener = new VirtualChannelSelector.VirtualSelectorListener()
/*     */       {
/*     */         private boolean selector_registered;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */         {
/*     */           try
/*     */           {
/* 410 */             int write_result = processor.processWrite();
/*     */             
/* 412 */             if (write_result > 0)
/*     */             {
/* 414 */               if (this.selector_registered)
/*     */               {
/* 416 */                 TRNonBlockingServer.this.write_selector.resumeSelects(sc);
/*     */               }
/*     */               else
/*     */               {
/* 420 */                 this.selector_registered = true;
/*     */                 
/* 422 */                 TRNonBlockingServer.this.write_selector.register(sc, this, null);
/*     */               }
/*     */             }
/* 425 */             else if (write_result == 0)
/*     */             {
/* 427 */               if (processor.getKeepAlive())
/*     */               {
/* 429 */                 processor.getReadListener().selectSuccess(TRNonBlockingServer.this.read_selector, sc, null);
/*     */               }
/*     */               else
/*     */               {
/* 433 */                 TRNonBlockingServer.this.removeAndCloseConnection(processor);
/*     */               }
/*     */             }
/* 436 */             else if (write_result < 0)
/*     */             {
/* 438 */               processor.failed();
/*     */               
/* 440 */               TRNonBlockingServer.this.removeAndCloseConnection(processor);
/*     */             }
/*     */             
/* 443 */             return write_result != 2;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 447 */             Debug.printStackTrace(e);
/*     */             
/* 449 */             TRNonBlockingServer.this.removeAndCloseConnection(processor);
/*     */           }
/* 451 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */         {
/* 462 */           TRNonBlockingServer.this.removeAndCloseConnection(processor);
/*     */         }
/*     */         
/* 465 */       };
/* 466 */       processor.setWriteListener(write_listener);
/*     */     }
/*     */     
/* 469 */     write_listener.selectSuccess(this.write_selector, processor.getSocketChannel(), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeAndCloseConnection(TRNonBlockingServerProcessor processor)
/*     */   {
/* 476 */     processor.completed();
/*     */     try
/*     */     {
/* 479 */       this.this_mon.enter();
/*     */       
/* 481 */       if (this.processors.remove(processor))
/*     */       {
/* 483 */         this.read_selector.cancel(processor.getSocketChannel());
/* 484 */         this.write_selector.cancel(processor.getSocketChannel());
/*     */         
/* 486 */         if (this.immediate_close) {
/*     */           try
/*     */           {
/* 489 */             processor.closed();
/*     */             
/* 491 */             processor.getSocketChannel().close();
/*     */ 
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */         else
/*     */         {
/* 498 */           this.connections_to_close.add(processor);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 504 */       this.this_mon.exit();
/*     */     }
/*     */   }
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
/*     */   public void checkTimeouts(long now)
/*     */   {
/*     */     try
/*     */     {
/* 540 */       this.this_mon.enter();
/*     */       
/* 542 */       List new_processors = new ArrayList(this.processors.size());
/*     */       
/* 544 */       for (int i = 0; i < this.processors.size(); i++)
/*     */       {
/* 546 */         TRNonBlockingServerProcessor processor = (TRNonBlockingServerProcessor)this.processors.get(i);
/*     */         
/* 548 */         if ((now - processor.getStartTime() > PROCESSING_GET_LIMIT) && (!processor.areTimeoutsDisabled()))
/*     */         {
/* 550 */           this.read_selector.cancel(processor.getSocketChannel());
/* 551 */           this.write_selector.cancel(processor.getSocketChannel());
/*     */           
/* 553 */           this.connections_to_close.add(processor);
/*     */           
/* 555 */           this.total_timeouts += 1L;
/*     */         }
/*     */         else
/*     */         {
/* 559 */           new_processors.add(processor);
/*     */         }
/*     */       }
/*     */       
/* 563 */       this.processors = new_processors;
/*     */     }
/*     */     finally
/*     */     {
/* 567 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void closeLoop()
/*     */   {
/* 577 */     List pending_list = new ArrayList();
/*     */     
/* 579 */     long default_delay = 3333L;
/*     */     
/* 581 */     long delay = default_delay;
/*     */     
/* 583 */     while (!this.closed)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 588 */       if (delay > 0L) {
/*     */         try
/*     */         {
/* 591 */           Thread.sleep(delay);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 595 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 601 */       long start = SystemTime.getCurrentTime();
/*     */       
/* 603 */       for (int i = 0; i < pending_list.size(); i++) {
/*     */         try
/*     */         {
/* 606 */           TRNonBlockingServerProcessor processor = (TRNonBlockingServerProcessor)pending_list.get(i);
/*     */           
/* 608 */           processor.closed();
/*     */           
/* 610 */           processor.getSocketChannel().close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 618 */         this.this_mon.enter();
/*     */         
/* 620 */         pending_list = this.connections_to_close;
/*     */         
/* 622 */         this.connections_to_close = new ArrayList();
/*     */       }
/*     */       finally
/*     */       {
/* 626 */         this.this_mon.exit();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 631 */       long duration = SystemTime.getCurrentTime() - start;
/*     */       
/* 633 */       if (duration < 0L)
/*     */       {
/* 635 */         duration = 0L;
/*     */       }
/*     */       
/* 638 */       delay = default_delay - duration;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void closeSupport()
/*     */   {
/* 645 */     this.closed = true;
/*     */     
/* 647 */     this.accept_server.stop();
/*     */     
/* 649 */     destroySupport();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/tcp/nonblocking/TRNonBlockingServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */