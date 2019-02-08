/*     */ package com.aelitis.azureus.ui.swt.devices;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.devices.DeviceManager;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerFactory;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeManager;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeviceInfoArea
/*     */   extends SkinView
/*     */ {
/*     */   private DeviceMediaRenderer device;
/*     */   private Composite main;
/*     */   private Composite parent;
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/*  65 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  66 */     if (mdi != null) {
/*  67 */       MdiEntrySWT entry = mdi.getEntryFromSkinObject(skinObject);
/*  68 */       if (entry != null) {
/*  69 */         this.device = ((DeviceMediaRenderer)entry.getDatasource());
/*     */       }
/*     */     }
/*     */     
/*  73 */     this.parent = ((Composite)skinObject.getControl());
/*     */     
/*  75 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/*  80 */     super.skinObjectShown(skinObject, params);
/*     */     
/*  82 */     if (this.device == null) {
/*  83 */       initDeviceOverview();
/*     */     } else {
/*  85 */       initDeviceView();
/*     */     }
/*  87 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/*  92 */     Utils.disposeComposite(this.main);
/*  93 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initDeviceView()
/*     */   {
/* 102 */     this.main = new Composite(this.parent, 0);
/* 103 */     GridLayout layout = new GridLayout();
/* 104 */     layout.numColumns = 1;
/* 105 */     layout.marginTop = 4;
/* 106 */     layout.marginBottom = 4;
/* 107 */     layout.marginHeight = 4;
/* 108 */     layout.marginWidth = 4;
/* 109 */     this.main.setLayout(layout);
/*     */     
/* 111 */     this.main.setLayoutData(Utils.getFilledFormData());
/*     */     
/*     */ 
/*     */ 
/* 115 */     Composite control = new Composite(this.main, 0);
/* 116 */     layout = new GridLayout();
/* 117 */     layout.numColumns = 3;
/* 118 */     layout.marginLeft = 0;
/* 119 */     control.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 123 */     GridData grid_data = new GridData(768);
/* 124 */     grid_data.horizontalSpan = 1;
/* 125 */     control.setLayoutData(grid_data);
/*     */     
/* 127 */     Label dir_lab = new Label(control, 0);
/* 128 */     dir_lab.setText("Local directory: " + this.device.getWorkingDirectory().getAbsolutePath());
/*     */     
/*     */ 
/* 131 */     Button show_folder_button = new Button(control, 8);
/*     */     
/* 133 */     Messages.setLanguageText(show_folder_button, "MyTorrentsView.menu.explore");
/*     */     
/* 135 */     show_folder_button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/*     */ 
/* 143 */         ManagerUtils.open(DeviceInfoArea.this.device.getWorkingDirectory());
/*     */       }
/*     */       
/* 146 */     });
/* 147 */     new Label(control, 0);
/*     */     
/* 149 */     if (this.device.canFilterFilesView())
/*     */     {
/* 151 */       final Button show_xcode_button = new Button(control, 32);
/*     */       
/* 153 */       Messages.setLanguageText(show_xcode_button, "devices.xcode.only.show");
/*     */       
/* 155 */       show_xcode_button.setSelection(this.device.getFilterFilesView());
/*     */       
/* 157 */       show_xcode_button.addSelectionListener(new SelectionAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void widgetSelected(SelectionEvent e)
/*     */         {
/*     */ 
/* 164 */           DeviceInfoArea.this.device.setFilterFilesView(show_xcode_button.getSelection());
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 169 */     final Button btnReset = new Button(this.main, 8);
/* 170 */     btnReset.setText("Forget Default Profile Choice");
/* 171 */     btnReset.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 173 */         DeviceInfoArea.this.device.setDefaultTranscodeProfile(null);
/* 174 */         btnReset.setEnabled(false);
/*     */       }
/*     */       
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/*     */     });
/*     */     try
/*     */     {
/* 181 */       btnReset.setEnabled(this.device.getDefaultTranscodeProfile() != null);
/*     */     } catch (TranscodeException e1) {
/* 183 */       btnReset.setEnabled(false);
/*     */     }
/* 185 */     btnReset.setLayoutData(new GridData());
/*     */     
/* 187 */     this.parent.getParent().layout();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void initDeviceOverview()
/*     */   {
/* 193 */     final PluginInstaller installer = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInstaller();
/* 194 */     boolean hasItunes = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azitunes") != null;
/*     */     
/*     */ 
/* 197 */     this.main = new Composite(this.parent, 0);
/* 198 */     this.main.setLayoutData(Utils.getFilledFormData());
/* 199 */     FormLayout layout = new FormLayout();
/* 200 */     layout.marginWidth = (layout.marginHeight = 5);
/* 201 */     this.main.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 207 */     if (hasItunes) {
/* 208 */       Button itunes_button = new Button(this.main, 0);
/* 209 */       Control top = itunes_button;
/*     */       
/* 211 */       itunes_button.setText("Install iTunes Integration");
/*     */       
/* 213 */       itunes_button.addListener(13, new Listener() {
/*     */         public void handleEvent(Event arg0) {
/*     */           try {
/* 216 */             StandardPlugin itunes_plugin = installer.getStandardPlugin("azitunes");
/*     */             
/* 218 */             if (itunes_plugin == null)
/*     */             {
/* 220 */               Debug.out("iTunes standard plugin not found");
/*     */             }
/*     */             else
/*     */             {
/* 224 */               itunes_plugin.install(false);
/*     */             }
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 229 */             Debug.printStackTrace(e);
/*     */           }
/*     */           
/*     */         }
/* 233 */       });
/* 234 */       FormData fd = new FormData();
/* 235 */       fd.left = new FormAttachment(0, 0);
/* 236 */       fd.top = new FormAttachment(0, 4);
/*     */       
/* 238 */       itunes_button.setLayoutData(fd);
/*     */     } else {
/* 240 */       Label lblItunesInstalled = new Label(this.main, 64);
/* 241 */       Control top = lblItunesInstalled;
/* 242 */       lblItunesInstalled.setText("iTunes support is available");
/*     */     }
/*     */     
/* 245 */     if (Constants.isCVSVersion()) {}
/*     */     
/*     */ 
/* 248 */     this.parent.getParent().layout();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void buildBetaArea(Composite parent, Control above)
/*     */   {
/* 255 */     Group betaArea = new Group(parent, 0);
/* 256 */     betaArea.setText("Beta Debug");
/* 257 */     betaArea.setLayout(new FormLayout());
/* 258 */     FormData fd = Utils.getFilledFormData();
/* 259 */     fd.top = new FormAttachment(above, 5);
/* 260 */     betaArea.setLayoutData(fd);
/*     */     
/* 262 */     fd = new FormData();
/* 263 */     fd.left = new FormAttachment(0, 0);
/* 264 */     fd.right = new FormAttachment(100, 0);
/* 265 */     fd.top = new FormAttachment(0, 0);
/*     */     
/* 267 */     Label label = new Label(betaArea, 0);
/*     */     
/* 269 */     label.setText("Transcode Providers:");
/*     */     
/* 271 */     label.setLayoutData(fd);
/*     */     
/* 273 */     Button vuze_button = new Button(betaArea, 0);
/*     */     
/* 275 */     vuze_button.setText("Install Vuze Transcoder");
/*     */     
/* 277 */     if (AzureusCoreFactory.isCoreRunning()) {
/* 278 */       PluginInstaller installer = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInstaller();
/*     */       
/* 280 */       StandardPlugin vuze_plugin = null;
/*     */       try
/*     */       {
/* 283 */         vuze_plugin = installer.getStandardPlugin("vuzexcode");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 288 */       if ((vuze_plugin == null) || (vuze_plugin.isAlreadyInstalled()))
/*     */       {
/* 290 */         vuze_button.setEnabled(false);
/*     */       }
/*     */       
/* 293 */       final StandardPlugin f_vuze_plugin = vuze_plugin;
/*     */       
/* 295 */       vuze_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event arg0)
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 304 */             f_vuze_plugin.install(false);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 308 */             Debug.printStackTrace(e);
/*     */           }
/*     */           
/*     */         }
/* 312 */       });
/* 313 */       fd = new FormData();
/* 314 */       fd.left = new FormAttachment(0, 0);
/* 315 */       fd.top = new FormAttachment(label, 4);
/*     */       
/* 317 */       vuze_button.setLayoutData(fd);
/*     */     }
/*     */     
/*     */ 
/* 321 */     Control top = vuze_button;
/*     */     
/*     */ 
/* 324 */     TranscodeProvider[] providers = DeviceManagerFactory.getSingleton().getTranscodeManager().getProviders();
/*     */     
/* 326 */     for (TranscodeProvider provider : providers)
/*     */     {
/* 328 */       fd = new FormData();
/* 329 */       fd.left = new FormAttachment(0, 10);
/* 330 */       fd.right = new FormAttachment(100, 0);
/* 331 */       fd.top = new FormAttachment(top, 4);
/*     */       
/* 333 */       Label prov_lab = new Label(betaArea, 0);
/*     */       
/* 335 */       prov_lab.setText(provider.getName());
/*     */       
/* 337 */       prov_lab.setLayoutData(fd);
/*     */       
/* 339 */       top = prov_lab;
/*     */       
/* 341 */       TranscodeProfile[] profiles = provider.getProfiles();
/*     */       
/* 343 */       String line = null;
/* 344 */       for (TranscodeProfile profile : profiles)
/*     */       {
/* 346 */         if (line == null) {
/* 347 */           line = profile.getName();
/*     */         } else {
/* 349 */           line = line + ", " + profile.getName();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 354 */       if (line != null) {
/* 355 */         fd = new FormData();
/* 356 */         fd.left = new FormAttachment(0, 25);
/* 357 */         fd.right = new FormAttachment(100, 0);
/* 358 */         fd.top = new FormAttachment(top, 4);
/*     */         
/* 360 */         Label prof_lab = new Label(betaArea, 64);
/*     */         
/* 362 */         prof_lab.setText("Profiles: " + line);
/*     */         
/* 364 */         prof_lab.setLayoutData(fd);
/*     */         
/* 366 */         top = prof_lab;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 372 */     final Button both_button = new Button(betaArea, 0);
/*     */     
/* 374 */     both_button.setText("Test! Install RSSGen and AZBlog!");
/*     */     
/*     */ 
/* 377 */     if (AzureusCoreFactory.isCoreRunning()) {
/* 378 */       final PluginInstaller installer = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInstaller();
/*     */       
/* 380 */       StandardPlugin plugin1 = null;
/* 381 */       StandardPlugin plugin2 = null;
/*     */       try
/*     */       {
/* 384 */         plugin1 = installer.getStandardPlugin("azrssgen");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */       try
/*     */       {
/* 390 */         plugin2 = installer.getStandardPlugin("azblog");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 395 */       if ((plugin1 != null) && (plugin2 != null))
/*     */       {
/* 397 */         final Composite install_area = new Composite(betaArea, 2048);
/*     */         
/* 399 */         fd = new FormData();
/* 400 */         fd.left = new FormAttachment(both_button, 0);
/* 401 */         fd.right = new FormAttachment(100, 0);
/* 402 */         fd.top = new FormAttachment(top, 4);
/* 403 */         fd.bottom = new FormAttachment(100, 0);
/*     */         
/* 405 */         install_area.setLayoutData(fd);
/*     */         
/* 407 */         final StandardPlugin f_plugin1 = plugin1;
/* 408 */         final StandardPlugin f_plugin2 = plugin2;
/*     */         
/* 410 */         both_button.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void handleEvent(Event arg0)
/*     */           {
/*     */ 
/*     */ 
/* 418 */             both_button.setEnabled(false);
/*     */             try
/*     */             {
/* 421 */               Map<Integer, Object> properties = new HashMap();
/*     */               
/* 423 */               properties.put(Integer.valueOf(1), Integer.valueOf(2));
/*     */               
/* 425 */               properties.put(Integer.valueOf(2), install_area);
/*     */               
/* 427 */               properties.put(Integer.valueOf(3), Boolean.valueOf(true));
/*     */               
/* 429 */               installer.install(new InstallablePlugin[] { f_plugin1, f_plugin2 }, false, properties, new PluginInstallationListener()
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void completed()
/*     */                 {
/*     */ 
/*     */ 
/* 438 */                   System.out.println("Install completed!");
/*     */                   
/* 440 */                   tidy();
/*     */                 }
/*     */                 
/*     */ 
/*     */                 public void cancelled()
/*     */                 {
/* 446 */                   System.out.println("Install cancelled");
/*     */                   
/* 448 */                   tidy();
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */                 public void failed(PluginException e)
/*     */                 {
/* 455 */                   System.out.println("Install failed: " + e);
/*     */                   
/* 457 */                   tidy();
/*     */                 }
/*     */                 
/*     */ 
/*     */                 protected void tidy()
/*     */                 {
/* 463 */                   Utils.execSWTThread(new Runnable()
/*     */                   {
/*     */ 
/*     */                     public void run()
/*     */                     {
/*     */ 
/* 469 */                       Control[] kids = DeviceInfoArea.6.this.val$install_area.getChildren();
/*     */                       
/* 471 */                       for (Control c : kids)
/*     */                       {
/* 473 */                         c.dispose();
/*     */                       }
/*     */                       
/* 476 */                       DeviceInfoArea.6.this.val$both_button.setEnabled(true);
/*     */                     }
/*     */                   });
/*     */                 }
/*     */               });
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 484 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       else {
/* 490 */         both_button.setEnabled(false);
/*     */       }
/*     */       
/* 493 */       fd = new FormData();
/* 494 */       fd.left = new FormAttachment(0, 0);
/* 495 */       fd.top = new FormAttachment(top, 4);
/* 496 */       fd.bottom = new FormAttachment(100, 0);
/*     */       
/* 498 */       both_button.setLayoutData(fd);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/DeviceInfoArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */