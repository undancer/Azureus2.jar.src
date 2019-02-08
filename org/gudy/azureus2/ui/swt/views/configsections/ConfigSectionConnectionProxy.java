/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSocksProxy;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.GenericActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*     */ public class ConfigSectionConnectionProxy
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.proxy.";
/*     */   private static final int REQUIRED_MODE = 2;
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  53 */     return 2;
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  58 */     return "server";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  62 */     return "proxy";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */ 
/*     */   public void configSectionDelete() {}
/*     */   
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  75 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  77 */     GridData gridData = new GridData(272);
/*     */     
/*  79 */     Utils.setLayoutData(cSection, gridData);
/*  80 */     GridLayout layout = new GridLayout();
/*  81 */     layout.numColumns = 2;
/*  82 */     cSection.setLayout(layout);
/*     */     
/*  84 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  85 */     if (userMode < 2) {
/*  86 */       Label label = new Label(cSection, 64);
/*  87 */       gridData = new GridData();
/*  88 */       gridData.horizontalSpan = 2;
/*  89 */       Utils.setLayoutData(label, gridData);
/*     */       
/*  91 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/*  96 */       if (2 < modeKeys.length) {
/*  97 */         param1 = MessageText.getString(modeKeys[2]);
/*     */       } else
/*  99 */         param1 = String.valueOf(2);
/*     */       String param2;
/* 101 */       String param2; if (userMode < modeKeys.length) {
/* 102 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/* 104 */         param2 = String.valueOf(userMode);
/*     */       }
/* 106 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/* 109 */       return cSection;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 114 */     Group gProxyTracker = new Group(cSection, 0);
/* 115 */     Messages.setLanguageText(gProxyTracker, "ConfigView.section.proxy.group.tracker");
/* 116 */     gridData = new GridData(768);
/* 117 */     gridData.horizontalSpan = 2;
/* 118 */     Utils.setLayoutData(gProxyTracker, gridData);
/* 119 */     layout = new GridLayout();
/* 120 */     layout.numColumns = 2;
/* 121 */     gProxyTracker.setLayout(layout);
/*     */     
/* 123 */     final BooleanParameter enableProxy = new BooleanParameter(gProxyTracker, "Enable.Proxy", "ConfigView.section.proxy.enable_proxy");
/*     */     
/* 125 */     gridData = new GridData();
/* 126 */     gridData.horizontalSpan = 2;
/* 127 */     enableProxy.setLayoutData(gridData);
/*     */     
/* 129 */     final BooleanParameter enableSocks = new BooleanParameter(gProxyTracker, "Enable.SOCKS", "ConfigView.section.proxy.enable_socks");
/*     */     
/* 131 */     gridData = new GridData();
/* 132 */     gridData.horizontalSpan = 2;
/* 133 */     enableSocks.setLayoutData(gridData);
/*     */     
/* 135 */     Label lHost = new Label(gProxyTracker, 0);
/* 136 */     Messages.setLanguageText(lHost, "ConfigView.section.proxy.host");
/* 137 */     final StringParameter pHost = new StringParameter(gProxyTracker, "Proxy.Host", "", false);
/* 138 */     gridData = new GridData();
/* 139 */     gridData.widthHint = 105;
/* 140 */     pHost.setLayoutData(gridData);
/*     */     
/* 142 */     Label lPort = new Label(gProxyTracker, 0);
/* 143 */     Messages.setLanguageText(lPort, "ConfigView.section.proxy.port");
/* 144 */     final StringParameter pPort = new StringParameter(gProxyTracker, "Proxy.Port", "", false);
/* 145 */     gridData = new GridData();
/* 146 */     gridData.widthHint = 40;
/* 147 */     pPort.setLayoutData(gridData);
/*     */     
/* 149 */     Label lUser = new Label(gProxyTracker, 0);
/* 150 */     Messages.setLanguageText(lUser, "ConfigView.section.proxy.username");
/* 151 */     final StringParameter pUser = new StringParameter(gProxyTracker, "Proxy.Username", false);
/* 152 */     gridData = new GridData();
/* 153 */     gridData.widthHint = 105;
/* 154 */     pUser.setLayoutData(gridData);
/*     */     
/* 156 */     Label lPass = new Label(gProxyTracker, 0);
/* 157 */     Messages.setLanguageText(lPass, "ConfigView.section.proxy.password");
/* 158 */     final StringParameter pPass = new StringParameter(gProxyTracker, "Proxy.Password", "", false);
/* 159 */     gridData = new GridData();
/* 160 */     gridData.widthHint = 105;
/* 161 */     pPass.setLayoutData(gridData);
/*     */     
/* 163 */     final BooleanParameter trackerDNSKill = new BooleanParameter(gProxyTracker, "Proxy.SOCKS.Tracker.DNS.Disable", "ConfigView.section.proxy.no.local.dns");
/*     */     
/* 165 */     gridData = new GridData();
/* 166 */     gridData.horizontalSpan = 2;
/* 167 */     trackerDNSKill.setLayoutData(gridData);
/*     */     
/* 169 */     final NetworkAdminSocksProxy[] test_proxy = { null };
/*     */     
/* 171 */     final Button test_socks = new Button(gProxyTracker, 8);
/* 172 */     Messages.setLanguageText(test_socks, "ConfigView.section.proxy.testsocks");
/*     */     
/* 174 */     test_socks.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */         final NetworkAdminSocksProxy target;
/* 179 */         synchronized (test_proxy)
/*     */         {
/* 181 */           target = test_proxy[0];
/*     */         }
/*     */         
/* 184 */         if (target != null)
/*     */         {
/* 186 */           final TextViewerWindow viewer = new TextViewerWindow(MessageText.getString("ConfigView.section.proxy.testsocks.title"), null, "Testing SOCKS connection to " + target.getHost() + ":" + target.getPort(), false);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 191 */           final AESemaphore test_done = new AESemaphore("");
/*     */           
/* 193 */           new AEThread2("SOCKS test")
/*     */           {
/*     */             public void run()
/*     */             {
/*     */               try
/*     */               {
/* 199 */                 String[] vers = target.getVersionsSupported();
/*     */                 
/* 201 */                 String ver = "";
/*     */                 
/* 203 */                 for (String v : vers)
/*     */                 {
/* 205 */                   ver = ver + (ver.length() == 0 ? "" : ", ") + v;
/*     */                 }
/*     */                 
/* 208 */                 ConfigSectionConnectionProxy.1.this.appendText(viewer, "\r\nConnection OK - supported version(s): " + ver);
/*     */ 
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 213 */                 ConfigSectionConnectionProxy.1.this.appendText(viewer, "\r\n" + Debug.getNestedExceptionMessage(e));
/*     */               }
/*     */               finally
/*     */               {
/* 217 */                 test_done.release();
/*     */               }
/*     */               
/*     */             }
/* 221 */           }.start();
/* 222 */           new AEThread2("SOCKS test dotter")
/*     */           {
/*     */             public void run()
/*     */             {
/*     */               for (;;) {
/* 227 */                 if (!test_done.reserveIfAvailable())
/*     */                 {
/* 229 */                   ConfigSectionConnectionProxy.1.this.appendText(viewer, ".");
/*     */                   try
/*     */                   {
/* 232 */                     Thread.sleep(500L);
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */               }
/*     */             }
/*     */           }.start();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       private void appendText(final TextViewerWindow viewer, final String line)
/*     */       {
/* 249 */         Utils.execSWTThread(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 255 */             if (!viewer.isDisposed())
/*     */             {
/* 257 */               viewer.append2(line);
/*     */             }
/*     */             
/*     */           }
/*     */         });
/*     */       }
/* 263 */     });
/* 264 */     Parameter[] socks_params = { enableProxy, enableSocks, pHost, pPort, pUser, pPass, trackerDNSKill };
/*     */     
/* 266 */     ParameterChangeAdapter socks_adapter = new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 274 */         if (test_socks.isDisposed())
/*     */         {
/* 276 */           p.removeChangeListener(this);
/*     */ 
/*     */         }
/* 279 */         else if (!caused_internally)
/*     */         {
/* 281 */           boolean enabled = (enableProxy.isSelected().booleanValue()) && (enableSocks.isSelected().booleanValue()) && (pHost.getValue().trim().length() > 0) && (pPort.getValue().trim().length() > 0);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 287 */           boolean socks_enabled = (enableProxy.isSelected().booleanValue()) && (enableSocks.isSelected().booleanValue());
/*     */           
/*     */ 
/*     */ 
/* 291 */           trackerDNSKill.setEnabled(socks_enabled);
/*     */           
/* 293 */           if (enabled) {
/*     */             try
/*     */             {
/* 296 */               int port = Integer.parseInt(pPort.getValue());
/*     */               
/* 298 */               NetworkAdminSocksProxy nasp = NetworkAdmin.getSingleton().createSocksProxy(pHost.getValue(), port, pUser.getValue(), pPass.getValue());
/*     */               
/*     */ 
/*     */ 
/* 302 */               synchronized (test_proxy)
/*     */               {
/* 304 */                 test_proxy[0] = nasp;
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 308 */               enabled = false;
/*     */             }
/*     */           }
/*     */           
/* 312 */           if (!enabled)
/*     */           {
/* 314 */             synchronized (test_proxy)
/*     */             {
/* 316 */               test_proxy[0] = null;
/*     */             }
/*     */           }
/*     */           
/* 320 */           final boolean f_enabled = enabled;
/*     */           
/* 322 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 328 */               if (!ConfigSectionConnectionProxy.2.this.val$test_socks.isDisposed())
/*     */               {
/* 330 */                 ConfigSectionConnectionProxy.2.this.val$test_socks.setEnabled(f_enabled);
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/* 340 */     for (Parameter p : socks_params)
/*     */     {
/* 342 */       p.addChangeListener(socks_adapter);
/*     */     }
/*     */     
/*     */ 
/* 346 */     socks_adapter.parameterChanged(null, false);
/*     */     
/*     */ 
/*     */ 
/* 350 */     Group gProxyPeer = new Group(cSection, 0);
/* 351 */     Messages.setLanguageText(gProxyPeer, "ConfigView.section.proxy.group.peer");
/* 352 */     gridData = new GridData(768);
/* 353 */     gridData.horizontalSpan = 2;
/* 354 */     Utils.setLayoutData(gProxyPeer, gridData);
/* 355 */     layout = new GridLayout();
/* 356 */     layout.numColumns = 2;
/* 357 */     gProxyPeer.setLayout(layout);
/*     */     
/* 359 */     final BooleanParameter enableSocksPeer = new BooleanParameter(gProxyPeer, "Proxy.Data.Enable", "ConfigView.section.proxy.enable_socks.peer");
/*     */     
/* 361 */     gridData = new GridData();
/* 362 */     gridData.horizontalSpan = 2;
/* 363 */     enableSocksPeer.setLayoutData(gridData);
/*     */     
/* 365 */     BooleanParameter socksPeerInform = new BooleanParameter(gProxyPeer, "Proxy.Data.SOCKS.inform", "ConfigView.section.proxy.peer.informtracker");
/*     */     
/* 367 */     gridData = new GridData();
/* 368 */     gridData.horizontalSpan = 2;
/* 369 */     socksPeerInform.setLayoutData(gridData);
/*     */     
/* 371 */     Label lSocksVersion = new Label(gProxyPeer, 0);
/* 372 */     Messages.setLanguageText(lSocksVersion, "ConfigView.section.proxy.socks.version");
/* 373 */     String[] socks_types = { "V4", "V4a", "V5" };
/* 374 */     String[] dropLabels = new String[socks_types.length];
/* 375 */     String[] dropValues = new String[socks_types.length];
/* 376 */     for (int i = 0; i < socks_types.length; i++) {
/* 377 */       dropLabels[i] = socks_types[i];
/* 378 */       dropValues[i] = socks_types[i];
/*     */     }
/* 380 */     StringListParameter socksType = new StringListParameter(gProxyPeer, "Proxy.Data.SOCKS.version", "V4", dropLabels, dropValues);
/*     */     
/*     */ 
/* 383 */     final BooleanParameter sameConfig = new BooleanParameter(gProxyPeer, "Proxy.Data.Same", "ConfigView.section.proxy.peer.same");
/*     */     
/* 385 */     gridData = new GridData();
/* 386 */     gridData.horizontalSpan = 2;
/* 387 */     sameConfig.setLayoutData(gridData);
/*     */     
/* 389 */     Label lDataHost = new Label(gProxyPeer, 0);
/* 390 */     Messages.setLanguageText(lDataHost, "ConfigView.section.proxy.host");
/* 391 */     StringParameter pDataHost = new StringParameter(gProxyPeer, "Proxy.Data.Host", "");
/*     */     
/* 393 */     gridData = new GridData();
/* 394 */     gridData.widthHint = 105;
/* 395 */     pDataHost.setLayoutData(gridData);
/*     */     
/* 397 */     Label lDataPort = new Label(gProxyPeer, 0);
/* 398 */     Messages.setLanguageText(lDataPort, "ConfigView.section.proxy.port");
/* 399 */     StringParameter pDataPort = new StringParameter(gProxyPeer, "Proxy.Data.Port", "");
/*     */     
/* 401 */     gridData = new GridData();
/* 402 */     gridData.widthHint = 40;
/* 403 */     pDataPort.setLayoutData(gridData);
/*     */     
/* 405 */     Label lDataUser = new Label(gProxyPeer, 0);
/* 406 */     Messages.setLanguageText(lDataUser, "ConfigView.section.proxy.username");
/* 407 */     StringParameter pDataUser = new StringParameter(gProxyPeer, "Proxy.Data.Username");
/*     */     
/* 409 */     gridData = new GridData();
/* 410 */     gridData.widthHint = 105;
/* 411 */     pDataUser.setLayoutData(gridData);
/*     */     
/* 413 */     Label lDataPass = new Label(gProxyPeer, 0);
/* 414 */     Messages.setLanguageText(lDataPass, "ConfigView.section.proxy.password");
/* 415 */     StringParameter pDataPass = new StringParameter(gProxyPeer, "Proxy.Data.Password", "");
/*     */     
/* 417 */     gridData = new GridData();
/* 418 */     gridData.widthHint = 105;
/* 419 */     pDataPass.setLayoutData(gridData);
/*     */     
/* 421 */     final Control[] proxy_controls = { enableSocks.getControl(), lHost, pHost.getControl(), lPort, pPort.getControl(), lUser, pUser.getControl(), lPass, pPass.getControl() };
/*     */     
/*     */ 
/*     */ 
/* 425 */     IAdditionalActionPerformer proxy_enabler = new GenericActionPerformer(new Control[0])
/*     */     {
/*     */       public void performAction() {
/* 428 */         for (int i = 0; i < proxy_controls.length; i++)
/*     */         {
/* 430 */           proxy_controls[i].setEnabled(enableProxy.isSelected().booleanValue());
/*     */         }
/*     */         
/*     */       }
/* 434 */     };
/* 435 */     final Control[] proxy_peer_controls = { lDataHost, pDataHost.getControl(), lDataPort, pDataPort.getControl(), lDataUser, pDataUser.getControl(), lDataPass, pDataPass.getControl() };
/*     */     
/*     */ 
/*     */ 
/* 439 */     final Control[] proxy_peer_details = { sameConfig.getControl(), socksPeerInform.getControl(), socksType.getControl(), lSocksVersion };
/*     */     
/*     */ 
/*     */ 
/* 443 */     IAdditionalActionPerformer proxy_peer_enabler = new GenericActionPerformer(new Control[0])
/*     */     {
/*     */       public void performAction() {
/* 446 */         for (int i = 0; i < proxy_peer_controls.length; i++)
/*     */         {
/* 448 */           proxy_peer_controls[i].setEnabled((enableSocksPeer.isSelected().booleanValue()) && (!sameConfig.isSelected().booleanValue()));
/*     */         }
/*     */         
/*     */ 
/* 452 */         for (int i = 0; i < proxy_peer_details.length; i++)
/*     */         {
/* 454 */           proxy_peer_details[i].setEnabled(enableSocksPeer.isSelected().booleanValue());
/*     */         }
/*     */         
/*     */       }
/* 458 */     };
/* 459 */     enableSocks.setAdditionalActionPerformer(proxy_enabler);
/* 460 */     enableProxy.setAdditionalActionPerformer(proxy_enabler);
/* 461 */     enableSocksPeer.setAdditionalActionPerformer(proxy_peer_enabler);
/* 462 */     sameConfig.setAdditionalActionPerformer(proxy_peer_enabler);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 467 */     Label label = new Label(cSection, 64);
/* 468 */     Messages.setLanguageText(label, "ConfigView.section.proxy.dns.info");
/* 469 */     gridData = new GridData(768);
/* 470 */     gridData.horizontalSpan = 2;
/* 471 */     gridData.widthHint = 200;
/* 472 */     Utils.setLayoutData(label, gridData);
/*     */     
/*     */ 
/*     */ 
/* 476 */     BooleanParameter disablepps = new BooleanParameter(cSection, "Proxy.SOCKS.disable.plugin.proxies", "ConfigView.section.proxy.disable.plugin.proxies");
/*     */     
/* 478 */     gridData = new GridData();
/* 479 */     gridData.horizontalSpan = 2;
/* 480 */     disablepps.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 485 */     BooleanParameter checkOnStart = new BooleanParameter(cSection, "Proxy.Check.On.Start", "ConfigView.section.proxy.check.on.start");
/*     */     
/* 487 */     gridData = new GridData();
/* 488 */     gridData.horizontalSpan = 2;
/* 489 */     checkOnStart.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 493 */     BooleanParameter showIcon = new BooleanParameter(cSection, "Proxy.SOCKS.ShowIcon", "ConfigView.section.proxy.show_icon");
/*     */     
/* 495 */     gridData = new GridData();
/* 496 */     gridData.horizontalSpan = 2;
/* 497 */     showIcon.setLayoutData(gridData);
/*     */     
/* 499 */     BooleanParameter flagIncoming = new BooleanParameter(cSection, "Proxy.SOCKS.ShowIcon.FlagIncoming", "ConfigView.section.proxy.show_icon.flag.incoming");
/*     */     
/* 501 */     gridData = new GridData();
/* 502 */     gridData.horizontalIndent = 50;
/* 503 */     gridData.horizontalSpan = 2;
/* 504 */     flagIncoming.setLayoutData(gridData);
/*     */     
/* 506 */     showIcon.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(flagIncoming));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 511 */     label = new Label(cSection, 64);
/* 512 */     gridData = new GridData();
/* 513 */     gridData.horizontalSpan = 2;
/* 514 */     Utils.setLayoutData(label, gridData);
/* 515 */     label.setText(MessageText.getString("ConfigView.section.proxy.username.info"));
/*     */     
/* 517 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionConnectionProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */