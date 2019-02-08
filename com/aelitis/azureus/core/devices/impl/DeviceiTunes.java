/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.Device.browseLocation;
/*     */ import com.aelitis.azureus.core.devices.DeviceManager.UnassociatedDevice;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeTargetListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeviceiTunes
/*     */   extends DeviceMediaRendererImpl
/*     */   implements DeviceMediaRenderer
/*     */ {
/*     */   private static final String UID = "a5d7869e-1ab9-6098-fef9-88476d988455";
/*  54 */   private static final Object ERRROR_KEY_ITUNES = new Object();
/*  55 */   private static final Object COPY_PENDING_KEY = new Object();
/*     */   
/*     */   private static final int INSTALL_CHECK_PERIOD = 60000;
/*     */   
/*     */   private static final int RUNNING_CHECK_PERIOD = 30000;
/*     */   
/*     */   private static final int DEVICE_CHECK_PERIOD = 10000;
/*     */   private static final int INSTALL_CHECK_TICKS = 12;
/*     */   private static final int RUNNING_CHECK_TICKS = 6;
/*     */   private static final int DEVICE_CHECK_TICKS = 2;
/*  65 */   private static final Object COPY_ERROR_KEY = new Object();
/*     */   
/*     */   private PluginInterface itunes;
/*     */   
/*     */   private volatile boolean is_installed;
/*     */   
/*     */   private volatile boolean is_running;
/*     */   private boolean copy_outstanding;
/*     */   private boolean copy_outstanding_set;
/*     */   private AEThread2 copy_thread;
/*  75 */   private AESemaphore copy_sem = new AESemaphore("Device:copy");
/*  76 */   private AsyncDispatcher async_dispatcher = new AsyncDispatcher(5000);
/*     */   
/*     */ 
/*     */   private long last_update_fail;
/*     */   
/*     */   private int consec_fails;
/*     */   
/*     */   private volatile boolean manual_copy_activated;
/*     */   
/*     */ 
/*     */   protected DeviceiTunes(DeviceManagerImpl _manager, PluginInterface _itunes)
/*     */   {
/*  88 */     super(_manager, "a5d7869e-1ab9-6098-fef9-88476d988455", "iTunes", true);
/*     */     
/*  90 */     this.itunes = _itunes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceiTunes(DeviceManagerImpl _manager, Map _map)
/*     */     throws IOException
/*     */   {
/* 100 */     super(_manager, _map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean updateFrom(DeviceImpl _other, boolean _is_alive)
/*     */   {
/* 108 */     if (!super.updateFrom(_other, _is_alive))
/*     */     {
/* 110 */       return false;
/*     */     }
/*     */     
/* 113 */     if (!(_other instanceof DeviceiTunes))
/*     */     {
/* 115 */       Debug.out("Inconsistent");
/*     */       
/* 117 */       return false;
/*     */     }
/*     */     
/* 120 */     DeviceiTunes other = (DeviceiTunes)_other;
/*     */     
/* 122 */     this.itunes = other.itunes;
/*     */     
/* 124 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void initialise()
/*     */   {
/* 130 */     super.initialise();
/*     */     
/* 132 */     if (getPersistentBooleanProperty("copy_outstanding", false))
/*     */     {
/* 134 */       setCopyOutstanding();
/*     */     }
/*     */     
/* 137 */     addListener(new TranscodeTargetListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void fileAdded(TranscodeFile file)
/*     */       {
/*     */ 
/* 144 */         if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */         {
/* 146 */           DeviceiTunes.this.setCopyOutstanding();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void fileChanged(TranscodeFile file, int type, Object data)
/*     */       {
/* 156 */         if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */         {
/* 158 */           DeviceiTunes.this.setCopyOutstanding();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void fileRemoved(TranscodeFile file)
/*     */       {
/* 166 */         DeviceiTunes.this.copy_sem.release();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getDeviceClassification()
/*     */   {
/* 174 */     return "apple.";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRendererSpecies()
/*     */   {
/* 180 */     return 3;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getAddress()
/*     */   {
/* 186 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canRemove()
/*     */   {
/* 194 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLivenessDetectable()
/*     */   {
/* 200 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getWikiURL()
/*     */   {
/*     */     try
/*     */     {
/* 208 */       return new URL(MessageText.getString("device.wiki.itunes"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 212 */       Debug.out(e);
/*     */     }
/* 214 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 221 */     super.destroy();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void updateStatus(int tick_count)
/*     */   {
/* 228 */     super.updateStatus(tick_count);
/*     */     
/* 230 */     updateStatusSupport(tick_count);
/*     */     
/* 232 */     if ((this.is_installed) && (this.is_running))
/*     */     {
/* 234 */       alive();
/*     */     }
/*     */     else
/*     */     {
/* 238 */       dead();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void updateStatusSupport(int tick_count)
/*     */   {
/* 246 */     if (this.itunes == null)
/*     */     {
/* 248 */       return;
/*     */     }
/*     */     
/* 251 */     if (!this.is_installed)
/*     */     {
/* 253 */       if (tick_count % 12 == 0)
/*     */       {
/* 255 */         updateiTunesStatus();
/*     */         
/* 257 */         return;
/*     */       }
/*     */     }
/*     */     
/* 261 */     if (!this.is_running)
/*     */     {
/* 263 */       if (tick_count % 6 == 0)
/*     */       {
/* 265 */         updateiTunesStatus();
/*     */         
/* 267 */         return;
/*     */       }
/*     */     }
/*     */     
/* 271 */     if (tick_count % 2 == 0)
/*     */     {
/* 273 */       updateiTunesStatus();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void updateiTunesStatus()
/*     */   {
/* 280 */     if (getManager().isClosing())
/*     */     {
/* 282 */       return;
/*     */     }
/*     */     
/* 285 */     IPCInterface ipc = this.itunes.getIPC();
/*     */     try
/*     */     {
/* 288 */       Map<String, Object> properties = (Map)ipc.invoke("getProperties", new Object[0]);
/*     */       
/* 290 */       this.is_installed = ((Boolean)properties.get("installed")).booleanValue();
/*     */       
/* 292 */       boolean was_running = this.is_running;
/*     */       
/* 294 */       this.is_running = ((Boolean)properties.get("running")).booleanValue();
/*     */       
/* 296 */       if ((this.is_running) && (!was_running))
/*     */       {
/* 298 */         this.copy_sem.release();
/*     */       }
/*     */       
/* 301 */       if ((!this.is_installed) && (!this.is_running))
/*     */       {
/* 303 */         this.last_update_fail = 0L;
/*     */       }
/*     */       
/* 306 */       String info = null;
/*     */       
/* 308 */       if (getCopyToDevicePending() > 0)
/*     */       {
/* 310 */         if (!this.is_installed)
/*     */         {
/* 312 */           info = MessageText.getString("device.itunes.install");
/*     */         }
/* 314 */         else if (!this.is_running)
/*     */         {
/* 316 */           if (!getAutoStartDevice())
/*     */           {
/* 318 */             info = MessageText.getString("device.itunes.start");
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 323 */       setInfo(ERRROR_KEY_ITUNES, info);
/*     */       
/* 325 */       Throwable error = (Throwable)properties.get("error");
/*     */       
/* 327 */       if (error != null)
/*     */       {
/* 329 */         throw error;
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
/* 344 */       this.last_update_fail = 0L;
/* 345 */       this.consec_fails = 0;
/*     */       
/* 347 */       setError(ERRROR_KEY_ITUNES, null);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 351 */       long now = SystemTime.getMonotonousTime();
/*     */       
/* 353 */       this.consec_fails += 1;
/*     */       
/* 355 */       if (this.last_update_fail == 0L)
/*     */       {
/* 357 */         this.last_update_fail = now;
/*     */       }
/* 359 */       else if ((now - this.last_update_fail > 60000L) && (this.consec_fails >= 3))
/*     */       {
/* 361 */         setError(ERRROR_KEY_ITUNES, MessageText.getString("device.itunes.install_problem"));
/*     */       }
/*     */       
/* 364 */       log("iTunes IPC failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canCopyToDevice()
/*     */   {
/* 371 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getAutoCopyToDevice()
/*     */   {
/* 379 */     return getPersistentBooleanProperty("auto_copy", true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAutoCopyToDevice(boolean auto)
/*     */   {
/* 386 */     setPersistentBooleanProperty("auto_copy", auto);
/*     */     
/* 388 */     setCopyOutstanding();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCopyToDevicePending()
/*     */   {
/* 394 */     synchronized (this)
/*     */     {
/* 396 */       if (!this.copy_outstanding)
/*     */       {
/* 398 */         return 0;
/*     */       }
/*     */     }
/*     */     
/* 402 */     TranscodeFileImpl[] files = getFiles();
/*     */     
/* 404 */     int result = 0;
/*     */     
/* 406 */     for (TranscodeFileImpl file : files)
/*     */     {
/* 408 */       if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */       {
/* 410 */         result++;
/*     */       }
/*     */     }
/*     */     
/* 414 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void manualCopy()
/*     */     throws DeviceManagerException
/*     */   {
/* 422 */     if (getAutoCopyToDevice())
/*     */     {
/* 424 */       throw new DeviceManagerException("Operation prohibited - auto copy enabled");
/*     */     }
/*     */     
/* 427 */     this.manual_copy_activated = true;
/*     */     
/* 429 */     setCopyOutstanding();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setCopyOutstanding()
/*     */   {
/* 435 */     synchronized (this)
/*     */     {
/* 437 */       this.copy_outstanding_set = true;
/*     */       
/* 439 */       if (this.copy_thread == null)
/*     */       {
/* 441 */         this.copy_thread = new AEThread2("Device:copier", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 447 */             DeviceiTunes.this.performCopy();
/*     */           }
/*     */           
/* 450 */         };
/* 451 */         this.copy_thread.start();
/*     */       }
/*     */       
/* 454 */       this.copy_sem.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canAutoStartDevice()
/*     */   {
/* 461 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAutoStartDevice()
/*     */   {
/* 467 */     return getPersistentBooleanProperty("auto_start", true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAutoStartDevice(boolean auto)
/*     */   {
/* 474 */     setPersistentBooleanProperty("auto_start", auto);
/*     */     
/* 476 */     if (auto)
/*     */     {
/* 478 */       this.copy_sem.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canAssociate()
/*     */   {
/* 485 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canRestrictAccess()
/*     */   {
/* 491 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void associate(DeviceManager.UnassociatedDevice assoc) {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected void performCopy()
/*     */   {
/* 503 */     synchronized (this)
/*     */     {
/* 505 */       this.copy_outstanding = true;
/*     */       
/* 507 */       this.async_dispatcher.dispatch(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 513 */           DeviceiTunes.this.setPersistentBooleanProperty("copy_outstanding", true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     for (;;)
/*     */     {
/* 520 */       if ((!this.copy_sem.reserve(60000L)) || 
/*     */       
/* 522 */         (!this.copy_sem.reserveIfAvailable()))
/*     */       {
/*     */ 
/* 525 */         if (!getAutoCopyToDevice())
/*     */         {
/* 527 */           if (this.manual_copy_activated)
/*     */           {
/* 529 */             this.manual_copy_activated = false;
/*     */           }
/*     */           else
/*     */           {
/* 533 */             TranscodeFileImpl[] files = getFiles();
/*     */             
/* 535 */             int to_copy = 0;
/*     */             
/* 537 */             for (TranscodeFileImpl file : files)
/*     */             {
/* 539 */               if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */               {
/* 541 */                 to_copy++;
/*     */               }
/*     */             }
/*     */             
/* 545 */             if (to_copy == 0)
/*     */             {
/* 547 */               setInfo(COPY_PENDING_KEY, null); continue;
/*     */             }
/*     */             
/* 550 */             String str = MessageText.getString("devices.info.copypending3", new String[] { String.valueOf(to_copy) });
/*     */             
/* 552 */             setInfo(COPY_PENDING_KEY, str);
/*     */             
/*     */ 
/* 555 */             continue;
/*     */           }
/*     */         }
/*     */         
/* 559 */         setInfo(COPY_PENDING_KEY, null);
/*     */         
/* 561 */         boolean auto_start = getAutoStartDevice();
/*     */         
/* 563 */         synchronized (this)
/*     */         {
/* 565 */           if ((this.itunes == null) || ((!this.is_running) && ((!auto_start) || (!this.is_installed))))
/*     */           {
/* 567 */             if ((!this.copy_outstanding) && (!this.copy_outstanding_set))
/*     */             {
/* 569 */               this.copy_thread = null;
/*     */               
/* 571 */               break;
/*     */             }
/*     */             
/* 574 */             continue;
/*     */           }
/*     */           
/* 577 */           this.copy_outstanding_set = false;
/*     */         }
/*     */         
/* 580 */         TranscodeFileImpl[] files = getFiles();
/*     */         
/* 582 */         List<TranscodeFileImpl> to_copy = new ArrayList();
/*     */         
/* 584 */         boolean borked_exist = false;
/*     */         
/* 586 */         for (TranscodeFileImpl file : files)
/*     */         {
/* 588 */           if ((file.isComplete()) && (!file.isCopiedToDevice()))
/*     */           {
/* 590 */             if (file.getCopyToDeviceFails() < 3L)
/*     */             {
/* 592 */               to_copy.add(file);
/*     */             }
/*     */             else
/*     */             {
/* 596 */               borked_exist = true;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 601 */         if (borked_exist)
/*     */         {
/* 603 */           setError(COPY_ERROR_KEY, MessageText.getString("device.error.copyfail2"));
/*     */         }
/*     */         
/* 606 */         synchronized (this)
/*     */         {
/* 608 */           if ((to_copy.size() == 0) && (!this.copy_outstanding_set) && (!borked_exist))
/*     */           {
/* 610 */             this.copy_outstanding = false;
/*     */             
/* 612 */             this.async_dispatcher.dispatch(new AERunnable()
/*     */             {
/*     */ 
/*     */               public void runSupport()
/*     */               {
/*     */ 
/* 618 */                 DeviceiTunes.this.setError(DeviceiTunes.COPY_ERROR_KEY, null);
/*     */                 
/* 620 */                 DeviceiTunes.this.setPersistentBooleanProperty("copy_outstanding", false);
/*     */               }
/*     */               
/* 623 */             });
/* 624 */             this.copy_thread = null;
/*     */             
/* 626 */             break;
/*     */           }
/*     */         }
/*     */         
/* 630 */         for (TranscodeFileImpl transcode_file : to_copy) {
/*     */           try
/*     */           {
/* 633 */             File file = transcode_file.getTargetFile().getFile();
/*     */             try
/*     */             {
/* 636 */               IPCInterface ipc = this.itunes.getIPC();
/*     */               
/* 638 */               if (!this.is_running)
/*     */               {
/* 640 */                 log("Auto-starting iTunes");
/*     */               }
/*     */               
/* 643 */               Object result = (Map)ipc.invoke("addFileToLibrary", new Object[] { file });
/*     */               
/* 645 */               Throwable error = (Throwable)((Map)result).get("error");
/*     */               
/* 647 */               if (error != null)
/*     */               {
/* 649 */                 throw error;
/*     */               }
/*     */               
/* 652 */               this.is_running = true;
/*     */               
/* 654 */               log("Added file '" + file + ": " + result);
/*     */               
/* 656 */               transcode_file.setCopiedToDevice(true);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 660 */               transcode_file.setCopyToDeviceFailed();
/*     */               
/* 662 */               log("Failed to copy file " + file, e);
/*     */             }
/*     */           }
/*     */           catch (TranscodeException e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isBrowsable()
/*     */   {
/* 675 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public Device.browseLocation[] getBrowseLocations()
/*     */   {
/* 681 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void getDisplayProperties(List<String[]> dp)
/*     */   {
/* 688 */     super.getDisplayProperties(dp);
/*     */     
/* 690 */     if (this.itunes == null)
/*     */     {
/* 692 */       addDP(dp, "devices.comp.missing", "<null>");
/*     */     }
/*     */     else
/*     */     {
/* 696 */       updateiTunesStatus();
/*     */       
/* 698 */       addDP(dp, "devices.installed", this.is_installed);
/*     */       
/* 700 */       addDP(dp, "MyTrackerView.status.started", this.is_running);
/*     */       
/* 702 */       addDP(dp, "devices.copy.pending", this.copy_outstanding);
/*     */       
/* 704 */       addDP(dp, "devices.auto.start", getAutoStartDevice());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatus()
/*     */   {
/* 711 */     if (this.is_running)
/*     */     {
/* 713 */       return MessageText.getString("device.itunes.status.running");
/*     */     }
/* 715 */     if (this.is_installed)
/*     */     {
/* 717 */       return MessageText.getString("device.itunes.status.notrunning");
/*     */     }
/*     */     
/*     */ 
/* 721 */     return MessageText.getString("device.itunes.status.notinstalled");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 729 */     super.generate(writer);
/*     */     try
/*     */     {
/* 732 */       writer.indent();
/*     */       
/* 734 */       writer.println("itunes=" + this.itunes + ", installed=" + this.is_installed + ", running=" + this.is_running + ", auto_start=" + getAutoStartDevice());
/* 735 */       writer.println("copy_os=" + this.copy_outstanding + ", last_fail=" + new SimpleDateFormat().format(new Date(this.last_update_fail)));
/*     */     }
/*     */     finally
/*     */     {
/* 739 */       writer.exdent();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceiTunes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */