/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.util.ResourceBundle;
/*     */ import org.eclipse.swt.graphics.Color;
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
/*     */ public class SWTSkinPropertiesParamImpl
/*     */   implements SWTSkinPropertiesParam
/*     */ {
/*     */   private final SWTSkinProperties properties;
/*     */   private final String[] sCloneParams;
/*     */   
/*     */   public SWTSkinPropertiesParamImpl(SWTSkinProperties properties, String[] sCloneParams)
/*     */   {
/*  43 */     this.properties = properties;
/*  44 */     this.sCloneParams = sCloneParams;
/*     */   }
/*     */   
/*     */   public void addProperty(String name, String value) {
/*  48 */     this.properties.addProperty(name, value);
/*     */   }
/*     */   
/*     */   public Color getColor(String name) {
/*  52 */     return this.properties.getColor(name);
/*     */   }
/*     */   
/*     */   public SWTColorWithAlpha getColorWithAlpha(String sID) {
/*  56 */     return this.properties.getColorWithAlpha(sID);
/*     */   }
/*     */   
/*     */   public int[] getColorValue(String name) {
/*  60 */     return this.properties.getColorValue(name);
/*     */   }
/*     */   
/*     */   public int getIntValue(String name, int def) {
/*  64 */     return this.properties.getIntValue(name, def);
/*     */   }
/*     */   
/*     */   public String[] getStringArray(String name, String[] params) {
/*  68 */     return this.properties.getStringArray(name, params);
/*     */   }
/*     */   
/*     */   public String[] getStringArray(String name) {
/*  72 */     return this.properties.getStringArray(name, this.sCloneParams);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String def) {
/*  76 */     return this.properties.getStringValue(name, this.sCloneParams, def);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String[] params, String def) {
/*  80 */     return this.properties.getStringValue(name, params, def);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String[] params) {
/*  84 */     return this.properties.getStringValue(name, params);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name) {
/*  88 */     return this.properties.getStringValue(name, this.sCloneParams);
/*     */   }
/*     */   
/*     */   public boolean getBooleanValue(String name, boolean def)
/*     */   {
/*  93 */     return this.properties.getBooleanValue(name, def);
/*     */   }
/*     */   
/*     */   public String[] getParamValues() {
/*  97 */     return this.sCloneParams;
/*     */   }
/*     */   
/*     */   public void clearCache() {
/* 101 */     this.properties.clearCache();
/*     */   }
/*     */   
/*     */   public boolean hasKey(String name)
/*     */   {
/* 106 */     return this.properties.hasKey(name);
/*     */   }
/*     */   
/*     */   public Color getColor(String name, Color def) {
/* 110 */     Color color = getColor(name);
/* 111 */     if (color == null) {
/* 112 */       return def;
/*     */     }
/* 114 */     return color;
/*     */   }
/*     */   
/*     */   public int getEmHeightPX()
/*     */   {
/* 119 */     return this.properties.getEmHeightPX();
/*     */   }
/*     */   
/*     */   public int getPxValue(String name, int def)
/*     */   {
/* 124 */     return this.properties.getPxValue(name, def);
/*     */   }
/*     */   
/*     */   public String getReferenceID(String name)
/*     */   {
/* 129 */     return this.properties.getReferenceID(name);
/*     */   }
/*     */   
/*     */   public void addResourceBundle(ResourceBundle subBundle, String skinPath)
/*     */   {
/* 134 */     this.properties.addResourceBundle(subBundle, skinPath);
/*     */   }
/*     */   
/*     */   public void addResourceBundle(ResourceBundle subBundle, String skinPath, ClassLoader loader)
/*     */   {
/* 139 */     this.properties.addResourceBundle(subBundle, skinPath, loader);
/*     */   }
/*     */   
/*     */   public ClassLoader getClassLoader() {
/* 143 */     return this.properties.getClassLoader();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinPropertiesParamImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */