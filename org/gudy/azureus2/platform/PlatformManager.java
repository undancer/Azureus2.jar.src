/*    */ package org.gudy.azureus2.platform;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import java.io.File;
/*    */ import java.net.InetAddress;
/*    */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface PlatformManager
/*    */   extends org.gudy.azureus2.plugins.platform.PlatformManager
/*    */ {
/*    */   public static final int PT_WINDOWS = 1;
/*    */   public static final int PT_OTHER = 2;
/*    */   public static final int PT_MACOSX = 3;
/*    */   public static final int PT_UNIX = 4;
/*    */   public static final int USER_REQUEST_INFO = 1;
/*    */   public static final int USER_REQUEST_WARNING = 2;
/*    */   public static final int USER_REQUEST_QUESTION = 3;
/*    */   public static final int SD_SHUTDOWN = 1;
/*    */   public static final int SD_HIBERNATE = 2;
/*    */   public static final int SD_SLEEP = 4;
/* 50 */   public static final int[] SD_ALL = { 1, 2, 4 };
/*    */   
/*    */   public abstract int getPlatformType();
/*    */   
/*    */   public abstract String getVersion()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void startup(AzureusCore paramAzureusCore)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract String getUserDataDirectory()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract boolean isApplicationRegistered()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void registerApplication()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract String getApplicationCommandLine()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract File getVMOptionFile()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract String[] getExplicitVMOptions()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void setExplicitVMOptions(String[] paramArrayOfString)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract boolean getRunAtLogin()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void setRunAtLogin(boolean paramBoolean)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract int getShutdownTypes();
/*    */   
/*    */   public abstract void shutdown(int paramInt)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void setPreventComputerSleep(boolean paramBoolean)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract boolean getPreventComputerSleep();
/*    */   
/*    */   public abstract void createProcess(String paramString, boolean paramBoolean)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void performRecoverableFileDelete(String paramString)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void setTCPTOSEnabled(boolean paramBoolean)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void copyFilePermissions(String paramString1, String paramString2)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract boolean testNativeAvailability(String paramString)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void traceRoute(InetAddress paramInetAddress1, InetAddress paramInetAddress2, PlatformManagerPingCallback paramPlatformManagerPingCallback)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract void ping(InetAddress paramInetAddress1, InetAddress paramInetAddress2, PlatformManagerPingCallback paramPlatformManagerPingCallback)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract int getMaxOpenFiles()
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract Class<?> loadClass(ClassLoader paramClassLoader, String paramString)
/*    */     throws PlatformManagerException;
/*    */   
/*    */   public abstract boolean hasCapability(PlatformManagerCapabilities paramPlatformManagerCapabilities);
/*    */   
/*    */   public abstract void dispose();
/*    */   
/*    */   public abstract void addListener(PlatformManagerListener paramPlatformManagerListener);
/*    */   
/*    */   public abstract void removeListener(PlatformManagerListener paramPlatformManagerListener);
/*    */   
/*    */   public abstract void requestUserAttention(int paramInt, Object paramObject)
/*    */     throws PlatformManagerException;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/PlatformManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */