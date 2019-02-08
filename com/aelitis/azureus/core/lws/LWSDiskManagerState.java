/*     */ package com.aelitis.azureus.core.lws;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.LinkFileMap;
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStateListener;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
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
/*     */ public class LWSDiskManagerState
/*     */   implements DownloadManagerState
/*     */ {
/*  42 */   private long flags = 20L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/*  52 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getStateFile(String name)
/*     */   {
/*  59 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getStateFile()
/*     */   {
/*  65 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadManager getDownloadManager()
/*     */   {
/*  71 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearResumeData() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getResumeData()
/*     */   {
/*  82 */     return new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setResumeData(Map data) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isResumeDataComplete()
/*     */   {
/*  94 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearTrackerResponseCache() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public Map getTrackerResponseCache()
/*     */   {
/* 105 */     return new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTrackerResponseCache(Map value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFlag(long flag, boolean set)
/*     */   {
/* 119 */     this.flags |= flag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getFlag(long flag)
/*     */   {
/* 126 */     return (this.flags & flag) != 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFlags()
/*     */   {
/* 132 */     return this.flags;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOurContent()
/*     */   {
/* 138 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getIntParameter(String name)
/*     */   {
/* 145 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIntParameter(String name, int value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLongParameter(String name)
/*     */   {
/* 159 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setParameterDefault(String name) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLongParameter(String name, long value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getBooleanParameter(String name)
/*     */   {
/* 179 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBooleanParameter(String name, boolean value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAttribute(String name, String value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAttribute(String name)
/*     */   {
/* 200 */     return null;
/*     */   }
/*     */   
/*     */   public void setIntAttribute(String name, int value) {}
/*     */   
/* 205 */   public int getIntAttribute(String name) { return 0; }
/*     */   public void setLongAttribute(String name, long value) {}
/* 207 */   public long getLongAttribute(String name) { return 0L; }
/*     */   public void setBooleanAttribute(String name, boolean value) {}
/* 209 */   public boolean getBooleanAttribute(String name) { return false; }
/* 210 */   public boolean hasAttribute(String name) { return false; }
/*     */   
/*     */ 
/*     */   public String getTrackerClientExtensions()
/*     */   {
/* 215 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTrackerClientExtensions(String value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setListAttribute(String name, String[] values) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] getListAttribute(String name)
/*     */   {
/* 235 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getListAttribute(String name, int idx)
/*     */   {
/* 243 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMapAttribute(String name, Map value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map getMapAttribute(String name)
/*     */   {
/* 257 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Category getCategory()
/*     */   {
/* 263 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCategory(Category cat) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPrimaryFile(DiskManagerFileInfo dmfi) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerFileInfo getPrimaryFile()
/*     */   {
/* 281 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getNetworks()
/*     */   {
/* 287 */     return new String[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isNetworkEnabled(String network)
/*     */   {
/* 295 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNetworks(String[] networks) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNetworkEnabled(String network, boolean enabled) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] getPeerSources()
/*     */   {
/* 315 */     return new String[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPeerSourcePermitted(String peerSource)
/*     */   {
/* 322 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPeerSourcePermitted(String peerSource, boolean permitted) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isPeerSourceEnabled(String peerSource)
/*     */   {
/* 336 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPeerSources(String[] networks) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPeerSourceEnabled(String source, boolean enabled) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFileLink(int source_index, File link_source, File link_destination) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFileLinks(List<Integer> source_indexes, List<File> link_sources, List<File> link_destinations) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void discardFluff() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearFileLinks() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFileLink(int source_index, File link_source)
/*     */   {
/* 384 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public LinkFileMap getFileLinks()
/*     */   {
/* 390 */     return new LinkFileMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUserComment()
/*     */   {
/* 396 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUserComment(String name) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRelativeSavePath()
/*     */   {
/* 408 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRelativeSavePath(String path) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setActive(boolean a) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean exportState(File target_dir)
/*     */   {
/* 427 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void save() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void suppressStateSave(boolean suppress) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(DownloadManagerStateListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(DownloadManagerStateListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(DownloadManagerStateAttributeListener l, String attribute, int event_type) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(DownloadManagerStateAttributeListener l, String attribute, int event_type) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void generateEvidence(IndentWriter writer) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dump(IndentWriter writer) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDisplayName()
/*     */   {
/* 489 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDisplayName(String name) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean parameterExists(String name)
/*     */   {
/* 502 */     return false;
/*     */   }
/*     */   
/*     */   public void supressStateSave(boolean supress) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LWSDiskManagerState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */