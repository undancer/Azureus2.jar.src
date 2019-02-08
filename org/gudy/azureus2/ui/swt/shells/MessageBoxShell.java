/*      */ package org.gudy.azureus2.ui.swt.shells;
/*      */ 
/*      */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*      */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*      */ import com.aelitis.azureus.ui.common.RememberedDecisionsManager;
/*      */ import com.aelitis.azureus.ui.swt.UISkinnableManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UISkinnableSWTListener;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.browser.LocationEvent;
/*      */ import org.eclipse.swt.browser.OpenWindowListener;
/*      */ import org.eclipse.swt.browser.ProgressEvent;
/*      */ import org.eclipse.swt.browser.ProgressListener;
/*      */ import org.eclipse.swt.browser.StatusTextEvent;
/*      */ import org.eclipse.swt.browser.StatusTextListener;
/*      */ import org.eclipse.swt.browser.WindowEvent;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.events.TraverseEvent;
/*      */ import org.eclipse.swt.events.TraverseListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
/*      */ 
/*      */ public class MessageBoxShell implements UIFunctionsUserPrompter
/*      */ {
/*      */   public static final String STATUS_TEXT_CLOSE = "__VUZE__MessageBoxShell__CLOSE";
/*      */   private static final int MIN_SIZE_X_DEFAULT = 300;
/*      */   private static final int MIN_SIZE_Y_DEFAULT = 120;
/*      */   private static final int MAX_SIZE_X_DEFAULT = 500;
/*      */   private static final int MIN_BUTTON_SIZE = 70;
/*   67 */   private static int numOpen = 0;
/*      */   
/*      */   private Shell parent;
/*      */   
/*   71 */   private int min_size_x = 300;
/*   72 */   private int min_size_y = 120;
/*   73 */   private int max_size_x = 500;
/*      */   
/*      */   private final String title;
/*      */   
/*      */   private final String text;
/*      */   
/*      */   private String[] buttons;
/*      */   
/*      */   private Integer[] buttonVals;
/*      */   
/*      */   private int defaultButtonPos;
/*      */   
/*   85 */   private String rememberID = null;
/*      */   
/*   87 */   private String rememberText = null;
/*      */   
/*   89 */   private boolean rememberByDefault = false;
/*      */   
/*   91 */   private int rememberOnlyIfButtonPos = -1;
/*      */   
/*   93 */   private int autoCloseInMS = 0;
/*      */   
/*      */   private String html;
/*      */   
/*      */   private String url;
/*      */   
/*      */   private boolean squish;
/*      */   
/*  101 */   private boolean autoClosed = false;
/*      */   
/*      */   private Object[] relatedObjects;
/*      */   
/*      */   private Image imgLeft;
/*      */   
/*      */   protected Color urlColor;
/*      */   
/*  109 */   private boolean handleHTML = true;
/*      */   
/*      */   private Image iconImage;
/*      */   
/*      */   private boolean browser_follow_links;
/*      */   
/*      */   protected boolean isRemembered;
/*      */   
/*      */   private String iconImageID;
/*      */   
/*      */   private UserPrompterResultListener resultListener;
/*      */   
/*      */   private int result;
/*      */   
/*      */   private Listener filterListener;
/*      */   
/*      */   private Shell shell;
/*      */   
/*      */   private boolean opened;
/*      */   
/*      */   private boolean useTextBox;
/*      */   
/*      */   private String cbMessageID;
/*      */   
/*      */   private int cbMinUserMode;
/*      */   
/*      */   private boolean cbEnabled;
/*      */   
/*      */   private String instanceID;
/*      */   
/*      */   private boolean modal;
/*      */   
/*  141 */   private static Map<String, MessageBoxShell> mapInstances = new java.util.HashMap(1);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void open(Shell parent, String title, String text, String[] buttons, int defaultOption, String rememberID, String rememberText, boolean bRememberByDefault, int autoCloseInMS, UserPrompterResultListener l)
/*      */   {
/*  148 */     MessageBoxShell messageBoxShell = new MessageBoxShell(title, text, buttons, defaultOption);
/*      */     
/*  150 */     messageBoxShell.setRemember(rememberID, bRememberByDefault, rememberText);
/*  151 */     messageBoxShell.setAutoCloseInMS(autoCloseInMS);
/*  152 */     messageBoxShell.setParent(parent);
/*  153 */     messageBoxShell.open(l);
/*      */   }
/*      */   
/*      */   public static boolean isOpen() {
/*  157 */     return numOpen > 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MessageBoxShell(String title, String text, String[] buttons, int defaultOption)
/*      */   {
/*  168 */     this.title = title;
/*  169 */     this.text = text;
/*  170 */     this.buttons = (buttons == null ? new String[0] : buttons);
/*  171 */     this.defaultButtonPos = defaultOption;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public MessageBoxShell(Shell parent, String title, String text, String[] buttons, int defaultOption)
/*      */   {
/*  180 */     this(title, text, buttons, defaultOption);
/*  181 */     this.parent = parent;
/*      */   }
/*      */   
/*      */   public MessageBoxShell(String title, String text) {
/*  185 */     this(title, text, null, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MessageBoxShell(int style, String keyPrefix, String[] textParams)
/*      */   {
/*  199 */     if ((style & 0xFE0) == 0)
/*      */     {
/*  201 */       style |= 0x20;
/*      */     }
/*  203 */     Object[] buttonInfo = swtButtonStylesToText(style);
/*      */     
/*  205 */     this.title = MessageText.getString(keyPrefix + ".title");
/*  206 */     this.text = MessageText.getString(keyPrefix + ".text", textParams);
/*  207 */     this.buttons = ((String[])buttonInfo[0]);
/*  208 */     this.defaultButtonPos = 0;
/*  209 */     this.rememberID = null;
/*  210 */     this.rememberText = null;
/*  211 */     this.rememberByDefault = false;
/*  212 */     this.autoCloseInMS = -1;
/*  213 */     this.buttonVals = ((Integer[])buttonInfo[1]);
/*      */     
/*  215 */     setLeftImage(style & 0x1F);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MessageBoxShell(int style, String title, String text)
/*      */   {
/*  227 */     if ((style & 0xFE0) == 0)
/*      */     {
/*  229 */       style |= 0x20;
/*      */     }
/*      */     
/*  232 */     Object[] buttonInfo = swtButtonStylesToText(style);
/*      */     
/*  234 */     this.title = title;
/*  235 */     this.text = text;
/*  236 */     this.buttons = ((String[])buttonInfo[0]);
/*  237 */     this.defaultButtonPos = 0;
/*  238 */     this.rememberID = null;
/*  239 */     this.rememberText = null;
/*  240 */     this.rememberByDefault = false;
/*  241 */     this.autoCloseInMS = -1;
/*  242 */     this.buttonVals = ((Integer[])buttonInfo[1]);
/*      */     
/*  244 */     setLeftImage(style & 0x1F);
/*      */   }
/*      */   
/*      */   public void setDefaultButtonUsingStyle(int defaultStyle) {
/*  248 */     Object[] defaultButtonInfo = swtButtonStylesToText(defaultStyle);
/*      */     
/*  250 */     int defaultIndex = 0;
/*  251 */     if (defaultButtonInfo.length > 0) {
/*  252 */       String name = ((String[])(String[])defaultButtonInfo[0])[0];
/*      */       
/*  254 */       for (int i = 0; i < this.buttons.length; i++) {
/*  255 */         if (this.buttons[i].equals(name)) {
/*  256 */           defaultIndex = i;
/*  257 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  261 */     this.defaultButtonPos = defaultIndex;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public int open()
/*      */   {
/*  272 */     open(false);
/*  273 */     return waitUntilClosed();
/*      */   }
/*      */   
/*      */   public void open(UserPrompterResultListener l) {
/*  277 */     this.resultListener = l;
/*  278 */     open(false);
/*      */   }
/*      */   
/*      */   private void triggerResultListener(final int returnVal) {
/*  282 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  284 */         if (MessageBoxShell.this.resultListener == null) {
/*  285 */           return;
/*      */         }
/*  287 */         int realResult = MessageBoxShell.this.getButtonVal(returnVal);
/*  288 */         MessageBoxShell.this.resultListener.prompterClosed(realResult);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private int getButtonVal(int buttonPos) {
/*  294 */     if (this.buttonVals == null) {
/*  295 */       return buttonPos;
/*      */     }
/*  297 */     if ((buttonPos < 0) || (buttonPos >= this.buttonVals.length)) {
/*  298 */       return 256;
/*      */     }
/*  300 */     return this.buttonVals[buttonPos].intValue();
/*      */   }
/*      */   
/*      */   private int getButtonPos(int buttonVal) {
/*  304 */     if (this.buttonVals == null) {
/*  305 */       return buttonVal;
/*      */     }
/*  307 */     for (int i = 0; i < this.buttonVals.length; i++) {
/*  308 */       if (this.buttonVals[i].intValue() == buttonVal) {
/*  309 */         return i;
/*      */       }
/*      */     }
/*  312 */     return -1;
/*      */   }
/*      */   
/*      */   private void open(boolean useCustomShell) {
/*  316 */     if (this.rememberID != null) {
/*  317 */       int rememberedDecision = RememberedDecisionsManager.getRememberedDecision(this.rememberID);
/*  318 */       if ((rememberedDecision >= 0) && ((this.rememberOnlyIfButtonPos == -1) || (this.rememberOnlyIfButtonPos == getButtonPos(rememberedDecision))))
/*      */       {
/*  320 */         this.result = getButtonPos(rememberedDecision);
/*  321 */         triggerResultListener(this.result);
/*  322 */         return;
/*      */       }
/*      */     }
/*      */     
/*  326 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  328 */         MessageBoxShell.this._open();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void _open()
/*      */   {
/*  336 */     if (this.instanceID != null) {
/*  337 */       if (mapInstances.containsKey(this.instanceID)) {
/*  338 */         MessageBoxShell mb = (MessageBoxShell)mapInstances.get(this.instanceID);
/*  339 */         if ((mb.shell != null) && (!mb.shell.isDisposed())) {
/*  340 */           mb.shell.open();
/*  341 */           return;
/*      */         }
/*      */       }
/*  344 */       mapInstances.put(this.instanceID, this);
/*      */     }
/*      */     
/*  347 */     this.result = -1;
/*      */     
/*  349 */     boolean ourParent = false;
/*  350 */     if ((this.parent == null) || (this.parent.isDisposed())) {
/*  351 */       this.parent = Utils.findAnyShell();
/*  352 */       ourParent = true;
/*  353 */       if ((this.parent == null) || (this.parent.isDisposed())) {
/*  354 */         triggerResultListener(this.result);
/*  355 */         return;
/*      */       }
/*      */     }
/*      */     
/*  359 */     final Display display = this.parent.getDisplay();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  366 */     int shell_style = 2160;
/*      */     
/*  368 */     if (this.modal)
/*      */     {
/*  370 */       shell_style |= 0x10000;
/*      */     }
/*      */     
/*  373 */     this.shell = org.gudy.azureus2.ui.swt.components.shell.ShellFactory.createShell(this.parent, shell_style);
/*  374 */     if (this.title != null) {
/*  375 */       this.shell.setText(this.title);
/*      */     }
/*  377 */     this.shell.setBackgroundMode(1);
/*      */     
/*  379 */     this.shell.addListener(12, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  381 */         MessageBoxShell.mapInstances.remove(MessageBoxShell.this.instanceID);
/*      */         
/*  383 */         if (MessageBoxShell.this.iconImageID != null) {
/*  384 */           ImageLoader.getInstance().releaseImage(MessageBoxShell.this.iconImageID);
/*      */         }
/*  386 */         MessageBoxShell.this.triggerResultListener(MessageBoxShell.this.result);
/*  387 */         if ((display != null) && (!display.isDisposed()) && (MessageBoxShell.this.filterListener != null)) {
/*  388 */           display.removeFilter(31, MessageBoxShell.this.filterListener);
/*      */         }
/*      */         
/*  391 */         MessageBoxShell.access$910();
/*      */       }
/*      */       
/*  394 */     });
/*  395 */     GridLayout gridLayout = new GridLayout();
/*      */     
/*  397 */     if (this.squish) {
/*  398 */       gridLayout.verticalSpacing = 0;
/*  399 */       gridLayout.horizontalSpacing = 0;
/*  400 */       gridLayout.marginLeft = 0;
/*  401 */       gridLayout.marginRight = 0;
/*  402 */       gridLayout.marginTop = 0;
/*  403 */       gridLayout.marginBottom = 0;
/*  404 */       gridLayout.marginWidth = 0;
/*  405 */       gridLayout.marginHeight = 0;
/*      */     }
/*      */     
/*  408 */     this.shell.setLayout(gridLayout);
/*  409 */     Utils.setShellIcon(this.shell);
/*      */     
/*  411 */     UISkinnableSWTListener[] listeners = UISkinnableManagerSWT.getInstance().getSkinnableListeners(MessageBoxShell.class.toString());
/*      */     
/*  413 */     for (int i = 0; i < listeners.length; i++) {
/*  414 */       listeners[i].skinBeforeComponents(this.shell, this, this.relatedObjects);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  420 */     Composite textComposite = this.shell;
/*  421 */     if (this.imgLeft != null) {
/*  422 */       textComposite = new Composite(this.shell, 0);
/*  423 */       textComposite.setForeground(this.shell.getForeground());
/*  424 */       GridLayout gl = new GridLayout(2, false);
/*  425 */       gl.horizontalSpacing = 10;
/*  426 */       Utils.setLayout(textComposite, gl);
/*  427 */       Utils.setLayoutData(textComposite, new GridData(1808));
/*  428 */       Label lblImage = new Label(textComposite, 0);
/*  429 */       lblImage.setImage(this.imgLeft);
/*  430 */       Utils.setLayoutData(lblImage, new GridData(2));
/*  431 */     } else if (!this.squish) {
/*  432 */       textComposite = new Composite(this.shell, 0);
/*  433 */       GridLayout gl = new GridLayout(2, false);
/*  434 */       gl.marginWidth = 5;
/*  435 */       Utils.setLayout(textComposite, gl);
/*  436 */       Utils.setLayoutData(textComposite, new GridData(1808));
/*      */     }
/*      */     Control linkControl;
/*      */     Control linkControl;
/*  440 */     if ((this.text != null) && (this.text.length() > 0)) { Control linkControl;
/*  441 */       if (useTextBox()) {
/*  442 */         linkControl = createTextBox(textComposite, this.text);
/*      */       } else {
/*  444 */         linkControl = createLinkLabel(textComposite, this.text);
/*      */       }
/*      */     } else {
/*  447 */       linkControl = null;
/*      */     }
/*      */     
/*  450 */     if (((this.html != null) && (this.html.length() > 0)) || ((this.url != null) && (this.url.length() > 0)))
/*      */     {
/*      */       try {
/*  453 */         final BrowserWrapper browser = Utils.createSafeBrowser(this.shell, 0);
/*  454 */         if ((this.url != null) && (this.url.length() > 0)) {
/*  455 */           browser.setUrl(this.url);
/*      */         } else {
/*  457 */           browser.setText(this.html);
/*      */         }
/*  459 */         GridData gd = new GridData(1808);
/*  460 */         gd.heightHint = 200;
/*      */         
/*  462 */         Utils.setLayoutData(browser.getControl(), gd);
/*  463 */         browser.addProgressListener(new ProgressListener() {
/*      */           public void completed(ProgressEvent event) {
/*  465 */             if ((MessageBoxShell.this.shell == null) || (MessageBoxShell.this.shell.isDisposed())) {
/*  466 */               return;
/*      */             }
/*  468 */             browser.addLocationListener(new org.eclipse.swt.browser.LocationListener() {
/*      */               public void changing(LocationEvent event) {
/*  470 */                 event.doit = MessageBoxShell.this.browser_follow_links;
/*      */               }
/*      */               
/*      */ 
/*      */               public void changed(LocationEvent event) {}
/*  475 */             });
/*  476 */             browser.addOpenWindowListener(new OpenWindowListener() {
/*      */               public void open(WindowEvent event) {
/*  478 */                 event.required = true;
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void changed(ProgressEvent event) {}
/*  487 */         });
/*  488 */         browser.addStatusTextListener(new StatusTextListener() {
/*      */           public void changed(StatusTextEvent event) {
/*  490 */             if ("__VUZE__MessageBoxShell__CLOSE".equals(event.text))
/*      */             {
/*      */ 
/*  493 */               Utils.execSWTThreadLater(0, new Runnable() {
/*      */                 public void run() {
/*  495 */                   if ((!MessageBoxShell.5.this.val$browser.isDisposed()) && (!MessageBoxShell.this.shell.isDisposed())) {
/*  496 */                     MessageBoxShell.this.shell.close();
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  506 */         Debug.out(e);
/*  507 */         if (this.html != null) {
/*  508 */           Text text = new Text(this.shell, 2120);
/*  509 */           text.setText(this.html);
/*  510 */           GridData gd = new GridData(1808);
/*  511 */           gd.heightHint = 200;
/*  512 */           Utils.setLayoutData(text, gd);
/*      */         }
/*      */       }
/*      */       
/*  516 */       if (linkControl != null) {
/*  517 */         GridData gridData = new GridData(768);
/*  518 */         Utils.setLayoutData(linkControl, gridData);
/*      */       }
/*      */     }
/*  521 */     else if (linkControl != null) {
/*  522 */       GridData gridData = new GridData(1808);
/*  523 */       Utils.setLayoutData(linkControl, gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  528 */     if ((!this.squish) && ((this.autoCloseInMS > 0) || (this.rememberID != null) || ((this.cbMessageID != null) && (Utils.getUserMode() >= this.cbMinUserMode))))
/*      */     {
/*  530 */       Label lblPadding = new Label(this.shell, 0);
/*  531 */       GridData gridData = new GridData(768);
/*  532 */       gridData.heightHint = 5;
/*  533 */       Utils.setLayoutData(lblPadding, gridData);
/*      */     }
/*      */     
/*      */ 
/*  537 */     if (this.autoCloseInMS > 0) {
/*  538 */       final BufferedLabel lblCloseIn = new BufferedLabel(this.shell, 536870976);
/*  539 */       lblCloseIn.setForeground(this.shell.getForeground());
/*  540 */       GridData gridData = new GridData(768);
/*  541 */       if (!this.squish) {
/*  542 */         gridData.horizontalIndent = 5;
/*      */       }
/*  544 */       lblCloseIn.setText(MessageText.getString("popup.closing.in", new String[] { String.valueOf(this.autoCloseInMS / 1000) }));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  550 */       Utils.setLayoutData(lblCloseIn, gridData);
/*  551 */       long endOn = SystemTime.getCurrentTime() + this.autoCloseInMS;
/*  552 */       lblCloseIn.setData("CloseOn", new Long(endOn));
/*  553 */       SimpleTimer.addPeriodicEvent("autoclose", 500L, new TimerEventPerformer() {
/*      */         public void perform(TimerEvent event) {
/*  555 */           if (MessageBoxShell.this.shell.isDisposed()) {
/*  556 */             event.cancel();
/*  557 */             return;
/*      */           }
/*  559 */           Utils.execSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/*  561 */               if (!MessageBoxShell.this.shell.isDisposed()) {
/*  562 */                 boolean bDelayPaused = MessageBoxShell.6.this.val$lblCloseIn.getData("DelayPaused") != null;
/*  563 */                 if (bDelayPaused) {
/*  564 */                   return;
/*      */                 }
/*      */                 
/*  567 */                 long endOn = ((Long)MessageBoxShell.6.this.val$lblCloseIn.getData("CloseOn")).longValue();
/*  568 */                 if (SystemTime.getCurrentTime() > endOn) {
/*  569 */                   MessageBoxShell.this.result = MessageBoxShell.this.defaultButtonPos;
/*  570 */                   MessageBoxShell.this.autoClosed = true;
/*  571 */                   MessageBoxShell.this.shell.dispose();
/*      */                 } else {
/*  573 */                   String sText = "";
/*      */                   
/*  575 */                   if (MessageBoxShell.6.this.val$lblCloseIn.isDisposed()) {
/*  576 */                     return;
/*      */                   }
/*  578 */                   if (!bDelayPaused) {
/*  579 */                     long delaySecs = (endOn - SystemTime.getCurrentTime()) / 1000L;
/*  580 */                     sText = MessageText.getString("popup.closing.in", new String[] { String.valueOf(delaySecs) });
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*  586 */                   MessageBoxShell.6.this.val$lblCloseIn.setText(sText);
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*  593 */       });
/*  594 */       SimpleTimer.addPeriodicEvent("OverPopup", 100L, new TimerEventPerformer() {
/*  595 */         boolean wasOver = true;
/*      */         
/*  597 */         long lEnterOn = 0L;
/*      */         
/*      */         public void perform(final TimerEvent event) {
/*  600 */           if (MessageBoxShell.this.shell.isDisposed()) {
/*  601 */             event.cancel();
/*  602 */             return;
/*      */           }
/*  604 */           Utils.execSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/*  606 */               if (MessageBoxShell.this.shell.isDisposed()) {
/*  607 */                 event.cancel();
/*  608 */                 return;
/*      */               }
/*  610 */               boolean isOver = MessageBoxShell.this.shell.getBounds().contains(MessageBoxShell.this.shell.getDisplay().getCursorLocation());
/*      */               
/*  612 */               if (isOver != MessageBoxShell.7.this.wasOver) {
/*  613 */                 MessageBoxShell.7.this.wasOver = isOver;
/*  614 */                 if (isOver) {
/*  615 */                   MessageBoxShell.7.this.val$lblCloseIn.setData("DelayPaused", "");
/*  616 */                   MessageBoxShell.7.this.lEnterOn = SystemTime.getCurrentTime();
/*  617 */                   MessageBoxShell.7.this.val$lblCloseIn.setText("");
/*      */                 } else {
/*  619 */                   MessageBoxShell.7.this.val$lblCloseIn.setData("DelayPaused", null);
/*  620 */                   if (MessageBoxShell.7.this.lEnterOn > 0L) {
/*  621 */                     long diff = SystemTime.getCurrentTime() - MessageBoxShell.7.this.lEnterOn;
/*  622 */                     long endOn = ((Long)MessageBoxShell.7.this.val$lblCloseIn.getData("CloseOn")).longValue() + diff;
/*      */                     
/*  624 */                     MessageBoxShell.7.this.val$lblCloseIn.setData("CloseOn", new Long(endOn));
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  634 */     if ((this.cbMessageID != null) && (Utils.getUserMode() >= this.cbMinUserMode)) {
/*  635 */       Button cb = new Button(this.shell, 32);
/*  636 */       cb.addSelectionListener(new SelectionListener()
/*      */       {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  639 */           MessageBoxShell.this.cbEnabled = ((Button)e.widget).getSelection();
/*      */         }
/*      */         
/*      */ 
/*      */         public void widgetDefaultSelected(SelectionEvent e) {}
/*  644 */       });
/*  645 */       Messages.setLanguageText(cb, this.cbMessageID);
/*  646 */       cb.setSelection(this.cbEnabled);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  651 */     Button checkRemember = null;
/*  652 */     if (this.rememberID != null) {
/*  653 */       checkRemember = new Button(this.shell, 32);
/*  654 */       checkRemember.setText(this.rememberText);
/*  655 */       checkRemember.setSelection(this.rememberByDefault);
/*  656 */       this.isRemembered = this.rememberByDefault;
/*  657 */       checkRemember.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  659 */           Button checkRemember = (Button)event.widget;
/*  660 */           MessageBoxShell.this.isRemembered = checkRemember.getSelection();
/*      */         }
/*      */         
/*  663 */       });
/*  664 */       checkRemember.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
/*      */         public void widgetDisposed(DisposeEvent e) {
/*  666 */           Button checkRemember = (Button)e.widget;
/*  667 */           MessageBoxShell.this.isRemembered = ((checkRemember != null) && (checkRemember.getSelection()));
/*  668 */           if ((MessageBoxShell.this.rememberID != null) && (MessageBoxShell.this.isRemembered) && ((MessageBoxShell.this.rememberOnlyIfButtonPos == -1) || (MessageBoxShell.this.rememberOnlyIfButtonPos == MessageBoxShell.this.result)))
/*      */           {
/*      */ 
/*  671 */             RememberedDecisionsManager.setRemembered(MessageBoxShell.this.rememberID, MessageBoxShell.this.getButtonVal(MessageBoxShell.this.result));
/*      */           }
/*      */         }
/*      */       });
/*      */     } else {
/*  676 */       Button spacer = new Button(this.shell, 32);
/*  677 */       spacer.setVisible(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  683 */     if (this.buttons.length > 0) {
/*  684 */       Canvas line = new Canvas(this.shell, 262144);
/*  685 */       line.addListener(9, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  687 */           Rectangle clientArea = ((Canvas)e.widget).getClientArea();
/*  688 */           e.gc.setForeground(e.display.getSystemColor(18));
/*  689 */           e.gc.drawRectangle(clientArea);
/*  690 */           clientArea.y += 1;
/*  691 */           e.gc.setForeground(e.display.getSystemColor(20));
/*  692 */           e.gc.drawRectangle(clientArea);
/*      */         }
/*  694 */       });
/*  695 */       GridData gridData = new GridData(768);
/*  696 */       gridData.heightHint = 2;
/*  697 */       Utils.setLayoutData(line, gridData);
/*      */       
/*  699 */       Composite cButtons = new Composite(this.shell, 0);
/*  700 */       FormLayout layout = new FormLayout();
/*      */       
/*  702 */       cButtons.setLayout(layout);
/*  703 */       gridData = new GridData(128);
/*  704 */       Utils.setLayoutData(cButtons, gridData);
/*      */       
/*  706 */       Control lastButton = null;
/*      */       
/*  708 */       Listener buttonListener = new Listener()
/*      */       {
/*      */         public void handleEvent(Event event) {
/*  711 */           MessageBoxShell.this.result = ((Integer)event.widget.getData()).intValue();
/*  712 */           MessageBoxShell.this.shell.dispose();
/*      */         }
/*      */         
/*      */ 
/*  716 */       };
/*  717 */       int buttonWidth = 0;
/*  718 */       Button[] swtButtons = new Button[this.buttons.length];
/*  719 */       for (int i = 0; i < this.buttons.length; i++) {
/*  720 */         Button button = new Button(cButtons, 8);
/*  721 */         swtButtons[i] = button;
/*  722 */         button.setData(Integer.valueOf(i));
/*  723 */         button.setText(this.buttons[i]);
/*  724 */         button.addListener(13, buttonListener);
/*      */         
/*  726 */         FormData formData = new FormData();
/*  727 */         if (lastButton != null) {
/*  728 */           formData.left = new FormAttachment(lastButton, 5);
/*      */         }
/*      */         
/*  731 */         Utils.setLayoutData(button, formData);
/*      */         
/*  733 */         Point size = button.computeSize(-1, -1);
/*  734 */         if (size.x > buttonWidth) {
/*  735 */           buttonWidth = size.x;
/*      */         }
/*      */         
/*  738 */         if (i == this.defaultButtonPos) {
/*  739 */           button.setFocus();
/*  740 */           this.shell.setDefaultButton(button);
/*      */         }
/*      */         
/*  743 */         lastButton = button;
/*      */       }
/*      */       
/*  746 */       if (buttonWidth > 0) {
/*  747 */         if (buttonWidth < 70) {
/*  748 */           buttonWidth = 70;
/*      */         }
/*  750 */         for (int i = 0; i < this.buttons.length; i++) {
/*  751 */           Point size = swtButtons[i].computeSize(buttonWidth, -1);
/*  752 */           swtButtons[i].setSize(size);
/*  753 */           FormData formData = (FormData)swtButtons[i].getLayoutData();
/*  754 */           formData.width = buttonWidth;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  759 */     this.shell.addTraverseListener(new TraverseListener() {
/*      */       public void keyTraversed(TraverseEvent event) {
/*  761 */         if (event.detail == 2) {
/*  762 */           MessageBoxShell.this.shell.dispose();
/*      */         }
/*      */         
/*      */       }
/*  766 */     });
/*  767 */     this.filterListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  769 */         if (event.detail == 64) {
/*  770 */           event.detail = 16;
/*  771 */           event.doit = true;
/*  772 */         } else if (event.detail == 32) {
/*  773 */           event.detail = 8;
/*  774 */           event.doit = true;
/*      */         }
/*      */       }
/*  777 */     };
/*  778 */     display.addFilter(31, this.filterListener);
/*      */     
/*  780 */     this.shell.pack();
/*  781 */     Point size = this.shell.getSize();
/*  782 */     if (size.x < this.min_size_x) {
/*  783 */       size.x = this.min_size_x;
/*  784 */       this.shell.setSize(size);
/*  785 */     } else if (size.x > this.max_size_x) {
/*  786 */       size = this.shell.computeSize(this.max_size_x, -1);
/*  787 */       this.shell.setSize(size);
/*      */     }
/*      */     
/*  790 */     if (size.y < this.min_size_y) {
/*  791 */       size.y = this.min_size_y;
/*  792 */       this.shell.setSize(size);
/*      */     }
/*      */     
/*  795 */     Shell centerRelativeToShell = this.parent;
/*  796 */     if (ourParent) {
/*  797 */       Control cursorControl = display.getCursorControl();
/*  798 */       if (cursorControl != null) {
/*  799 */         centerRelativeToShell = cursorControl.getShell();
/*      */       }
/*      */     }
/*  802 */     Utils.centerWindowRelativeTo(this.shell, centerRelativeToShell);
/*      */     
/*  804 */     for (int i = 0; i < listeners.length; i++) {
/*  805 */       listeners[i].skinAfterComponents(this.shell, this, this.relatedObjects);
/*      */     }
/*      */     
/*  808 */     this.shell.open();
/*  809 */     this.opened = true;
/*  810 */     numOpen += 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Control createTextBox(Composite textComposite, String text2)
/*      */   {
/*  822 */     Text tb = new Text(textComposite, 2826);
/*      */     
/*  824 */     tb.setText(text2);
/*      */     
/*  826 */     return tb;
/*      */   }
/*      */   
/*      */   private Canvas createLinkLabel(final Composite shell, final String text)
/*      */   {
/*  831 */     final Canvas canvas = new Canvas(shell, 0) {
/*      */       public Point computeSize(int wHint, int hHint, boolean changed) {
/*  833 */         Rectangle area = new Rectangle(0, 0, wHint < 0 ? MessageBoxShell.this.max_size_x : wHint, 5000);
/*      */         
/*  835 */         GC gc = new GC(this);
/*  836 */         GCStringPrinter sp = new GCStringPrinter(gc, text, area, true, false, 192);
/*      */         
/*  838 */         sp.calculateMetrics();
/*  839 */         gc.dispose();
/*  840 */         Point size = sp.getCalculatedSize();
/*  841 */         return size;
/*      */       }
/*      */       
/*  844 */     };
/*  845 */     Listener l = new Listener() {
/*      */       GCStringPrinter sp;
/*      */       
/*      */       public void handleEvent(Event e) {
/*  849 */         if (!MessageBoxShell.this.handleHTML) {
/*  850 */           if (e.type == 9) {
/*  851 */             Rectangle area = canvas.getClientArea();
/*  852 */             e.gc.setForeground(shell.getForeground());
/*  853 */             GCStringPrinter.printString(e.gc, text, area, true, false, 192);
/*      */           }
/*      */           
/*  856 */           return;
/*      */         }
/*      */         
/*  859 */         if (e.type == 9) {
/*  860 */           Rectangle area = canvas.getClientArea();
/*  861 */           this.sp = new GCStringPrinter(e.gc, text, area, true, false, 192);
/*      */           
/*  863 */           this.sp.setUrlColor(ColorCache.getColor(e.gc.getDevice(), "#0000ff"));
/*  864 */           if (MessageBoxShell.this.urlColor != null) {
/*  865 */             this.sp.setUrlColor(MessageBoxShell.this.urlColor);
/*      */           }
/*  867 */           e.gc.setForeground(shell.getForeground());
/*  868 */           this.sp.printString();
/*  869 */         } else if (e.type == 5) {
/*  870 */           if (this.sp != null) {
/*  871 */             GCStringPrinter.URLInfo hitUrl = this.sp.getHitUrl(e.x, e.y);
/*  872 */             if (hitUrl != null) {
/*  873 */               canvas.setCursor(canvas.getDisplay().getSystemCursor(21));
/*      */               
/*  875 */               canvas.setToolTipText(hitUrl.url);
/*      */             } else {
/*  877 */               canvas.setCursor(canvas.getDisplay().getSystemCursor(0));
/*      */               
/*  879 */               canvas.setToolTipText(null);
/*      */             }
/*      */           }
/*  882 */         } else if ((e.type == 4) && 
/*  883 */           (this.sp != null)) {
/*  884 */           GCStringPrinter.URLInfo hitUrl = this.sp.getHitUrl(e.x, e.y);
/*  885 */           if ((hitUrl != null) && (!hitUrl.url.startsWith(":"))) {
/*  886 */             Utils.launch(hitUrl.url);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  891 */     };
/*  892 */     canvas.addListener(9, l);
/*  893 */     if (this.handleHTML) {
/*  894 */       canvas.addListener(5, l);
/*  895 */       canvas.addListener(4, l);
/*      */     }
/*      */     
/*  898 */     org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.addCopyToClipMenu(canvas, new ClipboardCopy.copyToClipProvider()
/*      */     {
/*      */       public String getText() {
/*  901 */         return text;
/*      */       }
/*      */       
/*  904 */     });
/*  905 */     return canvas;
/*      */   }
/*      */   
/*      */   public String getHtml() {
/*  909 */     return this.html;
/*      */   }
/*      */   
/*      */   public void setHtml(String html) {
/*  913 */     this.html = html;
/*      */   }
/*      */   
/*      */   public void setUrl(String url) {
/*  917 */     this.url = url;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSize(int width, int height)
/*      */   {
/*  925 */     this.min_size_x = width;
/*  926 */     this.max_size_x = width;
/*  927 */     this.min_size_y = height;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRememberID()
/*      */   {
/*  933 */     return this.rememberID;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRemember(String rememberID, boolean rememberByDefault, String rememberText)
/*      */   {
/*  944 */     this.rememberID = rememberID;
/*  945 */     this.rememberByDefault = rememberByDefault;
/*  946 */     this.rememberText = rememberText;
/*  947 */     if (this.rememberText == null) {
/*  948 */       this.rememberText = MessageText.getString("MessageBoxWindow.rememberdecision");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getRememberText()
/*      */   {
/*  956 */     return this.rememberText;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRememberText(String rememberText)
/*      */   {
/*  963 */     this.rememberText = rememberText;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getAutoCloseInMS()
/*      */   {
/*  970 */     return this.autoCloseInMS;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoCloseInMS(int autoCloseInMS)
/*      */   {
/*  977 */     this.autoCloseInMS = autoCloseInMS;
/*      */   }
/*      */   
/*  980 */   public void setSquish(boolean b) { this.squish = b; }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isAutoClosed()
/*      */   {
/*  986 */     return this.autoClosed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setModal(boolean m)
/*      */   {
/*  999 */     this.modal = m;
/*      */   }
/*      */   
/*      */   public void setRelatedObject(Object relatedObject)
/*      */   {
/* 1004 */     this.relatedObjects = new Object[] { relatedObject };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRelatedObjects(Object[] relatedObjects)
/*      */   {
/* 1011 */     this.relatedObjects = relatedObjects;
/*      */   }
/*      */   
/*      */   public Object[] getRelatedObjects() {
/* 1015 */     return this.relatedObjects;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getLeftImage()
/*      */   {
/* 1024 */     return this.imgLeft == this.iconImage ? null : this.imgLeft;
/*      */   }
/*      */   
/*      */   public void setLeftImage(Image imgLeft) {
/* 1028 */     this.imgLeft = imgLeft;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLeftImage(final int icon)
/*      */   {
/* 1039 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1041 */         MessageBoxShell.this.setLeftImage(Display.getDefault().getSystemImage(icon));
/* 1042 */         MessageBoxShell.this.iconImage = Display.getDefault().getSystemImage(icon);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void setIconResource(String resource) {
/* 1048 */     this.iconImageID = null;
/* 1049 */     if (resource.equals("info")) {
/* 1050 */       this.iconImage = Display.getDefault().getSystemImage(2);
/*      */     }
/* 1052 */     else if (resource.equals("warning")) {
/* 1053 */       this.iconImage = Display.getDefault().getSystemImage(8);
/*      */     }
/* 1055 */     else if (resource.equals("error")) {
/* 1056 */       this.iconImage = Display.getDefault().getSystemImage(1);
/*      */     }
/*      */     else {
/* 1059 */       this.iconImage = ImageLoader.getInstance().getImage(resource);
/* 1060 */       this.iconImageID = resource;
/*      */     }
/* 1062 */     setLeftImage(this.iconImage);
/*      */   }
/*      */   
/*      */   public static void main(String[] args) {
/* 1066 */     Display display = Display.getDefault();
/* 1067 */     Shell shell = new Shell(display, 1264);
/* 1068 */     shell.open();
/*      */     
/* 1070 */     MessageBoxShell messageBoxShell = new MessageBoxShell("Title", "Test\nTHis is a very long line that tests whether the box gets really wide which is something we don't want.\nA <A HREF=\"Link\">link</A> for <A HREF=\"http://moo.com\">you</a>", new String[] { "Okay", "Cancyyyyyy", "Maybe" }, 1);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1080 */     messageBoxShell.setRemember("test2", false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*      */     
/* 1082 */     messageBoxShell.setAutoCloseInMS(15000);
/* 1083 */     messageBoxShell.setParent(shell);
/* 1084 */     messageBoxShell.setHtml("<b>Moo</b> goes the cow<p><hr>");
/* 1085 */     messageBoxShell.open(new UserPrompterResultListener()
/*      */     {
/*      */       public void prompterClosed(int returnVal) {
/* 1088 */         System.out.println(returnVal);
/*      */       }
/*      */     });
/* 1091 */     while (!shell.isDisposed()) {
/* 1092 */       if ((!display.isDisposed()) && (!display.readAndDispatch())) {
/* 1093 */         display.sleep();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int getRememberOnlyIfButton() {
/* 1099 */     return this.rememberOnlyIfButtonPos;
/*      */   }
/*      */   
/*      */   public void setRememberOnlyIfButton(int rememberOnlyIfButton) {
/* 1103 */     this.rememberOnlyIfButtonPos = rememberOnlyIfButton;
/*      */   }
/*      */   
/*      */   public Color getUrlColor() {
/* 1107 */     return this.urlColor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBrowserFollowLinks(boolean follow)
/*      */   {
/* 1114 */     this.browser_follow_links = follow;
/*      */   }
/*      */   
/*      */   public void setUrlColor(Color colorURL) {
/* 1118 */     this.urlColor = colorURL;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setHandleHTML(boolean handleHTML)
/*      */   {
/* 1127 */     this.handleHTML = handleHTML;
/*      */   }
/*      */   
/*      */   public boolean isRemembered() {
/* 1131 */     return this.isRemembered;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int waitUntilClosed()
/*      */   {
/* 1141 */     final AESemaphore sem = new AESemaphore("waitUntilClosed");
/*      */     
/* 1143 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*      */         try {
/* 1146 */           if (MessageBoxShell.this.shell == null) {
/*      */             return;
/*      */           }
/* 1149 */           if (!MessageBoxShell.this.opened) {
/* 1150 */             MessageBoxShell.this.shell.open();
/*      */           }
/* 1152 */           while ((MessageBoxShell.this.shell != null) && (!MessageBoxShell.this.shell.isDisposed())) {
/* 1153 */             if ((MessageBoxShell.this.shell.getDisplay() != null) && (!MessageBoxShell.this.shell.getDisplay().readAndDispatch()))
/*      */             {
/* 1155 */               MessageBoxShell.this.shell.getDisplay().sleep();
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 1160 */           sem.releaseForever();
/*      */         }
/*      */         
/*      */       }
/* 1164 */     });
/* 1165 */     sem.reserve();
/*      */     
/* 1167 */     int realResult = getButtonVal(this.result);
/*      */     
/* 1169 */     return realResult;
/*      */   }
/*      */   
/*      */   public int getResult() {
/* 1173 */     return this.result;
/*      */   }
/*      */   
/*      */   private static Object[] swtButtonStylesToText(int style) {
/* 1177 */     List<String> buttons = new ArrayList(2);
/* 1178 */     List<Integer> buttonVal = new ArrayList(2);
/* 1179 */     int buttonCount = 0;
/* 1180 */     if ((style & 0x20) > 0) {
/* 1181 */       buttons.add(MessageText.getString("Button.ok"));
/* 1182 */       buttonVal.add(Integer.valueOf(32));
/* 1183 */       buttonCount++;
/*      */     }
/* 1185 */     if ((style & 0x40) > 0) {
/* 1186 */       buttons.add(MessageText.getString("Button.yes"));
/* 1187 */       buttonVal.add(Integer.valueOf(64));
/* 1188 */       buttonCount++;
/*      */     }
/* 1190 */     if ((style & 0x80) > 0) {
/* 1191 */       buttons.add(MessageText.getString("Button.no"));
/* 1192 */       buttonVal.add(Integer.valueOf(128));
/* 1193 */       buttonCount++;
/*      */     }
/* 1195 */     if ((style & 0x100) > 0) {
/* 1196 */       buttons.add(MessageText.getString("Button.cancel"));
/* 1197 */       buttonVal.add(Integer.valueOf(256));
/* 1198 */       buttonCount++;
/*      */     }
/* 1200 */     if ((style & 0x200) > 0) {
/* 1201 */       buttons.add(MessageText.getString("Button.abort"));
/* 1202 */       buttonVal.add(Integer.valueOf(512));
/* 1203 */       buttonCount++;
/*      */     }
/* 1205 */     if ((style & 0x400) > 0) {
/* 1206 */       buttons.add(MessageText.getString("Button.retry"));
/* 1207 */       buttonVal.add(Integer.valueOf(1024));
/* 1208 */       buttonCount++;
/*      */     }
/* 1210 */     if ((style & 0x800) > 0) {
/* 1211 */       buttons.add(MessageText.getString("Button.ignore"));
/* 1212 */       buttonVal.add(Integer.valueOf(2048));
/* 1213 */       buttonCount++;
/*      */     }
/* 1215 */     return new Object[] { buttons.toArray(new String[buttonCount]), buttonVal.toArray(new Integer[buttonCount]) };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getButtons()
/*      */   {
/* 1222 */     return this.buttons;
/*      */   }
/*      */   
/*      */   public void setButtons(String[] buttons) {
/* 1226 */     this.buttons = buttons;
/*      */   }
/*      */   
/*      */   public void setButtons(int defaltButtonPos, String[] buttons, Integer[] buttonVals) {
/* 1230 */     this.defaultButtonPos = defaltButtonPos;
/* 1231 */     this.buttons = buttons;
/* 1232 */     this.buttonVals = buttonVals;
/*      */   }
/*      */   
/*      */   public void addCheckBox(String cbMessageID, int cbMinUserMode, boolean defaultOn) {
/* 1236 */     this.cbMessageID = cbMessageID;
/* 1237 */     this.cbMinUserMode = cbMinUserMode;
/* 1238 */     this.cbEnabled = defaultOn;
/*      */   }
/*      */   
/*      */   public boolean getCheckBoxEnabled() {
/* 1242 */     return this.cbEnabled;
/*      */   }
/*      */   
/*      */   public Shell getParent() {
/* 1246 */     return this.parent;
/*      */   }
/*      */   
/*      */   public void setParent(Shell parent) {
/* 1250 */     this.parent = parent;
/*      */   }
/*      */   
/*      */   public void close() {
/* 1254 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1256 */         if ((MessageBoxShell.this.shell != null) && (!MessageBoxShell.this.shell.isDisposed())) {
/* 1257 */           MessageBoxShell.this.shell.dispose();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setUseTextBox(boolean useTextBox)
/*      */   {
/* 1267 */     this.useTextBox = useTextBox;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean useTextBox()
/*      */   {
/* 1274 */     return this.useTextBox;
/*      */   }
/*      */   
/*      */   public void setLeftImage(final String id) {
/* 1278 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1280 */         MessageBoxShell.this.setLeftImage(ImageLoader.getInstance().getImage(id));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void setOneInstanceOf(String instanceID) {
/* 1286 */     this.instanceID = instanceID;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/MessageBoxShell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */