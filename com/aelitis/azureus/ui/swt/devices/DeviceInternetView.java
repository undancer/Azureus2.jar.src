/*     */ package com.aelitis.azureus.ui.swt.devices;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.plugins.net.netstatus.NetStatusPlugin;
/*     */ import com.aelitis.azureus.plugins.net.netstatus.swt.NetStatusPluginTester;
/*     */ import com.aelitis.azureus.plugins.net.netstatus.swt.NetStatusPluginTester.loggerProvider;
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
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
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
/*     */ public class DeviceInternetView
/*     */   extends DeviceManagerUI.categoryView
/*     */ {
/*     */   private DeviceManagerUI device_manager_ui;
/*     */   private NetStatusPlugin plugin;
/*     */   private Composite main;
/*     */   private Button start_button;
/*     */   private Button cancel_button;
/*     */   private StyledText log;
/*     */   private static final int selected_tests = 206;
/*     */   private NetStatusPluginTester current_test;
/*     */   private static final int LOG_NORMAL = 1;
/*     */   private static final int LOG_SUCCESS = 2;
/*     */   private static final int LOG_ERROR = 3;
/*     */   private static final int LOG_INFO = 4;
/*  78 */   private int log_type = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceInternetView(DeviceManagerUI dm_ui, String title)
/*     */   {
/*  85 */     super(dm_ui, 4, title);
/*     */     
/*  87 */     this.device_manager_ui = dm_ui;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(Composite parent)
/*     */   {
/*  94 */     PluginInterface pi = this.device_manager_ui.getPluginInterface().getPluginManager().getPluginInterfaceByClass(NetStatusPlugin.class);
/*     */     
/*  96 */     this.plugin = ((NetStatusPlugin)pi.getPlugin());
/*     */     
/*  98 */     this.main = new Composite(parent, 0);
/*  99 */     GridLayout layout = new GridLayout();
/* 100 */     layout.numColumns = 1;
/* 101 */     layout.marginTop = 4;
/* 102 */     layout.marginBottom = 4;
/* 103 */     layout.marginHeight = 4;
/* 104 */     layout.marginWidth = 4;
/* 105 */     this.main.setLayout(layout);
/* 106 */     GridData grid_data = new GridData(1808);
/* 107 */     this.main.setLayoutData(grid_data);
/*     */     
/* 109 */     Label info_lab = new Label(this.main, 0);
/*     */     
/* 111 */     Messages.setLanguageText(info_lab, "label.test.internet");
/*     */     
/*     */ 
/*     */ 
/* 115 */     Composite control = new Composite(this.main, 0);
/* 116 */     layout = new GridLayout();
/* 117 */     layout.numColumns = 3;
/* 118 */     layout.marginHeight = 4;
/* 119 */     layout.marginWidth = 4;
/* 120 */     control.setLayout(layout);
/*     */     
/* 122 */     grid_data = new GridData(768);
/* 123 */     grid_data.horizontalSpan = 1;
/* 124 */     control.setLayoutData(grid_data);
/*     */     
/*     */ 
/*     */ 
/* 128 */     this.start_button = new Button(control, 8);
/*     */     
/* 130 */     Messages.setLanguageText(this.start_button, "ConfigView.section.start");
/*     */     
/* 132 */     this.start_button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 139 */         DeviceInternetView.this.start_button.setEnabled(false);
/*     */         
/* 141 */         DeviceInternetView.this.cancel_button.setEnabled(true);
/*     */         
/* 143 */         DeviceInternetView.this.startTest();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 148 */     });
/* 149 */     this.cancel_button = new Button(control, 8);
/*     */     
/* 151 */     Messages.setLanguageText(this.cancel_button, "UpdateWindow.cancel");
/*     */     
/* 153 */     this.cancel_button.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 160 */         DeviceInternetView.this.cancel_button.setEnabled(false);
/*     */         
/* 162 */         DeviceInternetView.this.cancelTest();
/*     */       }
/*     */       
/* 165 */     });
/* 166 */     this.cancel_button.setEnabled(false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 171 */     this.log = new StyledText(this.main, 2824);
/* 172 */     grid_data = new GridData(1808);
/* 173 */     grid_data.horizontalSpan = 1;
/* 174 */     grid_data.horizontalIndent = 4;
/* 175 */     this.log.setLayoutData(grid_data);
/* 176 */     this.log.setIndent(4);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void startTest()
/*     */   {
/* 182 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 185 */         DeviceInternetView.this.startTestSupport(core);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancelTest()
/*     */   {
/* 193 */     new AEThread2("NetStatus:cancel", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 198 */         DeviceInternetView.this.cancelTestSupport();
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void startTestSupport(AzureusCore core)
/*     */   {
/*     */     try
/*     */     {
/* 208 */       synchronized (this)
/*     */       {
/* 210 */         if (this.current_test != null)
/*     */         {
/* 212 */           Debug.out("Test already running!!!!");
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
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 296 */             Composite c = this.main;
/*     */             
/* 298 */             if ((c != null) && (!c.isDisposed())) {
/*     */               try
/*     */               {
/* 301 */                 c.getDisplay().asyncExec(new Runnable()
/*     */                 {
/*     */ 
/*     */                   public void run()
/*     */                   {
/*     */ 
/* 307 */                     if (!DeviceInternetView.this.start_button.isDisposed())
/*     */                     {
/* 309 */                       DeviceInternetView.this.start_button.setEnabled(true);
/*     */                     }
/*     */                     
/* 312 */                     if (!DeviceInternetView.this.cancel_button.isDisposed())
/*     */                     {
/* 314 */                       DeviceInternetView.this.cancel_button.setEnabled(false);
/*     */                     }
/*     */                   }
/*     */                 });
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 324 */             synchronized (this)
/*     */             {
/* 326 */               this.current_test.cancel();
/*     */               
/* 328 */               this.current_test = null;
/*     */             }
/*     */           }
/*     */           return;
/*     */         }
/* 217 */         int tests = 206;
/*     */         
/* 219 */         if (NetworkAdmin.getSingleton().isIPV6Enabled())
/*     */         {
/* 221 */           tests |= 0x20;
/*     */         }
/*     */         
/* 224 */         this.current_test = new NetStatusPluginTester(this.plugin, tests, new NetStatusPluginTester.loggerProvider()
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
/* 235 */             DeviceInternetView.this.println(str);
/*     */           }
/*     */           
/*     */ 
/*     */           public void logSuccess(String str)
/*     */           {
/*     */             try
/*     */             {
/* 243 */               DeviceInternetView.this.log_type = 2;
/*     */               
/* 245 */               DeviceInternetView.this.println(str);
/*     */             }
/*     */             finally
/*     */             {
/* 249 */               DeviceInternetView.this.log_type = 1;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */           public void logInfo(String str)
/*     */           {
/*     */             try
/*     */             {
/* 258 */               DeviceInternetView.this.log_type = 4;
/*     */               
/* 260 */               DeviceInternetView.this.println(str);
/*     */             }
/*     */             finally
/*     */             {
/* 264 */               DeviceInternetView.this.log_type = 1;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */           public void logFailure(String str)
/*     */           {
/*     */             try
/*     */             {
/* 273 */               DeviceInternetView.this.log_type = 3;
/*     */               
/* 275 */               DeviceInternetView.this.println(str);
/*     */             }
/*     */             finally
/*     */             {
/* 279 */               DeviceInternetView.this.log_type = 1;
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 285 */       println("Test starting", true);
/*     */       
/* 287 */       this.current_test.run(core);
/*     */       
/* 289 */       println(this.current_test.isCancelled() ? "Test Cancelled" : "Test complete");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 296 */         Composite c = this.main;
/*     */         
/* 298 */         if ((c != null) && (!c.isDisposed())) {
/*     */           try
/*     */           {
/* 301 */             c.getDisplay().asyncExec(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 307 */                 if (!DeviceInternetView.this.start_button.isDisposed())
/*     */                 {
/* 309 */                   DeviceInternetView.this.start_button.setEnabled(true);
/*     */                 }
/*     */                 
/* 312 */                 if (!DeviceInternetView.this.cancel_button.isDisposed())
/*     */                 {
/* 314 */                   DeviceInternetView.this.cancel_button.setEnabled(false);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 324 */         synchronized (this)
/*     */         {
/* 326 */           this.current_test.cancel();
/*     */           
/* 328 */           this.current_test = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try
/*     */       {
/* 296 */         Composite c = this.main;
/*     */         
/* 298 */         if ((c != null) && (!c.isDisposed())) {
/*     */           try
/*     */           {
/* 301 */             c.getDisplay().asyncExec(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 307 */                 if (!DeviceInternetView.this.start_button.isDisposed())
/*     */                 {
/* 309 */                   DeviceInternetView.this.start_button.setEnabled(true);
/*     */                 }
/*     */                 
/* 312 */                 if (!DeviceInternetView.this.cancel_button.isDisposed())
/*     */                 {
/* 314 */                   DeviceInternetView.this.cancel_button.setEnabled(false);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 324 */         synchronized (this)
/*     */         {
/* 326 */           this.current_test.cancel();
/*     */           
/* 328 */           this.current_test = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 296 */         Composite c = this.main;
/*     */         
/* 298 */         if ((c != null) && (!c.isDisposed())) {
/*     */           try
/*     */           {
/* 301 */             c.getDisplay().asyncExec(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 307 */                 if (!DeviceInternetView.this.start_button.isDisposed())
/*     */                 {
/* 309 */                   DeviceInternetView.this.start_button.setEnabled(true);
/*     */                 }
/*     */                 
/* 312 */                 if (!DeviceInternetView.this.cancel_button.isDisposed())
/*     */                 {
/* 314 */                   DeviceInternetView.this.cancel_button.setEnabled(false);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 324 */         synchronized (this)
/*     */         {
/* 326 */           this.current_test.cancel();
/*     */           
/* 328 */           this.current_test = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void println(String str)
/*     */   {
/* 338 */     print(str + "\n", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void println(String str, boolean clear_first)
/*     */   {
/* 346 */     print(str + "\n", clear_first);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void print(final String str, final boolean clear_first)
/*     */   {
/* 354 */     if ((!this.log.isDisposed()) && (!this.log.getDisplay().isDisposed()))
/*     */     {
/* 356 */       final int f_log_type = this.log_type;
/*     */       
/* 358 */       this.log.getDisplay().asyncExec(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 364 */           if (DeviceInternetView.this.log.isDisposed()) {
/*     */             return;
/*     */           }
/*     */           
/*     */ 
/*     */           int start;
/*     */           
/* 371 */           if (clear_first)
/*     */           {
/* 373 */             int start = 0;
/*     */             
/* 375 */             DeviceInternetView.this.log.setText(str);
/*     */           }
/*     */           else
/*     */           {
/* 379 */             start = DeviceInternetView.this.log.getText().length();
/*     */             
/* 381 */             DeviceInternetView.this.log.append(str);
/*     */           }
/*     */           
/*     */           Color color;
/*     */           Color color;
/* 386 */           if (f_log_type == 1)
/*     */           {
/* 388 */             color = Colors.black;
/*     */           } else { Color color;
/* 390 */             if (f_log_type == 2)
/*     */             {
/* 392 */               color = Colors.green;
/*     */             } else { Color color;
/* 394 */               if (f_log_type == 4)
/*     */               {
/* 396 */                 color = Colors.blue;
/*     */               }
/*     */               else
/*     */               {
/* 400 */                 color = Colors.red; }
/*     */             }
/*     */           }
/* 403 */           StyleRange styleRange = new StyleRange();
/* 404 */           styleRange.start = start;
/* 405 */           styleRange.length = str.length();
/* 406 */           styleRange.foreground = color;
/* 407 */           DeviceInternetView.this.log.setStyleRange(styleRange);
/*     */           
/* 409 */           DeviceInternetView.this.log.setSelection(DeviceInternetView.this.log.getText().length());
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancelTestSupport()
/*     */   {
/* 418 */     synchronized (this)
/*     */     {
/* 420 */       if (this.current_test != null)
/*     */       {
/* 422 */         println("Cancelling test...");
/*     */         
/* 424 */         this.current_test.cancel();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite getComposite()
/*     */   {
/* 432 */     return this.main;
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 436 */     switch (event.getType())
/*     */     {
/*     */     case 0: 
/*     */       break;
/*     */     
/*     */     case 7: 
/*     */       break;
/*     */     
/*     */     case 2: 
/* 445 */       initialize((Composite)event.getData());
/* 446 */       break;
/*     */     
/*     */     case 6: 
/* 449 */       Messages.updateLanguageForControl(getComposite());
/* 450 */       break;
/*     */     case 1: 
/*     */       break;
/*     */     case 3: 
/*     */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 462 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/DeviceInternetView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */