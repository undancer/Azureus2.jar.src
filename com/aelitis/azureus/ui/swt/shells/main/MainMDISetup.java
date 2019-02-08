/*     */ package com.aelitis.azureus.ui.swt.shells.main;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*     */ import com.aelitis.azureus.plugins.net.buddy.swt.SBC_ChatOverview;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener2;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryDropListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_ActivityTableView;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_PlusFTUX;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_TorrentDetailsView.TorrentDetailMdiEntry;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SB_Discovery;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SB_Transfers;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SB_Vuze;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.FeatureUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationChecker;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistoryEvent;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistoryListener;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistoryManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostListener;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncController;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStubEvent;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStubListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManagerListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener2;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.views.ConfigShell;
/*     */ import org.gudy.azureus2.ui.swt.views.ConfigView;
/*     */ import org.gudy.azureus2.ui.swt.views.LoggerView;
/*     */ import org.gudy.azureus2.ui.swt.views.MySharesView;
/*     */ import org.gudy.azureus2.ui.swt.views.MyTrackerView;
/*     */ import org.gudy.azureus2.ui.swt.views.PeersSuperView;
/*     */ import org.gudy.azureus2.ui.swt.views.TorrentOptionsView;
/*     */ import org.gudy.azureus2.ui.swt.views.clientstats.ClientStatsView;
/*     */ import org.gudy.azureus2.ui.swt.views.stats.StatsView;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils.ArchiveCallback;
/*     */ 
/*     */ public class MainMDISetup
/*     */ {
/*     */   public static void setupSideBar(final MultipleDocumentInterfaceSWT mdi, final MdiListener l)
/*     */   {
/*  97 */     if (Utils.isAZ2UI()) {
/*  98 */       setupSidebarClassic(mdi);
/*     */     } else {
/* 100 */       setupSidebarVuzeUI(mdi);
/*     */     }
/*     */     
/* 103 */     SBC_TorrentDetailsView.TorrentDetailMdiEntry.register(mdi);
/*     */     
/* 105 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*     */     
/* 107 */     pi.getUIManager().addUIListener(new UIManagerListener2()
/*     */     {
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */ 
/*     */       public void UIAttached(UIInstance instance) {}
/*     */       
/*     */ 
/*     */       public void UIAttachedComplete(UIInstance instance)
/*     */       {
/* 117 */         PluginInitializer.getDefaultInterface().getUIManager().removeUIListener(this);
/*     */         
/*     */ 
/* 120 */         MdiEntry currentEntry = this.val$mdi.getCurrentEntry();
/* 121 */         if (currentEntry != null)
/*     */         {
/* 123 */           return;
/*     */         }
/*     */         
/* 126 */         String CFG_STARTTAB = "v3.StartTab";
/* 127 */         String CFG_STARTTAB_DS = "v3.StartTab.ds";
/*     */         
/* 129 */         String datasource = null;
/* 130 */         boolean showWelcome = COConfigurationManager.getBooleanParameter("v3.Show Welcome");
/* 131 */         if (ConfigurationChecker.isNewVersion()) {
/* 132 */           showWelcome = true;
/*     */         }
/*     */         
/* 135 */         ContentNetwork startupCN = ContentNetworkManagerFactory.getSingleton().getStartupContentNetwork();
/* 136 */         if ((startupCN == null) || (!startupCN.isServiceSupported(8)))
/* 137 */           showWelcome = false;
/*     */         String startTab;
/*     */         String startTab;
/* 140 */         if (showWelcome) {
/* 141 */           startTab = "Welcome";
/*     */         } else {
/* 143 */           if (!COConfigurationManager.hasParameter("v3.StartTab", true)) {
/* 144 */             COConfigurationManager.setParameter("v3.StartTab", "Library");
/*     */           }
/*     */           
/* 147 */           startTab = COConfigurationManager.getStringParameter("v3.StartTab");
/* 148 */           datasource = COConfigurationManager.getStringParameter("v3.StartTab.ds", null);
/*     */         }
/*     */         
/* 151 */         if (startTab.equals("Plus")) {
/* 152 */           SBC_PlusFTUX.setSourceRef("lastview");
/*     */         }
/* 154 */         if (!this.val$mdi.loadEntryByID(startTab, true, false, datasource)) {
/* 155 */           this.val$mdi.showEntryByID("Library");
/*     */         }
/* 157 */         if (l != null) {
/* 158 */           this.val$mdi.addListener(l);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 163 */     });
/* 164 */     COConfigurationManager.addAndFireParameterListener("Beta Programme Enabled", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/* 167 */         boolean enabled = COConfigurationManager.getBooleanParameter("Beta Programme Enabled");
/* 168 */         if (enabled) {
/* 169 */           this.val$mdi.loadEntryByID("BetaProgramme", false);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 174 */     });
/* 175 */     mdi.registerEntry(StatsView.VIEW_ID, new MdiEntryCreationListener() {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 177 */         MdiEntry entry = this.val$mdi.createEntryFromEventListener("header.plugins", new StatsView(), id, true, null, null);
/*     */         
/*     */ 
/* 180 */         return entry;
/*     */       }
/*     */       
/* 183 */     });
/* 184 */     mdi.registerEntry("AllPeersView", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 187 */         MdiEntry entry = this.val$mdi.createEntryFromEventListener("header.transfers", new PeersSuperView(), id, true, null, null);
/*     */         
/*     */ 
/* 190 */         entry.setImageLeftID("image.sidebar.allpeers");
/* 191 */         return entry;
/*     */       }
/*     */       
/* 194 */     });
/* 195 */     mdi.registerEntry("LoggerView", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 198 */         MdiEntry entry = this.val$mdi.createEntryFromEventListener("header.plugins", new LoggerView(), id, true, null, null);
/*     */         
/*     */ 
/* 201 */         return entry;
/*     */       }
/*     */       
/* 204 */     });
/* 205 */     mdi.registerEntry("TagsOverview", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 208 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.transfers", id, "tagsview", "{mdi.entry.tagsoverview}", null, null, true, null);
/*     */         
/*     */ 
/* 211 */         entry.setImageLeftID("image.sidebar.tag-overview");
/* 212 */         entry.setDefaultExpanded(true);
/* 213 */         return entry;
/*     */       }
/* 215 */     });
/* 216 */     mdi.registerEntry("TagDiscovery", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 219 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("TagsOverview", id, "tagdiscoveryview", "{mdi.entry.tagdiscovery}", null, null, true, null);
/*     */         
/*     */ 
/*     */ 
/* 223 */         entry.setImageLeftID("image.sidebar.tag-overview");
/* 224 */         return entry;
/*     */       }
/*     */       
/* 227 */     });
/* 228 */     mdi.registerEntry("ChatOverview", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id)
/*     */       {
/* 232 */         final ViewTitleInfo title_info = new ViewTitleInfo()
/*     */         {
/*     */ 
/*     */ 
/*     */           public Object getTitleInfoProperty(int propertyID)
/*     */           {
/*     */ 
/* 239 */             BuddyPluginBeta bp = com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils.getBetaPlugin();
/*     */             
/* 241 */             if (bp == null)
/*     */             {
/* 243 */               return null;
/*     */             }
/*     */             
/* 246 */             if (propertyID == 0)
/*     */             {
/* 248 */               int num = 0;
/*     */               
/* 250 */               for (BuddyPluginBeta.ChatInstance chat : bp.getChats())
/*     */               {
/* 252 */                 if (chat.getMessageOutstanding())
/*     */                 {
/* 254 */                   num++;
/*     */                 }
/*     */               }
/*     */               
/* 258 */               if (num > 0)
/*     */               {
/* 260 */                 return String.valueOf(num);
/*     */               }
/*     */               
/*     */ 
/* 264 */               return null;
/*     */             }
/* 266 */             if (propertyID == 8)
/*     */             {
/* 268 */               for (BuddyPluginBeta.ChatInstance chat : bp.getChats())
/*     */               {
/* 270 */                 if (chat.getMessageOutstanding())
/*     */                 {
/* 272 */                   if (chat.hasUnseenMessageWithNick())
/*     */                   {
/* 274 */                     return SBC_ChatOverview.COLOR_MESSAGE_WITH_NICK;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 280 */             return null;
/*     */           }
/*     */           
/* 283 */         };
/* 284 */         final TimerEventPeriodic timer = SimpleTimer.addPeriodicEvent("sb:chatup", 5000L, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 295 */             ViewTitleInfoManager.refreshTitleInfo(title_info);
/*     */           }
/*     */           
/* 298 */         });
/* 299 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.discovery", "ChatOverview", "chatsview", "{mdi.entry.chatsoverview}", title_info, null, true, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 304 */         entry.setImageLeftID("image.sidebar.chat-overview");
/*     */         
/* 306 */         entry.addListener(new MdiCloseListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void mdiEntryClosed(MdiEntry entry, boolean userClosed)
/*     */           {
/*     */ 
/* 313 */             timer.cancel();
/*     */           }
/*     */           
/* 316 */         });
/* 317 */         return entry;
/*     */       }
/*     */       
/* 320 */     });
/* 321 */     mdi.registerEntry("ArchivedDownloads", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id)
/*     */       {
/* 325 */         final DownloadManager download_manager = PluginInitializer.getDefaultInterface().getDownloadManager();
/*     */         
/* 327 */         final ViewTitleInfo title_info = new ViewTitleInfo()
/*     */         {
/*     */ 
/*     */ 
/*     */           public Object getTitleInfoProperty(int propertyID)
/*     */           {
/*     */ 
/* 334 */             if (propertyID == 0)
/*     */             {
/* 336 */               int num = download_manager.getDownloadStubCount();
/*     */               
/* 338 */               return String.valueOf(num);
/*     */             }
/*     */             
/* 341 */             return null;
/*     */           }
/*     */           
/* 344 */         };
/* 345 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.transfers", "ArchivedDownloads", "archivedlsview", "{mdi.entry.archiveddownloadsview}", title_info, null, true, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 351 */         entry.setImageLeftID("image.sidebar.archive");
/*     */         
/* 353 */         final DownloadStubListener stub_listener = new DownloadStubListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void downloadStubEventOccurred(DownloadStubEvent event)
/*     */           {
/*     */ 
/* 360 */             ViewTitleInfoManager.refreshTitleInfo(title_info);
/*     */           }
/*     */           
/* 363 */         };
/* 364 */         download_manager.addDownloadStubListener(stub_listener, false);
/*     */         
/* 366 */         entry.addListener(new MdiCloseListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void mdiEntryClosed(MdiEntry entry, boolean userClosed)
/*     */           {
/*     */ 
/* 373 */             download_manager.removeDownloadStubListener(stub_listener);
/*     */           }
/*     */           
/* 376 */         });
/* 377 */         entry.addListener(new MdiEntryDropListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public boolean mdiEntryDrop(MdiEntry entry, Object data)
/*     */           {
/*     */ 
/*     */ 
/* 385 */             if ((data instanceof String))
/*     */             {
/* 387 */               String str = (String)data;
/*     */               
/* 389 */               if (str.startsWith("DownloadManager\n"))
/*     */               {
/* 391 */                 String[] bits = str.split("\n");
/*     */                 
/* 393 */                 DownloadManager dm = PluginInitializer.getDefaultInterface().getDownloadManager();
/*     */                 
/* 395 */                 List<Download> downloads = new ArrayList();
/*     */                 
/* 397 */                 boolean failed = false;
/*     */                 
/* 399 */                 for (int i = 1; i < bits.length; i++)
/*     */                 {
/* 401 */                   byte[] hash = Base32.decode(bits[i]);
/*     */                   try
/*     */                   {
/* 404 */                     Download download = dm.getDownload(hash);
/*     */                     
/* 406 */                     if (download.canStubbify())
/*     */                     {
/* 408 */                       downloads.add(download);
/*     */                     }
/*     */                     else
/*     */                     {
/* 412 */                       failed = true;
/*     */                     }
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */                 
/* 418 */                 final boolean f_failed = failed;
/*     */                 
/* 420 */                 ManagerUtils.moveToArchive(downloads, new ManagerUtils.ArchiveCallback()
/*     */                 {
/*     */ 
/*     */ 
/* 424 */                   boolean error = f_failed;
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */                   public void failed(DownloadStub original, Throwable e)
/*     */                   {
/* 431 */                     this.error = true;
/*     */                   }
/*     */                   
/*     */ 
/*     */                   public void completed()
/*     */                   {
/* 437 */                     if (this.error)
/*     */                     {
/* 439 */                       String title = MessageText.getString("archive.failed.title");
/* 440 */                       String text = MessageText.getString("archive.failed.text");
/*     */                       
/* 442 */                       MessageBoxShell prompter = new MessageBoxShell(title, text, new String[] { MessageText.getString("Button.ok") }, 0);
/*     */                       
/*     */ 
/*     */ 
/*     */ 
/* 447 */                       prompter.setAutoCloseInMS(0);
/*     */                       
/* 449 */                       prompter.open(null);
/*     */                     }
/*     */                   }
/*     */                 });
/*     */               }
/*     */               
/* 455 */               return true;
/*     */             }
/*     */             
/* 458 */             return false;
/*     */           }
/*     */           
/* 461 */         });
/* 462 */         return entry;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 467 */     });
/* 468 */     mdi.registerEntry("DownloadHistory", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id)
/*     */       {
/* 472 */         final DownloadHistoryManager history_manager = (DownloadHistoryManager)AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadHistoryManager();
/*     */         
/* 474 */         final ViewTitleInfo title_info = new ViewTitleInfo()
/*     */         {
/*     */ 
/*     */ 
/*     */           public Object getTitleInfoProperty(int propertyID)
/*     */           {
/*     */ 
/* 481 */             if (propertyID == 0)
/*     */             {
/* 483 */               if (history_manager == null)
/*     */               {
/* 485 */                 return null;
/*     */               }
/* 487 */               if (history_manager.isEnabled())
/*     */               {
/* 489 */                 int num = history_manager.getHistoryCount();
/*     */                 
/* 491 */                 return String.valueOf(num);
/*     */               }
/*     */               
/*     */ 
/* 495 */               return MessageText.getString("pairing.status.disabled");
/*     */             }
/*     */             
/*     */ 
/* 499 */             return null;
/*     */           }
/*     */           
/* 502 */         };
/* 503 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.transfers", "DownloadHistory", "downloadhistoryview", "{mdi.entry.downloadhistoryview}", title_info, null, true, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 509 */         entry.setImageLeftID("image.sidebar.logview");
/*     */         
/* 511 */         if (history_manager != null)
/*     */         {
/* 513 */           final DownloadHistoryListener history_listener = new DownloadHistoryListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void downloadHistoryEventOccurred(DownloadHistoryEvent event)
/*     */             {
/*     */ 
/* 520 */               ViewTitleInfoManager.refreshTitleInfo(title_info);
/*     */             }
/*     */             
/* 523 */           };
/* 524 */           history_manager.addListener(history_listener, false);
/*     */           
/* 526 */           entry.addListener(new MdiCloseListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void mdiEntryClosed(MdiEntry entry, boolean userClosed)
/*     */             {
/*     */ 
/* 533 */               history_manager.removeListener(history_listener);
/*     */             }
/*     */           });
/*     */         }
/*     */         
/* 538 */         return entry;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 543 */     });
/* 544 */     mdi.registerEntry("TorrentOptionsView", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 547 */         MdiEntry entry = this.val$mdi.createEntryFromEventListener("header.transfers", TorrentOptionsView.class, "TorrentOptionsView", true, null, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 553 */         entry.setImageLeftID("image.sidebar.torrentoptions");
/*     */         
/* 555 */         return entry;
/*     */       }
/*     */       
/* 558 */     });
/* 559 */     mdi.registerEntry("MySharesView", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 562 */         MdiEntry entry = this.val$mdi.createEntryFromEventListener("header.transfers", MySharesView.class, "MySharesView", true, null, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 568 */         entry.setImageLeftID("image.sidebar.myshares");
/*     */         
/* 570 */         return entry;
/*     */       }
/*     */       
/* 573 */     });
/* 574 */     mdi.registerEntry("MyTrackerView", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 577 */         MdiEntry entry = this.val$mdi.createEntryFromEventListener("header.transfers", MyTrackerView.class, "MyTrackerView", true, null, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 583 */         entry.setImageLeftID("image.sidebar.mytracker");
/*     */         
/* 585 */         return entry;
/*     */       }
/*     */       
/* 588 */     });
/* 589 */     mdi.registerEntry("ClientStatsView", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 592 */         MdiEntry entry = this.val$mdi.createEntryFromEventListener("header.plugins", ClientStatsView.class, "ClientStatsView", true, null, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 598 */         entry.setImageLeftID("image.sidebar.clientstats");
/*     */         
/* 600 */         return entry;
/*     */       }
/*     */       
/* 603 */     });
/* 604 */     mdi.registerEntry("ConfigView", new MdiEntryCreationListener2()
/*     */     {
/*     */ 
/*     */       public MdiEntry createMDiEntry(MultipleDocumentInterface mdi, String id, Object datasource, Map<?, ?> params)
/*     */       {
/*     */ 
/* 610 */         String section = (datasource instanceof String) ? (String)datasource : null;
/*     */         
/*     */ 
/* 613 */         boolean uiClassic = COConfigurationManager.getStringParameter("ui").equals("az2");
/*     */         
/* 615 */         if ((uiClassic) || (COConfigurationManager.getBooleanParameter("Show Options In Side Bar")))
/*     */         {
/* 617 */           MdiEntry entry = ((MultipleDocumentInterfaceSWT)mdi).createEntryFromEventListener("header.plugins", ConfigView.class, "ConfigView", true, null, null);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 623 */           entry.setImageLeftID("image.sidebar.config");
/*     */           
/* 625 */           return entry;
/*     */         }
/*     */         
/* 628 */         ConfigShell.getInstance().open(section);
/* 629 */         return null;
/*     */       }
/*     */     });
/*     */     try
/*     */     {
/* 634 */       ShareManager share_manager = pi.getShareManager();
/* 635 */       if (share_manager.getShares().length > 0) {
/* 636 */         mdi.showEntryByID("MySharesView");
/*     */       } else {
/* 638 */         share_manager.addListener(new ShareManagerListener()
/*     */         {
/*     */           public void resourceModified(ShareResource old_resource, ShareResource new_resource) {}
/*     */           
/*     */ 
/*     */           public void resourceDeleted(ShareResource resource) {}
/*     */           
/*     */ 
/*     */           public void resourceAdded(ShareResource resource)
/*     */           {
/* 648 */             this.val$share_manager.removeListener(this);
/* 649 */             mdi.loadEntryByID("MySharesView", false);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           public void reportProgress(int percent_complete) {}
/*     */           
/*     */ 
/*     */ 
/*     */           public void reportCurrentTask(String task_description) {}
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {}
/*     */     
/*     */ 
/* 666 */     TRHost trackerHost = AzureusCoreFactory.getSingleton().getTrackerHost();
/* 667 */     trackerHost.addListener(new TRHostListener() {
/* 668 */       boolean done = false;
/*     */       
/*     */ 
/*     */       public void torrentRemoved(TRHostTorrent t) {}
/*     */       
/*     */       public void torrentChanged(TRHostTorrent t) {}
/*     */       
/*     */       public void torrentAdded(TRHostTorrent t)
/*     */       {
/* 677 */         if (this.done) {
/* 678 */           return;
/*     */         }
/* 680 */         TRHost trackerHost = AzureusCoreFactory.getSingleton().getTrackerHost();
/* 681 */         trackerHost.removeListener(this);
/* 682 */         this.done = true;
/* 683 */         this.val$mdi.loadEntryByID("MyTrackerView", false);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean handleExternalRequest(InetSocketAddress client_address, String user, String url, URL absolute_url, String header, InputStream is, OutputStream os, AsyncController async)
/*     */         throws IOException
/*     */       {
/* 691 */         return false;
/*     */       }
/*     */       
/* 694 */     });
/* 695 */     UIManager uim = pi.getUIManager();
/* 696 */     if (uim != null) {
/* 697 */       MenuItem menuItem = uim.getMenuManager().addMenuItem("mainmenu", "tags.view.heading");
/*     */       
/* 699 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 701 */           UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("TagsOverview");
/*     */         }
/*     */         
/*     */ 
/* 705 */       });
/* 706 */       menuItem = uim.getMenuManager().addMenuItem("mainmenu", "tag.discovery.view.heading");
/*     */       
/* 708 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 710 */           UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("TagDiscovery");
/*     */         }
/*     */         
/*     */ 
/* 714 */       });
/* 715 */       menuItem = uim.getMenuManager().addMenuItem("mainmenu", "chats.view.heading");
/*     */       
/* 717 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 719 */           UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("ChatOverview");
/*     */         }
/*     */         
/*     */ 
/* 723 */       });
/* 724 */       menuItem = uim.getMenuManager().addMenuItem("mainmenu", "archivedlsview.view.heading");
/*     */       
/* 726 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 728 */           UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("ArchivedDownloads");
/*     */         }
/*     */         
/*     */ 
/* 732 */       });
/* 733 */       menuItem = uim.getMenuManager().addMenuItem("mainmenu", "downloadhistoryview.view.heading");
/*     */       
/* 735 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 737 */           UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("DownloadHistory");
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setupSidebarClassic(MultipleDocumentInterfaceSWT mdi)
/*     */   {
/* 749 */     mdi.registerEntry("Library", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id)
/*     */       {
/* 753 */         boolean uiClassic = COConfigurationManager.getStringParameter("ui").equals("az2");
/*     */         
/* 755 */         String title = uiClassic ? "{MyTorrentsView.mytorrents}" : "{sidebar.Library}";
/*     */         
/*     */ 
/* 758 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef(null, "Library", "library", title, null, null, false, "");
/*     */         
/*     */ 
/*     */ 
/* 762 */         entry.setImageLeftID("image.sidebar.library");
/* 763 */         return entry;
/*     */       }
/*     */       
/* 766 */     });
/* 767 */     mdi.registerEntry("Tag\\..*", new MdiEntryCreationListener2()
/*     */     {
/*     */ 
/*     */       public MdiEntry createMDiEntry(MultipleDocumentInterface mdi, String id, Object datasource, Map<?, ?> params)
/*     */       {
/* 772 */         if ((datasource instanceof Tag)) {
/* 773 */           Tag tag = (Tag)datasource;
/*     */           
/* 775 */           return SB_Transfers.setupTag(tag);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 782 */           TagManager tm = TagManagerFactory.getTagManager();
/*     */           
/* 784 */           String[] bits = id.split("\\.");
/*     */           
/* 786 */           int tag_type = Integer.parseInt(bits[1]);
/* 787 */           int tag_id = Integer.parseInt(bits[2]);
/*     */           
/* 789 */           Tag tag = tm.getTagType(tag_type).getTag(tag_id);
/*     */           
/* 791 */           if (tag != null)
/*     */           {
/* 793 */             return SB_Transfers.setupTag(tag);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/*     */ 
/* 800 */         return null;
/*     */       }
/*     */       
/* 803 */     });
/* 804 */     SBC_ActivityTableView.setupSidebarEntry(mdi);
/*     */     
/* 806 */     mdi.showEntryByID("Library");
/*     */   }
/*     */   
/*     */ 
/*     */   private static void setupSidebarVuzeUI(MultipleDocumentInterfaceSWT mdi)
/*     */   {
/* 812 */     String[] preferredOrder = { "header.transfers", "header.vuze", "header.discovery", "header.devices", "header.dvd", "header.plugins" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 820 */     mdi.setPreferredOrder(preferredOrder);
/*     */     
/* 822 */     for (int i = 0; i < preferredOrder.length; i++) {
/* 823 */       String id = preferredOrder[i];
/* 824 */       mdi.registerEntry(id, new MdiEntryCreationListener() {
/*     */         public MdiEntry createMDiEntry(String id) {
/* 826 */           MdiEntry entry = this.val$mdi.createHeader(id, "sidebar." + id, null);
/*     */           
/* 828 */           if (entry == null)
/*     */           {
/* 830 */             return null;
/*     */           }
/*     */           
/* 833 */           entry.setDefaultExpanded(true);
/*     */           
/* 835 */           if (id.equals("header.plugins")) {
/* 836 */             entry.addListener(new com.aelitis.azureus.ui.mdi.MdiChildCloseListener()
/*     */             {
/*     */               public void mdiChildEntryClosed(MdiEntry parent, MdiEntry child, boolean user) {
/* 839 */                 if (MainMDISetup.25.this.val$mdi.getChildrenOf(parent.getId()).size() == 0) {
/* 840 */                   parent.close(true);
/*     */                 }
/*     */                 
/*     */               }
/* 844 */             });
/* 845 */             PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 846 */             UIManager uim = pi.getUIManager();
/* 847 */             MenuManager menuManager = uim.getMenuManager();
/*     */             
/*     */ 
/* 850 */             MenuItem menuItem = menuManager.addMenuItem("sidebar.header.plugins", "label.plugin.options");
/*     */             
/*     */ 
/*     */ 
/* 854 */             menuItem.addListener(new MenuItemListener() {
/*     */               public void selected(MenuItem menu, Object target) {
/* 856 */                 UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                 
/* 858 */                 if (uif != null) {
/* 859 */                   uif.getMDI().showEntryByID("ConfigView", "plugins");
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 867 */           return entry;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 895 */     mdi.registerEntry("About.Plugins", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 898 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.plugins", "About.Plugins", "main.generic.browse", "{mdi.entry.about.plugins}", null, null, true, "");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 903 */         String url = ConstantsVuze.getDefaultContentNetwork().getSiteRelativeURL("plugins", true);
/*     */         
/* 905 */         entry.setDatasource(url);
/* 906 */         entry.setImageLeftID("image.sidebar.plugin");
/* 907 */         return entry;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 912 */     });
/* 913 */     final int burnInfoShown = COConfigurationManager.getIntParameter("burninfo.shown", 0);
/*     */     
/* 915 */     if (burnInfoShown == 0) {
/* 916 */       AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */         public void azureusCoreRunning(AzureusCore core) {
/* 918 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/* 920 */               if (FeatureManagerUI.enabled)
/*     */               {
/*     */ 
/* 923 */                 MainMDISetup.27.this.val$mdi.loadEntryByID("Plus", false);
/*     */                 
/*     */ 
/* 926 */                 if (!FeatureUtils.hasFullBurn()) {
/* 927 */                   MainMDISetup.27.this.val$mdi.loadEntryByID("BurnInfo", false);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 932 */                 COConfigurationManager.setParameter("burninfo.shown", MainMDISetup.27.this.val$burnInfoShown + 1);
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/* 941 */     SB_Transfers.setup(mdi);
/* 942 */     new SB_Vuze(mdi);
/* 943 */     new SB_Discovery(mdi);
/*     */     
/* 945 */     mdi.loadEntryByID("Library", false);
/* 946 */     mdi.loadEntryByID("LibraryUnopened", false);
/*     */     
/* 948 */     mdi.loadEntryByID("Subscriptions", false);
/*     */     
/* 950 */     mdi.loadEntryByID("Devices", false);
/* 951 */     mdi.loadEntryByID("Activity", false);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/MainMDISetup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */