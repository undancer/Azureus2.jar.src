/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ public class ResourceDownloaderRetryImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */   implements ResourceDownloaderListener
/*     */ {
/*     */   protected ResourceDownloaderBaseImpl delegate;
/*     */   protected int retry_count;
/*     */   protected boolean cancelled;
/*     */   protected ResourceDownloader current_downloader;
/*     */   protected int done_count;
/*     */   protected Object result;
/*  44 */   protected AESemaphore done_sem = new AESemaphore("RDRretry");
/*     */   
/*  46 */   protected long size = -2L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderRetryImpl(ResourceDownloaderBaseImpl _parent, ResourceDownloader _delegate, int _retry_count)
/*     */   {
/*  54 */     super(_parent);
/*     */     
/*  56 */     this.delegate = ((ResourceDownloaderBaseImpl)_delegate);
/*     */     
/*  58 */     this.delegate.setParent(this);
/*     */     
/*  60 */     this.retry_count = _retry_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  66 */     return this.delegate.getName() + ", retry=" + this.retry_count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/*  74 */     if (this.size != -2L)
/*     */     {
/*  76 */       return this.size;
/*     */     }
/*     */     try
/*     */     {
/*  80 */       for (int i = 0; i < this.retry_count; i++) {
/*     */         try
/*     */         {
/*  83 */           ResourceDownloaderBaseImpl c = this.delegate.getClone(this);
/*     */           
/*  85 */           addReportListener(c);
/*     */           
/*  87 */           this.size = c.getSize();
/*     */           
/*  89 */           setProperties(c);
/*     */           
/*  91 */           return this.size;
/*     */         }
/*     */         catch (ResourceDownloaderException e)
/*     */         {
/*  95 */           if (i == this.retry_count - 1)
/*     */           {
/*  97 */             throw e;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 103 */       if (this.size == -2L)
/*     */       {
/* 105 */         this.size = -1L;
/*     */       }
/*     */       
/* 108 */       setSize(this.size);
/*     */     }
/*     */     
/* 111 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSize(long l)
/*     */   {
/* 118 */     this.size = l;
/*     */     
/* 120 */     if (this.size >= 0L)
/*     */     {
/* 122 */       this.delegate.setSize(this.size);
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
/* 133 */     setPropertySupport(name, value);
/*     */     
/* 135 */     this.delegate.setProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/* 142 */     ResourceDownloaderRetryImpl c = new ResourceDownloaderRetryImpl(parent, this.delegate.getClone(this), this.retry_count);
/*     */     
/* 144 */     c.setSize(this.size);
/*     */     
/* 146 */     c.setProperties(this);
/*     */     
/* 148 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 156 */     asyncDownload();
/*     */     
/* 158 */     this.done_sem.reserve();
/*     */     
/* 160 */     if ((this.result instanceof InputStream))
/*     */     {
/* 162 */       return (InputStream)this.result;
/*     */     }
/*     */     
/* 165 */     throw ((ResourceDownloaderException)this.result);
/*     */   }
/*     */   
/*     */   public void asyncDownload()
/*     */   {
/*     */     try
/*     */     {
/* 172 */       this.this_mon.enter();
/*     */       
/* 174 */       if ((this.done_count == this.retry_count) || (this.cancelled))
/*     */       {
/* 176 */         this.done_sem.release();
/*     */         
/* 178 */         informFailed((ResourceDownloaderException)this.result);
/*     */       }
/*     */       else
/*     */       {
/* 182 */         this.done_count += 1;
/*     */         
/* 184 */         if (this.done_count > 1)
/*     */         {
/* 186 */           informActivity(getLogIndent() + "  attempt " + this.done_count + " of " + this.retry_count);
/*     */         }
/*     */         
/* 189 */         this.current_downloader = this.delegate.getClone(this);
/*     */         
/* 191 */         this.current_downloader.addListener(this);
/*     */         
/* 193 */         this.current_downloader.asyncDownload();
/*     */       }
/*     */     }
/*     */     finally {
/* 197 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 204 */     setCancelled();
/*     */     try
/*     */     {
/* 207 */       this.this_mon.enter();
/*     */       
/* 209 */       this.result = new ResourceDownloaderCancelledException(this);
/*     */       
/* 211 */       this.cancelled = true;
/*     */       
/* 213 */       informFailed((ResourceDownloaderException)this.result);
/*     */       
/* 215 */       this.done_sem.release();
/*     */       
/* 217 */       if (this.current_downloader != null)
/*     */       {
/* 219 */         this.current_downloader.cancel();
/*     */       }
/*     */     }
/*     */     finally {
/* 223 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 232 */     if (informComplete(data))
/*     */     {
/* 234 */       this.result = data;
/*     */       
/* 236 */       this.done_sem.release();
/*     */       
/* 238 */       return true;
/*     */     }
/*     */     
/* 241 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 249 */     this.result = e;
/*     */     
/* 251 */     asyncDownload();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderRetryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */