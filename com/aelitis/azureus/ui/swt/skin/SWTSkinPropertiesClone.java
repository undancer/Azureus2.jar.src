/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ResourceBundle;
/*     */ import org.eclipse.swt.graphics.Color;
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
/*     */ public class SWTSkinPropertiesClone
/*     */   implements SWTSkinPropertiesParam
/*     */ {
/*     */   private static final String IGNORE_NAME = ".type";
/*     */   private static final boolean DEBUG = true;
/*     */   private final SWTSkinProperties properties;
/*     */   private final String sCloneConfigID;
/*     */   private final String sTemplateConfigID;
/*     */   private final String[] sCloneParams;
/*     */   
/*     */   public SWTSkinPropertiesClone(SWTSkinProperties properties, String sCloneConfigID, String[] sCloneParams)
/*     */   {
/*  60 */     this.properties = properties;
/*  61 */     this.sCloneConfigID = sCloneConfigID;
/*  62 */     this.sCloneParams = sCloneParams;
/*  63 */     this.sTemplateConfigID = sCloneParams[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void checkName(String name)
/*     */   {
/*  70 */     if (name.startsWith(this.sTemplateConfigID)) {
/*  71 */       System.err.println(name + " shouldn't have template prefix of " + this.sTemplateConfigID + "; " + Debug.getStackTrace(true, false));
/*     */     }
/*     */     
/*     */ 
/*  75 */     if (name.startsWith(this.sCloneConfigID)) {
/*  76 */       System.err.println(name + " shouldn't have clone prefix of " + this.sCloneConfigID + "; " + Debug.getStackTrace(true, false));
/*     */     }
/*     */   }
/*     */   
/*     */   public void addProperty(String name, String value)
/*     */   {
/*  82 */     this.properties.addProperty(this.sCloneConfigID + name, value);
/*     */   }
/*     */   
/*     */   public SWTColorWithAlpha getColorWithAlpha(String name)
/*     */   {
/*  87 */     if (name == null) {
/*  88 */       return null;
/*     */     }
/*     */     
/*  91 */     checkName(name);
/*     */     
/*  93 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/*  94 */       return this.properties.getColorWithAlpha(name);
/*     */     }
/*     */     
/*     */ 
/*  98 */     SWTColorWithAlpha val = this.properties.getColorWithAlpha(this.sCloneConfigID + name);
/*  99 */     if (val != null) {
/* 100 */       return val;
/*     */     }
/*     */     
/* 103 */     return this.properties.getColorWithAlpha(this.sTemplateConfigID + name);
/*     */   }
/*     */   
/*     */   public Color getColor(String name) {
/* 107 */     if (name == null) {
/* 108 */       return null;
/*     */     }
/*     */     
/* 111 */     checkName(name);
/*     */     
/* 113 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 114 */       return this.properties.getColor(name);
/*     */     }
/*     */     
/*     */ 
/* 118 */     Color val = this.properties.getColor(this.sCloneConfigID + name);
/* 119 */     if (val != null) {
/* 120 */       return val;
/*     */     }
/*     */     
/* 123 */     return this.properties.getColor(this.sTemplateConfigID + name);
/*     */   }
/*     */   
/*     */   public int[] getColorValue(String name) {
/* 127 */     if (name == null) {
/* 128 */       return new int[] { -1, -1, -1 };
/*     */     }
/*     */     
/* 131 */     checkName(name);
/*     */     
/* 133 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 134 */       return this.properties.getColorValue(name);
/*     */     }
/*     */     
/* 137 */     if (!name.equals(".type")) {
/* 138 */       int[] val = this.properties.getColorValue(this.sCloneConfigID + name);
/* 139 */       if (val[0] < 0) {
/* 140 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 144 */     return this.properties.getColorValue(this.sTemplateConfigID + name);
/*     */   }
/*     */   
/*     */   public int getIntValue(String name, int def) {
/* 148 */     if (name == null) {
/* 149 */       return def;
/*     */     }
/*     */     
/* 152 */     checkName(name);
/*     */     
/* 154 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 155 */       return this.properties.getIntValue(name, def);
/*     */     }
/*     */     
/* 158 */     if ((!name.equals(".type")) && 
/* 159 */       (this.properties.getStringValue(this.sCloneConfigID + name) != null)) {
/* 160 */       return this.properties.getIntValue(this.sCloneConfigID + name, def);
/*     */     }
/*     */     
/* 163 */     return this.properties.getIntValue(this.sTemplateConfigID + name, def);
/*     */   }
/*     */   
/*     */   public String[] getStringArray(String name) {
/* 167 */     if (name == null) {
/* 168 */       return null;
/*     */     }
/*     */     
/* 171 */     checkName(name);
/*     */     
/* 173 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 174 */       return this.properties.getStringArray(name);
/*     */     }
/*     */     
/* 177 */     if (!name.equals(".type")) {
/* 178 */       String[] val = this.properties.getStringArray(this.sCloneConfigID + name, this.sCloneParams);
/*     */       
/* 180 */       if (val != null) {
/* 181 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 185 */     return this.properties.getStringArray(this.sTemplateConfigID + name, this.sCloneParams);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String def) {
/* 189 */     if (name == null) {
/* 190 */       return def;
/*     */     }
/*     */     
/* 193 */     checkName(name);
/*     */     
/* 195 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 196 */       return this.properties.getStringValue(name, def);
/*     */     }
/*     */     
/* 199 */     if (!name.equals(".type")) {
/* 200 */       String val = this.properties.getStringValue(this.sCloneConfigID + name, this.sCloneParams);
/*     */       
/* 202 */       if (val != null) {
/* 203 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 207 */     return this.properties.getStringValue(this.sTemplateConfigID + name, this.sCloneParams, def);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name)
/*     */   {
/* 212 */     if (name == null) {
/* 213 */       return null;
/*     */     }
/*     */     
/* 216 */     checkName(name);
/*     */     
/* 218 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 219 */       return this.properties.getStringValue(name);
/*     */     }
/*     */     
/* 222 */     if (!name.equals(".type")) {
/* 223 */       String val = this.properties.getStringValue(this.sCloneConfigID + name, this.sCloneParams);
/*     */       
/* 225 */       if (val != null) {
/* 226 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 230 */     return this.properties.getStringValue(this.sTemplateConfigID + name, this.sCloneParams);
/*     */   }
/*     */   
/*     */   public String[] getStringArray(String name, String[] params) {
/* 234 */     if (name == null) {
/* 235 */       return null;
/*     */     }
/*     */     
/* 238 */     checkName(name);
/*     */     
/* 240 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 241 */       return this.properties.getStringArray(name, params);
/*     */     }
/*     */     
/* 244 */     if (!name.equals(".type")) {
/* 245 */       String[] val = this.properties.getStringArray(this.sCloneConfigID + name, params);
/* 246 */       if (val != null) {
/* 247 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 251 */     return this.properties.getStringArray(this.sTemplateConfigID + name, params);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String[] params, String def) {
/* 255 */     if (name == null) {
/* 256 */       return def;
/*     */     }
/*     */     
/* 259 */     checkName(name);
/*     */     
/* 261 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 262 */       return this.properties.getStringValue(name, params, def);
/*     */     }
/*     */     
/* 265 */     if (!name.equals(".type")) {
/* 266 */       String val = this.properties.getStringValue(this.sCloneConfigID + name, params);
/* 267 */       if (val != null) {
/* 268 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 272 */     return this.properties.getStringValue(this.sTemplateConfigID + name, params, def);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String[] params) {
/* 276 */     if (name == null) {
/* 277 */       return null;
/*     */     }
/*     */     
/* 280 */     checkName(name);
/*     */     
/* 282 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 283 */       return this.properties.getStringValue(name, params);
/*     */     }
/*     */     
/* 286 */     if (!name.equals(".type")) {
/* 287 */       String val = this.properties.getStringValue(this.sCloneConfigID + name, params);
/* 288 */       if (val != null) {
/* 289 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 293 */     return this.properties.getStringValue(this.sTemplateConfigID + name, params);
/*     */   }
/*     */   
/*     */   public SWTSkinProperties getOriginalProperties() {
/* 297 */     return this.properties;
/*     */   }
/*     */   
/*     */   public String[] getParamValues() {
/* 301 */     return this.sCloneParams;
/*     */   }
/*     */   
/*     */   public boolean getBooleanValue(String name, boolean def)
/*     */   {
/* 306 */     if (name == null) {
/* 307 */       return def;
/*     */     }
/*     */     
/* 310 */     checkName(name);
/*     */     
/* 312 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 313 */       return this.properties.getBooleanValue(name, def);
/*     */     }
/*     */     
/* 316 */     if ((!name.equals(".type")) && 
/* 317 */       (this.properties.getStringValue(this.sCloneConfigID + name) != null)) {
/* 318 */       return this.properties.getBooleanValue(this.sCloneConfigID + name, def);
/*     */     }
/*     */     
/* 321 */     return this.properties.getBooleanValue(this.sTemplateConfigID + name, def);
/*     */   }
/*     */   
/*     */   public void clearCache()
/*     */   {
/* 326 */     this.properties.clearCache();
/*     */   }
/*     */   
/*     */   public boolean hasKey(String name)
/*     */   {
/* 331 */     return this.properties.hasKey(name);
/*     */   }
/*     */   
/*     */   public Color getColor(String name, Color def) {
/* 335 */     Color color = getColor(name);
/* 336 */     if (color == null) {
/* 337 */       return def;
/*     */     }
/* 339 */     return color;
/*     */   }
/*     */   
/*     */   public int getEmHeightPX()
/*     */   {
/* 344 */     return this.properties.getEmHeightPX();
/*     */   }
/*     */   
/*     */   public int getPxValue(String name, int def)
/*     */   {
/* 349 */     String value = getStringValue(name, (String)null);
/* 350 */     if (value == null) {
/* 351 */       return def;
/*     */     }
/*     */     
/* 354 */     int result = def;
/*     */     try {
/* 356 */       if (value.endsWith("rem")) {
/* 357 */         float em = Float.parseFloat(value.substring(0, value.length() - 3));
/*     */         
/* 359 */         result = (int)(this.properties.getEmHeightPX() * em);
/*     */       } else {
/* 361 */         result = Integer.parseInt(value);
/* 362 */         result = Utils.adjustPXForDPI(result);
/*     */       }
/*     */     }
/*     */     catch (NumberFormatException e) {}
/*     */     
/*     */ 
/* 368 */     return result;
/*     */   }
/*     */   
/*     */   public String getReferenceID(String name)
/*     */   {
/* 373 */     if (name == null) {
/* 374 */       return null;
/*     */     }
/*     */     
/* 377 */     checkName(name);
/*     */     
/* 379 */     if ((name.length() > 0) && (name.charAt(0) != '.')) {
/* 380 */       return this.properties.getReferenceID(name);
/*     */     }
/*     */     
/* 383 */     if (!name.equals(".type")) {
/* 384 */       String val = this.properties.getReferenceID(this.sCloneConfigID + name);
/* 385 */       if (val != null) {
/* 386 */         return val;
/*     */       }
/*     */     }
/*     */     
/* 390 */     return this.properties.getReferenceID(this.sTemplateConfigID + name);
/*     */   }
/*     */   
/*     */   public void addResourceBundle(ResourceBundle subBundle, String skinPath)
/*     */   {
/* 395 */     this.properties.addResourceBundle(subBundle, skinPath);
/*     */   }
/*     */   
/*     */   public void addResourceBundle(ResourceBundle subBundle, String skinPath, ClassLoader loader)
/*     */   {
/* 400 */     this.properties.addResourceBundle(subBundle, skinPath, loader);
/*     */   }
/*     */   
/*     */   public ClassLoader getClassLoader() {
/* 404 */     return this.properties.getClassLoader();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinPropertiesClone.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */