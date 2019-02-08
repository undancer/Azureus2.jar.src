/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeTargetListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeviceMediaRendererManual
/*     */   extends DeviceMediaRendererImpl
/*     */ {
/*  49 */   private static final Object COPY_ERROR_KEY = new Object();
/*  50 */   private static final Object COPY_PENDING_KEY = new Object();
/*     */   
/*  52 */   private boolean can_copy_to_folder = true;
/*     */   private boolean copy_outstanding;
/*     */   private boolean copy_outstanding_set;
/*     */   private AEThread2 copy_thread;
/*  56 */   private AESemaphore copy_sem = new AESemaphore("Device:copy");
/*  57 */   private AsyncDispatcher async_dispatcher = new AsyncDispatcher(5000);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceMediaRendererManual(DeviceManagerImpl _manager, String _uid, String _classification, boolean _manual, String _name)
/*     */   {
/*  67 */     super(_manager, _uid, _classification, _manual, _name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceMediaRendererManual(DeviceManagerImpl _manager, Map _map)
/*     */     throws IOException
/*     */   {
/*  77 */     super(_manager, _map);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void initialise()
/*     */   {
/*  83 */     super.initialise();
/*     */     
/*  85 */     if (getPersistentBooleanProperty("copy_outstanding", false))
/*     */     {
/*  87 */       setCopyOutstanding();
/*     */     }
/*     */     
/*  90 */     addListener(new TranscodeTargetListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void fileAdded(TranscodeFile file)
/*     */       {
/*     */ 
/*  97 */         updateStatus(file);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void fileChanged(TranscodeFile file, int type, Object data)
/*     */       {
/* 106 */         updateStatus(file);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void fileRemoved(TranscodeFile file)
/*     */       {
/* 115 */         DeviceMediaRendererManual.this.setCopyOutstanding();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       private void updateStatus(TranscodeFile file)
/*     */       {
/* 122 */         if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */         {
/* 124 */           DeviceMediaRendererManual.this.setCopyOutstanding();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canAssociate()
/*     */   {
/* 133 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canRestrictAccess()
/*     */   {
/* 139 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canFilterFilesView()
/*     */   {
/* 145 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isBrowsable()
/*     */   {
/* 151 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canCopyToFolder()
/*     */   {
/* 157 */     return this.can_copy_to_folder;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCanCopyToFolder(boolean can)
/*     */   {
/* 164 */     this.can_copy_to_folder = can;
/*     */     
/* 166 */     if (!can)
/*     */     {
/* 168 */       setPersistentBooleanProperty("copy_outstanding", false);
/*     */       
/* 170 */       synchronized (this) {
/* 171 */         this.copy_outstanding = false;
/* 172 */         this.copy_outstanding_set = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public File getCopyToFolder()
/*     */   {
/* 180 */     String str = getPersistentStringProperty("copy_to_folder", null);
/*     */     
/* 182 */     if (str == null)
/*     */     {
/* 184 */       return null;
/*     */     }
/*     */     
/* 187 */     return new File(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCopyToFolder(File file)
/*     */   {
/* 194 */     setPersistentStringProperty("copy_to_folder", file == null ? null : file.getAbsolutePath());
/*     */     
/* 196 */     if (getAutoCopyToFolder())
/*     */     {
/* 198 */       setCopyOutstanding();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLivenessDetectable()
/*     */   {
/* 205 */     return getPersistentBooleanProperty("live_det", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLivenessDetectable(boolean b)
/*     */   {
/* 212 */     setPersistentBooleanProperty("live_det", true);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCopyToFolderPending()
/*     */   {
/* 218 */     if (!this.can_copy_to_folder)
/*     */     {
/* 220 */       return 0;
/*     */     }
/*     */     
/* 223 */     synchronized (this)
/*     */     {
/* 225 */       if (!this.copy_outstanding)
/*     */       {
/* 227 */         return 0;
/*     */       }
/*     */     }
/*     */     
/* 231 */     TranscodeFileImpl[] files = getFiles();
/*     */     
/* 233 */     int result = 0;
/*     */     
/* 235 */     for (TranscodeFileImpl file : files)
/*     */     {
/* 237 */       if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */       {
/* 239 */         result++;
/*     */       }
/*     */     }
/*     */     
/* 243 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAutoCopyToFolder()
/*     */   {
/* 249 */     return getPersistentBooleanProperty("auto_copy", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAutoCopyToFolder(boolean auto)
/*     */   {
/* 256 */     setPersistentBooleanProperty("auto_copy", auto);
/*     */     
/* 258 */     setCopyOutstanding();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void manualCopy()
/*     */     throws DeviceManagerException
/*     */   {
/* 266 */     if (getAutoCopyToFolder())
/*     */     {
/* 268 */       throw new DeviceManagerException("Operation prohibited - auto copy enabled");
/*     */     }
/*     */     
/* 271 */     doCopy();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setCopyOutstanding()
/*     */   {
/* 277 */     if (!this.can_copy_to_folder)
/*     */     {
/* 279 */       return;
/*     */     }
/*     */     
/* 282 */     synchronized (this)
/*     */     {
/* 284 */       this.copy_outstanding_set = true;
/*     */       
/* 286 */       if (this.copy_thread == null)
/*     */       {
/* 288 */         this.copy_thread = new AEThread2("Device:copier", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 294 */             DeviceMediaRendererManual.this.performCopy();
/*     */           }
/*     */           
/* 297 */         };
/* 298 */         this.copy_thread.start();
/*     */       }
/*     */       
/* 301 */       this.copy_sem.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAudioCompatible(TranscodeFile transcode_file)
/*     */   {
/* 309 */     if (getDeviceClassification().equals("sony.PSP")) {
/*     */       try
/*     */       {
/* 312 */         File file = transcode_file.getSourceFile().getFile();
/*     */         
/* 314 */         if (file.exists())
/*     */         {
/* 316 */           String name = file.getName().toLowerCase();
/*     */           
/* 318 */           if ((name.endsWith(".mp3")) || (name.endsWith(".wma")))
/*     */           {
/* 320 */             ((TranscodeFileImpl)transcode_file).setCopyToFolderOverride(".." + File.separator + "MUSIC");
/*     */             
/* 322 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 327 */         log("audio compatible check failed", e);
/*     */       }
/*     */     }
/*     */     
/* 331 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void performCopy()
/*     */   {
/* 337 */     if (!this.can_copy_to_folder)
/*     */     {
/* 339 */       return;
/*     */     }
/*     */     
/* 342 */     synchronized (this)
/*     */     {
/* 344 */       this.copy_outstanding = true;
/*     */       
/* 346 */       this.async_dispatcher.dispatch(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 352 */           DeviceMediaRendererManual.this.setPersistentBooleanProperty("copy_outstanding", true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     for (;;)
/*     */     {
/* 359 */       if ((!this.copy_sem.reserve(10000L)) || 
/*     */       
/* 361 */         (!this.copy_sem.reserveIfAvailable()))
/*     */       {
/*     */ 
/* 364 */         boolean auto_copy = getAutoCopyToFolder();
/*     */         
/* 366 */         boolean nothing_to_do = false;
/*     */         
/* 368 */         synchronized (this)
/*     */         {
/* 370 */           if (!auto_copy)
/*     */           {
/* 372 */             this.copy_thread = null;
/*     */             
/* 374 */             nothing_to_do = true;
/*     */           }
/*     */           else
/*     */           {
/* 378 */             this.copy_outstanding_set = false;
/*     */           }
/*     */         }
/*     */         
/* 382 */         if (nothing_to_do)
/*     */         {
/* 384 */           setError(COPY_ERROR_KEY, null);
/*     */           
/* 386 */           int pending = getCopyToFolderPending();
/*     */           
/* 388 */           if (pending == 0)
/*     */           {
/* 390 */             setInfo(COPY_PENDING_KEY, null);
/*     */           }
/*     */           else
/*     */           {
/* 394 */             String str = MessageText.getString("devices.info.copypending", new String[] { String.valueOf(pending) });
/*     */             
/* 396 */             setInfo(COPY_PENDING_KEY, str);
/*     */           }
/* 398 */           return;
/*     */         }
/*     */         
/*     */ 
/* 402 */         if (doCopy()) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean doCopy()
/*     */   {
/* 412 */     if (!this.can_copy_to_folder)
/*     */     {
/* 414 */       return true;
/*     */     }
/*     */     
/* 417 */     setInfo(COPY_PENDING_KEY, null);
/*     */     
/*     */ 
/* 420 */     File copy_to = getCopyToFolder();
/*     */     
/* 422 */     List<TranscodeFileImpl> to_copy = new ArrayList();
/*     */     
/* 424 */     boolean borked = false;
/*     */     
/* 426 */     TranscodeFileImpl[] files = getFiles();
/*     */     
/* 428 */     int pending = 0;
/*     */     
/* 430 */     for (TranscodeFileImpl file : files)
/*     */     {
/* 432 */       if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */       {
/* 434 */         pending++;
/*     */         
/* 436 */         if (file.getCopyToDeviceFails() < 3L)
/*     */         {
/* 438 */           to_copy.add(file);
/*     */         }
/*     */         else
/*     */         {
/* 442 */           String info = (String)file.getTransientProperty(COPY_ERROR_KEY);
/*     */           
/* 444 */           setError(COPY_ERROR_KEY, MessageText.getString("device.error.copyfail") + (info == null ? "" : new StringBuilder().append(" - ").append(info).toString()));
/*     */           
/* 446 */           borked = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 451 */     boolean try_copy = false;
/*     */     
/* 453 */     if (to_copy.size() > 0)
/*     */     {
/*     */ 
/*     */ 
/* 457 */       if ((isLivenessDetectable()) && (!isAlive()) && ((copy_to == null) || (!copy_to.exists())))
/*     */       {
/* 459 */         String str = MessageText.getString("devices.info.copypending2", new String[] { String.valueOf(pending) });
/*     */         
/* 461 */         setInfo(COPY_PENDING_KEY, str);
/*     */         
/* 463 */         borked = true;
/*     */       }
/*     */       else {
/* 466 */         setInfo(COPY_PENDING_KEY, null);
/*     */         
/* 468 */         boolean sub_borked = false;
/*     */         
/* 470 */         if (copy_to == null)
/*     */         {
/* 472 */           setError(COPY_ERROR_KEY, MessageText.getString("device.error.copytonotset"));
/*     */           
/* 474 */           sub_borked = true;
/*     */         }
/* 476 */         else if (!copy_to.exists())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 482 */           File parent = copy_to.getParentFile();
/*     */           
/* 484 */           if ((parent != null) && (parent.canWrite()))
/*     */           {
/* 486 */             copy_to.mkdir();
/*     */           }
/*     */           
/* 489 */           if (!copy_to.exists())
/*     */           {
/* 491 */             setError(COPY_ERROR_KEY, MessageText.getString("device.error.mountrequired", new String[] { copy_to.getAbsolutePath() }));
/*     */             
/* 493 */             sub_borked = true;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 500 */         if (!sub_borked)
/*     */         {
/* 502 */           if (!copy_to.canWrite())
/*     */           {
/* 504 */             setError(COPY_ERROR_KEY, MessageText.getString("device.error.copytonowrite", new String[] { copy_to.getAbsolutePath() }));
/*     */             
/* 506 */             sub_borked = true;
/*     */           }
/*     */           else
/*     */           {
/* 510 */             try_copy = true;
/*     */             
/* 512 */             setError(COPY_ERROR_KEY, null);
/*     */           }
/*     */         }
/*     */         
/* 516 */         borked |= sub_borked;
/*     */       }
/*     */     }
/*     */     else {
/* 520 */       setInfo(COPY_PENDING_KEY, null);
/*     */     }
/*     */     
/* 523 */     synchronized (this)
/*     */     {
/*     */ 
/*     */ 
/* 527 */       if ((to_copy.size() == 0) && (!this.copy_outstanding_set) && (!borked))
/*     */       {
/* 529 */         this.copy_outstanding = false;
/*     */         
/* 531 */         this.async_dispatcher.dispatch(new AERunnable()
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/* 537 */             DeviceMediaRendererManual.this.setError(DeviceMediaRendererManual.COPY_ERROR_KEY, null);
/*     */             
/* 539 */             DeviceMediaRendererManual.this.setPersistentBooleanProperty("copy_outstanding", false);
/*     */           }
/*     */           
/* 542 */         });
/* 543 */         this.copy_thread = null;
/*     */         
/* 545 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 549 */     if (try_copy) {
/*     */       try
/*     */       {
/* 552 */         setBusy(true);
/*     */         
/* 554 */         for (TranscodeFileImpl transcode_file : to_copy) {
/*     */           try
/*     */           {
/* 557 */             transcode_file.setCopyingToDevice(true);
/*     */             
/* 559 */             File file = transcode_file.getTargetFile().getFile();
/*     */             
/* 561 */             File target = new File(copy_to, file.getName());
/*     */             
/* 563 */             String override_str = transcode_file.getCopyToFolderOverride();
/*     */             
/* 565 */             if (override_str != null)
/*     */             {
/* 567 */               File override_dir = new File(copy_to, override_str);
/*     */               
/* 569 */               if (override_dir.exists())
/*     */               {
/* 571 */                 target = new File(override_dir, file.getName());
/*     */               }
/*     */             }
/*     */             try
/*     */             {
/* 576 */               FileUtil.copyFileWithException(file, target);
/*     */               
/* 578 */               log("Copied file '" + file + ": to " + copy_to);
/*     */               
/* 580 */               transcode_file.setCopiedToDevice(true);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 584 */               copy_to.delete();
/*     */               
/* 586 */               transcode_file.setCopyToDeviceFailed();
/*     */               
/* 588 */               transcode_file.setTransientProperty(COPY_ERROR_KEY, Debug.getNestedExceptionMessage(e));
/*     */               
/* 590 */               log("Failed to copy file " + file, e);
/*     */             }
/*     */             
/*     */           }
/*     */           catch (TranscodeException e) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 599 */         setBusy(false);
/*     */       }
/*     */     }
/*     */     
/* 603 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isExportable()
/*     */   {
/* 609 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void getDisplayProperties(List<String[]> dp)
/*     */   {
/* 616 */     super.getDisplayProperties(dp);
/*     */     
/* 618 */     addDP(dp, "devices.copy.pending", this.copy_outstanding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 625 */     super.generate(writer);
/*     */     try
/*     */     {
/* 628 */       writer.indent();
/*     */       
/* 630 */       writer.println("auto_copy=" + getAutoCopyToFolder() + ", copy_to=" + getCopyToFolder() + ", copy_os=" + this.copy_outstanding);
/*     */     }
/*     */     finally
/*     */     {
/* 634 */       writer.exdent();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getStatus()
/*     */   {
/* 640 */     String s = super.getStatus();
/*     */     
/* 642 */     if ((COConfigurationManager.getIntParameter("User Mode") > 0) && (getCopyToFolder() != null)) {
/* 643 */       s = s + " (" + getCopyToFolder().getPath() + ")";
/*     */     }
/*     */     
/* 646 */     return s;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceMediaRendererManual.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */