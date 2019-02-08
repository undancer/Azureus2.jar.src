/*      */ package com.aelitis.azureus.core.peermanager.piecepicker.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.peermanager.control.PeerControlScheduler;
/*      */ import com.aelitis.azureus.core.peermanager.control.SpeedTokenDispenser;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.EndGameModeChunk;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePickerListener;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePriorityProvider;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PieceRTAProvider;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*      */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMap;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerListener;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerListenerAdapter;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*      */ import org.gudy.azureus2.core3.peer.PEPiece;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPieceImpl;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ public class PiecePickerImpl implements com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker
/*      */ {
/*      */   private static final boolean LOG_RTA = false;
/*   52 */   private static final LogIDs LOGID = LogIDs.PIECES;
/*      */   
/*      */ 
/*      */   private static final long TIME_MIN_AVAILABILITY = 974L;
/*      */   
/*      */ 
/*      */   private static final long TIME_MIN_FILE_AVAILABILITY = 5000L;
/*      */   
/*      */ 
/*      */   private static final long TIME_MIN_PRIORITIES = 999L;
/*      */   
/*      */ 
/*      */   private static final long TIME_AVAIL_REBUILD = 299976L;
/*      */   
/*      */ 
/*      */   private static final int PRIORITY_W_FIRSTLAST = 999;
/*      */   
/*      */ 
/*      */   private static final long FIRST_PIECE_MIN_NB = 4L;
/*      */   
/*      */   private static final int PRIORITY_W_FILE_BASE = 1000;
/*      */   
/*      */   private static final int PRIORITY_W_FILE_RANGE = 1000;
/*      */   
/*      */   private static final int PRIORITY_W_COMPLETION = 2000;
/*      */   
/*      */   private static final int PRIORITY_W_AGE = 900;
/*      */   
/*      */   private static final int PRIORITY_DW_AGE = 60000;
/*      */   
/*      */   private static final int PRIORITY_DW_STALE = 120000;
/*      */   
/*      */   private static final int PRIORITY_W_PIECE_DONE = 900;
/*      */   
/*      */   private static final int PRIORITY_W_SAME_PIECE = 700;
/*      */   
/*      */   private static final int PRIORITY_OVERRIDES_RAREST = 9000;
/*      */   
/*      */   private static final int PRIORITY_REQUEST_HINT = 3000;
/*      */   
/*      */   private static final int PRIORITY_REALTIME = 9999999;
/*      */   
/*      */   private static final int PRIORITY_FORCED = 1000000;
/*      */   
/*      */   private static final int REQUESTS_MIN_MIN = 2;
/*      */   
/*      */   private static final int REQUESTS_MIN_MAX = 8;
/*      */   
/*      */   private static final int REQUESTS_MAX = 256;
/*      */   
/*      */   private static final int SLOPE_REQUESTS = 4096;
/*      */   
/*      */   private static final long RTA_END_GAME_MODE_SIZE_TRIGGER = 262144L;
/*      */   
/*      */   private static final long END_GAME_MODE_RESERVED_TRIGGER = 5242880L;
/*      */   
/*      */   private static final long END_GAME_MODE_SIZE_TRIGGER = 20971520L;
/*      */   
/*      */   private static final long END_GAME_MODE_TIMEOUT = 76800L;
/*      */   
/*  112 */   protected static volatile boolean firstPiecePriority = COConfigurationManager.getBooleanParameter("Prioritize First Piece");
/*  113 */   protected static volatile boolean completionPriority = COConfigurationManager.getBooleanParameter("Prioritize Most Completed Files");
/*      */   
/*  115 */   protected static volatile long paramPriorityChange = Long.MIN_VALUE;
/*      */   
/*      */   private static final int NO_REQUEST_BACKOFF_MAX_MILLIS = 5000;
/*  118 */   private static final int NO_REQUEST_BACKOFF_MAX_LOOPS = 5000 / PeerControlScheduler.SCHEDULE_PERIOD_MILLIS;
/*      */   
/*  120 */   static final Random random = new Random();
/*      */   
/*      */   private final DiskManager diskManager;
/*      */   
/*      */   private final PEPeerControl peerControl;
/*      */   
/*      */   private final DiskManagerListenerImpl diskManagerListener;
/*      */   
/*      */   protected final Map peerListeners;
/*      */   
/*      */   private final PEPeerManagerListener peerManagerListener;
/*      */   
/*      */   protected final int nbPieces;
/*      */   protected final DiskManagerPiece[] dmPieces;
/*      */   protected final PEPiece[] pePieces;
/*      */   private final List<PEPiece> rarestStartedPieces;
/*  136 */   protected final AEMonitor availabilityMon = new AEMonitor("PiecePicker:avail");
/*  137 */   private final AEMonitor endGameModeChunks_mon = new AEMonitor("PiecePicker:EGM");
/*      */   
/*      */   protected volatile int nbPiecesDone;
/*      */   
/*      */   protected volatile int[] availabilityAsynch;
/*      */   
/*      */   protected volatile long availabilityDrift;
/*      */   
/*  145 */   private long timeAvailRebuild = 299976L;
/*      */   
/*      */   protected volatile int[] availability;
/*      */   
/*      */   private long time_last_avail;
/*      */   
/*      */   protected volatile long availabilityChange;
/*      */   
/*      */   private volatile long availabilityComputeChange;
/*      */   
/*      */   private long time_last_rebuild;
/*      */   
/*      */   private long timeAvailLessThanOne;
/*      */   
/*      */   private float globalAvail;
/*      */   
/*      */   private float globalAvgAvail;
/*      */   
/*      */   private int nbRarestActive;
/*      */   
/*      */   private int globalMin;
/*      */   
/*      */   private int globalMax;
/*      */   
/*      */   private long bytesUnavailable;
/*      */   
/*      */   private volatile int globalMinOthers;
/*      */   
/*      */   protected volatile long filePriorityChange;
/*      */   
/*      */   private volatile long priorityParamChange;
/*      */   
/*      */   private volatile long priorityFileChange;
/*      */   
/*      */   private volatile long priorityAvailChange;
/*      */   
/*      */   private boolean priorityRTAexists;
/*      */   
/*      */   private long timeLastPriorities;
/*      */   
/*      */   private int[] startPriorities;
/*      */   
/*      */   protected volatile boolean hasNeededUndonePiece;
/*      */   
/*      */   protected volatile long neededUndonePieceChange;
/*      */   
/*      */   private volatile boolean endGameMode;
/*      */   
/*      */   private volatile boolean endGameModeAbandoned;
/*      */   private volatile long timeEndGameModeEntered;
/*      */   private List endGameModeChunks;
/*      */   private long lastProviderRecalcTime;
/*  197 */   private final CopyOnWriteList rta_providers = new CopyOnWriteList();
/*      */   private long[] provider_piece_rtas;
/*  199 */   private final CopyOnWriteList priority_providers = new CopyOnWriteList();
/*      */   
/*      */   private long[] provider_piece_priorities;
/*      */   
/*      */   private int allocate_request_loop_count;
/*      */   
/*      */   private int max_file_priority;
/*      */   
/*      */   private int min_file_priority;
/*      */   
/*      */   private boolean reverse_block_order;
/*      */   private int[] global_request_hint;
/*      */   private static boolean enable_request_hints;
/*      */   private static boolean includeLanPeersInReqLimiting;
/*  213 */   private final CopyOnWriteList listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile float[] fileAvailabilities;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile long fileAvailabilitiesCalcTime;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile CopyOnWriteSet<Integer> forced_pieces;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*  243 */     org.gudy.azureus2.core3.config.ParameterListener parameterListener = new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */       public final void parameterChanged(String parameterName)
/*      */       {
/*  227 */         if (parameterName.equals("Prioritize Most Completed Files"))
/*      */         {
/*  229 */           PiecePickerImpl.completionPriority = COConfigurationManager.getBooleanParameter(parameterName);
/*  230 */           PiecePickerImpl.paramPriorityChange += 1L;
/*  231 */         } else if (parameterName.equals("Prioritize First Piece"))
/*      */         {
/*  233 */           PiecePickerImpl.firstPiecePriority = COConfigurationManager.getBooleanParameter(parameterName);
/*  234 */           PiecePickerImpl.paramPriorityChange += 1L;
/*  235 */         } else if (parameterName.equals("Piece Picker Request Hint Enabled")) {
/*  236 */           PiecePickerImpl.access$002(COConfigurationManager.getBooleanParameter(parameterName));
/*      */         }
/*      */         
/*  239 */         PiecePickerImpl.access$102(!COConfigurationManager.getBooleanParameter("LAN Speed Enabled"));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  244 */     };
/*  245 */     COConfigurationManager.addParameterListener("Prioritize Most Completed Files", parameterListener);
/*  246 */     COConfigurationManager.addAndFireParameterListener("Prioritize First Piece", parameterListener);
/*  247 */     COConfigurationManager.addAndFireParameterListener("Piece Picker Request Hint Enabled", parameterListener);
/*  248 */     COConfigurationManager.addAndFireParameterListener("LAN Speed Enabled", parameterListener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PiecePickerImpl(PEPeerControl pc)
/*      */   {
/*  257 */     this.peerControl = pc;
/*  258 */     this.diskManager = this.peerControl.getDiskManager();
/*  259 */     this.dmPieces = this.diskManager.getPieces();
/*  260 */     this.nbPieces = this.diskManager.getNbPieces();
/*  261 */     this.nbPiecesDone = 0;
/*      */     
/*  263 */     this.pePieces = pc.getPieces();
/*      */     
/*      */ 
/*  266 */     this.availability = new int[this.nbPieces];
/*      */     
/*      */ 
/*  269 */     this.hasNeededUndonePiece = false;
/*  270 */     this.neededUndonePieceChange = Long.MIN_VALUE;
/*      */     
/*      */ 
/*  273 */     this.time_last_avail = Long.MIN_VALUE;
/*  274 */     this.availabilityChange = -9223372036854775807L;
/*  275 */     this.availabilityComputeChange = Long.MIN_VALUE;
/*  276 */     this.availabilityDrift = this.nbPieces;
/*      */     
/*      */ 
/*  279 */     for (int i = 0; i < this.nbPieces; i++)
/*      */     {
/*  281 */       if (this.dmPieces[i].isDone()) {
/*  282 */         this.availability[i] += 1;
/*  283 */         this.nbPiecesDone += 1;
/*      */       } else {
/*  285 */         this.hasNeededUndonePiece |= this.dmPieces[i].calcNeeded();
/*      */       }
/*      */     }
/*  288 */     if (this.hasNeededUndonePiece) {
/*  289 */       this.neededUndonePieceChange += 1L;
/*      */     }
/*  291 */     updateAvailability();
/*      */     
/*      */ 
/*  294 */     this.peerListeners = new HashMap();
/*  295 */     this.peerManagerListener = new PEPeerManagerListenerImpl(null);
/*  296 */     this.peerControl.addListener(this.peerManagerListener);
/*      */     
/*      */ 
/*      */ 
/*  300 */     this.rarestStartedPieces = new ArrayList();
/*      */     
/*      */ 
/*  303 */     this.filePriorityChange = Long.MIN_VALUE;
/*      */     
/*  305 */     this.priorityParamChange = Long.MIN_VALUE;
/*  306 */     this.priorityFileChange = Long.MIN_VALUE;
/*  307 */     this.priorityAvailChange = Long.MIN_VALUE;
/*      */     
/*  309 */     this.timeLastPriorities = Long.MIN_VALUE;
/*      */     
/*      */ 
/*  312 */     this.endGameMode = false;
/*  313 */     this.endGameModeAbandoned = false;
/*  314 */     this.timeEndGameModeEntered = 0L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  319 */     this.diskManagerListener = new DiskManagerListenerImpl(null);
/*      */     
/*  321 */     syncFilePriorities();
/*      */     
/*  323 */     this.diskManager.addListener(this.diskManagerListener);
/*      */   }
/*      */   
/*      */ 
/*      */   public final void addHavePiece(PEPeer peer, int pieceNumber)
/*      */   {
/*      */     try
/*      */     {
/*  331 */       this.availabilityMon.enter();
/*  332 */       if (this.availabilityAsynch == null) {
/*  333 */         this.availabilityAsynch = ((int[])this.availability.clone());
/*      */       }
/*  335 */       this.availabilityAsynch[pieceNumber] += 1;
/*  336 */       this.availabilityChange += 1L;
/*  337 */     } finally { this.availabilityMon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  342 */     if ((peer != null) && (this.dmPieces[pieceNumber].isDownloadable())) {
/*  343 */       peer.setConsecutiveNoRequestCount(0);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void updateAvailability()
/*      */   {
/*  353 */     long now = SystemTime.getCurrentTime();
/*  354 */     if ((now >= this.time_last_avail) && (now < this.time_last_avail + 974L)) {
/*  355 */       return;
/*      */     }
/*      */     
/*  358 */     if ((this.availabilityDrift > 0L) || (now < this.time_last_rebuild) || (now - this.time_last_rebuild > this.timeAvailRebuild)) {
/*      */       try {
/*  360 */         this.availabilityMon.enter();
/*      */         
/*  362 */         this.time_last_rebuild = now;
/*  363 */         int[] new_availability = recomputeAvailability();
/*      */         
/*  365 */         if (Constants.isCVSVersion())
/*      */         {
/*  367 */           int[] old_availability = this.availabilityAsynch == null ? this.availability : this.availabilityAsynch;
/*  368 */           int errors = 0;
/*      */           
/*  370 */           for (int i = 0; i < new_availability.length; i++) {
/*  371 */             if (new_availability[i] != old_availability[i]) {
/*  372 */               errors++;
/*      */             }
/*      */           }
/*  375 */           if ((errors > 0) && (errors != this.nbPieces))
/*      */           {
/*  377 */             if (Logger.isEnabled()) {
/*  378 */               Logger.log(new LogEvent(this.peerControl, LOGID, 3, "updateAvailability(): availability rebuild errors = " + errors + " timeAvailRebuild =" + this.timeAvailRebuild));
/*      */             }
/*      */             
/*      */ 
/*  382 */             this.timeAvailRebuild -= errors;
/*      */           } else {
/*  384 */             this.timeAvailRebuild += 1L;
/*      */           }
/*      */         }
/*  387 */         this.availabilityAsynch = new_availability;
/*      */         
/*  389 */         this.availabilityDrift = 0L;
/*  390 */         this.availabilityChange += 1L;
/*  391 */       } finally { this.availabilityMon.exit();
/*      */       }
/*  393 */     } else if (this.availabilityComputeChange >= this.availabilityChange) {
/*  394 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  398 */       this.availabilityMon.enter();
/*  399 */       this.time_last_avail = now;
/*  400 */       this.availabilityComputeChange = this.availabilityChange;
/*      */       
/*      */ 
/*  403 */       if (this.availabilityAsynch != null) {
/*  404 */         this.availability = this.availabilityAsynch;
/*  405 */         this.availabilityAsynch = null;
/*      */       }
/*  407 */     } finally { this.availabilityMon.exit();
/*      */     }
/*      */     
/*  410 */     int allMin = Integer.MAX_VALUE;
/*  411 */     int allMax = 0;
/*  412 */     int rarestMin = Integer.MAX_VALUE;
/*  413 */     for (int i = 0; i < this.nbPieces; i++)
/*      */     {
/*  415 */       int avail = this.availability[i];
/*  416 */       DiskManagerPiece dmPiece = this.dmPieces[i];
/*  417 */       PEPiece pePiece = this.pePieces[i];
/*      */       
/*  419 */       if ((avail > 0) && (avail < rarestMin) && (dmPiece.isDownloadable()) && ((pePiece == null) || (pePiece.isRequestable()))) {
/*  420 */         rarestMin = avail;
/*      */       }
/*  422 */       if (avail < allMin)
/*  423 */         allMin = avail;
/*  424 */       if (avail > allMax) {
/*  425 */         allMax = avail;
/*      */       }
/*      */     }
/*  428 */     this.globalMin = allMin;
/*  429 */     this.globalMax = allMax;
/*  430 */     this.globalMinOthers = rarestMin;
/*      */     
/*  432 */     int total = 0;
/*  433 */     int rarestActive = 0;
/*  434 */     long totalAvail = 0L;
/*  435 */     long newBytesUnavailable = 0L;
/*  436 */     for (i = 0; i < this.nbPieces; i++)
/*      */     {
/*  438 */       int avail = this.availability[i];
/*  439 */       DiskManagerPiece dmPiece = this.dmPieces[i];
/*  440 */       PEPiece pePiece = this.pePieces[i];
/*      */       
/*  442 */       if (avail > 0)
/*      */       {
/*  444 */         if (avail > allMin)
/*  445 */           total++;
/*  446 */         if ((avail <= rarestMin) && (dmPiece.isDownloadable()) && (pePiece != null) && (!pePiece.isRequested()))
/*  447 */           rarestActive++;
/*  448 */         totalAvail += avail;
/*      */       } else {
/*  450 */         newBytesUnavailable += dmPiece.getLength();
/*      */       }
/*      */     }
/*      */     
/*  454 */     float newGlobalAvail = total / this.nbPieces + allMin;
/*  455 */     if ((this.globalAvail >= 1.0D) && (newGlobalAvail < 1.0D)) {
/*  456 */       this.timeAvailLessThanOne = now;
/*  457 */     } else if (newGlobalAvail >= 1.0D) {
/*  458 */       this.timeAvailLessThanOne = 0L;
/*      */     }
/*      */     
/*  461 */     this.bytesUnavailable = newBytesUnavailable;
/*  462 */     this.globalAvail = newGlobalAvail;
/*  463 */     this.nbRarestActive = rarestActive;
/*  464 */     this.globalAvgAvail = ((float)totalAvail / this.nbPieces / (1 + this.peerControl.getNbSeeds() + this.peerControl.getNbPeers()));
/*      */   }
/*      */   
/*      */ 
/*      */   private final int[] recomputeAvailability()
/*      */   {
/*  470 */     if ((this.availabilityDrift > 0L) && (this.availabilityDrift != this.nbPieces) && (Logger.isEnabled())) {
/*  471 */       Logger.log(new LogEvent(this.diskManager.getTorrent(), LOGID, 0, "Recomputing availabiliy. Drift=" + this.availabilityDrift + ":" + this.peerControl.getDisplayName()));
/*      */     }
/*  473 */     List peers = this.peerControl.getPeers();
/*      */     
/*  475 */     int[] newAvailability = new int[this.nbPieces];
/*      */     
/*      */ 
/*      */ 
/*  479 */     for (int j = 0; j < this.nbPieces; j++) {
/*  480 */       newAvailability[j] = (this.dmPieces[j].isDone() ? 1 : 0);
/*      */     }
/*  482 */     int peersSize = peers.size();
/*  483 */     for (int i = 0; i < peersSize; i++)
/*      */     {
/*  485 */       PEPeer peer = (PEPeerTransport)peers.get(i);
/*  486 */       if ((peer != null) && (peer.getPeerState() == 30))
/*      */       {
/*      */ 
/*  489 */         BitFlags peerHavePieces = peer.getAvailable();
/*  490 */         if ((peerHavePieces != null) && (peerHavePieces.nbSet > 0))
/*      */         {
/*  492 */           for (j = peerHavePieces.start; j <= peerHavePieces.end; j++)
/*      */           {
/*  494 */             if (peerHavePieces.flags[j] != 0) {
/*  495 */               newAvailability[j] += 1;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  501 */     return newAvailability;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNumberOfPieces()
/*      */   {
/*  507 */     return this.nbPieces;
/*      */   }
/*      */   
/*      */   public final int[] getAvailability()
/*      */   {
/*  512 */     return this.availability;
/*      */   }
/*      */   
/*      */   public final int getAvailability(int pieceNumber)
/*      */   {
/*  517 */     return this.availability[pieceNumber];
/*      */   }
/*      */   
/*      */ 
/*      */   public final float getMinAvailability()
/*      */   {
/*  523 */     return this.globalAvail;
/*      */   }
/*      */   
/*      */ 
/*      */   public float getMinAvailability(int fileIndex)
/*      */   {
/*  529 */     float[] avails = this.fileAvailabilities;
/*      */     
/*  531 */     if (avails == null)
/*      */     {
/*  533 */       DiskManagerFileInfo[] files = this.diskManager.getFiles();
/*      */       
/*  535 */       avails = new float[files.length];
/*      */     }
/*      */     
/*  538 */     if (avails.length == 1)
/*      */     {
/*  540 */       if (this.fileAvailabilities == null)
/*      */       {
/*  542 */         this.fileAvailabilities = avails;
/*      */       }
/*      */       
/*  545 */       return getMinAvailability();
/*      */     }
/*      */     
/*  548 */     long now = SystemTime.getMonotonousTime();
/*      */     
/*  550 */     if ((this.fileAvailabilities == null) || (now - this.fileAvailabilitiesCalcTime > 5000L))
/*      */     {
/*  552 */       int[] current_avail = this.availability;
/*      */       
/*  554 */       if (current_avail == null)
/*      */       {
/*  556 */         return 0.0F;
/*      */       }
/*      */       
/*  559 */       DiskManagerFileInfo[] files = this.diskManager.getFiles();
/*      */       
/*  561 */       for (int i = 0; i < files.length; i++)
/*      */       {
/*  563 */         DiskManagerFileInfo file = files[i];
/*      */         
/*  565 */         int start = file.getFirstPieceNumber();
/*  566 */         int end = start + file.getNbPieces();
/*      */         
/*  568 */         int min_avail = Integer.MAX_VALUE;
/*      */         
/*  570 */         for (int j = start; j < end; j++)
/*      */         {
/*  572 */           int a = current_avail[j];
/*      */           
/*  574 */           min_avail = Math.min(a, min_avail);
/*      */         }
/*      */         
/*  577 */         int total = 0;
/*      */         
/*  579 */         for (int j = start; j < end; j++)
/*      */         {
/*  581 */           int a = current_avail[j];
/*      */           
/*  583 */           if (a > 0)
/*      */           {
/*  585 */             if (a > min_avail)
/*      */             {
/*  587 */               total++;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  592 */         avails[i] = (total / (end - start + 1) + min_avail);
/*      */       }
/*      */       
/*  595 */       this.fileAvailabilities = avails;
/*  596 */       this.fileAvailabilitiesCalcTime = now;
/*      */     }
/*      */     
/*  599 */     return avails[fileIndex];
/*      */   }
/*      */   
/*      */   public final long getBytesUnavailable() {
/*  603 */     return this.bytesUnavailable;
/*      */   }
/*      */   
/*      */   public final long getAvailWentBadTime()
/*      */   {
/*  608 */     return this.timeAvailLessThanOne;
/*      */   }
/*      */   
/*      */   public final int getMaxAvailability()
/*      */   {
/*  613 */     return this.globalMax;
/*      */   }
/*      */   
/*      */   public final float getAvgAvail()
/*      */   {
/*  618 */     return this.globalAvgAvail;
/*      */   }
/*      */   
/*      */   public int getNbPiecesDone()
/*      */   {
/*  623 */     return this.nbPiecesDone;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void checkDownloadablePiece()
/*      */   {
/*  633 */     for (int i = 0; i < this.nbPieces; i++)
/*      */     {
/*  635 */       if (this.dmPieces[i].isInteresting())
/*      */       {
/*  637 */         if (!this.hasNeededUndonePiece)
/*      */         {
/*  639 */           this.hasNeededUndonePiece = true;
/*  640 */           this.neededUndonePieceChange += 1L;
/*      */         }
/*  642 */         return;
/*      */       }
/*      */     }
/*  645 */     if (this.hasNeededUndonePiece)
/*      */     {
/*  647 */       this.hasNeededUndonePiece = false;
/*  648 */       this.neededUndonePieceChange += 1L;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void allocateRequests()
/*      */   {
/*  659 */     if (!this.hasNeededUndonePiece) {
/*  660 */       return;
/*      */     }
/*      */     
/*  663 */     this.allocate_request_loop_count += 1;
/*      */     
/*  665 */     List peers = this.peerControl.getPeers();
/*  666 */     int peersSize = peers.size();
/*      */     
/*      */ 
/*  669 */     final ArrayList<PEPeerTransport> bestUploaders = new ArrayList(peersSize);
/*      */     
/*  671 */     for (int i = 0; i < peersSize; i++)
/*      */     {
/*  673 */       PEPeerTransport peer = (PEPeerTransport)peers.get(i);
/*      */       
/*  675 */       if (peer.isDownloadPossible())
/*      */       {
/*      */ 
/*  678 */         int no_req_count = peer.getConsecutiveNoRequestCount();
/*      */         
/*  680 */         if ((no_req_count == 0) || (this.allocate_request_loop_count % (no_req_count + 1) == 0))
/*      */         {
/*      */ 
/*  683 */           bestUploaders.add(peer);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  695 */     Collections.shuffle(bestUploaders);
/*      */     
/*  697 */     for (int i = 0; i < 3; i++) {
/*      */       try
/*      */       {
/*  700 */         Collections.sort(bestUploaders, new Comparator() {
/*      */           public int compare(PEPeerTransport pt1, PEPeerTransport pt2) {
/*  702 */             if (pt1 == pt2) {
/*  703 */               return 0;
/*      */             }
/*      */             
/*  706 */             PEPeerStats stats2 = pt2.getStats();
/*  707 */             PEPeerStats stats1 = pt1.getStats();
/*      */             
/*      */ 
/*      */ 
/*  711 */             int toReturn = 0;
/*      */             
/*      */ 
/*  714 */             if ((pt1.isLANLocal()) && (!pt2.isLANLocal())) {
/*  715 */               toReturn = -1;
/*  716 */             } else if ((!pt1.isLANLocal()) && (pt2.isLANLocal())) {
/*  717 */               toReturn = 1;
/*      */             }
/*      */             
/*  720 */             if (toReturn == 0) {
/*  721 */               toReturn = (int)(stats2.getSmoothDataReceiveRate() - stats1.getSmoothDataReceiveRate());
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  727 */             if ((toReturn == 0) && ((!pt2.isChokedByMe()) || (!pt1.isChokedByMe()))) {
/*  728 */               toReturn = (int)(stats2.getDataSendRate() - stats1.getDataSendRate());
/*      */             }
/*      */             
/*      */ 
/*  732 */             if ((toReturn == 0) && (pt2.isSnubbed()) && (!pt1.isSnubbed()))
/*  733 */               toReturn = -1;
/*  734 */             if ((toReturn == 0) && (!pt2.isSnubbed()) && (pt1.isSnubbed())) {
/*  735 */               toReturn = 1;
/*      */             }
/*      */             
/*  738 */             if ((toReturn == 0) && (stats2.getTotalDataBytesReceived() == 0L) && (stats1.getTotalDataBytesReceived() > 0L))
/*  739 */               toReturn = 1;
/*  740 */             if ((toReturn == 0) && (stats1.getTotalDataBytesReceived() == 0L) && (stats2.getTotalDataBytesReceived() > 0L)) {
/*  741 */               toReturn = -1;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  751 */             return toReturn;
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (IllegalArgumentException e) {}
/*      */     }
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
/*  767 */     int uploadersSize = bestUploaders.size();
/*      */     
/*  769 */     if (uploadersSize == 0)
/*      */     {
/*      */ 
/*  772 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  777 */     boolean done_priorities = false;
/*      */     int REQUESTS_MIN;
/*  779 */     if (this.priorityRTAexists)
/*      */     {
/*  781 */       int REQUESTS_MIN = 2;
/*      */       
/*  783 */       final Map[] peer_randomiser = { null };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  789 */       final Map block_time_order_peers_metrics = new HashMap(uploadersSize);
/*      */       
/*  791 */       Set block_time_order_peers = new java.util.TreeSet(new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(Object arg1, Object arg2)
/*      */         {
/*      */ 
/*      */ 
/*  800 */           if (arg1 == arg2)
/*      */           {
/*  802 */             return 0;
/*      */           }
/*      */           
/*  805 */           PEPeerTransport pt1 = (PEPeerTransport)arg1;
/*  806 */           PEPeerTransport pt2 = (PEPeerTransport)arg2;
/*      */           
/*  808 */           Integer m1 = (Integer)block_time_order_peers_metrics.get(pt1);
/*      */           
/*  810 */           if (m1 == null)
/*      */           {
/*  812 */             m1 = new Integer(PiecePickerImpl.this.getNextBlockETAFromNow(pt1));
/*      */             
/*  814 */             block_time_order_peers_metrics.put(pt1, m1);
/*      */           }
/*      */           
/*  817 */           Integer m2 = (Integer)block_time_order_peers_metrics.get(pt2);
/*      */           
/*  819 */           if (m2 == null)
/*      */           {
/*  821 */             m2 = new Integer(PiecePickerImpl.this.getNextBlockETAFromNow(pt2));
/*      */             
/*  823 */             block_time_order_peers_metrics.put(pt2, m2);
/*      */           }
/*      */           
/*  826 */           int result = m1.intValue() - m2.intValue();
/*      */           
/*  828 */           if (result == 0)
/*      */           {
/*  830 */             Map pr = peer_randomiser[0];
/*      */             
/*  832 */             if (pr == null)
/*      */             {
/*  834 */               pr = peer_randomiser[0] = new org.gudy.azureus2.core3.util.LightHashMap(bestUploaders.size());
/*      */             }
/*      */             
/*  837 */             Integer r_1 = (Integer)pr.get(pt1);
/*      */             
/*  839 */             if (r_1 == null)
/*      */             {
/*  841 */               r_1 = new Integer(PiecePickerImpl.random.nextInt());
/*      */               
/*  843 */               pr.put(pt1, r_1);
/*      */             }
/*      */             
/*  846 */             Integer r_2 = (Integer)pr.get(pt2);
/*      */             
/*  848 */             if (r_2 == null)
/*      */             {
/*  850 */               r_2 = new Integer(PiecePickerImpl.random.nextInt());
/*      */               
/*  852 */               pr.put(pt2, r_2);
/*      */             }
/*      */             
/*  855 */             result = r_1.intValue() - r_2.intValue();
/*      */             
/*  857 */             if (result == 0)
/*      */             {
/*  859 */               result = pt1.hashCode() - pt2.hashCode();
/*      */               
/*  861 */               if (result == 0)
/*      */               {
/*      */ 
/*      */ 
/*  865 */                 result = 1;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  870 */           return result;
/*      */         }
/*      */         
/*  873 */       });
/*  874 */       block_time_order_peers.addAll(bestUploaders);
/*      */       
/*  876 */       PEPeerTransport best_uploader = (PEPeerTransport)bestUploaders.get(0);
/*      */       
/*  878 */       long best_block_eta = SystemTime.getCurrentTime() + getNextBlockETAFromNow(best_uploader);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  883 */       boolean allocated_request = true;
/*      */       
/*  885 */       Set allocations_started = new java.util.HashSet();
/*      */       try
/*      */       {
/*  888 */         if ((allocated_request) && (this.priorityRTAexists))
/*      */         {
/*  890 */           allocated_request = false;
/*      */           
/*  892 */           while (!block_time_order_peers.isEmpty())
/*      */           {
/*  894 */             Iterator it = block_time_order_peers.iterator();
/*      */             
/*  896 */             PEPeerTransport pt = (PEPeerTransport)it.next();
/*      */             
/*  898 */             it.remove();
/*      */             
/*  900 */             if ((pt.isDownloadPossible()) && (!pt.isSnubbed()))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  907 */               int maxRequests = REQUESTS_MIN + (int)(pt.getStats().getDataReceiveRate() / 4096L) + 1;
/*      */               
/*  909 */               if ((maxRequests > 256) || (maxRequests < 0))
/*      */               {
/*  911 */                 maxRequests = 256;
/*      */               }
/*      */               
/*  914 */               int currentRequests = pt.getNbRequests();
/*      */               
/*  916 */               int allowed_requests = maxRequests - currentRequests;
/*      */               
/*  918 */               if (allowed_requests > 0)
/*      */               {
/*  920 */                 if (!done_priorities)
/*      */                 {
/*  922 */                   done_priorities = true;
/*      */                   
/*  924 */                   computeBasePriorities();
/*      */                   
/*  926 */                   if (!this.priorityRTAexists) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  934 */                 if (!allocations_started.contains(pt))
/*      */                 {
/*  936 */                   pt.requestAllocationStarts(this.startPriorities);
/*      */                   
/*  938 */                   allocations_started.add(pt);
/*      */                 }
/*      */                 
/*  941 */                 if (findRTAPieceToDownload(pt, pt == best_uploader, best_block_eta))
/*      */                 {
/*      */ 
/*      */ 
/*  945 */                   if (allowed_requests > 1)
/*      */                   {
/*  947 */                     block_time_order_peers_metrics.remove(pt);
/*      */                     
/*  949 */                     block_time_order_peers.add(pt);
/*      */                   } }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       } finally { Iterator it;
/*  956 */         Iterator it = allocations_started.iterator();
/*      */         
/*  958 */         while (it.hasNext()) {
/*  959 */           ((PEPeerTransport)it.next()).requestAllocationComplete();
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  964 */       int required_blocks = (int)(this.diskManager.getRemainingExcludingDND() / 16384L);
/*      */       
/*  966 */       int blocks_per_uploader = required_blocks / uploadersSize;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  971 */       REQUESTS_MIN = Math.max(2, Math.min(8, blocks_per_uploader / 2));
/*      */     }
/*      */     
/*  974 */     checkEndGameMode();
/*      */     
/*      */ 
/*      */ 
/*  978 */     for (int i = 0; i < uploadersSize; i++)
/*      */     {
/*  980 */       PEPeerTransport pt = (PEPeerTransport)bestUploaders.get(i);
/*      */       
/*      */ 
/*  983 */       if ((this.dispenser.peek(16384) < 1) && ((!pt.isLANLocal()) || (includeLanPeersInReqLimiting))) {
/*      */         break;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  989 */       if (pt.isDownloadPossible()) {
/*  990 */         int peer_request_num = pt.getMaxNbRequests();
/*      */         
/*      */         int maxRequests;
/*      */         int maxRequests;
/*  994 */         if (peer_request_num != -1) {
/*  995 */           maxRequests = peer_request_num;
/*      */         } else { int maxRequests;
/*  997 */           if (!pt.isSnubbed()) {
/*  998 */             if (!this.endGameMode)
/*      */             {
/*      */               int peer_requests_min;
/*      */               int peer_requests_min;
/* 1002 */               if (pt.getUnchokedForMillis() < 10000L)
/*      */               {
/* 1004 */                 peer_requests_min = REQUESTS_MIN;
/*      */               }
/*      */               else
/*      */               {
/* 1008 */                 peer_requests_min = 2;
/*      */               }
/*      */               
/* 1011 */               int maxRequests = peer_requests_min + (int)(pt.getStats().getDataReceiveRate() / 4096L);
/* 1012 */               if ((maxRequests > 256) || (maxRequests < 0))
/* 1013 */                 maxRequests = 256;
/*      */             } else {
/* 1015 */               maxRequests = 2;
/*      */             }
/*      */           }
/*      */           else {
/* 1019 */             maxRequests = pt.getNetwork() == "Public" ? 1 : 2;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1026 */         if (pt.getNbRequests() <= maxRequests * 3 / 5)
/*      */         {
/*      */ 
/*      */ 
/* 1030 */           if (!done_priorities)
/*      */           {
/* 1032 */             done_priorities = true;
/*      */             
/* 1034 */             computeBasePriorities();
/*      */           }
/*      */           
/* 1037 */           int total_allocated = 0;
/*      */           try
/*      */           {
/* 1040 */             boolean peer_managing_requests = pt.requestAllocationStarts(this.startPriorities);
/*      */             
/* 1042 */             while ((pt.isDownloadPossible()) && (pt.getNbRequests() < maxRequests))
/*      */             {
/*      */               int allocated;
/*      */               
/*      */               int allocated;
/*      */               
/* 1048 */               if ((peer_managing_requests) || (!this.endGameMode))
/*      */               {
/* 1050 */                 allocated = findPieceToDownload(pt, maxRequests);
/*      */               }
/*      */               else
/*      */               {
/* 1054 */                 allocated = findPieceInEndGameMode(pt, maxRequests);
/*      */               }
/*      */               
/* 1057 */               if (allocated == 0) {
/*      */                 break;
/*      */               }
/* 1060 */               total_allocated += allocated;
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 1065 */             pt.requestAllocationComplete();
/*      */           }
/*      */           
/* 1068 */           if (total_allocated == 0)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1075 */             int no_req_count = pt.getConsecutiveNoRequestCount();
/*      */             
/* 1077 */             if (no_req_count < NO_REQUEST_BACKOFF_MAX_LOOPS)
/*      */             {
/* 1079 */               pt.setConsecutiveNoRequestCount(no_req_count + 1);
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1086 */             pt.setConsecutiveNoRequestCount(0);
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
/*      */   protected int getNextBlockETAFromNow(PEPeerTransport pt)
/*      */   {
/* 1099 */     long upRate = pt.getStats().getDataReceiveRate();
/*      */     
/* 1101 */     if (upRate < 1L)
/*      */     {
/* 1103 */       upRate = 1L;
/*      */     }
/*      */     
/* 1106 */     int next_block_bytes = (pt.getNbRequests() + 1) * 16384;
/*      */     
/* 1108 */     return (int)(next_block_bytes * 1000 / upRate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int calcRarestAllowed()
/*      */   {
/* 1119 */     int RarestAllowed = 1;
/* 1120 */     if (this.globalMinOthers < 20) RarestAllowed = 2;
/* 1121 */     if (this.globalMinOthers < 8) RarestAllowed = 3;
/* 1122 */     if (this.globalMinOthers < 4) { RarestAllowed = 4;
/*      */     }
/* 1124 */     if (this.nbPiecesDone < 4) { RarestAllowed = 0;
/*      */     }
/* 1126 */     if (SystemTime.getCurrentTime() - this.peerControl.getTimeStarted(false) < 180000L) { RarestAllowed = 0;
/*      */     }
/* 1128 */     if (this.rarestStartedPieces.size() > RarestAllowed + 2) { RarestAllowed = 0;
/*      */     }
/*      */     
/*      */ 
/* 1132 */     for (int i = 0; i < this.rarestStartedPieces.size(); i++)
/*      */     {
/* 1134 */       PEPiece rarestStarted = (PEPiece)this.rarestStartedPieces.get(i);
/* 1135 */       if (this.pePieces[rarestStarted.getPieceNumber()] == null) { this.rarestStartedPieces.remove(i);i--;
/* 1136 */       } else if (((rarestStarted.getAvailability() <= this.globalMinOthers) || (this.globalMinOthers > this.globalMin)) && ((SystemTime.getCurrentTime() - rarestStarted.getLastDownloadTime(SystemTime.getCurrentTime()) < 60000L) || (rarestStarted.getNbWritten() == 0)) && (!rarestStarted.isDownloaded()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1144 */         RarestAllowed--;
/*      */       }
/*      */     }
/* 1147 */     return RarestAllowed;
/*      */   }
/*      */   
/*      */ 
/*      */   private void syncFilePriorities()
/*      */   {
/* 1153 */     DiskManagerFileInfo[] files = this.diskManager.getFiles();
/*      */     
/* 1155 */     int max = 0;
/* 1156 */     int min = 0;
/*      */     
/* 1158 */     for (DiskManagerFileInfo file : files)
/*      */     {
/* 1160 */       int p = file.getPriority();
/*      */       
/* 1162 */       if (p > max)
/*      */       {
/* 1164 */         max = p;
/*      */       }
/* 1166 */       else if (p < min)
/*      */       {
/* 1168 */         min = p;
/*      */       }
/*      */     }
/*      */     
/* 1172 */     this.max_file_priority = max;
/* 1173 */     this.min_file_priority = min;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void computeBasePriorities()
/*      */   {
/* 1183 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1185 */     if ((now < this.lastProviderRecalcTime) || (now - this.lastProviderRecalcTime > 1000L))
/*      */     {
/* 1187 */       this.lastProviderRecalcTime = now;
/*      */       
/* 1189 */       this.priorityRTAexists = computeProviderPriorities();
/*      */     }
/*      */     
/* 1192 */     if ((!this.priorityRTAexists) && 
/* 1193 */       (this.startPriorities != null) && (((now > this.timeLastPriorities) && (now < this.timeLastPriorities + 999L)) || ((this.priorityParamChange >= paramPriorityChange) && (this.priorityFileChange >= this.filePriorityChange) && (this.priorityAvailChange >= this.availabilityChange))))
/*      */     {
/*      */ 
/*      */ 
/* 1197 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1204 */     this.timeLastPriorities = now;
/* 1205 */     this.priorityParamChange = paramPriorityChange;
/* 1206 */     this.priorityFileChange = this.filePriorityChange;
/* 1207 */     this.priorityAvailChange = this.availabilityChange;
/*      */     
/* 1209 */     boolean foundPieceToDownload = false;
/* 1210 */     int[] newPriorities = new int[this.nbPieces];
/*      */     
/*      */ 
/* 1213 */     boolean firstPiecePriorityL = firstPiecePriority;
/* 1214 */     boolean completionPriorityL = completionPriority;
/*      */     
/* 1216 */     DMPieceMap pieceMap = this.diskManager.getPieceMap();
/*      */     
/* 1218 */     CopyOnWriteSet<Integer> forced = this.forced_pieces;
/*      */     
/*      */     try
/*      */     {
/* 1222 */       boolean rarestOverride = calcRarestAllowed() < 1;
/*      */       
/* 1224 */       int nbConnects = this.peerControl.getNbPeers() + this.peerControl.getNbSeeds();
/* 1225 */       for (int i = 0; i < this.nbPieces; i++)
/*      */       {
/* 1227 */         DiskManagerPiece dmPiece = this.dmPieces[i];
/*      */         
/* 1229 */         if (dmPiece.isDone())
/*      */         {
/* 1231 */           if ((forced != null) && (forced.contains(Integer.valueOf(i))))
/*      */           {
/* 1233 */             if ((forced.remove(Integer.valueOf(i))) && (forced.size() == 0))
/*      */             {
/* 1235 */               synchronized (this)
/*      */               {
/* 1237 */                 if ((this.forced_pieces != null) && (this.forced_pieces.size() == 0))
/*      */                 {
/* 1239 */                   this.forced_pieces = null;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1247 */           int startPriority = Integer.MIN_VALUE;
/*      */           
/* 1249 */           DMPieceList pieceList = pieceMap.getPieceList(dmPiece.getPieceNumber());
/* 1250 */           int pieceListSize = pieceList.size();
/* 1251 */           for (int j = 0; j < pieceListSize; j++)
/*      */           {
/* 1253 */             DiskManagerFileInfoImpl fileInfo = pieceList.get(j).getFile();
/* 1254 */             long downloaded = fileInfo.getDownloaded();
/* 1255 */             long length = fileInfo.getLength();
/* 1256 */             if ((length > 0L) && (downloaded < length) && (!fileInfo.isSkipped()))
/*      */             {
/* 1258 */               int priority = 0;
/*      */               
/*      */ 
/*      */ 
/* 1262 */               boolean hasFirstLastPriority = false;
/*      */               
/* 1264 */               if ((firstPiecePriorityL) && (fileInfo.getNbPieces() > 4L))
/*      */               {
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
/* 1278 */                 if ((i == fileInfo.getFirstPieceNumber()) || (i == fileInfo.getLastPieceNumber())) {
/* 1279 */                   hasFirstLastPriority = true;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 1286 */               int file_priority = fileInfo.getPriority();
/*      */               
/* 1288 */               int max = Math.max(file_priority, this.max_file_priority);
/* 1289 */               int min = Math.min(file_priority, this.min_file_priority);
/*      */               
/* 1291 */               int range = max - min;
/*      */               
/* 1293 */               if (range > 0)
/*      */               {
/* 1295 */                 int relative_file_priority = file_priority - min;
/*      */                 
/* 1297 */                 priority += 1000;
/*      */                 
/*      */                 int adjustment;
/*      */                 int adjustment;
/* 1301 */                 if (hasFirstLastPriority)
/*      */                 {
/*      */ 
/*      */ 
/* 1305 */                   adjustment = 1000 * (relative_file_priority + 1) / range - 1;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1309 */                   adjustment = 1000 * relative_file_priority / range;
/*      */                 }
/*      */                 
/* 1312 */                 priority += adjustment;
/*      */ 
/*      */ 
/*      */               }
/* 1316 */               else if (hasFirstLastPriority)
/*      */               {
/* 1318 */                 priority += 999;
/*      */               }
/*      */               
/*      */ 
/* 1322 */               if (completionPriorityL)
/*      */               {
/* 1324 */                 long percent = 1000L * downloaded / length;
/*      */                 
/* 1326 */                 if (percent >= 900L)
/*      */                 {
/* 1328 */                   priority = (int)(priority + 2000L * downloaded / this.diskManager.getTotalLength());
/*      */                 }
/*      */               }
/*      */               
/* 1332 */               if (priority > startPriority)
/*      */               {
/* 1334 */                 startPriority = priority;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1339 */           if (startPriority >= 0)
/*      */           {
/* 1341 */             dmPiece.setNeeded();
/* 1342 */             foundPieceToDownload = true;
/* 1343 */             int avail = this.availability[i];
/*      */             
/* 1345 */             if ((avail > 0) && (nbConnects > avail))
/*      */             {
/* 1347 */               startPriority += nbConnects - avail;
/*      */               
/*      */ 
/* 1350 */               if ((!rarestOverride) && (avail <= this.globalMinOthers)) {
/* 1351 */                 startPriority += nbConnects / avail;
/*      */               }
/*      */             }
/* 1354 */             if (this.provider_piece_rtas != null)
/*      */             {
/* 1356 */               if (this.provider_piece_rtas[i] > 0L)
/*      */               {
/* 1358 */                 startPriority = 9999999;
/*      */               }
/* 1360 */             } else if (this.provider_piece_priorities != null)
/*      */             {
/* 1362 */               startPriority = (int)(startPriority + this.provider_piece_priorities[i]);
/*      */             }
/* 1364 */             else if ((forced != null) && (forced.contains(Integer.valueOf(i))))
/*      */             {
/* 1366 */               startPriority = 1000000;
/*      */             }
/*      */           }
/*      */           else {
/* 1370 */             dmPiece.clearNeeded();
/*      */           }
/*      */           
/* 1373 */           newPriorities[i] = startPriority;
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/* 1377 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1380 */     if (foundPieceToDownload != this.hasNeededUndonePiece)
/*      */     {
/* 1382 */       this.hasNeededUndonePiece = foundPieceToDownload;
/* 1383 */       this.neededUndonePieceChange += 1L;
/*      */     }
/*      */     
/* 1386 */     this.startPriorities = newPriorities;
/*      */   }
/*      */   
/*      */ 
/*      */   private final boolean isRarestOverride()
/*      */   {
/* 1392 */     int nbSeeds = this.peerControl.getNbSeeds();
/* 1393 */     int nbPeers = this.peerControl.getNbPeers();
/* 1394 */     int nbMost = nbPeers > nbSeeds ? nbPeers : nbSeeds;
/* 1395 */     int nbActive = this.peerControl.getNbActivePieces();
/*      */     
/*      */ 
/*      */ 
/* 1399 */     boolean rarestOverride = (this.nbPiecesDone < 4) || (this.endGameMode) || ((this.globalMinOthers > 1) && ((this.nbRarestActive >= nbMost) || (nbActive >= nbMost)));
/*      */     
/* 1401 */     if ((!rarestOverride) && (this.nbRarestActive > 1) && (this.globalMinOthers > 1))
/*      */     {
/*      */ 
/* 1404 */       rarestOverride = (this.globalMinOthers > this.globalMin) || ((this.globalMinOthers >= 2 * nbSeeds) && (2 * this.globalMinOthers >= nbPeers));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1413 */     return rarestOverride;
/*      */   }
/*      */   
/* 1416 */   private final SpeedTokenDispenser dispenser = com.aelitis.azureus.core.peermanager.control.PeerControlSchedulerFactory.getSingleton(0).getSpeedTokenDispenser();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int findPieceToDownload(PEPeerTransport pt, int nbWanted)
/*      */   {
/* 1424 */     int pieceNumber = getRequestCandidate(pt);
/* 1425 */     if (pieceNumber < 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1430 */       return 0;
/*      */     }
/*      */     
/* 1433 */     int peerSpeed = (int)pt.getStats().getDataReceiveRate() / 1000;
/* 1434 */     if (peerSpeed < 0)
/* 1435 */       peerSpeed = 0;
/* 1436 */     if (pt.isSnubbed())
/* 1437 */       peerSpeed = 0;
/*      */     PEPiece pePiece;
/*      */     PEPiece pePiece;
/* 1440 */     if (this.pePieces[pieceNumber] != null) {
/* 1441 */       pePiece = this.pePieces[pieceNumber];
/*      */     }
/*      */     else {
/* 1444 */       int[] peer_priority_offsets = pt.getPriorityOffsets();
/*      */       
/* 1446 */       int this_offset = peer_priority_offsets == null ? 0 : peer_priority_offsets[pieceNumber];
/*      */       
/*      */ 
/*      */ 
/* 1450 */       pePiece = new PEPieceImpl(pt.getManager(), this.dmPieces[pieceNumber], peerSpeed >> 1);
/*      */       
/*      */ 
/* 1453 */       this.peerControl.addPiece(pePiece, pieceNumber, pt);
/* 1454 */       if (this.startPriorities != null) {
/* 1455 */         pePiece.setResumePriority(this.startPriorities[pieceNumber] + this_offset);
/*      */       } else {
/* 1457 */         pePiece.setResumePriority(this_offset);
/*      */       }
/* 1459 */       if (this.availability[pieceNumber] <= this.globalMinOthers) {
/* 1460 */         this.nbRarestActive += 1;
/*      */       }
/*      */     }
/* 1463 */     int[] request_hint = null;
/*      */     
/* 1465 */     if (enable_request_hints)
/*      */     {
/* 1467 */       request_hint = pt.getRequestHint();
/*      */       
/* 1469 */       if (request_hint != null)
/*      */       {
/* 1471 */         if (request_hint[0] != pieceNumber)
/*      */         {
/* 1473 */           request_hint = null;
/*      */         }
/*      */       }
/*      */       
/* 1477 */       if (request_hint == null)
/*      */       {
/* 1479 */         request_hint = this.global_request_hint;
/*      */         
/* 1481 */         if ((request_hint != null) && (request_hint[0] != pieceNumber))
/*      */         {
/* 1483 */           request_hint = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1489 */     if ((!pt.isLANLocal()) || (includeLanPeersInReqLimiting)) {
/* 1490 */       nbWanted = this.dispenser.dispense(nbWanted, 16384);
/*      */     }
/*      */     
/* 1493 */     int[] blocksFound = pePiece.getAndMarkBlocks(pt, nbWanted, request_hint, this.reverse_block_order);
/*      */     
/* 1495 */     int blockNumber = blocksFound[0];
/* 1496 */     int nbBlocks = blocksFound[1];
/*      */     
/* 1498 */     if (((!pt.isLANLocal()) || (includeLanPeersInReqLimiting)) && (nbBlocks != nbWanted)) {
/* 1499 */       this.dispenser.returnUnusedChunks(nbWanted - nbBlocks, 16384);
/*      */     }
/*      */     
/* 1502 */     if (nbBlocks <= 0) {
/* 1503 */       return 0;
/*      */     }
/* 1505 */     int requested = 0;
/*      */     
/*      */ 
/*      */ 
/* 1509 */     if (this.reverse_block_order)
/*      */     {
/* 1511 */       for (int i = nbBlocks - 1; i >= 0; i--)
/*      */       {
/* 1513 */         int thisBlock = blockNumber + i;
/*      */         
/* 1515 */         if (pt.request(pieceNumber, thisBlock * 16384, pePiece.getBlockSize(thisBlock), true) != null) {
/* 1516 */           requested++;
/* 1517 */           pt.setLastPiece(pieceNumber);
/*      */           
/* 1519 */           pePiece.setLastRequestedPeerSpeed(peerSpeed);
/*      */         }
/*      */         else {
/* 1522 */           pePiece.clearRequested(thisBlock);
/*      */         }
/*      */       }
/*      */     } else {
/* 1526 */       for (int i = 0; i < nbBlocks; i++)
/*      */       {
/* 1528 */         int thisBlock = blockNumber + i;
/*      */         
/* 1530 */         if (pt.request(pieceNumber, thisBlock * 16384, pePiece.getBlockSize(thisBlock), true) != null) {
/* 1531 */           requested++;
/* 1532 */           pt.setLastPiece(pieceNumber);
/*      */           
/* 1534 */           pePiece.setLastRequestedPeerSpeed(peerSpeed);
/*      */         }
/*      */         else {
/* 1537 */           pePiece.clearRequested(thisBlock);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1542 */     if ((requested > 0) && (pePiece.getAvailability() <= this.globalMinOthers) && (calcRarestAllowed() > 0) && (!this.rarestStartedPieces.contains(pePiece)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1547 */       this.rarestStartedPieces.add(pePiece);
/*      */     }
/*      */     
/* 1550 */     return requested;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean findRTAPieceToDownload(PEPeerTransport pt, boolean best_uploader, long best_uploader_next_block_eta)
/*      */   {
/* 1562 */     if ((pt == null) || (pt.getPeerState() != 30))
/*      */     {
/* 1564 */       return false;
/*      */     }
/*      */     
/* 1567 */     BitFlags peerHavePieces = pt.getAvailable();
/*      */     
/* 1569 */     if ((peerHavePieces == null) || (peerHavePieces.nbSet <= 0))
/*      */     {
/* 1571 */       return false;
/*      */     }
/*      */     
/* 1574 */     String rta_log_str = (String)null;
/*      */     try
/*      */     {
/* 1577 */       int peerSpeed = (int)pt.getStats().getDataReceiveRate() / 1024;
/*      */       
/* 1579 */       int startI = peerHavePieces.start;
/* 1580 */       int endI = peerHavePieces.end;
/*      */       
/* 1582 */       int piece_min_rta_index = -1;
/* 1583 */       int piece_min_rta_block = 0;
/* 1584 */       long piece_min_rta_time = Long.MAX_VALUE;
/*      */       
/* 1586 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 1588 */       long my_next_block_eta = now + getNextBlockETAFromNow(pt);
/*      */       
/*      */       PEPiece pePiece;
/* 1591 */       for (int i = startI; i <= endI; i++)
/*      */       {
/* 1593 */         long piece_rta = this.provider_piece_rtas[i];
/*      */         
/* 1595 */         if ((peerHavePieces.flags[i] != 0) && (this.startPriorities[i] == 9999999) && (piece_rta > 0L))
/*      */         {
/* 1597 */           DiskManagerPiece dmPiece = this.dmPieces[i];
/*      */           
/* 1599 */           if (dmPiece.isDownloadable())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1604 */             pePiece = this.pePieces[i];
/*      */             
/* 1606 */             if ((pePiece == null) || (!pePiece.isDownloaded()))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1611 */               Object realtime_data = null;
/*      */               
/* 1613 */               boolean try_allocate_even_though_late = (my_next_block_eta > piece_rta) && (best_uploader_next_block_eta > piece_rta);
/*      */               
/*      */ 
/* 1616 */               if (piece_rta < piece_min_rta_time)
/*      */               {
/*      */ 
/*      */ 
/* 1620 */                 if ((my_next_block_eta <= piece_rta) || (best_uploader) || (best_uploader_next_block_eta > piece_rta))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1630 */                   if ((pePiece == null) || ((realtime_data = pePiece.getRealTimeData()) == null))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1637 */                     piece_min_rta_time = piece_rta;
/* 1638 */                     piece_min_rta_index = i;
/* 1639 */                     piece_min_rta_block = 0;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1643 */                     RealTimeData rtd = (RealTimeData)realtime_data;
/*      */                     
/*      */ 
/*      */ 
/* 1647 */                     List[] peer_requests = rtd.getRequests();
/*      */                     
/* 1649 */                     for (int j = 0; j < peer_requests.length; j++)
/*      */                     {
/* 1651 */                       if ((!pePiece.isDownloaded(j)) && (!pePiece.isWritten(j)))
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1658 */                         List block_peer_requests = peer_requests[j];
/*      */                         
/*      */ 
/* 1661 */                         long best_eta = Long.MAX_VALUE;
/*      */                         
/* 1663 */                         boolean pt_already_present = false;
/*      */                         
/*      */ 
/*      */ 
/* 1667 */                         Iterator it = block_peer_requests.iterator();
/*      */                         
/* 1669 */                         while (it.hasNext())
/*      */                         {
/* 1671 */                           RealTimePeerRequest pr = (RealTimePeerRequest)it.next();
/*      */                           
/* 1673 */                           PEPeerTransport this_pt = pr.getPeer();
/*      */                           
/* 1675 */                           if (this_pt.getPeerState() != 30)
/*      */                           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1681 */                             it.remove();
/*      */ 
/*      */                           }
/*      */                           else
/*      */                           {
/*      */ 
/* 1687 */                             DiskManagerReadRequest this_request = pr.getRequest();
/*      */                             
/* 1689 */                             int request_index = this_pt.getRequestIndex(this_request);
/*      */                             
/* 1691 */                             if (request_index == -1)
/*      */                             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1697 */                               it.remove();
/*      */ 
/*      */                             }
/*      */                             else
/*      */                             {
/* 1702 */                               if (this_pt == pt)
/*      */                               {
/* 1704 */                                 pt_already_present = true;
/*      */                                 
/* 1706 */                                 break;
/*      */                               }
/*      */                               
/* 1709 */                               long this_up_bps = this_pt.getStats().getDataReceiveRate();
/*      */                               
/* 1711 */                               if (this_up_bps < 1L)
/*      */                               {
/* 1713 */                                 this_up_bps = 1L;
/*      */                               }
/*      */                               
/* 1716 */                               int next_block_bytes = (request_index + 1) * 16384;
/*      */                               
/* 1718 */                               long this_peer_eta = now + next_block_bytes * 1000 / this_up_bps;
/*      */                               
/* 1720 */                               best_eta = Math.min(best_eta, this_peer_eta);
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                         
/* 1725 */                         if (!pt_already_present)
/*      */                         {
/*      */ 
/*      */ 
/* 1729 */                           if (block_peer_requests.size() == 0)
/*      */                           {
/*      */ 
/*      */ 
/* 1733 */                             piece_min_rta_time = piece_rta;
/* 1734 */                             piece_min_rta_index = i;
/* 1735 */                             piece_min_rta_block = j;
/*      */                             
/* 1737 */                             break;
/*      */                           }
/* 1739 */                           if ((best_eta > piece_rta) && ((best_uploader) || (!try_allocate_even_though_late)))
/*      */                           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1748 */                             if (my_next_block_eta < best_eta)
/*      */                             {
/*      */ 
/*      */ 
/*      */ 
/* 1753 */                               piece_min_rta_time = piece_rta;
/* 1754 */                               piece_min_rta_index = i;
/* 1755 */                               piece_min_rta_block = j;
/*      */                               
/* 1757 */                               break;
/*      */                             } }
/*      */                         }
/*      */                       } }
/*      */                   } } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1766 */       if (piece_min_rta_index != -1)
/*      */       {
/*      */ 
/*      */ 
/* 1770 */         if ((this.dispenser.dispense(1, 16384) == 1) || ((pt.isLANLocal()) && (!includeLanPeersInReqLimiting)))
/*      */         {
/* 1772 */           pePiece = this.pePieces[piece_min_rta_index];
/*      */           
/* 1774 */           if (pePiece == null)
/*      */           {
/*      */ 
/*      */ 
/* 1778 */             pePiece = new PEPieceImpl(pt.getManager(), this.dmPieces[piece_min_rta_index], peerSpeed >> 1);
/*      */             
/*      */ 
/*      */ 
/* 1782 */             this.peerControl.addPiece(pePiece, piece_min_rta_index, pt);
/*      */             
/* 1784 */             pePiece.setResumePriority(9999999);
/*      */             
/* 1786 */             if (this.availability[piece_min_rta_index] <= this.globalMinOthers)
/*      */             {
/* 1788 */               this.nbRarestActive += 1;
/*      */             }
/*      */           }
/*      */           
/* 1792 */           RealTimeData rtd = (RealTimeData)pePiece.getRealTimeData();
/*      */           
/* 1794 */           if (rtd == null)
/*      */           {
/* 1796 */             rtd = new RealTimeData(pePiece);
/*      */             
/* 1798 */             pePiece.setRealTimeData(rtd);
/*      */           }
/*      */           
/* 1801 */           pePiece.getAndMarkBlock(pt, piece_min_rta_block);
/*      */           
/* 1803 */           DiskManagerReadRequest request = pt.request(piece_min_rta_index, piece_min_rta_block * 16384, pePiece.getBlockSize(piece_min_rta_block), true);
/*      */           
/* 1805 */           if (request != null)
/*      */           {
/* 1807 */             real_time_requests = rtd.getRequests()[piece_min_rta_block];
/*      */             
/* 1809 */             real_time_requests.add(new RealTimePeerRequest(pt, request));
/*      */             
/* 1811 */             pt.setLastPiece(piece_min_rta_index);
/*      */             
/* 1813 */             pePiece.setLastRequestedPeerSpeed(peerSpeed);
/*      */             
/* 1815 */             pePiece = 1;return pePiece;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1821 */           if ((!pt.isLANLocal()) || (includeLanPeersInReqLimiting)) {
/* 1822 */             this.dispenser.returnUnusedChunks(1, 16384);
/*      */           }
/* 1824 */           List real_time_requests = 0;return real_time_requests;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1831 */         pePiece = 0;return pePiece;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1838 */       PEPiece pePiece = 0;return pePiece;
/*      */     }
/*      */     finally {}
/*      */   }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int getRequestCandidate(PEPeerTransport pt)
/*      */   {
/* 1870 */     if ((pt == null) || (pt.getPeerState() != 30))
/* 1871 */       return -1;
/* 1872 */     BitFlags peerHavePieces = pt.getAvailable();
/* 1873 */     if ((peerHavePieces == null) || (peerHavePieces.nbSet <= 0)) {
/* 1874 */       return -1;
/*      */     }
/*      */     
/*      */ 
/* 1878 */     int[] reservedPieceNumbers = pt.getReservedPieceNumbers();
/*      */     
/*      */ 
/*      */ 
/* 1882 */     if (reservedPieceNumbers != null)
/*      */     {
/* 1884 */       for (int reservedPieceNumber : reservedPieceNumbers)
/*      */       {
/* 1886 */         PEPiece pePiece = this.pePieces[reservedPieceNumber];
/*      */         
/* 1888 */         if (pePiece != null)
/*      */         {
/* 1890 */           String peerReserved = pePiece.getReservedBy();
/*      */           
/* 1892 */           if ((peerReserved != null) && (peerReserved.equals(pt.getIp())))
/*      */           {
/* 1894 */             if ((peerHavePieces.flags[reservedPieceNumber] != 0) && (pePiece.isRequestable()))
/*      */             {
/* 1896 */               return reservedPieceNumber;
/*      */             }
/*      */             
/* 1899 */             pePiece.setReservedBy(null);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1906 */         pt.removeReservedPieceNumber(reservedPieceNumber);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1912 */     int reservedPieceNumber = -1;
/*      */     
/* 1914 */     int peerSpeed = (int)pt.getStats().getDataReceiveRate() / 1024;
/* 1915 */     int lastPiece = pt.getLastPiece();
/*      */     
/* 1917 */     int nbSnubbed = this.peerControl.getNbPeersSnubbed();
/*      */     
/* 1919 */     long resumeMinAvail = Long.MAX_VALUE;
/* 1920 */     int resumeMaxPriority = Integer.MIN_VALUE;
/* 1921 */     boolean resumeIsRarest = false;
/*      */     
/* 1923 */     int secondChoiceResume = -1;
/*      */     
/* 1925 */     BitFlags startCandidates = null;
/* 1926 */     int startMaxPriority = Integer.MIN_VALUE;
/* 1927 */     int startMinAvail = Integer.MAX_VALUE;
/* 1928 */     boolean startIsRarest = false;
/* 1929 */     boolean forceStart = false;
/*      */     
/*      */ 
/* 1932 */     int avail = 0;
/*      */     
/*      */ 
/* 1935 */     boolean rarestAllowed = calcRarestAllowed() > 0;
/* 1936 */     int startI = peerHavePieces.start;
/* 1937 */     int endI = peerHavePieces.end;
/*      */     
/*      */ 
/* 1940 */     int[] peerPriorities = pt.getPriorityOffsets();
/*      */     
/* 1942 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1944 */     int[] request_hint = pt.getRequestHint();
/*      */     
/*      */     int request_hint_piece_number;
/* 1947 */     if (request_hint != null)
/*      */     {
/* 1949 */       int request_hint_piece_number = request_hint[0];
/*      */       
/* 1951 */       if (this.dmPieces[request_hint_piece_number].isDone())
/*      */       {
/* 1953 */         pt.clearRequestHint();
/*      */         
/* 1955 */         request_hint_piece_number = -1;
/*      */       }
/*      */     }
/*      */     else {
/* 1959 */       request_hint_piece_number = -1;
/*      */     }
/*      */     
/* 1962 */     if (request_hint_piece_number == -1)
/*      */     {
/* 1964 */       int[] g_hint = this.global_request_hint;
/*      */       
/* 1966 */       if (g_hint != null)
/*      */       {
/* 1968 */         request_hint_piece_number = g_hint[0];
/*      */         
/* 1970 */         if (this.dmPieces[request_hint_piece_number].isDone())
/*      */         {
/* 1972 */           g_hint = null;
/*      */           
/* 1974 */           request_hint_piece_number = -1;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1979 */     CopyOnWriteSet<Integer> forced = this.forced_pieces;
/*      */     
/*      */ 
/*      */ 
/* 1983 */     for (int i = startI; i <= endI; i++)
/*      */     {
/*      */ 
/*      */ 
/* 1987 */       if (peerHavePieces.flags[i] != 0)
/*      */       {
/* 1989 */         int priority = this.startPriorities[i];
/*      */         
/* 1991 */         DiskManagerPiece dmPiece = this.dmPieces[i];
/*      */         
/* 1993 */         if ((priority >= 0) && (dmPiece.isDownloadable()))
/*      */         {
/* 1995 */           if (peerPriorities != null)
/*      */           {
/* 1997 */             int peer_priority = peerPriorities[i];
/*      */             
/* 1999 */             if (peer_priority >= 0)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2004 */               priority += peer_priority;
/*      */             }
/*      */           } else {
/* 2007 */             if ((enable_request_hints) && (i == request_hint_piece_number))
/*      */             {
/* 2009 */               priority += 3000;
/*      */               
/* 2011 */               PEPiece pePiece = this.pePieces[i];
/*      */               
/* 2013 */               if (pePiece == null) {
/* 2014 */                 forceStart = true;
/*      */               } else {
/* 2016 */                 pePiece.setReservedBy(pt.getIp());
/* 2017 */                 pt.addReservedPieceNumber(i);
/*      */               }
/*      */             }
/*      */             
/* 2021 */             PEPiece pePiece = this.pePieces[i];
/*      */             
/*      */ 
/*      */ 
/* 2025 */             if ((pePiece == null) || (pePiece.isRequestable()))
/*      */             {
/*      */ 
/* 2028 */               boolean pieceRarestOverride = priority >= 9000 ? true : rarestAllowed;
/*      */               
/*      */ 
/*      */ 
/* 2032 */               avail = this.availability[i];
/* 2033 */               if (avail == 0)
/*      */               {
/* 2035 */                 this.availability[i] = 1;
/* 2036 */                 avail = 1;
/* 2037 */               } else if ((forced != null) && (forced.contains(Integer.valueOf(i)))) {
/* 2038 */                 avail = this.globalMinOthers;
/*      */               }
/*      */               
/*      */ 
/* 2042 */               if (pePiece != null)
/*      */               {
/* 2044 */                 if (priority != this.startPriorities[i]) {
/* 2045 */                   pePiece.setResumePriority(priority);
/*      */                 }
/* 2047 */                 boolean startedRarest = this.rarestStartedPieces.contains(pePiece);
/* 2048 */                 boolean rarestPrio = (avail <= this.globalMinOthers) && ((startedRarest) || (rarestAllowed));
/*      */                 
/*      */ 
/* 2051 */                 int freeReqs = pePiece.getNbUnrequested();
/* 2052 */                 if (freeReqs <= 0)
/*      */                 {
/* 2054 */                   pePiece.setRequested();
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*      */ 
/* 2060 */                   String peerReserved = pePiece.getReservedBy();
/* 2061 */                   if (peerReserved != null)
/*      */                   {
/* 2063 */                     if (peerReserved.equals(pt.getIp()))
/*      */                     {
/*      */ 
/* 2066 */                       pt.addReservedPieceNumber(i);
/* 2067 */                       return i;
/*      */                     }
/*      */                   } else {
/* 2070 */                     int pieceSpeed = pePiece.getSpeed();
/*      */                     
/*      */ 
/* 2073 */                     boolean mayResume = true;
/*      */                     
/* 2075 */                     if (pt.isSnubbed())
/*      */                     {
/*      */ 
/*      */ 
/* 2079 */                       mayResume &= pieceSpeed < 1;
/* 2080 */                       mayResume &= ((freeReqs > 2) || (avail <= nbSnubbed));
/*      */                     }
/*      */                     else
/*      */                     {
/* 2084 */                       mayResume &= freeReqs * peerSpeed >= pieceSpeed / 2;
/*      */                       
/* 2086 */                       mayResume &= ((peerSpeed < 2) || (pieceSpeed > 0) || (pePiece.getNbRequests() == 0));
/* 2087 */                       mayResume |= i == pt.getLastPiece();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/* 2093 */                     if ((secondChoiceResume == -1) || (avail > this.availability[secondChoiceResume])) {
/* 2094 */                       secondChoiceResume = i;
/*      */                     }
/* 2096 */                     if (mayResume)
/*      */                     {
/* 2098 */                       if (avail <= resumeMinAvail)
/*      */                       {
/*      */ 
/* 2101 */                         priority += pieceSpeed;
/* 2102 */                         priority += (i == lastPiece ? 700 : 0);
/*      */                         
/*      */ 
/* 2105 */                         priority = (int)(priority + pePiece.getTimeSinceLastActivity() / 120000L);
/*      */                         
/* 2107 */                         long pieceAge = now - pePiece.getCreationTime();
/* 2108 */                         if (pieceAge > 0L) {
/* 2109 */                           priority = (int)(priority + 900L * pieceAge / (60000 * dmPiece.getNbBlocks()));
/*      */                         }
/* 2111 */                         priority += 900 * dmPiece.getNbWritten() / dmPiece.getNbBlocks();
/*      */                         
/* 2113 */                         pePiece.setResumePriority(priority);
/*      */                         
/* 2115 */                         if ((avail < resumeMinAvail) || ((avail == resumeMinAvail) && (priority > resumeMaxPriority)))
/*      */                         {
/*      */ 
/* 2118 */                           if (pePiece.hasUnrequestedBlock())
/*      */                           {
/* 2120 */                             reservedPieceNumber = i;
/* 2121 */                             resumeMinAvail = avail;
/* 2122 */                             resumeMaxPriority = priority;
/* 2123 */                             resumeMinAvail = avail;
/* 2124 */                             resumeIsRarest = rarestPrio;
/*      */                           } }
/*      */                       } }
/* 2127 */                   } } } else if ((avail <= this.globalMinOthers) && (rarestAllowed))
/*      */               {
/* 2129 */                 if (!startIsRarest)
/*      */                 {
/* 2131 */                   if (startCandidates == null)
/* 2132 */                     startCandidates = new BitFlags(this.nbPieces);
/* 2133 */                   startMaxPriority = priority;
/* 2134 */                   startMinAvail = avail;
/* 2135 */                   startIsRarest = avail <= this.globalMinOthers;
/* 2136 */                   startCandidates.setOnly(i);
/* 2137 */                 } else if (priority > startMaxPriority)
/*      */                 {
/* 2139 */                   if (startCandidates == null)
/* 2140 */                     startCandidates = new BitFlags(this.nbPieces);
/* 2141 */                   startMaxPriority = priority;
/* 2142 */                   startCandidates.setOnly(i);
/* 2143 */                 } else if (priority == startMaxPriority)
/*      */                 {
/* 2145 */                   startCandidates.setEnd(i);
/*      */                 }
/* 2147 */               } else if ((!startIsRarest) || (!rarestAllowed))
/*      */               {
/* 2149 */                 if (priority > startMaxPriority)
/*      */                 {
/* 2151 */                   if (startCandidates == null)
/* 2152 */                     startCandidates = new BitFlags(this.nbPieces);
/* 2153 */                   startMaxPriority = priority;
/* 2154 */                   startMinAvail = avail;
/* 2155 */                   startIsRarest = avail <= this.globalMinOthers;
/* 2156 */                   startCandidates.setOnly(i);
/* 2157 */                 } else if (priority == startMaxPriority)
/*      */                 {
/* 2159 */                   if (startCandidates == null) {
/* 2160 */                     startCandidates = new BitFlags(this.nbPieces);
/*      */                   }
/* 2162 */                   if (avail < startMinAvail)
/*      */                   {
/* 2164 */                     startMinAvail = avail;
/* 2165 */                     startIsRarest = avail <= this.globalMinOthers;
/* 2166 */                     startCandidates.setOnly(i);
/* 2167 */                   } else if (avail == startMinAvail)
/*      */                   {
/* 2169 */                     startCandidates.setEnd(i);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2185 */     if ((!forceStart) || (startCandidates == null) || (startCandidates.nbSet <= 0))
/*      */     {
/*      */ 
/* 2188 */       if ((reservedPieceNumber >= 0) && ((resumeIsRarest) || (!startIsRarest) || (!rarestAllowed) || (startCandidates == null) || (startCandidates.nbSet <= 0))) {
/* 2189 */         return reservedPieceNumber;
/*      */       }
/* 2191 */       if ((secondChoiceResume != -1) && ((startCandidates == null) || (startCandidates.nbSet <= 0)))
/*      */       {
/*      */ 
/* 2194 */         return secondChoiceResume;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2203 */       if ((reservedPieceNumber >= 0) && (this.globalMinOthers > 0) && (this.peerControl.getNbActivePieces() > 32))
/*      */       {
/* 2205 */         boolean resumeIsBetter = resumeMaxPriority / resumeMinAvail > startMaxPriority / this.globalMinOthers;
/*      */         
/* 2207 */         if ((Constants.isCVSVersion()) && (Logger.isEnabled())) {
/* 2208 */           Logger.log(new LogEvent(new Object[] { pt, this.peerControl }, LOGID, "Start/resume choice; piece #:" + reservedPieceNumber + " resumeIsBetter:" + resumeIsBetter + " globalMinOthers=" + this.globalMinOthers + " startMaxPriority=" + startMaxPriority + " startMinAvail=" + startMinAvail + " resumeMaxPriority=" + resumeMaxPriority + " resumeMinAvail=" + resumeMinAvail + " : " + pt));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2215 */         if (resumeIsBetter) {
/* 2216 */           return reservedPieceNumber;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2222 */     return getPieceToStart(startCandidates);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int getPieceToStart(BitFlags startCandidates)
/*      */   {
/* 2234 */     if ((startCandidates == null) || (startCandidates.nbSet <= 0))
/* 2235 */       return -1;
/* 2236 */     if (startCandidates.nbSet == 1) {
/* 2237 */       return startCandidates.start;
/*      */     }
/* 2239 */     int direction = RandomUtils.generateRandomPlusMinus1();
/*      */     int startI;
/* 2241 */     int startI; if (direction == 1) {
/* 2242 */       startI = startCandidates.start;
/*      */     } else {
/* 2244 */       startI = startCandidates.end;
/*      */     }
/*      */     
/* 2247 */     int targetNb = RandomUtils.generateRandomIntUpto(startCandidates.nbSet);
/*      */     
/*      */ 
/* 2250 */     int foundNb = -1;
/* 2251 */     for (int i = startI; (i <= startCandidates.end) && (i >= startCandidates.start); i += direction)
/*      */     {
/*      */ 
/* 2254 */       if (startCandidates.flags[i] != 0)
/*      */       {
/* 2256 */         foundNb++;
/* 2257 */         if (foundNb >= targetNb)
/* 2258 */           return i;
/*      */       }
/*      */     }
/* 2261 */     return -1;
/*      */   }
/*      */   
/*      */   public final boolean hasDownloadablePiece()
/*      */   {
/* 2266 */     return this.hasNeededUndonePiece;
/*      */   }
/*      */   
/*      */   public final long getNeededUndonePieceChange()
/*      */   {
/* 2271 */     return this.neededUndonePieceChange;
/*      */   }
/*      */   
/*      */ 
/*      */   private final void checkEndGameMode()
/*      */   {
/* 2277 */     if (this.peerControl.getNbSeeds() + this.peerControl.getNbPeers() < 3) {
/* 2278 */       return;
/*      */     }
/*      */     
/* 2281 */     long mono_now = SystemTime.getMonotonousTime();
/*      */     
/* 2283 */     if ((this.endGameMode) || (this.endGameModeAbandoned))
/*      */     {
/* 2285 */       if (!this.endGameModeAbandoned)
/*      */       {
/* 2287 */         if (mono_now - this.timeEndGameModeEntered > 76800L)
/*      */         {
/* 2289 */           abandonEndGameMode();
/*      */         }
/*      */       }
/*      */       
/* 2293 */       return;
/*      */     }
/*      */     
/* 2296 */     int active_pieces = 0;
/* 2297 */     int reserved_pieces = 0;
/*      */     
/* 2299 */     for (int i = 0; i < this.nbPieces; i++)
/*      */     {
/* 2301 */       DiskManagerPiece dmPiece = this.dmPieces[i];
/*      */       
/*      */ 
/*      */ 
/* 2305 */       if (dmPiece.isDownloadable())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2310 */         PEPiece pePiece = this.pePieces[i];
/*      */         
/* 2312 */         if (pePiece != null)
/*      */         {
/* 2314 */           if (pePiece.isDownloaded()) {
/*      */             continue;
/*      */           }
/*      */           
/*      */ 
/* 2319 */           if (dmPiece.isNeeded())
/*      */           {
/*      */ 
/*      */ 
/* 2323 */             if (pePiece.isRequested())
/*      */             {
/* 2325 */               active_pieces++;
/*      */               
/* 2327 */               continue;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2334 */             if (pePiece.getReservedBy() != null)
/*      */             {
/* 2336 */               reserved_pieces++;
/*      */               
/* 2338 */               if (reserved_pieces * this.diskManager.getPieceLength() <= 5242880L)
/*      */                 continue;
/* 2340 */               return;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2350 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2356 */     boolean use_rta_egm = this.rta_providers.size() > 0;
/*      */     
/* 2358 */     long remaining = active_pieces * this.diskManager.getPieceLength();
/*      */     
/* 2360 */     long trigger = use_rta_egm ? 262144L : 20971520L;
/*      */     
/*      */ 
/*      */ 
/* 2364 */     if (remaining <= trigger) {
/*      */       try
/*      */       {
/* 2367 */         this.endGameModeChunks_mon.enter();
/*      */         
/* 2369 */         this.endGameModeChunks = new ArrayList();
/*      */         
/* 2371 */         this.timeEndGameModeEntered = mono_now;
/*      */         
/* 2373 */         this.endGameMode = true;
/*      */         
/* 2375 */         computeEndGameModeChunks();
/*      */         
/* 2377 */         if (Logger.isEnabled()) {
/* 2378 */           Logger.log(new LogEvent(this.diskManager.getTorrent(), LOGID, "Entering end-game mode: " + this.peerControl.getDisplayName()));
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 2383 */         this.endGameModeChunks_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private final void computeEndGameModeChunks()
/*      */   {
/*      */     try {
/* 2391 */       this.endGameModeChunks_mon.enter();
/*      */       
/* 2393 */       for (int i = 0; i < this.nbPieces; i++)
/*      */       {
/* 2395 */         DiskManagerPiece dmPiece = this.dmPieces[i];
/*      */         
/*      */ 
/*      */ 
/* 2399 */         if (dmPiece.isInteresting())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2404 */           PEPiece pePiece = this.pePieces[i];
/*      */           
/* 2406 */           if (pePiece == null)
/*      */           {
/* 2408 */             pePiece = new PEPieceImpl(this.peerControl, dmPiece, 0);
/*      */             
/* 2410 */             this.peerControl.addPiece(pePiece, i, null);
/*      */           }
/*      */           
/* 2413 */           boolean[] written = dmPiece.getWritten();
/*      */           
/* 2415 */           if (written == null)
/*      */           {
/* 2417 */             if (!dmPiece.isDone())
/*      */             {
/* 2419 */               for (int j = 0; j < pePiece.getNbBlocks(); j++)
/*      */               {
/* 2421 */                 this.endGameModeChunks.add(new EndGameModeChunk(pePiece, j));
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/* 2426 */             for (int j = 0; j < written.length; j++)
/*      */             {
/* 2428 */               if (written[j] == 0)
/* 2429 */                 this.endGameModeChunks.add(new EndGameModeChunk(pePiece, j));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 2435 */       this.endGameModeChunks_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final boolean isInEndGameMode()
/*      */   {
/* 2442 */     return this.endGameMode;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasEndGameModeBeenAbandoned()
/*      */   {
/* 2448 */     return this.endGameModeAbandoned;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void addEndGameChunks(PEPiece pePiece)
/*      */   {
/* 2458 */     if (!this.endGameMode) {
/* 2459 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2463 */       this.endGameModeChunks_mon.enter();
/*      */       
/* 2465 */       int nbChunks = pePiece.getNbBlocks();
/*      */       
/* 2467 */       for (int i = 0; i < nbChunks; i++)
/*      */       {
/* 2469 */         this.endGameModeChunks.add(new EndGameModeChunk(pePiece, i));
/*      */       }
/*      */     }
/*      */     finally {
/* 2473 */       this.endGameModeChunks_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int findPieceInEndGameMode(PEPeerTransport pt, int wants)
/*      */   {
/* 2485 */     if ((pt == null) || (wants <= 0) || (pt.getPeerState() != 30))
/*      */     {
/* 2487 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 2493 */       this.endGameModeChunks_mon.enter();
/*      */       
/* 2495 */       int nbChunks = this.endGameModeChunks.size();
/*      */       
/* 2497 */       if (nbChunks > 0)
/*      */       {
/* 2499 */         int random = RandomUtils.generateRandomIntUpto(nbChunks);
/*      */         
/* 2501 */         EndGameModeChunk chunk = (EndGameModeChunk)this.endGameModeChunks.get(random);
/*      */         
/* 2503 */         int pieceNumber = chunk.getPieceNumber();
/*      */         
/* 2505 */         if (this.dmPieces[pieceNumber].isWritten(chunk.getBlockNumber()))
/*      */         {
/* 2507 */           this.endGameModeChunks.remove(chunk);
/*      */           
/* 2509 */           return 0;
/*      */         }
/*      */         
/* 2512 */         PEPiece pePiece = this.pePieces[pieceNumber];
/*      */         
/* 2514 */         if ((pt.isPieceAvailable(pieceNumber)) && (pePiece != null)) {
/*      */           int j;
/* 2516 */           if (((!pt.isSnubbed()) || (this.availability[pieceNumber] <= this.peerControl.getNbPeersSnubbed())) && (pt.request(pieceNumber, chunk.getOffset(), chunk.getLength(), false) != null))
/*      */           {
/*      */ 
/* 2519 */             pePiece.setRequested(pt, chunk.getBlockNumber());
/*      */             
/* 2521 */             pt.setLastPiece(pieceNumber);
/*      */             
/* 2523 */             return 1;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2528 */           return 0;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2537 */       leaveEndGameMode();
/*      */     }
/*      */     finally
/*      */     {
/* 2541 */       this.endGameModeChunks_mon.exit();
/*      */     }
/*      */     
/* 2544 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void removeFromEndGameModeChunks(int pieceNumber, int offset)
/*      */   {
/* 2552 */     if (!this.endGameMode)
/*      */     {
/* 2554 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2558 */       this.endGameModeChunks_mon.enter();
/*      */       
/* 2560 */       Iterator iter = this.endGameModeChunks.iterator();
/*      */       
/* 2562 */       while (iter.hasNext())
/*      */       {
/* 2564 */         EndGameModeChunk chunk = (EndGameModeChunk)iter.next();
/*      */         
/* 2566 */         if (chunk.equals(pieceNumber, offset))
/*      */         {
/* 2568 */           iter.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2573 */       this.endGameModeChunks_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final void clearEndGameChunks()
/*      */   {
/* 2580 */     if (!this.endGameMode)
/*      */     {
/* 2582 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2586 */       this.endGameModeChunks_mon.enter();
/*      */       
/* 2588 */       this.endGameModeChunks.clear();
/*      */       
/* 2590 */       this.endGameMode = false;
/*      */     }
/*      */     finally
/*      */     {
/* 2594 */       this.endGameModeChunks_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void leaveEndGameMode()
/*      */   {
/*      */     try
/*      */     {
/* 2602 */       this.endGameModeChunks_mon.enter();
/*      */       
/* 2604 */       if (this.endGameMode)
/*      */       {
/* 2606 */         if (Logger.isEnabled())
/*      */         {
/* 2608 */           Logger.log(new LogEvent(this.diskManager.getTorrent(), LOGID, "Leaving end-game mode: " + this.peerControl.getDisplayName()));
/*      */         }
/*      */         
/*      */ 
/* 2612 */         this.endGameMode = false;
/*      */         
/* 2614 */         this.endGameModeChunks.clear();
/*      */         
/* 2616 */         this.timeEndGameModeEntered = 0L;
/*      */       }
/*      */     }
/*      */     finally {
/* 2620 */       this.endGameModeChunks_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void abandonEndGameMode()
/*      */   {
/* 2627 */     if (!this.endGameModeAbandoned) {
/*      */       try
/*      */       {
/* 2630 */         this.endGameModeChunks_mon.enter();
/*      */         
/* 2632 */         this.endGameModeAbandoned = true;
/*      */         
/* 2634 */         this.endGameMode = false;
/*      */         
/* 2636 */         clearEndGameChunks();
/*      */         
/* 2638 */         if (Logger.isEnabled()) {
/* 2639 */           Logger.log(new LogEvent(this.diskManager.getTorrent(), LOGID, "Abandoning end-game mode: " + this.peerControl.getDisplayName()));
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 2644 */         this.endGameModeChunks_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean computeProviderPriorities()
/*      */   {
/* 2652 */     List p_ps = this.priority_providers.getList();
/*      */     
/* 2654 */     if (p_ps.size() == 0)
/*      */     {
/* 2656 */       if (this.provider_piece_priorities != null)
/*      */       {
/* 2658 */         paramPriorityChange += 1L;
/*      */         
/* 2660 */         this.provider_piece_priorities = null;
/*      */       }
/*      */     }
/*      */     else {
/* 2664 */       paramPriorityChange += 1L;
/*      */       
/* 2666 */       this.provider_piece_priorities = new long[this.nbPieces];
/*      */       
/* 2668 */       for (int i = 0; i < p_ps.size(); i++)
/*      */       {
/* 2670 */         PiecePriorityProvider shaper = (PiecePriorityProvider)p_ps.get(i);
/*      */         
/* 2672 */         long[] priorities = shaper.updatePriorities(this);
/*      */         
/* 2674 */         if (priorities != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2679 */           for (int j = 0; j < priorities.length; j++)
/*      */           {
/* 2681 */             long priority = priorities[j];
/*      */             
/* 2683 */             if (priority != 0L)
/*      */             {
/* 2685 */               this.provider_piece_priorities[j] += priority;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2691 */     List rta_ps = this.rta_providers.getList();
/*      */     
/* 2693 */     if (rta_ps.size() == 0)
/*      */     {
/* 2695 */       if (this.provider_piece_rtas != null)
/*      */       {
/*      */ 
/*      */ 
/* 2699 */         for (int i = 0; i < this.pePieces.length; i++)
/*      */         {
/* 2701 */           PEPiece piece = this.pePieces[i];
/*      */           
/* 2703 */           if (piece != null)
/*      */           {
/* 2705 */             piece.setRealTimeData(null);
/*      */           }
/*      */         }
/*      */         
/* 2709 */         this.provider_piece_rtas = null;
/*      */       }
/*      */       
/* 2712 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 2716 */     boolean has_rta = false;
/*      */     
/*      */ 
/*      */ 
/* 2720 */     this.provider_piece_rtas = new long[this.nbPieces];
/*      */     
/* 2722 */     for (int i = 0; i < rta_ps.size(); i++)
/*      */     {
/* 2724 */       PieceRTAProvider shaper = (PieceRTAProvider)rta_ps.get(i);
/*      */       
/* 2726 */       long[] offsets = shaper.updateRTAs(this);
/*      */       
/* 2728 */       if (offsets != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2733 */         for (int j = 0; j < offsets.length; j++)
/*      */         {
/* 2735 */           long rta = offsets[j];
/*      */           
/* 2737 */           if (rta > 0L)
/*      */           {
/* 2739 */             if (this.provider_piece_rtas[j] == 0L)
/*      */             {
/* 2741 */               this.provider_piece_rtas[j] = rta;
/*      */             }
/*      */             else
/*      */             {
/* 2745 */               this.provider_piece_rtas[j] = Math.min(this.provider_piece_rtas[j], rta);
/*      */             }
/*      */             
/* 2748 */             has_rta = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2753 */     return has_rta;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRTAProvider(PieceRTAProvider provider)
/*      */   {
/* 2761 */     this.rta_providers.add(provider);
/*      */     
/* 2763 */     Iterator it = this.listeners.iterator();
/*      */     
/* 2765 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 2768 */         ((PiecePickerListener)it.next()).providerAdded(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2772 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2781 */     leaveEndGameMode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeRTAProvider(PieceRTAProvider provider)
/*      */   {
/* 2788 */     this.rta_providers.remove(provider);
/*      */     
/* 2790 */     Iterator it = this.listeners.iterator();
/*      */     
/* 2792 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 2795 */         ((PiecePickerListener)it.next()).providerRemoved(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2799 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public List getRTAProviders()
/*      */   {
/* 2807 */     return this.rta_providers.getList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPriorityProvider(PiecePriorityProvider provider)
/*      */   {
/* 2814 */     this.priority_providers.add(provider);
/*      */     
/* 2816 */     Iterator it = this.listeners.iterator();
/*      */     
/* 2818 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 2821 */         ((PiecePickerListener)it.next()).providerAdded(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2825 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePriorityProvider(PiecePriorityProvider provider)
/*      */   {
/* 2834 */     this.priority_providers.remove(provider);
/*      */     
/* 2836 */     Iterator it = this.listeners.iterator();
/*      */     
/* 2838 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 2841 */         ((PiecePickerListener)it.next()).providerRemoved(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2845 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public List getPriorityProviders()
/*      */   {
/* 2853 */     return this.rta_providers.getList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(PiecePickerListener listener)
/*      */   {
/* 2860 */     this.listeners.add(listener);
/*      */     
/* 2862 */     Iterator it = this.rta_providers.iterator();
/*      */     
/* 2864 */     while (it.hasNext())
/*      */     {
/* 2866 */       listener.providerAdded((PieceRTAProvider)it.next());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(PiecePickerListener listener)
/*      */   {
/* 2874 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class PEPeerManagerListenerImpl
/*      */     extends PEPeerManagerListenerAdapter
/*      */   {
/*      */     private PEPeerManagerListenerImpl() {}
/*      */     
/*      */ 
/*      */ 
/*      */     public final void peerAdded(PEPeerManager manager, PEPeer peer)
/*      */     {
/* 2888 */       PiecePickerImpl.PEPeerListenerImpl peerListener = (PiecePickerImpl.PEPeerListenerImpl)PiecePickerImpl.this.peerListeners.get(peer);
/* 2889 */       if (peerListener == null)
/*      */       {
/* 2891 */         peerListener = new PiecePickerImpl.PEPeerListenerImpl(PiecePickerImpl.this, null);
/* 2892 */         PiecePickerImpl.this.peerListeners.put(peer, peerListener);
/*      */       }
/* 2894 */       peer.addListener(peerListener);
/*      */     }
/*      */     
/*      */ 
/*      */     public final void peerRemoved(PEPeerManager manager, PEPeer peer)
/*      */     {
/* 2900 */       PiecePickerImpl.PEPeerListenerImpl peerListener = (PiecePickerImpl.PEPeerListenerImpl)PiecePickerImpl.this.peerListeners.remove(peer);
/* 2901 */       peer.removeListener(peerListener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private class PEPeerListenerImpl
/*      */     implements org.gudy.azureus2.core3.peer.PEPeerListener
/*      */   {
/*      */     private PEPeerListenerImpl() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public final void stateChanged(PEPeer peer, int newState) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public final void sentBadChunk(PEPeer peer, int piece_num, int total_bad_chunks) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public final void addAvailability(PEPeer peer, BitFlags peerHavePieces)
/*      */     {
/* 2941 */       if ((peerHavePieces == null) || (peerHavePieces.nbSet <= 0))
/* 2942 */         return;
/*      */       try {
/* 2944 */         PiecePickerImpl.this.availabilityMon.enter();
/* 2945 */         if (PiecePickerImpl.this.availabilityAsynch == null) {
/* 2946 */           PiecePickerImpl.this.availabilityAsynch = ((int[])PiecePickerImpl.this.availability.clone());
/*      */         }
/* 2948 */         for (int i = peerHavePieces.start; i <= peerHavePieces.end; i++)
/*      */         {
/* 2950 */           if (peerHavePieces.flags[i] != 0) {
/* 2951 */             PiecePickerImpl.this.availabilityAsynch[i] += 1;
/*      */           }
/*      */         }
/* 2954 */         PiecePickerImpl.this.availabilityChange += 1L;
/* 2955 */       } finally { PiecePickerImpl.this.availabilityMon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public final void removeAvailability(PEPeer peer, BitFlags peerHavePieces)
/*      */     {
/* 2965 */       if ((peerHavePieces == null) || (peerHavePieces.nbSet <= 0))
/* 2966 */         return;
/*      */       try {
/* 2968 */         PiecePickerImpl.this.availabilityMon.enter();
/* 2969 */         if (PiecePickerImpl.this.availabilityAsynch == null)
/*      */         {
/* 2971 */           PiecePickerImpl.this.availabilityAsynch = ((int[])PiecePickerImpl.this.availability.clone());
/*      */         }
/* 2973 */         for (int i = peerHavePieces.start; i <= peerHavePieces.end; i++)
/*      */         {
/* 2975 */           if (peerHavePieces.flags[i] != 0)
/*      */           {
/* 2977 */             if (PiecePickerImpl.this.availabilityAsynch[i] > (PiecePickerImpl.this.dmPieces[i].isDone() ? 1 : 0)) {
/* 2978 */               PiecePickerImpl.this.availabilityAsynch[i] -= 1;
/*      */             } else
/* 2980 */               PiecePickerImpl.this.availabilityDrift += 1L;
/*      */           }
/*      */         }
/* 2983 */         PiecePickerImpl.this.availabilityChange += 1L;
/* 2984 */       } finally { PiecePickerImpl.this.availabilityMon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class DiskManagerListenerImpl
/*      */     implements org.gudy.azureus2.core3.disk.DiskManagerListener
/*      */   {
/*      */     private DiskManagerListenerImpl() {}
/*      */     
/*      */ 
/*      */     public final void stateChanged(int oldState, int newState) {}
/*      */     
/*      */ 
/*      */     public final void filePriorityChanged(DiskManagerFileInfo file)
/*      */     {
/* 3002 */       PiecePickerImpl.this.syncFilePriorities();
/*      */       
/* 3004 */       PiecePickerImpl.this.filePriorityChange += 1L;
/*      */       
/*      */ 
/* 3007 */       boolean foundPieceToDownload = false;
/*      */       
/*      */       int endI;
/*      */       
/*      */       int startI;
/*      */       int endI;
/* 3013 */       if (PiecePickerImpl.this.hasNeededUndonePiece)
/*      */       {
/* 3015 */         int startI = 0;
/* 3016 */         endI = PiecePickerImpl.this.nbPieces;
/*      */       }
/*      */       else {
/* 3019 */         startI = file.getFirstPieceNumber();
/* 3020 */         endI = file.getLastPieceNumber() + 1;
/*      */       }
/* 3022 */       for (int i = startI; i < endI; i++)
/*      */       {
/* 3024 */         DiskManagerPiece dmPiece = PiecePickerImpl.this.dmPieces[i];
/* 3025 */         if (!dmPiece.isDone())
/* 3026 */           foundPieceToDownload |= dmPiece.calcNeeded();
/*      */       }
/* 3028 */       if ((foundPieceToDownload ^ PiecePickerImpl.this.hasNeededUndonePiece))
/*      */       {
/* 3030 */         PiecePickerImpl.this.hasNeededUndonePiece = foundPieceToDownload;
/* 3031 */         PiecePickerImpl.this.neededUndonePieceChange += 1L;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public final void pieceDoneChanged(DiskManagerPiece dmPiece)
/*      */     {
/* 3038 */       int pieceNumber = dmPiece.getPieceNumber();
/* 3039 */       if (dmPiece.isDone())
/*      */       {
/* 3041 */         PiecePickerImpl.this.addHavePiece(null, pieceNumber);
/* 3042 */         PiecePickerImpl.this.nbPiecesDone += 1;
/* 3043 */         if (PiecePickerImpl.this.nbPiecesDone >= PiecePickerImpl.this.nbPieces) {
/* 3044 */           PiecePickerImpl.this.checkDownloadablePiece();
/*      */         }
/*      */       } else {
/*      */         try {
/* 3048 */           PiecePickerImpl.this.availabilityMon.enter();
/* 3049 */           if (PiecePickerImpl.this.availabilityAsynch == null) {
/* 3050 */             PiecePickerImpl.this.availabilityAsynch = ((int[])PiecePickerImpl.this.availability.clone());
/*      */           }
/* 3052 */           if (PiecePickerImpl.this.availabilityAsynch[pieceNumber] > 0) {
/* 3053 */             PiecePickerImpl.this.availabilityAsynch[pieceNumber] -= 1;
/*      */           } else
/* 3055 */             PiecePickerImpl.this.availabilityDrift += 1L;
/* 3056 */           PiecePickerImpl.this.availabilityChange += 1L;
/* 3057 */         } finally { PiecePickerImpl.this.availabilityMon.exit(); }
/* 3058 */         PiecePickerImpl.this.nbPiecesDone -= 1;
/* 3059 */         if ((dmPiece.calcNeeded()) && (!PiecePickerImpl.this.hasNeededUndonePiece))
/*      */         {
/* 3061 */           PiecePickerImpl.this.hasNeededUndonePiece = true;
/* 3062 */           PiecePickerImpl.this.neededUndonePieceChange += 1L;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public final void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setForcePiece(int pieceNumber, boolean forced)
/*      */   {
/* 3079 */     if ((pieceNumber < 0) || (pieceNumber >= this.nbPieces))
/*      */     {
/* 3081 */       Debug.out("Invalid piece number: " + pieceNumber);
/*      */       
/* 3083 */       return;
/*      */     }
/*      */     
/* 3086 */     synchronized (this)
/*      */     {
/* 3088 */       CopyOnWriteSet<Integer> set = this.forced_pieces;
/*      */       
/* 3090 */       if (set == null)
/*      */       {
/* 3092 */         if (!forced)
/*      */         {
/* 3094 */           return;
/*      */         }
/*      */         
/* 3097 */         set = new CopyOnWriteSet(false);
/*      */         
/* 3099 */         this.forced_pieces = set;
/*      */       }
/*      */       
/* 3102 */       if (forced)
/*      */       {
/* 3104 */         set.add(Integer.valueOf(pieceNumber));
/*      */       }
/*      */       else
/*      */       {
/* 3108 */         set.remove(Integer.valueOf(pieceNumber));
/*      */         
/* 3110 */         if (set.size() == 0)
/*      */         {
/* 3112 */           this.forced_pieces = null;
/*      */         }
/*      */       }
/*      */     }
/* 3116 */     paramPriorityChange += 1L;
/*      */     
/* 3118 */     computeBasePriorities();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isForcePiece(int pieceNumber)
/*      */   {
/* 3125 */     if ((pieceNumber < 0) || (pieceNumber >= this.nbPieces))
/*      */     {
/* 3127 */       Debug.out("Invalid piece number: " + pieceNumber);
/*      */       
/* 3129 */       return false;
/*      */     }
/*      */     
/* 3132 */     CopyOnWriteSet<Integer> set = this.forced_pieces;
/*      */     
/* 3134 */     return (set != null) && (set.contains(Integer.valueOf(pieceNumber)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setGlobalRequestHint(int piece_number, int start_bytes, int byte_count)
/*      */   {
/* 3143 */     if (piece_number < 0)
/*      */     {
/* 3145 */       this.global_request_hint = null;
/*      */     }
/*      */     else
/*      */     {
/* 3149 */       this.global_request_hint = new int[] { piece_number, start_bytes, byte_count };
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getGlobalRequestHint()
/*      */   {
/* 3156 */     return this.global_request_hint;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setReverseBlockOrder(boolean is_reverse)
/*      */   {
/* 3163 */     this.reverse_block_order = is_reverse;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getReverseBlockOrder()
/*      */   {
/* 3169 */     return this.reverse_block_order;
/*      */   }
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
/*      */   public String getPieceString(int piece_number)
/*      */   {
/* 3184 */     long priority = this.startPriorities == null ? 0L : this.startPriorities[piece_number];
/*      */     String str;
/* 3186 */     if (priority == 9999999L)
/*      */     {
/* 3188 */       long[] rta = this.provider_piece_rtas;
/*      */       
/* 3190 */       str = "pri=rta:" + (rta == null ? "?" : new StringBuilder().append("").append(rta[piece_number] - SystemTime.getCurrentTime()).toString());
/*      */     }
/*      */     else {
/* 3193 */       PEPiece pe_piece = this.pePieces[piece_number];
/*      */       
/* 3195 */       if (pe_piece != null)
/*      */       {
/* 3197 */         priority = pe_piece.getResumePriority();
/*      */       }
/*      */       
/* 3200 */       str = "pri=" + priority;
/*      */     }
/*      */     
/* 3203 */     String str = str + ",avail=" + this.availability[piece_number];
/*      */     
/* 3205 */     long[] exts = this.provider_piece_priorities;
/*      */     
/* 3207 */     if (exts != null)
/*      */     {
/* 3209 */       str = str + ",ext=" + exts[piece_number];
/*      */     }
/*      */     
/* 3212 */     CopyOnWriteSet<Integer> forced = this.forced_pieces;
/*      */     
/* 3214 */     if ((forced != null) && (forced.contains(Integer.valueOf(piece_number))))
/*      */     {
/* 3216 */       str = str + ", forced";
/*      */     }
/*      */     
/* 3219 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 3226 */     writer.println("Piece Picker");
/*      */     try
/*      */     {
/* 3229 */       writer.indent();
/*      */       
/* 3231 */       writer.println("globalAvail: " + this.globalAvail);
/* 3232 */       writer.println("globalAvgAvail: " + this.globalAvgAvail);
/* 3233 */       writer.println("nbRarestActive: " + this.nbRarestActive);
/* 3234 */       writer.println("globalMin: " + this.globalMin);
/* 3235 */       writer.println("globalMinOthers: " + this.globalMinOthers);
/* 3236 */       writer.println("hasNeededUndonePiece: " + this.hasNeededUndonePiece);
/* 3237 */       writer.println("endGameMode: " + this.endGameMode);
/* 3238 */       writer.println("endGameModeAbandoned: " + this.endGameModeAbandoned);
/* 3239 */       writer.println("endGameModeChunks: " + this.endGameModeChunks);
/*      */     }
/*      */     finally
/*      */     {
/* 3243 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy() {}
/*      */   
/*      */   protected static class RealTimeData
/*      */   {
/*      */     private final List[] peer_requests;
/*      */     
/*      */     protected RealTimeData(PEPiece piece)
/*      */     {
/* 3256 */       int nb = piece.getNbBlocks();
/*      */       
/* 3258 */       this.peer_requests = new List[nb];
/*      */       
/* 3260 */       for (int i = 0; i < this.peer_requests.length; i++) {
/* 3261 */         this.peer_requests[i] = new ArrayList(1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public final List[] getRequests()
/*      */     {
/* 3268 */       return this.peer_requests;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class RealTimePeerRequest
/*      */   {
/*      */     private final PEPeerTransport peer;
/*      */     
/*      */     private final DiskManagerReadRequest request;
/*      */     
/*      */ 
/*      */     protected RealTimePeerRequest(PEPeerTransport _peer, DiskManagerReadRequest _request)
/*      */     {
/* 3283 */       this.peer = _peer;
/* 3284 */       this.request = _request;
/*      */     }
/*      */     
/*      */ 
/*      */     protected PEPeerTransport getPeer()
/*      */     {
/* 3290 */       return this.peer;
/*      */     }
/*      */     
/*      */ 
/*      */     protected DiskManagerReadRequest getRequest()
/*      */     {
/* 3296 */       return this.request;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/piecepicker/impl/PiecePickerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */