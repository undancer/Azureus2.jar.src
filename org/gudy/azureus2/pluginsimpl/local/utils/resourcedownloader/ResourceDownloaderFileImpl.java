/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.HTTPUtils;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderCancelledException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
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
/*     */ public class ResourceDownloaderFileImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */ {
/*     */   protected boolean cancelled;
/*     */   protected File file;
/*     */   protected Object result;
/*  47 */   protected AESemaphore done_sem = new AESemaphore("RDTimeout");
/*     */   
/*  49 */   protected long size = -2L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderFileImpl(ResourceDownloaderBaseImpl _parent, File _file)
/*     */   {
/*  56 */     super(_parent);
/*     */     
/*  58 */     this.file = _file;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  64 */     return this.file.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setSize(long size) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/*  78 */     String file_str = this.file.toString();
/*     */     
/*  80 */     int pos = file_str.lastIndexOf(".");
/*     */     
/*     */     String file_type;
/*     */     String file_type;
/*  84 */     if (pos != -1)
/*     */     {
/*  86 */       file_type = file_str.substring(pos + 1);
/*     */     }
/*     */     else
/*     */     {
/*  90 */       file_type = null;
/*     */     }
/*     */     
/*  93 */     setProperty("ContentType", HTTPUtils.guessContentTypeFromFileType(file_type));
/*     */     
/*     */ 
/*  96 */     return FileUtil.getFileOrDirectorySize(this.file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(String name, Object value)
/*     */   {
/* 104 */     setPropertySupport(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/* 111 */     ResourceDownloaderFileImpl c = new ResourceDownloaderFileImpl(getParent(), this.file);
/*     */     
/* 113 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 121 */     asyncDownload();
/*     */     
/* 123 */     this.done_sem.reserve();
/*     */     
/* 125 */     if ((this.result instanceof ResourceDownloaderException))
/*     */     {
/* 127 */       throw ((ResourceDownloaderException)this.result);
/*     */     }
/*     */     
/* 130 */     return (InputStream)this.result;
/*     */   }
/*     */   
/*     */   public void asyncDownload()
/*     */   {
/*     */     try
/*     */     {
/* 137 */       this.this_mon.enter();
/*     */       
/* 139 */       if (!this.cancelled)
/*     */       {
/* 141 */         informActivity(getLogIndent() + (this.file.isDirectory() ? "Processing: " : "Downloading: ") + getName());
/*     */         
/* 143 */         final Object parent_tls = TorrentUtils.getTLS();
/*     */         
/* 145 */         AEThread2 t = new AEThread2("ResourceDownloaderTimeout", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 151 */             Object child_tls = TorrentUtils.getTLS();
/*     */             
/* 153 */             TorrentUtils.setTLS(parent_tls);
/*     */             
/*     */ 
/*     */ 
/*     */             try
/*     */             {
/* 159 */               if (ResourceDownloaderFileImpl.this.file.isDirectory())
/*     */               {
/* 161 */                 ResourceDownloaderFileImpl.this.completed(ResourceDownloaderFileImpl.this, null);
/*     */               }
/*     */               else
/*     */               {
/* 165 */                 ResourceDownloaderFileImpl.this.completed(ResourceDownloaderFileImpl.this, new FileInputStream(ResourceDownloaderFileImpl.this.file));
/*     */               }
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 170 */               ResourceDownloaderFileImpl.this.failed(ResourceDownloaderFileImpl.this, new ResourceDownloaderException(ResourceDownloaderFileImpl.this, "Failed to read file", e));
/*     */               
/* 172 */               Debug.printStackTrace(e);
/*     */             }
/*     */             finally
/*     */             {
/* 176 */               TorrentUtils.setTLS(child_tls);
/*     */             }
/*     */             
/*     */           }
/* 180 */         };
/* 181 */         t.start();
/*     */       }
/*     */     }
/*     */     finally {
/* 185 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 192 */     cancel(new ResourceDownloaderCancelledException(this));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void cancel(ResourceDownloaderException reason)
/*     */   {
/* 199 */     setCancelled();
/*     */     try
/*     */     {
/* 202 */       this.this_mon.enter();
/*     */       
/* 204 */       this.result = reason;
/*     */       
/* 206 */       this.cancelled = true;
/*     */       
/* 208 */       informFailed((ResourceDownloaderException)this.result);
/*     */     }
/*     */     finally
/*     */     {
/* 212 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/* 221 */     if (informComplete(data))
/*     */     {
/* 223 */       this.result = data;
/*     */       
/* 225 */       this.done_sem.release();
/*     */       
/* 227 */       return true;
/*     */     }
/*     */     
/* 230 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 238 */     this.result = e;
/*     */     
/* 240 */     this.done_sem.release();
/*     */     
/* 242 */     informFailed(e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderFileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */