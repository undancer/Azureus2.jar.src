package org.gudy.azureus2.platform.win32.access;

import java.io.File;
import java.net.InetAddress;
import java.util.Map;
import org.gudy.azureus2.platform.PlatformManagerPingCallback;

public abstract interface AEWin32Access
{
  public static final int HKEY_CLASSES_ROOT = 1;
  public static final int HKEY_CURRENT_CONFIG = 2;
  public static final int HKEY_LOCAL_MACHINE = 3;
  public static final int HKEY_CURRENT_USER = 4;
  public static final int SW_HIDE = 0;
  public static final int SW_NORMAL = 1;
  public static final int SW_SHOWNORMAL = 1;
  public static final int SW_SHOWMINIMIZED = 2;
  public static final int SW_SHOWMAXIMIZED = 3;
  public static final int SW_MAXIMIZE = 3;
  public static final int SW_SHOWNOACTIVATE = 4;
  public static final int SW_SHOW = 5;
  public static final int SW_MINIMIZE = 6;
  public static final int SW_SHOWMINNOACTIVE = 7;
  public static final int SW_SHOWNA = 8;
  public static final int SW_RESTORE = 9;
  public static final int SW_SHOWDEFAULT = 10;
  public static final int SW_FORCEMINIMIZE = 11;
  public static final int SW_MAX = 11;
  
  public abstract boolean isEnabled();
  
  public abstract String getVersion();
  
  public abstract String readStringValue(int paramInt, String paramString1, String paramString2)
    throws AEWin32AccessException;
  
  public abstract void writeStringValue(int paramInt, String paramString1, String paramString2, String paramString3)
    throws AEWin32AccessException;
  
  public abstract int readWordValue(int paramInt, String paramString1, String paramString2)
    throws AEWin32AccessException;
  
  public abstract void writeWordValue(int paramInt1, String paramString1, String paramString2, int paramInt2)
    throws AEWin32AccessException;
  
  public abstract void deleteKey(int paramInt, String paramString)
    throws AEWin32AccessException;
  
  public abstract void deleteKey(int paramInt, String paramString, boolean paramBoolean)
    throws AEWin32AccessException;
  
  public abstract void deleteValue(int paramInt, String paramString1, String paramString2)
    throws AEWin32AccessException;
  
  public abstract String getUserAppData()
    throws AEWin32AccessException;
  
  public abstract String getProgramFilesDir()
    throws AEWin32AccessException;
  
  public abstract String getApplicationInstallDir(String paramString)
    throws AEWin32AccessException;
  
  public abstract void createProcess(String paramString, boolean paramBoolean)
    throws AEWin32AccessException;
  
  public abstract void moveToRecycleBin(String paramString)
    throws AEWin32AccessException;
  
  public abstract void copyFilePermissions(String paramString1, String paramString2)
    throws AEWin32AccessException;
  
  public abstract boolean testNativeAvailability(String paramString)
    throws AEWin32AccessException;
  
  public abstract void traceRoute(InetAddress paramInetAddress1, InetAddress paramInetAddress2, PlatformManagerPingCallback paramPlatformManagerPingCallback)
    throws AEWin32AccessException;
  
  public abstract void ping(InetAddress paramInetAddress1, InetAddress paramInetAddress2, PlatformManagerPingCallback paramPlatformManagerPingCallback)
    throws AEWin32AccessException;
  
  public abstract void addListener(AEWin32AccessListener paramAEWin32AccessListener);
  
  public abstract void removeListener(AEWin32AccessListener paramAEWin32AccessListener);
  
  public abstract String getUserDocumentsDir()
    throws AEWin32AccessException;
  
  public abstract String getUserMusicDir()
    throws AEWin32AccessException;
  
  public abstract String getUserVideoDir()
    throws AEWin32AccessException;
  
  public abstract String getCommonAppData()
    throws AEWin32AccessException;
  
  public abstract int shellExecute(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
    throws AEWin32AccessException;
  
  public abstract int shellExecuteAndWait(String paramString1, String paramString2)
    throws AEWin32AccessException;
  
  public abstract Map<File, Map> getAllDrives();
  
  public abstract boolean isUSBDrive(Map paramMap);
  
  public abstract String getLocalAppData()
    throws AEWin32AccessException;
  
  public abstract void setThreadExecutionState(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/access/AEWin32Access.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */