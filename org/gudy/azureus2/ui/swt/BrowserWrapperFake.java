/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.SWTError;
/*     */ import org.eclipse.swt.browser.CloseWindowListener;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.browser.OpenWindowListener;
/*     */ import org.eclipse.swt.browser.ProgressEvent;
/*     */ import org.eclipse.swt.browser.ProgressListener;
/*     */ import org.eclipse.swt.browser.StatusTextListener;
/*     */ import org.eclipse.swt.browser.TitleEvent;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.browser.WindowEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy.copyToClipProvider;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class BrowserWrapperFake
/*     */   extends BrowserWrapper
/*     */ {
/*     */   private Composite parent;
/*     */   private Composite browser;
/*     */   private Label link_label;
/*     */   private Label description_label;
/*     */   private String url;
/*     */   private String description;
/*  72 */   private List<LocationListener> location_listeners = new ArrayList();
/*  73 */   private List<ProgressListener> progress_listeners = new ArrayList();
/*  74 */   private List<TitleListener> title_listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BrowserWrapperFake(Composite _parent, int style, SWTError _failure)
/*     */   {
/*  82 */     this.parent = _parent;
/*     */     
/*  84 */     this.browser = new Composite(this.parent, 0);
/*  85 */     this.browser.setBackground(Colors.white);
/*     */     
/*  87 */     GridLayout layout = new GridLayout();
/*  88 */     layout.numColumns = 3;
/*  89 */     this.browser.setLayout(layout);
/*     */     
/*  91 */     if (_failure == null)
/*     */     {
/*  93 */       Label label = new Label(this.browser, 64);
/*  94 */       Messages.setLanguageText(label, "browser.internal.disabled.info");
/*  95 */       GridData grid_data = new GridData(768);
/*  96 */       grid_data.horizontalSpan = 3;
/*  97 */       Utils.setLayoutData(label, grid_data);
/*  98 */       label.setBackground(Colors.white);
/*     */       
/* 100 */       label = new Label(this.browser, 0);
/* 101 */       Messages.setLanguageText(label, "browser.internal.disabled.reenable");
/*     */       
/* 103 */       final Button button = new Button(this.browser, 0);
/* 104 */       Messages.setLanguageText(button, "label.enable");
/*     */       
/* 106 */       button.addSelectionListener(new SelectionAdapter()
/*     */       {
/*     */         public void widgetSelected(SelectionEvent e)
/*     */         {
/* 110 */           button.setEnabled(false);
/* 111 */           COConfigurationManager.setParameter("browser.internal.disable", false);
/*     */         }
/*     */         
/* 114 */       });
/* 115 */       label = new Label(this.browser, 0);
/* 116 */       grid_data = new GridData(768);
/* 117 */       Utils.setLayoutData(label, grid_data);
/*     */     }
/*     */     else
/*     */     {
/* 121 */       Label label = new Label(this.browser, 64);
/* 122 */       Messages.setLanguageText(label, "browser.internal.failed.info", new String[] { Debug.getNestedExceptionMessage(_failure) });
/* 123 */       GridData grid_data = new GridData(768);
/* 124 */       grid_data.horizontalSpan = 3;
/* 125 */       Utils.setLayoutData(label, grid_data);
/* 126 */       label.setBackground(Colors.white);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 131 */     Composite details = new Composite(this.browser, 2048);
/* 132 */     layout = new GridLayout();
/* 133 */     layout.numColumns = 2;
/* 134 */     details.setLayout(layout);
/* 135 */     GridData grid_data = new GridData(1808);
/* 136 */     grid_data.horizontalSpan = 3;
/* 137 */     Utils.setLayoutData(details, grid_data);
/* 138 */     details.setBackground(Colors.white);
/*     */     
/*     */ 
/*     */ 
/* 142 */     Label label = new Label(details, 0);
/* 143 */     label.setText("URL");
/* 144 */     Utils.setLayoutData(label, new GridData());
/* 145 */     label.setBackground(Colors.white);
/*     */     
/*     */ 
/* 148 */     this.link_label = new Label(details, 0);
/* 149 */     this.link_label.setText(MessageText.getString("ConfigView.label.please.visit.here"));
/*     */     
/* 151 */     this.link_label.setCursor(this.link_label.getDisplay().getSystemCursor(21));
/* 152 */     this.link_label.setForeground(Colors.blue);
/* 153 */     this.link_label.setBackground(Colors.white);
/*     */     
/* 155 */     this.link_label.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent e) {
/* 157 */         Utils.launch(BrowserWrapperFake.this.url);
/*     */       }
/*     */       
/*     */       public void mouseUp(MouseEvent e) {
/* 161 */         if ((e.button == 1) && (e.stateMask != 262144))
/*     */         {
/* 163 */           Utils.launch(BrowserWrapperFake.this.url);
/*     */         }
/*     */         
/*     */       }
/* 167 */     });
/* 168 */     grid_data = new GridData(768);
/* 169 */     grid_data.horizontalIndent = 10;
/* 170 */     Utils.setLayoutData(this.link_label, grid_data);
/*     */     
/* 172 */     ClipboardCopy.addCopyToClipMenu(this.link_label, new ClipboardCopy.copyToClipProvider()
/*     */     {
/*     */ 
/*     */       public String getText()
/*     */       {
/* 177 */         return BrowserWrapperFake.this.url;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 182 */     });
/* 183 */     label = new Label(details, 0);
/* 184 */     Messages.setLanguageText(label, "columnChooser.columndescription");
/* 185 */     Utils.setLayoutData(label, new GridData());
/* 186 */     label.setBackground(Colors.white);
/*     */     
/* 188 */     this.description_label = new Label(details, 0);
/* 189 */     this.description_label.setText("");
/* 190 */     grid_data = new GridData(768);
/* 191 */     grid_data.horizontalIndent = 10;
/* 192 */     Utils.setLayoutData(this.description_label, grid_data);
/* 193 */     this.description_label.setBackground(Colors.white);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFake()
/*     */   {
/* 199 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite getControl()
/*     */   {
/* 205 */     return this.browser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBrowser(WindowEvent event) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setVisible(boolean visible)
/*     */   {
/* 218 */     this.browser.setVisible(visible);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isVisible()
/*     */   {
/* 224 */     return this.browser.isVisible();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/* 230 */     return this.browser.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 236 */     this.browser.dispose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean execute(String str)
/*     */   {
/* 243 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isBackEnabled()
/*     */   {
/* 249 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 255 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUrl(final String _url)
/*     */   {
/* 262 */     this.url = _url;
/*     */     
/* 264 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 270 */         String url_str = _url;
/*     */         
/* 272 */         int pos = url_str.indexOf('?');
/*     */         
/* 274 */         if (pos != -1)
/*     */         {
/* 276 */           url_str = url_str.substring(0, pos);
/*     */         }
/*     */         
/* 279 */         BrowserWrapperFake.this.link_label.setText(url_str);
/*     */         
/* 281 */         BrowserWrapperFake.this.browser.layout();
/*     */         
/* 283 */         for (LocationListener l : BrowserWrapperFake.this.location_listeners) {
/*     */           try
/*     */           {
/* 286 */             LocationEvent event = new LocationEvent(BrowserWrapperFake.this.browser);
/*     */             
/* 288 */             event.top = true;
/* 289 */             event.location = _url;
/*     */             
/* 291 */             l.changed(event);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 295 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */         
/* 299 */         for (ProgressListener l : BrowserWrapperFake.this.progress_listeners) {
/*     */           try
/*     */           {
/* 302 */             ProgressEvent event = new ProgressEvent(BrowserWrapperFake.this.browser);
/*     */             
/* 304 */             l.completed(event);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 308 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */         
/* 312 */         for (TitleListener l : BrowserWrapperFake.this.title_listeners) {
/*     */           try
/*     */           {
/* 315 */             TitleEvent event = new TitleEvent(BrowserWrapperFake.this.browser);
/*     */             
/* 317 */             event.title = "Browser Disabled";
/*     */             
/* 319 */             l.changed(event);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 323 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 334 */     this.description = text;
/*     */     
/* 336 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 342 */         BrowserWrapperFake.this.description_label.setText(BrowserWrapperFake.this.description);
/*     */         
/* 344 */         BrowserWrapperFake.this.browser.layout();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setData(String key, Object value)
/*     */   {
/* 354 */     this.browser.setData(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getData(String key)
/*     */   {
/* 361 */     return this.browser.getData(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void back() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void update()
/*     */   {
/* 377 */     this.browser.update();
/*     */   }
/*     */   
/*     */ 
/*     */   public Shell getShell()
/*     */   {
/* 383 */     return this.browser.getShell();
/*     */   }
/*     */   
/*     */ 
/*     */   public Display getDisplay()
/*     */   {
/* 389 */     return this.browser.getDisplay();
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite getParent()
/*     */   {
/* 395 */     return this.browser.getParent();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getLayoutData()
/*     */   {
/* 401 */     return this.browser.getLayoutData();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(Object data)
/*     */   {
/* 408 */     this.browser.setLayoutData(data);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setFocus()
/*     */   {
/* 414 */     this.browser.setFocus();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(int type, Listener l)
/*     */   {
/* 422 */     this.browser.addListener(type, l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addLocationListener(LocationListener l)
/*     */   {
/* 429 */     this.location_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeLocationListener(LocationListener l)
/*     */   {
/* 436 */     this.location_listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addTitleListener(TitleListener l)
/*     */   {
/* 443 */     this.title_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addProgressListener(ProgressListener l)
/*     */   {
/* 450 */     this.progress_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeProgressListener(ProgressListener l)
/*     */   {
/* 457 */     this.progress_listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addOpenWindowListener(OpenWindowListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addCloseWindowListener(CloseWindowListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDisposeListener(DisposeListener l)
/*     */   {
/* 476 */     this.browser.addDisposeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDisposeListener(DisposeListener l)
/*     */   {
/* 483 */     this.browser.removeDisposeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addStatusTextListener(StatusTextListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeStatusTextListener(StatusTextListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BrowserWrapper.BrowserFunction addBrowserFunction(String name, BrowserWrapper.BrowserFunction bf)
/*     */   {
/* 503 */     return new BrowserFunctionFake(bf, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class BrowserFunctionFake
/*     */     extends BrowserWrapper.BrowserFunction
/*     */   {
/*     */     private final BrowserWrapper.BrowserFunction bf;
/*     */     
/*     */     private boolean disposed;
/*     */     
/*     */ 
/*     */     private BrowserFunctionFake(BrowserWrapper.BrowserFunction _bf)
/*     */     {
/* 518 */       this.bf = _bf;
/*     */       
/* 520 */       this.bf.bind(this);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Object function(Object[] arguments)
/*     */     {
/* 527 */       return this.bf.function(arguments);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDisposed()
/*     */     {
/* 533 */       return this.disposed;
/*     */     }
/*     */     
/*     */ 
/*     */     public void dispose()
/*     */     {
/* 539 */       this.disposed = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/BrowserWrapperFake.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */