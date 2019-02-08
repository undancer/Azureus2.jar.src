/*     */ package com.aelitis.azureus.plugins.net.netstatus.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.plugins.net.netstatus.NetStatusPlugin;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.custom.StyleRange;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetStatusPluginView
/*     */   implements UISWTViewEventListener
/*     */ {
/*     */   private NetStatusPlugin plugin;
/*  64 */   private boolean created = false;
/*     */   
/*     */   private Composite composite;
/*     */   
/*     */   private Button start_button;
/*     */   
/*     */   private Button cancel_button;
/*     */   
/*     */   private StyledText log;
/*     */   
/*     */   private int selected_tests;
/*     */   private NetStatusPluginTester current_test;
/*     */   private static final int LOG_NORMAL = 1;
/*     */   private static final int LOG_SUCCESS = 2;
/*     */   private static final int LOG_ERROR = 3;
/*     */   private static final int LOG_INFO = 4;
/*  80 */   private int log_type = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NetStatusPluginView(NetStatusPlugin _plugin, UIInstance _ui, String VIEW_ID)
/*     */   {
/*  88 */     this.plugin = _plugin;
/*     */     
/*  90 */     ((UISWTInstance)_ui).addView("Main", VIEW_ID, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/*  97 */     switch (event.getType())
/*     */     {
/*     */ 
/*     */     case 0: 
/* 101 */       if (this.created)
/*     */       {
/* 103 */         return false;
/*     */       }
/*     */       
/* 106 */       this.created = true;
/*     */       
/* 108 */       break;
/*     */     
/*     */ 
/*     */     case 2: 
/* 112 */       initialise((Composite)event.getData());
/*     */       
/* 114 */       break;
/*     */     
/*     */     case 7: 
/*     */     case 8: 
/*     */       try
/*     */       {
/* 120 */         destroy();
/*     */       }
/*     */       finally
/*     */       {
/* 124 */         this.created = false;
/*     */       }
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 131 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void initialise(Composite _composite)
/*     */   {
/* 138 */     this.composite = _composite;
/*     */     
/* 140 */     Composite main = new Composite(this.composite, 0);
/* 141 */     GridLayout layout = new GridLayout();
/* 142 */     layout.numColumns = 1;
/* 143 */     layout.marginHeight = 0;
/* 144 */     layout.marginWidth = 0;
/* 145 */     main.setLayout(layout);
/* 146 */     GridData grid_data = new GridData(1808);
/* 147 */     Utils.setLayoutData(main, grid_data);
/*     */     
/*     */ 
/*     */ 
/* 151 */     Composite control = new Composite(main, 0);
/* 152 */     layout = new GridLayout();
/* 153 */     layout.numColumns = 3;
/* 154 */     layout.marginHeight = 4;
/* 155 */     layout.marginWidth = 4;
/* 156 */     control.setLayout(layout);
/*     */     
/* 158 */     Label info = new Label(control, 0);
/* 159 */     grid_data = new GridData(768);
/* 160 */     grid_data.horizontalSpan = 3;
/* 161 */     Utils.setLayoutData(info, grid_data);
/* 162 */     Messages.setLanguageText(info, "label.test.internet");
/*     */     
/* 164 */     grid_data = new GridData(768);
/* 165 */     grid_data.horizontalSpan = 1;
/* 166 */     Utils.setLayoutData(control, grid_data);
/*     */     
/* 168 */     List<Button> buttons = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/* 172 */     this.start_button = new Button(control, 8);
/*     */     
/* 174 */     buttons.add(this.start_button);
/*     */     
/* 176 */     Messages.setLanguageText(this.start_button, "ConfigView.section.start");
/*     */     
/* 178 */     this.start_button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 185 */         NetStatusPluginView.this.start_button.setEnabled(false);
/*     */         
/* 187 */         NetStatusPluginView.this.cancel_button.setEnabled(true);
/*     */         
/* 189 */         NetStatusPluginView.this.startTest();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 194 */     });
/* 195 */     this.cancel_button = new Button(control, 8);
/*     */     
/* 197 */     buttons.add(this.cancel_button);
/*     */     
/* 199 */     Messages.setLanguageText(this.cancel_button, "UpdateWindow.cancel");
/*     */     
/* 201 */     this.cancel_button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 208 */         NetStatusPluginView.this.cancel_button.setEnabled(false);
/*     */         
/* 210 */         NetStatusPluginView.this.cancelTest();
/*     */       }
/*     */       
/* 213 */     });
/* 214 */     this.cancel_button.setEnabled(false);
/*     */     
/* 216 */     Utils.makeButtonsEqualWidth(buttons);
/*     */     
/* 218 */     Group options = new Group(control, 0);
/* 219 */     layout = new GridLayout();
/* 220 */     layout.numColumns = 4;
/* 221 */     layout.marginHeight = 4;
/* 222 */     layout.marginWidth = 4;
/* 223 */     options.setLayout(layout);
/* 224 */     Messages.setLanguageText(options, "label.test.types");
/*     */     
/*     */ 
/* 227 */     grid_data = new GridData(768);
/* 228 */     Utils.setLayoutData(options, grid_data);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 237 */     Button opt = new Button(options, 32);
/*     */     
/* 239 */     Messages.setLanguageText(opt, "label.outbound");
/*     */     
/* 241 */     addOption(opt, 4, true);
/*     */     
/* 243 */     opt = new Button(options, 32);
/*     */     
/* 245 */     Messages.setLanguageText(opt, "label.inbound");
/*     */     
/* 247 */     addOption(opt, 8, true);
/*     */     
/* 249 */     opt = new Button(options, 32);
/*     */     
/* 251 */     Messages.setLanguageText(opt, "label.nat.proxies");
/*     */     
/* 253 */     addOption(opt, 2, true);
/*     */     
/* 255 */     opt = new Button(options, 32);
/*     */     
/* 257 */     Messages.setLanguageText(opt, "label.bt.connect");
/*     */     
/* 259 */     addOption(opt, 16, true);
/*     */     
/* 261 */     opt = new Button(options, 32);
/*     */     
/* 263 */     opt.setText("IPv6");
/*     */     
/* 265 */     boolean ipv6_enabled = COConfigurationManager.getBooleanParameter("IPV6 Enable Support");
/*     */     
/* 267 */     addOption(opt, 32, ipv6_enabled);
/*     */     
/* 269 */     opt = new Button(options, 32);
/*     */     
/* 271 */     Messages.setLanguageText(opt, "label.vuze.services");
/*     */     
/* 273 */     addOption(opt, 64, true);
/*     */     
/* 275 */     if ((Constants.isWindows) || (Constants.isOSX))
/*     */     {
/* 277 */       opt = new Button(options, 32);
/*     */       
/* 279 */       Messages.setLanguageText(opt, "label.indirect.connect");
/*     */       
/* 281 */       boolean ic_enabled = AEProxyFactory.hasPluginProxy();
/*     */       
/* 283 */       addOption(opt, 128, ic_enabled);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 288 */     this.log = new StyledText(main, 2824);
/* 289 */     grid_data = new GridData(1808);
/* 290 */     grid_data.horizontalSpan = 1;
/* 291 */     grid_data.horizontalIndent = 4;
/* 292 */     Utils.setLayoutData(this.log, grid_data);
/* 293 */     this.log.setIndent(4);
/*     */     
/* 295 */     ClipboardCopy.addCopyToClipMenu(this.log, new ClipboardCopy.copyToClipProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getText()
/*     */       {
/*     */ 
/* 302 */         return NetStatusPluginView.this.log.getText().trim();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addOption(final Button button, final int type, boolean enable)
/*     */   {
/* 313 */     final String config = "test.option." + type;
/*     */     
/* 315 */     boolean selected = this.plugin.getBooleanParameter(config, enable);
/*     */     
/* 317 */     if ((selected) && (enable))
/*     */     {
/* 319 */       this.selected_tests |= type;
/*     */     }
/*     */     else
/*     */     {
/* 323 */       this.selected_tests &= (type ^ 0xFFFFFFFF);
/*     */     }
/*     */     
/* 326 */     if (!enable)
/*     */     {
/* 328 */       button.setEnabled(false);
/*     */     }
/*     */     
/* 331 */     button.setSelection(selected);
/*     */     
/* 333 */     button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 340 */         boolean selected = button.getSelection();
/*     */         
/* 342 */         if (selected)
/*     */         {
/* 344 */           NetStatusPluginView.access$376(NetStatusPluginView.this, type);
/*     */         }
/*     */         else
/*     */         {
/* 348 */           NetStatusPluginView.access$372(NetStatusPluginView.this, type ^ 0xFFFFFFFF);
/*     */         }
/*     */         
/* 351 */         NetStatusPluginView.this.plugin.setBooleanParameter(config, selected);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void startTest()
/*     */   {
/* 359 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 362 */         NetStatusPluginView.this.startTestSupport(core);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancelTest()
/*     */   {
/* 370 */     new AEThread2("NetStatus:cancel", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 375 */         NetStatusPluginView.this.cancelTestSupport();
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */   protected void startTestSupport(AzureusCore core)
/*     */   {
/*     */     try
/*     */     {
/* 384 */       synchronized (this)
/*     */       {
/* 386 */         if (this.current_test != null)
/*     */         {
/* 388 */           Debug.out("Test already running!!!!");
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
/*     */           try
/*     */           {
/* 470 */             Composite c = this.composite;
/*     */             
/* 472 */             if ((c != null) && (!c.isDisposed())) {
/*     */               try
/*     */               {
/* 475 */                 c.getDisplay().asyncExec(new Runnable()
/*     */                 {
/*     */ 
/*     */                   public void run()
/*     */                   {
/*     */ 
/* 481 */                     if (!NetStatusPluginView.this.start_button.isDisposed())
/*     */                     {
/* 483 */                       NetStatusPluginView.this.start_button.setEnabled(true);
/*     */                     }
/*     */                     
/* 486 */                     if (!NetStatusPluginView.this.cancel_button.isDisposed())
/*     */                     {
/* 488 */                       NetStatusPluginView.this.cancel_button.setEnabled(false);
/*     */                     }
/*     */                   }
/*     */                 });
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 498 */             synchronized (this)
/*     */             {
/* 500 */               this.current_test.cancel();
/*     */               
/* 502 */               this.current_test = null;
/*     */             }
/*     */           }
/*     */           return;
/*     */         }
/* 393 */         this.current_test = new NetStatusPluginTester(this.plugin, this.selected_tests, new NetStatusPluginTester.loggerProvider()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void log(String str, boolean detailed)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 404 */             if ((detailed) && (!NetStatusPluginView.this.plugin.isDetailedLogging()))
/*     */             {
/* 406 */               return;
/*     */             }
/*     */             
/* 409 */             NetStatusPluginView.this.println(str);
/*     */           }
/*     */           
/*     */ 
/*     */           public void logSuccess(String str)
/*     */           {
/*     */             try
/*     */             {
/* 417 */               NetStatusPluginView.this.log_type = 2;
/*     */               
/* 419 */               NetStatusPluginView.this.println(str);
/*     */             }
/*     */             finally
/*     */             {
/* 423 */               NetStatusPluginView.this.log_type = 1;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */           public void logInfo(String str)
/*     */           {
/*     */             try
/*     */             {
/* 432 */               NetStatusPluginView.this.log_type = 4;
/*     */               
/* 434 */               NetStatusPluginView.this.println(str);
/*     */             }
/*     */             finally
/*     */             {
/* 438 */               NetStatusPluginView.this.log_type = 1;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */           public void logFailure(String str)
/*     */           {
/*     */             try
/*     */             {
/* 447 */               NetStatusPluginView.this.log_type = 3;
/*     */               
/* 449 */               NetStatusPluginView.this.println(str);
/*     */             }
/*     */             finally
/*     */             {
/* 453 */               NetStatusPluginView.this.log_type = 1;
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 459 */       println("Test starting", true);
/*     */       
/* 461 */       this.current_test.run(core);
/*     */       
/* 463 */       println(this.current_test.isCancelled() ? "Test Cancelled" : "Test complete");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 470 */         Composite c = this.composite;
/*     */         
/* 472 */         if ((c != null) && (!c.isDisposed())) {
/*     */           try
/*     */           {
/* 475 */             c.getDisplay().asyncExec(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 481 */                 if (!NetStatusPluginView.this.start_button.isDisposed())
/*     */                 {
/* 483 */                   NetStatusPluginView.this.start_button.setEnabled(true);
/*     */                 }
/*     */                 
/* 486 */                 if (!NetStatusPluginView.this.cancel_button.isDisposed())
/*     */                 {
/* 488 */                   NetStatusPluginView.this.cancel_button.setEnabled(false);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 498 */         synchronized (this)
/*     */         {
/* 500 */           this.current_test.cancel();
/*     */           
/* 502 */           this.current_test = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try
/*     */       {
/* 470 */         Composite c = this.composite;
/*     */         
/* 472 */         if ((c != null) && (!c.isDisposed())) {
/*     */           try
/*     */           {
/* 475 */             c.getDisplay().asyncExec(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 481 */                 if (!NetStatusPluginView.this.start_button.isDisposed())
/*     */                 {
/* 483 */                   NetStatusPluginView.this.start_button.setEnabled(true);
/*     */                 }
/*     */                 
/* 486 */                 if (!NetStatusPluginView.this.cancel_button.isDisposed())
/*     */                 {
/* 488 */                   NetStatusPluginView.this.cancel_button.setEnabled(false);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 498 */         synchronized (this)
/*     */         {
/* 500 */           this.current_test.cancel();
/*     */           
/* 502 */           this.current_test = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 470 */         Composite c = this.composite;
/*     */         
/* 472 */         if ((c != null) && (!c.isDisposed())) {
/*     */           try
/*     */           {
/* 475 */             c.getDisplay().asyncExec(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 481 */                 if (!NetStatusPluginView.this.start_button.isDisposed())
/*     */                 {
/* 483 */                   NetStatusPluginView.this.start_button.setEnabled(true);
/*     */                 }
/*     */                 
/* 486 */                 if (!NetStatusPluginView.this.cancel_button.isDisposed())
/*     */                 {
/* 488 */                   NetStatusPluginView.this.cancel_button.setEnabled(false);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 498 */         synchronized (this)
/*     */         {
/* 500 */           this.current_test.cancel();
/*     */           
/* 502 */           this.current_test = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void println(String str)
/*     */   {
/* 512 */     print(str + "\n", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void println(String str, boolean clear_first)
/*     */   {
/* 520 */     print(str + "\n", clear_first);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void print(final String str, final boolean clear_first)
/*     */   {
/* 528 */     this.plugin.log(str);
/*     */     
/* 530 */     if ((!this.log.isDisposed()) && (!this.log.getDisplay().isDisposed()))
/*     */     {
/* 532 */       final int f_log_type = this.log_type;
/*     */       
/* 534 */       this.log.getDisplay().asyncExec(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 540 */           if (NetStatusPluginView.this.log.isDisposed()) {
/*     */             return;
/*     */           }
/*     */           
/*     */ 
/*     */           int start;
/*     */           
/* 547 */           if (clear_first)
/*     */           {
/* 549 */             int start = 0;
/*     */             
/* 551 */             NetStatusPluginView.this.log.setText(str);
/*     */           }
/*     */           else
/*     */           {
/* 555 */             start = NetStatusPluginView.this.log.getText().length();
/*     */             
/* 557 */             NetStatusPluginView.this.log.append(str);
/*     */           }
/*     */           
/*     */           Color color;
/*     */           Color color;
/* 562 */           if (f_log_type == 1)
/*     */           {
/* 564 */             color = Colors.black;
/*     */           } else { Color color;
/* 566 */             if (f_log_type == 2)
/*     */             {
/* 568 */               color = Colors.green;
/*     */             } else { Color color;
/* 570 */               if (f_log_type == 4)
/*     */               {
/* 572 */                 color = Colors.blue;
/*     */               }
/*     */               else
/*     */               {
/* 576 */                 color = Colors.red; }
/*     */             }
/*     */           }
/* 579 */           StyleRange styleRange = new StyleRange();
/* 580 */           styleRange.start = start;
/* 581 */           styleRange.length = str.length();
/* 582 */           styleRange.foreground = color;
/* 583 */           NetStatusPluginView.this.log.setStyleRange(styleRange);
/*     */           
/* 585 */           NetStatusPluginView.this.log.setSelection(NetStatusPluginView.this.log.getText().length());
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancelTestSupport()
/*     */   {
/* 594 */     synchronized (this)
/*     */     {
/* 596 */       if (this.current_test != null)
/*     */       {
/* 598 */         println("Cancelling test...");
/*     */         
/* 600 */         this.current_test.cancel();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 608 */     cancelTest();
/*     */     
/* 610 */     this.composite = null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/netstatus/swt/NetStatusPluginView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */