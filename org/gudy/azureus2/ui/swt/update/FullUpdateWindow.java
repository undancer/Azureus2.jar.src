/*     */ package org.gudy.azureus2.ui.swt.update;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.browser.OpenWindowListener;
/*     */ import org.eclipse.swt.browser.StatusTextEvent;
/*     */ import org.eclipse.swt.browser.StatusTextListener;
/*     */ import org.eclipse.swt.browser.TitleEvent;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.browser.WindowEvent;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper.BrowserFunction;
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
/*     */ public class FullUpdateWindow
/*     */ {
/*  43 */   private static Shell current_shell = null;
/*     */   
/*     */ 
/*     */   private static BrowserWrapper browser;
/*     */   
/*     */   private static BrowserWrapper.BrowserFunction browserFunction;
/*     */   
/*     */ 
/*     */   public static void handleUpdate(String url, final UIFunctions.actionListener listener)
/*     */   {
/*     */     try
/*     */     {
/*  55 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/*  57 */           FullUpdateWindow.open(this.val$url, listener);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  63 */       Debug.out(e);
/*     */       
/*  65 */       listener.actionComplete(Boolean.valueOf(false));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void open(String url, final UIFunctions.actionListener listener)
/*     */   {
/*  74 */     boolean ok = false;
/*     */     
/*  76 */     final boolean[] listener_informed = { false };
/*     */     try
/*     */     {
/*  79 */       if ((current_shell != null) && (!current_shell.isDisposed())) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*  84 */       final Shell parentShell = Utils.findAnyShell();
/*     */       
/*  86 */       Shell shell = current_shell = ShellFactory.createShell(parentShell, 67680);
/*     */       
/*     */ 
/*  89 */       shell.setLayout(new FillLayout());
/*     */       
/*  91 */       if (parentShell != null) {
/*  92 */         parentShell.setCursor(shell.getDisplay().getSystemCursor(1));
/*     */       }
/*     */       
/*  95 */       shell.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/*     */           try {
/*  98 */             if (this.val$parentShell != null) {
/*  99 */               this.val$parentShell.setCursor(e.display.getSystemCursor(0));
/*     */             }
/* 101 */             if ((FullUpdateWindow.browserFunction != null) && (!FullUpdateWindow.browserFunction.isDisposed())) {
/* 102 */               FullUpdateWindow.browserFunction.dispose();
/*     */             }
/* 104 */             FullUpdateWindow.access$102(null); return;
/*     */           }
/*     */           finally
/*     */           {
/* 108 */             if (listener_informed[0] == 0) {
/*     */               try
/*     */               {
/* 111 */                 listener.actionComplete(Boolean.valueOf(false));
/*     */               }
/*     */               catch (Throwable f)
/*     */               {
/* 115 */                 Debug.out(f);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/*     */         }
/* 121 */       });
/* 122 */       browser = Utils.createSafeBrowser(shell, 0);
/* 123 */       if (browser == null) {
/* 124 */         shell.dispose();
/*     */       }
/*     */       else
/*     */       {
/* 128 */         browser.addTitleListener(new TitleListener() {
/*     */           public void changed(TitleEvent event) {
/* 130 */             if ((this.val$shell == null) || (this.val$shell.isDisposed())) {
/* 131 */               return;
/*     */             }
/* 133 */             this.val$shell.setText(event.title);
/*     */           }
/*     */           
/* 136 */         });
/* 137 */         browser.addOpenWindowListener(new OpenWindowListener() {
/*     */           public void open(WindowEvent event) {
/* 139 */             final BrowserWrapper subBrowser = Utils.createSafeBrowser(this.val$shell, Utils.getInitialBrowserStyle(0));
/*     */             
/* 141 */             subBrowser.addLocationListener(new LocationListener() {
/*     */               public void changed(LocationEvent arg0) {}
/*     */               
/*     */               public void changing(LocationEvent event) {
/* 145 */                 if ((event.location == null) || (!event.location.startsWith("http"))) {
/* 146 */                   return;
/*     */                 }
/* 148 */                 event.doit = false;
/* 149 */                 Utils.launch(event.location);
/*     */                 
/* 151 */                 Utils.execSWTThreadLater(1000, new AERunnable() {
/*     */                   public void runSupport() {
/* 153 */                     FullUpdateWindow.4.1.this.val$subBrowser.dispose();
/*     */                   }
/*     */                 });
/*     */               }
/* 157 */             });
/* 158 */             subBrowser.setBrowser(event);
/*     */           }
/*     */           
/* 161 */         });
/* 162 */         browserFunction = browser.addBrowserFunction("sendVuzeUpdateEvent", new BrowserWrapper.BrowserFunction()
/*     */         {
/*     */ 
/*     */ 
/* 166 */           private String last = null;
/*     */           
/*     */           public Object function(Object[] arguments)
/*     */           {
/* 170 */             if ((this.val$shell == null) || (this.val$shell.isDisposed())) {
/* 171 */               return null;
/*     */             }
/*     */             
/* 174 */             if (arguments == null) {
/* 175 */               Debug.out("Invalid sendVuzeUpdateEvent null ");
/* 176 */               return null;
/*     */             }
/* 178 */             if (arguments.length < 1) {
/* 179 */               Debug.out("Invalid sendVuzeUpdateEvent length " + arguments.length + " not 1");
/* 180 */               return null;
/*     */             }
/* 182 */             if (!(arguments[0] instanceof String)) {
/* 183 */               Debug.out("Invalid sendVuzeUpdateEvent " + (arguments[0] == null ? "NULL" : arguments.getClass().getSimpleName()) + " not String");
/*     */               
/*     */ 
/* 186 */               return null;
/*     */             }
/*     */             
/* 189 */             String text = ((String)arguments[0]).toLowerCase();
/* 190 */             if ((this.last != null) && (this.last.equals(text))) {
/* 191 */               return null;
/*     */             }
/* 193 */             this.last = text;
/* 194 */             if (text.contains("page-loaded"))
/*     */             {
/* 196 */               Utils.centreWindow(this.val$shell);
/* 197 */               if (parentShell != null) {
/* 198 */                 parentShell.setCursor(this.val$shell.getDisplay().getSystemCursor(0));
/*     */               }
/* 200 */               this.val$shell.open();
/*     */             }
/* 202 */             else if (text.startsWith("set-size"))
/*     */             {
/* 204 */               String[] strings = text.split(" ");
/*     */               
/* 206 */               if (strings.length > 2) {
/*     */                 try
/*     */                 {
/* 209 */                   int w = Integer.parseInt(strings[1]);
/* 210 */                   int h = Integer.parseInt(strings[2]);
/*     */                   
/* 212 */                   Rectangle computeTrim = this.val$shell.computeTrim(0, 0, w, h);
/* 213 */                   this.val$shell.setSize(computeTrim.width, computeTrim.height);
/*     */                 }
/*     */                 catch (Exception e) {}
/*     */               }
/*     */             }
/* 218 */             else if ((text.contains("decline")) || (text.contains("close")))
/*     */             {
/* 220 */               Utils.execSWTThreadLater(0, new AERunnable() {
/*     */                 public void runSupport() {
/* 222 */                   FullUpdateWindow.5.this.val$shell.dispose();
/*     */                 }
/*     */               });
/*     */             }
/* 226 */             else if (text.contains("accept"))
/*     */             {
/* 228 */               Utils.execSWTThreadLater(0, new AERunnable()
/*     */               {
/*     */                 public void runSupport() {
/* 231 */                   FullUpdateWindow.5.this.val$listener_informed[0] = true;
/*     */                   try
/*     */                   {
/* 234 */                     FullUpdateWindow.5.this.val$listener.actionComplete(Boolean.valueOf(true));
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 238 */                     Debug.out(e);
/*     */                   }
/*     */                   
/* 241 */                   FullUpdateWindow.5.this.val$shell.dispose();
/*     */                 }
/*     */               });
/*     */             }
/* 245 */             return null;
/*     */           }
/*     */           
/* 248 */         });
/* 249 */         browser.addStatusTextListener(new StatusTextListener() {
/*     */           public void changed(StatusTextEvent event) {
/* 251 */             FullUpdateWindow.browserFunction.function(new Object[] { event.text });
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 256 */         });
/* 257 */         browser.addLocationListener(new LocationListener()
/*     */         {
/*     */           public void changing(LocationEvent event) {}
/*     */           
/*     */ 
/*     */ 
/*     */           public void changed(LocationEvent event) {}
/* 264 */         });
/* 265 */         String final_url = url + (url.indexOf('?') == -1 ? "?" : "&") + "locale=" + MessageText.getCurrentLocale().toString() + "&azv=" + "5.7.6.0";
/*     */         
/*     */ 
/*     */ 
/* 269 */         SimpleTimer.addEvent("fullupdate.pageload", SystemTime.getOffsetTime(5000L), new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/* 274 */             Utils.execSWTThread(new AERunnable() {
/*     */               public void runSupport() {
/* 276 */                 if (!FullUpdateWindow.8.this.val$shell.isDisposed())
/*     */                 {
/* 278 */                   FullUpdateWindow.8.this.val$shell.open();
/*     */                 }
/*     */                 
/*     */               }
/*     */             });
/*     */           }
/* 284 */         });
/* 285 */         browser.setUrl(final_url);
/*     */         
/* 287 */         if (browser.isFake())
/*     */         {
/* 289 */           shell.setSize(400, 500);
/*     */           
/* 291 */           Utils.centreWindow(shell);
/*     */           
/* 293 */           browser.setUrl("http://www.vuze.com/download");
/*     */           
/* 295 */           browser.setText("Update available, please go to www.vuze.com to update.");
/*     */           
/* 297 */           shell.open();
/*     */         }
/*     */         
/* 300 */         ok = true;
/*     */       }
/*     */       return;
/*     */     } finally {
/* 304 */       if (!ok) {
/*     */         try
/*     */         {
/* 307 */           listener.actionComplete(Boolean.valueOf(false));
/*     */         }
/*     */         catch (Throwable f)
/*     */         {
/* 311 */           Debug.out(f);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 321 */       open("http://www.vuze.com/client/update.php?newversion=9.9.9.9", new UIFunctions.actionListener()
/*     */       {
/*     */ 
/*     */         public void actionComplete(Object result)
/*     */         {
/* 326 */           System.out.println("result=" + result);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 332 */       e.printStackTrace();
/*     */     }
/* 334 */     Display d = Display.getDefault();
/*     */     for (;;) {
/* 336 */       if (!d.readAndDispatch()) {
/* 337 */         d.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/update/FullUpdateWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */