/*      */ package com.aelitis.azureus.core.backup.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.backup.BackupManager;
/*      */ import com.aelitis.azureus.core.backup.BackupManager.BackupListener;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManager;
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
/*      */ public class BackupManagerImpl
/*      */   implements BackupManager
/*      */ {
/*      */   private static BackupManagerImpl singleton;
/*      */   private final AzureusCore core;
/*      */   
/*      */   public static synchronized BackupManager getSingleton(AzureusCore core)
/*      */   {
/*   80 */     if (singleton == null)
/*      */     {
/*   82 */       singleton = new BackupManagerImpl(core);
/*      */     }
/*      */     
/*   85 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*   90 */   private final AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */   
/*   92 */   private boolean first_schedule_check = true;
/*      */   
/*      */   private TimerEvent backup_event;
/*      */   
/*      */   private long last_auto_backup;
/*      */   
/*      */   private volatile boolean closing;
/*      */   
/*      */ 
/*      */   private BackupManagerImpl(AzureusCore _core)
/*      */   {
/*  103 */     this.core = _core;
/*      */     
/*  105 */     COConfigurationManager.addParameterListener(new String[] { "br.backup.auto.enable", "br.backup.auto.everydays", "br.backup.auto.retain" }, new ParameterListener()
/*      */     {
/*      */       private COConfigurationListener save_listener;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  115 */       final Object lock = this;
/*      */       
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameter)
/*      */       {
/*  121 */         synchronized (this.lock)
/*      */         {
/*  123 */           if (this.save_listener == null)
/*      */           {
/*  125 */             this.save_listener = new COConfigurationListener()
/*      */             {
/*      */ 
/*      */               public void configurationSaved()
/*      */               {
/*      */ 
/*  131 */                 BackupManagerImpl.this.checkSchedule();
/*      */                 
/*  133 */                 COConfigurationManager.removeListener(this);
/*      */                 
/*  135 */                 synchronized (BackupManagerImpl.1.this.lock)
/*      */                 {
/*  137 */                   if (BackupManagerImpl.1.this.save_listener == this)
/*      */                   {
/*  139 */                     BackupManagerImpl.1.this.save_listener = null;
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/*  144 */             };
/*  145 */             COConfigurationManager.addListener(this.save_listener);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  150 */     });
/*  151 */     checkSchedule();
/*      */     
/*  153 */     this.core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void stopping(AzureusCore core)
/*      */       {
/*      */ 
/*      */ 
/*  161 */         BackupManagerImpl.this.closing = true;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastBackupTime()
/*      */   {
/*  169 */     return COConfigurationManager.getLongParameter("br.backup.last.time", 0L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getLastBackupError()
/*      */   {
/*  176 */     return COConfigurationManager.getStringParameter("br.backup.last.error", "");
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkSchedule()
/*      */   {
/*  182 */     checkSchedule(null, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkSchedule(final BackupManager.BackupListener _listener, boolean force)
/*      */   {
/*  190 */     final BackupManager.BackupListener listener = new BackupManager.BackupListener()
/*      */     {
/*      */ 
/*      */       public boolean reportProgress(String str)
/*      */       {
/*      */ 
/*  196 */         if (_listener != null)
/*      */         {
/*      */           try
/*      */           {
/*  200 */             return _listener.reportProgress(str);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  204 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/*  208 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void reportError(Throwable error)
/*      */       {
/*  215 */         if (_listener != null)
/*      */         {
/*      */           try
/*      */           {
/*  219 */             _listener.reportError(error);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  223 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void reportComplete()
/*      */       {
/*  231 */         if (_listener != null)
/*      */         {
/*      */           try
/*      */           {
/*  235 */             _listener.reportComplete();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  239 */             Debug.out(e);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  244 */     };
/*  245 */     boolean enabled = COConfigurationManager.getBooleanParameter("br.backup.auto.enable");
/*      */     
/*  247 */     boolean do_backup = false;
/*      */     
/*  249 */     synchronized (this)
/*      */     {
/*  251 */       if (this.backup_event != null)
/*      */       {
/*  253 */         this.backup_event.cancel();
/*      */         
/*  255 */         this.backup_event = null;
/*      */       }
/*      */       
/*  258 */       if (this.first_schedule_check)
/*      */       {
/*  260 */         if (!enabled)
/*      */         {
/*  262 */           String last_ver = COConfigurationManager.getStringParameter("br.backup.config.info.ver", "");
/*      */           
/*  264 */           String current_ver = "5.7.6.0";
/*      */           
/*  266 */           if (!last_ver.equals(current_ver))
/*      */           {
/*  268 */             COConfigurationManager.setParameter("br.backup.config.info.ver", current_ver);
/*      */             
/*  270 */             Logger.log(new LogAlert(false, 0, MessageText.getString("br.backup.setup.info")));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  278 */         this.first_schedule_check = false;
/*      */         
/*  280 */         if (!force)
/*      */         {
/*  282 */           if (enabled)
/*      */           {
/*  284 */             this.backup_event = SimpleTimer.addEvent("BM:startup", SystemTime.getCurrentTime() + 300000L, new TimerEventPerformer()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public void perform(TimerEvent event)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  294 */                 BackupManagerImpl.this.checkSchedule();
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*  299 */           return;
/*      */         }
/*      */       }
/*      */       
/*  303 */       if (!enabled)
/*      */       {
/*  305 */         listener.reportError(new Exception("Auto-backup not enabled"));
/*      */         
/*  307 */         return;
/*      */       }
/*      */       
/*  310 */       long now_utc = SystemTime.getCurrentTime();
/*      */       
/*  312 */       int offset = TimeZone.getDefault().getOffset(now_utc);
/*      */       
/*  314 */       long now_local = now_utc + offset;
/*      */       
/*  316 */       long DAY = 86400000L;
/*      */       
/*  318 */       long local_day_index = now_local / DAY;
/*      */       
/*  320 */       long last_auto_backup_day = COConfigurationManager.getLongParameter("br.backup.auto.last_backup_day", 0L);
/*      */       
/*  322 */       if (last_auto_backup_day > local_day_index)
/*      */       {
/*  324 */         last_auto_backup_day = local_day_index;
/*      */       }
/*      */       
/*  327 */       long backup_every_days = COConfigurationManager.getLongParameter("br.backup.auto.everydays");
/*      */       
/*  329 */       backup_every_days = Math.max(1L, backup_every_days);
/*      */       
/*  331 */       long utc_next_backup = (last_auto_backup_day + backup_every_days) * DAY;
/*      */       
/*  333 */       long time_to_next_backup = utc_next_backup - now_local;
/*      */       
/*  335 */       if ((time_to_next_backup <= 0L) || (force))
/*      */       {
/*  337 */         if ((now_utc - this.last_auto_backup >= 14400000L) || (force))
/*      */         {
/*  339 */           do_backup = true;
/*      */           
/*  341 */           this.last_auto_backup = now_utc;
/*      */           
/*  343 */           COConfigurationManager.setParameter("br.backup.auto.last_backup_day", local_day_index);
/*      */         }
/*      */         else
/*      */         {
/*  347 */           time_to_next_backup = 14400000L;
/*      */         }
/*      */       }
/*      */       
/*  351 */       if (!do_backup)
/*      */       {
/*  353 */         time_to_next_backup = Math.max(time_to_next_backup, 60000L);
/*      */         
/*  355 */         listener.reportProgress("Scheduling next backup in " + TimeFormatter.format(time_to_next_backup / 1000L));
/*      */         
/*  357 */         this.backup_event = SimpleTimer.addEvent("BM:auto", now_utc + time_to_next_backup, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  367 */             BackupManagerImpl.this.checkSchedule();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*  373 */     if (do_backup)
/*      */     {
/*  375 */       String backup_dir = COConfigurationManager.getStringParameter("br.backup.auto.dir", "");
/*      */       
/*  377 */       listener.reportProgress("Auto backup starting: folder=" + backup_dir);
/*      */       
/*  379 */       final File target_dir = new File(backup_dir);
/*      */       
/*  381 */       backup(target_dir, new BackupManager.BackupListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean reportProgress(String str)
/*      */         {
/*      */ 
/*      */ 
/*  389 */           return listener.reportProgress(str);
/*      */         }
/*      */         
/*      */         public void reportComplete()
/*      */         {
/*      */           try
/*      */           {
/*  396 */             System.out.println("Auto backup completed");
/*      */             
/*  398 */             COConfigurationManager.save();
/*      */             
/*      */ 
/*  401 */             if (COConfigurationManager.getBooleanParameter("br.backup.notify"))
/*      */             {
/*  403 */               Logger.log(new LogAlert(true, 0, "Backup completed at " + new Date()));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  410 */             int backup_retain = COConfigurationManager.getIntParameter("br.backup.auto.retain");
/*      */             
/*  412 */             backup_retain = Math.max(1, backup_retain);
/*      */             
/*  414 */             File[] backups = target_dir.listFiles();
/*      */             
/*  416 */             List<File> backup_dirs = new ArrayList();
/*      */             
/*  418 */             for (File f : backups)
/*      */             {
/*  420 */               if ((f.isDirectory()) && (BackupManagerImpl.this.getBackupDirTime(f) > 0L))
/*      */               {
/*  422 */                 File test_file = new File(f, "azureus.config");
/*      */                 
/*  424 */                 if (test_file.exists())
/*      */                 {
/*  426 */                   backup_dirs.add(f);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  431 */             Collections.sort(backup_dirs, new Comparator()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public int compare(File o1, File o2)
/*      */               {
/*      */ 
/*      */ 
/*  440 */                 long t1 = BackupManagerImpl.this.getBackupDirTime(o1);
/*  441 */                 long t2 = BackupManagerImpl.this.getBackupDirTime(o2);
/*      */                 
/*  443 */                 long res = t2 - t1;
/*      */                 
/*  445 */                 if (res < 0L)
/*  446 */                   return -1;
/*  447 */                 if (res > 0L) {
/*  448 */                   return 1;
/*      */                 }
/*  450 */                 Debug.out("hmm: " + o1 + "/" + o2);
/*      */                 
/*  452 */                 return 0;
/*      */               }
/*      */             });
/*      */             
/*      */ 
/*  457 */             for (int i = backup_retain; i < backup_dirs.size(); i++)
/*      */             {
/*  459 */               File f = (File)backup_dirs.get(i);
/*      */               
/*  461 */               listener.reportProgress("Deleting old backup: " + f);
/*      */               
/*  463 */               FileUtil.recursiveDeleteNoCheck(f);
/*      */             }
/*      */           }
/*      */           finally {
/*  467 */             listener.reportComplete();
/*      */             
/*  469 */             BackupManagerImpl.this.checkSchedule();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public void reportError(Throwable error)
/*      */         {
/*      */           try
/*      */           {
/*  478 */             listener.reportProgress("Auto backup failed");
/*      */             
/*  480 */             Logger.log(new LogAlert(true, 3, "Backup failed at " + new Date(), error));
/*      */ 
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*      */ 
/*      */ 
/*  488 */             listener.reportError(error);
/*      */             
/*  490 */             BackupManagerImpl.this.checkSchedule();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     else
/*      */     {
/*  497 */       listener.reportError(new Exception("Backup not scheduled to run now"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void runAutoBackup(BackupManager.BackupListener listener)
/*      */   {
/*  505 */     checkSchedule(listener, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void backup(final File parent_folder, final BackupManager.BackupListener _listener)
/*      */   {
/*  513 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  519 */         BackupManager.BackupListener listener = new BackupManager.BackupListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean reportProgress(String str)
/*      */           {
/*      */ 
/*  526 */             return BackupManagerImpl.7.this.val$_listener.reportProgress(str);
/*      */           }
/*      */           
/*      */           public void reportComplete()
/*      */           {
/*      */             try
/*      */             {
/*  533 */               BackupManagerImpl.7.this.setStatus("");
/*      */             }
/*      */             finally
/*      */             {
/*  537 */               BackupManagerImpl.7.this.val$_listener.reportComplete();
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */           public void reportError(Throwable error)
/*      */           {
/*      */             try
/*      */             {
/*  546 */               BackupManagerImpl.7.this.setStatus(Debug.getNestedExceptionMessage(error));
/*      */             }
/*      */             finally
/*      */             {
/*  550 */               BackupManagerImpl.7.this.val$_listener.reportError(error);
/*      */             }
/*      */             
/*      */           }
/*  554 */         };
/*  555 */         BackupManagerImpl.this.backupSupport(parent_folder, listener);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private void setStatus(String error)
/*      */       {
/*  562 */         COConfigurationManager.setParameter("br.backup.last.time", SystemTime.getCurrentTime());
/*  563 */         COConfigurationManager.setParameter("br.backup.last.error", error);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkClosing()
/*      */     throws Exception
/*      */   {
/*  573 */     if (this.closing)
/*      */     {
/*  575 */       throw new Exception("operation cancelled, app is closing");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long[] copyFiles(File from_file, File to_file)
/*      */     throws Exception
/*      */   {
/*  586 */     return copyFilesSupport(from_file, to_file, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long[] copyFilesSupport(File from_file, File to_file, int depth)
/*      */     throws Exception
/*      */   {
/*  597 */     long total_files = 0L;
/*  598 */     long total_copied = 0L;
/*      */     
/*  600 */     if (depth > 16)
/*      */     {
/*      */ 
/*      */ 
/*  604 */       throw new Exception("Loop detected in backup path, abandoning");
/*      */     }
/*      */     
/*  607 */     if (from_file.isDirectory())
/*      */     {
/*  609 */       if (!to_file.mkdirs())
/*      */       {
/*  611 */         throw new Exception("Failed to create '" + to_file.getAbsolutePath() + "'");
/*      */       }
/*      */       
/*  614 */       File[] files = from_file.listFiles();
/*      */       
/*  616 */       for (File f : files)
/*      */       {
/*  618 */         checkClosing();
/*      */         
/*  620 */         long[] temp = copyFilesSupport(f, new File(to_file, f.getName()), depth + 1);
/*      */         
/*  622 */         total_files += temp[0];
/*  623 */         total_copied += temp[1];
/*      */       }
/*      */     }
/*      */     else {
/*  627 */       if (!FileUtil.copyFile(from_file, to_file))
/*      */       {
/*      */         try {
/*  630 */           Thread.sleep(5000L);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*  635 */         if (!FileUtil.copyFile(from_file, to_file))
/*      */         {
/*      */ 
/*      */ 
/*  639 */           String name = from_file.getName().toLowerCase(Locale.US);
/*      */           
/*  641 */           String full_name = from_file.getAbsolutePath().toLowerCase(Locale.US);
/*      */           
/*  643 */           if ((name.startsWith(".lock")) || (name.startsWith("lock")) || (name.equals("stats.lck")) || (name.endsWith(".saving")) || (name.endsWith(".gz")) || (name.endsWith(".jar")) || (name.endsWith(".zip")) || (name.endsWith(".dll")) || (name.endsWith(".so")) || (full_name.contains(File.separator + "cache" + File.separator)))
/*      */           {
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
/*  655 */             return new long[] { total_files, total_copied };
/*      */           }
/*      */           
/*  658 */           throw new Exception("Failed to copy file '" + from_file + "'");
/*      */         }
/*      */       }
/*      */       
/*  662 */       total_files += 1L;
/*      */       
/*  664 */       total_copied = from_file.length();
/*      */     }
/*      */     
/*  667 */     return new long[] { total_files, total_copied };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private long getBackupDirTime(File file)
/*      */   {
/*  674 */     String name = file.getName();
/*      */     
/*  676 */     int pos = name.indexOf(".");
/*      */     
/*  678 */     long suffix = 0L;
/*      */     
/*  680 */     if (pos != -1) {
/*      */       try
/*      */       {
/*  683 */         suffix = Integer.parseInt(name.substring(pos + 1));
/*      */         
/*  685 */         name = name.substring(0, pos);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  689 */         return -1L;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  694 */       return new SimpleDateFormat("yyyy-MM-dd").parse(name).getTime() + suffix;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  698 */     return -1L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void backupSupport(File parent_folder, final BackupManager.BackupListener _listener)
/*      */   {
/*      */     try
/*      */     {
/*  708 */       String date_dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
/*      */       
/*  710 */       File backup_folder = null;
/*  711 */       boolean ok = false;
/*      */       try
/*      */       {
/*  714 */         checkClosing();
/*      */         
/*  716 */         if ((parent_folder.getName().length() == 0) || (!parent_folder.isDirectory()))
/*      */         {
/*      */ 
/*  719 */           throw new Exception("Backup folder '" + parent_folder + "' is invalid");
/*      */         }
/*      */         
/*  722 */         BackupManager.BackupListener listener = new BackupManager.BackupListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean reportProgress(String str)
/*      */           {
/*      */ 
/*  729 */             if (!_listener.reportProgress(str))
/*      */             {
/*  731 */               throw new RuntimeException("Operation abandoned by listener");
/*      */             }
/*      */             
/*  734 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */           public void reportComplete()
/*      */           {
/*  740 */             _listener.reportComplete();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void reportError(Throwable error)
/*      */           {
/*  747 */             _listener.reportError(error);
/*      */           }
/*      */           
/*  750 */         };
/*  751 */         int max_suffix = -1;
/*      */         
/*  753 */         String[] existing = parent_folder.list();
/*      */         
/*  755 */         if (existing != null)
/*      */         {
/*  757 */           for (String ex : existing)
/*      */           {
/*  759 */             if (ex.startsWith(date_dir))
/*      */             {
/*  761 */               int pos = ex.indexOf(".");
/*      */               
/*  763 */               if (pos >= 0) {
/*      */                 try
/*      */                 {
/*  766 */                   max_suffix = Math.max(max_suffix, Integer.parseInt(ex.substring(pos + 1)));
/*      */ 
/*      */ 
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */ 
/*      */               }
/*  773 */               else if (max_suffix == -1)
/*      */               {
/*  775 */                 max_suffix = 0;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  782 */         for (int i = max_suffix + 1; i < 100; i++)
/*      */         {
/*  784 */           String test_dir = date_dir;
/*      */           
/*  786 */           if (i > 0)
/*      */           {
/*  788 */             test_dir = test_dir + "." + i;
/*      */           }
/*      */           
/*  791 */           File test_file = new File(parent_folder, test_dir);
/*      */           
/*  793 */           if (!test_file.exists())
/*      */           {
/*  795 */             backup_folder = test_file;
/*      */             
/*  797 */             backup_folder.mkdirs();
/*      */             
/*  799 */             break;
/*      */           }
/*      */         }
/*      */         
/*  803 */         if (backup_folder == null)
/*      */         {
/*  805 */           backup_folder = new File(parent_folder, date_dir);
/*      */         }
/*      */         
/*  808 */         File user_dir = new File(SystemProperties.getUserPath());
/*      */         
/*  810 */         File temp_dir = backup_folder;
/*      */         
/*  812 */         while (temp_dir != null)
/*      */         {
/*  814 */           if (temp_dir.equals(user_dir))
/*      */           {
/*  816 */             throw new Exception("Backup folder '" + backup_folder + "' is not permitted to be within the configuration folder '" + user_dir + "'.\r\nSelect an alternative location.");
/*      */           }
/*      */           
/*  819 */           temp_dir = temp_dir.getParentFile();
/*      */         }
/*      */         
/*  822 */         listener.reportProgress("Writing to " + backup_folder.getAbsolutePath());
/*      */         
/*  824 */         if ((!backup_folder.exists()) && (!backup_folder.mkdirs()))
/*      */         {
/*  826 */           throw new Exception("Failed to create '" + backup_folder.getAbsolutePath() + "'");
/*      */         }
/*      */         
/*  829 */         listener.reportProgress("Syncing current state");
/*      */         
/*  831 */         this.core.saveState();
/*      */         try
/*      */         {
/*  834 */           listener.reportProgress("Reading configuration data from " + user_dir.getAbsolutePath());
/*      */           
/*  836 */           File[] user_files = user_dir.listFiles();
/*      */           
/*  838 */           for (File f : user_files)
/*      */           {
/*  840 */             checkClosing();
/*      */             
/*  842 */             String name = f.getName();
/*      */             
/*  844 */             if (f.isDirectory() ? 
/*      */             
/*  846 */               (!name.equals("cache")) || (!name.equals("tmp")) || (!name.equals("logs")) || (!name.equals("updates")) || (!name.equals("debug")) : 
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  854 */               (!name.equals(".lock")) && (!name.equals(".azlock")) && (!name.equals("update.properties")) && (!name.endsWith(".log")))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  862 */               File dest_file = new File(backup_folder, name);
/*      */               
/*  864 */               listener.reportProgress("Copying '" + name + "' ...");
/*      */               
/*  866 */               long[] result = copyFiles(f, dest_file);
/*      */               
/*  868 */               String result_str = DisplayFormatters.formatByteCountToKiBEtc(result[1]);
/*      */               
/*  870 */               if (result[0] > 1L)
/*      */               {
/*  872 */                 result_str = result[0] + " files, " + result_str;
/*      */               }
/*      */               
/*  875 */               listener.reportProgress(result_str);
/*      */             }
/*      */           }
/*  878 */           listener.reportComplete();
/*      */           
/*  880 */           ok = true;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  884 */           throw e;
/*      */         }
/*      */       }
/*      */       finally {
/*  888 */         if (!ok)
/*      */         {
/*  890 */           if (backup_folder != null)
/*      */           {
/*  892 */             FileUtil.recursiveDeleteNoCheck(backup_folder);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  898 */       _listener.reportError(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void restore(final File backup_folder, final BackupManager.BackupListener listener)
/*      */   {
/*  907 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  913 */         BackupManagerImpl.this.restoreSupport(backup_folder, listener);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addActions(UpdateInstaller installer, File source, File target)
/*      */     throws Exception
/*      */   {
/*  926 */     if (source.isDirectory())
/*      */     {
/*  928 */       File[] files = source.listFiles();
/*      */       
/*  930 */       for (File f : files)
/*      */       {
/*  932 */         addActions(installer, f, new File(target, f.getName()));
/*      */       }
/*      */     }
/*      */     else {
/*  936 */       installer.addMoveAction(source.getAbsolutePath(), target.getAbsolutePath());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int patch(Map<String, Object> map, String from, String to)
/*      */   {
/*  948 */     int mods = 0;
/*      */     
/*  950 */     Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
/*      */     
/*  952 */     Map<String, Object> replacements = new HashMap();
/*      */     
/*  954 */     while (it.hasNext())
/*      */     {
/*  956 */       Map.Entry<String, Object> entry = (Map.Entry)it.next();
/*      */       
/*  958 */       String key = (String)entry.getKey();
/*      */       
/*  960 */       Object value = entry.getValue();
/*      */       
/*  962 */       Object new_value = value;
/*      */       
/*  964 */       if ((value instanceof Map))
/*      */       {
/*  966 */         mods += patch((Map)value, from, to);
/*      */       }
/*  968 */       else if ((value instanceof List))
/*      */       {
/*  970 */         mods += patch((List)value, from, to);
/*      */       }
/*  972 */       else if ((value instanceof byte[])) {
/*      */         try
/*      */         {
/*  975 */           String str = new String((byte[])value, "UTF-8");
/*      */           
/*  977 */           if (str.startsWith(from))
/*      */           {
/*  979 */             new_value = to + str.substring(from.length());
/*      */             
/*  981 */             mods++;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*  987 */       if (key.startsWith(from))
/*      */       {
/*      */ 
/*      */ 
/*  991 */         String new_key = to + key.substring(from.length());
/*      */         
/*  993 */         mods++;
/*      */         
/*  995 */         it.remove();
/*      */         
/*  997 */         replacements.put(new_key, new_value);
/*      */ 
/*      */ 
/*      */       }
/* 1001 */       else if (value != new_value)
/*      */       {
/* 1003 */         entry.setValue(new_value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1008 */     map.putAll(replacements);
/*      */     
/* 1010 */     return mods;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int patch(List list, String from, String to)
/*      */   {
/* 1019 */     int mods = 0;
/*      */     
/* 1021 */     for (int i = 0; i < list.size(); i++)
/*      */     {
/* 1023 */       Object entry = list.get(i);
/*      */       
/* 1025 */       if ((entry instanceof Map))
/*      */       {
/* 1027 */         mods += patch((Map)entry, from, to);
/*      */       }
/* 1029 */       else if ((entry instanceof List))
/*      */       {
/* 1031 */         mods += patch((List)entry, from, to);
/*      */       }
/* 1033 */       else if ((entry instanceof byte[])) {
/*      */         try
/*      */         {
/* 1036 */           String str = new String((byte[])entry, "UTF-8");
/*      */           
/* 1038 */           if (str.startsWith(from))
/*      */           {
/* 1040 */             list.set(i, to + str.substring(from.length()));
/*      */             
/* 1042 */             mods++;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/* 1049 */     return mods;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void restoreSupport(File backup_folder, BackupManager.BackupListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 1058 */       UpdateInstaller installer = null;
/* 1059 */       File temp_dir = null;
/*      */       
/* 1061 */       boolean ok = false;
/*      */       try
/*      */       {
/* 1064 */         listener.reportProgress("Reading from " + backup_folder.getAbsolutePath());
/*      */         
/* 1066 */         if (!backup_folder.isDirectory())
/*      */         {
/* 1068 */           throw new Exception("Location '" + backup_folder.getAbsolutePath() + "' must be a directory");
/*      */         }
/*      */         
/* 1071 */         listener.reportProgress("Analysing backup");
/*      */         
/* 1073 */         File config = new File(backup_folder, "azureus.config");
/*      */         
/* 1075 */         if (!config.exists())
/*      */         {
/* 1077 */           throw new Exception("Invalid backup: azureus.config not found");
/*      */         }
/*      */         
/* 1080 */         Map config_map = BDecoder.decode(FileUtil.readFileAsByteArray(config));
/*      */         
/* 1082 */         byte[] temp = (byte[])config_map.get("azureus.user.directory");
/*      */         
/* 1084 */         if (temp == null)
/*      */         {
/* 1086 */           throw new Exception("Invalid backup: azureus.config doesn't contain user directory details");
/*      */         }
/*      */         
/* 1089 */         File current_user_dir = new File(SystemProperties.getUserPath());
/* 1090 */         File backup_user_dir = new File(new String(temp, "UTF-8"));
/*      */         
/* 1092 */         listener.reportProgress("Current user directory:\t" + current_user_dir.getAbsolutePath());
/* 1093 */         listener.reportProgress("Backup's user directory:\t" + backup_user_dir.getAbsolutePath());
/*      */         
/* 1095 */         temp_dir = AETemporaryFileHandler.createTempDir();
/*      */         
/* 1097 */         PluginInterface pi = this.core.getPluginManager().getDefaultPluginInterface();
/*      */         
/* 1099 */         installer = pi.getUpdateManager().createInstaller();
/*      */         
/* 1101 */         File[] files = backup_folder.listFiles();
/*      */         
/* 1103 */         if (current_user_dir.equals(backup_user_dir))
/*      */         {
/* 1105 */           listener.reportProgress("Directories are the same, no patching required");
/*      */           
/* 1107 */           for (File f : files)
/*      */           {
/* 1109 */             File source = new File(temp_dir, f.getName());
/*      */             
/* 1111 */             listener.reportProgress("Creating restore action for '" + f.getName() + "'");
/*      */             
/* 1113 */             copyFiles(f, source);
/*      */             
/* 1115 */             File target = new File(current_user_dir, f.getName());
/*      */             
/* 1117 */             addActions(installer, source, target);
/*      */           }
/*      */         }
/*      */         else {
/* 1121 */           listener.reportProgress("Directories are different, backup requires patching");
/*      */           label813:
/* 1123 */           for (File f : files)
/*      */           {
/* 1125 */             File source = new File(temp_dir, f.getName());
/*      */             
/* 1127 */             listener.reportProgress("Creating restore action for '" + f.getName() + "'");
/*      */             
/* 1129 */             if ((f.isDirectory()) || (!f.getName().contains(".config")))
/*      */             {
/* 1131 */               copyFiles(f, source);
/*      */             }
/*      */             else
/*      */             {
/* 1135 */               boolean patched = false;
/*      */               
/* 1137 */               BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f), 1048576);
/*      */               try
/*      */               {
/* 1140 */                 Map m = BDecoder.decode(bis);
/*      */                 
/* 1142 */                 bis.close();
/*      */                 
/* 1144 */                 bis = null;
/*      */                 
/* 1146 */                 if (m.size() > 0)
/*      */                 {
/* 1148 */                   int applied = patch(m, backup_user_dir.getAbsolutePath(), current_user_dir.getAbsolutePath());
/*      */                   
/* 1150 */                   if (applied > 0)
/*      */                   {
/* 1152 */                     listener.reportProgress("    Applied " + applied + " patches");
/*      */                     
/* 1154 */                     patched = FileUtil.writeBytesAsFile2(source.getAbsolutePath(), BEncoder.encode(m));
/*      */                     
/* 1156 */                     if (!patched)
/*      */                     {
/* 1158 */                       throw new Exception("Failed to write " + source);
/*      */                     }
/*      */                   }
/*      */                 }
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
/* 1179 */                 if (bis != null) {
/*      */                   try
/*      */                   {
/* 1182 */                     bis.close();
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */                 
/*      */ 
/*      */                 String name;
/*      */                 
/* 1190 */                 if (patched) {
/*      */                   break label813;
/*      */                 }
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1164 */                 name = f.getName();
/*      */                 
/*      */ 
/*      */ 
/* 1168 */                 if ((name.contains(".bad")) || (name.contains(".bak")))
/*      */                 {
/* 1170 */                   listener.reportProgress("    Ignored failure to patch bad configuration file");
/*      */                 }
/*      */                 else
/*      */                 {
/* 1174 */                   throw e;
/*      */                 }
/*      */               }
/*      */               finally
/*      */               {
/* 1179 */                 if (bis != null) {
/*      */                   try
/*      */                   {
/* 1182 */                     bis.close();
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1192 */               copyFiles(f, source);
/*      */             }
/*      */             
/*      */ 
/* 1196 */             File target = new File(current_user_dir, f.getName());
/*      */             
/* 1198 */             addActions(installer, source, target);
/*      */           }
/*      */         }
/*      */         
/* 1202 */         listener.reportProgress("Restore action creation complete, restart required to complete the operation");
/*      */         
/* 1204 */         listener.reportComplete();
/*      */         
/* 1206 */         ok = true;
/*      */       }
/*      */       finally
/*      */       {
/* 1210 */         if (!ok)
/*      */         {
/* 1212 */           if (installer != null)
/*      */           {
/* 1214 */             installer.destroy();
/*      */           }
/*      */           
/* 1217 */           if (temp_dir != null)
/*      */           {
/* 1219 */             FileUtil.recursiveDeleteNoCheck(temp_dir);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1225 */       listener.reportError(e);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/backup/impl/BackupManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */