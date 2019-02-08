/*      */ package org.gudy.azureus2.pluginsimpl.remote.download;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManager;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadActivationEvent;
/*      */ import org.gudy.azureus2.plugins.download.DownloadActivationListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadPropertyListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
/*      */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadWillBeRemovedListener;
/*      */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
/*      */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.tag.Tag;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*      */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*      */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*      */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*      */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
/*      */ import org.gudy.azureus2.pluginsimpl.remote.disk.RPDiskManagerFileInfo;
/*      */ import org.gudy.azureus2.pluginsimpl.remote.torrent.RPTorrent;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class RPDownload
/*      */   extends RPObject
/*      */   implements Download
/*      */ {
/*      */   protected transient Download delegate;
/*      */   public RPTorrent torrent;
/*      */   public RPDownloadStats stats;
/*      */   public RPDownloadAnnounceResult announce_result;
/*      */   public RPDownloadScrapeResult scrape_result;
/*      */   public int position;
/*      */   public boolean force_start;
/*      */   
/*      */   public static RPDownload create(Download _delegate)
/*      */   {
/*   70 */     RPDownload res = (RPDownload)_lookupLocal(_delegate);
/*      */     
/*   72 */     if (res == null)
/*      */     {
/*   74 */       res = new RPDownload(_delegate);
/*      */     }
/*      */     
/*   77 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected RPDownload(Download _delegate)
/*      */   {
/*   84 */     super(_delegate);
/*      */     
/*      */ 
/*      */ 
/*   88 */     if (this.delegate.getTorrent() != null)
/*      */     {
/*   90 */       this.torrent = ((RPTorrent)_lookupLocal(this.delegate.getTorrent()));
/*      */       
/*   92 */       if (this.torrent == null)
/*      */       {
/*   94 */         this.torrent = RPTorrent.create(this.delegate.getTorrent());
/*      */       }
/*      */     }
/*      */     
/*   98 */     this.stats = ((RPDownloadStats)_lookupLocal(this.delegate.getStats()));
/*      */     
/*  100 */     if (this.stats == null)
/*      */     {
/*  102 */       this.stats = RPDownloadStats.create(this.delegate.getStats());
/*      */     }
/*      */     
/*  105 */     this.announce_result = ((RPDownloadAnnounceResult)_lookupLocal(this.delegate.getLastAnnounceResult()));
/*      */     
/*  107 */     if (this.announce_result == null)
/*      */     {
/*  109 */       this.announce_result = RPDownloadAnnounceResult.create(this.delegate.getLastAnnounceResult());
/*      */     }
/*      */     
/*  112 */     this.scrape_result = ((RPDownloadScrapeResult)_lookupLocal(this.delegate.getLastScrapeResult()));
/*      */     
/*  114 */     if (this.scrape_result == null)
/*      */     {
/*  116 */       this.scrape_result = RPDownloadScrapeResult.create(this.delegate.getLastScrapeResult());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void _setDelegate(Object _delegate)
/*      */   {
/*  124 */     this.delegate = ((Download)_delegate);
/*      */     
/*  126 */     this.position = this.delegate.getPosition();
/*  127 */     this.force_start = this.delegate.isForceStart();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object _setLocal()
/*      */     throws RPException
/*      */   {
/*  135 */     Object res = _fixupLocal();
/*      */     
/*  137 */     if (this.torrent != null)
/*      */     {
/*  139 */       this.torrent._setLocal();
/*      */     }
/*      */     
/*  142 */     this.stats._setLocal();
/*      */     
/*  144 */     this.announce_result._setLocal();
/*      */     
/*  146 */     this.scrape_result._setLocal();
/*      */     
/*  148 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void _setRemote(RPRequestDispatcher dispatcher)
/*      */   {
/*  155 */     super._setRemote(dispatcher);
/*      */     
/*  157 */     if (this.torrent != null)
/*      */     {
/*  159 */       this.torrent._setRemote(dispatcher);
/*      */     }
/*      */     
/*  162 */     this.stats._setRemote(dispatcher);
/*      */     
/*  164 */     this.announce_result._setRemote(dispatcher);
/*      */     
/*  166 */     this.scrape_result._setRemote(dispatcher);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public RPReply _process(RPRequest request)
/*      */   {
/*  173 */     String method = request.getMethod();
/*      */     
/*  175 */     if (method.equals("initialize"))
/*      */     {
/*      */       try {
/*  178 */         this.delegate.initialize();
/*      */       }
/*      */       catch (DownloadException e)
/*      */       {
/*  182 */         return new RPReply(e);
/*      */       }
/*      */       
/*  185 */       return null;
/*      */     }
/*  187 */     if (method.equals("start"))
/*      */     {
/*      */       try {
/*  190 */         this.delegate.start();
/*      */       }
/*      */       catch (DownloadException e)
/*      */       {
/*  194 */         return new RPReply(e);
/*      */       }
/*      */       
/*  197 */       return null;
/*      */     }
/*  199 */     if (method.equals("restart"))
/*      */     {
/*      */       try {
/*  202 */         this.delegate.restart();
/*      */       }
/*      */       catch (DownloadException e)
/*      */       {
/*  206 */         return new RPReply(e);
/*      */       }
/*      */       
/*  209 */       return null;
/*      */     }
/*  211 */     if (method.equals("stop"))
/*      */     {
/*      */       try {
/*  214 */         this.delegate.stop();
/*      */       }
/*      */       catch (DownloadException e)
/*      */       {
/*  218 */         return new RPReply(e);
/*      */       }
/*      */       
/*  221 */       return null;
/*      */     }
/*  223 */     if (method.equals("remove"))
/*      */     {
/*      */       try {
/*  226 */         this.delegate.remove();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  230 */         return new RPReply(e);
/*      */       }
/*      */       
/*  233 */       return null;
/*      */     }
/*  235 */     if (method.equals("setForceStart[boolean]"))
/*      */     {
/*  237 */       boolean b = ((Boolean)request.getParams()[0]).booleanValue();
/*      */       
/*  239 */       this.delegate.setForceStart(b);
/*      */       
/*  241 */       return null;
/*      */     }
/*  243 */     if (method.equals("setPosition[int]"))
/*      */     {
/*  245 */       int p = ((Integer)request.getParams()[0]).intValue();
/*      */       
/*  247 */       this.delegate.setPosition(p);
/*      */       
/*  249 */       return null;
/*      */     }
/*  251 */     if (method.equals("moveUp"))
/*      */     {
/*  253 */       this.delegate.moveUp();
/*      */       
/*  255 */       return null;
/*      */     }
/*  257 */     if (method.equals("moveDown"))
/*      */     {
/*  259 */       this.delegate.moveDown();
/*      */       
/*  261 */       return null;
/*      */     }
/*  263 */     if (method.equals("moveTo[int]"))
/*      */     {
/*  265 */       int p = ((Integer)request.getParams()[0]).intValue();
/*      */       
/*  267 */       this.delegate.setPosition(p);
/*      */       
/*  269 */       return null;
/*      */     }
/*  271 */     if (method.equals("setPriority[int]"))
/*      */     {
/*  273 */       this.delegate.setPriority(((Integer)request.getParams()[0]).intValue());
/*      */       
/*  275 */       return null;
/*      */     }
/*  277 */     if (method.equals("requestTrackerAnnounce"))
/*      */     {
/*  279 */       this.delegate.requestTrackerAnnounce();
/*      */       
/*  281 */       return null;
/*      */     }
/*  283 */     if (method.equals("getDiskManagerFileInfo"))
/*      */     {
/*  285 */       DiskManagerFileInfo[] info = this.delegate.getDiskManagerFileInfo();
/*      */       
/*  287 */       RPDiskManagerFileInfo[] rp_info = new RPDiskManagerFileInfo[info.length];
/*      */       
/*  289 */       for (int i = 0; i < rp_info.length; i++)
/*      */       {
/*  291 */         rp_info[i] = RPDiskManagerFileInfo.create(info[i]);
/*      */       }
/*      */       
/*  294 */       return new RPReply(rp_info);
/*      */     }
/*      */     
/*  297 */     throw new RPException("Unknown method: " + method);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getState()
/*      */   {
/*  305 */     notSupported();
/*      */     
/*  307 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSubState()
/*      */   {
/*  313 */     notSupported();
/*      */     
/*  315 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getErrorStateDetails()
/*      */   {
/*  321 */     notSupported();
/*      */     
/*  323 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getFlag(long flag)
/*      */   {
/*  330 */     notSupported();
/*      */     
/*  332 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getFlags()
/*      */   {
/*  338 */     notSupported();
/*      */     
/*  340 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getIndex()
/*      */   {
/*  346 */     notSupported();
/*      */     
/*  348 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public Torrent getTorrent()
/*      */   {
/*  354 */     return this.torrent;
/*      */   }
/*      */   
/*      */   public byte[] getDownloadPeerId()
/*      */   {
/*  359 */     return this.delegate.getDownloadPeerId();
/*      */   }
/*      */   
/*      */   public boolean isMessagingEnabled() {
/*  363 */     return this.delegate.isMessagingEnabled();
/*      */   }
/*      */   
/*  366 */   public void setMessagingEnabled(boolean enabled) { this.delegate.setMessagingEnabled(enabled); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initialize()
/*      */     throws DownloadException
/*      */   {
/*      */     try
/*      */     {
/*  377 */       this._dispatcher.dispatch(new RPRequest(this, "initialize", null)).getResponse();
/*      */     }
/*      */     catch (RPException e)
/*      */     {
/*  381 */       if ((e.getCause() instanceof DownloadException))
/*      */       {
/*  383 */         throw ((DownloadException)e.getCause());
/*      */       }
/*      */       
/*  386 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void start()
/*      */     throws DownloadException
/*      */   {
/*      */     try
/*      */     {
/*  396 */       this._dispatcher.dispatch(new RPRequest(this, "start", null)).getResponse();
/*      */     }
/*      */     catch (RPException e)
/*      */     {
/*  400 */       if ((e.getCause() instanceof DownloadException))
/*      */       {
/*  402 */         throw ((DownloadException)e.getCause());
/*      */       }
/*      */       
/*  405 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void stop()
/*      */     throws DownloadException
/*      */   {
/*      */     try
/*      */     {
/*  415 */       this._dispatcher.dispatch(new RPRequest(this, "stop", null)).getResponse();
/*      */     }
/*      */     catch (RPException e)
/*      */     {
/*  419 */       if ((e.getCause() instanceof DownloadException))
/*      */       {
/*  421 */         throw ((DownloadException)e.getCause());
/*      */       }
/*      */       
/*  424 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void restart()
/*      */     throws DownloadException
/*      */   {
/*      */     try
/*      */     {
/*  434 */       this._dispatcher.dispatch(new RPRequest(this, "restart", null)).getResponse();
/*      */     }
/*      */     catch (RPException e)
/*      */     {
/*  438 */       if ((e.getCause() instanceof DownloadException))
/*      */       {
/*  440 */         throw ((DownloadException)e.getCause());
/*      */       }
/*      */       
/*  443 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isStartStopLocked()
/*      */   {
/*  450 */     notSupported();
/*      */     
/*  452 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPaused()
/*      */   {
/*  458 */     notSupported();
/*      */     
/*  460 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void pause()
/*      */   {
/*  466 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */   public void resume()
/*      */   {
/*  472 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPriority()
/*      */   {
/*  479 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPriority(int priority) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public boolean isPriorityLocked()
/*      */   {
/*  498 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void remove()
/*      */     throws DownloadException, DownloadRemovalVetoException
/*      */   {
/*      */     try
/*      */     {
/*  507 */       this._dispatcher.dispatch(new RPRequest(this, "remove", null)).getResponse();
/*      */     }
/*      */     catch (RPException e)
/*      */     {
/*  511 */       Throwable cause = e.getCause();
/*      */       
/*  513 */       if ((cause instanceof DownloadException))
/*      */       {
/*  515 */         throw ((DownloadException)cause);
/*      */       }
/*      */       
/*  518 */       if ((cause instanceof DownloadRemovalVetoException))
/*      */       {
/*  520 */         throw ((DownloadRemovalVetoException)cause);
/*      */       }
/*      */       
/*  523 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void remove(boolean delete_torrent, boolean delete_data)
/*      */     throws DownloadException, DownloadRemovalVetoException
/*      */   {
/*  534 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canBeRemoved()
/*      */     throws DownloadRemovalVetoException
/*      */   {
/*  542 */     notSupported();
/*      */     
/*  544 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadAnnounceResult getLastAnnounceResult()
/*      */   {
/*  550 */     return this.announce_result;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadScrapeResult getLastScrapeResult()
/*      */   {
/*  556 */     return this.scrape_result;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadScrapeResult getAggregatedScrapeResult()
/*      */   {
/*  562 */     notSupported();
/*      */     
/*  564 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadStats getStats()
/*      */   {
/*  570 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DownloadListener l)
/*      */   {
/*  577 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DownloadListener l)
/*      */   {
/*  584 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPropertyListener(DownloadPropertyListener l)
/*      */   {
/*  591 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePropertyListener(DownloadPropertyListener l)
/*      */   {
/*  598 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTrackerListener(DownloadTrackerListener l)
/*      */   {
/*  605 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeTrackerListener(DownloadTrackerListener l)
/*      */   {
/*  612 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l)
/*      */   {
/*  619 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l)
/*      */   {
/*  626 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPosition()
/*      */   {
/*  632 */     return this.position;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isForceStart()
/*      */   {
/*  638 */     return this.force_start;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setForceStart(boolean _force_start)
/*      */   {
/*  645 */     this.force_start = _force_start;
/*      */     
/*  647 */     this._dispatcher.dispatch(new RPRequest(this, "setForceStart[boolean]", new Object[] { Boolean.valueOf(this.force_start) })).getResponse();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPosition(int new_position)
/*      */   {
/*  654 */     this._dispatcher.dispatch(new RPRequest(this, "setPosition[int]", new Object[] { new Integer(new_position) })).getResponse();
/*      */   }
/*      */   
/*      */ 
/*      */   public void moveUp()
/*      */   {
/*  660 */     this._dispatcher.dispatch(new RPRequest(this, "moveUp", null)).getResponse();
/*      */   }
/*      */   
/*      */ 
/*      */   public void moveDown()
/*      */   {
/*  666 */     this._dispatcher.dispatch(new RPRequest(this, "moveDown", null)).getResponse();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveTo(int position)
/*      */   {
/*  673 */     this._dispatcher.dispatch(new RPRequest(this, "moveTo[int]", new Object[] { new Integer(position) })).getResponse();
/*      */   }
/*      */   
/*      */   public void stopAndQueue() throws DownloadException {
/*  677 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void recheckData()
/*      */     throws DownloadException
/*      */   {
/*  685 */     notSupported();
/*      */   }
/*      */   
/*      */   public String getName() {
/*  689 */     notSupported();
/*  690 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DownloadPeerListener l)
/*      */   {
/*  697 */     notSupported(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeListener(DownloadPeerListener l)
/*      */   {
/*  705 */     notSupported(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPeerListener(DownloadPeerListener l)
/*      */   {
/*  712 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePeerListener(DownloadPeerListener l)
/*      */   {
/*  719 */     notSupported();
/*      */   }
/*      */   
/*      */   public String getTorrentFileName() {
/*  723 */     notSupported();
/*  724 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getAttribute(TorrentAttribute attribute)
/*      */   {
/*  731 */     notSupported();
/*  732 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAttribute(TorrentAttribute attribute, String value)
/*      */   {
/*  740 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getListAttribute(TorrentAttribute attribute)
/*      */   {
/*  747 */     notSupported();
/*  748 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMapAttribute(TorrentAttribute attribute, Map value)
/*      */   {
/*  756 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map getMapAttribute(TorrentAttribute attribute)
/*      */   {
/*  763 */     notSupported();
/*  764 */     return null;
/*      */   }
/*      */   
/*      */   public String getCategoryName() {
/*  768 */     notSupported();
/*  769 */     return "";
/*      */   }
/*      */   
/*      */   public void setCategory(String sName) {
/*  773 */     notSupported();
/*      */   }
/*      */   
/*      */   public List<Tag> getTags() {
/*  777 */     notSupported();
/*  778 */     return null;
/*      */   }
/*      */   
/*      */   public boolean isPersistent()
/*      */   {
/*  783 */     notSupported();
/*  784 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaximumDownloadKBPerSecond(int kb)
/*      */   {
/*  791 */     notSupported();
/*      */   }
/*      */   
/*      */   public int getUploadRateLimitBytesPerSecond() {
/*  795 */     notSupported();
/*  796 */     return 0;
/*      */   }
/*      */   
/*  799 */   public void setUploadRateLimitBytesPerSecond(int max_rate_bps) { notSupported(); }
/*      */   
/*      */   public int getDownloadRateLimitBytesPerSecond() {
/*  802 */     notSupported();
/*  803 */     return 0;
/*      */   }
/*      */   
/*      */   public void setDownloadRateLimitBytesPerSecond(int max_rate_bps) {
/*  807 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaximumDownloadKBPerSecond()
/*      */   {
/*  813 */     notSupported();
/*      */     
/*  815 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */   {
/*  823 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */   {
/*  831 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isComplete()
/*      */   {
/*  837 */     notSupported();
/*      */     
/*  839 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isComplete(boolean b)
/*      */   {
/*  845 */     notSupported();
/*      */     
/*  847 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isChecking()
/*      */   {
/*  853 */     notSupported();
/*      */     
/*  855 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isMoving()
/*      */   {
/*  861 */     notSupported();
/*      */     
/*  863 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public PeerManager getPeerManager()
/*      */   {
/*  869 */     notSupported();
/*      */     
/*  871 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public DiskManager getDiskManager()
/*      */   {
/*  877 */     notSupported();
/*      */     
/*  879 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DiskManagerFileInfo[] getDiskManagerFileInfo()
/*      */   {
/*  886 */     RPDiskManagerFileInfo[] resp = (RPDiskManagerFileInfo[])this._dispatcher.dispatch(new RPRequest(this, "getDiskManagerFileInfo", null)).getResponse();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  892 */     for (int i = 0; i < resp.length; i++)
/*      */     {
/*  894 */       resp[i]._setRemote(this._dispatcher);
/*      */     }
/*      */     
/*  897 */     return resp;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DiskManagerFileInfo getDiskManagerFileInfo(int index)
/*      */   {
/*  905 */     RPDiskManagerFileInfo[] resp = (RPDiskManagerFileInfo[])this._dispatcher.dispatch(new RPRequest(this, "getDiskManagerFileInfo", null)).getResponse();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  911 */     if ((index >= 0) && (index < resp.length)) {
/*  912 */       resp[index]._setRemote(this._dispatcher);
/*  913 */       return resp[index];
/*      */     }
/*      */     
/*  916 */     return null;
/*      */   }
/*      */   
/*      */   public int getDiskManagerFileCount()
/*      */   {
/*  921 */     notSupported();
/*  922 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCreationTime()
/*      */   {
/*  928 */     notSupported();
/*      */     
/*  930 */     return 0L;
/*      */   }
/*      */   
/*      */   public int getSeedingRank() {
/*  934 */     notSupported();
/*      */     
/*  936 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSavePath()
/*      */   {
/*  942 */     notSupported();
/*      */     
/*  944 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void moveDataFiles(File new_parent_dir)
/*      */     throws DownloadException
/*      */   {
/*  953 */     notSupported();
/*      */   }
/*      */   
/*      */   public void moveDataFiles(File new_parent_dir, String new_name) throws DownloadException {
/*  957 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveTorrentFile(File new_parent_dir)
/*      */   {
/*  964 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */   public void requestTrackerAnnounce()
/*      */   {
/*  970 */     this._dispatcher.dispatch(new RPRequest(this, "requestTrackerAnnounce", null)).getResponse();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestTrackerAnnounce(boolean immediate)
/*      */   {
/*  977 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestTrackerScrape(boolean immediate)
/*      */   {
/*  984 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAnnounceResult(DownloadAnnounceResult result)
/*      */   {
/*  991 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setScrapeResult(DownloadScrapeResult result)
/*      */   {
/*  998 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadActivationEvent getActivationState()
/*      */   {
/* 1004 */     notSupported();
/*      */     
/* 1006 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addActivationListener(DownloadActivationListener l)
/*      */   {
/* 1013 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeActivationListener(DownloadActivationListener l)
/*      */   {
/* 1020 */     notSupported();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSeedingRank(int rank) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTrackerListener(DownloadTrackerListener l, boolean immediateTrigger)
/*      */   {
/* 1032 */     notSupported();
/*      */   }
/*      */   
/*      */   public void renameDownload(String new_name) {
/* 1036 */     notSupported();
/*      */   }
/*      */   
/*      */   public File[] calculateDefaultPaths(boolean for_moving) {
/* 1040 */     notSupported();
/* 1041 */     return null;
/*      */   }
/*      */   
/* 1044 */   public boolean isInDefaultSaveDir() { notSupported();return false; }
/*      */   
/* 1046 */   public boolean getBooleanAttribute(TorrentAttribute ta) { notSupported();return false; }
/* 1047 */   public int getIntAttribute(TorrentAttribute ta) { notSupported();return 0; }
/* 1048 */   public long getLongAttribute(TorrentAttribute ta) { notSupported();return 0L; }
/* 1049 */   public boolean hasAttribute(TorrentAttribute ta) { notSupported();return false; }
/* 1050 */   public void setBooleanAttribute(TorrentAttribute ta, boolean value) { notSupported(); }
/* 1051 */   public void setIntAttribute(TorrentAttribute ta, int value) { notSupported(); }
/* 1052 */   public void setListAttribute(TorrentAttribute ta, String[] value) { notSupported(); }
/* 1053 */   public void setLongAttribute(TorrentAttribute ta, long value) { notSupported(); }
/* 1054 */   public void setFlag(long flag, boolean set) { notSupported(); }
/*      */   
/* 1056 */   public void addAttributeListener(DownloadAttributeListener l, TorrentAttribute a, int e) { notSupported(); }
/* 1057 */   public void removeAttributeListener(DownloadAttributeListener l, TorrentAttribute a, int e) { notSupported(); }
/*      */   
/* 1059 */   public void addCompletionListener(DownloadCompletionListener l) { notSupported(); }
/* 1060 */   public void removeCompletionListener(DownloadCompletionListener l) { notSupported(); }
/*      */   
/* 1062 */   public boolean isRemoved() { notSupported();return false; }
/* 1063 */   public boolean canMoveDataFiles() { notSupported();return false; }
/* 1064 */   public SaveLocationChange calculateDefaultDownloadLocation() { notSupported();return null;
/*      */   }
/*      */   
/* 1067 */   public Object getUserData(Object key) { notSupported();
/* 1068 */     return null;
/*      */   }
/*      */   
/*      */   public void setUserData(Object key, Object data) {
/* 1072 */     notSupported();
/*      */   }
/*      */   
/* 1075 */   public void startDownload(boolean force) { notSupported(); }
/* 1076 */   public void stopDownload() { notSupported(); }
/* 1077 */   public void changeLocation(SaveLocationChange slc) { notSupported(); }
/*      */   
/*      */ 
/*      */   public boolean isStub()
/*      */   {
/* 1082 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canStubbify()
/*      */   {
/* 1088 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadStub stubbify()
/*      */     throws DownloadException, DownloadRemovalVetoException
/*      */   {
/* 1096 */     throw new DownloadException("Not Supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Download destubbify()
/*      */     throws DownloadException
/*      */   {
/* 1104 */     throw new DownloadException("Not Supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public List<DistributedDatabase> getDistributedDatabases()
/*      */   {
/* 1110 */     notSupported();
/* 1111 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getTorrentHash()
/*      */   {
/* 1117 */     notSupported();
/*      */     
/* 1119 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTorrentSize()
/*      */   {
/* 1125 */     notSupported();
/*      */     
/* 1127 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadStub.DownloadStubFile[] getStubFiles()
/*      */   {
/* 1133 */     notSupported();
/*      */     
/* 1135 */     return null;
/*      */   }
/*      */   
/*      */   public DiskManagerFileInfo getPrimaryFile()
/*      */   {
/* 1140 */     return getDiskManagerFileInfo(0);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/download/RPDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */