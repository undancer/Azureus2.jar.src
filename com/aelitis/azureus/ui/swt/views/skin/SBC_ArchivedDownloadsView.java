/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.util.RegExUtil;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCoreCreationListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableCountChangeListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.columns.archivedls.ColumnArchiveDLDate;
/*     */ import com.aelitis.azureus.ui.swt.columns.archivedls.ColumnArchiveDLFileCount;
/*     */ import com.aelitis.azureus.ui.swt.columns.archivedls.ColumnArchiveDLName;
/*     */ import com.aelitis.azureus.ui.swt.columns.archivedls.ColumnArchiveDLSize;
/*     */ import com.aelitis.azureus.ui.swt.columns.archivedls.ColumnArchiveDLTags;
/*     */ import com.aelitis.azureus.ui.swt.columns.archivedls.ColumnArchiveShareRatio;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStubEvent;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStubListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.views.ArchivedFilesView;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils.ArchiveCallback;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SBC_ArchivedDownloadsView
/*     */   extends SkinView
/*     */   implements UIUpdatable, UIPluginViewToolBarListener, TableViewFilterCheck<DownloadStub>, TableViewSWTMenuFillListener, TableSelectionListener, DownloadStubListener
/*     */ {
/*     */   private static final String TABLE_NAME = "ArchivedDownloads";
/*     */   TableViewSWT<DownloadStub> tv;
/*     */   private Text txtFilter;
/*     */   private Composite table_parent;
/*  94 */   private boolean columnsAdded = false;
/*     */   
/*     */ 
/*     */   private boolean dm_listener_added;
/*     */   
/*     */ 
/*     */   private boolean registeredCoreSubViews;
/*     */   
/*     */   private Object datasource;
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 107 */     initColumns();
/*     */     
/* 109 */     new InfoBarUtil(skinObject, "archivedlsview.infobar", false, "archivedls.infobar", "archivedls.view.infobar")
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean allowShow()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 119 */         return true;
/*     */       }
/*     */       
/* 122 */     };
/* 123 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void initColumns()
/*     */   {
/* 129 */     synchronized (SBC_ArchivedDownloadsView.class)
/*     */     {
/* 131 */       if (this.columnsAdded)
/*     */       {
/* 133 */         return;
/*     */       }
/*     */       
/* 136 */       this.columnsAdded = true;
/*     */     }
/*     */     
/* 139 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*     */     
/* 141 */     tableManager.registerColumn(DownloadStub.class, ColumnArchiveDLName.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 144 */         new ColumnArchiveDLName(column);
/*     */       }
/*     */       
/* 147 */     });
/* 148 */     tableManager.registerColumn(DownloadStub.class, ColumnArchiveDLSize.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 151 */         new ColumnArchiveDLSize(column);
/*     */       }
/*     */       
/* 154 */     });
/* 155 */     tableManager.registerColumn(DownloadStub.class, ColumnArchiveDLFileCount.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 158 */         new ColumnArchiveDLFileCount(column);
/*     */       }
/*     */       
/* 161 */     });
/* 162 */     tableManager.registerColumn(DownloadStub.class, ColumnArchiveDLDate.COLUMN_ID, new TableColumnCoreCreationListener()
/*     */     {
/*     */ 
/*     */       public TableColumnCore createTableColumnCore(Class<?> forDataSourceType, String tableID, String columnID)
/*     */       {
/* 167 */         new ColumnDateSizer(DownloadStub.class, columnID, TableColumnCreator.DATE_COLUMN_WIDTH, tableID) {};
/*     */       }
/*     */       
/*     */ 
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 173 */         new ColumnArchiveDLDate(column);
/*     */       }
/*     */       
/* 176 */     });
/* 177 */     tableManager.registerColumn(DownloadStub.class, ColumnArchiveDLTags.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 180 */         new ColumnArchiveDLTags(column);
/*     */       }
/*     */       
/* 183 */     });
/* 184 */     tableManager.registerColumn(DownloadStub.class, ColumnArchiveShareRatio.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 187 */         new ColumnArchiveShareRatio(column);
/*     */       }
/*     */       
/* 190 */     });
/* 191 */     tableManager.setDefaultColumnNames("ArchivedDownloads", new String[] { ColumnArchiveDLName.COLUMN_ID, ColumnArchiveDLSize.COLUMN_ID, ColumnArchiveDLFileCount.COLUMN_ID, ColumnArchiveDLDate.COLUMN_ID, ColumnArchiveShareRatio.COLUMN_ID });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 200 */     tableManager.setDefaultSortColumnName("ArchivedDownloads", ColumnArchiveDLName.COLUMN_ID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 208 */     if (this.tv != null)
/*     */     {
/* 210 */       this.tv.delete();
/*     */       
/* 212 */       this.tv = null;
/*     */     }
/*     */     
/* 215 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*     */     
/*     */ 
/*     */ 
/* 219 */     if (this.dm_listener_added)
/*     */     {
/* 221 */       PluginInitializer.getDefaultInterface().getDownloadManager().removeDownloadStubListener(this);
/*     */       
/* 223 */       this.dm_listener_added = false;
/*     */     }
/*     */     
/* 226 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/* 234 */     super.skinObjectShown(skinObject, params);
/*     */     
/* 236 */     SWTSkinObjectTextbox soFilter = (SWTSkinObjectTextbox)getSkinObject("filterbox");
/*     */     
/* 238 */     if (soFilter != null)
/*     */     {
/* 240 */       this.txtFilter = soFilter.getTextControl();
/*     */     }
/*     */     
/* 243 */     SWTSkinObject so_list = getSkinObject("archived-dls-list");
/*     */     
/* 245 */     if (so_list != null)
/*     */     {
/* 247 */       initTable((Composite)so_list.getControl());
/*     */     }
/*     */     else
/*     */     {
/* 251 */       System.out.println("NO archived-dls-list");
/*     */       
/* 253 */       return null;
/*     */     }
/*     */     
/* 256 */     if (this.tv == null)
/*     */     {
/* 258 */       return null;
/*     */     }
/*     */     
/* 261 */     PluginInitializer.getDefaultInterface().getDownloadManager().addDownloadStubListener(this, true);
/*     */     
/* 263 */     this.dm_listener_added = true;
/*     */     
/* 265 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 274 */     if (this.dm_listener_added)
/*     */     {
/* 276 */       PluginInitializer.getDefaultInterface().getDownloadManager().removeDownloadStubListener(this);
/*     */       
/* 278 */       this.dm_listener_added = false;
/*     */     }
/*     */     
/* 281 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initTable(Composite control)
/*     */   {
/* 289 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */     
/* 291 */     if (uiFunctions != null)
/*     */     {
/* 293 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 295 */       registerPluginViews(pluginUI);
/*     */     }
/*     */     
/* 298 */     if (this.tv == null)
/*     */     {
/* 300 */       this.tv = TableViewFactory.createTableViewSWT(DownloadStub.class, "ArchivedDownloads", "ArchivedDownloads", new TableColumnCore[0], ColumnArchiveDLName.COLUMN_ID, 268500994);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 306 */       if (this.txtFilter != null)
/*     */       {
/* 308 */         this.tv.enableFilterCheck(this.txtFilter, this);
/*     */       }
/*     */       
/* 311 */       this.tv.setRowDefaultHeightEM(1.0F);
/*     */       
/* 313 */       this.tv.setEnableTabViews(true, true, null);
/*     */       
/* 315 */       this.table_parent = new Composite(control, 2048);
/*     */       
/* 317 */       this.table_parent.setLayoutData(Utils.getFilledFormData());
/*     */       
/* 319 */       GridLayout layout = new GridLayout();
/*     */       
/* 321 */       layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/*     */       
/* 323 */       this.table_parent.setLayout(layout);
/*     */       
/* 325 */       this.tv.addMenuFillListener(this);
/* 326 */       this.tv.addSelectionListener(this, false);
/*     */       
/* 328 */       this.tv.initialize(this.table_parent);
/*     */       
/* 330 */       this.tv.addCountChangeListener(new TableCountChangeListener()
/*     */       {
/*     */         public void rowRemoved(TableRowCore row) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void rowAdded(TableRowCore row)
/*     */         {
/* 343 */           if (SBC_ArchivedDownloadsView.this.datasource == row.getDataSource())
/*     */           {
/* 345 */             SBC_ArchivedDownloadsView.this.tv.setSelectedRows(new TableRowCore[] { row });
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 351 */     control.layout(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void registerPluginViews(UISWTInstance pluginUI)
/*     */   {
/* 358 */     if (this.registeredCoreSubViews)
/*     */     {
/* 360 */       return;
/*     */     }
/*     */     
/* 363 */     pluginUI.addView("ArchivedDownloads", "ArchivedFilesView", ArchivedFilesView.class, null);
/*     */     
/* 365 */     this.registeredCoreSubViews = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 374 */     if ((this.tv == null) || (!this.tv.isVisible()))
/*     */     {
/* 376 */       return false;
/*     */     }
/*     */     
/* 379 */     List<Object> datasources = this.tv.getSelectedDataSources();
/*     */     
/* 381 */     if (datasources.size() > 0)
/*     */     {
/* 383 */       List<DownloadStub> dms = new ArrayList(datasources.size());
/*     */       
/* 385 */       for (Object o : datasources)
/*     */       {
/* 387 */         dms.add((DownloadStub)o);
/*     */       }
/*     */       
/* 390 */       String id = item.getID();
/*     */       
/* 392 */       if (id.equals("remove"))
/*     */       {
/* 394 */         TorrentUtil.removeDataSources(datasources.toArray());
/*     */       }
/* 396 */       else if ((id.equals("startstop")) || (id.equals("start")))
/*     */       {
/* 398 */         ManagerUtils.restoreFromArchive(dms, true, null);
/*     */       }
/*     */       
/*     */ 
/* 402 */       return true;
/*     */     }
/*     */     
/* 405 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 413 */     if ((this.tv == null) || (!this.tv.isVisible()))
/*     */     {
/* 415 */       return;
/*     */     }
/*     */     
/* 418 */     boolean canEnable = false;
/*     */     
/* 420 */     Object[] datasources = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 422 */     if (datasources.length > 0)
/*     */     {
/* 424 */       canEnable = true;
/*     */     }
/*     */     
/* 427 */     list.put("start", Long.valueOf(canEnable ? 1L : 0L));
/* 428 */     list.put("startstop", Long.valueOf(canEnable ? 1L : 0L));
/*     */     
/* 430 */     list.put("remove", Long.valueOf(canEnable ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateUI()
/*     */   {
/* 436 */     if (this.tv != null)
/*     */     {
/* 438 */       this.tv.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUpdateUIName()
/*     */   {
/* 445 */     return "ArchivedDownloads";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 460 */     List<Object> ds = this.tv.getSelectedDataSources();
/*     */     
/* 462 */     final List<DownloadStub> dms = new ArrayList(ds.size());
/*     */     
/* 464 */     for (Object o : ds)
/*     */     {
/* 466 */       dms.add((DownloadStub)o);
/*     */     }
/*     */     
/* 469 */     boolean hasSelection = dms.size() > 0;
/*     */     
/*     */ 
/*     */ 
/* 473 */     final boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*     */     
/* 475 */     MenuItem itemExplore = new MenuItem(menu, 8);
/*     */     
/* 477 */     Messages.setLanguageText(itemExplore, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"));
/*     */     
/*     */ 
/* 480 */     itemExplore.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 485 */         for (DownloadStub download : dms)
/*     */         {
/* 487 */           ManagerUtils.open(new File(download.getSavePath()), use_open_containing_folder);
/*     */         }
/*     */         
/*     */       }
/* 491 */     });
/* 492 */     itemExplore.setEnabled(hasSelection);
/*     */     
/* 494 */     new MenuItem(menu, 2);
/*     */     
/* 496 */     MenuItem itemRestore = new MenuItem(menu, 8);
/*     */     
/* 498 */     Messages.setLanguageText(itemRestore, "MyTorrentsView.menu.restore");
/*     */     
/* 500 */     itemRestore.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 508 */         ManagerUtils.restoreFromArchive(dms, false, null);
/*     */       }
/*     */       
/* 511 */     });
/* 512 */     itemRestore.setEnabled(hasSelection);
/*     */     
/* 514 */     MenuItem itemRestoreAnd = new MenuItem(menu, 8);
/*     */     
/* 516 */     Messages.setLanguageText(itemRestoreAnd, "MyTorrentsView.menu.restore.and");
/*     */     
/* 518 */     itemRestoreAnd.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 526 */         ManagerUtils.restoreFromArchive(dms, false, new ManagerUtils.ArchiveCallback()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 531 */           private List<org.gudy.azureus2.core3.download.DownloadManager> targets = new ArrayList();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void success(DownloadStub source, DownloadStub target)
/*     */           {
/* 539 */             org.gudy.azureus2.core3.download.DownloadManager dm = PluginCoreUtils.unwrap((Download)target);
/*     */             
/* 541 */             if (dm != null)
/*     */             {
/* 543 */               this.targets.add(dm);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void completed()
/*     */           {
/* 551 */             Utils.execSWTThread(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 557 */                 if (SBC_ArchivedDownloadsView.11.1.this.targets.size() == 0)
/*     */                 {
/* 559 */                   return;
/*     */                 }
/*     */                 
/* 562 */                 final Menu menu = new Menu(SBC_ArchivedDownloadsView.this.table_parent);
/*     */                 
/* 564 */                 org.gudy.azureus2.core3.download.DownloadManager[] dm_list = (org.gudy.azureus2.core3.download.DownloadManager[])SBC_ArchivedDownloadsView.11.1.this.targets.toArray(new org.gudy.azureus2.core3.download.DownloadManager[SBC_ArchivedDownloadsView.11.this.val$dms.size()]);
/*     */                 
/* 566 */                 TorrentUtil.fillTorrentMenu(menu, dm_list, AzureusCoreFactory.getSingleton(), SBC_ArchivedDownloadsView.this.table_parent, true, 0, SBC_ArchivedDownloadsView.this.tv);
/*     */                 
/*     */ 
/* 569 */                 menu.addMenuListener(new MenuListener()
/*     */                 {
/*     */                   public void menuShown(MenuEvent e) {}
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                   public void menuHidden(MenuEvent e)
/*     */                   {
/* 582 */                     Utils.execSWTThreadLater(1, new Runnable()
/*     */                     {
/*     */ 
/*     */ 
/*     */                       public void run()
/*     */                       {
/*     */ 
/* 589 */                         SBC_ArchivedDownloadsView.11.1.1.1.this.val$menu.dispose();
/*     */                       }
/*     */                       
/*     */                     });
/*     */                   }
/* 594 */                 });
/* 595 */                 menu.setVisible(true);
/*     */               }
/*     */               
/*     */             });
/*     */           }
/*     */         });
/*     */       }
/* 602 */     });
/* 603 */     itemRestoreAnd.setEnabled(hasSelection);
/*     */     
/* 605 */     new MenuItem(menu, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void selected(TableRowCore[] row)
/*     */   {
/* 612 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */     
/* 614 */     if (uiFunctions != null)
/*     */     {
/* 616 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void deselected(TableRowCore[] rows)
/*     */   {
/* 624 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */     
/* 626 */     if (uiFunctions != null)
/*     */     {
/* 628 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void focusChanged(TableRowCore focus)
/*     */   {
/* 636 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */     
/* 638 */     if (uiFunctions != null)
/*     */     {
/* 640 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void defaultSelected(TableRowCore[] rows, int stateMask)
/*     */   {
/*     */     Object obj;
/*     */     
/* 649 */     if (rows.length == 1)
/*     */     {
/* 651 */       obj = rows[0].getDataSource();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadStubEventOccurred(DownloadStubEvent event)
/*     */     throws DownloadException
/*     */   {
/* 662 */     int type = event.getEventType();
/*     */     
/* 664 */     List<DownloadStub> dls = event.getDownloadStubs();
/*     */     
/* 666 */     if (type == 1)
/*     */     {
/* 668 */       this.tv.addDataSources(dls.toArray(new DownloadStub[dls.size()]));
/*     */     }
/* 670 */     else if (type == 2)
/*     */     {
/* 672 */       this.tv.removeDataSources(dls.toArray(new DownloadStub[dls.size()]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseEnter(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseExit(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void filterSet(String filter) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean filterCheck(DownloadStub ds, String filter, boolean regex)
/*     */   {
/* 700 */     if (filter.toLowerCase(Locale.US).startsWith("f:"))
/*     */     {
/* 702 */       filter = filter.substring(2).trim();
/*     */       
/* 704 */       DownloadStub.DownloadStubFile[] files = ds.getStubFiles();
/*     */       
/* 706 */       String s = "\\Q" + filter.replaceAll("\\s*[|;]\\s*", "\\\\E|\\\\Q") + "\\E";
/*     */       
/* 708 */       boolean match_result = true;
/*     */       
/* 710 */       if ((regex) && (s.startsWith("!")))
/*     */       {
/* 712 */         s = s.substring(1);
/*     */         
/* 714 */         match_result = false;
/*     */       }
/*     */       
/* 717 */       Pattern pattern = RegExUtil.getCachedPattern("archiveview:search", s, 2);
/*     */       
/*     */ 
/* 720 */       boolean result = !match_result;
/*     */       
/*     */ 
/* 723 */       for (DownloadStub.DownloadStubFile file : files)
/*     */       {
/* 725 */         String name = file.getFile().getName();
/*     */         
/* 727 */         if (pattern.matcher(name).find())
/*     */         {
/* 729 */           result = match_result;
/*     */           
/* 731 */           break;
/*     */         }
/*     */       }
/*     */       
/* 735 */       return result;
/*     */     }
/*     */     
/*     */ 
/* 739 */     String name = ds.getName();
/*     */     
/* 741 */     String s = "\\Q" + filter.replaceAll("\\s*[|;]\\s*", "\\\\E|\\\\Q") + "\\E";
/*     */     
/* 743 */     boolean match_result = true;
/*     */     
/* 745 */     if ((regex) && (s.startsWith("!")))
/*     */     {
/* 747 */       s = s.substring(1);
/*     */       
/* 749 */       match_result = false;
/*     */     }
/*     */     
/* 752 */     Pattern pattern = RegExUtil.getCachedPattern("archiveview:search", s, 2);
/*     */     
/* 754 */     return pattern.matcher(name).find() == match_result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params)
/*     */   {
/* 763 */     if ((params instanceof DownloadStub))
/*     */     {
/* 765 */       if (this.tv != null)
/*     */       {
/* 767 */         TableRowCore row = this.tv.getRow((DownloadStub)params);
/*     */         
/* 769 */         if (row != null)
/*     */         {
/* 771 */           this.tv.setSelectedRows(new TableRowCore[] { row });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 776 */     this.datasource = params;
/*     */     
/* 778 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_ArchivedDownloadsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */