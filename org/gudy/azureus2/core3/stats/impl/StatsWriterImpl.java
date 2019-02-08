/*     */ package org.gudy.azureus2.core3.stats.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.peermanager.utils.PeerClassifier;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
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
/*     */ public class StatsWriterImpl
/*     */   extends XUXmlWriter
/*     */ {
/*     */   final AzureusCore core;
/*     */   
/*     */   protected StatsWriterImpl(AzureusCore _core)
/*     */   {
/*  56 */     this.core = _core;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void write(String file_name)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  66 */       setOutputStream(new FileOutputStream(file_name));
/*     */       
/*  68 */       writeSupport();
/*     */     }
/*     */     finally {
/*  71 */       closeOutputStream();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void write(OutputStream os)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  82 */       setOutputStream(os);
/*     */       
/*  84 */       writeSupport();
/*     */     }
/*     */     finally {
/*  87 */       flushOutputStream();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void writeSupport()
/*     */   {
/*  94 */     writeLineRaw("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/*     */     
/*  96 */     boolean export_peer_stats = COConfigurationManager.getBooleanParameter("Stats Export Peer Details");
/*  97 */     boolean export_file_stats = COConfigurationManager.getBooleanParameter("Stats Export File Details");
/*     */     
/*  99 */     String xsl = COConfigurationManager.getStringParameter("Stats XSL File");
/*     */     
/* 101 */     if (xsl.length() > 0)
/*     */     {
/* 103 */       writeLineRaw("<?xml-stylesheet type=\"text/xsl\" href=\"" + xsl + "\"?>");
/*     */     }
/*     */     
/* 106 */     writeLineRaw("<STATS>");
/*     */     
/* 108 */     GlobalManager global = this.core.getGlobalManager();
/*     */     try
/*     */     {
/* 111 */       indent();
/*     */       
/* 113 */       writeTag("AZUREUS_VERSION", "5.7.6.0");
/*     */       
/* 115 */       writeLineRaw("<GLOBAL>");
/*     */       try
/*     */       {
/* 118 */         indent();
/*     */         
/* 120 */         GlobalManagerStats gm_stats = global.getStats();
/*     */         
/* 122 */         writeRawCookedAverageTag("DOWNLOAD_SPEED", gm_stats.getDataReceiveRate() + gm_stats.getProtocolReceiveRate());
/* 123 */         writeRawCookedAverageTag("UPLOAD_SPEED", gm_stats.getDataSendRate() + gm_stats.getProtocolSendRate());
/*     */       }
/*     */       finally {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 130 */       writeLineRaw("</GLOBAL>");
/*     */       
/* 132 */       writeLineRaw("<DOWNLOADS>");
/*     */       
/*     */       try
/*     */       {
/* 136 */         indent();
/*     */         
/* 138 */         List _dms = global.getDownloadManagers();
/*     */         
/* 140 */         DownloadManager[] dms = new DownloadManager[_dms.size()];
/*     */         
/*     */ 
/*     */ 
/* 144 */         _dms.toArray(dms);
/*     */         
/* 146 */         Arrays.sort(dms, new Comparator()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public int compare(Object o1, Object o2)
/*     */           {
/*     */ 
/*     */ 
/* 155 */             DownloadManager d1 = (DownloadManager)o1;
/* 156 */             DownloadManager d2 = (DownloadManager)o2;
/*     */             
/* 158 */             int d1_index = d1.getPosition();
/* 159 */             int d2_index = d2.getPosition();
/*     */             
/* 161 */             if (d1.isDownloadComplete(false))
/*     */             {
/* 163 */               d1_index += 1000000;
/*     */             }
/*     */             
/* 166 */             if (d2.isDownloadComplete(false))
/*     */             {
/* 168 */               d2_index += 1000000;
/*     */             }
/*     */             
/* 171 */             return d1_index - d2_index;
/*     */           }
/*     */         });
/*     */         
/* 175 */         for (int i = 0; i < dms.length; i++)
/*     */         {
/* 177 */           DownloadManager dm = dms[i];
/*     */           
/* 179 */           DownloadManagerStats dm_stats = dm.getStats();
/*     */           
/* 181 */           writeLineRaw("<DOWNLOAD>");
/*     */           try
/*     */           {
/* 184 */             indent();
/*     */             
/* 186 */             writeLineRaw("<TORRENT>");
/*     */             
/*     */ 
/*     */ 
/* 190 */             TOTorrent torrent = dm.getTorrent();
/*     */             try
/*     */             {
/* 193 */               indent();
/*     */               
/* 195 */               writeTag("NAME", dm.getDisplayName());
/*     */               
/* 197 */               writeTag("TORRENT_FILE", dm.getTorrentFileName());
/*     */               
/* 199 */               if (torrent != null)
/*     */               {
/* 201 */                 writeTag("HASH", TorrentUtils.nicePrintTorrentHash(torrent, true));
/*     */                 
/* 203 */                 writeRawCookedTag("SIZE", torrent.getSize());
/*     */                 
/* 205 */                 writeTag("PIECE_LENGTH", torrent.getPieceLength());
/*     */                 
/* 207 */                 writeTag("PIECE_COUNT", torrent.getNumberOfPieces());
/*     */                 
/* 209 */                 writeTag("FILE_COUNT", torrent.getFiles().length);
/*     */                 
/* 211 */                 writeTag("COMMENT", dm.getTorrentComment());
/*     */                 
/* 213 */                 writeTag("CREATED_BY", dm.getTorrentCreatedBy());
/*     */                 
/* 215 */                 writeTag("CREATION_DATE", torrent.getCreationDate());
/*     */               }
/*     */             }
/*     */             finally {}
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 223 */             writeLineRaw("</TORRENT>");
/*     */             
/* 225 */             writeTag("DOWNLOAD_STATUS", DisplayFormatters.formatDownloadStatusDefaultLocale(dm));
/*     */             
/* 227 */             writeTag("DOWNLOAD_DIR", dm.getSaveLocation().toString());
/*     */             
/* 229 */             if (torrent != null)
/*     */             {
/* 231 */               if (torrent.isSimpleTorrent())
/*     */               {
/* 233 */                 writeTag("TARGET_FILE", dm.getSaveLocation().toString());
/*     */               }
/*     */               else
/*     */               {
/* 237 */                 writeTag("TARGET_DIR", dm.getSaveLocation().toString());
/*     */               }
/*     */             }
/*     */             
/* 241 */             writeTag("TRACKER_STATUS", dm.getTrackerStatus());
/*     */             
/* 243 */             writeTag("COMPLETED", dm_stats.getCompleted());
/* 244 */             writeTag("NON_DND_COMPLETED", dm.isDownloadComplete(false));
/*     */             
/* 246 */             writeRawCookedTag("DOWNLOADED", dm_stats.getTotalDataBytesReceived());
/* 247 */             writeRawCookedTag("UPLOADED", dm_stats.getTotalDataBytesSent());
/* 248 */             writeRawCookedTag("DISCARDED", dm_stats.getDiscarded());
/*     */             
/* 250 */             writeRawCookedAverageTag("DOWNLOAD_SPEED", dm_stats.getDataReceiveRate());
/* 251 */             writeRawCookedAverageTag("UPLOAD_SPEED", dm_stats.getDataSendRate());
/* 252 */             writeRawCookedAverageTag("TOTAL_SPEED", dm_stats.getTotalAverage());
/*     */             
/* 254 */             writeRawCookedAverageTag("DOWNLOAD_SPEED_SMOOTH", dm_stats.getSmoothedDataReceiveRate());
/* 255 */             writeRawCookedAverageTag("UPLOAD_SPEED_SMOOTH", dm_stats.getSmoothedDataSendRate());
/*     */             
/* 257 */             writeTag("ELAPSED", dm_stats.getElapsedTime());
/* 258 */             writeTag("ETA", DisplayFormatters.formatETA(dm_stats.getSmoothedETA()));
/* 259 */             writeTag("HASH_FAILS", dm_stats.getHashFailCount());
/* 260 */             writeTag("SHARE_RATIO", dm_stats.getShareRatio());
/*     */             
/* 262 */             writeTag("TOTAL_SEEDS", dm.getNbSeeds());
/* 263 */             writeTag("TOTAL_LEECHERS", dm.getNbPeers());
/*     */             
/* 265 */             if (export_file_stats) {
/*     */               try
/*     */               {
/* 268 */                 writeLineRaw("<FILES>");
/*     */                 
/* 270 */                 indent();
/*     */                 
/* 272 */                 DiskManagerFileInfo[] files = dm.getDiskManagerFileInfo();
/*     */                 
/* 274 */                 for (int j = 0; j < files.length; j++)
/*     */                 {
/* 276 */                   DiskManagerFileInfo file = files[j];
/*     */                   try
/*     */                   {
/* 279 */                     writeLineRaw("<FILE>");
/*     */                     
/* 281 */                     indent();
/*     */                     
/* 283 */                     writeTag("NAME", file.getTorrentFile().getRelativePath());
/*     */                     
/* 285 */                     writeTag("DND", file.isSkipped());
/*     */                     
/* 287 */                     writeRawCookedTag("SIZE", file.getLength());
/*     */                     
/* 289 */                     writeRawCookedTag("DOWNLOADED", file.getDownloaded());
/*     */ 
/*     */ 
/*     */                   }
/*     */                   finally {}
/*     */                 }
/*     */                 
/*     */ 
/*     */               }
/*     */               finally
/*     */               {
/*     */ 
/* 301 */                 exdent();
/*     */                 
/* 303 */                 writeLineRaw("</FILES>");
/*     */               }
/*     */             }
/* 306 */             if (export_peer_stats) {
/*     */               try
/*     */               {
/* 309 */                 writeLineRaw("<PEERS>");
/*     */                 
/* 311 */                 indent();
/*     */                 
/* 313 */                 PEPeerManager pm = dm.getPeerManager();
/*     */                 
/* 315 */                 if (pm != null)
/*     */                 {
/* 317 */                   List peers = pm.getPeers();
/*     */                   
/* 319 */                   for (int j = 0; j < peers.size(); j++)
/*     */                   {
/* 321 */                     PEPeer peer = (PEPeer)peers.get(j);
/*     */                     
/* 323 */                     PEPeerStats peer_stats = peer.getStats();
/*     */                     
/* 325 */                     byte[] id = peer.getId();
/*     */                     
/* 327 */                     if (id != null)
/*     */                     {
/*     */ 
/*     */                       try
/*     */                       {
/*     */ 
/* 333 */                         String peer_id = PeerClassifier.getPrintablePeerID(id);
/*     */                         
/* 335 */                         peer_id = escapeXML(peer_id);
/*     */                         
/* 337 */                         String type = escapeXML(peer.getClient());
/*     */                         
/* 339 */                         writeLineRaw("<PEER hex_id=\"" + ByteFormatter.encodeString(id) + "\" printable_id=\"" + peer_id + "\" type=\"" + type + "\">");
/*     */                         
/* 341 */                         indent();
/*     */                         
/* 343 */                         writeTag("IP", peer.getIp());
/*     */                         
/* 345 */                         writeTag("IS_SEED", peer.isSeed());
/*     */                         
/* 347 */                         writeRawCookedTag("DOWNLOADED", peer_stats.getTotalDataBytesReceived());
/* 348 */                         writeRawCookedTag("UPLOADED", peer_stats.getTotalDataBytesSent());
/*     */                         
/* 350 */                         writeRawCookedAverageTag("DOWNLOAD_SPEED", peer_stats.getDataReceiveRate());
/* 351 */                         writeRawCookedAverageTag("UPLOAD_SPEED", peer_stats.getDataSendRate());
/*     */                       }
/*     */                       catch (Throwable e)
/*     */                       {
/* 355 */                         Debug.printStackTrace(e);
/*     */ 
/*     */                       }
/*     */                       finally {}
/*     */                     }
/*     */                     
/*     */                   }
/*     */                   
/*     */                 }
/*     */               }
/*     */               finally
/*     */               {
/* 367 */                 exdent();
/*     */                 
/* 369 */                 writeLineRaw("</PEERS>");
/*     */               }
/*     */             }
/*     */           }
/*     */           finally {}
/*     */           
/*     */ 
/*     */ 
/* 377 */           writeLineRaw("</DOWNLOAD>");
/*     */         }
/*     */       }
/*     */       finally {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 385 */       writeLineRaw("</DOWNLOADS>");
/*     */     }
/*     */     finally
/*     */     {
/* 389 */       exdent();
/*     */     }
/* 391 */     writeLineRaw("</STATS>");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeRawCookedTag(String tag, long raw)
/*     */   {
/* 400 */     writeLineRaw("<" + tag + ">");
/*     */     try
/*     */     {
/* 403 */       indent();
/*     */       
/* 405 */       writeTag("TEXT", DisplayFormatters.formatByteCountToKiBEtc(raw));
/* 406 */       writeTag("RAW", raw);
/*     */     }
/*     */     finally
/*     */     {
/* 410 */       exdent();
/*     */     }
/*     */     
/* 413 */     writeLineRaw("</" + tag + ">");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeRawCookedAverageTag(String tag, long raw)
/*     */   {
/* 421 */     writeLineRaw("<" + tag + ">");
/*     */     try
/*     */     {
/* 424 */       indent();
/*     */       
/* 426 */       writeTag("TEXT", DisplayFormatters.formatByteCountToKiBEtcPerSec(raw));
/* 427 */       writeTag("RAW", raw);
/*     */     }
/*     */     finally
/*     */     {
/* 431 */       exdent();
/*     */     }
/*     */     
/* 434 */     writeLineRaw("</" + tag + ">");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/impl/StatsWriterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */