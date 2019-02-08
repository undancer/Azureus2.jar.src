/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.DirectoryParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.FileParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.InfoParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringAreaParameter;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionInterfaceDisplay
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String MSG_PREFIX = "ConfigView.section.style.";
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  57 */     return "style";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  61 */     return "display";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  71 */     return 2;
/*     */   }
/*     */   
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  76 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  77 */     boolean isAZ3 = COConfigurationManager.getStringParameter("ui").equals("az3");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  82 */     Composite cSection = new Composite(parent, 0);
/*  83 */     Utils.setLayoutData(cSection, new GridData(1808));
/*  84 */     GridLayout layout = new GridLayout();
/*  85 */     layout.numColumns = 1;
/*  86 */     cSection.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  90 */     Group gVarious = new Group(cSection, 0);
/*  91 */     layout = new GridLayout();
/*  92 */     layout.numColumns = 1;
/*  93 */     gVarious.setLayout(layout);
/*  94 */     Utils.setLayoutData(gVarious, new GridData(768));
/*     */     
/*  96 */     gVarious.setText(MessageText.getString("label.various"));
/*     */     
/*     */ 
/*  99 */     new BooleanParameter(gVarious, "Show Download Basket", "ConfigView.section.style.showdownloadbasket");
/*     */     
/*     */ 
/* 102 */     if (!isAZ3) {
/* 103 */       new BooleanParameter(gVarious, "IconBar.enabled", "ConfigView.section.style.showiconbar");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 109 */     new BooleanParameter(gVarious, "suppress_file_download_dialog", "ConfigView.section.interface.display.suppress.file.download.dialog");
/*     */     
/* 111 */     new BooleanParameter(gVarious, "Suppress Sharing Dialog", "ConfigView.section.interface.display.suppress.sharing.dialog");
/*     */     
/* 113 */     new BooleanParameter(gVarious, "show_torrents_menu", "Menu.show.torrent.menu");
/*     */     
/* 115 */     if (!Constants.isLinux)
/*     */     {
/* 117 */       new BooleanParameter(gVarious, "mainwindow.search.history.enabled", "search.history.enable");
/*     */     }
/*     */     
/* 120 */     if (Constants.isWindowsXP) {
/* 121 */       final Button enableXPStyle = new Button(gVarious, 32);
/* 122 */       Messages.setLanguageText(enableXPStyle, "ConfigView.section.style.enableXPStyle");
/*     */       
/* 124 */       boolean enabled = false;
/* 125 */       boolean valid = false;
/*     */       try {
/* 127 */         File f = new File(System.getProperty("java.home") + "\\bin\\javaw.exe.manifest");
/*     */         
/* 129 */         if (f.exists()) {
/* 130 */           enabled = true;
/*     */         }
/* 132 */         f = FileUtil.getApplicationFile("javaw.exe.manifest");
/* 133 */         if (f.exists()) {
/* 134 */           valid = true;
/*     */         }
/*     */       } catch (Exception e) {
/* 137 */         Debug.printStackTrace(e);
/* 138 */         valid = false;
/*     */       }
/* 140 */       enableXPStyle.setEnabled(valid);
/* 141 */       enableXPStyle.setSelection(enabled);
/* 142 */       enableXPStyle.addListener(13, new Listener()
/*     */       {
/*     */         public void handleEvent(Event arg0) {
/* 145 */           if (enableXPStyle.getSelection()) {
/*     */             try {
/* 147 */               File fDest = new File(System.getProperty("java.home") + "\\bin\\javaw.exe.manifest");
/*     */               
/* 149 */               File fOrigin = new File("javaw.exe.manifest");
/* 150 */               if ((!fDest.exists()) && (fOrigin.exists())) {
/* 151 */                 FileUtil.copyFile(fOrigin, fDest);
/*     */               }
/*     */             } catch (Exception e) {
/* 154 */               Debug.printStackTrace(e);
/*     */             }
/*     */           } else {
/*     */             try {
/* 158 */               File fDest = new File(System.getProperty("java.home") + "\\bin\\javaw.exe.manifest");
/*     */               
/* 160 */               fDest.delete();
/*     */             } catch (Exception e) {
/* 162 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 169 */     if (Constants.isOSX) {
/* 170 */       new BooleanParameter(gVarious, "enable_small_osx_fonts", "ConfigView.section.style.osx_small_fonts");
/*     */     }
/*     */     
/*     */ 
/* 174 */     if (PlatformManagerFactory.getPlatformManager().hasCapability(PlatformManagerCapabilities.ShowFileInBrowser)) {
/* 175 */       BooleanParameter bp = new BooleanParameter(gVarious, "MyTorrentsView.menu.show_parent_folder_enabled", "ConfigView.section.style.use_show_parent_folder");
/*     */       
/* 177 */       Messages.setLanguageText(bp.getControl(), "ConfigView.section.style.use_show_parent_folder", new String[] { MessageText.getString("MyTorrentsView.menu.open_parent_folder"), MessageText.getString("MyTorrentsView.menu.explore") });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 182 */       if (Constants.isOSX) {
/* 183 */         new BooleanParameter(gVarious, "FileBrowse.usePathFinder", "ConfigView.section.style.usePathFinder");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 188 */     if (userMode > 0) {
/* 189 */       BooleanParameter paramEnableForceDPI = new BooleanParameter(gVarious, "enable.ui.forceDPI", "ConfigView.section.style.forceDPI");
/*     */       
/* 191 */       paramEnableForceDPI.setLayoutData(new GridData());
/* 192 */       IntParameter forceDPI = new IntParameter(gVarious, "Force DPI", 0, Integer.MAX_VALUE);
/*     */       
/* 194 */       forceDPI.setLayoutData(new GridData());
/* 195 */       paramEnableForceDPI.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(forceDPI.getControl()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */     if (isAZ3)
/*     */     {
/* 205 */       Group gSideBar = new Group(cSection, 0);
/* 206 */       Messages.setLanguageText(gSideBar, "v3.MainWindow.menu.view.sidebar");
/* 207 */       layout = new GridLayout();
/* 208 */       layout.numColumns = 2;
/* 209 */       gSideBar.setLayout(layout);
/* 210 */       Utils.setLayoutData(gSideBar, new GridData(768));
/*     */       
/* 212 */       new BooleanParameter(gSideBar, "Show Side Bar", "sidebar.show");
/* 213 */       Label label = new Label(gSideBar, 0);
/*     */       
/* 215 */       label = new Label(gSideBar, 0);
/* 216 */       Messages.setLanguageText(label, "sidebar.top.level.gap");
/*     */       
/* 218 */       new IntParameter(gSideBar, "Side Bar Top Level Gap", 0, 5);
/*     */       
/* 220 */       new BooleanParameter(gSideBar, "Show Options In Side Bar", "sidebar.show.options");
/* 221 */       label = new Label(gSideBar, 0);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 227 */     Group cStatusBar = new Group(cSection, 0);
/* 228 */     Messages.setLanguageText(cStatusBar, "ConfigView.section.style.status");
/* 229 */     layout = new GridLayout();
/* 230 */     layout.numColumns = 1;
/* 231 */     cStatusBar.setLayout(layout);
/* 232 */     Utils.setLayoutData(cStatusBar, new GridData(768));
/*     */     
/* 234 */     new BooleanParameter(cStatusBar, "Status Area Show SR", "ConfigView.section.style.status.show_sr");
/* 235 */     new BooleanParameter(cStatusBar, "Status Area Show NAT", "ConfigView.section.style.status.show_nat");
/* 236 */     new BooleanParameter(cStatusBar, "Status Area Show DDB", "ConfigView.section.style.status.show_ddb");
/* 237 */     new BooleanParameter(cStatusBar, "Status Area Show IPF", "ConfigView.section.style.status.show_ipf");
/* 238 */     new BooleanParameter(cStatusBar, "status.rategraphs", "ConfigView.section.style.status.show_rategraphs");
/*     */     
/*     */ 
/*     */ 
/* 242 */     if (userMode > 0) {
/* 243 */       Group cUnits = new Group(cSection, 0);
/* 244 */       Messages.setLanguageText(cUnits, "ConfigView.section.style.units");
/* 245 */       layout = new GridLayout();
/* 246 */       layout.numColumns = 1;
/* 247 */       cUnits.setLayout(layout);
/* 248 */       Utils.setLayoutData(cUnits, new GridData(768));
/*     */       
/* 250 */       new BooleanParameter(cUnits, "config.style.useSIUnits", "ConfigView.section.style.useSIUnits");
/*     */       
/*     */ 
/* 253 */       new BooleanParameter(cUnits, "config.style.forceSIValues", "ConfigView.section.style.forceSIValues");
/*     */       
/*     */ 
/* 256 */       new BooleanParameter(cUnits, "config.style.useUnitsRateBits", "ConfigView.section.style.useUnitsRateBits");
/*     */       
/*     */ 
/* 259 */       new BooleanParameter(cUnits, "config.style.doNotUseGB", "ConfigView.section.style.doNotUseGB");
/*     */       
/*     */ 
/* 262 */       new BooleanParameter(cUnits, "config.style.dataStatsOnly", "ConfigView.section.style.dataStatsOnly");
/*     */       
/*     */ 
/* 265 */       new BooleanParameter(cUnits, "config.style.separateProtDataStats", "ConfigView.section.style.separateProtDataStats");
/*     */       
/*     */ 
/* 268 */       new BooleanParameter(cUnits, "ui.scaled.graphics.binary.based", "ConfigView.section.style.scaleBinary");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 273 */     if (userMode > 0) {
/* 274 */       Group formatters_group = new Group(cSection, 0);
/* 275 */       Messages.setLanguageText(formatters_group, "ConfigView.label.general.formatters");
/* 276 */       layout = new GridLayout();
/* 277 */       formatters_group.setLayout(layout);
/* 278 */       Utils.setLayoutData(formatters_group, new GridData(768));
/* 279 */       StringAreaParameter formatters = new StringAreaParameter(formatters_group, "config.style.formatOverrides");
/* 280 */       GridData gridData = new GridData(768);
/* 281 */       gridData.heightHint = formatters.getPreferredHeight(3);
/* 282 */       formatters.setLayoutData(gridData);
/*     */       
/* 284 */       Composite format_info = new Composite(formatters_group, 0);
/* 285 */       layout = new GridLayout();
/* 286 */       layout.marginHeight = 0;
/* 287 */       layout.marginWidth = 0;
/* 288 */       layout.numColumns = 3;
/* 289 */       format_info.setLayout(layout);
/* 290 */       Utils.setLayoutData(format_info, new GridData(768));
/*     */       
/* 292 */       new LinkLabel(format_info, "ConfigView.label.general.formatters.link", MessageText.getString("ConfigView.label.general.formatters.link.url"));
/*     */       
/* 294 */       Label label = new Label(format_info, 0);
/* 295 */       Messages.setLanguageText(label, "GeneralView.label.status");
/*     */       
/* 297 */       InfoParameter info_param = new InfoParameter(format_info, "config.style.formatOverrides.status");
/* 298 */       gridData = new GridData(768);
/*     */       
/* 300 */       info_param.setLayoutData(gridData);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 305 */     if (userMode > 0)
/*     */     {
/* 307 */       Group gExternalBrowser = new Group(cSection, 0);
/* 308 */       layout = new GridLayout();
/* 309 */       layout.numColumns = 1;
/* 310 */       gExternalBrowser.setLayout(layout);
/* 311 */       Utils.setLayoutData(gExternalBrowser, new GridData(768));
/*     */       
/* 313 */       gExternalBrowser.setText(MessageText.getString("config.external.browser"));
/* 314 */       Label label = new Label(gExternalBrowser, 64);
/* 315 */       Messages.setLanguageText(label, "config.external.browser.info1");
/* 316 */       Utils.setLayoutData(label, Utils.getWrappableLabelGridData(1, 0));
/* 317 */       label = new Label(gExternalBrowser, 64);
/* 318 */       Messages.setLanguageText(label, "config.external.browser.info2");
/* 319 */       Utils.setLayoutData(label, Utils.getWrappableLabelGridData(1, 0));
/*     */       
/*     */ 
/*     */ 
/* 323 */       final List<String[]> browser_choices = new ArrayList();
/*     */       
/* 325 */       browser_choices.add(new String[] { "system", MessageText.getString("external.browser.system") });
/*     */       
/* 327 */       browser_choices.add(new String[] { "manual", MessageText.getString("external.browser.manual") });
/*     */       
/*     */ 
/* 330 */       List<PluginInterface> pis = AzureusCoreFactory.getSingleton().getPluginManager().getPluginsWithMethod("launchURL", new Class[] { URL.class, Boolean.TYPE, Runnable.class });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 335 */       String pi_names = "";
/*     */       
/* 337 */       for (PluginInterface pi : pis)
/*     */       {
/* 339 */         String pi_name = pi.getPluginName();
/*     */         
/* 341 */         pi_names = pi_names + (pi_names.length() == 0 ? "" : "/") + pi_name;
/*     */         
/* 343 */         browser_choices.add(new String[] { "plugin:" + pi.getPluginID(), pi_name });
/*     */       }
/*     */       
/*     */ 
/* 347 */       Composite cEBArea = new Composite(gExternalBrowser, 64);
/* 348 */       GridData gridData = new GridData(768);
/* 349 */       Utils.setLayoutData(cEBArea, gridData);
/* 350 */       layout = new GridLayout();
/* 351 */       layout.numColumns = 2;
/* 352 */       layout.marginHeight = 0;
/* 353 */       cEBArea.setLayout(layout);
/*     */       
/* 355 */       label = new Label(cEBArea, 64);
/* 356 */       Messages.setLanguageText(label, "config.external.browser.select");
/*     */       
/* 358 */       final Composite cEB = new Group(cEBArea, 64);
/* 359 */       gridData = new GridData(768);
/* 360 */       Utils.setLayoutData(cEB, gridData);
/* 361 */       layout = new GridLayout();
/* 362 */       layout.numColumns = browser_choices.size();
/* 363 */       layout.marginHeight = 0;
/* 364 */       cEB.setLayout(layout);
/*     */       
/* 366 */       List<Button> buttons = new ArrayList();
/*     */       
/* 368 */       for (int i = 0; i < browser_choices.size(); i++) {
/* 369 */         Button button = new Button(cEB, 16);
/* 370 */         button.setText(((String[])browser_choices.get(i))[1]);
/* 371 */         button.setData("index", String.valueOf(i));
/*     */         
/* 373 */         buttons.add(button);
/*     */       }
/*     */       
/* 376 */       String existing = COConfigurationManager.getStringParameter("browser.external.id", ((String[])browser_choices.get(0))[0]);
/*     */       
/* 378 */       int existing_index = -1;
/*     */       
/* 380 */       for (int i = 0; i < browser_choices.size(); i++)
/*     */       {
/* 382 */         if (((String[])browser_choices.get(i))[0].equals(existing))
/*     */         {
/* 384 */           existing_index = i;
/*     */           
/* 386 */           break;
/*     */         }
/*     */       }
/*     */       
/* 390 */       if (existing_index == -1)
/*     */       {
/* 392 */         existing_index = 0;
/*     */         
/* 394 */         COConfigurationManager.setParameter("browser.external.id", ((String[])browser_choices.get(0))[0]);
/*     */       }
/*     */       
/* 397 */       ((Button)buttons.get(existing_index)).setSelection(true);
/*     */       
/* 399 */       Messages.setLanguageText(new Label(cEBArea, 64), "config.external.browser.prog");
/*     */       
/* 401 */       Composite manualArea = new Composite(cEBArea, 0);
/* 402 */       layout = new GridLayout(2, false);
/* 403 */       layout.marginHeight = 0;
/* 404 */       layout.marginWidth = 0;
/* 405 */       manualArea.setLayout(layout);
/* 406 */       Utils.setLayoutData(manualArea, new GridData(768));
/*     */       
/* 408 */       final Parameter manualProg = new FileParameter(manualArea, "browser.external.prog", "", new String[0]);
/*     */       
/* 410 */       manualProg.setEnabled(existing_index == 1);
/*     */       
/* 412 */       Listener radioListener = new Listener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 419 */           Button button = (Button)event.widget;
/*     */           
/* 421 */           if (button.getSelection()) {
/* 422 */             Control[] children = cEB.getChildren();
/*     */             
/* 424 */             for (int j = 0; j < children.length; j++) {
/* 425 */               Control child = children[j];
/* 426 */               if ((child != button) && ((child instanceof Button))) {
/* 427 */                 Button b = (Button)child;
/*     */                 
/* 429 */                 b.setSelection(false);
/*     */               }
/*     */             }
/*     */             
/* 433 */             int index = Integer.parseInt((String)button.getData("index"));
/*     */             
/* 435 */             COConfigurationManager.setParameter("browser.external.id", ((String[])browser_choices.get(index))[0]);
/*     */             
/* 437 */             manualProg.setEnabled(index == 1);
/*     */           }
/*     */         }
/*     */       };
/*     */       
/* 442 */       for (Button b : buttons)
/*     */       {
/* 444 */         b.addListener(13, radioListener);
/*     */       }
/*     */       
/*     */       BooleanParameter non_pub;
/*     */       
/* 449 */       if (pis.size() > 0)
/*     */       {
/* 451 */         Composite nonPubArea = new Composite(gExternalBrowser, 0);
/* 452 */         layout = new GridLayout(2, false);
/* 453 */         layout.marginHeight = 0;
/* 454 */         nonPubArea.setLayout(layout);
/* 455 */         Utils.setLayoutData(nonPubArea, new GridData(768));
/*     */         
/* 457 */         String temp = MessageText.getString("config.external.browser.non.pub", new String[] { pi_names });
/*     */         
/* 459 */         non_pub = new BooleanParameter(nonPubArea, "browser.external.non.pub", true, "!" + temp + "!");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 464 */       Composite testArea = new Composite(gExternalBrowser, 0);
/* 465 */       layout = new GridLayout(4, false);
/* 466 */       layout.marginHeight = 0;
/* 467 */       testArea.setLayout(layout);
/* 468 */       Utils.setLayoutData(testArea, new GridData(768));
/*     */       
/* 470 */       label = new Label(testArea, 64);
/* 471 */       Messages.setLanguageText(label, "config.external.browser.test");
/*     */       
/* 473 */       final Button test_button = new Button(testArea, 8);
/*     */       
/* 475 */       Messages.setLanguageText(test_button, "configureWizard.nat.test");
/*     */       
/* 477 */       final Text test_url = new Text(testArea, 2048);
/*     */       
/* 479 */       Utils.setLayoutData(test_url, new GridData(768));
/*     */       
/* 481 */       test_url.setText("http://www.vuze.com/");
/*     */       
/* 483 */       test_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 489 */           test_button.setEnabled(false);
/*     */           
/* 491 */           final String url_str = test_url.getText().trim();
/*     */           
/* 493 */           new AEThread2("async")
/*     */           {
/*     */             public void run()
/*     */             {
/*     */               try
/*     */               {
/* 499 */                 Utils.launch(url_str, true);
/*     */               }
/*     */               finally
/*     */               {
/* 503 */                 Utils.execSWTThread(new Runnable()
/*     */                 {
/*     */ 
/*     */                   public void run()
/*     */                   {
/*     */ 
/* 509 */                     if (!ConfigSectionInterfaceDisplay.3.this.val$test_button.isDisposed())
/*     */                     {
/* 511 */                       ConfigSectionInterfaceDisplay.3.this.val$test_button.setEnabled(true);
/*     */                     }
/*     */                     
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/*     */           }.start();
/*     */         }
/* 520 */       });
/* 521 */       label = new Label(testArea, 0);
/* 522 */       Utils.setLayoutData(label, new GridData(768));
/*     */       
/*     */ 
/*     */ 
/* 526 */       label = new Label(gExternalBrowser, 64);
/* 527 */       Messages.setLanguageText(label, "config.external.browser.switch.info");
/*     */       
/* 529 */       Group switchArea = new Group(gExternalBrowser, 0);
/* 530 */       layout = new GridLayout(3, false);
/*     */       
/*     */ 
/* 533 */       switchArea.setLayout(layout);
/* 534 */       Utils.setLayoutData(switchArea, new GridData(768));
/*     */       
/*     */ 
/*     */ 
/* 538 */       label = new Label(switchArea, 64);
/* 539 */       Messages.setLanguageText(label, "config.external.browser.switch.feature");
/* 540 */       label = new Label(switchArea, 64);
/* 541 */       Messages.setLanguageText(label, "config.external.browser.switch.external");
/* 542 */       label = new Label(switchArea, 64);
/* 543 */       gridData = new GridData(768);
/* 544 */       gridData.horizontalIndent = 10;
/* 545 */       Utils.setLayoutData(label, gridData);
/* 546 */       Messages.setLanguageText(label, "config.external.browser.switch.implic");
/*     */       
/*     */ 
/*     */ 
/* 550 */       label = new Label(switchArea, 64);
/* 551 */       gridData = new GridData();
/* 552 */       gridData.verticalIndent = 10;
/* 553 */       Utils.setLayoutData(label, gridData);
/* 554 */       Messages.setLanguageText(label, "config.external.browser.switch.search");
/*     */       
/* 556 */       BooleanParameter switchSearch = new BooleanParameter(switchArea, "browser.external.search");
/* 557 */       gridData = new GridData();
/* 558 */       gridData.verticalIndent = 10;
/* 559 */       gridData.horizontalAlignment = 16777216;
/* 560 */       switchSearch.setLayoutData(gridData);
/*     */       
/* 562 */       label = new Label(switchArea, 64);
/* 563 */       gridData = Utils.getWrappableLabelGridData(1, 768);
/* 564 */       gridData.verticalIndent = 10;
/* 565 */       gridData.horizontalIndent = 10;
/* 566 */       Utils.setLayoutData(label, gridData);
/* 567 */       Messages.setLanguageText(label, "config.external.browser.switch.search.inf");
/*     */       
/*     */ 
/*     */ 
/* 571 */       label = new Label(switchArea, 64);
/* 572 */       gridData = new GridData();
/* 573 */       Utils.setLayoutData(label, gridData);
/* 574 */       Messages.setLanguageText(label, "config.external.browser.switch.subs");
/*     */       
/* 576 */       BooleanParameter switchSubs = new BooleanParameter(switchArea, "browser.external.subs");
/* 577 */       gridData = new GridData();
/* 578 */       gridData.horizontalAlignment = 16777216;
/* 579 */       switchSubs.setLayoutData(gridData);
/*     */       
/* 581 */       label = new Label(switchArea, 64);
/* 582 */       gridData = Utils.getWrappableLabelGridData(1, 768);
/* 583 */       gridData.horizontalIndent = 10;
/* 584 */       Utils.setLayoutData(label, gridData);
/* 585 */       Messages.setLanguageText(label, "config.external.browser.switch.subs.inf");
/*     */     }
/*     */     
/*     */     BooleanParameter intbrow_disable;
/*     */     
/* 590 */     if (userMode > 1) {
/* 591 */       Group gInternalBrowser = new Group(cSection, 0);
/* 592 */       layout = new GridLayout();
/* 593 */       layout.numColumns = 1;
/* 594 */       gInternalBrowser.setLayout(layout);
/* 595 */       Utils.setLayoutData(gInternalBrowser, new GridData(768));
/*     */       
/* 597 */       gInternalBrowser.setText(MessageText.getString("config.internal.browser"));
/*     */       
/* 599 */       Label label = new Label(gInternalBrowser, 64);
/* 600 */       GridData gridData = Utils.getWrappableLabelGridData(1, 768);
/* 601 */       Utils.setLayoutData(label, gridData);
/* 602 */       Messages.setLanguageText(label, "config.internal.browser.info1");
/*     */       
/*     */ 
/* 605 */       intbrow_disable = new BooleanParameter(gInternalBrowser, "browser.internal.disable", "config.browser.internal.disable");
/* 606 */       label = new Label(gInternalBrowser, 64);
/* 607 */       gridData = Utils.getWrappableLabelGridData(1, 768);
/* 608 */       gridData.horizontalIndent = 15;
/* 609 */       Utils.setLayoutData(label, gridData);
/* 610 */       Messages.setLanguageText(label, "config.browser.internal.disable.info");
/*     */       
/* 612 */       label = new Label(gInternalBrowser, 64);
/* 613 */       gridData = Utils.getWrappableLabelGridData(1, 768);
/* 614 */       Utils.setLayoutData(label, gridData);
/* 615 */       Messages.setLanguageText(label, "config.internal.browser.info3");
/*     */       
/* 617 */       List<PluginInterface> pis = AEProxyFactory.getPluginHTTPProxyProviders(true);
/*     */       
/* 619 */       final List<String[]> proxy_choices = new ArrayList();
/*     */       
/* 621 */       proxy_choices.add(new String[] { "none", MessageText.getString("PeersView.uniquepiece.none") });
/*     */       
/*     */ 
/* 624 */       for (PluginInterface pi : pis)
/*     */       {
/* 626 */         proxy_choices.add(new String[] { "plugin:" + pi.getPluginID(), pi.getPluginName() });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 631 */       Composite cIPArea = new Composite(gInternalBrowser, 64);
/* 632 */       gridData = new GridData(768);
/* 633 */       Utils.setLayoutData(cIPArea, gridData);
/* 634 */       layout = new GridLayout();
/* 635 */       layout.numColumns = 2;
/* 636 */       layout.marginHeight = 0;
/* 637 */       cIPArea.setLayout(layout);
/*     */       
/* 639 */       label = new Label(cIPArea, 64);
/* 640 */       gridData = Utils.getWrappableLabelGridData(1, 768);
/* 641 */       Utils.setLayoutData(label, gridData);
/* 642 */       Messages.setLanguageText(label, "config.internal.browser.proxy.select");
/*     */       
/* 644 */       final Composite cIP = new Group(cIPArea, 64);
/* 645 */       gridData = new GridData(768);
/* 646 */       Utils.setLayoutData(cIP, gridData);
/* 647 */       layout = new GridLayout();
/* 648 */       layout.numColumns = proxy_choices.size();
/* 649 */       layout.marginHeight = 0;
/* 650 */       cIP.setLayout(layout);
/*     */       
/* 652 */       List<Button> buttons = new ArrayList();
/*     */       
/* 654 */       for (int i = 0; i < proxy_choices.size(); i++) {
/* 655 */         Button button = new Button(cIP, 16);
/* 656 */         button.setText(((String[])proxy_choices.get(i))[1]);
/* 657 */         button.setData("index", String.valueOf(i));
/*     */         
/* 659 */         buttons.add(button);
/*     */       }
/*     */       
/* 662 */       String existing = COConfigurationManager.getStringParameter("browser.internal.proxy.id", ((String[])proxy_choices.get(0))[0]);
/*     */       
/* 664 */       int existing_index = -1;
/*     */       
/* 666 */       for (int i = 0; i < proxy_choices.size(); i++)
/*     */       {
/* 668 */         if (((String[])proxy_choices.get(i))[0].equals(existing))
/*     */         {
/* 670 */           existing_index = i;
/*     */           
/* 672 */           break;
/*     */         }
/*     */       }
/*     */       
/* 676 */       if (existing_index == -1)
/*     */       {
/* 678 */         existing_index = 0;
/*     */         
/* 680 */         COConfigurationManager.setParameter("browser.internal.proxy.id", ((String[])proxy_choices.get(0))[0]);
/*     */       }
/*     */       
/* 683 */       ((Button)buttons.get(existing_index)).setSelection(true);
/*     */       
/*     */ 
/* 686 */       Listener radioListener = new Listener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 693 */           Button button = (Button)event.widget;
/*     */           
/* 695 */           if (button.getSelection()) {
/* 696 */             Control[] children = cIP.getChildren();
/*     */             
/* 698 */             for (int j = 0; j < children.length; j++) {
/* 699 */               Control child = children[j];
/* 700 */               if ((child != button) && ((child instanceof Button))) {
/* 701 */                 Button b = (Button)child;
/*     */                 
/* 703 */                 b.setSelection(false);
/*     */               }
/*     */             }
/*     */             
/* 707 */             int index = Integer.parseInt((String)button.getData("index"));
/*     */             
/* 709 */             COConfigurationManager.setParameter("browser.internal.proxy.id", ((String[])proxy_choices.get(index))[0]);
/*     */           }
/*     */         }
/*     */       };
/*     */       
/* 714 */       for (Button b : buttons)
/*     */       {
/* 716 */         b.addListener(13, radioListener);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 721 */       label = new Label(gInternalBrowser, 64);
/* 722 */       gridData = Utils.getWrappableLabelGridData(1, 768);
/* 723 */       Utils.setLayoutData(label, gridData);
/* 724 */       Messages.setLanguageText(label, "config.internal.browser.info2");
/*     */       
/*     */ 
/* 727 */       BooleanParameter fMoz = new BooleanParameter(gInternalBrowser, "swt.forceMozilla", "ConfigView.section.style.forceMozilla");
/* 728 */       Composite pArea = new Composite(gInternalBrowser, 0);
/* 729 */       pArea.setLayout(new GridLayout(3, false));
/* 730 */       Utils.setLayoutData(pArea, new GridData(768));
/* 731 */       Messages.setLanguageText(new Label(pArea, 64), "ConfigView.section.style.xulRunnerPath");
/* 732 */       Parameter xulDir = new DirectoryParameter(pArea, "swt.xulRunner.path", "");
/* 733 */       fMoz.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(xulDir.getControls(), false));
/*     */       
/* 735 */       intbrow_disable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(xulDir.getControls(), true));
/*     */       
/*     */ 
/*     */ 
/* 739 */       intbrow_disable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { fMoz.getControl() }, true));
/*     */       
/*     */ 
/*     */ 
/* 743 */       for (Button b : buttons) {
/* 744 */         intbrow_disable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { b }, true));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 752 */     Group gRefresh = new Group(cSection, 0);
/* 753 */     gRefresh.setText(MessageText.getString("upnp.refresh.button"));
/*     */     
/* 755 */     layout = new GridLayout();
/* 756 */     layout.numColumns = 2;
/* 757 */     gRefresh.setLayout(layout);
/* 758 */     Utils.setLayoutData(gRefresh, new GridData(768));
/*     */     
/* 760 */     Label label = new Label(gRefresh, 0);
/* 761 */     Messages.setLanguageText(label, "ConfigView.section.style.guiUpdate");
/* 762 */     int[] values = { 10, 25, 50, 100, 250, 500, 1000, 2000, 5000, 10000, 15000 };
/* 763 */     String[] labels = { "10 ms", "25 ms", "50 ms", "100 ms", "250 ms", "500 ms", "1 s", "2 s", "5 s", "10 s", "15 s" };
/* 764 */     new IntListParameter(gRefresh, "GUI Refresh", 1000, labels, values);
/*     */     
/* 766 */     label = new Label(gRefresh, 0);
/* 767 */     Messages.setLanguageText(label, "ConfigView.section.style.inactiveUpdate");
/* 768 */     GridData gridData = new GridData();
/* 769 */     IntParameter inactiveUpdate = new IntParameter(gRefresh, "Refresh When Inactive", 1, Integer.MAX_VALUE);
/* 770 */     inactiveUpdate.setLayoutData(gridData);
/*     */     
/* 772 */     label = new Label(gRefresh, 0);
/* 773 */     Messages.setLanguageText(label, "ConfigView.section.style.graphicsUpdate");
/* 774 */     gridData = new GridData();
/* 775 */     IntParameter graphicUpdate = new IntParameter(gRefresh, "Graphics Update", 1, Integer.MAX_VALUE);
/* 776 */     graphicUpdate.setLayoutData(gridData);
/*     */     
/* 778 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfaceDisplay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */