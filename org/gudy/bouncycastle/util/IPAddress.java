/*     */ package org.gudy.bouncycastle.util;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public class IPAddress
/*     */ {
/*   7 */   private static final BigInteger ZERO = BigInteger.valueOf(0L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isValid(String address)
/*     */   {
/*  18 */     return (isValidIPv4(address)) || (isValidIPv6(address));
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
/*     */   private static boolean isValidIPv4(String address)
/*     */   {
/*  31 */     if (address.length() == 0)
/*     */     {
/*  33 */       return false;
/*     */     }
/*     */     
/*     */ 
/*  37 */     int octets = 0;
/*     */     
/*  39 */     String temp = address + ".";
/*     */     
/*     */ 
/*  42 */     int start = 0;
/*     */     int pos;
/*  44 */     while ((start < temp.length()) && ((pos = temp.indexOf('.', start)) > start))
/*     */     {
/*  46 */       if (octets == 4)
/*     */       {
/*  48 */         return false;
/*     */       }
/*     */       BigInteger octet;
/*     */       try {
/*  52 */         octet = new BigInteger(temp.substring(start, pos));
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/*  56 */         return false;
/*     */       }
/*  58 */       if ((octet.compareTo(ZERO) == -1) || (octet.compareTo(BigInteger.valueOf(255L)) == 1))
/*     */       {
/*     */ 
/*  61 */         return false;
/*     */       }
/*  63 */       start = pos + 1;
/*  64 */       octets++;
/*     */     }
/*     */     
/*  67 */     return octets == 4;
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
/*     */   private static boolean isValidIPv6(String address)
/*     */   {
/*  80 */     if (address.length() == 0)
/*     */     {
/*  82 */       return false;
/*     */     }
/*     */     
/*     */ 
/*  86 */     int octets = 0;
/*     */     
/*  88 */     String temp = address + ":";
/*     */     
/*     */ 
/*  91 */     int start = 0;
/*     */     int pos;
/*  93 */     while ((start < temp.length()) && ((pos = temp.indexOf(':', start)) > start))
/*     */     {
/*  95 */       if (octets == 8)
/*     */       {
/*  97 */         return false;
/*     */       }
/*     */       BigInteger octet;
/*     */       try {
/* 101 */         octet = new BigInteger(temp.substring(start, pos), 16);
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/* 105 */         return false;
/*     */       }
/* 107 */       if ((octet.compareTo(ZERO) == -1) || (octet.compareTo(BigInteger.valueOf(65535L)) == 1))
/*     */       {
/*     */ 
/* 110 */         return false;
/*     */       }
/* 112 */       start = pos + 1;
/* 113 */       octets++;
/*     */     }
/*     */     
/* 116 */     return octets == 8;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/IPAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */