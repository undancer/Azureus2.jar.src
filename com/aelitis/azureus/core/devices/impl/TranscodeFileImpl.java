/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProviderAnalysis;
/*     */ import com.aelitis.azureus.core.download.DiskManagerFileInfoDelegate;
/*     */ import com.aelitis.azureus.core.download.DiskManagerFileInfoFile;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.util.ImportExportUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
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
/*     */ class TranscodeFileImpl
/*     */   implements TranscodeFile
/*     */ {
/*     */   protected static final String KEY_FILE = "file";
/*  56 */   private static final TagManager tag_manager = ;
/*     */   
/*     */   private static final String KEY_PROFILE_NAME = "pn";
/*     */   
/*     */   private static final String KEY_SOURCE_FILE_HASH = "sf_hash";
/*     */   
/*     */   private static final String KEY_SOURCE_FILE_INDEX = "sf_index";
/*     */   
/*     */   private static final String KEY_SOURCE_FILE_LINK = "sf_link";
/*     */   
/*     */   private static final String KEY_NO_XCODE = "no_xcode";
/*     */   
/*     */   private static final String KEY_FOR_JOB = "fj";
/*     */   
/*     */   private static final String KEY_DURATION = "at_dur";
/*     */   
/*     */   private static final String KEY_VIDEO_WIDTH = "at_vw";
/*     */   
/*     */   private static final String KEY_VIDEO_HEIGHT = "at_vh";
/*     */   
/*     */   private static final String KEY_XCODE_SIZE = "at_xs";
/*     */   
/*     */   private static final String KEY_DATE = "at_dt";
/*     */   
/*     */   private static final String KEY_CATEGORIES = "cat";
/*     */   
/*     */   private static final String KEY_TAGS = "tags";
/*     */   private static final String KEY_COPY_TO_OVERRIDE = "ct_over";
/*     */   private static final String KEY_COPYING = "copying";
/*     */   private DeviceImpl device;
/*     */   private String key;
/*     */   private Map<String, Map<String, ?>> files_map;
/*     */   
/*     */   protected TranscodeFileImpl(DeviceImpl _device, String _key, String _profile_name, Map<String, Map<String, ?>> _files_map, File _file, boolean _for_job)
/*     */   {
/*  91 */     this.device = _device;
/*  92 */     this.key = _key;
/*  93 */     this.files_map = _files_map;
/*     */     
/*  95 */     getMap(true);
/*     */     
/*  97 */     setString("file", _file.getAbsolutePath());
/*     */     
/*  99 */     setString("pn", _profile_name);
/*     */     
/* 101 */     setLong("at_dt", SystemTime.getCurrentTime());
/*     */     
/* 103 */     setBoolean("fj", _for_job);
/*     */     
/* 105 */     setBoolean("copying", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TranscodeFileImpl(DeviceImpl _device, String _key, Map<String, Map<String, ?>> _map)
/*     */     throws IOException
/*     */   {
/* 116 */     this.device = _device;
/* 117 */     this.key = _key;
/* 118 */     this.files_map = _map;
/*     */     
/* 120 */     Map<String, ?> map = getMap();
/*     */     
/* 122 */     if ((map == null) || (!map.containsKey("file")))
/*     */     {
/* 124 */       throw new IOException("File has been deleted");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getKey()
/*     */   {
/* 132 */     return this.key;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 138 */     TranscodeJob job = getJob();
/*     */     
/*     */     String text;
/*     */     
/* 142 */     if (job == null) {
/*     */       try
/*     */       {
/* 145 */         DiskManagerFileInfo sourceFile = getSourceFile();
/*     */         try
/*     */         {
/* 148 */           Download download = sourceFile.getDownload();
/*     */           String text;
/* 150 */           if (download == null)
/*     */           {
/* 152 */             text = sourceFile.getFile().getName();
/*     */           }
/*     */           else
/*     */           {
/* 156 */             text = download.getName();
/*     */             
/* 158 */             DiskManagerFileInfo[] fileInfo = download.getDiskManagerFileInfo();
/*     */             
/* 160 */             if (fileInfo.length > 1)
/*     */             {
/* 162 */               text = text + ": " + sourceFile.getFile(true).getName();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (DownloadException e) {
/* 167 */           text = sourceFile.getFile().getName();
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 172 */         String text = "";
/*     */       }
/*     */       
/*     */     } else {
/* 176 */       text = job.getName();
/*     */     }
/*     */     
/* 179 */     return text;
/*     */   }
/*     */   
/*     */ 
/*     */   public Device getDevice()
/*     */   {
/* 185 */     return this.device;
/*     */   }
/*     */   
/*     */ 
/*     */   public TranscodeJobImpl getJob()
/*     */   {
/* 191 */     if (isComplete())
/*     */     {
/* 193 */       return null;
/*     */     }
/*     */     
/* 196 */     return this.device.getManager().getTranscodeManager().getQueue().getJob(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getCacheFile()
/*     */     throws TranscodeException
/*     */   {
/* 204 */     String file_str = getString("file");
/*     */     
/* 206 */     if (file_str == null)
/*     */     {
/* 208 */       throw new TranscodeException("File has been deleted");
/*     */     }
/*     */     
/* 211 */     return new File(file_str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setCacheFile(File file)
/*     */   {
/* 218 */     setString("file", file.getAbsolutePath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkDeleted()
/*     */     throws TranscodeException
/*     */   {
/* 226 */     if (isDeleted())
/*     */     {
/* 228 */       throw new TranscodeException("File has been deleted");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DiskManagerFileInfo getSourceFile()
/*     */     throws TranscodeException
/*     */   {
/* 237 */     checkDeleted();
/*     */     
/*     */ 
/*     */ 
/* 241 */     String hash = getString("sf_hash");
/*     */     
/* 243 */     if (hash != null) {
/*     */       try
/*     */       {
/* 246 */         Download download = PluginInitializer.getDefaultInterface().getDownloadManager().getDownload(Base32.decode(hash));
/*     */         
/* 248 */         if (download != null)
/*     */         {
/* 250 */           int index = (int)getLong("sf_index");
/*     */           
/* 252 */           return download.getDiskManagerFileInfo(index);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 260 */     String link = getString("sf_link");
/*     */     
/* 262 */     if (link != null)
/*     */     {
/* 264 */       File link_file = new File(link);
/*     */       
/*     */ 
/*     */ 
/* 268 */       if ((link_file.exists()) || (getBoolean("no_xcode")))
/*     */       {
/* 270 */         return new DiskManagerFileInfoFile(link_file);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 276 */     return new DiskManagerFileInfoFile(getCacheFile());
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setSourceFile(DiskManagerFileInfo file)
/*     */   {
/*     */     try
/*     */     {
/* 284 */       Download download = file.getDownload();
/*     */       
/* 286 */       if ((download != null) && (download.getTorrent() != null))
/*     */       {
/* 288 */         setString("sf_hash", Base32.encode(download.getTorrent().getHash()));
/*     */         
/* 290 */         setLong("sf_index", file.getIndex());
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 295 */     setString("sf_link", file.getFile().getAbsolutePath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerFileInfo getTargetFile()
/*     */     throws TranscodeException
/*     */   {
/* 306 */     File cache_file = getCacheFile();
/*     */     
/* 308 */     if ((cache_file.exists()) && (cache_file.length() > 0L))
/*     */     {
/* 310 */       return new DiskManagerFileInfoFile(cache_file);
/*     */     }
/*     */     
/* 313 */     if (getBoolean("no_xcode"))
/*     */     {
/* 315 */       DiskManagerFileInfo res = getSourceFile();
/*     */       
/* 317 */       if ((res instanceof DiskManagerFileInfoFile))
/*     */       {
/* 319 */         return res;
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 324 */         return new DiskManagerFileInfoDelegate(res);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 328 */         Debug.out(e);
/*     */         
/* 330 */         return res;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 337 */     return new DiskManagerFileInfoFile(cache_file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setTranscodeRequired(boolean required)
/*     */     throws TranscodeException
/*     */   {
/* 346 */     setBoolean("no_xcode", !required);
/*     */     
/* 348 */     if (!required)
/*     */     {
/*     */ 
/*     */ 
/* 352 */       this.device.revertFileName(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getTranscodeRequired()
/*     */   {
/* 359 */     return !getBoolean("no_xcode");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setComplete(boolean b)
/*     */   {
/* 366 */     setBoolean("comp", b);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComplete()
/*     */   {
/* 372 */     return getBoolean("comp");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTemplate()
/*     */   {
/* 378 */     return !getBoolean("fj");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setCopiedToDevice(boolean b)
/*     */   {
/* 386 */     setBoolean("copied", b);
/*     */     
/* 388 */     setLong("copy_fail", 0L);
/*     */     
/* 390 */     setCopyingToDevice(false);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setCopyToDeviceFailed()
/*     */   {
/* 396 */     setLong("copy_fail", getLong("copy_fail") + 1L);
/*     */     
/* 398 */     setCopyingToDevice(false);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCopyToDeviceFails()
/*     */   {
/* 404 */     return getLong("copy_fail");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCopiedToDevice()
/*     */   {
/* 410 */     return getBoolean("copied");
/*     */   }
/*     */   
/*     */ 
/*     */   public void retryCopyToDevice()
/*     */   {
/* 416 */     if (isCopiedToDevice())
/*     */     {
/* 418 */       setCopiedToDevice(false);
/*     */     }
/*     */     else
/*     */     {
/* 422 */       setLong("copy_fail", 0L);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setProfileName(String s)
/*     */   {
/* 430 */     setString("pn", s);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getProfileName()
/*     */   {
/* 436 */     String s = getString("pn");
/*     */     
/* 438 */     if (s == null)
/*     */     {
/* 440 */       s = "Unknown";
/*     */     }
/*     */     
/* 443 */     return s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setCopyToFolderOverride(String s)
/*     */   {
/* 450 */     setString("ct_over", s);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCopyToFolderOverride()
/*     */   {
/* 456 */     return getString("ct_over");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void update(TranscodeProviderAnalysis analysis)
/*     */     throws TranscodeException
/*     */   {
/* 465 */     checkDeleted();
/*     */     
/* 467 */     long duration = analysis.getLongProperty(2);
/* 468 */     long video_width = analysis.getLongProperty(3);
/* 469 */     long video_height = analysis.getLongProperty(4);
/* 470 */     long xcode_size = analysis.getLongProperty(7);
/*     */     
/* 472 */     if (duration > 0L)
/*     */     {
/* 474 */       setLong("at_dur", duration);
/*     */     }
/*     */     
/* 477 */     if ((video_width > 0L) && (video_height > 0L))
/*     */     {
/* 479 */       setLong("at_vw", video_width);
/*     */       
/* 481 */       setLong("at_vh", video_height);
/*     */     }
/*     */     
/* 484 */     if (xcode_size > 0L)
/*     */     {
/* 486 */       setLong("at_xs", xcode_size);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setResolution(int video_width, int video_height)
/*     */   {
/* 495 */     if ((video_width > 0) && (video_height > 0))
/*     */     {
/* 497 */       setLong("at_vw", video_width);
/*     */       
/* 499 */       setLong("at_vh", video_height);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDurationMillis()
/*     */   {
/* 506 */     return getLong("at_dur");
/*     */   }
/*     */   
/*     */ 
/*     */   public long getVideoWidth()
/*     */   {
/* 512 */     return getLong("at_vw");
/*     */   }
/*     */   
/*     */ 
/*     */   public long getVideoHeight()
/*     */   {
/* 518 */     return getLong("at_vh");
/*     */   }
/*     */   
/*     */ 
/*     */   public long getEstimatedTranscodeSize()
/*     */   {
/* 524 */     return getLong("at_xs");
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getCategories()
/*     */   {
/* 530 */     String cats = getString("cat");
/*     */     
/* 532 */     if ((cats == null) || (cats.length() == 0))
/*     */     {
/* 534 */       return new String[0];
/*     */     }
/*     */     
/* 537 */     return Constants.PAT_SPLIT_COMMA.split(cats);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCategories(String[] cats)
/*     */   {
/* 544 */     String[] existing = getCategories();
/*     */     
/* 546 */     if ((existing.length == 0) && (existing.length == cats.length))
/*     */     {
/* 548 */       return;
/*     */     }
/*     */     
/* 551 */     String str = "";
/*     */     
/* 553 */     for (String cat : cats)
/*     */     {
/* 555 */       cat = cat.replaceAll(",", "").trim();
/*     */       
/* 557 */       if (cat.length() > 0)
/*     */       {
/* 559 */         str = str + (str.length() == 0 ? "" : ",") + cat;
/*     */       }
/*     */     }
/*     */     
/* 563 */     setString("cat", str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getTags(boolean localize)
/*     */   {
/* 570 */     String tags_str = getString("tags");
/*     */     
/* 572 */     if ((tags_str == null) || (tags_str.length() == 0))
/*     */     {
/* 574 */       return new String[0];
/*     */     }
/*     */     
/* 577 */     String[] tags = Constants.PAT_SPLIT_COMMA.split(tags_str);
/*     */     
/* 579 */     if (localize)
/*     */     {
/* 581 */       List<String> derp = null;
/*     */       
/* 583 */       int pos = 0;
/*     */       
/* 585 */       for (String s : tags) {
/*     */         try
/*     */         {
/* 588 */           Tag tag = tag_manager.lookupTagByUID(Long.parseLong(s));
/*     */           
/* 590 */           if (tag == null)
/*     */           {
/* 592 */             throw new Exception();
/*     */           }
/*     */           
/* 595 */           String tag_name = tag.getTagName(true);
/*     */           
/* 597 */           if (derp == null)
/*     */           {
/* 599 */             tags[(pos++)] = tag_name;
/*     */           }
/*     */           else
/*     */           {
/* 603 */             derp.add(tag_name);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 607 */           if (derp == null)
/*     */           {
/* 609 */             derp = new ArrayList();
/*     */             
/* 611 */             derp.addAll(Arrays.asList(tags).subList(0, pos));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 616 */       if (derp == null)
/*     */       {
/* 618 */         return tags;
/*     */       }
/*     */       
/* 621 */       return (String[])derp.toArray(new String[derp.size()]);
/*     */     }
/*     */     
/*     */ 
/* 625 */     return tags;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTags(String[] tags)
/*     */   {
/* 633 */     String[] existing = getTags(false);
/*     */     
/* 635 */     if ((existing.length == 0) && (existing.length == tags.length))
/*     */     {
/* 637 */       return;
/*     */     }
/*     */     
/* 640 */     String str = "";
/*     */     
/* 642 */     for (String tag : tags)
/*     */     {
/* 644 */       tag = tag.replaceAll(",", "").trim();
/*     */       
/* 646 */       if (tag.length() > 0)
/*     */       {
/* 648 */         str = str + (str.length() == 0 ? "" : ",") + tag;
/*     */       }
/*     */     }
/*     */     
/* 652 */     setString("tags", str);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreationDateMillis()
/*     */   {
/* 658 */     return getLong("at_dt");
/*     */   }
/*     */   
/*     */   public File getCacheFileIfExists()
/*     */   {
/*     */     try
/*     */     {
/* 665 */       return getCacheFile();
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 669 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URL getStreamURL()
/*     */   {
/* 676 */     return this.device.getStreamURL(this, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URL getStreamURL(String host)
/*     */   {
/* 683 */     return this.device.getStreamURL(this, host);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getMimeType()
/*     */   {
/* 689 */     return this.device.getMimeType(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(boolean delete_contents)
/*     */     throws TranscodeException
/*     */   {
/* 698 */     this.device.deleteFile(this, delete_contents, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void deleteCacheFile()
/*     */     throws TranscodeException
/*     */   {
/* 706 */     this.device.deleteFile(this, true, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 712 */     return getMap() == null;
/*     */   }
/*     */   
/*     */ 
/*     */   private Map<String, ?> getMap()
/*     */   {
/* 718 */     return getMap(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Map<String, ?> getMap(boolean create)
/*     */   {
/* 725 */     synchronized (this.files_map)
/*     */     {
/* 727 */       Map<String, ?> map = (Map)this.files_map.get(this.key);
/*     */       
/* 729 */       if ((map == null) && (create))
/*     */       {
/* 731 */         map = new HashMap();
/*     */         
/* 733 */         this.files_map.put(this.key, map);
/*     */       }
/*     */       
/* 736 */       return map;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean getBoolean(String key)
/*     */   {
/* 744 */     return getLong(key) == 1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setBoolean(String key, boolean b)
/*     */   {
/* 752 */     setLong(key, b ? 1L : 0L);
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLong(String key)
/*     */   {
/*     */     try
/*     */     {
/* 760 */       Map<String, ?> map = getMap();
/*     */       
/* 762 */       return ImportExportUtils.importLong(map, key, 0L);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 766 */       Debug.out(e);
/*     */     }
/* 768 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setLong(String key, long value)
/*     */   {
/* 777 */     if (getLong(key) == value)
/*     */     {
/* 779 */       return;
/*     */     }
/*     */     
/* 782 */     synchronized (this.files_map)
/*     */     {
/*     */       try {
/* 785 */         Map<String, ?> map = getMap();
/*     */         
/* 787 */         ImportExportUtils.exportLong(map, key, value);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 791 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 795 */     this.device.fileDirty(this, 1, key);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString(String key)
/*     */   {
/*     */     try
/*     */     {
/* 803 */       Map<String, ?> map = getMap();
/*     */       
/* 805 */       return ImportExportUtils.importString(map, key);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 809 */       Debug.out(e);
/*     */     }
/* 811 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setString(String key, String value)
/*     */   {
/* 820 */     String existing = getString(key);
/*     */     
/* 822 */     if ((existing == null) && (value == null))
/*     */     {
/* 824 */       return;
/*     */     }
/* 826 */     if ((existing != null) && (value != null))
/*     */     {
/* 828 */       if (existing.equals(value))
/*     */       {
/* 830 */         return;
/*     */       }
/*     */     }
/* 833 */     synchronized (this.files_map)
/*     */     {
/* 835 */       Map<String, ?> map = getMap();
/*     */       try
/*     */       {
/* 838 */         ImportExportUtils.exportString(map, key, value);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 842 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 846 */     this.device.fileDirty(this, 1, key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTransientProperty(Object key2, Object value)
/*     */   {
/* 854 */     this.device.setTransientProperty(this.key, key2, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getTransientProperty(Object key2)
/*     */   {
/* 861 */     return this.device.getTransientProperty(this.key, key2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 868 */     if ((other instanceof TranscodeFileImpl))
/*     */     {
/* 870 */       return this.key.equals(((TranscodeFileImpl)other).key);
/*     */     }
/*     */     
/* 873 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 879 */     return this.key.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 885 */     Map<String, ?> map = getMap();
/*     */     
/* 887 */     if (map == null)
/*     */     {
/* 889 */       return this.key + ": deleted";
/*     */     }
/*     */     
/*     */ 
/* 893 */     return this.key + ": " + map;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCopyingToDevice(boolean b)
/*     */   {
/* 901 */     setBoolean("copying", b);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCopyingToDevice()
/*     */   {
/* 907 */     return getBoolean("copying");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodeFileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */