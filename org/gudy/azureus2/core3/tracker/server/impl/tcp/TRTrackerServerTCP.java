/*     */ package org.gudy.azureus2.core3.tracker.server.impl.tcp;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2.ExternalRequest;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerImpl;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AsyncController;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TRTrackerServerTCP
/*     */   extends TRTrackerServerImpl
/*     */ {
/*  41 */   private static final int THREAD_POOL_SIZE = Math.max(1, COConfigurationManager.getIntParameter("Tracker Max Threads"));
/*     */   
/*  43 */   public static final long PROCESSING_GET_LIMIT = Math.max(0, COConfigurationManager.getIntParameter("Tracker Max GET Time") * 1000);
/*  44 */   public static final int PROCESSING_POST_MULTIPLIER = Math.max(0, COConfigurationManager.getIntParameter("Tracker Max POST Time Multiplier"));
/*     */   
/*     */   private final boolean ssl;
/*     */   
/*     */   private int port;
/*     */   private final boolean apply_ip_filter;
/*  50 */   private boolean restrict_non_blocking_requests = TRTrackerServerImpl.restrict_non_blocking_requests;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final ThreadPool thread_pool;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerServerTCP(String _name, int _port, boolean _ssl, boolean _apply_ip_filter, boolean _start_up_ready)
/*     */     throws TRTrackerServerException
/*     */   {
/*  64 */     super(_name, _start_up_ready);
/*     */     
/*  66 */     this.port = _port;
/*  67 */     this.ssl = _ssl;
/*  68 */     this.apply_ip_filter = _apply_ip_filter;
/*     */     
/*  70 */     this.thread_pool = new ThreadPool("TrackerServer:TCP:" + this.port, THREAD_POOL_SIZE);
/*     */     
/*  72 */     if (PROCESSING_GET_LIMIT > 0L)
/*     */     {
/*  74 */       this.thread_pool.setExecutionLimit(PROCESSING_GET_LIMIT);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void runProcessor(TRTrackerServerProcessorTCP processor)
/*     */   {
/*  82 */     this.thread_pool.run(processor);
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isIPFilterEnabled()
/*     */   {
/*  88 */     return this.apply_ip_filter;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getRestrictNonBlocking()
/*     */   {
/*  94 */     return this.restrict_non_blocking_requests;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRestrictNonBlocking(boolean restrict)
/*     */   {
/* 101 */     this.restrict_non_blocking_requests = restrict;
/*     */   }
/*     */   
/* 104 */   static boolean LOG_DOS_TO_FILE = System.getProperty("azureus.log.dos") != null;
/*     */   
/*     */   protected static File dos_log_file;
/*     */   
/* 108 */   protected static final AEMonitor class_mon = new AEMonitor("TRTrackerServerTCP:class");
/*     */   
/* 110 */   final Map DOS_map = new LinkedHashMap(1000, 0.75F, true)
/*     */   {
/*     */ 
/*     */ 
/*     */     protected boolean removeEldestEntry(Map.Entry eldest)
/*     */     {
/*     */ 
/* 117 */       return TRTrackerServerTCP.this.checkDOSRemove(eldest);
/*     */     }
/*     */   };
/*     */   
/* 121 */   final List dos_list = new ArrayList(128);
/*     */   
/* 123 */   long last_dos_check = 0L;
/*     */   static final long MAX_DOS_ENTRIES = 10000L;
/*     */   static final long MAX_DOS_RETENTION = 10000L;
/*     */   static final int DOS_CHECK_DEAD_WOOD_COUNT = 512;
/*     */   static final int DOS_MIN_INTERVAL = 1000;
/* 128 */   int dos_check_count = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean checkDOS(String ip)
/*     */     throws UnknownHostException
/*     */   {
/* 136 */     InetAddress inet_address = InetAddress.getByName(ip);
/*     */     
/* 138 */     if ((inet_address.isLoopbackAddress()) || (InetAddress.getLocalHost().equals(inet_address)))
/*     */     {
/* 140 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 145 */     this.last_dos_check = SystemTime.getCurrentTime();
/*     */     
/* 147 */     DOSEntry entry = (DOSEntry)this.DOS_map.get(ip);
/*     */     boolean res;
/* 149 */     boolean res; if (entry == null)
/*     */     {
/* 151 */       entry = new DOSEntry(ip);
/*     */       
/* 153 */       this.DOS_map.put(ip, entry);
/*     */       
/* 155 */       res = false;
/*     */     }
/*     */     else
/*     */     {
/* 159 */       res = this.last_dos_check - entry.last_time < 1000L;
/*     */       
/* 161 */       if ((res) && (LOG_DOS_TO_FILE))
/*     */       {
/* 163 */         this.dos_list.add(entry);
/*     */       }
/*     */       
/* 166 */       entry.last_time = this.last_dos_check;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 171 */     this.dos_check_count += 1;
/*     */     
/* 173 */     if (this.dos_check_count == 512)
/*     */     {
/* 175 */       this.dos_check_count = 0;
/*     */       
/* 177 */       Iterator it = this.DOS_map.values().iterator();
/*     */       
/* 179 */       while (it.hasNext())
/*     */       {
/* 181 */         DOSEntry this_entry = (DOSEntry)it.next();
/*     */         
/* 183 */         if (this.last_dos_check - this_entry.last_time <= 10000L)
/*     */           break;
/* 185 */         it.remove();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */       if (this.dos_list.size() > 0) {
/*     */         try
/*     */         {
/* 196 */           class_mon.enter();
/*     */           
/* 198 */           if (dos_log_file == null)
/*     */           {
/* 200 */             dos_log_file = new File(System.getProperty("user.dir") + File.separator + "dos.log");
/*     */           }
/*     */           
/* 203 */           PrintWriter pw = null;
/*     */           
/*     */           try
/*     */           {
/* 207 */             pw = new PrintWriter(new FileWriter(dos_log_file, true));
/*     */             
/* 209 */             for (int i = 0; i < this.dos_list.size(); i++)
/*     */             {
/* 211 */               DOSEntry this_entry = (DOSEntry)this.dos_list.get(i);
/*     */               
/* 213 */               String ts = new SimpleDateFormat("HH:mm:ss - ").format(new Date(this_entry.last_time));
/*     */               
/* 215 */               pw.println(ts + this_entry.ip);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */             this.dos_list.clear();
/*     */             
/* 224 */             if (pw != null)
/*     */             {
/*     */               try
/*     */               {
/* 228 */                 pw.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}finally
/*     */           {
/* 222 */             this.dos_list.clear();
/*     */             
/* 224 */             if (pw != null)
/*     */             {
/*     */               try
/*     */               {
/* 228 */                 pw.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 236 */           class_mon.exit();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 241 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean checkDOSRemove(Map.Entry eldest)
/*     */   {
/* 248 */     boolean res = (this.DOS_map.size() > 10000L) || (this.last_dos_check - ((DOSEntry)eldest.getValue()).last_time > 10000L);
/*     */     
/*     */ 
/* 251 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   protected class DOSEntry
/*     */   {
/*     */     final String ip;
/*     */     
/*     */     long last_time;
/*     */     
/*     */ 
/*     */     protected DOSEntry(String _ip)
/*     */     {
/* 264 */       this.ip = _ip;
/* 265 */       this.last_time = TRTrackerServerTCP.this.last_dos_check;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 272 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPort(int _port)
/*     */   {
/* 279 */     this.port = _port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 285 */     return COConfigurationManager.getStringParameter("Tracker IP", "");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSSL()
/*     */   {
/* 291 */     return this.ssl;
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
/*     */   protected boolean handleExternalRequest(final TRTrackerServerProcessorTCP processor, final InetSocketAddress local_address, final InetSocketAddress client_address, final String user, final String url, final URL absolute_url, final String header, final InputStream is, final OutputStream os, final AsyncController async, final boolean[] keep_alive)
/*     */     throws IOException
/*     */   {
/* 311 */     final boolean original_ka = keep_alive[0];
/*     */     
/* 313 */     keep_alive[0] = false;
/*     */     
/* 315 */     for (TRTrackerServerListener listener : this.listeners)
/*     */     {
/* 317 */       if (listener.handleExternalRequest(client_address, user, url, absolute_url, header, is, os, async))
/*     */       {
/* 319 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 323 */     for (TRTrackerServerListener2 listener : this.listeners2)
/*     */     {
/* 325 */       TRTrackerServerListener2.ExternalRequest request = new TRTrackerServerListener2.ExternalRequest()
/*     */       {
/*     */ 
/*     */         public InetSocketAddress getClientAddress()
/*     */         {
/*     */ 
/* 331 */           return client_address;
/*     */         }
/*     */         
/*     */ 
/*     */         public InetSocketAddress getLocalAddress()
/*     */         {
/* 337 */           return local_address;
/*     */         }
/*     */         
/*     */ 
/*     */         public String getUser()
/*     */         {
/* 343 */           return user;
/*     */         }
/*     */         
/*     */ 
/*     */         public String getURL()
/*     */         {
/* 349 */           return url;
/*     */         }
/*     */         
/*     */ 
/*     */         public URL getAbsoluteURL()
/*     */         {
/* 355 */           return absolute_url;
/*     */         }
/*     */         
/*     */ 
/*     */         public String getHeader()
/*     */         {
/* 361 */           return header;
/*     */         }
/*     */         
/*     */ 
/*     */         public InputStream getInputStream()
/*     */         {
/* 367 */           return is;
/*     */         }
/*     */         
/*     */ 
/*     */         public OutputStream getOutputStream()
/*     */         {
/* 373 */           return os;
/*     */         }
/*     */         
/*     */ 
/*     */         public AsyncController getAsyncController()
/*     */         {
/* 379 */           return async;
/*     */         }
/*     */         
/*     */ 
/*     */         public boolean canKeepAlive()
/*     */         {
/* 385 */           return original_ka;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void setKeepAlive(boolean ka)
/*     */         {
/* 392 */           keep_alive[0] = ((original_ka) && (ka) ? 1 : false);
/*     */         }
/*     */         
/*     */ 
/*     */         public boolean isActive()
/*     */         {
/* 398 */           return processor.isActive();
/*     */         }
/*     */       };
/*     */       
/* 402 */       if (listener.handleExternalRequest(request))
/*     */       {
/* 404 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 408 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/tcp/TRTrackerServerTCP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */