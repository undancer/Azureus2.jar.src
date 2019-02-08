/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.layout.RowData;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.eclipse.swt.widgets.Scrollable;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputValidator;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.AbstractUISWTInputReceiver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleTextEntryWindow
/*     */   extends AbstractUISWTInputReceiver
/*     */ {
/*     */   private Display display;
/*     */   private Shell parent_shell;
/*     */   private Shell shell;
/*     */   private int textLimit;
/*     */   private Combo text_entry_combo;
/*     */   private Text text_entry_text;
/*     */   
/*     */   public SimpleTextEntryWindow() {}
/*     */   
/*     */   public SimpleTextEntryWindow(String sTitleKey, String sLabelKey)
/*     */   {
/*  50 */     setTitle(sTitleKey);
/*  51 */     setMessage(sLabelKey);
/*     */   }
/*     */   
/*     */   public SimpleTextEntryWindow(String sTitleKey, String sLabelKey, boolean bMultiLine) {
/*  55 */     setTitle(sTitleKey);
/*  56 */     setMessage(sLabelKey);
/*  57 */     setMultiLine(bMultiLine);
/*     */   }
/*     */   
/*     */   public void initTexts(String sTitleKey, String[] p0, String sLabelKey, String[] p1)
/*     */   {
/*  62 */     setLocalisedTitle(MessageText.getString(sTitleKey, p0));
/*  63 */     setLocalisedMessage(MessageText.getString(sLabelKey, p1));
/*     */   }
/*     */   
/*     */   protected void promptForInput() {
/*  67 */     Utils.execSWTThread(new Runnable() {
/*     */       public void run() {
/*  69 */         SimpleTextEntryWindow.this.promptForInput0();
/*  70 */         if (SimpleTextEntryWindow.this.receiver_listener == null)
/*  71 */           while ((SimpleTextEntryWindow.this.shell != null) && (!SimpleTextEntryWindow.this.shell.isDisposed()))
/*  72 */             if (!SimpleTextEntryWindow.this.display.readAndDispatch()) SimpleTextEntryWindow.this.display.sleep(); } }, this.receiver_listener != null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void promptForInput0()
/*     */   {
/*  82 */     Shell parent = this.parent_shell;
/*     */     
/*  84 */     if (this.parent_shell == null)
/*     */     {
/*  86 */       parent = Display.getDefault().getActiveShell();
/*     */     }
/*     */     
/*  89 */     if (parent == null) {
/*  90 */       parent = Utils.findAnyShell();
/*     */     }
/*  92 */     this.shell = ShellFactory.createShell(parent, 2144);
/*     */     
/*  94 */     this.display = this.shell.getDisplay();
/*  95 */     if (this.title != null) {
/*  96 */       this.shell.setText(this.title);
/*     */     }
/*     */     
/*  99 */     Utils.setShellIcon(this.shell);
/*     */     
/* 101 */     GridLayout layout = new GridLayout();
/* 102 */     layout.verticalSpacing = 10;
/* 103 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/* 106 */     int width_hint = this.width_hint == -1 ? 330 : this.width_hint;
/*     */     
/*     */ 
/* 109 */     Label label = null;
/* 110 */     GridData gridData = null;
/* 111 */     for (int i = 0; i < this.messages.length; i++) {
/* 112 */       label = new Label(this.shell, 64);
/* 113 */       label.setText(this.messages[i]);
/*     */       
/*     */ 
/* 116 */       gridData = new GridData();
/* 117 */       gridData.widthHint = width_hint;
/* 118 */       Utils.setLayoutData(label, gridData);
/*     */     }
/*     */     
/*     */     Scrollable text_entry;
/*     */     Scrollable text_entry;
/* 123 */     if (this.choices != null) {
/* 124 */       int text_entry_flags = 4;
/* 125 */       if (!this.choices_allow_edit) {
/* 126 */         text_entry_flags |= 0x8;
/*     */       }
/*     */       
/* 129 */       this.text_entry_combo = new Combo(this.shell, text_entry_flags);
/* 130 */       this.text_entry_combo.setItems(this.choices);
/* 131 */       if (this.textLimit > 0) {
/* 132 */         this.text_entry_combo.setTextLimit(this.textLimit);
/*     */       }
/* 134 */       this.text_entry_text = null;
/* 135 */       text_entry = this.text_entry_combo;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 140 */       int text_entry_flags = 2048;
/* 141 */       if (this.multiline_mode) {
/* 142 */         text_entry_flags |= 0x242;
/*     */       }
/*     */       else {
/* 145 */         text_entry_flags |= 0x4;
/*     */       }
/*     */       
/* 148 */       this.text_entry_text = new Text(this.shell, text_entry_flags);
/*     */       
/* 150 */       if (this.textLimit > 0) {
/* 151 */         this.text_entry_text.setTextLimit(this.textLimit);
/*     */       }
/* 153 */       this.text_entry_combo = null;
/* 154 */       text_entry = this.text_entry_text;
/*     */     }
/* 156 */     if (this.preentered_text != null) {
/* 157 */       if (this.text_entry_text != null) {
/* 158 */         this.text_entry_text.setText(this.preentered_text);
/* 159 */         if (this.select_preentered_text)
/*     */         {
/* 161 */           int[] range = this.select_preentered_text_range;
/*     */           
/* 163 */           if ((range == null) || (range.length != 2)) {
/* 164 */             this.text_entry_text.selectAll();
/*     */           } else {
/*     */             try {
/* 167 */               this.text_entry_text.setSelection(range[0], range[1]);
/*     */             } catch (Throwable e) {
/* 169 */               this.text_entry_text.selectAll();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 174 */       else if (this.text_entry_combo != null) {
/* 175 */         this.text_entry_combo.setText(this.preentered_text);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 180 */     text_entry.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 182 */         if ((e.detail == 16) || (e.detail == 8)) {
/* 183 */           e.doit = true;
/*     */         }
/*     */         
/*     */       }
/* 187 */     });
/* 188 */     text_entry.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyPressed(KeyEvent e) {
/* 191 */         int key = e.character;
/* 192 */         if ((key <= 26) && (key > 0)) {
/* 193 */           key += 96;
/*     */         }
/*     */         
/* 196 */         if ((key == 97) && (e.stateMask == SWT.MOD1) && 
/* 197 */           (SimpleTextEntryWindow.this.text_entry_text != null)) {
/* 198 */           SimpleTextEntryWindow.this.text_entry_text.selectAll();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void keyReleased(KeyEvent e) {}
/* 209 */     });
/* 210 */     int line_height = this.line_height;
/* 211 */     if (line_height == -1) {
/* 212 */       line_height = this.multiline_mode ? 3 : 1;
/*     */     }
/*     */     
/* 215 */     gridData = new GridData();
/* 216 */     gridData.widthHint = width_hint;
/* 217 */     if (this.text_entry_text != null)
/* 218 */       gridData.minimumHeight = (this.text_entry_text.getLineHeight() * line_height);
/* 219 */     gridData.heightHint = gridData.minimumHeight;
/* 220 */     Utils.setLayoutData(text_entry, gridData);
/*     */     
/* 222 */     Composite panel = new Composite(this.shell, 0);
/* 223 */     RowLayout rLayout = new RowLayout();
/* 224 */     rLayout.marginTop = 0;
/* 225 */     rLayout.marginLeft = 0;
/* 226 */     rLayout.marginBottom = 0;
/* 227 */     rLayout.marginRight = 0;
/*     */     try {
/* 229 */       rLayout.fill = true;
/*     */     }
/*     */     catch (NoSuchFieldError e) {}
/*     */     
/* 233 */     rLayout.spacing = Utils.BUTTON_MARGIN;
/* 234 */     panel.setLayout(rLayout);
/* 235 */     gridData = new GridData();
/* 236 */     gridData.horizontalAlignment = 16777224;
/* 237 */     Utils.setLayoutData(panel, gridData);
/*     */     Button ok;
/*     */     Button ok;
/*     */     Button cancel;
/* 241 */     if (Constants.isOSX) {
/* 242 */       Button cancel = createAlertButton(panel, "Button.cancel");
/* 243 */       ok = createAlertButton(panel, "Button.ok");
/*     */     } else {
/* 245 */       ok = createAlertButton(panel, "Button.ok");
/* 246 */       cancel = createAlertButton(panel, "Button.cancel");
/*     */     }
/*     */     
/* 249 */     ok.addListener(13, new Listener()
/*     */     {
/*     */       private void showError(String text) {
/* 252 */         String error_title = SimpleTextEntryWindow.this.title;
/* 253 */         if (error_title == null) { error_title = "";
/*     */         }
/* 255 */         MessageBox mb = new MessageBox(SimpleTextEntryWindow.this.shell, 33);
/* 256 */         mb.setText(error_title);
/* 257 */         mb.setMessage(text);
/* 258 */         mb.open();
/*     */       }
/*     */       
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */         try
/*     */         {
/* 266 */           String entered_data = "";
/* 267 */           if (SimpleTextEntryWindow.this.text_entry_text != null) {
/* 268 */             entered_data = SimpleTextEntryWindow.this.text_entry_text.getText();
/*     */           }
/* 270 */           else if (SimpleTextEntryWindow.this.text_entry_combo != null) {
/* 271 */             entered_data = SimpleTextEntryWindow.this.text_entry_combo.getText();
/*     */           }
/*     */           
/*     */ 
/* 275 */           if (!SimpleTextEntryWindow.this.maintain_whitespace) {
/* 276 */             entered_data = entered_data.trim();
/*     */           }
/*     */           
/* 279 */           if ((SimpleTextEntryWindow.this.textLimit > 0) && (entered_data.length() > SimpleTextEntryWindow.this.textLimit)) {
/* 280 */             entered_data = entered_data.substring(0, SimpleTextEntryWindow.this.textLimit);
/*     */           }
/*     */           
/* 283 */           if ((!SimpleTextEntryWindow.this.allow_empty_input) && (entered_data.length() == 0)) {
/* 284 */             showError(MessageText.getString("UI.cannot_submit_blank_text"));
/* 285 */             return;
/*     */           }
/*     */           
/* 288 */           UIInputValidator validator = SimpleTextEntryWindow.this.validator;
/* 289 */           if (validator != null) {
/* 290 */             String validate_result = validator.validate(entered_data);
/* 291 */             if (validate_result != null) {
/* 292 */               showError(MessageText.getString(validate_result));
/* 293 */               return;
/*     */             }
/*     */           }
/* 296 */           SimpleTextEntryWindow.this.recordUserInput(entered_data);
/*     */         }
/*     */         catch (Exception e) {
/* 299 */           Debug.printStackTrace(e);
/* 300 */           SimpleTextEntryWindow.this.recordUserAbort();
/*     */         }
/* 302 */         SimpleTextEntryWindow.this.shell.dispose();
/*     */       }
/*     */       
/* 305 */     });
/* 306 */     cancel.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 311 */         SimpleTextEntryWindow.this.recordUserAbort();
/* 312 */         SimpleTextEntryWindow.this.shell.dispose();
/*     */       }
/*     */       
/* 315 */     });
/* 316 */     this.shell.setDefaultButton(ok);
/*     */     
/* 318 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 320 */         if (e.character == '\033') {
/* 321 */           SimpleTextEntryWindow.this.recordUserAbort();
/* 322 */           SimpleTextEntryWindow.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/* 326 */     });
/* 327 */     this.shell.addListener(12, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 329 */         if (!SimpleTextEntryWindow.this.isResultRecorded()) {
/* 330 */           SimpleTextEntryWindow.this.recordUserAbort();
/*     */         }
/* 332 */         SimpleTextEntryWindow.this.triggerReceiverListener();
/*     */       }
/*     */       
/* 335 */     });
/* 336 */     this.shell.pack();
/* 337 */     if (this.text_entry_text != null) {
/* 338 */       Utils.createURLDropTarget(this.shell, this.text_entry_text);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 343 */     Utils.centreWindow(this.shell, false);
/* 344 */     this.shell.open();
/*     */   }
/*     */   
/*     */   private static Button createAlertButton(Composite panel, String localizationKey)
/*     */   {
/* 349 */     Button button = new Button(panel, 8);
/* 350 */     button.setText(MessageText.getString(localizationKey));
/* 351 */     RowData rData = new RowData();
/* 352 */     rData.width = Math.max(Utils.BUTTON_MINWIDTH, button.computeSize(-1, -1).x);
/*     */     
/*     */ 
/*     */ 
/* 356 */     button.setLayoutData(rData);
/* 357 */     return button;
/*     */   }
/*     */   
/*     */   public void setTextLimit(int limit) {
/* 361 */     this.textLimit = limit;
/* 362 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 364 */         if ((SimpleTextEntryWindow.this.text_entry_combo != null) && (!SimpleTextEntryWindow.this.text_entry_combo.isDisposed())) {
/* 365 */           SimpleTextEntryWindow.this.text_entry_combo.setTextLimit(SimpleTextEntryWindow.this.textLimit);
/*     */         }
/* 367 */         if ((SimpleTextEntryWindow.this.text_entry_text != null) && (!SimpleTextEntryWindow.this.text_entry_text.isDisposed())) {
/* 368 */           SimpleTextEntryWindow.this.text_entry_text.setTextLimit(SimpleTextEntryWindow.this.textLimit);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setParentShell(Shell shell)
/*     */   {
/* 378 */     this.parent_shell = shell;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/SimpleTextEntryWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */