/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class IPToHostNameResolver
/*     */ {
/*     */   protected static AEThread2 resolver_thread;
/*  35 */   protected static final List request_queue = new ArrayList();
/*  36 */   protected static final AEMonitor request_mon = new AEMonitor("IPToHostNameResolver");
/*     */   
/*  38 */   protected static final AESemaphore request_semaphore = new AESemaphore("IPToHostNameResolver");
/*     */   
/*     */ 
/*     */ 
/*     */   public static IPToHostNameResolverRequest addResolverRequest(String ip, IPToHostNameResolverListener l)
/*     */   {
/*     */     try
/*     */     {
/*  46 */       request_mon.enter();
/*     */       
/*  48 */       IPToHostNameResolverRequest request = new IPToHostNameResolverRequest(ip, l);
/*     */       
/*  50 */       request_queue.add(request);
/*     */       
/*  52 */       request_semaphore.release();
/*     */       
/*  54 */       if (resolver_thread == null)
/*     */       {
/*  56 */         resolver_thread = new AEThread2("IPToHostNameResolver", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/*     */               for (;;)
/*     */               {
/*  65 */                 IPToHostNameResolver.request_semaphore.reserve(30000L);
/*     */                 
/*     */                 IPToHostNameResolverRequest req;
/*     */                 try
/*     */                 {
/*  70 */                   IPToHostNameResolver.request_mon.enter();
/*     */                   
/*  72 */                   if (IPToHostNameResolver.request_queue.isEmpty())
/*     */                   {
/*  74 */                     IPToHostNameResolver.resolver_thread = null;
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */                     IPToHostNameResolver.request_mon.exit(); break;
/*     */                   }
/*  79 */                   req = (IPToHostNameResolverRequest)IPToHostNameResolver.request_queue.remove(0);
/*     */                 }
/*     */                 finally
/*     */                 {
/*  83 */                   IPToHostNameResolver.request_mon.exit();
/*     */                 }
/*     */                 
/*  86 */                 IPToHostNameResolverListener listener = req.getListener();
/*     */                 
/*     */ 
/*     */ 
/*  90 */                 if (listener != null)
/*     */                 {
/*  92 */                   String ip = req.getIP();
/*     */                   
/*  94 */                   if (AENetworkClassifier.categoriseAddress(ip) == "Public") {
/*     */                     try
/*     */                     {
/*  97 */                       InetAddress addr = InetAddress.getByName(ip);
/*     */                       
/*  99 */                       req.getListener().IPResolutionComplete(addr.getHostName(), true);
/*     */                     }
/*     */                     catch (Throwable e)
/*     */                     {
/* 103 */                       req.getListener().IPResolutionComplete(ip, false);
/*     */                     }
/*     */                     
/*     */                   }
/*     */                   else {
/* 108 */                     req.getListener().IPResolutionComplete(ip, true);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             } catch (Throwable e) {
/* 113 */               Debug.printStackTrace(e);
/*     */             }
/*     */             
/*     */           }
/*     */           
/* 118 */         };
/* 119 */         resolver_thread.start();
/*     */       }
/*     */       
/* 122 */       return request;
/*     */     }
/*     */     finally
/*     */     {
/* 126 */       request_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String syncResolve(String ip, int timeout)
/*     */     throws Exception
/*     */   {
/* 137 */     final AESemaphore sem = new AESemaphore("IPToHostNameREsolver:sync");
/*     */     
/* 139 */     Object[] result = { null };
/*     */     
/* 141 */     addResolverRequest(ip, new IPToHostNameResolverListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void IPResolutionComplete(String resolved_ip, boolean succeeded)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 151 */           synchronized (this.val$result)
/*     */           {
/* 153 */             if (succeeded)
/*     */             {
/* 155 */               this.val$result[0] = resolved_ip;
/*     */             }
/*     */           }
/*     */         }
/*     */         finally {
/* 160 */           sem.release();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 165 */     if (!sem.reserve(timeout))
/*     */     {
/* 167 */       throw new Exception("Timeout");
/*     */     }
/*     */     
/* 170 */     synchronized (result)
/*     */     {
/* 172 */       if (result[0] != null)
/*     */       {
/* 174 */         return (String)result[0];
/*     */       }
/*     */       
/* 177 */       throw new UnknownHostException(ip);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/IPToHostNameResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */