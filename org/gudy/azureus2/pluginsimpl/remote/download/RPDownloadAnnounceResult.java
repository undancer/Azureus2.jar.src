/*     */ package org.gudy.azureus2.pluginsimpl.remote.download;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;
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
/*     */ public class RPDownloadAnnounceResult
/*     */   extends RPObject
/*     */   implements DownloadAnnounceResult
/*     */ {
/*     */   protected transient DownloadAnnounceResult delegate;
/*     */   public int seed_count;
/*     */   public int non_seed_count;
/*     */   
/*     */   public static RPDownloadAnnounceResult create(DownloadAnnounceResult _delegate)
/*     */   {
/*  53 */     RPDownloadAnnounceResult res = (RPDownloadAnnounceResult)_lookupLocal(_delegate);
/*     */     
/*  55 */     if (res == null)
/*     */     {
/*  57 */       res = new RPDownloadAnnounceResult(_delegate);
/*     */     }
/*     */     
/*  60 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPDownloadAnnounceResult(DownloadAnnounceResult _delegate)
/*     */   {
/*  67 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  74 */     this.delegate = ((DownloadAnnounceResult)_delegate);
/*     */     
/*  76 */     this.seed_count = this.delegate.getSeedCount();
/*  77 */     this.non_seed_count = this.delegate.getNonSeedCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  85 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  92 */     String method = request.getMethod();
/*     */     
/*  94 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */   {
/* 103 */     notSupported();
/*     */     
/* 105 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseType()
/*     */   {
/* 111 */     notSupported();
/*     */     
/* 113 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getReportedPeerCount()
/*     */   {
/* 119 */     notSupported();
/*     */     
/* 121 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 127 */     return this.seed_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNonSeedCount()
/*     */   {
/* 133 */     return this.non_seed_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getError()
/*     */   {
/* 139 */     notSupported();
/*     */     
/* 141 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 147 */     notSupported();
/*     */     
/* 149 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadAnnounceResultPeer[] getPeers()
/*     */   {
/* 155 */     notSupported();
/*     */     
/* 157 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeToWait()
/*     */   {
/* 163 */     notSupported();
/*     */     
/* 165 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getExtensions()
/*     */   {
/* 171 */     notSupported();
/*     */     
/* 173 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/download/RPDownloadAnnounceResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */