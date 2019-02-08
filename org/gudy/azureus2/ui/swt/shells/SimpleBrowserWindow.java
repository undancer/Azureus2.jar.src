/*     */ package org.gudy.azureus2.ui.swt.shells;
/*     */ 
/*     */ import org.eclipse.swt.browser.CloseWindowListener;
/*     */ import org.eclipse.swt.browser.ProgressEvent;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.browser.TitleEvent;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.browser.WindowEvent;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ public class SimpleBrowserWindow
/*     */ {
/*     */   private Shell shell;
/*     */   private BrowserWrapper browser;
/*     */   
/*     */   public SimpleBrowserWindow(Shell parent, String url, double wPct, double hPct, boolean allowResize, boolean isModal)
/*     */   {
/*  42 */     if (parent == null) {
/*  43 */       init(parent, url, 0, 0, allowResize, isModal);
/*     */     } else {
/*  45 */       Rectangle clientArea = parent.getClientArea();
/*  46 */       init(parent, url, (int)(clientArea.width * wPct), (int)(clientArea.height * hPct), allowResize, isModal);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleBrowserWindow(Shell parent, String url, int w, int h, boolean allowResize, boolean isModal)
/*     */   {
/*  53 */     init(parent, url, w, h, allowResize, isModal);
/*     */   }
/*     */   
/*     */   private void init(Shell parent, String url, int w, int h, boolean allowResize, boolean isModal)
/*     */   {
/*  58 */     if (parent == null) {
/*  59 */       parent = Utils.findAnyShell();
/*     */     }
/*     */     
/*  62 */     int style = 2144;
/*  63 */     if (allowResize) {
/*  64 */       style |= 0x10;
/*     */     }
/*  66 */     if (isModal) {
/*  67 */       style |= 0x10000;
/*     */     }
/*  69 */     this.shell = ShellFactory.createShell(parent, style);
/*     */     
/*  71 */     this.shell.setLayout(new FillLayout());
/*     */     
/*  73 */     Utils.setShellIcon(this.shell);
/*     */     
/*  75 */     this.browser = Utils.createSafeBrowser(this.shell, 0);
/*  76 */     if (this.browser == null) {
/*  77 */       this.shell.dispose();
/*  78 */       return;
/*     */     }
/*     */     
/*  81 */     this.browser.addProgressListener(new ProgressListener() {
/*     */       public void completed(ProgressEvent event) {
/*  83 */         SimpleBrowserWindow.this.shell.open();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void changed(ProgressEvent event) {}
/*  89 */     });
/*  90 */     this.browser.addCloseWindowListener(new CloseWindowListener() {
/*     */       public void close(WindowEvent event) {
/*  92 */         if ((SimpleBrowserWindow.this.shell == null) || (SimpleBrowserWindow.this.shell.isDisposed())) {
/*  93 */           return;
/*     */         }
/*  95 */         SimpleBrowserWindow.this.shell.dispose();
/*     */       }
/*     */       
/*  98 */     });
/*  99 */     this.browser.addTitleListener(new TitleListener()
/*     */     {
/*     */       public void changed(TitleEvent event) {
/* 102 */         if ((SimpleBrowserWindow.this.shell == null) || (SimpleBrowserWindow.this.shell.isDisposed())) {
/* 103 */           return;
/*     */         }
/* 105 */         SimpleBrowserWindow.this.shell.setText(event.title);
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 110 */     if ((w > 0) && (h > 0)) {
/* 111 */       this.shell.setSize(w, h);
/*     */     }
/*     */     
/* 114 */     Utils.centerWindowRelativeTo(this.shell, parent);
/* 115 */     this.browser.setUrl(url);
/* 116 */     this.browser.setData("StartURL", url);
/*     */   }
/*     */   
/*     */   public void waitUntilClosed() {
/* 120 */     Display display = this.shell.getDisplay();
/* 121 */     while (!this.shell.isDisposed()) {
/* 122 */       if (!display.readAndDispatch()) {
/* 123 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 129 */     Display display = new Display();
/* 130 */     Shell shell = new Shell(display, 2144);
/* 131 */     shell.setSize(800, 600);
/*     */     
/* 133 */     new SimpleBrowserWindow(shell, "http://google.com", 0.8D, 0.5D, true, false);
/*     */     
/* 135 */     shell.open();
/*     */     
/* 137 */     while (!shell.isDisposed()) {
/* 138 */       if (!display.readAndDispatch()) {
/* 139 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/SimpleBrowserWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */