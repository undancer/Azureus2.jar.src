/*     */ package com.aelitis.azureus.core.helpers;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TorrentFolderWatcher
/*     */ {
/*  49 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */   private static final String PARAMID_FOLDER = "Watch Torrent Folder";
/*     */   
/*     */   private volatile GlobalManager _global_manager;
/*     */   
/*  55 */   private volatile boolean running = false;
/*     */   
/*  57 */   private final ArrayList<TOTorrent> to_delete = new ArrayList();
/*     */   
/*  59 */   protected final AEMonitor this_mon = new AEMonitor("TorrentFolderWatcher");
/*     */   
/*  61 */   private final FilenameFilter filename_filter = new FilenameFilter() {
/*     */     public boolean accept(File dir, String name) {
/*  63 */       String lc_name = name.toLowerCase();
/*     */       
/*  65 */       return (lc_name.endsWith(".torrent")) || (lc_name.endsWith(".tor"));
/*     */     }
/*     */   };
/*     */   
/*  69 */   private final ParameterListener param_listener = new ParameterListener() {
/*     */     public void parameterChanged(String parameterName) {
/*  71 */       if (COConfigurationManager.getBooleanParameter("Watch Torrent Folder")) {
/*  72 */         if (!TorrentFolderWatcher.this.running) {
/*  73 */           TorrentFolderWatcher.this.running = true;
/*  74 */           if (!TorrentFolderWatcher.this.watch_thread.isAlive())
/*     */           {
/*  76 */             TorrentFolderWatcher.this.watch_thread.setDaemon(true);
/*  77 */             TorrentFolderWatcher.this.watch_thread.setPriority(1);
/*  78 */             TorrentFolderWatcher.this.watch_thread.start();
/*     */           }
/*     */         }
/*     */       } else {
/*  82 */         TorrentFolderWatcher.this.running = false;
/*     */       }
/*     */     }
/*     */   };
/*     */   
/*  87 */   private final Thread watch_thread = new AEThread("FolderWatcher")
/*     */   {
/*     */     private long last_run;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     final AESemaphore wait_sem;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void runSupport()
/*     */     {
/*     */       for (;;)
/*     */       {
/* 118 */         long now = SystemTime.getMonotonousTime();
/*     */         
/* 120 */         int sleep_secs = COConfigurationManager.getIntParameter("Watch Torrent Folder Interval Secs");
/*     */         
/* 122 */         if (sleep_secs < 1)
/*     */         {
/* 124 */           sleep_secs = 1;
/*     */         }
/*     */         
/* 127 */         int sleep_ms = sleep_secs * 1000;
/*     */         
/* 129 */         long remaining = this.last_run + sleep_ms - now;
/*     */         
/* 131 */         if ((remaining < 250L) || (this.last_run == 0L))
/*     */         {
/* 133 */           this.last_run = now;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 138 */           if (remaining < 250L)
/*     */           {
/* 140 */             remaining = 250L;
/*     */           }
/*     */           
/* 143 */           this.wait_sem.reserve(remaining);
/* 144 */           continue;
/*     */         }
/*     */         try {
/* 147 */           if (TorrentFolderWatcher.this.running)
/*     */           {
/* 149 */             TorrentFolderWatcher.this.importAddedFiles();
/*     */           }
/*     */           else
/*     */           {
/* 153 */             this.wait_sem.reserve(60000L);
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 158 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentFolderWatcher(GlobalManager global_manager)
/*     */   {
/* 171 */     this._global_manager = global_manager;
/*     */   }
/*     */   
/*     */ 
/*     */   public void start()
/*     */   {
/* 177 */     if (COConfigurationManager.getBooleanParameter("Watch Torrent Folder")) {
/* 178 */       this.running = true;
/* 179 */       this.watch_thread.setDaemon(true);
/* 180 */       this.watch_thread.setPriority(1);
/* 181 */       this.watch_thread.start();
/*     */     }
/*     */     
/* 184 */     COConfigurationManager.addParameterListener("Watch Torrent Folder", this.param_listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 191 */     this.running = false;
/* 192 */     this._global_manager = null;
/* 193 */     COConfigurationManager.removeParameterListener("Watch Torrent Folder", this.param_listener);
/*     */   }
/*     */   
/*     */ 
/*     */   private void importAddedFiles()
/*     */   {
/* 199 */     AzureusCore core = AzureusCoreFactory.getSingleton();
/*     */     try
/*     */     {
/* 202 */       this.this_mon.enter();
/*     */       
/* 204 */       if (!this.running) {
/*     */         return;
/*     */       }
/*     */       
/* 208 */       GlobalManager global_manager = this._global_manager;
/*     */       
/* 210 */       if ((global_manager == null) || (!core.isStarted())) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 215 */       org.gudy.azureus2.plugins.download.DownloadManager plugin_dm = core.getPluginManager().getDefaultPluginInterface().getDownloadManager();
/*     */       
/* 217 */       boolean save_torrents_default = COConfigurationManager.getBooleanParameter("Save Torrent Files");
/*     */       
/* 219 */       String torrent_save_path = COConfigurationManager.getStringParameter("General_sDefaultTorrent_Directory");
/*     */       
/*     */ 
/* 222 */       int start_state = COConfigurationManager.getBooleanParameter("Start Watched Torrents Stopped") ? 70 : 75;
/*     */       
/*     */ 
/*     */ 
/* 226 */       int num_folders = COConfigurationManager.getIntParameter("Watch Torrent Folder Path Count", 1);
/*     */       
/* 228 */       List<File> folders = new ArrayList();
/* 229 */       List<String> tags = new ArrayList();
/*     */       
/* 231 */       for (int i = 0; i < num_folders; i++) {
/* 232 */         String folder_path = COConfigurationManager.getStringParameter("Watch Torrent Folder Path" + (i == 0 ? "" : new StringBuilder().append(" ").append(i).toString()));
/*     */         
/*     */ 
/* 235 */         File folder = null;
/*     */         
/* 237 */         if ((folder_path != null) && (folder_path.length() > 0)) {
/* 238 */           folder = new File(folder_path);
/* 239 */           if (!folder.isDirectory()) {
/* 240 */             if (!folder.exists()) {
/* 241 */               FileUtil.mkdirs(folder);
/*     */             }
/* 243 */             if (!folder.isDirectory()) {
/* 244 */               if (Logger.isEnabled()) {
/* 245 */                 Logger.log(new LogEvent(LOGID, 3, "[Watch Torrent Folder Path] does not exist or is not a dir"));
/*     */               }
/*     */               
/* 248 */               folder = null;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 253 */         if (folder != null)
/*     */         {
/* 255 */           folders.add(folder);
/*     */           
/* 257 */           String tag = COConfigurationManager.getStringParameter("Watch Torrent Folder Tag" + (i == 0 ? "" : new StringBuilder().append(" ").append(i).toString()), null);
/*     */           
/*     */ 
/* 260 */           if ((tag != null) && (tag.trim().length() == 0))
/*     */           {
/* 262 */             tag = null;
/*     */           }
/*     */           
/* 265 */           tags.add(tag);
/*     */         }
/*     */       }
/*     */       
/* 269 */       if (folders.isEmpty()) {
/* 270 */         if (Logger.isEnabled()) {
/* 271 */           Logger.log(new LogEvent(LOGID, 3, "[Watch Torrent Folder Path] not configured"));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 276 */         String data_save_path = COConfigurationManager.getStringParameter("Default save path");
/*     */         
/* 278 */         File f = null;
/* 279 */         if ((data_save_path != null) && (data_save_path.length() > 0)) {
/* 280 */           f = new File(data_save_path);
/*     */           
/*     */ 
/* 283 */           if (!f.isDirectory()) {
/* 284 */             if (!f.exists()) { FileUtil.mkdirs(f);
/*     */             }
/*     */             
/* 287 */             if (!f.isDirectory()) {
/* 288 */               if (Logger.isEnabled()) {
/* 289 */                 Logger.log(new LogEvent(LOGID, 3, "[Default save path] does not exist or is not a dir"));
/*     */               }
/*     */               
/* 292 */               Logger.log(new LogAlert(false, 3, "[Default save path] does not exist or is not a dir")); return;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 300 */         if (f == null) {
/* 301 */           if (Logger.isEnabled()) {
/* 302 */             Logger.log(new LogEvent(LOGID, 3, "[Default save path] needs to be set for auto-.torrent-import to work"));
/*     */           }
/*     */           
/* 305 */           Logger.log(new LogAlert(false, 3, "[Default save path] needs to be set for auto-.torrent-import to work"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 312 */         for (int i = 0; i < this.to_delete.size(); i++)
/*     */         {
/* 314 */           TOTorrent torrent = (TOTorrent)this.to_delete.get(i);
/*     */           try
/*     */           {
/* 317 */             TorrentUtils.delete(torrent);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 321 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */         
/* 325 */         this.to_delete.clear();
/*     */         
/* 327 */         for (int folder_index = 0; folder_index < folders.size(); folder_index++)
/*     */         {
/* 329 */           File folder = (File)folders.get(folder_index);
/*     */           
/* 331 */           final String tag_name = (String)tags.get(folder_index);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 336 */           boolean save_torrents = save_torrents_default;
/*     */           
/* 338 */           if ((torrent_save_path.length() == 0) || (new File(torrent_save_path).getAbsolutePath().equals(folder.getAbsolutePath())) || (!new File(torrent_save_path).isDirectory()))
/*     */           {
/*     */ 
/*     */ 
/* 342 */             save_torrents = false;
/*     */           }
/*     */           
/* 345 */           String[] currentFileList = folder.list(this.filename_filter);
/*     */           
/* 347 */           if (currentFileList == null) {
/* 348 */             Logger.log(new LogEvent(LOGID, 3, "There was a problem trying to get a listing of torrents from " + folder));
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 353 */             for (int i = 0; i < currentFileList.length; i++)
/*     */             {
/* 355 */               if (!this.running) {
/*     */                 return;
/*     */               }
/*     */               
/*     */ 
/* 360 */               File file = new File(folder, currentFileList[i]);
/*     */               
/*     */ 
/*     */ 
/*     */               try
/*     */               {
/* 366 */                 TOTorrent torrent = TorrentUtils.readFromFile(file, false);
/*     */                 
/* 368 */                 if (global_manager.getDownloadManager(torrent) != null)
/*     */                 {
/* 370 */                   if (Logger.isEnabled()) {
/* 371 */                     Logger.log(new LogEvent(LOGID, file.getAbsolutePath() + " is already being downloaded"));
/*     */                   }
/*     */                   
/*     */ 
/*     */                 }
/* 376 */                 else if (plugin_dm.lookupDownloadStub(torrent.getHash()) != null)
/*     */                 {
/*     */ 
/*     */ 
/* 380 */                   if (Logger.isEnabled()) {
/* 381 */                     Logger.log(new LogEvent(LOGID, file.getAbsolutePath() + " is an archived download"));
/*     */                   }
/*     */                   
/* 384 */                   if (!save_torrents)
/*     */                   {
/* 386 */                     File imported = new File(folder, file.getName() + ".imported");
/*     */                     
/* 388 */                     TorrentUtils.move(file, imported);
/*     */                   }
/*     */                   else
/*     */                   {
/* 392 */                     this.to_delete.add(torrent);
/*     */                   }
/*     */                 }
/*     */                 else
/*     */                 {
/* 397 */                   DownloadManagerInitialisationAdapter dmia = new DownloadManagerInitialisationAdapter()
/*     */                   {
/*     */ 
/*     */                     public int getActions()
/*     */                     {
/* 402 */                       return 1;
/*     */                     }
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */                     public void initialised(org.gudy.azureus2.core3.download.DownloadManager dm, boolean for_seeding)
/*     */                     {
/* 410 */                       if (tag_name != null)
/*     */                       {
/* 412 */                         TagManager tm = TagManagerFactory.getTagManager();
/*     */                         
/* 414 */                         TagType tt = tm.getTagType(3);
/*     */                         
/* 416 */                         Tag tag = tt.getTag(tag_name, true);
/*     */                         try
/*     */                         {
/* 419 */                           if (tag == null)
/*     */                           {
/* 421 */                             tag = tt.createTag(tag_name, true);
/*     */                           }
/*     */                           
/* 424 */                           tag.addTaggable(dm);
/*     */                         }
/*     */                         catch (Throwable e)
/*     */                         {
/* 428 */                           Debug.out(e);
/*     */                         }
/*     */                         
/*     */                       }
/*     */                     }
/* 433 */                   };
/* 434 */                   byte[] hash = null;
/*     */                   try {
/* 436 */                     hash = torrent.getHash();
/*     */                   }
/*     */                   catch (Exception e) {}
/* 439 */                   if (!save_torrents)
/*     */                   {
/* 441 */                     File imported = new File(folder, file.getName() + ".imported");
/*     */                     
/* 443 */                     TorrentUtils.move(file, imported);
/*     */                     
/* 445 */                     global_manager.addDownloadManager(imported.getAbsolutePath(), hash, data_save_path, start_state, true, false, dmia);
/*     */ 
/*     */                   }
/*     */                   else
/*     */                   {
/* 450 */                     global_manager.addDownloadManager(file.getAbsolutePath(), hash, data_save_path, start_state, true, false, dmia);
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/* 455 */                     this.to_delete.add(torrent);
/*     */                   }
/*     */                   
/* 458 */                   if (Logger.isEnabled()) {
/* 459 */                     Logger.log(new LogEvent(LOGID, "Auto-imported " + file.getAbsolutePath()));
/*     */                   }
/*     */                 }
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 465 */                 Debug.out("Failed to auto-import torrent file '" + file.getAbsolutePath() + "' - " + Debug.getNestedExceptionMessage(e));
/*     */                 
/*     */ 
/* 468 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             } }
/*     */         }
/*     */       }
/*     */     } finally {
/* 474 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/helpers/TorrentFolderWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */