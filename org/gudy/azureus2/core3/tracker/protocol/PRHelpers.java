/*     */ package org.gudy.azureus2.core3.tracker.protocol;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
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
/*     */ public class PRHelpers
/*     */ {
/*     */   public static int addressToInt(String address)
/*     */     throws UnknownHostException
/*     */   {
/*  43 */     InetAddress i_address = HostNameToIPResolver.syncResolve(address);
/*     */     
/*  45 */     byte[] bytes = i_address.getAddress();
/*     */     
/*  47 */     int resp = bytes[0] << 24 & 0xFF000000 | bytes[1] << 16 & 0xFF0000 | bytes[2] << 8 & 0xFF00 | bytes[3] & 0xFF;
/*     */     
/*     */ 
/*     */ 
/*  51 */     return resp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int addressToInt(InetAddress i_address)
/*     */   {
/*  58 */     byte[] bytes = i_address.getAddress();
/*     */     
/*  60 */     int resp = bytes[0] << 24 & 0xFF000000 | bytes[1] << 16 & 0xFF0000 | bytes[2] << 8 & 0xFF00 | bytes[3] & 0xFF;
/*     */     
/*     */ 
/*     */ 
/*  64 */     return resp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static long addressToLong(InetAddress i_address)
/*     */   {
/*  71 */     return addressToInt(i_address) & 0xFFFFFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String intToAddress(int value)
/*     */   {
/*  78 */     byte[] bytes = { (byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value };
/*     */     try
/*     */     {
/*  81 */       return InetAddress.getByAddress(bytes).getHostAddress();
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (UnknownHostException e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  91 */       Debug.printStackTrace(e);
/*     */     }
/*  93 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addressTo4ByteArray(String address, byte[] buffer, int offset)
/*     */     throws UnknownHostException
/*     */   {
/* 105 */     InetAddress i_address = HostNameToIPResolver.syncResolve(address);
/*     */     
/* 107 */     byte[] bytes = i_address.getAddress();
/*     */     
/* 109 */     System.arraycopy(bytes, 0, buffer, offset, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String DNSToIPAddress(String dns_name)
/*     */     throws UnknownHostException
/*     */   {
/* 118 */     return HostNameToIPResolver.syncResolve(dns_name).getHostAddress();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/PRHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */