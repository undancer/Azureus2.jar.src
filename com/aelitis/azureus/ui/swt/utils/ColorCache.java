/*     */ package com.aelitis.azureus.ui.swt.utils;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class ColorCache
/*     */ {
/*  38 */   private static final boolean DEBUG = ;
/*     */   
/*  40 */   private static final Map<Long, Color> mapColors = new HashMap();
/*     */   
/*     */   private static final int SYSTEMCOLOR_INDEXSTART = 17;
/*  43 */   private static final String[] systemColorNames = { "COLOR_WIDGET_DARK_SHADOW", "COLOR_WIDGET_NORMAL_SHADOW", "COLOR_WIDGET_LIGHT_SHADOW", "COLOR_WIDGET_HIGHLIGHT_SHADOW", "COLOR_WIDGET_FOREGROUND", "COLOR_WIDGET_BACKGROUND", "COLOR_WIDGET_BORDER", "COLOR_LIST_FOREGROUND", "COLOR_LIST_BACKGROUND", "COLOR_LIST_SELECTION", "COLOR_LIST_SELECTION_TEXT", "COLOR_INFO_FOREGROUND", "COLOR_INFO_BACKGROUND", "COLOR_TITLE_FOREGROUND", "COLOR_TITLE_BACKGROUND" };
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
/*     */   static
/*     */   {
/*  62 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator() {
/*     */       public void generate(IndentWriter writer) {
/*  64 */         writer.println("Colors:");
/*  65 */         writer.indent();
/*  66 */         writer.println("# cached: " + ColorCache.mapColors.size());
/*  67 */         writer.exdent();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void reset()
/*     */   {
/*  79 */     mapColors.clear();
/*     */   }
/*     */   
/*     */   public static Color getSchemedColor(Device device, int red, int green, int blue) {
/*  83 */     ensureMapColorsInitialized(device);
/*     */     
/*  85 */     Long key = new Long((red << 16) + (green << 8) + blue + 16777216L);
/*     */     
/*  87 */     Color color = (Color)mapColors.get(key);
/*  88 */     if ((color == null) || (color.isDisposed())) {
/*     */       try {
/*  90 */         if (red < 0) {
/*  91 */           red = 0;
/*  92 */         } else if (red > 255) {
/*  93 */           red = 255;
/*     */         }
/*  95 */         if (green < 0) {
/*  96 */           green = 0;
/*  97 */         } else if (green > 255) {
/*  98 */           green = 255;
/*     */         }
/* 100 */         if (blue < 0) {
/* 101 */           blue = 0;
/* 102 */         } else if (blue > 255) {
/* 103 */           blue = 255;
/*     */         }
/*     */         
/* 106 */         RGB rgb = new RGB(red, green, blue);
/* 107 */         float[] hsb = rgb.getHSB();
/* 108 */         hsb[0] += Colors.diffHue;
/* 109 */         if (hsb[0] > 360.0F) {
/* 110 */           hsb[0] -= 360.0F;
/* 111 */         } else if (hsb[0] < 0.0F) {
/* 112 */           hsb[0] += 360.0F;
/*     */         }
/* 114 */         hsb[1] *= Colors.diffSatPct;
/*     */         
/*     */ 
/* 117 */         color = getColor(device, hsb);
/* 118 */         mapColors.put(key, color);
/*     */       } catch (IllegalArgumentException e) {
/* 120 */         Debug.out("One Invalid: " + red + ";" + green + ";" + blue, e);
/*     */       }
/*     */     }
/*     */     
/* 124 */     return color;
/*     */   }
/*     */   
/*     */   public static Color getColor(Device device, int red, int green, int blue) {
/* 128 */     if ((device == null) || (device.isDisposed())) {
/* 129 */       return null;
/*     */     }
/* 131 */     ensureMapColorsInitialized(device);
/*     */     
/* 133 */     Long key = new Long((red << 16) + (green << 8) + blue);
/*     */     
/* 135 */     Color color = (Color)mapColors.get(key);
/* 136 */     if ((color == null) || (color.isDisposed())) {
/*     */       try {
/* 138 */         if (red < 0) {
/* 139 */           red = 0;
/* 140 */         } else if (red > 255) {
/* 141 */           red = 255;
/*     */         }
/* 143 */         if (green < 0) {
/* 144 */           green = 0;
/* 145 */         } else if (green > 255) {
/* 146 */           green = 255;
/*     */         }
/* 148 */         if (blue < 0) {
/* 149 */           blue = 0;
/* 150 */         } else if (blue > 255) {
/* 151 */           blue = 255;
/*     */         }
/* 153 */         color = new Color(device, red, green, blue);
/*     */       } catch (IllegalArgumentException e) {
/* 155 */         Debug.out("One Invalid: " + red + ";" + green + ";" + blue, e);
/*     */       }
/* 157 */       addColor(key, color);
/*     */     }
/*     */     
/* 160 */     return color;
/*     */   }
/*     */   
/*     */   private static void ensureMapColorsInitialized(Device device) {
/* 164 */     if ((device == null) || (device.isDisposed())) {
/* 165 */       return;
/*     */     }
/* 167 */     if (mapColors.size() == 0) {
/* 168 */       for (int i = 1; i <= 16; i++) {
/* 169 */         Color color = device.getSystemColor(i);
/* 170 */         Long key = new Long((color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue());
/*     */         
/* 172 */         addColor(key, color);
/*     */       }
/* 174 */       if (DEBUG) {
/* 175 */         SimpleTimer.addPeriodicEvent("ColorCacheChecker", 60000L, new TimerEventPerformer()
/*     */         {
/*     */           public void perform(TimerEvent event) {
/* 178 */             Utils.execSWTThread(new AERunnable() {
/*     */               public void runSupport() {
/* 180 */                 for (Iterator<Long> iter = ColorCache.mapColors.keySet().iterator(); iter.hasNext();) {
/* 181 */                   Long key = (Long)iter.next();
/* 182 */                   Color color = (Color)ColorCache.mapColors.get(key);
/* 183 */                   if (color.isDisposed()) {
/* 184 */                     Logger.log(new LogAlert(false, 3, "Someone disposed of color " + Long.toHexString(key.longValue()) + ". Please report this on the " + "<A HREF=\"http://forum.vuze.com/forum.jspa?forumID=124\">forum</A>"));
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/* 189 */                     iter.remove();
/*     */                   }
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static Color getColor(Device device, String value) {
/* 201 */     return getColor(device, value, false);
/*     */   }
/*     */   
/*     */   public static Color getSchemedColor(Device device, String value) {
/* 205 */     return getColor(device, value, true);
/*     */   }
/*     */   
/*     */   private static Color getColor(Device device, String c_value, boolean useScheme) {
/* 209 */     int[] colors = new int[3];
/*     */     
/* 211 */     if ((c_value == null) || (c_value.length() == 0)) {
/* 212 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 216 */       if (c_value.charAt(0) == '#')
/*     */       {
/* 218 */         long l = Long.parseLong(c_value.substring(1), 16);
/* 219 */         colors[0] = ((int)(l >> 16 & 0xFF));
/* 220 */         colors[1] = ((int)(l >> 8 & 0xFF));
/* 221 */         colors[2] = ((int)(l & 0xFF));
/* 222 */       } else if (c_value.indexOf(',') > 0) {
/* 223 */         StringTokenizer st = new StringTokenizer(c_value, ",");
/* 224 */         colors[0] = Integer.parseInt(st.nextToken());
/* 225 */         colors[1] = Integer.parseInt(st.nextToken());
/* 226 */         colors[2] = Integer.parseInt(st.nextToken());
/*     */       } else {
/* 228 */         String u_value = c_value.toUpperCase();
/* 229 */         if (u_value.startsWith("COLOR_")) {
/* 230 */           for (int i = 0; i < systemColorNames.length; i++) {
/* 231 */             String name = systemColorNames[i];
/* 232 */             if ((name.equals(u_value)) && (device != null) && (!device.isDisposed()))
/* 233 */               return device.getSystemColor(i + 17);
/*     */           }
/*     */         } else {
/* 236 */           if (u_value.startsWith("BLUE.FADED.")) {
/* 237 */             int idx = Integer.parseInt(u_value.substring(11));
/* 238 */             return Colors.faded[idx]; }
/* 239 */           if (u_value.startsWith("BLUE.")) {
/* 240 */             int idx = Integer.parseInt(u_value.substring(5));
/* 241 */             return Colors.blues[idx]; }
/* 242 */           if (u_value.equals("ALTROW"))
/* 243 */             return Colors.colorAltRow;
/* 244 */           if (c_value.startsWith("config.")) {
/* 245 */             int def_pos = c_value.indexOf(':');
/*     */             
/*     */             String def_value;
/*     */             String config_name;
/*     */             String def_value;
/* 250 */             if (def_pos != -1)
/*     */             {
/* 252 */               String config_name = c_value.substring(0, def_pos);
/* 253 */               def_value = c_value.substring(def_pos + 1);
/*     */             } else {
/* 255 */               config_name = c_value;
/* 256 */               def_value = null;
/*     */             }
/*     */             
/* 259 */             String x_value = COConfigurationManager.getStringParameter(config_name, def_value);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 264 */             useScheme = x_value == def_value;
/*     */             
/* 266 */             Color result = getColor(device, x_value, useScheme);
/*     */             
/* 268 */             if (result == null)
/*     */             {
/* 270 */               Debug.out("No color found for '" + c_value + "'");
/*     */             }
/* 272 */             return Colors.white;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 277 */         return null;
/*     */       }
/*     */     } catch (Exception e) {
/* 280 */       Debug.out(c_value, e);
/* 281 */       return null;
/*     */     }
/*     */     
/* 284 */     if (!useScheme) {
/* 285 */       return getColor(device, colors[0], colors[1], colors[2]);
/*     */     }
/* 287 */     return getSchemedColor(device, colors[0], colors[1], colors[2]);
/*     */   }
/*     */   
/*     */   private static void addColor(Long key, Color color) {
/* 291 */     mapColors.put(key, color);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Color getColor(Device device, int[] rgb)
/*     */   {
/* 302 */     if ((rgb == null) || (rgb.length < 3)) {
/* 303 */       return null;
/*     */     }
/* 305 */     return getColor(device, rgb[0], rgb[1], rgb[2]);
/*     */   }
/*     */   
/*     */   public static Color getRandomColor() {
/* 309 */     if (mapColors.size() == 0) {
/* 310 */       return Colors.black;
/*     */     }
/* 312 */     int r = (int)(Math.random() * mapColors.size());
/* 313 */     return (Color)mapColors.values().toArray()[r];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Color getColor(Device device, float[] hsb)
/*     */   {
/* 324 */     if (hsb[0] < 0.0F) {
/* 325 */       hsb[0] = 0.0F;
/* 326 */     } else if (hsb[0] > 360.0F) {
/* 327 */       hsb[0] = 360.0F;
/*     */     }
/* 329 */     if (hsb[1] < 0.0F) {
/* 330 */       hsb[1] = 0.0F;
/* 331 */     } else if (hsb[1] > 1.0F) {
/* 332 */       hsb[1] = 1.0F;
/*     */     }
/* 334 */     if (hsb[2] < 0.0F) {
/* 335 */       hsb[2] = 0.0F;
/* 336 */     } else if (hsb[2] > 1.0F) {
/* 337 */       hsb[2] = 1.0F;
/*     */     }
/* 339 */     RGB rgb = new RGB(hsb[0], hsb[1], hsb[2]);
/* 340 */     return getColor(device, rgb.red, rgb.green, rgb.blue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Color getColor(Device device, RGB rgb)
/*     */   {
/* 351 */     return getColor(device, rgb.red, rgb.green, rgb.blue);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/ColorCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */