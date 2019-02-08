/*     */ package com.aelitis.azureus.core.content;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagListener;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
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
/*     */ public class AzureusPlatformContentDirectory
/*     */   implements AzureusContentDirectory
/*     */ {
/*  61 */   private static boolean registered = false;
/*     */   
/*     */   private static TorrentAttribute ta_category;
/*     */   
/*     */ 
/*     */   public static synchronized void register()
/*     */   {
/*  68 */     if (!registered)
/*     */     {
/*  70 */       registered = true;
/*     */       
/*  72 */       ta_category = PluginInitializer.getDefaultInterface().getTorrentManager().getAttribute("Category");
/*     */       
/*  74 */       AzureusContentDirectoryManager.registerDirectory(new AzureusPlatformContentDirectory());
/*     */     }
/*     */   }
/*     */   
/*  78 */   private static CopyOnWriteList<AzureusContentDirectoryListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */   public AzureusContent lookupContent(Map attributes)
/*     */   {
/*  84 */     byte[] hash = (byte[])attributes.get("btih");
/*     */     
/*  86 */     if (hash == null)
/*     */     {
/*  88 */       return null;
/*     */     }
/*     */     
/*  91 */     String url_str = ConstantsVuze.getDefaultContentNetwork().getTorrentDownloadService(Base32.encode(hash), null);
/*     */     
/*  93 */     ResourceDownloaderFactory rdf = ResourceDownloaderFactoryImpl.getSingleton();
/*     */     try
/*     */     {
/*  96 */       ResourceDownloader rd = rdf.create(new URL(url_str));
/*     */       
/*  98 */       InputStream is = rd.download();
/*     */       try
/*     */       {
/* 101 */         TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedInputStream(is);
/*     */         
/* 103 */         return new AzureusPlatformContent(new TorrentImpl(torrent));
/*     */       }
/*     */       finally
/*     */       {
/* 107 */         is.close();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */       return null;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 112 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AzureusContentDownload lookupContentDownload(Map attributes)
/*     */   {
/* 122 */     byte[] hash = (byte[])attributes.get("btih");
/*     */     try
/*     */     {
/* 125 */       final Download download = PluginInitializer.getDefaultInterface().getDownloadManager().getDownload(hash);
/*     */       
/* 127 */       if (download == null)
/*     */       {
/* 129 */         return null;
/*     */       }
/*     */       
/* 132 */       new AzureusContentDownload()
/*     */       {
/*     */ 
/*     */         public Download getDownload()
/*     */         {
/*     */ 
/* 138 */           return download;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public Object getProperty(String name)
/*     */         {
/* 145 */           return null;
/*     */         }
/*     */       };
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 151 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AzureusContentFile lookupContentFile(Map attributes)
/*     */   {
/* 159 */     byte[] hash = (byte[])attributes.get("btih");
/* 160 */     int index = ((Integer)attributes.get("file_index")).intValue();
/*     */     
/*     */     try
/*     */     {
/* 164 */       Download download = PluginInitializer.getDefaultInterface().getDownloadManager().getDownload(hash);
/*     */       
/* 166 */       if (download == null)
/*     */       {
/* 168 */         return null;
/*     */       }
/*     */       
/* 171 */       Torrent t_torrent = download.getTorrent();
/*     */       
/* 173 */       if (t_torrent == null)
/*     */       {
/* 175 */         return null;
/*     */       }
/*     */       
/* 178 */       String ud_key = "AzureusPlatformContentDirectory:" + index;
/*     */       
/* 180 */       AzureusContentFile acf = (AzureusContentFile)download.getUserData(ud_key);
/*     */       
/* 182 */       if (acf != null)
/*     */       {
/* 184 */         return acf;
/*     */       }
/*     */       
/* 187 */       final TOTorrent torrent = ((TorrentImpl)t_torrent).getTorrent();
/*     */       
/* 189 */       final DiskManagerFileInfo file = download.getDiskManagerFileInfo(index);
/*     */       
/* 191 */       if (PlatformTorrentUtils.isContent(torrent, false))
/*     */       {
/* 193 */         acf = new AzureusContentFile()
/*     */         {
/*     */ 
/*     */           public DiskManagerFileInfo getFile()
/*     */           {
/*     */ 
/* 199 */             return file;
/*     */           }
/*     */           
/*     */ 
/*     */           public Object getProperty(String name)
/*     */           {
/*     */             try
/*     */             {
/* 207 */               if (name.equals("duration"))
/*     */               {
/* 209 */                 long duration = PlatformTorrentUtils.getContentVideoRunningTime(torrent);
/*     */                 
/* 211 */                 if (duration > 0L)
/*     */                 {
/*     */ 
/*     */ 
/* 215 */                   return new Long(duration * 1000L);
/*     */                 }
/* 217 */               } else if (name.equals("video_width"))
/*     */               {
/* 219 */                 int[] res = PlatformTorrentUtils.getContentVideoResolution(torrent);
/*     */                 
/* 221 */                 if (res != null)
/*     */                 {
/* 223 */                   return new Long(res[0]);
/*     */                 }
/* 225 */               } else if (name.equals("video_height"))
/*     */               {
/* 227 */                 int[] res = PlatformTorrentUtils.getContentVideoResolution(torrent);
/*     */                 
/* 229 */                 if (res != null)
/*     */                 {
/* 231 */                   return new Long(res[1]); }
/*     */               } else {
/* 233 */                 if (name.equals("date"))
/*     */                 {
/* 235 */                   return new Long(file.getDownload().getCreationTime());
/*     */                 }
/* 237 */                 if (name.equals("cats"))
/*     */                 {
/*     */                   try {
/* 240 */                     String cat = file.getDownload().getCategoryName();
/*     */                     
/* 242 */                     if ((cat != null) && (cat.length() > 0))
/*     */                     {
/* 244 */                       if (!cat.equalsIgnoreCase("Categories.uncategorized"))
/*     */                       {
/* 246 */                         return new String[] { cat };
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                   
/*     */ 
/* 253 */                   return new String[0];
/*     */                 }
/* 255 */                 if (name.equals("tags"))
/*     */                 {
/* 257 */                   List<Tag> tags = TagManagerFactory.getTagManager().getTagsForTaggable(PluginCoreUtils.unwrap(file.getDownload()));
/*     */                   
/* 259 */                   List<String> tag_names = new ArrayList();
/*     */                   
/* 261 */                   for (Tag tag : tags)
/*     */                   {
/* 263 */                     if (tag.getTagType().getTagType() == 3)
/*     */                     {
/* 265 */                       tag_names.add(tag.getTagName(true));
/*     */                     }
/*     */                   }
/*     */                   
/* 269 */                   return tag_names.toArray(new String[tag_names.size()]);
/*     */                 }
/* 271 */                 if (name.equals("percent"))
/*     */                 {
/* 273 */                   long size = file.getLength();
/*     */                   
/* 275 */                   return new Long(size == 0L ? 100L : 1000L * file.getDownloaded() / size);
/*     */                 }
/* 277 */                 if (name.equals("eta"))
/*     */                 {
/* 279 */                   return Long.valueOf(AzureusPlatformContentDirectory.this.getETA(file));
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {}
/* 284 */             return null;
/*     */           }
/*     */         };
/*     */       } else {
/* 288 */         acf = new AzureusContentFile()
/*     */         {
/*     */ 
/*     */           public DiskManagerFileInfo getFile()
/*     */           {
/*     */ 
/* 294 */             return file;
/*     */           }
/*     */           
/*     */ 
/*     */           public Object getProperty(String name)
/*     */           {
/*     */             try
/*     */             {
/* 302 */               if (name.equals("date"))
/*     */               {
/* 304 */                 return new Long(file.getDownload().getCreationTime());
/*     */               }
/* 306 */               if (name.equals("cats"))
/*     */               {
/*     */                 try {
/* 309 */                   String cat = file.getDownload().getCategoryName();
/*     */                   
/* 311 */                   if ((cat != null) && (cat.length() > 0))
/*     */                   {
/* 313 */                     if (!cat.equalsIgnoreCase("Categories.uncategorized"))
/*     */                     {
/* 315 */                       return new String[] { cat };
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */                 
/*     */ 
/* 322 */                 return new String[0];
/*     */               }
/* 324 */               if (name.equals("tags"))
/*     */               {
/*     */ 
/* 327 */                 List<Tag> tags = TagManagerFactory.getTagManager().getTagsForTaggable(PluginCoreUtils.unwrap(file.getDownload()));
/*     */                 
/* 329 */                 List<String> tag_names = new ArrayList();
/*     */                 
/* 331 */                 for (Tag tag : tags)
/*     */                 {
/* 333 */                   if (tag.getTagType().getTagType() == 3)
/*     */                   {
/* 335 */                     tag_names.add(tag.getTagName(true));
/*     */                   }
/*     */                 }
/*     */                 
/* 339 */                 return tag_names.toArray(new String[tag_names.size()]);
/*     */               }
/* 341 */               if (name.equals("percent"))
/*     */               {
/* 343 */                 long size = file.getLength();
/*     */                 
/* 345 */                 return new Long(size == 0L ? 100L : 1000L * file.getDownloaded() / size);
/*     */               }
/* 347 */               if (name.equals("eta"))
/*     */               {
/* 349 */                 return Long.valueOf(AzureusPlatformContentDirectory.this.getETA(file));
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {}
/*     */             
/* 354 */             return null;
/*     */           }
/*     */         };
/*     */       }
/*     */       
/* 359 */       download.setUserData(ud_key, acf);
/*     */       
/* 361 */       final AzureusContentFile f_acf = acf;
/*     */       
/* 363 */       download.addAttributeListener(new DownloadAttributeListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void attributeEventOccurred(Download download, TorrentAttribute attribute, int eventType) {
/* 372 */           AzureusPlatformContentDirectory.fireCatsChanged(f_acf); } }, ta_category, 1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 378 */       TagManagerFactory.getTagManager().getTagType(3).addTagListener(PluginCoreUtils.unwrap(download), new TagListener()
/*     */       {
/*     */         public void taggableSync(Tag tag) {}
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
/*     */         public void taggableRemoved(Tag tag, Taggable tagged)
/*     */         {
/* 393 */           update(tagged);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void taggableAdded(Tag tag, Taggable tagged)
/*     */         {
/* 401 */           update(tagged);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         private void update(Taggable tagged)
/*     */         {
/* 408 */           AzureusPlatformContentDirectory.fireTagsChanged(f_acf);
/*     */         }
/*     */         
/* 411 */       });
/* 412 */       return acf;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 416 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getETA(DiskManagerFileInfo file)
/*     */   {
/*     */     try
/*     */     {
/* 425 */       if (file.getDownloaded() == file.getLength())
/*     */       {
/* 427 */         return 0L;
/*     */       }
/*     */       
/* 430 */       if ((file.isDeleted()) || (file.isSkipped()))
/*     */       {
/* 432 */         return Long.MAX_VALUE;
/*     */       }
/*     */       
/* 435 */       long eta = file.getDownload().getStats().getETASecs();
/*     */       
/* 437 */       if (eta < 0L)
/*     */       {
/* 439 */         return Long.MAX_VALUE;
/*     */       }
/*     */       
/* 442 */       return eta;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 446 */     return Long.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void fireCatsChanged(AzureusContentFile acf)
/*     */   {
/* 454 */     for (AzureusContentDirectoryListener l : listeners)
/*     */     {
/* 456 */       l.contentChanged(acf, "cats");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void fireTagsChanged(AzureusContentFile acf)
/*     */   {
/* 464 */     for (AzureusContentDirectoryListener l : listeners)
/*     */     {
/* 466 */       l.contentChanged(acf, "tags");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(AzureusContentDirectoryListener listener)
/*     */   {
/* 474 */     listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(AzureusContentDirectoryListener listener)
/*     */   {
/* 481 */     listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class AzureusPlatformContent
/*     */     implements AzureusContent
/*     */   {
/*     */     private Torrent torrent;
/*     */     
/*     */ 
/*     */     protected AzureusPlatformContent(Torrent _torrent)
/*     */     {
/* 494 */       this.torrent = _torrent;
/*     */     }
/*     */     
/*     */ 
/*     */     public Torrent getTorrent()
/*     */     {
/* 500 */       return this.torrent;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/AzureusPlatformContentDirectory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */