/*     */ package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.Coordinates;
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
/*     */ public class HeightCoordinatesImpl
/*     */   implements Coordinates
/*     */ {
/*     */   protected final float x;
/*     */   protected final float y;
/*     */   protected final float h;
/*     */   
/*     */   public HeightCoordinatesImpl(float x, float y, float h)
/*     */   {
/*  34 */     this.x = x;
/*  35 */     this.y = y;
/*  36 */     this.h = h;
/*     */   }
/*     */   
/*     */   public HeightCoordinatesImpl(HeightCoordinatesImpl copy) {
/*  40 */     this.x = copy.x;
/*  41 */     this.y = copy.y;
/*  42 */     this.h = copy.h;
/*     */   }
/*     */   
/*     */   public Coordinates add(Coordinates other) {
/*  46 */     HeightCoordinatesImpl o = (HeightCoordinatesImpl)other;
/*  47 */     return new HeightCoordinatesImpl(this.x + o.x, this.y + o.y, Math.abs(this.h + o.h));
/*     */   }
/*     */   
/*     */   public Coordinates sub(Coordinates other) {
/*  51 */     HeightCoordinatesImpl o = (HeightCoordinatesImpl)other;
/*  52 */     return new HeightCoordinatesImpl(this.x - o.x, this.y - o.y, Math.abs(this.h + o.h));
/*     */   }
/*     */   
/*     */   public Coordinates scale(float scale) {
/*  56 */     return new HeightCoordinatesImpl(scale * this.x, scale * this.y, scale * this.h);
/*     */   }
/*     */   
/*     */   public float measure() {
/*  60 */     return (float)(Math.sqrt(this.x * this.x + this.y * this.y) + this.h);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean atOrigin()
/*     */   {
/*  66 */     return (this.x == 0.0F) && (this.y == 0.0F);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/*  72 */     return (valid(this.x)) && (valid(this.y)) && (valid(this.h)) && (Math.abs(this.x) <= 30000.0F) && (Math.abs(this.y) <= 30000.0F) && (Math.abs(this.h) <= 30000.0F);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean valid(float f)
/*     */   {
/*  79 */     return (!Float.isInfinite(f)) && (!Float.isNaN(f));
/*     */   }
/*     */   
/*     */   public float distance(Coordinates other) {
/*  83 */     return sub(other).measure();
/*     */   }
/*     */   
/*     */   public Coordinates unity() {
/*  87 */     float measure = measure();
/*  88 */     if (measure == 0.0F)
/*     */     {
/*  90 */       float x = (float)Math.random();
/*  91 */       float y = (float)Math.random();
/*  92 */       float h = (float)Math.random();
/*  93 */       return new HeightCoordinatesImpl(x, y, h).unity();
/*     */     }
/*  95 */     return scale(1.0F / measure);
/*     */   }
/*     */   
/*     */   public double[] getCoordinates() {
/*  99 */     return new double[] { this.x, this.y };
/*     */   }
/*     */   
/*     */   public String toString() {
/* 103 */     return (int)this.x + "," + (int)this.y + "," + (int)this.h;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public float getH()
/*     */   {
/* 110 */     return this.h;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public float getX()
/*     */   {
/* 118 */     return this.x;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public float getY()
/*     */   {
/* 126 */     return this.y;
/*     */   }
/*     */   
/*     */   public boolean equals(Object arg0) {
/* 130 */     if ((arg0 instanceof HeightCoordinatesImpl)) {
/* 131 */       HeightCoordinatesImpl other = (HeightCoordinatesImpl)arg0;
/* 132 */       if ((other.x != this.x) || (other.y != this.y) || (other.h != this.h)) return false;
/* 133 */       return true;
/*     */     }
/* 135 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/impl/HeightCoordinatesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */