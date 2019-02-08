/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.FontMetrics;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ScrollBar;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.ILogEventListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.logging.impl.FileLogging;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoggerView
/*     */   implements ILogEventListener, ParameterListener, UISWTViewCoreEventListenerEx
/*     */ {
/*  74 */   private static Color[] colors = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */   private StyledText consoleText = null;
/*     */   
/*  92 */   private Button buttonAutoScroll = null;
/*     */   
/*  94 */   private Object[] filter = null;
/*     */   
/*     */ 
/*  97 */   private LinkedList<LogEvent> buffer = new LinkedList();
/*     */   
/*  99 */   private boolean bPaused = false;
/*     */   
/* 101 */   private boolean bRealtime = false;
/*     */   
/* 103 */   private boolean bEnabled = false;
/*     */   
/* 105 */   private boolean bAutoScroll = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 112 */   private ArrayList[] ignoredComponents = new ArrayList[3];
/*     */   
/* 114 */   private boolean stopOnNull = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 119 */   private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("[HH:mm:ss.SSS] ");
/* 120 */   private static final FieldPosition formatPos = new FieldPosition(0);
/*     */   public static final String VIEW_ID = "LoggerView";
/*     */   
/*     */   public LoggerView() {
/* 124 */     this(false);
/* 125 */     setEnabled(true);
/*     */   }
/*     */   
/*     */   public LoggerView(boolean stopOnNull) {
/* 129 */     for (int i = 0; i < this.ignoredComponents.length; i++) {
/* 130 */       this.ignoredComponents[i] = new ArrayList();
/*     */     }
/* 132 */     this.stopOnNull = stopOnNull;
/*     */   }
/*     */   
/*     */   public LoggerView(java.util.List<? extends LogEvent> initialList) {
/* 136 */     this();
/* 137 */     if (initialList != null)
/* 138 */       this.buffer.addAll(initialList);
/* 139 */     setEnabled(true);
/*     */   }
/*     */   
/*     */   private static final int COLOR_INFO = 0;
/*     */   private static final int COLOR_WARN = 1;
/*     */   private LoggerView(LoggerView other)
/*     */   {
/* 146 */     this.buffer.addAll(other.buffer);
/*     */     
/* 148 */     for (int i = 0; i < this.ignoredComponents.length; i++) {
/* 149 */       this.ignoredComponents[i] = new ArrayList();
/*     */     }
/*     */     
/* 152 */     this.stopOnNull = other.stopOnNull;
/*     */     
/* 154 */     setEnabled(other.bEnabled);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCloneable()
/*     */   {
/* 160 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public UISWTViewCoreEventListener getClone()
/*     */   {
/* 166 */     return new LoggerView(this);
/*     */   }
/*     */   
/*     */   private void initialize(Composite composite) {
/* 170 */     this.display = composite.getDisplay();
/*     */     
/* 172 */     Colors.getInstance().addColorsChangedListener(this);
/* 173 */     parameterChanged("Color");
/*     */     
/* 175 */     this.panel = new Composite(composite, 0);
/* 176 */     GridLayout layout = new GridLayout();
/* 177 */     layout.marginHeight = 0;
/* 178 */     layout.marginWidth = 0;
/* 179 */     layout.verticalSpacing = 2;
/* 180 */     layout.numColumns = 2;
/* 181 */     this.panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 185 */     this.consoleText = new StyledText(this.panel, 776);
/*     */     
/* 187 */     GridData gd = new GridData(1808);
/* 188 */     gd.horizontalSpan = 2;
/* 189 */     Utils.setLayoutData(this.consoleText, gd);
/*     */     
/*     */ 
/* 192 */     this.consoleText.addListener(11, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 194 */         GC gc = new GC(LoggerView.this.consoleText);
/* 195 */         int charWidth = gc.getFontMetrics().getAverageCharWidth();
/* 196 */         gc.dispose();
/*     */         
/* 198 */         int areaWidth = LoggerView.this.consoleText.getBounds().width;
/* 199 */         LoggerView.this.consoleText.setTabs(areaWidth / 6 / charWidth);
/*     */       }
/*     */       
/* 202 */     });
/* 203 */     this.consoleText.addListener(1, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 205 */         int key = event.character;
/* 206 */         if ((key <= 26) && (key > 0)) {
/* 207 */           key += 96;
/*     */         }
/* 209 */         if (((event.stateMask & SWT.MOD1) > 0) && (key == 97)) {
/* 210 */           ((StyledText)event.widget).selectAll();
/*     */         }
/*     */         
/*     */       }
/* 214 */     });
/* 215 */     ScrollBar sb = this.consoleText.getVerticalBar();
/* 216 */     sb.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 218 */         LoggerView.this.bAutoScroll = false;
/* 219 */         if ((LoggerView.this.buttonAutoScroll != null) && (!LoggerView.this.buttonAutoScroll.isDisposed())) {
/* 220 */           LoggerView.this.buttonAutoScroll.setSelection(false);
/*     */         }
/*     */       }
/* 223 */     });
/* 224 */     Composite cLeft = new Composite(this.panel, 0);
/* 225 */     layout = new GridLayout();
/* 226 */     layout.marginHeight = 0;
/* 227 */     layout.marginWidth = 0;
/* 228 */     layout.verticalSpacing = 1;
/* 229 */     cLeft.setLayout(layout);
/* 230 */     gd = new GridData(128, 16384, false, false);
/* 231 */     Utils.setLayoutData(cLeft, gd);
/*     */     
/* 233 */     Button buttonPause = new Button(cLeft, 32);
/* 234 */     Messages.setLanguageText(buttonPause, "LoggerView.pause");
/* 235 */     gd = new GridData();
/* 236 */     Utils.setLayoutData(buttonPause, gd);
/* 237 */     buttonPause.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 239 */         if ((e.widget == null) || (!(e.widget instanceof Button)))
/* 240 */           return;
/* 241 */         Button btn = (Button)e.widget;
/* 242 */         LoggerView.this.bPaused = btn.getSelection();
/* 243 */         if ((!LoggerView.this.bPaused) && (LoggerView.this.buffer != null)) {
/* 244 */           LoggerView.this.refresh();
/*     */         }
/*     */         
/*     */       }
/* 248 */     });
/* 249 */     Button buttonRealtime = new Button(cLeft, 32);
/* 250 */     Messages.setLanguageText(buttonRealtime, "LoggerView.realtime");
/* 251 */     gd = new GridData();
/* 252 */     Utils.setLayoutData(buttonRealtime, gd);
/* 253 */     buttonRealtime.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 255 */         if ((e.widget == null) || (!(e.widget instanceof Button)))
/* 256 */           return;
/* 257 */         Button btn = (Button)e.widget;
/* 258 */         LoggerView.this.bRealtime = btn.getSelection();
/*     */       }
/*     */       
/* 261 */     });
/* 262 */     this.buttonAutoScroll = new Button(cLeft, 32);
/* 263 */     Messages.setLanguageText(this.buttonAutoScroll, "LoggerView.autoscroll");
/* 264 */     gd = new GridData();
/* 265 */     Utils.setLayoutData(this.buttonAutoScroll, gd);
/* 266 */     this.buttonAutoScroll.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 268 */         if ((e.widget == null) || (!(e.widget instanceof Button)))
/* 269 */           return;
/* 270 */         Button btn = (Button)e.widget;
/* 271 */         LoggerView.this.bAutoScroll = btn.getSelection();
/*     */       }
/* 273 */     });
/* 274 */     this.buttonAutoScroll.setSelection(true);
/*     */     
/* 276 */     Button buttonClear = new Button(cLeft, 8);
/* 277 */     Messages.setLanguageText(buttonClear, "LoggerView.clear");
/* 278 */     gd = new GridData();
/* 279 */     Utils.setLayoutData(buttonClear, gd);
/* 280 */     buttonClear.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 282 */         LoggerView.this.consoleText.setText("");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 288 */     });
/* 289 */     String sFilterPrefix = "ConfigView.section.logging.filter";
/* 290 */     Group gLogIDs = new Group(this.panel, 0);
/* 291 */     Messages.setLanguageText(gLogIDs, "LoggerView.filter");
/* 292 */     layout = new GridLayout();
/* 293 */     layout.marginHeight = 0;
/* 294 */     layout.numColumns = 2;
/* 295 */     gLogIDs.setLayout(layout);
/* 296 */     gd = new GridData();
/* 297 */     Utils.setLayoutData(gLogIDs, gd);
/*     */     
/* 299 */     Label label = new Label(gLogIDs, 0);
/* 300 */     Messages.setLanguageText(label, "ConfigView.section.logging.level");
/* 301 */     Utils.setLayoutData(label, new GridData());
/*     */     
/* 303 */     final Label labelCatFilter = new Label(gLogIDs, 0);
/* 304 */     Utils.setLayoutData(labelCatFilter, new GridData(256));
/*     */     
/* 306 */     final org.eclipse.swt.widgets.List listLogTypes = new org.eclipse.swt.widgets.List(gLogIDs, 2564);
/*     */     
/* 308 */     gd = new GridData(0, 1, false, false);
/* 309 */     Utils.setLayoutData(listLogTypes, gd);
/*     */     
/* 311 */     final int[] logTypes = { 0, 1, 3 };
/*     */     
/* 313 */     for (int i = 0; i < logTypes.length; i++) {
/* 314 */       listLogTypes.add(MessageText.getString("ConfigView.section.logging.log" + i + "type"));
/*     */     }
/* 316 */     listLogTypes.select(0);
/*     */     
/* 318 */     LogIDs[] logIDs = FileLogging.configurableLOGIDs;
/*     */     
/*     */ 
/* 321 */     Composite cChecksAndButtons = new Composite(gLogIDs, 0);
/* 322 */     layout = new GridLayout(2, false);
/* 323 */     layout.marginHeight = 0;
/* 324 */     layout.marginWidth = 0;
/* 325 */     cChecksAndButtons.setLayout(layout);
/* 326 */     Utils.setLayoutData(cChecksAndButtons, new GridData());
/*     */     
/* 328 */     final Composite cChecks = new Composite(cChecksAndButtons, 0);
/* 329 */     RowLayout rowLayout = new RowLayout(512);
/* 330 */     rowLayout.wrap = true;
/* 331 */     rowLayout.marginLeft = 0;
/* 332 */     rowLayout.marginRight = 0;
/* 333 */     rowLayout.marginTop = 0;
/* 334 */     rowLayout.marginBottom = 0;
/* 335 */     cChecks.setLayout(rowLayout);
/*     */     
/* 337 */     SelectionAdapter buttonClickListener = new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 339 */         int index = listLogTypes.getSelectionIndex();
/* 340 */         if ((index < 0) || (index >= logTypes.length))
/* 341 */           return;
/* 342 */         Button item = (Button)e.widget;
/* 343 */         if (item.getSelection()) {
/* 344 */           LoggerView.this.ignoredComponents[index].remove(item.getData("LOGID"));
/*     */         } else
/* 346 */           LoggerView.this.ignoredComponents[index].add(item.getData("LOGID"));
/*     */       }
/*     */     };
/* 349 */     for (int i = 0; i < logIDs.length; i++) {
/* 350 */       Button btn = new Button(cChecks, 32);
/* 351 */       btn.setText(MessageText.getString("ConfigView.section.logging.filter." + logIDs[i], logIDs[i].toString()));
/*     */       
/*     */ 
/* 354 */       btn.setData("LOGID", logIDs[i]);
/*     */       
/* 356 */       btn.addSelectionListener(buttonClickListener);
/*     */       
/* 358 */       if (i == 0) {
/* 359 */         gd = new GridData(4, 4, false, false, 1, 2);
/* 360 */         gd.heightHint = ((btn.computeSize(-1, -1).y + 2) * 3);
/* 361 */         Utils.setLayoutData(cChecks, gd);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 366 */     listLogTypes.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 368 */         int index = listLogTypes.getSelectionIndex();
/* 369 */         if ((index < 0) || (index >= logTypes.length)) {
/* 370 */           return;
/*     */         }
/* 372 */         labelCatFilter.setText(MessageText.getString("ConfigView.section.logging.showLogsFor", listLogTypes.getSelection()));
/*     */         
/*     */ 
/*     */ 
/* 376 */         Control[] items = cChecks.getChildren();
/* 377 */         for (int i = 0; i < items.length; i++) {
/* 378 */           if ((items[i] instanceof Button)) {
/* 379 */             LogIDs ID = (LogIDs)items[i].getData("LOGID");
/* 380 */             if (ID != null) {
/* 381 */               boolean checked = !LoggerView.this.ignoredComponents[index].contains(ID);
/* 382 */               ((Button)items[i]).setSelection(checked);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 388 */     });
/* 389 */     listLogTypes.notifyListeners(13, null);
/*     */     
/*     */ 
/* 392 */     Button btn = new Button(cChecksAndButtons, 8);
/* 393 */     gd = new GridData();
/* 394 */     Utils.setLayoutData(btn, gd);
/* 395 */     Messages.setLanguageText(btn, "LoggerView.filter.checkAll");
/* 396 */     btn.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 398 */         int index = listLogTypes.getSelectionIndex();
/*     */         
/* 400 */         Control[] items = cChecks.getChildren();
/* 401 */         for (int i = 0; i < items.length; i++) {
/* 402 */           if ((items[i] instanceof Button)) {
/* 403 */             LogIDs ID = (LogIDs)items[i].getData("LOGID");
/* 404 */             if ((ID != null) && (LoggerView.this.ignoredComponents[index].contains(ID))) {
/* 405 */               ((Button)items[i]).setSelection(true);
/* 406 */               LoggerView.this.ignoredComponents[index].remove(ID);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 412 */     });
/* 413 */     btn = new Button(cChecksAndButtons, 8);
/* 414 */     gd = new GridData();
/* 415 */     Utils.setLayoutData(btn, gd);
/* 416 */     Messages.setLanguageText(btn, "LoggerView.filter.uncheckAll");
/* 417 */     btn.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 419 */         int index = listLogTypes.getSelectionIndex();
/*     */         
/* 421 */         Control[] items = cChecks.getChildren();
/* 422 */         for (int i = 0; i < items.length; i++) {
/* 423 */           if ((items[i] instanceof Button)) {
/* 424 */             LogIDs ID = (LogIDs)items[i].getData("LOGID");
/* 425 */             if ((ID != null) && (!LoggerView.this.ignoredComponents[index].contains(ID))) {
/* 426 */               ((Button)items[i]).setSelection(false);
/* 427 */               LoggerView.this.ignoredComponents[index].add(ID);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 433 */     });
/* 434 */     Composite cBottom = new Composite(this.panel, 0);
/* 435 */     gd = new GridData(4, 4, true, false);
/* 436 */     gd.horizontalSpan = 2;
/* 437 */     Utils.setLayoutData(cBottom, gd);
/* 438 */     cBottom.setLayout(new GridLayout(2, false));
/*     */     
/*     */ 
/* 441 */     label = new Label(cBottom, 0);
/* 442 */     Utils.setLayoutData(label, new GridData());
/* 443 */     Messages.setLanguageText(label, "LoggerView.includeOnly");
/*     */     
/* 445 */     final Text inclText = new Text(cBottom, 2048);
/* 446 */     gd = new GridData();
/* 447 */     gd.widthHint = 200;
/* 448 */     Utils.setLayoutData(inclText, gd);
/* 449 */     inclText.addModifyListener(new ModifyListener()
/*     */     {
/*     */       public void modifyText(ModifyEvent e) {
/* 452 */         String newExpression = inclText.getText();
/* 453 */         if (newExpression.length() == 0) {
/* 454 */           LoggerView.this.inclusionFilter = null;
/*     */         }
/*     */         else {
/*     */           try
/*     */           {
/* 459 */             LoggerView.this.inclusionFilter = Pattern.compile(newExpression, 2);
/* 460 */             inclText.setBackground(null);
/*     */           }
/*     */           catch (PatternSyntaxException e1) {
/* 463 */             inclText.setBackground(Colors.colorErrorBG);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 468 */     });
/* 469 */     label = new Label(cBottom, 0);
/* 470 */     Utils.setLayoutData(label, new GridData());
/* 471 */     Messages.setLanguageText(label, "LoggerView.excludeAll");
/*     */     
/* 473 */     final Text exclText = new Text(cBottom, 2048);
/* 474 */     gd = new GridData();
/* 475 */     gd.widthHint = 200;
/* 476 */     Utils.setLayoutData(exclText, gd);
/* 477 */     exclText.addModifyListener(new ModifyListener()
/*     */     {
/*     */       public void modifyText(ModifyEvent e) {
/* 480 */         String newExpression = exclText.getText();
/* 481 */         if (newExpression.length() == 0) {
/* 482 */           LoggerView.this.exclusionFilter = null;
/*     */         }
/*     */         else {
/*     */           try
/*     */           {
/* 487 */             LoggerView.this.exclusionFilter = Pattern.compile(newExpression, 2);
/* 488 */             exclText.setBackground(null);
/*     */           }
/*     */           catch (PatternSyntaxException e1) {
/* 491 */             exclText.setBackground(Colors.colorErrorBG);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 498 */     if (!Logger.isEnabled()) {
/* 499 */       this.consoleText.setText(MessageText.getString("LoggerView.loggingDisabled") + "\n");
/*     */     }
/*     */   }
/*     */   
/*     */   private Composite getComposite()
/*     */   {
/* 505 */     return this.panel;
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 509 */     if (this.bPaused) {
/* 510 */       return;
/*     */     }
/* 512 */     synchronized (this.buffer) {
/* 513 */       if ((this.consoleText == null) || (this.consoleText.isDisposed())) {
/* 514 */         return;
/*     */       }
/* 516 */       for (int i = 0; i < this.buffer.size(); i++) {
/*     */         try {
/* 518 */           LogEvent event = (LogEvent)this.buffer.get(i);
/*     */           
/* 520 */           int nbLinesBefore = this.consoleText.getLineCount();
/* 521 */           if (nbLinesBefore > 1280)
/*     */           {
/* 523 */             this.consoleText.replaceTextRange(0, this.consoleText.getOffsetAtLine(256), "");
/* 524 */             nbLinesBefore = this.consoleText.getLineCount();
/*     */           }
/*     */           
/*     */ 
/* 528 */           StringBuffer buf = new StringBuffer();
/* 529 */           buf.append('\n');
/*     */           
/* 531 */           dateFormatter.format(event.timeStamp, buf, formatPos);
/* 532 */           buf.append("{").append(event.logID).append("} ");
/*     */           
/* 534 */           buf.append(event.text);
/* 535 */           if (event.relatedTo != null) {
/* 536 */             buf.append("; \t| ");
/* 537 */             for (int j = 0; j < event.relatedTo.length; j++) {
/* 538 */               Object obj = event.relatedTo[j];
/* 539 */               if (j > 0)
/* 540 */                 buf.append("; ");
/* 541 */               if ((obj instanceof LogRelation)) {
/* 542 */                 buf.append(((LogRelation)obj).getRelationText());
/* 543 */               } else if (obj != null) {
/* 544 */                 buf.append(obj.getClass().getName()).append(": '").append(obj.toString()).append("'");
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 551 */           String toAppend = buf.toString();
/*     */           
/* 553 */           if (((this.inclusionFilter != null) && (!this.inclusionFilter.matcher(toAppend).find())) || ((this.exclusionFilter == null) || (!this.exclusionFilter.matcher(toAppend).find())))
/*     */           {
/*     */ 
/* 556 */             this.consoleText.append(toAppend);
/*     */             
/* 558 */             int nbLinesNow = this.consoleText.getLineCount();
/* 559 */             int colorIdx = -1;
/* 560 */             if (event.entryType == 0) {
/* 561 */               colorIdx = 0;
/* 562 */             } else if (event.entryType == 1) {
/* 563 */               colorIdx = 1;
/* 564 */             } else if (event.entryType == 3) {
/* 565 */               colorIdx = 2;
/*     */             }
/* 567 */             if ((colors != null) && (colorIdx >= 0)) {
/* 568 */               this.consoleText.setLineBackground(nbLinesBefore, nbLinesNow - nbLinesBefore, colors[colorIdx]);
/*     */             }
/*     */           }
/*     */         } catch (Exception e) {
/* 572 */           PrintStream ps = Logger.getOldStdErr();
/* 573 */           if (ps != null) {
/* 574 */             ps.println("Error writing event to console:");
/* 575 */             e.printStackTrace(ps);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 580 */       this.buffer.clear();
/* 581 */       if (this.bAutoScroll)
/* 582 */         this.consoleText.setTopIndex(this.consoleText.getLineCount());
/*     */     }
/*     */   }
/*     */   
/*     */   private void delete() {
/* 587 */     Logger.removeListener(this);
/* 588 */     if ((this.panel != null) && (!this.panel.isDisposed()))
/* 589 */       this.panel.dispose();
/* 590 */     Colors.getInstance().removeColorsChangedListener(this);
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 594 */     return MessageText.getString("ConsoleView.title.full");
/*     */   }
/*     */   
/*     */   public synchronized void log(LogEvent event)
/*     */   {
/* 599 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 600 */       return;
/*     */     }
/* 602 */     if (this.ignoredComponents[logTypeToIndex(event.entryType)].contains(event.logID)) {
/* 603 */       return;
/*     */     }
/*     */     
/* 606 */     boolean bMatch = (event.logID == LogIDs.STDERR) || (this.filter == null);
/*     */     
/* 608 */     if ((!bMatch) && (event.relatedTo != null)) {
/* 609 */       for (int i = 0; (!bMatch) && (i < event.relatedTo.length); i++) {
/* 610 */         Object obj = event.relatedTo[i];
/*     */         
/* 612 */         if (obj != null)
/*     */         {
/*     */ 
/* 615 */           for (int j = 0; (!bMatch) && (j < this.filter.length); j++) {
/* 616 */             if ((obj instanceof LogRelation))
/*     */             {
/*     */ 
/* 619 */               Object newObj = ((LogRelation)obj).queryForClass(this.filter[j].getClass());
/*     */               
/* 621 */               if (newObj != null) {
/* 622 */                 obj = newObj;
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 627 */             if (obj == this.filter[j])
/* 628 */               bMatch = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 633 */     if (bMatch) {
/* 634 */       synchronized (this.buffer) {
/* 635 */         if (this.buffer.size() >= 200)
/* 636 */           this.buffer.removeFirst();
/* 637 */         this.buffer.add(event);
/*     */       }
/*     */       
/* 640 */       if ((this.bRealtime) && (!this.bPaused)) {
/* 641 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 643 */             LoggerView.this.refresh();
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setFilter(Object[] _filter) {
/* 651 */     synchronized (this) {
/* 652 */       this.filter = _filter;
/*     */     }
/*     */     
/* 655 */     clearConsole();
/*     */   }
/*     */   
/*     */   private void clearConsole() {
/* 659 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 660 */       return;
/*     */     }
/* 662 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 664 */         if ((LoggerView.this.consoleText != null) && (!LoggerView.this.consoleText.isDisposed())) {
/* 665 */           LoggerView.this.consoleText.setText("");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setEnabled(boolean on) {
/* 672 */     if (this.bEnabled == on)
/* 673 */       return;
/* 674 */     this.bEnabled = on;
/* 675 */     if (on) {
/* 676 */       Logger.addListener(this);
/*     */     } else {
/* 678 */       Logger.removeListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPluginViewName()
/*     */   {
/* 685 */     return "Console";
/*     */   }
/*     */   
/*     */   private void dataSourceChanged(Object newDataSource)
/*     */   {
/* 690 */     if (newDataSource == null) {
/* 691 */       if (this.stopOnNull) {
/* 692 */         setEnabled(false);
/* 693 */         return;
/*     */       }
/* 695 */       setFilter(null);
/* 696 */     } else if ((newDataSource instanceof Object[])) {
/* 697 */       setFilter((Object[])newDataSource);
/* 698 */     } else { if ((newDataSource instanceof Boolean)) {
/* 699 */         this.stopOnNull = ((Boolean)newDataSource).booleanValue();
/* 700 */         return;
/*     */       }
/* 702 */       setFilter(new Object[] { newDataSource });
/*     */     }
/*     */     
/* 705 */     setEnabled(true);
/*     */   }
/*     */   
/*     */   private int logTypeToIndex(int entryType) {
/* 709 */     switch (entryType) {
/*     */     case 0: 
/* 711 */       return 0;
/*     */     case 1: 
/* 713 */       return 1;
/*     */     case 3: 
/* 715 */       return 2;
/*     */     }
/* 717 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final int COLOR_ERR = 2;
/*     */   
/*     */   private static final int PREFERRED_LINES = 256;
/*     */   
/*     */   private static final int MAX_LINES = 1280;
/*     */   
/*     */   public static final String MSGID_PREFIX = "ConsoleView";
/*     */   private Display display;
/*     */   private Composite panel;
/*     */   private Pattern inclusionFilter;
/*     */   private Pattern exclusionFilter;
/*     */   private UISWTView swtView;
/*     */   public void parameterChanged(String parameterName)
/*     */   {
/* 735 */     if (parameterName.startsWith("Color")) {
/* 736 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 738 */           if ((LoggerView.this.display == null) || (LoggerView.this.display.isDisposed())) {
/* 739 */             return;
/*     */           }
/* 741 */           if (LoggerView.colors == null) {
/* 742 */             LoggerView.access$1102(new Color[3]);
/*     */           }
/* 744 */           Color[] newColors = { Colors.blues[2], Colors.colorWarning, Colors.red_ConsoleView };
/*     */           
/* 746 */           boolean bColorChanged = false;
/*     */           
/* 748 */           for (int i = 0; i < newColors.length; i++) {
/* 749 */             if ((LoggerView.colors[i] == null) || (LoggerView.colors[i].isDisposed())) {
/* 750 */               LoggerView.colors[i] = newColors[i];
/* 751 */               bColorChanged = true;
/*     */             }
/*     */           }
/*     */           
/* 755 */           if ((bColorChanged) && (LoggerView.this.consoleText != null))
/*     */           {
/* 757 */             String text = LoggerView.this.consoleText.getText();
/* 758 */             LoggerView.this.consoleText.setText(text);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 766 */     switch (event.getType()) {
/*     */     case 0: 
/* 768 */       event.getView().setDestroyOnDeactivate(false);
/* 769 */       this.swtView = ((UISWTView)event.getData());
/* 770 */       this.swtView.setTitle(getFullTitle());
/* 771 */       break;
/*     */     
/*     */     case 7: 
/* 774 */       delete();
/* 775 */       break;
/*     */     
/*     */     case 2: 
/* 778 */       initialize((Composite)event.getData());
/* 779 */       break;
/*     */     
/*     */     case 6: 
/* 782 */       Messages.updateLanguageForControl(getComposite());
/* 783 */       this.swtView.setTitle(getFullTitle());
/* 784 */       break;
/*     */     
/*     */     case 1: 
/* 787 */       dataSourceChanged(event.getData());
/* 788 */       break;
/*     */     
/*     */     case 3: 
/*     */       break;
/*     */     
/*     */     case 5: 
/* 794 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 798 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/LoggerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */