/*     */ package org.gudy.azureus2.pluginsimpl.remote.torrent;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentDownloader;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
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
/*     */ public class RPTorrentDownloader
/*     */   extends RPObject
/*     */   implements TorrentDownloader
/*     */ {
/*     */   protected transient TorrentDownloader delegate;
/*     */   
/*     */   public static RPTorrentDownloader create(TorrentDownloader _delegate)
/*     */   {
/*  44 */     RPTorrentDownloader res = (RPTorrentDownloader)_lookupLocal(_delegate);
/*     */     
/*  46 */     if (res == null)
/*     */     {
/*  48 */       res = new RPTorrentDownloader(_delegate);
/*     */     }
/*     */     
/*  51 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPTorrentDownloader(TorrentDownloader _delegate)
/*     */   {
/*  58 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  65 */     this.delegate = ((TorrentDownloader)_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  73 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  81 */     String method = request.getMethod();
/*     */     
/*  83 */     if (method.equals("download"))
/*     */       try
/*     */       {
/*  86 */         Torrent to = this.delegate.download();
/*     */         
/*  88 */         RPTorrent res = RPTorrent.create(to);
/*     */         
/*  90 */         return new RPReply(res);
/*     */       }
/*     */       catch (TorrentException e)
/*     */       {
/*  94 */         return new RPReply(e);
/*     */       }
/*  96 */     if (method.equals("download[String]")) {
/*     */       try
/*     */       {
/*  99 */         Torrent to = this.delegate.download((String)request.getParams()[0]);
/*     */         
/* 101 */         RPTorrent res = RPTorrent.create(to);
/*     */         
/* 103 */         return new RPReply(res);
/*     */       }
/*     */       catch (TorrentException e)
/*     */       {
/* 107 */         return new RPReply(e);
/*     */       }
/*     */     }
/*     */     
/* 111 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent download()
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 122 */       RPTorrent resp = (RPTorrent)this._dispatcher.dispatch(new RPRequest(this, "download", null)).getResponse();
/*     */       
/* 124 */       resp._setRemote(this._dispatcher);
/*     */       
/* 126 */       return resp;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 130 */       if ((e.getCause() instanceof TorrentException))
/*     */       {
/* 132 */         throw ((TorrentException)e.getCause());
/*     */       }
/*     */       
/* 135 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Torrent download(String encoding)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 146 */       RPTorrent resp = (RPTorrent)this._dispatcher.dispatch(new RPRequest(this, "download[String]", new Object[] { encoding })).getResponse();
/*     */       
/* 148 */       resp._setRemote(this._dispatcher);
/*     */       
/* 150 */       return resp;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 154 */       if ((e.getCause() instanceof TorrentException))
/*     */       {
/* 156 */         throw ((TorrentException)e.getCause());
/*     */       }
/*     */       
/* 159 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setRequestProperty(String key, Object value) throws TorrentException
/*     */   {
/* 165 */     this.delegate.setRequestProperty(key, value);
/*     */   }
/*     */   
/*     */   public Object getRequestProperty(String key) throws TorrentException
/*     */   {
/* 170 */     return this.delegate.getRequestProperty(key);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/torrent/RPTorrentDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */