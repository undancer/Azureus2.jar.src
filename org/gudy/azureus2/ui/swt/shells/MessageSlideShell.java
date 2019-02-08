/*      */ package org.gudy.azureus2.ui.swt.shells;
/*      */ 
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.UISkinnableManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UISkinnableSWTListener;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import org.eclipse.swt.SWT;
/*      */ import org.eclipse.swt.custom.StyledText;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.MouseTrackAdapter;
/*      */ import org.eclipse.swt.events.MouseTrackListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FillLayout;
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
/*      */ import org.eclipse.swt.widgets.Monitor;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
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
/*      */ 
/*      */ 
/*      */ public class MessageSlideShell
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   private static final int EDGE_GAP = 0;
/*      */   private static final int SHELL_DEF_WIDTH = 280;
/*      */   private static final int SHELL_MIN_HEIGHT = 150;
/*      */   private static final int SHELL_MAX_HEIGHT = 330;
/*      */   private static final int DETAILS_WIDTH = 550;
/*      */   private static final int DETAILS_HEIGHT = 180;
/*   85 */   private static final AEMonitor monitor = new AEMonitor("slidey_mon");
/*      */   
/*      */ 
/*   88 */   private static ArrayList<PopupParams> historyList = new ArrayList();
/*      */   
/*      */ 
/*   91 */   private static int currentPopupIndex = -1;
/*      */   
/*      */ 
/*   94 */   private static int firstUnreadMessage = -1;
/*      */   
/*      */ 
/*      */ 
/*      */   private Shell shell;
/*      */   
/*      */ 
/*      */ 
/*      */   private Composite cShell;
/*      */   
/*      */ 
/*      */ 
/*      */   private Label lblCloseIn;
/*      */   
/*      */ 
/*      */ 
/*      */   private Button btnHideAll;
/*      */   
/*      */ 
/*      */   private Button btnNext;
/*      */   
/*      */ 
/*  116 */   private boolean bDelayPaused = false;
/*      */   
/*      */ 
/*  119 */   private ArrayList<Object> disposeList = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String sDetails;
/*      */   
/*      */ 
/*      */ 
/*      */   private int idxHistory;
/*      */   
/*      */ 
/*      */ 
/*      */   protected Color colorURL;
/*      */   
/*      */ 
/*      */ 
/*      */   private Color colorFG;
/*      */   
/*      */ 
/*      */ 
/*      */   private int shellWidth;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public MessageSlideShell(Display display, int iconID, String keyPrefix, String details, String[] textParams, int timeoutSecs)
/*      */   {
/*  147 */     this(display, iconID, MessageText.getString(keyPrefix + ".title"), MessageText.getString(keyPrefix + ".text", textParams), details, timeoutSecs);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public MessageSlideShell(Display display, int iconID, String keyPrefix, String details, String[] textParams, Object[] relatedObjects, int timeoutSecs)
/*      */   {
/*  164 */     this(display, iconID, MessageText.getString(keyPrefix + ".title"), MessageText.getString(keyPrefix + ".text", textParams), details, relatedObjects, timeoutSecs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public MessageSlideShell(Display display, int iconID, String title, String text, String details, int timeoutSecs)
/*      */   {
/*  171 */     this(display, iconID, title, text, details, null, timeoutSecs);
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
/*      */ 
/*      */   public MessageSlideShell(Display display, int iconID, String title, String text, String details, Object[] relatedObjects, int timeoutSecs)
/*      */   {
/*      */     try
/*      */     {
/*  188 */       monitor.enter();
/*      */       
/*  190 */       PopupParams popupParams = new PopupParams(iconID, title, text, details, relatedObjects, timeoutSecs);
/*      */       
/*  192 */       addToHistory(popupParams);
/*  193 */       if (currentPopupIndex < 0) {
/*  194 */         create(display, popupParams, true);
/*      */       }
/*      */     } catch (Exception e) {
/*  197 */       Logger.log(new LogEvent(LogIDs.GUI, "Mr. Slidey Init", e));
/*  198 */       disposeShell(this.shell);
/*  199 */       Utils.disposeSWTObjects(this.disposeList);
/*      */     } finally {
/*  201 */       monitor.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void addToHistory(PopupParams popupParams)
/*      */   {
/*  211 */     monitor.enter();
/*      */     try {
/*  213 */       historyList.add(popupParams);
/*      */     } finally {
/*  215 */       monitor.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private MessageSlideShell(Display display, PopupParams popupParams, boolean bSlide)
/*      */   {
/*  221 */     create(display, popupParams, bSlide);
/*      */   }
/*      */   
/*      */   public static void displayLastMessage(final Display display, boolean last_unread)
/*      */   {
/*  226 */     display.asyncExec(new AERunnable() {
/*      */       public void runSupport() {
/*  228 */         if (MessageSlideShell.historyList.isEmpty()) {
/*  229 */           return;
/*      */         }
/*  231 */         if (MessageSlideShell.currentPopupIndex >= 0) {
/*  232 */           return;
/*      */         }
/*  234 */         int msg_index = MessageSlideShell.firstUnreadMessage;
/*  235 */         if ((!this.val$last_unread) || (msg_index == -1)) {
/*  236 */           msg_index = MessageSlideShell.historyList.size() - 1;
/*      */         }
/*  238 */         new MessageSlideShell(display, (MessageSlideShell.PopupParams)MessageSlideShell.historyList.get(msg_index), true, null);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void recordMessage(int iconID, String title, String text, String details, Object[] relatedTo, int timeoutSecs)
/*      */   {
/*      */     try
/*      */     {
/*  250 */       monitor.enter();
/*  251 */       addToHistory(new PopupParams(iconID, title, text, details, relatedTo, timeoutSecs));
/*  252 */       if (firstUnreadMessage == -1) {
/*  253 */         firstUnreadMessage = historyList.size() - 1;
/*      */       }
/*      */     } finally {
/*  256 */       monitor.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void create(final Display display, final PopupParams popupParams, boolean bSlide)
/*      */   {
/*  263 */     firstUnreadMessage = -1;
/*      */     
/*      */ 
/*  266 */     int style = 16384;
/*      */     
/*  268 */     boolean bDisableSliding = COConfigurationManager.getBooleanParameter("GUI_SWT_DisableAlertSliding");
/*  269 */     if (bDisableSliding) {
/*  270 */       bSlide = false;
/*  271 */       style = 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  278 */     this.idxHistory = historyList.indexOf(popupParams);
/*      */     
/*      */ 
/*  281 */     if (this.idxHistory < 0) {
/*  282 */       System.err.println("Not in popup history list");
/*  283 */       return;
/*      */     }
/*      */     
/*  286 */     if (currentPopupIndex == this.idxHistory) {
/*  287 */       System.err.println("Trying to open already opened!! " + this.idxHistory);
/*  288 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  292 */       monitor.enter();
/*  293 */       currentPopupIndex = this.idxHistory;
/*      */     } finally {
/*  295 */       monitor.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  301 */     this.sDetails = popupParams.details;
/*      */     
/*      */ 
/*  304 */     Image imgIcon = popupParams.iconID <= 0 ? null : display.getSystemImage(popupParams.iconID);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  314 */     this.bDelayPaused = ((popupParams.iconID != 2) || (!bSlide));
/*      */     
/*      */ 
/*      */ 
/*  318 */     final MouseTrackAdapter mouseAdapter = this.bDelayPaused ? null : new MouseTrackAdapter()
/*      */     {
/*      */       public void mouseEnter(MouseEvent e) {
/*  321 */         MessageSlideShell.this.bDelayPaused = true;
/*      */       }
/*      */       
/*      */       public void mouseExit(MouseEvent e) {
/*  325 */         MessageSlideShell.this.bDelayPaused = false;
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*  330 */     if (bDisableSliding) {
/*  331 */       UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  332 */       if (uiFunctions != null) {
/*  333 */         Shell mainShell = uiFunctions.getMainShell();
/*  334 */         if (mainShell != null) {
/*  335 */           this.shell = new Shell(mainShell, style);
/*      */         }
/*      */       }
/*      */     }
/*  339 */     if (this.shell == null) {
/*  340 */       this.shell = new Shell(display, style);
/*      */     }
/*      */     try {
/*  343 */       this.shell.setBackgroundMode(1);
/*      */     }
/*      */     catch (NoSuchMethodError e) {}catch (NoSuchFieldError e2) {}
/*      */     
/*      */ 
/*      */ 
/*  349 */     Utils.setShellIcon(this.shell);
/*  350 */     if (popupParams.title != null) {
/*  351 */       this.shell.setText(popupParams.title);
/*      */     }
/*      */     
/*  354 */     this.shellWidth = Utils.adjustPXForDPI(280);
/*      */     
/*  356 */     UISkinnableSWTListener[] listeners = UISkinnableManagerSWT.getInstance().getSkinnableListeners(MessageSlideShell.class.toString());
/*      */     
/*  358 */     for (int i = 0; i < listeners.length; i++) {
/*      */       try {
/*  360 */         listeners[i].skinBeforeComponents(this.shell, this, popupParams.relatedTo);
/*      */       } catch (Exception e) {
/*  362 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  366 */     if (this.colorFG == null) {
/*  367 */       this.colorFG = display.getSystemColor(2);
/*      */     }
/*      */     
/*  370 */     FormLayout shellLayout = new FormLayout();
/*  371 */     this.shell.setLayout(shellLayout);
/*      */     
/*  373 */     this.cShell = new Composite(this.shell, 0);
/*  374 */     GridLayout layout = new GridLayout(3, false);
/*  375 */     this.cShell.setLayout(layout);
/*      */     
/*  377 */     FormData formData = new FormData();
/*  378 */     formData.left = new FormAttachment(0, 0);
/*  379 */     formData.right = new FormAttachment(100, 0);
/*  380 */     this.cShell.setLayoutData(formData);
/*      */     
/*  382 */     Label lblIcon = new Label(this.cShell, 0);
/*  383 */     lblIcon.setImage(imgIcon);
/*  384 */     lblIcon.setLayoutData(new GridData());
/*      */     
/*  386 */     if (popupParams.title != null) {
/*  387 */       Label lblTitle = new Label(this.cShell, SWT.getVersion() < 3100 ? 0 : 64);
/*      */       
/*  389 */       GridData gridData = new GridData(768);
/*  390 */       if (SWT.getVersion() < 3100)
/*  391 */         gridData.widthHint = 140;
/*  392 */       lblTitle.setLayoutData(gridData);
/*  393 */       lblTitle.setForeground(this.colorFG);
/*  394 */       lblTitle.setText(popupParams.title);
/*  395 */       FontData[] fontData = lblTitle.getFont().getFontData();
/*  396 */       fontData[0].setStyle(1);
/*  397 */       fontData[0].setHeight((int)(fontData[0].getHeight() * 1.5D));
/*  398 */       Font boldFont = new Font(display, fontData);
/*  399 */       this.disposeList.add(boldFont);
/*  400 */       lblTitle.setFont(boldFont);
/*      */     }
/*      */     
/*  403 */     final Button btnDetails = new Button(this.cShell, 2);
/*  404 */     btnDetails.setForeground(this.colorFG);
/*  405 */     Messages.setLanguageText(btnDetails, "popup.error.details");
/*  406 */     GridData gridData = new GridData();
/*  407 */     btnDetails.setLayoutData(gridData);
/*  408 */     btnDetails.addListener(4, new Listener() {
/*      */       public void handleEvent(Event arg0) {
/*      */         try {
/*  411 */           boolean bShow = btnDetails.getSelection();
/*  412 */           if (bShow) {
/*  413 */             Shell detailsShell = new Shell(display, 18432);
/*  414 */             Utils.setShellIcon(detailsShell);
/*  415 */             detailsShell.setLayout(new FillLayout());
/*  416 */             StyledText textDetails = new StyledText(detailsShell, 2824);
/*      */             
/*  418 */             textDetails.setBackground(display.getSystemColor(25));
/*  419 */             textDetails.setForeground(display.getSystemColor(24));
/*  420 */             textDetails.setWordWrap(true);
/*  421 */             textDetails.setText(MessageSlideShell.this.sDetails);
/*  422 */             detailsShell.layout();
/*  423 */             Rectangle shellBounds = MessageSlideShell.this.shell.getBounds();
/*  424 */             int detailsWidth = Utils.adjustPXForDPI(550);
/*  425 */             int detailsHeight = Utils.adjustPXForDPI(180);
/*  426 */             detailsShell.setBounds(shellBounds.x + shellBounds.width - detailsWidth, shellBounds.y - detailsHeight, detailsWidth, detailsHeight);
/*      */             
/*      */ 
/*  429 */             detailsShell.open();
/*  430 */             MessageSlideShell.this.shell.setData("detailsShell", detailsShell);
/*  431 */             MessageSlideShell.this.shell.addDisposeListener(new DisposeListener() {
/*      */               public void widgetDisposed(DisposeEvent e) {
/*  433 */                 Shell detailsShell = (Shell)MessageSlideShell.this.shell.getData("detailsShell");
/*  434 */                 if ((detailsShell != null) && (!detailsShell.isDisposed())) {
/*  435 */                   detailsShell.dispose();
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*  440 */             });
/*  441 */             MessageSlideShell.this.bDelayPaused = true;
/*  442 */             MessageSlideShell.this.removeMouseTrackListener(MessageSlideShell.this.shell, mouseAdapter);
/*      */           } else {
/*  444 */             Shell detailsShell = (Shell)MessageSlideShell.this.shell.getData("detailsShell");
/*  445 */             if ((detailsShell != null) && (!detailsShell.isDisposed())) {
/*  446 */               detailsShell.dispose();
/*      */             }
/*      */           }
/*      */         } catch (Exception e) {
/*  450 */           Logger.log(new LogEvent(LogIDs.GUI, "Mr. Slidey DetailsButton", e));
/*      */         }
/*      */         
/*      */       }
/*  454 */     });
/*  455 */     createLinkLabel(this.cShell, popupParams);
/*      */     
/*  457 */     this.lblCloseIn = new Label(this.cShell, 131072);
/*  458 */     this.lblCloseIn.setForeground(this.colorFG);
/*      */     
/*  460 */     this.lblCloseIn.setText(" \n ");
/*  461 */     gridData = new GridData(4, 128, true, false);
/*  462 */     gridData.horizontalSpan = 3;
/*  463 */     this.lblCloseIn.setLayoutData(gridData);
/*      */     
/*  465 */     final Composite cButtons = new Composite(this.cShell, 0);
/*  466 */     GridLayout gridLayout = new GridLayout();
/*  467 */     gridLayout.marginHeight = 0;
/*  468 */     gridLayout.marginWidth = 0;
/*  469 */     gridLayout.verticalSpacing = 0;
/*  470 */     if (Constants.isOSX)
/*  471 */       gridLayout.horizontalSpacing = 0;
/*  472 */     gridLayout.numColumns = (this.idxHistory > 0 ? 3 : 2);
/*  473 */     cButtons.setLayout(gridLayout);
/*  474 */     gridData = new GridData(132);
/*      */     
/*  476 */     gridData.horizontalSpan = 3;
/*  477 */     cButtons.setLayoutData(gridData);
/*      */     
/*  479 */     this.btnHideAll = new Button(cButtons, 8);
/*  480 */     Messages.setLanguageText(this.btnHideAll, "popup.error.hideall");
/*  481 */     this.btnHideAll.setVisible(false);
/*  482 */     this.btnHideAll.setForeground(display.getSystemColor(2));
/*      */     
/*  484 */     this.btnHideAll.addListener(4, new Listener() {
/*      */       public void handleEvent(Event arg0) {
/*  486 */         cButtons.setEnabled(false);
/*      */         
/*  488 */         MessageSlideShell.this.shell.dispose();
/*      */       }
/*      */     });
/*      */     
/*  492 */     if (this.idxHistory > 0) {
/*  493 */       Button btnPrev = new Button(cButtons, 8);
/*  494 */       btnPrev.setForeground(display.getSystemColor(2));
/*  495 */       btnPrev.setText(MessageText.getString("popup.previous", new String[] { "" + this.idxHistory }));
/*      */       
/*      */ 
/*  498 */       btnPrev.addListener(4, new Listener() {
/*      */         public void handleEvent(Event arg0) {
/*  500 */           MessageSlideShell.this.disposeShell(MessageSlideShell.this.shell);
/*  501 */           int idx = MessageSlideShell.historyList.indexOf(popupParams) - 1;
/*  502 */           if (idx >= 0) {
/*  503 */             MessageSlideShell.PopupParams item = (MessageSlideShell.PopupParams)MessageSlideShell.historyList.get(idx);
/*  504 */             MessageSlideShell.this.showPopup(display, item, false);
/*  505 */             MessageSlideShell.this.disposeShell(MessageSlideShell.this.shell);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  511 */     this.btnNext = new Button(cButtons, 8);
/*  512 */     this.btnNext.setForeground(display.getSystemColor(2));
/*  513 */     int numAfter = historyList.size() - this.idxHistory - 1;
/*  514 */     setButtonNextText(numAfter);
/*      */     
/*  516 */     this.btnNext.addListener(4, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event arg0)
/*      */       {
/*  521 */         if (MessageSlideShell.this.idxHistory + 1 < MessageSlideShell.historyList.size()) {
/*  522 */           MessageSlideShell.this.showPopup(display, (MessageSlideShell.PopupParams)MessageSlideShell.historyList.get(MessageSlideShell.this.idxHistory + 1), false);
/*      */         }
/*      */         
/*  525 */         MessageSlideShell.this.disposeShell(MessageSlideShell.this.shell);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  530 */     });
/*  531 */     Point bestSize = this.cShell.computeSize(this.shellWidth, -1);
/*  532 */     int minHeight = Utils.adjustPXForDPI(150);
/*  533 */     int maxHeight = Utils.adjustPXForDPI(330);
/*  534 */     if (bestSize.y < minHeight) {
/*  535 */       bestSize.y = minHeight;
/*  536 */     } else if (bestSize.y > maxHeight) {
/*  537 */       bestSize.y = maxHeight;
/*  538 */       if (this.sDetails == null) {
/*  539 */         this.sDetails = popupParams.text;
/*      */       } else {
/*  541 */         this.sDetails = (popupParams.text + "\n===============\n" + this.sDetails);
/*      */       }
/*      */     }
/*      */     
/*  545 */     Rectangle bounds = null;
/*      */     try {
/*  547 */       UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  548 */       if (uiFunctions != null) {
/*  549 */         Shell mainShell = uiFunctions.getMainShell();
/*  550 */         if (mainShell != null) {
/*  551 */           bounds = mainShell.getMonitor().getClientArea();
/*      */         }
/*      */       } else {
/*  554 */         Shell shell = display.getActiveShell();
/*  555 */         if (shell != null) {
/*  556 */           bounds = shell.getMonitor().getClientArea();
/*      */         }
/*      */       }
/*  559 */       if (bounds == null) {
/*  560 */         bounds = this.shell.getMonitor().getClientArea();
/*      */       }
/*      */     }
/*      */     catch (Exception e) {}
/*  564 */     if (bounds == null) {
/*  565 */       bounds = display.getClientArea();
/*      */     }
/*      */     Rectangle endBounds;
/*      */     Rectangle endBounds;
/*  569 */     if (bDisableSliding) {
/*  570 */       endBounds = new Rectangle((bounds.x + bounds.width) / 2 - bestSize.x / 2, (bounds.y + bounds.height) / 2 - bestSize.y / 2, bestSize.x, bestSize.y);
/*      */     }
/*      */     else
/*      */     {
/*  574 */       int boundsX2 = bounds.x + bounds.width;
/*  575 */       int boundsY2 = bounds.y + bounds.height;
/*  576 */       endBounds = this.shell.computeTrim(boundsX2 - bestSize.x, boundsY2 - bestSize.y, bestSize.x, bestSize.y);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  581 */       int diff = endBounds.x + endBounds.width - boundsX2;
/*  582 */       if (diff >= 0)
/*  583 */         endBounds.x -= diff + 0;
/*  584 */       diff = endBounds.y + endBounds.height - boundsY2;
/*  585 */       if (diff >= 0) {
/*  586 */         endBounds.y -= diff + 0;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  591 */     FormData data = new FormData(bestSize.x, bestSize.y);
/*  592 */     this.cShell.setLayoutData(data);
/*      */     
/*  594 */     btnDetails.setVisible(this.sDetails != null);
/*  595 */     if (this.sDetails == null) {
/*  596 */       gridData = new GridData();
/*  597 */       gridData.widthHint = 0;
/*  598 */       btnDetails.setLayoutData(gridData);
/*      */     }
/*  600 */     this.shell.layout();
/*      */     
/*  602 */     this.btnNext.setFocus();
/*  603 */     this.shell.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  605 */         Utils.disposeSWTObjects(MessageSlideShell.this.disposeList);
/*      */         
/*  607 */         if (MessageSlideShell.currentPopupIndex == MessageSlideShell.this.idxHistory)
/*      */         {
/*      */           try
/*      */           {
/*  611 */             MessageSlideShell.monitor.enter();
/*  612 */             MessageSlideShell.access$102(-1);
/*      */           } finally {
/*  614 */             MessageSlideShell.monitor.exit();
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  619 */     });
/*  620 */     this.shell.addListener(31, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  622 */         if (event.detail == 2) {
/*  623 */           MessageSlideShell.this.disposeShell(MessageSlideShell.this.shell);
/*  624 */           event.doit = false;
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  629 */     if (mouseAdapter != null) {
/*  630 */       addMouseTrackListener(this.shell, mouseAdapter);
/*      */     }
/*  632 */     for (int i = 0; i < listeners.length; i++) {
/*      */       try {
/*  634 */         listeners[i].skinAfterComponents(this.shell, this, popupParams.relatedTo);
/*      */       } catch (Exception e) {
/*  636 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*      */     int timeoutSecs;
/*      */     int timeoutSecs;
/*  642 */     if (popupParams.timeoutSecs < 0)
/*      */     {
/*  644 */       timeoutSecs = COConfigurationManager.getIntParameter("Message Popup Autoclose in Seconds");
/*      */     }
/*      */     else
/*      */     {
/*  648 */       timeoutSecs = popupParams.timeoutSecs;
/*      */     }
/*      */     
/*  651 */     runPopup(endBounds, this.idxHistory, bSlide, timeoutSecs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void createLinkLabel(Composite shell, final PopupParams popupParams)
/*      */   {
/*  662 */     final Canvas canvas = new Canvas(shell, 0) {
/*      */       public Point computeSize(int wHint, int hHint, boolean changed) {
/*  664 */         Rectangle area = new Rectangle(0, 0, MessageSlideShell.this.shellWidth, 5000);
/*  665 */         GC gc = new GC(this);
/*  666 */         GCStringPrinter sp = new GCStringPrinter(gc, popupParams.text, area, true, false, 192);
/*      */         
/*  668 */         sp.calculateMetrics();
/*  669 */         gc.dispose();
/*  670 */         Point size = sp.getCalculatedSize();
/*  671 */         return size;
/*      */       }
/*      */       
/*  674 */     };
/*  675 */     Listener l = new Listener() {
/*      */       GCStringPrinter sp;
/*      */       
/*      */       public void handleEvent(Event e) {
/*  679 */         if (e.type == 9) {
/*  680 */           Rectangle area = canvas.getClientArea();
/*  681 */           this.sp = new GCStringPrinter(e.gc, popupParams.text, area, true, false, 192);
/*      */           
/*  683 */           this.sp.setUrlColor(ColorCache.getColor(e.gc.getDevice(), "#0000ff"));
/*  684 */           if (MessageSlideShell.this.colorURL != null) {
/*  685 */             this.sp.setUrlColor(MessageSlideShell.this.colorURL);
/*      */           }
/*  687 */           if (MessageSlideShell.this.colorFG != null) {
/*  688 */             e.gc.setForeground(MessageSlideShell.this.colorFG);
/*      */           }
/*  690 */           this.sp.printString();
/*  691 */         } else if (e.type == 5) {
/*  692 */           if (this.sp != null) {
/*  693 */             GCStringPrinter.URLInfo hitUrl = this.sp.getHitUrl(e.x, e.y);
/*  694 */             if (hitUrl != null) {
/*  695 */               canvas.setCursor(canvas.getDisplay().getSystemCursor(21));
/*      */               
/*  697 */               canvas.setToolTipText(hitUrl.url);
/*      */             } else {
/*  699 */               canvas.setCursor(canvas.getDisplay().getSystemCursor(0));
/*      */               
/*  701 */               canvas.setToolTipText(null);
/*      */             }
/*      */           }
/*  704 */         } else if ((e.type == 4) && 
/*  705 */           (this.sp != null)) {
/*  706 */           GCStringPrinter.URLInfo hitUrl = this.sp.getHitUrl(e.x, e.y);
/*  707 */           if ((hitUrl != null) && (!hitUrl.url.startsWith(":"))) {
/*  708 */             Utils.launch(hitUrl.url);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  713 */     };
/*  714 */     canvas.addListener(9, l);
/*  715 */     canvas.addListener(5, l);
/*  716 */     canvas.addListener(4, l);
/*      */     
/*  718 */     ClipboardCopy.addCopyToClipMenu(canvas, new ClipboardCopy.copyToClipProvider()
/*      */     {
/*      */       public String getText() {
/*  721 */         return popupParams.title + "\n\n" + popupParams.text;
/*      */       }
/*      */       
/*  724 */     });
/*  725 */     GridData gridData = new GridData(1808);
/*  726 */     gridData.horizontalSpan = 3;
/*  727 */     canvas.setLayoutData(gridData);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setButtonNextText(int numAfter)
/*      */   {
/*  734 */     if (numAfter <= 0) {
/*  735 */       Messages.setLanguageText(this.btnNext, "popup.error.hide");
/*      */     } else {
/*  737 */       Messages.setLanguageText(this.btnNext, "popup.next", new String[] { "" + numAfter });
/*      */     }
/*      */     
/*  740 */     this.cShell.layout(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void showPopup(final Display display, final PopupParams item, final boolean bSlide)
/*      */   {
/*  752 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  754 */         new MessageSlideShell(display, item, bSlide, null);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addMouseTrackListener(Composite parent, MouseTrackListener listener)
/*      */   {
/*  767 */     if ((parent == null) || (listener == null) || (parent.isDisposed())) {
/*  768 */       return;
/*      */     }
/*  770 */     parent.addMouseTrackListener(listener);
/*  771 */     Control[] children = parent.getChildren();
/*  772 */     for (int i = 0; i < children.length; i++) {
/*  773 */       Control control = children[i];
/*  774 */       if ((control instanceof Composite)) {
/*  775 */         addMouseTrackListener((Composite)control, listener);
/*      */       } else {
/*  777 */         control.addMouseTrackListener(listener);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void removeMouseTrackListener(Composite parent, MouseTrackListener listener)
/*      */   {
/*  789 */     if ((parent == null) || (listener == null) || (parent.isDisposed())) {
/*  790 */       return;
/*      */     }
/*  792 */     Control[] children = parent.getChildren();
/*  793 */     for (int i = 0; i < children.length; i++) {
/*  794 */       Control control = children[i];
/*  795 */       control.removeMouseTrackListener(listener);
/*  796 */       if ((control instanceof Composite)) {
/*  797 */         removeMouseTrackListener((Composite)control, listener);
/*      */       }
/*      */     }
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
/*      */   private void runPopup(final Rectangle endBounds, final int idx, final boolean bSlide, final int timeoutSecs)
/*      */   {
/*  812 */     if ((this.shell == null) || (this.shell.isDisposed())) {
/*  813 */       return;
/*      */     }
/*  815 */     final Display display = this.shell.getDisplay();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  820 */     AEThread thread = new AEThread("Slidey", true) {
/*      */       private static final int PAUSE = 500;
/*      */       
/*      */       public void runSupport() {
/*  824 */         if ((MessageSlideShell.this.shell == null) || (MessageSlideShell.this.shell.isDisposed())) {
/*  825 */           return;
/*      */         }
/*  827 */         if (bSlide) {
/*  828 */           new ShellSlider(MessageSlideShell.this.shell, 128, endBounds).run();
/*      */         } else {
/*  830 */           Utils.execSWTThread(new AERunnable()
/*      */           {
/*      */             public void runSupport() {
/*  833 */               MessageSlideShell.this.shell.setBounds(MessageSlideShell.13.this.val$endBounds);
/*  834 */               MessageSlideShell.this.shell.open();
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*  839 */         int delayLeft = timeoutSecs * 1000;
/*  840 */         final boolean autohide = delayLeft != 0;
/*      */         
/*  842 */         long lastDelaySecs = 0L;
/*  843 */         int lastNumPopups = -1;
/*      */         
/*  845 */         while (((!autohide) || (MessageSlideShell.this.bDelayPaused) || (delayLeft > 0)) && (!MessageSlideShell.this.shell.isDisposed())) {
/*  846 */           int delayPausedOfs = MessageSlideShell.this.bDelayPaused ? 1 : 0;
/*  847 */           final long delaySecs = Math.round(delayLeft / 1000.0D) + delayPausedOfs;
/*      */           
/*  849 */           int numPopups = MessageSlideShell.historyList.size();
/*  850 */           if ((lastDelaySecs != delaySecs) || (lastNumPopups != numPopups)) {
/*  851 */             lastDelaySecs = delaySecs;
/*  852 */             lastNumPopups = numPopups;
/*  853 */             MessageSlideShell.this.shell.getDisplay().asyncExec(new AERunnable() {
/*      */               public void runSupport() {
/*  855 */                 String sText = "";
/*      */                 
/*  857 */                 if ((MessageSlideShell.this.lblCloseIn == null) || (MessageSlideShell.this.lblCloseIn.isDisposed())) {
/*  858 */                   return;
/*      */                 }
/*  860 */                 MessageSlideShell.this.lblCloseIn.setRedraw(false);
/*  861 */                 if ((!MessageSlideShell.this.bDelayPaused) && (autohide)) {
/*  862 */                   sText = sText + MessageText.getString("popup.closing.in", new String[] { String.valueOf(delaySecs) });
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*  867 */                 int numPopupsAfterUs = this.val$numPopups - MessageSlideShell.13.this.val$idx - 1;
/*  868 */                 boolean bHasMany = numPopupsAfterUs > 0;
/*  869 */                 if (bHasMany) {
/*  870 */                   sText = sText + "\n";
/*  871 */                   sText = sText + MessageText.getString("popup.more.waiting", new String[] { String.valueOf(numPopupsAfterUs) });
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  877 */                 MessageSlideShell.this.lblCloseIn.setText(sText);
/*      */                 
/*  879 */                 if (MessageSlideShell.this.btnHideAll.getVisible() != bHasMany) {
/*  880 */                   MessageSlideShell.this.cShell.setRedraw(false);
/*  881 */                   MessageSlideShell.this.btnHideAll.setVisible(bHasMany);
/*  882 */                   MessageSlideShell.this.lblCloseIn.getParent().layout(true);
/*  883 */                   MessageSlideShell.this.cShell.setRedraw(true);
/*      */                 }
/*      */                 
/*  886 */                 MessageSlideShell.this.setButtonNextText(numPopupsAfterUs);
/*      */                 
/*      */ 
/*  889 */                 MessageSlideShell.this.lblCloseIn.setRedraw(true);
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*  894 */           if (!MessageSlideShell.this.bDelayPaused)
/*  895 */             delayLeft -= 500;
/*      */           try {
/*  897 */             Thread.sleep(500L);
/*      */           } catch (InterruptedException e) {
/*  899 */             delayLeft = 0;
/*      */           }
/*      */         }
/*      */         
/*  903 */         if (isInterrupted())
/*      */         {
/*  905 */           MessageSlideShell.this.disposeShell(MessageSlideShell.this.shell);
/*  906 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  911 */         if ((MessageSlideShell.this.shell != null) && (!MessageSlideShell.this.shell.isDisposed())) {
/*  912 */           if (idx + 1 < MessageSlideShell.historyList.size()) {
/*  913 */             MessageSlideShell.this.showPopup(display, (MessageSlideShell.PopupParams)MessageSlideShell.historyList.get(idx + 1), true);
/*      */           }
/*      */           
/*      */ 
/*  917 */           if (bSlide) {
/*  918 */             new ShellSlider(MessageSlideShell.this.shell, 131072).run();
/*      */           }
/*  920 */           MessageSlideShell.this.disposeShell(MessageSlideShell.this.shell);
/*      */         }
/*      */       }
/*  923 */     };
/*  924 */     thread.start();
/*      */   }
/*      */   
/*      */   private void disposeShell(final Shell shell) {
/*  928 */     if ((shell == null) || (shell.isDisposed())) {
/*  929 */       return;
/*      */     }
/*  931 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  933 */         shell.dispose();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void waitUntilClosed()
/*      */   {
/*  942 */     if (currentPopupIndex < 0) {
/*  943 */       return;
/*      */     }
/*  945 */     Display display = Display.getCurrent();
/*  946 */     while (currentPopupIndex >= 0) {
/*  947 */       if (!display.readAndDispatch()) {
/*  948 */         display.sleep();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class PopupParams
/*      */   {
/*      */     public int iconID;
/*      */     
/*      */ 
/*      */     public String title;
/*      */     
/*      */     public String text;
/*      */     
/*      */     public String details;
/*      */     
/*      */     public long addedOn;
/*      */     
/*      */     public Object[] relatedTo;
/*      */     
/*      */     public int timeoutSecs;
/*      */     
/*      */ 
/*      */     public PopupParams(int iconID, String title, String text, String details, int timeoutSecs)
/*      */     {
/*  975 */       this.iconID = iconID;
/*  976 */       this.title = title;
/*  977 */       this.text = text;
/*  978 */       this.details = details;
/*  979 */       this.timeoutSecs = timeoutSecs;
/*  980 */       this.addedOn = System.currentTimeMillis();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public PopupParams(int iconID, String title, String text, String details, Object[] relatedTo, int timeoutSecs)
/*      */     {
/*  992 */       this(iconID, title, text, details, timeoutSecs);
/*  993 */       this.relatedTo = relatedTo;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1003 */     Display display = Display.getDefault();
/*      */     
/* 1005 */     Shell shell = new Shell(display, 2144);
/* 1006 */     shell.setLayout(new FillLayout());
/* 1007 */     Button btn = new Button(shell, 8);
/* 1008 */     btn.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1010 */         MessageSlideShell.test(this.val$display);
/*      */       }
/* 1012 */     });
/* 1013 */     shell.open();
/*      */     
/* 1015 */     while (!shell.isDisposed()) {
/* 1016 */       if (!display.readAndDispatch()) {
/* 1017 */         display.sleep();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void test(Display display)
/*      */   {
/* 1024 */     String title = "This is the title that never ends, never ends!";
/* 1025 */     String text = "This is a very long message with lots of information and stuff you really should read.  Are you still reading? Good, because reading <a href=\"http://moo.com\">stimulates</a> the mind and grows hair on your chest.\n\n  Unless you are a girl, then it makes you want to read more.  It's an endless cycle of reading that will never end.  Cursed is the long text that is in this test and may it fillevery last line of the shell until there is no more.";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1036 */       Thread.sleep(2000L);
/*      */     }
/*      */     catch (InterruptedException e) {
/* 1039 */       e.printStackTrace();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1045 */     new MessageSlideShell(display, 2, "Simple. . . . . . . . . . . . . . . . . . .", "Simple", (String)null, -1);
/*      */     
/*      */ 
/* 1048 */     new MessageSlideShell(display, 2, title + "1", text, "Details: " + text, -1);
/*      */     
/*      */ 
/* 1051 */     new MessageSlideShell(display, 2, "ShortTitle2", "ShortText", "Details", -1);
/*      */     
/* 1053 */     waitUntilClosed();
/*      */     
/* 1055 */     new MessageSlideShell(display, 2, "ShortTitle3", "ShortText", (String)null, -1);
/*      */     
/* 1057 */     for (int x = 0; x < 10; x++)
/* 1058 */       text = text + "\n\n\n\n\n\n\n\nWow";
/* 1059 */     new MessageSlideShell(display, 2, title + "4", text, "Details", -1);
/*      */     
/*      */ 
/* 1062 */     new MessageSlideShell(display, 1, title + "5", text, (String)null, -1);
/*      */     
/*      */ 
/* 1065 */     waitUntilClosed();
/*      */   }
/*      */   
/*      */   public Color getUrlColor() {
/* 1069 */     return this.colorURL;
/*      */   }
/*      */   
/*      */   public void setUrlColor(Color urlColor) {
/* 1073 */     this.colorURL = urlColor;
/*      */   }
/*      */   
/*      */   public Color getColorFG() {
/* 1077 */     return this.colorFG;
/*      */   }
/*      */   
/*      */   public void setColorFG(Color colorFG) {
/* 1081 */     this.colorFG = colorFG;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/MessageSlideShell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */