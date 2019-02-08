/*     */ package org.gudy.azureus2.pluginsimpl.local.download;
/*     */ 
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubEx;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubFile;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class DownloadStubImpl
/*     */   implements DownloadStub.DownloadStubEx
/*     */ {
/*     */   private final DownloadManagerImpl manager;
/*     */   private final String name;
/*     */   private final byte[] hash;
/*     */   private final long size;
/*     */   private final long date_created;
/*     */   private final String save_path;
/*     */   private final DownloadStubFileImpl[] files;
/*     */   private final String[] manual_tags;
/*     */   private final int share_ratio;
/*     */   private final Map<String, Object> gm_map;
/*     */   private DownloadImpl temp_download;
/*     */   private Map<String, Object> attributes;
/*     */   
/*     */   protected DownloadStubImpl(DownloadManagerImpl _manager, DownloadImpl _download, String[] _manual_tags, Map<String, Object> _gm_map)
/*     */   {
/*  71 */     this.manager = _manager;
/*  72 */     this.temp_download = _download;
/*     */     
/*  74 */     this.date_created = SystemTime.getCurrentTime();
/*     */     
/*  76 */     this.name = this.temp_download.getName();
/*     */     
/*  78 */     Torrent torrent = this.temp_download.getTorrent();
/*     */     
/*  80 */     this.hash = torrent.getHash();
/*  81 */     this.size = torrent.getSize();
/*  82 */     this.save_path = this.temp_download.getSavePath();
/*     */     
/*  84 */     DownloadStub.DownloadStubFile[] _files = this.temp_download.getStubFiles();
/*     */     
/*  86 */     this.gm_map = _gm_map;
/*     */     
/*  88 */     this.files = new DownloadStubFileImpl[_files.length];
/*     */     
/*  90 */     for (int i = 0; i < this.files.length; i++)
/*     */     {
/*  92 */       this.files[i] = new DownloadStubFileImpl(this, _files[i]);
/*     */     }
/*     */     
/*  95 */     this.manual_tags = _manual_tags;
/*     */     
/*  97 */     DownloadStats stats = this.temp_download.getStats();
/*     */     
/*  99 */     this.share_ratio = stats.getShareRatio();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DownloadStubImpl(DownloadManagerImpl _manager, Map<String, Object> _map)
/*     */   {
/* 107 */     this.manager = _manager;
/*     */     
/* 109 */     this.date_created = MapUtils.getMapLong(_map, "dt", 0L);
/* 110 */     this.hash = ((byte[])_map.get("hash"));
/* 111 */     this.name = MapUtils.getMapString(_map, "name", null);
/* 112 */     this.size = MapUtils.getMapLong(_map, "s", 0L);
/* 113 */     this.save_path = MapUtils.getMapString(_map, "l", null);
/* 114 */     this.gm_map = ((Map)_map.get("gm"));
/*     */     
/* 116 */     List<Map<String, Object>> file_list = (List)_map.get("files");
/*     */     
/* 118 */     if (file_list == null)
/*     */     {
/* 120 */       this.files = new DownloadStubFileImpl[0];
/*     */     }
/*     */     else
/*     */     {
/* 124 */       this.files = new DownloadStubFileImpl[file_list.size()];
/*     */       
/* 126 */       for (int i = 0; i < this.files.length; i++)
/*     */       {
/* 128 */         this.files[i] = new DownloadStubFileImpl(this, (Map)file_list.get(i));
/*     */       }
/*     */     }
/*     */     
/* 132 */     List<Object> tag_list = (List)_map.get("t");
/*     */     
/* 134 */     if (tag_list != null)
/*     */     {
/* 136 */       this.manual_tags = new String[tag_list.size()];
/*     */       
/* 138 */       for (int i = 0; i < this.manual_tags.length; i++)
/*     */       {
/* 140 */         this.manual_tags[i] = MapUtils.getString(tag_list.get(i));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 145 */       this.manual_tags = null;
/*     */     }
/*     */     
/* 148 */     this.attributes = ((Map)_map.get("attr"));
/*     */     
/* 150 */     this.share_ratio = MapUtils.getMapInt(_map, "sr", -1);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> exportToMap()
/*     */   {
/* 156 */     Map<String, Object> map = new HashMap();
/*     */     
/* 158 */     map.put("dt", Long.valueOf(this.date_created));
/* 159 */     map.put("hash", this.hash);
/* 160 */     map.put("s", Long.valueOf(this.size));
/*     */     
/* 162 */     MapUtils.setMapString(map, "name", this.name);
/* 163 */     MapUtils.setMapString(map, "l", this.save_path);
/*     */     
/* 165 */     map.put("gm", this.gm_map);
/*     */     
/* 167 */     List<Map<String, Object>> file_list = new ArrayList();
/*     */     
/* 169 */     map.put("files", file_list);
/*     */     
/* 171 */     for (DownloadStubFileImpl file : this.files)
/*     */     {
/* 173 */       file_list.add(file.exportToMap());
/*     */     }
/*     */     
/* 176 */     if (this.manual_tags != null)
/*     */     {
/* 178 */       List<String> tag_list = new ArrayList(this.manual_tags.length);
/*     */       
/* 180 */       for (String s : this.manual_tags) {
/* 181 */         if (s != null) {
/* 182 */           tag_list.add(s);
/*     */         }
/*     */       }
/*     */       
/* 186 */       if (tag_list.size() > 0) {
/* 187 */         map.put("t", tag_list);
/*     */       }
/*     */     }
/*     */     
/* 191 */     if (this.attributes != null)
/*     */     {
/* 193 */       map.put("attr", this.attributes);
/*     */     }
/*     */     
/* 196 */     if (this.share_ratio >= 0)
/*     */     {
/* 198 */       map.put("sr", new Long(this.share_ratio));
/*     */     }
/*     */     
/* 201 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStub()
/*     */   {
/* 207 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setStubbified()
/*     */   {
/* 213 */     this.temp_download = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download destubbify()
/*     */     throws DownloadException
/*     */   {
/* 221 */     if (this.temp_download != null)
/*     */     {
/* 223 */       return this.temp_download;
/*     */     }
/*     */     
/* 226 */     return this.manager.destubbify(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public Torrent getTorrent()
/*     */   {
/* 232 */     if (this.temp_download != null)
/*     */     {
/* 234 */       return this.temp_download.getTorrent();
/*     */     }
/*     */     
/* 237 */     return PluginCoreUtils.wrap(this.manager.getTorrent(this));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 243 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getTorrentHash()
/*     */   {
/* 249 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTorrentSize()
/*     */   {
/* 255 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreationDate()
/*     */   {
/* 261 */     return this.date_created;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSavePath()
/*     */   {
/* 267 */     return this.save_path;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadStub.DownloadStubFile[] getStubFiles()
/*     */   {
/* 273 */     return this.files;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getManualTags()
/*     */   {
/* 279 */     return this.manual_tags;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getShareRatio()
/*     */   {
/* 285 */     return this.share_ratio;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getLongAttribute(TorrentAttribute attribute)
/*     */   {
/* 292 */     if (this.attributes == null)
/*     */     {
/* 294 */       return 0L;
/*     */     }
/*     */     
/* 297 */     Long l = (Long)this.attributes.get(attribute.getName());
/*     */     
/* 299 */     if (l == null)
/*     */     {
/* 301 */       return 0L;
/*     */     }
/*     */     
/* 304 */     return l.longValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLongAttribute(TorrentAttribute attribute, long value)
/*     */   {
/* 313 */     if (this.attributes == null)
/*     */     {
/* 315 */       this.attributes = new HashMap();
/*     */     }
/*     */     
/* 318 */     this.attributes.put(attribute.getName(), Long.valueOf(value));
/*     */     
/* 320 */     if (this.temp_download == null)
/*     */     {
/* 322 */       this.manager.updated(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getGMMap()
/*     */   {
/* 329 */     return this.gm_map;
/*     */   }
/*     */   
/*     */ 
/*     */   public void remove()
/*     */   {
/* 335 */     this.manager.remove(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void remove(boolean delete_torrent, boolean delete_data)
/*     */     throws DownloadException, DownloadRemovalVetoException
/*     */   {
/* 345 */     if (delete_data)
/*     */     {
/* 347 */       TOTorrent torrent = this.manager.getTorrent(this);
/*     */       
/* 349 */       if (torrent != null)
/*     */       {
/* 351 */         File save_location = new File(getSavePath());
/*     */         
/* 353 */         if (torrent.isSimpleTorrent())
/*     */         {
/* 355 */           if (save_location.isFile())
/*     */           {
/* 357 */             FileUtil.deleteWithRecycle(save_location, false);
/*     */ 
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */         }
/* 365 */         else if (save_location.isDirectory())
/*     */         {
/* 367 */           DownloadStub.DownloadStubFile[] files = getStubFiles();
/*     */           
/* 369 */           String save_path = save_location.getAbsolutePath();
/*     */           
/* 371 */           if (!save_path.endsWith(File.separator))
/*     */           {
/* 373 */             save_path = save_path + File.separator;
/*     */           }
/*     */           
/* 376 */           int found = 0;
/*     */           
/* 378 */           for (DownloadStub.DownloadStubFile file : files)
/*     */           {
/* 380 */             File f = file.getFile();
/*     */             
/* 382 */             String path = f.getAbsolutePath();
/*     */             
/* 384 */             if (path.startsWith(save_path))
/*     */             {
/* 386 */               if (f.exists())
/*     */               {
/* 388 */                 found++;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 393 */           int actual = countFiles(save_location);
/*     */           
/* 395 */           if (actual == found)
/*     */           {
/* 397 */             FileUtil.deleteWithRecycle(save_location, false);
/*     */           }
/*     */           else
/*     */           {
/* 401 */             for (DownloadStub.DownloadStubFile file : files)
/*     */             {
/* 403 */               File f = file.getFile();
/*     */               
/* 405 */               String path = f.getAbsolutePath();
/*     */               
/* 407 */               if (path.startsWith(save_path))
/*     */               {
/* 409 */                 FileUtil.deleteWithRecycle(f, false);
/*     */               }
/*     */             }
/*     */             
/* 413 */             TorrentUtils.recursiveEmptyDirDelete(save_location, false);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 420 */     if (delete_torrent)
/*     */     {
/* 422 */       byte[] bytes = (byte[])this.gm_map.get("torrent");
/*     */       
/* 424 */       if (bytes != null) {
/*     */         try
/*     */         {
/* 427 */           String torrent_file = new String(bytes, "UTF-8");
/*     */           
/* 429 */           File file = new File(torrent_file);
/*     */           
/* 431 */           TorrentUtils.delete(file, false);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 435 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 440 */     this.manager.remove(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int countFiles(File dir)
/*     */   {
/* 447 */     int result = 0;
/*     */     
/* 449 */     File[] files = dir.listFiles();
/*     */     
/* 451 */     if (files != null)
/*     */     {
/* 453 */       for (File f : files)
/*     */       {
/* 455 */         if (f.isFile())
/*     */         {
/* 457 */           result++;
/*     */         }
/*     */         else
/*     */         {
/* 461 */           result += countFiles(f);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 466 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class DownloadStubFileImpl
/*     */     implements DownloadStub.DownloadStubFile
/*     */   {
/*     */     private final DownloadStubImpl stub;
/*     */     
/*     */     private final Object file;
/*     */     
/*     */     private final long length;
/*     */     
/*     */ 
/*     */     protected DownloadStubFileImpl(DownloadStubImpl _stub, DownloadStub.DownloadStubFile stub_file)
/*     */     {
/* 482 */       this.stub = _stub;
/* 483 */       this.length = stub_file.getLength();
/*     */       
/* 485 */       File f = stub_file.getFile();
/*     */       
/* 487 */       String path = f.getAbsolutePath();
/*     */       
/* 489 */       String save_loc = this.stub.getSavePath();
/*     */       
/* 491 */       int save_loc_len = save_loc.length();
/*     */       
/* 493 */       if ((path.startsWith(save_loc)) && (path.length() > save_loc_len) && (path.charAt(save_loc_len) == File.separatorChar))
/*     */       {
/*     */ 
/*     */ 
/* 497 */         this.file = path.substring(save_loc_len + 1);
/*     */       }
/*     */       else
/*     */       {
/* 501 */         this.file = f;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected DownloadStubFileImpl(DownloadStubImpl _stub, Map map)
/*     */     {
/* 510 */       this.stub = _stub;
/*     */       
/* 512 */       String abs_file = MapUtils.getMapString(map, "file", null);
/*     */       
/* 514 */       if (abs_file != null)
/*     */       {
/* 516 */         this.file = new File(abs_file);
/*     */       }
/*     */       else
/*     */       {
/* 520 */         this.file = MapUtils.getMapString(map, "rel", null);
/*     */       }
/*     */       
/* 523 */       this.length = ((Long)map.get("len")).longValue();
/*     */     }
/*     */     
/*     */ 
/*     */     protected Map exportToMap()
/*     */     {
/* 529 */       Map map = new HashMap();
/*     */       
/* 531 */       if ((this.file instanceof File))
/*     */       {
/* 533 */         map.put("file", ((File)this.file).getAbsolutePath());
/*     */       }
/*     */       else
/*     */       {
/* 537 */         map.put("rel", (String)this.file);
/*     */       }
/*     */       
/* 540 */       map.put("len", Long.valueOf(this.length));
/*     */       
/* 542 */       return map;
/*     */     }
/*     */     
/*     */ 
/*     */     public File getFile()
/*     */     {
/* 548 */       if ((this.file instanceof File))
/*     */       {
/* 550 */         return (File)this.file;
/*     */       }
/*     */       
/*     */ 
/* 554 */       return new File(this.stub.getSavePath(), (String)this.file);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getLength()
/*     */     {
/* 561 */       return this.length;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadStubImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */