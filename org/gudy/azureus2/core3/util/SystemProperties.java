/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtil;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
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
/*     */ public class SystemProperties
/*     */ {
/*  38 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String SYS_PROP_CONFIG_OVERRIDE = "azureus.config.path";
/*     */   
/*     */ 
/*     */ 
/*  46 */   public static final String SEP = System.getProperty("file.separator");
/*     */   
/*     */   public static final String AZ_APP_ID = "az";
/*     */   
/*  50 */   public static String APPLICATION_NAME = "Azureus";
/*  51 */   private static String APPLICATION_ID = "az";
/*  52 */   private static String APPLICATION_VERSION = "5.7.6.0";
/*     */   
/*     */ 
/*  55 */   private static String APPLICATION_ENTRY_POINT = "org.gudy.azureus2.ui.swt.Main";
/*     */   
/*     */   private static final String WIN_DEFAULT = "Application Data";
/*  58 */   private static final String OSX_DEFAULT = "Library" + SEP + "Application Support";
/*     */   
/*     */ 
/*  61 */   private static final boolean PORTABLE = System.getProperty("azureus.portable.root", "").length() > 0;
/*     */   
/*     */   private static String user_path;
/*     */   
/*     */   private static String app_path;
/*     */   
/*     */   public static void determineApplicationName()
/*     */   {
/*  69 */     String explicit_name = System.getProperty("azureus.app.name", null);
/*     */     
/*  71 */     if (explicit_name != null)
/*     */     {
/*  73 */       explicit_name = explicit_name.trim();
/*     */       
/*  75 */       if (explicit_name.length() > 0)
/*     */       {
/*  77 */         setApplicationName(explicit_name);
/*     */         
/*  79 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  86 */     if ((Constants.isOSX) && (!System.getProperty("azureus.infer.app.name", "true").equals("false")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  93 */       String classpath = System.getProperty("exe4j.moduleName", null);
/*  94 */       if ((classpath == null) || (!classpath.contains(".app")))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */         classpath = System.getProperty("java.class.path");
/*     */       }
/*     */       
/*     */ 
/* 107 */       if (classpath == null)
/*     */       {
/*     */ 
/*     */ 
/* 111 */         System.out.println("SystemProperties: determineApplicationName - class path is null");
/*     */       }
/*     */       else
/*     */       {
/* 115 */         int dot_pos = classpath.indexOf(".app");
/*     */         
/* 117 */         if (dot_pos == -1)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */           File fileUserDir = new File(System.getProperty("user.dir", ""));
/* 124 */           if (new File(fileUserDir, "Azureus2.jar").exists()) {
/* 125 */             setApplicationName(fileUserDir.getName());
/* 126 */             return;
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 134 */           int start_pos = dot_pos;
/*     */           
/* 136 */           while ((start_pos >= 0) && (classpath.charAt(start_pos) != '/'))
/*     */           {
/* 138 */             start_pos--;
/*     */           }
/*     */           
/* 141 */           String app_name = classpath.substring(start_pos + 1, dot_pos);
/*     */           
/* 143 */           setApplicationName(app_name);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setApplicationName(String name)
/*     */   {
/* 153 */     if ((name != null) && (name.trim().length() > 0))
/*     */     {
/* 155 */       name = name.trim();
/*     */       
/* 157 */       if (user_path != null)
/*     */       {
/* 159 */         if (!name.equals(APPLICATION_NAME))
/*     */         {
/* 161 */           System.out.println("**** SystemProperties::setApplicationName called too late! ****");
/*     */         }
/*     */       }
/*     */       
/* 165 */       APPLICATION_NAME = name;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setApplicationIdentifier(String application_id)
/*     */   {
/* 173 */     if ((application_id != null) && (application_id.trim().length() > 0))
/*     */     {
/* 175 */       APPLICATION_ID = application_id.trim();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setApplicationEntryPoint(String entry_point)
/*     */   {
/* 183 */     if ((entry_point != null) && (entry_point.trim().length() > 0))
/*     */     {
/* 185 */       APPLICATION_ENTRY_POINT = entry_point.trim();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getApplicationName()
/*     */   {
/* 192 */     return APPLICATION_NAME;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setApplicationVersion(String v)
/*     */   {
/* 199 */     APPLICATION_VERSION = v;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getApplicationVersion()
/*     */   {
/* 205 */     return APPLICATION_VERSION;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getApplicationIdentifier()
/*     */   {
/* 211 */     return APPLICATION_ID;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getApplicationEntryPoint()
/*     */   {
/* 217 */     return APPLICATION_ENTRY_POINT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setUserPath(String _path)
/*     */   {
/* 229 */     user_path = _path;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getUserPath()
/*     */   {
/* 241 */     if (user_path != null) {
/* 242 */       return user_path;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 253 */     String temp_user_path = System.getProperty("azureus.config.path");
/*     */     try
/*     */     {
/* 256 */       if (temp_user_path != null)
/*     */       {
/* 258 */         if (!temp_user_path.endsWith(SEP))
/*     */         {
/* 260 */           temp_user_path = temp_user_path + SEP;
/*     */         }
/*     */         
/* 263 */         File dir = new File(temp_user_path);
/*     */         
/* 265 */         if (!dir.exists()) {
/* 266 */           FileUtil.mkdirs(dir);
/*     */         }
/*     */         
/* 269 */         if (Logger.isEnabled()) {
/* 270 */           Logger.log(new LogEvent(LOGID, "SystemProperties::getUserPath(Custom): user_path = " + temp_user_path));
/*     */         }
/*     */         
/*     */ 
/* 274 */         return temp_user_path;
/*     */       }
/*     */       
/*     */       Object loc;
/*     */       try
/*     */       {
/* 280 */         PlatformManager platformManager = PlatformManagerFactory.getPlatformManager();
/*     */         
/* 282 */         loc = platformManager.getLocation(1L);
/*     */         
/* 284 */         if (loc != null) {
/* 285 */           temp_user_path = ((File)loc).getPath() + SEP;
/*     */           
/* 287 */           if (Logger.isEnabled()) {
/* 288 */             Logger.log(new LogEvent(LOGID, "SystemProperties::getUserPath: user_path = " + temp_user_path));
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 293 */         if (Logger.isEnabled()) {
/* 294 */           Logger.log(new LogEvent(LOGID, "Unable to retrieve user config path from the platform manager. Make sure aereg.dll is present."));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 302 */       if (temp_user_path == null) {
/* 303 */         String userhome = System.getProperty("user.home");
/*     */         
/* 305 */         if (Constants.isWindows) {
/* 306 */           temp_user_path = getEnvironmentalVariable("APPDATA");
/*     */           
/* 308 */           if ((temp_user_path != null) && (temp_user_path.length() > 0)) {
/* 309 */             if (Logger.isEnabled()) {
/* 310 */               Logger.log(new LogEvent(LOGID, "Using user config path from APPDATA env var instead: " + temp_user_path));
/*     */             }
/*     */           }
/*     */           else {
/* 314 */             temp_user_path = userhome + SEP + "Application Data";
/* 315 */             if (Logger.isEnabled()) {
/* 316 */               Logger.log(new LogEvent(LOGID, "Using user config path from java user.home var instead: " + temp_user_path));
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 321 */           temp_user_path = temp_user_path + SEP + APPLICATION_NAME + SEP;
/*     */           
/* 323 */           if (Logger.isEnabled()) {
/* 324 */             Logger.log(new LogEvent(LOGID, "SystemProperties::getUserPath(Win): user_path = " + temp_user_path));
/*     */           }
/*     */           
/*     */         }
/* 328 */         else if (Constants.isOSX) {
/* 329 */           temp_user_path = userhome + SEP + OSX_DEFAULT + SEP + APPLICATION_NAME + SEP;
/*     */           
/*     */ 
/* 332 */           if (Logger.isEnabled()) {
/* 333 */             Logger.log(new LogEvent(LOGID, "SystemProperties::getUserPath(Mac): user_path = " + temp_user_path));
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 339 */           temp_user_path = userhome + SEP + "." + APPLICATION_NAME.toLowerCase() + SEP;
/*     */           
/*     */ 
/* 342 */           if (Logger.isEnabled()) {
/* 343 */             Logger.log(new LogEvent(LOGID, "SystemProperties::getUserPath(Unix): user_path = " + temp_user_path));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 350 */       File dir = new File(temp_user_path);
/* 351 */       if (!dir.exists()) {
/* 352 */         FileUtil.mkdirs(dir);
/*     */       }
/*     */       
/* 355 */       return temp_user_path;
/*     */     }
/*     */     finally {
/* 358 */       user_path = temp_user_path;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getApplicationPath()
/*     */   {
/* 370 */     if (app_path != null)
/*     */     {
/* 372 */       return app_path;
/*     */     }
/*     */     
/* 375 */     String temp_app_path = System.getProperty("azureus.install.path", System.getProperty("user.dir"));
/*     */     
/* 377 */     if (!temp_app_path.endsWith(SEP))
/*     */     {
/* 379 */       temp_app_path = temp_app_path + SEP;
/*     */     }
/*     */     
/* 382 */     if (Constants.isOSX)
/*     */     {
/* 384 */       String appName = getApplicationName() + ".app/";
/* 385 */       if (temp_app_path.endsWith(appName)) {
/* 386 */         temp_app_path = temp_app_path.substring(0, temp_app_path.length() - appName.length());
/*     */       }
/*     */     }
/*     */     
/* 390 */     app_path = temp_app_path;
/*     */     
/* 392 */     return app_path;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isJavaWebStartInstance()
/*     */   {
/*     */     try
/*     */     {
/* 402 */       String java_ws_prop = System.getProperty("azureus.javaws");
/* 403 */       return (java_ws_prop != null) && (java_ws_prop.equals("true"));
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 407 */     return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getEnvironmentalVariable(String _var)
/*     */   {
/* 425 */     if (Constants.isWindows9598ME)
/*     */     {
/* 427 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 432 */     String res = System.getenv(_var);
/*     */     
/* 434 */     if (res != null)
/*     */     {
/* 436 */       return res;
/*     */     }
/*     */     
/* 439 */     Properties envVars = new Properties();
/* 440 */     BufferedReader br = null;
/*     */     
/*     */     try
/*     */     {
/* 444 */       Process p = null;
/* 445 */       Runtime r = Runtime.getRuntime();
/*     */       
/* 447 */       if (Constants.isWindows) {
/* 448 */         p = r.exec(new String[] { "cmd.exe", "/c", "set" });
/*     */       }
/*     */       else {
/* 451 */         p = r.exec("env");
/*     */       }
/*     */       
/* 454 */       String system_encoding = LocaleUtil.getSingleton().getSystemEncoding();
/*     */       
/* 456 */       if (Logger.isEnabled()) {
/* 457 */         Logger.log(new LogEvent(LOGID, "SystemProperties::getEnvironmentalVariable - " + _var + ", system encoding = " + system_encoding));
/*     */       }
/*     */       
/*     */ 
/* 461 */       br = new BufferedReader(new InputStreamReader(p.getInputStream(), system_encoding), 8192);
/*     */       String line;
/* 463 */       while ((line = br.readLine()) != null) {
/* 464 */         int idx = line.indexOf('=');
/* 465 */         if (idx >= 0) {
/* 466 */           String key = line.substring(0, idx);
/* 467 */           String value = line.substring(idx + 1);
/* 468 */           envVars.setProperty(key, value);
/*     */         }
/*     */       }
/* 471 */       br.close();
/*     */     }
/*     */     catch (Throwable t) {
/* 474 */       if (br != null) try { br.close();
/*     */         } catch (Exception ingore) {}
/*     */     }
/* 477 */     return envVars.getProperty(_var, "");
/*     */   }
/*     */   
/*     */   public static String getDocPath() {
/* 481 */     String explicit_dir = System.getProperty("azureus.doc.path", null);
/*     */     
/* 483 */     if (explicit_dir != null) {
/* 484 */       File temp = new File(explicit_dir);
/* 485 */       if (!temp.exists()) {
/* 486 */         if (!temp.mkdirs()) {
/* 487 */           System.err.println("Failed to create document dir: " + temp);
/*     */         }
/* 489 */       } else if ((!temp.isDirectory()) || (!temp.canWrite())) {
/* 490 */         System.err.println("Document dir is not a directory or not writable: " + temp);
/*     */       }
/* 492 */       return temp.getAbsolutePath();
/*     */     }
/* 494 */     if (PORTABLE)
/*     */     {
/* 496 */       return getUserPath();
/*     */     }
/*     */     
/* 499 */     File fDocPath = null;
/*     */     try {
/* 501 */       PlatformManager platformManager = PlatformManagerFactory.getPlatformManager();
/*     */       
/* 503 */       fDocPath = platformManager.getLocation(3L);
/*     */     }
/*     */     catch (Throwable e) {}
/* 506 */     if (fDocPath == null) {
/* 507 */       System.err.println("This is BAD - fix me!");
/* 508 */       new Throwable().printStackTrace();
/*     */       
/* 510 */       fDocPath = new File(getUserPath(), "Documents");
/*     */     }
/*     */     
/* 513 */     return fDocPath.getAbsolutePath();
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getAzureusJarPath()
/*     */   {
/* 519 */     String str = getApplicationPath();
/*     */     
/* 521 */     if ((Constants.isOSX) && (!new File(str, "Azureus2.jar").exists()))
/*     */     {
/* 523 */       str = str + getApplicationName() + ".app/Contents/Resources/Java/";
/*     */     }
/*     */     
/* 526 */     return str + "Azureus2.jar";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/SystemProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */