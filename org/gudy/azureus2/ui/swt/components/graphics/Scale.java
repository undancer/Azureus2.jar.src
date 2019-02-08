/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
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
/*     */ public class Scale
/*     */ {
/*     */   private static boolean wantBinary;
/*     */   private static boolean useSI;
/*     */   
/*     */   static
/*     */   {
/*  36 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "ui.scaled.graphics.binary.based", "config.style.useSIUnits", "config.style.forceSIValues" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*  45 */         Scale.access$002(COConfigurationManager.getBooleanParameter("ui.scaled.graphics.binary.based"));
/*     */         
/*  47 */         boolean wantSI = COConfigurationManager.getBooleanParameter("config.style.useSIUnits");
/*  48 */         boolean forceSI = COConfigurationManager.getBooleanParameter("config.style.forceSIValues");
/*     */         
/*  50 */         Scale.access$102((wantSI) || (forceSI));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*  55 */   private int pixelsPerLevel = 50;
/*     */   
/*     */ 
/*  58 */   private int max = 1;
/*     */   
/*     */ 
/*     */ 
/*     */   private int nbLevels;
/*     */   
/*     */ 
/*     */   private int displayedMax;
/*     */   
/*     */ 
/*  68 */   private int nbPixels = 1;
/*     */   
/*     */ 
/*     */   boolean isSIIECSensitive;
/*     */   
/*  73 */   private int[] scaleValues = new int[0];
/*     */   
/*     */ 
/*     */   public Scale()
/*     */   {
/*  78 */     this(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Scale(boolean _isSIIECSensitive)
/*     */   {
/*  85 */     this.isSIIECSensitive = _isSIIECSensitive;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSIIECSensitive()
/*     */   {
/*  91 */     return this.isSIIECSensitive;
/*     */   }
/*     */   
/*     */   public void setMax(int max) {
/*  95 */     this.max = max;
/*  96 */     if (max < 1)
/*  97 */       max = 1;
/*  98 */     computeValues();
/*     */   }
/*     */   
/*     */   public int getMax() {
/* 102 */     return this.max;
/*     */   }
/*     */   
/*     */   public void setNbPixels(int nbPixels) {
/* 106 */     this.nbPixels = nbPixels;
/* 107 */     if (nbPixels < 1)
/* 108 */       nbPixels = 1;
/* 109 */     computeValues();
/*     */   }
/*     */   
/*     */   private void computeValues() {
/* 113 */     int targetNbLevels = this.nbPixels / this.pixelsPerLevel;
/* 114 */     if (targetNbLevels < 1)
/* 115 */       targetNbLevels = 1;
/* 116 */     double scaleFactor = this.max / targetNbLevels;
/* 117 */     long powFactor = 1L;
/*     */     
/*     */ 
/* 120 */     int scaleThing = wantBinary ? 2 : 10;
/* 121 */     double scaleMax = wantBinary ? 4.0D : 5.0D;
/*     */     
/*     */ 
/* 124 */     while (scaleFactor >= scaleThing) {
/* 125 */       powFactor = scaleThing * powFactor;
/* 126 */       scaleFactor /= scaleThing;
/*     */     }
/*     */     
/*     */ 
/* 130 */     if (scaleFactor >= scaleMax) {
/* 131 */       scaleFactor = scaleMax;
/* 132 */     } else if (scaleFactor >= 2.0D) {
/* 133 */       scaleFactor = scaleMax / 2.0D;
/*     */     } else {
/* 135 */       scaleFactor = 1.0D;
/*     */     }
/*     */     
/* 138 */     long increment = (scaleFactor * powFactor);
/*     */     
/* 140 */     if (this.isSIIECSensitive)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 147 */       int divBy = 0;
/* 148 */       int multBy = 0;
/*     */       
/* 150 */       if ((useSI) && (!wantBinary))
/*     */       {
/* 152 */         divBy = 1000;
/* 153 */         multBy = 1024;
/*     */       }
/* 155 */       else if ((!useSI) && (wantBinary))
/*     */       {
/* 157 */         divBy = 1024;
/* 158 */         multBy = 1000;
/*     */       }
/*     */       
/* 161 */       if (divBy > 0)
/*     */       {
/* 163 */         long temp = increment;
/* 164 */         int pow = -1;
/*     */         
/* 166 */         while (temp > 0L)
/*     */         {
/* 168 */           temp /= divBy;
/* 169 */           pow++;
/*     */         }
/*     */         
/* 172 */         long temp2 = 1L;
/* 173 */         long temp3 = 1L;
/*     */         
/* 175 */         for (int i = 0; i < pow; i++)
/*     */         {
/* 177 */           temp2 *= multBy;
/* 178 */           temp3 *= divBy;
/*     */         }
/*     */         
/* 181 */         increment = (increment / temp3 * temp2);
/*     */       }
/*     */     }
/*     */     
/* 185 */     this.nbLevels = ((int)(this.max / increment + 1L));
/* 186 */     this.displayedMax = ((int)(increment * this.nbLevels));
/*     */     
/*     */ 
/* 189 */     int[] result = new int[this.nbLevels + 1];
/* 190 */     for (int i = 0; i < this.nbLevels + 1; i++) {
/* 191 */       result[i] = ((int)(i * increment));
/*     */     }
/*     */     
/* 194 */     this.scaleValues = result;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getScaleValues()
/*     */   {
/* 200 */     return this.scaleValues;
/*     */   }
/*     */   
/*     */   public int getScaledValue(int value) {
/* 204 */     return (int)(value * this.nbPixels / this.displayedMax);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/Scale.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */