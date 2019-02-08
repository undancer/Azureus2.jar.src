/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import org.eclipse.swt.SWTError;
/*     */ import org.eclipse.swt.browser.CloseWindowListener;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.browser.OpenWindowListener;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.browser.StatusTextListener;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.browser.WindowEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public abstract class BrowserWrapper
/*     */ {
/*     */   public static BrowserWrapper createBrowser(Composite composite, int style)
/*     */   {
/*  52 */     AEDiagnostics.waitForDumpChecks(10000L);
/*     */     
/*  54 */     boolean use_fake = COConfigurationManager.getBooleanParameter("browser.internal.disable");
/*     */     
/*  56 */     if (use_fake)
/*     */     {
/*  58 */       return new BrowserWrapperFake(composite, style, null);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*  63 */       return new BrowserWrapperSWT(composite, style);
/*     */     }
/*     */     catch (SWTError error)
/*     */     {
/*  67 */       return new BrowserWrapperFake(composite, style, error);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean isFake();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract Composite getControl();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void setBrowser(WindowEvent paramWindowEvent);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void setVisible(boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean isVisible();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean isDisposed();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void dispose();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean execute(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean isBackEnabled();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract String getUrl();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void setUrl(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void setText(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void setData(String paramString, Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract Object getData(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void back();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void refresh();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void update();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract Shell getShell();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract Display getDisplay();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract Composite getParent();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract Object getLayoutData();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void setLayoutData(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void setFocus();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void addListener(int paramInt, Listener paramListener);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void addLocationListener(LocationListener paramLocationListener);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void removeLocationListener(LocationListener paramLocationListener);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void addTitleListener(TitleListener paramTitleListener);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void addProgressListener(ProgressListener paramProgressListener);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void removeProgressListener(ProgressListener paramProgressListener);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void addOpenWindowListener(OpenWindowListener paramOpenWindowListener);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void addCloseWindowListener(CloseWindowListener paramCloseWindowListener);
/*     */   
/*     */ 
/*     */   public abstract void addDisposeListener(DisposeListener paramDisposeListener);
/*     */   
/*     */ 
/*     */   public abstract void removeDisposeListener(DisposeListener paramDisposeListener);
/*     */   
/*     */ 
/*     */   public abstract void addStatusTextListener(StatusTextListener paramStatusTextListener);
/*     */   
/*     */ 
/*     */   public abstract void removeStatusTextListener(StatusTextListener paramStatusTextListener);
/*     */   
/*     */ 
/*     */   public abstract BrowserFunction addBrowserFunction(String paramString, BrowserFunction paramBrowserFunction);
/*     */   
/*     */ 
/*     */   public static abstract class BrowserFunction
/*     */   {
/*     */     private BrowserFunction delegate;
/*     */     
/*     */ 
/*     */     protected void bind(BrowserFunction _delegate)
/*     */     {
/* 218 */       this.delegate = _delegate;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract Object function(Object[] paramArrayOfObject);
/*     */     
/*     */ 
/*     */     public boolean isDisposed()
/*     */     {
/* 228 */       if (this.delegate != null)
/*     */       {
/* 230 */         return this.delegate.isDisposed();
/*     */       }
/*     */       
/* 233 */       Debug.out("wrong");
/*     */       
/* 235 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public void dispose()
/*     */     {
/* 241 */       if (this.delegate != null)
/*     */       {
/* 243 */         this.delegate.dispose();
/*     */       }
/*     */       
/* 246 */       Debug.out("wrong");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/BrowserWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */