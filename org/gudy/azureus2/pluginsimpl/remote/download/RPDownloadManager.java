/*     */ package org.gudy.azureus2.pluginsimpl.remote.download;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStubListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.DefaultSaveLocationManager;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.torrent.RPTorrent;
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
/*     */ public class RPDownloadManager
/*     */   extends RPObject
/*     */   implements DownloadManager
/*     */ {
/*     */   protected transient DownloadManager delegate;
/*     */   
/*     */   public static RPDownloadManager create(DownloadManager _delegate)
/*     */   {
/*  51 */     RPDownloadManager res = (RPDownloadManager)_lookupLocal(_delegate);
/*     */     
/*  53 */     if (res == null)
/*     */     {
/*  55 */       res = new RPDownloadManager(_delegate);
/*     */     }
/*     */     
/*  58 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPDownloadManager(DownloadManager _delegate)
/*     */   {
/*  65 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  72 */     this.delegate = ((DownloadManager)_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  80 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  88 */     String method = request.getMethod();
/*  89 */     Object[] params = request.getParams();
/*     */     
/*  91 */     if (method.equals("getDownloads"))
/*     */     {
/*  93 */       Download[] downloads = this.delegate.getDownloads();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  98 */       RPDownload[] res = new RPDownload[downloads.length];
/*     */       
/* 100 */       for (int i = 0; i < res.length; i++)
/*     */       {
/* 102 */         res[i] = RPDownload.create(downloads[i]);
/*     */       }
/*     */       
/* 105 */       return new RPReply(res);
/*     */     }
/* 107 */     if (method.equals("getDownloads[boolean]"))
/*     */     {
/* 109 */       Download[] downloads = this.delegate.getDownloads(((Boolean)request.getParams()[0]).booleanValue());
/*     */       
/* 111 */       RPDownload[] res = new RPDownload[downloads.length];
/*     */       
/* 113 */       for (int i = 0; i < res.length; i++)
/*     */       {
/* 115 */         res[i] = RPDownload.create(downloads[i]);
/*     */       }
/*     */       
/* 118 */       return new RPReply(res);
/*     */     }
/* 120 */     if (method.equals("addDownload[Torrent]")) {
/*     */       try
/*     */       {
/* 123 */         RPTorrent torrent = (RPTorrent)request.getParams()[0];
/*     */         
/* 125 */         Download res = this.delegate.addDownload((Torrent)torrent._setLocal());
/*     */         
/* 127 */         return new RPReply(RPDownload.create(res));
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 131 */         throw new RPException("DownloadManager::addDownload failed", e);
/*     */       }
/*     */     }
/* 134 */     if (method.equals("addDownload[Torrent,String,String]")) {
/*     */       try
/*     */       {
/* 137 */         RPTorrent torrent = (RPTorrent)request.getParams()[0];
/* 138 */         File f1 = params[1] == null ? null : new File((String)params[1]);
/* 139 */         File f2 = params[2] == null ? null : new File((String)params[2]);
/*     */         
/* 141 */         Download res = this.delegate.addDownload((Torrent)torrent._setLocal(), f1, f2);
/*     */         
/* 143 */         return new RPReply(RPDownload.create(res));
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 147 */         throw new RPException("DownloadManager::addDownload failed", e);
/*     */       }
/*     */     }
/* 150 */     if (method.equals("addDownload[URL]"))
/*     */     {
/*     */       try {
/* 153 */         this.delegate.addDownload((URL)request.getParams()[0]);
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 157 */         throw new RPException("DownloadManager::addDownload failed", e);
/*     */       }
/*     */       
/* 160 */       return new RPReply(null);
/*     */     }
/* 162 */     if (method.equals("pauseDownloads"))
/*     */     {
/* 164 */       this.delegate.pauseDownloads();
/*     */       
/* 166 */       return null;
/*     */     }
/* 168 */     if (method.equals("resumeDownloads"))
/*     */     {
/* 170 */       this.delegate.resumeDownloads();
/*     */       
/* 172 */       return null;
/*     */     }
/* 174 */     if (method.equals("stopAllDownloads"))
/*     */     {
/* 176 */       this.delegate.stopAllDownloads();
/*     */       
/* 178 */       return null;
/*     */     }
/* 180 */     if (method.equals("startAllDownloads"))
/*     */     {
/* 182 */       this.delegate.startAllDownloads();
/*     */       
/* 184 */       return null;
/*     */     }
/*     */     
/* 187 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDownload(File torrent_file)
/*     */     throws DownloadException
/*     */   {
/* 198 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDownload(URL url, URL referer)
/*     */   {
/* 206 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDownload(URL url)
/*     */     throws DownloadException
/*     */   {
/* 215 */     this._dispatcher.dispatch(new RPRequest(this, "addDownload[URL]", new Object[] { url })).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDownload(URL url, boolean auto_download)
/*     */     throws DownloadException
/*     */   {
/* 225 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDownload(URL url, Map request_properties)
/*     */   {
/* 233 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download addDownload(Torrent torrent)
/*     */     throws DownloadException
/*     */   {
/*     */     try
/*     */     {
/* 243 */       RPDownload res = (RPDownload)this._dispatcher.dispatch(new RPRequest(this, "addDownload[Torrent]", new Object[] { torrent })).getResponse();
/*     */       
/* 245 */       res._setRemote(this._dispatcher);
/*     */       
/* 247 */       return res;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 251 */       if ((e.getCause() instanceof DownloadException))
/*     */       {
/* 253 */         throw ((DownloadException)e.getCause());
/*     */       }
/*     */       
/* 256 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Download addDownload(Torrent torrent, File torrent_location, File data_location)
/*     */     throws DownloadException
/*     */   {
/*     */     try
/*     */     {
/* 270 */       RPDownload res = (RPDownload)this._dispatcher.dispatch(new RPRequest(this, "addDownload[Torrent,String,String]", new Object[] { torrent, torrent_location == null ? null : torrent_location.toString(), data_location == null ? null : data_location.toString() })).getResponse();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 278 */       res._setRemote(this._dispatcher);
/*     */       
/* 280 */       return res;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 284 */       if ((e.getCause() instanceof DownloadException))
/*     */       {
/* 286 */         throw ((DownloadException)e.getCause());
/*     */       }
/*     */       
/* 289 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Download addDownloadStopped(Torrent torrent, File torrent_location, File data_location)
/*     */     throws DownloadException
/*     */   {
/* 301 */     notSupported();
/*     */     
/* 303 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Download addNonPersistentDownload(Torrent torrent, File torrent_location, File data_location)
/*     */     throws DownloadException
/*     */   {
/* 314 */     notSupported();
/*     */     
/* 316 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Download addNonPersistentDownloadStopped(Torrent torrent, File torrentLocation, File dataLocation)
/*     */     throws DownloadException
/*     */   {
/* 327 */     notSupported();
/*     */     
/* 329 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearNonPersistentDownloadState(byte[] hash)
/*     */   {
/* 336 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload(Torrent torrent)
/*     */   {
/* 343 */     notSupported();
/*     */     
/* 345 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload(byte[] hash)
/*     */   {
/* 352 */     notSupported();
/*     */     
/* 354 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Download[] getDownloads()
/*     */   {
/* 360 */     RPDownload[] res = (RPDownload[])this._dispatcher.dispatch(new RPRequest(this, "getDownloads", null)).getResponse();
/*     */     
/* 362 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 364 */       res[i]._setRemote(this._dispatcher);
/*     */     }
/*     */     
/* 367 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public Download[] getDownloads(boolean bSort)
/*     */   {
/* 373 */     RPDownload[] res = (RPDownload[])this._dispatcher.dispatch(new RPRequest(this, "getDownloads[boolean]", new Object[] { Boolean.valueOf(bSort) })).getResponse();
/*     */     
/*     */ 
/*     */ 
/* 377 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 379 */       res[i]._setRemote(this._dispatcher);
/*     */     }
/*     */     
/* 382 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public void pauseDownloads()
/*     */   {
/* 388 */     this._dispatcher.dispatch(new RPRequest(this, "pauseDownloads", null)).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canPauseDownloads()
/*     */   {
/* 394 */     notSupported();
/*     */     
/* 396 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void resumeDownloads()
/*     */   {
/* 402 */     this._dispatcher.dispatch(new RPRequest(this, "resumeDownloads", null)).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canResumeDownloads()
/*     */   {
/* 408 */     notSupported();
/*     */     
/* 410 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void startAllDownloads()
/*     */   {
/* 416 */     this._dispatcher.dispatch(new RPRequest(this, "startAllDownloads", null)).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */   public void stopAllDownloads()
/*     */   {
/* 422 */     this._dispatcher.dispatch(new RPRequest(this, "stopAllDownloads", null)).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadManagerStats getStats()
/*     */   {
/* 428 */     notSupported();
/*     */     
/* 430 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSeedingOnly()
/*     */   {
/* 436 */     notSupported();
/*     */     
/* 438 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(DownloadManagerListener l)
/*     */   {
/* 445 */     notSupported();
/*     */   }
/*     */   
/*     */   public void addListener(DownloadManagerListener l, boolean notify) {
/* 449 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(DownloadManagerListener l)
/*     */   {
/* 456 */     notSupported();
/*     */   }
/*     */   
/*     */   public void removeListener(DownloadManagerListener l, boolean notify) {
/* 460 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addDownloadWillBeAddedListener(DownloadWillBeAddedListener listener)
/*     */   {
/* 467 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDownloadWillBeAddedListener(DownloadWillBeAddedListener listener)
/*     */   {
/* 474 */     notSupported();
/*     */   }
/*     */   
/*     */   public DownloadEventNotifier getGlobalDownloadEventNotifier() {
/* 478 */     notSupported();
/* 479 */     return null;
/*     */   }
/*     */   
/*     */   public void setSaveLocationManager(SaveLocationManager manager) {
/* 483 */     notSupported();
/*     */   }
/*     */   
/*     */   public SaveLocationManager getSaveLocationManager() {
/* 487 */     notSupported();
/* 488 */     return null;
/*     */   }
/*     */   
/*     */   public DefaultSaveLocationManager getDefaultSaveLocationManager() {
/* 492 */     notSupported();
/* 493 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadStub[] getDownloadStubs()
/*     */   {
/* 499 */     notSupported();
/*     */     
/* 501 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadStubCount()
/*     */   {
/* 507 */     notSupported();
/*     */     
/* 509 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DownloadStub lookupDownloadStub(byte[] hash)
/*     */   {
/* 516 */     notSupported();
/*     */     
/* 518 */     return null;
/*     */   }
/*     */   
/*     */   public void addDownloadStubListener(DownloadStubListener l, boolean inform_of_current) {
/* 522 */     notSupported();
/*     */   }
/*     */   
/*     */   public void removeDownloadStubListener(DownloadStubListener l) {
/* 526 */     notSupported();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/download/RPDownloadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */