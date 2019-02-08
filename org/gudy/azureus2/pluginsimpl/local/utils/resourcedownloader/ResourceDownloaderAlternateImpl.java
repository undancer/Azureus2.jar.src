/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ public class ResourceDownloaderAlternateImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */   implements ResourceDownloaderListener
/*     */ {
/*     */   protected ResourceDownloader[] delegates;
/*     */   protected int max_to_try;
/*     */   protected boolean random;
/*     */   protected boolean cancelled;
/*     */   protected ResourceDownloader current_downloader;
/*     */   protected int current_index;
/*     */   protected Object result;
/*  47 */   protected AESemaphore done_sem = new AESemaphore("RDAlternate");
/*     */   
/*  49 */   protected long size = -2L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderAlternateImpl(ResourceDownloaderBaseImpl _parent, ResourceDownloader[] _delegates, int _max_to_try, boolean _random)
/*     */   {
/*  58 */     super(_parent);
/*     */     
/*  60 */     this.delegates = _delegates;
/*  61 */     this.max_to_try = _max_to_try;
/*  62 */     this.random = _random;
/*     */     
/*  64 */     for (int i = 0; i < this.delegates.length; i++)
/*     */     {
/*  66 */       ((ResourceDownloaderBaseImpl)this.delegates[i]).setParent(this);
/*     */     }
/*     */     
/*  69 */     if (this.max_to_try < 0)
/*     */     {
/*  71 */       this.max_to_try = this.delegates.length;
/*     */     }
/*     */     else
/*     */     {
/*  75 */       this.max_to_try = Math.min(this.max_to_try, this.delegates.length);
/*     */     }
/*     */     
/*  78 */     if (this.random)
/*     */     {
/*  80 */       List l = new ArrayList(Arrays.asList(this.delegates));
/*     */       
/*  82 */       this.delegates = new ResourceDownloader[this.delegates.length];
/*     */       
/*  84 */       for (int i = 0; i < this.delegates.length; i++)
/*     */       {
/*  86 */         this.delegates[i] = ((ResourceDownloader)l.remove((int)(Math.random() * l.size())));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  94 */     String res = "[";
/*     */     
/*  96 */     for (int i = 0; i < this.delegates.length; i++)
/*     */     {
/*  98 */       res = res + (i == 0 ? "" : ",") + this.delegates[i].getName();
/*     */     }
/*     */     
/* 101 */     return res + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 110 */     if (this.delegates.length == 0)
/*     */     {
/* 112 */       ResourceDownloaderException error = new ResourceDownloaderException(this, "Alternate download fails - 0 alteratives");
/*     */       
/* 114 */       informFailed(error);
/*     */       
/* 116 */       throw error;
/*     */     }
/*     */     
/* 119 */     if (this.size != -2L)
/*     */     {
/* 121 */       return this.size;
/*     */     }
/*     */     try
/*     */     {
/* 125 */       for (int i = 0; i < this.max_to_try; i++) {
/*     */         try
/*     */         {
/* 128 */           ResourceDownloaderBaseImpl c = ((ResourceDownloaderBaseImpl)this.delegates[i]).getClone(this);
/*     */           
/* 130 */           addReportListener(c);
/*     */           
/* 132 */           this.size = c.getSize();
/*     */           
/* 134 */           setProperties(c);
/*     */ 
/*     */         }
/*     */         catch (ResourceDownloaderException e)
/*     */         {
/*     */ 
/* 140 */           if (i == this.delegates.length - 1)
/*     */           {
/* 142 */             throw e;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 148 */       if (this.size == -2L)
/*     */       {
/* 150 */         this.size = -1L;
/*     */       }
/*     */       
/* 153 */       setSize(this.size);
/*     */     }
/*     */     
/* 156 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSize(long l)
/*     */   {
/* 163 */     this.size = l;
/*     */     
/* 165 */     if (this.size >= 0L)
/*     */     {
/* 167 */       for (int i = 0; i < this.delegates.length; i++)
/*     */       {
/* 169 */         ((ResourceDownloaderBaseImpl)this.delegates[i]).setSize(this.size);
/*     */       }
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
/* 181 */     setPropertySupport(name, value);
/*     */     
/* 183 */     for (int i = 0; i < this.delegates.length; i++)
/*     */     {
/* 185 */       ((ResourceDownloaderBaseImpl)this.delegates[i]).setProperty(name, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/* 193 */     ResourceDownloader[] clones = new ResourceDownloader[this.delegates.length];
/*     */     
/* 195 */     for (int i = 0; i < this.delegates.length; i++)
/*     */     {
/* 197 */       clones[i] = ((ResourceDownloaderBaseImpl)this.delegates[i]).getClone(this);
/*     */     }
/*     */     
/* 200 */     ResourceDownloaderAlternateImpl c = new ResourceDownloaderAlternateImpl(parent, clones, this.max_to_try, this.random);
/*     */     
/*     */ 
/* 203 */     c.setSize(this.size);
/*     */     
/* 205 */     c.setProperties(this);
/*     */     
/* 207 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 215 */     if (this.delegates.length == 0)
/*     */     {
/* 217 */       ResourceDownloaderException error = new ResourceDownloaderException(this, "Alternate download fails - 0 alteratives");
/*     */       
/* 219 */       informFailed(error);
/*     */       
/* 221 */       throw error;
/*     */     }
/*     */     
/* 224 */     asyncDownload();
/*     */     
/* 226 */     this.done_sem.reserve();
/*     */     
/* 228 */     if ((this.result instanceof InputStream))
/*     */     {
/* 230 */       return (InputStream)this.result;
/*     */     }
/*     */     
/* 233 */     throw ((ResourceDownloaderException)this.result);
/*     */   }
/*     */   
/*     */   public void asyncDownload()
/*     */   {
/*     */     try
/*     */     {
/* 240 */       this.this_mon.enter();
/*     */       
/* 242 */       if ((this.current_index == this.max_to_try) || (this.cancelled))
/*     */       {
/* 244 */         this.done_sem.release();
/*     */         
/* 246 */         informFailed((ResourceDownloaderException)this.result);
/*     */       }
/*     */       else
/*     */       {
/* 250 */         this.current_downloader = ((ResourceDownloaderBaseImpl)this.delegates[this.current_index]).getClone(this);
/*     */         
/* 252 */         informActivity(getLogIndent() + "Downloading: " + getName());
/*     */         
/* 254 */         this.current_index += 1;
/*     */         
/* 256 */         this.current_downloader.addListener(this);
/*     */         
/* 258 */         this.current_downloader.asyncDownload();
/*     */       }
/*     */     }
/*     */     finally {
/* 262 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 269 */     setCancelled();
/*     */     try
/*     */     {
/* 272 */       this.this_mon.enter();
/*     */       
/* 274 */       this.result = new ResourceDownloaderCancelledException(this);
/*     */       
/* 276 */       this.cancelled = true;
/*     */       
/* 278 */       informFailed((ResourceDownloaderException)this.result);
/*     */       
/* 280 */       this.done_sem.release();
/*     */       
/* 282 */       if (this.current_downloader != null)
/*     */       {
/* 284 */         this.current_downloader.cancel();
/*     */       }
/*     */     }
/*     */     finally {
/* 288 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 297 */     if (informComplete(data))
/*     */     {
/* 299 */       this.result = data;
/*     */       
/* 301 */       this.done_sem.release();
/*     */       
/* 303 */       return true;
/*     */     }
/*     */     
/* 306 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 314 */     this.result = e;
/*     */     
/* 316 */     asyncDownload();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderAlternateImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */