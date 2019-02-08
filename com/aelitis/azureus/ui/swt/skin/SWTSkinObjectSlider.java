/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
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
/*     */ public class SWTSkinObjectSlider
/*     */   extends SWTSkinObjectBasic
/*     */   implements PaintListener, MouseListener, MouseMoveListener
/*     */ {
/*     */   private Image imageFG;
/*     */   private Object imageFGLeft;
/*     */   private Object imageFGRight;
/*     */   private Canvas canvas;
/*     */   private Image imageThumbRight;
/*     */   private Image imageThumb;
/*     */   private Image imageThumbLeft;
/*     */   private Image imageBGRight;
/*     */   private Image imageBG;
/*     */   private Image imageBGLeft;
/*     */   private double percent;
/*     */   private Rectangle imageFGbounds;
/*     */   private Rectangle imageBGbounds;
/*     */   private Rectangle imageThumbBounds;
/*  73 */   private Point maxSize = new Point(0, 0);
/*     */   
/*     */   private boolean mouseDown;
/*     */   
/*  77 */   private boolean mouseMoveAdjusts = true;
/*     */   
/*  79 */   private ArrayList listeners = new ArrayList();
/*     */   
/*     */   private double draggingPercent;
/*     */   
/*     */   private boolean disabled;
/*     */   
/*  85 */   private List<String> imagesToRelease = new ArrayList();
/*     */   
/*     */   public SWTSkinObjectSlider(SWTSkin skin, SWTSkinProperties skinProperties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parent)
/*     */   {
/*  89 */     super(skin, skinProperties, sID, sConfigID, "slider", parent);
/*     */     
/*  91 */     String sSuffix = ".complete";
/*  92 */     final ImageLoader imageLoader = skin.getImageLoader(this.properties);
/*     */     
/*  94 */     String imagePrefix = sConfigID + sSuffix;
/*  95 */     Image[] images = imageLoader.getImages(imagePrefix);
/*  96 */     this.imagesToRelease.add(imagePrefix);
/*  97 */     if ((images.length == 1) && (ImageLoader.isRealImage(images[0]))) {
/*  98 */       this.imageFG = images[0];
/*  99 */       this.imageFGLeft = imageLoader.getImage(imagePrefix + "-left");
/* 100 */       this.imageFGRight = imageLoader.getImage(imagePrefix + "-right");
/* 101 */       this.imagesToRelease.add(imagePrefix + "-left");
/* 102 */       this.imagesToRelease.add(imagePrefix + "-right");
/* 103 */     } else if ((images.length == 3) && (ImageLoader.isRealImage(images[2]))) {
/* 104 */       this.imageFGLeft = images[0];
/* 105 */       this.imageFG = images[1];
/* 106 */       this.imageFGRight = images[2];
/*     */     }
/*     */     
/* 109 */     if (this.imageFG != null) {
/* 110 */       this.imageFGbounds = this.imageFG.getBounds();
/*     */     }
/*     */     
/* 113 */     sSuffix = ".incomplete";
/* 114 */     imagePrefix = sConfigID + sSuffix;
/* 115 */     images = imageLoader.getImages(imagePrefix);
/* 116 */     this.imagesToRelease.add(imagePrefix);
/* 117 */     if ((images.length == 1) && (ImageLoader.isRealImage(images[0]))) {
/* 118 */       this.imageBG = images[0];
/* 119 */       this.imageBGLeft = imageLoader.getImage(imagePrefix + "-left");
/* 120 */       this.imageBGRight = imageLoader.getImage(imagePrefix + "-right");
/* 121 */       this.imagesToRelease.add(imagePrefix + "-left");
/* 122 */       this.imagesToRelease.add(imagePrefix + "-right");
/* 123 */     } else if ((images.length == 3) && (ImageLoader.isRealImage(images[2]))) {
/* 124 */       this.imageBGLeft = images[0];
/* 125 */       this.imageBG = images[1];
/* 126 */       this.imageBGRight = images[2];
/*     */     }
/*     */     
/* 129 */     if (this.imageBG != null) {
/* 130 */       this.imageBGbounds = this.imageBG.getBounds();
/*     */     }
/*     */     
/* 133 */     sSuffix = ".thumb";
/* 134 */     imagePrefix = sConfigID + sSuffix;
/* 135 */     images = imageLoader.getImages(imagePrefix);
/* 136 */     this.imagesToRelease.add(imagePrefix);
/* 137 */     if (images.length == 1) {
/* 138 */       this.imageThumb = images[0];
/* 139 */       this.imageThumbLeft = imageLoader.getImage(imagePrefix + "-left");
/* 140 */       this.imageThumbRight = imageLoader.getImage(imagePrefix + "-right");
/* 141 */       this.imagesToRelease.add(imagePrefix + "-left");
/* 142 */       this.imagesToRelease.add(imagePrefix + "-right");
/* 143 */     } else if ((images.length == 3) && (ImageLoader.isRealImage(images[2]))) {
/* 144 */       this.imageThumbLeft = images[0];
/* 145 */       this.imageThumb = images[1];
/* 146 */       this.imageThumbRight = images[2];
/*     */     }
/*     */     
/* 149 */     if (this.imageThumb != null) {
/* 150 */       this.imageThumbBounds = this.imageThumb.getBounds();
/*     */     }
/*     */     
/* 153 */     this.maxSize = buildMaxSize(new Rectangle[] { this.imageThumbBounds, this.imageBGbounds, this.imageFGbounds });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */     if (skinProperties.getStringValue(sConfigID + ".width", "").equalsIgnoreCase("auto"))
/*     */     {
/* 161 */       this.maxSize.x = 0;
/*     */     }
/*     */     
/* 164 */     int style = 0;
/*     */     
/* 166 */     if (skinProperties.getIntValue(sConfigID + ".border", 0) == 1) {
/* 167 */       style |= 0x800;
/*     */     }
/*     */     Composite createOn;
/*     */     Composite createOn;
/* 171 */     if (parent == null) {
/* 172 */       createOn = skin.getShell();
/*     */     } else {
/* 174 */       createOn = (Composite)parent.getControl();
/*     */     }
/*     */     
/* 177 */     this.canvas = new Canvas(createOn, style);
/* 178 */     Utils.setLayoutData(this.canvas, new FormData(this.maxSize.x == 0 ? -1 : this.maxSize.x, this.maxSize.y));
/*     */     
/* 180 */     this.canvas.setSize(-1, this.maxSize.y);
/* 181 */     setControl(this.canvas);
/*     */     
/* 183 */     this.canvas.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/*     */         try {
/* 186 */           for (String key : SWTSkinObjectSlider.this.imagesToRelease) {
/* 187 */             imageLoader.releaseImage(key);
/*     */           }
/*     */         } catch (Exception ex) {
/* 190 */           Debug.out(ex);
/*     */         }
/*     */         
/*     */       }
/* 194 */     });
/* 195 */     setAlwaysHookPaintListener(true);
/* 196 */     this.canvas.addMouseListener(this);
/* 197 */     this.canvas.addMouseMoveListener(this);
/*     */   }
/*     */   
/*     */   private Point buildMaxSize(Rectangle[] bounds) {
/* 201 */     Point maxSize = new Point(0, 0);
/* 202 */     for (int i = 0; i < bounds.length; i++) {
/* 203 */       if (bounds[i] != null)
/*     */       {
/*     */ 
/*     */ 
/* 207 */         if (bounds[i].width > maxSize.x) {
/* 208 */           maxSize.x = bounds[i].width;
/*     */         }
/* 210 */         if (bounds[i].height > maxSize.y)
/* 211 */           maxSize.y = bounds[i].height;
/*     */       }
/*     */     }
/* 214 */     return maxSize;
/*     */   }
/*     */   
/*     */   public void paintControl(GC gc)
/*     */   {
/* 219 */     super.paintControl(gc);
/*     */     
/* 221 */     int fullWidth = (this.maxSize.x == 0) || (this.imageFGbounds == null) ? this.canvas.getClientArea().width : this.imageFGbounds.width;
/*     */     
/*     */ 
/* 224 */     if ((this.percent > 0.0D) && (this.imageFG != null)) {
/* 225 */       int xDrawTo = (int)(fullWidth * this.percent);
/* 226 */       int xDrawToSrc = xDrawTo > this.imageFGbounds.width ? this.imageFGbounds.width : xDrawTo;
/*     */       
/* 228 */       int y = (this.maxSize.y - this.imageFGbounds.height) / 2;
/* 229 */       gc.drawImage(this.imageFG, 0, 0, xDrawToSrc, this.imageFGbounds.height, 0, y, xDrawTo, this.imageFGbounds.height);
/*     */     }
/*     */     
/* 232 */     if ((this.percent < 100.0D) && (this.imageBG != null) && (this.imageFGbounds != null)) {
/* 233 */       int xDrawFrom = (int)(this.imageBGbounds.width * this.percent);
/* 234 */       int xDrawWidth = this.imageBGbounds.width - xDrawFrom;
/* 235 */       gc.drawImage(this.imageBG, xDrawFrom, 0, xDrawWidth, this.imageFGbounds.height, xDrawFrom, 0, xDrawWidth, this.imageFGbounds.height);
/*     */     }
/*     */     
/*     */ 
/* 239 */     int drawWidth = fullWidth - this.imageThumbBounds.width;
/* 240 */     int xThumbPos = (int)(((this.mouseDown) && (!this.mouseMoveAdjusts) ? this.draggingPercent : this.percent) * drawWidth);
/* 241 */     gc.drawImage(this.imageThumb, xThumbPos, 0);
/*     */   }
/*     */   
/*     */   public double getPercent()
/*     */   {
/* 246 */     return this.percent;
/*     */   }
/*     */   
/*     */   public void setPercent(double percent) {
/* 250 */     setPercent(percent, false);
/*     */   }
/*     */   
/*     */   private boolean areDoublesEqual(double a, double b) {
/* 254 */     return Math.abs(a - b) < 1.0E-6D;
/*     */   }
/*     */   
/*     */   private void setPercent(double newPercent, boolean triggerListeners) {
/* 258 */     if (areDoublesEqual(this.percent, newPercent)) {
/* 259 */       return;
/*     */     }
/*     */     
/* 262 */     newPercent = validatePercent(newPercent, triggerListeners);
/*     */     
/* 264 */     if (areDoublesEqual(this.percent, newPercent)) {
/* 265 */       return;
/*     */     }
/*     */     
/* 268 */     this.percent = newPercent;
/*     */     
/* 270 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 272 */         if ((SWTSkinObjectSlider.this.canvas != null) && (!SWTSkinObjectSlider.this.canvas.isDisposed())) {
/* 273 */           SWTSkinObjectSlider.this.canvas.redraw();
/* 274 */           SWTSkinObjectSlider.this.canvas.update();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 279 */     if (triggerListeners) {
/* 280 */       Object[] listenersArray = this.listeners.toArray();
/* 281 */       for (int i = 0; i < listenersArray.length; i++) {
/* 282 */         SWTSkinListenerSliderSelection l = (SWTSkinListenerSliderSelection)listenersArray[i];
/* 283 */         l.selectionChanged(this.percent);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private double validatePercent(double percent, boolean triggerListeners)
/*     */   {
/* 294 */     if (triggerListeners) {
/* 295 */       Object[] listenersArray = this.listeners.toArray();
/* 296 */       for (int i = 0; i < listenersArray.length; i++) {
/* 297 */         SWTSkinListenerSliderSelection l = (SWTSkinListenerSliderSelection)listenersArray[i];
/* 298 */         Double changedPercent = l.selectionChanging(this.percent, percent);
/* 299 */         if (changedPercent != null) {
/* 300 */           return changedPercent.floatValue();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 305 */     if (percent < 0.0D)
/* 306 */       return 0.0D;
/* 307 */     if (percent > 1.0D) {
/* 308 */       return 1.0D;
/*     */     }
/* 310 */     return percent;
/*     */   }
/*     */   
/*     */ 
/*     */   public void mouseDoubleClick(MouseEvent e) {}
/*     */   
/*     */ 
/*     */   public void mouseDown(MouseEvent e)
/*     */   {
/* 319 */     if (this.disabled) {
/* 320 */       return;
/*     */     }
/* 322 */     this.mouseDown = true;
/*     */     
/* 324 */     int offset = this.imageThumbBounds.width / 2;
/* 325 */     int sizeX = this.maxSize.x;
/* 326 */     if (this.maxSize.x == 0) {
/* 327 */       sizeX = this.canvas.getClientArea().width;
/*     */     }
/* 329 */     float newPercent = (e.x - offset) / (sizeX - this.imageThumbBounds.width);
/*     */     
/*     */ 
/* 332 */     if (this.mouseMoveAdjusts) {
/* 333 */       setPercent(newPercent, true);
/*     */     } else {
/* 335 */       this.draggingPercent = validatePercent(newPercent, true);
/*     */       
/* 337 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 339 */           if ((SWTSkinObjectSlider.this.canvas != null) && (!SWTSkinObjectSlider.this.canvas.isDisposed())) {
/* 340 */             SWTSkinObjectSlider.this.canvas.redraw();
/* 341 */             SWTSkinObjectSlider.this.canvas.update();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void mouseUp(MouseEvent e)
/*     */   {
/* 351 */     if (this.disabled) {
/* 352 */       return;
/*     */     }
/* 354 */     this.mouseDown = false;
/* 355 */     if (!this.mouseMoveAdjusts) {
/* 356 */       int offset = this.imageThumbBounds.width / 2;
/* 357 */       int sizeX = this.maxSize.x;
/* 358 */       if (this.maxSize.x == 0) {
/* 359 */         sizeX = this.canvas.getClientArea().width;
/*     */       }
/* 361 */       float newPercent = (e.x - offset) / (sizeX - this.imageThumbBounds.width);
/*     */       
/* 363 */       setPercent(newPercent, true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void mouseMove(MouseEvent e)
/*     */   {
/* 369 */     if (this.disabled) {
/* 370 */       return;
/*     */     }
/* 372 */     if (this.mouseDown) {
/* 373 */       int offset = this.imageThumbBounds.width / 2;
/* 374 */       int sizeX = this.maxSize.x;
/* 375 */       if (this.maxSize.x == 0) {
/* 376 */         sizeX = this.canvas.getClientArea().width;
/*     */       }
/* 378 */       float newPercent = (e.x - offset) / (sizeX - this.imageThumbBounds.width);
/*     */       
/*     */ 
/* 381 */       if (this.mouseMoveAdjusts) {
/* 382 */         setPercent(newPercent, true);
/*     */       } else {
/* 384 */         this.draggingPercent = validatePercent(newPercent, true);
/*     */         
/* 386 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 388 */             if ((SWTSkinObjectSlider.this.canvas != null) && (!SWTSkinObjectSlider.this.canvas.isDisposed())) {
/* 389 */               SWTSkinObjectSlider.this.canvas.redraw();
/* 390 */               SWTSkinObjectSlider.this.canvas.update();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(SWTSkinListenerSliderSelection listener)
/*     */   {
/* 400 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class SWTSkinListenerSliderSelection
/*     */   {
/*     */     public Double selectionChanging(double oldPercent, double newPercent)
/*     */     {
/* 414 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public void selectionChanged(double percent) {}
/*     */   }
/*     */   
/*     */   public boolean getMouseMoveAdjusts()
/*     */   {
/* 423 */     return this.mouseMoveAdjusts;
/*     */   }
/*     */   
/*     */   public void setMouseMoveAdjusts(boolean mouseMoveAdjusts) {
/* 427 */     this.mouseMoveAdjusts = mouseMoveAdjusts;
/*     */   }
/*     */   
/*     */   public boolean isDisabled() {
/* 431 */     return this.disabled;
/*     */   }
/*     */   
/*     */   public void setDisabled(boolean disabled) {
/* 435 */     if (disabled == this.disabled) {
/* 436 */       return;
/*     */     }
/* 438 */     this.disabled = disabled;
/* 439 */     if (disabled) {
/* 440 */       this.mouseDown = false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectSlider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */