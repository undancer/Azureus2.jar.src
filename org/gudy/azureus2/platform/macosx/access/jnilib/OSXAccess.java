/*     */ package org.gudy.azureus2.platform.macosx.access.jnilib;
/*     */ 
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectedInfo;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetector;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectorFactory;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ public class OSXAccess
/*     */ {
/*  37 */   private static boolean bLoaded = false;
/*     */   
/*  39 */   private static boolean DEBUG = false;
/*     */   
/*  41 */   private static List<String> parameters = new ArrayList(1);
/*     */   
/*     */   static {
/*  44 */     if ((!Constants.isOSX_10_5_OrHigher) || (!loadLibrary("OSXAccess_10.5"))) {
/*  45 */       loadLibrary("OSXAccess");
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean loadLibrary(String lib)
/*     */   {
/*     */     try {
/*  52 */       SystemLoadLibrary(lib);
/*     */       
/*  54 */       bLoaded = true;
/*  55 */       initialize();
/*     */     } catch (Throwable e1) {
/*  57 */       System.err.println("Could not find lib" + lib + ".jnilib; " + e1.toString());
/*     */     }
/*     */     
/*  60 */     return bLoaded;
/*     */   }
/*     */   
/*     */   private static void SystemLoadLibrary(String lib) throws Throwable {
/*     */     try {
/*  65 */       System.loadLibrary(lib);
/*     */     }
/*     */     catch (Throwable t) {
/*     */       try {
/*  69 */         File f = new File("Azureus.app/Contents/Resources/Java/dll/lib" + lib + ".jnilib");
/*  70 */         System.load(f.getAbsolutePath());
/*     */       } catch (Throwable t2) {
/*  72 */         throw t;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void passParameter(String s) {
/*  78 */     if (DEBUG) {
/*  79 */       System.err.println("passing Parameter " + s);
/*     */     }
/*  81 */     if (s != null) {
/*  82 */       parameters.add(s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static String[] runLight(String[] args)
/*     */   {
/*  89 */     if (args != null) {
/*  90 */       Collections.addAll(parameters, args);
/*     */     }
/*  92 */     return (String[])parameters.toArray(new String[0]);
/*     */   }
/*     */   
/*     */   private static void initialize()
/*     */   {
/*     */     try {
/*  98 */       if (System.getProperty("osxaccess.light", "0").equals("1")) {
/*  99 */         initializeLight();
/*     */         
/* 101 */         Class<?> claOSXFileOpen = Class.forName("org.gudy.azureus2.ui.swt.osx.OSXFileOpen");
/* 102 */         if (claOSXFileOpen != null) {
/* 103 */           Method method = claOSXFileOpen.getMethod("initLight", new Class[0]);
/* 104 */           method.invoke(null, new Object[0]);
/*     */         }
/* 106 */         return;
/*     */       }
/*     */       
/* 109 */       initializeDriveDetection(new OSXDriveDetectListener() {
/*     */         public void driveRemoved(File mount, Map driveInfo) {
/* 111 */           if (OSXAccess.DEBUG) {
/* 112 */             System.out.println("UNMounted " + mount);
/* 113 */             for (Object key : driveInfo.keySet()) {
/* 114 */               Object val = driveInfo.get(key);
/* 115 */               System.out.println("\t" + key + "\t:\t" + val);
/*     */             }
/*     */           }
/* 118 */           DriveDetectorFactory.getDeviceDetector().driveRemoved(mount);
/*     */         }
/*     */         
/*     */         public void driveDetected(File mount, Map driveInfo) {
/* 122 */           if (OSXAccess.DEBUG) {
/* 123 */             System.out.println("Mounted " + mount);
/* 124 */             for (Object key : driveInfo.keySet()) {
/* 125 */               Object val = driveInfo.get(key);
/* 126 */               System.out.println("\t" + key + "\t:\t" + val);
/*     */             }
/*     */           }
/*     */           
/* 130 */           boolean isOptical = MapUtils.getMapLong(driveInfo, "isOptical", 0L) != 0L;
/* 131 */           boolean isRemovable = MapUtils.getMapLong(driveInfo, "Removable", 0L) != 0L;
/* 132 */           boolean isWritable = MapUtils.getMapLong(driveInfo, "Writable", 0L) != 0L;
/*     */           
/* 134 */           boolean isWritableUSB = (isRemovable) && (isWritable) && (!isOptical);
/* 135 */           driveInfo.put("isWritableUSB", Boolean.valueOf(isWritableUSB));
/*     */           
/* 137 */           DriveDetectorFactory.getDeviceDetector().driveDetected(mount, driveInfo);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable t) {}
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
/*     */   public static boolean isLoaded()
/*     */   {
/* 180 */     return bLoaded;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 184 */     DriveDetectedInfo[] infos = DriveDetectorFactory.getDeviceDetector().getDetectedDriveInfo();
/* 185 */     Map<String, Object> infoMap; for (DriveDetectedInfo info : infos) {
/* 186 */       System.out.println(info.getLocation());
/*     */       
/* 188 */       infoMap = info.getInfoMap();
/* 189 */       for (String key : infoMap.keySet()) {
/* 190 */         Object val = infoMap.get(key);
/* 191 */         System.out.println("\t" + key + ": " + val);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static final native int AEGetParamDesc(int paramInt1, int paramInt2, int paramInt3, Object paramObject);
/*     */   
/*     */   public static final native String getVersion();
/*     */   
/*     */   public static final native String getDocDir();
/*     */   
/*     */   public static final native void memmove(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */   
/*     */   public static final native void initializeDriveDetection(OSXDriveDetectListener paramOSXDriveDetectListener);
/*     */   
/*     */   public static final native void initializeLight();
/*     */   
/*     */   public static final native boolean setDefaultAppForExt(String paramString1, String paramString2);
/*     */   
/*     */   public static final native boolean setDefaultAppForMime(String paramString1, String paramString2);
/*     */   
/*     */   public static final native boolean setDefaultAppForScheme(String paramString1, String paramString2);
/*     */   
/*     */   public static final native String getDefaultAppForExt(String paramString);
/*     */   
/*     */   public static final native String getDefaultAppForMime(String paramString);
/*     */   
/*     */   public static final native String getDefaultAppForScheme(String paramString);
/*     */   
/*     */   public static final native boolean canSetDefaultApp();
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/macosx/access/jnilib/OSXAccess.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */