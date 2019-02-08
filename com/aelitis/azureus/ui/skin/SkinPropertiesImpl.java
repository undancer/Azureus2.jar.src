/*     */ package com.aelitis.azureus.ui.skin;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.internat.IntegratedResourceBundle;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class SkinPropertiesImpl
/*     */   implements SkinProperties
/*     */ {
/*  49 */   private static final LogIDs LOGID = LogIDs.UI3;
/*     */   
/*     */   public static final String PATH_SKIN_DEFS = "com/aelitis/azureus/ui/skin/";
/*     */   
/*     */   private static final String FILE_SKIN_DEFS = "skin3.properties";
/*     */   
/*  55 */   private static final Pattern PAT_PARAM_ALPHA = Pattern.compile("\\{([^0-9].+?)\\}");
/*     */   
/*  57 */   private static final Pattern PAT_PARAM_NUM = Pattern.compile("\\{([0-9]+?)\\}");
/*     */   
/*     */   private IntegratedResourceBundle rb;
/*     */   
/*     */   private final ClassLoader classLoader;
/*     */   
/*  63 */   private int emHeightPX = 15;
/*     */   
/*     */   public SkinPropertiesImpl() {
/*  66 */     this(SkinPropertiesImpl.class.getClassLoader(), "com/aelitis/azureus/ui/skin/", "skin3.properties");
/*     */   }
/*     */   
/*     */ 
/*     */   public SkinPropertiesImpl(ClassLoader classLoader, String skinPath, String mainSkinFile)
/*     */   {
/*  72 */     this.classLoader = classLoader;
/*  73 */     skinPath = skinPath.replaceAll("/", ".");
/*  74 */     if (!skinPath.endsWith(".")) {
/*  75 */       skinPath = skinPath + ".";
/*     */     }
/*  77 */     if (mainSkinFile.endsWith(".properties")) {
/*  78 */       mainSkinFile = mainSkinFile.substring(0, mainSkinFile.length() - 11);
/*     */     }
/*  80 */     ResourceBundle bundle = ResourceBundle.getBundle(skinPath + mainSkinFile, Locale.getDefault(), classLoader);
/*     */     
/*  82 */     this.rb = new IntegratedResourceBundle(bundle, Collections.EMPTY_MAP, 1200);
/*  83 */     this.rb.setUseNullList(true);
/*     */     
/*  85 */     String sFiles = this.rb.getString("skin.include", null);
/*  86 */     if (sFiles != null) {
/*  87 */       String[] sFilesArray = sFiles.split(",");
/*  88 */       for (int i = 0; i < sFilesArray.length; i++) {
/*  89 */         String sFile = skinPath + sFilesArray[i];
/*     */         
/*  91 */         sFile = sFile.replaceAll("/", ".");
/*     */         try {
/*  93 */           ResourceBundle subBundle = ResourceBundle.getBundle(sFile, Locale.getDefault(), classLoader);
/*     */           
/*  95 */           this.rb.addResourceMessages(subBundle);
/*     */         } catch (Throwable t) {
/*  97 */           Debug.out("Err loading skin include: " + sFile, t);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void addResourceBundle(ResourceBundle subBundle, String skinPath) {
/* 104 */     addResourceBundle(subBundle, skinPath, this.classLoader);
/*     */   }
/*     */   
/*     */   public void addResourceBundle(ResourceBundle subBundle, String skinPath, ClassLoader loader) {
/*     */     try {
/* 109 */       clearCache();
/* 110 */       this.rb.addResourceMessages(subBundle);
/*     */       try
/*     */       {
/* 113 */         String sFiles = subBundle.getString("skin.include");
/*     */         
/* 115 */         if ((sFiles != null) && (skinPath != null))
/*     */         {
/* 117 */           String[] sFilesArray = Constants.PAT_SPLIT_COMMA.split(sFiles);
/* 118 */           for (int i = 0; i < sFilesArray.length; i++) {
/* 119 */             String sFile = skinPath + sFilesArray[i];
/*     */             
/* 121 */             sFile = sFile.replaceAll("/", ".");
/*     */             try {
/* 123 */               ResourceBundle incBundle = ResourceBundle.getBundle(sFile, Locale.getDefault(), loader);
/*     */               
/* 125 */               this.rb.addResourceMessages(incBundle);
/*     */             } catch (Throwable t) {
/* 127 */               Debug.out("Err loading skin include: " + sFile, t);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (MissingResourceException e) {}
/*     */     }
/*     */     catch (Throwable t) {
/* 135 */       Debug.out("Err loading skin include: " + subBundle, t);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addProperty(String name, String value)
/*     */   {
/* 144 */     this.rb.addString(name, value);
/*     */   }
/*     */   
/*     */   public boolean hasKey(String name) {
/* 148 */     if (name == null) {
/* 149 */       return false;
/*     */     }
/*     */     
/* 152 */     String osName = null;
/* 153 */     if (Constants.isWindows) {
/* 154 */       osName = name + "._windows";
/* 155 */     } else if (Constants.isOSX) {
/* 156 */       osName = name + "._mac";
/* 157 */     } else if (Constants.isUnix) {
/* 158 */       osName = name + "._unix";
/* 159 */     } else if (Constants.isFreeBSD) {
/* 160 */       osName = name + "._freebsd";
/* 161 */     } else if (Constants.isLinux) {
/* 162 */       osName = name + "._linux";
/* 163 */     } else if (Constants.isSolaris) {
/* 164 */       osName = name + "._solaris";
/*     */     }
/*     */     
/* 167 */     boolean contains = false;
/* 168 */     if (osName != null)
/*     */     {
/* 170 */       contains = this.rb.getString(osName, null) != null;
/*     */     }
/*     */     
/* 173 */     if (!contains) {
/* 174 */       contains = this.rb.getString(name, null) != null;
/*     */     }
/* 176 */     return contains;
/*     */   }
/*     */   
/*     */   public String getReferenceID(String name)
/*     */   {
/* 181 */     String value = getValue(name, null, false);
/* 182 */     if ((value == null) || (value.length() < 2)) {
/* 183 */       return null;
/*     */     }
/* 185 */     if ((value.charAt(0) == '{') && (value.charAt(value.length() - 1) == '}')) {
/* 186 */       return value.substring(1, value.length() - 1);
/*     */     }
/* 188 */     return null;
/*     */   }
/*     */   
/*     */   protected String getValue(String name, String[] params) {
/* 192 */     return getValue(name, params, true);
/*     */   }
/*     */   
/*     */   private String getValue(String name, String[] params, boolean expandReferences) {
/* 196 */     String value = null;
/* 197 */     String osName = null;
/*     */     
/* 199 */     if (name == null) {
/* 200 */       return null;
/*     */     }
/*     */     
/* 203 */     if (Constants.isWindows) {
/* 204 */       osName = name + "._windows";
/* 205 */     } else if (Constants.isOSX) {
/* 206 */       osName = name + "._mac";
/* 207 */     } else if (Constants.isUnix) {
/* 208 */       osName = name + "._unix";
/* 209 */     } else if (Constants.isFreeBSD) {
/* 210 */       osName = name + "._freebsd";
/* 211 */     } else if (Constants.isLinux) {
/* 212 */       osName = name + "._linux";
/* 213 */     } else if (Constants.isSolaris) {
/* 214 */       osName = name + "._solaris";
/*     */     }
/*     */     
/* 217 */     if (osName != null) {
/* 218 */       value = this.rb.getString(osName, null);
/*     */     }
/*     */     
/* 221 */     if (value == null) {
/* 222 */       value = this.rb.getString(name, null);
/*     */     }
/*     */     
/* 225 */     if ((expandReferences) && (value != null) && (value.indexOf('}') > 0))
/*     */     {
/*     */ 
/* 228 */       if (params != null) {
/* 229 */         Matcher matcher = PAT_PARAM_NUM.matcher(value);
/* 230 */         while (matcher.find()) {
/* 231 */           String key = matcher.group(1);
/*     */           try {
/* 233 */             int i = Integer.parseInt(key);
/*     */             
/* 235 */             if (i < params.length) {
/* 236 */               value = value.replaceAll("\\Q{" + key + "}\\E", params[i]);
/*     */             } else {
/* 238 */               value = value.replaceAll("\\Q{" + key + "}\\E", "");
/*     */             }
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */       }
/*     */       
/* 245 */       Matcher matcher = PAT_PARAM_ALPHA.matcher(value);
/* 246 */       while (matcher.find()) {
/* 247 */         String key = matcher.group(1);
/* 248 */         String text = getValue(key, params);
/* 249 */         if (text == null) {
/* 250 */           text = MessageText.getString(key);
/*     */         }
/* 252 */         value = value.replaceAll("\\Q{" + key + "}\\E", text);
/*     */       }
/*     */     }
/*     */     
/* 256 */     return value;
/*     */   }
/*     */   
/*     */   public int getIntValue(String name, int def) {
/* 260 */     String value = getValue(name, null);
/* 261 */     if (value == null) {
/* 262 */       return def;
/*     */     }
/*     */     
/* 265 */     int result = def;
/*     */     try {
/* 267 */       if (value.endsWith("rem")) {
/* 268 */         float em = Float.parseFloat(value.substring(0, value.length() - 3));
/*     */         
/* 270 */         result = (int)(this.emHeightPX * em);
/*     */       } else {
/* 272 */         result = Integer.parseInt(value);
/*     */       }
/*     */     }
/*     */     catch (NumberFormatException e) {}
/*     */     
/*     */ 
/* 278 */     return result;
/*     */   }
/*     */   
/*     */   public int[] getColorValue(String name) {
/* 282 */     int[] colors = new int[4];
/* 283 */     String value = getValue(name, null);
/*     */     
/* 285 */     if ((value == null) || (value.length() == 0) || (value.startsWith("COLOR_"))) {
/* 286 */       colors[0] = (colors[1] = colors[2] = -1);
/* 287 */       return colors;
/*     */     }
/*     */     try
/*     */     {
/* 291 */       if (value.charAt(0) == '#')
/*     */       {
/* 293 */         long l = Long.parseLong(value.substring(1), 16);
/* 294 */         if (value.length() == 9) {
/* 295 */           colors = new int[] { (int)(l >> 24 & 0xFF), (int)(l >> 16 & 0xFF), (int)(l >> 8 & 0xFF), (int)(l & 0xFF) };
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 302 */           colors[0] = ((int)(l >> 16 & 0xFF));
/* 303 */           colors[1] = ((int)(l >> 8 & 0xFF));
/* 304 */           colors[2] = ((int)(l & 0xFF));
/* 305 */           colors[3] = 255;
/*     */         }
/* 307 */       } else if (value.contains(",")) {
/* 308 */         StringTokenizer st = new StringTokenizer(value, ",");
/* 309 */         colors[0] = Integer.parseInt(st.nextToken());
/* 310 */         colors[1] = Integer.parseInt(st.nextToken());
/* 311 */         colors[2] = Integer.parseInt(st.nextToken());
/* 312 */         colors[3] = (st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 255);
/*     */       } else {
/* 314 */         colors[0] = (colors[1] = colors[2] = -1);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 318 */       colors[0] = (colors[1] = colors[2] = -1);
/*     */     }
/*     */     
/* 321 */     return colors;
/*     */   }
/*     */   
/*     */   public String getStringValue(String name) {
/* 325 */     return getStringValue(name, (String[])null);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String def) {
/* 329 */     return getStringValue(name, (String[])null, def);
/*     */   }
/*     */   
/*     */   public String[] getStringArray(String name) {
/* 333 */     return getStringArray(name, (String[])null);
/*     */   }
/*     */   
/*     */   public String[] getStringArray(String name, String[] params) {
/* 337 */     String s = getValue(name, params);
/* 338 */     if (s == null) {
/* 339 */       return null;
/*     */     }
/*     */     
/* 342 */     String[] values = Constants.PAT_SPLIT_COMMAWORDS.split(s);
/* 343 */     if (values == null) {
/* 344 */       return new String[] { s };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 349 */     return values;
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String[] params) {
/* 353 */     return getValue(name, params);
/*     */   }
/*     */   
/*     */   public String getStringValue(String name, String[] params, String def) {
/* 357 */     String s = getValue(name, params);
/* 358 */     return s == null ? def : s;
/*     */   }
/*     */   
/*     */   public boolean getBooleanValue(String name, boolean def)
/*     */   {
/* 363 */     String s = getStringValue(name, (String)null);
/* 364 */     if (s == null) {
/* 365 */       return def;
/*     */     }
/* 367 */     return (s.toLowerCase().equals("true")) || (s.equals("1"));
/*     */   }
/*     */   
/*     */   public void clearCache() {
/* 371 */     this.rb.clearUsedMessagesMap(1);
/*     */   }
/*     */   
/*     */   public ClassLoader getClassLoader() {
/* 375 */     return this.classLoader;
/*     */   }
/*     */   
/*     */   protected void setEmHeightPX(int fontHeightInPX) {
/* 379 */     this.emHeightPX = fontHeightInPX;
/*     */   }
/*     */   
/*     */   public int getEmHeightPX() {
/* 383 */     return this.emHeightPX;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/skin/SkinPropertiesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */