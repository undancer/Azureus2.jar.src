/*     */ package com.aelitis.azureus.ui.swt.shells;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.pairing.PairingException;
/*     */ import com.aelitis.azureus.core.pairing.PairingManager;
/*     */ import com.aelitis.azureus.core.pairing.PairingManagerFactory;
/*     */ import com.aelitis.azureus.core.pairing.PairingManagerListener;
/*     */ import com.aelitis.azureus.core.pairing.PairingTest;
/*     */ import com.aelitis.azureus.core.pairing.PairingTestListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectButton;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectImage;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText_UrlClickedListener;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog.SkinnedDialogClosedListener;
/*     */ import com.aelitis.azureus.util.StringCompareUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter.URLInfo;
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
/*     */ public class RemotePairingWindow
/*     */   implements PairingManagerListener
/*     */ {
/*     */   private static final String PLUGINID_WEBUI = "xmwebui";
/*     */   private static final boolean SHOW_SPEW = false;
/*     */   private static final boolean DEBUG = false;
/*     */   private static final boolean USE_OUR_QR = false;
/*  79 */   static RemotePairingWindow instance = null;
/*     */   
/*     */   private SkinnedDialog skinnedDialog;
/*     */   
/*     */   private SWTSkin skin;
/*     */   private SWTSkinObjectButton soEnablePairing;
/*     */   private PairingManager pairingManager;
/*     */   private SWTSkinObject soCodeArea;
/*     */   private Font fontCode;
/*     */   private String accessCode;
/*     */   private Control control;
/*     */   private SWTSkinObjectText soStatusText;
/*     */   private SWTSkinObject soFTUX;
/*     */   private SWTSkinObject soCode;
/*     */   private SWTSkinObjectText soToClipboard;
/*     */   private boolean hideCode;
/*     */   private String fallBackStatusText;
/*     */   private static testPairingClass testPairingClass;
/*     */   private PairingTest pairingTest;
/*     */   private boolean alreadyTested;
/*     */   private String storedToClipboardText;
/*     */   private String lastPairingTestError;
/*     */   private SWTSkinObjectImage soQR;
/*     */   
/*     */   public RemotePairingWindow()
/*     */   {
/* 105 */     this.hideCode = true;
/*     */     
/* 107 */     this.fallBackStatusText = "";
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
/*     */   public static void open()
/*     */   {
/*     */     RemotePairingWindow inst;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */     synchronized (RemotePairingWindow.class) {
/* 133 */       if (instance == null) {
/* 134 */         instance = new RemotePairingWindow();
/*     */       }
/*     */       
/* 137 */       inst = instance;
/*     */     }
/*     */     
/* 140 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.SWT_THREAD, new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 143 */         this.val$inst._open();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private PluginInterface getWebUI() {
/* 149 */     return AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("xmwebui", true);
/*     */   }
/*     */   
/*     */   private void _open()
/*     */   {
/* 154 */     this.alreadyTested = false;
/*     */     
/* 156 */     this.pairingManager = PairingManagerFactory.getSingleton();
/* 157 */     PluginInterface piWebUI = getWebUI();
/*     */     
/* 159 */     boolean showFTUX = (piWebUI == null) || (!this.pairingManager.isEnabled());
/*     */     
/* 161 */     if ((this.skinnedDialog == null) || (this.skinnedDialog.isDisposed())) {
/* 162 */       this.skinnedDialog = new SkinnedDialog("skin3_dlg_remotepairing", "shell", 2144);
/*     */       
/*     */ 
/* 165 */       this.skin = this.skinnedDialog.getSkin();
/*     */       
/* 167 */       this.soCodeArea = this.skin.getSkinObject("code-area");
/* 168 */       this.control = this.soCodeArea.getControl();
/*     */       
/* 170 */       this.soEnablePairing = ((SWTSkinObjectButton)this.skin.getSkinObject("enable-pairing"));
/* 171 */       this.soEnablePairing.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask)
/*     */         {
/* 175 */           skinObject.getControl().setEnabled(false);
/*     */           
/* 177 */           if (!RemotePairingWindow.this.pairingManager.isEnabled())
/*     */           {
/*     */ 
/* 180 */             RemotePairingWindow.this.pairingManager.setEnabled(true);
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 190 */             RemotePairingWindow.this.somethingChanged(RemotePairingWindow.this.pairingManager);
/*     */           }
/*     */           
/* 193 */           if (RemotePairingWindow.this.getWebUI() == null) {
/* 194 */             RemotePairingWindow.this.installWebUI();
/*     */           } else {
/* 196 */             RemotePairingWindow.this.switchToCode();
/*     */           }
/*     */           
/*     */         }
/* 200 */       });
/* 201 */       this.soFTUX = this.skin.getSkinObject("pairing-ftux");
/* 202 */       this.soCode = this.skin.getSkinObject("pairing-code");
/* 203 */       this.soQR = ((SWTSkinObjectImage)this.skin.getSkinObject("pairing-qr"));
/* 204 */       if (this.accessCode != null) {
/* 205 */         setupQR(this.accessCode);
/*     */       }
/*     */       
/* 208 */       this.soStatusText = ((SWTSkinObjectText)this.skin.getSkinObject("status-text"));
/* 209 */       this.soStatusText.addUrlClickedListener(new SWTSkinObjectText_UrlClickedListener() {
/*     */         public boolean urlClicked(GCStringPrinter.URLInfo urlInfo) {
/* 211 */           if (urlInfo.url.equals("retry"))
/*     */           {
/*     */ 
/*     */ 
/* 215 */             RemotePairingWindow.this.alreadyTested = false;
/* 216 */             RemotePairingWindow.this.testPairing(false);
/* 217 */             return true;
/*     */           }
/* 219 */           return false;
/*     */         }
/*     */         
/* 222 */       });
/* 223 */       this.pairingManager.addListener(this);
/*     */       
/* 225 */       Font font = this.control.getFont();
/* 226 */       GC gc = new GC(this.control);
/* 227 */       this.fontCode = FontUtils.getFontWithHeight(font, gc, Constants.isWindows ? 20 : 18, 1);
/*     */       
/* 229 */       gc.dispose();
/* 230 */       this.control.setFont(this.fontCode);
/*     */       
/* 232 */       this.control.addPaintListener(new PaintListener() {
/*     */         public void paintControl(PaintEvent e) {
/* 234 */           Color oldColor = e.gc.getForeground();
/*     */           
/* 236 */           Rectangle printArea = ((Composite)e.widget).getClientArea();
/* 237 */           printArea.y += 10;
/* 238 */           printArea.height -= 20;
/* 239 */           int fullWidth = printArea.width;
/* 240 */           int fullHeight = printArea.height;
/* 241 */           GCStringPrinter sp = new GCStringPrinter(e.gc, MessageText.getString("remote.pairing.accesscode"), printArea, false, false, 0);
/*     */           
/*     */ 
/* 244 */           sp.calculateMetrics();
/* 245 */           Point sizeAccess = sp.getCalculatedSize();
/*     */           
/* 247 */           String drawAccessCode = RemotePairingWindow.this.accessCode == null ? "      " : RemotePairingWindow.this.accessCode;
/*     */           
/* 249 */           int numBoxes = drawAccessCode.length();
/* 250 */           int boxSize = 25;
/* 251 */           int boxSizeAndPadding = 30;
/* 252 */           int allBoxesWidth = numBoxes * boxSizeAndPadding;
/* 253 */           int textPadding = 15;
/* 254 */           printArea.y = ((fullHeight - boxSizeAndPadding - sizeAccess.y + textPadding) / 2);
/*     */           
/* 256 */           sp.printString(e.gc, printArea, 16777344);
/* 257 */           e.gc.setBackground(Colors.white);
/* 258 */           e.gc.setForeground(Colors.blue);
/*     */           
/* 260 */           int xStart = (fullWidth - allBoxesWidth) / 2;
/* 261 */           int yStart = printArea.y + sizeAccess.y + textPadding;
/* 262 */           for (int i = 0; i < numBoxes; i++) {
/* 263 */             Rectangle r = new Rectangle(xStart + i * boxSizeAndPadding, yStart, boxSize, boxSize);
/*     */             
/* 265 */             e.gc.fillRectangle(r);
/* 266 */             e.gc.setForeground(Colors.blues[9]);
/* 267 */             e.gc.drawRectangle(r);
/* 268 */             if (RemotePairingWindow.this.isCodeVisible()) {
/* 269 */               e.gc.setForeground(oldColor);
/* 270 */               GCStringPrinter.printString(e.gc, "" + drawAccessCode.charAt(i), r, false, false, 16777216);
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         }
/* 276 */       });
/* 277 */       this.soToClipboard = ((SWTSkinObjectText)this.skin.getSkinObject("pair-clipboard"));
/*     */       
/* 279 */       this.soToClipboard.addUrlClickedListener(new SWTSkinObjectText_UrlClickedListener() {
/*     */         public boolean urlClicked(GCStringPrinter.URLInfo urlInfo) {
/* 281 */           if (urlInfo.url.equals("new")) {
/*     */             try {
/* 283 */               RemotePairingWindow.this.accessCode = RemotePairingWindow.this.pairingManager.getReplacementAccessCode();
/*     */             }
/*     */             catch (PairingException e) {}
/*     */             
/* 287 */             RemotePairingWindow.this.control.redraw();
/* 288 */             RemotePairingWindow.this.setupQR(RemotePairingWindow.this.accessCode);
/*     */             
/* 290 */             String s = RemotePairingWindow.this.soToClipboard.getText();
/* 291 */             int i = s.indexOf("|");
/* 292 */             if (i > 0) {
/* 293 */               RemotePairingWindow.this.soToClipboard.setText(s.substring(0, i - 1));
/*     */             }
/* 295 */           } else if (urlInfo.url.equals("clip")) {
/* 296 */             ClipboardCopy.copyToClipBoard(RemotePairingWindow.this.accessCode);
/*     */           }
/* 298 */           return true;
/*     */         }
/* 300 */       });
/* 301 */       SWTSkinButtonUtility btnToClipboard = new SWTSkinButtonUtility(this.soToClipboard);
/*     */       
/* 303 */       btnToClipboard.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */ 
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {}
/*     */ 
/* 308 */       });
/* 309 */       this.skinnedDialog.addCloseListener(new SkinnedDialog.SkinnedDialogClosedListener() {
/*     */         public void skinDialogClosed(SkinnedDialog dialog) {
/* 311 */           RemotePairingWindow.this.skinnedDialog = null;
/* 312 */           RemotePairingWindow.this.pairingManager.removeListener(RemotePairingWindow.this);
/* 313 */           Utils.disposeSWTObjects(new Object[] { RemotePairingWindow.this.fontCode });
/*     */           
/*     */ 
/* 316 */           if (RemotePairingWindow.this.pairingTest != null) {
/* 317 */             RemotePairingWindow.this.pairingTest.cancel();
/*     */           }
/*     */         }
/*     */       });
/*     */       
/* 322 */       if (showFTUX) {
/* 323 */         this.soFTUX.getControl().moveAbove(null);
/*     */       }
/*     */     }
/* 326 */     setCodeVisible(false);
/* 327 */     this.skinnedDialog.open();
/* 328 */     setCodeVisible(true);
/*     */     
/* 330 */     if (showFTUX) {
/* 331 */       switchToFTUX();
/*     */     } else {
/* 333 */       switchToCode();
/*     */     }
/*     */   }
/*     */   
/*     */   private void setupQR(String ac) {
/* 338 */     if ((this.soQR == null) || (this.soQR.isDisposed())) {
/* 339 */       return;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 372 */     setupQR_URL(ac);
/*     */   }
/*     */   
/*     */   private void setupQR_URL(String ac)
/*     */   {
/* 377 */     String url = "https://chart.googleapis.com/chart?chs=150x150&cht=qr&chl=" + UrlUtils.encode(new StringBuilder().append("http://remote.vuze.com/?ac=").append(ac).append("&ref=1").toString()) + "&choe=UTF-8&chld=|0";
/*     */     
/*     */ 
/* 380 */     this.soQR.setImageUrl(url);
/*     */   }
/*     */   
/*     */   public void switchToFTUX()
/*     */   {
/* 385 */     SWTSkinObject soPairInstallArea = this.skin.getSkinObject("pair-install");
/* 386 */     if (soPairInstallArea != null) {
/* 387 */       soPairInstallArea.getControl().moveAbove(null);
/*     */     }
/* 389 */     this.soFTUX.setVisible(true);
/* 390 */     this.soCode.setVisible(false);
/*     */   }
/*     */   
/*     */   public void switchToCode()
/*     */   {
/* 395 */     somethingChanged(this.pairingManager);
/*     */     
/* 397 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/* 401 */         if ((RemotePairingWindow.this.skinnedDialog == null) || (RemotePairingWindow.this.skinnedDialog.isDisposed())) {
/* 402 */           return;
/*     */         }
/*     */         
/* 405 */         SWTSkinObjectImage soImage = (SWTSkinObjectImage)RemotePairingWindow.this.skin.getSkinObject("status-image");
/* 406 */         if (soImage != null) {
/* 407 */           soImage.setImageByID("icon.spin", null);
/*     */         }
/*     */         
/* 410 */         SWTSkinObject soPairArea = RemotePairingWindow.this.skin.getSkinObject("reset-pair-area");
/* 411 */         if (soPairArea != null) {
/* 412 */           soPairArea.getControl().moveAbove(null);
/*     */         }
/* 414 */         RemotePairingWindow.this.soFTUX.setVisible(false);
/* 415 */         RemotePairingWindow.this.soCode.setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void testPairing(boolean delay)
/*     */   {
/* 426 */     if (this.alreadyTested) {
/* 427 */       return;
/*     */     }
/*     */     
/* 430 */     this.lastPairingTestError = "";
/* 431 */     this.alreadyTested = true;
/*     */     
/* 433 */     this.storedToClipboardText = this.soToClipboard.getText();
/*     */     try {
/* 435 */       setCodeVisible(false);
/* 436 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 438 */           RemotePairingWindow.this.control.redraw();
/* 439 */           SWTSkinObjectImage soImage = (SWTSkinObjectImage)RemotePairingWindow.this.skin.getSkinObject("status-image");
/* 440 */           if (soImage != null) {
/* 441 */             soImage.setImageByID("icon.spin", null);
/*     */           }
/*     */           
/*     */         }
/* 445 */       });
/* 446 */       this.soStatusText.setTextID("remote.pairing.test.running");
/* 447 */       this.soStatusText.setTextColor(ColorCache.getColor(this.control.getDisplay(), "#000000"));
/*     */       
/* 449 */       this.soToClipboard.setText(" ");
/*     */       
/* 451 */       final PairingTestListener testListener = new PairingTestListener()
/*     */       {
/*     */         public void testStarted(PairingTest test) {}
/*     */         
/*     */         public void testComplete(PairingTest test) {
/* 456 */           if ((RemotePairingWindow.this.skinnedDialog == null) || (RemotePairingWindow.this.skinnedDialog.isDisposed()) || (RemotePairingWindow.this.control.isDisposed())) {
/* 457 */             return;
/*     */           }
/*     */           
/* 460 */           int outcome = test.getOutcome();
/* 461 */           String iconID = null;
/* 462 */           String colorID = "#000000";
/* 463 */           switch (outcome) {
/*     */           case 1: 
/* 465 */             RemotePairingWindow.this.fallBackStatusText = MessageText.getString("remote.pairing.test.success");
/* 466 */             iconID = "icon.success";
/* 467 */             colorID = "#007305";
/* 468 */             break;
/*     */           
/*     */           case 6: 
/* 471 */             RemotePairingWindow.this.fallBackStatusText = test.getErrorMessage();
/* 472 */             iconID = "icon.warning";
/* 473 */             colorID = "#A97000";
/* 474 */             break;
/*     */           
/*     */           case 3: 
/*     */           case 4: 
/*     */           case 5: 
/* 479 */             RemotePairingWindow.this.fallBackStatusText = MessageText.getString("remote.pairing.test.unavailable", new String[] { test.getErrorMessage() });
/*     */             
/*     */ 
/*     */ 
/* 483 */             iconID = "icon.warning";
/* 484 */             colorID = "#C98000";
/* 485 */             break;
/*     */           case 2: 
/*     */           default: 
/* 488 */             RemotePairingWindow.this.fallBackStatusText = MessageText.getString("remote.pairing.test.fail", new String[] { test.getErrorMessage() });
/*     */             
/*     */ 
/*     */ 
/* 492 */             iconID = "icon.failure";
/* 493 */             colorID = "#c90000";
/*     */           }
/*     */           
/*     */           
/* 497 */           RemotePairingWindow.this.setCodeVisible(true);
/* 498 */           final String fIconID = iconID;
/* 499 */           RemotePairingWindow.this.somethingChanged(RemotePairingWindow.this.pairingManager);
/* 500 */           RemotePairingWindow.this.lastPairingTestError = RemotePairingWindow.this.pairingTest.getErrorMessage();
/* 501 */           Utils.execSWTThread(new AERunnable() {
/*     */             public void runSupport() {
/* 503 */               if (!RemotePairingWindow.this.control.isDisposed()) {
/* 504 */                 RemotePairingWindow.this.control.redraw();
/* 505 */                 SWTSkinObjectImage soImage = (SWTSkinObjectImage)RemotePairingWindow.this.skin.getSkinObject("status-image");
/* 506 */                 if (soImage != null) {
/* 507 */                   soImage.setImageByID(fIconID, null);
/*     */                 }
/*     */               }
/*     */             }
/* 511 */           });
/* 512 */           RemotePairingWindow.this.updateToolTip();
/* 513 */           RemotePairingWindow.this.soStatusText.setText(RemotePairingWindow.this.fallBackStatusText);
/* 514 */           RemotePairingWindow.this.soStatusText.setTextColor(ColorCache.getColor(RemotePairingWindow.this.control.getDisplay(), colorID));
/*     */           
/* 516 */           RemotePairingWindow.this.soToClipboard.setText(RemotePairingWindow.this.storedToClipboardText);
/*     */         }
/* 518 */       };
/* 519 */       SimpleTimer.addEvent("testPairing", SystemTime.getOffsetTime(delay ? 5000L : 0L), new TimerEventPerformer()
/*     */       {
/*     */         public void perform(TimerEvent event) {
/*     */           try {
/* 523 */             RemotePairingWindow.this.pairingTest = RemotePairingWindow.this.pairingManager.testService("xmwebui", testListener);
/*     */           }
/*     */           catch (PairingException e) {
/* 526 */             RemotePairingWindow.this.finishFailedTest();
/*     */             
/* 528 */             RemotePairingWindow.this.setStatusToException(e);
/* 529 */             Debug.out(e);
/*     */           }
/*     */           
/* 532 */           if (RemotePairingWindow.this.pairingTest == null) {
/* 533 */             RemotePairingWindow.this.finishFailedTest();
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */ 
/*     */       });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 543 */       finishFailedTest();
/*     */       
/* 545 */       setStatusToException(e);
/* 546 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void setStatusToException(Exception e)
/*     */   {
/* 552 */     this.soStatusText.setText(Debug.getNestedExceptionMessage(e) + ". <A HREF=\"retry\">Try again</A>");
/* 553 */     this.soStatusText.setTextColor(ColorCache.getColor(this.control.getDisplay(), "#c90000"));
/*     */     
/*     */ 
/* 556 */     SWTSkinObjectImage soImage = (SWTSkinObjectImage)this.skin.getSkinObject("status-image");
/* 557 */     if (soImage != null) {
/* 558 */       soImage.setImageByID("icon.failure", null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void updateToolTip()
/*     */   {
/* 568 */     SWTSkinObjectImage soImage = (SWTSkinObjectImage)this.skin.getSkinObject("status-image");
/* 569 */     if (soImage != null) {
/* 570 */       String s = this.lastPairingTestError;
/* 571 */       if (s == null) {
/* 572 */         s = "";
/*     */       }
/*     */       
/* 575 */       String status = this.pairingManager.getStatus();
/* 576 */       if ((status != null) && (status.length() > 0)) {
/* 577 */         if (s.length() > 0) {
/* 578 */           s = s + "\n";
/*     */         }
/* 580 */         s = s + "Pairing Status: " + status;
/*     */       }
/* 582 */       String lastPairingErr = this.pairingManager.getLastServerError();
/* 583 */       if ((lastPairingErr != null) && (lastPairingErr.length() > 0)) {
/* 584 */         if (s.length() > 0) {
/* 585 */           s = s + "\n";
/*     */         }
/* 587 */         s = s + "Pairing Error: " + lastPairingErr;
/*     */       }
/* 589 */       soImage.setTooltipID("!" + s + "!");
/*     */     }
/*     */   }
/*     */   
/*     */   private void finishFailedTest() {
/* 594 */     setCodeVisible(true);
/* 595 */     somethingChanged(this.pairingManager);
/* 596 */     if ((this.storedToClipboardText != null) && (this.storedToClipboardText.length() > 0)) {
/* 597 */       this.soToClipboard.setText(this.storedToClipboardText);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void installWebUI() {
/* 602 */     PluginInstaller installer = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInstaller();
/*     */     
/* 604 */     StandardPlugin vuze_plugin = null;
/*     */     try
/*     */     {
/* 607 */       vuze_plugin = installer.getStandardPlugin("xmwebui");
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 612 */     if (vuze_plugin == null) {
/* 613 */       return;
/*     */     }
/*     */     
/* 616 */     if (vuze_plugin.isAlreadyInstalled()) {
/* 617 */       PluginInterface plugin = vuze_plugin.getAlreadyInstalledPlugin();
/* 618 */       plugin.getPluginState().setDisabled(false);
/* 619 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 623 */       switchToFTUX();
/*     */       
/* 625 */       SWTSkinObject soInstall = this.skin.getSkinObject("pairing-install");
/* 626 */       final SWTSkinObject soLearnMore = this.skin.getSkinObject("learn-more");
/* 627 */       if (soLearnMore != null) {
/* 628 */         soLearnMore.setVisible(false);
/*     */       }
/*     */       
/* 631 */       Map<Integer, Object> properties = new HashMap();
/*     */       
/* 633 */       properties.put(Integer.valueOf(1), Integer.valueOf(2));
/*     */       
/*     */ 
/* 636 */       properties.put(Integer.valueOf(2), soInstall.getControl());
/*     */       
/*     */ 
/* 639 */       properties.put(Integer.valueOf(3), Boolean.valueOf(true));
/*     */       
/* 641 */       installer.install(new InstallablePlugin[] { vuze_plugin }, false, properties, new PluginInstallationListener()
/*     */       {
/*     */         public void completed()
/*     */         {
/* 645 */           if (soLearnMore != null) {
/* 646 */             soLearnMore.setVisible(true);
/*     */           }
/* 648 */           RemotePairingWindow.this.switchToCode();
/*     */         }
/*     */         
/*     */         public void cancelled() {
/* 652 */           Utils.execSWTThread(new AERunnable()
/*     */           {
/*     */             public void runSupport()
/*     */             {
/* 656 */               if ((RemotePairingWindow.this.skinnedDialog != null) && (!RemotePairingWindow.this.skinnedDialog.isDisposed()))
/*     */               {
/* 658 */                 RemotePairingWindow.this.skinnedDialog.close();
/*     */                 
/* 660 */                 RemotePairingWindow.this.skinnedDialog = null;
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */         public void failed(PluginException e)
/*     */         {
/* 668 */           Debug.out(e);
/*     */         }
/*     */         
/*     */ 
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 676 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void somethingChanged(PairingManager pm)
/*     */   {
/* 682 */     if (this.skinnedDialog.isDisposed()) {
/* 683 */       return;
/*     */     }
/*     */     
/* 686 */     updateToolTip();
/*     */     
/* 688 */     String lastAccessCode = this.accessCode;
/*     */     
/* 690 */     this.accessCode = this.pairingManager.peekAccessCode();
/* 691 */     boolean newAccessCode = !StringCompareUtils.equals(lastAccessCode, this.accessCode);
/* 692 */     if ((this.accessCode != null) && (getWebUI() != null) && (!this.alreadyTested) && (!pm.hasActionOutstanding()))
/*     */     {
/* 694 */       if (newAccessCode)
/*     */       {
/* 696 */         testPairing(true);
/*     */       } else {
/* 698 */         testPairing(false);
/*     */       }
/*     */     } else {
/* 701 */       String last_error = pm.getLastServerError();
/*     */       
/* 703 */       if ((last_error != null) && (last_error.length() > 0))
/*     */       {
/* 705 */         this.soStatusText.setText(last_error);
/* 706 */         this.soStatusText.setTextColor(ColorCache.getColor(this.control.getDisplay(), "#c90000"));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 711 */     if (newAccessCode) {
/* 712 */       setupQR(this.accessCode);
/*     */       
/* 714 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 716 */           RemotePairingWindow.this.control.redraw();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isCodeVisible() {
/* 723 */     return this.hideCode;
/*     */   }
/*     */   
/*     */   public void setCodeVisible(boolean hideCode) {
/* 727 */     this.hideCode = hideCode;
/*     */     
/* 729 */     if ((this.soQR != null) && (!this.soQR.isDisposed())) {
/* 730 */       this.soQR.setVisible(hideCode);
/*     */     }
/* 732 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 734 */         if ((RemotePairingWindow.this.control != null) && (!RemotePairingWindow.this.control.isDisposed())) {
/* 735 */           RemotePairingWindow.this.control.redraw();
/* 736 */           RemotePairingWindow.this.control.update();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static class testPairingClass
/*     */     implements PairingTest
/*     */   {
/* 745 */     int curOutcome = 0;
/*     */     
/* 747 */     int[] testOutcomes = { 1, 2, 6, 5, 4, 3 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 756 */     String[] testErrs = { "Success", "Could Not Connect blah blah technical stuff", "You Cancelled (unpossible!)", "Server Failed", "Server Overloaded", "Server Unavailable" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void inc()
/*     */     {
/* 766 */       this.curOutcome += 1;
/* 767 */       if (this.curOutcome == this.testOutcomes.length) {
/* 768 */         this.curOutcome = 0;
/*     */       }
/*     */     }
/*     */     
/*     */     public int getOutcome() {
/* 773 */       return this.testOutcomes[this.curOutcome];
/*     */     }
/*     */     
/*     */     public String getErrorMessage() {
/* 777 */       return this.testErrs[this.curOutcome];
/*     */     }
/*     */     
/*     */     public void cancel() {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/RemotePairingWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */