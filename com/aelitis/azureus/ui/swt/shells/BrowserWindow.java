/*     */ package com.aelitis.azureus.ui.swt.shells;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext;
/*     */ import com.aelitis.azureus.ui.swt.browser.OpenCloseSearchDetailsListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.ConfigListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.DisplayListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.MetaSearchListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.TorrentListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.VuzeListener;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.browser.CloseWindowListener;
/*     */ import org.eclipse.swt.browser.ProgressEvent;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.browser.StatusTextEvent;
/*     */ import org.eclipse.swt.browser.StatusTextListener;
/*     */ import org.eclipse.swt.browser.TitleEvent;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.browser.WindowEvent;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class BrowserWindow
/*     */ {
/*     */   private Shell shell;
/*     */   private ClientMessageContext context;
/*     */   private BrowserWrapper browser;
/*     */   
/*     */   public BrowserWindow(Shell parent, String url, double wPct, double hPct, boolean allowResize, boolean isModal)
/*     */   {
/*  62 */     if (parent == null) {
/*  63 */       init(parent, url, 0, 0, allowResize, isModal);
/*     */     } else {
/*  65 */       Rectangle clientArea = parent.getClientArea();
/*  66 */       init(parent, url, (int)(clientArea.width * wPct), (int)(clientArea.height * hPct), allowResize, isModal);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BrowserWindow(Shell parent, String url, int w, int h, boolean allowResize, boolean isModal)
/*     */   {
/*  79 */     init(parent, url, w, h, allowResize, isModal);
/*     */   }
/*     */   
/*     */   public void init(Shell parent, String url, int w, int h, boolean allowResize, boolean isModal)
/*     */   {
/*  84 */     int style = 2144;
/*  85 */     if (allowResize) {
/*  86 */       style |= 0x10;
/*     */     }
/*  88 */     if (isModal) {
/*  89 */       style |= 0x10000;
/*     */     }
/*  91 */     this.shell = ShellFactory.createShell(parent, style);
/*     */     
/*  93 */     this.shell.setLayout(new FillLayout());
/*     */     
/*  95 */     Utils.setShellIcon(this.shell);
/*     */     
/*  97 */     this.shell.setText(url);
/*     */     
/*  99 */     this.shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 101 */         if (e.detail == 2) {
/* 102 */           BrowserWindow.this.shell.dispose();
/* 103 */           e.doit = false;
/*     */         }
/*     */         
/*     */       }
/* 107 */     });
/* 108 */     final Listener escListener = new Listener() {
/*     */       public void handleEvent(Event event) {
/* 110 */         if (event.keyCode == 27) {
/* 111 */           BrowserWindow.this.shell.dispose();
/*     */         }
/*     */       }
/* 114 */     };
/* 115 */     this.shell.getDisplay().addFilter(1, escListener);
/* 116 */     this.shell.addListener(12, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 118 */         event.display.removeFilter(1, escListener);
/*     */       }
/*     */       
/*     */ 
/* 122 */     });
/* 123 */     this.browser = Utils.createSafeBrowser(this.shell, 0);
/*     */     
/* 125 */     if (this.browser == null) {
/* 126 */       this.shell.dispose();
/* 127 */       return;
/*     */     }
/*     */     
/* 130 */     this.context = new BrowserContext("browser-window" + Math.random(), this.browser, null, true);
/*     */     
/* 132 */     this.context.addMessageListener(new TorrentListener());
/* 133 */     this.context.addMessageListener(new VuzeListener());
/* 134 */     this.context.addMessageListener(new DisplayListener(this.browser));
/* 135 */     this.context.addMessageListener(new ConfigListener(this.browser));
/* 136 */     this.context.addMessageListener(new MetaSearchListener(new OpenCloseSearchDetailsListener()
/*     */     {
/*     */       public void resizeSecondaryBrowser() {}
/*     */       
/*     */ 
/*     */       public void resizeMainBrowser() {}
/*     */       
/*     */ 
/*     */       public void openSearchResults(Map params) {}
/*     */       
/*     */       public void closeSearchResults(Map params)
/*     */       {
/* 148 */         if ((BrowserWindow.this.browser.isDisposed()) || (BrowserWindow.this.browser.getShell().isDisposed())) {
/* 149 */           return;
/*     */         }
/* 151 */         BrowserWindow.this.shell.dispose();
/*     */       }
/*     */       
/*     */ 
/* 155 */     }));
/* 156 */     this.browser.addProgressListener(new ProgressListener() {
/*     */       public void completed(ProgressEvent event) {
/* 158 */         if ((BrowserWindow.this.browser.isDisposed()) || (BrowserWindow.this.browser.getShell().isDisposed())) {
/* 159 */           return;
/*     */         }
/* 161 */         BrowserWindow.this.shell.open();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void changed(ProgressEvent event) {}
/* 167 */     });
/* 168 */     this.browser.addCloseWindowListener(new CloseWindowListener() {
/*     */       public void close(WindowEvent event) {
/* 170 */         if ((BrowserWindow.this.browser.isDisposed()) || (BrowserWindow.this.browser.getShell().isDisposed())) {
/* 171 */           return;
/*     */         }
/* 173 */         BrowserWindow.this.context.debug("window.close called");
/* 174 */         BrowserWindow.this.shell.dispose();
/*     */       }
/*     */       
/* 177 */     });
/* 178 */     this.browser.addTitleListener(new TitleListener()
/*     */     {
/*     */       public void changed(TitleEvent event) {
/* 181 */         if ((BrowserWindow.this.browser.isDisposed()) || (BrowserWindow.this.browser.getShell().isDisposed())) {
/* 182 */           return;
/*     */         }
/* 184 */         BrowserWindow.this.shell.setText(event.title);
/*     */       }
/*     */       
/*     */ 
/* 188 */     });
/* 189 */     this.browser.addStatusTextListener(new StatusTextListener() {
/*     */       public void changed(StatusTextEvent event) {
/* 191 */         if ((BrowserWindow.this.browser.isDisposed()) || (BrowserWindow.this.browser.getShell().isDisposed())) {
/* 192 */           return;
/*     */         }
/* 194 */         if ("__VUZE__MessageBoxShell__CLOSE".equals(event.text))
/*     */         {
/*     */ 
/* 197 */           Utils.execSWTThreadLater(0, new Runnable() {
/*     */             public void run() {
/* 199 */               if ((!BrowserWindow.this.browser.isDisposed()) && (!BrowserWindow.this.shell.isDisposed())) {
/* 200 */                 BrowserWindow.this.shell.close();
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           });
/*     */         }
/*     */       }
/* 208 */     });
/* 209 */     SimpleTimer.addEvent("showWin", SystemTime.getOffsetTime(3000L), new TimerEventPerformer()
/*     */     {
/*     */       public void perform(TimerEvent event) {
/* 212 */         Utils.execSWTThread(new AERunnable()
/*     */         {
/*     */           public void runSupport() {
/* 215 */             if ((BrowserWindow.this.shell != null) && (!BrowserWindow.this.shell.isDisposed())) {
/* 216 */               BrowserWindow.this.shell.open();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */     
/* 223 */     if ((w > 0) && (h > 0)) {
/* 224 */       Rectangle computeTrim = this.shell.computeTrim(0, 0, w, h);
/* 225 */       this.shell.setSize(computeTrim.width, computeTrim.height);
/*     */     }
/*     */     
/*     */ 
/* 229 */     Utils.centerWindowRelativeTo(this.shell, parent);
/* 230 */     this.browser.setUrl(url);
/* 231 */     this.browser.setData("StartURL", url);
/*     */   }
/*     */   
/*     */   public void waitUntilClosed() {
/* 235 */     Display display = this.shell.getDisplay();
/* 236 */     while (!this.shell.isDisposed()) {
/* 237 */       if (!display.readAndDispatch()) {
/* 238 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public ClientMessageContext getContext()
/*     */   {
/* 246 */     return this.context;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 250 */     Display display = new Display();
/* 251 */     Shell shell = new Shell(display, 2144);
/*     */     
/* 253 */     new BrowserWindow(shell, "http://google.com", 500, 200, true, false);
/*     */     
/* 255 */     shell.pack();
/* 256 */     shell.open();
/*     */     
/* 258 */     while (!shell.isDisposed()) {
/* 259 */       if (!display.readAndDispatch()) {
/* 260 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/BrowserWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */