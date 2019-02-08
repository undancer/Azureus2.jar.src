/*     */ package org.gudy.azureus2.ui.swt.win32;
/*     */ 
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectedInfo;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetector;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectorFactory;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32Access;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32Manager;
/*     */ import org.gudy.azureus2.platform.win32.access.impl.AEWin32AccessInterface;
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
/*     */ public class Win32UIEnhancer
/*     */ {
/*     */   public static final boolean DEBUG = false;
/*     */   public static final int SHGFI_ICON = 256;
/*     */   public static final int SHGFI_SMALLICON = 1;
/*     */   public static final int SHGFI_USEFILEATTRIBUTES = 16;
/*     */   public static final int SHGFI_LARGEICON = 2;
/*     */   public static final int WM_DEVICECHANGE = 537;
/*     */   public static final int DBT_DEVICEARRIVAL = 32768;
/*     */   public static final int DBT_DEVICEREMOVECOMPLETE = 32772;
/*     */   public static final int DBT_DEVTYP_VOLUME = 2;
/*     */   public static final int FILE_ATTRIBUTE_NORMAL = 128;
/*     */   private static int messageProcInt;
/*     */   private static long messageProcLong;
/*     */   private static Object messageCallback;
/*     */   private static DriveDetectedInfo loc;
/*     */   private static Class<?> claOS;
/*     */   private static boolean useLong;
/*     */   private static Class<?> claCallback;
/*     */   private static Constructor<?> constCallBack;
/*     */   private static Method mCallback_getAddress;
/*     */   private static Method mSetWindowLongPtr;
/*     */   private static int OS_GWLP_WNDPROC;
/*     */   private static Method mOS_memmove_byte;
/*     */   private static Method mOS_memmove_int;
/*     */   private static boolean isUnicode;
/*     */   private static Class<?> claSHFILEINFO;
/*     */   private static Class<?> claSHFILEINFOA;
/*     */   private static Class<?> claSHFILEINFOW;
/*     */   private static Class<?> claTCHAR;
/*     */   private static Method mSHGetFileInfo;
/*     */   private static Method mImage_win32_new;
/*     */   private static Constructor<?> constTCHAR3;
/*     */   private static int SHFILEINFO_sizeof;
/*     */   private static long oldProc;
/*     */   private static Method mGetWindowLongPtr;
/*     */   private static Method mCallWindowProc;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 121 */       claOS = Class.forName("org.eclipse.swt.internal.win32.OS");
/*     */       
/* 123 */       isUnicode = claOS.getDeclaredField("IsUnicode").getBoolean(null);
/*     */       
/* 125 */       claSHFILEINFO = Class.forName("org.eclipse.swt.internal.win32.SHFILEINFO");
/*     */       
/* 127 */       SHFILEINFO_sizeof = claSHFILEINFO.getField("sizeof").getInt(null);
/*     */       
/* 129 */       claSHFILEINFOA = Class.forName("org.eclipse.swt.internal.win32.SHFILEINFOA");
/* 130 */       claSHFILEINFOW = Class.forName("org.eclipse.swt.internal.win32.SHFILEINFOW");
/*     */       
/* 132 */       claTCHAR = Class.forName("org.eclipse.swt.internal.win32.TCHAR");
/*     */       
/*     */ 
/* 135 */       constTCHAR3 = claTCHAR.getConstructor(new Class[] { Integer.TYPE, String.class, Boolean.TYPE });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */       mSHGetFileInfo = claOS.getMethod("SHGetFileInfo", new Class[] { claTCHAR, Integer.TYPE, claSHFILEINFO, Integer.TYPE, Integer.TYPE });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 152 */       claCallback = Class.forName("org.eclipse.swt.internal.Callback");
/* 153 */       constCallBack = claCallback.getDeclaredConstructor(new Class[] { Object.class, String.class, Integer.TYPE });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */       mCallback_getAddress = claCallback.getDeclaredMethod("getAddress", new Class[0]);
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 164 */         mSetWindowLongPtr = claOS.getMethod("SetWindowLongPtr", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */         mGetWindowLongPtr = claOS.getMethod("GetWindowLongPtr", new Class[] { Integer.TYPE, Integer.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 179 */         mCallWindowProc = claOS.getMethod("CallWindowProc", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 188 */         useLong = false;
/*     */         
/* 190 */         mOS_memmove_byte = claOS.getMethod("memmove", new Class[] { byte[].class, Integer.TYPE, Integer.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 195 */         mOS_memmove_int = claOS.getMethod("memmove", new Class[] { int[].class, Integer.TYPE, Integer.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 201 */         mImage_win32_new = Image.class.getMethod("win32_new", new Class[] { Device.class, Integer.TYPE, Integer.TYPE });
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */ 
/* 208 */         mSetWindowLongPtr = claOS.getMethod("SetWindowLongPtr", new Class[] { Long.TYPE, Integer.TYPE, Long.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */         mGetWindowLongPtr = claOS.getMethod("GetWindowLongPtr", new Class[] { Long.TYPE, Integer.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 220 */         mCallWindowProc = claOS.getMethod("CallWindowProc", new Class[] { Long.TYPE, Long.TYPE, Integer.TYPE, Long.TYPE, Long.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 229 */         useLong = true;
/* 230 */         mOS_memmove_byte = claOS.getMethod("memmove", new Class[] { byte[].class, Long.TYPE, Long.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 235 */         mOS_memmove_int = claOS.getMethod("memmove", new Class[] { int[].class, Long.TYPE, Long.TYPE });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 241 */         mImage_win32_new = Image.class.getMethod("win32_new", new Class[] { Device.class, Integer.TYPE, Long.TYPE });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 249 */       OS_GWLP_WNDPROC = ((Integer)claOS.getField("GWLP_WNDPROC").get(null)).intValue();
/*     */     } catch (Throwable e) {
/* 251 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static Image getFileIcon(File file, boolean big) {
/*     */     try {
/* 257 */       int flags = 256;
/* 258 */       flags |= (big ? 2 : 1);
/* 259 */       if (!file.exists())
/* 260 */         flags |= 0x10;
/*     */       Object shfi;
/*     */       Object shfi;
/* 263 */       if (isUnicode) {
/* 264 */         shfi = claSHFILEINFOW.newInstance();
/*     */       } else {
/* 266 */         shfi = claSHFILEINFOA.newInstance();
/*     */       }
/* 268 */       Object pszPath = constTCHAR3.newInstance(new Object[] { Integer.valueOf(0), file.getAbsolutePath(), Boolean.valueOf(true) });
/*     */       
/* 270 */       mSHGetFileInfo.invoke(null, new Object[] { pszPath, Integer.valueOf(file.isDirectory() ? 16 : 128), shfi, Integer.valueOf(SHFILEINFO_sizeof), Integer.valueOf(flags) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 276 */       Field fldHIcon = claSHFILEINFO.getField("hIcon");
/* 277 */       if (fldHIcon.getLong(shfi) == 0L) {
/* 278 */         return null;
/*     */       }
/* 280 */       Image image = null;
/* 281 */       if (useLong) {
/* 282 */         image = (Image)mImage_win32_new.invoke(null, new Object[] { null, Integer.valueOf(1), Long.valueOf(fldHIcon.getLong(shfi)) });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 288 */       return (Image)mImage_win32_new.invoke(null, new Object[] { null, Integer.valueOf(1), Integer.valueOf(fldHIcon.getInt(shfi)) });
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 297 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void initMainShell(Shell shell)
/*     */   {
/* 304 */     Shell subshell = new Shell(shell);
/*     */     try
/*     */     {
/* 307 */       messageCallback = constCallBack.newInstance(new Object[] { Win32UIEnhancer.class, "messageProc2", Integer.valueOf(4) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 314 */       Object oHandle = subshell.getClass().getField("handle").get(subshell);
/* 315 */       oldProc = ((Number)mGetWindowLongPtr.invoke(null, new Object[] { oHandle, Integer.valueOf(OS_GWLP_WNDPROC) })).longValue();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 320 */       if (useLong) {
/* 321 */         Number n = (Number)mCallback_getAddress.invoke(messageCallback, new Object[0]);
/*     */         
/* 323 */         messageProcLong = n.longValue();
/* 324 */         if (messageProcLong != 0L) {
/* 325 */           mSetWindowLongPtr.invoke(null, new Object[] { oHandle, Integer.valueOf(OS_GWLP_WNDPROC), Long.valueOf(messageProcLong) });
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 332 */         Number n = (Number)mCallback_getAddress.invoke(messageCallback, new Object[0]);
/*     */         
/* 334 */         messageProcInt = n.intValue();
/* 335 */         if (messageProcInt != 0) {
/* 336 */           mSetWindowLongPtr.invoke(null, new Object[] { oHandle, Integer.valueOf(OS_GWLP_WNDPROC), Integer.valueOf(messageProcInt) });
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 344 */       ex.printStackTrace();
/*     */     }
/*     */     
/* 347 */     new AEThread2("Async:USB")
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 352 */         if (Constants.isWindows7OrHigher)
/*     */         {
/* 354 */           String version = AEWin32Manager.getAccessor(false).getVersion();
/*     */           
/* 356 */           if (Constants.compareVersions("1.21", version) > 0)
/*     */           {
/*     */ 
/*     */ 
/* 360 */             return;
/*     */           }
/*     */         }
/*     */         
/* 364 */         Map<File, Map> drives = AEWin32Manager.getAccessor(false).getAllDrives();
/* 365 */         if (drives != null) {
/* 366 */           for (File file : drives.keySet()) {
/* 367 */             Map driveInfo = (Map)drives.get(file);
/* 368 */             boolean isWritableUSB = AEWin32Manager.getAccessor(false).isUSBDrive(driveInfo);
/* 369 */             driveInfo.put("isWritableUSB", Boolean.valueOf(isWritableUSB));
/* 370 */             DriveDetectorFactory.getDeviceDetector().driveDetected(file, driveInfo);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */   static int messageProc2(int hwnd, int msg, int wParam, int lParam)
/*     */   {
/* 379 */     return (int)messageProc2(hwnd, msg, wParam, lParam);
/*     */   }
/*     */   
/*     */   static long messageProc2(long hwnd, long msg, long wParam, long lParam)
/*     */   {
/*     */     try
/*     */     {
/* 386 */       switch ((int)msg) {
/*     */       case 537: 
/* 388 */         if (wParam == 32768L) {
/* 389 */           int[] st = new int[3];
/* 390 */           if (useLong) {
/* 391 */             mOS_memmove_int.invoke(null, new Object[] { st, Long.valueOf(lParam), Long.valueOf(12L) });
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/* 397 */             mOS_memmove_int.invoke(null, new Object[] { st, Integer.valueOf((int)lParam), Integer.valueOf(12) });
/*     */           }
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
/* 409 */           if (st[1] == 2)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 414 */             byte[] b = new byte[st[0]];
/*     */             
/* 416 */             if (useLong) {
/* 417 */               mOS_memmove_byte.invoke(null, new Object[] { b, Long.valueOf(lParam), Integer.valueOf(st[0]) });
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 423 */               mOS_memmove_byte.invoke(null, new Object[] { b, Integer.valueOf((int)lParam), Integer.valueOf(st[0]) });
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 429 */             long unitMask = (b[12] & 0xFF) + ((b[13] & 0xFF) << 8) + ((b[14] & 0xFF) << 16) + ((b[15] & 0x3) << 24);
/*     */             
/* 431 */             char letter = '?';
/* 432 */             for (int i = 0; i < 26; i++) {
/* 433 */               if ((1 << i & unitMask) > 0L) {
/* 434 */                 letter = (char)(65 + i);
/*     */                 
/*     */ 
/*     */ 
/* 438 */                 Map driveInfo = AEWin32AccessInterface.getDriveInfo(letter);
/* 439 */                 boolean isWritableUSB = AEWin32Manager.getAccessor(false).isUSBDrive(driveInfo);
/* 440 */                 driveInfo.put("isWritableUSB", Boolean.valueOf(isWritableUSB));
/* 441 */                 DriveDetector driveDetector = DriveDetectorFactory.getDeviceDetector();
/* 442 */                 driveDetector.driveDetected(new File(letter + ":\\"), driveInfo);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 447 */         else if (wParam == 32772L) {
/* 448 */           int[] st = new int[3];
/* 449 */           if (useLong) {
/* 450 */             mOS_memmove_int.invoke(null, new Object[] { st, Long.valueOf(lParam), Long.valueOf(12L) });
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/* 456 */             mOS_memmove_int.invoke(null, new Object[] { st, Integer.valueOf((int)lParam), Integer.valueOf(12) });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 467 */           if (st[1] == 2)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 472 */             byte[] b = new byte[st[0]];
/* 473 */             if (useLong) {
/* 474 */               mOS_memmove_byte.invoke(null, new Object[] { b, Long.valueOf(lParam), Integer.valueOf(st[0]) });
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 480 */               mOS_memmove_byte.invoke(null, new Object[] { b, Integer.valueOf((int)lParam), Integer.valueOf(st[0]) });
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 486 */             long unitMask = (b[12] & 0xFF) + ((b[13] & 0xFF) << 8) + ((b[14] & 0xFF) << 16) + ((b[15] & 0x3) << 24);
/*     */             
/* 488 */             char letter = '?';
/* 489 */             DriveDetector driveDetector = DriveDetectorFactory.getDeviceDetector();
/* 490 */             for (int i = 0; i < 26; i++) {
/* 491 */               if ((1 << i & unitMask) > 0L) {
/* 492 */                 letter = (char)(65 + i);
/*     */                 
/*     */ 
/*     */ 
/* 496 */                 driveDetector.driveRemoved(new File(letter + ":\\"));
/*     */               }
/*     */             }
/*     */             
/* 500 */             Map<File, Map> drives = AEWin32Manager.getAccessor(false).getAllDrives();
/* 501 */             if (drives != null) {
/* 502 */               DriveDetectedInfo[] existingDrives = driveDetector.getDetectedDriveInfo();
/* 503 */               for (DriveDetectedInfo existingDrive : existingDrives) {
/* 504 */                 File existingDriveFile = existingDrive.getLocation();
/* 505 */                 boolean found = drives.containsKey(existingDriveFile);
/* 506 */                 if (!found)
/*     */                 {
/*     */ 
/*     */ 
/* 510 */                   driveDetector.driveRemoved(existingDriveFile);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       }
/*     */       
/*     */       
/*     */ 
/* 524 */       if (useLong) {
/* 525 */         return ((Long)mCallWindowProc.invoke(null, new Object[] { Long.valueOf(oldProc), Long.valueOf(hwnd), Integer.valueOf((int)msg), Long.valueOf(wParam), Long.valueOf(lParam) })).longValue();
/*     */       }
/* 527 */       return ((Integer)mCallWindowProc.invoke(null, new Object[] { Integer.valueOf((int)oldProc), Integer.valueOf((int)hwnd), Integer.valueOf((int)msg), Integer.valueOf((int)wParam), Integer.valueOf((int)lParam) })).intValue();
/*     */     }
/*     */     catch (Exception e) {
/* 530 */       e.printStackTrace();
/*     */     }
/* 532 */     return 0L;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/win32/Win32UIEnhancer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */