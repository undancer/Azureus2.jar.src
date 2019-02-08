/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class SWTBGImagePainter2
/*     */   implements Listener
/*     */ {
/*  45 */   private static boolean DEBUG = false;
/*     */   
/*  47 */   private static boolean TEST_SWT_PAINTING = false;
/*     */   
/*  49 */   private Rectangle lastResizeRect = Utils.EMPTY_RECT;
/*     */   
/*     */   private final Shell shell;
/*     */   
/*     */   private String imgSrcID;
/*     */   
/*     */   private String imgSrcLeftID;
/*     */   
/*     */   private String imgSrcRightID;
/*     */   
/*     */   private Image imgSrc;
/*     */   
/*     */   private Image imgSrcLeft;
/*     */   
/*     */   private Image imgSrcRight;
/*     */   
/*     */   private Rectangle imgSrcBounds;
/*     */   
/*     */   private Rectangle imgSrcLeftBounds;
/*     */   
/*     */   private Rectangle imgSrcRightBounds;
/*     */   
/*     */   private Rectangle imgSrcBoundsAdj;
/*     */   
/*     */   private Rectangle imgSrcLeftBoundsAdj;
/*     */   
/*     */   private Rectangle imgSrcRightBoundsAdj;
/*     */   
/*  77 */   private Image lastImage = null;
/*     */   
/*  79 */   boolean inEvent = false;
/*     */   
/*  81 */   Rectangle lastBounds = Utils.EMPTY_RECT;
/*     */   
/*  83 */   Point lastShellBGSize = new Point(0, 0);
/*     */   
/*     */   private final int tileMode;
/*     */   
/*     */   private final Control control;
/*     */   
/*     */   private boolean bDirty;
/*     */   
/*  91 */   private int fdWidth = -1;
/*     */   
/*  93 */   private int fdHeight = -1;
/*     */   
/*  95 */   private ImageLoader imageLoader = null;
/*     */   
/*     */   private SWTBGImagePainter2(Control control, int tileMode) {
/*  98 */     this.control = control;
/*  99 */     this.shell = control.getShell();
/* 100 */     this.tileMode = tileMode;
/* 101 */     control.setData("BGPainter", this);
/*     */   }
/*     */   
/*     */   public SWTBGImagePainter2(Control control, Image bgImage, int tileMode) {
/* 105 */     this(control, null, null, bgImage, tileMode);
/*     */   }
/*     */   
/*     */   public SWTBGImagePainter2(Control control, Image bgImageLeft, Image bgImageRight, Image bgImage, int tileMode)
/*     */   {
/* 110 */     this(control, tileMode);
/* 111 */     setImages(bgImageLeft, bgImageRight, bgImage);
/*     */     
/* 113 */     if ((this.bDirty) && 
/* 114 */       (control.isVisible())) {
/* 115 */       buildBackground(control);
/*     */     }
/*     */     
/*     */ 
/* 119 */     if (!TEST_SWT_PAINTING) {
/* 120 */       control.addListener(11, this);
/* 121 */       control.addListener(9, this);
/* 122 */       control.getShell().addListener(22, this);
/*     */     }
/*     */     
/* 125 */     control.addListener(12, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public SWTBGImagePainter2(Control control, ImageLoader imageLoader, String bgImageLeftId, String bgImageRightId, String bgImageId, int tileMode)
/*     */   {
/* 131 */     this(control, tileMode);
/* 132 */     setImage(imageLoader, bgImageLeftId, bgImageRightId, bgImageId);
/*     */     
/* 134 */     if ((this.bDirty) && 
/* 135 */       (control.isVisible())) {
/* 136 */       buildBackground(control);
/*     */     }
/*     */     
/*     */ 
/* 140 */     if (!TEST_SWT_PAINTING) {
/* 141 */       control.addListener(11, this);
/* 142 */       control.addListener(9, this);
/* 143 */       control.getShell().addListener(22, this);
/*     */     }
/*     */     
/* 146 */     control.addListener(12, this);
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 150 */     if ((this.control == null) || (this.control.isDisposed())) {
/* 151 */       return;
/*     */     }
/*     */     
/* 154 */     if (!TEST_SWT_PAINTING) {
/* 155 */       this.control.removeListener(11, this);
/* 156 */       this.control.removeListener(9, this);
/* 157 */       this.control.getShell().removeListener(22, this);
/*     */     }
/*     */     
/* 160 */     this.control.removeListener(12, this);
/* 161 */     this.control.setBackgroundImage(null);
/* 162 */     FormData formData = (FormData)this.control.getLayoutData();
/* 163 */     formData.width = -1;
/* 164 */     formData.height = -1;
/* 165 */     this.control.setData("BGPainter", null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setImage(Image bgImageLeft, Image bgImageRight, Image bgImage)
/*     */   {
/* 174 */     setImages(bgImageLeft, bgImageRight, bgImage);
/* 175 */     if (this.bDirty) {
/* 176 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 178 */           if (!SWTBGImagePainter2.this.control.isVisible()) {
/* 179 */             return;
/*     */           }
/* 181 */           SWTBGImagePainter2.this.buildBackground(SWTBGImagePainter2.this.control);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void setImage(ImageLoader imageLoader, String idLeft, String idRight, String id)
/*     */   {
/* 189 */     setImages(imageLoader, idLeft, idRight, id);
/* 190 */     if (this.bDirty) {
/* 191 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 193 */           if (!SWTBGImagePainter2.this.control.isVisible()) {
/* 194 */             return;
/*     */           }
/* 196 */           SWTBGImagePainter2.this.buildBackground(SWTBGImagePainter2.this.control);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean imagesEqual(Image image1, Image image2) {
/* 203 */     if (image1 == image2) {
/* 204 */       return true;
/*     */     }
/*     */     
/* 207 */     if ((!ImageLoader.isRealImage(image1)) && (!ImageLoader.isRealImage(image2))) {
/* 208 */       return true;
/*     */     }
/*     */     
/* 211 */     return false;
/*     */   }
/*     */   
/*     */   private void setImages(Image bgImageLeft, Image bgImageRight, Image bgImage) {
/* 215 */     if ((imagesEqual(this.imgSrc, bgImage)) && (imagesEqual(this.imgSrcLeft, bgImageLeft)) && (imagesEqual(this.imgSrcRight, bgImageRight)))
/*     */     {
/* 217 */       if (DEBUG) {
/* 218 */         System.out.println("same");
/*     */       }
/* 220 */       return;
/*     */     }
/*     */     
/* 223 */     this.imgSrcLeftID = null;
/* 224 */     this.imgSrcRightID = null;
/* 225 */     this.imgSrcID = null;
/*     */     
/*     */ 
/* 228 */     if (DEBUG) {
/* 229 */       System.out.println("SI " + bgImageLeft + ";" + bgImageRight + ";" + bgImage + ";" + this.control.getData("SkinObject") + "/" + this.control.isVisible() + this.control.getSize() + "\\" + Debug.getStackTrace(true, false));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 234 */     this.imgSrc = bgImage;
/* 235 */     if (this.imgSrc != null) {
/* 236 */       this.imgSrcBounds = this.imgSrc.getBounds();
/* 237 */       this.imgSrcBoundsAdj = Utils.adjustPXForDPI(this.imgSrcBounds);
/*     */     }
/* 239 */     this.lastShellBGSize = new Point(0, 0);
/* 240 */     if (ImageLoader.isRealImage(bgImageLeft)) {
/* 241 */       this.imgSrcLeft = bgImageLeft;
/* 242 */       this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/* 243 */       this.imgSrcLeftBoundsAdj = Utils.adjustPXForDPI(this.imgSrcLeftBounds);
/*     */     } else {
/* 245 */       this.imgSrcLeft = null;
/* 246 */       this.imgSrcLeftBounds = Utils.EMPTY_RECT;
/* 247 */       this.imgSrcLeftBoundsAdj = Utils.EMPTY_RECT;
/*     */     }
/* 249 */     if (ImageLoader.isRealImage(bgImageRight)) {
/* 250 */       this.imgSrcRight = bgImageRight;
/* 251 */       this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/* 252 */       this.imgSrcRightBoundsAdj = Utils.adjustPXForDPI(this.imgSrcRightBounds);
/*     */     } else {
/* 254 */       this.imgSrcRight = null;
/* 255 */       this.imgSrcRightBounds = Utils.EMPTY_RECT;
/* 256 */       this.imgSrcRightBoundsAdj = Utils.EMPTY_RECT;
/*     */     }
/*     */     
/*     */ 
/* 260 */     if (TEST_SWT_PAINTING) {
/* 261 */       this.control.removeListener(11, this);
/* 262 */       this.control.removeListener(9, this);
/*     */       
/* 264 */       if ((this.imgSrcRight == null) && (this.imgSrcLeft == null) && (this.tileMode == 0))
/*     */       {
/* 266 */         this.control.setBackgroundImage(this.imgSrc);
/*     */       } else {
/* 268 */         this.control.addListener(11, this);
/* 269 */         this.control.addListener(9, this);
/* 270 */         this.bDirty = true;
/* 271 */         buildBackground(this.control);
/*     */       }
/*     */     } else {
/* 274 */       this.bDirty = true;
/*     */     }
/*     */     
/*     */ 
/* 278 */     if ((this.tileMode & 0x3) != 3) {
/* 279 */       int width = -1;
/* 280 */       int height = -1;
/*     */       
/* 282 */       if ((this.tileMode == 1) || (this.tileMode == 0)) {
/* 283 */         width = this.imgSrcBoundsAdj.width + this.imgSrcLeftBoundsAdj.width + this.imgSrcRightBoundsAdj.width;
/*     */       }
/*     */       
/* 286 */       if ((this.tileMode == 2) || (this.tileMode == 0)) {
/* 287 */         height = this.imgSrcBoundsAdj.height;
/*     */       }
/* 289 */       FormData fd = (FormData)this.control.getLayoutData();
/* 290 */       if (fd == null) {
/* 291 */         fd = new FormData();
/*     */       }
/*     */       
/* 294 */       if ((fd.width == this.fdWidth) || (fd.height == this.fdHeight))
/*     */       {
/* 296 */         if (fd.width == this.fdWidth) {
/* 297 */           this.fdWidth = (fd.width = width);
/*     */         }
/* 299 */         if (fd.height == this.fdHeight) {
/* 300 */           this.fdHeight = (fd.height = height);
/*     */         }
/* 302 */         this.control.setLayoutData(fd);
/* 303 */         if (this.control.isVisible()) {
/* 304 */           this.bDirty = true;
/* 305 */           this.control.getParent().layout(true, true);
/*     */         }
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
/*     */ 
/*     */   public void setImages(ImageLoader imageLoader, String bgImageLeftId, String bgImageRightId, String bgImageId)
/*     */   {
/* 321 */     this.imageLoader = imageLoader;
/* 322 */     this.imgSrcLeftID = bgImageLeftId;
/* 323 */     this.imgSrcRightID = bgImageRightId;
/* 324 */     this.imgSrcID = bgImageId;
/*     */     
/* 326 */     this.imgSrcLeftBounds = Utils.EMPTY_RECT;
/* 327 */     this.imgSrcRightBounds = Utils.EMPTY_RECT;
/* 328 */     this.imgSrcLeftBoundsAdj = Utils.EMPTY_RECT;
/* 329 */     this.imgSrcRightBoundsAdj = Utils.EMPTY_RECT;
/*     */     
/* 331 */     if (this.imgSrcID != null) {
/* 332 */       Image imgSrc = imageLoader.getImage(this.imgSrcID);
/* 333 */       this.imgSrcBounds = imgSrc.getBounds();
/* 334 */       this.imgSrcBoundsAdj = Utils.adjustPXForDPI(this.imgSrcBounds);
/* 335 */       imageLoader.releaseImage(this.imgSrcID);
/*     */     }
/* 337 */     Image imgSrcLeft = imageLoader.getImage(this.imgSrcLeftID);
/* 338 */     if (ImageLoader.isRealImage(imgSrcLeft)) {
/* 339 */       this.imgSrcLeftBounds = imgSrcLeft.getBounds();
/* 340 */       this.imgSrcLeftBoundsAdj = Utils.adjustPXForDPI(this.imgSrcLeftBounds);
/*     */     }
/* 342 */     imageLoader.releaseImage(this.imgSrcLeftID);
/*     */     
/* 344 */     Image imgSrcRight = imageLoader.getImage(this.imgSrcRightID);
/* 345 */     if (ImageLoader.isRealImage(imgSrcRight)) {
/* 346 */       this.imgSrcRightBounds = imgSrcRight.getBounds();
/* 347 */       this.imgSrcRightBoundsAdj = Utils.adjustPXForDPI(this.imgSrcRightBounds);
/*     */     }
/* 349 */     imageLoader.releaseImage(this.imgSrcRightID);
/*     */     
/* 351 */     if (TEST_SWT_PAINTING) {
/* 352 */       this.control.removeListener(11, this);
/* 353 */       this.control.removeListener(9, this);
/*     */       
/* 355 */       this.control.addListener(11, this);
/* 356 */       this.control.addListener(9, this);
/* 357 */       this.bDirty = true;
/* 358 */       buildBackground(this.control);
/*     */     } else {
/* 360 */       this.bDirty = true;
/*     */     }
/*     */     
/*     */ 
/* 364 */     if ((this.tileMode & 0x3) != 3) {
/* 365 */       int width = -1;
/* 366 */       int height = -1;
/*     */       
/* 368 */       if ((this.tileMode == 1) || (this.tileMode == 0)) {
/* 369 */         width = this.imgSrcBoundsAdj.width + this.imgSrcLeftBoundsAdj.width + this.imgSrcRightBoundsAdj.width;
/*     */       }
/*     */       
/* 372 */       if ((this.tileMode == 2) || (this.tileMode == 0)) {
/* 373 */         height = this.imgSrcBoundsAdj.height;
/*     */       }
/* 375 */       FormData fd = (FormData)this.control.getLayoutData();
/* 376 */       if (fd == null) {
/* 377 */         fd = new FormData();
/*     */       }
/*     */       
/* 380 */       if ((fd.width == this.fdWidth) || (fd.height == this.fdHeight))
/*     */       {
/* 382 */         if (fd.width == this.fdWidth) {
/* 383 */           this.fdWidth = (fd.width = width);
/*     */         }
/* 385 */         if (fd.height == this.fdHeight) {
/* 386 */           this.fdHeight = (fd.height = height);
/*     */         }
/* 388 */         this.control.setLayoutData(fd);
/* 389 */         if (this.control.isVisible()) {
/* 390 */           this.bDirty = true;
/* 391 */           this.control.getParent().layout(true, true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void buildBackground(Control control)
/*     */   {
/* 400 */     if ((this.inEvent) || (this.shell == null) || (this.shell.isDisposed()) || (control == null) || (control.isDisposed()))
/*     */     {
/* 402 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 407 */     this.inEvent = true;
/*     */     
/* 409 */     ArrayList<String> imagesToRelease = new ArrayList(0);
/*     */     
/* 411 */     if ((this.imgSrcLeftID != null) && (this.imageLoader.imageExists(this.imgSrcLeftID))) {
/* 412 */       imagesToRelease.add(this.imgSrcLeftID);
/* 413 */       this.imgSrcLeft = this.imageLoader.getImage(this.imgSrcLeftID);
/* 414 */       this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/*     */     }
/* 416 */     if ((this.imgSrcRightID != null) && (this.imageLoader.imageExists(this.imgSrcRightID))) {
/* 417 */       imagesToRelease.add(this.imgSrcRightID);
/* 418 */       this.imgSrcRight = this.imageLoader.getImage(this.imgSrcRightID);
/* 419 */       this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/*     */     }
/* 421 */     if (this.imgSrcID != null) {
/* 422 */       Image[] images = this.imageLoader.getImages(this.imgSrcID);
/* 423 */       imagesToRelease.add(this.imgSrcID);
/* 424 */       if (images.length == 1) {
/* 425 */         this.imgSrc = images[0];
/* 426 */         this.imgSrcBounds = this.imgSrc.getBounds();
/* 427 */         this.imgSrcBoundsAdj = Utils.adjustPXForDPI(this.imgSrcBounds);
/* 428 */       } else if (images.length == 2) {
/* 429 */         this.imgSrcLeft = images[0];
/* 430 */         this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/* 431 */         this.imgSrcLeftBoundsAdj = Utils.adjustPXForDPI(this.imgSrcLeftBounds);
/* 432 */         this.imgSrc = images[1];
/* 433 */         this.imgSrcBounds = this.imgSrc.getBounds();
/* 434 */         this.imgSrcBoundsAdj = Utils.adjustPXForDPI(this.imgSrcBounds);
/* 435 */         this.imgSrcRight = images[1];
/* 436 */         this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/* 437 */         this.imgSrcRightBoundsAdj = Utils.adjustPXForDPI(this.imgSrcRightBounds);
/* 438 */       } else if (images.length == 3) {
/* 439 */         this.imgSrcLeft = images[0];
/* 440 */         this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/* 441 */         this.imgSrc = images[1];
/* 442 */         this.imgSrcBoundsAdj = Utils.adjustPXForDPI(this.imgSrcBounds);
/* 443 */         this.imgSrcBounds = this.imgSrc.getBounds();
/* 444 */         this.imgSrcRight = images[2];
/* 445 */         this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/* 446 */         this.imgSrcRightBoundsAdj = Utils.adjustPXForDPI(this.imgSrcRightBounds);
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 452 */       Point size = control.getSize();
/* 453 */       Iterator i$; String key; if ((size.x <= 0) || (size.y <= 0) || (this.imgSrc == null) || (this.imgSrc.isDisposed())) {
/* 454 */         if (DEBUG) {
/* 455 */           System.out.println("- size " + control.getData("ConfigID"));
/*     */         }
/* 457 */         Image image = new Image(this.shell.getDisplay(), 1, 1);
/* 458 */         control.setBackgroundImage(image);
/*     */         
/* 460 */         if (this.lastImage != null) {
/* 461 */           this.lastImage.dispose();
/*     */         }
/*     */         
/* 464 */         this.lastImage = image;
/* 465 */         this.imgSrc = image;
/* 466 */         this.imgSrcBounds = new Rectangle(0, 0, 1, 1);
/*     */         
/* 468 */         this.lastBounds = control.getBounds();
/*     */         
/* 470 */         this.inEvent = false;
/*     */       }
/*     */       else
/*     */       {
/* 474 */         Composite parent = control.getParent();
/* 475 */         Image imgBG = parent.getBackgroundImage();
/*     */         
/* 477 */         if ((imgBG != null) && (imgBG.isDisposed())) {
/* 478 */           imgBG = null;
/*     */         }
/*     */         
/* 481 */         Rectangle imgBGBounds = imgBG == null ? new Rectangle(0, 0, 1, 1) : imgBG.getBounds();
/*     */         
/* 483 */         Rectangle compositeArea = control.getBounds();
/*     */         
/* 485 */         boolean bTileY = (this.tileMode & 0x1) > 0;
/* 486 */         boolean bTileX = (this.tileMode & 0x2) > 0;
/*     */         
/*     */         Iterator i$;
/*     */         
/*     */         String key;
/* 491 */         if ((!this.bDirty) && (imgBG == null) && (bTileX) && (bTileY)) {
/* 492 */           this.inEvent = false;
/*     */         } else {
/*     */           Iterator i$;
/*     */           String key;
/* 496 */           if ((!this.bDirty) && (imgBG == null) && (compositeArea.width == this.lastBounds.width) && (compositeArea.height == this.lastBounds.height))
/*     */           {
/* 498 */             this.inEvent = false;
/*     */           } else {
/*     */             Iterator i$;
/*     */             String key;
/* 502 */             if ((!this.bDirty) && (compositeArea.equals(this.lastBounds)) && (imgBGBounds.width == this.lastShellBGSize.x) && (imgBGBounds.height == this.lastShellBGSize.y))
/*     */             {
/*     */ 
/* 505 */               this.inEvent = false;
/*     */             } else {
/*     */               Iterator i$;
/*     */               String key;
/* 509 */               if ((TEST_SWT_PAINTING) && (!this.bDirty) && (compositeArea.width == this.lastBounds.width) && (compositeArea.height == this.lastBounds.height))
/*     */               {
/* 511 */                 this.inEvent = false;
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/*     */ 
/* 517 */                 System.out.println(System.currentTimeMillis() + "@" + Integer.toHexString(hashCode()) + "BGPain: " + control.getData("SkinObject") + "/" + "; image" + size + ";" + this.tileMode + ";lB=" + this.lastBounds + "/" + compositeArea + ";" + "lBG=" + this.lastShellBGSize + "/" + imgBGBounds.width + "x" + imgBGBounds.height + ";" + this.bDirty);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 526 */                 this.lastBounds = compositeArea;
/* 527 */                 this.lastShellBGSize = new Point(imgBGBounds.width, imgBGBounds.height);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 532 */                 Image newImage = new Image(this.shell.getDisplay(), size.x, size.y);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */                 Point ofs;
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 542 */                 if (control.getParent() == this.shell) {
/* 543 */                   Point ofs = control.getLocation();
/* 544 */                   Rectangle clientArea = this.shell.getClientArea();
/* 545 */                   ofs.x += clientArea.x;
/* 546 */                   ofs.y += clientArea.y;
/*     */                 } else {
/* 548 */                   Point controlPos = new Point(0, 0);
/* 549 */                   if ((control instanceof Composite)) {
/* 550 */                     Composite composite = (Composite)control;
/* 551 */                     Rectangle compArea = composite.getClientArea();
/*     */                     
/* 553 */                     controlPos.x = compArea.x;
/* 554 */                     controlPos.y = compArea.y;
/*     */                   }
/*     */                   
/* 557 */                   Point locControl = control.toDisplay(controlPos.x, controlPos.y);
/* 558 */                   Rectangle clientArea = this.shell.getClientArea();
/* 559 */                   Point locShell = control.getParent().toDisplay(clientArea.x, clientArea.y);
/*     */                   
/*     */ 
/*     */ 
/* 563 */                   ofs = new Point(locControl.x - locShell.x, locControl.y - locShell.y);
/*     */                 }
/*     */                 
/* 566 */                 ofs.x %= imgBGBounds.width;
/* 567 */                 ofs.y %= imgBGBounds.height;
/*     */                 
/* 569 */                 GC gc = new GC(newImage);
/*     */                 try
/*     */                 {
/* 572 */                   control.setBackgroundImage(null);
/* 573 */                   gc.setBackground(control.getBackground());
/* 574 */                   gc.fillRectangle(0, 0, size.x, size.y);
/*     */                   
/* 576 */                   if (imgBG != null) {
/* 577 */                     for (int y = 0; y < size.y; y += imgBGBounds.height) {
/* 578 */                       for (int x = 0; x < size.x; x += imgBGBounds.width) {
/* 579 */                         gc.drawImage(imgBG, x - ofs.x, y - ofs.y);
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   
/* 584 */                   int maxY = bTileY ? size.y : this.imgSrcBoundsAdj.height;
/* 585 */                   int maxX = bTileX ? size.x : this.imgSrcBoundsAdj.width;
/* 586 */                   int x0 = 0;
/*     */                   
/* 588 */                   if ((this.tileMode & 0x4) > 0) {
/* 589 */                     x0 = (size.x - this.imgSrcBoundsAdj.width) / 2;
/* 590 */                     maxX += x0;
/*     */                   }
/* 592 */                   int y0 = 0;
/* 593 */                   if ((this.tileMode & 0x8) > 0) {
/* 594 */                     y0 = (size.y - this.imgSrcBoundsAdj.height) / 2;
/* 595 */                     maxY += y0;
/*     */                   }
/*     */                   
/* 598 */                   if (this.imgSrcRight != null) {
/* 599 */                     int width = this.imgSrcRightBoundsAdj.width;
/*     */                     
/* 601 */                     maxX -= width;
/*     */                   }
/*     */                   
/* 604 */                   if (this.imgSrcLeft != null)
/*     */                   {
/* 606 */                     gc.drawImage(this.imgSrcLeft, 0, 0, this.imgSrcLeftBounds.width, this.imgSrcLeftBounds.height, 0, 0, this.imgSrcLeftBoundsAdj.width, this.imgSrcLeftBoundsAdj.height);
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/* 611 */                     x0 += this.imgSrcLeftBoundsAdj.width;
/*     */                   }
/*     */                   
/* 614 */                   for (int y = y0; y < maxY; y += this.imgSrcBoundsAdj.height) {
/* 615 */                     for (int x = x0; x < maxX; x += this.imgSrcBoundsAdj.width) {
/* 616 */                       if (x + this.imgSrcBoundsAdj.width >= maxX) {
/* 617 */                         int width = maxX - x;
/* 618 */                         if (width > 0) {
/*     */                           try {
/* 620 */                             gc.drawImage(this.imgSrc, 0, 0, this.imgSrcBounds.width, this.imgSrcBounds.height, x, y, width, this.imgSrcBoundsAdj.height);
/*     */                           }
/*     */                           catch (Exception e) {}
/*     */                         }
/*     */                       }
/*     */                       else
/*     */                       {
/* 627 */                         gc.drawImage(this.imgSrc, 0, 0, this.imgSrcBounds.width, this.imgSrcBounds.height, x, y, this.imgSrcBoundsAdj.width, this.imgSrcBoundsAdj.height);
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/* 634 */                   if (this.imgSrcRight != null)
/*     */                   {
/* 636 */                     gc.drawImage(this.imgSrcRight, 0, 0, this.imgSrcRightBounds.width, this.imgSrcRightBounds.height, maxX, 0, this.imgSrcRightBoundsAdj.width, this.imgSrcRightBoundsAdj.height);
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 finally
/*     */                 {
/* 642 */                   gc.dispose();
/*     */                 }
/*     */                 
/* 645 */                 control.setBackgroundImage(newImage);
/*     */                 
/* 647 */                 if (this.lastImage != null) {
/* 648 */                   this.lastImage.dispose();
/*     */                 }
/*     */                 
/* 651 */                 this.lastImage = newImage;
/*     */                 
/* 653 */                 this.bDirty = false;
/*     */               } } } } } } finally { Iterator i$;
/*     */       String key;
/* 656 */       for (String key : imagesToRelease) {
/* 657 */         this.imageLoader.releaseImage(key);
/*     */       }
/* 659 */       if ((this.imgSrcID != null) && (this.imgSrc != null)) {
/* 660 */         this.imgSrc = null;
/*     */       }
/* 662 */       if ((this.imgSrcLeftID != null) && (this.imgSrcLeft != null)) {
/* 663 */         this.imgSrcLeft = null;
/*     */       }
/* 665 */       if ((this.imgSrcRightID != null) && (this.imgSrcRight != null)) {
/* 666 */         this.imgSrcRight = null;
/*     */       }
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
/* 682 */       this.inEvent = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 687 */     Display display = Display.getDefault();
/* 688 */     Shell shell = new Shell(display, 2144);
/* 689 */     shell.setLayout(new FillLayout());
/*     */     
/* 691 */     Composite c = new Composite(shell, 2048);
/* 692 */     c.setLayout(new FillLayout());
/* 693 */     c.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 695 */         e.gc.drawLine(0, 0, 100, 50);
/*     */       }
/*     */       
/* 698 */     });
/* 699 */     Label lbl = new Label(c, 0);
/* 700 */     lbl.setText("text");
/*     */     
/* 702 */     shell.open();
/*     */     
/* 704 */     while (!shell.isDisposed()) {
/* 705 */       if (display.readAndDispatch()) {
/* 706 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleEvent(Event event) {
/* 712 */     if (event.type == 11) {
/* 713 */       Control control = (Control)event.widget;
/*     */       
/* 715 */       Rectangle resizeRect = control.getBounds();
/* 716 */       if (resizeRect.equals(this.lastResizeRect)) {
/* 717 */         return;
/*     */       }
/*     */       
/* 720 */       this.lastResizeRect = resizeRect;
/*     */       
/* 722 */       if (DEBUG) {
/* 723 */         System.out.println("BGPaint:HE: " + control.getData("ConfigID") + ";" + event + ";" + control.isVisible());
/*     */       }
/*     */       
/* 726 */       buildBackground(control);
/* 727 */     } else if (event.type == 9) {
/* 728 */       Control control = (Control)event.widget;
/* 729 */       if (DEBUG) {
/* 730 */         System.out.println("BGPaint:P: " + control.getData("ConfigID") + ";" + event + ";" + control.isVisible());
/*     */       }
/*     */       
/*     */ 
/* 734 */       if (!TEST_SWT_PAINTING) {
/* 735 */         buildBackground(control);
/*     */       }
/* 737 */     } else if (event.type == 22) {
/* 738 */       if (DEBUG) {
/* 739 */         System.out.println("BGPaint:S: " + this.control.getData("ConfigID") + ";" + event + ";" + this.control.isVisible());
/*     */       }
/*     */       
/*     */ 
/* 743 */       if (!TEST_SWT_PAINTING) {
/* 744 */         buildBackground(this.control);
/*     */       }
/* 746 */     } else if (event.type == 12) {
/* 747 */       if (DEBUG) {
/* 748 */         System.out.println("dispose.. " + this.lastImage + ";" + this.control.getData("SkinObject"));
/*     */       }
/*     */       
/* 751 */       if ((this.lastImage != null) && (!this.lastImage.isDisposed())) {
/* 752 */         this.lastImage.dispose();
/* 753 */         this.lastImage = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTBGImagePainter2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */