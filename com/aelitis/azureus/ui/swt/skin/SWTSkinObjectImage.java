/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader.ImageDownloaderListener;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.util.AECallback;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AERunnableWithCallback;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ public class SWTSkinObjectImage
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*  41 */   protected static final Long DRAW_SCALE = new Long(1L);
/*     */   
/*  43 */   protected static final Long DRAW_SCALEDOWN_OR_CENTER = new Long(8L);
/*     */   
/*  45 */   protected static final Long DRAW_STRETCH = new Long(2L);
/*     */   
/*  47 */   protected static final Long DRAW_NORMAL = new Long(0L);
/*     */   
/*  49 */   protected static final Long DRAW_LEFT = new Long(7L);
/*     */   
/*  51 */   protected static final Long DRAW_TILE = new Long(3L);
/*     */   
/*  53 */   protected static final Long DRAW_CENTER = new Long(4L);
/*     */   
/*  55 */   protected static final Long DRAW_HCENTER = new Long(5L);
/*     */   
/*  57 */   protected static final Long DRAW_ANIMATE = new Long(6L);
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
/*  71 */   private int drawAlpha = 255;
/*     */   
/*     */ 
/*  74 */   private static PaintListener paintListener = new PaintListener()
/*     */   {
/*     */     public void paintControl(PaintEvent e) {
/*     */       try {
/*  78 */         e.gc.setAdvanced(true);
/*  79 */         e.gc.setInterpolation(2);
/*     */       }
/*     */       catch (Exception ex) {}
/*     */       
/*  83 */       Canvas control = (Canvas)e.widget;
/*  84 */       Image imgSrc = (Image)control.getData("image");
/*     */       
/*  86 */       Integer drawAlpha = (Integer)control.getData("drawAlpha");
/*  87 */       if (drawAlpha != null) {
/*  88 */         e.gc.setAlpha(drawAlpha.intValue());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  94 */       Long drawMode = (Long)control.getData("drawmode");
/*  95 */       if (drawMode == SWTSkinObjectImage.DRAW_ANIMATE) {
/*  96 */         Image[] images = (Image[])control.getData("images");
/*  97 */         if (images != null) {
/*  98 */           int idx = ((Number)control.getData("ImageIndex")).intValue();
/*  99 */           imgSrc = images[idx];
/*     */         }
/*     */       }
/*     */       
/* 103 */       Image imgRight = null;
/* 104 */       Image imgLeft = null;
/* 105 */       String idToRelease = null;
/* 106 */       ImageLoader imageLoader = null;
/*     */       
/* 108 */       if ((imgSrc == null) || (imgSrc.isDisposed())) {
/* 109 */         SWTSkinObjectImage soImage = (SWTSkinObjectImage)control.getData("SkinObject");
/* 110 */         imageLoader = soImage.getSkin().getImageLoader(soImage.getProperties());
/*     */         
/* 112 */         String imageID = (String)control.getData("ImageID");
/* 113 */         if (imageLoader.imageExists(imageID)) {
/* 114 */           idToRelease = imageID;
/* 115 */           Image[] images = imageLoader.getImages(imageID);
/* 116 */           if (images.length == 3) {
/* 117 */             imgLeft = images[0];
/* 118 */             imgSrc = images[1];
/* 119 */             imgRight = images[2];
/*     */           } else {
/* 121 */             imgSrc = images[0];
/*     */           }
/*     */         } else {
/* 124 */           return;
/*     */         }
/*     */       }
/* 127 */       Rectangle imgSrcBounds = imgSrc.getBounds();
/* 128 */       Point size = control.getSize();
/*     */       
/* 130 */       if (drawMode == SWTSkinObjectImage.DRAW_SCALEDOWN_OR_CENTER) {
/* 131 */         if ((size.x < imgSrcBounds.width) || (size.y < imgSrcBounds.height)) {
/* 132 */           drawMode = SWTSkinObjectImage.DRAW_SCALE;
/*     */         } else {
/* 134 */           drawMode = SWTSkinObjectImage.DRAW_CENTER;
/*     */         }
/*     */       }
/*     */       
/* 138 */       if (drawMode == SWTSkinObjectImage.DRAW_STRETCH) {
/* 139 */         e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, 0, 0, size.x, size.y);
/*     */       }
/* 141 */       else if (drawMode == SWTSkinObjectImage.DRAW_LEFT) {
/* 142 */         e.gc.drawImage(imgSrc, 0, 0);
/* 143 */       } else if ((drawMode == SWTSkinObjectImage.DRAW_NORMAL) || (drawMode == SWTSkinObjectImage.DRAW_CENTER) || (drawMode == SWTSkinObjectImage.DRAW_ANIMATE))
/*     */       {
/* 145 */         if ((control.getStyle() & 0x20000) != 0) {
/* 146 */           e.gc.drawImage(imgSrc, size.x - imgSrcBounds.width, (size.y - imgSrcBounds.height) / 2);
/*     */         }
/*     */         else {
/* 149 */           e.gc.drawImage(imgSrc, (size.x - imgSrcBounds.width) / 2, (size.y - imgSrcBounds.height) / 2);
/*     */         }
/*     */       }
/* 152 */       else if (drawMode == SWTSkinObjectImage.DRAW_HCENTER) {
/* 153 */         e.gc.drawImage(imgSrc, (size.x - imgSrcBounds.width) / 2, 0);
/* 154 */       } else if (drawMode == SWTSkinObjectImage.DRAW_SCALE) {
/* 155 */         float dx = size.x / imgSrcBounds.width;
/* 156 */         float dy = size.y / imgSrcBounds.height;
/* 157 */         float d = Math.min(dx, dy);
/* 158 */         int newX = (int)(imgSrcBounds.width * d);
/* 159 */         int newY = (int)(imgSrcBounds.height * d);
/*     */         
/* 161 */         e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, (size.x - newX) / 2, (size.y - newY) / 2, newX, newY);
/*     */       }
/*     */       else {
/* 164 */         int x0 = 0;
/* 165 */         int y0 = 0;
/* 166 */         int x1 = size.x;
/* 167 */         int y1 = size.y;
/*     */         
/* 169 */         if (imgRight == null) {
/* 170 */           imgRight = (Image)control.getData("image-right");
/*     */         }
/* 172 */         if (imgRight != null) {
/* 173 */           int width = imgRight.getBounds().width;
/*     */           
/* 175 */           x1 -= width;
/*     */         }
/*     */         
/* 178 */         if (imgLeft == null) {
/* 179 */           imgLeft = (Image)control.getData("image-left");
/*     */         }
/* 181 */         if (imgLeft != null)
/*     */         {
/* 183 */           e.gc.drawImage(imgLeft, 0, 0);
/*     */           
/* 185 */           x0 += imgLeft.getBounds().width;
/*     */         }
/*     */         
/* 188 */         for (int y = y0; y < y1; y += imgSrcBounds.height) {
/* 189 */           for (int x = x0; x < x1; x += imgSrcBounds.width) {
/* 190 */             e.gc.drawImage(imgSrc, x, y);
/*     */           }
/*     */         }
/*     */         
/* 194 */         if (imgRight != null)
/*     */         {
/* 196 */           e.gc.drawImage(imgRight, x1, 0);
/*     */         }
/*     */       }
/* 199 */       if ((idToRelease != null) && (imageLoader != null)) {
/* 200 */         imageLoader.releaseImage(idToRelease);
/*     */       }
/*     */     }
/*     */   };
/*     */   private Canvas canvas;
/*     */   private boolean customImage;
/*     */   private String customImageID;
/*     */   private String currentImageID;
/*     */   private int h_align;
/*     */   
/*     */   public SWTSkinObjectImage(SWTSkin skin, SWTSkinProperties skinProperties, String sID, String sConfigID, SWTSkinObject parent)
/*     */   {
/* 212 */     super(skin, skinProperties, sID, sConfigID, "image", parent);
/* 213 */     this.customImage = false;
/* 214 */     this.customImageID = null;
/* 215 */     setControl(createImageWidget(sConfigID));
/*     */   }
/*     */   
/*     */   private Canvas createImageWidget(String sConfigID) {
/* 219 */     String propImageID = this.properties.getStringValue(sConfigID + ".imageid");
/* 220 */     if (propImageID != null) {
/* 221 */       this.currentImageID = (this.customImageID = propImageID);
/*     */     } else {
/* 223 */       this.currentImageID = sConfigID;
/*     */     }
/* 225 */     int style = 536870976;
/*     */     
/* 227 */     String sAlign = this.properties.getStringValue(sConfigID + ".align");
/* 228 */     if ((sAlign != null) && (!Constants.isUnix)) {
/* 229 */       this.h_align = SWTSkinUtils.getAlignment(sAlign, 0);
/* 230 */       if (this.h_align != 0) {
/* 231 */         style |= this.h_align;
/*     */       }
/*     */     }
/*     */     
/* 235 */     if (this.properties.getIntValue(sConfigID + ".border", 0) == 1) {
/* 236 */       style |= 0x800;
/*     */     }
/*     */     Composite createOn;
/*     */     Composite createOn;
/* 240 */     if (this.parent == null) {
/* 241 */       createOn = this.skin.getShell();
/*     */     } else {
/* 243 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/* 246 */     this.canvas = new Canvas(createOn, style);
/* 247 */     this.canvas.setData("SkinObject", this);
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
/* 271 */     Color color = this.properties.getColor(sConfigID + ".color");
/* 272 */     if (color != null) {
/* 273 */       this.canvas.setBackground(color);
/*     */     }
/*     */     
/* 276 */     final String sURL = this.properties.getStringValue(sConfigID + ".url");
/* 277 */     if ((sURL != null) && (sURL.length() > 0)) {
/* 278 */       this.canvas.setToolTipText(sURL);
/* 279 */       this.canvas.addListener(4, new Listener() {
/*     */         public void handleEvent(Event arg0) {
/* 281 */           Utils.launch(UrlUtils.encode(sURL));
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 286 */     String sCursor = this.properties.getStringValue(sConfigID + ".cursor");
/* 287 */     if ((sCursor != null) && (sCursor.length() > 0) && 
/* 288 */       (sCursor.equalsIgnoreCase("hand"))) {
/* 289 */       this.canvas.addListener(6, this.skin.getHandCursorListener(this.canvas.getDisplay()));
/*     */       
/* 291 */       this.canvas.addListener(7, this.skin.getHandCursorListener(this.canvas.getDisplay()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 300 */     this.canvas.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 302 */         String oldImageID = (String)SWTSkinObjectImage.this.canvas.getData("ImageID");
/* 303 */         if ((oldImageID != null) && (SWTSkinObjectImage.this.canvas.getData("image") != null)) {
/* 304 */           ImageLoader imageLoader = SWTSkinObjectImage.this.skin.getImageLoader(SWTSkinObjectImage.this.properties);
/* 305 */           imageLoader.releaseImage(oldImageID);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 310 */     });
/* 311 */     swt_reallySetImage();
/*     */     
/* 313 */     return this.canvas;
/*     */   }
/*     */   
/*     */   public void setVisible(boolean visible) {
/* 317 */     super.setVisible(visible);
/*     */     
/* 319 */     if (visible) {
/* 320 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 322 */           SWTSkinObjectImage.this.swt_reallySetImage();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   protected void setCanvasImage(String sImageID, AECallback callback)
/*     */   {
/* 330 */     setCanvasImage(this.sConfigID, sImageID, callback);
/*     */   }
/*     */   
/*     */ 
/*     */   private void setCanvasImage(final String sConfigID, final String sImageID, AECallback callback)
/*     */   {
/* 336 */     Utils.execSWTThread(new AERunnableWithCallback(callback)
/*     */     {
/*     */       public Object runSupport() {
/* 339 */         if ((SWTSkinObjectImage.this.canvas == null) || (SWTSkinObjectImage.this.canvas.isDisposed())) {
/* 340 */           return null;
/*     */         }
/*     */         
/* 343 */         SWTSkinObjectImage.this.canvas.setData("drawAlpha", Integer.valueOf(SWTSkinObjectImage.this.drawAlpha));
/*     */         
/* 345 */         String oldImageID = (String)SWTSkinObjectImage.this.canvas.getData("ImageID");
/* 346 */         if ((sImageID != null) && (sImageID.equals(oldImageID))) {
/* 347 */           return null;
/*     */         }
/*     */         
/* 350 */         ImageLoader imageLoader = SWTSkinObjectImage.this.skin.getImageLoader(SWTSkinObjectImage.this.properties);
/*     */         
/* 352 */         if ((oldImageID != null) && (SWTSkinObjectImage.this.canvas.getData("image") != null)) {
/* 353 */           imageLoader.releaseImage(oldImageID);
/*     */         }
/*     */         
/* 356 */         int hpadding = SWTSkinObjectImage.this.properties.getIntValue(sConfigID + ".h-padding", 0);
/* 357 */         SWTSkinObjectImage.this.canvas.setData("hpadding", new Long(hpadding));
/*     */         
/*     */ 
/* 360 */         Image[] images = (sImageID == null) || (sImageID.length() == 0) ? null : imageLoader.getImages(sImageID);
/*     */         
/*     */ 
/* 363 */         String sDrawMode = SWTSkinObjectImage.this.properties.getStringValue(sConfigID + ".drawmode");
/* 364 */         if (sDrawMode == null) {
/* 365 */           sDrawMode = SWTSkinObjectImage.this.properties.getStringValue(SWTSkinObjectImage.this.sConfigID + ".drawmode", "");
/*     */         }
/*     */         
/*     */         Long drawMode;
/*     */         Long drawMode;
/* 370 */         if (sDrawMode.equals("scale")) {
/* 371 */           drawMode = SWTSkinObjectImage.DRAW_SCALE; } else { Long drawMode;
/* 372 */           if (sDrawMode.equals("scaledown")) {
/* 373 */             drawMode = SWTSkinObjectImage.DRAW_SCALEDOWN_OR_CENTER; } else { Long drawMode;
/* 374 */             if (sDrawMode.equals("stretch")) {
/* 375 */               drawMode = SWTSkinObjectImage.DRAW_STRETCH; } else { Long drawMode;
/* 376 */               if (sDrawMode.equals("center")) {
/* 377 */                 drawMode = SWTSkinObjectImage.DRAW_CENTER; } else { Long drawMode;
/* 378 */                 if (sDrawMode.equals("h-center")) {
/* 379 */                   drawMode = SWTSkinObjectImage.DRAW_HCENTER; } else { Long drawMode;
/* 380 */                   if (sDrawMode.equalsIgnoreCase("tile")) {
/* 381 */                     drawMode = SWTSkinObjectImage.DRAW_TILE; } else { Long drawMode;
/* 382 */                     if ((sDrawMode.equalsIgnoreCase("animate")) || ((sDrawMode.length() == 0) && (images != null) && (images.length > 3)))
/*     */                     {
/* 384 */                       drawMode = SWTSkinObjectImage.DRAW_ANIMATE; } else { Long drawMode;
/* 385 */                       if (sDrawMode.equalsIgnoreCase("left")) {
/* 386 */                         drawMode = SWTSkinObjectImage.DRAW_LEFT;
/*     */                       } else
/* 388 */                         drawMode = SWTSkinObjectImage.DRAW_NORMAL;
/*     */                     } } } } } } }
/* 390 */         SWTSkinObjectImage.this.canvas.setData("drawmode", drawMode);
/*     */         
/* 392 */         Image image = null;
/*     */         
/* 394 */         boolean hasExistingDelay = SWTSkinObjectImage.this.canvas.getData("delay") != null;
/* 395 */         SWTSkinObjectImage.this.canvas.setData("delay", null);
/* 396 */         if (images == null) {
/* 397 */           SWTSkinObjectImage.this.canvas.setData("images", null);
/* 398 */           image = null;
/* 399 */         } else if (drawMode == SWTSkinObjectImage.DRAW_ANIMATE) {
/* 400 */           int animationDelay = ImageLoader.getInstance().getAnimationDelay(sImageID);
/*     */           
/* 402 */           SWTSkinObjectImage.this.canvas.setData("images", images);
/* 403 */           SWTSkinObjectImage.this.canvas.setData("ImageIndex", Long.valueOf(0L));
/* 404 */           SWTSkinObjectImage.this.canvas.setData("delay", new Long(animationDelay));
/* 405 */           image = images[0];
/*     */           
/* 407 */           if (!hasExistingDelay) {
/* 408 */             SWTSkinObjectImage.this.setupAnimationTrigger(animationDelay);
/*     */           }
/* 410 */         } else if (images.length == 3) {
/* 411 */           Image imageLeft = images[0];
/* 412 */           if (ImageLoader.isRealImage(imageLeft)) {
/* 413 */             SWTSkinObjectImage.this.canvas.setData("image-left", imageLeft);
/*     */           }
/*     */           
/* 416 */           image = images[1];
/*     */           
/* 418 */           Image imageRight = images[2];
/* 419 */           if (ImageLoader.isRealImage(imageRight)) {
/* 420 */             SWTSkinObjectImage.this.canvas.setData("image-right", imageRight);
/*     */           }
/* 422 */         } else if (images.length > 0) {
/* 423 */           image = images[0];
/*     */         }
/*     */         
/* 426 */         if ((image == null) || (image.isDisposed())) {
/* 427 */           image = ImageLoader.noImage;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 433 */         Rectangle imgBounds = image.getBounds();
/* 434 */         if ((drawMode != SWTSkinObjectImage.DRAW_CENTER) && (drawMode != SWTSkinObjectImage.DRAW_HCENTER) && (drawMode != SWTSkinObjectImage.DRAW_STRETCH) && (drawMode != SWTSkinObjectImage.DRAW_SCALEDOWN_OR_CENTER))
/*     */         {
/* 436 */           SWTSkinObjectImage.this.canvas.setSize(imgBounds.width + hpadding, imgBounds.height);
/* 437 */           SWTSkinObjectImage.this.canvas.setData("oldSize", SWTSkinObjectImage.this.canvas.getSize());
/*     */         }
/*     */         
/*     */ 
/* 441 */         if ((drawMode == SWTSkinObjectImage.DRAW_TILE) || (drawMode == SWTSkinObjectImage.DRAW_NORMAL) || (drawMode == SWTSkinObjectImage.DRAW_LEFT) || (drawMode == SWTSkinObjectImage.DRAW_ANIMATE))
/*     */         {
/*     */ 
/* 444 */           FormData fd = (FormData)SWTSkinObjectImage.this.canvas.getLayoutData();
/* 445 */           if (fd == null) {
/* 446 */             fd = new FormData(imgBounds.width + hpadding, imgBounds.height);
/*     */           } else {
/* 448 */             fd.width = (imgBounds.width + hpadding);
/* 449 */             fd.height = imgBounds.height;
/*     */           }
/* 451 */           SWTSkinObjectImage.this.canvas.setData("oldSize", new Point(fd.width, fd.height));
/* 452 */           SWTSkinObjectImage.this.canvas.setLayoutData(fd);
/* 453 */           Utils.relayout(SWTSkinObjectImage.this.canvas);
/*     */         }
/*     */         
/*     */ 
/* 457 */         SWTSkinObjectImage.this.canvas.removePaintListener(SWTSkinObjectImage.paintListener);
/*     */         
/* 459 */         SWTSkinObjectImage.this.canvas.addPaintListener(SWTSkinObjectImage.paintListener);
/* 460 */         SWTSkinObjectImage.this.canvas.setData("ImageID", sImageID);
/*     */         
/* 462 */         SWTSkinObjectImage.this.canvas.redraw();
/*     */         
/* 464 */         SWTSkinUtils.addMouseImageChangeListeners(SWTSkinObjectImage.this.canvas);
/* 465 */         if (drawMode != SWTSkinObjectImage.DRAW_ANIMATE) {
/* 466 */           imageLoader.releaseImage(sImageID);
/*     */         }
/* 468 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void setupAnimationTrigger(int animationDelay) {
/* 474 */     Utils.execSWTThreadLater(animationDelay, new AERunnable() {
/*     */       public void runSupport() {
/* 476 */         if (!SWTSkinObjectImage.this.control.isDisposed()) {
/* 477 */           Object data = SWTSkinObjectImage.this.control.getData("delay");
/* 478 */           if (data == null) {
/* 479 */             return;
/*     */           }
/*     */           
/* 482 */           Image[] images = (Image[])SWTSkinObjectImage.this.control.getData("images");
/* 483 */           int idx = ((Number)SWTSkinObjectImage.this.control.getData("ImageIndex")).intValue();
/* 484 */           idx++;
/* 485 */           if (idx >= images.length) {
/* 486 */             idx = 0;
/*     */           }
/* 488 */           SWTSkinObjectImage.this.control.setData("ImageIndex", new Long(idx));
/* 489 */           SWTSkinObjectImage.this.control.redraw();
/*     */           
/* 491 */           int delay = ((Number)SWTSkinObjectImage.this.control.getData("delay")).intValue();
/* 492 */           SWTSkinObjectImage.this.setupAnimationTrigger(delay);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBackground(String sConfigID, String sSuffix) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/* 506 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/* 507 */     if (this.customImage) {
/* 508 */       return suffix;
/*     */     }
/* 510 */     if (suffix == null) {
/* 511 */       return null;
/*     */     }
/*     */     
/* 514 */     final String fSuffix = suffix;
/*     */     
/* 516 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 519 */         SWTSkinObjectImage.this.currentImageID = ((SWTSkinObjectImage.this.customImageID == null ? SWTSkinObjectImage.this.sConfigID + ".image" : SWTSkinObjectImage.this.customImageID) + fSuffix);
/*     */         
/*     */ 
/* 522 */         if (SWTSkinObjectImage.this.isVisible()) {
/* 523 */           SWTSkinObjectImage.this.swt_reallySetImage();
/*     */         }
/*     */         
/*     */       }
/* 527 */     });
/* 528 */     return suffix;
/*     */   }
/*     */   
/*     */   protected void swt_reallySetImage() {
/* 532 */     if ((this.currentImageID == null) || (this.customImage)) {
/* 533 */       this.drawAlpha = 255;
/* 534 */       return;
/*     */     }
/*     */     
/* 537 */     boolean removedDisabled = false;
/* 538 */     ImageLoader imageLoader = this.skin.getImageLoader(this.properties);
/* 539 */     boolean imageExists = imageLoader.imageExists(this.currentImageID);
/* 540 */     if ((!imageExists) && (imageLoader.imageExists(this.currentImageID + ".image"))) {
/* 541 */       this.currentImageID = (this.sConfigID + ".image");
/* 542 */       imageExists = true;
/*     */     }
/* 544 */     if ((!imageExists) && (this.suffixes != null)) {
/* 545 */       for (int i = this.suffixes.length - 1; i >= 0; i--) {
/* 546 */         String suffixToRemove = this.suffixes[i];
/* 547 */         if (suffixToRemove != null) {
/* 548 */           if (suffixToRemove.equals("-disabled")) {
/* 549 */             removedDisabled = true;
/*     */           }
/* 551 */           this.currentImageID = this.currentImageID.substring(0, this.currentImageID.length() - suffixToRemove.length());
/*     */           
/* 553 */           if (imageLoader.imageExists(this.currentImageID)) {
/* 554 */             imageExists = true;
/* 555 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 561 */     if (imageExists) {
/* 562 */       this.drawAlpha = (removedDisabled ? 64 : 255);
/* 563 */       setCanvasImage(this.currentImageID, null);
/*     */     } else {
/* 565 */       this.drawAlpha = 255;
/* 566 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 568 */           FormData fd = (FormData)SWTSkinObjectImage.this.canvas.getLayoutData();
/* 569 */           if (fd == null) {
/* 570 */             fd = new FormData(0, 0);
/*     */           } else {
/* 572 */             fd.width = 0;
/* 573 */             fd.height = 0;
/*     */           }
/* 575 */           SWTSkinObjectImage.this.canvas.setLayoutData(fd);
/* 576 */           if (SWTSkinObjectImage.this.initialized) {
/* 577 */             Utils.relayout(SWTSkinObjectImage.this.canvas);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void setImage(final Image image) {
/* 585 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 587 */         SWTSkinObjectImage.this.customImage = true;
/* 588 */         SWTSkinObjectImage.this.customImageID = null;
/* 589 */         SWTSkinObjectImage.this.drawAlpha = 255;
/* 590 */         SWTSkinObjectImage.this.canvas.setData("image", image);
/* 591 */         SWTSkinObjectImage.this.canvas.setData("ImageID", null);
/* 592 */         SWTSkinObjectImage.this.canvas.setData("image-left", null);
/* 593 */         SWTSkinObjectImage.this.canvas.setData("image-right", null);
/* 594 */         SWTSkinObjectImage.this.canvas.setData("drawAlpha", null);
/*     */         
/* 596 */         SWTSkinObjectImage.this.canvas.removePaintListener(SWTSkinObjectImage.paintListener);
/* 597 */         SWTSkinObjectImage.this.canvas.addPaintListener(SWTSkinObjectImage.paintListener);
/*     */         
/* 599 */         Utils.relayout(SWTSkinObjectImage.this.canvas);
/* 600 */         SWTSkinObjectImage.this.canvas.redraw();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setImageByID(final String imageID, final AECallback callback) {
/* 606 */     if ((!this.customImage) && (this.customImageID != null) && (this.customImageID.equals(imageID)))
/*     */     {
/* 608 */       if (callback != null) {
/* 609 */         callback.callbackFailure(null);
/*     */       }
/* 611 */       return;
/*     */     }
/* 613 */     this.customImage = false;
/* 614 */     this.customImageID = imageID;
/*     */     
/* 616 */     if (imageID == null) {
/* 617 */       setCanvasImage(this.sConfigID, null, null);
/* 618 */       return;
/*     */     }
/*     */     
/* 621 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 623 */         String fullImageID = imageID + SWTSkinObjectImage.this.getSuffix();
/* 624 */         ImageLoader imageLoader = SWTSkinObjectImage.this.skin.getImageLoader(SWTSkinObjectImage.this.properties);
/* 625 */         Image image = imageLoader.getImage(fullImageID);
/* 626 */         if (ImageLoader.isRealImage(image)) {
/* 627 */           SWTSkinObjectImage.this.setCanvasImage(SWTSkinObjectImage.this.sConfigID, fullImageID, callback);
/*     */         } else {
/* 629 */           SWTSkinObjectImage.this.setCanvasImage(SWTSkinObjectImage.this.sConfigID, imageID, callback);
/*     */         }
/* 631 */         imageLoader.releaseImage(fullImageID);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setImageUrl(final String url) {
/* 637 */     if ((!this.customImage) && (this.customImageID != null) && (this.customImageID.equals(url)))
/*     */     {
/* 639 */       return;
/*     */     }
/* 641 */     this.customImage = false;
/* 642 */     this.customImageID = url;
/*     */     
/* 644 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 646 */         final ImageLoader imageLoader = SWTSkinObjectImage.this.skin.getImageLoader(SWTSkinObjectImage.this.properties);
/* 647 */         imageLoader.getUrlImage(url, new ImageLoader.ImageDownloaderListener() {
/*     */           public void imageDownloaded(Image image, boolean returnedImmediately) {
/* 649 */             SWTSkinObjectImage.this.setCanvasImage(SWTSkinObjectImage.11.this.val$url, null);
/* 650 */             imageLoader.releaseImage(SWTSkinObjectImage.11.this.val$url);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */