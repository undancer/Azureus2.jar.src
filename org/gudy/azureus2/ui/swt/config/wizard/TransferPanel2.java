/*     */ package org.gudy.azureus2.ui.swt.config.wizard;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*     */ public class TransferPanel2
/*     */   extends AbstractWizardPanel<ConfigureWizard>
/*     */ {
/*     */   private static final int kbit = 1000;
/*     */   private static final int mbit = 1000000;
/*  60 */   private static final int[] connection_rates = { 0, 28800, 56000, 64000, 96000, 128000, 192000, 256000, 384000, 512000, 640000, 768000, 1000000, 2000000, 5000000, 10000000, 20000000, 50000000, 100000000 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private volatile boolean test_in_progress;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean manual_mode;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Label uprate_label;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TransferPanel2(ConfigureWizard wizard, IWizardPanel previous)
/*     */   {
/*  90 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  97 */     ((ConfigureWizard)this.wizard).setTitle(MessageText.getString("configureWizard.transfer.title"));
/*  98 */     ((ConfigureWizard)this.wizard).setCurrentInfo(MessageText.getString("configureWizard.transfer2.hint"));
/*  99 */     final Composite rootPanel = ((ConfigureWizard)this.wizard).getPanel();
/* 100 */     GridLayout layout = new GridLayout();
/* 101 */     layout.numColumns = 1;
/* 102 */     rootPanel.setLayout(layout);
/*     */     
/* 104 */     Composite panel = new Composite(rootPanel, 0);
/* 105 */     GridData gridData = new GridData(1808);
/* 106 */     Utils.setLayoutData(panel, gridData);
/* 107 */     layout = new GridLayout();
/* 108 */     layout.numColumns = 2;
/* 109 */     panel.setLayout(layout);
/*     */     
/* 111 */     Label label = new Label(panel, 64);
/* 112 */     gridData = new GridData(768);
/* 113 */     gridData.horizontalSpan = 2;
/* 114 */     Utils.setLayoutData(label, gridData);
/* 115 */     Messages.setLanguageText(label, "configureWizard.transfer2.message");
/*     */     
/* 117 */     Group gRadio = new Group(panel, 0);
/* 118 */     Messages.setLanguageText(gRadio, "configureWizard.transfer2.group");
/* 119 */     Utils.setLayoutData(gRadio, gridData);
/* 120 */     layout = new GridLayout();
/* 121 */     layout.numColumns = 2;
/* 122 */     gRadio.setLayout(layout);
/* 123 */     gridData = new GridData(768);
/* 124 */     gridData.horizontalSpan = 2;
/* 125 */     Utils.setLayoutData(gRadio, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 130 */     Button auto_button = new Button(gRadio, 16);
/* 131 */     Messages.setLanguageText(auto_button, "auto.mode");
/* 132 */     auto_button.setSelection(true);
/*     */     
/* 134 */     new Label(gRadio, 0);
/*     */     
/*     */ 
/*     */ 
/* 138 */     label = new Label(gRadio, 0);
/* 139 */     Messages.setLanguageText(label, "configureWizard.transfer2.test.info");
/*     */     
/* 141 */     final Button speed_test = new Button(gRadio, 0);
/*     */     
/* 143 */     Messages.setLanguageText(speed_test, "configureWizard.transfer2.test");
/*     */     
/* 145 */     final SelectionAdapter speed_test_listener = new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent arg0)
/*     */       {
/*     */ 
/* 152 */         speed_test.setEnabled(false);
/*     */         
/* 154 */         TransferPanel2.this.test_in_progress = true;
/*     */         
/* 156 */         TransferPanel2.this.updateNextEnabled();
/*     */         
/* 158 */         rootPanel.getShell().setEnabled(false);
/*     */         
/* 160 */         UIFunctionsManager.getUIFunctions().installPlugin("mlab", "dlg.install.mlab", new UIFunctions.actionListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void actionComplete(Object result)
/*     */           {
/*     */ 
/*     */ 
/* 169 */             if ((result instanceof Boolean))
/*     */             {
/* 171 */               PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("mlab");
/*     */               
/* 173 */               if (pi == null)
/*     */               {
/* 175 */                 Debug.out("mlab plugin not found");
/*     */                 
/* 177 */                 enableTest();
/*     */               }
/*     */               else {
/* 180 */                 IPCInterface callback = new IPCInterface()
/*     */                 {
/*     */                   public Object invoke(String methodName, Object[] params)
/*     */                   {
/*     */                     try
/*     */                     {
/*     */                       Map<String, Object> results;
/*     */                       
/*     */ 
/* 189 */                       if (methodName.equals("results"))
/*     */                       {
/* 191 */                         results = (Map)params[0];
/*     */                         
/* 193 */                         Long up_rate = (Long)results.get("up");
/*     */                         
/* 195 */                         if (up_rate != null)
/*     */                         {
/* 197 */                           final int u = up_rate.intValue();
/*     */                           
/* 199 */                           if (u > 0)
/*     */                           {
/* 201 */                             Utils.execSWTThread(new Runnable()
/*     */                             {
/*     */ 
/*     */                               public void run()
/*     */                               {
/*     */ 
/* 207 */                                 TransferPanel2.this.updateUp(u, false);
/*     */                               }
/*     */                             });
/*     */                           }
/*     */                         }
/*     */                       }
/*     */                       
/* 214 */                       return null;
/*     */                     }
/*     */                     finally
/*     */                     {
/* 218 */                       TransferPanel2.1.1.this.enableTest();
/*     */                     }
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */                   public boolean canInvoke(String methodName, Object[] params)
/*     */                   {
/* 227 */                     return true;
/*     */                   }
/*     */                 };
/*     */                 try
/*     */                 {
/* 232 */                   pi.getIPC().invoke("runTest", new Object[] { new HashMap(), callback, Boolean.valueOf(false) });
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/*     */ 
/* 238 */                   Debug.out(e);
/*     */                   
/* 240 */                   enableTest();
/*     */                 }
/*     */               }
/*     */             }
/*     */             else {
/*     */               try {
/* 246 */                 Throwable error = (Throwable)result;
/*     */                 
/* 248 */                 Debug.out(error);
/*     */               }
/*     */               finally
/*     */               {
/* 252 */                 enableTest();
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */           protected void enableTest()
/*     */           {
/* 260 */             Utils.execSWTThread(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 266 */                 TransferPanel2.1.this.val$speed_test.setEnabled(true);
/*     */                 
/* 268 */                 TransferPanel2.this.test_in_progress = false;
/*     */                 
/* 270 */                 TransferPanel2.this.updateNextEnabled();
/*     */                 
/* 272 */                 TransferPanel2.1.this.val$rootPanel.getShell().setEnabled(true);
/*     */               }
/*     */               
/*     */             });
/*     */           }
/*     */         });
/*     */       }
/* 279 */     };
/* 280 */     speed_test.addSelectionListener(speed_test_listener);
/*     */     
/*     */ 
/*     */ 
/* 284 */     final Button manual_button = new Button(gRadio, 16);
/* 285 */     Messages.setLanguageText(manual_button, "manual.mode");
/*     */     
/* 287 */     new Label(gRadio, 0);
/*     */     
/*     */ 
/*     */ 
/* 291 */     final Label manual_label = new Label(gRadio, 0);
/* 292 */     Messages.setLanguageText(manual_label, "configureWizard.transfer2.mselect");
/*     */     
/* 294 */     String[] connection_labels = new String[connection_rates.length];
/*     */     
/* 296 */     connection_labels[0] = MessageText.getString("configureWizard.transfer2.current");
/*     */     
/* 298 */     String dial_up = MessageText.getString("dial.up");
/*     */     
/* 300 */     for (int i = 1; i < connection_rates.length; i++)
/*     */     {
/* 302 */       connection_labels[i] = ((i < 3 ? dial_up + " " : "xxx/") + DisplayFormatters.formatByteCountToBitsPerSec(connection_rates[i] / 8));
/*     */     }
/*     */     
/* 305 */     final Combo connection_speed = new Combo(gRadio, 12);
/*     */     
/* 307 */     for (int i = 0; i < connection_rates.length; i++)
/*     */     {
/* 309 */       connection_speed.add(connection_labels[i]);
/*     */     }
/*     */     
/* 312 */     connection_speed.select(0);
/*     */     
/* 314 */     connection_speed.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/*     */ 
/* 322 */         int index = connection_speed.getSelectionIndex();
/*     */         
/* 324 */         TransferPanel2.this.updateUp(TransferPanel2.connection_rates[index] / 8, true);
/*     */       }
/*     */       
/* 327 */     });
/* 328 */     final Label manual2_label = new Label(gRadio, 64);
/* 329 */     Messages.setLanguageText(manual2_label, "configureWizard.transfer2.mselect.info");
/* 330 */     gridData = new GridData(768);
/* 331 */     gridData.horizontalSpan = 2;
/* 332 */     Utils.setLayoutData(manual2_label, gridData);
/*     */     
/* 334 */     Listener listener = new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/* 341 */         boolean is_manual = manual_button.getSelection();
/*     */         
/* 343 */         speed_test.setEnabled(!is_manual);
/*     */         
/* 345 */         connection_speed.setEnabled(is_manual);
/* 346 */         manual_label.setEnabled(is_manual);
/* 347 */         manual2_label.setEnabled(is_manual);
/*     */         
/* 349 */         TransferPanel2.this.manual_mode = is_manual;
/*     */         
/* 351 */         TransferPanel2.this.updateNextEnabled();
/*     */       }
/* 353 */     };
/* 354 */     manual_button.addListener(13, listener);
/*     */     
/* 356 */     listener.handleEvent(null);
/*     */     
/* 358 */     this.uprate_label = new Label(panel, 64);
/* 359 */     gridData = new GridData(1808);
/* 360 */     gridData.verticalIndent = 10;
/* 361 */     Utils.setLayoutData(this.uprate_label, gridData);
/* 362 */     updateUp(0, true);
/*     */     
/* 364 */     this.manual_mode = false;
/*     */     
/* 366 */     updateNextEnabled();
/*     */     
/* 368 */     if (((ConfigureWizard)this.wizard).getWizardMode() == 1)
/*     */     {
/* 370 */       Utils.execSWTThreadLater(0, new Runnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 377 */           speed_test_listener.widgetSelected(null);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateUp(int rate, boolean manual)
/*     */   {
/* 388 */     ((ConfigureWizard)this.wizard).setConnectionUploadLimit(rate, manual);
/*     */     
/* 390 */     if (rate == 0)
/*     */     {
/* 392 */       this.uprate_label.setText(MessageText.getString("configureWizard.transfer2.rate.unchanged"));
/*     */     }
/*     */     else
/*     */     {
/* 396 */       this.uprate_label.setText(MessageText.getString("configureWizard.transfer2.rate.changed", new String[] { DisplayFormatters.formatByteCountToBitsPerSec(rate) + " (" + DisplayFormatters.formatByteCountToKiBEtcPerSec(rate) + ")", DisplayFormatters.formatByteCountToKiBEtcPerSec(((ConfigureWizard)this.wizard).getUploadLimit()), String.valueOf(((ConfigureWizard)this.wizard).maxActiveTorrents), String.valueOf(((ConfigureWizard)this.wizard).maxDownloads) }));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateNextEnabled()
/*     */   {
/* 410 */     ((ConfigureWizard)this.wizard).setPreviousEnabled(isPreviousEnabled());
/*     */     
/* 412 */     boolean enabled = isProgressEnabled();
/*     */     
/*     */ 
/* 415 */     if (((ConfigureWizard)this.wizard).getWizardMode() != 0)
/*     */     {
/* 417 */       ((ConfigureWizard)this.wizard).setNextEnabled(false);
/*     */       
/* 419 */       ((ConfigureWizard)this.wizard).setFinishEnabled(enabled);
/*     */     }
/*     */     else {
/* 422 */       ((ConfigureWizard)this.wizard).setNextEnabled(enabled);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isProgressEnabled()
/*     */   {
/* 429 */     if (this.test_in_progress)
/*     */     {
/* 431 */       return false;
/*     */     }
/*     */     
/* 434 */     if ((this.manual_mode) || (((ConfigureWizard)this.wizard).getConnectionUploadLimit() > 0))
/*     */     {
/* 436 */       return true;
/*     */     }
/*     */     
/* 439 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 445 */     return (isProgressEnabled()) && (((ConfigureWizard)this.wizard).getWizardMode() == 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPreviousEnabled()
/*     */   {
/* 451 */     return (!this.test_in_progress) && (((ConfigureWizard)this.wizard).getWizardMode() == 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel getFinishPanel()
/*     */   {
/* 457 */     return new FinishPanel((ConfigureWizard)this.wizard, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel getNextPanel()
/*     */   {
/* 463 */     return new NatPanel((ConfigureWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/wizard/TransferPanel2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */