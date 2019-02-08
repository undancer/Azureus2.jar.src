/*     */ package com.aelitis.azureus.ui.swt.devices;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceListener;
/*     */ import com.aelitis.azureus.core.devices.DeviceOfflineDownload;
/*     */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloader;
/*     */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloaderListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.columns.torrent.ColumnThumbnail;
/*     */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnOD_Completion;
/*     */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnOD_Name;
/*     */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnOD_Remaining;
/*     */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnOD_Status;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.custom.StackLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
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
/*     */ public class SBC_DevicesODView
/*     */   extends SkinView
/*     */   implements UIUpdatable, UIPluginViewToolBarListener
/*     */ {
/*     */   public static final String TABLE_ID = "DevicesOD";
/*  74 */   private static boolean columnsAdded = false;
/*     */   
/*     */ 
/*     */   private DeviceOfflineDownloader device;
/*     */   
/*     */   private TableViewSWT<DeviceOfflineDownload> tv_downloads;
/*     */   
/*     */   private MdiEntrySWT mdi_entry;
/*     */   
/*     */   private Composite control_parent;
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/*  88 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void azureusCoreRunning(AzureusCore core)
/*     */       {
/*     */ 
/*  95 */         SBC_DevicesODView.this.initColumns(core);
/*     */       }
/*     */       
/*  98 */     });
/*  99 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*     */     
/* 101 */     if (mdi != null)
/*     */     {
/* 103 */       this.mdi_entry = mdi.getCurrentEntrySWT();
/*     */       
/* 105 */       this.mdi_entry.addToolbarEnabler(this);
/*     */       
/* 107 */       this.device = ((DeviceOfflineDownloader)this.mdi_entry.getDatasource());
/*     */     }
/*     */     
/* 110 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initColumns(AzureusCore core)
/*     */   {
/* 118 */     synchronized (SBC_DevicesODView.class)
/*     */     {
/* 120 */       if (columnsAdded)
/*     */       {
/* 122 */         return;
/*     */       }
/*     */       
/* 125 */       columnsAdded = true;
/*     */     }
/*     */     
/* 128 */     UIManager uiManager = PluginInitializer.getDefaultInterface().getUIManager();
/*     */     
/* 130 */     TableManager tableManager = uiManager.getTableManager();
/*     */     
/* 132 */     tableManager.registerColumn(DeviceOfflineDownload.class, "Thumbnail", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 136 */         new ColumnThumbnail(column);
/* 137 */         column.setWidth(70);
/*     */       }
/*     */       
/* 140 */     });
/* 141 */     tableManager.registerColumn(DeviceOfflineDownload.class, "od_name", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 145 */         new ColumnOD_Name(column);
/*     */       }
/*     */       
/* 148 */     });
/* 149 */     tableManager.registerColumn(DeviceOfflineDownload.class, "od_status", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 153 */         new ColumnOD_Status(column);
/*     */       }
/*     */       
/* 156 */     });
/* 157 */     tableManager.registerColumn(DeviceOfflineDownload.class, "od_completion", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 161 */         new ColumnOD_Completion(column);
/*     */       }
/*     */       
/* 164 */     });
/* 165 */     tableManager.registerColumn(DeviceOfflineDownload.class, "od_remaining", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column)
/*     */       {
/* 169 */         new ColumnOD_Remaining(column);
/*     */       }
/*     */       
/* 172 */     });
/* 173 */     TableColumnManager tcm = TableColumnManager.getInstance();
/* 174 */     TableColumnCore[] allTCs = tcm.getAllTableColumnCoreAsArray(DeviceOfflineDownload.class, "DevicesOD");
/*     */     
/*     */ 
/* 177 */     ArrayList<String> names = new ArrayList();
/* 178 */     for (int i = 0; i < allTCs.length; i++) {
/* 179 */       TableColumn tc = allTCs[i];
/* 180 */       if (tc.isVisible()) {
/* 181 */         names.add(tc.getName());
/*     */       }
/*     */     }
/* 184 */     tcm.setDefaultColumnNames("DevicesOD", (String[])names.toArray(new String[0]));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/* 192 */     super.skinObjectShown(skinObject, params);
/*     */     
/* 194 */     SWTSkinObject so_list = getSkinObject("devicesod-list");
/*     */     
/* 196 */     if (so_list != null)
/*     */     {
/* 198 */       initTable((Composite)so_list.getControl());
/*     */     }
/*     */     
/* 201 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 209 */     synchronized (this)
/*     */     {
/* 211 */       if (this.tv_downloads != null)
/*     */       {
/* 213 */         this.tv_downloads.delete();
/*     */         
/* 215 */         this.tv_downloads = null;
/*     */       }
/*     */     }
/*     */     
/* 219 */     Utils.disposeSWTObjects(new Object[] { this.control_parent });
/*     */     
/*     */ 
/*     */ 
/* 223 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 231 */     synchronized (this)
/*     */     {
/* 233 */       if (this.tv_downloads != null)
/*     */       {
/* 235 */         this.tv_downloads.delete();
/*     */         
/* 237 */         this.tv_downloads = null;
/*     */       }
/*     */     }
/*     */     
/* 241 */     Utils.disposeSWTObjects(new Object[] { this.control_parent });
/*     */     
/*     */ 
/*     */ 
/* 245 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void initTable(final Composite control)
/*     */   {
/* 252 */     this.control_parent = new Composite(control, 0);
/* 253 */     Utils.setLayoutData(this.control_parent, Utils.getFilledFormData());
/*     */     
/* 255 */     final StackLayout stack_layout = new StackLayout();
/*     */     
/* 257 */     this.control_parent.setLayout(stack_layout);
/*     */     
/*     */ 
/*     */ 
/* 261 */     final Composite enabled_device_parent = new Composite(this.control_parent, 0);
/*     */     
/* 263 */     GridLayout layout = new GridLayout();
/* 264 */     layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/* 265 */     enabled_device_parent.setLayout(layout);
/*     */     
/* 267 */     this.tv_downloads = TableViewFactory.createTableViewSWT(DeviceOfflineDownload.class, "DevicesOD", "DevicesOD", new TableColumnCore[0], "od_name", 268500994);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 275 */     this.tv_downloads.setRowDefaultHeightEM(3.0F);
/* 276 */     this.tv_downloads.setHeaderVisible(true);
/*     */     
/* 278 */     this.tv_downloads.addSelectionListener(new TableSelectionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void selected(TableRowCore[] row)
/*     */       {
/*     */ 
/* 285 */         refreshIconBar();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void mouseExit(TableRowCore row) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void mouseEnter(TableRowCore row) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void focusChanged(TableRowCore focus)
/*     */       {
/* 304 */         refreshIconBar();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void deselected(TableRowCore[] rows)
/*     */       {
/* 311 */         refreshIconBar();
/*     */       }
/*     */       
/*     */ 
/*     */       public void defaultSelected(TableRowCore[] rows, int stateMask)
/*     */       {
/* 317 */         refreshIconBar();
/*     */       }
/*     */       
/*     */ 
/*     */       protected void refreshIconBar()
/*     */       {
/* 323 */         SelectedContentManager.clearCurrentlySelectedContent();
/*     */         
/* 325 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 326 */         if (uiFunctions != null)
/* 327 */           uiFunctions.refreshIconBar(); } }, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 332 */     this.tv_downloads.addLifeCycleListener(new TableLifeCycleListener()
/*     */     {
/*     */ 
/* 335 */       private final TableViewSWT<DeviceOfflineDownload> f_table = SBC_DevicesODView.this.tv_downloads;
/*     */       
/* 337 */       private Set<DeviceOfflineDownload> download_set = new HashSet();
/*     */       
/*     */       private boolean destroyed;
/*     */       
/* 341 */       private DeviceOfflineDownloaderListener od_listener = new DeviceOfflineDownloaderListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void downloadAdded(final DeviceOfflineDownload download)
/*     */         {
/*     */ 
/* 348 */           synchronized (SBC_DevicesODView.8.this.download_set)
/*     */           {
/* 350 */             if (SBC_DevicesODView.8.this.destroyed)
/*     */             {
/* 352 */               return;
/*     */             }
/*     */             
/* 355 */             if (SBC_DevicesODView.8.this.download_set.contains(download))
/*     */             {
/* 357 */               return;
/*     */             }
/*     */             
/* 360 */             SBC_DevicesODView.8.this.download_set.add(download);
/*     */           }
/*     */           
/* 363 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 369 */               if ((SBC_DevicesODView.this.tv_downloads == SBC_DevicesODView.8.this.f_table) && (!SBC_DevicesODView.8.this.f_table.isDisposed()))
/*     */               {
/* 371 */                 synchronized (SBC_DevicesODView.8.this.download_set)
/*     */                 {
/* 373 */                   if (SBC_DevicesODView.8.this.destroyed)
/*     */                   {
/* 375 */                     return;
/*     */                   }
/*     */                 }
/*     */                 
/* 379 */                 SBC_DevicesODView.8.this.f_table.addDataSources(new DeviceOfflineDownload[] { download });
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void downloadChanged(final DeviceOfflineDownload download)
/*     */         {
/* 389 */           synchronized (SBC_DevicesODView.8.this.download_set)
/*     */           {
/* 391 */             if (SBC_DevicesODView.8.this.destroyed)
/*     */             {
/* 393 */               return;
/*     */             }
/*     */             
/* 396 */             if (!SBC_DevicesODView.8.this.download_set.contains(download))
/*     */             {
/* 398 */               return;
/*     */             }
/*     */           }
/*     */           
/* 402 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 408 */               if ((SBC_DevicesODView.this.tv_downloads == SBC_DevicesODView.8.this.f_table) && (!SBC_DevicesODView.8.this.f_table.isDisposed()))
/*     */               {
/* 410 */                 synchronized (SBC_DevicesODView.8.this.download_set)
/*     */                 {
/* 412 */                   if (SBC_DevicesODView.8.this.destroyed)
/*     */                   {
/* 414 */                     return;
/*     */                   }
/*     */                 }
/*     */                 
/* 418 */                 TableRowCore row = SBC_DevicesODView.8.this.f_table.getRow(download);
/*     */                 
/* 420 */                 if (row != null)
/*     */                 {
/* 422 */                   row.refresh(true);
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void downloadRemoved(final DeviceOfflineDownload download)
/*     */         {
/* 433 */           synchronized (SBC_DevicesODView.8.this.download_set)
/*     */           {
/* 435 */             if (SBC_DevicesODView.8.this.destroyed)
/*     */             {
/* 437 */               return;
/*     */             }
/*     */             
/* 440 */             if (!SBC_DevicesODView.8.this.download_set.remove(download))
/*     */             {
/* 442 */               return;
/*     */             }
/*     */           }
/*     */           
/* 446 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 452 */               if ((SBC_DevicesODView.this.tv_downloads == SBC_DevicesODView.8.this.f_table) && (!SBC_DevicesODView.8.this.f_table.isDisposed()))
/*     */               {
/* 454 */                 synchronized (SBC_DevicesODView.8.this.download_set)
/*     */                 {
/* 456 */                   if (SBC_DevicesODView.8.this.destroyed)
/*     */                   {
/* 458 */                     return;
/*     */                   }
/*     */                 }
/*     */                 
/* 462 */                 SBC_DevicesODView.8.this.f_table.removeDataSources(new DeviceOfflineDownload[] { download });
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       };
/*     */       
/*     */ 
/*     */       public void tableViewInitialized()
/*     */       {
/* 472 */         SBC_DevicesODView.this.device.addListener(this.od_listener);
/*     */         
/* 474 */         DeviceOfflineDownload[] downloads = SBC_DevicesODView.this.device.getDownloads();
/*     */         
/* 476 */         final ArrayList<DeviceOfflineDownload> new_downloads = new ArrayList(downloads.length);
/*     */         
/* 478 */         synchronized (this.download_set)
/*     */         {
/* 480 */           if (this.destroyed)
/*     */           {
/* 482 */             return;
/*     */           }
/*     */           
/* 485 */           for (DeviceOfflineDownload download : downloads)
/*     */           {
/* 487 */             if (!this.download_set.contains(download))
/*     */             {
/* 489 */               this.download_set.add(download);
/*     */               
/* 491 */               new_downloads.add(download);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 496 */         if (new_downloads.size() > 0)
/*     */         {
/* 498 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 504 */               if ((SBC_DevicesODView.this.tv_downloads == SBC_DevicesODView.8.this.f_table) && (!SBC_DevicesODView.8.this.f_table.isDisposed()))
/*     */               {
/* 506 */                 synchronized (SBC_DevicesODView.8.this.download_set)
/*     */                 {
/* 508 */                   if (SBC_DevicesODView.8.this.destroyed)
/*     */                   {
/* 510 */                     return;
/*     */                   }
/*     */                 }
/*     */                 
/* 514 */                 SBC_DevicesODView.8.this.f_table.addDataSources(new_downloads.toArray(new DeviceOfflineDownload[new_downloads.size()]));
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void tableViewDestroyed()
/*     */       {
/* 524 */         SBC_DevicesODView.this.device.removeListener(this.od_listener);
/*     */         
/* 526 */         synchronized (this.download_set)
/*     */         {
/* 528 */           this.destroyed = true;
/*     */           
/* 530 */           this.download_set.clear();
/*     */         }
/*     */         
/*     */       }
/* 534 */     });
/* 535 */     this.tv_downloads.initialize(enabled_device_parent);
/*     */     
/*     */ 
/*     */ 
/* 539 */     final Composite disabled_device_parent = new Composite(this.control_parent, 0);
/*     */     
/* 541 */     layout = new GridLayout();
/* 542 */     layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/* 543 */     disabled_device_parent.setLayout(layout);
/*     */     
/* 545 */     Label l = new Label(disabled_device_parent, 0);
/* 546 */     GridData grid_data = new GridData(768);
/* 547 */     grid_data.horizontalIndent = 5;
/* 548 */     Utils.setLayoutData(l, grid_data);
/*     */     
/* 550 */     l.setText(MessageText.getString("device.is.disabled"));
/*     */     
/* 552 */     this.device.addListener(new DeviceListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void deviceChanged(Device d)
/*     */       {
/*     */ 
/* 559 */         Composite x = SBC_DevicesODView.this.device.isEnabled() ? enabled_device_parent : disabled_device_parent;
/*     */         
/* 561 */         if (x.isDisposed())
/*     */         {
/* 563 */           SBC_DevicesODView.this.device.removeListener(this);
/*     */ 
/*     */ 
/*     */         }
/* 567 */         else if (x != stack_layout.topControl)
/*     */         {
/* 569 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 575 */               Composite x = SBC_DevicesODView.this.device.isEnabled() ? SBC_DevicesODView.9.this.val$enabled_device_parent : SBC_DevicesODView.9.this.val$disabled_device_parent;
/*     */               
/* 577 */               if ((!x.isDisposed()) && (x != SBC_DevicesODView.9.this.val$stack_layout.topControl))
/*     */               {
/* 579 */                 SBC_DevicesODView.9.this.val$stack_layout.topControl = x;
/*     */                 
/* 581 */                 SBC_DevicesODView.9.this.val$control.layout(true, true);
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           });
/*     */         }
/*     */       }
/* 589 */     });
/* 590 */     stack_layout.topControl = (this.device.isEnabled() ? enabled_device_parent : disabled_device_parent);
/*     */     
/* 592 */     control.layout(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 599 */     long stateRemove = 0L;
/* 600 */     if ((this.tv_downloads != null) && (this.tv_downloads.getSelectedRowsSize() > 0)) {
/* 601 */       stateRemove = 1L;
/*     */     }
/* 603 */     list.put("remove", Long.valueOf(stateRemove));
/*     */   }
/*     */   
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 608 */     if (item.getID().equals("remove")) {
/* 609 */       MessageBoxShell mb = new MessageBoxShell(MessageText.getString("message.confirm.delete.title"), MessageText.getString("message.confirm.delete.text", new String[] { this.device.getName() }), new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 1);
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
/* 622 */       mb.open(new UserPrompterResultListener() {
/*     */         public void prompterClosed(int result) {
/* 624 */           if (result == 0) {
/* 625 */             SBC_DevicesODView.this.device.remove();
/*     */           }
/*     */         }
/* 628 */       });
/* 629 */       return true;
/*     */     }
/* 631 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUpdateUIName()
/*     */   {
/* 637 */     return "DevicesODView";
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateUI()
/*     */   {
/* 643 */     if (this.tv_downloads != null)
/*     */     {
/* 645 */       this.tv_downloads.refreshTable(false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/SBC_DevicesODView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */