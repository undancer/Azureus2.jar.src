/*     */ package org.gudy.azureus2.core3.tracker.server.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServer;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerFactoryListener;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerStats;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.dht.TRTrackerServerDHT;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerTCP;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.blocking.TRBlockingServer;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.nonblocking.TRNonBlockingServer;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.nonblocking.TRNonBlockingServerProcessor;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.nonblocking.TRNonBlockingServerProcessorFactory;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.udp.TRTrackerServerUDP;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AsyncController;
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
/*     */ public class TRTrackerServerFactoryImpl
/*     */ {
/*  58 */   protected static final CopyOnWriteList servers = new CopyOnWriteList();
/*     */   
/*  60 */   protected static final List listeners = new ArrayList();
/*  61 */   protected static final AEMonitor class_mon = new AEMonitor("TRTrackerServerFactory");
/*     */   
/*     */   static {
/*  64 */     Set types = new HashSet();
/*     */     
/*  66 */     types.add("tracker.read.bytes.total");
/*  67 */     types.add("tracker.write.bytes.total");
/*  68 */     types.add("tracker.announce.count");
/*  69 */     types.add("tracker.announce.time");
/*  70 */     types.add("tracker.scrape.count");
/*  71 */     types.add("tracker.scrape.time");
/*     */     
/*  73 */     AzureusCoreStats.registerProvider(types, new AzureusCoreStatsProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void updateStats(Set types, Map values)
/*     */       {
/*     */ 
/*     */ 
/*  82 */         long read_bytes = 0L;
/*  83 */         long write_bytes = 0L;
/*  84 */         long announce_count = 0L;
/*  85 */         long announce_time = 0L;
/*  86 */         long scrape_count = 0L;
/*  87 */         long scrape_time = 0L;
/*     */         
/*  89 */         Iterator it = TRTrackerServerFactoryImpl.servers.iterator();
/*     */         
/*  91 */         while (it.hasNext())
/*     */         {
/*  93 */           TRTrackerServerStats stats = ((TRTrackerServer)it.next()).getStats();
/*     */           
/*  95 */           read_bytes += stats.getBytesIn();
/*  96 */           write_bytes += stats.getBytesOut();
/*  97 */           announce_count += stats.getAnnounceCount();
/*  98 */           announce_time += stats.getAnnounceTime();
/*  99 */           scrape_count += stats.getScrapeCount();
/* 100 */           scrape_time += stats.getScrapeTime();
/*     */         }
/*     */         
/* 103 */         if (types.contains("tracker.read.bytes.total"))
/*     */         {
/* 105 */           values.put("tracker.read.bytes.total", new Long(read_bytes));
/*     */         }
/* 107 */         if (types.contains("tracker.write.bytes.total"))
/*     */         {
/* 109 */           values.put("tracker.write.bytes.total", new Long(write_bytes));
/*     */         }
/* 111 */         if (types.contains("tracker.announce.count"))
/*     */         {
/* 113 */           values.put("tracker.announce.count", new Long(announce_count));
/*     */         }
/* 115 */         if (types.contains("tracker.announce.time"))
/*     */         {
/* 117 */           values.put("tracker.announce.time", new Long(announce_time));
/*     */         }
/* 119 */         if (types.contains("tracker.scrape.count"))
/*     */         {
/* 121 */           values.put("tracker.scrape.count", new Long(scrape_count));
/*     */         }
/* 123 */         if (types.contains("tracker.scrape.time"))
/*     */         {
/* 125 */           values.put("tracker.scrape.time", new Long(scrape_time));
/*     */         }
/*     */       }
/*     */     });
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
/*     */   public static TRTrackerServer create(String name, int protocol, int port, InetAddress bind_ip, boolean ssl, boolean apply_ip_filter, boolean main_tracker, boolean start_up_ready, Map<String, Object> properties)
/*     */     throws TRTrackerServerException
/*     */   {
/* 145 */     if (properties == null)
/*     */     {
/* 147 */       properties = new HashMap();
/*     */     }
/*     */     
/* 150 */     Boolean pr_non_blocking = (Boolean)properties.get("nonblocking");
/*     */     try
/*     */     {
/* 153 */       class_mon.enter();
/*     */       
/*     */       TRTrackerServerImpl server;
/*     */       TRTrackerServerImpl server;
/* 157 */       if (protocol == 1)
/*     */       {
/* 159 */         boolean explicit_non_blocking = (pr_non_blocking != null) && (pr_non_blocking.booleanValue());
/*     */         
/* 161 */         boolean non_blocking = ((COConfigurationManager.getBooleanParameter("Tracker TCP NonBlocking")) && (main_tracker)) || (explicit_non_blocking);
/*     */         
/*     */ 
/*     */ 
/* 165 */         if ((non_blocking) && (!ssl))
/*     */         {
/* 167 */           TRNonBlockingServer nb_server = new TRNonBlockingServer(name, port, bind_ip, apply_ip_filter, start_up_ready, new TRNonBlockingServerProcessorFactory()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             public TRNonBlockingServerProcessor create(TRTrackerServerTCP _server, SocketChannel _socket)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */               return new TRTrackerServerFactoryImpl.NonBlockingProcessor(_server, _socket);
/*     */             }
/*     */             
/*     */ 
/* 185 */           });
/* 186 */           TRTrackerServerImpl server = nb_server;
/*     */           
/* 188 */           if (explicit_non_blocking)
/*     */           {
/* 190 */             nb_server.setRestrictNonBlocking(false);
/*     */           }
/*     */         }
/*     */         else {
/* 194 */           server = new TRBlockingServer(name, port, bind_ip, ssl, apply_ip_filter, start_up_ready);
/*     */         }
/*     */       } else { TRTrackerServerImpl server;
/* 197 */         if (protocol == 2)
/*     */         {
/* 199 */           if (ssl)
/*     */           {
/* 201 */             throw new TRTrackerServerException("TRTrackerServerFactory: UDP doesn't support SSL");
/*     */           }
/*     */           
/* 204 */           server = new TRTrackerServerUDP(name, port, start_up_ready);
/*     */         }
/*     */         else
/*     */         {
/* 208 */           server = new TRTrackerServerDHT(name, start_up_ready);
/*     */         }
/*     */       }
/* 211 */       servers.add(server);
/*     */       
/* 213 */       for (int i = 0; i < listeners.size(); i++)
/*     */       {
/* 215 */         ((TRTrackerServerFactoryListener)listeners.get(i)).serverCreated(server);
/*     */       }
/*     */       
/* 218 */       return server;
/*     */     }
/*     */     finally
/*     */     {
/* 222 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static void close(TRTrackerServerImpl server)
/*     */   {
/*     */     try
/*     */     {
/* 231 */       class_mon.enter();
/*     */       
/* 233 */       server.closeSupport();
/*     */       
/* 235 */       if (servers.remove(server))
/*     */       {
/* 237 */         for (int i = 0; i < listeners.size(); i++)
/*     */         {
/* 239 */           ((TRTrackerServerFactoryListener)listeners.get(i)).serverDestroyed(server);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 244 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void addListener(TRTrackerServerFactoryListener l)
/*     */   {
/*     */     try
/*     */     {
/* 253 */       class_mon.enter();
/*     */       
/* 255 */       listeners.add(l);
/*     */       
/* 257 */       Iterator it = servers.iterator();
/*     */       
/* 259 */       while (it.hasNext())
/*     */       {
/* 261 */         l.serverCreated((TRTrackerServer)it.next());
/*     */       }
/*     */     }
/*     */     finally {
/* 265 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void removeListener(TRTrackerServerFactoryListener l)
/*     */   {
/*     */     try
/*     */     {
/* 274 */       class_mon.enter();
/*     */       
/* 276 */       listeners.remove(l);
/*     */     }
/*     */     finally
/*     */     {
/* 280 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static class NonBlockingProcessor
/*     */     extends TRNonBlockingServerProcessor
/*     */   {
/*     */     protected NonBlockingProcessor(TRTrackerServerTCP _server, SocketChannel _socket)
/*     */     {
/* 293 */       super(_socket);
/*     */     }
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
/*     */     protected ByteArrayOutputStream process(String input_header, String lowercase_input_header, String url_path, InetSocketAddress remote_address, boolean announce_and_scrape_only, InputStream is, AsyncController async)
/*     */       throws IOException
/*     */     {
/* 308 */       ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
/*     */       
/* 310 */       InetSocketAddress local_address = null;
/*     */       
/* 312 */       processRequest(input_header, lowercase_input_header, url_path, local_address, remote_address, announce_and_scrape_only, false, is, os, async);
/*     */       
/* 314 */       return os;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */