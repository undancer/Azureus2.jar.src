/*     */ package org.gudy.azureus2.pluginsimpl.remote.torrent;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentCreator;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentDownloader;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManagerListener;
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
/*     */ public class RPTorrentManager
/*     */   extends RPObject
/*     */   implements TorrentManager
/*     */ {
/*     */   protected transient TorrentManager delegate;
/*     */   
/*     */   public static RPTorrentManager create(TorrentManager _delegate)
/*     */   {
/*  49 */     RPTorrentManager res = (RPTorrentManager)_lookupLocal(_delegate);
/*     */     
/*  51 */     if (res == null)
/*     */     {
/*  53 */       res = new RPTorrentManager(_delegate);
/*     */     }
/*     */     
/*  56 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPTorrentManager(TorrentManager _delegate)
/*     */   {
/*  63 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  70 */     this.delegate = ((TorrentManager)_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  78 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  86 */     String method = request.getMethod();
/*  87 */     Object[] params = request.getParams();
/*     */     
/*  89 */     if (method.equals("getURLDownloader[URL]"))
/*     */       try
/*     */       {
/*  92 */         TorrentDownloader dl = this.delegate.getURLDownloader((URL)params[0]);
/*     */         
/*  94 */         RPTorrentDownloader res = RPTorrentDownloader.create(dl);
/*     */         
/*  96 */         return new RPReply(res);
/*     */       }
/*     */       catch (TorrentException e)
/*     */       {
/* 100 */         return new RPReply(e);
/*     */       }
/* 102 */     if (method.equals("getURLDownloader[URL,String,String]"))
/*     */       try
/*     */       {
/* 105 */         TorrentDownloader dl = this.delegate.getURLDownloader((URL)params[0], (String)params[1], (String)params[2]);
/*     */         
/* 107 */         RPTorrentDownloader res = RPTorrentDownloader.create(dl);
/*     */         
/* 109 */         return new RPReply(res);
/*     */       }
/*     */       catch (TorrentException e)
/*     */       {
/* 113 */         return new RPReply(e);
/*     */       }
/* 115 */     if (method.equals("createFromBEncodedData[byte[]]")) {
/*     */       try
/*     */       {
/* 118 */         return new RPReply(RPTorrent.create(this.delegate.createFromBEncodedData((byte[])params[0])));
/*     */       }
/*     */       catch (TorrentException e)
/*     */       {
/* 122 */         return new RPReply(e);
/*     */       }
/*     */     }
/*     */     
/* 126 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentDownloader getURLDownloader(URL url)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 138 */       RPTorrentDownloader resp = (RPTorrentDownloader)this._dispatcher.dispatch(new RPRequest(this, "getURLDownloader[URL]", new Object[] { url })).getResponse();
/*     */       
/* 140 */       resp._setRemote(this._dispatcher);
/*     */       
/* 142 */       return resp;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 146 */       if ((e.getCause() instanceof TorrentException))
/*     */       {
/* 148 */         throw ((TorrentException)e.getCause());
/*     */       }
/*     */       
/* 151 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentDownloader getURLDownloader(URL url, String user_name, String password)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 164 */       RPTorrentDownloader resp = (RPTorrentDownloader)this._dispatcher.dispatch(new RPRequest(this, "getURLDownloader[URL,String,String]", new Object[] { url, user_name, password })).getResponse();
/*     */       
/* 166 */       resp._setRemote(this._dispatcher);
/*     */       
/* 168 */       return resp;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 172 */       if ((e.getCause() instanceof TorrentException))
/*     */       {
/* 174 */         throw ((TorrentException)e.getCause());
/*     */       }
/*     */       
/* 177 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedFile(File file)
/*     */     throws TorrentException
/*     */   {
/* 187 */     notSupported();
/*     */     
/* 189 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedFile(File file, boolean for_seeding)
/*     */     throws TorrentException
/*     */   {
/* 199 */     notSupported();
/*     */     
/* 201 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedInputStream(InputStream data)
/*     */     throws TorrentException
/*     */   {
/* 209 */     notSupported();
/*     */     
/* 211 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedData(byte[] data)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 221 */       RPTorrent res = (RPTorrent)this._dispatcher.dispatch(new RPRequest(this, "createFromBEncodedData[byte[]]", new Object[] { data })).getResponse();
/*     */       
/* 223 */       res._setRemote(this._dispatcher);
/*     */       
/* 225 */       return res;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 229 */       if ((e.getCause() instanceof TorrentException))
/*     */       {
/* 231 */         throw ((TorrentException)e.getCause());
/*     */       }
/*     */       
/* 234 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedData(byte[] data, int preserve)
/*     */     throws TorrentException
/*     */   {
/* 246 */     notSupported();
/*     */     
/* 248 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedFile(File file, int preserve)
/*     */     throws TorrentException
/*     */   {
/* 259 */     notSupported();
/*     */     
/* 261 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromBEncodedInputStream(InputStream data, int preserve)
/*     */     throws TorrentException
/*     */   {
/* 271 */     notSupported();
/*     */     
/* 273 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromDataFile(File data, URL announce_url)
/*     */     throws TorrentException
/*     */   {
/* 283 */     notSupported();
/*     */     
/* 285 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent createFromDataFile(File data, URL announce_url, boolean include_other_hashes)
/*     */     throws TorrentException
/*     */   {
/* 296 */     notSupported();
/*     */     
/* 298 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentCreator createFromDataFileEx(File data, URL announce_url, boolean include_other_hashes)
/*     */     throws TorrentException
/*     */   {
/* 310 */     notSupported();
/*     */     
/* 312 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentAttribute[] getDefinedAttributes()
/*     */   {
/* 318 */     notSupported();
/*     */     
/* 320 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TorrentAttribute getAttribute(String name)
/*     */   {
/* 327 */     notSupported();
/*     */     
/* 329 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TorrentAttribute getPluginAttribute(String name)
/*     */   {
/* 336 */     notSupported();
/*     */     
/* 338 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TorrentManagerListener l)
/*     */   {
/* 345 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TorrentManagerListener l)
/*     */   {
/* 352 */     notSupported();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/torrent/RPTorrentManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */