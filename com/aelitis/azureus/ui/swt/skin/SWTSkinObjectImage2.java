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
/*     */ public class SWTSkinObjectImage2
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
/*  95 */       if (drawMode == SWTSkinObjectImage2.DRAW_ANIMATE) {
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
/* 109 */         SWTSkinObjectImage2 soImage = (SWTSkinObjectImage2)control.getData("SkinObject");
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
/* 128 */       Rectangle imgSrcBoundsAdj = Utils.adjustPXForDPI(imgSrcBounds);
/* 129 */       Point size = control.getSize();
/*     */       
/* 131 */       if (drawMode == SWTSkinObjectImage2.DRAW_SCALEDOWN_OR_CENTER) {
/* 132 */         if ((size.x < imgSrcBounds.width) || (size.y < imgSrcBounds.height)) {
/* 133 */           drawMode = SWTSkinObjectImage2.DRAW_SCALE;
/*     */         } else {
/* 135 */           drawMode = SWTSkinObjectImage2.DRAW_CENTER;
/*     */         }
/*     */       }
/*     */       
/* 139 */       if (drawMode == SWTSkinObjectImage2.DRAW_STRETCH) {
/* 140 */         e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, 0, 0, size.x, size.y);
/*     */       }
/* 142 */       else if (drawMode == SWTSkinObjectImage2.DRAW_LEFT) {
/* 143 */         e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, 0, 0, imgSrcBoundsAdj.width, imgSrcBoundsAdj.height);
/*     */ 
/*     */       }
/* 146 */       else if ((drawMode == SWTSkinObjectImage2.DRAW_NORMAL) || (drawMode == SWTSkinObjectImage2.DRAW_CENTER) || (drawMode == SWTSkinObjectImage2.DRAW_ANIMATE)) {
/*     */         int y;
/*     */         int x;
/*     */         int y;
/* 150 */         if ((control.getStyle() & 0x20000) != 0) {
/* 151 */           int x = size.x - imgSrcBoundsAdj.width;
/* 152 */           y = (size.y - imgSrcBoundsAdj.height) / 2;
/*     */         } else {
/* 154 */           x = (size.x - imgSrcBoundsAdj.width) / 2;
/* 155 */           y = (size.y - imgSrcBoundsAdj.height) / 2;
/*     */         }
/* 157 */         e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, x, y, imgSrcBoundsAdj.width, imgSrcBoundsAdj.height);
/*     */ 
/*     */       }
/* 160 */       else if (drawMode == SWTSkinObjectImage2.DRAW_HCENTER) {
/* 161 */         int x = (size.x - imgSrcBounds.width) / 2;
/* 162 */         int y = 0;
/* 163 */         e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, x, y, imgSrcBoundsAdj.width, imgSrcBoundsAdj.height);
/*     */ 
/*     */       }
/* 166 */       else if (drawMode == SWTSkinObjectImage2.DRAW_SCALE) {
/* 167 */         float dx = size.x / imgSrcBounds.width;
/* 168 */         float dy = size.y / imgSrcBounds.height;
/* 169 */         float d = Math.min(dx, dy);
/* 170 */         int newX = (int)(imgSrcBounds.width * d);
/* 171 */         int newY = (int)(imgSrcBounds.height * d);
/*     */         
/* 173 */         e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, (size.x - newX) / 2, (size.y - newY) / 2, newX, newY);
/*     */       }
/*     */       else {
/* 176 */         int x0 = 0;
/* 177 */         int y0 = 0;
/* 178 */         int x1 = size.x;
/* 179 */         int y1 = size.y;
/*     */         
/* 181 */         if (imgRight == null) {
/* 182 */           imgRight = (Image)control.getData("image-right");
/*     */         }
/* 184 */         if (imgRight != null) {
/* 185 */           int width = Utils.adjustPXForDPI(imgRight.getBounds().width);
/*     */           
/* 187 */           x1 -= width;
/*     */         }
/*     */         
/* 190 */         if (imgLeft == null) {
/* 191 */           imgLeft = (Image)control.getData("image-left");
/*     */         }
/* 193 */         if (imgLeft != null)
/*     */         {
/* 195 */           e.gc.drawImage(imgLeft, 0, 0);
/*     */           
/* 197 */           x0 += Utils.adjustPXForDPI(imgLeft.getBounds().width);
/*     */         }
/*     */         
/* 200 */         for (int y = y0; y < y1; y += imgSrcBoundsAdj.height) {
/* 201 */           for (int x = x0; x < x1; x += imgSrcBoundsAdj.width)
/*     */           {
/* 203 */             e.gc.drawImage(imgSrc, 0, 0, imgSrcBounds.width, imgSrcBounds.height, x, y, imgSrcBoundsAdj.width, imgSrcBoundsAdj.height);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 208 */         if (imgRight != null)
/*     */         {
/* 210 */           Rectangle imgRightBounds = imgRight.getBounds();
/* 211 */           Rectangle imgRightBoundsAdj = Utils.adjustPXForDPI(imgRightBounds);
/*     */           
/* 213 */           e.gc.drawImage(imgRight, 0, 0, imgRightBounds.width, imgRightBounds.height, x1, 0, imgRightBoundsAdj.width, imgRightBoundsAdj.height);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 218 */       if ((idToRelease != null) && (imageLoader != null)) {
/* 219 */         imageLoader.releaseImage(idToRelease);
/*     */       }
/*     */     }
/*     */   };
/*     */   private Canvas canvas;
/*     */   private boolean customImage;
/*     */   private String customImageID;
/*     */   private String currentImageID;
/*     */   private int h_align;
/*     */   
/*     */   public SWTSkinObjectImage2(SWTSkin skin, SWTSkinProperties skinProperties, String sID, String sConfigID, SWTSkinObject parent)
/*     */   {
/* 231 */     super(skin, skinProperties, sID, sConfigID, "image", parent);
/* 232 */     this.customImage = false;
/* 233 */     this.customImageID = null;
/* 234 */     setControl(createImageWidget(sConfigID));
/*     */   }
/*     */   
/*     */   private Canvas createImageWidget(String sConfigID) {
/* 238 */     String propImageID = this.properties.getStringValue(sConfigID + ".imageid");
/* 239 */     if (propImageID != null) {
/* 240 */       this.currentImageID = (this.customImageID = propImageID);
/*     */     } else {
/* 242 */       this.currentImageID = sConfigID;
/*     */     }
/* 244 */     int style = 536870976;
/*     */     
/* 246 */     String sAlign = this.properties.getStringValue(sConfigID + ".align");
/* 247 */     if ((sAlign != null) && (!Constants.isUnix)) {
/* 248 */       this.h_align = SWTSkinUtils.getAlignment(sAlign, 0);
/* 249 */       if (this.h_align != 0) {
/* 250 */         style |= this.h_align;
/*     */       }
/*     */     }
/*     */     
/* 254 */     if (this.properties.getIntValue(sConfigID + ".border", 0) == 1) {
/* 255 */       style |= 0x800;
/*     */     }
/*     */     Composite createOn;
/*     */     Composite createOn;
/* 259 */     if (this.parent == null) {
/* 260 */       createOn = this.skin.getShell();
/*     */     } else {
/* 262 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/* 265 */     this.canvas = new Canvas(createOn, style);
/* 266 */     this.canvas.setData("SkinObject", this);
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
/* 290 */     Color color = this.properties.getColor(sConfigID + ".color");
/* 291 */     if (color != null) {
/* 292 */       this.canvas.setBackground(color);
/*     */     }
/*     */     
/* 295 */     final String sURL = this.properties.getStringValue(sConfigID + ".url");
/* 296 */     if ((sURL != null) && (sURL.length() > 0)) {
/* 297 */       this.canvas.setToolTipText(sURL);
/* 298 */       this.canvas.addListener(4, new Listener() {
/*     */         public void handleEvent(Event arg0) {
/* 300 */           Utils.launch(UrlUtils.encode(sURL));
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 305 */     String sCursor = this.properties.getStringValue(sConfigID + ".cursor");
/* 306 */     if ((sCursor != null) && (sCursor.length() > 0) && 
/* 307 */       (sCursor.equalsIgnoreCase("hand"))) {
/* 308 */       this.canvas.addListener(6, this.skin.getHandCursorListener(this.canvas.getDisplay()));
/*     */       
/* 310 */       this.canvas.addListener(7, this.skin.getHandCursorListener(this.canvas.getDisplay()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 319 */     this.canvas.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 321 */         String oldImageID = (String)SWTSkinObjectImage2.this.canvas.getData("ImageID");
/* 322 */         if ((oldImageID != null) && (SWTSkinObjectImage2.this.canvas.getData("image") != null)) {
/* 323 */           ImageLoader imageLoader = SWTSkinObjectImage2.this.skin.getImageLoader(SWTSkinObjectImage2.this.properties);
/* 324 */           imageLoader.releaseImage(oldImageID);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 329 */     });
/* 330 */     swt_reallySetImage();
/*     */     
/* 332 */     return this.canvas;
/*     */   }
/*     */   
/*     */   public void setVisible(boolean visible) {
/* 336 */     super.setVisible(visible);
/*     */     
/* 338 */     if (visible) {
/* 339 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 341 */           SWTSkinObjectImage2.this.swt_reallySetImage();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   protected void setCanvasImage(String sImageID, AECallback callback)
/*     */   {
/* 349 */     setCanvasImage(this.sConfigID, sImageID, callback);
/*     */   }
/*     */   
/*     */ 
/*     */   private void setCanvasImage(final String sConfigID, final String sImageID, AECallback callback)
/*     */   {
/* 355 */     Utils.execSWTThread(new AERunnableWithCallback(callback)
/*     */     {
/*     */       public Object runSupport() {
/* 358 */         if ((SWTSkinObjectImage2.this.canvas == null) || (SWTSkinObjectImage2.this.canvas.isDisposed())) {
/* 359 */           return null;
/*     */         }
/*     */         
/* 362 */         SWTSkinObjectImage2.this.canvas.setData("drawAlpha", Integer.valueOf(SWTSkinObjectImage2.this.drawAlpha));
/*     */         
/* 364 */         String oldImageID = (String)SWTSkinObjectImage2.this.canvas.getData("ImageID");
/* 365 */         if ((sImageID != null) && (sImageID.equals(oldImageID))) {
/* 366 */           return null;
/*     */         }
/*     */         
/* 369 */         ImageLoader imageLoader = SWTSkinObjectImage2.this.skin.getImageLoader(SWTSkinObjectImage2.this.properties);
/*     */         
/* 371 */         if ((oldImageID != null) && (SWTSkinObjectImage2.this.canvas.getData("image") != null)) {
/* 372 */           imageLoader.releaseImage(oldImageID);
/*     */         }
/*     */         
/* 375 */         int hpadding = SWTSkinObjectImage2.this.properties.getIntValue(sConfigID + ".h-padding", 0);
/* 376 */         SWTSkinObjectImage2.this.canvas.setData("hpadding", new Long(hpadding));
/*     */         
/*     */ 
/* 379 */         Image[] images = (sImageID == null) || (sImageID.length() == 0) ? null : imageLoader.getImages(sImageID);
/*     */         
/*     */ 
/* 382 */         String sDrawMode = SWTSkinObjectImage2.this.properties.getStringValue(sConfigID + ".drawmode");
/* 383 */         if (sDrawMode == null) {
/* 384 */           sDrawMode = SWTSkinObjectImage2.this.properties.getStringValue(SWTSkinObjectImage2.this.sConfigID + ".drawmode", "");
/*     */         }
/*     */         
/*     */         Long drawMode;
/*     */         Long drawMode;
/* 389 */         if (sDrawMode.equals("scale")) {
/* 390 */           drawMode = SWTSkinObjectImage2.DRAW_SCALE; } else { Long drawMode;
/* 391 */           if (sDrawMode.equals("scaledown")) {
/* 392 */             drawMode = SWTSkinObjectImage2.DRAW_SCALEDOWN_OR_CENTER; } else { Long drawMode;
/* 393 */             if (sDrawMode.equals("stretch")) {
/* 394 */               drawMode = SWTSkinObjectImage2.DRAW_STRETCH; } else { Long drawMode;
/* 395 */               if (sDrawMode.equals("center")) {
/* 396 */                 drawMode = SWTSkinObjectImage2.DRAW_CENTER; } else { Long drawMode;
/* 397 */                 if (sDrawMode.equals("h-center")) {
/* 398 */                   drawMode = SWTSkinObjectImage2.DRAW_HCENTER; } else { Long drawMode;
/* 399 */                   if (sDrawMode.equalsIgnoreCase("tile")) {
/* 400 */                     drawMode = SWTSkinObjectImage2.DRAW_TILE; } else { Long drawMode;
/* 401 */                     if ((sDrawMode.equalsIgnoreCase("animate")) || ((sDrawMode.length() == 0) && (images != null) && (images.length > 3)))
/*     */                     {
/* 403 */                       drawMode = SWTSkinObjectImage2.DRAW_ANIMATE; } else { Long drawMode;
/* 404 */                       if (sDrawMode.equalsIgnoreCase("left")) {
/* 405 */                         drawMode = SWTSkinObjectImage2.DRAW_LEFT;
/*     */                       } else
/* 407 */                         drawMode = SWTSkinObjectImage2.DRAW_NORMAL;
/*     */                     } } } } } } }
/* 409 */         SWTSkinObjectImage2.this.canvas.setData("drawmode", drawMode);
/*     */         
/* 411 */         Image image = null;
/*     */         
/* 413 */         boolean hasExistingDelay = SWTSkinObjectImage2.this.canvas.getData("delay") != null;
/* 414 */         SWTSkinObjectImage2.this.canvas.setData("delay", null);
/* 415 */         if (images == null) {
/* 416 */           SWTSkinObjectImage2.this.canvas.setData("images", null);
/* 417 */           image = null;
/* 418 */         } else if (drawMode == SWTSkinObjectImage2.DRAW_ANIMATE) {
/* 419 */           int animationDelay = ImageLoader.getInstance().getAnimationDelay(sImageID);
/*     */           
/* 421 */           SWTSkinObjectImage2.this.canvas.setData("images", images);
/* 422 */           SWTSkinObjectImage2.this.canvas.setData("ImageIndex", Long.valueOf(0L));
/* 423 */           SWTSkinObjectImage2.this.canvas.setData("delay", new Long(animationDelay));
/* 424 */           image = images[0];
/*     */           
/* 426 */           if (!hasExistingDelay) {
/* 427 */             SWTSkinObjectImage2.this.setupAnimationTrigger(animationDelay);
/*     */           }
/* 429 */         } else if (images.length == 3) {
/* 430 */           Image imageLeft = images[0];
/* 431 */           if (ImageLoader.isRealImage(imageLeft)) {
/* 432 */             SWTSkinObjectImage2.this.canvas.setData("image-left", imageLeft);
/*     */           }
/*     */           
/* 435 */           image = images[1];
/*     */           
/* 437 */           Image imageRight = images[2];
/* 438 */           if (ImageLoader.isRealImage(imageRight)) {
/* 439 */             SWTSkinObjectImage2.this.canvas.setData("image-right", imageRight);
/*     */           }
/* 441 */         } else if (images.length > 0) {
/* 442 */           image = images[0];
/*     */         }
/*     */         
/* 445 */         if ((image == null) || (image.isDisposed())) {
/* 446 */           image = ImageLoader.noImage;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 452 */         Rectangle imgBounds = image.getBounds();
/*     */         
/* 454 */         if ((drawMode != SWTSkinObjectImage2.DRAW_CENTER) && (drawMode != SWTSkinObjectImage2.DRAW_HCENTER) && (drawMode != SWTSkinObjectImage2.DRAW_STRETCH) && (drawMode != SWTSkinObjectImage2.DRAW_SCALEDOWN_OR_CENTER))
/*     */         {
/* 456 */           SWTSkinObjectImage2.this.canvas.setSize(imgBounds.width + hpadding, imgBounds.height);
/* 457 */           SWTSkinObjectImage2.this.canvas.setData("oldSize", SWTSkinObjectImage2.this.canvas.getSize());
/*     */         }
/*     */         
/*     */ 
/* 461 */         if ((drawMode == SWTSkinObjectImage2.DRAW_TILE) || (drawMode == SWTSkinObjectImage2.DRAW_NORMAL) || (drawMode == SWTSkinObjectImage2.DRAW_LEFT) || (drawMode == SWTSkinObjectImage2.DRAW_ANIMATE))
/*     */         {
/*     */ 
/* 464 */           FormData fd = (FormData)SWTSkinObjectImage2.this.canvas.getLayoutData();
/* 465 */           if (fd == null) {
/* 466 */             fd = new FormData(imgBounds.width + hpadding, imgBounds.height);
/*     */           } else {
/* 468 */             fd.width = (imgBounds.width + hpadding);
/* 469 */             fd.height = imgBounds.height;
/*     */           }
/* 471 */           SWTSkinObjectImage2.this.canvas.setData("oldSize", new Point(fd.width, fd.height));
/* 472 */           SWTSkinObjectImage2.this.canvas.setLayoutData(fd);
/* 473 */           Utils.relayout(SWTSkinObjectImage2.this.canvas);
/*     */         }
/*     */         
/*     */ 
/* 477 */         SWTSkinObjectImage2.this.canvas.removePaintListener(SWTSkinObjectImage2.paintListener);
/*     */         
/* 479 */         SWTSkinObjectImage2.this.canvas.addPaintListener(SWTSkinObjectImage2.paintListener);
/* 480 */         SWTSkinObjectImage2.this.canvas.setData("ImageID", sImageID);
/*     */         
/* 482 */         SWTSkinObjectImage2.this.canvas.redraw();
/*     */         
/* 484 */         SWTSkinUtils.addMouseImageChangeListeners(SWTSkinObjectImage2.this.canvas);
/* 485 */         if (drawMode != SWTSkinObjectImage2.DRAW_ANIMATE) {
/* 486 */           imageLoader.releaseImage(sImageID);
/*     */         }
/* 488 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void setupAnimationTrigger(int animationDelay) {
/* 494 */     Utils.execSWTThreadLater(animationDelay, new AERunnable() {
/*     */       public void runSupport() {
/* 496 */         if (!SWTSkinObjectImage2.this.control.isDisposed()) {
/* 497 */           Object data = SWTSkinObjectImage2.this.control.getData("delay");
/* 498 */           if (data == null) {
/* 499 */             return;
/*     */           }
/*     */           
/* 502 */           Image[] images = (Image[])SWTSkinObjectImage2.this.control.getData("images");
/* 503 */           int idx = ((Number)SWTSkinObjectImage2.this.control.getData("ImageIndex")).intValue();
/* 504 */           idx++;
/* 505 */           if (idx >= images.length) {
/* 506 */             idx = 0;
/*     */           }
/* 508 */           SWTSkinObjectImage2.this.control.setData("ImageIndex", new Long(idx));
/* 509 */           SWTSkinObjectImage2.this.control.redraw();
/*     */           
/* 511 */           int delay = ((Number)SWTSkinObjectImage2.this.control.getData("delay")).intValue();
/* 512 */           SWTSkinObjectImage2.this.setupAnimationTrigger(delay);
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
/* 526 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/* 527 */     if (this.customImage) {
/* 528 */       return suffix;
/*     */     }
/* 530 */     if (suffix == null) {
/* 531 */       return null;
/*     */     }
/*     */     
/* 534 */     final String fSuffix = suffix;
/*     */     
/* 536 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 539 */         SWTSkinObjectImage2.this.currentImageID = ((SWTSkinObjectImage2.this.customImageID == null ? SWTSkinObjectImage2.this.sConfigID + ".image" : SWTSkinObjectImage2.this.customImageID) + fSuffix);
/*     */         
/*     */ 
/* 542 */         if (SWTSkinObjectImage2.this.isVisible()) {
/* 543 */           SWTSkinObjectImage2.this.swt_reallySetImage();
/*     */         }
/*     */         
/*     */       }
/* 547 */     });
/* 548 */     return suffix;
/*     */   }
/*     */   
/*     */   protected void swt_reallySetImage() {
/* 552 */     if ((this.currentImageID == null) || (this.customImage)) {
/* 553 */       this.drawAlpha = 255;
/* 554 */       return;
/*     */     }
/*     */     
/* 557 */     boolean removedDisabled = false;
/* 558 */     ImageLoader imageLoader = this.skin.getImageLoader(this.properties);
/* 559 */     boolean imageExists = imageLoader.imageExists(this.currentImageID);
/* 560 */     if ((!imageExists) && (imageLoader.imageExists(this.currentImageID + ".image"))) {
/* 561 */       this.currentImageID = (this.sConfigID + ".image");
/* 562 */       imageExists = true;
/*     */     }
/* 564 */     if ((!imageExists) && (this.suffixes != null)) {
/* 565 */       for (int i = this.suffixes.length - 1; i >= 0; i--) {
/* 566 */         String suffixToRemove = this.suffixes[i];
/* 567 */         if (suffixToRemove != null) {
/* 568 */           if (suffixToRemove.equals("-disabled")) {
/* 569 */             removedDisabled = true;
/*     */           }
/* 571 */           this.currentImageID = this.currentImageID.substring(0, this.currentImageID.length() - suffixToRemove.length());
/*     */           
/* 573 */           if (imageLoader.imageExists(this.currentImageID)) {
/* 574 */             imageExists = true;
/* 575 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 581 */     if (imageExists) {
/* 582 */       this.drawAlpha = (removedDisabled ? 64 : 255);
/* 583 */       setCanvasImage(this.currentImageID, null);
/*     */     } else {
/* 585 */       this.drawAlpha = 255;
/* 586 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 588 */           FormData fd = (FormData)SWTSkinObjectImage2.this.canvas.getLayoutData();
/* 589 */           if (fd == null) {
/* 590 */             fd = new FormData(0, 0);
/*     */           } else {
/* 592 */             fd.width = 0;
/* 593 */             fd.height = 0;
/*     */           }
/* 595 */           SWTSkinObjectImage2.this.canvas.setLayoutData(fd);
/* 596 */           if (SWTSkinObjectImage2.this.initialized) {
/* 597 */             Utils.relayout(SWTSkinObjectImage2.this.canvas);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void setImage(final Image image) {
/* 605 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 607 */         SWTSkinObjectImage2.this.customImage = true;
/* 608 */         SWTSkinObjectImage2.this.customImageID = null;
/* 609 */         SWTSkinObjectImage2.this.drawAlpha = 255;
/* 610 */         SWTSkinObjectImage2.this.canvas.setData("image", image);
/* 611 */         SWTSkinObjectImage2.this.canvas.setData("ImageID", null);
/* 612 */         SWTSkinObjectImage2.this.canvas.setData("image-left", null);
/* 613 */         SWTSkinObjectImage2.this.canvas.setData("image-right", null);
/* 614 */         SWTSkinObjectImage2.this.canvas.setData("drawAlpha", null);
/*     */         
/* 616 */         SWTSkinObjectImage2.this.canvas.removePaintListener(SWTSkinObjectImage2.paintListener);
/* 617 */         SWTSkinObjectImage2.this.canvas.addPaintListener(SWTSkinObjectImage2.paintListener);
/*     */         
/* 619 */         Utils.relayout(SWTSkinObjectImage2.this.canvas);
/* 620 */         SWTSkinObjectImage2.this.canvas.redraw();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setImageByID(final String imageID, final AECallback callback) {
/* 626 */     if ((!this.customImage) && (this.customImageID != null) && (this.customImageID.equals(imageID)))
/*     */     {
/* 628 */       if (callback != null) {
/* 629 */         callback.callbackFailure(null);
/*     */       }
/* 631 */       return;
/*     */     }
/* 633 */     this.customImage = false;
/* 634 */     this.customImageID = imageID;
/*     */     
/* 636 */     if (imageID == null) {
/* 637 */       setCanvasImage(this.sConfigID, null, null);
/* 638 */       return;
/*     */     }
/*     */     
/* 641 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 643 */         String fullImageID = imageID + SWTSkinObjectImage2.this.getSuffix();
/* 644 */         ImageLoader imageLoader = SWTSkinObjectImage2.this.skin.getImageLoader(SWTSkinObjectImage2.this.properties);
/* 645 */         Image image = imageLoader.getImage(fullImageID);
/* 646 */         if (ImageLoader.isRealImage(image)) {
/* 647 */           SWTSkinObjectImage2.this.setCanvasImage(SWTSkinObjectImage2.this.sConfigID, fullImageID, callback);
/*     */         } else {
/* 649 */           SWTSkinObjectImage2.this.setCanvasImage(SWTSkinObjectImage2.this.sConfigID, imageID, callback);
/*     */         }
/* 651 */         imageLoader.releaseImage(fullImageID);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setImageUrl(final String url) {
/* 657 */     if ((!this.customImage) && (this.customImageID != null) && (this.customImageID.equals(url)))
/*     */     {
/* 659 */       return;
/*     */     }
/* 661 */     this.customImage = false;
/* 662 */     this.customImageID = url;
/*     */     
/* 664 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 666 */         final ImageLoader imageLoader = SWTSkinObjectImage2.this.skin.getImageLoader(SWTSkinObjectImage2.this.properties);
/* 667 */         imageLoader.getUrlImage(url, new ImageLoader.ImageDownloaderListener() {
/*     */           public void imageDownloaded(Image image, boolean returnedImmediately) {
/* 669 */             SWTSkinObjectImage2.this.setCanvasImage(SWTSkinObjectImage2.11.this.val$url, null);
/* 670 */             imageLoader.releaseImage(SWTSkinObjectImage2.11.this.val$url);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectImage2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */