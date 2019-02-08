/*     */ package org.gudy.azureus2.ui.swt.update;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.SashForm;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AERunnableBoolean;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerDecisionListener;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkArea;
/*     */ import org.gudy.azureus2.ui.swt.components.StringListChooser;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpdateWindow
/*     */   implements ResourceDownloaderListener
/*     */ {
/*     */   private UpdateMonitor update_monitor;
/*     */   private UpdateCheckInstance check_instance;
/*     */   private int check_type;
/*     */   Display display;
/*     */   Shell updateWindow;
/*     */   Table table;
/*     */   LinkArea link_area;
/*     */   ProgressBar progress;
/*     */   Label status;
/*     */   Button btnOk;
/*     */   Listener lOk;
/*     */   Button btnCancel;
/*     */   Listener lCancel;
/*     */   boolean hasMandatoryUpdates;
/*     */   boolean restartRequired;
/*     */   private long totalDownloadSize;
/*     */   private List downloaders;
/*     */   private Iterator iterDownloaders;
/*     */   private BrowserWrapper browser;
/*     */   private static final int COL_NAME = 0;
/*     */   private static final int COL_OLD_VERSION = 1;
/*     */   private static final int COL_NEW_VERSION = 2;
/*     */   private static final int COL_SIZE = 3;
/*     */   private Map downloadersToData;
/*     */   
/*     */   public UpdateWindow(UpdateMonitor _update_monitor, AzureusCore _azureus_core, UpdateCheckInstance _check_instance)
/*     */   {
/* 104 */     this.update_monitor = _update_monitor;
/* 105 */     this.check_instance = _check_instance;
/*     */     
/* 107 */     this.check_type = this.check_instance.getType();
/*     */     
/* 109 */     this.check_instance.addDecisionListener(new UpdateManagerDecisionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public Object decide(Update update, int decision_type, String decision_name, String decision_description, Object decision_data)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 120 */         if (decision_type == 0)
/*     */         {
/* 122 */           String[] options = (String[])decision_data;
/*     */           
/* 124 */           Shell shell = UpdateWindow.this.updateWindow;
/*     */           
/* 126 */           if (shell == null)
/*     */           {
/* 128 */             Debug.out("Shell doesn't exist");
/*     */             
/* 130 */             return null;
/*     */           }
/*     */           
/* 133 */           StringListChooser chooser = new StringListChooser(shell);
/*     */           
/* 135 */           chooser.setTitle(decision_name);
/* 136 */           chooser.setText(decision_description);
/*     */           
/* 138 */           for (int i = 0; i < options.length; i++)
/*     */           {
/* 140 */             chooser.addOption(options[i]);
/*     */           }
/*     */           
/* 143 */           String result = chooser.open();
/*     */           
/* 145 */           return result;
/*     */         }
/*     */         
/* 148 */         return null;
/*     */       }
/*     */       
/* 151 */     });
/* 152 */     this.updateWindow = null;
/* 153 */     this.display = SWTThread.getInstance().getDisplay();
/*     */     
/* 155 */     Utils.execSWTThreadWithBool("UpdateWindow", new AERunnableBoolean() {
/*     */       public boolean runSupport() {
/* 157 */         UpdateWindow.this.buildWindow();
/* 158 */         return true;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void buildWindow()
/*     */   {
/* 165 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 166 */       return;
/*     */     }
/* 168 */     Utils.waitForModals();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 173 */     this.updateWindow = ShellFactory.createMainShell(2288);
/*     */     
/* 175 */     this.updateWindow.addListener(21, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 177 */         UpdateWindow.this.dispose();
/*     */       }
/*     */       
/* 180 */     });
/* 181 */     Utils.setShellIcon(this.updateWindow);
/*     */     
/* 183 */     String res_prefix = "swt.";
/*     */     
/* 185 */     if (this.check_type == 1)
/*     */     {
/* 187 */       res_prefix = res_prefix + "install.window";
/*     */     }
/* 189 */     else if (this.check_type == 3)
/*     */     {
/* 191 */       res_prefix = res_prefix + "uninstall.window";
/*     */     }
/*     */     else
/*     */     {
/* 195 */       res_prefix = "UpdateWindow";
/*     */     }
/*     */     
/* 198 */     Messages.setLanguageText(this.updateWindow, res_prefix + ".title");
/*     */     
/* 200 */     FormLayout layout = new FormLayout();
/*     */     try {
/* 202 */       layout.spacing = 5;
/*     */     } catch (NoSuchFieldError e) {}
/* 204 */     layout.marginHeight = 10;
/* 205 */     layout.marginWidth = 10;
/*     */     
/* 207 */     this.updateWindow.setLayout(layout);
/*     */     
/* 209 */     Label lHeaderText = new Label(this.updateWindow, 64);
/* 210 */     Messages.setLanguageText(lHeaderText, res_prefix + ".header");
/* 211 */     FormData formData = new FormData();
/* 212 */     formData.left = new FormAttachment(0, 0);
/* 213 */     formData.right = new FormAttachment(100, 0);
/* 214 */     formData.top = new FormAttachment(0, 0);
/* 215 */     lHeaderText.setLayoutData(formData);
/*     */     
/* 217 */     SashForm sash = new SashForm(this.updateWindow, 512);
/*     */     
/* 219 */     this.table = new Table(sash, 67620);
/* 220 */     String[] names = { "name", "currentversion", "version", "size" };
/* 221 */     int[] sizes = { 350, 100, 100, 100 };
/* 222 */     for (int i = 0; i < names.length; i++) {
/* 223 */       TableColumn column = new TableColumn(this.table, i == 0 ? 16384 : 131072);
/* 224 */       Messages.setLanguageText(column, "UpdateWindow.columns." + names[i]);
/* 225 */       column.setWidth(Utils.adjustPXForDPI(sizes[i]));
/*     */     }
/* 227 */     this.table.setHeaderVisible(true);
/*     */     
/* 229 */     this.table.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 231 */         UpdateWindow.this.rowSelected();
/*     */       }
/*     */       
/* 234 */     });
/* 235 */     Composite cInfoArea = new Composite(sash, 0);
/* 236 */     cInfoArea.setLayout(new FormLayout());
/*     */     
/* 238 */     this.link_area = new LinkArea(cInfoArea);
/* 239 */     FormData fd = new FormData();
/* 240 */     fd.top = new FormAttachment(0, 0);
/* 241 */     fd.bottom = new FormAttachment(100, 0);
/* 242 */     fd.right = new FormAttachment(100, 0);
/* 243 */     fd.left = new FormAttachment(0, 0);
/* 244 */     this.link_area.getComponent().setLayoutData(fd);
/*     */     try
/*     */     {
/* 247 */       this.browser = Utils.createSafeBrowser(cInfoArea, 2048);
/* 248 */       if (this.browser != null) {
/* 249 */         fd = new FormData();
/* 250 */         fd.top = new FormAttachment(0, 0);
/* 251 */         fd.bottom = new FormAttachment(100, 0);
/* 252 */         fd.right = new FormAttachment(100, 0);
/* 253 */         fd.left = new FormAttachment(0, 0);
/* 254 */         this.browser.setLayoutData(fd);
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {}
/*     */     
/* 259 */     this.progress = new ProgressBar(this.updateWindow, 0);
/* 260 */     this.progress.setMinimum(0);
/* 261 */     this.progress.setMaximum(100);
/* 262 */     this.progress.setSelection(0);
/*     */     
/* 264 */     this.status = new Label(this.updateWindow, 0);
/*     */     
/*     */ 
/* 267 */     Composite cButtons = new Composite(this.updateWindow, 0);
/* 268 */     FillLayout fl = new FillLayout(256);
/* 269 */     fl.spacing = 3;
/* 270 */     cButtons.setLayout(fl);
/*     */     
/* 272 */     this.btnOk = new Button(cButtons, 8);
/* 273 */     Messages.setLanguageText(this.btnOk, res_prefix + ".ok");
/*     */     
/* 275 */     this.updateWindow.setDefaultButton(this.btnOk);
/* 276 */     this.lOk = new Listener() {
/*     */       public void handleEvent(Event e) {
/* 278 */         UpdateWindow.this.update();
/*     */       }
/*     */       
/* 281 */     };
/* 282 */     this.btnOk.addListener(13, this.lOk);
/* 283 */     this.btnOk.setEnabled(false);
/*     */     
/* 285 */     this.btnCancel = new Button(cButtons, 8);
/*     */     
/* 287 */     Messages.setLanguageText(this.btnCancel, "UpdateWindow.cancel");
/*     */     
/* 289 */     this.lCancel = new Listener() {
/*     */       public void handleEvent(Event e) {
/* 291 */         UpdateWindow.this.dispose();
/* 292 */         UpdateWindow.this.check_instance.cancel();
/*     */       }
/* 294 */     };
/* 295 */     this.btnCancel.addListener(13, this.lCancel);
/*     */     
/* 297 */     this.updateWindow.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 299 */         if (e.character == '\033') {
/* 300 */           UpdateWindow.this.dispose();
/* 301 */           UpdateWindow.this.check_instance.cancel();
/*     */         }
/*     */         
/*     */       }
/* 305 */     });
/* 306 */     this.updateWindow.addDisposeListener(new DisposeListener()
/*     */     {
/*     */       public void widgetDisposed(DisposeEvent arg0)
/*     */       {
/* 310 */         if (!UpdateWindow.this.check_instance.isCancelled()) {
/* 311 */           UpdateWindow.this.check_instance.cancel();
/*     */         }
/*     */         
/*     */       }
/* 315 */     });
/* 316 */     formData = new FormData();
/* 317 */     formData.left = new FormAttachment(0, 0);
/* 318 */     formData.right = new FormAttachment(100, 0);
/* 319 */     formData.top = new FormAttachment(lHeaderText);
/* 320 */     formData.bottom = new FormAttachment(this.progress);
/* 321 */     sash.setLayoutData(formData);
/*     */     
/* 323 */     formData = new FormData();
/* 324 */     formData.left = new FormAttachment(0, 0);
/* 325 */     formData.right = new FormAttachment(100, 0);
/* 326 */     formData.bottom = new FormAttachment(this.status);
/* 327 */     this.progress.setLayoutData(formData);
/*     */     
/* 329 */     formData = new FormData();
/* 330 */     formData.left = new FormAttachment(0, 0);
/* 331 */     formData.right = new FormAttachment(100, 0);
/* 332 */     formData.bottom = new FormAttachment(cButtons);
/* 333 */     this.status.setLayoutData(formData);
/*     */     
/* 335 */     formData = new FormData();
/* 336 */     formData.right = new FormAttachment(100, 0);
/* 337 */     formData.bottom = new FormAttachment(100, 0);
/* 338 */     cButtons.setLayoutData(formData);
/*     */     
/* 340 */     sash.setWeights(new int[] { 25, 75 });
/*     */     
/* 342 */     this.updateWindow.setSize(700, 450);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void rowSelected()
/*     */   {
/* 349 */     checkMandatory();
/* 350 */     checkRestartNeeded();
/* 351 */     TableItem[] items = this.table.getSelection();
/* 352 */     if (items.length == 0) return;
/* 353 */     Update update = (Update)items[0].getData();
/*     */     
/* 355 */     String desciptionURL = update.getDesciptionURL();
/* 356 */     if ((desciptionURL != null) && (this.browser != null)) {
/* 357 */       this.browser.setUrl(desciptionURL);
/* 358 */       this.browser.setVisible(true);
/* 359 */       this.link_area.getComponent().setVisible(false);
/*     */     } else {
/* 361 */       if (this.browser != null) {
/* 362 */         this.browser.setVisible(false);
/*     */       }
/* 364 */       this.link_area.getComponent().setVisible(true);
/*     */       
/* 366 */       String[] descriptions = update.getDescription();
/*     */       
/* 368 */       this.link_area.reset();
/*     */       
/* 370 */       this.link_area.setRelativeURLBase(update.getRelativeURLBase());
/*     */       
/* 372 */       for (int i = 0; i < descriptions.length; i++)
/*     */       {
/* 374 */         this.link_area.addLine(descriptions[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Shell getShell()
/*     */   {
/* 382 */     return this.updateWindow;
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 386 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 388 */         UpdateWindow.this.updateWindow.dispose();
/* 389 */         UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 390 */         if (functionsSWT != null) {
/* 391 */           IMainStatusBar mainStatusBar = functionsSWT.getMainStatusBar();
/* 392 */           if (mainStatusBar != null) {
/* 393 */             mainStatusBar.setUpdateNeeded(null);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void addUpdate(final Update update) {
/* 401 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 402 */       return;
/*     */     }
/* 404 */     if (update.isMandatory())
/*     */     {
/* 406 */       this.hasMandatoryUpdates = true;
/*     */     }
/*     */     
/* 409 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 411 */         if ((UpdateWindow.this.table == null) || (UpdateWindow.this.table.isDisposed())) {
/* 412 */           return;
/*     */         }
/* 414 */         TableItem item = new TableItem(UpdateWindow.this.table, 0);
/* 415 */         item.setData(update);
/* 416 */         item.setText(0, update.getName() == null ? "Unknown" : update.getName());
/*     */         
/* 418 */         String old_version = update.getOldVersion();
/* 419 */         if (old_version == null) {
/* 420 */           old_version = MessageText.getString("SpeedView.stats.unknown");
/* 421 */         } else if ((old_version.equals("0")) || (old_version.equals("0.0"))) {
/* 422 */           old_version = "";
/*     */         }
/*     */         
/* 425 */         item.setText(1, old_version);
/*     */         
/* 427 */         String new_version = update.getNewVersion();
/* 428 */         if (new_version == null) {
/* 429 */           new_version = MessageText.getString("SpeedView.stats.unknown");
/*     */         }
/*     */         
/* 432 */         item.setText(2, new_version);
/*     */         
/* 434 */         ResourceDownloader[] rds = update.getDownloaders();
/* 435 */         long totalLength = 0L;
/* 436 */         for (int i = 0; i < rds.length; i++) {
/*     */           try {
/* 438 */             totalLength += rds[i].getSize();
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */         
/* 443 */         item.setText(3, DisplayFormatters.formatByteCountToKiBEtc(totalLength));
/*     */         
/* 445 */         item.setChecked(true);
/*     */         
/*     */ 
/*     */ 
/* 449 */         if (UpdateWindow.this.table.getItemCount() == 1)
/*     */         {
/* 451 */           UpdateWindow.this.table.select(0);
/*     */           
/* 453 */           UpdateWindow.this.rowSelected();
/*     */         }
/*     */         
/* 456 */         UpdateWindow.this.checkRestartNeeded();
/*     */         
/* 458 */         if ((COConfigurationManager.getBooleanParameter("update.opendialog")) || (UpdateWindow.this.check_instance.getType() != 2))
/*     */         {
/*     */ 
/* 461 */           UpdateWindow.this.show();
/*     */         }
/*     */         else
/*     */         {
/* 465 */           UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 466 */           if (functionsSWT != null) {
/* 467 */             IMainStatusBar mainStatusBar = functionsSWT.getMainStatusBar();
/* 468 */             if (mainStatusBar != null) {
/* 469 */               mainStatusBar.setUpdateNeeded(UpdateWindow.this);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void updateAdditionComplete()
/*     */   {
/* 480 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 481 */       return;
/*     */     }
/* 483 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 485 */         if ((UpdateWindow.this.btnOk == null) || (UpdateWindow.this.btnOk.isDisposed())) {
/* 486 */           return;
/*     */         }
/* 488 */         UpdateWindow.this.btnOk.setEnabled(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void show() {
/* 494 */     if ((this.updateWindow == null) || (this.updateWindow.isDisposed())) {
/* 495 */       return;
/*     */     }
/*     */     
/* 498 */     Utils.centreWindow(this.updateWindow);
/* 499 */     this.updateWindow.setMinimized(false);
/* 500 */     this.updateWindow.open();
/* 501 */     this.updateWindow.forceActive();
/*     */   }
/*     */   
/*     */   private void checkMandatory()
/*     */   {
/* 506 */     TableItem[] items = this.table.getItems();
/* 507 */     for (int i = 0; i < items.length; i++) {
/* 508 */       Update update = (Update)items[i].getData();
/* 509 */       if (update.isMandatory()) items[i].setChecked(true);
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkRestartNeeded() {
/* 514 */     this.restartRequired = false;
/* 515 */     boolean restartMaybeRequired = false;
/* 516 */     TableItem[] items = this.table.getItems();
/* 517 */     for (int i = 0; i < items.length; i++) {
/* 518 */       if (items[i].getChecked()) {
/* 519 */         Update update = (Update)items[i].getData();
/* 520 */         int required = update.getRestartRequired();
/* 521 */         if (required == 3) {
/* 522 */           restartMaybeRequired = true;
/* 523 */         } else if (required == 2)
/* 524 */           this.restartRequired = true;
/*     */       }
/*     */     }
/* 527 */     if (this.restartRequired) {
/* 528 */       this.status.setText(MessageText.getString("UpdateWindow.status.restartNeeded"));
/* 529 */     } else if (restartMaybeRequired) {
/* 530 */       this.status.setText(MessageText.getString("UpdateWindow.status.restartMaybeNeeded"));
/*     */     } else {
/* 532 */       this.status.setText("");
/*     */     }
/*     */   }
/*     */   
/*     */   private void update() {
/* 537 */     this.btnOk.setEnabled(false);
/* 538 */     Messages.setLanguageText(this.btnCancel, "UpdateWindow.cancel");
/* 539 */     this.table.setEnabled(false);
/*     */     
/* 541 */     this.link_area.reset();
/* 542 */     if (this.browser != null) {
/* 543 */       this.browser.setVisible(false);
/*     */     }
/* 545 */     this.link_area.getComponent().setVisible(true);
/*     */     
/* 547 */     TableItem[] items = this.table.getItems();
/*     */     
/* 549 */     this.totalDownloadSize = 0L;
/* 550 */     this.downloaders = new ArrayList();
/*     */     
/* 552 */     for (int i = 0; i < items.length; i++)
/* 553 */       if (items[i].getChecked())
/*     */       {
/* 555 */         Update update = (Update)items[i].getData();
/* 556 */         ResourceDownloader[] rds = update.getDownloaders();
/* 557 */         for (int j = 0; j < rds.length; j++) {
/* 558 */           this.downloaders.add(rds[j]);
/*     */           try {
/* 560 */             this.totalDownloadSize += rds[j].getSize();
/*     */           } catch (Exception e) {
/* 562 */             this.link_area.addLine(MessageText.getString("UpdateWindow.no_size") + rds[j].getName());
/*     */           }
/*     */         }
/*     */       }
/* 566 */     this.downloadersToData = new HashMap();
/* 567 */     this.iterDownloaders = this.downloaders.iterator();
/* 568 */     nextUpdate();
/*     */   }
/*     */   
/*     */   private void nextUpdate() {
/* 572 */     if (this.iterDownloaders.hasNext()) {
/* 573 */       ResourceDownloader downloader = (ResourceDownloader)this.iterDownloaders.next();
/* 574 */       downloader.addListener(this);
/* 575 */       downloader.asyncDownload();
/*     */     } else {
/* 577 */       switchToRestart();
/*     */     }
/*     */   }
/*     */   
/*     */   private void switchToRestart() {
/* 582 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 583 */       return;
/*     */     }
/* 585 */     this.display.asyncExec(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 588 */         Boolean b = (Boolean)UpdateWindow.this.check_instance.getProperty(4);
/*     */         
/* 590 */         if ((b != null) && (b.booleanValue()))
/*     */         {
/* 592 */           UpdateWindow.this.finishUpdate(false, true);
/*     */           
/* 594 */           return;
/*     */         }
/*     */         
/* 597 */         UpdateWindow.this.checkRestartNeeded();
/* 598 */         UpdateWindow.this.progress.setSelection(100);
/* 599 */         UpdateWindow.this.status.setText(MessageText.getString("UpdateWindow.status.done"));
/* 600 */         UpdateWindow.this.btnOk.removeListener(13, UpdateWindow.this.lOk);
/* 601 */         UpdateWindow.this.btnOk.setEnabled(true);
/* 602 */         UpdateWindow.this.btnOk.addListener(13, new Listener() {
/*     */           public void handleEvent(Event e) {
/* 604 */             UpdateWindow.this.btnOk.setEnabled(false);
/* 605 */             UpdateWindow.this.btnCancel.setEnabled(false);
/*     */             
/* 607 */             UpdateWindow.this.finishUpdate(true, false);
/*     */           }
/*     */         });
/* 610 */         if (UpdateWindow.this.restartRequired) {
/* 611 */           Messages.setLanguageText(UpdateWindow.this.btnOk, "UpdateWindow.restart");
/* 612 */           UpdateWindow.this.btnCancel.removeListener(13, UpdateWindow.this.lCancel);
/* 613 */           Messages.setLanguageText(UpdateWindow.this.btnCancel, "UpdateWindow.restartLater");
/* 614 */           UpdateWindow.this.btnCancel.addListener(13, new Listener() {
/*     */             public void handleEvent(Event e) {
/* 616 */               UpdateWindow.this.finishUpdate(false, false);
/*     */             }
/* 618 */           });
/* 619 */           UpdateWindow.this.updateWindow.layout();
/*     */         } else {
/* 621 */           Messages.setLanguageText(UpdateWindow.this.btnOk, "UpdateWindow.close");
/* 622 */           UpdateWindow.this.btnCancel.setEnabled(false);
/* 623 */           UpdateWindow.this.updateWindow.layout();
/*     */         }
/*     */         
/* 626 */         UpdateWindow.this.updateWindow.setMinimized(false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */   {
/* 633 */     setProgressSelection(percentage);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportAmountComplete(ResourceDownloader downloader, long amount) {}
/*     */   
/*     */ 
/*     */ 
/*     */   private void setProgressSelection(final int percent)
/*     */   {
/* 644 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 645 */       return;
/*     */     }
/* 647 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 649 */         if ((UpdateWindow.this.progress != null) && (!UpdateWindow.this.progress.isDisposed())) {
/* 650 */           UpdateWindow.this.progress.setSelection(percent);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 658 */     this.downloadersToData.put(downloader, data);
/* 659 */     downloader.removeListener(this);
/* 660 */     setProgressSelection(0);
/* 661 */     nextUpdate();
/* 662 */     return true;
/*     */   }
/*     */   
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 667 */     downloader.removeListener(this);
/* 668 */     setStatusText(MessageText.getString("UpdateWindow.status.failed"));
/*     */     
/* 670 */     String msg = downloader.getName() + " : " + e;
/*     */     
/* 672 */     if (e.getCause() != null)
/*     */     {
/* 674 */       msg = msg + " [" + e.getCause() + "]";
/*     */     }
/*     */     
/* 677 */     appendDetails(msg);
/*     */   }
/*     */   
/*     */   public void reportActivity(ResourceDownloader downloader, String activity) {
/* 681 */     setStatusText(activity.trim());
/* 682 */     appendDetails(activity);
/*     */   }
/*     */   
/*     */   private void setStatusText(final String text) {
/* 686 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 687 */       return;
/*     */     }
/* 689 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 691 */         if ((UpdateWindow.this.status != null) && (!UpdateWindow.this.status.isDisposed()))
/* 692 */           UpdateWindow.this.status.setText(text);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void appendDetails(final String text) {
/* 698 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 699 */       return;
/*     */     }
/* 701 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 703 */         UpdateWindow.this.link_area.addLine(text);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void finishUpdate(boolean restartNow, boolean just_close)
/*     */   {
/* 711 */     UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 712 */     if (functionsSWT != null) {
/* 713 */       IMainStatusBar mainStatusBar = functionsSWT.getMainStatusBar();
/* 714 */       if (mainStatusBar != null) {
/* 715 */         mainStatusBar.setUpdateNeeded(null);
/*     */       }
/*     */     }
/*     */     
/* 719 */     boolean bDisposeUpdateWindow = true;
/*     */     
/* 721 */     if (!just_close)
/*     */     {
/* 723 */       if ((this.restartRequired) && (restartNow))
/*     */       {
/*     */ 
/* 726 */         UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 727 */         if ((uiFunctions != null) && (uiFunctions.dispose(true, false))) {
/* 728 */           bDisposeUpdateWindow = false;
/*     */         }
/* 730 */       } else if ((this.hasMandatoryUpdates) && (!this.restartRequired))
/*     */       {
/*     */ 
/*     */ 
/* 734 */         this.update_monitor.requestRecheck();
/*     */       }
/*     */     }
/*     */     
/* 738 */     if (bDisposeUpdateWindow) {
/* 739 */       this.updateWindow.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isDisposed()
/*     */   {
/* 746 */     return (this.display == null) || (this.display.isDisposed()) || (this.updateWindow == null) || (this.updateWindow.isDisposed());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/update/UpdateWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */