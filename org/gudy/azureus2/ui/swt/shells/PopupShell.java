/*    */ package org.gudy.azureus2.ui.swt.shells;
/*    */ 
/*    */ import org.eclipse.swt.layout.FormLayout;
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.eclipse.swt.widgets.Shell;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PopupShell
/*    */ {
/*    */   protected Shell shell;
/*    */   public static final String IMG_INFORMATION = "information";
/*    */   
/*    */   public PopupShell(Display display)
/*    */   {
/* 46 */     this(display, 16384);
/*    */   }
/*    */   
/*    */   public PopupShell(Display display, int type)
/*    */   {
/* 51 */     if (display.isDisposed()) {
/* 52 */       return;
/*    */     }
/*    */     
/* 55 */     this.shell = new Shell(display, type);
/*    */     
/* 57 */     this.shell.setSize(250, 150);
/* 58 */     Utils.setShellIcon(this.shell);
/*    */     
/* 60 */     FormLayout layout = new FormLayout();
/* 61 */     layout.marginHeight = 0;
/* 62 */     layout.marginWidth = 0;
/*    */     try {
/* 64 */       layout.spacing = 0;
/*    */     }
/*    */     catch (NoSuchFieldError e) {}catch (Throwable e)
/*    */     {
/* 68 */       Debug.printStackTrace(e);
/*    */     }
/*    */     
/* 71 */     this.shell.setLayout(layout);
/*    */   }
/*    */   
/*    */   protected void layout() {
/* 75 */     this.shell.layout();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/PopupShell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */