/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostException;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.TrackersUtil;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProgressPanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */   implements TOTorrentProgressListener
/*     */ {
/*     */   Text tasks;
/*     */   ProgressBar progress;
/*     */   Display display;
/*     */   Button show_torrent_file;
/*     */   
/*     */   public ProgressPanel(NewTorrentWizard wizard, IWizardPanel<NewTorrentWizard> _previousPanel)
/*     */   {
/*  74 */     super(wizard, _previousPanel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/*  80 */     this.display = ((NewTorrentWizard)this.wizard).getDisplay();
/*  81 */     ((NewTorrentWizard)this.wizard).setTitle(MessageText.getString("wizard.progresstitle"));
/*  82 */     ((NewTorrentWizard)this.wizard).setCurrentInfo("");
/*  83 */     ((NewTorrentWizard)this.wizard).setPreviousEnabled(false);
/*  84 */     Composite rootPanel = ((NewTorrentWizard)this.wizard).getPanel();
/*  85 */     GridLayout layout = new GridLayout();
/*  86 */     layout.numColumns = 1;
/*  87 */     rootPanel.setLayout(layout);
/*     */     
/*  89 */     Composite panel = new Composite(rootPanel, 0);
/*  90 */     GridData gridData = new GridData(772);
/*  91 */     panel.setLayoutData(gridData);
/*  92 */     layout = new GridLayout();
/*  93 */     layout.numColumns = 2;
/*  94 */     panel.setLayout(layout);
/*     */     
/*  96 */     this.tasks = new Text(panel, 2058);
/*  97 */     this.tasks.setBackground(this.display.getSystemColor(1));
/*  98 */     gridData = new GridData(1808);
/*  99 */     gridData.heightHint = 120;
/* 100 */     gridData.horizontalSpan = 2;
/* 101 */     this.tasks.setLayoutData(gridData);
/*     */     
/* 103 */     this.progress = new ProgressBar(panel, 0);
/* 104 */     this.progress.setMinimum(0);
/* 105 */     this.progress.setMaximum(0);
/* 106 */     gridData = new GridData(768);
/* 107 */     gridData.horizontalSpan = 2;
/* 108 */     this.progress.setLayoutData(gridData);
/*     */     
/* 110 */     Label label = new Label(panel, 0);
/* 111 */     gridData = new GridData(768);
/* 112 */     label.setLayoutData(gridData);
/* 113 */     Composite Browsepanel = new Composite(panel, 0);
/* 114 */     layout = new GridLayout();
/* 115 */     layout.numColumns = 2;
/* 116 */     Browsepanel.setLayout(layout);
/*     */     
/* 118 */     label = new Label(Browsepanel, 0);
/* 119 */     Messages.setLanguageText(label, "wizard.newtorrent.showtorrent");
/*     */     
/* 121 */     this.show_torrent_file = new Button(Browsepanel, 8);
/*     */     
/* 123 */     Messages.setLanguageText(this.show_torrent_file, "MyTorrentsView.menu.explore");
/*     */     
/* 125 */     this.show_torrent_file.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 132 */         ManagerUtils.open(new File(((NewTorrentWizard)ProgressPanel.this.wizard).savePath));
/*     */       }
/*     */       
/* 135 */     });
/* 136 */     this.show_torrent_file.setEnabled(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void finish()
/*     */   {
/* 143 */     Thread t = new AEThread("Torrent Maker") {
/*     */       public void runSupport() {
/* 145 */         ProgressPanel.this.makeTorrent();
/*     */       }
/* 147 */     };
/* 148 */     t.setPriority(1);
/* 149 */     t.setDaemon(true);
/* 150 */     t.start();
/*     */   }
/*     */   
/*     */   public void makeTorrent()
/*     */   {
/* 155 */     int tracker_type = ((NewTorrentWizard)this.wizard).getTrackerType();
/*     */     
/* 157 */     if (tracker_type == 2)
/*     */     {
/* 159 */       TrackersUtil.getInstance().addTracker(((NewTorrentWizard)this.wizard).trackerURL);
/*     */     }
/*     */     
/*     */     File f;
/*     */     File f;
/* 164 */     if (((NewTorrentWizard)this.wizard).create_mode == 2) {
/* 165 */       f = new File(((NewTorrentWizard)this.wizard).directoryPath); } else { File f;
/* 166 */       if (((NewTorrentWizard)this.wizard).create_mode == 1) {
/* 167 */         f = new File(((NewTorrentWizard)this.wizard).singlePath);
/*     */       } else {
/* 169 */         f = ((NewTorrentWizard)this.wizard).byo_desc_file;
/*     */       }
/*     */     }
/*     */     try {
/* 173 */       URL url = new URL(((NewTorrentWizard)this.wizard).trackerURL);
/*     */       
/*     */       TOTorrent torrent;
/*     */       final TOTorrent torrent;
/* 177 */       if (((NewTorrentWizard)this.wizard).getPieceSizeComputed())
/*     */       {
/* 179 */         ((NewTorrentWizard)this.wizard).creator = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(f, url, ((NewTorrentWizard)this.wizard).getAddOtherHashes());
/*     */         
/*     */ 
/*     */ 
/* 183 */         ((NewTorrentWizard)this.wizard).creator.addListener(this);
/*     */         
/* 185 */         ((NewTorrentWizard)this.wizard).creator.setFileIsLayoutDescriptor(((NewTorrentWizard)this.wizard).create_mode == 3);
/*     */         
/* 187 */         torrent = ((NewTorrentWizard)this.wizard).creator.create();
/*     */       }
/*     */       else {
/* 190 */         ((NewTorrentWizard)this.wizard).creator = TOTorrentFactory.createFromFileOrDirWithFixedPieceLength(f, url, ((NewTorrentWizard)this.wizard).getAddOtherHashes(), ((NewTorrentWizard)this.wizard).getPieceSizeManual());
/*     */         
/*     */ 
/*     */ 
/* 194 */         ((NewTorrentWizard)this.wizard).creator.addListener(this);
/*     */         
/* 196 */         ((NewTorrentWizard)this.wizard).creator.setFileIsLayoutDescriptor(((NewTorrentWizard)this.wizard).create_mode == 3);
/*     */         
/* 198 */         torrent = ((NewTorrentWizard)this.wizard).creator.create();
/*     */       }
/*     */       
/* 201 */       if (tracker_type == 3)
/*     */       {
/* 203 */         TorrentUtils.setDecentralised(torrent);
/*     */       }
/*     */       
/* 206 */       torrent.setComment(((NewTorrentWizard)this.wizard).getComment());
/*     */       
/* 208 */       TorrentUtils.setDHTBackupEnabled(torrent, ((NewTorrentWizard)this.wizard).permitDHT);
/*     */       
/* 210 */       TorrentUtils.setPrivate(torrent, ((NewTorrentWizard)this.wizard).getPrivateTorrent());
/*     */       
/* 212 */       LocaleTorrentUtil.setDefaultTorrentEncoding(torrent);
/*     */       
/*     */       File save_dir;
/*     */       
/*     */       final File save_dir;
/*     */       
/* 218 */       if (((NewTorrentWizard)this.wizard).create_mode == 2)
/*     */       {
/* 220 */         save_dir = f;
/*     */       } else { File save_dir;
/* 222 */         if (((NewTorrentWizard)this.wizard).create_mode == 1)
/*     */         {
/* 224 */           save_dir = f.getParentFile();
/*     */         }
/*     */         else
/*     */         {
/* 228 */           String save_path = COConfigurationManager.getStringParameter("Default save path");
/*     */           
/* 230 */           File f_save_path = new File(save_path);
/*     */           
/* 232 */           if (!f_save_path.canWrite())
/*     */           {
/* 234 */             throw new Exception("Default save path is not configured: See Tools->Options->File");
/*     */           }
/*     */           
/* 237 */           save_dir = f_save_path;
/*     */         }
/*     */       }
/* 240 */       if (((NewTorrentWizard)this.wizard).useMultiTracker) {
/* 241 */         reportCurrentTask(MessageText.getString("wizard.addingmt"));
/* 242 */         TorrentUtils.listToAnnounceGroups(((NewTorrentWizard)this.wizard).trackers, torrent);
/*     */       }
/*     */       
/* 245 */       if ((((NewTorrentWizard)this.wizard).useWebSeed) && (((NewTorrentWizard)this.wizard).webseeds.size() > 0)) {
/* 246 */         reportCurrentTask(MessageText.getString("wizard.webseed.adding"));
/*     */         
/* 248 */         Map ws = ((NewTorrentWizard)this.wizard).webseeds;
/*     */         
/* 250 */         List getright = (List)ws.get("getright");
/*     */         
/* 252 */         if (getright.size() > 0)
/*     */         {
/* 254 */           for (int i = 0; i < getright.size(); i++) {
/* 255 */             reportCurrentTask("    GetRight: " + getright.get(i));
/*     */           }
/* 257 */           torrent.setAdditionalListProperty("url-list", new ArrayList(getright));
/*     */         }
/*     */         
/* 260 */         List webseed = (List)ws.get("webseed");
/*     */         
/* 262 */         if (webseed.size() > 0)
/*     */         {
/* 264 */           for (int i = 0; i < webseed.size(); i++) {
/* 265 */             reportCurrentTask("    WebSeed: " + webseed.get(i));
/*     */           }
/* 267 */           torrent.setAdditionalListProperty("httpseeds", new ArrayList(webseed));
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
/* 286 */       reportCurrentTask(MessageText.getString("wizard.savingfile"));
/*     */       
/* 288 */       final File torrent_file = new File(((NewTorrentWizard)this.wizard).savePath);
/*     */       
/* 290 */       torrent.serialiseToBEncodedFile(torrent_file);
/* 291 */       reportCurrentTask(MessageText.getString("wizard.filesaved"));
/*     */       
/* 293 */       ((NewTorrentWizard)this.wizard).switchToClose(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 299 */           ProgressPanel.this.show_torrent_file.setEnabled(true);
/*     */         }
/*     */       });
/*     */       
/* 303 */       if (((NewTorrentWizard)this.wizard).autoOpen) {
/* 304 */         CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*     */         {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 307 */             boolean start_stopped = COConfigurationManager.getBooleanParameter("Default Start Torrents Stopped");
/*     */             
/* 309 */             byte[] hash = null;
/*     */             try {
/* 311 */               hash = torrent.getHash();
/*     */             }
/*     */             catch (TOTorrentException e1) {}
/*     */             
/* 315 */             if ((((NewTorrentWizard)ProgressPanel.this.wizard).forceStart) || (((NewTorrentWizard)ProgressPanel.this.wizard).superseed))
/*     */             {
/*     */ 
/*     */ 
/* 319 */               start_stopped = false;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 324 */             final String initialTags = ((NewTorrentWizard)ProgressPanel.this.wizard).getInitialTags(true);
/*     */             DownloadManagerInitialisationAdapter dmia;
/* 326 */             DownloadManagerInitialisationAdapter dmia; if (initialTags.length() > 0)
/*     */             {
/* 328 */               dmia = new DownloadManagerInitialisationAdapter()
/*     */               {
/*     */ 
/*     */                 public int getActions()
/*     */                 {
/*     */ 
/* 334 */                   return 1;
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void initialised(DownloadManager dm, boolean for_seeding)
/*     */                 {
/* 342 */                   TagManager tm = TagManagerFactory.getTagManager();
/*     */                   
/* 344 */                   TagType tag_type = tm.getTagType(3);
/*     */                   
/* 346 */                   String[] bits = initialTags.replace(';', ',').split(",");
/*     */                   
/* 348 */                   for (String tag : bits)
/*     */                   {
/* 350 */                     tag = tag.trim();
/*     */                     
/* 352 */                     if (tag.length() > 0) {
/*     */                       try
/*     */                       {
/* 355 */                         Tag t = tag_type.getTag(tag, true);
/*     */                         
/* 357 */                         if (t == null)
/*     */                         {
/* 359 */                           t = tag_type.createTag(tag, true);
/*     */                         }
/*     */                         
/* 362 */                         t.addTaggable(dm);
/*     */                       }
/*     */                       catch (Throwable e)
/*     */                       {
/* 366 */                         Debug.out(e);
/*     */                       }
/*     */                       
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               };
/*     */             } else {
/* 374 */               dmia = null;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 380 */             final DownloadManager dm = core.getGlobalManager().addDownloadManager(torrent_file.toString(), hash, save_dir.toString(), start_stopped ? 70 : 75, true, true, dmia);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 389 */             if ((!start_stopped) && (dm != null))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 395 */               dm.getGlobalManager().moveTop(new DownloadManager[] { dm });
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 400 */             if ((((NewTorrentWizard)ProgressPanel.this.wizard).autoHost) && (((NewTorrentWizard)ProgressPanel.this.wizard).getTrackerType() != 2))
/*     */             {
/*     */               try
/*     */               {
/* 404 */                 core.getTrackerHost().hostTorrent(torrent, true, false);
/*     */               }
/*     */               catch (TRHostException e) {
/* 407 */                 Logger.log(new LogAlert(true, "Host operation fails", e));
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 412 */             if (dm != null)
/*     */             {
/* 414 */               if (((NewTorrentWizard)ProgressPanel.this.wizard).forceStart)
/*     */               {
/* 416 */                 dm.setForceStart(true);
/*     */               }
/*     */               
/* 419 */               if (((NewTorrentWizard)ProgressPanel.this.wizard).superseed)
/*     */               {
/* 421 */                 new AEThread2("startwait")
/*     */                 {
/*     */ 
/*     */                   public void run()
/*     */                   {
/* 426 */                     long start = SystemTime.getMonotonousTime();
/*     */                     
/*     */ 
/*     */ 
/* 430 */                     while (!dm.isDestroyed())
/*     */                     {
/*     */ 
/*     */ 
/*     */ 
/* 435 */                       long elapsed = SystemTime.getMonotonousTime() - start;
/*     */                       
/* 437 */                       if (elapsed > 60000L)
/*     */                       {
/* 439 */                         int state = dm.getState();
/*     */                         
/* 441 */                         if ((state == 100) || (state == 70)) {
/*     */                           break;
/*     */                         }
/*     */                       }
/*     */                       
/*     */ 
/*     */ 
/* 448 */                       if (elapsed > 300000L) {
/*     */                         break;
/*     */                       }
/*     */                       
/*     */ 
/* 453 */                       PEPeerManager pm = dm.getPeerManager();
/*     */                       
/* 455 */                       if (pm != null)
/*     */                       {
/* 457 */                         pm.setSuperSeedMode(true);
/*     */                         
/* 459 */                         break;
/*     */                       }
/*     */                       try
/*     */                       {
/* 463 */                         Thread.sleep(1000L);
/*     */                       }
/*     */                       catch (Throwable e)
/*     */                       {
/*     */                         break;
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }.start();
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 480 */       if ((e instanceof TOTorrentException))
/*     */       {
/* 482 */         TOTorrentException te = (TOTorrentException)e;
/*     */         
/* 484 */         if (te.getReason() != 9)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 489 */           reportCurrentTask(MessageText.getString("wizard.operationfailed"));
/* 490 */           reportCurrentTask(TorrentUtils.exceptionToText(te));
/*     */         }
/*     */       } else {
/* 493 */         Debug.printStackTrace(e);
/* 494 */         reportCurrentTask(MessageText.getString("wizard.operationfailed"));
/* 495 */         reportCurrentTask(Debug.getStackTrace(e));
/*     */       }
/*     */       
/* 498 */       ((NewTorrentWizard)this.wizard).switchToClose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportCurrentTask(final String task_description)
/*     */   {
/* 506 */     if ((this.display != null) && (!this.display.isDisposed())) {
/* 507 */       this.display.asyncExec(new AERunnable() {
/*     */         public void runSupport() {
/* 509 */           if ((ProgressPanel.this.tasks != null) && (!ProgressPanel.this.tasks.isDisposed())) {
/* 510 */             ProgressPanel.this.tasks.append(task_description + Text.DELIMITER);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportProgress(final int percent_complete)
/*     */   {
/* 521 */     if ((this.display != null) && (!this.display.isDisposed())) {
/* 522 */       this.display.asyncExec(new AERunnable() {
/*     */         public void runSupport() {
/* 524 */           if ((ProgressPanel.this.progress != null) && (!ProgressPanel.this.progress.isDisposed())) {
/* 525 */             ProgressPanel.this.progress.setSelection(percent_complete);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/ProgressPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */