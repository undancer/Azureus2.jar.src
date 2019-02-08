/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.ui.IUIIntializer;
/*     */ import com.aelitis.azureus.ui.InitializerListener;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ public class SplashWindow
/*     */   implements InitializerListener
/*     */ {
/*     */   private static final String IMG_SPLASH = "azureus_splash";
/*     */   protected static final int OFFSET_LEFT = 10;
/*     */   protected static final int OFFSET_RIGHT = 10;
/*     */   protected static final int OFFSET_BOTTOM = 12;
/*     */   protected static final int PB_HEIGHT = 2;
/*     */   protected static final boolean PB_INVERTED = true;
/*     */   protected static final int PB_INVERTED_BG_HEIGHT = 2;
/*     */   protected static final int PB_INVERTED_X_OFFSET = 0;
/*     */   protected static final boolean DISPLAY_BORDER = true;
/*     */   Display display;
/*     */   IUIIntializer initializer;
/*     */   Shell splash;
/*     */   Canvas canvas;
/*     */   Image background;
/*     */   int width;
/*     */   int height;
/*     */   Image current;
/*     */   Color progressBarColor;
/*     */   Color textColor;
/*     */   Color fadedGreyColor;
/*     */   Font textFont;
/*     */   private String task;
/*     */   private int percent;
/*     */   private boolean updating;
/*     */   int pbX;
/*     */   int pbY;
/*     */   int pbWidth;
/*     */   
/*     */   public SplashWindow(Display display)
/*     */   {
/*  99 */     this(display, null);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 103 */     Display display = new Display();
/*     */     
/* 105 */     SplashWindow splash = new SplashWindow(display);
/*     */     
/* 107 */     Thread t = new Thread() {
/*     */       public void run() {
/*     */         try {
/* 110 */           int percent = 0;
/* 111 */           while (percent <= 100) {
/* 112 */             this.val$splash.reportPercent(percent++);
/* 113 */             this.val$splash.reportCurrentTask(percent + "% Loading dbnvsudn vjksfdh fgshdu fbhsduh bvsfd fbsd fbvsdb fsuid opnum supnum boopergood haha text doot subliminal.".substring(0, (int)(1.0D + Math.random() * 110.0D)));
/*     */             
/*     */ 
/* 116 */             Thread.sleep(100L);
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */         
/* 121 */         this.val$splash.closeSplash();
/*     */       }
/* 123 */     };
/* 124 */     t.start();
/*     */     
/* 126 */     while (!splash.splash.isDisposed()) {
/* 127 */       if (!display.readAndDispatch()) {
/* 128 */         display.sleep();
/*     */       }
/*     */     }
/* 131 */     display.dispose();
/*     */   }
/*     */   
/*     */   public SplashWindow(Display _display, IUIIntializer initializer) {
/* 135 */     this.display = _display;
/* 136 */     this.initializer = initializer;
/*     */     
/* 138 */     this.splash = new Shell(this.display, 8);
/* 139 */     this.splash.setText(Constants.APP_NAME);
/* 140 */     Utils.setShellIcon(this.splash);
/*     */     
/* 142 */     this.splash.setLayout(new FillLayout());
/* 143 */     this.canvas = new Canvas(this.splash, 536870912);
/*     */     
/* 145 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 146 */     this.background = imageLoader.getImage("azureus_splash");
/* 147 */     if (ImageLoader.isRealImage(this.background)) {
/* 148 */       this.width = this.background.getBounds().width;
/* 149 */       this.height = this.background.getBounds().height;
/*     */       
/* 151 */       this.width = 500;
/* 152 */       this.height = 250;
/*     */       
/* 154 */       this.current = new Image(this.display, this.background, 0);
/*     */     } else {
/* 156 */       this.width = 400;
/* 157 */       this.height = 80;
/* 158 */       this.background = new Image(this.display, this.width, this.height);
/* 159 */       GC gc = new GC(this.background);
/*     */       try {
/* 161 */         gc.setBackground(ColorCache.getColor(this.display, 255, 255, 255));
/* 162 */         gc.fillRectangle(0, 0, this.width, this.height);
/* 163 */         gc.drawRectangle(0, 0, this.width - 1, this.height - 1);
/* 164 */         gc.drawText(Constants.APP_NAME, 5, 5, true);
/*     */       } finally {
/* 166 */         gc.dispose();
/*     */       }
/* 168 */       this.current = new Image(this.display, this.background, 0);
/*     */     }
/*     */     
/* 171 */     this.progressBarColor = new Color(this.display, 21, 92, 198);
/* 172 */     this.textColor = new Color(this.display, 90, 90, 90);
/* 173 */     this.fadedGreyColor = new Color(this.display, 170, 170, 170);
/*     */     
/*     */ 
/* 176 */     this.pbX = 10;
/* 177 */     this.pbY = (this.height - 12);
/* 178 */     this.pbWidth = (this.width - 10 - 10);
/*     */     
/* 180 */     this.canvas.setSize(this.width, this.height);
/* 181 */     Font font = this.canvas.getFont();
/* 182 */     FontData[] fdata = font.getFontData();
/* 183 */     fdata[0].setHeight(Constants.isOSX ? 9 : 7);
/* 184 */     this.textFont = new Font(this.display, fdata);
/*     */     
/* 186 */     this.canvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent event) {
/* 188 */         if (SplashWindow.this.current == null) {
/* 189 */           return;
/*     */         }
/* 191 */         Rectangle imgBounds = SplashWindow.this.current.getBounds();
/* 192 */         Rectangle canvasBounds = SplashWindow.this.canvas.getBounds();
/* 193 */         event.gc.drawImage(SplashWindow.this.current, (canvasBounds.width - imgBounds.width) / 2, (canvasBounds.height - imgBounds.height - 30) / 2);
/*     */         
/* 195 */         GC gc = event.gc;
/*     */         try
/*     */         {
/* 198 */           gc.setAntialias(1);
/* 199 */           gc.setTextAntialias(1);
/*     */         }
/*     */         catch (Exception e) {}
/*     */         
/*     */ 
/* 204 */         int y = SplashWindow.this.pbY;
/*     */         
/* 206 */         if (SplashWindow.this.task != null) {
/* 207 */           gc.setFont(SplashWindow.this.textFont);
/* 208 */           gc.setForeground(SplashWindow.this.textColor);
/* 209 */           Point extent = gc.textExtent(SplashWindow.this.task);
/* 210 */           y = SplashWindow.this.pbY - extent.y - 5;
/* 211 */           gc.setClipping(10, y, SplashWindow.this.width - 20, extent.y);
/* 212 */           gc.drawText(SplashWindow.this.task, 10, y, true);
/* 213 */           Utils.setClipping(gc, (Rectangle)null);
/*     */         }
/*     */         
/*     */ 
/* 217 */         gc.setForeground(SplashWindow.this.fadedGreyColor);
/* 218 */         gc.setBackground(SplashWindow.this.fadedGreyColor);
/* 219 */         gc.fillRectangle(SplashWindow.this.pbX - 0, SplashWindow.this.pbY + Math.abs(0) / 2, SplashWindow.this.pbWidth + 0, 2);
/* 220 */         gc.setForeground(SplashWindow.this.progressBarColor);
/* 221 */         gc.setBackground(SplashWindow.this.progressBarColor);
/* 222 */         gc.fillRectangle(SplashWindow.this.pbX, SplashWindow.this.pbY, SplashWindow.this.percent * SplashWindow.this.pbWidth / 100, 2);
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
/* 233 */         gc.setForeground(SplashWindow.this.fadedGreyColor);
/* 234 */         gc.setBackground(SplashWindow.this.fadedGreyColor);
/* 235 */         canvasBounds.height -= 1;
/* 236 */         canvasBounds.width -= 1;
/* 237 */         gc.drawRectangle(canvasBounds);
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 244 */     });
/* 245 */     this.splash.setSize(this.width, this.height);
/*     */     
/* 247 */     Utils.centreWindow(this.splash);
/* 248 */     this.splash.open();
/*     */     
/* 250 */     if (initializer != null) {
/* 251 */       initializer.addListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void create(Display display, final IUIIntializer initializer) {
/* 256 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 258 */         if ((this.val$display == null) || (this.val$display.isDisposed())) {
/* 259 */           return;
/*     */         }
/* 261 */         new SplashWindow(this.val$display, initializer);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void closeSplash()
/*     */   {
/* 270 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/*     */         try {
/* 273 */           if (SplashWindow.this.initializer != null)
/* 274 */             SplashWindow.this.initializer.removeListener(SplashWindow.this);
/* 275 */           if ((SplashWindow.this.splash != null) && (!SplashWindow.this.splash.isDisposed()))
/* 276 */             SplashWindow.this.splash.dispose();
/* 277 */           if ((SplashWindow.this.current != null) && (!SplashWindow.this.current.isDisposed())) {
/* 278 */             SplashWindow.this.current.dispose();
/*     */           }
/* 280 */           if ((SplashWindow.this.progressBarColor != null) && (!SplashWindow.this.progressBarColor.isDisposed())) {
/* 281 */             SplashWindow.this.progressBarColor.dispose();
/*     */           }
/* 283 */           if ((SplashWindow.this.fadedGreyColor != null) && (!SplashWindow.this.fadedGreyColor.isDisposed())) {
/* 284 */             SplashWindow.this.fadedGreyColor.dispose();
/*     */           }
/* 286 */           if ((SplashWindow.this.textColor != null) && (!SplashWindow.this.textColor.isDisposed())) {
/* 287 */             SplashWindow.this.textColor.dispose();
/*     */           }
/* 289 */           if ((SplashWindow.this.textFont != null) && (!SplashWindow.this.textFont.isDisposed())) {
/* 290 */             SplashWindow.this.textFont.dispose();
/*     */           }
/*     */           
/* 293 */           ImageLoader imageLoader = ImageLoader.getInstance();
/* 294 */           imageLoader.releaseImage("azureus_splash");
/* 295 */           imageLoader.collectGarbage();
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportCurrentTask(String task)
/*     */   {
/* 306 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 307 */       return;
/*     */     }
/* 309 */     if ((this.task == null) || (this.task.compareTo(task) != 0)) {
/* 310 */       this.task = task;
/* 311 */       update();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void update()
/*     */   {
/* 321 */     if ((this.updating) && (!Utils.isThisThreadSWT())) {
/* 322 */       return;
/*     */     }
/*     */     
/* 325 */     this.updating = true;
/*     */     
/* 327 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 329 */         SplashWindow.this.updating = false;
/* 330 */         if ((SplashWindow.this.splash == null) || (SplashWindow.this.splash.isDisposed())) {
/* 331 */           return;
/*     */         }
/*     */         
/* 334 */         SplashWindow.this.canvas.redraw(0, SplashWindow.this.height - 50, SplashWindow.this.width, SplashWindow.this.height, true);
/* 335 */         SplashWindow.this.canvas.update();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public int getPercent() {
/* 341 */     return this.percent;
/*     */   }
/*     */   
/*     */ 
/*     */   public void reportPercent(int percent)
/*     */   {
/* 347 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 348 */       return;
/*     */     }
/*     */     
/* 351 */     if (percent > 100) {
/* 352 */       closeSplash();
/* 353 */       return;
/*     */     }
/*     */     
/* 356 */     if (this.percent != percent) {
/* 357 */       this.percent = percent;
/* 358 */       update();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/SplashWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */