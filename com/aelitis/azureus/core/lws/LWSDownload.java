/*     */ package com.aelitis.azureus.core.lws;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManager;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadActivationEvent;
/*     */ import org.gudy.azureus2.plugins.download.DownloadActivationListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPropertyListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeRemovedListener;
/*     */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
/*     */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.tag.Tag;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadAnnounceResultImpl;
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
/*     */ public class LWSDownload
/*     */   implements Download
/*     */ {
/*     */   private final LightWeightSeed lws;
/*     */   private final TRTrackerAnnouncer announcer;
/*     */   final DownloadAnnounceResultImpl announce_result;
/*  68 */   private final Map user_data = new HashMap();
/*  69 */   private final Map torrent_attributes = new HashMap();
/*     */   
/*  71 */   private final DownloadScrapeResult scrape_result = new DownloadScrapeResult()
/*     */   {
/*     */ 
/*     */     public Download getDownload()
/*     */     {
/*     */ 
/*  77 */       return LWSDownload.this;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getResponseType()
/*     */     {
/*  83 */       return LWSDownload.this.announce_result.getResponseType() == 1 ? 1 : 2;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getSeedCount()
/*     */     {
/*  89 */       return LWSDownload.this.announce_result.getSeedCount();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getNonSeedCount()
/*     */     {
/*  95 */       int seeds = getSeedCount();
/*  96 */       int reported = LWSDownload.this.announce_result.getReportedPeerCount();
/*     */       
/*  98 */       int min_peers = reported - seeds;
/*     */       
/* 100 */       int peers = LWSDownload.this.announce_result.getNonSeedCount();
/*     */       
/* 102 */       if (peers < min_peers)
/*     */       {
/* 104 */         peers = min_peers;
/*     */       }
/*     */       
/* 107 */       return peers;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getScrapeStartTime()
/*     */     {
/* 113 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setNextScrapeStartTime(long nextScrapeStartTime) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public long getNextScrapeStartTime()
/*     */     {
/* 125 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getStatus()
/*     */     {
/* 131 */       if (getResponseType() == 1)
/*     */       {
/* 133 */         return "OK";
/*     */       }
/*     */       
/*     */ 
/* 137 */       return LWSDownload.this.announce_result.getError();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public URL getURL()
/*     */     {
/* 144 */       return LWSDownload.this.announce_result.getURL();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LWSDownload(LightWeightSeed _lws, TRTrackerAnnouncer _announcer)
/*     */   {
/* 154 */     this.lws = _lws;
/* 155 */     this.announcer = _announcer;
/*     */     
/* 157 */     this.announce_result = new DownloadAnnounceResultImpl(this, this.announcer.getLastResponse());
/*     */   }
/*     */   
/*     */ 
/*     */   public LightWeightSeed getLWS()
/*     */   {
/* 163 */     return this.lws;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getState()
/*     */   {
/* 169 */     return 5;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSubState()
/*     */   {
/* 175 */     return 5;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getErrorStateDetails()
/*     */   {
/* 181 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFlag(long flag, boolean value)
/*     */   {
/* 189 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getFlag(long flag)
/*     */   {
/* 196 */     return flag == 1024L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFlags()
/*     */   {
/* 202 */     return 1024L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 208 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File[] calculateDefaultPaths(boolean for_moving)
/*     */   {
/* 215 */     return new File[2];
/*     */   }
/*     */   
/*     */ 
/*     */   public SaveLocationChange calculateDefaultDownloadLocation()
/*     */   {
/* 221 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInDefaultSaveDir()
/*     */   {
/* 227 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public Torrent getTorrent()
/*     */   {
/* 233 */     return this.lws.getTorrent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initialize()
/*     */     throws DownloadException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */     throws DownloadException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startDownload(boolean force) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stopDownload() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stop()
/*     */     throws DownloadException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stopAndQueue()
/*     */     throws DownloadException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void restart()
/*     */     throws DownloadException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pause() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resume() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recheckData()
/*     */     throws DownloadException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isStartStopLocked()
/*     */   {
/* 302 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isForceStart()
/*     */   {
/* 309 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setForceStart(boolean forceStart) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 321 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPriority(int priority) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPriorityLocked()
/*     */   {
/* 333 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPaused()
/*     */   {
/* 339 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 345 */     return this.lws.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTorrentFileName()
/*     */   {
/* 351 */     return getName();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String getAttribute(TorrentAttribute attribute)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 364	com/aelitis/azureus/core/lws/LWSDownload:torrent_attributes	Ljava/util/Map;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 364	com/aelitis/azureus/core/lws/LWSDownload:torrent_attributes	Ljava/util/Map;
/*     */     //   11: aload_1
/*     */     //   12: invokeinterface 386 2 0
/*     */     //   17: checkcast 234	java/lang/String
/*     */     //   20: aload_2
/*     */     //   21: monitorexit
/*     */     //   22: areturn
/*     */     //   23: astore_3
/*     */     //   24: aload_2
/*     */     //   25: monitorexit
/*     */     //   26: aload_3
/*     */     //   27: athrow
/*     */     // Line number table:
/*     */     //   Java source line #358	-> byte code offset #0
/*     */     //   Java source line #360	-> byte code offset #7
/*     */     //   Java source line #361	-> byte code offset #23
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	28	0	this	LWSDownload
/*     */     //   0	28	1	attribute	TorrentAttribute
/*     */     //   5	20	2	Ljava/lang/Object;	Object
/*     */     //   23	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	22	23	finally
/*     */     //   23	26	23	finally
/*     */   }
/*     */   
/*     */   public void setAttribute(TorrentAttribute attribute, String value)
/*     */   {
/* 369 */     synchronized (this.torrent_attributes)
/*     */     {
/* 371 */       this.torrent_attributes.put(attribute, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getListAttribute(TorrentAttribute attribute)
/*     */   {
/* 379 */     TorrentManager tm = PluginInitializer.getDefaultInterface().getTorrentManager();
/*     */     
/* 381 */     if (attribute == tm.getAttribute("Networks"))
/*     */     {
/* 383 */       return new String[] { this.lws.getNetwork() };
/*     */     }
/* 385 */     if (attribute == tm.getAttribute("PeerSources"))
/*     */     {
/* 387 */       return new String[] { "DHT" };
/*     */     }
/*     */     
/* 390 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setListAttribute(TorrentAttribute attribute, String[] value)
/*     */   {
/* 398 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMapAttribute(TorrentAttribute attribute, Map value)
/*     */   {
/* 406 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getMapAttribute(TorrentAttribute attribute)
/*     */   {
/* 413 */     return null;
/*     */   }
/*     */   
/* 416 */   public void setIntAttribute(TorrentAttribute name, int value) { notSupported(); }
/* 417 */   public int getIntAttribute(TorrentAttribute name) { return 0; }
/* 418 */   public void setLongAttribute(TorrentAttribute name, long value) { notSupported(); }
/* 419 */   public long getLongAttribute(TorrentAttribute name) { return 0L; }
/* 420 */   public void setBooleanAttribute(TorrentAttribute name, boolean value) { notSupported(); }
/* 421 */   public boolean getBooleanAttribute(TorrentAttribute name) { return false; }
/* 422 */   public boolean hasAttribute(TorrentAttribute name) { return false; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAttributeListener(DownloadAttributeListener l, TorrentAttribute attr, int event_type) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeAttributeListener(DownloadAttributeListener l, TorrentAttribute attr, int event_type) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getCategoryName()
/*     */   {
/* 443 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCategory(String sName)
/*     */   {
/* 450 */     notSupported();
/*     */   }
/*     */   
/*     */   public List<Tag> getTags() {
/* 454 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove()
/*     */     throws DownloadException, DownloadRemovalVetoException
/*     */   {
/* 462 */     throw new DownloadRemovalVetoException("no way");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void remove(boolean delete_torrent, boolean delete_data)
/*     */     throws DownloadException, DownloadRemovalVetoException
/*     */   {
/* 472 */     throw new DownloadRemovalVetoException("no way");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isRemoved()
/*     */   {
/* 478 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPosition()
/*     */   {
/* 484 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreationTime()
/*     */   {
/* 490 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPosition(int newPosition)
/*     */   {
/* 497 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public void moveUp()
/*     */   {
/* 503 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public void moveDown()
/*     */   {
/* 509 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void moveTo(int position)
/*     */   {
/* 516 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canBeRemoved()
/*     */     throws DownloadRemovalVetoException
/*     */   {
/* 524 */     throw new DownloadRemovalVetoException("no way");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAnnounceResult(DownloadAnnounceResult result)
/*     */   {
/* 531 */     this.announcer.setAnnounceResult(result);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setScrapeResult(DownloadScrapeResult result) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public DownloadAnnounceResult getLastAnnounceResult()
/*     */   {
/* 543 */     this.announce_result.setContent(this.announcer.getLastResponse());
/*     */     
/* 545 */     return this.announce_result;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadScrapeResult getLastScrapeResult()
/*     */   {
/* 551 */     this.announce_result.setContent(this.announcer.getLastResponse());
/*     */     
/* 553 */     return this.scrape_result;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadScrapeResult getAggregatedScrapeResult()
/*     */   {
/* 559 */     return getLastScrapeResult();
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadActivationEvent getActivationState()
/*     */   {
/* 565 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadStats getStats()
/*     */   {
/* 571 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPersistent()
/*     */   {
/* 577 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaximumDownloadKBPerSecond(int kb)
/*     */   {
/* 584 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumDownloadKBPerSecond()
/*     */   {
/* 590 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRateLimiter(RateLimiter limiter, boolean is_upload)
/*     */   {
/* 598 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRateLimiter(RateLimiter limiter, boolean is_upload)
/*     */   {
/* 606 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUploadRateLimitBytesPerSecond()
/*     */   {
/* 612 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUploadRateLimitBytesPerSecond(int max_rate_bps)
/*     */   {
/* 619 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadRateLimitBytesPerSecond()
/*     */   {
/* 625 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadRateLimitBytesPerSecond(int max_rate_bps)
/*     */   {
/* 632 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComplete()
/*     */   {
/* 638 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isComplete(boolean bIncludeDND)
/*     */   {
/* 645 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isChecking()
/*     */   {
/* 651 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isMoving()
/*     */   {
/* 657 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSavePath()
/*     */   {
/* 663 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void moveDataFiles(File new_parent_dir)
/*     */     throws DownloadException
/*     */   {
/* 672 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canMoveDataFiles()
/*     */   {
/* 678 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void moveTorrentFile(File new_parent_dir)
/*     */     throws DownloadException
/*     */   {
/* 687 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void renameDownload(String name)
/*     */     throws DownloadException
/*     */   {
/* 696 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public PeerManager getPeerManager()
/*     */   {
/* 702 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManager getDiskManager()
/*     */   {
/* 708 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerFileInfo[] getDiskManagerFileInfo()
/*     */   {
/* 714 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerFileInfo getDiskManagerFileInfo(int i)
/*     */   {
/* 720 */     return null;
/*     */   }
/*     */   
/*     */   public int getDiskManagerFileCount()
/*     */   {
/* 725 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void requestTrackerAnnounce() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void requestTrackerAnnounce(boolean immediate) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void requestTrackerScrape(boolean immediate) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(DownloadListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(DownloadListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addCompletionListener(DownloadCompletionListener l)
/*     */   {
/* 762 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeCompletionListener(DownloadCompletionListener l)
/*     */   {
/* 769 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTrackerListener(DownloadTrackerListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTrackerListener(DownloadTrackerListener l, boolean immediateTrigger) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeTrackerListener(DownloadTrackerListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l)
/*     */   {
/* 795 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l)
/*     */   {
/* 802 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addActivationListener(DownloadActivationListener l)
/*     */   {
/* 809 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeActivationListener(DownloadActivationListener l)
/*     */   {
/* 816 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPeerListener(DownloadPeerListener l)
/*     */   {
/* 823 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePeerListener(DownloadPeerListener l)
/*     */   {
/* 830 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getSeedingRank()
/*     */   {
/* 837 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSeedingRank(int rank)
/*     */   {
/* 844 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPropertyListener(DownloadPropertyListener l)
/*     */   {
/* 851 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePropertyListener(DownloadPropertyListener l)
/*     */   {
/* 858 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getDownloadPeerId()
/*     */   {
/* 864 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isMessagingEnabled()
/*     */   {
/* 870 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMessagingEnabled(boolean enabled) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void moveDataFiles(File new_parent_dir, String new_name)
/*     */     throws DownloadException
/*     */   {
/* 886 */     notSupported();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Object getUserData(Object key)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 365	com/aelitis/azureus/core/lws/LWSDownload:user_data	Ljava/util/Map;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 365	com/aelitis/azureus/core/lws/LWSDownload:user_data	Ljava/util/Map;
/*     */     //   11: aload_1
/*     */     //   12: invokeinterface 386 2 0
/*     */     //   17: aload_2
/*     */     //   18: monitorexit
/*     */     //   19: areturn
/*     */     //   20: astore_3
/*     */     //   21: aload_2
/*     */     //   22: monitorexit
/*     */     //   23: aload_3
/*     */     //   24: athrow
/*     */     // Line number table:
/*     */     //   Java source line #893	-> byte code offset #0
/*     */     //   Java source line #895	-> byte code offset #7
/*     */     //   Java source line #896	-> byte code offset #20
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	25	0	this	LWSDownload
/*     */     //   0	25	1	key	Object
/*     */     //   5	17	2	Ljava/lang/Object;	Object
/*     */     //   20	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	19	20	finally
/*     */     //   20	23	20	finally
/*     */   }
/*     */   
/*     */   public void setUserData(Object key, Object data)
/*     */   {
/* 904 */     synchronized (this.user_data)
/*     */     {
/* 906 */       this.user_data.put(key, data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void changeLocation(SaveLocationChange slc)
/*     */     throws DownloadException
/*     */   {
/* 916 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStub()
/*     */   {
/* 922 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canStubbify()
/*     */   {
/* 928 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DownloadStub stubbify()
/*     */     throws DownloadException, DownloadRemovalVetoException
/*     */   {
/* 936 */     throw new DownloadException("Not Supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download destubbify()
/*     */     throws DownloadException
/*     */   {
/* 944 */     throw new DownloadException("Not Supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public List<DistributedDatabase> getDistributedDatabases()
/*     */   {
/* 950 */     return DDBaseImpl.getDDBs(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getTorrentHash()
/*     */   {
/* 956 */     return this.lws.getTorrent().getHash();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTorrentSize()
/*     */   {
/* 962 */     return this.lws.getTorrent().getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadStub.DownloadStubFile[] getStubFiles()
/*     */   {
/* 968 */     notSupported();
/*     */     
/* 970 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void notSupported()
/*     */   {
/* 976 */     Debug.out("Not Supported");
/*     */   }
/*     */   
/*     */   public DiskManagerFileInfo getPrimaryFile()
/*     */   {
/* 981 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LWSDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */