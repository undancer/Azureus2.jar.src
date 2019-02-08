/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class Colors
/*     */   implements ParameterListener
/*     */ {
/*  44 */   private static final LogIDs LOGID = LogIDs.GUI;
/*  45 */   private static Colors instance = null;
/*     */   
/*     */   public static final int BLUES_LIGHTEST = 0;
/*     */   
/*     */   public static final int BLUES_DARKEST = 9;
/*     */   public static final int BLUES_MIDLIGHT = 2;
/*     */   public static final int BLUES_MIDDARK = 7;
/*     */   public static final int FADED_LIGHTEST = 0;
/*     */   public static final int FADED_DARKEST = 9;
/*  54 */   public static Color[] blues = new Color[10];
/*  55 */   public static Color[] faded = new Color[10];
/*     */   
/*     */   public static Color colorProgressBar;
/*     */   public static Color colorInverse;
/*     */   public static Color colorShiftLeft;
/*     */   public static Color colorShiftRight;
/*     */   public static Color colorError;
/*     */   public static Color colorErrorBG;
/*     */   public static Color colorAltRow;
/*     */   public static Color colorWarning;
/*     */   public static Color black;
/*     */   public static Color light_grey;
/*     */   public static Color dark_grey;
/*     */   public static Color blue;
/*     */   public static Color green;
/*     */   public static Color fadedGreen;
/*     */   public static Color grey;
/*     */   public static Color red;
/*     */   public static Color fadedRed;
/*     */   public static Color yellow;
/*     */   public static Color fadedYellow;
/*     */   public static Color white;
/*     */   public static Color background;
/*     */   public static Color red_ConsoleView;
/*  79 */   private static AEMonitor class_mon = new AEMonitor("Colors");
/*     */   
/*     */   public static int diffHue;
/*     */   
/*     */   public static float diffSatPct;
/*     */   public static float diffLumPct;
/*  85 */   static List<ParameterListener> listeners = new ArrayList();
/*     */   private Display display;
/*     */   
/*  88 */   static { ParameterListener l = new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */         List<ParameterListener> copy;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  97 */         synchronized (Colors.listeners) {
/*  98 */           copy = new ArrayList(Colors.listeners);
/*     */         }
/*     */         
/* 101 */         for (ParameterListener l : copy) {
/*     */           try {
/* 103 */             l.parameterChanged(parameterName);
/*     */           } catch (Throwable e) {
/* 105 */             Debug.out(e);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 110 */     };
/* 111 */     COConfigurationManager.addParameterListener("Color Scheme", l);
/* 112 */     COConfigurationManager.addParameterListener("Colors.progressBar.override", l);
/* 113 */     COConfigurationManager.addParameterListener("Colors.progressBar", l);
/* 114 */     COConfigurationManager.addParameterListener("Colors.error.override", l);
/* 115 */     COConfigurationManager.addParameterListener("Colors.error", l);
/* 116 */     COConfigurationManager.addParameterListener("Colors.warning.override", l);
/* 117 */     COConfigurationManager.addParameterListener("Colors.warning", l);
/* 118 */     COConfigurationManager.addParameterListener("Colors.altRow.override", l);
/* 119 */     COConfigurationManager.addParameterListener("Colors.altRow", l);
/*     */   }
/*     */   
/*     */   private void allocateBlues() {
/* 123 */     int r = 0;
/* 124 */     int g = 128;
/* 125 */     int b = 255;
/*     */     try {
/* 127 */       r = COConfigurationManager.getIntParameter("Color Scheme.red", r);
/* 128 */       g = COConfigurationManager.getIntParameter("Color Scheme.green", g);
/* 129 */       b = COConfigurationManager.getIntParameter("Color Scheme.blue", b);
/*     */       
/* 131 */       boolean bGrayScale = (r == b) && (b == g);
/*     */       
/* 133 */       HSLColor hslDefault = new HSLColor();
/* 134 */       hslDefault.initHSLbyRGB(0, 128, 255);
/*     */       
/* 136 */       HSLColor hslScheme = new HSLColor();
/* 137 */       hslScheme.initHSLbyRGB(r, g, b);
/*     */       
/* 139 */       diffHue = hslScheme.getHue() - hslDefault.getHue();
/* 140 */       diffSatPct = hslScheme.getSaturation() == 0 ? 0.0F : hslDefault.getSaturation() / hslScheme.getSaturation();
/* 141 */       diffLumPct = hslScheme.getLuminence() == 0 ? 0.0F : hslDefault.getLuminence() / hslScheme.getLuminence();
/*     */       
/* 143 */       HSLColor hslColor = new HSLColor();
/* 144 */       Color colorTables = this.display.getSystemColor(25);
/* 145 */       int tR = colorTables.getRed();
/* 146 */       int tG = colorTables.getGreen();
/* 147 */       int tB = colorTables.getBlue();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 152 */       for (int i = 0; i < blues.length; i++) {
/* 153 */         hslColor.initHSLbyRGB(r, g, b);
/* 154 */         float blendBy = i == 0 ? 1.0F : 1.0F - i / (blues.length - 1);
/*     */         
/* 156 */         hslColor.blend(tR, tG, tB, blendBy);
/* 157 */         blues[i] = ColorCache.getColor(this.display, hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue());
/*     */         
/* 159 */         int iSat = hslColor.getSaturation();
/* 160 */         int luminence = hslColor.getLuminence();
/* 161 */         if (luminence < 20) {
/* 162 */           if (iSat > 10) {
/* 163 */             hslColor.setSaturation(iSat / 2);
/* 164 */             hslColor.brighten(1.25F);
/* 165 */           } else if (bGrayScale)
/*     */           {
/* 167 */             hslColor.brighten(1.2F);
/*     */           }
/*     */         }
/* 170 */         else if (iSat > 10) {
/* 171 */           hslColor.setSaturation(iSat / 2);
/* 172 */           hslColor.brighten(0.75F);
/* 173 */         } else if (bGrayScale)
/*     */         {
/* 175 */           hslColor.brighten(0.8F);
/*     */         }
/*     */         
/*     */ 
/* 179 */         faded[i] = ColorCache.getColor(this.display, hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue());
/*     */       }
/*     */       
/*     */ 
/* 183 */       if (bGrayScale) {
/* 184 */         if (b > 200) {
/* 185 */           b -= 20;
/*     */         } else
/* 187 */           b += 20;
/*     */       }
/* 189 */       hslColor.initHSLbyRGB(r, g, b);
/* 190 */       hslColor.reverseColor();
/* 191 */       colorInverse = ColorCache.getColor(this.display, hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue());
/*     */       
/*     */ 
/* 194 */       hslColor.initHSLbyRGB(r, g, b);
/* 195 */       hslColor.setHue(hslColor.getHue() + 25);
/* 196 */       colorShiftRight = ColorCache.getColor(this.display, hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue());
/*     */       
/*     */ 
/* 199 */       hslColor.initHSLbyRGB(r, g, b);
/* 200 */       hslColor.setHue(hslColor.getHue() - 25);
/* 201 */       colorShiftLeft = ColorCache.getColor(this.display, hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue());
/*     */     }
/*     */     catch (Exception e) {
/* 204 */       Logger.log(new LogEvent(LOGID, "Error allocating colors", e));
/*     */     }
/*     */   }
/*     */   
/*     */   private void allocateColorProgressBar() {
/* 209 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 210 */       return;
/*     */     }
/* 212 */     colorProgressBar = new AllocateColor("progressBar", colorShiftRight, colorProgressBar).getColor();
/*     */   }
/*     */   
/*     */   private void allocateColorErrorBG()
/*     */   {
/* 217 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 218 */       return;
/*     */     }
/* 220 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 222 */         Color colorTables = Colors.this.display.getSystemColor(25);
/* 223 */         HSLColor hslColor = new HSLColor();
/* 224 */         hslColor.initHSLbyRGB(colorTables.getRed(), colorTables.getGreen(), colorTables.getBlue());
/*     */         
/* 226 */         int lum = hslColor.getLuminence();
/* 227 */         int sat = hslColor.getSaturation();
/*     */         
/* 229 */         lum = (int)(lum > 127 ? lum * 0.8D : lum * 1.3D);
/*     */         
/* 231 */         if (sat == 0) {
/* 232 */           sat = 80;
/*     */         }
/*     */         
/* 235 */         hslColor.initRGBbyHSL(0, sat, lum);
/*     */         
/* 237 */         Colors.colorErrorBG = new Colors.AllocateColor(Colors.this, "errorBG", new RGB(hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue()), Colors.colorErrorBG).getColor(); } }, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void allocateColorError()
/*     */   {
/* 244 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 245 */       return;
/*     */     }
/* 247 */     colorError = new AllocateColor("error", new RGB(255, 68, 68), colorError).getColor();
/*     */   }
/*     */   
/*     */   private void allocateColorWarning()
/*     */   {
/* 252 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 253 */       return;
/*     */     }
/* 255 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 257 */         Color colorTables = Colors.this.display.getSystemColor(25);
/* 258 */         HSLColor hslBG = new HSLColor();
/* 259 */         hslBG.initHSLbyRGB(colorTables.getRed(), colorTables.getGreen(), colorTables.getBlue());
/*     */         
/* 261 */         int lum = hslBG.getLuminence();
/*     */         
/* 263 */         HSLColor hslColor = new HSLColor();
/* 264 */         hslColor.initRGBbyHSL(25, 200, '' + (lum < 160 ? 10 : -10));
/* 265 */         Colors.colorWarning = new Colors.AllocateColor(Colors.this, "warning", new RGB(hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue()), Colors.colorWarning).getColor(); } }, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void allocateColorAltRow()
/*     */   {
/* 272 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 273 */       return;
/*     */     }
/* 275 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 277 */         Color colorTables = Colors.this.display.getSystemColor(25);
/* 278 */         HSLColor hslColor = new HSLColor();
/* 279 */         hslColor.initHSLbyRGB(colorTables.getRed(), colorTables.getGreen(), colorTables.getBlue());
/*     */         
/*     */ 
/* 282 */         int lum = hslColor.getLuminence();
/* 283 */         int sat = hslColor.getSaturation();
/* 284 */         int hue = hslColor.getHue();
/* 285 */         if (lum > 127) {
/* 286 */           lum -= 10;
/* 287 */           sat = 127;
/* 288 */           hue = 155;
/*     */         } else {
/* 290 */           lum += 30;
/*     */         }
/* 292 */         hslColor.setLuminence(lum);
/* 293 */         hslColor.setHue(hue);
/* 294 */         hslColor.setSaturation(sat);
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
/* 307 */         Colors.colorAltRow = new Colors.AllocateColor(Colors.this, "altRow", new RGB(hslColor.getRed(), hslColor.getGreen(), hslColor.getBlue()), Colors.colorAltRow).getColor(); } }, false);
/*     */   }
/*     */   
/*     */ 
/*     */   private class AllocateColor
/*     */     extends AERunnable
/*     */   {
/*     */     private String sName;
/*     */     private RGB rgbDefault;
/*     */     private Color newColor;
/*     */     
/*     */     public AllocateColor(String sName, RGB rgbDefault, Color colorOld)
/*     */     {
/* 320 */       this.sName = sName;
/* 321 */       this.rgbDefault = rgbDefault;
/*     */     }
/*     */     
/*     */     public AllocateColor(String sName, final Color colorDefault, Color colorOld) {
/* 325 */       this.sName = sName;
/* 326 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 328 */           if (!colorDefault.isDisposed()) {
/* 329 */             Colors.AllocateColor.this.rgbDefault = colorDefault.getRGB();
/*     */           } else
/* 331 */             Colors.AllocateColor.this.rgbDefault = new RGB(0, 0, 0); } }, false);
/*     */     }
/*     */     
/*     */ 
/*     */     public Color getColor()
/*     */     {
/* 337 */       Utils.execSWTThread(this, false);
/* 338 */       return this.newColor;
/*     */     }
/*     */     
/*     */     public void runSupport() {
/* 342 */       if (COConfigurationManager.getBooleanParameter("Colors." + this.sName + ".override")) {
/* 343 */         this.newColor = ColorCache.getColor(Colors.this.display, COConfigurationManager.getIntParameter("Colors." + this.sName + ".red", this.rgbDefault.red), COConfigurationManager.getIntParameter("Colors." + this.sName + ".green", this.rgbDefault.green), COConfigurationManager.getIntParameter("Colors." + this.sName + ".blue", this.rgbDefault.blue));
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 351 */         this.newColor = ColorCache.getColor(Colors.this.display, this.rgbDefault.red, this.rgbDefault.green, this.rgbDefault.blue);
/*     */         
/*     */ 
/*     */ 
/* 355 */         COConfigurationManager.setRGBParameter("Colors." + this.sName, this.rgbDefault.red, this.rgbDefault.green, this.rgbDefault.blue);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void allocateDynamicColors(final boolean first_time)
/*     */   {
/* 362 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 363 */       return;
/*     */     }
/* 365 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 367 */         Colors.this.allocateBlues();
/* 368 */         Colors.this.allocateColorProgressBar();
/* 369 */         Colors.this.allocateColorErrorBG();
/*     */         
/* 371 */         if (!first_time)
/* 372 */           ColorCache.reset(); } }, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void allocateNonDynamicColors()
/*     */   {
/* 379 */     allocateColorWarning();
/* 380 */     allocateColorError();
/* 381 */     allocateColorAltRow();
/*     */     
/* 383 */     black = ColorCache.getColor(this.display, 0, 0, 0);
/* 384 */     light_grey = ColorCache.getColor(this.display, 192, 192, 192);
/* 385 */     dark_grey = ColorCache.getColor(this.display, 96, 96, 96);
/* 386 */     blue = ColorCache.getColor(this.display, 0, 0, 170);
/* 387 */     green = ColorCache.getColor(this.display, 0, 170, 0);
/* 388 */     fadedGreen = ColorCache.getColor(this.display, 96, 160, 96);
/* 389 */     grey = ColorCache.getColor(this.display, 170, 170, 170);
/* 390 */     red = ColorCache.getColor(this.display, 255, 0, 0);
/* 391 */     fadedRed = ColorCache.getColor(this.display, 160, 96, 96);
/* 392 */     yellow = ColorCache.getColor(this.display, 255, 255, 0);
/* 393 */     fadedYellow = ColorCache.getColor(this.display, 255, 255, 221);
/* 394 */     white = ColorCache.getColor(this.display, 255, 255, 255);
/* 395 */     background = ColorCache.getColor(this.display, 248, 248, 248);
/* 396 */     red_ConsoleView = ColorCache.getColor(this.display, 255, 192, 192);
/*     */   }
/*     */   
/*     */ 
/*     */   private Colors()
/*     */   {
/* 402 */     instance = this;
/*     */     try {
/* 404 */       this.display = SWTThread.getInstance().getDisplay();
/*     */     } catch (Exception e) {
/* 406 */       this.display = Display.getDefault();
/*     */     }
/* 408 */     allocateDynamicColors(true);
/* 409 */     allocateNonDynamicColors();
/*     */     
/* 411 */     addColorsChangedListener(this);
/*     */   }
/*     */   
/*     */   public static Colors getInstance() {
/*     */     try {
/* 416 */       class_mon.enter();
/* 417 */       if (instance == null) {
/* 418 */         instance = new Colors();
/*     */       }
/* 420 */       return instance;
/*     */     }
/*     */     finally {
/* 423 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addColorsChangedListener(ParameterListener l) {
/* 428 */     synchronized (listeners) {
/* 429 */       listeners.add(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeColorsChangedListener(ParameterListener l) {
/* 434 */     synchronized (listeners) {
/* 435 */       listeners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameterName) {
/* 440 */     if (parameterName.equals("Color Scheme")) {
/* 441 */       allocateDynamicColors(false);
/*     */     }
/*     */     
/* 444 */     if (parameterName.startsWith("Colors.progressBar")) {
/* 445 */       allocateColorProgressBar();
/*     */     }
/* 447 */     if (parameterName.startsWith("Colors.error")) {
/* 448 */       allocateColorError();
/*     */     }
/* 450 */     if (parameterName.startsWith("Colors.warning")) {
/* 451 */       allocateColorWarning();
/*     */     }
/* 453 */     if (parameterName.startsWith("Colors.altRow")) {
/* 454 */       allocateColorAltRow();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/Colors.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */