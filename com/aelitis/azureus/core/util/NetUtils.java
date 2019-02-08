/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.runnableWithException;
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
/*     */ public class NetUtils
/*     */ {
/*     */   private static final int MIN_NI_CHECK_MILLIS = 30000;
/*     */   private static final int INC1_NI_CHECK_MILLIS = 120000;
/*     */   private static final int INC2_NI_CHECK_MILLIS = 900000;
/*  42 */   private static int current_check_millis = 30000;
/*     */   
/*  44 */   private static long last_ni_check = -1L;
/*     */   
/*  46 */   private static volatile List<NetworkInterface> current_interfaces = new ArrayList();
/*     */   
/*  48 */   private static boolean first_check = true;
/*     */   
/*     */   private static boolean check_in_progress;
/*  51 */   static final AESemaphore ni_sem = new AESemaphore("NetUtils:ni");
/*     */   
/*  53 */   private static final Map<Object, Object[]> host_or_address_map = new HashMap();
/*     */   
/*  55 */   private static final Object RESULT_NULL = new Object();
/*     */   
/*     */ 
/*     */ 
/*     */   public static List<NetworkInterface> getNetworkInterfaces()
/*     */     throws SocketException
/*     */   {
/*  62 */     long now = SystemTime.getMonotonousTime();
/*     */     
/*  64 */     boolean do_check = false;
/*  65 */     boolean is_first = false;
/*     */     
/*  67 */     synchronized (NetUtils.class)
/*     */     {
/*  69 */       if (!check_in_progress)
/*     */       {
/*  71 */         if ((last_ni_check < 0L) || (now - last_ni_check > current_check_millis))
/*     */         {
/*  73 */           do_check = true;
/*  74 */           check_in_progress = true;
/*     */           
/*  76 */           if (first_check)
/*     */           {
/*  78 */             first_check = false;
/*  79 */             is_first = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  85 */     if (do_check)
/*     */     {
/*  87 */       final UtilitiesImpl.runnableWithException<SocketException> do_it = new UtilitiesImpl.runnableWithException()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void run()
/*     */           throws SocketException
/*     */         {
/*     */ 
/*  95 */           List<NetworkInterface> result = new ArrayList();
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 100 */             long start = SystemTime.getHighPrecisionCounter();
/*     */             
/* 102 */             Enumeration<NetworkInterface> nis = NetUtils.access$000();
/*     */             
/* 104 */             long elapsed_millis = (SystemTime.getHighPrecisionCounter() - start) / 1000000L;
/*     */             
/* 106 */             long old_period = NetUtils.current_check_millis;
/*     */             
/* 108 */             if ((elapsed_millis > (Constants.isAndroid ? 'ᎈ' : 'Ϩ')) && (NetUtils.current_check_millis < 900000))
/*     */             {
/* 110 */               NetUtils.access$102(900000);
/*     */             }
/* 112 */             else if ((elapsed_millis > (Constants.isAndroid ? 'Ϩ' : 'ú')) && (NetUtils.current_check_millis < 120000))
/*     */             {
/* 114 */               NetUtils.access$102(120000);
/*     */             }
/*     */             
/* 117 */             if (old_period != NetUtils.current_check_millis)
/*     */             {
/* 119 */               Debug.out("Network interface enumeration took " + elapsed_millis + ": decreased refresh frequency to " + NetUtils.current_check_millis + "ms");
/*     */             }
/*     */             
/* 122 */             if (nis != null)
/*     */             {
/* 124 */               while (nis.hasMoreElements())
/*     */               {
/* 126 */                 result.add(nis.nextElement());
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           }
/*     */           finally
/*     */           {
/* 134 */             synchronized (NetUtils.class)
/*     */             {
/* 136 */               NetUtils.access$202(false);
/* 137 */               NetUtils.access$302(result);
/*     */               
/* 139 */               NetUtils.access$402(SystemTime.getMonotonousTime());
/*     */             }
/*     */             
/* 142 */             NetUtils.ni_sem.releaseForever();
/*     */           }
/*     */         }
/*     */       };
/*     */       
/* 147 */       if (is_first)
/*     */       {
/* 149 */         final AESemaphore do_it_sem = new AESemaphore("getNIs");
/*     */         
/* 151 */         final SocketException[] error = { null };
/*     */         
/* 153 */         new AEThread2("getNIAsync")
/*     */         {
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/* 159 */               do_it.run();
/*     */             }
/*     */             catch (SocketException e)
/*     */             {
/* 163 */               error[0] = e;
/*     */             }
/*     */             finally
/*     */             {
/* 167 */               do_it_sem.release();
/*     */             }
/*     */           }
/*     */         }.start();
/*     */         
/* 172 */         if (!do_it_sem.reserve(15000L))
/*     */         {
/* 174 */           Debug.out("Timeout obtaining network interfaces");
/*     */           
/* 176 */           ni_sem.releaseForever();
/*     */ 
/*     */ 
/*     */         }
/* 180 */         else if (error[0] != null)
/*     */         {
/* 182 */           throw error[0];
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 187 */         do_it.run();
/*     */       }
/*     */     }
/*     */     
/* 191 */     ni_sem.reserve();
/*     */     
/* 193 */     return current_interfaces;
/*     */   }
/*     */   
/*     */ 
/*     */   public static InetAddress getLocalHost()
/*     */     throws UnknownHostException
/*     */   {
/*     */     try
/*     */     {
/* 202 */       return InetAddress.getLocalHost();
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/* 210 */         List<NetworkInterface> nis = getNetworkInterfaces();
/*     */         
/* 212 */         for (NetworkInterface ni : nis)
/*     */         {
/* 214 */           Enumeration addresses = ni.getInetAddresses();
/*     */           
/* 216 */           while (addresses.hasMoreElements())
/*     */           {
/* 218 */             InetAddress address = (InetAddress)addresses.nextElement();
/*     */             
/* 220 */             if ((!address.isLoopbackAddress()) && (!(address instanceof Inet6Address)))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 225 */               return address;
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (Throwable f) {}
/*     */     }
/* 231 */     return InetAddress.getByName("127.0.0.1");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static NetworkInterface getByName(String name)
/*     */     throws SocketException
/*     */   {
/* 241 */     return getBySupport(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static NetworkInterface getByInetAddress(InetAddress addr)
/*     */     throws SocketException
/*     */   {
/* 250 */     return getBySupport(addr);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static NetworkInterface getBySupport(Object name_or_address)
/*     */     throws SocketException
/*     */   {
/*     */     Object[] entry;
/*     */     
/*     */ 
/* 261 */     synchronized (host_or_address_map)
/*     */     {
/* 263 */       entry = (Object[])host_or_address_map.get(name_or_address);
/*     */       
/* 265 */       if (entry != null)
/*     */       {
/* 267 */         synchronized (entry)
/*     */         {
/* 269 */           long now = SystemTime.getMonotonousTime();
/*     */           
/* 271 */           Object result_or_error = entry[0];
/*     */           
/* 273 */           if (result_or_error != null)
/*     */           {
/* 275 */             if (((Long)entry[1]).longValue() > now)
/*     */             {
/*     */ 
/*     */ 
/* 279 */               if (result_or_error == RESULT_NULL)
/*     */               {
/* 281 */                 return null;
/*     */               }
/* 283 */               if ((result_or_error instanceof NetworkInterface))
/*     */               {
/* 285 */                 return (NetworkInterface)result_or_error;
/*     */               }
/*     */               
/*     */ 
/* 289 */               throw ((SocketException)result_or_error);
/*     */             }
/*     */             
/*     */ 
/* 293 */             entry[0] = null;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 298 */         entry = new Object[2];
/*     */         
/* 300 */         host_or_address_map.put(name_or_address, entry);
/*     */       }
/*     */     }
/*     */     
/* 304 */     synchronized (entry)
/*     */     {
/*     */ 
/*     */ 
/* 308 */       Object result_or_error = entry[0];
/*     */       
/* 310 */       if (result_or_error != null)
/*     */       {
/* 312 */         if (result_or_error == RESULT_NULL)
/*     */         {
/* 314 */           return null;
/*     */         }
/* 316 */         if ((result_or_error instanceof NetworkInterface))
/*     */         {
/* 318 */           return (NetworkInterface)result_or_error;
/*     */         }
/*     */         
/*     */ 
/* 322 */         throw ((SocketException)result_or_error);
/*     */       }
/*     */       
/*     */ 
/* 326 */       long start = SystemTime.getHighPrecisionCounter();
/*     */       
/* 328 */       Object result = null;
/* 329 */       SocketException error = null;
/*     */       try
/*     */       {
/* 332 */         if ((name_or_address instanceof String))
/*     */         {
/* 334 */           result = NetworkInterface.getByName((String)name_or_address);
/*     */         }
/*     */         else
/*     */         {
/* 338 */           result = NetworkInterface.getByInetAddress((InetAddress)name_or_address);
/*     */         }
/*     */         
/*     */ 
/* 342 */         if (result == null)
/*     */         {
/* 344 */           result = RESULT_NULL;
/*     */         }
/*     */       }
/*     */       catch (SocketException e) {
/* 348 */         error = e;
/*     */       }
/*     */       
/* 351 */       long elapsed = (SystemTime.getHighPrecisionCounter() - start) / 1000000L;
/*     */       
/* 353 */       entry[0] = (result == null ? error : result);
/*     */       
/* 355 */       long delay = 250L * elapsed;
/*     */       
/* 357 */       if (delay > 300000L)
/*     */       {
/* 359 */         delay = 300000L;
/*     */       }
/*     */       
/* 362 */       entry[1] = Long.valueOf(SystemTime.getMonotonousTime() + delay);
/*     */       
/* 364 */       if (error != null)
/*     */       {
/* 366 */         throw error;
/*     */       }
/*     */       
/*     */ 
/* 370 */       if (result == RESULT_NULL)
/*     */       {
/* 372 */         return null;
/*     */       }
/*     */       
/*     */ 
/* 376 */       return (NetworkInterface)result;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Enumeration<NetworkInterface> NetworkInterface_getNetworkInterfaces()
/*     */     throws SocketException
/*     */   {
/*     */     try
/*     */     {
/* 391 */       return NetworkInterface.getNetworkInterfaces();
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
/*     */     }
/*     */     catch (SocketException e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 414 */       SocketException se = e;
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 419 */         Method mGetByIndex = NetworkInterface.class.getDeclaredMethod("getByIndex", new Class[] { Integer.TYPE });
/*     */         
/* 421 */         List<NetworkInterface> list = new ArrayList();
/* 422 */         int i = 0;
/*     */         for (;;)
/*     */         {
/* 425 */           NetworkInterface nif = null;
/*     */           try {
/* 427 */             nif = (NetworkInterface)mGetByIndex.invoke(null, new Object[] { Integer.valueOf(i) });
/*     */           }
/*     */           catch (IllegalAccessException e) {
/*     */             break;
/*     */           }
/*     */           catch (InvocationTargetException ignore) {}
/* 433 */           if (nif != null)
/* 434 */             list.add(nif); else {
/* 435 */             if (i > 0)
/*     */               break;
/*     */           }
/* 438 */           i++;
/*     */         }
/* 440 */         if (list.size() > 0) {
/* 441 */           return Collections.enumeration(list);
/*     */         }
/*     */       }
/*     */       catch (NoSuchMethodException ignore) {}
/*     */       
/*     */ 
/* 447 */       List<NetworkInterface> list = new ArrayList();
/* 448 */       String[] commonNames = { "lo", "eth", "lan", "wlan", "en", "p2p", "net", "ppp" };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 458 */       for (String commonName : commonNames) {
/*     */         try {
/* 460 */           NetworkInterface nif = NetworkInterface.getByName(commonName);
/* 461 */           if (nif != null) {
/* 462 */             list.add(nif);
/*     */           }
/*     */           
/*     */ 
/* 466 */           int i = 0;
/*     */           for (;;) {
/* 468 */             nif = NetworkInterface.getByName(commonName + i);
/* 469 */             if (nif == null) break;
/* 470 */             list.add(nif);
/*     */             
/*     */ 
/*     */ 
/* 474 */             i++;
/*     */           }
/*     */         }
/*     */         catch (Throwable ignore) {}
/*     */       }
/* 479 */       if (list.size() > 0) {
/* 480 */         return Collections.enumeration(list);
/*     */       }
/*     */       
/* 483 */       throw se;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/NetUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */