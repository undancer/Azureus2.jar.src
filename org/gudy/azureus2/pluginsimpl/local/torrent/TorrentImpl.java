/*     */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.plugins.magnet.MagnetPlugin;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStateFactory;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilEncodingException;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLList;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentEncodingException;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentFile;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
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
/*     */ public class TorrentImpl
/*     */   extends LogRelation
/*     */   implements Torrent
/*     */ {
/*     */   private static MagnetPlugin magnet_plugin;
/*     */   private PluginInterface pi;
/*     */   private TOTorrent torrent;
/*     */   private LocaleUtilDecoder decoder;
/*     */   private boolean complete;
/*     */   
/*     */   public TorrentImpl(TOTorrent _torrent)
/*     */   {
/*  61 */     this(null, _torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentImpl(PluginInterface _pi, TOTorrent _torrent)
/*     */   {
/*  69 */     this.pi = _pi;
/*  70 */     this.torrent = _torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  76 */     String utf8Name = this.torrent.getUTF8Name();
/*  77 */     String name = utf8Name == null ? decode(this.torrent.getName()) : utf8Name;
/*     */     
/*  79 */     name = FileUtil.convertOSSpecificChars(name, false);
/*     */     
/*  81 */     return name;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getAnnounceURL()
/*     */   {
/*  87 */     return this.torrent.getAnnounceURL();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAnnounceURL(URL url)
/*     */   {
/*  94 */     this.torrent.setAnnounceURL(url);
/*     */     
/*  96 */     updated();
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentAnnounceURLList getAnnounceURLList()
/*     */   {
/* 102 */     return new TorrentAnnounceURLListImpl(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 108 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDecentralised()
/*     */   {
/* 114 */     return TorrentUtils.isDecentralised(this.torrent);
/*     */   }
/*     */   
/*     */   public boolean isDecentralisedBackupEnabled()
/*     */   {
/* 119 */     return TorrentUtils.getDHTBackupEnabled(this.torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDecentralisedBackupRequested(boolean requested)
/*     */   {
/* 126 */     TorrentUtils.setDHTBackupRequested(this.torrent, requested);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDecentralisedBackupRequested()
/*     */   {
/* 132 */     return TorrentUtils.isDHTBackupRequested(this.torrent);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPrivate()
/*     */   {
/* 138 */     return TorrentUtils.getPrivate(this.torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPrivate(boolean priv)
/*     */   {
/* 145 */     TorrentUtils.setPrivate(this.torrent, priv);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean wasCreatedByUs()
/*     */   {
/* 151 */     return TorrentUtils.isCreatedTorrent(this.torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URL getMagnetURI()
/*     */     throws TorrentException
/*     */   {
/* 159 */     if (magnet_plugin == null)
/*     */     {
/* 161 */       PluginInterface magnet_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(MagnetPlugin.class);
/*     */       
/* 163 */       if (magnet_pi != null)
/*     */       {
/* 165 */         magnet_plugin = (MagnetPlugin)magnet_pi.getPlugin();
/*     */       }
/*     */     }
/*     */     
/* 169 */     if (magnet_plugin == null)
/*     */     {
/* 171 */       throw new TorrentException("MegnetPlugin unavailable");
/*     */     }
/*     */     try
/*     */     {
/* 175 */       return magnet_plugin.getMagnetURL(this.torrent.getHash());
/*     */ 
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/*     */ 
/* 181 */       throw new TorrentException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] getHash()
/*     */   {
/*     */     try
/*     */     {
/* 189 */       return this.torrent.getHash();
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 193 */       Debug.printStackTrace(e);
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 202 */     return this.torrent.getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getComment()
/*     */   {
/* 208 */     return decode(this.torrent.getComment());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setComment(String comment)
/*     */   {
/* 215 */     this.torrent.setComment(comment);
/*     */   }
/*     */   
/*     */   public long getCreationDate()
/*     */   {
/* 220 */     return this.torrent.getCreationDate();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCreatedBy()
/*     */   {
/* 226 */     return decode(this.torrent.getCreatedBy());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getPieceSize()
/*     */   {
/* 233 */     return this.torrent.getPieceLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceCount()
/*     */   {
/* 239 */     return this.torrent.getNumberOfPieces();
/*     */   }
/*     */   
/*     */   public byte[][] getPieces()
/*     */   {
/*     */     try
/*     */     {
/* 246 */       return this.torrent.getPieces();
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 250 */       Debug.printStackTrace(e);
/*     */     }
/* 252 */     return new byte[0][0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TorrentFile[] getFiles()
/*     */   {
/* 259 */     TOTorrentFile[] files = this.torrent.getFiles();
/*     */     
/* 261 */     TorrentFile[] res = new TorrentFile[files.length];
/*     */     
/* 263 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 265 */       TOTorrentFile tf = files[i];
/*     */       
/* 267 */       byte[][] comps = tf.getPathComponents();
/*     */       
/* 269 */       String name = "";
/*     */       
/* 271 */       for (int j = 0; j < comps.length; j++)
/*     */       {
/* 273 */         String comp = decode(comps[j]);
/*     */         
/* 275 */         comp = FileUtil.convertOSSpecificChars(comp, j != comps.length - 1);
/*     */         
/* 277 */         name = name + (j == 0 ? "" : File.separator) + comp;
/*     */       }
/*     */       
/* 280 */       res[i] = new TorrentFileImpl(name, tf.getLength());
/*     */     }
/*     */     
/* 283 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void getDecoder()
/*     */   {
/*     */     try
/*     */     {
/* 295 */       this.decoder = LocaleTorrentUtil.getTorrentEncoding(this.torrent);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getEncoding()
/*     */   {
/* 305 */     getDecoder();
/*     */     
/* 307 */     if (this.decoder != null)
/*     */     {
/* 309 */       return this.decoder.getName();
/*     */     }
/*     */     
/* 312 */     return "UTF8";
/*     */   }
/*     */   
/*     */   public void setEncoding(String encoding) throws TorrentEncodingException
/*     */   {
/*     */     try {
/* 318 */       LocaleTorrentUtil.setTorrentEncoding(this.torrent, encoding);
/*     */     } catch (LocaleUtilEncodingException e) {
/* 320 */       throw new TorrentEncodingException("Failed to set the encoding", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDefaultEncoding() throws TorrentEncodingException
/*     */   {
/* 326 */     setEncoding("UTF8");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String decode(byte[] data)
/*     */   {
/* 333 */     getDecoder();
/*     */     
/* 335 */     if (data != null)
/*     */     {
/* 337 */       if (this.decoder != null) {
/*     */         try
/*     */         {
/* 340 */           return this.decoder.decodeString(data);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/* 346 */       return new String(data);
/*     */     }
/*     */     
/* 349 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getAdditionalProperty(String name)
/*     */   {
/* 356 */     return this.torrent.getAdditionalProperty(name);
/*     */   }
/*     */   
/*     */   public Torrent removeAdditionalProperties()
/*     */   {
/*     */     try
/*     */     {
/* 363 */       TOTorrent t = TOTorrentFactory.deserialiseFromMap(this.torrent.serialiseToMap());
/*     */       
/* 365 */       t.removeAdditionalProperties();
/*     */       
/* 367 */       return new TorrentImpl(t);
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 371 */       Debug.printStackTrace(e);
/*     */     }
/* 373 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPluginStringProperty(String name, String value)
/*     */   {
/* 382 */     PluginInterface p = this.pi;
/*     */     
/* 384 */     if (p == null)
/*     */     {
/* 386 */       p = UtilitiesImpl.getPluginThreadContext();
/*     */     }
/*     */     
/* 389 */     if (p == null)
/*     */     {
/* 391 */       name = "<internal>." + name;
/*     */     }
/*     */     else
/*     */     {
/* 395 */       name = p.getPluginID() + "." + name;
/*     */     }
/*     */     
/* 398 */     TorrentUtils.setPluginStringProperty(this.torrent, name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getPluginStringProperty(String name)
/*     */   {
/* 405 */     PluginInterface p = this.pi;
/*     */     
/* 407 */     if (p == null)
/*     */     {
/* 409 */       p = UtilitiesImpl.getPluginThreadContext();
/*     */     }
/*     */     
/* 412 */     if (p == null)
/*     */     {
/* 414 */       name = "<internal>." + name;
/*     */     }
/*     */     else
/*     */     {
/* 418 */       name = p.getPluginID() + "." + name;
/*     */     }
/*     */     
/* 421 */     return TorrentUtils.getPluginStringProperty(this.torrent, name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMapProperty(String name, Map value)
/*     */   {
/* 429 */     TorrentUtils.setPluginMapProperty(this.torrent, name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getMapProperty(String name)
/*     */   {
/* 436 */     return TorrentUtils.getPluginMapProperty(this.torrent, name);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map writeToMap()
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 445 */       return this.torrent.serialiseToMap();
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 449 */       throw new TorrentException("Torrent::writeToMap: fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] writeToBEncodedData()
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 459 */       Map map = this.torrent.serialiseToMap();
/*     */       
/* 461 */       return BEncoder.encode(map);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 465 */       throw new TorrentException("Torrent::writeToBEncodedData: fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeToFile(File file)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 480 */       this.torrent.serialiseToBEncodedFile(file);
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 484 */       throw new TorrentException("Torrent::writeToFile: fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void updated()
/*     */   {
/*     */     try
/*     */     {
/* 492 */       DownloadImpl dm = (DownloadImpl)DownloadManagerImpl.getDownloadStatic(this.torrent);
/*     */       
/* 494 */       if (dm != null)
/*     */       {
/* 496 */         dm.torrentChanged();
/*     */       }
/*     */     }
/*     */     catch (DownloadException e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void save()
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 509 */       TorrentUtils.writeToFile(this.torrent);
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 513 */       throw new TorrentException("Torrent::save Fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setComplete(File data_dir)
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 524 */       LocaleTorrentUtil.setDefaultTorrentEncoding(this.torrent);
/*     */       
/* 526 */       DownloadManagerState download_manager_state = DownloadManagerStateFactory.getDownloadState(this.torrent);
/*     */       
/*     */ 
/* 529 */       TorrentUtils.setResumeDataCompletelyValid(download_manager_state);
/*     */       
/* 531 */       download_manager_state.save();
/*     */       
/* 533 */       this.complete = true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 537 */       throw new TorrentException("encoding selection fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isComplete()
/*     */   {
/* 546 */     return this.complete;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRelationText()
/*     */   {
/* 556 */     return propogatedRelationText(this.torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object[] getQueryableInterfaces()
/*     */   {
/* 563 */     return new Object[] { this.torrent };
/*     */   }
/*     */   
/* 566 */   public boolean isSimpleTorrent() { return this.torrent.isSimpleTorrent(); }
/*     */   
/*     */ 
/*     */   public Torrent getClone()
/*     */     throws TorrentException
/*     */   {
/*     */     try
/*     */     {
/* 574 */       return new TorrentImpl(TOTorrentFactory.deserialiseFromMap(this.torrent.serialiseToMap()));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 578 */       throw new TorrentException("Cloning fails", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */