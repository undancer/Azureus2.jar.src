/*     */ package org.gudy.azureus2.core3.tracker.server.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
/*     */ import org.gudy.azureus2.core3.util.ThreadPoolTask;
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
/*     */ public class TRTrackerServerNATChecker
/*     */ {
/*  41 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*  42 */   protected static final TRTrackerServerNATChecker singleton = new TRTrackerServerNATChecker();
/*     */   
/*     */   protected static final int THREAD_POOL_SIZE = 32;
/*     */   
/*     */   protected static final int CHECK_QUEUE_LIMIT = 2048;
/*  47 */   protected static int check_timeout = 15000;
/*     */   protected boolean enabled;
/*     */   protected ThreadPool thread_pool;
/*     */   
/*     */   protected static TRTrackerServerNATChecker getSingleton() {
/*  52 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  58 */   protected final List check_queue = new ArrayList();
/*  59 */   protected final AESemaphore check_queue_sem = new AESemaphore("TracerServerNATChecker");
/*  60 */   protected final AEMonitor check_queue_mon = new AEMonitor("TRTrackerServerNATChecker:Q");
/*     */   
/*  62 */   protected final AEMonitor this_mon = new AEMonitor("TRTrackerServerNATChecker");
/*     */   
/*     */ 
/*     */   protected TRTrackerServerNATChecker()
/*     */   {
/*  67 */     String enable_param = "Tracker NAT Check Enable";
/*  68 */     String timeout_param = "Tracker NAT Check Timeout";
/*     */     
/*  70 */     String[] params = { "Tracker NAT Check Enable", "Tracker NAT Check Timeout" };
/*     */     
/*  72 */     for (int i = 0; i < params.length; i++)
/*     */     {
/*  74 */       COConfigurationManager.addParameterListener(params[i], new ParameterListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void parameterChanged(String parameter_name)
/*     */         {
/*     */ 
/*     */ 
/*  82 */           TRTrackerServerNATChecker.this.checkConfig("Tracker NAT Check Enable", "Tracker NAT Check Timeout");
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*  87 */     checkConfig("Tracker NAT Check Enable", "Tracker NAT Check Timeout");
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isEnabled()
/*     */   {
/*  93 */     return this.enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkConfig(String enable_param, String timeout_param)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       this.this_mon.enter();
/*     */       
/* 104 */       this.enabled = COConfigurationManager.getBooleanParameter(enable_param);
/*     */       
/* 106 */       check_timeout = COConfigurationManager.getIntParameter(timeout_param) * 1000;
/*     */       
/* 108 */       if (check_timeout < 1000)
/*     */       {
/* 110 */         Debug.out("NAT check timeout too small - " + check_timeout);
/*     */         
/* 112 */         check_timeout = 1000;
/*     */       }
/*     */       
/* 115 */       if (this.thread_pool == null)
/*     */       {
/* 117 */         this.thread_pool = new ThreadPool("Tracker NAT Checker", 32);
/*     */         
/* 119 */         this.thread_pool.setExecutionLimit(check_timeout);
/*     */         
/* 121 */         Thread dispatcher_thread = new AEThread("Tracker NAT Checker Dispatcher")
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/*     */             for (;;)
/*     */             {
/* 129 */               TRTrackerServerNATChecker.this.check_queue_sem.reserve();
/*     */               
/*     */               ThreadPoolTask task;
/*     */               try
/*     */               {
/* 134 */                 TRTrackerServerNATChecker.this.check_queue_mon.enter();
/*     */                 
/* 136 */                 task = (ThreadPoolTask)TRTrackerServerNATChecker.this.check_queue.remove(0);
/*     */               }
/*     */               finally {
/* 139 */                 TRTrackerServerNATChecker.this.check_queue_mon.exit();
/*     */               }
/*     */               try
/*     */               {
/* 143 */                 TRTrackerServerNATChecker.this.thread_pool.run(task);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 147 */                 Debug.printStackTrace(e);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 152 */         };
/* 153 */         dispatcher_thread.setDaemon(true);
/*     */         
/* 155 */         dispatcher_thread.start();
/*     */       }
/*     */       else
/*     */       {
/* 159 */         this.thread_pool.setExecutionLimit(check_timeout);
/*     */       }
/*     */     }
/*     */     finally {
/* 163 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean addNATCheckRequest(final String host, final int port, final TRTrackerServerNatCheckerListener listener)
/*     */   {
/* 173 */     if ((!this.enabled) || (this.thread_pool == null))
/*     */     {
/* 175 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 179 */       this.check_queue_mon.enter();
/*     */       
/* 181 */       if (this.check_queue.size() > 2048) {
/* 182 */         if (Logger.isEnabled()) {
/* 183 */           Logger.log(new LogEvent(LOGID, 1, "NAT Check queue size too large, check for '" + host + ":" + port + "' skipped"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 188 */         listener.NATCheckComplete(true);
/*     */       }
/*     */       else
/*     */       {
/* 192 */         this.check_queue.add(new ThreadPoolTask()
/*     */         {
/*     */           protected Socket socket;
/*     */           
/*     */ 
/*     */ 
/*     */           public void runSupport()
/*     */           {
/* 200 */             boolean ok = false;
/*     */             try
/*     */             {
/* 203 */               InetSocketAddress address = new InetSocketAddress(AEProxyFactory.getAddressMapper().internalise(host), port);
/*     */               
/*     */ 
/* 206 */               this.socket = new Socket();
/*     */               
/* 208 */               this.socket.connect(address, TRTrackerServerNATChecker.check_timeout);
/*     */               
/* 210 */               ok = true;
/*     */               
/* 212 */               this.socket.close();
/*     */               
/* 214 */               this.socket = null; return;
/*     */ 
/*     */             }
/*     */             catch (Throwable e) {}finally
/*     */             {
/*     */ 
/* 220 */               listener.NATCheckComplete(ok);
/*     */               
/* 222 */               if (this.socket != null) {
/*     */                 try
/*     */                 {
/* 225 */                   this.socket.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void interruptTask()
/*     */           {
/* 236 */             if (this.socket != null) {
/*     */               try
/*     */               {
/* 239 */                 this.socket.close();
/*     */ 
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/*     */           }
/* 246 */         });
/* 247 */         this.check_queue_sem.release();
/*     */       }
/*     */     }
/*     */     finally {
/* 251 */       this.check_queue_mon.exit();
/*     */     }
/*     */     
/* 254 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerNATChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */