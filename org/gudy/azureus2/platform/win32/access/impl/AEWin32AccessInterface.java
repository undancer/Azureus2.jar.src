/*     */ package org.gudy.azureus2.platform.win32.access.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.platform.win32.PlatformManagerImpl;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32AccessException;
/*     */ import org.gudy.azureus2.update.UpdaterUtils;
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
/*     */ public class AEWin32AccessInterface
/*     */ {
/*     */   public static final int HKEY_CLASSES_ROOT = 1;
/*     */   public static final int HKEY_CURRENT_CONFIG = 2;
/*     */   public static final int HKEY_LOCAL_MACHINE = 3;
/*     */   public static final int HKEY_CURRENT_USER = 4;
/*     */   public static final int WM_QUERYENDSESSION = 17;
/*     */   public static final int WM_ENDSESSION = 22;
/*     */   public static final int WM_POWERBROADCAST = 536;
/*     */   public static final int PBT_APMQUERYSUSPEND = 0;
/*     */   public static final int PBT_APMSUSPEND = 4;
/*     */   public static final int PBT_APMRESUMESUSPEND = 7;
/*     */   public static final long BROADCAST_QUERY_DENY = 1112363332L;
/*     */   public static final int ES_SYSTEM_REQUIRED = 1;
/*     */   public static final int ES_DISPLAY_REQUIRED = 2;
/*     */   public static final int ES_USER_PRESENT = 4;
/*     */   public static final int ES_AWAYMODE_REQUIRED = 64;
/*     */   public static final int ES_CONTINUOUS = Integer.MIN_VALUE;
/*     */   private static boolean enabled;
/*     */   private static boolean enabled_set;
/*     */   private static AEWin32AccessCallback cb;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  65 */       System.loadLibrary(PlatformManagerImpl.DLL_NAME);
/*     */     } catch (Throwable e) {
/*  67 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static boolean isEnabled(boolean check_if_disabled)
/*     */   {
/*  75 */     if (!check_if_disabled)
/*     */     {
/*  77 */       return true;
/*     */     }
/*     */     
/*  80 */     if (enabled_set)
/*     */     {
/*  82 */       return enabled;
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*  88 */       enabled = !UpdaterUtils.disableNativeCode(getVersion());
/*     */       
/*  90 */       if (!enabled)
/*     */       {
/*  92 */         System.err.println("Native code has been disabled");
/*     */       }
/*     */     }
/*     */     finally {
/*  96 */       enabled_set = true;
/*     */     }
/*     */     
/*  99 */     return enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void load(AEWin32AccessCallback _callback, boolean _fully_initialise)
/*     */   {
/* 107 */     cb = _callback;
/*     */     
/* 109 */     if (_fully_initialise) {
/*     */       try
/*     */       {
/* 112 */         initialise();
/*     */       }
/*     */       catch (Throwable e) {}
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
/*     */   public static long callback(int msg, int param1, long param2)
/*     */   {
/* 128 */     if (cb == null)
/*     */     {
/* 130 */       return -1L;
/*     */     }
/*     */     
/*     */ 
/* 134 */     return cb.windowsMessage(msg, param1, param2);
/*     */   }
/*     */   
/*     */   protected static native void initialise()
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   protected static native String getVersion();
/*     */   
/*     */   protected static native String readStringValue(int paramInt, String paramString1, String paramString2)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   protected static native void writeStringValue(int paramInt, String paramString1, String paramString2, String paramString3)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   protected static native int readWordValue(int paramInt, String paramString1, String paramString2)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   protected static native void writeWordValue(int paramInt1, String paramString1, String paramString2, int paramInt2)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   protected static native void deleteKey(int paramInt, String paramString, boolean paramBoolean)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   protected static native void deleteValue(int paramInt, String paramString1, String paramString2)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   public static native void createProcess(String paramString, boolean paramBoolean)
/*     */     throws AEWin32AccessException;
/*     */   
/*     */   public static native void moveToRecycleBin(String paramString)
/*     */     throws AEWin32AccessException;
/*     */   
/*     */   public static native void copyPermission(String paramString1, String paramString2)
/*     */     throws AEWin32AccessException;
/*     */   
/*     */   public static native boolean testNativeAvailability(String paramString)
/*     */     throws AEWin32AccessException;
/*     */   
/*     */   public static native void traceRoute(int paramInt1, int paramInt2, int paramInt3, int paramInt4, AEWin32AccessCallback paramAEWin32AccessCallback)
/*     */     throws AEWin32AccessException;
/*     */   
/*     */   public static native int shellExecute(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   public static native int shellExecuteAndWait(String paramString1, String paramString2)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   public static native List getAvailableDrives()
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   public static native Map getDriveInfo(char paramChar)
/*     */     throws AEWin32AccessExceptionImpl;
/*     */   
/*     */   public static native int setThreadExecutionState(int paramInt);
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/access/impl/AEWin32AccessInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */