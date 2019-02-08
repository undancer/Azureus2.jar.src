/*    */ package org.gudy.azureus2.plugins.utils;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.net.InetAddress;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class LocationProvider
/*    */ {
/*    */   public static final long CAP_COUNTY_BY_IP = 1L;
/*    */   public static final long CAP_ISO3166_BY_IP = 2L;
/*    */   public static final long CAP_FLAG_BY_IP = 4L;
/*    */   
/*    */   public abstract String getProviderName();
/*    */   
/*    */   public abstract long getCapabilities();
/*    */   
/*    */   public boolean hasCapability(long capability)
/*    */   {
/* 44 */     return (getCapabilities() & capability) != 0L;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean hasCapabilities(long capabilities)
/*    */   {
/* 51 */     return (getCapabilities() & capabilities) == capabilities;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getCountryNameForIP(InetAddress address, Locale locale)
/*    */   {
/* 59 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getISO3166CodeForIP(InetAddress address)
/*    */   {
/* 66 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int[][] getCountryFlagSizes()
/*    */   {
/* 77 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public InputStream getCountryFlagForIP(InetAddress address, int size_index)
/*    */   {
/* 92 */     return null;
/*    */   }
/*    */   
/*    */   public abstract boolean isDestroyed();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/LocationProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */