/*     */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentDownloader;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
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
/*     */ public class TorrentDownloaderImpl
/*     */   implements TorrentDownloader
/*     */ {
/*  45 */   private static final LogIDs LOGID = LogIDs.PLUGIN;
/*     */   
/*     */   protected TorrentManagerImpl manager;
/*     */   
/*     */   protected URL url;
/*     */   
/*     */   protected ResourceDownloader _downloader;
/*     */   
/*     */   protected boolean encoding_requested;
/*     */   protected String requested_encoding;
/*     */   protected boolean set_encoding;
/*     */   
/*     */   protected TorrentDownloaderImpl(TorrentManagerImpl _manager, URL _url)
/*     */   {
/*  59 */     this.manager = _manager;
/*  60 */     this.url = _url;
/*     */     
/*  62 */     this._downloader = ResourceDownloaderFactoryImpl.getSingleton().create(this.url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TorrentDownloaderImpl(TorrentManagerImpl _manager, URL _url, String _user_name, String _password)
/*     */   {
/*  72 */     this.manager = _manager;
/*  73 */     this.url = _url;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  81 */     this.set_encoding = true;
/*     */     
/*  83 */     this._downloader = ResourceDownloaderFactoryImpl.getSingleton().create(this.url, _user_name, _password);
/*     */     
/*  85 */     this._downloader.addListener(new ResourceDownloaderAdapter() {
/*     */       public void reportActivity(ResourceDownloader downloader, String activity) {
/*  87 */         if (Logger.isEnabled()) {
/*  88 */           Logger.log(new LogEvent(TorrentDownloaderImpl.LOGID, "TorrentDownloader:" + activity));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Torrent download()
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 100 */       return downloadSupport(this._downloader);
/*     */     }
/*     */     catch (TorrentException e)
/*     */     {
/* 104 */       if (this.url.getProtocol().toLowerCase(Locale.US).startsWith("http"))
/*     */       {
/* 106 */         ResourceDownloader rd = this._downloader.getClone();
/*     */         
/*     */ 
/*     */ 
/* 110 */         UrlUtils.setBrowserHeaders(rd, this.url.toExternalForm());
/*     */         
/* 112 */         return downloadSupport(rd);
/*     */       }
/*     */       
/*     */ 
/* 116 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Torrent downloadSupport(ResourceDownloader downloader)
/*     */     throws TorrentException
/*     */   {
/* 127 */     InputStream is = null;
/*     */     try
/*     */     {
/* 130 */       is = downloader.download();
/*     */       
/* 132 */       TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedInputStream(is);
/*     */       
/* 134 */       if (this.encoding_requested)
/*     */       {
/* 136 */         this.manager.tryToSetTorrentEncoding(torrent, this.requested_encoding);
/*     */ 
/*     */ 
/*     */       }
/* 140 */       else if (this.set_encoding)
/*     */       {
/* 142 */         this.manager.tryToSetDefaultTorrentEncoding(torrent);
/*     */       }
/*     */       
/*     */ 
/* 146 */       return new TorrentImpl(torrent);
/*     */     }
/*     */     catch (TorrentException e)
/*     */     {
/* 150 */       throw e;
/*     */     }
/*     */     catch (ResourceDownloaderException e)
/*     */     {
/* 154 */       throw new TorrentException(e);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 158 */       throw new TorrentException("TorrentDownloader: download fails", e);
/*     */     }
/*     */     finally
/*     */     {
/* 162 */       if (is != null)
/*     */       {
/*     */         try
/*     */         {
/* 166 */           is.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 170 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent download(String encoding)
/*     */     throws TorrentException
/*     */   {
/* 182 */     this.encoding_requested = true;
/* 183 */     this.requested_encoding = encoding;
/*     */     
/* 185 */     return download();
/*     */   }
/*     */   
/*     */   public void setRequestProperty(String key, Object value) throws TorrentException
/*     */   {
/* 190 */     if (this._downloader != null) {
/*     */       try {
/* 192 */         this._downloader.setProperty(key, value);
/*     */       } catch (ResourceDownloaderException e) {
/* 194 */         throw new TorrentException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getRequestProperty(String key) throws TorrentException
/*     */   {
/* 201 */     if (this._downloader != null) {
/*     */       try {
/* 203 */         return this._downloader.getProperty(key);
/*     */       } catch (ResourceDownloaderException e) {
/* 205 */         throw new TorrentException(e);
/*     */       }
/*     */     }
/* 208 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentDownloaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */