/*     */ package org.gudy.azureus2.ui.swt.shells;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.MouseTrackListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpeedScaleShell
/*     */ {
/*     */   private static final boolean MOUSE_ONLY_UP_EXITS = true;
/*  43 */   private int OPTION_HEIGHT = 15;
/*     */   
/*  45 */   private int TEXT_HEIGHT = 32;
/*     */   
/*  47 */   private int SCALER_HEIGHT = 20;
/*     */   
/*  49 */   private int HEIGHT = this.TEXT_HEIGHT + this.SCALER_HEIGHT;
/*     */   
/*  51 */   private int MIN_WIDTH = 130;
/*     */   
/*  53 */   private int PADDING_X0 = 10;
/*     */   
/*  55 */   private int PADDING_X1 = 10;
/*     */   
/*  57 */   private int MARKER_HEIGHT = 10;
/*     */   
/*  59 */   private int MARKER_WIDTH = 5;
/*     */   
/*  61 */   private int PX_5 = 5;
/*     */   
/*  63 */   private int PX_2 = 2;
/*     */   
/*  65 */   private int PX_10 = 10;
/*     */   
/*     */   private static final int TYPED_TEXT_ALPHA = 80;
/*     */   
/*     */   private static final long CLOSE_DELAY = 600L;
/*     */   
/*     */   private int WIDTH;
/*     */   
/*     */   private int WIDTH_NO_PADDING;
/*     */   
/*     */   private int value;
/*     */   
/*     */   private boolean cancelled;
/*     */   
/*     */   private int minValue;
/*     */   
/*     */   private int maxValue;
/*     */   
/*     */   private int maxTextValue;
/*     */   
/*     */   private int pageIncrement;
/*     */   
/*     */   private int bigPageIncrement;
/*     */   
/*     */   private Shell shell;
/*     */   
/*     */   private Shell parentShell;
/*     */   
/*  93 */   private LinkedHashMap mapOptions = new LinkedHashMap();
/*     */   
/*  95 */   private String sValue = "";
/*     */   
/*     */   private Composite composite;
/*     */   
/*     */   private boolean menuChosen;
/*     */   
/*     */   protected boolean lastMoveHadMouseDown;
/*     */   
/*     */   private boolean assumeInitiallyDown;
/*     */   
/* 105 */   private TimerEventPerformer cursorBlinkPerformer = null;
/*     */   
/* 107 */   private TimerEvent cursorBlinkEvent = null;
/*     */   
/*     */   public SpeedScaleShell() {
/* 110 */     this.minValue = 0;
/* 111 */     this.maxValue = -1;
/* 112 */     this.maxTextValue = -1;
/* 113 */     this.pageIncrement = 10;
/* 114 */     this.bigPageIncrement = 100;
/* 115 */     this.cancelled = true;
/* 116 */     this.menuChosen = false;
/*     */     
/* 118 */     this.OPTION_HEIGHT = Utils.adjustPXForDPI(this.OPTION_HEIGHT);
/* 119 */     this.TEXT_HEIGHT = Utils.adjustPXForDPI(this.TEXT_HEIGHT);
/* 120 */     this.SCALER_HEIGHT = Utils.adjustPXForDPI(this.SCALER_HEIGHT);
/* 121 */     this.HEIGHT = Utils.adjustPXForDPI(this.HEIGHT);
/* 122 */     this.MIN_WIDTH = Utils.adjustPXForDPI(this.MIN_WIDTH);
/* 123 */     this.PADDING_X0 = Utils.adjustPXForDPI(this.PADDING_X0);
/* 124 */     this.PADDING_X1 = Utils.adjustPXForDPI(this.PADDING_X1);
/* 125 */     this.MARKER_HEIGHT = Utils.adjustPXForDPI(this.MARKER_HEIGHT);
/* 126 */     this.MARKER_WIDTH = Utils.adjustPXForDPI(this.MARKER_WIDTH);
/* 127 */     this.PX_2 = Utils.adjustPXForDPI(this.PX_2);
/* 128 */     this.PX_5 = Utils.adjustPXForDPI(this.PX_5);
/* 129 */     this.PX_10 = Utils.adjustPXForDPI(this.PX_10);
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
/*     */ 
/*     */ 
/*     */   public boolean open(final Control cClickedFrom, final int startValue, boolean _assumeInitiallyDown)
/*     */   {
/* 144 */     this.value = startValue;
/* 145 */     this.assumeInitiallyDown = _assumeInitiallyDown;
/* 146 */     if (this.assumeInitiallyDown) {
/* 147 */       this.lastMoveHadMouseDown = true;
/*     */     }
/* 149 */     this.cancelled = true;
/*     */     
/* 151 */     this.shell = new Shell(this.parentShell == null ? Utils.findAnyShell() : this.parentShell, 536887296);
/*     */     
/* 153 */     this.shell.setLayout(new FillLayout());
/* 154 */     final Display display = this.shell.getDisplay();
/*     */     
/* 156 */     this.composite = new Composite(this.shell, 536870912);
/*     */     
/* 158 */     GC gc = new GC(this.composite);
/* 159 */     gc.setAntialias(1);
/* 160 */     this.WIDTH = this.MIN_WIDTH;
/* 161 */     Rectangle r = new Rectangle(0, 0, 9999, 20);
/* 162 */     for (Iterator iter = this.mapOptions.keySet().iterator(); iter.hasNext();) {
/* 163 */       Integer value = (Integer)iter.next();
/* 164 */       String text = (String)this.mapOptions.get(value);
/*     */       
/* 166 */       String s = getStringValue(value.intValue(), text);
/* 167 */       GCStringPrinter stringPrinter = new GCStringPrinter(gc, s, r, 0, 0);
/* 168 */       stringPrinter.calculateMetrics();
/* 169 */       Point size = stringPrinter.getCalculatedSize(); Point 
/* 170 */         tmp234_232 = size;tmp234_232.x = ((int)(tmp234_232.x * 1.1D));
/*     */       
/* 172 */       if (this.WIDTH < size.x) {
/* 173 */         this.WIDTH = size.x;
/*     */       }
/*     */     }
/* 176 */     gc.dispose();
/* 177 */     this.WIDTH_NO_PADDING = (this.WIDTH - this.PADDING_X0 - this.PADDING_X1);
/*     */     
/* 179 */     final Point firstMousePos = display.getCursorLocation();
/*     */     
/* 181 */     this.composite.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 183 */         if (e.detail == 2) {
/* 184 */           SpeedScaleShell.this.setCancelled(true);
/* 185 */           SpeedScaleShell.this.shell.dispose();
/* 186 */         } else if (e.detail == 64) {
/* 187 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.value + 1);
/* 188 */         } else if (e.detail == 32) {
/* 189 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.value - 1);
/* 190 */         } else if (e.detail == 512) {
/* 191 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.value + SpeedScaleShell.this.bigPageIncrement);
/* 192 */         } else if (e.detail == 256) {
/* 193 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.value - SpeedScaleShell.this.bigPageIncrement);
/* 194 */         } else if (e.detail == 4) {
/* 195 */           SpeedScaleShell.this.setCancelled(false);
/* 196 */           SpeedScaleShell.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/* 200 */     });
/* 201 */     this.composite.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {}
/*     */       
/*     */       public void keyPressed(KeyEvent e) {
/* 206 */         if ((e.keyCode == 16777222) && (e.stateMask == 0)) {
/* 207 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.value + SpeedScaleShell.this.pageIncrement);
/* 208 */         } else if ((e.keyCode == 16777221) && (e.stateMask == 0)) {
/* 209 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.value - SpeedScaleShell.this.pageIncrement);
/* 210 */         } else if (e.keyCode == 16777223) {
/* 211 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.minValue);
/* 212 */         } else if ((e.keyCode == 16777224) && 
/* 213 */           (SpeedScaleShell.this.maxValue != -1)) {
/* 214 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.maxValue);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 219 */     });
/* 220 */     final MouseMoveListener mouseMoveListener = new MouseMoveListener() {
/*     */       public void mouseMove(MouseEvent e) {
/* 222 */         Point ptOnDisplay = ((Control)e.widget).toDisplay(e.x, e.y);
/* 223 */         Point ptOnComposite = SpeedScaleShell.this.composite.toControl(ptOnDisplay);
/* 224 */         SpeedScaleShell.this.lastMoveHadMouseDown = false;
/* 225 */         boolean hasButtonDown = ((e.stateMask & SWT.BUTTON_MASK) > 0) || (SpeedScaleShell.this.assumeInitiallyDown);
/*     */         
/* 227 */         if (hasButtonDown) {
/* 228 */           if (ptOnComposite.y > SpeedScaleShell.this.HEIGHT - SpeedScaleShell.this.SCALER_HEIGHT) {
/* 229 */             SpeedScaleShell.this.lastMoveHadMouseDown = true;
/* 230 */             SpeedScaleShell.this.setValue(SpeedScaleShell.this.getValueFromMousePos(ptOnComposite.x));
/*     */           }
/* 232 */           SpeedScaleShell.this.composite.redraw();
/*     */         } else {
/* 234 */           SpeedScaleShell.this.composite.redraw();
/*     */         }
/*     */         
/*     */       }
/* 238 */     };
/* 239 */     this.composite.addMouseMoveListener(mouseMoveListener);
/*     */     
/* 241 */     this.composite.addMouseTrackListener(new MouseTrackListener() {
/* 242 */       boolean mouseIsOut = false;
/*     */       
/* 244 */       private boolean exitCancelled = false;
/*     */       
/*     */       public void mouseHover(MouseEvent e) {}
/*     */       
/*     */       public void mouseExit(MouseEvent e)
/*     */       {
/* 250 */         if (SpeedScaleShell.this.composite.equals(Utils.getCursorControl())) {
/* 251 */           return;
/*     */         }
/* 253 */         this.mouseIsOut = true;
/* 254 */         SimpleTimer.addEvent("close scaler", SystemTime.getOffsetTime(600L), new TimerEventPerformer()
/*     */         {
/*     */           public void perform(TimerEvent event) {
/* 257 */             Utils.execSWTThread(new AERunnable() {
/*     */               public void runSupport() {
/* 259 */                 if (!SpeedScaleShell.4.this.exitCancelled) {
/* 260 */                   SpeedScaleShell.this.shell.dispose();
/*     */                 } else {
/* 262 */                   SpeedScaleShell.4.this.exitCancelled = false;
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */       public void mouseEnter(MouseEvent e) {
/* 271 */         if (this.mouseIsOut) {
/* 272 */           this.exitCancelled = true;
/*     */         }
/* 274 */         this.mouseIsOut = false;
/*     */       }
/*     */       
/* 277 */     });
/* 278 */     final MouseListener mouseListener = new MouseListener() {
/* 279 */       boolean bMouseDown = false;
/*     */       
/*     */       public void mouseUp(MouseEvent e) {
/* 282 */         Point ptOnDisplay = ((Control)e.widget).toDisplay(e.x, e.y);
/* 283 */         Point ptOnComposite = SpeedScaleShell.this.composite.toControl(ptOnDisplay);
/* 284 */         if ((SpeedScaleShell.this.assumeInitiallyDown) && (e.widget == SpeedScaleShell.this.composite))
/*     */         {
/* 286 */           SpeedScaleShell.this.assumeInitiallyDown = false;
/*     */         }
/*     */         
/*     */ 
/* 290 */         if (SpeedScaleShell.this.lastMoveHadMouseDown) {
/* 291 */           Point mousePos = display.getCursorLocation();
/*     */           
/* 293 */           if (mousePos.equals(firstMousePos)) {
/* 294 */             return;
/*     */           }
/*     */         }
/* 297 */         this.bMouseDown = true;
/*     */         
/* 299 */         if (this.bMouseDown) {
/* 300 */           if (ptOnComposite.y > SpeedScaleShell.this.HEIGHT - SpeedScaleShell.this.SCALER_HEIGHT) {
/* 301 */             SpeedScaleShell.this.setValue(SpeedScaleShell.this.getValueFromMousePos(ptOnComposite.x));
/* 302 */             SpeedScaleShell.this.setCancelled(false);
/* 303 */             if (SpeedScaleShell.this.lastMoveHadMouseDown) {
/* 304 */               SpeedScaleShell.this.shell.dispose();
/*     */             }
/* 306 */           } else if (ptOnComposite.y > SpeedScaleShell.this.TEXT_HEIGHT) {
/* 307 */             int idx = (ptOnComposite.y - SpeedScaleShell.this.TEXT_HEIGHT) / SpeedScaleShell.this.OPTION_HEIGHT;
/* 308 */             Iterator iterator = SpeedScaleShell.this.mapOptions.keySet().iterator();
/*     */             int newValue;
/*     */             do {
/* 311 */               newValue = ((Integer)iterator.next()).intValue();
/* 312 */               idx--;
/* 313 */             } while (idx >= 0);
/* 314 */             SpeedScaleShell.this.value = newValue;
/* 315 */             SpeedScaleShell.this.setCancelled(false);
/* 316 */             SpeedScaleShell.this.setMenuChosen(true);
/* 317 */             SpeedScaleShell.this.shell.dispose();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       public void mouseDown(MouseEvent e) {
/* 323 */         Point ptOnDisplay = ((Control)e.widget).toDisplay(e.x, e.y);
/* 324 */         Point ptOnComposite = SpeedScaleShell.this.composite.toControl(ptOnDisplay);
/* 325 */         if (e.count > 1) {
/* 326 */           SpeedScaleShell.this.lastMoveHadMouseDown = true;
/* 327 */           return;
/*     */         }
/* 329 */         Point mousePos = display.getCursorLocation();
/* 330 */         if (ptOnComposite.y > SpeedScaleShell.this.HEIGHT - SpeedScaleShell.this.SCALER_HEIGHT) {
/* 331 */           this.bMouseDown = true;
/* 332 */           SpeedScaleShell.this.setValue(SpeedScaleShell.this.getValueFromMousePos(e.x));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void mouseDoubleClick(MouseEvent e) {}
/* 339 */     };
/* 340 */     this.composite.addMouseListener(mouseListener);
/* 341 */     if (cClickedFrom != null) {
/* 342 */       cClickedFrom.addMouseListener(mouseListener);
/* 343 */       cClickedFrom.addMouseMoveListener(mouseMoveListener);
/* 344 */       this.composite.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent arg0) {
/* 346 */           cClickedFrom.removeMouseListener(mouseListener);
/* 347 */           cClickedFrom.removeMouseMoveListener(mouseMoveListener);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 352 */     this.composite.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 354 */         int x = SpeedScaleShell.this.WIDTH_NO_PADDING * SpeedScaleShell.this.value / SpeedScaleShell.this.maxValue;
/* 355 */         if (x < 0) {
/* 356 */           x = 0;
/* 357 */         } else if (x > SpeedScaleShell.this.WIDTH_NO_PADDING) {
/* 358 */           x = SpeedScaleShell.this.WIDTH_NO_PADDING;
/*     */         }
/* 360 */         int startX = SpeedScaleShell.this.WIDTH_NO_PADDING * startValue / SpeedScaleShell.this.maxValue;
/* 361 */         if (startX < 0) {
/* 362 */           startX = 0;
/* 363 */         } else if (startX > SpeedScaleShell.this.WIDTH_NO_PADDING) {
/* 364 */           startX = SpeedScaleShell.this.WIDTH_NO_PADDING;
/*     */         }
/* 366 */         int baseLinePos = SpeedScaleShell.this.getBaselinePos();
/*     */         try
/*     */         {
/* 369 */           e.gc.setAdvanced(true);
/* 370 */           e.gc.setAntialias(1);
/*     */         }
/*     */         catch (Exception ex) {}
/*     */         
/*     */ 
/* 375 */         e.gc.setLineWidth(Utils.adjustPXForDPI(1));
/*     */         
/* 377 */         e.gc.setForeground(display.getSystemColor(18));
/*     */         
/*     */ 
/* 380 */         e.gc.drawLine(SpeedScaleShell.this.PADDING_X0, baseLinePos - 6, SpeedScaleShell.this.PADDING_X0, baseLinePos + 6);
/*     */         
/* 382 */         e.gc.drawLine(SpeedScaleShell.this.PADDING_X0 + SpeedScaleShell.this.WIDTH_NO_PADDING, baseLinePos - 6, SpeedScaleShell.this.PADDING_X0 + SpeedScaleShell.this.WIDTH_NO_PADDING, baseLinePos + 6);
/*     */         
/*     */ 
/* 385 */         e.gc.drawLine(SpeedScaleShell.this.PADDING_X0, baseLinePos, SpeedScaleShell.this.PADDING_X0 + SpeedScaleShell.this.WIDTH_NO_PADDING, baseLinePos);
/*     */         
/*     */ 
/* 388 */         e.gc.setForeground(display.getSystemColor(21));
/* 389 */         e.gc.setBackground(display.getSystemColor(21));
/*     */         
/* 391 */         e.gc.drawLine(SpeedScaleShell.this.PADDING_X0 + startX, baseLinePos - SpeedScaleShell.this.PX_5, SpeedScaleShell.this.PADDING_X0 + startX, baseLinePos + SpeedScaleShell.this.PX_5);
/*     */         
/*     */ 
/* 394 */         e.gc.fillRoundRectangle(SpeedScaleShell.this.PADDING_X0 + x - SpeedScaleShell.this.PX_2, baseLinePos - SpeedScaleShell.this.PX_5, SpeedScaleShell.this.MARKER_WIDTH, SpeedScaleShell.this.MARKER_HEIGHT, SpeedScaleShell.this.MARKER_HEIGHT, SpeedScaleShell.this.MARKER_HEIGHT);
/*     */         
/*     */ 
/*     */ 
/* 398 */         e.gc.setForeground(display.getSystemColor(28));
/* 399 */         e.gc.setBackground(display.getSystemColor(29));
/*     */         
/* 401 */         e.gc.fillRectangle(0, 0, SpeedScaleShell.this.WIDTH, SpeedScaleShell.this.TEXT_HEIGHT);
/*     */         
/* 403 */         GCStringPrinter.printString(e.gc, SpeedScaleShell.this._getStringValue(), new Rectangle(0, 0, SpeedScaleShell.this.WIDTH, SpeedScaleShell.this.HEIGHT), true, false, 16777408);
/*     */         
/*     */ 
/*     */ 
/* 407 */         e.gc.drawLine(0, SpeedScaleShell.this.TEXT_HEIGHT, SpeedScaleShell.this.WIDTH, SpeedScaleShell.this.TEXT_HEIGHT);
/*     */         
/*     */ 
/* 410 */         int y = SpeedScaleShell.this.TEXT_HEIGHT;
/* 411 */         Point mousePos = SpeedScaleShell.this.composite.toControl(display.getCursorLocation());
/* 412 */         for (Iterator iter = SpeedScaleShell.this.mapOptions.keySet().iterator(); iter.hasNext();) {
/* 413 */           Integer value = (Integer)iter.next();
/* 414 */           String text = (String)SpeedScaleShell.this.mapOptions.get(value);
/*     */           
/* 416 */           e.gc.setAntialias(1);
/* 417 */           Rectangle area = new Rectangle(0, y, SpeedScaleShell.this.WIDTH, SpeedScaleShell.this.OPTION_HEIGHT);
/*     */           Color bg;
/* 419 */           if (area.contains(mousePos)) {
/* 420 */             Color bg = display.getSystemColor(26);
/* 421 */             e.gc.setBackground(bg);
/* 422 */             e.gc.setForeground(display.getSystemColor(27));
/*     */             
/* 424 */             e.gc.fillRectangle(area);
/*     */           } else {
/* 426 */             bg = display.getSystemColor(25);
/* 427 */             e.gc.setBackground(bg);
/* 428 */             e.gc.setForeground(display.getSystemColor(24));
/*     */           }
/*     */           
/*     */ 
/* 432 */           int ovalGap = Utils.adjustPXForDPI(6);
/* 433 */           float ovalPadding = ovalGap / 2.0F;
/* 434 */           int ovalSize = SpeedScaleShell.this.OPTION_HEIGHT - ovalGap;
/* 435 */           float xCenter = ovalSize / 2.0F + SpeedScaleShell.this.PX_2;
/* 436 */           float yCenter = ovalSize / 2.0F + ovalPadding;
/* 437 */           if (SpeedScaleShell.this.getValue() == value.intValue()) {
/* 438 */             Color saveColor = e.gc.getBackground();
/* 439 */             e.gc.setBackground(e.gc.getForeground());
/* 440 */             float ovalSizeMini = ovalSize - ovalGap / 2.0F;
/* 441 */             int xMiniOval = (int)Math.round(xCenter - ovalSizeMini / 2.0D);
/* 442 */             int yMiniOval = (int)Math.round(yCenter - ovalSizeMini / 2.0D);
/* 443 */             e.gc.fillOval(xMiniOval, y + yMiniOval, Math.round(ovalSizeMini), Math.round(ovalSizeMini));
/*     */             
/* 445 */             e.gc.setBackground(saveColor);
/*     */           }
/* 447 */           if (Constants.isLinux)
/*     */           {
/*     */ 
/*     */ 
/* 451 */             Color saveColor = e.gc.getForeground();
/* 452 */             e.gc.setForeground(bg);
/* 453 */             e.gc.drawPoint(SpeedScaleShell.this.PX_2, (int)(y + ovalPadding));
/* 454 */             e.gc.setForeground(saveColor);
/*     */           }
/* 456 */           e.gc.drawOval(SpeedScaleShell.this.PX_2, (int)(y + ovalPadding), ovalSize, ovalSize);
/*     */           
/* 458 */           GCStringPrinter.printString(e.gc, text, new Rectangle(SpeedScaleShell.this.OPTION_HEIGHT, y, SpeedScaleShell.this.WIDTH - SpeedScaleShell.this.OPTION_HEIGHT, SpeedScaleShell.this.OPTION_HEIGHT), true, false, 16384);
/*     */           
/* 460 */           y += SpeedScaleShell.this.OPTION_HEIGHT;
/*     */         }
/*     */         
/*     */ 
/* 464 */         if (SpeedScaleShell.this.sValue.length() > 0) {
/* 465 */           Point extent = e.gc.textExtent(SpeedScaleShell.this.sValue);
/* 466 */           if (extent.x > SpeedScaleShell.this.WIDTH - SpeedScaleShell.this.PX_10) {
/* 467 */             extent.x = (SpeedScaleShell.this.WIDTH - SpeedScaleShell.this.PX_10);
/*     */           }
/* 469 */           int yTypedValue = Utils.adjustPXForDPI(15);
/* 470 */           Rectangle rect = new Rectangle(SpeedScaleShell.this.WIDTH - (SpeedScaleShell.this.PX_10 - 2) - extent.x, yTypedValue - 1, extent.x + SpeedScaleShell.this.PX_5, extent.y + (SpeedScaleShell.this.PX_5 - 1) + (yTypedValue - 1) > SpeedScaleShell.this.TEXT_HEIGHT ? SpeedScaleShell.this.TEXT_HEIGHT - yTypedValue : extent.y + (SpeedScaleShell.this.PX_5 - 1));
/*     */           
/*     */ 
/*     */ 
/* 474 */           e.gc.setBackground(display.getSystemColor(29));
/* 475 */           e.gc.fillRectangle(rect);
/*     */           try
/*     */           {
/* 478 */             e.gc.setAlpha(80);
/*     */           }
/*     */           catch (Exception ex) {}
/*     */           
/* 482 */           e.gc.setForeground(display.getSystemColor(28));
/*     */           
/* 484 */           GCStringPrinter.printString(e.gc, SpeedScaleShell.this.sValue, new Rectangle(rect.x + SpeedScaleShell.this.PX_2, rect.y + SpeedScaleShell.this.PX_2, SpeedScaleShell.this.WIDTH - SpeedScaleShell.this.PX_5, SpeedScaleShell.this.OPTION_HEIGHT), true, false, 17408);
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 491 */     });
/* 492 */     final AERunnable cursorBlinkRunnable = new AERunnable() {
/* 493 */       boolean on = false;
/*     */       
/*     */       public void runSupport() {
/* 496 */         if (SpeedScaleShell.this.composite.isDisposed()) {
/* 497 */           return;
/*     */         }
/*     */         
/* 500 */         this.on = (!this.on);
/*     */         
/* 502 */         GC gc = new GC(SpeedScaleShell.this.composite);
/*     */         try {
/* 504 */           gc.setLineWidth(SpeedScaleShell.this.PX_2);
/* 505 */           if (!this.on) {
/* 506 */             gc.setForeground(display.getSystemColor(29));
/*     */           } else {
/*     */             try {
/* 509 */               gc.setForeground(display.getSystemColor(28));
/*     */               
/* 511 */               gc.setAlpha(80);
/*     */             }
/*     */             catch (Exception e) {}
/*     */           }
/* 515 */           int y = Utils.adjustPXForDPI(15);
/* 516 */           gc.drawLine(SpeedScaleShell.this.WIDTH - SpeedScaleShell.this.PX_5, y + 1, SpeedScaleShell.this.WIDTH - SpeedScaleShell.this.PX_5, y + SpeedScaleShell.this.OPTION_HEIGHT);
/*     */         } finally {
/* 518 */           gc.dispose();
/*     */         }
/* 520 */         if (SpeedScaleShell.this.cursorBlinkPerformer != null) {
/* 521 */           SpeedScaleShell.this.cursorBlinkEvent = SimpleTimer.addEvent("BlinkingCursor", SystemTime.getOffsetTime(500L), SpeedScaleShell.this.cursorBlinkPerformer);
/*     */         }
/*     */         
/*     */       }
/* 525 */     };
/* 526 */     this.cursorBlinkPerformer = new TimerEventPerformer() {
/*     */       public void perform(TimerEvent event) {
/* 528 */         Utils.execSWTThread(cursorBlinkRunnable);
/*     */       }
/* 530 */     };
/* 531 */     this.cursorBlinkEvent = SimpleTimer.addEvent("BlinkingCursor", SystemTime.getOffsetTime(500L), this.cursorBlinkPerformer);
/*     */     
/*     */ 
/* 534 */     this.composite.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {}
/*     */       
/*     */       public void keyPressed(KeyEvent e) {
/* 539 */         if (Character.isDigit(e.character)) {
/* 540 */           SpeedScaleShell.access$2284(SpeedScaleShell.this, String.valueOf(e.character));
/* 541 */         } else if ((e.keyCode == 8) && (SpeedScaleShell.this.sValue.length() > 0)) {
/* 542 */           SpeedScaleShell.this.sValue = SpeedScaleShell.this.sValue.substring(0, SpeedScaleShell.this.sValue.length() - 1);
/*     */         } else {
/* 544 */           return;
/*     */         }
/*     */         try {
/* 547 */           int newValue = Integer.parseInt(SpeedScaleShell.this.sValue);
/* 548 */           if (SpeedScaleShell.this.maxTextValue == -1) {
/* 549 */             SpeedScaleShell.this.setValue(newValue);
/*     */           } else {
/* 551 */             if ((SpeedScaleShell.this.minValue > 0) && (newValue < SpeedScaleShell.this.minValue)) {
/* 552 */               newValue = SpeedScaleShell.this.minValue;
/*     */             }
/* 554 */             if (newValue > SpeedScaleShell.this.maxTextValue) {
/* 555 */               newValue = SpeedScaleShell.this.maxTextValue;
/*     */             }
/* 557 */             SpeedScaleShell.this.value = newValue;
/* 558 */             SpeedScaleShell.this.composite.redraw();
/*     */           }
/*     */         } catch (Exception ex) {
/* 561 */           SpeedScaleShell.this.setValue(startValue);
/*     */         }
/*     */         
/*     */       }
/* 565 */     });
/* 566 */     Point location = display.getCursorLocation();
/*     */     
/* 568 */     location.y -= getBaselinePos();
/* 569 */     int x = (int)(this.WIDTH_NO_PADDING * (this.value > this.maxValue ? 1.0D : this.value / this.maxValue));
/*     */     
/* 571 */     location.x -= this.PADDING_X0 + x;
/*     */     
/* 573 */     Rectangle bounds = new Rectangle(location.x, location.y, this.WIDTH, this.HEIGHT);
/* 574 */     Monitor mouseMonitor = this.shell.getMonitor();
/* 575 */     Monitor[] monitors = display.getMonitors();
/* 576 */     for (int i = 0; i < monitors.length; i++) {
/* 577 */       Monitor monitor = monitors[i];
/* 578 */       if (monitor.getBounds().contains(location)) {
/* 579 */         mouseMonitor = monitor;
/* 580 */         break;
/*     */       }
/*     */     }
/* 583 */     Rectangle monitorBounds = mouseMonitor.getBounds();
/* 584 */     Rectangle intersection = monitorBounds.intersection(bounds);
/* 585 */     if (intersection.width != bounds.width) {
/* 586 */       bounds.x = (monitorBounds.x + monitorBounds.width - this.WIDTH);
/* 587 */       bounds.width = this.WIDTH;
/*     */     }
/* 589 */     if (intersection.height != bounds.height) {
/* 590 */       bounds.y = (monitorBounds.y + monitorBounds.height - this.HEIGHT);
/* 591 */       bounds.height = this.HEIGHT;
/*     */     }
/*     */     
/* 594 */     this.shell.setBounds(bounds);
/* 595 */     if (!bounds.contains(firstMousePos))
/*     */     {
/* 597 */       this.shell.setLocation(firstMousePos.x - bounds.width / 2, firstMousePos.y - bounds.height + 2);
/*     */     }
/*     */     
/*     */ 
/* 601 */     this.shell.open();
/*     */     
/* 603 */     this.composite.setFocus();
/*     */     try
/*     */     {
/* 606 */       while (!this.shell.isDisposed()) {
/* 607 */         if (!display.readAndDispatch()) {
/* 608 */           display.sleep();
/*     */         }
/*     */       }
/*     */     } catch (Throwable t) {
/* 612 */       Debug.out(t);
/*     */     }
/*     */     
/* 615 */     if (this.cursorBlinkEvent != null) {
/* 616 */       this.cursorBlinkEvent.cancel();
/* 617 */       this.cursorBlinkEvent = null;
/*     */     }
/*     */     
/* 620 */     return !this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getValueFromMousePos(int x)
/*     */   {
/* 630 */     int x0 = x + 1;
/* 631 */     if (x < this.PADDING_X0) {
/* 632 */       x0 = this.PADDING_X0;
/* 633 */     } else if (x > this.PADDING_X0 + this.WIDTH_NO_PADDING) {
/* 634 */       x0 = this.PADDING_X0 + this.WIDTH_NO_PADDING;
/*     */     }
/*     */     
/* 637 */     return (x0 - this.PADDING_X0) * this.maxValue / this.WIDTH_NO_PADDING;
/*     */   }
/*     */   
/*     */   public int getValue() {
/* 641 */     return this.value;
/*     */   }
/*     */   
/*     */   public boolean isCancelled() {
/* 645 */     return this.cancelled;
/*     */   }
/*     */   
/*     */   public void setCancelled(boolean cancelled) {
/* 649 */     this.cancelled = cancelled;
/*     */   }
/*     */   
/*     */   public int getMinValue() {
/* 653 */     return this.minValue;
/*     */   }
/*     */   
/*     */   public void setMinValue(int minValue) {
/* 657 */     this.minValue = minValue;
/*     */   }
/*     */   
/*     */   public int getMaxValue() {
/* 661 */     return this.maxValue;
/*     */   }
/*     */   
/*     */   public void setMaxValue(int maxValue) {
/* 665 */     this.maxValue = maxValue;
/*     */   }
/*     */   
/*     */   public void setValue(int value)
/*     */   {
/* 670 */     if (value > this.maxValue) {
/* 671 */       value = this.maxValue;
/* 672 */     } else if (value < this.minValue) {
/* 673 */       value = this.minValue;
/*     */     }
/* 675 */     this.value = value;
/* 676 */     if ((this.composite != null) && (!this.composite.isDisposed())) {
/* 677 */       this.composite.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   public String _getStringValue() {
/* 682 */     String name = (String)this.mapOptions.get(new Integer(this.value));
/* 683 */     return getStringValue(this.value, name);
/*     */   }
/*     */   
/*     */   public String getStringValue(int value, String sValue) {
/* 687 */     if (sValue != null) {
/* 688 */       return sValue;
/*     */     }
/* 690 */     return "" + value;
/*     */   }
/*     */   
/*     */   private int getBaselinePos() {
/* 694 */     return this.HEIGHT - this.SCALER_HEIGHT / 2;
/*     */   }
/*     */   
/*     */   public void addOption(String id, int value) {
/* 698 */     this.mapOptions.put(new Integer(value), id);
/* 699 */     this.HEIGHT += this.OPTION_HEIGHT;
/*     */   }
/*     */   
/*     */   public int getMaxTextValue() {
/* 703 */     return this.maxTextValue;
/*     */   }
/*     */   
/*     */   public void setMaxTextValue(int maxTextValue) {
/* 707 */     this.maxTextValue = maxTextValue;
/*     */   }
/*     */   
/*     */   public boolean wasMenuChosen() {
/* 711 */     return this.menuChosen;
/*     */   }
/*     */   
/*     */   public void setMenuChosen(boolean menuChosen) {
/* 715 */     this.menuChosen = menuChosen;
/*     */   }
/*     */   
/*     */   public void setParentShell(Shell parentShell) {
/* 719 */     this.parentShell = parentShell;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/SpeedScaleShell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */