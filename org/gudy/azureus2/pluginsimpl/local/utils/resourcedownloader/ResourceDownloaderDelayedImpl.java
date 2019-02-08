/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderDelayedFactory;
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
/*     */ 
/*     */ 
/*     */ public class ResourceDownloaderDelayedImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */ {
/*     */   protected ResourceDownloaderDelayedFactory factory;
/*     */   protected ResourceDownloaderBaseImpl delegate;
/*  39 */   protected long size = -2L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ResourceDownloaderDelayedImpl(ResourceDownloaderBaseImpl _parent, ResourceDownloaderDelayedFactory _factory)
/*     */   {
/*  46 */     super(_parent);
/*     */     
/*  48 */     this.factory = _factory;
/*     */   }
/*     */   
/*     */   protected void getDelegate()
/*     */   {
/*     */     try
/*     */     {
/*  55 */       this.this_mon.enter();
/*     */       
/*  57 */       if (this.delegate == null) {
/*     */         try
/*     */         {
/*  60 */           this.delegate = ((ResourceDownloaderBaseImpl)this.factory.create());
/*     */           
/*  62 */           this.delegate.setParent(this);
/*     */           
/*  64 */           if (this.size >= 0L)
/*     */           {
/*  66 */             this.delegate.setSize(this.size);
/*     */           }
/*     */         }
/*     */         catch (ResourceDownloaderException e)
/*     */         {
/*  71 */           this.delegate = new ResourceDownloaderErrorImpl(this, e);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/*  76 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  83 */     if (this.delegate == null)
/*     */     {
/*  85 */       return "<...>";
/*     */     }
/*     */     
/*  88 */     return this.delegate.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/*  95 */     ResourceDownloaderDelayedImpl c = new ResourceDownloaderDelayedImpl(parent, this.factory);
/*     */     
/*  97 */     c.setSize(this.size);
/*     */     
/*  99 */     c.setProperties(this);
/*     */     
/* 101 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 109 */     getDelegate();
/*     */     
/* 111 */     return this.delegate.download();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void asyncDownload()
/*     */   {
/* 118 */     getDelegate();
/*     */     
/* 120 */     this.delegate.asyncDownload();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSize(long _size)
/*     */   {
/* 127 */     this.size = _size;
/*     */     
/* 129 */     if ((this.delegate != null) && (this.size >= 0L))
/*     */     {
/* 131 */       this.delegate.setSize(this.size);
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
/* 142 */     setPropertySupport(name, value);
/*     */     
/* 144 */     if (this.delegate != null)
/*     */     {
/* 146 */       this.delegate.setProperty(name, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 155 */     getDelegate();
/*     */     
/* 157 */     return this.delegate.getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 163 */     setCancelled();
/*     */     
/* 165 */     getDelegate();
/*     */     
/* 167 */     this.delegate.cancel();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportActivity(String activity)
/*     */   {
/* 174 */     getDelegate();
/*     */     
/* 176 */     this.delegate.reportActivity(activity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(ResourceDownloaderListener l)
/*     */   {
/* 183 */     getDelegate();
/*     */     
/* 185 */     this.delegate.addListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(ResourceDownloaderListener l)
/*     */   {
/* 192 */     getDelegate();
/*     */     
/* 194 */     this.delegate.removeListener(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderDelayedImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */