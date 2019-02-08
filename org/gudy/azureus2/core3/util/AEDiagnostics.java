/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
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
/*     */ public class AEDiagnostics
/*     */ {
/*     */   public static final boolean ALWAYS_PASS_HASH_CHECKS = false;
/*     */   public static final boolean USE_DUMMY_FILE_DATA = false;
/*     */   public static final boolean CHECK_DUMMY_FILE_DATA = false;
/*     */   public static final boolean DEBUG_MONITOR_SEM_USAGE = false;
/*     */   public static final boolean DEBUG_THREADS = true;
/*     */   public static final boolean TRACE_DIRECT_BYTE_BUFFERS = false;
/*     */   public static final boolean TRACE_DBB_POOL_USAGE = false;
/*     */   public static final boolean PRINT_DBB_POOL_USAGE = false;
/*     */   public static final boolean TRACE_TCP_TRANSPORT_STATS = false;
/*     */   public static final boolean TRACE_CONNECTION_DROPS = false;
/*     */   private static final int MAX_FILE_SIZE;
/*     */   private static final String CONFIG_KEY = "diagnostics.tidy_close";
/*     */   private static File debug_dir;
/*     */   private static File debug_save_dir;
/*     */   private static boolean started_up;
/*     */   private static volatile boolean startup_complete;
/*     */   private static boolean enable_pending_writes;
/*     */   
/*     */   static
/*     */   {
/*  88 */     int maxFileSize = 262144;
/*     */     try {
/*  90 */       String logSize = System.getProperty("diag.logsize", null);
/*  91 */       if (logSize != null) {
/*  92 */         if (logSize.toLowerCase().endsWith("m")) {
/*  93 */           maxFileSize = Integer.parseInt(logSize.substring(0, logSize.length() - 1)) * 1024 * 1024;
/*     */         }
/*     */         else {
/*  96 */           maxFileSize = Integer.parseInt(logSize);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {}
/* 101 */     MAX_FILE_SIZE = maxFileSize;
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
/* 116 */   private static final Map<String, AEDiagnosticsLogger> loggers = new HashMap();
/*     */   
/*     */   protected static boolean logging_enabled;
/*     */   
/*     */   protected static boolean loggers_enabled;
/* 121 */   private static final List<AEDiagnosticsEvidenceGenerator> evidence_generators = new ArrayList();
/*     */   
/* 123 */   private static final AESemaphore dump_check_done_sem = new AESemaphore("dumpcheckcomplete");
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized void startup(boolean _enable_pending)
/*     */   {
/* 129 */     if (started_up)
/*     */     {
/* 131 */       return;
/*     */     }
/*     */     
/* 134 */     started_up = true;
/*     */     
/* 136 */     enable_pending_writes = _enable_pending;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 141 */       boolean transitoryStartup = System.getProperty("transitory.startup", "0").equals("1");
/*     */       
/* 143 */       if (transitoryStartup)
/*     */       {
/*     */ 
/*     */ 
/* 147 */         loggers_enabled = false;
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 154 */         debug_dir = FileUtil.getUserFile("logs");
/*     */         
/* 156 */         debug_save_dir = new File(debug_dir, "save");
/*     */         
/* 158 */         COConfigurationManager.addAndFireParameterListeners(new String[] { "Logger.Enabled", "Logger.DebugFiles.Enabled" }, new ParameterListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void parameterChanged(String parameterName)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 169 */             AEDiagnostics.logging_enabled = COConfigurationManager.getBooleanParameter("Logger.Enabled");
/*     */             
/* 171 */             AEDiagnostics.loggers_enabled = (AEDiagnostics.logging_enabled) && (COConfigurationManager.getBooleanParameter("Logger.DebugFiles.Enabled"));
/*     */             
/* 173 */             if (!AEDiagnostics.loggers_enabled)
/*     */             {
/* 175 */               boolean skipCVSCheck = System.getProperty("skip.loggers.enabled.cvscheck", "0").equals("1");
/* 176 */               AEDiagnostics.loggers_enabled = ((!skipCVSCheck) && (Constants.IS_CVS_VERSION)) || (COConfigurationManager.getBooleanParameter("Logger.DebugFiles.Enabled.Force"));
/*     */             }
/*     */             
/*     */           }
/* 180 */         });
/* 181 */         boolean was_tidy = COConfigurationManager.getBooleanParameter("diagnostics.tidy_close");
/*     */         
/* 183 */         new AEThread2("asyncify", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/* 188 */             SimpleTimer.addEvent("AEDiagnostics:logCleaner", SystemTime.getCurrentTime() + 60000L + RandomUtils.nextInt(15000), new TimerEventPerformer()
/*     */             {
/*     */               public void perform(TimerEvent event) {}
/*     */             });
/*     */           }
/*     */         }.start();
/*     */         
/*     */ 
/*     */ 
/* 197 */         if (debug_dir.exists())
/*     */         {
/* 199 */           boolean save_logs = System.getProperty("az.logging.save.debug", "true").equals("true");
/*     */           
/* 201 */           long now = SystemTime.getCurrentTime();
/*     */           
/* 203 */           File[] files = debug_dir.listFiles();
/*     */           
/* 205 */           if (files != null)
/*     */           {
/* 207 */             boolean file_found = false;
/*     */             
/* 209 */             for (int i = 0; i < files.length; i++)
/*     */             {
/* 211 */               File file = files[i];
/*     */               
/* 213 */               if (!file.isDirectory())
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 218 */                 if (!was_tidy)
/*     */                 {
/* 220 */                   file_found = true;
/*     */                   
/* 222 */                   if (save_logs)
/*     */                   {
/* 224 */                     if (!debug_save_dir.exists())
/*     */                     {
/* 226 */                       debug_save_dir.mkdir();
/*     */                     }
/*     */                     
/* 229 */                     FileUtil.copyFile(file, new File(debug_save_dir, now + "_" + file.getName()));
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/* 234 */             if (file_found)
/*     */             {
/* 236 */               Logger.logTextResource(new LogAlert(false, 1, "diagnostics.log_found"), new String[] { debug_save_dir.toString() });
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 243 */           debug_dir.mkdir();
/*     */         }
/*     */         
/* 246 */         AEJavaManagement.initialise();
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 252 */       if (!(e instanceof NoClassDefFoundError))
/*     */       {
/* 254 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     finally {
/* 258 */       startup_complete = true;
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
/*     */   private static synchronized void cleanOldLogs()
/*     */   {
/*     */     try
/*     */     {
/* 273 */       long now = SystemTime.getCurrentTime();
/*     */       
/*     */ 
/*     */ 
/* 277 */       File[] files = debug_save_dir.listFiles();
/*     */       
/* 279 */       if (files != null)
/*     */       {
/* 281 */         for (int i = 0; i < files.length; i++)
/*     */         {
/* 283 */           File file = files[i];
/*     */           
/* 285 */           if (!file.isDirectory())
/*     */           {
/* 287 */             long last_modified = file.lastModified();
/*     */             
/* 289 */             if (now - last_modified > 864000000L)
/*     */             {
/* 291 */               file.delete();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isStartupComplete()
/*     */   {
/* 304 */     return startup_complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public static File getLogDir()
/*     */   {
/* 310 */     startup(false);
/*     */     
/* 312 */     return debug_dir;
/*     */   }
/*     */   
/*     */ 
/*     */   public static synchronized void flushPendingLogs()
/*     */   {
/* 318 */     for (AEDiagnosticsLogger logger : loggers.values())
/*     */     {
/* 320 */       logger.writePending();
/*     */     }
/*     */     
/* 323 */     enable_pending_writes = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized AEDiagnosticsLogger getLogger(String name)
/*     */   {
/* 330 */     AEDiagnosticsLogger logger = (AEDiagnosticsLogger)loggers.get(name);
/*     */     
/* 332 */     if (logger == null)
/*     */     {
/* 334 */       startup(false);
/*     */       
/* 336 */       logger = new AEDiagnosticsLogger(debug_dir, name, MAX_FILE_SIZE, !enable_pending_writes);
/*     */       
/* 338 */       loggers.put(name, logger);
/*     */     }
/*     */     
/* 341 */     return logger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void logWithStack(String logger_name, String str)
/*     */   {
/* 349 */     log(logger_name, str + ": " + Debug.getCompressedStackTrace());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void log(String logger_name, String str)
/*     */   {
/* 357 */     getLogger(logger_name).log(str);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void markDirty()
/*     */   {
/*     */     try
/*     */     {
/* 365 */       COConfigurationManager.setParameter("diagnostics.tidy_close", false);
/*     */       
/* 367 */       COConfigurationManager.save();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 371 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isDirty()
/*     */   {
/* 378 */     return !COConfigurationManager.getBooleanParameter("diagnostics.tidy_close");
/*     */   }
/*     */   
/*     */   public static void markClean()
/*     */   {
/*     */     try
/*     */     {
/* 385 */       COConfigurationManager.setParameter("diagnostics.tidy_close", true);
/*     */       
/* 387 */       COConfigurationManager.save();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 391 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 396 */   private static final String[][] bad_dlls = { { "niphk", "y" }, { "nvappfilter", "y" }, { "netdog", "y" }, { "vlsp", "y" }, { "imon", "y" }, { "sarah", "y" }, { "MxAVLsp", "y" }, { "mclsp", "y" }, { "radhslib", "y" }, { "winsflt", "y" }, { "nl_lsp", "y" }, { "AxShlex", "y" }, { "iFW_Xfilter", "y" }, { "gapsp", "y" }, { "WSOCKHK", "n" }, { "InjHook12", "n" }, { "FPServiceProvider", "n" }, { "SBLSP.dll", "y" }, { "nvLsp.dll", "y" } };
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
/*     */   public static void checkDumpsAndNatives()
/*     */   {
/*     */     try
/*     */     {
/* 422 */       PlatformManager p_man = PlatformManagerFactory.getPlatformManager();
/*     */       
/* 424 */       if ((p_man.getPlatformType() == 1) && (p_man.hasCapability(PlatformManagerCapabilities.TestNativeAvailability)))
/*     */       {
/*     */ 
/* 427 */         for (int i = 0; i < bad_dlls.length; i++)
/*     */         {
/* 429 */           String dll = bad_dlls[i][0];
/* 430 */           String load = bad_dlls[i][1];
/*     */           
/* 432 */           if (!load.equalsIgnoreCase("n"))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 437 */             if (!COConfigurationManager.getBooleanParameter("platform.win32.dll_found." + dll, false)) {
/*     */               try
/*     */               {
/* 440 */                 if (p_man.testNativeAvailability(dll + ".dll"))
/*     */                 {
/* 442 */                   COConfigurationManager.setParameter("platform.win32.dll_found." + dll, true);
/*     */                   
/* 444 */                   String detail = MessageText.getString("platform.win32.baddll." + dll);
/*     */                   
/* 446 */                   Logger.logTextResource(new LogAlert(true, 1, "platform.win32.baddll.info"), new String[] { dll + ".dll", detail });
/*     */ 
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/*     */ 
/* 456 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 462 */       List<File> fdirs_to_check = new ArrayList();
/*     */       
/* 464 */       fdirs_to_check.add(new File(SystemProperties.getApplicationPath()));
/*     */       try
/*     */       {
/* 467 */         File temp_file = File.createTempFile("AZU", "tmp");
/*     */         
/* 469 */         fdirs_to_check.add(temp_file.getParentFile());
/*     */         
/* 471 */         temp_file.delete();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/*     */ 
/* 477 */       File most_recent_dump = null;
/* 478 */       long most_recent_time = 0L;
/*     */       
/* 480 */       for (File dir : fdirs_to_check)
/*     */       {
/* 482 */         if (dir.canRead())
/*     */         {
/* 484 */           File[] files = dir.listFiles(new FilenameFilter()
/*     */           {
/*     */ 
/*     */ 
/*     */             public boolean accept(File dir, String name)
/*     */             {
/*     */ 
/*     */ 
/* 492 */               return (name.startsWith("hs_err_pid")) && (name.endsWith(".log"));
/*     */             }
/*     */           });
/*     */           
/* 496 */           if (files != null)
/*     */           {
/* 498 */             long now = SystemTime.getCurrentTime();
/*     */             
/* 500 */             long one_week_ago = now - 604800000L;
/*     */             
/* 502 */             for (int i = 0; i < files.length; i++)
/*     */             {
/* 504 */               File f = files[i];
/*     */               
/* 506 */               long last_mod = f.lastModified();
/*     */               
/* 508 */               if ((last_mod > most_recent_time) && (last_mod > one_week_ago))
/*     */               {
/* 510 */                 most_recent_dump = f;
/* 511 */                 most_recent_time = last_mod;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 518 */       if (most_recent_dump != null)
/*     */       {
/* 520 */         long last_done = COConfigurationManager.getLongParameter("diagnostics.dump.lasttime", 0L);
/*     */         
/*     */ 
/* 523 */         if (last_done < most_recent_time)
/*     */         {
/* 525 */           COConfigurationManager.setParameter("diagnostics.dump.lasttime", most_recent_time);
/*     */           
/* 527 */           analyseDump(most_recent_dump);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 532 */       Debug.printStackTrace(e);
/*     */     }
/*     */     finally
/*     */     {
/* 536 */       dump_check_done_sem.releaseForever();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static void analyseDump(File file)
/*     */   {
/* 544 */     System.out.println("Analysing " + file);
/*     */     try
/*     */     {
/* 547 */       LineNumberReader lnr = new LineNumberReader(new FileReader(file));
/*     */       try
/*     */       {
/* 550 */         boolean float_excep = false;
/* 551 */         boolean swt_crash = false;
/* 552 */         boolean browser_crash = false;
/*     */         
/* 554 */         String[] bad_dlls_uc = new String[bad_dlls.length];
/*     */         
/* 556 */         for (int i = 0; i < bad_dlls.length; i++)
/*     */         {
/* 558 */           String dll = bad_dlls[i][0];
/*     */           
/* 560 */           bad_dlls_uc[i] = (dll + ".dll").toUpperCase();
/*     */         }
/*     */         
/* 563 */         String alcohol_dll = "AxShlex";
/*     */         
/* 565 */         List<String> matches = new ArrayList();
/*     */         
/*     */         for (;;)
/*     */         {
/* 569 */           String line = lnr.readLine();
/*     */           
/* 571 */           if (line == null) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 576 */           line = line.toUpperCase();
/*     */           
/* 578 */           if (line.contains("EXCEPTION_FLT"))
/*     */           {
/* 580 */             float_excep = true;
/*     */           }
/*     */           else
/*     */           {
/* 584 */             if ((line.startsWith("# C")) && (line.contains("[SWT-WIN32")))
/*     */             {
/* 586 */               swt_crash = true;
/*     */             }
/* 588 */             else if ((line.contains("CURRENT THREAD")) && (line.contains("SWT THREAD")))
/*     */             {
/* 590 */               swt_crash = true;
/*     */             }
/* 592 */             else if ((line.startsWith("# C")) && ((line.contains("[IEFRAME")) || (line.contains("[JSCRIPT")) || (line.contains("[FLASH")) || (line.contains("[MSHTML"))))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 598 */               swt_crash = browser_crash = 1;
/*     */             }
/* 600 */             else if (((line.startsWith("J ")) && (line.contains("SWT.BROWSER"))) || ((line.startsWith("C ")) && (line.contains("[IEFRAME"))) || ((line.startsWith("C ")) && (line.contains("[MSHTML"))) || ((line.startsWith("C ")) && (line.contains("[FLASH"))) || ((line.startsWith("C ")) && (line.contains("[JSCRIPT"))))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 606 */               browser_crash = true;
/*     */             }
/*     */             
/* 609 */             for (int i = 0; i < bad_dlls_uc.length; i++)
/*     */             {
/* 611 */               String b_uc = bad_dlls_uc[i];
/*     */               
/* 613 */               if (line.contains(b_uc))
/*     */               {
/* 615 */                 String dll = bad_dlls[i][0];
/*     */                 
/* 617 */                 if (dll.equals(alcohol_dll))
/*     */                 {
/* 619 */                   if (float_excep)
/*     */                   {
/* 621 */                     matches.add(dll);
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 else {
/* 626 */                   matches.add(dll);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 633 */         for (int i = 0; i < matches.size(); i++)
/*     */         {
/* 635 */           String dll = (String)matches.get(i);
/*     */           
/* 637 */           String detail = MessageText.getString("platform.win32.baddll." + dll);
/*     */           
/* 639 */           Logger.logTextResource(new LogAlert(true, 1, "platform.win32.baddll.info"), new String[] { dll + ".dll", detail });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 647 */         if ((swt_crash) && (browser_crash))
/*     */         {
/* 649 */           if (Constants.isWindows)
/*     */           {
/* 651 */             if (!COConfigurationManager.getBooleanParameter("browser.internal.disable", false))
/*     */             {
/* 653 */               COConfigurationManager.setParameter("browser.internal.disable", true);
/*     */               
/* 655 */               COConfigurationManager.save();
/*     */               
/* 657 */               Logger.logTextResource(new LogAlert(true, 1, "browser.internal.auto.disabled"));
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       finally
/*     */       {
/* 667 */         lnr.close();
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 671 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void waitForDumpChecks(long max_wait)
/*     */   {
/* 679 */     dump_check_done_sem.reserve(max_wait);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addEvidenceGenerator(AEDiagnosticsEvidenceGenerator gen)
/*     */   {
/* 686 */     synchronized (evidence_generators)
/*     */     {
/* 688 */       evidence_generators.add(gen);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeEvidenceGenerator(AEDiagnosticsEvidenceGenerator gen)
/*     */   {
/* 696 */     synchronized (evidence_generators)
/*     */     {
/* 698 */       evidence_generators.remove(gen);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void generateEvidence(PrintWriter _writer)
/*     */   {
/* 706 */     IndentWriter writer = new IndentWriter(_writer);
/*     */     
/* 708 */     synchronized (evidence_generators)
/*     */     {
/* 710 */       for (int i = 0; i < evidence_generators.size(); i++) {
/*     */         try
/*     */         {
/* 713 */           ((AEDiagnosticsEvidenceGenerator)evidence_generators.get(i)).generate(writer);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 717 */           e.printStackTrace(_writer);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 722 */     writer.println("Memory");
/*     */     try
/*     */     {
/* 725 */       writer.indent();
/*     */       
/* 727 */       Runtime rt = Runtime.getRuntime();
/*     */       
/* 729 */       writer.println("max=" + rt.maxMemory() + ",total=" + rt.totalMemory() + ",free=" + rt.freeMemory());
/*     */     }
/*     */     finally
/*     */     {
/* 733 */       writer.exdent();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void dumpThreads() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEDiagnostics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */