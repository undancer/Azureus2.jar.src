/*     */ package org.gudy.azureus2.platform.dummy;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import java.net.InetAddress;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.platform.PlatformManagerListener;
/*     */ import org.gudy.azureus2.platform.PlatformManagerPingCallback;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
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
/*     */ public class PlatformManagerImpl
/*     */   implements PlatformManager
/*     */ {
/*  43 */   private static PlatformManager singleton = new PlatformManagerImpl();
/*     */   
/*     */ 
/*     */   public static PlatformManager getSingleton()
/*     */   {
/*  48 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPlatformType()
/*     */   {
/*  58 */     return PlatformManagerFactory.getPlatformType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUserDataDirectory()
/*     */     throws PlatformManagerException
/*     */   {
/*  68 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isApplicationRegistered()
/*     */     throws PlatformManagerException
/*     */   {
/*  78 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getApplicationCommandLine()
/*     */     throws PlatformManagerException
/*     */   {
/*  86 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */   public String getComputerName()
/*     */   {
/*  92 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getLocation(long location_id)
/*     */     throws PlatformManagerException
/*     */   {
/* 101 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getVMOptionFile()
/*     */     throws PlatformManagerException
/*     */   {
/* 109 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getExplicitVMOptions()
/*     */     throws PlatformManagerException
/*     */   {
/* 117 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getRunAtLogin()
/*     */     throws PlatformManagerException
/*     */   {
/* 125 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRunAtLogin(boolean run)
/*     */     throws PlatformManagerException
/*     */   {
/* 134 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startup(AzureusCore azureus_core)
/*     */     throws PlatformManagerException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getShutdownTypes()
/*     */   {
/* 148 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void shutdown(int type)
/*     */     throws PlatformManagerException
/*     */   {
/* 157 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPreventComputerSleep(boolean b)
/*     */     throws PlatformManagerException
/*     */   {
/* 166 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getPreventComputerSleep()
/*     */   {
/* 172 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setExplicitVMOptions(String[] options)
/*     */     throws PlatformManagerException
/*     */   {
/* 181 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAdditionalFileTypeRegistered(String name, String type)
/*     */     throws PlatformManagerException
/*     */   {
/* 191 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void unregisterAdditionalFileType(String name, String type)
/*     */     throws PlatformManagerException
/*     */   {
/* 201 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerApplication()
/*     */     throws PlatformManagerException
/*     */   {
/* 211 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerAdditionalFileType(String name, String description, String type, String content_type)
/*     */     throws PlatformManagerException
/*     */   {
/* 223 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void createProcess(String command_line, boolean inherit_handles)
/*     */     throws PlatformManagerException
/*     */   {
/* 233 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void performRecoverableFileDelete(String file_name)
/*     */     throws PlatformManagerException
/*     */   {
/* 243 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getVersion()
/*     */     throws PlatformManagerException
/*     */   {
/* 253 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTCPTOSEnabled(boolean enabled)
/*     */     throws PlatformManagerException
/*     */   {
/* 265 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void copyFilePermissions(String from_file_name, String to_file_name)
/*     */     throws PlatformManagerException
/*     */   {
/* 275 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void showFile(String file_name)
/*     */     throws PlatformManagerException
/*     */   {
/* 285 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean testNativeAvailability(String name)
/*     */     throws PlatformManagerException
/*     */   {
/* 294 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void traceRoute(InetAddress interface_address, InetAddress target, PlatformManagerPingCallback callback)
/*     */     throws PlatformManagerException
/*     */   {
/* 305 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void ping(InetAddress interface_address, InetAddress target, PlatformManagerPingCallback callback)
/*     */     throws PlatformManagerException
/*     */   {
/* 316 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMaxOpenFiles()
/*     */     throws PlatformManagerException
/*     */   {
/* 324 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasCapability(PlatformManagerCapabilities capability)
/*     */   {
/* 332 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispose() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(PlatformManagerListener listener) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(PlatformManagerListener listener) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAzComputerID()
/*     */     throws PlatformManagerException
/*     */   {
/* 356 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void requestUserAttention(int type, Object data) throws PlatformManagerException {
/* 360 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?> loadClass(ClassLoader loader, String class_name)
/*     */     throws PlatformManagerException
/*     */   {
/*     */     try
/*     */     {
/* 371 */       return loader.loadClass(class_name);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 375 */       throw new PlatformManagerException("load of '" + class_name + "' failed", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/dummy/PlatformManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */