/*     */ package org.gudy.azureus2.ui.swt.components.shell;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
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
/*     */ public final class ShellFactory
/*     */ {
/*     */   public static Shell createMainShell(int styles)
/*     */   {
/*  48 */     Shell parent = null;
/*     */     
/*  50 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */     
/*  52 */     if (uiFunctions != null)
/*     */     {
/*  54 */       parent = uiFunctions.getMainShell();
/*     */     }
/*     */     
/*  57 */     if (parent == null)
/*     */     {
/*  59 */       return createShell(SWTThread.getInstance().getDisplay());
/*     */     }
/*     */     
/*  62 */     return createShell(parent, styles);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Shell createShell(Display disp, int styles)
/*     */   {
/*  71 */     return getRegisteredShell(new AEShell(disp, styles, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Shell createShell(Display disp)
/*     */   {
/*  80 */     return getRegisteredShell(new AEShell(disp, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Shell createShell(Shell parent, int styles)
/*     */   {
/*  89 */     if ((parent != null) && (parent.isDisposed())) {
/*  90 */       return null;
/*     */     }
/*  92 */     return getRegisteredShell(new AEShell(parent, styles, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Shell createShell(Shell parent)
/*     */   {
/* 101 */     return getRegisteredShell(new AEShell(parent, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Shell createShell(int styles)
/*     */   {
/* 110 */     return getRegisteredShell(new AEShell(styles, null));
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
/*     */   private static Shell getRegisteredShell(Shell toRegister)
/*     */   {
/* 140 */     if (null == toRegister) {
/* 141 */       return null;
/*     */     }
/* 143 */     if (Constants.isOSX) {
/* 144 */       UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 145 */       if (uiFunctions == null) {
/* 146 */         System.err.println("Main window is not initialized yet");
/*     */       } else {
/* 148 */         uiFunctions.createMainMenu(toRegister);
/*     */       }
/*     */     }
/*     */     
/* 152 */     ShellManager.sharedManager().addWindow(toRegister);
/*     */     
/* 154 */     return toRegister;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class AEShell
/*     */     extends Shell
/*     */   {
/*     */     private AEShell(int styles)
/*     */     {
/* 167 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private AEShell(Display display)
/*     */     {
/* 174 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private AEShell(Display display, int styles)
/*     */     {
/* 181 */       super(fixupStyle(styles));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private AEShell(Shell parent)
/*     */     {
/* 188 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private AEShell(Shell parent, int styles)
/*     */     {
/* 195 */       super(fixupStyle(styles));
/*     */     }
/*     */     
/*     */     private static int fixupStyle(int style) {
/* 199 */       if (((style & 0x38000) != 0) && (Utils.anyShellHaveStyle(16416)))
/*     */       {
/* 201 */         UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 202 */         if ((uiFunctions != null) && (uiFunctions.getMainShell() != null)) {
/* 203 */           style |= 0x4000;
/*     */         }
/*     */       }
/* 206 */       return style;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void checkSubclass() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setImage(Image image)
/*     */     {
/* 224 */       if (!Constants.isOSX) {
/* 225 */         super.setImage(image);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setImages(Image[] images)
/*     */     {
/* 237 */       if (!Constants.isOSX) {
/* 238 */         super.setImages(images);
/*     */       }
/*     */     }
/*     */     
/*     */     public Point computeSize(int wHint, int hHint) {
/* 243 */       if ((!this.inSetSize) && (wHint > 0) && (hHint == -1)) {
/* 244 */         this.inSetSize = true;
/* 245 */         return super.computeSize(Utils.adjustPXForDPI(wHint), hHint);
/*     */       }
/* 247 */       return super.computeSize(wHint, hHint);
/*     */     }
/*     */     
/* 250 */     private boolean inSetSize = false;
/*     */     
/*     */     public void setAdjustPXforDPI(boolean adjust) {
/* 253 */       this.inSetSize = (!adjust);
/*     */     }
/*     */     
/*     */     public void setSize(int width, int height)
/*     */     {
/* 258 */       if (this.inSetSize) {
/* 259 */         super.setSize(width, height);
/* 260 */         return;
/*     */       }
/* 262 */       this.inSetSize = true;
/*     */       try {
/* 264 */         width = Utils.adjustPXForDPI(width);
/* 265 */         height = Utils.adjustPXForDPI(height);
/* 266 */         super.setSize(width, height);
/*     */       } finally {
/* 268 */         this.inSetSize = false;
/*     */       }
/*     */     }
/*     */     
/*     */     public void pack()
/*     */     {
/* 274 */       this.inSetSize = true;
/*     */       try {
/* 276 */         super.pack();
/*     */       } finally {
/* 278 */         this.inSetSize = false;
/*     */       }
/*     */     }
/*     */     
/*     */     public void setSize(Point size)
/*     */     {
/* 284 */       if (this.inSetSize) {
/* 285 */         super.setSize(size);
/* 286 */         return;
/*     */       }
/* 288 */       this.inSetSize = true;
/*     */       try {
/* 290 */         super.setSize(Utils.adjustPXForDPI(size));
/*     */       } finally {
/* 292 */         this.inSetSize = false;
/*     */       }
/*     */     }
/*     */     
/*     */     public void open() {
/* 297 */       UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 298 */       if (uiFunctions != null) {
/* 299 */         Boolean bringToFront = (Boolean)getData("bringToFront");
/* 300 */         if ((bringToFront == null) || (bringToFront.booleanValue())) {
/* 301 */           Shell mainShell = uiFunctions.getMainShell();
/* 302 */           if ((mainShell != null) && (mainShell.getMinimized())) {
/* 303 */             uiFunctions.bringToFront();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 308 */       Shell firstShellWithStyle = Utils.findFirstShellWithStyle(65536);
/* 309 */       if ((firstShellWithStyle != null) && (firstShellWithStyle != this))
/*     */       {
/*     */ 
/*     */ 
/* 313 */         firstShellWithStyle.addDisposeListener(new DisposeListener()
/*     */         {
/*     */           public void widgetDisposed(DisposeEvent e)
/*     */           {
/* 317 */             Utils.execSWTThreadLater(0, new AERunnable() {
/*     */               public void runSupport() {
/* 319 */                 ShellFactory.AEShell.this.open();
/*     */               }
/*     */             });
/*     */           }
/* 323 */         });
/* 324 */         firstShellWithStyle.setVisible(true);
/* 325 */         firstShellWithStyle.forceActive();
/*     */       }
/* 327 */       else if (!isDisposed()) {
/* 328 */         super.open();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/shell/ShellFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */