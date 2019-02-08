/*     */ package org.gudy.azureus2.core3.tracker.server;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerFactoryImpl;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TRTrackerServerFactory
/*     */ {
/*     */   public static final int PR_TCP = 1;
/*     */   public static final int PR_UDP = 2;
/*     */   public static final int PR_DHT = 3;
/*     */   
/*     */   public static TRTrackerServer create(int protocol, int port, boolean apply_ip_filter, boolean main_tracker)
/*     */     throws TRTrackerServerException
/*     */   {
/*  47 */     return TRTrackerServerFactoryImpl.create("<none>", protocol, port, null, false, apply_ip_filter, main_tracker, true, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TRTrackerServer createSSL(int protocol, int port, boolean apply_ip_filter, boolean main_tracker)
/*     */     throws TRTrackerServerException
/*     */   {
/*  59 */     return TRTrackerServerFactoryImpl.create("<none>", protocol, port, null, true, apply_ip_filter, main_tracker, true, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TRTrackerServer create(String name, int protocol, int port, boolean apply_ip_filter, boolean main_tracker)
/*     */     throws TRTrackerServerException
/*     */   {
/*  72 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, null, false, apply_ip_filter, main_tracker, true, null);
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
/*     */   public static TRTrackerServer create(String name, int protocol, int port, boolean apply_ip_filter, boolean main_tracker, boolean start_up_ready)
/*     */     throws TRTrackerServerException
/*     */   {
/*  86 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, null, false, apply_ip_filter, main_tracker, start_up_ready, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TRTrackerServer createSSL(String name, int protocol, int port, boolean apply_ip_filter, boolean main_tracker)
/*     */     throws TRTrackerServerException
/*     */   {
/*  99 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, null, true, apply_ip_filter, main_tracker, true, null);
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
/*     */   public static TRTrackerServer createSSL(String name, int protocol, int port, boolean apply_ip_filter, boolean main_tracker, boolean startup_ready)
/*     */     throws TRTrackerServerException
/*     */   {
/* 113 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, null, true, apply_ip_filter, main_tracker, startup_ready, null);
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
/*     */   public static TRTrackerServer create(String name, int protocol, int port, InetAddress bind_ip, boolean apply_ip_filter, boolean main_tracker)
/*     */     throws TRTrackerServerException
/*     */   {
/* 127 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, bind_ip, false, apply_ip_filter, main_tracker, true, null);
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
/*     */   public static TRTrackerServer createSSL(String name, int protocol, int port, InetAddress bind_ip, boolean apply_ip_filter, boolean main_tracker)
/*     */     throws TRTrackerServerException
/*     */   {
/* 141 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, bind_ip, true, apply_ip_filter, main_tracker, true, null);
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
/*     */   public static TRTrackerServer create(String name, int protocol, int port, InetAddress bind_ip, boolean apply_ip_filter, boolean main_tracker, Map<String, Object> properties)
/*     */     throws TRTrackerServerException
/*     */   {
/* 156 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, bind_ip, false, apply_ip_filter, main_tracker, true, properties);
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
/*     */   public static TRTrackerServer createSSL(String name, int protocol, int port, InetAddress bind_ip, boolean apply_ip_filter, boolean main_tracker, Map<String, Object> properties)
/*     */     throws TRTrackerServerException
/*     */   {
/* 171 */     return TRTrackerServerFactoryImpl.create(name, protocol, port, bind_ip, true, apply_ip_filter, main_tracker, true, properties);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(TRTrackerServerFactoryListener l)
/*     */   {
/* 178 */     TRTrackerServerFactoryImpl.addListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeListener(TRTrackerServerFactoryListener l)
/*     */   {
/* 185 */     TRTrackerServerFactoryImpl.removeListener(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */