/*     */ package org.gudy.azureus2.ui.swt.shells;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
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
/*     */ public class ShellDocker
/*     */ {
/*  42 */   private DockPosition anchorControlPosition = new DockPosition();
/*     */   
/*  44 */   private boolean isDocked = true;
/*     */   
/*  46 */   private boolean moveWithShell = true;
/*     */   
/*  48 */   private boolean resizeWithShell = false;
/*     */   
/*  50 */   private Listener dockingEnabler = null;
/*     */   
/*  52 */   private Control anchorControl = null;
/*     */   
/*  54 */   private Shell dockedShell = null;
/*     */   
/*  56 */   private Shell mainShell = null;
/*     */   
/*     */   public ShellDocker(Control anchorControl, Shell dockedShell) {
/*  59 */     if ((null == anchorControl) || (anchorControl.isDisposed())) {
/*  60 */       throw new NullPointerException("anchorControl cannot be null or disposed");
/*     */     }
/*  62 */     if ((null == dockedShell) || (dockedShell.isDisposed())) {
/*  63 */       throw new NullPointerException("dockedShell cannot be null or disposed");
/*     */     }
/*     */     
/*  66 */     this.anchorControl = anchorControl;
/*  67 */     this.dockedShell = dockedShell;
/*  68 */     this.mainShell = anchorControl.getShell();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void openShell()
/*     */   {
/*  75 */     openShell(isDocked(), false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void openShell(boolean isDocked)
/*     */   {
/*  83 */     openShell(isDocked, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void openShell(boolean isDocked, boolean isAnimated)
/*     */   {
/*  92 */     setDocked(isDocked);
/*     */     
/*  94 */     if (!isDocked)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  99 */       UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 100 */       if (null == uiFunctions)
/*     */       {
/*     */ 
/*     */ 
/* 104 */         Utils.centreWindow(this.dockedShell);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 109 */         Utils.centerWindowRelativeTo(this.dockedShell, uiFunctions.getMainShell());
/*     */       }
/*     */     }
/*     */     
/* 113 */     if (!isAnimated) {
/* 114 */       this.dockedShell.open();
/*     */     }
/*     */     else {
/* 117 */       this.dockedShell.open();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isDocked()
/*     */   {
/* 123 */     return this.isDocked;
/*     */   }
/*     */   
/*     */   public void setDocked(boolean isDocked) {
/* 127 */     this.isDocked = isDocked;
/*     */     
/* 129 */     if (isDocked)
/*     */     {
/* 131 */       performDocking();
/*     */       
/* 133 */       if (null == this.dockingEnabler) {
/* 134 */         this.dockingEnabler = new Listener()
/*     */         {
/*     */           public void handleEvent(Event event) {
/* 137 */             if (event.type == 11)
/*     */             {
/* 139 */               if (ShellDocker.this.isResizeWithShell()) {
/* 140 */                 System.out.println("resizing");
/*     */               } else {
/* 142 */                 ShellDocker.this.performDocking();
/*     */               }
/* 144 */             } else if (event.type == 10) {
/* 145 */               ShellDocker.this.performDocking();
/*     */             }
/*     */           }
/*     */         };
/*     */       }
/*     */       
/*     */ 
/* 152 */       if ((null != this.mainShell) && (!this.mainShell.isDisposed())) {
/* 153 */         if (isMoveWithShell()) {
/* 154 */           this.mainShell.addListener(10, this.dockingEnabler);
/*     */         }
/* 156 */         if (isResizeWithShell()) {
/* 157 */           this.mainShell.addListener(11, this.dockingEnabler);
/*     */         }
/* 159 */         this.anchorControl.addListener(10, this.dockingEnabler);
/* 160 */         this.anchorControl.addListener(11, this.dockingEnabler);
/*     */       }
/*     */       
/* 163 */       this.anchorControl.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/* 165 */           ShellDocker.this.setDocked(false);
/*     */         }
/*     */         
/* 168 */       });
/* 169 */       this.dockedShell.addListener(21, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 171 */           ShellDocker.this.setDocked(false);
/*     */         }
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/* 177 */       if ((null != this.mainShell) && (!this.mainShell.isDisposed()) && 
/* 178 */         (null != this.dockingEnabler)) {
/* 179 */         this.mainShell.removeListener(10, this.dockingEnabler);
/* 180 */         this.mainShell.removeListener(11, this.dockingEnabler);
/*     */       }
/*     */       
/* 183 */       if ((null != this.anchorControl) && (!this.anchorControl.isDisposed()) && 
/* 184 */         (null != this.dockingEnabler)) {
/* 185 */         this.anchorControl.removeListener(10, this.dockingEnabler);
/* 186 */         this.anchorControl.removeListener(11, this.dockingEnabler);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void performDocking()
/*     */   {
/* 194 */     if (isAlive()) {
/* 195 */       switch (this.anchorControlPosition.getPosition()) {
/*     */       case 1: 
/* 197 */         this.dockedShell.setLocation(this.mainShell.toDisplay(this.anchorControl.getLocation()));
/* 198 */         break;
/*     */       case 3: 
/*     */         break;
/*     */       
/*     */       case 2: 
/* 203 */         Point p = this.mainShell.toDisplay(this.anchorControl.getLocation());
/* 204 */         p.x += this.anchorControlPosition.getOffset().xOffset;
/* 205 */         p.y += this.anchorControlPosition.getOffset().yOffset;
/* 206 */         p.y += this.anchorControl.getSize().y;
/* 207 */         this.dockedShell.setLocation(p);
/*     */         
/* 209 */         break;
/*     */       
/*     */       case 4: 
/* 212 */         Point p = this.mainShell.toDisplay(this.anchorControl.getLocation());
/* 213 */         p.x += this.anchorControlPosition.getOffset().xOffset;
/* 214 */         p.y += this.anchorControlPosition.getOffset().yOffset;
/*     */         
/* 216 */         p.x += this.anchorControl.getSize().x;
/* 217 */         p.y += this.anchorControl.getSize().y;
/* 218 */         this.dockedShell.setLocation(p);
/*     */         
/* 220 */         break;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isAlive()
/*     */   {
/* 230 */     if ((null == this.mainShell) || (this.mainShell.isDisposed())) {
/* 231 */       System.err.println("\tmainshell is disposed?");
/* 232 */       return false;
/*     */     }
/* 234 */     if ((null == this.dockedShell) || (this.dockedShell.isDisposed())) {
/* 235 */       System.err.println("\tdockedShell is disposed?");
/* 236 */       return false;
/*     */     }
/*     */     
/* 239 */     if ((null == this.anchorControl) || (this.anchorControl.isDisposed())) {
/* 240 */       System.err.println("\tanchorControl is disposed?");
/* 241 */       return false;
/*     */     }
/*     */     
/* 244 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isMoveWithShell() {
/* 248 */     return this.moveWithShell;
/*     */   }
/*     */   
/*     */   public void setMoveWithShell(boolean moveWithShell) {
/* 252 */     this.moveWithShell = moveWithShell;
/*     */   }
/*     */   
/*     */   public boolean isResizeWithShell() {
/* 256 */     return this.resizeWithShell;
/*     */   }
/*     */   
/*     */   public void setResizeWithShell(boolean resizeWithShell) {
/* 260 */     this.resizeWithShell = resizeWithShell;
/*     */   }
/*     */   
/*     */   public DockPosition getAnchorControlPosition() {
/* 264 */     return this.anchorControlPosition;
/*     */   }
/*     */   
/*     */   public void setAnchorControlPosition(DockPosition anchorControlPosition) {
/* 268 */     this.anchorControlPosition = anchorControlPosition;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/ShellDocker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */