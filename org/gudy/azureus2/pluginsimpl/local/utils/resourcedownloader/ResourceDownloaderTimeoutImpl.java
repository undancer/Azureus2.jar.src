/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ public class ResourceDownloaderTimeoutImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */   implements ResourceDownloaderListener
/*     */ {
/*     */   protected ResourceDownloaderBaseImpl delegate;
/*     */   protected int timeout_millis;
/*     */   protected boolean cancelled;
/*     */   protected ResourceDownloaderBaseImpl current_downloader;
/*     */   protected Object result;
/*  45 */   protected AESemaphore done_sem = new AESemaphore("RDTimeout");
/*     */   
/*  47 */   protected long size = -2L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderTimeoutImpl(ResourceDownloaderBaseImpl _parent, ResourceDownloader _delegate, int _timeout_millis)
/*     */   {
/*  55 */     super(_parent);
/*     */     
/*  57 */     this.delegate = ((ResourceDownloaderBaseImpl)_delegate);
/*     */     
/*  59 */     this.delegate.setParent(this);
/*     */     
/*  61 */     this.timeout_millis = _timeout_millis;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  67 */     return this.delegate.getName() + ": timeout=" + this.timeout_millis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/*  75 */     if (this.size != -2L)
/*     */     {
/*  77 */       return this.size;
/*     */     }
/*     */     try
/*     */     {
/*  81 */       ResourceDownloaderTimeoutImpl x = new ResourceDownloaderTimeoutImpl(getParent(), this.delegate.getClone(this), this.timeout_millis);
/*     */       
/*  83 */       addReportListener(x);
/*     */       
/*  85 */       this.size = x.getSizeSupport();
/*     */       
/*  87 */       setProperties(x);
/*     */     }
/*     */     finally
/*     */     {
/*  91 */       if (this.size == -2L)
/*     */       {
/*  93 */         this.size = -1L;
/*     */       }
/*     */       
/*  96 */       setSize(this.size);
/*     */     }
/*     */     
/*  99 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSize(long l)
/*     */   {
/* 106 */     this.size = l;
/*     */     
/* 108 */     if (this.size >= 0L)
/*     */     {
/* 110 */       this.delegate.setSize(this.size);
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
/* 121 */     setPropertySupport(name, value);
/*     */     
/* 123 */     this.delegate.setProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/* 130 */     ResourceDownloaderTimeoutImpl c = new ResourceDownloaderTimeoutImpl(getParent(), this.delegate.getClone(parent), this.timeout_millis);
/*     */     
/* 132 */     c.setSize(this.size);
/*     */     
/* 134 */     c.setProperties(this);
/*     */     
/* 136 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 144 */     asyncDownload();
/*     */     
/* 146 */     this.done_sem.reserve();
/*     */     
/* 148 */     if ((this.result instanceof InputStream))
/*     */     {
/* 150 */       return (InputStream)this.result;
/*     */     }
/*     */     
/* 153 */     throw ((ResourceDownloaderException)this.result);
/*     */   }
/*     */   
/*     */   public void asyncDownload()
/*     */   {
/*     */     try
/*     */     {
/* 160 */       this.this_mon.enter();
/*     */       
/* 162 */       if (!this.cancelled)
/*     */       {
/* 164 */         this.current_downloader = this.delegate.getClone(this);
/*     */         
/* 166 */         informActivity(getLogIndent() + "Downloading: " + getName());
/*     */         
/* 168 */         this.current_downloader.addListener(this);
/*     */         
/* 170 */         this.current_downloader.asyncDownload();
/*     */         
/* 172 */         Thread t = new AEThread("ResourceDownloaderTimeout")
/*     */         {
/*     */           public void runSupport()
/*     */           {
/*     */             try
/*     */             {
/* 178 */               Thread.sleep(ResourceDownloaderTimeoutImpl.this.timeout_millis);
/*     */               
/* 180 */               ResourceDownloaderTimeoutImpl.this.cancel(new ResourceDownloaderException(ResourceDownloaderTimeoutImpl.this, "Download timeout"));
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 184 */               Debug.printStackTrace(e);
/*     */             }
/*     */             
/*     */           }
/* 188 */         };
/* 189 */         t.setDaemon(true);
/*     */         
/* 191 */         t.start();
/*     */       }
/*     */     }
/*     */     finally {
/* 195 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getSizeSupport()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 204 */     asyncGetSize();
/*     */     
/* 206 */     this.done_sem.reserve();
/*     */     
/* 208 */     if ((this.result instanceof Long))
/*     */     {
/* 210 */       return ((Long)this.result).longValue();
/*     */     }
/*     */     
/* 213 */     throw ((ResourceDownloaderException)this.result);
/*     */   }
/*     */   
/*     */   public void asyncGetSize()
/*     */   {
/*     */     try
/*     */     {
/* 220 */       this.this_mon.enter();
/*     */       
/* 222 */       if (!this.cancelled)
/*     */       {
/* 224 */         this.current_downloader = this.delegate.getClone(this);
/*     */         
/* 226 */         Thread size_thread = new AEThread("ResourceDownloader:size getter")
/*     */         {
/*     */           public void runSupport()
/*     */           {
/*     */             try
/*     */             {
/* 232 */               long res = ResourceDownloaderTimeoutImpl.this.current_downloader.getSize();
/*     */               
/* 234 */               ResourceDownloaderTimeoutImpl.this.result = new Long(res);
/*     */               
/* 236 */               ResourceDownloaderTimeoutImpl.this.setProperties(ResourceDownloaderTimeoutImpl.this.current_downloader);
/*     */               
/* 238 */               ResourceDownloaderTimeoutImpl.this.done_sem.release();
/*     */             }
/*     */             catch (ResourceDownloaderException e)
/*     */             {
/* 242 */               ResourceDownloaderTimeoutImpl.this.failed(ResourceDownloaderTimeoutImpl.this.current_downloader, e);
/*     */             }
/*     */             
/*     */           }
/* 246 */         };
/* 247 */         size_thread.setDaemon(true);
/*     */         
/* 249 */         size_thread.start();
/*     */         
/* 251 */         Thread t = new AEThread("ResourceDownloaderTimeout")
/*     */         {
/*     */           public void runSupport()
/*     */           {
/*     */             try
/*     */             {
/* 257 */               Thread.sleep(ResourceDownloaderTimeoutImpl.this.timeout_millis);
/*     */               
/* 259 */               ResourceDownloaderTimeoutImpl.this.cancel(new ResourceDownloaderException(ResourceDownloaderTimeoutImpl.this, "getSize timeout"));
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 263 */               Debug.printStackTrace(e);
/*     */             }
/*     */             
/*     */           }
/* 267 */         };
/* 268 */         t.setDaemon(true);
/*     */         
/* 270 */         t.start();
/*     */       }
/*     */     }
/*     */     finally {
/* 274 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 281 */     cancel(new ResourceDownloaderCancelledException(this));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void cancel(ResourceDownloaderException reason)
/*     */   {
/* 288 */     setCancelled();
/*     */     try
/*     */     {
/* 291 */       this.this_mon.enter();
/*     */       
/* 293 */       this.result = reason;
/*     */       
/* 295 */       this.cancelled = true;
/*     */       
/* 297 */       informFailed((ResourceDownloaderException)this.result);
/*     */       
/* 299 */       if (this.current_downloader != null)
/*     */       {
/* 301 */         this.current_downloader.cancel();
/*     */       }
/*     */     }
/*     */     finally {
/* 305 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 314 */     if (informComplete(data))
/*     */     {
/* 316 */       this.result = data;
/*     */       
/* 318 */       this.done_sem.release();
/*     */       
/* 320 */       return true;
/*     */     }
/*     */     
/* 323 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 331 */     this.result = e;
/*     */     
/* 333 */     this.done_sem.release();
/*     */     
/* 335 */     informFailed(e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderTimeoutImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */