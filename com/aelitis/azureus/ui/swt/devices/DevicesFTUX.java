/*     */ package com.aelitis.azureus.ui.swt.devices;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceManager;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerFactory;
/*     */ import com.aelitis.azureus.core.devices.TranscodeManager;
/*     */ import com.aelitis.azureus.core.messenger.config.PlatformDevicesMessenger;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.ConfigListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.DisplayListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.TorrentListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.VuzeListener;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Link;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
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
/*     */ public class DevicesFTUX
/*     */ {
/*     */   private static final String URL_LEARN_MORE = "/devices/qos.start";
/*     */   private static final String URL_DEVICES_INFO = "/devices/turnon.start";
/*     */   public static DevicesFTUX instance;
/*     */   Shell shell;
/*     */   private BrowserWrapper browser;
/*     */   private Button checkITunes;
/*     */   private Button btnInstall;
/*     */   private Button btnCancel;
/*     */   private Composite install_area;
/*     */   private Button checkQOS;
/*     */   private Composite install_area_parent;
/*  92 */   private List<Runnable> to_fire_on_complete = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean isDisposed()
/*     */   {
/* 100 */     return this.shell.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setFocus(Runnable fire_on_install)
/*     */   {
/* 110 */     synchronized (this.to_fire_on_complete)
/*     */     {
/* 112 */       this.to_fire_on_complete.add(fire_on_install);
/*     */     }
/* 114 */     this.shell.forceActive();
/* 115 */     this.shell.forceFocus();
/*     */   }
/*     */   
/*     */   private void open(Runnable fire_on_install)
/*     */   {
/* 120 */     synchronized (this.to_fire_on_complete)
/*     */     {
/* 122 */       this.to_fire_on_complete.add(fire_on_install);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 127 */     this.shell = ShellFactory.createMainShell(2144);
/* 128 */     this.shell.setText(MessageText.getString("devices.turnon.title"));
/*     */     
/* 130 */     Utils.setShellIcon(this.shell);
/*     */     try
/*     */     {
/* 133 */       this.browser = Utils.createSafeBrowser(this.shell, 0);
/* 134 */       if (this.browser != null) {
/* 135 */         BrowserContext context = new BrowserContext("DevicesFTUX", this.browser, null, true);
/*     */         
/* 137 */         context.addMessageListener(new TorrentListener());
/* 138 */         context.addMessageListener(new VuzeListener());
/* 139 */         context.addMessageListener(new DisplayListener(this.browser));
/* 140 */         context.addMessageListener(new ConfigListener(this.browser));
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {}
/*     */     
/* 145 */     Label lblInfo = new Label(this.shell, 64);
/* 146 */     Messages.setLanguageText(lblInfo, "devices.turnon.prepageload");
/*     */     
/* 148 */     this.checkITunes = new Button(this.shell, 32);
/* 149 */     this.checkITunes.setSelection(true);
/* 150 */     Messages.setLanguageText(this.checkITunes, "devices.turnon.itunes");
/*     */     
/* 152 */     PluginInterface itunes_plugin = null;
/*     */     try {
/* 154 */       itunes_plugin = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azitunes", true);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 159 */     if ((itunes_plugin != null) && (itunes_plugin.getPluginState().isOperational())) {
/* 160 */       this.checkITunes.setVisible(false);
/*     */     }
/*     */     
/*     */ 
/* 164 */     this.checkQOS = new Button(this.shell, 32);
/* 165 */     this.checkQOS.setSelection(true);
/* 166 */     Messages.setLanguageText(this.checkQOS, "devices.turnon.qos");
/*     */     
/* 168 */     Link lblLearnMore = new Link(this.shell, 0);
/* 169 */     lblLearnMore.setText("<A HREF=\"/devices/qos.start\">" + MessageText.getString("label.learnmore") + "</A>");
/*     */     
/* 171 */     lblLearnMore.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 173 */         String url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL(e.text, true);
/* 174 */         Utils.launch(url);
/*     */       }
/*     */       
/* 177 */     });
/* 178 */     this.btnInstall = new Button(this.shell, 0);
/* 179 */     Messages.setLanguageText(this.btnInstall, "Button.turnon");
/* 180 */     this.btnInstall.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 182 */         boolean sendQOS = DevicesFTUX.this.checkQOS.getSelection();
/* 183 */         DevicesFTUX.this.doInstall(DevicesFTUX.this.checkITunes.getSelection(), sendQOS);
/*     */       }
/*     */       
/* 186 */     });
/* 187 */     this.shell.setDefaultButton(this.btnInstall);
/*     */     
/* 189 */     this.btnCancel = new Button(this.shell, 0);
/* 190 */     Messages.setLanguageText(this.btnCancel, "Button.cancel");
/* 191 */     this.btnCancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 193 */         DevicesFTUX.this.shell.dispose();
/*     */       }
/*     */       
/* 196 */     });
/* 197 */     this.shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 199 */         if (e.detail == 2) {
/* 200 */           DevicesFTUX.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/* 204 */     });
/* 205 */     this.install_area_parent = new Composite(this.shell, 0);
/* 206 */     this.install_area_parent.setLayout(new FormLayout());
/* 207 */     this.install_area_parent.setVisible(false);
/*     */     
/* 209 */     this.install_area = new Composite(this.install_area_parent, 0);
/*     */     
/* 211 */     FormLayout formLayout = new FormLayout();
/* 212 */     formLayout.marginWidth = (formLayout.marginHeight = 0);
/* 213 */     this.shell.setLayout(formLayout);
/*     */     
/*     */ 
/* 216 */     FormData fd = Utils.getFilledFormData();
/* 217 */     fd.bottom = new FormAttachment(this.checkITunes, -5);
/* 218 */     fd.top = new FormAttachment(0, 8);
/* 219 */     fd.left = new FormAttachment(0, 8);
/* 220 */     fd.right = new FormAttachment(100, -8);
/* 221 */     lblInfo.setLayoutData(fd);
/*     */     
/* 223 */     fd = Utils.getFilledFormData();
/* 224 */     fd.bottom = new FormAttachment(this.checkITunes, -5);
/* 225 */     fd.width = 550;
/* 226 */     fd.height = 490;
/* 227 */     this.browser.setLayoutData(fd);
/*     */     
/* 229 */     fd = new FormData();
/* 230 */     fd.bottom = new FormAttachment(100, -10);
/* 231 */     fd.right = new FormAttachment(100, -10);
/* 232 */     this.btnCancel.setLayoutData(fd);
/*     */     
/* 234 */     fd = new FormData();
/* 235 */     fd.bottom = new FormAttachment(100, -10);
/* 236 */     fd.right = new FormAttachment(this.btnCancel, -12);
/* 237 */     this.btnInstall.setLayoutData(fd);
/*     */     
/* 239 */     fd = new FormData();
/* 240 */     fd.bottom = new FormAttachment(this.checkQOS, -3);
/* 241 */     fd.left = new FormAttachment(0, 10);
/* 242 */     fd.right = new FormAttachment(this.btnInstall, -12);
/* 243 */     this.checkITunes.setLayoutData(fd);
/*     */     
/* 245 */     fd = new FormData();
/* 246 */     fd.bottom = new FormAttachment(100, -5);
/* 247 */     fd.left = new FormAttachment(0, 10);
/* 248 */     this.checkQOS.setLayoutData(fd);
/*     */     
/* 250 */     fd = new FormData();
/* 251 */     fd.top = new FormAttachment(this.checkQOS, 0, 16777216);
/* 252 */     fd.left = new FormAttachment(this.checkQOS, 5);
/* 253 */     lblLearnMore.setLayoutData(fd);
/*     */     
/* 255 */     fd = new FormData();
/* 256 */     fd.top = new FormAttachment(this.browser.getControl(), 0);
/* 257 */     fd.bottom = new FormAttachment(100, 0);
/* 258 */     fd.left = new FormAttachment(0, 0);
/* 259 */     fd.right = new FormAttachment(100, 0);
/* 260 */     this.install_area_parent.setLayoutData(fd);
/*     */     
/* 262 */     fd = new FormData();
/* 263 */     fd.height = this.btnInstall.computeSize(-1, -1).y;
/* 264 */     fd.bottom = new FormAttachment(100, -5);
/* 265 */     fd.left = new FormAttachment(0, 5);
/* 266 */     fd.right = new FormAttachment(100, -12);
/* 267 */     this.install_area.setLayoutData(fd);
/*     */     
/* 269 */     String url = ConstantsVuze.getDefaultContentNetwork().getSiteRelativeURL("/devices/turnon.start", true);
/*     */     
/* 271 */     this.browser.setUrl(url);
/*     */     
/* 273 */     this.shell.pack();
/* 274 */     Utils.centreWindow(this.shell);
/*     */     
/* 276 */     this.btnInstall.setFocus();
/* 277 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void doInstall(final boolean itunes, final boolean sendQOS)
/*     */   {
/* 287 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 289 */         DevicesFTUX.this._doInstall(core, itunes, sendQOS);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void _doInstall(AzureusCore core, boolean itunes, boolean sendQOS) {
/* 295 */     qosTurnOn(sendQOS, itunes, false);
/*     */     
/*     */ 
/* 298 */     List<InstallablePlugin> plugins = new ArrayList(2);
/*     */     
/* 300 */     PluginInstaller installer = core.getPluginManager().getPluginInstaller();
/*     */     
/* 302 */     StandardPlugin vuze_plugin = null;
/*     */     try
/*     */     {
/* 305 */       vuze_plugin = installer.getStandardPlugin("vuzexcode");
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 310 */     if ((vuze_plugin != null) && (!vuze_plugin.isAlreadyInstalled())) {
/* 311 */       plugins.add(vuze_plugin);
/*     */     }
/*     */     
/* 314 */     if (itunes) {
/* 315 */       StandardPlugin itunes_plugin = null;
/*     */       try
/*     */       {
/* 318 */         itunes_plugin = installer.getStandardPlugin("azitunes");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 323 */       if ((itunes_plugin != null) && (!itunes_plugin.isAlreadyInstalled())) {
/* 324 */         plugins.add(itunes_plugin);
/*     */       }
/*     */     }
/*     */     
/* 328 */     if (plugins.size() == 0) {
/* 329 */       close();
/* 330 */       return;
/*     */     }
/* 332 */     InstallablePlugin[] installablePlugins = (InstallablePlugin[])plugins.toArray(new InstallablePlugin[0]);
/*     */     try
/*     */     {
/* 335 */       this.install_area_parent.setVisible(true);
/* 336 */       this.install_area_parent.moveAbove(null);
/*     */       
/* 338 */       Map<Integer, Object> properties = new HashMap();
/*     */       
/* 340 */       properties.put(Integer.valueOf(1), Integer.valueOf(2));
/*     */       
/*     */ 
/* 343 */       properties.put(Integer.valueOf(2), this.install_area);
/*     */       
/*     */ 
/* 346 */       properties.put(Integer.valueOf(3), Boolean.valueOf(true));
/*     */       
/* 348 */       installer.install(installablePlugins, false, properties, new PluginInstallationListener()
/*     */       {
/*     */         public void completed() {
/* 351 */           DevicesFTUX.this.close();
/*     */           
/* 353 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 354 */           MdiEntry entry = mdi.getEntry("header.devices");
/* 355 */           MdiEntryVitalityImage[] vitalityImages = entry.getVitalityImages();
/* 356 */           for (MdiEntryVitalityImage vi : vitalityImages) {
/* 357 */             if (vi.getImageID().contains("turnon")) {
/* 358 */               vi.setVisible(false);
/*     */             }
/*     */           }
/*     */           
/*     */           List<Runnable> to_fire;
/*     */           
/* 364 */           synchronized (DevicesFTUX.this.to_fire_on_complete)
/*     */           {
/* 366 */             to_fire = new ArrayList(DevicesFTUX.this.to_fire_on_complete);
/*     */             
/* 368 */             DevicesFTUX.this.to_fire_on_complete.clear();
/*     */           }
/*     */           
/* 371 */           for (Runnable r : to_fire)
/*     */           {
/* 373 */             if (r != null) {
/*     */               try
/*     */               {
/* 376 */                 Utils.execSWTThread(r);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 380 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         public void cancelled()
/*     */         {
/* 388 */           DevicesFTUX.this.close();
/*     */         }
/*     */         
/*     */         public void failed(PluginException e)
/*     */         {
/* 393 */           Debug.out(e);
/*     */           
/*     */ 
/* 396 */           DevicesFTUX.this.close();
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 402 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void qosTurnOn(boolean on, boolean itunes, boolean isBugFix) {
/* 407 */     COConfigurationManager.setParameter("devices.sendQOS", on);
/*     */     
/*     */ 
/* 410 */     if (!on) {
/* 411 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 415 */       PlatformDevicesMessenger.qosTurnOn(itunes, isBugFix);
/*     */     }
/*     */     catch (Throwable ignore) {}
/*     */     try
/*     */     {
/* 420 */       DeviceManager device_manager = DeviceManagerFactory.getSingleton();
/* 421 */       Device[] devices = device_manager.getDevices();
/* 422 */       for (Device device : devices) {
/*     */         try {
/* 424 */           PlatformDevicesMessenger.qosFoundDevice(device);
/*     */         }
/*     */         catch (Throwable ignore) {}
/*     */       }
/*     */     }
/*     */     catch (Throwable ignore) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void close()
/*     */   {
/* 438 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 440 */         if ((DevicesFTUX.this.shell != null) && (!DevicesFTUX.this.shell.isDisposed())) {
/* 441 */           DevicesFTUX.this.shell.dispose();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean ensureInstalled(Runnable fire_on_install)
/*     */   {
/* 453 */     DeviceManager device_manager = DeviceManagerFactory.getSingleton();
/*     */     
/* 455 */     if (device_manager.getTranscodeManager().getProviders().length == 0) {
/* 456 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 458 */           if ((DevicesFTUX.instance == null) || (DevicesFTUX.instance.isDisposed())) {
/* 459 */             DevicesFTUX.instance = new DevicesFTUX();
/* 460 */             DevicesFTUX.instance.open(this.val$fire_on_install);
/*     */           } else {
/* 462 */             DevicesFTUX.instance.setFocus(this.val$fire_on_install);
/*     */           }
/*     */         }
/* 465 */       });
/* 466 */       return false;
/*     */     }
/* 468 */     return true;
/*     */   }
/*     */   
/*     */   public static void showForDebug() {
/* 472 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 474 */         if ((DevicesFTUX.instance == null) || (DevicesFTUX.instance.isDisposed())) {
/* 475 */           DevicesFTUX.instance = new DevicesFTUX();
/* 476 */           DevicesFTUX.instance.open(null);
/*     */         } else {
/* 478 */           DevicesFTUX.instance.setFocus(null);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void alreadyInstalledFixup() {
/* 485 */     if (!COConfigurationManager.hasParameter("devices.sendQOS", true))
/*     */     {
/*     */ 
/*     */ 
/* 489 */       PluginInterface itunes_plugin = null;
/*     */       try {
/* 491 */         itunes_plugin = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azitunes", true);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 496 */       boolean hasItunes = (itunes_plugin != null) && (itunes_plugin.getPluginState().isOperational());
/* 497 */       qosTurnOn(true, hasItunes, true);
/* 498 */       return;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/DevicesFTUX.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */