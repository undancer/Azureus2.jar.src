/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class DNSUtils
/*     */ {
/*     */   private static DNSUtilsIntf impl;
/*     */   
/*     */   static
/*     */   {
/*  39 */     String cla = System.getProperty("az.factory.dnsutils.impl", "com.aelitis.azureus.core.util.dns.DNSUtilsImpl");
/*     */     try
/*     */     {
/*  42 */       impl = (DNSUtilsIntf)Class.forName(cla).newInstance();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  46 */       Debug.out("Failed to instantiate impl: " + cla, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static DNSUtilsIntf getSingleton()
/*     */   {
/*  53 */     return impl;
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
/*     */   public static String getInterestingHostSuffix(String host)
/*     */   {
/* 111 */     if (host == null)
/*     */     {
/* 113 */       return null;
/*     */     }
/*     */     
/* 116 */     String[] bits = host.split("\\.");
/*     */     
/* 118 */     int num_bits = bits.length;
/*     */     
/* 120 */     if (bits[(num_bits - 1)].equals("dht"))
/*     */     {
/* 122 */       return null;
/*     */     }
/*     */     
/* 125 */     if (bits.length > 2)
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
/* 136 */       int hit = -1;
/*     */       
/* 138 */       for (int i = num_bits - 1; i >= 0; i--)
/*     */       {
/* 140 */         String bit = bits[i];
/*     */         
/* 142 */         if (bit.length() > 3)
/*     */         {
/* 144 */           hit = i;
/*     */           
/* 146 */           break;
/*     */         }
/*     */       }
/*     */       
/* 150 */       if (hit > 0)
/*     */       {
/* 152 */         host = "";
/*     */         
/* 154 */         for (int i = hit; i < num_bits; i++)
/*     */         {
/* 156 */           host = host + (host == "" ? "" : ".") + bits[i];
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 161 */     return host;
/*     */   }
/*     */   
/*     */   public static abstract interface DNSDirContext
/*     */   {
/*     */     public abstract String getString();
/*     */   }
/*     */   
/*     */   public static abstract interface DNSUtilsIntf
/*     */   {
/*     */     public abstract DNSUtils.DNSDirContext getInitialDirContext()
/*     */       throws Exception;
/*     */     
/*     */     public abstract DNSUtils.DNSDirContext getDirContextForServer(String paramString)
/*     */       throws Exception;
/*     */     
/*     */     public abstract Inet6Address getIPV6ByName(String paramString)
/*     */       throws UnknownHostException;
/*     */     
/*     */     public abstract List<InetAddress> getAllByName(String paramString)
/*     */       throws UnknownHostException;
/*     */     
/*     */     public abstract List<InetAddress> getAllByName(DNSUtils.DNSDirContext paramDNSDirContext, String paramString)
/*     */       throws UnknownHostException;
/*     */     
/*     */     public abstract List<String> getTXTRecords(String paramString);
/*     */     
/*     */     public abstract String getTXTRecord(String paramString)
/*     */       throws UnknownHostException;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/DNSUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */