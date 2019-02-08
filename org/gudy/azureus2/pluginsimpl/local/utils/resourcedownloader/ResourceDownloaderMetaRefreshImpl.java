/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.html.HTMLException;
/*     */ import org.gudy.azureus2.core3.html.HTMLPage;
/*     */ import org.gudy.azureus2.core3.html.HTMLPageFactory;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderCancelledException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
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
/*     */ public class ResourceDownloaderMetaRefreshImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */   implements ResourceDownloaderListener
/*     */ {
/*     */   public static final int MAX_FOLLOWS = 1;
/*     */   protected ResourceDownloaderBaseImpl delegate;
/*     */   protected ResourceDownloaderBaseImpl current_delegate;
/*  44 */   protected long size = -2L;
/*     */   
/*     */   protected boolean cancelled;
/*     */   protected ResourceDownloader current_downloader;
/*     */   protected Object result;
/*     */   protected int done_count;
/*  50 */   protected AESemaphore done_sem = new AESemaphore("RDMetaRefresh");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderMetaRefreshImpl(ResourceDownloaderBaseImpl _parent, ResourceDownloader _delegate)
/*     */   {
/*  57 */     super(_parent);
/*     */     
/*  59 */     this.delegate = ((ResourceDownloaderBaseImpl)_delegate);
/*     */     
/*  61 */     this.delegate.setParent(this);
/*     */     
/*  63 */     this.current_delegate = this.delegate;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  69 */     return this.delegate.getName() + ": meta-refresh";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/*  77 */     if (this.size == -2L) {
/*     */       try
/*     */       {
/*  80 */         this.size = getSizeSupport();
/*     */       }
/*     */       finally
/*     */       {
/*  84 */         if (this.size == -2L)
/*     */         {
/*  86 */           this.size = -1L;
/*     */         }
/*     */         
/*  89 */         setSize(this.size);
/*     */       }
/*     */     }
/*     */     
/*  93 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSize(long l)
/*     */   {
/* 100 */     this.size = l;
/*     */     
/* 102 */     if (this.size >= 0L)
/*     */     {
/* 104 */       this.delegate.setSize(this.size);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(String name, Object value)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 115 */     setPropertySupport(name, value);
/*     */     
/* 117 */     this.delegate.setProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getSizeSupport()
/*     */     throws ResourceDownloaderException
/*     */   {
/*     */     try
/*     */     {
/* 126 */       ResourceDownloader x = this.delegate.getClone(this);
/*     */       
/* 128 */       addReportListener(x);
/*     */       
/* 130 */       HTMLPage page = HTMLPageFactory.loadPage(x.download());
/*     */       
/* 132 */       URL base_url = (URL)x.getProperty("URL_URL");
/*     */       
/* 134 */       URL redirect = page.getMetaRefreshURL(base_url);
/*     */       
/* 136 */       if (redirect == null)
/*     */       {
/* 138 */         ResourceDownloaderBaseImpl c = this.delegate.getClone(this);
/*     */         
/* 140 */         addReportListener(c);
/*     */         
/* 142 */         long res = c.getSize();
/*     */         
/* 144 */         setProperties(c);
/*     */         
/* 146 */         return res;
/*     */       }
/*     */       
/* 149 */       ResourceDownloaderURLImpl c = new ResourceDownloaderURLImpl(getParent(), redirect);
/*     */       
/* 151 */       addReportListener(c);
/*     */       
/* 153 */       long res = c.getSize();
/*     */       
/* 155 */       setProperties(c);
/*     */       
/* 157 */       return res;
/*     */     }
/*     */     catch (HTMLException e)
/*     */     {
/* 161 */       throw new ResourceDownloaderException(this, "getSize failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/* 170 */     ResourceDownloaderMetaRefreshImpl c = new ResourceDownloaderMetaRefreshImpl(parent, this.delegate.getClone(this));
/*     */     
/* 172 */     c.setSize(this.size);
/*     */     
/* 174 */     c.setProperties(this);
/*     */     
/* 176 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 184 */     asyncDownload();
/*     */     
/* 186 */     this.done_sem.reserve();
/*     */     
/* 188 */     if ((this.result instanceof InputStream))
/*     */     {
/* 190 */       return (InputStream)this.result;
/*     */     }
/*     */     
/* 193 */     throw ((ResourceDownloaderException)this.result);
/*     */   }
/*     */   
/*     */   public void asyncDownload()
/*     */   {
/*     */     try
/*     */     {
/* 200 */       this.this_mon.enter();
/*     */       
/* 202 */       if (this.cancelled)
/*     */       {
/* 204 */         this.done_sem.release();
/*     */         
/* 206 */         informFailed((ResourceDownloaderException)this.result);
/*     */       }
/*     */       else
/*     */       {
/* 210 */         this.done_count += 1;
/*     */         
/* 212 */         this.current_downloader = this.current_delegate.getClone(this);
/*     */         
/* 214 */         informActivity(getLogIndent() + "Downloading: " + getName());
/*     */         
/* 216 */         this.current_downloader.addListener(this);
/*     */         
/* 218 */         this.current_downloader.asyncDownload();
/*     */       }
/*     */     }
/*     */     finally {
/* 222 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 229 */     setCancelled();
/*     */     try
/*     */     {
/* 232 */       this.this_mon.enter();
/*     */       
/* 234 */       this.result = new ResourceDownloaderCancelledException(this);
/*     */       
/* 236 */       this.cancelled = true;
/*     */       
/* 238 */       informFailed((ResourceDownloaderException)this.result);
/*     */       
/* 240 */       this.done_sem.release();
/*     */       
/* 242 */       if (this.current_downloader != null)
/*     */       {
/* 244 */         this.current_downloader.cancel();
/*     */       }
/*     */     }
/*     */     finally {
/* 248 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 257 */     boolean complete = false;
/*     */     try
/*     */     {
/* 260 */       if (this.done_count == 1)
/*     */       {
/*     */ 
/*     */ 
/* 264 */         boolean marked = false;
/*     */         
/* 266 */         if (data.markSupported())
/*     */         {
/* 268 */           data.mark(data.available());
/*     */           
/* 270 */           marked = true;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 275 */         HTMLPage page = HTMLPageFactory.loadPage(data, !marked);
/*     */         
/* 277 */         URL base_url = (URL)downloader.getProperty("URL_URL");
/*     */         
/* 279 */         URL redirect = page.getMetaRefreshURL(base_url);
/*     */         
/* 281 */         if (redirect == null)
/*     */         {
/* 283 */           if (!marked)
/*     */           {
/* 285 */             failed(downloader, new ResourceDownloaderException(this, "meta refresh tag not found and input stream not recoverable"));
/*     */           }
/*     */           else
/*     */           {
/* 289 */             data.reset();
/*     */             
/* 291 */             complete = true;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 296 */           this.current_delegate = new ResourceDownloaderURLImpl(this, redirect);
/*     */           
/*     */ 
/*     */ 
/* 300 */           asyncDownload();
/*     */         }
/*     */         
/* 303 */         if ((marked) && (!complete))
/*     */         {
/* 305 */           data.close();
/*     */         }
/*     */       }
/*     */       else {
/* 309 */         complete = true;
/*     */       }
/*     */       
/* 312 */       if (complete)
/*     */       {
/* 314 */         if (informComplete(data))
/*     */         {
/* 316 */           this.result = data;
/*     */           
/* 318 */           this.done_sem.release();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 323 */       failed(downloader, new ResourceDownloaderException(this, "meta-refresh processing fails", e));
/*     */     }
/*     */     
/* 326 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 334 */     this.result = e;
/*     */     
/* 336 */     this.done_sem.release();
/*     */     
/* 338 */     informFailed(e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderMetaRefreshImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */