/*     */ package com.aelitis.azureus.core.peermanager.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*     */ import java.net.InetAddress;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.IPToHostNameResolver;
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
/*     */ public class PeerClassifier
/*     */ {
/*     */   public static final String CACHE_LOGIC = "CacheLogic";
/*     */   
/*     */   public static String getClientDescription(byte[] peer_id)
/*     */   {
/*  46 */     return BTPeerIDByteDecoder.decode(peer_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getPrintablePeerID(byte[] peer_id)
/*     */   {
/*  57 */     return BTPeerIDByteDecoder.getPrintablePeerID(peer_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isClientTypeAllowed(String client_description)
/*     */   {
/*  68 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean fullySupportsFE(String client_description)
/*     */   {
/*  73 */     if (FeatureAvailability.allowAllFEClients())
/*     */     {
/*  75 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  80 */     boolean res = (!client_description.startsWith("µ")) && (!client_description.startsWith("Trans"));
/*     */     
/*  82 */     return res;
/*     */   }
/*     */   
/*  85 */   private static final Set platform_ips = Collections.synchronizedSet(new HashSet());
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
/*     */   public static boolean isAzureusIP(String ip)
/*     */   {
/*  98 */     return platform_ips.contains(ip);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setAzureusIP(String ip)
/*     */   {
/* 105 */     platform_ips.add(ip);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean testIfAzureusIP(String ip)
/*     */   {
/*     */     try
/*     */     {
/* 119 */       InetAddress address = HostNameToIPResolver.syncResolve(ip);
/*     */       
/* 121 */       String host_address = address.getHostAddress();
/*     */       
/* 123 */       if (platform_ips.contains(host_address))
/*     */       {
/* 125 */         return true;
/*     */       }
/*     */       
/* 128 */       String name = IPToHostNameResolver.syncResolve(ip, 10000);
/*     */       
/* 130 */       if (Constants.isAzureusDomain(name))
/*     */       {
/* 132 */         platform_ips.add(host_address);
/*     */         
/* 134 */         return true;
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 139 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/PeerClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */