/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEJavaManagement;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.FileParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ 
/*     */ 
/*     */ public class ConfigSectionStartShutdown
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String LBLKEY_PREFIX = "ConfigView.label.";
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  58 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  62 */     return "startstop";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  72 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  82 */     final Composite cDisplay = new Composite(parent, 0);
/*     */     
/*  84 */     GridData gridData = new GridData(272);
/*     */     
/*  86 */     Utils.setLayoutData(cDisplay, gridData);
/*  87 */     GridLayout layout = new GridLayout();
/*  88 */     layout.numColumns = 1;
/*  89 */     layout.marginWidth = 0;
/*  90 */     layout.marginHeight = 0;
/*  91 */     cDisplay.setLayout(layout);
/*     */     
/*  93 */     final PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*     */     
/*  95 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*     */ 
/*     */ 
/*  99 */     boolean can_ral = platform.hasCapability(PlatformManagerCapabilities.RunAtLogin);
/*     */     
/* 101 */     if ((can_ral) || (userMode > 0))
/*     */     {
/* 103 */       Group gStartStop = new Group(cDisplay, 0);
/* 104 */       Messages.setLanguageText(gStartStop, "ConfigView.label.start");
/* 105 */       layout = new GridLayout(2, false);
/* 106 */       gStartStop.setLayout(layout);
/* 107 */       Utils.setLayoutData(gStartStop, new GridData(768));
/*     */       
/* 109 */       if (can_ral)
/*     */       {
/* 111 */         gridData = new GridData();
/* 112 */         gridData.horizontalSpan = 2;
/* 113 */         BooleanParameter start_on_login = new BooleanParameter(gStartStop, "Start On Login", "ConfigView.label.start.onlogin");
/*     */         try
/*     */         {
/* 116 */           start_on_login.setSelected(platform.getRunAtLogin());
/*     */           
/* 118 */           start_on_login.addChangeListener(new ParameterChangeAdapter()
/*     */           {
/*     */ 
/*     */             public void booleanParameterChanging(Parameter p, boolean toValue)
/*     */             {
/*     */ 
/*     */               try
/*     */               {
/*     */ 
/* 127 */                 platform.setRunAtLogin(toValue);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 131 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 138 */           start_on_login.setEnabled(false);
/*     */           
/* 140 */           Debug.out(e);
/*     */         }
/*     */         
/* 143 */         start_on_login.setLayoutData(gridData);
/*     */       }
/*     */       
/* 146 */       if (userMode > 0)
/*     */       {
/* 148 */         gridData = new GridData();
/* 149 */         gridData.horizontalSpan = 2;
/*     */         
/* 151 */         BooleanParameter start_in_lr_mode = new BooleanParameter(gStartStop, "Start In Low Resource Mode", "ConfigView.label.start.inlrm");
/*     */         
/* 153 */         start_in_lr_mode.setLayoutData(gridData);
/*     */       }
/*     */     }
/*     */     
/* 157 */     if (platform.hasCapability(PlatformManagerCapabilities.PreventComputerSleep))
/*     */     {
/* 159 */       Group gSleep = new Group(cDisplay, 0);
/* 160 */       Messages.setLanguageText(gSleep, "ConfigView.label.sleep");
/* 161 */       layout = new GridLayout(2, false);
/* 162 */       gSleep.setLayout(layout);
/* 163 */       Utils.setLayoutData(gSleep, new GridData(768));
/*     */       
/* 165 */       gridData = new GridData();
/* 166 */       gridData.horizontalSpan = 2;
/* 167 */       Label label = new Label(gSleep, 0);
/* 168 */       Messages.setLanguageText(label, "ConfigView.label.sleep.info");
/* 169 */       Utils.setLayoutData(label, gridData);
/*     */       
/* 171 */       gridData = new GridData();
/* 172 */       gridData.horizontalSpan = 2;
/* 173 */       BooleanParameter no_sleep_dl = new BooleanParameter(gSleep, "Prevent Sleep Downloading", "ConfigView.label.sleep.download");
/* 174 */       no_sleep_dl.setLayoutData(gridData);
/*     */       
/* 176 */       gridData = new GridData();
/* 177 */       gridData.horizontalSpan = 2;
/* 178 */       BooleanParameter no_sleep_se = new BooleanParameter(gSleep, "Prevent Sleep FP Seeding", "ConfigView.label.sleep.fpseed");
/* 179 */       no_sleep_se.setLayoutData(gridData);
/*     */     }
/*     */     
/* 182 */     if (userMode > 0)
/*     */     {
/* 184 */       Group gPR = new Group(cDisplay, 0);
/* 185 */       Messages.setLanguageText(gPR, "ConfigView.label.pauseresume");
/* 186 */       layout = new GridLayout(2, false);
/* 187 */       gPR.setLayout(layout);
/* 188 */       Utils.setLayoutData(gPR, new GridData(768));
/*     */       
/* 190 */       gridData = new GridData();
/* 191 */       gridData.horizontalSpan = 2;
/* 192 */       BooleanParameter pauseOnExit = new BooleanParameter(gPR, "Pause Downloads On Exit", "ConfigView.label.pause.downloads.on.exit");
/*     */       
/* 194 */       pauseOnExit.setLayoutData(gridData);
/*     */       
/* 196 */       gridData = new GridData();
/* 197 */       gridData.horizontalSpan = 2;
/* 198 */       BooleanParameter resumeOnStart = new BooleanParameter(gPR, "Resume Downloads On Start", "ConfigView.label.resume.downloads.on.start");
/*     */       
/* 200 */       resumeOnStart.setLayoutData(gridData);
/*     */     }
/*     */     
/* 203 */     if (userMode >= 0)
/*     */     {
/* 205 */       Group gStop = new Group(cDisplay, 0);
/* 206 */       Messages.setLanguageText(gStop, "ConfigView.label.stop");
/* 207 */       layout = new GridLayout(5, false);
/* 208 */       gStop.setLayout(layout);
/* 209 */       Utils.setLayoutData(gStop, new GridData(768));
/*     */       
/*     */ 
/*     */ 
/* 213 */       addDoneDownloadingOption(gStop, true);
/*     */       
/*     */ 
/*     */ 
/* 217 */       addDoneSeedingOption(gStop, true);
/*     */       
/*     */ 
/*     */ 
/* 221 */       gridData = new GridData();
/* 222 */       gridData.horizontalSpan = 2;
/* 223 */       BooleanParameter resetOnTrigger = new BooleanParameter(gStop, "Stop Triggers Auto Reset", "!" + MessageText.getString("ConfigView.label.stop.autoreset", new String[] { MessageText.getString("ConfigView.label.stop.Nothing") }) + "!");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 230 */       resetOnTrigger.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 234 */       gridData = new GridData();
/* 235 */       gridData.horizontalSpan = 2;
/* 236 */       BooleanParameter enablePrompt = new BooleanParameter(gStop, "Prompt To Abort Shutdown", "ConfigView.label.prompt.abort");
/* 237 */       enablePrompt.setLayoutData(gridData);
/*     */     }
/*     */     
/* 240 */     if (userMode > 0)
/*     */     {
/* 242 */       Group gRestart = new Group(cDisplay, 0);
/* 243 */       Messages.setLanguageText(gRestart, "label.restart");
/* 244 */       layout = new GridLayout(2, false);
/* 245 */       gRestart.setLayout(layout);
/* 246 */       Utils.setLayoutData(gRestart, new GridData(768));
/*     */       
/* 248 */       Label label = new Label(gRestart, 0);
/* 249 */       Messages.setLanguageText(label, "ConfigView.label.restart.auto");
/*     */       
/* 251 */       new IntParameter(gRestart, "Auto Restart When Idle", 0, 100000);
/*     */     }
/*     */     
/* 254 */     if ((userMode > 0) && (platform.hasCapability(PlatformManagerCapabilities.AccessExplicitVMOptions)))
/*     */     {
/* 256 */       Group gJVM = new Group(cDisplay, 0);
/* 257 */       Messages.setLanguageText(gJVM, "ConfigView.label.jvm");
/* 258 */       layout = new GridLayout(2, false);
/* 259 */       gJVM.setLayout(layout);
/* 260 */       Utils.setLayoutData(gJVM, new GridData(768));
/*     */       
/*     */ 
/*     */ 
/* 264 */       gridData = new GridData();
/* 265 */       gridData.horizontalSpan = 2;
/*     */       
/* 267 */       LinkLabel link = new LinkLabel(gJVM, gridData, "ConfigView.label.please.visit.here", "http://wiki.vuze.com/w/Java_VM_memory_usage");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 272 */       Label label = new Label(gJVM, 0);
/* 273 */       Messages.setLanguageText(label, "jvm.info");
/* 274 */       gridData = new GridData();
/* 275 */       gridData.horizontalSpan = 2;
/* 276 */       Utils.setLayoutData(label, gridData);
/*     */       try
/*     */       {
/* 279 */         final File option_file = platform.getVMOptionFile();
/*     */         
/* 281 */         final Group gJVMOptions = new Group(gJVM, 0);
/* 282 */         layout = new GridLayout(3, false);
/* 283 */         gJVMOptions.setLayout(layout);
/* 284 */         gridData = new GridData(768);
/* 285 */         gridData.horizontalSpan = 2;
/* 286 */         Utils.setLayoutData(gJVMOptions, gridData);
/*     */         
/* 288 */         buildOptions(cDisplay, platform, gJVMOptions, false);
/*     */         
/*     */ 
/*     */ 
/* 292 */         label = new Label(gJVM, 0);
/* 293 */         Messages.setLanguageText(label, "jvm.show.file", new String[] { option_file.getAbsolutePath() });
/*     */         
/* 295 */         Button show_folder_button = new Button(gJVM, 8);
/*     */         
/* 297 */         Messages.setLanguageText(show_folder_button, "MyTorrentsView.menu.explore");
/*     */         
/* 299 */         show_folder_button.addSelectionListener(new SelectionAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*     */ 
/* 306 */             ManagerUtils.open(option_file);
/*     */           }
/*     */           
/* 309 */         });
/* 310 */         label = new Label(gJVM, 0);
/* 311 */         Messages.setLanguageText(label, "jvm.reset");
/*     */         
/* 313 */         Button reset_button = new Button(gJVM, 8);
/*     */         
/* 315 */         Messages.setLanguageText(reset_button, "Button.reset");
/*     */         
/* 317 */         reset_button.addSelectionListener(new SelectionAdapter()
/*     */         {
/*     */ 
/*     */           public void widgetSelected(SelectionEvent event)
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/* 325 */               platform.setExplicitVMOptions(new String[0]);
/*     */               
/* 327 */               ConfigSectionStartShutdown.this.buildOptions(cDisplay, platform, gJVMOptions, true);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 331 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 338 */         Debug.out(e);
/*     */         
/* 340 */         label = new Label(gJVM, 0);
/* 341 */         Messages.setLanguageText(label, "jvm.error", new String[] { Debug.getNestedExceptionMessage(e) });
/* 342 */         gridData = new GridData();
/* 343 */         gridData.horizontalSpan = 2;
/* 344 */         Utils.setLayoutData(label, gridData);
/*     */       }
/*     */     }
/*     */     
/* 348 */     return cDisplay;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void buildOptions(final Composite parent, final PlatformManager platform, final Composite area, boolean rebuild)
/*     */     throws PlatformManagerException
/*     */   {
/* 360 */     if (rebuild)
/*     */     {
/* 362 */       Control[] kids = area.getChildren();
/*     */       
/* 364 */       for (Control k : kids) {
/* 365 */         k.dispose();
/*     */       }
/*     */     }
/*     */     
/* 369 */     String[] options = platform.getExplicitVMOptions();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 374 */     long max_mem = AEJavaManagement.getJVMLongOption(options, "-Xmx");
/*     */     
/* 376 */     int MIN_MAX_JVM = 33554432;
/*     */     
/* 378 */     GridData gridData = new GridData();
/* 379 */     Label label = new Label(area, 0);
/* 380 */     Utils.setLayoutData(label, gridData);
/* 381 */     Messages.setLanguageText(label, "jvm.max.mem", new String[] { encodeDisplayLong(33554432L) });
/*     */     
/* 383 */     gridData = new GridData();
/* 384 */     gridData.widthHint = 125;
/* 385 */     final StringParameter max_vm = new StringParameter(area, "jvm.max.mem", "", false);
/* 386 */     max_vm.setLayoutData(gridData);
/*     */     
/* 388 */     max_vm.setValue(max_mem == -1L ? "" : encodeDisplayLong(max_mem));
/*     */     
/* 390 */     max_vm.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */       private String last_value;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/* 400 */         if (max_vm.isDisposed())
/*     */         {
/* 402 */           max_vm.removeChangeListener(this);
/*     */           
/* 404 */           return;
/*     */         }
/*     */         
/* 407 */         String val = max_vm.getValue();
/*     */         
/* 409 */         if ((this.last_value != null) && (this.last_value.equals(val)))
/*     */         {
/* 411 */           return;
/*     */         }
/*     */         
/* 414 */         this.last_value = val;
/*     */         try
/*     */         {
/* 417 */           long max_mem = ConfigSectionStartShutdown.decodeDisplayLong(val);
/*     */           
/* 419 */           if (max_mem < 33554432L)
/*     */           {
/* 421 */             throw new Exception("Min=" + ConfigSectionStartShutdown.encodeDisplayLong(33554432L));
/*     */           }
/*     */           
/* 424 */           String[] options = platform.getExplicitVMOptions();
/*     */           
/* 426 */           options = AEJavaManagement.setJVMLongOption(options, "-Xmx", max_mem);
/*     */           
/* 428 */           long min_mem = AEJavaManagement.getJVMLongOption(options, "-Xms");
/*     */           
/* 430 */           if ((min_mem == -1L) || (min_mem > max_mem))
/*     */           {
/* 432 */             options = AEJavaManagement.setJVMLongOption(options, "-Xms", max_mem);
/*     */           }
/*     */           
/* 435 */           platform.setExplicitVMOptions(options);
/*     */           
/* 437 */           ConfigSectionStartShutdown.this.buildOptions(parent, platform, area, true);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 441 */           String param_name = MessageText.getString("jvm.max.mem");
/*     */           
/* 443 */           int pos = param_name.indexOf('[');
/*     */           
/* 445 */           if (pos != -1)
/*     */           {
/* 447 */             param_name = param_name.substring(0, pos).trim();
/*     */           }
/*     */           
/* 450 */           MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.invalid.value.title"), MessageText.getString("ConfigView.section.invalid.value", new String[] { val, param_name, Debug.getNestedExceptionMessage(e) }));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 458 */           mb.setParent(parent.getShell());
/* 459 */           mb.open(null);
/*     */         }
/*     */         
/*     */       }
/* 463 */     });
/* 464 */     label = new Label(area, 0);
/* 465 */     gridData = new GridData(768);
/* 466 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 468 */     Long max_heap_mb = Long.valueOf(AEJavaManagement.getMaxHeapMB());
/*     */     
/* 470 */     if (max_heap_mb.longValue() > 0L)
/*     */     {
/* 472 */       Messages.setLanguageText(label, "jvm.max.mem.current", new String[] { DisplayFormatters.formatByteCountToKiBEtc(max_heap_mb.longValue() * 1024L * 1024L, true) });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 479 */     int MIN_MIN_JVM = 8388608;
/*     */     
/* 481 */     long min_mem = AEJavaManagement.getJVMLongOption(options, "-Xms");
/*     */     
/* 483 */     GridData gridData = new GridData();
/* 484 */     Label label = new Label(area, 0);
/* 485 */     Utils.setLayoutData(label, gridData);
/* 486 */     Messages.setLanguageText(label, "jvm.min.mem", new String[] { encodeDisplayLong(8388608L) });
/*     */     
/* 488 */     gridData = new GridData();
/* 489 */     gridData.widthHint = 125;
/* 490 */     final StringParameter min_vm = new StringParameter(area, "jvm.min.mem", "", false);
/* 491 */     min_vm.setLayoutData(gridData);
/*     */     
/* 493 */     min_vm.setValue(min_mem == -1L ? "" : encodeDisplayLong(min_mem));
/*     */     
/* 495 */     min_vm.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */       private String last_value;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/* 505 */         if (min_vm.isDisposed())
/*     */         {
/* 507 */           min_vm.removeChangeListener(this);
/*     */           
/* 509 */           return;
/*     */         }
/*     */         
/* 512 */         String val = min_vm.getValue();
/*     */         
/* 514 */         if ((this.last_value != null) && (this.last_value.equals(val)))
/*     */         {
/* 516 */           return;
/*     */         }
/*     */         
/* 519 */         this.last_value = val;
/*     */         try
/*     */         {
/* 522 */           long min_mem = ConfigSectionStartShutdown.decodeDisplayLong(val);
/*     */           
/* 524 */           if (min_mem < 8388608L)
/*     */           {
/* 526 */             throw new Exception("Min=" + ConfigSectionStartShutdown.encodeDisplayLong(8388608L));
/*     */           }
/*     */           
/* 529 */           String[] options = platform.getExplicitVMOptions();
/*     */           
/* 531 */           options = AEJavaManagement.setJVMLongOption(options, "-Xms", min_mem);
/*     */           
/* 533 */           long max_mem = AEJavaManagement.getJVMLongOption(options, "-Xmx");
/*     */           
/* 535 */           if ((max_mem == -1L) || (max_mem < min_mem))
/*     */           {
/* 537 */             options = AEJavaManagement.setJVMLongOption(options, "-Xmx", min_mem);
/*     */           }
/*     */           
/* 540 */           platform.setExplicitVMOptions(options);
/*     */           
/* 542 */           ConfigSectionStartShutdown.this.buildOptions(parent, platform, area, true);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 546 */           String param_name = MessageText.getString("jvm.min.mem");
/*     */           
/* 548 */           int pos = param_name.indexOf('[');
/*     */           
/* 550 */           if (pos != -1)
/*     */           {
/* 552 */             param_name = param_name.substring(0, pos).trim();
/*     */           }
/*     */           
/* 555 */           MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.invalid.value.title"), MessageText.getString("ConfigView.section.invalid.value", new String[] { val, param_name, Debug.getNestedExceptionMessage(e) }));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 563 */           mb.setParent(parent.getShell());
/* 564 */           mb.open(null);
/*     */         }
/*     */         
/*     */       }
/* 568 */     });
/* 569 */     label = new Label(area, 0);
/* 570 */     gridData = new GridData(768);
/* 571 */     Utils.setLayoutData(label, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 577 */     int MIN_DIRECT_JVM = 33554432;
/*     */     
/* 579 */     String OPTION_KEY = "-XX:MaxDirectMemorySize=";
/*     */     
/* 581 */     long max_direct = AEJavaManagement.getJVMLongOption(options, "-XX:MaxDirectMemorySize=");
/*     */     
/* 583 */     GridData gridData = new GridData();
/* 584 */     Label label = new Label(area, 0);
/* 585 */     Utils.setLayoutData(label, gridData);
/* 586 */     Messages.setLanguageText(label, "jvm.max.direct.mem", new String[] { encodeDisplayLong(33554432L) });
/*     */     
/* 588 */     gridData = new GridData();
/* 589 */     gridData.widthHint = 125;
/* 590 */     final StringParameter max_direct_vm = new StringParameter(area, "jvm.max.direct.mem", "", false);
/* 591 */     max_direct_vm.setLayoutData(gridData);
/*     */     
/* 593 */     max_direct_vm.setValue(max_direct == -1L ? "" : encodeDisplayLong(max_direct));
/*     */     
/* 595 */     max_direct_vm.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */       private String last_value;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/* 605 */         if (max_direct_vm.isDisposed())
/*     */         {
/* 607 */           max_direct_vm.removeChangeListener(this);
/*     */           
/* 609 */           return;
/*     */         }
/*     */         
/* 612 */         String val = max_direct_vm.getValue();
/*     */         
/* 614 */         if ((this.last_value != null) && (this.last_value.equals(val)))
/*     */         {
/* 616 */           return;
/*     */         }
/*     */         
/* 619 */         this.last_value = val;
/*     */         try
/*     */         {
/* 622 */           long max_direct = ConfigSectionStartShutdown.decodeDisplayLong(val);
/*     */           
/* 624 */           if (max_direct < 33554432L)
/*     */           {
/* 626 */             throw new Exception("Min=" + ConfigSectionStartShutdown.encodeDisplayLong(33554432L));
/*     */           }
/*     */           
/* 629 */           String[] options = platform.getExplicitVMOptions();
/*     */           
/* 631 */           options = AEJavaManagement.setJVMLongOption(options, "-XX:MaxDirectMemorySize=", max_direct);
/*     */           
/* 633 */           platform.setExplicitVMOptions(options);
/*     */           
/* 635 */           ConfigSectionStartShutdown.this.buildOptions(parent, platform, area, true);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 639 */           String param_name = MessageText.getString("jvm.max.direct.mem");
/*     */           
/* 641 */           int pos = param_name.indexOf('[');
/*     */           
/* 643 */           if (pos != -1)
/*     */           {
/* 645 */             param_name = param_name.substring(0, pos).trim();
/*     */           }
/*     */           
/* 648 */           MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.invalid.value.title"), MessageText.getString("ConfigView.section.invalid.value", new String[] { val, param_name, Debug.getNestedExceptionMessage(e) }));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 656 */           mb.setParent(parent.getShell());
/* 657 */           mb.open(null);
/*     */         }
/*     */         
/*     */       }
/* 661 */     });
/* 662 */     label = new Label(area, 0);
/* 663 */     gridData = new GridData(768);
/* 664 */     Utils.setLayoutData(label, gridData);
/* 665 */     Messages.setLanguageText(label, "jvm.max.direct.mem.info");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 670 */     Label label = new Label(area, 0);
/* 671 */     GridData gridData = new GridData(768);
/* 672 */     gridData.horizontalSpan = 3;
/* 673 */     Utils.setLayoutData(label, gridData);
/* 674 */     Messages.setLanguageText(label, "jvm.options.summary");
/*     */     
/* 676 */     for (String option : options)
/*     */     {
/* 678 */       label = new Label(area, 0);
/* 679 */       label.setText(option);
/* 680 */       gridData = new GridData();
/* 681 */       gridData.horizontalSpan = 3;
/* 682 */       gridData.horizontalIndent = 20;
/* 683 */       Utils.setLayoutData(label, gridData);
/*     */     }
/*     */     
/* 686 */     if (rebuild)
/*     */     {
/* 688 */       parent.layout(true, true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String encodeDisplayLong(long val)
/*     */   {
/* 696 */     if (val < 1024L)
/*     */     {
/* 698 */       return String.valueOf(val);
/*     */     }
/*     */     
/* 701 */     val /= 1024L;
/*     */     
/* 703 */     if (val < 1024L)
/*     */     {
/* 705 */       return String.valueOf(val) + " KB";
/*     */     }
/*     */     
/* 708 */     val /= 1024L;
/*     */     
/* 710 */     if (val < 1024L)
/*     */     {
/* 712 */       return String.valueOf(val) + " MB";
/*     */     }
/*     */     
/* 715 */     val /= 1024L;
/*     */     
/* 717 */     return String.valueOf(val) + " GB";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static long decodeDisplayLong(String val)
/*     */     throws Exception
/*     */   {
/* 726 */     char[] chars = val.trim().toCharArray();
/*     */     
/* 728 */     String digits = "";
/* 729 */     String units = "";
/*     */     
/* 731 */     for (char c : chars)
/*     */     {
/* 733 */       if (Character.isDigit(c))
/*     */       {
/* 735 */         if (units.length() > 0)
/*     */         {
/* 737 */           throw new Exception("Invalid unit");
/*     */         }
/*     */         
/* 740 */         digits = digits + c;
/*     */       }
/*     */       else
/*     */       {
/* 744 */         if (digits.length() == 0)
/*     */         {
/* 746 */           throw new Exception("Missing digits");
/*     */         }
/* 748 */         if ((units.length() != 0) || (!Character.isWhitespace(c)))
/*     */         {
/*     */ 
/*     */ 
/* 752 */           units = units + c;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 757 */     long value = Long.parseLong(digits);
/*     */     
/* 759 */     if (units.length() == 0)
/*     */     {
/* 761 */       units = "m";
/*     */     }
/*     */     
/* 764 */     if (units.length() > 0)
/*     */     {
/* 766 */       char c = Character.toLowerCase(units.charAt(0));
/*     */       
/* 768 */       if (c == 'k')
/*     */       {
/* 770 */         value *= 1024L;
/*     */       }
/* 772 */       else if (c == 'm')
/*     */       {
/* 774 */         value = value * 1024L * 1024L;
/*     */       }
/* 776 */       else if (c == 'g')
/*     */       {
/* 778 */         value = value * 1024L * 1024L * 1024L;
/*     */       }
/*     */       else
/*     */       {
/* 782 */         throw new Exception("Invalid size unit '" + units + "'");
/*     */       }
/*     */     }
/*     */     
/* 786 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   private static String[][] getActionDetails()
/*     */   {
/* 792 */     PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*     */     
/* 794 */     int shutdown_types = platform.getShutdownTypes();
/*     */     
/* 796 */     List<String> l_action_values = new ArrayList();
/* 797 */     List<String> l_action_descs = new ArrayList();
/*     */     
/* 799 */     l_action_values.add("Nothing");
/* 800 */     l_action_values.add("QuitVuze");
/*     */     
/* 802 */     if ((shutdown_types & 0x4) != 0)
/*     */     {
/* 804 */       l_action_values.add("Sleep");
/*     */     }
/* 806 */     if ((shutdown_types & 0x2) != 0)
/*     */     {
/* 808 */       l_action_values.add("Hibernate");
/*     */     }
/* 810 */     if ((shutdown_types & 0x1) != 0)
/*     */     {
/* 812 */       l_action_values.add("Shutdown");
/*     */     }
/*     */     
/* 815 */     l_action_values.add("RunScript");
/* 816 */     l_action_values.add("RunScriptAndClose");
/*     */     
/* 818 */     String[] action_values = (String[])l_action_values.toArray(new String[l_action_values.size()]);
/*     */     
/* 820 */     for (String s : action_values)
/*     */     {
/* 822 */       l_action_descs.add(MessageText.getString("ConfigView.label.stop." + s));
/*     */     }
/*     */     
/* 825 */     String[] action_descs = (String[])l_action_descs.toArray(new String[l_action_descs.size()]);
/*     */     
/* 827 */     return new String[][] { action_descs, action_values };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addDoneDownloadingOption(Composite comp, boolean include_script_setting)
/*     */   {
/* 835 */     GridData gridData = new GridData();
/* 836 */     Label label = new Label(comp, 0);
/* 837 */     Messages.setLanguageText(label, "ConfigView.label.stop.downcomp");
/* 838 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 840 */     String[][] action_details = getActionDetails();
/*     */     
/* 842 */     StringListParameter dc = new StringListParameter(comp, "On Downloading Complete Do", "Nothing", action_details[0], action_details[1]);
/*     */     
/* 844 */     if (include_script_setting)
/*     */     {
/* 846 */       final Label dc_label = new Label(comp, 0);
/* 847 */       Messages.setLanguageText(dc_label, "label.script.to.run");
/* 848 */       Utils.setLayoutData(dc_label, new GridData());
/*     */       
/* 850 */       gridData = new GridData(768);
/* 851 */       final FileParameter dc_script = new FileParameter(comp, "On Downloading Complete Script", "", new String[0]);
/* 852 */       dc_script.setLayoutData(gridData);
/*     */       
/* 854 */       boolean is_script = dc.getValue().startsWith("RunScript");
/*     */       
/* 856 */       dc_label.setEnabled(is_script);
/* 857 */       dc_script.setEnabled(is_script);
/*     */       
/* 859 */       dc.addChangeListener(new ParameterChangeAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void parameterChanged(Parameter p, boolean caused_internally)
/*     */         {
/*     */ 
/*     */ 
/* 867 */           boolean is_script = this.val$dc.getValue().startsWith("RunScript");
/*     */           
/* 869 */           dc_label.setEnabled(is_script);
/* 870 */           dc_script.setEnabled(is_script);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void addDoneSeedingOption(Composite comp, boolean include_script_setting)
/*     */   {
/* 881 */     GridData gridData = new GridData();
/* 882 */     Label label = new Label(comp, 0);
/* 883 */     Messages.setLanguageText(label, "ConfigView.label.stop.seedcomp");
/* 884 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 886 */     String[][] action_details = getActionDetails();
/*     */     
/* 888 */     StringListParameter sc = new StringListParameter(comp, "On Seeding Complete Do", "Nothing", action_details[0], action_details[1]);
/*     */     
/* 890 */     if (include_script_setting)
/*     */     {
/* 892 */       final Label sc_label = new Label(comp, 0);
/* 893 */       Messages.setLanguageText(sc_label, "label.script.to.run");
/* 894 */       Utils.setLayoutData(sc_label, new GridData());
/* 895 */       gridData = new GridData(768);
/* 896 */       final FileParameter sc_script = new FileParameter(comp, "On Seeding Complete Script", "", new String[0]);
/* 897 */       sc_script.setLayoutData(gridData);
/*     */       
/* 899 */       boolean is_script = sc.getValue().startsWith("RunScript");
/*     */       
/* 901 */       sc_label.setEnabled(is_script);
/* 902 */       sc_script.setEnabled(is_script);
/*     */       
/* 904 */       sc.addChangeListener(new ParameterChangeAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void parameterChanged(Parameter p, boolean caused_internally)
/*     */         {
/*     */ 
/*     */ 
/* 912 */           boolean is_script = this.val$sc.getValue().startsWith("RunScript");
/*     */           
/* 914 */           sc_label.setEnabled(is_script);
/* 915 */           sc_script.setEnabled(is_script);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionStartShutdown.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */