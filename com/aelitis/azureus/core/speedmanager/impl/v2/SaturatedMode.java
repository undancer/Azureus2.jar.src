/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
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
/*     */ public class SaturatedMode
/*     */   implements Comparable
/*     */ {
/*  23 */   public static final SaturatedMode AT_LIMIT = new SaturatedMode("AT_LIMIT", 0.95F);
/*  24 */   public static final SaturatedMode HIGH = new SaturatedMode("HIGH", 0.75F);
/*  25 */   public static final SaturatedMode MED = new SaturatedMode("MED", 0.25F);
/*  26 */   public static final SaturatedMode LOW = new SaturatedMode("LOW", 0.03F);
/*  27 */   public static final SaturatedMode NONE = new SaturatedMode("NONE", 0.0F);
/*     */   private final String name;
/*     */   private final float percentCapacity;
/*     */   
/*     */   private SaturatedMode(String _name, float _percent)
/*     */   {
/*  33 */     this.name = _name;
/*  34 */     this.percentCapacity = _percent;
/*     */   }
/*     */   
/*     */   private float getThreshold() {
/*  38 */     return this.percentCapacity;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SaturatedMode getSaturatedMode(int currentRate, int limit)
/*     */   {
/*  50 */     if (limit == 0)
/*     */     {
/*  52 */       limit = 61440;
/*     */     }
/*     */     
/*  55 */     float percent = currentRate / limit;
/*     */     
/*  57 */     if (percent > AT_LIMIT.getThreshold()) {
/*  58 */       return AT_LIMIT;
/*     */     }
/*  60 */     if (percent > HIGH.getThreshold()) {
/*  61 */       return HIGH;
/*     */     }
/*  63 */     if (percent > MED.getThreshold()) {
/*  64 */       return MED;
/*     */     }
/*  66 */     if (percent > LOW.getThreshold()) {
/*  67 */       return LOW;
/*     */     }
/*     */     
/*  70 */     return NONE;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  74 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreater(SaturatedMode mode)
/*     */   {
/*  84 */     if (compareTo(mode) > 0) {
/*  85 */       return true;
/*     */     }
/*  87 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(SaturatedMode satMode)
/*     */   {
/*  96 */     if (this.percentCapacity < satMode.getThreshold()) {
/*  97 */       return -1;
/*     */     }
/*  99 */     if (this.percentCapacity > satMode.getThreshold()) {
/* 100 */       return 1;
/*     */     }
/*     */     
/* 103 */     return 0;
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
/*     */   public int compareTo(Object obj)
/*     */   {
/* 116 */     if (!(obj instanceof SaturatedMode)) {
/* 117 */       throw new ClassCastException("Only comparable to SaturatedMode class.");
/*     */     }
/* 119 */     SaturatedMode casted = (SaturatedMode)obj;
/* 120 */     return compareTo(casted);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SaturatedMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */