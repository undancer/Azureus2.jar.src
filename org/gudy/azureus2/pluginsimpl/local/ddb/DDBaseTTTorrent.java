/*     */ package org.gudy.azureus2.pluginsimpl.local.ddb;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginProgressListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseProgressListener;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferHandler;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferType;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseValue;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.utils.Formatters;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class DDBaseTTTorrent
/*     */   implements DistributedDatabaseTransferType, DistributedDatabaseTransferHandler
/*     */ {
/*     */   private static final boolean TRACE = false;
/*     */   private static final byte CRYPTO_VERSION = 1;
/*     */   private DDBaseImpl ddb;
/*     */   private TorrentAttribute ta_sha1;
/*     */   private boolean crypto_tested;
/*     */   private boolean crypto_available;
/*     */   private List external_downloads;
/*  72 */   private Map data_cache = new LinkedHashMap(5, 0.75F, true)
/*     */   {
/*     */ 
/*     */ 
/*     */     protected boolean removeEldestEntry(Map.Entry eldest)
/*     */     {
/*     */ 
/*  79 */       return size() > 5;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */   protected DDBaseTTTorrent(DDBaseImpl _ddb)
/*     */   {
/*  87 */     this.ddb = _ddb;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addDownload(Download download)
/*     */   {
/*  94 */     synchronized (this)
/*     */     {
/*  96 */       if (this.external_downloads == null)
/*     */       {
/*  98 */         this.external_downloads = new ArrayList();
/*     */       }
/*     */       
/* 101 */       this.external_downloads.add(download);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDownload(Download download)
/*     */   {
/* 109 */     synchronized (this)
/*     */     {
/* 111 */       if (this.external_downloads != null)
/*     */       {
/* 113 */         this.external_downloads.remove(download);
/*     */         
/* 115 */         if (this.external_downloads.size() == 0)
/*     */         {
/* 117 */           this.external_downloads = null;
/*     */         }
/*     */       }
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
/*     */ 
/*     */ 
/*     */   public DistributedDatabaseValue read(DistributedDatabaseContact contact, DistributedDatabaseTransferType type, DistributedDatabaseKey key)
/*     */     throws DistributedDatabaseException
/*     */   {
/*     */     try
/*     */     {
/* 138 */       byte[] search_key = ((DDBaseKeyImpl)key).getBytes();
/*     */       
/* 140 */       Download download = null;
/*     */       
/* 142 */       PluginInterface pi = PluginInitializer.getDefaultInterface();
/*     */       
/* 144 */       String search_sha1 = pi.getUtilities().getFormatters().encodeBytesToString(search_key);
/*     */       
/* 146 */       if (this.ta_sha1 == null)
/*     */       {
/* 148 */         this.ta_sha1 = pi.getTorrentManager().getPluginAttribute("DDBaseTTTorrent::sha1");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 153 */       Download[] downloads = pi.getDownloadManager().getDownloads();
/*     */       
/* 155 */       for (int i = 0; i < downloads.length; i++)
/*     */       {
/* 157 */         Download dl = downloads[i];
/*     */         
/* 159 */         if (dl.getTorrent() != null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 164 */           String sha1 = dl.getAttribute(this.ta_sha1);
/*     */           
/* 166 */           if (sha1 == null)
/*     */           {
/* 168 */             sha1 = pi.getUtilities().getFormatters().encodeBytesToString(new SHA1Simple().calculateHash(dl.getTorrent().getHash()));
/*     */             
/*     */ 
/* 171 */             dl.setAttribute(this.ta_sha1, sha1);
/*     */           }
/*     */           
/* 174 */           if (sha1.equals(search_sha1))
/*     */           {
/* 176 */             download = dl;
/*     */             
/* 178 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 182 */       if (download == null)
/*     */       {
/* 184 */         synchronized (this)
/*     */         {
/* 186 */           if (this.external_downloads != null)
/*     */           {
/* 188 */             for (int i = 0; i < this.external_downloads.size(); i++)
/*     */             {
/* 190 */               Download dl = (Download)this.external_downloads.get(i);
/*     */               
/* 192 */               if (dl.getTorrent() != null)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 197 */                 String sha1 = dl.getAttribute(this.ta_sha1);
/*     */                 
/* 199 */                 if (sha1 == null)
/*     */                 {
/* 201 */                   sha1 = pi.getUtilities().getFormatters().encodeBytesToString(new SHA1Simple().calculateHash(dl.getTorrent().getHash()));
/*     */                   
/*     */ 
/* 204 */                   dl.setAttribute(this.ta_sha1, sha1);
/*     */                 }
/*     */                 
/* 207 */                 if (sha1.equals(search_sha1))
/*     */                 {
/* 209 */                   download = dl;
/*     */                   
/* 211 */                   break;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 218 */       String originator = contact.getName();
/*     */       
/* 220 */       if (download == null)
/*     */       {
/* 222 */         String msg = "TorrentDownload: request from " + originator + " for '" + pi.getUtilities().getFormatters().encodeBytesToString(search_key) + "' not found";
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 229 */         this.ddb.log(msg);
/*     */         
/*     */ 
/*     */ 
/* 233 */         return null;
/*     */       }
/*     */       
/*     */ 
/* 237 */       Torrent torrent = download.getTorrent();
/*     */       
/* 239 */       if (torrent.isPrivate())
/*     */       {
/* 241 */         Debug.out("Attempt to download private torrent");
/*     */         
/* 243 */         this.ddb.log("TorrentDownload: request from " + originator + "  for '" + download.getName() + "' denied as it is private");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 248 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 256 */         org.gudy.azureus2.core3.download.DownloadManager dm = PluginCoreUtils.unwrapIfPossible(download);
/*     */         
/* 258 */         if ((dm != null) && (!dm.getDownloadState().isPeerSourceEnabled("DHT")))
/*     */         {
/* 260 */           this.ddb.log("TorrentDownload: request from " + originator + "  for '" + download.getName() + "' denied as DHT peer source disabled");
/*     */           
/* 262 */           return null;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 266 */         Debug.out(e);
/*     */       }
/*     */       
/* 269 */       String msg = "TorrentDownload: request from " + originator + "  for '" + download.getName() + "' OK";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 276 */       this.ddb.log(msg);
/*     */       
/* 278 */       HashWrapper hw = new HashWrapper(torrent.getHash());
/*     */       
/* 280 */       synchronized (this.data_cache)
/*     */       {
/* 282 */         Object[] data = (Object[])this.data_cache.get(hw);
/*     */         
/* 284 */         if (data != null)
/*     */         {
/* 286 */           data[1] = new Long(SystemTime.getCurrentTime());
/*     */           
/* 288 */           return this.ddb.createValue((byte[])data[0]);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 293 */       torrent = torrent.removeAdditionalProperties();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 298 */       torrent.setDecentralisedBackupRequested(true);
/*     */       
/* 300 */       byte[] data = torrent.writeToBEncodedData();
/*     */       
/* 302 */       data = encrypt(torrent.getHash(), data);
/*     */       
/* 304 */       if (data == null)
/*     */       {
/* 306 */         return null;
/*     */       }
/*     */       
/* 309 */       synchronized (this.data_cache)
/*     */       {
/* 311 */         if (this.data_cache.size() == 0)
/*     */         {
/* 313 */           final TimerEventPeriodic[] pe = { null };
/*     */           
/* 315 */           pe[0 = SimpleTimer.addPeriodicEvent("DDBTorrent:timeout", 30000L, new TimerEventPerformer()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public void perform(TimerEvent event)
/*     */             {
/*     */ 
/*     */ 
/* 324 */               long now = SystemTime.getCurrentTime();
/*     */               
/* 326 */               synchronized (DDBaseTTTorrent.this.data_cache)
/*     */               {
/* 328 */                 Iterator it = DDBaseTTTorrent.this.data_cache.values().iterator();
/*     */                 
/* 330 */                 while (it.hasNext())
/*     */                 {
/* 332 */                   long time = ((Long)((Object[])(Object[])it.next())[1]).longValue();
/*     */                   
/* 334 */                   if ((now < time) || (now - time > 120000L))
/*     */                   {
/* 336 */                     it.remove();
/*     */                   }
/*     */                 }
/*     */                 
/* 340 */                 if (DDBaseTTTorrent.this.data_cache.size() == 0)
/*     */                 {
/* 342 */                   pe[0].cancel();
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/* 349 */         this.data_cache.put(hw, new Object[] { data, new Long(SystemTime.getCurrentTime()) });
/*     */       }
/*     */       
/* 352 */       return this.ddb.createValue(data);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 356 */       throw new DistributedDatabaseException("Torrent write fails", e);
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
/*     */   public DistributedDatabaseValue write(DistributedDatabaseContact contact, DistributedDatabaseTransferType type, DistributedDatabaseKey key, DistributedDatabaseValue value)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 371 */     throw new DistributedDatabaseException("not supported");
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
/*     */   protected DistributedDatabaseValue read(DDBaseContactImpl contact, final DistributedDatabaseProgressListener listener, DistributedDatabaseTransferType type, DistributedDatabaseKey key, long timeout)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 386 */     byte[] torrent_hash = ((DDBaseKeyImpl)key).getBytes();
/*     */     
/* 388 */     byte[] lookup_key = new SHA1Simple().calculateHash(torrent_hash);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 394 */     byte[] data = contact.getContact().read(listener == null ? null : new DHTPluginProgressListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void reportSize(long size)
/*     */       {
/*     */ 
/* 401 */         listener.reportSize(size);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void reportActivity(String str)
/*     */       {
/* 408 */         listener.reportActivity(str);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 415 */       public void reportCompleteness(int percent) { listener.reportCompleteness(percent); } }, DDBaseHelpers.getKey(type.getClass()).getHash(), lookup_key, timeout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 422 */     if (data == null)
/*     */     {
/* 424 */       return null;
/*     */     }
/*     */     
/* 427 */     data = decrypt(torrent_hash, data);
/*     */     
/* 429 */     if (data == null)
/*     */     {
/* 431 */       return null;
/*     */     }
/*     */     
/* 434 */     return new DDBaseValueImpl(contact, data, SystemTime.getCurrentTime(), -1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] encrypt(byte[] hash, byte[] data)
/*     */   {
/* 442 */     if (!testCrypto())
/*     */     {
/* 444 */       return null;
/*     */     }
/*     */     
/* 447 */     byte[] enc = doCrypt(1, hash, data, 0);
/*     */     
/* 449 */     if (enc == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 456 */       byte[] res = new byte[data.length + 2];
/*     */       
/* 458 */       res[0] = 1;
/* 459 */       res[1] = 0;
/*     */       
/* 461 */       System.arraycopy(data, 0, res, 2, data.length);
/*     */       
/* 463 */       return res;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 472 */     byte[] res = new byte[enc.length + 2];
/*     */     
/* 474 */     res[0] = 1;
/* 475 */     res[1] = 1;
/*     */     
/* 477 */     System.arraycopy(enc, 0, res, 2, enc.length);
/*     */     
/* 479 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] decrypt(byte[] hash, byte[] data)
/*     */   {
/* 488 */     if (!testCrypto())
/*     */     {
/* 490 */       return null;
/*     */     }
/*     */     
/* 493 */     if (data[0] != 1)
/*     */     {
/* 495 */       Debug.out("Invalid crypto version received");
/*     */       
/* 497 */       return data;
/*     */     }
/*     */     
/* 500 */     if (data[1] == 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 508 */       byte[] res = new byte[data.length - 2];
/*     */       
/* 510 */       System.arraycopy(data, 2, res, 0, res.length);
/*     */       
/* 512 */       return res;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 520 */     byte[] res = doCrypt(2, hash, data, 2);
/*     */     
/* 522 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] doCrypt(int mode, byte[] hash, byte[] data, int data_offset)
/*     */   {
/*     */     try
/*     */     {
/* 534 */       byte[] key_data = new byte[24];
/*     */       
/*     */ 
/*     */ 
/* 538 */       System.arraycopy(hash, 0, key_data, 0, hash.length);
/*     */       
/* 540 */       SecretKey tdes_key = new SecretKeySpec(key_data, "DESede");
/*     */       
/* 542 */       Cipher cipher = Cipher.getInstance("DESede");
/*     */       
/* 544 */       cipher.init(mode, tdes_key);
/*     */       
/* 546 */       return cipher.doFinal(data, data_offset, data.length - data_offset);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 550 */       Debug.out(e);
/*     */     }
/* 552 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean testCrypto()
/*     */   {
/* 559 */     if (!this.crypto_tested)
/*     */     {
/* 561 */       this.crypto_tested = true;
/*     */       try
/*     */       {
/* 564 */         Cipher.getInstance("DESede");
/*     */         
/* 566 */         this.crypto_available = true;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 570 */         Logger.log(new LogAlert(false, "Unable to initialise cryptographic framework for magnet-based torrent downloads, please re-install Java", e));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 576 */     return this.crypto_available;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ddb/DDBaseTTTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */