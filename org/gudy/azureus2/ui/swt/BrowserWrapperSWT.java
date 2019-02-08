/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import org.eclipse.swt.browser.Browser;
/*     */ import org.eclipse.swt.browser.BrowserFunction;
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
/*     */ public class BrowserWrapperSWT
/*     */   extends BrowserWrapper
/*     */ {
/*     */   private Browser browser;
/*     */   
/*     */   protected BrowserWrapperSWT(Composite composite, int style)
/*     */   {
/*  50 */     this.browser = new Browser(composite, style);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFake()
/*     */   {
/*  56 */     return false;
/*     */   }
/*     */   
/*     */   public Composite getControl()
/*     */   {
/*  61 */     return this.browser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBrowser(WindowEvent event)
/*     */   {
/*  68 */     event.browser = this.browser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setVisible(boolean visible)
/*     */   {
/*  75 */     this.browser.setVisible(visible);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isVisible()
/*     */   {
/*  81 */     return this.browser.isVisible();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/*  87 */     return this.browser.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */   public void dispose()
/*     */   {
/*  93 */     this.browser.dispose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean execute(String str)
/*     */   {
/* 102 */     return this.browser.execute(str);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isBackEnabled()
/*     */   {
/* 108 */     return this.browser.isBackEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 114 */     return this.browser.getUrl();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUrl(String url)
/*     */   {
/* 121 */     this.browser.setUrl(url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 128 */     this.browser.setText(text);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setData(String key, Object value)
/*     */   {
/* 136 */     this.browser.setData(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getData(String key)
/*     */   {
/* 143 */     return this.browser.getData(key);
/*     */   }
/*     */   
/*     */ 
/*     */   public void back()
/*     */   {
/* 149 */     this.browser.back();
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 155 */     this.browser.refresh();
/*     */   }
/*     */   
/*     */ 
/*     */   public void update()
/*     */   {
/* 161 */     this.browser.update();
/*     */   }
/*     */   
/*     */ 
/*     */   public Shell getShell()
/*     */   {
/* 167 */     return this.browser.getShell();
/*     */   }
/*     */   
/*     */ 
/*     */   public Display getDisplay()
/*     */   {
/* 173 */     return this.browser.getDisplay();
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite getParent()
/*     */   {
/* 179 */     return this.browser.getParent();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getLayoutData()
/*     */   {
/* 185 */     return this.browser.getLayoutData();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(Object data)
/*     */   {
/* 192 */     this.browser.setLayoutData(data);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setFocus()
/*     */   {
/* 198 */     this.browser.setFocus();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(int type, Listener l)
/*     */   {
/* 206 */     this.browser.addListener(type, l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addLocationListener(LocationListener l)
/*     */   {
/* 213 */     this.browser.addLocationListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeLocationListener(LocationListener l)
/*     */   {
/* 220 */     this.browser.removeLocationListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addTitleListener(TitleListener l)
/*     */   {
/* 227 */     this.browser.addTitleListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addProgressListener(ProgressListener l)
/*     */   {
/* 234 */     this.browser.addProgressListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeProgressListener(ProgressListener l)
/*     */   {
/* 241 */     this.browser.removeProgressListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addOpenWindowListener(OpenWindowListener l)
/*     */   {
/* 248 */     this.browser.addOpenWindowListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addCloseWindowListener(CloseWindowListener l)
/*     */   {
/* 255 */     this.browser.addCloseWindowListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addDisposeListener(DisposeListener l)
/*     */   {
/* 262 */     this.browser.addDisposeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDisposeListener(DisposeListener l)
/*     */   {
/* 269 */     this.browser.removeDisposeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addStatusTextListener(StatusTextListener l)
/*     */   {
/* 276 */     this.browser.addStatusTextListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeStatusTextListener(StatusTextListener l)
/*     */   {
/* 283 */     this.browser.removeStatusTextListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BrowserWrapper.BrowserFunction addBrowserFunction(String name, final BrowserWrapper.BrowserFunction bf)
/*     */   {
/* 291 */     BrowserFunction swt_bf = new BrowserFunction(this.browser, name)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public Object function(Object[] arguments)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 301 */         return bf.function(arguments);
/*     */       }
/*     */       
/* 304 */     };
/* 305 */     return new BrowserFunctionSWT(bf, swt_bf, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class BrowserFunctionSWT
/*     */     extends BrowserWrapper.BrowserFunction
/*     */   {
/*     */     private final BrowserWrapper.BrowserFunction bf;
/*     */     
/*     */     private final BrowserFunction swt_bf;
/*     */     
/*     */ 
/*     */     private BrowserFunctionSWT(BrowserWrapper.BrowserFunction _bf, BrowserFunction _swt_bf)
/*     */     {
/* 320 */       this.bf = _bf;
/* 321 */       this.swt_bf = _swt_bf;
/*     */       
/* 323 */       this.bf.bind(this);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Object function(Object[] arguments)
/*     */     {
/* 330 */       return this.bf.function(arguments);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDisposed()
/*     */     {
/* 336 */       return this.swt_bf.isDisposed();
/*     */     }
/*     */     
/*     */ 
/*     */     public void dispose()
/*     */     {
/* 342 */       this.swt_bf.dispose();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/BrowserWrapperSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */