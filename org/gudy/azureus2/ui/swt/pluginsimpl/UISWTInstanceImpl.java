/*      */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*      */ 
/*      */ import com.aelitis.azureus.ui.IUIIntializer;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableStructureEventDispatcher;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnImpl;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.WeakHashMap;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.ImageData;
/*      */ import org.eclipse.swt.graphics.Resource;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.ui.UIException;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstanceFactory;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManagerEvent;
/*      */ import org.gudy.azureus2.plugins.ui.UIMessage;
/*      */ import org.gudy.azureus2.plugins.ui.UIRuntimeException;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarItem;
/*      */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ConfigSectionRepository;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.FileDownloadWindow;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.gudy.azureus2.ui.swt.minibar.AllTransfersBar;
/*      */ import org.gudy.azureus2.ui.swt.minibar.DownloadBar;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance.UISWTViewEventListenerWrapper;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntry;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.swt.views.table.utils.TableContextMenuManager;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*      */ 
/*      */ public class UISWTInstanceImpl implements UIInstanceFactory, UISWTInstance, org.gudy.azureus2.plugins.ui.UIManagerEventListener
/*      */ {
/*   82 */   private Map<BasicPluginConfigModel, BasicPluginConfigImpl> config_view_map = new WeakHashMap();
/*      */   
/*      */ 
/*   85 */   private Map<String, Map<String, UISWTViewEventListenerHolder>> views = new HashMap();
/*      */   
/*   87 */   private Map<PluginInterface, UIInstance> plugin_map = new WeakHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean bUIAttaching;
/*      */   
/*      */ 
/*      */ 
/*      */   private final UIFunctionsSWT uiFunctions;
/*      */   
/*      */ 
/*   98 */   private List<SWTViewListener> listSWTViewListeners = new ArrayList(0);
/*      */   
/*      */ 
/*      */   public UISWTInstanceImpl()
/*      */   {
/*  103 */     this.uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUIType()
/*      */   {
/*  109 */     return 1;
/*      */   }
/*      */   
/*      */   public void init(IUIIntializer init) {
/*  113 */     UIManager ui_manager = PluginInitializer.getDefaultInterface().getUIManager();
/*  114 */     ui_manager.addUIEventListener(this);
/*      */     
/*  116 */     this.bUIAttaching = true;
/*      */     
/*  118 */     ((UIManagerImpl)ui_manager).attachUI(this, init);
/*      */     
/*  120 */     this.bUIAttaching = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public UIInstance getInstance(PluginInterface plugin_interface)
/*      */   {
/*  127 */     UIInstance instance = (UIInstance)this.plugin_map.get(plugin_interface);
/*      */     
/*  129 */     if (instance == null)
/*      */     {
/*  131 */       instance = new instanceWrapper(plugin_interface, this.uiFunctions, this);
/*      */       
/*  133 */       this.plugin_map.put(plugin_interface, instance);
/*      */     }
/*      */     
/*  136 */     return instance;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean eventOccurred(UIManagerEvent event)
/*      */   {
/*  143 */     boolean done = true;
/*      */     
/*  145 */     final Object data = event.getData();
/*      */     
/*  147 */     switch (event.getType())
/*      */     {
/*      */ 
/*      */     case 1: 
/*  151 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*  157 */           String[] params = (String[])data;
/*      */           
/*  159 */           new org.gudy.azureus2.ui.swt.TextViewerWindow(params[0], params[1], params[2]);
/*      */         }
/*      */         
/*  162 */       });
/*  163 */       break;
/*      */     
/*      */ 
/*      */     case 21: 
/*  167 */       final int[] result = { 0 };
/*      */       
/*  169 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*  175 */           UIFunctionsManagerSWT.getUIFunctionsSWT().bringToFront();
/*      */           
/*  177 */           Object[] params = (Object[])data;
/*      */           
/*  179 */           long _styles = ((Long)params[2]).longValue();
/*      */           
/*  181 */           int styles = 0;
/*  182 */           int def = 0;
/*      */           
/*  184 */           if ((_styles & 0x4) != 0L)
/*      */           {
/*  186 */             styles |= 0x40;
/*      */           }
/*  188 */           if ((_styles & 0x10) != 0L)
/*      */           {
/*  190 */             styles |= 0x40;
/*  191 */             def = 64;
/*      */           }
/*  193 */           if ((_styles & 0x8) != 0L)
/*      */           {
/*  195 */             styles |= 0x80;
/*      */           }
/*  197 */           if ((_styles & 0x20) != 0L)
/*      */           {
/*  199 */             styles |= 0x80;
/*  200 */             def = 128;
/*      */           }
/*  202 */           if ((_styles & 1L) != 0L)
/*      */           {
/*  204 */             styles |= 0x20;
/*      */           }
/*  206 */           if ((_styles & 0x40) != 0L)
/*      */           {
/*  208 */             styles |= 0x20;
/*  209 */             def = 32;
/*      */           }
/*      */           
/*  212 */           if ((_styles & 0x2) != 0L)
/*      */           {
/*  214 */             styles |= 0x100;
/*      */           }
/*      */           
/*      */ 
/*  218 */           MessageBoxShell mb = new MessageBoxShell(styles, MessageText.getString((String)params[0]), MessageText.getString((String)params[1]));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  224 */           if (def != 0)
/*      */           {
/*  226 */             mb.setDefaultButtonUsingStyle(def);
/*      */           }
/*      */           
/*  229 */           if ((params.length == 4) && ((params[3] instanceof Map)))
/*      */           {
/*  231 */             Map<String, Object> options = (Map)params[3];
/*      */             
/*  233 */             String rememberID = (String)options.get("remember-id");
/*  234 */             Boolean rememberByDefault = (Boolean)options.get("remember-by-def");
/*  235 */             String rememberText = (String)options.get("remember-res");
/*      */             
/*  237 */             if ((rememberID != null) && (rememberByDefault != null) && (rememberText != null))
/*      */             {
/*  239 */               mb.setRemember(rememberID, rememberByDefault.booleanValue(), rememberText);
/*      */             }
/*      */             
/*  242 */             Number auto_close_ms = (Number)options.get("auto-close-ms");
/*      */             
/*  244 */             if (auto_close_ms != null)
/*      */             {
/*  246 */               mb.setAutoCloseInMS(auto_close_ms.intValue());
/*      */             }
/*  248 */           } else if (params.length >= 6)
/*      */           {
/*  250 */             String rememberID = (String)params[3];
/*  251 */             Boolean rememberByDefault = (Boolean)params[4];
/*  252 */             String rememberText = (String)params[5];
/*      */             
/*  254 */             if ((rememberID != null) && (rememberByDefault != null) && (rememberText != null))
/*      */             {
/*  256 */               mb.setRemember(rememberID, rememberByDefault.booleanValue(), rememberText);
/*      */             }
/*      */           }
/*      */           
/*  260 */           mb.open(null);
/*      */           
/*  262 */           int _r = mb.waitUntilClosed();
/*      */           
/*  264 */           int r = 0;
/*      */           
/*  266 */           if ((_r & 0x40) != 0)
/*      */           {
/*  268 */             r |= 0x4;
/*      */           }
/*  270 */           if ((_r & 0x80) != 0)
/*      */           {
/*  272 */             r |= 0x8;
/*      */           }
/*  274 */           if ((_r & 0x20) != 0)
/*      */           {
/*  276 */             r |= 0x1;
/*      */           }
/*  278 */           if ((_r & 0x100) != 0)
/*      */           {
/*  280 */             r |= 0x2;
/*      */           }
/*      */           
/*  283 */           result[0] = r; } }, false);
/*      */       
/*      */ 
/*      */ 
/*  287 */       event.setResult(new Long(result[0]));
/*      */       
/*  289 */       break;
/*      */     
/*      */ 
/*      */     case 2: 
/*  293 */       TorrentOpener.openTorrent(((File)data).toString());
/*      */       
/*  295 */       break;
/*      */     
/*      */ 
/*      */     case 22: 
/*  299 */       Torrent t = (Torrent)data;
/*      */       try
/*      */       {
/*  302 */         File f = AETemporaryFileHandler.createTempFile();
/*      */         
/*  304 */         t.writeToFile(f);
/*      */         
/*  306 */         TorrentOpener.openTorrent(f.toString());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  310 */         Debug.printStackTrace(e);
/*      */       }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 3: 
/*  317 */       Display display = SWTThread.getInstance().getDisplay();
/*      */       
/*  319 */       display.syncExec(new AERunnable() {
/*      */         public void runSupport() {
/*  321 */           Object[] params = (Object[])data;
/*      */           
/*  323 */           URL target = (URL)params[0];
/*  324 */           URL referrer = (URL)params[1];
/*  325 */           boolean auto_download = ((Boolean)params[2]).booleanValue();
/*  326 */           Map<?, ?> request_properties = (Map)params[3];
/*      */           
/*      */ 
/*      */ 
/*  330 */           if (auto_download)
/*      */           {
/*  332 */             final Shell shell = UISWTInstanceImpl.this.uiFunctions.getMainShell();
/*      */             
/*  334 */             if (shell != null)
/*      */             {
/*  336 */               final List<String> alt_uris = new ArrayList();
/*      */               
/*  338 */               if (request_properties != null)
/*      */               {
/*  340 */                 request_properties = new HashMap(request_properties);
/*      */                 
/*  342 */                 for (int i = 1; i < 16; i++)
/*      */                 {
/*  344 */                   String key = "X-Alternative-URI-" + i;
/*      */                   
/*  346 */                   String uri = (String)request_properties.remove(key);
/*      */                   
/*  348 */                   if (uri == null)
/*      */                     break;
/*  350 */                   alt_uris.add(uri);
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  359 */               final Map<?, ?> f_request_properties = request_properties;
/*      */               
/*  361 */               new FileDownloadWindow(shell, target.toString(), referrer == null ? null : referrer.toString(), request_properties, new Runnable()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  368 */                 int alt_index = 0;
/*      */                 
/*      */                 public void run()
/*      */                 {
/*  372 */                   if (this.alt_index < alt_uris.size())
/*      */                   {
/*  374 */                     String alt_target = (String)alt_uris.get(this.alt_index++);
/*      */                     
/*  376 */                     new FileDownloadWindow(shell, alt_target, null, f_request_properties, this);
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */ 
/*      */               });
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  390 */             TorrentOpener.openTorrent(target.toString());
/*      */           }
/*      */           
/*      */         }
/*  394 */       });
/*  395 */       break;
/*      */     
/*      */ 
/*      */     case 4: 
/*  399 */       if ((data instanceof BasicPluginViewModel)) {
/*  400 */         BasicPluginViewModel model = (BasicPluginViewModel)data;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  406 */         String sViewID = model.getName().replaceAll(" ", ".");
/*  407 */         BasicPluginViewImpl view = new BasicPluginViewImpl(model);
/*  408 */         addView("Main", sViewID, view); }
/*  409 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 7: 
/*  415 */       if ((data instanceof BasicPluginViewModel)) {
/*  416 */         BasicPluginViewModel model = (BasicPluginViewModel)data;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  421 */         String sViewID = model.getName().replaceAll(" ", ".");
/*  422 */         removeViews("Main", sViewID); }
/*  423 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 5: 
/*  429 */       if ((data instanceof BasicPluginConfigModel))
/*      */       {
/*  431 */         BasicPluginConfigModel model = (BasicPluginConfigModel)data;
/*      */         
/*  433 */         BasicPluginConfigImpl view = new BasicPluginConfigImpl(new WeakReference(model));
/*      */         
/*  435 */         this.config_view_map.put(model, view);
/*      */         
/*  437 */         ConfigSectionRepository.getInstance().addConfigSection(view, model.getPluginInterface()); }
/*  438 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 8: 
/*  444 */       if ((data instanceof BasicPluginConfigModel))
/*      */       {
/*  446 */         BasicPluginConfigModel model = (BasicPluginConfigModel)data;
/*      */         
/*  448 */         BasicPluginConfigImpl view = (BasicPluginConfigImpl)this.config_view_map.get(model);
/*      */         
/*  450 */         if (view != null)
/*      */         {
/*  452 */           ConfigSectionRepository.getInstance().removeConfigSection(view);
/*      */         }
/*      */       }
/*  455 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 6: 
/*  461 */       ClipboardCopy.copyToClipBoard((String)data);
/*      */       
/*  463 */       break;
/*      */     
/*      */ 
/*      */     case 9: 
/*  467 */       Utils.launch(((URL)data).toExternalForm());
/*      */       
/*  469 */       break;
/*      */     
/*      */ 
/*      */     case 10: 
/*  473 */       if ((data instanceof TableColumn)) {
/*  474 */         event.setResult(data);
/*      */       } else {
/*  476 */         String[] args = (String[])data;
/*      */         
/*  478 */         event.setResult(new TableColumnImpl(args[0], args[1]));
/*      */       }
/*      */       
/*  481 */       break;
/*      */     
/*      */ 
/*      */     case 11: 
/*  485 */       TableColumn _col = (TableColumn)data;
/*      */       
/*  487 */       if ((_col instanceof TableColumnImpl))
/*      */       {
/*  489 */         TableColumnManager.getInstance().addColumns(new TableColumnCore[] { (TableColumnCore)_col });
/*      */         
/*  491 */         TableStructureEventDispatcher tsed = TableStructureEventDispatcher.getInstance(_col.getTableID());
/*      */         
/*  493 */         tsed.tableStructureChanged(true, _col.getForDataSourceType());
/*      */       }
/*      */       else
/*      */       {
/*  497 */         throw new UIRuntimeException("TableManager.addColumn(..) can only add columns created by createColumn(..)");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       break;
/*      */     case 25: 
/*  504 */       Object[] params = (Object[])data;
/*      */       
/*  506 */       TableColumnManager tcManager = TableColumnManager.getInstance();
/*      */       
/*  508 */       Class<?> dataSource = (Class)params[0];
/*  509 */       String columnName = (String)params[1];
/*      */       
/*  511 */       tcManager.registerColumn(dataSource, columnName, (TableColumnCreationListener)params[2]);
/*      */       
/*  513 */       String[] tables = tcManager.getTableIDs();
/*      */       
/*  515 */       for (String tid : tables)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  521 */         TableStructureEventDispatcher tsed = TableStructureEventDispatcher.getInstance(tid);
/*      */         
/*  523 */         tsed.tableStructureChanged(true, dataSource);
/*      */       }
/*      */       
/*  526 */       break;
/*      */     
/*      */ 
/*      */     case 26: 
/*  530 */       Object[] params = (Object[])data;
/*      */       
/*  532 */       TableColumnManager tcManager = TableColumnManager.getInstance();
/*      */       
/*  534 */       Class<?> dataSource = (Class)params[0];
/*  535 */       String columnName = (String)params[1];
/*      */       
/*  537 */       tcManager.unregisterColumn(dataSource, columnName, (TableColumnCreationListener)params[2]);
/*      */       
/*  539 */       String[] tables = tcManager.getTableIDs();
/*      */       
/*  541 */       for (String tid : tables)
/*      */       {
/*  543 */         TableColumnCore col = tcManager.getTableColumnCore(tid, columnName);
/*      */         
/*  545 */         if (col != null)
/*      */         {
/*  547 */           col.remove();
/*      */         }
/*      */       }
/*      */       
/*  551 */       break;
/*      */     
/*      */     case 12: 
/*  554 */       TableContextMenuItem item = (TableContextMenuItem)data;
/*  555 */       TableContextMenuManager.getInstance().addContextMenuItem(item);
/*  556 */       break;
/*      */     
/*      */     case 15: 
/*  559 */       MenuItem item = (MenuItem)data;
/*  560 */       MenuItemManager.getInstance().addMenuItem(item);
/*  561 */       break;
/*      */     
/*      */     case 17: 
/*  564 */       TableContextMenuItem item = (TableContextMenuItem)data;
/*  565 */       TableContextMenuManager.getInstance().removeContextMenuItem(item);
/*  566 */       break;
/*      */     
/*      */     case 19: 
/*  569 */       MenuItem item = (MenuItem)data;
/*  570 */       MenuItemManager.getInstance().removeMenuItem(item);
/*  571 */       break;
/*      */     
/*      */     case 13: 
/*  574 */       event.setResult(Boolean.FALSE);
/*      */       
/*  576 */       if ((data instanceof String))
/*      */       {
/*      */ 
/*      */ 
/*  580 */         event.setResult(Boolean.TRUE);
/*      */         
/*  582 */         this.uiFunctions.getMDI().showEntryByID("ConfigView", data);
/*      */       }
/*      */       
/*  585 */       break;
/*      */     
/*      */     case 24: 
/*  588 */       File file_to_use = (File)data;
/*  589 */       Utils.launch(file_to_use.getAbsolutePath());
/*  590 */       break;
/*      */     
/*      */     case 23: 
/*  593 */       File file_to_use = (File)data;
/*  594 */       boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*  595 */       ManagerUtils.open(file_to_use, use_open_containing_folder);
/*  596 */       break;
/*      */     
/*      */     case 27: 
/*  599 */       boolean hide = ((Boolean)data).booleanValue();
/*      */       
/*  601 */       this.uiFunctions.setHideAll(hide);
/*      */       
/*  603 */       break;
/*      */     case 14: case 16: 
/*      */     case 18: case 20: 
/*      */     default: 
/*  607 */       done = false;
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*  613 */     return done;
/*      */   }
/*      */   
/*      */ 
/*      */   public Display getDisplay()
/*      */   {
/*  619 */     return SWTThread.getInstance().getDisplay();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Image loadImage(String resource)
/*      */   {
/*  626 */     throw new RuntimeException("plugin specific instance required");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public UISWTGraphic createGraphic(Image img)
/*      */   {
/*  633 */     return new UISWTGraphicImpl(img);
/*      */   }
/*      */   
/*      */   public Shell createShell(int style) {
/*  637 */     Shell shell = ShellFactory.createMainShell(style);
/*  638 */     Utils.setShellIcon(shell);
/*  639 */     return shell;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void detach()
/*      */     throws UIException
/*      */   {
/*  648 */     throw new UIException("not supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addView(String sParentID, String sViewID, Class<? extends UISWTViewEventListener> cla, Object datasource)
/*      */   {
/*  657 */     addView(null, sParentID, sViewID, cla, datasource);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addView(PluginInterface pi, String sParentID, String sViewID, Class<? extends UISWTViewEventListener> cla, Object datasource)
/*      */   {
/*  663 */     UISWTViewEventListenerHolder _l = new UISWTViewEventListenerHolder(sViewID, cla, datasource, pi);
/*      */     
/*  665 */     addView(sParentID, sViewID, _l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addView(String sParentID, String sViewID, UISWTViewEventListener l)
/*      */   {
/*  673 */     UISWTViewEventListenerHolder _l = new UISWTViewEventListenerHolder(sViewID, l, null);
/*  674 */     addView(sParentID, sViewID, _l);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addView(String sParentID, final String sViewID, final UISWTViewEventListenerHolder holder)
/*      */   {
/*  680 */     if (sParentID == null) {
/*  681 */       sParentID = "Main";
/*      */     }
/*  683 */     Map<String, UISWTViewEventListenerHolder> subViews = (Map)this.views.get(sParentID);
/*  684 */     if (subViews == null) {
/*  685 */       subViews = new LinkedHashMap();
/*  686 */       this.views.put(sParentID, subViews);
/*      */     }
/*      */     
/*  689 */     subViews.put(sViewID, holder);
/*      */     
/*  691 */     if (sParentID.equals("Main")) {
/*  692 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*      */           try {
/*  695 */             UISWTInstanceImpl.this.uiFunctions.addPluginView(sViewID, holder);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  703 */     SWTViewListener[] viewListeners = (SWTViewListener[])this.listSWTViewListeners.toArray(new SWTViewListener[0]);
/*  704 */     for (SWTViewListener l : viewListeners) {
/*  705 */       l.setViewAdded(sParentID, sViewID, holder);
/*      */     }
/*      */   }
/*      */   
/*      */   public void addSWTViewListener(SWTViewListener l) {
/*  710 */     this.listSWTViewListeners.add(l);
/*      */   }
/*      */   
/*      */   public void removeSWTViewListener(SWTViewListener l) {
/*  714 */     this.listSWTViewListeners.remove(l);
/*      */   }
/*      */   
/*      */   public void removeViews(String sParentID, final String sViewID)
/*      */   {
/*  719 */     Map<String, UISWTViewEventListenerHolder> subViews = (Map)this.views.get(sParentID);
/*  720 */     if (subViews == null) {
/*  721 */       return;
/*      */     }
/*  723 */     if (sParentID.equals("Main")) {
/*  724 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*      */           try {
/*  727 */             if (UISWTInstanceImpl.this.uiFunctions != null) {
/*  728 */               UISWTInstanceImpl.this.uiFunctions.removePluginView(sViewID);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  737 */     SWTViewListener[] viewListeners = (SWTViewListener[])this.listSWTViewListeners.toArray(new SWTViewListener[0]);
/*  738 */     for (UISWTViewEventListener holder : subViews.values()) {
/*  739 */       for (SWTViewListener l : viewListeners) {
/*  740 */         l.setViewRemoved(sParentID, sViewID, holder);
/*      */       }
/*      */     }
/*      */     
/*  744 */     subViews.remove(sViewID);
/*      */   }
/*      */   
/*      */   public boolean openView(String sParentID, String sViewID, Object dataSource)
/*      */   {
/*  749 */     return openView(sParentID, sViewID, dataSource, true);
/*      */   }
/*      */   
/*      */   public boolean openView(final String sParentID, final String sViewID, final Object dataSource, final boolean setfocus)
/*      */   {
/*  754 */     Map<String, UISWTViewEventListenerHolder> subViews = (Map)this.views.get(sParentID);
/*  755 */     if (subViews == null) {
/*  756 */       return false;
/*      */     }
/*      */     
/*  759 */     final UISWTViewEventListenerHolder l = (UISWTViewEventListenerHolder)subViews.get(sViewID);
/*  760 */     if (l == null) {
/*  761 */       return false;
/*      */     }
/*      */     
/*  764 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  766 */         if (UISWTInstanceImpl.this.uiFunctions != null) {
/*  767 */           UISWTInstanceImpl.this.uiFunctions.openPluginView(sParentID, sViewID, l, dataSource, (setfocus) && (!UISWTInstanceImpl.this.bUIAttaching));
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*  773 */     });
/*  774 */     return true;
/*      */   }
/*      */   
/*      */   public void openMainView(String sViewID, UISWTViewEventListener l, Object dataSource)
/*      */   {
/*  779 */     openMainView(null, sViewID, l, dataSource, true);
/*      */   }
/*      */   
/*      */   public void openMainView(PluginInterface pi, String sViewID, UISWTViewEventListener l, Object dataSource)
/*      */   {
/*  784 */     openMainView(pi, sViewID, l, dataSource, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void openMainView(String sViewID, UISWTViewEventListener l, Object dataSource, boolean setfocus)
/*      */   {
/*  790 */     openMainView(null, sViewID, l, dataSource, setfocus);
/*      */   }
/*      */   
/*      */   public void openMainView(final PluginInterface pi, final String sViewID, final UISWTViewEventListener _l, final Object dataSource, final boolean setfocus)
/*      */   {
/*  795 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  797 */         if (UISWTInstanceImpl.this.uiFunctions != null) {
/*  798 */           UISWTViewEventListenerHolder l = new UISWTViewEventListenerHolder(sViewID, _l, pi);
/*      */           
/*  800 */           UISWTInstanceImpl.this.uiFunctions.openPluginView("Main", sViewID, l, dataSource, (setfocus) && (!UISWTInstanceImpl.this.bUIAttaching));
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public UISWTView[] getOpenViews(String sParentID) {
/*  807 */     if (sParentID.equals("Main")) {
/*      */       try {
/*  809 */         if (this.uiFunctions != null) {
/*  810 */           return this.uiFunctions.getPluginViews();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*  816 */     return new UISWTView[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int promptUser(String title, String text, String[] options, int defaultOption)
/*      */   {
/*  823 */     MessageBoxShell mb = new MessageBoxShell(title, text, options, defaultOption);
/*      */     
/*  825 */     mb.open(null);
/*      */     
/*  827 */     return mb.waitUntilClosed();
/*      */   }
/*      */   
/*      */   public void showDownloadBar(Download download, final boolean display) {
/*  831 */     if (!(download instanceof DownloadImpl)) return;
/*  832 */     final DownloadManager dm = ((DownloadImpl)download).getDownload();
/*  833 */     if (dm == null) return;
/*  834 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  836 */         if (display) {
/*  837 */           DownloadBar.open(dm, UISWTInstanceImpl.this.getDisplay().getActiveShell());
/*      */         }
/*      */         else
/*  840 */           DownloadBar.close(dm); } }, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void showTransfersBar(final boolean display)
/*      */   {
/*  847 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  849 */         if (display) {
/*  850 */           AllTransfersBar.open(UISWTInstanceImpl.this.getDisplay().getActiveShell());
/*      */         }
/*      */         else
/*  853 */           AllTransfersBar.closeAllTransfersBar(); } }, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UISWTViewEventListenerHolder[] getViewListeners(String sParentID)
/*      */   {
/*  863 */     Map<String, UISWTViewEventListenerHolder> map = (Map)this.views.get(sParentID);
/*  864 */     if (map == null) {
/*  865 */       return new UISWTViewEventListenerHolder[0];
/*      */     }
/*  867 */     UISWTViewEventListenerHolder[] array = (UISWTViewEventListenerHolder[])map.values().toArray(new UISWTViewEventListenerHolder[0]);
/*  868 */     Arrays.sort(array, new Comparator()
/*      */     {
/*      */       public int compare(UISWTViewEventListenerHolder o1, UISWTViewEventListenerHolder o2) {
/*  871 */         if ((o1.getPluginInterface() == null) && (o2.getPluginInterface() == null)) {
/*  872 */           return 0;
/*      */         }
/*  874 */         if ((o1.getPluginInterface() != null) && (o2.getPluginInterface() != null)) {
/*  875 */           return 0;
/*      */         }
/*  877 */         return o1.getPluginInterface() == null ? -1 : 1;
/*      */       }
/*  879 */     });
/*  880 */     return array;
/*      */   }
/*      */   
/*      */   public UIInputReceiver getInputReceiver()
/*      */   {
/*  885 */     return new SimpleTextEntryWindow();
/*      */   }
/*      */   
/*      */   public UIMessage createMessage()
/*      */   {
/*  890 */     return new UIMessageImpl();
/*      */   }
/*      */   
/*      */   public UISWTStatusEntry createStatusEntry() {
/*  894 */     UISWTStatusEntryImpl entry = new UISWTStatusEntryImpl();
/*  895 */     UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  896 */     if (functionsSWT == null) {
/*  897 */       Debug.outNoStack("No UIFunctionsSWT on createStatusEntry");
/*  898 */       return null;
/*      */     }
/*      */     
/*  901 */     IMainStatusBar mainStatusBar = functionsSWT.getMainStatusBar();
/*  902 */     if (mainStatusBar == null) {
/*  903 */       Debug.outNoStack("No MainStatusBar on createStatusEntry");
/*  904 */       return null;
/*      */     }
/*  906 */     mainStatusBar.createStatusEntry(entry);
/*      */     
/*  908 */     return entry;
/*      */   }
/*      */   
/*      */   public boolean openView(BasicPluginViewModel model) {
/*  912 */     return openView("Main", model.getName().replaceAll(" ", "."), null);
/*      */   }
/*      */   
/*      */   public void openConfig(final BasicPluginConfigModel model) {
/*  916 */     Utils.execSWTThread(new Runnable() {
/*      */       public void run() {
/*  918 */         UISWTInstanceImpl.this.uiFunctions.getMDI().loadEntryByID("ConfigView", true, false, model.getSection());
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  926 */   public UIToolBarManager getToolBarManager() { throw new RuntimeException("plugin specific instance required"); }
/*      */   
/*      */   public static abstract interface SWTViewListener {
/*      */     public abstract void setViewAdded(String paramString1, String paramString2, UISWTViewEventListener paramUISWTViewEventListener);
/*      */     
/*      */     public abstract void setViewRemoved(String paramString1, String paramString2, UISWTViewEventListener paramUISWTViewEventListener);
/*      */   }
/*      */   
/*      */   protected static class instanceWrapper implements UISWTInstance, UIToolBarManager { private WeakReference<PluginInterface> pi_ref;
/*      */     private UIFunctionsSWT ui_functions;
/*      */     private UISWTInstanceImpl delegate;
/*      */     private UIToolBarManagerCore toolBarManager;
/*  938 */     private List<UIToolBarItem> listItems = new ArrayList();
/*  939 */     private List<Resource> listDisposeOnUnload = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected instanceWrapper(PluginInterface _pi, UIFunctionsSWT _ui_functions, UISWTInstanceImpl _delegate)
/*      */     {
/*  947 */       this.pi_ref = new WeakReference(_pi);
/*  948 */       this.ui_functions = _ui_functions;
/*  949 */       this.delegate = _delegate;
/*      */     }
/*      */     
/*      */     public UIToolBarItem getToolBarItem(String id) {
/*  953 */       return this.toolBarManager.getToolBarItem(id);
/*      */     }
/*      */     
/*      */     public UIToolBarItem[] getAllToolBarItems() {
/*  957 */       return this.toolBarManager.getAllToolBarItems();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public UIToolBarItem createToolBarItem(String id)
/*      */     {
/*  964 */       UIToolBarItem addToolBarItem = this.toolBarManager.createToolBarItem(id);
/*      */       
/*  966 */       synchronized (this)
/*      */       {
/*  968 */         this.listItems.add(addToolBarItem);
/*      */       }
/*      */       
/*  971 */       return addToolBarItem;
/*      */     }
/*      */     
/*      */     public void addToolBarItem(UIToolBarItem item) {
/*  975 */       this.toolBarManager.addToolBarItem(item);
/*      */     }
/*      */     
/*      */     public void removeToolBarItem(String id) {
/*  979 */       this.toolBarManager.removeToolBarItem(id);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void detach()
/*      */       throws UIException
/*      */     {
/*  987 */       this.delegate.detach();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getUIType()
/*      */     {
/*  993 */       return this.delegate.getUIType();
/*      */     }
/*      */     
/*      */ 
/*      */     public Display getDisplay()
/*      */     {
/*  999 */       return this.delegate.getDisplay();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Image loadImage(String resource)
/*      */     {
/* 1006 */       PluginInterface pi = (PluginInterface)this.pi_ref.get();
/*      */       
/* 1008 */       if (pi == null)
/*      */       {
/* 1010 */         return null;
/*      */       }
/*      */       
/* 1013 */       InputStream is = pi.getPluginClassLoader().getResourceAsStream(resource);
/*      */       
/* 1015 */       if (is != null)
/*      */       {
/* 1017 */         ImageData imageData = new ImageData(is);
/*      */         try
/*      */         {
/* 1020 */           is.close();
/*      */         } catch (IOException e) {
/* 1022 */           Debug.out(e);
/*      */         }
/*      */         
/* 1025 */         Display display = getDisplay();
/*      */         
/* 1027 */         Image image = new Image(display, imageData);
/*      */         
/* 1029 */         image = Utils.adjustPXForDPI(display, image);
/*      */         
/* 1031 */         this.listDisposeOnUnload.add(image);
/*      */         
/* 1033 */         return image;
/*      */       }
/*      */       
/* 1036 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public UISWTGraphic createGraphic(Image img)
/*      */     {
/* 1043 */       return this.delegate.createGraphic(img);
/*      */     }
/*      */     
/*      */ 
/*      */     public void addView(String sParentID, String sViewID, UISWTViewEventListener l)
/*      */     {
/* 1049 */       PluginInterface pi = (PluginInterface)this.pi_ref.get();
/*      */       
/* 1051 */       this.delegate.addView(sParentID, sViewID, new UISWTViewEventListenerHolder(sViewID, l, pi));
/*      */     }
/*      */     
/*      */     public void addView(String sParentID, String sViewID, Class<? extends UISWTViewEventListener> cla, Object datasource)
/*      */     {
/* 1056 */       PluginInterface pi = (PluginInterface)this.pi_ref.get();
/*      */       
/* 1058 */       this.delegate.addView(sParentID, sViewID, new UISWTViewEventListenerHolder(sViewID, cla, datasource, pi));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void openMainView(String sViewID, UISWTViewEventListener l, Object dataSource)
/*      */     {
/* 1065 */       PluginInterface pi = (PluginInterface)this.pi_ref.get();
/*      */       
/* 1067 */       this.delegate.openMainView(pi, sViewID, l, dataSource);
/*      */     }
/*      */     
/*      */ 
/*      */     public void openMainView(String sViewID, UISWTViewEventListener l, Object dataSource, boolean setfocus)
/*      */     {
/* 1073 */       PluginInterface pi = (PluginInterface)this.pi_ref.get();
/*      */       
/* 1075 */       this.delegate.openMainView(pi, sViewID, l, dataSource, setfocus);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeViews(String sParentID, String sViewID)
/*      */     {
/* 1082 */       this.delegate.removeViews(sParentID, sViewID);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public UISWTView[] getOpenViews(String sParentID)
/*      */     {
/* 1089 */       return this.delegate.getOpenViews(sParentID);
/*      */     }
/*      */     
/*      */     public int promptUser(String title, String text, String[] options, int defaultOption)
/*      */     {
/* 1094 */       return this.delegate.promptUser(title, text, options, defaultOption);
/*      */     }
/*      */     
/*      */     public boolean openView(String sParentID, String sViewID, Object dataSource) {
/* 1098 */       return this.delegate.openView(sParentID, sViewID, dataSource);
/*      */     }
/*      */     
/*      */     public boolean openView(String sParentID, String sViewID, Object dataSource, boolean setfocus) {
/* 1102 */       return this.delegate.openView(sParentID, sViewID, dataSource, setfocus);
/*      */     }
/*      */     
/*      */     public UISWTInstance.UISWTViewEventListenerWrapper[] getViewListeners(String sParentId) {
/* 1106 */       return this.delegate.getViewListeners(sParentId);
/*      */     }
/*      */     
/* 1109 */     public UIInputReceiver getInputReceiver() { return this.delegate.getInputReceiver(); }
/*      */     
/*      */     public UIMessage createMessage()
/*      */     {
/* 1113 */       return this.delegate.createMessage();
/*      */     }
/*      */     
/*      */     public void showDownloadBar(Download download, boolean display) {
/* 1117 */       this.delegate.showDownloadBar(download, display);
/*      */     }
/*      */     
/*      */     public void showTransfersBar(boolean display) {
/* 1121 */       this.delegate.showTransfersBar(display);
/*      */     }
/*      */     
/*      */     public UISWTStatusEntry createStatusEntry() {
/* 1125 */       return this.delegate.createStatusEntry();
/*      */     }
/*      */     
/*      */     public boolean openView(BasicPluginViewModel model) {
/* 1129 */       return this.delegate.openView(model);
/*      */     }
/*      */     
/*      */     public void openConfig(BasicPluginConfigModel model) {
/* 1133 */       this.delegate.openConfig(model);
/*      */     }
/*      */     
/*      */     public Shell createShell(int style) {
/* 1137 */       return this.delegate.createShell(style);
/*      */     }
/*      */     
/*      */ 
/*      */     public UIToolBarManager getToolBarManager()
/*      */     {
/* 1143 */       if (this.toolBarManager == null)
/*      */       {
/* 1145 */         UIToolBarManager tbm = this.ui_functions.getToolBarManager();
/*      */         
/* 1147 */         if ((tbm instanceof UIToolBarManagerCore))
/*      */         {
/* 1149 */           this.toolBarManager = ((UIToolBarManagerCore)tbm);
/*      */         }
/*      */         else
/*      */         {
/* 1153 */           return null;
/*      */         }
/*      */       }
/*      */       
/* 1157 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void unload(PluginInterface pi)
/*      */     {
/* 1164 */       if (this.toolBarManager != null)
/*      */       {
/* 1166 */         synchronized (this)
/*      */         {
/* 1168 */           for (UIToolBarItem item : this.listItems)
/*      */           {
/* 1170 */             this.toolBarManager.removeToolBarItem(item.getID());
/*      */           }
/*      */           
/* 1173 */           this.listItems.clear();
/*      */         }
/*      */       }
/*      */       
/* 1177 */       Utils.disposeSWTObjects(this.listDisposeOnUnload);
/*      */       
/* 1179 */       this.listDisposeOnUnload.clear();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unload(PluginInterface pi)
/*      */   {
/* 1187 */     throw new RuntimeException("plugin specific instance required");
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UISWTInstanceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */