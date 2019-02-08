/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.ui.InitializerListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.Initializer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils.ViewTitleExtraInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SBC_LibraryView
/*     */   extends SkinView
/*     */   implements UIPluginViewToolBarListener
/*     */ {
/*     */   private static final String ID = "library-list";
/*     */   public static final int MODE_BIGTABLE = 0;
/*     */   public static final int MODE_SMALLTABLE = 1;
/*     */   public static final int TORRENTS_ALL = 0;
/*     */   public static final int TORRENTS_COMPLETE = 1;
/*     */   public static final int TORRENTS_INCOMPLETE = 2;
/*     */   public static final int TORRENTS_UNOPENED = 3;
/* 100 */   private static final String[] modeViewIDs = { "library-big-area", "library-small-area" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 105 */   private static final String[] modeIDs = { "library.table.big", "library.table.small" };
/*     */   
/*     */   private static boolean header_show_uptime;
/*     */   
/*     */   private static boolean header_show_rates;
/*     */   
/*     */   private static volatile OverallStats totalStats;
/*     */   
/*     */   private static volatile int selection_count;
/*     */   
/*     */   private static volatile long selection_size;
/*     */   
/*     */   private static volatile long selection_done;
/* 118 */   private static volatile DownloadManager[] selection_dms = new DownloadManager[0];
/*     */   private int viewMode;
/*     */   
/*     */   static {
/* 122 */     SimpleTimer.addPeriodicEvent("SBLV:updater", 60000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/* 131 */         if (SBC_LibraryView.header_show_uptime)
/*     */         {
/* 133 */           SB_Transfers.triggerCountRefreshListeners();
/*     */         }
/*     */         
/*     */       }
/* 137 */     });
/* 138 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "MyTorrentsView.showuptime", "MyTorrentsView.showrates" }, new ParameterListener()
/*     */     {
/*     */       private TimerEventPeriodic rate_event;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/* 150 */         SBC_LibraryView.access$002(COConfigurationManager.getBooleanParameter("MyTorrentsView.showuptime"));
/* 151 */         SBC_LibraryView.access$102(COConfigurationManager.getBooleanParameter("MyTorrentsView.showrates"));
/*     */         
/* 153 */         SB_Transfers.triggerCountRefreshListeners();
/*     */         
/* 155 */         synchronized (this)
/*     */         {
/* 157 */           if (SBC_LibraryView.header_show_rates)
/*     */           {
/* 159 */             if (this.rate_event == null)
/*     */             {
/* 161 */               this.rate_event = SimpleTimer.addPeriodicEvent("SBLV:rate-updater", 1000L, new TimerEventPerformer()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void perform(TimerEvent event) {}
/*     */ 
/*     */ 
/*     */ 
/*     */               });
/*     */ 
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */           }
/* 176 */           else if (this.rate_event != null)
/*     */           {
/* 178 */             this.rate_event.cancel();
/*     */             
/* 180 */             this.rate_event = null;
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/* 186 */     });
/* 187 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void azureusCoreRunning(AzureusCore core)
/*     */       {
/*     */ 
/* 194 */         SBC_LibraryView.access$202(StatsFactory.getStats());
/*     */       }
/*     */       
/* 197 */     });
/* 198 */     SelectedContentManager.addCurrentlySelectedContentListener(new SelectedContentListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void currentlySelectedContentChanged(ISelectedContent[] currentContent, String viewId)
/*     */       {
/*     */ 
/*     */ 
/* 206 */         SBC_LibraryView.access$302(currentContent.length);
/*     */         
/* 208 */         long total_size = 0L;
/* 209 */         long total_done = 0L;
/*     */         
/* 211 */         ArrayList<DownloadManager> dms = new ArrayList(currentContent.length);
/*     */         
/* 213 */         for (ISelectedContent sc : currentContent)
/*     */         {
/* 215 */           DownloadManager dm = sc.getDownloadManager();
/*     */           
/* 217 */           if (dm != null)
/*     */           {
/* 219 */             dms.add(dm);
/*     */             
/* 221 */             int file_index = sc.getFileIndex();
/*     */             
/* 223 */             if (file_index == -1)
/*     */             {
/* 225 */               DiskManagerFileInfo[] file_infos = dm.getDiskManagerFileInfoSet().getFiles();
/*     */               
/* 227 */               for (DiskManagerFileInfo file_info : file_infos)
/*     */               {
/* 229 */                 if (!file_info.isSkipped())
/*     */                 {
/* 231 */                   total_size += file_info.getLength();
/* 232 */                   total_done += file_info.getDownloaded();
/*     */                 }
/*     */               }
/*     */             }
/*     */             else {
/* 237 */               DiskManagerFileInfo file_info = dm.getDiskManagerFileInfoSet().getFiles()[file_index];
/*     */               
/* 239 */               if (!file_info.isSkipped())
/*     */               {
/* 241 */                 total_size += file_info.getLength();
/* 242 */                 total_done += file_info.getDownloaded();
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 248 */         SBC_LibraryView.access$402(total_size);
/* 249 */         SBC_LibraryView.access$502(total_done);
/*     */         
/* 251 */         SBC_LibraryView.access$602((DownloadManager[])dms.toArray(new DownloadManager[dms.size()]));
/*     */         
/* 253 */         SB_Transfers.triggerCountRefreshListeners(); } }); }
/*     */   
/*     */   private SWTSkinButtonUtility btnSmallTable;
/*     */   private SWTSkinButtonUtility btnBigTable;
/*     */   
/* 258 */   public SBC_LibraryView() { this.viewMode = -1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 266 */     this.torrentFilterMode = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 276 */     this.waitProgress = 0;
/*     */   }
/*     */   
/*     */   private SWTSkinObject soListArea;
/*     */   private int torrentFilterMode;
/*     */   private String torrentFilter;
/*     */   private SWTSkinObject soWait;
/*     */   private SWTSkinObject soWaitProgress;
/*     */   
/* 285 */   public void setViewMode(int viewMode, boolean save) { if ((viewMode >= modeViewIDs.length) || (viewMode < 0) || (viewMode == this.viewMode))
/*     */     {
/* 287 */       return;
/*     */     }
/*     */     
/* 290 */     if (!COConfigurationManager.getBooleanParameter("Library.EnableSimpleView"))
/*     */     {
/* 292 */       viewMode = 1;
/*     */     }
/*     */     
/* 295 */     int oldViewMode = this.viewMode;
/*     */     
/* 297 */     this.viewMode = viewMode;
/*     */     
/* 299 */     if ((oldViewMode >= 0) && (oldViewMode < modeViewIDs.length)) {
/* 300 */       SWTSkinObject soOldViewArea = getSkinObject(modeViewIDs[oldViewMode]);
/*     */       
/* 302 */       if (soOldViewArea != null) {
/* 303 */         soOldViewArea.setVisible(false);
/*     */       }
/*     */     }
/*     */     
/* 307 */     SelectedContentManager.clearCurrentlySelectedContent();
/*     */     
/* 309 */     SWTSkinObject soViewArea = getSkinObject(modeViewIDs[viewMode]);
/* 310 */     if (soViewArea == null) {
/* 311 */       soViewArea = this.skin.createSkinObject(modeIDs[viewMode] + this.torrentFilterMode, modeIDs[viewMode], this.soListArea);
/*     */       
/* 313 */       soViewArea.getControl().setData("SBC_LibraryView:ViewMode", Integer.valueOf(viewMode));
/* 314 */       this.skin.layout();
/* 315 */       soViewArea.setVisible(true);
/* 316 */       soViewArea.getControl().setLayoutData(Utils.getFilledFormData());
/*     */     } else {
/* 318 */       soViewArea.setVisible(true);
/*     */     }
/*     */     
/* 321 */     if (save) {
/* 322 */       COConfigurationManager.setParameter(this.torrentFilter + ".viewmode", viewMode);
/*     */     }
/*     */     
/* 325 */     String entryID = null;
/* 326 */     if (this.torrentFilterMode == 0) {
/* 327 */       entryID = "Library";
/* 328 */     } else if (this.torrentFilterMode == 1) {
/* 329 */       entryID = "LibraryCD";
/* 330 */     } else if (this.torrentFilterMode == 2) {
/* 331 */       entryID = "LibraryDL";
/* 332 */     } else if (this.torrentFilterMode == 3) {
/* 333 */       entryID = "LibraryUnopened";
/*     */     }
/*     */     
/* 336 */     if (entryID != null) {
/* 337 */       MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 338 */       this.currentEntry = mdi.getEntry(entryID);
/* 339 */       if (this.currentEntry != null) {
/* 340 */         this.currentEntry.setLogID(entryID + "-" + viewMode);
/*     */       }
/*     */     }
/*     */     
/* 344 */     SB_Transfers.triggerCountRefreshListeners();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(final SWTSkinObject skinObject, Object params)
/*     */   {
/* 350 */     this.soWait = null;
/*     */     try {
/* 352 */       this.soWait = getSkinObject("library-wait");
/* 353 */       this.soWaitProgress = getSkinObject("library-wait-progress");
/* 354 */       this.soWaitTask = ((SWTSkinObjectText)getSkinObject("library-wait-task"));
/* 355 */       if (this.soWaitProgress != null) {
/* 356 */         this.soWaitProgress.getControl().addPaintListener(new PaintListener() {
/*     */           public void paintControl(PaintEvent e) {
/* 358 */             Control c = (Control)e.widget;
/* 359 */             Point size = c.getSize();
/* 360 */             e.gc.setBackground(ColorCache.getColor(e.display, "#23a7df"));
/* 361 */             int breakX = size.x * SBC_LibraryView.this.waitProgress / 100;
/* 362 */             e.gc.fillRectangle(0, 0, breakX, size.y);
/* 363 */             e.gc.setBackground(ColorCache.getColor(e.display, "#cccccc"));
/* 364 */             e.gc.fillRectangle(breakX, 0, size.x - breakX, size.y);
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 369 */       this.soLibraryInfo = ((SWTSkinObjectText)getSkinObject("library-info"));
/*     */       
/* 371 */       if (this.soLibraryInfo != null)
/*     */       {
/* 373 */         SB_Transfers.addCountRefreshListener(new SB_Transfers.countRefreshListener()
/*     */         {
/*     */           final Map<Composite, ExtraInfoProvider> extra_info_map;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void countRefreshed(SB_Transfers.stats statsWithLowNoise, SB_Transfers.stats statsNoLowNoise)
/*     */           {
/* 475 */             SB_Transfers.stats stats = SBC_LibraryView.this.viewMode == 1 ? statsWithLowNoise : statsNoLowNoise;
/*     */             
/*     */ 
/*     */ 
/*     */             String s;
/*     */             
/*     */ 
/*     */ 
/* 483 */             if ((SBC_LibraryView.this.torrentFilterMode == 0) || ((SBC_LibraryView.this.datasource instanceof Tag))) {
/*     */               String s;
/* 485 */               if ((SBC_LibraryView.this.datasource instanceof Category)) {
/* 486 */                 Category cat = (Category)SBC_LibraryView.this.datasource;
/*     */                 
/* 488 */                 String id = "library.category.header";
/*     */                 
/* 490 */                 s = MessageText.getString(id, new String[] { cat.getType() != 0 ? MessageText.getString(cat.getName()) : cat.getName() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               }
/* 497 */               else if ((SBC_LibraryView.this.datasource instanceof Tag))
/*     */               {
/* 499 */                 Tag tag = (Tag)SBC_LibraryView.this.datasource;
/*     */                 
/* 501 */                 String id = "library.tag.header";
/*     */                 
/* 503 */                 String s = MessageText.getString(id, new String[] { tag.getTagName(true) });
/*     */                 
/*     */ 
/*     */ 
/* 507 */                 String desc = tag.getDescription();
/*     */                 
/* 509 */                 if (desc != null)
/*     */                 {
/* 511 */                   s = s + " - " + desc;
/*     */                 }
/*     */               }
/*     */               else {
/* 515 */                 String id = "library.all.header";
/* 516 */                 if (stats.numComplete + stats.numIncomplete != 1) {
/* 517 */                   id = id + ".p";
/*     */                 }
/* 519 */                 String s = MessageText.getString(id, new String[] { String.valueOf(stats.numComplete + stats.numIncomplete), String.valueOf(stats.numSeeding + stats.numDownloading) });
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 525 */                 if (stats.numQueued > 0)
/*     */                 {
/* 527 */                   s = s + ", " + MessageText.getString("label.num_queued", new String[] { String.valueOf(stats.numQueued) });
/*     */                 }
/*     */               }
/*     */             }
/*     */             else {
/*     */               String s;
/* 533 */               if (SBC_LibraryView.this.torrentFilterMode == 2) {
/* 534 */                 String id = "library.incomplete.header";
/* 535 */                 if (stats.numDownloading != 1) {
/* 536 */                   id = id + ".p";
/*     */                 }
/* 538 */                 int numWaiting = Math.max(stats.numIncomplete - stats.numDownloading, 0);
/* 539 */                 s = MessageText.getString(id, new String[] { String.valueOf(stats.numDownloading), String.valueOf(numWaiting) });
/*     */               }
/*     */               else
/*     */               {
/*     */                 String s;
/*     */                 
/* 545 */                 if ((SBC_LibraryView.this.torrentFilterMode == 3) || (SBC_LibraryView.this.torrentFilterMode == 1))
/*     */                 {
/* 547 */                   String id = "library.unopened.header";
/* 548 */                   if (stats.numUnOpened != 1) {
/* 549 */                     id = id + ".p";
/*     */                   }
/* 551 */                   s = MessageText.getString(id, new String[] { String.valueOf(stats.numUnOpened) });
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/*     */ 
/* 557 */                   s = "";
/*     */                 }
/*     */               } }
/* 560 */             synchronized (this.extra_info_map)
/*     */             {
/* 562 */               int filter_total = 0;
/* 563 */               int filter_active = 0;
/*     */               
/* 565 */               boolean filter_enabled = false;
/*     */               
/* 567 */               for (ExtraInfoProvider provider : this.extra_info_map.values())
/*     */               {
/* 569 */                 if (SBC_LibraryView.this.viewMode == provider.view_mode)
/*     */                 {
/* 571 */                   if (provider.enabled)
/*     */                   {
/* 573 */                     filter_enabled = true;
/* 574 */                     filter_total += provider.value;
/* 575 */                     filter_active += provider.active;
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 580 */               if (filter_enabled)
/*     */               {
/* 582 */                 String extra = MessageText.getString("filter.header.matches2", new String[] { String.valueOf(filter_total), String.valueOf(filter_active) });
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 587 */                 s = s + " " + extra;
/*     */               }
/*     */             }
/*     */             
/* 591 */             if (SBC_LibraryView.selection_count > 1)
/*     */             {
/* 593 */               s = s + ", " + MessageText.getString("label.num_selected", new String[] { String.valueOf(SBC_LibraryView.selection_count) });
/*     */               
/*     */ 
/*     */ 
/* 597 */               String size_str = null;
/* 598 */               String rate_str = null;
/*     */               
/* 600 */               if (SBC_LibraryView.selection_size > 0L)
/*     */               {
/* 602 */                 if (SBC_LibraryView.selection_size == SBC_LibraryView.selection_done)
/*     */                 {
/* 604 */                   size_str = DisplayFormatters.formatByteCountToKiBEtc(SBC_LibraryView.selection_size);
/*     */                 }
/*     */                 else
/*     */                 {
/* 608 */                   size_str = DisplayFormatters.formatByteCountToKiBEtc(SBC_LibraryView.selection_done) + "/" + DisplayFormatters.formatByteCountToKiBEtc(SBC_LibraryView.selection_size);
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/* 613 */               DownloadManager[] dms = SBC_LibraryView.selection_dms;
/*     */               
/* 615 */               if ((SBC_LibraryView.header_show_rates) && (dms.length > 1))
/*     */               {
/* 617 */                 long total_data_up = 0L;
/* 618 */                 long total_prot_up = 0L;
/* 619 */                 long total_data_down = 0L;
/* 620 */                 long total_prot_down = 0L;
/*     */                 
/* 622 */                 for (DownloadManager dm : dms)
/*     */                 {
/* 624 */                   DownloadManagerStats dm_stats = dm.getStats();
/*     */                   
/* 626 */                   total_prot_up += dm_stats.getProtocolSendRate();
/* 627 */                   total_data_up += dm_stats.getDataSendRate();
/* 628 */                   total_prot_down += dm_stats.getProtocolReceiveRate();
/* 629 */                   total_data_down += dm_stats.getDataReceiveRate();
/*     */                 }
/*     */                 
/* 632 */                 rate_str = MessageText.getString("ConfigView.download.abbreviated") + DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(total_data_down, total_prot_down) + " " + MessageText.getString("ConfigView.upload.abbreviated") + DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(total_data_up, total_prot_up);
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 637 */               if ((size_str != null) || (rate_str != null))
/*     */               {
/*     */                 String temp;
/*     */                 String temp;
/* 641 */                 if (size_str == null)
/*     */                 {
/* 643 */                   temp = rate_str;
/*     */                 } else { String temp;
/* 645 */                   if (rate_str == null)
/*     */                   {
/* 647 */                     temp = size_str;
/*     */                   }
/*     */                   else
/*     */                   {
/* 651 */                     temp = size_str + "; " + rate_str;
/*     */                   }
/*     */                 }
/* 654 */                 s = s + " (" + temp + ")";
/*     */               }
/*     */             }
/*     */             
/* 658 */             if ((SBC_LibraryView.header_show_uptime) && (SBC_LibraryView.totalStats != null))
/*     */             {
/* 660 */               long up_secs = SBC_LibraryView.totalStats.getSessionUpTime() / 60L * 60L;
/*     */               
/*     */               String op;
/*     */               String op;
/* 664 */               if (up_secs < 60L)
/*     */               {
/* 666 */                 up_secs = 60L;
/*     */                 
/* 668 */                 op = "<";
/*     */               }
/*     */               else
/*     */               {
/* 672 */                 op = " ";
/*     */               }
/*     */               
/* 675 */               String up_str = TimeFormatter.format2(up_secs, false);
/*     */               
/* 677 */               if (s.equals("")) {
/* 678 */                 Debug.out("eh");
/*     */               }
/* 680 */               s = s + "; " + MessageText.getString("label.uptime_coarse", new String[] { op, up_str });
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 686 */             SBC_LibraryView.this.soLibraryInfo.setText(s);
/*     */           }
/*     */           
/*     */ 
/*     */           class ExtraInfoProvider
/*     */           {
/*     */             int view_mode;
/*     */             
/*     */             boolean enabled;
/*     */             
/*     */             int value;
/*     */             int active;
/*     */             
/*     */             private ExtraInfoProvider(int vm)
/*     */             {
/* 701 */               this.view_mode = vm;
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/*     */ 
/* 711 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 712 */       if (this.soWait != null) {
/* 713 */         this.soWait.setVisible(true);
/*     */       }
/*     */       
/* 716 */       final Initializer initializer = Initializer.getLastInitializer();
/* 717 */       if (initializer != null) {
/* 718 */         initializer.addListener(new InitializerListener() {
/*     */           public void reportPercent(final int percent) {
/* 720 */             Utils.execSWTThread(new AERunnable() {
/*     */               public void runSupport() {
/* 722 */                 if ((SBC_LibraryView.this.soWaitProgress != null) && (!SBC_LibraryView.this.soWaitProgress.isDisposed())) {
/* 723 */                   SBC_LibraryView.this.waitProgress = percent;
/* 724 */                   SBC_LibraryView.this.soWaitProgress.getControl().redraw();
/* 725 */                   SBC_LibraryView.this.soWaitProgress.getControl().update();
/*     */                 }
/*     */               }
/*     */             });
/* 729 */             if (percent > 100) {
/* 730 */               initializer.removeListener(this);
/*     */             }
/*     */           }
/*     */           
/*     */           public void reportCurrentTask(String currentTask) {
/* 735 */             if ((SBC_LibraryView.this.soWaitTask != null) && (!SBC_LibraryView.this.soWaitTask.isDisposed())) {
/* 736 */               SBC_LibraryView.this.soWaitTask.setText(currentTask);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/* 743 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(final AzureusCore core) {
/* 745 */         PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 746 */         final UIManager uim = pi.getUIManager();
/* 747 */         uim.addUIListener(new UIManagerListener()
/*     */         {
/*     */           public void UIDetached(UIInstance instance) {}
/*     */           
/*     */           public void UIAttached(UIInstance instance) {
/* 752 */             if ((instance instanceof UISWTInstance)) {
/* 753 */               uim.removeUIListener(this);
/* 754 */               Utils.execSWTThread(new AERunnable() {
/*     */                 public void runSupport() {
/* 756 */                   if (SBC_LibraryView.this.soWait != null) {
/* 757 */                     SBC_LibraryView.this.soWait.setVisible(false);
/*     */                   }
/* 759 */                   if (!SBC_LibraryView.8.this.val$skinObject.isDisposed())
/*     */                   {
/* 761 */                     SBC_LibraryView.this.setupView(SBC_LibraryView.8.1.this.val$core, SBC_LibraryView.8.this.val$skinObject);
/*     */                   }
/*     */                   
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 770 */     });
/* 771 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private void setupView(AzureusCore core, SWTSkinObject skinObject)
/*     */   {
/* 777 */     this.torrentFilter = skinObject.getSkinObjectID();
/* 778 */     if (this.torrentFilter.equalsIgnoreCase("LibraryDL")) {
/* 779 */       this.torrentFilterMode = 2;
/* 780 */     } else if (this.torrentFilter.equalsIgnoreCase("LibraryCD")) {
/* 781 */       this.torrentFilterMode = 1;
/* 782 */     } else if (this.torrentFilter.equalsIgnoreCase("LibraryUnopened")) {
/* 783 */       this.torrentFilterMode = 3;
/*     */     }
/*     */     
/* 786 */     if ((this.datasource instanceof Tag)) {
/* 787 */       Tag tag = (Tag)this.datasource;
/* 788 */       TagType tagType = tag.getTagType();
/* 789 */       if (tagType.getTagType() == 2) {
/* 790 */         int tagID = tag.getTagID();
/* 791 */         if ((tagID == 1) || (tagID == 3) || (tagID == 11)) {
/* 792 */           this.torrentFilterMode = 2;
/* 793 */         } else if ((tagID == 2) || (tagID == 4) || (tagID == 10)) {
/* 794 */           this.torrentFilterMode = 1;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 799 */     this.soListArea = getSkinObject("library-list-area");
/*     */     
/* 801 */     this.soListArea.getControl().setData("TorrentFilterMode", new Long(this.torrentFilterMode));
/*     */     
/* 803 */     this.soListArea.getControl().setData("DataSource", this.datasource);
/*     */     
/* 805 */     setViewMode(COConfigurationManager.getIntParameter(this.torrentFilter + ".viewmode"), false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 810 */     SWTSkinObject so = getSkinObject("library-list-button-smalltable");
/* 811 */     if (so != null) {
/* 812 */       this.btnSmallTable = new SWTSkinButtonUtility(so);
/* 813 */       this.btnSmallTable.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 816 */           SBC_LibraryView.this.setViewMode(1, true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 821 */     so = getSkinObject("library-list-button-bigtable");
/* 822 */     if (so != null) {
/* 823 */       this.btnBigTable = new SWTSkinButtonUtility(so);
/* 824 */       this.btnBigTable.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 827 */           SBC_LibraryView.this.setViewMode(0, true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 832 */     SB_Transfers.setupViewTitleWithCore(core);
/*     */   }
/*     */   
/*     */   private SWTSkinObjectText soWaitTask;
/*     */   private int waitProgress;
/*     */   private SWTSkinObjectText soLibraryInfo;
/*     */   private Object datasource;
/*     */   private MdiEntry currentEntry;
/* 840 */   public void refreshToolBarItems(Map<String, Long> list) { long stateSmall = 1L;
/* 841 */     long stateBig = 1L;
/* 842 */     if (this.viewMode == 0) {
/* 843 */       stateBig |= 0x2;
/*     */     } else {
/* 845 */       stateSmall |= 0x2;
/*     */     }
/* 847 */     list.put("modeSmall", Long.valueOf(stateSmall));
/* 848 */     list.put("modeBig", Long.valueOf(stateBig));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 856 */     String itemKey = item.getID();
/*     */     
/* 858 */     if ((itemKey.equals("modeSmall")) && 
/* 859 */       (isVisible())) {
/* 860 */       setViewMode(1, true);
/* 861 */       return true;
/*     */     }
/*     */     
/* 864 */     if ((itemKey.equals("modeBig")) && 
/* 865 */       (isVisible())) {
/* 866 */       setViewMode(0, true);
/* 867 */       return true;
/*     */     }
/*     */     
/* 870 */     return false;
/*     */   }
/*     */   
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 875 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params) {
/* 879 */     this.datasource = params;
/* 880 */     if (this.soListArea != null) {
/* 881 */       Control control = this.soListArea.getControl();
/*     */       
/* 883 */       if (!control.isDisposed())
/*     */       {
/* 885 */         control.setData("DataSource", params);
/*     */       }
/*     */     }
/*     */     
/* 889 */     return null;
/*     */   }
/*     */   
/*     */   public int getViewMode() {
/* 893 */     return this.viewMode;
/*     */   }
/*     */   
/*     */   protected void addHeaderInfoExtender(HeaderInfoExtender extender) {}
/*     */   
/*     */   protected void removeHeaderInfoExtender(HeaderInfoExtender extender) {}
/*     */   
/*     */   protected void refreshHeaderInfo() {}
/*     */   
/*     */   protected static abstract interface HeaderInfoExtender {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_LibraryView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */