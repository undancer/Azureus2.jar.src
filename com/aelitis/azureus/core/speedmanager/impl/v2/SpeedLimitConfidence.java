/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ public class SpeedLimitConfidence
/*     */   implements Comparable
/*     */ {
/*  27 */   public static final SpeedLimitConfidence NONE = new SpeedLimitConfidence("NONE", 0, -0.1F);
/*  28 */   public static final SpeedLimitConfidence LOW = new SpeedLimitConfidence("LOW", 1, 0.0F);
/*  29 */   public static final SpeedLimitConfidence MED = new SpeedLimitConfidence("MED", 2, 0.5F);
/*  30 */   public static final SpeedLimitConfidence HIGH = new SpeedLimitConfidence("HIGH", 3, 0.8F);
/*  31 */   public static final SpeedLimitConfidence ABSOLUTE = new SpeedLimitConfidence("ABSOLUTE", 4, 1.0F);
/*     */   private final String name;
/*     */   private final int order;
/*     */   private final float estimateType;
/*     */   private static final String MESSAGE_BUNDLE_PREFIX = "SpeedTestWizard.name.conf.level.";
/*     */   
/*     */   private SpeedLimitConfidence(String _name, int _order, float _speedLimitEstimateType) {
/*  38 */     this.name = _name;
/*  39 */     this.order = _order;
/*  40 */     this.estimateType = _speedLimitEstimateType;
/*     */   }
/*     */   
/*     */   public static SpeedLimitConfidence convertType(float type) {
/*  44 */     if (type <= NONE.estimateType)
/*  45 */       return NONE;
/*  46 */     if (type <= LOW.estimateType)
/*  47 */       return LOW;
/*  48 */     if (type <= MED.estimateType)
/*  49 */       return MED;
/*  50 */     if (type <= HIGH.estimateType) {
/*  51 */       return HIGH;
/*     */     }
/*  53 */     return ABSOLUTE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SpeedLimitConfidence parseString(String setting)
/*     */   {
/*  64 */     SpeedLimitConfidence retVal = NONE;
/*     */     
/*  66 */     if (setting == null) {
/*  67 */       return retVal;
/*     */     }
/*     */     
/*  70 */     if ("NONE".equalsIgnoreCase(setting))
/*  71 */       return NONE;
/*  72 */     if ("LOW".equalsIgnoreCase(setting))
/*  73 */       return LOW;
/*  74 */     if ("MED".equalsIgnoreCase(setting))
/*  75 */       return MED;
/*  76 */     if ("HIGH".equalsIgnoreCase(setting))
/*  77 */       return HIGH;
/*  78 */     if ("ABSOLUTE".equalsIgnoreCase(setting)) {
/*  79 */       return ABSOLUTE;
/*     */     }
/*     */     
/*  82 */     return retVal;
/*     */   }
/*     */   
/*     */   public float asEstimateType() {
/*  86 */     return this.estimateType;
/*     */   }
/*     */   
/*     */   public static String asEstimateTypeString(float type)
/*     */   {
/*  91 */     if (type == -0.1F)
/*  92 */       return "Unknown";
/*  93 */     if (type == 0.0F)
/*  94 */       return "Estimate";
/*  95 */     if (type == 1.0F) {
/*  96 */       return "Fixed";
/*     */     }
/*  98 */     return "";
/*     */   }
/*     */   
/*     */   public String getString() {
/* 102 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getInternationalizedString()
/*     */   {
/* 114 */     return MessageText.getString("SpeedTestWizard.name.conf.level." + this.name.toLowerCase());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreater(SpeedLimitConfidence limitConf)
/*     */   {
/* 125 */     if (compareTo(limitConf) > 0) {
/* 126 */       return true;
/*     */     }
/* 128 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(SpeedLimitConfidence limitConf)
/*     */   {
/* 137 */     return this.order - limitConf.order;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(Object obj)
/*     */   {
/* 146 */     if (!(obj instanceof SpeedLimitConfidence)) {
/* 147 */       throw new ClassCastException("Only comparable to SpeedLimitConfidence class.");
/*     */     }
/* 149 */     SpeedLimitConfidence casted = (SpeedLimitConfidence)obj;
/* 150 */     return compareTo(casted);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SpeedLimitConfidence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */