/*     */ package com.aelitis.azureus.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.views.configsections.ConfigSectionStartShutdown;
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
/*     */ public class ViewQuickConfig
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private UISWTView swtView;
/*     */   Composite composite;
/*     */   
/*     */   public ViewQuickConfig()
/*     */   {
/*  66 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initialize(Composite ooparent)
/*     */   {
/*  77 */     GridLayout layout = new GridLayout(1, false);
/*  78 */     layout.marginWidth = 0;
/*  79 */     layout.marginHeight = 0;
/*     */     
/*  81 */     ooparent.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  86 */     Composite oparent = new Composite(ooparent, 2048);
/*  87 */     GridData gridData = new GridData(1808);
/*  88 */     Utils.setLayoutData(oparent, gridData);
/*     */     
/*  90 */     layout = new GridLayout(1, false);
/*  91 */     layout.marginWidth = 0;
/*  92 */     layout.marginHeight = 0;
/*     */     
/*  94 */     oparent.setLayout(layout);
/*     */     
/*  96 */     final Composite parent = new Composite(oparent, 0);
/*  97 */     gridData = new GridData(1808);
/*  98 */     Utils.setLayoutData(parent, gridData);
/*     */     
/* 100 */     layout = new GridLayout(1, false);
/* 101 */     layout.marginWidth = 0;
/* 102 */     layout.marginHeight = 0;
/*     */     
/* 104 */     parent.setLayout(layout);
/*     */     
/* 106 */     final ScrolledComposite sc = new ScrolledComposite(parent, 512);
/* 107 */     sc.setExpandHorizontal(true);
/* 108 */     sc.setExpandVertical(true);
/* 109 */     sc.addListener(11, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 112 */         int width = sc.getClientArea().width;
/* 113 */         Point size = parent.computeSize(width, -1);
/* 114 */         sc.setMinSize(size);
/*     */       }
/*     */       
/* 117 */     });
/* 118 */     gridData = new GridData(1808);
/* 119 */     Utils.setLayoutData(sc, gridData);
/*     */     
/* 121 */     this.composite = new Composite(sc, 0);
/*     */     
/* 123 */     sc.setContent(this.composite);
/*     */     
/* 125 */     gridData = new GridData(1808);
/* 126 */     Utils.setLayoutData(this.composite, gridData);
/*     */     
/* 128 */     layout = new GridLayout(4, false);
/*     */     
/* 130 */     this.composite.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 134 */     ConfigSectionStartShutdown.addDoneDownloadingOption(this.composite, false);
/*     */     
/*     */ 
/*     */ 
/* 138 */     Label label = new Label(this.composite, 0);
/* 139 */     gridData = new GridData();
/* 140 */     gridData.horizontalIndent = 8;
/* 141 */     Utils.setLayoutData(label, gridData);
/* 142 */     Messages.setLanguageText(label, "ConfigView.label.maxdownloads.short");
/*     */     
/* 144 */     IntParameter maxDLs = new IntParameter(this.composite, "max downloads");
/*     */     
/*     */ 
/*     */ 
/* 148 */     Group temp_rates = new Group(this.composite, 0);
/* 149 */     Messages.setLanguageText(temp_rates, "label.temporary.rates");
/*     */     
/* 151 */     gridData = new GridData(768);
/* 152 */     gridData.horizontalSpan = 4;
/*     */     
/* 154 */     Utils.setLayoutData(temp_rates, gridData);
/*     */     
/* 156 */     layout = new GridLayout(10, false);
/* 157 */     layout.marginWidth = 0;
/* 158 */     layout.marginHeight = 0;
/*     */     
/* 160 */     temp_rates.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 165 */     label = new Label(temp_rates, 0);
/* 166 */     gridData = new GridData();
/* 167 */     gridData.horizontalIndent = 4;
/* 168 */     Utils.setLayoutData(label, gridData);
/* 169 */     Messages.setLanguageText(label, "label.upload.kbps", new String[] { DisplayFormatters.getRateUnit(1) });
/*     */     
/* 171 */     final IntParameter tempULRate = new IntParameter(temp_rates, "global.download.rate.temp.kbps", 0, Integer.MAX_VALUE);
/*     */     
/* 173 */     label = new Label(temp_rates, 0);
/* 174 */     Messages.setLanguageText(label, "label.download.kbps", new String[] { DisplayFormatters.getRateUnit(1) });
/*     */     
/* 176 */     final IntParameter tempDLRate = new IntParameter(temp_rates, "global.upload.rate.temp.kbps", 0, Integer.MAX_VALUE);
/*     */     
/* 178 */     label = new Label(temp_rates, 0);
/* 179 */     Messages.setLanguageText(label, "label.duration.mins");
/*     */     
/* 181 */     final IntParameter tempMins = new IntParameter(temp_rates, "global.rate.temp.min", 0, Integer.MAX_VALUE);
/*     */     
/* 183 */     final Button activate = new Button(temp_rates, 2);
/* 184 */     Messages.setLanguageText(activate, "label.activate");
/*     */     
/* 186 */     final BufferedLabel remLabel = new BufferedLabel(temp_rates, 536870912);
/* 187 */     gridData = new GridData();
/* 188 */     gridData.widthHint = 150;
/* 189 */     Utils.setLayoutData(remLabel, gridData);
/*     */     
/* 191 */     activate.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */       private AzureusCoreLifecycleAdapter listener;
/*     */       
/*     */       private TimerEventPeriodic event;
/*     */       
/*     */       private boolean auto_up_enabled;
/*     */       
/*     */       private boolean auto_up_seeding_enabled;
/*     */       
/*     */       private boolean seeding_limits_enabled;
/*     */       
/*     */       private int up_limit;
/*     */       
/*     */       private int down_limit;
/*     */       private long end_time;
/*     */       
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/* 210 */         if (activate.getSelection())
/*     */         {
/* 212 */           this.listener = new AzureusCoreLifecycleAdapter()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void stopping(AzureusCore core)
/*     */             {
/*     */ 
/* 219 */               ViewQuickConfig.3.this.deactivate(true);
/*     */             }
/*     */             
/* 222 */           };
/* 223 */           AzureusCoreFactory.getSingleton().addLifecycleListener(this.listener);
/*     */           
/* 225 */           Messages.setLanguageText(activate, "FileView.BlockView.Active");
/*     */           
/* 227 */           tempDLRate.setEnabled(false);
/* 228 */           tempULRate.setEnabled(false);
/* 229 */           tempMins.setEnabled(false);
/*     */           
/* 231 */           this.auto_up_enabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled");
/* 232 */           this.auto_up_seeding_enabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Seeding Enabled");
/* 233 */           this.seeding_limits_enabled = COConfigurationManager.getBooleanParameter("enable.seedingonly.upload.rate");
/* 234 */           this.up_limit = COConfigurationManager.getIntParameter("Max Upload Speed KBs");
/* 235 */           this.down_limit = COConfigurationManager.getIntParameter("Max Download Speed KBs");
/*     */           
/* 237 */           COConfigurationManager.setParameter("Auto Upload Speed Enabled", false);
/* 238 */           COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", false);
/* 239 */           COConfigurationManager.setParameter("enable.seedingonly.upload.rate", false);
/*     */           
/* 241 */           COConfigurationManager.setParameter("Max Upload Speed KBs", tempULRate.getValue());
/* 242 */           COConfigurationManager.setParameter("Max Download Speed KBs", tempDLRate.getValue());
/*     */           
/* 244 */           this.end_time = (SystemTime.getCurrentTime() + tempMins.getValue() * 60 * 1000);
/*     */           
/* 246 */           this.event = SimpleTimer.addPeriodicEvent("TempRates", 1000L, new TimerEventPerformer()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void perform(TimerEvent e)
/*     */             {
/*     */ 
/* 253 */               Utils.execSWTThread(new Runnable()
/*     */               {
/*     */ 
/*     */                 public void run()
/*     */                 {
/* 258 */                   if (ViewQuickConfig.3.this.event == null)
/*     */                   {
/* 260 */                     return;
/*     */                   }
/*     */                   
/* 263 */                   long now = SystemTime.getCurrentTime();
/*     */                   
/* 265 */                   long rem = ViewQuickConfig.3.this.end_time - now;
/*     */                   
/* 267 */                   if ((rem < 1000L) || (ViewQuickConfig.this.composite.isDisposed()))
/*     */                   {
/* 269 */                     ViewQuickConfig.3.this.deactivate(false);
/*     */                   }
/*     */                   else
/*     */                   {
/* 273 */                     ViewQuickConfig.3.this.val$remLabel.setText(MessageText.getString("TableColumn.header.remaining") + ": " + DisplayFormatters.formatTime(rem));
/*     */                   }
/*     */                 }
/*     */               });
/*     */             }
/*     */           });
/*     */         }
/*     */         else {
/* 281 */           deactivate(false);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       private void deactivate(boolean closing)
/*     */       {
/* 289 */         COConfigurationManager.setParameter("Auto Upload Speed Enabled", this.auto_up_enabled);
/* 290 */         COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", this.auto_up_seeding_enabled);
/* 291 */         COConfigurationManager.setParameter("enable.seedingonly.upload.rate", this.seeding_limits_enabled);
/*     */         
/* 293 */         COConfigurationManager.setParameter("Max Upload Speed KBs", this.up_limit);
/* 294 */         COConfigurationManager.setParameter("Max Download Speed KBs", this.down_limit);
/*     */         
/* 296 */         if (!closing)
/*     */         {
/* 298 */           if (this.listener != null)
/*     */           {
/* 300 */             AzureusCoreFactory.getSingleton().removeLifecycleListener(this.listener);
/*     */             
/* 302 */             this.listener = null;
/*     */           }
/*     */           
/* 305 */           if (!ViewQuickConfig.this.composite.isDisposed())
/*     */           {
/* 307 */             Messages.setLanguageText(activate, "label.activate");
/* 308 */             activate.setSelection(false);
/*     */             
/* 310 */             tempDLRate.setEnabled(true);
/* 311 */             tempULRate.setEnabled(true);
/* 312 */             tempMins.setEnabled(true);
/* 313 */             remLabel.setText("");
/*     */           }
/*     */         }
/*     */         
/* 317 */         if (this.event != null) {
/* 318 */           this.event.cancel();
/* 319 */           this.event = null;
/*     */         }
/*     */         
/*     */       }
/* 323 */     });
/* 324 */     activate.setEnabled(tempMins.getValue() > 0);
/*     */     
/* 326 */     tempMins.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 335 */         activate.setEnabled(tempMins.getValue() > 0);
/*     */       }
/*     */       
/* 338 */     });
/* 339 */     Utils.execSWTThreadLater(100, new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 344 */         ViewQuickConfig.this.composite.traverse(16);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void delete() {
/* 350 */     Utils.disposeComposite(this.composite);
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 354 */     return MessageText.getString("label.quick.config");
/*     */   }
/*     */   
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 360 */     return this.composite;
/*     */   }
/*     */   
/*     */ 
/*     */   private void refresh() {}
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/* 368 */     switch (event.getType()) {
/*     */     case 0: 
/* 370 */       this.swtView = event.getView();
/* 371 */       this.swtView.setTitle(getFullTitle());
/* 372 */       break;
/*     */     
/*     */     case 7: 
/* 375 */       delete();
/* 376 */       break;
/*     */     
/*     */     case 2: 
/* 379 */       initialize((Composite)event.getData());
/* 380 */       break;
/*     */     
/*     */     case 6: 
/* 383 */       Messages.updateLanguageForControl(getComposite());
/* 384 */       this.swtView.setTitle(getFullTitle());
/* 385 */       break;
/*     */     
/*     */     case 5: 
/* 388 */       refresh();
/* 389 */       break;
/*     */     case 3: 
/* 391 */       this.composite.traverse(16);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 396 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/ViewQuickConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */