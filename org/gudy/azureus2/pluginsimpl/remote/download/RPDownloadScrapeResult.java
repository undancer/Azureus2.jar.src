/*     */ package org.gudy.azureus2.pluginsimpl.remote.download;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPDownloadScrapeResult
/*     */   extends RPObject
/*     */   implements DownloadScrapeResult
/*     */ {
/*     */   protected transient DownloadScrapeResult delegate;
/*     */   public int seed_count;
/*     */   public int non_seed_count;
/*     */   
/*     */   public static RPDownloadScrapeResult create(DownloadScrapeResult _delegate)
/*     */   {
/*  52 */     RPDownloadScrapeResult res = (RPDownloadScrapeResult)_lookupLocal(_delegate);
/*     */     
/*  54 */     if (res == null)
/*     */     {
/*  56 */       res = new RPDownloadScrapeResult(_delegate);
/*     */     }
/*     */     
/*  59 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPDownloadScrapeResult(DownloadScrapeResult _delegate)
/*     */   {
/*  66 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  73 */     this.delegate = ((DownloadScrapeResult)_delegate);
/*     */     
/*  75 */     this.seed_count = this.delegate.getSeedCount();
/*  76 */     this.non_seed_count = this.delegate.getNonSeedCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  84 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  91 */     String method = request.getMethod();
/*     */     
/*  93 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */   {
/* 102 */     notSupported();
/*     */     
/* 104 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseType()
/*     */   {
/* 110 */     notSupported();
/*     */     
/* 112 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 118 */     return this.seed_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNonSeedCount()
/*     */   {
/* 124 */     return this.non_seed_count;
/*     */   }
/*     */   
/*     */   public long getScrapeStartTime() {
/* 128 */     notSupported();
/*     */     
/* 130 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNextScrapeStartTime(long nextScrapeStartTime)
/*     */   {
/* 137 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getNextScrapeStartTime()
/*     */   {
/* 143 */     notSupported();
/*     */     
/* 145 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatus()
/*     */   {
/* 151 */     notSupported();
/*     */     
/* 153 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 159 */     notSupported();
/*     */     
/* 161 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/download/RPDownloadScrapeResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */