/*     */ package org.gudy.azureus2.pluginsimpl.remote.torrent;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLList;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentFile;
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
/*     */ public class RPTorrent
/*     */   extends RPObject
/*     */   implements Torrent
/*     */ {
/*     */   protected transient Torrent delegate;
/*     */   public String name;
/*     */   public long size;
/*     */   public byte[] hash;
/*     */   
/*     */   public static RPTorrent create(Torrent _delegate)
/*     */   {
/*  51 */     RPTorrent res = (RPTorrent)_lookupLocal(_delegate);
/*     */     
/*  53 */     if (res == null)
/*     */     {
/*  55 */       res = new RPTorrent(_delegate);
/*     */     }
/*     */     
/*  58 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPTorrent(Torrent _delegate)
/*     */   {
/*  65 */     super(_delegate);
/*     */     
/*  67 */     this.delegate = _delegate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  74 */     this.delegate = ((Torrent)_delegate);
/*     */     
/*  76 */     this.name = this.delegate.getName();
/*  77 */     this.size = this.delegate.getSize();
/*  78 */     this.hash = this.delegate.getHash();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  86 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  94 */     String method = request.getMethod();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 109 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getAnnounceURL()
/*     */   {
/* 115 */     notSupported();
/*     */     
/* 117 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAnnounceURL(URL url)
/*     */   {
/* 124 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentAnnounceURLList getAnnounceURLList()
/*     */   {
/* 130 */     notSupported();
/*     */     
/* 132 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDecentralised()
/*     */   {
/* 138 */     notSupported();
/*     */     
/* 140 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDecentralisedBackupEnabled()
/*     */   {
/* 146 */     notSupported();
/*     */     
/* 148 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDecentralisedBackupRequested(boolean requested)
/*     */   {
/* 155 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDecentralisedBackupRequested()
/*     */   {
/* 161 */     notSupported();
/*     */     
/* 163 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPrivate()
/*     */   {
/* 169 */     notSupported();
/*     */     
/* 171 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean wasCreatedByUs()
/*     */   {
/* 177 */     notSupported();
/*     */     
/* 179 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPrivate(boolean priv)
/*     */   {
/* 186 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getHash()
/*     */   {
/* 192 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 198 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getComment()
/*     */   {
/* 204 */     notSupported();
/*     */     
/* 206 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setComment(String comment)
/*     */   {
/* 213 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreationDate()
/*     */   {
/* 219 */     notSupported();
/*     */     
/* 221 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCreatedBy()
/*     */   {
/* 227 */     notSupported();
/*     */     
/* 229 */     return null;
/*     */   }
/*     */   
/*     */   public long getPieceSize()
/*     */   {
/* 234 */     notSupported();
/*     */     
/* 236 */     return 0L;
/*     */   }
/*     */   
/*     */   public long getPieceCount()
/*     */   {
/* 241 */     notSupported();
/*     */     
/* 243 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[][] getPieces()
/*     */   {
/* 249 */     notSupported();
/*     */     
/* 251 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getMagnetURI()
/*     */   {
/* 257 */     notSupported();
/*     */     
/* 259 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getEncoding()
/*     */   {
/* 265 */     notSupported();
/*     */     
/* 267 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEncoding(String encoding)
/*     */   {
/* 273 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDefaultEncoding()
/*     */   {
/* 279 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentFile[] getFiles()
/*     */   {
/* 285 */     notSupported();
/*     */     
/* 287 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getAdditionalProperty(String name)
/*     */   {
/* 294 */     notSupported();
/*     */     
/* 296 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Torrent removeAdditionalProperties()
/*     */   {
/* 302 */     notSupported();
/*     */     
/* 304 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPluginStringProperty(String name, String value)
/*     */   {
/* 312 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getPluginStringProperty(String name)
/*     */   {
/* 319 */     notSupported();
/*     */     
/* 321 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMapProperty(String name, Map value)
/*     */   {
/* 329 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getMapProperty(String name)
/*     */   {
/* 336 */     notSupported();
/*     */     
/* 338 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map writeToMap()
/*     */     throws TorrentException
/*     */   {
/* 346 */     notSupported();
/*     */     
/* 348 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] writeToBEncodedData()
/*     */     throws TorrentException
/*     */   {
/* 356 */     notSupported();
/*     */     
/* 358 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeToFile(File file)
/*     */     throws TorrentException
/*     */   {
/* 367 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public void save()
/*     */     throws TorrentException
/*     */   {
/* 374 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setComplete(File data_dir)
/*     */     throws TorrentException
/*     */   {
/* 383 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComplete()
/*     */   {
/* 389 */     notSupported();
/*     */     
/* 391 */     return false; }
/*     */   
/* 393 */   public boolean isSimpleTorrent() { notSupported();return false;
/*     */   }
/*     */   
/* 396 */   public Torrent getClone() throws TorrentException { notSupported();
/* 397 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/torrent/RPTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */