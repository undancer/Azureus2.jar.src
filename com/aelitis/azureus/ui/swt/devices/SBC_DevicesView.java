/*      */ package com.aelitis.azureus.ui.swt.devices;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.devices.Device;
/*      */ import com.aelitis.azureus.core.devices.DeviceListener;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager;
/*      */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*      */ import com.aelitis.azureus.core.devices.TranscodeActionVetoException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*      */ import com.aelitis.azureus.core.devices.TranscodeManager;
/*      */ import com.aelitis.azureus.core.devices.TranscodeQueue;
/*      */ import com.aelitis.azureus.core.devices.TranscodeQueueListener;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTarget;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTargetListener;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctions.TagReturner;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*      */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Category;
/*      */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_CopiedToDevice;
/*      */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Device;
/*      */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Name;
/*      */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Rank;
/*      */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Resolution;
/*      */ import com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Status;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*      */ import com.aelitis.azureus.ui.swt.utils.TagUIUtilsV3;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.InfoBarUtil;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*      */ import java.io.File;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import org.eclipse.swt.dnd.DragSource;
/*      */ import org.eclipse.swt.dnd.DragSourceAdapter;
/*      */ import org.eclipse.swt.dnd.DragSourceEvent;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.dnd.DropTargetEvent;
/*      */ import org.eclipse.swt.dnd.TextTransfer;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*      */ import org.gudy.azureus2.ui.swt.CategoryAdderWindow;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.URLTransfer;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*      */ 
/*      */ public class SBC_DevicesView extends SkinView implements TranscodeQueueListener, UIUpdatable, TranscodeTargetListener, DeviceListener, org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener
/*      */ {
/*      */   public static final String TABLE_DEVICES = "Devices";
/*      */   public static final String TABLE_TRANSCODE_QUEUE = "TranscodeQueue";
/*      */   public static final String TABLE_DEVICE_LIBRARY = "DeviceLibrary";
/*  103 */   private static boolean columnsAdded = false;
/*      */   
/*      */   private DeviceManager device_manager;
/*      */   
/*      */   private TranscodeManager transcode_manager;
/*      */   
/*      */   private TranscodeQueue transcode_queue;
/*      */   
/*      */   private TableViewSWT<?> tvDevices;
/*      */   private DragSource dragSource;
/*      */   private DropTarget dropTarget;
/*  114 */   private int drag_drop_line_start = -1;
/*      */   
/*      */   private TableRowCore[] drag_drop_rows;
/*      */   
/*      */   private TableViewSWT<TranscodeFile> tvFiles;
/*      */   
/*      */   private MdiEntrySWT mdiEntry;
/*      */   
/*      */   private Composite tableJobsParent;
/*      */   
/*      */   private Device device;
/*      */   
/*      */   private String device_name;
/*      */   private TranscodeTarget transTarget;
/*      */   
/*      */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*      */   {
/*  131 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  133 */         SBC_DevicesView.this.initColumns(core);
/*      */       }
/*      */       
/*  136 */     });
/*  137 */     this.device_manager = com.aelitis.azureus.core.devices.DeviceManagerFactory.getSingleton();
/*      */     
/*  139 */     this.transcode_manager = this.device_manager.getTranscodeManager();
/*      */     
/*  141 */     this.transcode_queue = this.transcode_manager.getQueue();
/*      */     
/*  143 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  144 */     if (mdi != null) {
/*  145 */       this.mdiEntry = mdi.getCurrentEntrySWT();
/*  146 */       Object ds = this.mdiEntry.getDatasource();
/*  147 */       if (!(ds instanceof Device)) {
/*  148 */         return null;
/*      */       }
/*  150 */       this.device = ((Device)ds);
/*      */     }
/*      */     
/*  153 */     if ((this.device instanceof TranscodeTarget)) {
/*  154 */       this.transTarget = ((TranscodeTarget)this.device);
/*      */     }
/*      */     
/*  157 */     if (this.device == null) {
/*  158 */       new InfoBarUtil(skinObject, "devicesview.infobar", false, "DeviceView.infobar", "v3.deviceview.infobar")
/*      */       {
/*      */         public boolean allowShow() {
/*  161 */           return true;
/*      */         }
/*      */       };
/*  164 */     } else if ((this.device instanceof DeviceMediaRenderer)) {
/*  165 */       DeviceMediaRenderer renderer = (DeviceMediaRenderer)this.device;
/*  166 */       int species = renderer.getRendererSpecies();
/*  167 */       String speciesID = null;
/*  168 */       switch (species) {
/*      */       case 3: 
/*  170 */         speciesID = "itunes";
/*  171 */         break;
/*      */       case 1: 
/*  173 */         speciesID = "ps3";
/*  174 */         break;
/*      */       case 2: 
/*  176 */         speciesID = "xbox";
/*  177 */         break;
/*      */       case 6: 
/*  179 */         String classification = renderer.getClassification();
/*      */         
/*  181 */         if (classification.equals("sony.PSP")) {
/*  182 */           speciesID = "psp";
/*  183 */         } else if (classification.startsWith("tivo.")) {
/*  184 */           speciesID = "tivo";
/*  185 */         } else if (classification.toLowerCase().contains("android")) {
/*  186 */           speciesID = "android";
/*      */         }
/*      */         
/*      */         break;
/*      */       }
/*      */       
/*      */       
/*  193 */       if (speciesID != null) {
/*  194 */         final String fSpeciesID = speciesID;
/*  195 */         new InfoBarUtil(skinObject, "devicesview.infobar", false, "DeviceView.infobar." + speciesID, "v3.deviceview.infobar")
/*      */         {
/*      */           public boolean allowShow() {
/*  198 */             return true;
/*      */           }
/*      */           
/*      */           protected void created(SWTSkinObject parent)
/*      */           {
/*  203 */             SWTSkinObjectText soLine1 = (SWTSkinObjectText)SBC_DevicesView.this.skin.getSkinObject("line1", parent);
/*      */             
/*  205 */             soLine1.setTextID("v3.deviceview.infobar.line1.generic", new String[] { SBC_DevicesView.this.device.getName() });
/*      */             
/*      */ 
/*      */ 
/*  209 */             SWTSkinObjectText soLine2 = (SWTSkinObjectText)SBC_DevicesView.this.skin.getSkinObject("line2", parent);
/*      */             
/*  211 */             soLine2.setTextID("v3.deviceview.infobar.line2." + fSpeciesID);
/*      */           }
/*      */         };
/*      */       }
/*      */     }
/*      */     
/*  217 */     SWTSkinObject soAdvInfo = getSkinObject("advinfo");
/*  218 */     if (soAdvInfo != null) {
/*  219 */       initAdvInfo(soAdvInfo);
/*      */     }
/*      */     
/*  222 */     if (this.device != null) {
/*  223 */       this.device_name = this.device.getName();
/*      */       
/*  225 */       SWTSkinObject soTitle = getSkinObject("title");
/*  226 */       if ((soTitle instanceof SWTSkinObjectText)) {
/*  227 */         ((SWTSkinObjectText)soTitle).setTextID("device.view.heading", new String[] { this.device_name });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  234 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initColumns(AzureusCore core)
/*      */   {
/*  243 */     if (columnsAdded) {
/*  244 */       return;
/*      */     }
/*  246 */     columnsAdded = true;
/*  247 */     UIManager uiManager = org.gudy.azureus2.pluginsimpl.local.PluginInitializer.getDefaultInterface().getUIManager();
/*  248 */     TableManager tableManager = uiManager.getTableManager();
/*  249 */     tableManager.registerColumn(TranscodeFile.class, "trancode_qpos", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  252 */         new ColumnTJ_Rank(column);
/*  253 */         if (!column.getTableID().equals("TranscodeQueue")) {
/*  254 */           column.setVisible(false);
/*      */         }
/*      */       }
/*  257 */     });
/*  258 */     tableManager.registerColumn(TranscodeFile.class, "Thumbnail", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  261 */         new com.aelitis.azureus.ui.swt.columns.torrent.ColumnThumbnail(column);
/*  262 */         column.setWidth(70);
/*  263 */         column.setVisible(false);
/*      */       }
/*  265 */     });
/*  266 */     tableManager.registerColumn(TranscodeFile.class, "transcode_name", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  269 */         new ColumnTJ_Name(column);
/*  270 */         if (column.getTableID().equals("TranscodeQueue")) {
/*  271 */           column.setWidth(200);
/*  272 */         } else if (!column.getTableID().endsWith(":type=1")) {
/*  273 */           column.setWidth(140);
/*      */         }
/*      */       }
/*  276 */     });
/*  277 */     tableManager.registerColumn(TranscodeFile.class, "duration", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  280 */         new com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Duration(column);
/*      */       }
/*  282 */     });
/*  283 */     tableManager.registerColumn(TranscodeFile.class, "device", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  286 */         new ColumnTJ_Device(column);
/*  287 */         column.setVisible(false);
/*      */       }
/*  289 */     });
/*  290 */     tableManager.registerColumn(TranscodeFile.class, "profile", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  293 */         new com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Profile(column);
/*  294 */         if (column.getTableID().equals("TranscodeQueue")) {
/*  295 */           column.setWidth(70);
/*      */         }
/*      */         
/*      */       }
/*  299 */     });
/*  300 */     tableManager.registerColumn(TranscodeFile.class, "resolution", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  303 */         new ColumnTJ_Resolution(column);
/*  304 */         column.setVisible(false);
/*  305 */         if (column.getTableID().equals("TranscodeQueue")) {
/*  306 */           column.setWidth(95);
/*      */         }
/*      */         
/*      */       }
/*  310 */     });
/*  311 */     tableManager.registerColumn(TranscodeFile.class, "transcode_status", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  314 */         new ColumnTJ_Status(column);
/*      */       }
/*  316 */     });
/*  317 */     tableManager.registerColumn(TranscodeFile.class, "trancode_completion", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  320 */         new com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Completion(column);
/*  321 */         column.setWidth(145);
/*      */       }
/*  323 */     });
/*  324 */     tableManager.registerColumn(TranscodeFile.class, "copied", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  327 */         new ColumnTJ_CopiedToDevice(column);
/*      */         
/*  329 */         if ((column.getTableID().endsWith(":type=1")) || (column.getTableID().equals("TranscodeQueue")))
/*      */         {
/*      */ 
/*  332 */           column.setVisible(false);
/*      */         }
/*      */         
/*      */       }
/*  336 */     });
/*  337 */     tableManager.registerColumn(TranscodeFile.class, "category", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  340 */         new ColumnTJ_Category(column);
/*      */       }
/*      */       
/*  343 */     });
/*  344 */     tableManager.registerColumn(TranscodeFile.class, "tags", new TableColumnCreationListener()
/*      */     {
/*      */       public void tableColumnCreated(TableColumn column) {
/*  347 */         new com.aelitis.azureus.ui.swt.devices.columns.ColumnTJ_Tags(column);
/*      */       }
/*      */       
/*  350 */     });
/*  351 */     TableColumnManager tcm = TableColumnManager.getInstance();
/*  352 */     String[] defaultLibraryColumns = { "trancode_qpos", "transcode_name", "duration", "device", "transcode_status", "trancode_completion" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  360 */     tcm.setDefaultColumnNames("TranscodeQueue", defaultLibraryColumns);
/*      */     
/*  362 */     String[] defaultQColumns = { "transcode_name", "duration", "profile", "transcode_status", "trancode_completion" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  369 */     tcm.setDefaultColumnNames("DeviceLibrary", defaultQColumns);
/*      */   }
/*      */   
/*      */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*      */   {
/*  374 */     super.skinObjectShown(skinObject, params);
/*      */     
/*  376 */     this.transcode_queue.addListener(this);
/*      */     
/*  378 */     if (this.transTarget != null) {
/*  379 */       this.transTarget.addListener(this);
/*      */     }
/*      */     
/*  382 */     SWTSkinObject soDeviceList = getSkinObject("device-list");
/*  383 */     if (soDeviceList != null) {
/*  384 */       initDeviceListTable((Composite)soDeviceList.getControl());
/*      */     }
/*      */     
/*  387 */     SWTSkinObject soTranscodeQueue = getSkinObject("transcode-queue");
/*  388 */     if (soTranscodeQueue != null) {
/*  389 */       initTranscodeQueueTable((Composite)soTranscodeQueue.getControl());
/*      */     }
/*      */     
/*  392 */     if (this.device != null)
/*      */     {
/*  394 */       this.device.addListener(this);
/*      */     }
/*      */     
/*  397 */     if ((this.device instanceof TranscodeTarget))
/*      */     {
/*  399 */       createDragDrop(this.tvFiles != null ? this.tvFiles : this.tvDevices);
/*      */     }
/*      */     
/*  402 */     setAdditionalInfoTitle(false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  414 */     updateSelectedContent();
/*      */     
/*  416 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initAdvInfo(SWTSkinObject soAdvInfo)
/*      */   {
/*  425 */     SWTSkinButtonUtility btnAdvInfo = new SWTSkinButtonUtility(soAdvInfo);
/*  426 */     btnAdvInfo.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*      */     {
/*      */       public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/*  429 */         SWTSkinObject soArea = SBC_DevicesView.this.getSkinObject("advinfo-area");
/*  430 */         if (soArea != null) {
/*  431 */           boolean newVisibility = !soArea.isVisible();
/*  432 */           SBC_DevicesView.this.setAdditionalInfoTitle(newVisibility);
/*      */         }
/*      */       }
/*  435 */     });
/*  436 */     setAdditionalInfoTitle(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setAdditionalInfoTitle(boolean newVisibility)
/*      */   {
/*  445 */     SWTSkinObject soArea = getSkinObject("advinfo-area");
/*  446 */     if (soArea != null) {
/*  447 */       soArea.setVisible(newVisibility);
/*      */     }
/*  449 */     SWTSkinObject soText = getSkinObject("advinfo-title");
/*  450 */     if ((soText instanceof SWTSkinObjectText)) {
/*  451 */       String s = newVisibility ? "[-]" : "[+]";
/*  452 */       if (this.device != null) {
/*  453 */         s = s + "Additional Device Info and Settings";
/*      */       } else {
/*  455 */         s = s + "General Options";
/*      */       }
/*  457 */       ((SWTSkinObjectText)soText).setText(s);
/*      */     }
/*      */   }
/*      */   
/*      */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*      */   {
/*  463 */     this.transcode_queue.removeListener(this);
/*      */     
/*  465 */     if (this.transTarget != null) {
/*  466 */       this.transTarget.removeListener(this);
/*      */     }
/*      */     
/*  469 */     if (this.device != null)
/*      */     {
/*  471 */       this.device.removeListener(this);
/*      */     }
/*      */     
/*  474 */     synchronized (this) {
/*  475 */       if (this.tvFiles != null) {
/*  476 */         this.tvFiles.delete();
/*  477 */         this.tvFiles = null;
/*      */       }
/*      */     }
/*  480 */     Utils.disposeSWTObjects(new Object[] { this.tableJobsParent, this.dropTarget, this.dragSource });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  485 */     if (this.tvDevices != null) {
/*  486 */       this.tvDevices.delete();
/*  487 */       this.tvDevices = null;
/*      */     }
/*      */     
/*  490 */     return super.skinObjectHidden(skinObject, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void initTranscodeQueueTable(Composite control)
/*      */   {
/*      */     String tableID;
/*      */     
/*      */     String tableID;
/*      */     
/*  501 */     if (this.device == null)
/*      */     {
/*  503 */       tableID = "TranscodeQueue";
/*      */     }
/*      */     else
/*      */     {
/*  507 */       tableID = "DeviceLibrary";
/*      */       
/*  509 */       if ((this.device instanceof DeviceMediaRenderer))
/*      */       {
/*  511 */         DeviceMediaRenderer dmr = (DeviceMediaRenderer)this.device;
/*      */         
/*  513 */         if ((!dmr.canCopyToDevice()) && (!dmr.canCopyToFolder()))
/*      */         {
/*  515 */           tableID = tableID + ":type=1";
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  520 */     this.tvFiles = TableViewFactory.createTableViewSWT(TranscodeFile.class, tableID, tableID, new TableColumnCore[0], this.device == null ? "trancode_qpos" : "transcode_status", 268500994);
/*      */     
/*      */ 
/*      */ 
/*  524 */     this.tvFiles.setRowDefaultHeightEM(1.5F);
/*  525 */     this.tvFiles.setHeaderVisible(true);
/*  526 */     this.tvFiles.setParentDataSource(this.device);
/*      */     
/*  528 */     this.tableJobsParent = new Composite(control, 0);
/*  529 */     this.tableJobsParent.setLayoutData(Utils.getFilledFormData());
/*  530 */     GridLayout layout = new GridLayout();
/*  531 */     layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/*  532 */     this.tableJobsParent.setLayout(layout);
/*      */     
/*  534 */     this.tvFiles.addSelectionListener(new TableSelectionListener()
/*      */     {
/*      */       public void selected(TableRowCore[] row) {
/*  537 */         SBC_DevicesView.this.updateSelectedContent();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void mouseExit(TableRowCore row) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void mouseEnter(TableRowCore row) {}
/*      */       
/*      */       public void focusChanged(TableRowCore focus) {}
/*      */       
/*  550 */       public void deselected(TableRowCore[] rows) { SBC_DevicesView.this.updateSelectedContent(); } public void defaultSelected(TableRowCore[] rows, int stateMask) {} }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  557 */     this.tvFiles.addLifeCycleListener(new TableLifeCycleListener() {
/*      */       public void tableViewInitialized() {
/*  559 */         if (SBC_DevicesView.this.transTarget == null)
/*      */         {
/*  561 */           TranscodeJob[] jobs = SBC_DevicesView.this.transcode_queue.getJobs();
/*  562 */           for (TranscodeJob job : jobs) {
/*  563 */             TranscodeFile file = job.getTranscodeFile();
/*  564 */             if (file != null) {
/*  565 */               SBC_DevicesView.this.tvFiles.addDataSource(file);
/*      */             }
/*      */           }
/*      */         } else {
/*  569 */           SBC_DevicesView.this.tvFiles.addDataSources(SBC_DevicesView.this.transTarget.getFiles());
/*      */         }
/*  571 */         SBC_DevicesView.this.updateSelectedContent();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void tableViewDestroyed() {}
/*  577 */     });
/*  578 */     this.tvFiles.addMenuFillListener(new org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener() {
/*      */       public void fillMenu(String sColumnName, Menu menu) {
/*  580 */         SBC_DevicesView.this.fillMenu(menu);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*  586 */     });
/*  587 */     this.tvFiles.addKeyListener(new org.eclipse.swt.events.KeyListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void keyPressed(KeyEvent e)
/*      */       {
/*      */ 
/*  594 */         if ((e.stateMask == 0) && (e.keyCode == 127))
/*      */         {
/*      */           TranscodeFile[] selected;
/*      */           
/*  598 */           synchronized (this) {
/*      */             TranscodeFile[] selected;
/*  600 */             if (SBC_DevicesView.this.tvFiles == null)
/*      */             {
/*  602 */               selected = new TranscodeFile[0];
/*      */             }
/*      */             else
/*      */             {
/*  606 */               List<Object> selectedDataSources = SBC_DevicesView.this.tvFiles.getSelectedDataSources();
/*  607 */               selected = (TranscodeFile[])selectedDataSources.toArray(new TranscodeFile[0]);
/*      */             }
/*      */           }
/*      */           
/*  611 */           if (selected.length > 0)
/*      */           {
/*  613 */             SBC_DevicesView.this.deleteFiles(selected, 0);
/*      */           }
/*      */           
/*  616 */           e.doit = false;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void keyReleased(KeyEvent arg0) {}
/*  626 */     });
/*  627 */     this.tvFiles.initialize(this.tableJobsParent);
/*      */     
/*  629 */     control.layout(true, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fillMenu(Menu menu)
/*      */   {
/*  639 */     Object[] _files = this.tvFiles.getSelectedDataSources().toArray();
/*      */     
/*  641 */     final TranscodeFile[] files = new TranscodeFile[_files.length];
/*      */     
/*  643 */     System.arraycopy(_files, 0, files, 0, files.length);
/*      */     
/*      */ 
/*      */ 
/*  647 */     MenuItem open_item = new MenuItem(menu, 8);
/*      */     
/*  649 */     Messages.setLanguageText(open_item, "MyTorrentsView.menu.open");
/*      */     
/*  651 */     Utils.setMenuItemImage(open_item, "run");
/*      */     
/*  653 */     File target_file = null;
/*  654 */     File source_file = null;
/*      */     try
/*      */     {
/*  657 */       if (files.length == 1)
/*      */       {
/*  659 */         target_file = files[0].getTargetFile().getFile(true);
/*      */         
/*  661 */         if (!target_file.exists())
/*      */         {
/*  663 */           target_file = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  668 */       Debug.out(e);
/*      */     }
/*      */     try
/*      */     {
/*  672 */       if (files.length == 1)
/*      */       {
/*  674 */         source_file = files[0].getSourceFile().getFile(true);
/*      */         
/*  676 */         if (!source_file.exists())
/*      */         {
/*  678 */           source_file = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  683 */       Debug.out(e);
/*      */     }
/*      */     
/*  686 */     final File f_target_file = target_file;
/*  687 */     final File f_source_file = source_file;
/*      */     
/*  689 */     open_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent ev) {
/*  692 */         Utils.launch(f_target_file.getAbsolutePath());
/*      */       }
/*      */       
/*  695 */     });
/*  696 */     open_item.setEnabled(target_file != null);
/*      */     
/*      */ 
/*      */ 
/*  700 */     final boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*      */     
/*  702 */     MenuItem show_item = new MenuItem(menu, 8);
/*      */     
/*  704 */     Messages.setLanguageText(show_item, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"));
/*      */     
/*      */ 
/*  707 */     show_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  710 */         org.gudy.azureus2.ui.swt.views.utils.ManagerUtils.open(f_target_file != null ? f_target_file : f_source_file, use_open_containing_folder);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  715 */     });
/*  716 */     show_item.setEnabled(((source_file != null) && (!files[0].isComplete())) || ((target_file != null) && (files[0].isComplete())));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  722 */     Menu menu_category = new Menu(menu.getShell(), 4);
/*  723 */     MenuItem item_category = new MenuItem(menu, 64);
/*  724 */     Messages.setLanguageText(item_category, "MyTorrentsView.menu.setCategory");
/*  725 */     item_category.setMenu(menu_category);
/*      */     
/*  727 */     addCategorySubMenu(menu_category, files);
/*      */     
/*      */ 
/*      */ 
/*  731 */     Menu menu_tags = new Menu(menu.getShell(), 4);
/*  732 */     MenuItem item_tags = new MenuItem(menu, 64);
/*  733 */     Messages.setLanguageText(item_tags, "label.tag");
/*  734 */     item_tags.setMenu(menu_tags);
/*      */     
/*  736 */     addTagsSubMenu(menu_tags, files);
/*      */     
/*  738 */     new MenuItem(menu, 2);
/*      */     
/*      */ 
/*      */ 
/*  742 */     MenuItem pause_item = new MenuItem(menu, 8);
/*      */     
/*  744 */     pause_item.setText(MessageText.getString("v3.MainWindow.button.pause"));
/*      */     
/*  746 */     pause_item.addSelectionListener(new SelectionAdapter()
/*      */     {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  749 */         for (int i = 0; i < files.length; i++) {
/*  750 */           TranscodeJob job = files[i].getJob();
/*      */           
/*  752 */           if (job != null) {
/*  753 */             job.pause();
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  760 */     });
/*  761 */     MenuItem resume_item = new MenuItem(menu, 8);
/*      */     
/*  763 */     resume_item.setText(MessageText.getString("v3.MainWindow.button.resume"));
/*      */     
/*  765 */     resume_item.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  767 */         for (int i = 0; i < files.length; i++) {
/*  768 */           TranscodeJob job = files[i].getJob();
/*      */           
/*  770 */           if (job != null) {
/*  771 */             job.resume();
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  778 */     });
/*  779 */     new MenuItem(menu, 2);
/*      */     
/*  781 */     if ((this.device instanceof DeviceMediaRenderer))
/*      */     {
/*  783 */       DeviceMediaRenderer dmr = (DeviceMediaRenderer)this.device;
/*      */       
/*  785 */       if ((dmr.canCopyToDevice()) || (dmr.canCopyToFolder()))
/*      */       {
/*      */ 
/*      */ 
/*  789 */         MenuItem retry_item = new MenuItem(menu, 8);
/*      */         
/*  791 */         retry_item.setText(MessageText.getString("device.retry.copy"));
/*      */         
/*  793 */         retry_item.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  795 */             for (int i = 0; i < files.length; i++) {
/*  796 */               TranscodeFile file = files[i];
/*      */               
/*  798 */               if ((file.getCopyToDeviceFails() > 0L) || (file.isCopiedToDevice()))
/*      */               {
/*  800 */                 file.retryCopyToDevice();
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*  805 */         });
/*  806 */         retry_item.setEnabled(false);
/*      */         
/*  808 */         for (TranscodeFile file : files)
/*      */         {
/*  810 */           if ((file.getCopyToDeviceFails() > 0L) || (file.isCopiedToDevice()))
/*      */           {
/*  812 */             retry_item.setEnabled(true);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  818 */         new MenuItem(menu, 2);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  824 */     MenuItem sc_item = new MenuItem(menu, 8);
/*      */     
/*  826 */     sc_item.setText(MessageText.getString("devices.copy_url"));
/*      */     
/*  828 */     if (files.length == 1)
/*      */     {
/*  830 */       final URL url = files[0].getStreamURL();
/*      */       
/*  832 */       if (url != null)
/*      */       {
/*  834 */         sc_item.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  836 */             org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipBoard(url.toExternalForm());
/*      */           }
/*      */           
/*      */ 
/*      */         });
/*      */       } else {
/*  842 */         sc_item.setEnabled(false);
/*      */       }
/*      */     }
/*      */     else {
/*  846 */       sc_item.setEnabled(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  851 */     int comp = 0;
/*  852 */     int incomp = 0;
/*      */     
/*  854 */     for (TranscodeFile f : files)
/*      */     {
/*  856 */       if (f.isComplete()) {
/*  857 */         comp++;
/*      */       } else {
/*  859 */         incomp++;
/*      */       }
/*      */     }
/*  862 */     MenuItem remove_item = new MenuItem(menu, 8);
/*      */     
/*      */     String text;
/*      */     String text;
/*  866 */     if (comp == 0) {
/*  867 */       text = "devices.cancel_xcode"; } else { String text;
/*  868 */       if (incomp == 0) {
/*  869 */         text = "azbuddy.ui.menu.remove";
/*      */       } else {
/*  871 */         text = "devices.cancel_xcode_del";
/*      */       }
/*      */     }
/*  874 */     remove_item.setText(MessageText.getString(text));
/*      */     
/*  876 */     Utils.setMenuItemImage(remove_item, "delete");
/*      */     
/*  878 */     remove_item.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  880 */         SBC_DevicesView.this.deleteFiles(files, 0);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  885 */     });
/*  886 */     new MenuItem(menu, 2);
/*      */     
/*      */ 
/*      */ 
/*  890 */     boolean has_selection = files.length > 0;
/*      */     
/*  892 */     remove_item.setEnabled(has_selection);
/*      */     
/*  894 */     boolean can_pause = has_selection;
/*  895 */     boolean can_resume = has_selection;
/*      */     
/*  897 */     int job_count = 0;
/*      */     
/*  899 */     for (int i = 0; i < files.length; i++) {
/*  900 */       TranscodeJob job = files[i].getJob();
/*  901 */       if (job != null)
/*      */       {
/*      */ 
/*      */ 
/*  905 */         job_count++;
/*      */         
/*  907 */         int state = job.getState();
/*      */         
/*  909 */         if ((state != 1) || (!job.canPause()))
/*      */         {
/*  911 */           can_pause = false;
/*      */         }
/*      */         
/*  914 */         if (state != 2)
/*      */         {
/*  916 */           can_resume = false;
/*      */         }
/*      */       }
/*      */     }
/*  920 */     pause_item.setEnabled((can_pause) && (job_count > 0));
/*  921 */     resume_item.setEnabled((can_resume) && (job_count > 0));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addCategorySubMenu(Menu menu_category, final TranscodeFile[] files)
/*      */   {
/*  929 */     MenuItem[] items = menu_category.getItems();
/*      */     
/*  931 */     for (int i = 0; i < items.length; i++) {
/*  932 */       items[i].dispose();
/*      */     }
/*      */     
/*  935 */     Category[] categories = CategoryManager.getCategories();
/*  936 */     Arrays.sort(categories);
/*      */     
/*  938 */     if (categories.length > 0) {
/*  939 */       Category catUncat = CategoryManager.getCategory(2);
/*  940 */       if (catUncat != null) {
/*  941 */         MenuItem itemCategory = new MenuItem(menu_category, 8);
/*  942 */         Messages.setLanguageText(itemCategory, catUncat.getName());
/*  943 */         itemCategory.setData("Category", catUncat);
/*  944 */         itemCategory.addListener(13, new Listener() {
/*      */           public void handleEvent(Event event) {
/*  946 */             MenuItem item = (MenuItem)event.widget;
/*  947 */             SBC_DevicesView.this.assignSelectedToCategory((Category)item.getData("Category"), files);
/*      */           }
/*      */           
/*  950 */         });
/*  951 */         new MenuItem(menu_category, 2);
/*      */       }
/*      */       
/*  954 */       for (i = 0; i < categories.length; i++) {
/*  955 */         if (categories[i].getType() == 0) {
/*  956 */           MenuItem itemCategory = new MenuItem(menu_category, 8);
/*  957 */           itemCategory.setText(categories[i].getName());
/*  958 */           itemCategory.setData("Category", categories[i]);
/*      */           
/*  960 */           itemCategory.addListener(13, new Listener() {
/*      */             public void handleEvent(Event event) {
/*  962 */               MenuItem item = (MenuItem)event.widget;
/*  963 */               SBC_DevicesView.this.assignSelectedToCategory((Category)item.getData("Category"), files);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*  969 */       new MenuItem(menu_category, 2);
/*      */     }
/*      */     
/*  972 */     MenuItem itemAddCategory = new MenuItem(menu_category, 8);
/*  973 */     Messages.setLanguageText(itemAddCategory, "MyTorrentsView.menu.setCategory.add");
/*      */     
/*      */ 
/*  976 */     itemAddCategory.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  978 */         SBC_DevicesView.this.addCategory(files);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addCategory(TranscodeFile[] files)
/*      */   {
/*  988 */     CategoryAdderWindow adderWindow = new CategoryAdderWindow(Display.getDefault());
/*  989 */     Category newCategory = adderWindow.getNewCategory();
/*  990 */     if (newCategory != null) {
/*  991 */       assignSelectedToCategory(newCategory, files);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void assignSelectedToCategory(Category category, TranscodeFile[] files)
/*      */   {
/*      */     String[] cats;
/*      */     
/*      */     String[] cats;
/* 1001 */     if (category.getType() == 2)
/*      */     {
/* 1003 */       cats = new String[0];
/*      */     }
/*      */     else
/*      */     {
/* 1007 */       cats = new String[] { category.getName() };
/*      */     }
/*      */     
/* 1010 */     for (TranscodeFile file : files)
/*      */     {
/* 1012 */       file.setCategories(cats);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addTagsSubMenu(Menu menu_tags, final TranscodeFile[] files)
/*      */   {
/* 1021 */     MenuItem[] items = menu_tags.getItems();
/*      */     
/* 1023 */     for (int i = 0; i < items.length; i++) {
/* 1024 */       items[i].dispose();
/*      */     }
/*      */     
/* 1027 */     TagManager tm = com.aelitis.azureus.core.tag.TagManagerFactory.getTagManager();
/*      */     
/* 1029 */     List<Tag> all_tags = tm.getTagType(3).getTags();
/*      */     
/* 1031 */     all_tags = org.gudy.azureus2.ui.swt.views.utils.TagUIUtils.sortTags(all_tags);
/*      */     Set<String> shared_tags;
/* 1033 */     Map<String, Tag> menu_name_map; if (all_tags.size() > 0)
/*      */     {
/* 1035 */       shared_tags = null;
/*      */       
/* 1037 */       boolean some_tags_assigned = false;
/*      */       
/* 1039 */       for (TranscodeFile file : files)
/*      */       {
/* 1041 */         Set<String> file_tags = new java.util.HashSet();
/*      */         
/* 1043 */         file_tags.addAll(Arrays.asList(file.getTags(true)));
/*      */         
/* 1045 */         if (file_tags.size() > 0)
/*      */         {
/* 1047 */           some_tags_assigned = true;
/*      */         }
/*      */         
/* 1050 */         if (shared_tags == null)
/*      */         {
/* 1052 */           shared_tags = file_tags;
/*      */         }
/*      */         else
/*      */         {
/* 1056 */           if (shared_tags.size() != file_tags.size())
/*      */           {
/* 1058 */             shared_tags.clear();
/*      */             
/* 1060 */             break;
/*      */           }
/*      */           
/*      */ 
/* 1064 */           if (!shared_tags.equals(file_tags))
/*      */           {
/* 1066 */             shared_tags.clear();
/*      */             
/* 1068 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1074 */       if (some_tags_assigned)
/*      */       {
/* 1076 */         MenuItem mi_no_tag = new MenuItem(menu_tags, 8);
/*      */         
/* 1078 */         mi_no_tag.setText(MessageText.getString("label.no.tag"));
/*      */         
/* 1080 */         mi_no_tag.addListener(13, new Listener()
/*      */         {
/*      */           public void handleEvent(Event event) {
/* 1083 */             for (TranscodeFile file : files)
/*      */             {
/* 1085 */               file.setTags(new String[0]);
/*      */             }
/*      */             
/*      */           }
/* 1089 */         });
/* 1090 */         new MenuItem(menu_tags, 2);
/*      */       }
/*      */       
/* 1093 */       List<String> menu_names = new ArrayList();
/* 1094 */       menu_name_map = new java.util.IdentityHashMap();
/*      */       
/* 1096 */       for (Tag t : all_tags)
/*      */       {
/* 1098 */         if (t.isTagAuto()[0] == 0)
/*      */         {
/* 1100 */           String name = t.getTagName(true);
/*      */           
/* 1102 */           menu_names.add(name);
/* 1103 */           menu_name_map.put(name, t);
/*      */         }
/*      */       }
/*      */       
/* 1107 */       List<Object> menu_structure = MenuBuildUtils.splitLongMenuListIntoHierarchy(menu_names, 20);
/*      */       
/* 1109 */       for (Object obj : menu_structure)
/*      */       {
/* 1111 */         List<Tag> bucket_tags = new ArrayList();
/*      */         
/*      */ 
/*      */ 
/* 1115 */         if ((obj instanceof String))
/*      */         {
/* 1117 */           Menu parent_menu = menu_tags;
/*      */           
/* 1119 */           bucket_tags.add(menu_name_map.get((String)obj));
/*      */         }
/*      */         else
/*      */         {
/* 1123 */           Object[] entry = (Object[])obj;
/*      */           
/* 1125 */           List<String> tag_names = (List)entry[1];
/*      */           
/* 1127 */           boolean sub_all_selected = true;
/* 1128 */           boolean sub_some_selected = false;
/*      */           
/* 1130 */           for (String name : tag_names)
/*      */           {
/* 1132 */             Tag sub_tag = (Tag)menu_name_map.get(name);
/*      */             
/* 1134 */             if ((shared_tags != null) && (shared_tags.contains(name)))
/*      */             {
/* 1136 */               sub_some_selected = true;
/*      */             }
/*      */             else
/*      */             {
/* 1140 */               sub_all_selected = false;
/*      */             }
/*      */             
/* 1143 */             bucket_tags.add(sub_tag);
/*      */           }
/*      */           
/*      */           String mod;
/*      */           String mod;
/* 1148 */           if (sub_all_selected)
/*      */           {
/* 1150 */             mod = " (*)";
/*      */           } else { String mod;
/* 1152 */             if (sub_some_selected)
/*      */             {
/* 1154 */               mod = " (+)";
/*      */             }
/*      */             else
/*      */             {
/* 1158 */               mod = "";
/*      */             }
/*      */           }
/* 1161 */           Menu menu_bucket = new Menu(menu_tags.getShell(), 4);
/*      */           
/* 1163 */           MenuItem bucket_item = new MenuItem(menu_tags, 64);
/*      */           
/* 1165 */           bucket_item.setText((String)entry[0] + mod);
/*      */           
/* 1167 */           bucket_item.setMenu(menu_bucket);
/*      */           
/* 1169 */           parent_menu = menu_bucket;
/*      */         }
/*      */         
/* 1172 */         for (final Tag t : bucket_tags)
/*      */         {
/* 1174 */           final MenuItem t_i = new MenuItem(parent_menu, 32);
/*      */           
/* 1176 */           String tag_name = t.getTagName(true);
/*      */           
/* 1178 */           t_i.setText(tag_name);
/*      */           
/* 1180 */           boolean selected = (shared_tags != null) && (shared_tags.contains(tag_name));
/*      */           
/* 1182 */           t_i.setSelection(selected);
/*      */           
/* 1184 */           t_i.addListener(13, new Listener()
/*      */           {
/*      */             public void handleEvent(Event event) {
/* 1187 */               boolean selected = t_i.getSelection();
/*      */               
/* 1189 */               String tag_uid = String.valueOf(t.getTagUID());
/*      */               
/* 1191 */               for (TranscodeFile file : files)
/*      */               {
/* 1193 */                 Set<String> uids = new TreeSet();
/*      */                 
/* 1195 */                 uids.addAll(Arrays.asList(file.getTags(false)));
/*      */                 
/* 1197 */                 boolean update = false;
/*      */                 
/* 1199 */                 if (selected)
/*      */                 {
/* 1201 */                   if (!uids.contains(tag_uid))
/*      */                   {
/* 1203 */                     uids.add(tag_uid);
/*      */                     
/* 1205 */                     update = true;
/*      */                   }
/*      */                   
/*      */                 }
/* 1209 */                 else if (uids.contains(tag_uid))
/*      */                 {
/* 1211 */                   uids.remove(tag_uid);
/*      */                   
/* 1213 */                   update = true;
/*      */                 }
/*      */                 
/*      */ 
/* 1217 */                 if (update)
/*      */                 {
/* 1219 */                   file.setTags((String[])uids.toArray(new String[uids.size()]));
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     Menu parent_menu;
/* 1228 */     new MenuItem(menu_tags, 2);
/*      */     
/* 1230 */     MenuItem item_create = new MenuItem(menu_tags, 8);
/*      */     
/* 1232 */     Messages.setLanguageText(item_create, "label.add.tag");
/*      */     
/* 1234 */     item_create.addListener(13, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 1237 */         TagUIUtilsV3.showCreateTagDialog(new UIFunctions.TagReturner() {
/*      */           public void returnedTags(Tag[] tags) {
/* 1239 */             if (tags != null) {
/* 1240 */               for (Tag new_tag : tags) {
/* 1241 */                 if (new_tag != null)
/*      */                 {
/* 1243 */                   String[] tagUIDs = { String.valueOf(new_tag.getTagUID()) };
/*      */                   
/* 1245 */                   for (TranscodeFile file : SBC_DevicesView.33.this.val$files)
/*      */                   {
/* 1247 */                     file.setTags(tagUIDs);
/*      */                   }
/*      */                   
/* 1250 */                   COConfigurationManager.setParameter("Library.TagInSideBar", true);
/*      */                 }
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
/*      */   public void deviceChanged(Device device)
/*      */   {
/* 1266 */     String name = device.getName();
/*      */     
/* 1268 */     if (!name.equals(this.device_name))
/*      */     {
/* 1270 */       this.device_name = name;
/*      */       
/*      */ 
/* 1273 */       SWTSkinObject soTitle = getSkinObject("title");
/* 1274 */       if ((soTitle instanceof SWTSkinObjectText)) {
/* 1275 */         ((SWTSkinObjectText)soTitle).setTextID("device.view.heading", new String[] { name });
/*      */       }
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
/*      */ 
/*      */ 
/*      */   private void initDeviceListTable(Composite control)
/*      */   {
/* 1291 */     this.tvDevices = TableViewFactory.createTableViewSWT(com.aelitis.azureus.core.devices.TranscodeProvider.class, "Devices", "Devices", new TableColumnCore[0], "trancode_qpos", 65540);
/*      */     
/*      */ 
/* 1294 */     this.tvDevices.setRowDefaultHeightEM(1.5F);
/* 1295 */     this.tvDevices.setHeaderVisible(true);
/*      */     
/*      */ 
/*      */ 
/* 1299 */     Composite parent = new Composite(control, 0);
/* 1300 */     parent.setLayoutData(Utils.getFilledFormData());
/* 1301 */     GridLayout layout = new GridLayout();
/* 1302 */     layout.marginHeight = (layout.marginWidth = layout.verticalSpacing = layout.horizontalSpacing = 0);
/* 1303 */     parent.setLayout(layout);
/*      */     
/* 1305 */     this.tvDevices.initialize(parent);
/*      */   }
/*      */   
/*      */   public void jobAdded(TranscodeJob job)
/*      */   {
/* 1310 */     synchronized (this) {
/* 1311 */       if (this.tvFiles == null) {
/* 1312 */         return;
/*      */       }
/*      */       
/* 1315 */       if (this.transTarget == null) {
/* 1316 */         TranscodeFile file = job.getTranscodeFile();
/* 1317 */         if (file != null) {
/* 1318 */           this.tvFiles.addDataSource(file);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void jobChanged(TranscodeJob job)
/*      */   {
/* 1326 */     synchronized (this) {
/* 1327 */       if (this.tvFiles == null) {
/* 1328 */         return;
/*      */       }
/* 1330 */       TableRowCore row = this.tvFiles.getRow(getFileInTable(job.getTranscodeFile()));
/* 1331 */       if (row != null) {
/* 1332 */         row.invalidate();
/* 1333 */         if (row.isVisible()) {
/* 1334 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1335 */           if (uiFunctions != null) {
/* 1336 */             uiFunctions.refreshIconBar();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void jobRemoved(TranscodeJob job)
/*      */   {
/* 1345 */     synchronized (this) {
/* 1346 */       if (this.tvFiles == null) {
/* 1347 */         return;
/*      */       }
/* 1349 */       if (this.transTarget == null) {
/* 1350 */         TranscodeFile file = job.getTranscodeFile();
/* 1351 */         if (file != null) {
/* 1352 */           this.tvFiles.removeDataSource(getFileInTable(file));
/*      */         }
/*      */       } else {
/* 1355 */         TableRowCore row = this.tvFiles.getRow(getFileInTable(job.getTranscodeFile()));
/* 1356 */         if (row != null) {
/* 1357 */           row.invalidate();
/* 1358 */           if (row.isVisible()) {
/* 1359 */             UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1360 */             if (uiFunctions != null) {
/* 1361 */               uiFunctions.refreshIconBar();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private TranscodeFile getFileInTable(TranscodeFile file)
/*      */   {
/* 1377 */     if (file == null)
/*      */     {
/* 1379 */       return null;
/*      */     }
/*      */     
/* 1382 */     if (this.tvFiles.getRow(file) == null)
/*      */     {
/* 1384 */       List<TranscodeFile> files = this.tvFiles.getDataSources();
/*      */       
/* 1386 */       for (TranscodeFile f : files)
/*      */       {
/* 1388 */         if (f.equals(file))
/*      */         {
/* 1390 */           return f;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1395 */     return file;
/*      */   }
/*      */   
/*      */ 
/*      */   public void refreshToolBarItems(Map<String, Long> list)
/*      */   {
/*      */     Object[] selectedDS;
/*      */     
/*      */     int size;
/* 1404 */     synchronized (this) {
/* 1405 */       if (this.tvFiles == null) {
/* 1406 */         return;
/*      */       }
/* 1408 */       selectedDS = this.tvFiles.getSelectedDataSources().toArray();
/* 1409 */       size = this.tvFiles.size(false);
/*      */     }
/* 1411 */     if (selectedDS.length == 0) {
/* 1412 */       return;
/*      */     }
/*      */     
/* 1415 */     list.put("remove", Long.valueOf(1L));
/*      */     
/* 1417 */     boolean can_stop = true;
/* 1418 */     boolean can_queue = true;
/* 1419 */     boolean can_move_up = true;
/* 1420 */     boolean can_move_down = true;
/* 1421 */     boolean hasJob = false;
/*      */     
/* 1423 */     for (Object ds : selectedDS) {
/* 1424 */       TranscodeJob job = ((TranscodeFile)ds).getJob();
/*      */       
/* 1426 */       if (job != null)
/*      */       {
/*      */ 
/*      */ 
/* 1430 */         hasJob = true;
/*      */         
/* 1432 */         int index = job.getIndex();
/*      */         
/* 1434 */         if (index == 1)
/*      */         {
/* 1436 */           can_move_up = false;
/*      */         }
/*      */         
/*      */ 
/* 1440 */         if (index == size)
/*      */         {
/* 1442 */           can_move_down = false;
/*      */         }
/*      */         
/* 1445 */         int state = job.getState();
/*      */         
/* 1447 */         if ((state != 2) && (state != 1) && (state != 5) && (state != 0))
/*      */         {
/*      */ 
/* 1450 */           can_stop = false;
/*      */         }
/*      */         
/* 1453 */         if ((state != 2) && (state != 6) && (state != 5))
/*      */         {
/*      */ 
/* 1456 */           can_queue = false;
/*      */         }
/*      */       }
/*      */     }
/* 1460 */     if (!hasJob) {
/* 1461 */       can_stop = can_queue = can_move_down = can_move_up = 0;
/*      */     }
/*      */     
/* 1464 */     if ((can_queue) && (can_stop)) {
/* 1465 */       can_stop = false;
/*      */     }
/*      */     
/* 1468 */     list.put("stop", Long.valueOf(can_stop ? 1L : 0L));
/* 1469 */     list.put("start", Long.valueOf(can_queue ? 1L : 0L));
/* 1470 */     list.put("up", Long.valueOf(can_move_up ? 1L : 0L));
/* 1471 */     list.put("down", Long.valueOf(can_move_down ? 1L : 0L));
/*      */     
/* 1473 */     if (selectedDS.length == 1)
/*      */     {
/* 1475 */       TranscodeFile f = (TranscodeFile)selectedDS[0];
/*      */       
/* 1477 */       if ((f.isComplete()) && (f.getStreamURL() != null)) {
/*      */         try
/*      */         {
/* 1480 */           if (com.aelitis.azureus.util.PlayUtils.canUseEMP(f.getTargetFile()))
/*      */           {
/* 1482 */             list.put("play", Long.valueOf(1L));
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 1486 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*      */   {
/* 1498 */     if (this.tvFiles == null) {
/* 1499 */       return false;
/*      */     }
/*      */     
/* 1502 */     TranscodeFile[] selectedDS = (TranscodeFile[])this.tvFiles.getSelectedDataSources().toArray(new TranscodeFile[0]);
/* 1503 */     if (selectedDS.length == 0) {
/* 1504 */       return false;
/*      */     }
/*      */     
/* 1507 */     String itemKey = item.getID();
/*      */     
/* 1509 */     if (itemKey.equals("remove")) {
/* 1510 */       deleteFiles(selectedDS, 0);
/* 1511 */       return true;
/*      */     }
/*      */     
/* 1514 */     if (itemKey.equals("play"))
/*      */     {
/* 1516 */       if (selectedDS.length == 1)
/*      */       {
/* 1518 */         TranscodeFile f = selectedDS[0];
/*      */         
/* 1520 */         if (com.aelitis.azureus.ui.swt.views.skin.TorrentListViewsUtils.openInEMP(f.getName(), f.getStreamURL()) == 0)
/*      */         {
/* 1522 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1527 */     List<TranscodeJob> jobs = new ArrayList(selectedDS.length);
/*      */     
/*      */ 
/* 1530 */     boolean can_stop = true;
/* 1531 */     boolean can_queue = true;
/*      */     
/* 1533 */     for (int i = 0; i < selectedDS.length; i++) {
/* 1534 */       TranscodeFile file = selectedDS[i];
/* 1535 */       TranscodeJob job = file.getJob();
/* 1536 */       if (job != null) {
/* 1537 */         jobs.add(job);
/*      */         
/* 1539 */         int state = job.getState();
/*      */         
/* 1541 */         if ((state != 2) && (state != 1) && (state != 5) && (state != 0))
/*      */         {
/*      */ 
/* 1544 */           can_stop = false;
/*      */         }
/*      */         
/* 1547 */         if ((state != 2) && (state != 6) && (state != 5))
/*      */         {
/*      */ 
/* 1550 */           can_queue = false;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1555 */     if (jobs.size() == 0) {
/* 1556 */       return false;
/*      */     }
/*      */     
/* 1559 */     if ((can_queue) && (can_stop)) {
/* 1560 */       can_stop = false;
/*      */     }
/*      */     
/* 1563 */     if ((itemKey.equals("up")) || (itemKey.equals("down")))
/*      */     {
/* 1565 */       final String f_itemKey = itemKey;
/*      */       
/* 1567 */       java.util.Collections.sort(jobs, new java.util.Comparator()
/*      */       {
/*      */         public int compare(TranscodeJob j1, TranscodeJob j2) {
/* 1570 */           return (f_itemKey.equals("up") ? 1 : -1) * (j1.getIndex() - j2.getIndex());
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1575 */     if (itemKey.equals("startstop"))
/*      */     {
/* 1577 */       if (can_queue) {
/* 1578 */         itemKey = "start";
/* 1579 */       } else if (can_stop) {
/* 1580 */         itemKey = "stop";
/*      */       }
/*      */     }
/*      */     
/* 1584 */     boolean didSomething = false;
/* 1585 */     boolean forceSort = false;
/* 1586 */     for (TranscodeJob job : jobs)
/*      */     {
/* 1588 */       if (itemKey.equals("stop"))
/*      */       {
/* 1590 */         job.stop();
/* 1591 */         didSomething = true;
/*      */       }
/* 1593 */       else if (itemKey.equals("start"))
/*      */       {
/* 1595 */         didSomething = true;
/* 1596 */         job.queue();
/*      */       }
/* 1598 */       else if (itemKey.equals("up"))
/*      */       {
/* 1600 */         didSomething = true;
/* 1601 */         job.moveUp();
/*      */         
/* 1603 */         TableColumnCore sortColumn = this.tvFiles.getSortColumn();
/* 1604 */         forceSort = (sortColumn != null) && (sortColumn.getName().equals("trancode_qpos"));
/*      */ 
/*      */       }
/* 1607 */       else if (itemKey.equals("down"))
/*      */       {
/* 1609 */         didSomething = true;
/* 1610 */         job.moveDown();
/*      */         
/* 1612 */         TableColumnCore sortColumn = this.tvFiles.getSortColumn();
/* 1613 */         forceSort = (sortColumn != null) && (sortColumn.getName().equals("trancode_qpos"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1618 */     this.tvFiles.refreshTable(forceSort);
/*      */     
/* 1620 */     return didSomething;
/*      */   }
/*      */   
/*      */   public String getUpdateUIName()
/*      */   {
/* 1625 */     return "DevicesView";
/*      */   }
/*      */   
/*      */   public void updateUI()
/*      */   {
/* 1630 */     if (this.tvFiles != null) {
/* 1631 */       this.tvFiles.refreshTable(false);
/*      */     }
/*      */   }
/*      */   
/*      */   public void fileAdded(TranscodeFile file)
/*      */   {
/* 1637 */     synchronized (this) {
/* 1638 */       if (this.tvFiles != null) {
/* 1639 */         this.tvFiles.addDataSource(file);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void fileChanged(TranscodeFile file, int type, Object data)
/*      */   {
/*      */     TableRowCore row;
/* 1647 */     synchronized (this) {
/* 1648 */       if (this.tvFiles == null) {
/* 1649 */         return;
/*      */       }
/* 1651 */       row = this.tvFiles.getRow(getFileInTable(file));
/*      */     }
/* 1653 */     if (row != null) {
/* 1654 */       row.invalidate();
/* 1655 */       if (row.isVisible()) {
/* 1656 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1657 */         if (uiFunctions != null) {
/* 1658 */           uiFunctions.refreshIconBar();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void fileRemoved(TranscodeFile file)
/*      */   {
/* 1666 */     synchronized (this) {
/* 1667 */       if (this.tvFiles != null) {
/* 1668 */         this.tvFiles.removeDataSource(getFileInTable(file));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void deleteFiles(final TranscodeFile[] toRemove, final int startIndex) {
/* 1674 */     if (toRemove[startIndex] == null) {
/* 1675 */       int nextIndex = startIndex + 1;
/* 1676 */       if (nextIndex < toRemove.length) {
/* 1677 */         deleteFiles(toRemove, nextIndex);
/*      */       }
/* 1679 */       return;
/*      */     }
/*      */     
/* 1682 */     final TranscodeFile file = toRemove[startIndex];
/*      */     try
/*      */     {
/* 1685 */       File cache_file = file.getCacheFileIfExists();
/*      */       
/* 1687 */       if ((cache_file != null) && (cache_file.exists()) && (file.isComplete()))
/*      */       {
/* 1689 */         String path = cache_file.toString();
/*      */         
/* 1691 */         String title = MessageText.getString("xcode.deletedata.title");
/*      */         
/* 1693 */         String copy_text = "";
/*      */         
/* 1695 */         Device device = file.getDevice();
/*      */         
/* 1697 */         if ((device instanceof DeviceMediaRenderer))
/*      */         {
/* 1699 */           DeviceMediaRenderer dmr = (DeviceMediaRenderer)device;
/*      */           
/* 1701 */           File copy_to = dmr.getCopyToFolder();
/*      */           
/* 1703 */           if ((dmr.canCopyToDevice()) || ((dmr.canCopyToFolder()) && (copy_to != null) && (copy_to.exists())))
/*      */           {
/* 1705 */             copy_text = MessageText.getString("xcode.deletedata.message.2", new String[] { device.getName() });
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1712 */         String text = MessageText.getString("xcode.deletedata.message", new String[] { file.getName(), file.getProfileName(), copy_text });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1719 */         MessageBoxShell mb = new MessageBoxShell(title, text);
/* 1720 */         mb.setRemember("xcode.deletedata.noconfirm.key", false, MessageText.getString("deletedata.noprompt"));
/*      */         
/*      */ 
/* 1723 */         if (startIndex == toRemove.length - 1) {
/* 1724 */           mb.setButtons(0, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, new Integer[] { Integer.valueOf(0), Integer.valueOf(1) });
/*      */           
/*      */ 
/*      */ 
/* 1728 */           mb.setRememberOnlyIfButton(0);
/*      */         } else {
/* 1730 */           mb.setButtons(1, new String[] { MessageText.getString("Button.removeAll"), MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, new Integer[] { Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1) });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1735 */           mb.setRememberOnlyIfButton(1);
/*      */         }
/*      */         
/* 1738 */         org.gudy.azureus2.core3.download.DownloadManager dm = null;
/*      */         
/* 1740 */         if (dm != null)
/*      */         {
/* 1742 */           mb.setRelatedObject(dm);
/*      */         }
/*      */         
/* 1745 */         mb.setLeftImage(8);
/*      */         
/* 1747 */         mb.open(new UserPrompterResultListener() {
/*      */           public void prompterClosed(int result) {
/* 1749 */             if (result == -1)
/* 1750 */               return;
/* 1751 */             if (result == 0) {
/* 1752 */               SBC_DevicesView.this.deleteNoCheck(file);
/* 1753 */             } else if (result == 2) {
/* 1754 */               for (int i = startIndex; i < toRemove.length; i++) {
/* 1755 */                 if (toRemove[i] != null) {
/* 1756 */                   SBC_DevicesView.this.deleteNoCheck(toRemove[i]);
/*      */                 }
/*      */               }
/* 1759 */               return;
/*      */             }
/*      */             
/* 1762 */             int nextIndex = startIndex + 1;
/* 1763 */             if (nextIndex < toRemove.length) {
/* 1764 */               SBC_DevicesView.this.deleteFiles(toRemove, nextIndex);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       else
/*      */       {
/* 1771 */         deleteNoCheck(file);
/*      */         
/* 1773 */         int nextIndex = startIndex + 1;
/* 1774 */         if (nextIndex < toRemove.length) {
/* 1775 */           deleteFiles(toRemove, nextIndex);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1780 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */   private void deleteNoCheck(TranscodeFile file) {
/* 1785 */     TranscodeJob job = file.getJob();
/*      */     
/* 1787 */     if (job != null) {
/*      */       try
/*      */       {
/* 1790 */         job.remove();
/*      */       }
/*      */       catch (TranscodeActionVetoException e)
/*      */       {
/* 1794 */         UIFunctionsManager.getUIFunctions().forceNotify(1, MessageText.getString("globalmanager.download.remove.veto"), e.getMessage(), null, null, -1);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1799 */         return;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1804 */       file.delete(file.getCacheFileIfExists() != null);
/*      */     } catch (TranscodeException e) {
/* 1806 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void createDragDrop(final TableViewSWT<?> table)
/*      */   {
/*      */     try
/*      */     {
/* 1816 */       Transfer[] types = { TextTransfer.getInstance() };
/*      */       
/* 1818 */       if ((this.dragSource != null) && (!this.dragSource.isDisposed())) {
/* 1819 */         this.dragSource.dispose();
/*      */       }
/*      */       
/* 1822 */       if ((this.dropTarget != null) && (!this.dropTarget.isDisposed())) {
/* 1823 */         this.dropTarget.dispose();
/*      */       }
/*      */       
/* 1826 */       this.dragSource = table.createDragSource(3);
/* 1827 */       if (this.dragSource != null) {
/* 1828 */         this.dragSource.setTransfer(types);
/* 1829 */         this.dragSource.addDragListener(new DragSourceAdapter() {
/*      */           private String eventData;
/*      */           
/*      */           public void dragStart(DragSourceEvent event) {
/* 1833 */             TableRowCore[] rows = table.getSelectedRows();
/* 1834 */             if (rows.length != 0) {
/* 1835 */               event.doit = true;
/*      */               
/* 1837 */               SBC_DevicesView.this.drag_drop_line_start = rows[0].getIndex();
/* 1838 */               SBC_DevicesView.this.drag_drop_rows = rows;
/*      */             } else {
/* 1840 */               event.doit = false;
/* 1841 */               SBC_DevicesView.this.drag_drop_line_start = -1;
/* 1842 */               SBC_DevicesView.this.drag_drop_rows = null;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1848 */             List selectedFiles = table.getSelectedDataSources();
/*      */             
/* 1850 */             this.eventData = "TranscodeFile\n";
/*      */             
/* 1852 */             for (Object o : selectedFiles)
/*      */             {
/* 1854 */               TranscodeFile file = (TranscodeFile)o;
/*      */               
/* 1856 */               if (file.isComplete()) {
/*      */                 try
/*      */                 {
/* 1859 */                   this.eventData = (this.eventData + file.getTargetFile().getFile(true).getAbsolutePath() + "\n");
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void dragSetData(DragSourceEvent event)
/*      */           {
/* 1870 */             event.data = this.eventData;
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1875 */       this.dropTarget = table.createDropTarget(31);
/*      */       
/* 1877 */       if (this.dropTarget != null) {
/* 1878 */         this.dropTarget.setTransfer(new Transfer[] { org.eclipse.swt.dnd.HTMLTransfer.getInstance(), URLTransfer.getInstance(), org.eclipse.swt.dnd.FileTransfer.getInstance(), TextTransfer.getInstance() });
/*      */         
/*      */ 
/*      */ 
/* 1882 */         this.dropTarget.addDropListener(new org.eclipse.swt.dnd.DropTargetAdapter() {
/*      */           public void dropAccept(DropTargetEvent event) {
/* 1884 */             event.currentDataType = URLTransfer.pickBestType(event.dataTypes, event.currentDataType);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void dragEnter(DropTargetEvent event)
/*      */           {
/* 1891 */             if (SBC_DevicesView.this.drag_drop_line_start < 0) {
/* 1892 */               if (event.detail != 1) {
/* 1893 */                 if ((event.operations & 0x4) > 0) {
/* 1894 */                   event.detail = 4;
/* 1895 */                 } else if ((event.operations & 0x1) > 0)
/* 1896 */                   event.detail = 1;
/*      */               }
/* 1898 */             } else if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
/*      */             {
/* 1900 */               event.detail = (event.item == null ? 0 : 2);
/* 1901 */               event.feedback = 10;
/*      */             }
/*      */           }
/*      */           
/*      */           public void dragOver(DropTargetEvent event) {
/* 1906 */             if (SBC_DevicesView.this.drag_drop_line_start >= 0) {
/* 1907 */               event.detail = (event.item == null ? 0 : 2);
/* 1908 */               event.feedback = 10;
/*      */             }
/*      */           }
/*      */           
/*      */           public void drop(DropTargetEvent event) {
/*      */             try {
/* 1914 */               if (((event.data instanceof String)) && (((String)event.data).startsWith("TranscodeFile\n"))) {
/*      */                 return;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1922 */               event.detail = 0;
/*      */               
/* 1924 */               DeviceManagerUI.handleDrop((TranscodeTarget)SBC_DevicesView.this.device, event.data);
/*      */             }
/*      */             finally
/*      */             {
/* 1928 */               SBC_DevicesView.this.drag_drop_line_start = -1;
/* 1929 */               SBC_DevicesView.this.drag_drop_rows = null;
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     catch (Throwable t) {
/* 1936 */       Debug.out("failed to init drag-n-drop", t);
/*      */     }
/*      */   }
/*      */   
/*      */   public void updateSelectedContent() {
/* 1941 */     TableView tv = this.tvFiles != null ? this.tvFiles : this.tvDevices;
/* 1942 */     Object[] dataSources = tv.getSelectedDataSources(true);
/* 1943 */     List<SelectedContent> listSelected = new ArrayList(dataSources.length);
/*      */     
/* 1945 */     for (Object ds : dataSources) {
/* 1946 */       if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 1947 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds;
/* 1948 */         listSelected.add(new SelectedContent(fileInfo.getDownloadManager(), fileInfo.getIndex()));
/*      */       }
/*      */     }
/*      */     
/* 1952 */     SelectedContent[] sc = (SelectedContent[])listSelected.toArray(new SelectedContent[0]);
/* 1953 */     com.aelitis.azureus.ui.selectedcontent.SelectedContentManager.changeCurrentlySelectedContent(tv.getTableID(), null, tv);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/SBC_DevicesView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */