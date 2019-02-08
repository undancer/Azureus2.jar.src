/*     */ package org.gudy.azureus2.ui.swt.progress;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.ControlAdapter;
/*     */ import org.eclipse.swt.events.ControlEvent;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.twistie.ITwistieListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProgressReporterWindow
/*     */   implements IProgressReportConstants, ITwistieListener, DisposeListener
/*     */ {
/*     */   private Shell shell;
/*     */   private ScrolledComposite scrollable;
/*     */   private Composite scrollChild;
/*     */   private IProgressReporter[] pReporters;
/*  69 */   private static final ArrayList reportersRegistry = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */   private static boolean isShowingEmpty = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  80 */   private int defaultShellWidth = 500;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  85 */   private int initialMaxNumberOfPanels = 3;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int style;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  95 */   private boolean isAutoRemove = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ProgressReporterWindow(IProgressReporter pReporter, int style)
/*     */   {
/* 102 */     this.style = style;
/* 103 */     if (null != pReporter) {
/* 104 */       this.pReporters = new IProgressReporter[] { pReporter };
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 109 */       this.pReporters = new IProgressReporter[0];
/*     */     }
/*     */     
/* 112 */     createControls();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ProgressReporterWindow(IProgressReporter[] pReporters, int style)
/*     */   {
/* 120 */     this.style = style;
/* 121 */     if (null != pReporters) {
/* 122 */       this.pReporters = pReporters;
/*     */     }
/*     */     else {
/* 125 */       this.pReporters = new IProgressReporter[0];
/*     */     }
/*     */     
/* 128 */     createControls();
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
/*     */ 
/*     */   public static void open(IProgressReporter pReporter, int style)
/*     */   {
/* 144 */     new ProgressReporterWindow(pReporter, style).openWindow();
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
/*     */ 
/*     */   public static void open(IProgressReporter[] pReporters, int style)
/*     */   {
/* 160 */     new ProgressReporterWindow(pReporters, style).openWindow();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isShowingEmpty()
/*     */   {
/* 168 */     return isShowingEmpty;
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
/*     */   public static boolean isOpened(IProgressReporter pReporter)
/*     */   {
/* 181 */     return reportersRegistry.contains(pReporter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createControls()
/*     */   {
/* 189 */     int shellStyle = 2160;
/* 190 */     if ((this.style & 0x4) != 0) {
/* 191 */       shellStyle |= 0x10000;
/*     */     }
/*     */     
/* 194 */     this.shell = ShellFactory.createMainShell(shellStyle);
/* 195 */     this.shell.setText(MessageText.getString("progress.window.title"));
/*     */     
/* 197 */     Utils.setShellIcon(this.shell);
/*     */     
/* 199 */     GridLayout gLayout = new GridLayout();
/* 200 */     gLayout.marginHeight = 0;
/* 201 */     gLayout.marginWidth = 0;
/* 202 */     this.shell.setLayout(gLayout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 207 */     this.scrollable = new ScrolledComposite(this.shell, 512);
/* 208 */     this.scrollable.setLayoutData(new GridData(4, 4, true, true));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 213 */     this.scrollChild = new Composite(this.scrollable, 0);
/*     */     
/* 215 */     GridLayout gLayoutChild = new GridLayout();
/* 216 */     gLayoutChild.marginHeight = 0;
/* 217 */     gLayoutChild.marginWidth = 0;
/* 218 */     gLayoutChild.verticalSpacing = 0;
/* 219 */     this.scrollChild.setLayout(gLayoutChild);
/* 220 */     this.scrollable.setContent(this.scrollChild);
/* 221 */     this.scrollable.setExpandVertical(true);
/* 222 */     this.scrollable.setExpandHorizontal(true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 227 */     this.scrollable.addControlListener(new ControlAdapter() {
/*     */       public void controlResized(ControlEvent e) {
/* 229 */         Rectangle r = ProgressReporterWindow.this.scrollable.getClientArea();
/* 230 */         ProgressReporterWindow.this.scrollable.setMinSize(ProgressReporterWindow.this.scrollChild.computeSize(r.width, -1));
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 236 */     });
/* 237 */     this.shell.addListener(21, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 245 */         Control[] controls = ProgressReporterWindow.this.scrollChild.getChildren();
/* 246 */         for (int i = 0; i < controls.length; i++) {
/* 247 */           if ((controls[i] instanceof ProgressReporterPanel)) {
/* 248 */             ((ProgressReporterPanel)controls[i]).removeDisposeListener(ProgressReporterWindow.this);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 255 */         for (int i = 0; i < ProgressReporterWindow.this.pReporters.length; i++) {
/* 256 */           ProgressReporterWindow.reportersRegistry.remove(ProgressReporterWindow.this.pReporters[i]);
/*     */         }
/*     */         
/* 259 */         ProgressReporterWindow.access$402(false);
/*     */       }
/*     */     });
/*     */     
/* 263 */     if (this.pReporters.length == 0) {
/* 264 */       createEmptyPanel();
/*     */     } else {
/* 266 */       createPanels();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 272 */     if ((this.style & 0x20) != 0) {
/* 273 */       createToolbar();
/*     */     }
/* 275 */     this.isAutoRemove = COConfigurationManager.getBooleanParameter("auto_remove_inactive_items");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createToolbar()
/*     */   {
/* 283 */     Composite toolbarPanel = new Composite(this.shell, 0);
/* 284 */     toolbarPanel.setLayoutData(new GridData(4, 4, false, false));
/* 285 */     GridLayout gLayout = new GridLayout(3, false);
/* 286 */     gLayout.marginWidth = 25;
/* 287 */     gLayout.marginTop = 0;
/* 288 */     gLayout.marginBottom = 0;
/* 289 */     toolbarPanel.setLayout(gLayout);
/*     */     
/* 291 */     final Button autoClearButton = new Button(toolbarPanel, 32);
/* 292 */     autoClearButton.setText(MessageText.getString("Progress.reporting.window.remove.auto"));
/* 293 */     autoClearButton.setToolTipText(MessageText.getString("Progress.reporting.window.remove.auto.tooltip"));
/* 294 */     autoClearButton.setLayoutData(new GridData(1, 16777216, false, false));
/*     */     
/*     */ 
/* 297 */     autoClearButton.setSelection(COConfigurationManager.getBooleanParameter("auto_remove_inactive_items"));
/*     */     
/* 299 */     Label dummy = new Label(toolbarPanel, 0);
/* 300 */     dummy.setLayoutData(new GridData(4, 4, true, false));
/*     */     
/* 302 */     final Button clearInActiveButton = new Button(toolbarPanel, 0);
/* 303 */     clearInActiveButton.setText(MessageText.getString("Progress.reporting.window.remove.now"));
/* 304 */     clearInActiveButton.setToolTipText(MessageText.getString("Progress.reporting.window.remove.now.tooltip"));
/* 305 */     clearInActiveButton.setLayoutData(new GridData(16777224, 16777216, false, false));
/*     */     
/* 307 */     clearInActiveButton.setEnabled(!COConfigurationManager.getBooleanParameter("auto_remove_inactive_items"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 312 */     autoClearButton.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 314 */         COConfigurationManager.setParameter("auto_remove_inactive_items", autoClearButton.getSelection());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 320 */         clearInActiveButton.setEnabled(!autoClearButton.getSelection());
/*     */         
/* 322 */         ProgressReporterWindow.this.isAutoRemove = autoClearButton.getSelection();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 327 */         if (ProgressReporterWindow.this.isAutoRemove) {
/* 328 */           ProgressReporterWindow.this.removeInActivePanels();
/*     */         }
/*     */       }
/*     */       
/*     */       public void widgetDefaultSelected(SelectionEvent e)
/*     */       {
/* 334 */         widgetSelected(e);
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 341 */     });
/* 342 */     clearInActiveButton.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 344 */         ProgressReporterWindow.this.removeInActivePanels();
/*     */       }
/*     */       
/*     */       public void widgetDefaultSelected(SelectionEvent e) {
/* 348 */         widgetSelected(e);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void removeInActivePanels()
/*     */   {
/* 358 */     Control[] controls = this.scrollChild.getChildren();
/* 359 */     for (int i = 0; i < controls.length; i++) {
/* 360 */       if ((null != controls[i]) && (!controls[i].isDisposed()))
/*     */       {
/*     */ 
/* 363 */         if ((controls[i] instanceof ProgressReporterPanel)) {
/* 364 */           IProgressReporter pReporter = ((ProgressReporterPanel)controls[i]).getProgressReporter();
/* 365 */           if (!pReporter.getProgressReport().isActive())
/*     */           {
/* 367 */             if (!pReporter.getProgressReport().isInErrorState())
/*     */             {
/* 369 */               ProgressReportingManager.getInstance().remove(pReporter);
/*     */               
/* 371 */               controls[i].dispose();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void createEmptyPanel()
/*     */   {
/* 382 */     Composite emptyPanel = new Composite(this.scrollChild, 2048);
/* 383 */     GridData gData = new GridData(4, 4, true, true);
/* 384 */     gData.heightHint = 100;
/* 385 */     emptyPanel.setLayoutData(gData);
/* 386 */     emptyPanel.setLayout(new GridLayout());
/* 387 */     Label nothingToDisplay = new Label(emptyPanel, 0);
/* 388 */     nothingToDisplay.setLayoutData(new GridData(4, 4, true, true));
/* 389 */     nothingToDisplay.setText(MessageText.getString("Progress.reporting.no.reports.to.display"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 394 */     isShowingEmpty = true;
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
/*     */   private void openWindow()
/*     */   {
/* 409 */     Control[] controls = this.scrollChild.getChildren();
/* 410 */     for (int i = this.initialMaxNumberOfPanels; i < controls.length; i++) {
/* 411 */       ((GridData)controls[i].getLayoutData()).exclude = true;
/*     */     }
/*     */     
/* 414 */     Point p = this.shell.computeSize(this.defaultShellWidth, -1);
/*     */     
/* 416 */     for (int i = 0; i < controls.length; i++) {
/* 417 */       ((GridData)controls[i].getLayoutData()).exclude = false;
/*     */     }
/* 419 */     formatLastPanel(null);
/* 420 */     this.scrollChild.layout();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 425 */     if (!this.shell.getSize().equals(p)) {
/* 426 */       this.shell.setSize(p);
/* 427 */       this.shell.layout(false);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 434 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 435 */     if (null == uiFunctions)
/*     */     {
/*     */ 
/*     */ 
/* 439 */       Utils.centreWindow(this.shell);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 444 */       Utils.centerWindowRelativeTo(this.shell, uiFunctions.getMainShell());
/*     */     }
/*     */     
/* 447 */     this.shell.open();
/*     */   }
/*     */   
/*     */   private void createPanels()
/*     */   {
/* 452 */     int size = this.pReporters.length;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 457 */     if (size < 2) {
/* 458 */       this.style |= 0x8;
/*     */     }
/*     */     
/* 461 */     for (int i = 0; i < size; i++) {
/* 462 */       if (null != this.pReporters[i])
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 467 */         reportersRegistry.add(this.pReporters[i]);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 472 */         ProgressReporterPanel panel = new ProgressReporterPanel(this.scrollChild, this.pReporters[i], this.style | 0x10);
/*     */         
/*     */ 
/* 475 */         panel.setLayoutData(new GridData(4, 4, true, false));
/*     */         
/* 477 */         panel.addTwistieListener(this);
/* 478 */         panel.addDisposeListener(this);
/* 479 */         this.pReporters[i].addListener(new AutoRemoveListener(panel, null));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 484 */     formatLastPanel(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void formatLastPanel(ProgressReporterPanel panelToIgnore)
/*     */   {
/* 493 */     Control[] controls = this.scrollChild.getChildren();
/*     */     
/* 495 */     for (int i = controls.length - 1; i >= 0; i--) {
/* 496 */       if (!controls[i].equals(panelToIgnore)) {
/* 497 */         ((GridData)controls[i].getLayoutData()).grabExcessVerticalSpace = true;
/* 498 */         break;
/*     */       }
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
/*     */   private void removeReporter(IProgressReporter reporter)
/*     */   {
/* 512 */     reportersRegistry.remove(reporter);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 518 */     int IDX = Arrays.binarySearch(this.pReporters, reporter);
/* 519 */     if (IDX >= 0) {
/* 520 */       IProgressReporter[] rps = new IProgressReporter[this.pReporters.length - 1];
/* 521 */       for (int i = 0; i < rps.length; i++) {
/* 522 */         rps[i] = this.pReporters[i];
/*     */       }
/* 524 */       this.pReporters = rps;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void isCollapsed(boolean value)
/*     */   {
/* 533 */     if ((null != this.shell) && (!this.shell.isDisposed())) {
/* 534 */       this.scrollable.setRedraw(false);
/* 535 */       Rectangle r = this.scrollable.getClientArea();
/* 536 */       this.scrollable.setMinSize(this.scrollChild.computeSize(r.width, -1));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 541 */       if (this.pReporters.length == 1) {
/* 542 */         Point p = this.shell.computeSize(this.defaultShellWidth, -1);
/* 543 */         if (this.shell.getSize().y != p.y) {
/* 544 */           p.x = this.shell.getSize().x;
/* 545 */           this.shell.setSize(p);
/*     */         }
/*     */       }
/*     */       
/* 549 */       this.scrollable.layout();
/* 550 */       this.scrollable.setRedraw(true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void widgetDisposed(DisposeEvent e)
/*     */   {
/* 560 */     if ((e.widget instanceof ProgressReporterPanel)) {
/* 561 */       ProgressReporterPanel panel = (ProgressReporterPanel)e.widget;
/* 562 */       removeReporter(panel.pReporter);
/*     */       
/* 564 */       panel.removeTwistieListener(this);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 569 */       ((GridData)panel.getLayoutData()).exclude = true;
/* 570 */       panel.setVisible(false);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 575 */       if (this.pReporters.length == 0) {
/* 576 */         if ((this.style & 0x2) != 0) {
/* 577 */           if ((null != this.shell) && (!this.shell.isDisposed())) {
/* 578 */             this.shell.close();
/*     */           }
/*     */         } else {
/* 581 */           createEmptyPanel();
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 590 */         formatLastPanel(panel);
/*     */       }
/*     */       
/* 593 */       if ((null != this.shell) && (!this.shell.isDisposed())) {
/* 594 */         this.shell.layout(true, true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private class AutoRemoveListener
/*     */     implements IProgressReporterListener
/*     */   {
/* 608 */     private ProgressReporterPanel panel = null;
/*     */     
/*     */     private AutoRemoveListener(ProgressReporterPanel panel) {
/* 611 */       this.panel = panel;
/*     */     }
/*     */     
/*     */     public int report(IProgressReport progressReport)
/*     */     {
/* 616 */       if ((ProgressReporterWindow.this.isAutoRemove) && (!progressReport.isActive()) && (!progressReport.isInErrorState())) {
/* 617 */         if ((null != this.panel) && (!this.panel.isDisposed())) {
/* 618 */           ProgressReportingManager.getInstance().remove(this.panel.getProgressReporter());
/*     */           
/*     */ 
/* 621 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/* 623 */               ProgressReporterWindow.AutoRemoveListener.this.panel.dispose();
/*     */             }
/*     */           });
/*     */         }
/* 627 */         return 1;
/*     */       }
/* 629 */       return 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/ProgressReporterWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */