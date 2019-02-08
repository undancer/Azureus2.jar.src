/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.skin.SkinPropertiesImpl;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
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
/*     */ 
/*     */ 
/*     */ public class SWTSkinPropertiesImpl
/*     */   extends SkinPropertiesImpl
/*     */   implements SWTSkinProperties
/*     */ {
/*  41 */   private static Map<String, SWTColorWithAlpha> colorMap = new LightHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SWTSkinPropertiesImpl(ClassLoader classLoader, String skinPath, String mainSkinFile)
/*     */   {
/*  48 */     super(classLoader, skinPath, mainSkinFile);
/*  49 */     setEmHeight();
/*     */   }
/*     */   
/*     */   private void setEmHeight() {
/*  53 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/*  55 */         SWTSkinPropertiesImpl.this.setEmHeightPX(FontUtils.getFontHeightInPX(Display.getDefault().getSystemFont()));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SWTSkinPropertiesImpl()
/*     */   {
/*  65 */     setEmHeight();
/*     */   }
/*     */   
/*     */   public Color getColor(String sID)
/*     */   {
/*  70 */     return getColorWithAlpha(sID).color;
/*     */   }
/*     */   
/*     */   public SWTColorWithAlpha getColorWithAlpha(String sID)
/*     */   {
/*  75 */     if (colorMap.containsKey(sID)) {
/*  76 */       return (SWTColorWithAlpha)colorMap.get(sID);
/*     */     }
/*     */     
/*  79 */     int alpha = 255;
/*     */     Color color;
/*  81 */     try { int[] rgb = getColorValue(sID);
/*  82 */       if (rgb[0] > -1) {
/*  83 */         Color color = ColorCache.getSchemedColor(Utils.getDisplay(), rgb[0], rgb[1], rgb[2]);
/*  84 */         if (rgb.length > 3) {
/*  85 */           alpha = rgb[3];
/*     */         }
/*     */       } else {
/*  88 */         color = ColorCache.getColor(Utils.getDisplay(), getStringValue(sID));
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  93 */       color = null;
/*     */     }
/*     */     
/*  96 */     SWTColorWithAlpha colorInfo = new SWTColorWithAlpha(color, alpha);
/*  97 */     colorMap.put(sID, colorInfo);
/*     */     
/*  99 */     return colorInfo;
/*     */   }
/*     */   
/*     */   public void clearCache() {
/* 103 */     super.clearCache();
/* 104 */     colorMap.clear();
/*     */   }
/*     */   
/*     */   public Color getColor(String name, Color def)
/*     */   {
/* 109 */     Color color = getColor(name);
/* 110 */     if (color == null) {
/* 111 */       return def;
/*     */     }
/* 113 */     return color;
/*     */   }
/*     */   
/*     */   public int getPxValue(String name, int def)
/*     */   {
/* 118 */     String value = getValue(name, null);
/* 119 */     if (value == null) {
/* 120 */       return def;
/*     */     }
/*     */     
/* 123 */     int result = def;
/*     */     try {
/* 125 */       if (value.endsWith("rem")) {
/* 126 */         float em = Float.parseFloat(value.substring(0, value.length() - 3));
/*     */         
/* 128 */         result = (int)(getEmHeightPX() * em);
/*     */       } else {
/* 130 */         result = Integer.parseInt(value);
/* 131 */         result = Utils.adjustPXForDPI(result);
/*     */       }
/*     */     }
/*     */     catch (NumberFormatException e) {}
/*     */     
/*     */ 
/* 137 */     return result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinPropertiesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */