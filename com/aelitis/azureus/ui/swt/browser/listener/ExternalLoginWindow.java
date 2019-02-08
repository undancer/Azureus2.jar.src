/*     */ package com.aelitis.azureus.ui.swt.browser.listener;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.http.HTTPAuthHelper;
/*     */ import com.aelitis.azureus.core.util.http.HTTPAuthHelperListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.CookiesListener;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.browser.ProgressEvent;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.progress.ProgressWindow;
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
/*     */ public class ExternalLoginWindow
/*     */ {
/*     */   Display display;
/*     */   Shell shell;
/*     */   BrowserWrapper browser;
/*     */   ExternalLoginListener listener;
/*     */   String originalLoginUrl;
/*  60 */   Map cookies = new HashMap();
/*     */   
/*  62 */   Set sniffer_cookies = new HashSet();
/*  63 */   Set js_cookies = new HashSet();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   HTTPAuthHelper sniffer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExternalLoginWindow(ExternalLoginListener _listener, String name, String _loginUrl, boolean captureMode, String authMode, boolean isMine)
/*     */   {
/*  76 */     this.listener = _listener;
/*  77 */     this.originalLoginUrl = _loginUrl;
/*     */     
/*  79 */     this.shell = ShellFactory.createMainShell(96);
/*  80 */     this.shell.setSize(800, 600);
/*  81 */     Utils.centreWindow(this.shell);
/*     */     
/*  83 */     this.display = this.shell.getDisplay();
/*  84 */     this.shell.setText(MessageText.getString("externalLogin.title"));
/*     */     
/*  86 */     this.shell.setLayout(new FormLayout());
/*     */     
/*  88 */     this.shell.addDisposeListener(new DisposeListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetDisposed(DisposeEvent arg0)
/*     */       {
/*     */ 
/*  95 */         if (ExternalLoginWindow.this.sniffer != null)
/*     */         {
/*  97 */           ExternalLoginWindow.this.sniffer.destroy();
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 102 */     });
/* 103 */     Label explain = new Label(this.shell, 64);
/* 104 */     if (captureMode) {
/* 105 */       explain.setText(MessageText.getString("externalLogin.explanation.capture", new String[] { name }));
/*     */     } else {
/* 107 */       explain.setText(MessageText.getString("externalLogin.explanation", new String[] { name }));
/*     */     }
/*     */     
/* 110 */     this.browser = Utils.createSafeBrowser(this.shell, 2048);
/* 111 */     if (this.browser == null) {
/* 112 */       this.shell.dispose();
/* 113 */       return;
/*     */     }
/* 115 */     ExternalLoginCookieListener cookieListener = new ExternalLoginCookieListener(new CookiesListener()
/*     */     {
/* 117 */       public void cookiesFound(String cookies) { ExternalLoginWindow.this.foundCookies(cookies, true); } }, this.browser);
/*     */     
/*     */ 
/*     */ 
/* 121 */     cookieListener.hook();
/*     */     
/* 123 */     Label separator = new Label(this.shell, 258);
/*     */     
/* 125 */     Button alt_method = null;
/*     */     
/* 127 */     if (isMine)
/*     */     {
/* 129 */       alt_method = new Button(this.shell, 32);
/*     */       
/* 131 */       final Button f_alt_method = alt_method;
/*     */       
/* 133 */       alt_method.setText(MessageText.getString("externalLogin.auth_method_proxy"));
/*     */       
/* 135 */       alt_method.setSelection(authMode == "proxy");
/*     */       
/* 137 */       alt_method.addListener(13, new Listener()
/*     */       {
/*     */         public void handleEvent(Event arg0) {
/* 140 */           ExternalLoginWindow.this.setCaptureMethod(ExternalLoginWindow.this.browser, !f_alt_method.getSelection(), true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 145 */     setCaptureMethod(this.browser, authMode == "transparent", true);
/*     */     
/* 147 */     Button cancel = new Button(this.shell, 8);
/* 148 */     cancel.setText(MessageText.getString("Button.cancel"));
/*     */     
/* 150 */     Button done = new Button(this.shell, 8);
/* 151 */     done.setText(MessageText.getString("Button.done"));
/*     */     
/* 153 */     cancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 155 */         if (ExternalLoginWindow.this.listener != null) {
/* 156 */           ExternalLoginWindow.this.listener.canceled(ExternalLoginWindow.this);
/*     */         }
/* 158 */         ExternalLoginWindow.this.shell.dispose();
/*     */       }
/*     */       
/* 161 */     });
/* 162 */     done.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 164 */         if (ExternalLoginWindow.this.listener != null) {
/* 165 */           ExternalLoginWindow.this.listener.done(ExternalLoginWindow.this, ExternalLoginWindow.this.cookiesToString());
/*     */         }
/* 167 */         ExternalLoginWindow.this.shell.dispose();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 172 */     });
/* 173 */     FormData data = new FormData();
/* 174 */     data.left = new FormAttachment(0, 5);
/* 175 */     data.right = new FormAttachment(100, -5);
/* 176 */     data.top = new FormAttachment(0, 5);
/* 177 */     explain.setLayoutData(data);
/*     */     
/* 179 */     data = new FormData();
/* 180 */     data.left = new FormAttachment(0, 5);
/* 181 */     data.right = new FormAttachment(100, -5);
/* 182 */     data.top = new FormAttachment(explain, 5);
/* 183 */     data.bottom = new FormAttachment(separator, -5);
/* 184 */     this.browser.setLayoutData(data);
/*     */     
/* 186 */     data = new FormData();
/* 187 */     data.left = new FormAttachment(0, 0);
/* 188 */     data.right = new FormAttachment(100, 0);
/* 189 */     data.bottom = new FormAttachment(cancel, -5);
/* 190 */     separator.setLayoutData(data);
/*     */     
/* 192 */     if (isMine)
/*     */     {
/* 194 */       data = new FormData();
/* 195 */       data.width = 100;
/* 196 */       data.left = new FormAttachment(0, 5);
/* 197 */       data.right = new FormAttachment(cancel, -5);
/* 198 */       data.bottom = new FormAttachment(100, -5);
/* 199 */       alt_method.setLayoutData(data);
/*     */     }
/*     */     
/* 202 */     data = new FormData();
/* 203 */     data.width = 100;
/* 204 */     data.right = new FormAttachment(done, -5);
/* 205 */     data.bottom = new FormAttachment(100, -5);
/* 206 */     cancel.setLayoutData(data);
/*     */     
/* 208 */     data = new FormData();
/* 209 */     data.width = 100;
/* 210 */     data.right = new FormAttachment(100, -5);
/* 211 */     data.bottom = new FormAttachment(100, -5);
/* 212 */     done.setLayoutData(data);
/*     */     
/* 214 */     this.shell.layout();
/* 215 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setCaptureMethod(final BrowserWrapper browser, boolean transparent, boolean show_progress)
/*     */   {
/* 224 */     if (this.sniffer != null)
/*     */     {
/* 226 */       this.sniffer.destroy();
/*     */       
/* 228 */       this.sniffer = null;
/*     */     }
/*     */     
/* 231 */     if (show_progress)
/*     */     {
/* 233 */       final ProgressWindow prog_wind = new ProgressWindow(this.shell, "externalLogin.wait", 2144, 500);
/*     */       
/*     */ 
/* 236 */       browser.addProgressListener(new ProgressListener()
/*     */       {
/*     */         public void changed(ProgressEvent arg0) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void completed(ProgressEvent arg0)
/*     */         {
/* 249 */           if ((browser.isDisposed()) || (browser.getShell().isDisposed())) {
/* 250 */             return;
/*     */           }
/* 252 */           browser.removeProgressListener(this);
/*     */           
/* 254 */           prog_wind.destroy();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 259 */     if (transparent)
/*     */     {
/* 261 */       browser.setUrl(this.originalLoginUrl);
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 266 */         final HTTPAuthHelper this_sniffer = this.sniffer = new HTTPAuthHelper(new URL(this.originalLoginUrl));
/*     */         
/*     */ 
/* 269 */         this_sniffer.addListener(new HTTPAuthHelperListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void cookieFound(HTTPAuthHelper helper, String cookie_name, String cookie_value)
/*     */           {
/*     */ 
/*     */ 
/* 278 */             if (helper == this_sniffer)
/*     */             {
/* 280 */               ExternalLoginWindow.this.foundCookies(cookie_name + "=" + cookie_value, false);
/*     */             }
/*     */             
/*     */           }
/* 284 */         });
/* 285 */         this_sniffer.start();
/*     */         
/* 287 */         String str = this.originalLoginUrl.toString();
/*     */         
/* 289 */         int pos = str.indexOf("://");
/*     */         
/* 291 */         str = str.substring(pos + 3);
/*     */         
/* 293 */         pos = str.indexOf("/");
/*     */         
/* 295 */         if (pos != -1)
/*     */         {
/* 297 */           str = str.substring(pos);
/*     */         }
/*     */         
/* 300 */         if (!str.startsWith("/"))
/*     */         {
/* 302 */           str = "/" + str;
/*     */         }
/*     */         
/* 305 */         browser.setUrl("http://localhost:" + this.sniffer.getPort() + str);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 309 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void foundCookies(String _cookies, boolean _from_js)
/*     */   {
/* 319 */     String[] x = _cookies.split(";");
/*     */     
/* 321 */     synchronized (this.cookies)
/*     */     {
/* 323 */       for (int i = 0; i < x.length; i++)
/*     */       {
/* 325 */         String cookie = x[i];
/*     */         
/* 327 */         String[] bits = cookie.split("=");
/*     */         
/* 329 */         if (bits.length == 2)
/*     */         {
/* 331 */           String name = bits[0];
/* 332 */           String value = bits[1];
/*     */           
/* 334 */           if (_from_js)
/*     */           {
/* 336 */             this.js_cookies.add(name);
/*     */           }
/*     */           else
/*     */           {
/* 340 */             this.sniffer_cookies.add(name);
/*     */           }
/*     */           
/* 343 */           this.cookies.put(name, value);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 348 */     if (this.listener != null)
/*     */     {
/* 350 */       this.listener.cookiesFound(this, cookiesToString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String cookiesToString()
/*     */   {
/* 357 */     synchronized (this.cookies)
/*     */     {
/* 359 */       String res = "";
/*     */       
/* 361 */       Iterator it = this.cookies.entrySet().iterator();
/*     */       
/* 363 */       while (it.hasNext())
/*     */       {
/* 365 */         Map.Entry entry = (Map.Entry)it.next();
/*     */         
/* 367 */         res = res + (res.length() == 0 ? "" : ";") + entry.getKey() + "=" + entry.getValue();
/*     */       }
/*     */       
/* 370 */       return res;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean proxyCaptureModeRequired()
/*     */   {
/* 379 */     if (this.sniffer_cookies.size() > this.js_cookies.size())
/*     */     {
/* 381 */       return true;
/*     */     }
/*     */     
/* 384 */     return (this.sniffer != null) && (this.sniffer.wasHTTPOnlyCookieDetected());
/*     */   }
/*     */   
/*     */   public void close() {
/* 388 */     Utils.execSWTThread(new Runnable() {
/*     */       public void run() {
/* 390 */         ExternalLoginWindow.this.shell.close();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 396 */     Display display = new Display();
/* 397 */     ExternalLoginWindow slw = new ExternalLoginWindow(new ExternalLoginListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void cookiesFound(ExternalLoginWindow window, String cookies)
/*     */       {
/*     */ 
/*     */ 
/* 405 */         System.out.println("Cookies found: " + cookies);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void canceled(ExternalLoginWindow window)
/*     */       {
/* 412 */         System.out.println("Cancelled");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 419 */       public void done(ExternalLoginWindow window, String cookies) { System.out.println("Done"); } }, "test", "http://www.sf.net/", false, "proxy", true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 428 */     while (!slw.shell.isDisposed()) {
/* 429 */       if (!display.readAndDispatch()) {
/* 430 */         display.sleep();
/*     */       }
/*     */     }
/*     */     
/* 434 */     System.out.println("Found httponly cookies=" + slw.proxyCaptureModeRequired());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/ExternalLoginWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */