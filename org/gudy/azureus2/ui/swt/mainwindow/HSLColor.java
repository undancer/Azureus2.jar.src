/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HSLColor
/*     */ {
/*     */   private static final int HSLMAX = 255;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int RGBMAX = 255;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int UNDEFINED = 170;
/*     */   
/*     */ 
/*     */ 
/*     */   private int pHue;
/*     */   
/*     */ 
/*     */   private int pSat;
/*     */   
/*     */ 
/*     */   private int pLum;
/*     */   
/*     */ 
/*     */   private int pRed;
/*     */   
/*     */ 
/*     */   private int pGreen;
/*     */   
/*     */ 
/*     */   private int pBlue;
/*     */   
/*     */ 
/*     */ 
/*     */   public void initHSLbyRGB(int R, int G, int B)
/*     */   {
/*  41 */     this.pRed = R;
/*  42 */     this.pGreen = G;
/*  43 */     this.pBlue = B;
/*     */     
/*     */ 
/*  46 */     int cMax = iMax(iMax(R, G), B);
/*  47 */     int cMin = iMin(iMin(R, G), B);
/*     */     
/*  49 */     int cMinus = cMax - cMin;
/*  50 */     int cPlus = cMax + cMin;
/*     */     
/*     */ 
/*  53 */     this.pLum = ((cPlus * 255 + 255) / 510);
/*     */     
/*  55 */     if (cMax == cMin)
/*     */     {
/*  57 */       this.pSat = 0;
/*  58 */       this.pHue = 170;
/*     */     }
/*     */     else {
/*  61 */       if (this.pLum <= 127) {
/*  62 */         this.pSat = ((int)((cMinus * 255 + 0.5D) / cPlus));
/*     */       } else {
/*  64 */         this.pSat = ((int)((cMinus * 255 + 0.5D) / (510 - cPlus)));
/*     */       }
/*     */       
/*     */ 
/*  68 */       int RDelta = (int)(((cMax - R) * 42 + 0.5D) / cMinus);
/*  69 */       int GDelta = (int)(((cMax - G) * 42 + 0.5D) / cMinus);
/*  70 */       int BDelta = (int)(((cMax - B) * 42 + 0.5D) / cMinus);
/*     */       
/*  72 */       if (cMax == R) {
/*  73 */         this.pHue = (BDelta - GDelta);
/*  74 */       } else if (cMax == G) {
/*  75 */         this.pHue = (85 + RDelta - BDelta);
/*  76 */       } else if (cMax == B) {
/*  77 */         this.pHue = (170 + GDelta - RDelta);
/*     */       }
/*     */       
/*  80 */       if (this.pHue < 0) {
/*  81 */         this.pHue += 255;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initRGBbyHSL(int H, int S, int L)
/*     */   {
/*  90 */     this.pHue = H;
/*  91 */     this.pLum = L;
/*  92 */     this.pSat = S;
/*     */     
/*  94 */     if (S == 0) {
/*  95 */       this.pRed = (L * 255 / 255);
/*  96 */       this.pGreen = this.pRed;
/*  97 */       this.pBlue = this.pRed; } else { int Magic2;
/*     */       int Magic2;
/*  99 */       if (L <= 127) {
/* 100 */         Magic2 = (L * (255 + S) + 127) / 255;
/*     */       } else {
/* 102 */         Magic2 = L + S - (L * S + 127) / 255;
/*     */       }
/* 104 */       int Magic1 = 2 * L - Magic2;
/*     */       
/*     */ 
/* 107 */       this.pRed = ((hueToRGB(Magic1, Magic2, H + 85) * 255 + 127) / 255);
/* 108 */       if (this.pRed > 255) {
/* 109 */         this.pRed = 255;
/*     */       }
/*     */       
/* 112 */       this.pGreen = ((hueToRGB(Magic1, Magic2, H) * 255 + 127) / 255);
/* 113 */       if (this.pGreen > 255) {
/* 114 */         this.pGreen = 255;
/*     */       }
/*     */       
/* 117 */       this.pBlue = ((hueToRGB(Magic1, Magic2, H - 85) * 255 + 127) / 255);
/* 118 */       if (this.pBlue > 255) {
/* 119 */         this.pBlue = 255;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private int hueToRGB(int mag1, int mag2, int Hue)
/*     */   {
/* 126 */     if (Hue < 0) {
/* 127 */       Hue += 255;
/* 128 */     } else if (Hue > 255) {
/* 129 */       Hue -= 255;
/*     */     }
/*     */     
/* 132 */     if (Hue < 42) {
/* 133 */       return mag1 + ((mag2 - mag1) * Hue + 21) / 42;
/*     */     }
/* 135 */     if (Hue < 127) {
/* 136 */       return mag2;
/*     */     }
/* 138 */     if (Hue < 170) {
/* 139 */       return mag1 + ((mag2 - mag1) * (170 - Hue) + 21) / 42;
/*     */     }
/* 141 */     return mag1;
/*     */   }
/*     */   
/*     */   private int iMax(int a, int b) {
/* 145 */     if (a > b) return a; return b;
/*     */   }
/*     */   
/* 148 */   private int iMin(int a, int b) { if (a < b) return a; return b;
/*     */   }
/*     */   
/*     */ 
/*     */   private void greyscale()
/*     */   {
/* 154 */     initRGBbyHSL(170, 0, this.pLum);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getHue()
/*     */   {
/* 161 */     return this.pHue;
/*     */   }
/*     */   
/*     */   public void setHue(int iToValue) {
/* 165 */     while (iToValue < 0) {
/* 166 */       iToValue = 255 + iToValue;
/*     */     }
/* 168 */     while (iToValue > 255) {
/* 169 */       iToValue -= 255;
/*     */     }
/*     */     
/* 172 */     initRGBbyHSL(iToValue, this.pSat, this.pLum);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSaturation()
/*     */   {
/* 178 */     return this.pSat;
/*     */   }
/*     */   
/*     */   public void setSaturation(int iToValue) {
/* 182 */     if (iToValue < 0) {
/* 183 */       iToValue = 0;
/* 184 */     } else if (iToValue > 255) {
/* 185 */       iToValue = 255;
/*     */     }
/*     */     
/* 188 */     initRGBbyHSL(this.pHue, iToValue, this.pLum);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLuminence()
/*     */   {
/* 194 */     return this.pLum;
/*     */   }
/*     */   
/*     */   public void setLuminence(int iToValue) {
/* 198 */     if (iToValue < 0) {
/* 199 */       iToValue = 0;
/* 200 */     } else if (iToValue > 255) {
/* 201 */       iToValue = 255;
/*     */     }
/*     */     
/* 204 */     initRGBbyHSL(this.pHue, this.pSat, iToValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRed()
/*     */   {
/* 210 */     return this.pRed;
/*     */   }
/*     */   
/*     */   private void setRed(int iNewValue) {
/* 214 */     initHSLbyRGB(iNewValue, this.pGreen, this.pBlue);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getGreen()
/*     */   {
/* 220 */     return this.pGreen;
/*     */   }
/*     */   
/*     */   private void setGreen(int iNewValue) {
/* 224 */     initHSLbyRGB(this.pRed, iNewValue, this.pBlue);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBlue()
/*     */   {
/* 230 */     return this.pBlue;
/*     */   }
/*     */   
/*     */   private void setBlue(int iNewValue) {
/* 234 */     initHSLbyRGB(this.pRed, this.pGreen, iNewValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public void reverseColor()
/*     */   {
/* 240 */     setHue(this.pHue + 127);
/*     */   }
/*     */   
/*     */ 
/*     */   private void reverseLight()
/*     */   {
/* 246 */     setLuminence(255 - this.pLum);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void brighten(float fPercent)
/*     */   {
/* 254 */     if (fPercent == 0.0F) {
/* 255 */       return;
/*     */     }
/*     */     
/* 258 */     int L = (int)(this.pLum * fPercent);
/* 259 */     if (L < 0) L = 0;
/* 260 */     if (L > 255) { L = 255;
/*     */     }
/* 262 */     setLuminence(L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void blend(int R, int G, int B, float fPercent)
/*     */   {
/* 269 */     if (fPercent >= 1.0F) {
/* 270 */       initHSLbyRGB(R, G, B);
/* 271 */       return;
/*     */     }
/* 273 */     if (fPercent <= 0.0F) {
/* 274 */       return;
/*     */     }
/* 276 */     int newR = (int)(R * fPercent + this.pRed * (1.0D - fPercent));
/* 277 */     int newG = (int)(G * fPercent + this.pGreen * (1.0D - fPercent));
/* 278 */     int newB = (int)(B * fPercent + this.pBlue * (1.0D - fPercent));
/*     */     
/* 280 */     initHSLbyRGB(newR, newG, newB);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/HSLColor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */