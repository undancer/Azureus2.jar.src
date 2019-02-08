/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResourceDownloaderErrorImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */ {
/*     */   protected ResourceDownloaderException error;
/*     */   
/*     */   protected ResourceDownloaderErrorImpl(ResourceDownloaderBaseImpl _parent, ResourceDownloaderException _error)
/*     */   {
/*  42 */     super(_parent);
/*     */     
/*  44 */     this.error = _error;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  50 */     return "<error>:" + this.error.getMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/*  57 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/*  65 */     throw this.error;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void asyncDownload() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setSize(long size) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(String name, Object value)
/*     */   {
/*  85 */     setPropertySupport(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/*  93 */     throw this.error;
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/*  99 */     setCancelled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportActivity(String activity)
/*     */   {
/* 106 */     informActivity(activity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(ResourceDownloaderListener l)
/*     */   {
/* 113 */     super.addListener(l);
/*     */     
/* 115 */     informFailed(this.error);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderErrorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */