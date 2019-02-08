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
/*     */ 
/*     */ 
/*     */ public class HostNameToIPResolver
/*     */ {
/*     */   protected static AEThread2 resolver_thread;
/*  37 */   protected static final List request_queue = new ArrayList();
/*     */   
/*  39 */   protected static final AEMonitor request_queue_mon = new AEMonitor("HostNameToIPResolver");
/*     */   
/*  41 */   protected static final AESemaphore request_semaphore = new AESemaphore("HostNameToIPResolver");
/*     */   
/*     */   static final int INADDRSZ = 4;
/*     */   
/*     */   public static boolean isDNSName(String host)
/*     */   {
/*  47 */     return AENetworkClassifier.categoriseAddress(host) == "Public";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isNonDNSName(String host)
/*     */   {
/*  54 */     return AENetworkClassifier.categoriseAddress(host) != "Public";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetAddress syncResolve(String host)
/*     */     throws UnknownHostException
/*     */   {
/*  63 */     if (isNonDNSName(host))
/*     */     {
/*  65 */       throw new HostNameToIPResolverException("non-DNS name '" + host + "'", true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  70 */     byte[] bytes = textToNumericFormat(host);
/*     */     
/*  72 */     if (bytes != null)
/*     */     {
/*  74 */       return InetAddress.getByAddress(bytes);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  80 */     char[] chars = host.toCharArray();
/*     */     
/*  82 */     boolean resolve = false;
/*     */     
/*  84 */     for (int i = 0; i < chars.length; i++)
/*     */     {
/*  86 */       if ((chars[i] != '.') && (!Character.isDigit(chars[i])))
/*     */       {
/*  88 */         resolve = true;
/*     */         
/*  90 */         break;
/*     */       }
/*     */     }
/*     */     
/*  94 */     if ((resolve) && (host.startsWith("websocket.")))
/*     */     {
/*  96 */       resolve = false;
/*     */       
/*  98 */       for (int i = 10; i < chars.length; i++)
/*     */       {
/* 100 */         if (!Character.isDigit(chars[i]))
/*     */         {
/* 102 */           resolve = true;
/*     */           
/* 104 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 109 */     if (resolve)
/*     */     {
/* 111 */       return InetAddress.getByName(host);
/*     */     }
/*     */     
/*     */ 
/* 115 */     throw new UnknownHostException("Host '" + host + "' doesn't obey minimal validation rules");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addResolverRequest(String host, HostNameToIPResolverListener l)
/*     */   {
/* 124 */     byte[] bytes = textToNumericFormat(host);
/*     */     
/* 126 */     if (bytes != null) {
/*     */       try
/*     */       {
/* 129 */         l.hostNameResolutionComplete(InetAddress.getByAddress(host, bytes));
/*     */         
/* 131 */         return;
/*     */       }
/*     */       catch (UnknownHostException e) {}
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 138 */       request_queue_mon.enter();
/*     */       
/* 140 */       request_queue.add(new request(host, l));
/*     */       
/* 142 */       request_semaphore.release();
/*     */       
/* 144 */       if (resolver_thread == null)
/*     */       {
/* 146 */         resolver_thread = new AEThread2("HostNameToIPResolver", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/*     */               for (;;)
/*     */               {
/* 155 */                 HostNameToIPResolver.request_semaphore.reserve(30000L);
/*     */                 
/*     */                 HostNameToIPResolver.request req;
/*     */                 try
/*     */                 {
/* 160 */                   HostNameToIPResolver.request_queue_mon.enter();
/*     */                   
/* 162 */                   if (HostNameToIPResolver.request_queue.isEmpty())
/*     */                   {
/* 164 */                     HostNameToIPResolver.resolver_thread = null;
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 173 */                     HostNameToIPResolver.request_queue_mon.exit(); break;
/*     */                   }
/* 169 */                   req = (HostNameToIPResolver.request)HostNameToIPResolver.request_queue.remove(0);
/*     */                 }
/*     */                 finally
/*     */                 {
/* 173 */                   HostNameToIPResolver.request_queue_mon.exit();
/*     */                 }
/*     */                 try
/*     */                 {
/* 177 */                   InetAddress addr = HostNameToIPResolver.syncResolve(req.getHost());
/*     */                   
/* 179 */                   req.getListener().hostNameResolutionComplete(addr);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 183 */                   req.getListener().hostNameResolutionComplete(null);
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 188 */               Debug.printStackTrace(e);
/*     */             }
/*     */             
/*     */           }
/*     */           
/* 193 */         };
/* 194 */         resolver_thread.start();
/*     */       }
/*     */     }
/*     */     finally {
/* 198 */       request_queue_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static InetAddress hostAddressToInetAddress(String host)
/*     */   {
/* 206 */     byte[] bytes = hostAddressToBytes(host);
/*     */     
/* 208 */     if (bytes != null) {
/*     */       try
/*     */       {
/* 211 */         return InetAddress.getByAddress(bytes);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 215 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 219 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static byte[] hostAddressToBytes(String host)
/*     */   {
/* 226 */     byte[] res = textToNumericFormat(host);
/*     */     
/* 228 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] textToNumericFormat(String src)
/*     */   {
/* 237 */     if (src.length() == 0) {
/* 238 */       return null;
/*     */     }
/*     */     
/* 241 */     if (src.indexOf(':') != -1)
/*     */     {
/*     */       try
/*     */       {
/* 245 */         return InetAddress.getByName(src).getAddress();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 249 */         return null;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 255 */     byte[] dst = new byte[4];
/*     */     
/* 257 */     char[] srcb = src.toCharArray();
/* 258 */     boolean saw_digit = false;
/*     */     
/* 260 */     int octets = 0;
/* 261 */     int i = 0;
/* 262 */     int cur = 0;
/* 263 */     while (i < srcb.length) {
/* 264 */       char ch = srcb[(i++)];
/* 265 */       if (Character.isDigit(ch))
/*     */       {
/* 267 */         int sum = (dst[cur] & 0xFF) * 10 + (Character.digit(ch, 10) & 0xFF);
/*     */         
/*     */ 
/* 270 */         if (sum > 255) {
/* 271 */           return null;
/*     */         }
/* 273 */         dst[cur] = ((byte)(sum & 0xFF));
/* 274 */         if (!saw_digit) {
/* 275 */           octets++; if (octets > 4)
/* 276 */             return null;
/* 277 */           saw_digit = true;
/*     */         }
/* 279 */       } else if ((ch == '.') && (saw_digit)) {
/* 280 */         if (octets == 4)
/* 281 */           return null;
/* 282 */         cur++;
/* 283 */         dst[cur] = 0;
/* 284 */         saw_digit = false;
/*     */       } else {
/* 286 */         return null;
/*     */       } }
/* 288 */     if (octets < 4)
/* 289 */       return null;
/* 290 */     return dst;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class request
/*     */   {
/*     */     protected final String host;
/*     */     
/*     */ 
/*     */     protected final HostNameToIPResolverListener listener;
/*     */     
/*     */ 
/*     */ 
/*     */     protected request(String _host, HostNameToIPResolverListener _listener)
/*     */     {
/* 306 */       this.host = _host;
/* 307 */       this.listener = _listener;
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getHost()
/*     */     {
/* 313 */       return this.host;
/*     */     }
/*     */     
/*     */ 
/*     */     protected HostNameToIPResolverListener getListener()
/*     */     {
/* 319 */       return this.listener;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/HostNameToIPResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */