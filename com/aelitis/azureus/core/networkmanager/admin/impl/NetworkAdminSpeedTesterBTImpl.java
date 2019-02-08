/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTester;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterResult;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.security.SECertificateListener;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
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
/*     */ public class NetworkAdminSpeedTesterBTImpl
/*     */   extends NetworkAdminSpeedTesterImpl
/*     */   implements NetworkAdminSpeedTester
/*     */ {
/*     */   public static final String DOWNLOAD_AVE = "download-ave";
/*     */   public static final String UPLOAD_AVE = "upload-ave";
/*     */   public static final String DOWNLOAD_STD_DEV = "download-std-dev";
/*     */   public static final String UPLOAD_STD_DEV = "upload-std-dev";
/*  65 */   private static int testMode = 0;
/*     */   
/*     */   private static TorrentAttribute speedTestAttrib;
/*     */   private static NetworkAdminSpeedTesterResult lastResult;
/*     */   private final PluginInterface plugin;
/*     */   private boolean test_started;
/*     */   
/*     */   protected static void initialise()
/*     */   {
/*  74 */     PluginInterface plugin = PluginInitializer.getDefaultInterface();
/*     */     
/*  76 */     speedTestAttrib = plugin.getTorrentManager().getPluginAttribute(NetworkAdminSpeedTesterBTImpl.class.getName() + ".test.attrib");
/*     */   }
/*     */   
/*     */ 
/*     */   protected static void startUp()
/*     */   {
/*  82 */     PluginInterface plugin = PluginInitializer.getDefaultInterface();
/*     */     
/*  84 */     org.gudy.azureus2.plugins.download.DownloadManager dm = plugin.getDownloadManager();
/*  85 */     Download[] downloads = dm.getDownloads();
/*     */     
/*  87 */     if (downloads != null) {
/*  88 */       int num = downloads.length;
/*  89 */       for (int i = 0; i < num; i++) {
/*  90 */         Download download = downloads[i];
/*  91 */         if (download.getBooleanAttribute(speedTestAttrib)) {
/*     */           try {
/*  93 */             if (download.getState() != 7) {
/*     */               try {
/*  95 */                 download.stop();
/*     */               } catch (Throwable e) {
/*  97 */                 Debug.out(e);
/*     */               }
/*     */             }
/* 100 */             download.remove(true, true);
/*     */           } catch (Throwable e) {
/* 102 */             Debug.out("Had " + e.getMessage() + " while trying to remove " + downloads[i].getName());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static NetworkAdminSpeedTesterResult getLastResult()
/*     */   {
/* 112 */     return lastResult;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean test_completed;
/*     */   
/*     */ 
/*     */   private boolean use_crypto;
/*     */   
/*     */ 
/*     */   private volatile boolean aborted;
/*     */   
/*     */ 
/*     */   private String deferred_abort;
/*     */   
/*     */ 
/*     */   public NetworkAdminSpeedTesterBTImpl(PluginInterface pi)
/*     */   {
/* 132 */     this.plugin = pi;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTestType()
/*     */   {
/* 138 */     return 1;
/*     */   }
/*     */   
/*     */   public void setMode(int mode) {
/* 142 */     testMode = mode;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMode()
/*     */   {
/* 148 */     return testMode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUseCrypto(boolean _use_crypto)
/*     */   {
/* 155 */     this.use_crypto = _use_crypto;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getUseCrypto()
/*     */   {
/* 161 */     return this.use_crypto;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void start(TOTorrent tot)
/*     */   {
/* 172 */     if (this.test_started)
/*     */     {
/* 174 */       Debug.out("Test already started!");
/*     */       
/* 176 */       return;
/*     */     }
/*     */     
/* 179 */     this.test_started = true;
/*     */     
/*     */     try
/*     */     {
/* 183 */       TorrentUtils.setFlag(tot, 1, true);
/*     */       
/* 185 */       Torrent torrent = new TorrentImpl(tot);
/* 186 */       String fileName = torrent.getName();
/*     */       
/* 188 */       sendStageUpdateToListeners(MessageText.getString("SpeedTestWizard.stage.message.preparing"));
/*     */       
/*     */ 
/* 191 */       File saveLocation = AETemporaryFileHandler.createTempFile();
/* 192 */       File baseDir = saveLocation.getParentFile();
/* 193 */       File blankFile = new File(baseDir, fileName);
/* 194 */       File blankTorrentFile = new File(baseDir, "speedTestTorrent.torrent");
/* 195 */       torrent.writeToFile(blankTorrentFile);
/*     */       
/* 197 */       URL announce_url = torrent.getAnnounceURL();
/*     */       
/* 199 */       if (announce_url.getProtocol().equalsIgnoreCase("https"))
/*     */       {
/* 201 */         SESecurityManager.setCertificateHandler(announce_url, new SECertificateListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public boolean trustCertificate(String resource, X509Certificate cert)
/*     */           {
/*     */ 
/*     */ 
/* 210 */             return true;
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 215 */       Download speed_download = this.plugin.getDownloadManager().addDownloadStopped(torrent, blankTorrentFile, blankFile);
/*     */       
/* 217 */       speed_download.setBooleanAttribute(speedTestAttrib, true);
/*     */       
/* 219 */       org.gudy.azureus2.core3.download.DownloadManager core_download = PluginCoreUtils.unwrap(speed_download);
/*     */       
/* 221 */       core_download.setPieceCheckingEnabled(false);
/*     */       
/*     */ 
/*     */ 
/* 225 */       core_download.getDownloadState().setIntParameter("max.uploads", 32);
/* 226 */       core_download.getDownloadState().setIntParameter("max.uploads.when.seeding", 32);
/*     */       
/* 228 */       if (this.use_crypto)
/*     */       {
/* 230 */         core_download.setCryptoLevel(1);
/*     */       }
/*     */       
/* 233 */       core_download.addPeerListener(new DownloadManagerPeerListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void peerManagerWillBeAdded(PEPeerManager peer_manager)
/*     */         {
/*     */ 
/* 240 */           DiskManager disk_manager = peer_manager.getDiskManager();
/*     */           
/* 242 */           DiskManagerPiece[] pieces = disk_manager.getPieces();
/*     */           
/* 244 */           int startPiece = NetworkAdminSpeedTesterBTImpl.setStartPieceBasedOnMode(NetworkAdminSpeedTesterBTImpl.testMode, pieces.length);
/*     */           
/* 246 */           for (int i = startPiece; i < pieces.length; i++) {
/* 247 */             pieces[i].setDone(true);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void peerManagerAdded(PEPeerManager peer_manager) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void peerManagerRemoved(PEPeerManager manager) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void peerAdded(PEPeer peer) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void peerRemoved(PEPeer peer) {}
/* 271 */       });
/* 272 */       speed_download.moveTo(1);
/*     */       
/* 274 */       speed_download.setFlag(4L, true);
/*     */       
/* 276 */       core_download.initialize();
/*     */       
/* 278 */       core_download.setForceStart(true);
/*     */       
/* 280 */       TorrentSpeedTestMonitorThread monitor = new TorrentSpeedTestMonitorThread(speed_download);
/*     */       
/* 282 */       monitor.start();
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 288 */       this.test_completed = true;
/*     */       
/* 290 */       abort("Could not start test", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void complete(NetworkAdminSpeedTesterResult result)
/*     */   {
/* 299 */     sendResultToListeners(result);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void abort(String reason, Throwable cause)
/*     */   {
/*     */     String msg;
/*     */     
/*     */     String msg;
/*     */     
/* 309 */     if ((cause instanceof RuntimeException))
/*     */     {
/* 311 */       msg = Debug.getNestedExceptionMessageAndStack(cause);
/*     */     }
/*     */     else
/*     */     {
/* 315 */       msg = Debug.getNestedExceptionMessage(cause);
/*     */     }
/*     */     
/* 318 */     abort(reason + ": " + msg);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void abort(String reason)
/*     */   {
/* 325 */     reason = "Test aborted: " + reason;
/*     */     
/* 327 */     synchronized (this)
/*     */     {
/* 329 */       if (this.aborted)
/*     */       {
/* 331 */         return;
/*     */       }
/*     */       
/* 334 */       this.aborted = true;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 340 */       if ((this.test_started) && (!this.test_completed))
/*     */       {
/* 342 */         this.deferred_abort = reason;
/*     */         
/* 344 */         return;
/*     */       }
/*     */     }
/*     */     
/* 348 */     sendResultToListeners(new BitTorrentResult(reason));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NetworkAdminSpeedTesterResult getResult()
/*     */   {
/* 357 */     return lastResult;
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
/*     */   private static int setStartPieceBasedOnMode(int mode, int totalPieces)
/*     */   {
/* 376 */     if (mode == 0)
/*     */     {
/* 378 */       return 0; }
/* 379 */     if (mode == 1)
/*     */     {
/* 381 */       return totalPieces;
/*     */     }
/*     */     
/* 384 */     throw new IllegalStateException("Did not recognize the NetworkAdmin Speed Test type. mode=" + mode);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class TorrentSpeedTestMonitorThread
/*     */     extends Thread
/*     */   {
/* 392 */     final List historyDownloadSpeed = new LinkedList();
/* 393 */     final List historyUploadSpeed = new LinkedList();
/* 394 */     final List timestamps = new LinkedList();
/*     */     
/*     */     final Download testDownload;
/*     */     
/*     */     public static final long MAX_TEST_TIME = 120000L;
/*     */     
/*     */     public static final long MAX_PEAK_TIME = 30000L;
/*     */     long startTime;
/*     */     long peakTime;
/*     */     long peakRate;
/*     */     public static final String AVE = "ave";
/*     */     public static final String STD_DEV = "stddev";
/*     */     
/*     */     public TorrentSpeedTestMonitorThread(Download d)
/*     */     {
/* 409 */       this.testDownload = d;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/*     */       try {
/* 415 */         Set connected_peers = new HashSet();
/* 416 */         Set not_choked_peers = new HashSet();
/* 417 */         Set not_choking_peers = new HashSet();
/*     */         
/*     */         try
/*     */         {
/* 421 */           this.startTime = SystemTime.getCurrentTime();
/* 422 */           this.peakTime = this.startTime;
/*     */           
/* 424 */           boolean testDone = false;
/* 425 */           long lastTotalTransferredBytes = 0L;
/*     */           
/* 427 */           NetworkAdminSpeedTesterBTImpl.this.sendStageUpdateToListeners(MessageText.getString("SpeedTestWizard.stage.message.starting"));
/* 428 */           while ((!testDone) && (!NetworkAdminSpeedTesterBTImpl.this.aborted))
/*     */           {
/* 430 */             int state = this.testDownload.getState();
/*     */             
/* 432 */             if (state == 8)
/*     */             {
/* 434 */               String enteredErrorState = MessageText.getString("SpeedTestWizard.abort.message.entered.error", new String[] { this.testDownload.getErrorStateDetails() });
/*     */               
/* 436 */               NetworkAdminSpeedTesterBTImpl.this.abort(enteredErrorState);
/*     */               
/* 438 */               break;
/*     */             }
/*     */             
/* 441 */             if (state == 7)
/*     */             {
/* 443 */               NetworkAdminSpeedTesterBTImpl.this.abort(MessageText.getString("SpeedTestWizard.abort.message.entered.queued"));
/*     */               
/* 445 */               break;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 451 */             if (!this.testDownload.isForceStart())
/*     */             {
/* 453 */               this.testDownload.setForceStart(true);
/*     */             }
/*     */             
/* 456 */             PeerManager pm = this.testDownload.getPeerManager();
/*     */             
/* 458 */             if (pm != null)
/*     */             {
/* 460 */               Peer[] peers = pm.getPeers();
/*     */               
/* 462 */               for (int i = 0; i < peers.length; i++)
/*     */               {
/* 464 */                 Peer peer = peers[i];
/*     */                 
/*     */ 
/*     */ 
/* 468 */                 String key = peer.getIp();
/*     */                 
/* 470 */                 connected_peers.add(key);
/*     */                 
/* 472 */                 if (!peer.isChoked())
/*     */                 {
/* 474 */                   not_choked_peers.add(key);
/*     */                 }
/*     */                 
/* 477 */                 if (!peer.isChoking())
/*     */                 {
/* 479 */                   not_choking_peers.add(key);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 484 */             long currTime = SystemTime.getCurrentTime();
/* 485 */             DownloadStats stats = this.testDownload.getStats();
/* 486 */             this.historyDownloadSpeed.add(NetworkAdminSpeedTesterBTImpl.autoboxLong(stats.getDownloaded(true)));
/* 487 */             this.historyUploadSpeed.add(NetworkAdminSpeedTesterBTImpl.autoboxLong(stats.getUploaded(true)));
/* 488 */             this.timestamps.add(NetworkAdminSpeedTesterBTImpl.autoboxLong(currTime));
/*     */             
/* 490 */             updateTestProgress(currTime, stats);
/*     */             
/* 492 */             lastTotalTransferredBytes = checkForNewPeakValue(stats, lastTotalTransferredBytes, currTime);
/*     */             
/* 494 */             testDone = checkForTestDone();
/* 495 */             if (testDone)
/*     */               break;
/*     */             try {
/* 498 */               Thread.sleep(1000L);
/*     */             }
/*     */             catch (InterruptedException ie) {
/* 501 */               NetworkAdminSpeedTesterBTImpl.this.abort(MessageText.getString("SpeedTestWizard.abort.message.interrupted"));
/*     */               
/* 503 */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 510 */             if (this.testDownload.getState() != 7) {
/*     */               try {
/* 512 */                 this.testDownload.stop();
/*     */               } catch (Throwable e) {
/* 514 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/* 517 */             this.testDownload.remove(true, true);
/*     */           }
/*     */           catch (DownloadException de)
/*     */           {
/* 521 */             NetworkAdminSpeedTesterBTImpl.this.abort("TorrentSpeedTestMonitorThread could not stop the torrent " + this.testDownload.getName(), de);
/*     */           }
/*     */           catch (DownloadRemovalVetoException drve)
/*     */           {
/* 525 */             NetworkAdminSpeedTesterBTImpl.this.abort("TorrentSpeedTestMonitorTheard could not remove the torrent " + this.testDownload.getName(), drve);
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 530 */           NetworkAdminSpeedTesterBTImpl.this.abort(MessageText.getString("SpeedTestWizard.abort.message.execution.failed"), e);
/*     */         }
/*     */         
/* 533 */         if (!NetworkAdminSpeedTesterBTImpl.this.aborted)
/*     */         {
/*     */ 
/* 536 */           String connectStats = MessageText.getString("SpeedTestWizard.stage.message.connect.stats", new String[] { "" + connected_peers.size(), "" + not_choked_peers.size(), "" + not_choking_peers.size() });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 541 */           NetworkAdminSpeedTesterBTImpl.this.sendStageUpdateToListeners(connectStats);
/*     */           
/* 543 */           if (connected_peers.size() == 0)
/*     */           {
/* 545 */             NetworkAdminSpeedTesterBTImpl.this.abort(MessageText.getString("SpeedTestWizard.abort.message.failed.peers"));
/*     */           }
/* 547 */           else if ((not_choking_peers.size() == 0) && (NetworkAdminSpeedTesterBTImpl.testMode != 1))
/*     */           {
/* 549 */             NetworkAdminSpeedTesterBTImpl.this.abort(MessageText.getString("SpeedTestWizard.abort.message.insufficient.slots"));
/*     */           }
/* 551 */           else if ((not_choked_peers.size() == 0) && (NetworkAdminSpeedTesterBTImpl.testMode != 0))
/*     */           {
/* 553 */             NetworkAdminSpeedTesterBTImpl.this.abort(MessageText.getString("SpeedTestWizard.abort.message.not.unchoked"));
/*     */           }
/*     */         }
/*     */         
/* 557 */         if (!NetworkAdminSpeedTesterBTImpl.this.aborted)
/*     */         {
/*     */ 
/* 560 */           NetworkAdminSpeedTesterResult r = calculateDownloadRate();
/*     */           
/* 562 */           NetworkAdminSpeedTesterBTImpl.access$402(r);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 567 */           AEDiagnosticsLogger diagLogger = AEDiagnostics.getLogger("v3.STres");
/* 568 */           diagLogger.log(r.toString());
/*     */           
/* 570 */           NetworkAdminSpeedTesterBTImpl.this.complete(r);
/*     */         }
/*     */       }
/*     */       finally {
/* 574 */         synchronized (NetworkAdminSpeedTesterBTImpl.this)
/*     */         {
/* 576 */           NetworkAdminSpeedTesterBTImpl.this.test_completed = true;
/*     */           
/* 578 */           if (NetworkAdminSpeedTesterBTImpl.this.deferred_abort != null)
/*     */           {
/* 580 */             NetworkAdminSpeedTesterBTImpl.this.sendResultToListeners(new NetworkAdminSpeedTesterBTImpl.BitTorrentResult(NetworkAdminSpeedTesterBTImpl.this, NetworkAdminSpeedTesterBTImpl.this.deferred_abort));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void updateTestProgress(long currTime, DownloadStats stats)
/*     */     {
/* 594 */       long totalDownloadTimeUsed = currTime - this.startTime;
/* 595 */       float percentTotal = (float)totalDownloadTimeUsed / 120000.0F;
/*     */       
/*     */ 
/* 598 */       long totalTestTimeUsed = currTime - this.peakTime;
/* 599 */       float percentDownload = (float)totalTestTimeUsed / 30000.0F;
/*     */       
/*     */ 
/* 602 */       float reportedProgress = percentTotal;
/* 603 */       if (percentDownload > reportedProgress) {
/* 604 */         reportedProgress = percentDownload;
/*     */       }
/* 606 */       int progressBarVal = Math.round(reportedProgress * 100.0F);
/* 607 */       StringBuilder msg = new StringBuilder("progress: ");
/* 608 */       msg.append(progressBarVal);
/*     */       
/* 610 */       msg.append(" : download ave ");
/* 611 */       msg.append(stats.getDownloadAverage(true));
/* 612 */       msg.append(" : upload ave ");
/* 613 */       msg.append(stats.getUploadAverage(true));
/* 614 */       msg.append(" : ");
/* 615 */       int totalTimeLeft = (int)((120000L - totalDownloadTimeUsed) / 1000L);
/* 616 */       msg.append(totalTimeLeft);
/* 617 */       msg.append(" : ");
/* 618 */       int testTimeLeft = (int)((30000L - totalTestTimeUsed) / 1000L);
/* 619 */       msg.append(testTimeLeft);
/*     */       
/* 621 */       NetworkAdminSpeedTesterBTImpl.this.sendStageUpdateToListeners(msg.toString());
/*     */     }
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
/*     */     private Map calculate(List history)
/*     */     {
/* 635 */       List deltas = convertSumToDeltas(history);
/*     */       
/*     */ 
/* 638 */       Collections.sort(deltas);
/*     */       
/*     */ 
/* 641 */       int nSamples = deltas.size();
/* 642 */       int nRemove = nSamples / 10;
/*     */       
/* 644 */       for (int i = 0; i < nRemove; i++) {
/* 645 */         deltas.remove(0);
/* 646 */         deltas.remove(deltas.size() - 1);
/*     */       }
/*     */       
/*     */ 
/* 650 */       long sumBytes = 0L;
/* 651 */       int j = 0;
/* 652 */       while (j < deltas.size()) {
/* 653 */         sumBytes += NetworkAdminSpeedTesterBTImpl.autoboxLong(deltas.get(j));
/* 654 */         j++;
/*     */       }
/*     */       
/* 657 */       double aveRate = sumBytes / deltas.size();
/*     */       
/*     */ 
/*     */ 
/* 661 */       double variance = 0.0D;
/*     */       
/* 663 */       for (j = 0; j < deltas.size(); j++)
/*     */       {
/*     */ 
/* 666 */         double s = NetworkAdminSpeedTesterBTImpl.autoboxLong(deltas.get(j)) - aveRate;
/* 667 */         variance += s * s;
/*     */       }
/* 669 */       double stddev = Math.sqrt(variance / (j - 1));
/*     */       
/*     */ 
/* 672 */       Map retVal = new HashMap();
/* 673 */       retVal.put("ave", NetworkAdminSpeedTesterBTImpl.autoboxDouble(aveRate));
/* 674 */       retVal.put("stddev", NetworkAdminSpeedTesterBTImpl.autoboxDouble(stddev));
/* 675 */       return retVal;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private List convertSumToDeltas(List sumHistory)
/*     */     {
/* 686 */       int numStats = sumHistory.size();
/* 687 */       int i = findIndexPeak(numStats);
/*     */       
/* 689 */       List deltas = new ArrayList(numStats);
/* 690 */       if (i == 0) return deltas;
/* 691 */       long prevSumDownload = NetworkAdminSpeedTesterBTImpl.autoboxLong(sumHistory.get(i - 1));
/*     */       
/* 693 */       while (i < numStats)
/*     */       {
/* 695 */         long currSumDownload = NetworkAdminSpeedTesterBTImpl.autoboxLong(sumHistory.get(i));
/* 696 */         Long currDelta = NetworkAdminSpeedTesterBTImpl.autoboxLong(currSumDownload - prevSumDownload);
/*     */         
/* 698 */         deltas.add(currDelta);
/* 699 */         i++;
/* 700 */         prevSumDownload = currSumDownload;
/*     */       }
/*     */       
/* 703 */       return deltas;
/*     */     }
/*     */     
/*     */ 
/*     */     private int findIndexPeak(int numStats)
/*     */     {
/* 709 */       for (int i = 0; i < numStats; i++) {
/* 710 */         long thisTime = NetworkAdminSpeedTesterBTImpl.autoboxLong(this.timestamps.get(i));
/* 711 */         if (thisTime > this.peakTime) {
/*     */           break;
/*     */         }
/*     */       }
/* 715 */       return i;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     NetworkAdminSpeedTesterResult calculateDownloadRate()
/*     */     {
/* 727 */       Map resDown = calculate(this.historyDownloadSpeed);
/*     */       
/*     */ 
/*     */ 
/* 731 */       Map resUp = calculate(this.historyUploadSpeed);
/*     */       
/* 733 */       return new NetworkAdminSpeedTesterBTImpl.BitTorrentResult(NetworkAdminSpeedTesterBTImpl.this, resUp, resDown);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     boolean checkForTestDone()
/*     */     {
/* 745 */       long currTime = SystemTime.getCurrentTime();
/*     */       
/* 747 */       if (currTime - this.startTime > 120000L) {
/* 748 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 752 */       return currTime - this.peakTime > 30000L;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     long checkForNewPeakValue(DownloadStats stat, long lastTotalDownload, long currTime)
/*     */     {
/*     */       long totTransferred;
/*     */       
/*     */ 
/*     */ 
/*     */       long totTransferred;
/*     */       
/*     */ 
/* 767 */       if (NetworkAdminSpeedTesterBTImpl.testMode == 0) {
/* 768 */         totTransferred = stat.getUploaded(true);
/*     */       } else {
/* 770 */         totTransferred = stat.getDownloaded(true);
/*     */       }
/* 772 */       long currTransferRate = totTransferred - lastTotalDownload;
/*     */       
/*     */ 
/* 775 */       if (currTransferRate > this.peakRate) {
/* 776 */         this.peakRate = ((currTransferRate * 1.1D));
/* 777 */         this.peakTime = currTime;
/*     */       }
/*     */       
/* 780 */       return totTransferred;
/*     */     }
/*     */   }
/*     */   
/*     */   class BitTorrentResult
/*     */     implements NetworkAdminSpeedTesterResult
/*     */   {
/*     */     final long time;
/*     */     int downspeed;
/*     */     int upspeed;
/* 790 */     boolean hadError = false;
/* 791 */     String lastError = "";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public BitTorrentResult(Map uploadRes, Map downloadRes)
/*     */     {
/* 799 */       this.time = SystemTime.getCurrentTime();
/* 800 */       Double dAve = (Double)downloadRes.get("ave");
/* 801 */       Double uAve = (Double)uploadRes.get("ave");
/* 802 */       this.downspeed = dAve.intValue();
/* 803 */       this.upspeed = uAve.intValue();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public BitTorrentResult(String errorMsg)
/*     */     {
/* 811 */       this.time = SystemTime.getCurrentTime();
/* 812 */       this.hadError = true;
/* 813 */       this.lastError = errorMsg;
/*     */     }
/*     */     
/*     */     public NetworkAdminSpeedTester getTest() {
/* 817 */       return NetworkAdminSpeedTesterBTImpl.this;
/*     */     }
/*     */     
/*     */     public long getTestTime() {
/* 821 */       return this.time;
/*     */     }
/*     */     
/*     */     public int getDownloadSpeed() {
/* 825 */       return this.downspeed;
/*     */     }
/*     */     
/*     */     public int getUploadSpeed() {
/* 829 */       return this.upspeed;
/*     */     }
/*     */     
/*     */     public boolean hadError() {
/* 833 */       return this.hadError;
/*     */     }
/*     */     
/*     */     public String getLastError() {
/* 837 */       return this.lastError;
/*     */     }
/*     */     
/*     */     public String getResultString() {
/* 841 */       StringBuilder sb = new StringBuilder();
/*     */       
/*     */ 
/* 844 */       SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmss z");
/* 845 */       String d = format.format(new Date(this.time));
/* 846 */       sb.append(d).append(" ");
/*     */       
/* 848 */       sb.append("type: BT test ");
/*     */       
/*     */ 
/* 851 */       sb.append("mode: ").append(NetworkAdminSpeedTesterBTImpl.this.getMode());
/*     */       
/*     */ 
/* 854 */       sb.append(" encrypted: ");
/* 855 */       if (NetworkAdminSpeedTesterBTImpl.this.use_crypto) {
/* 856 */         sb.append("y");
/*     */       } else {
/* 858 */         sb.append("n");
/*     */       }
/*     */       
/* 861 */       if (this.hadError)
/*     */       {
/* 863 */         sb.append(" Last Error: ").append(this.lastError);
/*     */       }
/*     */       else {
/* 866 */         sb.append(" download speed: ").append(this.downspeed).append(" bits/sec");
/* 867 */         sb.append(" upload speed: ").append(this.upspeed).append(" bits/sec");
/*     */       }
/*     */       
/* 870 */       return sb.toString();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 875 */       StringBuilder sb = new StringBuilder("[com.aelitis.azureus.core.networkmanager.admin.impl.NetworkAdminSpeedTesterBTImpl");
/*     */       
/* 877 */       sb.append(" ").append(getResultString()).append(" ");
/* 878 */       sb.append("]");
/*     */       
/* 880 */       return sb.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   private static long autoboxLong(Object o)
/*     */   {
/* 886 */     return autoboxLong((Long)o);
/*     */   }
/*     */   
/*     */   private static long autoboxLong(Long l) {
/* 890 */     return l.longValue();
/*     */   }
/*     */   
/*     */   private static Long autoboxLong(long l) {
/* 894 */     return new Long(l);
/*     */   }
/*     */   
/*     */   private static Double autoboxDouble(double d) {
/* 898 */     return new Double(d);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminSpeedTesterBTImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */