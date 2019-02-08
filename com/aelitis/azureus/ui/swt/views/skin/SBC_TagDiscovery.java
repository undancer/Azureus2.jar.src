/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.content.ContentException;
/*     */ import com.aelitis.azureus.core.content.RelatedAttributeLookupListener;
/*     */ import com.aelitis.azureus.core.content.RelatedContentManager;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagDiscovery;
/*     */ import com.aelitis.azureus.core.tag.TagException;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCoreCreationListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.columns.tag.ColumnTagName;
/*     */ import com.aelitis.azureus.ui.swt.columns.tagdiscovery.ColumnTagDiscoveryAddedOn;
/*     */ import com.aelitis.azureus.ui.swt.columns.tagdiscovery.ColumnTagDiscoveryName;
/*     */ import com.aelitis.azureus.ui.swt.columns.tagdiscovery.ColumnTagDiscoveryNetwork;
/*     */ import com.aelitis.azureus.ui.swt.columns.tagdiscovery.ColumnTagDiscoveryTorrent;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectButton;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectToggle;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinToggleListener;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor2;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableSelectedRowsListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SBC_TagDiscovery
/*     */   extends SkinView
/*     */   implements UIUpdatable, UIPluginViewToolBarListener, TableViewFilterCheck<TagDiscovery>, TableViewSWTMenuFillListener, TableSelectionListener, ViewTitleInfo
/*     */ {
/*     */   private static final String TABLE_TAGDISCOVERY = "TagDiscoveryView";
/*     */   private static final boolean DEBUG = false;
/*     */   private static final String CONFIG_FILE = "tag-discovery.config";
/*     */   private static final String ID_VITALITY_ACTIVE = "image.sidebar.vitality.dots";
/*     */   TableViewSWT<TagDiscovery> tv;
/*     */   private Text txtFilter;
/*     */   private Composite table_parent;
/*  97 */   private boolean columnsAdded = false;
/*     */   
/*  99 */   private int scansRemaining = 0;
/*     */   
/* 101 */   private AEMonitor2 mon_scansRemaining = new AEMonitor2("scansRemaining");
/*     */   
/* 103 */   private Map<String, TagDiscovery> mapTagDiscoveries = new HashMap();
/*     */   
/*     */   private MdiEntry entry;
/*     */   
/*     */   private SWTSkinObjectText soTitle;
/*     */   
/*     */   private MdiEntryVitalityImage vitalityImage;
/*     */   
/*     */   private Map mapConfig;
/*     */   
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 116 */     if ((this.tv == null) || (!this.tv.isVisible())) {
/* 117 */       return false;
/*     */     }
/* 119 */     if (item.getID().equals("remove"))
/*     */     {
/* 121 */       Object[] datasources = this.tv.getSelectedDataSources().toArray();
/*     */       
/* 123 */       if (datasources.length > 0) {
/*     */         TagDiscovery discovery;
/* 125 */         for (Object object : datasources) {
/* 126 */           if ((object instanceof TagDiscovery)) {
/* 127 */             discovery = (TagDiscovery)object;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 132 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 136 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void filterSet(String filter) {}
/*     */   
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 145 */     if ((this.tv == null) || (!this.tv.isVisible())) {
/* 146 */       return;
/*     */     }
/*     */     
/* 149 */     list.put("remove", Long.valueOf(this.tv.getSelectedDataSources().size() > 0 ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateUI()
/*     */   {
/* 155 */     if (this.tv != null) {
/* 156 */       this.tv.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 162 */     return "TagDiscoveryView";
/*     */   }
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 168 */     this.mapConfig = FileUtil.readResilientConfigFile("tag-discovery.config");
/*     */     
/* 170 */     this.soTitle = ((SWTSkinObjectText)getSkinObject("title"));
/*     */     
/* 172 */     SWTSkinObjectButton soScanButton = (SWTSkinObjectButton)getSkinObject("scan-button");
/* 173 */     if (soScanButton != null) {
/* 174 */       soScanButton.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask)
/*     */         {
/* 178 */           SBC_TagDiscovery.this.startScan();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 183 */     final SWTSkinObject soFilterArea = getSkinObject("filterarea");
/* 184 */     if (soFilterArea != null)
/*     */     {
/* 186 */       SWTSkinObjectToggle soFilterButton = (SWTSkinObjectToggle)getSkinObject("filter-button");
/* 187 */       if (soFilterButton != null) {
/* 188 */         soFilterButton.addSelectionListener(new SWTSkinToggleListener() {
/*     */           public void toggleChanged(SWTSkinObjectToggle so, boolean toggled) {
/* 190 */             soFilterArea.setVisible(toggled);
/* 191 */             Utils.relayout(soFilterArea.getControl().getParent());
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 199 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*     */     
/* 201 */     if (mdi != null) {
/* 202 */       this.entry = mdi.getEntry("TagDiscovery");
/* 203 */       if (this.entry != null) {
/* 204 */         this.entry.setViewTitleInfo(this);
/* 205 */         this.vitalityImage = this.entry.addVitalityImage("image.sidebar.vitality.dots");
/* 206 */         if (this.vitalityImage != null) {
/* 207 */           this.vitalityImage.setVisible(false);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 212 */     initColumns();
/*     */     
/* 214 */     return null;
/*     */   }
/*     */   
/*     */   public Object getTitleInfoProperty(int propertyID) {
/* 218 */     if (propertyID == 0) {
/* 219 */       int num = this.mapTagDiscoveries.size();
/* 220 */       if (num > 0) {
/* 221 */         return "" + num;
/*     */       }
/*     */     }
/* 224 */     return null;
/*     */   }
/*     */   
/*     */   protected void initColumns() {
/* 228 */     synchronized (SBC_TagDiscovery.class)
/*     */     {
/* 230 */       if (this.columnsAdded)
/*     */       {
/* 232 */         return;
/*     */       }
/*     */       
/* 235 */       this.columnsAdded = true;
/*     */     }
/*     */     
/* 238 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*     */     
/* 240 */     tableManager.registerColumn(TagDiscovery.class, ColumnTagDiscoveryName.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 243 */         new ColumnTagDiscoveryName(column);
/*     */       }
/* 245 */     });
/* 246 */     tableManager.registerColumn(TagDiscovery.class, ColumnTagDiscoveryTorrent.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 249 */         new ColumnTagDiscoveryTorrent(column);
/*     */       }
/* 251 */     });
/* 252 */     tableManager.registerColumn(TagDiscovery.class, ColumnTagDiscoveryAddedOn.COLUMN_ID, new TableColumnCoreCreationListener()
/*     */     {
/*     */ 
/*     */       public TableColumnCore createTableColumnCore(Class<?> forDataSourceType, String tableID, String columnID)
/*     */       {
/* 257 */         new ColumnDateSizer(TagDiscovery.class, columnID, TableColumnCreator.DATE_COLUMN_WIDTH, tableID) {};
/*     */       }
/*     */       
/*     */ 
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 263 */         new ColumnTagDiscoveryAddedOn(column);
/*     */       }
/* 265 */     });
/* 266 */     tableManager.registerColumn(TagDiscovery.class, ColumnTagDiscoveryNetwork.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 269 */         new ColumnTagDiscoveryNetwork(column);
/*     */       }
/*     */       
/* 272 */     });
/* 273 */     tableManager.setDefaultColumnNames("TagDiscoveryView", new String[] { ColumnTagDiscoveryName.COLUMN_ID, ColumnTagDiscoveryTorrent.COLUMN_ID, ColumnTagDiscoveryAddedOn.COLUMN_ID });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 279 */     tableManager.setDefaultSortColumnName("TagDiscoveryView", ColumnTagDiscoveryAddedOn.COLUMN_ID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 286 */     if (this.mapConfig != null) {
/* 287 */       FileUtil.writeResilientConfigFile("tag-discovery.config", this.mapConfig);
/*     */     }
/*     */     
/* 290 */     if (this.tv != null)
/*     */     {
/* 292 */       this.tv.delete();
/*     */       
/* 294 */       this.tv = null;
/*     */     }
/*     */     
/* 297 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*     */     
/*     */ 
/*     */ 
/* 301 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/* 306 */     super.skinObjectShown(skinObject, params);
/* 307 */     SWTSkinObject so_list = getSkinObject("tag-discovery-list");
/*     */     
/* 309 */     if (so_list != null) {
/* 310 */       initTable((Composite)so_list.getControl());
/*     */     } else {
/* 312 */       System.out.println("NO tag-discovery-list");
/* 313 */       return null;
/*     */     }
/*     */     
/* 316 */     if (this.tv == null) {
/* 317 */       return null;
/*     */     }
/*     */     
/* 320 */     TagDiscovery[] tagDiscoveries = (TagDiscovery[])this.mapTagDiscoveries.values().toArray(new TagDiscovery[0]);
/*     */     
/* 322 */     this.tv.addDataSources(tagDiscoveries);
/*     */     
/* 324 */     return null;
/*     */   }
/*     */   
/*     */   private void startScan() {
/*     */     try {
/* 329 */       this.mon_scansRemaining.enter();
/*     */       
/* 331 */       if (this.scansRemaining > 0) {
/*     */         return;
/*     */       }
/*     */     } finally {
/* 335 */       this.mon_scansRemaining.exit();
/*     */     }
/*     */     
/* 338 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 340 */         GlobalManager gm = core.getGlobalManager();
/*     */         try {
/*     */           try {
/* 343 */             SBC_TagDiscovery.this.mon_scansRemaining.enter();
/*     */             
/* 345 */             SBC_TagDiscovery.this.scansRemaining = 0;
/*     */           } finally {
/* 347 */             SBC_TagDiscovery.this.mon_scansRemaining.exit();
/*     */           }
/*     */           
/* 350 */           rcm = RelatedContentManager.getSingleton();
/* 351 */           List<DownloadManager> dms = gm.getDownloadManagers();
/*     */           
/* 353 */           for (final DownloadManager dm : dms) {
/* 354 */             if (SBC_TagDiscovery.this.tv == null) {
/* 355 */               return;
/*     */             }
/* 357 */             TOTorrent torrent = dm.getTorrent();
/* 358 */             if (torrent != null)
/*     */             {
/*     */               try
/*     */               {
/* 362 */                 final byte[] hash = torrent.getHash();
/*     */                 try {
/* 364 */                   SBC_TagDiscovery.this.mon_scansRemaining.enter();
/*     */                   
/* 366 */                   SBC_TagDiscovery.access$208(SBC_TagDiscovery.this);
/*     */                   
/* 368 */                   if ((SBC_TagDiscovery.this.vitalityImage != null) && (SBC_TagDiscovery.this.scansRemaining == 1)) {
/* 369 */                     SBC_TagDiscovery.this.vitalityImage.setVisible(true);
/*     */                   }
/*     */                   
/* 372 */                   if (SBC_TagDiscovery.this.soTitle != null) {
/* 373 */                     SBC_TagDiscovery.this.soTitle.setText(MessageText.getString("tag.discovery.view.heading") + " : Scanning " + SBC_TagDiscovery.this.scansRemaining);
/*     */                   }
/*     */                 }
/*     */                 finally {
/* 377 */                   SBC_TagDiscovery.this.mon_scansRemaining.exit();
/*     */                 }
/*     */                 try
/*     */                 {
/* 381 */                   rcm.lookupAttributes(hash, dm.getDownloadState().getNetworks(), new RelatedAttributeLookupListener()
/*     */                   {
/*     */ 
/*     */ 
/*     */                     public void tagFound(String tag, String network)
/*     */                     {
/*     */ 
/* 388 */                       if (SBC_TagDiscovery.this.tv == null) {
/* 389 */                         return;
/*     */                       }
/* 391 */                       String key = Base32.encode(hash) + tag;
/*     */                       
/* 393 */                       TagManager tm = TagManagerFactory.getTagManager();
/* 394 */                       TagType tt_manual = tm.getTagType(3);
/* 395 */                       List<Tag> existingDMTags = tt_manual.getTagsForTaggable(dm);
/* 396 */                       for (Tag existingTag : existingDMTags) {
/* 397 */                         if (existingTag.getTagName(true).equalsIgnoreCase(tag)) {
/* 398 */                           return;
/*     */                         }
/*     */                       }
/* 401 */                       synchronized (SBC_TagDiscovery.this.mapTagDiscoveries) {
/* 402 */                         if (!SBC_TagDiscovery.this.mapTagDiscoveries.containsKey(key)) {
/* 403 */                           TagDiscovery tagDiscovery = new TagDiscovery(tag, network, dm.getDisplayName(), hash);
/*     */                           
/* 405 */                           SBC_TagDiscovery.this.mapTagDiscoveries.put(key, tagDiscovery);
/* 406 */                           ViewTitleInfoManager.refreshTitleInfo(SBC_TagDiscovery.this);
/* 407 */                           SBC_TagDiscovery.this.tv.addDataSource(tagDiscovery);
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                     public void lookupStart() {}
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                     public void lookupFailed(ContentException error) {}
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */                     public void lookupComplete()
/*     */                     {
/* 429 */                       SBC_TagDiscovery.this.decreaseScansRemaining();
/*     */ 
/*     */                     }
/*     */                     
/*     */ 
/*     */                   });
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/*     */ 
/* 440 */                   SBC_TagDiscovery.this.decreaseScansRemaining();
/*     */                 }
/*     */               } catch (TOTorrentException e) {
/* 443 */                 e.printStackTrace();
/*     */               } }
/*     */           }
/*     */         } catch (ContentException e) {
/*     */           RelatedContentManager rcm;
/* 448 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void decreaseScansRemaining() {
/*     */     try {
/* 456 */       this.mon_scansRemaining.enter();
/*     */       
/* 458 */       this.scansRemaining -= 1;
/*     */       
/* 460 */       if (this.soTitle != null) {
/* 461 */         if (this.scansRemaining <= 0) {
/* 462 */           this.soTitle.setTextID("tag.discovery.view.heading");
/*     */         } else {
/* 464 */           this.soTitle.setText(MessageText.getString("tag.discovery.view.heading") + " : Scanning " + this.scansRemaining);
/*     */         }
/*     */       }
/*     */       
/* 468 */       if ((this.vitalityImage != null) && (this.scansRemaining <= 0)) {
/* 469 */         this.vitalityImage.setVisible(false);
/*     */       }
/*     */     } finally {
/* 472 */       this.mon_scansRemaining.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 478 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */   private void initTable(Composite control) {
/* 482 */     if (this.tv == null)
/*     */     {
/* 484 */       this.tv = TableViewFactory.createTableViewSWT(TagDiscovery.class, "TagDiscoveryView", "TagDiscoveryView", new TableColumnCore[0], ColumnTagName.COLUMN_ID, 268500994);
/*     */       
/*     */ 
/* 487 */       if (this.txtFilter != null) {
/* 488 */         this.tv.enableFilterCheck(this.txtFilter, this);
/*     */       }
/* 490 */       this.tv.setRowDefaultHeightEM(1.0F);
/*     */       
/* 492 */       this.table_parent = new Composite(control, 2048);
/* 493 */       this.table_parent.setLayoutData(Utils.getFilledFormData());
/* 494 */       GridLayout layout = new GridLayout();
/* 495 */       layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/* 496 */       this.table_parent.setLayout(layout);
/*     */       
/* 498 */       this.tv.addMenuFillListener(this);
/* 499 */       this.tv.addSelectionListener(this, false);
/*     */       
/* 501 */       this.tv.initialize(this.table_parent);
/*     */     }
/*     */     
/* 504 */     control.layout(true);
/*     */   }
/*     */   
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 509 */     List<Object> ds = this.tv.getSelectedDataSources();
/*     */     
/* 511 */     MenuItem menuTagIt = new MenuItem(menu, 8);
/*     */     
/* 513 */     Messages.setLanguageText(menuTagIt, "TagDiscoveriesView.menu.tagit");
/* 514 */     menuTagIt.addListener(13, new TableSelectedRowsListener(this.tv) {
/*     */       public void run(TableRowCore row) {
/* 516 */         TagDiscovery tagDiscovery = (TagDiscovery)row.getDataSource(true);
/* 517 */         TagManager tm = TagManagerFactory.getTagManager();
/* 518 */         TagType manual_tt = tm.getTagType(3);
/* 519 */         Tag tag = manual_tt.getTag(tagDiscovery.getName(), true);
/* 520 */         if (tag == null) {
/*     */           try {
/* 522 */             tag = manual_tt.createTag(tagDiscovery.getName(), true);
/* 523 */             tag.setPublic(true);
/* 524 */             tag.setGroup("Discovery");
/* 525 */             tag.setVisible(true);
/*     */           } catch (TagException e) {
/* 527 */             return;
/*     */           }
/*     */         }
/* 530 */         byte[] hash = tagDiscovery.getHash();
/* 531 */         DownloadManager dm = AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManager(new HashWrapper(hash));
/*     */         
/* 533 */         tag.addTaggable(dm);
/*     */         
/* 535 */         String key = Base32.encode(hash) + tag.getTagName(true);
/* 536 */         SBC_TagDiscovery.this.mapTagDiscoveries.remove(key);
/* 537 */         SBC_TagDiscovery.this.tv.removeDataSource(tagDiscovery);
/* 538 */         ViewTitleInfoManager.refreshTitleInfo(SBC_TagDiscovery.this);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void addThisColumnSubMenu(String sColumnName, Menu menuThisColumn) {}
/*     */   
/*     */ 
/*     */   public void selected(TableRowCore[] row) {}
/*     */   
/*     */ 
/*     */   public void deselected(TableRowCore[] rows) {}
/*     */   
/*     */ 
/*     */   public void focusChanged(TableRowCore focus) {}
/*     */   
/*     */   public void defaultSelected(TableRowCore[] rows, int stateMask)
/*     */   {
/*     */     TagDiscovery tag;
/* 558 */     if (rows.length == 1)
/*     */     {
/* 560 */       Object obj = rows[0].getDataSource();
/*     */       
/* 562 */       if ((obj instanceof TagDiscovery))
/*     */       {
/* 564 */         tag = (TagDiscovery)obj;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void mouseEnter(TableRowCore row) {}
/*     */   
/*     */ 
/*     */   public void mouseExit(TableRowCore row) {}
/*     */   
/*     */ 
/*     */   public boolean filterCheck(TagDiscovery ds, String filter, boolean regex)
/*     */   {
/* 579 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_TagDiscovery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */