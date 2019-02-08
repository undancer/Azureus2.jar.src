/*     */ package org.gudy.azureus2.ui.swt.progress;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.custom.StyleRange;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.AZProgressBar;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.twistie.ITwistieListener;
/*     */ import org.gudy.azureus2.ui.swt.twistie.TwistieSection;
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
/*     */ public class ProgressReporterPanel
/*     */   extends Composite
/*     */   implements IProgressReportConstants, IProgressReporterListener
/*     */ {
/*  51 */   private Color normalColor = null;
/*     */   
/*  53 */   private Color errorColor = null;
/*     */   
/*  55 */   public IProgressReporter pReporter = null;
/*     */   
/*  57 */   private Label imageLabel = null;
/*     */   
/*  59 */   private Label nameLabel = null;
/*     */   
/*  61 */   private Label statusLabel = null;
/*     */   
/*  63 */   private StyledText detailListWidget = null;
/*     */   
/*  65 */   private GridData detailSectionData = null;
/*     */   
/*  67 */   private AZProgressBar pBar = null;
/*     */   
/*  69 */   private Composite progressPanel = null;
/*     */   
/*  71 */   private TwistieSection detailSection = null;
/*     */   
/*     */   private int style;
/*     */   
/*  75 */   private Label actionLabel_cancel = null;
/*     */   
/*  77 */   private Label actionLabel_remove = null;
/*     */   
/*  79 */   private Label actionLabel_retry = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  87 */   private int maxPreferredDetailPanelHeight = 200;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  92 */   private int maxPreferredDetailPanelHeight_Standalone = 600;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  99 */   private int maxPreferredWidth = 900;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 105 */   private String lastStatusError = null;
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
/*     */   public ProgressReporterPanel(Composite parent, IProgressReporter reporter, int style)
/*     */   {
/* 122 */     super(parent, (style & 0x10) != 0 ? 2048 : 0);
/*     */     
/* 124 */     if (null == reporter) {
/* 125 */       throw new NullPointerException("IProgressReporter can not be null");
/*     */     }
/*     */     
/* 128 */     this.pReporter = reporter;
/* 129 */     this.style = style;
/*     */     
/* 131 */     this.normalColor = Colors.blue;
/* 132 */     this.errorColor = Colors.colorError;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 137 */     setLayoutData(new GridData(4, 128, true, false));
/* 138 */     GridLayout gLayout = new GridLayout(2, false);
/* 139 */     gLayout.marginWidth = 25;
/* 140 */     gLayout.marginTop = 15;
/* 141 */     gLayout.marginBottom = 10;
/* 142 */     setLayout(gLayout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 147 */     createControls(this.pReporter.getProgressReport());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 152 */     addListener(11, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 154 */         ProgressReporterPanel.this.resizeContent();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 160 */     });
/* 161 */     this.pReporter.addListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int report(IProgressReport pReport)
/*     */   {
/* 170 */     return handleEvents(pReport);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createControls(IProgressReport pReport)
/*     */   {
/* 179 */     this.imageLabel = new Label(this, 0);
/* 180 */     Utils.setLayoutData(this.imageLabel, new GridData(1, 128, false, false, 1, 3));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 186 */     this.progressPanel = new Composite(this, 0);
/* 187 */     Utils.setLayoutData(this.progressPanel, new GridData(4, 4, true, false));
/* 188 */     GridLayout rightLayout = new GridLayout(4, false);
/* 189 */     rightLayout.marginHeight = 0;
/* 190 */     rightLayout.marginWidth = 0;
/* 191 */     this.progressPanel.setLayout(rightLayout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 197 */     this.nameLabel = new Label(this.progressPanel, 64);
/* 198 */     Utils.setLayoutData(this.nameLabel, new GridData(4, 16777216, true, false, 4, 1));
/*     */     
/*     */ 
/* 201 */     this.pBar = new AZProgressBar(this.progressPanel, pReport.isIndeterminate());
/* 202 */     Utils.setLayoutData(this.pBar, new GridData(4, 16777216, true, false));
/*     */     
/* 204 */     this.actionLabel_cancel = new Label(this.progressPanel, 0);
/* 205 */     Utils.setLayoutData(this.actionLabel_cancel, new GridData(16777224, 16777216, false, false));
/*     */     
/*     */ 
/* 208 */     this.actionLabel_remove = new Label(this.progressPanel, 0);
/* 209 */     Utils.setLayoutData(this.actionLabel_remove, new GridData(16777224, 16777216, false, false));
/*     */     
/*     */ 
/* 212 */     this.actionLabel_retry = new Label(this.progressPanel, 0);
/* 213 */     Utils.setLayoutData(this.actionLabel_retry, new GridData(16777224, 16777216, false, false));
/*     */     
/*     */ 
/* 216 */     this.statusLabel = new Label(this.progressPanel, 0);
/* 217 */     Utils.setLayoutData(this.statusLabel, new GridData(1, 16777216, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 223 */     createDetailSection(pReport);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 228 */     initControls(pReport);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 233 */     this.actionLabel_cancel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDown(MouseEvent e) {
/* 235 */         ProgressReporterPanel.this.pReporter.cancel();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 241 */     });
/* 242 */     this.actionLabel_retry.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDown(MouseEvent e) {
/* 244 */         ProgressReporterPanel.this.pReporter.retry();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 250 */     });
/* 251 */     this.actionLabel_remove.addMouseListener(new MouseAdapter()
/*     */     {
/*     */ 
/*     */       public void mouseDown(MouseEvent e)
/*     */       {
/* 256 */         ProgressReportingManager.getInstance().remove(ProgressReporterPanel.this.pReporter);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 261 */         ProgressReporterPanel.this.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initControls(IProgressReport pReport)
/*     */   {
/* 275 */     if (null != pReport.getImage()) {
/* 276 */       this.imageLabel.setImage(pReport.getImage());
/*     */     } else {
/* 278 */       this.imageLabel.setImage(getDisplay().getSystemImage(2));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 284 */     this.nameLabel.setText(formatForDisplay(pReport.getName()));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 290 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 291 */     imageLoader.setLabelImage(this.actionLabel_cancel, "progress_cancel");
/* 292 */     imageLoader.setLabelImage(this.actionLabel_remove, "progress_remove");
/* 293 */     imageLoader.setLabelImage(this.actionLabel_retry, "progress_retry");
/*     */     
/* 295 */     this.actionLabel_cancel.setToolTipText(MessageText.getString("Progress.reporting.action.label.cancel.tooltip"));
/* 296 */     this.actionLabel_remove.setToolTipText(MessageText.getString("Progress.reporting.action.label.remove.tooltip"));
/* 297 */     this.actionLabel_retry.setToolTipText(MessageText.getString("Progress.reporting.action.label.retry.tooltip"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 304 */     if (pReport.isInErrorState()) {
/* 305 */       updateStatusLabel(MessageText.getString("Progress.reporting.default.error"), true);
/*     */     }
/* 307 */     else if (pReport.isDone()) {
/* 308 */       updateStatusLabel(MessageText.getString("Progress.reporting.status.finished"), false);
/*     */     }
/* 310 */     else if (pReport.isCanceled()) {
/* 311 */       updateStatusLabel(MessageText.getString("Progress.reporting.status.canceled"), false);
/*     */     }
/* 313 */     else if (pReport.isIndeterminate()) {
/* 314 */       updateStatusLabel("∞", false);
/*     */     } else {
/* 316 */       updateStatusLabel(pReport.getPercentage() + "%", false);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 324 */     synchProgressBar(pReport);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 329 */     synchActionLabels(pReport);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void createDetailSection(IProgressReport pReport)
/*     */   {
/* 336 */     Label separator = new Label(this, 258);
/* 337 */     Utils.setLayoutData(separator, new GridData(4, 128, true, false));
/*     */     
/* 339 */     this.detailSection = new TwistieSection(this, 2);
/* 340 */     this.detailSection.setTitle(MessageText.getString("Progress.reporting.action.label.detail"));
/* 341 */     Composite sectionContent = this.detailSection.getContent();
/*     */     
/* 343 */     this.detailSectionData = new GridData(4, 4, true, true);
/* 344 */     Utils.setLayoutData(this.detailSection, this.detailSectionData);
/*     */     
/* 346 */     GridLayout sectionLayout = new GridLayout();
/* 347 */     sectionLayout.marginHeight = 0;
/* 348 */     sectionLayout.marginWidth = 0;
/* 349 */     sectionContent.setLayout(sectionLayout);
/* 350 */     this.detailSection.setEnabled(false);
/*     */     
/* 352 */     this.detailListWidget = new StyledText(sectionContent, 2624);
/*     */     
/* 354 */     Utils.setLayoutData(this.detailListWidget, new GridData(4, 4, true, true));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 361 */     IMessage[] messages = this.pReporter.getMessageHistory();
/*     */     
/*     */ 
/*     */ 
/* 365 */     for (int i = 0; i < messages.length; i++) {
/* 366 */       if (messages[i].getType() == 4) {
/* 367 */         appendToDetail(formatForDisplay(messages[i].getValue()), true);
/*     */       } else {
/* 369 */         appendToDetail(formatForDisplay(messages[i].getValue()), false);
/*     */       }
/*     */     }
/*     */     
/* 373 */     resizeDetailSection();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 378 */     this.detailSection.addTwistieListener(new ITwistieListener() {
/*     */       public void isCollapsed(boolean value) {
/* 380 */         ProgressReporterPanel.this.resizeDetailSection();
/* 381 */         ProgressReporterPanel.this.layout(true, true);
/*     */       }
/*     */     });
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
/*     */ 
/*     */ 
/*     */   private void resizeDetailSection()
/*     */   {
/* 398 */     Point computedSize = this.detailSection.computeSize(-1, -1);
/*     */     
/* 400 */     this.detailSectionData.heightHint = computedSize.y;
/*     */     
/* 402 */     if ((0x8 & this.style) != 0) {
/* 403 */       if (computedSize.y > this.maxPreferredDetailPanelHeight_Standalone) {
/* 404 */         this.detailSectionData.heightHint = this.maxPreferredDetailPanelHeight_Standalone;
/*     */       }
/* 406 */     } else if (computedSize.y > this.maxPreferredDetailPanelHeight) {
/* 407 */       this.detailSectionData.heightHint = this.maxPreferredDetailPanelHeight;
/*     */     }
/*     */     
/* 410 */     if (computedSize.x > this.maxPreferredWidth) {
/* 411 */       this.detailSectionData.widthHint = this.maxPreferredWidth;
/*     */     }
/*     */   }
/*     */   
/*     */   public Point computeSize(int hint, int hint2, boolean changed)
/*     */   {
/* 417 */     Point newSize = super.computeSize(hint, hint2, changed);
/*     */     
/* 419 */     if (newSize.x > this.maxPreferredWidth) {
/* 420 */       newSize.x = this.maxPreferredWidth;
/*     */     }
/* 422 */     return newSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int handleEvents(final IProgressReport pReport)
/*     */   {
/* 431 */     if ((null == pReport) || (isDisposed()) || (null == getDisplay())) {
/* 432 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 442 */     switch (pReport.getReportType())
/*     */     {
/*     */     case 6: 
/* 445 */       getDisplay().asyncExec(new Runnable() {
/*     */         public void run() {
/* 447 */           if ((null != ProgressReporterPanel.this.nameLabel) && (!ProgressReporterPanel.this.nameLabel.isDisposed())) {
/* 448 */             ProgressReporterPanel.this.nameLabel.setText(ProgressReporterPanel.this.formatForDisplay(pReport.getName()));
/*     */           }
/* 450 */           if (pReport.isIndeterminate()) {
/* 451 */             ProgressReporterPanel.this.updateStatusLabel("∞", false);
/*     */           } else {
/* 453 */             ProgressReporterPanel.this.updateStatusLabel(pReport.getPercentage() + "%", false);
/*     */           }
/* 455 */           ProgressReporterPanel.this.appendToDetail(pReport.getMessage(), false);
/* 456 */           ProgressReporterPanel.this.appendToDetail(pReport.getDetailMessage(), false);
/* 457 */           ProgressReporterPanel.this.synchProgressBar(pReport);
/* 458 */           ProgressReporterPanel.this.synchActionLabels(pReport);
/* 459 */           ProgressReporterPanel.this.resizeContent();
/*     */         }
/* 461 */       });
/* 462 */       break;
/*     */     case 1: 
/* 464 */       getDisplay().asyncExec(new Runnable() {
/*     */         public void run() {
/* 466 */           if (pReport.getReporter().getCancelCloses()) {
/* 467 */             ProgressReporterPanel.this.dispose();
/*     */           } else {
/* 469 */             ProgressReporterPanel.this.synchProgressBar(pReport);
/* 470 */             ProgressReporterPanel.this.updateStatusLabel(MessageText.getString("Progress.reporting.status.canceled"), false);
/*     */             
/*     */ 
/* 473 */             ProgressReporterPanel.this.appendToDetail(pReport.getMessage(), false);
/* 474 */             ProgressReporterPanel.this.synchActionLabels(pReport);
/* 475 */             ProgressReporterPanel.this.resizeContent();
/*     */           }
/*     */         }
/* 478 */       });
/* 479 */       break;
/*     */     case 2: 
/* 481 */       getDisplay().asyncExec(new Runnable()
/*     */       {
/*     */         public void run() {
/* 484 */           if ((ProgressReporterPanel.this.style & 0x2) != 0) {
/* 485 */             ProgressReporterPanel.this.dispose();
/*     */           }
/*     */           else {
/* 488 */             ProgressReporterPanel.this.synchProgressBar(pReport);
/* 489 */             ProgressReporterPanel.this.updateStatusLabel(MessageText.getString("Progress.reporting.status.finished"), false);
/*     */             
/*     */ 
/* 492 */             ProgressReporterPanel.this.appendToDetail(MessageText.getString("Progress.reporting.status.finished"), false);
/*     */             
/*     */ 
/* 495 */             ProgressReporterPanel.this.synchActionLabels(pReport);
/* 496 */             ProgressReporterPanel.this.resizeContent();
/*     */ 
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */ 
/* 503 */       });
/* 504 */       return 1;
/*     */     case 3: 
/* 506 */       getDisplay().asyncExec(new Runnable() {
/*     */         public void run() {
/* 508 */           if ((null != ProgressReporterPanel.this.pBar) && (!ProgressReporterPanel.this.pBar.isDisposed())) {
/* 509 */             ProgressReporterPanel.this.pBar.setIndeterminate(pReport.isIndeterminate());
/*     */           }
/*     */         }
/* 512 */       });
/* 513 */       break;
/*     */     case 4: 
/* 515 */       getDisplay().asyncExec(new Runnable() {
/*     */         public void run() {
/* 517 */           ProgressReporterPanel.this.updateStatusLabel(MessageText.getString("Progress.reporting.default.error"), true);
/*     */           
/* 519 */           ProgressReporterPanel.this.appendToDetail(pReport.getErrorMessage(), true);
/* 520 */           ProgressReporterPanel.this.synchActionLabels(pReport);
/* 521 */           ProgressReporterPanel.this.synchProgressBar(pReport);
/* 522 */           ProgressReporterPanel.this.resizeContent();
/*     */         }
/* 524 */       });
/* 525 */       break;
/*     */     
/*     */     case 5: 
/* 528 */       getDisplay().asyncExec(new Runnable() {
/*     */         public void run() {
/* 530 */           ProgressReporterPanel.this.lastStatusError = null;
/* 531 */           ProgressReporterPanel.this.updateStatusLabel(pReport.getMessage(), false);
/* 532 */           ProgressReporterPanel.this.appendToDetail(MessageText.getString("Progress.reporting.status.retrying"), false);
/*     */           
/*     */ 
/* 535 */           ProgressReporterPanel.this.synchActionLabels(pReport);
/* 536 */           ProgressReporterPanel.this.synchProgressBar(pReport);
/* 537 */           ProgressReporterPanel.this.resizeContent();
/*     */         }
/*     */         
/* 540 */       });
/* 541 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 546 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void synchProgressBar(IProgressReport pReport)
/*     */   {
/* 554 */     if ((null == this.pBar) || (this.pBar.isDisposed()) || (null == pReport)) {
/* 555 */       return;
/*     */     }
/*     */     
/* 558 */     if (pReport.isInErrorState()) {
/* 559 */       this.pBar.setIndeterminate(false);
/* 560 */       this.pBar.setSelection(pReport.getMinimum());
/*     */     } else {
/* 562 */       this.pBar.setIndeterminate(pReport.isIndeterminate());
/* 563 */       if (!pReport.isIndeterminate()) {
/* 564 */         this.pBar.setMinimum(pReport.getMinimum());
/* 565 */         this.pBar.setMaximum(pReport.getMaximum());
/*     */       }
/* 567 */       this.pBar.setSelection(pReport.getSelection());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateStatusLabel(String text, boolean showAsError)
/*     */   {
/* 578 */     if ((null == this.statusLabel) || (this.statusLabel.isDisposed())) {
/* 579 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 584 */     if (showAsError) {
/* 585 */       this.lastStatusError = text;
/*     */     }
/* 587 */     if (this.lastStatusError != null) {
/* 588 */       showAsError = true;
/* 589 */       text = this.lastStatusError;
/*     */     }
/*     */     
/* 592 */     this.statusLabel.setText(formatForDisplay(text));
/* 593 */     if (!showAsError) {
/* 594 */       this.statusLabel.setForeground(this.normalColor);
/*     */     } else {
/* 596 */       this.statusLabel.setForeground(this.errorColor);
/*     */     }
/* 598 */     this.statusLabel.update();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void synchActionLabels(IProgressReport pReport)
/*     */   {
/* 607 */     if ((null == this.actionLabel_remove) || (null == this.actionLabel_cancel) || (null == this.actionLabel_retry) || (this.actionLabel_remove.isDisposed()) || (this.actionLabel_cancel.isDisposed()) || (this.actionLabel_retry.isDisposed()))
/*     */     {
/*     */ 
/*     */ 
/* 611 */       return;
/*     */     }
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
/* 638 */     showActionLabel(this.actionLabel_cancel, false);
/* 639 */     showActionLabel(this.actionLabel_remove, false);
/* 640 */     showActionLabel(this.actionLabel_retry, false);
/*     */     
/* 642 */     if (pReport.isDone()) {
/* 643 */       showActionLabel(this.actionLabel_remove, true);
/* 644 */     } else if (pReport.isInErrorState()) {
/* 645 */       if (pReport.isRetryAllowed()) {
/* 646 */         showActionLabel(this.actionLabel_retry, true);
/* 647 */         showActionLabel(this.actionLabel_remove, true);
/*     */       } else {
/* 649 */         showActionLabel(this.actionLabel_remove, true);
/*     */       }
/*     */     }
/* 652 */     else if (pReport.isCanceled()) {
/* 653 */       if (pReport.isRetryAllowed()) {
/* 654 */         showActionLabel(this.actionLabel_retry, true);
/* 655 */         showActionLabel(this.actionLabel_remove, true);
/*     */       } else {
/* 657 */         showActionLabel(this.actionLabel_remove, true);
/*     */       }
/*     */     }
/*     */     else {
/* 661 */       showActionLabel(this.actionLabel_cancel, true);
/* 662 */       this.actionLabel_cancel.setEnabled(pReport.isCancelAllowed());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void showActionLabel(Label label, boolean showIt)
/*     */   {
/* 673 */     ((GridData)label.getLayoutData()).widthHint = (showIt ? 16 : 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void resizeContent()
/*     */   {
/* 680 */     if (!isDisposed()) {
/* 681 */       layout(true, true);
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
/*     */   private String formatForDisplay(String string)
/*     */   {
/* 694 */     string = null == string ? "" : string;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 699 */     return string.replaceAll("&", "&&");
/*     */   }
/*     */   
/*     */   public void addTwistieListener(ITwistieListener listener)
/*     */   {
/* 704 */     this.detailSection.addTwistieListener(listener);
/*     */   }
/*     */   
/*     */   public void removeTwistieListener(ITwistieListener listener) {
/* 708 */     this.detailSection.removeTwistieListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void appendToDetail(String value, boolean isError)
/*     */   {
/* 718 */     if ((null == value) || (value.length() < 1)) {
/* 719 */       return;
/*     */     }
/*     */     
/* 722 */     if ((null == this.detailListWidget) || (this.detailListWidget.isDisposed())) {
/* 723 */       return;
/*     */     }
/*     */     
/* 726 */     int charCount = this.detailListWidget.getCharCount();
/* 727 */     this.detailListWidget.append(value + "\n");
/* 728 */     if (isError) {
/* 729 */       StyleRange style2 = new StyleRange();
/* 730 */       style2.start = charCount;
/* 731 */       style2.length = value.length();
/* 732 */       style2.foreground = this.errorColor;
/* 733 */       this.detailListWidget.setStyleRange(style2);
/*     */     }
/* 735 */     this.detailSection.setEnabled(true);
/* 736 */     if (isError) {
/* 737 */       this.detailSection.setCollapsed(false);
/* 738 */       this.detailListWidget.setSelection(this.detailListWidget.getCharCount(), this.detailListWidget.getCharCount());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IProgressReporter getProgressReporter()
/*     */   {
/* 747 */     return this.pReporter;
/*     */   }
/*     */   
/*     */   public int getStyle() {
/* 751 */     return this.style;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/ProgressReporterPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */