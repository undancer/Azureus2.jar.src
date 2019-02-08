/*     */ package org.gudy.azureus2.ui.swt.speedtest;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduledTest;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduledTestListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTestScheduler;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTester;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterResult;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.impl.NetworkAdminSpeedTestSchedulerImpl;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
/*     */ import org.gudy.azureus2.ui.swt.wizard.WizardListener;
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
/*     */ public class SpeedTestPanel
/*     */   extends AbstractWizardPanel
/*     */   implements NetworkAdminSpeedTestScheduledTestListener, NetworkAdminSpeedTesterListener
/*     */ {
/*     */   private NetworkAdminSpeedTestScheduler nasts;
/*     */   private NetworkAdminSpeedTestScheduledTest scheduled_test;
/*     */   private Combo testCombo;
/*     */   private Button encryptToggle;
/*     */   private Color originalColor;
/*     */   private Button test;
/*     */   private Button abort;
/*     */   private Label testCountDown1;
/*     */   private Label testCountDown2;
/*     */   private Text textMessages;
/*     */   private ProgressBar progress;
/*     */   private Display display;
/*     */   private boolean test_running;
/*     */   private boolean switched_to_close;
/*     */   int uploadTest;
/*     */   int downloadTest;
/*     */   long maxUploadTest;
/*     */   long maxDownloadTest;
/*     */   WizardListener clListener;
/*     */   private static final String START_VALUES = "   -         ";
/*     */   
/*     */   public SpeedTestPanel(SpeedTestWizard _wizard, IWizardPanel _previousPanel)
/*     */   {
/* 100 */     super(_wizard, _previousPanel);
/* 101 */     this.wizard = _wizard;
/* 102 */     this.nasts = NetworkAdminSpeedTestSchedulerImpl.getInstance();
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/* 108 */     this.display = this.wizard.getDisplay();
/* 109 */     this.wizard.setTitle(MessageText.getString("speedtest.wizard.run"));
/* 110 */     this.wizard.setCurrentInfo(MessageText.getString("SpeedTestWizard.test.panel.currinfo"));
/* 111 */     this.wizard.setPreviousEnabled(false);
/* 112 */     this.wizard.setFinishEnabled(false);
/*     */     
/* 114 */     Composite rootPanel = this.wizard.getPanel();
/* 115 */     GridLayout layout = new GridLayout();
/* 116 */     layout.numColumns = 1;
/* 117 */     rootPanel.setLayout(layout);
/*     */     
/* 119 */     Composite panel = new Composite(rootPanel, 0);
/* 120 */     GridData gridData = new GridData(1808);
/* 121 */     Utils.setLayoutData(panel, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 126 */     Group azWiki = new Group(panel, 64);
/* 127 */     GridData azwGridData = new GridData();
/* 128 */     azwGridData.widthHint = 350;
/* 129 */     azwGridData.horizontalSpan = 4;
/* 130 */     Utils.setLayoutData(azWiki, azwGridData);
/* 131 */     GridLayout azwLayout = new GridLayout();
/* 132 */     azwLayout.numColumns = 1;
/*     */     
/* 134 */     azWiki.setLayout(azwLayout);
/*     */     
/* 136 */     azWiki.setText(MessageText.getString("Utils.link.visit"));
/*     */     
/* 138 */     Label linkLabel = new Label(azWiki, 0);
/* 139 */     linkLabel.setText(Constants.APP_NAME + " Wiki Speed Test");
/* 140 */     linkLabel.setData("http://wiki.vuze.com/w/Speed_Test_FAQ");
/* 141 */     linkLabel.setCursor(this.display.getSystemCursor(21));
/* 142 */     linkLabel.setForeground(Colors.blue);
/* 143 */     azwGridData = new GridData();
/* 144 */     azwGridData.horizontalIndent = 10;
/* 145 */     Utils.setLayoutData(linkLabel, azwGridData);
/* 146 */     linkLabel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 148 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/* 151 */       public void mouseUp(MouseEvent arg0) { Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/*     */ 
/* 155 */     });
/* 156 */     Label spacer = new Label(panel, 0);
/* 157 */     gridData = new GridData();
/* 158 */     gridData.horizontalSpan = 4;
/* 159 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/*     */ 
/* 162 */     layout = new GridLayout();
/* 163 */     layout.numColumns = 4;
/* 164 */     panel.setLayout(layout);
/*     */     
/* 166 */     Label explain = new Label(panel, 64);
/* 167 */     gridData = new GridData(768);
/* 168 */     gridData.horizontalSpan = 4;
/* 169 */     Utils.setLayoutData(explain, gridData);
/* 170 */     Messages.setLanguageText(explain, "SpeedTestWizard.test.panel.explain");
/*     */     
/*     */ 
/*     */ 
/* 174 */     spacer = new Label(panel, 0);
/* 175 */     gridData = new GridData();
/* 176 */     gridData.horizontalSpan = 4;
/* 177 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/*     */ 
/* 180 */     Label ul = new Label(panel, 0);
/* 181 */     gridData = new GridData();
/* 182 */     Utils.setLayoutData(ul, gridData);
/* 183 */     Messages.setLanguageText(ul, "SpeedTestWizard.test.panel.label");
/*     */     
/* 185 */     this.testCombo = new Combo(panel, 8);
/* 186 */     gridData = new GridData(768);
/* 187 */     Utils.setLayoutData(this.testCombo, gridData);
/*     */     
/* 189 */     int[] test_types = NetworkAdminSpeedTester.TEST_TYPES;
/* 190 */     int up_only_index = 0;
/*     */     
/* 192 */     for (int i = 0; i < test_types.length; i++)
/*     */     {
/* 194 */       int test_type = test_types[i];
/*     */       
/* 196 */       String resource = null;
/*     */       
/* 198 */       if (test_type == 0) {
/* 199 */         resource = "up";
/* 200 */         up_only_index = i;
/* 201 */       } else if (test_type == 1) {
/* 202 */         resource = "down";
/*     */       } else {
/* 204 */         Debug.out("Unknown test type");
/*     */       }
/*     */       
/* 207 */       this.testCombo.add("BT " + MessageText.getString(new StringBuilder().append("speedtest.wizard.test.mode.").append(resource).toString()), i);
/*     */     }
/*     */     
/* 210 */     this.testCombo.select(up_only_index);
/*     */     
/* 212 */     this.test = new Button(panel, 8);
/* 213 */     Messages.setLanguageText(this.test, "dht.execute");
/* 214 */     gridData = new GridData();
/* 215 */     gridData.widthHint = 70;
/* 216 */     Utils.setLayoutData(this.test, gridData);
/* 217 */     this.test.addListener(13, new RunButtonListener());
/*     */     
/* 219 */     this.abort = new Button(panel, 8);
/* 220 */     Messages.setLanguageText(this.abort, "SpeedTestWizard.test.panel.abort");
/* 221 */     gridData = new GridData();
/* 222 */     gridData.widthHint = 70;
/* 223 */     Utils.setLayoutData(this.abort, gridData);
/* 224 */     this.abort.setEnabled(false);
/* 225 */     this.abort.addListener(13, new AbortButtonListener());
/*     */     
/*     */ 
/* 228 */     Label enc = new Label(panel, 0);
/* 229 */     gridData = new GridData();
/* 230 */     Utils.setLayoutData(enc, gridData);
/* 231 */     Messages.setLanguageText(enc, "SpeedTestWizard.test.panel.enc.label");
/*     */     
/* 233 */     this.encryptToggle = new Button(panel, 2);
/*     */     
/* 235 */     String statusString = "SpeedTestWizard.test.panel.standard";
/* 236 */     if (this.encryptToggle.getSelection()) {
/* 237 */       statusString = "SpeedTestWizard.test.panel.encrypted";
/*     */     }
/* 239 */     Messages.setLanguageText(this.encryptToggle, statusString);
/* 240 */     gridData = new GridData();
/* 241 */     gridData.widthHint = 80;
/* 242 */     Utils.setLayoutData(this.encryptToggle, gridData);
/* 243 */     this.encryptToggle.addListener(13, new EncryptToggleButtonListener());
/*     */     
/*     */ 
/* 246 */     Label spacer2 = new Label(panel, 0);
/* 247 */     gridData = new GridData();
/* 248 */     gridData.horizontalSpan = 2;
/* 249 */     spacer2.setLayoutData(gridData);
/*     */     
/*     */ 
/* 252 */     Label abortCountDown = new Label(panel, 0);
/* 253 */     gridData = new GridData();
/* 254 */     Utils.setLayoutData(abortCountDown, gridData);
/* 255 */     Messages.setLanguageText(abortCountDown, "SpeedTestWizard.test.panel.abort.countdown");
/*     */     
/* 257 */     this.testCountDown1 = new Label(panel, 0);
/* 258 */     gridData = new GridData();
/* 259 */     this.testCountDown1.setLayoutData(gridData);
/* 260 */     this.testCountDown1.setText("   -         ");
/*     */     
/* 262 */     Label testFinishCountDown = new Label(panel, 0);
/* 263 */     gridData = new GridData();
/* 264 */     Utils.setLayoutData(testFinishCountDown, gridData);
/* 265 */     Messages.setLanguageText(testFinishCountDown, "SpeedTestWizard.test.panel.test.countdown");
/*     */     
/* 267 */     this.testCountDown2 = new Label(panel, 0);
/* 268 */     gridData = new GridData();
/* 269 */     this.testCountDown2.setLayoutData(gridData);
/* 270 */     this.testCountDown2.setText("   -         ");
/*     */     
/*     */ 
/*     */ 
/* 274 */     this.progress = new ProgressBar(panel, 65536);
/* 275 */     this.progress.setMinimum(0);
/* 276 */     this.progress.setMaximum(100);
/* 277 */     gridData = new GridData(768);
/* 278 */     gridData.horizontalSpan = 4;
/* 279 */     Utils.setLayoutData(this.progress, gridData);
/*     */     
/*     */ 
/* 282 */     this.textMessages = new Text(panel, 2826);
/* 283 */     this.textMessages.setBackground(this.display.getSystemColor(1));
/* 284 */     gridData = new GridData(1808);
/* 285 */     gridData.horizontalSpan = 4;
/* 286 */     gridData.heightHint = 60;
/* 287 */     Utils.setLayoutData(this.textMessages, gridData);
/*     */     
/*     */ 
/* 290 */     String lastData = SpeedTestData.getInstance().getLastTestData();
/* 291 */     if (lastData != null) {
/* 292 */       this.textMessages.setText(lastData);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void finish()
/*     */   {
/* 300 */     this.test_running = true;
/*     */     
/* 302 */     this.clListener = new WizardListener()
/*     */     {
/*     */ 
/*     */       public void closed()
/*     */       {
/* 307 */         SpeedTestPanel.this.cancel();
/*     */       }
/*     */       
/* 310 */     };
/* 311 */     this.wizard.addListener(this.clListener);
/*     */     
/* 313 */     this.wizard.setFinishEnabled(false);
/*     */     
/*     */ 
/*     */ 
/* 317 */     final int test_mode = NetworkAdminSpeedTester.TEST_TYPES[this.testCombo.getSelectionIndex()];
/* 318 */     final boolean encState = this.encryptToggle.getSelection();
/*     */     
/* 320 */     Thread t = new AEThread("SpeedTest Performer")
/*     */     {
/*     */ 
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 327 */         SpeedTestPanel.this.runTest(test_mode, encState);
/*     */       }
/*     */       
/* 330 */     };
/* 331 */     t.setPriority(1);
/* 332 */     t.setDaemon(true);
/* 333 */     t.start();
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 339 */     if (this.scheduled_test != null)
/*     */     {
/* 341 */       this.scheduled_test.abort();
/*     */       
/* 343 */       if (!this.test.isDisposed())
/*     */       {
/* 345 */         this.test.setEnabled(true);
/* 346 */         this.abort.setEnabled(false);
/* 347 */         this.wizard.setNextEnabled(false);
/* 348 */         this.wizard.setFinishEnabled(false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void runTest(int test_mode, boolean encrypt_mode)
/*     */   {
/* 356 */     this.test_running = true;
/*     */     
/* 358 */     if (this.nasts.getCurrentTest() != null)
/*     */     {
/* 360 */       reportStage(MessageText.getString("SpeedTestWizard.test.panel.already.running"));
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 365 */         reportStage(MessageText.getString("SpeedTestWizard.stage.message.requesting"));
/* 366 */         this.scheduled_test = this.nasts.scheduleTest(1);
/*     */         
/* 368 */         this.scheduled_test.getTester().setMode(test_mode);
/* 369 */         this.scheduled_test.getTester().setUseCrypto(encrypt_mode);
/*     */         
/* 371 */         this.scheduled_test.addListener(this);
/* 372 */         this.scheduled_test.getTester().addListener(this);
/*     */         
/* 374 */         this.maxUploadTest = this.scheduled_test.getMaxUpBytePerSec();
/* 375 */         this.maxDownloadTest = this.scheduled_test.getMaxDownBytePerSec();
/*     */         
/* 377 */         this.scheduled_test.start();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 381 */         String requestNotAccepted = MessageText.getString("SpeedTestWizard.test.panel.not.accepted");
/* 382 */         reportStage(requestNotAccepted + Debug.getNestedExceptionMessage(e));
/*     */         
/* 384 */         if (!this.test.isDisposed()) {
/* 385 */           this.display.asyncExec(new AERunnable()
/*     */           {
/*     */             public void runSupport() {
/* 388 */               SpeedTestPanel.this.test.setEnabled(true);
/* 389 */               SpeedTestPanel.this.abort.setEnabled(false);
/* 390 */               SpeedTestPanel.this.encryptToggle.setEnabled(true);
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stage(NetworkAdminSpeedTestScheduledTest test, String step)
/*     */   {
/* 404 */     reportStage(step);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void complete(NetworkAdminSpeedTestScheduledTest test) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stage(NetworkAdminSpeedTester tester, String step)
/*     */   {
/* 418 */     reportStage(step);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void complete(NetworkAdminSpeedTester tester, NetworkAdminSpeedTesterResult result)
/*     */   {
/* 426 */     SpeedTestData.getInstance().setResult(result);
/* 427 */     reportComplete(result);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void reportComplete(final NetworkAdminSpeedTesterResult result)
/*     */   {
/* 434 */     if (!this.textMessages.isDisposed()) {
/* 435 */       this.display.asyncExec(new AERunnable() {
/*     */         public void runSupport() {
/* 437 */           if (!SpeedTestPanel.this.textMessages.isDisposed()) {
/* 438 */             if (result.hadError())
/*     */             {
/* 440 */               String testFailed = MessageText.getString("SpeedTestWizard.test.panel.testfailed");
/*     */               
/* 442 */               SpeedTestPanel.this.textMessages.append(testFailed + ": " + result.getLastError());
/* 443 */               SpeedTestPanel.this.test.setEnabled(true);
/* 444 */               SpeedTestPanel.this.abort.setEnabled(false);
/* 445 */               SpeedTestPanel.this.encryptToggle.setEnabled(true);
/* 446 */               SpeedTestPanel.this.wizard.setErrorMessage(testFailed);
/*     */             }
/*     */             else {
/* 449 */               SpeedTestPanel.this.uploadTest = result.getUploadSpeed();
/* 450 */               SpeedTestPanel.this.downloadTest = result.getDownloadSpeed();
/* 451 */               String uploadSpeedStr = MessageText.getString("GeneralView.label.uploadspeed");
/* 452 */               String downlaodSpeedStr = MessageText.getString("GeneralView.label.downloadspeed");
/* 453 */               SpeedTestPanel.this.textMessages.append(uploadSpeedStr + " " + DisplayFormatters.formatByteCountToKiBEtcPerSec(result.getUploadSpeed()) + Text.DELIMITER);
/* 454 */               SpeedTestPanel.this.textMessages.append(downlaodSpeedStr + " " + DisplayFormatters.formatByteCountToKiBEtcPerSec(result.getDownloadSpeed()) + Text.DELIMITER);
/*     */               
/* 456 */               SpeedTestPanel.this.wizard.setNextEnabled(true);
/*     */               
/* 458 */               SpeedTestPanel.this.abort.setEnabled(false);
/* 459 */               SpeedTestPanel.this.test.setEnabled(true);
/* 460 */               SpeedTestPanel.this.encryptToggle.setEnabled(true);
/*     */             }
/*     */             
/* 463 */             if (!result.hadError()) {
/* 464 */               SpeedTestPanel.this.switchToClose();
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 470 */     this.wizard.removeListener(this.clListener);
/* 471 */     this.clListener = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void reportStage(final String step)
/*     */   {
/* 478 */     if (!this.textMessages.isDisposed()) {
/* 479 */       this.display.asyncExec(new AERunnable()
/*     */       {
/*     */         public void runSupport() {
/* 482 */           if (!SpeedTestPanel.this.textMessages.isDisposed()) {
/* 483 */             if (step == null) {
/* 484 */               return;
/*     */             }
/*     */             
/* 487 */             if (step.startsWith("progress:"))
/*     */             {
/* 489 */               int progressAmount = SpeedTestPanel.getProgressBarValueFromString(step);
/* 490 */               SpeedTestPanel.this.progress.setSelection(progressAmount);
/*     */               
/* 492 */               int[] timeLeft = SpeedTestPanel.getTimeLeftFromString(step);
/* 493 */               if (timeLeft != null)
/*     */               {
/* 495 */                 SpeedTestPanel.this.testCountDown1.setText("" + timeLeft[0] + " sec ");
/* 496 */                 SpeedTestPanel.this.testCountDown2.setText("" + timeLeft[1] + " sec ");
/*     */               } else {
/* 498 */                 SpeedTestPanel.this.testCountDown1.setText("   -         ");
/* 499 */                 SpeedTestPanel.this.testCountDown2.setText("   -         ");
/*     */               }
/* 501 */               String modified = SpeedTestPanel.modifyProgressStatusString(step);
/* 502 */               SpeedTestPanel.this.textMessages.append(modified);
/*     */             }
/*     */             else {
/* 505 */               SpeedTestPanel.this.textMessages.append(step + Text.DELIMITER);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String modifyProgressStatusString(String step)
/*     */   {
/* 519 */     if (step == null) {
/* 520 */       return " ";
/*     */     }
/* 522 */     if (!step.startsWith("progress:")) {
/* 523 */       return " ";
/*     */     }
/*     */     
/* 526 */     String[] values = step.split(":");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 532 */     if (values.length < 4) {
/* 533 */       return " ";
/*     */     }
/*     */     
/* 536 */     int downAve = getValueFromAveString(values[2]);
/* 537 */     int upAve = getValueFromAveString(values[3]);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 542 */     StringBuilder sb = new StringBuilder();
/* 543 */     sb.append(MessageText.getString("GeneralView.label.uploadspeed"));
/* 544 */     sb.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(upAve)).append(" , ");
/* 545 */     sb.append(MessageText.getString("GeneralView.label.downloadspeed"));
/* 546 */     sb.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(downAve));
/* 547 */     sb.append("\n");
/*     */     
/* 549 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int getValueFromAveString(String aveStr)
/*     */   {
/*     */     try
/*     */     {
/* 559 */       int number = -2;
/* 560 */       aveStr = aveStr.trim();
/* 561 */       String[] parts = aveStr.split(" ");
/*     */       
/* 563 */       if (parts != null) {}
/* 564 */       return Integer.parseInt(parts[(parts.length - 1)].trim());
/*     */     }
/*     */     catch (Throwable t) {}
/*     */     
/* 568 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int[] getTimeLeftFromString(String step)
/*     */   {
/* 580 */     if (step == null)
/* 581 */       return null;
/* 582 */     if (!step.startsWith("progress:")) {
/* 583 */       return null;
/*     */     }
/* 585 */     String[] values = step.split(":");
/* 586 */     if (values.length < 5) {
/* 587 */       return null;
/*     */     }
/*     */     
/* 590 */     int[] times = new int[2];
/*     */     try {
/* 592 */       times[0] = Integer.parseInt(values[4].trim());
/* 593 */       times[1] = Integer.parseInt(values[5].trim());
/*     */       
/*     */ 
/* 596 */       if (times[0] < 0) {
/* 597 */         times[0] = 0;
/*     */       }
/*     */       
/* 600 */       if (times[1] < 0) {
/* 601 */         times[1] = 0;
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 606 */       return null;
/*     */     }
/* 608 */     return times;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int getProgressBarValueFromString(String step)
/*     */   {
/* 617 */     if (step == null) {
/* 618 */       return 0;
/*     */     }
/* 620 */     if (!step.startsWith("progress:")) {
/* 621 */       return 0;
/*     */     }
/* 623 */     String[] value = step.split(":");
/* 624 */     if (value.length < 2) {
/* 625 */       return 0;
/*     */     }
/*     */     int progress;
/*     */     try {
/* 629 */       progress = Integer.parseInt(value[1].trim());
/*     */     } catch (Exception e) {
/* 631 */       return 0;
/*     */     }
/*     */     
/* 634 */     if ((progress < 0) || (progress > 100)) {
/* 635 */       return 0;
/*     */     }
/* 637 */     return progress;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void switchToClose()
/*     */   {
/* 643 */     this.switched_to_close = true;
/*     */     
/* 645 */     this.wizard.switchToClose();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishEnabled()
/*     */   {
/* 651 */     return (!this.switched_to_close) && (!this.test_running);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishSelectionOK()
/*     */   {
/* 657 */     return (!this.switched_to_close) && (!this.test_running);
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel getFinishPanel()
/*     */   {
/* 663 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 669 */     return ((this.uploadTest > 0) || (this.downloadTest > 0)) && (!this.test_running);
/*     */   }
/*     */   
/*     */   public IWizardPanel getNextPanel()
/*     */   {
/* 674 */     SpeedTestData persist = SpeedTestData.getInstance();
/* 675 */     persist.setLastTestData(this.textMessages.getText());
/*     */     
/* 677 */     return new SpeedTestSetLimitPanel(this.wizard, this, this.uploadTest, this.maxUploadTest, this.downloadTest, this.maxDownloadTest);
/*     */   }
/*     */   
/*     */ 
/*     */   class AbortButtonListener
/*     */     implements Listener
/*     */   {
/*     */     AbortButtonListener() {}
/*     */     
/*     */     public void handleEvent(Event event)
/*     */     {
/* 688 */       SpeedTestPanel.this.cancel();
/* 689 */       SpeedTestPanel.this.test.setEnabled(true);
/* 690 */       SpeedTestPanel.this.abort.setEnabled(false);
/* 691 */       SpeedTestPanel.this.encryptToggle.setEnabled(true);
/* 692 */       SpeedTestPanel.this.wizard.setNextEnabled(false);
/* 693 */       SpeedTestPanel.this.uploadTest = 0;
/* 694 */       SpeedTestPanel.this.downloadTest = 0;
/*     */       
/* 696 */       String testAbortedManually = MessageText.getString("SpeedTestWizard.test.panel.aborted");
/* 697 */       SpeedTestPanel.this.wizard.setErrorMessage(testAbortedManually);
/* 698 */       SpeedTestPanel.this.reportStage("\n" + testAbortedManually);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class RunButtonListener
/*     */     implements Listener
/*     */   {
/*     */     RunButtonListener() {}
/*     */     
/*     */     public void handleEvent(Event event)
/*     */     {
/* 710 */       SpeedTestPanel.this.abort.setEnabled(true);
/* 711 */       SpeedTestPanel.this.test.setEnabled(false);
/* 712 */       SpeedTestPanel.this.encryptToggle.setEnabled(false);
/* 713 */       SpeedTestPanel.this.wizard.setErrorMessage("");
/* 714 */       SpeedTestPanel.this.wizard.setNextEnabled(false);
/* 715 */       SpeedTestPanel.this.textMessages.setText("");
/* 716 */       SpeedTestPanel.this.finish();
/*     */     }
/*     */   }
/*     */   
/*     */   class EncryptToggleButtonListener
/*     */     implements Listener
/*     */   {
/*     */     EncryptToggleButtonListener() {}
/*     */     
/*     */     public void handleEvent(Event event)
/*     */     {
/* 727 */       if (SpeedTestPanel.this.encryptToggle.getSelection()) {
/* 728 */         Messages.setLanguageText(SpeedTestPanel.this.encryptToggle, "SpeedTestWizard.test.panel.encrypted");
/* 729 */         SpeedTestPanel.this.originalColor = SpeedTestPanel.this.encryptToggle.getForeground();
/*     */         
/* 731 */         Color highlightColor = SpeedTestPanel.this.display.getSystemColor(8);
/* 732 */         SpeedTestPanel.this.encryptToggle.setBackground(highlightColor);
/*     */       } else {
/* 734 */         Messages.setLanguageText(SpeedTestPanel.this.encryptToggle, "SpeedTestWizard.test.panel.standard");
/* 735 */         if (SpeedTestPanel.this.originalColor != null) {
/* 736 */           SpeedTestPanel.this.encryptToggle.setBackground(SpeedTestPanel.this.originalColor);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/speedtest/SpeedTestPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */