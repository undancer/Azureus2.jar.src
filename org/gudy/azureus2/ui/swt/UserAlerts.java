/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager.UIFCallback;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import java.applet.Applet;
/*     */ import java.applet.AudioClip;
/*     */ import java.io.File;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerDiskListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.impl.DownloadManagerAdapter;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*     */ import org.gudy.azureus2.ui.swt.minibar.DownloadBar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UserAlerts
/*     */ {
/*     */   private static UserAlerts singleton;
/*     */   
/*     */   public static UserAlerts getSingleton()
/*     */   {
/*  61 */     return singleton;
/*     */   }
/*     */   
/*  64 */   private AudioClip audio_clip = null;
/*  65 */   private String audio_resource = "";
/*     */   
/*  67 */   private AEMonitor this_mon = new AEMonitor("UserAlerts");
/*     */   
/*  69 */   private boolean startup = true;
/*     */   private long last_error_speech;
/*     */   private long last_error_sound;
/*     */   
/*     */   public UserAlerts(GlobalManager global_manager)
/*     */   {
/*  75 */     singleton = this;
/*     */     
/*  77 */     final DownloadManagerAdapter download_manager_listener = new DownloadManagerAdapter()
/*     */     {
/*     */       public void downloadComplete(DownloadManager manager)
/*     */       {
/*  81 */         UserAlerts.this.activityFinished(manager, null);
/*     */       }
/*     */       
/*     */ 
/*     */       public void stateChanged(final DownloadManager manager, int state)
/*     */       {
/*  87 */         boolean lowNoise = manager.getDownloadState().getFlag(16L);
/*     */         
/*  89 */         if (lowNoise) {
/*  90 */           return;
/*     */         }
/*     */         
/*     */ 
/*  94 */         if ((state == 50) || (state == 60))
/*     */         {
/*  96 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/*  98 */               boolean complete = manager.isDownloadComplete(false);
/*     */               
/* 100 */               if (((!complete) && (COConfigurationManager.getBooleanParameter("Open Details"))) || ((complete) && (COConfigurationManager.getBooleanParameter("Open Seeding Details"))))
/*     */               {
/* 102 */                 UIFunctionsManager.getUIFunctions().getMDI().loadEntryByID("DMDetails", false, false, manager);
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 107 */               if (((!complete) && (COConfigurationManager.getBooleanParameter("Open Bar Incomplete"))) || ((complete) && (COConfigurationManager.getBooleanParameter("Open Bar Complete"))))
/*     */               {
/*     */ 
/* 110 */                 DownloadBar.open(manager, Utils.findAnyShell());
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/* 116 */         boolean error_reported = manager.getDownloadState().getFlag(2048L);
/*     */         
/* 118 */         if (state == 100)
/*     */         {
/* 120 */           if (!error_reported)
/*     */           {
/* 122 */             manager.getDownloadState().setFlag(2048L, true);
/*     */             
/* 124 */             UserAlerts.this.reportError(manager);
/*     */           }
/* 126 */         } else if ((state == 50) || (state == 60))
/*     */         {
/* 128 */           if (error_reported)
/*     */           {
/* 130 */             manager.getDownloadState().setFlag(2048L, false);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 135 */     };
/* 136 */     final DiskManagerListener disk_listener = new DiskManagerListener()
/*     */     {
/*     */       public void stateChanged(int oldState, int newState) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void filePriorityChanged(DiskManagerFileInfo file) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void pieceDoneChanged(DiskManagerPiece piece) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode)
/*     */       {
/* 164 */         DownloadManager dm = file.getDownloadManager();
/*     */         
/* 166 */         if (dm != null)
/*     */         {
/* 168 */           if ((old_mode == 2) && (new_mode == 1) && (file.getDownloaded() == file.getLength()))
/*     */           {
/*     */ 
/*     */ 
/* 172 */             UserAlerts.this.activityFinished(dm, file);
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 184 */     };
/* 185 */     final DownloadManagerDiskListener dm_disk_listener = new DownloadManagerDiskListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void diskManagerAdded(DiskManager dm)
/*     */       {
/*     */ 
/* 192 */         dm.addListener(disk_listener);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void diskManagerRemoved(DiskManager dm)
/*     */       {
/* 199 */         dm.removeListener(disk_listener);
/*     */       }
/*     */       
/*     */ 
/* 203 */     };
/* 204 */     global_manager.addListener(new GlobalManagerAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void downloadManagerAdded(DownloadManager manager)
/*     */       {
/*     */ 
/*     */ 
/* 213 */         if ((!UserAlerts.this.startup) && (manager.isPersistent()))
/*     */         {
/* 215 */           boolean bPopup = COConfigurationManager.getBooleanParameter("Popup Download Added");
/*     */           
/* 217 */           if (bPopup)
/*     */           {
/* 219 */             if (!manager.getDownloadState().getFlag(16L))
/*     */             {
/* 221 */               String popup_text = MessageText.getString("popup.download.added", new String[] { manager.getDisplayName() });
/*     */               
/*     */ 
/* 224 */               UserAlerts.this.forceNotify(0, null, popup_text, null, new Object[] { manager }, -1);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 233 */         manager.addListener(download_manager_listener);
/*     */         
/* 235 */         manager.addDiskListener(dm_disk_listener);
/*     */       }
/*     */       
/*     */ 
/*     */       public void downloadManagerRemoved(DownloadManager manager)
/*     */       {
/* 241 */         manager.removeListener(download_manager_listener);
/*     */         
/* 243 */         manager.removeDiskListener(dm_disk_listener);
/*     */       }
/*     */       
/*     */ 
/*     */       public void destroyed()
/*     */       {
/* 249 */         UserAlerts.this.tidyUp();
/*     */       }
/* 251 */     });
/* 252 */     this.startup = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void activityFinished(DownloadManager manager, DiskManagerFileInfo dm_file)
/*     */   {
/* 260 */     DownloadManagerState dm_state = manager.getDownloadState();
/*     */     
/* 262 */     if (dm_state.getFlag(16L))
/*     */     {
/* 264 */       return;
/*     */     }
/*     */     
/* 267 */     boolean download = dm_file == null;
/*     */     
/*     */     String item_name;
/*     */     Object relatedObject;
/*     */     String item_name;
/* 272 */     if (download)
/*     */     {
/* 274 */       Object relatedObject = manager;
/* 275 */       item_name = manager.getDisplayName();
/*     */     }
/*     */     else
/*     */     {
/* 279 */       relatedObject = dm_file.getDiskManager();
/* 280 */       item_name = dm_file.getFile(true).getName();
/*     */     }
/*     */     
/*     */     String popup_def_text;
/*     */     
/*     */     String sound_enabler;
/*     */     
/*     */     String sound_file;
/*     */     String speech_enabler;
/*     */     String speech_text;
/*     */     String popup_enabler;
/*     */     String popup_def_text;
/* 292 */     if (download) {
/* 293 */       String sound_enabler = "Play Download Finished";
/* 294 */       String sound_file = "Play Download Finished File";
/*     */       
/* 296 */       String speech_enabler = "Play Download Finished Announcement";
/* 297 */       String speech_text = "Play Download Finished Announcement Text";
/*     */       
/* 299 */       String popup_enabler = "Popup Download Finished";
/* 300 */       popup_def_text = "popup.download.finished";
/*     */     }
/*     */     else {
/* 303 */       sound_enabler = "Play File Finished";
/* 304 */       sound_file = "Play File Finished File";
/*     */       
/* 306 */       speech_enabler = "Play File Finished Announcement";
/* 307 */       speech_text = "Play File Finished Announcement Text";
/*     */       
/* 309 */       popup_enabler = "Popup File Finished";
/* 310 */       popup_def_text = "popup.file.finished";
/*     */     }
/*     */     
/* 313 */     Map dl_file_alerts = dm_state.getMapAttribute("df_alerts");
/* 314 */     String dlf_prefix = String.valueOf(dm_file.getIndex()) + ".";
/*     */     
/* 316 */     boolean do_popup = (COConfigurationManager.getBooleanParameter(popup_enabler)) || (isDLFEnabled(dl_file_alerts, dlf_prefix, popup_enabler));
/* 317 */     boolean do_speech = (Constants.isOSX) && ((COConfigurationManager.getBooleanParameter(speech_enabler)) || (isDLFEnabled(dl_file_alerts, dlf_prefix, speech_enabler)));
/* 318 */     boolean do_sound = (COConfigurationManager.getBooleanParameter(sound_enabler, false)) || (isDLFEnabled(dl_file_alerts, dlf_prefix, sound_enabler));
/*     */     
/* 320 */     doStuff(relatedObject, item_name, do_popup, popup_def_text, false, do_speech, speech_text, do_sound, sound_file);
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
/*     */   private void reportError(DownloadManager manager)
/*     */   {
/* 334 */     Object relatedObject = manager;
/* 335 */     String item_name = manager.getDisplayName();
/*     */     
/*     */ 
/* 338 */     String sound_enabler = "Play Download Error";
/* 339 */     String sound_file = "Play Download Error File";
/*     */     
/* 341 */     String speech_enabler = "Play Download Error Announcement";
/* 342 */     String speech_text = "Play Download Error Announcement Text";
/*     */     
/* 344 */     String popup_enabler = "Popup Download Error";
/* 345 */     String popup_def_text = "popup.download.error";
/*     */     
/* 347 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 349 */     boolean do_popup = COConfigurationManager.getBooleanParameter("Popup Download Error");
/*     */     
/* 351 */     boolean do_speech = (Constants.isOSX) && (COConfigurationManager.getBooleanParameter("Play Download Error Announcement")) && ((this.last_error_speech == 0L) || (now - this.last_error_speech > 5000L));
/*     */     
/*     */ 
/*     */ 
/* 355 */     boolean do_sound = (COConfigurationManager.getBooleanParameter("Play Download Error", false)) && ((this.last_error_sound == 0L) || (now - this.last_error_sound > 5000L));
/*     */     
/*     */ 
/* 358 */     if (do_speech) {
/* 359 */       this.last_error_speech = now;
/*     */     }
/* 361 */     if (do_sound) {
/* 362 */       this.last_error_sound = now;
/*     */     }
/*     */     
/* 365 */     doStuff(relatedObject, item_name, do_popup, "popup.download.error", true, do_speech, "Play Download Error Announcement Text", do_sound, "Play Download Error File");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void notificationAdded()
/*     */   {
/* 375 */     boolean do_popup = false;
/*     */     
/* 377 */     boolean do_speech = (Constants.isOSX) && (COConfigurationManager.getBooleanParameter("Play Notification Added Announcement"));
/*     */     
/*     */ 
/* 380 */     boolean do_sound = COConfigurationManager.getBooleanParameter("Play Notification Added", false);
/*     */     
/* 382 */     doStuff(null, null, do_popup, null, false, do_speech, "Play Notification Added Announcement Text", do_sound, "Play Notification Added File");
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
/*     */   private void doStuff(Object relatedObject, String item_name, boolean do_popup, String popup_def_text, boolean popup_is_error, boolean do_speech, final String speech_text, boolean do_sound, String sound_file)
/*     */   {
/* 401 */     String default_sound = "org/gudy/azureus2/ui/icons/downloadFinished.wav";
/*     */     try
/*     */     {
/* 404 */       this.this_mon.enter();
/*     */       
/* 406 */       if (do_popup) {
/* 407 */         String popup_text = MessageText.getString(popup_def_text, new String[] { item_name });
/*     */         
/* 409 */         forceNotify(popup_is_error ? 2 : 0, null, popup_text, null, new Object[] { relatedObject }, -1);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 416 */       if (do_speech) {
/* 417 */         new AEThread2("SaySound") {
/*     */           public void run() {
/*     */             try {
/* 420 */               Runtime.getRuntime().exec(new String[] { "say", COConfigurationManager.getStringParameter(speech_text) });
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 425 */               Thread.sleep(2500L);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }.start();
/*     */       }
/*     */       
/* 432 */       if (do_sound)
/*     */       {
/* 434 */         String file = COConfigurationManager.getStringParameter(sound_file);
/*     */         
/* 436 */         file = file.trim();
/*     */         
/*     */ 
/*     */ 
/* 440 */         if (file.startsWith("<"))
/*     */         {
/* 442 */           file = "";
/*     */         }
/*     */         
/* 445 */         if ((this.audio_clip == null) || (!file.equals(this.audio_resource)))
/*     */         {
/* 447 */           this.audio_clip = null;
/*     */           
/*     */ 
/*     */ 
/* 451 */           if (file.length() != 0)
/*     */           {
/* 453 */             File f = new File(file);
/*     */             
/*     */             try
/*     */             {
/* 457 */               if (f.exists())
/*     */               {
/* 459 */                 URL file_url = f.toURI().toURL();
/*     */                 
/* 461 */                 this.audio_clip = Applet.newAudioClip(file_url);
/*     */               }
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 466 */               Debug.printStackTrace(e);
/*     */             }
/*     */             finally
/*     */             {
/* 470 */               if (this.audio_clip == null) {
/* 471 */                 Logger.log(new LogAlert(relatedObject, false, 3, "Failed to load audio file '" + file + "'"));
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 480 */           if (this.audio_clip == null)
/*     */           {
/* 482 */             this.audio_clip = Applet.newAudioClip(UserAlerts.class.getClassLoader().getResource("org/gudy/azureus2/ui/icons/downloadFinished.wav"));
/*     */           }
/*     */           
/*     */ 
/* 486 */           this.audio_resource = file;
/*     */         }
/*     */         
/* 489 */         if (this.audio_clip != null)
/*     */         {
/* 491 */           new AEThread2("DownloadSound")
/*     */           {
/*     */             public void run()
/*     */             {
/*     */               try
/*     */               {
/* 497 */                 UserAlerts.this.audio_clip.play();
/*     */                 
/* 499 */                 Thread.sleep(2500L);
/*     */ 
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }.start();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 510 */       Debug.printStackTrace(e);
/*     */     }
/*     */     finally
/*     */     {
/* 514 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean isDLFEnabled(Map map, String prefix, String key)
/*     */   {
/* 525 */     if (map == null)
/*     */     {
/* 527 */       return false;
/*     */     }
/*     */     
/* 530 */     key = prefix + key;
/*     */     
/* 532 */     return map.containsKey(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void forceNotify(final int iconID, final String title, final String text, final String details, final Object[] relatedObjects, final int timeoutSecs)
/*     */   {
/* 540 */     UIFunctionsManager.execWithUIFunctions(new UIFunctionsManager.UIFCallback()
/*     */     {
/*     */ 
/*     */       public void run(UIFunctions uif)
/*     */       {
/* 545 */         uif.forceNotify(iconID, title, text, details, relatedObjects, timeoutSecs);
/*     */       }
/*     */     });
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
/*     */   protected void tidyUp()
/*     */   {
/*     */     try
/*     */     {
/* 563 */       ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
/*     */       
/* 565 */       Thread[] threadList = new Thread[threadGroup.activeCount()];
/*     */       
/* 567 */       threadGroup.enumerate(threadList);
/*     */       
/* 569 */       for (int i = 0; i < threadList.length; i++)
/*     */       {
/* 571 */         if ((threadList[i] != null) && ("Java Sound event dispatcher".equals(threadList[i].getName())))
/*     */         {
/* 573 */           threadList[i].interrupt();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 578 */       Debug.printStackTrace(e);
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
/*     */ 
/*     */ 
/*     */   public static void requestUserAttention(int type, Object data)
/*     */   {
/* 593 */     PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/* 594 */     if (pm.hasCapability(PlatformManagerCapabilities.RequestUserAttention)) {
/*     */       try {
/* 596 */         pm.requestUserAttention(type, data);
/*     */       } catch (PlatformManagerException e) {
/* 598 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/UserAlerts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */