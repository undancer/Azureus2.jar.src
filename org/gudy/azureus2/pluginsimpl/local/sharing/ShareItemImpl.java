/*     */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
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
/*     */ 
/*     */ 
/*     */ public class ShareItemImpl
/*     */   implements ShareItem
/*     */ {
/*     */   protected ShareResourceImpl resource;
/*     */   protected byte[] fingerprint;
/*     */   protected Torrent torrent;
/*     */   protected String torrent_save_location;
/*     */   
/*     */   protected ShareItemImpl(ShareResourceImpl _resource, byte[] _fingerprint, Torrent _torrent)
/*     */     throws ShareException
/*     */   {
/*  55 */     this.resource = _resource;
/*  56 */     this.fingerprint = _fingerprint;
/*  57 */     this.torrent = _torrent;
/*     */     
/*  59 */     writeTorrent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ShareItemImpl(ShareResourceImpl _resource, byte[] _fingerprint, String _save_location)
/*     */     throws ShareException
/*     */   {
/*  70 */     this.resource = _resource;
/*  71 */     this.fingerprint = _fingerprint;
/*  72 */     this.torrent_save_location = _save_location;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Torrent getTorrent()
/*     */     throws ShareException
/*     */   {
/*  84 */     if (this.torrent == null)
/*     */     {
/*  86 */       this.resource.readTorrent(this);
/*     */     }
/*     */     
/*  89 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeTorrent()
/*     */     throws ShareException
/*     */   {
/*  97 */     if (this.torrent_save_location == null)
/*     */     {
/*  99 */       this.torrent_save_location = this.resource.getNewTorrentLocation();
/*     */     }
/*     */     
/* 102 */     this.resource.writeTorrent(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTorrent(Torrent _torrent)
/*     */   {
/* 109 */     this.torrent = _torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getTorrentFile()
/*     */   {
/* 115 */     return this.resource.getTorrentFile(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getTorrentLocation()
/*     */   {
/* 121 */     return this.torrent_save_location;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getFingerPrint()
/*     */   {
/* 127 */     return this.fingerprint;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void delete()
/*     */   {
/* 133 */     this.resource.deleteTorrent(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serialiseItem(Map map)
/*     */   {
/* 140 */     map.put("ihash", this.fingerprint);
/*     */     try
/*     */     {
/* 143 */       map.put("ifile", this.torrent_save_location.getBytes("UTF8"));
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 147 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static ShareItemImpl deserialiseItem(ShareResourceImpl resource, Map map)
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/* 159 */       byte[] hash = (byte[])map.get("ihash");
/*     */       
/* 161 */       String save_location = new String((byte[])map.get("ifile"), "UTF8");
/*     */       
/* 163 */       return new ShareItemImpl(resource, hash, save_location);
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 167 */       throw new ShareException("internal error", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */