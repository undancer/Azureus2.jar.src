/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
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
/*     */ public class MultiPlotGraphic
/*     */   extends ScaledGraphic
/*     */   implements ParameterListener
/*     */ {
/*     */   private static final int DEFAULT_ENTRIES = 2000;
/*     */   private ValueSource[] value_sources;
/*     */   private int internalLoop;
/*     */   private int graphicsUpdate;
/*     */   private Point oldSize;
/*     */   private Image bufferImage;
/*     */   
/*     */   public static MultiPlotGraphic getInstance(ValueSource[] sources, ValueFormater formatter)
/*     */   {
/*  60 */     return new MultiPlotGraphic(new Scale(), sources, formatter);
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
/*  71 */   private int nbValues = 0;
/*  72 */   private int maxEntries = 2000;
/*     */   private int[][] all_values;
/*  74 */   private int currentPosition = 0;
/*     */   
/*  76 */   private boolean update_outstanding = false;
/*     */   
/*     */ 
/*     */ 
/*     */   private TimerEventPeriodic update_event;
/*     */   
/*     */ 
/*     */ 
/*     */   private MultiPlotGraphic(Scale scale, ValueSource[] sources, ValueFormater formater)
/*     */   {
/*  86 */     super(scale, formater);
/*     */     
/*  88 */     this.value_sources = sources;
/*     */     
/*  90 */     init((int[][])null);
/*     */     
/*  92 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Graphics Update", "Stats Graph Dividers" }, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void init(int[][] history)
/*     */   {
/* 100 */     this.nbValues = 0;
/* 101 */     this.maxEntries = 2000;
/* 102 */     this.all_values = new int[this.value_sources.length][this.maxEntries];
/* 103 */     this.currentPosition = 0;
/*     */     
/* 105 */     if (history != null)
/*     */     {
/* 107 */       if (history.length != this.value_sources.length)
/*     */       {
/* 109 */         Debug.out("Incompatible history records, ignored");
/*     */       }
/*     */       else {
/* 112 */         if (history.length > 0)
/*     */         {
/* 114 */           int history_entries = history[0].length;
/*     */           
/* 116 */           int offset = Math.max(history_entries - this.maxEntries, 0);
/*     */           
/* 118 */           for (int i = offset; i < history_entries; i++)
/*     */           {
/* 120 */             for (int j = 0; j < history.length; j++)
/*     */             {
/* 122 */               this.all_values[j][this.nbValues] = history[j][i];
/*     */             }
/*     */             
/* 125 */             this.nbValues += 1;
/*     */           }
/*     */         }
/*     */         
/* 129 */         this.currentPosition = this.nbValues;
/*     */       }
/*     */     }
/*     */     
/* 133 */     this.update_outstanding = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(Canvas canvas)
/*     */   {
/* 140 */     initialize(canvas, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initialize(Canvas canvas, boolean is_active)
/*     */   {
/* 148 */     super.initialize(canvas);
/*     */     
/* 150 */     this.drawCanvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 152 */         if ((MultiPlotGraphic.this.bufferImage != null) && (!MultiPlotGraphic.this.bufferImage.isDisposed())) {
/* 153 */           Rectangle bounds = MultiPlotGraphic.this.bufferImage.getBounds();
/* 154 */           if ((bounds.width >= e.width) && (bounds.height >= e.height))
/*     */           {
/* 156 */             e.gc.drawImage(MultiPlotGraphic.this.bufferImage, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 161 */     });
/* 162 */     this.drawCanvas.addListener(11, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 164 */         MultiPlotGraphic.this.drawChart(true);
/*     */       }
/*     */       
/* 167 */     });
/* 168 */     setActive(is_active);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setActive(boolean active)
/*     */   {
/* 175 */     if (active)
/*     */     {
/* 177 */       if (this.update_event != null)
/*     */       {
/* 179 */         return;
/*     */       }
/*     */       
/* 182 */       this.update_event = SimpleTimer.addPeriodicEvent("MPG:updater", 1000L, new TimerEventPerformer()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void perform(TimerEvent event)
/*     */         {
/*     */ 
/*     */ 
/* 191 */           if (MultiPlotGraphic.this.drawCanvas.isDisposed())
/*     */           {
/* 193 */             if (MultiPlotGraphic.this.update_event != null)
/*     */             {
/* 195 */               MultiPlotGraphic.this.update_event.cancel();
/*     */               
/* 197 */               MultiPlotGraphic.this.update_event = null;
/*     */             }
/*     */           } else {
/* 200 */             int[] new_values = new int[MultiPlotGraphic.this.value_sources.length];
/*     */             
/* 202 */             for (int i = 0; i < new_values.length; i++)
/*     */             {
/* 204 */               new_values[i] = MultiPlotGraphic.this.value_sources[i].getValue();
/*     */             }
/*     */             
/* 207 */             MultiPlotGraphic.this.addIntsValue(new_values);
/*     */           }
/*     */           
/*     */         }
/*     */       });
/*     */     }
/* 213 */     else if (this.update_event != null)
/*     */     {
/* 215 */       this.update_event.cancel();
/*     */       
/* 217 */       this.update_event = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset(int[][] history)
/*     */   {
/* 226 */     init(history);
/*     */     
/* 228 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 234 */         MultiPlotGraphic.this.refresh(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void addIntsValue(int[] new_values)
/*     */   {
/*     */     try
/*     */     {
/* 244 */       this.this_mon.enter();
/*     */       
/* 246 */       if (this.all_values.length < new_values.length)
/*     */       {
/* 248 */         int[][] new_all_values = new int[new_values.length][];
/*     */         
/* 250 */         System.arraycopy(this.all_values, 0, new_all_values, 0, this.all_values.length);
/*     */         
/* 252 */         for (int i = this.all_values.length; i < new_all_values.length; i++)
/*     */         {
/* 254 */           new_all_values[i] = new int[this.maxEntries];
/*     */         }
/*     */         
/* 257 */         this.all_values = new_all_values;
/*     */       }
/*     */       
/* 260 */       for (int i = 0; i < new_values.length; i++)
/*     */       {
/* 262 */         this.all_values[i][this.currentPosition] = new_values[i];
/*     */       }
/*     */       
/* 265 */       this.currentPosition += 1;
/*     */       
/* 267 */       if (this.nbValues < this.maxEntries)
/*     */       {
/* 269 */         this.nbValues += 1;
/*     */       }
/*     */       
/* 272 */       this.currentPosition %= this.maxEntries;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 277 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 280 */     if (this.update_outstanding)
/*     */     {
/* 282 */       this.update_outstanding = false;
/*     */       
/* 284 */       Utils.execSWTThread(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 290 */           MultiPlotGraphic.this.refresh(true);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refresh(boolean force)
/*     */   {
/* 301 */     if ((this.drawCanvas == null) || (this.drawCanvas.isDisposed()))
/*     */     {
/* 303 */       return;
/*     */     }
/*     */     
/* 306 */     Rectangle bounds = this.drawCanvas.getClientArea();
/*     */     
/* 308 */     if ((bounds.height < 30) || (bounds.width < 100) || (bounds.width > 10000) || (bounds.height > 10000))
/*     */     {
/* 310 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 315 */     if (bounds.width > this.maxEntries) {
/*     */       try
/*     */       {
/* 318 */         this.this_mon.enter();
/*     */         
/* 320 */         while (this.maxEntries < bounds.width)
/*     */         {
/* 322 */           this.maxEntries += 1000;
/*     */         }
/*     */         
/* 325 */         for (int i = 0; i < this.all_values.length; i++)
/*     */         {
/* 327 */           int[] newValues = new int[this.maxEntries];
/*     */           
/* 329 */           System.arraycopy(this.all_values[i], 0, newValues, 0, this.all_values[i].length);
/*     */           
/* 331 */           this.all_values[i] = newValues;
/*     */         }
/*     */       }
/*     */       finally {
/* 335 */         this.this_mon.exit();
/*     */       }
/*     */     }
/*     */     
/* 339 */     boolean sizeChanged = (this.oldSize == null) || (this.oldSize.x != bounds.width) || (this.oldSize.y != bounds.height);
/*     */     
/* 341 */     this.oldSize = new Point(bounds.width, bounds.height);
/*     */     
/* 343 */     this.internalLoop += 1;
/*     */     
/* 345 */     if (this.internalLoop > this.graphicsUpdate) {
/* 346 */       this.internalLoop = 0;
/*     */     }
/*     */     
/* 349 */     if ((this.internalLoop == 0) || (sizeChanged) || (force))
/*     */     {
/* 351 */       drawChart(sizeChanged);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 356 */       if (force)
/*     */       {
/* 358 */         drawChart(true);
/*     */       }
/*     */     }
/*     */     
/* 362 */     this.drawCanvas.redraw();
/* 363 */     this.drawCanvas.update();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void drawChart(boolean sizeChanged)
/*     */   {
/* 370 */     if ((this.drawCanvas == null) || (this.drawCanvas.isDisposed()) || (!this.drawCanvas.isVisible()))
/*     */     {
/* 372 */       return;
/*     */     }
/*     */     
/* 375 */     GC gcImage = null;
/*     */     try
/*     */     {
/* 378 */       this.this_mon.enter();
/*     */       
/* 380 */       drawScale(sizeChanged);
/*     */       
/* 382 */       if ((this.bufferScale == null) || (this.bufferScale.isDisposed())) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 387 */       Rectangle bounds = this.drawCanvas.getClientArea();
/*     */       
/* 389 */       if (bounds.isEmpty()) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 395 */       if ((this.bufferImage != null) && (!this.bufferImage.isDisposed()))
/*     */       {
/* 397 */         this.bufferImage.dispose();
/*     */       }
/*     */       
/* 400 */       this.bufferImage = new Image(this.drawCanvas.getDisplay(), bounds);
/*     */       
/* 402 */       gcImage = new GC(this.bufferImage);
/*     */       
/* 404 */       gcImage.drawImage(this.bufferScale, 0, 0);
/*     */       
/* 406 */       gcImage.setAntialias(1);
/* 407 */       gcImage.setTextAntialias(1);
/*     */       
/* 409 */       Set<ValueSource> invisible_sources = new HashSet();
/*     */       
/* 411 */       for (int i = 0; i < this.value_sources.length; i++)
/*     */       {
/* 413 */         ValueSource source = this.value_sources[i];
/*     */         
/* 415 */         if ((source.getStyle() & 0x10) != 0)
/*     */         {
/* 417 */           invisible_sources.add(source);
/*     */         }
/*     */       }
/*     */       
/* 421 */       int[] oldTargetValues = new int[this.all_values.length];
/*     */       
/* 423 */       int[] maxs = new int[this.all_values.length];
/*     */       
/* 425 */       for (int x = 0; x < bounds.width - 71; x++)
/*     */       {
/* 427 */         int position = this.currentPosition - x - 1;
/*     */         
/* 429 */         if (position < 0)
/*     */         {
/* 431 */           position += this.maxEntries;
/*     */           
/* 433 */           if (position < 0)
/*     */           {
/* 435 */             position = 0;
/*     */           }
/*     */         }
/*     */         
/* 439 */         for (int chartIdx = 0; chartIdx < this.all_values.length; chartIdx++)
/*     */         {
/* 441 */           ValueSource source = this.value_sources[chartIdx];
/*     */           
/* 443 */           if (!invisible_sources.contains(source))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 448 */             int value = this.all_values[chartIdx][position];
/*     */             
/* 450 */             if (value > maxs[chartIdx])
/*     */             {
/* 452 */               maxs[chartIdx] = value;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 457 */       Set<ValueSource> bold_sources = new HashSet();
/* 458 */       Set<ValueSource> dotted_sources = new HashSet();
/*     */       
/* 460 */       int max = 0;
/*     */       
/* 462 */       for (int i = 0; i < maxs.length; i++)
/*     */       {
/* 464 */         ValueSource source = this.value_sources[i];
/*     */         
/* 466 */         if (!invisible_sources.contains(source))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 471 */           if ((source.getStyle() & 0x8) != 0)
/*     */           {
/* 473 */             bold_sources.add(source);
/*     */           }
/*     */           
/* 476 */           if ((source.getStyle() & 0x20) != 0)
/*     */           {
/* 478 */             dotted_sources.add(source);
/*     */           }
/*     */           
/* 481 */           if (!source.isTrimmable())
/*     */           {
/* 483 */             max = Math.max(max, maxs[i]);
/*     */           }
/*     */         }
/*     */       }
/* 487 */       int max_primary = max;
/*     */       
/* 489 */       for (int i = 0; i < maxs.length; i++)
/*     */       {
/* 491 */         ValueSource source = this.value_sources[i];
/*     */         
/* 493 */         if (!invisible_sources.contains(source))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 498 */           if (source.isTrimmable())
/*     */           {
/*     */ 
/*     */ 
/* 502 */             int m = maxs[i];
/*     */             
/* 504 */             if (max < m)
/*     */             {
/* 506 */               if (m <= 2 * max_primary)
/*     */               {
/* 508 */                 max = m;
/*     */               }
/*     */               else
/*     */               {
/* 512 */                 max = 2 * max_primary;
/*     */                 
/* 514 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 520 */       int kInB = DisplayFormatters.getKinB();
/*     */       
/* 522 */       if (max > 5 * kInB)
/*     */       {
/* 524 */         max = (max + kInB - 1) / kInB * kInB;
/*     */       }
/*     */       
/* 527 */       this.scale.setMax(max);
/*     */       
/* 529 */       int[] prev_x = new int[this.value_sources.length];
/* 530 */       int[] prev_y = new int[this.value_sources.length];
/*     */       
/* 532 */       int bounds_width_adj = bounds.width - 71;
/*     */       
/* 534 */       int cycles = bold_sources.size() == 0 ? 2 : 3;
/*     */       
/*     */ 
/* 537 */       for (int x = 0; x < bounds_width_adj; x++)
/*     */       {
/* 539 */         int position = this.currentPosition - x - 1;
/*     */         
/* 541 */         if (position < 0)
/*     */         {
/* 543 */           position += this.maxEntries;
/*     */           
/* 545 */           if (position < 0)
/*     */           {
/* 547 */             position = 0;
/*     */           }
/*     */         }
/*     */         
/* 551 */         int xDraw = bounds_width_adj - x;
/*     */         
/* 553 */         for (int order = 0; order < cycles; order++)
/*     */         {
/* 555 */           for (int chartIdx = 0; chartIdx < this.all_values.length; chartIdx++)
/*     */           {
/* 557 */             ValueSource source = this.value_sources[chartIdx];
/*     */             
/* 559 */             if (!invisible_sources.contains(source))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 564 */               boolean is_bold = bold_sources.contains(source);
/*     */               
/* 566 */               if ((!is_bold) || (order == 2))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 571 */                 boolean is_dotted = dotted_sources.contains(source);
/*     */                 
/* 573 */                 if (source.isTrimmable() == (order == 0)) {} if ((order < 2) || ((is_bold) && (order == 2)))
/*     */                 {
/* 575 */                   Color line_color = source.getLineColor();
/*     */                   
/* 577 */                   int targetValue = this.all_values[chartIdx][position];
/*     */                   
/* 579 */                   int oldTargetValue = oldTargetValues[chartIdx];
/*     */                   
/* 581 */                   if (x > 0)
/*     */                   {
/*     */                     int trimmed;
/*     */                     int trimmed;
/* 585 */                     if (is_dotted)
/*     */                     {
/* 587 */                       trimmed = 2;
/*     */                     }
/*     */                     else
/*     */                     {
/* 591 */                       trimmed = 0;
/*     */                       
/* 593 */                       if (targetValue > max) {
/* 594 */                         targetValue = max;
/* 595 */                         trimmed++;
/*     */                       }
/*     */                       
/* 598 */                       if (oldTargetValue > max) {
/* 599 */                         oldTargetValue = max;
/* 600 */                         trimmed++;
/*     */                       }
/*     */                     }
/*     */                     
/* 604 */                     boolean force_draw = ((trimmed == 2) && (position % 4 == 0)) || (xDraw == 1);
/*     */                     
/* 606 */                     int h1 = bounds.height - this.scale.getScaledValue(targetValue) - 2;
/*     */                     
/* 608 */                     if (x == 1)
/*     */                     {
/* 610 */                       int h2 = bounds.height - this.scale.getScaledValue(oldTargetValue) - 2;
/*     */                       
/* 612 */                       prev_x[chartIdx] = (xDraw + 1);
/* 613 */                       prev_y[chartIdx] = h2;
/*     */                     }
/*     */                     
/* 616 */                     if ((trimmed < 2) || (force_draw))
/*     */                     {
/* 618 */                       if ((h1 != prev_y[chartIdx]) || (force_draw))
/*     */                       {
/* 620 */                         gcImage.setAlpha(source.getAlpha());
/* 621 */                         gcImage.setLineWidth(is_bold ? 4 : trimmed == 2 ? 3 : 2);
/* 622 */                         gcImage.setForeground(line_color);
/*     */                         
/* 624 */                         gcImage.drawLine(xDraw + 1, prev_y[chartIdx], prev_x[chartIdx], prev_y[chartIdx]);
/* 625 */                         gcImage.drawLine(xDraw, h1, xDraw + 1, prev_y[chartIdx]);
/*     */                         
/* 627 */                         prev_x[chartIdx] = xDraw;
/* 628 */                         prev_y[chartIdx] = h1;
/*     */                       }
/*     */                     }
/*     */                     else {
/* 632 */                       prev_x[chartIdx] = xDraw;
/* 633 */                       prev_y[chartIdx] = h1;
/*     */                     }
/*     */                   }
/*     */                   
/* 637 */                   oldTargetValues[chartIdx] = this.all_values[chartIdx][position];
/*     */                 }
/*     */               }
/*     */             }
/*     */           } }
/*     */       }
/* 643 */       if (this.nbValues > 0)
/*     */       {
/* 645 */         for (int order = 0; order < cycles; order++)
/*     */         {
/* 647 */           for (int chartIdx = 0; chartIdx < this.all_values.length; chartIdx++)
/*     */           {
/* 649 */             ValueSource source = this.value_sources[chartIdx];
/*     */             
/* 651 */             if (!invisible_sources.contains(source))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 656 */               boolean is_bold = bold_sources.contains(source);
/*     */               
/* 658 */               if ((!is_bold) || (order == 2))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 663 */                 if (source.isTrimmable() == (order == 0)) {} if ((order < 2) || ((is_bold) && (order == 2)))
/*     */                 {
/* 665 */                   int style = source.getStyle();
/*     */                   
/* 667 */                   if ((style & 0x40) == 0)
/*     */                   {
/* 669 */                     int average_val = computeAverage(chartIdx, this.currentPosition - 6);
/*     */                     
/* 671 */                     int average_mod = average_val;
/*     */                     
/* 673 */                     if (average_mod > max)
/*     */                     {
/* 675 */                       average_mod = max;
/*     */                     }
/*     */                     
/* 678 */                     int height = bounds.height - this.scale.getScaledValue(average_mod) - 2;
/*     */                     
/* 680 */                     gcImage.setAlpha(255);
/*     */                     
/* 682 */                     gcImage.setForeground(source.getLineColor());
/*     */                     
/* 684 */                     gcImage.drawText(this.formater.format(average_val), bounds.width - 65, height - 12, false);
/*     */                     
/* 686 */                     Color bg = gcImage.getBackground();
/*     */                     
/* 688 */                     if ((style & 0x2) != 0)
/*     */                     {
/* 690 */                       int x = bounds.width - 72;
/* 691 */                       int y = height - 12;
/*     */                       
/* 693 */                       gcImage.setBackground(source.getLineColor());
/*     */                       
/* 695 */                       gcImage.fillPolygon(new int[] { x, y, x + 7, y, x + 3, y + 7 });
/*     */                       
/* 697 */                       gcImage.setBackground(bg);
/*     */                     }
/* 699 */                     else if ((style & 0x1) != 0)
/*     */                     {
/* 701 */                       int x = bounds.width - 72;
/* 702 */                       int y = height - 12;
/*     */                       
/* 704 */                       gcImage.setBackground(source.getLineColor());
/*     */                       
/* 706 */                       gcImage.fillPolygon(new int[] { x, y + 7, x + 7, y + 7, x + 3, y });
/*     */                       
/* 708 */                       gcImage.setBackground(bg);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           } }
/*     */       }
/*     */     } catch (Throwable e) {
/* 717 */       Debug.out(e);
/*     */     }
/*     */     finally
/*     */     {
/* 721 */       if (gcImage != null)
/*     */       {
/* 723 */         gcImage.dispose();
/*     */       }
/*     */       
/* 726 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int computeAverage(int line_index, int position)
/*     */   {
/* 735 */     long sum = 0L;
/* 736 */     for (int i = -5; i < 6; i++) {
/* 737 */       int pos = position + i;
/* 738 */       pos %= this.maxEntries;
/* 739 */       if (pos < 0)
/* 740 */         pos += this.maxEntries;
/* 741 */       sum += this.all_values[line_index][pos];
/*     */     }
/* 743 */     return (int)(sum / 11L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void parameterChanged(String parameter)
/*     */   {
/* 751 */     this.graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update");
/*     */     
/* 753 */     boolean update_dividers = COConfigurationManager.getBooleanParameter("Stats Graph Dividers");
/*     */     
/* 755 */     int update_divider_width = update_dividers ? 60 : 0;
/*     */     
/* 757 */     setUpdateDividerWidth(update_divider_width);
/*     */   }
/*     */   
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 763 */     super.dispose();
/*     */     
/* 765 */     if ((this.bufferImage != null) && (!this.bufferImage.isDisposed()))
/*     */     {
/* 767 */       this.bufferImage.dispose();
/*     */     }
/*     */     
/* 770 */     if (this.update_event != null)
/*     */     {
/* 772 */       this.update_event.cancel();
/*     */       
/* 774 */       this.update_event = null;
/*     */     }
/*     */     
/* 777 */     COConfigurationManager.removeParameterListener("Graphics Update", this);
/* 778 */     COConfigurationManager.removeParameterListener("Stats Graph Dividers", this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/MultiPlotGraphic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */