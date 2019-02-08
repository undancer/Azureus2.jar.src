/*      */ package com.aelitis.azureus.ui.swt.devices;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.devices.Device;
/*      */ import com.aelitis.azureus.core.devices.Device.browseLocation;
/*      */ import com.aelitis.azureus.core.devices.DeviceContentDirectory;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager.UnassociatedDevice;
/*      */ import com.aelitis.azureus.core.devices.DeviceManagerFactory;
/*      */ import com.aelitis.azureus.core.devices.DeviceManagerListener;
/*      */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*      */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloader;
/*      */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloaderManager;
/*      */ import com.aelitis.azureus.core.devices.DeviceTemplate;
/*      */ import com.aelitis.azureus.core.devices.TranscodeException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*      */ import com.aelitis.azureus.core.devices.TranscodeManager;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*      */ import com.aelitis.azureus.core.devices.TranscodeQueue;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTarget;
/*      */ import com.aelitis.azureus.core.download.StreamManager;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*      */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.devices.add.DeviceTemplateChooser;
/*      */ import com.aelitis.azureus.ui.swt.devices.add.ManufacturerChooser;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinViewManager;
/*      */ import com.aelitis.net.upnpms.UPNPMSContainer;
/*      */ import com.aelitis.net.upnpms.UPNPMSItem;
/*      */ import com.aelitis.net.upnpms.UPNPMSNode;
/*      */ import java.io.File;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.custom.StyleRange;
/*      */ import org.eclipse.swt.custom.StyledText;
/*      */ import org.eclipse.swt.dnd.DragSource;
/*      */ import org.eclipse.swt.dnd.DragSourceEvent;
/*      */ import org.eclipse.swt.dnd.DropTarget;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.DirectoryDialog;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.DirectoryParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.URLTransfer.URLType;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInputReceiver;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ 
/*      */ public class DeviceManagerUI
/*      */ {
/*      */   private static final String CONFIG_SECTION_ID = "Devices";
/*      */   public static boolean DISABLED;
/*      */   private static final int MIN_FILE_SIZE_FOR_XCODE = 131072;
/*      */   private static final int MAX_FILES_FOR_MULTI_XCODE = 64;
/*  117 */   private static final Object DEVICE_IVIEW_KEY = new Object();
/*      */   
/*      */   private static final String CONFIG_VIEW_TYPE = "device.sidebar.ui.viewtype";
/*      */   
/*      */   public static final String CONFIG_VIEW_HIDE_REND_GENERIC = "device.sidebar.ui.rend.hidegeneric";
/*      */   
/*      */   public static final String CONFIG_VIEW_SHOW_ONLY_TAGGED = "device.sidebar.ui.rend.showonlytagged";
/*      */   
/*      */   private static final String SPINNER_IMAGE_ID = "image.sidebar.vitality.dl";
/*      */   
/*      */   private static final String INFO_IMAGE_ID = "image.sidebar.vitality.info";
/*      */   
/*      */   private static final String ALERT_IMAGE_ID = "image.sidebar.vitality.alert";
/*      */   
/*      */   private static final boolean SHOW_RENDERER_VITALITY = false;
/*      */   
/*      */   private static final boolean SHOW_OD_VITALITY = true;
/*      */   
/*      */   private DeviceManager device_manager;
/*      */   
/*      */   private DeviceManagerListener device_manager_listener;
/*      */   
/*      */   private boolean device_manager_listener_added;
/*      */   
/*      */   private final PluginInterface plugin_interface;
/*      */   
/*      */   private final UIManager ui_manager;
/*      */   private UISWTInstance swt_ui;
/*      */   private boolean ui_setup;
/*      */   private MultipleDocumentInterfaceSWT mdi;
/*      */   private boolean sidebar_built;
/*      */   private static final int SBV_SIMPLE = 0;
/*      */   private static final int SBV_FULL = Integer.MAX_VALUE;
/*  150 */   private int side_bar_view_type = COConfigurationManager.getIntParameter("device.sidebar.ui.viewtype", 0);
/*  151 */   private boolean side_bar_hide_rend_gen = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.hidegeneric", true);
/*  152 */   private boolean side_bar_show_tagged = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.showonlytagged", false);
/*      */   
/*      */   private int next_sidebar_id;
/*      */   
/*  156 */   private List<categoryView> categories = new ArrayList();
/*      */   
/*  158 */   private int last_job_count = 0;
/*      */   
/*      */   private MenuItemListener properties_listener;
/*      */   
/*      */   private MenuItemListener hide_listener;
/*      */   
/*      */   private MenuItemListener rename_listener;
/*      */   
/*      */   private MenuItemListener export_listener;
/*      */   
/*      */   private MenuItemFillListener will_remove_listener;
/*      */   
/*      */   private MenuItemListener remove_listener;
/*      */   
/*      */   private MenuItemFillListener show_fill_listener;
/*      */   
/*      */   private MenuItemListener show_listener;
/*      */   
/*      */   private MenuItemFillListener will_tag_listener;
/*      */   
/*      */   private MenuItemListener tag_listener;
/*      */   
/*      */   private MenuItemFillListener will_browse_listener;
/*      */   private boolean offline_menus_setup;
/*      */   private MdiEntry mdiEntryOverview;
/*      */   private boolean needsAddAllDevices;
/*      */   private MdiEntry entryHeader;
/*      */   private static final String OXC_NOTHING = "Nothing";
/*      */   private static final String OXC_QUIT_VUZE = "QuitVuze";
/*      */   private static final String OXC_SLEEP = "Sleep";
/*      */   private static final String OXC_HIBERNATE = "Hibernate";
/*      */   private static final String OXC_SHUTDOWN = "Shutdown";
/*  190 */   private String oxc_action = "Nothing";
/*  191 */   private boolean oxc_trigger_set = false;
/*      */   
/*      */   private static final int MAX_MS_DISPLAY_LINE_DEFAULT = 5000;
/*      */   private static int max_ms_display_lines;
/*      */   
/*      */   static
/*      */   {
/*  198 */     COConfigurationManager.addAndFireParameterListener("Plugin.default.device.config.ms.maxlines", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*  206 */         DeviceManagerUI.access$002(COConfigurationManager.getIntParameter(name, 5000));
/*      */       }
/*      */     });
/*      */     
/*      */     try
/*      */     {
/*  212 */       if (Constants.isOSX) {
/*  213 */         String arch = System.getProperty("os.arch", "");
/*  214 */         DISABLED = (arch.equalsIgnoreCase("powerpc")) || (arch.equalsIgnoreCase("ppc"));
/*      */       } else {
/*  216 */         DISABLED = Constants.isUnix;
/*      */       }
/*  218 */       DISABLED |= Utils.isAZ2UI();
/*      */     }
/*      */     catch (Throwable t) {
/*  221 */       DISABLED = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DeviceManagerUI(AzureusCore core)
/*      */   {
/*  229 */     this.plugin_interface = org.gudy.azureus2.pluginsimpl.local.PluginInitializer.getDefaultInterface();
/*      */     
/*  231 */     this.ui_manager = this.plugin_interface.getUIManager();
/*      */     
/*  233 */     if (DISABLED) {
/*  234 */       return;
/*      */     }
/*      */     
/*  237 */     this.ui_manager.addUIListener(new org.gudy.azureus2.plugins.ui.UIManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void UIAttached(UIInstance instance)
/*      */       {
/*      */ 
/*  244 */         if ((instance instanceof UISWTInstance))
/*      */         {
/*  246 */           DeviceManagerUI.this.swt_ui = ((UISWTInstance)instance);
/*      */           
/*  248 */           AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void azureusCoreRunning(AzureusCore core)
/*      */             {
/*      */ 
/*  255 */               DeviceManagerUI.this.uiAttachedAndCoreRunning(core);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void UIDetached(UIInstance instance) {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void uiAttachedAndCoreRunning(AzureusCore core)
/*      */   {
/*  272 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  274 */         MultipleDocumentInterfaceSWT mdi = com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*      */         
/*  276 */         if (mdi != null)
/*      */         {
/*  278 */           DeviceManagerUI.this.setupUI(mdi);
/*      */         }
/*      */         else {
/*  281 */           SkinViewManager.addListener(new com.aelitis.azureus.ui.swt.views.skin.SkinViewManager.SkinViewManagerListener()
/*      */           {
/*      */             public void skinViewAdded(SkinView skinview) {
/*  284 */               if ((skinview instanceof com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBar))
/*      */               {
/*  286 */                 DeviceManagerUI.this.setupUI((com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBar)skinview);
/*  287 */                 SkinViewManager.RemoveListener(this);
/*      */               }
/*      */               
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*  294 */     });
/*  295 */     org.gudy.azureus2.ui.swt.UIExitUtilsSWT.addListener(new org.gudy.azureus2.ui.swt.UIExitUtilsSWT.canCloseListener()
/*      */     {
/*      */ 
/*      */       public boolean canClose()
/*      */       {
/*      */         try
/*      */         {
/*  302 */           if (DeviceManagerUI.this.device_manager == null)
/*      */           {
/*      */ 
/*      */ 
/*  306 */             return true;
/*      */           }
/*      */           
/*  309 */           TranscodeJob job = DeviceManagerUI.this.device_manager.getTranscodeManager().getQueue().getCurrentJob();
/*      */           
/*  311 */           if ((job == null) || (job.getState() != 1))
/*      */           {
/*  313 */             return true;
/*      */           }
/*      */           
/*  316 */           if (job.getTranscodeFile().getDevice().isHidden())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  322 */             return true;
/*      */           }
/*      */           
/*  325 */           String title = MessageText.getString("device.quit.transcoding.title");
/*  326 */           String text = MessageText.getString("device.quit.transcoding.text", new String[] { job.getName(), job.getTarget().getDevice().getName(), String.valueOf(job.getPercentComplete()) });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  334 */           MessageBoxShell mb = new MessageBoxShell(title, text, new String[] { MessageText.getString("UpdateWindow.quit"), MessageText.getString("Content.alert.notuploaded.button.abort") }, 1);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  342 */           mb.open(null);
/*      */           
/*  344 */           mb.waitUntilClosed();
/*      */           
/*  346 */           return mb.getResult() == 0;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  350 */           Debug.out(e);
/*      */         }
/*  352 */         return true;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceManager getDeviceManager()
/*      */   {
/*  362 */     return this.device_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected PluginInterface getPluginInterface()
/*      */   {
/*  368 */     return this.plugin_interface;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setupUI(MultipleDocumentInterfaceSWT mdi)
/*      */   {
/*  375 */     synchronized (this)
/*      */     {
/*  377 */       if (this.ui_setup)
/*      */       {
/*  379 */         return;
/*      */       }
/*      */       
/*  382 */       this.ui_setup = true;
/*      */     }
/*      */     
/*  385 */     this.mdi = mdi;
/*      */     
/*  387 */     this.device_manager = DeviceManagerFactory.getSingleton();
/*      */     
/*  389 */     setupMenuListeners();
/*      */     
/*  391 */     mdi.registerEntry("Devices", new com.aelitis.azureus.ui.mdi.MdiEntryCreationListener()
/*      */     {
/*      */       public MdiEntry createMDiEntry(String id) {
/*  394 */         if (DeviceManagerUI.this.sidebar_built) {
/*  395 */           DeviceManagerUI.this.removeAllDevices();
/*      */           
/*  397 */           DeviceManagerUI.this.buildSideBar(true);
/*      */         } else {
/*  399 */           DeviceManagerUI.this.buildSideBar(false);
/*      */         }
/*      */         
/*  402 */         DeviceManagerUI.this.addAllDevices();
/*  403 */         return DeviceManagerUI.this.mdiEntryOverview;
/*      */       }
/*      */       
/*      */ 
/*  407 */     });
/*  408 */     this.device_manager.addListener(new DeviceManagerListener()
/*      */     {
/*      */       public void deviceRemoved(Device device) {}
/*      */       
/*      */       public void deviceManagerLoaded()
/*      */       {
/*  414 */         DeviceManagerUI.this.device_manager.removeListener(this);
/*  415 */         DeviceManagerUI.this.setupUIwithDeviceManager();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void deviceChanged(Device device) {}
/*      */       
/*      */ 
/*      */       public void deviceAttentionRequest(Device device) {}
/*      */       
/*      */ 
/*      */       public void deviceAdded(Device device) {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void setupUIwithDeviceManager()
/*      */   {
/*  433 */     boolean add_all = false;
/*      */     
/*  435 */     synchronized (this)
/*      */     {
/*  437 */       this.device_manager_listener = new DeviceManagerListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void deviceAdded(Device device)
/*      */         {
/*      */ 
/*  444 */           DeviceManagerUI.this.addOrChangeDevice(device);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void deviceChanged(Device device)
/*      */         {
/*  451 */           DeviceManagerUI.this.addOrChangeDevice(device);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void deviceAttentionRequest(Device device)
/*      */         {
/*  458 */           DeviceManagerUI.this.showDevice(device);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void deviceRemoved(Device device)
/*      */         {
/*  465 */           DeviceManagerUI.this.removeDevice(device);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void deviceManagerLoaded() {}
/*      */       };
/*      */       
/*  473 */       if (this.needsAddAllDevices)
/*      */       {
/*  475 */         add_all = true;
/*      */         
/*  477 */         this.needsAddAllDevices = false;
/*      */       }
/*      */     }
/*      */     
/*  481 */     TranscodeManager transMan = this.device_manager.getTranscodeManager();
/*      */     
/*  483 */     final TranscodeQueue transQ = transMan.getQueue();
/*      */     
/*  485 */     transQ.addListener(new com.aelitis.azureus.core.devices.TranscodeQueueListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void jobAdded(TranscodeJob job)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  495 */         check();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void jobChanged(TranscodeJob job)
/*      */       {
/*  502 */         check();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void jobRemoved(TranscodeJob job)
/*      */       {
/*  509 */         check();
/*      */       }
/*      */       
/*      */       protected void check()
/*      */       {
/*      */         try
/*      */         {
/*  516 */           int job_count = transQ.getJobCount();
/*      */           
/*  518 */           if (job_count != DeviceManagerUI.this.last_job_count)
/*      */           {
/*  520 */             if ((job_count == 0) || (DeviceManagerUI.this.last_job_count == 0))
/*      */             {
/*  522 */               MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*      */               
/*  524 */               if (mdi != null)
/*      */               {
/*  526 */                 MdiEntry main_entry = mdi.getEntry("Devices");
/*      */                 
/*  528 */                 if (main_entry != null)
/*      */                 {
/*  530 */                   ViewTitleInfoManager.refreshTitleInfo(main_entry.getViewTitleInfo());
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  535 */             DeviceManagerUI.this.last_job_count = job_count;
/*      */           }
/*      */         }
/*      */         finally {
/*  539 */           DeviceManagerUI.this.checkOXCState();
/*      */         }
/*      */         
/*      */       }
/*  543 */     });
/*  544 */     setupListeners();
/*      */     
/*      */ 
/*      */ 
/*  548 */     setupConfigUI();
/*      */     
/*  550 */     if (add_all)
/*      */     {
/*  552 */       addAllDevices();
/*      */     }
/*      */     
/*  555 */     setupTranscodeMenus();
/*      */   }
/*      */   
/*      */   public void setupConfigUI() {
/*  559 */     BasicPluginConfigModel configModel = this.ui_manager.createBasicPluginConfigModel("root", "Devices");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  564 */     final BooleanParameter as = configModel.addBooleanParameter2("device.search.auto", "device.search.auto", this.device_manager.getAutoSearch());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  569 */     as.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  576 */         DeviceManagerUI.this.device_manager.setAutoSearch(as.getValue());
/*      */         
/*  578 */         if (DeviceManagerUI.this.device_manager.getAutoSearch())
/*      */         {
/*  580 */           DeviceManagerUI.this.search();
/*      */         }
/*      */         
/*      */       }
/*  584 */     });
/*  585 */     final BooleanParameter qosParam = configModel.addBooleanParameter2("devices.sendQOS", "devices.turnon.qos", false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  590 */     qosParam.setValue(COConfigurationManager.getBooleanParameter("devices.sendQOS", false));
/*      */     
/*  592 */     qosParam.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */       public void parameterChanged(Parameter param) {
/*  595 */         COConfigurationManager.setParameter("devices.sendQOS", qosParam.getValue());
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  601 */     });
/*  602 */     final BooleanParameter config_simple_view = configModel.addBooleanParameter2("device.sidebar.ui.viewtype", "devices.sidebar.simple", this.side_bar_view_type == 0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  607 */     config_simple_view.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  614 */         COConfigurationManager.setParameter("device.sidebar.ui.viewtype", config_simple_view.getValue() ? 0 : Integer.MAX_VALUE);
/*      */       }
/*      */       
/*  617 */     });
/*  618 */     COConfigurationManager.addParameterListener("device.sidebar.ui.viewtype", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  626 */         config_simple_view.setValue(COConfigurationManager.getIntParameter("device.sidebar.ui.viewtype", 0) == 0);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  631 */     });
/*  632 */     configModel.addBooleanParameter2("!device.sidebar.ui.rend.hidegeneric!", "devices.sidebar.hide.rend.generic", this.side_bar_hide_rend_gen);
/*      */     
/*      */ 
/*      */ 
/*  636 */     configModel.addBooleanParameter2("!device.sidebar.ui.rend.showonlytagged!", "devices.sidebar.show.only.tagged", this.side_bar_show_tagged);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  642 */     final IntParameter auto_hide_old = configModel.addIntParameter2("device.config.autohide.old.devices", "device.config.autohide.old.devices", this.device_manager.getAutoHideOldDevicesDays(), 0, 2048);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  647 */     auto_hide_old.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  654 */         DeviceManagerUI.this.device_manager.setAutoHideOldDevicesDays(auto_hide_old.getValue());
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  661 */     });
/*  662 */     String def = this.device_manager.getDefaultWorkingDirectory().getAbsolutePath();
/*      */     
/*  664 */     final DirectoryParameter def_work_dir = configModel.addDirectoryParameter2("device.config.xcode.workdir", "device.config.xcode.workdir", def);
/*      */     
/*  666 */     def_work_dir.setValue(def);
/*      */     
/*  668 */     def_work_dir.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  675 */         DeviceManagerUI.this.device_manager.setDefaultWorkingDirectory(new File(def_work_dir.getValue()));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  680 */     });
/*  681 */     final IntParameter max_xcode = configModel.addIntParameter2("device.config.xcode.maxbps", "device.config.xcode.maxbps", (int)(this.device_manager.getTranscodeManager().getQueue().getMaxBytesPerSecond() / 1024L), 0, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  687 */     max_xcode.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  694 */         DeviceManagerUI.this.device_manager.getTranscodeManager().getQueue().setMaxBytesPerSecond(max_xcode.getValue() * 1024);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  699 */     });
/*  700 */     final BooleanParameter disable_sleep = configModel.addBooleanParameter2("device.config.xcode.disable_sleep", "device.config.xcode.disable_sleep", this.device_manager.getDisableSleep());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  706 */     disable_sleep.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  713 */         DeviceManagerUI.this.device_manager.setDisableSleep(disable_sleep.getValue());
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  718 */     });
/*  719 */     final ActionParameter btnITunes = configModel.addActionParameter2("devices.button.installitunes", "UpdateWindow.columns.install");
/*  720 */     btnITunes.setEnabled(false);
/*  721 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  723 */         boolean hasItunes = core.getPluginManager().getPluginInterfaceByID("azitunes") != null;
/*      */         
/*  725 */         btnITunes.setEnabled(!hasItunes);
/*      */       }
/*      */       
/*  728 */     });
/*  729 */     btnITunes.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener() {
/*      */       public void parameterChanged(Parameter param) {
/*  731 */         org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*      */           public void azureusCoreRunning(AzureusCore core) {
/*      */             try {
/*  734 */               PluginInstaller installer = core.getPluginManager().getPluginInstaller();
/*      */               
/*  736 */               org.gudy.azureus2.plugins.installer.StandardPlugin itunes_plugin = installer.getStandardPlugin("azitunes");
/*      */               
/*  738 */               if (itunes_plugin == null)
/*      */               {
/*  740 */                 Debug.out("iTunes standard plugin not found");
/*      */               }
/*      */               else
/*      */               {
/*  744 */                 itunes_plugin.install(false);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  748 */               Debug.printStackTrace(e);
/*      */             }
/*      */             
/*      */           }
/*      */         });
/*      */       }
/*  754 */     });
/*  755 */     configModel.createGroup("device.xcode.group", new Parameter[] { def_work_dir, max_xcode, disable_sleep, btnITunes });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  766 */     IntParameter max_ms_lines = configModel.addIntParameter2("device.config.ms.maxlines", "device.config.ms.maxlines", 5000, 0, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  772 */     configModel.createGroup("device.ms.group", new Parameter[] { max_ms_lines });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  781 */     final BooleanParameter rss_enable = configModel.addBooleanParameter2("device.rss.enable", "device.rss.enable", this.device_manager.isRSSPublishEnabled());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  786 */     rss_enable.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  793 */         DeviceManagerUI.this.device_manager.setRSSPublishEnabled(rss_enable.getValue());
/*      */       }
/*      */       
/*  796 */     });
/*  797 */     org.gudy.azureus2.plugins.ui.config.HyperlinkParameter rss_view = configModel.addHyperlinkParameter2("device.rss.view", this.device_manager.getRSSLink());
/*      */     
/*      */ 
/*      */ 
/*  801 */     rss_enable.addEnabledOnSelection(rss_view);
/*      */     
/*  803 */     configModel.createGroup("device.rss.group", new Parameter[] { rss_enable, rss_view });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  814 */     final DeviceOfflineDownloaderManager dodm = this.device_manager.getOfflineDownlaoderManager();
/*      */     
/*  816 */     final BooleanParameter od_enable = configModel.addBooleanParameter2("device.od.enable", "device.od.enable", dodm.isOfflineDownloadingEnabled());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  821 */     od_enable.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  828 */         dodm.setOfflineDownloadingEnabled(od_enable.getValue());
/*      */         
/*  830 */         DeviceManagerUI.this.rebuildSideBarIfExists();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  835 */     });
/*  836 */     final BooleanParameter od_auto_enable = configModel.addBooleanParameter2("device.odauto.enable", "device.odauto.enable", dodm.getOfflineDownloadingIsAuto());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  841 */     od_auto_enable.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  848 */         dodm.setOfflineDownloadingIsAuto(od_auto_enable.getValue());
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  853 */     });
/*  854 */     final BooleanParameter od_pt_enable = configModel.addBooleanParameter2("device.odpt.enable", "device.odpt.enable", dodm.getOfflineDownloadingIncludePrivate());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  859 */     od_pt_enable.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  866 */         dodm.setOfflineDownloadingIncludePrivate(od_pt_enable.getValue());
/*      */       }
/*      */       
/*  869 */     });
/*  870 */     od_auto_enable.addEnabledOnSelection(od_pt_enable);
/*      */     
/*  872 */     configModel.createGroup("device.od.group", new Parameter[] { od_enable, od_auto_enable, od_pt_enable });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  882 */     final StreamManager sm = StreamManager.getSingleton();
/*      */     
/*  884 */     final IntParameter pn_buffer = configModel.addIntParameter2("device.playnow.buffer", "device.playnow.buffer", sm.getBufferSecs());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  889 */     pn_buffer.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  896 */         sm.setBufferSecs(pn_buffer.getValue());
/*      */       }
/*      */       
/*  899 */     });
/*  900 */     IntParameter pn_min_buffer = configModel.addIntParameter2("device.playnow.min_buffer", "device.playnow.min_buffer", sm.getMinBufferSecs());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  905 */     pn_min_buffer.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  912 */         sm.setMinBufferSecs(pn_buffer.getValue());
/*      */       }
/*      */       
/*  915 */     });
/*  916 */     configModel.createGroup("device.playnow.group", new Parameter[] { pn_buffer, pn_min_buffer });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  923 */     final BooleanParameter tivo_enable = configModel.addBooleanParameter2("device.tivo.enable", "device.tivo.enable", false);
/*      */     
/*      */ 
/*      */ 
/*  927 */     tivo_enable.setValue(this.device_manager.isTiVoEnabled());
/*      */     
/*  929 */     tivo_enable.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  936 */         DeviceManagerUI.this.device_manager.setTiVoEnabled(tivo_enable.getValue());
/*      */         
/*  938 */         DeviceManagerUI.this.rebuildSideBarIfExists();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setupMenuListeners()
/*      */   {
/*  947 */     this.properties_listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/*  955 */         if ((target instanceof MdiEntry)) {
/*  956 */           MdiEntry info = (MdiEntry)target;
/*  957 */           Device device = (Device)info.getDatasource();
/*      */           
/*  959 */           DeviceManagerUI.this.showProperties(device);
/*      */         }
/*      */         
/*      */       }
/*  963 */     };
/*  964 */     this.hide_listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/*  972 */         if ((target instanceof MdiEntry))
/*      */         {
/*  974 */           MdiEntry info = (MdiEntry)target;
/*      */           
/*  976 */           Device device = (Device)info.getDatasource();
/*      */           
/*  978 */           device.setHidden(true);
/*      */         }
/*      */         
/*      */       }
/*  982 */     };
/*  983 */     this.will_tag_listener = new MenuItemFillListener()
/*      */     {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object targets)
/*      */       {
/*      */         Object[] rows;
/*      */         
/*      */ 
/*      */         Object[] rows;
/*      */         
/*      */ 
/*  993 */         if ((targets instanceof Object[]))
/*      */         {
/*  995 */           rows = (Object[])targets;
/*      */         }
/*      */         else
/*      */         {
/*  999 */           rows = new Object[] { targets };
/*      */         }
/*      */         
/* 1002 */         if ((rows.length > 0) && ((rows[0] instanceof MdiEntry)))
/*      */         {
/* 1004 */           MdiEntry info = (MdiEntry)rows[0];
/*      */           
/* 1006 */           Device device = (Device)info.getDatasource();
/*      */           
/* 1008 */           menu.setData(Boolean.valueOf(device.isTagged()));
/*      */         }
/*      */         else
/*      */         {
/* 1012 */           menu.setEnabled(false);
/*      */         }
/*      */         
/*      */       }
/* 1016 */     };
/* 1017 */     this.tag_listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/* 1025 */         if ((target instanceof MdiEntry))
/*      */         {
/* 1027 */           MdiEntry info = (MdiEntry)target;
/*      */           
/* 1029 */           Device device = (Device)info.getDatasource();
/*      */           
/* 1031 */           device.setTagged(!device.isTagged());
/*      */         }
/*      */         
/*      */       }
/* 1035 */     };
/* 1036 */     this.rename_listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/* 1044 */         if ((target instanceof MdiEntry))
/*      */         {
/* 1046 */           MdiEntry info = (MdiEntry)target;
/*      */           
/* 1048 */           final Device device = (Device)info.getDatasource();
/*      */           
/* 1050 */           UISWTInputReceiver entry = (UISWTInputReceiver)DeviceManagerUI.this.swt_ui.getInputReceiver();
/*      */           
/* 1052 */           entry.setPreenteredText(device.getName(), false);
/*      */           
/* 1054 */           entry.maintainWhitespace(false);
/*      */           
/* 1056 */           entry.allowEmptyInput(false);
/*      */           
/* 1058 */           entry.setLocalisedTitle(MessageText.getString("label.rename", new String[] { device.getName() }));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1063 */           entry.prompt(new org.gudy.azureus2.plugins.ui.UIInputReceiverListener() {
/*      */             public void UIInputReceiverClosed(UIInputReceiver entry) {
/* 1065 */               if (!entry.hasSubmittedInput()) {
/* 1066 */                 return;
/*      */               }
/* 1068 */               String input = entry.getSubmittedInput().trim();
/*      */               
/* 1070 */               if (input.length() > 0)
/*      */               {
/* 1072 */                 device.setName(input, false);
/*      */               }
/*      */               
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/* 1079 */     };
/* 1080 */     this.export_listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/* 1088 */         if ((target instanceof MdiEntry))
/*      */         {
/* 1090 */           MdiEntry info = (MdiEntry)target;
/*      */           
/* 1092 */           Device device = (Device)info.getDatasource();
/*      */           
/* 1094 */           DeviceManagerUI.this.export(device);
/*      */         }
/*      */         
/*      */       }
/* 1098 */     };
/* 1099 */     this.will_remove_listener = new MenuItemFillListener()
/*      */     {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object targets)
/*      */       {
/*      */         Object[] rows;
/*      */         
/*      */ 
/*      */         Object[] rows;
/*      */         
/*      */ 
/* 1109 */         if ((targets instanceof Object[]))
/*      */         {
/* 1111 */           rows = (Object[])targets;
/*      */         }
/*      */         else
/*      */         {
/* 1115 */           rows = new Object[] { targets };
/*      */         }
/*      */         
/* 1118 */         if ((rows.length > 0) && ((rows[0] instanceof MdiEntry)))
/*      */         {
/* 1120 */           MdiEntry info = (MdiEntry)rows[0];
/*      */           
/* 1122 */           Device device = (Device)info.getDatasource();
/*      */           
/* 1124 */           menu.setEnabled(device.canRemove());
/*      */         }
/*      */         else
/*      */         {
/* 1128 */           menu.setEnabled(false);
/*      */         }
/*      */         
/*      */       }
/* 1132 */     };
/* 1133 */     this.remove_listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/* 1141 */         if ((target instanceof MdiEntry))
/*      */         {
/* 1143 */           MdiEntry info = (MdiEntry)target;
/*      */           
/* 1145 */           Device device = (Device)info.getDatasource();
/*      */           
/* 1147 */           device.remove();
/*      */         }
/*      */         
/*      */       }
/* 1151 */     };
/* 1152 */     this.will_browse_listener = new MenuItemFillListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object targets)
/*      */       {
/*      */ 
/*      */ 
/* 1160 */         menu.removeAllChildItems();
/*      */         
/* 1162 */         boolean enabled = false;
/*      */         
/*      */         Object[] rows;
/*      */         Object[] rows;
/* 1166 */         if ((targets instanceof Object[]))
/*      */         {
/* 1168 */           rows = (Object[])targets;
/*      */         }
/*      */         else
/*      */         {
/* 1172 */           rows = new Object[] { targets };
/*      */         }
/*      */         
/* 1175 */         if ((rows.length > 0) && ((rows[0] instanceof MdiEntry)))
/*      */         {
/* 1177 */           MdiEntry info = (MdiEntry)rows[0];
/*      */           
/* 1179 */           Device device = (Device)info.getDatasource();
/*      */           
/* 1181 */           Device.browseLocation[] locs = device.getBrowseLocations();
/*      */           
/* 1183 */           enabled = (locs != null) && (locs.length > 0);
/*      */           
/* 1185 */           if (enabled)
/*      */           {
/* 1187 */             MenuManager menuManager = DeviceManagerUI.this.ui_manager.getMenuManager();
/*      */             
/* 1189 */             for (final Device.browseLocation loc : locs)
/*      */             {
/* 1191 */               org.gudy.azureus2.plugins.ui.menus.MenuItem loc_menu = menuManager.addMenuItem(menu, loc.getName());
/*      */               
/* 1193 */               loc_menu.addListener(new MenuItemListener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */                 {
/*      */ 
/*      */ 
/* 1201 */                   Utils.launch(loc.getURL().toExternalForm());
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1208 */         menu.setEnabled(enabled);
/*      */       }
/*      */       
/* 1211 */     };
/* 1212 */     this.show_listener = new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/* 1220 */         if ((target instanceof MdiEntry))
/*      */         {
/* 1222 */           MdiEntry info = (MdiEntry)target;
/*      */           
/* 1224 */           Object ds = info.getDatasource();
/*      */           
/* 1226 */           if ((ds instanceof Device))
/*      */           {
/*      */ 
/*      */ 
/* 1230 */             Device device = (Device)ds;
/*      */             
/* 1232 */             device.setHidden(true);
/*      */           }
/*      */           else
/*      */           {
/* 1236 */             int category_type = ds == null ? 0 : ((Integer)ds).intValue();
/*      */             
/* 1238 */             Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */             
/* 1240 */             for (Device device : devices)
/*      */             {
/* 1242 */               if ((category_type == 0) || ((device.getType() == category_type) && (device.isHidden())))
/*      */               {
/*      */ 
/* 1245 */                 device.setHidden(false);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1252 */     };
/* 1253 */     this.show_fill_listener = new MenuItemFillListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object targets)
/*      */       {
/*      */ 
/*      */ 
/* 1261 */         boolean enabled = false;
/*      */         
/*      */         Object[] rows;
/*      */         Object[] rows;
/* 1265 */         if ((targets instanceof Object[]))
/*      */         {
/* 1267 */           rows = (Object[])targets;
/*      */         }
/*      */         else
/*      */         {
/* 1271 */           rows = new Object[] { targets };
/*      */         }
/*      */         
/* 1274 */         for (Object row : rows)
/*      */         {
/* 1276 */           if ((row instanceof MdiEntry))
/*      */           {
/* 1278 */             MdiEntry info = (MdiEntry)row;
/*      */             
/* 1280 */             Object ds = info.getDatasource();
/*      */             
/* 1282 */             if (!(ds instanceof Device))
/*      */             {
/*      */ 
/*      */ 
/* 1286 */               int category_type = ds == null ? 0 : ((Integer)ds).intValue();
/*      */               
/* 1288 */               Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */               
/* 1290 */               for (Device device : devices)
/*      */               {
/* 1292 */                 if ((category_type == 0) || ((device.getType() == category_type) && (device.isHidden())))
/*      */                 {
/*      */ 
/* 1295 */                   if (device.isHidden())
/*      */                   {
/* 1297 */                     enabled = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1305 */         menu.setEnabled(enabled);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void export(final Device device)
/*      */   {
/* 1315 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 1321 */         FileDialog dialog = new FileDialog(Utils.findAnyShell(), 139264);
/*      */         
/*      */ 
/* 1324 */         dialog.setFilterPath(org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener.getFilterPathData());
/*      */         
/* 1326 */         dialog.setText(MessageText.getString("device.export.select.template.file"));
/*      */         
/* 1328 */         dialog.setFilterExtensions(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1333 */         dialog.setFilterNames(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1339 */         String path = org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener.setFilterPathData(dialog.open());
/*      */         
/* 1341 */         if (path != null)
/*      */         {
/* 1343 */           String lc = path.toLowerCase();
/*      */           
/* 1345 */           if ((!lc.endsWith(".vuze")) && (!lc.endsWith(".vuz")))
/*      */           {
/* 1347 */             path = path + ".vuze";
/*      */           }
/*      */           try
/*      */           {
/* 1351 */             VuzeFile vf = device.getVuzeFile();
/*      */             
/* 1353 */             vf.write(new File(path));
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1357 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setupListeners()
/*      */   {
/* 1367 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "device.sidebar.ui.viewtype", "device.sidebar.ui.rend.hidegeneric", "device.sidebar.ui.rend.showonlytagged" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1379 */         DeviceManagerUI.this.side_bar_view_type = COConfigurationManager.getIntParameter("device.sidebar.ui.viewtype", 0);
/*      */         
/* 1381 */         DeviceManagerUI.this.side_bar_hide_rend_gen = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.hidegeneric", true);
/*      */         
/* 1383 */         DeviceManagerUI.this.side_bar_show_tagged = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.showonlytagged", false);
/*      */         
/* 1385 */         DeviceManagerUI.this.rebuildSideBarIfExists();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static void hideIcon(MdiEntryVitalityImage x)
/*      */   {
/* 1394 */     if (x == null) {
/* 1395 */       return;
/*      */     }
/*      */     
/* 1398 */     x.setVisible(false);
/* 1399 */     x.setToolTip("");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void showIcon(MdiEntryVitalityImage x, String t)
/*      */   {
/* 1407 */     if (x == null) {
/* 1408 */       return;
/*      */     }
/*      */     
/* 1411 */     x.setToolTip(t);
/* 1412 */     x.setVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void rebuildSideBarIfExists()
/*      */   {
/* 1418 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*      */     
/* 1420 */     if (mdi != null)
/*      */     {
/* 1422 */       MdiEntry entry = mdi.getEntry("header.devices");
/*      */       
/* 1424 */       if (entry != null)
/*      */       {
/* 1426 */         rebuildSideBar();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void rebuildSideBar()
/*      */   {
/* 1435 */     if (this.sidebar_built) {
/* 1436 */       removeAllDevices();
/*      */       
/* 1438 */       buildSideBar(true);
/*      */     } else {
/* 1440 */       buildSideBar(false);
/*      */     }
/*      */     
/* 1443 */     addAllDevices();
/*      */   }
/*      */   
/*      */ 
/*      */   private String getHeaderToolTip()
/*      */   {
/* 1449 */     if ((this.side_bar_hide_rend_gen) || (this.side_bar_show_tagged))
/*      */     {
/* 1451 */       Device[] devices = this.device_manager.getDevices();
/*      */       
/* 1453 */       int generic = 0;
/* 1454 */       int untagged = 0;
/*      */       
/* 1456 */       for (Device device : devices)
/*      */       {
/* 1458 */         if (!device.isHidden())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1463 */           if (device.getType() == 3)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1468 */             DeviceMediaRenderer rend = (DeviceMediaRenderer)device;
/*      */             
/* 1470 */             if (rend.isNonSimple())
/*      */             {
/* 1472 */               generic++;
/*      */             }
/*      */             
/* 1475 */             if (!rend.isTagged())
/*      */             {
/* 1477 */               untagged++; }
/*      */           }
/*      */         }
/*      */       }
/* 1481 */       if (!this.side_bar_show_tagged)
/*      */       {
/* 1483 */         untagged = 0;
/*      */       }
/*      */       
/* 1486 */       if ((generic > 0) || (untagged > 0))
/*      */       {
/* 1488 */         return MessageText.getString("devices.sidebar.mainheader.tooltip", new String[] { String.valueOf(generic + untagged) });
/*      */       }
/*      */     }
/*      */     
/* 1492 */     return null;
/*      */   }
/*      */   
/*      */   protected MdiEntry buildSideBar(boolean rebuild) {
/* 1496 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*      */     
/* 1498 */     if (mdi == null) {
/* 1499 */       return null;
/*      */     }
/*      */     
/* 1502 */     if (this.entryHeader == null) {
/* 1503 */       this.entryHeader = mdi.getEntry("header.devices");
/* 1504 */       if (this.entryHeader != null) {
/* 1505 */         setupHeader(mdi, this.entryHeader);
/*      */       }
/*      */     }
/*      */     
/* 1509 */     this.mdiEntryOverview = mdi.getEntry("Devices");
/*      */     
/* 1511 */     if (this.mdiEntryOverview == null) {
/* 1512 */       this.mdiEntryOverview = mdi.createEntryFromSkinRef("header.devices", "Devices", "devicesview", MessageText.getString("mdi.entry.about.devices"), new ViewTitleInfo()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public Object getTitleInfoProperty(int propertyID)
/*      */         {
/*      */ 
/*      */ 
/* 1521 */           if (propertyID == 1)
/*      */           {
/* 1523 */             return DeviceManagerUI.this.getHeaderToolTip();
/*      */           }
/* 1525 */           if (propertyID == 0)
/*      */           {
/* 1527 */             if (DeviceManagerUI.this.last_job_count > 0)
/*      */             {
/* 1529 */               return String.valueOf(DeviceManagerUI.this.last_job_count);
/*      */             }
/*      */           }
/*      */           
/* 1533 */           return null; } }, null, false, "");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1538 */       this.mdiEntryOverview.setImageLeftID("image.sidebar.aboutdevices");
/*      */     }
/*      */     
/* 1541 */     if (rebuild) {
/* 1542 */       for (categoryView category : this.categories) {
/* 1543 */         category.destroy();
/*      */       }
/*      */     }
/*      */     
/* 1547 */     this.categories.clear();
/*      */     
/* 1549 */     if (this.side_bar_view_type == Integer.MAX_VALUE) {
/* 1550 */       buildCategories();
/*      */     }
/*      */     
/* 1553 */     this.sidebar_built = true;
/*      */     
/* 1555 */     return this.mdiEntryOverview;
/*      */   }
/*      */   
/*      */   private void buildCategories() {
/* 1559 */     MenuManager menu_manager = this.ui_manager.getMenuManager();
/*      */     
/*      */ 
/* 1562 */     categoryView renderers_category = addDeviceCategory(3, "device.renderer.view.title", "image.sidebar.device.renderer");
/*      */     
/*      */ 
/*      */ 
/* 1566 */     this.categories.add(renderers_category);
/*      */     
/* 1568 */     org.gudy.azureus2.plugins.ui.menus.MenuItem re_menu_item = menu_manager.addMenuItem("sidebar." + renderers_category.getKey(), "device.show");
/*      */     
/*      */ 
/* 1571 */     re_menu_item.addListener(this.show_listener);
/* 1572 */     re_menu_item.addFillListener(this.show_fill_listener);
/*      */     
/* 1574 */     re_menu_item = menu_manager.addMenuItem("sidebar." + renderers_category.getKey(), "sep_re");
/*      */     
/* 1576 */     re_menu_item.setStyle(4);
/*      */     
/* 1578 */     re_menu_item = menu_manager.addMenuItem("sidebar." + renderers_category.getKey(), "device.renderer.remove_all");
/*      */     
/*      */ 
/*      */ 
/* 1582 */     re_menu_item.addListener(new MenuItemListener()
/*      */     {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 1585 */         new AEThread2("doit")
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/* 1590 */             UIManager ui_manager = org.gudy.azureus2.plugins.utils.StaticUtilities.getUIManager(120000L);
/*      */             
/* 1592 */             long res = ui_manager.showMessageBox("device.mediaserver.remove_all.title", "device.renderer.remove_all.desc", 12L);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1597 */             if (res == 4L)
/*      */             {
/* 1599 */               Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */               
/* 1601 */               for (Device d : devices)
/*      */               {
/* 1603 */                 if (d.getType() == 3)
/*      */                 {
/* 1605 */                   if (d.canRemove())
/*      */                   {
/* 1607 */                     d.remove();
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */           }
/*      */         }.start();
/*      */       }
/* 1617 */     });
/* 1618 */     categoryView media_servers_category = addDeviceCategory(2, "device.mediaserver.view.title", "image.sidebar.device.mediaserver");
/*      */     
/*      */ 
/*      */ 
/* 1622 */     this.categories.add(media_servers_category);
/*      */     
/* 1624 */     org.gudy.azureus2.plugins.ui.menus.MenuItem ms_menu_item = menu_manager.addMenuItem("sidebar." + media_servers_category.getKey(), "device.show");
/*      */     
/*      */ 
/* 1627 */     ms_menu_item.addListener(this.show_listener);
/* 1628 */     ms_menu_item.addFillListener(this.show_fill_listener);
/*      */     
/* 1630 */     ms_menu_item = menu_manager.addMenuItem("sidebar." + media_servers_category.getKey(), "device.mediaserver.configure");
/*      */     
/*      */ 
/*      */ 
/* 1634 */     ms_menu_item.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 1636 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */         
/* 1638 */         if (uif != null) {
/* 1639 */           uif.getMDI().showEntryByID("ConfigView", "upnpmediaserver.name");
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 1645 */     });
/* 1646 */     ms_menu_item = menu_manager.addMenuItem("sidebar." + media_servers_category.getKey(), "sep_ms");
/*      */     
/* 1648 */     ms_menu_item.setStyle(4);
/*      */     
/* 1650 */     ms_menu_item = menu_manager.addMenuItem("sidebar." + media_servers_category.getKey(), "device.mediaserver.remove_all");
/*      */     
/*      */ 
/*      */ 
/* 1654 */     ms_menu_item.addListener(new MenuItemListener()
/*      */     {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 1657 */         new AEThread2("doit")
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/* 1662 */             UIManager ui_manager = org.gudy.azureus2.plugins.utils.StaticUtilities.getUIManager(120000L);
/*      */             
/* 1664 */             long res = ui_manager.showMessageBox("device.mediaserver.remove_all.title", "device.mediaserver.remove_all.desc", 12L);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1669 */             if (res == 4L)
/*      */             {
/* 1671 */               Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */               
/* 1673 */               for (Device d : devices)
/*      */               {
/* 1675 */                 if (d.getType() == 2)
/*      */                 {
/* 1677 */                   if (d.canRemove())
/*      */                   {
/* 1679 */                     d.remove();
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }.start();
/*      */       }
/* 1690 */     });
/* 1691 */     categoryView routers_category = addDeviceCategory(1, "device.router.view.title", "image.sidebar.device.router");
/*      */     
/*      */ 
/*      */ 
/* 1695 */     this.categories.add(routers_category);
/*      */     
/* 1697 */     org.gudy.azureus2.plugins.ui.menus.MenuItem rt_menu_item = menu_manager.addMenuItem("sidebar." + routers_category.getKey(), "device.show");
/*      */     
/*      */ 
/* 1700 */     rt_menu_item.addListener(this.show_listener);
/* 1701 */     rt_menu_item.addFillListener(this.show_fill_listener);
/*      */     
/* 1703 */     rt_menu_item = menu_manager.addMenuItem("sidebar." + routers_category.getKey(), "device.router.configure");
/*      */     
/*      */ 
/* 1706 */     rt_menu_item.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 1708 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */         
/* 1710 */         if (uif != null)
/*      */         {
/* 1712 */           uif.getMDI().showEntryByID("ConfigView", "UPnP");
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1721 */     if (this.device_manager.getOfflineDownlaoderManager().isOfflineDownloadingEnabled())
/*      */     {
/* 1723 */       categoryView od_category = addDeviceCategory(5, "device.offlinedownloader.view.title", "image.sidebar.device.offlinedownloader");
/*      */       
/*      */ 
/*      */ 
/* 1727 */       this.categories.add(od_category);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1732 */     categoryView internet_category = addDeviceCategory(4, "MainWindow.about.section.internet", "image.sidebar.device.internet");
/*      */     
/*      */ 
/* 1735 */     this.categories.add(internet_category);
/*      */   }
/*      */   
/*      */   private void setupHeader(MultipleDocumentInterface mdi, final MdiEntry entryHeader)
/*      */   {
/* 1740 */     addDefaultDropListener(entryHeader);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1761 */     entryHeader.setViewTitleInfo(new ViewTitleInfo()
/*      */     {
/*      */       private int last_indicator;
/*      */       
/*      */ 
/*      */       MdiEntryVitalityImage spinner;
/*      */       
/*      */ 
/*      */       MdiEntryVitalityImage warning;
/*      */       
/*      */ 
/*      */       MdiEntryVitalityImage info;
/*      */       
/*      */ 
/*      */       public Object getTitleInfoProperty(int propertyID)
/*      */       {
/* 1777 */         boolean expanded = entryHeader.isExpanded();
/*      */         
/* 1779 */         if (propertyID == 0)
/*      */         {
/* 1781 */           this.spinner.setVisible((!expanded) && (DeviceManagerUI.this.device_manager.isBusy(0)));
/*      */           
/* 1783 */           if (!expanded)
/*      */           {
/* 1785 */             Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */             
/* 1787 */             this.last_indicator = 0;
/*      */             
/* 1789 */             String all_errors = "";
/* 1790 */             String all_infos = "";
/*      */             
/* 1792 */             for (Device device : devices)
/*      */             {
/* 1794 */               String error = device.getError();
/*      */               
/* 1796 */               if (error != null)
/*      */               {
/* 1798 */                 all_errors = all_errors + (all_errors.length() == 0 ? "" : "; ") + error;
/*      */               }
/*      */               
/* 1801 */               String info = device.getInfo();
/*      */               
/* 1803 */               if (info != null)
/*      */               {
/* 1805 */                 all_infos = all_infos + (all_infos.length() == 0 ? "" : "; ") + info;
/*      */               }
/*      */               
/* 1808 */               if (!(device instanceof DeviceMediaRenderer))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1817 */                 if ((device instanceof DeviceOfflineDownloader))
/*      */                 {
/*      */ 
/*      */ 
/* 1821 */                   DeviceOfflineDownloader dod = (DeviceOfflineDownloader)device;
/*      */                   
/* 1823 */                   this.last_indicator += dod.getTransferingCount();
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1828 */             if (all_errors.length() > 0)
/*      */             {
/* 1830 */               DeviceManagerUI.hideIcon(this.info);
/*      */               
/* 1832 */               DeviceManagerUI.showIcon(this.warning, all_errors);
/*      */             }
/*      */             else
/*      */             {
/* 1836 */               DeviceManagerUI.hideIcon(this.warning);
/*      */               
/* 1838 */               if (all_infos.length() > 0)
/*      */               {
/* 1840 */                 DeviceManagerUI.showIcon(this.info, all_infos);
/*      */               }
/*      */               else
/*      */               {
/* 1844 */                 DeviceManagerUI.hideIcon(this.info);
/*      */               }
/*      */             }
/*      */             
/* 1848 */             if (this.last_indicator > 0)
/*      */             {
/* 1850 */               return String.valueOf(this.last_indicator);
/*      */             }
/*      */           }
/*      */           else {
/* 1854 */             DeviceManagerUI.hideIcon(this.warning);
/* 1855 */             DeviceManagerUI.hideIcon(this.info);
/*      */           }
/*      */         }
/* 1858 */         else if (propertyID != 8)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1869 */           if (propertyID == 1)
/*      */           {
/* 1871 */             return DeviceManagerUI.this.getHeaderToolTip();
/*      */           }
/*      */         }
/* 1874 */         return null;
/*      */       }
/*      */       
/*      */ 
/* 1878 */     });
/* 1879 */     this.device_manager.addListener(new DeviceManagerListener()
/*      */     {
/*      */       public void deviceRemoved(Device device) {}
/*      */       
/*      */       public void deviceManagerLoaded()
/*      */       {
/* 1885 */         DeviceManagerUI.this.device_manager.removeListener(this);
/* 1886 */         if ((entryHeader == null) || (entryHeader.isDisposed())) {
/* 1887 */           return;
/*      */         }
/* 1889 */         PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/*      */         
/* 1891 */         PluginInterface pi = pm.getPluginInterfaceByID("vuzexcode");
/* 1892 */         if ((DeviceManagerUI.this.device_manager.getTranscodeManager().getProviders().length == 0) || (pi == null))
/*      */         {
/*      */ 
/* 1895 */           final MdiEntryVitalityImage turnon = entryHeader.addVitalityImage("image.sidebar.turnon");
/* 1896 */           if (turnon != null) {
/* 1897 */             turnon.addListener(new com.aelitis.azureus.ui.mdi.MdiEntryVitalityImageListener() {
/*      */               public void mdiEntryVitalityImage_clicked(int x, int y) {
/* 1899 */                 DevicesFTUX.ensureInstalled(null);
/*      */               }
/*      */               
/* 1902 */             });
/* 1903 */             DeviceManagerUI.this.device_manager.getTranscodeManager().addListener(new com.aelitis.azureus.core.devices.TranscodeManagerListener()
/*      */             {
/*      */               public void providerAdded(TranscodeProvider provider)
/*      */               {
/* 1907 */                 turnon.setVisible(false);
/*      */               }
/*      */               
/*      */ 
/*      */               public void providerUpdated(TranscodeProvider provider) {}
/*      */               
/*      */ 
/*      */               public void providerRemoved(TranscodeProvider provider) {}
/*      */             });
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1920 */           DevicesFTUX.alreadyInstalledFixup();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void deviceChanged(Device device) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void deviceAttentionRequest(Device device) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void deviceAdded(Device device) {}
/* 1935 */     });
/* 1936 */     MdiEntryVitalityImage beta = entryHeader.addVitalityImage("image.sidebar.beta");
/* 1937 */     if (beta != null) {
/* 1938 */       beta.setAlignment(16384);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1943 */     MenuManager menu_manager = this.ui_manager.getMenuManager();
/*      */     
/* 1945 */     createOverallMenu(menu_manager, "sidebar.header.devices");
/* 1946 */     createOverallMenu(menu_manager, "sidebar.Devices");
/*      */   }
/*      */   
/*      */   private void createOverallMenu(final MenuManager menu_manager, String parentID) {
/* 1950 */     org.gudy.azureus2.plugins.ui.menus.MenuItem de_menu_item = menu_manager.addMenuItem(parentID, "device.search");
/*      */     
/* 1952 */     de_menu_item.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 1954 */         DeviceManagerUI.this.search();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 1959 */     });
/* 1960 */     de_menu_item = menu_manager.addMenuItem(parentID, "device.showGeneric");
/* 1961 */     de_menu_item.setStyle(2);
/* 1962 */     de_menu_item.addFillListener(new MenuItemFillListener()
/*      */     {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 1965 */         boolean is_hidden = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.hidegeneric", true);
/*      */         
/* 1967 */         menu.setData(Boolean.valueOf(!is_hidden));
/*      */         
/* 1969 */         boolean enabled = false;
/*      */         
/* 1971 */         if (is_hidden)
/*      */         {
/* 1973 */           Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */           
/* 1975 */           for (Device d : devices)
/*      */           {
/* 1977 */             if (!d.isHidden())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1982 */               if ((d instanceof DeviceMediaRenderer))
/*      */               {
/* 1984 */                 DeviceMediaRenderer rend = (DeviceMediaRenderer)d;
/*      */                 
/* 1986 */                 if (rend.isNonSimple())
/*      */                 {
/* 1988 */                   enabled = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         } else {
/* 1994 */           enabled = true;
/*      */         }
/*      */         
/* 1997 */         menu.setEnabled(enabled);
/*      */       }
/* 1999 */     });
/* 2000 */     de_menu_item.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2002 */         COConfigurationManager.setParameter("device.sidebar.ui.rend.hidegeneric", !COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.hidegeneric", true));
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2009 */     });
/* 2010 */     de_menu_item = menu_manager.addMenuItem(parentID, "device.onlyShowTagged");
/* 2011 */     de_menu_item.setStyle(2);
/* 2012 */     de_menu_item.addFillListener(new MenuItemFillListener() {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 2014 */         menu.setData(Boolean.valueOf(COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.showonlytagged", false)));
/*      */         
/*      */ 
/* 2017 */         Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */         
/* 2019 */         boolean has_tagged = false;
/*      */         
/* 2021 */         for (Device d : devices)
/*      */         {
/* 2023 */           if (d.isTagged())
/*      */           {
/* 2025 */             has_tagged = true;
/*      */             
/* 2027 */             break;
/*      */           }
/*      */         }
/*      */         
/* 2031 */         menu.setEnabled(has_tagged);
/*      */       }
/*      */       
/* 2034 */     });
/* 2035 */     de_menu_item.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2037 */         COConfigurationManager.setParameter("device.sidebar.ui.rend.showonlytagged", !COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.showonlytagged", false));
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2044 */     });
/* 2045 */     de_menu_item = menu_manager.addMenuItem(parentID, "device.show");
/*      */     
/* 2047 */     de_menu_item.addListener(this.show_listener);
/* 2048 */     de_menu_item.addFillListener(this.show_fill_listener);
/*      */     
/*      */ 
/*      */ 
/* 2052 */     de_menu_item = menu_manager.addMenuItem(parentID, "devices.sidebar.simple");
/*      */     
/* 2054 */     de_menu_item.setStyle(2);
/*      */     
/* 2056 */     de_menu_item.addFillListener(new MenuItemFillListener() {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 2058 */         menu.setData(Boolean.valueOf(COConfigurationManager.getIntParameter("device.sidebar.ui.viewtype", 0) == 0));
/*      */       }
/*      */       
/*      */ 
/* 2062 */     });
/* 2063 */     de_menu_item.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2065 */         COConfigurationManager.setParameter("device.sidebar.ui.viewtype", ((Boolean)menu.getData()).booleanValue() ? 0 : Integer.MAX_VALUE);
/*      */       }
/*      */       
/*      */ 
/* 2069 */     });
/* 2070 */     de_menu_item = menu_manager.addMenuItem(parentID, "sep");
/*      */     
/* 2072 */     de_menu_item.setStyle(4);
/*      */     
/*      */ 
/*      */ 
/* 2076 */     final org.gudy.azureus2.plugins.ui.menus.MenuItem de_oxc_menu = menu_manager.addMenuItem(parentID, "devices.sidebar.onxcodecomplete");
/*      */     
/* 2078 */     de_oxc_menu.setStyle(5);
/*      */     
/* 2080 */     de_oxc_menu.addFillListener(new MenuItemFillListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*      */       {
/*      */ 
/* 2087 */         menu.removeAllChildItems();
/*      */         
/* 2089 */         List<org.gudy.azureus2.plugins.ui.menus.MenuItem> oxc_items = new ArrayList();
/*      */         
/* 2091 */         final org.gudy.azureus2.plugins.ui.menus.MenuItem oxc_nothing = menu_manager.addMenuItem(de_oxc_menu, "devices.sidebar.oxc.nothing");
/* 2092 */         oxc_items.add(oxc_nothing);
/* 2093 */         oxc_nothing.setStyle(2);
/* 2094 */         oxc_nothing.setData(Boolean.valueOf(DeviceManagerUI.this.oxc_action == "Nothing"));
/*      */         
/* 2096 */         final org.gudy.azureus2.plugins.ui.menus.MenuItem oxc_close_vuze = menu_manager.addMenuItem(de_oxc_menu, "devices.sidebar.oxc.closevuze");
/* 2097 */         oxc_items.add(oxc_close_vuze);
/* 2098 */         oxc_close_vuze.setStyle(2);
/* 2099 */         oxc_close_vuze.setData(Boolean.valueOf(DeviceManagerUI.this.oxc_action == "QuitVuze"));
/*      */         
/* 2101 */         PlatformManager pm = org.gudy.azureus2.platform.PlatformManagerFactory.getPlatformManager();
/*      */         
/* 2103 */         int sdt = pm.getShutdownTypes();
/*      */         
/* 2105 */         final Map<org.gudy.azureus2.plugins.ui.menus.MenuItem, String> oxc_pm_map = new java.util.HashMap();
/*      */         
/* 2107 */         for (int type : PlatformManager.SD_ALL)
/*      */         {
/* 2109 */           if ((sdt | type) != 0)
/*      */           {
/* 2111 */             String action = "Nothing";
/*      */             
/* 2113 */             if (type == 4) {
/* 2114 */               action = "Sleep";
/* 2115 */             } else if (type == 2) {
/* 2116 */               action = "Hibernate";
/* 2117 */             } else if (type == 1) {
/* 2118 */               action = "Shutdown";
/*      */             } else {
/* 2120 */               Debug.out("Unknown type: " + type);
/*      */             }
/*      */             
/* 2123 */             org.gudy.azureus2.plugins.ui.menus.MenuItem oxc_pm = menu_manager.addMenuItem(de_oxc_menu, "devices.sidebar.oxc.pm." + type);
/* 2124 */             oxc_items.add(oxc_pm);
/* 2125 */             oxc_pm.setStyle(2);
/* 2126 */             oxc_pm.setData(Boolean.valueOf(DeviceManagerUI.this.oxc_action == action));
/*      */             
/* 2128 */             oxc_pm_map.put(oxc_pm, action);
/*      */           }
/*      */         }
/*      */         
/* 2132 */         MenuItemListener oxc_mil = new MenuItemListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */           {
/*      */ 
/*      */ 
/* 2140 */             if (((Boolean)menu.getData()).booleanValue())
/*      */             {
/* 2142 */               if (menu == oxc_nothing)
/*      */               {
/* 2144 */                 DeviceManagerUI.this.setOXCState("Nothing");
/*      */               }
/* 2146 */               else if (menu == oxc_close_vuze)
/*      */               {
/* 2148 */                 DeviceManagerUI.this.setOXCState("QuitVuze");
/*      */               }
/*      */               else
/*      */               {
/* 2152 */                 DeviceManagerUI.this.setOXCState((String)oxc_pm_map.get(menu));
/*      */               }
/*      */             }
/*      */           }
/*      */         };
/*      */         
/* 2158 */         for (org.gudy.azureus2.plugins.ui.menus.MenuItem mi : oxc_items)
/*      */         {
/* 2160 */           mi.addListener(oxc_mil);
/*      */         }
/*      */         
/*      */       }
/* 2164 */     });
/* 2165 */     de_menu_item = menu_manager.addMenuItem(parentID, "sep2");
/*      */     
/* 2167 */     de_menu_item.setStyle(4);
/*      */     
/*      */ 
/*      */ 
/* 2171 */     de_menu_item = menu_manager.addMenuItem(parentID, "MainWindow.menu.view.configuration");
/*      */     
/* 2173 */     de_menu_item.addListener(new MenuItemListener() {
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2175 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */         
/* 2177 */         if (uif != null)
/*      */         {
/* 2179 */           uif.getMDI().showEntryByID("ConfigView", "Devices");
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*      */ 
/* 2186 */     if (Constants.isCVSVersion()) {
/* 2187 */       de_menu_item = menu_manager.addMenuItem(parentID, "!(CVS Only)Show FTUX!");
/*      */       
/* 2189 */       de_menu_item.addListener(new MenuItemListener()
/*      */       {
/*      */         public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {}
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addNewDevice()
/*      */   {
/* 2204 */     ManufacturerChooser mfChooser = new ManufacturerChooser();
/* 2205 */     mfChooser.open(new com.aelitis.azureus.ui.swt.devices.add.ManufacturerChooser.ClosedListener() {
/*      */       public void MfChooserClosed(com.aelitis.azureus.core.devices.DeviceManager.DeviceManufacturer mf) {
/* 2207 */         if (mf == null) {
/* 2208 */           return;
/*      */         }
/* 2210 */         DeviceTemplateChooser deviceTemplateChooser = new DeviceTemplateChooser(mf);
/*      */         
/* 2212 */         deviceTemplateChooser.open(new com.aelitis.azureus.ui.swt.devices.add.DeviceTemplateChooser.DeviceTemplateClosedListener() {
/*      */           public void deviceTemplateChooserClosed(DeviceTemplate deviceTemplate) {
/* 2214 */             if (deviceTemplate == null) {
/* 2215 */               return;
/*      */             }
/*      */             
/*      */             try
/*      */             {
/* 2220 */               Device device = deviceTemplate.createInstance(deviceTemplate.getName() + " test!");
/* 2221 */               device.requestAttention();
/*      */             } catch (com.aelitis.azureus.core.devices.DeviceManagerException e) {
/* 2223 */               Debug.out(e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setOXCState(String new_action)
/*      */   {
/* 2247 */     this.oxc_action = new_action;
/*      */     
/* 2249 */     checkOXCState();
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkOXCState()
/*      */   {
/* 2255 */     if (this.oxc_action == "Nothing")
/*      */     {
/* 2257 */       this.oxc_trigger_set = false;
/*      */       
/* 2259 */       return;
/*      */     }
/*      */     
/* 2262 */     int jobs = this.device_manager.getTranscodeManager().getQueue().getJobCount();
/*      */     
/* 2264 */     if (jobs > 0)
/*      */     {
/* 2266 */       this.oxc_trigger_set = true;
/*      */ 
/*      */ 
/*      */     }
/* 2270 */     else if (this.oxc_trigger_set)
/*      */     {
/* 2272 */       this.oxc_trigger_set = false;
/*      */       
/* 2274 */       AzureusCoreFactory.getSingleton().executeCloseAction(this.oxc_action, MessageText.getString("core.shutdown.xcode"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setupTranscodeMenus()
/*      */   {
/* 2284 */     String[] tables = { "MyTorrents", "MyTorrents.big", "MySeeders", "MySeeders.big", "Files", "Unopened", "Unopened.big", "MyLibrary.big" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2295 */     TableManager table_manager = this.plugin_interface.getUIManager().getTableManager();
/*      */     
/* 2297 */     MenuItemFillListener menu_fill_listener = new MenuItemFillListener()
/*      */     {
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object _target)
/*      */       {
/*      */         TableRow[] target;
/*      */         
/*      */ 
/*      */         final TableRow[] target;
/*      */         
/*      */ 
/* 2307 */         if ((_target instanceof TableRow))
/*      */         {
/* 2309 */           target = new TableRow[] { (TableRow)_target };
/*      */         }
/*      */         else
/*      */         {
/* 2313 */           target = (TableRow[])_target;
/*      */         }
/*      */         
/* 2316 */         boolean enabled = target.length > 0;
/*      */         
/* 2318 */         for (TableRow row : target)
/*      */         {
/* 2320 */           Object obj = row.getDataSource();
/*      */           
/* 2322 */           if ((obj instanceof Download))
/*      */           {
/* 2324 */             Download download = (Download)obj;
/*      */             
/* 2326 */             if (download.getState() == 8)
/*      */             {
/* 2328 */               enabled = false;
/*      */             }
/*      */           }
/*      */           else {
/* 2332 */             DiskManagerFileInfo file = (DiskManagerFileInfo)obj;
/*      */             try
/*      */             {
/* 2335 */               if (file.getDownload().getState() == 8)
/*      */               {
/* 2337 */                 enabled = false;
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 2341 */               enabled = false;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2346 */         menu.setEnabled(enabled);
/*      */         
/* 2348 */         menu.removeAllChildItems();
/*      */         
/* 2350 */         if (enabled)
/*      */         {
/* 2352 */           Device[] devices = DeviceManagerUI.this.device_manager.getDevices();
/*      */           
/* 2354 */           int devices_added = 0;
/*      */           
/* 2356 */           for (Device device : devices)
/*      */           {
/* 2358 */             if (!device.isHidden())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2363 */               if ((device instanceof TranscodeTarget))
/*      */               {
/* 2365 */                 devices_added++;
/*      */                 
/* 2367 */                 final TranscodeTarget renderer = (TranscodeTarget)device;
/*      */                 
/* 2369 */                 TranscodeProfile[] profiles = renderer.getTranscodeProfiles();
/*      */                 
/*      */ 
/* 2372 */                 TableContextMenuItem device_item = DeviceManagerUI.this.plugin_interface.getUIManager().getTableManager().addContextMenuItem((TableContextMenuItem)menu, "!" + device.getName() + (profiles.length == 0 ? " (No Profiles)" : "") + "!");
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 2377 */                 device_item.setStyle(5);
/*      */                 
/* 2379 */                 if (profiles.length == 0)
/*      */                 {
/* 2381 */                   device_item.setEnabled(false);
/*      */                 }
/*      */                 else
/*      */                 {
/* 2385 */                   Arrays.sort(profiles, new Comparator() {
/*      */                     public int compare(TranscodeProfile o1, TranscodeProfile o2) {
/* 2387 */                       int i1 = o1.getIconIndex();
/* 2388 */                       int i2 = o2.getIconIndex();
/*      */                       
/* 2390 */                       if (i1 == i2)
/*      */                       {
/* 2392 */                         return o1.getName().compareToIgnoreCase(o2.getName());
/*      */                       }
/*      */                       
/* 2395 */                       return i1 - i2;
/*      */                     }
/*      */                   });
/*      */                   
/*      */ 
/*      */ 
/* 2401 */                   for (final TranscodeProfile profile : profiles)
/*      */                   {
/* 2403 */                     TableContextMenuItem profile_item = DeviceManagerUI.this.plugin_interface.getUIManager().getTableManager().addContextMenuItem(device_item, "!" + profile.getName() + "!");
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/* 2408 */                     profile_item.addMultiListener(new MenuItemListener()
/*      */                     {
/*      */ 
/*      */ 
/*      */                       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object x)
/*      */                       {
/*      */ 
/*      */ 
/* 2416 */                         for (TableRow row : target)
/*      */                         {
/* 2418 */                           Object obj = row.getDataSource();
/*      */                           try
/*      */                           {
/* 2421 */                             if ((obj instanceof Download))
/*      */                             {
/* 2423 */                               Download download = (Download)obj;
/*      */                               
/* 2425 */                               DeviceManagerUI.addDownload(renderer, profile, -1, download);
/*      */                             }
/*      */                             else
/*      */                             {
/* 2429 */                               DiskManagerFileInfo file = (DiskManagerFileInfo)obj;
/*      */                               
/* 2431 */                               DeviceManagerUI.addFile(renderer, profile, -1, file);
/*      */                             }
/*      */                           }
/*      */                           catch (Throwable e) {
/* 2435 */                             Debug.out(e);
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 2445 */           if (devices_added == 0)
/*      */           {
/* 2447 */             TableContextMenuItem device_item = DeviceManagerUI.this.plugin_interface.getUIManager().getTableManager().addContextMenuItem((TableContextMenuItem)menu, "!(No Devices)!");
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2452 */             device_item.setEnabled(false);
/*      */           }
/*      */         }
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2461 */     for (String table : tables)
/*      */     {
/* 2463 */       TableContextMenuItem menu = table_manager.addContextMenuItem(table, "devices.contextmenu.xcode");
/*      */       
/* 2465 */       menu.setStyle(5);
/*      */       
/* 2467 */       menu.addFillListener(menu_fill_listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void setupOfflineDownloadingMenus()
/*      */   {
/* 2474 */     String[] tables = { "MyTorrents", "MyTorrents.big", "MyLibrary.big" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2480 */     TableManager table_manager = this.plugin_interface.getUIManager().getTableManager();
/*      */     
/* 2482 */     final DeviceOfflineDownloaderManager dodm = this.device_manager.getOfflineDownlaoderManager();
/*      */     
/* 2484 */     MenuItemFillListener menu_fill_listener = new MenuItemFillListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object _target)
/*      */       {
/*      */ 
/*      */ 
/* 2492 */         menu.removeAllChildItems();
/*      */         
/* 2494 */         if (dodm.getOfflineDownloadingIsAuto())
/*      */         {
/* 2496 */           menu.setEnabled(true);
/*      */           
/* 2498 */           TableContextMenuItem auto_item = DeviceManagerUI.this.plugin_interface.getUIManager().getTableManager().addContextMenuItem((TableContextMenuItem)menu, "devices.contextmenu.od.auto");
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2503 */           auto_item.setEnabled(false); return;
/*      */         }
/*      */         
/*      */         TableRow[] target;
/*      */         
/*      */         TableRow[] target;
/*      */         
/* 2510 */         if ((_target instanceof TableRow))
/*      */         {
/* 2512 */           target = new TableRow[] { (TableRow)_target };
/*      */         }
/*      */         else
/*      */         {
/* 2516 */           target = (TableRow[])_target;
/*      */         }
/*      */         
/* 2519 */         boolean all_non_manual = true;
/* 2520 */         boolean all_manual = true;
/*      */         
/* 2522 */         final List<Download> downloads = new ArrayList();
/*      */         
/* 2524 */         for (TableRow row : target)
/*      */         {
/* 2526 */           Object obj = row.getDataSource();
/*      */           
/* 2528 */           if ((obj instanceof Download))
/*      */           {
/* 2530 */             Download download = (Download)obj;
/*      */             
/* 2532 */             downloads.add(download);
/*      */             
/* 2534 */             if (dodm.isManualDownload(download))
/*      */             {
/* 2536 */               all_non_manual = false;
/*      */             }
/*      */             else
/*      */             {
/* 2540 */               all_manual = false;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2545 */         boolean enabled = downloads.size() > 0;
/*      */         
/* 2547 */         menu.setEnabled(enabled);
/*      */         
/* 2549 */         if (enabled)
/*      */         {
/* 2551 */           TableContextMenuItem manual_item = DeviceManagerUI.this.plugin_interface.getUIManager().getTableManager().addContextMenuItem((TableContextMenuItem)menu, "devices.contextmenu.od.enable" + (all_manual ? "d" : ""));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2556 */           final boolean f_all_manual = all_manual;
/*      */           
/* 2558 */           manual_item.setData(Boolean.valueOf(f_all_manual));
/*      */           
/* 2560 */           manual_item.setStyle(2);
/*      */           
/* 2562 */           manual_item.addListener(new MenuItemListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */             {
/*      */ 
/*      */ 
/* 2570 */               Download[] d = (Download[])downloads.toArray(new Download[downloads.size()]);
/*      */               
/* 2572 */               if (f_all_manual)
/*      */               {
/* 2574 */                 DeviceManagerUI.58.this.val$dodm.removeManualDownloads(d);
/*      */               }
/*      */               else
/*      */               {
/* 2578 */                 DeviceManagerUI.58.this.val$dodm.addManualDownloads(d);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2589 */     for (String table : tables)
/*      */     {
/* 2591 */       TableContextMenuItem menu = table_manager.addContextMenuItem(table, "devices.contextmenu.od");
/*      */       
/* 2593 */       menu.setStyle(5);
/*      */       
/* 2595 */       menu.addFillListener(menu_fill_listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void search()
/*      */   {
/* 2602 */     this.device_manager.search(10000, new com.aelitis.azureus.core.devices.DeviceSearchListener()
/*      */     {
/*      */       public void deviceFound(Device device) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete() {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addOrChangeDevice(Device device)
/*      */   {
/* 2623 */     int type = device.getType();
/*      */     
/* 2625 */     if ((!this.device_manager.getOfflineDownlaoderManager().isOfflineDownloadingEnabled()) && (type == 5))
/*      */     {
/* 2627 */       return;
/*      */     }
/*      */     
/* 2630 */     String parent_key = null;
/*      */     
/* 2632 */     if (this.side_bar_view_type == Integer.MAX_VALUE)
/*      */     {
/* 2634 */       for (categoryView view : this.categories)
/*      */       {
/* 2636 */         if (view.getDeviceType() == type)
/*      */         {
/* 2638 */           parent_key = view.getKey();
/*      */           
/* 2640 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2645 */       if ((type != 3) && (type != 5))
/*      */       {
/* 2647 */         return;
/*      */       }
/*      */       
/* 2650 */       parent_key = "header.devices";
/*      */     }
/*      */     
/* 2653 */     if (parent_key == null)
/*      */     {
/* 2655 */       Debug.out("Unknown device type: " + device.getString());
/*      */       
/* 2657 */       return;
/*      */     }
/*      */     
/* 2660 */     boolean hide_device = device.isHidden();
/*      */     
/* 2662 */     if ((type == 3) && (this.side_bar_hide_rend_gen))
/*      */     {
/* 2664 */       DeviceMediaRenderer rend = (DeviceMediaRenderer)device;
/*      */       
/* 2666 */       if (rend.isNonSimple())
/*      */       {
/* 2668 */         hide_device = true;
/*      */       }
/*      */     }
/*      */     
/* 2672 */     if ((this.side_bar_show_tagged) && (!device.isTagged()))
/*      */     {
/* 2674 */       hide_device = true;
/*      */     }
/*      */     
/* 2677 */     if (hide_device)
/*      */     {
/* 2679 */       removeDevice(device);
/*      */       
/* 2681 */       return;
/*      */     }
/*      */     
/* 2684 */     String parent = parent_key;
/*      */     
/* 2686 */     synchronized (this)
/*      */     {
/* 2688 */       deviceItem existing_di = (deviceItem)device.getTransientProperty(DEVICE_IVIEW_KEY);
/*      */       
/* 2690 */       if (existing_di == null)
/*      */       {
/* 2692 */         if (type == 5)
/*      */         {
/* 2694 */           if (!this.offline_menus_setup)
/*      */           {
/* 2696 */             this.offline_menus_setup = true;
/*      */             
/* 2698 */             setupOfflineDownloadingMenus();
/*      */           }
/*      */           
/* 2701 */           DeviceOfflineDownloader dod = (DeviceOfflineDownloader)device;
/*      */           
/* 2703 */           if (!dod.hasShownFTUX()) {
/*      */             try
/*      */             {
/* 2706 */               new DevicesODFTUX(dod);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2710 */               Debug.out("Failed to show offline downloader FTUX", e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2715 */         if (!device.isHidden())
/*      */         {
/* 2717 */           deviceItem new_di = new deviceItem();
/*      */           
/* 2719 */           device.setTransientProperty(DEVICE_IVIEW_KEY, new_di);
/*      */           
/* 2721 */           setupEntry(new_di, device, parent);
/*      */         }
/*      */       }
/*      */       else {
/* 2725 */         ViewTitleInfoManager.refreshTitleInfo(existing_di.getView());
/*      */         
/* 2727 */         setStatus(device, existing_di);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void setupEntry(deviceItem new_di, final Device device, String parent) {
/* 2733 */     synchronized (this)
/*      */     {
/* 2735 */       if (new_di.isDestroyed())
/*      */       {
/* 2737 */         return;
/*      */       }
/*      */       
/* 2740 */       deviceView view = new deviceView(parent, device);
/*      */       
/* 2742 */       new_di.setView(view);
/*      */       
/* 2744 */       String key = parent + "/" + device.getID() + ":" + nextSidebarID();
/*      */       
/*      */ 
/*      */ 
/* 2748 */       int device_type = device.getType();
/*      */       MdiEntry entry;
/* 2750 */       MdiEntry entry; if (device_type == 3)
/*      */       {
/* 2752 */         entry = this.mdi.createEntryFromSkinRef(parent, key, "devicerendererview", device.getName(), view, null, false, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 2759 */       else if (device_type == 5)
/*      */       {
/* 2761 */         MdiEntry entry = this.mdi.createEntryFromSkinRef(parent, key, "devicesodview", device.getName(), view, null, false, null);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2767 */         entry.setExpanded(true);
/*      */         
/*      */ 
/* 2770 */         DeviceOfflineDownloader dod = (DeviceOfflineDownloader)device;
/*      */         
/*      */ 
/*      */ 
/* 2774 */         String manufacturer = dod.getManufacturer();
/*      */         String id;
/* 2776 */         String id; if (manufacturer.toLowerCase().contains("vuze"))
/*      */         {
/* 2778 */           id = "vuze";
/*      */         } else { String id;
/* 2780 */           if (manufacturer.toLowerCase().contains("belkin"))
/*      */           {
/* 2782 */             id = "bel";
/*      */           }
/*      */           else
/*      */           {
/* 2786 */             id = "other";
/*      */           }
/*      */         }
/* 2789 */         entry.setImageLeftID("image.sidebar.device.od." + id + ".small");
/*      */       }
/*      */       else
/*      */       {
/* 2793 */         entry = this.mdi.createEntryFromEventListener(parent, view, key, false, device, null);
/*      */         
/* 2795 */         entry.setExpanded(true);
/*      */       }
/*      */       
/*      */ 
/* 2799 */       entry.setDatasource(device);
/*      */       
/* 2801 */       entry.setLogID(parent + "-" + device.getName());
/*      */       
/* 2803 */       new_di.setMdiEntry(entry);
/*      */       
/* 2805 */       setStatus(device, new_di);
/*      */       
/* 2807 */       if ((device instanceof TranscodeTarget))
/*      */       {
/* 2809 */         entry.addListener(new com.aelitis.azureus.ui.mdi.MdiEntryDropListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean mdiEntryDrop(MdiEntry entry, Object payload)
/*      */           {
/*      */ 
/*      */ 
/* 2817 */             return DeviceManagerUI.handleDrop((TranscodeTarget)device, payload);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 2822 */       final MenuManager menu_manager = this.ui_manager.getMenuManager();
/*      */       
/* 2824 */       boolean need_sep = false;
/*      */       
/* 2826 */       if ((device instanceof TranscodeTarget))
/*      */       {
/* 2828 */         need_sep = true;
/*      */         
/* 2830 */         org.gudy.azureus2.plugins.ui.menus.MenuItem explore_menu_item = menu_manager.addMenuItem("sidebar." + key, "v3.menu.device.exploreTranscodes");
/*      */         
/* 2832 */         explore_menu_item.addListener(new MenuItemListener() {
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2834 */             org.gudy.azureus2.ui.swt.views.utils.ManagerUtils.open(((TranscodeTarget)device).getWorkingDirectory());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 2839 */       if ((device instanceof DeviceMediaRenderer))
/*      */       {
/* 2841 */         need_sep = true;
/*      */         
/*      */ 
/* 2844 */         final DeviceMediaRenderer renderer = (DeviceMediaRenderer)device;
/*      */         
/* 2846 */         if (renderer.canFilterFilesView()) {
/* 2847 */           org.gudy.azureus2.plugins.ui.menus.MenuItem filterfiles_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.only.show");
/* 2848 */           filterfiles_menu_item.setStyle(2);
/*      */           
/* 2850 */           filterfiles_menu_item.addFillListener(new MenuItemFillListener() {
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 2852 */               menu.setData(Boolean.valueOf(renderer.getFilterFilesView()));
/*      */             }
/* 2854 */           });
/* 2855 */           filterfiles_menu_item.addListener(new MenuItemListener() {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2857 */               renderer.setFilterFilesView(((Boolean)menu.getData()).booleanValue());
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2864 */         if (renderer.canShowCategories()) {
/* 2865 */           org.gudy.azureus2.plugins.ui.menus.MenuItem showcat_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.show.cat");
/* 2866 */           showcat_menu_item.setStyle(2);
/*      */           
/* 2868 */           showcat_menu_item.addFillListener(new MenuItemFillListener() {
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 2870 */               menu.setData(Boolean.valueOf(renderer.getShowCategories()));
/*      */             }
/* 2872 */           });
/* 2873 */           showcat_menu_item.addListener(new MenuItemListener() {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2875 */               renderer.setShowCategories(((Boolean)menu.getData()).booleanValue());
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2882 */         org.gudy.azureus2.plugins.ui.menus.MenuItem alwayscache_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.always.cache");
/* 2883 */         alwayscache_menu_item.setStyle(2);
/*      */         
/* 2885 */         alwayscache_menu_item.addFillListener(new MenuItemFillListener() {
/*      */           public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 2887 */             menu.setData(Boolean.valueOf(renderer.getAlwaysCacheFiles()));
/*      */           }
/* 2889 */         });
/* 2890 */         alwayscache_menu_item.addListener(new MenuItemListener() {
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2892 */             renderer.setAlwaysCacheFiles(((Boolean)menu.getData()).booleanValue());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/* 2898 */       if (need_sep)
/*      */       {
/* 2900 */         menu_manager.addMenuItem("sidebar." + key, "1").setStyle(4);
/*      */       }
/*      */       
/* 2903 */       need_sep = false;
/*      */       
/* 2905 */       if ((device instanceof DeviceMediaRenderer))
/*      */       {
/* 2907 */         final DeviceMediaRenderer renderer = (DeviceMediaRenderer)device;
/*      */         
/* 2909 */         if (renderer.canCopyToFolder())
/*      */         {
/* 2911 */           need_sep = true;
/*      */           
/* 2913 */           org.gudy.azureus2.plugins.ui.menus.MenuItem autocopy_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.autoCopy");
/* 2914 */           autocopy_menu_item.setStyle(2);
/*      */           
/* 2916 */           autocopy_menu_item.addFillListener(new MenuItemFillListener() {
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 2918 */               menu.setData(Boolean.valueOf(renderer.getAutoCopyToFolder()));
/*      */             }
/* 2920 */           });
/* 2921 */           autocopy_menu_item.addListener(new MenuItemListener() {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 2923 */               renderer.setAutoCopyToFolder(((Boolean)menu.getData()).booleanValue());
/*      */             }
/*      */             
/* 2926 */           });
/* 2927 */           final org.gudy.azureus2.plugins.ui.menus.MenuItem mancopy_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.mancopy");
/* 2928 */           mancopy_menu_item.setStyle(1);
/*      */           
/* 2930 */           mancopy_menu_item.addListener(new MenuItemListener()
/*      */           {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */             {
/*      */               try
/*      */               {
/* 2936 */                 renderer.manualCopy();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 2940 */                 Debug.out(e);
/*      */               }
/*      */               
/*      */             }
/* 2944 */           });
/* 2945 */           mancopy_menu_item.addFillListener(new MenuItemFillListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*      */             {
/*      */ 
/* 2952 */               boolean enabled = false;
/*      */               
/* 2954 */               if (!renderer.getAutoCopyToFolder())
/*      */               {
/* 2956 */                 File target = renderer.getCopyToFolder();
/*      */                 
/* 2958 */                 if ((target != null) && (target.exists()))
/*      */                 {
/* 2960 */                   enabled = renderer.getCopyToFolderPending() > 0;
/*      */                 }
/*      */               }
/* 2963 */               mancopy_menu_item.setEnabled(enabled);
/*      */             }
/*      */             
/* 2966 */           });
/* 2967 */           org.gudy.azureus2.plugins.ui.menus.MenuItem setcopyto_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.setcopyto");
/* 2968 */           setcopyto_menu_item.setStyle(1);
/*      */           
/*      */ 
/* 2971 */           setcopyto_menu_item.addListener(new MenuItemListener()
/*      */           {
/*      */ 
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */             {
/* 2976 */               org.eclipse.swt.widgets.Shell shell = Utils.findAnyShell();
/*      */               
/* 2978 */               DirectoryDialog dd = new DirectoryDialog(shell);
/*      */               
/* 2980 */               File existing = renderer.getCopyToFolder();
/*      */               
/* 2982 */               if (existing != null)
/*      */               {
/* 2984 */                 dd.setFilterPath(existing.getAbsolutePath());
/*      */               }
/*      */               
/* 2987 */               dd.setText(MessageText.getString("devices.xcode.setcopyto.title"));
/*      */               
/* 2989 */               String path = dd.open();
/*      */               
/* 2991 */               if (path != null)
/*      */               {
/* 2993 */                 renderer.setCopyToFolder(new File(path));
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3001 */         if (renderer.canCopyToDevice())
/*      */         {
/* 3003 */           need_sep = true;
/*      */           
/* 3005 */           org.gudy.azureus2.plugins.ui.menus.MenuItem autocopy_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.autoCopy.device");
/* 3006 */           autocopy_menu_item.setStyle(2);
/*      */           
/* 3008 */           autocopy_menu_item.addFillListener(new MenuItemFillListener() {
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3010 */               menu.setData(Boolean.valueOf(renderer.getAutoCopyToDevice()));
/*      */             }
/* 3012 */           });
/* 3013 */           autocopy_menu_item.addListener(new MenuItemListener() {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 3015 */               renderer.setAutoCopyToDevice(((Boolean)menu.getData()).booleanValue());
/*      */             }
/*      */             
/* 3018 */           });
/* 3019 */           final org.gudy.azureus2.plugins.ui.menus.MenuItem mancopy_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.mancopy");
/* 3020 */           mancopy_menu_item.setStyle(1);
/*      */           
/* 3022 */           mancopy_menu_item.addListener(new MenuItemListener()
/*      */           {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */             {
/*      */               try
/*      */               {
/* 3028 */                 renderer.manualCopy();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 3032 */                 Debug.out(e);
/*      */               }
/*      */               
/*      */             }
/* 3036 */           });
/* 3037 */           mancopy_menu_item.addFillListener(new MenuItemFillListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*      */             {
/*      */ 
/* 3044 */               boolean enabled = false;
/*      */               
/* 3046 */               if (!renderer.getAutoCopyToDevice())
/*      */               {
/* 3048 */                 enabled = renderer.getCopyToDevicePending() > 0;
/*      */               }
/*      */               
/* 3051 */               mancopy_menu_item.setEnabled(enabled);
/*      */             }
/*      */           });
/*      */         }
/*      */         
/* 3056 */         if (renderer.canAutoStartDevice())
/*      */         {
/* 3058 */           need_sep = true;
/*      */           
/* 3060 */           org.gudy.azureus2.plugins.ui.menus.MenuItem autostart_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.autoStart");
/* 3061 */           autostart_menu_item.setStyle(2);
/*      */           
/* 3063 */           autostart_menu_item.addFillListener(new MenuItemFillListener() {
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3065 */               menu.setData(Boolean.valueOf(renderer.getAutoStartDevice()));
/*      */             }
/* 3067 */           });
/* 3068 */           autostart_menu_item.addListener(new MenuItemListener() {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 3070 */               renderer.setAutoStartDevice(((Boolean)menu.getData()).booleanValue());
/*      */             }
/*      */           });
/*      */         }
/*      */         
/* 3075 */         if (renderer.canAssociate())
/*      */         {
/* 3077 */           need_sep = true;
/*      */           
/* 3079 */           final org.gudy.azureus2.plugins.ui.menus.MenuItem menu_associate = menu_manager.addMenuItem("sidebar." + key, "devices.associate");
/*      */           
/*      */ 
/* 3082 */           menu_associate.setStyle(5);
/*      */           
/* 3084 */           menu_associate.addFillListener(new MenuItemFillListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*      */             {
/*      */ 
/* 3091 */               menu_associate.removeAllChildItems();
/*      */               
/* 3093 */               if (renderer.isAlive())
/*      */               {
/* 3095 */                 InetAddress a = renderer.getAddress();
/*      */                 
/* 3097 */                 String address = a == null ? "" : a.getHostAddress();
/*      */                 
/* 3099 */                 org.gudy.azureus2.plugins.ui.menus.MenuItem menu_none = menu_manager.addMenuItem(menu_associate, "!" + MessageText.getString("devices.associate.already") + ": " + address + "!");
/*      */                 
/*      */ 
/*      */ 
/* 3103 */                 menu_none.setEnabled(false);
/*      */                 
/* 3105 */                 menu_associate.setEnabled(true);
/*      */               }
/*      */               else
/*      */               {
/* 3109 */                 DeviceManager.UnassociatedDevice[] unassoc = DeviceManagerUI.this.device_manager.getUnassociatedDevices();
/*      */                 
/* 3111 */                 if (unassoc.length == 0)
/*      */                 {
/* 3113 */                   menu_associate.setEnabled(false);
/*      */                 }
/*      */                 else
/*      */                 {
/* 3117 */                   menu_associate.setEnabled(true);
/*      */                   
/* 3119 */                   for (final DeviceManager.UnassociatedDevice un : unassoc)
/*      */                   {
/* 3121 */                     org.gudy.azureus2.plugins.ui.menus.MenuItem menu_un = menu_manager.addMenuItem(menu_associate, "!" + un.getAddress().getHostAddress() + ": " + un.getDescription() + "!");
/*      */                     
/*      */ 
/*      */ 
/* 3125 */                     menu_un.addListener(new MenuItemListener()
/*      */                     {
/*      */ 
/*      */ 
/*      */                       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */                       {
/*      */ 
/*      */ 
/* 3133 */                         DeviceManagerUI.79.this.val$renderer.associate(un);
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/* 3144 */         if (renderer.canRestrictAccess())
/*      */         {
/* 3146 */           need_sep = true;
/*      */           
/* 3148 */           org.gudy.azureus2.plugins.ui.menus.MenuItem menu_ra = menu_manager.addMenuItem("sidebar." + key, "devices.restrict_access");
/*      */           
/*      */ 
/* 3151 */           menu_ra.addListener(new MenuItemListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */             {
/*      */ 
/*      */ 
/* 3159 */               if ((target instanceof MdiEntry))
/*      */               {
/* 3161 */                 UISWTInputReceiver entry = (UISWTInputReceiver)DeviceManagerUI.this.swt_ui.getInputReceiver();
/*      */                 
/* 3163 */                 entry.setMessage("devices.restrict_access.msg");
/*      */                 
/* 3165 */                 entry.setPreenteredText(renderer.getAccessRestriction(), false);
/*      */                 
/* 3167 */                 entry.maintainWhitespace(false);
/*      */                 
/* 3169 */                 entry.allowEmptyInput(true);
/*      */                 
/* 3171 */                 entry.setLocalisedTitle(MessageText.getString("devices.restrict_access.prompt", new String[] { device.getName() }));
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3177 */                 entry.prompt(new org.gudy.azureus2.plugins.ui.UIInputReceiverListener() {
/*      */                   public void UIInputReceiverClosed(UIInputReceiver entry) {
/* 3179 */                     if (!entry.hasSubmittedInput()) {
/* 3180 */                       return;
/*      */                     }
/* 3182 */                     String input = entry.getSubmittedInput().trim();
/*      */                     
/* 3184 */                     DeviceManagerUI.80.this.val$renderer.setAccessRestriction(input);
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/* 3193 */         final TranscodeProfile[] transcodeProfiles = renderer.getTranscodeProfiles();
/*      */         
/* 3195 */         if (transcodeProfiles.length > 0) {
/* 3196 */           Arrays.sort(transcodeProfiles, new Comparator() {
/*      */             public int compare(TranscodeProfile o1, TranscodeProfile o2) {
/* 3198 */               int i1 = o1.getIconIndex();
/* 3199 */               int i2 = o2.getIconIndex();
/*      */               
/* 3201 */               if (i1 == i2)
/*      */               {
/* 3203 */                 return o1.getName().compareToIgnoreCase(o2.getName());
/*      */               }
/*      */               
/* 3206 */               return i1 - i2;
/*      */             }
/*      */             
/*      */ 
/* 3210 */           });
/* 3211 */           need_sep = true;
/*      */           
/* 3213 */           org.gudy.azureus2.plugins.ui.menus.MenuItem menu_default_profile = menu_manager.addMenuItem("sidebar." + key, "v3.menu.device.defaultprofile");
/*      */           
/* 3215 */           menu_default_profile.setStyle(5);
/*      */           
/* 3217 */           org.gudy.azureus2.plugins.ui.menus.MenuItem menu_profile_never = menu_manager.addMenuItem(menu_default_profile, "v3.menu.device.defaultprofile.never");
/*      */           
/* 3219 */           menu_profile_never.setStyle(2);
/* 3220 */           menu_profile_never.setData(Boolean.TRUE);
/* 3221 */           menu_profile_never.addListener(new MenuItemListener() {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 3223 */               renderer.setTranscodeRequirement(((Boolean)menu.getData()).booleanValue() ? 1 : 2);
/*      */             }
/*      */             
/* 3226 */           });
/* 3227 */           menu_profile_never.addFillListener(new MenuItemFillListener() {
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3229 */               boolean never = renderer.getTranscodeRequirement() == 1;
/* 3230 */               menu.setData(Boolean.valueOf(never));
/*      */             }
/* 3232 */           });
/* 3233 */           org.gudy.azureus2.plugins.ui.menus.MenuItem menu_profile_none = menu_manager.addMenuItem(menu_default_profile, "option.askeverytime");
/*      */           
/* 3235 */           menu_profile_none.setStyle(3);
/* 3236 */           menu_profile_none.setData(Boolean.FALSE);
/* 3237 */           menu_profile_none.addListener(new MenuItemListener() {
/*      */             public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 3239 */               renderer.setDefaultTranscodeProfile(null);
/*      */             }
/*      */             
/* 3242 */           });
/* 3243 */           menu_profile_none.addFillListener(new MenuItemFillListener() {
/*      */             public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3245 */               if (transcodeProfiles.length <= 1) {
/* 3246 */                 menu.setData(Boolean.FALSE);
/* 3247 */                 menu.setEnabled(false);
/*      */               } else {
/* 3249 */                 TranscodeProfile profile = null;
/*      */                 try {
/* 3251 */                   profile = renderer.getDefaultTranscodeProfile();
/*      */                 }
/*      */                 catch (TranscodeException e) {}
/* 3254 */                 menu.setData(profile == null ? Boolean.TRUE : Boolean.FALSE);
/*      */                 
/*      */ 
/* 3257 */                 menu.setEnabled(renderer.getTranscodeRequirement() != 1);
/*      */               }
/*      */             }
/*      */           });
/*      */           
/* 3262 */           for (final TranscodeProfile profile : transcodeProfiles) {
/* 3263 */             org.gudy.azureus2.plugins.ui.menus.MenuItem menuItem = menu_manager.addMenuItem(menu_default_profile, "!" + profile.getName() + "!");
/*      */             
/* 3265 */             menuItem.setStyle(3);
/* 3266 */             menuItem.setData(Boolean.FALSE);
/* 3267 */             menuItem.addListener(new MenuItemListener() {
/*      */               public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 3269 */                 renderer.setDefaultTranscodeProfile(profile);
/*      */               }
/*      */               
/* 3272 */             });
/* 3273 */             menuItem.addFillListener(new MenuItemFillListener() {
/*      */               public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3275 */                 if (transcodeProfiles.length <= 1) {
/* 3276 */                   menu.setData(Boolean.TRUE);
/* 3277 */                   menu.setEnabled(false);
/*      */                 } else {
/* 3279 */                   TranscodeProfile dprofile = null;
/*      */                   try {
/* 3281 */                     dprofile = renderer.getDefaultTranscodeProfile();
/*      */                   }
/*      */                   catch (TranscodeException e) {}
/* 3284 */                   menu.setData(profile.equals(dprofile) ? Boolean.TRUE : Boolean.FALSE);
/*      */                   
/*      */ 
/* 3287 */                   menu.setEnabled(renderer.getTranscodeRequirement() != 1);
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3298 */         need_sep = true;
/*      */         
/* 3300 */         final org.gudy.azureus2.plugins.ui.menus.MenuItem rss_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.rsspub");
/* 3301 */         rss_menu_item.setStyle(2);
/*      */         
/* 3303 */         rss_menu_item.addFillListener(new MenuItemFillListener() {
/*      */           public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3305 */             rss_menu_item.setEnabled(DeviceManagerUI.this.device_manager.isRSSPublishEnabled());
/*      */             
/* 3307 */             menu.setData(Boolean.valueOf((DeviceManagerUI.this.device_manager.isRSSPublishEnabled()) && (renderer.isRSSPublishEnabled())));
/*      */           }
/* 3309 */         });
/* 3310 */         rss_menu_item.addListener(new MenuItemListener() {
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 3312 */             renderer.setRSSPublishEnabled(((Boolean)menu.getData()).booleanValue());
/*      */           }
/*      */           
/* 3315 */         });
/* 3316 */         rss_menu_item.setEnabled(this.device_manager.isRSSPublishEnabled());
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3324 */         need_sep = true;
/*      */         
/* 3326 */         org.gudy.azureus2.plugins.ui.menus.MenuItem aswt_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.xcode.tagshare");
/* 3327 */         aswt_menu_item.setStyle(5);
/*      */         
/* 3329 */         aswt_menu_item.addFillListener(new MenuItemFillListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data)
/*      */           {
/*      */ 
/*      */ 
/* 3337 */             DeviceManagerUI.addTagSubMenu(menu_manager, menu, renderer);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/* 3343 */       if ((device instanceof DeviceOfflineDownloader))
/*      */       {
/* 3345 */         final DeviceOfflineDownloader dod = (DeviceOfflineDownloader)device;
/*      */         
/* 3347 */         need_sep = true;
/*      */         
/* 3349 */         org.gudy.azureus2.plugins.ui.menus.MenuItem configure_menu_item = menu_manager.addMenuItem("sidebar." + key, "device.configure");
/*      */         
/*      */ 
/* 3352 */         configure_menu_item.addFillListener(new MenuItemFillListener() {
/*      */           public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3354 */             menu.setEnabled(dod.isAlive());
/*      */           }
/*      */           
/* 3357 */         });
/* 3358 */         configure_menu_item.addListener(new MenuItemListener()
/*      */         {
/*      */ 
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/* 3367 */               new DevicesODFTUX(dod);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3371 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/* 3375 */         });
/* 3376 */         org.gudy.azureus2.plugins.ui.menus.MenuItem enabled_menu_item = menu_manager.addMenuItem("sidebar." + key, "devices.contextmenu.od.enable");
/*      */         
/* 3378 */         enabled_menu_item.setStyle(2);
/*      */         
/* 3380 */         enabled_menu_item.addFillListener(new MenuItemFillListener() {
/*      */           public void menuWillBeShown(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object data) {
/* 3382 */             menu.setData(Boolean.valueOf(dod.isEnabled()));
/*      */           }
/*      */           
/* 3385 */         });
/* 3386 */         enabled_menu_item.addListener(new MenuItemListener() {
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 3388 */             dod.setEnabled(((Boolean)menu.getData()).booleanValue());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 3393 */       if (device.isBrowsable())
/*      */       {
/* 3395 */         need_sep = true;
/*      */         
/* 3397 */         org.gudy.azureus2.plugins.ui.menus.MenuItem browse_menu_item = menu_manager.addMenuItem("sidebar." + key, "device.browse");
/*      */         
/* 3399 */         browse_menu_item.setStyle(5);
/*      */         
/* 3401 */         browse_menu_item.addFillListener(this.will_browse_listener);
/*      */       }
/*      */       
/*      */ 
/* 3405 */       if (need_sep)
/*      */       {
/* 3407 */         menu_manager.addMenuItem("sidebar." + key, "s2").setStyle(4);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3412 */       org.gudy.azureus2.plugins.ui.menus.MenuItem rename_menu_item = menu_manager.addMenuItem("sidebar." + key, "MyTorrentsView.menu.rename");
/*      */       
/* 3414 */       rename_menu_item.addListener(this.rename_listener);
/*      */       
/*      */ 
/*      */ 
/* 3418 */       if (device.isExportable())
/*      */       {
/* 3420 */         org.gudy.azureus2.plugins.ui.menus.MenuItem export_item = menu_manager.addMenuItem("sidebar." + key, "Subscription.menu.export");
/*      */         
/* 3422 */         export_item.addListener(this.export_listener);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3427 */       org.gudy.azureus2.plugins.ui.menus.MenuItem hide_menu_item = menu_manager.addMenuItem("sidebar." + key, "device.hide");
/*      */       
/* 3429 */       hide_menu_item.addListener(this.hide_listener);
/*      */       
/*      */ 
/*      */ 
/* 3433 */       org.gudy.azureus2.plugins.ui.menus.MenuItem tag_menu_item = menu_manager.addMenuItem("sidebar." + key, "device.tag");
/*      */       
/* 3435 */       tag_menu_item.setStyle(2);
/*      */       
/* 3437 */       tag_menu_item.addFillListener(this.will_tag_listener);
/*      */       
/* 3439 */       tag_menu_item.addListener(this.tag_listener);
/*      */       
/*      */ 
/*      */ 
/* 3443 */       org.gudy.azureus2.plugins.ui.menus.MenuItem remove_menu_item = menu_manager.addMenuItem("sidebar." + key, "MySharesView.menu.remove");
/*      */       
/* 3445 */       remove_menu_item.addFillListener(this.will_remove_listener);
/*      */       
/* 3447 */       remove_menu_item.addListener(this.remove_listener);
/*      */       
/*      */ 
/*      */ 
/* 3451 */       menu_manager.addMenuItem("sidebar." + key, "s3").setStyle(4);
/*      */       
/* 3453 */       final URL wiki_url = device.getWikiURL();
/*      */       
/* 3455 */       if (wiki_url != null)
/*      */       {
/* 3457 */         org.gudy.azureus2.plugins.ui.menus.MenuItem wiki_menu_item = menu_manager.addMenuItem("sidebar." + key, "device.wiki");
/*      */         
/* 3459 */         wiki_menu_item.addListener(new MenuItemListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */           {
/*      */ 
/*      */ 
/* 3467 */             Utils.launch(wiki_url.toExternalForm());
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3474 */       org.gudy.azureus2.plugins.ui.menus.MenuItem menu_item = menu_manager.addMenuItem("sidebar." + key, "Subscription.menu.properties");
/*      */       
/* 3476 */       menu_item.addListener(this.properties_listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static String getDeviceImageID(Device device)
/*      */   {
/* 3484 */     String imageID = device.getImageID();
/* 3485 */     if (imageID != null) {
/* 3486 */       return imageID;
/*      */     }
/*      */     
/* 3489 */     if (!(device instanceof DeviceMediaRenderer)) {
/* 3490 */       return "6";
/*      */     }
/*      */     
/* 3493 */     int species = ((DeviceMediaRenderer)device).getRendererSpecies();
/*      */     
/*      */     String id;
/*      */     String id;
/* 3497 */     if (species != 6)
/*      */     {
/* 3499 */       id = String.valueOf(species);
/*      */     }
/*      */     else
/*      */     {
/* 3503 */       String classification = device.getClassification();
/*      */       String id;
/* 3505 */       if (classification.equals("sony.PSP"))
/*      */       {
/* 3507 */         id = "psp";
/*      */       } else { String id;
/* 3509 */         if (classification.startsWith("tivo."))
/*      */         {
/* 3511 */           id = "tivo";
/*      */         } else { String id;
/* 3513 */           if (classification.startsWith("samsung."))
/*      */           {
/* 3515 */             id = "samsung";
/*      */           } else { String id;
/* 3517 */             if (classification.startsWith("western.digital."))
/*      */             {
/* 3519 */               id = "wdtv";
/*      */             } else { String id;
/* 3521 */               if (classification.startsWith("boxee."))
/*      */               {
/* 3523 */                 id = "boxee";
/*      */               } else { String id;
/* 3525 */                 if (classification.startsWith("sony.bravia"))
/*      */                 {
/* 3527 */                   id = "bravia";
/*      */                 } else { String id;
/* 3529 */                   if (classification.startsWith("ms_wmp."))
/*      */                   {
/*      */ 
/*      */ 
/* 3533 */                     id = "mswmp";
/*      */                   } else { String id;
/* 3535 */                     if (classification.toLowerCase().contains("android"))
/*      */                     {
/* 3537 */                       id = "android";
/*      */                     } else { String id;
/* 3539 */                       if (classification.toLowerCase().contains("neotv"))
/*      */                       {
/* 3541 */                         id = "neotv";
/*      */                       } else { String id;
/* 3543 */                         if (classification.startsWith("vuze-ms-browser."))
/*      */                         {
/* 3545 */                           id = "vuze";
/*      */                         }
/*      */                         else {
/*      */                           String id;
/* 3549 */                           if (device.isGenericUSB()) {
/* 3550 */                             id = "usb";
/*      */                           }
/*      */                           else
/*      */                           {
/* 3554 */                             id = String.valueOf(species); }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   } } } } } } } }
/* 3559 */     return id;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void addTagSubMenu(MenuManager menu_manager, org.gudy.azureus2.plugins.ui.menus.MenuItem menu, DeviceMediaRenderer device)
/*      */   {
/* 3568 */     menu.removeAllChildItems();
/*      */     
/* 3570 */     TagManager tm = com.aelitis.azureus.core.tag.TagManagerFactory.getTagManager();
/*      */     
/* 3572 */     List<Tag> tags = tm.getTagType(3).getTags();
/*      */     
/* 3574 */     tags = org.gudy.azureus2.ui.swt.views.utils.TagUIUtils.sortTags(tags);
/*      */     
/* 3576 */     long tag_id = device.getAutoShareToTagID();
/*      */     
/* 3578 */     Tag assigned_tag = tm.lookupTagByUID(tag_id);
/*      */     
/* 3580 */     org.gudy.azureus2.plugins.ui.menus.MenuItem m = menu_manager.addMenuItem(menu, "label.no.tag");
/*      */     
/* 3582 */     m.setStyle(3);
/*      */     
/* 3584 */     m.setData(Boolean.valueOf(assigned_tag == null));
/*      */     
/* 3586 */     m.addListener(new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/* 3594 */         this.val$device.setAutoShareToTagID(-1L);
/*      */       }
/*      */       
/*      */ 
/* 3598 */     });
/* 3599 */     m = menu_manager.addMenuItem(menu, "sep1");
/*      */     
/* 3601 */     m.setStyle(4);
/*      */     
/*      */ 
/* 3604 */     List<String> menu_names = new ArrayList();
/* 3605 */     Map<String, Tag> menu_name_map = new java.util.IdentityHashMap();
/*      */     
/* 3607 */     for (Tag t : tags)
/*      */     {
/* 3609 */       if (t.isTagAuto()[0] == 0)
/*      */       {
/* 3611 */         String name = t.getTagName(true);
/*      */         
/* 3613 */         menu_names.add(name);
/* 3614 */         menu_name_map.put(name, t);
/*      */       }
/*      */     }
/*      */     
/* 3618 */     List<Object> menu_structure = org.gudy.azureus2.ui.swt.MenuBuildUtils.splitLongMenuListIntoHierarchy(menu_names, 20);
/*      */     
/* 3620 */     for (Object obj : menu_structure)
/*      */     {
/* 3622 */       List<Tag> bucket_tags = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/* 3626 */       if ((obj instanceof String))
/*      */       {
/* 3628 */         org.gudy.azureus2.plugins.ui.menus.MenuItem parent_menu = menu;
/*      */         
/* 3630 */         bucket_tags.add(menu_name_map.get((String)obj));
/*      */       }
/*      */       else
/*      */       {
/* 3634 */         Object[] entry = (Object[])obj;
/*      */         
/* 3636 */         List<String> tag_names = (List)entry[1];
/*      */         
/* 3638 */         boolean has_selected = false;
/*      */         
/* 3640 */         for (String name : tag_names)
/*      */         {
/* 3642 */           Tag tag = (Tag)menu_name_map.get(name);
/*      */           
/* 3644 */           bucket_tags.add(tag);
/*      */           
/* 3646 */           if (assigned_tag == tag)
/*      */           {
/* 3648 */             has_selected = true;
/*      */           }
/*      */         }
/*      */         
/* 3652 */         parent_menu = menu_manager.addMenuItem(menu, "!" + (String)entry[0] + (has_selected ? " (*)" : "") + "!");
/*      */         
/* 3654 */         parent_menu.setStyle(5);
/*      */       }
/*      */       
/* 3657 */       for (final Tag tag : bucket_tags)
/*      */       {
/* 3659 */         m = menu_manager.addMenuItem(parent_menu, tag.getTagName(false));
/*      */         
/* 3661 */         m.setStyle(3);
/*      */         
/* 3663 */         m.setData(Boolean.valueOf(assigned_tag == tag));
/*      */         
/* 3665 */         m.addListener(new MenuItemListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */           {
/*      */ 
/*      */ 
/* 3673 */             this.val$device.setAutoShareToTagID(tag.getTagUID());
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     org.gudy.azureus2.plugins.ui.menus.MenuItem parent_menu;
/* 3679 */     m = menu_manager.addMenuItem(menu, "sep2");
/*      */     
/* 3681 */     m.setStyle(4);
/*      */     
/* 3683 */     m = menu_manager.addMenuItem(menu, "label.add.tag");
/*      */     
/* 3685 */     m.addListener(new MenuItemListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target)
/*      */       {
/*      */ 
/*      */ 
/* 3693 */         org.gudy.azureus2.ui.swt.views.utils.TagUIUtils.createManualTag(new com.aelitis.azureus.ui.UIFunctions.TagReturner() {
/*      */           public void returnedTags(Tag[] tags) {
/* 3695 */             if (tags != null) {
/* 3696 */               for (Tag new_tag : tags) {
/* 3697 */                 DeviceManagerUI.98.this.val$device.setAutoShareToTagID(new_tag.getTagUID());
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
/*      */   protected void showDevice(Device device)
/*      */   {
/* 3711 */     synchronized (this)
/*      */     {
/* 3713 */       deviceItem existing_di = (deviceItem)device.getTransientProperty(DEVICE_IVIEW_KEY);
/*      */       
/* 3715 */       if (existing_di != null)
/*      */       {
/* 3717 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*      */         
/* 3719 */         if (mdi != null)
/*      */         {
/* 3721 */           mdi.showEntry(existing_di.getMdiEntry());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean handleDrop(TranscodeTarget target, Object payload)
/*      */   {
/* 3732 */     return handleDropSupport(target, payload, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean handleDropSupport(final TranscodeTarget target, final Object payload, boolean allow_retry)
/*      */   {
/* 3741 */     if ((!(payload instanceof String[])) && (!(payload instanceof String)) && (!(payload instanceof URLTransfer.URLType)))
/*      */     {
/*      */ 
/*      */ 
/* 3745 */       return false;
/*      */     }
/* 3747 */     TranscodeChooser deviceChooser = new TranscodeChooser(target)
/*      */     {
/*      */ 
/*      */       public void closed()
/*      */       {
/* 3752 */         if ((this.selectedTranscodeTarget != null) && (this.selectedProfile != null))
/*      */         {
/* 3754 */           DeviceManagerUI.handleDrop(this.selectedTranscodeTarget, this.selectedProfile, payload, getTranscodeRequirement());
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 3762 */     };
/* 3763 */     deviceChooser.show(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 3769 */         if (this.val$allow_retry)
/*      */         {
/* 3771 */           DeviceManagerUI.handleDropSupport(target, payload, false);
/*      */         }
/*      */       }
/* 3774 */     });
/* 3775 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void addDownload(TranscodeTarget target, TranscodeProfile profile, int transcode_requirement, byte[] hash)
/*      */   {
/*      */     try
/*      */     {
/* 3787 */       addDownload(target, profile, transcode_requirement, AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getShortCuts().getDownload(hash));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3791 */       Debug.out(e);
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
/*      */ 
/*      */ 
/*      */   protected static void addDownload(TranscodeTarget target, TranscodeProfile profile, int transcode_requirement, Download download)
/*      */   {
/* 3808 */     DiskManagerFileInfo[] dm_files = download.getDiskManagerFileInfo();
/*      */     
/* 3810 */     int num_added = 0;
/*      */     
/* 3812 */     for (DiskManagerFileInfo dm_file : dm_files)
/*      */     {
/*      */ 
/*      */ 
/* 3816 */       if (num_added > 64) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3823 */       if ((dm_files.length == 1) || (dm_file.getLength() >= 131072L))
/*      */       {
/* 3825 */         addFile(target, profile, transcode_requirement, dm_file);
/*      */         
/* 3827 */         num_added++;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void addFile(TranscodeTarget target, TranscodeProfile profile, int transcode_requirement, DiskManagerFileInfo file)
/*      */   {
/*      */     try
/*      */     {
/* 3840 */       DeviceManagerFactory.getSingleton().getTranscodeManager().getQueue().add(target, profile, file, transcode_requirement, false);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 3849 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void addDirectory(TranscodeTarget target, TranscodeProfile profile, int transcode_requirement, File file)
/*      */   {
/* 3860 */     if (!file.isDirectory())
/*      */     {
/* 3862 */       return;
/*      */     }
/*      */     
/* 3865 */     File[] files = file.listFiles();
/*      */     
/* 3867 */     int num_added = 0;
/*      */     
/* 3869 */     for (File f : files)
/*      */     {
/* 3871 */       if (num_added > 64) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/* 3876 */       if (!f.isDirectory())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3881 */         if (f.length() > 131072L)
/*      */         {
/* 3883 */           addFile(target, profile, transcode_requirement, f);
/*      */           
/* 3885 */           num_added++;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void addFile(TranscodeTarget target, TranscodeProfile profile, int transcode_requirement, File file)
/*      */   {
/* 3897 */     if ((file.exists()) && (file.isFile())) {
/*      */       try
/*      */       {
/* 3900 */         DeviceManagerFactory.getSingleton().getTranscodeManager().getQueue().add(target, profile, new com.aelitis.azureus.core.download.DiskManagerFileInfoFile(file), transcode_requirement, false);
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/*      */ 
/* 3909 */         Debug.out(e);
/*      */       }
/*      */       
/*      */     } else {
/* 3913 */       Debug.out("Drop to " + target.getDevice().getName() + " for " + file + " failed, file doesn't exist");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void addURL(TranscodeTarget target, TranscodeProfile profile, int transcode_requirement, String url)
/*      */   {
/*      */     try
/*      */     {
/* 3925 */       DeviceManagerFactory.getSingleton().getTranscodeManager().getQueue().add(target, profile, new com.aelitis.azureus.core.download.DiskManagerFileInfoURL(new URL(url)), transcode_requirement, false);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 3934 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void handleDrop(TranscodeTarget target, TranscodeProfile profile, Object payload, int transcode_requirement)
/*      */   {
/* 3945 */     if ((payload instanceof String[]))
/*      */     {
/* 3947 */       String[] files = (String[])payload;
/*      */       
/* 3949 */       for (String file : files)
/*      */       {
/* 3951 */         File f = new File(file);
/*      */         
/* 3953 */         if (f.isFile())
/*      */         {
/* 3955 */           addFile(target, profile, transcode_requirement, f);
/*      */         }
/*      */         else
/*      */         {
/* 3959 */           addDirectory(target, profile, transcode_requirement, f);
/*      */         }
/*      */       }
/* 3962 */     } else if ((payload instanceof String))
/*      */     {
/* 3964 */       String stuff = (String)payload;
/*      */       
/* 3966 */       if ((stuff.startsWith("DownloadManager\n")) || (stuff.startsWith("DiskManagerFileInfo\n")))
/*      */       {
/* 3968 */         String[] bits = Constants.PAT_SPLIT_SLASH_N.split(stuff);
/*      */         
/* 3970 */         for (int i = 1; i < bits.length; i++)
/*      */         {
/* 3972 */           String hash_str = bits[i];
/*      */           
/* 3974 */           int pos = hash_str.indexOf(';');
/*      */           
/*      */           try
/*      */           {
/* 3978 */             if (pos == -1)
/*      */             {
/* 3980 */               byte[] hash = org.gudy.azureus2.core3.util.Base32.decode(bits[i]);
/*      */               
/* 3982 */               addDownload(target, profile, transcode_requirement, hash);
/*      */             }
/*      */             else
/*      */             {
/* 3986 */               String[] files = hash_str.split(";");
/*      */               
/* 3988 */               byte[] hash = org.gudy.azureus2.core3.util.Base32.decode(files[0].trim());
/*      */               
/* 3990 */               DiskManagerFileInfo[] dm_files = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getShortCuts().getDownload(hash).getDiskManagerFileInfo();
/*      */               
/* 3992 */               for (int j = 1; j < files.length; j++)
/*      */               {
/* 3994 */                 DiskManagerFileInfo dm_file = dm_files[Integer.parseInt(files[j].trim())];
/*      */                 
/* 3996 */                 addFile(target, profile, transcode_requirement, dm_file);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 4001 */             Debug.out("Failed to get download for hash " + bits[1]);
/*      */           }
/*      */         }
/* 4004 */       } else if (stuff.startsWith("TranscodeFile\n"))
/*      */       {
/* 4006 */         String[] bits = Constants.PAT_SPLIT_SLASH_N.split(stuff);
/*      */         
/* 4008 */         for (int i = 1; i < bits.length; i++)
/*      */         {
/* 4010 */           File f = new File(bits[i]);
/*      */           
/* 4012 */           if (f.isFile())
/*      */           {
/* 4014 */             addFile(target, profile, transcode_requirement, f);
/*      */           }
/*      */         }
/* 4017 */       } else if ((stuff.startsWith("http:")) || (stuff.startsWith("https://")))
/*      */       {
/* 4019 */         addURL(target, profile, transcode_requirement, stuff);
/*      */       }
/* 4021 */     } else if ((payload instanceof URLTransfer.URLType))
/*      */     {
/* 4023 */       String url = ((URLTransfer.URLType)payload).linkURL;
/*      */       
/* 4025 */       if (url != null)
/*      */       {
/* 4027 */         addURL(target, profile, transcode_requirement, url);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setStatus(Device device, deviceItem sbi)
/*      */   {
/* 4037 */     sbi.setStatus(device);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void addAllDevices()
/*      */   {
/* 4043 */     synchronized (this)
/*      */     {
/* 4045 */       if (this.device_manager_listener == null)
/*      */       {
/* 4047 */         this.needsAddAllDevices = true;
/*      */         
/* 4049 */         return;
/*      */       }
/*      */       
/* 4052 */       if (!this.device_manager_listener_added)
/*      */       {
/* 4054 */         this.device_manager_listener_added = true;
/*      */         
/* 4056 */         this.device_manager.addListener(this.device_manager_listener);
/*      */       }
/*      */     }
/*      */     
/* 4060 */     Device[] devices = this.device_manager.getDevices();
/*      */     
/* 4062 */     Arrays.sort(devices, new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(Device o1, Device o2)
/*      */       {
/*      */ 
/*      */ 
/* 4071 */         return o1.getName().compareToIgnoreCase(o2.getName());
/*      */       }
/*      */     });
/*      */     
/* 4075 */     for (Device device : devices)
/*      */     {
/* 4077 */       addOrChangeDevice(device);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void removeAllDevices()
/*      */   {
/* 4084 */     synchronized (this)
/*      */     {
/* 4086 */       if (this.device_manager_listener_added)
/*      */       {
/* 4088 */         this.device_manager_listener_added = false;
/*      */         
/* 4090 */         this.device_manager.removeListener(this.device_manager_listener);
/*      */       }
/*      */     }
/*      */     
/* 4094 */     Device[] devices = this.device_manager.getDevices();
/*      */     
/* 4096 */     for (Device device : devices)
/*      */     {
/* 4098 */       removeDevice(device);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeDevice(Device device)
/*      */   {
/* 4106 */     synchronized (this)
/*      */     {
/* 4108 */       deviceItem existing_di = (deviceItem)device.getTransientProperty(DEVICE_IVIEW_KEY);
/*      */       
/* 4110 */       if (existing_di != null)
/*      */       {
/* 4112 */         device.setTransientProperty(DEVICE_IVIEW_KEY, null);
/*      */         
/* 4114 */         existing_di.destroy();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected categoryView addDeviceCategory(int device_type, String category_title, String category_image_id)
/*      */   {
/* 4125 */     String key = "Device_" + category_title + ":" + nextSidebarID();
/*      */     
/*      */     categoryView eventListener;
/*      */     categoryView eventListener;
/* 4129 */     if (device_type == 4)
/*      */     {
/* 4131 */       eventListener = new DeviceInternetView(this, category_title);
/*      */     }
/*      */     else
/*      */     {
/* 4135 */       eventListener = new categoryViewGeneric(this, device_type, category_title);
/*      */     }
/*      */     
/* 4138 */     MdiEntry entry = this.mdi.createEntryFromEventListener("header.devices", eventListener, key, false, new Integer(device_type), null);
/*      */     
/*      */ 
/*      */ 
/* 4142 */     addDefaultDropListener(entry);
/*      */     
/* 4144 */     entry.setImageLeftID(category_image_id);
/*      */     
/* 4146 */     eventListener.setDetails(entry, key);
/*      */     
/* 4148 */     return eventListener;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addDefaultDropListener(MdiEntry mainSbEntry)
/*      */   {
/* 4155 */     mainSbEntry.addListener(new com.aelitis.azureus.ui.mdi.MdiEntryDropListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public boolean mdiEntryDrop(MdiEntry entry, Object payload)
/*      */       {
/*      */ 
/*      */ 
/* 4163 */         return DeviceManagerUI.handleDrop(null, payload);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void showProperties(Device device)
/*      */   {
/* 4172 */     String[][] props = device.getDisplayProperties();
/*      */     
/* 4174 */     new org.gudy.azureus2.ui.swt.PropertiesWindow(device.getName(), props[0], props[1]);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected int nextSidebarID()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: dup
/*      */     //   6: getfield 1682	com/aelitis/azureus/ui/swt/devices/DeviceManagerUI:next_sidebar_id	I
/*      */     //   9: dup_x1
/*      */     //   10: iconst_1
/*      */     //   11: iadd
/*      */     //   12: putfield 1682	com/aelitis/azureus/ui/swt/devices/DeviceManagerUI:next_sidebar_id	I
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: ireturn
/*      */     //   18: astore_2
/*      */     //   19: aload_1
/*      */     //   20: monitorexit
/*      */     //   21: aload_2
/*      */     //   22: athrow
/*      */     // Line number table:
/*      */     //   Java source line #4180	-> byte code offset #0
/*      */     //   Java source line #4182	-> byte code offset #4
/*      */     //   Java source line #4183	-> byte code offset #18
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	23	0	this	DeviceManagerUI
/*      */     //   2	18	1	Ljava/lang/Object;	Object
/*      */     //   18	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	17	18	finally
/*      */     //   18	21	18	finally
/*      */   }
/*      */   
/*      */   protected static abstract class categoryView
/*      */     implements ViewTitleInfo, org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener
/*      */   {
/*      */     private DeviceManagerUI ui;
/*      */     private int device_type;
/*      */     private String title;
/*      */     private String key;
/*      */     private MdiEntryVitalityImage spinner;
/*      */     private MdiEntryVitalityImage warning;
/*      */     private MdiEntryVitalityImage info;
/*      */     private int last_indicator;
/*      */     private MdiEntry mdiEntry;
/*      */     private UISWTView swtView;
/*      */     
/*      */     protected categoryView(DeviceManagerUI _ui, int _device_type, String _title)
/*      */     {
/* 4210 */       this.ui = _ui;
/* 4211 */       this.device_type = _device_type;
/* 4212 */       this.title = _title;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void setDetails(MdiEntry entry, String _key)
/*      */     {
/* 4220 */       this.mdiEntry = entry;
/*      */       
/* 4222 */       this.key = _key;
/*      */       
/* 4224 */       this.spinner = entry.addVitalityImage("image.sidebar.vitality.dl");
/*      */       
/* 4226 */       DeviceManagerUI.hideIcon(this.spinner);
/*      */       
/* 4228 */       this.warning = entry.addVitalityImage("image.sidebar.vitality.alert");
/*      */       
/* 4230 */       DeviceManagerUI.hideIcon(this.warning);
/*      */       
/* 4232 */       this.info = entry.addVitalityImage("image.sidebar.vitality.info");
/*      */       
/* 4234 */       DeviceManagerUI.hideIcon(this.info);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected int getDeviceType()
/*      */     {
/* 4241 */       return this.device_type;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getKey()
/*      */     {
/* 4247 */       return this.key;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getTitle()
/*      */     {
/* 4253 */       return MessageText.getString(this.title);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object getTitleInfoProperty(int propertyID)
/*      */     {
/* 4260 */       boolean expanded = (this.mdiEntry != null) && (this.mdiEntry.isExpanded());
/*      */       
/* 4262 */       if (propertyID == 5)
/*      */       {
/* 4264 */         return getTitle();
/*      */       }
/* 4266 */       if (propertyID == 0)
/*      */       {
/* 4268 */         if ((this.device_type == 3) || (this.device_type == 5))
/*      */         {
/* 4270 */           if (this.spinner != null)
/*      */           {
/* 4272 */             this.spinner.setVisible((!expanded) && (this.ui.getDeviceManager().isBusy(this.device_type)));
/*      */           }
/*      */           
/* 4275 */           if (!expanded)
/*      */           {
/* 4277 */             Device[] devices = this.ui.getDeviceManager().getDevices();
/*      */             
/* 4279 */             this.last_indicator = 0;
/*      */             
/* 4281 */             String all_errors = "";
/* 4282 */             String all_infos = "";
/*      */             
/* 4284 */             for (Device device : devices)
/*      */             {
/* 4286 */               if (this.device_type == device.getType())
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 4291 */                 String error = device.getError();
/*      */                 
/* 4293 */                 if (error != null)
/*      */                 {
/* 4295 */                   all_errors = all_errors + (all_errors.length() == 0 ? "" : "; ") + error;
/*      */                 }
/*      */                 
/* 4298 */                 String info = device.getInfo();
/*      */                 
/* 4300 */                 if (info != null)
/*      */                 {
/* 4302 */                   all_infos = all_infos + (all_infos.length() == 0 ? "" : "; ") + info;
/*      */                 }
/*      */                 
/* 4305 */                 if (!(device instanceof DeviceMediaRenderer))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4313 */                   if ((device instanceof DeviceOfflineDownloader))
/*      */                   {
/*      */ 
/*      */ 
/* 4317 */                     DeviceOfflineDownloader dod = (DeviceOfflineDownloader)device;
/*      */                     
/* 4319 */                     this.last_indicator += dod.getTransferingCount();
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 4324 */             if (all_errors.length() > 0)
/*      */             {
/* 4326 */               DeviceManagerUI.showIcon(this.warning, all_errors);
/*      */             }
/*      */             else
/*      */             {
/* 4330 */               DeviceManagerUI.hideIcon(this.warning);
/*      */               
/* 4332 */               if (all_infos.length() > 0)
/*      */               {
/* 4334 */                 DeviceManagerUI.showIcon(this.info, all_infos);
/*      */               }
/*      */               else
/*      */               {
/* 4338 */                 DeviceManagerUI.hideIcon(this.info);
/*      */               }
/*      */             }
/*      */             
/* 4342 */             if (this.last_indicator > 0)
/*      */             {
/* 4344 */               return String.valueOf(this.last_indicator);
/*      */             }
/*      */           }
/*      */           else {
/* 4348 */             DeviceManagerUI.hideIcon(this.warning);
/* 4349 */             DeviceManagerUI.hideIcon(this.info);
/*      */           }
/*      */         }
/* 4352 */       } else if (propertyID != 8) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4365 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void destroy()
/*      */     {
/* 4371 */       if (Utils.isThisThreadSWT())
/*      */       {
/* 4373 */         this.mdiEntry.close(false);
/*      */       }
/*      */       else
/*      */       {
/* 4377 */         Utils.execSWTThread(new Runnable()
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/* 4383 */             DeviceManagerUI.categoryView.this.mdiEntry.close(false);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean eventOccurred(UISWTViewEvent event)
/*      */     {
/* 4391 */       switch (event.getType()) {
/*      */       case 0: 
/* 4393 */         this.swtView = ((UISWTView)event.getData());
/* 4394 */         this.swtView.setTitle(this.title);
/*      */       }
/*      */       
/*      */       
/* 4398 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static class categoryViewGeneric
/*      */     extends DeviceManagerUI.categoryView
/*      */   {
/*      */     private Composite composite;
/*      */     
/*      */ 
/*      */ 
/*      */     protected categoryViewGeneric(DeviceManagerUI _ui, int _device_type, String _title)
/*      */     {
/* 4414 */       super(_device_type, _title);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void initialize(Composite parent_composite)
/*      */     {
/* 4421 */       this.composite = new Composite(parent_composite, 0);
/*      */       
/* 4423 */       FormLayout layout = new FormLayout();
/*      */       
/* 4425 */       layout.marginTop = 4;
/* 4426 */       layout.marginLeft = 4;
/* 4427 */       layout.marginRight = 4;
/* 4428 */       layout.marginBottom = 4;
/*      */       
/* 4430 */       this.composite.setLayout(layout);
/*      */       
/* 4432 */       FormData data = new FormData();
/* 4433 */       data.left = new FormAttachment(0, 0);
/* 4434 */       data.right = new FormAttachment(100, 0);
/* 4435 */       data.top = new FormAttachment(this.composite, 0);
/* 4436 */       data.bottom = new FormAttachment(100, 0);
/*      */       
/*      */ 
/* 4439 */       Label label = new Label(this.composite, 0);
/*      */       
/* 4441 */       label.setText("Nothing to show for " + getTitle());
/*      */       
/* 4443 */       label.setLayoutData(data);
/*      */     }
/*      */     
/*      */ 
/*      */     public Composite getComposite()
/*      */     {
/* 4449 */       return this.composite;
/*      */     }
/*      */     
/*      */     public boolean eventOccurred(UISWTViewEvent event) {
/* 4453 */       switch (event.getType())
/*      */       {
/*      */       case 0: 
/*      */         break;
/*      */       
/*      */       case 7: 
/*      */         break;
/*      */       
/*      */       case 2: 
/* 4462 */         initialize((Composite)event.getData());
/* 4463 */         break;
/*      */       
/*      */       case 6: 
/* 4466 */         Messages.updateLanguageForControl(getComposite());
/* 4467 */         break;
/*      */       case 1: 
/*      */         break;
/*      */       case 3: 
/*      */         break;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4479 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class deviceView
/*      */     implements ViewTitleInfo, com.aelitis.azureus.core.devices.TranscodeTargetListener, org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener
/*      */   {
/*      */     private String parent_key;
/*      */     
/*      */     private Device device;
/*      */     
/*      */     private Composite parent_composite;
/*      */     
/*      */     private Composite composite;
/*      */     
/*      */     private int last_indicator;
/*      */     
/*      */     private UISWTView swtView;
/*      */     
/*      */     protected deviceView(String _parent_key, Device _device)
/*      */     {
/* 4501 */       this.parent_key = _parent_key;
/* 4502 */       this.device = _device;
/*      */       DeviceMediaRenderer renderer;
/* 4504 */       if ((this.device instanceof DeviceMediaRenderer))
/*      */       {
/* 4506 */         renderer = (DeviceMediaRenderer)this.device;
/*      */         
/* 4508 */         renderer.addListener(this);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void initialize(Composite _parent_composite)
/*      */     {
/* 4516 */       this.parent_composite = _parent_composite;
/*      */       
/* 4518 */       this.composite = new Composite(this.parent_composite, 0);
/*      */       
/* 4520 */       FormLayout layout = new FormLayout();
/*      */       
/* 4522 */       layout.marginTop = 4;
/* 4523 */       layout.marginLeft = 4;
/* 4524 */       layout.marginRight = 4;
/* 4525 */       layout.marginBottom = 4;
/*      */       
/* 4527 */       this.composite.setLayout(layout);
/*      */       
/* 4529 */       if ((this.device instanceof DeviceContentDirectory))
/*      */       {
/* 4531 */         Label ms_label = new Label(this.composite, 0);
/* 4532 */         ms_label.setText("Media Server: " + this.device.getName());
/*      */         
/* 4534 */         final Button refresh = new Button(this.composite, 8);
/* 4535 */         refresh.setText("Refresh");
/*      */         
/* 4537 */         final StyledText info = new StyledText(this.composite, 2818)
/*      */         {
/*      */ 
/* 4540 */           private boolean adding = false;
/*      */           
/*      */ 
/*      */           private Event last_event;
/*      */           
/*      */ 
/*      */           public void addListener(int eventType, final Listener listener)
/*      */           {
/* 4548 */             if ((eventType == 3) && (!this.adding)) {
/*      */               try
/*      */               {
/* 4551 */                 this.adding = true;
/*      */                 
/* 4553 */                 super.addListener(eventType, new Listener()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void handleEvent(Event event)
/*      */                   {
/*      */ 
/*      */ 
/* 4561 */                     if ((event.type == 3) && (event != DeviceManagerUI.deviceView.1.this.last_event))
/*      */                     {
/* 4563 */                       if ((event.button == 1) && (event.stateMask != 262144))
/*      */                       {
/* 4565 */                         DeviceManagerUI.deviceView.1.this.last_event = event;
/*      */                         
/*      */ 
/*      */ 
/*      */                         try
/*      */                         {
/* 4571 */                           int offset = DeviceManagerUI.deviceView.1.this.getOffsetAtLocation(new Point(event.x, event.y));
/*      */                           
/* 4573 */                           StyleRange style = DeviceManagerUI.deviceView.1.this.getStyleRangeAtOffset(offset);
/*      */                           
/* 4575 */                           if (style != null)
/*      */                           {
/* 4577 */                             Object data = style.data;
/*      */                             
/* 4579 */                             if ((data instanceof UPNPMSItem))
/*      */                             {
/* 4581 */                               int line = DeviceManagerUI.deviceView.1.this.getLineAtOffset(offset);
/* 4582 */                               int lineOffset = DeviceManagerUI.deviceView.1.this.getOffsetAtLine(line);
/*      */                               
/* 4584 */                               DeviceManagerUI.deviceView.1.this.setSelection(lineOffset, lineOffset + DeviceManagerUI.deviceView.1.this.getLine(line).length());
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                         catch (Throwable e) {}
/*      */                       }
/*      */                     }
/*      */                     
/* 4592 */                     listener.handleEvent(event);
/*      */                   }
/*      */                 });
/*      */               }
/*      */               finally
/*      */               {
/* 4598 */                 this.adding = false;
/*      */               }
/*      */               
/*      */             } else {
/* 4602 */               super.addListener(eventType, listener);
/*      */             }
/*      */             
/*      */           }
/* 4606 */         };
/* 4607 */         info.setEditable(false);
/*      */         
/* 4609 */         info.setSelectionForeground(info.getForeground());
/* 4610 */         info.setSelectionBackground(info.getBackground());
/*      */         
/* 4612 */         FormData data = new FormData();
/* 4613 */         data.left = new FormAttachment(0, 0);
/* 4614 */         data.bottom = new FormAttachment(info, -8);
/* 4615 */         ms_label.setLayoutData(data);
/*      */         
/*      */ 
/* 4618 */         data = new FormData();
/* 4619 */         data.left = new FormAttachment(ms_label, 4);
/* 4620 */         data.top = new FormAttachment(this.composite, 0);
/* 4621 */         refresh.setLayoutData(data);
/*      */         
/*      */ 
/* 4624 */         data = new FormData();
/* 4625 */         data.left = new FormAttachment(0, 0);
/* 4626 */         data.right = new FormAttachment(100, 0);
/* 4627 */         data.top = new FormAttachment(refresh, 4);
/* 4628 */         data.bottom = new FormAttachment(100, 0);
/* 4629 */         info.setLayoutData(data);
/*      */         
/* 4631 */         final Runnable do_refresh = new Runnable()
/*      */         {
/*      */           private UPNPMSItem dragging_item;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           boolean play_available;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void run()
/*      */           {
/* 4946 */             boolean went_async = false;
/*      */             try
/*      */             {
/* 4949 */               refresh.setEnabled(false);
/*      */               
/* 4951 */               info.setText("");
/*      */               
/* 4953 */               this.play_available = com.aelitis.azureus.util.PlayUtils.isEMPAvailable();
/*      */               
/* 4955 */               final DeviceContentDirectory cd = (DeviceContentDirectory)DeviceManagerUI.deviceView.this.device;
/*      */               
/* 4957 */               final List<URL> endpoints = cd.getControlURLs();
/*      */               
/* 4959 */               if ((endpoints == null) || (endpoints.size() == 0))
/*      */               {
/* 4961 */                 info.append("Media Server is offline");
/*      */               }
/*      */               else
/*      */               {
/* 4965 */                 new AEThread2("CD:populate")
/*      */                 {
/*      */                   private int line_count;
/* 4968 */                   private List<Object[]> lines_to_add = new ArrayList();
/*      */                   
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */                     try
/*      */                     {
/* 4975 */                       String client_name = DeviceManagerUI.this.device_manager.getLocalServiceName();
/*      */                       
/* 4977 */                       com.aelitis.net.upnpms.UPNPMSBrowser browser = com.aelitis.net.upnpms.UPNPMSBrowserFactory.create(client_name, endpoints, new com.aelitis.net.upnpms.UPNPMSBrowserListener()
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void setPreferredURL(URL url)
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/* 4987 */                           DeviceManagerUI.deviceView.2.6.this.val$cd.setPreferredControlURL(url);
/*      */                         }
/*      */                         
/* 4990 */                       });
/* 4991 */                       print(browser.getRoot(), "");
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 4995 */                       Debug.out(e);
/*      */                     }
/*      */                     finally
/*      */                     {
/* 4999 */                       Utils.execSWTThread(new Runnable()
/*      */                       {
/*      */ 
/*      */                         public void run()
/*      */                         {
/*      */ 
/* 5005 */                           if (!DeviceManagerUI.deviceView.2.this.val$refresh.isDisposed())
/*      */                           {
/* 5007 */                             DeviceManagerUI.deviceView.2.this.val$refresh.setEnabled(true);
/*      */                           }
/*      */                         }
/*      */                       });
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   private boolean print(UPNPMSContainer container, String indent)
/*      */                     throws com.aelitis.net.upnpms.UPnPMSException
/*      */                   {
/* 5021 */                     if (!appendLine(indent, container))
/*      */                     {
/* 5023 */                       return false;
/*      */                     }
/*      */                     try
/*      */                     {
/* 5027 */                       indent = indent + "\t\t";
/*      */                       
/* 5029 */                       List<UPNPMSNode> kids = container.getChildren();
/*      */                       
/* 5031 */                       for (UPNPMSNode kid : kids) {
/*      */                         boolean bool;
/* 5033 */                         if ((kid instanceof UPNPMSContainer))
/*      */                         {
/* 5035 */                           if (!print((UPNPMSContainer)kid, indent))
/*      */                           {
/* 5037 */                             return false;
/*      */                           }
/*      */                           
/*      */ 
/*      */                         }
/* 5042 */                         else if (!print((UPNPMSItem)kid, indent))
/*      */                         {
/* 5044 */                           return false;
/*      */                         }
/*      */                       }
/*      */                       
/*      */ 
/* 5049 */                       return 1;
/*      */                     }
/*      */                     finally
/*      */                     {
/* 5053 */                       updateInfo();
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */                   private boolean print(UPNPMSItem item, String indent)
/*      */                   {
/* 5062 */                     return appendLine(indent, item);
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */                   private boolean appendLine(String indent, Object obj)
/*      */                   {
/* 5070 */                     this.line_count += 1;
/*      */                     
/* 5072 */                     if (this.line_count >= DeviceManagerUI.max_ms_display_lines)
/*      */                     {
/* 5074 */                       if (this.line_count == DeviceManagerUI.max_ms_display_lines)
/*      */                       {
/* 5076 */                         this.lines_to_add.add(new Object[] { indent, "Too many entries, output truncated..." });
/*      */                       }
/*      */                       
/* 5079 */                       return false;
/*      */                     }
/*      */                     
/*      */ 
/* 5083 */                     this.lines_to_add.add(new Object[] { indent, obj });
/*      */                     
/* 5085 */                     return true;
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */                   private void updateInfo()
/*      */                   {
/* 5092 */                     if (DeviceManagerUI.deviceView.2.this.val$info.isDisposed())
/*      */                     {
/* 5094 */                       return;
/*      */                     }
/*      */                     
/* 5097 */                     final List<Object[]> temp = this.lines_to_add;
/*      */                     
/* 5099 */                     this.lines_to_add = new ArrayList();
/*      */                     
/* 5101 */                     Utils.execSWTThread(new Runnable()
/*      */                     {
/*      */ 
/*      */                       public void run()
/*      */                       {
/*      */ 
/* 5107 */                         if (DeviceManagerUI.deviceView.2.this.val$info.isDisposed())
/*      */                         {
/* 5109 */                           return;
/*      */                         }
/*      */                         
/* 5112 */                         for (Object[] entry : temp)
/*      */                         {
/* 5114 */                           String indent = (String)entry[0];
/* 5115 */                           Object obj = entry[1];
/*      */                           
/* 5117 */                           String line = indent;
/*      */                           
/* 5119 */                           if ((obj instanceof UPNPMSContainer))
/*      */                           {
/* 5121 */                             UPNPMSContainer container = (UPNPMSContainer)obj;
/*      */                             
/* 5123 */                             line = line + container.getTitle();
/*      */                             
/* 5125 */                             line = line + "\r\n";
/*      */                             
/* 5127 */                             int start_pos = DeviceManagerUI.deviceView.2.this.val$info.getCharCount();
/*      */                             
/* 5129 */                             DeviceManagerUI.deviceView.2.this.val$info.append(line);
/*      */                             
/* 5131 */                             StyleRange style = new StyleRange(start_pos, line.length(), null, null, 1);
/*      */                             
/* 5133 */                             DeviceManagerUI.deviceView.2.this.val$info.setStyleRange(style);
/*      */                           }
/* 5135 */                           else if ((obj instanceof UPNPMSItem))
/*      */                           {
/* 5137 */                             UPNPMSItem item = (UPNPMSItem)obj;
/*      */                             
/* 5139 */                             line = line + item.getTitle();
/*      */                             
/* 5141 */                             line = line + "\r\n";
/*      */                             
/* 5143 */                             int start_pos = DeviceManagerUI.deviceView.2.this.val$info.getCharCount();
/*      */                             
/* 5145 */                             DeviceManagerUI.deviceView.2.this.val$info.append(line);
/*      */                             
/* 5147 */                             String item_class = item.getItemClass();
/*      */                             
/* 5149 */                             if ((DeviceManagerUI.deviceView.2.this.play_available) && (item.getURL() != null) && ((item_class == "video") || (item_class == "audio")))
/*      */                             {
/*      */ 
/*      */ 
/*      */ 
/* 5154 */                               StyleRange style = new StyleRange(start_pos + indent.length(), line.length() - indent.length(), null, null, 0);
/*      */                               
/* 5156 */                               style.underline = true;
/* 5157 */                               style.underlineStyle = 4;
/*      */                               
/* 5159 */                               style.data = item;
/*      */                               
/* 5161 */                               DeviceManagerUI.deviceView.2.this.val$info.setStyleRange(style);
/*      */                             }
/*      */                             else
/*      */                             {
/* 5165 */                               StyleRange style = new StyleRange(start_pos, line.length(), null, null, 2);
/*      */                               
/* 5167 */                               style.data = item;
/*      */                               
/* 5169 */                               DeviceManagerUI.deviceView.2.this.val$info.setStyleRange(style);
/*      */                             }
/*      */                           }
/*      */                           else {
/* 5173 */                             line = line + (String)obj;
/*      */                             
/* 5175 */                             line = line + "\r\n";
/*      */                             
/* 5177 */                             int start_pos = DeviceManagerUI.deviceView.2.this.val$info.getCharCount();
/*      */                             
/* 5179 */                             DeviceManagerUI.deviceView.2.this.val$info.append(line);
/*      */                             
/* 5181 */                             StyleRange style = new StyleRange(start_pos, line.length(), null, null, 0);
/*      */                             
/* 5183 */                             DeviceManagerUI.deviceView.2.this.val$info.setStyleRange(style);
/*      */                           }
/*      */                           
/*      */                         }
/*      */                       }
/*      */                     });
/*      */                   }
/* 5190 */                 }.start();
/* 5191 */                 went_async = true;
/*      */               }
/*      */             } finally {
/* 5194 */               if (!went_async)
/*      */               {
/* 5196 */                 refresh.setEnabled(true);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 5201 */         };
/* 5202 */         do_refresh.run();
/*      */         
/* 5204 */         refresh.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event arg0)
/*      */           {
/*      */ 
/* 5211 */             do_refresh.run();
/*      */           }
/*      */         });
/*      */       }
/*      */       else {
/* 5216 */         FormData data = new FormData();
/*      */         
/* 5218 */         data.left = new FormAttachment(0, 0);
/* 5219 */         data.right = new FormAttachment(100, 0);
/* 5220 */         data.top = new FormAttachment(this.composite, 0);
/* 5221 */         data.bottom = new FormAttachment(100, 0);
/*      */         
/* 5223 */         Label label = new Label(this.composite, 0);
/*      */         
/* 5225 */         label.setText("Nothing to show for " + this.device.getName());
/*      */         
/* 5227 */         label.setLayoutData(data);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public Composite getComposite()
/*      */     {
/* 5234 */       return this.composite;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object getTitleInfoProperty(int propertyID)
/*      */     {
/* 5241 */       if (propertyID == 5)
/*      */       {
/* 5243 */         return getTitle();
/*      */       }
/* 5245 */       if (propertyID == 2) {
/* 5246 */         String imageID = null;
/* 5247 */         final String id = DeviceManagerUI.getDeviceImageID(this.device);
/*      */         
/* 5249 */         if (id != null)
/*      */         {
/* 5251 */           imageID = "image.sidebar.device." + id + ".small";
/*      */           
/* 5253 */           if (id.startsWith("http")) {
/* 5254 */             if (ImageLoader.getInstance().imageAdded_NoSWT(id)) {
/* 5255 */               imageID = id;
/*      */             } else {
/* 5257 */               Utils.execSWTThreadLater(0, new AERunnable() {
/*      */                 public void runSupport() {
/* 5259 */                   ImageLoader.getInstance().getUrlImage(id, new com.aelitis.azureus.ui.swt.imageloader.ImageLoader.ImageDownloaderListener() {
/*      */                     public void imageDownloaded(org.eclipse.swt.graphics.Image image, boolean returnedImmediately) {
/* 5261 */                       ViewTitleInfoManager.refreshTitleInfo(DeviceManagerUI.deviceView.this);
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 5270 */         return imageID;
/*      */       }
/* 5272 */       if (propertyID == 0)
/*      */       {
/* 5274 */         if (!(this.device instanceof DeviceMediaRenderer))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5283 */           if ((this.device instanceof DeviceOfflineDownloader))
/*      */           {
/*      */ 
/*      */ 
/* 5287 */             DeviceOfflineDownloader dod = (DeviceOfflineDownloader)this.device;
/*      */             
/* 5289 */             this.last_indicator = dod.getTransferingCount();
/*      */           }
/*      */         }
/*      */         
/* 5293 */         if (this.last_indicator > 0)
/*      */         {
/* 5295 */           return String.valueOf(this.last_indicator);
/*      */         }
/* 5297 */       } else if (propertyID != 8)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5307 */         if (propertyID == 9)
/*      */         {
/* 5309 */           if (this.device.isLivenessDetectable())
/*      */           {
/* 5311 */             return new Long(this.device.isAlive() ? 1L : 2L);
/*      */           }
/* 5313 */         } else if (propertyID == 1)
/*      */         {
/* 5315 */           return this.device.getStatus();
/*      */         }
/*      */       }
/* 5318 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getTitle()
/*      */     {
/* 5324 */       return this.device.getName();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void fileAdded(TranscodeFile file) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void fileChanged(TranscodeFile file, int type, Object data)
/*      */     {
/* 5339 */       if ((type == 1) && (data == "comp"))
/*      */       {
/*      */ 
/* 5342 */         refreshTitles();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void refreshTitles()
/*      */     {
/* 5349 */       ViewTitleInfoManager.refreshTitleInfo(this);
/*      */       
/* 5351 */       String key = this.parent_key;
/*      */       
/* 5353 */       while (key != null)
/*      */       {
/* 5355 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*      */         
/* 5357 */         if (mdi == null) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 5363 */         MdiEntry parent = mdi.getEntry(key);
/*      */         
/* 5365 */         if (parent != null)
/*      */         {
/* 5367 */           ViewTitleInfoManager.refreshTitleInfo(parent.getViewTitleInfo());
/*      */           
/* 5369 */           key = parent.getParentID();
/*      */         } else {
/* 5371 */           key = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void fileRemoved(TranscodeFile file) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void delete()
/*      */     {
/* 5386 */       if ((this.device instanceof DeviceMediaRenderer))
/*      */       {
/* 5388 */         DeviceMediaRenderer renderer = (DeviceMediaRenderer)this.device;
/*      */         
/* 5390 */         renderer.removeListener(this);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean eventOccurred(UISWTViewEvent event) {
/* 5395 */       switch (event.getType()) {
/*      */       case 0: 
/* 5397 */         this.swtView = ((UISWTView)event.getData());
/* 5398 */         this.swtView.setTitle(getTitle());
/* 5399 */         break;
/*      */       
/*      */       case 7: 
/* 5402 */         delete();
/* 5403 */         break;
/*      */       
/*      */       case 2: 
/* 5406 */         initialize((Composite)event.getData());
/* 5407 */         break;
/*      */       
/*      */       case 6: 
/* 5410 */         Messages.updateLanguageForControl(getComposite());
/* 5411 */         this.swtView.setTitle(getTitle());
/* 5412 */         break;
/*      */       case 1: 
/*      */         break;
/*      */       case 3: 
/*      */         break;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5424 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class deviceItem
/*      */   {
/*      */     private DeviceManagerUI.deviceView view;
/*      */     
/*      */ 
/*      */     private MdiEntry sb_entry;
/*      */     
/*      */ 
/*      */     private boolean destroyed;
/*      */     
/*      */     private MdiEntryVitalityImage warning;
/*      */     
/*      */     private MdiEntryVitalityImage spinner;
/*      */     
/*      */     private MdiEntryVitalityImage info;
/*      */     
/*      */ 
/*      */     protected void setMdiEntry(MdiEntry _sb_entry)
/*      */     {
/* 5449 */       this.sb_entry = _sb_entry;
/*      */       
/* 5451 */       this.warning = this.sb_entry.addVitalityImage("image.sidebar.vitality.alert");
/*      */       
/* 5453 */       DeviceManagerUI.hideIcon(this.warning);
/*      */       
/* 5455 */       this.spinner = this.sb_entry.addVitalityImage("image.sidebar.vitality.dl");
/*      */       
/* 5457 */       DeviceManagerUI.hideIcon(this.spinner);
/*      */       
/* 5459 */       this.info = this.sb_entry.addVitalityImage("image.sidebar.vitality.info");
/*      */       
/* 5461 */       DeviceManagerUI.hideIcon(this.info);
/*      */     }
/*      */     
/*      */ 
/*      */     protected MdiEntry getMdiEntry()
/*      */     {
/* 5467 */       return this.sb_entry;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setView(DeviceManagerUI.deviceView _view)
/*      */     {
/* 5474 */       this.view = _view;
/*      */     }
/*      */     
/*      */ 
/*      */     protected DeviceManagerUI.deviceView getView()
/*      */     {
/* 5480 */       return this.view;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void setStatus(Device device)
/*      */     {
/* 5489 */       if ((this.warning != null) && (this.info != null))
/*      */       {
/* 5491 */         String error = device.getError();
/*      */         
/* 5493 */         if (error != null)
/*      */         {
/* 5495 */           DeviceManagerUI.hideIcon(this.info);
/*      */           
/* 5497 */           this.warning.setToolTip(error);
/*      */           
/* 5499 */           this.warning.setImageID("image.sidebar.vitality.alert");
/*      */           
/* 5501 */           this.warning.setVisible(true);
/*      */         }
/*      */         else
/*      */         {
/* 5505 */           DeviceManagerUI.hideIcon(this.warning);
/*      */           
/* 5507 */           String info_str = device.getInfo();
/*      */           
/* 5509 */           if (info_str != null)
/*      */           {
/* 5511 */             DeviceManagerUI.showIcon(this.info, info_str);
/*      */           }
/*      */           else
/*      */           {
/* 5515 */             DeviceManagerUI.hideIcon(this.info);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 5520 */       if (this.spinner != null)
/*      */       {
/* 5522 */         this.spinner.setVisible(device.isBusy());
/*      */       }
/*      */       
/* 5525 */       if (this.view != null)
/*      */       {
/* 5527 */         this.view.refreshTitles();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isDestroyed()
/*      */     {
/* 5534 */       return this.destroyed;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void destroy()
/*      */     {
/* 5540 */       this.destroyed = true;
/*      */       
/* 5542 */       if (this.sb_entry != null) {
/* 5543 */         this.sb_entry.close(false);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void activate()
/*      */     {
/* 5550 */       MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*      */       
/* 5552 */       if ((mdi != null) && (this.sb_entry != null))
/*      */       {
/* 5554 */         mdi.showEntryByID(this.sb_entry.getId());
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/DeviceManagerUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */