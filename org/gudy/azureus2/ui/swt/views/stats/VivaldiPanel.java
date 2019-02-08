/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlContact;
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.VivaldiPosition;
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.HeightCoordinatesImpl;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.MouseTrackAdapter;
/*     */ import org.eclipse.swt.events.MouseTrackListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.FontMetrics;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
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
/*     */ public class VivaldiPanel
/*     */ {
/*     */   private static final int ALPHA_FOCUS = 255;
/*     */   private static final int ALPHA_NOFOCUS = 150;
/*     */   Display display;
/*     */   Composite parent;
/*     */   Canvas canvas;
/*     */   Scale scale;
/*  56 */   private boolean mouseLeftDown = false;
/*  57 */   private boolean mouseRightDown = false;
/*     */   
/*     */   private int xDown;
/*     */   private int yDown;
/*  61 */   private boolean disableAutoScale = false;
/*  62 */   private long lastAutoScale = 0L;
/*     */   
/*  64 */   private boolean antiAliasingAvailable = true;
/*     */   
/*     */   private List<DHTControlContact> lastContacts;
/*     */   
/*     */   private DHTTransportContact lastSelf;
/*     */   private Image img;
/*  70 */   private int alpha = 255;
/*     */   
/*  72 */   private boolean autoAlpha = false;
/*     */   
/*  74 */   private List<Object[]> currentPositions = new ArrayList();
/*     */   
/*     */ 
/*     */   private class Scale
/*     */   {
/*     */     int width;
/*     */     
/*     */     int height;
/*     */     float minX;
/*     */     float maxX;
/*     */     float minY;
/*     */     float maxY;
/*     */     double rotation;
/*     */     float saveMinX;
/*     */     float saveMaxX;
/*     */     float saveMinY;
/*     */     float saveMaxY;
/*     */     double saveRotation;
/*     */     
/*     */     public Scale()
/*     */     {
/*  95 */       reset();
/*     */     }
/*     */     
/*     */     public int getX(float x, float y) {
/*  99 */       return (int)((x * Math.cos(this.rotation) + y * Math.sin(this.rotation) - this.minX) / (this.maxX - this.minX) * this.width);
/*     */     }
/*     */     
/*     */     public int getY(float x, float y) {
/* 103 */       return (int)((y * Math.cos(this.rotation) - x * Math.sin(this.rotation) - this.minY) / (this.maxY - this.minY) * this.height);
/*     */     }
/*     */     
/*     */ 
/*     */     public void reset()
/*     */     {
/* 109 */       this.minX = -1000.0F;
/* 110 */       this.maxX = 1000.0F;
/* 111 */       this.minY = -1000.0F;
/* 112 */       this.maxY = 1000.0F;
/* 113 */       this.rotation = 0.0D;
/*     */       
/* 115 */       VivaldiPanel.this.disableAutoScale = false;
/* 116 */       VivaldiPanel.this.lastAutoScale = 0L;
/*     */     }
/*     */   }
/*     */   
/*     */   public VivaldiPanel(Composite parent) {
/* 121 */     this.parent = parent;
/* 122 */     this.display = parent.getDisplay();
/* 123 */     this.canvas = new Canvas(parent, 262144);
/*     */     
/* 125 */     this.scale = new Scale();
/*     */     
/* 127 */     this.canvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 129 */         if ((VivaldiPanel.this.img != null) && (!VivaldiPanel.this.img.isDisposed())) {
/* 130 */           Rectangle bounds = VivaldiPanel.this.img.getBounds();
/* 131 */           if ((bounds.width >= e.width) && (bounds.height >= e.height)) {
/* 132 */             if (VivaldiPanel.this.alpha != 255) {
/*     */               try {
/* 134 */                 e.gc.setAlpha(VivaldiPanel.this.alpha);
/*     */               }
/*     */               catch (Exception ex) {}
/*     */             }
/*     */             
/* 139 */             e.gc.drawImage(VivaldiPanel.this.img, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
/*     */           }
/*     */         }
/*     */         else {
/* 143 */           e.gc.setBackground(VivaldiPanel.this.display.getSystemColor(22));
/* 144 */           e.gc.fillRectangle(e.x, e.y, e.width, e.height);
/* 145 */           e.gc.drawText(MessageText.getString("VivaldiView.notAvailable"), 10, 10, true);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 150 */     });
/* 151 */     this.canvas.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseDown(MouseEvent event) {
/* 154 */         if (event.button == 1) VivaldiPanel.this.mouseLeftDown = true;
/* 155 */         if (event.button == 3) VivaldiPanel.this.mouseRightDown = true;
/* 156 */         VivaldiPanel.this.xDown = event.x;
/* 157 */         VivaldiPanel.this.yDown = event.y;
/* 158 */         VivaldiPanel.this.scale.saveMinX = VivaldiPanel.this.scale.minX;
/* 159 */         VivaldiPanel.this.scale.saveMaxX = VivaldiPanel.this.scale.maxX;
/* 160 */         VivaldiPanel.this.scale.saveMinY = VivaldiPanel.this.scale.minY;
/* 161 */         VivaldiPanel.this.scale.saveMaxY = VivaldiPanel.this.scale.maxY;
/* 162 */         VivaldiPanel.this.scale.saveRotation = VivaldiPanel.this.scale.rotation;
/*     */       }
/*     */       
/*     */       public void mouseUp(MouseEvent event) {
/* 166 */         if (event.button == 1) VivaldiPanel.this.mouseLeftDown = false;
/* 167 */         if (event.button == 3) VivaldiPanel.this.mouseRightDown = false;
/* 168 */         VivaldiPanel.this.refreshContacts(VivaldiPanel.this.lastContacts, VivaldiPanel.this.lastSelf);
/*     */       }
/*     */       
/*     */       public void mouseDoubleClick(MouseEvent e)
/*     */       {
/* 173 */         VivaldiPanel.this.scale.reset();
/*     */       }
/*     */       
/* 176 */     });
/* 177 */     this.canvas.addMouseTrackListener(new MouseTrackAdapter()
/*     */     {
/*     */       public void mouseHover(MouseEvent e) {
/* 180 */         int x = e.x;
/* 181 */         int y = e.y;
/*     */         
/* 183 */         DHTControlContact closest = null;
/*     */         
/* 185 */         int closest_distance = Integer.MAX_VALUE;
/* 186 */         float height = -1.0F;
/*     */         
/* 188 */         for (Object[] entry : VivaldiPanel.this.currentPositions)
/*     */         {
/* 190 */           int e_x = ((Integer)entry[0]).intValue();
/* 191 */           int e_y = ((Integer)entry[1]).intValue();
/*     */           
/* 193 */           long x_diff = x - e_x;
/* 194 */           long y_diff = y - e_y;
/*     */           
/* 196 */           int distance = (int)Math.sqrt(x_diff * x_diff + y_diff * y_diff);
/*     */           
/* 198 */           if (distance < closest_distance)
/*     */           {
/* 200 */             closest_distance = distance;
/* 201 */             height = ((Float)entry[2]).floatValue();
/* 202 */             closest = (DHTControlContact)entry[3];
/*     */           }
/*     */         }
/*     */         
/* 206 */         if (closest_distance <= 25)
/*     */         {
/* 208 */           InetAddress address = closest.getTransportContact().getTransportAddress().getAddress();
/*     */           
/* 210 */           String[] details = PeerUtils.getCountryDetails(address);
/*     */           
/* 212 */           String tt = address.getHostAddress();
/*     */           
/* 214 */           if (details != null)
/*     */           {
/* 216 */             tt = tt + ": " + details[0] + "/" + details[1];
/*     */           }
/*     */           
/* 219 */           tt = tt + " (h=" + (int)(height * 10000.0F) / 10000.0F + ")";
/*     */           
/* 221 */           VivaldiPanel.this.canvas.setToolTipText(tt);
/*     */         }
/*     */         else
/*     */         {
/* 225 */           VivaldiPanel.this.canvas.setToolTipText("Use mouse wheel to scale, left+drag to move, right+drag to rotate");
/*     */         }
/*     */       }
/* 228 */     });
/* 229 */     this.canvas.addListener(1, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {}
/*     */ 
/* 233 */     });
/* 234 */     this.canvas.addListener(37, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 237 */         VivaldiPanel.this.scale.saveMinX = VivaldiPanel.this.scale.minX;
/* 238 */         VivaldiPanel.this.scale.saveMaxX = VivaldiPanel.this.scale.maxX;
/* 239 */         VivaldiPanel.this.scale.saveMinY = VivaldiPanel.this.scale.minY;
/* 240 */         VivaldiPanel.this.scale.saveMaxY = VivaldiPanel.this.scale.maxY;
/*     */         
/* 242 */         int deltaY = event.count * 5;
/*     */         
/*     */ 
/* 245 */         float scaleFactor = 1.0F - deltaY / 300.0F;
/* 246 */         if (scaleFactor <= 0.0F) { scaleFactor = 0.01F;
/*     */         }
/*     */         
/* 249 */         float moveFactor = 1.0F - 1.0F / scaleFactor;
/*     */         
/* 251 */         float centerX = (VivaldiPanel.this.scale.saveMinX + VivaldiPanel.this.scale.saveMaxX) / 2.0F;
/* 252 */         VivaldiPanel.this.scale.minX = (VivaldiPanel.this.scale.saveMinX + moveFactor * (centerX - VivaldiPanel.this.scale.saveMinX));
/* 253 */         VivaldiPanel.this.scale.maxX = (VivaldiPanel.this.scale.saveMaxX - moveFactor * (VivaldiPanel.this.scale.saveMaxX - centerX));
/*     */         
/* 255 */         float centerY = (VivaldiPanel.this.scale.saveMinY + VivaldiPanel.this.scale.saveMaxY) / 2.0F;
/* 256 */         VivaldiPanel.this.scale.minY = (VivaldiPanel.this.scale.saveMinY + moveFactor * (centerY - VivaldiPanel.this.scale.saveMinY));
/* 257 */         VivaldiPanel.this.scale.maxY = (VivaldiPanel.this.scale.saveMaxY - moveFactor * (VivaldiPanel.this.scale.saveMaxY - centerY));
/*     */         
/* 259 */         VivaldiPanel.this.disableAutoScale = true;
/* 260 */         VivaldiPanel.this.refreshContacts(VivaldiPanel.this.lastContacts, VivaldiPanel.this.lastSelf);
/*     */       }
/*     */       
/* 263 */     });
/* 264 */     this.canvas.addMouseMoveListener(new MouseMoveListener() {
/*     */       public void mouseMove(MouseEvent event) {
/* 266 */         if ((VivaldiPanel.this.mouseLeftDown) && ((event.stateMask & SWT.MOD4) == 0)) {
/* 267 */           int deltaX = event.x - VivaldiPanel.this.xDown;
/* 268 */           int deltaY = event.y - VivaldiPanel.this.yDown;
/* 269 */           float width = VivaldiPanel.this.scale.width;
/* 270 */           float height = VivaldiPanel.this.scale.height;
/* 271 */           float ratioX = (VivaldiPanel.this.scale.saveMaxX - VivaldiPanel.this.scale.saveMinX) / width;
/* 272 */           float ratioY = (VivaldiPanel.this.scale.saveMaxY - VivaldiPanel.this.scale.saveMinY) / height;
/* 273 */           float realDeltaX = deltaX * ratioX;
/* 274 */           float realDeltaY = deltaY * ratioY;
/* 275 */           VivaldiPanel.this.scale.minX = (VivaldiPanel.this.scale.saveMinX - realDeltaX);
/* 276 */           VivaldiPanel.this.scale.maxX = (VivaldiPanel.this.scale.saveMaxX - realDeltaX);
/* 277 */           VivaldiPanel.this.scale.minY = (VivaldiPanel.this.scale.saveMinY - realDeltaY);
/* 278 */           VivaldiPanel.this.scale.maxY = (VivaldiPanel.this.scale.saveMaxY - realDeltaY);
/* 279 */           VivaldiPanel.this.disableAutoScale = true;
/* 280 */           VivaldiPanel.this.refreshContacts(VivaldiPanel.this.lastContacts, VivaldiPanel.this.lastSelf);
/*     */         }
/* 282 */         if ((VivaldiPanel.this.mouseRightDown) || ((VivaldiPanel.this.mouseLeftDown) && ((event.stateMask & SWT.MOD4) > 0))) {
/* 283 */           int deltaX = event.x - VivaldiPanel.this.xDown;
/* 284 */           VivaldiPanel.this.scale.rotation = (VivaldiPanel.this.scale.saveRotation - deltaX / 100.0F);
/*     */           
/* 286 */           int deltaY = event.y - VivaldiPanel.this.yDown;
/*     */           
/*     */ 
/* 289 */           float scaleFactor = 1.0F - deltaY / 300.0F;
/* 290 */           if (scaleFactor <= 0.0F) { scaleFactor = 0.01F;
/*     */           }
/*     */           
/* 293 */           float moveFactor = 1.0F - 1.0F / scaleFactor;
/*     */           
/* 295 */           float centerX = (VivaldiPanel.this.scale.saveMinX + VivaldiPanel.this.scale.saveMaxX) / 2.0F;
/* 296 */           VivaldiPanel.this.scale.minX = (VivaldiPanel.this.scale.saveMinX + moveFactor * (centerX - VivaldiPanel.this.scale.saveMinX));
/* 297 */           VivaldiPanel.this.scale.maxX = (VivaldiPanel.this.scale.saveMaxX - moveFactor * (VivaldiPanel.this.scale.saveMaxX - centerX));
/*     */           
/* 299 */           float centerY = (VivaldiPanel.this.scale.saveMinY + VivaldiPanel.this.scale.saveMaxY) / 2.0F;
/* 300 */           VivaldiPanel.this.scale.minY = (VivaldiPanel.this.scale.saveMinY + moveFactor * (centerY - VivaldiPanel.this.scale.saveMinY));
/* 301 */           VivaldiPanel.this.scale.maxY = (VivaldiPanel.this.scale.saveMaxY - moveFactor * (VivaldiPanel.this.scale.saveMaxY - centerY));
/* 302 */           VivaldiPanel.this.disableAutoScale = true;
/* 303 */           VivaldiPanel.this.refreshContacts(VivaldiPanel.this.lastContacts, VivaldiPanel.this.lastSelf);
/*     */         }
/*     */         
/*     */       }
/* 307 */     });
/* 308 */     this.canvas.addMouseTrackListener(new MouseTrackListener()
/*     */     {
/*     */       public void mouseHover(MouseEvent e) {}
/*     */       
/*     */       public void mouseExit(MouseEvent e) {
/* 313 */         if (VivaldiPanel.this.autoAlpha) {
/* 314 */           VivaldiPanel.this.setAlpha(150);
/*     */         }
/*     */       }
/*     */       
/*     */       public void mouseEnter(MouseEvent e) {
/* 319 */         if (VivaldiPanel.this.autoAlpha) {
/* 320 */           VivaldiPanel.this.setAlpha(255);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object data) {
/* 327 */     this.canvas.setLayoutData(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refreshContacts(List<DHTControlContact> contacts, DHTTransportContact self)
/*     */   {
/* 335 */     if ((contacts == null) || (self == null)) {
/* 336 */       return;
/*     */     }
/* 338 */     this.lastContacts = contacts;
/* 339 */     this.lastSelf = self;
/*     */     
/* 341 */     if (this.canvas.isDisposed()) return;
/* 342 */     Rectangle size = this.canvas.getBounds();
/*     */     
/* 344 */     if (size.isEmpty()) {
/* 345 */       return;
/*     */     }
/*     */     
/* 348 */     this.scale.width = size.width;
/* 349 */     this.scale.height = size.height;
/*     */     
/* 351 */     Color white = ColorCache.getColor(this.display, 255, 255, 255);
/* 352 */     Color blue = ColorCache.getColor(this.display, 66, 87, 104);
/*     */     
/* 354 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 355 */       this.img.dispose();
/*     */     }
/*     */     
/* 358 */     this.img = new Image(this.display, size);
/*     */     
/* 360 */     GC gc = new GC(this.img);
/*     */     
/* 362 */     gc.setForeground(white);
/* 363 */     gc.setBackground(white);
/*     */     
/* 365 */     gc.fillRectangle(size);
/*     */     
/* 367 */     if ((SWT.getVersion() >= 3138) && (!this.antiAliasingAvailable)) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 377 */     gc.setForeground(blue);
/* 378 */     gc.setBackground(white);
/*     */     
/* 380 */     DHTNetworkPosition _ownPosition = self.getNetworkPosition((byte)1);
/*     */     
/* 382 */     if (_ownPosition == null)
/*     */     {
/* 384 */       gc.dispose();
/*     */       
/* 386 */       return;
/*     */     }
/*     */     
/* 389 */     this.currentPositions.clear();
/*     */     
/* 391 */     VivaldiPosition ownPosition = (VivaldiPosition)_ownPosition;
/* 392 */     float ownErrorEstimate = ownPosition.getErrorEstimate();
/* 393 */     HeightCoordinatesImpl ownCoords = (HeightCoordinatesImpl)ownPosition.getCoordinates();
/*     */     
/*     */ 
/* 396 */     gc.drawText("Our error: " + ownErrorEstimate, 10, 10);
/*     */     
/* 398 */     Color black = ColorCache.getColor(this.display, 0, 0, 0);
/* 399 */     gc.setBackground(black);
/*     */     
/*     */ 
/*     */ 
/* 403 */     long total_distance = 0L;
/*     */     
/* 405 */     for (DHTControlContact contact : contacts) {
/* 406 */       DHTNetworkPosition _position = contact.getTransportContact().getNetworkPosition((byte)1);
/* 407 */       if (_position != null)
/*     */       {
/*     */ 
/* 410 */         VivaldiPosition position = (VivaldiPosition)_position;
/* 411 */         HeightCoordinatesImpl coord = (HeightCoordinatesImpl)position.getCoordinates();
/* 412 */         if (coord.isValid()) {
/* 413 */           int distance = (int)ownCoords.distance(coord);
/* 414 */           total_distance += distance;
/*     */           
/* 416 */           draw(gc, coord.getX(), coord.getY(), coord.getH(), contact, distance, position.getErrorEstimate());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 421 */     Color red = ColorCache.getColor(this.display, 255, 0, 0);
/* 422 */     gc.setForeground(red);
/* 423 */     drawSelf(gc, ownCoords.getX(), ownCoords.getY(), ownCoords.getH(), ownErrorEstimate);
/*     */     
/*     */ 
/*     */ 
/* 427 */     gc.dispose();
/*     */     
/* 429 */     boolean skip_redraw = false;
/*     */     
/* 431 */     if (!this.disableAutoScale)
/*     */     {
/* 433 */       int num_pos = this.currentPositions.size();
/*     */       
/* 435 */       if (num_pos > 0)
/*     */       {
/* 437 */         long now = SystemTime.getMonotonousTime();
/*     */         
/* 439 */         if (now - this.lastAutoScale >= 5000L)
/*     */         {
/* 441 */           this.lastAutoScale = now;
/*     */           
/* 443 */           float min_x = Float.MAX_VALUE;
/* 444 */           float min_y = Float.MAX_VALUE;
/* 445 */           float max_x = Float.MIN_VALUE;
/* 446 */           float max_y = Float.MIN_VALUE;
/*     */           
/* 448 */           int average_distance = (int)(total_distance / num_pos);
/*     */           
/* 450 */           for (Object[] entry : this.currentPositions)
/*     */           {
/* 452 */             if (num_pos > 25)
/*     */             {
/* 454 */               int distance = ((Integer)entry[6]).intValue();
/*     */               
/* 456 */               if (distance >= average_distance * 4) {}
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 462 */               float x = ((Float)entry[4]).floatValue();
/* 463 */               float y = ((Float)entry[5]).floatValue();
/*     */               
/* 465 */               min_x = Math.min(min_x, x);
/* 466 */               min_y = Math.min(min_y, y);
/* 467 */               max_x = Math.max(max_x, x);
/* 468 */               max_y = Math.max(max_y, y);
/*     */             }
/*     */           }
/* 471 */           float new_min_x = min_x - 50.0F;
/* 472 */           float new_max_x = max_x + 50.0F;
/* 473 */           float new_min_y = min_y - 50.0F;
/* 474 */           float new_max_y = max_y + 50.0F;
/*     */           
/* 476 */           if ((this.scale.minX != new_min_x) || (this.scale.maxX != new_max_x) || (this.scale.minY != new_min_y) || (this.scale.maxY != new_max_y))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 481 */             this.scale.minX = new_min_x;
/* 482 */             this.scale.maxX = new_max_x;
/* 483 */             this.scale.minY = new_min_y;
/* 484 */             this.scale.maxY = new_max_y;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 489 */           refreshContacts(contacts, self);
/*     */           
/* 491 */           skip_redraw = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 496 */     if (!skip_redraw)
/*     */     {
/* 498 */       this.canvas.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh(List<VivaldiPosition> vivaldiPositions) {
/* 503 */     if (this.canvas.isDisposed()) return;
/* 504 */     Rectangle size = this.canvas.getBounds();
/*     */     
/* 506 */     this.scale.width = size.width;
/* 507 */     this.scale.height = size.height;
/*     */     
/* 509 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 510 */       this.img.dispose();
/*     */     }
/*     */     
/* 513 */     this.img = new Image(this.display, size);
/* 514 */     GC gc = new GC(this.img);
/*     */     
/* 516 */     Color white = ColorCache.getColor(this.display, 255, 255, 255);
/* 517 */     gc.setForeground(white);
/* 518 */     gc.setBackground(white);
/* 519 */     gc.fillRectangle(size);
/*     */     
/* 521 */     Color blue = ColorCache.getColor(this.display, 66, 87, 104);
/* 522 */     gc.setForeground(blue);
/* 523 */     gc.setBackground(blue);
/*     */     
/*     */ 
/*     */ 
/* 527 */     for (VivaldiPosition position : vivaldiPositions) {
/* 528 */       HeightCoordinatesImpl coord = (HeightCoordinatesImpl)position.getCoordinates();
/*     */       
/* 530 */       float error = position.getErrorEstimate() - 0.1F;
/* 531 */       if (error < 0.0F) error = 0.0F;
/* 532 */       if (error > 1.0F) error = 1.0F;
/* 533 */       int blueComponent = (int)(255.0F - error * 255.0F);
/* 534 */       int redComponent = (int)(255.0F * error);
/*     */       
/*     */ 
/* 537 */       Color drawColor = new Color(this.display, redComponent, 50, blueComponent);
/* 538 */       gc.setForeground(drawColor);
/* 539 */       draw(gc, coord.getX(), coord.getY(), coord.getH());
/* 540 */       drawColor.dispose();
/*     */     }
/*     */     
/* 543 */     gc.dispose();
/*     */     
/* 545 */     this.canvas.redraw();
/*     */   }
/*     */   
/*     */   private void draw(GC gc, float x, float y, float h) {
/* 549 */     int x0 = this.scale.getX(x, y);
/* 550 */     int y0 = this.scale.getY(x, y);
/* 551 */     gc.fillRectangle(x0 - 1, y0 - 1, 3, 3);
/* 552 */     gc.drawLine(x0, y0, x0, (int)(y0 - 200.0F * h / (this.scale.maxY - this.scale.minY)));
/*     */   }
/*     */   
/*     */   private void draw(GC gc, float x, float y, float h, DHTControlContact contact, int distance, float error) {
/* 556 */     if ((x == 0.0F) && (y == 0.0F)) return;
/* 557 */     if (error > 1.0F) error = 1.0F;
/* 558 */     int errDisplay = (int)(100.0F * error);
/* 559 */     int x0 = this.scale.getX(x, y);
/* 560 */     int y0 = this.scale.getY(x, y);
/*     */     
/* 562 */     Image img = ImageRepository.getCountryFlag(contact.getTransportContact().getTransportAddress().getAddress(), true);
/*     */     
/* 564 */     if (img != null) {
/* 565 */       Rectangle bounds = img.getBounds();
/* 566 */       int old = gc.getAlpha();
/* 567 */       gc.setAlpha(150);
/* 568 */       gc.drawImage(img, x0 - bounds.width / 2, y0 - bounds.height);
/* 569 */       gc.setAlpha(old);
/*     */     }
/*     */     
/* 572 */     gc.fillRectangle(x0 - 1, y0 - 1, 3, 3);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 577 */     String text = distance + " ms " + errDisplay + "%";
/*     */     
/* 579 */     int lineReturn = text.indexOf("\n");
/* 580 */     int xOffset = gc.getFontMetrics().getAverageCharWidth() * (lineReturn != -1 ? lineReturn : text.length()) / 2;
/* 581 */     gc.drawText(text, x0 - xOffset, y0, true);
/*     */     
/* 583 */     this.currentPositions.add(new Object[] { Integer.valueOf(x0), Integer.valueOf(y0), Float.valueOf(h), contact, Float.valueOf(x), Float.valueOf(y), Integer.valueOf(distance) });
/*     */   }
/*     */   
/*     */   private void drawSelf(GC gc, float x, float y, float h, float errorEstimate)
/*     */   {
/* 588 */     int x0 = this.scale.getX(x, y);
/* 589 */     int y0 = this.scale.getY(x, y);
/*     */     
/* 591 */     gc.drawLine(x0 - 15, y0, x0 + 15, y0);
/* 592 */     gc.drawLine(x0, y0 - 15, x0, y0 + 15);
/*     */   }
/*     */   
/*     */   public int getAlpha() {
/* 596 */     return this.alpha;
/*     */   }
/*     */   
/*     */   public void setAlpha(int alpha) {
/* 600 */     this.alpha = alpha;
/* 601 */     if ((this.canvas != null) && (!this.canvas.isDisposed())) {
/* 602 */       this.canvas.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAutoAlpha(boolean autoAlpha) {
/* 607 */     this.autoAlpha = autoAlpha;
/* 608 */     if (autoAlpha) {
/* 609 */       setAlpha(this.canvas.getDisplay().getCursorControl() == this.canvas ? 255 : 150);
/*     */     }
/*     */   }
/*     */   
/*     */   public void delete()
/*     */   {
/* 615 */     if ((this.img != null) && (!this.img.isDisposed()))
/*     */     {
/* 617 */       this.img.dispose();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/VivaldiPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */