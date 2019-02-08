/*     */ package org.gudy.azureus2.core3.torrentdownloader;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.impl.TorrentDownloaderImpl;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.impl.TorrentDownloaderManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ public class TorrentDownloaderFactory
/*     */ {
/*     */   private static TorrentDownloaderImpl getClass(boolean logged)
/*     */   {
/*     */     try
/*     */     {
/*  46 */       return (TorrentDownloaderImpl)Class.forName("org.gudy.azureus2.core3.torrentdownloader.impl.TorrentDownloader" + (logged ? "Logged" : "") + "Impl").newInstance();
/*     */     } catch (Exception e) {
/*  48 */       Debug.printStackTrace(e); }
/*  49 */     return null;
/*     */   }
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
/*     */   public static TorrentDownloader create(TorrentDownloaderCallBackInterface callback, String url, String referrer, String fileordir, boolean logged)
/*     */   {
/*  74 */     return create(callback, url, referrer, null, fileordir, logged);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TorrentDownloader create(TorrentDownloaderCallBackInterface callback, String url, String referrer, String fileordir)
/*     */   {
/*  84 */     return create(callback, url, referrer, fileordir, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TorrentDownloader create(TorrentDownloaderCallBackInterface callback, String url, String referrer, Map request_properties, String fileordir)
/*     */   {
/*  95 */     return create(callback, url, referrer, request_properties, fileordir, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static TorrentDownloader create(TorrentDownloaderCallBackInterface callback, String url, String referrer, Map request_properties, String fileordir, boolean logged)
/*     */   {
/* 107 */     return new TorrentDownloadRetrier(callback, url, referrer, request_properties, fileordir, logged, null);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader create(TorrentDownloaderCallBackInterface callback, String url, boolean logged) {
/* 111 */     return create(callback, url, null, null, logged);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader create(TorrentDownloaderCallBackInterface callback, String url) {
/* 115 */     return create(callback, url, null, null, false);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader create(String url, String fileordir, boolean logged) {
/* 119 */     return create(null, url, null, fileordir, logged);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader create(String url, String fileordir) {
/* 123 */     return create(null, url, null, fileordir, false);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader create(String url, boolean logged) {
/* 127 */     return create(null, url, null, null, logged);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader create(String url) {
/* 131 */     return create(null, url, null, null, false);
/*     */   }
/*     */   
/*     */   public static void initManager(GlobalManager gm, boolean logged, boolean autostart, String downloaddir) {
/* 135 */     TorrentDownloaderManager.getInstance().init(gm, logged, autostart, downloaddir);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader downloadManaged(String url, String fileordir, boolean logged) {
/* 139 */     return TorrentDownloaderManager.getInstance().download(url, fileordir, logged);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader downloadManaged(String url, String fileordir) {
/* 143 */     return TorrentDownloaderManager.getInstance().download(url, fileordir);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader downloadManaged(String url, boolean logged) {
/* 147 */     return TorrentDownloaderManager.getInstance().download(url, logged);
/*     */   }
/*     */   
/*     */   public static TorrentDownloader downloadManaged(String url) {
/* 151 */     return TorrentDownloaderManager.getInstance().download(url);
/*     */   }
/*     */   
/*     */ 
/*     */   private static class TorrentDownloadRetrier
/*     */     implements TorrentDownloader
/*     */   {
/*     */     private final String url;
/*     */     
/*     */     private final String referrer;
/*     */     
/*     */     private final Map request_properties;
/*     */     
/*     */     private final String fileordir;
/*     */     
/*     */     private final boolean logged;
/*     */     
/*     */     private volatile TorrentDownloaderImpl delegate;
/*     */     
/*     */     private volatile boolean cancelled;
/*     */     
/*     */     private volatile boolean sdp_set;
/*     */     
/*     */     private volatile String sdp_path;
/*     */     
/*     */     private volatile String sdp_file;
/*     */     
/*     */     private volatile boolean dfoc_set;
/*     */     
/*     */     private volatile boolean dfoc;
/*     */     
/*     */     private volatile boolean irc_set;
/*     */     private volatile boolean irc;
/*     */     private volatile String original_error;
/*     */     
/*     */     private TorrentDownloadRetrier(final TorrentDownloaderCallBackInterface _callback, String _url, String _referrer, Map _request_properties, String _fileordir, boolean _logged)
/*     */     {
/* 188 */       this.url = _url;
/* 189 */       this.referrer = _referrer;
/* 190 */       this.request_properties = _request_properties;
/* 191 */       this.fileordir = _fileordir;
/* 192 */       this.logged = _logged;
/*     */       
/* 194 */       TorrentDownloaderCallBackInterface callback = new TorrentDownloaderCallBackInterface()
/*     */       {
/*     */ 
/* 197 */         private final TorrentDownloaderCallBackInterface original_callback = _callback;
/*     */         
/* 199 */         private boolean no_retry = this.original_callback == null;
/*     */         
/* 201 */         private boolean init_reported = false;
/* 202 */         private boolean start_reported = false;
/* 203 */         private boolean finish_reported = false;
/*     */         
/* 205 */         private boolean proxy_tried = false;
/*     */         
/*     */ 
/*     */         private AEProxyFactory.PluginProxy plugin_proxy;
/*     */         
/*     */ 
/*     */         public void TorrentDownloaderEvent(int state, TorrentDownloader _delegate)
/*     */         {
/* 213 */           if (_delegate != TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate)
/*     */           {
/* 215 */             return;
/*     */           }
/*     */           
/* 218 */           if (state == 4)
/*     */           {
/* 220 */             if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.original_error == null)
/*     */             {
/* 222 */               TorrentDownloaderFactory.TorrentDownloadRetrier.this.original_error = TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.getError();
/*     */             }
/*     */           }
/*     */           
/* 226 */           if ((this.plugin_proxy != null) && ((state == 3) || (state == 5) || (state == 6) || (state == 4)))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 232 */             this.plugin_proxy.setOK(state != 4);
/*     */             
/* 234 */             this.plugin_proxy = null;
/*     */           }
/*     */           
/*     */ 
/* 238 */           synchronized (this)
/*     */           {
/* 240 */             if (state == 0)
/*     */             {
/* 242 */               if (this.init_reported)
/*     */               {
/* 244 */                 return;
/*     */               }
/*     */               
/* 247 */               this.init_reported = true;
/*     */             }
/*     */             
/* 250 */             if (state == 1)
/*     */             {
/* 252 */               if (this.start_reported)
/*     */               {
/* 254 */                 return;
/*     */               }
/*     */               
/* 257 */               this.start_reported = true;
/*     */             }
/*     */             
/* 260 */             if (state == 3)
/*     */             {
/* 262 */               if (this.finish_reported)
/*     */               {
/* 264 */                 return;
/*     */               }
/*     */               
/* 267 */               this.finish_reported = true;
/*     */             }
/*     */           }
/*     */           
/* 271 */           if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.cancelled)
/*     */           {
/* 273 */             this.no_retry = true;
/*     */           }
/*     */           
/* 276 */           if (this.no_retry)
/*     */           {
/* 278 */             if (this.original_callback != null)
/*     */             {
/* 280 */               this.original_callback.TorrentDownloaderEvent(state, TorrentDownloaderFactory.TorrentDownloadRetrier.this);
/*     */             }
/*     */             
/* 283 */             return;
/*     */           }
/*     */           
/* 286 */           if ((state == 3) || (state == 5) || (state == 6))
/*     */           {
/*     */ 
/*     */ 
/* 290 */             if ((state == 3) && (this.proxy_tried))
/*     */             {
/* 292 */               TorrentUtils.setObtainedFrom(TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.getFile(), TorrentDownloaderFactory.TorrentDownloadRetrier.this.url);
/*     */             }
/*     */             
/* 295 */             if (this.original_callback != null)
/*     */             {
/* 297 */               this.original_callback.TorrentDownloaderEvent(state, TorrentDownloaderFactory.TorrentDownloadRetrier.this);
/*     */             }
/*     */             
/* 300 */             this.no_retry = true;
/*     */             
/* 302 */             return;
/*     */           }
/*     */           
/* 305 */           if (state == 4)
/*     */           {
/* 307 */             String lc_url = TorrentDownloaderFactory.TorrentDownloadRetrier.this.url.toLowerCase().trim();
/*     */             
/* 309 */             if (!this.proxy_tried)
/*     */             {
/* 311 */               this.proxy_tried = true;
/*     */               
/* 313 */               boolean tor_hack = lc_url.startsWith("tor:");
/*     */               
/* 315 */               if ((lc_url.startsWith("http")) || (tor_hack)) {
/*     */                 try
/*     */                 {
/*     */                   URL original_url;
/*     */                   
/* 320 */                   if (tor_hack)
/*     */                   {
/* 322 */                     URL original_url = new URL(TorrentDownloaderFactory.TorrentDownloadRetrier.this.url.substring(4));
/*     */                     
/* 324 */                     Map<String, Object> options = new HashMap();
/*     */                     
/* 326 */                     options.put("peer_networks", new String[] { "Tor" });
/*     */                     
/* 328 */                     this.plugin_proxy = AEProxyFactory.getPluginProxy("torrent download", original_url, options, true);
/*     */ 
/*     */ 
/*     */                   }
/*     */                   else
/*     */                   {
/*     */ 
/*     */ 
/* 336 */                     original_url = new URL(TorrentDownloaderFactory.TorrentDownloadRetrier.this.url);
/*     */                   }
/*     */                   
/* 339 */                   this.plugin_proxy = AEProxyFactory.getPluginProxy("torrent download", original_url);
/*     */                   
/* 341 */                   if (this.plugin_proxy != null)
/*     */                   {
/* 343 */                     TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate = TorrentDownloaderFactory.getClass(TorrentDownloaderFactory.TorrentDownloadRetrier.this.logged);
/*     */                     
/* 345 */                     if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.sdp_set)
/*     */                     {
/* 347 */                       TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.setDownloadPath(TorrentDownloaderFactory.TorrentDownloadRetrier.this.sdp_path, TorrentDownloaderFactory.TorrentDownloadRetrier.this.sdp_file);
/*     */                     }
/*     */                     
/* 350 */                     if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.dfoc_set)
/*     */                     {
/* 352 */                       TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.setDeleteFileOnCancel(TorrentDownloaderFactory.TorrentDownloadRetrier.this.dfoc);
/*     */                     }
/*     */                     
/* 355 */                     if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.irc_set)
/*     */                     {
/* 357 */                       TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.setIgnoreReponseCode(TorrentDownloaderFactory.TorrentDownloadRetrier.this.irc);
/*     */                     }
/*     */                     
/* 360 */                     Map props = new HashMap();
/*     */                     
/* 362 */                     if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.request_properties != null)
/*     */                     {
/* 364 */                       props.putAll(TorrentDownloaderFactory.TorrentDownloadRetrier.this.request_properties);
/*     */                     }
/*     */                     
/* 367 */                     props.put("HOST", this.plugin_proxy.getURLHostRewrite() + (original_url.getPort() == -1 ? "" : new StringBuilder().append(":").append(original_url.getPort()).toString()));
/*     */                     
/* 369 */                     TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.init(this, this.plugin_proxy.getURL().toExternalForm(), this.plugin_proxy.getProxy(), TorrentDownloaderFactory.TorrentDownloadRetrier.this.referrer == null ? original_url.toExternalForm() : TorrentDownloaderFactory.TorrentDownloadRetrier.this.referrer, props, TorrentDownloaderFactory.TorrentDownloadRetrier.this.fileordir);
/*     */                     
/* 371 */                     TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.start();
/*     */                     
/* 373 */                     return;
/*     */                   }
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 381 */             String retry_url = null;
/*     */             
/* 383 */             if (lc_url.startsWith("http"))
/*     */             {
/* 385 */               retry_url = UrlUtils.parseTextForURL(TorrentDownloaderFactory.TorrentDownloadRetrier.this.url.substring(5), true);
/*     */             }
/*     */             
/* 388 */             if (retry_url != null)
/*     */             {
/* 390 */               TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate = TorrentDownloaderFactory.getClass(TorrentDownloaderFactory.TorrentDownloadRetrier.this.logged);
/*     */               
/* 392 */               if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.sdp_set)
/*     */               {
/* 394 */                 TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.setDownloadPath(TorrentDownloaderFactory.TorrentDownloadRetrier.this.sdp_path, TorrentDownloaderFactory.TorrentDownloadRetrier.this.sdp_file);
/*     */               }
/*     */               
/* 397 */               if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.dfoc_set)
/*     */               {
/* 399 */                 TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.setDeleteFileOnCancel(TorrentDownloaderFactory.TorrentDownloadRetrier.this.dfoc);
/*     */               }
/*     */               
/* 402 */               if (TorrentDownloaderFactory.TorrentDownloadRetrier.this.irc_set)
/*     */               {
/* 404 */                 TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.setIgnoreReponseCode(TorrentDownloaderFactory.TorrentDownloadRetrier.this.irc);
/*     */               }
/*     */               
/* 407 */               TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.init(this, retry_url, null, TorrentDownloaderFactory.TorrentDownloadRetrier.this.referrer, TorrentDownloaderFactory.TorrentDownloadRetrier.this.request_properties, TorrentDownloaderFactory.TorrentDownloadRetrier.this.fileordir);
/*     */               
/* 409 */               this.no_retry = true;
/*     */               
/* 411 */               TorrentDownloaderFactory.TorrentDownloadRetrier.this.delegate.start();
/*     */               
/* 413 */               return;
/*     */             }
/*     */             
/*     */ 
/* 417 */             this.no_retry = true;
/*     */           }
/*     */           
/*     */ 
/* 421 */           if (this.original_callback != null)
/*     */           {
/* 423 */             this.original_callback.TorrentDownloaderEvent(state, TorrentDownloaderFactory.TorrentDownloadRetrier.this);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 428 */       };
/* 429 */       this.delegate = TorrentDownloaderFactory.getClass(this.logged);
/*     */       
/* 431 */       this.delegate.init(callback, this.url, null, this.referrer, this.request_properties, this.fileordir);
/*     */     }
/*     */     
/*     */ 
/*     */     public void start()
/*     */     {
/* 437 */       this.delegate.start();
/*     */     }
/*     */     
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 443 */       this.cancelled = true;
/*     */       
/* 445 */       this.delegate.cancel();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setDownloadPath(String path, String file)
/*     */     {
/* 453 */       this.sdp_set = true;
/* 454 */       this.sdp_path = path;
/* 455 */       this.sdp_file = file;
/*     */       
/* 457 */       this.delegate.setDownloadPath(path, file);
/*     */     }
/*     */     
/*     */ 
/*     */     public int getDownloadState()
/*     */     {
/* 463 */       return this.delegate.getDownloadState();
/*     */     }
/*     */     
/*     */ 
/*     */     public File getFile()
/*     */     {
/* 469 */       return this.delegate.getFile();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPercentDone()
/*     */     {
/* 475 */       return this.delegate.getPercentDone();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getTotalRead()
/*     */     {
/* 481 */       return this.delegate.getTotalRead();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getError()
/*     */     {
/* 487 */       if (this.original_error != null)
/*     */       {
/* 489 */         return this.original_error;
/*     */       }
/*     */       
/* 492 */       return this.delegate.getError();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getStatus()
/*     */     {
/* 498 */       return this.delegate.getStatus();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getURL()
/*     */     {
/* 504 */       return this.delegate.getURL();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getLastReadCount()
/*     */     {
/* 510 */       return this.delegate.getLastReadCount();
/*     */     }
/*     */     
/*     */ 
/*     */     public byte[] getLastReadBytes()
/*     */     {
/* 516 */       return this.delegate.getLastReadBytes();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean getDeleteFileOnCancel()
/*     */     {
/* 522 */       return this.delegate.getDeleteFileOnCancel();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setDeleteFileOnCancel(boolean deleteFileOnCancel)
/*     */     {
/* 529 */       this.dfoc_set = true;
/* 530 */       this.dfoc = deleteFileOnCancel;
/*     */       
/* 532 */       this.delegate.setDeleteFileOnCancel(deleteFileOnCancel);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isIgnoreReponseCode()
/*     */     {
/* 538 */       return this.delegate.isIgnoreReponseCode();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setIgnoreReponseCode(boolean ignoreReponseCode)
/*     */     {
/* 545 */       this.irc_set = true;
/* 546 */       this.irc = ignoreReponseCode;
/*     */       
/* 548 */       this.delegate.setIgnoreReponseCode(ignoreReponseCode);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrentdownloader/TorrentDownloaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */