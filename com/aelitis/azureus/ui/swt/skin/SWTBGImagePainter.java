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
/*     */ public class SWTBGImagePainter
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
/*  71 */   private Image lastImage = null;
/*     */   
/*  73 */   boolean inEvent = false;
/*     */   
/*  75 */   Rectangle lastBounds = Utils.EMPTY_RECT;
/*     */   
/*  77 */   Point lastShellBGSize = new Point(0, 0);
/*     */   
/*     */   private final int tileMode;
/*     */   
/*     */   private final Control control;
/*     */   
/*     */   private boolean bDirty;
/*     */   
/*  85 */   private int fdWidth = -1;
/*     */   
/*  87 */   private int fdHeight = -1;
/*     */   
/*  89 */   private ImageLoader imageLoader = null;
/*     */   
/*     */   private SWTBGImagePainter(Control control, int tileMode) {
/*  92 */     this.control = control;
/*  93 */     this.shell = control.getShell();
/*  94 */     this.tileMode = tileMode;
/*  95 */     control.setData("BGPainter", this);
/*     */   }
/*     */   
/*     */   public SWTBGImagePainter(Control control, Image bgImage, int tileMode) {
/*  99 */     this(control, null, null, bgImage, tileMode);
/*     */   }
/*     */   
/*     */   public SWTBGImagePainter(Control control, Image bgImageLeft, Image bgImageRight, Image bgImage, int tileMode)
/*     */   {
/* 104 */     this(control, tileMode);
/* 105 */     setImages(bgImageLeft, bgImageRight, bgImage);
/*     */     
/* 107 */     if ((this.bDirty) && 
/* 108 */       (control.isVisible())) {
/* 109 */       buildBackground(control);
/*     */     }
/*     */     
/*     */ 
/* 113 */     if (!TEST_SWT_PAINTING) {
/* 114 */       control.addListener(11, this);
/* 115 */       control.addListener(9, this);
/* 116 */       control.getShell().addListener(22, this);
/*     */     }
/*     */     
/* 119 */     control.addListener(12, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public SWTBGImagePainter(Control control, ImageLoader imageLoader, String bgImageLeftId, String bgImageRightId, String bgImageId, int tileMode)
/*     */   {
/* 125 */     this(control, tileMode);
/* 126 */     setImage(imageLoader, bgImageLeftId, bgImageRightId, bgImageId);
/*     */     
/* 128 */     if ((this.bDirty) && 
/* 129 */       (control.isVisible())) {
/* 130 */       buildBackground(control);
/*     */     }
/*     */     
/*     */ 
/* 134 */     if (!TEST_SWT_PAINTING) {
/* 135 */       control.addListener(11, this);
/* 136 */       control.addListener(9, this);
/* 137 */       control.getShell().addListener(22, this);
/*     */     }
/*     */     
/* 140 */     control.addListener(12, this);
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 144 */     if ((this.control == null) || (this.control.isDisposed())) {
/* 145 */       return;
/*     */     }
/*     */     
/* 148 */     if (!TEST_SWT_PAINTING) {
/* 149 */       this.control.removeListener(11, this);
/* 150 */       this.control.removeListener(9, this);
/* 151 */       this.control.getShell().removeListener(22, this);
/*     */     }
/*     */     
/* 154 */     this.control.removeListener(12, this);
/* 155 */     this.control.setBackgroundImage(null);
/* 156 */     FormData formData = (FormData)this.control.getLayoutData();
/* 157 */     formData.width = -1;
/* 158 */     formData.height = -1;
/* 159 */     this.control.setData("BGPainter", null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setImage(Image bgImageLeft, Image bgImageRight, Image bgImage)
/*     */   {
/* 168 */     setImages(bgImageLeft, bgImageRight, bgImage);
/* 169 */     if (this.bDirty) {
/* 170 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 172 */           if (!SWTBGImagePainter.this.control.isVisible()) {
/* 173 */             return;
/*     */           }
/* 175 */           SWTBGImagePainter.this.buildBackground(SWTBGImagePainter.this.control);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void setImage(ImageLoader imageLoader, String idLeft, String idRight, String id)
/*     */   {
/* 183 */     setImages(imageLoader, idLeft, idRight, id);
/* 184 */     if (this.bDirty) {
/* 185 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 187 */           if (!SWTBGImagePainter.this.control.isVisible()) {
/* 188 */             return;
/*     */           }
/* 190 */           SWTBGImagePainter.this.buildBackground(SWTBGImagePainter.this.control);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean imagesEqual(Image image1, Image image2) {
/* 197 */     if (image1 == image2) {
/* 198 */       return true;
/*     */     }
/*     */     
/* 201 */     if ((!ImageLoader.isRealImage(image1)) && (!ImageLoader.isRealImage(image2))) {
/* 202 */       return true;
/*     */     }
/*     */     
/* 205 */     return false;
/*     */   }
/*     */   
/*     */   private void setImages(Image bgImageLeft, Image bgImageRight, Image bgImage) {
/* 209 */     if ((imagesEqual(this.imgSrc, bgImage)) && (imagesEqual(this.imgSrcLeft, bgImageLeft)) && (imagesEqual(this.imgSrcRight, bgImageRight)))
/*     */     {
/* 211 */       if (DEBUG) {
/* 212 */         System.out.println("same");
/*     */       }
/* 214 */       return;
/*     */     }
/*     */     
/* 217 */     this.imgSrcLeftID = null;
/* 218 */     this.imgSrcRightID = null;
/* 219 */     this.imgSrcID = null;
/*     */     
/*     */ 
/* 222 */     if (DEBUG) {
/* 223 */       System.out.println("SI " + bgImageLeft + ";" + bgImageRight + ";" + bgImage + ";" + this.control.getData("SkinObject") + "/" + this.control.isVisible() + this.control.getSize() + "\\" + Debug.getStackTrace(true, false));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 228 */     this.imgSrc = bgImage;
/* 229 */     if (this.imgSrc != null) {
/* 230 */       this.imgSrcBounds = this.imgSrc.getBounds();
/*     */     }
/* 232 */     this.lastShellBGSize = new Point(0, 0);
/* 233 */     if (ImageLoader.isRealImage(bgImageLeft)) {
/* 234 */       this.imgSrcLeft = bgImageLeft;
/* 235 */       this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/*     */     } else {
/* 237 */       this.imgSrcLeft = null;
/* 238 */       this.imgSrcLeftBounds = Utils.EMPTY_RECT;
/*     */     }
/* 240 */     if (ImageLoader.isRealImage(bgImageRight)) {
/* 241 */       this.imgSrcRight = bgImageRight;
/* 242 */       this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/*     */     } else {
/* 244 */       this.imgSrcRight = null;
/* 245 */       this.imgSrcRightBounds = Utils.EMPTY_RECT;
/*     */     }
/*     */     
/*     */ 
/* 249 */     if (TEST_SWT_PAINTING) {
/* 250 */       this.control.removeListener(11, this);
/* 251 */       this.control.removeListener(9, this);
/*     */       
/* 253 */       if ((this.imgSrcRight == null) && (this.imgSrcLeft == null) && (this.tileMode == 0))
/*     */       {
/* 255 */         this.control.setBackgroundImage(this.imgSrc);
/*     */       } else {
/* 257 */         this.control.addListener(11, this);
/* 258 */         this.control.addListener(9, this);
/* 259 */         this.bDirty = true;
/* 260 */         buildBackground(this.control);
/*     */       }
/*     */     } else {
/* 263 */       this.bDirty = true;
/*     */     }
/*     */     
/*     */ 
/* 267 */     if ((this.tileMode & 0x3) != 3) {
/* 268 */       int width = -1;
/* 269 */       int height = -1;
/*     */       
/* 271 */       if ((this.tileMode == 1) || (this.tileMode == 0)) {
/* 272 */         width = this.imgSrcBounds.width + this.imgSrcLeftBounds.width + this.imgSrcRightBounds.width;
/*     */       }
/*     */       
/* 275 */       if ((this.tileMode == 2) || (this.tileMode == 0)) {
/* 276 */         height = this.imgSrcBounds.height;
/*     */       }
/* 278 */       FormData fd = (FormData)this.control.getLayoutData();
/* 279 */       if (fd == null) {
/* 280 */         fd = new FormData();
/*     */       }
/*     */       
/* 283 */       if ((fd.width == this.fdWidth) || (fd.height == this.fdHeight))
/*     */       {
/* 285 */         if (fd.width == this.fdWidth) {
/* 286 */           this.fdWidth = (fd.width = width);
/*     */         }
/* 288 */         if (fd.height == this.fdHeight) {
/* 289 */           this.fdHeight = (fd.height = height);
/*     */         }
/* 291 */         this.control.setLayoutData(fd);
/* 292 */         if (this.control.isVisible()) {
/* 293 */           this.bDirty = true;
/* 294 */           this.control.getParent().layout(true, true);
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
/* 310 */     this.imageLoader = imageLoader;
/* 311 */     this.imgSrcLeftID = bgImageLeftId;
/* 312 */     this.imgSrcRightID = bgImageRightId;
/* 313 */     this.imgSrcID = bgImageId;
/*     */     
/* 315 */     this.imgSrcLeftBounds = Utils.EMPTY_RECT;
/* 316 */     this.imgSrcRightBounds = Utils.EMPTY_RECT;
/*     */     
/* 318 */     if (this.imgSrcID != null) {
/* 319 */       Image imgSrc = imageLoader.getImage(this.imgSrcID);
/* 320 */       this.imgSrcBounds = imgSrc.getBounds();
/* 321 */       imageLoader.releaseImage(this.imgSrcID);
/*     */     }
/* 323 */     Image imgSrcLeft = imageLoader.getImage(this.imgSrcLeftID);
/* 324 */     if (ImageLoader.isRealImage(imgSrcLeft)) {
/* 325 */       this.imgSrcLeftBounds = imgSrcLeft.getBounds();
/*     */     }
/* 327 */     imageLoader.releaseImage(this.imgSrcLeftID);
/*     */     
/* 329 */     Image imgSrcRight = imageLoader.getImage(this.imgSrcRightID);
/* 330 */     if (ImageLoader.isRealImage(imgSrcRight)) {
/* 331 */       this.imgSrcRightBounds = imgSrcRight.getBounds();
/*     */     }
/* 333 */     imageLoader.releaseImage(this.imgSrcRightID);
/*     */     
/* 335 */     if (TEST_SWT_PAINTING) {
/* 336 */       this.control.removeListener(11, this);
/* 337 */       this.control.removeListener(9, this);
/*     */       
/* 339 */       this.control.addListener(11, this);
/* 340 */       this.control.addListener(9, this);
/* 341 */       this.bDirty = true;
/* 342 */       buildBackground(this.control);
/*     */     } else {
/* 344 */       this.bDirty = true;
/*     */     }
/*     */     
/*     */ 
/* 348 */     if ((this.tileMode & 0x3) != 3) {
/* 349 */       int width = -1;
/* 350 */       int height = -1;
/*     */       
/* 352 */       if ((this.tileMode == 1) || (this.tileMode == 0)) {
/* 353 */         width = this.imgSrcBounds.width + this.imgSrcLeftBounds.width + this.imgSrcRightBounds.width;
/*     */       }
/*     */       
/* 356 */       if ((this.tileMode == 2) || (this.tileMode == 0)) {
/* 357 */         height = this.imgSrcBounds.height;
/*     */       }
/* 359 */       FormData fd = (FormData)this.control.getLayoutData();
/* 360 */       if (fd == null) {
/* 361 */         fd = new FormData();
/*     */       }
/*     */       
/* 364 */       if ((fd.width == this.fdWidth) || (fd.height == this.fdHeight))
/*     */       {
/* 366 */         if (fd.width == this.fdWidth) {
/* 367 */           this.fdWidth = (fd.width = width);
/*     */         }
/* 369 */         if (fd.height == this.fdHeight) {
/* 370 */           this.fdHeight = (fd.height = height);
/*     */         }
/* 372 */         this.control.setLayoutData(fd);
/* 373 */         if (this.control.isVisible()) {
/* 374 */           this.bDirty = true;
/* 375 */           this.control.getParent().layout(true, true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void buildBackground(Control control)
/*     */   {
/* 384 */     if ((this.inEvent) || (this.shell == null) || (this.shell.isDisposed()) || (control == null) || (control.isDisposed()))
/*     */     {
/* 386 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 391 */     this.inEvent = true;
/*     */     
/* 393 */     ArrayList<String> imagesToRelease = new ArrayList(0);
/*     */     
/* 395 */     if ((this.imgSrcLeftID != null) && (this.imageLoader.imageExists(this.imgSrcLeftID))) {
/* 396 */       imagesToRelease.add(this.imgSrcLeftID);
/* 397 */       this.imgSrcLeft = this.imageLoader.getImage(this.imgSrcLeftID);
/* 398 */       this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/*     */     }
/* 400 */     if ((this.imgSrcRightID != null) && (this.imageLoader.imageExists(this.imgSrcRightID))) {
/* 401 */       imagesToRelease.add(this.imgSrcRightID);
/* 402 */       this.imgSrcRight = this.imageLoader.getImage(this.imgSrcRightID);
/* 403 */       this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/*     */     }
/* 405 */     if (this.imgSrcID != null) {
/* 406 */       Image[] images = this.imageLoader.getImages(this.imgSrcID);
/* 407 */       imagesToRelease.add(this.imgSrcID);
/* 408 */       if (images.length == 1) {
/* 409 */         this.imgSrc = images[0];
/* 410 */         this.imgSrcBounds = this.imgSrc.getBounds();
/* 411 */       } else if (images.length == 2) {
/* 412 */         this.imgSrcLeft = images[0];
/* 413 */         this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/* 414 */         this.imgSrc = images[1];
/* 415 */         this.imgSrcBounds = this.imgSrc.getBounds();
/* 416 */         this.imgSrcRight = images[1];
/* 417 */         this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/* 418 */       } else if (images.length == 3) {
/* 419 */         this.imgSrcLeft = images[0];
/* 420 */         this.imgSrcLeftBounds = this.imgSrcLeft.getBounds();
/* 421 */         this.imgSrc = images[1];
/* 422 */         this.imgSrcBounds = this.imgSrc.getBounds();
/* 423 */         this.imgSrcRight = images[2];
/* 424 */         this.imgSrcRightBounds = this.imgSrcRight.getBounds();
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 430 */       Point size = control.getSize();
/* 431 */       Iterator i$; String key; if ((size.x <= 0) || (size.y <= 0) || (this.imgSrc == null) || (this.imgSrc.isDisposed())) {
/* 432 */         if (DEBUG) {
/* 433 */           System.out.println("- size " + control.getData("ConfigID"));
/*     */         }
/* 435 */         Image image = new Image(this.shell.getDisplay(), 1, 1);
/* 436 */         control.setBackgroundImage(image);
/*     */         
/* 438 */         if (this.lastImage != null) {
/* 439 */           this.lastImage.dispose();
/*     */         }
/*     */         
/* 442 */         this.lastImage = image;
/* 443 */         this.imgSrc = image;
/* 444 */         this.imgSrcBounds = new Rectangle(0, 0, 1, 1);
/*     */         
/* 446 */         this.lastBounds = control.getBounds();
/*     */         
/* 448 */         this.inEvent = false;
/*     */       }
/*     */       else
/*     */       {
/* 452 */         Composite parent = control.getParent();
/* 453 */         Image imgBG = parent.getBackgroundImage();
/*     */         
/* 455 */         if ((imgBG != null) && (imgBG.isDisposed())) {
/* 456 */           imgBG = null;
/*     */         }
/*     */         
/* 459 */         Rectangle imgBGBounds = imgBG == null ? new Rectangle(0, 0, 1, 1) : imgBG.getBounds();
/*     */         
/* 461 */         Rectangle compositeArea = control.getBounds();
/*     */         
/* 463 */         boolean bTileY = (this.tileMode & 0x1) > 0;
/* 464 */         boolean bTileX = (this.tileMode & 0x2) > 0;
/*     */         
/*     */         Iterator i$;
/*     */         
/*     */         String key;
/* 469 */         if ((!this.bDirty) && (imgBG == null) && (bTileX) && (bTileY)) {
/* 470 */           this.inEvent = false;
/*     */         } else {
/*     */           Iterator i$;
/*     */           String key;
/* 474 */           if ((!this.bDirty) && (imgBG == null) && (compositeArea.width == this.lastBounds.width) && (compositeArea.height == this.lastBounds.height))
/*     */           {
/* 476 */             this.inEvent = false;
/*     */           } else {
/*     */             Iterator i$;
/*     */             String key;
/* 480 */             if ((!this.bDirty) && (compositeArea.equals(this.lastBounds)) && (imgBGBounds.width == this.lastShellBGSize.x) && (imgBGBounds.height == this.lastShellBGSize.y))
/*     */             {
/*     */ 
/* 483 */               this.inEvent = false;
/*     */             } else {
/*     */               Iterator i$;
/*     */               String key;
/* 487 */               if ((TEST_SWT_PAINTING) && (!this.bDirty) && (compositeArea.width == this.lastBounds.width) && (compositeArea.height == this.lastBounds.height))
/*     */               {
/* 489 */                 this.inEvent = false;
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/* 494 */                 if (DEBUG) {
/* 495 */                   System.out.println(System.currentTimeMillis() + "@" + Integer.toHexString(hashCode()) + "BGPain: " + control.getData("SkinObject") + "/" + "; image" + size + ";" + this.tileMode + ";lB=" + this.lastBounds + "/" + compositeArea + ";" + "lBG=" + this.lastShellBGSize + "/" + imgBGBounds.width + "x" + imgBGBounds.height + ";" + this.bDirty);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 504 */                 this.lastBounds = compositeArea;
/* 505 */                 this.lastShellBGSize = new Point(imgBGBounds.width, imgBGBounds.height);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 510 */                 Image newImage = new Image(this.shell.getDisplay(), size.x, size.y);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */                 Point ofs;
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 520 */                 if (control.getParent() == this.shell) {
/* 521 */                   Point ofs = control.getLocation();
/* 522 */                   Rectangle clientArea = this.shell.getClientArea();
/* 523 */                   ofs.x += clientArea.x;
/* 524 */                   ofs.y += clientArea.y;
/*     */                 } else {
/* 526 */                   Point controlPos = new Point(0, 0);
/* 527 */                   if ((control instanceof Composite)) {
/* 528 */                     Composite composite = (Composite)control;
/* 529 */                     Rectangle compArea = composite.getClientArea();
/*     */                     
/* 531 */                     controlPos.x = compArea.x;
/* 532 */                     controlPos.y = compArea.y;
/*     */                   }
/*     */                   
/* 535 */                   Point locControl = control.toDisplay(controlPos.x, controlPos.y);
/* 536 */                   Rectangle clientArea = this.shell.getClientArea();
/* 537 */                   Point locShell = control.getParent().toDisplay(clientArea.x, clientArea.y);
/*     */                   
/*     */ 
/*     */ 
/* 541 */                   ofs = new Point(locControl.x - locShell.x, locControl.y - locShell.y);
/*     */                 }
/*     */                 
/* 544 */                 ofs.x %= imgBGBounds.width;
/* 545 */                 ofs.y %= imgBGBounds.height;
/*     */                 
/* 547 */                 GC gc = new GC(newImage);
/*     */                 try
/*     */                 {
/* 550 */                   control.setBackgroundImage(null);
/* 551 */                   gc.setBackground(control.getBackground());
/* 552 */                   gc.fillRectangle(0, 0, size.x, size.y);
/*     */                   
/* 554 */                   if (imgBG != null) {
/* 555 */                     for (int y = 0; y < size.y; y += imgBGBounds.height) {
/* 556 */                       for (int x = 0; x < size.x; x += imgBGBounds.width) {
/* 557 */                         gc.drawImage(imgBG, x - ofs.x, y - ofs.y);
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   
/* 562 */                   int maxY = bTileY ? size.y : this.imgSrcBounds.height;
/* 563 */                   int maxX = bTileX ? size.x : this.imgSrcBounds.width;
/* 564 */                   int x0 = 0;
/*     */                   
/* 566 */                   if ((this.tileMode & 0x4) > 0) {
/* 567 */                     x0 = (size.x - this.imgSrcBounds.width) / 2;
/* 568 */                     maxX += x0;
/*     */                   }
/* 570 */                   int y0 = 0;
/* 571 */                   if ((this.tileMode & 0x8) > 0) {
/* 572 */                     y0 = (size.y - this.imgSrcBounds.height) / 2;
/* 573 */                     maxY += y0;
/*     */                   }
/*     */                   
/* 576 */                   if (this.imgSrcRight != null) {
/* 577 */                     int width = this.imgSrcRightBounds.width;
/*     */                     
/* 579 */                     maxX -= width;
/*     */                   }
/*     */                   
/* 582 */                   if (this.imgSrcLeft != null)
/*     */                   {
/* 584 */                     gc.drawImage(this.imgSrcLeft, 0, 0);
/*     */                     
/* 586 */                     x0 += this.imgSrcLeftBounds.width;
/*     */                   }
/*     */                   
/* 589 */                   for (int y = y0; y < maxY; y += this.imgSrcBounds.height) {
/* 590 */                     for (int x = x0; x < maxX; x += this.imgSrcBounds.width) {
/* 591 */                       if (x + this.imgSrcBounds.width >= maxX) {
/* 592 */                         int width = maxX - x;
/* 593 */                         gc.drawImage(this.imgSrc, 0, 0, width, this.imgSrcBounds.height, x, y, width, this.imgSrcBounds.height);
/*     */                       }
/*     */                       else {
/* 596 */                         gc.drawImage(this.imgSrc, x, y);
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   
/* 601 */                   if (this.imgSrcRight != null)
/*     */                   {
/* 603 */                     gc.drawImage(this.imgSrcRight, maxX, 0);
/*     */                   }
/*     */                 } finally {
/* 606 */                   gc.dispose();
/*     */                 }
/*     */                 
/* 609 */                 control.setBackgroundImage(newImage);
/*     */                 
/* 611 */                 if (this.lastImage != null) {
/* 612 */                   this.lastImage.dispose();
/*     */                 }
/*     */                 
/* 615 */                 this.lastImage = newImage;
/*     */                 
/* 617 */                 this.bDirty = false;
/*     */               } } } } } } finally { Iterator i$;
/*     */       String key;
/* 620 */       for (String key : imagesToRelease) {
/* 621 */         this.imageLoader.releaseImage(key);
/*     */       }
/* 623 */       if ((this.imgSrcID != null) && (this.imgSrc != null)) {
/* 624 */         this.imgSrc = null;
/*     */       }
/* 626 */       if ((this.imgSrcLeftID != null) && (this.imgSrcLeft != null)) {
/* 627 */         this.imgSrcLeft = null;
/*     */       }
/* 629 */       if ((this.imgSrcRightID != null) && (this.imgSrcRight != null)) {
/* 630 */         this.imgSrcRight = null;
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
/* 646 */       this.inEvent = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 651 */     Display display = Display.getDefault();
/* 652 */     Shell shell = new Shell(display, 2144);
/* 653 */     shell.setLayout(new FillLayout());
/*     */     
/* 655 */     Composite c = new Composite(shell, 2048);
/* 656 */     c.setLayout(new FillLayout());
/* 657 */     c.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 659 */         e.gc.drawLine(0, 0, 100, 50);
/*     */       }
/*     */       
/* 662 */     });
/* 663 */     Label lbl = new Label(c, 0);
/* 664 */     lbl.setText("text");
/*     */     
/* 666 */     shell.open();
/*     */     
/* 668 */     while (!shell.isDisposed()) {
/* 669 */       if (display.readAndDispatch()) {
/* 670 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleEvent(Event event) {
/* 676 */     if (event.type == 11) {
/* 677 */       Control control = (Control)event.widget;
/*     */       
/* 679 */       Rectangle resizeRect = control.getBounds();
/* 680 */       if (resizeRect.equals(this.lastResizeRect)) {
/* 681 */         return;
/*     */       }
/*     */       
/* 684 */       this.lastResizeRect = resizeRect;
/*     */       
/* 686 */       if (DEBUG) {
/* 687 */         System.out.println("BGPaint:HE: " + control.getData("ConfigID") + ";" + event + ";" + control.isVisible());
/*     */       }
/*     */       
/* 690 */       buildBackground(control);
/* 691 */     } else if (event.type == 9) {
/* 692 */       Control control = (Control)event.widget;
/* 693 */       if (DEBUG) {
/* 694 */         System.out.println("BGPaint:P: " + control.getData("ConfigID") + ";" + event + ";" + control.isVisible());
/*     */       }
/*     */       
/*     */ 
/* 698 */       if (!TEST_SWT_PAINTING) {
/* 699 */         buildBackground(control);
/*     */       }
/* 701 */     } else if (event.type == 22) {
/* 702 */       if (DEBUG) {
/* 703 */         System.out.println("BGPaint:S: " + this.control.getData("ConfigID") + ";" + event + ";" + this.control.isVisible());
/*     */       }
/*     */       
/*     */ 
/* 707 */       if (!TEST_SWT_PAINTING) {
/* 708 */         buildBackground(this.control);
/*     */       }
/* 710 */     } else if (event.type == 12) {
/* 711 */       if (DEBUG) {
/* 712 */         System.out.println("dispose.. " + this.lastImage + ";" + this.control.getData("SkinObject"));
/*     */       }
/*     */       
/* 715 */       if ((this.lastImage != null) && (!this.lastImage.isDisposed())) {
/* 716 */         this.lastImage.dispose();
/* 717 */         this.lastImage = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTBGImagePainter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */