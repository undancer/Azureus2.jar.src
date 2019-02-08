/*     */ package com.aelitis.azureus.ui.swt.browser.listener;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.browser.CookiesListener;
/*     */ import java.net.URLDecoder;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.browser.ProgressEvent;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.browser.StatusTextEvent;
/*     */ import org.eclipse.swt.browser.StatusTextListener;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
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
/*     */ public class ExternalLoginCookieListener
/*     */   implements StatusTextListener, LocationListener, ProgressListener
/*     */ {
/*     */   private static final String AZCOOKIEMSG = "AZCOOKIEMSG;";
/*     */   private CookiesListener listener;
/*     */   private BrowserWrapper browser;
/*     */   private static final String getCookiesCode = "try {var cookies = encodeURIComponent(document.cookie);window.status = 'AZCOOKIEMSG;' + cookies;//alert(window.status);\nwindow.status = '';} catch(e) {}";
/*     */   
/*     */   public ExternalLoginCookieListener(CookiesListener _listener, BrowserWrapper browser)
/*     */   {
/*  56 */     this.listener = _listener;
/*  57 */     this.browser = browser;
/*  58 */     browser.addStatusTextListener(this);
/*     */   }
/*     */   
/*     */   public void changed(StatusTextEvent event)
/*     */   {
/*  63 */     if (event.text.startsWith("AZCOOKIEMSG;")) {
/*  64 */       String uriEncodedCookies = event.text.substring("AZCOOKIEMSG;".length());
/*     */       try {
/*  66 */         String cookies = URLDecoder.decode(uriEncodedCookies, "UTF-8");
/*     */         
/*  68 */         if (this.listener != null) {
/*  69 */           this.listener.cookiesFound(cookies);
/*     */         }
/*     */       } catch (Exception e) {
/*  72 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void getCookies() {
/*  78 */     if (this.browser != null) {
/*  79 */       this.browser.execute("try {var cookies = encodeURIComponent(document.cookie);window.status = 'AZCOOKIEMSG;' + cookies;//alert(window.status);\nwindow.status = '';} catch(e) {}");
/*     */     }
/*     */   }
/*     */   
/*     */   public void stopListening() {
/*  84 */     this.browser.removeStatusTextListener(this);
/*     */   }
/*     */   
/*     */   public void hookOnPageLoaded() {
/*  88 */     this.browser.addProgressListener(this);
/*     */   }
/*     */   
/*     */   public void hookOnPageChanged() {
/*  92 */     this.browser.addLocationListener(this);
/*     */   }
/*     */   
/*     */   public void hook() {
/*  96 */     hookOnPageChanged();
/*  97 */     hookOnPageLoaded();
/*     */   }
/*     */   
/*     */ 
/*     */   public void unHook() {}
/*     */   
/*     */ 
/*     */   public void changed(ProgressEvent arg0) {}
/*     */   
/*     */ 
/*     */   public void completed(ProgressEvent arg0)
/*     */   {
/* 109 */     getCookies();
/*     */   }
/*     */   
/*     */   public void changed(LocationEvent arg0) {
/* 113 */     getCookies();
/*     */   }
/*     */   
/*     */   public void changing(LocationEvent arg0) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/ExternalLoginCookieListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */