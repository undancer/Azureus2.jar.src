/*      */ package org.gudy.azureus2.platform.win32;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.InetAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*      */ import org.gudy.azureus2.platform.PlatformManagerListener;
/*      */ import org.gudy.azureus2.platform.PlatformManagerPingCallback;
/*      */ import org.gudy.azureus2.platform.win32.access.AEWin32Access;
/*      */ import org.gudy.azureus2.platform.win32.access.AEWin32AccessListener;
/*      */ import org.gudy.azureus2.platform.win32.access.AEWin32Manager;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*      */ import org.gudy.azureus2.plugins.update.UpdateException;
/*      */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*      */ import org.gudy.azureus2.plugins.update.UpdateInstallerListener;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManagerListener;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PlatformManagerImpl
/*      */   implements PlatformManager, AEWin32AccessListener, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   public static final int RT_NONE = 0;
/*      */   public static final int RT_AZ = 1;
/*      */   public static final int RT_OTHER = 2;
/*   62 */   public static String DLL_NAME = "aereg";
/*      */   
/*      */   public static final String VUZE_ASSOC = "Vuze";
/*      */   
/*      */   public static final String NEW_MAIN_ASSOC = "Azureus";
/*      */   
/*      */   public static final String OLD_MAIN_ASS0C = "BitTorrent";
/*      */   private static boolean initialising;
/*      */   private static boolean init_tried;
/*      */   private static PlatformManagerImpl singleton;
/*   72 */   private static AEMonitor class_mon = new AEMonitor("PlatformManager");
/*      */   
/*   74 */   private final Set capabilitySet = new HashSet();
/*      */   
/*   76 */   private List listeners = new ArrayList();
/*      */   private final AEWin32Access access;
/*      */   
/*   79 */   static { if (System.getProperty("aereg", null) != null) {
/*   80 */       DLL_NAME = System.getProperty("aereg");
/*   81 */     } else if (System.getProperty("os.arch", "").contains("64")) {
/*   82 */       DLL_NAME += "64";
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static PlatformManagerImpl getSingleton()
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/*   92 */       class_mon.enter();
/*      */       
/*   94 */       if (singleton != null)
/*      */       {
/*   96 */         return singleton;
/*      */       }
/*      */       try
/*      */       {
/*  100 */         if (initialising)
/*      */         {
/*  102 */           System.err.println("PlatformManager: recursive entry during initialisation");
/*      */         }
/*      */         
/*  105 */         initialising = true;
/*      */         
/*  107 */         if (!init_tried)
/*      */         {
/*  109 */           init_tried = true;
/*      */           try
/*      */           {
/*  112 */             singleton = new PlatformManagerImpl();
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (PlatformManagerException e)
/*      */           {
/*      */ 
/*      */ 
/*  121 */             throw e;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  125 */             if ((e instanceof PlatformManagerException))
/*      */             {
/*  127 */               throw ((PlatformManagerException)e);
/*      */             }
/*      */             
/*  130 */             throw new PlatformManagerException("Win32Platform: failed to initialise", (Throwable)e);
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/*  135 */         initialising = false;
/*      */       }
/*      */       
/*  138 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*  142 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final String app_name;
/*      */   
/*      */ 
/*      */ 
/*      */   private final String app_exe_name;
/*      */   
/*      */ 
/*      */ 
/*      */   protected PlatformManagerImpl()
/*      */     throws PlatformManagerException
/*      */   {
/*  161 */     this.access = AEWin32Manager.getAccessor(true);
/*      */     
/*  163 */     this.access.addListener(this);
/*      */     
/*  165 */     this.app_name = SystemProperties.getApplicationName();
/*      */     
/*  167 */     String mod_name = System.getProperty("exe4j.moduleName", null);
/*      */     
/*  169 */     String exe_name = null;
/*      */     
/*  171 */     if ((mod_name != null) && (new File(mod_name).exists()) && (mod_name.toLowerCase().endsWith(".exe")))
/*      */     {
/*  173 */       int pos = mod_name.lastIndexOf(File.separator);
/*      */       
/*  175 */       if (pos != -1)
/*      */       {
/*  177 */         exe_name = mod_name.substring(pos + 1);
/*      */       }
/*      */     }
/*      */     
/*  181 */     if (exe_name == null)
/*      */     {
/*  183 */       exe_name = this.app_name + ".exe";
/*      */     }
/*      */     
/*  186 */     this.app_exe_name = exe_name;
/*      */     
/*  188 */     initializeCapabilities();
/*      */   }
/*      */   
/*      */ 
/*      */   private void initializeCapabilities()
/*      */   {
/*  194 */     if (this.access.isEnabled())
/*      */     {
/*  196 */       this.capabilitySet.add(PlatformManagerCapabilities.CreateCommandLineProcess);
/*  197 */       this.capabilitySet.add(PlatformManagerCapabilities.GetUserDataDirectory);
/*  198 */       this.capabilitySet.add(PlatformManagerCapabilities.RecoverableFileDelete);
/*  199 */       this.capabilitySet.add(PlatformManagerCapabilities.RegisterFileAssociations);
/*  200 */       this.capabilitySet.add(PlatformManagerCapabilities.ShowFileInBrowser);
/*  201 */       this.capabilitySet.add(PlatformManagerCapabilities.GetVersion);
/*  202 */       this.capabilitySet.add(PlatformManagerCapabilities.SetTCPTOSEnabled);
/*  203 */       this.capabilitySet.add(PlatformManagerCapabilities.ComputerIDAvailability);
/*      */       
/*  205 */       String plugin_version = this.access.getVersion();
/*      */       
/*  207 */       if ((Constants.compareVersions(plugin_version, "1.11") >= 0) && (!Constants.isWindows9598ME))
/*      */       {
/*      */ 
/*  210 */         this.capabilitySet.add(PlatformManagerCapabilities.CopyFilePermissions);
/*      */       }
/*      */       
/*      */ 
/*  214 */       if (Constants.compareVersions(plugin_version, "1.12") >= 0)
/*      */       {
/*  216 */         this.capabilitySet.add(PlatformManagerCapabilities.TestNativeAvailability);
/*      */       }
/*      */       
/*  219 */       if (Constants.compareVersions(plugin_version, "1.14") >= 0)
/*      */       {
/*  221 */         this.capabilitySet.add(PlatformManagerCapabilities.TraceRouteAvailability);
/*      */       }
/*      */       
/*  224 */       if (Constants.compareVersions(plugin_version, "1.15") >= 0)
/*      */       {
/*  226 */         this.capabilitySet.add(PlatformManagerCapabilities.PingAvailability);
/*      */       }
/*      */       try
/*      */       {
/*  230 */         getUserDataDirectory();
/*      */         
/*      */ 
/*      */ 
/*  234 */         if (Constants.compareVersions(plugin_version, "1.19") >= 0)
/*      */         {
/*  236 */           this.capabilitySet.add(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*  241 */       this.capabilitySet.add(PlatformManagerCapabilities.RunAtLogin);
/*  242 */       this.capabilitySet.add(PlatformManagerCapabilities.PreventComputerSleep);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  248 */       this.capabilitySet.add(PlatformManagerCapabilities.GetVersion);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void applyPatches()
/*      */   {
/*      */     try
/*      */     {
/*  256 */       File exe_loc = getApplicationEXELocation();
/*      */       
/*  258 */       String az_exe_string = exe_loc.getAbsolutePath();
/*      */       
/*      */ 
/*      */ 
/*  262 */       String current = this.access.readStringValue(1, "Azureus\\DefaultIcon", "");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  270 */       String target = az_exe_string + "," + getIconIndex();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  276 */       if ((current.contains(this.app_exe_name)) && (!current.equals(target)))
/*      */       {
/*  278 */         writeStringToHKCRandHKCU("Azureus\\DefaultIcon", "", target);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  290 */     if ((hasCapability(PlatformManagerCapabilities.CopyFilePermissions)) && (!COConfigurationManager.getBooleanParameter("platform.win32.permfixdone2", false)))
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/*  295 */         String str = SystemProperties.getApplicationPath();
/*      */         
/*  297 */         if (str.endsWith(File.separator))
/*      */         {
/*  299 */           str = str.substring(0, str.length() - 1);
/*      */         }
/*      */         
/*  302 */         fixPermissions(new File(str), new File(str));
/*      */ 
/*      */       }
/*      */       catch (Throwable e) {}finally
/*      */       {
/*      */ 
/*  308 */         COConfigurationManager.setParameter("platform.win32.permfixdone2", true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fixPermissions(File parent, File dir)
/*      */     throws PlatformManagerException
/*      */   {
/*  320 */     File[] files = dir.listFiles();
/*      */     
/*  322 */     if (files == null)
/*      */     {
/*  324 */       return;
/*      */     }
/*      */     
/*  327 */     for (int i = 0; i < files.length; i++)
/*      */     {
/*  329 */       File file = files[i];
/*      */       
/*  331 */       if (file.isFile())
/*      */       {
/*  333 */         copyFilePermissions(parent.getAbsolutePath(), file.getAbsolutePath());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private File az_exe;
/*      */   
/*      */ 
/*      */   private boolean az_exe_checked;
/*      */   
/*      */ 
/*      */   private boolean prevent_computer_sleep;
/*      */   
/*      */ 
/*      */   private AEThread2 prevent_sleep_thread;
/*      */   
/*      */ 
/*      */   protected int getIconIndex()
/*      */     throws PlatformManagerException
/*      */   {
/*  355 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getVersion()
/*      */   {
/*  361 */     return this.access.getVersion();
/*      */   }
/*      */   
/*      */ 
/*      */   protected File getApplicationEXELocation()
/*      */     throws PlatformManagerException
/*      */   {
/*  368 */     if (this.az_exe == null)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/*      */ 
/*  375 */         String az_home = SystemProperties.getApplicationPath();
/*      */         
/*  377 */         this.az_exe = new File(az_home + File.separator + this.app_exe_name).getAbsoluteFile();
/*      */         
/*  379 */         if (!this.az_exe.exists()) {
/*      */           try {
/*  381 */             az_home = this.access.getApplicationInstallDir(this.app_name);
/*      */             
/*  383 */             this.az_exe = new File(az_home + File.separator + this.app_exe_name).getAbsoluteFile();
/*      */             
/*  385 */             if (!this.az_exe.exists())
/*      */             {
/*  387 */               throw new PlatformManagerException(this.app_exe_name + " not found in " + az_home + ", please re-install");
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/*  394 */         if (!this.az_exe.exists())
/*      */         {
/*  396 */           String msg = this.app_exe_name + " not found in " + az_home + " - can't check file associations. Please re-install " + this.app_name;
/*      */           
/*  398 */           this.az_exe = null;
/*      */           
/*  400 */           if (!this.az_exe_checked)
/*      */           {
/*  402 */             Logger.log(new LogAlert(false, 1, msg));
/*      */           }
/*      */           
/*      */ 
/*  406 */           throw new PlatformManagerException(msg);
/*      */         }
/*      */       }
/*      */       finally {
/*  410 */         this.az_exe_checked = true;
/*      */       }
/*      */     }
/*      */     
/*  414 */     return this.az_exe;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPlatformType()
/*      */   {
/*  420 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUserDataDirectory()
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/*  429 */       return this.access.getUserAppData() + SystemProperties.SEP + this.app_name + SystemProperties.SEP;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  433 */       throw new PlatformManagerException("Failed to read registry details", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getComputerName()
/*      */   {
/*  440 */     String host = System.getenv("COMPUTERNAME");
/*      */     
/*  442 */     if ((host != null) && (host.length() > 0))
/*      */     {
/*  444 */       return host;
/*      */     }
/*      */     
/*  447 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public File getLocation(long location_id)
/*      */     throws PlatformManagerException
/*      */   {
/*  456 */     if (location_id == 1L)
/*      */     {
/*  458 */       return new File(getUserDataDirectory());
/*      */     }
/*  460 */     if (location_id == 2L)
/*      */     {
/*      */       try
/*      */       {
/*  464 */         return new File(this.access.getUserMusicDir());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  468 */         throw new PlatformManagerException("Failed to read registry details", e);
/*      */       } }
/*  470 */     if (location_id == 3L)
/*      */       try
/*      */       {
/*  473 */         return new File(this.access.getUserDocumentsDir());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  477 */         throw new PlatformManagerException("Failed to read registry details", e);
/*      */       }
/*  479 */     if (location_id == 4L) {
/*      */       try
/*      */       {
/*  482 */         return new File(this.access.getUserVideoDir());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  486 */         throw new PlatformManagerException("Failed to read registry details", e);
/*      */       }
/*      */     }
/*      */     
/*  490 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getJVMOptionRedirect()
/*      */   {
/*  497 */     return "-include-options ${APPDATA}\\" + SystemProperties.getApplicationName() + "\\java.vmoptions";
/*      */   }
/*      */   
/*      */   private File[] getJVMOptionFiles()
/*      */   {
/*      */     try
/*      */     {
/*  504 */       File exe = getApplicationEXELocation();
/*      */       
/*  506 */       File shared_options = new File(exe.getParent(), exe.getName() + ".vmoptions");
/*  507 */       File local_options = new File(SystemProperties.getUserPath(), "java.vmoptions");
/*      */       
/*  509 */       return new File[] { shared_options, local_options };
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  513 */     return new File[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private File checkAndGetLocalVMOptionFile()
/*      */     throws PlatformManagerException
/*      */   {
/*  522 */     String vendor = System.getProperty("java.vendor", "<unknown>");
/*      */     
/*  524 */     if ((!vendor.toLowerCase().startsWith("sun ")) && (!vendor.toLowerCase().startsWith("oracle ")))
/*      */     {
/*  526 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.sunonly", new String[] { vendor }));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  532 */     File[] option_files = getJVMOptionFiles();
/*      */     
/*  534 */     if (option_files.length != 2)
/*      */     {
/*  536 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.configerror"));
/*      */     }
/*      */     
/*      */ 
/*  540 */     File shared_options = option_files[0];
/*      */     
/*  542 */     if (shared_options.exists()) {
/*      */       try
/*      */       {
/*  545 */         String s_options = FileUtil.readFileAsString(shared_options, -1);
/*      */         
/*  547 */         if (s_options.contains(getJVMOptionRedirect()))
/*      */         {
/*  549 */           return option_files[1];
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  555 */         throw new PlatformManagerException(MessageText.getString("platform.jvmopt.nolink"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  559 */         throw new PlatformManagerException(MessageText.getString("platform.jvmopt.accesserror", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */       }
/*      */     }
/*      */     
/*  563 */     throw new PlatformManagerException(MessageText.getString("platform.jvmopt.nolinkfile"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public File getVMOptionFile()
/*      */     throws PlatformManagerException
/*      */   {
/*  572 */     checkCapability(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */     
/*  574 */     File local_options = checkAndGetLocalVMOptionFile();
/*      */     
/*  576 */     if (!local_options.exists()) {
/*      */       try
/*      */       {
/*  579 */         local_options.createNewFile();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*  585 */     return local_options;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getExplicitVMOptions()
/*      */     throws PlatformManagerException
/*      */   {
/*  593 */     checkCapability(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */     
/*      */ 
/*  596 */     File local_options = checkAndGetLocalVMOptionFile();
/*      */     
/*      */     try
/*      */     {
/*  600 */       List<String> list = new ArrayList();
/*      */       
/*  602 */       if (local_options.exists())
/*      */       {
/*  604 */         LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(local_options), "UTF-8"));
/*      */         try
/*      */         {
/*      */           for (;;)
/*      */           {
/*  609 */             String line = lnr.readLine();
/*      */             
/*  611 */             if (line == null) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/*  616 */             line = line.trim();
/*      */             
/*  618 */             if (line.length() > 0)
/*      */             {
/*  620 */               list.add(line);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  626 */           lnr.close();
/*      */         }
/*      */       }
/*      */       
/*  630 */       return (String[])list.toArray(new String[list.size()]);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  634 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.accesserror", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setExplicitVMOptions(String[] options)
/*      */     throws PlatformManagerException
/*      */   {
/*  644 */     checkCapability(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */     
/*  646 */     File local_options = checkAndGetLocalVMOptionFile();
/*      */     try
/*      */     {
/*  649 */       if (local_options.exists())
/*      */       {
/*  651 */         File backup = new File(local_options.getParentFile(), local_options.getName() + ".bak");
/*      */         
/*  653 */         if (backup.exists())
/*      */         {
/*  655 */           backup.delete();
/*      */         }
/*      */         
/*  658 */         if (!local_options.renameTo(backup))
/*      */         {
/*  660 */           throw new Exception("Failed to move " + local_options + " to " + backup);
/*      */         }
/*      */         
/*  663 */         boolean ok = false;
/*      */         
/*      */         try
/*      */         {
/*  667 */           PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(local_options), "UTF-8"));
/*      */           try
/*      */           {
/*  670 */             for (String option : options)
/*      */             {
/*  672 */               pw.println(option);
/*      */             }
/*      */             
/*  675 */             ok = true;
/*      */           }
/*      */           finally
/*      */           {
/*  679 */             pw.close();
/*      */           }
/*      */         }
/*      */         finally {
/*  683 */           if (!ok)
/*      */           {
/*  685 */             local_options.delete();
/*      */             
/*  687 */             backup.renameTo(local_options);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  693 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.accesserror", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getRunAtLogin()
/*      */     throws PlatformManagerException
/*      */   {
/*  702 */     File exe = getApplicationEXELocation();
/*      */     
/*  704 */     if ((exe != null) && (exe.exists())) {
/*      */       try
/*      */       {
/*  707 */         String value = this.access.readStringValue(4, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", this.app_name);
/*      */         
/*      */ 
/*      */ 
/*  711 */         return value.equals(exe.getAbsolutePath());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  715 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  719 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRunAtLogin(boolean run)
/*      */     throws PlatformManagerException
/*      */   {
/*  729 */     File exe = getApplicationEXELocation();
/*      */     
/*  731 */     if ((exe != null) && (exe.exists())) {
/*      */       try
/*      */       {
/*  734 */         String key = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
/*      */         
/*  736 */         if (run)
/*      */         {
/*  738 */           this.access.writeStringValue(4, key, this.app_name, exe.getAbsolutePath());
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  743 */           this.access.deleteValue(4, key, this.app_name);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  747 */         throw new PlatformManagerException("Failed to write 'run at login' key", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getShutdownTypes()
/*      */   {
/*  755 */     int result = 5;
/*      */     
/*  757 */     if (canHibernate())
/*      */     {
/*  759 */       result |= 0x2;
/*      */     }
/*      */     
/*  762 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPreventComputerSleep(boolean prevent_it)
/*      */   {
/*  778 */     synchronized (this)
/*      */     {
/*  780 */       if (this.prevent_computer_sleep == prevent_it)
/*      */       {
/*  782 */         return;
/*      */       }
/*      */       
/*  785 */       this.prevent_computer_sleep = prevent_it;
/*      */       
/*  787 */       if (prevent_it)
/*      */       {
/*  789 */         if (this.prevent_sleep_thread == null)
/*      */         {
/*  791 */           this.prevent_sleep_thread = new AEThread2("SleepPreventer")
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */               for (;;)
/*      */               {
/*  798 */                 synchronized (PlatformManagerImpl.this)
/*      */                 {
/*  800 */                   if (!PlatformManagerImpl.this.prevent_computer_sleep)
/*      */                   {
/*  802 */                     if (PlatformManagerImpl.this.prevent_sleep_thread == this)
/*      */                     {
/*  804 */                       PlatformManagerImpl.this.prevent_sleep_thread = null;
/*      */                     }
/*      */                     
/*  807 */                     return;
/*      */                   }
/*      */                 }
/*      */                 try
/*      */                 {
/*  812 */                   PlatformManagerImpl.this.access.setThreadExecutionState(1);
/*      */                   
/*  814 */                   Thread.sleep(30000L);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*  818 */                   Debug.out(e);
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             }
/*      */             
/*  825 */           };
/*  826 */           this.prevent_sleep_thread.start();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean canHibernate()
/*      */   {
/*      */     try
/*      */     {
/*  836 */       if (Constants.isWindows7OrHigher)
/*      */       {
/*  838 */         int enabled = this.access.readWordValue(3, "System\\CurrentControlSet\\Control\\Power", "HibernateEnabled");
/*      */         
/*  840 */         return enabled != 0;
/*      */       }
/*      */       
/*  843 */       Process p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/C", "reg query \"HKLM\\System\\CurrentControlSet\\Control\\Session Manager\\Power\" /v Heuristics" });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  850 */       LineNumberReader lnr = new LineNumberReader(new InputStreamReader(p.getInputStream()));
/*      */       
/*      */       for (;;)
/*      */       {
/*  854 */         String line = lnr.readLine();
/*      */         
/*  856 */         if (line == null) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*  861 */         line = line.trim();
/*      */         
/*  863 */         if (line.startsWith("Heuristics"))
/*      */         {
/*  865 */           String[] bits = line.split("[\\s]+");
/*      */           
/*  867 */           byte[] value = ByteFormatter.decodeString(bits[2].trim());
/*      */           
/*  869 */           return (value[6] & 0x1) != 0;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  874 */       return false;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  878 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void startup(final AzureusCore azureus_core)
/*      */     throws PlatformManagerException
/*      */   {
/*  888 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  890 */     if (!hasCapability(PlatformManagerCapabilities.AccessExplicitVMOptions))
/*      */     {
/*  892 */       return;
/*      */     }
/*      */     
/*  895 */     if (COConfigurationManager.getBooleanParameter("platform.win32.vmo.migrated", false))
/*      */     {
/*      */       try {
/*  898 */         File local_options = checkAndGetLocalVMOptionFile();
/*      */         
/*  900 */         if (local_options.exists())
/*      */         {
/*  902 */           File last_good = new File(local_options.getParentFile(), local_options.getName() + ".lastgood");
/*      */           
/*  904 */           if ((!last_good.exists()) || (local_options.lastModified() > last_good.lastModified()))
/*      */           {
/*      */ 
/*  907 */             FileUtil.copyFile(local_options, last_good);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  912 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     else {
/*  916 */       final int fail_count = COConfigurationManager.getIntParameter("platform.win32.vmo.migrated.fails", 0);
/*      */       
/*  918 */       if (fail_count >= 3)
/*      */       {
/*  920 */         Debug.out("Not attempting vmoption migration due to previous failures, please perform a full install to fix this");
/*      */         
/*  922 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  927 */       PluginInterface pi = azureus_core.getPluginManager().getPluginInterfaceByID("azupdater");
/*      */       
/*  929 */       if ((pi != null) && (Constants.compareVersions(pi.getPluginVersion(), "1.8.15") >= 0))
/*      */       {
/*  931 */         new AEThread2("win32.vmo", true)
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/*  937 */               String redirect = PlatformManagerImpl.this.getJVMOptionRedirect();
/*      */               
/*  939 */               File[] option_files = PlatformManagerImpl.this.getJVMOptionFiles();
/*      */               
/*  941 */               if (option_files.length != 2)
/*      */               {
/*  943 */                 return;
/*      */               }
/*      */               
/*  946 */               File shared_options = option_files[0];
/*  947 */               File old_shared_options = new File(shared_options.getParentFile(), shared_options.getName() + ".old");
/*  948 */               File local_options = option_files[1];
/*      */               
/*  950 */               if (shared_options.exists())
/*      */               {
/*  952 */                 String options = FileUtil.readFileAsString(shared_options, -1);
/*      */                 
/*  954 */                 if (!options.contains(redirect))
/*      */                 {
/*      */ 
/*      */ 
/*  958 */                   if (!options.contains("-include-options"))
/*      */                   {
/*  960 */                     if (FileUtil.canReallyWriteToAppDirectory())
/*      */                     {
/*  962 */                       if (old_shared_options.exists())
/*      */                       {
/*  964 */                         old_shared_options.delete();
/*      */                       }
/*      */                       
/*  967 */                       if (shared_options.renameTo(old_shared_options))
/*      */                       {
/*  969 */                         if (!local_options.exists())
/*      */                         {
/*  971 */                           if (!FileUtil.copyFile(old_shared_options, local_options))
/*      */                           {
/*  973 */                             Debug.out("Failed to copy " + old_shared_options + " to " + local_options);
/*      */                           }
/*      */                         }
/*      */                         
/*  977 */                         if (!FileUtil.writeStringAsFile(shared_options, redirect + "\r\n"))
/*      */                         {
/*  979 */                           Debug.out("Failed to write to " + shared_options);
/*      */                         }
/*      */                       }
/*      */                       else {
/*  983 */                         Debug.out("Rename of " + shared_options + " to " + old_shared_options + " failed");
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     else
/*      */                     {
/*  989 */                       UpdateInstaller installer = PlatformManagerImpl.this.getInstaller(azureus_core);
/*      */                       
/*      */ 
/*      */ 
/*  993 */                       if (installer == null)
/*      */                       {
/*  995 */                         return;
/*      */                       }
/*      */                       
/*      */ 
/*  999 */                       if (!PlatformManagerImpl.this.informUpdateRequired())
/*      */                       {
/* 1001 */                         return;
/*      */                       }
/*      */                       
/* 1004 */                       if (old_shared_options.exists())
/*      */                       {
/* 1006 */                         installer.addRemoveAction(old_shared_options.getAbsolutePath());
/*      */                       }
/*      */                       
/* 1009 */                       installer.addMoveAction(shared_options.getAbsolutePath(), old_shared_options.getAbsolutePath());
/*      */                       
/* 1011 */                       if (!local_options.exists())
/*      */                       {
/* 1013 */                         installer.addResource("local_options", new ByteArrayInputStream(options.getBytes("UTF-8")));
/*      */                         
/* 1015 */                         installer.addMoveAction("local_options", local_options.getAbsolutePath());
/*      */                       }
/*      */                       
/* 1018 */                       installer.addResource("redirect", new ByteArrayInputStream((redirect + "\r\n").getBytes("UTF-8")));
/*      */                       
/* 1020 */                       installer.addMoveAction("redirect", shared_options.getAbsolutePath());
/*      */                       
/* 1022 */                       final AESemaphore sem = new AESemaphore("vmopt");
/*      */                       
/* 1024 */                       final UpdateException[] error = { null };
/*      */                       
/* 1026 */                       installer.installNow(new UpdateInstallerListener()
/*      */                       {
/*      */                         public void reportProgress(String str) {}
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void complete()
/*      */                         {
/* 1038 */                           sem.release();
/*      */                         }
/*      */                         
/*      */ 
/*      */ 
/*      */                         public void failed(UpdateException e)
/*      */                         {
/* 1045 */                           error[0] = e;
/*      */                           
/* 1047 */                           sem.release();
/*      */                         }
/*      */                         
/* 1050 */                       });
/* 1051 */                       sem.reserve();
/*      */                       
/* 1053 */                       if (error[0] != null)
/*      */                       {
/* 1055 */                         throw error[0];
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/*      */                 }
/* 1063 */                 else if ((old_shared_options.exists()) && (!local_options.exists()))
/*      */                 {
/* 1065 */                   if (!FileUtil.copyFile(old_shared_options, local_options))
/*      */                   {
/* 1067 */                     Debug.out("Failed to copy " + old_shared_options + " to " + local_options);
/*      */ 
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/* 1075 */               else if (FileUtil.canReallyWriteToAppDirectory())
/*      */               {
/* 1077 */                 if (!FileUtil.writeStringAsFile(shared_options, redirect + "\r\n"))
/*      */                 {
/* 1079 */                   Debug.out("Failed to write to " + shared_options);
/*      */                 }
/*      */                 
/*      */               }
/*      */               else
/*      */               {
/* 1085 */                 UpdateInstaller installer = PlatformManagerImpl.this.getInstaller(azureus_core);
/*      */                 
/*      */ 
/*      */ 
/* 1089 */                 if (installer == null)
/*      */                 {
/* 1091 */                   return;
/*      */                 }
/*      */                 
/*      */ 
/* 1095 */                 if (!PlatformManagerImpl.this.informUpdateRequired())
/*      */                 {
/* 1097 */                   return;
/*      */                 }
/*      */                 
/* 1100 */                 installer.addResource("redirect", new ByteArrayInputStream((redirect + "\r\n").getBytes("UTF-8")));
/*      */                 
/* 1102 */                 installer.addMoveAction("redirect", shared_options.getAbsolutePath());
/*      */                 
/* 1104 */                 final AESemaphore sem = new AESemaphore("vmopt");
/*      */                 
/* 1106 */                 final UpdateException[] error = { null };
/*      */                 
/* 1108 */                 installer.installNow(new UpdateInstallerListener()
/*      */                 {
/*      */                   public void reportProgress(String str) {}
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void complete()
/*      */                   {
/* 1120 */                     sem.release();
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */                   public void failed(UpdateException e)
/*      */                   {
/* 1127 */                     error[0] = e;
/*      */                     
/* 1129 */                     sem.release();
/*      */                   }
/*      */                   
/* 1132 */                 });
/* 1133 */                 sem.reserve();
/*      */                 
/* 1135 */                 if (error[0] != null)
/*      */                 {
/* 1137 */                   throw error[0];
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/* 1142 */               COConfigurationManager.setParameter("platform.win32.vmo.migrated", true);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1146 */               COConfigurationManager.setParameter("platform.win32.vmo.migrated.fails", fail_count + 1);
/*      */               
/* 1148 */               Debug.out("vmoption migration failed", e);
/*      */             }
/*      */           }
/*      */         }.start();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private UpdateInstaller getInstaller(AzureusCore azureus_core)
/*      */     throws Exception
/*      */   {
/* 1165 */     PluginInterface pi = azureus_core.getPluginManager().getDefaultPluginInterface();
/*      */     
/* 1167 */     UpdateManager update_manager = pi.getUpdateManager();
/*      */     
/* 1169 */     final List<UpdateCheckInstance> l_instances = new ArrayList();
/*      */     
/* 1171 */     update_manager.addListener(new UpdateManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void checkInstanceCreated(UpdateCheckInstance instance)
/*      */       {
/*      */ 
/* 1178 */         synchronized (l_instances)
/*      */         {
/* 1180 */           l_instances.add(instance);
/*      */         }
/*      */         
/*      */       }
/* 1184 */     });
/* 1185 */     UpdateCheckInstance[] instances = update_manager.getCheckInstances();
/*      */     
/* 1187 */     l_instances.addAll(Arrays.asList(instances));
/*      */     
/* 1189 */     long start = SystemTime.getMonotonousTime();
/*      */     
/*      */ 
/*      */ 
/* 1193 */     while (SystemTime.getMonotonousTime() - start < 300000L)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 1199 */         Thread.sleep(5000L);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1203 */         Debug.out(e);
/*      */         
/* 1205 */         return null;
/*      */       }
/*      */       
/* 1208 */       if (l_instances.size() > 0)
/*      */       {
/* 1210 */         boolean all_done = true;
/*      */         
/* 1212 */         for (UpdateCheckInstance instance : l_instances)
/*      */         {
/* 1214 */           if (!instance.isCompleteOrCancelled())
/*      */           {
/* 1216 */             all_done = false;
/*      */             
/* 1218 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1222 */         if (all_done) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1229 */     if (update_manager.getInstallers().length > 0)
/*      */     {
/* 1231 */       return null;
/*      */     }
/*      */     
/* 1234 */     UpdateInstaller installer = pi.getUpdateManager().createInstaller();
/*      */     
/* 1236 */     return installer;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean informUpdateRequired()
/*      */   {
/* 1242 */     UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */     
/* 1244 */     long res = ui_manager.showMessageBox("update.now.title", "update.now.desc", 3L);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1249 */     return res == 1L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void shutdown(int type)
/*      */     throws PlatformManagerException
/*      */   {
/* 1258 */     String windir = System.getenv("windir");
/*      */     
/* 1260 */     boolean vista_or_higher = Constants.isWindowsVistaOrHigher;
/*      */     try
/*      */     {
/* 1263 */       if (type == 4)
/*      */       {
/* 1265 */         Runtime.getRuntime().exec(new String[] { windir + "\\system32\\rundll32.exe", "powrprof.dll,SetSuspendState Sleep" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1271 */       else if (type == 2)
/*      */       {
/* 1273 */         if (vista_or_higher)
/*      */         {
/* 1275 */           Runtime.getRuntime().exec(new String[] { "shutdown", "-h" });
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/* 1283 */           Runtime.getRuntime().exec(new String[] { windir + "system32\\rundll32.exe", "powrprof.dll,SetSuspendState Hibernate" });
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 1289 */       else if (type == 1)
/*      */       {
/* 1291 */         Runtime.getRuntime().exec(new String[] { "shutdown", "-s" });
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1298 */         throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */       }
/*      */     }
/*      */     catch (PlatformManagerException e)
/*      */     {
/* 1303 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1307 */       throw new PlatformManagerException("shutdown failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */   public String getApplicationCommandLine()
/*      */   {
/*      */     try
/*      */     {
/* 1315 */       return getApplicationEXELocation().toString();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1319 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isApplicationRegistered()
/*      */     throws PlatformManagerException
/*      */   {
/* 1330 */     File exe_loc = getApplicationEXELocation();
/*      */     
/* 1332 */     if (exe_loc.exists())
/*      */     {
/* 1334 */       checkExeKey(exe_loc);
/*      */     }
/*      */     
/* 1337 */     String app_path = SystemProperties.getApplicationPath();
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1342 */       registerMagnet(false);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1346 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1352 */       if (getAdditionalFileTypeRegistrationDetails("DHT", ".dht") == 0)
/*      */       {
/* 1354 */         registerDHT();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1358 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1364 */       registerBC();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1368 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1371 */     if (isAdditionalFileTypeRegistered("BitTorrent", ".torrent"))
/*      */     {
/* 1373 */       unregisterAdditionalFileType("BitTorrent", ".torrent");
/*      */       
/* 1375 */       registerAdditionalFileType("Azureus", Constants.APP_NAME + " Download", ".torrent", "application/x-bittorrent");
/*      */     }
/*      */     
/* 1378 */     boolean reg = isAdditionalFileTypeRegistered("Azureus", ".torrent");
/*      */     
/*      */ 
/*      */ 
/* 1382 */     if ((!reg) && (!COConfigurationManager.getBooleanParameter("platform.win32.autoregdone", false)))
/*      */     {
/* 1384 */       registerAdditionalFileType("Azureus", Constants.APP_NAME + " Download", ".torrent", "application/x-bittorrent");
/*      */       
/* 1386 */       COConfigurationManager.setParameter("platform.win32.autoregdone", true);
/*      */       
/* 1388 */       reg = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1393 */     boolean vuze_reg = isAdditionalFileTypeRegistered("Vuze", ".vuze");
/*      */     
/* 1395 */     if (!vuze_reg)
/*      */     {
/* 1397 */       registerAdditionalFileType("Vuze", "Vuze File", ".vuze", "application/x-vuze");
/*      */     }
/*      */     
/* 1400 */     return reg;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkExeKey(File exe)
/*      */   {
/* 1407 */     checkExeKey(4, exe);
/* 1408 */     checkExeKey(3, exe);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkExeKey(int hkey, File exe)
/*      */   {
/* 1416 */     String exe_str = exe.getAbsolutePath();
/* 1417 */     String path_str = exe.getParent();
/*      */     
/* 1419 */     String execReg = null;
/* 1420 */     String parentReg = null;
/*      */     try
/*      */     {
/* 1423 */       execReg = this.access.readStringValue(hkey, "software\\" + this.app_name, "exec");
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */     try
/*      */     {
/* 1429 */       parentReg = this.access.readStringValue(hkey, "software\\" + this.app_name, "");
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */     try
/*      */     {
/* 1435 */       if ((execReg == null) || (!execReg.equals(exe_str)))
/*      */       {
/* 1437 */         this.access.writeStringValue(hkey, "software\\" + this.app_name, "exec", exe_str);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     try
/*      */     {
/* 1443 */       if ((parentReg == null) || (!parentReg.equals(path_str)))
/*      */       {
/* 1445 */         this.access.writeStringValue(hkey, "software\\" + this.app_name, "", path_str);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isAdditionalFileTypeRegistered(String name, String type)
/*      */     throws PlatformManagerException
/*      */   {
/* 1458 */     return getAdditionalFileTypeRegistrationDetails(name, type) == 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getAdditionalFileTypeRegistrationDetails(String name, String type)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     String az_exe_str;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1472 */       az_exe_str = getApplicationEXELocation().toString();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1476 */       return 0;
/*      */     }
/*      */     try
/*      */     {
/* 1480 */       String test1 = this.access.readStringValue(1, name + "\\shell\\open\\command", "");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1486 */       if (!test1.equals("\"" + az_exe_str + "\" \"%1\""))
/*      */       {
/* 1488 */         return test1.length() == 0 ? 0 : 2;
/*      */       }
/*      */       
/* 1491 */       String test2 = this.access.readStringValue(1, type, "");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1496 */       if (!test2.equals("Azureus")) {
/* 1497 */         return test2.length() == 0 ? 0 : 2;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1504 */         String always_open_with = this.access.readStringValue(4, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\" + type, "Application");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1512 */         if (always_open_with.length() > 0)
/*      */         {
/*      */ 
/*      */ 
/* 1516 */           return 2;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1555 */       return 1;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1559 */       if ((e.getMessage() == null) || (!e.getMessage().contains("RegOpenKey failed")))
/*      */       {
/*      */ 
/* 1562 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/* 1565 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerApplication()
/*      */     throws PlatformManagerException
/*      */   {
/* 1574 */     registerMagnet(true);
/*      */     
/* 1576 */     registerDHT();
/*      */     
/* 1578 */     registerAdditionalFileType("Azureus", Constants.APP_NAME + " Download", ".torrent", "application/x-bittorrent");
/*      */     
/* 1580 */     registerAdditionalFileType("Vuze", "Vuze File", ".vuze", "application/x-vuze");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void registerMagnet(boolean force)
/*      */   {
/*      */     try
/*      */     {
/* 1591 */       String az_exe_string = getApplicationEXELocation().toString();
/*      */       
/* 1593 */       boolean magnet_exe_managing = false;
/*      */       try
/*      */       {
/* 1596 */         String existing = this.access.readStringValue(1, "magnet\\shell\\open\\command", "");
/*      */         
/* 1598 */         magnet_exe_managing = existing.toLowerCase().contains("\\magnet.exe");
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/* 1603 */       if (!magnet_exe_managing)
/*      */       {
/* 1605 */         if ((force) || (getAdditionalFileTypeRegistrationDetails("Magnet", ".magnet") == 0)) {
/*      */           try
/*      */           {
/* 1608 */             registerAdditionalFileType("Magnet", "URL:Magnet Protocol", ".magnet", "application/x-magnet", true);
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/*      */ 
/* 1617 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1624 */       for (int type : new int[] { 3, 4 }) {
/*      */         try
/*      */         {
/* 1627 */           createKey(type, "Software\\Magnet");
/* 1628 */           createKey(type, "Software\\Magnet\\Handlers");
/* 1629 */           createKey(type, "Software\\Magnet\\Handlers\\Azureus");
/*      */           
/* 1631 */           this.access.writeStringValue(type, "Software\\Magnet\\Handlers\\Azureus", "DefaultIcon", "\"" + az_exe_string + "\"," + getIconIndex());
/* 1632 */           this.access.writeStringValue(type, "Software\\Magnet\\Handlers\\Azureus", "Description", "Download with Vuze");
/* 1633 */           this.access.writeStringValue(type, "Software\\Magnet\\Handlers\\Azureus", "ShellExecute", "\"" + az_exe_string + "\" \"%URL\"");
/*      */           
/* 1635 */           this.access.writeWordValue(type, "Software\\Magnet\\Handlers\\Azureus\\Type", "urn:btih", 0);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean createKey(int type, String key)
/*      */   {
/*      */     try
/*      */     {
/* 1651 */       this.access.readStringValue(type, key, "");
/*      */       
/* 1653 */       return true;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */       try {
/* 1658 */         this.access.writeStringValue(type, key, "", "");
/*      */         
/* 1660 */         return true;
/*      */       }
/*      */       catch (Throwable f) {}
/*      */     }
/* 1664 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void registerDHT()
/*      */   {
/*      */     try
/*      */     {
/* 1673 */       registerAdditionalFileType("DHT", "DHT URI", ".dht", "application/x-dht", true);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 1682 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void registerBC()
/*      */   {
/*      */     try
/*      */     {
/* 1690 */       registerAdditionalFileType("BC", "BC URI", ".bcuri", "application/x-bc-uri", true);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 1699 */       Debug.printStackTrace(e);
/*      */     }
/*      */     try
/*      */     {
/* 1703 */       registerAdditionalFileType("BCTP", "BCTP URI", ".bctpuri", "application/x-bctp-uri", true);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 1712 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerAdditionalFileType(String name, String description, String type, String content_type)
/*      */     throws PlatformManagerException
/*      */   {
/* 1725 */     registerAdditionalFileType(name, description, type, content_type, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerAdditionalFileType(String name, String description, String type, String content_type, boolean url_protocol)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1747 */       String az_exe_string = getApplicationEXELocation().toString();
/*      */       
/* 1749 */       unregisterAdditionalFileType(name, type);
/*      */       
/* 1751 */       writeStringToHKCRandHKCU(type, "", name);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1756 */       writeStringToHKCRandHKCU(type, "Content Type", content_type);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1761 */       writeStringToHKCRandHKCU("MIME\\Database\\Content Type\\" + content_type, "Extension", type);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1766 */       writeStringToHKCRandHKCU(name, "", description);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1771 */       writeStringToHKCRandHKCU(name + "\\shell", "", "open");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1776 */       writeStringToHKCRandHKCU(name + "\\DefaultIcon", "", "\"" + az_exe_string + "\"," + getIconIndex());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1781 */       writeStringToHKCRandHKCU(name + "\\shell\\open\\command", "", "\"" + az_exe_string + "\" \"%1\"");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1786 */       writeStringToHKCRandHKCU(name, "Content Type", content_type);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1791 */       if (url_protocol)
/*      */       {
/* 1793 */         writeStringToHKCRandHKCU(name, "URL Protocol", "");
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (PlatformManagerException e)
/*      */     {
/*      */ 
/* 1801 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1805 */       throw new PlatformManagerException("Failed to write registry details", e);
/*      */     }
/*      */   }
/*      */   
/*      */   private void writeStringToHKCRandHKCU(String subkey, String name, String value)
/*      */   {
/*      */     try {
/* 1812 */       this.access.writeStringValue(1, subkey, name, value);
/*      */     } catch (Throwable e) {
/* 1814 */       if (!Constants.isWindowsVistaOrHigher) {
/* 1815 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1820 */       this.access.writeStringValue(4, "Software\\Classes\\" + subkey, name, value);
/*      */     }
/*      */     catch (Throwable e) {
/* 1823 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void unregisterAdditionalFileType(String name, String type)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/* 1837 */         this.access.deleteValue(4, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\" + type, "Application");
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1848 */         this.access.deleteKey(1, type);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1858 */         this.access.deleteKey(1, name, true);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1869 */         this.access.deleteKey(4, "Software\\Classes\\" + type);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1879 */         this.access.deleteKey(4, "Software\\Classes\\" + name, true);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e) {}
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 1891 */       throw new PlatformManagerException("Failed to delete registry details", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void createProcess(String command_line, boolean inherit_handles)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1903 */       this.access.createProcess(command_line, inherit_handles);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1907 */       throw new PlatformManagerException("Failed to create process", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void performRecoverableFileDelete(String file_name)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1918 */       this.access.moveToRecycleBin(file_name);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1922 */       throw new PlatformManagerException("Failed to move file", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTCPTOSEnabled(boolean enabled)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1933 */       this.access.writeWordValue(3, "System\\CurrentControlSet\\Services\\Tcpip\\Parameters", "DisableUserTOSSetting", enabled ? 0 : 1);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 1941 */       throw new PlatformManagerException("Failed to write registry details", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyFilePermissions(String from_file_name, String to_file_name)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1953 */       this.access.copyFilePermissions(from_file_name, to_file_name);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1957 */       throw new PlatformManagerException("Failed to copy file permissions", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void showFile(String file_name)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1970 */       File file = new File(file_name);
/*      */       
/* 1972 */       this.access.createProcess("explorer.exe " + (file.isDirectory() ? "/e," : "/e,/select,") + "\"" + file_name + "\"", false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1983 */       throw new PlatformManagerException("Failed to show file " + file_name, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean testNativeAvailability(String name)
/*      */     throws PlatformManagerException
/*      */   {
/* 1993 */     if (!hasCapability(PlatformManagerCapabilities.TestNativeAvailability))
/*      */     {
/* 1995 */       throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */     }
/*      */     try
/*      */     {
/* 1999 */       return this.access.testNativeAvailability(name);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2003 */       throw new PlatformManagerException("Failed to test availability", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void traceRoute(InetAddress interface_address, InetAddress target, PlatformManagerPingCallback callback)
/*      */     throws PlatformManagerException
/*      */   {
/* 2015 */     if (!hasCapability(PlatformManagerCapabilities.TraceRouteAvailability))
/*      */     {
/* 2017 */       throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */     }
/*      */     try
/*      */     {
/* 2021 */       this.access.traceRoute(interface_address, target, callback);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2025 */       throw new PlatformManagerException("Failed to trace route", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void ping(InetAddress interface_address, InetAddress target, PlatformManagerPingCallback callback)
/*      */     throws PlatformManagerException
/*      */   {
/* 2037 */     if (!hasCapability(PlatformManagerCapabilities.PingAvailability))
/*      */     {
/* 2039 */       throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */     }
/*      */     try
/*      */     {
/* 2043 */       this.access.ping(interface_address, target, callback);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2047 */       throw new PlatformManagerException("Failed to trace route", e);
/*      */     }
/*      */   }
/*      */   
/*      */   public int shellExecute(String operation, String file, String parameters, String directory, int SW_const) throws PlatformManagerException
/*      */   {
/*      */     try {
/* 2054 */       return this.access.shellExecute(operation, file, parameters, directory, SW_const);
/*      */     } catch (Throwable e) {
/* 2056 */       throw new PlatformManagerException("Failed to shellExecute", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxOpenFiles()
/*      */     throws PlatformManagerException
/*      */   {
/* 2065 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasCapability(PlatformManagerCapabilities capability)
/*      */   {
/* 2075 */     return this.capabilitySet.contains(capability);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkCapability(PlatformManagerCapabilities capability)
/*      */     throws PlatformManagerException
/*      */   {
/* 2084 */     if (!hasCapability(capability))
/*      */     {
/* 2086 */       throw new PlatformManagerException("Capability " + capability + " not supported");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int eventOccurred(int type)
/*      */   {
/* 2101 */     int t_type = -1;
/* 2102 */     int res = -1;
/*      */     
/* 2104 */     if (type == 1)
/*      */     {
/* 2106 */       t_type = 1;
/*      */     }
/* 2108 */     else if (type == 2)
/*      */     {
/* 2110 */       t_type = 2;
/*      */       
/* 2112 */       synchronized (this)
/*      */       {
/* 2114 */         if (this.prevent_computer_sleep)
/*      */         {
/* 2116 */           res = 1;
/*      */         }
/*      */       }
/* 2119 */     } else if (type == 3)
/*      */     {
/* 2121 */       t_type = 3;
/*      */     }
/*      */     
/* 2124 */     if (t_type != -1)
/*      */     {
/* 2126 */       for (int i = 0; i < this.listeners.size(); i++) {
/*      */         try
/*      */         {
/* 2129 */           int my_res = ((PlatformManagerListener)this.listeners.get(i)).eventOccurred(t_type);
/*      */           
/* 2131 */           if (my_res == 1)
/*      */           {
/* 2133 */             res = 1;
/*      */           }
/* 2135 */           else if (my_res != -1)
/*      */           {
/* 2137 */             if ((res != -1) && (my_res != res))
/*      */             {
/* 2139 */               Debug.out("Incompatible result codes: " + res + "/" + my_res);
/*      */             }
/*      */             else
/*      */             {
/* 2143 */               res = my_res;
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2149 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2154 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(PlatformManagerListener listener)
/*      */   {
/* 2161 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(PlatformManagerListener listener)
/*      */   {
/* 2168 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */   public void requestUserAttention(int type, Object data) throws PlatformManagerException
/*      */   {
/* 2173 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Class<?> loadClass(ClassLoader loader, String class_name)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 2184 */       return loader.loadClass(class_name);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2188 */       throw new PlatformManagerException("load of '" + class_name + "' failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2196 */     writer.println("Platform");
/*      */     try
/*      */     {
/* 2199 */       writer.indent();
/*      */       try
/*      */       {
/* 2202 */         String[] options = getExplicitVMOptions();
/*      */         
/* 2204 */         writer.println("VM Options");
/*      */         try
/*      */         {
/* 2207 */           writer.indent();
/*      */           
/* 2209 */           for (String option : options)
/*      */           {
/* 2211 */             writer.println(option);
/*      */           }
/*      */           
/*      */         }
/*      */         finally {}
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2219 */         writer.println("VM options not available: " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2224 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean getPreventComputerSleep()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 1089	org/gudy/azureus2/platform/win32/PlatformManagerImpl:prevent_computer_sleep	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #768	-> byte code offset #0
/*      */     //   Java source line #770	-> byte code offset #4
/*      */     //   Java source line #771	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	PlatformManagerImpl
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */   
/*      */   public void dispose() {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/PlatformManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */