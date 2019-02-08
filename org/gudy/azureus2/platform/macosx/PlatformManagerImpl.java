/*      */ package org.gudy.azureus2.platform.macosx;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*      */ import org.gudy.azureus2.platform.PlatformManagerListener;
/*      */ import org.gudy.azureus2.platform.PlatformManagerPingCallback;
/*      */ import org.gudy.azureus2.platform.macosx.access.jnilib.OSXAccess;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*      */ 
/*      */ public class PlatformManagerImpl
/*      */   implements PlatformManager, AEDiagnosticsEvidenceGenerator
/*      */ {
/*   49 */   private static final LogIDs LOGID = LogIDs.CORE;
/*      */   
/*      */   private static final String BUNDLE_ID = "com.azureus.vuze";
/*      */   
/*   53 */   private static final String[] SCHEMES = { "magnet", "dht", "vuze", "bc", "bctp" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   61 */   private static final String[] MIMETYPES = { "application/x-bittorrent", "application/x-vuze", "application/x-bctp-uri", "application/x-bc-uri" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   68 */   private static final String[] EXTENSIONS = { "torrent", "tor", "vuze", "vuz", "bctpuri", "bcuri" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static PlatformManagerImpl singleton;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   78 */   protected static AEMonitor class_mon = new AEMonitor("PlatformManager");
/*      */   
/*   80 */   private static String fileBrowserName = "Finder";
/*      */   
/*      */ 
/*   83 */   private final HashSet capabilitySet = new HashSet();
/*      */   
/*      */   private volatile String computer_name;
/*      */   
/*      */   private volatile boolean computer_name_tried;
/*      */   
/*      */   private Class<?> claFileManager;
/*      */   
/*      */   private AzureusCore azureus_core;
/*   92 */   private boolean prevent_computer_sleep_pending = false;
/*   93 */   private boolean prevent_computer_sleep = false;
/*      */   
/*      */ 
/*      */   private Process prevent_computer_proc;
/*      */   
/*      */ 
/*      */   public static PlatformManagerImpl getSingleton()
/*      */   {
/*  101 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*  109 */     initializeSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void initializeSingleton()
/*      */   {
/*      */     try
/*      */     {
/*  119 */       class_mon.enter();
/*  120 */       singleton = new PlatformManagerImpl();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  124 */       Logger.log(new LogEvent(LOGID, "Failed to initialize platform manager for Mac OS X", e));
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  129 */       class_mon.exit();
/*      */     }
/*      */     
/*  132 */     COConfigurationManager.addAndFireParameterListener("FileBrowse.usePathFinder", new ParameterListener() {
/*      */       public void parameterChanged(String parameterName) {
/*  134 */         PlatformManagerImpl.access$002(COConfigurationManager.getBooleanParameter("FileBrowse.usePathFinder") ? "Path Finder" : "Finder");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PlatformManagerImpl()
/*      */   {
/*  145 */     this.capabilitySet.add(PlatformManagerCapabilities.RecoverableFileDelete);
/*  146 */     this.capabilitySet.add(PlatformManagerCapabilities.ShowFileInBrowser);
/*  147 */     this.capabilitySet.add(PlatformManagerCapabilities.ShowPathInCommandLine);
/*  148 */     this.capabilitySet.add(PlatformManagerCapabilities.CreateCommandLineProcess);
/*  149 */     this.capabilitySet.add(PlatformManagerCapabilities.GetUserDataDirectory);
/*  150 */     this.capabilitySet.add(PlatformManagerCapabilities.UseNativeScripting);
/*  151 */     this.capabilitySet.add(PlatformManagerCapabilities.PlaySystemAlert);
/*  152 */     this.capabilitySet.add(PlatformManagerCapabilities.RequestUserAttention);
/*      */     
/*  154 */     if (OSXAccess.isLoaded()) {
/*  155 */       this.capabilitySet.add(PlatformManagerCapabilities.GetVersion);
/*      */       try {
/*  157 */         if (OSXAccess.canSetDefaultApp()) {
/*  158 */           this.capabilitySet.add(PlatformManagerCapabilities.RegisterFileAssociations);
/*      */         }
/*      */       }
/*      */       catch (Throwable t) {}
/*      */     }
/*      */     
/*      */ 
/*  165 */     if (hasVMOptions()) {
/*  166 */       this.capabilitySet.add(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */     }
/*      */     
/*  169 */     this.capabilitySet.add(PlatformManagerCapabilities.RunAtLogin);
/*  170 */     this.capabilitySet.add(PlatformManagerCapabilities.GetMaxOpenFiles);
/*      */     
/*  172 */     if ((new File("/usr/bin/pmset").canRead()) || (new File("/usr/bin/caffeinate").canRead()))
/*      */     {
/*      */ 
/*  175 */       this.capabilitySet.add(PlatformManagerCapabilities.PreventComputerSleep);
/*      */     }
/*      */     try
/*      */     {
/*  179 */       if (new File("/usr/bin/defaults").exists())
/*      */       {
/*  181 */         boolean found = false;
/*      */         try
/*      */         {
/*  184 */           String[] read_command = { "/usr/bin/defaults", "read", "com.azureus.vuze" };
/*      */           
/*  186 */           Process p = Runtime.getRuntime().exec(read_command);
/*      */           
/*  188 */           if (p.waitFor() == 0)
/*      */           {
/*  190 */             InputStream is = p.getInputStream();
/*      */             
/*  192 */             LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
/*      */             
/*      */             for (;;)
/*      */             {
/*  196 */               String line = lnr.readLine();
/*      */               
/*  198 */               if (line == null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*  203 */               if (line.contains("NSAppSleepDisabled"))
/*      */               {
/*  205 */                 found = true;
/*      */                 
/*  207 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  213 */           e.printStackTrace();
/*      */         }
/*      */         
/*  216 */         if (!found)
/*      */         {
/*  218 */           String[] write_command = { "/usr/bin/defaults", "write", "com.azureus.vuze", "NSAppSleepDisabled", "-bool", "YES" };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  227 */           Runtime.getRuntime().exec(write_command);
/*      */         }
/*      */       }
/*      */       else {
/*  231 */         System.err.println("/usr/bin/defaults missing");
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  235 */       e.printStackTrace();
/*      */     }
/*      */     
/*  238 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPlatformType()
/*      */   {
/*  246 */     return 3;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getVersion()
/*      */     throws PlatformManagerException
/*      */   {
/*  254 */     if (!OSXAccess.isLoaded()) {
/*  255 */       throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */     }
/*      */     
/*  258 */     return OSXAccess.getVersion();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected PListEditor getPList()
/*      */     throws IOException
/*      */   {
/*  266 */     String plist = SystemProperties.getApplicationPath() + SystemProperties.getApplicationName() + ".app/Contents/Info.plist";
/*      */     
/*      */ 
/*  269 */     File plist_file = new File(plist);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  274 */     if (!plist_file.canWrite()) {
/*  275 */       return null;
/*      */     }
/*      */     
/*  278 */     PListEditor editor = new PListEditor(plist);
/*      */     
/*  280 */     return editor;
/*      */   }
/*      */   
/*      */   protected boolean checkPList()
/*      */   {
/*      */     try
/*      */     {
/*  287 */       PListEditor editor = getPList();
/*      */       
/*  289 */       if (editor == null)
/*      */       {
/*  291 */         return false;
/*      */       }
/*      */       
/*  294 */       editor.setFileTypeExtensions(EXTENSIONS);
/*  295 */       editor.setSimpleStringValue("CFBundleName", "Vuze");
/*  296 */       editor.setSimpleStringValue("CFBundleTypeName", "Vuze Download");
/*  297 */       editor.setSimpleStringValue("CFBundleGetInfoString", "Vuze");
/*  298 */       editor.setSimpleStringValue("CFBundleShortVersionString", "5.7.6.0");
/*  299 */       editor.setSimpleStringValue("CFBundleVersion", "5.7.6.0");
/*  300 */       editor.setArrayValues("CFBundleURLSchemes", "string", SCHEMES);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  305 */       editor.touchFile();
/*      */       
/*  307 */       return true;
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*  313 */       System.err.println("Failed to update plist");
/*  314 */       e.printStackTrace();
/*      */     }
/*  316 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void touchPList()
/*      */   {
/*      */     try
/*      */     {
/*  324 */       PListEditor editor = getPList();
/*      */       
/*  326 */       if (editor != null)
/*      */       {
/*  328 */         editor.touchFile();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  332 */       System.err.println("Failed to touch plist");
/*  333 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public File getVMOptionFile()
/*      */     throws PlatformManagerException
/*      */   {
/*  342 */     checkCapability(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */     
/*  344 */     File local_options = checkAndGetLocalVMOptionFile();
/*      */     
/*  346 */     if (!local_options.exists()) {
/*      */       try
/*      */       {
/*  349 */         local_options.createNewFile();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*  355 */     return local_options;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkCapability(PlatformManagerCapabilities capability)
/*      */     throws PlatformManagerException
/*      */   {
/*  364 */     if (!hasCapability(capability))
/*      */     {
/*  366 */       throw new PlatformManagerException("Capability " + capability + " not supported");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getExplicitVMOptions()
/*      */     throws PlatformManagerException
/*      */   {
/*  375 */     checkCapability(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */     
/*      */ 
/*  378 */     File local_options = checkAndGetLocalVMOptionFile();
/*      */     
/*      */     try
/*      */     {
/*  382 */       List<String> list = new ArrayList();
/*      */       
/*  384 */       if (local_options.exists())
/*      */       {
/*  386 */         LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(local_options), "UTF-8"));
/*      */         try
/*      */         {
/*      */           for (;;)
/*      */           {
/*  391 */             String line = lnr.readLine();
/*      */             
/*  393 */             if (line == null) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/*  398 */             line = line.trim();
/*      */             
/*  400 */             if (line.length() > 0)
/*      */             {
/*  402 */               list.add(line);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  408 */           lnr.close();
/*      */         }
/*      */       }
/*      */       
/*  412 */       return (String[])list.toArray(new String[list.size()]);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  416 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.accesserror", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setExplicitVMOptions(String[] options)
/*      */     throws PlatformManagerException
/*      */   {
/*  427 */     checkCapability(PlatformManagerCapabilities.AccessExplicitVMOptions);
/*      */     
/*  429 */     File local_options = checkAndGetLocalVMOptionFile();
/*      */     try
/*      */     {
/*  432 */       if (local_options.exists())
/*      */       {
/*  434 */         File backup = new File(local_options.getParentFile(), local_options.getName() + ".bak");
/*      */         
/*  436 */         if (backup.exists())
/*      */         {
/*  438 */           backup.delete();
/*      */         }
/*      */         
/*  441 */         if (!local_options.renameTo(backup))
/*      */         {
/*  443 */           throw new Exception("Failed to move " + local_options + " to " + backup);
/*      */         }
/*      */         
/*  446 */         boolean ok = false;
/*      */         
/*      */         try
/*      */         {
/*  450 */           PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(local_options), "UTF-8"));
/*      */           try
/*      */           {
/*  453 */             for (String option : options)
/*      */             {
/*  455 */               pw.println(option);
/*      */             }
/*      */             
/*  458 */             ok = true;
/*      */           }
/*      */           finally
/*      */           {
/*  462 */             pw.close();
/*      */           }
/*      */         }
/*      */         finally {
/*  466 */           if (!ok)
/*      */           {
/*  468 */             local_options.delete();
/*      */             
/*  470 */             backup.renameTo(local_options);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  476 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.accesserror", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private File checkAndGetLocalVMOptionFile()
/*      */     throws PlatformManagerException
/*      */   {
/*  485 */     String vendor = System.getProperty("java.vendor", "<unknown>");
/*      */     
/*  487 */     if ((!vendor.toLowerCase().startsWith("sun ")) && (!vendor.toLowerCase().startsWith("oracle ")))
/*      */     {
/*  489 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.sunonly", new String[] { vendor }));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  495 */     File[] option_files = getJVMOptionFiles();
/*      */     
/*  497 */     if (option_files.length != 2)
/*      */     {
/*  499 */       throw new PlatformManagerException(MessageText.getString("platform.jvmopt.configerror"));
/*      */     }
/*      */     
/*      */ 
/*  503 */     File shared_options = option_files[0];
/*      */     
/*  505 */     if (shared_options.exists()) {
/*      */       try
/*      */       {
/*  508 */         String s_options = FileUtil.readFileAsString(shared_options, -1);
/*      */         
/*  510 */         if (s_options.contains(getJVMOptionRedirect()))
/*      */         {
/*  512 */           return option_files[1];
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  518 */         throw new PlatformManagerException(MessageText.getString("platform.jvmopt.nolink"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  522 */         throw new PlatformManagerException(MessageText.getString("platform.jvmopt.accesserror", new String[] { Debug.getNestedExceptionMessage(e) }));
/*      */       }
/*      */     }
/*      */     
/*  526 */     throw new PlatformManagerException(MessageText.getString("platform.jvmopt.nolinkfile"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getJVMOptionRedirect()
/*      */   {
/*  533 */     return "-include-options ${HOME}/Library/Application Support/" + SystemProperties.getApplicationName() + "/java.vmoptions";
/*      */   }
/*      */   
/*      */   private boolean hasVMOptions()
/*      */   {
/*  538 */     File fileVMOption = FileUtil.getApplicationFile("java.vmoptions");
/*  539 */     return fileVMOption.exists();
/*      */   }
/*      */   
/*      */ 
/*      */   private File[] getJVMOptionFiles()
/*      */   {
/*      */     try
/*      */     {
/*  547 */       File shared_options = FileUtil.getApplicationFile("java.vmoptions");
/*      */       
/*      */ 
/*      */ 
/*  551 */       File local_options = new File(getLocation(1L), "java.vmoptions");
/*      */       
/*  553 */       return new File[] { shared_options, local_options };
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  557 */     return new File[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void startup(AzureusCore _azureus_core)
/*      */     throws PlatformManagerException
/*      */   {
/*  568 */     synchronized (this)
/*      */     {
/*  570 */       this.azureus_core = _azureus_core;
/*      */       
/*  572 */       if (this.prevent_computer_sleep_pending)
/*      */       {
/*  574 */         this.prevent_computer_sleep_pending = false;
/*      */         
/*  576 */         setPreventComputerSleep(true);
/*      */       }
/*      */     }
/*      */     
/*  580 */     this.azureus_core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void stopping(AzureusCore core)
/*      */       {
/*      */ 
/*  587 */         synchronized (PlatformManagerImpl.this)
/*      */         {
/*      */           try {
/*  590 */             PlatformManagerImpl.this.setPreventComputerSleep(false);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/*      */ 
/*  595 */           PlatformManagerImpl.this.azureus_core = null;
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public int getShutdownTypes()
/*      */   {
/*  604 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void shutdown(int type)
/*      */     throws PlatformManagerException
/*      */   {
/*  613 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPreventComputerSleep(boolean prevent_it)
/*      */     throws PlatformManagerException
/*      */   {
/*  622 */     synchronized (this)
/*      */     {
/*  624 */       if (this.azureus_core == null)
/*      */       {
/*  626 */         this.prevent_computer_sleep_pending = prevent_it;
/*      */         
/*  628 */         return;
/*      */       }
/*      */       
/*  631 */       if (this.prevent_computer_sleep == prevent_it)
/*      */       {
/*  633 */         return;
/*      */       }
/*      */       
/*  636 */       this.prevent_computer_sleep = prevent_it;
/*      */       
/*  638 */       if (prevent_it)
/*      */       {
/*      */ 
/*      */ 
/*  642 */         File binary = new File("/usr/bin/caffeinate");
/*      */         String[] command;
/*  644 */         if (binary.canRead())
/*      */         {
/*  646 */           command = new String[] { binary.getAbsolutePath(), "-i" };
/*      */         }
/*      */         else
/*      */         {
/*  650 */           binary = new File("/usr/bin/pmset");
/*      */           String[] command;
/*  652 */           if (binary.canRead())
/*      */           {
/*  654 */             command = new String[] { binary.getAbsolutePath(), "noidle" };
/*      */           }
/*      */           else
/*      */           {
/*  658 */             throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */           }
/*      */         }
/*      */         String[] command;
/*  662 */         if (this.prevent_computer_proc != null)
/*      */         {
/*  664 */           Debug.out("eh?");
/*      */           
/*  666 */           this.prevent_computer_proc.destroy();
/*      */         }
/*      */         try
/*      */         {
/*  670 */           System.out.println("Starting idle sleep preventer: " + command[0]);
/*      */           
/*  672 */           this.prevent_computer_proc = Runtime.getRuntime().exec(command);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  676 */           Debug.out(e);
/*      */         }
/*      */         
/*      */       }
/*  680 */       else if (this.prevent_computer_proc != null)
/*      */       {
/*  682 */         System.out.println("Stopping idle sleep preventer");
/*      */         
/*  684 */         this.prevent_computer_proc.destroy();
/*      */         
/*  686 */         this.prevent_computer_proc = null;
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
/*      */   public void setRunAtLogin(boolean run)
/*      */     throws PlatformManagerException
/*      */   {
/*  801 */     if (getRunAtLogin() == run)
/*      */     {
/*  803 */       return;
/*      */     }
/*      */     
/*  806 */     File bundle_file = getAbsoluteBundleFile();
/*      */     
/*  808 */     if (!bundle_file.exists())
/*      */     {
/*  810 */       throw new PlatformManagerException("Failed to write set run-at-login, bundle not found");
/*      */     }
/*      */     
/*  813 */     String abs_target = bundle_file.getAbsolutePath();
/*      */     
/*  815 */     if (Constants.isOSX_10_8_OrHigher)
/*      */     {
/*  817 */       if (run)
/*      */       {
/*      */         try
/*      */         {
/*  821 */           StringBuffer sb = new StringBuffer();
/*  822 */           sb.append("tell application \"");
/*  823 */           sb.append("System Events");
/*  824 */           sb.append("\" to make login item at end with properties {path:\"");
/*  825 */           sb.append(abs_target);
/*  826 */           sb.append("\", hidden:false}");
/*      */           
/*  828 */           System.out.println(performOSAScript(sb));
/*      */           
/*  830 */           return;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  834 */           throw new PlatformManagerException("Failed to add login item", e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  842 */         StringBuffer sb = new StringBuffer();
/*  843 */         sb.append("tell application \"");
/*  844 */         sb.append("System Events");
/*  845 */         sb.append("\" to delete login item \"");
/*  846 */         sb.append(SystemProperties.getApplicationName());
/*  847 */         sb.append("\"");
/*      */         
/*  849 */         System.out.println(performOSAScript(sb));
/*      */         
/*  851 */         return;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  855 */         throw new PlatformManagerException("Failed to delete login item", e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  862 */     File f = getLoginPList();
/*      */     
/*  864 */     if (f.exists())
/*      */     {
/*  866 */       convertToXML(f);
/*      */     }
/*      */     else {
/*      */       try
/*      */       {
/*  871 */         PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
/*      */         
/*      */         try
/*      */         {
/*  875 */           pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/*  876 */           pw.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
/*  877 */           pw.println("<plist version=\"1.0\">");
/*  878 */           pw.println("<dict>");
/*      */           
/*  880 */           pw.println("</dict>");
/*  881 */           pw.println("</plist>");
/*      */         }
/*      */         finally
/*      */         {
/*  885 */           pw.close();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  889 */         throw new PlatformManagerException("Failed to write output file", e);
/*      */       }
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  895 */       List<String> lines = new ArrayList();
/*      */       
/*  897 */       LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
/*      */       
/*  899 */       int dict_line = -1;
/*  900 */       int auto_launch_line = -1;
/*  901 */       int target_index = -1;
/*      */       try
/*      */       {
/*      */         for (;;)
/*      */         {
/*  906 */           String line = lnr.readLine();
/*      */           
/*  908 */           if (line == null) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*  913 */           lines.add(line);
/*      */           
/*  915 */           if ((dict_line == -1) && (containsTag(line, "<dict>")))
/*      */           {
/*  917 */             dict_line = lines.size();
/*      */           }
/*      */           
/*  920 */           if ((auto_launch_line == -1) && (containsTag(line, "AutoLaunchedApplicationDictionary")))
/*      */           {
/*  922 */             auto_launch_line = lines.size();
/*      */           }
/*      */           
/*  925 */           if (line.contains(abs_target))
/*      */           {
/*  927 */             target_index = lines.size();
/*      */           }
/*      */         }
/*      */         
/*  931 */         if (dict_line == -1)
/*      */         {
/*  933 */           throw new PlatformManagerException("Malformed plist - no 'dict' entry");
/*      */         }
/*      */         
/*  936 */         if (auto_launch_line == -1)
/*      */         {
/*  938 */           lines.add(dict_line, "\t<key>AutoLaunchedApplicationDictionary</key>");
/*      */           
/*  940 */           auto_launch_line = dict_line + 1;
/*      */           
/*  942 */           lines.add(auto_launch_line, "\t<array>");
/*  943 */           lines.add(auto_launch_line + 1, "\t</array>");
/*      */         }
/*      */       }
/*      */       finally {
/*  947 */         lnr.close();
/*      */       }
/*      */       
/*  950 */       if (run)
/*      */       {
/*  952 */         if ((target_index != -1) || (auto_launch_line == -1))
/*      */         {
/*  954 */           return;
/*      */         }
/*      */         
/*  957 */         target_index = auto_launch_line + 1;
/*      */         
/*  959 */         lines.add(target_index++, "\t\t<dict>");
/*  960 */         lines.add(target_index++, "\t\t\t<key>Path</key>");
/*  961 */         lines.add(target_index++, "\t\t\t<string>" + abs_target + "</string>");
/*  962 */         lines.add(target_index++, "\t\t</dict>");
/*      */       }
/*      */       else
/*      */       {
/*  966 */         if (target_index == -1)
/*      */         {
/*  968 */           return;
/*      */         }
/*      */         
/*  971 */         while (!containsTag((String)lines.get(target_index), "</dict>"))
/*      */         {
/*  973 */           lines.remove(target_index);
/*      */         }
/*      */         
/*  976 */         lines.remove(target_index);
/*      */         
/*  978 */         target_index--;
/*      */         
/*  980 */         while (!containsTag((String)lines.get(target_index), "<dict>"))
/*      */         {
/*  982 */           lines.remove(target_index);
/*      */           
/*  984 */           target_index--;
/*      */         }
/*      */         
/*  987 */         lines.remove(target_index);
/*      */       }
/*      */       
/*  990 */       File backup = new File(f.getParentFile(), f.getName() + ".bak");
/*      */       
/*  992 */       if (backup.exists())
/*      */       {
/*  994 */         backup.delete();
/*      */       }
/*      */       
/*  997 */       if (!f.renameTo(backup))
/*      */       {
/*  999 */         throw new PlatformManagerException("Failed to backup " + f);
/*      */       }
/*      */       
/* 1002 */       boolean ok = false;
/*      */       try
/*      */       {
/* 1005 */         PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
/*      */         
/*      */         try
/*      */         {
/* 1009 */           for (String line : lines)
/*      */           {
/* 1011 */             pw.println(line);
/*      */           }
/*      */         }
/*      */         finally {
/* 1015 */           pw.close();
/*      */           
/* 1017 */           if (pw.checkError())
/*      */           {
/* 1019 */             throw new PlatformManagerException("Failed to write output file");
/*      */           }
/*      */           
/* 1022 */           ok = true;
/*      */         }
/*      */       }
/*      */       finally {
/* 1026 */         if (!ok)
/*      */         {
/* 1028 */           backup.renameTo(f);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (PlatformManagerException e)
/*      */     {
/* 1034 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1038 */       throw new PlatformManagerException("Failed to write output file", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void convertToXML(File file)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1049 */       LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
/*      */       try
/*      */       {
/* 1052 */         String line = lnr.readLine();
/*      */         
/* 1054 */         if (line == null) {
/*      */           return;
/*      */         }
/*      */         
/*      */ 
/* 1059 */         if (line.trim().toLowerCase().startsWith("<?xml")) {
/*      */           return;
/*      */         }
/*      */         
/*      */ 
/* 1064 */         Runtime.getRuntime().exec(new String[] { findCommand("plutil"), "-convert", "xml1", file.getAbsolutePath() }).waitFor();
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1074 */         lnr.close();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1078 */       throw new PlatformManagerException("Failed to convert plist to xml");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String findCommand(String name)
/*      */   {
/* 1086 */     String[] locations = { "/bin", "/usr/bin" };
/*      */     
/* 1088 */     for (String s : locations)
/*      */     {
/* 1090 */       File f = new File(s, name);
/*      */       
/* 1092 */       if ((f.exists()) && (f.canRead()))
/*      */       {
/* 1094 */         return f.getAbsolutePath();
/*      */       }
/*      */     }
/*      */     
/* 1098 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean containsTag(String line, String tag)
/*      */   {
/* 1106 */     line = line.trim().toLowerCase(Locale.US);
/* 1107 */     tag = tag.toLowerCase(Locale.US);
/*      */     
/* 1109 */     StringBuilder line2 = new StringBuilder(line.length());
/*      */     
/* 1111 */     for (char c : line.toCharArray())
/*      */     {
/* 1113 */       if (!Character.isWhitespace(c))
/*      */       {
/* 1115 */         line2.append(c);
/*      */       }
/*      */     }
/*      */     
/* 1119 */     return line2.toString().contains(tag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private File getLoginPList()
/*      */     throws PlatformManagerException
/*      */   {
/* 1127 */     return new File(System.getProperty("user.home"), "/Library/Preferences/loginwindow.plist");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getUserDataDirectory()
/*      */     throws PlatformManagerException
/*      */   {
/* 1136 */     return new File(new StringBuilder().append(System.getProperty("user.home")).append("/Library/Application Support/").append(SystemProperties.APPLICATION_NAME).toString()).getPath() + SystemProperties.SEP;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getComputerName()
/*      */   {
/* 1145 */     if (this.computer_name_tried)
/*      */     {
/* 1147 */       return this.computer_name;
/*      */     }
/*      */     try
/*      */     {
/* 1151 */       String result = null;
/*      */       
/* 1153 */       String hostname = System.getenv("HOSTNAME");
/*      */       
/* 1155 */       if ((hostname != null) && (hostname.length() > 0))
/*      */       {
/* 1157 */         result = hostname;
/*      */       }
/*      */       
/* 1160 */       if (result == null)
/*      */       {
/* 1162 */         String host = System.getenv("HOST");
/*      */         
/* 1164 */         if ((host != null) && (host.length() > 0))
/*      */         {
/* 1166 */           result = host;
/*      */         }
/*      */       }
/*      */       
/* 1170 */       if (result == null) {
/*      */         try
/*      */         {
/* 1173 */           String[] to_run = new String[3];
/*      */           
/* 1175 */           to_run[0] = "/bin/sh";
/* 1176 */           to_run[1] = "-c";
/* 1177 */           to_run[2] = "echo $HOSTNAME";
/*      */           
/* 1179 */           Process p = Runtime.getRuntime().exec(to_run);
/*      */           
/* 1181 */           if (p.waitFor() == 0)
/*      */           {
/* 1183 */             String output = "";
/*      */             
/* 1185 */             InputStream is = p.getInputStream();
/*      */             
/*      */             for (;;)
/*      */             {
/* 1189 */               byte[] buffer = new byte['Ѐ'];
/*      */               
/* 1191 */               int len = is.read(buffer);
/*      */               
/* 1193 */               if (len <= 0) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1198 */               output = output + new String(buffer, 0, len);
/*      */               
/* 1200 */               if (output.length() > 64) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 1206 */             if (output.length() > 0)
/*      */             {
/* 1208 */               result = output.trim();
/*      */               
/* 1210 */               int pos = result.indexOf(' ');
/*      */               
/* 1212 */               if (pos != -1)
/*      */               {
/* 1214 */                 result = result.substring(0, pos).trim();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       int pos;
/* 1222 */       if (result != null)
/*      */       {
/* 1224 */         pos = result.lastIndexOf('.');
/*      */         
/* 1226 */         if (pos != -1)
/*      */         {
/* 1228 */           result = result.substring(0, pos);
/*      */         }
/*      */         
/* 1231 */         if (result.length() > 0)
/*      */         {
/* 1233 */           if (result.length() > 32)
/*      */           {
/* 1235 */             result = result.substring(0, 32);
/*      */           }
/*      */           
/* 1238 */           this.computer_name = result;
/*      */         }
/*      */       }
/*      */       
/* 1242 */       return this.computer_name;
/*      */     }
/*      */     finally
/*      */     {
/* 1246 */       this.computer_name_tried = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public File getLocation(long location_id)
/*      */     throws PlatformManagerException
/*      */   {
/* 1256 */     switch ((int)location_id) {
/*      */     case 1: 
/* 1258 */       return new File(getUserDataDirectory());
/*      */     case 3: 
/*      */       try
/*      */       {
/* 1262 */         return new File(OSXAccess.getDocDir());
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/* 1268 */         return new File(System.getProperty("user.home"), "Documents");
/*      */       }
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1276 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isApplicationRegistered()
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1287 */       if (OSXAccess.canSetDefaultApp()) {
/* 1288 */         for (String ext : EXTENSIONS) {
/* 1289 */           if (!isOurExt(ext)) {
/* 1290 */             return false;
/*      */           }
/*      */         }
/* 1293 */         for (String mimeType : MIMETYPES) {
/* 1294 */           if (!isOurMimeType(mimeType)) {
/* 1295 */             return false;
/*      */           }
/*      */         }
/* 1298 */         for (String scheme : SCHEMES) {
/* 1299 */           if (!isOurScheme(scheme)) {
/* 1300 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1307 */     return true;
/*      */   }
/*      */   
/*      */   private boolean isOurExt(String ext) {
/*      */     try {
/* 1312 */       String appForExt = OSXAccess.getDefaultAppForExt(ext);
/*      */       
/* 1314 */       return "com.azureus.vuze".equals(appForExt);
/*      */     } catch (Throwable e) {}
/* 1316 */     return true;
/*      */   }
/*      */   
/*      */   private boolean isOurScheme(String scheme)
/*      */   {
/*      */     try {
/* 1322 */       String appForScheme = OSXAccess.getDefaultAppForScheme(scheme);
/*      */       
/* 1324 */       return "com.azureus.vuze".equals(appForScheme);
/*      */     } catch (Throwable e) {}
/* 1326 */     return true;
/*      */   }
/*      */   
/*      */   private boolean isOurMimeType(String mimetype)
/*      */   {
/*      */     try {
/* 1332 */       String appForMimeType = OSXAccess.getDefaultAppForMime(mimetype);
/*      */       
/* 1334 */       return "com.azureus.vuze".equals(appForMimeType);
/*      */     } catch (Throwable e) {}
/* 1336 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getBundlePath()
/*      */   {
/* 1346 */     String mod_name = System.getProperty("exe4j.moduleName", null);
/* 1347 */     if ((mod_name != null) && (mod_name.endsWith(".app"))) {
/* 1348 */       return mod_name;
/*      */     }
/* 1350 */     return SystemProperties.getApplicationPath() + SystemProperties.getApplicationName() + ".app";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private File getAbsoluteBundleFile()
/*      */   {
/* 1359 */     return new File(getBundlePath()).getAbsoluteFile();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getApplicationCommandLine()
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1370 */       File osx_app_bundle = getAbsoluteBundleFile();
/*      */       
/* 1372 */       if (!osx_app_bundle.exists()) {
/* 1373 */         String msg = "OSX app bundle not found: [" + osx_app_bundle.toString() + "]";
/* 1374 */         System.out.println(msg);
/* 1375 */         if (Logger.isEnabled())
/* 1376 */           Logger.log(new LogEvent(LOGID, msg));
/* 1377 */         throw new PlatformManagerException(msg);
/*      */       }
/*      */       
/* 1380 */       return "open -a \"" + osx_app_bundle.toString() + "\"";
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1385 */       t.printStackTrace(); }
/* 1386 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isAdditionalFileTypeRegistered(String name, String type)
/*      */     throws PlatformManagerException
/*      */   {
/* 1398 */     String osxType = type.startsWith(".") ? type.substring(1) : type;
/* 1399 */     return isOurExt(osxType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void unregisterAdditionalFileType(String name, String type)
/*      */     throws PlatformManagerException
/*      */   {
/* 1409 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerAdditionalFileType(String name, String description, String type, String content_type)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1422 */       if (OSXAccess.canSetDefaultApp()) {
/* 1423 */         if (type != null) {
/* 1424 */           String osxType = type.startsWith(".") ? type.substring(1) : type;
/* 1425 */           OSXAccess.setDefaultAppForExt("com.azureus.vuze", osxType);
/*      */         }
/* 1427 */         if (content_type != null) {
/* 1428 */           OSXAccess.setDefaultAppForMime("com.azureus.vuze", content_type);
/*      */         }
/*      */       }
/*      */     } catch (Throwable t) {
/* 1432 */       throw new PlatformManagerException("registerAdditionalFileType failed on platform manager", t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void registerApplication()
/*      */     throws PlatformManagerException
/*      */   {
/* 1439 */     touchPList();
/*      */     try
/*      */     {
/* 1442 */       if (OSXAccess.canSetDefaultApp()) {
/* 1443 */         for (String ext : EXTENSIONS) {
/* 1444 */           OSXAccess.setDefaultAppForExt("com.azureus.vuze", ext);
/*      */         }
/* 1446 */         for (String mimeType : MIMETYPES) {
/* 1447 */           OSXAccess.setDefaultAppForMime("com.azureus.vuze", mimeType);
/*      */         }
/* 1449 */         for (String scheme : SCHEMES) {
/* 1450 */           OSXAccess.setDefaultAppForScheme("com.azureus.vuze", scheme);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1455 */         isApplicationRegistered();
/*      */       }
/*      */     } catch (Throwable t) {
/* 1458 */       throw new PlatformManagerException("registerApplication failed on platform manager", t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void createProcess(String cmd, boolean inheritsHandles)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1471 */       performRuntimeExec(cmd.split(" "));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1475 */       throw new PlatformManagerException("Failed to create process", e);
/*      */     }
/*      */   }
/*      */   
/*      */   private Class<?> getFileManagerClass() {
/* 1480 */     if (this.claFileManager != null) {
/* 1481 */       return this.claFileManager;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1488 */       Class<?> claCocoaUIEnhancer = Class.forName("org.gudy.azureus2.ui.swt.osx.CocoaUIEnhancer");
/* 1489 */       if (((Boolean)claCocoaUIEnhancer.getMethod("isInitialized", new Class[0]).invoke(null, new Object[0])).booleanValue()) {
/* 1490 */         this.claFileManager = Class.forName("com.apple.eio.FileManager");
/*      */       }
/*      */     }
/*      */     catch (Exception e) {}
/* 1494 */     return this.claFileManager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void performRecoverableFileDelete(String path)
/*      */     throws PlatformManagerException
/*      */   {
/* 1502 */     File file = new File(path);
/* 1503 */     if (!file.exists())
/*      */     {
/* 1505 */       if (Logger.isEnabled()) {
/* 1506 */         Logger.log(new LogEvent(LOGID, 1, "Cannot find " + file.getName()));
/*      */       }
/* 1508 */       return;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1513 */       Class<?> claFileManager = getFileManagerClass();
/*      */       
/* 1515 */       if (claFileManager != null) {
/* 1516 */         Method methMoveToTrash = claFileManager.getMethod("moveToTrash", new Class[] { File.class });
/*      */         
/*      */ 
/*      */ 
/* 1520 */         if (methMoveToTrash != null) {
/* 1521 */           Object result = methMoveToTrash.invoke(null, new Object[] { file });
/*      */           
/*      */ 
/* 1524 */           if (((result instanceof Boolean)) && 
/* 1525 */             (((Boolean)result).booleanValue())) {
/* 1526 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/* 1534 */     boolean useOSA = (!NativeInvocationBridge.sharedInstance().isEnabled()) || (!NativeInvocationBridge.sharedInstance().performRecoverableFileDelete(file));
/*      */     
/* 1536 */     if (useOSA)
/*      */     {
/*      */       try
/*      */       {
/* 1540 */         StringBuffer sb = new StringBuffer();
/* 1541 */         sb.append("tell application \"");
/* 1542 */         sb.append("Finder");
/* 1543 */         sb.append("\" to move (posix file \"");
/* 1544 */         sb.append(path);
/* 1545 */         sb.append("\" as alias) to the trash");
/*      */         
/* 1547 */         performOSAScript(sb);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1551 */         throw new PlatformManagerException("Failed to move file", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasCapability(PlatformManagerCapabilities capability)
/*      */   {
/* 1561 */     return this.capabilitySet.contains(capability);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void dispose()
/*      */   {
/*      */     try
/*      */     {
/* 1570 */       if (NativeInvocationBridge.hasSharedInstance()) {
/* 1571 */         NativeInvocationBridge.sharedInstance().dispose();
/*      */       }
/*      */     } catch (Throwable t) {
/* 1574 */       Debug.out("Problem disposing NativeInvocationBridge", t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTCPTOSEnabled(boolean enabled)
/*      */     throws PlatformManagerException
/*      */   {
/* 1583 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyFilePermissions(String from_file_name, String to_file_name)
/*      */     throws PlatformManagerException
/*      */   {
/* 1593 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void showFile(String path)
/*      */     throws PlatformManagerException
/*      */   {
/* 1601 */     File file = new File(path);
/* 1602 */     if (!file.exists())
/*      */     {
/* 1604 */       if (Logger.isEnabled()) {
/* 1605 */         Logger.log(new LogEvent(LOGID, 1, "Cannot find " + file.getName()));
/*      */       }
/* 1607 */       throw new PlatformManagerException("File not found");
/*      */     }
/*      */     
/* 1610 */     showInFinder(file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void playSystemAlert()
/*      */   {
/*      */     try
/*      */     {
/* 1622 */       performRuntimeExec(new String[] { "beep" });
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 1626 */       if (Logger.isEnabled()) {
/* 1627 */         Logger.log(new LogEvent(LOGID, 1, "Cannot play system alert"));
/*      */       }
/* 1629 */       Logger.log(new LogEvent(LOGID, "", e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void showInFinder(File path)
/*      */   {
/*      */     try
/*      */     {
/* 1641 */       Class<?> claFileManager = getFileManagerClass();
/* 1642 */       if ((claFileManager != null) && (getFileBrowserName().equals("Finder"))) {
/* 1643 */         Method methRevealInFinder = claFileManager.getMethod("revealInFinder", new Class[] { File.class });
/*      */         
/*      */ 
/*      */ 
/* 1647 */         if (methRevealInFinder != null) {
/* 1648 */           Object result = methRevealInFinder.invoke(null, new Object[] { path });
/*      */           
/*      */ 
/* 1651 */           if (((result instanceof Boolean)) && 
/* 1652 */             (((Boolean)result).booleanValue())) {
/* 1653 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/* 1661 */     boolean useOSA = (!NativeInvocationBridge.sharedInstance().isEnabled()) || (!NativeInvocationBridge.sharedInstance().showInFinder(path, fileBrowserName));
/*      */     
/* 1663 */     if (useOSA)
/*      */     {
/* 1665 */       StringBuffer sb = new StringBuffer();
/* 1666 */       sb.append("tell application \"");
/* 1667 */       sb.append(getFileBrowserName());
/* 1668 */       sb.append("\"\n");
/* 1669 */       sb.append("reveal (posix file \"");
/* 1670 */       sb.append(path);
/* 1671 */       sb.append("\" as alias)\n");
/* 1672 */       sb.append("activate\n");
/* 1673 */       sb.append("end tell\n");
/*      */       
/*      */       try
/*      */       {
/* 1677 */         performOSAScript(sb);
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1681 */         Logger.log(new LogAlert(false, 3, e.getMessage()));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void showInTerminal(String path)
/*      */   {
/* 1693 */     showInTerminal(new File(path));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void showInTerminal(File path)
/*      */   {
/* 1702 */     if (path.isFile())
/*      */     {
/* 1704 */       path = path.getParentFile();
/*      */     }
/*      */     
/* 1707 */     if ((path != null) && (path.isDirectory()))
/*      */     {
/* 1709 */       StringBuffer sb = new StringBuffer();
/* 1710 */       sb.append("tell application \"");
/* 1711 */       sb.append("Terminal");
/* 1712 */       sb.append("\" to do script \"cd ");
/* 1713 */       sb.append(path.getAbsolutePath().replaceAll(" ", "\\ "));
/* 1714 */       sb.append("\"");
/*      */       
/*      */       try
/*      */       {
/* 1718 */         performOSAScript(sb);
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1722 */         Logger.log(new LogAlert(false, 3, e.getMessage()));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/* 1728 */     else if (Logger.isEnabled()) {
/* 1729 */       Logger.log(new LogEvent(LOGID, 1, "Cannot find " + (path == null ? "null" : path.getName())));
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
/*      */   protected static String performOSAScript(CharSequence cmd)
/*      */     throws IOException
/*      */   {
/* 1744 */     return performOSAScript(new CharSequence[] { cmd });
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
/*      */ 
/*      */   protected static String performOSAScript(CharSequence[] cmds)
/*      */     throws IOException
/*      */   {
/* 1765 */     String[] cmdargs = new String[2 * cmds.length + 1];
/* 1766 */     cmdargs[0] = "osascript";
/* 1767 */     for (int i = 0; i < cmds.length; i++)
/*      */     {
/* 1769 */       cmdargs[(i * 2 + 1)] = "-e";
/* 1770 */       cmdargs[(i * 2 + 2)] = String.valueOf(cmds[i]);
/*      */     }
/*      */     
/* 1773 */     Process osaProcess = performRuntimeExec(cmdargs);
/* 1774 */     BufferedReader reader = new BufferedReader(new InputStreamReader(osaProcess.getInputStream()));
/* 1775 */     String line = reader.readLine();
/* 1776 */     reader.close();
/*      */     
/*      */ 
/*      */ 
/* 1780 */     reader = new BufferedReader(new InputStreamReader(osaProcess.getErrorStream()));
/* 1781 */     String errorMsg = reader.readLine();
/* 1782 */     reader.close();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1789 */       osaProcess.destroy();
/*      */     }
/*      */     catch (Throwable t) {}
/*      */     
/*      */ 
/* 1794 */     if (errorMsg != null)
/*      */     {
/* 1796 */       throw new IOException(errorMsg);
/*      */     }
/*      */     
/* 1799 */     return line;
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
/*      */   protected static String performOSAScript(File script)
/*      */     throws IOException
/*      */   {
/* 1815 */     Process osaProcess = performRuntimeExec(new String[] { "osascript", script.getPath() });
/* 1816 */     BufferedReader reader = new BufferedReader(new InputStreamReader(osaProcess.getInputStream()));
/* 1817 */     String line = reader.readLine();
/* 1818 */     reader.close();
/*      */     
/*      */ 
/* 1821 */     reader = new BufferedReader(new InputStreamReader(osaProcess.getErrorStream()));
/* 1822 */     String errorMsg = reader.readLine();
/* 1823 */     reader.close();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1830 */       osaProcess.destroy();
/*      */     }
/*      */     catch (Throwable t) {}
/*      */     
/* 1834 */     if (errorMsg != null)
/*      */     {
/* 1836 */       throw new IOException(errorMsg);
/*      */     }
/*      */     
/* 1839 */     return line;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static boolean compileOSAScript(CharSequence cmd, File destination)
/*      */   {
/* 1850 */     return compileOSAScript(new CharSequence[] { cmd }, destination);
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
/*      */ 
/*      */   protected static boolean compileOSAScript(CharSequence[] cmds, File destination)
/*      */   {
/* 1870 */     String[] cmdargs = new String[2 * cmds.length + 3];
/* 1871 */     cmdargs[0] = "osacompile";
/* 1872 */     for (int i = 0; i < cmds.length; i++)
/*      */     {
/* 1874 */       cmdargs[(i * 2 + 1)] = "-e";
/* 1875 */       cmdargs[(i * 2 + 2)] = String.valueOf(cmds[i]);
/*      */     }
/*      */     
/* 1878 */     cmdargs[(cmdargs.length - 2)] = "-o";
/* 1879 */     cmdargs[(cmdargs.length - 1)] = destination.getPath();
/*      */     
/*      */     String errorMsg;
/*      */     try
/*      */     {
/* 1884 */       Process osaProcess = performRuntimeExec(cmdargs);
/*      */       
/* 1886 */       BufferedReader reader = new BufferedReader(new InputStreamReader(osaProcess.getErrorStream()));
/* 1887 */       errorMsg = reader.readLine();
/* 1888 */       reader.close();
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 1892 */       Debug.outNoStack("OSACompile Execution Failed: " + e.getMessage());
/* 1893 */       Debug.printStackTrace(e);
/* 1894 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1901 */     return errorMsg == null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static Process performRuntimeExec(String[] cmdargs)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/* 1911 */       return Runtime.getRuntime().exec(cmdargs);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 1915 */       Logger.log(new LogAlert(false, e.getMessage(), e));
/* 1916 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getFileBrowserName()
/*      */   {
/* 1928 */     return fileBrowserName;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean testNativeAvailability(String name)
/*      */     throws PlatformManagerException
/*      */   {
/* 1937 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
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
/* 1948 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
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
/* 1959 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxOpenFiles()
/*      */     throws PlatformManagerException
/*      */   {
/* 1967 */     LineNumberReader lnr = null;
/*      */     try
/*      */     {
/* 1970 */       Process p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "ulimit -a" });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1977 */       lnr = new LineNumberReader(new InputStreamReader(p.getInputStream()));
/*      */       
/* 1979 */       Map<String, String> map = new HashMap();
/*      */       int pos1;
/*      */       for (;;)
/*      */       {
/* 1983 */         String line = lnr.readLine();
/*      */         
/* 1985 */         if (line == null) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1990 */         pos1 = line.indexOf('(');
/* 1991 */         int pos2 = line.indexOf(')', pos1 + 1);
/*      */         
/* 1993 */         String keyword = line.substring(0, pos1).trim().toLowerCase();
/* 1994 */         String value = line.substring(pos2 + 1).trim();
/*      */         
/* 1996 */         map.put(keyword, value);
/*      */       }
/*      */       
/* 1999 */       String open_files = (String)map.get("open files");
/*      */       
/* 2001 */       if (open_files != null)
/*      */       {
/* 2003 */         if (open_files.equalsIgnoreCase("unlimited"))
/*      */         {
/* 2005 */           return 0;
/*      */         }
/*      */         try {
/* 2008 */           return Integer.parseInt(open_files);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2012 */           Debug.out("open files invalid: " + open_files);
/*      */         }
/*      */       }
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
/* 2030 */       return -1;
/*      */     }
/*      */     catch (Throwable e) {}finally
/*      */     {
/* 2020 */       if (lnr != null) {
/*      */         try
/*      */         {
/* 2023 */           lnr.close();
/*      */         }
/*      */         catch (Throwable e) {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2046 */     writer.println("PlatformManager: MacOSX");
/*      */     try {
/* 2048 */       writer.indent();
/*      */       
/* 2050 */       if (OSXAccess.isLoaded()) {
/*      */         try {
/* 2052 */           writer.println("Version " + getVersion());
/* 2053 */           writer.println("User Data Dir: " + getLocation(1L));
/* 2054 */           writer.println("User Doc Dir: " + getLocation(3L));
/*      */         }
/*      */         catch (PlatformManagerException e) {}
/*      */       } else {
/* 2058 */         writer.println("Not loaded");
/*      */       }
/*      */       
/* 2061 */       writer.println("Computer Name: " + getComputerName());
/*      */       try
/*      */       {
/* 2064 */         writer.println("Max Open Files: " + getMaxOpenFiles());
/*      */       } catch (Throwable e) {
/* 2066 */         writer.println("Max Open Files: " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     } finally {
/* 2069 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */   public String getAzComputerID() throws PlatformManagerException
/*      */   {
/* 2075 */     throw new PlatformManagerException("Unsupported capability called on platform manager");
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
/*      */   public void requestUserAttention(int type, Object data)
/*      */     throws PlatformManagerException
/*      */   {
/* 2089 */     if (type == 3) {
/* 2090 */       return;
/*      */     }
/*      */     try {
/* 2093 */       Class<?> claNSApplication = Class.forName("com.apple.eawt.Application");
/* 2094 */       Method methGetApplication = claNSApplication.getMethod("getApplication", new Class[0]);
/* 2095 */       Object app = methGetApplication.invoke(null, new Object[0]);
/*      */       
/* 2097 */       Method methRequestUserAttention = claNSApplication.getMethod("requestUserAttention", new Class[] { Boolean.class });
/*      */       
/*      */ 
/*      */ 
/* 2101 */       if (type == 1) {
/* 2102 */         methRequestUserAttention.invoke(app, new Object[] { Boolean.valueOf(false) });
/* 2103 */       } else if (type == 2) {
/* 2104 */         methRequestUserAttention.invoke(app, new Object[] { Boolean.valueOf(true) });
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/* 2108 */       throw new PlatformManagerException("Failed to request user attention", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Class<?> loadClass(ClassLoader loader, String class_name)
/*      */     throws PlatformManagerException
/*      */   {
/*      */     try
/*      */     {
/* 2121 */       return loader.loadClass(class_name);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2125 */       throw new PlatformManagerException("load of '" + class_name + "' failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 2134 */       SystemProperties.setApplicationName("Vuze");
/*      */       
/*      */ 
/*      */ 
/* 2138 */       PlatformManagerImpl pm = new PlatformManagerImpl();
/*      */       
/* 2140 */       pm.getRunAtLogin();
/*      */       
/* 2142 */       pm.setRunAtLogin(false);
/*      */     }
/*      */     catch (Throwable e) {
/* 2145 */       e.printStackTrace();
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
/*      */     //   5: getfield 1143	org/gudy/azureus2/platform/macosx/PlatformManagerImpl:prevent_computer_sleep	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #695	-> byte code offset #0
/*      */     //   Java source line #697	-> byte code offset #4
/*      */     //   Java source line #698	-> byte code offset #11
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
/*      */   /* Error */
/*      */   public boolean getRunAtLogin()
/*      */     throws PlatformManagerException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 1125	org/gudy/azureus2/core3/util/Constants:isOSX_10_8_OrHigher	Z
/*      */     //   3: ifeq +105 -> 108
/*      */     //   6: invokestatic 1273	org/gudy/azureus2/core3/util/SystemProperties:getApplicationName	()Ljava/lang/String;
/*      */     //   9: astore_1
/*      */     //   10: new 756	java/lang/StringBuffer
/*      */     //   13: dup
/*      */     //   14: invokespecial 1227	java/lang/StringBuffer:<init>	()V
/*      */     //   17: astore_2
/*      */     //   18: aload_2
/*      */     //   19: ldc 88
/*      */     //   21: invokevirtual 1229	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   24: pop
/*      */     //   25: aload_2
/*      */     //   26: ldc 67
/*      */     //   28: invokevirtual 1229	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   31: pop
/*      */     //   32: aload_2
/*      */     //   33: ldc 12
/*      */     //   35: invokevirtual 1229	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   38: pop
/*      */     //   39: aload_2
/*      */     //   40: invokestatic 1319	org/gudy/azureus2/platform/macosx/PlatformManagerImpl:performOSAScript	(Ljava/lang/CharSequence;)Ljava/lang/String;
/*      */     //   43: ldc 15
/*      */     //   45: invokevirtual 1224	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
/*      */     //   48: astore_3
/*      */     //   49: aload_3
/*      */     //   50: astore 4
/*      */     //   52: aload 4
/*      */     //   54: arraylength
/*      */     //   55: istore 5
/*      */     //   57: iconst_0
/*      */     //   58: istore 6
/*      */     //   60: iload 6
/*      */     //   62: iload 5
/*      */     //   64: if_icmpge +30 -> 94
/*      */     //   67: aload 4
/*      */     //   69: iload 6
/*      */     //   71: aaload
/*      */     //   72: astore 7
/*      */     //   74: aload 7
/*      */     //   76: invokevirtual 1217	java/lang/String:trim	()Ljava/lang/String;
/*      */     //   79: aload_1
/*      */     //   80: invokevirtual 1221	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
/*      */     //   83: ifeq +5 -> 88
/*      */     //   86: iconst_1
/*      */     //   87: ireturn
/*      */     //   88: iinc 6 1
/*      */     //   91: goto -31 -> 60
/*      */     //   94: iconst_0
/*      */     //   95: ireturn
/*      */     //   96: astore_2
/*      */     //   97: new 790	org/gudy/azureus2/plugins/platform/PlatformManagerException
/*      */     //   100: dup
/*      */     //   101: ldc 53
/*      */     //   103: aload_2
/*      */     //   104: invokespecial 1336	org/gudy/azureus2/plugins/platform/PlatformManagerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   107: athrow
/*      */     //   108: aload_0
/*      */     //   109: invokespecial 1298	org/gudy/azureus2/platform/macosx/PlatformManagerImpl:getLoginPList	()Ljava/io/File;
/*      */     //   112: astore_1
/*      */     //   113: aload_1
/*      */     //   114: invokevirtual 1164	java/io/File:exists	()Z
/*      */     //   117: ifne +5 -> 122
/*      */     //   120: iconst_0
/*      */     //   121: ireturn
/*      */     //   122: aload_0
/*      */     //   123: invokespecial 1297	org/gudy/azureus2/platform/macosx/PlatformManagerImpl:getAbsoluteBundleFile	()Ljava/io/File;
/*      */     //   126: astore_2
/*      */     //   127: aload_2
/*      */     //   128: invokevirtual 1164	java/io/File:exists	()Z
/*      */     //   131: ifne +5 -> 136
/*      */     //   134: iconst_0
/*      */     //   135: ireturn
/*      */     //   136: aload_0
/*      */     //   137: aload_1
/*      */     //   138: invokespecial 1301	org/gudy/azureus2/platform/macosx/PlatformManagerImpl:convertToXML	(Ljava/io/File;)V
/*      */     //   141: new 741	java/io/LineNumberReader
/*      */     //   144: dup
/*      */     //   145: new 740	java/io/InputStreamReader
/*      */     //   148: dup
/*      */     //   149: new 736	java/io/FileInputStream
/*      */     //   152: dup
/*      */     //   153: aload_1
/*      */     //   154: invokespecial 1177	java/io/FileInputStream:<init>	(Ljava/io/File;)V
/*      */     //   157: ldc 68
/*      */     //   159: invokespecial 1183	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
/*      */     //   162: invokespecial 1185	java/io/LineNumberReader:<init>	(Ljava/io/Reader;)V
/*      */     //   165: astore_3
/*      */     //   166: iconst_0
/*      */     //   167: istore 4
/*      */     //   169: aload_2
/*      */     //   170: invokevirtual 1170	java/io/File:getAbsolutePath	()Ljava/lang/String;
/*      */     //   173: astore 5
/*      */     //   175: aload_3
/*      */     //   176: invokevirtual 1186	java/io/LineNumberReader:readLine	()Ljava/lang/String;
/*      */     //   179: astore 6
/*      */     //   181: aload 6
/*      */     //   183: ifnonnull +6 -> 189
/*      */     //   186: goto +48 -> 234
/*      */     //   189: iload 4
/*      */     //   191: ifne +20 -> 211
/*      */     //   194: aload_0
/*      */     //   195: aload 6
/*      */     //   197: ldc 41
/*      */     //   199: invokespecial 1322	org/gudy/azureus2/platform/macosx/PlatformManagerImpl:containsTag	(Ljava/lang/String;Ljava/lang/String;)Z
/*      */     //   202: ifeq +29 -> 231
/*      */     //   205: iconst_1
/*      */     //   206: istore 4
/*      */     //   208: goto +23 -> 231
/*      */     //   211: aload 6
/*      */     //   213: aload 5
/*      */     //   215: invokevirtual 1214	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   218: ifeq +13 -> 231
/*      */     //   221: iconst_1
/*      */     //   222: istore 7
/*      */     //   224: aload_3
/*      */     //   225: invokevirtual 1184	java/io/LineNumberReader:close	()V
/*      */     //   228: iload 7
/*      */     //   230: ireturn
/*      */     //   231: goto -56 -> 175
/*      */     //   234: iconst_0
/*      */     //   235: istore 6
/*      */     //   237: aload_3
/*      */     //   238: invokevirtual 1184	java/io/LineNumberReader:close	()V
/*      */     //   241: iload 6
/*      */     //   243: ireturn
/*      */     //   244: astore 8
/*      */     //   246: aload_3
/*      */     //   247: invokevirtual 1184	java/io/LineNumberReader:close	()V
/*      */     //   250: aload 8
/*      */     //   252: athrow
/*      */     //   253: astore_3
/*      */     //   254: new 790	org/gudy/azureus2/plugins/platform/PlatformManagerException
/*      */     //   257: dup
/*      */     //   258: ldc 56
/*      */     //   260: aload_3
/*      */     //   261: invokespecial 1336	org/gudy/azureus2/plugins/platform/PlatformManagerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   264: athrow
/*      */     // Line number table:
/*      */     //   Java source line #706	-> byte code offset #0
/*      */     //   Java source line #708	-> byte code offset #6
/*      */     //   Java source line #712	-> byte code offset #10
/*      */     //   Java source line #713	-> byte code offset #18
/*      */     //   Java source line #714	-> byte code offset #25
/*      */     //   Java source line #715	-> byte code offset #32
/*      */     //   Java source line #717	-> byte code offset #39
/*      */     //   Java source line #719	-> byte code offset #49
/*      */     //   Java source line #721	-> byte code offset #74
/*      */     //   Java source line #723	-> byte code offset #86
/*      */     //   Java source line #719	-> byte code offset #88
/*      */     //   Java source line #727	-> byte code offset #94
/*      */     //   Java source line #729	-> byte code offset #96
/*      */     //   Java source line #731	-> byte code offset #97
/*      */     //   Java source line #735	-> byte code offset #108
/*      */     //   Java source line #737	-> byte code offset #113
/*      */     //   Java source line #739	-> byte code offset #120
/*      */     //   Java source line #742	-> byte code offset #122
/*      */     //   Java source line #744	-> byte code offset #127
/*      */     //   Java source line #746	-> byte code offset #134
/*      */     //   Java source line #750	-> byte code offset #136
/*      */     //   Java source line #752	-> byte code offset #141
/*      */     //   Java source line #754	-> byte code offset #166
/*      */     //   Java source line #756	-> byte code offset #169
/*      */     //   Java source line #761	-> byte code offset #175
/*      */     //   Java source line #763	-> byte code offset #181
/*      */     //   Java source line #765	-> byte code offset #186
/*      */     //   Java source line #768	-> byte code offset #189
/*      */     //   Java source line #770	-> byte code offset #194
/*      */     //   Java source line #772	-> byte code offset #205
/*      */     //   Java source line #776	-> byte code offset #211
/*      */     //   Java source line #778	-> byte code offset #221
/*      */     //   Java source line #787	-> byte code offset #224
/*      */     //   Java source line #781	-> byte code offset #231
/*      */     //   Java source line #783	-> byte code offset #234
/*      */     //   Java source line #787	-> byte code offset #237
/*      */     //   Java source line #789	-> byte code offset #253
/*      */     //   Java source line #791	-> byte code offset #254
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	265	0	this	PlatformManagerImpl
/*      */     //   9	71	1	item_name	String
/*      */     //   112	42	1	f	File
/*      */     //   17	23	2	sb	StringBuffer
/*      */     //   96	8	2	e	Throwable
/*      */     //   126	44	2	bundle_file	File
/*      */     //   48	2	3	items	String[]
/*      */     //   165	82	3	lnr	LineNumberReader
/*      */     //   253	8	3	e	Throwable
/*      */     //   50	18	4	arr$	String[]
/*      */     //   167	40	4	state	int
/*      */     //   55	8	5	len$	int
/*      */     //   173	41	5	target	String
/*      */     //   58	31	6	i$	int
/*      */     //   179	63	6	line	String
/*      */     //   72	157	7	item	String
/*      */     //   244	7	8	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   10	87	96	java/lang/Throwable
/*      */     //   88	95	96	java/lang/Throwable
/*      */     //   175	224	244	finally
/*      */     //   231	237	244	finally
/*      */     //   244	246	244	finally
/*      */     //   136	228	253	java/lang/Throwable
/*      */     //   231	241	253	java/lang/Throwable
/*      */     //   244	253	253	java/lang/Throwable
/*      */   }
/*      */   
/*      */   public void addListener(PlatformManagerListener listener) {}
/*      */   
/*      */   public void removeListener(PlatformManagerListener listener) {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/macosx/PlatformManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */