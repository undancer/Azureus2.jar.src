/*     */ package org.gudy.azureus2.ui.swt.shells;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
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
/*     */ public class ShellSlider
/*     */ {
/*  31 */   private final boolean DEBUG = false;
/*     */   
/*  33 */   private int STEP = 8;
/*     */   
/*  35 */   private int PAUSE = 30;
/*     */   
/*     */   private Shell shell;
/*     */   
/*  39 */   private Rectangle shellBounds = null;
/*     */   
/*     */ 
/*     */ 
/*     */   private Rectangle endBounds;
/*     */   
/*     */ 
/*     */   private final int direction;
/*     */   
/*     */ 
/*     */   private final boolean slideIn;
/*     */   
/*     */ 
/*     */ 
/*     */   public ShellSlider(final Shell shell, int direction, final Rectangle endBounds)
/*     */   {
/*  55 */     this.shell = shell;
/*  56 */     this.endBounds = endBounds;
/*  57 */     this.slideIn = true;
/*  58 */     this.direction = direction;
/*     */     
/*  60 */     if ((shell == null) || (shell.isDisposed())) {
/*  61 */       return;
/*     */     }
/*  63 */     Display display = shell.getDisplay();
/*  64 */     display.syncExec(new Runnable() {
/*     */       public void run() {
/*  66 */         if (shell.isDisposed()) {
/*  67 */           return;
/*     */         }
/*  69 */         switch (ShellSlider.this.direction)
/*     */         {
/*     */         }
/*  72 */         shell.setLocation(endBounds.x, endBounds.y);
/*  73 */         Rectangle displayBounds = null;
/*     */         try {
/*  75 */           boolean ok = false;
/*  76 */           Monitor[] monitors = shell.getDisplay().getMonitors();
/*  77 */           for (int i = 0; i < monitors.length; i++) {
/*  78 */             Monitor monitor = monitors[i];
/*  79 */             displayBounds = monitor.getBounds();
/*  80 */             if (displayBounds.contains(endBounds.x, endBounds.y)) {
/*  81 */               ok = true;
/*  82 */               break;
/*     */             }
/*     */           }
/*  85 */           if (!ok) {
/*  86 */             displayBounds = shell.getMonitor().getBounds();
/*     */           }
/*     */         } catch (Throwable t) {
/*  89 */           displayBounds = shell.getDisplay().getBounds();
/*     */         }
/*     */         
/*  92 */         ShellSlider.this.shellBounds = new Rectangle(endBounds.x, displayBounds.y + displayBounds.height, endBounds.width, 0);
/*     */         
/*     */ 
/*     */ 
/*  96 */         shell.setBounds(ShellSlider.this.shellBounds);
/*  97 */         shell.setVisible(true);
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
/*     */   public ShellSlider(Shell shell, int direction)
/*     */   {
/* 112 */     this.shell = shell;
/* 113 */     this.slideIn = false;
/* 114 */     this.direction = direction;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean canContinue()
/*     */   {
/* 124 */     if ((this.shell == null) || (this.shell.isDisposed())) {
/* 125 */       return false;
/*     */     }
/* 127 */     if (this.shellBounds == null) {
/* 128 */       return true;
/*     */     }
/*     */     
/* 131 */     if (this.slideIn) {
/* 132 */       if (this.direction == 128) {
/* 133 */         return this.shellBounds.y > this.endBounds.y;
/*     */       }
/*     */       
/*     */     }
/* 137 */     else if (this.direction == 131072)
/*     */     {
/*     */ 
/* 140 */       return this.shellBounds.width > 10;
/*     */     }
/*     */     
/* 143 */     return false;
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/* 148 */     while (canContinue()) {
/* 149 */       long lStartedAt = System.currentTimeMillis();
/*     */       
/* 151 */       this.shell.getDisplay().syncExec(new AERunnable() {
/*     */         public void runSupport() {
/* 153 */           if ((ShellSlider.this.shell == null) || (ShellSlider.this.shell.isDisposed())) {
/* 154 */             return;
/*     */           }
/*     */           
/* 157 */           if (ShellSlider.this.shellBounds == null) {
/* 158 */             ShellSlider.this.shellBounds = ShellSlider.this.shell.getBounds();
/*     */           }
/*     */           
/*     */           int delta;
/* 162 */           if (ShellSlider.this.slideIn) {
/* 163 */             switch (ShellSlider.this.direction) {
/*     */             case 128: 
/* 165 */               delta = Math.min(ShellSlider.this.endBounds.height - ShellSlider.this.shellBounds.height, ShellSlider.this.STEP);
/* 166 */               ShellSlider.this.shellBounds.height += delta;
/* 167 */               delta = Math.min(ShellSlider.this.shellBounds.y - ShellSlider.this.endBounds.y, ShellSlider.this.STEP);
/* 168 */               ShellSlider.this.shellBounds.y -= delta;
/* 169 */               break;
/*     */             
/*     */ 
/*     */             }
/*     */             
/*     */           } else {
/* 175 */             switch (ShellSlider.this.direction) {
/*     */             case 131072: 
/* 177 */               delta = Math.min(ShellSlider.this.shellBounds.width, ShellSlider.this.STEP);
/* 178 */               ShellSlider.this.shellBounds.width -= delta;
/* 179 */               ShellSlider.this.shellBounds.x += delta;
/*     */               
/* 181 */               if (ShellSlider.this.shellBounds.width == 0) {
/* 182 */                 ShellSlider.this.shell.dispose(); return;
/*     */               }
/*     */               
/*     */ 
/*     */               break;
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */ 
/* 192 */           ShellSlider.this.shell.setBounds(ShellSlider.this.shellBounds);
/* 193 */           ShellSlider.this.shell.update();
/*     */         }
/*     */       });
/*     */       try
/*     */       {
/* 198 */         long lDrawTime = System.currentTimeMillis() - lStartedAt;
/* 199 */         long lSleepTime = this.PAUSE - lDrawTime;
/* 200 */         if (lSleepTime < 15L) {
/* 201 */           double d = (lDrawTime + 15.0D) / this.PAUSE;
/* 202 */           this.PAUSE = ((int)(this.PAUSE * d));
/* 203 */           this.STEP = ((int)(this.STEP * d));
/* 204 */           lSleepTime = 15L;
/*     */         }
/* 206 */         Thread.sleep(lSleepTime);
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/ShellSlider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */