/*     */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStateFactory;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDeletionVetoException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceWillBeDeletedListener;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
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
/*     */ public abstract class ShareResourceFileOrDirImpl
/*     */   extends ShareResourceImpl
/*     */ {
/*     */   private final File file;
/*     */   private final byte[] personal_key;
/*     */   private final Map<String, String> properties;
/*     */   private ShareItemImpl item;
/*     */   
/*     */   protected static ShareResourceImpl getResourceSupport(ShareManagerImpl _manager, File _file)
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/*  62 */       return _manager.getResource(_file.getCanonicalFile());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  66 */       throw new ShareException("getCanonicalFile fails", e);
/*     */     }
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
/*     */   protected ShareResourceFileOrDirImpl(ShareManagerImpl _manager, ShareResourceDirContentsImpl _parent, int _type, File _file, boolean _personal, Map<String, String> _properties)
/*     */     throws ShareException
/*     */   {
/*  81 */     super(_manager, _type);
/*     */     
/*  83 */     this.properties = _properties;
/*     */     
/*  85 */     if (getType() == 1)
/*     */     {
/*  87 */       if (!_file.exists())
/*     */       {
/*  89 */         throw new ShareException("File '" + _file.getName() + "' not found");
/*     */       }
/*     */       
/*  92 */       if (!_file.isFile())
/*     */       {
/*  94 */         throw new ShareException("Not a file");
/*     */       }
/*     */     }
/*     */     else {
/*  98 */       if (!_file.exists())
/*     */       {
/* 100 */         throw new ShareException("Dir '" + _file.getName() + "' not found");
/*     */       }
/*     */       
/* 103 */       if (_file.isFile())
/*     */       {
/* 105 */         throw new ShareException("Not a directory");
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 110 */       this.file = _file.getCanonicalFile();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 114 */       throw new ShareException("ShareResourceFile: failed to get canonical name", e);
/*     */     }
/*     */     
/* 117 */     this.personal_key = (_personal ? RandomUtils.nextSecureHash() : null);
/*     */     
/* 119 */     if (_parent != null)
/*     */     {
/* 121 */       setParent(_parent);
/*     */       
/* 123 */       inheritAttributes(_parent);
/*     */     }
/*     */     
/* 126 */     createTorrent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ShareResourceFileOrDirImpl(ShareManagerImpl _manager, int _type, File _file, Map _map)
/*     */     throws ShareException
/*     */   {
/* 138 */     super(_manager, _type, _map);
/*     */     
/* 140 */     this.file = _file;
/*     */     
/* 142 */     this.personal_key = ((byte[])_map.get("per_key"));
/*     */     
/* 144 */     this.properties = BDecoder.decodeStrings((Map)_map.get("props"));
/*     */     
/* 146 */     this.item = ShareItemImpl.deserialiseItem(this, _map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canBeDeleted()
/*     */     throws ShareResourceDeletionVetoException
/*     */   {
/* 154 */     for (int i = 0; i < this.deletion_listeners.size(); i++)
/*     */     {
/* 156 */       ((ShareResourceWillBeDeletedListener)this.deletion_listeners.get(i)).resourceWillBeDeleted(this);
/*     */     }
/*     */     
/* 159 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract byte[] getFingerPrint()
/*     */     throws ShareException;
/*     */   
/*     */ 
/*     */   protected void createTorrent()
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/* 173 */       this.manager.reportCurrentTask((this.item == null ? "Creating" : "Re-creating").concat(" torrent for '").concat(this.file.toString()).concat("'"));
/*     */       
/* 175 */       URL[] urls = this.manager.getAnnounceURLs();
/*     */       
/* 177 */       TOTorrentCreator creator = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(this.file, urls[0], this.manager.getAddHashes());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 182 */       creator.addListener(this.manager);
/*     */       
/*     */       TOTorrent to_torrent;
/*     */       try
/*     */       {
/* 187 */         this.manager.setTorrentCreator(creator);
/*     */         
/* 189 */         to_torrent = creator.create();
/*     */       }
/*     */       finally
/*     */       {
/* 193 */         this.manager.setTorrentCreator(null);
/*     */       }
/*     */       
/* 196 */       if (this.personal_key != null)
/*     */       {
/* 198 */         Map map = to_torrent.serialiseToMap();
/*     */         
/* 200 */         Map info = (Map)map.get("info");
/*     */         
/* 202 */         info.put("az_salt", this.personal_key);
/*     */         
/* 204 */         to_torrent = TOTorrentFactory.deserialiseFromMap(map);
/*     */       }
/*     */       
/* 207 */       LocaleTorrentUtil.setDefaultTorrentEncoding(to_torrent);
/*     */       
/* 209 */       for (int i = 1; i < urls.length; i++)
/*     */       {
/* 211 */         TorrentUtils.announceGroupsInsertLast(to_torrent, new URL[] { urls[i] });
/*     */       }
/*     */       
/* 214 */       String comment = COConfigurationManager.getStringParameter("Sharing Torrent Comment").trim();
/*     */       
/* 216 */       boolean private_torrent = COConfigurationManager.getBooleanParameter("Sharing Torrent Private");
/*     */       
/* 218 */       boolean dht_backup_enabled = COConfigurationManager.getBooleanParameter("Sharing Permit DHT");
/*     */       
/* 220 */       TorrentAttribute ta_props = TorrentManagerImpl.getSingleton().getAttribute("ShareProperties");
/*     */       
/* 222 */       String props = getAttribute(ta_props);
/*     */       
/* 224 */       if (props != null)
/*     */       {
/* 226 */         StringTokenizer tok = new StringTokenizer(props, ";");
/*     */         
/* 228 */         while (tok.hasMoreTokens())
/*     */         {
/* 230 */           String token = tok.nextToken();
/*     */           
/* 232 */           int pos = token.indexOf('=');
/*     */           
/* 234 */           if (pos == -1)
/*     */           {
/* 236 */             Debug.out("ShareProperty invalid: " + props);
/*     */           }
/*     */           else
/*     */           {
/* 240 */             String lhs = token.substring(0, pos).trim().toLowerCase();
/* 241 */             String rhs = token.substring(pos + 1).trim().toLowerCase();
/*     */             
/* 243 */             boolean set = rhs.equals("true");
/*     */             
/* 245 */             if (lhs.equals("private"))
/*     */             {
/* 247 */               private_torrent = set;
/*     */             }
/* 249 */             else if (lhs.equals("dht_backup"))
/*     */             {
/* 251 */               dht_backup_enabled = set;
/*     */             }
/* 253 */             else if (lhs.equals("comment"))
/*     */             {
/* 255 */               comment = rhs;
/*     */             }
/*     */             else
/*     */             {
/* 259 */               Debug.out("ShareProperty invalid: " + props);
/*     */               
/* 261 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 267 */       if (comment.length() > 0)
/*     */       {
/* 269 */         to_torrent.setComment(comment);
/*     */       }
/*     */       
/* 272 */       TorrentUtils.setDHTBackupEnabled(to_torrent, dht_backup_enabled);
/*     */       
/* 274 */       TorrentUtils.setPrivate(to_torrent, private_torrent);
/*     */       
/* 276 */       if (TorrentUtils.isDecentralised(to_torrent))
/*     */       {
/* 278 */         TorrentUtils.setDecentralised(to_torrent);
/*     */       }
/*     */       
/* 281 */       DownloadManagerState download_manager_state = DownloadManagerStateFactory.getDownloadState(to_torrent);
/*     */       
/*     */ 
/* 284 */       TorrentUtils.setResumeDataCompletelyValid(download_manager_state);
/*     */       
/*     */ 
/* 287 */       download_manager_state.save();
/*     */       
/* 289 */       if (this.item == null)
/*     */       {
/* 291 */         byte[] fingerprint = getFingerPrint();
/*     */         
/* 293 */         this.item = new ShareItemImpl(this, fingerprint, new TorrentImpl(to_torrent));
/*     */       }
/*     */       else
/*     */       {
/* 297 */         this.item.setTorrent(new TorrentImpl(to_torrent));
/*     */         
/* 299 */         this.item.writeTorrent();
/*     */       }
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 304 */       if (e.getReason() == 9)
/*     */       {
/* 306 */         throw new ShareException("ShareResource: Operation cancelled", e);
/*     */       }
/*     */       
/*     */ 
/* 310 */       throw new ShareException("ShareResource: Torrent create failed", e);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 314 */       throw new ShareException("ShareResource: Torrent create failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkConsistency()
/*     */     throws ShareException
/*     */   {
/* 323 */     if (!isPersistent())
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/*     */ 
/* 330 */         if (Arrays.equals(getFingerPrint(), this.item.getFingerPrint()))
/*     */         {
/*     */ 
/*     */ 
/* 334 */           if (!this.manager.torrentExists(this.item))
/*     */           {
/* 336 */             createTorrent();
/*     */           }
/*     */         }
/*     */         else {
/* 340 */           this.manager.addFileOrDir(null, this.file, getType(), this.personal_key != null, this.properties);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 344 */         this.manager.delete(this, true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static ShareResourceImpl deserialiseResource(ShareManagerImpl manager, Map map, int type)
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/* 358 */       File file = new File(new String((byte[])map.get("file"), "UTF8"));
/*     */       
/* 360 */       if (type == 1)
/*     */       {
/* 362 */         return new ShareResourceFileImpl(manager, file, map);
/*     */       }
/*     */       
/* 365 */       return new ShareResourceDirImpl(manager, file, map);
/*     */ 
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 370 */       throw new ShareException("internal error", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serialiseResource(Map map)
/*     */   {
/* 378 */     super.serialiseResource(map);
/*     */     
/* 380 */     map.put("type", new Long(getType()));
/*     */     try
/*     */     {
/* 383 */       map.put("file", this.file.toString().getBytes("UTF8"));
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 387 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 390 */     if (this.personal_key != null)
/*     */     {
/* 392 */       map.put("per_key", this.personal_key);
/*     */     }
/*     */     
/* 395 */     if (this.properties != null)
/*     */     {
/* 397 */       map.put("props", this.properties);
/*     */     }
/*     */     
/* 400 */     this.item.serialiseItem(map);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void deleteInternal()
/*     */   {
/* 406 */     this.item.delete();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 412 */     return this.file.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 418 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */   public ShareItem getItem()
/*     */   {
/* 424 */     return this.item;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, String> getProperties()
/*     */   {
/* 430 */     return this.properties;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareResourceFileOrDirImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */