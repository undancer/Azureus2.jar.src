/*      */ package org.gudy.azureus2.pluginsimpl.local.download;
/*      */ 
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteMap;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerActivationListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerException;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateEvent;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerTrackerListener;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerMoveHandler;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerDownloadRemovalVetoException;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogRelation;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadActivationEvent;
/*      */ import org.gudy.azureus2.plugins.download.DownloadActivationListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadPropertyEvent;
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
/*      */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.deprecate.PluginDeprecation;
/*      */ import org.gudy.azureus2.pluginsimpl.local.disk.DiskManagerFileInfoImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.peers.PeerManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DownloadImpl
/*      */   extends LogRelation
/*      */   implements Download, DownloadManagerListener, DownloadManagerTrackerListener, DownloadManagerStateListener, DownloadManagerActivationListener, DownloadManagerStateAttributeListener
/*      */ {
/*      */   private final DownloadManagerImpl manager;
/*      */   private final DownloadManager download_manager;
/*      */   private final DownloadStatsImpl download_stats;
/*   92 */   private int latest_state = 7;
/*      */   
/*      */   private boolean latest_forcedStart;
/*   95 */   private DownloadAnnounceResultImpl last_announce_result = new DownloadAnnounceResultImpl(this, null);
/*   96 */   private DownloadScrapeResultImpl last_scrape_result = new DownloadScrapeResultImpl(this, null);
/*   97 */   private AggregateScrapeResult last_aggregate_scrape = new AggregateScrapeResult(this, null);
/*      */   
/*   99 */   private TorrentImpl torrent = null;
/*      */   
/*  101 */   private List listeners = new ArrayList();
/*  102 */   private AEMonitor listeners_mon = new AEMonitor("Download:L");
/*  103 */   private List property_listeners = new ArrayList();
/*  104 */   private List tracker_listeners = new ArrayList();
/*  105 */   private AEMonitor tracker_listeners_mon = new AEMonitor("Download:TL");
/*  106 */   private List removal_listeners = new ArrayList();
/*  107 */   private AEMonitor removal_listeners_mon = new AEMonitor("Download:RL");
/*  108 */   private Map peer_listeners = new HashMap();
/*  109 */   private AEMonitor peer_listeners_mon = new AEMonitor("Download:PL");
/*      */   
/*  111 */   private CopyOnWriteList completion_listeners = new CopyOnWriteList();
/*      */   
/*  113 */   private CopyOnWriteMap read_attribute_listeners_map_cow = new CopyOnWriteMap();
/*  114 */   private CopyOnWriteMap write_attribute_listeners_map_cow = new CopyOnWriteMap();
/*      */   
/*  116 */   private CopyOnWriteList activation_listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */   private DownloadActivationEvent activation_state;
/*      */   
/*      */ 
/*      */   private Map<String, int[]> announce_response_map;
/*      */   
/*      */ 
/*      */   protected DownloadImpl(DownloadManagerImpl _manager, DownloadManager _dm)
/*      */   {
/*  127 */     this.manager = _manager;
/*  128 */     this.download_manager = _dm;
/*  129 */     this.download_stats = new DownloadStatsImpl(this.download_manager);
/*      */     
/*  131 */     this.activation_state = new DownloadActivationEvent()
/*      */     {
/*      */ 
/*      */       public Download getDownload()
/*      */       {
/*      */ 
/*  137 */         return DownloadImpl.this;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getActivationCount()
/*      */       {
/*  143 */         return DownloadImpl.this.download_manager.getActivationCount();
/*      */       }
/*      */       
/*  146 */     };
/*  147 */     this.download_manager.addListener(this);
/*      */     
/*  149 */     this.latest_forcedStart = this.download_manager.isForceStart();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadManager getDownload()
/*      */   {
/*  156 */     return this.download_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getState()
/*      */   {
/*  162 */     return convertState(this.download_manager.getState());
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSubState()
/*      */   {
/*  168 */     int state = getState();
/*      */     
/*  170 */     if (state == 6)
/*      */     {
/*  172 */       int substate = this.download_manager.getSubState();
/*      */       
/*  174 */       if (substate == 75)
/*      */       {
/*  176 */         return 9;
/*      */       }
/*  178 */       if (substate == 70)
/*      */       {
/*  180 */         return 7;
/*      */       }
/*  182 */       if (substate == 100)
/*      */       {
/*  184 */         return 8;
/*      */       }
/*      */     }
/*      */     
/*  188 */     return state;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int convertState(int dm_state)
/*      */   {
/*      */     int our_state;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  206 */     switch (dm_state)
/*      */     {
/*      */     case 0: 
/*  209 */       our_state = 1;
/*      */       
/*  211 */       break;
/*      */     
/*      */ 
/*      */     case 5: 
/*      */     case 10: 
/*      */     case 20: 
/*      */     case 30: 
/*  218 */       our_state = 2;
/*      */       
/*  220 */       break;
/*      */     
/*      */ 
/*      */     case 40: 
/*  224 */       our_state = 3;
/*      */       
/*  226 */       break;
/*      */     
/*      */ 
/*      */     case 50: 
/*      */     case 55: 
/*  231 */       our_state = 4;
/*      */       
/*  233 */       break;
/*      */     
/*      */ 
/*      */     case 60: 
/*  237 */       our_state = 5;
/*      */       
/*  239 */       break;
/*      */     
/*      */ 
/*      */     case 65: 
/*  243 */       our_state = 6;
/*      */       
/*  245 */       break;
/*      */     
/*      */ 
/*      */     case 70: 
/*  249 */       our_state = 7;
/*      */       
/*  251 */       break;
/*      */     
/*      */ 
/*      */     case 75: 
/*  255 */       our_state = 9;
/*      */       
/*  257 */       break;
/*      */     
/*      */ 
/*      */     case 100: 
/*  261 */       our_state = 8;
/*      */       
/*  263 */       break;
/*      */     
/*      */ 
/*      */     default: 
/*  267 */       our_state = 8;
/*      */     }
/*      */     
/*      */     
/*  271 */     return our_state;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getErrorStateDetails()
/*      */   {
/*  277 */     return this.download_manager.getErrorDetails();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getFlags()
/*      */   {
/*  283 */     return this.download_manager.getDownloadState().getFlags();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getFlag(long flag)
/*      */   {
/*  290 */     return this.download_manager.getDownloadState().getFlag(flag);
/*      */   }
/*      */   
/*      */   public void setFlag(long flag, boolean set) {
/*  294 */     this.download_manager.getDownloadState().setFlag(flag, set);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getIndex()
/*      */   {
/*  300 */     GlobalManager globalManager = this.download_manager.getGlobalManager();
/*  301 */     return globalManager.getIndexOf(this.download_manager);
/*      */   }
/*      */   
/*      */ 
/*      */   public Torrent getTorrent()
/*      */   {
/*  307 */     if (this.torrent != null) { return this.torrent;
/*      */     }
/*  309 */     TOTorrent torrent = this.download_manager.getTorrent();
/*  310 */     if (torrent == null) return null;
/*  311 */     this.torrent = new TorrentImpl(torrent);
/*  312 */     return this.torrent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialize()
/*      */     throws DownloadException
/*      */   {
/*  320 */     int state = this.download_manager.getState();
/*      */     
/*  322 */     if (state == 0)
/*      */     {
/*  324 */       this.download_manager.initialize();
/*      */     }
/*      */     else
/*      */     {
/*  328 */       throw new DownloadException("Download::initialize: download not waiting (state=" + state + ")");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void start()
/*      */     throws DownloadException
/*      */   {
/*  337 */     int state = this.download_manager.getState();
/*      */     
/*  339 */     if (state == 40)
/*      */     {
/*  341 */       this.download_manager.startDownload();
/*      */     }
/*      */     else
/*      */     {
/*  345 */       throw new DownloadException("Download::start: download not ready (state=" + state + ")");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void restart()
/*      */     throws DownloadException
/*      */   {
/*  354 */     int state = this.download_manager.getState();
/*      */     
/*  356 */     if ((state == 70) || (state == 75))
/*      */     {
/*      */ 
/*  359 */       this.download_manager.setStateWaiting();
/*      */     }
/*      */     else
/*      */     {
/*  363 */       throw new DownloadException("Download::restart: download already running (state=" + state + ")");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stop()
/*      */     throws DownloadException
/*      */   {
/*  372 */     if (this.download_manager.getState() != 70)
/*      */     {
/*  374 */       this.download_manager.stopIt(70, false, false);
/*      */     }
/*      */     else
/*      */     {
/*  378 */       throw new DownloadException("Download::stop: download already stopped");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stopAndQueue()
/*      */     throws DownloadException
/*      */   {
/*  387 */     if (this.download_manager.getState() != 75)
/*      */     {
/*  389 */       this.download_manager.stopIt(75, false, false);
/*      */     }
/*      */     else
/*      */     {
/*  393 */       throw new DownloadException("Download::stopAndQueue: download already queued");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void recheckData()
/*      */     throws DownloadException
/*      */   {
/*  402 */     if (!this.download_manager.canForceRecheck())
/*      */     {
/*  404 */       throw new DownloadException("Download::recheckData: download must be stopped, queued or in error state");
/*      */     }
/*      */     
/*  407 */     this.download_manager.forceRecheck();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isStartStopLocked()
/*      */   {
/*  413 */     return this.download_manager.getState() == 70;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isForceStart()
/*      */   {
/*  419 */     return this.download_manager.isForceStart();
/*      */   }
/*      */   
/*      */ 
/*      */   public void setForceStart(boolean forceStart)
/*      */   {
/*  425 */     this.download_manager.setForceStart(forceStart);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPaused()
/*      */   {
/*  431 */     return this.download_manager.isPaused();
/*      */   }
/*      */   
/*      */ 
/*      */   public void pause()
/*      */   {
/*  437 */     this.download_manager.pause();
/*      */   }
/*      */   
/*      */ 
/*      */   public void resume()
/*      */   {
/*  443 */     this.download_manager.resume();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPosition()
/*      */   {
/*  449 */     return this.download_manager.getPosition();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCreationTime()
/*      */   {
/*  455 */     return this.download_manager.getCreationTime();
/*      */   }
/*      */   
/*      */ 
/*      */   public void setPosition(int newPosition)
/*      */   {
/*  461 */     this.download_manager.setPosition(newPosition);
/*      */   }
/*      */   
/*      */ 
/*      */   public void moveUp()
/*      */   {
/*  467 */     this.download_manager.getGlobalManager().moveUp(this.download_manager);
/*      */   }
/*      */   
/*      */ 
/*      */   public void moveDown()
/*      */   {
/*  473 */     this.download_manager.getGlobalManager().moveDown(this.download_manager);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveTo(int pos)
/*      */   {
/*  480 */     this.download_manager.getGlobalManager().moveTo(this.download_manager, pos);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  486 */     return this.download_manager.getDisplayName();
/*      */   }
/*      */   
/*      */   public String getTorrentFileName() {
/*  490 */     return this.download_manager.getTorrentFileName();
/*      */   }
/*      */   
/*      */   public String getCategoryName() {
/*  494 */     Category category = this.download_manager.getDownloadState().getCategory();
/*  495 */     if (category == null) {
/*  496 */       category = CategoryManager.getCategory(2);
/*      */     }
/*  498 */     if (category == null)
/*  499 */       return null;
/*  500 */     return category.getName();
/*      */   }
/*      */   
/*      */   public List<Tag> getTags() {
/*  504 */     return new ArrayList(TagManagerFactory.getTagManager().getTagsForTaggable(this.download_manager));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getAttribute(TorrentAttribute attribute)
/*      */   {
/*  511 */     String name = convertAttribute(attribute);
/*      */     
/*  513 */     if (name != null)
/*      */     {
/*  515 */       return this.download_manager.getDownloadState().getAttribute(name);
/*      */     }
/*      */     
/*  518 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getListAttribute(TorrentAttribute attribute)
/*      */   {
/*  525 */     String name = convertAttribute(attribute);
/*      */     
/*  527 */     if (name != null)
/*      */     {
/*  529 */       return this.download_manager.getDownloadState().getListAttribute(name);
/*      */     }
/*      */     
/*  532 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setListAttribute(TorrentAttribute attribute, String[] value)
/*      */   {
/*  540 */     String name = convertAttribute(attribute);
/*      */     
/*  542 */     if (name != null) {
/*  543 */       this.download_manager.getDownloadState().setListAttribute(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMapAttribute(TorrentAttribute attribute, Map value)
/*      */   {
/*  552 */     String name = convertAttribute(attribute);
/*      */     
/*  554 */     if (name != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  559 */       this.download_manager.getDownloadState().setMapAttribute(name, BEncoder.cloneMap(value));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map getMapAttribute(TorrentAttribute attribute)
/*      */   {
/*  567 */     String name = convertAttribute(attribute);
/*      */     
/*  569 */     if (name != null)
/*      */     {
/*  571 */       return this.download_manager.getDownloadState().getMapAttribute(name);
/*      */     }
/*      */     
/*  574 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAttribute(TorrentAttribute attribute, String value)
/*      */   {
/*  582 */     String name = convertAttribute(attribute);
/*      */     
/*  584 */     if (name != null)
/*      */     {
/*  586 */       this.download_manager.getDownloadState().setAttribute(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean hasAttribute(TorrentAttribute attribute) {
/*  591 */     String name = convertAttribute(attribute);
/*  592 */     if (name == null) return false;
/*  593 */     return this.download_manager.getDownloadState().hasAttribute(name);
/*      */   }
/*      */   
/*      */   public boolean getBooleanAttribute(TorrentAttribute attribute) {
/*  597 */     String name = convertAttribute(attribute);
/*  598 */     if (name == null) return false;
/*  599 */     return this.download_manager.getDownloadState().getBooleanAttribute(name);
/*      */   }
/*      */   
/*      */   public void setBooleanAttribute(TorrentAttribute attribute, boolean value) {
/*  603 */     String name = convertAttribute(attribute);
/*  604 */     if (name != null) {
/*  605 */       this.download_manager.getDownloadState().setBooleanAttribute(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getIntAttribute(TorrentAttribute attribute) {
/*  610 */     String name = convertAttribute(attribute);
/*  611 */     if (name == null) return 0;
/*  612 */     return this.download_manager.getDownloadState().getIntAttribute(name);
/*      */   }
/*      */   
/*      */   public void setIntAttribute(TorrentAttribute attribute, int value) {
/*  616 */     String name = convertAttribute(attribute);
/*  617 */     if (name != null) {
/*  618 */       this.download_manager.getDownloadState().setIntAttribute(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */   public long getLongAttribute(TorrentAttribute attribute) {
/*  623 */     String name = convertAttribute(attribute);
/*  624 */     if (name == null) return 0L;
/*  625 */     return this.download_manager.getDownloadState().getLongAttribute(name);
/*      */   }
/*      */   
/*      */   public void setLongAttribute(TorrentAttribute attribute, long value) {
/*  629 */     String name = convertAttribute(attribute);
/*  630 */     if (name != null) {
/*  631 */       this.download_manager.getDownloadState().setLongAttribute(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String convertAttribute(TorrentAttribute attribute)
/*      */   {
/*  639 */     if (attribute.getName() == "Category")
/*      */     {
/*  641 */       return "category";
/*      */     }
/*  643 */     if (attribute.getName() == "Networks")
/*      */     {
/*  645 */       return "networks";
/*      */     }
/*  647 */     if (attribute.getName() == "TrackerClientExtensions")
/*      */     {
/*  649 */       return "trackerclientextensions";
/*      */     }
/*  651 */     if (attribute.getName() == "PeerSources")
/*      */     {
/*  653 */       return "peersources";
/*      */     }
/*  655 */     if (attribute.getName() == "DisplayName")
/*      */     {
/*  657 */       return "displayname";
/*      */     }
/*  659 */     if (attribute.getName() == "UserComment")
/*      */     {
/*  661 */       return "comment";
/*      */     }
/*  663 */     if (attribute.getName() == "RelativePath")
/*      */     {
/*  665 */       return "relativepath";
/*      */     }
/*  667 */     if (attribute.getName() == "ShareProperties")
/*      */     {
/*      */ 
/*      */ 
/*  671 */       return null;
/*      */     }
/*  673 */     if (attribute.getName().startsWith("Plugin."))
/*      */     {
/*  675 */       return attribute.getName();
/*      */     }
/*      */     
/*      */ 
/*  679 */     Debug.out("Can't convert attribute '" + attribute.getName() + "'");
/*      */     
/*  681 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TorrentAttribute convertAttribute(String name)
/*      */   {
/*  689 */     if (name.equals("category"))
/*      */     {
/*  691 */       return TorrentManagerImpl.getSingleton().getAttribute("Category");
/*      */     }
/*  693 */     if (name.equals("networks"))
/*      */     {
/*  695 */       return TorrentManagerImpl.getSingleton().getAttribute("Networks");
/*      */     }
/*  697 */     if (name.equals("peersources"))
/*      */     {
/*  699 */       return TorrentManagerImpl.getSingleton().getAttribute("PeerSources");
/*      */     }
/*  701 */     if (name.equals("trackerclientextensions"))
/*      */     {
/*  703 */       return TorrentManagerImpl.getSingleton().getAttribute("TrackerClientExtensions");
/*      */     }
/*  705 */     if (name.equals("displayname"))
/*      */     {
/*  707 */       return TorrentManagerImpl.getSingleton().getAttribute("DisplayName");
/*      */     }
/*  709 */     if (name.equals("comment"))
/*      */     {
/*  711 */       return TorrentManagerImpl.getSingleton().getAttribute("UserComment");
/*      */     }
/*  713 */     if (name.equals("relativepath"))
/*      */     {
/*  715 */       return TorrentManagerImpl.getSingleton().getAttribute("RelativePath");
/*      */     }
/*  717 */     if (name.startsWith("Plugin."))
/*      */     {
/*  719 */       return TorrentManagerImpl.getSingleton().getAttribute(name);
/*      */     }
/*      */     
/*      */ 
/*  723 */     return null;
/*      */   }
/*      */   
/*      */   public void setCategory(String sName)
/*      */   {
/*  728 */     Category category = CategoryManager.getCategory(sName);
/*  729 */     if (category == null)
/*  730 */       category = CategoryManager.createCategory(sName);
/*  731 */     this.download_manager.getDownloadState().setCategory(category);
/*      */   }
/*      */   
/*      */   public boolean isPersistent() {
/*  735 */     return this.download_manager.isPersistent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void remove()
/*      */     throws DownloadException, DownloadRemovalVetoException
/*      */   {
/*  743 */     remove(false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void remove(boolean delete_torrent, boolean delete_data)
/*      */     throws DownloadException, DownloadRemovalVetoException
/*      */   {
/*  753 */     int dl_state = this.download_manager.getState();
/*      */     
/*  755 */     if ((dl_state == 70) || (dl_state == 100) || (dl_state == 75))
/*      */     {
/*      */ 
/*      */ 
/*  759 */       GlobalManager globalManager = this.download_manager.getGlobalManager();
/*      */       
/*      */       try
/*      */       {
/*  763 */         globalManager.removeDownloadManager(this.download_manager, delete_torrent, delete_data);
/*      */       }
/*      */       catch (GlobalManagerDownloadRemovalVetoException e)
/*      */       {
/*  767 */         throw new DownloadRemovalVetoException(e.getMessage());
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  772 */       throw new DownloadRemovalVetoException(MessageText.getString("plugin.download.remove.veto.notstopped"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canBeRemoved()
/*      */     throws DownloadRemovalVetoException
/*      */   {
/*  781 */     int dl_state = this.download_manager.getState();
/*      */     
/*  783 */     if ((dl_state == 70) || (dl_state == 100) || (dl_state == 75))
/*      */     {
/*      */ 
/*      */ 
/*  787 */       GlobalManager globalManager = this.download_manager.getGlobalManager();
/*      */       try
/*      */       {
/*  790 */         globalManager.canDownloadManagerBeRemoved(this.download_manager, false, false);
/*      */       }
/*      */       catch (GlobalManagerDownloadRemovalVetoException e)
/*      */       {
/*  794 */         throw new DownloadRemovalVetoException(e.getMessage(), e.isSilent());
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  799 */       throw new DownloadRemovalVetoException(MessageText.getString("plugin.download.remove.veto.notstopped"));
/*      */     }
/*      */     
/*  802 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadStats getStats()
/*      */   {
/*  808 */     return this.download_stats;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isComplete()
/*      */   {
/*  814 */     return this.download_manager.isDownloadComplete(false);
/*      */   }
/*      */   
/*      */   public boolean isComplete(boolean bIncludeDND) {
/*  818 */     return this.download_manager.isDownloadComplete(bIncludeDND);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isChecking()
/*      */   {
/*  824 */     return this.download_stats.getCheckingDoneInThousandNotation() != -1;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isMoving()
/*      */   {
/*  830 */     org.gudy.azureus2.core3.disk.DiskManager dm = this.download_manager.getDiskManager();
/*      */     
/*  832 */     if (dm != null)
/*      */     {
/*  834 */       return dm.getMoveProgress() != -1;
/*      */     }
/*      */     
/*  837 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void isRemovable()
/*      */     throws DownloadRemovalVetoException
/*      */   {
/*  846 */     for (int i = 0; i < this.removal_listeners.size(); i++) {
/*      */       try
/*      */       {
/*  849 */         ((DownloadWillBeRemovedListener)this.removal_listeners.get(i)).downloadWillBeRemoved(this);
/*      */       }
/*      */       catch (DownloadRemovalVetoException e)
/*      */       {
/*  853 */         throw e;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  857 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*  865 */     this.download_manager.removeListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stateChanged(DownloadManager manager, int state)
/*      */   {
/*  876 */     int prev_state = this.latest_state;
/*      */     
/*  878 */     this.latest_state = convertState(state);
/*      */     
/*      */ 
/*      */ 
/*  882 */     boolean curr_forcedStart = isForceStart();
/*      */     
/*      */ 
/*  885 */     List listeners_to_use = this.listeners;
/*      */     
/*  887 */     if ((prev_state != this.latest_state) || (this.latest_forcedStart != curr_forcedStart))
/*      */     {
/*  889 */       this.latest_forcedStart = curr_forcedStart;
/*      */       
/*  891 */       for (int i = 0; i < listeners_to_use.size(); i++) {
/*      */         try
/*      */         {
/*  894 */           long startTime = SystemTime.getCurrentTime();
/*  895 */           DownloadListener listener = (DownloadListener)listeners_to_use.get(i);
/*      */           
/*  897 */           listener.stateChanged(this, prev_state, this.latest_state);
/*      */           
/*  899 */           long diff = SystemTime.getCurrentTime() - startTime;
/*  900 */           if (diff > 1000L) {
/*  901 */             System.out.println("Plugin should move long processes (" + diff + "ms) off of Download's stateChanged listener trigger. " + listener);
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  908 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void downloadComplete(DownloadManager manager)
/*      */   {
/*  917 */     if (this.completion_listeners.isEmpty()) return;
/*  918 */     Iterator itr = this.completion_listeners.iterator();
/*      */     
/*  920 */     while (itr.hasNext()) {
/*  921 */       DownloadCompletionListener dcl = (DownloadCompletionListener)itr.next();
/*  922 */       long startTime = SystemTime.getCurrentTime();
/*  923 */       try { dcl.onCompletion(this);
/*  924 */       } catch (Throwable t) { Debug.printStackTrace(t); }
/*  925 */       long diff = SystemTime.getCurrentTime() - startTime;
/*  926 */       if (diff > 1000L) {
/*  927 */         System.out.println("Plugin should move long processes (" + diff + "ms) off of Download's onCompletion listener trigger. " + dcl);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void completionChanged(DownloadManager manager, boolean bCompleted) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void filePriorityChanged(DownloadManager download, org.gudy.azureus2.core3.disk.DiskManagerFileInfo file) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void positionChanged(DownloadManager download, int oldPosition, int newPosition)
/*      */   {
/*  950 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try {
/*  952 */         long startTime = SystemTime.getCurrentTime();
/*  953 */         DownloadListener listener = (DownloadListener)this.listeners.get(i);
/*      */         
/*  955 */         listener.positionChanged(this, oldPosition, newPosition);
/*      */         
/*  957 */         long diff = SystemTime.getCurrentTime() - startTime;
/*  958 */         if (diff > 1000L) {
/*  959 */           System.out.println("Plugin should move long processes (" + diff + "ms) off of Download's positionChanged listener trigger. " + listener);
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  964 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addListener(DownloadListener l)
/*      */   {
/*      */     try
/*      */     {
/*  974 */       this.listeners_mon.enter();
/*      */       
/*  976 */       List new_listeners = new ArrayList(this.listeners);
/*      */       
/*  978 */       new_listeners.add(l);
/*      */       
/*  980 */       this.listeners = new_listeners;
/*      */     }
/*      */     finally {
/*  983 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DownloadListener l)
/*      */   {
/*      */     try
/*      */     {
/*  993 */       this.listeners_mon.enter();
/*      */       
/*  995 */       List new_listeners = new ArrayList(this.listeners);
/*      */       
/*  997 */       new_listeners.remove(l);
/*      */       
/*  999 */       this.listeners = new_listeners;
/*      */     }
/*      */     finally {
/* 1002 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addAttributeListener(DownloadAttributeListener listener, TorrentAttribute attr, int event_type) {
/* 1007 */     String attribute = convertAttribute(attr);
/* 1008 */     if (attribute == null) { return;
/*      */     }
/* 1010 */     CopyOnWriteMap attr_map = getAttributeMapForType(event_type);
/* 1011 */     CopyOnWriteList listener_list = (CopyOnWriteList)attr_map.get(attribute);
/* 1012 */     boolean add_self = false;
/*      */     
/* 1014 */     if (listener_list == null) {
/* 1015 */       listener_list = new CopyOnWriteList();
/* 1016 */       attr_map.put(attribute, listener_list);
/*      */     }
/* 1018 */     add_self = listener_list.isEmpty();
/*      */     
/* 1020 */     listener_list.add(listener);
/* 1021 */     if (add_self) {
/* 1022 */       this.download_manager.getDownloadState().addListener(this, attribute, event_type);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeAttributeListener(DownloadAttributeListener listener, TorrentAttribute attr, int event_type) {
/* 1027 */     String attribute = convertAttribute(attr);
/* 1028 */     if (attribute == null) { return;
/*      */     }
/* 1030 */     CopyOnWriteMap attr_map = getAttributeMapForType(event_type);
/* 1031 */     CopyOnWriteList listener_list = (CopyOnWriteList)attr_map.get(attribute);
/* 1032 */     boolean remove_self = false;
/*      */     
/* 1034 */     if (listener_list != null) {
/* 1035 */       listener_list.remove(listener);
/* 1036 */       remove_self = listener_list.isEmpty();
/*      */     }
/*      */     
/* 1039 */     if (remove_self) {
/* 1040 */       this.download_manager.getDownloadState().removeListener(this, attribute, event_type);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadAnnounceResult getLastAnnounceResult()
/*      */   {
/* 1048 */     TRTrackerAnnouncer tc = this.download_manager.getTrackerClient();
/*      */     
/* 1050 */     if (tc != null)
/*      */     {
/* 1052 */       this.last_announce_result.setContent(tc.getLastResponse());
/*      */     }
/*      */     
/* 1055 */     return this.last_announce_result;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadScrapeResult getLastScrapeResult()
/*      */   {
/* 1061 */     TRTrackerScraperResponse response = this.download_manager.getTrackerScrapeResponse();
/*      */     
/* 1063 */     if (response != null)
/*      */     {
/*      */ 
/*      */ 
/* 1067 */       if ((response.getStatus() == 1) || (response.getStatus() == 2))
/*      */       {
/* 1069 */         this.last_scrape_result.setContent(response);
/*      */       }
/*      */     }
/*      */     
/* 1073 */     return this.last_scrape_result;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadScrapeResult getAggregatedScrapeResult()
/*      */   {
/* 1079 */     DownloadScrapeResult result = getAggregatedScrapeResultSupport();
/*      */     
/* 1081 */     if (result != null)
/*      */     {
/* 1083 */       String cache = this.download_manager.getDownloadState().getAttribute("agsc");
/*      */       
/* 1085 */       boolean do_update = true;
/*      */       
/* 1087 */       long mins = SystemTime.getCurrentTime() / 60000L;
/*      */       
/* 1089 */       if (cache != null)
/*      */       {
/* 1091 */         String[] bits = cache.split(",");
/*      */         
/* 1093 */         if (bits.length == 3)
/*      */         {
/* 1095 */           long updated_mins = 0L;
/*      */           try
/*      */           {
/* 1098 */             updated_mins = Long.parseLong(bits[0]);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/*      */ 
/*      */ 
/* 1104 */           if (mins - updated_mins < 15L)
/*      */           {
/*      */ 
/* 1107 */             do_update = false;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1112 */       if (do_update)
/*      */       {
/* 1114 */         String str = mins + "," + result.getSeedCount() + "," + result.getNonSeedCount();
/*      */         
/* 1116 */         this.download_manager.getDownloadState().setAttribute("agsc", str);
/*      */       }
/*      */     }
/*      */     
/* 1120 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   private DownloadScrapeResult getAggregatedScrapeResultSupport()
/*      */   {
/* 1126 */     List<TRTrackerScraperResponse> responses = this.download_manager.getGoodTrackerScrapeResponses();
/*      */     
/* 1128 */     int best_peers = -1;
/* 1129 */     int best_seeds = -1;
/* 1130 */     int best_time = -1;
/*      */     
/* 1132 */     TRTrackerScraperResponse best_resp = null;
/*      */     
/* 1134 */     if (responses != null)
/*      */     {
/* 1136 */       for (TRTrackerScraperResponse response : responses)
/*      */       {
/* 1138 */         int peers = response.getPeers();
/* 1139 */         int seeds = response.getSeeds();
/*      */         
/* 1141 */         if ((peers > best_peers) || ((peers == best_peers) && (seeds > best_seeds)))
/*      */         {
/*      */ 
/* 1144 */           best_peers = peers;
/* 1145 */           best_seeds = seeds;
/*      */           
/* 1147 */           best_resp = response;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1154 */     if (best_peers == -1) {
/*      */       try
/*      */       {
/* 1157 */         TrackerPeerSource our_dht = null;
/*      */         
/* 1159 */         List<TrackerPeerSource> peer_sources = this.download_manager.getTrackerPeerSources();
/*      */         
/* 1161 */         for (TrackerPeerSource ps : peer_sources)
/*      */         {
/* 1163 */           if (ps.getType() == 3)
/*      */           {
/* 1165 */             our_dht = ps;
/*      */             
/* 1167 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1171 */         this.peer_listeners_mon.enter();
/*      */         
/* 1173 */         if (this.announce_response_map != null)
/*      */         {
/* 1175 */           int total_seeds = 0;
/* 1176 */           int total_peers = 0;
/* 1177 */           int latest_time = 0;
/*      */           
/* 1179 */           int num = 0;
/*      */           
/* 1181 */           if ((our_dht != null) && (our_dht.getStatus() == 5))
/*      */           {
/* 1183 */             total_seeds = our_dht.getSeedCount();
/* 1184 */             total_peers = our_dht.getLeecherCount();
/* 1185 */             latest_time = our_dht.getLastUpdate();
/*      */             
/* 1187 */             num = 1;
/*      */           }
/*      */           
/* 1190 */           for (int[] entry : this.announce_response_map.values())
/*      */           {
/* 1192 */             num++;
/*      */             
/* 1194 */             int seeds = entry[0];
/* 1195 */             int peers = entry[1];
/* 1196 */             int time = entry[3];
/*      */             
/* 1198 */             total_seeds += seeds;
/* 1199 */             total_peers += peers;
/*      */             
/* 1201 */             if (time > latest_time)
/*      */             {
/* 1203 */               latest_time = time;
/*      */             }
/*      */           }
/*      */           
/* 1207 */           if (total_peers >= 0)
/*      */           {
/* 1209 */             best_peers = Math.max(1, total_peers / num);
/* 1210 */             best_seeds = total_seeds / num;
/*      */             
/* 1212 */             if ((total_seeds > 0) && (best_seeds == 0))
/*      */             {
/* 1214 */               best_seeds = 1;
/*      */             }
/* 1216 */             best_time = latest_time;
/* 1217 */             best_resp = null;
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1223 */         this.peer_listeners_mon.exit();
/*      */       }
/*      */     }
/*      */     
/* 1227 */     if (best_peers >= 0)
/*      */     {
/*      */ 
/*      */ 
/* 1231 */       this.last_aggregate_scrape.update(best_resp, best_seeds, best_peers, best_time);
/*      */       
/* 1233 */       return this.last_aggregate_scrape;
/*      */     }
/*      */     
/*      */ 
/* 1237 */     return getLastScrapeResult();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void scrapeResult(TRTrackerScraperResponse response)
/*      */   {
/* 1246 */     if ((response.getStatus() != 1) && (response.getStatus() != 2)) {
/* 1247 */       return;
/*      */     }
/* 1249 */     this.last_scrape_result.setContent(response);
/*      */     
/* 1251 */     for (int i = 0; i < this.tracker_listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1254 */         ((DownloadTrackerListener)this.tracker_listeners.get(i)).scrapeResult(this.last_scrape_result);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1258 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   void announceTrackerResultsToListener(DownloadTrackerListener l)
/*      */   {
/* 1265 */     l.announceResult(this.last_announce_result);
/* 1266 */     l.scrapeResult(this.last_scrape_result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void announceResult(TRTrackerAnnouncerResponse response)
/*      */   {
/* 1273 */     this.last_announce_result.setContent(response);
/*      */     
/* 1275 */     List tracker_listeners_ref = this.tracker_listeners;
/*      */     
/* 1277 */     for (int i = 0; i < tracker_listeners_ref.size(); i++) {
/*      */       try
/*      */       {
/* 1280 */         ((DownloadTrackerListener)tracker_listeners_ref.get(i)).announceResult(this.last_announce_result);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1284 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TrackerPeerSource getTrackerPeerSource()
/*      */   {
/* 1292 */     new TrackerPeerSourceAdapter()
/*      */     {
/*      */       private long fixup;
/*      */       
/*      */       private int state;
/* 1297 */       private String details = "";
/*      */       
/*      */       private int seeds;
/*      */       private int leechers;
/*      */       private int peers;
/*      */       
/*      */       private void fixup()
/*      */       {
/* 1305 */         long now = SystemTime.getCurrentTime();
/*      */         
/* 1307 */         if (now - this.fixup > 1000L)
/*      */         {
/* 1309 */           if (!DownloadImpl.this.download_manager.getDownloadState().isPeerSourceEnabled("Plugin"))
/*      */           {
/* 1311 */             this.state = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1315 */             int s = DownloadImpl.this.getState();
/*      */             
/* 1317 */             if ((s == 4) || (s == 5))
/*      */             {
/* 1319 */               this.state = 5;
/*      */             }
/*      */             else
/*      */             {
/* 1323 */               this.state = 2;
/*      */             }
/*      */           }
/*      */           
/* 1327 */           if (this.state == 5) {
/*      */             try
/*      */             {
/* 1330 */               DownloadImpl.this.peer_listeners_mon.enter();
/*      */               
/* 1332 */               int s = 0;
/* 1333 */               int l = 0;
/* 1334 */               int p = 0;
/*      */               
/* 1336 */               String str = "";
/*      */               
/* 1338 */               if (DownloadImpl.this.announce_response_map != null)
/*      */               {
/* 1340 */                 for (Map.Entry<String, int[]> entry : DownloadImpl.this.announce_response_map.entrySet())
/*      */                 {
/* 1342 */                   String cn = (String)entry.getKey();
/* 1343 */                   int[] data = (int[])entry.getValue();
/*      */                   
/* 1345 */                   str = str + (str.length() == 0 ? "" : ", ") + cn;
/*      */                   
/* 1347 */                   str = str + " " + data[0] + "/" + data[1] + "/" + data[2];
/*      */                   
/* 1349 */                   s += data[0];
/* 1350 */                   l += data[1];
/* 1351 */                   p += data[2];
/*      */                 }
/*      */               }
/*      */               
/* 1355 */               this.details = str;
/*      */               
/* 1357 */               if (str.length() == 0)
/*      */               {
/* 1359 */                 this.seeds = -1;
/* 1360 */                 this.leechers = -1;
/* 1361 */                 this.peers = -1;
/*      */               }
/*      */               else
/*      */               {
/* 1365 */                 this.seeds = s;
/* 1366 */                 this.leechers = l;
/* 1367 */                 this.peers = p;
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/* 1372 */               DownloadImpl.this.peer_listeners_mon.exit();
/*      */             }
/*      */             
/*      */           } else {
/* 1376 */             this.details = "";
/*      */           }
/*      */           
/* 1379 */           this.fixup = now;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public int getType()
/*      */       {
/* 1386 */         return 7;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getStatus()
/*      */       {
/* 1392 */         fixup();
/*      */         
/* 1394 */         return this.state;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getName()
/*      */       {
/* 1400 */         fixup();
/*      */         
/* 1402 */         if (this.state == 5)
/*      */         {
/* 1404 */           return this.details;
/*      */         }
/*      */         
/* 1407 */         return "";
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSeedCount()
/*      */       {
/* 1413 */         fixup();
/*      */         
/* 1415 */         if (this.state == 5)
/*      */         {
/* 1417 */           return this.seeds;
/*      */         }
/*      */         
/* 1420 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLeecherCount()
/*      */       {
/* 1426 */         fixup();
/*      */         
/* 1428 */         if (this.state == 5)
/*      */         {
/* 1430 */           return this.leechers;
/*      */         }
/*      */         
/* 1433 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getPeers()
/*      */       {
/* 1439 */         fixup();
/*      */         
/* 1441 */         if (this.state == 5)
/*      */         {
/* 1443 */           return this.peers;
/*      */         }
/*      */         
/* 1446 */         return -1;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getTrackingName(Object obj)
/*      */   {
/* 1455 */     String name = obj.getClass().getName();
/*      */     
/* 1457 */     int pos = name.lastIndexOf('.');
/*      */     
/* 1459 */     name = name.substring(pos + 1);
/*      */     
/* 1461 */     pos = name.indexOf('$');
/*      */     
/* 1463 */     if (pos != -1)
/*      */     {
/* 1465 */       name = name.substring(0, pos);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1470 */     pos = name.indexOf("DHTTrackerPlugin");
/*      */     
/* 1472 */     if (pos == 0)
/*      */     {
/*      */ 
/*      */ 
/* 1476 */       name = null;
/*      */     }
/* 1478 */     else if (pos > 0)
/*      */     {
/* 1480 */       name = name.substring(0, pos);
/*      */     }
/* 1482 */     else if (name.equals("DHTAnnounceResult"))
/*      */     {
/* 1484 */       name = "mlDHT";
/*      */     }
/*      */     
/* 1487 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAnnounceResult(DownloadAnnounceResult result)
/*      */   {
/* 1494 */     String class_name = getTrackingName(result);
/*      */     
/* 1496 */     if (class_name != null)
/*      */     {
/* 1498 */       int seeds = result.getSeedCount();
/* 1499 */       int leechers = result.getNonSeedCount();
/*      */       
/* 1501 */       DownloadAnnounceResultPeer[] peers = result.getPeers();
/*      */       
/* 1503 */       int peer_count = peers == null ? 0 : peers.length;
/*      */       try
/*      */       {
/* 1506 */         this.peer_listeners_mon.enter();
/*      */         
/* 1508 */         if (this.announce_response_map == null)
/*      */         {
/* 1510 */           this.announce_response_map = new HashMap();
/*      */ 
/*      */ 
/*      */         }
/* 1514 */         else if (this.announce_response_map.size() > 32)
/*      */         {
/* 1516 */           Debug.out("eh?");
/*      */           
/* 1518 */           this.announce_response_map.clear();
/*      */         }
/*      */         
/*      */ 
/* 1522 */         int[] data = (int[])this.announce_response_map.get(class_name);
/*      */         
/* 1524 */         if (data == null)
/*      */         {
/* 1526 */           data = new int[4];
/*      */           
/* 1528 */           this.announce_response_map.put(class_name, data);
/*      */         }
/*      */         
/* 1531 */         data[0] = seeds;
/* 1532 */         data[1] = leechers;
/* 1533 */         data[2] = peer_count;
/* 1534 */         data[3] = ((int)(SystemTime.getCurrentTime() / 1000L));
/*      */       }
/*      */       finally
/*      */       {
/* 1538 */         this.peer_listeners_mon.exit();
/*      */       }
/*      */     }
/*      */     
/* 1542 */     this.download_manager.setAnnounceResult(result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setScrapeResult(DownloadScrapeResult result)
/*      */   {
/* 1549 */     String class_name = getTrackingName(result);
/*      */     
/* 1551 */     if (class_name != null)
/*      */     {
/* 1553 */       int seeds = result.getSeedCount();
/* 1554 */       int leechers = result.getNonSeedCount();
/*      */       try
/*      */       {
/* 1557 */         this.peer_listeners_mon.enter();
/*      */         
/* 1559 */         if (this.announce_response_map == null)
/*      */         {
/* 1561 */           this.announce_response_map = new HashMap();
/*      */ 
/*      */ 
/*      */         }
/* 1565 */         else if (this.announce_response_map.size() > 32)
/*      */         {
/* 1567 */           Debug.out("eh?");
/*      */           
/* 1569 */           this.announce_response_map.clear();
/*      */         }
/*      */         
/*      */ 
/* 1573 */         int[] data = (int[])this.announce_response_map.get(class_name);
/*      */         
/* 1575 */         if (data == null)
/*      */         {
/* 1577 */           data = new int[4];
/*      */           
/* 1579 */           this.announce_response_map.put(class_name, data);
/*      */         }
/*      */         
/* 1582 */         data[0] = seeds;
/* 1583 */         data[1] = leechers;
/* 1584 */         data[3] = ((int)(SystemTime.getCurrentTime() / 1000L));
/*      */       }
/*      */       finally {
/* 1587 */         this.peer_listeners_mon.exit();
/*      */       }
/*      */     }
/*      */     
/* 1591 */     this.download_manager.setScrapeResult(result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stateChanged(DownloadManagerState state, DownloadManagerStateEvent event)
/*      */   {
/* 1599 */     final int type = event.getType();
/*      */     
/* 1601 */     if ((type == 1) || (type == 2))
/*      */     {
/*      */ 
/* 1604 */       String name = (String)event.getData();
/*      */       
/* 1606 */       List property_listeners_ref = this.property_listeners;
/*      */       
/* 1608 */       final TorrentAttribute attr = convertAttribute(name);
/*      */       
/* 1610 */       if (attr != null)
/*      */       {
/* 1612 */         for (int i = 0; i < property_listeners_ref.size(); i++) {
/*      */           try
/*      */           {
/* 1615 */             ((DownloadPropertyListener)property_listeners_ref.get(i)).propertyChanged(this, new DownloadPropertyEvent()
/*      */             {
/*      */ 
/*      */ 
/*      */               public int getType()
/*      */               {
/*      */ 
/* 1622 */                 return type == 1 ? 1 : 2;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public Object getData()
/*      */               {
/* 1630 */                 return attr;
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1636 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addPropertyListener(DownloadPropertyListener l)
/*      */   {
/* 1649 */     if ("com.aimedia.stopseeding.core.RatioWatcher".equals(l.getClass().getName()))
/*      */     {
/*      */ 
/*      */ 
/* 1653 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1662 */     PluginDeprecation.call("property listener", l);
/*      */     try {
/* 1664 */       this.tracker_listeners_mon.enter();
/*      */       
/* 1666 */       List new_property_listeners = new ArrayList(this.property_listeners);
/*      */       
/* 1668 */       new_property_listeners.add(l);
/*      */       
/* 1670 */       this.property_listeners = new_property_listeners;
/*      */       
/* 1672 */       if (this.property_listeners.size() == 1)
/*      */       {
/* 1674 */         this.download_manager.getDownloadState().addListener(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1678 */       this.tracker_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removePropertyListener(DownloadPropertyListener l)
/*      */   {
/* 1688 */     if ("com.aimedia.stopseeding.core.RatioWatcher".equals(l.getClass().getName()))
/*      */     {
/*      */ 
/*      */ 
/* 1692 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1696 */       this.tracker_listeners_mon.enter();
/*      */       
/* 1698 */       List new_property_listeners = new ArrayList(this.property_listeners);
/*      */       
/* 1700 */       new_property_listeners.remove(l);
/*      */       
/* 1702 */       this.property_listeners = new_property_listeners;
/*      */       
/* 1704 */       if (this.property_listeners.size() == 0)
/*      */       {
/* 1706 */         this.download_manager.getDownloadState().removeListener(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1710 */       this.tracker_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void torrentChanged()
/*      */   {
/* 1717 */     TRTrackerAnnouncer client = this.download_manager.getTrackerClient();
/*      */     
/* 1719 */     if (client != null)
/*      */     {
/* 1721 */       client.resetTrackerUrl(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTrackerListener(DownloadTrackerListener l)
/*      */   {
/* 1729 */     addTrackerListener(l, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTrackerListener(DownloadTrackerListener l, boolean immediateTrigger)
/*      */   {
/*      */     try
/*      */     {
/* 1738 */       this.tracker_listeners_mon.enter();
/*      */       
/* 1740 */       List new_tracker_listeners = new ArrayList(this.tracker_listeners);
/*      */       
/* 1742 */       new_tracker_listeners.add(l);
/*      */       
/* 1744 */       this.tracker_listeners = new_tracker_listeners;
/*      */       
/* 1746 */       if (this.tracker_listeners.size() == 1)
/*      */       {
/* 1748 */         this.download_manager.addTrackerListener(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1752 */       this.tracker_listeners_mon.exit();
/*      */     }
/*      */     
/* 1755 */     if (immediateTrigger) { announceTrackerResultsToListener(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeTrackerListener(DownloadTrackerListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1763 */       this.tracker_listeners_mon.enter();
/*      */       
/* 1765 */       List new_tracker_listeners = new ArrayList(this.tracker_listeners);
/*      */       
/* 1767 */       new_tracker_listeners.remove(l);
/*      */       
/* 1769 */       this.tracker_listeners = new_tracker_listeners;
/*      */       
/* 1771 */       if (this.tracker_listeners.size() == 0)
/*      */       {
/* 1773 */         this.download_manager.removeTrackerListener(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1777 */       this.tracker_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1786 */       this.removal_listeners_mon.enter();
/*      */       
/* 1788 */       List new_removal_listeners = new ArrayList(this.removal_listeners);
/*      */       
/* 1790 */       new_removal_listeners.add(l);
/*      */       
/* 1792 */       this.removal_listeners = new_removal_listeners;
/*      */     }
/*      */     finally
/*      */     {
/* 1796 */       this.removal_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeDownloadWillBeRemovedListener(DownloadWillBeRemovedListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1805 */       this.removal_listeners_mon.enter();
/*      */       
/* 1807 */       List new_removal_listeners = new ArrayList(this.removal_listeners);
/*      */       
/* 1809 */       new_removal_listeners.remove(l);
/*      */       
/* 1811 */       this.removal_listeners = new_removal_listeners;
/*      */     }
/*      */     finally
/*      */     {
/* 1815 */       this.removal_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPeerListener(final DownloadPeerListener listener)
/*      */   {
/* 1823 */     DownloadManagerPeerListener delegate = new DownloadManagerPeerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void peerManagerAdded(PEPeerManager manager)
/*      */       {
/*      */ 
/*      */ 
/* 1831 */         PeerManager pm = PeerManagerImpl.getPeerManager(manager);
/*      */         
/* 1833 */         listener.peerManagerAdded(DownloadImpl.this, pm);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void peerManagerRemoved(PEPeerManager manager)
/*      */       {
/* 1840 */         PeerManager pm = PeerManagerImpl.getPeerManager(manager);
/*      */         
/* 1842 */         listener.peerManagerRemoved(DownloadImpl.this, pm);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void peerManagerWillBeAdded(PEPeerManager manager) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void peerAdded(PEPeer peer) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void peerRemoved(PEPeer peer) {}
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1866 */       this.peer_listeners_mon.enter();
/*      */       
/* 1868 */       this.peer_listeners.put(listener, delegate);
/*      */     }
/*      */     finally
/*      */     {
/* 1872 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */     
/* 1875 */     this.download_manager.addPeerListener(delegate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePeerListener(DownloadPeerListener listener)
/*      */   {
/*      */     DownloadManagerPeerListener delegate;
/*      */     
/*      */     try
/*      */     {
/* 1886 */       this.peer_listeners_mon.enter();
/*      */       
/* 1888 */       delegate = (DownloadManagerPeerListener)this.peer_listeners.remove(listener);
/*      */     }
/*      */     finally
/*      */     {
/* 1892 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */     
/* 1895 */     if (delegate != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1902 */       this.download_manager.removePeerListener(delegate);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean activateRequest(final int count)
/*      */   {
/* 1910 */     DownloadActivationEvent event = new DownloadActivationEvent()
/*      */     {
/*      */ 
/*      */       public Download getDownload()
/*      */       {
/*      */ 
/* 1916 */         return DownloadImpl.this;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getActivationCount()
/*      */       {
/* 1922 */         return count;
/*      */       }
/*      */     };
/*      */     
/* 1926 */     for (Iterator it = this.activation_listeners.iterator(); it.hasNext();) {
/*      */       try
/*      */       {
/* 1929 */         DownloadActivationListener listener = (DownloadActivationListener)it.next();
/*      */         
/* 1931 */         if (listener.activationRequested(event))
/*      */         {
/* 1933 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1937 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1941 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadActivationEvent getActivationState()
/*      */   {
/* 1947 */     return this.activation_state;
/*      */   }
/*      */   
/*      */ 
/*      */   public void addActivationListener(DownloadActivationListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1955 */       this.peer_listeners_mon.enter();
/*      */       
/* 1957 */       this.activation_listeners.add(l);
/*      */       
/* 1959 */       if (this.activation_listeners.size() == 1)
/*      */       {
/* 1961 */         this.download_manager.addActivationListener(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1965 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeActivationListener(DownloadActivationListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1974 */       this.peer_listeners_mon.enter();
/*      */       
/* 1976 */       this.activation_listeners.remove(l);
/*      */       
/* 1978 */       if (this.activation_listeners.size() == 0)
/*      */       {
/* 1980 */         this.download_manager.removeActivationListener(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1984 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addCompletionListener(DownloadCompletionListener l) {
/*      */     try {
/* 1990 */       this.listeners_mon.enter();
/* 1991 */       this.completion_listeners.add(l);
/*      */     }
/*      */     finally {
/* 1994 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeCompletionListener(DownloadCompletionListener l) {
/*      */     try {
/* 2000 */       this.listeners_mon.enter();
/* 2001 */       this.completion_listeners.remove(l);
/*      */     }
/*      */     finally {
/* 2004 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public PeerManager getPeerManager()
/*      */   {
/* 2011 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/* 2013 */     if (pm == null)
/*      */     {
/* 2015 */       return null;
/*      */     }
/*      */     
/* 2018 */     return PeerManagerImpl.getPeerManager(pm);
/*      */   }
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.plugins.disk.DiskManager getDiskManager()
/*      */   {
/* 2024 */     PeerManager pm = getPeerManager();
/*      */     
/* 2026 */     if (pm != null)
/*      */     {
/* 2028 */       return pm.getDiskManager();
/*      */     }
/*      */     
/* 2031 */     return null;
/*      */   }
/*      */   
/*      */   public int getDiskManagerFileCount() {
/* 2035 */     return this.download_manager.getNumFileInfos();
/*      */   }
/*      */   
/*      */   public org.gudy.azureus2.plugins.disk.DiskManagerFileInfo getDiskManagerFileInfo(int index) {
/* 2039 */     org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] info = this.download_manager.getDiskManagerFileInfo();
/*      */     
/* 2041 */     if (info == null) {
/* 2042 */       return null;
/*      */     }
/* 2044 */     if ((index < 0) || (index >= info.length)) {
/* 2045 */       return null;
/*      */     }
/*      */     
/* 2048 */     return new DiskManagerFileInfoImpl(this, info[index]);
/*      */   }
/*      */   
/*      */   public org.gudy.azureus2.plugins.disk.DiskManagerFileInfo getPrimaryFile()
/*      */   {
/* 2053 */     org.gudy.azureus2.core3.disk.DiskManagerFileInfo primaryFile = this.download_manager.getDownloadState().getPrimaryFile();
/*      */     
/* 2055 */     if (primaryFile == null) {
/* 2056 */       return null;
/*      */     }
/* 2058 */     return new DiskManagerFileInfoImpl(this, primaryFile);
/*      */   }
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[] getDiskManagerFileInfo()
/*      */   {
/* 2064 */     org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] info = this.download_manager.getDiskManagerFileInfo();
/*      */     
/* 2066 */     if (info == null)
/*      */     {
/* 2068 */       return new org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[0];
/*      */     }
/*      */     
/* 2071 */     org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[] res = new org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[info.length];
/*      */     
/* 2073 */     for (int i = 0; i < res.length; i++)
/*      */     {
/* 2075 */       res[i] = new DiskManagerFileInfoImpl(this, info[i]);
/*      */     }
/*      */     
/* 2078 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaximumDownloadKBPerSecond(int kb)
/*      */   {
/* 2085 */     if (kb == -1) {
/* 2086 */       Debug.out("setMaximiumDownloadKBPerSecond got value (-1) ZERO_DOWNLOAD. (-1)does not work through this method, use getDownloadRateLimitBytesPerSecond() instead.");
/*      */     }
/*      */     
/*      */ 
/* 2090 */     this.download_manager.getStats().setDownloadRateLimitBytesPerSecond(kb < 0 ? 0 : kb * 1024);
/*      */   }
/*      */   
/*      */   public int getMaximumDownloadKBPerSecond() {
/* 2094 */     int bps = this.download_manager.getStats().getDownloadRateLimitBytesPerSecond();
/* 2095 */     return bps < 1024 ? 1 : bps <= 0 ? bps : bps / 1024;
/*      */   }
/*      */   
/*      */   public int getUploadRateLimitBytesPerSecond() {
/* 2099 */     return this.download_manager.getStats().getUploadRateLimitBytesPerSecond();
/*      */   }
/*      */   
/*      */   public void setUploadRateLimitBytesPerSecond(int max_rate_bps) {
/* 2103 */     this.download_manager.getStats().setUploadRateLimitBytesPerSecond(max_rate_bps);
/*      */   }
/*      */   
/*      */   public int getDownloadRateLimitBytesPerSecond() {
/* 2107 */     return this.download_manager.getStats().getDownloadRateLimitBytesPerSecond();
/*      */   }
/*      */   
/*      */   public void setDownloadRateLimitBytesPerSecond(int max_rate_bps) {
/* 2111 */     this.download_manager.getStats().setDownloadRateLimitBytesPerSecond(max_rate_bps);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */   {
/* 2119 */     this.download_manager.addRateLimiter(UtilitiesImpl.wrapLimiter(limiter, false), is_upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */   {
/* 2127 */     this.download_manager.removeRateLimiter(UtilitiesImpl.wrapLimiter(limiter, false), is_upload);
/*      */   }
/*      */   
/*      */   public int getSeedingRank() {
/* 2131 */     return this.download_manager.getSeedingRank();
/*      */   }
/*      */   
/*      */   public void setSeedingRank(int rank) {
/* 2135 */     this.download_manager.setSeedingRank(rank);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSavePath()
/*      */   {
/* 2141 */     return this.download_manager.getSaveLocation().toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveDataFiles(File new_parent_dir)
/*      */     throws DownloadException
/*      */   {
/*      */     try
/*      */     {
/* 2151 */       this.download_manager.moveDataFiles(new_parent_dir);
/*      */     }
/*      */     catch (DownloadManagerException e)
/*      */     {
/* 2155 */       throw new DownloadException("move operation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */   public void moveDataFiles(File new_parent_dir, String new_name)
/*      */     throws DownloadException
/*      */   {
/*      */     try
/*      */     {
/* 2164 */       this.download_manager.moveDataFiles(new_parent_dir, new_name);
/*      */     }
/*      */     catch (DownloadManagerException e)
/*      */     {
/* 2168 */       throw new DownloadException("move / rename operation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */   public void renameDownload(String new_name) throws DownloadException {
/* 2173 */     try { this.download_manager.renameDownload(new_name);
/*      */     } catch (DownloadManagerException e) {
/* 2175 */       throw new DownloadException("rename operation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveTorrentFile(File new_parent_dir)
/*      */     throws DownloadException
/*      */   {
/*      */     try
/*      */     {
/* 2186 */       this.download_manager.moveTorrentFile(new_parent_dir);
/*      */     }
/*      */     catch (DownloadManagerException e)
/*      */     {
/* 2190 */       throw new DownloadException("move operation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public File[] calculateDefaultPaths(boolean for_moving) {
/* 2198 */     SaveLocationChange slc = calculateDefaultDownloadLocation();
/* 2199 */     if (slc == null) return null;
/* 2200 */     return new File[] { slc.download_location, slc.torrent_location };
/*      */   }
/*      */   
/*      */   public boolean isInDefaultSaveDir() {
/* 2204 */     return this.download_manager.isInDefaultSaveDir();
/*      */   }
/*      */   
/*      */ 
/*      */   public void requestTrackerAnnounce()
/*      */   {
/* 2210 */     this.download_manager.requestTrackerAnnounce(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestTrackerAnnounce(boolean immediate)
/*      */   {
/* 2217 */     this.download_manager.requestTrackerAnnounce(immediate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestTrackerScrape(boolean immediate)
/*      */   {
/* 2224 */     this.download_manager.requestTrackerScrape(immediate);
/*      */   }
/*      */   
/*      */   public byte[] getDownloadPeerId() {
/* 2228 */     TRTrackerAnnouncer announcer = this.download_manager.getTrackerClient();
/* 2229 */     if (announcer == null) return null;
/* 2230 */     return announcer.getPeerId();
/*      */   }
/*      */   
/*      */   public boolean isMessagingEnabled() {
/* 2234 */     return this.download_manager.getExtendedMessagingMode() == 2;
/*      */   }
/*      */   
/* 2237 */   public void setMessagingEnabled(boolean enabled) { throw new RuntimeException("setMessagingEnabled is in the process of being removed - if you are seeing this error, let the Azureus developers know that you need this method to stay!"); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPriority()
/*      */   {
/* 2245 */     return 0;
/*      */   }
/*      */   
/*      */   public boolean isPriorityLocked() {
/* 2249 */     return false;
/*      */   }
/*      */   
/*      */   public void setPriority(int priority) {}
/*      */   
/*      */   public boolean isRemoved()
/*      */   {
/* 2256 */     return this.download_manager.isDestroyed();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getRelationText()
/*      */   {
/* 2264 */     return propogatedRelationText(this.download_manager);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object[] getQueryableInterfaces()
/*      */   {
/* 2271 */     return new Object[] { this.download_manager };
/*      */   }
/*      */   
/*      */   private CopyOnWriteMap getAttributeMapForType(int event_type) {
/* 2275 */     return event_type == 2 ? this.read_attribute_listeners_map_cow : this.write_attribute_listeners_map_cow;
/*      */   }
/*      */   
/*      */   public boolean canMoveDataFiles() {
/* 2279 */     return this.download_manager.canMoveDataFiles();
/*      */   }
/*      */   
/*      */   public void attributeEventOccurred(DownloadManager download, String attribute, int event_type) {
/* 2283 */     CopyOnWriteMap attr_listener_map = getAttributeMapForType(event_type);
/*      */     
/* 2285 */     TorrentAttribute attr = convertAttribute(attribute);
/* 2286 */     if (attr == null) { return;
/*      */     }
/* 2288 */     List listeners = null;
/* 2289 */     listeners = ((CopyOnWriteList)attr_listener_map.get(attribute)).getList();
/*      */     
/* 2291 */     if (listeners == null) { return;
/*      */     }
/* 2293 */     for (int i = 0; i < listeners.size(); i++) {
/* 2294 */       DownloadAttributeListener dal = (DownloadAttributeListener)listeners.get(i);
/* 2295 */       try { dal.attributeEventOccurred(this, attr, event_type);
/* 2296 */       } catch (Throwable t) { Debug.printStackTrace(t);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/* 2301 */   public SaveLocationChange calculateDefaultDownloadLocation() { return DownloadManagerMoveHandler.recalculatePath(this.download_manager); }
/*      */   
/*      */   public Object getUserData(Object key)
/*      */   {
/* 2305 */     return this.download_manager.getUserData(key);
/*      */   }
/*      */   
/*      */   public void setUserData(Object key, Object data) {
/* 2309 */     this.download_manager.setUserData(key, data);
/*      */   }
/*      */   
/*      */   public void startDownload(boolean force) {
/* 2313 */     if (force) {
/* 2314 */       setForceStart(true);
/* 2315 */       return;
/*      */     }
/* 2317 */     setForceStart(false);
/*      */     
/* 2319 */     int state = getState();
/* 2320 */     if ((state == 70) || (state == 75)) {
/* 2321 */       this.download_manager.setStateWaiting();
/*      */     }
/*      */   }
/*      */   
/*      */   public void stopDownload()
/*      */   {
/* 2327 */     if (this.download_manager.getState() == 70) return;
/* 2328 */     this.download_manager.stopIt(70, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isStub()
/*      */   {
/* 2336 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canStubbify()
/*      */   {
/* 2342 */     return this.manager.canStubbify(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadStub stubbify()
/*      */     throws DownloadException, DownloadRemovalVetoException
/*      */   {
/* 2350 */     return this.manager.stubbify(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Download destubbify()
/*      */     throws DownloadException
/*      */   {
/* 2358 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   public List<DistributedDatabase> getDistributedDatabases()
/*      */   {
/* 2364 */     return DDBaseImpl.getDDBs(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getTorrentHash()
/*      */   {
/* 2370 */     Torrent t = getTorrent();
/*      */     
/* 2372 */     if (t == null)
/*      */     {
/* 2374 */       return null;
/*      */     }
/*      */     
/* 2377 */     return t.getHash();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getTorrentSize()
/*      */   {
/* 2384 */     Torrent t = getTorrent();
/*      */     
/* 2386 */     if (t == null)
/*      */     {
/* 2388 */       return 0L;
/*      */     }
/*      */     
/* 2391 */     return t.getSize();
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadStub.DownloadStubFile[] getStubFiles()
/*      */   {
/* 2397 */     org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[] dm_files = getDiskManagerFileInfo();
/*      */     
/* 2399 */     DownloadStub.DownloadStubFile[] files = new DownloadStub.DownloadStubFile[dm_files.length];
/*      */     
/* 2401 */     for (int i = 0; i < files.length; i++)
/*      */     {
/* 2403 */       final org.gudy.azureus2.plugins.disk.DiskManagerFileInfo dm_file = dm_files[i];
/*      */       
/* 2405 */       files[i = new DownloadStub.DownloadStubFile()
/*      */       {
/*      */ 
/*      */         public File getFile()
/*      */         {
/*      */ 
/* 2411 */           return dm_file.getFile(true);
/*      */         }
/*      */         
/*      */ 
/*      */         public long getLength()
/*      */         {
/* 2417 */           if ((dm_file.getDownloaded() == dm_file.getLength()) && (!dm_file.isSkipped()))
/*      */           {
/* 2419 */             return dm_file.getLength();
/*      */           }
/*      */           
/*      */ 
/* 2423 */           return -dm_file.getLength();
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*      */ 
/* 2429 */     return files;
/*      */   }
/*      */   
/*      */ 
/*      */   public void changeLocation(SaveLocationChange slc)
/*      */     throws DownloadException
/*      */   {
/* 2436 */     boolean has_change = (slc.hasDownloadChange()) || (slc.hasTorrentChange());
/* 2437 */     if (!has_change) { return;
/*      */     }
/*      */     
/* 2440 */     has_change = slc.isDifferentDownloadLocation(new File(getSavePath()));
/* 2441 */     if (!has_change) {
/* 2442 */       has_change = slc.isDifferentTorrentLocation(new File(getTorrentFileName()));
/*      */     }
/*      */     
/* 2445 */     if (!has_change) { return;
/*      */     }
/* 2447 */     boolean try_to_resume = !isPaused();
/*      */     try {
/*      */       try {
/* 2450 */         if (slc.hasDownloadChange()) this.download_manager.moveDataFiles(slc.download_location, slc.download_name);
/* 2451 */         if (slc.hasTorrentChange()) this.download_manager.moveTorrentFile(slc.torrent_location, slc.torrent_name);
/*      */       }
/*      */       catch (DownloadManagerException e) {
/* 2454 */         throw new DownloadException(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */     finally {
/* 2458 */       if (try_to_resume) resume();
/*      */     }
/*      */   }
/*      */   
/*      */   private static class PropertyListenerBridge implements DownloadAttributeListener {
/*      */     private DownloadPropertyListener l;
/*      */     
/* 2465 */     public PropertyListenerBridge(DownloadPropertyListener l) { this.l = l; }
/*      */     
/* 2467 */     public void attributeEventOccurred(Download d, final TorrentAttribute attr, final int event_type) { this.l.propertyChanged(d, new DownloadPropertyEvent() {
/* 2468 */         public int getType() { return event_type; }
/* 2469 */         public Object getData() { return attr; }
/*      */       }); }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class AggregateScrapeResult
/*      */     implements DownloadScrapeResult
/*      */   {
/*      */     private Download dl;
/*      */     
/*      */     private TRTrackerScraperResponse response;
/*      */     
/*      */     private int seeds;
/*      */     
/*      */     private int leechers;
/*      */     
/*      */     private int time_secs;
/*      */     
/*      */ 
/*      */     private AggregateScrapeResult(Download _dl)
/*      */     {
/* 2491 */       this.dl = _dl;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void update(TRTrackerScraperResponse _response, int _seeds, int _peers, int _time_secs)
/*      */     {
/* 2501 */       this.response = _response;
/* 2502 */       this.seeds = _seeds;
/* 2503 */       this.leechers = _peers;
/* 2504 */       this.time_secs = _time_secs;
/*      */     }
/*      */     
/*      */ 
/*      */     public Download getDownload()
/*      */     {
/* 2510 */       return this.dl;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getResponseType()
/*      */     {
/* 2516 */       return 1;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getSeedCount()
/*      */     {
/* 2522 */       return this.seeds;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getNonSeedCount()
/*      */     {
/* 2528 */       return this.leechers;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getScrapeStartTime()
/*      */     {
/* 2534 */       TRTrackerScraperResponse r = this.response;
/*      */       
/* 2536 */       if (r != null)
/*      */       {
/* 2538 */         return r.getScrapeStartTime();
/*      */       }
/*      */       
/* 2541 */       if (this.time_secs <= 0)
/*      */       {
/* 2543 */         return -1L;
/*      */       }
/*      */       
/*      */ 
/* 2547 */       return this.time_secs * 1000L;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setNextScrapeStartTime(long nextScrapeStartTime)
/*      */     {
/* 2555 */       Debug.out("Not Supported");
/*      */     }
/*      */     
/*      */ 
/*      */     public long getNextScrapeStartTime()
/*      */     {
/* 2561 */       TRTrackerScraperResponse r = this.response;
/*      */       
/* 2563 */       return r == null ? -1L : r.getScrapeStartTime();
/*      */     }
/*      */     
/*      */ 
/*      */     public String getStatus()
/*      */     {
/* 2569 */       return "Aggregate Scrape";
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getURL()
/*      */     {
/* 2575 */       TRTrackerScraperResponse r = this.response;
/*      */       
/* 2577 */       return r == null ? null : r.getURL();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */