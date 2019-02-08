/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.util.RegExUtil;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCoreCreationListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableCountChangeListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.swt.columns.dlhistory.ColumnDLHistoryAddDate;
/*     */ import com.aelitis.azureus.ui.swt.columns.dlhistory.ColumnDLHistoryCompleteDate;
/*     */ import com.aelitis.azureus.ui.swt.columns.dlhistory.ColumnDLHistoryHash;
/*     */ import com.aelitis.azureus.ui.swt.columns.dlhistory.ColumnDLHistoryName;
/*     */ import com.aelitis.azureus.ui.swt.columns.dlhistory.ColumnDLHistoryRemoveDate;
/*     */ import com.aelitis.azureus.ui.swt.columns.dlhistory.ColumnDLHistorySaveLocation;
/*     */ import com.aelitis.azureus.ui.swt.columns.dlhistory.ColumnDLHistorySize;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistory;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistoryEvent;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistoryListener;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistoryManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
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
/*     */ public class SBC_DownloadHistoryView
/*     */   extends SkinView
/*     */   implements UIUpdatable, UIPluginViewToolBarListener, TableViewFilterCheck<DownloadHistory>, TableViewSWTMenuFillListener, TableSelectionListener, DownloadHistoryListener
/*     */ {
/*     */   private static final String TABLE_NAME = "DownloadHistory";
/*  81 */   private static final DownloadHistoryManager dh_manager = (DownloadHistoryManager)AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadHistoryManager();
/*     */   
/*     */ 
/*     */   private TableViewSWT<DownloadHistory> tv;
/*     */   
/*     */   private Text txtFilter;
/*     */   
/*     */   private Composite table_parent;
/*     */   
/*  90 */   private boolean columnsAdded = false;
/*     */   
/*     */ 
/*     */   private boolean dh_listener_added;
/*     */   
/*     */ 
/*     */   private Object datasource;
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 101 */     initColumns();
/*     */     
/* 103 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void initColumns()
/*     */   {
/* 109 */     synchronized (SBC_DownloadHistoryView.class)
/*     */     {
/* 111 */       if (this.columnsAdded)
/*     */       {
/* 113 */         return;
/*     */       }
/*     */       
/* 116 */       this.columnsAdded = true;
/*     */     }
/*     */     
/* 119 */     TableColumnManager tableManager = TableColumnManager.getInstance();
/*     */     
/* 121 */     tableManager.registerColumn(DownloadHistory.class, ColumnDLHistoryName.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 124 */         new ColumnDLHistoryName(column);
/*     */       }
/*     */       
/* 127 */     });
/* 128 */     tableManager.registerColumn(DownloadHistory.class, ColumnDLHistoryAddDate.COLUMN_ID, new TableColumnCoreCreationListener()
/*     */     {
/*     */ 
/*     */       public TableColumnCore createTableColumnCore(Class<?> forDataSourceType, String tableID, String columnID)
/*     */       {
/*     */ 
/* 134 */         new ColumnDateSizer(DownloadHistory.class, columnID, TableColumnCreator.DATE_COLUMN_WIDTH, tableID) {};
/*     */       }
/*     */       
/*     */ 
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 140 */         new ColumnDLHistoryAddDate(column);
/*     */       }
/*     */       
/* 143 */     });
/* 144 */     tableManager.registerColumn(DownloadHistory.class, ColumnDLHistoryCompleteDate.COLUMN_ID, new TableColumnCoreCreationListener()
/*     */     {
/*     */ 
/*     */       public TableColumnCore createTableColumnCore(Class<?> forDataSourceType, String tableID, String columnID)
/*     */       {
/*     */ 
/* 150 */         new ColumnDateSizer(DownloadHistory.class, columnID, TableColumnCreator.DATE_COLUMN_WIDTH, tableID) {};
/*     */       }
/*     */       
/*     */ 
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 156 */         new ColumnDLHistoryCompleteDate(column);
/*     */       }
/*     */       
/* 159 */     });
/* 160 */     tableManager.registerColumn(DownloadHistory.class, ColumnDLHistoryRemoveDate.COLUMN_ID, new TableColumnCoreCreationListener()
/*     */     {
/*     */ 
/*     */       public TableColumnCore createTableColumnCore(Class<?> forDataSourceType, String tableID, String columnID)
/*     */       {
/*     */ 
/* 166 */         new ColumnDateSizer(DownloadHistory.class, columnID, TableColumnCreator.DATE_COLUMN_WIDTH, tableID) {};
/*     */       }
/*     */       
/*     */ 
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 172 */         new ColumnDLHistoryRemoveDate(column);
/*     */       }
/*     */       
/* 175 */     });
/* 176 */     tableManager.registerColumn(DownloadHistory.class, ColumnDLHistoryHash.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 179 */         new ColumnDLHistoryHash(column);
/*     */       }
/*     */       
/* 182 */     });
/* 183 */     tableManager.registerColumn(DownloadHistory.class, ColumnDLHistorySize.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 186 */         new ColumnDLHistorySize(column);
/*     */       }
/*     */       
/* 189 */     });
/* 190 */     tableManager.registerColumn(DownloadHistory.class, ColumnDLHistorySaveLocation.COLUMN_ID, new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 193 */         new ColumnDLHistorySaveLocation(column);
/*     */       }
/*     */       
/* 196 */     });
/* 197 */     tableManager.setDefaultColumnNames("DownloadHistory", new String[] { ColumnDLHistoryName.COLUMN_ID, ColumnDLHistoryAddDate.COLUMN_ID, ColumnDLHistoryCompleteDate.COLUMN_ID, ColumnDLHistoryRemoveDate.COLUMN_ID });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 205 */     tableManager.setDefaultSortColumnName("DownloadHistory", ColumnDLHistoryName.COLUMN_ID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 213 */     if (this.tv != null)
/*     */     {
/* 215 */       this.tv.delete();
/*     */       
/* 217 */       this.tv = null;
/*     */     }
/*     */     
/* 220 */     Utils.disposeSWTObjects(new Object[] { this.table_parent });
/*     */     
/*     */ 
/*     */ 
/* 224 */     if (this.dh_listener_added)
/*     */     {
/* 226 */       dh_manager.removeListener(this);
/*     */       
/* 228 */       this.dh_listener_added = false;
/*     */     }
/*     */     
/* 231 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/* 239 */     super.skinObjectShown(skinObject, params);
/*     */     
/* 241 */     SWTSkinObjectTextbox soFilter = (SWTSkinObjectTextbox)getSkinObject("filterbox");
/*     */     
/* 243 */     if (soFilter != null)
/*     */     {
/* 245 */       this.txtFilter = soFilter.getTextControl();
/*     */     }
/*     */     
/* 248 */     SWTSkinObject so_list = getSkinObject("dl-history-list");
/*     */     
/* 250 */     if (so_list != null)
/*     */     {
/* 252 */       initTable((Composite)so_list.getControl());
/*     */     }
/*     */     else
/*     */     {
/* 256 */       System.out.println("NO dl-history-list");
/*     */       
/* 258 */       return null;
/*     */     }
/*     */     
/* 261 */     if (this.tv == null)
/*     */     {
/* 263 */       return null;
/*     */     }
/*     */     
/* 266 */     if (dh_manager != null)
/*     */     {
/* 268 */       dh_manager.addListener(this, true);
/*     */       
/* 270 */       this.dh_listener_added = true;
/*     */     }
/*     */     
/* 273 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 282 */     if (this.dh_listener_added)
/*     */     {
/* 284 */       dh_manager.removeListener(this);
/*     */       
/* 286 */       this.dh_listener_added = false;
/*     */     }
/*     */     
/* 289 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initTable(Composite control)
/*     */   {
/* 297 */     if (this.tv == null)
/*     */     {
/* 299 */       this.tv = TableViewFactory.createTableViewSWT(DownloadHistory.class, "DownloadHistory", "DownloadHistory", new TableColumnCore[0], ColumnDLHistoryName.COLUMN_ID, 268500994);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 305 */       if (this.txtFilter != null)
/*     */       {
/* 307 */         this.tv.enableFilterCheck(this.txtFilter, this);
/*     */       }
/*     */       
/* 310 */       this.tv.setRowDefaultHeightEM(1.0F);
/*     */       
/* 312 */       this.tv.setEnableTabViews(true, true, null);
/*     */       
/* 314 */       this.table_parent = new Composite(control, 2048);
/*     */       
/* 316 */       this.table_parent.setLayoutData(Utils.getFilledFormData());
/*     */       
/* 318 */       GridLayout layout = new GridLayout();
/*     */       
/* 320 */       layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/*     */       
/* 322 */       this.table_parent.setLayout(layout);
/*     */       
/* 324 */       this.tv.addMenuFillListener(this);
/* 325 */       this.tv.addSelectionListener(this, false);
/*     */       
/* 327 */       this.tv.initialize(this.table_parent);
/*     */       
/* 329 */       this.tv.addCountChangeListener(new TableCountChangeListener()
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
/* 342 */           if (SBC_DownloadHistoryView.this.datasource == row.getDataSource())
/*     */           {
/* 344 */             SBC_DownloadHistoryView.this.tv.setSelectedRows(new TableRowCore[] { row });
/*     */           }
/*     */         }
/*     */       });
/*     */       
/* 349 */       if (dh_manager == null)
/*     */       {
/* 351 */         control.setEnabled(false);
/*     */       }
/*     */     }
/*     */     
/* 355 */     control.layout(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 364 */     if ((this.tv == null) || (!this.tv.isVisible()) || (dh_manager == null))
/*     */     {
/* 366 */       return false;
/*     */     }
/*     */     
/* 369 */     List<Object> datasources = this.tv.getSelectedDataSources();
/*     */     
/* 371 */     if (datasources.size() > 0)
/*     */     {
/* 373 */       List<DownloadHistory> dms = new ArrayList(datasources.size());
/*     */       
/* 375 */       for (Object o : datasources)
/*     */       {
/* 377 */         dms.add((DownloadHistory)o);
/*     */       }
/*     */       
/* 380 */       String id = item.getID();
/*     */       
/* 382 */       if (id.equals("remove"))
/*     */       {
/* 384 */         dh_manager.removeHistory(dms);
/*     */       }
/* 386 */       else if (id.equals("startstop"))
/*     */       {
/* 388 */         for (DownloadHistory download : dms)
/*     */         {
/* 390 */           download.setRedownloading();
/*     */           
/* 392 */           String magnet = UrlUtils.getMagnetURI(download.getTorrentHash(), download.getName(), null);
/*     */           
/* 394 */           TorrentOpener.openTorrent(magnet);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 399 */       return true;
/*     */     }
/*     */     
/* 402 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 410 */     if ((this.tv == null) || (!this.tv.isVisible()) || (dh_manager == null))
/*     */     {
/* 412 */       return;
/*     */     }
/*     */     
/* 415 */     boolean canEnable = false;
/* 416 */     boolean canStart = false;
/*     */     
/* 418 */     Object[] datasources = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 420 */     if (datasources.length > 0)
/*     */     {
/* 422 */       canEnable = true;
/* 423 */       canStart = true;
/*     */     }
/*     */     
/* 426 */     list.put("remove", Long.valueOf(canEnable ? 1L : 0L));
/* 427 */     list.put("start", Long.valueOf(canStart ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateUI()
/*     */   {
/* 433 */     if (this.tv != null)
/*     */     {
/* 435 */       this.tv.refreshTable(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUpdateUIName()
/*     */   {
/* 442 */     return "DownloadHistory";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String columnName, Menu menu)
/*     */   {
/* 450 */     if (dh_manager != null)
/*     */     {
/* 452 */       new MenuItem(menu, 2);
/*     */       
/* 454 */       if (dh_manager.isEnabled())
/*     */       {
/*     */ 
/*     */ 
/* 458 */         MenuItem itemReset = new MenuItem(menu, 8);
/*     */         
/* 460 */         Messages.setLanguageText(itemReset, "label.reset.history");
/*     */         
/* 462 */         itemReset.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 467 */             SBC_DownloadHistoryView.this.resetHistory();
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 472 */         });
/* 473 */         MenuItem itemDisable = new MenuItem(menu, 8);
/*     */         
/* 475 */         Messages.setLanguageText(itemDisable, "label.disable.history");
/*     */         
/* 477 */         itemDisable.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 482 */             SBC_DownloadHistoryView.dh_manager.setEnabled(false);
/*     */           }
/*     */           
/*     */ 
/*     */         });
/*     */       }
/*     */       else
/*     */       {
/* 490 */         MenuItem itemEnable = new MenuItem(menu, 8);
/*     */         
/* 492 */         Messages.setLanguageText(itemEnable, "label.enable.history");
/*     */         
/* 494 */         itemEnable.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 499 */             SBC_DownloadHistoryView.dh_manager.setEnabled(true);
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 504 */       new MenuItem(menu, 2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 513 */     if (dh_manager != null)
/*     */     {
/* 515 */       if (dh_manager.isEnabled())
/*     */       {
/* 517 */         List<Object> ds = this.tv.getSelectedDataSources();
/*     */         
/* 519 */         final List<DownloadHistory> dms = new ArrayList(ds.size());
/*     */         
/* 521 */         for (Object o : ds)
/*     */         {
/* 523 */           dms.add((DownloadHistory)o);
/*     */         }
/*     */         
/* 526 */         boolean hasSelection = dms.size() > 0;
/*     */         
/*     */ 
/*     */ 
/* 530 */         final boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*     */         
/* 532 */         MenuItem itemExplore = new MenuItem(menu, 8);
/*     */         
/* 534 */         Messages.setLanguageText(itemExplore, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"));
/*     */         
/*     */ 
/* 537 */         itemExplore.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 542 */             for (DownloadHistory download : dms)
/*     */             {
/* 544 */               ManagerUtils.open(new File(download.getSaveLocation()), use_open_containing_folder);
/*     */             }
/*     */             
/*     */           }
/* 548 */         });
/* 549 */         itemExplore.setEnabled(hasSelection);
/*     */         
/*     */ 
/*     */ 
/* 553 */         MenuItem itemRedownload = new MenuItem(menu, 8);
/*     */         
/* 555 */         Messages.setLanguageText(itemRedownload, "label.redownload");
/*     */         
/* 557 */         itemRedownload.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 562 */             for (DownloadHistory download : dms)
/*     */             {
/* 564 */               download.setRedownloading();
/*     */               
/* 566 */               String magnet = UrlUtils.getMagnetURI(download.getTorrentHash(), download.getName(), null);
/*     */               
/* 568 */               TorrentOpener.openTorrent(magnet);
/*     */             }
/*     */             
/*     */           }
/* 572 */         });
/* 573 */         itemExplore.setEnabled(hasSelection);
/*     */         
/*     */ 
/* 576 */         MenuItem itemRemove = new MenuItem(menu, 8);
/* 577 */         Utils.setMenuItemImage(itemRemove, "delete");
/*     */         
/* 579 */         Messages.setLanguageText(itemRemove, "MySharesView.menu.remove");
/*     */         
/* 581 */         itemRemove.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 586 */             SBC_DownloadHistoryView.dh_manager.removeHistory(dms);
/*     */           }
/*     */           
/* 589 */         });
/* 590 */         itemRemove.setEnabled(hasSelection);
/*     */         
/* 592 */         new MenuItem(menu, 2);
/*     */         
/*     */ 
/*     */ 
/* 596 */         MenuItem itemReset = new MenuItem(menu, 8);
/*     */         
/* 598 */         Messages.setLanguageText(itemReset, "label.reset.history");
/*     */         
/* 600 */         itemReset.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 605 */             SBC_DownloadHistoryView.this.resetHistory();
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 610 */         });
/* 611 */         MenuItem itemDisable = new MenuItem(menu, 8);
/*     */         
/* 613 */         Messages.setLanguageText(itemDisable, "label.disable.history");
/*     */         
/* 615 */         itemDisable.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */           public void handleEvent(Event event)
/*     */           {
/* 620 */             SBC_DownloadHistoryView.dh_manager.setEnabled(false);
/*     */           }
/*     */           
/* 623 */         });
/* 624 */         new MenuItem(menu, 2);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void resetHistory()
/*     */   {
/* 632 */     MessageBoxShell mb = new MessageBoxShell(MessageText.getString("downloadhistoryview.reset.title"), MessageText.getString("downloadhistoryview.reset.text"));
/*     */     
/*     */ 
/*     */ 
/* 636 */     mb.setButtons(0, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, new Integer[] { Integer.valueOf(0), Integer.valueOf(1) });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 641 */     mb.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 643 */         if (result == 0) {
/* 644 */           SBC_DownloadHistoryView.dh_manager.resetHistory();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void selected(TableRowCore[] row)
/*     */   {
/* 654 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */     
/* 656 */     if (uiFunctions != null)
/*     */     {
/* 658 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void deselected(TableRowCore[] rows)
/*     */   {
/* 666 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */     
/* 668 */     if (uiFunctions != null)
/*     */     {
/* 670 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void focusChanged(TableRowCore focus)
/*     */   {
/* 678 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */     
/* 680 */     if (uiFunctions != null)
/*     */     {
/* 682 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void defaultSelected(TableRowCore[] rows, int stateMask) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadHistoryEventOccurred(DownloadHistoryEvent event)
/*     */   {
/* 697 */     int type = event.getEventType();
/*     */     
/* 699 */     List<DownloadHistory> dls = event.getHistory();
/*     */     
/* 701 */     if (type == 1)
/*     */     {
/* 703 */       this.tv.addDataSources(dls.toArray(new DownloadHistory[dls.size()]));
/*     */     }
/* 705 */     else if (type == 2)
/*     */     {
/* 707 */       this.tv.removeDataSources(dls.toArray(new DownloadHistory[dls.size()]));
/*     */     }
/*     */     else
/*     */     {
/* 711 */       for (DownloadHistory d : dls)
/*     */       {
/* 713 */         TableRowCore row = this.tv.getRow(d);
/*     */         
/* 715 */         if (row != null)
/*     */         {
/* 717 */           row.invalidate(true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseEnter(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseExit(TableRowCore row) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void filterSet(String filter) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean filterCheck(DownloadHistory ds, String filter, boolean regex)
/*     */   {
/*     */     Object o_name;
/*     */     
/*     */ 
/*     */     Object o_name;
/*     */     
/*     */ 
/* 749 */     if (filter.startsWith("t:"))
/*     */     {
/* 751 */       filter = filter.substring(2);
/*     */       
/* 753 */       byte[] hash = ds.getTorrentHash();
/*     */       
/* 755 */       List<String> names = new ArrayList();
/*     */       
/* 757 */       names.add(ByteFormatter.encodeString(hash));
/*     */       
/* 759 */       names.add(Base32.encode(hash));
/*     */       
/* 761 */       o_name = names;
/*     */     }
/*     */     else
/*     */     {
/* 765 */       o_name = ds.getName();
/*     */     }
/*     */     
/*     */ 
/* 769 */     String s = "\\Q" + filter.replaceAll("\\s*[|;]\\s*", "\\\\E|\\\\Q") + "\\E";
/*     */     
/* 771 */     boolean match_result = true;
/*     */     
/* 773 */     if ((regex) && (s.startsWith("!")))
/*     */     {
/* 775 */       s = s.substring(1);
/*     */       
/* 777 */       match_result = false;
/*     */     }
/*     */     
/* 780 */     Pattern pattern = RegExUtil.getCachedPattern("downloadhistoryview:search", s, 2);
/*     */     
/*     */     boolean bOurs;
/*     */     boolean bOurs;
/* 784 */     if ((o_name instanceof String))
/*     */     {
/* 786 */       bOurs = pattern.matcher((String)o_name).find() == match_result;
/*     */     }
/*     */     else
/*     */     {
/* 790 */       List<String> names = (List)o_name;
/*     */       
/*     */ 
/*     */ 
/* 794 */       bOurs = !match_result;
/*     */       
/* 796 */       for (String name : names)
/*     */       {
/* 798 */         if (pattern.matcher(name).find())
/*     */         {
/* 800 */           bOurs = match_result;
/*     */           
/* 802 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 807 */     return bOurs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params)
/*     */   {
/* 815 */     if ((params instanceof DownloadHistory))
/*     */     {
/* 817 */       if (this.tv != null)
/*     */       {
/* 819 */         TableRowCore row = this.tv.getRow((DownloadHistory)params);
/*     */         
/* 821 */         if (row != null)
/*     */         {
/* 823 */           this.tv.setSelectedRows(new TableRowCore[] { row });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 828 */     this.datasource = params;
/*     */     
/* 830 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_DownloadHistoryView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */