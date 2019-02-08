/*     */ package com.aelitis.azureus.core.update.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import com.aelitis.azureus.core.update.AzureusRestarter;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.platform.unix.ScriptAfterShutdown;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32Access;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32Manager;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class AzureusRestarterImpl
/*     */   implements AzureusRestarter
/*     */ {
/*  45 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */   private static final String MAIN_CLASS = "org.gudy.azureus2.update.Updater";
/*     */   
/*     */   private static final String UPDATER_JAR = "Updater.jar";
/*     */   
/*     */   private static final String EXE_UPDATER = "AzureusUpdater.exe";
/*     */   public static final String UPDATE_PROPERTIES = "update.properties";
/*  53 */   protected static boolean restarted = false;
/*     */   
/*  55 */   private static final String JAVA_EXEC_DIR = System.getProperty("java.home") + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator");
/*     */   
/*     */ 
/*     */ 
/*     */   protected final AzureusCore azureus_core;
/*     */   
/*     */ 
/*     */   protected String classpath_prefix;
/*     */   
/*     */ 
/*     */ 
/*     */   public AzureusRestarterImpl(AzureusCore _azureus_core)
/*     */   {
/*  68 */     this.azureus_core = _azureus_core;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void restart(boolean update_only)
/*     */   {
/*  75 */     synchronized (AzureusRestarterImpl.class)
/*     */     {
/*  77 */       if (restarted)
/*     */       {
/*  79 */         Logger.log(new LogEvent(LOGID, 1, "AzureusRestarter: already restarted!!!!"));
/*     */         
/*     */ 
/*  82 */         return;
/*     */       }
/*     */       
/*  85 */       restarted = true;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*  90 */       runUpdateProcess(update_only, false);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateNow()
/*     */     throws AzureusCoreException
/*     */   {
/* 102 */     if (!runUpdateProcess(true, true))
/*     */     {
/* 104 */       throw new AzureusCoreException("Failed to invoke restart");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean runUpdateProcess(boolean update_only, boolean no_wait)
/*     */     throws AzureusCoreException
/*     */   {
/* 115 */     PluginInterface pi = this.azureus_core.getPluginManager().getPluginInterfaceByID("azupdater");
/*     */     
/* 117 */     if (pi == null) {
/* 118 */       Logger.log(new LogAlert(false, 3, "Can't update/restart, mandatory plugin 'azupdater' not found"));
/*     */       
/*     */ 
/* 121 */       throw new AzureusCoreException("mandatory plugin 'azupdater' not found");
/*     */     }
/*     */     
/* 124 */     String updater_dir = pi.getPluginDirectoryName();
/*     */     
/* 126 */     this.classpath_prefix = (updater_dir + File.separator + "Updater.jar");
/*     */     
/* 128 */     String app_path = SystemProperties.getApplicationPath();
/*     */     
/* 130 */     while (app_path.endsWith(File.separator))
/*     */     {
/* 132 */       app_path = app_path.substring(0, app_path.length() - 1);
/*     */     }
/*     */     
/* 135 */     String user_path = SystemProperties.getUserPath();
/*     */     
/* 137 */     while (user_path.endsWith(File.separator))
/*     */     {
/* 139 */       user_path = user_path.substring(0, user_path.length() - 1);
/*     */     }
/*     */     
/* 142 */     String config_override = System.getProperty("azureus.config.path");
/*     */     
/* 144 */     if (config_override == null)
/*     */     {
/* 146 */       config_override = "";
/*     */     }
/*     */     
/* 149 */     String[] parameters = { update_only ? "updateonly" : "restart", app_path, user_path, config_override };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 156 */     FileOutputStream fos = null;
/*     */     try
/*     */     {
/* 159 */       Properties update_properties = new Properties();
/*     */       
/* 161 */       long max_mem = Runtime.getRuntime().maxMemory();
/*     */       
/* 163 */       update_properties.put("max_mem", "" + max_mem);
/* 164 */       update_properties.put("app_name", SystemProperties.getApplicationName());
/* 165 */       update_properties.put("app_entry", SystemProperties.getApplicationEntryPoint());
/*     */       
/* 167 */       if ((System.getProperty("azureus.nativelauncher") != null) || (Constants.isOSX))
/*     */       {
/*     */         try
/*     */         {
/* 171 */           String cmd = PlatformManagerFactory.getPlatformManager().getApplicationCommandLine();
/*     */           
/* 173 */           if (cmd != null)
/*     */           {
/* 175 */             update_properties.put("app_cmd", cmd);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 179 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 183 */       if (no_wait)
/*     */       {
/* 185 */         update_properties.put("no_wait", "1");
/*     */       }
/*     */       
/* 188 */       update_properties.put("instance_port", String.valueOf(Constants.INSTANCE_PORT));
/*     */       
/* 190 */       fos = new FileOutputStream(new File(user_path, "update.properties"));
/*     */       
/*     */ 
/*     */ 
/* 194 */       update_properties.store(fos, "Azureus restart properties");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 202 */       if (fos != null)
/*     */       {
/*     */         try
/*     */         {
/* 206 */           fos.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 210 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 198 */       Debug.printStackTrace(e);
/*     */     }
/*     */     finally
/*     */     {
/* 202 */       if (fos != null)
/*     */       {
/*     */         try
/*     */         {
/* 206 */           fos.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 210 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 215 */     tmp458_455[0] = ("-Duser.dir=\"" + app_path + "\"");String[] properties = tmp458_455;
/*     */     
/* 217 */     ByteArrayOutputStream os = new ByteArrayOutputStream();
/*     */     
/* 219 */     boolean res = restartAzureus(new PrintWriter(os)
/*     */     {
/*     */ 
/* 222 */       public void println(String str) { Logger.log(new LogEvent(AzureusRestarterImpl.LOGID, str)); } }, "org.gudy.azureus2.update.Updater", properties, parameters, update_only);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 229 */     byte[] bytes = os.toByteArray();
/*     */     
/* 231 */     if (bytes.length > 0)
/*     */     {
/* 233 */       Logger.log(new LogEvent(LOGID, "AzureusUpdater: extra log - " + new String(bytes)));
/*     */     }
/*     */     
/*     */ 
/* 237 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getClassPath()
/*     */   {
/* 244 */     String classPath = System.getProperty("java.class.path");
/*     */     
/* 246 */     classPath = this.classpath_prefix + System.getProperty("path.separator") + classPath;
/*     */     
/* 248 */     return "-classpath \"" + classPath + "\" ";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean win32NativeRestart(PrintWriter log, String exec)
/*     */   {
/*     */     try
/*     */     {
/* 260 */       PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*     */       
/* 262 */       pm.createProcess(exec, false);
/*     */       
/* 264 */       return true;
/*     */     }
/*     */     catch (Throwable e) {
/* 267 */       e.printStackTrace(log);
/*     */     }
/* 269 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getExeUpdater(PrintWriter log)
/*     */   {
/*     */     try
/*     */     {
/* 280 */       if (Constants.isWindowsVistaOrHigher)
/*     */       {
/* 282 */         if (PluginInitializer.getDefaultInterface().getUpdateManager().getInstallers().length > 0)
/*     */         {
/* 284 */           log.println("Vista restart w/Updates.. checking if EXE needed");
/*     */           
/* 286 */           if (!FileUtil.canReallyWriteToAppDirectory())
/*     */           {
/* 288 */             log.println("It appears we can't write to the application dir, using the EXE updater");
/*     */             
/* 290 */             return "AzureusUpdater.exe";
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {}
/*     */     
/*     */ 
/* 298 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean restartViaEXE(PrintWriter log, String exeUpdater, String[] properties, String[] parameters, String backupJavaRunString, boolean update_only)
/*     */   {
/* 308 */     String azRunner = null;
/* 309 */     File fileRestart = null;
/* 310 */     if (!update_only) {
/*     */       try {
/* 312 */         azRunner = PlatformManagerFactory.getPlatformManager().getApplicationCommandLine();
/*     */       }
/*     */       catch (PlatformManagerException e) {
/* 315 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 321 */       AEWin32Access accessor = AEWin32Manager.getAccessor(true);
/* 322 */       int result; int result; if (accessor == null) {
/* 323 */         result = -123;
/*     */       } else { int result;
/* 325 */         if (azRunner != null)
/*     */         {
/*     */ 
/*     */ 
/* 329 */           fileRestart = FileUtil.getUserFile("restart.bat");
/* 330 */           String s = "title Azureus Updater Runner\r\n";
/* 331 */           s = s + exeUpdater + " \"updateonly\"";
/* 332 */           for (int i = 1; i < parameters.length; i++) {
/* 333 */             s = s + " \"" + parameters[i].replaceAll("\\\"", "") + "\"";
/*     */           }
/* 335 */           s = s + "\r\n";
/* 336 */           s = s + "start \"\" \"" + azRunner + "\"";
/*     */           
/*     */ 
/*     */ 
/* 340 */           String encoding = FileUtil.getScriptCharsetEncoding();
/*     */           byte[] bytes;
/* 342 */           byte[] bytes; if (encoding == null) {
/* 343 */             bytes = s.getBytes();
/*     */           } else {
/*     */             try {
/* 346 */               bytes = s.getBytes(encoding);
/*     */             } catch (Throwable e) {
/* 348 */               e.printStackTrace();
/*     */               
/* 350 */               bytes = s.getBytes();
/*     */             }
/*     */           }
/* 353 */           FileUtil.writeBytesAsFile(fileRestart.getAbsolutePath(), bytes);
/*     */           
/* 355 */           result = accessor.shellExecute(null, fileRestart.getAbsolutePath(), null, SystemProperties.getApplicationPath(), 2);
/*     */         }
/*     */         else
/*     */         {
/* 359 */           String execEXE = "\"-J" + getClassPath().replaceAll("\\\"", "") + "\" ";
/*     */           
/*     */ 
/* 362 */           for (int i = 0; i < properties.length; i++) {
/* 363 */             execEXE = execEXE + "\"-J" + properties[i].replaceAll("\\\"", "") + "\" ";
/*     */           }
/*     */           
/* 366 */           for (int i = 0; i < parameters.length; i++) {
/* 367 */             execEXE = execEXE + " \"" + parameters[i].replaceAll("\\\"", "") + "\"";
/*     */           }
/*     */           
/* 370 */           log.println("Launch via " + exeUpdater + " params " + execEXE);
/* 371 */           result = accessor.shellExecute(null, exeUpdater, execEXE, SystemProperties.getApplicationPath(), 1);
/*     */         }
/*     */       }
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
/* 393 */       log.println("   -> " + result);
/*     */       
/* 395 */       if (result <= 32) {
/* 396 */         String sErrorReason = "";
/* 397 */         String key = null;
/*     */         
/* 399 */         switch (result) {
/*     */         case 0: 
/*     */         case 8: 
/* 402 */           key = "oom";
/* 403 */           break;
/*     */         
/*     */         case 2: 
/* 406 */           key = "fnf";
/* 407 */           break;
/*     */         
/*     */         case 3: 
/* 410 */           key = "pnf";
/* 411 */           break;
/*     */         
/*     */         case 5: 
/* 414 */           key = "denied";
/* 415 */           break;
/*     */         
/*     */         case 11: 
/* 418 */           key = "bad";
/* 419 */           break;
/*     */         
/*     */         case -123: 
/* 422 */           key = "nowin32";
/* 423 */           break;
/*     */         
/*     */         default: 
/* 426 */           sErrorReason = "" + result;
/*     */         }
/*     */         
/* 429 */         if (key != null) {
/* 430 */           sErrorReason = MessageText.getString("restart.error." + key, new String[] { exeUpdater, SystemProperties.getApplicationPath() });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 436 */         Logger.log(new LogAlert(false, 3, MessageText.getString("restart.error", new String[] { sErrorReason })));
/*     */         
/*     */ 
/*     */ 
/* 440 */         return false;
/*     */       }
/*     */     }
/*     */     catch (Throwable f) {
/* 444 */       f.printStackTrace(log);
/*     */       
/* 446 */       return javaSpawn(log, backupJavaRunString);
/*     */     }
/*     */     
/* 449 */     return true;
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
/*     */   public boolean restartAzureus(PrintWriter log, String mainClass, String[] properties, String[] parameters, boolean update_only)
/*     */   {
/* 466 */     if (Constants.isOSX)
/*     */     {
/* 468 */       return restartAzureus_OSX(log, mainClass, properties, parameters);
/*     */     }
/* 470 */     if (Constants.isUnix)
/*     */     {
/* 472 */       return restartAzureus_Unix(log, mainClass, properties, parameters);
/*     */     }
/*     */     
/*     */ 
/* 476 */     return restartAzureus_win32(log, mainClass, properties, parameters, update_only);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean restartAzureus_win32(PrintWriter log, String mainClass, String[] properties, String[] parameters, boolean update_only)
/*     */   {
/* 488 */     String exeUpdater = getExeUpdater(log);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 493 */     String exec = "\"" + JAVA_EXEC_DIR + "javaw\" " + getClassPath() + getLibraryPath();
/*     */     
/* 495 */     for (int i = 0; i < properties.length; i++) {
/* 496 */       exec = exec + properties[i] + " ";
/*     */     }
/*     */     
/* 499 */     exec = exec + mainClass;
/*     */     
/* 501 */     for (int i = 0; i < parameters.length; i++) {
/* 502 */       exec = exec + " \"" + parameters[i] + "\"";
/*     */     }
/*     */     
/* 505 */     if (exeUpdater != null) {
/* 506 */       return restartViaEXE(log, exeUpdater, properties, parameters, exec, update_only);
/*     */     }
/* 508 */     log.println("  " + exec);
/*     */     
/* 510 */     if (!win32NativeRestart(log, exec)) {
/* 511 */       return javaSpawn(log, exec);
/*     */     }
/* 513 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean javaSpawn(PrintWriter log, String execString)
/*     */   {
/*     */     try
/*     */     {
/* 527 */       log.println("Using java spawn");
/*     */       
/*     */ 
/* 530 */       Process p = Runtime.getRuntime().exec(execString);
/*     */       
/* 532 */       log.println("    -> " + p);
/*     */       
/* 534 */       return true;
/*     */     }
/*     */     catch (Throwable g) {
/* 537 */       g.printStackTrace(); }
/* 538 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean restartAzureus_OSX(PrintWriter log, String mainClass, String[] properties, String[] parameters)
/*     */   {
/* 550 */     String exec = "\"" + JAVA_EXEC_DIR + "java\" " + getClassPath() + getLibraryPath();
/*     */     
/* 552 */     for (int i = 0; i < properties.length; i++) {
/* 553 */       exec = exec + properties[i] + " ";
/*     */     }
/*     */     
/* 556 */     exec = exec + mainClass;
/*     */     
/* 558 */     for (int i = 0; i < parameters.length; i++) {
/* 559 */       exec = exec + " \"" + parameters[i] + "\"";
/*     */     }
/*     */     
/* 562 */     return runExternalCommandViaUnixShell(log, exec);
/*     */   }
/*     */   
/*     */ 
/*     */   private int getUnixScriptVersion()
/*     */   {
/* 568 */     String sVersion = System.getProperty("azureus.script.version", "0");
/* 569 */     int version = 0;
/*     */     try {
/* 571 */       version = Integer.parseInt(sVersion);
/*     */     }
/*     */     catch (Throwable t) {}
/* 574 */     return version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean restartAzureus_Unix(PrintWriter log, String mainClass, String[] properties, String[] parameters)
/*     */   {
/* 585 */     String exec = "\"" + JAVA_EXEC_DIR + "java\" " + getClassPath() + getLibraryPath();
/*     */     
/* 587 */     for (int i = 0; i < properties.length; i++) {
/* 588 */       exec = exec + properties[i] + " ";
/*     */     }
/*     */     
/* 591 */     int scriptVersion = getUnixScriptVersion();
/* 592 */     boolean restartByScript = (Constants.compareVersions(UpdaterUtils.getUpdaterPluginVersion(), "1.8.5") >= 0) && (scriptVersion > 0);
/*     */     
/*     */ 
/* 595 */     if (restartByScript) {
/* 596 */       exec = exec + "-Dazureus.script.version=\"" + scriptVersion + "\" ";
/*     */     }
/*     */     
/* 599 */     exec = exec + mainClass;
/*     */     
/* 601 */     for (int i = 0; i < parameters.length; i++) {
/* 602 */       exec = exec + " \"" + parameters[i] + "\"";
/*     */     }
/*     */     
/* 605 */     if (restartByScript)
/*     */     {
/* 607 */       ScriptAfterShutdown.addExtraCommand("echo \"Applying (possible) patches before restarting..\"\n" + exec + "\n" + "echo \"Restarting Azureus..\"\n" + "$0\n");
/*     */       
/*     */ 
/*     */ 
/* 611 */       ScriptAfterShutdown.setRequiresExit(true);
/*     */       
/* 613 */       return true;
/*     */     }
/* 615 */     return runExternalCommandViaUnixShell(log, exec);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getLibraryPath()
/*     */   {
/* 624 */     String libraryPath = System.getProperty("java.library.path");
/*     */     
/* 626 */     if (libraryPath == null)
/*     */     {
/* 628 */       libraryPath = "";
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 634 */       String temp = "";
/*     */       
/* 636 */       for (int i = 0; i < libraryPath.length(); i++)
/*     */       {
/* 638 */         char c = libraryPath.charAt(i);
/*     */         
/* 640 */         if (c != '"')
/*     */         {
/* 642 */           temp = temp + c;
/*     */         }
/*     */       }
/*     */       
/* 646 */       libraryPath = temp;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 651 */       while (libraryPath.endsWith(File.separator))
/*     */       {
/* 653 */         libraryPath = libraryPath.substring(0, libraryPath.length() - 1);
/*     */       }
/*     */       
/* 656 */       if (libraryPath.length() > 0)
/*     */       {
/* 658 */         libraryPath = "-Djava.library.path=\"" + libraryPath + "\" ";
/*     */       }
/*     */     }
/*     */     
/* 662 */     return libraryPath;
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
/*     */   private boolean runExternalCommandViaUnixShell(PrintWriter log, String command)
/*     */   {
/* 743 */     String[] to_run = new String[3];
/* 744 */     to_run[0] = "/bin/sh";
/* 745 */     to_run[1] = "-c";
/* 746 */     to_run[2] = command;
/*     */     
/* 748 */     if (log != null) { log.println("Executing: R:[" + to_run[0] + " " + to_run[1] + " " + to_run[2] + "]");
/*     */     }
/*     */     try
/*     */     {
/* 752 */       Runtime.getRuntime().exec(to_run);
/*     */       
/* 754 */       return true;
/*     */     }
/*     */     catch (Throwable t) {
/* 757 */       if (log != null) {
/* 758 */         log.println(t.getMessage() != null ? t.getMessage() : "<null>");
/* 759 */         log.println(t);
/* 760 */         t.printStackTrace(log);
/*     */       }
/*     */       else {
/* 763 */         t.printStackTrace();
/*     */       }
/*     */     }
/* 766 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/update/impl/AzureusRestarterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */