/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.plugins.I2PHelpers;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class TorrentOpenOptions
/*     */ {
/*     */   private static final String PARAM_DEFSAVEPATH = "Default save path";
/*     */   private static final String PARAM_MOVEWHENDONE = "Move Completed When Done";
/*     */   public static final int QUEUELOCATION_BOTTOM = 1;
/*     */   public static final int QUEUELOCATION_TOP = 0;
/*     */   public static final int STARTMODE_FORCESTARTED = 2;
/*     */   public static final int STARTMODE_QUEUED = 0;
/*     */   public static final int STARTMODE_SEEDING = 3;
/*     */   public static final int STARTMODE_STOPPED = 1;
/*     */   public String sOriginatingLocation;
/*     */   public String sFileName;
/*     */   private String sDestDir;
/*     */   private String manualRename;
/*     */   private String sDestSubDir;
/*     */   private boolean explicitDataDir;
/*     */   private TOTorrent torrent;
/*     */   private long totalSize;
/*     */   private int iStartID;
/*     */   public int iQueueLocation;
/*     */   public boolean isValid;
/*     */   public boolean bDeleteFileOnCancel;
/* 101 */   private TorrentOpenFileOptions[] files = null;
/*     */   
/*     */ 
/* 104 */   public boolean disableIPFilter = false;
/*     */   
/* 106 */   private Map<Integer, File> initial_linkage_map = null;
/*     */   
/* 108 */   private final CopyOnWriteList<FileListener> fileListeners = new CopyOnWriteList(1);
/*     */   
/* 110 */   public Map<String, Boolean> peerSource = new HashMap();
/*     */   
/* 112 */   private Map<String, Boolean> enabledNetworks = new HashMap();
/*     */   
/* 114 */   private List<Tag> initialTags = new ArrayList();
/*     */   
/*     */   private List<List<String>> updatedTrackers;
/*     */   
/*     */   private int max_up;
/*     */   
/*     */   private int max_down;
/*     */   
/*     */   private boolean hide_errors;
/*     */   
/*     */   public static final int CA_NONE = 0;
/*     */   
/*     */   public static final int CA_ACCEPT = 1;
/*     */   
/*     */   public static final int CA_REJECT = 2;
/* 129 */   private int complete_action = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean dirty;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentOpenOptions(String sFileName, TOTorrent torrent, boolean bDeleteFileOnCancel)
/*     */   {
/* 141 */     this();
/* 142 */     this.bDeleteFileOnCancel = bDeleteFileOnCancel;
/* 143 */     this.sFileName = sFileName;
/* 144 */     this.sOriginatingLocation = sFileName;
/* 145 */     setTorrent(torrent);
/*     */   }
/*     */   
/*     */   public TorrentOpenOptions() {
/* 149 */     this.iStartID = getDefaultStartMode();
/* 150 */     this.iQueueLocation = 1;
/* 151 */     this.isValid = true;
/* 152 */     this.sDestDir = COConfigurationManager.getStringParameter("Default save path");
/*     */     
/* 154 */     for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++)
/*     */     {
/* 156 */       String nn = AENetworkClassifier.AT_NETWORKS[i];
/*     */       
/* 158 */       String config_name = "Network Selection Default." + nn;
/*     */       
/* 160 */       this.enabledNetworks.put(nn, Boolean.valueOf(COConfigurationManager.getBooleanParameter(config_name)));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TorrentOpenOptions(TorrentOpenOptions toBeCloned)
/*     */   {
/* 169 */     this.sOriginatingLocation = toBeCloned.sOriginatingLocation;
/* 170 */     this.sFileName = toBeCloned.sFileName;
/* 171 */     this.sDestDir = toBeCloned.sDestDir;
/* 172 */     this.sDestSubDir = toBeCloned.sDestSubDir;
/* 173 */     this.iStartID = toBeCloned.iStartID;
/* 174 */     this.iQueueLocation = toBeCloned.iQueueLocation;
/* 175 */     this.isValid = toBeCloned.isValid;
/* 176 */     this.bDeleteFileOnCancel = toBeCloned.bDeleteFileOnCancel;
/* 177 */     this.disableIPFilter = toBeCloned.disableIPFilter;
/*     */     
/*     */ 
/*     */ 
/* 181 */     this.peerSource = (toBeCloned.peerSource == null ? null : new HashMap(toBeCloned.peerSource));
/* 182 */     this.enabledNetworks = (toBeCloned.enabledNetworks == null ? null : new HashMap(toBeCloned.enabledNetworks));
/* 183 */     this.initialTags = (toBeCloned.initialTags == null ? null : new ArrayList(toBeCloned.initialTags));
/*     */     
/* 185 */     if (toBeCloned.updatedTrackers != null) {
/* 186 */       this.updatedTrackers = new ArrayList();
/* 187 */       for (List<String> l : toBeCloned.updatedTrackers) {
/* 188 */         this.updatedTrackers.add(new ArrayList(l));
/*     */       }
/*     */     }
/* 191 */     this.max_up = toBeCloned.max_up;
/* 192 */     this.max_down = toBeCloned.max_down;
/* 193 */     this.hide_errors = toBeCloned.hide_errors;
/*     */   }
/*     */   
/*     */   public static int getDefaultStartMode() {
/* 197 */     return COConfigurationManager.getBooleanParameter("Default Start Torrents Stopped") ? 1 : 0;
/*     */   }
/*     */   
/*     */   public File getInitialLinkage(int index)
/*     */   {
/* 202 */     return this.initial_linkage_map == null ? null : (File)this.initial_linkage_map.get(Integer.valueOf(index));
/*     */   }
/*     */   
/*     */   public String getParentDir() {
/* 206 */     return this.sDestDir;
/*     */   }
/*     */   
/*     */   public void setParentDir(String parentDir) {
/* 210 */     this.sDestDir = parentDir;
/* 211 */     parentDirChanged();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setManualRename(String manualRename)
/*     */   {
/* 218 */     this.manualRename = manualRename;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getManualRename()
/*     */   {
/* 224 */     return this.manualRename;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSubDir()
/*     */   {
/* 230 */     return this.sDestSubDir;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setExplicitDataDir(String parent_dir, String sub_dir)
/*     */   {
/* 238 */     this.sDestDir = parent_dir;
/* 239 */     this.sDestSubDir = sub_dir;
/*     */     
/* 241 */     this.explicitDataDir = true;
/*     */     
/* 243 */     parentDirChanged();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isExplicitDataDir()
/*     */   {
/* 249 */     return this.explicitDataDir;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSimpleTorrent()
/*     */   {
/* 255 */     return this.torrent.isSimpleTorrent();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStartMode()
/*     */   {
/* 261 */     return this.iStartID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setStartMode(int m)
/*     */   {
/* 268 */     this.iStartID = m;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Boolean> getEnabledNetworks()
/*     */   {
/* 274 */     return new HashMap(this.enabledNetworks);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNetworkEnabled(String net, boolean enabled)
/*     */   {
/* 282 */     this.enabledNetworks.put(net, Boolean.valueOf(enabled));
/*     */   }
/*     */   
/*     */   public String getDataDir() {
/* 286 */     if (this.torrent.isSimpleTorrent())
/* 287 */       return this.sDestDir;
/* 288 */     return new File(this.sDestDir, this.sDestSubDir == null ? FileUtil.convertOSSpecificChars(getTorrentName(), true) : this.sDestSubDir).getPath();
/*     */   }
/*     */   
/*     */   private String getSmartDestDir()
/*     */   {
/* 293 */     String sSmartDir = this.sDestDir;
/*     */     try {
/* 295 */       String name = getTorrentName();
/* 296 */       String torrentFileName = this.sFileName == null ? "" : new File(this.sFileName).getName().replaceFirst("\\.torrent$", "");
/*     */       
/* 298 */       int totalSegmentsLengths = 0;
/*     */       
/* 300 */       String[][] segments = { name.split("[^a-zA-Z]+"), torrentFileName.split("[^a-zA-Z]+") };
/*     */       
/*     */ 
/*     */ 
/* 304 */       List downloadManagers = AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManagers();
/*     */       
/* 306 */       for (int x = 0; x < segments.length; x++) {
/* 307 */         String[] segmentArray = segments[x];
/* 308 */         for (int i = 0; i < segmentArray.length; i++) {
/* 309 */           int l = segmentArray[i].length();
/* 310 */           if (l > 1)
/*     */           {
/*     */ 
/* 313 */             segmentArray[i] = segmentArray[i].toLowerCase();
/* 314 */             totalSegmentsLengths += l;
/*     */           }
/*     */         }
/*     */       }
/* 318 */       String temp_dir = AETemporaryFileHandler.getTempDirectory().getAbsolutePath().toLowerCase(Locale.US);
/*     */       
/* 320 */       int maxMatches = 0;
/* 321 */       DownloadManager match = null;
/* 322 */       for (Iterator iter = downloadManagers.iterator(); iter.hasNext();) {
/* 323 */         DownloadManager dm = (DownloadManager)iter.next();
/*     */         
/* 325 */         if (dm.getState() != 100)
/*     */         {
/*     */ 
/*     */ 
/* 329 */           DownloadManagerState dms = dm.getDownloadState();
/*     */           
/* 331 */           if ((!dms.getFlag(16L)) && (!dms.getFlag(512L)) && 
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 341 */             (!dm.getSaveLocation().getAbsolutePath().toLowerCase(Locale.US).startsWith(temp_dir)))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 346 */             int numMatches = 0;
/*     */             
/* 348 */             String dmName = dm.getDisplayName().toLowerCase();
/*     */             
/* 350 */             for (int x = 0; x < segments.length; x++) {
/* 351 */               String[] segmentArray = segments[x];
/* 352 */               for (int i = 0; i < segmentArray.length; i++) {
/* 353 */                 int l = segmentArray[i].length();
/* 354 */                 if (l > 1)
/*     */                 {
/*     */ 
/*     */ 
/* 358 */                   String segment = segmentArray[i];
/*     */                   
/* 360 */                   if (dmName.contains(segment)) {
/* 361 */                     numMatches += l;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/* 366 */             if (numMatches > maxMatches) {
/* 367 */               maxMatches = numMatches;
/* 368 */               match = dm;
/*     */             }
/*     */           } } }
/* 371 */       if (match != null)
/*     */       {
/* 373 */         int iMatchLevel = maxMatches * 100 / totalSegmentsLengths;
/* 374 */         if (iMatchLevel >= 30) {
/* 375 */           File f = match.getSaveLocation();
/* 376 */           if ((!f.isDirectory()) || (match.getDiskManagerFileInfo().length > 1))
/*     */           {
/* 378 */             f = f.getParentFile();
/*     */           }
/*     */           
/* 381 */           if ((f != null) && (f.isDirectory())) {
/* 382 */             sSmartDir = f.getAbsolutePath();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 388 */       e.printStackTrace();
/*     */     }
/* 390 */     return sSmartDir;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<Tag> getInitialTags()
/*     */   {
/* 396 */     return new ArrayList(this.initialTags);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setInitialTags(List<Tag> tags)
/*     */   {
/* 403 */     this.initialTags = tags;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDirty()
/*     */   {
/* 409 */     this.dirty = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAndClearDirt()
/*     */   {
/* 415 */     boolean result = this.dirty;
/*     */     
/* 417 */     this.dirty = false;
/*     */     
/* 419 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<List<String>> getTrackers(boolean if_updated)
/*     */   {
/* 426 */     if (this.updatedTrackers != null)
/*     */     {
/* 428 */       return this.updatedTrackers;
/*     */     }
/*     */     
/* 431 */     if (if_updated)
/*     */     {
/* 433 */       return null;
/*     */     }
/*     */     
/* 436 */     if (this.torrent == null)
/*     */     {
/* 438 */       return new ArrayList(0);
/*     */     }
/*     */     
/*     */ 
/* 442 */     return TorrentUtils.announceGroupsToList(this.torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTrackers(List<List<String>> trackers)
/*     */   {
/* 450 */     this.updatedTrackers = trackers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaxUploadSpeed(int kbs)
/*     */   {
/* 457 */     this.max_up = kbs;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxUploadSpeed()
/*     */   {
/* 463 */     return this.max_up;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaxDownloadSpeed(int kbs)
/*     */   {
/* 470 */     this.max_down = kbs;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxDownloadSpeed()
/*     */   {
/* 476 */     return this.max_down;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setHideErrors(boolean h)
/*     */   {
/* 482 */     this.hide_errors = h;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getHideErrors()
/*     */   {
/* 488 */     return this.hide_errors;
/*     */   }
/*     */   
/*     */   public TorrentOpenFileOptions[] getFiles() {
/* 492 */     if ((this.files == null) && (this.torrent != null)) {
/* 493 */       TOTorrentFile[] tfiles = this.torrent.getFiles();
/* 494 */       this.files = new TorrentOpenFileOptions[tfiles.length];
/*     */       
/* 496 */       Set<String> skip_extensons = TorrentUtils.getSkipExtensionsSet();
/*     */       
/* 498 */       long skip_min_size = COConfigurationManager.getLongParameter("File.Torrent.AutoSkipMinSizeKB") * 1024L;
/*     */       
/* 500 */       for (int i = 0; i < this.files.length; i++) {
/* 501 */         TOTorrentFile torrentFile = tfiles[i];
/*     */         
/* 503 */         String orgFullName = torrentFile.getRelativePath();
/* 504 */         String orgFileName = new File(orgFullName).getName();
/*     */         
/* 506 */         boolean wanted = true;
/*     */         
/* 508 */         if ((skip_min_size > 0L) && (torrentFile.getLength() < skip_min_size))
/*     */         {
/* 510 */           wanted = false;
/*     */         }
/* 512 */         else if (skip_extensons.size() > 0)
/*     */         {
/* 514 */           int pos = orgFileName.lastIndexOf('.');
/*     */           
/* 516 */           if (pos != -1)
/*     */           {
/* 518 */             String ext = orgFileName.substring(pos + 1);
/*     */             
/* 520 */             wanted = !skip_extensons.contains(ext);
/*     */           }
/*     */         }
/*     */         
/* 524 */         this.files[i] = new TorrentOpenFileOptions(this, i, orgFullName, orgFileName, torrentFile.getLength(), wanted);
/*     */       }
/*     */     }
/*     */     
/* 528 */     return this.files;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalSize()
/*     */   {
/* 534 */     if (this.totalSize == 0L)
/*     */     {
/* 536 */       TorrentOpenFileOptions[] files = getFiles();
/*     */       
/* 538 */       if (files != null)
/*     */       {
/* 540 */         for (TorrentOpenFileOptions file : files)
/*     */         {
/* 542 */           this.totalSize += file.lSize;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 547 */     return this.totalSize;
/*     */   }
/*     */   
/* 550 */   public String getTorrentName() { return TorrentUtils.getLocalisedName(this.torrent); }
/*     */   
/*     */   public boolean allFilesMoving()
/*     */   {
/* 554 */     TorrentOpenFileOptions[] files = getFiles();
/* 555 */     for (int j = 0; j < files.length; j++) {
/* 556 */       if (files[j].isLinked()) {
/* 557 */         return false;
/*     */       }
/*     */     }
/* 560 */     return true;
/*     */   }
/*     */   
/*     */   public boolean allFilesExist()
/*     */   {
/* 565 */     TorrentOpenFileOptions[] files = getFiles();
/* 566 */     for (int i = 0; i < files.length; i++) {
/* 567 */       TorrentOpenFileOptions fileInfo = files[i];
/* 568 */       if (fileInfo.isToDownload())
/*     */       {
/*     */ 
/* 571 */         File file = fileInfo.getDestFileFullName();
/* 572 */         if ((!file.exists()) || (file.length() != fileInfo.lSize))
/* 573 */           return false;
/*     */       }
/*     */     }
/* 576 */     return true;
/*     */   }
/*     */   
/*     */   public void renameDuplicates() {
/* 580 */     if ((this.iStartID == 3) || (!COConfigurationManager.getBooleanParameter("DefaultDir.AutoSave.AutoRename")) || (allFilesExist()))
/*     */     {
/*     */ 
/* 583 */       return;
/*     */     }
/*     */     
/* 586 */     if (!this.torrent.isSimpleTorrent()) {
/* 587 */       if (new File(getDataDir()).isDirectory())
/*     */       {
/* 589 */         int idx = 0;
/*     */         File f;
/* 591 */         do { idx++;
/* 592 */           f = new File(getDataDir() + "-" + idx);
/* 593 */         } while (f.isDirectory());
/*     */         
/* 595 */         this.sDestSubDir = f.getName();
/*     */       }
/*     */     }
/*     */     else {
/* 599 */       TorrentOpenFileOptions[] fileInfos = getFiles();
/* 600 */       for (int i = 0; i < fileInfos.length; i++) {
/* 601 */         TorrentOpenFileOptions info = fileInfos[i];
/*     */         
/* 603 */         File file = info.getDestFileFullName();
/* 604 */         int idx = 0;
/* 605 */         while (file.exists()) {
/* 606 */           idx++;
/* 607 */           file = new File(info.getDestPathName(), idx + "-" + info.getDestFileName());
/*     */         }
/*     */         
/*     */ 
/* 611 */         info.setDestFileName(file.getName(), false);
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
/*     */   public boolean okToDisableAll()
/*     */   {
/* 640 */     return true;
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
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 658 */     return this.torrent;
/*     */   }
/*     */   
/*     */   public void setTorrent(TOTorrent torrent) {
/* 662 */     this.torrent = torrent;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 668 */     if ((COConfigurationManager.getBooleanParameter("DefaultDir.BestGuess")) && (!COConfigurationManager.getBooleanParameter("Move Completed When Done")))
/*     */     {
/*     */ 
/* 671 */       String def_save_path = COConfigurationManager.getStringParameter("Default save path");
/*     */       
/* 673 */       if ((def_save_path == null) || (def_save_path.trim().length() == 0))
/*     */       {
/* 675 */         this.sDestDir = getSmartDestDir();
/*     */       }
/*     */     }
/*     */     
/* 679 */     if (torrent == null) {
/* 680 */       this.initial_linkage_map = null;
/*     */     } else {
/* 682 */       this.initial_linkage_map = TorrentUtils.getInitialLinkage(torrent);
/*     */       
/*     */       try
/*     */       {
/* 686 */         LocaleTorrentUtil.getTorrentEncoding(torrent);
/*     */       } catch (Exception e) {
/* 688 */         e.printStackTrace();
/*     */       }
/*     */       
/* 691 */       Set<String> tracker_hosts = TorrentUtils.getUniqueTrackerHosts(torrent);
/*     */       
/* 693 */       Set<String> networks = new HashSet();
/*     */       
/* 695 */       boolean decentralised = false;
/*     */       
/* 697 */       for (String host : tracker_hosts)
/*     */       {
/* 699 */         if (TorrentUtils.isDecentralised(host))
/*     */         {
/* 701 */           decentralised = true;
/*     */         }
/*     */         else
/*     */         {
/* 705 */           String network = AENetworkClassifier.categoriseAddress(host);
/*     */           
/* 707 */           networks.add(network);
/*     */         }
/*     */       }
/*     */       
/* 711 */       List<String> network_cache = TorrentUtils.getNetworkCache(torrent);
/*     */       
/* 713 */       if (network_cache.size() > 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 720 */         for (String net : this.enabledNetworks.keySet())
/*     */         {
/* 722 */           boolean enabled = network_cache.contains(net);
/*     */           
/* 724 */           if (!enabled)
/*     */           {
/* 726 */             this.enabledNetworks.put(net, Boolean.valueOf(false));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 731 */       networks.addAll(network_cache);
/*     */       
/*     */ 
/*     */ 
/* 735 */       boolean enable_i2p = networks.contains("I2P");
/* 736 */       String enable_i2p_reason = null;
/*     */       
/* 738 */       if (enable_i2p)
/*     */       {
/* 740 */         enable_i2p_reason = MessageText.getString("azneti2phelper.install.reason.i2ptracker");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 746 */       else if ((tracker_hosts.size() != 1) || (!decentralised)) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 756 */       if (((Boolean)this.enabledNetworks.get("I2P")).booleanValue())
/*     */       {
/*     */ 
/*     */ 
/* 760 */         enable_i2p = true;
/*     */       }
/*     */       
/* 763 */       if (enable_i2p)
/*     */       {
/* 765 */         String[] providers = { "azneti2p", "azneti2phelper" };
/*     */         
/* 767 */         boolean found = false;
/*     */         
/* 769 */         for (String provider : providers)
/*     */         {
/* 771 */           if (AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID(provider) != null)
/*     */           {
/* 773 */             found = true;
/*     */             
/* 775 */             break;
/*     */           }
/*     */         }
/*     */         
/* 779 */         if (found)
/*     */         {
/* 781 */           this.enabledNetworks.put("I2P", Boolean.valueOf(true));
/*     */           
/*     */ 
/*     */ 
/* 785 */           if ((networks.contains("I2P")) && (networks.size() == 1))
/*     */           {
/* 787 */             this.enabledNetworks.put("Public", Boolean.valueOf(false));
/*     */           }
/*     */         }
/*     */         else {
/* 791 */           final boolean[] install_outcome = { false };
/*     */           
/* 793 */           if (I2PHelpers.installI2PHelper(enable_i2p_reason, "azneti2phelper.install.open.torrent", install_outcome, new Runnable()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/*     */ 
/* 802 */               if (install_outcome[0] == 0) {}
/*     */             }
/* 793 */           }))
/*     */           {
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
/* 814 */             this.enabledNetworks.put("I2P", Boolean.valueOf(true));
/*     */             
/*     */ 
/*     */ 
/* 818 */             if ((networks.contains("I2P")) && (networks.size() == 1))
/*     */             {
/* 820 */               this.enabledNetworks.put("Public", Boolean.valueOf(false));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 826 */       boolean enable_tor = networks.contains("Tor");
/*     */       
/* 828 */       if (enable_tor)
/*     */       {
/* 830 */         String[] providers = { "aznettor" };
/*     */         
/* 832 */         boolean found = false;
/*     */         
/* 834 */         for (String provider : providers)
/*     */         {
/* 836 */           if (AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID(provider) != null)
/*     */           {
/* 838 */             found = true;
/*     */             
/* 840 */             break;
/*     */           }
/*     */         }
/*     */         
/* 844 */         if (found)
/*     */         {
/* 846 */           this.enabledNetworks.put("Tor", Boolean.valueOf(true));
/*     */           
/*     */ 
/*     */ 
/* 850 */           if (!networks.contains("Public"))
/*     */           {
/* 852 */             this.enabledNetworks.put("Public", Boolean.valueOf(false));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 857 */       renameDuplicates();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(FileListener l)
/*     */   {
/* 863 */     this.fileListeners.add(l);
/*     */   }
/*     */   
/*     */   public void removeListener(FileListener l) {
/* 867 */     this.fileListeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fileDownloadStateChanged(TorrentOpenFileOptions torrentOpenFileOptions, boolean toDownload)
/*     */   {
/* 879 */     for (FileListener l : this.fileListeners) {
/*     */       try {
/* 881 */         l.toDownloadChanged(torrentOpenFileOptions, toDownload);
/*     */       } catch (Throwable e) {
/* 883 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void filePriorityStateChanged(TorrentOpenFileOptions torrentOpenFileOptions, int priority)
/*     */   {
/* 891 */     for (FileListener l : this.fileListeners) {
/*     */       try {
/* 893 */         l.priorityChanged(torrentOpenFileOptions, priority);
/*     */       } catch (Throwable e) {
/* 895 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void parentDirChanged()
/*     */   {
/* 902 */     for (FileListener l : this.fileListeners) {
/*     */       try {
/* 904 */         l.parentDirChanged();
/*     */       } catch (Throwable e) {
/* 906 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCompleteAction(int ca)
/*     */   {
/* 917 */     this.complete_action = ca;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCompleteAction()
/*     */   {
/* 923 */     return this.complete_action;
/*     */   }
/*     */   
/*     */   public static abstract interface FileListener
/*     */   {
/*     */     public abstract void toDownloadChanged(TorrentOpenFileOptions paramTorrentOpenFileOptions, boolean paramBoolean);
/*     */     
/*     */     public abstract void priorityChanged(TorrentOpenFileOptions paramTorrentOpenFileOptions, int paramInt);
/*     */     
/*     */     public abstract void parentDirChanged();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TorrentOpenOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */