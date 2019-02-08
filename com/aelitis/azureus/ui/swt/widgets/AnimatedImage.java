/*     */ package com.aelitis.azureus.ui.swt.widgets;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
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
/*     */ public class AnimatedImage
/*     */ {
/*     */   private static final int SPEED = 100;
/*     */   Canvas canvas;
/*     */   boolean running;
/*     */   private AEThread2 runner;
/*     */   private Image[] images;
/*  40 */   private int currentImage = 0;
/*     */   private String imageName;
/*     */   
/*     */   public AnimatedImage(Composite parent)
/*     */   {
/*  45 */     this.canvas = new Canvas(parent, 262144);
/*  46 */     Color background = null;
/*  47 */     Composite p = parent;
/*  48 */     while ((p != null) && (background == null)) {
/*  49 */       background = p.getBackground();
/*  50 */       if (background != null) {
/*     */         break;
/*     */       }
/*     */       
/*  54 */       p = p.getParent();
/*     */     }
/*     */     
/*  57 */     this.canvas.setBackground(background);
/*  58 */     this.canvas.addListener(12, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  60 */         AnimatedImage.this.stop();
/*  61 */         AnimatedImage.this.disposeImages();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void renderNextImage()
/*     */   {
/*  68 */     if (!this.canvas.isDisposed()) {
/*  69 */       Display display = this.canvas.getDisplay();
/*  70 */       if (!display.isDisposed()) {
/*  71 */         display.asyncExec(new Runnable() {
/*     */           public void run() {
/*  73 */             if ((!AnimatedImage.this.canvas.isDisposed()) && (AnimatedImage.this.images != null)) {
/*  74 */               AnimatedImage.access$208(AnimatedImage.this);
/*  75 */               if (AnimatedImage.this.currentImage >= AnimatedImage.this.images.length) {
/*  76 */                 AnimatedImage.this.currentImage = 0;
/*     */               }
/*  78 */               if (AnimatedImage.this.currentImage < AnimatedImage.this.images.length) {
/*  79 */                 Image image = AnimatedImage.this.images[AnimatedImage.this.currentImage];
/*  80 */                 if ((image != null) && (!image.isDisposed()))
/*     */                 {
/*  82 */                   Rectangle imageBounds = image.getBounds();
/*     */                   
/*  84 */                   Image tempImage = new Image(AnimatedImage.this.canvas.getDisplay(), new Rectangle(0, 0, imageBounds.width, imageBounds.height));
/*  85 */                   GC gcImage = new GC(tempImage);
/*     */                   
/*  87 */                   gcImage.setBackground(AnimatedImage.this.canvas.getBackground());
/*  88 */                   gcImage.fillRectangle(new Rectangle(0, 0, imageBounds.width, imageBounds.width));
/*  89 */                   gcImage.drawImage(image, 0, 0);
/*     */                   
/*  91 */                   GC gc = new GC(AnimatedImage.this.canvas);
/*     */                   
/*  93 */                   Point canvasSize = AnimatedImage.this.canvas.getSize();
/*     */                   
/*  95 */                   gc.drawImage(tempImage, (canvasSize.x - imageBounds.width) / 2, (canvasSize.y - imageBounds.height) / 2);
/*     */                   
/*  97 */                   tempImage.dispose();
/*  98 */                   gcImage.dispose();
/*  99 */                   gc.dispose();
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object data) {
/* 110 */     Utils.adjustPXForDPI(data);
/* 111 */     this.canvas.setLayoutData(data);
/*     */   }
/*     */   
/*     */   public void start() {
/* 115 */     this.running = true;
/* 116 */     this.runner = new AEThread2("image runner", true) {
/*     */       public void run() {
/* 118 */         while (AnimatedImage.this.running) {
/*     */           try {
/* 120 */             AnimatedImage.this.renderNextImage();
/* 121 */             Thread.sleep(100L);
/*     */           } catch (Exception e) {
/* 123 */             AnimatedImage.this.running = false;
/*     */           }
/*     */         }
/*     */       }
/* 127 */     };
/* 128 */     this.runner.start();
/*     */   }
/*     */   
/*     */   public void stop() {
/* 132 */     this.running = false;
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 136 */     return this.canvas;
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 140 */     if ((this.canvas != null) && (!this.canvas.isDisposed())) {
/* 141 */       this.canvas.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setImageFromName(String imageName) {
/* 146 */     this.imageName = imageName;
/* 147 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 148 */     this.images = imageLoader.getImages(imageName);
/*     */   }
/*     */   
/*     */   private void setImages(Image[] images) {
/* 152 */     disposeImages();
/* 153 */     this.images = images;
/*     */   }
/*     */   
/*     */   private void disposeImages() {
/* 157 */     if (this.images != null) {
/* 158 */       ImageLoader imageLoader = ImageLoader.getInstance();
/* 159 */       imageLoader.releaseImage(this.imageName);
/* 160 */       this.images = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/widgets/AnimatedImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */