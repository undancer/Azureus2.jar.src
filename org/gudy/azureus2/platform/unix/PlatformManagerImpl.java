/*     */ package org.gudy.azureus2.platform.unix;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.util.HashSet;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
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
/*     */ public class PlatformManagerImpl
/*     */   implements PlatformManager
/*     */ {
/*  42 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */   private static final String ERR_UNSUPPORTED = "Unsupported capability called on platform manager";
/*     */   
/*     */   protected static PlatformManagerImpl singleton;
/*     */   
/*  48 */   protected static AEMonitor class_mon = new AEMonitor("PlatformManager");
/*     */   
/*  50 */   private final HashSet capabilitySet = new HashSet();
/*     */   
/*  52 */   private static final Object migrate_lock = new Object();
/*     */   
/*     */ 
/*     */ 
/*     */   public static PlatformManagerImpl getSingleton()
/*     */   {
/*  58 */     return singleton;
/*     */   }
/*     */   
/*     */   static {
/*  62 */     initializeSingleton();
/*     */   }
/*     */   
/*     */ 
/*     */   private static void initializeSingleton()
/*     */   {
/*     */     try
/*     */     {
/*  70 */       class_mon.enter();
/*  71 */       singleton = new PlatformManagerImpl();
/*     */     } catch (Throwable e) {
/*  73 */       Logger.log(new LogEvent(LOGID, "Failed to initialize platform manager for Unix Compatable OS", e));
/*     */     }
/*     */     finally {
/*  76 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PlatformManagerImpl()
/*     */   {
/*  84 */     this.capabilitySet.add(PlatformManagerCapabilities.GetUserDataDirectory);
/*     */   }
/*     */   
/*     */   public void copyFilePermissions(String from_file_name, String to_file_name)
/*     */     throws PlatformManagerException
/*     */   {
/*  90 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void createProcess(String command_line, boolean inherit_handles)
/*     */     throws PlatformManagerException
/*     */   {
/*  96 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getApplicationCommandLine()
/*     */     throws PlatformManagerException
/*     */   {
/* 105 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public int getPlatformType()
/*     */   {
/* 110 */     return 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUserDataDirectory()
/*     */     throws PlatformManagerException
/*     */   {
/* 117 */     String userhome = System.getProperty("user.home");
/* 118 */     String temp_user_path = userhome + SystemProperties.SEP + "." + SystemProperties.APPLICATION_NAME.toLowerCase() + SystemProperties.SEP;
/*     */     
/*     */ 
/*     */ 
/* 122 */     synchronized (migrate_lock) {
/* 123 */       File home = new File(temp_user_path);
/* 124 */       if (!home.exists()) {
/* 125 */         String old_home_path = userhome + SystemProperties.SEP + "." + SystemProperties.APPLICATION_NAME + SystemProperties.SEP;
/*     */         
/* 127 */         File old_home = new File(old_home_path);
/* 128 */         if (old_home.exists()) {
/* 129 */           String msg = "Migrating unix user config dir [" + old_home_path + "] ===> [" + temp_user_path + "]";
/*     */           
/* 131 */           System.out.println(msg);
/* 132 */           Logger.log(new LogEvent(LOGID, "SystemProperties::getUserPath(Unix): " + msg));
/*     */           try
/*     */           {
/* 135 */             old_home.renameTo(home);
/*     */           } catch (Throwable t) {
/* 137 */             t.printStackTrace();
/* 138 */             Logger.log(new LogEvent(LOGID, "migration rename failed:", t));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 144 */     return temp_user_path;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getComputerName()
/*     */   {
/* 150 */     String host = System.getenv("HOST");
/*     */     
/* 152 */     if ((host != null) && (host.length() > 0))
/*     */     {
/* 154 */       return host;
/*     */     }
/*     */     
/* 157 */     return null;
/*     */   }
/*     */   
/*     */   public String getVersion() throws PlatformManagerException
/*     */   {
/* 162 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public boolean hasCapability(PlatformManagerCapabilities capability)
/*     */   {
/* 167 */     return this.capabilitySet.contains(capability);
/*     */   }
/*     */   
/*     */   public boolean isApplicationRegistered() throws PlatformManagerException
/*     */   {
/* 172 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void performRecoverableFileDelete(String file_name)
/*     */     throws PlatformManagerException
/*     */   {
/* 178 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void ping(InetAddress interface_address, InetAddress target, PlatformManagerPingCallback callback)
/*     */     throws PlatformManagerException
/*     */   {
/* 184 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMaxOpenFiles()
/*     */     throws PlatformManagerException
/*     */   {
/* 192 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void registerApplication() throws PlatformManagerException
/*     */   {
/* 197 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
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
/*     */   public File getVMOptionFile()
/*     */     throws PlatformManagerException
/*     */   {
/* 215 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getExplicitVMOptions()
/*     */     throws PlatformManagerException
/*     */   {
/* 223 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setExplicitVMOptions(String[] options)
/*     */     throws PlatformManagerException
/*     */   {
/* 232 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getRunAtLogin()
/*     */     throws PlatformManagerException
/*     */   {
/* 240 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRunAtLogin(boolean run)
/*     */     throws PlatformManagerException
/*     */   {
/* 249 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
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
/*     */   public int getShutdownTypes()
/*     */   {
/* 263 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void shutdown(int type)
/*     */     throws PlatformManagerException
/*     */   {
/* 272 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPreventComputerSleep(boolean b)
/*     */     throws PlatformManagerException
/*     */   {
/* 281 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getPreventComputerSleep()
/*     */   {
/* 287 */     return false;
/*     */   }
/*     */   
/*     */   public void setTCPTOSEnabled(boolean enabled) throws PlatformManagerException
/*     */   {
/* 292 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public boolean testNativeAvailability(String name)
/*     */     throws PlatformManagerException
/*     */   {
/* 298 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void traceRoute(InetAddress interface_address, InetAddress target, PlatformManagerPingCallback callback)
/*     */     throws PlatformManagerException
/*     */   {
/* 304 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public File getLocation(long location_id) throws PlatformManagerException
/*     */   {
/* 309 */     switch ((int)location_id) {
/*     */     case 1: 
/* 311 */       return new File(getUserDataDirectory());
/*     */     
/*     */     case 3: 
/* 314 */       return new File(System.getProperty("user.home"));
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 321 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAdditionalFileTypeRegistered(String name, String type)
/*     */     throws PlatformManagerException
/*     */   {
/* 328 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void registerAdditionalFileType(String name, String description, String type, String content_type)
/*     */     throws PlatformManagerException
/*     */   {
/* 334 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void showFile(String file_name) throws PlatformManagerException
/*     */   {
/* 339 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void unregisterAdditionalFileType(String name, String type)
/*     */     throws PlatformManagerException
/*     */   {
/* 345 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public String getAzComputerID() throws PlatformManagerException
/*     */   {
/* 350 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*     */   }
/*     */   
/*     */   public void requestUserAttention(int type, Object data) throws PlatformManagerException {
/* 354 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
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
/* 365 */       return loader.loadClass(class_name);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 369 */       throw new PlatformManagerException("load of '" + class_name + "' failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose() {}
/*     */   
/*     */   public void addListener(PlatformManagerListener listener) {}
/*     */   
/*     */   public void removeListener(PlatformManagerListener listener) {}
/*     */   
/*     */   public void startup(AzureusCore azureus_core)
/*     */     throws PlatformManagerException
/*     */   {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/unix/PlatformManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */