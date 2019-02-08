/*     */ package org.gudy.azureus2.ui.swt.minibar;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.Iterator;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.FontMetrics;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.DoubleBufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory.AEShell;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class MiniBar
/*     */   implements MenuBuildUtils.MenuBuilder
/*     */ {
/*     */   protected Label lDrag;
/*     */   protected MiniBar stucked;
/*     */   protected Monitor[] screens;
/*     */   protected int xPressed;
/*     */   protected int yPressed;
/*     */   protected boolean moving;
/*     */   protected int hSize;
/*     */   protected Shell splash;
/*     */   protected MiniBarManager manager;
/*  59 */   private Font bold_font = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  64 */   private boolean constructing = false;
/*  65 */   private boolean constructed = false;
/*     */   
/*     */   private Menu menu;
/*     */   private MouseMoveListener mMoveListener;
/*     */   private MouseListener mListener;
/*     */   private int xSize;
/*     */   private boolean separateDataProt;
/*  72 */   private float width_multiplier = 1.0F;
/*     */   
/*     */   protected MiniBar(MiniBarManager manager) {
/*  75 */     this.manager = manager;
/*  76 */     setPrebuildValues();
/*  77 */     this.separateDataProt = DisplayFormatters.isDataProtSeparate();
/*     */   }
/*     */   
/*     */   private void setPrebuildValues() {
/*  81 */     this.constructing = false;
/*  82 */     this.constructed = false;
/*  83 */     this.xSize = 0;
/*  84 */     this.hSize = -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void assertConstructing()
/*     */   {
/*  91 */     if (!this.constructing)
/*  92 */       throw new RuntimeException("not constructing!");
/*     */   }
/*     */   
/*     */   private Font createBoldFont(Font original) {
/*  96 */     FontData[] font_data = original.getFontData();
/*  97 */     for (int i = 0; i < font_data.length; i++) {
/*  98 */       font_data[i].setStyle(font_data[i].getStyle() | 0x1);
/*     */     }
/* 100 */     return new Font(original.getDevice(), font_data);
/*     */   }
/*     */   
/*     */   protected final void createGap(int width) {
/* 104 */     width = (int)(width * this.width_multiplier);
/*     */     
/* 106 */     assertConstructing();
/* 107 */     Label result = new Label(this.splash, 0);
/* 108 */     result.setBackground(Colors.blues[0]);
/* 109 */     result.setForeground(Colors.blues[9]);
/* 110 */     result.setText("");
/* 111 */     result.addMouseListener(this.mListener);
/* 112 */     result.addMouseMoveListener(this.mMoveListener);
/* 113 */     result.setLocation(this.xSize, 0);
/* 114 */     result.setSize(width, this.hSize);
/* 115 */     result.setMenu(this.menu);
/* 116 */     this.xSize += width;
/*     */   }
/*     */   
/*     */   protected final Label createFixedTextLabel(String msg_key, boolean add_colon, boolean bold) {
/* 120 */     assertConstructing();
/* 121 */     Label result = new Label(this.splash, 0);
/* 122 */     result.setBackground(Colors.blues[0]);
/* 123 */     result.setForeground(Colors.blues[9]);
/* 124 */     result.setText(MessageText.getString(msg_key) + (add_colon ? ":" : ""));
/* 125 */     if (bold) {
/* 126 */       if (this.bold_font == null) {
/* 127 */         this.bold_font = createBoldFont(result.getFont());
/*     */       }
/* 129 */       result.setFont(this.bold_font);
/*     */     }
/* 131 */     result.addMouseListener(this.mListener);
/* 132 */     result.addMouseMoveListener(this.mMoveListener);
/* 133 */     result.pack();
/* 134 */     result.setLocation(this.xSize, 0);
/* 135 */     result.setMenu(this.menu);
/* 136 */     if (this.hSize == -1) {
/* 137 */       int hSizeText = result.getSize().y;
/* 138 */       int hSizeImage = this.lDrag.getSize().y;
/* 139 */       this.hSize = (hSizeText > hSizeImage ? hSizeText : hSizeImage);
/*     */     }
/* 141 */     this.xSize += result.getSize().x + 3;
/*     */     
/* 143 */     return result;
/*     */   }
/*     */   
/*     */   protected final Label createFixedLabel(int width) {
/* 147 */     assertConstructing();
/* 148 */     Label result = new Label(this.splash, 0);
/* 149 */     result.setBackground(Colors.white);
/* 150 */     result.setSize(width, -1);
/* 151 */     result.setLocation(this.xSize, 0);
/* 152 */     result.addMouseListener(this.mListener);
/* 153 */     result.addMouseMoveListener(this.mMoveListener);
/* 154 */     result.setMenu(this.menu);
/* 155 */     if (this.hSize == -1) {
/* 156 */       int hSizeText = result.getSize().y;
/* 157 */       int hSizeImage = this.lDrag.getSize().y;
/* 158 */       this.hSize = (hSizeText > hSizeImage ? hSizeText : hSizeImage);
/*     */     }
/* 160 */     this.xSize += width;
/*     */     
/* 162 */     return result;
/*     */   }
/*     */   
/*     */   protected final DoubleBufferedLabel createDataLabel(int width, boolean centered) {
/* 166 */     width = (int)(width * this.width_multiplier);
/* 167 */     assertConstructing();
/* 168 */     DoubleBufferedLabel result = new DoubleBufferedLabel(this.splash, (centered ? 16777216 : 0) | 0x20000000);
/* 169 */     result.setBackground(Colors.blues[0]);
/* 170 */     result.setText("");
/* 171 */     result.addMouseListener(this.mListener);
/* 172 */     result.addMouseMoveListener(this.mMoveListener);
/* 173 */     if (this.hSize == -1) {
/* 174 */       throw new RuntimeException("must add fixed text label first!");
/*     */     }
/* 176 */     result.setSize(width, this.hSize);
/* 177 */     result.setLocation(this.xSize, 0);
/* 178 */     result.setMenu(this.menu);
/* 179 */     this.xSize += width + 3;
/* 180 */     return result;
/*     */   }
/*     */   
/*     */   protected final DoubleBufferedLabel createDataLabel(int width) {
/* 184 */     return createDataLabel(width, false);
/*     */   }
/*     */   
/*     */   protected final DoubleBufferedLabel createSpeedLabel() {
/* 188 */     return createDataLabel(this.separateDataProt ? 110 : 65, this.separateDataProt);
/*     */   }
/*     */   
/*     */   protected void updateSpeedLabel(DoubleBufferedLabel label, long data_rate, long protocol_rate) {
/* 192 */     if (this.separateDataProt) {
/* 193 */       label.setText(DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(data_rate, protocol_rate));
/*     */     }
/*     */     else {
/* 196 */       label.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(data_rate + protocol_rate));
/*     */     }
/*     */   }
/*     */   
/*     */   protected final ProgressBar createProgressBar(int min, int max, int width, final ProgressBarText pbt) {
/* 201 */     width = (int)(width * this.width_multiplier);
/* 202 */     final ProgressBar result = new ProgressBar(this.splash, 65536);
/* 203 */     result.setBackground(Colors.blues[0]);
/* 204 */     result.setForeground(Colors.blues[2]);
/* 205 */     result.setMinimum(min);
/* 206 */     result.setMaximum(max);
/* 207 */     result.addMouseListener(this.mListener);
/* 208 */     result.addMouseMoveListener(this.mMoveListener);
/* 209 */     if (this.hSize == -1) {
/* 210 */       throw new RuntimeException("must add fixed text label first!");
/*     */     }
/* 212 */     result.setSize(width, this.hSize);
/* 213 */     result.setLocation(this.xSize, 0);
/* 214 */     result.setMenu(this.menu);
/* 215 */     this.xSize += width + 3;
/*     */     
/*     */ 
/* 218 */     if (pbt != null) {
/* 219 */       result.addListener(9, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 221 */           Color old = event.gc.getForeground();
/* 222 */           event.gc.setForeground(Colors.black);
/* 223 */           int char_width = event.gc.getFontMetrics().getAverageCharWidth();
/* 224 */           String pb_text = pbt.convert(result.getSelection());
/* 225 */           event.gc.drawText(pb_text, (result.getSize().x - pb_text.length() * char_width) / 2, -1, true);
/* 226 */           event.gc.setForeground(old);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 231 */     return result;
/*     */   }
/*     */   
/*     */   protected final ProgressBar createPercentProgressBar(int width) {
/* 235 */     createProgressBar(0, 1000, width, new ProgressBarText() {
/*     */       public String convert(int value) {
/* 237 */         return DisplayFormatters.formatPercentFromThousands(value);
/*     */       }
/*     */     });
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
/*     */   public final void construct(final Shell main)
/*     */   {
/* 252 */     if (this.constructed) {
/* 253 */       throw new RuntimeException("already constructed!");
/*     */     }
/*     */     
/* 256 */     this.constructing = true;
/*     */     
/* 258 */     this.stucked = null;
/* 259 */     this.splash = ShellFactory.createShell(16384);
/*     */     
/* 261 */     ((ShellFactory.AEShell)this.splash).setAdjustPXforDPI(false);
/*     */     
/* 263 */     int trans = COConfigurationManager.getIntParameter("Bar Transparency");
/*     */     
/* 265 */     if ((trans > 0) && (trans <= 100))
/*     */     {
/* 267 */       int alpha = (int)(255.0F * (100.0F - trans) / 100.0F);
/*     */       
/* 269 */       this.splash.setAlpha(alpha);
/*     */     }
/*     */     
/* 272 */     this.manager.register(this);
/*     */     final DisposeListener mainDisposeListener;
/* 274 */     main.addDisposeListener( = new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent event) {
/* 276 */         MiniBar.this.close();
/*     */       }
/*     */       
/*     */ 
/* 280 */     });
/* 281 */     this.splash.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 283 */         if (main.isDisposed()) return;
/* 284 */         main.removeDisposeListener(mainDisposeListener);
/*     */       }
/*     */       
/*     */ 
/* 288 */     });
/* 289 */     this.screens = main.getDisplay().getMonitors();
/* 290 */     build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void build()
/*     */   {
/* 297 */     this.lDrag = new Label(this.splash, 0);
/*     */     
/* 299 */     int testTextHeight = 0;
/*     */     try {
/* 301 */       GC gc = new GC(this.lDrag);
/* 302 */       gc.setFont(this.splash.getDisplay().getSystemFont());
/* 303 */       Point textSize = gc.textExtent("Vuze Rocks innit: 100 MB/sec");
/* 304 */       int textWidth = textSize.x;
/* 305 */       testTextHeight = textSize.y;
/*     */       
/* 307 */       if (textWidth > 139) {
/* 308 */         this.width_multiplier = (textWidth / 139.0F);
/*     */       }
/* 310 */       gc.dispose();
/*     */     } catch (Throwable e) {
/* 312 */       Debug.out(e);
/*     */     }
/* 314 */     if (!Constants.isOSX) {
/* 315 */       this.lDrag.setImage(ImageLoader.getInstance().getImage("dragger"));
/* 316 */       this.lDrag.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/* 318 */           ImageLoader.getInstance().releaseImage("dragger");
/*     */         }
/*     */       });
/*     */     }
/* 322 */     this.lDrag.pack();
/*     */     
/* 324 */     int yPad = 0;
/* 325 */     Point lDragSize = this.lDrag.getSize();
/* 326 */     this.xSize = (lDragSize.x + 3);
/* 327 */     if (lDragSize.y < testTextHeight) {
/* 328 */       yPad = (testTextHeight - lDragSize.y) / 2;
/*     */     }
/* 330 */     this.lDrag.setLocation(0, yPad);
/*     */     
/* 332 */     this.mListener = new MouseAdapter() {
/*     */       int old_alpha;
/*     */       
/*     */       public void mouseDown(MouseEvent e) {
/* 336 */         MiniBar.this.xPressed = e.x;
/* 337 */         MiniBar.this.yPressed = e.y;
/* 338 */         MiniBar.this.moving = true;
/* 339 */         this.old_alpha = MiniBar.this.splash.getAlpha();
/* 340 */         if (this.old_alpha != 255) {
/* 341 */           MiniBar.this.splash.setAlpha(255);
/*     */         }
/*     */       }
/*     */       
/*     */       public void mouseUp(MouseEvent e)
/*     */       {
/* 347 */         MiniBar.this.moving = false;
/* 348 */         if (this.old_alpha != 255) {
/* 349 */           MiniBar.this.splash.setAlpha(this.old_alpha);
/*     */         }
/*     */       }
/*     */       
/*     */       public void mouseDoubleClick(MouseEvent e)
/*     */       {
/* 355 */         MiniBar.this.doubleClick();
/*     */       }
/*     */       
/* 358 */     };
/* 359 */     this.mMoveListener = new MouseMoveListener() {
/*     */       public void mouseMove(MouseEvent e) {
/* 361 */         if (MiniBar.this.moving) {
/* 362 */           int dX = MiniBar.this.xPressed - e.x;
/* 363 */           int dY = MiniBar.this.yPressed - e.y;
/*     */           
/* 365 */           Point currentLoc = MiniBar.this.splash.getLocation();
/* 366 */           currentLoc.x -= dX;
/* 367 */           currentLoc.y -= dY;
/* 368 */           MiniBar.this.setSnapLocation(currentLoc);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 373 */     };
/* 374 */     this.splash.setBackground(Colors.blues[0]);
/* 375 */     this.splash.setForeground(Colors.blues[9]);
/* 376 */     this.splash.addMouseListener(this.mListener);
/* 377 */     this.splash.addMouseMoveListener(this.mMoveListener);
/* 378 */     this.lDrag.addMouseListener(this.mListener);
/* 379 */     this.lDrag.addMouseMoveListener(this.mMoveListener);
/*     */     
/* 381 */     this.menu = new Menu(this.splash, 8);
/* 382 */     MenuBuildUtils.addMaintenanceListenerForMenu(this.menu, this);
/* 383 */     beginConstruction();
/*     */     
/* 385 */     this.splash.addListener(20, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 387 */         MiniBar.this.splash.setVisible(true);
/* 388 */         MiniBar.this.splash.setActive();
/*     */       }
/* 390 */     });
/* 391 */     this.splash.setSize(this.xSize + 3, this.hSize + 2);
/*     */     
/*     */ 
/* 394 */     this.mListener = null;
/* 395 */     this.mMoveListener = null;
/* 396 */     this.menu = null;
/*     */     
/*     */ 
/* 399 */     refresh();
/* 400 */     this.constructing = false;
/* 401 */     this.constructed = true;
/*     */     
/*     */ 
/* 404 */     Point point = getInitialLocation();
/* 405 */     if (point == null) {
/* 406 */       Rectangle clientArea = this.splash.getMonitor().getClientArea();
/* 407 */       point = new Point(clientArea.x, clientArea.y);
/*     */     }
/* 409 */     if (point != null) { this.splash.setLocation(point);
/*     */     }
/* 411 */     Utils.verifyShellRect(this.splash, true);
/*     */     
/* 413 */     this.splash.setVisible(true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void buildMenu(Menu menu)
/*     */   {
/* 419 */     Object[] plugin_context_objs = getPluginMenuContextObjects();
/* 420 */     String[] plugin_menu_ids = getPluginMenuIdentifiers(plugin_context_objs);
/* 421 */     if (plugin_menu_ids != null) {
/* 422 */       org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray(plugin_menu_ids);
/* 423 */       if (menu_items.length > 0) {
/* 424 */         MenuBuildUtils.addPluginMenuItems(menu_items, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(plugin_context_objs));
/*     */         
/*     */ 
/*     */ 
/* 428 */         new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */       }
/*     */     }
/*     */     
/* 432 */     org.eclipse.swt.widgets.MenuItem itemClose = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 433 */     itemClose.setText(MessageText.getString("wizard.close"));
/* 434 */     itemClose.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 436 */         MiniBar.this.close();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected final void setSnapLocation(Point currentLoc)
/*     */   {
/* 443 */     Rectangle dim = new Rectangle(currentLoc.x, currentLoc.y, this.splash.getBounds().width, this.splash.getBounds().height);
/*     */     
/*     */ 
/* 446 */     int topIntersectArea = 0;
/* 447 */     int bestScreen = 0;
/* 448 */     for (int i = 0; i < this.screens.length; i++)
/*     */     {
/* 450 */       Rectangle curScreen = this.screens[i].getClientArea();
/* 451 */       curScreen.intersect(dim);
/* 452 */       int area = curScreen.width * curScreen.height;
/* 453 */       if (area > topIntersectArea)
/*     */       {
/* 455 */         bestScreen = i;
/* 456 */         topIntersectArea = area;
/*     */       }
/*     */     }
/*     */     
/* 460 */     Rectangle screen = this.screens[bestScreen].getClientArea();
/*     */     
/* 462 */     if (currentLoc.x - screen.x < 10) {
/* 463 */       currentLoc.x = screen.x;
/* 464 */     } else if (currentLoc.x - screen.x > screen.width - dim.width - 10)
/* 465 */       currentLoc.x = (screen.x + screen.width - dim.width);
/* 466 */     if (currentLoc.y - screen.y < 10)
/* 467 */       currentLoc.y = screen.y;
/* 468 */     MiniBar mw = this;
/* 469 */     int height = 0;
/* 470 */     while (mw != null) {
/* 471 */       Shell s = mw.getShell();
/* 472 */       if (s.isDisposed()) {
/* 473 */         mw = null;
/*     */       } else {
/* 475 */         height += s.getBounds().height - 1;
/* 476 */         mw = mw.getStucked();
/* 477 */         if (mw == this)
/* 478 */           mw = null;
/*     */       }
/*     */     }
/* 481 */     if (currentLoc.y - screen.y > screen.height - height - 10) {
/* 482 */       currentLoc.y = (screen.y + screen.height - height);
/*     */     }
/* 484 */     MiniBarManager g_manager = MiniBarManager.getManager();
/*     */     try {
/* 486 */       g_manager.getMiniBarMonitor().enter();
/* 487 */       if (g_manager.countMiniBars() > 1) {
/* 488 */         Iterator itr = g_manager.getMiniBarIterator();
/* 489 */         while (itr.hasNext()) {
/* 490 */           MiniBar downloadBar = (MiniBar)itr.next();
/* 491 */           Point location = downloadBar.getShell().getLocation();
/*     */           
/*     */ 
/* 494 */           location.y += downloadBar.getShell().getBounds().height;
/*     */           
/* 496 */           if (((downloadBar != this) && (downloadBar.getStucked() == null)) || (downloadBar.getStucked() == this))
/*     */           {
/* 498 */             if ((Math.abs(location.x - currentLoc.x) < 10) && (location.y - currentLoc.y < 10) && (location.y - currentLoc.y > 0))
/*     */             {
/*     */ 
/* 501 */               downloadBar.setStucked(this);
/* 502 */               currentLoc.x = location.x;
/* 503 */               location.y -= 1;
/*     */             }
/*     */           }
/*     */           
/* 507 */           if ((downloadBar != this) && (downloadBar.getStucked() == this) && (
/* 508 */             (Math.abs(location.x - currentLoc.x) > 10) || (Math.abs(location.y - currentLoc.y) > 10)))
/*     */           {
/* 510 */             downloadBar.setStucked(null);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 516 */       g_manager.getMiniBarMonitor().exit();
/*     */     }
/*     */     
/* 519 */     this.splash.setLocation(currentLoc);
/* 520 */     MiniBar mwCurrent = this;
/* 521 */     while (mwCurrent != null) {
/* 522 */       currentLoc.y += mwCurrent.getShell().getBounds().height - 1;
/* 523 */       MiniBar mwChild = mwCurrent.getStucked();
/* 524 */       if ((mwChild != null) && (mwChild != this)) {
/* 525 */         Shell s = mwChild.getShell();
/* 526 */         if (s.isDisposed()) {
/* 527 */           mwCurrent.setStucked(null);
/* 528 */           mwCurrent = null;
/*     */         }
/*     */         else {
/* 531 */           mwCurrent = mwChild;
/* 532 */           mwCurrent.getShell().setLocation(currentLoc);
/*     */         }
/*     */       }
/*     */       else {
/* 536 */         mwCurrent = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Shell getShell()
/*     */   {
/* 546 */     return this.splash;
/*     */   }
/*     */   
/*     */   public void setVisible(boolean visible) {
/* 550 */     this.splash.setVisible(visible);
/*     */   }
/*     */   
/*     */   public final boolean hasSameContext(MiniBar m) {
/* 554 */     return hasContext(m.getContextObject());
/*     */   }
/*     */   
/*     */   public final boolean hasContext(Object context) {
/* 558 */     Object my_context = getContextObject();
/* 559 */     if (my_context == null) {
/* 560 */       return context == null;
/*     */     }
/*     */     
/* 563 */     return my_context.equals(context);
/*     */   }
/*     */   
/*     */   public MiniBar getStucked()
/*     */   {
/* 568 */     return this.stucked;
/*     */   }
/*     */   
/*     */   public void setStucked(MiniBar mw) {
/* 572 */     this.stucked = mw;
/*     */   }
/*     */   
/*     */   public final void forceSaveLocation()
/*     */   {
/* 577 */     if (!this.splash.isDisposed()) {
/* 578 */       storeLastLocation(this.splash.getLocation());
/*     */     }
/*     */   }
/*     */   
/*     */   public final void close() {
/* 583 */     if (!this.splash.isDisposed()) {
/* 584 */       Display display = this.splash.getDisplay();
/* 585 */       if ((display != null) && (!display.isDisposed())) {
/* 586 */         display.asyncExec(new AERunnable() {
/* 587 */           public void runSupport() { MiniBar.this.dispose(); }
/*     */         });
/*     */       }
/*     */     }
/* 591 */     this.manager.unregister(this);
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 595 */     if (!this.splash.isDisposed()) {
/* 596 */       forceSaveLocation();
/* 597 */       this.splash.dispose();
/*     */     }
/* 599 */     if ((this.bold_font != null) && (!this.bold_font.isDisposed())) this.bold_font.dispose();
/*     */   }
/*     */   
/*     */   public final void refresh() {
/* 603 */     if (this.splash.isDisposed()) return;
/* 604 */     refresh0();
/*     */   }
/*     */   
/*     */   protected abstract void refresh0();
/*     */   
/*     */   protected abstract void beginConstruction();
/*     */   
/*     */   protected abstract Object getContextObject();
/*     */   
/*     */   public String[] getPluginMenuIdentifiers(Object[] context)
/*     */   {
/* 615 */     return null;
/*     */   }
/*     */   
/*     */   public Object[] getPluginMenuContextObjects() {
/* 619 */     return null;
/*     */   }
/*     */   
/*     */   protected Point getInitialLocation() {
/* 623 */     return null;
/*     */   }
/*     */   
/*     */   protected void doubleClick() {}
/*     */   
/*     */   protected void storeLastLocation(Point point) {}
/*     */   
/*     */   protected static abstract interface ProgressBarText
/*     */   {
/*     */     public abstract String convert(int paramInt);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/minibar/MiniBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */