/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlActivity;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlActivity.ActivityNode;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlActivity.ActivityState;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlListener;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.MouseTrackListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class DHTOpsPanel
/*     */   implements DHTControlListener
/*     */ {
/*     */   private static final int ALPHA_FOCUS = 255;
/*     */   private static final int ALPHA_NOFOCUS = 150;
/*     */   private static final int FADE_OUT = 10000;
/*     */   Display display;
/*     */   Composite parent;
/*     */   Canvas canvas;
/*     */   Scale scale;
/*  67 */   private int min_slots = 8;
/*     */   
/*     */   private boolean unavailable;
/*     */   
/*  71 */   private boolean mouseLeftDown = false;
/*  72 */   private boolean mouseRightDown = false;
/*     */   
/*     */   private int xDown;
/*     */   
/*     */   private int yDown;
/*     */   private Image img;
/*  78 */   private int alpha = 255;
/*     */   
/*  80 */   private boolean autoAlpha = false;
/*     */   
/*     */   private DHT current_dht;
/*     */   
/*     */   private ActivityFilter filter;
/*  85 */   private Map<DHTControlActivity, ActivityDetail> activity_map = new HashMap();
/*     */   
/*     */   private TimerEventPeriodic timeout_timer;
/*     */   
/*     */   private static class Scale
/*     */   {
/*     */     int width;
/*     */     int height;
/*  93 */     float minX = -1000.0F;
/*  94 */     float maxX = 1000.0F;
/*  95 */     float minY = -1000.0F;
/*  96 */     float maxY = 1000.0F;
/*  97 */     double rotation = 0.0D;
/*     */     float saveMinX;
/*     */     float saveMaxX;
/*     */     float saveMinY;
/*     */     float saveMaxY;
/*     */     double saveRotation;
/*     */     
/*     */     public int getX(float x, float y)
/*     */     {
/* 106 */       return (int)((x * Math.cos(this.rotation) + y * Math.sin(this.rotation) - this.minX) / (this.maxX - this.minX) * this.width);
/*     */     }
/*     */     
/*     */     public int getY(float x, float y) {
/* 110 */       return (int)((y * Math.cos(this.rotation) - x * Math.sin(this.rotation) - this.minY) / (this.maxY - this.minY) * this.height);
/*     */     }
/*     */   }
/*     */   
/*     */   public DHTOpsPanel(Composite parent) {
/* 115 */     this.parent = parent;
/* 116 */     this.display = parent.getDisplay();
/* 117 */     this.canvas = new Canvas(parent, 262144);
/*     */     
/* 119 */     this.scale = new Scale(null);
/*     */     
/* 121 */     this.canvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 123 */         if ((DHTOpsPanel.this.img != null) && (!DHTOpsPanel.this.img.isDisposed())) {
/* 124 */           Rectangle bounds = DHTOpsPanel.this.img.getBounds();
/* 125 */           if ((bounds.width >= e.width) && (bounds.height >= e.height)) {
/* 126 */             if (DHTOpsPanel.this.alpha != 255) {
/*     */               try {
/* 128 */                 e.gc.setAlpha(DHTOpsPanel.this.alpha);
/*     */               }
/*     */               catch (Exception ex) {}
/*     */             }
/*     */             
/* 133 */             e.gc.drawImage(DHTOpsPanel.this.img, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
/*     */           }
/*     */         }
/*     */         else {
/* 137 */           e.gc.setBackground(DHTOpsPanel.this.display.getSystemColor(22));
/* 138 */           e.gc.fillRectangle(e.x, e.y, e.width, e.height);
/*     */           
/* 140 */           e.gc.drawText(MessageText.getString(DHTOpsPanel.this.unavailable ? "DHTOpsView.notAvailable" : "v3.MainWindow.view.wait"), 10, 10, true);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 146 */     });
/* 147 */     this.canvas.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseDown(MouseEvent event) {
/* 150 */         if (event.button == 1) DHTOpsPanel.this.mouseLeftDown = true;
/* 151 */         if (event.button == 3) DHTOpsPanel.this.mouseRightDown = true;
/* 152 */         DHTOpsPanel.this.xDown = event.x;
/* 153 */         DHTOpsPanel.this.yDown = event.y;
/* 154 */         DHTOpsPanel.this.scale.saveMinX = DHTOpsPanel.this.scale.minX;
/* 155 */         DHTOpsPanel.this.scale.saveMaxX = DHTOpsPanel.this.scale.maxX;
/* 156 */         DHTOpsPanel.this.scale.saveMinY = DHTOpsPanel.this.scale.minY;
/* 157 */         DHTOpsPanel.this.scale.saveMaxY = DHTOpsPanel.this.scale.maxY;
/* 158 */         DHTOpsPanel.this.scale.saveRotation = DHTOpsPanel.this.scale.rotation;
/*     */       }
/*     */       
/*     */       public void mouseUp(MouseEvent event) {
/* 162 */         if (event.button == 1) DHTOpsPanel.this.mouseLeftDown = false;
/* 163 */         if (event.button == 3) DHTOpsPanel.this.mouseRightDown = false;
/* 164 */         DHTOpsPanel.this.refresh();
/*     */       }
/*     */       
/* 167 */     });
/* 168 */     this.canvas.addListener(1, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {}
/*     */ 
/* 172 */     });
/* 173 */     this.canvas.addListener(37, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 176 */         DHTOpsPanel.this.scale.saveMinX = DHTOpsPanel.this.scale.minX;
/* 177 */         DHTOpsPanel.this.scale.saveMaxX = DHTOpsPanel.this.scale.maxX;
/* 178 */         DHTOpsPanel.this.scale.saveMinY = DHTOpsPanel.this.scale.minY;
/* 179 */         DHTOpsPanel.this.scale.saveMaxY = DHTOpsPanel.this.scale.maxY;
/*     */         
/* 181 */         int deltaY = event.count * 5;
/*     */         
/*     */ 
/* 184 */         float scaleFactor = 1.0F - deltaY / 300.0F;
/* 185 */         if (scaleFactor <= 0.0F) { scaleFactor = 0.01F;
/*     */         }
/*     */         
/* 188 */         float moveFactor = 1.0F - 1.0F / scaleFactor;
/*     */         
/* 190 */         float centerX = (DHTOpsPanel.this.scale.saveMinX + DHTOpsPanel.this.scale.saveMaxX) / 2.0F;
/* 191 */         DHTOpsPanel.this.scale.minX = (DHTOpsPanel.this.scale.saveMinX + moveFactor * (centerX - DHTOpsPanel.this.scale.saveMinX));
/* 192 */         DHTOpsPanel.this.scale.maxX = (DHTOpsPanel.this.scale.saveMaxX - moveFactor * (DHTOpsPanel.this.scale.saveMaxX - centerX));
/*     */         
/* 194 */         float centerY = (DHTOpsPanel.this.scale.saveMinY + DHTOpsPanel.this.scale.saveMaxY) / 2.0F;
/* 195 */         DHTOpsPanel.this.scale.minY = (DHTOpsPanel.this.scale.saveMinY + moveFactor * (centerY - DHTOpsPanel.this.scale.saveMinY));
/* 196 */         DHTOpsPanel.this.scale.maxY = (DHTOpsPanel.this.scale.saveMaxY - moveFactor * (DHTOpsPanel.this.scale.saveMaxY - centerY));
/* 197 */         DHTOpsPanel.this.refresh();
/*     */       }
/*     */       
/* 200 */     });
/* 201 */     this.canvas.addMouseMoveListener(new MouseMoveListener() {
/*     */       private long last_refresh;
/*     */       
/* 204 */       public void mouseMove(MouseEvent event) { boolean do_refresh = false;
/* 205 */         if ((DHTOpsPanel.this.mouseLeftDown) && ((event.stateMask & SWT.MOD4) == 0)) {
/* 206 */           int deltaX = event.x - DHTOpsPanel.this.xDown;
/* 207 */           int deltaY = event.y - DHTOpsPanel.this.yDown;
/* 208 */           float width = DHTOpsPanel.this.scale.width;
/* 209 */           float height = DHTOpsPanel.this.scale.height;
/* 210 */           float ratioX = (DHTOpsPanel.this.scale.saveMaxX - DHTOpsPanel.this.scale.saveMinX) / width;
/* 211 */           float ratioY = (DHTOpsPanel.this.scale.saveMaxY - DHTOpsPanel.this.scale.saveMinY) / height;
/* 212 */           float realDeltaX = deltaX * ratioX;
/* 213 */           float realDeltaY = deltaY * ratioY;
/* 214 */           DHTOpsPanel.this.scale.minX = (DHTOpsPanel.this.scale.saveMinX - realDeltaX);
/* 215 */           DHTOpsPanel.this.scale.maxX = (DHTOpsPanel.this.scale.saveMaxX - realDeltaX);
/* 216 */           DHTOpsPanel.this.scale.minY = (DHTOpsPanel.this.scale.saveMinY - realDeltaY);
/* 217 */           DHTOpsPanel.this.scale.maxY = (DHTOpsPanel.this.scale.saveMaxY - realDeltaY);
/* 218 */           do_refresh = true;
/*     */         }
/* 220 */         if ((DHTOpsPanel.this.mouseRightDown) || ((DHTOpsPanel.this.mouseLeftDown) && ((event.stateMask & SWT.MOD4) > 0))) {
/* 221 */           int deltaX = event.x - DHTOpsPanel.this.xDown;
/* 222 */           DHTOpsPanel.this.scale.rotation = (DHTOpsPanel.this.scale.saveRotation - deltaX / 100.0F);
/*     */           
/* 224 */           int deltaY = event.y - DHTOpsPanel.this.yDown;
/*     */           
/*     */ 
/* 227 */           float scaleFactor = 1.0F - deltaY / 300.0F;
/* 228 */           if (scaleFactor <= 0.0F) { scaleFactor = 0.01F;
/*     */           }
/*     */           
/* 231 */           float moveFactor = 1.0F - 1.0F / scaleFactor;
/*     */           
/* 233 */           float centerX = (DHTOpsPanel.this.scale.saveMinX + DHTOpsPanel.this.scale.saveMaxX) / 2.0F;
/* 234 */           DHTOpsPanel.this.scale.minX = (DHTOpsPanel.this.scale.saveMinX + moveFactor * (centerX - DHTOpsPanel.this.scale.saveMinX));
/* 235 */           DHTOpsPanel.this.scale.maxX = (DHTOpsPanel.this.scale.saveMaxX - moveFactor * (DHTOpsPanel.this.scale.saveMaxX - centerX));
/*     */           
/* 237 */           float centerY = (DHTOpsPanel.this.scale.saveMinY + DHTOpsPanel.this.scale.saveMaxY) / 2.0F;
/* 238 */           DHTOpsPanel.this.scale.minY = (DHTOpsPanel.this.scale.saveMinY + moveFactor * (centerY - DHTOpsPanel.this.scale.saveMinY));
/* 239 */           DHTOpsPanel.this.scale.maxY = (DHTOpsPanel.this.scale.saveMaxY - moveFactor * (DHTOpsPanel.this.scale.saveMaxY - centerY));
/* 240 */           do_refresh = true;
/*     */         }
/*     */         
/* 243 */         if (do_refresh)
/*     */         {
/* 245 */           long now = SystemTime.getMonotonousTime();
/*     */           
/* 247 */           if (now - this.last_refresh >= 250L)
/*     */           {
/* 249 */             this.last_refresh = now;
/*     */             
/* 251 */             DHTOpsPanel.this.refresh();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 256 */     });
/* 257 */     this.canvas.addMouseTrackListener(new MouseTrackListener()
/*     */     {
/*     */       public void mouseHover(MouseEvent e) {}
/*     */       
/*     */       public void mouseExit(MouseEvent e) {
/* 262 */         if (DHTOpsPanel.this.autoAlpha) {
/* 263 */           DHTOpsPanel.this.setAlpha(150);
/*     */         }
/*     */       }
/*     */       
/*     */       public void mouseEnter(MouseEvent e) {
/* 268 */         if (DHTOpsPanel.this.autoAlpha) {
/* 269 */           DHTOpsPanel.this.setAlpha(255);
/*     */         }
/*     */         
/*     */       }
/* 273 */     });
/* 274 */     this.timeout_timer = SimpleTimer.addPeriodicEvent("DHTOps:timer", 30000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 284 */         if (DHTOpsPanel.this.canvas.isDisposed())
/*     */         {
/* 286 */           DHTOpsPanel.this.timeout_timer.cancel();
/*     */           
/* 288 */           return;
/*     */         }
/*     */         
/* 291 */         synchronized (DHTOpsPanel.this.activity_map)
/*     */         {
/* 293 */           Iterator<DHTOpsPanel.ActivityDetail> it = DHTOpsPanel.this.activity_map.values().iterator();
/*     */           
/* 295 */           while (it.hasNext())
/*     */           {
/* 297 */             DHTOpsPanel.ActivityDetail act = (DHTOpsPanel.ActivityDetail)it.next();
/*     */             
/* 299 */             if (DHTOpsPanel.ActivityDetail.access$1100(act))
/*     */             {
/* 301 */               it.remove();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object data) {
/* 310 */     this.canvas.setLayoutData(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void activityChanged(DHTControlActivity activity, int type)
/*     */   {
/* 318 */     if ((this.filter != null) && (!this.filter.accept(activity)))
/*     */     {
/* 320 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 325 */     if (activity.isQueued())
/*     */     {
/*     */ 
/*     */ 
/* 329 */       return;
/*     */     }
/*     */     
/* 332 */     synchronized (this.activity_map)
/*     */     {
/* 334 */       ActivityDetail details = (ActivityDetail)this.activity_map.get(activity);
/*     */       
/* 336 */       if (details == null)
/*     */       {
/* 338 */         details = new ActivityDetail(activity, null);
/*     */         
/* 340 */         this.activity_map.put(activity, details);
/*     */       }
/*     */       
/* 343 */       if (type == 3)
/*     */       {
/* 345 */         details.setComplete();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setUnavailable()
/*     */   {
/* 353 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 359 */         DHTOpsPanel.this.unavailable = true;
/*     */         
/* 361 */         if (!DHTOpsPanel.this.canvas.isDisposed())
/*     */         {
/* 363 */           DHTOpsPanel.this.canvas.redraw();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshView(DHT dht)
/*     */   {
/* 373 */     if (this.current_dht != dht)
/*     */     {
/* 375 */       if (this.current_dht != null)
/*     */       {
/* 377 */         this.current_dht.getControl().removeListener(this);
/*     */       }
/*     */       
/* 380 */       this.current_dht = dht;
/*     */       
/* 382 */       synchronized (this.activity_map)
/*     */       {
/* 384 */         this.activity_map.clear();
/*     */       }
/*     */       
/* 387 */       dht.getControl().addListener(this);
/*     */     }
/*     */     
/* 390 */     refresh();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFilter(ActivityFilter f)
/*     */   {
/* 397 */     this.filter = f;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMinimumSlots(int min)
/*     */   {
/* 408 */     this.min_slots = min;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setScaleAndRotation(float min_x, float max_x, float min_y, float max_y, double rot)
/*     */   {
/* 419 */     this.scale.minX = min_x;
/* 420 */     this.scale.maxX = max_x;
/* 421 */     this.scale.minY = min_y;
/* 422 */     this.scale.maxY = max_y;
/* 423 */     this.scale.rotation = rot;
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 429 */     if (this.canvas.isDisposed())
/*     */     {
/* 431 */       return;
/*     */     }
/*     */     
/* 434 */     Rectangle size = this.canvas.getBounds();
/*     */     
/* 436 */     if ((size.width <= 0) || (size.height <= 0))
/*     */     {
/* 438 */       return;
/*     */     }
/*     */     
/* 441 */     this.scale.width = size.width;
/* 442 */     this.scale.height = size.height;
/*     */     
/* 444 */     if ((this.img != null) && (!this.img.isDisposed()))
/*     */     {
/* 446 */       this.img.dispose();
/*     */     }
/*     */     
/* 449 */     this.img = new Image(this.display, size);
/*     */     
/* 451 */     GC gc = new GC(this.img);
/*     */     
/* 453 */     gc.setAdvanced(true);
/*     */     
/* 455 */     gc.setAntialias(1);
/* 456 */     gc.setTextAntialias(1);
/*     */     
/* 458 */     Color white = ColorCache.getColor(this.display, 255, 255, 255);
/* 459 */     gc.setForeground(white);
/* 460 */     gc.setBackground(white);
/* 461 */     gc.fillRectangle(size);
/*     */     
/*     */ 
/*     */ 
/* 465 */     List<ActivityDetail> to_remove = new ArrayList();
/*     */     List<ActivityDetail> activities;
/* 467 */     synchronized (this.activity_map)
/*     */     {
/* 469 */       activities = new ArrayList(this.activity_map.values());
/*     */     }
/*     */     
/* 472 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 474 */     int max_slot = Math.max(activities.size(), this.min_slots);
/*     */     
/* 476 */     for (ActivityDetail details : activities)
/*     */     {
/* 478 */       max_slot = Math.max(max_slot, details.getSlot() + 1);
/*     */       
/* 480 */       long comp_at = details.getCompleteTime();
/*     */       
/* 482 */       if ((comp_at >= 0L) && (now - comp_at > 10000L))
/*     */       {
/* 484 */         to_remove.add(details);
/*     */       }
/*     */     }
/*     */     
/* 488 */     boolean[] slots_in_use = new boolean[max_slot];
/*     */     
/* 490 */     for (ActivityDetail details : activities)
/*     */     {
/* 492 */       int slot = details.getSlot();
/*     */       
/* 494 */       if (slot != -1)
/*     */       {
/* 496 */         slots_in_use[slot] = true;
/*     */       }
/*     */     }
/*     */     
/* 500 */     int pos = 0;
/*     */     
/* 502 */     for (ActivityDetail details : activities)
/*     */     {
/* 504 */       int slot = details.getSlot();
/*     */       
/* 506 */       if (slot == -1)
/*     */       {
/* 508 */         while (slots_in_use[pos] != 0)
/*     */         {
/* 510 */           pos++;
/*     */         }
/*     */         
/* 513 */         details.setSlot(pos++);
/*     */       }
/*     */     }
/*     */     
/* 517 */     int x_origin = this.scale.getX(0.0F, 0.0F);
/* 518 */     int y_origin = this.scale.getY(0.0F, 0.0F);
/*     */     
/* 520 */     double slice_angle = 6.283185307179586D / max_slot;
/*     */     
/* 522 */     for (ActivityDetail details : activities)
/*     */     {
/* 524 */       details.draw(gc, x_origin, y_origin, slice_angle);
/*     */     }
/*     */     
/* 527 */     gc.setForeground(ColorCache.getColor(gc.getDevice(), 0, 0, 0));
/*     */     
/* 529 */     if (activities.size() == 0)
/*     */     {
/* 531 */       gc.drawText(MessageText.getString("DHTOpsView.idle"), x_origin, y_origin);
/*     */     }
/*     */     else
/*     */     {
/* 535 */       gc.drawLine(x_origin - 5, y_origin, x_origin + 5, y_origin);
/* 536 */       gc.drawLine(x_origin, y_origin - 5, x_origin, y_origin + 5);
/*     */     }
/*     */     
/*     */ 
/* 540 */     gc.dispose();
/*     */     
/* 542 */     this.canvas.redraw();
/*     */     
/* 544 */     if (to_remove.size() > 0)
/*     */     {
/* 546 */       synchronized (this.activity_map)
/*     */       {
/* 548 */         for (ActivityDetail detail : to_remove)
/*     */         {
/* 550 */           this.activity_map.remove(detail.getActivity());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAlpha()
/*     */   {
/* 559 */     return this.alpha;
/*     */   }
/*     */   
/*     */   public void setAlpha(int alpha) {
/* 563 */     this.alpha = alpha;
/* 564 */     if ((this.canvas != null) && (!this.canvas.isDisposed())) {
/* 565 */       this.canvas.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAutoAlpha(boolean autoAlpha) {
/* 570 */     this.autoAlpha = autoAlpha;
/* 571 */     if (autoAlpha) {
/* 572 */       setAlpha(this.canvas.getDisplay().getCursorControl() == this.canvas ? 255 : 150);
/*     */     }
/*     */   }
/*     */   
/*     */   public void delete()
/*     */   {
/* 578 */     if ((this.img != null) && (!this.img.isDisposed()))
/*     */     {
/* 580 */       this.img.dispose();
/*     */     }
/*     */     
/* 583 */     if (this.current_dht != null)
/*     */     {
/* 585 */       this.current_dht.getControl().removeListener(this);
/*     */       
/* 587 */       this.current_dht = null;
/*     */     }
/*     */     
/* 590 */     synchronized (this.activity_map)
/*     */     {
/* 592 */       this.activity_map.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface ActivityFilter { public abstract boolean accept(DHTControlActivity paramDHTControlActivity);
/*     */   }
/*     */   
/*     */   private class ActivityDetail { private DHTControlActivity activity;
/* 600 */     private long complete_time = -1L;
/*     */     
/* 602 */     private int slot = -1;
/*     */     
/* 604 */     private int draw_count = 0;
/* 605 */     private String result_str = "";
/*     */     
/*     */ 
/*     */ 
/*     */     private ActivityDetail(DHTControlActivity _act)
/*     */     {
/* 611 */       this.activity = _act;
/*     */     }
/*     */     
/*     */ 
/*     */     private DHTControlActivity getActivity()
/*     */     {
/* 617 */       return this.activity;
/*     */     }
/*     */     
/*     */ 
/*     */     private void setComplete()
/*     */     {
/* 623 */       this.complete_time = SystemTime.getMonotonousTime();
/*     */     }
/*     */     
/*     */ 
/*     */     private long getCompleteTime()
/*     */     {
/* 629 */       return this.complete_time;
/*     */     }
/*     */     
/*     */ 
/*     */     private boolean isComplete()
/*     */     {
/* 635 */       return (this.complete_time != -1L) && (SystemTime.getMonotonousTime() - this.complete_time > 10000L);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private int getSlot()
/*     */     {
/* 642 */       return this.slot;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void setSlot(int _s)
/*     */     {
/* 649 */       this.slot = _s;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void draw(GC gc, int x_origin, int y_origin, double slice_angle)
/*     */     {
/* 659 */       this.draw_count += 1;
/*     */       
/* 661 */       setColour(gc);
/*     */       
/* 663 */       double angle = slice_angle * this.slot;
/*     */       
/* 665 */       DHTControlActivity.ActivityState state_maybe_null = this.activity.getCurrentState();
/*     */       
/* 667 */       if (state_maybe_null != null)
/*     */       {
/* 669 */         int depth = state_maybe_null.getDepth();
/*     */         
/* 671 */         int level_depth = 750 / depth;
/*     */         
/* 673 */         DHTControlActivity.ActivityNode root = state_maybe_null.getRootNode();
/*     */         
/* 675 */         List<Object[]> level_nodes = new ArrayList();
/*     */         
/* 677 */         float x_start = (float)(50.0D * Math.sin(angle));
/* 678 */         float y_start = (float)(50.0D * Math.cos(angle));
/*     */         
/* 680 */         level_nodes.add(new Object[] { root, Float.valueOf(x_start), Float.valueOf(y_start) });
/*     */         
/* 682 */         int node_distance = 50;
/*     */         
/*     */         for (;;)
/*     */         {
/* 686 */           int nodes_at_next_level = 0;
/*     */           
/* 688 */           for (Object[] entry : level_nodes)
/*     */           {
/* 690 */             nodes_at_next_level += ((DHTControlActivity.ActivityNode)entry[0]).getChildren().size();
/*     */           }
/*     */           
/* 693 */           if (nodes_at_next_level == 0) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 698 */           node_distance += level_depth;
/*     */           
/* 700 */           double node_slice_angle = slice_angle / nodes_at_next_level;
/*     */           
/* 702 */           double current_angle = angle;
/*     */           
/* 704 */           if (nodes_at_next_level > 1)
/*     */           {
/* 706 */             current_angle -= slice_angle / 2.0D;
/*     */             
/* 708 */             current_angle += (slice_angle - node_slice_angle * (nodes_at_next_level - 1)) / 2.0D;
/*     */           }
/*     */           
/* 711 */           List<Object[]> next_level_nodes = new ArrayList();
/*     */           
/* 713 */           for (Object[] entry : level_nodes)
/*     */           {
/* 715 */             DHTControlActivity.ActivityNode node = (DHTControlActivity.ActivityNode)entry[0];
/* 716 */             float node_x = ((Float)entry[1]).floatValue();
/* 717 */             float node_y = ((Float)entry[2]).floatValue();
/*     */             
/* 719 */             seg_start_x = DHTOpsPanel.this.scale.getX(node_x, node_y);
/* 720 */             seg_start_y = DHTOpsPanel.this.scale.getY(node_x, node_y);
/*     */             
/* 722 */             List<DHTControlActivity.ActivityNode> kids = node.getChildren();
/*     */             
/* 724 */             for (DHTControlActivity.ActivityNode kid : kids)
/*     */             {
/* 726 */               float kid_x = (float)(node_distance * Math.sin(current_angle));
/* 727 */               float kid_y = (float)(node_distance * Math.cos(current_angle));
/*     */               
/* 729 */               next_level_nodes.add(new Object[] { kid, Float.valueOf(kid_x), Float.valueOf(kid_y) });
/*     */               
/* 731 */               current_angle += node_slice_angle;
/*     */               
/* 733 */               int seg_end_x = DHTOpsPanel.this.scale.getX(kid_x, kid_y);
/* 734 */               int seg_end_y = DHTOpsPanel.this.scale.getY(kid_x, kid_y);
/*     */               
/* 736 */               gc.drawLine(seg_start_x, seg_start_y, seg_end_x, seg_end_y);
/*     */               
/* 738 */               gc.drawOval(seg_end_x, seg_end_y, 1, 1);
/*     */             } }
/*     */           int seg_start_x;
/*     */           int seg_start_y;
/* 742 */           level_nodes = next_level_nodes;
/*     */         }
/*     */       }
/*     */       
/* 746 */       float x_end = (float)(850.0D * Math.sin(angle));
/* 747 */       float y_end = (float)(850.0D * Math.cos(angle));
/*     */       
/* 749 */       int text_x = DHTOpsPanel.this.scale.getX(x_end, y_end);
/* 750 */       int text_y = DHTOpsPanel.this.scale.getY(x_end, y_end);
/*     */       
/* 752 */       String desc = this.activity.getDescription();
/*     */       
/* 754 */       if ((this.complete_time >= 0L) && (this.result_str.length() == 0))
/*     */       {
/* 756 */         if (state_maybe_null != null)
/*     */         {
/* 758 */           this.result_str = ((desc.length() == 0 ? "" : ": ") + state_maybe_null.getResult());
/*     */         }
/*     */       }
/*     */       
/* 762 */       gc.drawText(desc + this.result_str, text_x, text_y);
/*     */       
/*     */ 
/*     */ 
/* 766 */       gc.setAlpha(255);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void setColour(GC gc)
/*     */     {
/* 773 */       if ((this.complete_time != -1L) && (this.draw_count > 1))
/*     */       {
/* 775 */         int age = (int)(SystemTime.getMonotonousTime() - this.complete_time);
/*     */         
/* 777 */         gc.setAlpha(Math.max(0, 200 - 255 * age / 10000));
/*     */         
/* 779 */         gc.setForeground(ColorCache.getColor(gc.getDevice(), 0, 0, 0));
/*     */       }
/*     */       else
/*     */       {
/* 783 */         gc.setAlpha(255);
/*     */         
/* 785 */         int type = this.activity.getType();
/*     */         
/* 787 */         if (type == 2)
/*     */         {
/* 789 */           gc.setForeground(ColorCache.getColor(gc.getDevice(), 20, 200, 20));
/*     */         }
/* 791 */         else if (type == 1)
/*     */         {
/* 793 */           gc.setForeground(ColorCache.getColor(gc.getDevice(), 140, 160, 40));
/*     */         }
/* 795 */         else if (type == 4)
/*     */         {
/* 797 */           gc.setForeground(ColorCache.getColor(gc.getDevice(), 20, 20, 220));
/*     */         }
/*     */         else
/*     */         {
/* 801 */           gc.setForeground(ColorCache.getColor(gc.getDevice(), 40, 140, 160));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/DHTOpsPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */