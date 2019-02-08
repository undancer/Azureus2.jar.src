/*      */ package com.aelitis.azureus.ui.swt.views.skin;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagListener;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagManagerListener;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.TagTypeListener;
/*      */ import com.aelitis.azureus.core.tag.TagTypeListener.TagEvent;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryDropListener;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MdiSWTMenuHackListener;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBarEntrySWT;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.TreeMap;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryListener;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.category.CategoryManagerListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerAdapter;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.PeersGeneralView;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.CategoryUIUtils;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
/*      */ 
/*      */ public class SB_Transfers
/*      */ {
/*   75 */   private static final Object AUTO_CLOSE_KEY = new Object();
/*   76 */   private static final Object TAG_DATA_KEY = new Object();
/*      */   private static final String ID_VITALITY_ACTIVE = "image.sidebar.vitality.dl";
/*      */   private static final String ID_VITALITY_ALERT = "image.sidebar.vitality.alert";
/*      */   
/*      */   protected static abstract interface countRefreshListener {
/*      */     public abstract void countRefreshed(SB_Transfers.stats paramstats1, SB_Transfers.stats paramstats2);
/*      */   }
/*      */   
/*   84 */   public static class stats { int numSeeding = 0;
/*      */     
/*   86 */     int numDownloading = 0;
/*      */     
/*   88 */     int numQueued = 0;
/*      */     
/*   90 */     int numComplete = 0;
/*      */     
/*   92 */     int numIncomplete = 0;
/*      */     
/*   94 */     int numErrorComplete = 0;
/*      */     
/*      */     String errorInCompleteTooltip;
/*      */     
/*   98 */     int numErrorInComplete = 0;
/*      */     
/*      */     String errorCompleteTooltip;
/*      */     
/*  102 */     int numUnOpened = 0;
/*      */     
/*  104 */     int numStoppedIncomplete = 0;
/*      */     
/*      */     boolean includeLowNoise;
/*      */     
/*  108 */     long newestIncompleteDownloadTime = 0L;
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean sameAs(stats other)
/*      */     {
/*  114 */       return (this.numSeeding == other.numSeeding) && (this.numDownloading == other.numDownloading) && (this.numQueued == other.numQueued) && (this.numComplete == other.numComplete) && (this.numIncomplete == other.numIncomplete) && (this.numErrorComplete == other.numErrorComplete) && (this.numErrorInComplete == other.numErrorInComplete) && (this.numUnOpened == other.numUnOpened) && (this.numStoppedIncomplete == other.numStoppedIncomplete) && (this.newestIncompleteDownloadTime == other.newestIncompleteDownloadTime);
/*      */     }
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
/*      */     private void copyFrom(stats other)
/*      */     {
/*  131 */       this.numSeeding = other.numSeeding;
/*  132 */       this.numDownloading = other.numDownloading;
/*  133 */       this.numQueued = other.numQueued;
/*  134 */       this.numComplete = other.numComplete;
/*  135 */       this.numIncomplete = other.numIncomplete;
/*  136 */       this.numErrorComplete = other.numErrorComplete;
/*  137 */       this.errorInCompleteTooltip = other.errorInCompleteTooltip;
/*  138 */       this.numErrorInComplete = other.numErrorInComplete;
/*  139 */       this.errorCompleteTooltip = other.errorCompleteTooltip;
/*  140 */       this.numUnOpened = other.numUnOpened;
/*  141 */       this.numStoppedIncomplete = other.numStoppedIncomplete;
/*  142 */       this.includeLowNoise = other.includeLowNoise;
/*  143 */       this.newestIncompleteDownloadTime = other.newestIncompleteDownloadTime;
/*      */     }
/*      */   }
/*      */   
/*  147 */   private static Object statsLock = new Object();
/*      */   
/*  149 */   private static stats statsWithLowNoise = new stats();
/*      */   
/*  151 */   private static stats statsNoLowNoise = new stats();
/*      */   
/*  153 */   private static CopyOnWriteList<countRefreshListener> listeners = new CopyOnWriteList();
/*      */   
/*  155 */   private static boolean first = true;
/*      */   
/*      */   private static AzureusCore core;
/*      */   
/*      */   private static long coreCreateTime;
/*      */   
/*      */   private static Object tag_setup_lock;
/*      */   
/*      */   private static FrequencyLimitedDispatcher refresh_limiter;
/*      */   
/*      */   public static void setup(MultipleDocumentInterface mdi)
/*      */   {
/*  167 */     MdiEntryCreationListener libraryCreator = new MdiEntryCreationListener() {
/*      */       public MdiEntry createMDiEntry(String id) {
/*  169 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.transfers", "Library", "library", "{sidebar.Library}", null, null, false, "");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  174 */         entry.setImageLeftID("image.sidebar.library");
/*  175 */         return entry;
/*      */       }
/*  177 */     };
/*  178 */     mdi.registerEntry("Library", libraryCreator);
/*  179 */     mdi.registerEntry("library", libraryCreator);
/*  180 */     mdi.registerEntry("minilibrary", libraryCreator);
/*      */     
/*  182 */     mdi.registerEntry("LibraryDL", new MdiEntryCreationListener()
/*      */     {
/*      */       public MdiEntry createMDiEntry(String id) {
/*  185 */         return SB_Transfers.createDownloadingEntry(this.val$mdi);
/*      */       }
/*      */       
/*  188 */     });
/*  189 */     mdi.registerEntry("LibraryCD", new MdiEntryCreationListener()
/*      */     {
/*      */       public MdiEntry createMDiEntry(String id) {
/*  192 */         return SB_Transfers.createSeedingEntry(this.val$mdi);
/*      */       }
/*      */       
/*  195 */     });
/*  196 */     mdi.registerEntry("LibraryUnopened", new MdiEntryCreationListener()
/*      */     {
/*      */       public MdiEntry createMDiEntry(String id) {
/*  199 */         return SB_Transfers.createUnopenedEntry(this.val$mdi);
/*      */       }
/*      */       
/*      */ 
/*  203 */     });
/*  204 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  206 */         SB_Transfers.setupViewTitleWithCore(core);
/*      */       }
/*      */       
/*  209 */     });
/*  210 */     PlatformTorrentUtils.addHasBeenOpenedListener(new com.aelitis.azureus.core.torrent.HasBeenOpenedListener() {
/*      */       public void hasBeenOpenedChanged(DownloadManager dm, boolean opened) {
/*  212 */         SB_Transfers.access$000();
/*  213 */         SB_Transfers.access$100();
/*      */       }
/*      */       
/*  216 */     });
/*  217 */     addMenuUnwatched("Library");
/*      */     
/*  219 */     mdi.addListener(new com.aelitis.azureus.ui.mdi.MdiEntryLoadedListener() {
/*      */       public void mdiEntryLoaded(MdiEntry entry) {
/*  221 */         if ("header.transfers".equals(entry.getId())) {
/*  222 */           SB_Transfers.addHeaderMenu();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected static void addHeaderMenu() {
/*  229 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*  230 */     UIManager uim = pi.getUIManager();
/*      */     
/*  232 */     MenuManager menuManager = uim.getMenuManager();
/*      */     
/*      */ 
/*      */ 
/*  236 */     MenuItem menuItem = menuManager.addMenuItem("sidebar.header.transfers", "MyTorrentsView.menu.setCategory.add");
/*      */     
/*      */ 
/*  239 */     menuItem.addListener(new MenuItemListener() {
/*      */       public void selected(MenuItem menu, Object target) {
/*  241 */         new org.gudy.azureus2.ui.swt.CategoryAdderWindow(null);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  246 */     });
/*  247 */     menuItem.addFillListener(new MenuItemFillListener() {
/*      */       public void menuWillBeShown(MenuItem menu, Object data) {
/*  249 */         menu.setVisible(COConfigurationManager.getBooleanParameter("Library.CatInSideBar"));
/*      */       }
/*      */       
/*  252 */     });
/*  253 */     menuItem = menuManager.addMenuItem("sidebar.header.transfers", "ConfigView.section.style.CatInSidebar");
/*      */     
/*      */ 
/*  256 */     menuItem.setStyle(2);
/*  257 */     menuItem.addListener(new MenuItemListener() {
/*      */       public void selected(MenuItem menu, Object target) {
/*  259 */         boolean b = COConfigurationManager.getBooleanParameter("Library.CatInSideBar");
/*  260 */         COConfigurationManager.setParameter("Library.CatInSideBar", !b);
/*      */       }
/*  262 */     });
/*  263 */     menuItem.addFillListener(new MenuItemFillListener() {
/*      */       public void menuWillBeShown(MenuItem menu, Object data) {
/*  265 */         menu.setVisible(CategoryManager.getCategories().length > 0);
/*  266 */         menu.setData(Boolean.valueOf(COConfigurationManager.getBooleanParameter("Library.CatInSideBar")));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  271 */     });
/*  272 */     TagUIUtils.setupSideBarMenus(menuManager);
/*      */   }
/*      */   
/*      */   protected static MdiEntry createUnopenedEntry(MultipleDocumentInterface mdi) {
/*  276 */     MdiEntry infoLibraryUn = mdi.createEntryFromSkinRef("header.transfers", "LibraryUnopened", "library", "{sidebar.LibraryUnopened}", null, null, false, "Library");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  281 */     infoLibraryUn.setImageLeftID("image.sidebar.unopened");
/*      */     
/*  283 */     addMenuUnwatched("LibraryUnopened");
/*  284 */     infoLibraryUn.setViewTitleInfo(new ViewTitleInfo() {
/*      */       public Object getTitleInfoProperty(int propertyID) {
/*  286 */         if ((propertyID == 0) && (SB_Transfers.statsNoLowNoise.numUnOpened > 0))
/*      */         {
/*  288 */           return "" + SB_Transfers.statsNoLowNoise.numUnOpened;
/*      */         }
/*  290 */         return null;
/*      */       }
/*  292 */     });
/*  293 */     return infoLibraryUn;
/*      */   }
/*      */   
/*      */   private static void addMenuUnwatched(String id) {
/*  297 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*  298 */     UIManager uim = pi.getUIManager();
/*  299 */     MenuManager menuManager = uim.getMenuManager();
/*      */     
/*  301 */     MenuItem menuItem = menuManager.addMenuItem("sidebar." + id, "v3.activity.button.watchall");
/*      */     
/*  303 */     menuItem.addListener(new MenuItemListener() {
/*      */       public void selected(MenuItem menu, Object target) {
/*  305 */         CoreWaiterSWT.waitForCore(org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread.ANY_THREAD, new AzureusCoreRunningListener()
/*      */         {
/*      */           public void azureusCoreRunning(AzureusCore core) {
/*  308 */             GlobalManager gm = core.getGlobalManager();
/*  309 */             List<?> downloadManagers = gm.getDownloadManagers();
/*  310 */             for (Iterator<?> iter = downloadManagers.iterator(); iter.hasNext();) {
/*  311 */               DownloadManager dm = (DownloadManager)iter.next();
/*      */               
/*  313 */               if ((!PlatformTorrentUtils.getHasBeenOpened(dm)) && (dm.getAssumedComplete()))
/*      */               {
/*  315 */                 PlatformTorrentUtils.setHasBeenOpened(dm, true);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static MdiEntry createSeedingEntry(MultipleDocumentInterface mdi)
/*      */   {
/*  331 */     ViewTitleInfo titleInfoSeeding = new ViewTitleInfo() {
/*      */       public Object getTitleInfoProperty(int propertyID) {
/*  333 */         if (propertyID == 0) {
/*  334 */           return null;
/*      */         }
/*      */         
/*  337 */         if (propertyID == 1) {
/*  338 */           return MessageText.getString("sidebar.LibraryCD.tooltip", new String[] { "" + SB_Transfers.statsNoLowNoise.numComplete, "" + SB_Transfers.statsNoLowNoise.numSeeding });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  344 */         return null;
/*      */       }
/*      */       
/*  347 */     };
/*  348 */     MdiEntry entry = mdi.createEntryFromSkinRef("header.transfers", "LibraryDL", "library", "{sidebar.LibraryDL}", titleInfoSeeding, null, false, null);
/*      */     
/*      */ 
/*      */ 
/*  352 */     entry.setImageLeftID("image.sidebar.downloading");
/*      */     
/*  354 */     MdiEntryVitalityImage vitalityImage = entry.addVitalityImage("image.sidebar.vitality.alert");
/*  355 */     vitalityImage.setVisible(false);
/*      */     
/*  357 */     entry.setViewTitleInfo(titleInfoSeeding);
/*      */     
/*  359 */     return entry;
/*      */   }
/*      */   
/*      */   protected static MdiEntry createDownloadingEntry(MultipleDocumentInterface mdi) {
/*  363 */     MdiEntry[] entry_holder = { null };
/*      */     
/*  365 */     ViewTitleInfo titleInfoDownloading = new ViewTitleInfo() {
/*      */       private long max_incomp_dl_time;
/*      */       
/*  368 */       public Object getTitleInfoProperty(int propertyID) { if (propertyID == 0) {
/*  369 */           if (COConfigurationManager.getBooleanParameter("Request Attention On New Download"))
/*      */           {
/*  371 */             if (SB_Transfers.coreCreateTime > 0L)
/*      */             {
/*  373 */               if (this.max_incomp_dl_time == 0L)
/*      */               {
/*  375 */                 this.max_incomp_dl_time = SB_Transfers.coreCreateTime;
/*      */               }
/*      */               
/*  378 */               if (SB_Transfers.statsNoLowNoise.newestIncompleteDownloadTime > this.max_incomp_dl_time)
/*      */               {
/*  380 */                 MdiEntry entry = this.val$entry_holder[0];
/*      */                 
/*  382 */                 if (entry != null)
/*      */                 {
/*  384 */                   this.max_incomp_dl_time = SB_Transfers.statsNoLowNoise.newestIncompleteDownloadTime;
/*      */                   
/*  386 */                   entry.requestAttention();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  392 */           int current = SB_Transfers.statsNoLowNoise.numIncomplete;
/*      */           
/*  394 */           if (current > 0) {
/*  395 */             return current + "";
/*      */           }
/*      */         }
/*  398 */         if (propertyID == 1) {
/*  399 */           return MessageText.getString("sidebar.LibraryDL.tooltip", new String[] { "" + SB_Transfers.statsNoLowNoise.numIncomplete, "" + SB_Transfers.statsNoLowNoise.numDownloading });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  406 */         return null;
/*      */       }
/*  408 */     };
/*  409 */     MdiEntry entry = mdi.createEntryFromSkinRef("header.transfers", "LibraryDL", "library", "{sidebar.LibraryDL}", titleInfoDownloading, null, false, null);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  414 */     entry_holder[0] = entry;
/*      */     
/*  416 */     entry.setImageLeftID("image.sidebar.downloading");
/*      */     
/*  418 */     MdiEntryVitalityImage vitalityImage = entry.addVitalityImage("image.sidebar.vitality.dl");
/*  419 */     vitalityImage.setVisible(false);
/*      */     
/*  421 */     vitalityImage = entry.addVitalityImage("image.sidebar.vitality.alert");
/*  422 */     vitalityImage.setVisible(false);
/*      */     
/*  424 */     return entry;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static void setupViewTitleWithCore(AzureusCore _core)
/*      */   {
/*  430 */     synchronized (SB_Transfers.class) {
/*  431 */       if (!first) {
/*  432 */         return;
/*      */       }
/*  434 */       first = false;
/*      */       
/*  436 */       core = _core;
/*  437 */       coreCreateTime = core.getCreateTime();
/*      */     }
/*      */     
/*  440 */     CategoryListener categoryListener = new CategoryListener()
/*      */     {
/*      */       public void downloadManagerRemoved(Category cat, DownloadManager removed) {
/*  443 */         SB_Transfers.RefreshCategorySideBar(cat);
/*      */       }
/*      */       
/*      */       public void downloadManagerAdded(Category cat, DownloadManager manager) {
/*  447 */         SB_Transfers.RefreshCategorySideBar(cat);
/*      */       }
/*      */       
/*  450 */     };
/*  451 */     COConfigurationManager.addAndFireParameterListener("Library.CatInSideBar", new ParameterListener()
/*      */     {
/*      */       private CategoryManagerListener categoryManagerListener;
/*      */       
/*      */       public void parameterChanged(String parameterName) {
/*  456 */         if (Utils.isAZ2UI()) {
/*  457 */           return;
/*      */         }
/*      */         
/*  460 */         Category[] categories = CategoryManager.getCategories();
/*  461 */         if (categories.length == 0) {
/*  462 */           return;
/*      */         }
/*      */         
/*  465 */         boolean catInSidebar = COConfigurationManager.getBooleanParameter("Library.CatInSideBar");
/*  466 */         if (catInSidebar) {
/*  467 */           if (this.categoryManagerListener != null) {
/*  468 */             return;
/*      */           }
/*      */           
/*  471 */           this.categoryManagerListener = new CategoryManagerListener()
/*      */           {
/*      */             public void categoryRemoved(Category category) {
/*  474 */               SB_Transfers.removeCategory(category);
/*      */             }
/*      */             
/*      */             public void categoryChanged(Category category) {
/*  478 */               SB_Transfers.RefreshCategorySideBar(category);
/*      */             }
/*      */             
/*      */             public void categoryAdded(Category category) {
/*  482 */               Category[] categories = CategoryManager.getCategories();
/*  483 */               if (categories.length == 3) {
/*  484 */                 for (Category cat : categories) {
/*  485 */                   SB_Transfers.setupCategory(cat);
/*      */                 }
/*      */               } else {
/*  488 */                 SB_Transfers.setupCategory(category);
/*      */               }
/*      */             }
/*  491 */           };
/*  492 */           CategoryManager.addCategoryManagerListener(this.categoryManagerListener);
/*  493 */           if (categories.length > 2) {
/*  494 */             for (Category category : categories) {
/*  495 */               category.addCategoryListener(this.val$categoryListener);
/*  496 */               SB_Transfers.setupCategory(category);
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  502 */           if (this.categoryManagerListener != null) {
/*  503 */             CategoryManager.removeCategoryManagerListener(this.categoryManagerListener);
/*  504 */             this.categoryManagerListener = null;
/*      */           }
/*  506 */           for (Category category : categories) {
/*  507 */             category.removeCategoryListener(this.val$categoryListener);
/*  508 */             SB_Transfers.removeCategory(category);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  513 */     });
/*  514 */     Object tagListener = new TagListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void taggableAdded(Tag tag, Taggable tagged)
/*      */       {
/*      */ 
/*      */ 
/*  522 */         SB_Transfers.RefreshTagSideBar(tag);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void taggableSync(Tag tag)
/*      */       {
/*  529 */         SB_Transfers.RefreshTagSideBar(tag);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void taggableRemoved(Tag tag, Taggable tagged)
/*      */       {
/*  537 */         SB_Transfers.RefreshTagSideBar(tag);
/*      */       }
/*      */       
/*  540 */     };
/*  541 */     COConfigurationManager.addAndFireParameterListener("Library.TagInSideBar", new ParameterListener()
/*      */     {
/*      */       private TagManagerListener tagManagerListener;
/*      */       
/*      */ 
/*      */       private TagTypeListener tagTypeListenerListener;
/*      */       
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*  551 */         boolean tagInSidebar = COConfigurationManager.getBooleanParameter("Library.TagInSideBar");
/*      */         
/*  553 */         if (tagInSidebar)
/*      */         {
/*  555 */           if (this.tagManagerListener != null)
/*      */           {
/*  557 */             return;
/*      */           }
/*      */           
/*  560 */           this.tagTypeListenerListener = new TagTypeListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void tagTypeChanged(TagType tag_type)
/*      */             {
/*      */ 
/*  567 */               for (Tag tag : tag_type.getTags())
/*      */               {
/*  569 */                 if (tag.isVisible())
/*      */                 {
/*  571 */                   SB_Transfers.setupTag(tag);
/*      */                 }
/*      */                 else
/*      */                 {
/*  575 */                   SB_Transfers.RefreshTagSideBar(tag);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */             public void tagEventOccurred(TagTypeListener.TagEvent event)
/*      */             {
/*  582 */               int type = event.getEventType();
/*  583 */               Tag tag = event.getTag();
/*  584 */               if (type == 0) {
/*  585 */                 tagAdded(tag);
/*  586 */               } else if (type == 1) {
/*  587 */                 tagChanged(tag);
/*  588 */               } else if (type == 2) {
/*  589 */                 tagRemoved(tag);
/*  590 */               } else if (type == 3) {
/*  591 */                 MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  592 */                 if (mdi == null) {
/*  593 */                   return;
/*      */                 }
/*      */                 
/*  596 */                 MdiEntry entry = mdi.getEntry("Tag." + tag.getTagType().getTagType() + "." + tag.getTagID());
/*      */                 
/*  598 */                 if (entry != null) {
/*  599 */                   mdi.showEntry(entry);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void tagAdded(Tag tag)
/*      */             {
/*  608 */               if (tag.isVisible())
/*      */               {
/*  610 */                 SB_Transfers.setupTag(tag);
/*      */                 
/*  612 */                 tag.addTagListener(SB_Transfers.19.this.val$tagListener, false);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void tagChanged(Tag tag)
/*      */             {
/*  620 */               SB_Transfers.RefreshTagSideBar(tag);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void tagRemoved(Tag tag)
/*      */             {
/*  627 */               SB_Transfers.removeTag(tag);
/*      */             }
/*      */             
/*  630 */           };
/*  631 */           this.tagManagerListener = new TagManagerListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void tagTypeAdded(TagManager manager, TagType tag_type)
/*      */             {
/*      */ 
/*      */ 
/*  639 */               if (tag_type.getTagType() != 1)
/*      */               {
/*  641 */                 tag_type.addTagTypeListener(SB_Transfers.19.this.tagTypeListenerListener, true);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void tagTypeRemoved(TagManager manager, TagType tag_type)
/*      */             {
/*  650 */               for (Tag t : tag_type.getTags())
/*      */               {
/*  652 */                 SB_Transfers.removeTag(t);
/*      */               }
/*      */               
/*      */             }
/*  656 */           };
/*  657 */           TagManagerFactory.getTagManager().addTagManagerListener(this.tagManagerListener, true);
/*      */ 
/*      */ 
/*      */         }
/*  661 */         else if (this.tagManagerListener != null)
/*      */         {
/*  663 */           TagManagerFactory.getTagManager().removeTagManagerListener(this.tagManagerListener);
/*      */           
/*  665 */           List<TagType> tag_types = TagManagerFactory.getTagManager().getTagTypes();
/*      */           
/*  667 */           for (TagType tt : tag_types)
/*      */           {
/*  669 */             if (tt.getTagType() != 1)
/*      */             {
/*  671 */               tt.removeTagTypeListener(this.tagTypeListenerListener);
/*      */             }
/*      */             
/*  674 */             for (Tag t : tt.getTags())
/*      */             {
/*  676 */               t.removeTagListener(this.val$tagListener);
/*      */               
/*  678 */               SB_Transfers.removeTag(t);
/*      */             }
/*      */           }
/*      */           
/*  682 */           this.tagManagerListener = null;
/*  683 */           this.tagTypeListenerListener = null;
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  691 */     });
/*  692 */     GlobalManager gm = core.getGlobalManager();
/*  693 */     DownloadManagerListener dmListener = new DownloadManagerAdapter() {
/*      */       public void stateChanged(DownloadManager dm, int state) {
/*  695 */         stateChanged(dm, state, SB_Transfers.statsNoLowNoise);
/*  696 */         stateChanged(dm, state, SB_Transfers.statsWithLowNoise);
/*      */       }
/*      */       
/*      */       public void stateChanged(DownloadManager dm, int state, SB_Transfers.stats stats) {
/*  700 */         if ((!stats.includeLowNoise) && (PlatformTorrentUtils.isAdvancedViewOnly(dm)))
/*      */         {
/*  702 */           return;
/*      */         }
/*      */         
/*  705 */         synchronized (SB_Transfers.statsLock) {
/*  706 */           SB_Transfers.updateDMCounts(dm);
/*      */           
/*  708 */           boolean complete = dm.getAssumedComplete();
/*  709 */           Boolean wasErrorStateB = (Boolean)dm.getUserData("wasErrorState");
/*  710 */           boolean wasErrorState = wasErrorStateB == null ? false : wasErrorStateB.booleanValue();
/*      */           
/*  712 */           boolean isErrorState = state == 100;
/*  713 */           if (isErrorState != wasErrorState) {
/*  714 */             int rel = isErrorState ? 1 : -1;
/*  715 */             if (complete) {
/*  716 */               stats.numErrorComplete += rel;
/*      */             } else {
/*  718 */               stats.numErrorInComplete += rel;
/*      */             }
/*  720 */             SB_Transfers.updateErrorTooltip(this.val$gm, stats);
/*  721 */             dm.setUserData("wasErrorState", Boolean.valueOf(isErrorState));
/*      */           }
/*  723 */           SB_Transfers.access$100();
/*      */         }
/*      */       }
/*      */       
/*      */       public void completionChanged(DownloadManager dm, boolean completed) {
/*  728 */         completionChanged(dm, completed, SB_Transfers.statsNoLowNoise);
/*  729 */         completionChanged(dm, completed, SB_Transfers.statsWithLowNoise);
/*      */       }
/*      */       
/*      */       public void completionChanged(DownloadManager dm, boolean completed, SB_Transfers.stats stats)
/*      */       {
/*  734 */         if ((!stats.includeLowNoise) && (PlatformTorrentUtils.isAdvancedViewOnly(dm)))
/*      */         {
/*  736 */           return;
/*      */         }
/*      */         
/*  739 */         synchronized (SB_Transfers.statsLock) {
/*  740 */           int dm_state = SB_Transfers.updateDMCounts(dm);
/*      */           
/*  742 */           if (completed) {
/*  743 */             stats.numComplete += 1;
/*  744 */             stats.numIncomplete -= 1;
/*  745 */             if (dm_state == 100) {
/*  746 */               stats.numErrorComplete += 1;
/*  747 */               stats.numErrorInComplete -= 1;
/*      */             }
/*  749 */             if (dm_state == 70) {
/*  750 */               SB_Transfers.statsNoLowNoise.numStoppedIncomplete -= 1;
/*      */             }
/*      */           }
/*      */           else {
/*  754 */             stats.numComplete -= 1;
/*  755 */             stats.numIncomplete += 1;
/*      */             
/*  757 */             if (dm_state == 100) {
/*  758 */               stats.numErrorComplete -= 1;
/*  759 */               stats.numErrorInComplete += 1;
/*      */             }
/*  761 */             if (dm_state == 70) {
/*  762 */               SB_Transfers.statsNoLowNoise.numStoppedIncomplete += 1;
/*      */             }
/*      */           }
/*  765 */           SB_Transfers.access$000();
/*  766 */           SB_Transfers.updateErrorTooltip(this.val$gm, stats);
/*  767 */           SB_Transfers.access$100();
/*      */         }
/*      */         
/*      */       }
/*  771 */     };
/*  772 */     gm.addListener(new GlobalManagerAdapter() {
/*      */       public void downloadManagerRemoved(DownloadManager dm) {
/*  774 */         downloadManagerRemoved(dm, SB_Transfers.statsNoLowNoise);
/*  775 */         downloadManagerRemoved(dm, SB_Transfers.statsWithLowNoise);
/*      */       }
/*      */       
/*      */       public void downloadManagerRemoved(DownloadManager dm, SB_Transfers.stats stats) {
/*  779 */         if ((!stats.includeLowNoise) && (PlatformTorrentUtils.isAdvancedViewOnly(dm)))
/*      */         {
/*  781 */           return;
/*      */         }
/*      */         
/*  784 */         synchronized (SB_Transfers.statsLock) {
/*  785 */           SB_Transfers.access$000();
/*  786 */           if (dm.getAssumedComplete()) {
/*  787 */             stats.numComplete -= 1;
/*  788 */             Boolean wasDownloadingB = (Boolean)dm.getUserData("wasDownloading");
/*  789 */             if ((wasDownloadingB != null) && (wasDownloadingB.booleanValue())) {
/*  790 */               stats.numDownloading -= 1;
/*      */             }
/*      */           } else {
/*  793 */             stats.numIncomplete -= 1;
/*  794 */             Boolean wasSeedingB = (Boolean)dm.getUserData("wasSeeding");
/*  795 */             if ((wasSeedingB != null) && (wasSeedingB.booleanValue())) {
/*  796 */               stats.numSeeding -= 1;
/*      */             }
/*      */           }
/*      */           
/*  800 */           Boolean wasStoppedB = (Boolean)dm.getUserData("wasStopped");
/*  801 */           boolean wasStopped = wasStoppedB == null ? false : wasStoppedB.booleanValue();
/*      */           
/*  803 */           if ((wasStopped) && 
/*  804 */             (!dm.getAssumedComplete())) {
/*  805 */             stats.numStoppedIncomplete -= 1;
/*      */           }
/*      */           
/*  808 */           Boolean wasQueuedB = (Boolean)dm.getUserData("wasQueued");
/*  809 */           boolean wasQueued = wasQueuedB == null ? false : wasQueuedB.booleanValue();
/*      */           
/*  811 */           if (wasQueued) {
/*  812 */             stats.numQueued -= 1;
/*      */           }
/*  814 */           SB_Transfers.access$100();
/*      */         }
/*      */         
/*  817 */         dm.removeListener(this.val$dmListener);
/*      */       }
/*      */       
/*      */       public void downloadManagerAdded(DownloadManager dm) {
/*  821 */         dm.addListener(this.val$dmListener, false);
/*      */         
/*  823 */         synchronized (SB_Transfers.statsLock) {
/*  824 */           SB_Transfers.access$000();
/*      */           
/*  826 */           downloadManagerAdded(dm, SB_Transfers.statsNoLowNoise);
/*  827 */           downloadManagerAdded(dm, SB_Transfers.statsWithLowNoise);
/*  828 */           SB_Transfers.access$100();
/*      */         }
/*      */       }
/*      */       
/*      */       public void downloadManagerAdded(DownloadManager dm, SB_Transfers.stats stats) {
/*  833 */         if ((!stats.includeLowNoise) && (PlatformTorrentUtils.isAdvancedViewOnly(dm)))
/*      */         {
/*  835 */           return;
/*      */         }
/*  837 */         boolean assumed_complete = dm.getAssumedComplete();
/*      */         
/*  839 */         synchronized (SB_Transfers.statsLock) {
/*  840 */           if ((dm.isPersistent()) && (dm.getTorrent() != null) && (!assumed_complete)) {
/*  841 */             stats.newestIncompleteDownloadTime = Math.max(stats.newestIncompleteDownloadTime, dm.getCreationTime());
/*      */           }
/*  843 */           int dm_state = dm.getState();
/*  844 */           if (assumed_complete) {
/*  845 */             stats.numComplete += 1;
/*  846 */             if (dm_state == 60) {
/*  847 */               stats.numSeeding += 1;
/*      */             }
/*      */           } else {
/*  850 */             stats.numIncomplete += 1;
/*  851 */             if (dm_state == 50) {
/*  852 */               dm.setUserData("wasDownloading", Boolean.TRUE);
/*  853 */               stats.numDownloading += 1;
/*      */             } else {
/*  855 */               dm.setUserData("wasDownloading", Boolean.FALSE); } } } } }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  862 */     resetStats(gm, dmListener, statsWithLowNoise, statsNoLowNoise);
/*      */     
/*  864 */     refreshAllLibraries();
/*      */     
/*  866 */     SimpleTimer.addPeriodicEvent("header:refresh", 60000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  875 */         SB_Transfers.stats withNoise = new SB_Transfers.stats();
/*  876 */         SB_Transfers.stats noNoise = new SB_Transfers.stats();
/*      */         
/*  878 */         noNoise.includeLowNoise = false;
/*  879 */         withNoise.includeLowNoise = true;
/*      */         
/*  881 */         synchronized (SB_Transfers.statsLock)
/*      */         {
/*  883 */           SB_Transfers.resetStats(this.val$gm, null, withNoise, noNoise);
/*      */           
/*  885 */           boolean fixed = false;
/*      */           
/*  887 */           if (!SB_Transfers.stats.access$1500(withNoise, SB_Transfers.statsWithLowNoise)) {
/*  888 */             SB_Transfers.stats.access$1600(SB_Transfers.statsWithLowNoise, withNoise);
/*  889 */             fixed = true;
/*      */           }
/*      */           
/*  892 */           if (!SB_Transfers.stats.access$1500(noNoise, SB_Transfers.statsNoLowNoise)) {
/*  893 */             SB_Transfers.stats.access$1600(SB_Transfers.statsNoLowNoise, noNoise);
/*  894 */             fixed = true;
/*      */           }
/*      */           
/*  897 */           if (fixed)
/*      */           {
/*  899 */             SB_Transfers.updateErrorTooltip(this.val$gm, SB_Transfers.statsWithLowNoise);
/*  900 */             SB_Transfers.updateErrorTooltip(this.val$gm, SB_Transfers.statsNoLowNoise);
/*      */             
/*  902 */             SB_Transfers.access$100();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void resetStats(GlobalManager gm, DownloadManagerListener listener, stats statsWithLowNoise, stats statsNoLowNoise)
/*      */   {
/*  916 */     List<DownloadManager> downloadManagers = gm.getDownloadManagers();
/*  917 */     for (Iterator<DownloadManager> iter = downloadManagers.iterator(); iter.hasNext();) {
/*  918 */       DownloadManager dm = (DownloadManager)iter.next();
/*  919 */       boolean lowNoise = PlatformTorrentUtils.isAdvancedViewOnly(dm);
/*  920 */       boolean assumed_complete = dm.getAssumedComplete();
/*  921 */       if ((dm.isPersistent()) && (dm.getTorrent() != null) && (!assumed_complete)) {
/*  922 */         long createTime = dm.getCreationTime();
/*  923 */         statsWithLowNoise.newestIncompleteDownloadTime = Math.max(statsWithLowNoise.newestIncompleteDownloadTime, createTime);
/*  924 */         if (!lowNoise) {
/*  925 */           statsNoLowNoise.newestIncompleteDownloadTime = Math.max(statsNoLowNoise.newestIncompleteDownloadTime, createTime);
/*      */         }
/*      */       }
/*  928 */       if (listener != null) {
/*  929 */         dm.addListener(listener, false);
/*      */       }
/*      */       
/*  932 */       int dm_state = dm.getState();
/*  933 */       if (dm_state == 70) {
/*  934 */         dm.setUserData("wasStopped", Boolean.TRUE);
/*  935 */         if (!dm.getAssumedComplete()) {
/*  936 */           statsWithLowNoise.numStoppedIncomplete += 1;
/*      */         }
/*  938 */         if ((!lowNoise) && 
/*  939 */           (!dm.getAssumedComplete())) {
/*  940 */           statsNoLowNoise.numStoppedIncomplete += 1;
/*      */         }
/*      */       }
/*      */       else {
/*  944 */         dm.setUserData("wasStopped", Boolean.FALSE);
/*      */       }
/*      */       
/*  947 */       if (dm_state == 75) {
/*  948 */         dm.setUserData("wasQueued", Boolean.TRUE);
/*  949 */         statsWithLowNoise.numQueued += 1;
/*  950 */         if (!lowNoise) {
/*  951 */           statsNoLowNoise.numQueued += 1;
/*      */         }
/*      */       } else {
/*  954 */         dm.setUserData("wasQueued", Boolean.FALSE);
/*      */       }
/*  956 */       if (dm.getAssumedComplete()) {
/*  957 */         statsWithLowNoise.numComplete += 1;
/*  958 */         if (!lowNoise) {
/*  959 */           statsNoLowNoise.numComplete += 1;
/*      */         }
/*  961 */         if (dm_state == 60) {
/*  962 */           dm.setUserData("wasSeeding", Boolean.TRUE);
/*  963 */           statsWithLowNoise.numSeeding += 1;
/*  964 */           if (!lowNoise) {
/*  965 */             statsNoLowNoise.numSeeding += 1;
/*      */           }
/*      */         } else {
/*  968 */           dm.setUserData("wasSeeding", Boolean.FALSE);
/*      */         }
/*      */       } else {
/*  971 */         statsWithLowNoise.numIncomplete += 1;
/*  972 */         if (!lowNoise) {
/*  973 */           statsNoLowNoise.numIncomplete += 1;
/*      */         }
/*  975 */         if (dm_state == 50) {
/*  976 */           statsWithLowNoise.numDownloading += 1;
/*  977 */           if (!lowNoise) {
/*  978 */             statsNoLowNoise.numDownloading += 1;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  983 */       if ((!PlatformTorrentUtils.getHasBeenOpened(dm)) && (dm.getAssumedComplete())) {
/*  984 */         statsNoLowNoise.numUnOpened += 1;
/*      */       }
/*      */     }
/*      */     
/*  988 */     statsWithLowNoise.numUnOpened = statsNoLowNoise.numUnOpened;
/*      */   }
/*      */   
/*      */ 
/*      */   private static void updateErrorTooltip(GlobalManager gm, stats stats)
/*      */   {
/*  994 */     if (stats.numErrorComplete < 0) {
/*  995 */       stats.numErrorComplete = 0;
/*      */     }
/*  997 */     if (stats.numErrorInComplete < 0) {
/*  998 */       stats.numErrorInComplete = 0;
/*      */     }
/*      */     
/* 1001 */     if ((stats.numErrorComplete > 0) || (stats.numErrorInComplete > 0))
/*      */     {
/* 1003 */       String comp_error = null;
/* 1004 */       String incomp_error = null;
/*      */       
/* 1006 */       List<?> downloads = gm.getDownloadManagers();
/*      */       
/* 1008 */       for (int i = 0; i < downloads.size(); i++)
/*      */       {
/* 1010 */         DownloadManager download = (DownloadManager)downloads.get(i);
/*      */         
/* 1012 */         if (download.getState() == 100)
/*      */         {
/* 1014 */           if (download.getAssumedComplete())
/*      */           {
/* 1016 */             if (comp_error == null)
/*      */             {
/* 1018 */               comp_error = download.getDisplayName() + ": " + download.getErrorDetails();
/*      */             }
/*      */             else
/*      */             {
/* 1022 */               comp_error = comp_error + "...";
/*      */             }
/*      */           }
/* 1025 */           else if (incomp_error == null)
/*      */           {
/* 1027 */             incomp_error = download.getDisplayName() + ": " + download.getErrorDetails();
/*      */           }
/*      */           else
/*      */           {
/* 1031 */             incomp_error = incomp_error + "...";
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1037 */       stats.errorCompleteTooltip = comp_error;
/* 1038 */       stats.errorInCompleteTooltip = incomp_error;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void RefreshCategorySideBar(Category category)
/*      */   {
/* 1045 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1046 */     if (mdi == null) {
/* 1047 */       return;
/*      */     }
/*      */     
/* 1050 */     MdiEntry entry = mdi.getEntry("Cat." + Base32.encode(category.getName().getBytes()));
/*      */     
/* 1052 */     if (entry == null) {
/* 1053 */       return;
/*      */     }
/*      */     
/* 1056 */     ViewTitleInfoManager.refreshTitleInfo(entry.getViewTitleInfo());
/*      */   }
/*      */   
/*      */   private static void setupCategory(Category category) {
/* 1060 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1061 */     if (mdi == null) {
/* 1062 */       return;
/*      */     }
/*      */     
/* 1065 */     String name = category.getName();
/* 1066 */     String id = "Cat." + Base32.encode(name.getBytes());
/* 1067 */     if (category.getType() != 0) {
/* 1068 */       name = "{" + name + "}";
/*      */     }
/*      */     
/* 1071 */     ViewTitleInfo viewTitleInfo = new ViewTitleInfo()
/*      */     {
/*      */       public Object getTitleInfoProperty(int propertyID) {
/* 1074 */         if ((propertyID == 0) && 
/* 1075 */           (SB_Transfers.statsNoLowNoise.numIncomplete > 0)) {
/* 1076 */           List<?> dms = this.val$category.getDownloadManagers(null);
/* 1077 */           if (dms != null) {
/* 1078 */             return "" + dms.size();
/*      */           }
/*      */         }
/*      */         
/* 1082 */         return null;
/*      */       }
/*      */       
/* 1085 */     };
/* 1086 */     MdiEntry entry = mdi.createEntryFromSkinRef("header.transfers", id, "library", name, viewTitleInfo, category, false, null);
/*      */     
/*      */ 
/* 1089 */     if (entry != null) {
/* 1090 */       entry.setImageLeftID("image.sidebar.library");
/*      */       
/* 1092 */       entry.addListener(new MdiEntryDropListener() {
/*      */         public boolean mdiEntryDrop(MdiEntry entry, Object payload) {
/* 1094 */           if (!(payload instanceof String)) {
/* 1095 */             return false;
/*      */           }
/*      */           
/* 1098 */           String dropped = (String)payload;
/* 1099 */           String[] split = Constants.PAT_SPLIT_SLASH_N.split(dropped);
/* 1100 */           if (split.length > 1) {
/* 1101 */             String type = split[0];
/* 1102 */             if (type.startsWith("DownloadManager")) {
/* 1103 */               GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 1104 */               for (int i = 1; i < split.length; i++) {
/* 1105 */                 String hash = split[i];
/*      */                 try
/*      */                 {
/* 1108 */                   DownloadManager dm = gm.getDownloadManager(new HashWrapper(Base32.decode(hash)));
/*      */                   
/*      */ 
/* 1111 */                   if (dm != null) {
/* 1112 */                     org.gudy.azureus2.ui.swt.TorrentUtil.assignToCategory(new Object[] { dm }, this.val$category);
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable t) {}
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1124 */           return true;
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1129 */     if ((entry instanceof SideBarEntrySWT)) {
/* 1130 */       SideBarEntrySWT entrySWT = (SideBarEntrySWT)entry;
/* 1131 */       entrySWT.addListener(new MdiSWTMenuHackListener() {
/*      */         public void menuWillBeShown(MdiEntry entry, Menu menuTree) {
/* 1133 */           CategoryUIUtils.createMenuItems(menuTree, this.val$category);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   private static void removeCategory(Category category)
/*      */   {
/* 1141 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1142 */     if (mdi == null) {
/* 1143 */       return;
/*      */     }
/*      */     
/* 1146 */     MdiEntry entry = mdi.getEntry("Cat." + Base32.encode(category.getName().getBytes()));
/*      */     
/*      */ 
/* 1149 */     if (entry != null) {
/* 1150 */       entry.close(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void RefreshTagSideBar(Tag tag)
/*      */   {
/* 1157 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1158 */     if (mdi == null) {
/* 1159 */       return;
/*      */     }
/*      */     
/* 1162 */     MdiEntry entry = mdi.getEntry("Tag." + tag.getTagType().getTagType() + "." + tag.getTagID());
/*      */     
/* 1164 */     if (entry == null)
/*      */     {
/* 1166 */       if (tag.isVisible())
/*      */       {
/* 1168 */         setupTag(tag);
/*      */       }
/*      */       
/* 1171 */       return;
/*      */     }
/*      */     
/* 1174 */     if (!tag.isVisible())
/*      */     {
/* 1176 */       removeTag(tag);
/*      */       
/* 1178 */       return;
/*      */     }
/*      */     
/* 1181 */     String old_title = entry.getTitle();
/*      */     
/* 1183 */     String tag_title = tag.getTagName(true);
/*      */     
/* 1185 */     if (!old_title.equals(tag_title))
/*      */     {
/* 1187 */       entry.setTitle(tag_title);
/*      */     }
/*      */     
/* 1190 */     ViewTitleInfoManager.refreshTitleInfo(entry.getViewTitleInfo());
/*      */     
/* 1192 */     Object[] tag_data = (Object[])entry.getUserData(TAG_DATA_KEY);
/*      */     
/* 1194 */     if (tag_data != null)
/*      */     {
/* 1196 */       boolean[] auto_tag = tag.isTagAuto();
/* 1197 */       boolean[] old_auto_tag = (boolean[])tag_data[1];
/*      */       
/* 1199 */       if (!Arrays.equals(auto_tag, old_auto_tag))
/*      */       {
/* 1201 */         tag_data[1] = auto_tag;
/*      */         
/* 1203 */         if ((auto_tag[0] != 0) && (auto_tag[1] != 0))
/*      */         {
/* 1205 */           entry.removeListener((MdiEntryDropListener)tag_data[0]);
/*      */         }
/*      */         else
/*      */         {
/* 1209 */           entry.addListener((MdiEntryDropListener)tag_data[0]);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static MdiEntry setupTag(Tag tag)
/*      */   {
/* 1221 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*      */     
/* 1223 */     if (mdi == null)
/*      */     {
/* 1225 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1232 */     synchronized (tag_setup_lock)
/*      */     {
/* 1234 */       String id = "Tag." + tag.getTagType().getTagType() + "." + tag.getTagID();
/*      */       
/* 1236 */       if (mdi.getEntry(id) != null)
/*      */       {
/* 1238 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1243 */       TreeMap<Tag, String> name_map = new TreeMap(TagUIUtils.getTagComparator());
/*      */       
/* 1245 */       name_map.put(tag, id);
/*      */       
/* 1247 */       for (Tag t : tag.getTagType().getTags())
/*      */       {
/* 1249 */         if (t.isVisible())
/*      */         {
/* 1251 */           String tid = "Tag." + tag.getTagType().getTagType() + "." + t.getTagID();
/*      */           
/* 1253 */           if (mdi.getEntry(tid) != null)
/*      */           {
/* 1255 */             name_map.put(t, tid);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1260 */       String prev_id = null;
/*      */       
/* 1262 */       for (String this_id : name_map.values())
/*      */       {
/* 1264 */         if (this_id == id) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1269 */         prev_id = this_id;
/*      */       }
/*      */       
/* 1272 */       if ((prev_id == null) && (name_map.size() > 1))
/*      */       {
/* 1274 */         Iterator<String> it = name_map.values().iterator();
/*      */         
/* 1276 */         it.next();
/*      */         
/* 1278 */         prev_id = "~" + (String)it.next();
/*      */       }
/*      */       
/* 1281 */       boolean auto = tag.getTagType().isTagTypeAuto();
/*      */       
/* 1283 */       ViewTitleInfo viewTitleInfo = new ViewTitleInfo()
/*      */       {
/*      */ 
/*      */ 
/*      */         public Object getTitleInfoProperty(int pid)
/*      */         {
/*      */ 
/* 1290 */           if (pid == 0)
/*      */           {
/* 1292 */             return String.valueOf(this.val$tag.getTaggedCount());
/*      */           }
/* 1294 */           if (pid == 8)
/*      */           {
/* 1296 */             TagType tag_type = this.val$tag.getTagType();
/*      */             
/* 1298 */             int[] def_color = tag_type.getColorDefault();
/*      */             
/* 1300 */             int[] tag_color = this.val$tag.getColor();
/*      */             
/* 1302 */             if (tag_color != def_color)
/*      */             {
/* 1304 */               return tag_color;
/*      */             }
/*      */           }
/* 1307 */           else if (pid == 1)
/*      */           {
/* 1309 */             return TagUIUtils.getTagTooltip(this.val$tag);
/*      */           }
/*      */           
/* 1312 */           return null;
/*      */ 
/*      */         }
/*      */         
/*      */ 
/* 1317 */       };
/* 1318 */       boolean closable = auto;
/*      */       MdiEntry entry;
/* 1320 */       MdiEntry entry; if (tag.getTaggableTypes() == 2)
/*      */       {
/* 1322 */         closable = true;
/*      */         
/* 1324 */         String name = tag.getTagName(true);
/*      */         
/* 1326 */         entry = mdi.createEntryFromSkinRef("header.transfers", id, "library", name, viewTitleInfo, tag, closable, prev_id);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1331 */         entry = mdi.createEntryFromEventListener("header.transfers", new PeersGeneralView(tag), id, closable, null, prev_id);
/*      */         
/*      */ 
/*      */ 
/* 1335 */         entry.setViewTitleInfo(viewTitleInfo);
/*      */       }
/*      */       
/* 1338 */       if (closable)
/*      */       {
/* 1340 */         entry.addListener(new com.aelitis.azureus.ui.mdi.MdiCloseListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void mdiEntryClosed(MdiEntry entry, boolean userClosed)
/*      */           {
/*      */ 
/*      */ 
/* 1348 */             if ((userClosed) && (entry.getUserData(SB_Transfers.AUTO_CLOSE_KEY) == null))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1353 */               if (COConfigurationManager.getBooleanParameter("Library.TagInSideBar"))
/*      */               {
/* 1355 */                 this.val$tag.setVisible(false);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1362 */       if (entry != null) {
/* 1363 */         String image_id = tag.getImageID();
/*      */         
/* 1365 */         if (image_id != null) {
/* 1366 */           entry.setImageLeftID(image_id);
/* 1367 */         } else if (tag.getTagType().getTagType() == 4) {
/* 1368 */           entry.setImageLeftID("image.sidebar.tag-red");
/* 1369 */         } else if (tag.getTagType().isTagTypePersistent()) {
/* 1370 */           entry.setImageLeftID("image.sidebar.tag-green");
/*      */         } else {
/* 1372 */           entry.setImageLeftID("image.sidebar.tag-blue");
/*      */         }
/*      */       }
/*      */       
/* 1376 */       if ((entry instanceof SideBarEntrySWT)) {
/* 1377 */         SideBarEntrySWT entrySWT = (SideBarEntrySWT)entry;
/* 1378 */         entrySWT.addListener(new MdiSWTMenuHackListener() {
/*      */           public void menuWillBeShown(MdiEntry entry, Menu menuTree) {
/* 1380 */             TagUIUtils.createSideBarMenuItems(menuTree, this.val$tag);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1385 */       if ((!auto) && (entry != null))
/*      */       {
/* 1387 */         MdiEntryDropListener dl = new MdiEntryDropListener() {
/*      */           public boolean mdiEntryDrop(MdiEntry entry, Object payload) {
/* 1389 */             if (!(payload instanceof String)) {
/* 1390 */               return false;
/*      */             }
/*      */             
/* 1393 */             boolean[] auto = this.val$tag.isTagAuto();
/*      */             
/* 1395 */             if ((auto[0] != 0) && (auto[1] != 0))
/*      */             {
/* 1397 */               return false;
/*      */             }
/*      */             
/* 1400 */             final String dropped = (String)payload;
/*      */             
/* 1402 */             new AEThread2("Tagger")
/*      */             {
/*      */               public void run() {
/* 1405 */                 SB_Transfers.29.this.dropTorrentOnTag(SB_Transfers.29.this.val$tag, dropped);
/*      */               }
/*      */               
/* 1408 */             }.start();
/* 1409 */             return true;
/*      */           }
/*      */           
/*      */           private void dropTorrentOnTag(Tag tag, String dropped) {
/* 1413 */             String[] split = Constants.PAT_SPLIT_SLASH_N.split(dropped);
/* 1414 */             if (split.length <= 1) {
/* 1415 */               return;
/*      */             }
/*      */             
/* 1418 */             String type = split[0];
/* 1419 */             if ((!type.startsWith("DownloadManager")) && (!type.startsWith("DiskManagerFileInfo"))) {
/* 1420 */               return;
/*      */             }
/* 1422 */             GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 1423 */             List<DownloadManager> listDMs = new ArrayList();
/* 1424 */             boolean doAdd = false;
/* 1425 */             boolean[] auto; for (int i = 1; i < split.length; i++) {
/* 1426 */               String hash = split[i];
/*      */               
/* 1428 */               int sep = hash.indexOf(";");
/*      */               
/* 1430 */               if (sep != -1)
/*      */               {
/* 1432 */                 hash = hash.substring(0, sep);
/*      */               }
/*      */               try
/*      */               {
/* 1436 */                 DownloadManager dm = gm.getDownloadManager(new HashWrapper(Base32.decode(hash)));
/*      */                 
/*      */ 
/* 1439 */                 if (dm != null)
/*      */                 {
/* 1441 */                   listDMs.add(dm);
/*      */                   
/* 1443 */                   if ((!doAdd) && (!tag.hasTaggable(dm))) {
/* 1444 */                     doAdd = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Throwable t) {}
/*      */               
/*      */ 
/* 1451 */               auto = tag.isTagAuto();
/*      */               
/* 1453 */               for (DownloadManager dm : listDMs) {
/* 1454 */                 if (doAdd)
/*      */                 {
/* 1456 */                   if (auto[0] == 0)
/*      */                   {
/* 1458 */                     tag.addTaggable(dm);
/*      */                   }
/*      */                   
/*      */                 }
/* 1462 */                 else if ((auto[0] == 0) || (auto[1] == 0))
/*      */                 {
/* 1464 */                   tag.removeTaggable(dm);
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 1471 */         };
/* 1472 */         boolean[] tag_auto = tag.isTagAuto();
/*      */         
/* 1474 */         entry.setUserData(TAG_DATA_KEY, new Object[] { dl, tag_auto });
/*      */         
/* 1476 */         if ((tag_auto[0] == 0) || (tag_auto[1] == 0))
/*      */         {
/* 1478 */           entry.addListener(dl);
/*      */         }
/*      */       }
/* 1481 */       return entry;
/*      */     }
/*      */   }
/*      */   
/*      */   private static void removeTag(Tag tag) {
/* 1486 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1487 */     if (mdi == null) {
/* 1488 */       return;
/*      */     }
/*      */     
/* 1491 */     MdiEntry entry = mdi.getEntry("Tag." + tag.getTagType().getTagType() + "." + tag.getTagID());
/*      */     
/* 1493 */     if (entry != null)
/*      */     {
/* 1495 */       entry.setUserData(AUTO_CLOSE_KEY, "");
/*      */       
/* 1497 */       entry.close(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int updateDMCounts(DownloadManager dm)
/*      */   {
/* 1510 */     Boolean wasSeedingB = (Boolean)dm.getUserData("wasSeeding");
/* 1511 */     boolean wasSeeding = wasSeedingB == null ? false : wasSeedingB.booleanValue();
/*      */     
/* 1513 */     Boolean wasDownloadingB = (Boolean)dm.getUserData("wasDownloading");
/* 1514 */     boolean wasDownloading = wasDownloadingB == null ? false : wasDownloadingB.booleanValue();
/*      */     
/* 1516 */     Boolean wasStoppedB = (Boolean)dm.getUserData("wasStopped");
/* 1517 */     boolean wasStopped = wasStoppedB == null ? false : wasStoppedB.booleanValue();
/*      */     
/* 1519 */     Boolean wasQueuedB = (Boolean)dm.getUserData("wasQueued");
/* 1520 */     boolean wasQueued = wasQueuedB == null ? false : wasQueuedB.booleanValue();
/*      */     
/*      */ 
/* 1523 */     int dm_state = dm.getState();
/*      */     boolean isDownloading;
/* 1525 */     boolean isDownloading; boolean isSeeding; if (dm.getAssumedComplete()) {
/* 1526 */       boolean isSeeding = dm_state == 60;
/* 1527 */       isDownloading = false;
/*      */     } else {
/* 1529 */       isDownloading = dm_state == 50;
/* 1530 */       isSeeding = false;
/*      */     }
/*      */     
/* 1533 */     boolean isStopped = dm_state == 70;
/* 1534 */     boolean isQueued = dm_state == 75;
/*      */     
/* 1536 */     boolean lowNoise = PlatformTorrentUtils.isAdvancedViewOnly(dm);
/*      */     
/* 1538 */     if (isDownloading != wasDownloading) {
/* 1539 */       if (isDownloading) {
/* 1540 */         statsWithLowNoise.numDownloading += 1;
/* 1541 */         if (!lowNoise) {
/* 1542 */           statsNoLowNoise.numDownloading += 1;
/*      */         }
/*      */       } else {
/* 1545 */         statsWithLowNoise.numDownloading -= 1;
/* 1546 */         if (!lowNoise) {
/* 1547 */           statsNoLowNoise.numDownloading -= 1;
/*      */         }
/*      */       }
/* 1550 */       dm.setUserData("wasDownloading", Boolean.valueOf(isDownloading));
/*      */     }
/*      */     
/* 1553 */     if (isSeeding != wasSeeding) {
/* 1554 */       if (isSeeding) {
/* 1555 */         statsWithLowNoise.numSeeding += 1;
/* 1556 */         if (!lowNoise) {
/* 1557 */           statsNoLowNoise.numSeeding += 1;
/*      */         }
/*      */       } else {
/* 1560 */         statsWithLowNoise.numSeeding -= 1;
/* 1561 */         if (!lowNoise) {
/* 1562 */           statsNoLowNoise.numSeeding -= 1;
/*      */         }
/*      */       }
/* 1565 */       dm.setUserData("wasSeeding", Boolean.valueOf(isSeeding));
/*      */     }
/*      */     
/* 1568 */     if (isStopped != wasStopped) {
/* 1569 */       if (isStopped) {
/* 1570 */         if (!dm.getAssumedComplete()) {
/* 1571 */           statsWithLowNoise.numStoppedIncomplete += 1;
/*      */         }
/* 1573 */         if ((!lowNoise) && 
/* 1574 */           (!dm.getAssumedComplete())) {
/* 1575 */           statsNoLowNoise.numStoppedIncomplete += 1;
/*      */         }
/*      */       }
/*      */       else {
/* 1579 */         if (!dm.getAssumedComplete()) {
/* 1580 */           statsWithLowNoise.numStoppedIncomplete -= 1;
/*      */         }
/* 1582 */         if ((!lowNoise) && 
/* 1583 */           (!dm.getAssumedComplete())) {
/* 1584 */           statsNoLowNoise.numStoppedIncomplete -= 1;
/*      */         }
/*      */       }
/*      */       
/* 1588 */       dm.setUserData("wasStopped", Boolean.valueOf(isStopped));
/*      */     }
/*      */     
/* 1591 */     if (isQueued != wasQueued) {
/* 1592 */       if (isQueued) {
/* 1593 */         statsWithLowNoise.numQueued += 1;
/* 1594 */         if (!lowNoise) {
/* 1595 */           statsNoLowNoise.numQueued += 1;
/*      */         }
/*      */       } else {
/* 1598 */         statsWithLowNoise.numQueued -= 1;
/* 1599 */         if (!lowNoise) {
/* 1600 */           statsNoLowNoise.numQueued -= 1;
/*      */         }
/*      */       }
/* 1603 */       dm.setUserData("wasQueued", Boolean.valueOf(isQueued));
/*      */     }
/* 1605 */     return dm_state;
/*      */   }
/*      */   
/*      */   private static void recountUnopened() {
/* 1609 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 1610 */       return;
/*      */     }
/* 1612 */     GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 1613 */     List<?> dms = gm.getDownloadManagers();
/* 1614 */     statsNoLowNoise.numUnOpened = 0;
/* 1615 */     for (Iterator<?> iter = dms.iterator(); iter.hasNext();) {
/* 1616 */       DownloadManager dm = (DownloadManager)iter.next();
/* 1617 */       if ((!PlatformTorrentUtils.getHasBeenOpened(dm)) && (dm.getAssumedComplete())) {
/* 1618 */         statsNoLowNoise.numUnOpened += 1;
/*      */       }
/*      */     }
/* 1621 */     statsWithLowNoise.numUnOpened = statsNoLowNoise.numUnOpened;
/*      */   }
/*      */   
/*      */   protected static void addCountRefreshListener(countRefreshListener l) {
/* 1625 */     l.countRefreshed(statsWithLowNoise, statsNoLowNoise);
/* 1626 */     listeners.add(l);
/*      */   }
/*      */   
/*      */   public static void triggerCountRefreshListeners() {
/* 1630 */     for (countRefreshListener l : listeners) {
/* 1631 */       l.countRefreshed(statsWithLowNoise, statsNoLowNoise);
/*      */     }
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  161 */     statsNoLowNoise.includeLowNoise = false;
/*  162 */     statsWithLowNoise.includeLowNoise = true;
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
/* 1215 */     tag_setup_lock = new Object();
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
/* 1641 */     refresh_limiter = new FrequencyLimitedDispatcher(new AERunnable() { public void runSupport() {} }, 250);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1649 */     refresh_limiter.setSingleThreaded();
/*      */   }
/*      */   
/*      */   private static void refreshAllLibraries() {
/* 1653 */     refresh_limiter.dispatch();
/*      */   }
/*      */   
/*      */   private static void refreshAllLibrariesSupport() {
/* 1657 */     for (countRefreshListener l : listeners) {
/* 1658 */       l.countRefreshed(statsWithLowNoise, statsNoLowNoise);
/*      */     }
/* 1660 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1661 */     if (mdi == null) {
/* 1662 */       return;
/*      */     }
/*      */     
/* 1665 */     if (statsNoLowNoise.numIncomplete > 0) {
/* 1666 */       MdiEntry entry = mdi.getEntry("LibraryDL");
/* 1667 */       if (entry == null) {
/* 1668 */         mdi.loadEntryByID("LibraryDL", false);
/*      */       }
/*      */     } else {
/* 1671 */       MdiEntry entry = mdi.getEntry("LibraryDL");
/* 1672 */       if (entry != null) {
/* 1673 */         entry.close(true);
/*      */       }
/*      */     }
/* 1676 */     MdiEntry entry = mdi.getEntry("LibraryDL");
/* 1677 */     if (entry != null) {
/* 1678 */       MdiEntryVitalityImage[] vitalityImages = entry.getVitalityImages();
/* 1679 */       for (int i = 0; i < vitalityImages.length; i++) {
/* 1680 */         MdiEntryVitalityImage vitalityImage = vitalityImages[i];
/* 1681 */         String imageID = vitalityImage.getImageID();
/* 1682 */         if (imageID != null)
/*      */         {
/*      */ 
/* 1685 */           if (imageID.equals("image.sidebar.vitality.dl")) {
/* 1686 */             vitalityImage.setVisible(statsNoLowNoise.numDownloading > 0);
/*      */           }
/* 1688 */           else if (imageID.equals("image.sidebar.vitality.alert")) {
/* 1689 */             vitalityImage.setVisible(statsNoLowNoise.numErrorInComplete > 0);
/* 1690 */             if (statsNoLowNoise.numErrorInComplete > 0)
/* 1691 */               vitalityImage.setToolTip(statsNoLowNoise.errorInCompleteTooltip);
/*      */           }
/*      */         }
/*      */       }
/* 1695 */       ViewTitleInfoManager.refreshTitleInfo(entry.getViewTitleInfo());
/*      */     }
/*      */     
/* 1698 */     entry = mdi.getEntry("LibraryCD");
/* 1699 */     if (entry != null) {
/* 1700 */       MdiEntryVitalityImage[] vitalityImages = entry.getVitalityImages();
/* 1701 */       for (int i = 0; i < vitalityImages.length; i++) {
/* 1702 */         MdiEntryVitalityImage vitalityImage = vitalityImages[i];
/* 1703 */         String imageID = vitalityImage.getImageID();
/* 1704 */         if (imageID != null)
/*      */         {
/*      */ 
/* 1707 */           if (imageID.equals("image.sidebar.vitality.alert")) {
/* 1708 */             vitalityImage.setVisible(statsNoLowNoise.numErrorComplete > 0);
/* 1709 */             if (statsNoLowNoise.numErrorComplete > 0) {
/* 1710 */               vitalityImage.setToolTip(statsNoLowNoise.errorCompleteTooltip);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1716 */     entry = mdi.getEntry("LibraryUnopened");
/* 1717 */     if (entry != null) {
/* 1718 */       ViewTitleInfoManager.refreshTitleInfo(entry.getViewTitleInfo());
/*      */     }
/*      */   }
/*      */   
/*      */   public static String getTableIdFromFilterMode(int torrentFilterMode, boolean big)
/*      */   {
/* 1724 */     if (torrentFilterMode == 1) {
/* 1725 */       return big ? "MySeeders.big" : "MySeeders";
/*      */     }
/* 1727 */     if (torrentFilterMode == 2) {
/* 1728 */       return big ? "MyTorrents.big" : "MyTorrents";
/*      */     }
/* 1730 */     if (torrentFilterMode == 0)
/* 1731 */       return "MyLibrary.big";
/* 1732 */     if (torrentFilterMode == 3) {
/* 1733 */       return big ? "Unopened.big" : "Unopened";
/*      */     }
/*      */     
/* 1736 */     return null;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SB_Transfers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */