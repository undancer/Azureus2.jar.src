/*     */ package org.gudy.azureus2.ui.swt.views.peer;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ScrollBar;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend;
/*     */ import org.gudy.azureus2.ui.swt.debug.UIDebugGenerator;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PeerInfoView
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private static final int BLOCK_FILLSIZE = 14;
/*     */   private static final int BLOCK_SPACING = 2;
/*     */   private static final int BLOCK_SIZE = 16;
/*     */   private static final int BLOCKCOLOR_AVAIL_HAVE = 0;
/*     */   private static final int BLOCKCOLOR_AVAIL_NOHAVE = 1;
/*     */   private static final int BLOCKCOLOR_NOAVAIL_HAVE = 2;
/*     */   private static final int BLOCKCOLOR_NOAVAIL_NOHAVE = 3;
/*     */   private static final int BLOCKCOLOR_TRANSFER = 4;
/*     */   private static final int BLOCKCOLOR_NEXT = 5;
/*     */   private static final int BLOCKCOLOR_AVAILCOUNT = 6;
/*     */   private Composite peerInfoComposite;
/*     */   private ScrolledComposite sc;
/*     */   protected Canvas peerInfoCanvas;
/*     */   private Color[] blockColors;
/*     */   private Label topLabel;
/*     */   private Label imageLabel;
/* 116 */   private int graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update") * 2;
/*     */   
/*     */ 
/* 119 */   private int loopFactor = 0;
/*     */   
/*     */   private PEPeer peer;
/*     */   
/* 123 */   private Plugin countryLocator = null;
/*     */   
/*     */   private String sCountryImagesDir;
/*     */   
/* 127 */   private Font font = null;
/*     */   
/* 129 */   Image img = null;
/*     */   
/*     */ 
/*     */   protected boolean refreshInfoCanvasQueued;
/*     */   
/*     */ 
/*     */   private UISWTView swtView;
/*     */   
/*     */ 
/*     */   public PeerInfoView()
/*     */   {
/* 140 */     this.blockColors = new Color[] { Colors.blues[9], Colors.blues[2], Colors.fadedGreen, Colors.white, Colors.red, Colors.fadedRed, Colors.black };
/*     */     
/*     */ 
/*     */ 
/* 144 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 146 */         PeerInfoView.this.initCountryPlugin();
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
/*     */   private void initCountryPlugin()
/*     */   {
/*     */     try
/*     */     {
/* 162 */       PluginInterface pi = PluginInitializer.getDefaultInterface().getPluginManager().getPluginInterfaceByID("CountryLocator");
/*     */       
/* 164 */       if (pi != null) {
/* 165 */         this.countryLocator = pi.getPlugin();
/* 166 */         if ((!pi.getPluginState().isOperational()) || (pi.getUtilities().compareVersions(pi.getPluginVersion(), "1.6") < 0))
/*     */         {
/* 168 */           this.countryLocator = null;
/*     */         }
/* 170 */         if (this.countryLocator != null) {
/* 171 */           this.sCountryImagesDir = ((String)this.countryLocator.getClass().getMethod("getImageLocation", new Class[] { Integer.TYPE }).invoke(this.countryLocator, new Object[] { new Integer(0) }));
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void dataSourceChanged(Object newDataSource)
/*     */   {
/* 182 */     if (((newDataSource instanceof Object[])) && (((Object[])newDataSource).length > 0))
/*     */     {
/* 184 */       newDataSource = ((Object[])(Object[])newDataSource)[0];
/*     */     }
/*     */     
/* 187 */     if ((newDataSource instanceof PEPeer)) {
/* 188 */       this.peer = ((PEPeer)newDataSource);
/*     */     } else {
/* 190 */       this.peer = null;
/*     */     }
/*     */     
/* 193 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*     */       public void runSupport() {
/* 195 */         PeerInfoView.this.swt_fillPeerInfoSection();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 201 */     return MessageText.getString("PeersView.BlockView.title");
/*     */   }
/*     */   
/*     */   private void initialize(Composite composite) {
/* 205 */     if ((this.peerInfoComposite != null) && (!this.peerInfoComposite.isDisposed())) {
/* 206 */       Logger.log(new LogEvent(LogIDs.GUI, 3, "PeerInfoView already initialized! Stack: " + Debug.getStackTrace(true, false)));
/*     */       
/*     */ 
/* 209 */       delete();
/*     */     }
/* 211 */     createPeerInfoPanel(composite);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Composite createPeerInfoPanel(Composite parent)
/*     */   {
/* 221 */     this.peerInfoComposite = new Composite(parent, 0);
/* 222 */     GridLayout layout = new GridLayout();
/* 223 */     layout.numColumns = 2;
/* 224 */     layout.horizontalSpacing = 0;
/* 225 */     layout.verticalSpacing = 0;
/* 226 */     layout.marginHeight = 0;
/* 227 */     layout.marginWidth = 0;
/* 228 */     this.peerInfoComposite.setLayout(layout);
/* 229 */     GridData gridData = new GridData(4, 4, true, true);
/* 230 */     Utils.setLayoutData(this.peerInfoComposite, gridData);
/*     */     
/* 232 */     this.imageLabel = new Label(this.peerInfoComposite, 0);
/* 233 */     gridData = new GridData();
/* 234 */     if ((ImageRepository.hasCountryFlags(false)) || (this.countryLocator != null))
/* 235 */       gridData.widthHint = 28;
/* 236 */     Utils.setLayoutData(this.imageLabel, gridData);
/*     */     
/* 238 */     this.topLabel = new Label(this.peerInfoComposite, 0);
/* 239 */     gridData = new GridData(4, -1, false, false);
/* 240 */     Utils.setLayoutData(this.topLabel, gridData);
/*     */     
/* 242 */     this.sc = new ScrolledComposite(this.peerInfoComposite, 512);
/* 243 */     this.sc.setExpandHorizontal(true);
/* 244 */     this.sc.setExpandVertical(true);
/* 245 */     layout = new GridLayout();
/* 246 */     layout.horizontalSpacing = 0;
/* 247 */     layout.verticalSpacing = 0;
/* 248 */     layout.marginHeight = 0;
/* 249 */     layout.marginWidth = 0;
/* 250 */     this.sc.setLayout(layout);
/* 251 */     gridData = new GridData(4, 4, true, true, 2, 1);
/* 252 */     Utils.setLayoutData(this.sc, gridData);
/* 253 */     this.sc.getVerticalBar().setIncrement(16);
/*     */     
/* 255 */     this.peerInfoCanvas = new Canvas(this.sc, 1310720);
/* 256 */     gridData = new GridData(4, -1, true, false);
/* 257 */     Utils.setLayoutData(this.peerInfoCanvas, gridData);
/* 258 */     this.peerInfoCanvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 260 */         if ((e.width <= 0) || (e.height <= 0))
/* 261 */           return;
/*     */         try {
/* 263 */           Rectangle bounds = PeerInfoView.this.img == null ? null : PeerInfoView.this.img.getBounds();
/* 264 */           if (bounds == null) {
/* 265 */             e.gc.fillRectangle(e.x, e.y, e.width, e.height);
/*     */           } else {
/* 267 */             if (e.x + e.width > bounds.width) {
/* 268 */               e.gc.fillRectangle(bounds.width, e.y, e.x + e.width - bounds.width + 1, e.height);
/*     */             }
/* 270 */             if (e.y + e.height > bounds.height) {
/* 271 */               e.gc.fillRectangle(e.x, bounds.height, e.width, e.y + e.height - bounds.height + 1);
/*     */             }
/*     */             
/* 274 */             int width = Math.min(e.width, bounds.width - e.x);
/* 275 */             int height = Math.min(e.height, bounds.height - e.y);
/* 276 */             e.gc.drawImage(PeerInfoView.this.img, e.x, e.y, width, height, e.x, e.y, width, height);
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Exception ex) {}
/*     */       }
/* 282 */     });
/* 283 */     Listener doNothingListener = new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {}
/* 286 */     };
/* 287 */     this.peerInfoCanvas.addListener(1, doNothingListener);
/*     */     
/* 289 */     this.peerInfoCanvas.addListener(11, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 291 */         if ((PeerInfoView.this.refreshInfoCanvasQueued) || (!PeerInfoView.this.peerInfoCanvas.isVisible())) {
/* 292 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 297 */         Utils.execSWTThreadLater(100, new AERunnable() {
/*     */           public void runSupport() {
/* 299 */             if (PeerInfoView.this.refreshInfoCanvasQueued) {
/* 300 */               return;
/*     */             }
/* 302 */             PeerInfoView.this.refreshInfoCanvasQueued = true;
/*     */             
/* 304 */             if (PeerInfoView.this.img != null) {
/* 305 */               int iOldColCount = PeerInfoView.this.img.getBounds().width / 16;
/* 306 */               int iNewColCount = PeerInfoView.this.peerInfoCanvas.getClientArea().width / 16;
/* 307 */               if (iOldColCount != iNewColCount) {
/* 308 */                 PeerInfoView.this.refreshInfoCanvas();
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 314 */     });
/* 315 */     this.sc.setContent(this.peerInfoCanvas);
/*     */     
/* 317 */     Legend.createLegendComposite(this.peerInfoComposite, this.blockColors, new String[] { "PeersView.BlockView.Avail.Have", "PeersView.BlockView.Avail.NoHave", "PeersView.BlockView.NoAvail.Have", "PeersView.BlockView.NoAvail.NoHave", "PeersView.BlockView.Transfer", "PeersView.BlockView.NextRequest", "PeersView.BlockView.AvailCount" }, new GridData(4, -1, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 326 */     int iFontPixelsHeight = 10;
/* 327 */     int iFontPointHeight = iFontPixelsHeight * 72 / Utils.getDPIRaw(this.peerInfoCanvas.getDisplay()).y;
/*     */     
/* 329 */     Font f = this.peerInfoCanvas.getFont();
/* 330 */     FontData[] fontData = f.getFontData();
/* 331 */     fontData[0].setHeight(iFontPointHeight);
/* 332 */     this.font = new Font(this.peerInfoCanvas.getDisplay(), fontData);
/*     */     
/* 334 */     return this.peerInfoComposite;
/*     */   }
/*     */   
/*     */   private void swt_fillPeerInfoSection() {
/* 338 */     if ((this.peerInfoComposite == null) || (this.peerInfoComposite.isDisposed())) {
/* 339 */       return;
/*     */     }
/* 341 */     if (this.imageLabel.getImage() != null) {
/* 342 */       Image image = this.imageLabel.getImage();
/* 343 */       this.imageLabel.setImage(null);
/* 344 */       image.dispose();
/*     */     }
/*     */     
/* 347 */     if (this.peer == null) {
/* 348 */       this.topLabel.setText("");
/*     */     } else {
/* 350 */       String s = this.peer.getClient();
/* 351 */       if (s == null) {
/* 352 */         s = "";
/* 353 */       } else if (s.length() > 0) {
/* 354 */         s = s + "; ";
/*     */       }
/*     */       
/* 357 */       s = s + this.peer.getIp() + "; " + DisplayFormatters.formatPercentFromThousands(this.peer.getPercentDoneInThousandNotation());
/*     */       
/*     */ 
/*     */ 
/* 361 */       this.topLabel.setText(s);
/*     */       
/* 363 */       Image flag = ImageRepository.getCountryFlag(this.peer, false);
/*     */       
/* 365 */       if (flag != null)
/*     */       {
/* 367 */         flag = new Image(flag.getDevice(), flag.getImageData());
/*     */         
/* 369 */         flag.setBackground(this.imageLabel.getBackground());
/*     */         
/* 371 */         this.imageLabel.setImage(flag);
/*     */         
/* 373 */         String[] country_details = PeerUtils.getCountryDetails(this.peer);
/*     */         
/* 375 */         if ((country_details != null) && (country_details.length == 2)) {
/* 376 */           this.imageLabel.setToolTipText(country_details[0] + "- " + country_details[1]);
/*     */         } else {
/* 378 */           this.imageLabel.setToolTipText("");
/*     */         }
/* 380 */       } else if (this.countryLocator != null) {
/*     */         try {
/* 382 */           String sCountry = (String)this.countryLocator.getClass().getMethod("getIPCountry", new Class[] { String.class, Locale.class }).invoke(this.countryLocator, new Object[] { this.peer.getIp(), Locale.getDefault() });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 387 */           String sCode = (String)this.countryLocator.getClass().getMethod("getIPISO3166", new Class[] { String.class }).invoke(this.countryLocator, new Object[] { this.peer.getIp() });
/*     */           
/*     */ 
/*     */ 
/* 391 */           this.imageLabel.setToolTipText(sCode + "- " + sCountry);
/*     */           
/* 393 */           InputStream is = this.countryLocator.getClass().getClassLoader().getResourceAsStream(this.sCountryImagesDir + "/" + sCode.toLowerCase() + ".png");
/*     */           
/*     */ 
/* 396 */           if (is != null) {
/*     */             try {
/* 398 */               Image img = new Image(this.imageLabel.getDisplay(), is);
/* 399 */               img.setBackground(this.imageLabel.getBackground());
/* 400 */               this.imageLabel.setImage(img);
/*     */             } finally {
/* 402 */               is.close();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */       else
/*     */       {
/* 410 */         this.imageLabel.setToolTipText("");
/*     */       }
/*     */     }
/*     */     
/* 414 */     refreshInfoCanvas();
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 418 */     if (this.loopFactor++ % this.graphicsUpdate == 0) {
/* 419 */       refreshInfoCanvas();
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
/*     */   private void refreshInfoCanvas()
/*     */   {
/* 432 */     this.refreshInfoCanvasQueued = false;
/*     */     
/* 434 */     if ((this.peerInfoComposite == null) || (this.peerInfoComposite.isDisposed()) || (!this.peerInfoComposite.isVisible()))
/*     */     {
/* 436 */       return;
/*     */     }
/*     */     
/* 439 */     this.peerInfoCanvas.layout(true);
/* 440 */     Rectangle bounds = this.peerInfoCanvas.getClientArea();
/* 441 */     if ((bounds.width <= 0) || (bounds.height <= 0)) {
/* 442 */       return;
/*     */     }
/* 444 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 445 */       this.img.dispose();
/* 446 */       this.img = null;
/*     */     }
/*     */     
/* 449 */     if ((this.peer == null) || (this.peer.getPeerState() != 30)) {
/* 450 */       GC gc = new GC(this.peerInfoCanvas);
/* 451 */       gc.fillRectangle(bounds);
/* 452 */       gc.dispose();
/*     */       
/* 454 */       return;
/*     */     }
/*     */     
/* 457 */     BitFlags peerHavePieces = this.peer.getAvailable();
/* 458 */     if (peerHavePieces == null) {
/* 459 */       GC gc = new GC(this.peerInfoCanvas);
/* 460 */       gc.fillRectangle(bounds);
/* 461 */       gc.dispose();
/*     */       
/* 463 */       return;
/*     */     }
/*     */     
/* 466 */     DiskManagerPiece[] dm_pieces = null;
/*     */     
/* 468 */     PEPeerManager pm = this.peer.getManager();
/*     */     
/* 470 */     DiskManager dm = pm.getDiskManager();
/*     */     
/* 472 */     dm_pieces = dm.getPieces();
/*     */     
/* 474 */     int iNumCols = bounds.width / 16;
/* 475 */     int iNeededHeight = ((dm.getNbPieces() - 1) / iNumCols + 1) * 16;
/*     */     
/* 477 */     if (this.sc.getMinHeight() != iNeededHeight) {
/* 478 */       this.sc.setMinHeight(iNeededHeight);
/* 479 */       this.sc.layout(true, true);
/* 480 */       bounds = this.peerInfoCanvas.getClientArea();
/*     */     }
/*     */     
/* 483 */     this.img = new Image(this.peerInfoCanvas.getDisplay(), bounds.width, iNeededHeight);
/* 484 */     GC gcImg = new GC(this.img);
/*     */     
/*     */     try
/*     */     {
/* 488 */       gcImg.setAdvanced(true);
/*     */       
/* 490 */       gcImg.setBackground(this.peerInfoCanvas.getBackground());
/* 491 */       gcImg.fillRectangle(0, 0, bounds.width, iNeededHeight);
/*     */       
/* 493 */       int[] availability = pm.getAvailability();
/*     */       
/* 495 */       int iNextDLPieceID = -1;
/* 496 */       int iDLPieceID = -1;
/* 497 */       int[] ourRequestedPieces = this.peer.getOutgoingRequestedPieceNumbers();
/* 498 */       if (ourRequestedPieces != null) {
/* 499 */         if (!this.peer.isChokingMe())
/*     */         {
/*     */ 
/* 502 */           if (ourRequestedPieces.length > 0) {
/* 503 */             iDLPieceID = ourRequestedPieces[0];
/* 504 */             if (ourRequestedPieces.length > 1) {
/* 505 */               iNextDLPieceID = ourRequestedPieces[1];
/*     */             }
/*     */           }
/* 508 */         } else if (ourRequestedPieces.length > 0) {
/* 509 */           iNextDLPieceID = ourRequestedPieces[0];
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 517 */       int[] peerRequestedPieces = this.peer.getIncomingRequestedPieceNumbers();
/* 518 */       if (peerRequestedPieces == null) {
/* 519 */         peerRequestedPieces = new int[0];
/*     */       }
/* 521 */       int peerNextRequestedPiece = -1;
/* 522 */       if (peerRequestedPieces.length > 0)
/* 523 */         peerNextRequestedPiece = peerRequestedPieces[0];
/* 524 */       Arrays.sort(peerRequestedPieces);
/*     */       
/* 526 */       int iRow = 0;
/* 527 */       int iCol = 0;
/* 528 */       for (int i = 0; i < peerHavePieces.flags.length; i++)
/*     */       {
/* 530 */         boolean done = dm_pieces == null ? false : dm_pieces[i].isDone();
/* 531 */         int iXPos = iCol * 16;
/* 532 */         int iYPos = iRow * 16;
/*     */         
/* 534 */         if (done) { int colorIndex;
/* 535 */           int colorIndex; if (peerHavePieces.flags[i] != 0) {
/* 536 */             colorIndex = 0;
/*     */           } else {
/* 538 */             colorIndex = 2;
/*     */           }
/* 540 */           gcImg.setBackground(this.blockColors[colorIndex]);
/* 541 */           gcImg.fillRectangle(iXPos, iYPos, 14, 14);
/*     */         }
/*     */         else {
/* 544 */           boolean partiallyDone = dm_pieces != null;
/*     */           
/*     */ 
/* 547 */           int x = iXPos;
/* 548 */           int width = 14;
/* 549 */           if (partiallyDone) { int colorIndex;
/* 550 */             int colorIndex; if (peerHavePieces.flags[i] != 0) {
/* 551 */               colorIndex = 0;
/*     */             } else {
/* 553 */               colorIndex = 2;
/*     */             }
/* 555 */             gcImg.setBackground(this.blockColors[colorIndex]);
/*     */             
/*     */ 
/* 558 */             int iNewWidth = (int)(dm_pieces[i].getNbWritten() / dm_pieces[i].getNbBlocks() * width);
/*     */             
/* 560 */             if (iNewWidth >= width) {
/* 561 */               iNewWidth = width - 1;
/* 562 */             } else if (iNewWidth <= 0) {
/* 563 */               iNewWidth = 1;
/*     */             }
/* 565 */             gcImg.fillRectangle(x, iYPos, iNewWidth, 14);
/* 566 */             width -= iNewWidth;
/* 567 */             x += iNewWidth; }
/*     */           int colorIndex;
/*     */           int colorIndex;
/* 570 */           if (peerHavePieces.flags[i] != 0) {
/* 571 */             colorIndex = 1;
/*     */           } else {
/* 573 */             colorIndex = 3;
/*     */           }
/* 575 */           gcImg.setBackground(this.blockColors[colorIndex]);
/* 576 */           gcImg.fillRectangle(x, iYPos, width, 14);
/*     */         }
/*     */         
/*     */ 
/* 580 */         if (i == iDLPieceID) {
/* 581 */           gcImg.setBackground(this.blockColors[4]);
/* 582 */           gcImg.fillPolygon(new int[] { iXPos, iYPos, iXPos + 14, iYPos, iXPos + 7, iYPos + 14 });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 587 */         if (i == iNextDLPieceID) {
/* 588 */           gcImg.setBackground(this.blockColors[5]);
/* 589 */           gcImg.fillPolygon(new int[] { iXPos + 2, iYPos + 2, iXPos + 14 - 1, iYPos + 2, iXPos + 7, iYPos + 14 - 1 });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 595 */         if (i == peerNextRequestedPiece) {
/* 596 */           gcImg.setBackground(this.blockColors[4]);
/* 597 */           gcImg.fillPolygon(new int[] { iXPos, iYPos + 14, iXPos + 14, iYPos + 14, iXPos + 7, iYPos });
/*     */ 
/*     */         }
/* 600 */         else if (Arrays.binarySearch(peerRequestedPieces, i) >= 0)
/*     */         {
/* 602 */           gcImg.setBackground(this.blockColors[5]);
/* 603 */           gcImg.fillPolygon(new int[] { iXPos + 1, iYPos + 14 - 2, iXPos + 14 - 2, iYPos + 14 - 2, iXPos + 7, iYPos + 2 });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 608 */         if ((availability != null) && (availability[i] < 10)) {
/* 609 */           gcImg.setFont(this.font);
/* 610 */           String sNumber = String.valueOf(availability[i]);
/* 611 */           Point size = gcImg.stringExtent(sNumber);
/*     */           
/* 613 */           int x = iXPos + 7 - size.x / 2;
/* 614 */           int y = iYPos + 7 - size.y / 2;
/* 615 */           gcImg.setForeground(this.blockColors[6]);
/* 616 */           gcImg.drawText(sNumber, x, y, true);
/*     */         }
/*     */         
/* 619 */         iCol++;
/* 620 */         if (iCol >= iNumCols) {
/* 621 */           iCol = 0;
/* 622 */           iRow++;
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 626 */       Logger.log(new LogEvent(LogIDs.GUI, "drawing piece map", e));
/*     */     } finally {
/* 628 */       gcImg.dispose();
/*     */     }
/*     */     
/* 631 */     this.peerInfoCanvas.redraw();
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 635 */     return this.peerInfoComposite;
/*     */   }
/*     */   
/*     */   private void delete() {
/* 639 */     if ((this.imageLabel != null) && (!this.imageLabel.isDisposed()) && (this.imageLabel.getImage() != null))
/*     */     {
/* 641 */       Image image = this.imageLabel.getImage();
/* 642 */       this.imageLabel.setImage(null);
/* 643 */       image.dispose();
/*     */     }
/*     */     
/* 646 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 647 */       this.img.dispose();
/* 648 */       this.img = null;
/*     */     }
/*     */     
/* 651 */     if ((this.font != null) && (!this.font.isDisposed())) {
/* 652 */       this.font.dispose();
/* 653 */       this.font = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private Image obfusticatedImage(Image image) {
/* 658 */     UIDebugGenerator.obfusticateArea(image, this.topLabel, "");
/* 659 */     return image;
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 663 */     switch (event.getType()) {
/*     */     case 0: 
/* 665 */       this.swtView = ((UISWTView)event.getData());
/* 666 */       this.swtView.setTitle(getFullTitle());
/* 667 */       break;
/*     */     
/*     */     case 7: 
/* 670 */       delete();
/* 671 */       break;
/*     */     
/*     */     case 2: 
/* 674 */       initialize((Composite)event.getData());
/* 675 */       break;
/*     */     
/*     */     case 6: 
/* 678 */       Messages.updateLanguageForControl(getComposite());
/* 679 */       this.swtView.setTitle(getFullTitle());
/* 680 */       break;
/*     */     
/*     */     case 1: 
/* 683 */       dataSourceChanged(event.getData());
/* 684 */       break;
/*     */     
/*     */     case 3: 
/* 687 */       refreshInfoCanvas();
/* 688 */       break;
/*     */     
/*     */     case 5: 
/* 691 */       refresh();
/* 692 */       break;
/*     */     
/*     */     case 9: 
/* 695 */       Object data = event.getData();
/* 696 */       if ((data instanceof Map)) {
/* 697 */         obfusticatedImage((Image)MapUtils.getMapObject((Map)data, "image", null, Image.class));
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/* 703 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/peer/PeerInfoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */