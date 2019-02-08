/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AERunnableBoolean;
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
/*     */ 
/*     */ public class SWTSkinUtils
/*     */ {
/*     */   public static final int TILE_NONE = 0;
/*     */   public static final int TILE_Y = 1;
/*     */   public static final int TILE_X = 2;
/*     */   public static final int TILE_CENTER_X = 4;
/*     */   public static final int TILE_CENTER_Y = 8;
/*     */   public static final int TILE_BOTH = 3;
/*  59 */   private static Listener imageDownListener = new SWTSkinImageChanger("-down", 3, 4);
/*     */   
/*     */ 
/*     */   public static int getAlignment(String sAlign, int def)
/*     */   {
/*     */     int align;
/*     */     int align;
/*  66 */     if (sAlign == null) {
/*  67 */       align = def; } else { int align;
/*  68 */       if (sAlign.equalsIgnoreCase("center")) {
/*  69 */         align = 16777216; } else { int align;
/*  70 */         if (sAlign.equalsIgnoreCase("bottom")) {
/*  71 */           align = 1024; } else { int align;
/*  72 */           if (sAlign.equalsIgnoreCase("top")) {
/*  73 */             align = 128; } else { int align;
/*  74 */             if (sAlign.equalsIgnoreCase("left")) {
/*  75 */               align = 16384; } else { int align;
/*  76 */               if (sAlign.equalsIgnoreCase("right")) {
/*  77 */                 align = 131072;
/*     */               } else
/*  79 */                 align = def;
/*     */             }
/*     */           } } } }
/*  82 */     return align;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getTileMode(String sTileMode)
/*     */   {
/*  90 */     int tileMode = 0;
/*  91 */     if ((sTileMode == null) || (sTileMode == "")) {
/*  92 */       return tileMode;
/*     */     }
/*     */     
/*  95 */     sTileMode = sTileMode.toLowerCase();
/*     */     
/*  97 */     if (sTileMode.equals("tile")) {
/*  98 */       tileMode = 3;
/*  99 */     } else if (sTileMode.equals("tile-x")) {
/* 100 */       tileMode = 2;
/* 101 */     } else if (sTileMode.equals("tile-y")) {
/* 102 */       tileMode = 1;
/* 103 */     } else if (sTileMode.equals("center-x")) {
/* 104 */       tileMode = 4;
/* 105 */     } else if (sTileMode.equals("center-y")) {
/* 106 */       tileMode = 8;
/*     */     }
/*     */     
/* 109 */     return tileMode;
/*     */   }
/*     */   
/*     */   static void addMouseImageChangeListeners(Control widget) {
/* 113 */     if (widget.getData("hasMICL") != null) {
/* 114 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 121 */     widget.addListener(3, imageDownListener);
/* 122 */     widget.addListener(4, imageDownListener);
/*     */     
/* 124 */     widget.setData("hasMICL", "1");
/*     */   }
/*     */   
/*     */   public static void setVisibility(SWTSkin skin, String configID, String viewID, boolean visible)
/*     */   {
/* 129 */     setVisibility(skin, configID, viewID, visible, true, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setVisibility(SWTSkin skin, String configID, String viewID, boolean visible, boolean save, boolean fast)
/*     */   {
/* 135 */     SWTSkinObject skinObject = skin.getSkinObject(viewID);
/*     */     
/* 137 */     if (skinObject == null) {
/* 138 */       Debug.out("setVisibility on non existing skin object: " + viewID);
/* 139 */       return;
/*     */     }
/*     */     
/* 142 */     if ((skinObject.isVisible() == visible) && (skin.getShell().isVisible())) {
/* 143 */       return;
/*     */     }
/*     */     
/* 146 */     Control control = skinObject.getControl();
/*     */     
/* 148 */     if ((control != null) && (!control.isDisposed())) {
/*     */       Point size;
/* 150 */       if (visible) {
/* 151 */         FormData fd = (FormData)control.getLayoutData();
/* 152 */         Point size = (Point)control.getData("v3.oldHeight");
/*     */         
/* 154 */         if (size == null) {
/* 155 */           size = control.computeSize(-1, -1);
/* 156 */           if (fd.height > 0) {
/* 157 */             size.y = fd.height;
/*     */           }
/* 159 */           if (fd.width > 0) {
/* 160 */             size.x = fd.width;
/*     */           }
/*     */         }
/*     */       } else {
/* 164 */         size = new Point(0, 0);
/*     */       }
/* 166 */       setVisibility(skin, configID, skinObject, size, save, fast, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setVisibility(SWTSkin skin, String configID, SWTSkinObject skinObject, Point destSize, boolean save, boolean fast, Runnable runAfterSlide)
/*     */   {
/* 173 */     boolean visible = (destSize.x != 0) || (destSize.y != 0);
/*     */     try {
/* 175 */       if (skinObject == null) {
/*     */         return;
/*     */       }
/* 178 */       Control control = skinObject.getControl();
/* 179 */       if ((control != null) && (!control.isDisposed())) {
/* 180 */         if (visible) {
/* 181 */           FormData fd = (FormData)control.getLayoutData();
/* 182 */           fd.width = 0;
/* 183 */           fd.height = 0;
/* 184 */           control.setData("oldSize", new Point(0, 0));
/*     */           
/* 186 */           skinObject.setVisible(visible);
/*     */           
/*     */ 
/*     */ 
/* 190 */           fd = (FormData)control.getLayoutData();
/*     */           
/* 192 */           if ((fd.width != 0) || (fd.height != 0)) {
/*     */             return;
/*     */           }
/*     */           
/*     */ 
/* 197 */           if ((fd.width != destSize.x) || (fd.height != destSize.y)) {
/* 198 */             if (fast) {
/* 199 */               fd.width = destSize.x;
/* 200 */               fd.height = destSize.y;
/* 201 */               control.setLayoutData(fd);
/* 202 */               Utils.relayout(control);
/*     */             } else {
/* 204 */               slide(skinObject, fd, destSize, runAfterSlide);
/* 205 */               runAfterSlide = null;
/*     */             }
/*     */           }
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
/* 218 */           control.setData("v3.oldHeight", null);
/*     */         } else {
/* 220 */           FormData fd = (FormData)control.getLayoutData();
/* 221 */           if (fd != null) {
/* 222 */             Point oldSize = new Point(fd.width, fd.height);
/* 223 */             if (oldSize.y <= 0) {
/* 224 */               oldSize = null;
/*     */             }
/* 226 */             control.setData("v3.oldHeight", oldSize);
/*     */             
/* 228 */             if (fast) {
/* 229 */               skinObject.setVisible(false);
/*     */             } else {
/* 231 */               slide(skinObject, fd, destSize, runAfterSlide);
/* 232 */               runAfterSlide = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 239 */       if ((save) && (COConfigurationManager.getBooleanParameter(configID) != visible))
/*     */       {
/* 241 */         COConfigurationManager.setParameter(configID, visible);
/*     */       }
/*     */       
/* 244 */       if (runAfterSlide != null) {
/* 245 */         runAfterSlide.run();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void slide(final SWTSkinObject skinObject, final FormData fd, final Point destSize, final Runnable runOnCompletion)
/*     */   {
/* 252 */     Control control = skinObject.getControl();
/*     */     
/* 254 */     Boolean exit = Utils.execSWTThreadWithBool("slide", new AERunnableBoolean()
/*     */     {
/*     */       public boolean runSupport() {
/* 257 */         boolean exit = this.val$control.getData("slide.active") != null;
/* 258 */         Runnable oldROC = (Runnable)this.val$control.getData("slide.runOnCompletion");
/* 259 */         if (oldROC != null) {
/* 260 */           oldROC.run();
/*     */         }
/* 262 */         this.val$control.setData("slide.destSize", destSize);
/* 263 */         this.val$control.setData("slide.runOnCompletion", runOnCompletion);
/* 264 */         if (destSize.y > 0) {
/* 265 */           skinObject.setVisible(true);
/*     */         }
/* 267 */         return exit; } }, 1000L);
/*     */     
/*     */ 
/*     */ 
/* 271 */     if ((exit == null) || (exit.booleanValue())) {
/* 272 */       return;
/*     */     }
/*     */     
/* 275 */     AERunnable runnable = new AERunnable() {
/* 276 */       boolean firstTime = true;
/*     */       
/* 278 */       float pct = 0.4F;
/*     */       
/*     */       public void runSupport() {
/* 281 */         if (this.val$control.isDisposed()) {
/* 282 */           return;
/*     */         }
/* 284 */         Point size = (Point)this.val$control.getData("slide.destSize");
/* 285 */         if (size == null) {
/* 286 */           return;
/*     */         }
/*     */         
/* 289 */         if (this.firstTime) {
/* 290 */           this.firstTime = false;
/* 291 */           this.val$control.setData("slide.active", "1");
/*     */         }
/*     */         
/* 294 */         int newWidth = (int)(fd.width + (size.x - fd.width) * this.pct);
/* 295 */         int h = fd.height >= 0 ? fd.height : this.val$control.getSize().y;
/* 296 */         int newHeight = (int)(h + (size.y - h) * this.pct);
/* 297 */         this.pct = ((float)(this.pct + 0.01D));
/*     */         
/*     */ 
/* 300 */         if ((newWidth == fd.width) && (newHeight == h)) {
/* 301 */           fd.width = size.x;
/* 302 */           fd.height = size.y;
/*     */           
/* 304 */           this.val$control.setLayoutData(fd);
/* 305 */           Utils.relayout(this.val$control);
/* 306 */           this.val$control.getParent().layout();
/*     */           
/* 308 */           this.val$control.setData("slide.active", null);
/* 309 */           this.val$control.setData("slide.destSize", null);
/*     */           
/* 311 */           if (newHeight == 0) {
/* 312 */             skinObject.setVisible(false);
/* 313 */             Utils.relayout(this.val$control);
/*     */           }
/*     */           
/* 316 */           Runnable oldROC = (Runnable)this.val$control.getData("slide.runOnCompletion");
/* 317 */           if (oldROC != null) {
/* 318 */             this.val$control.setData("slide.runOnCompletion", null);
/* 319 */             oldROC.run();
/*     */           }
/*     */         } else {
/* 322 */           fd.width = newWidth;
/* 323 */           fd.height = newHeight;
/* 324 */           this.val$control.setLayoutData(fd);
/*     */           
/* 326 */           this.val$control.getParent().layout();
/*     */           
/* 328 */           Utils.execSWTThreadLater(20, this);
/*     */         }
/*     */       }
/* 331 */     };
/* 332 */     control.getDisplay().asyncExec(runnable);
/*     */   }
/*     */   
/*     */ 
/*     */   public static class MouseEnterExitListener
/*     */     implements Listener
/*     */   {
/* 339 */     boolean bOver = false;
/*     */     
/*     */     public MouseEnterExitListener(Widget widget)
/*     */     {
/* 343 */       widget.addListener(5, this);
/* 344 */       widget.addListener(7, this);
/*     */     }
/*     */     
/*     */     public void handleEvent(Event event) {
/* 348 */       Control control = (Control)event.widget;
/*     */       
/* 350 */       SWTSkinObject skinObject = (SWTSkinObject)control.getData("SkinObject");
/*     */       
/* 352 */       if (event.type == 5) {
/* 353 */         if (this.bOver) {
/* 354 */           return;
/*     */         }
/* 356 */         System.out.println(System.currentTimeMillis() + ": " + skinObject + "-- OVER");
/*     */         
/* 358 */         this.bOver = true;
/* 359 */         skinObject.switchSuffix("-over", 2, true);
/*     */       }
/*     */       else {
/* 362 */         this.bOver = false;
/* 363 */         System.out.println(System.currentTimeMillis() + ": " + skinObject + "-- NOOVER");
/*     */         
/* 365 */         skinObject.switchSuffix("", 2, true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static SWTSkinObjectBrowser findBrowserSO(SWTSkinObject so)
/*     */   {
/* 374 */     if ((so instanceof SWTSkinObjectBrowser)) {
/* 375 */       return (SWTSkinObjectBrowser)so;
/*     */     }
/* 377 */     if ((so instanceof SWTSkinObjectContainer)) {
/* 378 */       SWTSkinObjectContainer soContainer = (SWTSkinObjectContainer)so;
/* 379 */       SWTSkinObject[] children = soContainer.getChildren();
/* 380 */       for (int i = 0; i < children.length; i++) {
/* 381 */         SWTSkinObject child = children[i];
/* 382 */         SWTSkinObjectBrowser found = findBrowserSO(child);
/* 383 */         if (found != null) {
/* 384 */           return found;
/*     */         }
/*     */       }
/*     */     }
/* 388 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */