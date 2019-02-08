/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureDetails;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
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
/*     */ public class FeatureUtils
/*     */ {
/*     */   private static FeatureManager featman;
/*     */   
/*     */   static
/*     */   {
/*  46 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void azureusCoreRunning(AzureusCore core)
/*     */       {
/*     */ 
/*  53 */         PluginInterface pi = core.getPluginManager().getDefaultPluginInterface();
/*  54 */         FeatureUtils.access$002(pi.getUtilities().getFeatureManager());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static String getPlusMode() {
/*  60 */     boolean isFull = hasPlusLicence();
/*  61 */     boolean isTrial = (hasFullBurn()) && (!isFull);
/*  62 */     return isTrial ? "trial" : isFull ? "plus" : "free";
/*     */   }
/*     */   
/*     */   public static String getNoAdsMode() {
/*  66 */     boolean isNoAds = hasNoAdLicence();
/*  67 */     return isNoAds ? "no_ads" : "free";
/*     */   }
/*     */   
/*     */   public static boolean hasPlusLicence() {
/*  71 */     if (featman == null)
/*     */     {
/*  73 */       Set<String> featuresInstalled = UtilitiesImpl.getFeaturesInstalled();
/*  74 */       return featuresInstalled.contains("dvdburn");
/*     */     }
/*  76 */     licenceDetails fullFeatureDetails = getPlusFeatureDetails();
/*  77 */     long now = SystemTime.getCurrentTime();
/*  78 */     return (fullFeatureDetails != null) && (fullFeatureDetails.expiry > now) && (fullFeatureDetails.displayedExpiry > now);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static boolean hasFullLicence()
/*     */   {
/*  89 */     return hasPlusLicence();
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean hasTrialLicence()
/*     */   {
/*  95 */     boolean isFull = hasPlusLicence();
/*  96 */     boolean isTrial = (hasFullBurn()) && (!isFull);
/*     */     
/*  98 */     return isTrial;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean hasNoAdLicence()
/*     */   {
/* 104 */     if (featman == null)
/*     */     {
/* 106 */       Set<String> featuresInstalled = UtilitiesImpl.getFeaturesInstalled();
/*     */       
/* 108 */       return featuresInstalled.contains("no_ads");
/*     */     }
/*     */     
/* 111 */     licenceDetails details = getNoAdFeatureDetails();
/* 112 */     long now = SystemTime.getCurrentTime();
/* 113 */     return (details != null) && (details.expiry > now) && (details.displayedExpiry > now);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static licenceDetails getPlusFeatureDetails()
/*     */   {
/* 120 */     return getFeatureDetails("dvdburn");
/*     */   }
/*     */   
/* 123 */   private static licenceDetails getNoAdFeatureDetails() { return getFeatureDetails("no_ads"); }
/*     */   
/*     */ 
/*     */   public static licenceDetails getPlusOrNoAdFeatureDetails()
/*     */   {
/* 128 */     licenceDetails plusDetails = getPlusFeatureDetails();
/*     */     
/* 130 */     long now = System.currentTimeMillis();
/*     */     
/* 132 */     if ((plusDetails != null) && (plusDetails.licence.getState() == 2) && (plusDetails.expiry >= now))
/*     */     {
/*     */ 
/*     */ 
/* 136 */       return plusDetails;
/*     */     }
/*     */     
/* 139 */     licenceDetails noAdDetails = getNoAdFeatureDetails();
/*     */     
/* 141 */     if (noAdDetails == null)
/*     */     {
/* 143 */       return plusDetails;
/*     */     }
/* 145 */     if (plusDetails == null)
/*     */     {
/* 147 */       return noAdDetails;
/*     */     }
/*     */     
/* 150 */     if ((noAdDetails.licence.getState() == 2) && (noAdDetails.expiry >= now))
/*     */     {
/*     */ 
/* 153 */       return noAdDetails;
/*     */     }
/*     */     
/* 156 */     return plusDetails;
/*     */   }
/*     */   
/*     */   private static licenceDetails getFeatureDetails(String feature) {
/* 160 */     if (featman == null) {
/* 161 */       Debug.out("featman null");
/* 162 */       return null;
/*     */     }
/*     */     
/* 165 */     TreeMap<Long, Object[]> mapOrder = new TreeMap(Collections.reverseOrder());
/*     */     
/* 167 */     FeatureManager.FeatureDetails[] featureDetails = featman.getFeatureDetails(feature);
/*     */     
/* 169 */     for (FeatureManager.FeatureDetails fd : featureDetails) {
/* 170 */       FeatureManager.Licence licence = fd.getLicence();
/* 171 */       int state = licence.getState();
/* 172 */       if (state == 6) {
/* 173 */         mapOrder.put(Long.valueOf(-1L), new Object[] { licence, Long.valueOf(0L) });
/*     */       }
/* 175 */       else if (state == 4) {
/* 176 */         mapOrder.put(Long.valueOf(-2L), new Object[] { licence, Long.valueOf(0L) });
/*     */       }
/* 178 */       else if (state == 3) {
/* 179 */         mapOrder.put(Long.valueOf(-3L), new Object[] { licence, Long.valueOf(0L) });
/*     */       }
/* 181 */       else if (state == 5) {
/* 182 */         mapOrder.put(Long.valueOf(-4L), new Object[] { licence, Long.valueOf(0L) });
/*     */       }
/* 184 */       else if (state == 1) {
/* 185 */         mapOrder.put(Long.valueOf(-6L), new Object[] { licence, Long.valueOf(0L) });
/*     */       }
/*     */       else
/*     */       {
/* 189 */         long now = SystemTime.getCurrentTime();
/* 190 */         Long lValidUntil = (Long)fd.getProperty("ValidUntil");
/* 191 */         Long lValidOfflineUntil = (Long)fd.getProperty("OfflineValidUntil");
/*     */         
/* 193 */         if ((lValidUntil != null) || (lValidOfflineUntil != null))
/*     */         {
/*     */ 
/*     */ 
/* 197 */           long minValidUntil = -1L;
/* 198 */           long maxValidUntil = -1L;
/* 199 */           if (lValidUntil != null) {
/* 200 */             minValidUntil = maxValidUntil = lValidUntil.longValue();
/* 201 */             if (minValidUntil < now) {
/* 202 */               mapOrder.put(Long.valueOf(minValidUntil), new Object[] { licence, Long.valueOf(minValidUntil) });
/* 203 */               continue;
/*     */             }
/*     */           }
/* 206 */           if (lValidOfflineUntil != null) {
/* 207 */             long validOfflineUntil = lValidOfflineUntil.longValue();
/* 208 */             if (validOfflineUntil < now) {
/* 209 */               mapOrder.put(Long.valueOf(validOfflineUntil), new Object[] { licence, Long.valueOf(maxValidUntil) });
/* 210 */               continue;
/*     */             }
/* 212 */             if ((maxValidUntil == -1L) || (validOfflineUntil > maxValidUntil)) {
/* 213 */               maxValidUntil = validOfflineUntil;
/*     */             }
/*     */           }
/*     */           
/* 217 */           mapOrder.put(Long.valueOf(maxValidUntil), new Object[] { licence, Long.valueOf(minValidUntil) });
/*     */         }
/*     */       } }
/* 220 */     if (mapOrder.size() == 0) {
/* 221 */       return null;
/*     */     }
/*     */     
/* 224 */     Long firstKey = (Long)mapOrder.firstKey();
/* 225 */     Object[] objects = (Object[])mapOrder.get(firstKey);
/* 226 */     FeatureManager.Licence licence = (FeatureManager.Licence)objects[0];
/* 227 */     return new licenceDetails(firstKey.longValue(), ((Long)objects[1]).longValue(), licence, feature);
/*     */   }
/*     */   
/*     */   public static boolean isTrialLicence(FeatureManager.Licence licence) {
/* 231 */     if (featman == null) {
/* 232 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 237 */     boolean trial = false;
/* 238 */     FeatureManager.FeatureDetails[] featureDetails = licence.getFeatures();
/* 239 */     for (FeatureManager.FeatureDetails fd : featureDetails) {
/* 240 */       trial = isTrial(fd);
/* 241 */       if (trial) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/* 246 */     return trial;
/*     */   }
/*     */   
/*     */   public static boolean isTrial(FeatureManager.FeatureDetails fd) {
/* 250 */     Long lIsTrial = (Long)fd.getProperty("IsTrial");
/* 251 */     return lIsTrial != null;
/*     */   }
/*     */   
/*     */   public static long getRemaining() {
/* 255 */     FeatureManager.FeatureDetails[] featureDetails = featman.getFeatureDetails("dvdburn_trial");
/* 256 */     if (featureDetails == null) {
/* 257 */       return 0L;
/*     */     }
/* 259 */     for (FeatureManager.FeatureDetails fd : featureDetails) {
/* 260 */       long remainingUses = getRemainingUses(fd);
/* 261 */       if (remainingUses >= 0L) {
/* 262 */         return remainingUses;
/*     */       }
/*     */     }
/* 265 */     return 0L;
/*     */   }
/*     */   
/*     */   private static long getRemainingUses(FeatureManager.FeatureDetails fd) {
/* 269 */     if (fd == null) {
/* 270 */       return 0L;
/*     */     }
/* 272 */     Long lRemainingUses = (Long)fd.getProperty("TrialUsesRemaining");
/* 273 */     long remainingUses = lRemainingUses == null ? -1L : lRemainingUses.longValue();
/*     */     
/* 275 */     return remainingUses;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean hasFullBurn()
/*     */   {
/* 283 */     PluginInterface pi = PluginInitializer.getDefaultInterface().getPluginState().isInitialisationComplete() ? AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azburn_v") : null;
/*     */     
/*     */ 
/* 286 */     if (pi == null)
/*     */     {
/* 288 */       Set<String> featuresInstalled = UtilitiesImpl.getFeaturesInstalled();
/* 289 */       return (featuresInstalled.contains("dvdburn_trial")) && (!featuresInstalled.contains("dvdburn"));
/*     */     }
/* 291 */     return pi.getPluginState().isOperational();
/*     */   }
/*     */   
/*     */   public static class licenceDetails
/*     */   {
/*     */     private final FeatureManager.Licence licence;
/*     */     private final long expiry;
/*     */     private final long displayedExpiry;
/*     */     private final String feature;
/*     */     
/*     */     public licenceDetails(long expiry, long displayedExpiry, FeatureManager.Licence licence, String feature) {
/* 302 */       this.expiry = expiry;
/* 303 */       this.displayedExpiry = displayedExpiry;
/* 304 */       this.licence = licence;
/* 305 */       this.feature = feature;
/*     */     }
/*     */     
/*     */ 
/*     */     public FeatureManager.Licence getLicence()
/*     */     {
/* 311 */       return this.licence;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getRenewalKey()
/*     */     {
/* 317 */       FeatureManager.FeatureDetails[] features = this.licence.getFeatures();
/* 318 */       if (features == null) {
/* 319 */         return null;
/*     */       }
/* 321 */       for (FeatureManager.FeatureDetails fd : features) {
/* 322 */         Object property = fd.getProperty("RenewalKey");
/* 323 */         if ((property instanceof String)) {
/* 324 */           return (String)property;
/*     */         }
/*     */       }
/* 327 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isPlus()
/*     */     {
/* 333 */       return this.feature.equals("dvdburn");
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isNoAds()
/*     */     {
/* 339 */       return !isPlus();
/*     */     }
/*     */     
/*     */     public long getExpiryTimeStamp()
/*     */     {
/* 344 */       return this.expiry;
/*     */     }
/*     */     
/*     */     public long getExpiryDisplayTimeStamp() {
/* 348 */       if (this.expiry == 0L) {
/* 349 */         return 0L;
/*     */       }
/* 351 */       return this.displayedExpiry;
/*     */     }
/*     */     
/*     */     public String getRenewalCode()
/*     */     {
/* 356 */       if (this.expiry == 0L) {
/* 357 */         return null;
/*     */       }
/*     */       
/* 360 */       return getRenewalKey();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/FeatureUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */