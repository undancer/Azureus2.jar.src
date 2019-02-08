/*     */ package org.gudy.azureus2.pluginsimpl.local.update;
/*     */ 
/*     */ import com.aelitis.azureus.core.update.AzureusRestarter;
/*     */ import com.aelitis.azureus.core.update.AzureusRestarterFactory;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.update.UpdateException;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstallerListener;
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
/*     */ public class UpdateInstallerImpl
/*     */   implements UpdateInstaller
/*     */ {
/*     */   protected static final String UPDATE_DIR = "updates";
/*     */   protected static final String ACTIONS_LEGACY = "install.act";
/*     */   protected static final String ACTIONS_UTF8 = "install.act.utf8";
/*  51 */   protected static AEMonitor class_mon = new AEMonitor("UpdateInstaller:class");
/*     */   
/*     */   private UpdateManagerImpl manager;
/*     */   
/*     */   private File install_dir;
/*     */   
/*     */   protected static void checkForFailedInstalls(UpdateManagerImpl manager)
/*     */   {
/*     */     try
/*     */     {
/*  61 */       File update_dir = new File(manager.getUserDir() + File.separator + "updates");
/*     */       
/*  63 */       File[] dirs = update_dir.listFiles();
/*     */       
/*  65 */       if (dirs != null)
/*     */       {
/*  67 */         boolean found_failure = false;
/*     */         
/*  69 */         String files = "";
/*     */         
/*  71 */         for (int i = 0; i < dirs.length; i++)
/*     */         {
/*  73 */           File dir = dirs[i];
/*     */           
/*  75 */           if (dir.isDirectory())
/*     */           {
/*     */ 
/*     */ 
/*  79 */             found_failure = true;
/*     */             
/*  81 */             File[] x = dir.listFiles();
/*     */             
/*  83 */             if (x != null)
/*     */             {
/*  85 */               for (int j = 0; j < x.length; j++)
/*     */               {
/*  87 */                 files = files + (files.length() == 0 ? "" : ",") + x[j].getName();
/*     */               }
/*     */             }
/*     */             
/*  91 */             FileUtil.recursiveDelete(dir);
/*     */           }
/*     */         }
/*     */         
/*  95 */         if (found_failure) {
/*  96 */           Logger.log(new LogAlert(false, 3, MessageText.getString("Alert.failed.update", new String[] { files })));
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 102 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UpdateInstallerImpl(UpdateManagerImpl _manager)
/*     */     throws UpdateException
/*     */   {
/* 112 */     this.manager = _manager;
/*     */     try
/*     */     {
/* 115 */       class_mon.enter();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 120 */       String update_dir = getUserDir() + File.separator + "updates";
/*     */       
/* 122 */       for (int i = 1; i < 1024; i++)
/*     */       {
/* 124 */         File try_dir = new File(update_dir + File.separator + "inst_" + i);
/*     */         
/* 126 */         if (!try_dir.exists())
/*     */         {
/* 128 */           if (!FileUtil.mkdirs(try_dir))
/*     */           {
/* 130 */             throw new UpdateException("Failed to create a temporary installation dir");
/*     */           }
/*     */           
/* 133 */           this.install_dir = try_dir;
/*     */           
/* 135 */           break;
/*     */         }
/*     */       }
/*     */       
/* 139 */       if (this.install_dir == null)
/*     */       {
/* 141 */         throw new UpdateException("Failed to find a temporary installation dir");
/*     */       }
/*     */     }
/*     */     finally {
/* 145 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addResource(String resource_name, InputStream is)
/*     */     throws UpdateException
/*     */   {
/* 156 */     addResource(resource_name, is, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addResource(String resource_name, InputStream is, boolean closeInputStream)
/*     */     throws UpdateException
/*     */   {
/*     */     try
/*     */     {
/* 168 */       File target_file = new File(this.install_dir, resource_name);
/*     */       
/* 170 */       FileUtil.copyFile(is, new FileOutputStream(target_file), closeInputStream);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 174 */       throw new UpdateException("UpdateInstaller: resource addition fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getInstallDir()
/*     */   {
/* 181 */     return this.manager.getInstallDir();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUserDir()
/*     */   {
/* 187 */     return this.manager.getUserDir();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addMoveAction(String from_file_or_resource, String to_file)
/*     */     throws UpdateException
/*     */   {
/* 199 */     if (!from_file_or_resource.contains(File.separator))
/*     */     {
/* 201 */       from_file_or_resource = this.install_dir.toString() + File.separator + from_file_or_resource;
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 207 */       File to_f = new File(to_file);
/*     */       
/* 209 */       File parent = to_f.getParentFile();
/*     */       
/* 211 */       if ((parent != null) && (!parent.exists()))
/*     */       {
/* 213 */         parent.mkdirs();
/*     */       }
/*     */       
/* 216 */       boolean log_perm_set_fail = true;
/*     */       
/* 218 */       if (parent != null)
/*     */       {
/*     */ 
/*     */ 
/* 222 */         if (!parent.canWrite())
/*     */         {
/* 224 */           log_perm_set_fail = false;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 229 */           if (!Constants.isWindowsVistaOrHigher)
/*     */           {
/* 231 */             Logger.log(new LogAlert(false, 1, "The location '" + parent.toString() + "' isn't writable, this update will probably fail." + " Check permissions and retry the update"));
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 240 */           PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*     */           
/* 242 */           if (pm.hasCapability(PlatformManagerCapabilities.CopyFilePermissions))
/*     */           {
/* 244 */             String parent_str = parent.getAbsolutePath();
/*     */             
/* 246 */             PlatformManagerFactory.getPlatformManager().copyFilePermissions(parent_str, from_file_or_resource);
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 251 */           if (log_perm_set_fail)
/*     */           {
/* 253 */             if (!Constants.isWindowsVistaOrHigher)
/*     */             {
/* 255 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 264 */     from_file_or_resource = escapeFile(from_file_or_resource);
/* 265 */     to_file = escapeFile(to_file);
/*     */     
/* 267 */     appendAction("move," + from_file_or_resource + "," + to_file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addChangeRightsAction(String rights, String to_file)
/*     */     throws UpdateException
/*     */   {
/* 278 */     to_file = escapeFile(to_file);
/*     */     
/* 280 */     appendAction("chmod," + rights + "," + to_file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRemoveAction(String file)
/*     */     throws UpdateException
/*     */   {
/* 289 */     file = escapeFile(file);
/*     */     
/* 291 */     appendAction("remove," + file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String escapeFile(String file)
/*     */   {
/* 298 */     if (file.contains(","))
/*     */     {
/* 300 */       file = file.replaceAll(",", "&#0002C;");
/*     */     }
/*     */     
/* 303 */     return file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void appendAction(String data)
/*     */     throws UpdateException
/*     */   {
/* 312 */     PrintWriter pw_legacy = null;
/*     */     
/*     */     try
/*     */     {
/* 316 */       pw_legacy = new PrintWriter(new FileWriter(this.install_dir.toString() + File.separator + "install.act", true));
/*     */       
/* 318 */       pw_legacy.println(data);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 326 */       if (pw_legacy != null) {
/*     */         try
/*     */         {
/* 329 */           pw_legacy.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 333 */           throw new UpdateException("Failed to write actions file", e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 338 */       pw_utf8 = null;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 322 */       throw new UpdateException("Failed to write actions file", e);
/*     */     }
/*     */     finally
/*     */     {
/* 326 */       if (pw_legacy != null) {
/*     */         try
/*     */         {
/* 329 */           pw_legacy.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 333 */           throw new UpdateException("Failed to write actions file", e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 342 */       PrintWriter pw_utf8 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.install_dir.toString() + File.separator + "install.act.utf8", true), "UTF-8"));
/*     */       
/* 344 */       pw_utf8.println(data); return;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 348 */       throw new UpdateException("Failed to write actions file", e);
/*     */     }
/*     */     finally
/*     */     {
/* 352 */       if (pw_utf8 != null) {
/*     */         try
/*     */         {
/* 355 */           pw_utf8.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 359 */           throw new UpdateException("Failed to write actions file", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void installNow(final UpdateInstallerListener listener)
/*     */     throws UpdateException
/*     */   {
/*     */     try
/*     */     {
/* 372 */       UpdateInstaller[] installers = this.manager.getInstallers();
/*     */       
/* 374 */       if ((installers.length != 1) || (installers[0] != this))
/*     */       {
/* 376 */         throw new UpdateException("Other installers exist - aborting");
/*     */       }
/*     */       
/* 379 */       listener.reportProgress("Update starts");
/*     */       
/* 381 */       AzureusRestarter ar = AzureusRestarterFactory.create(this.manager.getCore());
/*     */       
/* 383 */       ar.updateNow();
/*     */       
/* 385 */       new AEThread2("installNow:waiter", true)
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/* 391 */             long start = SystemTime.getMonotonousTime();
/*     */             
/* 393 */             UpdateException pending_error = null;
/*     */             
/*     */             for (;;)
/*     */             {
/* 397 */               Thread.sleep(1000L);
/*     */               
/* 399 */               listener.reportProgress("Checking progress");
/*     */               
/* 401 */               if (!UpdateInstallerImpl.this.install_dir.exists()) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 406 */               File fail_file = new File(UpdateInstallerImpl.this.install_dir, "install.fail");
/*     */               
/* 408 */               if (fail_file.exists()) {
/*     */                 try
/*     */                 {
/* 411 */                   String error = FileUtil.readFileAsString(fail_file, 1024);
/*     */                   
/* 413 */                   throw new UpdateException(error);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 417 */                   if ((e instanceof UpdateException))
/*     */                   {
/* 419 */                     throw e;
/*     */                   }
/*     */                   
/* 422 */                   if (pending_error != null)
/*     */                   {
/* 424 */                     throw pending_error;
/*     */                   }
/*     */                   
/* 427 */                   pending_error = new UpdateException("Install failed, reason unknown");
/*     */                 }
/*     */               }
/*     */               
/* 431 */               if (SystemTime.getMonotonousTime() - start >= 300000L)
/*     */               {
/* 433 */                 listener.reportProgress("Timeout");
/*     */                 
/* 435 */                 throw new UpdateException("Timeout waiting for update to apply");
/*     */               }
/*     */             }
/*     */             
/* 439 */             listener.reportProgress("Complete");
/*     */             
/* 441 */             listener.complete();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*     */             UpdateException fail;
/*     */             UpdateException fail;
/* 447 */             if ((e instanceof UpdateException))
/*     */             {
/* 449 */               fail = (UpdateException)e;
/*     */             }
/*     */             else
/*     */             {
/* 453 */               fail = new UpdateException("install failed", e);
/*     */             }
/*     */             
/* 456 */             listener.reportProgress(fail.getMessage());
/*     */             
/* 458 */             listener.failed(fail);
/*     */           }
/*     */           finally
/*     */           {
/* 462 */             UpdateInstallerImpl.this.deleteInstaller();
/*     */           }
/*     */         }
/*     */       }.start();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 469 */       deleteInstaller();
/*     */       
/*     */       UpdateException fail;
/*     */       UpdateException fail;
/* 473 */       if ((e instanceof UpdateException))
/*     */       {
/* 475 */         fail = (UpdateException)e;
/*     */       }
/*     */       else
/*     */       {
/* 479 */         fail = new UpdateException("install failed", e);
/*     */       }
/*     */       
/* 482 */       listener.reportProgress(fail.getMessage());
/*     */       
/* 484 */       listener.failed(fail);
/*     */       
/* 486 */       throw fail;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 493 */     deleteInstaller();
/*     */   }
/*     */   
/*     */ 
/*     */   private void deleteInstaller()
/*     */   {
/* 499 */     this.manager.removeInstaller(this);
/*     */     
/* 501 */     if (this.install_dir.exists())
/*     */     {
/* 503 */       FileUtil.recursiveDelete(this.install_dir);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/update/UpdateInstallerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */