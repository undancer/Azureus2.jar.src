/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseTrackListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class SpeedGraphic
/*     */   extends ScaledGraphic
/*     */   implements ParameterListener
/*     */ {
/*     */   private static final int DEFAULT_ENTRIES = 2000;
/*     */   public static final int COLOR_AVERAGE = 0;
/*     */   public static final int COLOR_MAINSPEED = 1;
/*     */   public static final int COLOR_OVERHEAD = 2;
/*     */   public static final int COLOR_LIMIT = 3;
/*     */   public static final int COLOR_OTHERS = 4;
/*     */   public static final int COLOR_TRIMMED = 5;
/*     */   private static final int ALPHA_FOCUS = 200;
/*     */   private static final int ALPHA_NOFOCUS = 150;
/*  58 */   public Color[] colors = { Colors.red, Colors.blues[7], Colors.colorInverse, Colors.blue, Colors.grey, Colors.light_grey };
/*     */   
/*     */ 
/*     */   private int internalLoop;
/*     */   
/*     */   private int graphicsUpdate;
/*     */   
/*     */   private Point oldSize;
/*     */   
/*     */   protected Image bufferImage;
/*     */   
/*  69 */   private int nbValues = 0;
/*  70 */   private int maxEntries = 2000;
/*  71 */   private int[][] all_values = new int[1][this.maxEntries];
/*     */   
/*     */   private int currentPosition;
/*  74 */   private int alpha = 255;
/*     */   
/*  76 */   private boolean autoAlpha = false;
/*     */   
/*     */   private SpeedGraphic(Scale scale, ValueFormater formater)
/*     */   {
/*  80 */     super(scale, formater);
/*     */     
/*  82 */     this.currentPosition = 0;
/*     */     
/*  84 */     COConfigurationManager.addParameterListener("Graphics Update", this);
/*  85 */     parameterChanged("Graphics Update");
/*     */   }
/*     */   
/*     */   public void initialize(Canvas canvas) {
/*  89 */     super.initialize(canvas);
/*     */     
/*  91 */     canvas.addMouseTrackListener(new MouseTrackListener()
/*     */     {
/*     */       public void mouseHover(MouseEvent e) {}
/*     */       
/*     */       public void mouseExit(MouseEvent e) {
/*  96 */         if (SpeedGraphic.this.autoAlpha) {
/*  97 */           SpeedGraphic.this.setAlpha(150);
/*     */         }
/*     */       }
/*     */       
/*     */       public void mouseEnter(MouseEvent e) {
/* 102 */         if (SpeedGraphic.this.autoAlpha) {
/* 103 */           SpeedGraphic.this.setAlpha(200);
/*     */         }
/*     */         
/*     */       }
/* 107 */     });
/* 108 */     this.drawCanvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 110 */         if ((SpeedGraphic.this.bufferImage != null) && (!SpeedGraphic.this.bufferImage.isDisposed())) {
/* 111 */           Rectangle bounds = SpeedGraphic.this.bufferImage.getBounds();
/* 112 */           if ((bounds.width >= e.width) && (bounds.height >= e.height)) {
/* 113 */             if (SpeedGraphic.this.alpha != 255) {
/*     */               try {
/* 115 */                 e.gc.setAlpha(SpeedGraphic.this.alpha);
/*     */               }
/*     */               catch (Exception ex) {}
/*     */             }
/*     */             
/* 120 */             e.gc.drawImage(SpeedGraphic.this.bufferImage, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/* 126 */     });
/* 127 */     this.drawCanvas.addListener(11, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 129 */         SpeedGraphic.this.drawChart(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static SpeedGraphic getInstance() {
/* 135 */     new SpeedGraphic(new Scale(), new ValueFormater() {
/*     */       public String format(int value) {
/* 137 */         return DisplayFormatters.formatByteCountToKiBEtcPerSec(value);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static SpeedGraphic getInstance(ValueFormater formatter) {
/* 143 */     return new SpeedGraphic(new Scale(), formatter);
/*     */   }
/*     */   
/*     */   public static SpeedGraphic getInstance(Scale scale, ValueFormater formatter) {
/* 147 */     return new SpeedGraphic(scale, formatter);
/*     */   }
/*     */   
/*     */   public void addIntsValue(int[] new_values) {
/*     */     try {
/* 152 */       this.this_mon.enter();
/*     */       
/* 154 */       if (this.all_values.length < new_values.length)
/*     */       {
/* 156 */         int[][] new_all_values = new int[new_values.length][];
/*     */         
/* 158 */         System.arraycopy(this.all_values, 0, new_all_values, 0, this.all_values.length);
/*     */         
/* 160 */         for (int i = this.all_values.length; i < new_all_values.length; i++)
/*     */         {
/* 162 */           new_all_values[i] = new int[this.maxEntries];
/*     */         }
/*     */         
/* 165 */         this.all_values = new_all_values;
/*     */       }
/*     */       
/* 168 */       for (int i = 0; i < new_values.length; i++)
/*     */       {
/* 170 */         this.all_values[i][this.currentPosition] = new_values[i];
/*     */       }
/*     */       
/* 173 */       this.currentPosition += 1;
/*     */       
/* 175 */       if (this.nbValues < this.maxEntries)
/*     */       {
/* 177 */         this.nbValues += 1;
/*     */       }
/*     */       
/* 180 */       this.currentPosition %= this.maxEntries;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 185 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addIntValue(int value) {
/* 190 */     addIntsValue(new int[] { value });
/*     */   }
/*     */   
/*     */   public void refresh(boolean force) {
/* 194 */     if ((this.drawCanvas == null) || (this.drawCanvas.isDisposed())) {
/* 195 */       return;
/*     */     }
/* 197 */     Rectangle bounds = this.drawCanvas.getClientArea();
/* 198 */     if ((bounds.height < 30) || (bounds.width < 100) || (bounds.width > 10000) || (bounds.height > 10000)) {
/* 199 */       return;
/*     */     }
/*     */     
/* 202 */     if (bounds.width > this.maxEntries) {
/*     */       try
/*     */       {
/* 205 */         this.this_mon.enter();
/*     */         
/* 207 */         while (this.maxEntries < bounds.width) {
/* 208 */           this.maxEntries += 1000;
/*     */         }
/* 210 */         for (int i = 0; i < this.all_values.length; i++)
/*     */         {
/* 212 */           int[] newValues = new int[this.maxEntries];
/* 213 */           System.arraycopy(this.all_values[i], 0, newValues, 0, this.all_values[i].length);
/* 214 */           this.all_values[i] = newValues;
/*     */         }
/*     */       }
/*     */       finally {
/* 218 */         this.this_mon.exit();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 225 */     boolean sizeChanged = (this.oldSize == null) || (this.oldSize.x != bounds.width) || (this.oldSize.y != bounds.height);
/* 226 */     this.oldSize = new Point(bounds.width, bounds.height);
/*     */     
/* 228 */     this.internalLoop += 1;
/* 229 */     if (this.internalLoop > this.graphicsUpdate) {
/* 230 */       this.internalLoop = 0;
/*     */     }
/*     */     
/* 233 */     if ((this.internalLoop == 0) || (sizeChanged) || (force)) {
/* 234 */       drawChart(sizeChanged);
/*     */       
/*     */ 
/* 237 */       if (force) {
/* 238 */         drawChart(true);
/*     */       }
/*     */     }
/*     */     
/* 242 */     this.drawCanvas.redraw();
/* 243 */     this.drawCanvas.update();
/*     */   }
/*     */   
/*     */   protected void drawChart(boolean sizeChanged) {
/* 247 */     if ((this.drawCanvas == null) || (this.drawCanvas.isDisposed()) || (!this.drawCanvas.isVisible()))
/*     */     {
/* 249 */       return;
/*     */     }
/* 251 */     GC gcImage = null;
/*     */     try
/*     */     {
/* 254 */       this.this_mon.enter();
/*     */       
/* 256 */       drawScale(sizeChanged);
/*     */       
/* 258 */       if ((this.bufferScale == null) || (this.bufferScale.isDisposed())) {
/*     */         return;
/*     */       }
/* 261 */       Rectangle bounds = this.drawCanvas.getClientArea();
/* 262 */       if (bounds.isEmpty()) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 267 */       if ((this.bufferImage != null) && (!this.bufferImage.isDisposed())) {
/* 268 */         this.bufferImage.dispose();
/*     */       }
/* 270 */       this.bufferImage = new Image(this.drawCanvas.getDisplay(), bounds);
/* 271 */       gcImage = new GC(this.bufferImage);
/* 272 */       gcImage.drawImage(this.bufferScale, 0, 0);
/*     */       
/* 274 */       gcImage.setAntialias(1);
/*     */       
/* 276 */       int oldAverage = 0;
/* 277 */       int[] oldTargetValues = new int[this.all_values.length];
/*     */       
/* 279 */       int[] maxs = new int[this.all_values.length];
/*     */       
/* 281 */       for (int x = 0; x < bounds.width - 71; x++)
/*     */       {
/* 283 */         int position = this.currentPosition - x - 1;
/* 284 */         if (position < 0)
/*     */         {
/* 286 */           position += this.maxEntries;
/* 287 */           if (position < 0)
/*     */           {
/* 289 */             position = 0;
/*     */           }
/*     */         }
/* 292 */         for (int chartIdx = 0; chartIdx < this.all_values.length; chartIdx++)
/*     */         {
/* 294 */           int value = this.all_values[chartIdx][position];
/* 295 */           if (value > maxs[chartIdx])
/*     */           {
/* 297 */             maxs[chartIdx] = value;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 302 */       int max = maxs[0];
/* 303 */       int max_primary = max;
/*     */       
/* 305 */       for (int i = 1; i < maxs.length; i++)
/*     */       {
/* 307 */         int m = maxs[i];
/* 308 */         if (i == 1)
/*     */         {
/* 310 */           if (max < m)
/*     */           {
/* 312 */             max = m;
/* 313 */             max_primary = max;
/*     */           }
/*     */           
/*     */ 
/*     */         }
/* 318 */         else if (max < m)
/*     */         {
/* 320 */           if (m <= 2 * max_primary)
/*     */           {
/* 322 */             max = m;
/*     */           }
/*     */           else {
/* 325 */             max = 2 * max_primary;
/* 326 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 332 */       this.scale.setMax(max);
/* 333 */       int maxHeight = this.scale.getScaledValue(max);
/*     */       
/* 335 */       Color background = this.colors[1];
/* 336 */       Color foreground = this.colors[1];
/*     */       
/* 338 */       for (int x = 0; x < bounds.width - 71; x++)
/*     */       {
/* 340 */         int position = this.currentPosition - x - 1;
/* 341 */         if (position < 0)
/*     */         {
/* 343 */           position += this.maxEntries;
/* 344 */           if (position < 0)
/*     */           {
/* 346 */             position = 0;
/*     */           }
/*     */         }
/* 349 */         int xDraw = bounds.width - 71 - x;
/* 350 */         int height = this.scale.getScaledValue(this.all_values[0][position]);
/*     */         
/* 352 */         gcImage.setForeground(background);
/* 353 */         gcImage.setBackground(foreground);
/* 354 */         gcImage.setClipping(xDraw, bounds.height - 1 - height, 1, height);
/* 355 */         gcImage.fillGradientRectangle(xDraw, bounds.height - 1 - maxHeight, 1, maxHeight, true);
/* 356 */         gcImage.setClipping(0, 0, bounds.width, bounds.height);
/* 357 */         if (this.all_values.length > 1)
/*     */         {
/* 359 */           gcImage.setForeground(this.colors[2]);
/* 360 */           height = this.scale.getScaledValue(this.all_values[1][position]);
/* 361 */           Utils.drawStriped(gcImage, xDraw, bounds.height - 1 - height, 1, height, 1, this.currentPosition, false);
/*     */         }
/* 363 */         for (int chartIdx = 2; chartIdx < this.all_values.length; chartIdx++)
/*     */         {
/* 365 */           int targetValue = this.all_values[chartIdx][position];
/* 366 */           int oldTargetValue = oldTargetValues[chartIdx];
/* 367 */           if ((x > 1) && (((chartIdx == 2) && (targetValue > 0) && (oldTargetValue > 0)) || ((chartIdx > 2) && ((targetValue > 0) || (oldTargetValue > 0)))))
/*     */           {
/* 369 */             int trimmed = 0;
/* 370 */             if (targetValue > max)
/*     */             {
/* 372 */               targetValue = max;
/* 373 */               trimmed++;
/*     */             }
/* 375 */             if (oldTargetValue > max)
/*     */             {
/* 377 */               oldTargetValue = max;
/* 378 */               trimmed++;
/*     */             }
/* 380 */             if ((trimmed < 2) || ((trimmed == 2) && (position % 3 == 0)))
/*     */             {
/* 382 */               int h1 = bounds.height - this.scale.getScaledValue(targetValue) - 2;
/* 383 */               int h2 = bounds.height - this.scale.getScaledValue(oldTargetValue) - 2;
/* 384 */               gcImage.setForeground(trimmed > 0 ? this.colors[5] : chartIdx == 2 ? this.colors[3] : this.colors[4]);
/* 385 */               gcImage.drawLine(xDraw, h1, xDraw + 1, h2);
/*     */             }
/*     */           }
/* 388 */           oldTargetValues[chartIdx] = this.all_values[chartIdx][position];
/*     */         }
/* 390 */         int average = computeAverage(position);
/* 391 */         if (x > 6)
/*     */         {
/* 393 */           int h1 = bounds.height - this.scale.getScaledValue(average) - 2;
/* 394 */           int h2 = bounds.height - this.scale.getScaledValue(oldAverage) - 2;
/* 395 */           gcImage.setForeground(this.colors[0]);
/* 396 */           gcImage.drawLine(xDraw, h1, xDraw + 1, h2);
/*     */         }
/* 398 */         oldAverage = average;
/*     */       }
/*     */       
/* 401 */       if (this.nbValues > 0)
/*     */       {
/* 403 */         int height = bounds.height - this.scale.getScaledValue(computeAverage(this.currentPosition - 6)) - 2;
/* 404 */         gcImage.setForeground(this.colors[0]);
/* 405 */         gcImage.drawText(this.formater.format(computeAverage(this.currentPosition - 6)), bounds.width - 65, height - 12, true);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 410 */       Debug.out("Warning", e);
/*     */     }
/*     */     finally {
/* 413 */       if (gcImage != null)
/*     */       {
/* 415 */         gcImage.dispose();
/*     */       }
/* 417 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected int computeAverage(int position) {
/* 422 */     long sum = 0L;
/* 423 */     for (int i = -5; i < 6; i++) {
/* 424 */       int pos = position + i;
/* 425 */       pos %= this.maxEntries;
/* 426 */       if (pos < 0)
/* 427 */         pos += this.maxEntries;
/* 428 */       sum += this.all_values[0][pos];
/*     */     }
/* 430 */     return (int)(sum / 11L);
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameter)
/*     */   {
/* 435 */     this.graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update");
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 439 */     super.dispose();
/* 440 */     if ((this.bufferImage != null) && (!this.bufferImage.isDisposed())) {
/* 441 */       this.bufferImage.dispose();
/*     */     }
/* 443 */     COConfigurationManager.removeParameterListener("Graphics Update", this);
/*     */   }
/*     */   
/*     */   private int getAlpha() {
/* 447 */     return this.alpha;
/*     */   }
/*     */   
/*     */   public void setAlpha(int alpha) {
/* 451 */     this.alpha = alpha;
/* 452 */     if ((this.drawCanvas != null) && (!this.drawCanvas.isDisposed())) {
/* 453 */       this.drawCanvas.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isAutoAlpha() {
/* 458 */     return this.autoAlpha;
/*     */   }
/*     */   
/*     */   private void setAutoAlpha(boolean autoAlpha) {
/* 462 */     this.autoAlpha = autoAlpha;
/* 463 */     if (autoAlpha) {
/* 464 */       setAlpha(this.drawCanvas.getDisplay().getCursorControl() == this.drawCanvas ? 200 : 150);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLineColors(Color average, Color speed, Color overhead, Color limit, Color others, Color trimmed) {
/* 469 */     if (average != null) {
/* 470 */       this.colors[0] = average;
/*     */     }
/* 472 */     if (speed != null) {
/* 473 */       this.colors[1] = speed;
/*     */     }
/* 475 */     if (overhead != null) {
/* 476 */       this.colors[2] = overhead;
/*     */     }
/* 478 */     if (limit != null) {
/* 479 */       this.colors[3] = limit;
/*     */     }
/* 481 */     if (others != null) {
/* 482 */       this.colors[4] = others;
/*     */     }
/* 484 */     if (trimmed != null) {
/* 485 */       this.colors[5] = trimmed;
/*     */     }
/* 487 */     if ((this.drawCanvas != null) && (!this.drawCanvas.isDisposed())) {
/* 488 */       this.drawCanvas.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLineColors(Color[] newChangeableColorSet) {
/* 493 */     this.colors = newChangeableColorSet;
/* 494 */     if ((this.drawCanvas != null) && (!this.drawCanvas.isDisposed())) {
/* 495 */       this.drawCanvas.redraw();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/SpeedGraphic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */