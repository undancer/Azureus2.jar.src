/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*     */ public class TOTorrentCreatorImpl
/*     */   implements TOTorrentCreator
/*     */ {
/*     */   private final File torrent_base;
/*     */   private URL announce_url;
/*     */   private boolean add_other_hashes;
/*     */   private long piece_length;
/*     */   private long piece_min_size;
/*     */   private long piece_max_size;
/*     */   private long piece_num_lower;
/*     */   private long piece_num_upper;
/*     */   private boolean is_desc;
/*  53 */   private final Map<String, File> linkage_map = new HashMap();
/*     */   
/*     */   private File descriptor_dir;
/*     */   
/*     */   private TOTorrentCreateImpl torrent;
/*  58 */   private final List<TOTorrentProgressListener> listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   public TOTorrentCreatorImpl(File _torrent_base)
/*     */   {
/*  64 */     this.torrent_base = _torrent_base;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TOTorrentCreatorImpl(File _torrent_base, URL _announce_url, boolean _add_other_hashes, long _piece_length)
/*     */     throws TOTorrentException
/*     */   {
/*  76 */     this.torrent_base = _torrent_base;
/*  77 */     this.announce_url = _announce_url;
/*  78 */     this.add_other_hashes = _add_other_hashes;
/*  79 */     this.piece_length = _piece_length;
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
/*     */   public TOTorrentCreatorImpl(File _torrent_base, URL _announce_url, boolean _add_other_hashes, long _piece_min_size, long _piece_max_size, long _piece_num_lower, long _piece_num_upper)
/*     */     throws TOTorrentException
/*     */   {
/*  94 */     this.torrent_base = _torrent_base;
/*  95 */     this.announce_url = _announce_url;
/*  96 */     this.add_other_hashes = _add_other_hashes;
/*  97 */     this.piece_min_size = _piece_min_size;
/*  98 */     this.piece_max_size = _piece_max_size;
/*  99 */     this.piece_num_lower = _piece_num_lower;
/* 100 */     this.piece_num_upper = _piece_num_upper;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFileIsLayoutDescriptor(boolean b)
/*     */   {
/* 107 */     this.is_desc = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrent create()
/*     */     throws TOTorrentException
/*     */   {
/*     */     try
/*     */     {
/* 116 */       if (this.announce_url == null)
/*     */       {
/* 118 */         throw new TOTorrentException("Skeleton creator", 5);
/*     */       }
/*     */       
/*     */       File base_to_use;
/*     */       File base_to_use;
/* 123 */       if (this.is_desc)
/*     */       {
/* 125 */         base_to_use = createLayoutMap();
/*     */       }
/*     */       else
/*     */       {
/* 129 */         base_to_use = this.torrent_base;
/*     */       }
/*     */       
/* 132 */       if (this.piece_length > 0L)
/*     */       {
/* 134 */         this.torrent = new TOTorrentCreateImpl(this.linkage_map, base_to_use, this.announce_url, this.add_other_hashes, this.piece_length);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 143 */         this.torrent = new TOTorrentCreateImpl(this.linkage_map, base_to_use, this.announce_url, this.add_other_hashes, this.piece_min_size, this.piece_max_size, this.piece_num_lower, this.piece_num_upper);
/*     */       }
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
/* 155 */       for (TOTorrentProgressListener l : this.listeners)
/*     */       {
/* 157 */         this.torrent.addListener(l);
/*     */       }
/*     */       
/* 160 */       this.torrent.create();
/*     */       
/* 162 */       return this.torrent;
/*     */     }
/*     */     finally
/*     */     {
/* 166 */       if (this.is_desc)
/*     */       {
/* 168 */         destroyLayoutMap();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private List<DescEntry> readDescriptor()
/*     */     throws TOTorrentException
/*     */   {
/*     */     try
/*     */     {
/* 179 */       int top_files = 0;
/* 180 */       int top_entries = 0;
/*     */       
/* 182 */       String top_component = null;
/*     */       
/* 184 */       Map map = BDecoder.decode(FileUtil.readFileAsByteArray(this.torrent_base));
/*     */       
/* 186 */       List<Map> file_map = (List)map.get("file_map");
/*     */       
/* 188 */       if (file_map == null)
/*     */       {
/* 190 */         throw new TOTorrentException("Invalid descriptor file", 4);
/*     */       }
/*     */       
/* 193 */       List<DescEntry> desc_entries = new ArrayList();
/*     */       
/* 195 */       BDecoder.decodeStrings(file_map);
/*     */       
/* 197 */       for (Map m : file_map)
/*     */       {
/* 199 */         List<String> logical_path = (List)m.get("logical_path");
/* 200 */         String target = (String)m.get("target");
/*     */         
/* 202 */         if ((logical_path == null) || (target == null))
/*     */         {
/* 204 */           throw new TOTorrentException("Invalid descriptor file: entry=" + m, 4);
/*     */         }
/*     */         
/* 207 */         if (logical_path.size() == 0)
/*     */         {
/* 209 */           throw new TOTorrentException("Logical path must have at least one entry: " + m, 4);
/*     */         }
/*     */         
/* 212 */         for (int i = 0; i < logical_path.size(); i++)
/*     */         {
/* 214 */           logical_path.set(i, FileUtil.convertOSSpecificChars((String)logical_path.get(i), i < logical_path.size() - 1));
/*     */         }
/*     */         
/* 217 */         File tf = new File(target);
/*     */         
/* 219 */         if (!tf.exists())
/*     */         {
/* 221 */           throw new TOTorrentException("Invalid descriptor file: file '" + tf + "' not found" + m, 4);
/*     */         }
/*     */         
/*     */ 
/* 225 */         String str = (String)logical_path.get(0);
/*     */         
/* 227 */         if (logical_path.size() == 1)
/*     */         {
/* 229 */           top_entries++;
/*     */         }
/*     */         
/* 232 */         if ((top_component != null) && (!top_component.equals(str)))
/*     */         {
/* 234 */           throw new TOTorrentException("Invalid descriptor file: multiple top level elements specified", 4);
/*     */         }
/*     */         
/* 237 */         top_component = str;
/*     */         
/*     */ 
/* 240 */         desc_entries.add(new DescEntry(logical_path, tf, null));
/*     */       }
/*     */       
/* 243 */       if (top_entries > 1)
/*     */       {
/* 245 */         throw new TOTorrentException("Invalid descriptor file: exactly one top level entry required", 4);
/*     */       }
/*     */       
/* 248 */       if (desc_entries.isEmpty())
/*     */       {
/* 250 */         throw new TOTorrentException("Invalid descriptor file: no mapping entries found", 4);
/*     */       }
/*     */       
/* 253 */       return desc_entries;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 257 */       throw new TOTorrentException("Invalid descriptor file: " + Debug.getNestedExceptionMessage(e), 4);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void mapDirectory(int prefix_length, File target, File temp)
/*     */     throws IOException
/*     */   {
/* 270 */     File[] files = target.listFiles();
/*     */     
/* 272 */     for (File f : files)
/*     */     {
/* 274 */       String file_name = f.getName();
/*     */       
/* 276 */       if ((!file_name.equals(".")) && (!file_name.equals("..")))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 281 */         File t = new File(temp, file_name);
/*     */         
/* 283 */         if (f.isDirectory())
/*     */         {
/* 285 */           if (!t.isDirectory())
/*     */           {
/* 287 */             t.mkdirs();
/*     */           }
/*     */           
/* 290 */           mapDirectory(prefix_length, f, t);
/*     */         }
/*     */         else
/*     */         {
/* 294 */           if (!t.exists())
/*     */           {
/* 296 */             t.createNewFile();
/*     */           }
/*     */           else
/*     */           {
/* 300 */             throw new IOException("Duplicate file: " + t);
/*     */           }
/*     */           
/* 303 */           this.linkage_map.put(t.getAbsolutePath().substring(prefix_length), f);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private File createLayoutMap()
/*     */     throws TOTorrentException
/*     */   {
/* 316 */     if (this.descriptor_dir != null)
/*     */     {
/* 318 */       return this.descriptor_dir;
/*     */     }
/*     */     try
/*     */     {
/* 322 */       this.descriptor_dir = AETemporaryFileHandler.createTempDir();
/*     */       
/* 324 */       File top_level_file = null;
/*     */       
/* 326 */       List<DescEntry> desc_entries = readDescriptor();
/*     */       
/* 328 */       for (DescEntry entry : desc_entries)
/*     */       {
/* 330 */         List<String> logical_path = entry.getLogicalPath();
/* 331 */         File target = entry.getTarget();
/*     */         
/* 333 */         File temp = this.descriptor_dir;
/*     */         
/* 335 */         int prefix_length = this.descriptor_dir.getAbsolutePath().length() + 1;
/*     */         
/* 337 */         for (int i = 0; i < logical_path.size(); i++)
/*     */         {
/* 339 */           temp = new File(temp, (String)logical_path.get(i));
/*     */           
/* 341 */           if (top_level_file == null)
/*     */           {
/* 343 */             top_level_file = temp;
/*     */           }
/*     */         }
/*     */         
/* 347 */         if (target.isDirectory())
/*     */         {
/* 349 */           if (!temp.isDirectory())
/*     */           {
/* 351 */             if (!temp.mkdirs())
/*     */             {
/* 353 */               throw new TOTorrentException("Failed to create logical directory: " + temp, 5);
/*     */             }
/*     */           }
/*     */           
/* 357 */           mapDirectory(prefix_length, target, temp);
/*     */         }
/*     */         else
/*     */         {
/* 361 */           File p = temp.getParentFile();
/*     */           
/* 363 */           if (!p.isDirectory())
/*     */           {
/* 365 */             if (!p.mkdirs())
/*     */             {
/* 367 */               throw new TOTorrentException("Failed to create logical directory: " + p, 5);
/*     */             }
/*     */           }
/*     */           
/* 371 */           if (temp.exists())
/*     */           {
/* 373 */             throw new TOTorrentException("Duplicate file: " + temp, 5);
/*     */           }
/*     */           
/*     */ 
/* 377 */           temp.createNewFile();
/*     */           
/* 379 */           this.linkage_map.put(temp.getAbsolutePath().substring(prefix_length), target);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 384 */       return top_level_file;
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 388 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 392 */       throw new TOTorrentException(Debug.getNestedExceptionMessage(e), 5);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void destroyLayoutMap()
/*     */   {
/* 399 */     if ((this.descriptor_dir != null) && (this.descriptor_dir.exists()))
/*     */     {
/* 401 */       if (!FileUtil.recursiveDelete(this.descriptor_dir))
/*     */       {
/* 403 */         Debug.out("Failed to delete descriptor directory '" + this.descriptor_dir + "'");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getTorrentDataSizeFromFileOrDir()
/*     */     throws TOTorrentException
/*     */   {
/* 413 */     if (this.is_desc)
/*     */     {
/* 415 */       List<DescEntry> desc_entries = readDescriptor();
/*     */       
/* 417 */       long result = 0L;
/*     */       
/* 419 */       for (DescEntry entry : desc_entries)
/*     */       {
/* 421 */         result += getTorrentDataSizeFromFileOrDir(entry.getTarget());
/*     */       }
/*     */       
/* 424 */       return result;
/*     */     }
/*     */     
/*     */ 
/* 428 */     return getTorrentDataSizeFromFileOrDir(this.torrent_base);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long getTorrentDataSizeFromFileOrDir(File file)
/*     */   {
/* 436 */     String name = file.getName();
/*     */     
/* 438 */     if ((name.equals(".")) || (name.equals("..")))
/*     */     {
/* 440 */       return 0L;
/*     */     }
/*     */     
/* 443 */     if (!file.exists())
/*     */     {
/* 445 */       return 0L;
/*     */     }
/*     */     
/* 448 */     if (file.isFile())
/*     */     {
/* 450 */       return file.length();
/*     */     }
/*     */     
/*     */ 
/* 454 */     File[] dir_files = file.listFiles();
/*     */     
/* 456 */     long length = 0L;
/*     */     
/* 458 */     for (int i = 0; i < dir_files.length; i++)
/*     */     {
/* 460 */       length += getTorrentDataSizeFromFileOrDir(dir_files[i]);
/*     */     }
/*     */     
/* 463 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 470 */     if (this.torrent != null)
/*     */     {
/* 472 */       this.torrent.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TOTorrentProgressListener listener)
/*     */   {
/* 480 */     if (this.torrent == null)
/*     */     {
/* 482 */       this.listeners.add(listener);
/*     */     }
/*     */     else
/*     */     {
/* 486 */       this.torrent.addListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TOTorrentProgressListener listener)
/*     */   {
/* 494 */     if (this.torrent == null)
/*     */     {
/* 496 */       this.listeners.remove(listener);
/*     */     }
/*     */     else
/*     */     {
/* 500 */       this.torrent.removeListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class DescEntry
/*     */   {
/*     */     private final List<String> logical_path;
/*     */     
/*     */     private final File target;
/*     */     
/*     */ 
/*     */     private DescEntry(List<String> _l, File _t)
/*     */     {
/* 515 */       this.logical_path = _l;
/* 516 */       this.target = _t;
/*     */     }
/*     */     
/*     */ 
/*     */     private List<String> getLogicalPath()
/*     */     {
/* 522 */       return this.logical_path;
/*     */     }
/*     */     
/*     */ 
/*     */     private File getTarget()
/*     */     {
/* 528 */       return this.target;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TOTorrentCreatorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */