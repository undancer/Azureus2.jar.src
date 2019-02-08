/*     */ package com.aelitis.azureus.ui.swt.utils;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
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
/*     */ public class FontUtils
/*     */ {
/*     */   private static Method mFontData_SetHeight;
/*     */   private static Method mFontData_GetHeightF;
/*     */   private static Font fontBold;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  47 */       mFontData_SetHeight = FontData.class.getDeclaredMethod("setHeight", new Class[] { Float.TYPE });
/*     */       
/*     */ 
/*     */ 
/*  51 */       mFontData_SetHeight.setAccessible(true);
/*     */     } catch (Throwable e) {
/*  53 */       mFontData_SetHeight = null;
/*     */     }
/*     */     try
/*     */     {
/*  57 */       mFontData_GetHeightF = FontData.class.getDeclaredMethod("getHeightF", new Class[0]);
/*     */       
/*     */ 
/*  60 */       mFontData_GetHeightF.setAccessible(true);
/*     */     } catch (Throwable e) {
/*  62 */       mFontData_GetHeightF = null;
/*     */     }
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
/*     */ 
/*     */   public static float getFontHeightFromPX(Font baseFont, GC gc, int heightInPixels)
/*     */   {
/*  77 */     Font font = null;
/*  78 */     Device device = baseFont.getDevice();
/*     */     
/*     */ 
/*  81 */     heightInPixels++;
/*     */     
/*     */ 
/*  84 */     float[] size = { Utils.pixelsToPoint(heightInPixels, Utils.getDPIRaw(device).y) + 1 };
/*     */     
/*     */ 
/*  87 */     if (size[0] <= 0.0F) {
/*  88 */       return 0.0F;
/*     */     }
/*     */     
/*  91 */     boolean bOurGC = (gc == null) || (gc.isDisposed());
/*     */     try {
/*  93 */       if (bOurGC) {
/*  94 */         gc = new GC(device);
/*     */       }
/*  96 */       FontData[] fontData = baseFont.getFontData();
/*     */       
/*  98 */       font = findFont(gc, font, fontData, size, heightInPixels, -1);
/*     */     }
/*     */     finally {
/* 101 */       if (bOurGC) {
/* 102 */         gc.dispose();
/*     */       }
/* 104 */       if ((font != null) && (!font.isDisposed())) {
/* 105 */         font.dispose();
/*     */       }
/*     */     }
/* 108 */     return size[0];
/*     */   }
/*     */   
/*     */   public static float getFontHeightFromPX(Device device, FontData[] fontData, GC gc, int heightInPixels)
/*     */   {
/* 113 */     Font font = null;
/*     */     
/*     */ 
/* 116 */     heightInPixels++;
/*     */     
/*     */ 
/* 119 */     float[] size = { Utils.pixelsToPoint(heightInPixels, Utils.getDPIRaw(device).y) + 1 };
/*     */     
/*     */ 
/* 122 */     if (size[0] <= 0.0F) {
/* 123 */       return 0.0F;
/*     */     }
/*     */     
/* 126 */     boolean bOurGC = (gc == null) || (gc.isDisposed());
/*     */     try {
/* 128 */       if (bOurGC) {
/* 129 */         gc = new GC(device);
/*     */       }
/*     */       
/* 132 */       font = findFont(gc, font, fontData, size, heightInPixels, -1);
/*     */     }
/*     */     finally {
/* 135 */       if (bOurGC) {
/* 136 */         gc.dispose();
/*     */       }
/* 138 */       if ((font != null) && (!font.isDisposed())) {
/* 139 */         font.dispose();
/*     */       }
/*     */     }
/* 142 */     return size[0];
/*     */   }
/*     */   
/*     */   public static Font getFontWithHeight(Font baseFont, GC gc, int heightInPixels) {
/* 146 */     return getFontWithHeight(baseFont, gc, heightInPixels, -1);
/*     */   }
/*     */   
/*     */   public static Font getFontWithHeight(Font baseFont, GC gc, int heightInPixels, int style)
/*     */   {
/* 151 */     Font font = null;
/* 152 */     Device device = baseFont.getDevice();
/*     */     
/*     */ 
/* 155 */     heightInPixels++;
/*     */     
/*     */ 
/* 158 */     float[] size = { Utils.pixelsToPoint(heightInPixels, Utils.getDPIRaw(device).y) + 1 };
/*     */     
/*     */ 
/* 161 */     if (size[0] <= 0.0F) {
/* 162 */       size[0] = 2.0F;
/*     */     }
/*     */     
/* 165 */     boolean bOurGC = (gc == null) || (gc.isDisposed());
/*     */     try {
/* 167 */       if (bOurGC) {
/* 168 */         gc = new GC(device);
/*     */       }
/* 170 */       FontData[] fontData = baseFont.getFontData();
/*     */       
/* 172 */       font = findFont(gc, font, fontData, size, heightInPixels, style);
/*     */     }
/*     */     finally {
/* 175 */       if (bOurGC) {
/* 176 */         gc.dispose();
/*     */       }
/*     */     }
/*     */     
/* 180 */     return font;
/*     */   }
/*     */   
/*     */   public static void setFontDataHeight(FontData[] fd, float fontSize) {
/* 184 */     if (mFontData_SetHeight != null) {
/*     */       try {
/* 186 */         mFontData_SetHeight.invoke(fd[0], new Object[] { Float.valueOf(fontSize) });
/* 187 */         return;
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 192 */     fd[0].setHeight((int)fontSize);
/*     */   }
/*     */   
/*     */   private static Font findFont(GC gc, Font font, FontData[] fontData, float[] size, int heightInPixels, int style)
/*     */   {
/* 197 */     if (mFontData_SetHeight != null) {
/* 198 */       return findFontByFloat(gc, font, fontData, size, heightInPixels, style);
/*     */     }
/* 200 */     return findFontByInt(gc, font, fontData, size, heightInPixels, style);
/*     */   }
/*     */   
/*     */   public static Font findFontByInt(GC gc, Font font, FontData[] fontData, float[] returnSize, int heightInPixels, int style)
/*     */   {
/* 205 */     int size = (int)returnSize[0];
/*     */     do {
/* 207 */       if (font != null) {
/* 208 */         size--;
/* 209 */         font.dispose();
/*     */       }
/* 211 */       fontData[0].setHeight(size);
/* 212 */       if (style != -1) {
/* 213 */         fontData[0].setStyle(style);
/*     */       }
/*     */       
/* 216 */       font = new Font(gc.getDevice(), fontData);
/*     */       
/* 218 */       gc.setFont(font);
/*     */ 
/*     */     }
/* 221 */     while ((font != null) && (gc.textExtent("(/|,jI~`gy").y > heightInPixels) && (size > 1));
/*     */     
/* 223 */     returnSize[0] = size;
/* 224 */     return font;
/*     */   }
/*     */   
/*     */   public static Font findFontByFloat(GC gc, Font font, FontData[] fontData, float[] returnSize, int heightInPixels, int style)
/*     */   {
/* 229 */     float size = returnSize[0];
/* 230 */     float delta = 2.0F;
/*     */     
/* 232 */     int numLoops = 0;
/*     */     boolean fits;
/* 234 */     do { numLoops++;
/* 235 */       if (font != null) {
/* 236 */         size -= delta;
/* 237 */         font.dispose();
/*     */       }
/*     */       try {
/* 240 */         mFontData_SetHeight.invoke(fontData[0], new Object[] { Float.valueOf(size) });
/*     */       } catch (Throwable e) {
/* 242 */         Debug.out(e);
/*     */       }
/* 244 */       if (style != -1) {
/* 245 */         fontData[0].setStyle(style);
/*     */       }
/*     */       
/* 248 */       font = new Font(gc.getDevice(), fontData);
/*     */       
/* 250 */       gc.setFont(font);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 256 */       fits = gc.textExtent("(/|,jI~`gy").y <= heightInPixels;
/* 257 */       if ((fits) && (delta > 0.1D)) {
/* 258 */         size += delta;
/* 259 */         delta /= 2.0F;
/* 260 */         fits = false;
/*     */       }
/* 262 */     } while ((!fits) && (size > 1.0F));
/*     */     
/* 264 */     returnSize[0] = size;
/* 265 */     return font;
/*     */   }
/*     */   
/*     */   public static int getFontHeightInPX(FontData[] fd) {
/* 269 */     Font font = new Font(Display.getDefault(), fd);
/*     */     try {
/* 271 */       return getFontHeightInPX(font);
/*     */     } finally {
/* 273 */       font.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public static int getFontHeightInPX(Font font) {
/* 278 */     GC gc = new GC(font.getDevice());
/*     */     try {
/* 280 */       gc.setFont(font);
/* 281 */       return gc.textExtent("(/|,jI~`gy").y;
/*     */     } finally {
/* 283 */       gc.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setFontHeight(Control control, int height, int style)
/*     */   {
/* 295 */     FontData[] fDatas = control.getFont().getFontData();
/* 296 */     for (int i = 0; i < fDatas.length; i++) {
/* 297 */       fDatas[i].height = height;
/* 298 */       fDatas[i].setStyle(style);
/*     */     }
/* 300 */     Font newFont = new Font(control.getDisplay(), fDatas);
/* 301 */     control.setFont(newFont);
/* 302 */     control.addDisposeListener(new DisposeListener()
/*     */     {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 305 */         if ((null != this.val$newFont) && (!this.val$newFont.isDisposed())) {
/* 306 */           this.val$newFont.dispose();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static float getHeight(FontData[] fd) {
/* 313 */     if (mFontData_GetHeightF != null) {
/*     */       try {
/* 315 */         return ((Number)mFontData_GetHeightF.invoke(fd[0], new Object[0])).floatValue();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 320 */     return fd[0].getHeight();
/*     */   }
/*     */   
/*     */   public static Font getFontPercentOf(Font baseFont, float pct) {
/* 324 */     FontData[] fontData = baseFont.getFontData();
/* 325 */     float height = getHeight(fontData) * pct;
/* 326 */     setFontDataHeight(fontData, height);
/*     */     
/* 328 */     return new Font(baseFont.getDevice(), fontData);
/*     */   }
/*     */   
/*     */   public static Font getAnyFontBold(GC gc) {
/* 332 */     if (fontBold == null) {
/* 333 */       FontData[] fontData = gc.getFont().getFontData();
/* 334 */       for (int i = 0; i < fontData.length; i++) {
/* 335 */         FontData fd = fontData[i];
/* 336 */         fd.setStyle(1);
/*     */       }
/* 338 */       fontBold = new Font(gc.getDevice(), fontData);
/*     */     }
/* 340 */     return fontBold;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/FontUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */