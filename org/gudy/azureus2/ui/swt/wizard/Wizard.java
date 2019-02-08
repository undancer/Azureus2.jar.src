/*     */ package org.gudy.azureus2.ui.swt.wizard;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Wizard
/*     */ {
/*     */   private static final int DEFAULT_WIDTH = 500;
/*  58 */   List<WizardListener> listeners = new ArrayList(1);
/*     */   
/*     */   Display display;
/*     */   
/*     */   Shell wizardWindow;
/*     */   
/*     */   Label title;
/*     */   Label currentInfo;
/*     */   Label errorMessage;
/*     */   IWizardPanel<?> currentPanel;
/*     */   Composite panel;
/*     */   Font titleFont;
/*     */   protected Button previous;
/*     */   protected Button next;
/*     */   protected Button finish;
/*     */   protected Button cancel;
/*     */   Listener closeCatcher;
/*     */   int wizardHeight;
/*     */   private boolean completed;
/*     */   
/*     */   public Wizard(String keyTitle)
/*     */   {
/*  80 */     this(keyTitle, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Wizard(String keyTitle, boolean modal)
/*     */   {
/*  88 */     this(modal);
/*     */     
/*  90 */     setTitleKey(keyTitle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Wizard(String keyTitle, boolean modal, int width)
/*     */   {
/*  99 */     this(modal, width);
/*     */     
/* 101 */     setTitleKey(keyTitle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Wizard(boolean modal)
/*     */   {
/* 108 */     this(modal, 500);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Wizard(boolean modal, int width)
/*     */   {
/* 116 */     int style = 2160;
/* 117 */     if (modal) {
/* 118 */       style |= 0x10000;
/*     */     }
/* 120 */     this.wizardWindow = ShellFactory.createMainShell(style);
/* 121 */     this.display = this.wizardWindow.getDisplay();
/*     */     
/* 123 */     GridLayout layout = new GridLayout();
/* 124 */     layout.numColumns = 1;
/* 125 */     layout.horizontalSpacing = 0;
/* 126 */     layout.verticalSpacing = 0;
/* 127 */     layout.marginHeight = 0;
/* 128 */     layout.marginWidth = 0;
/* 129 */     this.wizardWindow.setLayout(layout);
/* 130 */     Utils.setShellIcon(this.wizardWindow);
/*     */     
/* 132 */     Composite cTitle = new Composite(this.wizardWindow, 0);
/* 133 */     Color white = this.display.getSystemColor(1);
/* 134 */     cTitle.setBackground(white);
/* 135 */     GridData gridData = new GridData(768);
/* 136 */     Utils.setLayoutData(cTitle, gridData);
/* 137 */     GridLayout titleLayout = new GridLayout();
/* 138 */     titleLayout.numColumns = 1;
/* 139 */     cTitle.setLayout(titleLayout);
/* 140 */     this.title = new Label(cTitle, 0);
/* 141 */     this.title.setBackground(white);
/* 142 */     gridData = new GridData(768);
/* 143 */     Utils.setLayoutData(this.title, gridData);
/* 144 */     Font font = this.title.getFont();
/* 145 */     FontData[] data = font.getFontData();
/* 146 */     for (int i = 0; i < data.length; i++) {
/* 147 */       data[i].setStyle(1);
/*     */     }
/* 149 */     this.titleFont = new Font(this.display, data);
/* 150 */     this.title.setFont(this.titleFont);
/* 151 */     this.currentInfo = new Label(cTitle, 64);
/* 152 */     gridData = Utils.getWrappableLabelGridData(1, 768);
/* 153 */     Utils.setLayoutData(this.currentInfo, gridData);
/* 154 */     this.currentInfo.setBackground(white);
/* 155 */     this.errorMessage = new Label(cTitle, 0);
/* 156 */     gridData = new GridData(768);
/* 157 */     Utils.setLayoutData(this.errorMessage, gridData);
/* 158 */     this.errorMessage.setBackground(white);
/* 159 */     Color red = this.display.getSystemColor(3);
/* 160 */     this.errorMessage.setForeground(red);
/*     */     
/* 162 */     gridData = new GridData(768);
/* 163 */     new Label(this.wizardWindow, 258).setLayoutData(gridData);
/*     */     
/* 165 */     this.panel = new Composite(this.wizardWindow, 0);
/* 166 */     gridData = new GridData(1808);
/* 167 */     Utils.setLayoutData(this.panel, gridData);
/*     */     
/* 169 */     gridData = new GridData(768);
/* 170 */     new Label(this.wizardWindow, 258).setLayoutData(gridData);
/*     */     
/* 172 */     Composite cButtons = new Composite(this.wizardWindow, 0);
/* 173 */     gridData = new GridData(768);
/* 174 */     Utils.setLayoutData(cButtons, gridData);
/* 175 */     GridLayout layoutButtons = new GridLayout();
/* 176 */     layoutButtons.numColumns = 5;
/* 177 */     cButtons.setLayout(layoutButtons);
/* 178 */     gridData = new GridData(768);
/* 179 */     new Label(cButtons, 0).setLayoutData(gridData);
/*     */     
/* 181 */     this.cancel = new Button(cButtons, 8);
/* 182 */     gridData = new GridData();
/* 183 */     gridData.widthHint = 90;
/* 184 */     gridData.horizontalAlignment = 2;
/* 185 */     Utils.setLayoutData(this.cancel, gridData);
/* 186 */     Messages.setLanguageText(this.cancel, "Button.cancel");
/*     */     
/* 188 */     this.previous = new Button(cButtons, 8);
/* 189 */     gridData = new GridData();
/* 190 */     gridData.widthHint = 90;
/* 191 */     gridData.horizontalAlignment = 3;
/* 192 */     Utils.setLayoutData(this.previous, gridData);
/* 193 */     Messages.setLanguageText(this.previous, "wizard.previous");
/*     */     
/* 195 */     this.next = new Button(cButtons, 8);
/* 196 */     gridData = new GridData();
/* 197 */     gridData.widthHint = 90;
/* 198 */     gridData.horizontalAlignment = 1;
/* 199 */     Utils.setLayoutData(this.next, gridData);
/* 200 */     Messages.setLanguageText(this.next, "wizard.next");
/*     */     
/* 202 */     this.finish = new Button(cButtons, 8);
/* 203 */     gridData = new GridData();
/* 204 */     gridData.widthHint = 90;
/* 205 */     gridData.horizontalAlignment = 2;
/* 206 */     Utils.setLayoutData(this.finish, gridData);
/* 207 */     Messages.setLanguageText(this.finish, "wizard.finish");
/*     */     
/* 209 */     this.previous.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/* 214 */         Wizard.this.clearPanel();
/* 215 */         Wizard.this.currentPanel = Wizard.this.currentPanel.getPreviousPanel();
/* 216 */         Wizard.this.refresh();
/*     */       }
/*     */       
/* 219 */     });
/* 220 */     this.next.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/* 225 */         IWizardPanel<?> nextPanel = Wizard.this.currentPanel.getNextPanel();
/* 226 */         Wizard.this.clearPanel();
/* 227 */         Wizard.this.currentPanel = nextPanel;
/* 228 */         Wizard.this.refresh();
/*     */       }
/*     */       
/* 231 */     });
/* 232 */     this.closeCatcher = new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 237 */         event.doit = false;
/*     */       }
/*     */       
/* 240 */     };
/* 241 */     this.finish.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/* 246 */         Wizard.this.finishSelected();
/*     */       }
/*     */       
/* 249 */     });
/* 250 */     this.cancel.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/* 255 */         Wizard.this.cancelSelected();
/*     */       }
/*     */       
/* 258 */     });
/* 259 */     this.wizardWindow.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent de) {
/* 261 */         Wizard.this.onClose();
/*     */       }
/*     */       
/* 264 */     });
/* 265 */     this.wizardWindow.addListener(31, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e)
/*     */       {
/* 269 */         if (e.character == '\033')
/*     */         {
/* 271 */           if (Wizard.this.cancel.isEnabled())
/*     */           {
/* 273 */             Wizard.this.wizardWindow.dispose();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 278 */     });
/* 279 */     this.wizardHeight = (this.wizardWindow.computeSize(width, -1).y - 50);
/* 280 */     this.wizardWindow.setSize(Utils.adjustPXForDPI(width), Utils.adjustPXForDPI(400));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void cancelSelected()
/*     */   {
/* 287 */     this.completed = true;
/*     */     
/* 289 */     if (this.currentPanel != null)
/*     */     {
/* 291 */       this.currentPanel.cancelled();
/*     */     }
/* 293 */     this.wizardWindow.dispose();
/*     */   }
/*     */   
/*     */ 
/*     */   private void finishSelected()
/*     */   {
/* 299 */     if (this.currentPanel.isFinishSelectionOK()) {
/* 300 */       this.completed = true;
/* 301 */       this.wizardWindow.addListener(21, this.closeCatcher);
/* 302 */       clearPanel();
/* 303 */       this.currentPanel = this.currentPanel.getFinishPanel();
/* 304 */       refresh();
/* 305 */       this.currentPanel.finish();
/*     */     }
/*     */   }
/*     */   
/*     */   private void clearPanel() {
/* 310 */     if (this.panel.isDisposed()) {
/* 311 */       return;
/*     */     }
/* 313 */     Control[] controls = this.panel.getChildren();
/* 314 */     for (int i = 0; i < controls.length; i++) {
/* 315 */       if ((controls[i] != null) && (!controls[i].isDisposed()))
/* 316 */         controls[i].dispose();
/*     */     }
/* 318 */     setTitle("");
/* 319 */     setCurrentInfo("");
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 323 */     if (this.currentPanel == null)
/*     */     {
/* 325 */       setDefaultButton();
/*     */       
/* 327 */       return;
/*     */     }
/*     */     
/* 330 */     this.previous.setEnabled(this.currentPanel.isPreviousEnabled());
/*     */     
/* 332 */     this.next.setEnabled(this.currentPanel.isNextEnabled());
/*     */     
/* 334 */     this.finish.setEnabled(this.currentPanel.isFinishEnabled());
/*     */     
/* 336 */     setDefaultButton();
/* 337 */     this.currentPanel.show();
/* 338 */     this.panel.layout();
/* 339 */     this.panel.redraw();
/* 340 */     insureSize();
/*     */   }
/*     */   
/*     */ 
/*     */   private void setDefaultButton()
/*     */   {
/* 346 */     if (!this.wizardWindow.isDisposed())
/*     */     {
/* 348 */       this.display.asyncExec(new AERunnable()
/*     */       {
/*     */         public void runSupport() {
/* 351 */           if (!Wizard.this.wizardWindow.isDisposed()) {
/* 352 */             Button default_button = null;
/*     */             
/* 354 */             if (Wizard.this.next.isEnabled())
/*     */             {
/* 356 */               default_button = Wizard.this.next;
/*     */             }
/* 358 */             else if (Wizard.this.finish.isEnabled())
/*     */             {
/* 360 */               default_button = Wizard.this.finish;
/*     */             }
/* 362 */             else if (Wizard.this.previous.isEnabled())
/*     */             {
/* 364 */               default_button = Wizard.this.previous;
/*     */             }
/* 366 */             else if (Wizard.this.cancel.isEnabled())
/*     */             {
/* 368 */               default_button = Wizard.this.cancel;
/*     */             }
/*     */             
/* 371 */             if (default_button != null)
/*     */             {
/* 373 */               Wizard.this.wizardWindow.setDefaultButton(default_button);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public Composite getPanel() {
/* 382 */     return this.panel;
/*     */   }
/*     */   
/*     */   public void setTitle(String title) {
/* 386 */     this.title.setText(title);
/*     */   }
/*     */   
/*     */   public void setTitleAsResourceID(String id) {
/* 390 */     Messages.setLanguageText(this.title, id);
/*     */   }
/*     */   
/*     */   public void setCurrentInfo(String currentInfo) {
/* 394 */     currentInfo = currentInfo.replaceAll("\n", "\n\t");
/* 395 */     this.currentInfo.setText("\t" + currentInfo);
/* 396 */     this.currentInfo.getParent().layout();
/*     */   }
/*     */   
/*     */   public void setErrorMessage(String errorMessage) {
/* 400 */     this.errorMessage.setText(errorMessage);
/*     */   }
/*     */   
/*     */   public void setTitleKey(String key) {
/* 404 */     Messages.setLanguageText(this.wizardWindow, key);
/*     */   }
/*     */   
/*     */   public void setNextEnabled(boolean enabled) {
/* 408 */     this.next.setEnabled(enabled);
/* 409 */     setDefaultButton();
/*     */   }
/*     */   
/*     */   public void setPreviousEnabled(boolean enabled) {
/* 413 */     this.previous.setEnabled(enabled);
/* 414 */     setDefaultButton();
/*     */   }
/*     */   
/*     */   public void setFinishEnabled(boolean enabled) {
/* 418 */     this.finish.setEnabled(enabled);
/* 419 */     setDefaultButton();
/*     */   }
/*     */   
/*     */   public void setFirstPanel(IWizardPanel<?> panel) {
/* 423 */     this.currentPanel = panel;
/* 424 */     refresh();
/* 425 */     insureSize();
/* 426 */     Utils.centreWindow(this.wizardWindow);
/* 427 */     this.wizardWindow.open();
/*     */   }
/*     */   
/*     */   public Shell getWizardWindow() {
/* 431 */     return this.wizardWindow;
/*     */   }
/*     */   
/*     */   public String getErrorMessage() {
/* 435 */     return this.errorMessage.getText();
/*     */   }
/*     */   
/*     */   public Display getDisplay() {
/* 439 */     return this.display;
/*     */   }
/*     */   
/*     */   public void switchToClose() {
/* 443 */     switchToClose(null);
/*     */   }
/*     */   
/*     */   public void switchToClose(final Runnable do_it) {
/* 447 */     if (!this.wizardWindow.isDisposed()) {
/* 448 */       this.display.asyncExec(new AERunnable() {
/*     */         public void runSupport() {
/* 450 */           if ((Wizard.this.closeCatcher != null) && (Wizard.this.wizardWindow != null) && (!Wizard.this.wizardWindow.isDisposed())) {
/* 451 */             Wizard.this.wizardWindow.removeListener(21, Wizard.this.closeCatcher);
/* 452 */             Wizard.this.cancel.setText(MessageText.getString("wizard.close"));
/* 453 */             Wizard.this.cancel.setEnabled(true);
/* 454 */             Wizard.this.setDefaultButton();
/*     */             
/* 456 */             if (do_it != null) {
/* 457 */               do_it.run();
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 468 */     this.completed = true;
/*     */     
/* 470 */     if (!this.wizardWindow.isDisposed())
/*     */     {
/* 472 */       this.wizardWindow.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public void onClose() {
/* 477 */     if ((this.titleFont != null) && (!this.titleFont.isDisposed())) {
/* 478 */       this.titleFont.dispose();
/* 479 */       this.titleFont = null;
/*     */     }
/*     */     
/* 482 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 484 */       ((WizardListener)this.listeners.get(i)).closed();
/*     */     }
/*     */     
/* 487 */     if (!this.completed)
/*     */     {
/* 489 */       this.completed = true;
/*     */       
/* 491 */       if (this.currentPanel != null)
/*     */       {
/* 493 */         this.currentPanel.cancelled();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel<?> getCurrentPanel()
/*     */   {
/* 501 */     return this.currentPanel;
/*     */   }
/*     */   
/*     */   private void insureSize()
/*     */   {
/* 506 */     Point p = this.panel.computeSize(this.wizardWindow.getSize().x, -1);
/* 507 */     int height = p.y + this.wizardHeight;
/* 508 */     if (height > this.wizardWindow.getSize().y) {
/* 509 */       this.wizardWindow.setSize(p.x, height);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(WizardListener l)
/*     */   {
/* 516 */     if ((this.wizardWindow.isDisposed()) && (this.closeCatcher != null)) {
/* 517 */       l.closed();
/*     */     }
/* 519 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(WizardListener l)
/*     */   {
/* 526 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/wizard/Wizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */